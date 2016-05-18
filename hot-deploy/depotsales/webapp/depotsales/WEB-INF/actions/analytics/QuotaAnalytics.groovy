import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.ofbiz.service.ServiceUtil;

resultCtx = dispatcher.runSync("getCustomerBranch",UtilMisc.toMap("userLogin",userLogin));
Map formatMap = [:];
List formatList = [];
List categroyList = [];
for (eachList in resultCtx.get("productStoreList")) {
	
	formatMap = [:];
	formatMap.put("storeName",eachList.get("storeName"));
	formatMap.put("payToPartyId",eachList.get("payToPartyId"));
	formatList.addAll(formatMap);
	
}
context.branchList = formatList;

catgMap = [:];
catgMap.put("categoryName","COTTON_40ABOVE");
catgMap.put("categoryId","COTTON_40ABOVE");
categroyList.addAll(catgMap);
catgMap = [:];
catgMap.put("categoryName","COTTON_UPTO40");
catgMap.put("categoryId","COTTON_UPTO40");
categroyList.addAll(catgMap);
catgMap = [:];
catgMap.put("categoryName","SILK_YARN");
catgMap.put("categoryId","SILK_YARN");
categroyList.addAll(catgMap);
catgMap = [:];
catgMap.put("categoryName","WOOLYARN_10STO39NM");
catgMap.put("categoryId","WOOLYARN_10STO39NM");
categroyList.addAll(catgMap);
catgMap = [:];
catgMap.put("categoryName","WOOLYARN_40SNMABOVE");
catgMap.put("categoryId","WOOLYARN_40SNMABOVE");
categroyList.addAll(catgMap);
catgMap = [:];
catgMap.put("categoryName","WOOLYARN_BELOW10NM");
catgMap.put("categoryId","WOOLYARN_BELOW10NM");
categroyList.addAll(catgMap);
context.categoryList = categroyList;


dctx = dispatcher.getDispatchContext();


JSONArray dataList = new JSONArray();
JSONArray quotaAnalyticsList = new JSONArray();
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
fromDate=null;
thruDate=null;
partyId=parameters.partyId;
categoryId=parameters.categoryId;
branchId=parameters.branchId;
try {
	   if (parameters.fromDate) {
			   fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
	   }
	   else{
		   fromDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
	   }
	   if (parameters.thruDate) {
			   thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.thruDate).getTime()));
	   }else {
			   thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
	   }
	   
	   if(UtilValidate.isNotEmpty(fromDate)){
		   context.defaultEffectiveDate=UtilDateTime.toDateString(fromDate,"MMMM dd, yyyy");
	   }
	   if(UtilValidate.isNotEmpty(thruDate)){
		   context.defaultEffectiveThruDate=UtilDateTime.toDateString(thruDate,"MMMM dd, yyyy");
	   }
	  monthStartQuotaDetails = dispatcher.runSync("getPartyQuotaAnalytics", [partyId :partyId ,effectiveDate:fromDate,categoryId:categoryId,branchId:branchId ,userLogin : userLogin]);
	  monthEndQuotaDetails = dispatcher.runSync("getPartyQuotaAnalytics", [partyId :partyId ,effectiveDate:thruDate,categoryId:categoryId,branchId:branchId , userLogin : userLogin]);
	  startQuotaMap=monthStartQuotaDetails.get("quotaMap");
	  endQuotaMap=monthEndQuotaDetails.get("quotaMap");

	  for(Map.Entry entry : startQuotaMap.entrySet()){
		  
		  partyId = entry.getKey();
		  entryValue = entry.getValue();
		  endValu=endQuotaMap.get(partyId);
		  schemeStart=entryValue.get("TEN_PERCENT_MGPS");
		  schemeEnd=endValu.get("TEN_PERCENT_MGPS");
		  Debug.log("___________________________(((((((%%%%"+schemeEnd);
		  for(Map.Entry entry1 : schemeStart.entrySet()){
			  JSONObject newObj = new JSONObject();
			  categoryId = entry1.getKey();
			  categoryStartValue = entry1.getValue();
			  categoryEndValue = schemeEnd.get(categoryId);
			  
			  usedQuota = dispatcher.runSync("getUsedQuotaFromOrders", [partyId :partyId ,fromDate:fromDate,thruDate:thruDate,productCategoryId:categoryId, userLogin : userLogin]);
			  newObj.put("partyId", partyId);
			  newObj.put("categoryId", categoryId);
			  newObj.put("looms", categoryStartValue.get("looms"));
			  newObj.put("categoryQuotaPerMonth", categoryStartValue.get("categoryQuota"));
			  newObj.put("openingQuotaBalance", categoryStartValue.get("availableQuota"));
			  Debug.log("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^%%%%%%%%"+categoryEndValue.get("availableQuota"));
			  newObj.put("closingQuotaBalance", categoryEndValue.get("availableQuota"));
			  newObj.put("usedQuota",usedQuota.get("usedQuota"));
			  
			  quotaAnalyticsList.add(newObj);
		  }
  
  }
	  
	  
	  
} catch (ParseException e) {
	   Debug.logError(e, "Cannot parse date string: " + e, "");
	   context.errorMessage = "Cannot parse date string: " + e;
	   return;
}
Debug.log("______@@@@@@@@@@@@@@@@@@______"+quotaAnalyticsList.size());
context.putAt("dataJSON",quotaAnalyticsList);
Map resultMap = FastMap.newInstance();
resultMap = ServiceUtil.returnSuccess(); 
resultMap.put("data",quotaAnalyticsList);

return resultMap;

