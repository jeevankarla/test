import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.byproducts.ByProductServices;

PeriodTypeList = [];
conditionList = [];
conditionList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "PAYROLL_BILL"));
condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
PayrollTypeList = delegator.findList("PayrollType", condition, null, null, null, true);
if(UtilValidate.isNotEmpty(PayrollTypeList)){
	for(i=0; i<PayrollTypeList.size(); i++){
		PayrollTypeDet = PayrollTypeList.get(i);
		periodTypeMap = [:];
		periodTypeId = PayrollTypeDet.get("periodTypeId");
		PeriodTypeDetails = delegator.findOne("PeriodType", [periodTypeId : periodTypeId], false);
		description=PeriodTypeDetails.description;
		periodTypeMap.put("periodId", periodTypeId);
		periodTypeMap.put("description", description);
		PeriodTypeList.add(periodTypeMap);
	}
}
context.put("PeriodTypeList",PeriodTypeList);
