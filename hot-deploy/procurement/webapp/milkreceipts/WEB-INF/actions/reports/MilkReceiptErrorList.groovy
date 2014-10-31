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
import org.ofbiz.network.NetworkServices;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import javax.rmi.CORBA.Util;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;


	fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
	def sdf = new SimpleDateFormat("MMMM dd, yyyy");
	try {
		if (parameters.fromDate) {
			fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
		}	  
	}catch (ParseException e) {
	   context.errorMessage = "Cannot parse date string: " + e;
	   return;
	}
	dayStart = UtilDateTime.getDayStart(fromDate);
	dayEnd= UtilDateTime.getDayEnd(fromDate);
	context.put("fromDate", fromDate);
	nextDay=UtilDateTime.addDaysToTimestamp(fromDate, 1);
	dctx = dispatcher.getDispatchContext();
	context.put("dctx",dctx);
// Milk Transfer for Duplicate Entries
	conList=[];
	conList.add(EntityCondition.makeCondition("facilityIdTo", EntityOperator.EQUALS , "MAIN_PLANT"));
	conList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "MXF_RECD"));
	conList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart)]));
	conList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
	EntityCondition cond = EntityCondition.makeCondition(conList,EntityOperator.AND);
	milkTrnsferList = delegator.findList("MilkTransfer",cond,null,null,null,false);
	List tankerNumberList=FastList.newInstance();
	List duplicateTransferIds=FastList.newInstance();
	List facilityIdsList=FastList.newInstance();
	if(UtilValidate.isNotEmpty(milkTrnsferList)){
		milkTrnsferList.each{ mlkTrnsf->
			milkTransferId= mlkTrnsf.get("milkTransferId");
			tankerNo= mlkTrnsf.get("containerId");		
			facilityId= mlkTrnsf.get("facilityId");
			if(tankerNumberList.contains(tankerNo) && facilityIdsList.contains(facilityId)){
				duplicateTransferIds.add(milkTransferId);
			}			
			tankerNumberList.add(tankerNo);
			facilityIdsList.add(facilityId);
		}
	}
	milkDetailslist=[];	
	List conditionList=FastList.newInstance();
	conditionList.add(EntityCondition.makeCondition("facilityIdTo", EntityOperator.EQUALS , "MAIN_PLANT"));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "MXF_RECD"));
	conditionList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart)]));
	conditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	milkDetailslist = delegator.findList("MilkTransferAndMilkTransferItem",condition,null,null,null,false);
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("shedId", EntityOperator.EQUALS, "_NA_"));
	conditionList.add(EntityCondition.makeCondition("validationTypeId", EntityOperator.EQUALS, "SNFFAT_CHECK"));
	ruleCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	validationRuleList =delegator.findList("ProcBillingValidationRule", ruleCondition, null, null, null,false);
	 
	//getting qty variation
	List qtyConList=FastList.newInstance();
	qtyConList.add(EntityCondition.makeCondition("shedId", EntityOperator.EQUALS, "_NA_"));
	qtyConList.add(EntityCondition.makeCondition("validationTypeId", EntityOperator.EQUALS, "QTY_VALIDATION"));
	qtyCond = EntityCondition.makeCondition(qtyConList, EntityOperator.AND);
	qtyValidationRuleList =delegator.findList("ProcBillingValidationRule", qtyCond, null, null, null,false);		
	
	fatErrorList=[]; 
	snfErrorList=[];
	qtyErrorList=[];
	sourProdErrorList=[];
	skimmedProdErrorList=[];
	clrErrorList=[];
	finalQtyMap=[:];
	duplicateEntriesList =[];
	if(UtilValidate.isNotEmpty(milkDetailslist)){
		milkDetailslist.each{ milkDetails->			
			
			BigDecimal sendFat= BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty(milkDetails.fat)){
				sendFat= milkDetails.fat;
			}
			BigDecimal sendSnf= BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty(milkDetails.snf)){
				sendSnf= milkDetails.snf;
			}
		    BigDecimal itemFat= BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty(milkDetails.receivedFat)){
				itemFat= milkDetails.receivedFat;
			}
			BigDecimal itemSnf= BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty(milkDetails.receivedSnf)){
				itemSnf= milkDetails.receivedSnf;
			}
			
			BigDecimal sendClr= BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty(milkDetails.sendLR)){
				sendClr= milkDetails.sendLR;
			}
			BigDecimal recClr=BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty(milkDetails.receivedLR)){
				recClr= milkDetails.receivedLR;
			}
			
			milkTranferId=milkDetails.milkTransferId;
			productId= milkDetails.receivedProductId;
			facilityId=milkDetails.getString("facilityId");			
			if(UtilValidate.isNotEmpty(duplicateTransferIds)&& duplicateTransferIds.contains(milkTranferId)){
				duplicateEntriesList.add(milkDetails);
			}
			
			facilityDetails = delegator.findOne("Facility", ["facilityId" :facilityId], true);			
			facilityAttribute = delegator.findOne("FacilityAttribute", ["facilityId" :facilityDetails.get("parentFacilityId"), "attrName":"enableQuantityKgs"], true);
			String facilityAttrValue= "N";
			if(UtilValidate.isNotEmpty(facilityAttribute)){
				  facilityAttrValue = facilityAttribute.attrValue;
			}			
			sendQty=milkDetails.get("quantityLtrs");
			if("Y".equalsIgnoreCase(facilityAttrValue)){
			   sendQty=milkDetails.get("quantity");
			}
			RecQty=milkDetails.get("receivedQuantityLtrs");
			if(UtilValidate.isNotEmpty(qtyValidationRuleList)){
				qtyValidationRule= EntityUtil.getFirst(qtyValidationRuleList);
				minClrRange=qtyValidationRule.get("minFat");
				maxClrRange=qtyValidationRule.get("maxFat");
				
				if(((UtilValidate.isNotEmpty(minClrRange) && (recClr.compareTo(BigDecimal.ZERO) !=0) && UtilValidate.isNotEmpty(maxClrRange)) &&
					( (minClrRange.compareTo(recClr) >0) || (maxClrRange.compareTo(recClr) <=0))) || ((UtilValidate.isNotEmpty(minClrRange) && (sendClr.compareTo(BigDecimal.ZERO) !=0) && UtilValidate.isNotEmpty(maxClrRange)) &&
					( (minClrRange.compareTo(sendClr) >0) || (maxClrRange.compareTo(sendClr) <=0)))){
					clrErrorList.add(milkDetails);
				}				
			}
			diffClr=recClr-sendClr;			
			if(UtilValidate.isNotEmpty(diffClr) && (diffClr>1)){
				clrErrorList.add(milkDetails);
			}
			//sour product Errors
			if(UtilValidate.isNotEmpty(productId)&& "999".equals(productId)){
				sourProdErrorList.add(milkDetails);
			}
			if(UtilValidate.isNotEmpty(productId)&& (("221".equals(productId)) || ("121".equals(productId)))){
				skimmedProdErrorList.add(milkDetails);
			}			
			entryValidationRule =  EntityUtil.filterByAnd(validationRuleList, UtilMisc.toMap("productId",productId));
			if(UtilValidate.isNotEmpty(entryValidationRule)){
				validationRule =  EntityUtil.getFirst(entryValidationRule);
				BigDecimal minFat = validationRule.getBigDecimal("minFat");
		    	BigDecimal maxFat = validationRule.getBigDecimal("maxFat");
		    	BigDecimal minSnf = validationRule.getBigDecimal("minSnf");
		    	BigDecimal maxSnf = validationRule.getBigDecimal("maxSnf");
		    	BigDecimal maxSFat = validationRule.getBigDecimal("maxSFat");
				
				if(((UtilValidate.isNotEmpty(minFat) && (itemFat.compareTo(BigDecimal.ZERO) !=0) && UtilValidate.isNotEmpty(maxFat)) &&
					( (minFat.compareTo(itemFat) >0) || (maxFat.compareTo(itemFat) <=0))) || ((UtilValidate.isNotEmpty(minFat) && (sendFat.compareTo(BigDecimal.ZERO) !=0) && UtilValidate.isNotEmpty(maxFat)) &&
					( (minFat.compareTo(sendFat) >0) || (maxFat.compareTo(sendFat) <=0)))){
					fatErrorList.add(milkDetails);					
				}
				fatDiff=itemFat-sendFat;
				if(UtilValidate.isNotEmpty(fatDiff) && (fatDiff>0.20)){
					fatErrorList.add(milkDetails);
				}	
				if(((UtilValidate.isNotEmpty(minSnf) && (itemSnf.compareTo(BigDecimal.ZERO) !=0) && UtilValidate.isNotEmpty(maxSnf)) &&
						( (minSnf.compareTo(itemSnf) >0) ||	(maxSnf.compareTo(itemSnf) <=0))) || ((UtilValidate.isNotEmpty(minSnf) && (sendSnf.compareTo(BigDecimal.ZERO) !=0) && UtilValidate.isNotEmpty(maxSnf)) &&
						( (minSnf.compareTo(sendSnf) >0) ||	(maxSnf.compareTo(sendSnf) <=0)))){
					snfErrorList.add(milkDetails);					
				}	
				snfDiff=itemSnf-sendSnf;
				if(UtilValidate.isNotEmpty(snfDiff) &&(snfDiff>1)){
					fatErrorList.add(milkDetails);
				}
														
			}				
			Map milkQtyMap = FastMap.newInstance();
			milkQtyMap.put("send",sendQty);
			milkQtyMap.put("recd",RecQty);
			milkQtyMap.put("productId",productId);
			milkQtyMap.put("milkCondition",milkDetails.milkCondition);
			milkQtyMap.put("vehicleId",milkDetails.vehicleId);
			milkQtyMap.put("facilityId",milkDetails.facilityId);
			milkQtyMap.put("milkTranferId",milkTranferId);
			if(UtilValidate.isEmpty(finalQtyMap.get(milkTranferId))){				
				finalQtyMap.put(milkTranferId, milkQtyMap);
			}
			else{
				Map tempQtyMap=FastMap.newInstance();
				tempQtyMap=finalQtyMap.get(milkTranferId);
				tempQtyMap.put("send",tempQtyMap.get("send")+sendQty);
				tempQtyMap.put("recd",tempQtyMap.get("recd")+RecQty);
				finalQtyMap.put(milkTranferId, tempQtyMap);
			}
		}
	}
	
if(UtilValidate.isNotEmpty(finalQtyMap)){
	for(key in finalQtyMap.keySet()){
		sentQty =finalQtyMap.get(key).getAt("send");
		recdQty=finalQtyMap.get(key).getAt("recd");
		if(UtilValidate.isNotEmpty(qtyValidationRuleList)){
			qtyValidationRule= EntityUtil.getFirst(qtyValidationRuleList);
			validQtyDiff=qtyValidationRule.get("quantity");
			qtyRecdDiff=recdQty-sentQty;			
			if(UtilValidate.isNotEmpty(validQtyDiff) && (qtyRecdDiff>=validQtyDiff)){
				qtyErrorList.add(finalQtyMap.get(key));
			}
		}
		
	}
}	
context.putAt("fatErrorList", fatErrorList);
context.putAt("snfErrorList", snfErrorList);
context.putAt("qtyErrorList", qtyErrorList);
context.putAt("sourProdErrorList", sourProdErrorList);
context.putAt("skimmedProdErrorList", skimmedProdErrorList);
context.putAt("duplicateEntriesList", duplicateEntriesList);
context.putAt("clrErrorList", clrErrorList);
	