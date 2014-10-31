import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.service.ServiceUtil;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;

detailsMap=[:];
detailsMap["quantity"]="";
detailsMap["quantityLtrs"]="";
detailsMap["fat"]="";
detailsMap["snf"]="";
detailsMap["acid"]="";
detailsMap["temp"]="";
detailsMap["clr"]="";
milkTankerCellsList = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS,"MILK_CELL_TYPE"), null, ['sequenceId'], null, true);

JSONArray dataJSONList= new JSONArray();
milkTankerCellsList.each{ cellDetails->
	JSONObject newObj = new JSONObject(detailsMap);
	newObj.put("id",cellDetails.enumId);
	dataJSONList.add(newObj);
}
context.dataJSON = dataJSONList.toString();

JSONObject mccCodeJson = new JSONObject();
JSONArray mccItemsJSON = new JSONArray();

List conditionList  =FastList.newInstance();
conditionList.add(EntityCondition.makeCondition("facilityTypeId",EntityOperator.EQUALS,"UNIT"));
conditionList.add(EntityCondition.makeCondition("mccCode",EntityOperator.NOT_EQUAL,null));
EntityCondition condition = EntityCondition.makeCondition(conditionList);

List<GenericValue> unitsList = delegator.findList("Facility",condition, null,["mccCode"],null,false);
List tempUnitsList = FastList.newInstance();
for(unit in unitsList){
	Map tempUnitMap = FastMap.newInstance();
	tempUnitMap.put("facilityId", unit.get("facilityId"));
	tempUnitMap.put("facilityName", unit.get("facilityName"));
	tempUnitMap.put("parentFacilityId", unit.get("parentFacilityId"));
	tempUnitMap.put("mccCode",Integer.parseInt(unit.get("mccCode")));
	tempUnitsList.add(tempUnitMap);
}
unitsList = UtilMisc.sortMaps(tempUnitsList, UtilMisc.toList("mccCode"));
unitWiseList=[];
for(unit in unitsList){		
	JSONObject unitJson = new JSONObject();
	unitJson.put("name", unit.get("facilityName"));
	unitJson.put("facilityId", unit.get("facilityId"));
	facilityAttribute = delegator.findOne("FacilityAttribute", [facilityId : unit.get("parentFacilityId"), attrName:"enableQuantityKgs"], false);
	unitJson.put("showQtyKgs", "N");
	if(UtilValidate.isNotEmpty(facilityAttribute)){
		qtyConfigValue=facilityAttribute.attrValue;
		unitJson.put("showQtyKgs", qtyConfigValue);
	}
	mccCodeJson.put( unit.get("mccCode"), unitJson);
	if(UtilValidate.isNotEmpty(unit.get("mccCode"))){
		UnitMap=[:];
		UnitMap.put("mccCode", unit.get("mccCode"));
		UnitMap.put("mccName", unit.get("facilityName"));
		unitWiseList.addAll(UnitMap);
		
		JSONObject mccObjectJson = new JSONObject();
		mccObjectJson.put("value",unit.get("mccCode"));
		mccObjectJson.put("label",unit.get("facilityName")+"-"+unit.get("parentFacilityId")+ " [" + unit.get("mccCode") + "]");
		mccItemsJSON.add(mccObjectJson);
	}
}

context.putAt("unitWiseList", unitWiseList);
context.put("mccCodeJson",mccCodeJson);
context.put("mccItemsJSON",mccItemsJSON);
List<GenericValue> productCatMembers = ProcurementNetworkServices.getMilkReceiptProducts(dispatcher.getDispatchContext(), context);
List productIds = EntityUtil.getFieldListFromEntityList(productCatMembers, "productId", false);


String tempProductId = null;
JSONObject productJson = new JSONObject();
JSONArray productItemsJSON = new JSONArray();
for(product in productCatMembers){
	JSONObject productNamesJson = new JSONObject();
	JSONObject productDetailsJson = new JSONObject();
	productDetailsJson.put("name",product.get("productName"));
	productDetailsJson.put("brandName",product.get("brandName"));
	
	productNamesJson.put("value",product.get("productId"));
	productNamesJson.put("label",product.get("productName")+"("+product.get("productId")+")"+ " [" + product.get("brandName") + "]");
	
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("validationTypeId", EntityOperator.EQUALS,"SNFFAT_CHECK"));
	conditionList.add(EntityCondition.makeCondition("shedId", EntityOperator.EQUALS,"_NA_"));
	conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS,product.get("productId")));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	List fatSnfList = delegator.findList("ProcBillingValidationRule",condition,null,null,null,true);
	
	BigDecimal minFat = new BigDecimal(2.5);;
	BigDecimal maxFat = new BigDecimal(12);
	BigDecimal minSnf = new BigDecimal(7.5);
	BigDecimal maxSnf = new BigDecimal(12);
	if(UtilValidate.isNotEmpty(fatSnfList)){
			Map fatSnfDetails = EntityUtil.getFirst(fatSnfList);
			minFat = fatSnfDetails.get("minFat");
			maxFat = fatSnfDetails.get("maxFat");
			minSnf = fatSnfDetails.get("minSnf");
			maxSnf = fatSnfDetails.get("maxSnf");
		}
	productDetailsJson.put("minFat",minFat);
	productDetailsJson.put("maxFat",maxFat);
	productDetailsJson.put("minSnf",minSnf);
	productDetailsJson.put("maxSnf",maxSnf);
	productItemsJSON.add(productNamesJson);
	productJson.put(product.get("productId"), productDetailsJson);
	}
context.putAt("productItemsJSON", productItemsJSON);
context.put("productJson",productJson);











