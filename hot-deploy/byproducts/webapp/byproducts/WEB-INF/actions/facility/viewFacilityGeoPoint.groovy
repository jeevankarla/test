import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;


categoryTypeEnum = parameters.categoryTypeEnum;
//Debug.log("categoryTypeEnum="+categoryTypeEnum,"");

def populateBoothPoints(facility, points) {
	
	childFacilities = delegator.findByAnd("FacilityGroupAndMemberAndFacility", [ownerFacilityId : facility.facilityId]);
	childFacilities = delegator.findList("Facility", EntityCondition.makeCondition("facilityId" ,EntityOperator.IN , EntityUtil.getFieldListFromEntityList(childFacilities,"facilityId",true)), null , null, null, false);
	childFacilities.each { childFacility ->
//Debug.log("childFacility="+childFacility.facilityId,"");
		if (childFacility.facilityTypeId == 'BOOTH') {
			if (UtilValidate.isEmpty(categoryTypeEnum) || 
				( categoryTypeEnum && categoryTypeEnum == childFacility.categoryTypeEnum)) {
//Debug.log("add pt","");				
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
		}
		else {
			populateBoothPoints(childFacility, points);
		}
	}
}

Map geoChart = UtilMisc.toMap("width", "800px", "height", "600px", "controlUI" , "small", "dataSourceId", "GEOPT_GOOGLE");
geoPoints = [];
geoLines = []; // array of points array
geoRouteLabels = [];
facilityId = parameters.facilityId;
facilityGroupId = parameters.facilityGroupId;
routeId2 = parameters.routeId2;

if (facilityId) {
	facility = delegator.findByPrimaryKey("Facility", [facilityId : facilityId]);
	if (facility.facilityTypeId == 'ROUTE') {
		points = [];
		populateBoothPoints(facility, points);
		points.each { point ->
			geoPoints.add(point);
		}	
		geoLines.add(points);
		geoRouteLabels.add(facilityId);
Debug.log("geoLines="+geoLines,"");
		  
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
}
else if (facilityGroupId) {
	childFacilities = delegator.findByAnd("FacilityGroupMember", [facilityGroupId : facilityGroupId]);
	childFacilities.each { childFacility ->
		routePoints = [];
		populateBoothPoints(childFacility, routePoints);
		routePoints.each { point ->
			geoPoints.add(point);
		}
	}
}
else {
	childFacilities = delegator.findByAnd("Facility", [facilityTypeId:"ROUTE"]);
	childFacilities.each { childFacility ->
		childPoints = [];			
		populateBoothPoints(childFacility, childPoints);
		childPoints.each { point ->
			geoPoints.add(point);
		}
		if (childFacility.facilityTypeId == 'ROUTE') {
			//geoLines.add(childPoints);
			//geoRouteLabels.add(childFacility.facilityId);
		}
	}
}

if (routeId2) {
	facility = delegator.findByPrimaryKey("Facility", [facilityId : routeId2]);
	if (facility.facilityTypeId == 'ROUTE') {
		childPoints = [];
		populateBoothPoints(facility, childPoints);
		childPoints.each { point ->
			geoPoints.add(point);
		}			
		geoLines.add(childPoints);
		geoRouteLabels.add(routeId2);
	}
}

// dedup points
geoPoints.unique();

Debug.log("geoPoints.size="+geoPoints.size(),"");	
//Debug.log("geoLines.size="+geoLines.size(),"");
//Debug.log("geoRouteLabels="+geoRouteLabels,"");

geoChart.points = geoPoints;
geoChart.lines = geoLines;
geoChart.routeLabels = geoRouteLabels;
context.geoChart = geoChart;