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

import javolution.util.FastMap;
import java.util.regex.*;
import org.ofbiz.base.util.Debug;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.*;
import javolution.util.FastList;
import java.sql.Time;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * Services for Party-punches maintenance
 */

public class PunchService {

	public static final String module = PunchService.class.getName();

	public static Map emplPunch(DispatchContext dctx, Map context)

	{

		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();

		LocalDispatcher dispatcher = dctx.getDispatcher();
		int PunchId = 0;
		String partyId = (String) context.get("partyId");
		Debug.logInfo("Debug.loginfo*****" + partyId + "*****", module);
		String emplPunchId = (String) context.get("emplPunchId");
		Date punchdate = (Date) context.get("punchdate");
		Time punchtime = (Time) context.get("punchtime");
		String PunchType = (String) context.get("PunchType");
		String inout = (String) context.get("InOut");
		String note = (String) context.get("Note");
		String flag = "out";
		String date3 = punchdate.toString();
		String dateArr2[] = date3.split(Pattern.quote("-"));
		String contactMechId = null;
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		int sec = 0, sec0 = 0, sec1 = 0;
		int min = 0, min0 = 0, min1 = 0;
		int hr = 0, hr0 = 0, hr1 = 0;

		String punchout = punchtime.toString();
		String punchintime = null;
		String punchouttime = null;
		int dai = Integer.parseInt(dateArr2[2]);

		int res = dai % 7;
		if ((dai % 7) != 0) {
			PunchId = res;
			System.out.println("in if.........condition");

		} else {
			PunchId = 7;

		}		
		Debug.logInfo("emplPunchId----" + emplPunchId, module);
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
				if (tops1.size() > 0) {
					if ((tops1.size() % 2 == 1) && inout.equals("IN"))
						return ServiceUtil
								.returnError("Please PUNCH-OUT Before PUNCH-IN Again....");
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

				if (!PunchType.equals("Normal")) {

					return ServiceUtil.returnError("You can't punch for ["
							+ PunchType + "] without Normal Punch ");
				}
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

				if (!PunchType.equals("Break")) {

					return ServiceUtil
							.returnError(" Please PunchOut for Break First ");
				}
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

			if (check.size() == 0) {

				if (inout.equals("OUT")) {

					return ServiceUtil
							.returnError("You can't punch out without punchin");
				}
			}
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

			GenericValue EmplPunch = delegator.makeValue("EmplPunch", UtilMisc
					.toMap("partyId", partyId, "emplPunchId", emplPunchId,
							"PunchType", PunchType, "punchdate", punchdate,
							"InOut", inout)); // create a generic value from id
												// we just got

			EmplPunch.setNonPKFields(context); // move non-primary key fields
												// from input parameters to
												// genericvalue

			// delegator.create(EmplPunch);
			delegator.createOrStore(EmplPunch);
			return ServiceUtil.returnSuccess("Data added successfully");

		} catch (GenericEntityException e) {

			return ServiceUtil
					.returnError("Unable to add punch, mismatch found! , Contact administrator");

		}

	}

}// end of PunchService Class

