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
import org.ofbiz.accounting.invoice.InvoiceWorker;
import in.vasista.vbiz.byproducts.SalesInvoiceServices;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.order.order.*;
import java.math.RoundingMode;
import org.ofbiz.party.contact.ContactMechWorker;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;


condListGroup = [];
partyClassificationGroup = delegator.findList("PartyClassificationGroup", EntityCondition.makeCondition("partyClassificationTypeId", EntityOperator.EQUALS, "CUST_CLASSIFICATION"), null,null,null, false);
/*List partyClassificationGroupIds = EntityUtil.getFieldListFromEntityList(partyClassificationGroup, "partyClassificationGroupId", true);
condListGroup.add(EntityCondition.makeCondition("partyClassificationTypeId", EntityOperator.IN, partyClassificationGroupIds));
condGroup=EntityCondition.makeCondition(condListGroup,EntityOperator.AND);
partyClassList = delegator.findList("PartyClassificationType", condGroup, null, null, null, false);*/
context.partyClassList=partyClassificationGroup;


AllLoomDetails = delegator.findList("LoomType",null,null,null,null,false);
context.AllLoomDetails=AllLoomDetails;



productStoreIds=[];

productStoreDetails = delegator.findList("ProductStore", EntityCondition.makeCondition("productStoreId", EntityOperator.NOT_IN,UtilMisc.toList("1003","1012","9000","STORE") ), null,null,null, false);
if(parameters.productStoreId){
	productStoreIds.add(parameters.productStoreId);
}else{
	productStoreIds = EntityUtil.getFieldListFromEntityList(productStoreDetails, "productStoreId", true);
}
Debug.log("productStoreIds=============================="+productStoreIds);
context.productStoreDetails=productStoreDetails;


BankBranchList = delegator.findList("BankBranch", null, null, null, null, false);

context.BankBranchList=BankBranchList;


BankList = delegator.findList("Bank", null, null, null, null, false);


JSONArray BankListJSON = new JSONArray();

if(BankList){
	BankList.each{ eachBank ->
		JSONObject newObj = new JSONObject();
			newObj.put("value",eachBank.bankId);
			newObj.put("label",eachBank.description);
			BankListJSON.add(newObj);
	}
}



conditionList = [];
conditionList.add(EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS,"BANK_BRANCH"));
condGroup=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
GeoList = delegator.findList("Geo", condGroup, null, null, null, false);



JSONArray brachesGeoLIst = new JSONArray();

if(GeoList){
	GeoList.each{ eachGEO ->
		JSONObject newObj = new JSONObject();
			newObj.put("value",eachGEO.geoId);
			newObj.put("label",eachGEO.geoName);
			brachesGeoLIst.add(newObj);
	}
}


context.brachesGeoLIst=brachesGeoLIst;

conditionList.clear();
conditionList.add(EntityCondition.makeCondition("geoId", EntityOperator.LIKE,"IN%"));
conditionList.add(EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS,"STATE"));
cond1=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
indianStateList = delegator.findList("Geo", cond1, null, null, null, false);
stateIds = EntityUtil.getFieldListFromEntityList(indianStateList, "geoId", true);

conditionList.clear();
conditionList.add(EntityCondition.makeCondition("geoId", EntityOperator.IN,stateIds));
conditionList.add(EntityCondition.makeCondition("geoAssocTypeId", EntityOperator.EQUALS,"DISTRICT"));
cond2=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
indianStateDistList = delegator.findList("GeoAssoc", cond2, null, null, null, false);
disticIds = EntityUtil.getFieldListFromEntityList(indianStateDistList, "geoIdTo", true);
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("geoId", EntityOperator.IN,disticIds));
conditionList.add(EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS,"DISTRICT"));
cond3=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
indianStateWiseDistList = delegator.findList("Geo", cond3, null, null, null, false);


JSONObject stateDistJSONMAP= new JSONObject();
JSONObject stateNamesJSON= new JSONObject();
JSONObject DistNamesJSON= new JSONObject();
JSONArray stateDistJSONLIST= new JSONArray();

for(indianState in indianStateList)
{
	JSONArray stateDistJSON= new JSONArray();
	JSONObject obj1 = new JSONObject();
	obj1.put("value",indianState.geoId);
	obj1.put("label",indianState.geoName);
	stateDistJSONLIST.add(obj1);
	stateNamesJSON.put(indianState.geoId, indianState.geoName);
	stateDistLst = EntityUtil.filterByCondition(indianStateDistList, EntityCondition.makeCondition("geoId", EntityOperator.EQUALS, indianState.geoId));
	stateDisticIds = EntityUtil.getFieldListFromEntityList(stateDistLst, "geoIdTo", true);
	
	stateDistList = EntityUtil.filterByCondition(indianStateWiseDistList, EntityCondition.makeCondition("geoId", EntityOperator.IN, stateDisticIds));
	
	for(stateDist in stateDistList)
	{
		JSONObject obj2 = new JSONObject();
		obj2.put("value",stateDist.geoId);
		obj2.put("label",stateDist.geoName);
		stateDistJSON.add(obj2);
		DistNamesJSON.put(stateDist.geoId, stateDist.geoName);
	}
	stateDistJSONMAP.put(indianState.geoId , stateDistJSON);
}


context.stateDistJSONMAP=stateDistJSONMAP;
context.stateDistJSONLIST=stateDistJSONLIST;
context.DistNamesJSON=DistNamesJSON;
context.stateNamesJSON=stateNamesJSON;


context.BankListJSON=BankListJSON;










