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
import org.ofbiz.accounting.util.*;

dctx = dispatcher.getDispatchContext();
customTimePeriodIds = [];
parentCategoryIds = [];
glAccountCategoryIds =[];
conditionList = [];
customTimePeriodId= parameters.customTimePeriodId;
presentYear=customTimePeriodId;
context.presentYear=presentYear;
glAccountCategoryTypeId = parameters.glAccountCategoryTypeId;
customTimePeriodIds.add(customTimePeriodId);
assetGlAccountClsIds=[];
liabilityGlAccountClsIds=[];
assetCategoryList=[];
liabilityCategoryList=[];
assetGlAccountCls = delegator.findList("GlAccountClass",EntityCondition.makeCondition("parentClassId",EntityOperator.EQUALS,"ASSET"),null,null,null,false);
assetGlAccountClsIds = EntityUtil.getFieldListFromEntityList(assetGlAccountCls,"glAccountClassId",true);
assetGlAccountCls = delegator.findList("GlAccountClass",EntityCondition.makeCondition("parentClassId",EntityOperator.IN,assetGlAccountClsIds),null,null,null,false);
assetGlAccountClsIds.addAll(EntityUtil.getFieldListFromEntityList(assetGlAccountCls,"glAccountClassId",true));
liabilityGlAccountCls = delegator.findList("GlAccountClass",EntityCondition.makeCondition("parentClassId",EntityOperator.IN,UtilMisc.toList("LIABILITY","EQUITY")),null,null,null,false);
liabilityGlAccountClsIds = EntityUtil.getFieldListFromEntityList(liabilityGlAccountCls,"glAccountClassId",true);
liabilityGlAccountCls = delegator.findList("GlAccountClass",EntityCondition.makeCondition("parentClassId",EntityOperator.IN,liabilityGlAccountClsIds),null,null,null,false);
liabilityGlAccountClsIds.addAll(EntityUtil.getFieldListFromEntityList(liabilityGlAccountCls,"glAccountClassId",true));
//Debug.log("liabilityGlAccountClsIds############"+liabilityGlAccountClsIds);
//Debug.log("assetGlAccountClsIds############"+assetGlAccountClsIds);
prevTimePeriodMap=[:];
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
if(!(parameters.organizationPartyId).equalsIgnoreCase("Company")){
conditionList.add(EntityCondition.makeCondition("organizationPartyId",EntityOperator.EQUALS,parameters.organizationPartyId));
}else{
conditionList.add(EntityCondition.makeCondition("organizationPartyId",EntityOperator.EQUALS, "Company"));
}
previousCustomTimePeriod = delegator.findList("CustomTimePeriod",EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, null, null, false );
previousCustomTimePeriod = EntityUtil.getFirst(previousCustomTimePeriod);
prevTimePeriodMap.put(customTimePeriodId, previousCustomTimePeriod.customTimePeriodId);
customTimePeriodId= previousCustomTimePeriod.customTimePeriodId;
context.prevFromDate=previousCustomTimePeriod.fromDate;
context.prevThruDate=previousCustomTimePeriod.thruDate;
prevsYear=customTimePeriodId;
context.prevsYear=customTimePeriodId;
customTimePeriodIds.add(customTimePeriodId);
//getting prevPrevTimeperiod
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO,previousCustomTimePeriod.fromDate));
conditionList.add(EntityCondition.makeCondition("periodTypeId",EntityOperator.EQUALS, "FISCAL_YEAR"));
if(!(parameters.organizationPartyId).equalsIgnoreCase("Company")){
conditionList.add(EntityCondition.makeCondition("organizationPartyId",EntityOperator.EQUALS,parameters.organizationPartyId));
}else{
conditionList.add(EntityCondition.makeCondition("organizationPartyId",EntityOperator.EQUALS, "Company"));
}
prevPreviousCustomTimePeriod = delegator.findList("CustomTimePeriod",EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, null, null, false );
if(UtilValidate.isNotEmpty(prevPreviousCustomTimePeriod)){
	prevPreviousCustomTimePeriod = EntityUtil.getFirst(prevPreviousCustomTimePeriod);
	prevTimePeriodMap.put(customTimePeriodId, prevPreviousCustomTimePeriod.customTimePeriodId);
}else{
	prevTimePeriodMap.put(customTimePeriodId, "");
}
segmentList=[];

