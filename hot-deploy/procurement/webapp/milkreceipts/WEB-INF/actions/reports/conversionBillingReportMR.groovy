
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
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

import java.math.BigDecimal; 
import java.math.RoundingMode;
import java.util.Map;

import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;

import in.vasista.vbiz.procurement.ProcurementNetworkServices;

import org.ofbiz.party.party.PartyHelper;

import in.vasista.vbiz.milkReceipts.MilkReceiptBillingServices;


dctx = dispatcher.getDispatchContext();

String customTimePeriodId=parameters.customTimePeriodId;
GenericValue customTimePeriod = null;
String reportTypeFlag  = parameters.reportTypeFlag;
Timestamp fromDateTime = UtilDateTime.nowTimestamp();
Timestamp thruDateTime = UtilDateTime.nowTimestamp();

if(UtilValidate.isEmpty(reportTypeFlag)){
	if(UtilValidate.isEmpty(customTimePeriodId)){
		Debug.logError("customTimePeriod Cannot Be Empty","");
		context.errorMessage = "customTimePeriod Cannot Be Empty.......!";
		return;
	}
	customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : customTimePeriodId], false);
	
	
	List periodBillingConditionList = UtilMisc.toList(EntityCondition.makeCondition("billingTypeId",EntityOperator.EQUALS,"PB_CONV_MRGN"));
	periodBillingConditionList.add(EntityCondition.makeCondition("customTimePeriodId",EntityOperator.EQUALS,customTimePeriodId));
	periodBillingConditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"COM_CANCELLED"));
	EntityCondition periodBillingCondition = EntityCondition.makeCondition(periodBillingConditionList);
	
	List periodBillingList = delegator.findList("PeriodBilling",periodBillingCondition,null,null,null,false);
	
	if(UtilValidate.isEmpty(periodBillingList)){
		Debug.logError("Conversion Billing not generated","");
		context.errorMessage = "Conversion Billing not generated.......!";
		return;
	}
}
context.put("showReference","N");
if(UtilValidate.isNotEmpty(customTimePeriod)){
	fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
}else{
	String fromDateStr = parameters.fromDate;
	String thruDateStr = parameters.thruDate;
	if(UtilValidate.isEmpty(fromDateStr) ||UtilValidate.isEmpty(thruDateStr) ){
		Debug.logError("From Date or thru date is empty","");
		context.errorMessage = "From Date or thru date is empty :";
		return;
	}
	def sdf = new SimpleDateFormat("MMMM dd, yyyy");
	try {
		fromDateTime = new java.sql.Timestamp(sdf.parse(fromDateStr).getTime());
		thruDateTime = new java.sql.Timestamp(sdf.parse(thruDateStr).getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: "+fromDate, "");
	}
	
	context.put("showReference","Y");
	
}

dayBegin = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(thruDateTime);

Map inMap = FastMap.newInstance();
inMap.put("userLogin", userLogin);
inMap.put("shiftType", "MILK_SHIFT");
inMap.put("fromDate", dayBegin);
inMap.put("thruDate", dayEnd);
Map workShifts = MilkReceiptBillingServices.getShiftDaysByType(dctx,inMap );

fromDate=workShifts.fromDate;
thruDate=workShifts.thruDate;

context.fromDate = fromDate;
context.thruDate = dayEnd;

String partyId=parameters.partyId;
String partyName = PartyHelper.getPartyName(delegator,partyId,false);
context.put("partyName",partyName);
context.put("abstPartyName",partyName);




List sentPartyIds = FastList.newInstance();
List partyRelationShipConditionList = FastList.newInstance();
partyRelationShipConditionList.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.EQUALS,partyId));
partyRelationShipConditionList.add(EntityCondition.makeCondition("roleTypeIdFrom",EntityOperator.EQUALS,"UNION"));
	 EntityCondition partyRelationShipCondition  = EntityCondition.makeCondition(partyRelationShipConditionList)	;
List unionsList = delegator.findList("PartyRelationship",partyRelationShipCondition,null,null,null,false);
unionsList = EntityUtil.filterByDate(unionsList,dayBegin);
Set sendPartyIdsSet = new HashSet(EntityUtil.getFieldListFromEntityList(unionsList,"partyIdTo", false));
//sendPartyIdsSet.add(partyId);

