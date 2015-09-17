
import java.sql.*

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import in.vasista.vbiz.production.ProductionServices;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;

import org.ofbiz.party.party.PartyHelper;

import in.vasista.vbiz.milkReceipts.MilkReceiptBillingServices;

fromDate=parameters.mateBalanceFromDate;
thruDate=parameters.mateBalanceThruDate;
deptId =parameters.deptId;

dctx = dispatcher.getDispatchContext();
fromDateTime = null;
thruDateTime = null;
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	fromDateTime = new java.sql.Timestamp(sdf.parse(fromDate).getTime());
	thruDateTime = new java.sql.Timestamp(sdf.parse(thruDate).getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+fromDate, "");
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
context.deptId = deptId;
// purchas,conversion, receipts
BigDecimal totReceiptQty = BigDecimal.ZERO;
BigDecimal totReceiptFat = BigDecimal.ZERO;
BigDecimal totReceiptSnf = BigDecimal.ZERO;



totalDays=UtilDateTime.getIntervalInDays(fromDateTime,thruDateTime);

Map closingBalanceMap =FastMap.newInstance();


List facilityList = delegator.findList("Facility", null, null,null, null, false);

conditionList =[];
conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, deptId));
conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.NOT_EQUAL, "PLANT"));
EntityCondition facilityfromDeptCond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
fromDeptSiloList=EntityUtil.filterByCondition(facilityList,facilityfromDeptCond );
//issuedToDepts=EntityUtil.getFieldListFromEntityList(issuedToDeptsList, "ownerPartyId", true);
fromDeptStorageIds=null;
//fromDeptSiloList=EntityUtil.filterByCondition(facilityList, EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS,deptId));
if(UtilValidate.isNotEmpty(fromDeptSiloList)){
	fromDeptStorageIds=EntityUtil.getFieldListFromEntityList(fromDeptSiloList, "facilityId", true);
}
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, deptId));
conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "PLANT"));
fromDeptPlantCond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
fromDeptPlantList=EntityUtil.filterByCondition(facilityList,fromDeptPlantCond );
if(UtilValidate.isNotEmpty(fromDeptPlantList)){
	String deptName = (EntityUtil.getFirst(fromDeptPlantList)).getString("facilityName");
	context.deptName=deptName;
	fromDeptPlantList=EntityUtil.getFieldListFromEntityList(fromDeptPlantList, "facilityId", true);
	context.fromDeptPlantList = fromDeptPlantList;
}

//OPENING BALANCE REPORT ===============>
Map openingBalProductMap = FastMap.newInstance();
Map openingBalProductTotalMap = FastMap.newInstance();

