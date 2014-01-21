import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javolution.util.FastList;
import org.ofbiz.network.NetworkServices;
import org.ofbiz.base.util.*;

if(parameters.statusId){
	statusId = parameters.statusId;
}else{
	statusId = context.statusId;
}

dctx = dispatcher.getDispatchContext();

boothPaymentsCsvList =FastList.newInstance();

	boothPaymentsList.each { boothPayment ->
	boothPaymentCsvMap =[:];
	boothDetails = (NetworkServices.getBoothDetails(dctx , UtilMisc.toMap("boothId",boothPayment.get("facilityId")))).get("boothDetails");
	
	boothPaymentCsvMap["supplyDate"] = UtilDateTime.toDateString(paymentTimestamp,"MM/dd/yy");
	boothPaymentCsvMap["zoneId"] = boothDetails.get("zoneId");
	boothPaymentCsvMap["routeId"] = (boothDetails.get("routeId")).substring(2);
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

context.boothPaymentsCsvList = boothPaymentsCsvList;


