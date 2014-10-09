import org.ofbiz.base.util.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javolution.util.FastList
import javolution.util.FastMap
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import net.sf.json.JSONArray;

JSONArray procDataListJSON = new JSONArray();
JSONArray procFatDataListJSON = new JSONArray();
JSONArray procSnfDataListJSON = new JSONArray();

JSONArray lowProcDataListJSON = new JSONArray();
JSONArray lowProcFatDataListJSON = new JSONArray();
JSONArray lowProcSnfDataListJSON = new JSONArray();

JSONArray labelsJSON = new JSONArray();
JSONArray lowProclabelsJSON = new JSONArray();
context.procDataListJSON = procDataListJSON;
context.procFatDataListJSON = procFatDataListJSON;
context.procSnfDataListJSON = procSnfDataListJSON;
context.labelsJSON = labelsJSON;

context.lowProcDataListJSON = lowProcDataListJSON;
context.lowProcFatDataListJSON = lowProcFatDataListJSON;
context.lowProcSnfDataListJSON = lowProcSnfDataListJSON;
context.lowProclabelsJSON = lowProclabelsJSON;




dctx = dispatcher.getDispatchContext();
fromDate=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
thruDate=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());

def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (parameters.procurementDate) {
		procurementDate = new java.sql.Timestamp(sdf.parse(parameters.procurementDate).getTime());
		fromDate = UtilDateTime.getDayStart(procurementDate);
		thruDate = UtilDateTime.getDayEnd(procurementDate);
	}
	if (parameters.procurementThruDate) {
		procurementThruDate = new java.sql.Timestamp(sdf.parse(parameters.procurementThruDate).getTime());
		thruDate = UtilDateTime.getDayEnd(procurementThruDate);
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}

if(UtilValidate.isEmpty(parameters.facilityId) && UtilValidate.isNotEmpty(parameters.procurementDate)){
	parameters.facilityId = "MAIN_PLANT";
}

facilityId = parameters.facilityId;

if(facilityId){
	facility = delegator.findOne("Facility",[facilityId : facilityId], false);
	if (facility == null) {
		Debug.logInfo("Facility '" + facilityId + "' does not exist!","");
		context.errorMessage = "Facility '" + facilityId + "' does not exist!";
		return;
	}
	if (facility.facilityTypeId != "SHED" && facility.facilityTypeId != "UNIT" && facility.facilityTypeId != "PLANT" && facility.facilityTypeId != "PROC_ROUTE") {
		Debug.logInfo("Facility '" + facilityId + "' is not of type plant/shed/unit/route!","");
		context.errorMessage = "Facility '" + facilityId + "' is not of type plant/unit/route!";
		return;
	}
}
else {
	return;
}


int i = 1;
int j = 1;

List lowProcSortList = FastList.newInstance();
List procSortList = FastList.newInstance();
	childFacilities = (ProcurementNetworkServices.getFacilityAgents(dctx, UtilMisc.toMap("facilityId", facility.facilityId))).get("facilityIds");
	totalsMap = ProcurementReports.getAnnualPeriodTotals(dctx, [fromDate:fromDate, thruDate:thruDate, facilityId:facility.facilityId]);
childFacilities.each { childFacilityDetails ->	
		childFacility = delegator.findOne("Facility",UtilMisc.toMap("facilityId",childFacilityDetails),false);
		totalsMap = ProcurementReports.getAnnualPeriodTotals(dctx, [fromDate:fromDate, thruDate:thruDate, facilityId:childFacility.facilityId]);
	
//Debug.logInfo("totalsMap="+ totalsMap, "");
	facilityMap = totalsMap.get(childFacility.facilityId);
	if (facilityMap != null) {
		Map productMap = FastMap.newInstance();
		dateMap = facilityMap.get("TOT");
		productMap = dateMap.get("TOT");
		
		qtyKgs = productMap.get("qtyKgs");
		if(qtyKgs!=0){
			BigDecimal fat = BigDecimal.ZERO;
			BigDecimal snf = BigDecimal.ZERO;
			fat = productMap.get("fat");
			snf = productMap.get("snf");
			fat = fat.setScale(1,BigDecimal.ROUND_HALF_UP);
			snf = snf.setScale(2,BigDecimal.ROUND_HALF_UP);
			
			JSONArray dayList= new JSONArray();
			JSONArray dayFatList= new JSONArray();
			JSONArray daySnfList= new JSONArray();
			Map seqMap = FastMap.newInstance();
			if(fat>4.5){
				seqMap.put("sequence",i);
				seqMap.put("fat",fat);
				procSortList.add(seqMap);
				dayList.add(i);
				dayList.add(qtyKgs);
				procDataListJSON.add(dayList);
				dayFatList.add(i);
				dayFatList.add(fat);
				procFatDataListJSON.add(dayFatList);
				daySnfList.add(i);
				daySnfList.add(snf);
				procSnfDataListJSON.add(daySnfList);
				JSONArray labelsList= new JSONArray();
				labelsList.add(i);
				labelsList.add(childFacility.facilityName+"["+childFacility.facilityCode+"]");
				labelsJSON.add(labelsList);
				++i;
			}else{
				seqMap.put("sequence",j);
				seqMap.put("fat",fat);
				lowProcSortList.add(seqMap);
				dayList.add(j);
				dayList.add(qtyKgs);
				lowProcDataListJSON.add(dayList);
				dayFatList.add(j);
				dayFatList.add(fat);
				lowProcFatDataListJSON.add(dayFatList);
				daySnfList.add(j);
				daySnfList.add(snf);
				lowProcSnfDataListJSON.add(daySnfList);
				JSONArray labelsList= new JSONArray();
				labelsList.add(j);
				labelsList.add(childFacility.facilityName+"["+childFacility.facilityCode+"]");
				lowProclabelsJSON.add(labelsList);
				++j;
			}
		}
		
	}
}


