import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import javolution.util.FastMap;
import java.sql.Timestamp;
import org.ofbiz.base.util.UtilDateTime;
import java.text.SimpleDateFormat;
import org.ofbiz.party.party.PartyHelper;
import java.text.ParseException;
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.product.product.ProductWorker;
import in.vasista.vbiz.facility.util.FacilityUtil;
import in.vasista.vbiz.byproducts.icp.ICPServices;
import in.vasista.vbiz.purchase.MaterialHelperServices;
import java.io.ObjectOutputStream.DebugTraceInfoStack;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javolution.util.FastList;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.datasource.GenericHelperInfo;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.service.GenericDispatcher;

HttpServletRequest httpRequest = (HttpServletRequest) request;
HttpServletResponse httpResponse = (HttpServletResponse) response;
dctx = dispatcher.getDispatchContext();
delegator = DelegatorFactory.getDelegator("default#NHDC");
List formatRList = [];
List formatBList = [];
List<GenericValue> partyClassificationList = null;
	partyClassificationList = delegator.findList("PartyClassification", EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.IN, UtilMisc.toList("REGIONAL_OFFICE","BRANCH_OFFICE")), UtilMisc.toSet("partyId","partyClassificationGroupId"), null, null,false);
if(partyClassificationList){
	for (eachList in partyClassificationList) {
		formatMap = [:];
		partyName = PartyHelper.getPartyName(delegator, eachList.get("partyId"), false);
		formatMap.put("productStoreName",partyName);
		formatMap.put("payToPartyId",eachList.get("partyId"));
		if(eachList.partyClassificationGroupId=="REGIONAL_OFFICE"){
			formatRList.addAll(formatMap);
		}else{
			formatBList.addAll(formatMap);
		}
	}
}
context.formatRList = formatRList;
context.formatBList = formatBList;
daystart = null;
dayend = null; 
period=parameters.period;
isFormSubmitted=parameters.isFormSubmitted;
context.period=period;
periodFrmDate=null;
conditionList = [];
conditionList.add(EntityCondition.makeCondition("geoId", EntityOperator.LIKE,"IN-%"));
conditionList.add(EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS,"STATE"));
statesList = delegator.findList("Geo",EntityCondition.makeCondition(conditionList,EntityOperator.AND),null,null,null,false);
statesIdsList=EntityUtil.getFieldListFromEntityList(statesList, "geoId", true);

