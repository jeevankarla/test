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

