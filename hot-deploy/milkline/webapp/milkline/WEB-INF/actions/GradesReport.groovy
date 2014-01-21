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

import net.sf.antcontrib.logic.IfTask.ElseIf;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.io.ObjectOutputStream.DebugTraceInfoStack;
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
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.Debug;
import org.ofbiz.network.NetworkServices;
import org.ofbiz.party.party.PartyHelper;

import java.math.RoundingMode;
import java.util.Map;

import javax.wsdl.Import;

import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;

import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;

shedId = parameters.shedId;
customTimePeriodId = parameters.customTimePeriodId;
dctx = dispatcher.getDispatchContext();
if(UtilValidate.isEmpty(shedId)){
	context.errorMessage = "Shed is Not Selected";
	return ;
	}
dctx = dispatcher.getDispatchContext();
fromDateStr = parameters.fromDate;
thruDateStr = parameters.thruDate;
/*customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));*/
Timestamp fromDate ;
Timestamp thruDate ;
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (UtilValidate.isNotEmpty(fromDateStr)) {
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(fromDateStr).getTime()));
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(fromDateStr).getTime()));
	}else{
		context.errorMessage = "From Date Not Selected " ;
		return;
	}
	if (UtilValidate.isNotEmpty(thruDateStr)) {
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(thruDateStr).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}
context.putAt("fromDate", fromDate);
context.putAt("thruDate", thruDate);
grade = parameters.grade;
productsList = [];
productsList = ProcurementNetworkServices.getProcurementProducts(dctx,UtilMisc.toMap());
unitId = parameters.gUnitId;
context.putAt("unitId", unitId);
unitsList = [];
facilityName="";
supervisorName="";
supervisorDataMap = [:];
supervisorsList = delegator.findList("FacilityFacilityPartyAndPerson",EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"SUPERVISOR"),["facilityId","partyId"]as Set,null,null,false);
for(supervisor in supervisorsList){
	supervisorDataMap.put(supervisor.facilityId,supervisor.partyId);
	}