allSiloDetailsMap=[:];
allSiloOpeningList=[];
BigDecimal totInventoryQty = BigDecimal.ZERO;
BigDecimal totOpenFatQtyKg = BigDecimal.ZERO;
BigDecimal totOpenSnfQtyKg = BigDecimal.ZERO;
openTotBalSiloMap=[:];
eachSiloNo=1;
if(UtilValidate.isNotEmpty(fromDeptStorageIds) || UtilValidate.isNotEmpty(fromDeptPlantList)){
		
	if(UtilValidate.isNotEmpty(fromDeptStorageIds)){
		fromDeptStorageIds.each {eachDeptStorageId->
			openingBalSiloMap=[:];
			
			BigDecimal openingQty = BigDecimal.ZERO;
			BigDecimal openingFatKg = BigDecimal.ZERO;
			BigDecimal openingSnfKg = BigDecimal.ZERO;
			BigDecimal openingFatPers = BigDecimal.ZERO;
			BigDecimal openingSnfPers = BigDecimal.ZERO;
			
			invCountMap = ProductionServices.getSiloInventoryOpeningBalance(dctx, [effectiveDate:fromDate, facilityId: eachDeptStorageId, userLogin: userLogin,]);
			invCountMapData=invCountMap.openingBalance;
			if(invCountMapData.get("quantityKgs") && UtilValidate.isNotEmpty(invCountMapData)){
				// && invCountMapData.get("quantityKgs") >0
				openingQty = invCountMapData.get("quantityKgs");
				invProductId = invCountMapData.get("invProductId");
				openingFatKg=invCountMapData.get("kgFat");
				openingSnfKg=invCountMapData.get("kgSnf");
				openingFatPers = ProcurementNetworkServices.calculateFatOrSnf(openingFatKg, openingQty);
				openingSnfPers = ProcurementNetworkServices.calculateFatOrSnf(openingSnfKg, openingQty);
				if(UtilValidate.isNotEmpty(openingQty) && !(openingQty.compareTo(BigDecimal.ZERO)==0)){
			        facilityNames = delegator.findOne("Facility",["facilityId":eachDeptStorageId],false);
			        storageName=facilityNames.get("facilityName");
					openingBalSiloMap.put("productId", invProductId);
					openingBalSiloMap.put("description", storageName);
					openingBalSiloMap.put("quantity", openingQty);
					openingBalSiloMap.put("fatKg", openingFatKg);
					openingBalSiloMap.put("snfKg", openingSnfKg);
					openingBalSiloMap.put("fatPers", openingFatPers);
					openingBalSiloMap.put("snfPers", openingSnfPers);
					totInventoryQty=totInventoryQty+openingQty;
					totOpenFatQtyKg=totOpenFatQtyKg+openingFatKg;
					totOpenSnfQtyKg=totOpenSnfQtyKg+openingSnfKg;
					openingBalProductMap.put(eachSiloNo,openingBalSiloMap)
					eachSiloNo=eachSiloNo+1;
					
				}
			}
		}
	}
	fromDeptPlantList.each {fromDeptPlantId->
		openingBalSiloMap=[:];
		List productFacility = delegator.findList("ProductFacility", EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, fromDeptPlantId), null,null, null, false);
		if(UtilValidate.isNotEmpty(productFacility)){
			productIds=EntityUtil.getFieldListFromEntityList(productFacility, "productId", true);
			productIds.each {eachProductId->
				BigDecimal openingQty = BigDecimal.ZERO;
				BigDecimal openingFatKg = BigDecimal.ZERO;
				BigDecimal openingSnfKg = BigDecimal.ZERO;
				BigDecimal openingFatPers = BigDecimal.ZERO;
				BigDecimal openingSnfPers = BigDecimal.ZERO;
				invCountMap = ProductionServices.getSiloInventoryOpeningBalance(dctx, [effectiveDate:fromDate, facilityId: fromDeptPlantId,productId: eachProductId, userLogin: userLogin,]);
				invCountMapData=invCountMap.openingBalance;
				if(invCountMapData.get("quantityKgs") && UtilValidate.isNotEmpty(invCountMapData)){
					openingQty = invCountMapData.get("quantityKgs");
					invProductId = invCountMapData.get("invProductId");
					openingFatKg=invCountMapData.get("kgFat");
					openingSnfKg=invCountMapData.get("kgSnf");
					openingFatPers = ProcurementNetworkServices.calculateFatOrSnf(openingFatKg, openingQty);
					openingSnfPers = ProcurementNetworkServices.calculateFatOrSnf(openingSnfKg, openingQty);
					if(UtilValidate.isNotEmpty(openingQty) && !(openingQty.compareTo(BigDecimal.ZERO)==0)){
						facilityNames = delegator.findOne("Facility",["facilityId":fromDeptPlantId],false);
						storageName=facilityNames.get("facilityName");
						openingBalSiloMap.put("productId", invProductId);
						openingBalSiloMap.put("description", storageName);
						openingBalSiloMap.put("quantity", openingQty);
						openingBalSiloMap.put("fatKg", openingFatKg);
						openingBalSiloMap.put("snfKg", openingSnfKg);
						openingBalSiloMap.put("fatPers", openingFatPers);
						openingBalSiloMap.put("snfPers", openingSnfPers);
						totInventoryQty=totInventoryQty+openingQty;
						totOpenFatQtyKg=totOpenFatQtyKg+openingFatKg;
						totOpenSnfQtyKg=totOpenSnfQtyKg+openingSnfKg;
						openingBalProductMap.put(eachSiloNo,openingBalSiloMap)
						eachSiloNo=eachSiloNo+1;
						
					}
				 }
			}
		}
	}
	openingBalProductTotalMap.put("description", "Total");
	openingBalProductTotalMap.put("quantity", totInventoryQty);
	openingBalProductTotalMap.put("fatKg", totOpenFatQtyKg);
	openingBalProductTotalMap.put("snfKg", totOpenSnfQtyKg);
} 
context.openingBalProductMap=openingBalProductMap;
context.openingBalProductTotalMap=openingBalProductTotalMap;

	

