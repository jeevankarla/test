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

import javolution.util.FastMap;
import javolution.util.FastList;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import java.math.BigDecimal;
import java.math.MathContext;


rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");


dctx = dispatcher.getDispatchContext();

context.partyName = parameters.partyName;




conditionList=[];
			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, parameters.orderId));
			condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			OrderItemList = delegator.findList("OrderItem", condition, null, null, null, false);

			
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, parameters.orderId));
			conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,"SUPPLIER"));
			
						condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			orderRoleList = delegator.findList("OrderRole", condition,  UtilMisc.toSet("partyId"), null, null, false);

			supplierPartyId = "";
			supplierHindiPartyId = "";
			if(UtilValidate.isNotEmpty(orderRoleList)){
			supplierPartyId = orderRoleList[0].get("partyId");
			
			supplierHindiPartyId = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, supplierPartyId, false);
			}
			
			context.supplierPartyId = supplierPartyId;
			
			context.supplierHindiPartyId = supplierHindiPartyId;
			
			orderedHindiItemList = [];
			
			totalsList = [];
			
			totQuantity = 0;
			totannum = 0;
			
			SrNo = 1;
			
			for (eachOrderItem in OrderItemList) {
				
				 tempMap = [:];
				 productName = ""
				 prod=delegator.findOne("Product",[productId:eachOrderItem.productId],false);
				 
				 String srNoStr = SrNo;
				 char firSrno = srNoStr.charAt(0);
				 srNoStr = String.valueOf(firSrno)+srNoStr;
				 tempMap.put("SrNo", srNoStr);
				 
				 if(UtilValidate.isNotEmpty(prod)){
					 productName = prod.get("productName");
					 String productNameStr = productName;
					 char firstProChr = productNameStr.charAt(0);
					 productNameStr = String.valueOf(firstProChr)+productNameStr;
					 tempMap.put("productName", productNameStr);
					 
				 }else{
					 tempMap.put("productName", productName);
				 }
				
				 
				 
				 String qtyStr = Math.round(eachOrderItem.get("quantity"));
				 char firstChr = qtyStr.charAt(0);
				 qtyStr = String.valueOf(firstChr)+qtyStr;
				 tempMap.put("quantity", qtyStr);
				 
				 String unitStr = Math.round(eachOrderItem.get("unitPrice"));
				 char firstUChr = unitStr.charAt(0);
				 unitStr = String.valueOf(firstUChr)+unitStr;
				 
				 tempMap.put("unitPrice", unitStr);
				 
				  double annum = 0;
				 if(UtilValidate.isNotEmpty(eachOrderItem.get("quantity"))&& UtilValidate.isNotEmpty(eachOrderItem.get("unitPrice"))){
				 
					 annum = (double) (eachOrderItem.get("unitPrice")*eachOrderItem.get("quantity"));
					
					 String anumtr = String.valueOf(Math.round(annum));
					 char firstAnnuChr = anumtr.charAt(0);
					 anumtr = String.valueOf(firstAnnuChr)+anumtr;
					 
					  tempMap.put("annum", anumtr);
				 }else{
				 tempMap.put("annum", 0);
				 }
				 
				 totannum = totannum+annum;
				 totQuantity = totQuantity+eachOrderItem.get("quantity");
				 orderedHindiItemList.add(tempMap);
				 
				 
				 SrNo = SrNo+1;
				 
			}
			
			totalsMap = [:];
			
			String totQuantityStr = String.valueOf(Math.round(totQuantity));
			char firstTOTQChr = totQuantityStr.charAt(0);
			totQuantityStr = String.valueOf(firstTOTQChr)+totQuantityStr;
			totalsMap.put("totQuantity", totQuantityStr);
			
			String totannumStr = String.valueOf(Math.round(totannum));
			char firstTOTANNUQChr = totannumStr.charAt(0);
			totannumStr = String.valueOf(firstTOTANNUQChr)+totannumStr;
			
			totalsMap.put("totannum", totannumStr);
			
			totalsList.add(totalsMap);
			
			
	context.OrderItemList = OrderItemList;
	
	contextMap = UtilMisc.toMap("translateList", orderedHindiItemList);
	dayWiseEntriesLidast = (ByProductNetworkServices.icu4JTrans(dctx, contextMap)).getAt("translateList");
	
	
	contextMap = UtilMisc.toMap("translateList", totalsList);
	totalsHindiList = (ByProductNetworkServices.icu4JTrans(dctx, contextMap)).getAt("translateList");
	
	context.totalsHindiList = totalsHindiList[0];
	context.dayWiseEntriesLidast = dayWiseEntriesLidast;
