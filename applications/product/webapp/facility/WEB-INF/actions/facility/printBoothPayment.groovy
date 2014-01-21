import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javolution.util.FastList;
import org.ofbiz.network.NetworkServices;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.party.party.PartyHelper;

paymentIds=FastList.newInstance();
tempPaymentIds=FastList.newInstance();
conditionList=[];
if(parameters.paymentId){
	paymentId=parameters.paymentId;
	tempPaymentIds.add(paymentId);
	parameters.paymentIds = tempPaymentIds;
}

if(parameters.paymentIds){
	paymentIds.addAll(parameters.paymentIds);
	List printPaymentsList = delegator.findList("PaymentAndFacility",EntityCondition.makeCondition("paymentId", EntityOperator.IN , paymentIds)  , null, null, null, false );
	context.put("printPaymentsList", printPaymentsList);
	if(paymentIds.size()==1){
			facilityId=printPaymentsList[0].facilityId;
			facility=delegator.findOne("Facility", [facilityId : printPaymentsList[0].facilityId], false);
		    context.put("vendorName", PartyHelper.getPartyName(delegator, facility.ownerPartyId, false));
						
	}else{	
		conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, printPaymentsList[0].parentFacilityId));
		conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ROUTE_CASHIER"));
		condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		facilityDetails = delegator.findList("FacilityParty", condition , null, null, null, false );
		if(!UtilValidate.isEmpty(facilityDetails)){
			context.put("vendorName", PartyHelper.getPartyName(delegator, facilityDetails[0].partyId, false));
		}
	
	}
	
}
