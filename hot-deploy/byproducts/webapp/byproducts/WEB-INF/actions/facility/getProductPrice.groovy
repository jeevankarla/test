
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

import java.sql.*;

import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.purchase.MaterialHelperServices;

import org.ofbiz.party.party.PartyHelper;



supplierId = parameters.supplierId;

productId = parameters.productId;

facilityId = parameters.facilityId;

Debug.log("supplierId=============="+supplierId);

Debug.log("productId=============="+productId);

Debug.log("facilityId=============="+facilityId);


daystart = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());

JSONArray facilityAddressJSON = new  JSONArray();

lastPrice = 0;
if(supplierId && productId && facilityId){
	inputCtx = [:];
	inputCtx.put("userLogin",userLogin);
	inputCtx.put("productId", productId);
	inputCtx.put("partyId", supplierId);
	inputCtx.put("facilityId", facilityId);
	try{
	 resultCtx = dispatcher.runSync("getSupplierProductPrice", inputCtx);
	 
	 lastPrice = resultCtx.lastPrice;
	 
	 
	}catch(Exception e){}

}

request.setAttribute("lastPrice", lastPrice);

return "sucess";