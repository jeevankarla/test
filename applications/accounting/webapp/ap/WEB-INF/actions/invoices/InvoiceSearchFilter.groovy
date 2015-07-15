import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import javolution.util.FastMap;
import java.text.ParseException;
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.facility.util.FacilityUtil;

if(UtilValidate.isNotEmpty(result.listIt) && UtilValidate.isEmpty(parameters.VIEW_INDEX_1) && UtilValidate.isEmpty(parameters.VIEW_SIZE_1)){
	listItr = result.listIt;
	Integer	totalSize = listItr.size();
	inputFields = [:];
	inputFields.put("noConditionFind", parameters.noConditionFind);
	inputFields.put("hideSearch", parameters.hideSearch);
	inputFields.put("parentTypeId", parameters.parentTypeId);
	inputFields.put("invoiceId_op", parameters.invoiceId_op);
	inputFields.put("invoiceId", parameters.invoiceId);
	inputFields.put("invoiceId_ic", parameters.invoiceId_ic);
	inputFields.put("description_op", parameters.description_op);
	inputFields.put("description", parameters.description);
	inputFields.put("description_ic", parameters.description_ic);
	inputFields.put("invoiceTypeId", parameters.invoiceTypeId);
	inputFields.put("statusId", parameters.statusId);
	inputFields.put("partyIdFrom", parameters.partyIdFrom);
	inputFields.put("partyId", parameters.partyId);
	inputFields.put("facilityId", parameters.facilityId);
	inputFields.put("facilityId_op", parameters.facilityId_op);
	inputFields.put("facilityId_ic", parameters.facilityId_ic);
	inputFields.put("invoiceDate_fld0_value", parameters.invoiceDate_fld0_value);
	inputFields.put("invoiceDate_fld0_op", parameters.invoiceDate_fld0_op);
	inputFields.put("invoiceDate_fld1_value", parameters.invoiceDate_fld1_value);
	inputFields.put("invoiceDate_fld1_op", parameters.invoiceDate_fld1_op);
	inputFields.put("dueDate_fld0_value", parameters.dueDate_fld0_value);
	inputFields.put("dueDate_fld0_op", parameters.dueDate_fld0_op);
	inputFields.put("dueDate_fld1_value", parameters.dueDate_fld1_value);
	inputFields.put("dueDate_fld1_op", parameters.dueDate_fld1_op);
	
	result = dispatcher.runSync("performFind", UtilMisc.toMap("entityName", "InvoiceAndType", "inputFields", inputFields, "userLogin", userLogin));
    demoListIt = result.listIt;
	
	maximumSize = demoListIt.size();

	context.totalSize = totalSize;
}

if(UtilValidate.isNotEmpty(parameters.VIEW_INDEX_1) && UtilValidate.isNotEmpty(parameters.VIEW_SIZE_1) && UtilValidate.isNotEmpty(result.listIt)){
	
	maximumSize = parameters.maximumSize;
	
	nextFlag = "Y";
	prevFlag = "Y";
	Integer index = Integer.parseInt(parameters.VIEW_INDEX_1);
	Integer viewSize = Integer.parseInt(parameters.VIEW_SIZE_1);
	lowIndex = (index * viewSize) + 1;
	highIndex = (index * viewSize) + viewSize;
	listItr = result.listIt;
Integer	totalSize = listItr.size();
		if(lowIndex == totalSize){
			highIndex = lowIndex;
		}
		if(lowIndex > totalSize){
			lowIndex = lowIndex-viewSize;
			highIndex = totalSize;
			nextFlag = "N";
		}
		if(highIndex > totalSize){
			nextFlag = "N";
			highIndex = totalSize;
		}
		if(lowIndex <= 1){
			prevFlag = "N";
			lowIndex = 1;
			highIndex = totalSize;
		}
	invoiceList = [];
	invoiceList = listItr.getPartialList(lowIndex, highIndex);
	context.invoiceList=invoiceList;
	context.lowIndex=lowIndex;
	context.highIndex=highIndex;
	context.nextFlag=nextFlag;
	context.prevFlag=prevFlag;
}
context.maximumSize = maximumSize;
