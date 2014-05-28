import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.io.ObjectOutputStream.DebugTraceInfoStack;
import java.lang.*;
import java.math.BigDecimal;
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

import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.byproducts.ByProductServices;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductReportServices;

userLogin= context.userLogin;
fromDate=parameters.fromDate;
thruDate=parameters.thruDate;
partyCode = parameters.boothId;
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
def sdf1 = new SimpleDateFormat("dd.MM.yyyy");

fromDateTime = UtilDateTime.getDayStart(fromDateTime);
dayBegin = UtilDateTime.getDayEnd(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(thruDateTime);
context.fromDate = fromDateTime;
context.thruDate = thruDateTime;
maxIntervalDays=UtilDateTime.getIntervalInDays(fromDateTime,thruDateTime);

if(maxIntervalDays > 32){
	Debug.logError("You Cannot Choose More Than 31 Days.","");
	context.errorMessage = "You Cannot Choose More Than 31 Days";
	return;
}
boothIdsList = [];
resultMap = ByProductServices.getAllByproductBooths(delegator, fromDateTime);
boothsList = resultMap.get("boothsList");

facilityDesc = [:];
if(boothsList){
	boothsList.each{ eachBooth ->
		eachBoothId = eachBooth.facilityId;
		eachBoothId = eachBoothId.toUpperCase();
		eachBoothName = eachBooth.facilityName;
		if(!boothIdsList.contains(eachBoothId)){
			boothIdsList.add(eachBoothId);
		}
		facilityDesc.putAt(eachBoothId, eachBoothName);
	}
}
if(partyCode){
	boothIdsList.clear();
	partyCode = partyCode.toUpperCase();
	boothIdsList.add(partyCode);
}

context.facilityDesc = facilityDesc;
boothSummary = [:];
partyWiseLedger = [:];
boothOBMap = new TreeMap();

daywiseReceipts = ByProductNetworkServices.getByProductPaymentDetails(dctx, UtilMisc.toMap("fromDate",fromDateTime,"thruDate" ,dayEnd,"facilityList", boothIdsList)).get("paymentDetails");
invoiceResult = ByProductNetworkServices.getByProductDayWiseInvoiceTotals(dctx, UtilMisc.toMap("fromDate", fromDateTime, "thruDate", dayEnd, "facilityList", boothIdsList, "userLogin", userLogin)).get("dayWisePartyInvoiceDetail");
penaltyResult = ByProductNetworkServices.getByProductDaywisePenaltyTotals(dctx, fromDateTime, dayEnd, boothIdsList, userLogin);

penalty = penaltyResult.get("facilityPenalty");
returnPaymentReferences = penaltyResult.get("returnPaymentReferences");
maxIntervalDays=UtilDateTime.getIntervalInDays(fromDateTime,thruDateTime);
if(boothIdsList){
	boothIdsList.each{eachBooth ->
		paymentDetailList = [];
		grandTotalMap = [:];
		skipCounter = 0;
		testFlag = 0;
		openingBalance = 0;
		closingBalance = 0;
		tempOB = 0;
		startingDate = fromDateTime;
		paymentDetailList = [];
		boothPenalty = penalty.get(eachBooth);
		tempPaymentList = [];
		for(k = 1;k<=maxIntervalDays+1;k++){
			tempDayBalanceMap = [:];
			firstRowCounter = 0;
			dayStart = UtilDateTime.getDayStart(startingDate);
			saleAmt = 0;
			stDate = UtilDateTime.toDateString(startingDate, "dd.MM.yyyy");
			if(testFlag == 0){
				testFlag = 1;
				openingBalance =	( ByProductNetworkServices.getOpeningBalanceForBooth( dctx , [userLogin: userLogin ,saleDate: dayStart , facilityId:eachBooth])).get("openingBalance");
				//openingBalance = (ByProductNetworkServices.getOpeningBalanceForByProductFacilities(dctx, [facilityId: eachBooth, userLogin: userLogin ,saleDate: dayStart])).get("openingBalance");
			}else{
				openingBalance = closingBalance;
			}
			invoiceId = "";
			if(invoiceResult.get(dayStart)){
				tempDaySale = invoiceResult.get(dayStart);
				if(tempDaySale && tempDaySale.get(eachBooth)){
					saleAmt = (tempDaySale.get(eachBooth)).get("amount");
					invoiceId = (tempDaySale.get(eachBooth)).get("invoiceId");
				}
			}
			tempOB = openingBalance;
			receiptAmt = [:];
			
			if(daywiseReceipts.get(eachBooth)){
				boothReceipts = daywiseReceipts.get(eachBooth);
				if(boothReceipts && boothReceipts.get(dayStart)){
					receiptAmt = boothReceipts.get(dayStart);
				}
			}
			chequeNo = "";
			returnChequeNo = "";
			returnAmount = 0;
			chequeDate = "";
			amount = 0;
			tempDayBalanceMap.put("stDate", stDate);
			tempDayBalanceMap.put("openingBalance", tempOB);
			tempDayBalanceMap.put("invoiceId", invoiceId);
			tempDayBalanceMap.put("saleAmount", saleAmt);
			
			if(receiptAmt){
				if(receiptAmt.get("payment")){
					payments = receiptAmt.get("payment");
					tempPaymentList.addAll(payments);
					for(i=0;i<payments.size();i++){
						eachPayment = payments.get(i);
						chequeDate = eachPayment.get("chequeDate");
						chequeNo = eachPayment.get("paymentRefNum");
						if(!chequeNo){
							chequeNo = "CASH";
							chequeDate = "";
						}
						
						if(chequeDate){
							chequeDate = UtilDateTime.toDateString(chequeDate, "dd.MM.yyyy")	
						}
						amount = eachPayment.get("amount");
						if(firstRowCounter == 0){
							closingBalance = tempOB-amount+saleAmt;
							tempDayBalanceMap.putAt("receipts", amount);
							tempDayBalanceMap.putAt("chequeDate", chequeDate);
							tempDayBalanceMap.putAt("chequeNo", chequeNo);
							tempDayBalanceMap.putAt("chequeReturn", "");
							tempDayBalanceMap.putAt("returnAmt", 0);
							tempDayBalanceMap.putAt("penaltyAmt", 0);
							tempDayBalanceMap.putAt("closingBalance", closingBalance);
							paymentDetailList.addAll(tempDayBalanceMap);
						}else{
							closingBalance = tempOB-amount;
							tempMap = [:];
							tempMap.put("stDate", "");
							tempMap.put("openingBalance", "");
							tempMap.put("invoiceId", "");
							tempMap.put("saleAmt", 0);
							tempMap.putAt("receipts", amount);
							tempMap.putAt("chequeDate", chequeDate);
							tempMap.putAt("chequeNo", chequeNo);
							tempMap.putAt("chequeReturn", "");
							tempMap.putAt("returnAmt", 0);
							tempMap.putAt("penaltyAmt", 0);
							tempMap.putAt("closingBalance", closingBalance);
							paymentDetailList.addAll(tempMap);
							
						}
						firstRowCounter++;
						tempOB = closingBalance;
					}
				}
				if(receiptAmt.get("chequeReturn")){
					chequeReturnList = receiptAmt.get("chequeReturn");
					tempPaymentList.addAll(chequeReturnList);
					for(i=0;i<chequeReturnList.size();i++){
						chequeReturns = chequeReturnList.get(i);
						chequeNo = chequeReturns.get("paymentRefNum");
						chequeDate = chequeReturns.get("chequeDate");
						returnAmount = chequeReturns.get("amount");
						if(chequeDate){
							chequeDate = UtilDateTime.toDateString(chequeDate, "dd.MM.yyyy");
						}
						if(firstRowCounter == 0){
							closingBalance = tempOB+saleAmt-returnAmount;
							tempDayBalanceMap.putAt("receipts", returnAmount);
							tempDayBalanceMap.putAt("chequeDate", chequeDate);
							tempDayBalanceMap.putAt("chequeNo", chequeNo);
							tempDayBalanceMap.putAt("chequeReturn", "");
							tempDayBalanceMap.putAt("returnAmt", 0);
							tempDayBalanceMap.putAt("penaltyAmt", 0);
							tempDayBalanceMap.putAt("closingBalance", closingBalance);
							paymentDetailList.addAll(tempDayBalanceMap);
						}else{
							closingBalance = tempOB-returnAmount;
							tempMap = [:];
							tempMap.put("stDate", "");
							tempMap.put("openingBalance", "");
							tempMap.put("invoiceId", "");
							tempMap.put("saleAmt", 0);
							tempMap.putAt("receipts", returnAmount);
							tempMap.putAt("chequeDate", chequeDate);
							tempMap.putAt("chequeNo", chequeNo);
							tempMap.putAt("chequeReturn", "");
							tempMap.putAt("returnAmt", 0);
							tempMap.putAt("penaltyAmt", 0);
							tempMap.putAt("closingBalance", closingBalance);
							paymentDetailList.addAll(tempMap);
						}
						firstRowCounter++;
						tempOB = closingBalance;
					}
				}
			}
			else{
				if(saleAmt){
					closingBalance = tempOB+saleAmt;
				}
				else{
					closingBalance = tempOB;
				}
				
				tempDayBalanceMap.put("chequeNo", "");
				tempDayBalanceMap.put("chequeDate", "");
				tempDayBalanceMap.put("chequeReturn", "");
				tempDayBalanceMap.put("returnAmt", 0);
				tempDayBalanceMap.put("receipts", 0);
				tempDayBalanceMap.putAt("penaltyAmt", 0);
				tempDayBalanceMap.put("closingBalance", closingBalance);
				if(firstRowCounter == 0){
					paymentDetailList.addAll(tempDayBalanceMap);
				}
				tempOB = closingBalance;
				firstRowCounter++;
			}
			if(closingBalance == 0 && saleAmt == 0){
				skipCounter++;
			}
			startingDate = UtilDateTime.addDaysToTimestamp(startingDate, 1);
		}
		tempOpenBal = 0;
		balCounter = 0;
		closeBal = 0;
		openBal = 0;
		total_Sale = 0;
		total_Receipt = 0;
		total_Penalty = 0;
		total_returnAmt = 0;
		tempPaymentDetailList = [];
		tempDate = null;
		multiEntries = false;
		paymentDetailList.each{eachItem ->
			tempMap = [:];
			counterFlag = 0;
			startDate = eachItem.get("stDate");
			OB = eachItem.get("openingBalance");
			salAmt = eachItem.get("saleAmount");
			if(!salAmt){
				salAmt = 0;
			}
			total_Sale = total_Sale+salAmt;
			payment = eachItem.get("receipts");
			invId = eachItem.get("invoiceId");
			total_Receipt = total_Receipt+payment
			cheqDate = eachItem.get("chequeDate");
			cheqNo = eachItem.get("chequeNo");
			cheqRet = eachItem.get("chequeReturn");
			retAmt = eachItem.get("returnAmt");
			penAmt = eachItem.get("penaltyAmt");
			if(balCounter == 0){
				openBal = OB;
				grandTotalMap.put("periodOB", openBal);
			}
			else{
				openBal = closeBal;
			}
			tempOpenBal = openBal;
			
			currDay = null;
			
			if(startDate){
				try {
					currDay = new java.sql.Timestamp(sdf1.parse(startDate).getTime());
				} catch (ParseException e) {
					Debug.logError(e, "Cannot parse date string: " + startDate, "");
					displayGrid = false;
				}
				multiEntries = true;
				tempDate = currDay;
			}
			else{
				multiEntries = false;
				currDay = tempDate;
			}
			fDate = UtilDateTime.toDateString(currDay, "dd/MM");
			tempMap.put("stDate", fDate);
			tempMap.put("openingBalance", openBal);
			tempMap.put("invoiceId", invId);
			tempMap.put("saleAmount", salAmt);
			tempMap.put("receipts", payment);
			tempMap.put("chequeDate", cheqDate);
			tempMap.put("chequeNo", cheqNo);
			returnChequeDate = "";
			
			tempPenaltyList = [];
			if(boothPenalty){
				tempPenaltyList = boothPenalty.get(currDay);
			}
			if(tempPenaltyList && multiEntries){
				tempPenaltyList.each{ eachPenalty ->
					penaltyAmount = eachPenalty.get("amount");
					total_Penalty = total_Penalty+penaltyAmount;
					paymentId = eachPenalty.get("paymentId");
					returnDetail = returnPaymentReferences.get(paymentId);
					referenceNum = "";
					returnAmt = 0;
					if(returnDetail){
						returnAmt = returnDetail.get("amount");
						referenceNum = returnDetail.get("referenceNum");
					}
					
					if(tempPaymentList){
						tempPaymentList.each{eachPay ->
							if(referenceNum == eachPay.get("paymentRefNum")){
								returnAmt = eachPay.get("amount");
								total_returnAmt = total_returnAmt+returnAmt;
							}
						}
					}
					if(counterFlag == 0){
						closeBal = tempOpenBal+salAmt-payment+returnAmt+penaltyAmount;
						tempMap.put("chequeReturn", referenceNum);
						tempMap.put("penaltyAmt", penaltyAmount);
						tempMap.put("returnAmt", returnAmt);
						tempMap.put("closingBalance", closeBal);
						tempPaymentDetailList.addAll(tempMap);
					}
					else {
						closeBal = tempOpenBal+returnAmt+penaltyAmount;
						newTempMap = [:];
						newTempMap.put("stDate", "");
						newTempMap.put("openingBalance", "");
						newTempMap.put("invoiceId", "");
						newTempMap.put("saleAmount", 0);
						newTempMap.put("receipts", 0);
						newTempMap.put("chequeDate", "");
						newTempMap.put("chequeNo", "");
						newTempMap.put("chequeReturn", referenceNum);
						newTempMap.put("penaltyAmt", penaltyAmount);
						newTempMap.put("returnAmt", returnAmt);
						newTempMap.put("closingBalance", closeBal);
						tempPaymentDetailList.addAll(newTempMap);
					}
					counterFlag++;
				}
			}
			else{
				if(counterFlag == 0){
					closeBal = tempOpenBal+salAmt-payment;
					tempMap.put("chequeReturn", "");
					tempMap.put("penaltyAmt", penAmt);
					tempMap.put("returnAmt", retAmt);
					tempMap.put("closingBalance", closeBal);
					tempPaymentDetailList.addAll(tempMap);
				}
				else{
					closeBal = tempOpenBal-payment;
					newTempMap = [:];
					newTempMap.put("stDate", "");
					newTempMap.put("openingBalance", "");
					newTempMap.put("invoiceId", "");
					newTempMap.put("saleAmount", 0);
					newTempMap.put("receipts", payment);
					newTempMap.put("chequeDate", cheqDate);
					newTempMap.put("chequeNo", cheqNo);
					newTempMap.put("chequeReturn", "");
					newTempMap.put("penaltyAmt", penAmt);
					newTempMap.put("returnAmt", retAmt);
					newTempMap.put("closingBalance", closeBal);
					tempPaymentDetailList.addAll(newTempMap);
				}
				counterFlag++;
			}
			balCounter++;
		}
		grandTotalMap.put("totalSale", total_Sale);
		grandTotalMap.put("totalReceipt", total_Receipt);
		grandTotalMap.put("periodCB", closeBal);
		grandTotalMap.put("totalReturn", total_returnAmt);
		grandTotalMap.put("totalPenalty", total_Penalty);
		if(skipCounter-1 != maxIntervalDays){
			boothOBMap.put(eachBooth, tempPaymentDetailList);
			boothSummary.put(eachBooth, grandTotalMap);
		}
	}
}
context.put("partyWiseLedger", boothOBMap);
context.put("boothSummary", boothSummary);


