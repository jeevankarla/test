<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>
<#if openingBalance?has_content>
  <form name="leaveBalanceDays" id="leaveBalanceDays"  method="post" action="updateEmployeeLeaveBalance">
    <table class="basic-table hover-bar" cellspacing="0">
      <thead>
        <tr class="header-row-2">
          <td>Employee</td>
          <td>Time Period</td>
          <td>Leave Type</td>
          <td>Balance</td>
          <td>Alloted Days</td>
          <td>Availed Days</td>
          <td>Adjusted Days</td>
          <td>Encashed Days</td>
          <td>Lapsed Days</td>
          <td>Update</td>
        </tr>
      </thead>
      <tbody>
        <#assign alt_row = false>
        	<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)>
        	<#assign customTimePeriod = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : customTimePeriodId}, true)>
            <#if customTimePeriod?has_content>
            	<#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMM")/>
				<#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMM,yyyy")/>
            </#if>
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
              <td><input type="display" readOnly size="25" id = "partyName" name="partyName" value="${partyId?if_exists}(${partyName?if_exists})"/></td>
              <td><input type="display" readOnly size="25" id = "ctpId" name="ctpId" value="${customTimePeriodId?if_exists}(${fromDate?if_exists}-${thruDate?if_exists})" /></td>
              <input type="hidden" size="20" id = "customTimePeriodId" name="customTimePeriodId" value="${customTimePeriodId?if_exists}" />
              <input type="hidden" size="20" id = "partyId" name="partyId" value="${partyId?if_exists}" />
              <td><input type="display" size="10" id = "leaveTypeId" name="leaveTypeId" value="${leaveTypeId?if_exists}" /></td>
              <td><input type="text" size="10" id = "openingBalance" name="openingBalance" value="${openingBalance?if_exists}" /></td>
              <td><input type="text" size="10" id = "allotedDays" name="allotedDays" value="${allotedDays?if_exists}" /></td>
              <td><input type="text" size="10" id = "availedDays" name="availedDays" value="${availedDays?if_exists}" /></td>
              <td><input type="text" size="10" id = "adjustedDays" name="adjustedDays" value="${adjustedDays?if_exists}" /></td>
              <td><input type="text" size="10" id = "encashedDays" name="encashedDays" value="${encashedDays?if_exists}" /></td>
              <td><input type="text" size="10" id = "lapsedDays" name="lapsedDays" value="${lapsedDays?if_exists}" /></td>
              <td><input type="submit" name="submit" id="submit" value="Update" style="buttontext"/></td>
              </tr>
            <#assign alt_row = !alt_row>
      </tbody>
    </table>
  </form>
<#else>
  <h3>No Attendance Days Found...</h3>
</#if>
        