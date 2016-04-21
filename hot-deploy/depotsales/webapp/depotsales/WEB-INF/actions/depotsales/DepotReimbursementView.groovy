
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.party.party.PartyHelper;

import java.sql.*;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.model.DynamicViewEntity
import org.ofbiz.entity.model.ModelKeyMap;

timePeriodFromDate="";
timePeriodThruDate="";
branchId = parameters.branchId;
passbookNumber = parameters.passbookNumber;
partyId = parameters.partyId;
groupName = parameters.groupName;
timePeriodId=parameters.timePeriodId;
reimbursementEligibilityPercentage=2;
JSONObject depotReimbursementJson = new JSONObject();
//get Branch list start
resultCtx = dispatcher.runSync("getCustomerBranch",UtilMisc.toMap("userLogin",userLogin));


Map formatMap = [:];
List formatList = [];

	for (eachList in resultCtx.get("productStoreList")) {
		
		formatMap = [:];
		formatMap.put("productStoreName",eachList.get("storeName"));
		formatMap.put("payToPartyId",eachList.get("payToPartyId"));
		formatList.addAll(formatMap);
		
	}
context.formatList = formatList;

//get Branch list end

//get SchemeTimePeriod list start
	condList = [];
	condList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "DEPOT_REIMB_YEAR"));
	condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.toSqlDate( UtilDateTime.nowTimestamp())));
	condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),EntityOperator.OR,EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN,UtilDateTime.toSqlDate(UtilDateTime.nowTimestamp()))));
    depotReimbYearList = delegator.findList("SchemeTimePeriod", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
	depotReimbYear = EntityUtil.getFirst(depotReimbYearList);
	if(depotReimbYear) {
		condList.clear();
		condList.add(EntityCondition.makeCondition("parentPeriodId", EntityOperator.EQUALS,depotReimbYear.get("schemeTimePeriodId")));
		condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(UtilDateTime.nowTimestamp())));
//		condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),EntityOperator.OR,
//		EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, UtilDateTime.toSqlDate(UtilDateTime.nowTimestamp()))));
		List<String> orderBy = UtilMisc.toList("periodNum");
		 depotReimbPeriodList = delegator.findList("SchemeTimePeriod", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
		List reimbPeriodList = [];
		for (eachList in depotReimbPeriodList) {
			
			depotReimbPeriodMap = [:];
			timePeriodDate=new SimpleDateFormat("MMM").format(eachList.get("fromDate"))+", "+new SimpleDateFormat("yyyy").format(eachList.get("fromDate"));
			
			if(UtilValidate.isNotEmpty(eachList.get("thruDate"))){
				timePeriodDate=timePeriodDate+" - "+new SimpleDateFormat("MMM").format(eachList.get("thruDate"))+", "+new SimpleDateFormat("yyyy").format(eachList.get("thruDate"));
			}
			depotReimbPeriodMap.put("schemeTimePeriodId",eachList.get("schemeTimePeriodId"));
			depotReimbPeriodMap.put("value",timePeriodDate);
			reimbPeriodList.addAll(depotReimbPeriodMap);
			if(UtilValidate.isEmpty(timePeriodId) || timePeriodId.equals(eachList.get("schemeTimePeriodId"))){
				timePeriodId=eachList.get("schemeTimePeriodId");
				timePeriodFromDate=eachList.get("fromDate");
				timePeriodThruDate=eachList.get("thruDate");
			}
		}
	    context.reimbPeriodList = reimbPeriodList;
	
		}
//get SchemeTimePeriod list end


//get search result start