if(!(parameters.segmentId).equalsIgnoreCase("ALL")){
	segmentList.add(parameters.segmentId);
	if(parameters.segmentId=="YARN_SALE"){
		segmentList.add("DEPOT_YARN_SALE");
	}
	if(parameters.segmentId=="DIES_AND_CHEM_SALE"){
		segmentList.add("DEPOT_DIES_CHEM_SALE");
	}
}else{
	segmentList.add("YARN_SALE");
	segmentList.add("DEPOT_YARN_SALE");
	segmentList.add("DIES_AND_CHEM_SALE");
	segmentList.add("DEPOT_DIES_CHEM_SALE");
	segmentList.add("COMMON");
}
partyIds=[];
if(UtilValidate.isNotEmpty(parameters.roPartyId)){
	parameters.organizationPartyId=parameters.roPartyId;
}
if(!(parameters.organizationPartyId).equalsIgnoreCase("Company")){
	partyIds.clear();
	conList=[];
	conList.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.EQUALS, parameters.organizationPartyId));
	conList.add(EntityCondition.makeCondition("roleTypeIdFrom",EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
	conList.add(EntityCondition.makeCondition("roleTypeIdTo",EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
	conList.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS,"BRANCH_CUSTOMER"));
	baseExprs = EntityCondition.makeCondition(conList,EntityOperator.AND);
	List partyRelationShipIds = delegator.findList("PartyRelationship",baseExprs,null,null,null,false);
	if(UtilValidate.isNotEmpty(partyRelationShipIds)){
		partyIds = EntityUtil.getFieldListFromEntityList(partyRelationShipIds,"partyIdTo",true);
	}
	partyIds.add(parameters.organizationPartyId);
}else{
	conList=[];
	conList.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.EQUALS, parameters.organizationPartyId));
	conList.add(EntityCondition.makeCondition("roleTypeIdFrom",EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
	conList.add(EntityCondition.makeCondition("roleTypeIdTo",EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
	conList.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS,"GROUP_ROLLUP"));
	baseExprs = EntityCondition.makeCondition(conList,EntityOperator.AND);
	List partyRelationShipIds = delegator.findList("PartyRelationship",baseExprs,null,null,null,false);
	if(UtilValidate.isNotEmpty(partyRelationShipIds)){
		partyIds = EntityUtil.getFieldListFromEntityList(partyRelationShipIds,"partyIdTo",true);
	}
	conList=[];
	conList.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.IN, partyIds));
	conList.add(EntityCondition.makeCondition("roleTypeIdFrom",EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
	conList.add(EntityCondition.makeCondition("roleTypeIdTo",EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
	conList.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS,"BRANCH_CUSTOMER"));
	baseExprs1 = EntityCondition.makeCondition(conList,EntityOperator.AND);
	partyRelationShipIds.clear();
	partyRelationShipIds = delegator.findList("PartyRelationship",baseExprs1,null,null,null,false);
	if(UtilValidate.isNotEmpty(partyRelationShipIds)){
		partyIds.addAll(EntityUtil.getFieldListFromEntityList(partyRelationShipIds,"partyIdTo",true));
	}
	partyIds.add(parameters.organizationPartyId);

}
GrandLiablitesTotal=[:];
GrandassetsTotal=[:];
liablitiesChildWiseMap=[:];
assetsChildWiseMap=[:];
GlAccountIdsWiseMap=[:];

for(a=0; a<customTimePeriodIds.size(); a++){
	eachCustmTime = customTimePeriodIds.get(a);
	//Debug.log("eachCustmTime####"+eachCustmTime);
	 liablitiesGrandTotal=0;
	 assetsGrandTotal=0;
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
conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO,eachPeriodThruDateTime));
//conditionList.add(EntityCondition.makeCondition("thruDate",EntityOperator.LESS_THAN_EQUAL_TO, eachPeriodThruDateTime));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO,eachPeriodFromDateTime) , EntityOperator.OR ,EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null) ));
/*if(UtilValidate.isNotEmpty(parameters.organizationPartyId) && "Company".equalsIgnoreCase(parameters.organizationPartyId)){
	//conditionList.add(EntityCondition.makeCondition("organizationPartyId",EntityOperator.IN, partyIds);
}else{
	conditionList.add(EntityCondition.makeCondition("organizationPartyId",EntityOperator.EQUALS, parameters.organizationPartyId));
}*/
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
glAccountCategoryMemberDetails = delegator.findList("GlAccountCategoryMember",condition,UtilMisc.toSet("organizationPartyId","glAccountCategoryId","glAccountId"), null, null, false );
glAccountCategoryIds=EntityUtil.getFieldListFromEntityList(glAccountCategoryMemberDetails, "glAccountCategoryId", true);
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("glAccountCategoryId", EntityOperator.IN, glAccountCategoryIds));
conditionList.add(EntityCondition.makeCondition("glAccountCategoryTypeId",EntityOperator.EQUALS, glAccountCategoryTypeId));
condition1=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
glAccountCategoryDetails = delegator.findList("GlAccountCategory",condition1,null, ["seqId"], null, false );

