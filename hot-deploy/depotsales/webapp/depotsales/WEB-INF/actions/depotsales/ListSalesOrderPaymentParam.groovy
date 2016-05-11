import org.ofbiz.base.util.UtilDateTime;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import net.sf.json.JSONObject;
import javolution.util.FastList;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.party.contact.ContactMechWorker;

dctx = dispatcher.getDispatchContext();
Map boothsPaymentsDetail = [:];

partyId = userLogin.get("partyId");

resultCtx = dispatcher.runSync("getCustomerBranch",UtilMisc.toMap("userLogin",userLogin));

Map formatMap = [:];
List formatList = [];
List productStoreList = resultCtx.get("productStoreList");
context.productStoreList = productStoreList;

for (eachList in productStoreList) {
	formatMap = [:];
	formatMap.put("productStoreName",eachList.get("storeName"));
	formatMap.put("payToPartyId",eachList.get("payToPartyId"));
	formatList.addAll(formatMap);
}
context.formatList = formatList;

branchList = EntityUtil.getFieldListFromEntityList(productStoreList, "payToPartyId", true);
if(UtilValidate.isNotEmpty(parameters.partyIdFrom)){
	branchList.clear();
	branchList.add(parameters.partyIdFrom)
}

//branchId = parameters.partyIdFrom;

salesChannel = parameters.salesChannelEnumId;



paramOrderId = "";
paramFacilityId = "";
paramEstimatedDeliveryDate = "";
paramStatusId = "";
paramBranch = "";
indentDateSort = ""
if(parameters.orderId)
	 paramOrderId = parameters.orderId;
   
if(parameters.partyId)
   paramFacilityId = parameters.partyId;

if(parameters.estimatedDeliveryDate)
   paramEstimatedDeliveryDate = parameters.estimatedDeliveryDate;
   
if(parameters.statusId)
   paramStatusId = parameters.statusId;
   
if(parameters.partyIdFrom)
  paramBranch = parameters.partyIdFrom;
  
if(parameters.indentDateSort)
  indentDateSort = parameters.indentDateSort;
  
   
context.paramOrderId = paramOrderId;
context.paramFacilityId = paramFacilityId;
context.paramEstimatedDeliveryDate = paramEstimatedDeliveryDate;
context.paramStatusId = paramStatusId;
context.paramBranch = paramBranch;
context.indentDateSort = indentDateSort;

	 



facilityOrderId = parameters.orderId;
facilityDeliveryDate = parameters.estimatedDeliveryDate;
productId = parameters.productId;
facilityStatusId = parameters.statusId;
facilityPartyId = parameters.partyId;
 screenFlag = parameters.screenFlag;

facilityDateStart = null;
facilityDateEnd = null;
if(UtilValidate.isNotEmpty(facilityDeliveryDate)){
	def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	try {
		transDate = new java.sql.Timestamp(sdf.parse(facilityDeliveryDate+" 00:00:00").getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + facilityDeliveryDate, "");
	}
	facilityDateStart = UtilDateTime.getDayStart(transDate);
	facilityDateEnd = UtilDateTime.getDayEnd(transDate);
}


condtList = [];
condtList.add(EntityCondition.makeCondition("parentTypeId" ,EntityOperator.EQUALS, "MONEY"));
cond = EntityCondition.makeCondition(condtList, EntityOperator.AND);
PaymentMethodType = delegator.findList("PaymentMethodType", cond, UtilMisc.toSet("paymentMethodTypeId","description"), null, null ,false);
context.PaymentMethodType = PaymentMethodType;

