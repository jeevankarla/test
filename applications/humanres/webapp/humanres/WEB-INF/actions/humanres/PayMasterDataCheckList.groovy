import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilNumber;
import in.vasista.vbiz.humanres.PayrollService;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.humanres.HumanresService;

dctx = dispatcher.getDispatchContext();
sortBy = UtilMisc.toList("sequenceNum");
context.dctx=dctx;
allChanges= false;
fromDate = null;
thruDate = null;
reportTypeFlag = parameters.reportTypeFlag;

if (reportTypeFlag.equals("payMasterDataAllCheckList")) {
	allChanges = true;
}

def sdf = new SimpleDateFormat("MMMM dd, yyyy");
if(reportTypeFlag.equals("payMasterDataMyCheckList")){
	try {
		if (parameters.payMasterDataMyfromDate) {
			fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.payMasterDataMyfromDate).getTime()));
		}
		if (parameters.payMasterDataMythruDate) {
			thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.payMasterDataMythruDate).getTime()));
		}
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + e, "");
		context.errorMessage = "Cannot parse date string: " + e;
		return;
	}
}else if(reportTypeFlag.equals("payMasterDataAllCheckList")){
	try {
		if (parameters.payMasterDataAllfromDate) {
			fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.payMasterDataAllfromDate).getTime()));
		}
		if (parameters.payMasterDataAllthruDate) {
			thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.payMasterDataAllthruDate).getTime()));
		}
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + e, "");
		context.errorMessage = "Cannot parse date string: " + e;
		return;
	}
}

context.put("fromDate",fromDate);
context.put("thruDate",thruDate);

dayBegin = UtilDateTime.getDayStart(fromDate, timeZone, locale);
dayEnd = UtilDateTime.getDayEnd(thruDate, timeZone, locale);

if(UtilValidate.isNotEmpty(context.reportFlag) && (context.reportFlag).equals("summary")){
	sortBy = UtilMisc.toList("description");
}
benefitTypeList = delegator.findList("BenefitType", null, null, sortBy, null, false);
benefitTypeIds = EntityUtil.getFieldListFromEntityList(benefitTypeList, "benefitTypeId", true);
context.benefitTypeIds=benefitTypeIds;

deductionTypeList = delegator.findList("DeductionType", null, null, sortBy, null, false);
dedTypeIds = EntityUtil.getFieldListFromEntityList(deductionTypeList, "deductionTypeId", true);
context.dedTypeIds=dedTypeIds;

