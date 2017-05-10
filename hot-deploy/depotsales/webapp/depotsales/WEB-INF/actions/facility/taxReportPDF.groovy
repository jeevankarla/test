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



branchId=parameters.branchId3;
taxType=parameters.taxType;
purposeTypeId=parameters.purposeTypeId;
partyfromDate=parameters.partyfromDate;
partythruDate=parameters.partythruDate;
partyId=parameters.partyId;
state=parameters.state;
searchType=parameters.searchType;
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
branchIdForAdd="";
if(searchType=="BY_STATE"){
	if(!state){
		conditionList=[];
		conditionList.add(EntityCondition.makeCondition("geoId", EntityOperator.LIKE,"IN-%"));
		conditionList.add(EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS,"STATE"));
		statesList = delegator.findList("Geo",EntityCondition.makeCondition(conditionList,EntityOperator.AND),null,null,null,false);
		indianStates = EntityUtil.getFieldListFromEntityList(statesList, "geoId", true);
		conditionList.clear();
	}else{
		conditionList=[];
		conditionList.add(EntityCondition.makeCondition("geoId", EntityOperator.EQUALS,state));
		conditionList.add(EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS,"STATE"));
		statesList = delegator.findList("Geo",EntityCondition.makeCondition(conditionList,EntityOperator.AND),null,null,null,false);
		indianStates = EntityUtil.getFieldListFromEntityList(statesList, "geoId", true);
	}
	branchList=[];
	for(eachState in indianStates){
		result = dispatcher.runSync("getRegionalAndBranchOfficesByState",UtilMisc.toMap("state",eachState,"userLogin",userLogin));
		stateBranchsList=result.get("stateBranchsList");
		
		for(eachBranch in stateBranchsList){
			branchList.add(eachBranch.partyId);
		}
	}
}
else if(searchType=="BY_BO_RO"){
	if(branchId){
		branchIdForAdd=branchId;
		branchName = "";
			branch = delegator.findOne("PartyGroup",[partyId : branchId] , false);
			branchName = branch.get("groupName");
			DateMap.put("branchName", branchName);
			branchList = [];
			condListb = [];
				condListb.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, branchId));
				condListb.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
				condListb = EntityCondition.makeCondition(condListb, EntityOperator.AND);
				PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdTo"), null, null, false);
				branchList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);
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
			if(!branchList){
				if(branchId)
				branchList.add(branchId);
			}
	}
	else{
		List<GenericValue> partyClassificationList = null;
		partyClassificationList = delegator.findList("PartyClassification", EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.IN, UtilMisc.toList("BRANCH_OFFICE")), UtilMisc.toSet("partyId"), null, null,false);
		if(partyClassificationList){
			branchList=[];
			for (eachList in partyClassificationList) {
				partyName = PartyHelper.getPartyName(delegator, eachList.get("partyId"), false);
				branchList.addAll(eachList.get("partyId"));
			}
		}
	}

}
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

fromDateForFtl=UtilDateTime.toDateString(daystart, "dd/MM/yyyy");
thruDateForFtl=UtilDateTime.toDateString(dayend, "dd/MM/yyyy");

context.fromDateForFtl=fromDateForFtl;
context.thruDateForFtl=thruDateForFtl;

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
conditionList.add(EntityCondition.makeCondition("componentType", EntityOperator.IN,["ENTRY_TAX","CST_PUR","VAT_PUR","EXCISE_DUTY"]));
condListb = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
taxPurList = delegator.findList("OrderTaxTypeComponentMap", condListb,null, null, null, false);
if(taxType=="VAT_PUR"){
	taxPercentageList= EntityUtil.filterByCondition(taxPurList, EntityCondition.makeCondition("componentType", EntityOperator.EQUALS,"VAT_PUR"));
	taxPercentageList=taxPercentageList.componentRate;
}
else if(taxType=="CST_PUR"){
	taxPercentageList= EntityUtil.filterByCondition(taxPurList, EntityCondition.makeCondition("componentType", EntityOperator.EQUALS,"CST_PUR"));
	taxPercentageList=taxPercentageList.componentRate;
}
else if(taxType=="EXCISE_DUTY"){
	taxPercentageList= EntityUtil.filterByCondition(taxPurList, EntityCondition.makeCondition("componentType", EntityOperator.EQUALS,"EXCISE_DUTY"));
	taxPercentageList=taxPercentageList.componentRate;
}
else if(taxType=="ENTRY_TAX"){
	taxPercentageList= EntityUtil.filterByCondition(taxPurList, EntityCondition.makeCondition("componentType", EntityOperator.EQUALS,"ENTRY_TAX"));
	taxPercentageList=taxPercentageList.componentRate;
}

