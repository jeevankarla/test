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
import org.ofbiz.party.party.PartyHelper;

List ecsList = FastList.newInstance();
totalAmt = 0;
if(UtilValidate.isNotEmpty(context.bankWiseMap)){
	for (bankKey in bankWiseMap.keySet()){
		unitData = bankWiseMap.get(bankKey);
		if(UtilValidate.isNotEmpty(unitData)){
			for(unitKey in unitData.keySet()){
				routeDataList = unitData.get(unitKey);
				for(routeData in routeDataList){
					if(UtilValidate.isNotEmpty(routeData)){
						for(routeKey in routeData.keySet()){
							ecsMap = [:];
							centerData = routeData.get(routeKey);
							if(centerData.get("amount") != 0&& (centerData.get("accNo") != null)){
								ecsMap["bankAccNum"] = centerData.get("accNo");
								ecsMap["centerName"] = centerData.get("centerName");
								String pName = "";
								pName = centerData.get("partyName");
								if(UtilValidate.isNotEmpty(pName) && UtilValidate.isNotEmpty(pName.trim())){
									ecsMap["centerName"] = pName;
									}
								ecsMap["netAmount"] = centerData.get("amount").toString();
								totalAmt += centerData.get("amount");
								ecsList.add(ecsMap);
							}
						}
					}
				}
			}
		}	
	}
}
//handling transfer bank details
if(UtilValidate.isNotEmpty(context.getAt("transferBankMap")) && "WGD".equals(parameters.shedId)){
	trnsferBankMap=context.getAt("transferBankMap");
	Iterator mapIter = trnsferBankMap.entrySet().iterator();
	while (mapIter.hasNext()) {
		Map.Entry entry = mapIter.next();
		branchMap = entry.getValue();
		if(UtilValidate.isNotEmpty(branchMap)){
			Iterator branchMapIter = branchMap.entrySet().iterator();
			while(branchMapIter.hasNext()){
				Map.Entry branchEntry = branchMapIter.next();
					branchWiseValues=branchEntry.getValue();
					if(UtilValidate.isNotEmpty(branchWiseValues.get("amount")) && branchWiseValues.get("amount") !=0){
						trnsfMap=[:];
						trnsfMap["bankAccNum"] = branchWiseValues.get("TrnsfAcNo");
						trnsfMap["centerName"] = "MD. A.P.D.D.C.F.Ltd";
						trnsfMap["netAmount"] = branchWiseValues.get("amount").toString();
						totalAmt += branchWiseValues.get("amount");
						ecsList.add(trnsfMap);
					}
			}
		
		}
	
	}
}
if(UtilValidate.isNotEmpty(parameters.rTypeFlag) && "unitWise".equalsIgnoreCase(parameters.rTypeFlag)){
	ecsList = FastList.newInstance();
	totalAmt =0;
	List unitBankWiseMapDetails = context.get("unitBankWiseAbstract");
	if(UtilValidate.isNotEmpty(unitBankWiseMapDetails)){
			
		String bankName = parameters.ecsBankName;
			for(unitBank in unitBankWiseMapDetails){
				 if(bankName.equalsIgnoreCase(unitBank.getAt("nameOfTheBank"))){
					 if(UtilValidate.isNotEmpty(unitBank.get("amount")) && unitBank.get("amount") !=0){
						 ecsMap = [:];
						 ecsMap["bankAccNum"] = unitBank.get("bankAccNo");
						 ecsMap["centerName"] = unitBank.get("accountHolder");
						 ecsMap["netAmount"] = unitBank.get("amount").toString();
						 totalAmt += unitBank.get("amount");
						 ecsList.add(ecsMap);
						 }		
				 	}
				}
		}
	}



if(UtilValidate.isNotEmpty(context.get("ddAmountMap")) ){
	String bankName = parameters.ecsBankName;
				String ddBankName = ddAmountMap.get("nameOfTheBank");
				if(ddBankName.equalsIgnoreCase(bankName)){
					ecsMap = [:];
					ecsMap["bankAccNum"] = ddAmountMap.get("bankAccNo");
					ecsMap["centerName"] = ddAmountMap.get("nameOfUnit");
					ecsMap["netAmount"] = ddAmountMap.get("amount").toString();
					int index = ((String)ecsMap.get("netAmount")).indexOf(".");
					if(index>=0){
						ecsMap["netAmount"] = ((String)ecsMap.get("netAmount")).substring(0,((String)ecsMap.get("netAmount")).indexOf("."));
					}
					totalAmt += ddAmountMap.get("amount");
					ecsList.add(ecsMap);
				}
				}
ecsTotMap = [:];
ecsTotMap["centerName"] = "TOTAL";
ecsTotMap["netAmount"] = totalAmt;
ecsTotMap["netAmount"] = ecsTotMap.get("netAmount").toString();


int index = ((String)ecsTotMap.get("netAmount")).indexOf(".");
if(index>=0){
	ecsTotMap["netAmount"] = ((String)ecsTotMap.get("netAmount")).substring(0,((String)ecsTotMap.get("netAmount")).indexOf("."));
}
ecsList.add(ecsTotMap);
context.put("ecsList", ecsList);