import org.ofbiz.base.conversion.NumberConverters.BigDecimalToString;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import in.vasista.vbiz.production.ProductionServices;

import java.util.*;
import java.awt.image.renderable.ContextualRenderedImageFactory;
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

import java.math.RoundingMode;
import java.util.Map;

import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;

import in.vasista.vbiz.milkReceipts.MilkReceiptReports;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementServices;
import in.vasista.vbiz.milkReceipts.MilkReceiptBillingServices;

import org.ofbiz.party.party.PartyHelper;

dctx = dispatcher.getDispatchContext();

fromDeptId=parameters.fromDeptId;
selectProdId=parameters.productId;
thruDeptId=parameters.thruDeptId;
prodType=parameters.prodType;
reportType=parameters.reportType;


fromDate=parameters.fromDate;
thruDate=parameters.thruDate;

dctx = dispatcher.getDispatchContext();
fromDateTime = null;
thruDateTime = null;
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	fromDateTime = new java.sql.Timestamp(sdf.parse(fromDate).getTime());
	thruDateTime = new java.sql.Timestamp(sdf.parse(thruDate).getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+fromDate, "");
}
dayBegin = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(thruDateTime);

Map inMap = FastMap.newInstance();
inMap.put("userLogin", userLogin);
inMap.put("shiftType", "MILK_SHIFT");
inMap.put("fromDate", dayBegin);
inMap.put("thruDate", dayEnd);
Map workShifts = MilkReceiptBillingServices.getShiftDaysByType(dctx,inMap );

fromDate=workShifts.fromDate;
thruDate=workShifts.thruDate;
context.prodType =prodType;
context.fromDate = fromDate;
context.thruDate = dayEnd;
context.fromDeptId = fromDeptId;
context.selectProdId = selectProdId;
context.reportType = reportType;

partyName =  PartyHelper.getPartyName(delegator, thruDeptId, false);
if(UtilValidate.isNotEmpty(partyName)){
	context.thruDeptId = partyName;
}

int totalDays =UtilDateTime.getIntervalInDays(dayBegin, dayEnd);
totalDays=totalDays+1;

List<String> productIds=[];
List facilityCondList =FastList.newInstance();
facilityCondList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS,fromDeptId ));
facilityCondList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS,"PLANT" ));
EntityCondition facilityCond = EntityCondition.makeCondition(facilityCondList,EntityOperator.AND);
List<GenericValue> facility = delegator.findList("Facility", facilityCond, null, null, null, false);
List facilityIds=[];
if(UtilValidate.isNotEmpty(facility)){
	facilityIds = EntityUtil.getFieldListFromEntityList(facility, "facilityId", true);
}

Map productionMap =FastMap.newInstance();
Map productionTotalsMap =FastMap.newInstance();

Map productionTotIssMap =FastMap.newInstance();
Map productionTotDeclaresMap =FastMap.newInstance();
Map productionTotReturnsMap =FastMap.newInstance();

BigDecimal totIssueQty = BigDecimal.ZERO;
BigDecimal totDeclareQty = BigDecimal.ZERO;
BigDecimal totReturnQty = BigDecimal.ZERO;


