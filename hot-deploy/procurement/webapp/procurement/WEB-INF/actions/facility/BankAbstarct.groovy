import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;

bankAbstract = [:];
conditionList = [];
conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS ,parameters.unitId));
conditionList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS ,"BANK_ACCOUNT"));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS ,"FNACT_ACTIVE"));
//conditionList.add(EntityCondition.makeCondition("finAccountCode", EntityOperator.NOT_EQUAL,null));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
unitFinAccountList = delegator.findList("FacilityPersonAndFinAccount",condition,null,null,null,false);
if(UtilValidate.isNotEmpty(unitFinAccountList)){
unitDetail = unitFinAccountList.get(0);
bankAbstract["nameOfTheBank"] = unitDetail.finAccountName;
bankAbstract["nameOfTheBrch"] = unitDetail.finAccountBranch;
bankAbstract["dedAmount"] = unitGrndTot.get("DednsTot");
bankAbstract["grossAmount"] = unitGrndTot.get("grossAmount");
bankAbstract["netAmount"] = unitGrndTot.get("netAmount");
bankAbstract["netRndAmount"] = unitGrndTot.get("netRndAmount");
}
context.put("bankAbstract",bankAbstract);

//Debug.logInfo(" bankAbstract===================================="+bankAbstract, "");