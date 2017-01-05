import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import java.lang.*;
import java.lang.Long;
import java.math.BigDecimal;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Time;
import java.sql.Timestamp;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.ofbiz.party.party.PartyHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.ofbiz.service.ServiceUtil;

dctx = dispatcher.getDispatchContext();
JSONArray dataList = new JSONArray();
period = parameters.periodName;

Debug.log("period==========="+period);

isFormSubmitted=parameters.isFormSubmitted;

if("Y".equals(isFormSubmitted)){
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
fromDate=null;
thruDate=null;

thruDate =UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());

if("One_Month".equals(period)){
	fromDate = UtilDateTime.addDaysToTimestamp(thruDate,-30);
	context.periodName="Last One Month";
}
if("Two_Month".equals(period)){
	fromDate = UtilDateTime.addDaysToTimestamp(thruDate,-60);
	context.periodName="Last Two Months";
}
if("Three_Month".equals(period)){
	fromDate = UtilDateTime.addDaysToTimestamp(thruDate,-90);
	context.periodName="Last Three  Months";
}
if("Six_Month".equals(period)){
	fromDate = UtilDateTime.addDaysToTimestamp(thruDate,-180);
	context.periodName="Last  Six Months";
}

branchId=parameters.branchId2;

context.branchId=branchId;

branchId = parameters.branchId2;
branchIdName =  PartyHelper.getPartyName(delegator, branchId, false);
context.branchIdName=branchIdName;

dctx = dispatcher.getDispatchContext();

days = parameters.days;

branchId = parameters.branch;

partyId = userLogin.get("partyId");

resultCtx = dispatcher.runSync("getCustomerBranch",UtilMisc.toMap("userLogin",userLogin));


Map formatMap = [:];
List formatList = [];
List productStoreList = resultCtx.get("productStoreList");
context.productStoreList = productStoreList;

for (eachList in productStoreList) {
	formatMap = [:];
	formatMap.put("productStoreName",eachList.get("storeName"));
	formatMap.put("payToPartyId",eachList.get("payToPartyId"));
	formatList.addAll(formatMap);
}
context.formatList = formatList;

if(!branchId)
branchId = "INT10";

branchList = [];

if(branchId){
condListb = [];

condListb.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, branchId));
condListb.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
condListb = EntityCondition.makeCondition(condListb, EntityOperator.AND);

PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdTo"), null, null, false);

branchList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);

if(!branchList)
branchList.add(branchId);
}else if(!branchId){

formatList1 = [];
for (eachList in formatList) {
	formatList1.add(eachList.payToPartyId);
}
branchList = formatList1;
}


if(UtilValidate.isNotEmpty(days))
days = Integer.parseInt(days);
else
days = 7;

branchListOf = [];

branchListOf.add(branchId);

if(days > 0){

	for (eachBranch in branchListOf) {
		
		Debug.log("eachBranch============="+eachBranch);
		
		 branchMap = [:];
		
		branchWise = 0;
	
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,eachBranch));
	conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL,"ORDER_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
	if(UtilValidate.isNotEmpty(fromDate)){
		conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
		conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
		
	}
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	orderHeaderAndRoles1 = delegator.findList("OrderHeaderAndRoles", condition,UtilMisc.toSet("orderId"), null, null, false);
	
	FromOrders=EntityUtil.getFieldListFromEntityList(orderHeaderAndRoles1, "orderId", true);
	
	Debug.log("FromOrders================="+FromOrders);
	


for (eachOrder in FromOrders) {
	
	
	 exprCondList=[];
   exprCondList.add(EntityCondition.makeCondition("toOrderId", EntityOperator.EQUALS, eachOrder));
   exprCondList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, "BackToBackOrder"));
   EntityCondition disCondition = EntityCondition.makeCondition(exprCondList, EntityOperator.AND);
   eachOrderAssoc = EntityUtil.getFirst(delegator.findList("OrderAssoc", disCondition, null,null,null, false));
   
	//Debug.log("purcahseorderId================="+purcahseorderId);
	
	
	if(eachOrderAssoc){
		purchaseOrder = "";
		if(eachOrderAssoc){
			purchaseOrder=eachOrderAssoc.orderId;
		}
	
		//Debug.log("purchaseOrder================="+purchaseOrder);
		
		
		SaleOrderHeader = delegator.findOne("OrderHeader",[orderId : eachOrder] , false);
		
		PurchaseOrderHeader = delegator.findOne("OrderHeader",[orderId : purchaseOrder] , false);
		
		
		if(SaleOrderHeader && PurchaseOrderHeader){
			
			saleOrderDate = SaleOrderHeader.orderDate;
			
			purchaseOrderDate = PurchaseOrderHeader.orderDate;
			
			long timeDiff = 0;
			if(purchaseOrderDate && saleOrderDate)
			  timeDiff = saleOrderDate.getTime() - purchaseOrderDate.getTime();
			
			 diffHours = timeDiff / (60 * 60 * 1000);
			
			
			int diffHours = diffHours.intValue();
			
			diffDays = diffHours/24;
			
			
			diffDays = Math.abs(diffDays);
			
			//Debug.log("diffDays============="+diffDays);
			
			if(diffDays >= days){
				
				branchMap.put("orderId", eachOrder);
				
				orderHeaderSequences = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , eachOrder)  , UtilMisc.toSet("orderNo"), null, null, false );
				
				if(UtilValidate.isNotEmpty(orderHeaderSequences)){
					orderSeqDetails = EntityUtil.getFirst(orderHeaderSequences);
					orderNo = orderSeqDetails.orderNo;
					branchMap.put("orderNo", orderNo);
					
				}else{
				
				branchMap.put("orderNo", eachOrder);
				}
				
				branchIdName =  PartyHelper.getPartyName(delegator, eachBranch, false);
				
				branchMap.put("branchIdName", branchIdName);
				
				dateStr=UtilDateTime.toDateString(saleOrderDate,"dd/MM/yyyy");
				shptDateStr=UtilDateTime.toDateString(purchaseOrderDate,"dd/MM/yyyy");
				
				branchMap.put("saleOrderDate", dateStr);
				
				branchMap.put("purchaseOrderDate", shptDateStr);
				
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,eachOrder));
				cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				OrderItemDetail = delegator.findList("OrderItem", cond, null, null, null, false);
		   
				orderQuantity = 0;
				
				for (eachItem in OrderItemDetail) {
					
					orderQuantity = orderQuantity + eachItem.quantity;
					
				}
				
				branchMap.put("orderQuantity", orderQuantity);
				
				branchMap.put("diffDays", Math.round(diffDays)+" Days");
				
				dataList.add(branchMap);
				
			}
		
	   }
	
}



}



	}

}
}

//Debug.log("dataList============="+dataList);

context.putAt("dataJSON",dataList);
Map resultMap = FastMap.newInstance();
resultMap = ServiceUtil.returnSuccess();
resultMap.put("data",dataList);
return resultMap;
