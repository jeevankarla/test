import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javolution.util.FastList;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.base.util.*;

if(parameters.statusId){
	statusId = parameters.statusId;
}else{
	statusId = context.statusId;
}

dctx = dispatcher.getDispatchContext();
boothRouteIdsMap=context.boothRouteIdsMap;
boothPaymentsCsvList =FastList.newInstance();
reportTypeFlag=parameters.reportTypeFlag;
if(UtilValidate.isNotEmpty(reportTypeFlag) && "DailyPaymentCheckList".equals(reportTypeFlag)){
	boothPaymentsList=bankPaidMap.entrySet();
	boothPaymentsList.each { boothPaymentEach ->
		bankPaymentList=boothPaymentEach.getValue();
		//boothPaymentsCsvList.addAll(bankPaymentList);
		//eachBank Payments add to List
		bankPaymentList.each{ boothPayment->
			boothPaymentCsvMap =[:];
		boothPaymentCsvMap["paymentId"] =boothPayment.paymentId;
		boothPaymentCsvMap["routeId"] = boothRouteIdsMap.get(boothPayment.facilityId);
		boothPaymentCsvMap["Date"] =UtilDateTime.toDateString(boothPayment.paymentDate,"dd MMM, yyyy");
		boothPaymentCsvMap["facilityId"] = boothPayment.facilityId;
		boothPaymentCsvMap["facilityName"] = boothPayment.facilityName;
		boothPaymentCsvMap["createdByUserLogin"] =boothPayment.createdByUserLogin;
		boothPaymentCsvMap["issuingAuthority"] =boothPayment.issuingAuthority;
		boothPaymentCsvMap["paymentMethodTypeId"] = boothPayment.paymentMethodTypeId;
		boothPaymentCsvMap["amount"] = (new BigDecimal(boothPayment.amount));
		boothPaymentsCsvList.add(boothPaymentCsvMap);
		}
	}
}else{
	boothPaymentsList.each { boothPayment ->
	boothPaymentCsvMap =[:];
	boothDetails = (ByProductNetworkServices.getBoothDetails(dctx , UtilMisc.toMap("boothId",boothPayment.get("facilityId")))).get("boothDetails");
	
	boothPaymentCsvMap["supplyDate"] = UtilDateTime.toDateString(paymentTimestamp,"MM/dd/yy");
	boothPaymentCsvMap["zoneId"] = boothDetails.get("zoneId");
	boothPaymentCsvMap["routeId"] = boothRouteIdsMap.get(boothPayment.get("facilityId"));
	boothPaymentCsvMap["boothId"] = boothDetails.get("boothId");
	boothPaymentCsvMap["boothName"] = boothDetails.get("boothName");
	boothPaymentCsvMap["vendorName"] = boothDetails.get("vendorName");	
	if(statusId == "PAID"){
		boothPaymentCsvMap["paymentMethodTypeId"] = boothPayment.get("paymentMethodTypeId");
		boothPaymentCsvMap["amount"] = (new BigDecimal(boothPayment.get("amount")));
		boothPaymentCsvMap["userId"] =  boothPayment.get("userId");;
	}else{
		boothPaymentCsvMap["paymentMethodTypeId"] = null;
		boothPaymentCsvMap["amount"] = (new BigDecimal(boothPayment.get("grandTotal")));
		boothPaymentCsvMap["userId"] = null;
	}
	boothPaymentsCsvList.add(boothPaymentCsvMap);
}
}
context.boothPaymentsCsvList = boothPaymentsCsvList;