if(UtilValidate.isNotEmpty(glAccountCategoryDetails)){
	glAccountCategoryDetails = EntityUtil.orderBy(glAccountCategoryDetails, ['seqId']);
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
		for(k=0; k<glAccountCategoryMember.size(); k++){
			//GenericValue glAccntCatMbr = (GenericValue)glAccountCategoryMember.get(k)
			glAccountId = glAccountCategoryMember.get(k).get("glAccountId");
			organizationPartyId = glAccountCategoryMember.get(k).get("organizationPartyId");
			//Debug.log("glAccountId####################"+glAccountId);
			//lastClosedGlBalances = UtilAccounting.getLastClosedGlBalance(dctx, UtilMisc.toMap("glAccountId",glAccountId,"organizationPartyId", organizationPartyId,"customTimePeriodId",eachCustmTime,"periodTypeId","FISCAL_YEAR"));
			
			//Map result=dispatcher.runSync("getGlAccountOpeningBalance", [glAccountId:glAccountId,fromDate:eachPeriodFromDateTime,userLogin:userLogin]);
			totalEndingBalance=0;
			//totalEndingBalance=result.get("openingBal")
			/*if(UtilValidate.isNotEmpty(lastClosedGlBalances)){
			lastClosedGlBalance = lastClosedGlBalances.get(0);
			if(UtilValidate.isNotEmpty(lastClosedGlBalance)){
				totalEndingBalance=lastClosedGlBalance.get("totalEndingBalance");
			}
			}*/
			
			condList = [];
			condList.add(EntityCondition.makeCondition("glAccountId" , EntityOperator.EQUALS,glAccountId));
			condList.add(EntityCondition.makeCondition("customTimePeriodId" , EntityOperator.EQUALS,eachCustmTime));
			condList.add(EntityCondition.makeCondition("costCenterId" , EntityOperator.IN, partyIds));
			condList.add(EntityCondition.makeCondition("segmentId" , EntityOperator.IN, segmentList));
			
			//Debug.log("##############################"+condList);
			tempGlAccountAndHistories=null;
			prevTempGlAccountAndHistories=null;
			tempGlAccountAndHistories = delegator.findList("GlAccountAndHistoryTotals", EntityCondition.makeCondition(condList,EntityOperator.AND), UtilMisc.toSet("totalPostedDebits","glAccountId","totalPostedCredits","description","organizationPartyId","glAccountClassId","costCenterId","segmentId"), null, null, false);
			condList.clear();
			if(UtilValidate.isNotEmpty(prevTimePeriodMap.get(eachCustmTime))){
				condList.add(EntityCondition.makeCondition("glAccountId" , EntityOperator.EQUALS,glAccountId));
				condList.add(EntityCondition.makeCondition("customTimePeriodId" , EntityOperator.EQUALS,prevTimePeriodMap.get(eachCustmTime)));
				condList.add(EntityCondition.makeCondition("costCenterId" , EntityOperator.IN, partyIds));
				condList.add(EntityCondition.makeCondition("segmentId" , EntityOperator.IN, segmentList));
				//condList.add(EntityCondition.makeCondition("organizationPartyId" , EntityOperator.EQUALS, tempGlAccountAndHistories.organizationPartyId));
				//Debug.log("##############################"+condList);
				prevTempGlAccountAndHistories = delegator.findList("GlAccountAndHistoryTotals", EntityCondition.makeCondition(condList,EntityOperator.AND), UtilMisc.toSet("totalPostedDebits","glAccountId","totalPostedCredits","description","glAccountClassId","organizationPartyId","costCenterId","segmentId"), null, null, false);
				
			}else{
				prevTempGlAccountAndHistories=[];
			}
			
			if(UtilValidate.isEmpty(tempGlAccountAndHistories) && UtilValidate.isNotEmpty(prevTempGlAccountAndHistories)){
				tempGlAccountAndHistories.addAll(prevTempGlAccountAndHistories);
				prevTempGlAccountAndHistories=[];
			}
			if(UtilValidate.isNotEmpty(tempGlAccountAndHistories) && UtilValidate.isNotEmpty(prevTempGlAccountAndHistories)){
				tempGlAccountAndHistories.addAll(prevTempGlAccountAndHistories);
				prevTempGlAccountAndHistories=[];
			}
			if(UtilValidate.isNotEmpty(tempGlAccountAndHistories)){
			for(int j=0;j<tempGlAccountAndHistories.size();j++){
				/*if(UtilValidate.isNotEmpty(parameters.organizationPartyId) && "Company".equalsIgnoreCase(parameters.organizationPartyId)){
					tempGlAccountAndHistories = EntityUtil.getFirst(EntityUtil.filterByCondition(tempGlAccountAndHistories,EntityCondition.makeCondition("costCenterId",EntityOperator.EQUALS,organizationPartyId)));
				}else{
					tempGlAccountAndHistories = EntityUtil.getFirst(tempGlAccountAndHistories);
				}*/
				tempGlAccountAndHistory=tempGlAccountAndHistories.get(j);
				
				if(UtilValidate.isNotEmpty(tempGlAccountAndHistory)){
					//Debug.log("assetCategoryList########"+tempGlAccountAndHistories.glAccountClassId);
					if(assetGlAccountClsIds.contains(tempGlAccountAndHistory.glAccountClassId)){
						if(!assetCategoryList.contains(glAccntCategoryId)){
							assetCategoryList.add(glAccntCategoryId);
							//Debug.log("assetCategoryList########"+assetCategoryList);
						}
					}else{
						if(!liabilityCategoryList.contains(glAccntCategoryId)){
							liabilityCategoryList.add(glAccntCategoryId);
							//Debug.log("liabilityCategoryList########"+liabilityCategoryList);
						}
					}
					
					
					totalPostedDebits= tempGlAccountAndHistory.totalPostedDebits;
					totalPostedCredits = tempGlAccountAndHistory.totalPostedCredits;
					/*if(UtilValidate.isNotEmpty(prevTempGlAccountAndHistories)){
						prevsCondList=[];
						prevsCondList.add(EntityCondition.makeCondition("costCenterId", EntityOperator.EQUALS, tempGlAccountAndHistory.costCenterId));
						prevsCondList.add(EntityCondition.makeCondition("segmentId", EntityOperator.EQUALS, tempGlAccountAndHistory.segmentId));
						prevsCondList.add(EntityCondition.makeCondition("glAccountId" , EntityOperator.EQUALS,glAccountId));
						prevsCondList.add(EntityCondition.makeCondition("customTimePeriodId" , EntityOperator.EQUALS,prevTimePeriodMap.get(eachCustmTime)));
						prevsCondList.add(EntityCondition.makeCondition("segmentId" , EntityOperator.IN, segmentList));
						//prevsCond = EntityCondition.makeCondition(prevsCondList,EntityOperator.AND);
						//condList.add(EntityCondition.makeCondition("organizationPartyId" , EntityOperator.EQUALS, tempGlAccountAndHistories.organizationPartyId));
						//Debug.log("##############################"+condList);
						prevTempGlAccountAndHistories = delegator.findList("GlAccountAndHistoryTotals", EntityCondition.makeCondition(prevsCondList,EntityOperator.AND), UtilMisc.toSet("totalPostedDebits","glAccountId","totalPostedCredits","description","glAccountClassId","organizationPartyId","costCenterId","segmentId"), null, null, false);
						
						prevTempGlAccountAndHistories = EntityUtil.getFirst(prevTempGlAccountAndHistories);
						if(glAccountId=="210000"){
							Debug.log("prevTempGlAccountAndHistories######222########################"+prevTempGlAccountAndHistories);
						}
						Debug.log("prevsCond########"+prevsCond);
						Debug.log("prevTempGlAccountAndHistories########"+prevTempGlAccountAndHistories.size());
						if(UtilValidate.isNotEmpty(prevTempGlAccountAndHistories)){
						prevTempGlAccountAndHistories = EntityUtil.filterByCondition(prevTempGlAccountAndHistories,prevsCond);
						Debug.log("prevTempGlAccountAndHistories##1111######"+prevTempGlAccountAndHistories);
						
							prevTempGlAccountAndHistories = EntityUtil.getFirst(prevTempGlAccountAndHistories);
						}
						if(UtilValidate.isNotEmpty(prevTempGlAccountAndHistories)){
							if(UtilValidate.isNotEmpty(prevTempGlAccountAndHistories.totalEndingBalance)){
							totalEndingBalance = prevTempGlAccountAndHistories.totalEndingBalance;
							}
							if(UtilValidate.isNotEmpty(prevTempGlAccountAndHistories.totalPostedDebits)){
								totalPostedDebits = totalPostedDebits+prevTempGlAccountAndHistories.totalPostedDebits;
							}
							if(UtilValidate.isNotEmpty(prevTempGlAccountAndHistories.totalPostedCredits)){
								totalPostedCredits = totalPostedCredits+prevTempGlAccountAndHistories.totalPostedCredits;
							}
							if(glAccountId=="210000"){
								Debug.log("totalPostedDebits##############################"+totalPostedDebits);
								Debug.log("totalPostedCredits##############################"+totalPostedCredits);
							}
						}
						
					}*/
					
					if(totalEndingBalance>0){
						totalPostedDebits =totalPostedDebits+totalEndingBalance;
					}else{
						totalPostedCredits=totalPostedCredits+totalEndingBalance;
					}
					if(UtilValidate.isNotEmpty(totalPostedDebits)){
						childCatDebitTotal=childCatDebitTotal+totalPostedDebits;
					}
					
					if(UtilValidate.isNotEmpty(totalPostedCredits)){
						childCatCreditTotal=childCatCreditTotal+totalPostedCredits;
					}
					glTempVal=0
					glTempVal=totalPostedDebits-totalPostedCredits;
					/*if(glTempVal<0){
						glTempVal=glTempVal.multiply(-1);
					}*/
					if(GlAccountIdsWiseMap && GlAccountIdsWiseMap.get(glAccntCategoryId)){
						tempGlMap=GlAccountIdsWiseMap.get(glAccntCategoryId);
						if( tempGlMap && tempGlMap.get(glAccountId)){
							tempExGlMap = tempGlMap.get(glAccountId);
							temVal=0;
							if(UtilValidate.isNotEmpty(tempExGlMap.get(eachCustmTime))){
								temVal = tempExGlMap.get(eachCustmTime);
							}
							tempExGlMap.put(eachCustmTime,temVal+glTempVal);
							tempGlMap.put(glAccountId,tempExGlMap);
						}else{
							tempMap=[:];
							tempMap.put("glAccountId",glAccountId);
							tempMap.put("description",tempGlAccountAndHistory.description);
							tempMap.put(eachCustmTime,glTempVal);
							tempGlMap.put(glAccountId,tempMap);
						}
						GlAccountIdsWiseMap.put(glAccntCategoryId,tempGlMap);
					}else{
						tempglAccountIdsMap=[:];
						tempMap=[:];
						tempMap.put("glAccountId",glAccountId);
						tempMap.put("description",tempGlAccountAndHistory.description);
						tempMap.put(eachCustmTime,glTempVal);
						tempglAccountIdsMap.put(glAccountId,tempMap);
						GlAccountIdsWiseMap.put(glAccntCategoryId,tempglAccountIdsMap);
					}
				}
			}
			}
			
		}
		//Debug.log("GlAccountIdsWiseMap#####################"+GlAccountIdsWiseMap);
		childCatName=glAccountChildCategory.get("description");
		scheduleNo = "";
		if(UtilValidate.isNotEmpty(glAccountChildCategory.get("categoryNarration"))){
			scheduleNo = glAccountChildCategory.get("categoryNarration");
		}
		childCurrentYearBal= childCatDebitTotal-childCatCreditTotal;
		if(childCurrentYearBal<0){
			childCurrentYearBal=childCurrentYearBal.multiply(-1);
		}
		if(assetCategoryList.contains(glAccntCategoryId)){	
			assetsGrandTotal=assetsGrandTotal+childCurrentYearBal;
			if(assetsChildWiseMap && assetsChildWiseMap.get(glAccntCategoryId)){
				existMap=assetsChildWiseMap.get(glAccntCategoryId);
				existMap.put("childCurrentYearBal",childCurrentYearBal);
				existMap.put("childCurrentPostedCredit",childCatCreditTotal);
				existMap.put("childCurrentPostedDebit",childCatDebitTotal);
				existMap.put(eachCustmTime,childCurrentYearBal);
				assetsChildWiseMap.put(glAccntCategoryId, existMap);
			}else{
				tempMap=[:];
				tempMap.put("childCatName",childCatName);
				tempMap.put("scheduleNo",scheduleNo);
				tempMap.put("childCurrentYearBal",childCurrentYearBal);
				tempMap.put("childCurrentPostedCredit",childCatCreditTotal);
				tempMap.put("childCurrentPostedDebit",childCatDebitTotal);
				tempMap.put(eachCustmTime,childCurrentYearBal);
				assetsChildWiseMap.put(glAccntCategoryId, tempMap);
			}
		}else if(liabilityCategoryList.contains(glAccntCategoryId)){
			//childCurrentYearBal=childCurrentYearBal.multiply(-1);
			liablitiesGrandTotal=liablitiesGrandTotal+childCurrentYearBal;
			if(liablitiesChildWiseMap && liablitiesChildWiseMap.get(glAccntCategoryId)){
				existMap=liablitiesChildWiseMap.get(glAccntCategoryId);
				existMap.put("childCurrentYearBal",childCurrentYearBal);
				existMap.put("childCurrentPostedCredit",childCatCreditTotal);
				existMap.put("childCurrentPostedDebit",childCatDebitTotal);
				existMap.put(eachCustmTime,childCurrentYearBal);
				liablitiesChildWiseMap.put(glAccntCategoryId, existMap);
			}else{
				tempMap=[:];
				tempMap.put("childCatName",childCatName);
				tempMap.put("scheduleNo",scheduleNo);
				tempMap.put("childCurrentYearBal",childCurrentYearBal);
				tempMap.put("childCurrentPostedCredit",childCatCreditTotal);
				tempMap.put("childCurrentPostedDebit",childCatDebitTotal);
				tempMap.put(eachCustmTime,childCurrentYearBal);
				liablitiesChildWiseMap.put(glAccntCategoryId, tempMap);
			}
		}
		
	}
}
GrandLiablitesTotal.put(eachCustmTime,liablitiesGrandTotal);
GrandassetsTotal.put(eachCustmTime,assetsGrandTotal);
}
context.assetsChildWiseMap=assetsChildWiseMap;
context.liablitiesChildWiseMap=liablitiesChildWiseMap;
context.GrandLiablitesTotal=GrandLiablitesTotal;
context.GrandassetsTotal=GrandassetsTotal;
context.GlAccountIdsWiseMap=GlAccountIdsWiseMap;
context.liabilityCategoryList=liabilityCategoryList;
context.assetCategoryList=assetCategoryList;



