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
import javolution.util.FastMap;
import javolution.util.FastList;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.product.inventory.InventoryWorker;
import org.ofbiz.product.inventory.InventoryServices;
import in.vasista.vbiz.purchase.MaterialHelperServices;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.util.EntityUtil;

fromDate = parameters.ivdFromDate;
thruDate = parameters.ivdThruDate;

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

Map finalMap = FastMap.newInstance();
tempTotMap=[:];
finalCSVList=[];
conditionList=[];
conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
			conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
			conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
			conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, "BRANCH_SALES"));
			condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			OrderHeaderList = delegator.findList("OrderHeaderAndItems", condition, null, null, null, false); 
			OrderIdList = EntityUtil.getFieldListFromEntityList(OrderHeaderList, "orderId", true);
			//if(UtilValidate.isNotEmpty(parameters.header)&&parameters.header.equals("required")){
			headerData=[:];
			headerData.put("orderNo", " ");
			headerData.put("orderDate", " ");
			headerData.put("partyName", "INDENT VS DISPATCH REPORT");
			headerData.put("productName", " ");
			headerData.put("initialQty", " ");
			headerData.put("finalQty", " ");
			headerData.put("diffQty", " ");
			finalCSVList.add(headerData);
			headerData1=[:];
			headerData1.put("orderNo", " ");
			headerData1.put("orderDate", " ");
			headerData1.put("partyName", " ");
			headerData1.put("productName", " ");
			headerData1.put("initialQty", " ");
			headerData1.put("finalQty", " ");
			headerData1.put("diffQty", " ");
			finalCSVList.add(headerData1);
			headerData2=[:];
			headerData2.put("orderNo", "Order Id");
			headerData2.put("orderDate", "Order Date");
			headerData2.put("partyName", "Party Name");
			headerData2.put("productName", "Product Name");
			headerData2.put("initialQty", "indent value");
			headerData2.put("finalQty", "dispatch value");
			headerData2.put("diffQty", "Difference Quantity");
			finalCSVList.add(headerData2);
			
			//}
			
			totinitialQty=0;
			totfinalQty=0;
			totdiffQty=0;
	if(UtilValidate.isNotEmpty(OrderIdList)){
			
			OrderIdList.each{orderId->
				
				Map ProductWiseMap = FastMap.newInstance(); 
				List productList = EntityUtil.filterByAnd(OrderHeaderList, UtilMisc.toMap("orderId", orderId));
				//Debug.log("productList====================== ####### "+productList);
				if(UtilValidate.isNotEmpty(productList)){
					productList.each{productEntry->
						conditionList.clear();
						tempMap =[:];
						 BigDecimal initial=BigDecimal.ZERO;
						tempMap["productCode"]="";
						tempMap["productName"]="";
						tempMap["initialQty"]=BigDecimal.ZERO;
						tempMap["finalQty"]=BigDecimal.ZERO;
						tempMap["diffQty"]=BigDecimal.ZERO;
						tempMap["partyName"]="";
						orderNo ="NA";
						orderHeaderSequences = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS ,orderId), null, null, null, false );
						if(UtilValidate.isNotEmpty(orderHeaderSequences)){
							orderSeqDetails = EntityUtil.getFirst(orderHeaderSequences);
							orderNo = orderSeqDetails.orderNo;
						}
						//give prefrence to ShipToCustomer
						conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
						conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SHIP_TO_CUSTOMER"));
						shipCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
						orderRole = EntityUtil.getFirst(delegator.findList("OrderRole", shipCond, null, null, null, false));
						product = delegator.findOne("Product",UtilMisc.toMap("productId", productEntry.productId), false);
						//OrderItemAttr = EntityUtil.getFirst(delegator.findByAnd("OrderItemAttribute", UtilMisc.toMap("orderId",orderId,"attrName","INDENTQTY_FOR:"+productEntry.productId)));	
						if(UtilValidate.isNotEmpty(orderRole)){
						/*if(UtilValidate.isNotEmpty(OrderItemAttr)){
							initial=new BigDecimal(OrderItemAttr.attrValue);
						}*/
						//if(!(initial.compareTo(productEntry.quantity)==0)){
							//initial=new BigDecimal(OrderItemAttr.attrValue);
							tempMap["initialQty"]=productEntry.quantity;
							//Debug.log("initialQty======="+productEntry.quantity);
							totinitialQty=totinitialQty+productEntry.quantity;
							//Debug.log("totinitialQty======="+totinitialQty);
							orderAssocs = EntityUtil.getFirst(delegator.findList("OrderAssoc",EntityCondition.makeCondition("toOrderId", EntityOperator.EQUALS , orderId)  , null, null, null, false ));
							shippedQty=0;
							if(orderAssocs){
								poOrderId=orderAssocs.orderId;
								conditionList.clear();
								conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, poOrderId));
								conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productEntry.productId));
								shiprecCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
								shipmentReceipts =EntityUtil.getFirst(delegator.findList("ShipmentReceipt",shiprecCond, null, null, null, false ));
								if(UtilValidate.isNotEmpty(shipmentReceipts)){
									shippedQty=shipmentReceipts.quantityAccepted;
								}	
							}
							tempMap["finalQty"]=shippedQty;
							totfinalQty=totfinalQty+shippedQty;
						if(tempMap["initialQty"]!=null && tempMap["finalQty"]!=null){
							
						tempMap["diffQty"] = (tempMap["initialQty"]).subtract(tempMap["finalQty"]);
						totdiffQty=totdiffQty+tempMap["diffQty"];
						}
						
						if(UtilValidate.isNotEmpty(product)){
							tempMap["productName"]=product.productName;
							tempMap["productCode"]=(product.internalName).substring(0, 8);
						}
						tempMap["partyName"] = PartyHelper.getPartyName(delegator, orderRole.partyId, false);
						tempMap["orderDate"]=UtilDateTime.toDateString(productEntry.orderDate, "dd-MM-yy");
						//Debug.log("orderDate======="+tempMap["orderDate"]);
						tempMap["orderNo"]=orderNo;
						
						ProductWiseMap.put(productEntry.productId, tempMap);
						
					finalMap.put(orderId, ProductWiseMap);
					finalCSVList.add(tempMap);
					
					//}
				  }
				}
			}
		}
	}
	tempTotMap.put("orderNo", "TOTAL");
	tempTotMap.put("initialQty", totinitialQty);
	tempTotMap.put("finalQty", totfinalQty);
	tempTotMap.put("diffQty", totdiffQty);
	context.tempTotMap=tempTotMap;
	finalCSVList.add(tempTotMap);
	context.IndentVsDispatchMap=finalMap;
	context.finalCSVList=finalCSVList;
	
	
