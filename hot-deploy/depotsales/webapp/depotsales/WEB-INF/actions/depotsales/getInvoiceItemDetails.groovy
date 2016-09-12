import org.ofbiz.base.util.UtilDateTime;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;


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



JSONArray invoiceItemList =new JSONArray();

invoiceId = parameters.invoiceId;


if(UtilValidate.isNotEmpty(invoiceId)){
	
	invoiceSequence = "";
	billOfSalesInvSeqs = delegator.findList("BillOfSaleInvoiceSequence",EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS , invoiceId)  , UtilMisc.toSet("invoiceSequence"), null, null, false );
	if(UtilValidate.isNotEmpty(billOfSalesInvSeqs)){
		invoiceSeqDetails = EntityUtil.getFirst(billOfSalesInvSeqs);
		invoiceSequence = invoiceSeqDetails.invoiceSequence;
	}else{
		invoiceSequence = invoiceId;
	}
	
	
    condList = [];
	condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
	condList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL,null));
	condList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, null));
	invoiceItemcond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	
	invoiceItem = delegator.findList("InvoiceItem", invoiceItemcond, null, null, null, false);
	
	double totalAmout = 0;
	for (eachItem in invoiceItem) {
		
		tempMap = [:];
		
		tempMap.put("invoiceSequence", invoiceSequence);
		tempMap.put("invoiceId", eachItem.invoiceId);
		tempMap.put("seqId", eachItem.invoiceItemSeqId);
		tempMap.put("productId", eachItem.productId);
		tempMap.put("description", eachItem.description);
		tempMap.put("quantity", eachItem.quantity);
		tempMap.put("unitPrice", eachItem.amount);
		
		double invoItemAmt = Double.valueOf(eachItem.quantity*eachItem.amount);
		
		tempMap.put("beforeRound",invoItemAmt);
		
		double roundedInvAmt = Math.round(invoItemAmt);
		
		totalAmout = totalAmout+roundedInvAmt;
		tempMap.put("invoItemAmt",roundedInvAmt);
		tempMap.put("invoiceGrandTotal",totalAmout);
		
		invoiceItemList.add(tempMap);
		
	}
	
	
	
	Debug.log("invoiceItem======================"+invoiceItem);
	
	request.setAttribute("invoiceItem", invoiceItemList);

}

return "success";
