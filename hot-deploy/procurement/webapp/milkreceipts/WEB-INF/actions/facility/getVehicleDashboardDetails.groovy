import org.ofbiz.base.util.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.entity.util.EntityUtil;
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

dctx = dispatcher.getDispatchContext();
//resultReturn = ServiceUtil.returnSuccess();
vehicleId=parameters.vehicleId;
sequenceNum=parameters.sequenceNum;


JSONArray vehicleStatusJSONList = new JSONArray();
JSONObject productObj = new JSONObject();


conditionList=[];
conditionList.add(EntityCondition.makeCondition("vehicleId",EntityOperator.EQUALS,vehicleId));
conditionList.add(EntityCondition.makeCondition("sequenceNum",EntityOperator.EQUALS,sequenceNum));
EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
List<GenericValue> vehicleTripStatus = delegator.findList("MilkTransferAndItemVehicleTripStatus", condition, null, UtilMisc.toList("estimatedStartDate"), null, false);
if(UtilValidate.isNotEmpty(vehicleTripStatus)){
	vehicleProductDetails=EntityUtil.getFirst(vehicleTripStatus);
	if(UtilValidate.isNotEmpty(vehicleProductDetails.productId)){
		prodDetails = delegator.findOne("Product", [productId : vehicleProductDetails.productId], false);
		if(UtilValidate.isNotEmpty(prodDetails)){
		   productName = prodDetails.description;
		   productObj.put("productName",productName);
		   productObj.put("productId",vehicleProductDetails.productId);
		}
	}
	productObj.put("partyId",vehicleProductDetails.partyId);
    vehicleTripStatus.each{eachVehicleStatus->
	   JSONObject newVehicleObj = new JSONObject();
	   String statusId =eachVehicleStatus.vehicleTripStatusId;
	   Timestamp statusEntryDate =eachVehicleStatus.estimatedStartDate;
	   String statusDate=null;
	   if(UtilValidate.isNotEmpty(statusEntryDate)){
		   statusDate = UtilDateTime.toDateString(statusEntryDate,"dd-MM-yyyy HH:mm");
       }
	   statusItem = delegator.findOne("StatusItem",["statusId":statusId],false);
	   if(statusItem){
		   currentStatusId=statusItem.get("description");
	       newVehicleObj.put("statusId", currentStatusId);
	   }
	   newVehicleObj.put("statusEntryDate", statusDate);
		
	   productObj.put("dcNo",eachVehicleStatus.dcNo);
		receivedFat=0;receivedSnf=0;
		if(UtilValidate.isNotEmpty(eachVehicleStatus.receivedFat)){
			receivedFat=eachVehicleStatus.receivedFat;
			receivedSnf=eachVehicleStatus.receivedSnf;
			
			productObj.put("receivedFat", receivedFat);
			productObj.put("receivedSnf", receivedSnf);
			productObj.put("siloId", eachVehicleStatus.siloId);
		}
		if(UtilValidate.isNotEmpty(eachVehicleStatus.grossWeight)){
			grossWeight=eachVehicleStatus.grossWeight;
			dispatchWeight=eachVehicleStatus.dispatchWeight;
			productObj.put("dispatchWeight", dispatchWeight);
			productObj.put("grossWeight", grossWeight);
		}
		if(UtilValidate.isNotEmpty(eachVehicleStatus.tareWeight)){
			tareWeight=eachVehicleStatus.tareWeight;
			receivedQuantity=eachVehicleStatus.receivedQuantity;
			productObj.put("tareWeight", tareWeight);
			productObj.put("netWeight", receivedQuantity);
		}
	       
	   vehicleStatusJSONList.add(newVehicleObj);
	}
}
request.setAttribute("vehicleStatusJSONList", vehicleStatusJSONList);
request.setAttribute("productObj", productObj);

