import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;

import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.party.party.PartyHelper;

dctx = dispatcher.getDispatchContext();

fromMonth=parameters.fromMonth;
if(UtilValidate.isEmpty(fromMonth)){
	Debug.logError("Month Cannot Be Empty","");
	context.errorMessage = "Month Cannot Be Empty";
	return;
}
thruMonth=parameters.thruMonth;
if(UtilValidate.isEmpty(thruMonth)){
	Debug.logError("Month Cannot Be Empty","");
	context.errorMessage = "Month Cannot Be Empty";
	return;
}

def sdf = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");
try {
	fromMonthTime = new java.sql.Timestamp(sdf.parse("01-"+ fromMonth +" 00:00:00").getTime());
	thruMonthTime = new java.sql.Timestamp(sdf.parse("01-"+ thruMonth +" 00:00:00").getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: ", "");
}
locale = Locale.getDefault();
timeZone = TimeZone.getDefault();

Timestamp monthBegin = UtilDateTime.getMonthStart(fromMonthTime);
Timestamp monthEnd = UtilDateTime.getMonthEnd(thruMonthTime, timeZone, locale);

Debug.log("monthBegin 11111111111111111111111111111 "+monthBegin);
Debug.log("monthEnd 11111111111111111111111111111 "+monthEnd);

totalDays=UtilDateTime.getIntervalInDays(monthBegin,monthEnd);
if(totalDays>93){
	Debug.logError("Total Days Must Be Lessthan 93 Days","");
	context.errorMessage = "Total Days Must Be Lessthan 93 Days";
	return;
}
context.put("fromMonthTime",fromMonthTime);
context.put("thruMonthTime",thruMonthTime);

Debug.log("fromMonthTime @@@ "+fromMonthTime);

//---------custome time perid--------------
GenericValue customTimePeriod = null;
Map finYearContext = [:];
finYearContext.put("onlyIncludePeriodTypeIdList", UtilMisc.toList("FISCAL_YEAR"));
finYearContext.put("organizationPartyId", "Company");
finYearContext.put("userLogin", userLogin);
finYearContext.put("findDate", fromMonthTime);
finYearContext.put("excludeNoOrganizationPeriods", "Y");
List customTimePeriodList = [];
Map resultCtx = [:];

	resultCtx = dispatcher.runSync("findCustomTimePeriods", finYearContext);
	/*if(ServiceUtil.isError(resultCtx)){
		Debug.logError("Problem in fetching financial year ", module);
		return ServiceUtil.returnError("Problem in fetching financial year ");
	}*/

customTimePeriodList = (List)resultCtx.get("customTimePeriodList");
String finYearId = "";
if(UtilValidate.isNotEmpty(customTimePeriodList)){
	customTimePeriod = EntityUtil.getFirst(customTimePeriodList);
	finYearId = (String)customTimePeriod.get("customTimePeriodId");
}
if(customTimePeriod)
{
	customTimePeriodValue = customTimePeriod.get("customTimePeriodId");
	Debug.log("customTimePeriodValue %%%%%%%% "+customTimePeriodValue);
}
else
{
	customTimePeriodValue = " ";
}
context.customTimePeriodValue = customTimePeriodValue;
//-----------------------------------------
sectionCode = parameters.sectionCode;
//----------------------

panNo = delegator.findOne("PartyIdentification",UtilMisc.toMap("partyId", "Company", "partyIdentificationTypeId", "PAN_NUMBER"), false);
if(panNo){
	panNumber = panNo.get("idValue");
}else{
	panNumber = "-----";
}
tanNo = delegator.findOne("PartyIdentification",UtilMisc.toMap("partyId", "Company", "partyIdentificationTypeId", "TAN_NUMBER"), false);
if(tanNo){
	tanNumber = tanNo.get("idValue");
}else{
	tanNumber = "-----";
}
//----------party COntact details-------------------------------------------------------------
address1 = " ";
address2 = " ";
city = " ";
postalCode = " ";
state = " ";
email = " ";
telephone = " ";
group = delegator.findOne("PartyGroup",UtilMisc.toMap("partyId", "Company"), false);
if(group){
	partyGroup = group.get("groupName");
}else{
	partyGroup = "-----";
}
conditionListAddress = [];
conditionListAddress.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
conditionListAddress.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_EMAIL"));
conditionListAddress.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "FAX_BILLING"));
conditionAddress = EntityCondition.makeCondition(conditionListAddress,EntityOperator.OR);
listAddress = delegator.findList("PartyContactDetailByPurpose", conditionAddress, null, null, null, false);
listPartyAddress = EntityUtil.filterByCondition(listAddress, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company"));
	Debug.log("listPartyAddress @@@ "+listPartyAddress);
	if(listPartyAddress){
	listPartyAddress.each{ addressList ->
		if((addressList.contactMechPurposeTypeId).equals("BILLING_LOCATION"))
		{
			address1 = addressList.address1;
			address2 = addressList.address2;
			city = addressList.city;
			postalCode = addressList.postalCode;
			state = addressList.stateGeoName;
		}
		else if((addressList.contactMechPurposeTypeId).equals("PRIMARY_EMAIL"))
		{
			email = addressList.infoString;
		}
		else if((addressList.contactMechPurposeTypeId).equals("FAX_BILLING"))
		{
			telephone = addressList.contactNumber;
		}
		else
		{
			address1 = " ";
			address2 = " ";
			city = " ";
			postalCode = " ";
			state = " ";
			email = " ";
			telephone = " ";
		}
	}

	}
//--------------------------4. Details of tax deducted and paid to the credit of the Central Government:--------------------------
condList = [];
condList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "STATUTORY_OUT"));
condList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, "TAX1"));
condStatutory = EntityCondition.makeCondition(condList,EntityOperator.AND);
listStatutory = delegator.findList("Invoice", condStatutory, null, null, null, false);
statutoryInvoiceIds = EntityUtil.getFieldListFromEntityList(listStatutory, "invoiceId", true);

