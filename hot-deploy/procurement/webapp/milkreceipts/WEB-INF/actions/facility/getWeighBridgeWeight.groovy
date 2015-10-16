import org.ofbiz.base.util.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;

dctx = dispatcher.getDispatchContext();

vehicleId = parameters.vehicleId;
weighmentType = parameters.weighmentType;

List condList = FastList.newInstance();
BigDecimal weight = BigDecimal.ZERO;
String weighBridgeId = "";
if(UtilValidate.isNotEmpty(vehicleId) && UtilValidate.isNotEmpty(weighmentType)){
	condList.add(EntityCondition.makeCondition("vehicleId",EntityOperator.EQUALS,vehicleId));
	condList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_CREATED"));
	condList.add(EntityCondition.makeCondition("weighmentType",EntityOperator.EQUALS,weighmentType));
	
	EntityCondition con = EntityCondition.makeCondition(condList,EntityOperator.AND);
	
	List weighBridgeDetails = delegator.findList("WeighBridgeDetails",con,null,null,null,false);
	if(UtilValidate.isNotEmpty(weighBridgeDetails)){
		weighBridgeDetail = EntityUtil.getFirst(weighBridgeDetails);
		weight = weighBridgeDetail.getBigDecimal("weightKgs");
		weighBridgeId = weighBridgeDetail.getString("weighmentId");
	}
	request.setAttribute("weight", weight);
	request.setAttribute("weighBridgeId", weighBridgeId);
	
}



