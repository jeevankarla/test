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

import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;

import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.sql.Timestamp;
import org.ofbiz.base.util.UtilMisc;
if(UtilValidate.isNotEmpty(parameters.findPaymentMethodType)){
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.NOT_LIKE, "%_PAYOUT%"));
	conditionList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "MONEY"));
	List paymentMethodTypeList = delegator.findList("PaymentMethodType", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
	context.paymentMethodTypeList=paymentMethodTypeList;
	}