
context.isChecklistComplete = true;
if (taskAttributes) {
	taskAttributes.each { attribute ->
	 	if(attribute.attrValue == null || attribute.attrValue=='N') {
	 		context.isChecklistComplete = false;
	 		return;
	 	}
	}
}