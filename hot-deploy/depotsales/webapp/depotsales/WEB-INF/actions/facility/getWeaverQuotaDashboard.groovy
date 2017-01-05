import org.ofbiz.base.util.UtilDateTime;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import org.ofbiz.party.party.PartyHelper;


import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import net.sf.json.JSONObject;
import javolution.util.FastList;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.party.contact.ContactMechWorker;
import javolution.util.FastMap;
import java.text.ParseException;
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.facility.util.FacilityUtil;




branchId = parameters.branchId;
passbookNumber = parameters.passbookNumber;
partyId = parameters.partyId;
partyClassification = parameters.partyClassification;
isDepot = parameters.isDepot;
state = parameters.satate;
district = parameters.district;
passGreater = parameters.passGreater;



effectiveDate = parameters.effectiveDate;



facilityDateStart = null;
facilityDateEnd = null;
if(UtilValidate.isNotEmpty(effectiveDate)){
	def sdf = new SimpleDateFormat("yyyy-MM");
	try {
		transDate = new java.sql.Timestamp(sdf.parse(effectiveDate+" 00:00:00").getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + effectiveDate, "");
	}
	facilityDateStart = UtilDateTime.getDayStart(transDate);
	facilityDateEnd = UtilDateTime.getDayEnd(transDate);
	
}else{

  facilityDateEnd = UtilDateTime.nowTimestamp()

}




uniqueOrderId = parameters.uniqueOrderId;


uniqueOrderIdsList = Eval.me(uniqueOrderId)


//Debug.log("branchId================="+branchId);
//Debug.log("passbookNumber================="+passbookNumber);
//Debug.log("partyId================="+partyId);
//Debug.log("partyClassification================="+partyClassification);

double totalIndents = 0;
JSONArray weaverDetailsList = new JSONArray();



branchList = [];

if(branchId){
condListb = [];
condListb.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, branchId));
condListb.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
condListb = EntityCondition.makeCondition(condListb, EntityOperator.AND);

PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdTo"), null, null, false);
branchList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);
}

if(UtilValidate.isEmpty(branchList) && UtilValidate.isNotEmpty(branchId))
branchList.add(branchId);



partyList = [];


condListba = [];
condListba.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, branchList));
condListba.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
condListba.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "EMPANELLED_CUSTOMER"));
condListb = EntityCondition.makeCondition(condListba, EntityOperator.AND);

partyIdsList = delegator.find("PartyRelationship", condListb, null, UtilMisc.toSet("partyIdTo"), null, null);


