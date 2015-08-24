import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;

import java.util.*;
import java.lang.*;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import java.sql.*;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

import org.ofbiz.service.ServiceUtil;
import org.webslinger.resolver.UtilDateResolver;

import java.util.Calendar;

import javolution.util.FastList;
import javolution.util.FastMap;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import org.ofbiz.party.party.PartyHelper;

String displayScreen = parameters.displayScreen;
if(UtilValidate.isEmpty(displayScreen)){
	displayScreen = context.displayScreen;
}
if(UtilValidate.isNotEmpty(displayScreen) && (displayScreen=="RETURN_GRSWEIGHT") || (displayScreen=="RETURN_QC") || (displayScreen=="RETURN_UNLOAD") || (displayScreen=="RETURN_TARWEIGHT")){
	List conList = FastList.newInstance();
	if(displayScreen=="RETURN_GRSWEIGHT"){
		conList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"MR_RETURN_INITIATE"));
	}
	if(displayScreen=="RETURN_UNLOAD"){
		conList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"MR_RETURN_QC"));
	}
	if(displayScreen=="RETURN_QC"){
		conList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"MR_RETURN_GRWEIGHT"));
	}
	if(displayScreen=="RETURN_TARWEIGHT"){
		conList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"MR_RETURN_UNLOAD"));
	}
	
	conList.add(EntityCondition.makeCondition("estimatedEndDate",EntityOperator.EQUALS,null));
	EntityCondition ecl = EntityCondition.makeCondition(conList,EntityOperator.AND);
	List vehicleTripStatus = delegator.findList("VehicleTripStatus",ecl,UtilMisc.toSet("vehicleId","sequenceNum","statusId"),null,null,false);
	List vehicleList = FastList.newInstance();
	vehicleTripStatus.each{trip->
		tempMap=[:];
		vehicleInTime = delegator.findOne("VehicleTripStatus",[vehicleId:trip.vehicleId,sequenceNum:trip.sequenceNum,statusId:"MR_RETURN_INITIATE"],false);
		List milkTransferList = delegator.findList("MilkTransfer",EntityCondition.makeCondition([EntityCondition.makeCondition("containerId",EntityOperator.EQUALS,trip.vehicleId),
																							 EntityCondition.makeCondition("sequenceNum",EntityOperator.EQUALS,trip.sequenceNum)],EntityOperator.AND),UtilMisc.toSet("partyId"),null,null,false);
		String partyId="";
		String partyName="";
		if(milkTransferList){
			GenericValue milkTransfer = EntityUtil.getFirst(milkTransferList);
			partyId = milkTransfer.partyId;
			partyName = PartyHelper.getPartyName(delegator, partyId, false);
		}
		tempMap.partyId = partyName +"["+partyId+"]";
		tempMap.inTime = vehicleInTime.estimatedStartDate;
		tempMap.vehicleId = trip.vehicleId;
		vehicleList.add(tempMap);
	}
	context.vehicleList = vehicleList;
}