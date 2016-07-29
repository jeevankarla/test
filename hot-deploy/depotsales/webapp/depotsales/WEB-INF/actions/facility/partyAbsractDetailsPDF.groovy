import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.SortedMap;
import org.ofbiz.party.party.PartyHelper;


import javolution.util.FastMap;
import javolution.util.FastList;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;
import java.util.Map.Entry;


SimpleDateFormat sdf = new SimpleDateFormat("yyyy, MMM dd");


partyfromDate=parameters.abstrctFromDate;
partythruDate=parameters.abstrctThruDate;

partyId=parameters.partyId;

branchId=parameters.branchId;

Timestamp fromDate;
Timestamp thruDate;

if(UtilValidate.isNotEmpty(partyfromDate)){
	
	  try {
		  //daystart = UtilDateTime.toTimestamp(sdf.parse(parameters.partyfromDate));
		  
		  fromDate = new java.sql.Timestamp(sdf.parse(partyfromDate).getTime());
		  
		   } catch (ParseException e) {
			   //Debug.logError(e, "Cannot parse date string: " + parameters.partyfromDate, "");
			   }
	 
  }
  if(UtilValidate.isNotEmpty(partythruDate)){
	 
	 try {
	   //  dayend = UtilDateTime.toTimestamp(sdf.parse(parameters.partythruDate));
		 
		 thruDate = new java.sql.Timestamp(sdf.parse(partythruDate).getTime());
	 } catch (ParseException e) {
		 //Debug.logError(e, "Cannot parse date string: " + parameters.partythruDate, "");
		  }
  }
  
  daystart = UtilDateTime.getDayStart(fromDate);
  dayend = UtilDateTime.getDayEnd(thruDate);
  
  //Debug.log("daystart==================="+daystart);
  
  //Debug.log("dayend==================="+dayend);
  
  //Debug.log("branchId==================="+branchId);
  
  branchList = [];
  
  condListb = [];
  condListb.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, branchId));
  condListb.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
  condListb = EntityCondition.makeCondition(condListb, EntityOperator.AND);
  
  PartyRole = delegator.findList("PartyRole", condListb,UtilMisc.toSet("roleTypeId"), null, null, false);
  
  if(PartyRole){
	    condListb = [];
	  condListb.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, branchId));
	  condListb.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
	  cond = EntityCondition.makeCondition(condListb, EntityOperator.AND);
	  
	  PartyRelationship = delegator.findList("PartyRelationship", cond,UtilMisc.toSet("partyIdTo"), null, null, false);
	  
	  branchList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);
  }else{
  
  branchList.add(branchId);
  }
  
  //Debug.log("branchList================"+branchList);
  condListb = [];
  
  condListb.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, branchList));
 
  if(partyId)
   condListb.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId));
  
  condListb.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
  condListb.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "EMPANELLED_CUSTOMER"));
  cond = EntityCondition.makeCondition(condListb, EntityOperator.AND);
  
  PartyRelationship = delegator.findList("PartyRelationship", cond,UtilMisc.toSet("partyIdTo"), null, null, false);
  
  weaversList =EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);
  
  
  conditionList = [];
  conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, weaversList));
  conditionList.add(EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "PSB_NUMER"));
  cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
  PartyIdentificationList = delegator.findList("PartyIdentification", cond, null, null, null, false);
  
  finalList = [];
  quotaList = ["availableQuota","usedQuota","eligibleQuota"];
  
 
  
  for (eachWeaver in weaversList) {
 
	  int i = 0;
	 quotaList.each{ eachType->
  
  
	
	   
	   tempMap = [:];
	   
	   partyName = PartyHelper.getPartyName(delegator, eachWeaver, false);
	   
	   //Debug.log("partyName==================="+partyName);
	   
	   conditionList.clear();
	   conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, eachWeaver));
       conditionList.add(EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "PSB_NUMER"));
	   cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	   PartyIdentification = EntityUtil.filterByCondition(PartyIdentificationList, cond);
     
	   passNo = "";
	   if(PartyIdentificationList){
		   passNo = PartyIdentificationList[0].get("idValue");
		 }
	   
	   tempMap.put("partyId", eachWeaver);
	   tempMap.put("partyName", partyName);
	   tempMap.put("passbookno", passNo);
	   //=============district==============
	   
	   contactMechesDetails = [];
	   conditionListAddress = [];
	   conditionListAddress.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, eachWeaver));
	   conditionListAddress.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "SHIPPING_LOCATION"));
	   conditionAddress = EntityCondition.makeCondition(conditionListAddress,EntityOperator.AND);
		List<String> orderBy = UtilMisc.toList("-contactMechId");
	   contactMech = delegator.findList("PartyContactDetailByPurpose", conditionAddress, null, orderBy, null, false);
	   
	   
	   if(contactMech){
	   contactMechesDetails = contactMech;
	   }
	   else{
		   conditionListAddress.clear();
		   conditionListAddress.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, eachWeaver));
		   conditionListAddress.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
		   conditionAddress = EntityCondition.makeCondition(conditionListAddress,EntityOperator.AND);
		   contactMechesDetails = delegator.findList("PartyContactDetailByPurpose", conditionAddress, null, null, null, false);
	   }
	   
	 
	   
	   shipingAdd = [:];
	   
	   if(contactMechesDetails){
		   contactMec=contactMechesDetails.getFirst();
		   if(contactMec){
			   
			   partyPostalAddress=contactMec;
			   
			   if(partyPostalAddress){
				   address1="";
				   address2="";
				   state="";
				   city="";
				   postalCode="";
				  if(partyPostalAddress.get("address1")){
				   address1=partyPostalAddress.get("address1");
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
				 /*   //shipingAdd.put("name",shippPartyName);
				   shipingAdd.put("address1",address1);
				   shipingAdd.put("address2",address2);
				   shipingAdd.put("city",city);
				   shipingAdd.put("state",state);
				   shipingAdd.put("postalCode",postalCode);*/
				
				   tempMap.put("district",city);
				   tempMap.put("state",city);
				      
			   }
		   }
	   }
	   
	   
	   
	   resultCtx = dispatcher.runSync("getPartyAvailableQuotaBalanceHistory",UtilMisc.toMap("userLogin",userLogin, "partyId", eachWeaver,"effectiveDate",dayend));
	   productCategoryQuotasMap = resultCtx.get("schemesMap");
	   usedQuotaMap = resultCtx.get("usedQuotaMap");
	   eligibleQuota = resultCtx.get("eligibleQuota");
	   
	  //Debug.log("productCategoryQuotasMap================"+productCategoryQuotasMap);
	   
	   availableQuotaUP = productCategoryQuotasMap.get("COTTON_UPTO40");
	   availableQuotaAbove40 = productCategoryQuotasMap.get("COTTON_40ABOVE");
	   availableSILK_YARN = productCategoryQuotasMap.get("SILK_YARN");
	   availableWOOLYARN_10STO39NM = productCategoryQuotasMap.get("WOOLYARN_10STO39NM");
	   availableWOOLYARN_40SNMABOVE = productCategoryQuotasMap.get("WOOLYARN_40SNMABOVE");
	   availableWOOLYARN_BELOW10NM = productCategoryQuotasMap.get("WOOLYARN_BELOW10NM");
	   
	   
	   usedQuotaUP = usedQuotaMap.get("COTTON_UPTO40");
	   usedQuotaAbove40 = usedQuotaMap.get("COTTON_40ABOVE");
	   usedSILK_YARN = usedQuotaMap.get("SILK_YARN");
	   usedWOOLYARN_10STO39NM = usedQuotaMap.get("WOOLYARN_10STO39NM");
	   usedWOOLYARN_40SNMABOVE = usedQuotaMap.get("WOOLYARN_40SNMABOVE");
	   usedWOOLYARN_BELOW10NM = usedQuotaMap.get("WOOLYARN_BELOW10NM");
	   
	   
	   eligibleQuotaUP = eligibleQuota.get("COTTON_UPTO40");
	   eligibleQuotaAbove40 = eligibleQuota.get("COTTON_40ABOVE");
	   eligibleSILK_YARN = eligibleQuota.get("SILK_YARN");
	   eligibleWOOLYARN_10STO39NM = eligibleQuota.get("WOOLYARN_10STO39NM");
	   eligibleWOOLYARN_40SNMABOVE = eligibleQuota.get("WOOLYARN_40SNMABOVE");
	   eligibleWOOLYARN_BELOW10NM = eligibleQuota.get("WOOLYARN_BELOW10NM");
	   
	   if(i == 0){
	   tempMap.put("COTTON_UPTO40","Available Quota ="+Math.round(availableQuotaUP));
	   tempMap.put("COTTON_40ABOVE","Available Quota ="+Math.round(availableQuotaAbove40));
	   tempMap.put("SILK_YARN","Available Quota ="+Math.round(availableSILK_YARN));
	   tempMap.put("WOOLYARN_10STO39NM","Available Quota ="+Math.round(availableWOOLYARN_10STO39NM));
	   tempMap.put("WOOLYARN_40SNMABOVE","Available Quota ="+Math.round(availableWOOLYARN_40SNMABOVE));
	   tempMap.put("WOOLYARN_BELOW10NM","Available Quota ="+Math.round(availableWOOLYARN_BELOW10NM));
	   
	   }
	   if(i == 1){
		   
		   tempMap.put("partyId", "");
		   tempMap.put("partyName", "");
		   tempMap.put("passbookno", "");
		   tempMap.put("state", "");
		   tempMap.put("district", "");
		   
	   tempMap.put("COTTON_UPTO40","Qualized Quota ="+Math.round(usedQuotaUP));
	   tempMap.put("COTTON_40ABOVE","Qualized Quota ="+Math.round(usedQuotaAbove40));
	   tempMap.put("SILK_YARN","Qualized Quota ="+Math.round(usedSILK_YARN));
	   tempMap.put("WOOLYARN_10STO39NM","Qualized Quota ="+Math.round(usedWOOLYARN_10STO39NM));
	   tempMap.put("WOOLYARN_40SNMABOVE","Qualized Quota ="+Math.round(usedWOOLYARN_40SNMABOVE));
	   tempMap.put("WOOLYARN_BELOW10NM","Qualized Quota ="+Math.round(usedWOOLYARN_BELOW10NM));

	   
	   }
	   if(i == 2){

		   tempMap.put("partyId", "");
		   tempMap.put("partyName", "");
		   tempMap.put("passbookno", "");
		   tempMap.put("state", "");
		   tempMap.put("district", "");
		   
	   tempMap.put("COTTON_UPTO40","Balance Quota ="+Math.round(eligibleQuotaUP));
	   tempMap.put("COTTON_40ABOVE","Balance Quota ="+Math.round(eligibleQuotaAbove40));
	   tempMap.put("SILK_YARN","Balance Quota ="+Math.round(eligibleSILK_YARN));
	   tempMap.put("WOOLYARN_10STO39NM","Balance Quota ="+Math.round(eligibleWOOLYARN_10STO39NM));
	   tempMap.put("WOOLYARN_40SNMABOVE","Balance Quota ="+Math.round(eligibleWOOLYARN_40SNMABOVE));
	   tempMap.put("WOOLYARN_BELOW10NM","Balance Quota ="+Math.round(eligibleWOOLYARN_BELOW10NM));
	   }
	  
	   
/*	   conditionList.clear();
	   conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,eachWeaver));
	   condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	   PartyLoomDetails = delegator.findList("PartyLoom",condition,null,null,null,false);
	   
	   if(PartyLoomDetails){
		   PartyLoomDetails.each{ eachPartyLoom ->
			   loomType="";
			   loomQuota="";
			   loomQty="";
			   Desc="";
		   loomQuota=eachPartyLoom.quotaPerLoom;
		   loomQty=eachPartyLoom.quantity;
		   conditionList.clear();
		   conditionList.add(EntityCondition.makeCondition("loomTypeId", EntityOperator.EQUALS,eachPartyLoom.loomTypeId));
		   condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		   LoomTypeDetails = EntityUtil.getFirst(delegator.findList("LoomType",condition,null,null,null,false));
		   ////Debug.log("PartyLoomDetails======================"+PartyLoomDetails);
		   if(LoomTypeDetails){
			   type=LoomTypeDetails.loomTypeId;
			   if(LoomTypeDetails.description){
			   Desc=LoomTypeDetails.description
			   }
			   Desc +=type;
		   }
		    partyLoom = [:];
		   
		   partyLoom.put("loomType",Desc);
		   partyLoom.put("loomQuota",eligibleQuota.get(Desc));
		   partyLoom.put("availableQuota",productCategoryQuotasMap.get(Desc));
		   partyLoom.put("usedQuota",usedQuotaMap.get(Desc));
		   partyLoom.put("loomQty",loomQty);
		   quotaList.add(partyLoom)
		   }
	   }*/
	   
	   //tempMap.put("loomDetails",quotaList);
	   //tempMap.put("loomDetails",quotaList);
	  // //Debug.log("usedQuotaMap==============="+usedQuotaMap);
	   ////Debug.log("eligibleQuota==============="+eligibleQuota);
	  // tempMap.put("COTTON_UPTO40", condListb)
	   
	   
	   
	   finalList.add(tempMap);
	   i++;
}
   
  }
//Debug.log("finalList==============="+finalList);

   context.finalList = finalList;
   ////Debug.log("finalList====================="+finalList);
   
