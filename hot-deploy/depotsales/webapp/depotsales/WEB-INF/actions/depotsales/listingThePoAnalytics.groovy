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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ofbiz.entity.DelegatorFactory;

HttpServletRequest httpRequest = (HttpServletRequest) request;
HttpServletResponse httpResponse = (HttpServletResponse) response;
dctx = dispatcher.getDispatchContext();
delegator = DelegatorFactory.getDelegator("default#NHDC");

dctx = dispatcher.getDispatchContext();
JSONArray dataList = new JSONArray();
period = parameters.periodName;

isFormSubmitted=parameters.isFormSubmitted;

List formatRList = [];
List formatBList = [];
List<GenericValue> partyClassificationList = null;
	partyClassificationList = delegator.findList("PartyClassification", EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.IN, UtilMisc.toList("REGIONAL_OFFICE","BRANCH_OFFICE")), UtilMisc.toSet("partyId","partyClassificationGroupId"), null, null,false);
if(partyClassificationList){
	for (eachList in partyClassificationList) {
		formatMap = [:];
		partyName = PartyHelper.getPartyName(delegator, eachList.get("partyId"), false);
		formatMap.put("productStoreName",partyName);
		formatMap.put("payToPartyId",eachList.get("partyId"));
		if(eachList.partyClassificationGroupId=="REGIONAL_OFFICE"){
			formatRList.addAll(formatMap);
		}else{
			formatBList.addAll(formatMap);
		}
	}
}
context.formatRList = formatRList;
context.formatBList = formatBList;
daystart = null;
dayend = null;
period=parameters.period;
context.period=period;
periodFrmDate=null;
isFormSubmitted=parameters.isFormSubmitted;

periodFrmDate=null;
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("geoId", EntityOperator.LIKE,"IN-%"));
	conditionList.add(EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS,"STATE"));
	statesList = delegator.findList("Geo",EntityCondition.makeCondition(conditionList,EntityOperator.AND),null,null,null,false);
	statesIdsList=EntityUtil.getFieldListFromEntityList(statesList, "geoId", true);
	
	JSONArray stateListJSON = new JSONArray();
	statesList.each{ eachState ->
			JSONObject newObj = new JSONObject();
			newObj.put("value",eachState.geoId);
			newObj.put("label",eachState.geoName);
			stateListJSON.add(newObj);
	}
	context.stateListJSON = stateListJSON;

if("Y".equals(isFormSubmitted)){
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
fromDate=null;
thruDate=null;
thruDate =UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
branchIds=[]; 
	branchId = parameters.branchId2;
	searchType = parameters.searchType;
	context.searchType=searchType;
	branchIdName =  PartyHelper.getPartyName(delegator, branchId, false);
	context.branchId=branchId;
	context.branchIdName=branchIdName;
	if(UtilValidate.isNotEmpty(branchId) && "BY_BO".equals(searchType)){
		branchIds.add(branchId)
		context.searchTypeName="By Branch"
	}
	regionId = parameters.regionId;
	regionIdName =  PartyHelper.getPartyName(delegator, regionId, false);
	context.regionId=regionId;
	context.regionIdName=regionIdName;
	if(UtilValidate.isNotEmpty(regionId) && "BY_RO".equals(searchType)){
		partyRelationship = delegator.findList("PartyRelationship", EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS,regionId), UtilMisc.toSet("partyIdTo"), null, null,false);
		branchIds=EntityUtil.getFieldListFromEntityList(partyRelationship, "partyIdTo", true);
		context.searchTypeName="By Regional Office";
	}
	stateId = parameters.stateId;
	if(UtilValidate.isNotEmpty(stateId) && "BY_STATE".equals(searchType)){
		GenericValue state=delegator.findOne("Geo",[geoId:stateId],false);
		context.searchTypeName="By State";
		context.stateId=stateId;
		context.stateIdName=state.geoName;
		roIdsList=[];
		branchIdsList=[];
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("stateProvinceGeoId", EntityOperator.EQUALS, stateId));
		conditionList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.LIKE, "INT%"));
		stateWiseRosAndBranchList = delegator.findList("PartyContactDetailByPurpose", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
		if(UtilValidate.isNotEmpty(stateWiseRosAndBranchList)){
			List roAndBranchIds = EntityUtil.getFieldListFromEntityList(stateWiseRosAndBranchList, "partyId", true);
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, roAndBranchIds));
			conditionList.add(EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.EQUALS, "BRANCH_OFFICE"));
			List<GenericValue> partyClassicationForBranch= delegator.findList("PartyClassification", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
			if(UtilValidate.isNotEmpty(partyClassicationForBranch)){
				 branchIdsList = EntityUtil.getFieldListFromEntityList(partyClassicationForBranch, "partyId", true);
			}
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, roAndBranchIds));
			conditionList.add(EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.EQUALS, "REGIONAL_OFFICE"));
			List<GenericValue> partyClassicationForRo= delegator.findList("PartyClassification", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
			if(UtilValidate.isNotEmpty(partyClassicationForRo)){
				roIdsList = EntityUtil.getFieldListFromEntityList(partyClassicationForRo, "partyId", true);
			}
			List<GenericValue> partyGroupRo=null;
			List<GenericValue> partyGroupBranch=null;
			if(UtilValidate.isNotEmpty(roIdsList)){
				stateRosList = delegator.findList("PartyGroup", EntityCondition.makeCondition("partyId", EntityOperator.IN, roIdsList), UtilMisc.toSet("partyId","groupName"), null, null, false);
				stateRosList.each{ eachState ->
					branchIds.add(eachState.partyId);
				}
			}
			if(UtilValidate.isNotEmpty(branchIdsList)){
				stateBranchsList = delegator.findList("PartyGroup", EntityCondition.makeCondition("partyId", EntityOperator.IN, branchIdsList), UtilMisc.toSet("partyId","groupName"), null, null, false);
				stateBranchsList.each{ eachState ->
					branchIds.add(eachState.partyId);
				}
			}
		}
	}
	dayend =UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());


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

