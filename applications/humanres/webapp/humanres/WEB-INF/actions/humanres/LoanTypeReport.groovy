import org.apache.avalon.framework.parameters.Parameters;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.party.party.PartyHelper;

dctx = dispatcher.getDispatchContext();
loanTypeId = parameters.loanTypeId;
fromDateStart=null;
thruDateEnd=null;
context.putAt("loanTypeId", loanTypeId);
customTimePeriodId=parameters.customTimePeriodId;
List condList =[];
condList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS ,"HR_MONTH"));
condList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
List<GenericValue> customTimePeriodList = delegator.findList("CustomTimePeriod", cond, null, null, null, false);
if(UtilValidate.isNotEmpty(customTimePeriodList)){
	GenericValue customTimePeriod = EntityUtil.getFirst(customTimePeriodList);
	 fromDate=UtilDateTime.toTimestamp(customTimePeriod.get("fromDate"));
	 thruDate=UtilDateTime.toTimestamp(customTimePeriod.get("thruDate"));
		if (fromDate) {
			fromDateStart = UtilDateTime.getDayStart(fromDate);
		}
		if (thruDate) {
			thruDateEnd = UtilDateTime.getDayEnd(thruDate);
		}
}
employmentsList = [];
emplInputMap = [:];
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("orgPartyId", "Company");
emplInputMap.put("fromDate", fromDateStart);
emplInputMap.put("thruDate", thruDateEnd);
context.putAt("fromDate", fromDate);
context.putAt("thruDate", thruDate);

Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
employments=EmploymentsMap.get("employementList");
if(UtilValidate.isNotEmpty(employments)){
	employmentsList = EntityUtil.getFieldListFromEntityList(employments, "partyIdTo", true);
}
List loanTypeList=[];
if(UtilValidate.isNotEmpty(employmentsList)){
	employmentsList.each{ employeeId->
		List conditionList=[];
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
		conditionList.add(EntityCondition.makeCondition("loanTypeId", EntityOperator.EQUALS, loanTypeId));
		conditionList.add(EntityCondition.makeCondition("disbDate", EntityOperator.GREATER_THAN_EQUAL_TO ,fromDateStart));
		conditionList.add(EntityCondition.makeCondition("disbDate", EntityOperator.LESS_THAN_EQUAL_TO ,thruDateEnd));
		condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		loanAndTypeList = delegator.findList("LoanAndType", condition , null, null, null, false );
		if(UtilValidate.isNotEmpty(loanAndTypeList)){
			loanAndTypeList.each { loanAndType ->
				if(UtilValidate.isNotEmpty(loanAndType)){
					loanId = null;
					employeeId = null;
					principalAmount = BigDecimal.ZERO;
					interestAmount = BigDecimal.ZERO;
					numInterestInst = BigDecimal.ZERO;
					numPrincipalInst = BigDecimal.ZERO;
					prinAmtEmi = BigDecimal.ZERO;
					intrstAmtEmi = BigDecimal.ZERO;
					disbDate = null;
					employeeName = null;
					
					totalRecPrinAmount = BigDecimal.ZERO;
					totalRecPrinInst = BigDecimal.ZERO;
					
					totalRecIntAmount = BigDecimal.ZERO;
					totalRecIntInst = BigDecimal.ZERO;
					
					netPrinAmount = BigDecimal.ZERO;
					netPrinInst = BigDecimal.ZERO;
					netIntAmount = BigDecimal.ZERO;
					netIntInst = BigDecimal.ZERO;
					
					loanId = loanAndType.loanId;
					employeeId = loanAndType.partyId;
					if(UtilValidate.isNotEmpty(employeeId)){
						employeeName =  PartyHelper.getPartyName(delegator, employeeId, false);
					}
					disbDate = loanAndType.disbDate;
					principalAmount = loanAndType.principalAmount;
					interestAmount = loanAndType.interestAmount;
					numInterestInst = loanAndType.numInterestInst;
					numPrincipalInst = loanAndType.numPrincipalInst;
					
					if(UtilValidate.isNotEmpty(numPrincipalInst) && numPrincipalInst!=0){
						prinAmtEmi = (principalAmount/numPrincipalInst);
					}
					if(UtilValidate.isNotEmpty(numInterestInst) && numInterestInst!=0){
						intrstAmtEmi = (interestAmount/numInterestInst);
					}
					if(UtilValidate.isNotEmpty(loanId)){
						loanRecoveryList = delegator.findList("LoanRecovery",EntityCondition.makeCondition("loanId", EntityOperator.EQUALS , loanId)  , null, null, null, false );
						if(UtilValidate.isNotEmpty(loanRecoveryList)){
							loanRecoveryList.each { loanRecovery->
								if(UtilValidate.isNotEmpty(loanRecovery)){
									recPrinAmount = loanRecovery.principalAmount;
									recPrinInst = loanRecovery.principalInstNum;
									
									recIntAmount = loanRecovery.interestAmount;
									recIntInst = loanRecovery.interestInstNum;
									
									totalRecPrinAmount = totalRecPrinAmount+recPrinAmount;
									totalRecPrinInst =  totalRecPrinInst+recPrinInst;
									
									totalRecIntAmount = totalRecIntAmount+recIntAmount;
									totalRecIntInst =  totalRecIntInst+recIntInst;
								}
								
							}
						}
					}
					netPrinAmount = principalAmount - totalRecPrinAmount;
					netPrinInst = numPrincipalInst - totalRecPrinInst;
					netIntAmount = interestAmount - totalRecIntAmount;
					netIntInst = numInterestInst - totalRecIntInst;
					
					loanTypeMap = [:];
					loanTypeMap["loanId"] = loanId;
					loanTypeMap["employeeId"] = employeeId;
					loanTypeMap["employeeName"] = employeeName;
					loanTypeMap["loanTypeId"] = loanTypeId;
					loanTypeMap["disbDate"] = disbDate;
					loanTypeMap["principalAmount"] = principalAmount;
					loanTypeMap["interestAmount"] = interestAmount;
					loanTypeMap["numInterestInst"] = numInterestInst;
					loanTypeMap["numPrincipalInst"] = numPrincipalInst;
					loanTypeMap["prinAmtEmi"] = prinAmtEmi.setScale(2,BigDecimal.ROUND_HALF_UP);
					loanTypeMap["intrstAmtEmi"] = intrstAmtEmi.setScale(2,BigDecimal.ROUND_HALF_UP);
					
					loanTypeMap["totalRecPrinAmount"] = totalRecPrinAmount;
					loanTypeMap["totalRecIntAmount"] = totalRecIntAmount;
					loanTypeMap["netPrinAmount"] = netPrinAmount;
					loanTypeMap["netIntAmount"] = netIntAmount;
					loanTypeMap["netPrinInst"] = netPrinInst;
					loanTypeMap["netIntInst"] = netIntInst;
					if(UtilValidate.isNotEmpty(loanTypeMap)){
						tempMap = [:];
						tempMap.putAll(loanTypeMap);
						if(UtilValidate.isNotEmpty(tempMap)){
							loanTypeList.addAll(tempMap);
						}
					}
				}
			}
		}
	}
}
context.putAt("loanTypeList", loanTypeList);



