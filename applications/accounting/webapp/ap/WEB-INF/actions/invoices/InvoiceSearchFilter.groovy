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
	context.totalSize = totalSize;
}

if(UtilValidate.isNotEmpty(parameters.VIEW_INDEX_1) && UtilValidate.isNotEmpty(parameters.VIEW_SIZE_1) && UtilValidate.isNotEmpty(result.listIt)){
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
