/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
userLogin = session.getAttribute("userLogin");

if (context.facilityId == null && userLogin.partyId) {
	roleTypeAndParty = delegator.findByAnd("RoleTypeAndParty", ['partyId': userLogin.partyId, 'roleTypeId': 'ZONE_OWNER']);
	if (roleTypeAndParty) {
		// for now we assume a user can own only a single zone.
		// Should not be a big deal to handle the other scenario..
		owningFacility = delegator.findByAnd("Facility", [ownerPartyId : userLogin.partyId]);
		if (owningFacility)  {
			facilityId = owningFacility.get(0).facilityId;
			context.facilityId = facilityId;
			context.facility = owningFacility.get(0);
			//Debug.logInfo("context="+context,"");
		}
	}
}

def populateBooths(facility, booths) {
	childFacilities = delegator.findByAnd("Facility", [parentFacilityId : facility.facilityId],["sequenceNum","facilityName"]);
	childFacilities.each { childFacility ->
		populateBooths(childFacility, booths);
		if (childFacility.facilityTypeId == "BOOTH") {
			booths.add(childFacility.facilityId);
		}
	}
}
Debug.logInfo("context.facilityId="+context.facilityId,"");

booths =[];
if (context.facilityId != null) {
	facility = delegator.findOne("Facility",[facilityId : context.facilityId], false);
	populateBooths(facility, booths);
}
context.booths = booths; 

