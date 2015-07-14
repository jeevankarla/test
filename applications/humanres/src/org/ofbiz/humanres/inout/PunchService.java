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

package org.ofbiz.humanres.inout;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;
import in.vasista.vbiz.humanres.PayrollService;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.security.Security;
/**
 * Services for Party-punches maintenance
 */

public class PunchService {

	public static final String module = PunchService.class.getName();

	public static Map emplPunch(DispatchContext dctx, Map context)

	{

		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		int PunchId = 0;
		String employeePunchId = (String) context.get("employeePunchId");
		String partyId = (String) context.get("partyId");
		String emplPunchId = (String) context.get("emplPunchId");
		//String oldPunchType = (String) context.get("oldPunchType");
		String oldShiftTypeId = (String) context.get("oldShiftTypeId");
		String oldInOut = (String) context.get("oldInOut");
		Date punchdate = (Date) context.get("punchdate");
		Time punchtime = (Time) context.get("punchtime");
		String PunchType = (String) context.get("PunchType");
		String inout = (String) context.get("InOut");
		String note = (String) context.get("Note");
		String isManual = (String) context.get("isManual");
		String shiftTypeId = (String) context.get("shiftType");
		String consolidatedFlag = (String) context.get("consolidatedFlag");
		String flag = "out";
		GenericValue emplPunch = null;
		
		 if(UtilValidate.isNotEmpty(employeePunchId)){
			 try{
				 emplPunch = delegator.findOne("EmplPunch", UtilMisc.toMap("employeePunchId",employeePunchId), false);
		         punchdate = emplPunch.getDate("punchdate");
		         
		         oldShiftTypeId = emplPunch.getString("shiftType");
		         if(UtilValidate.isEmpty(punchtime)){
		        	 punchtime = emplPunch.getTime("punchtime");
		         }
		         if(UtilValidate.isEmpty(PunchType)){
		        	 PunchType = emplPunch.getString("PunchType");
		         }
		         if(UtilValidate.isEmpty(shiftTypeId)){
		        	 shiftTypeId = emplPunch.getString("shiftType");
		         }
		         
		         
			 }catch(Exception e){
				 Debug.logError("Error In fetch employeePunch :"+e.toString(), module);
				 return ServiceUtil
							.returnError("Error In fetch employeePunch :"+e.toString());
			 }
         	
         	
         }
		// Returning error if payroll already generated
		Map customTimePeriodIdMap = PayrollService.checkPayrollGeneratedOrNotForDate(dctx,UtilMisc.toMap("userLogin",userLogin,"date",punchdate));
		if (ServiceUtil.isError(customTimePeriodIdMap)) {
			return customTimePeriodIdMap;
		}	
		String date3 = punchdate.toString();
		String dateArr2[] = date3.split(Pattern.quote("-"));
		String contactMechId = null;
		Security security = dctx.getSecurity();
		int sec = 0, sec0 = 0, sec1 = 0;
		int min = 0, min0 = 0, min1 = 0;
		int hr = 0, hr0 = 0, hr1 = 0;
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        if(UtilValidate.isEmpty(consolidatedFlag)){
        	if (!(security.hasEntityPermission("EMP_PUNCH_OOD", "_UPDATE", userLogin)) && (UtilDateTime.getIntervalInDays(UtilDateTime.toTimestamp(punchdate),nowTimestamp) >0) && PunchType.equals("Ood")) {
            	String errMsg = "you don't have permissoin to edit previous day punch";
            	Debug.logError(errMsg, module);
                return ServiceUtil.returnError(errMsg);
            } 
        }
		String punchout = punchtime.toString();
		String punchintime = null;
		String punchouttime = null;
		int dai = Integer.parseInt(dateArr2[2]);

		int res = dai % 7;
		if ((dai % 7) != 0) {
			PunchId = res;

		} else {
			PunchId = 7;

		}		
		//Debug.log("emplPunchId----" + emplPunchId, module);
		if (emplPunchId == null) {
			try {

				List conditionList = UtilMisc.toList(EntityExpr.makeCondition(
						"partyId", EntityOperator.EQUALS, partyId), EntityExpr
						.makeCondition("PunchType", EntityOperator.EQUALS,
								PunchType), EntityExpr.makeCondition(
						"punchdate", EntityOperator.EQUALS, punchdate),
						EntityExpr.makeCondition("InOut",
								EntityOperator.EQUALS, inout));
				List conditionList1 = UtilMisc.toList(EntityExpr.makeCondition(
						"partyId", EntityOperator.EQUALS, partyId), EntityExpr
						.makeCondition("PunchType", EntityOperator.EQUALS,
								PunchType), EntityExpr.makeCondition(
						"punchdate", EntityOperator.EQUALS, punchdate));

				Map enConList = UtilMisc.toMap("partyId", partyId, "PunchType",
						PunchType, "punchdate", punchdate);
				
				List tops1 = delegator.findByAnd("EmplPunch", enConList);
				Debug.logInfo("after findby..........." + tops1.size(), module);
				//commented out for bio-metric punch
				try {
			    	GenericValue punchInoutTenantConfiguration = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyName", "PUNCHINOUT_CHECK","propertyTypeEnumId","HUMANRES"), false);
			    	 if(UtilValidate.isNotEmpty(punchInoutTenantConfiguration)&& ("Y".equals(punchInoutTenantConfiguration.get("propertyValue")))){
			    		 if (tops1.size() > 0) {
								if ((tops1.size() % 2 == 1) && inout.equals("IN"))
									return ServiceUtil
											.returnError("Please PUNCH-OUT Before PUNCH-IN Again....");
							}
			    	 }
		    	}catch(GenericEntityException e){
		    		 Debug.logError(e, module);    
		    	}
				

				List tops = delegator.findByAnd("EmplPunch", UtilMisc.toMap(
						"partyId", partyId, "PunchType", PunchType,
						"punchdate", punchdate, "InOut", inout));				
				if (tops.size() == 0)
					emplPunchId = new Integer(PunchId).toString();

				else{
					int e = tops.size();
					PunchId = PunchId + 7 * e;
					emplPunchId = new Integer(PunchId).toString();
					
				}

			} catch (GenericEntityException e) {
				return ServiceUtil
						.returnError("Error In generating emplPunchId ");
			}

		}
		context.put("emplPunchId",emplPunchId);
		//Debug.logInfo("emplPunchId===" + emplPunchId, module);

		try {

			List conditionList2 = UtilMisc.toList(EntityExpr.makeCondition(
					"partyId", EntityOperator.EQUALS, partyId), EntityExpr
					.makeCondition("punchdate", EntityOperator.EQUALS,
							punchdate), EntityExpr.makeCondition("PunchType",
					EntityOperator.EQUALS, "Normal"));

			List south = delegator.findByAnd("EmplPunch", UtilMisc.toMap(
					"partyId", partyId, "punchdate", punchdate, "PunchType",
					"Normal"));
			Debug.logInfo("===================[ " + punchdate
					+ " ]=================\n\n", module);

			int sub = south.size();

			if (sub % 2 == 0) {

				/*if (!PunchType.equals("Normal")) {

					return ServiceUtil.returnError("You can't punch for ["
							+ PunchType + "] without Normal Punch ");
				}*/
			}
		} catch (GenericEntityException e) {
			return ServiceUtil
					.returnError("Error in PunchType Please contact adminstrator ");
		}

		try {

			List conditionListb = UtilMisc.toList(EntityExpr.makeCondition(
					"partyId", EntityOperator.EQUALS, partyId), EntityExpr
					.makeCondition("punchdate", EntityOperator.EQUALS,
							punchdate), EntityExpr.makeCondition("PunchType",
					EntityOperator.EQUALS, "Break"));

			List brk = delegator.findByAnd("EmplPunch", UtilMisc.toMap(
					"partyId", partyId, "punchdate", punchdate, "PunchType",
					"Break"));

			int subb = brk.size();

			if (subb % 2 != 0) {

				/*if (!PunchType.equals("Break")) {

					return ServiceUtil
							.returnError(" Please PunchOut for Break First ");
				}*/
			}
		} catch (GenericEntityException e) {
			return ServiceUtil
					.returnError("Error in PunchType[Break] Please contact adminstrator ");
		}

		try {

			List conditionListl = UtilMisc.toList(EntityExpr.makeCondition(
					"partyId", EntityOperator.EQUALS, partyId), EntityExpr
					.makeCondition("punchdate", EntityOperator.EQUALS,
							punchdate), EntityExpr.makeCondition("PunchType",
					EntityOperator.EQUALS, "Lunch"));

			List lun = delegator.findByAnd("EmplPunch", UtilMisc.toMap(
					"partyId", partyId, "punchdate", punchdate, "PunchType",
					"Lunch"));

			int subl = lun.size();

			if (subl % 2 != 0) {

				if (!PunchType.equals("Lunch")) {

					return ServiceUtil
							.returnError(" Please PunchOut for Lunch First ");
				}
			}
		} catch (GenericEntityException e) {
			return ServiceUtil
					.returnError("Error in PunchType[Lunch] Please contact adminstrator ");
		}

		try {

		List check = delegator.findByAnd("EmplPunch", UtilMisc.toMap(
					"partyId", partyId, "PunchType", PunchType, "punchdate",
					punchdate));
		try {
	    	GenericValue punchInoutTenantConfiguration = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyName", "PUNCHINOUT_CHECK","propertyTypeEnumId","HUMANRES"), false);
	    	 if(UtilValidate.isNotEmpty(punchInoutTenantConfiguration)&& ("Y".equals(punchInoutTenantConfiguration.get("propertyValue")))){
	    		 if (check.size() == 0) {

	 				if (inout.equals("OUT")) {
	                      
	 					return ServiceUtil
	 							.returnError("You can't punch out without punchin");
	 				}
	 			}
	    	 }
    	}catch(GenericEntityException e){
    		 Debug.logError(e, module);    
    	}
		
		    //commented out for bio-metric punch
			/*if (check.size() == 0) {

				if (inout.equals("OUT")) {
                     
					return ServiceUtil
							.returnError("You can't punch out without punchin");
				}
			}*/
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("Error Please contact adminstrator");
		}

		// This is to send email to admin if employee punch in after specified
		// time
		try {

			List exprList = UtilMisc.toList(EntityCondition.makeCondition(
					"partyId", EntityOperator.EQUALS, partyId), EntityCondition
					.makeCondition("contactMechTypeId", EntityOperator.EQUALS,
							"EMAIL_ADDRESS"));
			EntityCondition condition = EntityCondition.makeCondition(exprList,
					EntityOperator.AND);
			List tasks = delegator.findList("ContactPersonDetails", condition,
					null, null, null, false);
			List requirements = FastList.newInstance();
			for (Iterator iter = tasks.iterator(); iter.hasNext();) {
				Map union = FastMap.newInstance();
				GenericValue requirement = (GenericValue) iter.next();
				contactMechId = requirement.getString("contactMechId");
			}

			
			List notiemail = delegator.findByAnd("EmplPunch", UtilMisc.toMap(
					"partyId", partyId, "punchdate", punchdate, "PunchType",
					"Normal"));

			if (notiemail.size() == 0) {

				String firstpunchtime = punchtime.toString();
				String subpunchtime[] = firstpunchtime.split(":");
				int hrpunchtime = Integer.parseInt(subpunchtime[0]);
				int minpunchtime = Integer.parseInt(subpunchtime[1]);
				int secpunchtime = Integer.parseInt(subpunchtime[2]);

				List var1 = new ArrayList(); // =delegator.findByAnd("Notification",null);
				Iterator t1 = var1.iterator();
				while (t1.hasNext()) {
					GenericValue gvar1 = (GenericValue) t1.next();
					String pruleid = gvar1.getString("inputParamEnumId");
					EntityExpr condition_punchin2 = EntityCondition
							.makeCondition("inputParamEnumId",
									EntityOperator.EQUALS, "LATE_PUNCH");
					List var2 = delegator.findByAnd("Notification",
							UtilMisc.toMap("inputParamEnumId", "LATE_PUNCH"));
					Iterator t2 = var2.iterator();
					while (t2.hasNext()) {

						GenericValue gvar2 = (GenericValue) t2.next();
						String operatorEnumId = gvar2
								.getString("operatorEnumId");
						String condValue = gvar2.getString("condValue");
						int lenofcondValue = condValue.length();
						int minvalue = 59;
						int hrvalue = 0;
						if (lenofcondValue > 2) {
							String scondValue[] = condValue.split(":");
							hrvalue = Integer.parseInt(scondValue[0]);
							minvalue = Integer.parseInt(scondValue[1]);
						}

						if (lenofcondValue <= 2) {
							hrvalue = Integer.parseInt(condValue);
						}
						if (operatorEnumId.equals("IS_AFT")) {

							if ((hrpunchtime > hrvalue)
									|| ((hrpunchtime == hrvalue) && (minpunchtime > minvalue))) {

								// String user=userLogin.toString();
								String tm = punchtime.toString();
								String content = "Employee Punched In Very late......Punched in at "
										+ tm;
								Map commEventMap = FastMap.newInstance();
								commEventMap.put("communicationEventTypeId",
										"AUTO_EMAIL_COMM");
								commEventMap.put("statusId", "COM_IN_PROGRESS");
								commEventMap.put("contactMechTypeId",
										"EMAIL_ADDRESS");
								commEventMap.put("partyIdFrom", partyId);
								commEventMap.put("partyIdTo", "admin");
								commEventMap.put("contactMechIdFrom",
										contactMechId);
								commEventMap.put("contactMechIdTo", "admin");
								commEventMap.put("datetimeStarted",
										UtilDateTime.nowTimestamp());
								commEventMap.put("datetimeEnded",
										UtilDateTime.nowTimestamp());
								commEventMap.put("subject", "Late Punch");
								commEventMap.put("content", content);
								commEventMap.put("userLogin", userLogin);
								commEventMap.put("contentMimeTypeId",
										"text/html");
								Map<String, Object> createResp = null;
								try {
									createResp = dispatcher.runSync(
											"createCommunicationEvent",
											commEventMap);
								} catch (GenericServiceException e) {
									Debug.logError(e, module);
									return ServiceUtil.returnError(e
											.getMessage());
								}
								if (ServiceUtil.isError(createResp)) {
									return ServiceUtil.returnError(ServiceUtil
											.getErrorMessage(createResp));
								}

							}

						}

					}
					break;
				}

			} 

		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("Error Please contact adminstrator");
		}

		// This is to send mail if employee took more than 1hr for lunch
		try {
			List exprList = UtilMisc.toList(EntityCondition.makeCondition(
					"partyId", EntityOperator.EQUALS, partyId), EntityCondition
					.makeCondition("contactMechTypeId", EntityOperator.EQUALS,
							"EMAIL_ADDRESS"));
			EntityCondition condition = EntityCondition.makeCondition(exprList,
					EntityOperator.AND);
			List tasks = delegator.findList("ContactPersonDetails", condition,
					null, null, null, false);
			List requirements = FastList.newInstance();
			for (Iterator iter = tasks.iterator(); iter.hasNext();) {
				Map union = FastMap.newInstance();
				GenericValue requirement = (GenericValue) iter.next();
				contactMechId = requirement.getString("contactMechId");
			}
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("Error while finding contactmechId");
		}

		if (PunchType.equals("Lunch")) {
			if (inout.equals("OUT")) {

				try {

					List conditionList = UtilMisc.toList(EntityExpr
							.makeCondition("partyId", EntityOperator.EQUALS,
									partyId), EntityExpr.makeCondition(
							"PunchType", EntityOperator.EQUALS, "Lunch"),
							EntityExpr.makeCondition("punchdate",
									EntityOperator.EQUALS, punchdate),
							EntityExpr.makeCondition("InOut",
									EntityOperator.EQUALS, "IN"));
					List tasks = delegator.findByAnd("EmplPunch", UtilMisc
							.toMap("partyId", partyId, "PunchType", "Lunch",
									"punchdate", punchdate, "InOut", "IN"));

					if (tasks.size() > 0) {
						Iterator tr = tasks.iterator();
						while (tr.hasNext()) {
							GenericValue supr = (GenericValue) tr.next();
							punchintime = supr.getString("punchtime");

						}
					}
					if (tasks.size() > 0) {

						// String Intime = punchintime.toString();
						String dateArr[] = punchintime
								.split(Pattern.quote(":"));
						sec0 = Integer.parseInt(dateArr[2]);
						min0 = Integer.parseInt(dateArr[1]);
						hr0 = Integer.parseInt(dateArr[0]);

					}

				} catch (GenericEntityException e) {

					return ServiceUtil
							.returnError("Error in reading punchintime ");

				}

				
				punchouttime = punchout;
				String dateArr1[] = punchouttime.split(Pattern.quote(":"));
				sec0 = Integer.parseInt(dateArr1[2]);
				min1 = Integer.parseInt(dateArr1[1]);
				hr1 = Integer.parseInt(dateArr1[0]);


			}
		}
		// this is end for lunch hr notification
		try {
            if(UtilValidate.isNotEmpty(employeePunchId)){
            	emplPunch.set("emplPunchId", emplPunchId);
            	
            }else{
            	
            	emplPunch = delegator.makeValue("EmplPunch", UtilMisc
    					.toMap("partyId", partyId, "emplPunchId", emplPunchId,
    							"PunchType", PunchType, "punchdate", punchdate,
    							"InOut", inout ,"isManual" ,isManual)); // create a generic value from id
            	emplPunch.setNextSeqId();
            }
			
												// we just got

            emplPunch.setNonPKFields(context); // move non-primary key fields
												// from input parameters to
												// genericvalue
			emplPunch.set("shiftType", shiftTypeId);
			
			Timestamp punchDateTime =
				    Timestamp.valueOf(
				        new SimpleDateFormat("yyyy-MM-dd ")
				        .format(punchdate) // get the current date as String
				        .concat(punchtime.toString())        // and append the time
				    );
			
			emplPunch.set("punchDateTime", punchDateTime);
			
			// delegator.create(EmplPunch);
			/*if(UtilValidate.isNotEmpty(shiftTypeId)){
				emplPunch.set("shiftType", shiftTypeId);
				List condList = FastList.newInstance();
				condList.add(EntityCondition.makeCondition("shiftTypeId", EntityOperator.EQUALS, shiftTypeId));
				condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isDefault", EntityOperator.EQUALS, "Y"),EntityOperator.AND ,EntityCondition.makeCondition("isDefault", EntityOperator.NOT_EQUAL, null)));
				EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
				//punchTime 
				GenericValue shiftType =null;
				List<GenericValue> workShiftTypePeriodAndMap = delegator.findList("WorkShiftTypePeriodAndMap", cond, null, UtilMisc.toList("-startTime"),null, false);
				if(UtilValidate.isNotEmpty(workShiftTypePeriodAndMap)){
					shiftType = EntityUtil.getFirst(workShiftTypePeriodAndMap);
				}
				int shiftThreshold =0;
				GenericValue tenantShiftThreshold = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","HUMANRES", "propertyName","HR_SHIFT_THRESHOLD"), false);
	  	    	if (UtilValidate.isNotEmpty(tenantShiftThreshold)) {
	  	    		shiftThreshold = (new Double(tenantShiftThreshold.getString("propertyValue"))).intValue();
	  	    	}
			}*/
			//handle shift logic here
			/*if(UtilValidate.isNotEmpty(shiftTypeId)){
				GenericValue emplDetails = delegator.findOne("EmployeeDetail", UtilMisc.toMap("partyId",partyId), false);
				List<GenericValue> shiftEmplDetails = delegator.findByAnd("EmplDailyAttendanceDetail", UtilMisc.toMap("shiftType",shiftTypeId,"date",punchdate ,"partyId",partyId));
				if(UtilValidate.isEmpty(shiftEmplDetails)){
					Map employeeDailyAttendanceMap = UtilMisc.toMap("userLogin",userLogin);
					employeeDailyAttendanceMap.put("partyId", partyId);
					employeeDailyAttendanceMap.put("availedCanteen", emplDetails.getString("canteenFacin"));
					employeeDailyAttendanceMap.put("shiftType", shiftTypeId);
					employeeDailyAttendanceMap.put("date", punchdate);
					employeeDailyAttendanceMap.put("lateMin", BigDecimal.ZERO);
					employeeDailyAttendanceMap.put("extraMin", BigDecimal.ZERO);
					
					Map result = dispatcher.runSync("createorUpdateEmployeeDailyAttendance", employeeDailyAttendanceMap);
					if(ServiceUtil.isError(result)){
						Debug.logError(ServiceUtil.getErrorMessage(result), module);
						return result;
					}
					
				}
			}*/
			if(UtilValidate.isEmpty(consolidatedFlag)){
				emplPunch.set("createdDate", UtilDateTime.nowTimestamp());
				emplPunch.set("createdByUserLogin", userLogin.getString("userLoginId"));
				emplPunch.set("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
				emplPunch.set("lastModifiedDate", UtilDateTime.nowTimestamp());
			}
			Debug.logInfo("emplPunch-==========="+emplPunch,module);
			
			delegator.createOrStore(emplPunch);
			/*if(UtilValidate.isNotEmpty(oldPunchType) && (!PunchType.equals(oldPunchType))){				  
				   GenericValue oldRecord = delegator.findOne("EmplPunch",UtilMisc.toMap("emplPunchId", emplPunchId,
							"partyId", partyId, "PunchType", oldPunchType,
							"punchdate", punchdate, "InOut", oldInOut), true);
				   if(UtilValidate.isNotEmpty(oldRecord)){
					   delegator.removeValue(oldRecord);
				   }
				  				  
			}*/
			if(UtilValidate.isNotEmpty(oldShiftTypeId) && (!shiftTypeId.equals(oldShiftTypeId))){				  
				   List<GenericValue> shiftEmplPunch = delegator.findByAnd("EmplPunch", UtilMisc.toMap("shiftType",oldShiftTypeId,"punchdate",punchdate,"partyId",partyId));
				   if(UtilValidate.isEmpty(shiftEmplPunch)){
					   List<GenericValue> shiftEmplDetails = delegator.findByAnd("EmplDailyAttendanceDetail", UtilMisc.toMap("shiftType",oldShiftTypeId,"date",punchdate,"partyId",partyId));
				       delegator.removeAll(shiftEmplDetails);
				   }
				  				  
			}
			Map resultMap = ServiceUtil.returnSuccess("Data added successfully");
			resultMap.put("employeePunchId",emplPunch.get("employeePunchId"));
			return resultMap;

		} catch (Exception e) {
            Debug.logError("Unable to add punch, mismatch found! , Contact administrator"+e.toString(), punchouttime);
			return ServiceUtil
					.returnError("Unable to add punch, mismatch found! , Contact administrator :"+e.toString());

		}

	}

	
	public static Map emplDailyPunchReport(DispatchContext dctx, Map context) {

		Map emplDailyPunchMap = FastMap.newInstance();
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();

		String partyId = (String) context.get("partyId");
		java.sql.Date seleDate = (java.sql.Date) context.get("punchDate");
		java.sql.Date selectedDate;
		String encashFlag = (String) context.get("encashFlag");
		try{
			selectedDate = seleDate;
		} catch (Exception e) {
			   Debug.logError(e, e.getMessage());
			   return ServiceUtil.returnError("Invalid Date Field.. Please provide valid input" + e);
		  }
		
		ArrayList employeeList = new ArrayList();
		emplDailyPunchMap.put("orgId", partyId);
		populateChildren(dctx, emplDailyPunchMap, employeeList);
		
		if(UtilValidate.isEmpty(employeeList)){
			emplDailyPunchMap.clear();
			emplDailyPunchMap.put("employeeId", partyId);
			if(UtilValidate.isNotEmpty(encashFlag)){
				emplDailyPunchMap.put("encashFlag", encashFlag);
			}
			populateChildren(dctx, emplDailyPunchMap, employeeList);
		}
		List empPartyIds = EntityUtil
				.getFieldListFromEntityList(employeeList, "partyIdTo", true);
		
		try {
			List conditionList1 = UtilMisc.toList(EntityCondition
					.makeCondition("punchdate", EntityOperator.EQUALS,
							selectedDate));
			conditionList1.add(EntityCondition.makeCondition("partyId",
					EntityOperator.IN, empPartyIds));
			conditionList1.add(EntityCondition.makeCondition("InOut", "IN"));
			EntityCondition condition1 = EntityCondition.makeCondition(
					conditionList1, EntityOperator.AND);
			List<GenericValue> finalPunchIN = delegator.findList("EmplPunch",
					condition1, null, null, null, false);
			
			GenericValue finalPunchINValue = EntityUtil.getFirst(finalPunchIN);
			GenericValue finalPunchINValueLatest =  (GenericValue) finalPunchIN.get(finalPunchIN.size()-1);
			
			if(UtilValidate.isNotEmpty(finalPunchINValueLatest) && UtilValidate.isNotEmpty(finalPunchINValueLatest.getString("shiftType")) && (finalPunchINValueLatest.getString("shiftType").equals("SHIFT_NIGHT"))){
				finalPunchINValue = finalPunchINValueLatest;
			}else{
				finalPunchINValue = finalPunchINValue;
			}
			if(UtilValidate.isNotEmpty(finalPunchINValue) && UtilValidate.isNotEmpty(finalPunchINValue.getString("shiftType")) && (finalPunchINValue.getString("shiftType").equals("SHIFT_NIGHT"))){
				selectedDate = UtilDateTime.toSqlDate(UtilDateTime.addDaysToTimestamp(UtilDateTime.toTimestamp(selectedDate), 1));
			}
			List conditionList2 = FastList.newInstance();
			if(UtilValidate.isNotEmpty(finalPunchINValueLatest) && UtilValidate.isNotEmpty(finalPunchINValueLatest.getString("shiftType")) && (finalPunchINValueLatest.getString("shiftType").equals("SHIFT_NIGHT"))){
				conditionList2 = UtilMisc.toList(EntityCondition
						.makeCondition("punchdate", EntityOperator.EQUALS,
										selectedDate));
			}else{
				conditionList2 = UtilMisc.toList(EntityCondition
						.makeCondition("punchdate", EntityOperator.IN,
								UtilMisc.toList(selectedDate,seleDate)));
			}
			conditionList2.add(EntityCondition.makeCondition("shiftType",
                    EntityOperator.EQUALS, finalPunchINValueLatest.getString("shiftType")));
			conditionList2.add(EntityCondition.makeCondition("partyId",
					EntityOperator.IN, empPartyIds));
			conditionList2.add(EntityCondition.makeCondition("InOut", "OUT"));
			EntityCondition condition2 = EntityCondition.makeCondition(
					conditionList2, EntityOperator.AND);
			List<GenericValue> finalPunchOUT = delegator.findList("EmplPunch",
					condition2, null, null, null, false);
			ArrayList punchDataList = new ArrayList();
			int i = 0;
			for (GenericValue emplPunchIn : finalPunchIN) {
				i = i + 1;
				int j = 0;
				Map emplPunchMap = FastMap.newInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
				String stDate = sdf.format(emplPunchIn.get("punchdate"));
				emplPunchMap.put("date", stDate.toString());
				emplPunchMap.put("partyId", emplPunchIn.get("partyId"));
				String EmployeeId = ((emplPunchIn.get("partyId")).toString());
				String EmployeeName = PartyHelper.getPartyName(delegator,
						EmployeeId, false);
				GenericValue employeeDetail = delegator.findOne("EmployeeDetail", UtilMisc.toMap("partyId",partyId), true);
				emplPunchMap.put("Employee", EmployeeName);
				emplPunchMap.put("inTime",(emplPunchIn.getTime("punchtime").getHours())+":"+(emplPunchIn.getTime("punchtime").getMinutes()));
				//emplPunchMap.put("inTime", emplPunchIn.getString("punchtime"));
				String inPunchTime = (emplPunchIn.get("punchtime")).toString();
			     if(UtilValidate.isNotEmpty(employeeDetail.getString("companyBus")) && employeeDetail.getString("companyBus").equalsIgnoreCase("Y") && UtilValidate.isNotEmpty(emplPunchIn.getString("shiftType"))){
			    	 List condList = FastList.newInstance();
						condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isDefault", EntityOperator.EQUALS, "Y"),EntityOperator.AND ,EntityCondition.makeCondition("isDefault", EntityOperator.NOT_EQUAL, null)));
						condList.add(EntityCondition.makeCondition("shiftTypeId", EntityOperator.EQUALS, emplPunchIn.getString("shiftType")));
						EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
						//punchTime 
						List<GenericValue> workShiftTypePeriodAndMap = delegator.findList("WorkShiftTypePeriodAndMap", cond, null, UtilMisc.toList("-startTime"),null, false); 
						if(UtilValidate.isNotEmpty(workShiftTypePeriodAndMap)){
							inPunchTime = (EntityUtil.getFirst(workShiftTypePeriodAndMap)).getString("startTime");
						}
						 
			     }
			     Map emplPunchOutTimeMap = FastMap.newInstance();
			     Map emplPunchOutDateMap = FastMap.newInstance();
			     for (GenericValue emplPunchOut : finalPunchOUT) {
					if (emplPunchIn.get("partyId").equals(
							emplPunchOut.get("partyId"))) {
						/*Date inTime = format.parse((emplPunchIn
								.get("punchtime")).toString());
						Date outTime = format.parse(emplPunchOut.get(
								"punchtime").toString());*/
						//long timeDiff = outTime.getTime() - inTime.getTime();
						j= j + 1;
						emplPunchOutTimeMap.put(j,emplPunchOut.get("punchtime"));
						emplPunchOutDateMap.put(j,emplPunchOut.get("punchdate"));
					}
				}
			    if(UtilValidate.isNotEmpty(emplPunchOutDateMap)){
					if(UtilValidate.isNotEmpty(emplPunchOutTimeMap)){
						SimpleDateFormat format = new SimpleDateFormat(
								"HH:mm:ss");
						Timestamp punchInTimestamp =
							    Timestamp.valueOf(
							        new SimpleDateFormat("yyyy-MM-dd ")
							        .format(emplPunchIn.getDate("punchdate")) // get the current date as String
							        .concat(inPunchTime)        // and append the time
							    );
						Date punchOutDate = (Date)emplPunchOutDateMap.get(i);
						Time punchOutTimings = (Time)emplPunchOutTimeMap.get(i);
						Timestamp punchOutTimestamp =
							    Timestamp.valueOf(
							        new SimpleDateFormat("yyyy-MM-dd ")
							        .format(punchOutDate) // get the current date as String
							        .concat((punchOutTimings).toString())        // and append the time
							    );
						
						long timeDiff = punchOutTimestamp.getTime() - punchInTimestamp.getTime();
						Long diffHours = new Long(timeDiff / (60 * 60 * 1000));
						long modOfDiffHours = timeDiff % (60 * 60 * 1000);
						Long diffMinutes = new Long(modOfDiffHours
								/ (60 * 1000));
						String totalTime = diffHours.toString() + ":"
								+ diffMinutes.toString() + " Hrs";
						/*emplPunchMap.put("outTime",
								emplPunchOut.getString("punchtime"));*/
						
						emplPunchMap.put("outTime",((punchOutTimings.getHours())+":"+(punchOutTimings.getMinutes())));
						emplPunchMap.put("totalTime", totalTime.toString());
					}
				}
				punchDataList.add(emplPunchMap);
			}
			result.put("punchDataList", punchDataList);
		} catch (GenericEntityException e) {
			Debug.logError(e, e.getMessage());
			return ServiceUtil
					.returnError("Error while finding Punch Time" + e);
		} catch (Exception e) {
			Debug.logError(e, e.getMessage());
			return ServiceUtil
					.returnError("Error while finding Punch Time" + e);
		}
		return result;
	}


	public static ArrayList populateChildren(DispatchContext dctx, Map context,
			ArrayList employeeList) {

		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map punchMap = FastMap.newInstance();
		String orgId = (String) context.get("orgId");
		String employeeId = (String) context.get("employeeId");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		if(UtilValidate.isEmpty(thruDate)){
			thruDate = UtilDateTime.nowTimestamp();
		}
		String encashFlag = (String) context.get("encashFlag");
		try {
			List<String> orderBy = UtilMisc.toList("groupName");
			if(UtilValidate.isNotEmpty(orgId)){
				List<GenericValue> internalOrgs = delegator.findByAnd(
						"PartyRelationshipAndDetail", UtilMisc.toMap("partyIdFrom",
								orgId, "partyRelationshipTypeId", "GROUP_ROLLUP"),
						orderBy);
				for (GenericValue internalOrg : internalOrgs) {
					punchMap.put("orgId", internalOrg.get("partyId"));
					populateChildren(dctx, punchMap, employeeList);
				}
			}
			

			List<String> order = UtilMisc.toList("firstName");
			List condList = FastList.newInstance();
			if(UtilValidate.isNotEmpty(orgId)){
				condList.add(EntityCondition.makeCondition("partyIdFrom",
					EntityOperator.EQUALS, orgId));
			}
			condList.add(EntityCondition.makeCondition("roleTypeIdTo",
					EntityOperator.EQUALS, "EMPLOYEE"));
			if(UtilValidate.isNotEmpty(employeeId)){
				condList.add(EntityCondition.makeCondition("partyIdTo",
						EntityOperator.EQUALS, employeeId));
			}
			EntityCondition condition = EntityCondition.makeCondition(
					condList, EntityOperator.AND);
			List<GenericValue> employments = delegator.findList("EmploymentAndPerson", condition,
					null, order, null, false);
			if(UtilValidate.isNotEmpty(encashFlag)){
				employments = employments;
			}else{
				employments = EntityUtil.filterByDate(employments, thruDate);
			}
			for (GenericValue employee : employments) {
				employeeList.add(employee);
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, e.getMessage());
		}
		return employeeList;
	}

	public static Map emplMonthlyPunchReport(DispatchContext dctx, Map context) {

		Map emplDailyPunchMap = FastMap.newInstance();
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();

		Map columnTitleMap = FastMap.newInstance();
		TimeZone timeZone = TimeZone.getDefault();
		Locale locale = Locale.getDefault();

		String partyId = (String) context.get("partyId");
		String employeeId = (String) context.get("employeeId");
		String customTimePeriodId = (String) context.get("customTimePeriodId");
		java.sql.Timestamp fromDate = null;
		java.sql.Timestamp thruDate = null;
		
		
		if(UtilValidate.isNotEmpty(customTimePeriodId)){
			GenericValue customTimePeriod;
		try {
			customTimePeriod = delegator.findOne("CustomTimePeriod",
					UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
		} catch (GenericEntityException e1) {
			Debug.logError(e1, e1.getMessage());
			return ServiceUtil
			.returnError("Error in customTimePeriod" + e1);
		}
		if(customTimePeriod == null) {
			GenericEntityException e1 = null;
			Debug.logError(e1, e1.getMessage());
			return ServiceUtil
			.returnError("NULL in customTimePeriod" + e1);
		}
		
			fromDate = new java.sql.Timestamp(
					((Date) customTimePeriod.get("fromDate")).getTime());
			thruDate = new java.sql.Timestamp(
					((Date) customTimePeriod.get("thruDate")).getTime());
		}else{
			fromDate = (Timestamp)context.get("fromDate");
			thruDate = (Timestamp)context.get("thruDate");
		}
		
		if(UtilValidate.isEmpty(fromDate) || UtilValidate.isEmpty(thruDate)){
			thruDate = UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(), 1));
			fromDate =  UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(thruDate, -30));
		}
		int totalDays = UtilDateTime.getIntervalInDays(
				UtilDateTime.toTimestamp(fromDate),
				UtilDateTime.toTimestamp(thruDate)) + 1;
		
