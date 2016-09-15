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
	//condList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, null));
	invoiceItemcond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	
	invoiceItem = delegator.findList("InvoiceItem", invoiceItemcond, null, null, null, false);
	
	double totalAmout = 0;
	for (eachItem in invoiceItem) {
		
		tempMap = [:];
		
		BigDecimal decimal  = BigDecimal.ZERO;
		
		
		tempMap.put("invoiceSequence", invoiceSequence);
		tempMap.put("invoiceId", eachItem.invoiceId);
		tempMap.put("seqId", eachItem.invoiceItemSeqId);
		if(eachItem.productId)
		tempMap.put("productId", eachItem.productId);
		else
		tempMap.put("productId", "");
		
		tempMap.put("description", eachItem.description);
		tempMap.put("quantity", eachItem.quantity);
		tempMap.put("unitPrice", eachItem.amount);
		
	     invoItemAmt = eachItem.quantity*eachItem.amount;
		
		 
		tempMap.put("beforeRound",invoItemAmt);
		
		
		/*inputCtx = [:];
		inputCtx.put("userLogin",userLogin);
		inputCtx.put("invoiceItemTypeId", eachItem.invoiceItemTypeId);
		inputCtx.put("amount", invoItemAmt);
		try{
		 resultCtx = dispatcher.runSync("getInvoiceItemTypeDecimals", inputCtx);
		 
		 
		 Debug.log("resultCtx============"+resultCtx);
		 
		 
		 if(resultCtx.amount)
		 invoItemAmt = resultCtx.amount;
		 
		 Debug.log("decimal============"+decimal);
		 
		 
		}catch(Exception e){}
	*/	
		
		double roundedInvAmt = Math.round(invoItemAmt);
		
	/*	BigDecimal newPrice = new BigDecimal(100);
		
		
		Debug.log("Given=============="+newPrice);
		
  			int places = 100;
  			int decimal = 2;
			roundingType = "ROUND";
			  
  			BigDecimal roundedAmount = BigDecimal.ZERO;
  			
  	     	if((places == 0  && roundingType == "ROUND_UP" ))
  		      roundedAmount = (newPrice.setScale(decimal, BigDecimal.ROUND_UP));
		    if((places == 0  && roundingType == "ROUND_DOWN" ))
			  roundedAmount = (newPrice.setScale(decimal, BigDecimal.ROUND_DOWN));
		    if((places == 0  && roundingType == "ROUND" ))
			  roundedAmount = (newPrice.setScale(decimal, rounding));
	
			 if(places == 1 && roundingType == "ROUND" )
			   roundedAmount = (newPrice.setScale(0, rounding));
			 if(places == 1 && roundingType == "ROUND_DOWN" )
				 roundedAmount = (newPrice.setScale(0, BigDecimal.ROUND_DOWN));
			 if(places == 1 && roundingType == "ROUND_UP" )
				 roundedAmount = (newPrice.setScale(0, BigDecimal.ROUND_UP));
			  
			
			  
			  if(places == 10 && roundingType == "ROUND"){
				  
				  newPrice = (newPrice.setScale(0, rounding));
				  int x = 10 - ( newPrice.intValue() % 10);
				  if(x<=5)
				  roundedAmount = newPrice.add(x)
				  else
				  roundedAmount = (newPrice.add(x)).subtract(10);
			  }else if(places == 10 && roundingType == "ROUND_UP"){
			  
				  newPrice = (newPrice.setScale(0, rounding));
				  int x = 10 - ( newPrice.intValue() % 10);
				  
				  if(newPrice != x)
				  roundedAmount = newPrice.add(x)
				  else
				  roundedAmount = newPrice;
				  
			  
			  }else if(places == 10 && roundingType == "ROUND_DOWN"){
			  
				  newPrice = (newPrice.setScale(0, rounding));
				  int x = 10 - ( newPrice.intValue() % 10);
				  
				  if(newPrice != x)
				  roundedAmount = (newPrice.add(x)).subtract(10);
				  else
				  roundedAmount = newPrice;
			  }
			  
			  if(places == 100 && roundingType == "ROUND"){
				  
				  newPrice = (newPrice.setScale(0, rounding));
				  int x = 100 - ( newPrice.intValue() % 100);
				  if(x<=50)
				  roundedAmount = newPrice.add(x)
				  else
				  roundedAmount = (newPrice.add(x)).subtract(100);
			  }
			  if(places == 100 && roundingType == "ROUND_UP"){
				  
				  newPrice = (newPrice.setScale(0, rounding));
				  int x = 100 - ( newPrice.intValue() % 100);
				  if(newPrice != x)
				  roundedAmount = newPrice.add(x)
				  else
				  roundedAmount = newPrice;
			  }
			  if(places == 100 && roundingType == "ROUND_DOWN"){
				  
				  newPrice = (newPrice.setScale(0, rounding));
				  int x = 100 - ( newPrice.intValue() % 100);
				  
				  if(newPrice != x)
				  roundedAmount = (newPrice.add(x)).subtract(100);
				  else
				  roundedAmount = newPrice;
			  }
			  
			  
			  
			  
			  Debug.log("output=============="+roundedAmount);
			  
			  
			  
	//	BigDecimal newPrice = new BigDecimal(44.50);
		
		
		
		double tenthPower = Math.floor(Math.log10(newPrice));
		double place = Math.pow(10, tenthPower);
		
		
		BigDecimal price = (newPrice.setScale(0, rounding));
		
		int x = 10 - ( price.intValue() % 10);	
		
		BigDecimal finalVal = BigDecimal.ZERO;
		if(x<=5)
		finalVal = price.add(x)
		else
		finalVal = (price.add(x)).subtract(10);
		
		
		
		///============round UP===========
		
		
		//==============================================
        		
		int y =100 - ( price.intValue() % 100);
		Debug.log("y================="+y);
		BigDecimal finalVal100 = BigDecimal.ZERO;
		if(y<=50){
		finalVal100 = price.add(y)
		}else{
		finalVal100 = (price.add(y));
		finalVal100 = finalVal100.subtract(100);
		}
		
		//totalAmout = totalAmout+roundedInvAmt;
*/		
		tempMap.put("invoItemAmt",roundedInvAmt);
		tempMap.put("invoiceGrandTotal",totalAmout);
		
		invoiceItemList.add(tempMap);
		
	}
	//Debug.log("invoiceItem======================"+invoiceItem);
	request.setAttribute("invoiceItem", invoiceItemList);

}

return "success";
