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
invoices = delegator.findList("Invoice",condition,UtilMisc.toSet("invoiceId"),null,null,false);
invoiceIds=EntityUtil.getFieldListFromEntityList(invoices, "invoiceId", true);

conditionList.clear();
conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,invoiceIds));
conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"TEN_PERCENT_SUBSIDY"));
tenPerInvoicedsList = delegator.findList("InvoiceItem",EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, null, null, false );
tenPerinvoiceIds=EntityUtil.getFieldListFromEntityList(tenPerInvoicedsList, "invoiceId", true);

conditionList.clear();
conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,tenPerinvoiceIds));
invoices = EntityUtil.filterByCondition(invoices, EntityCondition.makeCondition(conditionList,EntityOperator.AND));
invoiceIds=EntityUtil.getFieldListFromEntityList(invoices, "invoiceId", true);

conditionList.clear();
conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,invoiceIds));
conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.IN,["TEN_PERCENT_SUBSIDY","INV_FPROD_ITEM"]));
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
InvoiceItem = delegator.findList("InvoiceItem",condition, null, null, null, false );

DecimalFormat df = new DecimalFormat("0.00");
DistrictWiseMap=[:];
totalsMap=[:];
totMap = [:];
BigDecimal totalQty= BigDecimal.ZERO;
BigDecimal totalvalue= BigDecimal.ZERO;
totalsubsidyAmt=0
BigDecimal totalserviceCharg= BigDecimal.ZERO;
BigDecimal totalclaimTotal= BigDecimal.ZERO;


// call seperate method for applyStyles in java class 

stylesMap=[:];   //stylesMap
stylesMap.put("mainHeader1", "NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD. ");  //mainHeader
stylesMap.put("mainHeader2", "Statement for Claiming Reimbursement against Yarn ");
stylesMap.put("mainHeader3", "Subsidy allowed to the Handloom Weavers towards the Supply");
stylesMap.put("mainHeader4", "of Indian Silk and Cotton Hank Yarn from "+ claimFromDate +" to "+claimThruDate);
stylesMap.put("mainHeadercellHeight",300);  //mainHeadercellHeight
stylesMap.put("mainHeaderfontName","Arial");  //URW Chancery L
stylesMap.put("mainHeaderFontSize",10);
stylesMap.put("mainHeadingCell",4);
stylesMap.put("mainHeaderBold",true);
stylesMap.put("columnHeaderBgColor",false);  //column_header
stylesMap.put("columnHeaderFontName","Arial");
stylesMap.put("columnHeaderFontSize",10);
stylesMap.put("autoSizeCell",true);
stylesMap.put("columnHeaderCellHeight",300);//columnHeaderCellHeight

request.setAttribute("stylesMap", stylesMap);
request.setAttribute("enableStyles", true);

headingMap=[:];
headingMap.put("sNo", "SI. No.");
headingMap.put("districtName", "Name Of State");
headingMap.put("userAgency", "Name Of User Agency");
headingMap.put("invoiceDate", "Date Of Supply");
headingMap.put("productName","Count Of yarn");
headingMap.put("categoryname","Varity Of yarn");
headingMap.put("quantity","Yarn Supplied during the quarter(in Kgs)");
headingMap.put("value","Value Of Yarn Before yarn subsidy(in Rs)");
headingMap.put("subsidyAmt","Yarn Subsidy @10% on yarn value Before Subsidy(in Rs)");
headingMap.put("serviceCharg","Service Charges @0.5% Of yarn value Before Subsidy(in Rs)");
headingMap.put("claimTotal","Total Claim For Yarn Subsidy And Claim Charges(in Rs)");

finalList.add(stylesMap);
finalList.add(headingMap);

