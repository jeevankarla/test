import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import org.ofbiz.entity.util.*;
import net.sf.json.JSONArray;

def mainAndConds = [];
def orExprs = [];
def entityName = parameters.entityName;
def searchFields = "[partyId, firstName, middleName, lastName, groupName]";
def displayFields = "[firstName, lastName, groupName]";
def searchDistinct = Boolean.valueOf(context.searchDistinct ?: false);

def searchValueFieldName = parameters.term;
def fieldValue = null;
if (searchValueFieldName) {
	fieldValue = searchValueFieldName;
} else if (parameters.searchValueFieldName) { // This is to find the description of a lookup value on initialization.
	fieldValue = parameters.get(parameters.searchValueFieldName);
	context.description = "true";
}

def searchType = context.searchType;
def displayFieldsSet = null;

if (searchFields && fieldValue) {
	def searchFieldsList = StringUtil.toList(searchFields);
	displayFieldsSet = StringUtil.toSet(displayFields);
	if (context.description && fieldValue instanceof java.lang.String) {
		returnField = parameters.searchValueFieldName;
	} else {
		returnField = searchFieldsList[0]; //default to first element of searchFields
		displayFieldsSet.add(returnField); //add it to select fields, in case it is missing
	}
	context.returnField = returnField;
	context.displayFieldsSet = displayFieldsSet;
	if ("STARTS_WITH".equals(searchType)) {
		searchValue = fieldValue.toUpperCase() + "%";
	} else if ("EQUALS".equals(searchType)) {
		searchValue = fieldValue;
	} else {//default is CONTAINS
		searchValue = "%" + fieldValue.toUpperCase() + "%";
	}
	searchFieldsList.each { fieldName ->
		if ("EQUALS".equals(searchType)) {
			orExprs.add(EntityCondition.makeCondition(EntityFieldValue.makeFieldValue(searchFieldsList[0]), EntityOperator.EQUALS, searchValue));
			return;//in case of EQUALS, we search only a match for the returned field
		} else {
			orExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue(fieldName)), EntityOperator.LIKE, searchValue));
		}
	}
}

/* the following is part of an attempt to handle additional parameters that are passed in from other form fields at run-time,
 * but that is not supported by the scrip.aculo.us Ajax.Autocompleter, but this is still useful to pass parameters from the
 * lookup screen definition:
 */
def conditionFields = context.conditionFields;
if (conditionFields) {
	// these fields are for additonal conditions, this is a Map of name/value pairs
	for (conditionFieldEntry in conditionFields.entrySet()) {
		if (conditionFieldEntry.getValue() instanceof java.util.List) {
			def orCondFields = [];
			for (entry in conditionFieldEntry.getValue()) {
				orCondFields.add(EntityCondition.makeCondition(EntityFieldValue.makeFieldValue(conditionFieldEntry.getKey()), EntityOperator.EQUALS, entry));
			}
			mainAndConds.add(EntityCondition.makeCondition(orCondFields, EntityOperator.OR));
		} else {
			mainAndConds.add(EntityCondition.makeCondition(EntityFieldValue.makeFieldValue(conditionFieldEntry.getKey()), EntityOperator.EQUALS, conditionFieldEntry.getValue()));
		}
	}
}
JSONArray partyJSON = new JSONArray();
JSONObject partyNameObj = new JSONObject();
if (orExprs && entityName && displayFieldsSet) {
	mainAndConds.add(EntityCondition.makeCondition(orExprs, EntityOperator.OR));

	//if there is an extra condition, add it to main condition list
	if (context.andCondition && context.andCondition instanceof EntityCondition) {
		mainAndConds.add(context.andCondition);
	}
	
	def entityConditionList = EntityCondition.makeCondition(mainAndConds, EntityOperator.AND);
	
	Integer autocompleterViewSize = Integer.valueOf(context.autocompleterViewSize ?: 10);
	EntityFindOptions findOptions = new EntityFindOptions();
	findOptions.setMaxRows(autocompleterViewSize);
	findOptions.setDistinct(searchDistinct);
	autocompleteOptions = delegator.findList(entityName, entityConditionList, displayFieldsSet, StringUtil.toList(displayFields), findOptions, false);
	if (autocompleteOptions) {
		//context.autocompleteOptions = autocompleteOptions;
		autocompleteOptions.each{eachParty ->
			JSONObject newPartyObj = new JSONObject();
			newPartyObj.put("value",eachParty.partyId);
			String partyName = "";
			if(UtilValidate.isNotEmpty(eachParty.groupName)){
				partyName = eachParty.groupName;
			}
			else{
				String firstName="";
				String lastName="";
				String middleName="";
				if(eachParty.getString("firstName")!=null){
					firstName=eachParty.getString("firstName");
				}
				if(eachParty.getString("lastName")!=null){
					lastName=eachParty.getString("lastName");
				}
				if(eachParty.getString("middleName") !=null){
					middleName = eachParty.getString("middleName");
				}
				partyName = firstName + " " + middleName + " " + lastName;
			}
			
			newPartyObj.put("label",partyName+" ["+eachParty.partyId+"]");
			partyNameObj.put(eachParty.partyId,partyName);
			partyJSON.add(newPartyObj);
		}
	}
}
if (partyJSON.size() > 0) {
	request.setAttribute("partyJSON",partyJSON);
}