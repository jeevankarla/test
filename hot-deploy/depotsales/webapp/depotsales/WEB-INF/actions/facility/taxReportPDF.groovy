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
taxType=parameters.taxType;
partyfromDate=parameters.partyfromDate;
partythruDate=parameters.partythruDate;
partyId=parameters.partyId;

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
branchName = "";
if(branchId){
	branch = delegator.findOne("PartyGroup",[partyId : branchId] , false);
	branchName = branch.get("groupName");
	DateMap.put("branchName", branchName);
}
branchList = [];
condListb = [];
if(branchId){
	condListb.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, branchId));
	condListb.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
	condListb = EntityCondition.makeCondition(condListb, EntityOperator.AND);
	PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdTo"), null, null, false);
 
	branchList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);
 
	if(!branchList)
		branchList.add(branchId);
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
context.daystart = daystart;
context.dayend = dayend;

daystart = UtilDateTime.getDayStart(fromDate);
dayend = UtilDateTime.getDayEnd(thruDate);
finalMap=[:];
if(taxType=="VAT_PUR"){
	taxPercentageList=["4","5","5.5","14.5"];
}
else{
	taxPercentageList=["2","12.5"];
}
for(eachTaxPer in taxPercentageList){
	invioceItemsList=[];
	findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
	condList = [];
	if(UtilValidate.isNotEmpty(daystart)){
		condList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, daystart));
		condList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, dayend));
	}
	condList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "PURCHASE_INVOICE"));
	if(branchList)
	condList.add(EntityCondition.makeCondition("costCenterId", EntityOperator.IN,branchList));
	condList.add(EntityCondition.makeCondition("sourcePercentage", EntityOperator.EQUALS, new BigDecimal(eachTaxPer)));
	condList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
	
	condList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.IN, ["YARN_SALE","DEPOT_YARN_SALE"]));
	
	condList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.IN,["INV_RAWPROD_ITEM",taxType]));
	cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	fieldsToSelect = ["invoiceId","invoiceTypeId","partyIdFrom","invoiceItemTypeId","itemValue","sourcePercentage"] as Set;
	invoiceIterator = delegator.find("InvoiceAndItem", cond, null, fieldsToSelect, null, findOpts);
	//invoiceIds=EntityUtil.getFieldListFromEntityListIterator(invoice, "invoiceId", true);

	while (eachInvoice = invoiceIterator.next()) {
		invoiceDetailMap = [:];
		invoiceId=eachInvoice.invoiceId;
		invoiceDetailMap.put("invoiceId", invoiceId);
		conditionList=[];
		conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS,eachInvoice.invoiceId));
			//conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS,["INV_RAWPROD_ITEM",]));
		invoiceItems = delegator.findList("InvoiceItem",EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, null, null, false );
		if(UtilValidate.isNotEmpty(eachInvoice)){
					
			partyIdFrom = eachInvoice.partyIdFrom;
			invoiceDetailMap.put("partyIdFrom", partyIdFrom);		
		}
		partyIdFromName="";
		if(partyIdFrom){
			partyIdFromName = PartyHelper.getPartyName(delegator, partyIdFrom, false);
		}
		invoiceDetailMap.put("partyIdFromName", partyIdFromName);
		invoiceValue=0;
		SURCHARGE=0;
		for(eachInvoiceItem in invoiceItems){
			invoiceValue=invoiceValue+eachInvoiceItem.itemValue;
			invoiceDetailMap.put("invoiceValue", invoiceValue);
			if(eachInvoiceItem.invoiceItemTypeId==taxType){
				invoiceDetailMap.put("taxValue", eachInvoiceItem.itemValue);
			}
			if(taxType=="VAT_PUR" && eachInvoiceItem.invoiceItemTypeId=="VAT_SURCHARGE"){
				SURCHARGE=eachInvoiceItem.itemValue;
						//invoiceDetailMap.put("taxSurChargeValue", SURCHARGE);
			}
			else if(taxType=="CST_PUR" && eachInvoiceItem.invoiceItemTypeId=="CST_SURCHARGE"){
				SURCHARGE=eachInvoiceItem.itemValue;
			}
				invoiceDetailMap.put("taxSurChargeValue", SURCHARGE);
					
		}
				invioceItemsList.add(invoiceDetailMap);
	}
			if(invioceItemsList){
				finalMap.put(eachTaxPer, invioceItemsList);
			}
}

invoiceIterator.close();
context.finalMap=finalMap;

