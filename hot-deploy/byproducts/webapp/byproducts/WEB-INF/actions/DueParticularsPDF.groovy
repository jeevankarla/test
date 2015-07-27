import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
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
import org.ofbiz.service.DispatchContext;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.product.price.PriceServices;
import in.vasista.vbiz.byproducts.ByProductReportServices;
import org.ofbiz.base.util.Debug;

dctx=dispatcher.getDispatchContext();
if(UtilValidate.isNotEmpty(parameters.customTimePeriodId)){
	customTimePeriod =delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
	fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
	dayBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
	dayEnd = UtilDateTime.getDayEnd(thruDateTime , timeZone, locale);
	context.fromDateTime = fromDateTime;
	context.thruDateTime = thruDateTime;
}
enableOBbyParty = Boolean.FALSE;
tenantConfigEnableOBbyParty = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId", "LMS", "propertyName","enableOBbyParty"), false);
if (tenantConfigEnableOBbyParty && (tenantConfigEnableOBbyParty.getString("propertyValue")).equals("Y")) {
	enableOBbyParty = Boolean.TRUE;
}

isByParty = Boolean.TRUE;
fromDateStr = parameters.fromDate;
thruDateStr = parameters.thruDate;
categoryTypeEnum=parameters.categoryTypeEnum;
context.categoryTypeEnum = categoryTypeEnum;
fromDateTime=UtilDateTime.nowTimestamp();
thruDateTime=UtilDateTime.nowTimestamp();

if (UtilValidate.isNotEmpty(fromDateStr)) {
	def sdf = new SimpleDateFormat("MMMM dd, yyyy");
	try {
		fromDateTime = new java.sql.Timestamp(sdf.parse(fromDateStr+" 00:00:00").getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + fromDateStr, "");
	}
}
if (UtilValidate.isNotEmpty(thruDateStr)) {
	def sdf = new SimpleDateFormat("MMMM dd, yyyy");
	try {
		thruDateTime = new java.sql.Timestamp(sdf.parse(thruDateStr+" 00:00:00").getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + thruDateStr, "");
	}
}
boothsList=[];
dayBegin = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(thruDateTime);
context.fromDateTime = fromDateTime;
context.thruDateTime = thruDateTime;

/*conditionList=[];
conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS , "BOOTH"));
conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.NOT_EQUAL , null));
conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.NOT_EQUAL, "2093"));
conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN , UtilMisc.toList("S1174","S1169","B30101","B30202")));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
booths = delegator.findList("Facility", condition, null, UtilMisc.toList("facilityId"), null, false);
boothsList = EntityUtil.getFieldListFromEntityList(booths, "facilityId", false);*/

boothsList=ByProductNetworkServices.getAllBooths(delegator,categoryTypeEnum).get("boothsList");
boothTotals=[:];
returnBoothTotals=[:];
boothTotalsWithReturn=[:];
periodBoothTotals=[:];
List shipmentIds = ByProductNetworkServices.getAllShipmentIds(delegator, dayBegin, dayEnd);//include Adhoc SALE

boothTotals = ByProductNetworkServices.getByProductDayWiseInvoiceTotals(dctx, UtilMisc.toMap("fromDate", dayBegin, "thruDate", dayEnd, "facilityList", boothsList, "userLogin", userLogin, "isByParty", isByParty)).get("boothInvoiceTotalMap");
inputMap = [];
penaltyResult = ByProductNetworkServices.getChequePenaltyTotals(dctx, UtilMisc.toMap("fromDate", dayBegin, "thruDate", dayEnd,"facilityList", boothsList, "userLogin", userLogin));
facilityPenaltyMap = penaltyResult.get("facilityPenalty");
returnPaymentReferences = penaltyResult.get("returnPaymentReferences");
facilityPenaltyPaymentIdsMap= penaltyResult.get("facilityPenaltyPaymentIdsMap");

returnBoothTotals = ByProductNetworkServices.getDaywiseProductReturnTotal(dctx, UtilMisc.toMap("fromDate",dayBegin,"thruDate" ,dayEnd,"facilityList", [], "isByParty",isByParty)).get("productReturnTotals");
List<GenericValue> paymentsList = FastList.newInstance();
conditionList=[];
facilityIdsList=[];

boothPaidDetail=[:];
boothPaidDetail = ByProductNetworkServices.getByProductPaymentDetails(dctx, UtilMisc.toMap("fromDate",dayBegin,"thruDate" ,dayEnd,"facilityList", boothsList, "isByParty",isByParty)).get("facilityPaidMap");

