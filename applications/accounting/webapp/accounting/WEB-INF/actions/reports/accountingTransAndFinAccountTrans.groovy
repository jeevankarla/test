
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

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.accounting.util.UtilAccounting;
import org.ofbiz.party.party.PartyWorker;
import org.ofbiz.base.util.*
import org.ofbiz.minilang.SimpleMapProcessor
import org.ofbiz.content.ContentManagementWorker
import org.ofbiz.content.content.ContentWorker
import org.ofbiz.content.data.DataResourceWorker
import java.sql.Date;
import java.sql.Timestamp;

import javolution.util.FastList;

acctgTransId = "";
accountingTransEntryList = [];
accountingTransEntries = [:];
finAccountTransId = parameters.finAccountTransId;
acctgTransId = parameters.acctgTransId;
if(UtilValidate.isNotEmpty(acctgTransId)){
	accountingTransEntries = delegator.findOne("AcctgTrans",[acctgTransId : acctgTransId] , false);
}else{
	if(UtilValidate.isNotEmpty(finAccountTransId)){
		accountingTransList = delegator.findList("AcctgTrans",EntityCondition.makeCondition("finAccountTransId", EntityOperator.EQUALS , finAccountTransId)  , null, null, null, false );
		if(UtilValidate.isNotEmpty(accountingTransList)){
			accountingTransEntries = EntityUtil.getFirst(accountingTransList);
			if(UtilValidate.isNotEmpty(accountingTransEntries)){
				acctgTransId = accountingTransEntries.acctgTransId
			}
		}
	}
}
context.put("accountingTransEntries",accountingTransEntries);

if(UtilValidate.isNotEmpty(acctgTransId)){
	accountingTransEntryList = delegator.findList("AcctgTransEntry",EntityCondition.makeCondition("acctgTransId", EntityOperator.EQUALS , acctgTransId)  , null, null, null, false );
}
context.put("accountingTransEntryList",accountingTransEntryList);

if(UtilValidate.isNotEmpty(parameters.reportTypeFlag)){
	reportTypeFlag = parameters.reportTypeFlag;
	context.put("reportTypeFlag",reportTypeFlag);
}