totalBaseValue=0;
totalTaxValue=0
totalSurChrgValue=0
totalValue=0;
if(branchList){
	
	for(eachTaxPer in taxPercentageList){
		branchWiseMap=[:];
		for(eachBranch in branchList){
			if(taxType=="VAT_PUR" || taxType=="CST_PUR"){
				
				if(eachTaxPer == 0){
					condList=[];
					condList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, daystart));
					condList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, dayend));
					condList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "PURCHASE_INVOICE"));
					if(eachBranch){
						condList.add(EntityCondition.makeCondition("costCenterId", EntityOperator.EQUALS,eachBranch));
					}
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
					condList.add(EntityCondition.makeCondition("invoiceAttrName", EntityOperator.EQUALS,"purchaseTitleTransferEnumId"));
					if(taxType=="VAT_PUR"){
						condList.add(EntityCondition.makeCondition("invoiceAttrValue", EntityOperator.EQUALS,"NO_E2_FORM"));
					}
					else{
						condList.add(EntityCondition.makeCondition("invoiceAttrValue", EntityOperator.NOT_IN,["NO_E2_FORM","EXEMPTED_GOODS"]));
					}
					cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
					invWithAllPer = delegator.findList("InvoiceAndAttribute",EntityCondition.makeCondition(condList,EntityOperator.AND), null, null, null, false );
					invIds=EntityUtil.getFieldListFromEntityList(invWithAllPer, "invoiceId", true);
					condList.clear();
					condList=[];
					condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN,invIds));
					condList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS,taxType));
					invWithZeroPer = delegator.findList("InvoiceItem",EntityCondition.makeCondition(condList,EntityOperator.AND), null, null, null, false );
					
					invoiceIds=EntityUtil.getFieldListFromEntityList(invWithZeroPer, "invoiceId", true);
					invWithoutTaxType= EntityUtil.filterByCondition(invWithAllPer, EntityCondition.makeCondition("invoiceId", EntityOperator.NOT_IN,invoiceIds));
					
					invoiceIds=EntityUtil.getFieldListFromEntityList(invWithoutTaxType, "invoiceId", true);
				  }
			}
			
		
			invioceItemsList=[];
			findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
			condList = [];
			if((taxType=="VAT_PUR" || taxType=="CST_PUR") && (eachTaxPer == 0)){
				condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIds));
			}
			if(UtilValidate.isNotEmpty(daystart)){
				condList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, daystart));
				condList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, dayend));
			}
			condList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "PURCHASE_INVOICE"));
			/*if(branchList){
				condList.add(EntityCondition.makeCondition("costCenterId", EntityOperator.IN,branchList));
			}*/
			if(eachBranch){
				condList.add(EntityCondition.makeCondition("costCenterId", EntityOperator.EQUALS,eachBranch));
			}
			if(eachTaxPer != 0){
				condList.add(EntityCondition.makeCondition("sourcePercentage", EntityOperator.EQUALS, new BigDecimal(eachTaxPer)));
			}
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
			if(eachTaxPer != 0){
				condList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.IN,["INV_RAWPROD_ITEM",taxType]));
			}
			else{
				condList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS,"INV_RAWPROD_ITEM"));
			}
			cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
			fieldsToSelect = ["invoiceId","invoiceTypeId","partyIdFrom","invoiceItemTypeId","itemValue","sourcePercentage","shipmentId","invoiceDate","parentInvoiceItemSeqId","invoiceItemSeqId"] as Set;
			invoiceIterator = delegator.find("InvoiceAndItem", cond, null, fieldsToSelect, null, findOpts);
			//invoiceIds=EntityUtil.getFieldListFromEntityListIterator(invoice, "invoiceId", true);
			partyIdFrom="";
			invItemsList=[];
			while (eachInvoice = invoiceIterator.next()) {
				invoiceValue=0;
				surcharge=0;
				baseValue=0;
				totalAmt=0;
				taxValue=0;
				taxPercentage=0;
				taxSurChgPer=0;
				conditionList=[];
				conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS,eachInvoice.invoiceId));
				if(eachTaxPer == 0){
				condList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS,"INV_RAWPROD_ITEM"));
				}
				
				if(eachTaxPer != 0){
				conditionList.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS,eachInvoice.parentInvoiceItemSeqId));
				}
				else{
					conditionList.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS,eachInvoice.invoiceItemSeqId));
				}
				invoiceItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, null, null, false );
				invItemsWithCstSur="";
				invItemsWithVatSur="";
				for(eachInvoiceItem in invoiceItems){
					invoiceDetailMap=[:];
					invoiceId=eachInvoiceItem.invoiceId;
					invoiceDetailMap.put("invoiceId", eachInvoiceItem.invoiceId);
					if(UtilValidate.isNotEmpty(eachInvoiceItem)){
						partyIdFrom = eachInvoiceItem.partyIdFrom;
						if(partyIdFrom){
							conditionList.clear();
							conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyIdFrom));
							conditionList.add(EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "TIN_NUMBER"));
							cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
							PartyIdentificationList = delegator.findList("PartyIdentification", cond, null, null, null, false);
							if(PartyIdentificationList && UtilValidate.isNotEmpty(PartyIdentificationList[0].get("idValue"))){
								TinNo = PartyIdentificationList[0].get("idValue");
								invoiceDetailMap.put("partyTinNo", TinNo);
							}
						}
						
						invoiceDetailMap.put("partyIdFrom", partyIdFrom);
					}
					partyIdFromName="";
					if(partyIdFrom){
						partyIdFromName = PartyHelper.getPartyName(delegator, partyIdFrom, false);
					}
					invoiceDetailMap.put("partyIdFromName", partyIdFromName);
					if(eachInvoiceItem.invoiceDate){
						invoiceDate=UtilDateTime.toDateString(eachInvoiceItem.invoiceDate, "dd/MM/yyyy");
						invoiceDetailMap.put("invoiceDate", invoiceDate);
					}
					if(eachInvoiceItem.shipmentId){
						shipmentId=eachInvoiceItem.shipmentId;
						shipmentList = delegator.findOne("Shipment",[shipmentId : shipmentId] , false);
					}
					supplierInvoiceId="";
					if(shipmentList){
						if(shipmentList.supplierInvoiceId){
							supplierInvoiceId=shipmentList.supplierInvoiceId;	
						}
					}
						invoiceDetailMap.put("supplierInvId", supplierInvoiceId);
					
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
					if(invoiceItemsWithTax.itemValue)
						taxValue= invoiceItemsWithTax.itemValue;
					if(invoiceItemsWithTax.sourcePercentage)
						taxPercentage=invoiceItemsWithTax.sourcePercentage;
						
					if((taxValue) && (taxType=="VAT_PUR" || taxType=="CST_PUR")){
						invoiceItemsWithCstSur= EntityUtil.filterByCondition(invoiceItemsList, EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS,"VAT_SURCHARGE"));
						if(invoiceItemsWithCstSur)
							invItemsWithCstSur=EntityUtil.getFirst(invoiceItemsWithCstSur);
						invoiceItemsWithVatSur= EntityUtil.filterByCondition(invoiceItemsList, EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS,"CST_SURCHARGE"));
						if(invoiceItemsWithVatSur)
							invItemsWithVatSur=EntityUtil.getFirst(invoiceItemsWithVatSur);
						
						if(UtilValidate.isNotEmpty(invItemsWithCstSur)){
							surcharge=invItemsWithCstSur.itemValue;
							taxSurChgPer=invItemsWithCstSur.sourcePercentage;
						}
						else if(UtilValidate.isNotEmpty(invItemsWithVatSur)){		
							surcharge=invItemsWithVatSur.itemValue;
							taxSurChgPer=invItemsWithVatSur.sourcePercentage;
						}
					}
					baseValue=eachInvoiceItem.itemValue;
					totalBaseValue=totalBaseValue+baseValue;
					totalTaxValue=totalTaxValue+taxValue;
					totalSurChrgValue=totalSurChrgValue+surcharge;
					
					invoiceDetailMap.put("baseValue", baseValue);
					invoiceDetailMap.put("taxValue", taxValue);
					invoiceDetailMap.put("taxPercentage", taxPercentage);
					if(taxType=="VAT_PUR" || taxType=="CST_PUR"){
						invoiceDetailMap.put("taxSurChargeValue", surcharge);
						invoiceDetailMap.put("taxSurChgPer", taxSurChgPer);
					}
					//totalAmt=invoiceDetailMap["taxValue"]+invoiceDetailMap["taxSurChargeValue"]+baseValue;
					totalAmt=taxValue+surcharge+baseValue;
					totalValue=totalValue+totalAmt;
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
	
}

