import org.ofbiz.base.conversion.NumberConverters.BigDecimalToString;
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

dctx = dispatcher.getDispatchContext();


customTimePeriodId=parameters.customTimePeriodId;
if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
	Debug.logError("customTimePeriod Cannot Be Empty","");
	context.errorMessage = "No Shed Has Been Selected.......!";
	return;
}
customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
nextDateTime = UtilDateTime.getNextDayStart(thruDateTime);
thruDate = UtilDateTime.toDateString(nextDateTime,"yyyy-MM-dd");
fromDate = UtilDateTime.toDateString(fromDateTime,"yyyy-MM-dd");
sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
shiftDayTimeStart = fromDate + " 05:00:00.000";
shiftDayTimeEnd = thruDate + " 04:59:59.000";

dayBegin = new java.sql.Timestamp(sdf.parse(shiftDayTimeStart).getTime());
dayEnd = new java.sql.Timestamp(sdf.parse(shiftDayTimeEnd).getTime());
context.fromDate = dayBegin;
context.thruDate = thruDateTime;

partyId=parameters.partyId;
if(UtilValidate.isNotEmpty(partyId)){
	partyName =  PartyHelper.getPartyName(delegator, partyId, false);
	if(UtilValidate.isNotEmpty(partyName)){
		context.partyName = partyName;
	}
	partyIdTo="MD"
	partyToName =  PartyHelper.getPartyName(delegator, partyIdTo, false);
	if(UtilValidate.isNotEmpty(partyToName)){
		context.partyToName = partyToName;
	}

	partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:partyId, userLogin: userLogin]);
	address1="";address2="";city="";postalCode="";
	 if (partyPostalAddress != null && UtilValidate.isNotEmpty(partyPostalAddress)) {
		if(partyPostalAddress.address1){
			address1=partyPostalAddress.address1;
			context.address1 = address1;
		}
		if(partyPostalAddress.address2){
			address2=partyPostalAddress.address2;
			context.address2 = address2;
		 }
		if(partyPostalAddress.city){
			city=partyPostalAddress.city;
			context.city = city;
		 }
		if(partyPostalAddress.postalCode){
			postalCode=partyPostalAddress.postalCode;
			context.postalCode = postalCode;
		 }
	  }
	 tinCstKstDetails = delegator.findList("PartyIdentification",EntityCondition.makeCondition("partyId", EntityOperator.EQUALS ,partyId)  , null, null, null, false );
	 if(UtilValidate.isNotEmpty(tinCstKstDetails)){
		 tinDetails = EntityUtil.filterByCondition(tinCstKstDetails, EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "TIN_NUMBER"));
		 tinNumber="";
		 if(UtilValidate.isNotEmpty(tinDetails)){
			 tinDetails=EntityUtil.getFirst(tinDetails);
			 tinNumber=tinDetails.idValue;
			 context.tinNumber = tinNumber;
		 }
	 }
}


containerIds=null;
conditionList =[];
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , ["MXF_RECD","MXF_APPROVED"]));
conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS , "INTERNAL"));
if(UtilValidate.isNotEmpty(partyId)){
	context.partyId = partyId;
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , partyId));
	conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS , "MD"));
	
}
conditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
conditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
List<String> orderBy = UtilMisc.toList("receiveDate");
milkTransferAndItemList = delegator.findList("MilkTransfer", condition, null, orderBy, null, false);
Set productIds=null;
if(UtilValidate.isNotEmpty(milkTransferAndItemList)){
	 productIds = new HashSet(EntityUtil.getFieldListFromEntityList(milkTransferAndItemList, "productId", false));
}
Map allProdProcPriceMap =FastMap.newInstance();