sentPartyIds.addAll(sendPartyIdsSet);
sentPartyIds.add(0,partyId);
String butterProductId = "84";
Map unionWiseProductAbstract = FastMap.newInstance();

List milkConversionConditionList = UtilMisc.toList(EntityCondition.makeCondition("purposeTypeId",EntityOperator.EQUALS,"CONVERSION"));
milkConversionConditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "MXF_APPROVED"));
milkConversionConditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN , sentPartyIds));
milkConversionConditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS , "MD"));
milkConversionConditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
milkConversionConditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
EntityCondition milkConversionCondition = EntityCondition.makeCondition(milkConversionConditionList,EntityOperator.AND);
List<String> orderBy = UtilMisc.toList("receiveDate");
List milkTransferConversionList = delegator.findList("MilkTransfer", milkConversionCondition, null, orderBy, null, false);
Map partyWiseProductWiseConversionMap = FastMap.newInstance();
for(String unionId in sentPartyIds){
	String unionName = PartyHelper.getPartyName(delegator,unionId,false);
	List unionConversionList = EntityUtil.filterByCondition(milkTransferConversionList, EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,unionId));
	if(UtilValidate.isNotEmpty(unionConversionList)){
		Map conversionProductMap = FastMap.newInstance();
		
		Set productIdsSet = new HashSet(EntityUtil.getFieldListFromEntityList(unionConversionList, "productId", false));
		for(productId in productIdsSet){
			List productTransferDetList = FastList.newInstance();
			List productRelatedTransfers = EntityUtil.filterByCondition(unionConversionList, EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
			// Here we are trying to get conversion product configurations
			Set conProductIdsSet = new HashSet(EntityUtil.getFieldListFromEntityList(productRelatedTransfers, "conversionProductId", false));
			Map productConversionRateInMap = FastMap.newInstance();
			productConversionRateInMap.put("userLogin", userLogin);
			productConversionRateInMap.put("productId", productId);
			productConversionRateInMap.put("fromDate",dayBegin);
			Map productConversionRatesResult = MilkReceiptBillingServices.getProductConversionRates(dctx,productConversionRateInMap);
			if(ServiceUtil.isError(productConversionRatesResult)){
				Debug.logError("Error while getting product conversion Rates :"+productConversionRatesResult, "");
				context.errorMessage = "Error while getting product conversion Rates :";
				return;
			}
			Map conversionProductPriceMap = (Map)productConversionRatesResult.get("conversionProductPriceMap");
			Map conversionProductConfigDetMap = FastMap.newInstance();
			GenericValue productDetails = delegator.findOne("Product",[productId : productId], false);
			for(conProductId in conProductIdsSet){
				List tranConProductDetPriceMapList = FastList.newInstance();
				List conProductTransfersList = EntityUtil.filterByCondition(productRelatedTransfers, EntityCondition.makeCondition("conversionProductId",EntityOperator.EQUALS,conProductId));
				GenericValue conversionProduct = delegator.findOne("Product",[productId : conProductId], false);
				List<GenericValue> productConfigList = FastList.newInstance();
				List productConditionList = FastList.newInstance();
				productConditionList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,conProductId));
				productConditionList.add(EntityCondition.makeCondition("productCategoryId",EntityOperator.EQUALS,"PRODUCT_CON_STD"));
				EntityCondition productCondition = EntityCondition.makeCondition(productConditionList);
				productConfigList = delegator.findList("ProductTestComponent", productCondition, null, null, null, false);
				productConfigList = EntityUtil.filterByDate(productConfigList,dayBegin);
				
				BigDecimal   fatLoss             = BigDecimal.ZERO;
				BigDecimal   snfLoss             = BigDecimal.ZERO;
				BigDecimal   addSugar       	 = BigDecimal.ZERO;
				BigDecimal   totSolidsLoss  	 = BigDecimal.ZERO;
				BigDecimal   butterYield    	 = BigDecimal.ZERO;
				BigDecimal   productYield   	 = BigDecimal.ZERO;
				BigDecimal   fatPercentStd	     = BigDecimal.ZERO;
				GenericValue fatLossDetails      = EntityUtil.getFirst(EntityUtil.filterByCondition(productConfigList,EntityCondition.makeCondition("testComponent",EntityOperator.EQUALS,"fatLoss")));
				GenericValue snfLossDetails      = EntityUtil.getFirst(EntityUtil.filterByCondition(productConfigList,EntityCondition.makeCondition("testComponent",EntityOperator.EQUALS,"snfLoss")));
				GenericValue addSugarDetails     = EntityUtil.getFirst(EntityUtil.filterByCondition(productConfigList,EntityCondition.makeCondition("testComponent",EntityOperator.EQUALS,"addSugar")));
				GenericValue totSolidsDetails    = EntityUtil.getFirst(EntityUtil.filterByCondition(productConfigList,EntityCondition.makeCondition("testComponent",EntityOperator.EQUALS,"totSolids")));
				GenericValue butterYieldDetails  = EntityUtil.getFirst(EntityUtil.filterByCondition(productConfigList,EntityCondition.makeCondition("testComponent",EntityOperator.EQUALS,"butterYield")));
				GenericValue productYieldDetails = EntityUtil.getFirst(EntityUtil.filterByCondition(productConfigList,EntityCondition.makeCondition("testComponent",EntityOperator.EQUALS,"productYield")));
				GenericValue fatPercentDetails   = EntityUtil.getFirst(EntityUtil.filterByCondition(productConfigList,EntityCondition.makeCondition("testComponent",EntityOperator.EQUALS,"fatPercentStd")));
				
				if(UtilValidate.isNotEmpty(fatLossDetails)){
					fatLoss = (BigDecimal)fatLossDetails.get("standardValue");
				}
				if(UtilValidate.isNotEmpty(snfLossDetails)){
					snfLoss = (BigDecimal)snfLossDetails.get("standardValue");
				}
				if(UtilValidate.isNotEmpty(totSolidsDetails)){
					totSolidsLoss = (BigDecimal)totSolidsDetails.get("standardValue");
				}
				if(UtilValidate.isNotEmpty(addSugarDetails)){
					addSugar = (BigDecimal)addSugarDetails.get("standardValue");
				}
				if(UtilValidate.isNotEmpty(butterYieldDetails)){
					butterYield = (BigDecimal)butterYieldDetails.get("standardValue");;
				}
				if(UtilValidate.isNotEmpty(productYieldDetails)){
					productYield = (BigDecimal)productYieldDetails.get("standardValue");
				}
				if(UtilValidate.isNotEmpty(fatPercentDetails)){
					fatPercentStd = (BigDecimal)fatPercentDetails.get("standardValue");
				}
				if(snfLoss.compareTo(totSolidsLoss)==1){
					totSolidsLoss = snfLoss;
				}
				String productName = (String) productDetails.get("internalName");
				String conProductName = (String) conversionProduct.get("internalName");
				
				Map productConfigMap = FastMap.newInstance();
				
				productConfigMap.put("productName", productDetails.get("productName"));
				productConfigMap.put("productBrandName", productDetails.get("brandName"));
				productConfigMap.put("productIntName", productDetails.get("internalName"));
				
				productConfigMap.put("conProductName", conversionProduct.get("productName"));
				productConfigMap.put("conProductBrandName", conversionProduct.get("brandName"));
				productConfigMap.put("conProductIntName", conversionProduct.get("internalName"));
				
				productConfigMap.put("fatLoss", fatLoss);
				productConfigMap.put("snfLoss", snfLoss);
				productConfigMap.put("addSugar", addSugar);
				productConfigMap.put("totSolidsLoss", totSolidsLoss);
				productConfigMap.put("butterYield", butterYield);
				productConfigMap.put("productYield", productYield);
				productConfigMap.put("fatPercentStd", fatPercentStd);
				BigDecimal butterConversionPrice = BigDecimal.ZERO;
				BigDecimal prodConversionPrice = BigDecimal.ZERO;
				if(UtilValidate.isNotEmpty(conversionProductPriceMap) && UtilValidate.isNotEmpty(conversionProductPriceMap.get(butterProductId))){
					butterConversionPrice = (BigDecimal)conversionProductPriceMap.get(butterProductId);
				}
				if(UtilValidate.isNotEmpty(conversionProductPriceMap) && UtilValidate.isNotEmpty(conversionProductPriceMap.get(conProductId))){
					prodConversionPrice = (BigDecimal)conversionProductPriceMap.get(conProductId);
				}
				productConfigMap.put("butterConversionPrice", butterConversionPrice);
				productConfigMap.put("prodConversionPrice", prodConversionPrice);
				int slNo = 1;
				Map totTransferMap = FastMap.newInstance();
				for(conProductTransfers in conProductTransfersList){
					Timestamp recdDate = (Timestamp)conProductTransfers.get("receiveDate");
					
					String dateString  = UtilDateTime.toDateString(recdDate, "dd/MM");
					String containerId = (String) conProductTransfers.get("containerId");
					String dcNo = (String) conProductTransfers.get("dcNo");
					
					
					BigDecimal recdQty = (BigDecimal) conProductTransfers.get("receivedQuantity");
					BigDecimal recdFat = (BigDecimal) conProductTransfers.get("receivedFat");
					BigDecimal recdSnf = (BigDecimal) conProductTransfers.get("receivedSnf");
					BigDecimal recdKgFat = (BigDecimal) conProductTransfers.get("receivedKgFat");
					BigDecimal recdKgSnf = (BigDecimal) conProductTransfers.get("receivedKgSnf");
					
					BigDecimal prodQty  = recdQty;
					BigDecimal prodFat  = fatPercentStd;
					int recdFatIntValue = recdFat.intValue();
					if(fatPercentStd.compareTo(BigDecimal.ZERO)==0 && recdFatIntValue<1){
						prodFat = recdFat;
					}
					BigDecimal prodSnf  = recdSnf;
					BigDecimal prodKgFat  = ProcurementNetworkServices.calculateKgFatOrKgSnf(recdQty, prodFat);
					BigDecimal prodKgSnf  = recdKgSnf;
					BigDecimal conSugarAddn = BigDecimal.ZERO;
					BigDecimal prodTs     = prodKgFat.add(prodKgSnf);
					if(UtilValidate.isNotEmpty(conProductTransfers.get("conSugarAddn"))){
						conSugarAddn = (BigDecimal)conProductTransfers.get("conSugarAddn");
					}
					BigDecimal prodTotTs     = prodTs.add(conSugarAddn);
					 
					BigDecimal diffFat  = BigDecimal.ZERO;
					if(UtilValidate.isNotEmpty(conProductTransfers.get("conFatLoss"))){
						diffFat = (BigDecimal) conProductTransfers.get("conFatLoss");
					}
					BigDecimal conTsLoss  = BigDecimal.ZERO;
					if(UtilValidate.isNotEmpty(conProductTransfers.get("conFatLoss"))){
						conTsLoss = (BigDecimal) conProductTransfers.get("conTotalSolidsLoss");
					}
					BigDecimal butterYieldVal = BigDecimal.ZERO;
					BigDecimal prodYieldVal = BigDecimal.ZERO;
					BigDecimal butterAmount = BigDecimal.ZERO;
					BigDecimal prodAmount = BigDecimal.ZERO;
					
					if(UtilValidate.isNotEmpty(conProductTransfers.get("butterYield"))){
						butterYieldVal = (BigDecimal)conProductTransfers.get("butterYield");
					}
					if(UtilValidate.isNotEmpty(conProductTransfers.get("butterAmount"))){
						butterAmount = (BigDecimal)conProductTransfers.get("butterAmount");
					}
					if(UtilValidate.isNotEmpty(conProductTransfers.get("conProductYield"))){
						prodYieldVal = (BigDecimal)conProductTransfers.get("conProductYield");
					}
					if(UtilValidate.isNotEmpty(conProductTransfers.get("conProductAmount"))){
						prodAmount = (BigDecimal)conProductTransfers.get("conProductAmount");
					}
					BigDecimal prodNetTs     = prodTotTs.subtract(conTsLoss);
					
					Map tranProductDetPriceMap = FastMap.newInstance();
					tranProductDetPriceMap.put("recdQty",recdQty);
					tranProductDetPriceMap.put("recdFat",recdFat);
					tranProductDetPriceMap.put("recdSnf",recdSnf);
					tranProductDetPriceMap.put("recdKgFat",recdKgFat);
					tranProductDetPriceMap.put("recdKgSnf",recdKgSnf);
					tranProductDetPriceMap.put("prodQty",prodQty);
					tranProductDetPriceMap.put("prodFat",prodFat);
					tranProductDetPriceMap.put("prodSnf",prodSnf);
					tranProductDetPriceMap.put("prodKgFat",prodKgFat);
					tranProductDetPriceMap.put("prodKgSnf",prodKgSnf);
					tranProductDetPriceMap.put("prodTs",prodTs);
					tranProductDetPriceMap.put("conSugarAddn",conSugarAddn);
					tranProductDetPriceMap.put("prodTotTs",prodTotTs);
					tranProductDetPriceMap.put("conTsLoss",conTsLoss);
					tranProductDetPriceMap.put("prodNetTs",prodNetTs);
					tranProductDetPriceMap.put("prodYield",prodYieldVal);
					tranProductDetPriceMap.put("prodAmount",prodAmount);
					tranProductDetPriceMap.put("diffFat",diffFat);
					tranProductDetPriceMap.put("butterYield",butterYieldVal);
					tranProductDetPriceMap.put("butterAmount",butterAmount);
					
					if(UtilValidate.isEmpty(totTransferMap)){
						totTransferMap.putAll(tranProductDetPriceMap);
					}else{
						for(trnKey in tranProductDetPriceMap.keySet()){
							BigDecimal existedQty = (BigDecimal)totTransferMap.get(trnKey);
							BigDecimal newQty = (BigDecimal)tranProductDetPriceMap.get(trnKey);
							totTransferMap.put(trnKey,existedQty.add(newQty));
						}
						BigDecimal existedRecdQty = (BigDecimal)totTransferMap.get("recdQty");
						BigDecimal existedRecdKgFat = (BigDecimal)totTransferMap.get("recdKgFat");
						BigDecimal existedRecdKgSnf = (BigDecimal)totTransferMap.get("recdKgSnf");
						BigDecimal existedProdQty = (BigDecimal)totTransferMap.get("prodQty");
						BigDecimal existedProdKgFat = (BigDecimal)totTransferMap.get("prodKgFat");
						BigDecimal existedProdKgSnf = (BigDecimal)totTransferMap.get("prodKgSnf");
						totTransferMap.put("recdFat",ProcurementNetworkServices.calculateFatOrSnf(existedRecdKgFat, existedRecdQty));
						totTransferMap.put("recdSnf",ProcurementNetworkServices.calculateFatOrSnf(existedRecdKgSnf, existedRecdQty));
						totTransferMap.put("prodFat",ProcurementNetworkServices.calculateFatOrSnf(existedProdKgFat, existedProdQty));
						totTransferMap.put("prodSnf",ProcurementNetworkServices.calculateFatOrSnf(existedProdKgSnf, existedProdQty));
					}
					tranProductDetPriceMap.put("slNo", slNo);
					tranProductDetPriceMap.put("dcNo", dcNo);
					tranProductDetPriceMap.put("date", dateString);
					tranProductDetPriceMap.put("tankerNo", containerId);
					tranConProductDetPriceMapList.add(tranProductDetPriceMap);
					slNo = slNo+1;
				}
				String abstProductName = productName.concat(" TO ").concat(conProductName);
				if(UtilValidate.isNotEmpty(unionWiseProductAbstract) && UtilValidate.isNotEmpty(unionWiseProductAbstract.get(abstProductName))){
					Map tempMap = FastMap.newInstance();
					tempMap.putAll(unionWiseProductAbstract.get(abstProductName));
					for(String prodAbstKey in tempMap.keySet()){
						BigDecimal tempQty = tempMap.get(prodAbstKey);
						BigDecimal newQty  = totTransferMap.get(prodAbstKey);
						tempMap.put(prodAbstKey,tempQty.add(newQty));
					}
					BigDecimal existedRecdQty = (BigDecimal)tempMap.get("recdQty");
					BigDecimal existedRecdKgFat = (BigDecimal)tempMap.get("recdKgFat");
					BigDecimal existedRecdKgSnf = (BigDecimal)tempMap.get("recdKgSnf");
					BigDecimal existedProdQty = (BigDecimal)tempMap.get("prodQty");
					BigDecimal existedProdKgFat = (BigDecimal)tempMap.get("prodKgFat");
					BigDecimal existedProdKgSnf = (BigDecimal)tempMap.get("prodKgSnf");
					
					tempMap.put("recdFat",ProcurementNetworkServices.calculateFatOrSnf(existedRecdKgFat, existedRecdQty));
					tempMap.put("recdSnf",ProcurementNetworkServices.calculateFatOrSnf(existedRecdKgSnf, existedRecdQty));
					tempMap.put("prodFat",ProcurementNetworkServices.calculateFatOrSnf(existedProdKgFat, existedProdQty));
					tempMap.put("prodSnf",ProcurementNetworkServices.calculateFatOrSnf(existedProdKgSnf, existedProdQty));
					unionWiseProductAbstract.put(abstProductName, tempMap);
				}else{
					Map tempMap = FastMap.newInstance();
					tempMap.putAll(totTransferMap);
					unionWiseProductAbstract.put(abstProductName, tempMap);
				}
				
				totTransferMap.put("slNo"," ");
				totTransferMap.put("date"," ");
				totTransferMap.put("tankerNo","TOTAL");
				totTransferMap.put("dcNo"," ");
				if(UtilValidate.isNotEmpty(tranConProductDetPriceMapList)){
					tranConProductDetPriceMapList.add(totTransferMap);
					productConfigMap.put("tankerList",tranConProductDetPriceMapList);
				}
				conversionProductConfigDetMap.put(conProductId,productConfigMap);
				
			}
			if(UtilValidate.isNotEmpty(conversionProductConfigDetMap)){
				conversionProductMap.put(productId,conversionProductConfigDetMap);
			}
		}
		conversionProductMap.put("unionName",unionName);
		partyWiseProductWiseConversionMap.put(unionId,conversionProductMap);
	}
}

