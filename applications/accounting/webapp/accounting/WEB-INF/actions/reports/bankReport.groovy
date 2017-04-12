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
 
 import java.util.ArrayList;
 import org.ofbiz.base.util.UtilHttp;
 import org.ofbiz.base.util.UtilMisc;
 import org.ofbiz.base.util.UtilNumber;
 import org.ofbiz.entity.condition.EntityCondition;
 import org.ofbiz.entity.condition.EntityOperator;
 import org.ofbiz.entity.util.EntityUtil;
 import org.ofbiz.base.util.*;
 import org.ofbiz.entity.Delegator;
 import org.ofbiz.entity.GenericEntityException;
 import org.ofbiz.entity.GenericValue;
 import org.ofbiz.entity.util.EntityUtil;
 import org.ofbiz.entity.condition.EntityCondition;
 import org.ofbiz.entity.condition.EntityOperator;
 import org.ofbiz.service.LocalDispatcher;
 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import javolution.util.FastList;
 import javolution.util.FastMap;
 import org.ofbiz.base.util.UtilMisc;
 import java.math.RoundingMode;
 import java.sql.Timestamp;
 import java.util.List;
 import java.text.SimpleDateFormat;
 import javax.swing.text.html.parser.Entity;
 import in.vasista.vbiz.byproducts.ByProductNetworkServices;
 import in.vasista.vbiz.byproducts.ByProductServices;
 import org.ofbiz.product.product.ProductWorker;
 import java.util.Map;
 import org.ofbiz.entity.util.EntityFindOptions;
 import org.ofbiz.service.ServiceUtil;
 import org.ofbiz.network.LmsServices;
 import in.vasista.vbiz.byproducts.TransporterServices;
 import org.ofbiz.party.party.PartyHelper;
 import in.vasista.vbiz.humanres.HumanresService;
 import org.ofbiz.service.GenericServiceException;
 
 dctx = dispatcher.getDispatchContext();
 
 printPaymentsList = [];
 paymentGroupId = parameters.paymentGroupId;
 
 
 
 //finAccountId = parameters.finAccountId;
 context.put("paymentGroupId",paymentGroupId);
 
 paymentDatee = parameters.paymentDate;
 
 SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
 
 
 Timestamp paymentDate = null;
 if(UtilValidate.isNotEmpty(paymentDatee)){
	 
	  try {
		  paymentDate = UtilDateTime.toTimestamp(sdf2.parse(paymentDatee));
		   } catch (ParseException e) {
			   Debug.logError(e, "Cannot parse date string: " + paymentDatee, "");
			   }
	 
  }
  
 paymentRefNum = parameters.paymentRefNum;
 
 context.put("paymentRefNum",paymentRefNum);
 
 partyPaymentGroupMap = [:];
 
 finalMap = [:];
 bankTypeListCSV=[];
 String bankTypeFlag ="SBI";
 String empTypeFlag = "";
 String bankFlag = "";
 if(UtilValidate.isNotEmpty(paymentGroupId)){
	 paymentGroupMemberList = delegator.findList("PaymentGroupMember",EntityCondition.makeCondition("paymentGroupId", EntityOperator.EQUALS , paymentGroupId), null, null, null, false );
	 
	 GenericValue paymentGroup = delegator.findOne("PaymentGroup", [paymentGroupId :paymentGroupId], false);
	 paymentDate=paymentGroup.paymentDate;
	 String accPin = "";
	 
	 if(paymentGroup && UtilValidate.isNotEmpty(paymentGroup.finAccountId)){
		 finAccDetails = delegator.findOne("BankAccount", UtilMisc.toMap("bankAccountId", paymentGroup.finAccountId), false);
		 if(finAccDetails && !UtilValidate.isEmpty(finAccDetails.finAccountPin)){
			 accPin = finAccDetails.finAccountPin;
		 }
	 }
	 
	 if(UtilValidate.isNotEmpty(paymentGroupMemberList)){
		 
		 paymentIds = EntityUtil.getFieldListFromEntityList(paymentGroupMemberList, "paymentId", true);
		 
		 Map employeeMap = UtilMisc.toMap("userLogin", userLogin,"orgPartyId","Company","fromDate",paymentGroup.paymentDate);
		 Map employees = HumanresService.getActiveEmployements(dctx,employeeMap);
		 List<GenericValue> employementList = (List<GenericValue>)employees.get("employementList");
		 employeeIdList=[];
		 
		 if(UtilValidate.isNotEmpty(employementList)){
			 //employeesList=employees.get("employeesResult").get("employeeList");
			 for(i = 0;i < employementList.size();i ++){
				 employeeId = employementList.get(i).get("partyIdTo");
				 employeeIdList.add(employeeId);
			 }
		 }
		
		 paymentConditionList = [];
		 paymentConditionList.add(EntityCondition.makeCondition("paymentId", EntityOperator.IN , paymentIds));
		 //paymentConditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, employeeIdList));
		 paymentCond = EntityCondition.makeCondition(paymentConditionList, EntityOperator.AND);
		 
		 paymentDetList = delegator.findList("Payment",paymentCond ,null, null, null, true);
		 employeeIds = EntityUtil.getFieldListFromEntityList(paymentDetList, "partyIdTo", true);
		
		 for(int e = 0; e < employeeIds.size(); e++){
			 eachEmp = employeeIds.get(e);
			 bankTypeList =[];
		 if(!UtilValidate.isEmpty(paymentDetList)){
			 
			 partyIdFromList=EntityUtil.getFirst(paymentDetList);
			 branchId = partyIdFromList.partyIdFrom;
			 branchIdForAdd="";
			 branchList = [];
			 condListb = [];
			 if(branchId){
			 condListb.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, branchId));
			 condListb.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
			 condListb = EntityCondition.makeCondition(condListb, EntityOperator.AND);
			 
			 PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdTo"), null, null, false);
			 
			 branchList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);
			 if(!branchList){
				 condListb2 = [];
				 //condListb2.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS,"%"));
				 condListb2.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, branchId));
				 condListb2.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
				 condListb2.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
				 cond = EntityCondition.makeCondition(condListb2, EntityOperator.AND);
				 
				 PartyRelationship1 = delegator.findList("PartyRelationship", cond,UtilMisc.toSet("partyIdFrom"), null, null, false);
				 if(PartyRelationship1){
				 branchDetails = EntityUtil.getFirst(PartyRelationship1);
				 branchIdForAdd=branchDetails.partyIdFrom;
				 }
			 }
			 else{
				 if(branchId){
				 branchIdForAdd=branchId;
				 }
			 }
			 if(!branchList)
			 branchList.add(branchId);
			 }
			 
			 branchBasedWeaversList = [];
			 condListb1 = [];
			 if(branchId){
			 condListb1.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, branchList));
			 condListb1.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
			 condListb = EntityCondition.makeCondition(condListb1, EntityOperator.AND);
			 
			 PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdTo"), null, null, false);
			 branchBasedWeaversList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);
			 
			 if(!branchBasedWeaversList)
			 branchBasedWeaversList.add(branchId);
			 }
			 BOAddress="";
			 if(branchIdForAdd){
			 branchContextForADD=[:];
			 branchContextForADD.put("branchId",branchIdForAdd);
			 try{
				 resultCtx = dispatcher.runSync("getBoHeader", branchContextForADD);
				 if(ServiceUtil.isError(resultCtx)){
					 Debug.logError("Problem in BO Header ", module);
					 return ServiceUtil.returnError("Problem in fetching financial year ");
				 }
				 if(resultCtx.get("boHeaderMap")){
					 boHeaderMap=resultCtx.get("boHeaderMap");
					 
					 if(boHeaderMap.get("header0")){
						 BOAddress=boHeaderMap.get("header0");
					 }
				 }
			 }catch(GenericServiceException ee){
				 Debug.logError(ee, module);
				 return ServiceUtil.returnError(ee.getMessage());
			 }
			 context.BOAddress=BOAddress;
			 }
			 List conditionList = [];
			 conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, eachEmp));
			 /*conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "FNACT_ACTIVE"));
*/			 paramCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			 
			 List<String> orderBy = UtilMisc.toList("-fromDate");
			 finAccountDet = delegator.findList("BankAccount", paramCond, null, orderBy, null, false);
			 totnetSal=0;
			
			 
			 paymentConditionList1 = [];
			 paymentConditionList1.add(EntityCondition.makeCondition("paymentId", EntityOperator.IN , paymentIds));
			 paymentConditionList1.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, eachEmp));
			 paymentCond1 = EntityCondition.makeCondition(paymentConditionList1, EntityOperator.AND);
			 
			 paymentDetList1 = delegator.findList("Payment",paymentCond1 ,null, null, null, false );
			 amount = 0;
			 for(int z = 0; z < paymentDetList1.size(); z++){
				 
				 tempMap = [:];
				 
				 paymentDet = paymentDetList1.get(z);
				 
				 partyIdTo = paymentDet.partyIdTo;
				 txnDate = paymentDet.transactionDate;
				 paymentDate = paymentDet.paymentDate;
				 amount = amount + paymentDet.amount;
				 totnetSal+=amount;
				 partyName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, partyIdTo, false);
				 
				 SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMM");
				 paymentDt = sdf1.format(paymentDate);
				 
				 tempMap.put("empId",partyIdTo);
				 tempMap.put("empName",partyName);
				 tempMap.put("empDate",paymentDt);
				 tempMap.put("txnDate",txnDate);
				 tempMap.put("netSal",amount);
				 tempMap.put("totnetSal",totnetSal);
				 tempMap.put("empty","");
				 EmplPositionDetails = delegator.findList("EmplPosition", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyIdTo), null, null, null, false);
				 if(!UtilValidate.isEmpty(EmplPositionDetails)){
					 EmplPositionDetailsFirst = EntityUtil.getFirst(EmplPositionDetails);
					 emplPositionTypeId = EmplPositionDetailsFirst.get("emplPositionTypeId");
					 gradeTypeId = EmplPositionDetailsFirst.get("gradeTypeId");
					 
					 GenericValue EmplPositionTypeD = delegator.findOne("EmplPositionType", [emplPositionTypeId :emplPositionTypeId], false);
					 if(UtilValidate.isNotEmpty(EmplPositionTypeD)){
						 description=EmplPositionTypeD.get("description");
						 if(UtilValidate.isNotEmpty(description)){
							 if(UtilValidate.isNotEmpty(gradeTypeId)){
								 enumDet = delegator.findOne("Enumeration", [enumId : gradeTypeId], false);
								 if(UtilValidate.isNotEmpty(enumDet)){
									 tempMap.put("position", enumDet.description+" "+description);
								 }
							 }else{
								 tempMap.put("position", description);
							 }
						 }
						 else{
							 shortName=EmplPositionTypeD.shortName;
							 if(UtilValidate.isNotEmpty(shortName)){
								 tempMap.put("position", shortName);
							 }
						 }
					 }
					 else{
						 tempMap.put("position", EmplPositionDetailsFirst.get("name"));
					 }
				 }
				 
				 
				 if(!UtilValidate.isEmpty(finAccountDet)){
					 
					 ownerPartyFinAccList = EntityUtil.filterByCondition(finAccountDet, EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, partyIdTo));
					 if(ownerPartyFinAccList){
						 bankName = EntityUtil.getFirst(ownerPartyFinAccList).bankAccountName;
						 finAccountId = EntityUtil.getFirst(ownerPartyFinAccList).bankAccountId;
						 
						 accntNo = EntityUtil.getFirst(ownerPartyFinAccList).bankAccountCode;
					/*	 branchName = EntityUtil.getFirst(ownerPartyFinAccList).finAccountBranch;*/
						 ifscCode = EntityUtil.getFirst(ownerPartyFinAccList).ifscCode;
						 bCode = EntityUtil.getFirst(ownerPartyFinAccList).branchCode;
						 //finAccountPin = ifscCode.substring(0, 3).toUpperCase();
						 finAccountPin="";
						 if(UtilValidate.isNotEmpty(EntityUtil.getFirst(ownerPartyFinAccList).finAccountPin)){
							 finAccountPin = EntityUtil.getFirst(ownerPartyFinAccList).finAccountPin;
							 finAccountPin = finAccountPin.toUpperCase();
						 }
						 
						 if((finAccountPin.equals("SBI") && accPin.equals("SBI") ) || (finAccountPin.equals("OTHR") && accPin.equals("OTHR")) || UtilValidate.isEmpty(finAccountPin) || UtilValidate.isEmpty(accPin)){
							 bankTypeFlag = "SBI";
						 }
						 else if(finAccountPin.equals("CANARA") && accPin.equals("CANARA")){
							 bankTypeFlag = "CANARA";
						 }
						 
						 tempMap.put("bankName",bankName);
						 tempMap.put("accntNo",accntNo);
						 tempMap.put("ifscCode",ifscCode);
						/* tempMap.put("branchName",branchName);*/
						 tempMap.put("branchCode",bCode);
						 tempMap.put("DrCr","C");
						 tempMap.put("txnType","1");
						 tempMap.put("txnCode","1408");
						 tempMap.put("txnCCY","104");
						 tempMap.put("rateConfig","1.00");
						 tempMap.put("refNo","0");
						 tempMap.put("refDocNo","0");
					 }
				 }
				 
			 }
			 bankTypeList.add(tempMap);
			 bankTypeListCSV.add(tempMap);
			
		 }
		 finalMap.put(eachEmp, bankTypeList);
		
		 
		 }
		 
	 }
 }
 context.finalMap = finalMap;
 context.put("paymentDate",paymentDate);
 context.totnetSal=totnetSal;
 context.bankTypeFlag = bankTypeFlag;
 context.bankTypeList = bankTypeList;
 context.bankTypeListCSV = bankTypeListCSV;
 