categoryTotalMap = [:];
categorysList = [];
categorysParloursList = [];
BigDecimal totaRETNAmount=BigDecimal.ZERO;
Iterator boothTotIter = boothTotals.entrySet().iterator();
partyFacilityMap = [:];
if(isByParty){
	ownerPartyList = delegator.findList("Facility", EntityCondition.makeCondition("facilityId", EntityOperator.IN, boothsList), UtilMisc.toSet("ownerPartyId"), null, null, false);
	boothsList.clear();
	boothsList = EntityUtil.getFieldListFromEntityList(ownerPartyList, "ownerPartyId", true);
	partyFacilityMap = ByProductNetworkServices.getFacilityOwnerMap(dctx, [ownerPartyIds : boothsList]).get("partyFacilityMap");
	
}

boothsList.each{  boothId->
	
	boothFacility = boothId;
	facList = partyFacilityMap.get(boothId);
	if(facList){
		boothFacility = facList.get(0);
	}
	BigDecimal totalRevenue=BigDecimal.ZERO;
	if(UtilValidate.isNotEmpty(boothTotals.get(boothId))){
		totalRevenue=boothTotals.get(boothId);
	}
	BigDecimal paymentAmount=BigDecimal.ZERO;
	if(UtilValidate.isNotEmpty(boothPaidDetail.get(boothId))){
		paymentAmount=boothPaidDetail.get(boothId);
	}
	
	BigDecimal invoiceAmount = totalRevenue;
	BigDecimal chequePenality=BigDecimal.ZERO;
	BigDecimal chequePenalityPaidAmount=BigDecimal.ZERO;
	if(UtilValidate.isNotEmpty(facilityPenaltyMap.get(boothId))){
		chequePenality=facilityPenaltyMap.get(boothId);
	}
	if(UtilValidate.isNotEmpty(facilityPenaltyPaymentIdsMap.get(boothId))){
		penaltyPaymentIdsList=facilityPenaltyPaymentIdsMap.get(boothId);
		penaltyPaymentIdsList.each{paymentId->
			returnDetail = returnPaymentReferences.get(paymentId);
			if(returnDetail){
				chequePenalityPaidAmount=chequePenalityPaidAmount.add(returnDetail.get("amount"));
			}
		}
	}
	
	BigDecimal returnAmount=BigDecimal.ZERO;
	if(UtilValidate.isNotEmpty(returnBoothTotals.get(boothId))){
		returnAmount=(returnBoothTotals.get(boothId)).get("totalAmount");
		totaRETNAmount=totaRETNAmount.add(returnAmount);
		//invoiceAmount=invoiceAmount.add(returnAmount);
	}
	
	invoiceAmount=invoiceAmount.add(chequePenality);
	BigDecimal totalPaidAmnt=(paymentAmount+returnAmount);
	
	BigDecimal netAmount =(BigDecimal) invoiceAmount.subtract(totalPaidAmnt);
	BigDecimal openingBalance=BigDecimal.ZERO;
	boothTotalsMap=[:];
	if(reportTypeFlag=="DuesAbstractReport"){
		if(enableOBbyParty){
			openingBalance = (ByProductNetworkServices.getOpeningBalanceForParty( dctx , [userLogin: userLogin, saleDate: dayBegin, partyId:boothId])).get("openingBalance");
		}
		else{
			openingBalance =(ByProductNetworkServices.getOpeningBalanceForBooth( dctx , [userLogin: userLogin ,saleDate: dayBegin , facilityId:boothFacility, isByParty:isByParty])).get("openingBalance");
		}
		boothTotalsMap.put("openingBalance", openingBalance);
		netAmount=netAmount.add(openingBalance);
	}
	netAmount=netAmount.add(chequePenalityPaidAmount);//to match with closing Balance
	
   boothTotalsMap.put("facilityId", boothId);
   boothTotalsMap.put("invoiceAmount", totalRevenue+chequePenality);
   boothTotalsMap.put("returnAmount", returnAmount);
   boothTotalsMap.put("chequeRetnAmount", chequePenality);
   boothTotalsMap.put("chequePenalityPaidAmount", chequePenalityPaidAmount);
   boothTotalsMap.put("totalPaid", totalPaidAmnt);
   boothTotalsMap.put("netAmount", netAmount);
   if(openingBalance!=0|| totalPaidAmnt!=0 || totalRevenue!=0 ){
	facility = delegator.findOne("Facility",[facilityId : boothId], false);
	if(UtilValidate.isEmpty(facility)){
		facilityBooth =EntityUtil.getFirst(delegator.findList("Facility", EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, boothId),  UtilMisc.toSet("facilityId"), null, null, false));
		facility = delegator.findOne("Facility",[facilityId : facilityBooth.get("facilityId")], false);
	}
	if(	UtilValidate.isNotEmpty(facility)&& facility.categoryTypeEnum){
		categoryType = facility.categoryTypeEnum;
			if(categoryTotalMap.containsKey(categoryType)){
			tempCatList = categoryTotalMap.get(categoryType);
			tempCatList.addAll(boothTotalsMap);
			categoryTotalMap.putAt(categoryType, tempCatList);
		}else{
			tempList = [];
			tempList.add(boothTotalsMap);
			categoryTotalMap.putAt(categoryType, tempList);
			categorysList.add(categoryType);
		}
   }
}
}