employementIds = [];
Map emplInputMap = FastMap.newInstance();
if(UtilValidate.isNotEmpty(parameters.partyId)){
	emplInputMap.put("orgPartyId", parameters.partyId);
}else{
	emplInputMap.put("orgPartyId", "Company");
}
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("fromDate", dayBegin);
emplInputMap.put("thruDate", dayEnd);
Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
List<GenericValue> employementList = (List<GenericValue>)EmploymentsMap.get("employementList");
employementList = EntityUtil.orderBy(employementList, UtilMisc.toList("partyIdTo"));
employementIds = EntityUtil.getFieldListFromEntityList(employementList, "partyIdTo", true);
partyBenefitMap = [:];
if(UtilValidate.isNotEmpty(employementIds)){
	employementIds.each{ employee ->
		payRateList=[];
		if(!allChanges){
			payRateList.add(EntityCondition.makeCondition("lastModifiedByUserLogin", EntityOperator.EQUALS , userLogin.userLoginId));
		}
		payRateList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS ,employee));
		payRateList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS ,parameters.partyId));
		payRateList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS ,"INTERNAL_ORGANIZATIO"));
		payRateList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, dayBegin));
			//EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd)], EntityOperator.AND));
		//payRateList.add(EntityCondition.makeCondition("fromDate", EntityOperator.BETWEEN, UtilMisc.toList(dayBegin,dayEnd)));
		payRateCond = EntityCondition.makeCondition(payRateList,EntityOperator.AND);
		payRateList = delegator.findList("PayHistory", payRateCond, null, null, null, false);
		if(UtilValidate.isNotEmpty(payRateList)){
			payRateList.each { pay ->
				benefitMap = [:];
				salaryStepSeqId = pay.get("salaryStepSeqId");
				payGradeId = pay.get("payGradeId");
				payRateSalary = delegator.findOne("SalaryStep", [salaryStepSeqId : salaryStepSeqId, payGradeId : payGradeId], false);
				if(UtilValidate.isNotEmpty(payRateSalary)){
					salary = payRateSalary.get("amount");
					if(UtilValidate.isNotEmpty(benefitTypeIds)){
						benefitTypeIds.each { benefit ->
							conditionList = [];
							if(!allChanges){
								conditionList.add(EntityCondition.makeCondition("lastModifiedByUserLogin", EntityOperator.EQUALS , userLogin.userLoginId));
							}
							conditionList.add(EntityCondition.makeCondition("benefitTypeId", EntityOperator.EQUALS , benefit));
							conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS , employee));
							conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, dayBegin));
							conditionList.add(EntityCondition.makeCondition([
								EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
								EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin)
							   ], EntityOperator.OR));
							condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
							benefitsItemsList = delegator.findList("PartyBenefit", condition, null, ["lastUpdatedStamp"], null, false);
							if(UtilValidate.isNotEmpty(benefitsItemsList)){
								benefitsItemsList.each { benefitsItem ->
									benefitAmount = benefitsItem.get("cost");
									benefitMap.put(benefit,benefitAmount);
								}
							}
						}
					}
					if(UtilValidate.isNotEmpty(dedTypeIds)){
						dedTypeIds.each { deduction ->
							dedConditionList = [];
							if(!allChanges){
								conditionList.add(EntityCondition.makeCondition("lastModifiedByUserLogin", EntityOperator.EQUALS , userLogin.userLoginId));
							}
							dedConditionList.add(EntityCondition.makeCondition("deductionTypeId", EntityOperator.EQUALS , deduction));
							dedConditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS , employee));
							dedConditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, dayBegin));
							dedConditionList.add(EntityCondition.makeCondition([
								EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
								EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin)
							   ], EntityOperator.OR));
							dedCondition = EntityCondition.makeCondition(dedConditionList, EntityOperator.AND);
							deductionItemsList = delegator.findList("PartyDeduction", dedCondition, null, ["lastUpdatedStamp"], null, false);
							if(UtilValidate.isNotEmpty(deductionItemsList)){
								deductionItemsList.each { deductionItem ->
									deductionAmount = deductionItem.get("cost");
									benefitMap.put(deduction,deductionAmount);
								}
							}
						}
					}
				}
				unitDetails = delegator.findList("Employment", EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS , employee), null, null, null, false);
				if(UtilValidate.isNotEmpty(unitDetails)){
					unitDetails = EntityUtil.getFirst(unitDetails);
					if(UtilValidate.isNotEmpty(unitDetails))	{
						locationGeoId=unitDetails.get("locationGeoId");
						benefitMap.put("unit",locationGeoId);
					}
				}
				partyRelationconditionList=[];
				partyRelationconditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS , "DEPARTMENT"));
				partyRelationconditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS , "EMPLOYEE"));
				partyRelationconditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS ,employee));
				partyCondition = EntityCondition.makeCondition(partyRelationconditionList,EntityOperator.AND);
				def orderBy = UtilMisc.toList("comments");
				partyRelationList = delegator.findList("PartyRelationship", partyCondition, null, orderBy, null, false);
				if(UtilValidate.isNotEmpty(partyRelationList)){
					costDetails = EntityUtil.getFirst(partyRelationList);
					if(UtilValidate.isNotEmpty(costDetails))	{
						costCode=costDetails.get("comments");
						benefitMap.put("costCode",costCode);
					}
				}
				conditionList1 = [];
				conditionList1.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS ,"EMPLOYEE"));
				conditionList1.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS ,employee));
				conditionList1.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
				conditionList1.add(EntityCondition.makeCondition([EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin),
					EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)],EntityOperator.OR));
				EntityCondition cond1 = EntityCondition.makeCondition(conditionList1, EntityOperator.AND);
				finAccountRoleList=delegator.findList("FinAccountRole",cond1, null,null, null, false);
				if(UtilValidate.isNotEmpty(finAccountRoleList)){
					finAccountRoleList.each { finAccountRole ->
						finAccountId = finAccountRole.get("finAccountId");
						conditionList2 = [];
						conditionList2.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS ,"company"));
						conditionList2.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS ,"FNACT_ACTIVE"));
						conditionList2.add(EntityCondition.makeCondition("finAccountId", EntityOperator.EQUALS ,finAccountId));
						EntityCondition condition2 = EntityCondition.makeCondition(conditionList2, EntityOperator.AND);
						companyBankAccountList= delegator.findList("FinAccount",condition2,null,null,null,false);
						if(UtilValidate.isNotEmpty(companyBankAccountList)){
							companyBankAccountList.each { bankAccount ->
								finAccountCode = bankAccount.get("finAccountCode");
								benefitMap.put("finAccountCode",finAccountCode);
							}
						}
					}
				}
				benefitMap.put("pay",salary);
				partyBenefitMap.put(employee,benefitMap);
			}
		}
	}
}


context.put("partyBenefitMap",partyBenefitMap);




