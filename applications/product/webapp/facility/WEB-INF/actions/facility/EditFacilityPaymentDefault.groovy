
import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.util.List;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
    facilityId = parameters.facilityId;
	condList = [];
	if (UtilValidate.isNotEmpty(facilityId)) {
		paymentMethodType="";
		GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
		partyId = (String) facility.get("ownerPartyId");
		
		List<GenericValue> partyProfileDefault = delegator.findList("PartyProfileDefault", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), null, ['-fromDate'], null, false);
		if(UtilValidate.isNotEmpty(partyProfileDefault)){
			GenericValue partyProfile = EntityUtil.getFirst(partyProfileDefault);
			paymentMethodType=partyProfile.get("defaultPayMeth");
		}
		context.paymentMethodType = paymentMethodType;
		condList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, partyId));
		condList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "FNACT_ACTIVE"));
		condList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS, "BANK_ACCOUNT"));
		cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
		finAccountList = delegator.findList("FinAccount", cond, null, null, null, false);
		if (UtilValidate.isNotEmpty(finAccountList)) {
			finAccountName = EntityUtil.getFieldListFromEntityList(finAccountList, "finAccountName", true);
			context.finAccountName = finAccountName[0];
		}
	}
	condList.clear();
	condList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, "Company"));
	condList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "FNACT_ACTIVE"));
	condList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS, "BANK_ACCOUNT"));
	finAccCond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	finAccount = delegator.findList("FinAccount", finAccCond, null, null, null, false);
	context.finAccount = finAccount;
