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

if(parameters.partyId){
	address1="";
	address2="";
	state="";
	city="";
	postalCode="";
contactMechesDetails = ContactMechWorker.getPartyContactMechValueMaps(delegator, parameters.partyId, false,"POSTAL_ADDRESS");
//Debug.log("contactMechesDetails======================="+contactMechesDetails);
if(contactMechesDetails){
	contactMec=contactMechesDetails.getLast();
	if(contactMec){
		partyPostalAddress=contactMec.get("postalAddress");
		//Debug.log("partyPostalAddress=========================="+partyPostalAddress);
	//	partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:invoicePartyId, userLogin: userLogin]);
		if(partyPostalAddress){
			
			if(partyPostalAddress.get("address1")){
			address1=partyPostalAddress.get("address1");
			//Debug.log("address1=========================="+address1);
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
			
			
		
			
			//Debug.log("shipingAdd========================="+shipingAdd);
			
		}
	}
}

conditionList=[];
conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS,parameters.partyId));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
facilityDepo = delegator.findList("Facility",condition,null,null,null,false);
Debug.log("facilityDepo======================"+facilityDepo);
String Depo="NO";
if(facilityDepo){
   Depo="YES";
   }
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,parameters.partyId));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
PartyLoomDetails = delegator.findList("PartyLoom",condition,null,null,null,false);
Debug.log("PartyLoomDetails======================"+PartyLoomDetails);
custPartyName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, parameters.partyId, false);
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
	//Debug.log("PartyLoomDetails======================"+PartyLoomDetails);
	if(LoomTypeDetails){
		type=LoomTypeDetails.loomTypeId;
		/*if(LoomTypeDetails.description){
		Desc=LoomTypeDetails.description
		}*/
		Desc +=type;
	}
	JSONObject partyLoomJSON = new JSONObject();
	
	partyLoomJSON.put("loomType",Desc);
	partyLoomJSON.put("loomQuota",loomQuota);
	partyLoomJSON.put("loomQty",loomQty);
	partyLoomArrayJSON.add(partyLoomJSON);
	}
}
psbNo="";
partyIdentification = delegator.findOne("PartyIdentification",UtilMisc.toMap("partyId", parameters.partyId, "partyIdentificationTypeId", "PSB_NUMER"), false);
if(partyIdentification){
	psbNo = partyIdentification.get("idValue");
}
partyJSON.put("psbNo",psbNo);

partyJSON.put("address1",address1);
partyJSON.put("address2",address2);
partyJSON.put("city",city);
partyJSON.put("postalCode",postalCode);
partyJSON.put("Depo",Depo);
partyJSON.put("LoomDetails",partyLoomArrayJSON);
}
context.partyJSON=partyJSON;
Debug.log("partyJSON====================="+partyJSON);
request.setAttribute("partyJSON", partyJSON);
return "success";

	

