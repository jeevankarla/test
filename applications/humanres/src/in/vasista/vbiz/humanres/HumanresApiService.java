package in.vasista.vbiz.humanres;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.security.Security;

public class HumanresApiService {

    public static final String module = HumanresApiService.class.getName();

    
    static List getEmployeePayslips(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();   
        List result = FastList.newInstance();
        String employeeId = (String) context.get("employeeId");
        try{		
        	Map <String, String> payheadsMap = FastMap.newInstance();
        	List<GenericValue> benefitTypes = delegator.findList("BenefitType",null, null,null, null, true);
        	for (int i = 0; i < benefitTypes.size(); ++i) {
        		GenericValue benefitType = benefitTypes.get(i);
        		payheadsMap.put(benefitType.getString("benefitTypeId"), benefitType.getString("benefitName"));
        	}
        	List<GenericValue> deductionTypes = delegator.findList("DeductionType",null, null,null, null, true);
        	for (int i = 0; i < deductionTypes.size(); ++i) {
        		GenericValue deductionType = deductionTypes.get(i);
        		payheadsMap.put(deductionType.getString("deductionTypeId"), deductionType.getString("deductionName"));
        	}       	
        	List conditionList = UtilMisc.toList(
				EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS ,"PAYROLL_BILL"));		
        	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS ,"APPROVED"));
        	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
        	List<GenericValue> periodBillingList = delegator.findList("PeriodBilling", condition, null, null, null, false);

        	List periodIds = EntityUtil.getFieldListFromEntityList(periodBillingList, "customTimePeriodId", true);

        	conditionList.clear();
        	conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.IN, periodIds));
        	condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
        	List customTimePeriods = delegator.findList("CustomTimePeriod", condition, null, UtilMisc.toList("-thruDate"), null, true);
        	for (int i = 0; i < customTimePeriods.size(); ++i) {
        		GenericValue period = (GenericValue)customTimePeriods.get(i);
        		String periodId = period.getString("customTimePeriodId");
            	conditionList.clear();
            	conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS ,"PAYROLL_BILL"));            	
            	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS ,"APPROVED"));
            	conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, periodId));
            	condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
            	List periodBillings = delegator.findList("PeriodBilling", condition, null, null, null, false);
        		GenericValue periodBilling = EntityUtil.getFirst(periodBillings);
        		String periodBillingId = periodBilling.getString("periodBillingId");
            	conditionList.clear();
            	conditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS, periodBillingId));
            	conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, employeeId));
            	condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
