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
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
fromDate=null;
thruDate=null;
try {
	   if (parameters.fromDate) {
			   fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
	   }
	   if (parameters.thruDate) {
			   thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.thruDate).getTime()));
	   }else {
			   thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
	   }
} catch (ParseException e) {
	   Debug.logError(e, "Cannot parse date string: " + e, "");
	   context.errorMessage = "Cannot parse date string: " + e;
	   return;
}
if(UtilValidate.isNotEmpty(fromDate)){
	context.defaultEffectiveDate=UtilDateTime.toDateString(fromDate,"MMMM dd, yyyy");
}
if(UtilValidate.isNotEmpty(thruDate)){
	context.defaultEffectiveThruDate=UtilDateTime.toDateString(thruDate,"MMMM dd, yyyy");
}
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
days = 0;




/*conditionList = [];
//conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN,FromOrders));
conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL,"ORDER_CANCELLED"));
if(UtilValidate.isNotEmpty(fromDate)){
	conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
	conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
	
}
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
SaleOrderHeader = delegator.findList("OrderHeader", condition,UtilMisc.toSet("orderId","orderDate"), null, null, false);

SalesOrders=EntityUtil.getFieldListFromEntityList(SaleOrderHeader, "orderId", true);

Debug.log("SalesOrders================="+SalesOrders);

conditionList = [];
conditionList.add(EntityCondition.makeCondition("toOrderId", EntityOperator.IN,SalesOrders));
conditionList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, "BackToBackOrder"));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
OrderAssoc = delegator.findList("OrderAssoc", condition,UtilMisc.toSet("orderId","toOrderId"), null, null, false);

purchaseOrders = EntityUtil.getFieldListFromEntityList(OrderAssoc, "orderId", true);

Debug.log("purchaseOrders================="+purchaseOrders);

conditionList.clear();
conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN,purchaseOrders));
conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL,"ORDER_CANCELLED"));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
PurchaseOrderHeader = delegator.findList("OrderHeader", condition,UtilMisc.toSet("orderId","orderDate"), null, null, false);
*/

condListb = [];
condListb.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, "Company"));
condListb.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.NOT_EQUAL, "HO"));
condListb.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
condListb.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
condListb = EntityCondition.makeCondition(condListb, EntityOperator.AND);

PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdTo"), null, null, false);
//branchList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);

//Debug.log("days========="+days);

if(days > 0){
hoCount = 0;

JSONObject hoJSson = new JSONObject();


hoJSson.put("partyId", "nhdcId");
hoJSson.put("ROName", "NHDC");
hoJSson.put("branchName", "");
hoJSson.put("ReportsTo", "");





for (eachPartyRel in PartyRelationship) {
	
	
	roID = eachPartyRel.partyIdTo;
	
	//hoJSson.put("partyId", roID);
	
	//hoJSson.put("ROName", roID);
	
	JSONObject roJson = new JSONObject();
	
	roWiseCount = 0;
	
	
	branchList = [];
	
	if(roID){
	condListb = [];
	condListb.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, roID));
	condListb.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
	condListb = EntityCondition.makeCondition(condListb, EntityOperator.AND);
	
	PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdTo"), null, null, false);
	branchList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);
	}
	
	
	
	
	for (eachBranch in branchList) {
		
	//	Debug.log("eachBranch============="+eachBranch);
		
		JSONObject branchJson = new JSONObject();
		
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
	
	//Debug.log("FromOrders================="+FromOrders);
	


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
				branchWise = branchWise+1;
			}
		
	   }	
	
}



}



BranchpartyName = PartyHelper.getPartyName(delegator, eachBranch, false);
if(branchWise > 0){
branchJson.put("partyId", eachBranch);
branchJson.put("ROName", "");
branchJson.put("branchName", BranchpartyName);
branchJson.put("Count", branchWise);
branchJson.put("ReportsTo", roID);

dataList.add(branchJson);

}
roWiseCount = roWiseCount+branchWise;

	}
	
	ropartyName = PartyHelper.getPartyName(delegator, roID, false);
	
	if(roWiseCount > 0){
	
	roJson.put("partyId", roID);
	roJson.put("ROName", ropartyName);
	roJson.put("branchName", "");
	roJson.put("Count", roWiseCount);
	roJson.put("ReportsTo", "nhdcId");
	
	dataList.add(roJson);
	}
	
	hoCount = hoCount + roWiseCount;
	
}

hoJSson.put("Count", hoCount);

dataList.add(hoJSson);

}

context.putAt("dataJSON",dataList);
Map resultMap = FastMap.newInstance();
resultMap = ServiceUtil.returnSuccess();
resultMap.put("data",dataList);
return resultMap;