branchpartyIdsList = EntityUtil.getFieldListFromEntityListIterator(partyIdsList, "partyIdTo", true);


	PartyClassificationPartyIds = [];
	if(partyClassification && !partyId){
		condListb1 = [];
		if(branchpartyIdsList)
		condListb1.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, branchpartyIdsList));
		condListb1.add(EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.EQUALS, partyClassification));
		condListb = EntityCondition.makeCondition(condListb1, EntityOperator.AND);
		PartyClassificationList = delegator.find("PartyClassification", condListb, null, UtilMisc.toSet("partyId"), null, null);
		
		
		branchpartyIdsList = EntityUtil.getFieldListFromEntityListIterator(PartyClassificationList, "partyId", true);
	}
	
	isDepotPartyIds = [];
	if(isDepot && !partyId){
		condListb2 = [];
		if(parameters.isDepot == "Y"){
			
			condListb2.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "DEPOT_SOCIETY"));
			if(branchpartyIdsList)
			condListb2.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.IN,branchpartyIdsList));
			fcond = EntityCondition.makeCondition(condListb2, EntityOperator.AND);
			FacilityList = delegator.find("Facility", fcond, null, UtilMisc.toSet("ownerPartyId"), null, null);
			
			branchpartyIdsList = EntityUtil.getFieldListFromEntityListIterator(FacilityList, "ownerPartyId", true);
		}
	
	}
	
	PartyContactDetailByPurposeIds = [];
	if(state && !partyId){
		condListb4 = [];
		if(branchpartyIdsList)
		condListb4.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, branchpartyIdsList));
		condListb4.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
		condListb4.add(EntityCondition.makeCondition("stateProvinceGeoId", EntityOperator.EQUALS, state));
		condListb = EntityCondition.makeCondition(condListb4, EntityOperator.AND);
		PartyContactDetailByPurposeList = delegator.find("PartyContactDetailByPurpose", condListb, null, UtilMisc.toSet("partyId"), null, null);
		
		branchpartyIdsList = EntityUtil.getFieldListFromEntityListIterator(PartyContactDetailByPurposeList, "partyId", true);
	
	}
	
	PartyContactDetailByDistrict = [];
	
	if(district){
		condListb5 = [];
		if(branchpartyIdsList)
		condListb5.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, branchpartyIdsList));
		condListb5.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
		condListb5.add(EntityCondition.makeCondition("districtGeoId", EntityOperator.EQUALS, district));
		condListb = EntityCondition.makeCondition(condListb5, EntityOperator.AND);
		
		PartyContactDetailByDistrict = delegator.find("PartyContactDetailByPurpose", condListb, null, UtilMisc.toSet("partyId"), null, null);
		
		branchpartyIdsList = EntityUtil.getFieldListFromEntityListIterator(PartyContactDetailByDistrict, "partyId", true);
	
		
	}
	
	
	List condList = [];
	if((branchpartyIdsList || branchId || partyClassification || isDepot || state || district) && !partyId)
	condList.add(EntityCondition.makeCondition("partyId" ,EntityOperator.IN, branchpartyIdsList));
	else if(UtilValidate.isNotEmpty(partyId))
		condList.add(EntityCondition.makeCondition("partyId" ,EntityOperator.EQUALS, partyId));
	
		
	
	if(branchList.size() == 1){
		condList.add(EntityCondition.makeCondition("partyIdFrom" ,EntityOperator.EQUALS, branchId));
	}
		
		
	condList.add(EntityCondition.makeCondition("roleTypeId" ,EntityOperator.EQUALS, "EMPANELLED_CUSTOMER"));
	condList.add(EntityCondition.makeCondition("partyIdentificationTypeId" ,EntityOperator.EQUALS,"PSB_NUMER"));
	
	if(UtilValidate.isNotEmpty(passbookNumber) && UtilValidate.isNotEmpty(passGreater)){
		condList.add(EntityCondition.makeCondition("idValue" ,EntityOperator.GREATER_THAN, passbookNumber));
	}else if(UtilValidate.isNotEmpty(passbookNumber)){
	   condList.add(EntityCondition.makeCondition("idValue" ,EntityOperator.EQUALS, passbookNumber));
	}
	
	cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	
	fieldsToSelect = ["partyId","idValue","partyIdFrom"] as Set;
	
	List<String> payOrderBy = UtilMisc.toList("idValue");
	
	
	resultList = delegator.find("PartyRelationAndIdentificationAndPerson", cond, null, fieldsToSelect, payOrderBy, null);
	
	partyList = resultList.getPartialList(Integer.valueOf(parameters.low),Integer.valueOf(parameters.high)-Integer.valueOf(parameters.low));
	
	
	
	if(!uniqueOrderIdsList){
		fieldsToSelect = ["partyId"] as Set;
		forIndentsCount = delegator.find("PartyRelationAndIdentificationAndPerson", cond, null, fieldsToSelect, null, null);
		totalIndents = forIndentsCount.size();
	}
	


