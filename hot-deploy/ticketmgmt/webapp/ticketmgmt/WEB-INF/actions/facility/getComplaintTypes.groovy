import org.ofbiz.base.util.*;
import java.sql.Date;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;

complaintsTypeMap =[:];
complaintsTypeMap = dispatcher.runSync("getTicketMgmtComplaintTypes",[userLogin:userLogin]);
complaintTypes =[];
if(ServiceUtil.isSuccess(complaintsTypeMap)){
	complaintTypes = complaintsTypeMap.complaintsList;
}else{
	context.errorMessage = "No ComplaintType found";
	return;
} 
context.put("complaintTypeList",complaintTypes);
