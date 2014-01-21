import org.ofbiz.base.util.*;

if(parameters.ajaxLookup == 'Y'){
	contactMechTypeId=parameters.contactMechTypeId;
	if (contactMechTypeId != null) {
		// initialising  conditionFields Map 
		conditionFields=[:];
		conditionFields.contactMechTypeId=contactMechTypeId;
		context.conditionFields=conditionFields;
	}
}	
//not ajax
else{
	context.contactMechTypeId=parameters.parm0;
}