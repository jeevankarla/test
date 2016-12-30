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
glAccountCategoryTypeId = parameters.glAccountCategoryTypeId;
customTimePeriodIds.add(customTimePeriodId);
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
partyIds=[];
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
childWiseMap=[:];
GrandProfitTotal=[:];
GrandLossTotal=[:];
GrandProfitTotal=[:];
GrandLossTotal=[:];
profitChildWiseMap=[:];
lossChildWiseMap=[:];
GlAccountIdsWiseMap=[:];
for(a=0; a<customTimePeriodIds.size(); a++){
	eachCustmTime = customTimePeriodIds.get(a);
	 grandTotal=0;
	 profitGrandTotal=0;
	 lossGrandTotal=0;
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
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO,eachPeriodThruDateTime) , EntityOperator.OR ,EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null) ));
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

if(UtilValidate.isNotEmpty(glAccountCategoryDetails)){
	glAccountCategoryIds = EntityUtil.getFieldListFromEntityList(glAccountCategoryDetails,"glAccountCategoryId",true);
	for(i=0; i<glAccountCategoryIds.size(); i++){
		childCatDebitTotal=0;
		childCatCreditTotal=0;
		childCurrentYearBal=0;
		glAccntCategoryId = glAccountCategoryIds.get(i);
		glAccountChildCategory = EntityUtil.getFirst(EntityUtil.filterByCondition(glAccountCategoryDetails, EntityCondition.makeCondition("glAccountCategoryId", EntityOperator.EQUALS, glAccntCategoryId)));
		glAccountCategoryMember = EntityUtil.filterByCondition(glAccountCategoryMemberDetails, EntityCondition.makeCondition("glAccountCategoryId", EntityOperator.EQUALS,glAccntCategoryId));
		glAccountIds = EntityUtil.getFieldListFromEntityList(glAccountCategoryMember,"glAccountId", true);
		glAccountIdsMap=[:];
		for(k=0; k<glAccountIds.size(); k++){
			glAccountId = glAccountIds.get(k);
			condList = [];
			condList.add(EntityCondition.makeCondition("glAccountId" , EntityOperator.EQUALS,glAccountId));
			condList.add(EntityCondition.makeCondition("customTimePeriodId" , EntityOperator.EQUALS,eachCustmTime));
			condList.add(EntityCondition.makeCondition("organizationPartyId" , EntityOperator.IN, partyIds));
			tempGlAccountAndHistories = delegator.findList("GlAccountAndHistoryTotals", EntityCondition.makeCondition(condList,EntityOperator.AND), UtilMisc.toSet("totalPostedDebits","glAccountId","totalPostedCredits","description"), null, null, false);
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
				glTempVal=0
				glTempVal=totalPostedDebits-totalPostedCredits;
				if(glTempVal<0){
					glTempVal=glTempVal.multiply(-1);
				}
				if(GlAccountIdsWiseMap && GlAccountIdsWiseMap.get(glAccntCategoryId)){
					tempGlMap=GlAccountIdsWiseMap.get(glAccntCategoryId);
					if( tempGlMap && tempGlMap.get(glAccountId)){
						tempExGlMap = tempGlMap.get(glAccountId);
						tempExGlMap.put(eachCustmTime,glTempVal);
						glAccountIdsMap.put(glAccountId,tempExGlMap);
					}else{
						tempMap=[:];
						tempMap.put("glAccountId",glAccountId);
						tempMap.put("description",tempGlAccountAndHistories.description);
						tempMap.put(eachCustmTime,glTempVal);
						glAccountIdsMap.put(glAccountId,tempMap);
					}
					GlAccountIdsWiseMap.put(glAccntCategoryId,glAccountIdsMap);
				}else{
					tempMap=[:];
					tempMap.put("glAccountId",glAccountId);
					tempMap.put("description",tempGlAccountAndHistories.description);
					tempMap.put(eachCustmTime,glTempVal);
					glAccountIdsMap.put(glAccountId,tempMap);
					GlAccountIdsWiseMap.put(glAccntCategoryId,glAccountIdsMap);
				}
			}
			
		}
		childCatName=glAccountChildCategory.get("description");
		scheduleNo = "";
		if(UtilValidate.isNotEmpty(glAccountChildCategory.get("categoryNarration"))){
			scheduleNo = glAccountChildCategory.get("categoryNarration");
		}
		childCurrentYearBal= childCatDebitTotal-childCatCreditTotal;
		grandTotal = grandTotal+childCurrentYearBal;
		if(childCurrentYearBal>0){
			profitGrandTotal=profitGrandTotal+childCurrentYearBal;
			if(profitChildWiseMap && profitChildWiseMap.get(glAccntCategoryId)){
				existMap=profitChildWiseMap.get(glAccntCategoryId);
				existMap.put("childCurrentYearBal",childCurrentYearBal);
				existMap.put("childCurrentPostedCredit",childCatCreditTotal);
				existMap.put("childCurrentPostedDebit",childCatDebitTotal);
				existMap.put(eachCustmTime,childCurrentYearBal);
				profitChildWiseMap.put(glAccntCategoryId, existMap);
			}else{
				tempMap=[:];
				tempMap.put("childCatName",childCatName);
				tempMap.put("scheduleNo",scheduleNo);
				tempMap.put("childCurrentYearBal",childCurrentYearBal);
				tempMap.put("childCurrentPostedCredit",childCatCreditTotal);
				tempMap.put("childCurrentPostedDebit",childCatDebitTotal);
				tempMap.put(eachCustmTime,childCurrentYearBal);
				profitChildWiseMap.put(glAccntCategoryId, tempMap);
			}
		}else if(childCurrentYearBal<0){
			childCurrentYearBal=childCurrentYearBal.multiply(-1);
			lossGrandTotal=lossGrandTotal+childCurrentYearBal;
			if(lossChildWiseMap && lossChildWiseMap.get(glAccntCategoryId)){
				existMap=lossChildWiseMap.get(glAccntCategoryId);
				existMap.put("childCurrentYearBal",childCurrentYearBal);
				existMap.put("childCurrentPostedCredit",childCatCreditTotal);
				existMap.put("childCurrentPostedDebit",childCatDebitTotal);
				existMap.put(eachCustmTime,childCurrentYearBal);
				lossChildWiseMap.put(glAccntCategoryId, existMap);
			}else{
				tempMap=[:];
				tempMap.put("childCatName",childCatName);
				tempMap.put("scheduleNo",scheduleNo);
				tempMap.put("childCurrentYearBal",childCurrentYearBal);
				tempMap.put("childCurrentPostedCredit",childCatCreditTotal);
				tempMap.put("childCurrentPostedDebit",childCatDebitTotal);
				tempMap.put(eachCustmTime,childCurrentYearBal);
				lossChildWiseMap.put(glAccntCategoryId, tempMap);
			}
		}
	}
}

GrandTotal.put(eachCustmTime,grandTotal);
GrandProfitTotal.put(eachCustmTime,profitGrandTotal);
GrandLossTotal.put(eachCustmTime,lossGrandTotal);
}
context.GlAccountIdsWiseMap=GlAccountIdsWiseMap;
context.profitChildWiseMap=profitChildWiseMap;
context.lossChildWiseMap=lossChildWiseMap;
context.GrandProfitTotal=GrandProfitTotal;
context.GrandLossTotal=GrandLossTotal;


																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																														












