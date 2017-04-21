import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.SortedMap;
import org.ofbiz.party.party.PartyHelper;
import javolution.util.FastMap;
import javolution.util.FastList;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;
import java.util.Map.Entry;



//branchId=parameters.branchId;
taxType=parameters.taxType;
purposeTypeId=parameters.purposeTypeId;
partyfromDate=parameters.partyfromDate;
partythruDate=parameters.partythruDate;
partyId=parameters.partyId;
state=parameters.state;
context.taxType=taxType;
SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
Timestamp fromDate;
Timestamp thruDate;
 
DateList=[];
DateMap = [:];
DateMap.put("partyfromDate", partyfromDate);
DateMap.put("partythruDate", partythruDate);
DateList.add(DateMap);
context.DateList=DateList;

result = dispatcher.runSync("getRegionalAndBranchOfficesByState",UtilMisc.toMap("state",state,"userLogin",userLogin));
stateBranchsList=result.get("stateBranchsList");
branchList=[];
if(stateBranchsList){
	for(eachBranch in stateBranchsList){
		branchList.add(eachBranch.partyId);
	}
}
/*branchName = "";
if(branchId){
	branch = delegator.findOne("PartyGroup",[partyId : branchId] , false);
	branchName = branch.get("groupName");
	DateMap.put("branchName", branchName);
}
branchList = [];
branchIdForAdd="";
condListb = [];
if(branchId){
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
if(!branchList){
	if(branchId)
	branchList.add(branchId);
}*/
daystart = null;
dayend = null;
if(UtilValidate.isNotEmpty(parameters.partyfromDate)){
	try {
		fromDate = new java.sql.Timestamp(sdf.parse(parameters.partyfromDate).getTime());
		daystart = UtilDateTime.getDayStart(fromDate);
	} catch (ParseException e) {
	}
}
if(UtilValidate.isNotEmpty(parameters.partythruDate)){
   try {
	   thruDate = new java.sql.Timestamp(sdf.parse(parameters.partythruDate).getTime());
	   dayend = UtilDateTime.getDayEnd(thruDate);
   } catch (ParseException e) {
   }
}
if(UtilValidate.isEmpty(parameters.partyfromDate)){
	try {
		fromDate = UtilDateTime.nowTimestamp()
		daystart = UtilDateTime.getDayStart(fromDate);
	} catch (ParseException e) {
	}
}
if(UtilValidate.isEmpty(parameters.partythruDate)){
	try {
		thruDate = UtilDateTime.nowTimestamp()
		dayend = UtilDateTime.getDayEnd(thruDate);
	} catch (ParseException e) {
	}
}
context.daystart = daystart;
context.dayend = dayend;