if(UtilValidate.isNotEmpty(productIds)){
	productIds.each{eachProductId->
		BigDecimal totQuantity= BigDecimal.ZERO;
		BigDecimal totAmount= BigDecimal.ZERO;
		BigDecimal price=BigDecimal.ZERO;
		Map eachProductMap =FastMap.newInstance();
		Map PremAndDeductionMap =FastMap.newInstance();
		priceChart = PriceServices.fetchPriceChartForParty(dctx, [priceDate:dayBegin, partyId: partyId, userLogin: userLogin,]);
		procPriceChartId=null;
		billQuality=null;
		if(UtilValidate.isNotEmpty(priceChart)){
			procPriceChartId=priceChart.procPriceChartId;
			billQuantity=priceChart.billQuantity;
			billQuality=priceChart.billQuality;
			vehicleId=priceChart.containerId;
		}
		procurementPriceList = delegator.findList("ProcurementPrice", EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS , procPriceChartId), null, null, null, false);
		
		fatSnfDedProcPriceList =EntityUtil.filterByCondition(procurementPriceList,EntityCondition.makeCondition("procurementPriceTypeId",EntityOperator.EQUALS,"PROC_PRICE_SLAB1"));
		if(UtilValidate.isNotEmpty(fatSnfDedProcPriceList)){
			fatSnfDedProcPriceList = EntityUtil.getFirst(fatSnfDedProcPriceList);
			price=fatSnfDedProcPriceList.price;
			snfProcPercent=fatSnfDedProcPriceList.snfPercent;
			fatProcPercent=fatSnfDedProcPriceList.fatPercent;
			
			PremAndDeductionMap.put("productId", eachProductId);
			PremAndDeductionMap.put("price", price);
			PremAndDeductionMap.put("snfProcPercent", snfProcPercent);
			PremAndDeductionMap.put("fatProcPercent", fatProcPercent);
		}
	/*	conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("procurementPriceTypeId",EntityOperator.EQUALS,"PROC_PRICE_SNF_DED"));
		conditionList.add(EntityCondition.makeCondition("price",EntityOperator.EQUALS,BigDecimal.ZERO));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		snfDedProcPriceList=EntityUtil.filterByCondition(procurementPriceList,condition);
		if(UtilValidate.isNotEmpty(snfDedProcPriceList)){
			snfDedProcPriceList = EntityUtil.getFirst(snfDedProcPriceList);
			snfDed=snfDedProcPriceList.snfPercent;
			snfDedPrice=snfDedProcPriceList.price;
			
			PremAndDeductionMap.put("snfDed", snfDed);
			PremAndDeductionMap.put("snfDedPrice", snfDedPrice);
		}*/
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("price",EntityOperator.GREATER_THAN, BigDecimal.ZERO));
		conditionList.add(EntityCondition.makeCondition("procurementPriceTypeId",EntityOperator.EQUALS,"PROC_PRICE_SNF_PRM"));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		snfPremiumProcPriceList=EntityUtil.filterByCondition(procurementPriceList,condition);
		if(UtilValidate.isNotEmpty(snfPremiumProcPriceList)){
			snfPremiumProcPriceList = EntityUtil.getFirst(snfPremiumProcPriceList);
			snfPremium=snfPremiumProcPriceList.snfPercent;
			snfPremPrice=snfPremiumProcPriceList.price;

			PremAndDeductionMap.put("snfPremium", snfPremium);
			PremAndDeductionMap.put("snfPremPrice", snfPremPrice);
		}
	/*	conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("price", EntityOperator.EQUALS, BigDecimal.ZERO));
		conditionList.add(EntityCondition.makeCondition("procurementPriceTypeId",EntityOperator.EQUALS,"PROC_PRICE_FAT_DED"));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		fatDedProcPriceList=EntityUtil.filterByCondition(procurementPriceList,condition);
		if(UtilValidate.isNotEmpty(fatDedProcPriceList)){
			fatDedProcPriceList = EntityUtil.getFirst(fatDedProcPriceList);
			fatDed=fatDedProcPriceList.fatPercent;
			fatDedPrice=fatDedProcPriceList.price;
			
			PremAndDeductionMap.put("fatDed", fatDed);
			PremAndDeductionMap.put("fatDedPrice", fatDedPrice);
		}*/
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("price", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
		conditionList.add(EntityCondition.makeCondition("procurementPriceTypeId",EntityOperator.EQUALS,"PROC_PRICE_FAT_PRM"));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		fatPremiumProcPriceList=EntityUtil.filterByCondition(procurementPriceList,condition);
		if(UtilValidate.isNotEmpty(fatPremiumProcPriceList)){
			fatPremiumProcPriceList = EntityUtil.getFirst(fatPremiumProcPriceList);
			fatPremium=fatPremiumProcPriceList.fatPercent;
			fatPremPrice=fatPremiumProcPriceList.price;
			
			PremAndDeductionMap.put("fatPremium", fatPremium);
			PremAndDeductionMap.put("fatPremPrice", fatPremPrice);
		}
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("price", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
		conditionList.add(EntityCondition.makeCondition("procurementPriceTypeId",EntityOperator.EQUALS,"PROC_PRICE_FAT_DED"));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		fatDedProcPriceList=EntityUtil.filterByCondition(procurementPriceList,condition);
		if(UtilValidate.isNotEmpty(fatDedProcPriceList)){
			fatDedProcPriceList = EntityUtil.getFirst(fatDedProcPriceList);
			fatDed=fatDedProcPriceList.fatPercent;
			fatDedPrice=fatDedProcPriceList.price;
			
			PremAndDeductionMap.put("fatDed", fatDed);
			PremAndDeductionMap.put("fatDedPrice", fatDedPrice);
		}
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("price", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
		conditionList.add(EntityCondition.makeCondition("procurementPriceTypeId",EntityOperator.EQUALS,"PROC_PRICE_SNF_DED"));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		snfDedProcPriceList=EntityUtil.filterByCondition(procurementPriceList,condition);
		if(UtilValidate.isNotEmpty(snfDedProcPriceList)){
			snfDedProcPriceList = EntityUtil.getFirst(snfDedProcPriceList);
			snfDed=snfDedProcPriceList.snfPercent;
			snfDedPrice=snfDedProcPriceList.price;
			
			PremAndDeductionMap.put("snfDed", snfDed);
			PremAndDeductionMap.put("snfDedPrice", snfDedPrice);
		}

		
		eachProductMap.put("PremAndDeductionMap", PremAndDeductionMap);
		
		prodMilkTransferList=EntityUtil.filterByCondition(milkTransferAndItemList,EntityCondition.makeCondition("productId",EntityOperator.EQUALS,eachProductId));
		Map vehicleWiseDetailsMap =[:];
		if(UtilValidate.isNotEmpty(prodMilkTransferList)){
			sno=1;
			prodMilkTransferList.each{eachDateMilkReeceipt->
				BigDecimal unitPrice = BigDecimal.ZERO;
				Map eachVehicleMap =FastMap.newInstance();
				
				receiveDate=eachDateMilkReeceipt.receiveDate;
				dcNo=eachDateMilkReeceipt.dcNo;
				vehicleId=eachDateMilkReeceipt.containerId;
				BigDecimal receivedQuantity=eachDateMilkReeceipt.receivedQuantity;
				if(UtilValidate.isNotEmpty(receivedQuantity)){
				totQuantity=totQuantity+receivedQuantity;
				}
				milkUnitPrice=eachDateMilkReeceipt.unitPrice;
				fatPremium =eachDateMilkReeceipt.fatPremium	;
				snfPremium =eachDateMilkReeceipt.snfPremium;
				billQuality=eachDateMilkReeceipt.billQuality;
				if("DISP_QLTY".equals(billQuality) && UtilValidate.isNotEmpty(billQuality)){
					snfPercent=eachDateMilkReeceipt.snf;
					fatPercent=eachDateMilkReeceipt.fat;
				}else{
					snfPercent=eachDateMilkReeceipt.receivedSnf;
					fatPercent=eachDateMilkReeceipt.receivedFat;
				}
				if(UtilValidate.isNotEmpty(fatPremium)){
					unitPrice=fatPremium+snfPremium+milkUnitPrice;
				}
				BigDecimal vehicleTotAmt =BigDecimal.ZERO;
				BigDecimal actualAmt =BigDecimal.ZERO;
				BigDecimal fatPremAmt =BigDecimal.ZERO;
				BigDecimal snfPremAmt =BigDecimal.ZERO;
				if(UtilValidate.isNotEmpty(unitPrice)){
					actualAmt=receivedQuantity*milkUnitPrice;
					actualAmt=actualAmt.setScale(2, BigDecimal.ROUND_HALF_UP);
					vehicleTotAmt=vehicleTotAmt+actualAmt;
					eachVehicleMap.put("actualAmt",actualAmt);
				}
				if(UtilValidate.isNotEmpty(fatPremium)){
					fatPremAmt=receivedQuantity*fatPremium;
					fatPremAmt=fatPremAmt.setScale(2, BigDecimal.ROUND_HALF_UP);
					vehicleTotAmt=vehicleTotAmt+fatPremAmt;
					eachVehicleMap.put("fatPremAmt",fatPremAmt);
				}
				if(UtilValidate.isNotEmpty(snfPremium)){
					snfPremAmt=receivedQuantity*snfPremium;
					snfPremAmt=snfPremAmt.setScale(2, BigDecimal.ROUND_HALF_UP);
					vehicleTotAmt=vehicleTotAmt+snfPremAmt;
					eachVehicleMap.put("snfPremAmt",snfPremAmt);
				}
				eachVehicleMap.put("recdDate",receiveDate);
				eachVehicleMap.put("dcNo",dcNo);
				eachVehicleMap.put("vehicleId",vehicleId);
				eachVehicleMap.put("receivedQuantity",receivedQuantity);
				eachVehicleMap.put("unitPrice",unitPrice);
				eachVehicleMap.put("snfPercent",snfPercent);
				eachVehicleMap.put("fatPercent",fatPercent);
				eachVehicleMap.put("vehicleTotAmt",vehicleTotAmt);
				totAmount=totAmount+vehicleTotAmt;
			//	vehicleWiseDetails.add(eachVehicleMap);
				
				vehicleWiseDetailsMap.put(sno,eachVehicleMap);
				sno=sno+1;
				
			}
		}
		eachProductMap.put("vehicleWiseDetailsMap", vehicleWiseDetailsMap);
		eachProductMap.put("totQuantity", totQuantity);
		eachProductMap.put("totAmount", totAmount);
		
		allProdProcPriceMap.put(eachProductId,eachProductMap);
	}
}
context.allProdProcPriceMap=allProdProcPriceMap;







