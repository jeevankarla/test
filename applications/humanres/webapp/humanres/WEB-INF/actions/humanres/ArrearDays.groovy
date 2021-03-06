import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.RowFilter.NotFilter;

import org.ofbiz.base.util.UtilDateTime;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.byproducts.ByProductServices;

dctx = dispatcher.getDispatchContext();
EditArrearDays=[];
EditArrearDaysMap=[:];
timePeriodId=parameters.customTimePeriodId;
		if(UtilValidate.isNotEmpty(timePeriodId)){
			dates=delegator.findOne("CustomTimePeriod", [customTimePeriodId:timePeriodId], false);
			fromDate=UtilDateTime.toDateString(dates.get("fromDate"), "MMM dd, yyyy");
			thruDate=UtilDateTime.toDateString(dates.get("thruDate"), "MMM dd, yyyy");
			fromDateStart = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
			thruDateEnd= UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
			def sdf = new SimpleDateFormat("MMMM dd, yyyy");
			try {
				if (fromDate) {
					fromDateStart = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(fromDate).getTime()));
				}
				if (thruDate) {
					thruDateEnd = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(thruDate).getTime()));
				}
			} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + e, "");
			context.errorMessage = "Cannot parse date string: " + e;
			return;
			}
			partyId=parameters.partyId;
			resultMap=PayrollService.getPayrollAttedancePeriod(dctx,[userLogin:userLogin,timePeriodStart:fromDateStart,timePeriodEnd:thruDateEnd,timePeriodId:timePeriodId,locale:locale]);
			lastClosePeriod=resultMap.get("lastCloseAttedancePeriod");
			if(UtilValidate.isNotEmpty(lastClosePeriod)){
			customTimePeriodId=lastClosePeriod.get("customTimePeriodId");
			EditArrearDaysMap.put("partyId",partyId);
			nameList=delegator.findByAnd("PartyRelationshipAndDetail", [partyId: partyId],["firstName","middleName","lastName"]);
			lastName=nameList.get(0).lastName;
			if(lastName==null){
				lastName="";
			}
			context.put("name", nameList.get(0).firstName + " " + lastName);
			payrollAttendance=delegator.findOne("PayrollAttendance",[partyId:partyId,customTimePeriodId:customTimePeriodId],false);
			if(UtilValidate.isNotEmpty(payrollAttendance)){
				payrollPeriodId=payrollAttendance.get("customTimePeriodId");
				EditArrearDaysMap.put("customTimePeriodId",payrollPeriodId);
				noOfPayableDays=payrollAttendance.get("noOfPayableDays");
				if(noOfPayableDays==null)
				noOfPayableDays=0;
				EditArrearDaysMap.put("noOfPayableDays",noOfPayableDays);
				noOfArrearDays=payrollAttendance.get("noOfArrearDays");
				if(noOfArrearDays==null)
				noOfArrearDays=0;
				lossOfPayDays=payrollAttendance.get("lossOfPayDays");
				if(lossOfPayDays==null)
				lossOfPayDays=0;
				lateMin=payrollAttendance.get("lateMin");
				if(lateMin==null)
				lateMin=0;
				lateMin=lateMin*480;
				noOfAttendedSsDays=payrollAttendance.get("noOfAttendedSsDays");
				if(noOfAttendedSsDays==null)
				noOfAttendedSsDays=0;
				noOfAttendedHoliDays=payrollAttendance.get("noOfAttendedHoliDays");
				if(noOfAttendedHoliDays==null)
				noOfAttendedHoliDays=0;
				EditArrearDaysMap.put("noOfAttendedSsDays",noOfAttendedSsDays);
				EditArrearDaysMap.put("noOfAttendedHoliDays",noOfAttendedHoliDays);
				EditArrearDaysMap.put("noOfArrearDays",noOfArrearDays);
				EditArrearDaysMap.put("lossOfPayDays",lossOfPayDays);
				EditArrearDaysMap.put("lateMin",lateMin);
				EditArrearDays.add(EditArrearDaysMap);
				EditArrearDaysMap.put("timePeriodId", timePeriodId);
				enumeration = delegator.findOne("Enumeration",[enumId:payrollAttendance.get("remarks")],false);
				if(UtilValidate.isNotEmpty(enumeration)){
					EditArrearDaysMap.put("enumId", payrollAttendance.get("remarks"));
					EditArrearDaysMap.put("remarks", enumeration.get("description"));
				}
			}
			}
			context.put("EditArrearDays",EditArrearDays);
			
		}
		