if(UtilValidate.isNotEmpty(InvoiceItem)){
	sNo=1;
	summarySNo=0;
	for(i=0; i<InvoiceItem.size(); i++){
		
		 BigDecimal quantity= BigDecimal.ZERO; 
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
			 partyIdFrom = invoice.partyIdFrom;
			 userAgency = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, partyId, false);
			 temMap.put("userAgency", userAgency);
			 districtName = "";
			 districtGeoId ="";
			 conditionList.clear();
			 conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyIdFrom));
			 conditionList.add(EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "POSTAL_ADDRESS"));
			 conditionList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
			 conditionList.add(EntityCondition.makeCondition("stateProvinceGeoId", EntityOperator.NOT_EQUAL,null));
			 condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			 partyPostalAddress = delegator.findList("PartyContactDetailByPurpose", condition, null, null, null, false);
			 partyPostalAddress= EntityUtil.getFirst(partyPostalAddress);
			 if(UtilValidate.isNotEmpty(partyPostalAddress) && (partyPostalAddress.stateProvinceGeoId)){
				 stateProvinceGeoId=partyPostalAddress.stateProvinceGeoId;
				 geo=delegator.findOne("Geo",[geoId : stateProvinceGeoId], false);
				 if(UtilValidate.isNotEmpty(geo)){
					 districtName= geo.geoName;
				 }
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
			 conditionList.add(EntityCondition.makeCondition("parentTypeId",EntityOperator.IN,UtilMisc.toList("ADDITIONAL_CHARGES","DISCOUNTS")));
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
			 conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"TEN_PERCENT_SUBSIDY"));
			 conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId",EntityOperator.EQUALS,invoiceItemSeqId));
			 invoiceSubsidyDetails2 = EntityUtil.filterByCondition(invoiceSubsidyDetails1, EntityCondition.makeCondition(conditionList,EntityOperator.AND));
			 invoiceSubsidyDetail2= EntityUtil.getFirst(invoiceSubsidyDetails2);
			 /*if(UtilValidate.isNotEmpty(invoiceSubsidyDetail2)){
				 value=-(invoiceSubsidyDetail2.get("amount")*10)
			 }*/
			 conditionList.clear();
			 conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
			 conditionList.add(EntityCondition.makeCondition("invoiceItemSeqId",EntityOperator.EQUALS,invoiceItemSeqId));
			 invoiceSubsidyDetails4 = EntityUtil.filterByCondition(invoiceSubsidyDetails1, EntityCondition.makeCondition(conditionList,EntityOperator.AND));
			 invoiceSubsidyDetail4= EntityUtil.getFirst(invoiceSubsidyDetails4);
			 temMap.put("categoryname", categoryname);
			 //quantity = eachInvoiceItem.get("quantity");
			 //temMap.put("quantity", df.format(quantity.setScale(0, 0)));
			 //value=eachInvoiceItem.get("itemValue");
			 BigDecimal serviceCharg= BigDecimal.ZERO;
			 conditionList.clear();
			 conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"TEN_PERCENT_SUBSIDY"));
			 conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS,invoiceItemSeqId)));
			 tenPeSubDetails = EntityUtil.filterByCondition(invoiceSubsidyDetails1, EntityCondition.makeCondition(conditionList,EntityOperator.AND));
			 tenPeSubDetail= EntityUtil.getFirst(tenPeSubDetails);
			 subsidyAmt=subsidyAmt.add(tenPeSubDetail.itemValue*(-1))
			 value=subsidyAmt.multiply(10);
		
			 conditionList.clear();
			 conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.EQUALS,invoiceId));
			 conditionList.add(EntityCondition.makeCondition("invoiceItemSeqId",EntityOperator.EQUALS,invoiceSubsidyDetail2.invoiceItemSeqId));
			 orderadjsmentBillings = delegator.findList("OrderAdjustmentBilling",EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, null, null, false );
			 orderadjsmentBilling= EntityUtil.getFirst(orderadjsmentBillings);
			 if(orderadjsmentBilling){
				 quantity=orderadjsmentBilling.quantity
			 }
			 temMap.put("subsidyAmt", subsidyAmt.setScale(0, rounding));
			 temMap.put("quantity", (quantity).setScale(2, rounding));
			 temMap.put("value", value.setScale(2, rounding));
			 serviceCharg= (value*0.005);
			 temMap.put("serviceCharg", (serviceCharg).setScale(0, rounding));
			 BigDecimal claimTotal = subsidyAmt +serviceCharg;
			 temMap.put("claimTotal", claimTotal.setScale(0, rounding));
			 if(UtilValidate.isNotEmpty(subsidyAmt) && (subsidyAmt >0) && quantity>0){
				 totalQty=totalQty.add(quantity);
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
					existingMap["quantity"]=quantity.add(new BigDecimal(existingMap.get("quantity"))).setScale(2, rounding);
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

totalQty=(totalQty).setScale(2, rounding);
totalsMap.put("quantity", totalQty);
totalvalue=(totalvalue).setScale(0, rounding);
totalsMap.put("value", totalvalue);
totalsubsidyAmt=(totalsubsidyAmt).setScale(0, rounding);
totalsMap.put("subsidyAmt", totalsubsidyAmt);
totalserviceCharg= (totalserviceCharg).setScale(0, rounding);
totalsMap.put("serviceCharg", totalserviceCharg);
totalclaimTotal= (totalclaimTotal).setScale(0, rounding);
totalsMap.put("claimTotal", totalclaimTotal);
}
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
BigDecimal totalQtySum= BigDecimal.ZERO;
BigDecimal totalvalueSum= BigDecimal.ZERO;
totalsubsidyAmtSum=0
BigDecimal totalserviceChargSum= BigDecimal.ZERO;
BigDecimal totalclaimTotalSum= BigDecimal.ZERO;
tempToMap=[:];
for(districtDetails in DistrictWiseList){
	totalQtySum+=new BigDecimal(districtDetails.quantity);
	totalvalueSum+=new BigDecimal(districtDetails.value);
	totalsubsidyAmtSum+=new BigDecimal(districtDetails.subsidyAmt);
	totalserviceChargSum+=new BigDecimal(districtDetails.serviceCharg);
	totalclaimTotalSum+=new BigDecimal(districtDetails.claimTotal);
}
//Debug.log("totalserviceChargSum===@@@@@==="+totalserviceChargSum);
tempToMap.put("quantity", totalQtySum);
tempToMap.put("value", totalvalueSum);
tempToMap.put("subsidyAmt", totalsubsidyAmtSum);
tempToMap.put("serviceCharg", totalserviceChargSum.setScale(0, rounding));
tempToMap.put("claimTotal", totalclaimTotalSum);
context.tempToMap = tempToMap;
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


