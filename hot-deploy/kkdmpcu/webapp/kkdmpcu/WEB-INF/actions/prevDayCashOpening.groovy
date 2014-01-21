/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import java.util.Calendar;
import java.util.List;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.service.DispatchContext;
import java.math.MathContext;
import javax.naming.Context;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.network.NetworkServices;
import org.ofbiz.network.LmsServices;

cashTransactionList = [];
fromDateTime = null;
thruDateTime = null;

fromDate = parameters.fromDate;
thruDate = parameters.thruDate;
def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
try {
	fromDateTime = new java.sql.Timestamp(sdf.parse(fromDate+" 00:00:00").getTime());
	thruDateTime = new java.sql.Timestamp(sdf.parse(thruDate+" 00:00:00").getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+fromDate, "");
}
pmSupplyDate=UtilDateTime.getDayStart(fromDateTime,-1);
monthDayBegin = UtilDateTime.getDayStart(pmSupplyDate, timeZone, locale);
monthDayEnd = UtilDateTime.getDayEnd(pmSupplyDate, timeZone, locale);
prevDate = 0;
totRemittance = 0;
totMilkValue = 0;
totReceipts = 0;
cashTransactionMap = [:];
totPaymentTypeWise = [:];
previousTotPaymentMap = [:];
cardCash = 0;
rtCash = 0;
vehShort = 0;
otherCash = 0;
othgerReceipts = 0;