List convPurchaseList=FastList.newInstance();
if("INT7".equals(deptId) && UtilValidate.isNotEmpty(deptId)){
	// CONVERSION RECEIPTS======================>
	conversionReceipts = ProductionServices.getPurchaseAndConversionMilkReceipts(dctx, [fromDate: fromDate,thruDate: thruDate, purposeTypeId: "CONVERSION", userLogin: userLogin,]);
	convMilkReceipts=conversionReceipts.get("milkReceiptsMap");
	convMilkReceiptsTotal=conversionReceipts.get("milkReceiptsTotalsMap");
	context.convMilkReceipts=convMilkReceipts;
	context.convMilkReceiptsTotal=convMilkReceiptsTotal;
	convProductWiseMap=conversionReceipts.get("productWiseMap");
	if(UtilValidate.isNotEmpty(convProductWiseMap)){
		convPurchaseList.add(convProductWiseMap);
		totReceiptQty=totReceiptQty+convMilkReceiptsTotal.get("totRecdQty");
		totReceiptFat=totReceiptFat+convMilkReceiptsTotal.get("totRecdKgFat");
		totReceiptSnf=totReceiptSnf+convMilkReceiptsTotal.get("totRecdKgSnf");
	}
	
	// PURCHASE RECEIPTS======================>
	purchaseReceipts = ProductionServices.getPurchaseAndConversionMilkReceipts(dctx, [fromDate: fromDate,thruDate: thruDate, purposeTypeId: "INTERNAL", userLogin: userLogin,]);
	purchaseMilkReceipts=purchaseReceipts.get("milkReceiptsMap");
	purchaseMilkReceiptsTotal=purchaseReceipts.get("milkReceiptsTotalsMap");
	context.purchaseMilkReceipts=purchaseMilkReceipts;
	context.purchaseMilkReceiptsTotal=purchaseMilkReceiptsTotal;
	purProductWiseMap=purchaseReceipts.get("productWiseMap");
	if(UtilValidate.isNotEmpty(purProductWiseMap)){
		convPurchaseList.add(purProductWiseMap);
		totReceiptQty=totReceiptQty+purchaseMilkReceiptsTotal.get("totRecdQty");
		totReceiptFat=totReceiptFat+purchaseMilkReceiptsTotal.get("totRecdKgFat");
		totReceiptSnf=totReceiptSnf+purchaseMilkReceiptsTotal.get("totRecdKgSnf");
	}
}

// INTERNAL, RECEIPTS AND RETURNS =============>
internalReturnsAndReceipts = ProductionServices.getMilkReturnsAndIntenalReceipts(dctx, [fromDate: fromDate,thruDate: thruDate, ownerPartyId:deptId, userLogin: userLogin,]);
intReturnsAndReceipts=internalReturnsAndReceipts.get("milkReturnsAndReceiptsMap");
intReturnsAndReceiptsTotal=internalReturnsAndReceipts.get("milkRetnsAndRcptsTotalsMap");
context.intReturnsAndReceipts=intReturnsAndReceipts;
context.intReturnsAndReceiptsTotal=intReturnsAndReceiptsTotal;
if(UtilValidate.isNotEmpty(intReturnsAndReceiptsTotal)){
	totReceiptQty=totReceiptQty+intReturnsAndReceiptsTotal.get("totRecdQty");
	totReceiptFat=totReceiptFat+intReturnsAndReceiptsTotal.get("totRecdKgFat");
	totReceiptSnf=totReceiptSnf+intReturnsAndReceiptsTotal.get("totRecdKgSnf");
}

