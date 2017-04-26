import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.SortedMap;
 
import javolution.util.FastMap;
import javolution.util.FastList;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;
import java.util.Map.Entry;
import org.ofbiz.accounting.invoice.InvoiceWorker;

SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");

Timestamp fromDate;
Timestamp thruDate;

Timestamp fStMnthfromDate;
Timestamp fStMnththruDate;

Timestamp sndMnthfromDate;
Timestamp sndMnththruDate;

Timestamp thrdMnthfromDate;
Timestamp thrdMnththruDate;

Timestamp frthMnthfromDate;
Timestamp frthMnththruDate;

Timestamp fromfifthMnthfromDate;
Timestamp fromfifthMnththruDate;

Timestamp firstyearfromDate;
Timestamp firstyearthruDate;

Timestamp secondyearfromDate;
Timestamp secondyearthruDate;

dateStr=parameters.APRDate;
reportType=parameters.reportType;
days=parameters.days;
roId=parameters.roId;
context.days=days;

branchContextForADD=[:];
branchContextForADD.put("branchId",roId);
try{
	resultCtx = dispatcher.runSync("getBoHeader", branchContextForADD);
	if(ServiceUtil.isError(resultCtx)){
		Debug.logError("Problem in BO Header ", module);
		return ServiceUtil.returnError("Problem in fetching financial year ");
	}
	if(resultCtx.get("boHeaderMap")){
		boHeaderMap=resultCtx.get("boHeaderMap");
		
		if(boHeaderMap.get("header0")){
			BOAddress=boHeaderMap.get("header0");
		}
		if(boHeaderMap.get("header1")){
			BOEmail=boHeaderMap.get("header1");
		}
	}
}catch(GenericServiceException e){
	Debug.logError(e, module);
	return ServiceUtil.returnError(e.getMessage());
}
context.BOAddress=BOAddress;
context.BOEmail=BOEmail;
if(UtilValidate.isEmpty(dateStr)){
	try {
		dateStr = UtilDateTime.nowTimestamp()
		dateStr = UtilDateTime.getDayStart(dateStr);
		dateStr=UtilDateTime.toDateString(dateStr, "MMM dd,yyyy");
	} catch (ParseException e) {
	}
}

if(UtilValidate.isNotEmpty(dateStr)){
	if(days == "30days"){
	thruDate = new java.sql.Timestamp(sdf.parse(dateStr).getTime());
	fromDate = UtilDateTime.addDaysToTimestamp(thruDate,-1460);
	
	fStMnththruDate = new java.sql.Timestamp(sdf.parse(dateStr).getTime());
	fStMnthfromDate = UtilDateTime.addDaysToTimestamp(fStMnththruDate,-30);
	
	sndMnththruDate = UtilDateTime.addDaysToTimestamp(fStMnthfromDate,-1);
	sndMnthfromDate = UtilDateTime.addDaysToTimestamp(sndMnththruDate,-30);
	
	thrdMnththruDate = UtilDateTime.addDaysToTimestamp(sndMnthfromDate,-1);
	thrdMnthfromDate = UtilDateTime.addDaysToTimestamp(thrdMnththruDate,-30);
	
	frthMnththruDate = UtilDateTime.addDaysToTimestamp(thrdMnthfromDate,-1);
	frthMnthfromDate = UtilDateTime.addDaysToTimestamp(frthMnththruDate,-90);
	
	fromfifthMnththruDate = UtilDateTime.addDaysToTimestamp(frthMnthfromDate,-1);
	fromfifthMnthfromDate = UtilDateTime.addDaysToTimestamp(fromfifthMnththruDate,-181);
	
	firstyearthruDate = UtilDateTime.addDaysToTimestamp(fromfifthMnthfromDate,-1);
	firstyearfromDate = UtilDateTime.addDaysToTimestamp(firstyearthruDate,-364);
	
	secondyearthruDate = UtilDateTime.addDaysToTimestamp(firstyearfromDate,-1);
	secondyearfromDate = UtilDateTime.addDaysToTimestamp(secondyearthruDate,-364);
	}
	else{
		thruDate = new java.sql.Timestamp(sdf.parse(dateStr).getTime());
		fromDate = UtilDateTime.addDaysToTimestamp(thruDate,-1460);
		
		fStMnththruDate = new java.sql.Timestamp(sdf.parse(dateStr).getTime());
		fStMnthfromDate = UtilDateTime.addDaysToTimestamp(fStMnththruDate,-45);
		
		sndMnththruDate = UtilDateTime.addDaysToTimestamp(fStMnthfromDate,-1);
		sndMnthfromDate = UtilDateTime.addDaysToTimestamp(sndMnththruDate,-45);
		
		frthMnththruDate = UtilDateTime.addDaysToTimestamp(sndMnthfromDate,-1);
		frthMnthfromDate = UtilDateTime.addDaysToTimestamp(frthMnththruDate,-90);
		
		fromfifthMnththruDate = UtilDateTime.addDaysToTimestamp(frthMnthfromDate,-1);
		fromfifthMnthfromDate = UtilDateTime.addDaysToTimestamp(fromfifthMnththruDate,-181);
		
		firstyearthruDate = UtilDateTime.addDaysToTimestamp(fromfifthMnthfromDate,-1);
		firstyearfromDate = UtilDateTime.addDaysToTimestamp(firstyearthruDate,-364);
		
		secondyearthruDate = UtilDateTime.addDaysToTimestamp(firstyearfromDate,-1);
		secondyearfromDate = UtilDateTime.addDaysToTimestamp(secondyearthruDate,-364);	
	}
}

