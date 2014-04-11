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
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
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
import org.ofbiz.service.DispatchContext;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.product.price.PriceServices;



import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import java.sql.*;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.service.DispatchContext;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;

conditionList = [];
invoiceList = [];

shipmentId = null;
shipmentIds = [];
requestedFacilityId = null;
Timestamp estimatedDeliveryDateTime = null;

if(parameters.estimatedShipDate){
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	try {
		estimatedDeliveryDateTime = new java.sql.Timestamp(formatter.parse(parameters.estimatedShipDate).getTime());
		
	} catch (ParseException e) {
	}
}

context.put("estimatedDeliveryDateTime", estimatedDeliveryDateTime);


if(parameters.facilityId){
	requestedFacilityId = parameters.facilityId;
}
if(parameters.shipmentId){
	if(parameters.shipmentId == "allRoutes"){
		shipments = delegator.findByAnd("Shipment", [estimatedShipDate : estimatedDeliveryDateTime , shipmentTypeId : parameters.shipmentTypeId ],["routeId"]);
		shipmentIds.addAll(EntityUtil.getFieldListFromEntityList(shipments, "shipmentId", false));
	}else{
		shipmentId = parameters.shipmentId;
		shipmentIds.add(shipmentId);
	}
}
if(context.shipmentId){
	shipmentId = context.shipmentId;
	shipmentIds.add(shipmentId);
}
if(parameters.shipmentIds){
	shipmentIds = parameters.shipmentIds;
	shipmentId=shipmentIds[0];
}
if(context.shipmentIds){
	shipmentIds = context.shipmentIds;
	shipmentId=shipmentIds[0];
}

if(parameters.invoiceId){
	invoiceList.add(parameters.invoiceId);
}else{
	shipmentId = parameters.shipmentId;
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));
	//conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS, "CR_INST"));
	
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL , "ORDER_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL ,"ORDER_REJECTED"));
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	fieldsToSelect = ["invoiceId"] as Set;
	orderInvoicesList = delegator.findList("OrderHeaderFacAndItemBillingInv", condition, fieldsToSelect, ["parentFacilityId","originFacilityId"], null, false);
	invoiceList = EntityUtil.getFieldListFromEntityList(orderInvoicesList, "invoiceId", true);
}
	context.invoiceIds = invoiceList;
    return "success";
	
	
	
	
	
	