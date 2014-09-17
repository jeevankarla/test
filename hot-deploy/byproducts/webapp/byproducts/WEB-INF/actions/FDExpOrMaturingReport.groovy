import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.LocalDispatcher;
import java.text.ParseException;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.base.util.UtilMisc;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import javax.swing.text.html.parser.Entity;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
dctx = dispatcher.getDispatchContext();
userLogin= context.userLogin;
fromDate=parameters.fromDate;
def sdf = new SimpleDateFormat("dd-MMM-yyyy");
fdate="01-"+fromDate+" 00:00:00";
try {
	fromDateTime = new java.sql.Timestamp(sdf.parse(fdate).getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: ", "");
}
fromDateTime = UtilDateTime.getMonthStart(fromDateTime);
thruDateTime = UtilDateTime.getMonthEnd(fromDateTime,TimeZone.getDefault(),Locale.getDefault());;
dayBegin = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(thruDateTime);
context.fromDate = fromDateTime;
context.thruDate = thruDateTime;
printDate = UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "dd/MM/yyyy");
context.printDate = printDate;
if(parameters.categoryType != "ALL"){
	boothDetails = (List)((Map)ByProductNetworkServices.getAllActiveOrInactiveBooths(delegator,parameters.categoryType,thruDateTime)).get("boothActiveList");
}else{
    boothDetails = (List)((Map)ByProductNetworkServices.getAllActiveOrInactiveBooths(delegator,null,thruDateTime)).get("boothActiveList");
}
List<String> facIds = EntityUtil.getFieldListFromEntityList(boothDetails, "facilityId", true);
conditionList=[];
conditionList.add(EntityCondition.makeCondition("thruDate",EntityOperator.GREATER_THAN_EQUAL_TO, fromDateTime));
conditionList.add(EntityCondition.makeCondition("thruDate",EntityOperator.LESS_THAN_EQUAL_TO, thruDateTime));
conditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN, facIds));
EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
List<GenericValue> facilityFDs = delegator.findList("FacilityFixedDeposit", condition, null, ["thruDate"], null, false);
facilityFDs = EntityUtil.orderBy(facilityFDs, UtilMisc.toList("thruDate"));
List<String> boothIds = EntityUtil.getFieldListFromEntityList(facilityFDs, "facilityId", true);
Debug.log("boothIds==="+boothIds);
Map facilityFDMap = FastMap.newInstance();
facilityFixedDepositMap = [:];
Map SCTMap=FastMap.newInstance();

boothIds.each{boothId->
	List<GenericValue> eachBoothFDRs = EntityUtil.filterByCondition(facilityFDs, EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, boothId));
	for (GenericValue eachBoothData : eachBoothFDRs) {
		if (facilityFDMap.containsKey(boothId)) {
			Map tempFDRDetail = FastMap.newInstance();
			tempFDRDetail = (Map) facilityFDMap.get(boothId);
			List FDREntries = (List) tempFDRDetail.get("FDRDetail");
			BigDecimal totalAmt = BigDecimal.ZERO;
			BigDecimal extTotAmt = (BigDecimal) tempFDRDetail.get("totalAmount");
			Map tempMap = FastMap.newInstance();
			tempMap.put("facilityId",eachBoothData.getString("facilityId"));
			tempMap.put("fdrNumber",eachBoothData.getString("fdrNumber"));
			tempMap.put("bankName",	eachBoothData.getString("bankName"));
			tempMap.put("branchName",eachBoothData.getString("branchName"));
			tempMap.put("amount",eachBoothData.getBigDecimal("amount"));
			tempMap.put("fromDate",	eachBoothData.getTimestamp("fromDate"));
			tempMap.put("thruDate",	eachBoothData.getTimestamp("thruDate"));
			FDREntries.add(tempMap);
			totalAmt = extTotAmt.add(eachBoothData.getBigDecimal("amount"));
			tempFDRDetail.put("FDRDetail", FDREntries);
			tempFDRDetail.put("totalAmount", totalAmt);
			facilityFDMap.put(boothId, tempFDRDetail);

		} else {
			Map FDRDetail = FastMap.newInstance();
			List tempList = FastList.newInstance();
			Map tempMap = FastMap.newInstance();
			tempMap.put("facilityId",eachBoothData.getString("facilityId"));
			tempMap.put("fdrNumber",eachBoothData.getString("fdrNumber"));
			tempMap.put("bankName",eachBoothData.getString("bankName"));
			tempMap.put("branchName",eachBoothData.getString("branchName"));
			tempMap.put("amount",eachBoothData.getBigDecimal("amount"));
			tempMap.put("fromDate",	eachBoothData.getTimestamp("fromDate"));
			tempMap.put("thruDate",	eachBoothData.getTimestamp("thruDate"));
			tempList.add(tempMap);
			tempList=UtilMisc.sortMaps(tempList, UtilMisc.toList("-thruDate"));
			FDRDetail.put("FDRDetail", tempList);
			FDRDetail.put("totalAmount",eachBoothData.getBigDecimal("amount"));
			facilityFDMap.put(boothId, FDRDetail);
		}
	}
	facilityList = delegator.findOne("Facility", [facilityId :  boothId], false);
	if(UtilValidate.isNotEmpty(facilityList)){
		//for(i=0;i<facilityList.size();i++){
			categoryType=facilityList.categoryTypeEnum;
			Map facilityFDRDetail=[:];
			facilityId=facilityList.ownerPartyId;
			if(!categoryType.equals("CR_INST")){
				if(UtilValidate.isEmpty(facilityFixedDepositMap[categoryType])){
					facDetailsMap=[:];
					Map finalMap=FastMap.newInstance();
					facDetailsMap.putAt("name",facilityList.facilityName);
					if(UtilValidate.isNotEmpty(facilityFDMap)){
						facilityFDRDetail = (Map)facilityFDMap.get(facilityId);
						if(UtilValidate.isNotEmpty(facilityFDRDetail)){
						fDRLst = facilityFDRDetail.get("FDRDetail");
						facDetailsMap.putAt("fDRLst",fDRLst);
						}
					}
					if(UtilValidate.isNotEmpty(facilityFDRDetail)){
						tempMap = [:];
						tempMap.putAll(facDetailsMap);
						finalMap.put(facilityId,tempMap);
						tempFinalMap = [:];
						tempFinalMap.putAll(finalMap);
						facilityFixedDepositMap.put(categoryType,tempFinalMap);
					}
					
				 }else{
					 facDetailsMap = [:];
					 Map finalMap=FastMap.newInstance();
					 finalMap=facilityFixedDepositMap.get(categoryType);
					 facDetailsMap.putAt("name",facilityList.facilityName);
					if(UtilValidate.isNotEmpty(facilityFDMap)){
						facilityFDRDetail = (Map)facilityFDMap.get(facilityId);
						if(UtilValidate.isNotEmpty(facilityFDRDetail)){
							fDRLst = facilityFDRDetail.get("FDRDetail");
							facDetailsMap.putAt("fDRLst",fDRLst);
						}
					}
					 
					tempMap = [:];
					tempMap.putAll(facDetailsMap);
					if(UtilValidate.isNotEmpty(facilityFDRDetail)){
						finalMap.put(facilityId,tempMap);
						tempFinalMap = [:];
						tempFinalMap.putAll(finalMap);
						facilityFixedDepositMap.put(categoryType,tempFinalMap);
					}
				 }
			 }
		//}
	}
}
Debug.log("facilityFDMap==="+facilityFDMap);



context.facilityFixedDepositMap = facilityFixedDepositMap;
