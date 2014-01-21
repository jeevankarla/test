import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

       
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;

def populateBoothPoints(facility, points) {
	childFacilities = delegator.findByAnd("FacilityGroupAndMemberAndFacility", [ownerFacilityId : facility.facilityId]);
	childFacilities = delegator.findList("Facility", EntityCondition.makeCondition("facilityId" ,EntityOperator.IN , EntityUtil.getFieldListFromEntityList(childFacilities,"facilityId",true)), null , null, null, false);
	childFacilities.each { childFacility ->		
		if (childFacility.facilityTypeId == 'BOOTH') {
			pt = childFacility.getRelatedOne("GeoPoint");
			if (pt) {
				geoPoint = [:];    		
				geoPoint.lat = pt.latitude;
				geoPoint.lon = pt.longitude; 
				geoPoint.facilityId = childFacility.facilityId 	
				geoPoint.title = childFacility.facilityName;// + " [" + childFacility.sequenceNum + "]";
				geoPoint.shortName = "" + childFacility.facilityId;//childFacility.sequenceNum;
	
				points.add(geoPoint);
			}
		}
		else {
			populateBoothPoints(childFacility, points);
		}
	}
}


facilityId = parameters.facilityId;
facilityGroupId = parameters.facilityGroupId;

if (facilityId) {
    Map geoChart = UtilMisc.toMap("width", "800px", "height", "600px", "controlUI" , "small", "dataSourceId", "GEOPT_GOOGLE");
	geoPoints = [];
	geoLines = [];
	facility = delegator.findByPrimaryKey("Facility", [facilityId : facilityId]);
	if (facility.facilityTypeId == 'ROUTE') {
		points = [];
		populateBoothPoints(facility, points);
  		geoPoints = points;
  		geoLines.add(points);
	}
	else if (facility.facilityTypeId == 'ZONE') {
		points = [];
		lines = [];
		childFacilities = delegator.findByAnd("Facility", [parentFacilityId : facility.facilityId]);
		childFacilities.each { childFacility ->	
			routePoints = [];		
			populateBoothPoints(childFacility, routePoints);
			routePoints.each { point ->
				points.add(point);
			}
			lines.add(routePoints);
		}
  		geoPoints = points;
  		geoLines = lines;
	}	
	else if (facility.facilityTypeId == 'BOOTH') {
    	point = facility.getRelatedOne("GeoPoint");
    	if (point) {
			geoPoint = [:];    	
    		geoPoint.lat = point.latitude;
    		geoPoint.lon = point.longitude;    	
	    	geoPoint.title = facility.facilityName;// + " [" + facility.sequenceNum + "]";
			geoPoint.shortName = "" + facility.facilityId;//facility.sequenceNum; 
			geoPoint.facilityId = facility.facilityId; 
			geoPoints.add(geoPoint);
		}	
	}
	else {
		points = [];
		populateBoothPoints(facility, points);
		geoPoints = points;
	}
Debug.logInfo("geoPoints="+geoPoints,"");	
	geoChart.points = geoPoints;
	geoChart.lines = geoLines;
	context.geoChart = geoChart;
}
else if (facilityGroupId) {
	Map geoChart = UtilMisc.toMap("width", "800px", "height", "600px", "controlUI" , "small", "dataSourceId", "GEOPT_GOOGLE");
	geoPoints = [];
	geoLines = [];
	childFacilities = delegator.findByAnd("FacilityGroupMember", [facilityGroupId : facilityGroupId]);
	childFacilities.each { childFacility ->
		routePoints = [];
		populateBoothPoints(childFacility, routePoints);
		routePoints.each { point ->
			geoPoints.add(point);
		}
	}
Debug.logInfo("geoPoints="+geoPoints,"");
	geoChart.points = geoPoints;
	geoChart.lines = geoLines;
	context.geoChart = geoChart;
}
                