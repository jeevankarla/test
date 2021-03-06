import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.order.order.*;
import org.ofbiz.party.contact.*;
import org.ofbiz.product.inventory.InventoryWorker;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.accounting.payment.*;
import in.vasista.vbiz.purchase.MaterialHelperServices;
import javolution.util.FastMap;

dctx = dispatcher.getDispatchContext();

/*currentYear=UtilDateTime.getYear(UtilDateTime.nowTimestamp(),timeZone,locale);
context.year=currentYear;*/
if(UtilValidate.isNotEmpty(parameters.IndentNo)){
	custRequestId=parameters.IndentNo;
}
if(UtilValidate.isNotEmpty(parameters.custRequestId)){
	custRequestId=parameters.custRequestId;
}
GenericValue custRequest=delegator.findOne("CustRequest",["custRequestId":custRequestId],false);
context.custRequestStatus=custRequest.statusId;
context.fromPartyId=custRequest.fromPartyId;
context.custRequestDate=custRequest.custRequestDate;
context.custRequestId=custRequestId;

custRequestItems=[];
materialIndentList=[];
itemIssuanceList=[];
if(UtilValidate.isNotEmpty(parameters.custRequestId) && UtilValidate.isNotEmpty(parameters.custRequestItemSeqId)){
	ecl = EntityCondition.makeCondition([EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, parameters.custRequestId),
		  EntityCondition.makeCondition("custRequestItemSeqId",EntityOperator.EQUALS,parameters.custRequestItemSeqId)],EntityOperator.AND);
	  
}else{
	ecl = EntityCondition.makeCondition([EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId)],EntityOperator.AND);
}

custRequestItems=delegator.findList("CustRequestItem",ecl,null,null,null,false);
if(custRequestItems){
		custRequestItems.each{ item->
		if(UtilValidate.isNotEmpty(parameters.custRequestId)){
			if(item.statusId=="CRQ_ISSUED" || item.statusId=="CRQ_COMPLETED" || item.statusId=="CRQ_SUBMITTED"){
				tempMap=[:];
				GenericValue product=delegator.findOne("Product",[productId:item.productId],true);
				GenericValue productAttr=delegator.findOne("ProductAttribute",[productId:item.productId,attrName:"LEDGERFOLIONO"],true);
				if(product){
					tempMap.put("internalName",product.internalName);
					tempMap.put("productdesc",product.description);
				}
				if(productAttr){
					tempMap.put("ledgerNo",productAttr.attrValue);
				}
				tempMap.put("identedQty",item.quantity);
				issuedQty=0;
				if(UtilValidate.isNotEmpty(parameters.itemIssuanceId)){
					itemIssuances=delegator.findOne("ItemIssuance",[itemIssuanceId:parameters.itemIssuanceId],true);
					if(UtilValidate.isNotEmpty(itemIssuances)){
						issuedQty=itemIssuances.quantity;
					}
					tempMap.put("itemIssuanceId", parameters.itemIssuanceId);
				}
				tempMap.put("issuedQty", issuedQty);
				itemIssuanceList.add(tempMap);
			}	
		}else{	
				//if(item.statusId=="CRQ_ISSUED" || item.statusId=="CRQ_COMPLETED" || item.statusId=="CRQ_SUBMITTED" ){
					tempMap=[:];
					GenericValue product=delegator.findOne("Product",[productId:item.productId],true);
					GenericValue productAttr=delegator.findOne("ProductAttribute",[productId:item.productId,attrName:"LEDGERFOLIONO"],true);
					if(product){
						tempMap.put("internalName",product.internalName);
						tempMap.put("productdesc",product.description);
					}
					if(productAttr){
						tempMap.put("ledgerNo",productAttr.attrValue);
					}
					tempMap.put("identedQty",item.quantity);
					issuedQty=0;
					totalValue=0;
					totalUnitPrice=0;
					productDetails= MaterialHelperServices.getUnitPriceAndQuantity(dctx,UtilMisc.toMap("userLogin",userLogin,"custRequestItemSeqId",item.custRequestItemSeqId,"custRequestId",item.custRequestId));
					if(UtilValidate.isNotEmpty(productDetails)){
						if(UtilValidate.isNotEmpty(productDetails.get("totalQty"))){
							issuedQty=productDetails.get("totalQty");
						}
						if(UtilValidate.isNotEmpty(productDetails.get("totalValue"))){
							totalValue=productDetails.get("totalValue");
						}
						if(UtilValidate.isNotEmpty(productDetails.get("totalUnitPrice"))){
							totalUnitPrice=productDetails.get("totalUnitPrice");
						}
					}
					tempMap.put("issuedQty",issuedQty);
					tempMap.put("totalValue",totalValue);
					tempMap.put("totalUnitPrice",totalUnitPrice);
					stock = dispatcher.runSync("getProductInventoryAvailable",[productId:item.productId]);
					if(stock){
						tempMap.put("stock",stock.get("quantityOnHandTotal"));
					}
					materialIndentList.add(tempMap);
				//}
		}
	}
}
context.materialIndentList=materialIndentList;
context.itemIssuanceList=itemIssuanceList;


































