import org.ofbiz.base.util.UtilDateTime;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javolution.util.FastList;
import org.ofbiz.entity.Delegator;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import org.ofbiz.entity.util.*;
import net.sf.json.JSONArray;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;

parameters.ownerPartyId = "";
conditionList = [];
conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userLogin.get("partyId")));
conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "EMPLOYEE"));
conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
employment = delegator.findList("Employment", EntityCondition.makeCondition(conditionList,EntityOperator.AND),null, null, null, false);
if(UtilValidate.isNotEmpty(employment)){
	GenericValue empDetail = EntityUtil.getFirst(employment);
	if(empDetail.partyIdFrom != "Company" || empDetail.partyIdFrom != "HO"){
		parameters.ownerPartyId = empDetail.partyIdFrom;
		parameters.roId = empDetail.partyIdFrom;
	}
}
else{
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userLogin.get("partyId")));
	conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
	conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "BRANCH_EMPLOYEE"));
	conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
	employment = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditionList,EntityOperator.AND),null, null, null, false);	
	if(UtilValidate.isNotEmpty(employment)){
		GenericValue empDetail = EntityUtil.getFirst(employment);
		if(empDetail.partyIdFrom != "Company" || empDetail.partyIdFrom != "HO"){
			parameters.ownerPartyId = empDetail.partyIdFrom;
			parameters.roId = empDetail.partyIdFrom;
		}
	}
}

PartyAcctgPreferenceSegmentList = [];
segmentconditionList = [];
if(UtilValidate.isNotEmpty(parameters.ownerPartyId)){
	segmentconditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, parameters.ownerPartyId));
}
PartyAcctgPreferenceSegmentList = delegator.findList("PartyAcctgPreferenceSegment", EntityCondition.makeCondition(segmentconditionList,EntityOperator.AND),null, null, null, false);

segmentIds=EntityUtil.getFieldListFromEntityList(PartyAcctgPreferenceSegmentList, "segmentId", true);
enumconditionList = [];
segmentList = [];
enumconditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.IN, segmentIds));
segmentList = delegator.findList("Enumeration", EntityCondition.makeCondition(enumconditionList,EntityOperator.AND),null, null, null, false);
context.segmentList=segmentList;

orgPartyIdList = [];
conditionList.clear();
if(UtilValidate.isNotEmpty(parameters.ownerPartyId)){
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, parameters.ownerPartyId));
}
conditionList.add(EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.IN, ["REGIONAL_OFFICE","BRANCH_OFFICE"]));
orgPartyIdList = delegator.findList("PartyClassification", EntityCondition.makeCondition(conditionList,EntityOperator.AND),null, null, null, false);
context.orgPartyIdList=orgPartyIdList;

costCenterPartyIdList = [];
conditionList.clear();
if(UtilValidate.isNotEmpty(parameters.ownerPartyId)){
	resultCtx = dispatcher.runSync("getRoBranchList",UtilMisc.toMap("userLogin",userLogin,"productStoreId",parameters.ownerPartyId));
	partyList = resultCtx.get("partyList");
	partyList.each{ eachBranch ->
		tempMap=[:];
		tempMap.put("partyId", eachBranch.partyIdTo);
		costCenterPartyIdList.add(tempMap);
	}
}
else{
	conditionList.add(EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.IN, ["REGIONAL_OFFICE","BRANCH_OFFICE","COMPANY","HEAD_OFFICE"]));
	costCenterPartyIdList = delegator.findList("PartyClassification", EntityCondition.makeCondition(conditionList,EntityOperator.AND),null, null, null, false);
}
context.costCenterPartyIdList=costCenterPartyIdList;

partyList = [];
partyList.addAll(orgPartyIdList);
partyList.addAll(costCenterPartyIdList);
context.partyList=partyList;

conditionList.clear();
conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, "Company"));
conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
conditionList.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "BRANCH_CUSTOMER"));
if(UtilValidate.isNotEmpty(parameters.ownerPartyId)){
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, parameters.ownerPartyId));
}
intOrgList = delegator.findList("PartyRelationshipAndDetail", EntityCondition.makeCondition(conditionList,EntityOperator.AND),null, null, null, false);

context.roId=parameters.roId;
context.intOrgList=intOrgList;



