import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import javolution.util.FastMap;
import java.sql.Timestamp;
import org.ofbiz.base.util.UtilDateTime;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.ofbiz.service.ServiceUtil;

partyId = "";
if (UtilValidate.isNotEmpty(parameters.partyIdTo)) {
	partyId = parameters.partyIdTo;
}

form16InputTypes = delegator.findList("Enumeration",EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "FORM16_INPUT_TYPE") , null, null, null, false);


JSONArray productItemsJSON = new JSONArray();
JSONObject productIdLabelJSON = new JSONObject();
JSONObject productLabelIdJSON=new JSONObject();
//context.productList = prodList;

form16InputTypes.each{eachItem ->
	JSONObject newObj = new JSONObject();
	newObj.put("value",eachItem.enumId);
	newObj.put("label",eachItem.description);
	productItemsJSON.add(newObj);
	productIdLabelJSON.put(eachItem.enumId, eachItem.description);
	productLabelIdJSON.put(eachItem.description, eachItem.enumId);
}


context.productItemsJSON = productItemsJSON;
context.productIdLabelJSON = productIdLabelJSON;
context.productLabelIdJSON = productLabelIdJSON;

context.partyId = partyId;
