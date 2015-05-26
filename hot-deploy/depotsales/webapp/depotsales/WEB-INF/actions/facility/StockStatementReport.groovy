import in.vasista.vbiz.byproducts.ByProductNetworkServices;

import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.SortedMap;
import java.math.RoundingMode;
import javolution.util.FastMap;
import javolution.util.FastList;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.product.inventory.InventoryWorker;
import org.ofbiz.product.inventory.InventoryServices;
import in.vasista.vbiz.purchase.MaterialHelperServices;

rounding = RoundingMode.HALF_UP;
dctx = dispatcher.getDispatchContext();

fromDate = parameters.stockFromDate;
thruDate = parameters.stockThruDate;
context.fromDate=fromDate;
context.thruDate=thruDate;

def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (UtilValidate.isNotEmpty(fromDate)) {
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(fromDate).getTime()));
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(thruDate).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
context.errorMessage = "Cannot parse date string: " + e;
	return;
}

dayStart = UtilDateTime.getDayStart(fromDate);
dayEnd = UtilDateTime.getDayEnd(thruDate);

Map finalStockStatementMap = FastMap.newInstance();

conditionList = [];
conditionList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.NOT_EQUAL, null));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
productCategoriesList = delegator.findList("ProductCategory", condition, null, null, null, false);


productCategoriesList.each{productCatg->
	Map StockStatementMap = FastMap.newInstance();
	conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("productTypeId", EntityOperator.EQUALS, "FINISHED_GOOD"));
		conditionList.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.EQUALS, productCatg.productCategoryId));
		condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		products = delegator.findList("Product", condition, null, null, null, false);
		productId="";
		productName="";
		
		
		products.each{productEntry->
			openingBalance=BigDecimal.ZERO;
			closingBalance=BigDecimal.ZERO;
			receivedQty=BigDecimal.ZERO;
			issuedQty=BigDecimal.ZERO;
			
			tempMap = [:];
			tempMap["productName"]="";
			tempMap["openingBalance"]="";
			tempMap["openingRate"]=BigDecimal.ZERO;
			tempMap["openingValue"]=BigDecimal.ZERO;
			tempMap["closingBalance"]="";
			tempMap["closingRate"]=BigDecimal.ZERO;
			tempMap["closingValue"]=BigDecimal.ZERO;
			tempMap["receivedQty"]="";
			tempMap["receivedVal"]=BigDecimal.ZERO;
			tempMap["receivedRate"]=BigDecimal.ZERO;
			tempMap["issuedQty"]="";
			tempMap["issuedVal"]=BigDecimal.ZERO;
			tempMap["issuedRate"]=BigDecimal.ZERO;
			
			
			tempMap["productName"]=productEntry.productName;
			inventoryItem = InventoryServices.getProductInventoryOpeningBalance(dctx, [effectiveDate:dayStart,productId:productEntry.productId,getInventoryOnlyWithUnitCost:"Y"]);
			if(UtilValidate.isNotEmpty(inventoryItem)){
				openingBalance=inventoryItem.inventoryCount;
				openingValue=inventoryItem.inventoryCost;
				tempMap["openingBalance"]=openingBalance;
				if(openingBalance==0){
					openingBalance=1;
				}
				tempMap["openingRate"]=(openingValue).divide(openingBalance , 2, rounding);
				tempMap["openingValue"]=openingValue;
			}
			
			inventoryItem = InventoryServices.getProductInventoryOpeningBalance(dctx, [effectiveDate:dayEnd,productId:productEntry.productId,getInventoryOnlyWithUnitCost:"Y"]);
			if(UtilValidate.isNotEmpty(inventoryItem)){
				closingBalance=inventoryItem.inventoryCount;
				closingValue=inventoryItem.inventoryCost;
				tempMap["closingBalance"]=closingBalance;
				if(closingBalance==0){
					closingBalance=1;
				}
				tempMap["closingRate"]=(closingValue).divide(closingBalance , 2, rounding);
				tempMap["closingValue"]=closingValue;
			}
			
			conditionList = [];
			conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productEntry.productId));
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SR_RECEIVED"), EntityOperator.OR,
							  EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SR_ACCEPTED")));
			
			conditionList.add(EntityCondition.makeCondition("datetimeReceived", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
			conditionList.add(EntityCondition.makeCondition("datetimeReceived", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
			condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			ShipmentReceiptList = delegator.findList("ShipmentReceipt", condition, null, null, null, false);
			receivedVal=BigDecimal.ZERO;
			receivedRate=BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty(ShipmentReceiptList)){
				ShipmentReceiptList.each{receipt->
					if((UtilValidate.isNotEmpty(receipt.statusId)) && (receipt.statusId == "SR_RECEIVED" || receipt.statusId == "SR_ACCEPTED") && UtilValidate.isNotEmpty(receipt.quantityAccepted)){
						receivedQty+=receipt.quantityAccepted;
						inventoryItemEntry = delegator.findOne("InventoryItem",[inventoryItemId : receipt.inventoryItemId], false);
						receivedVal+=(receipt.quantityAccepted)*(inventoryItemEntry.unitCost);
					}
				}
			}
			tempMap["receivedQty"]=receivedQty;
			tempMap["receivedVal"]=receivedVal;
			if(UtilValidate.isEmpty(receivedQty) || receivedQty==0){
				receivedQty=1;
			}
			tempMap["receivedRate"] = (receivedVal).divide(receivedQty , 2, rounding);
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productEntry.productId));
			conditionList.add(EntityCondition.makeCondition("issuedDateTime", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
			conditionList.add(EntityCondition.makeCondition("issuedDateTime", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
			condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			ItemIssuanceList = delegator.findList("ItemIssuanceInventoryItemAndProduct", condition, null, null, null, false);
			issuedVal=BigDecimal.ZERO;
			issuedRate=BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty(ItemIssuanceList)){
				ItemIssuanceList.each{item->
					if((UtilValidate.isNotEmpty(item.quantity))){
						issuedQty+=item.quantity;
						if(UtilValidate.isNotEmpty(item.cancelQuantity)){
							issuedQty-=item.cancelQuantity;
						}
					}
					issuedVal+=(issuedQty)*(item.unitCost);
				}
				tempMap["issuedQty"]=issuedQty;
				if(UtilValidate.isEmpty(issuedQty) || issuedQty==0){
					issuedQty=1;
				}
				tempMap["issuedVal"]=issuedVal;
				tempMap["issuedRate"]=(issuedVal).divide(issuedQty , 2, rounding);
			}
			StockStatementMap.put(productEntry.productId,tempMap);
		}
		finalStockStatementMap.put(productCatg.productCategoryId,StockStatementMap);
	}		
context.finalStockStatementMap=finalStockStatementMap;