partyRoleAndIde = new DynamicViewEntity();
partyRoleAndIde.addMemberEntity("FACILITY", "Facility");
partyRoleAndIde.addMemberEntity("PRPD", "PartyRoleDetailAndPartyDetail");
//partyRoleAndIde.addMemberEntity("PID", "PartyIdentification");
partyRoleAndIde.addMemberEntity("PRS", "PartyRelationship");
partyRoleAndIde.addAliasAll("FACILITY", null, null);
partyRoleAndIde.addAliasAll("PRPD", null, null);
//partyRoleAndIde.addAliasAll("PID", null, null);
partyRoleAndIde.addAliasAll("PRS", null, null);
partyRoleAndIde.addViewLink("FACILITY","PRPD", Boolean.FALSE, ModelKeyMap.makeKeyMapList("ownerPartyId","partyId"));
//partyRoleAndIde.addViewLink("PRPD","PID", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));
partyRoleAndIde.addViewLink("PRPD","PRS", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId","partyIdTo"));
condList = [];
condList.add(EntityCondition.makeCondition("facilityTypeId" ,EntityOperator.EQUALS, "DEPOT_SOCIETY"));
condList.add(EntityCondition.makeCondition("roleTypeId" ,EntityOperator.EQUALS, "EMPANELLED_CUSTOMER"));

if(partyId!=null && partyId!=""){
	condList.add(EntityCondition.makeCondition("partyId" ,EntityOperator.EQUALS,partyId.trim()));
}
if(groupName!=null && groupName!=""){
	condList.add(EntityCondition.makeCondition("facilityName" ,EntityOperator.LIKE,"%"+groupName.trim()+"%"));
}
if(branchId!=null && branchId!=""){
	condList.add(EntityCondition.makeCondition("partyIdFrom" ,EntityOperator.EQUALS,branchId.trim()));
}
//if(passbookNumber!=null && passbookNumber!=""){
//	condList.add(EntityCondition.makeCondition("idValue" ,EntityOperator.EQUALS,passbookNumber.trim()));
//}
cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
prodsEli = delegator.findListIteratorByCondition(partyRoleAndIde, cond,null ,UtilMisc.toSet("partyId","facilityId","facilityName","partyIdFrom"), null, null);
groupNameList = prodsEli.getCompleteList();
finalList =[];

for(customer in groupNameList){
	eachPartyId=String.valueOf(customer.get("partyId"));
	branchId=String.valueOf(customer.get("partyIdFrom"));
	tempMap = [:];
	tempMap.put("partyId",eachPartyId);
	facilityId=String.valueOf(customer.get("facilityId"));
	tempMap.put("facilityId",facilityId);
//	String partyName = PartyHelper.getPartyName(delegator,eachPartyId,false);
//	tempMap.put("partyName",partyName);
	tempMap.put("groupName",customer.get("facilityName"));
//	tempMap.put("passbookId",customer["idValue"])
	if(branchId!=null && UtilValidate.isNotEmpty(branchId)){
		result = EntityUtil.filterByCondition(resultCtx.get("productStoreList"), EntityCondition.makeCondition("payToPartyId", EntityOperator.EQUALS, branchId));
		if(result.size()>0 && UtilValidate.isNotEmpty(result[0].get("storeName"))){
			tempMap.put("storeName",result[0].get("storeName"));
		}else{
		  tempMap.put("storeName","");
		}
		
	}else{
		  tempMap.put("storeName","");
		}
	
	//get depot period invoice details start
	
	conditionList = [];
//	Timestamp periodStart = UtilDateTime.getMonthStart(fromMonthTime);
//	Timestamp periodEnd = UtilDateTime.getMonthEnd(fromMonthTime, timeZone, locale);
//	conditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,facilityId));
	conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,eachPartyId));
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
	Debug.log(timePeriodFromDate.toString()+"^^^^^^^^^^^^^^^^^^+++++++444++++++++^^^^^^^^^^^^^^^^^^"+timePeriodThruDate.toString());
	Debug.log(UtilDateTime.toTimestamp(timePeriodFromDate).toString()+"^^^^^^^^^^^^^^^^^^+++++++++++++++^^^^^^^^^^^^^^^^^^"+UtilDateTime.toTimestamp(timePeriodThruDate).toString());
	conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.BETWEEN,UtilMisc.toList(UtilDateTime.toTimestamp(timePeriodFromDate),UtilDateTime.toTimestamp(timePeriodThruDate))));
//		conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"INVOICE_PAID"));
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	invoiceAndItemList=delegator.findList("InvoiceAndItem",condition,null,null,null,false);
	Debug.log(timePeriodFromDate.toString()+"^^^^^^^^^^^^^^^^^^++++++++2+++++++^^^^^^^^^^^^^^^^^^"+timePeriodThruDate.toString());
	Debug.log(invoiceAndItemList.toString()+"^^^^^^^^^^^^^^^^^^++++++++3+++++++^^^^^^^^^^^^^^^^^^");
	invoiceAmount=0;
	invoiceEligablityAmount=0;
	if(UtilValidate.isNotEmpty(invoiceAndItemList)){
		 invoiceAndItemList.each{list->
			 quantity=list.quantity;
			 amount=list.amount;
			 invoiceAmount=invoiceAmount+(quantity.multiply(amount));
		}
		
	}
	reimbursementEligibilityAmount=(invoiceAmount.multiply(reimbursementEligibilityPercentage)).div(100);
	tempMap.put("invoiceAmount",invoiceAmount);
	tempMap.put("reimbursementEligibilityAmount",reimbursementEligibilityAmount);
	tempMap.put("reimbursementEligibilityPercentage",reimbursementEligibilityPercentage);
	
	//get depot period invoice details end
	
	// prepare edit reimbursement array
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,facilityId));
	conditionList.add(EntityCondition.makeCondition("schemeTimePeriodId",EntityOperator.EQUALS,timePeriodId));
//		conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"INVOICE_PAID"));
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	depotReimbursementList=delegator.findList("DepotReimbursementReceipt",condition,null,null,null,false);
//prepare json object
	tempMap.put("receiptAction","AddReceipt");
	claimStatus	="";
	climeAmount=0;
	if(UtilValidate.isNotEmpty(depotReimbursementList)){
		JSONArray depotReimbursementJsonArray = new JSONArray();
		depotReimbursementList.each{eachItem->
			JSONObject newObj = new JSONObject();
			
			newObj.put("claimId",eachItem.claimId);
			newObj.put("claimType",eachItem.schemeTimePeriodId);
			newObj.put("receiptAmount",eachItem.receiptAmount);
			newObj.put("description",eachItem.description);	
			newObj.put("fromDate",UtilDateTime.toDateString(eachItem.fromDate, "dd/MM/yyyy"));
			newObj.put("thruDate",UtilDateTime.toDateString(eachItem.thruDate, "dd/MM/yyyy"));
			depotReimbursementJsonArray.add(newObj);
			claimStatus=eachItem.statusId;
			climeAmount=climeAmount+eachItem.receiptAmount;
		}
		depotReimbursementJson.put(facilityId,depotReimbursementJsonArray);
		tempMap.put("receiptAction","EditReceipt");
		climeAmount=(reimbursementEligibilityAmount>=climeAmount?climeAmount:reimbursementEligibilityAmount);
		
	}
	tempMap.put("claimAmount",climeAmount);
	tempMap.put("claimStatus",claimStatus);
	
	finalList.addAll(tempMap);
	
}
//get search result end

context.listIt = finalList;
context.depotReimbursementList=depotReimbursementJson.toString();
JSONObject timePeriodIdJson = new JSONObject();
timePeriodIdJson.put("timePeriodId", timePeriodId)
context.timePeriodIds=timePeriodIdJson.toString();


