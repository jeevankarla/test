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
monthDayBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
monthDayEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);
totRemittance = 0;
totMilkValue = 0;
trReceiptTot = 0;
totReceipts = 0;
cashTransactionTotalsMap = [:];
totPaymentTypeWise = [:];
totPaymentTypeWiseList = [];
totTransptDueAmt = 0;
try {
	for( k=0 ; k <= (UtilDateTime.getIntervalInDays(monthDayBegin,monthDayEnd)); k++){
		selectedDate = UtilDateTime.addDaysToTimestamp(monthDayBegin,k);
		dayBegin = UtilDateTime.getDayStart(selectedDate, timeZone, locale);
		dayEnd = UtilDateTime.getDayEnd(selectedDate, timeZone, locale);
		java.sql.Date supplyDateBegin = new java.sql.Date(dayBegin.getTime());
	//bank Remittance Details
		List condition = UtilMisc.toList(
			EntityCondition.makeCondition("supplyDate", EntityOperator.EQUALS, supplyDateBegin));
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
	//Milk value Details
		cashTransactionMap = [:];
		dayTotals = NetworkServices.getDayTotals(dispatcher.getDispatchContext(), selectedDate, false, false);
		if((dayTotals.totalRevenue != 0)){
			cashTransactionMap["date"] = supplyDateBegin;
			cashTransactionMap["milkValue"] = dayTotals.totalRevenue;
			totMilkValue += dayTotals.totalRevenue;
		}
	
	//Payment Details
		List conditionList = UtilMisc.toList(
				EntityCondition.makeCondition("paymentDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
			conditionList.add(EntityCondition.makeCondition("paymentDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("PMNT_VOID","PMNT_CANCELLED")));
			conditionList.add(EntityCondition.makeCondition("amount", EntityOperator.NOT_EQUAL, null));
			conditionList.add(EntityCondition.makeCondition("paymentTypeId", EntityOperator.NOT_EQUAL, "TRANS_CREDIT_PAYIN"));
			EntityCondition Cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		paymentDetailList = delegator.findList("Payment", Cond, null, null, null, false);
	
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
				//for total payment typewise
					if(totPaymentTypeWise[pmtDetail]== null){
						totPaymentTypeWise[pmtDetail] = paymentDetail.amount;
					}else{
						vrReceiptsAllDays = totPaymentTypeWise.get(pmtDetail);
						vrReceiptsAllDays += paymentDetail.amount;
						totPaymentTypeWise[pmtDetail] = vrReceiptsAllDays;
					}
				} catch (Exception e) {
					Debug.logError(e, "Cannot add null object on VrReceipts", "");
				}
			}
		}
		if(UtilValidate.isNotEmpty(cashTransactionMap)){
			cashTransactionList.add(cashTransactionMap);
		}
	//for TransporterDues
		dueReceipt = 0;
		trRecMap = [:];
		Map transporterDuesMap = LmsServices.getTransporterDues(dispatcher.getDispatchContext(), UtilMisc.toMap("userLogin", userLogin,"fromDate", dayBegin, "thruDate", dayEnd));
		List transporterDueList = (List) transporterDuesMap.get("transporterDuesList");
	
		if(UtilValidate.isNotEmpty(transporterDueList)){
			for(int l=0; l< transporterDueList.size();l++){
				transptDueAmt = 0;
				Map transporterDue = (Map) transporterDueList.get(l);
				transptDueAmt = transporterDue.get("amount");
				dueReceipt += transptDueAmt;
				totTransptDueAmt += transptDueAmt;
			}
			tempTrPayments =0;
			if(UtilValidate.isNotEmpty(cashTransactionMap.get("TRANSPORTER_PAYIN"))){
				tempTrPayments =cashTransactionMap.get("TRANSPORTER_PAYIN")
			}
			trRecMap["trReceipt"] = (cashTransactionMap.get("RT-MILKCASH")+tempTrPayments-dueReceipt);
			trReceiptTot += (cashTransactionMap.get("RT-MILKCASH")+tempTrPayments-dueReceipt);
			trRecMap["date"] = supplyDateBegin;
			cashTransactionList.add(trRecMap);
		}
	}

	cashTransactionTotalsMap["totMilkValue"] = totMilkValue;
	cashTransactionTotalsMap["totReceipts"] = totReceipts - totTransptDueAmt;
	cashTransactionTotalsMap["totRemittance"] = totRemittance;
	cashTransactionTotalsMap["trReceiptTot"] = trReceiptTot;

	context.put("cashTransactionTotalsMap", cashTransactionTotalsMap);
	context.put("cashTransactionList", cashTransactionList);
	context.put("totPaymentTypeWise", totPaymentTypeWise);
	context.put("totTransptDueAmt", totTransptDueAmt);
} catch (Exception e) {
  //ignore
}
//Debug.logInfo(" cashTransactionList====================================="+ cashTransactionList, "");
//Debug.logInfo(" cashTransactionList====================================="+ totPaymentTypeWise, "");