		ArrayList<GenericValue> employeeList = new ArrayList();
		emplDailyPunchMap.put("orgId", partyId);
		emplDailyPunchMap.put("employeeId", employeeId);
		emplDailyPunchMap.put("fromDate", fromDate);
		emplDailyPunchMap.put("thruDate", thruDate);
		populateChildren(dctx, emplDailyPunchMap, employeeList);
		
		/*------------------------------------------------ColumnTitleMap(Titles)---------------------------------------------*/
		Timestamp startingDate = fromDate;
		for (int k = 1; k <= totalDays; k++) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
			String stDate = sdf.format(startingDate);
			int dateHeading = UtilDateTime.getDayOfMonth(startingDate,
					timeZone, locale);
			String heading = "d" + new Integer(k).toString();
			columnTitleMap.put(heading + "Title", stDate.toString());
			startingDate = UtilDateTime.addDaysToTimestamp(startingDate, 1);
		}
		/*-------------------------------------------------------------------------------------------------------------------*/
		try {
			ArrayList punchDataList = new ArrayList();
			if(UtilValidate.isEmpty(employeeList)){
				result.put("columnTitleMap", columnTitleMap);
				result.put("punchDataList", punchDataList);
				return result;
			}
			
			for (int i = 0; i < employeeList.size(); i++) {
				String empId = employeeList.get(i).getString("partyIdTo");

				java.sql.Date startDate = new java.sql.Date(fromDate.getTime());
				java.sql.Date endDate = new java.sql.Date(thruDate.getTime());

				List<GenericValue> finalPunchIN = new ArrayList();
				List conditionList1 = UtilMisc
						.toList(EntityCondition.makeCondition("partyId",
								EntityOperator.EQUALS, empId));
				conditionList1.add(EntityCondition.makeCondition("punchdate",
						EntityOperator.GREATER_THAN_EQUAL_TO, startDate));
				conditionList1.add(EntityCondition.makeCondition("punchdate",
						EntityOperator.LESS_THAN_EQUAL_TO, endDate));
				conditionList1
						.add(EntityCondition.makeCondition("InOut", "IN"));
				EntityCondition condition1 = EntityCondition.makeCondition(
						conditionList1, EntityOperator.AND);
				finalPunchIN = delegator.findList("EmplPunch", condition1,
						null, null, null, false);
				

				List<GenericValue> finalPunchOUT = new ArrayList();
				List conditionList2 = UtilMisc
						.toList(EntityCondition.makeCondition("partyId",
								EntityOperator.EQUALS, empId));
				conditionList2.add(EntityCondition.makeCondition("punchdate",
						EntityOperator.GREATER_THAN_EQUAL_TO, startDate));
				conditionList2.add(EntityCondition.makeCondition("punchdate",
						EntityOperator.LESS_THAN_EQUAL_TO, endDate));
				conditionList2.add(EntityCondition
						.makeCondition("InOut", "OUT"));
				EntityCondition condition2 = EntityCondition.makeCondition(
						conditionList2, EntityOperator.AND);
				finalPunchOUT = delegator.findList("EmplPunch", condition2,
						null, null, null, false);
				

				Map emplPunchMap = FastMap.newInstance();
				Map tempMap = FastMap.newInstance();
				emplPunchMap.put("partyId", empId);
				String EmployeeName = PartyHelper.getPartyName(delegator,
						empId, false);
				emplPunchMap.put("Employee", EmployeeName);

				Timestamp startDateTimestamp = fromDate;
				for (int j = 1; j <= totalDays; j++) {
					int dateHeading = UtilDateTime.getDayOfMonth(
							startDateTimestamp, timeZone, locale);
					String heading = "temp"
							+ new Integer(dateHeading).toString();
					emplPunchMap.put(heading, "0");
					startDateTimestamp = UtilDateTime.addDaysToTimestamp(
							startDateTimestamp, 1);
				}
				
				for (GenericValue emplPunchIn : finalPunchIN) {
					for (GenericValue emplPunchOut : finalPunchOUT) {
						if (emplPunchIn.get("punchdate").equals(
								emplPunchOut.get("punchdate"))) {
							SimpleDateFormat format = new SimpleDateFormat(
									"HH:mm:ss");
							Date inTime = format.parse((emplPunchIn
									.get("punchtime")).toString());
							Date outTime = format.parse(emplPunchOut.get(
									"punchtime").toString());
							long timeDiff = outTime.getTime()
									- inTime.getTime();
							Long diffHours = new Long(timeDiff
									/ (60 * 60 * 1000));
							long modOfDiffHours = timeDiff % (60 * 60 * 1000);
							Long diffMinutes = new Long(modOfDiffHours
									/ (60 * 1000));
							String totalTime = diffHours.toString() + ":"
									+ diffMinutes.toString() + " Hrs";
							java.sql.Timestamp eachPunchDay = new java.sql.Timestamp(
									emplPunchIn.getDate("punchdate").getTime());
							int dateHeading = UtilDateTime.getDayOfMonth(
									eachPunchDay, timeZone, locale);
							int monthNo = UtilDateTime.getMonth(
									eachPunchDay, timeZone, locale);
							String heading = "temp"
									+ new Integer(dateHeading).toString()+"-"+new Integer(monthNo).toString();
							emplPunchMap.put(heading, totalTime.toString());
							break;
						}
					}
				}
				Timestamp tempStartDate = fromDate;
				for (int a = 1; a <= totalDays; a++) {
					int dateHeading = UtilDateTime.getDayOfMonth(tempStartDate,
							timeZone, locale);
					int monthNo = UtilDateTime.getMonth(
							tempStartDate, timeZone, locale);
					emplPunchMap.put("d" + new Integer(a).toString(),
							emplPunchMap.get("temp"
									+ new Integer(dateHeading).toString()+"-"+new Integer(monthNo).toString()));
					tempStartDate = UtilDateTime.addDaysToTimestamp(tempStartDate, 1);
				}
				punchDataList.add(emplPunchMap);
			}
			result.put("columnTitleMap", columnTitleMap);
			result.put("punchDataList", punchDataList);
		} catch (GenericEntityException e) {
			Debug.logError(e, e.getMessage());
			return ServiceUtil
					.returnError("Error while finding Punch Date" + e);
		} catch (Exception e) {
			Debug.logError(e, e.getMessage());
			return ServiceUtil
					.returnError("Error while finding Punch Time" + e);
		}
		return result;
	}
	
	public static Map emplMonthlyLeaveReport(DispatchContext dctx, Map context) {

		Map emplDailyPunchMap = FastMap.newInstance();
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();

		//Map columnTitleMap = FastMap.newInstance();
		TimeZone timeZone = TimeZone.getDefault();
		Locale locale = Locale.getDefault();

		String partyId = (String) context.get("partyId");
		String employeeId = (String) context.get("employeeId");
		String customTimePeriodId = (String) context.get("customTimePeriodId");
		java.sql.Timestamp fromDate = null;
		java.sql.Timestamp thruDate = null;
		
		
		if(UtilValidate.isNotEmpty(customTimePeriodId)){
			GenericValue customTimePeriod;
		try {
			customTimePeriod = delegator.findOne("CustomTimePeriod",
					UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
		} catch (GenericEntityException e1) {
			Debug.logError(e1, e1.getMessage());
			return ServiceUtil
			.returnError("Error in customTimePeriod" + e1);
		}
		if(customTimePeriod == null) {
			GenericEntityException e1 = null;
			Debug.logError(e1, e1.getMessage());
			return ServiceUtil
			.returnError("NULL in customTimePeriod" + e1);
		}
		
			fromDate = new java.sql.Timestamp(
					((Date) customTimePeriod.get("fromDate")).getTime());
			thruDate = new java.sql.Timestamp(
					((Date) customTimePeriod.get("thruDate")).getTime());
		}else{
			fromDate = (Timestamp)context.get("fromDate");
			thruDate = (Timestamp)context.get("thruDate");
		}
		
		if(UtilValidate.isEmpty(fromDate) || UtilValidate.isEmpty(thruDate)){
			thruDate = UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(), 1));
			fromDate =  UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(thruDate, -360));
		}
		int totalDays = UtilDateTime.getIntervalInDays(
				UtilDateTime.toTimestamp(fromDate),
				UtilDateTime.toTimestamp(thruDate)) + 1;
		
		ArrayList<GenericValue> employeeList = new ArrayList();
		emplDailyPunchMap.put("orgId", partyId);
		emplDailyPunchMap.put("employeeId", employeeId);
		emplDailyPunchMap.put("fromDate", fromDate);
		emplDailyPunchMap.put("thruDate", thruDate);
		populateChildren(dctx, emplDailyPunchMap, employeeList);
		
		try {
			ArrayList leaveDataList = new ArrayList();
			if(UtilValidate.isEmpty(employeeList)){
				result.put("leaveDataList", leaveDataList);
				return result;
			}
			for (int i = 0; i < employeeList.size(); i++) {
				String empId = employeeList.get(i).getString("partyIdTo");
				List<GenericValue> employeeLeaves = new ArrayList();
				Map emplLeaveMap = FastMap.newInstance();
				Map leaveDetailMap = FastMap.newInstance();
				List conditionList1 = UtilMisc
						.toList(EntityCondition.makeCondition("partyId",
								EntityOperator.EQUALS, empId));
				conditionList1.add(EntityCondition.makeCondition("fromDate",
						EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
				conditionList1.add(EntityCondition.makeCondition("thruDate",
						EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
				
				EntityCondition condition1 = EntityCondition.makeCondition(
						conditionList1, EntityOperator.AND);
				employeeLeaves = delegator.findList("EmplLeave", condition1,
						null, UtilMisc.toList("fromDate"), null, false);
				
                 for(GenericValue emplLeave :employeeLeaves){
                	 Timestamp monthStart =  UtilDateTime.getMonthStart(emplLeave.getTimestamp("fromDate"));
                	 BigDecimal noLeaves = emplLeave.getBigDecimal("effectedCreditDays");
                	 if(UtilValidate.isNotEmpty(leaveDetailMap.get(monthStart))){
                		 noLeaves = noLeaves.add((BigDecimal)leaveDetailMap.get(monthStart));
                	 }
                	 leaveDetailMap.put(monthStart, noLeaves);
                 }
			
				emplLeaveMap.put("partyId", empId);
				String EmployeeName = PartyHelper.getPartyName(delegator,
						empId, false);
				emplLeaveMap.put("Employee", EmployeeName);
				emplLeaveMap.put("emplLeaveDetail", leaveDetailMap);
				leaveDataList.add(emplLeaveMap);
			}
			//result.put("columnTitleMap", columnTitleMap);
			result.put("leaveDataList", leaveDataList);
			
		} catch (GenericEntityException e) {
			Debug.logError(e, e.getMessage());
			return ServiceUtil
					.returnError("Error while finding Punch Date" + e);
		} catch (Exception e) {
			Debug.logError(e, e.getMessage());
			return ServiceUtil
					.returnError("Error while finding Punch Time" + e);
		}
		return result;
	}

}