//-------------------------
conditionListFilter = [];
conditionListFilter.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "INVOICE_PAID"));
conditionListFilter.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS, "INVOICE_READY"));
condExpr = EntityCondition.makeCondition(conditionListFilter, EntityOperator.OR);
invoiceStatusList = EntityUtil.filterByCondition(listStatutory, condExpr);
//-------------------------
listTaxPaidNew = [];
invoiceStatusList.each{ invoices ->
	invoiceItemList = delegator.findList("InvoiceItem", EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS,invoices.invoiceId), null, null, null, false);
	tempMap = [:];
	tempMap["serialNo"]= "";
	tempMap["invoiceId"] = "";
	tempMap["tax"] = 0;
	tempMap["interest"] = 0;
	tempMap["fee"] = 0;
	tempMap["penalty"] = 0;
	tempMap["total"]=0;
	tempMap["paidDate"] = "";
	temp = "";
	if(invoiceItemList)
	{
		invoiceItemList.each{ list ->
			amountTotal = 0;
			invoiceItemTypeId = list.get("invoiceItemTypeId");
			temp = invoiceItemTypeId;
		if(invoiceItemTypeId.equals("TDS_INT") || invoiceItemTypeId.equals("TDS_234E") || invoiceItemTypeId.equals("TDS_PENAL") || invoiceItemTypeId.equals(sectionCode))
			{
			if(invoiceItemTypeId.equals("TDS_INT"))
			{
				tempMap["interest"] = (Integer)list.get("amount");
			}
			else if(invoiceItemTypeId.equals("TDS_234E"))
			{
				tempMap["fee"] = (Integer)list.get("amount");
				
			}
			else if(invoiceItemTypeId.equals("TDS_PENAL"))
			{
				tempMap["penalty"] = (Integer)list.get("amount");
			}
			else if(invoiceItemTypeId.equals(sectionCode))
			{
				tempMap["tax"] = (Integer)list.get("amount");
			}
tempMap["paidDate"] = UtilDateTime.toDateString(invoices.paidDate, "dd/MM/yyyy");

tempMap["invoiceId"] = invoices.invoiceId;
amountTotal = amountTotal + tempMap["tax"] + tempMap["penalty"] + tempMap["fee"] + tempMap["interest"];


tempMap["total"] =  amountTotal;
			}
		}
		if(temp.equals("TDS_INT") || temp.equals("TDS_234E") || temp.equals("TDS_PENAL") || temp.equals(sectionCode))
		{
		listTaxPaidNew.add(tempMap);
		}
	}
}
Debug.log("listTaxPaidNew @@@ "+listTaxPaidNew);

