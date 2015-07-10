import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.humanres.EmplLeaveService;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresApiService;
import org.ofbiz.party.party.PartyHelper;

flag = "N";
if(UtilValidate.isNotEmpty(parameters.flag)){
	flag = parameters.flag;
}




salaryStepSeqId = parameters.salaryStepSeqId;
payGradeId = parameters.payGradeId;

if(flag.equals("Y")){
	salaryStepSeqId = null;
	payGradeId = null;
}



List conditionList=[];
if(UtilValidate.isNotEmpty(salaryStepSeqId)){
	conditionList.add(EntityCondition.makeCondition("salaryStepSeqId", EntityOperator.EQUALS, salaryStepSeqId));
}
if(UtilValidate.isNotEmpty(payGradeId)){
	conditionList.add(EntityCondition.makeCondition("payGradeId", EntityOperator.EQUALS, payGradeId));
}
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
orderBy = UtilMisc.toList("salaryStepSeqId");
salaryStepList = delegator.findList("SalaryStep", condition , null, orderBy, null, false );


context.put("salaryStepList", salaryStepList);
