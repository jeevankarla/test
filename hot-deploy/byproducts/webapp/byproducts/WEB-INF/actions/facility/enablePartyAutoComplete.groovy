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

JSONArray partyJSON = new JSONArray();
dctx = dispatcher.getDispatchContext();

if(UtilValidate.isNotEmpty(parameters.roleTypeId)){//to handle IceCream Parties
	roleTypeId =parameters.roleTypeId;
	partyDetailsList = ByProductNetworkServices.getPartyByRoleType(dctx, [userLogin: userLogin, roleTypeId: roleTypeId]).get("partyDetails");
	   //To handle KMFUnions along with OtherSuppliers in PurchaseOrder tab.
		if("MAINSTORE_VENDOR"==parameters.roleTypeId){
			unionPartyDetailsList = ByProductNetworkServices.getPartyByRoleType(dctx, [userLogin: userLogin, roleTypeId: "UNION"]).get("partyDetails");
			//Debug.log("======unionPartyDetailsList=="+unionPartyDetailsList.size());
			if(UtilValidate.isNotEmpty(unionPartyDetailsList)&&(UtilValidate.isNotEmpty(partyDetailsList))){
				partyDetailsList.addAll(unionPartyDetailsList);
			}
			if(UtilValidate.isNotEmpty(unionPartyDetailsList)&&(UtilValidate.isEmpty(partyDetailsList))){
				partyDetailsList=unionPartyDetailsList;
			}
		}
	partyDetailsList.each{eachParty ->
		JSONObject newPartyObj = new JSONObject();
		newPartyObj.put("value",eachParty.partyId);
		newPartyObj.put("label",eachParty.groupName+" ["+eachParty.partyId+"]");
		partyJSON.add(newPartyObj);
	}
}
context.partyJSON = partyJSON;
