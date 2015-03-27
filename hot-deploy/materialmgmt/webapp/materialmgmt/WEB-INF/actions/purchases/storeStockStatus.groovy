import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityListIterator;
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
import org.ofbiz.product.inventory.InventoryServices;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import in.vasista.vbiz.purchase.MaterialHelperServices;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import in.vasista.vbiz.byproducts.SalesInvoiceServices;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.product.inventory.InventoryWorker;
import in.vasista.vbiz.purchase.MaterialHelperServices;
import org.ofbiz.product.inventory.InventoryWorker;
import in.vasista.vbiz.purchase.MaterialHelperServices;
 
dctx = dispatcher.getDispatchContext();
facilityId=parameters.issueToFacilityId;
nowDate = UtilDateTime.nowTimestamp();
nextDay = UtilDateTime.addDaysToTimestamp(nowDate, 1);
nextDayBegin = UtilDateTime.getDayStart(nextDay);

prodMap=[:];

 productDetails = delegator.findList("ProductFacility",EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS , facilityId)  ,  UtilMisc.toSet("productId"), null, null, false );
 productIds = EntityUtil.getFieldListFromEntityList(productDetails, "productId", true);
 
 
 //get product from ProductCategory
 
 exprList=[];
 exprList.add(EntityCondition.makeCondition("productCategoryTypeId", EntityOperator.EQUALS, "RAW_MATERIAL"));
 exprList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
 condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
// productCatDetails = delegator.findList("ProductCategoryAndMember", condition, null, null, null, false);
 productCatDetails = EntityUtil.filterByDate(delegator.findList("ProductCategoryAndMember", condition, null, null, null, false));
 
 //productCatDetails = delegator.findList("ProductCategoryMember",EntityCondition.makeCondition("productId", EntityOperator.IN , productIds)  , null, null, null, false );
productCatIds = EntityUtil.getFieldListFromEntityList(productCatDetails,"productCategoryId", true);
if(UtilValidate.isNotEmpty(productCatIds)){
	productCatIds.each{productCatId->
    prodList=[];
	List conlist=[];
	conlist.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, productCatId));
	conlist.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
	cond=EntityCondition.makeCondition(conlist,EntityOperator.AND);
	prodIdData = delegator.findList("ProductAndCategoryMember", cond , null, null, null, false );
	
	if(UtilValidate.isNotEmpty(prodIdData)){
	  prodIdData.each{productDetails->
	  productDetailMap=[:];
	  
	  productAttr = delegator.findOne("ProductAttribute", [productId : productDetails.productId , attrName : "LEDGERFOLIONO"], false);
	  String attrName = null;
	  String attrValue = null;
	  if(UtilValidate.isNotEmpty(productAttr)){
		  attrName = productAttr.get("attrName");
		  attrValue = productAttr.get("attrValue");
		  productDetailMap["ledgerfolio"]=attrValue;		  
	  }
	  productDetailMap["productId"]=productDetails.productId;
	  productDetailMap["description"]=productDetails.description;
	  uomId=productDetails.quantityUomId;
	  Map resultOutput = null;
	  
	  /*bookStock = dispatcher.runSync("getInventoryAvailableByFacility", [productId : productDetails.productId, facilityId : facilityId ,ownerPartyId :"Company"]);
	 
      totalInventory = dispatcher.runSync("getInventoryAvailableByFacility", [productId : productDetails.productId, facilityId : facilityId]);
	  Debug.log("totalInventory==============================="+totalInventory);
	  
	  bookStock = InventoryServices.getProductInventoryOpeningBalance(dctx, [effectiveDate:effdayEnd,productId:productDetails.productId,facilityId:facilityId ]);
	  Debug.log("bookStock==============================="+bookStock);
	  productDetailMap["inventoryCount"]=bookStock.quantityOnHandTotal;*/
	  openingQty=BigDecimal.ZERO;
	  openingTotCost=BigDecimal.ZERO;
	  closingQty=BigDecimal.ZERO;
	  invCountMap = dispatcher.runSync("getProductInventoryOpeningBalance", [productId: productDetails.productId,effectiveDate:nextDayBegin, facilityId : facilityId ,ownerPartyId:"Company", userLogin: userLogin]);
	  if(UtilValidate.isNotEmpty(invCountMap)){
		   openingQty = invCountMap.get("inventoryCount");
		  
		   openingTotCost=invCountMap.get("inventoryCost");
		   
	  }
	  if(UtilValidate.isNotEmpty(uomId)){
		  unitDesciption = delegator.findOne("Uom",["uomId":uomId],false);
	   productDetailMap["unit"]=unitDesciption.get("abbreviation");
	  }
	  
	  qcQuantity = BigDecimal.ZERO;
	  receivedQty = BigDecimal.ZERO;
	  qcQtyValue= BigDecimal.ZERO;
	  ecl = EntityCondition.makeCondition([
		  EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productDetails.productId),
		  EntityCondition.makeCondition("statusId",EntityOperator.IN,UtilMisc.toList("SR_RECEIVED","SR_QUALITYCHECK"))],
		  EntityOperator.AND);
		shipmentReceipts=delegator.findList("ShipmentReceipt",ecl,UtilMisc.toSet("statusId","quantityAccepted","quantityRejected","inventoryItemId"),null,null,false);
		if(UtilValidate.isNotEmpty(shipmentReceipts)){
			shipmentReceipts.each{receipt->
				if((UtilValidate.isNotEmpty(receipt.statusId)) && (receipt.statusId == "SR_RECEIVED") && UtilValidate.isNotEmpty(receipt.quantityAccepted)){
					receivedQty+=receipt.quantityAccepted;
				}
				if((UtilValidate.isNotEmpty(receipt.statusId)) && (receipt.statusId == "SR_QUALITYCHECK") && UtilValidate.isNotEmpty(receipt.quantityAccepted)){
					qcQuantity+=receipt.quantityAccepted;
					inventoryItem=delegator.findOne("InventoryItem",[inventoryItemId:receipt.inventoryItemId],false);
					if(UtilValidate.isNotEmpty(inventoryItem)){
						unitCost=BigDecimal.ZERO;
						if(UtilValidate.isNotEmpty(inventoryItem.unitCost)){
							unitCost=inventoryItem.unitCost;
						}
						qcQtyValue=qcQtyValue+(receipt.quantityAccepted*unitCost);
					}
				}
			 }
		}
		closingQty=openingQty+qcQuantity;
		openingTotCost=openingTotCost+qcQtyValue;
		productDetailMap.put("closingQty", closingQty);
		productDetailMap.put("openingQty", openingQty);
		productDetailMap.put("openingTotCost", openingTotCost);
	  prodList.addAll(productDetailMap);
	  
	  
       }
	 }
  prodMap.put(productCatId,prodList);
   }
}

context.prodMap=prodMap;