conditionList=[];
if(reportType=="ADV_RECEIVED"){
	conditionList.add(EntityCondition.makeCondition("paymentTypeId", EntityOperator.IN,["ONACCOUNT_PAYIN","INDENTADV_PAYIN"]));
}
else{
	conditionList.add(EntityCondition.makeCondition("paymentTypeId", EntityOperator.EQUALS,"ONACCOUNT_PAYOUT"));
}
if(reportType=="ADV_RECEIVED"){
	conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, roId));
}
else{
	conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, roId));
}
conditionList.add(EntityCondition.makeCondition("paymentDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
conditionList.add(EntityCondition.makeCondition("paymentDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN,["PMNT_NOT_PAID","PMNT_VOID"]));
cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
advancePaymentList = delegator.findList("Payment", cond, null, null, null, false);
paymentIds = EntityUtil.getFieldListFromEntityList(advancePaymentList, "paymentId", true);

paymentList=[];
for(eachPaymentId in paymentIds){
	tempMap=[:];
	fstMntAdvPmntTotals =0;
	secMntAdvPmntTotals =0;
	thrdMntAdvPmntTotals =0;
	frthToSixthMntAdvPmntTotals =0;
	sixthToOneYrAdvPmntTotals=0;
	oneToTwoYrsAdvPmntTotals=0;
	secndToThirdYrAdvPmntTotals=0;
	above3YrsAdvPmntTotals=0;
	
	fstMntApldPmntTotals =0;
	secMntApldPmntTotals =0;
	thrdMntApldPmntTotals =0;
	frthToSixthMntApldPmntTotals =0;
	sixthToOneYrApldPmntTotals=0;
	oneToTwoYrsApldPmntTotals=0;
	secndToThirdYrApldPmntTotals=0;
	above3YrsApldPmntTotals=0;
	appliedPmntsForEachId = delegator.findList("PaymentApplication", EntityCondition.makeCondition("paymentId", EntityOperator.EQUALS,eachPaymentId),null, null, null, false);
		
	advancePaymentForEachId = EntityUtil.filterByCondition(advancePaymentList, EntityCondition.makeCondition("paymentId", EntityOperator.EQUALS, eachPaymentId));
	
	firstMntAdvPmnt = EntityUtil.filterByCondition(advancePaymentForEachId, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, fStMnthfromDate) ,EntityOperator.AND,
		EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,fStMnththruDate)));
	
	secndMntAdvPmnt = EntityUtil.filterByCondition(advancePaymentForEachId, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, sndMnthfromDate) ,EntityOperator.AND,
		EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,sndMnththruDate)));
	
	if(days == "30days"){
		thrdMntAdvPmnt = EntityUtil.filterByCondition(advancePaymentForEachId, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, thrdMnthfromDate) ,EntityOperator.AND,
			EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,thrdMnththruDate)));
	}
	frthToSixthMntAdvPmnt = EntityUtil.filterByCondition(advancePaymentForEachId, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, frthMnthfromDate) ,EntityOperator.AND,
		EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,frthMnththruDate)));
	
	sixthToOneYrAdvPmnt = EntityUtil.filterByCondition(advancePaymentForEachId, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, fromfifthMnthfromDate) ,EntityOperator.AND,
		EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,fromfifthMnththruDate)));
	
	oneToTwoYrsAdvPmnt = EntityUtil.filterByCondition(advancePaymentForEachId, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, firstyearfromDate) ,EntityOperator.AND,
		EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,firstyearthruDate)));
	
	secndToThirdYrAdvPmnt = EntityUtil.filterByCondition(advancePaymentForEachId, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, secondyearfromDate) ,EntityOperator.AND,
		EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,secondyearthruDate )));
	above3YrsAdvPmnt = EntityUtil.filterByCondition(advancePaymentList, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,secondyearfromDate)));
	
	if(UtilValidate.isNotEmpty(above3YrsAdvPmnt)){
		above3YrsAdvPmnt=EntityUtil.getFirst(above3YrsAdvPmnt);
		above3YrsAdvPmntTotals = above3YrsAdvPmnt.amount;	
	}
	if(UtilValidate.isNotEmpty(secndToThirdYrAdvPmnt)){
		secndToThirdYrAdvPmnt=EntityUtil.getFirst(secndToThirdYrAdvPmnt);
		secndToThirdYrAdvPmntTotals = firstMntAdvPmnt.amount;		
	}
	secndToThirdYrAdvPmntTotals=secndToThirdYrAdvPmntTotals+above3YrsAdvPmntTotals;
	
	if(UtilValidate.isNotEmpty(oneToTwoYrsAdvPmnt)){
		oneToTwoYrsAdvPmnt=EntityUtil.getFirst(oneToTwoYrsAdvPmnt);
		oneToTwoYrsAdvPmntTotals =oneToTwoYrsAdvPmnt.amount;
	}
	oneToTwoYrsAdvPmntTotals=oneToTwoYrsAdvPmntTotals+secndToThirdYrAdvPmntTotals;
	
	if(UtilValidate.isNotEmpty(sixthToOneYrAdvPmnt)){
		sixthToOneYrAdvPmnt=EntityUtil.getFirst(sixthToOneYrAdvPmnt);
		sixthToOneYrAdvPmntTotals =sixthToOneYrAdvPmnt.amount;
	}
	sixthToOneYrAdvPmntTotals=sixthToOneYrAdvPmntTotals+oneToTwoYrsAdvPmntTotals;
		
	if(UtilValidate.isNotEmpty(frthToSixthMntAdvPmnt)){
		frthToSixthMntAdvPmnt=EntityUtil.getFirst(frthToSixthMntAdvPmnt);
		frthToSixthMntAdvPmntTotals =frthToSixthMntAdvPmnt.amount;	
	}
	frthToSixthMntAdvPmntTotals=frthToSixthMntAdvPmntTotals+sixthToOneYrAdvPmntTotals;
	
	if(days == "30days"){
		if(UtilValidate.isNotEmpty(thrdMntAdvPmnt)){
			thrdMntAdvPmnt=EntityUtil.getFirst(thrdMntAdvPmnt);
			thrdMntAdvPmntTotals = thrdMntAdvPmnt.amount;	
		}
	}
	thrdMntAdvPmntTotals=thrdMntAdvPmntTotals+frthToSixthMntAdvPmntTotals;
		
	if(UtilValidate.isNotEmpty(secndMntAdvPmnt)){
		secndMntAdvPmnt=EntityUtil.getFirst(secndMntAdvPmnt);
		secMntAdvPmntTotals = secndMntAdvPmnt.amount;
		
		secMntAdvPmntTotals=secMntAdvPmntTotals+thrdMntAdvPmntTotals;
	}
	
	if(UtilValidate.isNotEmpty(firstMntAdvPmnt)){
		firstMntAdvPmnt=EntityUtil.getFirst(firstMntAdvPmnt);
		fstMntAdvPmntTotals = firstMntAdvPmnt.amount;
		
	}
	fstMntAdvPmntTotals=fstMntAdvPmntTotals+secMntAdvPmntTotals;
	
	firstMntApldPmnt = EntityUtil.filterByCondition(appliedPmntsForEachId, EntityCondition.makeCondition(EntityCondition.makeCondition("applicationDate",EntityOperator.GREATER_THAN_EQUAL_TO, fStMnthfromDate) ,EntityOperator.AND,
		EntityCondition.makeCondition("applicationDate",EntityOperator.LESS_THAN_EQUAL_TO,fStMnththruDate )));
	
	secndMntApldPmnt = EntityUtil.filterByCondition(appliedPmntsForEachId, EntityCondition.makeCondition(EntityCondition.makeCondition("applicationDate",EntityOperator.GREATER_THAN_EQUAL_TO, sndMnthfromDate) ,EntityOperator.AND,
		EntityCondition.makeCondition("applicationDate",EntityOperator.LESS_THAN_EQUAL_TO,sndMnththruDate )));
	
	if(days == "30days"){
		thrdMntApldPmnt = EntityUtil.filterByCondition(appliedPmntsForEachId, EntityCondition.makeCondition(EntityCondition.makeCondition("applicationDate",EntityOperator.GREATER_THAN_EQUAL_TO, thrdMnthfromDate) ,EntityOperator.AND,
			EntityCondition.makeCondition("applicationDate",EntityOperator.LESS_THAN_EQUAL_TO,thrdMnththruDate )));
	
	}
	frthToSixthMntApldPmnt = EntityUtil.filterByCondition(appliedPmntsForEachId, EntityCondition.makeCondition(EntityCondition.makeCondition("applicationDate",EntityOperator.GREATER_THAN_EQUAL_TO, frthMnthfromDate) ,EntityOperator.AND,
		EntityCondition.makeCondition("applicationDate",EntityOperator.LESS_THAN_EQUAL_TO,frthMnththruDate )));
	
	sixthToOneYrApldPmnt = EntityUtil.filterByCondition(appliedPmntsForEachId, EntityCondition.makeCondition(EntityCondition.makeCondition("applicationDate",EntityOperator.GREATER_THAN_EQUAL_TO, fromfifthMnthfromDate) ,EntityOperator.AND,
		EntityCondition.makeCondition("applicationDate",EntityOperator.LESS_THAN_EQUAL_TO,fromfifthMnththruDate )));
	
	oneToTwoYrsApldPmnt = EntityUtil.filterByCondition(appliedPmntsForEachId, EntityCondition.makeCondition(EntityCondition.makeCondition("applicationDate",EntityOperator.GREATER_THAN_EQUAL_TO, firstyearfromDate) ,EntityOperator.AND,
		EntityCondition.makeCondition("applicationDate",EntityOperator.LESS_THAN_EQUAL_TO,firstyearthruDate)));
	
	secndToThirdYrApldPmnt = EntityUtil.filterByCondition(appliedPmntsForEachId, EntityCondition.makeCondition(EntityCondition.makeCondition("applicationDate",EntityOperator.GREATER_THAN_EQUAL_TO, secondyearfromDate) ,EntityOperator.AND,
		EntityCondition.makeCondition("applicationDate",EntityOperator.LESS_THAN_EQUAL_TO,secondyearthruDate )));
	
	above3YrsApldPmnt = EntityUtil.filterByCondition(appliedPmntsForEachId, EntityCondition.makeCondition(EntityCondition.makeCondition("applicationDate",EntityOperator.LESS_THAN_EQUAL_TO,secondyearfromDate)));
	
	if(UtilValidate.isNotEmpty(above3YrsApldPmnt)){
		for(eachPayment in above3YrsApldPmnt){
			total=eachPayment.amountApplied;
			above3YrsApldPmntTotals =above3YrsApldPmntTotals+total;
		}
	}
	
	if(UtilValidate.isNotEmpty(secndToThirdYrApldPmnt)){
		for(eachPayment in secndToThirdYrApldPmnt){
			total=eachPayment.amountApplied;
			secndToThirdYrApldPmntTotals =secndToThirdYrApldPmntTotals+total;
		}
	}
	secndToThirdYrApldPmntTotals =secndToThirdYrApldPmntTotals+above3YrsApldPmntTotals;
	
	if(UtilValidate.isNotEmpty(oneToTwoYrsApldPmnt)){
		for(eachPayment in oneToTwoYrsApldPmnt){
			total=eachPayment.amountApplied;
			oneToTwoYrsApldPmntTotals =oneToTwoYrsApldPmntTotals+total;
		}
	}
	oneToTwoYrsApldPmntTotals =oneToTwoYrsApldPmntTotals+secndToThirdYrApldPmntTotals;
	
	if(UtilValidate.isNotEmpty(sixthToOneYrApldPmnt)){
		for(eachPayment in sixthToOneYrApldPmnt){
			total=eachPayment.amountApplied;
			sixthToOneYrApldPmntTotals =sixthToOneYrApldPmntTotals+total;
		}
		
	}
	sixthToOneYrApldPmntTotals =sixthToOneYrApldPmntTotals+oneToTwoYrsApldPmntTotals;	
	
	if(UtilValidate.isNotEmpty(frthToSixthMntApldPmnt)){
		for(eachPayment in frthToSixthMntApldPmnt){
			total=eachPayment.amountApplied;
			frthToSixthMntApldPmntTotals =frthToSixthMntApldPmntTotals+total;
		}
		
	}
	frthToSixthMntApldPmntTotals =frthToSixthMntApldPmntTotals+sixthToOneYrApldPmntTotals;
	
	if(days == "30days"){
		if(UtilValidate.isNotEmpty(thrdMntApldPmnt)){
			for(eachPayment in thrdMntApldPmnt){
				total=eachPayment.amountApplied;
				thrdMntApldPmntTotals =thrdMntApldPmntTotals+total;
			}
		}
	}
	thrdMntApldPmntTotals =thrdMntApldPmntTotals+frthToSixthMntApldPmntTotals;
	
	if(UtilValidate.isNotEmpty(secndMntApldPmnt)){
		for(eachPayment in secndMntApldPmnt){
			total=eachPayment.amountApplied;
			secMntApldPmntTotals =secMntApldPmntTotals+total;
		}
	}
	secMntApldPmntTotals =secMntApldPmntTotals+thrdMntApldPmntTotals;
	
	if(UtilValidate.isNotEmpty(firstMntApldPmnt)){
		for(eachPayment in firstMntApldPmnt){
			total=eachPayment.amountApplied;
			fstMntApldPmntTotals =fstMntApldPmntTotals+total;
		}
	}
	fstMntApldPmntTotals =fstMntApldPmntTotals+secMntApldPmntTotals;
	
	fstMntAdvPmntTotals =fstMntAdvPmntTotals-fstMntApldPmntTotals;
	secMntAdvPmntTotals =secMntAdvPmntTotals-secMntApldPmntTotals;
	if(days == "30days"){
		thrdMntAdvPmntTotals =thrdMntAdvPmntTotals-thrdMntApldPmntTotals;
	}
	frthToSixthMntAdvPmntTotals =frthToSixthMntAdvPmntTotals-frthToSixthMntApldPmntTotals;
	sixthToOneYrAdvPmntTotals=sixthToOneYrAdvPmntTotals-sixthToOneYrApldPmntTotals;
	oneToTwoYrsAdvPmntTotals=oneToTwoYrsAdvPmntTotals-oneToTwoYrsApldPmntTotals;
	secndToThirdYrAdvPmntTotals=secndToThirdYrAdvPmntTotals-secndToThirdYrApldPmntTotals;
	above3YrsAdvPmntTotals=above3YrsAdvPmntTotals-above3YrsApldPmntTotals;
	if(advancePaymentForEachId){
		pmntDetails=EntityUtil.getFirst(advancePaymentForEachId);
		if(reportType=="ADV_RECEIVED")
			partyId=pmntDetails.partyIdFrom;
		else
		partyId=pmntDetails.partyIdTo;
	}		
	if(advancePaymentForEachId){
		pmntDetails=EntityUtil.getFirst(advancePaymentForEachId);
		paymentDate=pmntDetails.paymentDate;
	}
	String partyName = PartyHelper.getPartyName(delegator,partyId,false);
	tempMap.put("paymentId", eachPaymentId);
	tempMap.put("paymentDate", paymentDate);
	tempMap.put("partyName", partyName);
	tempMap.put("fstMntAdvPmntTotals", fstMntAdvPmntTotals);
	tempMap.put("secMntAdvPmntTotals", secMntAdvPmntTotals);
	if(days == "30days"){
		tempMap.put("thrdMntAdvPmntTotals", thrdMntAdvPmntTotals);
	}
	tempMap.put("frthToSixthMntAdvPmntTotals", frthToSixthMntAdvPmntTotals);
	tempMap.put("sixthToOneYrAdvPmntTotals", sixthToOneYrAdvPmntTotals);
	tempMap.put("oneToTwoYrsAdvPmntTotals", oneToTwoYrsAdvPmntTotals);
	tempMap.put("secndToThirdYrAdvPmntTotals", secndToThirdYrAdvPmntTotals);
	tempMap.put("above3YrsAdvPmntTotals", above3YrsAdvPmntTotals);
						
		paymentList.add(tempMap);
}

context.paymentList=paymentList;
