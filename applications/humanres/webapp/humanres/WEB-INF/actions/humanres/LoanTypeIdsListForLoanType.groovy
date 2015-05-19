import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.byproducts.ByProductServices;

import java.util.Calendar;
import org.ofbiz.base.util.UtilNumber;

loanType = parameters.loanType;

loanTypeIdsList = [];

conditionList = [];
if(loanType.equals("external")){
	conditionList.add(EntityCondition.makeCondition("isExternal", EntityOperator.EQUALS ,"Y"));
}else{
	conditionList.add(EntityCondition.makeCondition("isExternal", EntityOperator.EQUALS ,null));
}
EntityCondition Cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
LoanTypeList = delegator.findList("LoanType", Cond, null, null, null, false);
if(UtilValidate.isNotEmpty(LoanTypeList)){
	
}
request.setAttribute("loanTypeIdsList", LoanTypeList);
