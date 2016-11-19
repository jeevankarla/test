import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
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
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.order.order.*;
import org.ofbiz.party.contact.ContactMechWorker;
import java.util.Map.Entry;
reportTypeFlag=parameters.reportTypeFlag;
context.reportTypeFlag=reportTypeFlag;
claimFromDate=parameters.claimFromDate;
claimThruDate=parameters.claimThruDate;
dctx = dispatcher.getDispatchContext();
fromDateTime = null;
thruDateTime = null;
finalList = [];
def sdf = new SimpleDateFormat("yyyy, MMM dd");
try {
	fromDateTime = new java.sql.Timestamp(sdf.parse(claimFromDate).getTime());
	thruDateTime = new java.sql.Timestamp(sdf.parse(claimThruDate).getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+fromDate, "");
}
fromDateTime = UtilDateTime.getDayStart(fromDateTime);
monthBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
monthEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);
dayBegin = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(thruDateTime);
context.fromDate = dayBegin;
context.thruDate = dayEnd;
branchId = parameters.branchId;
branchContext=[:];
if(UtilValidate.isNotEmpty(branchId)){
   branchContext.put("branchId",branchId);
}
geoId = parameters.geoId;
roPartIds=["INT1","INT2","INT3","INT4","INT5","INT6","INT26","INT28","INT47"];
if(UtilValidate.isNotEmpty(geoId)){
	partyAndPostalAddress = delegator.findList("PartyAndPostalAddress",EntityCondition.makeCondition("partyId", EntityOperator.IN , roPartIds)  , null, null, null, false );
	if(UtilValidate.isNotEmpty(partyAndPostalAddress)){
		stateProvinceGeoIds= EntityUtil.getFieldListFromEntityList(partyAndPostalAddress,"stateProvinceGeoId", true);
		if(stateProvinceGeoIds.contains(geoId)){
			  partyAndPostalAddress = EntityUtil.filterByCondition(partyAndPostalAddress, EntityCondition.makeCondition("stateProvinceGeoId", EntityOperator.EQUALS,geoId));
			  branchId=EntityUtil.getFirst(partyAndPostalAddress).partyId;
			  branchContext.put("branchId",branchId);
		} 
	}
}
BOAddress="";
BOEmail="";
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
partyIdToList = [];

