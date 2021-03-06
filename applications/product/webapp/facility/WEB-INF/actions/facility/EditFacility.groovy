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

import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;

import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.sql.Timestamp;
userLogin= context.userLogin;
facilityId = parameters.facilityId;
if (!facilityId && request.getAttribute("facilityId")) {
  facilityId = request.getAttribute("facilityId");
}
facility = delegator.findOne("Facility", [facilityId : facilityId], false);
if (!facility) {
  facility = delegator.makeValue("Facility");
  facilityType = delegator.makeValue("FacilityType");
} else {
  facilityType = facility.getRelatedOne("FacilityType");
}

context.facility = facility;
context.facilityType = facilityType;
context.facilityId = facilityId;
if (facility && facility.ownerPartyId) {
partyTelephone= dispatcher.runSync("getPartyTelephone", [partyId: facility.ownerPartyId, userLogin: userLogin]);
context.mobileNumber = partyTelephone.contactNumber;
context.countryCode = partyTelephone.countryCode;
partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId: facility.ownerPartyId, userLogin: userLogin]);
context.partyPostalAddress = partyPostalAddress;
context.address1 = partyPostalAddress.address1;
context.address2 = partyPostalAddress.address2;
context.city = partyPostalAddress.city;
context.postalCode = partyPostalAddress.postalCode;

email= dispatcher.runSync("getPartyEmail", [partyId: facility.ownerPartyId, userLogin: userLogin,contactMechPurposeTypeId:"PRIMARY_EMAIL"]);
context.emailAddress = email.emailAddress;
}
//Facility types
facilityTypes = delegator.findList("FacilityType", null, null, null, null, false);
if (facilityTypes) {
  context.facilityTypes = facilityTypes;
}
GenericValue rateAmountTypes=null;
if (UtilValidate.isNotEmpty(facilityId)) {
	conditionList=[];
	conditionList.add(EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS,"CR_INST_MRGN"));
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, facility.ownerPartyId));
	condition1=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	fieldsToSelect = ["byprodProductPriceTypeId","lmsProductPriceTypeId","fromDate"] as Set;
	rateAmountList = delegator.findList("RateAmount", condition1, fieldsToSelect , ["-fromDate"], null, false);
	if (UtilValidate.isNotEmpty(rateAmountList)) {
	    rateAmountTypes= EntityUtil.getFirst(rateAmountList);
	}
}
context.rateAmountTypes = rateAmountTypes;
// all possible inventory item types
context.inventoryItemTypes = delegator.findList("InventoryItemType", null, null, ['description'], null, true);

// weight unit of measures
context.weightUomList = delegator.findList("Uom", EntityCondition.makeCondition([uomTypeId : 'WEIGHT_MEASURE']), null, null, null, true);

// area unit of measures
context.areaUomList = delegator.findList("Uom", EntityCondition.makeCondition([uomTypeId : 'AREA_MEASURE']), null, null, null, true);
Timestamp fromDate=UtilDateTime.toTimestamp(facility.openedDate);

BigDecimal rateAmount=null;

if (UtilValidate.isNotEmpty(facilityId)) {
	dayBegin = UtilDateTime.getDayStart(fromDate);
	Map inputRateAmt = UtilMisc.toMap("userLogin", userLogin);
	inputRateAmt.put("rateCurrencyUomId", "INR");
	inputRateAmt.put("facilityId", facilityId);
	inputRateAmt.put("fromDate",dayBegin);
	inputRateAmt.put("rateTypeId", "SHOPEE_RENT");
	condList=[];
	groupMemberList=[];
	boothsList=[];
	condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS,facilityId));
	condList.add(EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS,"SHOPEE_RENT"));
	condList.add(EntityCondition.makeCondition("rateCurrencyUomId", EntityOperator.EQUALS, "INR"));
	/*condList.add(EntityCondition.makeCondition("supplyTypeEnumId", EntityOperator.EQUALS, "_NA_"));*/
	cond1=EntityCondition.makeCondition(condList,EntityOperator.AND);
	facilityRates = delegator.findList("FacilityRate", cond1, null , null, null, false);
	facilityRate = null;
	if (UtilValidate.isNotEmpty(facilityTypes)) {
	   facilityRate = EntityUtil.filterByDate(facilityRates,UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()));
	}
	if (UtilValidate.isNotEmpty(facilityRate)) {
		GenericValue validFacilityRate= EntityUtil.getFirst(facilityRate);
		 rateAmount=(BigDecimal)validFacilityRate.get("rateAmount");
	}
		condList.clear();
		condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
		condList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
		cond1=EntityCondition.makeCondition(condList,EntityOperator.AND);

	    groupMemberList = delegator.findList("FacilityGroupMember",cond1 , null, null, null, false);
		groupMemberList = EntityUtil.getFieldListFromEntityList(groupMemberList, "facilityGroupId", true);
		
		condList.clear();
	    condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, groupMemberList));
		condList.add(EntityCondition.makeCondition("facilityGroupId", EntityOperator.EQUALS, "AM_RT_GROUP"));
		condition=EntityCondition.makeCondition(condList,EntityOperator.AND);
	 
		boothsList = delegator.findList("FacilityGroupAndMemberAndFacility",condition , null, null, null, false);
		boothsList = EntityUtil.getFieldListFromEntityList(boothsList, "facilityId", true);
		if (UtilValidate.isNotEmpty(boothsList)) {
		 context.amRoute=boothsList.get(0);
		}
		condList.clear();
		condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, groupMemberList));
		condList.add(EntityCondition.makeCondition("facilityGroupId", EntityOperator.EQUALS, "PM_RT_GROUP"));
		condition=EntityCondition.makeCondition(condList,EntityOperator.AND);
	 
		boothsList = delegator.findList("FacilityGroupAndMemberAndFacility",condition , null, null, null, false);
		boothsList = EntityUtil.getFieldListFromEntityList(boothsList, "facilityId", true);
		if (UtilValidate.isNotEmpty(boothsList)) {
		 context.pmRoute=boothsList.get(0);
		}
	
}
context.rateAmount=rateAmount;









