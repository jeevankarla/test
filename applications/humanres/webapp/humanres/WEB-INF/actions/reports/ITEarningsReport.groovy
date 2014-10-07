import org.apache.avalon.framework.parameters.Parameters;
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

fromDate = parameters.fromDate;
thruDate = parameters.thruDate;
employeeId = parameters.employeeId;

context.putAt("fromDate", fromDate);
context.putAt("thruDate", thruDate);
context.putAt("employeeId", employeeId);

reportTypeFlag = parameters.reportTypeFlag;
if(UtilValidate.isNotEmpty(reportTypeFlag) && reportTypeFlag=="ITAXStatement"){
	customTimePeriodId=parameters.customTimePeriodId;
	List condList =[];
	condList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS ,"HR_MONTH"));
	condList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
	EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
	List<GenericValue> customTimePeriodList = delegator.findList("CustomTimePeriod", cond, null, null, null, false);
	if(UtilValidate.isNotEmpty(customTimePeriodList)){
		GenericValue customTimePeriod = EntityUtil.getFirst(customTimePeriodList);
		Date fromDate = (Date)customTimePeriod.get("fromDate");
		fromDateStart=UtilDateTime.toTimestamp(fromDate);
		Date thruDate = (Date)customTimePeriod.get("thruDate");
		thruDateEnd=UtilDateTime.toTimestamp(thruDate);
	}
	context.orderDate=UtilDateTime.nowTimestamp();
}

def sdf = new SimpleDateFormat("MMMM dd,yyyy");
try {
	if (fromDate) {
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(fromDate).getTime()));
	}
	if (thruDate) {
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(thruDate).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}