// ISSUE DETAILS==============================>
departmentMilkIssues = ProductionServices.getDepartmentMilkIssues(dctx, [fromDate: fromDate,thruDate: thruDate, ownerPartyId:deptId, userLogin: userLogin,]);
milkIssuesMap=departmentMilkIssues.get("milkIssuesMap");
milkIssuesTotalsMap=departmentMilkIssues.get("milkIssuesTotalsMap");
context.milkIssuesMap=milkIssuesMap;
context.milkIssuesTotalsMap=milkIssuesTotalsMap;

//  CLOSING BALANCE=========================
if("INT7".equals(deptId) && UtilValidate.isNotEmpty(deptId)){
	convPurchaseList.each{eachConPurList->
		if(UtilValidate.isNotEmpty(eachConPurList)){
			for(Map.Entry convMilkReceiptsData : eachConPurList.entrySet()){
				String productId = convMilkReceiptsData.getKey();
				BigDecimal quantity = convMilkReceiptsData.getValue().get("quantity");
				BigDecimal kgFat = convMilkReceiptsData.getValue().get("kgFat");
				BigDecimal kgSnf = convMilkReceiptsData.getValue().get("kgSnf");
				if(UtilValidate.isNotEmpty(quantity)){
					if(UtilValidate.isEmpty(closingBalanceMap) || (UtilValidate.isNotEmpty(closingBalanceMap) && UtilValidate.isEmpty(closingBalanceMap.get(productId)))){
						Map tempOpenMap = FastMap.newInstance();
						tempOpenMap.put("productId", productId);
						tempOpenMap.put("quantity", quantity);
						tempOpenMap.put("kgFat",kgFat);
						tempOpenMap.put("kgSnf",kgSnf);
						closingBalanceMap.put(productId,tempOpenMap);
					 }else{
						Map tempMap = FastMap.newInstance();
						tempMap=(Map) closingBalanceMap.get(productId);
						BigDecimal tempQty =(BigDecimal) tempMap.get("quantity");
						tempMap.put("quantity", ((BigDecimal) tempMap.get("quantity"))+quantity);
						tempMap.put("kgFat", ((BigDecimal) tempMap.get("kgFat"))+kgFat);
						tempMap.put("kgSnf", ((BigDecimal) tempMap.get("kgSnf"))+kgSnf);
						closingBalanceMap.put(productId,tempMap);
					}
				}
			}
		}
	}
}
if(UtilValidate.isNotEmpty(openingBalProductMap)){
	for(Map.Entry openingBalDetails : openingBalProductMap.entrySet()){
		String productId = openingBalDetails.getValue().get("productId");
		BigDecimal quantity = openingBalDetails.getValue().get("quantity");
		BigDecimal kgFat = openingBalDetails.getValue().get("fatKg");
		BigDecimal kgSnf = openingBalDetails.getValue().get("snfKg");
		if(UtilValidate.isNotEmpty(quantity)){
			if(UtilValidate.isEmpty(closingBalanceMap) || (UtilValidate.isNotEmpty(closingBalanceMap) && UtilValidate.isEmpty(closingBalanceMap.get(productId)))){
				Map tempOpenMap = FastMap.newInstance();
				tempOpenMap.put("productId", productId);
				tempOpenMap.put("quantity", quantity);
				tempOpenMap.put("kgFat",kgFat);
				tempOpenMap.put("kgSnf",kgSnf);
				closingBalanceMap.put(productId,tempOpenMap);
			 }else{
			 	Map tempMap = FastMap.newInstance();
				tempMap=(Map) closingBalanceMap.get(productId);
				tempMap.put("quantity", ((BigDecimal) tempMap.get("quantity"))+quantity);
				tempMap.put("kgFat", ((BigDecimal) tempMap.get("kgFat"))+kgFat);
				tempMap.put("kgSnf", ((BigDecimal) tempMap.get("kgSnf"))+kgSnf);
				closingBalanceMap.put(productId,tempMap);
			}
		}
	}
} 

