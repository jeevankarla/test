import org.apache.batik.util.EventDispatcher.Dispatcher;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.GenericServiceException;
dctx = dispatcher.getDispatchContext();
LocalDispatcher dispatcher = dctx.getDispatcher();
transferGroupId = parameters.transId;
toFacilityId = parameters.toFacilityId;
if(UtilValidate.isNotEmpty(transferGroupId) && UtilValidate.isNotEmpty(toFacilityId)){
	List inventoryTransferGroupMember = delegator.findList("InventoryTransferGroupMember",EntityCondition.makeCondition("transferGroupId",EntityOperator.EQUALS,transferGroupId),null,null,null,false);
	List transferIds = EntityUtil.getFieldListFromEntityList(inventoryTransferGroupMember, "inventoryTransferId", true);
	List inventoryTransfers = delegator.findList("InventoryTransfer",EntityCondition.makeCondition("inventoryTransferId",EntityOperator.IN,transferIds),null,null,null,false);
	inventoryTransferGroupMember.each{member->
		inventoryTransfer = EntityUtil.filterByCondition(inventoryTransfers, EntityCondition.makeCondition("inventoryTransferId",EntityOperator.EQUALS,member.inventoryTransferId));
		invTransfer = EntityUtil.getFirst(inventoryTransfer);
		inventoryItemId = invTransfer.inventoryItemId;
		productId = member.productId;
		quantity = member.xferQty;
		Map inputMap = FastMap.newInstance();
		inputMap.put("facilityId", toFacilityId);
		inputMap.put("productId", productId);
		inputMap.put("quantityOnHandTotal", quantity);
		inputMap.put("inventoryItemId", inventoryItemId);
		Map resultMap = FastMap.newInstance();
		resultMap = dispatcher.runSync("checkAndManageBlendedProductInventory", inputMap);
			if(ServiceUtil.isError(resultMap)){
				Debug.logError("Problem in Changing Facility.","");
				return  resultMap;
			}
	}
}


