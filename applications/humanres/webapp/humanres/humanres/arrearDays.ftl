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
<#if EditArrearDays?has_content>
  <form name="arrearPayableDays" id="arrearPayableDays"  method="post" action="updateArrearDays">
    <table class="basic-table hover-bar" cellspacing="0">
      <thead>
        <tr class="header-row-2">
          <td>Employee</td>
          <td>Time Period</td>
          <td>Payable Days</td>
          <td>Arrear Days</td>
          <td>Loss of Pay Days</td>
          <td>Late Min</td>
          <td>Attended SS Days</td>
          <td>Attended Holidays</td>
          <td>Update</td>
        </tr>
      </thead>
      <tbody>
        <#assign alt_row = false>
        <#list EditArrearDays as eachArrearDay>
        	<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, eachArrearDay.partyId, false)>
        	<#assign customTimePeriod = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : eachArrearDay.customTimePeriodId}, true)>
            <#if customTimePeriod?has_content>
            	<#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMM")/>
				<#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMM,yyyy")/>
            </#if>
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
              <td><input type="display" readOnly size="25" id = "partyName" name="partyName" value="${eachArrearDay.partyId?if_exists}(${partyName?if_exists})"/></td>
              <td><input type="display" readOnly size="25" id = "ctpId" name="ctpId" value="${eachArrearDay.customTimePeriodId?if_exists}(${fromDate?if_exists}-${thruDate?if_exists})" /></td>
              <input type="hidden" size="20" id = "customTimePeriodId" name="customTimePeriodId" value="${eachArrearDay.customTimePeriodId?if_exists}" />
              <input type="hidden" size="20" id = "timePeriodId" name="timePeriodId" value="${eachArrearDay.timePeriodId?if_exists}" />
              <input type="hidden" size="20" id = "partyId" name="partyId" value="${eachArrearDay.partyId?if_exists}" />
              <#if security.hasPermission("EMP_PAYDAYS_EDIT", session)>
             	 <td><input type="text" size="10" id = "payableDays" name="payableDays" value="${eachArrearDay.noOfPayableDays?if_exists}" /></td>
              <#else>
              	 <td><input type="display" readOnly size="10" id = "payDays" name="payDays" value="${eachArrearDay.noOfPayableDays?if_exists}" /></td>
              </#if>
              	 <td><input type="text" size="10" id = "noOfArrearDays" name="noOfArrearDays" value="${eachArrearDay.noOfArrearDays?if_exists}" /></td>
              <#if security.hasPermission("EMP_LOP_EDIT_UPDATE", session)>
             	 <td><input type="text" size="10" id = "lossOfPayDays" name="lossOfPayDays" value="${eachArrearDay.lossOfPayDays?if_exists}" /></td>
              <#else>
              	 <td><input type="display" readOnly size="10" id = "lOPDays" name="lOPDays" value="${eachArrearDay.lossOfPayDays?if_exists}" /></td>
              </#if>
              <#if security.hasPermission("EMP_LATEMIN_EDIT_UPDATE", session)>
             	 <td><input type="text" size="10" id = "lateMin" name="lateMin" value="${eachArrearDay.lateMin?if_exists}" /></td>
              <#else>
              	 <td><input type="display" readOnly size="10" id = "latMin" name="latMin" value="${eachArrearDay.lateMin?if_exists}" /></td>
              </#if>
              <#if security.hasPermission("EMP_GHSS_EDIT_UPDATE", session)>
             	 <td><input type="text" size="10" id = "noOfAttendedSsDays" name="noOfAttendedSsDays" value="${eachArrearDay.noOfAttendedSsDays?if_exists}" /></td>
             	 <td><input type="text" size="10" id = "noOfAttendedHoliDays" name="noOfAttendedHoliDays" value="${eachArrearDay.noOfAttendedHoliDays?if_exists}" /></td>
              <#else>
              	 <td><input type="display" readOnly size="10" id = "ssDays" name="ssDays" value="${eachArrearDay.noOfAttendedSsDays?if_exists}" /></td>
              	 <td><input type="display" readOnly size="10" id = "holidays" name="holidays" value="${eachArrearDay.noOfAttendedHoliDays?if_exists}" /></td>
              </#if>
			  <td><input type="submit" name="submit" id="submit" value="Update" style="buttontext"/></td>
            </tr>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        </#list>
      </tbody>
    </table>
  </form>
<#else>
  <h3>No Days Found...</h3>
</#if>
        