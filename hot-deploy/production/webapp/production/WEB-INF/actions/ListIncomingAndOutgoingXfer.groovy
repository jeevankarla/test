import java.sql.*
import java.text.SimpleDateFormat;

import javolution.util.FastList;
import javolution.util.FastMap;
import java.math.BigDecimal;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

facilityId = parameters.facilityId;
productId = parameters.productId;

incommingTransfer = [];
outgoingTransfer = [];
if(facilityId){

	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("toFacilityId", EntityOperator.EQUALS, facilityId));
	if(productId){
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	}
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "IXF_EN_ROUTE"));
	condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	incommingTransfer = delegator.findList("InventoryTransferGroupAndMemberSum", condition, null, null, null, false);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("fromFacilityId", EntityOperator.EQUALS, facilityId));
	if(productId){
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	}
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "IXF_REQUESTED"));
	cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	outgoingTransfer = delegator.findList("InventoryTransferGroupAndMemberSum", cond, null, null, null, false);
	
}
context.incommingTransfer = incommingTransfer;
context.outgoingTransfer = outgoingTransfer;