tempFinalListNew =[];
for(int i=0;i<listTaxPaidNew.size();i++){
	Map tempMap = listTaxPaidNew.get(i);
	tempMap.put("serialNo",i+1);
	tempFinalListNew.add(tempMap);
}
//--------------------------------- ANNEXURE ------------------------------------------------------------------------------
conditionList = [];
conditionList.add(EntityCondition.makeCondition("taxAuthPartyId", EntityOperator.EQUALS, "TAX1"));
conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, sectionCode));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
invoiceListFromInvoiceItem = delegator.findList("InvoiceItem", condition, null, null, null, false);

	conditionListForDate = [];
	conditionListForDate.add(EntityCondition.makeCondition("paidDate", EntityOperator.BETWEEN,UtilMisc.toList(monthBegin,monthEnd)));
	conditionListForDate.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,EntityUtil.getFieldListFromEntityList(invoiceListFromInvoiceItem, "invoiceId", true)));
	conditionListForDate.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.NOT_EQUAL, "Company")); 
	conditionListForDate.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.NOT_EQUAL, "TAX1"));
	
	cond=EntityCondition.makeCondition(conditionListForDate,EntityOperator.AND);
	invoiceList = delegator.findList("Invoice", cond, null, null, null, false);

	conditionListForDeductor = [];
	conditionListForDeductor.add(EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "TAN_NUMBER"));
	conditionListForDeductor.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company"));
	conditionDeductor=EntityCondition.makeCondition(conditionListForDeductor,EntityOperator.AND);
	deductorList = delegator.findList("PartyIdentification", conditionDeductor, null, null, null, false);
	
	if(deductorList)
	{
	partyId = deductorList[0].get("partyId");
	deductorName = PartyHelper.getPartyName(delegator, partyId, false);
	TAN = deductorList[0].get("idValue");
	}
	else
	{
		deductorName = "--";
		TAN = "--";
	}
	listAnnexureNew = [];
	GenericValue panId;
	GenericValue invoiceAmount;
	total = 0;
	invoiceTotal = 0;
		
		invoiceList.each{ invoiceList ->
			
			quantityNo = 0;
			amount = 0;
			
			tempMap = [:];
			tempMap["serialNo"]= "";
			tempMap["invoiceId"] = "";
			tempMap["partyId"] = "";
			tempMap["code"] = "";
			tempMap["partyName"] = "";
			tempMap["paidDate"] = "";
			tempMap["panNo"]= "";
			tempMap["amount"] = "";
			tempMap["section"] = "";
			tempMap["invoiceDate"] = "";
			tempMap["invoiceAmount"] = "";
					tempMap["invoiceId"]=invoiceList.invoiceId;
					tempMap["paidDate"] = UtilDateTime.toDateString(invoiceList.paidDate, "dd/MM/yyyy");
					tempMap["invoiceDate"] = UtilDateTime.toDateString(invoiceList.invoiceDate, "dd/MM/yyyy");
					tempMap["partyId"] = invoiceList.partyIdFrom;
					condListGroup = [];
					condListGroup.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, tempMap["partyId"]));
					condGroup=EntityCondition.makeCondition(condListGroup,EntityOperator.AND);
					groupCodeList = delegator.findList("PartyClassification", condGroup, null, null, null, false);
					if(groupCodeList){ tempMap["code"] = groupCodeList[0].get("partyClassificationGroupId"); }
					else{ tempMap["code"] = "";}
					
					tempMap["partyName"] = PartyHelper.getPartyName(delegator, tempMap["partyId"], false);
					
					panId = delegator.findOne("PartyIdentification",UtilMisc.toMap("partyId", tempMap["partyId"], "partyIdentificationTypeId", "PAN_NUMBER"), false);
					if(panId){
						tempMap["panNo"] = panId.get("idValue");
					}else{
						tempMap["panNo"] = "";
					}
					conditionList = [];
					conditionList.add(EntityCondition.makeCondition("taxAuthPartyId", EntityOperator.EQUALS, "TAX1"));
					conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, tempMap["invoiceId"]));
					condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
					invoiceAmountList = delegator.findList("InvoiceItem", condition, null, null, null, false);
					quantityNo = invoiceAmountList[0].get("quantity");
					if(quantityNo)
					{
					amount = -(invoiceAmountList[0].get("amount"));
					tempMap["amount"] = quantityNo * amount;
					}
					else
					{
						tempMap["amount"] = -(invoiceAmountList[0].get("amount"));
					}
					if(tempMap["amount"]<0)
					{
						tempMap["amount"] = -(tempMap["amount"]);
					}
					sectionCode = invoiceAmountList[0].get("invoiceItemTypeId");
					tempMap["section"] = sectionCode.substring(sectionCode.lastIndexOf("_") + 1);

					tempMap["invoiceAmount"] = org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceTotal(delegator,invoiceList.invoiceId);
					
					total = total + tempMap["amount"];
					invoiceTotal = invoiceTotal + tempMap["invoiceAmount"];
		listAnnexureNew.add(tempMap);
			}
		sectionCode = sectionCode.substring(sectionCode.lastIndexOf("_") + 1);
		
		tempFinalList =[];
		for(int i=0;i<listAnnexureNew.size();i++){
			Map tempMap = listAnnexureNew.get(i);
			tempMap.put("serialNo",i+1);
			tempFinalList.add(tempMap);
		}

context.listAnnexure = listAnnexureNew;
context.deductorName = deductorName;
context.TAN = TAN;
context.total = total;
context.invoiceTotal = invoiceTotal;
context.listTaxPaid = tempFinalListNew;
context.panNumber = panNumber;
context.tanNumber = tanNumber;
context.partyGroup = partyGroup;
context.sectionCode = sectionCode;

context.address1 = address1;
context.address2 = address2;	
context.city = city;
context.postalCode = postalCode;
context.state = state;
context.email = email;
context.telephone = telephone;

