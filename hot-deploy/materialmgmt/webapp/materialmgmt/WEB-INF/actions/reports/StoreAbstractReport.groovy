import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import in.vasista.vbiz.purchase.MaterialHelperServices;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import in.vasista.vbiz.byproducts.SalesInvoiceServices;
import org.ofbiz.party.party.PartyHelper;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
fromDate=parameters.storeAbstFromDate;
thruDate=parameters.storeAbstThruDate;
reportTypeFlag = parameters.reportTypeFlag;
dctx = dispatcher.getDispatchContext();
fromDateTime = null;
thruDateTime = null;
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	fromDateTime = new java.sql.Timestamp(sdf.parse(fromDate).getTime());
	thruDateTime = new java.sql.Timestamp(sdf.parse(thruDate).getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+fromDate, "");
}
//fromDateTime = UtilDateTime.getDayStart(fromDateTime);
dayBegin = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(thruDateTime);
context.fromDate = dayBegin;
context.thruDate = dayEnd;

isByParty = Boolean.TRUE;
List conditionList = [];
facilityId=parameters.issueToFacilityId;
context.facilityId=facilityId;
ledgerFolioNo=parameters.ledgerFolioNo;
context.ledgerFolioNo=ledgerFolioNo;
if(UtilValidate.isNotEmpty(ledgerFolioNo)){
	ProductAttributeId = delegator.findList("ProductAttribute",EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS , ledgerFolioNo)  , UtilMisc.toSet("productId"), null, null, false );
	if(UtilValidate.isNotEmpty(ProductAttributeId)){
		List prodIds= EntityUtil.getFieldListFromEntityList(ProductAttributeId, "productId", true);
		if(UtilValidate.isNotEmpty(prodIds)){	
			if(UtilValidate.isNotEmpty(facilityId)){
			     conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS , facilityId));
			}
			conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN,prodIds));
			condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			productDetails = delegator.findList("ProductFacility", condition, null, null, null, false);
		}
	}
}else{
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS , facilityId));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		productDetails = delegator.findList("ProductFacility", condition, null, null, null, false);
 }
//productDetails = delegator.findList("ProductFacility",EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS , facilityId)  ,  UtilMisc.toSet("productId"), null, null, false );
productIdsFacility = EntityUtil.getFieldListFromEntityList(productDetails, "productId", true);

conditionList.clear();
conditionList.add(EntityCondition.makeCondition("issuedDateTime", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
conditionList.add(EntityCondition.makeCondition("issuedDateTime",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
itemIssuances=delegator.findList("ItemIssuance",condition,UtilMisc.toSet("productId"),null,null,false);
issuedProductIds=EntityUtil.getFieldListFromEntityList(itemIssuances,"productId",true);

/*custReqAndItemDetails= delegator.findList("ItemIssuanceInventoryItemAndProduct",condition,null,null,null,false);*/
custReqAndItemDetails=delegator.findList("InventoryItem",EntityCondition.makeCondition("productId",EntityOperator.IN,productIdsFacility),UtilMisc.toSet("productId"),null,null,false);
productMap=[:];
itecodeSort=[];
sortedMap = [:];
if(UtilValidate.isNotEmpty(custReqAndItemDetails)){
		 productIds = EntityUtil.getFieldListFromEntityList(custReqAndItemDetails, "productId", true);			 
		 productIds.each{eachProduct->
			 productDetailsMap=[:];
			 
			 productDetails = delegator.findOne("Product",["productId":eachProduct],false);
			 if(UtilValidate.isNotEmpty(productDetails)){
				 
					itemCode=productDetails.internalName;
					description=productDetails.description;
					productDetailsMap.put("eachProduct",eachProduct);
					productDetailsMap.put("itemCode",itemCode);
					String paddedItemCode = String.format("%15s", itemCode).replace(' ', '0');
                    productDetailsMap.put("paddedItemCode",paddedItemCode);					
					productDetailsMap.put("description",description);
					uomId=productDetails.quantityUomId;
			  }
			 if(UtilValidate.isNotEmpty(uomId)){
					unitDesciption = delegator.findOne("Uom",["uomId":uomId],false);
					productDetailsMap.put("unit",unitDesciption.abbreviation);
			 }
			 invCountMap = dispatcher.runSync("getProductInventoryOpeningBalance", [productId: eachProduct,effectiveDate:dayBegin, ownerPartyId:"Company", userLogin: userLogin]);
			 if(UtilValidate.isNotEmpty(invCountMap)){
			 	 openingQty = invCountMap.get("inventoryCount");
			 openingTotCost=invCountMap.get("inventoryCost");
			 productDetailsMap.put("openingQty", openingQty);
			 productDetailsMap.put("openingTot", openingTotCost);
			 }
			 
			 storeIssueReceipts = MaterialHelperServices.getMaterialReceiptsForPeriod(dctx, [fromDate:dayBegin,thruDate:dayEnd, productId: eachProduct, userLogin: userLogin,]);
			  receiptList =storeIssueReceipts.get("receiptsList");
			  ReceiptQty=0;ReceiptAmount=0;
			 if(UtilValidate.isNotEmpty(receiptList)){
				 receiptList.each{receiptDetails->
					 ReceiptQty=ReceiptQty+receiptDetails.get("quantity");
					 ReceiptAmount=ReceiptAmount+receiptDetails.get("amount");
				 }
			 }
			 productDetailsMap.put("ReceiptQty",ReceiptQty);
			 productDetailsMap.put("ReceiptAmount",ReceiptAmount);
			 IssueQty=0;IssueAmount=0;
			 if(issuedProductIds.contains(eachProduct)){
			 itemIssueMap=MaterialHelperServices.getCustRequestIssuancesForPeriod(dctx,[fromDate:dayBegin, thruDate:dayEnd,productId: eachProduct, userLogin: userLogin]);
			 StoreIssueList=itemIssueMap.get("itemIssuanceList");
			 
			 if(UtilValidate.isNotEmpty(StoreIssueList)){
				 StoreIssueList.each{storeIssueDetails->
					 IssueQty=IssueQty+storeIssueDetails.get("quantity");
					 IssueAmount=IssueAmount+storeIssueDetails.get("amount");
				 }
			 }	
			 }
			 productDetailsMap.put("IssueQty",IssueQty);
			 productDetailsMap.put("IssueAmount",IssueAmount);
			 
		 if( ((ReceiptQty) != 0) && ((IssueQty) != 0) || ((openingQty) != 0)){
			 itecodeSort.addAll(productDetailsMap);
			 productMap.put(eachProduct,productDetailsMap);
			 }
		
			 
		 }		 			 
		 itecodeSort = UtilMisc.sortMaps(itecodeSort, UtilMisc.toList("paddedItemCode"));
		 for (eachOne in itecodeSort) {
		      sortedMap.put(eachOne.eachProduct,productMap.getAt(eachOne.eachProduct));
		 }				 				  
}
context.sortedMap=sortedMap;
