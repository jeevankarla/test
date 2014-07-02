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
        	List conditionList = UtilMisc.toList(
				EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS ,"PAYROLL_BILL"));		
        	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS ,"GENERATED"));
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
            			payrollItem.put(payrollHeaderItem.getString("payrollHeaderItemTypeId"), amount);
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
				if (emplPositionType != null) {
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
    
}