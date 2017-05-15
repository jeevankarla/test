import org.ofbiz.base.util.UtilDateTime
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import net.sf.json.JSONObject;
import javolution.util.FastList;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.party.contact.ContactMechWorker;
import javolution.util.FastMap;
import java.text.ParseException;
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.facility.util.FacilityUtil;
import org.ofbiz.service.GenericServiceException;
import java.math.RoundingMode;

BranchList=[];
	branchMap = [:];
	branchName="";
	 branchId = parameters.branchId;
	 if(branchId){
	branch = delegator.findOne("PartyGroup",[partyId : branchId] , false);
	branchName = branch.get("groupName");
	 }
	branchMap.put("branchName", branchName);
	BranchList.add(branchMap);
	context.BranchList=BranchList;
	dctx = dispatcher.getDispatchContext();	
	SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");
	dayend = null;
	daystart = null;
	Timestamp fromDate;
	Timestamp thruDate;
	
	partyfromDate=parameters.fromDate;
	partythruDate=parameters.thruDate;
	facilityPartyId=parameters.partyId;
	
	context.partyfromDate=partyfromDate;
	context.partythruDate=partythruDate;
	rounding = RoundingMode.HALF_UP;
	
	daystart = null;
	dayend = null;
	if(UtilValidate.isNotEmpty(partyfromDate)){
		try {
			fromDate = new java.sql.Timestamp(sdf.parse(partyfromDate).getTime());
			daystart = UtilDateTime.getDayStart(fromDate);
			 } catch (ParseException e) {
				 //////Debug.logError(e, "Cannot parse date string: " + parameters.partyfromDate, "");
			}
	}
	if(UtilValidate.isNotEmpty(partythruDate)){
	   try {
		   thruDate = new java.sql.Timestamp(sdf.parse(partythruDate).getTime());
		   dayend = UtilDateTime.getDayEnd(thruDate);
	   } catch (ParseException e) {
		   //////Debug.logError(e, "Cannot parse date string: " + parameters.partythruDate, "");
			}
	}
	context.daystart=daystart
	context.dayend=dayend
	
	JSONArray orderList=new JSONArray();
	 List condList = [];
	
	inputFields = [:];
	inputFields.put("noConditionFind", "Y");
	inputFields.put("hideSearch","Y");
	branchIdForAdd="";
	branchList=[];
	condListb = [];
	if(UtilValidate.isNotEmpty(branchId)){
	condListb.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, branchId));
	condListb.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
	condListb = EntityCondition.makeCondition(condListb, EntityOperator.AND);
	PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdTo"), null, null, false);
	branchList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);
	}
	if(!branchList){
		condListb2 = [];
		//condListb2.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS,"%"));
		condListb2.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, branchId));
		condListb2.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
		condListb2.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
		cond = EntityCondition.makeCondition(condListb2, EntityOperator.AND);
		
		PartyRelationship1 = delegator.findList("PartyRelationship", cond,UtilMisc.toSet("partyIdFrom"), null, null, false);
		if(PartyRelationship1){
		branchDetails = EntityUtil.getFirst(PartyRelationship1);
		branchIdForAdd=branchDetails.partyIdFrom;
		}
	}
	else{
		if(branchId){
		branchIdForAdd=branchId;
		}
	}
	if(!branchList)
	branchList.add(branchId);
	
	orderRoles = [];
	branchBasedOrderIds=[];
	if(UtilValidate.isNotEmpty(branchList)){
		custCondList = [];
		if((UtilValidate.isNotEmpty(branchList))){
			custCondList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, branchList));
		}
		custCondList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
		custCond = EntityCondition.makeCondition(custCondList, EntityOperator.AND);
		orderRoles = delegator.findList("OrderRole", custCond, null, null, null, false);
		branchBasedOrderIds = EntityUtil.getFieldListFromEntityList(orderRoles, "orderId", true);
	}
	
	custOrderRoles =[];
	//custBasededOrderIds=[];
	if(UtilValidate.isNotEmpty(facilityPartyId)){
		custCondList = [];
		custCondList.add(EntityCondition.makeCondition("partyId",  EntityOperator.EQUALS, facilityPartyId));
		custCondList.add(EntityCondition.makeCondition("orderId",  EntityOperator.IN, branchBasedOrderIds));
		custCondList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SHIP_TO_CUSTOMER"));
		custCond = EntityCondition.makeCondition(custCondList, EntityOperator.AND);
		custOrderRoles = delegator.findList("OrderRole", custCond, null, null, null, false);
		branchBasedOrderIds = EntityUtil.getFieldListFromEntityList(custOrderRoles, "orderId", true);
	}
	
		condList.add(EntityCondition.makeCondition("orderId" ,EntityOperator.IN, branchBasedOrderIds));
		condList.add(EntityCondition.makeCondition("statusId" ,EntityOperator.NOT_EQUAL,"ORDER_CANCELLED"));
		condList.add(EntityCondition.makeCondition("purposeTypeId" ,EntityOperator.EQUALS, "BRANCH_SALES"));
		condList.add(EntityCondition.makeCondition("entryDate", EntityOperator.GREATER_THAN_EQUAL_TO, daystart));
		condList.add(EntityCondition.makeCondition("entryDate", EntityOperator.LESS_THAN_EQUAL_TO, dayend));
		condList.add(EntityCondition.makeCondition("shipmentId" ,EntityOperator.EQUALS, null)); // Review

	if(parameters.indentDateSort)
	dateSort = parameters.indentDateSort;
	else
	dateSort = "-orderDate";
	
	cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	List<String> payOrderBy = UtilMisc.toList(dateSort,"-orderId");
	
	resultList = [];
	forIndentsCount = [];
	int totalIndents = 0
	orderHeader = delegator.findList("OrderHeader", cond, null, payOrderBy, null, false);
	orderIds=EntityUtil.getFieldListFromEntityList(orderHeader, "orderId", true);
	BOAddress="";
	BOEmail="";
	if(branchIdForAdd){
	branchContextForADD=[:];
	branchContextForADD.put("branchId",branchIdForAdd);
	try{
		resultCtx = dispatcher.runSync("getBoHeader", branchContextForADD);
		if(ServiceUtil.isError(resultCtx)){
			Debug.logError("Problem in BO Header ", module);
			return ServiceUtil.returnError("Problem in fetching financial year ");
		}
		if(resultCtx.get("boHeaderMap")){
			boHeaderMap=resultCtx.get("boHeaderMap");
			
			if(boHeaderMap.get("header0")){
				BOAddress=boHeaderMap.get("header0");
			}
			if(boHeaderMap.get("header1")){
				BOEmail=boHeaderMap.get("header1");
			}
		}
	}catch(GenericServiceException e){
	Debug.logError(e, module);
	return ServiceUtil.returnError(e.getMessage());
	}
	context.BOAddress=BOAddress;
	context.BOEmail=BOEmail;
	}
		
	stylesMap=[:];
	if(branchId){
		stylesMap.put("mainHeader1", "NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD");
		stylesMap.put("mainHeader2", BOAddress);
		stylesMap.put("mainHeader3", "Purchase Order Report");
	}
	else{
		stylesMap.put("mainHeader1", "NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD");
		stylesMap.put("mainHeader2", "Purchase Register Report");
	}
	stylesMap.put("mainHeaderFontName","Arial");
	stylesMap.put("mainHeadercellHeight",300);
	stylesMap.put("mainHeadingCell",4);
	stylesMap.put("mainHeaderFontSize",10);
	stylesMap.put("mainHeaderBold",true);
	stylesMap.put("columnHeaderBgColor",false);
	stylesMap.put("columnHeaderFontName","Arial");
	stylesMap.put("columnHeaderFontSize",10);
	stylesMap.put("autoSizeCell",true);
	stylesMap.put("columnHeaderCellHeight",300);
	request.setAttribute("stylesMap", stylesMap);
	request.setAttribute("enableStyles", true);
	orderList.add(stylesMap);
	headerData=[:];
	headerData.put("orderNo", "Indent Sequence Id");
	headerData.put("indentRefNo", "Indent Reference No");
	headerData.put("poNo", "PO Id");
	headerData.put("poQty", "PO Qty");
	headerData.put("poUnitPrice", "PO Rate");
	headerData.put("poAmount", "PO Value");
	headerData.put("value", "Value");
	headerData.put("amount", "Amount");
	headerData.put("weaverName", "Weaver Name");
	headerData.put("poSquenceNo", "PO Sequence No");
	headerData.put("poDate", "PO Date");
	headerData.put("supplierName", "Supplier Name");
	headerData.put("productName","Product Name");
	orderList.add(headerData);
	
	orderHeader.each{ eachHeader ->
		orderId = eachHeader.orderId;
		
		custOrderRoles.clear();
		partyId = "";
		if(UtilValidate.isEmpty(partyId)){
			custCondList = [];
			custCondList.add(EntityCondition.makeCondition("orderId",  EntityOperator.EQUALS, orderId));
			custCondList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SHIP_TO_CUSTOMER"));
			custCond = EntityCondition.makeCondition(custCondList, EntityOperator.AND);
			 custOrderRoles = delegator.findList("OrderRole", custCond, null, null, null, false);
													
		}
			//custBasededOrderIds = EntityUtil.getFieldListFromEntityList(custOrderRoles, "partyId", true);
			if(custOrderRoles)
			partyId = EntityUtil.getFirst(custOrderRoles).get("partyId");
		
		if(partyId)	
		partyName = PartyHelper.getPartyName(delegator, partyId, false);
		
		tempData=[:];
		orderNo ="NA";
		orderHeaderSequences = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , eachHeader.orderId)  , null, null, null, false );
		if(UtilValidate.isNotEmpty(orderHeaderSequences)){
			orderSeqDetails = EntityUtil.getFirst(orderHeaderSequences);
			orderNo = orderSeqDetails.orderNo;
		}
		
		resultCtx = dispatcher.runSync("getAssociateOrder",UtilMisc.toMap("userLogin",userLogin, "orderId", orderId));
		POorder="NA";
		isgeneratedPO="N";
		if(resultCtx.orderId){
			POorder=resultCtx.orderId;
			isgeneratedPO = "Y";
		}
		poSquenceNo="NA";
		poOrderHeaderSequences = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , POorder)  , null, null, null, false );
		if(UtilValidate.isNotEmpty(poOrderHeaderSequences)){
			poOrderSeqDetails = EntityUtil.getFirst(poOrderHeaderSequences);
			poSquenceNo = poOrderSeqDetails.orderNo;
		}
		exprList=[];
		exprList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
		exprList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SUPPLIER"));
		EntityCondition discontinuationDateCondition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
		supplierPartyId="";
		productStoreId="";
		supplierDetails = EntityUtil.getFirst(delegator.findList("OrderRole", discontinuationDateCondition, null,null,null, false));
		if(supplierDetails){
			supplierPartyId=supplierDetails.get("partyId");
		}
		supplierPartyName="";
		if(supplierPartyId){
			supplierPartyName = PartyHelper.getPartyName(delegator, supplierPartyId, false);
		}
		productStoreId=eachHeader.productStoreId;
		poId="";
		poQty=0;
		//BigDecimal poUnitPrice=0;
		//BigDecimal poAmount=0;
		poUnitPrice=0;
		poAmount=0;
		productName="";
		custCondList.clear();
		custCondList.add(EntityCondition.makeCondition("toOrderId",  EntityOperator.EQUALS, orderId));
		custCondList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, "BackToBackOrder"));
		custCond1 = EntityCondition.makeCondition(custCondList, EntityOperator.AND);
		orderAssocList = delegator.findList("OrderAssoc", custCond1, null, null, null, false);
		if(UtilValidate.isNotEmpty(orderAssocList)){
			orderAssoc = EntityUtil.getFirst(orderAssocList);
			poId=orderAssoc.get("orderId");
			custCondList.clear();
			custCondList.add(EntityCondition.makeCondition("orderId",  EntityOperator.IN, UtilMisc.toList(orderId,poId)));
			custCond2 = EntityCondition.makeCondition(custCondList, EntityOperator.AND);
			orderItemList = delegator.findList("OrderItem", custCond2, null, null, null, false);
			if(UtilValidate.isNotEmpty(orderItemList)){
								
				orderItemList2 = EntityUtil.filterByCondition(orderItemList, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, poId));
				
				if(UtilValidate.isNotEmpty(orderItemList)){
					for(orderItem in orderItemList2){
						poQty=orderItem.quantity;
						poUnitPrice=orderItem.unitPrice;
						//poUnitPrice=Math.round(poUnitPrice * 100) / 100;
						poAmount=(poUnitPrice)*(poQty);
						if(orderItem.itemDescription)
							productName=orderItem.itemDescription;
	
						tempData.put("poQty",poQty);
						tempData.put("poUnitPrice", poUnitPrice);
						tempData.put("poAmount",poAmount);
						//tempData.put("poUnitPrice", poUnitPrice.setScale(2, rounding));
						//tempData.put("poAmount",poAmount.setScale(2, rounding));
						tempData.put("weaverName", partyName);
						tempData.put("poNo", poId);
						tempData.put("poSquenceNo", poSquenceNo);
						tempData.put("supplierName", supplierPartyName);
						tempData.put("orderNo", orderNo);
						tempData.put("productName",productName);
						orderHeader = delegator.findOne("OrderHeader",[orderId : poId] , false);
						if(orderHeader){
							tempData.put("poDate", UtilDateTime.toDateString(orderHeader.orderDate, "dd/MM/yyyy"));
						}
						
						if(eachHeader.externalId)
						tempData.put("indentRefNo", eachHeader.externalId);
					   else
						tempData.put("indentRefNo", "");
						orderList.add(tempData);
					}
					
					
				}
			}		
			
		}
		
		
	}
	
	
	
	context.orderList=orderList;
	
	