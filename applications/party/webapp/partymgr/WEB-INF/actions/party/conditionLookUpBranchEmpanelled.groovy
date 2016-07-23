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
	
	
	partyIdFrom = null
	
	if(parameters.partyIdFrom){
		partyIdFrom = parameters.partyIdFrom;
	}
	else if(parameters.productStoreId){
		GenericValue productStore=delegator.findOne("ProductStore",[productStoreId:parameters.productStoreId],false);
		if(UtilValidate.isNotEmpty(productStore)){
			partyIdFrom = productStore.payToPartyId;
		}
	}
	
	List roleTypeList=[];
	if(parameters.ajaxLookup == 'Y'){
		roleTypeId=parameters.roleTypeId;
		partyClassificationGroupId=parameters.partyClassificationGroupId;
		// initialising  conditionFields Map 
		
		conditionFields=[:];
		
		//checking for the roleType parameter in Ajax call
		//if roleTypeId is null use the default view  "PartyNameView"
	
		if(roleTypeId != null){
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
			
			
		}
	
	}
	//not ajax
	else{
		context.defaultRoleTypeList=new Boolean(true);
		StringToURL stringToUrl=new StringToURL();
		URL refererUri= stringToUrl.convert(request.getHeader("referer"));
		pathInfo=refererUri.getPath();
		Debug.log("pathInfo================="+pathInfo);
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
