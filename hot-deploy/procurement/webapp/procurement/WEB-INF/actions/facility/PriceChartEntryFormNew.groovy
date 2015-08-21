
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import javolution.util.FastMap;
import javolution.util.FastList;
import java.sql.Timestamp;
import org.ofbiz.base.util.UtilDateTime;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.product.product.ProductWorker;
import in.vasista.vbiz.facility.util.FacilityUtil;

import in.vasista.vbiz.purchase.PurchaseStoreServices;
import in.vasista.vbiz.purchase.MaterialHelperServices;


conditionList = [];
conditionList.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS,"PROC_PRICE_TYPE"));
conditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.NOT_EQUAL,"PROC_PRC_FAT_EP_DED"));
conditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.NOT_EQUAL,"PROC_PRICE_FAT_DED"));
conditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.NOT_EQUAL,"PROC_PRICE_FAT_PRM"));
conditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.NOT_EQUAL,"PROC_PRICE_SNF_DED"));
conditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.NOT_EQUAL,"PROC_PRICE_SNF_PRM"));

condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
List enumerationlist = delegator.findList("Enumeration",condition,null,null,null,true);

JSONArray slabTermsJSON = new JSONArray();

JSONObject newObj = new JSONObject();
newObj.put("value","");
newObj.put("label","");
slabTermsJSON.add(newObj);



JSONObject productLabelIdJSON=new JSONObject();
enumerationlist.each{ eachTerm ->
	
	newObj.put("value",eachTerm.enumId);
	newObj.put("label",eachTerm.description);
	slabTermsJSON.add(newObj);
	
	productLabelIdJSON.put(eachTerm.description, eachTerm.enumId);
	
}
context.slabTermsJSON = slabTermsJSON;
context.productLabelIdJSON = productLabelIdJSON;

conditionList.clear();
conditionList.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS,"PROC_PRICE_TYPE"));
conditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.IN,UtilMisc.toList("PROC_PRC_FAT_EP_DED","PROC_PRICE_FAT_DED","PROC_PRICE_FAT_PRM")));
condition2 = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
List enumerationlist2 = delegator.findList("Enumeration",condition2,null,null,null,true);

JSONArray fatTermsJSON = new JSONArray();

JSONObject newObj3 = new JSONObject();
newObj3.put("value","");
newObj3.put("label","");
fatTermsJSON.add(newObj3);

JSONObject fatLabelIdJSON=new JSONObject();

enumerationlist2.each{ eachTerm ->
	newObj3.put("value",eachTerm.enumId);
	newObj3.put("label",eachTerm.description);
	fatTermsJSON.add(newObj3);
	
	fatLabelIdJSON.put(eachTerm.description, eachTerm.enumId);
	
}

context.fatTermsJSON = fatTermsJSON;
context.fatLabelIdJSON = fatLabelIdJSON;



conditionList.clear();
conditionList.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS,"PROC_PRICE_TYPE"));
conditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.IN,UtilMisc.toList("PROC_PRICE_SNF_DED","PROC_PRICE_SNF_PRM")));
condition3 = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
List enumerationlist3 = delegator.findList("Enumeration",condition3,null,null,null,true);


JSONArray snfTermsJSON = new JSONArray();
JSONObject snfLabelIdJSON=new JSONObject();
JSONObject newObj2 = new JSONObject();
newObj2.put("value","");
newObj2.put("label","");
snfTermsJSON.add(newObj2);

enumerationlist3.each{ eachTerm ->
	newObj2.put("value",eachTerm.enumId);
	newObj2.put("label",eachTerm.description);
	snfTermsJSON.add(newObj2);
	snfLabelIdJSON.put(eachTerm.description, eachTerm.enumId);
}

context.snfTermsJSON = snfTermsJSON;
context.snfLabelIdJSON = snfLabelIdJSON;




JSONArray existSnfJSON = new JSONArray();
JSONArray existFatJSON = new JSONArray();
JSONArray existSlabBasedJSON = new JSONArray();

regionId = "";
productId = "";
useBaseSnf = "";
useBaseFat = "";
useTotalSolids = "";
fromDate = "";

procPriceChartId = parameters.procPriceChartId;
if(procPriceChartId){
	
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS,procPriceChartId));
	condition4 = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	List procurementPricelist = delegator.findList("ProcurementPriceChartAndProcurementPrice",condition4,null,null,null,true);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.LIKE, "%" + "SNF" + "%"));
	condition5 = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	existingSnfDedList =  EntityUtil.filterByCondition(procurementPricelist, condition5);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.LIKE, "%" + "FAT" + "%"));
	condition6 = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	existingFatDedList =  EntityUtil.filterByCondition(procurementPricelist, condition6);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.LIKE, "%" + "SLAB" + "%"));
	conditionList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS, "PROC_PRICE_MIN_QLTY"));
	conditionList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS, "PROC_PRICE_MAX_QLTY"));
	conditionList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS, "PROC_PRICE_SOUR"));
	condition7 = EntityCondition.makeCondition(conditionList,EntityOperator.OR);
	existingSlabList =  EntityUtil.filterByCondition(procurementPricelist, condition7);
	
	JSONObject snfObj = new JSONObject();
	existingSnfDedList.each{ eachSnfDed ->
		snfObj.put("snfProcurementPriceTypeId",eachSnfDed.procurementPriceTypeId);
		snfObj.put("snfFat",eachSnfDed.fatPercent);
		snfObj.put("snfSnf",eachSnfDed.snfPercent);
		snfObj.put("snfPrice",eachSnfDed.price);
		existSnfJSON.add(snfObj);
	}
	
	JSONObject fatObj = new JSONObject();
	existingFatDedList.each{ eachFatDed ->
		fatObj.put("fatProcurementPriceTypeId",eachFatDed.procurementPriceTypeId);
		fatObj.put("fatFat",eachFatDed.fatPercent);
		fatObj.put("fatSnf",eachFatDed.snfPercent);
		fatObj.put("fatPrice",eachFatDed.price);
		existFatJSON.add(fatObj);
	}
	
	JSONObject slabObj = new JSONObject();
	existingSlabList.each{ eachSlabBased ->
		
		if((eachSlabBased.procurementPriceTypeId) == "PROC_PRICE_SLAB1"){
			
			regionId = eachSlabBased.regionId;
			productId = eachSlabBased.productId;
			String fromDateStr = (String)eachSlabBased.fromDate;
			useBaseSnf = eachSlabBased.useBaseSnf;
			useBaseFat = eachSlabBased.useBaseFat;
			useTotalSolids = eachSlabBased.useTotalSolids;
			
			DateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			Date date = inputFormatter.parse(fromDateStr);
			
			DateFormat outputFormatter = new SimpleDateFormat("dd MMMM, yyyy");
			fromDate = outputFormatter.format(date);
			
		}
		
		slabObj.put("procurementPriceTypeId",eachSlabBased.procurementPriceTypeId);
		slabObj.put("fatPercent",eachSlabBased.fatPercent);
		slabObj.put("snfPercent",eachSlabBased.snfPercent);
		slabObj.put("amount",eachSlabBased.price);
		existSlabBasedJSON.add(slabObj);
	}
	
	
}

context.regionId = regionId;
context.productId = productId;
context.fromDate = fromDate;
context.useBaseSnf = useBaseSnf;
context.useBaseFat = useBaseFat;
context.useTotalSolids = useTotalSolids;

context.existSnfJSON = existSnfJSON;
context.existFatJSON = existFatJSON;
context.existSlabBasedJSON = existSlabBasedJSON;













