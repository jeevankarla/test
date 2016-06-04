






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



bankName = parameters.bankName;

branch = parameters.branch;


Debug.log("bankName==============="+bankName);

Debug.log("branch==============="+branch);



conditionList =[];
conditionList.add(EntityCondition.makeCondition("bankId", EntityOperator.EQUALS , bankName));
conditionList.add(EntityCondition.makeCondition("geoId", EntityOperator.EQUALS ,branch));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
BankBranchList = delegator.findList("BankBranch", condition, null, null, null, false);

Debug.log("BankBranchList==============="+BankBranchList);


ifscCode = BankBranchList[0].ifscCode;

JSONObject ifScCodeMap = new JSONObject();


ifScCodeMap.putAt("ifscCode", ifscCode);


Debug.log("ifscCode==============="+ifscCode);


request.setAttribute("ifscCode", ifScCodeMap);

return "sucess";