if(UtilValidate.isNotEmpty(intReturnsAndReceipts)){
	for(Map.Entry intReturnsAndReceiptsMap : intReturnsAndReceipts.entrySet()){
		productId = intReturnsAndReceiptsMap.getKey();
		quantity = intReturnsAndReceiptsMap.getValue().get("receivedQuantity");
		kgFat = intReturnsAndReceiptsMap.getValue().get("receivedKgFat");
		kgSnf = intReturnsAndReceiptsMap.getValue().get("receivedKgSnf");
		if(UtilValidate.isNotEmpty(quantity)){
			if(UtilValidate.isEmpty(closingBalanceMap) || (UtilValidate.isNotEmpty(closingBalanceMap) && UtilValidate.isEmpty(closingBalanceMap.get(productId)))){
				Map tempOpenMap = FastMap.newInstance();
				tempOpenMap.put("productId", productId);
				tempOpenMap.put("quantity", quantity);
				tempOpenMap.put("kgFat",kgFat);
				tempOpenMap.put("kgSnf",kgSnf);
				closingBalanceMap.put(productId,tempOpenMap);
			 }else{
				Map tempMap = FastMap.newInstance();
				tempMap=(Map) closingBalanceMap.get(productId);
				BigDecimal tempQty =(BigDecimal) tempMap.get("quantity");
				tempMap.put("quantity", ((BigDecimal) tempMap.get("quantity"))+quantity);
				tempMap.put("kgFat", ((BigDecimal) tempMap.get("kgFat"))+kgFat);
				tempMap.put("kgSnf", ((BigDecimal) tempMap.get("kgSnf"))+kgSnf);
				closingBalanceMap.put(productId,tempMap);
			}
		}
	}
}
if(UtilValidate.isNotEmpty(milkIssuesMap)){
	for(Map.Entry milkIssues : milkIssuesMap.entrySet()){
		productId = milkIssues.getKey();
		quantity = milkIssues.getValue().get("issuedQuantity");
		kgFat = milkIssues.getValue().get("issuedKgFat");
		kgSnf = milkIssues.getValue().get("issuedKgSnf");
		if(UtilValidate.isNotEmpty(quantity)){
			if(UtilValidate.isEmpty(closingBalanceMap) || (UtilValidate.isNotEmpty(closingBalanceMap) && UtilValidate.isEmpty(closingBalanceMap.get(productId)))){
				Map tempOpenMap = FastMap.newInstance();
				tempOpenMap.put("productId", -productId);
				tempOpenMap.put("quantity", quantity);
				tempOpenMap.put("kgFat",kgFat);
				tempOpenMap.put("kgSnf",kgSnf);
				closingBalanceMap.put(productId,tempOpenMap);
			 }else{
				Map tempMap = FastMap.newInstance();
				tempMap=(Map) closingBalanceMap.get(productId);
				tempMap.put("quantity", ((BigDecimal) tempMap.get("quantity"))-quantity);
				tempMap.put("kgFat", ((BigDecimal) tempMap.get("kgFat"))-kgFat);
				tempMap.put("kgSnf", ((BigDecimal) tempMap.get("kgSnf"))-kgSnf);
				closingBalanceMap.put(productId,tempMap);
			 }
		}
	}
}

