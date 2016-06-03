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
 
 import in.vasista.vbiz.byproducts.ByProductNetworkServices;
 
 import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import net.sf.json.JSONArray;
import java.util.SortedMap;
import java.math.RoundingMode;
import javolution.util.FastList;
import org.ofbiz.entity.util.EntityTypeUtil;
//import org.ofbiz.product.inventory.InventoryWorker;
import in.vasista.vbiz.purchase.MaterialHelperServices;
import org.ofbiz.party.party.PartyHelper;


Map formatMap = [:];
List formatList = [];
	
	List<GenericValue> partyClassificationList = null;
		partyClassificationList = delegator.findList("PartyClassification", EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.EQUALS, "REGIONAL_OFFICE"), UtilMisc.toSet("partyId"), null, null,false);
	//Debug.log("partyClassificationList========================"+partyClassificationList);
	
	if(partyClassificationList){
		for (eachList in partyClassificationList) {
			//Debug.log("eachList========================"+eachList.get("partyId"));
			formatMap = [:];
			partyName = PartyHelper.getPartyName(delegator, eachList.get("partyId"), false);
			formatMap.put("productStoreName",partyName);
			formatMap.put("payToPartyId",eachList.get("partyId"));
			formatList.addAll(formatMap);
		}
	}

	context.formatList = formatList;
	resultCtx = dispatcher.runSync("getCustomerBranch",UtilMisc.toMap("userLogin",userLogin));
	Map branchMap = [:];
	List branchList = [];
	List productStoreList = resultCtx.get("productStoreList");
	context.productStoreList = productStoreList;
	
	for (eachList in productStoreList) {
		branchMap = [:];
		branchMap.put("productStoreName",eachList.get("storeName"));
		branchMap.put("payToPartyId",eachList.get("payToPartyId"));
		branchList.addAll(branchMap);
	}
	context.branchList = branchList;
dctx = dispatcher.getDispatchContext();
userLogin= context.userLogin;
organisationList = delegator.findByAnd("PartyRoleAndPartyDetail", [roleTypeId : "INTERNAL_ORGANIZATIO"], ["groupName", "partyId"]);
context.put("organisationList",organisationList);
//departmentList = delegator.findByAnd("PartyRelationshipAndDetail", [roleTypeIdFrom : "INTERNAL_ORGANIZATIO",roleTypeIdTo : "DIVISION"], ["groupName", "partyId"]);
// sub division here
inputMap = [:];
inputMap.put("userLogin", userLogin);
inputMap.put("fromDate", UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(), -730));
Map departmentsMap = MaterialHelperServices.getDivisionDepartments(dctx,inputMap);
if(UtilValidate.isNotEmpty(departmentsMap)){
	departmentList=departmentsMap.get("subDivisionDepartmentList");
}
finalDepartmentList = [];
if(UtilValidate.isNotEmpty(organisationList)){
	finalDepartmentList.addAll(organisationList);
}
if(UtilValidate.isNotEmpty(departmentList)){
	finalDepartmentList.addAll(departmentList);
}
context.put("finalDepartmentList",finalDepartmentList);
context.put("toDepartmentList", finalDepartmentList);

List<String> orderBy = UtilMisc.toList("attrValue");
ledgerFolioList = delegator.findList("ProductAttribute",EntityCondition.makeCondition("attrName", EntityOperator.EQUALS , "LEDGERFOLIONO")  , null, orderBy, null, false );
ledgerFolioList=EntityUtil.getFieldListFromEntityList(ledgerFolioList, "attrValue", true);
context.put("ledgerFolioList",ledgerFolioList);
resultMap=[:];
if(UtilValidate.isNotEmpty(parameters.flag) && (parameters.flag=="DEPT_HEADAPPROVE")){
	inputMap.clear();
	inputMap.put("userLogin",userLogin);
	inputMap.put("partyId",userLogin.partyId);
	inputMap.put("roleTypeIdTo","INDENTDEPTAPPROVER");
	resultMap=MaterialHelperServices.getDepartmentByUserLogin(dctx,inputMap);
	newDepatmentList=EntityUtil.filterByCondition(finalDepartmentList,EntityCondition.makeCondition("partyId",EntityOperator.IN,resultMap.get("deptId")));
}	

if(UtilValidate.isNotEmpty(parameters.custRequestTypeId) && parameters.custRequestTypeId=="INTERNAL_INDENT"){
	inputMap.clear();
	inputMap.put("userLogin",userLogin);
	inputMap.put("partyId",userLogin.partyId);
	inputMap.put("roleTypeIdTo","PRODUCTION_RUN");
	resultMap=MaterialHelperServices.getDepartmentByUserLogin(dctx,inputMap);
	newDepatmentList=EntityUtil.filterByCondition(finalDepartmentList,EntityCondition.makeCondition("partyId",EntityOperator.IN,resultMap.get("deptId")));
}
if(UtilValidate.isNotEmpty(resultMap.get("deptId"))){
	context.put("finalDepartmentList",newDepatmentList);
	context.partyId=resultMap.get("deptId");
}
//Debug.log("finalDepartmentList================="+finalDepartmentList);
	

