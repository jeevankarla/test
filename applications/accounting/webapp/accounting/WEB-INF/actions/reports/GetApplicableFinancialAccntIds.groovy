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
	import org.ofbiz.entity.condition.EntityCondition;
	import org.ofbiz.entity.condition.EntityOperator;
	import org.ofbiz.entity.util.EntityUtil;
	import org.ofbiz.base.util.UtilDateTime;
	import org.ofbiz.base.util.UtilValidate;
	import org.ofbiz.base.util.UtilNumber;
	import org.ofbiz.accounting.util.UtilAccounting;
	import java.math.BigDecimal;
	import com.ibm.icu.util.Calendar;
	import org.ofbiz.base.util.*;
	
	if (organizationPartyId) {
		
		glAccnt = delegator.findList("GlAccountOrganizationAndClass", EntityCondition.makeCondition(["organizationPartyId" : organizationPartyId]), null, null, null, true);
		glAccntIds = EntityUtil.getFieldListFromEntityList(glAccnt, "glAccountId", true);
		
		financialAccnt = delegator.findList("FinAccount", EntityCondition.makeCondition("postToGlAccountId", EntityOperator.IN, glAccntIds), null, null, null, false);
		//glAccntIds = EntityUtil.getFieldListFromEntityList(glAccnt, "glAccountId", true);
		
	    context.financialAccnt = financialAccnt;
	   
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
