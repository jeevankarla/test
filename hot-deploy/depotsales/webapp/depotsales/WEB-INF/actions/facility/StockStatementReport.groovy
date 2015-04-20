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
			currentRate=BigDecimal.ZERO;
			rate=BigDecimal.ZERO;
			val=BigDecimal.ZERO;
			cnt=0;
			tempMap = [:];
			tempMap["productName"]="";
			tempMap["openingBalance"]="";
			tempMap["closingBalance"]="";
			tempMap["receivedQty"]="";
			tempMap["issuedQty"]="";
			tempMap["rate"]="";
			tempMap["val"]="";
			
			
			tempMap["productName"]=productEntry.productName;
			inventoryItem = InventoryServices.getProductInventoryOpeningBalance(dctx, [effectiveDate:dayStart,productId:productEntry.productId,ownerPartyId:"Company"]);
			if(UtilValidate.isNotEmpty(inventoryItem)){
				openingBalance=inventoryItem.inventoryCount;
				tempMap["openingBalance"]=openingBalance;
			}
			
			inventoryItem = InventoryServices.getProductInventoryOpeningBalance(dctx, [effectiveDate:dayEnd,productId:productEntry.productId,ownerPartyId:"Company"]);
			if(UtilValidate.isNotEmpty(inventoryItem)){
				closingBalance=inventoryItem.inventoryCount;
				tempMap["closingBalance"]=closingBalance;
			}
			
			conditionList = [];
			conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productEntry.productId));
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SR_RECEIVED"), EntityOperator.OR,
							  EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SR_ACCEPTED")));
			
			conditionList.add(EntityCondition.makeCondition("datetimeReceived", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
			conditionList.add(EntityCondition.makeCondition("datetimeReceived", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
			condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			ShipmentReceiptList = delegator.findList("ShipmentReceipt", condition, null, null, null, false);
			
			if(UtilValidate.isNotEmpty(ShipmentReceiptList)){
				ShipmentReceiptList.each{receipt->
					if((UtilValidate.isNotEmpty(receipt.statusId)) && (receipt.statusId == "SR_RECEIVED" || receipt.statusId == "SR_ACCEPTED") && UtilValidate.isNotEmpty(receipt.quantityAccepted)){
						receivedQty+=receipt.quantityAccepted;
					}
				}
			}
			tempMap["receivedQty"]=receivedQty;
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productEntry.productId));
			conditionList.add(EntityCondition.makeCondition("issuedDateTime", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
			conditionList.add(EntityCondition.makeCondition("issuedDateTime", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
			condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			ItemIssuanceList = delegator.findList("ItemIssuanceInventoryItemAndProduct", condition, null, null, null, false);
			
			if(UtilValidate.isNotEmpty(ItemIssuanceList)){
				ItemIssuanceList.each{item->
					if((UtilValidate.isNotEmpty(item.quantity))){
						issuedQty+=item.quantity;
						if(UtilValidate.isNotEmpty(item.cancelQuantity)){
							issuedQty-=item.cancelQuantity;
						}
					}
					if(currentRate!=item.unitCost){
						cnt++;
						currentRate=item.unitCost;
					}
					rate+=item.unitCost;
					val+=currentRate*item.quantity;
				}
				if(cnt==0){
					cnt=1;
				}
				rate/=cnt;
			}
			tempMap["issuedQty"]=issuedQty;
			tempMap["rate"]=rate;
			tempMap["val"]=val;
			
			StockStatementMap.put(productEntry.productId,tempMap);
		}
		finalStockStatementMap.put(productCatg.productCategoryId,StockStatementMap);
	}		
context.finalStockStatementMap=finalStockStatementMap;