for(int i=0; i <totalDays; i++){
	dayBeginStart = UtilDateTime.getDayStart(fromDate, i);
	Map prunIssuesMap =FastMap.newInstance();
	Map prunDeclaresMap =FastMap.newInstance();
	Map prunReturnsMap =FastMap.newInstance();
	Map prunDayWiseMap =FastMap.newInstance();
	
	
	Map inMapStr = FastMap.newInstance();
	inMapStr.put("userLogin", userLogin);
	inMapStr.put("shiftType", "MILK_SHIFT");
	inMapStr.put("fromDate", dayBeginStart);
	Map dayWorkShifts = MilkReceiptBillingServices.getShiftDaysByType(dctx,inMapStr );
	
	fromDateStart=dayWorkShifts.fromDate;
	thruDateEnd=dayWorkShifts.thruDate;
	
	List workEffortIds =[];
	List workEffCondList =FastList.newInstance();
	workEffCondList.add(EntityCondition.makeCondition("estimatedStartDate", EntityOperator.GREATER_THAN_EQUAL_TO,fromDateStart ));
	workEffCondList.add(EntityCondition.makeCondition("estimatedStartDate", EntityOperator.LESS_THAN_EQUAL_TO,thruDateEnd ));
	workEffCondList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN,facilityIds ));
	workEffCondList.add(EntityCondition.makeCondition("workEffortParentId", EntityOperator.EQUALS,null ));
	workEffCond = EntityCondition.makeCondition(workEffCondList,EntityOperator.AND);
	List<GenericValue> workEffort = delegator.findList("WorkEffort", workEffCond, null, null, null, false);
	if(UtilValidate.isNotEmpty(workEffort)){
		workEffortIds = EntityUtil.getFieldListFromEntityList(workEffort, "workEffortId", true);
	}
	if(UtilValidate.isNotEmpty(workEffortIds)){
		workEffortIds.each {eachWorkEffId->
			productionDetails = ProductionServices.getProductionRunDetails(dctx, [ workEffortId: eachWorkEffId, userLogin: userLogin,]);
			issuedProductsList=productionDetails.get("issuedProductsMap");
			declareProductsList=productionDetails.get("declareProductsList");
			returnProductsList=productionDetails.get("returnProductsList");
			//qcComponentsList=productionDetails.get("qcComponentsList");
			
			if(UtilValidate.isEmpty(prodType) || (UtilValidate.isNotEmpty(prodType) && prodType.equals("IssueProducts"))){
				
				if(UtilValidate.isNotEmpty(issuedProductsList)){
					issuedProductsList.each {eachissuedProduct->
						String productId = eachissuedProduct.getValue().get("issuedProdId");
						BigDecimal quantity = eachissuedProduct.getValue().get("issuedQty");
						if(UtilValidate.isNotEmpty(quantity) && (UtilValidate.isEmpty(selectProdId) || (UtilValidate.isNotEmpty(selectProdId) && selectProdId.equals(productId)))){
							//totIssueQty=totIssueQty+quantity;
							if(UtilValidate.isEmpty(prunIssuesMap) || (UtilValidate.isNotEmpty(prunIssuesMap) && UtilValidate.isEmpty(prunIssuesMap.get(productId)))){
								Map tempOpenMap = FastMap.newInstance();
								tempOpenMap.put("productId", productId);
								tempOpenMap.put("quantity", quantity);
								prunIssuesMap.put(productId,tempOpenMap);
							 }else{
								Map tempMap = FastMap.newInstance();
								tempMap=(Map) prunIssuesMap.get(productId);
								BigDecimal tempQty =(BigDecimal) tempMap.get("quantity");
								tempMap.put("quantity", ((BigDecimal) tempMap.get("quantity"))+quantity);
								
								prunIssuesMap.put(productId,tempMap);
							}
							 if(UtilValidate.isEmpty(productionTotIssMap) || (UtilValidate.isNotEmpty(productionTotIssMap) && UtilValidate.isEmpty(productionTotIssMap.get(productId)))){
								 Map tempOpenMap = FastMap.newInstance();
								 tempOpenMap.put("productId", productId);
								 tempOpenMap.put("quantity", quantity);
								 productionTotIssMap.put(productId,tempOpenMap);
							  }else{
								 Map tempMap = FastMap.newInstance();
								 tempMap=(Map) productionTotIssMap.get(productId);
								 BigDecimal tempQty =(BigDecimal) tempMap.get("quantity");
								 tempMap.put("quantity", ((BigDecimal) tempMap.get("quantity"))+quantity);
								 
								 productionTotIssMap.put(productId,tempMap);
							 }
	 
						}
					}
				}
			}
			if(UtilValidate.isEmpty(prodType) || (UtilValidate.isNotEmpty(prodType) && prodType.equals("DeclareProducts"))){
				if(UtilValidate.isNotEmpty(declareProductsList)){
					declareProductsList.each {eachDeclareProduct->
						String productId = eachDeclareProduct.get("declareProdId");
						BigDecimal quantity = eachDeclareProduct.get("declareQty");
						if(UtilValidate.isNotEmpty(quantity) && (UtilValidate.isEmpty(selectProdId) || (UtilValidate.isNotEmpty(selectProdId) && selectProdId.equals(productId)))){
							//totDeclareQty=totDeclareQty+quantity;
							if(UtilValidate.isEmpty(prunDeclaresMap) || (UtilValidate.isNotEmpty(prunDeclaresMap) && UtilValidate.isEmpty(prunDeclaresMap.get(productId)))){
								Map tempOpenMap = FastMap.newInstance();
								tempOpenMap.put("productId", productId);
								tempOpenMap.put("quantity", quantity);
								prunDeclaresMap.put(productId,tempOpenMap);
							 }else{
								Map tempMap = FastMap.newInstance();
								tempMap=(Map) prunDeclaresMap.get(productId);
								BigDecimal tempQty =(BigDecimal) tempMap.get("quantity");
								tempMap.put("quantity", ((BigDecimal) tempMap.get("quantity"))+quantity);
								prunDeclaresMap.put(productId,tempMap);
							}
							 if(UtilValidate.isEmpty(productionTotDeclaresMap) || (UtilValidate.isNotEmpty(productionTotDeclaresMap) && UtilValidate.isEmpty(productionTotDeclaresMap.get(productId)))){
								 Map tempOpenMap = FastMap.newInstance();
								 tempOpenMap.put("productId", productId);
								 tempOpenMap.put("quantity", quantity);
								 productionTotDeclaresMap.put(productId,tempOpenMap);
							  }else{
								 Map tempMap = FastMap.newInstance();
								 tempMap=(Map) productionTotDeclaresMap.get(productId);
								 BigDecimal tempQty =(BigDecimal) tempMap.get("quantity");
								 tempMap.put("quantity", ((BigDecimal) tempMap.get("quantity"))+quantity);
								 
								 productionTotDeclaresMap.put(productId,tempMap);
							 }
	
						}
					}
				}		
			}	
			if(UtilValidate.isEmpty(prodType) || (UtilValidate.isNotEmpty(prodType) && prodType.equals("ReturnProducts"))){
				if(UtilValidate.isNotEmpty(returnProductsList)){
					returnProductsList.each {eachReturnProduct->
						String productId = eachReturnProduct.get("returnProdId");
						BigDecimal quantity = eachReturnProduct.get("returnQty");
						if(UtilValidate.isNotEmpty(quantity) && (UtilValidate.isEmpty(selectProdId) || (UtilValidate.isNotEmpty(selectProdId) && selectProdId.equals(productId)))){
							//totReturnQty=totReturnQty+quantity;
							if(UtilValidate.isEmpty(prunReturnsMap) || (UtilValidate.isNotEmpty(prunReturnsMap) && UtilValidate.isEmpty(prunReturnsMap.get(productId)))){
								Map tempOpenMap = FastMap.newInstance();
								tempOpenMap.put("productId", productId);
								tempOpenMap.put("quantity", quantity);
								prunReturnsMap.put(productId,tempOpenMap);
							 }else{
								Map tempMap = FastMap.newInstance();
								tempMap=(Map) prunReturnsMap.get(productId);
								BigDecimal tempQty =(BigDecimal) tempMap.get("quantity");
								tempMap.put("quantity", ((BigDecimal) tempMap.get("quantity"))+quantity);
								prunReturnsMap.put(productId,tempMap);
							}
							 if(UtilValidate.isEmpty(productionTotReturnsMap) || (UtilValidate.isNotEmpty(productionTotReturnsMap) && UtilValidate.isEmpty(productionTotReturnsMap.get(productId)))){
								 Map tempOpenMap = FastMap.newInstance();
								 tempOpenMap.put("productId", productId);
								 tempOpenMap.put("quantity", quantity);
								 productionTotReturnsMap.put(productId,tempOpenMap);
							  }else{
								 Map tempMap = FastMap.newInstance();
								 tempMap=(Map) productionTotReturnsMap.get(productId);
								 BigDecimal tempQty =(BigDecimal) tempMap.get("quantity");
								 tempMap.put("quantity", ((BigDecimal) tempMap.get("quantity"))+quantity);
								 
								 productionTotReturnsMap.put(productId,tempMap);
							 }
						}
				}
	
			}
		}
		prunDayWiseMap.put("prunIssuesMap",prunIssuesMap);
		prunDayWiseMap.put("prunDeclaresMap",prunDeclaresMap);
		prunDayWiseMap.put("prunReturnsMap",prunReturnsMap);
		productionMap.put(fromDateStart, prunDayWiseMap);
			
	}

	
}

}
productionTotalsMap.put("productionTotIssMap",productionTotIssMap);
productionTotalsMap.put("productionTotDeclaresMap",productionTotDeclaresMap);
productionTotalsMap.put("productionTotReturnsMap",productionTotReturnsMap);
if(UtilValidate.isEmpty(reportType) || (UtilValidate.isNotEmpty(reportType) && reportType.equals("prunDetail"))){
	context.productionMap=productionMap;
}
if(UtilValidate.isEmpty(reportType) || (UtilValidate.isNotEmpty(reportType) && reportType.equals("abstract"))){
	context.productionTotalsMap=productionTotalsMap;
}
