import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.network.NetworkServices;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;


/*RECNO	WEF	ZONE	ROUTE	BOOTHNO	SEQNO	CAT	BNAME	LOCALITY	STATUS	STATUSDT	ABSENTDUE	ATCHD
 * 	PRDFROM	PRDUPTO	DEPOSIT	ADEPOSIT	RECTNO	RECTDT	RFNDDPST	RFNDDATE	PROCNO	REMARKS	ALLOTEE	FNAME
 * 	ADDRESS	LANDLINE	MOBILENO	TCOST	BANK	BRANCH	BACNO	ECSTEST	ECSCONF	MICRNO	PANID	ACTIVE	MAS	DELETED */

boothMasterList = [];
facilityList = delegator.findList("Facility",EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "BOOTH") , null, null, null, false);


dctx = dispatcher.getDispatchContext();

facilityList.each { facilityDetail ->
	boothCsvMap =[:];
	boothDetails = (NetworkServices.getBoothDetails(dctx , UtilMisc.toMap("boothId",facilityDetail.get("facilityId")))).get("boothDetails");
	
	boothCsvMap["openedDate"] = facilityDetail.get("openedDate");
	
	if(facilityDetail.get("categoryTypeEnum").equals("CR_INST")){
		boothCsvMap["categoryTypeEnum"] = 1;
	}
	if(facilityDetail.get("categoryTypeEnum").equals("SO_INST")){
		boothCsvMap["categoryTypeEnum"] = 2;
	}
	if(facilityDetail.get("categoryTypeEnum").equals("PTC")){
		boothCsvMap["categoryTypeEnum"] = 3;
	}
	if(facilityDetail.get("categoryTypeEnum").equals("VENDOR")){
		boothCsvMap["categoryTypeEnum"] = 6;
	}
	boothCsvMap["zoneId"] = boothDetails.get("zoneId");
	boothCsvMap["routeId"] = (boothDetails.get("routeId")).substring(2);
	boothCsvMap["boothId"] = boothDetails.get("boothId");
	boothCsvMap["boothName"] = boothDetails.get("boothName");
	boothCsvMap["vendorName"] = boothDetails.get("vendorName");
	boothCsvMap["sequenceNum"] = facilityDetail.get("sequenceNum");
	partyTelephone= dispatcher.runSync("getPartyTelephone", [partyId: facilityDetail.get("ownerPartyId"), userLogin: userLogin]);
	boothCsvMap["MOBILENO"] = partyTelephone.contactNumber;
	boothMasterList.add(boothCsvMap);
}

context.boothMasterList = boothMasterList;

