import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilDateTime;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.ofbiz.party.party.PartyHelper;


custRequestId = parameters.custRequestId;
context.custRequestId = custRequestId;

if(UtilValidate.isNotEmpty(custRequestId)){
	custRequestDetails = delegator.findOne("CustRequest", UtilMisc.toMap("custRequestId", custRequestId), false);
	if(UtilValidate.isNotEmpty(custRequestDetails)){
		context.statusId = custRequestDetails.statusId;
		context.severity = custRequestDetails.severity;
		context.custRequestDate = UtilDateTime.toDateString(custRequestDetails.custRequestDate,"dd-MM-yyyy HH:mm:ss");
		context.custRequestTypeId = custRequestDetails.custRequestTypeId;
		
	}
	categoryIdAttr = delegator.findOne("CustRequestAttribute", [custRequestId : custRequestId , attrName : "CATEGORY_ID"], false);
	if(UtilValidate.isNotEmpty(categoryIdAttr)){
		categoryId = categoryIdAttr.attrValue;
		context.categoryId =categoryId;
		
	}
	productCategoryIdAttr = delegator.findOne("CustRequestAttribute", [custRequestId : custRequestId , attrName : "PRODUCT_CATEGORY_ID"], false);
	if(UtilValidate.isNotEmpty(productCategoryIdAttr)){
		productCategoryId = productCategoryIdAttr.attrValue;
		context.productCategoryId =productCategoryId;
		
	}
	
	group_ClientAttr = delegator.findOne("CustRequestAttribute", [custRequestId : custRequestId , attrName : "GROUP_CLIENT"], false);
	if(UtilValidate.isNotEmpty(group_ClientAttr)){
		groupClient = group_ClientAttr.attrValue;
		context.groupClient =groupClient;
		
	}
	environment_Attr = delegator.findOne("CustRequestAttribute", [custRequestId : custRequestId , attrName : "ENVIRONMENT"], false);
	if(UtilValidate.isNotEmpty(environment_Attr)){
		environment = environment_Attr.attrValue;
		context.environment =environment;
	}
	projectAttr = delegator.findOne("CustRequestAttribute", [custRequestId : custRequestId , attrName : "PROJECT"], false);
	if(UtilValidate.isNotEmpty(projectAttr)){
		project = projectAttr.attrValue;
		context.project =project;
	}
	Asset_MappingAttr = delegator.findOne("CustRequestAttribute", [custRequestId : custRequestId , attrName : "ASSET_MAPPING"], false);
	if(UtilValidate.isNotEmpty(Asset_MappingAttr)){
		assetMapping = Asset_MappingAttr.attrValue;
		context.assetMapping =assetMapping;
	}
	SLA_Attr = delegator.findOne("CustRequestAttribute", [custRequestId : custRequestId , attrName : "SLA"], false);
	if(UtilValidate.isNotEmpty(SLA_Attr)){
		SLA = SLA_Attr.attrValue;
		context.sla =SLA;
	}
	custRequestAndContents = [];
	conditionList =[];
	conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS,custRequestId));
	conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS,null));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	custRequestAndContents = delegator.findList("CustRequestAndContent", condition, null,null, null, false);
	context.putAt("custRequestAndContents", custRequestAndContents);
	
	custRequestStatus = [];
	conditionList1 =[];
	conditionList1.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS,custRequestId));
	condition1 = EntityCondition.makeCondition(conditionList1,EntityOperator.AND);
	custRequestParties = delegator.findList("CustRequestStatus", condition1, null,null, null, false);
	context.putAt("custRequestParties", custRequestParties);
	
}


