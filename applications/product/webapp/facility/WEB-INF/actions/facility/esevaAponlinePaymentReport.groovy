import org.ofbiz.base.util.UtilNumber;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.network.NetworkServices;
import org.ofbiz.base.util.*;

boothPaymentsList = context.boothPaymentsList;
boothPaymentsCsvList = [];
 int decimals;
 int rounding;
decimals = 0;//UtilNumber.getBigDecimalScale("order.decimals");
rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
	

//RECTDATE	TRSPTRID	ZONE	ROUTE	BOOTH	BNAME	ALLOTEE	CASHVALU

dctx = dispatcher.getDispatchContext();

boothPaymentsList.each { boothPayment ->
	boothPaymentCsvMap =[:];	
	boothDetails = (NetworkServices.getBoothDetails(dctx , UtilMisc.toMap("boothId",boothPayment.get("facilityId")))).get("boothDetails");
	
	boothPaymentCsvMap["supplyDate"] = UtilDateTime.toDateString(paymentTimestamp,"MM/dd/yy");
	if(boothDetails.get("categoryTypeEnum").equals("PTC") || boothDetails.get("isUpcountry").equals("Y")){
		boothPaymentCsvMap["distributorId"] = (boothDetails.get("distributorId")).substring(6);
	}else{
		boothPaymentCsvMap["distributorId"] = "91";
	}
	
	boothPaymentCsvMap["zoneId"] = boothDetails.get("zoneId");	
	boothPaymentCsvMap["routeId"] = (boothDetails.get("routeId")).substring(2);
	boothPaymentCsvMap["boothId"] = boothDetails.get("boothId");
	boothPaymentCsvMap["boothName"] = boothDetails.get("boothName");
	boothPaymentCsvMap["vendorName"] = boothDetails.get("vendorName");
	boothPaymentCsvMap["amount"] = (new BigDecimal(boothPayment.get("grandTotal"))).setScale(decimals ,rounding).toString();
	boothPaymentsCsvList.add(boothPaymentCsvMap);	
}

context.boothPaymentsCsvList = boothPaymentsCsvList;

