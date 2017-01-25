import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.Delegator;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;
import javolution.util.FastList;
import javolution.util.FastSet;

import org.ofbiz.accounting.util.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.plaf.basic.BasicInternalFrameUI.GlassPaneDispatcher;
customTimePeriodIds = [];
parentCategoryIds = [];
glAccountCategoryIds =[];
conditionList = [];
customTimePeriodId= parameters.customTimePeriodId;
presentYear=customTimePeriodId;
context.presentYear=presentYear;
customTimePeriodIds.add(customTimePeriodId);
glAccountCategoryTypeId = parameters.glAccountCategoryTypeId;
reportTypeFlag = parameters.reportTypeFlag;
GrandTotal=[:];

fromDate = "";
thruDate = "";
customTimePeriods = delegator.findOne("CustomTimePeriod", [customTimePeriodId : customTimePeriodId], false);
if(UtilValidate.isNotEmpty(customTimePeriods)){
	fromDate =(String)customTimePeriods.fromDate;
	thruDate= (String)customTimePeriods.thruDate;
}
context.fromDate= fromDate;
context.thruDate=thruDate;
Timestamp fromDateTime = null;
Timestamp thruDateTime = null;
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
try {
	fromDateTime = new java.sql.Timestamp(sdf.parse(fromDate).getTime());
	thruDateTime = new java.sql.Timestamp(sdf.parse(thruDate).getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+fromDate, "");
}
fromDateTime = UtilDateTime.getDayStart(fromDateTime);
thruDateTime = UtilDateTime.getDayEnd(thruDateTime);
context.fromDateTime=fromDateTime;
context.thruDateTime=thruDateTime;
Timestamp prevoiusYearMonth = UtilDateTime.addDaysToTimestamp(fromDateTime,-1);
java.sql.Date previousDate = new java.sql.Date(prevoiusYearMonth.getTime());

conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS,previousDate));
conditionList.add(EntityCondition.makeCondition("periodTypeId",EntityOperator.EQUALS, "FISCAL_YEAR"));
previousCustomTimePeriod = delegator.findList("CustomTimePeriod",EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, null, null, false );
previousCustomTimePeriod = EntityUtil.getFirst(previousCustomTimePeriod);
customTimePeriodId= previousCustomTimePeriod.customTimePeriodId;
context.prevFromDate=previousCustomTimePeriod.fromDate;
context.prevThruDate=previousCustomTimePeriod.thruDate;
prevsYear=customTimePeriodId;
context.prevsYear=customTimePeriodId;
customTimePeriodIds.add(customTimePeriodId);
if(!(parameters.organizationPartyId).equalsIgnoreCase("Company")){
	partyIds.clear();
	partyIds.add(parameters.organizationPartyId);
}else{
	conList=[];
	conList.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.EQUALS, "Company"));
	conList.add(EntityCondition.makeCondition("roleTypeIdFrom",EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
	conList.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS,"GROUP_ROLLUP"));
	baseExprs = EntityCondition.makeCondition(conList,EntityOperator.AND);
	List partyRelationShipIds = delegator.findList("PartyRelationship",baseExprs,null,null,null,false);
	if(UtilValidate.isNotEmpty(partyRelationShipIds)){
		partyIds = EntityUtil.getFieldListFromEntityList(partyRelationShipIds,"partyIdTo",true);
	}

}
finalScheduleWiseMap = [:];
for(a=0; a<customTimePeriodIds.size(); a++){
	eachCustmTime = customTimePeriodIds.get(a);
	 grandTotal=0;
	eachPeriodFromDate = "";
	eachPeriodThruDate = "";
	eachCustomTimePeriods = delegator.findOne("CustomTimePeriod", [customTimePeriodId : eachCustmTime], false);
	if(UtilValidate.isNotEmpty(	eachCustomTimePeriods)){
		eachPeriodFromDate =(String)	eachCustomTimePeriods.fromDate;
		eachPeriodThruDate= (String)	eachCustomTimePeriods.thruDate;
	}
	Timestamp eachPeriodFromDateTime = null;
	Timestamp eachPeriodThruDateTime = null;
	try {
		eachPeriodFromDateTime = new java.sql.Timestamp(sdf.parse(eachPeriodFromDate).getTime());
		eachPeriodThruDateTime = new java.sql.Timestamp(sdf.parse(eachPeriodThruDate).getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: "+fromDate, "");
	}
	eachPeriodFromDateTime = UtilDateTime.getDayStart(eachPeriodFromDateTime);
	eachPeriodThruDateTime = UtilDateTime.getDayEnd(eachPeriodThruDateTime);
	
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO,eachPeriodFromDateTime));
//conditionList.add(EntityCondition.makeCondition("thruDate",EntityOperator.LESS_THAN_EQUAL_TO, eachPeriodThruDateTime));
//conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO,eachPeriodThruDateTime) , EntityOperator.OR ,EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null) ));
if(UtilValidate.isNotEmpty(parameters.organizationPartyId) && "Company".equalsIgnoreCase(parameters.organizationPartyId)){
	//conditionList.add(EntityCondition.makeCondition("organizationPartyId",EntityOperator.IN, partyIds);
}else{
	conditionList.add(EntityCondition.makeCondition("organizationPartyId",EntityOperator.EQUALS, parameters.organizationPartyId));
}
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
glAccountCategoryMemberDetails = delegator.findList("GlAccountCategoryMember",condition,UtilMisc.toSet("glAccountCategoryId","glAccountId"), null, null, false );
glAccountCategoryIds=EntityUtil.getFieldListFromEntityList(glAccountCategoryMemberDetails, "glAccountCategoryId", true);
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("glAccountCategoryId", EntityOperator.IN, glAccountCategoryIds));
conditionList.add(EntityCondition.makeCondition("glAccountCategoryTypeId",EntityOperator.EQUALS, glAccountCategoryTypeId));
condition1=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
glAccountCategoryDetails = delegator.findList("GlAccountCategory",condition1,null, ["categoryNarration"], null, false );
parentCategoryIds = EntityUtil.getFieldListFromEntityList(glAccountCategoryDetails,"parentCategoryId", true);
if(UtilValidate.isEmpty(parentCategoryIds)){
	parentCategoryIds = EntityUtil.getFieldListFromEntityList(glAccountCategoryDetails,"glAccountCategoryId", true);
}
glAccountWiseMap=[:];
parentScheduleMap = [:];
parentChildMap=[:];
ParentsWiseMap=[:];
for(i=0; i<parentCategoryIds.size(); i++){
	childWiseMap=[:];
	parentCategoryId = parentCategoryIds.get(i);
	parentCatDebitTotal=0;
	parentCatCreditTotal=0;
	parentCurrentYearBal=0;
	glAccountParentCategory = EntityUtil.filterByCondition(glAccountCategoryDetails, EntityCondition.makeCondition("parentCategoryId", EntityOperator.EQUALS, parentCategoryId));
	if(UtilValidate.isEmpty(glAccountParentCategory)){
		glAccountParentCategory = EntityUtil.filterByCondition(glAccountCategoryDetails, EntityCondition.makeCondition("glAccountCategoryId", EntityOperator.EQUALS, parentCategoryId));
	}
	parentCategories = EntityUtil.getFirst(glAccountParentCategory);
	childCategoryIds = EntityUtil.getFieldListFromEntityList(glAccountParentCategory,"glAccountCategoryId", true);
	for(j=0; j<childCategoryIds.size(); j++){
		
		childCategoryId = childCategoryIds.get(j);
		childCatDebitTotal=0;
		childCatCreditTotal=0;
		childCurrentYearBal=0;
		childCatMap = [:];
		glAccountChildCategory = EntityUtil.filterByCondition(glAccountCategoryDetails, EntityCondition.makeCondition("glAccountCategoryId", EntityOperator.EQUALS, childCategoryId));
		childCategories = EntityUtil.getFirst(glAccountChildCategory);
		actualGlAccountCategoryIds = EntityUtil.getFieldListFromEntityList(glAccountChildCategory,"glAccountCategoryId", true);
		glAccountCategoryMember = EntityUtil.filterByCondition(glAccountCategoryMemberDetails, EntityCondition.makeCondition("glAccountCategoryId", EntityOperator.IN,actualGlAccountCategoryIds));
		glAccountIds = EntityUtil.getFieldListFromEntityList(glAccountCategoryMember,"glAccountId", true);
		for(k=0; k<glAccountIds.size(); k++){
			
			glAccountId = glAccountIds.get(k);
			condList = [];			
			condList.add(EntityCondition.makeCondition("glAccountId" , EntityOperator.EQUALS,glAccountId));
			condList.add(EntityCondition.makeCondition("customTimePeriodId" , EntityOperator.EQUALS,eachCustmTime));
			condList.add(EntityCondition.makeCondition("organizationPartyId" , EntityOperator.IN, partyIds));
			tempGlAccountAndHistories = delegator.findList("GlAccountAndHistoryTotals", EntityCondition.makeCondition(condList,EntityOperator.AND), UtilMisc.toSet("totalPostedDebits","glAccountId","totalPostedCredits"), null, null, false);
			if(UtilValidate.isNotEmpty(tempGlAccountAndHistories)){
			    tempGlAccountAndHistories = EntityUtil.getFirst(tempGlAccountAndHistories);
			    totalPostedDebits= tempGlAccountAndHistories.totalPostedDebits;
				if(UtilValidate.isNotEmpty(totalPostedDebits)){
					childCatDebitTotal=childCatDebitTotal+totalPostedDebits;
				}
				totalPostedCredits = tempGlAccountAndHistories.totalPostedCredits;
				if(UtilValidate.isNotEmpty(totalPostedCredits)){
					childCatCreditTotal=childCatCreditTotal+totalPostedCredits;		
				}		
			}
			
		}
		childCatName=childCategories.get("description");
		childCurrentYearBal= childCatDebitTotal-childCatCreditTotal;
		
		parentCatDebitTotal= parentCatDebitTotal+childCatDebitTotal;
		parentCatCreditTotal= parentCatCreditTotal+childCatCreditTotal;
		parentCurrentYearBal= parentCurrentYearBal+childCurrentYearBal;
		if(childWiseMap && childWiseMap.get(childCategoryId)){
			existMap=childWiseMap.get(childCategoryId);
			if(childCurrentYearBal<0){
				childCurrentYearBal=childCurrentYearBal.multiply(-1);
			}
			existMap.put("childCurrentYearBal",childCurrentYearBal);
			existMap.put("childCurrentPostedCredit",childCatCreditTotal);
			existMap.put("childCurrentPostedDebit",childCatDebitTotal);
			childWiseMap.put(childCategoryId, existMap);
		}else{
			tempMap=[:];
			tempMap.put("childCatName",childCatName);
			if(childCurrentYearBal<0){
				childCurrentYearBal=childCurrentYearBal.multiply(-1);
			}
			tempMap.put("childCurrentYearBal",childCurrentYearBal);
			tempMap.put("childCurrentPostedCredit",childCatCreditTotal);
			tempMap.put("childCurrentPostedDebit",childCatDebitTotal);
			childWiseMap.put(childCategoryId, tempMap);
		}
	}
	parentCatName=parentCategories.get("description");
	
	parentDetaiMap=[:];
	parentDetaiMap.put("parentCategoryId", parentCategoryId);
	parentDetaiMap.put("parentCatName", parentCatName);
	grandTotal=grandTotal+parentCurrentYearBal;
	if(ParentsWiseMap && ParentsWiseMap.get(parentCategoryId)){
		existMap=childWiseMap.get(parentCategoryId);
		if(parentCurrentYearBal<0){
			parentCurrentYearBal=parentCurrentYearBal.multiply(-1);
		}
		existMap.put("parentCurrentYearBal",parentCurrentYearBal);
		existMap.put("parentCurrentYearPostedDebits",parentCatDebitTotal);
		existMap.put("parentCurrentYearPostedCredits",parentCatCreditTotal);
		ParentsWiseMap.put(parentCategoryId, existMap);
	}else{
		tempMap=[:];
		tempMap.put("parentCatName",parentCatName);
		tempMap.put("childDetails", childWiseMap);
		if(parentCurrentYearBal<0){
			parentCurrentYearBal=parentCurrentYearBal.multiply(-1);
		}
		tempMap.put("parentCurrentYearBal",parentCurrentYearBal);
		tempMap.put("parentCurrentYearPostedDebits",parentCatDebitTotal);
		tempMap.put("parentCurrentYearPostedCredits",parentCatCreditTotal);
		ParentsWiseMap.put(parentCategoryId, tempMap);
	}
}
finalScheduleWiseMap.put(eachCustmTime, ParentsWiseMap);
if(grandTotal<0)
{
	grandTotal=grandTotal.multiply(-1);
}
GrandTotal.put(eachCustmTime,grandTotal);
}
AssertsList=["SCH 1","SCH 9","SCH 10","SCH 11","SCH 12","SCH 13","SCH 14","SCH 30"];
liablityList=["SCH 4","SCH 5","SCH 6","SCH 7","SCH 8","SCH 31","SCH 34","SCH 3"];
context.liablityList=liablityList;
context.AssertsList=AssertsList;
context.finalScheduleWiseMap=finalScheduleWiseMap;
context.grandTotal=GrandTotal;
