
       
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.*;
import org.ofbiz.party.party.PartyHelper;
import java.text.SimpleDateFormat;
import java.text.ParseException;



def populatePartyPoints(partyId, locationDate, points) {
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	List exprList = [];
	if (partyId) {
		exprList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
	}
	if (locationDate) {
		dayBegin = UtilDateTime.getDayStart(locationDate);
		dayEnd = UtilDateTime.getDayEnd(locationDate);
		exprList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
		exprList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
	}
Debug.logInfo("exprList="+exprList,"");
	
	condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
	childLocations = delegator.findList("PartyLocation", condition, null, null, null, false);
	childLocations.each { childLocation ->		
		geoPoint = [:];    		
		geoPoint.lat = childLocation.latitude;
		geoPoint.lon = childLocation.longitude; 
		partyName = PartyHelper.getPartyName(delegator, childLocation.partyId, false);
		title = dateFormat.format(childLocation.fromDate);
		geoPoint.facilityId = childLocation.partyId;
		geoPoint.title = title;  
		geoPoint.shortName = partyName; 	
		points.add(geoPoint);
	}
}


partyId = null;
if (parameters.partyId) {
	partyId = parameters.partyId;
}
locationDate = null;
if (parameters.locationDate) {
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	try {
		locationDate = UtilDateTime.toTimestamp(dateFormat.parse(parameters.locationDate));
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + parameters.locationDate, "");
	}
}

    Map geoChart = UtilMisc.toMap("width", "800px", "height", "600px", "controlUI" , "small", "dataSourceId", "GEOPT_GOOGLE");
	geoPoints = [];
	geoLines = [];
	populatePartyPoints(partyId, locationDate, geoPoints);
	geoLines.add(geoPoints);
Debug.logInfo("geoPoints="+geoPoints,"");	
	geoChart.points = geoPoints;
	geoChart.lines = geoLines;
	context.geoChart = geoChart;

locationPointsTitle = "Location Points";
if (partyId != null) {
	locationPointsTitle = locationPointsTitle + " for " + PartyHelper.getPartyName(delegator, partyId, false);
} 
if (locationDate != null) {
	locationPointsTitle = locationPointsTitle + " on " + parameters.locationDate;
}          
context.locationPointsTitle = locationPointsTitle;    