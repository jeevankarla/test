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


branchId=parameters.branchId;
purposeTypeId=parameters.purposeTypeId;
partyfromDate=parameters.partyfromDate;
partythruDate=parameters.partythruDate;
state=parameters.geoId;


SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
Timestamp fromDate;
Timestamp thruDate;
DateList=[];
DateMap = [:];
DateMap.put("partyfromDate", partyfromDate);
DateMap.put("partythruDate", partythruDate);
DateList.add(DateMap);
context.DateList=DateList;

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



branchList=[];
if(UtilValidate.isNotEmpty(state)){
	conditionList=[];
	//conditionList.add(EntityCondition.makeCondition("geoId", EntityOperator.LIKE,"IN-%"));
	if("ALL".equals(state)){
		conditionList.add(EntityCondition.makeCondition("geoId",EntityOperator.LIKE,"IN-%"));
	}else{
		conditionList.add(EntityCondition.makeCondition("geoId",EntityOperator.EQUALS,state));
	}
	conditionList.add(EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS,"STATE"));
	statesList = delegator.findList("Geo",EntityCondition.makeCondition(conditionList,EntityOperator.AND),null,null,null,false);
	indianStates = EntityUtil.getFieldListFromEntityList(statesList, "geoId", true);

branchList=[];
for(eachState in indianStates){
	result = dispatcher.runSync("getRegionalAndBranchOfficesByState",UtilMisc.toMap("state",eachState,"userLogin",userLogin));
	stateBranchsList=result.get("stateBranchsList");
	
	for(eachBranch in stateBranchsList){
		branchList.add(eachBranch.partyId);
	}
}
}

conditionList = [];
conditionList.add(EntityCondition.makeCondition("partyClassificationGroupId",EntityOperator.EQUALS,"REGIONAL_OFFICE"));
partyClassification = delegator.findList("PartyClassification",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("partyId"), null, null, false );
roPartIds = EntityUtil.getFieldListFromEntityList(partyClassification, "partyId", true);
if(branchId){
if(branchId=="ALL"){
	roPartIds.each{ eachRo ->
			resultCtx = dispatcher.runSync("getRoBranchList",UtilMisc.toMap("userLogin",userLogin,"productStoreId",eachRo));
			if(resultCtx && resultCtx.get("partyList")){
				partyList=resultCtx.get("partyList");
				partyList.each{eachparty ->
					branchList.add(eachparty.partyIdTo);
				}
			}
			branchList.add(eachRo);
	}
}
else{
	resultCtx = dispatcher.runSync("getRoBranchList",UtilMisc.toMap("userLogin",userLogin,"productStoreId",branchId));
	if(resultCtx && resultCtx.get("partyList")){
		partyList=resultCtx.get("partyList");
		branchList= EntityUtil.getFieldListFromEntityList(partyList,"partyIdTo", true);
	}
	branchList.add(branchId);
}
}


finalList=[];



stylesMap=[:];   //stylesMap
stylesMap.put("mainHeader1", "NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD");  //mainHeader
stylesMap.put("mainHeadercellHeight",300);  //mainHeadercellHeight
stylesMap.put("mainHeaderfontName","Arial");  //URW Chancery L
stylesMap.put("mainHeaderFontSize",10);
stylesMap.put("mainHeadingCell",0);
stylesMap.put("mainHeaderBold",true);
stylesMap.put("columnHeaderBgColor",false);  //column_header
stylesMap.put("columnHeaderFontName","Arial");
stylesMap.put("columnHeaderFontSize",10);
stylesMap.put("autoSizeCell",true);
stylesMap.put("columnHeaderCellHeight",300);//columnHeaderCellHeight

request.setAttribute("stylesMap", stylesMap);
request.setAttribute("enableStyles", true);
finalList.add(stylesMap);

headingMap=[:];
headingMap.put("taxType", "Tax Type");
headingMap.put("baseValue", "Assessable Value");
headingMap.put("taxValue", "Tax Amount");
finalList.add(headingMap);

headingMap1=[:];
headingMap1.put("taxType", "Sales");
headingMap1.put("baseValue", " ");
headingMap1.put("taxValue", " ");
finalList.add(headingMap1);

/*headingMap2=[:];
headingMap2.put("taxType", "Output Tax");
headingMap2.put("baseValue", " ");
headingMap2.put("taxValue", " ");
finalList.add(headingMap2);*/


conditionList = [];
conditionList.add(EntityCondition.makeCondition("componentType", EntityOperator.IN,["CST_PUR","VAT_PUR"]));
condListb = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
taxPurList = delegator.findList("OrderTaxTypeComponentMap", condListb,null, null, null, false);



taxList=[["CST_SALE","VAT_SALE"],["CST_PUR","VAT_PUR"]];