if(unitId.equalsIgnoreCase("allUnits")){
		facilityName = "ALL UNITS";
		supervisorName = "ALL UNITS";
		unitsList = context.unitsList;
	}else if(unitId.equalsIgnoreCase("supervisor")){
		partyId = parameters.supervisor;
		facilityName = "SUPERVISOR WISE";
		supervisorDetails = delegator.findOne("Person",[partyId:partyId],false);
		firstName="";
		lastName ="";
		supervisorName="";
		if(UtilValidate.isNotEmpty(supervisorDetails)){ 
			lastName =(supervisorDetails.lastName);
			if(UtilValidate.isNotEmpty(lastName)&&!lastName.contains('.')){
				lastName = lastName+'.';
				supervisorName =lastName;
			}
			
			if(UtilValidate.isNotEmpty(supervisorDetails.firstName)){
				 supervisorName =supervisorName+" "+supervisorDetails.firstName;
			 }
		}
		
	for(supervisor in supervisorsList){
		if(partyId.equals(supervisor.partyId)){
			unitMap=[:];
			unitMap.putAt("facilityId", supervisor.facilityId);
			unitsList.add(unitMap);
			}	
	}
}else{
		unitMap=[:];
		unitMap.putAt("facilityId", unitId);
		unitDetails = delegator.findOne("Facility",[facilityId:unitId],false);
		facilityName = unitDetails.facilityName;
		supervisorId = supervisorDataMap.get(unitId);
		if(UtilValidate.isNotEmpty(supervisorId)){
		supervisorDetails = delegator.findOne("Person",[partyId:supervisorId],false);
			lastName = (supervisorDetails.lastName);
		if(!lastName.contains('.')){
				lastName = lastName+'.';
			}
		
		if(UtilValidate.isNotEmpty(supervisorDetails.firstName)){
			supervisorName =supervisorName+" "+supervisorDetails.firstName;
		 }
	}else{
		supervisorName = "Company";
		}
		unitsList.add (unitMap);
	}
	tempMap = [:];
	totalsList = [];
	totalsMap = [:];
	bmTotalsMap =[:];
	cmTotalsMap =[:];
	tempMap.put("qtyLtrs", BigDecimal.ZERO);
	tempMap.put("qtyKgs", BigDecimal.ZERO);
	tempMap.put("kgFat", BigDecimal.ZERO);
	tempMap.put("kgSnf", BigDecimal.ZERO);
	tempMap.put("fat", BigDecimal.ZERO);
	tempMap.put("snf", BigDecimal.ZERO);
	
	totalsMap.putAll(tempMap);
	bmTotalsMap.putAll(tempMap);
	cmTotalsMap.putAll(tempMap);
	
	countGrades=0;
	context.putAt("facilityName", facilityName);
	context.putAt("supervisorName", supervisorName);
	gradesList = FastList.newInstance();
	for(unitDetails in unitsList){
		facilityId = unitDetails.facilityId;
		unitPeriodTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: fromDate , thruDate: thruDate,includeCenterTotals:true , facilityId: facilityId,userLogin:userLogin]);
		if(UtilValidate.isNotEmpty(unitPeriodTotals.get("centerWiseTotals"))){
			centerWiseTotals = [:];
			centerWiseTotals = unitPeriodTotals.get("centerWiseTotals");
			keys = [];
			centerKeys = centerWiseTotals.keySet();
			for (centerKey in centerKeys){
				gradesMap = FastMap.newInstance();
				gradesMap.putAt("centerCode", centerKey);
				completeCenterDetails = ProcurementNetworkServices.getCenterDtails(dctx,[centerId:centerKey]);
				centerDetails = [:];
				UnitDetails = [:];
				if(UtilValidate.isNotEmpty(completeCenterDetails.centerFacility)){
					centerDetails = completeCenterDetails.centerFacility;
					}
				if(UtilValidate.isNotEmpty(completeCenterDetails.unitFacility)){
					unitDetails = completeCenterDetails.unitFacility;
					}
				gradesMap.putAt("unit", unitDetails.facilityId);
				if(UtilValidate.isNotEmpty(centerDetails)){	
					supervisorId = supervisorDataMap.get(centerDetails.parentFacilityId);
					supervisorDetails = delegator.findOne("Person",[partyId:supervisorId],false);
					supervisor ="";
					firstName = "";
					lastName = "";
					if(UtilValidate.isNotEmpty(supervisorDetails)){
						lastName = (supervisorDetails.lastName);
						if(UtilValidate.isNotEmpty(lastName)&&!lastName.contains('.')){
							lastName = lastName+'.';
							supervisor =lastName;
							}
						if(UtilValidate.isNotEmpty(supervisorDetails.firstName)){
							 supervisor =supervisor+" "+supervisorDetails.firstName;
						}
					}
					
					
					
					gradesMap.put("centerName", centerDetails.facilityName);
					//ownerPartyName = PartyHelper.getPartyName(delegator, centerDetails.get("ownerPartyId"), false);
					gradesMap.put("superviser", supervisor);
					}else{
						gradesMap.put("centerName", "");
						gradesMap.put("superviser", "");
						}	
					dayTotals = centerWiseTotals.get(centerKey).dayTotals;
					if(UtilValidate.isNotEmpty(dayTotals)){
						centerTotals = dayTotals.TOT;
						if(UtilValidate.isNotEmpty(centerTotals)){
							totMap = [:];
							totMap = centerTotals.TOT;
							if(UtilValidate.isNotEmpty(totMap)){
								for(product in productsList){
									prodctBrandName = product.brandName;
									qtyMap = [:];
									qtyMap = totMap.get(product.productName);
									if(UtilValidate.isNotEmpty(qtyMap)){	
										snf = qtyMap.get("snf");
										gradesMap.put("fat", qtyMap.fat);
										gradesMap.put("snf", snf);
										gradesMap.put("qtyLtrs",qtyMap.qtyLtrs );
										tempGrade = "";
										if(UtilValidate.isNotEmpty(snf)){
											if("BM".equalsIgnoreCase(prodctBrandName)){
												gradesMap.put("milkType", prodctBrandName);
												if(snf>=8.80){
													tempGrade = "A";
												 }else if(snf>=8.5 && snf<8.8){
												 	tempGrade = "B";
												 }else if(snf>=8.2 && snf<8.5){
												 	tempGrade = "C";
												 }else if(snf>=8.0 && snf<8.2){
													 tempGrade = "D";
												 }else if(snf>=7.7 && snf<8.0){
													 tempGrade = "E";
												 }else if(snf<7.7){
													 tempGrade = "F";
												 }
												 gradesMap.put("grade", tempGrade);
												 if(grade.equalsIgnoreCase("all")|| grade.equalsIgnoreCase(tempGrade)){
													 bmTotalsMap.put("qtyLtrs",bmTotalsMap.get("qtyLtrs")+qtyMap.get("qtyLtrs"));
													 bmTotalsMap.put("qtyKgs",bmTotalsMap.get("qtyKgs")+qtyMap.get("qtyKgs"));
													 bmTotalsMap.put("kgFat",bmTotalsMap.get("kgFat")+qtyMap.get("kgFat"));
													 bmTotalsMap.put("kgSnf",bmTotalsMap.get("kgSnf")+qtyMap.get("kgSnf"));
												 }
											}
											if("CM".equalsIgnoreCase(prodctBrandName)){
												gradesMap.put("milkType", prodctBrandName);
												if(snf>=8.40){
													tempGrade ="A";
												}else if(snf>=8.1 && snf<8.4){
													tempGrade ="B";
												}else if(snf>=7.9 && snf<8.1){
													tempGrade ="C";
												}else if(snf>=7.6 && snf<7.9){
													tempGrade ="D";
												}else if(snf>=7.4 && snf<7.6){
													tempGrade ="E";
												}else if(snf<7.4){
													tempGrade ="F";
												}
												gradesMap.put("grade", tempGrade);
												if(grade.equalsIgnoreCase("all")|| grade.equalsIgnoreCase(tempGrade)){
													cmTotalsMap.put("qtyLtrs",cmTotalsMap.get("qtyLtrs")+qtyMap.get("qtyLtrs"));
													cmTotalsMap.put("qtyKgs",cmTotalsMap.get("qtyKgs")+qtyMap.get("qtyKgs"));
													cmTotalsMap.put("kgFat",cmTotalsMap.get("kgFat")+qtyMap.get("kgFat"));
													cmTotalsMap.put("kgSnf",cmTotalsMap.get("kgSnf")+qtyMap.get("kgSnf"));
												}
											}
									}
								}
								tempMap = [:];
								tempMap.putAll(gradesMap);
								if((tempMap.qtyLtrs!=0)&&(grade.equalsIgnoreCase("all")|| grade.equalsIgnoreCase(tempMap.grade))){
									gradesList.add(tempMap);
								}
							}
						}
					}
				}
			}
					
		}
	}

	for(key in totalsMap.keySet()){
		totalsMap.put(key,totalsMap.get(key)+bmTotalsMap.get(key)+cmTotalsMap.get(key));
	}
