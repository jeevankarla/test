
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
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

if(UtilValidate.isNotEmpty(parameters.invoiceSequence)){
	invoiceSequence = parameters.invoiceSequence;
	billOfSaleInvoiceSequence = delegator.findList("BillOfSaleInvoiceSequence",EntityCondition.makeCondition("invoiceSequence", EntityOperator.EQUALS , invoiceSequence)  , null, null, null, false );
	invoiceId=EntityUtil.getFirst(billOfSaleInvoiceSequence).invoiceId;
	parameters.invoiceId=invoiceId;
}
if(UtilValidate.isNotEmpty(parameters.schemeCategory)){
	List condList = [];
	List orderIdList = [];
	List invoiceIdList = [];
	condList.add(EntityCondition.makeCondition("attrName" ,EntityOperator.EQUALS,"SCHEME_CAT"));
	condList.add(EntityCondition.makeCondition("attrValue" ,EntityOperator.EQUALS,parameters.schemeCategory));
	orderAttribute = delegator.findList("OrderAttribute",EntityCondition.makeCondition(condList,EntityOperator.AND), null, null, null, false );
	if(UtilValidate.isNotEmpty(orderAttribute)){
		orderIdList = EntityUtil.getFieldListFromEntityList(orderAttribute, "orderId", true);
		orderItemBilling = delegator.findList("OrderItemBilling",EntityCondition.makeCondition("orderId" ,EntityOperator.IN,orderIdList), null, null, null, false );
		if(UtilValidate.isNotEmpty(orderItemBilling)){
			invoiceIdList = EntityUtil.getFieldListFromEntityList(orderItemBilling, "invoiceId", true);
			if(UtilValidate.isNotEmpty(invoiceIdList)){
				parameters.invoiceId = invoiceIdList;
				parameters.invoiceId_op = "in";
			}
			
		}
	}
}

if(parameters.invoiceTypeId == "SALES_INVOICE"){
	if(UtilValidate.isNotEmpty(parameters.partyIdFrom)){
		parameters.tempPartyIdFrom = parameters.partyIdFrom
		condList = [];
		condList.add(EntityCondition.makeCondition("partyId" ,EntityOperator.EQUALS,parameters.partyIdFrom));
		condList.add(EntityCondition.makeCondition("partyClassificationGroupId" ,EntityOperator.EQUALS,"REGIONAL_OFFICE"));
		PartyClassification = delegator.findList("PartyClassification",EntityCondition.makeCondition(condList,EntityOperator.AND), null, null, null, false );
		if(UtilValidate.isNotEmpty(PartyClassification)){
			condList.clear();
			condList.add(EntityCondition.makeCondition("partyIdFrom" ,EntityOperator.EQUALS,parameters.partyIdFrom));
			condList.add(EntityCondition.makeCondition("partyRelationshipTypeId" ,EntityOperator.EQUALS,"BRANCH_CUSTOMER"));
			PartyRelationship = delegator.findList("PartyRelationship",EntityCondition.makeCondition(condList,EntityOperator.AND), null, null, null, false );
			if(UtilValidate.isNotEmpty(PartyRelationship)){
				partyIdList = EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);
				parameters.partyIdFrom = partyIdList;
				parameters.partyIdFrom_op = "in";
			}
			
		}
	}
}


if(parameters.invoiceTypeId == "PURCHASE_INVOICE"){
	if(UtilValidate.isNotEmpty(parameters.partyId)){
		partyIdFrom = parameters.partyId;
		parameters.tempPartyId = partyIdFrom;
		condList = [];
		condList.add(EntityCondition.makeCondition("partyId" ,EntityOperator.EQUALS,partyIdFrom));
		condList.add(EntityCondition.makeCondition("partyClassificationGroupId" ,EntityOperator.EQUALS,"REGIONAL_OFFICE"));
		PartyClassification = delegator.findList("PartyClassification",EntityCondition.makeCondition(condList,EntityOperator.AND), null, null, null, false );
		if(UtilValidate.isNotEmpty(PartyClassification)){
			condList.clear();
			condList.add(EntityCondition.makeCondition("partyIdFrom" ,EntityOperator.EQUALS,partyIdFrom));
			condList.add(EntityCondition.makeCondition("partyRelationshipTypeId" ,EntityOperator.EQUALS,"BRANCH_CUSTOMER"));
			PartyRelationship = delegator.findList("PartyRelationship",EntityCondition.makeCondition(condList,EntityOperator.AND), null, null, null, false );
			if(UtilValidate.isNotEmpty(PartyRelationship)){
				partyIdList = EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);
				parameters.partyId = partyIdList;
				parameters.partyId_op = "in";
				
			}
			
		}
	}
}



