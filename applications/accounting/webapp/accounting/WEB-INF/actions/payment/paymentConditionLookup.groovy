import org.ofbiz.base.util.*;

if(parameters.ajaxLookup == 'Y'){
	// initialising  conditionFields Map 
	conditionFields=[:];
	parentTypeId=parameters.parentTypeId;
	if (parentTypeId != null) {
		conditionFields.parentTypeId=parentTypeId;
	}	
	if (conditionFields.size() > 0) {
		context.conditionFields=conditionFields;	
	}
}	