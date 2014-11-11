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

totalDays=UtilDateTime.getIntervalInDays(monthBegin,monthEnd);
if(totalDays>93){
	Debug.logError("Total Days Must Be Lessthan 93 Days","");
	context.errorMessage = "Total Days Must Be Lessthan 93 Days";
	return;
}
finalList=[];
if(reportType=="deductee"){
	Timestamp start=monthBegin;
	monthSno=1;
	while(start<=monthEnd){
	Timestamp end=UtilDateTime.getMonthEnd(start, timeZone, locale);
		emplInputMap = [:];
		emplInputMap.put("userLogin", userLogin);
		emplInputMap.put("orgPartyId", "Company");
		emplInputMap.put("fromDate", start);
		emplInputMap.put("thruDate", end);
		Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
		List<GenericValue> employementList = (List<GenericValue>)EmploymentsMap.get("employementList");
		employementList = EntityUtil.orderBy(employementList, UtilMisc.toList("partyIdTo"));
		employementList.each{employment->
			String lastName="";
			if(employment.lastName!=null){
				lastName=employment.lastName;
			}
			name=employment.firstName+" "+lastName;
			name=name.toUpperCase();
			panId="";
			if(UtilValidate.isNotEmpty(employment.panId)){
				panId=employment.panId;
			}
			customMap=[:];
			customTimePeriodTotals = PayrollService.getEmployeeSalaryTotalsForPeriod(dctx,UtilMisc.toMap("partyId",employment.partyId,"fromDate",start,"thruDate",end,"userLogin",userLogin)).get("periodTotalsForParty");
			if(UtilValidate.isNotEmpty(customTimePeriodTotals)){
				Iterator customTimePeriodIter = customTimePeriodTotals.entrySet().iterator();
				custTempMap=[:];
				while(customTimePeriodIter.hasNext()){
					Map.Entry customTimePeriodEntry = customTimePeriodIter.next();
					if(customTimePeriodEntry.getKey() != "customTimePeriodTotals"){
						tempMap=[:];
						periodTotals = customTimePeriodEntry.getValue().get("periodTotals");
						grossBenefitAmt=periodTotals.get("grossBenefitAmt")
						if(UtilValidate.isNotEmpty(grossBenefitAmt) && grossBenefitAmt>0){
							incomeTax = periodTotals.get("PAYROL_DD_INC_TAX");
							if(UtilValidate.isNotEmpty(incomeTax) && -(incomeTax)>0){
								tempMap.put("monthSno",monthSno);
								tempMap.put("mode","");
								tempMap.put("partyId",employment.partyId);
								tempMap.put("name",name);
								tempMap.put("panId",panId);
								tempMap.put("endDate",UtilDateTime.toDateString(end, "dd/MM/yyyy"));
								tempMap.put("ddDate",UtilDateTime.toDateString(end, "dd/MM/yyyy"));
								tempMap.put("grossAmt",grossBenefitAmt);
								tempMap.put("tdsAmt",-(incomeTax));
								surcharge=0.00;
								educationCess=0.00;
								totalTds=surcharge+educationCess-(incomeTax);
								totalTax=surcharge+educationCess-(incomeTax);
								tempMap.put("surcharge",surcharge);
								tempMap.put("educationCess",educationCess);
								tempMap.put("totalTds",totalTds);
								tempMap.put("totalTax",totalTax);
								custTempMap.putAll(tempMap);
								finalList.add(custTempMap);
							}
						}
					}
				}
			}
		}	
		start=UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(UtilDateTime.toTimestamp(end), 1));
		monthSno=monthSno+1;
	}
	finalList = UtilMisc.sortMaps(finalList, UtilMisc.toList("partyId"));
	
	tempFinalList =[];
	for(int i=0;i<finalList.size();i++){
		Map tempMap = finalList.get(i);
		tempMap.put("serialNo",i+1);
		tempFinalList.add(tempMap);
	}
	context.finalList=tempFinalList;
}
if(reportType=="deductor"){
 partyId="Company";	
 finalList=[];
 address1="";
 address2="";
 city="";
 postalCode="";
 panId="";
 tanId="";
 List conditionList=[];
 conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
 condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
 partyIdentificationList=delegator.findList("PartyIdentification",condition,UtilMisc.toSet("partyIdentificationTypeId","idValue"),null,null,false)
 partyNameList=delegator.findList("PartyGroup",condition,UtilMisc.toSet("groupName"),null,null,false);
 if(partyIdentificationList){
	 partyIdentificationList.each{ partyIdentification ->
		 if(partyIdentification.get("partyIdentificationTypeId")=="PAN_NUMBER"){
			 tempMap=[:];
			 panId=partyIdentification.get("idValue");
			 tempMap.put("key1","Permanent Account Number");
			 tempMap.put("key2",panId);
			 finalList.add(tempMap);
		 }
		 if(partyIdentification.get("partyIdentificationTypeId")=="TAN_NUMBER"){
			 tempMap=[:];
			 tanId=partyIdentification.get("idValue");
			 tempMap.put("key1","Tax Deduction Account Number");
			 tempMap.put("key2",tanId);
			 finalList.add(tempMap);
		 }
		 
	 }
 }
 if(UtilValidate.isEmpty(panId)){
	 tempMap=[:];
	 tempMap.put("key1","Permanent Account Number");
	 tempMap.put("key2",panId);
	 finalList.add(tempMap);
 }
 if(UtilValidate.isEmpty(tanId)){
	 tempMap=[:];
	 tempMap.put("key1","Tax Deduction Account Number");
	 tempMap.put("key2",tanId);
	 finalList.add(tempMap);
 }
 tempMap=[:];
 companyName="KARNATAKA COOP MILK PRODUCERS FEDERATION";
 tempMap.put("key1","Name of the Company");
 tempMap.put("key2",companyName);
 finalList.add(tempMap);
 tempMap=[:];
 branch="";
 tempMap.put("key1","Branch/ Divison");
 tempMap.put("key2",branch);
 finalList.add(tempMap);
 tempMap=[:];
 doorNo="SURVEY NO 16 NORTH 7";
 tempMap.put("key1","Flat / Door / Block No");
 tempMap.put("key2",doorNo);
 finalList.add(tempMap);
 partyName=EntityUtil.getFirst(partyNameList);
 premisesName="";
 if(partyName){
	 premisesName=partyName.get("groupName");
 }
 tempMap=[:];
 tempMap.put("key1","Name of Premises / Building");
 tempMap.put("key2",premisesName);
 finalList.add(tempMap);
 partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:partyId, userLogin: userLogin]);
 if(partyPostalAddress){
	 
	if(partyPostalAddress.address1){
		address1=partyPostalAddress.address1;
	}
	tempMap=[:];
	tempMap.put("key1","Road / Street / Lane");
	tempMap.put("key2",address1);
	finalList.add(tempMap);
	if(partyPostalAddress.address2){
		address2=partyPostalAddress.address2;
	}
	tempMap=[:];
	tempMap.put("key1","Area / Locality");
	tempMap.put("key2",address2);
	finalList.add(tempMap);
	if(partyPostalAddress.city){
		
		city=partyPostalAddress.city;
	}
	tempMap=[:];
	tempMap.put("key1","Town / District / City");
	tempMap.put("key2",city);
	finalList.add(tempMap);
	
	if(partyPostalAddress.postalCode){
		postalCode=partyPostalAddress.postalCode;
	}
	tempMap=[:];
	tempMap.put("key1","PIN Code");
	tempMap.put("key2",postalCode);
	finalList.add(tempMap);
	
 }
 
 
 
 partyTelephone= dispatcher.runSync("getPartyTelephone", [partyId:partyId, userLogin: userLogin]);
 phoneNumber = "";
 stdCode="";
 if (partyTelephone) {
	 phoneNumber = partyTelephone.contactNumber;
	 stdCode=partyTelephone.areaCode;
 }
 tempMap=[:];
 tempMap.put("key1","STD CODE");
 tempMap.put("key2",stdCode);
 finalList.add(tempMap);
 
 tempMap=[:];
 tempMap.put("key1","Telephone No.");
 tempMap.put("key2",phoneNumber);
 finalList.add(tempMap);
 emailAddress="";
 partyEmail= dispatcher.runSync("getPartyEmail", [partyId:partyId, userLogin: userLogin]);
 if(partyEmail){
	 emailAddress=partyEmail.emailAddress;
 }
 tempMap=[:];
 tempMap.put("key1","Email");
 tempMap.put("key2",emailAddress);
 finalList.add(tempMap);
 
 partyName="";
 emplPositionTypeId="MNG_FIN";
 List condList=[];
 condList.add(EntityCondition.makeCondition("emplPositionTypeId", EntityOperator.EQUALS, "MNG_FIN"));
 cond=EntityCondition.makeCondition(condList,EntityOperator.AND);
 emplPositionAndFulfillments=delegator.findList("EmplPositionAndFulfillment",cond,UtilMisc.toSet("employeePartyId","fromDate"),null,null,false);
 designation="";
 emplDesignations=delegator.findList("EmplPositionType",cond,UtilMisc.toSet("description"),null,null,false);
 emplDesignation=EntityUtil.getFirst(emplDesignations);
 designation=emplDesignation.get("description");
 if(emplPositionAndFulfillments){
	 emplPositionAndFulfillment=emplPositionAndFulfillments.getLast();
	 emplId=emplPositionAndFulfillment.get("employeePartyId");
	 partyName=PartyHelper.getPartyName(delegator, emplId, false);
	 
 }
 tempMap=[:];
 tempMap.put("key1","Name of the Responsible Person");
 tempMap.put("key2",partyName);
 finalList.add(tempMap);
 
 tempMap=[:];
 tempMap.put("key1","Flat / Door / Block No");
 tempMap.put("key2",doorNo);
 finalList.add(tempMap);
 
 tempMap=[:];
 tempMap.put("key1","Name of Premises / Building");
 tempMap.put("key2",premisesName);
 finalList.add(tempMap);
 
 tempMap=[:];
 tempMap.put("key1","Road / Street / Lane");
 tempMap.put("key2",address1);
 finalList.add(tempMap);
 
 tempMap=[:];
 tempMap.put("key1","Area / Locality");
 tempMap.put("key2",address2);
 finalList.add(tempMap);
 
 tempMap=[:];
 tempMap.put("key1","Town / District / City");
 tempMap.put("key2",city);
 finalList.add(tempMap);
 
 tempMap=[:];
 tempMap.put("key1","PIN Code");
 tempMap.put("key2",postalCode);
 finalList.add(tempMap);
 
 tempMap=[:];
 tempMap.put("key1","STD CODE");
 tempMap.put("key2",stdCode);
 finalList.add(tempMap);
 
 tempMap=[:];
 tempMap.put("key1","Telephone No.");
 tempMap.put("key2",phoneNumber);
 finalList.add(tempMap);
 
 tempMap=[:];
 tempMap.put("key1","Email");
 tempMap.put("key2",emailAddress);
 finalList.add(tempMap);
 
 tempMap=[:];
 tempMap.put("key1","Designation");
 tempMap.put("key2",designation);
 finalList.add(tempMap);
 
 remarks="";
 tempMap=[:];
 tempMap.put("key1","Remarks");
 tempMap.put("key2",remarks);
 finalList.add(tempMap);
 
 tdscircle="";
 tempMap=[:];
 tempMap.put("key1","TDS Circle");
 tempMap.put("key2",tdscircle);
 finalList.add(tempMap);
 