if(branchList){
	for(eachTaxList in taxList){
		tempToMap=[:];
		tempMap=[:];
		totTempMap=[:];
		totalOutputBaseVal=0;
		totalOutputTaxVal=0;
		for(eachTax in eachTaxList){
			totalBaseVal=0;
			totalTaxVal=0;
			if(eachTax=="VAT_SALE" || eachTax=="VAT_PUR"){
				taxPercentageList= EntityUtil.filterByCondition(taxPurList, EntityCondition.makeCondition("componentType", EntityOperator.EQUALS,"VAT_PUR"));
				taxPercentageList=taxPercentageList.componentRate;
			}
			else if(eachTax=="CST_SALE" || "CST_PUR"){
				taxPercentageList= EntityUtil.filterByCondition(taxPurList, EntityCondition.makeCondition("componentType", EntityOperator.EQUALS,"CST_PUR"));
				taxPercentageList=taxPercentageList.componentRate;
			}
			
			for(eachTaxPer in taxPercentageList){
				//Debug.log("eachTax============"+eachTax);
				//Debug.log("eachTaxPer============"+eachTaxPer);
				
				if(eachTaxPer == 0){
					condList=[];
					condList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, daystart));
					condList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, dayend));
					if(eachTax=="VAT_SALE" || eachTax=="CST_SALE"){
					condList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, ["SALES_INVOICE"]));
					}
					else if(eachTax=="VAT_PUR" || eachTax=="CST_PUR"){
						condList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, ["PURCHASE_INVOICE"]));
					}
					condList.add(EntityCondition.makeCondition("costCenterId", EntityOperator.IN,branchList));
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
					if(eachTax=="VAT_SALE" || eachTax=="CST_SALE"){
					condList.add(EntityCondition.makeCondition("invoiceAttrName", EntityOperator.EQUALS,"saleTaxType"));
					}
					if(eachTax=="VAT_PUR" || eachTax=="CST_PUR"){
						condList.add(EntityCondition.makeCondition("invoiceAttrName",  EntityOperator.EQUALS,"purchaseTaxType"));
					}
					if(eachTax=="VAT_SALE" || eachTax=="VAT_PUR"){
						condList.add(EntityCondition.makeCondition("invoiceAttrValue", EntityOperator.EQUALS,"Intra-State"));
					}
					else if(eachTax=="CST_SALE" || eachTax=="CST_PUR"){
						condList.add(EntityCondition.makeCondition("invoiceAttrValue", EntityOperator.EQUALS,"Inter-State"));
					}
					cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
					invWithAllPer = delegator.findList("InvoiceAndAttribute",EntityCondition.makeCondition(condList,EntityOperator.AND), null, null, null, false );
					invIds=EntityUtil.getFieldListFromEntityList(invWithAllPer, "invoiceId", true);
					condList.clear();
						condList=[];
						condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN,invIds));
						if(eachTax=="CST_SALE" || eachTax=="CST_PUR"){
						condList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.IN,["CST_SALE","CST_PUR"]));
						}
						else{
							condList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.IN,["VAT_SALE","VAT_PUR"]));
						}
						invWithZeroPer = delegator.findList("InvoiceItem",EntityCondition.makeCondition(condList,EntityOperator.AND), null, null, null, false );
						
						invoiceIds=EntityUtil.getFieldListFromEntityList(invWithZeroPer, "invoiceId", true);
						invWithoutTaxType= EntityUtil.filterByCondition(invWithAllPer, EntityCondition.makeCondition("invoiceId", EntityOperator.NOT_IN,invoiceIds));
						
						invoiceIds=EntityUtil.getFieldListFromEntityList(invWithoutTaxType, "invoiceId", true);
				}
				invioceItemsList=[];
				findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
				condList = [];
				
				if(eachTaxPer == 0){
					condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIds));
				}
				if(UtilValidate.isNotEmpty(daystart)){
					condList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, daystart));
					condList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, dayend));
				}
				if(eachTax=="VAT_SALE" || eachTax=="CST_SALE"){
					condList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, ["SALES_INVOICE"]));
				}
				else if(eachTax=="VAT_PUR" || eachTax=="CST_PUR"){
					condList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, ["PURCHASE_INVOICE"]));
				}
				if(branchList){
					condList.add(EntityCondition.makeCondition("costCenterId", EntityOperator.IN,branchList));
				}
				if(eachTaxPer != 0)
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
				if(eachTaxPer != 0){
				condList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.IN,["INV_FPROD_ITEM","INV_RAWPROD_ITEM",eachTax]));
				}
				else{
					condList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.IN,["INV_FPROD_ITEM","INV_RAWPROD_ITEM"]));
				}
				cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
				fieldsToSelect = ["invoiceId","invoiceTypeId","partyIdFrom","invoiceItemTypeId","itemValue","sourcePercentage","shipmentId","invoiceDate","parentInvoiceItemSeqId","invoiceItemSeqId"] as Set;
				invoiceIterator = delegator.find("InvoiceAndItem", cond, null, fieldsToSelect, null, findOpts);
				
				invoiceValue=0;
				baseValue=0;
				taxValue=0;
				invoiceDetailMap=[:];
				while (eachInvoice = invoiceIterator.next()) {
					
					conditionList=[];
					conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS,eachInvoice.invoiceId));
					if(eachTaxPer == 0){
						condList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.IN,["INV_FPROD_ITEM","INV_RAWPROD_ITEM"]));
					}
					if((eachInvoice.parentInvoiceItemSeqId) && (eachTaxPer != 0)){
						conditionList.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS,eachInvoice.parentInvoiceItemSeqId));
					}
					else{
						conditionList.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS,eachInvoice.invoiceItemSeqId));
					}
					invoiceItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, null, null, false );
					
						for(eachInvoiceItem in invoiceItems){
							//Debug.log("invoiceId============"+eachInvoiceItem.invoiceId);
							invoiceId=eachInvoiceItem.invoiceId;
							conditionList.clear();
							//conditionList=[];
							conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS,eachInvoiceItem.invoiceId));
							conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS,eachInvoiceItem.invoiceItemSeqId));
							invoiceItemsList = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, null, null, false );
							invoiceItemsWithTax= EntityUtil.filterByCondition(invoiceItemsList, EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS,eachTax));
							//Debug.log("invoiceItemsWithTax==========="+invoiceItemsWithTax);
							if(invoiceItemsWithTax)
								invoiceItemsWithTax=EntityUtil.getFirst(invoiceItemsWithTax);
							if(invoiceItemsWithTax.itemValue)
								taxValue= taxValue+invoiceItemsWithTax.itemValue;
							baseValue=baseValue+eachInvoiceItem.itemValue;
							//Debug.log("baseValue============"+baseValue);
						}
						//Debug.log("invoiceId============"+eachInvoice.invoiceId);
						
						//Debug.log("eachTaxPer============"+eachTaxPer);
						//invoiceDetailMap.put("invoiceId", eachInvoice.invoiceId);
				}
				
				totalBaseVal=totalBaseVal+baseValue;
				totalTaxVal=totalTaxVal+taxValue;
				//Debug.log("eachTax============"+eachTax);
				
				
				if((eachTax=="CST_SALE" || eachTax=="CST_PUR") && eachTaxPer!=0 ){
				invoiceDetailMap.put("taxType", "CST"+""+"@"+new BigDecimal(eachTaxPer));
				}
				if(eachTax=="CST_SALE" && eachTaxPer==0){
					invoiceDetailMap.put("taxType", "Inter- State Sales - Exempted");
				}
				if((eachTax=="CST_SALE" || eachTax=="CST_PUR") && eachTaxPer==2 ){
				invoiceDetailMap.put("taxType", "CST"+""+"@"+new BigDecimal(eachTaxPer)+" Against Form C");
				}
				if(eachTax=="CST_PUR" && eachTaxPer==0){
					invoiceDetailMap.put("taxType", "Inter- State Purchases - Exempted");
				}
				if((eachTax=="VAT_SALE" || eachTax=="VAT_PUR") && eachTaxPer!=0){
					invoiceDetailMap.put("taxType", "VAT"+""+"@"+new BigDecimal(eachTaxPer));
				}
				if(eachTax=="VAT_SALE" && eachTaxPer==0){
					invoiceDetailMap.put("taxType", "Sales - Exempt");
				}
				if(eachTax=="VAT_PUR" && eachTaxPer==0){
					invoiceDetailMap.put("taxType", "Purchases - Exempt");
				}
				//invoiceDetailMap.put("taxPer", eachTaxPer);
				invoiceDetailMap.put("baseValue", baseValue);
				invoiceDetailMap.put("taxValue", taxValue);
				//Debug.log("invoiceDetailMap============"+invoiceDetailMap);
				finalList.add(invoiceDetailMap);
				//Debug.log("finalList============"+finalList);
				
			}
			totalOutputBaseVal=totalOutputBaseVal+totalBaseVal;
			totalOutputTaxVal=totalOutputTaxVal+totalTaxVal;
			if(eachTax=="VAT_SALE"){
				totTempMap.put("taxType", "Total Output Tax");
				totTempMap.put("baseValue", totalOutputBaseVal);
				totTempMap.put("taxValue", totalOutputTaxVal);
				finalList.add(totTempMap);
			}
			if(eachTax=="VAT_SALE"){
				tempMap.put("taxType", " ");
				finalList.add(tempMap);
			}
			
			if(eachTax=="VAT_SALE"){
				tempToMap.put("taxType", "Purchases");
				finalList.add(tempToMap);
			}
			if(eachTax=="VAT_PUR"){
				totTempMap.put("taxType", "Total Input Credit");
				totTempMap.put("baseValue", totalOutputBaseVal);
				totTempMap.put("taxValue", totalOutputTaxVal);
				finalList.add(totTempMap);
			}
			
		}
		/*totalOutputBaseVal=totalOutputBaseVal+totalBaseVal;
		totalOutputTaxVal=totalOutputTaxVal+totalTaxVal;
		tempMap.put("taxType", "Total");
		tempMap.put("baseValue", totalOutputBaseVal);
		tempMap.put("taxValue", totalOutputTaxVal);
		finalList.add(tempMap);*/
		
	}
}


context.finalList=finalList;
//Debug.log("finalList============"+finalList);



