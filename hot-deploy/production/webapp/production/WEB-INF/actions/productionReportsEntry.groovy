import java.sql.*

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;




List<GenericValue> facility = delegator.findList("Facility",null, null,null,null,false);

JSONObject facilityProductObj=new JSONObject();
facilityDepartments=EntityUtil.filterByCondition(facility,EntityCondition.makeCondition("facilityTypeId",EntityOperator.EQUALS,"PLANT"));
if(UtilValidate.isNotEmpty(facilityDepartments)){
	 context.facilityDepartments=facilityDepartments;
	 ownerPartyIds = EntityUtil.getFieldListFromEntityList(facilityDepartments, "ownerPartyId", true);
	 if(UtilValidate.isNotEmpty(ownerPartyIds)){
		 ownerPartyIds.each{eachDeptOwner->
			 facilityDepts=EntityUtil.filterByCondition(facilityDepartments,EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,eachDeptOwner));
			 facilityIds = EntityUtil.getFieldListFromEntityList(facilityDepts, "facilityId", true);
			 List<GenericValue> facilityProducts = delegator.findList("ProductFacility",EntityCondition.makeCondition("facilityId", EntityOperator.IN, facilityIds), null,UtilMisc.toList("productId"),null,false);
			 productIds = EntityUtil.getFieldListFromEntityList(facilityProducts, "productId", true);
			 List<GenericValue> productList = delegator.findList("Product",EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null,UtilMisc.toList("internalName"),null,false);
			 JSONArray arrayJSON = new JSONArray();
			 productList.each{product->
				 JSONObject newObj=new JSONObject();
				 newObj.put("description", product.internalName);
				 newObj.put("productId",product.productId);
				 arrayJSON.add(newObj);
			 }
			 if(UtilValidate.isNotEmpty(arrayJSON)){
			 facilityProductObj.put(eachDeptOwner, arrayJSON);
			 }
		 }
	}
 }
context.facilityProductObj=facilityProductObj;


allShiftsList = delegator.findList("WorkShiftTypePeriodAndMap",EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS ,"MILK_SHIFT"),null,UtilMisc.toList("shiftTypeId"),null,false);
if(UtilValidate.isNotEmpty(allShiftsList)){
	context.allShiftsList=allShiftsList;
}