/*branchId=parameters.branchId2;

context.branchId=branchId;

branchId = parameters.branchId2;
branchIdName =  PartyHelper.getPartyName(delegator, branchId, false);
context.branchIdName=branchIdName;
*/
dctx = dispatcher.getDispatchContext();

days = parameters.days;

branchId = parameters.branchId2;

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

/*if(!branchId)
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
*/

if(UtilValidate.isNotEmpty(days))
days = Integer.parseInt(days);
else
days = 7;

branchListOf = [];

branchListOf.addAll(branchIds);

if(days > 0){

	for (eachBranch in branchListOf) {
		
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

for (eachOrder in FromOrders) {	
	 exprCondList=[];
   exprCondList.add(EntityCondition.makeCondition("toOrderId", EntityOperator.EQUALS, eachOrder));
   exprCondList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, "BackToBackOrder"));
   EntityCondition disCondition = EntityCondition.makeCondition(exprCondList, EntityOperator.AND);
   eachOrderAssoc = EntityUtil.getFirst(delegator.findList("OrderAssoc", disCondition, null,null,null, false));
  
	if(eachOrderAssoc){
		purchaseOrder = "";
		if(eachOrderAssoc){
			purchaseOrder=eachOrderAssoc.orderId;
		}		
		
		SaleOrderHeader = delegator.findOne("OrderHeader",[orderId : eachOrder] , false);
		
		PurchaseOrderHeader = delegator.findOne("OrderHeader",[orderId : purchaseOrder] , false);
		
		
		if(SaleOrderHeader && PurchaseOrderHeader){
			
			saleOrderDate = SaleOrderHeader.orderDate;
			
			purchaseOrderDate = PurchaseOrderHeader.orderDate;
			
			long timeDiff = 0;
			if(purchaseOrderDate && saleOrderDate)
			  timeDiff =  purchaseOrderDate.getTime()-saleOrderDate.getTime();
		
			  	
			 diffHours = timeDiff / (60 * 60 * 1000);
			
			
			int diffHours = diffHours.intValue();
			
			diffDays = diffHours/24;
			
			if(diffDays > 0){
					
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
}
formSubmitted="N";
if(UtilValidate.isNotEmpty(dataList)){
	formSubmitted="Y";
}
context.formSubmitted=formSubmitted

context.putAt("dataJSON",dataList);
Map resultMap = FastMap.newInstance();
resultMap = ServiceUtil.returnSuccess();
resultMap.put("data",dataList);
return resultMap;
