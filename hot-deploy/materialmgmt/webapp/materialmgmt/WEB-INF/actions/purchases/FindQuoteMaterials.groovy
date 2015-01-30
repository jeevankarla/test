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
import java.text.ParseException;

userLogin= context.userLogin;
materialId = parameters.materialId;
materialId_op= parameters.materialId_op;

if((materialId_op).equals("notEqual"))
	{
		conditionList = [];
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, materialId));
		conditionList.add(EntityCondition.makeCondition("productName", EntityOperator.NOT_EQUAL, materialId));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		productList = delegator.findList("Product", condition, null, null, null, false);
		productNameList = EntityUtil.getFieldListFromEntityList(productList, "productName", true);
		
		List productIdList = EntityUtil.getFieldListFromEntityList(productList, "productId", true);
		if(UtilValidate.isEmpty(productIdList)){
			productIdList.add(" ");
		}
		parameters.productId = productIdList;
		parameters.productId_op = "in";
	}
if((materialId_op).equals("contains") )
	{
		conditionList = [];
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.LIKE, "%"+materialId+"%"));
		conditionList.add(EntityCondition.makeCondition("productName", EntityOperator.LIKE, "%"+materialId+"%"));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.OR);
		productList = delegator.findList("Product", condition, null, null, null, false);
		productNameList = EntityUtil.getFieldListFromEntityList(productList, "productName", true);
		
		List productIdList = EntityUtil.getFieldListFromEntityList(productList, "productId", true);
		if(UtilValidate.isEmpty(productIdList)){
			productIdList.add(" ");
		}
		parameters.productId = productIdList;
		parameters.productId_op = "in";
	}
if((materialId_op).equals("equals") )
	{
		conditionList = [];
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, materialId));
		conditionList.add(EntityCondition.makeCondition("productName", EntityOperator.EQUALS, materialId));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.OR);
		productList = delegator.findList("Product", condition, null, null, null, false);
		productNameList = EntityUtil.getFieldListFromEntityList(productList, "productName", true);
		
		List productIdList = EntityUtil.getFieldListFromEntityList(productList, "productId", true);
		if(UtilValidate.isEmpty(productIdList)){
			productIdList.add(" ");
		}
		parameters.productId = productIdList;
		parameters.productId_op = "in";
	}
if((materialId_op).equals("like") )
	{
		conditionList = [];
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.LIKE, materialId+"%"));
		conditionList.add(EntityCondition.makeCondition("productName", EntityOperator.LIKE, materialId+"%"));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.OR);
		productList = delegator.findList("Product", condition, null, null, null, false);
		productNameList = EntityUtil.getFieldListFromEntityList(productList, "productName", true);
		
		List productIdList = EntityUtil.getFieldListFromEntityList(productList, "productId", true);
		if(UtilValidate.isEmpty(productIdList)){
			productIdList.add(" ");
		}
		parameters.productId = productIdList;
		parameters.productId_op = "in";
	}