daystart = UtilDateTime.getDayStart(fromDate);
dayend = UtilDateTime.getDayEnd(thruDate);
branchIdForAdd="";
BOAddress="";
BOEmail="";
if(branchIdForAdd){
	branchContext=[:];
	branchContext.put("branchId",branchIdForAdd);
	try{
		resultCtx = dispatcher.runSync("getBoHeader", branchContext);
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
finalMap=[:];
conditionList = [];
conditionList.add(EntityCondition.makeCondition("componentType", EntityOperator.IN,["EXCISE_DUTY","CST_PUR","VAT_PUR","ENTRY_TAX"]));
condListb = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
taxPurList = delegator.findList("OrderTaxTypeComponentMap", condListb,null, null, null, false);

if(taxType=="VAT_SALE"){
	taxPercentageList= EntityUtil.filterByCondition(taxPurList, EntityCondition.makeCondition("componentType", EntityOperator.EQUALS,"VAT_PUR"));
	taxPercentageList=taxPercentageList.componentRate;
	//taxPercentageList=["4","5","5.5","14.5"];
}
else if(taxType=="CST_SALE"){
	taxPercentageList= EntityUtil.filterByCondition(taxPurList, EntityCondition.makeCondition("componentType", EntityOperator.EQUALS,"CST_PUR"));
	taxPercentageList=taxPercentageList.componentRate;
	//taxPercentageList=["2","12.5"];
}
else if(taxType=="EXCISE_DUTY"){
	taxPercentageList= EntityUtil.filterByCondition(taxPurList, EntityCondition.makeCondition("componentType", EntityOperator.EQUALS,"EXCISE_DUTY"));
	taxPercentageList=taxPercentageList.componentRate;
	//taxPercentageList=["12.5"];
	//Debug.log("taxPercentageList=========="+taxPercentageList);
}
else if(taxType=="ENTRY_TAX"){
	taxPercentageList= EntityUtil.filterByCondition(taxPurList, EntityCondition.makeCondition("componentType", EntityOperator.EQUALS,"ENTRY_TAX"));
	taxPercentageList=taxPercentageList.componentRate;
	//taxPercentageList=["1"];
}
if(branchList){
	for(eachTaxPer in taxPercentageList){
		branchWiseMap=[:];
		for(eachBranch in branchList){
			invioceItemsList=[];
			findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
			condList = [];
			if(UtilValidate.isNotEmpty(daystart)){
				condList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, daystart));
				condList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, dayend));
			}
			condList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "SALES_INVOICE"));
			/*if(branchList){
				condList.add(EntityCondition.makeCondition("costCenterId", EntityOperator.IN,branchList));
			}*/
			if(eachBranch){
				condList.add(EntityCondition.makeCondition("costCenterId", EntityOperator.EQUALS,eachBranch));
			}
			condList.add(EntityCondition.makeCondition("sourcePercentage", EntityOperator.EQUALS, new BigDecimal(eachTaxPer)));
			condList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
			if(purposeTypeId=="YARN_SALE"){
			condList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.IN, ["YARN_SALE","DEPOT_YARN_SALE"]));
			}
			else if(purposeTypeId=="DIES_AND_CHEM_SALE"){
				condList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.IN, ["DIES_AND_CHEM_SALE","DEPOT_DIES_CHEM_SALE"]));
			}
			else{
				condList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.IN, ["YARN_SALE","DEPOT_YARN_SALE","DIES_AND_CHEM_SALE","DEPOT_DIES_CHEM_SALE"]));
			}
			condList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.IN,["INV_FPROD_ITEM",taxType]));
			cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
			//Debug.log("cond=========#########======="+cond);
			fieldsToSelect = ["invoiceId","invoiceTypeId","partyId","invoiceItemTypeId","itemValue","sourcePercentage","shipmentId","invoiceDate","parentInvoiceItemSeqId","purposeTypeId"] as Set;
			invoiceIterator = delegator.find("InvoiceAndItem", cond, null, fieldsToSelect, null, findOpts);
			//invoiceIds=EntityUtil.getFieldListFromEntityListIterator(invoice, "invoiceId", true);
			partyId="";
			invItemsList=[];
			while (eachInvoice = invoiceIterator.next()) {
				invoiceValue=0;
				SURCHARGE=0;
				baseValue=0;
				totalAmt=0;
				taxValue=0;
				taxPercentage=0;
				taxSurChgPer=0;
				conditionList=[];
				conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS,eachInvoice.invoiceId));
				conditionList.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS,eachInvoice.parentInvoiceItemSeqId));
				invoiceItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, null, null, false );
				
				invItemsWithCstSur="";
				invItemsWithVatSur="";
				for(eachInvoiceItem in invoiceItems){
					invoiceDetailMap=[:];
					invoiceId=eachInvoiceItem.invoiceId;
					//Debug.log("eachInvoiceItem============="+eachInvoiceItem);
					invoiceDetailMap.put("invoiceId", eachInvoiceItem.invoiceId);
					if(UtilValidate.isNotEmpty(eachInvoiceItem)){
						partyId = eachInvoiceItem.partyId;
						if(partyId){
							conditionList.clear();
							conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
							conditionList.add(EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "TIN_NUMBER"));
							cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
							PartyIdentificationList = delegator.findList("PartyIdentification", cond, null, null, null, false);
							if(PartyIdentificationList && UtilValidate.isNotEmpty(PartyIdentificationList[0].get("idValue"))){
								TinNo = PartyIdentificationList[0].get("idValue");
								invoiceDetailMap.put("partyTinNo", TinNo);
							}
						}
						
						invoiceDetailMap.put("partyId", partyId);
					}
					partyIdName="";
					if(partyId){
						partyIdName = PartyHelper.getPartyName(delegator, partyId, false);
					}
					invoiceDetailMap.put("partyIdName", partyIdName);
					if(eachInvoiceItem.invoiceDate){
						invoiceDate=UtilDateTime.toDateString(eachInvoiceItem.invoiceDate, "dd/MM/yyyy");
						invoiceDetailMap.put("invoiceDate", invoiceDate);
					}
					
					if((eachInvoiceItem.purposeTypeId=="DEPOT_YARN_SALE") || (eachInvoiceItem.purposeTypeId=="DEPOT_DIES_CHEM_SALE")){
						orderItemBillings = delegator.findList("OrderItemBilling", EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId), null, null, null, false);
						orderItemBillings = EntityUtil.getFirst(orderItemBillings);
					
						orderId = orderItemBillings.orderId;
						context.orderId = orderId;
						
						conditionList = [];
						conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId));
						conditionList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS , "ORDRITEM_INVENTORY_ID"));
						cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
						OrderItemAttribute = delegator.findList("OrderItemAttribute",  cond,null, null, null, false );
						
						inventoryItemId = "";
						if(OrderItemAttribute){
							inventoryItemId = EntityUtil.getFirst(OrderItemAttribute).attrValue;
						}
						
						conditionList.clear();
						conditionList.add(EntityCondition.makeCondition("inventoryItemId", EntityOperator.EQUALS , inventoryItemId));
						cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
						ShipmentReceipt = delegator.findList("ShipmentReceipt",  cond,null, null, null, false );
						
						if(ShipmentReceipt){
							shipmentId = EntityUtil.getFirst(ShipmentReceipt).shipmentId;
						}
					}
					else if(((eachInvoiceItem.purposeTypeId=="YARN_SALE") || (eachInvoiceItem.purposeTypeId=="DIES_AND_CHEM_SALE")) && eachInvoiceItem.shipmentId){
						shipmentId=eachInvoiceItem.shipmentId;
					}
					if(shipmentId){
						shipmentList = delegator.findOne("Shipment",[shipmentId : shipmentId] , false);
					}
					buyerInvoiceId="";
					if(shipmentList){
						if(shipmentList.supplierInvoiceId){
							buyerInvoiceId=shipmentList.supplierInvoiceId;
						}
					}
						invoiceDetailMap.put("buyerInvId", buyerInvoiceId);
					
					if(eachInvoiceItem.productId){
						productId=eachInvoiceItem.productId;
						invoiceDetailMap.put("productId", productId);
					}
					conditionList=[];
					conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS,eachInvoiceItem.invoiceId));
					conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS,eachInvoiceItem.invoiceItemSeqId));
					invoiceItemsList = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, null, null, false );
					
					invoiceItemsWithTax= EntityUtil.filterByCondition(invoiceItemsList, EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS,taxType));
					if(invoiceItemsWithTax)
						invoiceItemsWithTax=EntityUtil.getFirst(invoiceItemsWithTax);
					invoiceItemsWithCstSur= EntityUtil.filterByCondition(invoiceItemsList, EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS,"VAT_SURCHARGE"));
					if(invoiceItemsWithCstSur)
						invItemsWithCstSur=EntityUtil.getFirst(invoiceItemsWithCstSur);
					invoiceItemsWithVatSur= EntityUtil.filterByCondition(invoiceItemsList, EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS,"CST_SURCHARGE"));
					if(invoiceItemsWithVatSur)
						invItemsWithVatSur=EntityUtil.getFirst(invoiceItemsWithVatSur);
					
						taxValue= invoiceItemsWithTax.itemValue;
						taxPercentage=invoiceItemsWithTax.sourcePercentage;
					
					if(UtilValidate.isNotEmpty(invItemsWithCstSur)){
						SURCHARGE=invItemsWithCstSur.itemValue;
						taxSurChgPer=invItemsWithCstSur.sourcePercentage;
					}
					else if(UtilValidate.isNotEmpty(invItemsWithVatSur)){
						SURCHARGE=invItemsWithVatSur.itemValue;
						taxSurChgPer=invItemsWithVatSur.sourcePercentage;
					}
					baseValue=eachInvoiceItem.itemValue;
					invoiceDetailMap.put("baseValue", baseValue);
					invoiceDetailMap.put("taxValue", taxValue);
					invoiceDetailMap.put("taxPercentage", taxPercentage);
					invoiceDetailMap.put("taxSurChargeValue", SURCHARGE);
					invoiceDetailMap.put("taxSurChgPer", taxSurChgPer);
					totalAmt=invoiceDetailMap["taxValue"]+invoiceDetailMap["taxSurChargeValue"]+baseValue;
					invoiceDetailMap.put("total", totalAmt);
					
					invItemsList.add(invoiceDetailMap);
				}
				
			}
			if(invItemsList){
				branchWiseMap.put(eachBranch, invItemsList);
			}
	   }
			if(branchWiseMap){
				finalMap.put(eachTaxPer, branchWiseMap);
			}
	}
	invoiceIterator.close();
}


context.finalMap=finalMap;



