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

import java.io.ObjectOutputStream.DebugTraceInfoStack;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.Debug;

partyId = parameters.partyId ?parameters.partyId: parameters.party_id;
partyList=[];
Debug.log("partyId===="+partyId);
if (!partyId) {
    PartyRoleAndPartyDetail=delegator.findByAnd("PartyRoleDetailAndPartyDetail",[partyId:partyId,parentTypeId:"Vendor"]);
    if (PartyRoleAndPartyDetail) {
        partyId = PartyRoleAndPartyDetail.get(0).partyId;
        parameters.partyId = partyId;
		context.partyId = partyId;
		
    }
}else{
   PartyRoleAndPartyDetail=delegator.findByAnd("PartyRoleDetailAndPartyDetail",[parentTypeId:"Vendor"]);
   if(PartyRoleAndPartyDetail){
    context.partyId = PartyRoleAndPartyDetail.get(0).partyId;
	Debug.log("PartyRoleAndPartyDetail===="+PartyRoleAndPartyDetail.get(0).partyId);
    partyList.add(PartyRoleAndPartyDetail.get(0).partyId);
  }
}
context.party = delegator.findByPrimaryKey("Party", [partyId : partyId]);
context.partyList=partyList;
context.partyId = partyId;
context.showOld = "true".equals(parameters.SHOW_OLD);
Debug.log("====partyList===="+partyList);
