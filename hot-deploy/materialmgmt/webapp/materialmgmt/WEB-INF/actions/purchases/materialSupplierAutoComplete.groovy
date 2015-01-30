import org.ofbiz.base.util.UtilDateTime;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javolution.util.FastList;
import org.ofbiz.entity.Delegator;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import org.ofbiz.entity.util.*;
import net.sf.json.JSONArray;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.party.party.PartyHelper;

custReqId =parameters.custRequestId;
Debug.log("Enquiry id= SSS "+custReqId);
nameMap=[:];
JSONArray supplierJSON = new JSONArray();

Condition = EntityCondition.makeCondition([EntityCondition.makeCondition("custRequestId", custReqId)],EntityOperator.AND);
supplierList=delegator.findList("CustRequestParty",Condition,null,null,null,false);
if(supplierList){
	supplierList.each{ supplier ->
		Cond = EntityCondition.makeCondition([EntityCondition.makeCondition("custRequestId", custReqId),EntityCondition.makeCondition("partyId",supplier.partyId)],EntityOperator.AND);
		existedSuppliers=delegator.findList("QuoteAndItemAndCustRequest",Cond,null,null,null,false);
		if(UtilValidate.isEmpty(existedSuppliers)){
			JSONObject newObj = new JSONObject();
			newObj.put("value",supplier.partyId);
			partyName=PartyHelper.getPartyName(delegator, supplier.partyId, false);
			newObj.put("label",partyName+"["+supplier.partyId+"]");
			supplierJSON.add(newObj);
			if(UtilValidate.isEmpty(nameMap[supplier.partyId])){
				nameMap[supplier.partyId]=partyName;
			}
		}
	}
}
context.supplierJSON=supplierJSON;
partyId=parameters.partyId;
if(UtilValidate.isNotEmpty(partyId)){
	partyName=nameMap.get(partyId);
	request.setAttribute("partyName",partyName);
}