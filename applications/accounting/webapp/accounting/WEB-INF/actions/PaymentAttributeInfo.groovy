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
	import org.ofbiz.accounting.payment.PaymentWorker;
	
	userLogin= context.userLogin;
	chequeInFavour = parameters.chequeInFavour;
	paymentId = parameters.paymentId;
	condList = [];
	condList.add(EntityCondition.makeCondition("paymentId", EntityOperator.EQUALS, paymentId));
	List PaymentAttributes = delegator.findList("PaymentAttribute", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
	if(UtilValidate.isNotEmpty(PaymentAttributes)){  
         PaymentAttributeValues = EntityUtil.getFirst(PaymentAttributes);
 	     attrValue=PaymentAttributeValues.attrValue;
 	     chequeInFavour=attrValue;
	}
   context.chequeInFavour=chequeInFavour;
	
   
   finAccntTransSequenceEntry = EntityUtil.getFirst(delegator.findList("FinAccntTransSequence", EntityCondition.makeCondition("finAccountTransId", EntityOperator.EQUALS, finAccountTransId), null, null, null, false));
   finAccntTransSequence = "";
   if(finAccntTransSequenceEntry){
	   finAccntTransSequence = finAccntTransSequenceEntry.transSequenceId;
	   context.finAccntTransSequence = finAccntTransSequence;
   }
	
	
	
	
	
