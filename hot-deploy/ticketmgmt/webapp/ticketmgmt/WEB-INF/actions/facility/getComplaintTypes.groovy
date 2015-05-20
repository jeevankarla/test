import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;

complaintsTypeMap =[:];
complaintsTypeMap = dispatcher.runSync("getTicketMgmtComplaintTypes",[userLogin:userLogin]);
complaintTypes =[];
enumType =[];

if(ServiceUtil.isSuccess(complaintsTypeMap)){
	complaintTypes = complaintsTypeMap.complaintsList;
}else{
	context.errorMessage = "No ComplaintType found";
	return;
} 
context.put("complaintTypeList",complaintTypes);


condList =[];
condList.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.IN,['DELL','BROCADE','APW']));
condList.add(EntityCondition.makeCondition("productTypeId", EntityOperator.EQUALS,"ASSET_MAP"));
EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
assertMappingProds = delegator.findList("Product", cond, null,null, null, false);

EnumerationDetails= delegator.findList("Enumeration",null, null, null, null, false );
ProductCategories= delegator.findList("Enumeration",EntityCondition.makeCondition("enumTypeId", EntityOperator.IN ,['HARDWARE','SOFTWARE'])  , null, null, null, false );	
//assertMappingProds= delegator.findList("Product",EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.IN ,['DELL','BROCADE','APW'])  , null, null, null, false );
environmentDetails = EntityUtil.filterByAnd(EnumerationDetails, [enumTypeId : "CUST_ENVT_TYPE"]);
projectDetails = EntityUtil.filterByAnd(EnumerationDetails, [enumTypeId : "CUST_PROJECT_TYPE"]);
ProductIds= delegator.findList("Product",EntityCondition.makeCondition("productTypeId", EntityOperator.EQUALS ,"FINISHED_GOOD")  , null, null, null, false );

categories = delegator.findList("ProductCategory",EntityCondition.makeCondition("productCategoryTypeId", EntityOperator.EQUALS ,"PRODUCTS_BRAND")  , null, null, null, false );

StatusItemTypes= delegator.findList("StatusItem",null, null, null, null, false );

statusTypeIds = EntityUtil.filterByAnd(StatusItemTypes, [statusTypeId : "CUST_COMP_STATUS"]);
severityTypes = EntityUtil.filterByAnd(StatusItemTypes, [statusTypeId : "CUST_SEVERITY"]);
context.put("ProductIds",ProductIds);
context.put("categories",categories);
context.put("assertMappingProds",assertMappingProds);
context.put("ProductCategories",ProductCategories);
context.put("statusTypeIds",statusTypeIds);
context.put("severityTypes",severityTypes);
context.put("environmentDetails",environmentDetails);
context.put("projectDetails",projectDetails);

JSONArray partyJSON = new JSONArray();
JSONObject partyNameObj = new JSONObject();
dctx = dispatcher.getDispatchContext();
inputMap = [:];
inputMap.put("userLogin", userLogin);
inputMap.put("roleTypeId", "CUSTOMER");
Map partyDetailsMap = ByProductNetworkServices.getPartyByRoleType(dctx, inputMap);
if(UtilValidate.isNotEmpty(partyDetailsMap)){
	partyDetailsList = partyDetailsMap.get("partyDetails");
	if(UtilValidate.isNotEmpty(partyDetailsList)){
	partyDetailsList.each{eachParty ->
		JSONObject newPartyObj = new JSONObject();
		partyName=PartyHelper.getPartyName(delegator, eachParty.partyId, false);
		newPartyObj.put("value",eachParty.partyId);
		newPartyObj.put("label",partyName+" ["+eachParty.partyId+"]");
		partyNameObj.put(eachParty.partyId,partyName);
		partyJSON.add(newPartyObj);
	  }
	}
}
context.partyNameObj = partyNameObj;
context.partyJSON = partyJSON;
