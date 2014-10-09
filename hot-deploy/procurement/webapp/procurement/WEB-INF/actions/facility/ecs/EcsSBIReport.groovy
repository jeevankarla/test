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
	import org.ofbiz.base.util.UtilValidate;
	import java.lang.*;
	import org.ofbiz.entity.*;
	import org.ofbiz.entity.condition.*;
	import java.sql.*;
	import java.util.Calendar;
	import javolution.util.FastList;
	import javolution.util.FastMap;
	import java.sql.Timestamp;
	import java.text.SimpleDateFormat;
	import org.ofbiz.base.util.UtilNumber;
	import java.math.RoundingMode;
	import java.util.Map;
	import javax.naming.Context;
	import org.ofbiz.entity.util.EntityFindOptions;
	import in.vasista.vbiz.procurement.ProcurementNetworkServices;
	import org.ofbiz.party.party.PartyHelper;

	String BR = System.getProperty("line.separator");
	StringBuffer ecsBuffer = new StringBuffer();
	//ecsBuffer.append("115008152APDAIRY DEVELOPMENT CO-OP FEDERATION LTDVENDORS MARGIN         500240002000212320005869   00001"+(ecsHeader.get("totalMarginForMonth")).toString().padLeft(21, '0')+(ecsHeader.get("day")).toString().padLeft(2, '0')+(ecsHeader.get("month")+1).toString().padLeft(2, '0')+(ecsHeader.get("year"))).append(BR);
	totalAmount = 0;
	for (bankKey in bankWiseMap.keySet()){
		unitData = bankWiseMap.get(bankKey);
		for(unitKey in unitData.keySet()){
			routeDataList = unitData.get(unitKey);
			for(routeData in routeDataList){
				for(routeKey in routeData.keySet()){
					centerData = routeData.get(routeKey);
					if((centerData.get("amount") != 0) && (centerData.get("accNo") != null)){
						totalAmount += centerData.get("amount");
						ecsBuffer.append("01");
						ecsBuffer.append((centerData.get("accNo")).toString().padLeft(17, '0'));
						ecsBuffer.append((centerData.get("amount")).toString().padLeft(14, '0'));
						ecsBuffer.append("00");
						ecsBuffer.append("  ");
						ecsBuffer.append(("BY MILK-BILL"));
						ecsBuffer.append("  ");
						ecsBuffer.append(ecsMonth);
						ecsBuffer.append("  ");
						ecsBuffer.append((centerData.get("unitName")).replace(',', '').padRight(20, ' ').substring(0, 20));
						ecsBuffer.append(" ");
						ecsBuffer.append((centerData.get("centerName")).replace(',', '').padRight(20, ' ').substring(0, 20));
						ecsBuffer.append(BR);
					}
				}
			}
		}
	}
	
	facilityDetails = delegator.findOne("Facility", UtilMisc.toMap("facilityId",parameters.shedId), false);
	if(UtilValidate.isNotEmpty(parameters.rTypeFlag) && "unitWise".equalsIgnoreCase(parameters.rTypeFlag)){
		ecsBuffer.delete(0, ecsBuffer.length());
		
		ecsList = FastList.newInstance();
		totalAmount =0;
		List unitBankWiseMapDetails = context.get("unitBankWiseAbstract");
		if(UtilValidate.isNotEmpty(unitBankWiseMapDetails)){
				
			String bankName = parameters.ecsBankName;
				for(unitBank in unitBankWiseMapDetails){
					 if(bankName.equalsIgnoreCase(unitBank.getAt("nameOfTheBank"))){
						 if(UtilValidate.isNotEmpty(unitBank.get("amount")) && unitBank.get("amount") !=0){
							 totalAmount += unitBank.get("amount");
							 
							 ecsBuffer.append("01");
							 ecsBuffer.append((unitBank.get("bankAccNo")).toString().padLeft(17, '0'));
							 ecsBuffer.append((unitBank.get("amount")).toString().padLeft(14, '0'));
							 ecsBuffer.append("00");
							 ecsBuffer.append("  ");
							 ecsBuffer.append(("BY MILK-BILL"));
							 ecsBuffer.append("  ");
							 ecsBuffer.append(ecsMonth);
							 ecsBuffer.append("  ");
							 ecsBuffer.append((facilityDetails.get("facilityName")).replace(',', '').padRight(20, ' ').substring(0, 20).toUpperCase());
							 ecsBuffer.append(" ");
							 ecsBuffer.append((unitBank.get("nameOfUnit")).replace(',', '').padRight(20, ' ').substring(0, 20).toUpperCase());
							 ecsBuffer.append(BR);
							 
							 }
						 }
					}
			}
		}
	
	
	ecsBuffer.append("54");
	ecsBuffer.append(("98571200829").toString().padLeft(17, '0'));
	ecsBuffer.append((totalAmount).toString().padLeft(14, '0'));
	ecsBuffer.append("00");
	ecsBuffer.append("  ");
	ecsBuffer.append(("BY MILK-BILL"));
	ecsBuffer.append("  ");
	ecsBuffer.append(ecsMonth);
	ecsBuffer.append("  ");
	ecsBuffer.append((facilityDetails.facilityName).replace(',', '').toUpperCase().replace(' MILK', '').padRight(20, ' ').substring(0, 20));
	ecsBuffer.append(" ");
	//ecsBuffer.append(("sds").replace(',', '').padRight(20, ' ').substring(0, 20));
	ecsBuffer.append(BR);
	context.put("ecsBuffer", ecsBuffer);
