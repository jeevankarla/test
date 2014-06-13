import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
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
import org.ofbiz.base.util.UtilNumber;

GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
context.timePeriodStart= UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
context.timePeriodEnd= UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
conditionList=[];
conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS ,"PAYROLL_BILL"));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS ,"GENERATED"));
conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,parameters.customTimePeriodId));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
periodBillingList = delegator.findList("PeriodBilling", condition, null, null, null, false);

//getting benefits
benefitTypeList = delegator.findList("BenefitType", null, null, null, null, false);
benefitDescMap=[:];
if(UtilValidate.isNotEmpty(benefitTypeList)){
	benefitTypeList.each{ benefit->
		benefitName =  benefit.get("benefitName");
		benefitType = benefit.get("benefitTypeId");
		benefitDescMap.put(benefitType,benefitName);
	}
}
benefitTypeIds = EntityUtil.getFieldListFromEntityList(benefitTypeList, "benefitTypeId", true);
context.benefitTypeIds=benefitTypeIds;
context.benefitDescMap=benefitDescMap;
//getting deductions

deductionTypeList = delegator.findList("DeductionType", null, null, null, null, false);
dedDescMap=[:];
if(UtilValidate.isNotEmpty(deductionTypeList)){
	deductionTypeList.each{ deduction->
		dedName =  deduction.get("deductionName");
		dedType = deduction.get("deductionTypeId");
		dedDescMap.put(dedType,dedName);
	}
}
dedTypeIds = EntityUtil.getFieldListFromEntityList(deductionTypeList, "deductionTypeId", true);
context.dedTypeIds=dedTypeIds;
context.dedDescMap=dedDescMap;
payRollMap=FastMap.newInstance();
if(UtilValidate.isNotEmpty(periodBillingList)){
	periodBillDetails = EntityUtil.getFirst(periodBillingList);
	periodBillingId = periodBillDetails.get("periodBillingId");
	payConList=[];
	payConList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS ,periodBillingId));
	payConList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS ,parameters.partyId));
	payCond = EntityCondition.makeCondition(payConList,EntityOperator.AND);
	payRollHeaderList = delegator.findList("PayrollHeader", payCond, null, null, null, false);
	
	if(UtilValidate.isNotEmpty(payRollHeaderList)){
		payRollHeaderList.each{ payRollHead->
			payrollHeaderId=payRollHead.get("payrollHeaderId");
			partyId=payRollHead.get("partyIdFrom");
			payRollItemsMap=[:];
			itemConList=[];
			itemConList.add(EntityCondition.makeCondition("payrollHeaderId", EntityOperator.EQUALS ,payrollHeaderId));
			itemCond = EntityCondition.makeCondition(itemConList,EntityOperator.AND);
			payRollHeaderItemsList = delegator.findList("PayrollHeaderItem", itemCond, null, null, null, false);
			if(UtilValidate.isNotEmpty(payRollHeaderItemsList)){
				tempAmount =0;
				payRollHeaderItemsList.each{ payRollHeaderItem->
					payrollHeaderItemTypeId=payRollHeaderItem.get("payrollHeaderItemTypeId");
					amount=payRollHeaderItem.get("amount");
					if(amount >0){
						tempAmount +=amount;
					}
					
					if(UtilValidate.isEmpty(payRollItemsMap.get(payrollHeaderItemTypeId))){
						payRollItemsMap[payrollHeaderItemTypeId]=amount;
					}else{
						payRollItemsMap[payrollHeaderItemTypeId]+=amount;
					}
				}
				if(UtilValidate.isNotEmpty(payRollItemsMap) || tempAmount !=0){
					payRollMap.put(payrollHeaderId,payRollItemsMap);
				}
			}
		}
	}
	
}
context.put("payRollMap",payRollMap);