partyBenefitsList = [];
partyDeductionsList = [];
customTimePeriodTotals = PayrollService.getEmployeeSalaryTotalsForPeriod(dctx,UtilMisc.toMap("partyId",employeeId,"fromDate",fromDate,"thruDate",thruDate,"userLogin",userLogin)).get("periodTotalsForParty");
if(UtilValidate.isNotEmpty(customTimePeriodTotals)){
	Iterator customTimePeriodIter = customTimePeriodTotals.entrySet().iterator();
	while(customTimePeriodIter.hasNext()){
		Map.Entry customTimePeriodEntry = customTimePeriodIter.next();
		if(customTimePeriodEntry.getKey() != "customTimePeriodTotals"){
			Map periodTotalsMap = [:];
			periodTotals = customTimePeriodEntry.getValue().get("periodTotals");
			if(UtilValidate.isNotEmpty(reportTypeFlag) && reportTypeFlag == "ITEarningsReport"){
				basic = 0;
				attendanceBonus =0;
				cityComp =0;
				convey =0;
				coldAllowance =0;
				dearnessAllowance =0;
				holidayAllowance =0;
				houseRentAllowance =0;
				personalPay =0;
				secndSatDay =0;
				shift =0;
				washing =0;
				others = 0;
				totalBenefits = 0;
				
				
				basic = periodTotals.get("PAYROL_BEN_SALARY");
				if(UtilValidate.isEmpty(basic)){
					basic = 0;
				}
				attendanceBonus = periodTotals.get("PAYROL_BEN_ATNDBON");
				if(UtilValidate.isEmpty(attendanceBonus)){
					attendanceBonus = 0;
				}
				cityComp = periodTotals.get("PAYROL_BEN_CITYCOMP");
				if(UtilValidate.isEmpty(cityComp)){
					cityComp = 0;
				}
				convey = periodTotals.get("PAYROL_BEN_CONVEY");
				if(UtilValidate.isEmpty(convey)){
					convey = 0;
				}
				coldAllowance = periodTotals.get("PAYROL_BEN_COLDALLOW");
				if(UtilValidate.isEmpty(coldAllowance)){
					coldAllowance = 0;
				}
				dearnessAllowance = periodTotals.get("PAYROL_BEN_DA");
				if(UtilValidate.isEmpty(dearnessAllowance)){
					dearnessAllowance = 0;
				}
				holidayAllowance = periodTotals.get("PAYROL_BEN_GEN_HOL_W");
				if(UtilValidate.isEmpty(holidayAllowance)){
					holidayAllowance = 0;
				}
				houseRentAllowance = periodTotals.get("PAYROL_BEN_HRA");
				if(UtilValidate.isEmpty(houseRentAllowance)){
					houseRentAllowance = 0;
				}
				personalPay = periodTotals.get("PAYROL_BEN_PERS_PAY");
				if(UtilValidate.isEmpty(personalPay)){
					personalPay = 0;
				}
				secndSatDay = periodTotals.get("PAYROL_BEN_SECSATDAY");
				if(UtilValidate.isEmpty(secndSatDay)){
					secndSatDay = 0;
				}
				shift = periodTotals.get("PAYROL_BEN_SHIFT");
				if(UtilValidate.isEmpty(shift)){
					shift = 0;
				}
				washing = periodTotals.get("PAYROL_BEN_WASHG");
				if(UtilValidate.isEmpty(washing)){
					washing = 0;
				}
				others = attendanceBonus+coldAllowance+holidayAllowance+personalPay+secndSatDay+shift+washing;
				totalBenefits = others+basic+cityComp+convey+dearnessAllowance+houseRentAllowance;
				
				tempMap = [:];
				tempMap["basic"] = basic;
				tempMap["dearnessAllowance"] = dearnessAllowance;
				tempMap["houseRentAllowance"] = houseRentAllowance;
				tempMap["convey"] = convey;
				tempMap["cityComp"] = cityComp;
				tempMap["others"] = others;
				tempMap["totalBenefits"] = totalBenefits;
				
				benefitsMap = [:];
				benefitsMap.putAll(tempMap);
				
				partyBenefitsMap = [:];
				partyBenefitsMap.put(customTimePeriodEntry.getKey(),benefitsMap);
				partyBenefitsList.addAll(partyBenefitsMap);
			}
			if(UtilValidate.isNotEmpty(reportTypeFlag) && reportTypeFlag == "ITDeductionsReport"){
				
				epf = 0;
				vpf =0;
				gsls =0;
				licp =0;
				frf =0;
				nsc =0;
				ppf =0;
				gsas =0;
				hba =0;
				canf =0;
				hdfc =0;
				hbac =0;
				incomeTax = 0;
				prfTax = 0;
				
				totalFRFNSC = 0;
				totalPPFGSAS = 0;
				totalExterLoan = 0;
				
				totalDeductions = 0;
				
				epf = periodTotals.get("PAYROL_DD_EMP_PR");
				if(UtilValidate.isEmpty(epf)){
					epf = 0;
				}
				vpf = periodTotals.get("PAYROL_DD_VLNT_PR");
				if(UtilValidate.isEmpty(vpf)){
					vpf = 0;
				}
				gsls = periodTotals.get("PAYROL_DD_GR_SAVG");
				if(UtilValidate.isEmpty(gsls)){
					gsls = 0;
				}
				licp = periodTotals.get("PAYROL_DD_LIC_PGS");
				if(UtilValidate.isEmpty(licp)){
					licp = 0;
				}
				frf = periodTotals.get("PAYROL_DD_FD_RELF");
				if(UtilValidate.isEmpty(frf)){
					frf = 0;
				}
				nsc = periodTotals.get("PAYROL_DD_NATINAL");
				if(UtilValidate.isEmpty(nsc)){
					nsc = 0;
				}
				ppf = periodTotals.get("PAYROL_DD_PERSNL_PR");
				if(UtilValidate.isEmpty(ppf)){
					ppf = 0;
				}
				gsas = periodTotals.get("PAYROL_DD_GR_SUP");
				if(UtilValidate.isEmpty(gsas)){
					gsas = 0;
				}
				hba = periodTotals.get("PAYROL_DD_HUSE_ADV");
				if(UtilValidate.isEmpty(hba)){
					hba = 0;
				}
				canf = periodTotals.get("PAYROL_DD_CANFN_HO");
				if(UtilValidate.isEmpty(canf)){
					canf = 0;
				}
				/*hdfc = periodTotals.get("PAYROL_BEN_SHIFT");
				if(UtilValidate.isEmpty(hdfc)){
					hdfc = 0;
				}*/
				hbac = periodTotals.get("PAYROL_DD_HUSE_B");
				if(UtilValidate.isEmpty(hbac)){
					hbac = 0;
				}
				incomeTax = periodTotals.get("PAYROL_DD_INC_TAX");
				if(UtilValidate.isEmpty(incomeTax)){
					incomeTax = 0;
				}
				prfTax = periodTotals.get("PAYROL_DD_PR_TAX");
				if(UtilValidate.isEmpty(prfTax)){
					prfTax = 0;
				}
				
				totalFRFNSC = frf+nsc;
				totalPPFGSAS = ppf+gsas;
				totalExterLoan = hba+canf+hbac;
				totalDeductions = totalFRFNSC+totalPPFGSAS+totalExterLoan+epf+vpf+gsls+licp;
				
				tempMap = [:];
				tempMap["epf"] = -(epf);
				tempMap["vpf"] = -(vpf);
				tempMap["gsls"] = -(gsls);
				tempMap["licp"] = -(licp);
				tempMap["incomeTax"] = -(incomeTax);
				tempMap["prfTax"] = -(prfTax);
				tempMap["totalFRFNSC"] = -(totalFRFNSC);
				tempMap["totalPPFGSAS"] = -(totalPPFGSAS);
				tempMap["totalExterLoan"] = -(totalExterLoan);
				tempMap["totalDeductions"] = -(totalDeductions);
				
				deductionsMap = [:];
				deductionsMap.putAll(tempMap);
				
				partyDeductionsMap = [:];
				partyDeductionsMap.put(customTimePeriodEntry.getKey(),deductionsMap);
				partyDeductionsList.addAll(partyDeductionsMap);
			}
			
		}else{
			grandTotals = customTimePeriodEntry.getValue();
		}
	}
}
context.putAt("partyBenefitsList", partyBenefitsList);
context.putAt("partyDeductionsList", partyDeductionsList);

