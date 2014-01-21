import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.*;



result = ServiceUtil.returnSuccess();
validationResult = "";


if (validator == null) {
	validationResult = "No validator specified!"
}
if (component == null) {
	validationResult = "No component specified!"
}

if (component && validator) {
	validationResult = GroovyUtil.runScriptAtLocation("component://appvalidator/webapp/appvalidator/WEB-INF/actions/validators/" + 
		component + "/" + validator + ".groovy", context);
}

context.validationResult = validationResult;
result.validationResult = validationResult;

return result;