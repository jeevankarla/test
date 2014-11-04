import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.awt.image.renderable.ContextualRenderedImageFactory;
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
import org.ofbiz.base.util.UtilNumber;

import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.milkReceipts.MilkReceiptReports;
import in.vasista.vbiz.procurement.PriceServices;


Timestamp fromDate;
Timestamp thruDate; 
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
String fromDateString = parameters.fromDate;
String thruDateString = parameters.thruDate;
try {
	   if (parameters.fromDate) {
			   fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
	   }
	   if(parameters.thruDate){
		   thruDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.thruDate).getTime()));
	   }
	  
} catch (ParseException e) {
	   context.errorMessage = "Cannot parse date string: " + e;
	   return;
}
context.putAt("fromDate", fromDate);
context.putAt("thruDate", thruDate);
dctx = dispatcher.getDispatchContext();
context.putAt("dctx", dctx);
procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getMilkReceiptProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
context.put("procurementProductList", procurementProductList);

List productWiseList = FastList.newInstance();
productWiseList.addAll(EntityUtil.getFieldListFromEntityList(procurementProductList, "productId", true));
Map initMap = FastMap.newInstance();
initMap.put("recdQtyLtrs", BigDecimal.ZERO);
initMap.put("recdQtyKgs", BigDecimal.ZERO);
initMap.put("recdKgFat", BigDecimal.ZERO);
initMap.put("recdKgSnf", BigDecimal.ZERO);
initMap.put("recdFat", BigDecimal.ZERO);
initMap.put("recdSnf", BigDecimal.ZERO);
initMap.put("sendQtyLtrs", BigDecimal.ZERO);
initMap.put("sendQtyKgs", BigDecimal.ZERO);
initMap.put("sendKgFat", BigDecimal.ZERO);
initMap.put("sendKgSnf", BigDecimal.ZERO);
initMap.put("totSolids", BigDecimal.ZERO);
initMap.put("opCost", BigDecimal.ZERO);

Map initProductMap = FastMap.newInstance();
for(productId in productWiseList){
		tempInitMap = [:];
		tempInitMap.putAll(initMap);
		initProductMap.put(productId, tempInitMap);
	}
List finalAbstractSupportList = FastList.newInstance();
conditionList =[];
conditionList.add(EntityCondition.makeCondition("mccTypeId", EntityOperator.IN , UtilMisc.toList("UNION","OTHERS")));
conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS , "SHED"));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
List shedsList = delegator.findList("Facility",condition,null,null,null,false);

shedIds= EntityUtil.getFieldListFromEntityList(shedsList, "facilityId", true);

List unionOthersConditionList = FastList.newInstance();
unionOthersConditionList.add(EntityCondition.makeCondition("parentFacilityId",EntityOperator.IN,shedIds));
unionOthersConditionList.add(EntityCondition.makeCondition("mccCode",EntityOperator.NOT_EQUAL,null));
EntityCondition unionOthersCondition = EntityCondition.makeCondition(unionOthersConditionList,EntityOperator.AND);
unionAndOthersDetailsList = delegator.findList("Facility",unionOthersCondition,null,null,null,false);

facilityIds= EntityUtil.getFieldListFromEntityList(unionAndOthersDetailsList, "facilityId", true);
Set facilityIdsSet = new HashSet(facilityIds);
Map privateDairiesMap = FastMap.newInstance();
Map productWiseTotalsMap = FastMap.newInstance();
//productWiseTotalsMap.putAll(initProductMap);
facilityIdsSet.each{ facilityId ->
	conList =[];
	milkDetailslist =[];	
	conList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS , facilityId));	
	conList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "MXF_RECD"));
	conList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate),
					EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate)],EntityOperator.AND));
	EntityCondition cond = EntityCondition.makeCondition(conList,EntityOperator.AND);
	milkDetailslist = delegator.findList("MilkTransferAndMilkTransferItem",cond,null,null,null,false);
	milkDetailslist = UtilMisc.sortMaps(milkDetailslist,UtilMisc.toList("-sendProductId"));
	Map productWisePrivateDairiesMap= FastMap.newInstance();
	if(UtilValidate.isNotEmpty(milkDetailslist)){
			List produtsList = EntityUtil.getFieldListFromEntityList(milkDetailslist, "sendProductId", false);
			Set produtsSet = new HashSet(produtsList);
			Map inputMap = FastMap.newInstance();
			inputMap.put("userLogin", userLogin);
			inputMap.put("fromDate", fromDateString);
			inputMap.put("thruDate", thruDateString);
			inputMap.put("unitId", facilityId);
			for(productId in produtsSet){
				 	inputMap.put("productId", productId);
					Map resultMap = dispatcher.runSync("getUnionOthersBillsList", inputMap);
					if(UtilValidate.isNotEmpty(resultMap) && UtilValidate.isNotEmpty(resultMap.get("finalBillingList"))){
							finalAbstractSupportList.add(resultMap);
						}
				}
		}//End  of the If 
}//end of facilityIds

context.put("finalAbstractSupportList",finalAbstractSupportList);