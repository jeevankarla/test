
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

import java.util.*;
import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.lang.*;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import java.sql.*;
import java.util.Calendar;

import javolution.util.FastList;
import javolution.util.FastMap;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.ofbiz.base.util.UtilNumber;

import java.math.RoundingMode;
import java.util.Map;

import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;

import in.vasista.vbiz.milkReceipts.MilkReceiptReports;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementServices;
import in.vasista.vbiz.procurement.PriceServices;
import org.ofbiz.party.party.PartyHelper;

/*milkIncomeDate = parameters.milkIncomeDate;
partyId = parameters.partyId;
dcNo = parameters.dcNo;
if(UtilValidate.isEmpty(partyId)){
	Debug.logError("partyId Cannot Be Empty","");
	context.errorMessage = "partyId Cannot Be Empty, Please Enter PartyId";
	return;
}
if(UtilValidate.isEmpty(dcNo)){
	Debug.logError("dcNo Cannot Be Empty","");
	context.errorMessage = "dcNo Cannot Be Empty, Please Enter Dc No";
	return;
}*/
weighmentId=parameters.weighmentId;
weighmentDetailMap=[:];
conditionList =[];
List weighmentPartyDetails = FastList.newInstance();
partyId=null;
if(UtilValidate.isNotEmpty(weighmentId) && !("undefined".equals(weighmentId))){
	context.weighmentId=weighmentId;
	conditionList.add(EntityCondition.makeCondition("weighmentId", EntityOperator.EQUALS, weighmentId));
	//conditionList.add(EntityCondition.makeCondition("dcNo", EntityOperator.EQUALS, dcNo));
	//conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , ["MXF_RECD","MXF_APPROVED"]));
	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	weighmentAndItemDetailsList = delegator.findList("WeighmentAndItemDetails", condition, null,null, null, false);
	if(UtilValidate.isNotEmpty(weighmentAndItemDetailsList)){
		weighmentAndItemDetail = EntityUtil.getFirst(weighmentAndItemDetailsList);
		partyId = "";partyIdTo="";
		if(UtilValidate.isNotEmpty(weighmentAndItemDetail.getString("partyId"))){
			partyId = weighmentAndItemDetail.getString("partyId");
			if(partyId =="MD"){
				List weighmentPartyList = delegator.findList("WeighmentParty",EntityCondition.makeCondition("weighmentId",EntityOperator.EQUALS,weighmentAndItemDetail.getString("weighmentId")),null,null,null,false);
				if(UtilValidate.isNotEmpty(weighmentPartyList)){
					weighmentPartyList.each{weighmentParty->
						List tempList = FastList.newInstance();
						Map tempMap = FastMap.newInstance();
						tempMap.put("partyId", weighmentParty.partyId);
						partyName =  PartyHelper.getPartyName(delegator, weighmentParty.partyId, false);
						if(UtilValidate.isNotEmpty(partyName)){
							 tempMap.put("partyName",partyName);
						}
						
						partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:weighmentParty.partyId, userLogin: userLogin]);
						  address1="";address2="";city="";postalCode="";
						   if (partyPostalAddress != null && UtilValidate.isNotEmpty(partyPostalAddress)) {
							  if(partyPostalAddress.address1){
								  address1=partyPostalAddress.address1;
								  tempMap.put("address1",address1);
							  }
							  if(partyPostalAddress.address2){
								  address2=partyPostalAddress.address2;
								  tempMap.put("address2",address2);
							   }
							  if(partyPostalAddress.city){
								  city=partyPostalAddress.city;
								  tempMap.put("city",city);
							   }
							  if(partyPostalAddress.postalCode){
								  postalCode=partyPostalAddress.postalCode;
								  tempMap.put("postalCode",postalCode);
							   }
							}
						   
						   partyTelephone= dispatcher.runSync("getPartyTelephone", [partyId: weighmentParty.partyId, userLogin: userLogin]);
						   phoneNumber = "";
						   if (partyTelephone != null && partyTelephone.contactNumber != null) {
							   phoneNumber = partyTelephone.contactNumber;
						   }
						   tempMap.put("phoneNumber", phoneNumber);
						   
						   partyEmail= dispatcher.runSync("getPartyEmail", [partyId: weighmentParty.partyId, userLogin: userLogin]);
						   emailAddress="";
						   if (partyEmail != null && partyEmail.emailAddress != null) {
							   emailAddress = partyEmail.emailAddress;
						   }
						   tempMap.put("emailAddress", emailAddress);
						   weighmentPartyDetails.add(tempMap);
					}
				}
			}else{
				Map partyDetailsMap = FastMap.newInstance();
				if(UtilValidate.isNotEmpty(partyId)){
					
					partyDetailsMap.put("partyId",partyId);
					partyName =  PartyHelper.getPartyName(delegator, partyId, false);
					if(UtilValidate.isNotEmpty(partyName)){
						 partyDetailsMap.put("partyName",partyName);
					}
					
				  partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:partyId, userLogin: userLogin]);
					address1="";address2="";city="";postalCode="";
					 if (partyPostalAddress != null && UtilValidate.isNotEmpty(partyPostalAddress)) {
						if(partyPostalAddress.address1){
					address1=partyPostalAddress.address1;
					partyDetailsMap.put("address1",address1);
						}
						if(partyPostalAddress.address2){
					address2=partyPostalAddress.address2;
					partyDetailsMap.put("address2",address2);
						 }
						if(partyPostalAddress.city){
					city=partyPostalAddress.city;
					partyDetailsMap.put("city",city);
						 }
						if(partyPostalAddress.postalCode){
					postalCode=partyPostalAddress.postalCode;
					partyDetailsMap.put("postalCode",postalCode);
						 }
					  }
					 
					 partyTelephone= dispatcher.runSync("getPartyTelephone", [partyId: partyId, userLogin: userLogin]);
					 phoneNumber = "";
					 if (partyTelephone != null && partyTelephone.contactNumber != null) {
						 phoneNumber = partyTelephone.contactNumber;
					 }
					 partyDetailsMap.put("phoneNumber", phoneNumber);
					 
					 partyEmail= dispatcher.runSync("getPartyEmail", [partyId: partyId, userLogin: userLogin]);
					 emailAddress="";
					 if (partyEmail != null && partyEmail.emailAddress != null) {
						 emailAddress = partyEmail.emailAddress;
					 }
					 partyDetailsMap.put("emailAddress", emailAddress);
					 weighmentPartyDetails.add(partyDetailsMap);
			}
		}
		partyIdTo =  weighmentAndItemDetail.getString("partyIdTo");

		vehicleId  = weighmentAndItemDetail.getString("vehicleId");
		dcNo = "";
		if(UtilValidate.isNotEmpty(weighmentAndItemDetail.getString("dcNo"))){
			dcNo = weighmentAndItemDetail.getString("dcNo");
		}
		sendDate = weighmentAndItemDetail.getTimestamp("sendDate");
		receiveDate = weighmentAndItemDetail.getTimestamp("receiveDate");
		listSize = weighmentAndItemDetailsList.size();
		context.partyIdTo = partyIdTo;
		context.vehicleId = vehicleId; 
		context.dcNo = dcNo;
		context.sendDate = sendDate;
		context.receiveDate=receiveDate;
		context.listSize = listSize;
	}
	
	}
}
context.weighmentAndItemDetailsList=weighmentAndItemDetailsList;
context.weighmentPartyDetails=weighmentPartyDetails;





