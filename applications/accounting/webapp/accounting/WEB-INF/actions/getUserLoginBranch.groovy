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




resultCtx = dispatcher.runSync("getCustomerBranch",UtilMisc.toMap("userLogin",userLogin));
productStoreDetails = resultCtx.get("productStoreList");
Debug.log("productStoreDetails======================="+productStoreDetails);
productStoreIds = EntityUtil.getFieldListFromEntityList(productStoreDetails, "productStoreId", true);
	
	
	if(productStoreIds.size() == 1){
		Debug.log("productStoreIds.get(0)===================="+productStoreIds.get(0));
		context.ownerPartyId = productStoreIds.get(0);
		parameters.ownerPartyId = productStoreIds.get(0);
	}