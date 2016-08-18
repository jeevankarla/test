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

import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;

import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.sql.Timestamp;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

partyId = parameters.partyId;
context.partyId = partyId;
Map mechMap = new HashMap();
ContactMechWorker.getContactMechAndRelated(request, partyId, mechMap);
context.mechMap = mechMap;
context.contactMechId = mechMap.contactMechId;
context.preContactMechTypeId = parameters.preContactMechTypeId;
context.paymentMethodId = parameters.paymentMethodId;

cmNewPurposeTypeId = parameters.contactMechPurposeTypeId;
if (cmNewPurposeTypeId) {
    contactMechPurposeType = delegator.findByPrimaryKey("ContactMechPurposeType", [contactMechPurposeTypeId : cmNewPurposeTypeId]);
    if (contactMechPurposeType) {
        context.contactMechPurposeType = contactMechPurposeType;
    } else {
        cmNewPurposeTypeId = null;
    }
    context.cmNewPurposeTypeId = cmNewPurposeTypeId;
}
context.donePage = parameters.DONE_PAGE ?:"viewprofile?party_id=" + partyId + "&partyId=" + partyId;;



conditionDeopoList = [];
conditionDeopoList.add(EntityCondition.makeCondition("geoId", EntityOperator.LIKE,"IN-%"));
conditionDeopoList.add(EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS,"STATE"));
conditionDepo=EntityCondition.makeCondition(conditionDeopoList,EntityOperator.AND);
statesList = delegator.findList("Geo",conditionDepo,null,null,null,false);


JSONArray stateListJSON = new JSONArray();
statesList.each{ eachState ->
		JSONObject newObj = new JSONObject();
		newObj.put("value",eachState.geoId);
		newObj.put("label",eachState.geoName);
		stateListJSON.add(newObj);
}
context.stateListJSON = stateListJSON;


//===============================state District================================


contactMechesDetails = [];
conditionListAddress = [];
conditionListAddress.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
conditionListAddress.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "SHIPPING_LOCATION"));
conditionAddress = EntityCondition.makeCondition(conditionListAddress,EntityOperator.AND);
 List<String> orderBy = UtilMisc.toList("-contactMechId");
contactMech = delegator.findList("PartyContactDetailByPurpose", conditionAddress, null, orderBy, null, false);


if(contactMech){
contactMechesDetails = contactMech;
}
else{
	conditionListAddress.clear();
	conditionListAddress.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
	conditionListAddress.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
	conditionAddress = EntityCondition.makeCondition(conditionListAddress,EntityOperator.AND);
	contactMechesDetails = delegator.findList("PartyContactDetailByPurpose", conditionAddress, null, null, null, false);
}

shipingAdd = [:];

if(contactMechesDetails){
	contactMec=contactMechesDetails.getFirst();
	if(contactMec){
		//partyPostalAddress=contactMec.get("postalAddress");
		
		partyPostalAddress=contactMec;
		
		////////Debug.log("partyPostalAddress=========================="+partyPostalAddress);
	//	partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:invoicePartyId, userLogin: userLogin]);
		if(partyPostalAddress){
			address1="";
			address2="";
			state="";
			city="";
			postalCode="";
			districtGeoId = "";
			stateGeoId = "";
			stateProvinceGeoId = "";
			if(partyPostalAddress.get("address1")){
			address1=partyPostalAddress.get("address1");
			////////Debug.log("address1=========================="+address1);
			}
			if(partyPostalAddress.get("address2")){
				address2=partyPostalAddress.get("address2");
				}
			if(partyPostalAddress.get("city")){
				city=partyPostalAddress.get("city");
				}
			if(partyPostalAddress.get("stateGeoName")){
				state=partyPostalAddress.get("stateGeoName");
				}
			if(partyPostalAddress.get("postalCode")){
				postalCode=partyPostalAddress.get("postalCode");
				}
			if(partyPostalAddress.get("districtGeoId")){
				districtGeoId=partyPostalAddress.get("districtGeoId");
				}
			if(partyPostalAddress.get("stateGeoId")){
				stateGeoId=partyPostalAddress.get("stateGeoId");
				}
			if(partyPostalAddress.get("stateProvinceGeoId")){
				stateProvinceGeoId=partyPostalAddress.get("stateProvinceGeoId");
				}
			
			
			
			//shipingAdd.put("name",shippPartyName);
			shipingAdd.put("address1",address1);
			shipingAdd.put("address2",address2);
			shipingAdd.put("city",city);
			shipingAdd.put("state",state);
			shipingAdd.put("districtGeoId",districtGeoId);
			shipingAdd.put("postalCode",postalCode);
			shipingAdd.put("stateGeoId",stateGeoId);
			
			//Debug.log("districtGeoId============"+districtGeoId);
			
			//Debug.log("stateGeoId============"+stateGeoId);
			
			//Debug.log("stateProvinceGeoId============"+stateProvinceGeoId);
			
			
			geo=delegator.findOne("Geo",[geoId : districtGeoId], false);
			if(geo)
			shipingAdd.put("district",geo.geoName);
			else
			shipingAdd.put("district","");
			
			
			if(!state){
				geo=delegator.findOne("Geo",[geoId : stateProvinceGeoId], false);
				if(geo)
				shipingAdd.put("state",geo.geoName);
				else
				shipingAdd.put("state","");
				
			}
			
		}
	}
}
//========================================

stateGeoId = shipingAdd.get("stateGeoId");

districtGeoId = shipingAdd.get("districtGeoId");


state = shipingAdd.get("state");

district = shipingAdd.get("district");

context.district = district;

context.districtGeoId = districtGeoId;

context.stateGeoId = stateGeoId;
context.state = state;


conditionDeopoList.clear();
conditionDeopoList.add(EntityCondition.makeCondition("geoId", EntityOperator.EQUALS,stateGeoId));
conditionDeopoList.add(EntityCondition.makeCondition("geoAssocTypeId", EntityOperator.EQUALS,"DISTRICT"));
conditionDepo=EntityCondition.makeCondition(conditionDeopoList,EntityOperator.AND);
districtListAss = delegator.findList("GeoAssoc",conditionDepo,UtilMisc.toSet("geoIdTo"),null,null,false);

List districts = EntityUtil.getFieldListFromEntityList(districtListAss, "geoIdTo", true);


conditionDeopoList.clear();
conditionDeopoList.add(EntityCondition.makeCondition("geoId", EntityOperator.IN,districts));
conditionDeopoList.add(EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS,"DISTRICT"));
conditionDepo=EntityCondition.makeCondition(conditionDeopoList,EntityOperator.AND);
districtList = delegator.findList("Geo",conditionDepo,null,null,null,false);


JSONArray districtJSON = new JSONArray();
districtList.each{ eachDistrict ->
		JSONObject newObj = new JSONObject();
		newObj.put("value",eachDistrict.geoId);
		newObj.put("label",eachDistrict.geoName);
		districtJSON.add(newObj);
}
context.districtJSON = districtJSON;


