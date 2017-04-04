import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import javolution.util.FastMap;
import java.text.ParseException;
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.facility.util.FacilityUtil;
import java.text.SimpleDateFormat;
import java.util.List;
import org.ofbiz.base.util.UtilMisc;
 
List roleTypeAttr=delegator.findList("RoleTypeAttr",EntityCondition.makeCondition("attrName",EntityOperator.EQUALS,"ACCOUNTING_ROLE"),null,null,null,false);
roleTypeAttrList=[];
if(roleTypeAttr){
	roleTypeAttr.each{roleType->
		tempMap=[:];
		if(roleType.roleTypeId != "EMPLOYEE" && roleType.roleTypeId != "MILK_SUPPLIER"){
			GenericValue roleTypeDes=delegator.findOne("RoleType",[roleTypeId:roleType.roleTypeId],false);
			if(UtilValidate.isNotEmpty(roleTypeDes)){
				tempMap["roleTypeId"]=roleType.roleTypeId;
				tempMap["description"]=roleTypeDes.description;
				roleTypeAttrList.add(tempMap);
			}
		}
	}
}
context.roleTypeAttrList=roleTypeAttrList;
isFormSubmitted = parameters.isFormSubmitted;
finalList = [];
if(isFormSubmitted && "Y"==isFormSubmitted){
	paymentGroupTypeId = parameters.paymentGroupTypeId;
	statusId = parameters.statusId;
	paymentDateStr = parameters.paymentDate;
	partyIdTo = parameters.partyIdTo;
	roleTypeId = parameters.roleTypeId;
	finAccountId = parameters.finAccountId;
	paymentDate = null;
	paymentAndGroupMemberList = [];
	
	if(paymentDateStr){
		def sdf = new SimpleDateFormat("dd MMMM, yyyy");
		try {
			paymentDate = new java.sql.Timestamp(sdf.parse(paymentDateStr).getTime());
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: "+paymentDateStr, "");
		}
		paymentDate = UtilDateTime.getDayStart(paymentDate);
		
	}
	
	conditionList = [];
	
	
	if(paymentGroupTypeId){
		conditionList.add(EntityCondition.makeCondition("paymentGroupTypeId", EntityOperator.EQUALS, paymentGroupTypeId));
	}
	
	if(finAccountId){
		conditionList.add(EntityCondition.makeCondition("finAccountId", EntityOperator.EQUALS, finAccountId));
	}
	if(statusId){
		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId));
	}
	if(UtilValidate.isNotEmpty(paymentDate) && UtilValidate.isNotEmpty(paymentDateStr)){
		conditionList.add(EntityCondition.makeCondition("paymentDate", EntityOperator.EQUALS, paymentDate));
	}
	
	
	if(partyIdTo){
		PaymentAndGroupMemberList = delegator.findList("PaymentAndGroupMember",EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyIdTo) , null, null, null, false);
		paymentGroupIdList = EntityUtil.getFieldListFromEntityList(PaymentAndGroupMemberList, "paymentGroupId", false);
		if(UtilValidate.isNotEmpty(paymentGroupIdList)){
			conditionList.add(EntityCondition.makeCondition("paymentGroupId", EntityOperator.IN, paymentGroupIdList));
		}
	}
	
	EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	List<String> orderBy = UtilMisc.toList("-paymentDate");
	paymentGroupList = delegator.findList("PaymentGroup", condition , null, orderBy, null, false);
	
	
	
	for(String paymentGroup : paymentGroupList){
		
		
				tempMap = [:];
				tempMap["paymentGroupId"] = paymentGroup.paymentGroupId;
				tempMap["paymentGroupTypeId"] = paymentGroup.paymentGroupTypeId;
				tempMap["paymentMethodTypeId"] = paymentGroup.paymentMethodTypeId;
				tempMap["paymentMethodId"] = paymentGroup.paymentMethodId;
				tempMap["paymentDate"] = paymentGroup.paymentDate;
				tempMap["paymentRefNum"] = paymentGroup.paymentRefNum;
				tempMap["issuingAuthority"] = paymentGroup.issuingAuthority;
				tempMap["amount"] = paymentGroup.amount;
				finalList.add(tempMap);
	}
	
}
context.finalList = finalList;
