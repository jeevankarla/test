import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
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
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.product.product.ProductWorker;
import in.vasista.vbiz.facility.util.FacilityUtil;
import in.vasista.vbiz.byproducts.icp.ICPServices;
import in.vasista.vbiz.purchase.MaterialHelperServices;


dctx = dispatcher.getDispatchContext();
parentTypeId = parameters.parentTypeId;
filterTermTypeList = [];
termTypeList = delegator.findList("TermType",null , null, null, null, false);
if(UtilValidate.isNotEmpty(termTypeList)){
	parentList = EntityUtil.getFieldListFromEntityList(termTypeList,"parentTypeId",true);
	parentTypeList = (new HashSet(parentList)).toList();
	if(UtilValidate.isNotEmpty(parentTypeList)){
		filterTermTypeList = delegator.findList("TermType",EntityCondition.makeCondition("termTypeId", EntityOperator.IN, parentTypeList) , null, null, null, false);
		if(UtilValidate.isNotEmpty(filterTermTypeList)){
			context.put("filterTermTypeList",filterTermTypeList);
		}
	}
}

if(UtilValidate.isNotEmpty(parentTypeId)){
	termTypeIdList = delegator.findList("TermType",EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, parentTypeId) , null, null, null, false);
	if(UtilValidate.isNotEmpty(termTypeIdList)){
		request.setAttribute("termTypeIdList", termTypeIdList);
	}
}









