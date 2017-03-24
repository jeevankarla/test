import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import in.vasista.vbiz.purchase.MaterialHelperServices;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import in.vasista.vbiz.byproducts.SalesInvoiceServices;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.order.order.*;
import java.math.RoundingMode;
import org.ofbiz.party.contact.ContactMechWorker;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;




JSONObject partyJSON = new JSONObject();
JSONArray AllLoomArrayJSON = new JSONArray();

if(parameters.partyId){
	address1="";
	address2="";
	state="";
	city="";
	postalCode="";
	effectiveDate = parameters.effectiveDate;
	if(UtilValidate.isEmpty(effectiveDate)){
		effectiveDate=UtilDateTime.nowTimestamp();
	}
	else if(UtilValidate.isNotEmpty(effectiveDate)){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");
			effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDate).getTime());
		}catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + effDate, "");
			displayGrid = false;
		}
	}
contactMechesDetails = ContactMechWorker.getPartyContactMechValueMaps(delegator, parameters.partyId, false,"POSTAL_ADDRESS");
////Debug.log("contactMechesDetails======================="+contactMechesDetails);
if(contactMechesDetails){
	contactMec=contactMechesDetails.getLast();
	if(contactMec){
		partyPostalAddress=contactMec.get("postalAddress");
		////Debug.log("partyPostalAddress=========================="+partyPostalAddress);
	//	partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:invoicePartyId, userLogin: userLogin]);
		if(partyPostalAddress){
			
			if(partyPostalAddress.get("address1")){
			address1=partyPostalAddress.get("address1");
			////Debug.log("address1=========================="+address1);
			}
			if(partyPostalAddress.get("address2")){
				address2=partyPostalAddress.get("address2");
				}
			if(partyPostalAddress.get("city")){
				city=partyPostalAddress.get("city");
				}
			if(partyPostalAddress.get("state")){
				state=partyPostalAddress.get("state");
				}
			if(partyPostalAddress.get("postalCode")){
				postalCode=partyPostalAddress.get("postalCode");
				}
			
			//partyJSON.put("name",shippPartyName);
			
			
		
			
			////Debug.log("shipingAdd========================="+shipingAdd);
			
		}
	}
}

conditionList=[];
conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS,parameters.partyId));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
facilityDepo = delegator.findList("Facility",condition,null,null,null,false);
String Depo="NO";
String DAO="";

facilityType = "";


if(UtilValidate.isNotEmpty(facilityDepo)){
	
	facilityTypeIds = EntityUtil.getFieldListFromEntityList(facilityDepo, "facilityTypeId", true);
	
   Depo="YES";
	   if(UtilValidate.isNotEmpty(facilityDepo[0].openedDate)){
	   	  DAO=UtilDateTime.toDateString(facilityDepo[0].openedDate,"dd-MM-yyyy");
	   }
	   
	   if(facilityTypeIds.contains("YARN_HUB"))
	    facilityType = "(Yarn Hub)";
	   
   }

context.facilityType = facilityType;

AllLoomDetails = delegator.findList("LoomType",null,null,null,null,false);

AllLoomDetails.each{ eachloom ->
	JSONObject AllLoomsJSON = new JSONObject();
	
	desc="";
	loomType="";
	loomType=eachloom.loomTypeId;
		////Debug.log("loomType==========111============"+loomType);
		if(eachloom.description){
			desc=eachloom.description;
		}
	AllLoomsJSON.put("loomType",loomType);
	AllLoomsJSON.put("desc",desc);
	
	AllLoomArrayJSON.add(AllLoomsJSON);
	
}
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,parameters.partyId));
conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
conditionList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, effectiveDate),
	EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)],EntityOperator.OR));

condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
PartyLoomDetails = delegator.findList("PartyLoom",condition,null,null,null,false);
PartyClassificationDetails = EntityUtil.getFirst(delegator.findList("PartyClassification",condition,null,null,null,false));
partyType="";
resultCtx = dispatcher.runSync("getPartyAvailableQuotaBalanceHistory",UtilMisc.toMap("userLogin",userLogin, "partyId", parameters.partyId,"effectiveDate",effectiveDate));
productCategoryQuotasMap = resultCtx.get("schemesMap");
usedQuotaMap = resultCtx.get("usedQuotaMap");
eligibleQuota = resultCtx.get("eligibleQuota");
if(PartyClassificationDetails){
	PartyClassificationDetails.each{ eachPartyClassificationDetails ->
		partyClassificationGroupId=PartyClassificationDetails.get("partyClassificationGroupId");
		if(partyClassificationGroupId){
			partyClassificationGroupIdList = delegator.findOne("PartyClassificationGroup",UtilMisc.toMap("partyClassificationGroupId", partyClassificationGroupId), false);
			if(partyClassificationGroupIdList && partyClassificationGroupIdList.get("description")){
				partyType=partyClassificationGroupIdList.get("description");
			}
		}
	}
}
custPartyName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, parameters.partyId, false);
regno="";
partyRegIdentification = delegator.findOne("PartyIdentification",UtilMisc.toMap("partyId", parameters.partyId, "partyIdentificationTypeId", "REGISTRATION_NUMBER"), false);
if(partyRegIdentification){
	regno = partyRegIdentification.get("idValue");
}
if(regno){
	custPartyName=custPartyName+"[ RegNo: "+regno+"]";
}

partyJSON.put("custPartyName",custPartyName);
JSONArray partyLoomArrayJSON = new JSONArray();
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
		/*if(LoomTypeDetails.description){
		Desc=LoomTypeDetails.description
		}*/
		Desc +=type;
	}
	JSONObject partyLoomJSON = new JSONObject();
	
	partyLoomJSON.put("loomType",Desc);
	partyLoomJSON.put("loomQuota",eligibleQuota.get(Desc));
	partyLoomJSON.put("availableQuota",productCategoryQuotasMap.get(Desc));
	partyLoomJSON.put("usedQuota",usedQuotaMap.get(Desc));
	partyLoomJSON.put("loomQty",loomQty);
	partyLoomArrayJSON.add(partyLoomJSON);
	}
}
psbNo="";
issueDate="";
partyIdentification = delegator.findOne("PartyIdentification",UtilMisc.toMap("partyId", parameters.partyId, "partyIdentificationTypeId", "PSB_NUMER"), false);
if(partyIdentification){
	psbNo = partyIdentification.get("idValue");
	if(UtilValidate.isNotEmpty(partyIdentification.get("issueDate"))){
		issueDate=UtilDateTime.toDateString(partyIdentification.issueDate,"dd-MM-yyyy");
	}
}


panNo="";
partyRegIdentification = delegator.findOne("PartyIdentification",UtilMisc.toMap("partyId", parameters.partyId, "partyIdentificationTypeId", "PAN_NUMBER"), false);
if(partyRegIdentification){
	panNo = partyRegIdentification.get("idValue");
}

resultCtx = dispatcher.runSync("getCustomerBranch",UtilMisc.toMap("userLogin",userLogin, "partyId", parameters.partyId));
	
	productStoreIds=[];
	productStoreDetails = resultCtx.get("productStoreList");
	//productStoreDetails = delegator.findList("ProductStore", EntityCondition.makeCondition("productStoreId", EntityOperator.NOT_IN,UtilMisc.toList("1003","1012","9000","STORE") ), null,null,null, false);
	productStoreIds = EntityUtil.getFieldListFromEntityList(productStoreDetails, "productStoreId", true);
	
	storeSize = 0;
	if(productStoreIds.size() == 1){
		partyJSON.put("productStoreId",productStoreIds.get(0));
		storeSize = 1;
	}else{
	JSONArray productStoreJSON = new JSONArray();
		productStoreIds.each{ productStore ->
				JSONObject newObj = new JSONObject();
				newObj.put("value",productStore);
				newObj.put("label",productStore);
				productStoreJSON.add(newObj);
		}
	storeSize = productStoreIds.size();
	partyJSON.put("productStoreJSON",productStoreJSON);
	
	}

partyJSON.put("storeSize",storeSize);
partyJSON.put("psbNo",psbNo);
partyJSON.put("panNo",panNo);
partyJSON.put("address1",address1);
partyJSON.put("address2",address2);
partyJSON.put("city",city);
partyJSON.put("postalCode",postalCode);
partyJSON.put("Depo",Depo);
partyJSON.put("facilityType",facilityType);
partyJSON.put("DAO",DAO);
partyJSON.put("issueDate",issueDate);
partyJSON.put("partyType",partyType);
partyJSON.put("LoomDetails",partyLoomArrayJSON);
partyJSON.put("LoomList",AllLoomArrayJSON);

}
context.partyJSON=partyJSON;
////Debug.log("partyJSON====================="+partyJSON);
request.setAttribute("partyJSON", partyJSON);
return "success";

	

