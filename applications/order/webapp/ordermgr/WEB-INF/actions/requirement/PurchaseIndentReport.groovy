import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.finder.EntityFinderUtil.ConditionList;
import in.vasista.vbiz.purchase.MaterialHelperServices;
import javolution.util.FastMap;
import java.sql.Timestamp;
import org.ofbiz.base.util.UtilDateTime;
import java.text.SimpleDateFormat;
import java.text.ParseException;

requirementId = parameters.requirementId;
Debug.log("requirementId ==== rrrr "+requirementId);
productId = "";
purchaseIndentMap = [:];
purchaseIndentMap["materialCode"]="";
purchaseIndentMap["description"]="";
purchaseIndentMap["lastPOdate"];
purchaseIndentMap["lastPOrate"];
purchaseIndentMap["stockQTY"]="";
purchaseIndentMap["requiredQTY"]="";

requirement = delegator.findOne("Requirement",UtilMisc.toMap("requirementId", requirementId), false);
if(UtilValidate.isNotEmpty(requirement)){	
	productId = requirement.productId; 
	purchaseIndentMap["requiredQTY"] = requirement.quantity;
	}
tempMap = dispatcher.runSync("getLastSupplyMaterialDetails", [productId:productId, userLogin:userLogin]);
invCountMap = dispatcher.runSync("getProductInventoryOpeningBalance", [productId: productId, ownerPartyId:"Company", userLogin: userLogin]);
product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
if(UtilValidate.isNotEmpty(product)){
	purchaseIndentMap["materialCode"] = product.internalName;
	purchaseIndentMap["description"] = product.description;
	}
if(UtilValidate.isNotEmpty(tempMap)){
	purchaseIndentMap["lastPOdate"] = UtilDateTime.toDateString(tempMap.productSupplyDetails.supplyDate, "dd-MM-yyyy");
	purchaseIndentMap["lastPOrate"] = tempMap.productSupplyDetails.supplyRate;
	}
if(UtilValidate.isNotEmpty(invCountMap)){
	purchaseIndentMap["stockQTY"] = invCountMap.inventoryCount;
	}
context.purchaseIndentMap=purchaseIndentMap;

conditionList =[];
conditionList.add(EntityCondition.makeCondition("requirementId", EntityOperator.EQUALS, requirementId));
conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ENQ_CANCELLED"));
condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
requirementCustRequestList = delegator.findList("RequirementCustRequestView", condition, null, null, null, false);
requirementCustRequest = EntityUtil.getFirst(requirementCustRequestList);

requirementIndent = delegator.findOne("CustRequest", UtilMisc.toMap("custRequestId", requirementCustRequest.custRequestId), false);
context.indentNo=requirementIndent.custRequestId;
context.indentDate=UtilDateTime.toDateString(requirementIndent.custRequestDate, "dd-MM-yyyy");
context.requirementId=requirementId;

conditionStatus = [];
conditionStatus.add(EntityCondition.makeCondition("requirementId", EntityOperator.EQUALS, requirementId));
conditionStatus.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "REQ_APPROVED"));
condition = EntityCondition.makeCondition(conditionStatus, EntityOperator.AND);
requirementStatusList = delegator.findList("RequirementStatus", condition, null, null, null, false);
statusList = EntityUtil.getFirst(requirementStatusList);
context.approvedDate=UtilDateTime.toDateString(statusList.statusDate, "dd-MM-yyyy");