Debug.logInfo("getEmployeePayslips: condition->" + condition, module);		             	
            	List payrollHeaders = delegator.findList("PayrollHeader", condition, null, null, null, false);
            	if (payrollHeaders.size() > 0)  {
            		GenericValue payrollHeader = EntityUtil.getFirst(payrollHeaders);      
            		Map<String, Object> payroll = FastMap.newInstance();
            		String payrollHeaderId = payrollHeader.getString("payrollHeaderId");
            		payroll.put("payrollHeaderId", payrollHeaderId);
            		Date thruDate = period.getDate("thruDate");
            		String payrollPeriod = UtilDateTime.toDateString(thruDate ,"MMM yyyy");            	
            		payroll.put("payrollPeriod", payrollPeriod);
            		payroll.put("payrollDate", thruDate);            		
            		BigDecimal netAmount = BigDecimal.ZERO;
            		List payrollItems = FastList.newInstance();
            		conditionList.clear();
            		conditionList.add(EntityCondition.makeCondition("payrollHeaderId", EntityOperator.EQUALS, payrollHeaderId));
            		condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
            		List payrollHeaderItems = delegator.findList("PayrollHeaderItem", condition, null, null, null, false);
            		for (int j = 0; j < payrollHeaderItems.size(); ++j) {
            			Map<String, Object> payrollItem = FastMap.newInstance();
            			GenericValue payrollHeaderItem = (GenericValue)payrollHeaderItems.get(j);
            			BigDecimal amount = payrollHeaderItem.getBigDecimal("amount");
            			netAmount = netAmount.add(amount);
            			String payrollHeaderItemType = (payrollHeaderItem.getString("payrollHeaderItemTypeId"));
            			if (payheadsMap.get(payrollHeaderItem.getString("payrollHeaderItemTypeId")) != null) {
            				payrollHeaderItemType = payheadsMap.get(payrollHeaderItem.getString("payrollHeaderItemTypeId"));
            			}
            			payrollItem.put(payrollHeaderItemType, amount);
            			payrollItems.add(payrollItem);
            		}
            		payroll.put("netAmount", netAmount);            	
            		payroll.put("payrollItems", payrollItems);
            		result.add(payroll);
            	}
        	}
        } catch (Exception e) {
        	Debug.logError(e, "Error fetching employee payslips", module);
        }
        
    	return result;
    }
    
    /*
     * Helper that returns full employee profile.  This method expects the employee's EmploymentAndPerson
     * record as an input.
     */
    static Map<String, Object> getEmployeeProfile(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();    
    	Map<String, Object> employee = FastMap.newInstance();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        GenericValue employment = (GenericValue) context.get("employment");
        if (UtilValidate.isEmpty(employment)) {
        	return employee;
        }
		GenericValue dept;
		try {
			dept = delegator.findByPrimaryKey("PartyAndGroup", UtilMisc.toMap("partyId", employment.getString("partyIdFrom")));	
			if (UtilValidate.isNotEmpty(dept)) {
				employee.put("department", dept.getString("groupName"));
			}
			String employeePosition = "";
			List<GenericValue> emplPositionAndFulfillments = EntityUtil.filterByDate(delegator.findByAnd("EmplPositionAndFulfillment", UtilMisc.toMap("employeePartyId", employment.getString("partyId"))));
			GenericValue emplPositionAndFulfillment = EntityUtil.getFirst(emplPositionAndFulfillments);
			if(UtilValidate.isNotEmpty(emplPositionAndFulfillment) && emplPositionAndFulfillment.getString("emplPositionTypeId") != null){
				GenericValue emplPositionType = delegator.findOne("EmplPositionType",UtilMisc.toMap("emplPositionTypeId", emplPositionAndFulfillment.getString("emplPositionTypeId")), true);
				if(UtilValidate.isNotEmpty(emplPositionAndFulfillment) && UtilValidate.isNotEmpty(emplPositionAndFulfillment.getString("name"))){
					employeePosition = emplPositionAndFulfillment.getString("name");
				}else if (emplPositionType != null) {
					employeePosition = emplPositionType.getString("description");
				}
				else {
					employeePosition = emplPositionAndFulfillment.getString("emplPositionId");
				}
			}
			employee.put("position", employeePosition);
			String lastName="";
			if(employment.getString("lastName") !=null){
				lastName = employment.getString("lastName");
			}
			employee.put("name", employment.getString("firstName") + " " + lastName);
			employee.put("employeeId", employment.getString("partyId"));
			String joinDate = UtilDateTime.toDateString(employment.getTimestamp("appointmentDate"), "yyyy-MM-dd");
			employee.put("joinDate", joinDate);
		
			Map <String, Object>partyTelephone= dispatcher.runSync("getPartyTelephone", UtilMisc.toMap("partyId", employment.getString("partyId"), 
					"userLogin", userLogin));
			String phoneNumber = "";
			if (partyTelephone != null && partyTelephone.get("contactNumber") != null) {
				phoneNumber = (String)partyTelephone.get("contactNumber");
			}
			employee.put("phoneNumber", phoneNumber);  
	    	Map leaveBalancesResult = EmplLeaveService.getEmployeeLeaveBalance(dctx, UtilMisc.toMap("employeeId", employment.getString("partyId")));
	    	if (UtilValidate.isNotEmpty(leaveBalancesResult)) {
	    		if (leaveBalancesResult.get("leaveBalanceDate") != null) {
    				employee.put("leaveBalanceDate", leaveBalancesResult.get("leaveBalanceDate"));
	    			Map leaveBalances = (Map)leaveBalancesResult.get("leaveBalances");
	    			if (leaveBalances != null) {
	    				employee.put("earnedLeaveBalance", leaveBalances.get("EL"));
	    				employee.put("casualLeaveBalance", leaveBalances.get("CL"));
	    				employee.put("halfPayLeaveBalance", leaveBalances.get("HPL"));
	    			}
	    		}
	    	}	
    		String emplWeeklyOffDay = "SUNDAY";
    		GenericValue employeeDetail = delegator.findOne("EmployeeDetail", UtilMisc.toMap("partyId",employment.getString("partyId")), false);
	        if(UtilValidate.isNotEmpty(employeeDetail) && UtilValidate.isNotEmpty(employeeDetail.getString("weeklyOff"))){
	        	emplWeeklyOffDay = employeeDetail.getString("weeklyOff");
	        }
			employee.put("weeklyOff", emplWeeklyOffDay);  	        
		} catch(Exception e){
			Debug.logError("Error fetching employee profile " + e.getMessage(), module);
		}
		return employee;
    }
    
	static void populateOrgEmployees(DispatchContext dctx, Map<String, ? extends Object> context, List employeeList) {
    	Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();    	    	
        GenericValue userLogin = (GenericValue) context.get("userLogin");		
        GenericValue org = (GenericValue) context.get("org");		
        if (org == null) {
        	return;
        }
		List<GenericValue> internalOrgs = FastList.newInstance();
  		try{
  			internalOrgs = EntityUtil.filterByDate(delegator.findByAnd("PartyRelationshipAndDetail", UtilMisc.toMap("partyIdFrom", org.getString("partyId"),
				"partyRelationshipTypeId", "GROUP_ROLLUP"),UtilMisc.toList("groupName")));
  			for(GenericValue internalOrg : internalOrgs){
  				Map<String, Object> inputParamMap = FastMap.newInstance();
  				inputParamMap.put("userLogin", userLogin);			  				
  				inputParamMap.put("org", internalOrg);
  				populateOrgEmployees(dctx, inputParamMap, employeeList);
  			}
			List<GenericValue> employments = EntityUtil.filterByDate(delegator.findByAnd("EmploymentAndPerson", UtilMisc.toMap("partyIdFrom", org.getString("partyId"), 
					"roleTypeIdTo", "EMPLOYEE"), UtilMisc.toList("firstName")));
			for(GenericValue employment : employments){
				Map<String, Object> employee = FastMap.newInstance();	
				
				Map<String, Object> inputParamMap = FastMap.newInstance();
				inputParamMap.put("userLogin", userLogin);			
				inputParamMap.put("employment", employment);
				employee = getEmployeeProfile(dctx, inputParamMap);
				if (UtilValidate.isNotEmpty(employee)) {
					employeeList.add(employee);					
				}
			}
  		}catch(GenericEntityException e){
  			Debug.logError("Error fetching employees " + e.getMessage(), module);
  		}
		catch (Exception e) {
  			Debug.logError("Error fetching employees " + e.getMessage(), module);
		}  		
	}


	
    public static Map<String, Object> getActiveEmployees(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();    	
        GenericValue userLogin = (GenericValue) context.get("userLogin");		
        Security security = dctx.getSecurity();
        // security check
        if (!security.hasEntityPermission("MOB_EMPLOYEES", "_VIEW", userLogin)) {
            Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to fetch employees!", module);
            return ServiceUtil.returnError("You do not have permission for this transaction.");
        }    	
		List employeeList = FastList.newInstance();        
		try {
			GenericValue company = delegator.findByPrimaryKey("PartyAndGroup", UtilMisc.toMap("partyId", "Company"));
			Map<String, Object> inputParamMap = FastMap.newInstance();
			inputParamMap.put("userLogin", userLogin);			
			inputParamMap.put("org", company);
			populateOrgEmployees(dctx, inputParamMap, employeeList);	
		}catch(GenericEntityException e){
  			Debug.logError("Error fetching employees " + e.getMessage(), module);
  		}
    	Map result = FastMap.newInstance();  
    	Map employeeMap = FastMap.newInstance();  
    	employeeMap.put("employeeList", employeeList);
		result.put("employeesResult", employeeMap);
Debug.logInfo("result:" + result, module);		 
    	return result;
    }      
    
    public static Map<String, Object> fetchEmployeeDetails(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();    	
        GenericValue userLogin = (GenericValue) context.get("userLogin");		
        Security security = dctx.getSecurity();
        // security check
        if (!security.hasEntityPermission("MOB_MYEMPLOYEE", "_VIEW", userLogin)) {
            Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to fetch employees!", module);
            return ServiceUtil.returnError("You do not have permission for this transaction.");
        }    	
        
    	if (userLogin == null || userLogin.get("partyId") == null) {
            Debug.logWarning("**** INVALID PARTY [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " not mapped to a party!", module);
            return ServiceUtil.returnError("Valid employee code not found.");    		
    	}
		Map<String, Object> employeeProfile = FastMap.newInstance();   
		List payslips = FastList.newInstance();
    	String employeeId = (String)userLogin.get("partyId");
		try {    	
			List<GenericValue> employments = EntityUtil.filterByDate(delegator.findByAnd("EmploymentAndPerson", UtilMisc.toMap("partyIdTo", employeeId, 
				"roleTypeIdTo", "EMPLOYEE"), null));    	
			if (employments.size() == 0) {
				Debug.logWarning("**** INVALID PARTY [" + (new Date()).toString() + "]: " + employeeId + " does not have an active employment!", module);
				return ServiceUtil.returnError("Active employment not found.");  
			}
         
			Map<String, Object> inputParamMap = FastMap.newInstance();
			inputParamMap.put("userLogin", userLogin);			
			inputParamMap.put("employment", EntityUtil.getFirst(employments));
			employeeProfile = getEmployeeProfile(dctx, inputParamMap);

			inputParamMap.clear();
			inputParamMap.put("userLogin", userLogin);			
			inputParamMap.put("employeeId", employeeId);		
			payslips = getEmployeePayslips(dctx, inputParamMap);			
		} catch(Exception e){
			Debug.logError("Error fetching employee details " + e.getMessage(), module);
		}     
		
    	Map result = FastMap.newInstance(); 	
		Map<String, Object> employeeDetailsMap = FastMap.newInstance();    	    	
    	employeeDetailsMap.put("employeeProfile", employeeProfile);    
    	employeeDetailsMap.put("payslips", payslips);    	
    	
    	result.put("employeeDetailsResult", employeeDetailsMap);
Debug.logInfo("result:" + result, module);		 
    	return result;
    }          

    /*
     * Fetches all leaves applied since last 45 days for given employee
     */
    public static Map<String, Object> fetchEmployeeRecentLeaves(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();    	
        GenericValue userLogin = (GenericValue) context.get("userLogin");		
        Security security = dctx.getSecurity();
        // security check
        if (!security.hasEntityPermission("MOB_MYEMPLOYEE", "_VIEW", userLogin)) {
            Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to fetch employees!", module);
            return ServiceUtil.returnError("You do not have permission for this transaction.");
        }    	
        
    	if (userLogin == null || userLogin.get("partyId") == null) {
            Debug.logWarning("**** INVALID PARTY [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " not mapped to a party!", module);
            return ServiceUtil.returnError("Valid employee code not found.");    		
    	}
		List leaves = FastList.newInstance();        
    	String partyId = (String)userLogin.get("partyId");
    	Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
    	Timestamp yearStart = UtilDateTime.getYearStart(nowTimestamp);
		Timestamp fromDate = UtilDateTime.addDaysToTimestamp(yearStart, -31);

		try{    	
			List conditionList = UtilMisc.toList(
	            EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);  		
			List<GenericValue> emplLeaves = delegator.findList("EmplLeave", condition, null, null, null, false);
			for (int i = 0; i < emplLeaves.size(); ++i) {
		    	Map<String, Object> leave = FastMap.newInstance();
				GenericValue emplLeave = emplLeaves.get(i);
				leave.put("leaveTypeId", emplLeave.getString("leaveTypeId"));
				String leaveFromDate = UtilDateTime.toDateString(emplLeave.getTimestamp("fromDate"), "yyyy-MM-dd");
				String leaveThruDate = UtilDateTime.toDateString(emplLeave.getTimestamp("thruDate"), "yyyy-MM-dd");	
				String leaveStatus;
				GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", emplLeave.getString("leaveStatus")),true); 
		        if (statusItem != null) {
		        	leaveStatus = statusItem.getString("description");
		        } else {
		        	leaveStatus = emplLeave.getString("leaveStatus"); 
		        }
				leave.put("leaveStatus", leaveStatus);				
				leave.put("leaveFromDate", leaveFromDate);
				leave.put("leaveThruDate", leaveThruDate);		
				leaves.add(leave);
			}
		
		} catch(Exception e){
			Debug.logError("Error fetching employee leaves " + e.getMessage(), module);
		} 
		Map<String, Object> employeeLeavesMap = FastMap.newInstance();  
		employeeLeavesMap.put("employeeId", partyId);    	 		
		employeeLeavesMap.put("recentLeaves", leaves);    	 
    	Map leaveBalancesResult = EmplLeaveService.getEmployeeLeaveBalance(dctx, UtilMisc.toMap("employeeId", partyId));
    	if (UtilValidate.isNotEmpty(leaveBalancesResult)) {
    		if (leaveBalancesResult.get("leaveBalanceDate") != null) {
    			employeeLeavesMap.put("leaveBalanceDate", leaveBalancesResult.get("leaveBalanceDate"));
    			Map leaveBalances = (Map)leaveBalancesResult.get("leaveBalances");
    			if (leaveBalances != null) {
    				employeeLeavesMap.put("earnedLeaveBalance", leaveBalances.get("EL"));
    				employeeLeavesMap.put("casualLeaveBalance", leaveBalances.get("CL"));
    				employeeLeavesMap.put("halfPayLeaveBalance", leaveBalances.get("HPL"));
    			}
    		}
    	}			
    	Map result = FastMap.newInstance(); 	
    	result.put("employeeLeavesResult", employeeLeavesMap);	
Debug.logInfo("result:" + result, module);		 
    	return result;
    }    
    
    /*
     * Fetches all attendance records since last 45 days for given employee.
     * Note: Only "Normal" punchtype entries are currently handled
     */
    public static Map<String, Object> fetchEmployeeAttendance(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();    	
        GenericValue userLogin = (GenericValue) context.get("userLogin");		
        Security security = dctx.getSecurity();
        // security check
        if (!security.hasEntityPermission("MOB_MYEMPLOYEE", "_VIEW", userLogin)) {
            Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to fetch employees!", module);
            return ServiceUtil.returnError("You do not have permission for this transaction.");
        }    	
        
    	if (userLogin == null || userLogin.get("partyId") == null) {
            Debug.logWarning("**** INVALID PARTY [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " not mapped to a party!", module);
            return ServiceUtil.returnError("Valid employee code not found.");    		
    	}
    	SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    	SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    	String partyId = (String)userLogin.get("partyId");
		List punches = FastList.newInstance();        
    	Timestamp fromDate = UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(), -45));
		try{  
			List conditionList = UtilMisc.toList(
		            EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				conditionList.add(EntityCondition.makeCondition("punchdate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(fromDate)));    	
	    	// currently other punctypes such as OOD are not handled
	    	conditionList.add(EntityCondition.makeCondition("PunchType", EntityOperator.EQUALS , "Normal")); 
	    	conditionList.add(EntityCondition.makeCondition("partyId", partyId));
	
	    	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	    	List<GenericValue> punchList = delegator.findList("EmplPunch", condition, null, UtilMisc.toList("-punchdate","-punchtime"), null, false);
	    	String outTimestamp = "";    	
	    	for (int i = 0; i < punchList.size(); ++i) {
		    	Map<String, Object> emplPunch = FastMap.newInstance();
				GenericValue punch = punchList.get(i); 
	    		String punchTime = timeFormat.format(punch.get("punchtime"));
	    		String inOut = "";
	    		if (UtilValidate.isNotEmpty(punch.getString("InOut"))) {
	    			inOut = punch.getString("InOut");
	    		}
	    		if (inOut.equals("OUT")) {
	    			outTimestamp = UtilDateTime.toDateString((Date)punch.get("punchdate"), "dd/MM/yy") + "  " + punchTime;
	    			continue;
	    		}
	    		if (inOut.equals("IN")) {
	    			String inTimestamp = UtilDateTime.toDateString((Date)punch.get("punchdate"), "dd/MM/yy") + "  " + punchTime;
					emplPunch.put("inTimestamp", inTimestamp);
	    			if (outTimestamp.isEmpty()) {
	    				emplPunch.put("outTimestamp", "");
	    				emplPunch.put("duration", "");
	    			}
	    			else {
	    				emplPunch.put("outTimestamp", outTimestamp);    				
	    				double elapsedHours = UtilDateTime.getInterval(new java.sql.Timestamp(dateTimeFormat.parse(inTimestamp).getTime()), 
	    									new java.sql.Timestamp(dateTimeFormat.parse(outTimestamp).getTime()))/(1000*60*60);
	    				emplPunch.put("duration", String.format( "%.2f", elapsedHours ));
	    			}
	    			punches.add(emplPunch);
	    			outTimestamp = "";
	    		}
	    	}
		} catch(Exception e){
			Debug.logError("Error fetching employee attendance " + e.getMessage(), module);
		}
		Map<String, Object> employeeLeavesMap = FastMap.newInstance();  
		employeeLeavesMap.put("employeeId", partyId);    	 		
		employeeLeavesMap.put("recentPunches", punches); 
		
    	Map result = FastMap.newInstance(); 	
    	result.put("employeeAttendanceResult", employeeLeavesMap);	
Debug.logInfo("result:" + result, module);		 
    	return result;   	
    }
   
    /*
     * Fetch last punch for given employee.  Currently this will return only today's last punch
     * Note: Only "Normal" punchtype entries are considered
     */
    public static Map<String, Object> fetchEmployeeLastPunch(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();    	
        GenericValue userLogin = (GenericValue) context.get("userLogin");		
        Security security = dctx.getSecurity();
        // security check
        if (!security.hasEntityPermission("MOB_PUNCH", "_VIEW", userLogin)) {
            Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to fetch employees!", module);
            return ServiceUtil.returnError("You do not have permission for this transaction.");
        }   
        String partyId = (String)context.get("partyId");
        if(UtilValidate.isEmpty(context.get("partyId"))){
            Debug.logWarning("**** INVALID PARTY [" + (new Date()).toString() + "]: " + "party Id missing!", module);
            return ServiceUtil.returnError("Party Id is missing.");         
        }
        Timestamp timePeriodStart = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
        Timestamp timePeriodEnd = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());        
    	SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    	EntityListIterator punchIter = null;
    	Map<String, Object> emplPunch = FastMap.newInstance();
		try {
			List condList = FastList.newInstance();
			condList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			condList.add(EntityCondition.makeCondition("PunchType", EntityOperator.EQUALS , "Normal")); 
			condList.add(EntityCondition.makeCondition("punchdate", EntityOperator.GREATER_THAN_EQUAL_TO , UtilDateTime.toSqlDate(timePeriodStart)));
			condList.add(EntityCondition.makeCondition("punchdate", EntityOperator.LESS_THAN_EQUAL_TO , UtilDateTime.toSqlDate(timePeriodEnd)));			
			EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			
			punchIter = delegator.find("EmplPunch", cond, null, null, UtilMisc.toList("-punchdate","-punchtime"),null);
	        GenericValue punch;
	    	if( punchIter != null && (punch = punchIter.next()) != null) {
    			String punchTime = UtilDateTime.toDateString((Date)punch.get("punchdate"), "dd/MM/yy") + "  " 
    				+ timeFormat.format(punch.get("punchtime"));
				emplPunch.put("punchTime", punchTime);
	    		String inOut = "";
	    		if (UtilValidate.isNotEmpty(punch.getString("InOut"))) {
	    			inOut = punch.getString("InOut");
	    		}
				emplPunch.put("inOut", inOut);	    		
	    	}
        } catch (Exception e) {
        	Debug.logError(e, module);
        }
        finally {
            if (punchIter != null) {
                try {
                	punchIter.close();
                } catch (GenericEntityException e) {
                    Debug.logWarning(e, module);
                }
            }
        }
		Map<String, Object> employeeLastPunchMap = FastMap.newInstance();  
		employeeLastPunchMap.put("employeeId", partyId);    	 		
		employeeLastPunchMap.put("punch", emplPunch); 
		
    	Map result = FastMap.newInstance(); 	
    	result.put("employeeLastPunchResult", employeeLastPunchMap);	
Debug.logInfo("result:" + result, module);		 
    	return result;        
    }
}