//IncomeTax Statement
if(UtilValidate.isNotEmpty(reportTypeFlag) && reportTypeFlag=="ITAXStatement"){
context.fromDate=fromDateStart;
emplInputMap = [:];
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("orgPartyId", "Company");
emplInputMap.put("fromDate", fromDateStart);
emplInputMap.put("thruDate", thruDateEnd);
Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
List<GenericValue> employementList = (List<GenericValue>)EmploymentsMap.get("employementList");
employementList = EntityUtil.orderBy(employementList, UtilMisc.toList("partyIdTo"));
ITAXFinalList=[];
grandTotal=0;
employementList.each{employment->
	String lastName="";
	if(employment.lastName!=null){
		lastName=employment.lastName;
	}
	name=employment.firstName+" "+lastName;
	customTimePeriodTotals = PayrollService.getEmployeeSalaryTotalsForPeriod(dctx,UtilMisc.toMap("partyId",employment.partyId,"fromDate",fromDateStart,"thruDate",thruDateEnd,"userLogin",userLogin)).get("periodTotalsForParty");
	if(UtilValidate.isNotEmpty(customTimePeriodTotals)){
		Iterator customTimePeriodIter = customTimePeriodTotals.entrySet().iterator();
		while(customTimePeriodIter.hasNext()){
			tempMap=[:];
			Map.Entry customTimePeriodEntry = customTimePeriodIter.next();
			if(customTimePeriodEntry.getKey() != "customTimePeriodTotals"){
				periodTotals = customTimePeriodEntry.getValue().get("periodTotals");
				tempMap.put("name",name);
				tempMap.put("partyId",employment.partyId);
				panId=employment.panId;
				if(UtilValidate.isEmpty(panId)){
					panId="";
				}
				tempMap.put("panId",panId);
				incomeTax = periodTotals.get("PAYROL_DD_INC_TAX");
				if(UtilValidate.isEmpty(incomeTax)){
					incomeTax = 0;
				}
				grandTotal=grandTotal-(incomeTax);
				tempMap.put("incomeTax",-(incomeTax));
				ITAXFinalList.add(tempMap);
			}	
		}		
	}			
				
}
context.grandTotal=grandTotal;
context.ITAXFinalList=ITAXFinalList;
}



