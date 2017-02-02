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



rounding = UtilNumber.getBigDecimalRoundingMode("orderHeader.rounding");



JSONArray invoiceItemList =new JSONArray();

selectedInvoices = parameters.selectedInvoices;


selectedInvoicesList = Eval.me(selectedInvoices)

if(UtilValidate.isNotEmpty(selectedInvoicesList)){
	
	
	condList = [];
	condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, selectedInvoicesList));
	condList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL,null));
	//condList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, null));
	invoiceItemcond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	
	invoiceItem = delegator.findList("InvoiceItem", invoiceItemcond, null, null, null, false);
 
	double totalAmout = 0;
	BigDecimal totalQuantity = 0;
	
	for (eachItem in invoiceItem) {
		if(UtilValidate.isNotEmpty(eachItem.itemValue)){
			totalAmout = totalAmout+eachItem.itemValue;
		}else{		
		  totalAmout = totalAmout+Math.round(eachItem.amount*eachItem.quantity);
		}
		 if(eachItem.invoiceItemTypeId == "INV_FPROD_ITEM" || eachItem.invoiceItemTypeId == "INV_RAWPROD_ITEM"){
			 
			 if(eachItem.quantity)
			 totalQuantity = totalQuantity.add(eachItem.quantity);
		 }
	}
	 totalQuantity = (totalQuantity.setScale(2, rounding));
	JSONObject tempMap = new JSONObject();
	
	tempMap.put("totalAmout", totalAmout);
	tempMap.put("totalQuantity", totalQuantity);
	
	invoiceItemList.add(tempMap);
	
	
	request.setAttribute("invoiceItemList", invoiceItemList);

}

return "success";