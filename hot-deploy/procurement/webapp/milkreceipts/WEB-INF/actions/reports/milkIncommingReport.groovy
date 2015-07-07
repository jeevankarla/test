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

milkIncomeDate = parameters.milkIncomeDate;
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
}
milkTransferMap=[:];
conditionList =[];

if(UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(dcNo)){
	context.dcNo=dcNo;	context.partyId=partyId;

	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
	conditionList.add(EntityCondition.makeCondition("dcNo", EntityOperator.EQUALS, dcNo));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , ["MXF_RECD","MXF_APPROVED"]));
	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	
	MilkTransferList = delegator.findList("MilkTransferAndMilkTransferItem", condition, null,null, null, false);
	if(UtilValidate.isNotEmpty(MilkTransferList)){
	MilkTransferList=EntityUtil.getFirst(MilkTransferList);
	containerId=MilkTransferList.containerId;
	sendDate=MilkTransferList.sendDate;
	receiveDate=MilkTransferList.receiveDate;
	sendTime=MilkTransferList.sendTime;
	ackTime=MilkTransferList.ackTime;
	
	sendQtyKgs=MilkTransferList.quantity;
	receivedQuantity=MilkTransferList.receivedQuantity;
	grossWeight	=MilkTransferList.grossWeight;
	tareWeight	=MilkTransferList.tareWeight;
	milkTransferId	=MilkTransferList.milkTransferId;
	partyId	=MilkTransferList.partyId;
	receivedProductId=MilkTransferList.receivedProductId;
	sendProductId=MilkTransferList.sendProductId;
	receivedProductId=MilkTransferList.receivedProductId;
	sendTemparature=MilkTransferList.sendTemparature;
	receivedTemparature=MilkTransferList.receivedTemparature;
	sendAcidity	=MilkTransferList.sendAcidity;
	receivedAcidity=MilkTransferList.receivedAcidity;
	sendLR=MilkTransferList.sendLR;
	receivedLR=MilkTransferList.receivedLR;
	fat=MilkTransferList.fat;
	receivedFat=MilkTransferList.receivedFat;
	snf	=MilkTransferList.snf;
	receivedSnf	=MilkTransferList.receivedSnf;
	
	milkTransferMap.put("containerId", containerId);
	milkTransferMap.put("sendDate", sendDate);
	milkTransferMap.put("receiveDate", receiveDate);
	milkTransferMap.put("sendTime", sendTime);
	milkTransferMap.put("ackTime", ackTime);
	milkTransferMap.put("sendQtyKgs", sendQtyKgs);
	milkTransferMap.put("receivedQuantity", receivedQuantity);
	milkTransferMap.put("grossWeight", grossWeight);
	milkTransferMap.put("tareWeight", tareWeight);
	milkTransferMap.put("milkTransferId", milkTransferId);
	milkTransferMap.put("receivedProductId", receivedProductId);
	milkTransferMap.put("sendProductId", sendProductId);
	milkTransferMap.put("receivedProductId", receivedProductId);
	milkTransferMap.put("sendTemparature", sendTemparature);
	milkTransferMap.put("receivedTemparature", receivedTemparature);
	milkTransferMap.put("sendAcidity", sendAcidity);
	milkTransferMap.put("receivedAcidity", receivedAcidity);
	milkTransferMap.put("sendLR", sendLR);
	milkTransferMap.put("receivedLR", receivedLR);
	milkTransferMap.put("fat", fat);
	milkTransferMap.put("snf", snf);
	milkTransferMap.put("receivedFat", receivedFat);
	milkTransferMap.put("receivedSnf", receivedSnf);
	  }
	partyDetailsMap=[:]
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
	}
	
	context.partyDetailsMap=partyDetailsMap;
	context.milkTransferMap=milkTransferMap;
}
