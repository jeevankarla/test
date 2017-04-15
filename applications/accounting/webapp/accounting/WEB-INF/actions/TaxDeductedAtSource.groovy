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

condList = [];
roId = parameters.division;
context.roId = roId;
segmentId = parameters.segment;
branchList = [];
condList.clear();
if(UtilValidate.isNotEmpty(roId)&& !roId.equals("Company")){
	condList.add(EntityCondition.makeCondition("partyIdFrom" , EntityOperator.EQUALS,roId));
	condList.add(EntityCondition.makeCondition("roleTypeIdFrom" , EntityOperator.EQUALS,"PARENT_ORGANIZATION"));
	condList.add(EntityCondition.makeCondition("roleTypeIdTo" , EntityOperator.EQUALS,"ORGANIZATION_UNIT"));
	condList.add(EntityCondition.makeCondition("partyRelationshipTypeId" , EntityOperator.EQUALS,"BRANCH_CUSTOMER"));
	List roWiseBranchaList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(condList,EntityOperator.AND), null, null, null, false);
	if(UtilValidate.isNotEmpty(roWiseBranchaList)){
		branchList= EntityUtil.getFieldListFromEntityList(roWiseBranchaList,"partyIdTo", true);
		branchList.add(roId);
	}
	
}
condList.clear();

if(UtilValidate.isEmpty(fromMonth)){
	Debug.logError("Month Cannot Be Empty","");
	context.errorMessage = "Month Cannot Be Empty";
	return;
}
Debug.log("fromMonth ^^^^^^^^ "+fromMonth);

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

context.put("fromMonthTime",fromMonthTime);
context.put("thruMonthTime",thruMonthTime);

totalDays=UtilDateTime.getIntervalInDays(monthBegin,monthEnd);
if(totalDays>93){
	Debug.logError("Total Days Must Be Lessthan 93 Days","");
	context.errorMessage = "Total Days Must Be Lessthan 93 Days";
	return;
}

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
finalList=[];
sectionList=[];
if(sectionCode == "ALL"){
	sectionList.add("TDS_194C");
	sectionList.add("TDS_194H");
	sectionList.add("TDS_194J");
	sectionList.add("TDS_194I");
}else{
	sectionList.add(parameters.sectionCode);
}
finalList.addAll(sectionList);

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
/*conditionListAddress.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
conditionListAddress.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_EMAIL"));
conditionListAddress.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "FAX_BILLING"));*/
conditionListAddress.add(EntityCondition.makeCondition("contactMechPurposeTypeId" , EntityOperator.IN, UtilMisc.toList("BILLING_LOCATION", "PRIMARY_EMAIL", "FAX_BILLING")));
conditionListAddress.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company"));
conditionAddress = EntityCondition.makeCondition(conditionListAddress,EntityOperator.AND);
listPartyAddress = delegator.findList("PartyContactDetailByPurpose", conditionAddress, null, null, null, false);
/*listPartyAddress = EntityUtil.filterByCondition(listAddress, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company"));*/
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
if(UtilValidate.isNotEmpty(roId)&& !roId.equals("Company"))
	condList.add(EntityCondition.makeCondition("costCenterId" , EntityOperator.IN, branchList));
if(!segmentId.equals("All") && !segmentId.equals("YARN_SALE"))
	condList.add(EntityCondition.makeCondition("purposeTypeId" , EntityOperator.EQUALS, segmentId));