// Closing Balance Final Map
Map closingBalanceFinalMap=FastMap.newInstance();
Map closingBalanceFinalTotalMap=FastMap.newInstance();
if(UtilValidate.isNotEmpty(closingBalanceMap)){
	BigDecimal totClosingQty = BigDecimal.ZERO;
	BigDecimal totClosingFatKg = BigDecimal.ZERO;
	BigDecimal totClosingSnfKg = BigDecimal.ZERO;
	
	for(Map.Entry closingBalanceDetails : closingBalanceMap.entrySet()){
		quantity = closingBalanceDetails.getValue().get("quantity");
		kgFat = closingBalanceDetails.getValue().get("kgFat");
		kgSnf = closingBalanceDetails.getValue().get("kgSnf");
		totClosingQty=totClosingQty+quantity;
		totClosingFatKg=totClosingFatKg+kgFat;
		totClosingSnfKg=totClosingSnfKg+kgSnf;
		
		fatPercent = ProcurementNetworkServices.calculateFatOrSnf(kgFat, quantity);
		snfPercent = ProcurementNetworkServices.calculateFatOrSnf(kgSnf, quantity);
		tempFinalMap=[:];
		tempFinalMap.put("quantity",closingBalanceDetails.getValue().get("quantity"));
		tempFinalMap.put("kgFat",closingBalanceDetails.getValue().get("kgFat"));
		tempFinalMap.put("kgSnf",closingBalanceDetails.getValue().get("kgSnf"));
		tempFinalMap.put("fat",fatPercent);
		tempFinalMap.put("snf",snfPercent);
		closingBalanceFinalMap.putAt(closingBalanceDetails.getKey(), tempFinalMap);
	}
	closingBalanceFinalTotalMap.put("totClosingQty",totClosingQty);
	closingBalanceFinalTotalMap.put("totClosingFatKg",totClosingFatKg);
	closingBalanceFinalTotalMap.put("totClosingSnfKg",totClosingSnfKg);
	
}
context.closingBalanceFinalMap=closingBalanceFinalMap;
context.closingBalanceFinalTotalMap=closingBalanceFinalTotalMap;



// OB + Receipts
BigDecimal obAndReceiptQty = BigDecimal.ZERO;
BigDecimal obAndReceiptFatKg = BigDecimal.ZERO;
BigDecimal obAndReceiptSnfKg = BigDecimal.ZERO;
if(UtilValidate.isNotEmpty(openingBalProductTotalMap)){
	obAndReceiptQty=totReceiptQty+openingBalProductTotalMap.get("quantity");
	obAndReceiptFatKg=totReceiptFat+openingBalProductTotalMap.get("fatKg");
	obAndReceiptSnfKg=totReceiptSnf+openingBalProductTotalMap.get("snfKg");
	context.obAndReceiptQty=obAndReceiptQty;
	context.obAndReceiptFatKg=obAndReceiptFatKg;
	context.obAndReceiptSnfKg=obAndReceiptSnfKg;
}
// CB + ISSUES
BigDecimal cbAndIssueQty = BigDecimal.ZERO;
BigDecimal cbAndIssueFatKg = BigDecimal.ZERO;
BigDecimal cbAndIssueSnfKg = BigDecimal.ZERO;
if(UtilValidate.isNotEmpty(closingBalanceFinalTotalMap)){
	if(UtilValidate.isNotEmpty(milkIssuesTotalsMap)){
		cbAndIssueQty=milkIssuesTotalsMap.get("totIssuedQty")+closingBalanceFinalTotalMap.get("totClosingQty");
		cbAndIssueFatKg=milkIssuesTotalsMap.get("totIssuedKgFat")+closingBalanceFinalTotalMap.get("totClosingFatKg");
		cbAndIssueSnfKg=milkIssuesTotalsMap.get("totIssuedKgSnf")+closingBalanceFinalTotalMap.get("totClosingSnfKg");
	}else{
		cbAndIssueQty=closingBalanceFinalTotalMap.get("totClosingQty");
		cbAndIssueFatKg=closingBalanceFinalTotalMap.get("totClosingFatKg");
		cbAndIssueSnfKg=closingBalanceFinalTotalMap.get("totClosingSnfKg");
	}
	context.cbAndIssueQty=cbAndIssueQty;
	context.cbAndIssueFatKg=cbAndIssueFatKg;
	context.cbAndIssueSnfKg=cbAndIssueSnfKg;
	
}

// (OB+RECEIPTS) - (CB + ISSUES)

BigDecimal diffQty = obAndReceiptQty-cbAndIssueQty
BigDecimal diffKgFat = obAndReceiptFatKg-cbAndIssueFatKg
BigDecimal diffKgSnf = obAndReceiptSnfKg-cbAndIssueSnfKg
context.diffQty=diffQty;
context.diffKgFat=diffKgFat;
context.diffKgSnf=diffKgSnf;



