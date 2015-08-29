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
if(UtilValidate.isNotEmpty(displayScreen) && (displayScreen=="ISSUE_TARWEIGHT") || (displayScreen=="ISSUE_CIP") || (displayScreen=="ISSUE_QC") || (displayScreen=="ISSUE_GRSWEIGHT") || (displayScreen=="ISSUE_OUT") ||(displayScreen=="ISSUE_LOAD")){
	List conList = FastList.newInstance();
	if(displayScreen=="ISSUE_TARWEIGHT"){
		conList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"MR_ISSUE_INIT"));
	}
	if(displayScreen=="ISSUE_CIP"){
		conList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"MR_ISSUE_TARWEIGHT"));
	}
	if(displayScreen=="ISSUE_LOAD"){
		conList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"MR_ISSUE_CIP"));
	}
	if(displayScreen=="ISSUE_QC"){
		conList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"MR_ISSUE_LOAD"));
	}
	if(displayScreen=="ISSUE_GRSWEIGHT"){
		conList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"MR_ISSUE_QC"));
	}
	if(displayScreen=="ISSUE_OUT"){
		conList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"MR_ISSUE_GRWEIGHT"));
	}
	
	conList.add(EntityCondition.makeCondition("estimatedEndDate",EntityOperator.EQUALS,null));
	EntityCondition ecl = EntityCondition.makeCondition(conList,EntityOperator.AND);
	List vehicleTripStatus = delegator.findList("VehicleTripStatus",ecl,UtilMisc.toSet("vehicleId","sequenceNum","statusId"),null,null,false);
	List vehicleList = FastList.newInstance();
	vehicleTripStatus.each{trip->
		tempMap=[:];
		vehicleInTime = delegator.findOne("VehicleTripStatus",[vehicleId:trip.vehicleId,sequenceNum:trip.sequenceNum,statusId:"MR_ISSUE_INIT"],false);
		List milkTransferList = delegator.findList("MilkTransfer",EntityCondition.makeCondition([EntityCondition.makeCondition("containerId",EntityOperator.EQUALS,trip.vehicleId),
																							 EntityCondition.makeCondition("sequenceNum",EntityOperator.EQUALS,trip.sequenceNum)],EntityOperator.AND),UtilMisc.toSet("partyIdTo"),null,null,false);
		String partyId="";
		String partyName="";
		if(milkTransferList){
			GenericValue milkTransfer = EntityUtil.getFirst(milkTransferList);
			partyId = milkTransfer.partyIdTo;
			partyName = PartyHelper.getPartyName(delegator, partyId, false);
		}
		tempMap.partyId = partyName +"["+partyId+"]";
		tempMap.inTime = vehicleInTime.estimatedStartDate;
		tempMap.vehicleId = trip.vehicleId;
		vehicleList.add(tempMap);
	}
	context.vehicleList = vehicleList;
}