categorysParloursList.add("PARLOUR");
categorysParloursList.add("PARLOUR_EX");
context.categoryTotalMap = categoryTotalMap;
if(parloursOnly == "Y"){
	context.categorysList = categorysParloursList;
}else{
	context.categorysList = categorysList;
}
categoryCsvList=[];
if(UtilValidate.isNotEmpty(parameters.csvFlag) && parameters.csvFlag=="dueParticularsCSV"){
	sno=1;
	grandOpeningDebit=0;
	grandOpeningCredit=0;
	grandInvAmt=0;
	grandChqAmnt=0;
	grandClosingDebit=0;
	grandClosingCredit=0;
	finalMap=[:];
	categorysList.each{category->
		catTempMap=[:];
		categoryDetails=delegator.findOne("Enumeration",[enumId:category],false);
		catTempMap.categoryName=categoryDetails.description;
		invTot=0;
		catDrOB=0;
		catCrOB=0;
		returnCatAmount=0;
		cashTot=0;
		chqTot=0;
		chqRetnTot=0;
		challanTot=0;
		totalCatPaidAmnt=0;
		netBalTot=0;
		chequePPaidAmountCat=0;
		eatot=0;
		satot=0;
		batot=0;
		duesData=categoryTotalMap.get(category);
		if(UtilValidate.isNotEmpty(duesData)){
			duesData.each{data->
				tempMap=[:];
				tempMap.sno=sno;
				/*categoryDetails=delegator.findOne("Enumeration",[enumId:category],false);
				tempMap.categoryName=categoryDetails.description;*/
				facilityId=data.get("facilityId");
				tempMap.facilityId=facilityId;
				facilityDetails=delegator.findOne("Facility",[facilityId:facilityId],false);
				tempMap.facilityName=facilityDetails.facilityName;
				invoiceAmount=data.get("invoiceAmount");
				tempMap.invoiceAmount=invoiceAmount;
				invTot=invTot+invoiceAmount;
				returnAmount=data.get("returnAmount");
				chequeRetnAmount=data.get("chequeRetnAmount");
				chequePenalityPaidAmount=data.get("chequePenalityPaidAmount");
				tempMap.chequePenalityPaidAmount=chequePenalityPaidAmount;
				chequePPaidAmountCat=chequePPaidAmountCat+chequePenalityPaidAmount;
				totalPaid=data.get("totalPaid");
				tempMap.totalPaid=totalPaid;
				totalCatPaidAmnt=totalCatPaidAmnt+totalPaid;
				netAmount=data.get("netAmount");
				closingDebit=0;
				closingCredit=0;
				if(netAmount>=0){
					closingDebit=netAmount;
				}else{
					closingCredit=-(netAmount);
				}
				tempMap.closingDebit=closingDebit;
				tempMap.closingCredit=closingCredit;
				eatot=eatot+closingCredit;
				satot=satot+closingDebit;
				openingBalance=data.get("openingBalance");
				openingDebit=0;
				openingCredit=0;
				if(openingBalance>=0){
					openingDebit=openingBalance;
				}else{
					openingCredit=-(openingBalance);
				}
				tempMap.openingDebit=openingDebit;
				tempMap.openingCredit=openingCredit;
				catDrOB=catDrOB+openingDebit;
				catCrOB=catCrOB+openingCredit;
				categoryCsvList.add(tempMap);
				sno+=1;
			}
			catTempMap.facilityId="TOTAL :";
			catTempMap.openingDebit=catDrOB;
			grandOpeningDebit=grandOpeningDebit+catDrOB;
			catTempMap.openingCredit=catCrOB;
			grandOpeningCredit=grandOpeningCredit+catCrOB;
			catTempMap.invoiceAmount=invTot;
			grandInvAmt=grandInvAmt+invTot;
			catTempMap.chequePenalityPaidAmount=chequePPaidAmountCat;
			grandChqAmnt=grandChqAmnt+chequePPaidAmountCat;
			catTempMap.closingDebit=satot;
			grandClosingDebit=grandClosingDebit+satot;
			catTempMap.closingCredit=eatot;
			grandClosingCredit=grandClosingCredit+eatot;
			categoryCsvList.add(catTempMap);
		}
	}
	finalMap.facilityId="GRAND TOTAL :";
	finalMap.openingDebit=grandOpeningDebit;
	finalMap.openingCredit=grandOpeningCredit
	finalMap.invoiceAmount=grandInvAmt;
	finalMap.chequePenalityPaidAmount=grandChqAmnt;
	finalMap.closingDebit=grandClosingDebit;
	finalMap.closingCredit=grandClosingCredit;
	categoryCsvList.add(finalMap);
}
//Debug.log("categoryCsvList========================="+categoryCsvList);
context.categoryCsvList=categoryCsvList;