if(segmentId.equals("YARN_SALE"))
	condList.add(EntityCondition.makeCondition("purposeTypeId" , EntityOperator.IN, UtilMisc.toList("YARN_SALE", "DEPOT_YARN_SALE")));

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
	tempMap["total"] = 0;
	tempMap["paidDate"] = "";
	temp = "";
	if(invoiceItemList)
	{
		invoiceItemList.each{ invoiceItem ->
			amountTotal = 0;
			invoiceItemTypeId = invoiceItem.get("invoiceItemTypeId");
			temp = invoiceItemTypeId;
		if(invoiceItemTypeId.equals("TDS_INT") || invoiceItemTypeId.equals("TDS_234E") || invoiceItemTypeId.equals("TDS_PENAL") || invoiceItemTypeId.contains(finalList))
			{
			if(invoiceItemTypeId.equals("TDS_INT"))
			{
				tempMap["interest"] = (Integer)invoiceItem.get("amount");
			}
			else if(invoiceItemTypeId.equals("TDS_234E"))
			{
				tempMap["fee"] = (Integer)invoiceItem.get("amount");
				
			}
			else if(invoiceItemTypeId.equals("TDS_PENAL"))
			{
				tempMap["penalty"] = (Integer)invoiceItem.get("amount");
			}
			else if(invoiceItemTypeId.contains(finalList))
			{
				tempMap["tax"] = (Integer)invoiceItem.get("amount");
			}
tempMap["paidDate"] = UtilDateTime.toDateString(invoices.paidDate, "dd/MM/yyyy");

tempMap["invoiceId"] = invoices.invoiceId;
amountTotal = amountTotal + tempMap["tax"] + tempMap["penalty"] + tempMap["fee"] + tempMap["interest"];


tempMap["total"] =  amountTotal;
			}
		}
		if(temp.equals("TDS_INT") || temp.equals("TDS_234E") || temp.equals("TDS_PENAL") || temp.contains(finalList))
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
Timestamp monthBeginNew = UtilDateTime.getMonthStart(fromMonthTime);
Timestamp monthEndNew = UtilDateTime.getMonthEnd(fromMonthTime, timeZone, locale);

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

monthCheck = UtilDateTime.toDateString(monthEndNew, "MMM");
boolean flag = true;
listAnnexureNew = [];

while(flag)
{
	Debug.log("monthBeginNew== "+monthBeginNew);
	Debug.log("monthEndNew== "+monthEndNew);

conditionList = [];
if(UtilValidate.isNotEmpty(roId)&& !roId.equals("Company"))
	conditionList.add(EntityCondition.makeCondition("costCenterId" , EntityOperator.IN, branchList));
if(!segmentId.equals("All") && !segmentId.equals("YARN_SALE"))
	conditionList.add(EntityCondition.makeCondition("purposeTypeId" , EntityOperator.EQUALS, segmentId));
if(segmentId.equals("YARN_SALE"))
	conditionList.add(EntityCondition.makeCondition("purposeTypeId" , EntityOperator.IN, UtilMisc.toList("YARN_SALE", "DEPOT_YARN_SALE")));

//conditionList.add(EntityCondition.makeCondition("taxAuthPartyId", EntityOperator.EQUALS, "TAX1"));
conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.IN, finalList));
conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.BETWEEN,UtilMisc.toList(monthBeginNew,monthEndNew)));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.NOT_EQUAL, "Company"));
conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.NOT_EQUAL, "TAX1"));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
invoiceList = delegator.findList("InvoiceAndItem", condition, null, null, null, false);


	GenericValue partyIdentification;

	
		invoiceList.each{ invoiceEntry ->
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
			tempMap["month"] = "";
			
					tempMap["invoiceId"]=invoiceEntry.invoiceId;
					tempMap["paidDate"] = UtilDateTime.toDateString(invoiceEntry.paidDate, "dd/MM/yyyy");
					tempMap["invoiceDate"] = UtilDateTime.toDateString(invoiceEntry.invoiceDate, "dd/MM/yyyy");
					tempMap["partyId"] = invoiceEntry.partyIdFrom;
					condListGroup = [];
					condListGroup.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, tempMap["partyId"]));
					condGroup=EntityCondition.makeCondition(condListGroup,EntityOperator.AND);
					groupCodeList = delegator.findList("PartyClassification", condGroup, null, null, null, false);
					if(groupCodeList){ tempMap["code"] = groupCodeList[0].get("partyClassificationGroupId"); }
					else{ tempMap["code"] = "";}
					tempMap["partyName"] = PartyHelper.getPartyName(delegator, tempMap["partyId"], false);
					partyIdentification = delegator.findOne("PartyIdentification",UtilMisc.toMap("partyId", tempMap["partyId"], "partyIdentificationTypeId", "PAN_NUMBER"), false);
					if(partyIdentification){
						tempMap["panNo"] = partyIdentification.get("idValue");
					}else{
						tempMap["panNo"] = "";
					}
					tempMap["amount"] = invoiceEntry.amount;
					if(tempMap["amount"]<0)
					{
						tempMap["amount"] = -(tempMap["amount"]);
					}
					sectionCode = invoiceEntry.invoiceItemTypeId;
					tempMap["section"] = sectionCode.substring(sectionCode.lastIndexOf("_") + 1);
					
					grossAmtConditionList = [];
					grossAmtConditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceEntry.invoiceId));
					grossAmtConditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL, sectionCode));
					grossCond=EntityCondition.makeCondition(grossAmtConditionList,EntityOperator.AND);
					grossCondList = delegator.findList("InvoiceItem", grossCond, null, null, null, false);
					tempAmt = 0;
					grossCondList.each{ grossEntry ->
					if(grossEntry.amount<0)
					{
						grossEntry.amount = -(grossEntry.amount);
					}
					tempAmt = tempAmt+grossEntry.amount;
					}
					tempMap["invoiceAmount"] = tempAmt;
					//tempMap["invoiceAmount"] = org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceTotal(delegator,invoiceEntry.invoiceId);
					if(tempMap["invoiceAmount"] < 0)
					{
						tempMap["invoiceAmount"] = -(tempMap["invoiceAmount"]);
					}
					tempMap["month"] = UtilDateTime.toDateString(monthEndNew, "MMM");
					
		listAnnexureNew.add(tempMap);
		
		}
		
		if(monthEndNew == monthEnd)
		{
			flag = false;
		}
		else
		{
			monthBeginNew = UtilDateTime.addDaysToTimestamp(monthEndNew, 1);
			monthBeginNew = UtilDateTime.getMonthStart(monthBeginNew);
			monthEndNew = UtilDateTime.getMonthEnd(monthBeginNew, timeZone, locale);
		}
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
		context.listTaxPaid = tempFinalListNew;
		context.panNumber = panNumber;
		context.tanNumber = tanNumber;
		context.partyGroup = partyGroup;
		context.sectionCode = sectionCode;
		context.monthCheck = monthCheck;
		
		context.address1 = address1;
		context.address2 = address2;
		context.city = city;
		context.postalCode = postalCode;
		context.state = state;
		context.email = email;
		context.telephone = telephone;
		