partyIdRepeat = []as HashSet;
partyList.each{ partyList ->
	
	JSONObject tempData = new JSONObject();
	
	partyId = "";
	
	
	
	partyClassification = parameters.partyClassification;
	
	partyId = partyList.partyId;
	
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, partyId));
	conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "DEPOT_SOCIETY"));
	fcond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	
	FacilityList = delegator.findList("Facility", fcond, UtilMisc.toSet("facilityId"), null, null, false);
	
	
	isDepots = "";
	if(FacilityList)
	isDepots ="Y"
	else
	isDepots ="N"

	
	if(!partyIdRepeat.contains(partyId)){
	
		partyIdRepeat.add(partyId);
		
	tempData.put("partyId", partyId);
	String partyName = PartyHelper.getPartyName(delegator,partyId,false);
	
	
	if(partyName)
	tempData.put("partyName", partyName);
	else
	tempData.put("partyName", "");
	
	tempData.put("totalIndents", totalIndents);
	
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId));
	conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
	conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "EMPANELLED_CUSTOMER"));
	conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
	conditionList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()),
		EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)],EntityOperator.OR));
	
	fcondP = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	
	PartyRelationship = delegator.findList("PartyRelationship", fcondP, null, null, null, false);

	
	branchName = "";
	
	if(PartyRelationship.size() !=1){
		
		int i=0;
		for (eachEbranchList in PartyRelationship) {
			
			if(i!=0 && i != PartyRelationship.size())
			branchName = branchName +","+ PartyHelper.getPartyName(delegator, eachEbranchList.partyIdFrom, false);
			else
			branchName = branchName + PartyHelper.getPartyName(delegator, eachEbranchList.partyIdFrom, false);
			
			i++;
		}
	}else{
	  branchName = branchName + PartyHelper.getPartyName(delegator, partyList.partyIdFrom, false);
	}
	
	
	tempData.put("branchName", branchName);
	
	tempData.put("passbookNo", partyList.idValue);
	
	
	partyClassificationList = delegator.findList("PartyClassification", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), UtilMisc.toSet("partyClassificationGroupId"), null, null,false);
	
	partyClassification = "";
	if(partyClassificationList){
		partyClassificationGroupId = partyClassificationList[0].get("partyClassificationGroupId");
		PartyClassificationGroup = delegator.findOne("PartyClassificationGroup",[partyClassificationGroupId : partyClassificationGroupId] , false);
		
		if(PartyClassificationGroup)
		partyClassification = PartyClassificationGroup.get("description");
		
	 }
	
	tempData.put("partyClassification", partyClassification);
	
	
	//tempData.put("isDepot", isDepots);
	
	
	/*conditionListParty=[];
	conditionListParty.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId));
	Pcond = EntityCondition.makeCondition(conditionListParty, EntityOperator.AND);
	List partyLoomDetails = delegator.findList("PartyLoom",Pcond,UtilMisc.toSet("partyId","loomTypeId","quantity"),null,null,false);
	
*/	
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId));
	conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, facilityDateEnd));
	conditionList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, facilityDateEnd),
		EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)],EntityOperator.OR));
	
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	partyLoomDetails = delegator.findList("PartyLoom",condition,null,null,null,false);
	
	
	
	
	int totalLooms = 0;
	//String loomDetail = "";
	
	JSONArray loomDetail = new JSONArray();
	JSONArray partyLoomArrayJSON = new JSONArray();
	
	if(partyLoomDetails){
		partyLoomDetails.each{ eachPartyLoom ->
		
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("loomTypeId", EntityOperator.EQUALS,eachPartyLoom.loomTypeId));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		LoomTypeDetails = EntityUtil.getFirst(delegator.findList("LoomType",condition,UtilMisc.toSet("description"),null,null,false));
		
		if(LoomTypeDetails){
		//loomDetail = loomDetail+(LoomTypeDetails.description+" : "+Math.round(eachPartyLoom.quantity)+"   ");
		
			JSONObject newObj = new JSONObject();
			newObj.put("description",LoomTypeDetails.description);
			newObj.put("quantity",eachPartyLoom.quantity);
			loomDetail.add(newObj);
			
		}	
		
		resultCtx = dispatcher.runSync("getPartyAvailableQuotaBalanceHistory",UtilMisc.toMap("userLogin",userLogin, "partyId", partyId,"effectiveDate",facilityDateEnd));
		productCategoryQuotasMap = resultCtx.get("schemesMap");
		usedQuotaMap = resultCtx.get("usedQuotaMap");
		eligibleQuota = resultCtx.get("eligibleQuota");
		
		
		
		
		//start===============================================================
		/*quotaQuantity = 0;
		tenPerValue = 0;
		invoiceValue = 0;
		invoiceGrossValue = 0;
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS,eachPartyLoom.loomTypeId));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			
		productIdsList = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductCategoryAndMember",condition, UtilMisc.toSet("productId"), null, null, false), "productId", true);
		
		if(partyId){
			condList.clear();
			condList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			condList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "SALES_INVOICE"));
			condList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
			condList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.IN, ["YARN_SALE","DEPOT_YARN_SALE"]));
			condList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, facilityDateEnd));
			
			cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
			
			fieldsToSelect = ["invoiceId"] as Set;
			
			
			//invoice = delegator.findList("Invoice", cond, fieldsToSelect, null, null, false);
			
			invoiceIdsList = EntityUtil.getFieldListFromEntityList(delegator.findList("Invoice",cond, UtilMisc.toSet("invoiceId"), null, null, false), "invoiceId", true);
			
			
		
			if(invoiceIdsList){
				condList.clear();
				condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIdsList));
				condList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "TEN_PERCENT_SUBSIDY"));
				condList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIdsList));
				invoiceItemcond = EntityCondition.makeCondition(condList, EntityOperator.AND);
				
				InvoiceTenItem = delegator.findList("InvoiceItem", invoiceItemcond, UtilMisc.toSet("invoiceId"), null, null, false);
				
				invoicetenIds = EntityUtil.getFieldListFromEntityList(InvoiceTenItem,"invoiceId", true);
				
				//Debug.log("invoiceIdsList======="+eachPartyLoom.loomTypeId+"=========="+invoicetenIds);
				
				condList.clear();
				condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoicetenIds));
				condList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIdsList));
				invoiceItemcond = EntityCondition.makeCondition(condList, EntityOperator.AND);
				
				InvoiceItem = delegator.findList("InvoiceItem", invoiceItemcond, UtilMisc.toSet("invoiceId","invoiceItemSeqId","itemValue","invoiceItemTypeId","amount","quantity"), null, null, false);
				
				
				//Debug.log("InvoiceItem=================="+InvoiceItem.size());
				
				for (eachItem in InvoiceItem) {
				
					if(eachItem.invoiceItemTypeId == "TEN_PERCENT_SUBSIDY"){
					condList.clear();
					condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachItem.invoiceId));
					condList.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS, eachItem.invoiceItemSeqId));
					orderAjBillCon = EntityCondition.makeCondition(condList, EntityOperator.AND);
					
					OrderAdjustmentBilling = delegator.findList("OrderAdjustmentBilling", orderAjBillCon, UtilMisc.toSet("quantity"), null, null, false);
			
					quotaQuantity = quotaQuantity + OrderAdjustmentBilling[0].quantity;
					
					tenPerValue = tenPerValue+Math.round(eachItem.amount*eachItem.quantity);
					
					//invoiceValue = invoiceValue + Math.round(eachItem.amount*eachItem.quantity);
					
					}else{
					
					invoiceValue = invoiceValue + Math.round(eachItem.amount*eachItem.quantity);
					
					}
					
					
				}
			}
		}*/
		//end====================================================
		
		
		
		
		
		
		
		
		
		
		
		
		Desc = eachPartyLoom.loomTypeId;
		
		//invoiceGrossValue = invoiceValue + tenPerValue;
		
		JSONObject partyLoomJSON = new JSONObject();
		partyLoomJSON.put("loomType",Desc);
		partyLoomJSON.put("loomQuota",eligibleQuota.get(Desc));
		partyLoomJSON.put("availableQuota",productCategoryQuotasMap.get(Desc));
		partyLoomJSON.put("usedQuota",usedQuotaMap.get(Desc));
		
		/*partyLoomJSON.put("quotaQuantity",quotaQuantity);
		partyLoomJSON.put("tenPerValue",tenPerValue);
		partyLoomJSON.put("invoiceValue",invoiceValue);
		partyLoomJSON.put("invoiceGrossValue",invoiceGrossValue);*/
		
		partyLoomArrayJSON.add(partyLoomJSON);
			
		}
	}
	
	
	tempData.put("loomDetail", loomDetail);
	
	tempData.put("partyLoomArrayJSON", partyLoomArrayJSON);
	
	
	weaverDetailsList.add(tempData);
	}
	
	
}
	

request.setAttribute("weaverDetailsList", weaverDetailsList);
return "success";

