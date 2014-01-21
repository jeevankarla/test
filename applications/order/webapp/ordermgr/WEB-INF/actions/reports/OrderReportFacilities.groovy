


import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;


def populateChildren(facility, facilityIds) {	
	childFacilities = delegator.findByAnd("Facility", [parentFacilityId : facility.facilityId],["sequenceNum","facilityName"]);
	childFacilities.each { childFacility ->
		populateChildren(childFacility, facilityIds);
		facilityIds.add(childFacility.facilityId);
	}	
}


facilityIds = [];
String facilityId = request.getParameter("originFacilityId");
if (facilityId) {
	facilityIds.add(facilityId);
	facility = delegator.findByPrimaryKey("Facility", [facilityId : facilityId]);
	populateChildren(facility, facilityIds);
}

context.put("facilityIds", facilityIds);
