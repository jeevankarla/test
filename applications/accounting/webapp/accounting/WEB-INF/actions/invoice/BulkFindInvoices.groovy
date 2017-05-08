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

import org.ofbiz.entity.*
import org.ofbiz.base.util.UtilDateTime;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javolution.util.FastList;
import org.ofbiz.entity.Delegator;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;

invoiceId = parameters.invoiceId;
fromDate = parameters.fromDate;
thruDate = parameters.thruDate;
invoiceTypeId=parameters.invoiceTypeId;
context.fromDate=fromDate;
context.thruDate=thruDate;
Timestamp fromdate1=null;
Timestamp thrudate1=null;

Debug.log("Date======"+parameters.ownerPartyId+userLogin);
if(UtilValidate.isNotEmpty(fromDate)||UtilValidate.isNotEmpty(thruDate)){
	def sdf = new SimpleDateFormat("yy-MM-dd");
	
	try {
		if (UtilValidate.isNotEmpty(fromDate)) {
			fromdate1 = new java.sql.Timestamp(sdf.parse(fromDate).getTime());
		}
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
		return;
	}
	try {
		if (UtilValidate.isNotEmpty(thruDate)) {
			thrudate1 = new java.sql.Timestamp(sdf.parse(thruDate).getTime());
		}
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
		return;
	}
	thruDateEnd = UtilDateTime.getDayEnd(thrudate1, timeZone, locale);

}
List invoiceItems = [];
conditionList = [];
if(invoiceId){
	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
}
if(fromdate1){
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(fromdate1)));
}
if(thrudate1){
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDateEnd)));
}
if(parameters.partyId){
conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, parameters.partyId));
}
if(invoiceTypeId){
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS,invoiceTypeId));
}
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, [ "INVOICE_READY","INVOICE_PAID"]));
cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
Debug.log("condi====="+cond);
invoiceItemList = delegator.findList("InvoiceAndType", cond, null, null, null, false);
Debug.log("Invoices==="+invoiceItemList);
context.invoices = invoiceItemList;
