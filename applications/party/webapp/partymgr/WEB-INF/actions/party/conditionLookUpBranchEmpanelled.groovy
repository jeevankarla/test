	/*
	 * Licensed to the Apache Software Foundation (ASF) under one
	 * or more contributor license agreements.  See the NOTICE file
	 * distributed with this work for additional information
	 * regarding copyright ownership.  The ASF licenses this file
	 * to you under the Apache License, Version 2.0 (the
	 * "License"); you may not use this file except in compliance
	 * with the License.  You may obtain a copy of the License at
	 *
	 * http://www.apache.org/licenses/LICENSE-2.0
	 *
	 * Unless required by applicable law or agreed to in writing,
	 * software distributed under the License is distributed on an
	 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
	 * KIND, either express or implied.  See the License for the
	 * specific language governing permissions and limitations
	 * under the License.
	 */
	 
	 
	 /*   ********* AjaxConditionLookUp.groovy **********
	 * This Groovy add conditional Parameters to Ajax call   
	 * based on the screen which Ajax is call has been Made.
	 */
	 
	import org.ofbiz.base.util.*;
	import org.ofbiz.entity.condition.*;
	import java.net.URL;
	import org.ofbiz.base.conversion.NetConverters;
	import org.ofbiz.base.conversion.NetConverters.StringToURL;
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
	
	
	
	partyIdFrom = null
	
	RobranchList = null;
	if(parameters.roWise){
		condListb = [];
		
		condListb.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, parameters.roWise));
		condListb.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
		condListb.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
		condListb = EntityCondition.makeCondition(condListb, EntityOperator.AND);
		
		PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdTo"), null, null, false);
		
		RobranchList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);
		
		
	}
	
	if(parameters.state && !parameters.partyIdFrom && !parameters.roWise && !parameters.productStoreId){
		condListb4 = [];
		condListb4.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
		condListb4.add(EntityCondition.makeCondition("stateProvinceGeoId", EntityOperator.EQUALS, parameters.state));
		condListb = EntityCondition.makeCondition(condListb4, EntityOperator.AND);
		PartyContactDetailByPurposeList = delegator.find("PartyContactDetailByPurpose", condListb, null, UtilMisc.toSet("partyId"), null, null);
		branchpartyIdsList = EntityUtil.getFieldListFromEntityListIterator(PartyContactDetailByPurposeList, "partyId", true);
		
		condListb = [];
		condListb.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, branchpartyIdsList));
		condListb.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
		condListb.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "EMPANELLED_CUSTOMER"));
		condListb = EntityCondition.makeCondition(condListb, EntityOperator.AND);
		PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdFrom"), null, null, false);
		RobranchList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdFrom", true);
	}

	
	if(parameters.partyIdFrom){
		partyIdFrom = parameters.partyIdFrom;
	}
	else if(parameters.productStoreId){
		GenericValue productStore=delegator.findOne("ProductStore",[productStoreId:parameters.productStoreId],false);
		if(UtilValidate.isNotEmpty(productStore)){
			partyIdFrom = productStore.payToPartyId;
		}
	}
	
	if(UtilValidate.isEmpty(partyIdFrom)){
		resultCtx = dispatcher.runSync("getCustomerBranch",UtilMisc.toMap("userLogin",userLogin));
		List branchIds = [];
		for (eachList in resultCtx.get("productStoreList")) {
			branchIds.add(eachList.get("payToPartyId"));
		}
		partyIdFrom = branchIds;
	}
	
	
	
	if(RobranchList && !parameters.partyIdFrom){
		partyIdFrom = RobranchList;
	}
	
	List roleTypeList=[];
	if(parameters.ajaxLookup == 'Y'){
		roleTypeId=parameters.roleTypeId;
		partyClassificationGroupId=parameters.partyClassificationGroupId;
		// initialising  conditionFields Map 
		
		conditionFields=[:];
		
		//checking for the roleType parameter in Ajax call
		//if roleTypeId is null use the default view  "PartyNameView"
	
		
		if(roleTypeId != null){/*
			if(partyClassificationGroupId != null){
				parameters.partyClassificationGroupId=partyClassificationGroupId;
			}
			parameters.roleTypeId=roleTypeId;
			String screenFlag="";
			if(parameters.screenFlag){
			 screenFlag=parameters.screenFlag;
			}
			if(screenFlag && screenFlag.equals("onlyIndividual")){
				context.entityName="PartyIdentificationAndPersonClassification";		
			}else{
				context.entityName="PartyIdentificationAndPerson";
			}
			
			if(partyIdFrom != null){
				context.entityName="PartyRelationAndIdentificationAndPerson";
				conditionFields.partyIdFrom=partyIdFrom;
			}	
			
			   conditionFields.partyIdentificationTypeId=["PSB_NUMER"];
			
				conditionFields.roleTypeId=roleTypeId;
				if(partyClassificationGroupId != null){
				conditionFields.partyClassificationGroupId=partyClassificationGroupId;
				}
				//conditionFields.partyIdFrom=partyIdFrom;
				context.conditionFields=conditionFields;	
			
			
		*/}
		
		conditionFields.partyIdentificationTypeId=["PSB_NUMER","REGISTRATION_NUMBER"];
		if(roleTypeId)
		conditionFields.roleTypeId=roleTypeId;
		if(partyIdFrom)
		conditionFields.partyIdFrom=partyIdFrom;
		if(partyClassificationGroupId)
		conditionFields.partyClassificationGroupId=partyClassificationGroupId;
		
		context.conditionFields=conditionFields;
		
	}
	//not ajax
	else{
		context.defaultRoleTypeList=new Boolean(true);
		StringToURL stringToUrl=new StringToURL();
		URL refererUri= stringToUrl.convert(request.getHeader("referer"));
		pathInfo=refererUri.getPath();
		//Debug.log("pathInfo================="+pathInfo);
		if(pathInfo=="/depotsales/control/BranchSalesOrder"){
			context.defaultRoleTypeList= new Boolean(false);
			roleTypeId=parameters.roleTypeId;
			description="";
			if(roleTypeId){
				GenericValue roleTypeDes=delegator.findOne("RoleType",[roleTypeId:roleTypeId],false);
				if(roleTypeDes){
					roleTypeId=roleTypeDes.roleTypeId;
					description=roleTypeDes.description;
					context.roleTypeId=roleTypeId;
				}
			}
			roleTypeList.add([roleTypeId :roleTypeId ,description : description]);
			context.roleTypeList=roleTypeList;
		}
		
		
		
	}
