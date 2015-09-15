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
	context.errorMessage = "customTimePeriod Cannot Be Empty.......!";
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


Map vehicleWiseDetailsMap =FastMap.newInstance();
Map unionTotalsMap =FastMap.newInstance();
sno=1;
BigDecimal totQuantity= BigDecimal.ZERO;
BigDecimal totActualAmt= BigDecimal.ZERO;
BigDecimal totFatPremAmt= BigDecimal.ZERO;
BigDecimal totSnfPremamt= BigDecimal.ZERO;
BigDecimal totAmount= BigDecimal.ZERO;



unionParties=[];
unionPartyId=parameters.partyId;
productId=parameters.productId;
context.productId=productId;

partyName =  PartyHelper.getPartyName(delegator, unionPartyId, false);
if(UtilValidate.isNotEmpty(partyName)){
	context.partyName=partyName;
}
partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:unionPartyId, userLogin: userLogin]);
address1="";address2="";city="";postalCode="";
if (partyPostalAddress != null && UtilValidate.isNotEmpty(partyPostalAddress)) {
	if(partyPostalAddress.address1){
	address1=partyPostalAddress.address1;
	context.address1=address1;
	}
	if(partyPostalAddress.address2){
	address2=partyPostalAddress.address2;
	context.address2=address2;
	}
	if(partyPostalAddress.city){
	city=partyPostalAddress.city;
	context.city=city;
	}
	if(partyPostalAddress.postalCode){
	postalCode=partyPostalAddress.postalCode;
	context.postalCode=postalCode;
	 }
  }
 partyTelephone= dispatcher.runSync("getPartyTelephone", [partyId: unionPartyId, userLogin: userLogin]);
 phoneNumber = "";
 if (partyTelephone != null && partyTelephone.contactNumber != null) {
	 phoneNumber = partyTelephone.contactNumber;
	 context.phoneNumber=phoneNumber;
 }
 

if(UtilValidate.isNotEmpty(unionPartyId)){
	unionParties.add(unionPartyId);
	partyCondList=[];
	//partyCondList.add(EntityCondition.makeCondition("roleTypeIdTo",EntityOperator.EQUALS,"CHILL_CENTER"));
	partyCondList.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.EQUALS,unionPartyId));
	partyCondList.add(EntityCondition.makeCondition("roleTypeIdFrom",EntityOperator.EQUALS,"UNION"));
	EntityCondition partyCondition = EntityCondition.makeCondition(partyCondList,EntityOperator.AND);
	partyRelationship = delegator.findList("PartyRelationship", partyCondition , null, null, null, false );
	if(UtilValidate.isNotEmpty(partyRelationship)){
		partyRelationship = EntityUtil.filterByDate(partyRelationship,dayBegin);
		ccIds = new HashSet(EntityUtil.getFieldListFromEntityList(partyRelationship, "partyIdTo", false));
		ccIds.each{eachCcId->
			unionParties.add(eachCcId);
		}
	}
	Set unionPartySet = new HashSet(unionParties);
	/*	Map ccNameMap =FastMap.newInstance();
		partyName =  PartyHelper.getPartyName(delegator, partyId, false);
		if(UtilValidate.isNotEmpty(partyName)){
			ccNameMap.put("partyName", partyName);
		}
		partyIdTo="MD"
		partyToName =  PartyHelper.getPartyName(delegator, partyIdTo, false);
		if(UtilValidate.isNotEmpty(partyToName)){
			ccNameMap.put("partyToName", partyToName);
		}
		tinCstKstDetails = delegator.findList("PartyIdentification",EntityCondition.makeCondition("partyId", EntityOperator.EQUALS ,partyId)  , null, null, null, false );
		if(UtilValidate.isNotEmpty(tinCstKstDetails)){
			 tinDetails = EntityUtil.filterByCondition(tinCstKstDetails, EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "TIN_NUMBER"));
			 tinNumber="";
			 if(UtilValidate.isNotEmpty(tinDetails)){
				 tinDetails=EntityUtil.getFirst(tinDetails);
				 tinNumber=tinDetails.idValue;
				 ccNameMap.put("tinNumber", tinNumber);
			 }
		}
		allUnionPartyNamesMap.put(partyId, ccNameMap);
		*/
		
		containerIds=null;
		conditionList =[];
		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "MXF_APPROVED"));
		conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS , "INTERNAL"));
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN , unionPartySet));
		if(UtilValidate.isNotEmpty(productId)){
			conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS , productId));
			
		}
		conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS , "MD"));
		conditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
		conditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
		EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		List<String> orderBy = UtilMisc.toList("receiveDate");
		milkTransferAndItemList = delegator.findList("MilkTransfer", condition, null, orderBy, null, false);
		Set productIds=null;
		if(UtilValidate.isNotEmpty(milkTransferAndItemList)){
			 productIds = new HashSet(EntityUtil.getFieldListFromEntityList(milkTransferAndItemList, "productId", false));
		}
				
		if(UtilValidate.isNotEmpty(productIds)){
			productIds.each{eachProductId->
				BigDecimal price=BigDecimal.ZERO;
				priceChart = PriceServices.fetchPriceChartForParty(dctx, [priceDate:dayBegin, partyId: unionPartyId, userLogin: userLogin,]);
				procPriceChartId=null;
				billQuality=null;
				if(UtilValidate.isNotEmpty(priceChart)){
					procPriceChartId=priceChart.procPriceChartId;
					billQuantity=priceChart.billQuantity;
					billQuality=priceChart.billQuality;
					vehicleId=priceChart.containerId;
				}
				prodMilkTransferList=EntityUtil.filterByCondition(milkTransferAndItemList,EntityCondition.makeCondition("productId",EntityOperator.EQUALS,eachProductId));
				if(UtilValidate.isNotEmpty(prodMilkTransferList)){
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
							totActualAmt=totActualAmt+actualAmt;
							eachVehicleMap.put("actualAmt",actualAmt);
						}
						if(UtilValidate.isNotEmpty(fatPremium)){
							fatPremAmt=receivedQuantity*fatPremium;
							fatPremAmt=fatPremAmt.setScale(2, BigDecimal.ROUND_HALF_UP);
							vehicleTotAmt=vehicleTotAmt+fatPremAmt;
							totFatPremAmt=totFatPremAmt+fatPremAmt;
							eachVehicleMap.put("fatPremAmt",fatPremAmt);
						}
						if(UtilValidate.isNotEmpty(snfPremium)){
							snfPremAmt=receivedQuantity*snfPremium;
							snfPremAmt=snfPremAmt.setScale(2, BigDecimal.ROUND_HALF_UP);
							vehicleTotAmt=vehicleTotAmt+snfPremAmt;
							totSnfPremamt=totSnfPremamt+snfPremAmt;
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
				
			}
			unionTotalsMap.put("vehicleWiseDetailsMap", vehicleWiseDetailsMap);
			unionTotalsMap.put("totQuantity", totQuantity);
			unionTotalsMap.put("totActualAmt", totActualAmt);
			unionTotalsMap.put("totFatPremAmt", totFatPremAmt);
			unionTotalsMap.put("totSnfPremamt", totSnfPremamt);
			unionTotalsMap.put("totAmount", totAmount);
		}
	//allUnionPartyBillMap.put(partyId, allProdProcPriceMap);}
context.vehicleWiseDetailsMap=vehicleWiseDetailsMap;
context.unionTotalsMap=unionTotalsMap;

