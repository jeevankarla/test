import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.io.ObjectOutputStream.DebugTraceInfoStack;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilNumber;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.network.NetworkServices;
import java.math.RoundingMode;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;

dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
	if(UtilValidate.isNotEmpty(context.listProcBillingValidation)){
		tenantConfigConditionList = [];
		tenantConfigConditionList.add(EntityCondition.makeCondition("propertyTypeEnumId", EntityOperator.EQUALS ,"MILK_PROCUREMENT"));
		tenantConfigCondition = EntityCondition.makeCondition(tenantConfigConditionList,EntityOperator.AND);
		tenantConfigList = [];
		tenantConfigList = delegator.findList("TenantConfiguration",tenantConfigCondition,null,null,null,false);
		tenantConfigCondition = [:];
		for(tenantconfig in tenantConfigList){
			tenantConfigCondition.put(tenantconfig.propertyName,tenantconfig.propertyValue);
		}
		context.put("tenantConfigCondition",tenantConfigCondition);
		
		
			listProcurementBillingValidation =context.listProcBillingValidation;
			listProcValidations = listProcurementBillingValidation.getCompleteList();
			negativeAmtList = EntityUtil.filterByAnd(listProcValidations, [validationTypeId : "NEGATIVE_AMOUNT"]);
			qtySnfFatCheckList = EntityUtil.filterByAnd(listProcValidations, [validationTypeId : "QTYSNFFAT_CHECK"]);
			outLierList = EntityUtil.filterByAnd(listProcValidations, [validationTypeId : "QTY_OUTLIER"]);
			checkCodeList = EntityUtil.filterByAnd(listProcValidations, [validationTypeId : "CHECKCENTER_CODE"]);
			qtySnfFatFinalList =[];
			//This is for Snf and Fat check
		if(UtilValidate.isNotEmpty(qtySnfFatCheckList)){
			qtySnfFatCheckList.each{ qtySnfFatCheck ->	
				Map qtySnfFatCheckMap = FastMap.newInstance();
				tempMap =[:];
				tempMap.putAll(qtySnfFatCheck)
				qtySnfFatCheckMap.putAll(tempMap);
				
				orderId =qtySnfFatCheck.getAt("orderId");
				orderItemSeqId =qtySnfFatCheck.getAt("orderItemSeqId");
				
				conditionList =[];
				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId)));
				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS ,orderItemSeqId)));
				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS ,"PURCHASE_ORDER")));
				condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				qtySnfFinalList = delegator.findList("OrderHeaderItemProductAndFacility",condition,null,null,null,false);
				if(UtilValidate.isNotEmpty(qtySnfFinalList)){
					qtySnfList =EntityUtil.getFirst(qtySnfFinalList);
					tempMap1=[:];
					tempMap1.putAll(qtySnfList);
					qtySnfFatCheckMap.putAll(tempMap1);
					qtySnfFatFinalList.add(qtySnfFatCheckMap);
				}
				
			}
		}
		// this is for quantity out lier	
			outLierFinalList =[];
		if(UtilValidate.isNotEmpty(outLierList)){	
			outLierList.each{ outLiers ->
				Map outLierCheckMap = FastMap.newInstance();
				tempOutMap =[:];
				tempOutMap.putAll(outLiers)
				outLierCheckMap.putAll(tempOutMap);
				
				orderId =outLiers.getAt("orderId");
				orderItemSeqId =outLiers.getAt("orderItemSeqId");
				
				conditionList =[];
				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId)));
				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS ,orderItemSeqId)));
				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS ,"PURCHASE_ORDER")));
				condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				outLierQtyList = delegator.findList("OrderHeaderItemProductAndFacility",condition,null,null,null,false);
				if(UtilValidate.isNotEmpty(outLierQtyList)){
					qtyOutLier =EntityUtil.getFirst(outLierQtyList);			
					tempMap2=[:];
					tempMap2.putAll(qtyOutLier);
					outLierCheckMap.putAll(tempMap2);
					outLierFinalList.add(outLierCheckMap);
				}
				
			}
		}
		
		// this is for Check Code
		checkCodeFinalList =[];
		if(UtilValidate.isNotEmpty(checkCodeList)){
			checkCodeList.each{ checkCode ->
				Map CheckCodeMap = FastMap.newInstance();
				tempOutMap =[:];
				tempOutMap.putAll(checkCode)
				CheckCodeMap.putAll(tempOutMap);
				
				orderId =checkCode.getAt("orderId");
				orderItemSeqId =checkCode.getAt("orderItemSeqId");
				
				conditionList =[];
				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId)));
				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS ,orderItemSeqId)));
				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS ,"PURCHASE_ORDER")));
				condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				checkCodeDetailsList = delegator.findList("OrderHeaderItemProductAndFacility",condition,null,null,null,false);
				if(UtilValidate.isNotEmpty(checkCodeDetailsList)){
					checkCodes =EntityUtil.getFirst(checkCodeDetailsList);
					tempMap2=[:];
					tempMap2.putAll(checkCodes);
					CheckCodeMap.putAll(tempMap2);
					checkCodeFinalList.add(CheckCodeMap);
				}
				
			}
		}
			
		context.putAt("checkCodeFinalList", checkCodeFinalList);
		context.putAt("negativeAmtList", negativeAmtList);
		context.putAt("qtySnfFinalList", qtySnfFatFinalList);
		context.putAt("outLierFinalList", outLierFinalList);
			
			//For Negative Amount Validations
		/*JSONArray negativeAmtItemsJson = new JSONArray();
		l=0;
		if(UtilValidate.isNotEmpty(negativeAmtList)){
			negativeAmtList.each{ negativeAmt->
				++l;
				centerDetails = ProcurementNetworkServices.getCenterDtails(dctx,[centerId:negativeAmt.centerId]);
				centerCode=centerDetails.get("centerFacility").get("facilityCode");
				unitCode = centerDetails.get("unitFacility").get("facilityCode");
				unitId=centerDetails.get("unitFacility").get("facilityId");
				shedCode = centerDetails.get("shedFacility").get("facilityCode");
				JSONObject newObj = new JSONObject();
				newObj.put("id",l);
				newObj.put("shedCode",shedCode);
				newObj.put("unitCode",unitCode);
				newObj.put("centerCode",centerCode);
				newObj.put("unitId",unitId);
				newObj.put("orderId",negativeAmt.orderId);
				newObj.put("shedId",unitId);
				newObj.put("centerId",negativeAmt.centerId);
				newObj.put("sequenceNum",negativeAmt.sequenceNum);
				status = delegator.findOne("StatusItem",[statusId:negativeAmt.statusId],false);
				newObj.put("statusId",status.description);
				newObj.put("validationTypeId",negativeAmt.validationTypeId);
				customTimePeriod = delegator.findOne("CustomTimePeriod",[customTimePeriodId:negativeAmt.customTimePeriodId],false);
				newObj.put("periodName",UtilDateTime.toDateString(customTimePeriod.fromDate,"MMMdd")+"-"+UtilDateTime.toDateString(customTimePeriod.thruDate,"MMMdd yyyy"));
				newObj.put("customTimePeriodId",negativeAmt.customTimePeriodId);
				negativeAmtItemsJson.add(newObj);
			}
			context.negativeAmtItemsJson=negativeAmtItemsJson;
		}*/			
			
		JSONArray checkCodeItemsJson = new JSONArray();
		i=0;
		if(UtilValidate.isNotEmpty(checkCodeFinalList)){
			checkCodeFinalList.each{ checkCode->
				++i;					
				centerDetails = ProcurementNetworkServices.getCenterDtails(dctx,[centerId:checkCode.centerId]);
				centerCode=centerDetails.get("centerFacility").get("facilityCode");
				unitCode = centerDetails.get("unitFacility").get("facilityCode");
				unitId=centerDetails.get("unitFacility").get("facilityId");
				shedCode = centerDetails.get("shedFacility").get("facilityCode");
				shedId = centerDetails.get("shedFacility").get("facilityId");
				JSONObject newObj = new JSONObject();
				newObj.put("id",i);					
				newObj.put("shedCode",shedCode);
				newObj.put("shedName",shedId);
				newObj.put("unitCode",unitCode);
				newObj.put("centerCode",centerCode);
				estimatedDeliveryDate=UtilDateTime.toDateString(checkCode.estimatedDeliveryDate,"yyyy-MM-dd");
				newObj.put("purchaseTime",checkCode.supplyTypeEnumId);					
				newObj.put("orderDate",estimatedDeliveryDate);
				newObj.put("orderId",checkCode.orderId);
				newObj.put("orderItemSeqId",checkCode.orderItemSeqId);
				newObj.put("quantity",checkCode.quantity);
				newObj.put("fat",checkCode.fat);
				newObj.put("snf",checkCode.snf);
				newObj.put("lactoReading", checkCode.lactoReading);
				newObj.put("sQuantity",checkCode.sQuantityLtrs);
				newObj.put("sFat",checkCode.sFat);
				newObj.put("cQuantity",checkCode.cQuantityLtrs);
				newObj.put("ptcQuantity",checkCode.ptcQuantity);
				product = delegator.findOne("Product",[productId:checkCode.productId],false);
				newObj.put("productId",checkCode.productId);
				newObj.put("productName",product.brandName);
				newObj.put("unitId",unitId);
				newObj.put("sequenceNum",checkCode.sequenceNum);
				status = delegator.findOne("StatusItem",[statusId:checkCode.statusId],false);
				newObj.put("statusId",status.description);
				newObj.put("validationTypeId",checkCode.validationTypeId);
				newObj.put("customTimePeriodId",checkCode.customTimePeriodId);
				checkCodeItemsJson.add(newObj);
			}
			context.checkCodeItemsJson=checkCodeItemsJson;
		}
			
		JSONArray outLierItemsJson = new JSONArray();
		j=0;
		if(UtilValidate.isNotEmpty(outLierFinalList)){
			outLierFinalList.each{ outLier->
				++j;
				centerDetails = ProcurementNetworkServices.getCenterDtails(dctx,[centerId:outLier.centerId]);
				centerCode=centerDetails.get("centerFacility").get("facilityCode");
				unitCode = centerDetails.get("unitFacility").get("facilityCode");
				unitId=centerDetails.get("unitFacility").get("facilityId");
				shedCode = centerDetails.get("shedFacility").get("facilityCode");
				shedId = centerDetails.get("shedFacility").get("facilityId");
				JSONObject newObj = new JSONObject();
				newObj.put("id",j);					
				newObj.put("shedCode",shedCode);
				newObj.put("shedName",shedId);
				newObj.put("unitCode",unitCode);
				newObj.put("centerCode",centerCode);
				estimatedDeliveryDate=UtilDateTime.toDateString(outLier.estimatedDeliveryDate,"yyyy-MM-dd");
				newObj.put("purchaseTime",outLier.supplyTypeEnumId);
				newObj.put("orderDate",estimatedDeliveryDate);
				newObj.put("orderId",outLier.orderId);
				newObj.put("orderItemSeqId",outLier.orderItemSeqId);
				newObj.put("quantity",outLier.quantity);
				newObj.put("fat",outLier.fat);
				newObj.put("snf",outLier.snf);
				newObj.put("lactoReading", outLier.lactoReading);
				newObj.put("sQuantity",outLier.sQuantityLtrs);
				newObj.put("sFat",outLier.sFat);
				newObj.put("cQuantity",outLier.cQuantityLtrs);
				newObj.put("ptcQuantity",outLier.ptcQuantity);
				product = delegator.findOne("Product",[productId:outLier.productId],false);
				newObj.put("productId",outLier.productId);
				newObj.put("productName",product.brandName);
				newObj.put("unitId",unitId);
				newObj.put("sequenceNum",outLier.sequenceNum);
				status = delegator.findOne("StatusItem",[statusId:outLier.statusId],false);
				newObj.put("statusId",status.description);
				newObj.put("validationTypeId",outLier.validationTypeId);
				newObj.put("customTimePeriodId",outLier.customTimePeriodId);
				outLierItemsJson.add(newObj);
			}
			context.outLierItemsJson=outLierItemsJson;
		}
			
			
		JSONArray qtySnfItemsJson = new JSONArray();
		k=0;
		if(UtilValidate.isNotEmpty(qtySnfFatFinalList)){
			qtySnfFatFinalList.each{ qtySnfValue->					
				++k;
				centerDetails = ProcurementNetworkServices.getCenterDtails(dctx,[centerId:qtySnfValue.centerId]);
				centerCode=centerDetails.get("centerFacility").get("facilityCode");
				unitCode = centerDetails.get("unitFacility").get("facilityCode");
				unitId=centerDetails.get("unitFacility").get("facilityId");
				shedCode = centerDetails.get("shedFacility").get("facilityCode");
				shedId = centerDetails.get("shedFacility").get("facilityId");
				JSONObject newObj = new JSONObject();
				newObj.put("id",k);					
				newObj.put("shedCode",shedCode);
				newObj.put("shedName",shedId);
				newObj.put("unitCode",unitCode);
				newObj.put("centerCode",centerCode);
				estimatedDeliveryDate=UtilDateTime.toDateString(qtySnfValue.estimatedDeliveryDate,"yyyy-MM-dd");
				newObj.put("purchaseTime",qtySnfValue.supplyTypeEnumId);
				newObj.put("orderDate",estimatedDeliveryDate);
				newObj.put("orderId",qtySnfValue.orderId);
				newObj.put("orderItemSeqId",qtySnfValue.orderItemSeqId);
				newObj.put("quantity",qtySnfValue.quantity);
				newObj.put("fat",qtySnfValue.fat);
				newObj.put("snf",qtySnfValue.snf);
				newObj.put("lactoReading", qtySnfValue.lactoReading)
				newObj.put("sQuantity",qtySnfValue.sQuantityLtrs);
				newObj.put("sFat",qtySnfValue.sFat);
				newObj.put("cQuantity",qtySnfValue.cQuantityLtrs);
				newObj.put("ptcQuantity",qtySnfValue.ptcQuantity);
				product = delegator.findOne("Product",[productId:qtySnfValue.productId],false);
				newObj.put("productId",qtySnfValue.productId);
				newObj.put("productName",product.brandName);
				newObj.put("unitId",unitId);
				newObj.put("sequenceNum",qtySnfValue.sequenceNum);
				status = delegator.findOne("StatusItem",[statusId:qtySnfValue.statusId],false);
				newObj.put("statusId",status.description);
				newObj.put("validationTypeId",qtySnfValue.validationTypeId);
				newObj.put("customTimePeriodId",qtySnfValue.customTimePeriodId);
				qtySnfItemsJson.add(newObj);
			}				
			context.qtySnfItemsJson=qtySnfItemsJson;
		}
}
