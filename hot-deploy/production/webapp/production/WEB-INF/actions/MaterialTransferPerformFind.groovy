import java.util.List;
import org.ofbiz.entity.GenericValue;

import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.util.EntityUtil;
List facilityIds = FastList.newInstance();
if(UtilValidate.isNotEmpty(parameters.fromPartyId)){
	List facilityList = delegator.findList("Facility",EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,parameters.fromPartyId),null,null,null,false);
	facilityIds = EntityUtil.getFieldListFromEntityList(facilityList, "facilityId", true);
	requestParameters.fromFacilityId= facilityIds;
	requestParameters.fromFacilityId_op="in";
}else{
	if(UtilValidate.isNotEmpty(context.get("partyId"))){
		List facilityList = delegator.findList("Facility",EntityCondition.makeCondition("ownerPartyId",EntityOperator.IN,context.get("partyId")),null,null,null,false);
		facilityIds = EntityUtil.getFieldListFromEntityList(facilityList, "facilityId", true);
		requestParameters.fromFacilityId= facilityIds;
		requestParameters.fromFacilityId_op="in";
	}
}