if(bmTotalsMap.qtyKgs!=0){
	bmTotalsMap.put("fat", ProcurementNetworkServices.calculateFatOrSnf(bmTotalsMap.kgFat, bmTotalsMap.qtyKgs));
	bmTotalsMap.put("snf", ProcurementNetworkServices.calculateFatOrSnf(bmTotalsMap.kgSnf, bmTotalsMap.qtyKgs));
	}
if(cmTotalsMap.qtyKgs!=0){
	cmTotalsMap.put("fat", ProcurementNetworkServices.calculateFatOrSnf(cmTotalsMap.kgFat, cmTotalsMap.qtyKgs));
	cmTotalsMap.put("snf", ProcurementNetworkServices.calculateFatOrSnf(cmTotalsMap.kgSnf, cmTotalsMap.qtyKgs));
	}
if(totalsMap.qtyKgs!=0){
	totalsMap.put("fat", ProcurementNetworkServices.calculateFatOrSnf(totalsMap.kgFat, totalsMap.qtyKgs));
	totalsMap.put("snf", ProcurementNetworkServices.calculateFatOrSnf(totalsMap.kgSnf, totalsMap.qtyKgs));
	}

tempBmSnf=0;
tempCmSnf=0;
 tempBmSnf= bmTotalsMap.get("snf");
if(tempBmSnf>=8.80){
	bmTotalsMap.put("grade", "A");
   }else if(tempBmSnf>=8.5 && tempBmSnf<8.8){
	   bmTotalsMap.put("grade", "B");
   }else if(tempBmSnf>=8.2 && tempBmSnf<8.5){
	   bmTotalsMap.put("grade", "C");
   }else if(tempBmSnf>=8.0 && tempBmSnf<8.2){
	   bmTotalsMap.put("grade", "D");
   }else if(tempBmSnf>=7.7 && tempBmSnf<8.0){
	   bmTotalsMap.put("grade", "E");
   }else if(tempBmSnf<7.7){
	   bmTotalsMap.put("grade", "F");
   }
   
   
   tempCmSnf = cmTotalsMap.get("snf");
if(tempCmSnf>=8.40){
	   cmTotalsMap.put("grade", "A");
 }else if(tempCmSnf>=8.1 && tempCmSnf<8.4){
	   cmTotalsMap.put("grade", "B");
   }else if(tempCmSnf>=7.9 && tempCmSnf<8.1){
	   cmTotalsMap.put("grade", "C");
   }else if(tempCmSnf>=7.6 && tempCmSnf<7.9){
	   cmTotalsMap.put("grade", "D");
   }else if(tempCmSnf>=7.4 && tempCmSnf<7.6){
	   cmTotalsMap.put("grade", "E");
   }else if(tempCmSnf<7.4){
	   cmTotalsMap.put("grade", "F");
   }
totalsMap.put("grade","-");
bmTotalsMap.put("milkType","BM");
cmTotalsMap.put("milkType","CM");
totalsMap.put("milkType","TOT");
totalsList.add(bmTotalsMap);
totalsList.add(cmTotalsMap);
totalsList.add(totalsMap);

context.putAt("totalsList", totalsList)
context.putAt("gradesList", gradesList);
