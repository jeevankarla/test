import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import java.security.Policy.Parameters;
import java.sql.*;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.party.contact.ContactMechWorker;
import java.lang.Integer;


	List requirementList=FastList.newInstance();
	List conditionList=FastList.newInstance();
	
	if(UtilValidate.isNotEmpty(parameters.requirementId)){
		conditionList.add(EntityCondition.makeCondition("requirementId",EntityOperator.EQUALS,parameters.requirementId));
	}
	if(UtilValidate.isNotEmpty(parameters.facilityId)){
		conditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,parameters.facilityId));
	}
	if(UtilValidate.isNotEmpty(parameters.productId)){
		conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,parameters.productId));
	}
	if(UtilValidate.isNotEmpty(parameters.requirementByDate)){
		conditionList.add(EntityCondition.makeCondition("requirementByDate",EntityOperator.EQUALS,parameters.requirementByDate));
	}
	conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"REQ_APPROVED"));
	 condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	
	requirementList=delegator.findList("Requirement",condition,null,UtilMisc.toList("-requirementId"),null,false);
	
	context.requirements=requirementList;
	