roIds=[];
if(branchId.equals("HO")){
	roPartIds.each{ eachRo ->
			resultCtx = dispatcher.runSync("getRoBranchList",UtilMisc.toMap("userLogin",userLogin,"productStoreId",eachRo));
			if(resultCtx && resultCtx.get("partyList")){
				partyList=resultCtx.get("partyList");
				partyList.each{eachparty ->
				partyIdToList.add(eachparty.partyIdTo);
				}
			}
			partyIdToList.add(eachRo);
	}
}
else{
	resultCtx = dispatcher.runSync("getRoBranchList",UtilMisc.toMap("userLogin",userLogin,"productStoreId",branchId));
	if(resultCtx && resultCtx.get("partyList")){
		partyList=resultCtx.get("partyList");
		partyIdToList= EntityUtil.getFieldListFromEntityList(partyList,"partyIdTo", true);
	}	
	partyIdToList.add(branchId);
}
stateFilterParties = delegator.findList("PartyContactDetailByPurpose",EntityCondition.makeCondition("stateProvinceGeoId", EntityOperator.EQUALS , geoId)  , UtilMisc.toSet("partyId"), null, null, false );
partyIds = EntityUtil.getFieldListFromEntityList(stateFilterParties, "partyId", true);
finalList = [];
conditionList = [];
conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
if(UtilValidate.isNotEmpty(branchId)){
	conditionList.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.IN,partyIdToList));
}
if(UtilValidate.isNotEmpty(geoId)){
	conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.IN,partyIds));
}
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
invoices = delegator.find("Invoice",condition,null,UtilMisc.toSet("invoiceId"),null,null);
invoiceIds=EntityUtil.getFieldListFromEntityListIterator(invoices, "invoiceId", true);
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,invoiceIds));
conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
InvoiceItem = delegator.findList("InvoiceItem",condition, null, null, null, false );
DecimalFormat df = new DecimalFormat("0.00");
DistrictWiseMap=[:];
if(UtilValidate.isNotEmpty(InvoiceItem)){
	sNo=1;
	summarySNo=0;
	for(i=0; i<InvoiceItem.size(); i++){
		
		 quantity =0;
		 temMap = [:];
		 invoiceDate = "";
		 productName = "";
		 BigDecimal subsidyAmt= BigDecimal.ZERO;
		 categoryname= "";
		 eachInvoiceItem = InvoiceItem[i];
		 invoiceItemTypeId=eachInvoiceItem.get("invoiceItemTypeId");
		 invoiceId = eachInvoiceItem.get("invoiceId");
		 invoiceItemSeqId = eachInvoiceItem.get("invoiceItemSeqId");
		 if(invoiceItemTypeId.equals("INV_FPROD_ITEM")){
			 invoice = delegator.findOne("Invoice",["invoiceId":eachInvoiceItem.get("invoiceId")],false);
			 invoiceDate = UtilDateTime.toDateString(invoice.invoiceDate,"dd/MM/yyyy");
			 temMap.put("invoiceDate", invoiceDate);
			 partyId = invoice.partyId;
			 userAgency = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, partyId, false);
			 temMap.put("userAgency", userAgency);
			 districtName = "";
			 districtGeoId ="";
			 conditionList.clear();
			 conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			 conditionList.add(EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "POSTAL_ADDRESS"));
			 condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			 partyPostalAddress = delegator.findList("PartyContactDetailByPurpose", condition, null, null, null, false);
			 partyPostalAddress= EntityUtil.getFirst(partyPostalAddress);
			 if(UtilValidate.isNotEmpty(partyPostalAddress) && (partyPostalAddress.stateProvinceGeoId)){
				 stateProvinceGeoId=partyPostalAddress.stateProvinceGeoId;
			 }
			 geo=delegator.findOne("Geo",[geoId : stateProvinceGeoId], false);
			 if(UtilValidate.isNotEmpty(geo)){
				 districtName= geo.geoName;
			 }
			 temMap.put("districtName", districtName);
			 productId = eachInvoiceItem.get("productId");
			 productDetails = delegator.findOne("Product",["productId":productId],false);
			 if(UtilValidate.isNotEmpty(productDetails)){
				 productName=productDetails.description;
			 }
			 temMap.put("productName", productName);
			 conditionList.clear();
			 conditionList.add(EntityCondition.makeCondition("primaryParentCategoryId",EntityOperator.NOT_EQUAL,null));
			 conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
			 condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			 productCategoryAndMember = delegator.findList("ProductCategoryAndMember",condition, null, null, null, false );
			 productCategoryAndMember= EntityUtil.getFirst(productCategoryAndMember);
			 if(UtilValidate.isNotEmpty(productCategoryAndMember) && (productCategoryAndMember.primaryParentCategoryId)){
				 productCategory = delegator.findOne("ProductCategory",["productCategoryId":productCategoryAndMember.primaryParentCategoryId],false);
				 if(UtilValidate.isNotEmpty(productCategory) && (productCategory.description)){
				     categoryname= productCategory.description;
				 }		 
			 }
			 temMap.put("categoryname", categoryname);
			 quantity = eachInvoiceItem.get("quantity");
			 temMap.put("quantity", df.format(quantity.setScale(0, 0)));
			 amount = eachInvoiceItem.get("amount");
			 value= quantity*amount;
			 temMap.put("value", df.format(value.setScale(0, 0)));
			 BigDecimal serviceCharg= BigDecimal.ZERO;
			 conditionList.clear();
			 conditionList.add(EntityCondition.makeCondition("parentInvoiceId",EntityOperator.EQUALS,invoiceId));
			 conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId",EntityOperator.EQUALS,invoiceItemSeqId));
			 conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"TEN_PERCENT_SUBSIDY"));
			 condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			 invoiceSubsidyDetails = delegator.findList("InvoiceItem",condition, null, null, null, false );
			 invoiceSubsidyDetails= EntityUtil.getFirst(invoiceSubsidyDetails);
			 if(UtilValidate.isNotEmpty(invoiceSubsidyDetails) && (invoiceSubsidyDetails.amount)){
				 subsidyAmt= (invoiceSubsidyDetails.amount)*(-1);
			 }
			 temMap.put("subsidyAmt", df.format(subsidyAmt.setScale(0, 0)));
			 serviceCharg= (subsidyAmt*0.05);
			 temMap.put("serviceCharg", df.format(serviceCharg.setScale(0, 0)));
			 BigDecimal claimTotal = (subsidyAmt +serviceCharg).setScale(0, 0);
			 temMap.put("claimTotal", df.format(claimTotal));
			 if(UtilValidate.isNotEmpty(subsidyAmt) && (subsidyAmt >0)){
				temMap.put("sNo", sNo);
				sNo = sNo+1;
			    finalList.add(temMap);
				if(DistrictWiseMap.get(districtName)){
					existingMap=(Map)DistrictWiseMap.get(districtName);
					existingMap["sNo"]=existingMap.get("sNo");
					existingMap["districtName"]=existingMap.get("districtName");
					existingMap["invoiceDate"]=existingMap.get("invoiceDate");
					existingMap["userAgency"]=existingMap.get("userAgency");
					existingMap["productName"]=existingMap.get("productName");
					existingMap["categoryname"]=existingMap.get("categoryname");
					/*quantity=df.format(quantity.setScale(0, 0));
					value=df.format(value.setScale(0, 0));
					subsidyAmt=df.format(subsidyAmt.setScale(0, 0));
					serviceCharg=df.format(serviceCharg.setScale(0, 0));
					claimTotal=df.format(claimTotal);*/
					existingMap["quantity"]=df.format(quantity.add(new BigDecimal(existingMap.get("quantity"))).setScale(0, 0));
					existingMap["value"]=df.format(value.add(new BigDecimal(existingMap.get("value"))).setScale(0, 0));
					existingMap["subsidyAmt"]=df.format(subsidyAmt.add(new BigDecimal(existingMap.get("subsidyAmt"))).setScale(0, 0));
					existingMap["serviceCharg"]=df.format(serviceCharg.add(new BigDecimal(existingMap.get("serviceCharg"))).setScale(0, 0));
					existingMap["claimTotal"]=df.format(claimTotal.add(new BigDecimal(existingMap.get("claimTotal"))));
					DistrictWiseMap.put(districtName,existingMap);
					
				}else{
				  fieldMap=[:];
				  summarySNo=summarySNo+1;				  
				  fieldMap["sNo"]=summarySNo;
				  fieldMap["districtName"]=temMap.get("districtName");
				  fieldMap["invoiceDate"]=temMap.get("invoiceDate");
				  fieldMap["userAgency"]=temMap.get("userAgency");
				  fieldMap["productName"]=temMap.get("productName");
				  fieldMap["categoryname"]=temMap.get("categoryname");
				  fieldMap["quantity"]=temMap.get("quantity");
				  fieldMap["value"]=temMap.get("value");
				  fieldMap["subsidyAmt"]=temMap.get("subsidyAmt");
				  fieldMap["serviceCharg"]=temMap.get("serviceCharg");
				  fieldMap["claimTotal"]=temMap.get("claimTotal");
				  Debug.log("fieldMap============================="+fieldMap);
				  
				  DistrictWiseMap.put(districtName,fieldMap);
				}
				
			 } 
			 
		 }
		
	}
}
context.DistrictWiseMap=DistrictWiseMap;
DistrictWiseList=[];
for(Map DistrictMap : DistrictWiseMap){
	DistrictWiseList.add(DistrictMap.getValue());
}
context.DistrictWiseList = DistrictWiseList;
context.finalList = finalList;