context.finalMap=finalMap;

finalList=[];

stylesMap=[:];

stylesMap.put("mainHeader1", "NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD.");
stylesMap.put("mainHeader2", "Purchase Tax Report");
stylesMap.put("mainHeader3", "From  "+fromDateForFtl +" to "+thruDateForFtl);
stylesMap.put("mainHeaderFontName","Arial");
stylesMap.put("mainHeadercellHeight",300);
stylesMap.put("mainHeaderFontSize",10);
stylesMap.put("mainHeadingCell",1);
stylesMap.put("mainHeaderBold",true);
stylesMap.put("columnHeaderBgColor",false);
stylesMap.put("columnHeaderFontName","Arial");
stylesMap.put("columnHeaderFontSize",10);
stylesMap.put("autoSizeCell",true);
stylesMap.put("columnHeaderCellHeight",300);
request.setAttribute("stylesMap", stylesMap);
request.setAttribute("enableStyles", true);
finalList.add(stylesMap);

headingMap=[:];
headingMap.put("partyTinNo", "Buyer Tin No");
headingMap.put("partyIdFromName", "Name of Buyer");
headingMap.put("supplierInvId", "Invoice No");
headingMap.put("invoiceDate", "Invoice Date");
headingMap.put("productId", "Commodity Code");
headingMap.put("baseValue", "Purchase Value");
if(taxType=="VAT_PUR"){
	headingMap.put("taxValue", "Vat Amount");
	headingMap.put("taxPercentage", "Vat Percentage");
}
else if(taxType=="CST_PUR"){
	headingMap.put("taxValue", "Cst Amount");
	headingMap.put("taxPercentage", "Cst Percentage");
}
else if(taxType=="EXCISE_DUTY"){
	headingMap.put("taxValue", "Excise Amount");
	headingMap.put("taxPercentage", "Excise Percentage");
}
else if(taxType=="ENTRY_TAX"){
	headingMap.put("taxValue", "Entry Tax Amount");
	headingMap.put("taxPercentage", "Entry Tax Percentage");
}
if(taxType=="VAT_PUR" || taxType=="CST_PUR"){
	if(taxType=="VAT_PUR"){
		headingMap.put("taxSurChargeValue", "Vat Surcharge Amount");
		headingMap.put("taxSurChgPer", "Vat Surcharge Percentage");
	}
	else if(taxType=="CST_PUR"){
		headingMap.put("taxSurChargeValue", "Cst Surcharge Amount");
		headingMap.put("taxSurChgPer", "Cst Surcharge Percentage");
	}
}
headingMap.put("total", "Total Amount");

finalList.add(headingMap);

finalMapEntryList = finalMap.entrySet();
//Debug.log("finalMapEntryList====================="+finalMapEntryList);
for(eachEntry in finalMapEntryList){
	taxPer=eachEntry.getKey();
	branchWiseList=eachEntry.getValue();
	for(eachBranch in branchWiseList){
		invList= eachBranch.getKey();
		invoiceWiseList = eachBranch.getValue();
		for(eachValue in invoiceWiseList){
			//Debug.log("eachValue====================="+eachValue);
			finalList.add(eachValue);
		}
		
	}
	
}

tempToMap=[:];
tempToMap.put("partyTinNo", "Total");
tempToMap.put("baseValue", totalBaseValue);
tempToMap.put("taxValue", totalTaxValue);
if(taxType=="VAT_PUR" || taxType=="CST_PUR"){
	tempToMap.put("taxSurChargeValue", totalSurChrgValue);
}
tempToMap.put("total", totalValue);

finalList.add(tempToMap);


context.finalList=finalList;


