import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilDateTime;
import java.util.*;
import java.lang.*;
import java.math.BigDecimal;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.GenericValue;
import java.sql.*;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementReports;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.service.ServiceUtil;

dctx = dispatcher.getDispatchContext();
shedList =[];
shedList = context.getAt("relatedShedList");
resultReturn = ServiceUtil.returnSuccess();
JSONObject shedFinAccountsJson = new JSONObject();
List finAccountNameList=FastList.newInstance();
List shrtNameList = FastList.newInstance();
Set<String> shrtNameSet = new HashSet();
List finAccountShedList = FastList.newInstance();
if(UtilValidate.isEmpty(shedList)){
	shedList =[];
	shedDetails =null;
	if(context.shedId){
		shedDetails = delegator.findOne("Facility",[facilityId : context.shedId], false);
	}
	if(parameters.shedId){
			shedDetails = delegator.findOne("Facility",[facilityId : parameters.shedId], false);
	}
	shedList.add(shedDetails);
}
if(UtilValidate.isNotEmpty(parameters.get("facilityId"))){
	Map shedInMap = FastMap.newInstance();
	shedInMap.put("userLogin", userLogin);
	shedInMap.put("facilityId", parameters.get("facilityId"));
	Map shedDetails = ProcurementNetworkServices.getShedDetailsForFacility(dctx,shedInMap);
	}

if(shedList.size() <2 && shedList.size()!=0){
	finAccountShedList.addAll(shedList);
	}
if(UtilValidate.isEmpty(finAccountShedList)){
	if(UtilValidate.isNotEmpty(parameters.get("facilityId"))){
		Map shedInMap = FastMap.newInstance();
		shedInMap.put("userLogin", userLogin);
		shedInMap.put("facilityId", parameters.get("facilityId"));
		Map shedDetails = ProcurementNetworkServices.getShedDetailsForFacility(dctx,shedInMap);
		if(UtilValidate.isNotEmpty(shedDetails.get("facility"))){	
			finAccountShedList.add(shedDetails.get("facility"));
		}	
			
		}
	
	}
Map ifscMap = FastMap.newInstance();
Map shortNamesMap = FastMap.newInstance();
for(shed in finAccountShedList){
	String shedId = shed.facilityId;
	context.put("shedId",shedId)
	String shedCode = shed.facilityCode;
	finAccountList =[];
	finAccountList = (ProcurementNetworkServices.getShedFacilityFinAccount(dctx, UtilMisc.toMap("facilityId",shedId))).get("finAccountList");
	shedBankNames = [];
	JSONArray  shedFinAccListJSON= new JSONArray();
	if(finAccountList){
		for(finAcc in finAccountList){
			finAccountMap =[:];
			shortName = finAcc.shortName;
			bankName = finAcc.finAccountName;
			gbCode = finAcc.gbCode;
			bCode = finAcc.bCode;
			bPlace = finAcc.bPlace;

			branchName = finAcc.finAccountBranch;
			ifscCode = finAcc.ifscCode;
			if(UtilValidate.isNotEmpty(bankName)){
				if(!shedBankNames.contains(bankName)){
					shedBankNames.add(bankName);
					shedFinAccListJSON.add(bankName);
					finAccountNameList.add(bankName);
				}
			}
			finAccountMap.put("finAccountName", bankName);
			finAccountMap.put("shortName", shortName);
			finAccountMap.put("gbCode", gbCode);
			Map branchWiseMap = FastMap.newInstance();
			if(UtilValidate.isNotEmpty(branchName)){
				if(UtilValidate.isNotEmpty(branchName.trim())){
					Map branchDetMap= FastMap.newInstance();
					branchDetMap.put("ifscCode",ifscCode);
					branchDetMap.put("bCode",bCode);
					branchDetMap.put("bPlace",bPlace);
					branchWiseMap.put(branchName,branchDetMap);
					
					}
			}
			if((UtilValidate.isNotEmpty(shortName)) && UtilValidate.isNotEmpty(bankName)){
				if(UtilValidate.isNotEmpty(bankName.trim())){
					shortNamesMap.put(bankName,finAccountMap);
				}
				if(UtilValidate.isNotEmpty(branchWiseMap)){
					if(UtilValidate.isNotEmpty(ifscMap) && UtilValidate.isNotEmpty(ifscMap.get(bankName))){
						branchWiseMap.putAll(ifscMap.get(bankName));
						}
					ifscMap.put(bankName,branchWiseMap);
					}
				shrtNameList.add(finAccountMap);
				shrtNameSet.add(shortName);
			}
		}
	}
	shedFinAccListJSON.add("All");
	shedFinAccountsJson.putAt(shedId, shedFinAccListJSON);
}
context.put("shedFinAccountsJson",shedFinAccountsJson);
context.putAt("finAccountNameList", finAccountNameList);
context.putAt("shrtNameList", shrtNameList);

resultReturn.put("shedFinAccountsJson",shedFinAccountsJson);
resultReturn.put("finAccountNameList", finAccountNameList);
resultReturn.put("ifscMap", ifscMap);
resultReturn.put("shrtNameList", shrtNameList);
resultReturn.put("shortNamesMap", shortNamesMap);
return resultReturn;