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
customTimePeriodTotals = PayrollService.getEmployeeSalaryTotalsForPeriod(dctx,UtilMisc.toMap("partyId",employeeId,"fromDate",fromDate,"thruDate",thruDate,"userLogin",userLogin)).get("periodTotalsForParty");
if(UtilValidate.isNotEmpty(customTimePeriodTotals)){
	Iterator customTimePeriodIter = customTimePeriodTotals.entrySet().iterator();
	while(customTimePeriodIter.hasNext()){
		Map.Entry customTimePeriodEntry = customTimePeriodIter.next();
		if(customTimePeriodEntry.getKey() != "customTimePeriodTotals"){
			Map periodTotalsMap = [:];
			periodTotals = customTimePeriodEntry.getValue().get("periodTotals");
			
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
		}else{
			grandTotals = customTimePeriodEntry.getValue();
		}
	}
}
context.putAt("partyBenefitsList", partyBenefitsList);

