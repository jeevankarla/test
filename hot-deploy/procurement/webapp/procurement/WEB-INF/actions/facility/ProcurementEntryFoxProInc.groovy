
import static org.ofbiz.base.util.UtilGenerics.checkMap;

import java.util.List;
import java.util.Map;

import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import javolution.util.FastList;
import org.ofbiz.network.NetworkServices;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.common.FindServices;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import org.ofbiz.entity.model.ModelEntity;
EntityConditionList<EntityCondition> exprList = null;
Map<String, ?> inputFields = checkMap(parameters, String.class, Object.class); // Input
 String entityName ="OrderHeaderItemProductAndFacility";
ModelEntity modelEntity = delegator.getModelEntity(entityName);
List<EntityCondition> tmpList = FindServices.createConditionList(inputFields, modelEntity.getFieldsUnmodifiable(), UtilMisc.toMap(), delegator, context);
exprList = EntityCondition.makeCondition(tmpList);
procurementEntryList =[];
EntityFindOptions findOptions = new EntityFindOptions();
findOptions.setFetchSize(100000);
findOptions.setMaxRows(0);
procurementEntryList = delegator.find(entityName, exprList, null, null, null,findOptions);
//Debug.log("list size========"+procurementEntryList.size());
context.listProcurementOrders = procurementEntryList;