if(UtilValidate.isNotEmpty(unionWiseProductAbstract)){
	Map grandTotMap = FastMap.newInstance();
	List productTransferList = FastList.newInstance();
	for(String prodConvKey in unionWiseProductAbstract.keySet()){
		Map tempMap = FastMap.newInstance();
		tempMap.putAll((Map)unionWiseProductAbstract.get(prodConvKey));
		// Here we are preparing totals 
		if(UtilValidate.isEmpty(grandTotMap)){
			grandTotMap.putAll(tempMap);
		}else{
			for(String prodKey in grandTotMap.keySet()){
				BigDecimal existedQty = (BigDecimal)grandTotMap.get(prodKey);
				BigDecimal newQty = (BigDecimal)tempMap.get(prodKey);
				grandTotMap.put(prodKey,existedQty.add(newQty));
			}
		}
		
		tempMap.put("prodConvKey",prodConvKey);
		productTransferList.add(tempMap);
	}
	if(UtilValidate.isNotEmpty(grandTotMap)){
		BigDecimal existedRecdQty = (BigDecimal)grandTotMap.get("recdQty");
		BigDecimal existedRecdKgFat = (BigDecimal)grandTotMap.get("recdKgFat");
		BigDecimal existedRecdKgSnf = (BigDecimal)grandTotMap.get("recdKgSnf");
		BigDecimal existedProdQty = (BigDecimal)grandTotMap.get("prodQty");
		BigDecimal existedProdKgFat = (BigDecimal)grandTotMap.get("prodKgFat");
		BigDecimal existedProdKgSnf = (BigDecimal)grandTotMap.get("prodKgSnf");
		
		grandTotMap.put("recdFat",ProcurementNetworkServices.calculateFatOrSnf(existedRecdKgFat, existedRecdQty));
		grandTotMap.put("recdSnf",ProcurementNetworkServices.calculateFatOrSnf(existedRecdKgSnf, existedRecdQty));
		grandTotMap.put("prodFat",ProcurementNetworkServices.calculateFatOrSnf(existedProdKgFat, existedProdQty));
		grandTotMap.put("prodSnf",ProcurementNetworkServices.calculateFatOrSnf(existedProdKgSnf, existedProdQty));
		grandTotMap.put("prodConvKey","TOTAL");
		productTransferList.add(grandTotMap);
	}
	
	
	unionWiseProductAbstract.clear();
	unionWiseProductAbstract.put("tankerList",productTransferList);
	
}

context.putAt("unionWiseProductAbstract", unionWiseProductAbstract);
context.putAt("nowTimestamp", UtilDateTime.nowTimestamp());
context.put("partyWiseProductWiseConversionMap",partyWiseProductWiseConversionMap);