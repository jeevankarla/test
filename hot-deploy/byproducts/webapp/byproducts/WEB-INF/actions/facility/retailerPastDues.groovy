import org.ofbiz.base.util.UtilDateTime;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javolution.util.FastList;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.accounting.payment.PaymentWorker;

dctx = dispatcher.getDispatchContext();
if(parameters.boothId){
	facilityId = parameters.boothId;
}else{
	facilityId = context.facilityId;
}

JSONObject boothsDuesDaywiseJSON = new JSONObject();
facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
if(facility){
	categoryTypeEnum = facility.categoryTypeEnum;
	boothDuesDetail = [:];
	if(categoryTypeEnum == "CR_INST"){
		boothDuesDetail = ByProductNetworkServices.getDaywiseCRInstDues(dctx, [userLogin: userLogin, facilityId:facilityId, isByParty:Boolean.TRUE, enableCRInst:Boolean.TRUE]);
	}
	else{
		boothDuesDetail = ByProductNetworkServices.getDaywiseBoothDues(dctx, [userLogin: userLogin, facilityId:facilityId, isByParty:Boolean.TRUE, enableCRInst:Boolean.TRUE]);
	}
	
	resultCtx = PaymentWorker.getNotAppliedPaymentDetailsForParty(dctx, [userLogin: userLogin, partyIdFrom:facility.ownerPartyId, partyIdTo: "Company"]);
	
	unAppliedTotalAmt = resultCtx.get("unAppliedPaymentTotalAmt");
	unappliedAmtDetail = resultCtx.get("UnAppliedPaymentDetails");
	unAppliedPaymentList = resultCtx.get("unAppliedPaymentList");
	
	duesList = boothDuesDetail["boothDuesList"];
	JSONArray boothDuesList= new JSONArray();
	duesList.each { due ->
		JSONObject dueJSON = new JSONObject();
		dueJSON.put("supplyDate", UtilDateTime.toDateString(due.supplyDate, "dd MMM, yyyy"));
		dueJSON.put("amount", due.amount);
		dueJSON.put("amount", UtilFormatOut.formatCurrency(due.amount, context.get("currencyUomId"), locale));
		boothDuesList.add(dueJSON);
	}
	
	JSONArray boothUnAppPaymentList= new JSONArray();
	unAppliedPaymentList.each { eachPay ->
		JSONObject advPayJSON = new JSONObject();
		paymentId = eachPay.paymentId;
		unAppAmt = unappliedAmtDetail.get(paymentId);
		paymentType = eachPay.paymentTypeId;
		if(paymentType != "SECURITYDEPSIT_PAYIN"){
			advPayJSON.put("supplyDate", UtilDateTime.toDateString(eachPay.paymentDate, "dd MMM, yyyy"));
			advPayJSON.put("amount", unAppAmt);
			advPayJSON.put("amount", UtilFormatOut.formatCurrency(unAppAmt, context.get("currencyUomId"), locale));
			boothUnAppPaymentList.add(advPayJSON);
		}
		else{
			unAppliedTotalAmt = unAppliedTotalAmt-unAppAmt;
		}
	}
	JSONObject boothDuesMap = new JSONObject();
	if(!unAppliedTotalAmt){
		unAppliedTotalAmt = 0;
	}
	totalDueAmt = boothDuesDetail["totalAmount"]-unAppliedTotalAmt;
	boothDuesMap.put("totalAmount", UtilFormatOut.formatCurrency(totalDueAmt, context.get("currencyUomId"), locale));
	boothDuesMap.put("boothDuesList", boothDuesList);
	boothDuesMap.put("boothAdvPaymentList", boothUnAppPaymentList);
	boothsDuesDaywiseJSON.put(facilityId, boothDuesMap);
	context.boothsDuesDaywiseJSON = boothsDuesDaywiseJSON;
	request.setAttribute("boothsDuesDaywiseJSON", boothsDuesDaywiseJSON);
}

return "success";