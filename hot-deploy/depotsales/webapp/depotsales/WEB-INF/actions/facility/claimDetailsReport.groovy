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
rounding = RoundingMode.HALF_UP;
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
branchName =  PartyHelper.getPartyName(delegator, branchId, false);
context.branchName=branchName;
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
conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.IN,["TEN_PERCENT_SUBSIDY","INV_FPROD_ITEM"]));
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
InvoiceItem = delegator.findList("InvoiceItem",condition, null, null, null, false );

DecimalFormat df = new DecimalFormat("0.00");
DistrictWiseMap=[:];
totalsMap=[:];
totMap = [:];
totalQty=0
totalvalue=0
totalsubsidyAmt=0
totalserviceCharg=0;
totalclaimTotal=0
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
			 conditionList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
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
			 BigDecimal value= BigDecimal.ZERO;
			 conditionList.clear();
			 conditionList.add(EntityCondition.makeCondition("parentTypeId",EntityOperator.EQUALS,"ADDITIONAL_CHARGES"));
			 condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			 invoiceItemSubsidyDetails = delegator.findList("InvoiceItemType",condition, null, null, null, false );
			 invoiceItemTypeIdList= EntityUtil.getFieldListFromEntityList(invoiceItemSubsidyDetails,"invoiceItemTypeId", true);
			 invoiceItemTypeIdList.add("TEN_PERCENT_SUBSIDY");
			 invoiceItemTypeIdList.add("INV_FPROD_ITEM");
			 conditionList.clear();
			 conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.EQUALS,invoiceId));
			 conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.IN,invoiceItemTypeIdList));
			 condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			 invoiceSubsidyDetails1 = delegator.findList("InvoiceItem",condition, null, null, null, false );
			 conditionList.clear();
			 conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.NOT_EQUAL,"TEN_PERCENT_SUBSIDY"));
			 conditionList.add(EntityCondition.makeCondition("invoiceItemSeqId",EntityOperator.EQUALS,invoiceItemSeqId));
			 invoiceSubsidyDetails2 = EntityUtil.filterByCondition(invoiceSubsidyDetails1, EntityCondition.makeCondition(conditionList,EntityOperator.AND));
			 conditionList.clear();
			 conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.NOT_EQUAL,"TEN_PERCENT_SUBSIDY"));
			 conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId",EntityOperator.EQUALS,invoiceItemSeqId));
			 invoiceSubsidyDetails3 = EntityUtil.filterByCondition(invoiceSubsidyDetails1, EntityCondition.makeCondition(conditionList,EntityOperator.AND));
			 invoiceSubsidyDetails2.addAll(invoiceSubsidyDetails3);
			 for(eachItem in invoiceSubsidyDetails2)
			 {
				 value=value.add(eachItem.get("itemValue"));
			 }
			 temMap.put("categoryname", categoryname);
			 quantity = eachInvoiceItem.get("quantity");
			 //temMap.put("quantity", df.format(quantity.setScale(0, 0)));
			 temMap.put("quantity", df.format(quantity.setScale(0, rounding)));
			 value=eachInvoiceItem.get("itemValue");
			 temMap.put("value", df.format(value.setScale(2, rounding)));
			 BigDecimal serviceCharg= BigDecimal.ZERO;
			 conditionList.clear();
			 conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"TEN_PERCENT_SUBSIDY"));
			 conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId",EntityOperator.EQUALS,invoiceItemSeqId));
			 invoiceSubsidyDetails = EntityUtil.filterByCondition(invoiceSubsidyDetails1, EntityCondition.makeCondition(conditionList,EntityOperator.AND));
			 invoiceSubsidyDetails= EntityUtil.getFirst(invoiceSubsidyDetails);
			 
			 
			 if(UtilValidate.isNotEmpty(invoiceSubsidyDetails) && (invoiceSubsidyDetails.amount)){
				 subsidyAmt= (invoiceSubsidyDetails.itemValue)*(-1);
			 }
			 temMap.put("subsidyAmt", df.format(subsidyAmt.setScale(0, rounding)));
			
			 serviceCharg= (value*0.005);
			 temMap.put("serviceCharg", df.format(serviceCharg.setScale(0, rounding)));
			
			 BigDecimal claimTotal = (subsidyAmt +serviceCharg).setScale(0, rounding);
			 temMap.put("claimTotal", claimTotal);
			 
			 if(UtilValidate.isNotEmpty(subsidyAmt) && (subsidyAmt >0)){
				 totalQty=totalQty+quantity;
				 totalvalue=totalvalue+value;
				 totalsubsidyAmt=totalsubsidyAmt+subsidyAmt
				 totalserviceCharg=totalserviceCharg+serviceCharg
				 totalclaimTotal=totalclaimTotal+claimTotal
				temMap.put("sNo", String.valueOf(sNo));
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
					existingMap["quantity"]=df.format(quantity.add(new BigDecimal(existingMap.get("quantity"))).setScale(0, rounding));
					existingMap["value"]=df.format(value.add(new BigDecimal(existingMap.get("value"))).setScale(0, rounding));
					existingMap["subsidyAmt"]=df.format(subsidyAmt.add(new BigDecimal(existingMap.get("subsidyAmt"))).setScale(0, rounding));
					existingMap["serviceCharg"]=df.format(serviceCharg.add(new BigDecimal(existingMap.get("serviceCharg"))).setScale(0, rounding));
					existingMap["claimTotal"]=claimTotal.add(new BigDecimal(existingMap.get("claimTotal")));
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
				  DistrictWiseMap.put(districtName,fieldMap);
				}
			 } 
		 }
	}
}
totalsMap.put("quantity", totalQty);
totalsMap.put("value", totalvalue);
totalsMap.put("subsidyAmt", totalsubsidyAmt);
totalsMap.put("serviceCharg", totalserviceCharg.setScale(0, rounding));
totalsMap.put("claimTotal", totalclaimTotal);
context.totalsMap=totalsMap; 
context.totalsubsidyAmt=totalsubsidyAmt;
context.totalserviceCharg=totalserviceCharg;
context.DistrictWiseMap=DistrictWiseMap;
DistrictWiseList=[];
granTotal=0;
totalList=[];
for(Map DistrictMap : DistrictWiseMap){
	claimTotal= DistrictMap.getValue().get("claimTotal");
	granTotal +=claimTotal;
	DistrictWiseList.add(DistrictMap.getValue());
}
desList=["Amount of Reimbursement claimed","Less:- Advance amount already claimed","Balance amount (i-ii)"];
int i=1;
desList.each{ eachdesc ->
	temMap=[:];
	temMap.put("sno", i);
	temMap.put("amtString", eachdesc);
	if(i==1){
	   temMap.put("grandTotal", granTotal);
	}
	else{
	   temMap.put("grandTotal", "");
	}
	totalList.add(temMap);
	i=i+1;
}
//if(UtilValidate.isNotEmpty(productCategory) && (productCategory.description) && UtilValidate.isNotEmpty(invoiceSubsidyDetails)){
	totMap.put("sNo", "TOTAL");
	totMap.put("districtName", " ");
	totMap.put("userAgency", " ");
	totMap.put("invoiceDate", " ");
	totMap.put("productName", " ");
	totMap.put("categoryname", " ");
	totMap.put("quantity", totalQty);
	totMap.put("value", totalvalue);
	totMap.put("subsidyAmt", totalsubsidyAmt);
	totMap.put("serviceCharg", totalserviceCharg.setScale(0, rounding));
	totMap.put("claimTotal", String.valueOf(totalclaimTotal));	
//}
finalList.add(totMap);
context.totalList = totalList;
context.DistrictWiseList = DistrictWiseList;
context.finalList = finalList;
headinglist=[];
headingFrom = UtilDateTime.toDateString(dayBegin,"dd/MM/yyyy");
headingThru = UtilDateTime.toDateString(dayEnd,"dd/MM/yyyy");

headList=["                                  NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD.",
	"                    "+BOAddress,
	"                    Statement for Claiming Reimbursement against Yarn Subsidy allowed to the",
	"                    Handloom Weavers towards the Supply of Indian Silk and Cotton Hank Yarn",
	"                                                           "+headingFrom+"-"+headingThru," "];
headList.each{ eachHead ->
	headMap=[:];
	headMap.put("headerName",eachHead);
	headinglist.add(headMap);
}
context.headinglist = headinglist;