context.finalList=finalList;
}
if(reportType=="challan"){
	Timestamp start=monthBegin;
	monthSno=1;
	while(start<=monthEnd){
	Timestamp end=UtilDateTime.getMonthEnd(start, timeZone, locale);
		emplInputMap = [:];
		emplInputMap.put("userLogin", userLogin);
		emplInputMap.put("orgPartyId", "Company");
		emplInputMap.put("fromDate", start);
		emplInputMap.put("thruDate", end);
		Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
		List<GenericValue> employementList = (List<GenericValue>)EmploymentsMap.get("employementList");
		employementList = EntityUtil.orderBy(employementList, UtilMisc.toList("partyIdTo"));
		monthTDS=0;
		tempMap=[:];
		employementList.each{employment->
			customTimePeriodTotals = PayrollService.getEmployeeSalaryTotalsForPeriod(dctx,UtilMisc.toMap("partyId",employment.partyId,"fromDate",start,"thruDate",end,"userLogin",userLogin)).get("periodTotalsForParty");
			if(UtilValidate.isNotEmpty(customTimePeriodTotals)){
				Iterator customTimePeriodIter = customTimePeriodTotals.entrySet().iterator();
				while(customTimePeriodIter.hasNext()){
					Map.Entry customTimePeriodEntry = customTimePeriodIter.next();
					if(customTimePeriodEntry.getKey() != "customTimePeriodTotals"){
						periodTotals = customTimePeriodEntry.getValue().get("periodTotals");
						grossBenefitAmt=periodTotals.get("grossBenefitAmt")
						if(UtilValidate.isNotEmpty(grossBenefitAmt) && grossBenefitAmt>0){
							incomeTax = periodTotals.get("PAYROL_DD_INC_TAX");
							if(UtilValidate.isNotEmpty(incomeTax) && -(incomeTax)>0){
								monthTDS=monthTDS-(incomeTax);
								tempMap.put("monthSno",monthSno);
								tempMap.put("tdsAmt",monthTDS);
								surcharge=0.00;
								educationCess=0.00;
								interest=0.00;
								others=0.00;
								totalTax=surcharge+educationCess+interest+others+monthTDS;
								tempMap.put("surcharge",surcharge);
								tempMap.put("chequeNo","");
								tempMap.put("bsrCode","6360218");
								tempMap.put("taxDeptDate","");
								tempMap.put("challanSerialNo","");
								tempMap.put("tdsBookEntry","N");
								tempMap.put("intersetAllocated","");
								tempMap.put("otherAmtAllocated","");
								tempMap.put("nilChallanIndicator","N");
								tempMap.put("remarkes","");
								tempMap.put("sectionCode","92B");
								tempMap.put("educationCess",educationCess);
								tempMap.put("totalTax",totalTax);
								tempMap.put("interest",interest);
								tempMap.put("others",others);
							}
						}
					}
				}
			}
			
		}
		finalList.add(tempMap);
		start=UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(UtilDateTime.toTimestamp(end), 1));
		monthSno=monthSno+1;
	}
	finalList = UtilMisc.sortMaps(finalList, UtilMisc.toList("partyId"));
	tempFinalList =[];
	for(int i=0;i<finalList.size();i++){
		Map tempMap = finalList.get(i);
		tempMap.put("serialNo",i+1);
		tempFinalList.add(tempMap);
	}
	context.finalList=tempFinalList;
}

