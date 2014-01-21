import org.ofbiz.base.util.*;

if(parameters.ajaxLookup == 'Y'){
	// initialising  conditionFields Map 
	conditionFields=[:];
	partyId=parameters.partyId;
	if (partyId != null) {
		conditionFields.partyId=partyId;
	}	
	contactMechTypeId=parameters.contactMechTypeId;
	if (contactMechTypeId != null) {
		conditionFields.contactMechTypeId=contactMechTypeId;
	}
	if (conditionFields.size() > 0) {
		context.conditionFields=conditionFields;	
	}
}	
