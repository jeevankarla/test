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
import in.vasista.vbiz.purchase.MaterialHelperServices;
import org.ofbiz.party.party.PartyHelper;

JSONArray glAccountTypeJSON= new JSONArray();
JSONObject glAccountName = new JSONObject();
List glAccount = FastList.newInstance();
List glAccountTypeIds= FastList.newInstance();

glAccountName=[:];
glAccount=delegator.findList("GlAccount",null,UtilMisc.toSet("glAccountId","glAccountTypeId","accountName"),null,null,false);

glAccount.each{glAccountTypeId->
	JSONObject newPartyObj = new JSONObject();
	newPartyObj.put("value",glAccountTypeId.glAccountId);
	newPartyObj.put("label",glAccountTypeId.accountName);
	glAccountTypeJSON.add(newPartyObj);
	glAccountName.put(glAccountTypeId.glAccountId, glAccountTypeId.accountName);
}
context.glAccountTypeJSON=glAccountTypeJSON;
context.glAccountName=glAccountName;
