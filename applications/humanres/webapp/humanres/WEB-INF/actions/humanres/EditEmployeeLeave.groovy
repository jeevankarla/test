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
 
 
 /*   ********* AjaxConditionLookUp.groovy **********
 * This Groovy add conditional Parameters to Ajax call
 * based on the screen which Ajax is call has been Made.
 */
 
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;

context.target= "createEmplLeaveExt";
context.editFlag= Boolean.FALSE;
if(leaveApp){
	context.editFlag= Boolean.TRUE;
	context.target= "updateEmplLeaveExt";
	context.leaveTypeId = leaveApp.leaveTypeId;
	context.emplLeaveReasonTypeId= leaveApp.emplLeaveReasonTypeId;
	context.dayFractionId= leaveApp.dayFractionId;
	context.comment= leaveApp.comment;
	context.approverPartyId = leaveApp.approverPartyId;
	context.fromDate = UtilDateTime.toDateString(leaveApp.fromDate, "dd-MM-yyyy");
	context.thruDate =  UtilDateTime.toDateString(leaveApp.thruDate, "dd-MM-yyyy");
}