for( k=0 ; k <= (UtilDateTime.getIntervalInDays(monthDayBegin,monthDayEnd)); k++){
	selectedDate = UtilDateTime.addDaysToTimestamp(monthDayBegin,k);
	dayNumber = UtilDateTime.getDayOfMonth(selectedDate, timeZone, locale);
	dayBegin = UtilDateTime.getDayStart(selectedDate, timeZone, locale);
	dayEnd = UtilDateTime.getDayEnd(selectedDate, timeZone, locale);
	java.sql.Date supplyDateBegin = new java.sql.Date(dayBegin.getTime());
	java.sql.Date supplyDateEnd = new java.sql.Date(dayEnd.getTime());

	List condition = UtilMisc.toList(
		EntityCondition.makeCondition("supplyDate", EntityOperator.GREATER_THAN_EQUAL_TO, supplyDateBegin));
	condition.add(EntityCondition.makeCondition("supplyDate", EntityOperator.LESS_THAN_EQUAL_TO, supplyDateEnd));
	condition.add(EntityCondition.makeCondition("amountRemitted", EntityOperator.NOT_EQUAL, null));
	EntityCondition Conds = EntityCondition.makeCondition(condition, EntityOperator.AND);
	bankRemittanceDetail = delegator.findList("BankRemittance", Conds, null, null, null, false);
	
	for(int j=0; j< bankRemittanceDetail.size();j++){
		cashTransactionMap = [:];
		bankRemittanceItem = bankRemittanceDetail.get(j);
		cashTransactionMap["date"] = supplyDateBegin;
		cashTransactionMap["REMIT_CASH"] = bankRemittanceItem.amountRemitted;
		if((bankRemittanceItem.amountRemitted != 0)){
			cashTransactionList.add(cashTransactionMap);
			totRemittance += bankRemittanceItem.amountRemitted;
		}
	}
	List conditionList = UtilMisc.toList(
				EntityCondition.makeCondition("paymentDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
		conditionList.add(EntityCondition.makeCondition("paymentDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
		conditionList.add(EntityCondition.makeCondition("amount", EntityOperator.NOT_EQUAL, null));
		EntityCondition Cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	paymentDetailList = delegator.findList("Payment", Cond, null, null, null, false);
	cashTransactionMap = [:];
	
	dayTotals = NetworkServices.getDayTotals(dispatcher.getDispatchContext(), selectedDate, false, false);
	if(dayNumber != prevDate){
		if((dayTotals.totalRevenue != 0)){
			cashTransactionMap["date"] = supplyDateBegin;
			cashTransactionMap["milkValue"] = dayTotals.totalRevenue;
			totMilkValue += dayTotals.totalRevenue;
		}
	}
	prevDate = dayNumber;
	for(int i=0; i< paymentDetailList.size();i++){
		paymentDetail = paymentDetailList.get(i);
		if((paymentDetail.amount != 0)){
			if(paymentDetail.paymentTypeId == "SALES_PAYIN" || paymentDetail.paymentTypeId == "ADVDEPOSIT_PAYIN"){
				pmtDetail = "RT-MILKCASH";
			}else{
				pmtDetail = paymentDetail.paymentTypeId;
			}
			try {		
				cashTransactionMap["date"] = supplyDateBegin;
				if(cashTransactionMap[pmtDetail]== null){
					cashTransactionMap[pmtDetail] = paymentDetail.amount;
					totReceipts += paymentDetail.amount;
				}else{
					vrReceipts = cashTransactionMap.get(pmtDetail);
					vrReceipts += paymentDetail.amount;
					cashTransactionMap[pmtDetail] = vrReceipts;
					totReceipts += paymentDetail.amount;
				}
				//dynamic totalpaymanttypemap
				/*if(totPaymentTypeWise[pmtDetail]== null){
					totPaymentTypeWise[pmtDetail] = paymentDetail.amount;
				}else{
					vrReceiptsAllDays = totPaymentTypeWise.get(pmtDetail);
					vrReceiptsAllDays += paymentDetail.amount;
					totPaymentTypeWise[pmtDetail] = vrReceiptsAllDays;
				}*/
				if(pmtDetail == "RT-MILKCASH"){
					rtCash += paymentDetail.amount;
				} else if (pmtDetail == "CARDDEPOSIT_PAYIN") {
					cardCash +=  paymentDetail.amount;
				} else if (pmtDetail == "TRANSPORTER_PAYIN") {
					vehShort +=  paymentDetail.amount;
				} else if (pmtDetail == "CURD_PAYIN"||pmtDetail == "BUTTER_PAYIN"){
					othgerReceipts += paymentDetail.amount;
				}else{
				 	otherCash += paymentDetail.amount;
				}
			} catch (Exception e) {
				Debug.logError(e, "Cannot add null object on VrReceipts", "");
			}
		}
	}
	if(UtilValidate.isNotEmpty(cashTransactionMap)){
		cashTransactionList.add(cashTransactionMap);
	}
	trReceipt = 0;
	trRecMap = [:];
	Map transporterDuesMap = LmsServices.getTransporterDues(dispatcher.getDispatchContext(), UtilMisc.toMap("userLogin", userLogin,"fromDate", dayBegin, "thruDate", dayEnd));
	List transporterDueList = (List) transporterDuesMap.get("transporterDuesList");
	BigDecimal transptDueAmt = BigDecimal.ZERO;
	if(UtilValidate.isNotEmpty(transporterDueList)){
		for(int l=0; l< transporterDueList.size();l++){
			Map transporterDue = (Map) transporterDueList.get(l);
			transptDueAmt = (BigDecimal) transporterDue.get("amount");
			trReceipt += transptDueAmt;
		}
		trRecMap["trReceipt"] = trReceipt+cashTransactionMap.get("RT-MILKCASH");
		trRecMap["date"] = supplyDateBegin;
		cashTransactionList.add(trRecMap);
	}
	totPaymentTypeWise["rtCash"] = rtCash;
	totPaymentTypeWise["cardCash"] = cardCash;
	totPaymentTypeWise["vehShort"] = vehShort;
	totPaymentTypeWise["otherCash"] = otherCash;
	totPaymentTypeWise["othgerReceipts"] = othgerReceipts;
}
cashTransactionMap["totMilkValue"] = totMilkValue;
cashTransactionMap["totReceipts"] = totReceipts;
cashTransactionMap["totRemittance"] = totRemittance;

previousTotPaymentMap.putAll(cashTransactionMap);
context.put("previousTotPaymentMap", previousTotPaymentMap);