JSONArray stateListJSON = new JSONArray();
statesList.each{ eachState ->
		JSONObject newObj = new JSONObject();
		newObj.put("value",eachState.geoId);
		newObj.put("label",eachState.geoName);
		stateListJSON.add(newObj);
}
context.stateListJSON = stateListJSON;
if("Y".equals(isFormSubmitted)){
	branchIds=[]; 
	branchId = parameters.branchId2;
	searchType = parameters.searchType;
	branchIdName =  PartyHelper.getPartyName(delegator, branchId, false);
	context.branchId=branchId;
	context.branchIdName=branchIdName;
	if(UtilValidate.isNotEmpty(branchId) && "BY_BO".equals(searchType)){
		branchIds.add(branchId)
	}
	regionId = parameters.regionId;
	regionIdName =  PartyHelper.getPartyName(delegator, regionId, false);
	context.regionId=regionId;
	context.regionIdName=regionIdName;
	if(UtilValidate.isNotEmpty(regionId) && "BY_RO".equals(searchType)){
		partyRelationship = delegator.findList("PartyRelationship", EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS,regionId), UtilMisc.toSet("partyIdTo"), null, null,false);
		branchIds=EntityUtil.getFieldListFromEntityList(partyRelationship, "partyIdTo", true);
	}
	stateId = parameters.stateId;
	if(UtilValidate.isNotEmpty(stateId) && "BY_STATE".equals(searchType)){
		GenericValue state=delegator.findOne("Geo",[geoId:stateId],false);
		context.stateId=stateId;
		context.stateIdName=state.geoName;
		result = dispatcher.runSync("getRegionalAndBranchOfficesByState",UtilMisc.toMap("state",stateId,"userLogin",userLogin));
		stateBranchsList=result.get("stateBranchsList");
		stateRosList=result.get("stateRosList");
		stateBranchsList.each{ eachState ->
			branchIds.add(eachState.partyId);
		}
		stateRosList.each{ eachState ->
			branchIds.add(eachState.partyId);
		}
	}
	dayend =UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
	
	if("One_Month".equals(period)){
		daystart = UtilDateTime.addDaysToTimestamp(dayend,-30);
		context.periodName="Last One Month";
	}
	if("Two_Month".equals(period)){
		daystart = UtilDateTime.addDaysToTimestamp(dayend,-60);
		context.periodName="Last Two Months";
	}
	if("Three_Month".equals(period)){
		daystart = UtilDateTime.addDaysToTimestamp(dayend,-90);
		context.periodName="Last Three  Months";
	}
	if("Six_Month".equals(period)){
		daystart = UtilDateTime.addDaysToTimestamp(dayend,-180);
		context.periodName="Last  Six Months";
	}
	JSONArray dataList = new JSONArray();
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN,branchIds));
	conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL,"ORDER_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER"));
	if(UtilValidate.isNotEmpty(daystart)){
		conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, daystart));
		conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, dayend));
	}
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	orderHeaderAndRoles = delegator.findList("OrderHeaderAndRoles", condition,UtilMisc.toSet("orderId"), null, null, false);
	branchPoIds=EntityUtil.getFieldListFromEntityList(orderHeaderAndRoles, "orderId", true);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, branchPoIds));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "SHIPMENT_CANCELLED"));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	List<String> payOrderBy = UtilMisc.toList("supplierInvoiceDate");
	ShipmentList = delegator.findList("ShipmentAndReceipt", condition,UtilMisc.toSet("orderId","shipmentId","supplierInvoiceDate","quantityAccepted"), payOrderBy, null, false);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,"BILL_FROM_VENDOR"));
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, branchPoIds));
	orderHeaderAndRoleSuppliers = delegator.findList("OrderHeaderItemAndRoles",EntityCondition.makeCondition(conditionList,EntityOperator.AND),UtilMisc.toSet("partyId","orderId","orderDate","quantity"), null, null, false);
	partyIdsFromOrders=EntityUtil.getFieldListFromEntityList(orderHeaderAndRoleSuppliers, "partyId", true);
	orderHeaderSequenceList = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId",EntityOperator.IN,branchPoIds),UtilMisc.toSet("orderNo","orderId"), null, null, false);
	orderIds=[];
	for (eachParty in partyIdsFromOrders) {
		totordQty=0
		totShipQty=0;
		totDelayDays=0;
		JSONObject totNewObj = new JSONObject();
		orderHeaderAndRole = EntityUtil.filterByCondition(orderHeaderAndRoleSuppliers, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,eachParty));
		double peddingShipments = 0;
		if(orderHeaderAndRole){
			  for (eachList in orderHeaderAndRole) {
				  if(!orderIds.contains(eachList.orderId)){
					  Shipment = EntityUtil.filterByCondition(ShipmentList, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,eachList.orderId));
					  /*conditionList.clear();
					  conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, eachList.orderId));
					  conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_APPROVED"));
					  condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
					  OrderStatus = delegator.findList("OrderStatus", condition,UtilMisc.toSet("orderId","statusDatetime"), null, null, false);*/
					 /* statusDatetime =null;
					  if(OrderStatus[0]){
						  statusDatetime = OrderStatus[0].get("statusDatetime");
					  }*/
					  statusDatetime=eachList.orderDate;
					  orderHeaderAndRole2 = EntityUtil.filterByCondition(orderHeaderAndRole, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,eachList.orderId));
					  shipedQty=0;
					  ordQty=0;
					  supplierName =  PartyHelper.getPartyName(delegator, eachParty, false);
					   if(UtilValidate.isNotEmpty(Shipment)){
						  ShipmentFirst = Shipment[0];
						  supplierInvoiceDate  = ShipmentFirst.supplierInvoiceDate;
						  long timeDiff = 0;
						  if(statusDatetime && supplierInvoiceDate){
							  timeDiff = supplierInvoiceDate.getTime() - statusDatetime.getTime();
						  }
						  diffHours = timeDiff / (60 * 60 * 1000);
						  int diffHours = diffHours.intValue();
						  diffDays = diffHours/24;
						  diffDays = Math.abs(diffDays);
						  for(eachRecord in Shipment){
							  shipedQty=shipedQty+eachRecord.quantityAccepted;
						  }
						  for(eachRecord in orderHeaderAndRole2){
							  ordQty=ordQty+eachRecord.quantity;
						  }
						  if(diffDays >= 7){
							  JSONObject newObj = new JSONObject();
							  orderHeaderSequence = EntityUtil.filterByCondition(orderHeaderSequenceList, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,eachList.orderId));
							  orderseq=EntityUtil.getFirst(orderHeaderSequence).get("orderNo")
							  newObj.put("orderId",orderseq)
							  dateStr=UtilDateTime.toDateString(eachList.orderDate,"dd/MM/yyyy");
							  //statusDatetime=UtilDateTime.addDaysToTimestamp(statusDatetime,diffDays);
							  shptDateStr=UtilDateTime.toDateString(supplierInvoiceDate,"dd/MM/yyyy");
							  newObj.put("orderDate",dateStr)
							  newObj.put("shipDate",shptDateStr)
							  newObj.put("supplierName",supplierName)
							  newObj.put("shipedQty",shipedQty)
							  newObj.put("ordQty",ordQty)
							  newObj.put("diffDays",(int)diffDays)
							  totordQty=totordQty+ordQty;
							  totShipQty=totShipQty+shipedQty;
							  totDelayDays=totDelayDays+diffDays;
							  dataList.add(newObj);
							  orderIds.add(eachList.orderId)
						  }
					  }else if(UtilValidate.isNotEmpty(statusDatetime)){
					     JSONObject newObj = new JSONObject();
					     orderHeaderSequence = EntityUtil.filterByCondition(orderHeaderSequenceList, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,eachList.orderId));
					     orderseq=EntityUtil.getFirst(orderHeaderSequence).get("orderNo")
						newObj.put("orderId",orderseq)
						dateStr=UtilDateTime.toDateString(statusDatetime,"dd/MM/yyyy");
						newObj.put("orderDate",dateStr)
						newObj.put("shipDate","-")
						currentDate=UtilDateTime.nowTimestamp()
						timeDiff =currentDate.getTime() - statusDatetime.getTime();
						diffHours = timeDiff / (60 * 60 * 1000);
						int diffHours = diffHours.intValue();
						diffDays = diffHours/24;
						for(eachRecord in orderHeaderAndRole2){
							ordQty=ordQty+eachRecord.quantity;
						}
						newObj.put("diffDays",(int)diffDays+1)
						newObj.put("supplierName",supplierName)
						newObj.put("shipedQty","-")
						newObj.put("ordQty",ordQty)
						totordQty=totordQty+ordQty;
						totDelayDays=totDelayDays+diffDays;
						dataList.add(newObj);
						orderIds.add(eachList.orderId)
					  }
				  }
			  }
		}
		if(totordQty > 0){
			totNewObj.put("orderId","Total")
			totNewObj.put("diffDays",(int)totDelayDays+1)
			totNewObj.put("shipedQty",totShipQty)
			totNewObj.put("ordQty",totordQty)
			dataList.add(totNewObj);
		}
	}
	context.dataList=dataList;
}