JSONArray tempProcDataListJSON = new JSONArray();
JSONArray tempProcFatDataListJSON = new JSONArray();
JSONArray tempProcSnfDataListJSON = new JSONArray();
JSONArray tempLabelsJSON = new JSONArray();
if(procSortList.size()>50){
	procSortList = UtilMisc.sortMaps(procSortList,UtilMisc.toList("-fat"));
	for(int k=0;k<procSortList.size();k++){
		if(k>=50){
				break;
			}
		Map  fatSeqMap = FastMap.newInstance();
		fatSeqMap.putAll(procSortList.get(k));
		int seqNum = fatSeqMap.get("sequence");
		
		List procDataList = FastList.newInstance();
		procDataList.addAll(procDataListJSON.get(seqNum-1));
		procDataList.remove(0);
		procDataList.add(0,k);
		tempProcDataListJSON.add(procDataList);
		
		
		
		List procFatDataList = FastList.newInstance();
		procFatDataList.addAll(procFatDataListJSON.get(seqNum-1));
		procFatDataList.remove(0);
		procFatDataList.add(0,k);
		tempProcFatDataListJSON.add(procFatDataList);
		
		List procSnfDataList = FastList.newInstance();
		procSnfDataList.addAll(procSnfDataListJSON.get(seqNum-1));
		procSnfDataList.remove(0);
		procSnfDataList.add(0,k);
		tempProcSnfDataListJSON.add(procSnfDataList);
		
		List lablesList = FastList.newInstance();
		lablesList.addAll(labelsJSON.get(seqNum-1));
		lablesList.remove(0);
		lablesList.add(0,k);
		tempLabelsJSON.add(lablesList);
		
		}
	
	}else{
	tempProcDataListJSON.addAll(procDataListJSON);
	tempProcFatDataListJSON.addAll(procFatDataListJSON);
	tempProcSnfDataListJSON.addAll(procSnfDataListJSON);
	tempLabelsJSON.addAll(labelsJSON);
	}
JSONArray tempLowProcDataListJSON = new JSONArray();
JSONArray tempLowProcFatDataListJSON = new JSONArray();
JSONArray tempLowProcSnfDataListJSON = new JSONArray();
JSONArray tempLowLabelsJSON = new JSONArray();
if(lowProcSortList.size()>50){
	lowProcSortList = UtilMisc.sortMaps(lowProcSortList,UtilMisc.toList("-fat"));
	for(int k=0;k<lowProcSortList.size();k++){
		if(k>=50){
				break;
			}
		Map  fatSeqMap = FastMap.newInstance();
		fatSeqMap.putAll(lowProcSortList.get(k));
		int seqNum = fatSeqMap.get("sequence");
		List procDataList = FastList.newInstance();
		procDataList.addAll(lowProcDataListJSON.get(seqNum-1));
		procDataList.remove(0);
		procDataList.add(0,k);
		tempLowProcDataListJSON.add(procDataList);
		
		
		
		List procFatDataList = FastList.newInstance();
		procFatDataList.addAll(lowProcFatDataListJSON.get(seqNum-1));
		procFatDataList.remove(0);
		procFatDataList.add(0,k);
		tempLowProcFatDataListJSON.add(procFatDataList);
		
		List procSnfDataList = FastList.newInstance();
		procSnfDataList.addAll(lowProcSnfDataListJSON.get(seqNum-1));
		procSnfDataList.remove(0);
		procSnfDataList.add(0,k);
		tempLowProcSnfDataListJSON.add(procSnfDataList);
		
		List lablesList = FastList.newInstance();
		lablesList.addAll(lowProclabelsJSON.get(seqNum-1));
		lablesList.remove(0);
		lablesList.add(0,k);
		tempLowLabelsJSON.add(lablesList);
		
		}
	
	}else{
		tempLowProcDataListJSON.addAll(lowProcDataListJSON);
		tempLowProcFatDataListJSON.addAll(lowProcFatDataListJSON);
		tempLowProcSnfDataListJSON.addAll(lowProcSnfDataListJSON);
		tempLowLabelsJSON.addAll(lowProclabelsJSON);
	}
//Debug.logInfo("procDataListJSON="+ procDataListJSON, "");

context.facility = facility;
context.procDataListJSON = tempProcDataListJSON;
context.procFatDataListJSON = tempProcFatDataListJSON;
context.procSnfDataListJSON = tempProcSnfDataListJSON;
context.labelsJSON = tempLabelsJSON;

context.lowProcDataListJSON = tempLowProcDataListJSON;
context.lowProcFatDataListJSON = tempLowProcFatDataListJSON;
context.lowProcSnfDataListJSON = tempLowProcSnfDataListJSON;
context.lowProclabelsJSON = tempLowLabelsJSON;


