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

import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;

import java.text.SimpleDateFormat;
import java.math.BigDecimal;

partyId = parameters.partyId;
if(UtilValidate.isEmpty(partyId)){
	facilityId = parameters.facilityId;
	facility = delegator.findOne("Facility", [facilityId : facilityId], false);
	if(UtilValidate.isNotEmpty(facility)){
		partyId = facility.ownerPartyId;
	}
}

showOld = "true".equals(parameters.SHOW_OLD);
context.contactMeches = ContactMechWorker.getPartyContactMechValueMaps(delegator, partyId, showOld);






