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
				Map<String, String> employee = FastMap.newInstance();			
				employee.put("department", org.getString("groupName"));
				String employeePosition = "";
				List<GenericValue> emplPositionAndFulfillments = EntityUtil.filterByDate(delegator.findByAnd("EmplPositionAndFulfillment", UtilMisc.toMap("employeePartyId", employment.getString("partyId"))));
				GenericValue emplPositionAndFulfillment = EntityUtil.getFirst(emplPositionAndFulfillments);
				if(UtilValidate.isNotEmpty(emplPositionAndFulfillment) && emplPositionAndFulfillment.getString("emplPositionId") != null){
					GenericValue emplPositionType = delegator.findOne("EmplPositionType",UtilMisc.toMap("emplPositionTypeId", emplPositionAndFulfillment.getString("emplPositionId")), true);
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
				employeeList.add(employee);
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
    
}