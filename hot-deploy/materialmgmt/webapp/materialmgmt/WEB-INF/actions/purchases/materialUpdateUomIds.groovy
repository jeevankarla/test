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
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.product.product.ProductWorker;

import org.ofbiz.entity.util.EntityUtil;
uomList=[];
condList=[];
productId=parameters.productId;
if("y".equals(parameters.productflag))
{
	JSONArray productJSONList = new JSONArray();
	
	productDetails=ProductWorker.findProduct(delegator, productId);
	JSONObject productObj = new JSONObject();
	productObj.put("productName",productDetails.productName);
	productObj.put("longDescription",productDetails.longDescription);
	request.setAttribute("productObj", productObj);
	return "success";
}

JSONArray dataJSONList = new JSONArray();
innerUomId=parameters.innerUomId;
condList.add(EntityCondition.makeCondition("uomTypeId", EntityOperator.NOT_IN, UtilMisc.toList("CURRENCY_MEASURE", "TIME_FREQ_MEASURE")));
cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
uomList = delegator.findList("Uom", cond, null, ["description"], null, false);
uomList.each{eachuom ->
	JSONObject newUomObj = new JSONObject();
	newUomObj.put("value",eachuom.uomId);
	newUomObj.put("text",eachuom.description+"[" +eachuom.abbreviation+" ]");
	dataJSONList.add(newUomObj);
}
context.dataJSONList=dataJSONList;




