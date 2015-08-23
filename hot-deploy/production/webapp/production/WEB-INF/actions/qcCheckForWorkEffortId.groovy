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

dctx = dispatcher.getDispatchContext();
if(UtilValidate.isNotEmpty(context.get("workEffortId"))){
	workEffortId=context.get("workEffortId");
	List conlist=[];
	conlist.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
	conlist.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS ,"QC_ACCEPT"));
	cond=EntityCondition.makeCondition(conlist,EntityOperator.AND);
	productQcTest = delegator.findList("ProductQcTest", cond , null, null, null, false );
	qcCheckForProdRun="COMPLETE";
	qcProductsList=[];
	List<GenericValue> workEffort = delegator.findList("WorkEffort", EntityCondition.makeCondition("workEffortParentId", EntityOperator.EQUALS, workEffortId), null, UtilMisc.toList("workEffortId"), null, false);
	if(UtilValidate.isNotEmpty(workEffort)){
		List<String> workEffortIds = EntityUtil.getFieldListFromEntityList(workEffort, "workEffortId", false);
	    inventoryItemAndDetail = delegator.findList("InventoryItemAndDetail", EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIds), null, null, null, false);
			if(UtilValidate.isNotEmpty(inventoryItemAndDetail)){
			inventoryItemAndDetail.each{eachDeclaredItem->
				String productId = eachDeclaredItem.productId;
				String productBatchId = eachDeclaredItem.productBatchId;
				BigDecimal quantityOnHandDiff = eachDeclaredItem.quantityOnHandDiff;
				 if((quantityOnHandDiff.compareTo(BigDecimal.ZERO) >= 0) && (UtilValidate.isNotEmpty(productBatchId))){
					  productQcTestDetails= EntityUtil.filterByCondition(productQcTest, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
					   if(UtilValidate.isEmpty(productQcTestDetails)){
						 qcCheckForProdRun="INCOMPLETE"
					 }
				 }
			 }
		}
	}
	context.qcCheckForProdRun=qcCheckForProdRun;
}