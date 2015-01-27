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
<#if holidaysList?has_content && (parameters.noConditionFind)?if_exists == 'Y'>
  <form name="holidaysList" id="holidaysList"  method="post" action="updatEncashmentStatus">
    <table class="basic-table hover-bar" cellspacing="0">
      <thead>
        <tr class="header-row-2">
          <td>Employee</td>
          <td>Date</td>
          <td>Encashment Cash</td>
          <td>Enabled</td>
          <td>Update</td>
        </tr>
      </thead>
      <tbody>
        <#assign alt_row = false>
        <#list holidaysList as holiday>
        	<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, holiday.partyId, false)>
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
              <td><input type="display" readOnly size="20" name="partyId" value="${holiday.partyId?if_exists}(${partyName?if_exists})"/></td>
              <td><input type="display" readOnly size="10" name="date" id = "date" value="${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(holiday.date?if_exists, "dd-MM-yyyy"))}" />
              <input type="hidden" size="20" name="seqId" value="${holiday.seqId?if_exists}" />
              <input type="hidden" size="20" name="timePeriodId" value="${timePeriodId?if_exists}" /></td>
              <td><select name="encashmentStatus"  id="encashmentStatus">
      						<#if holiday.encashmentStatus?has_content && holiday.encashmentStatus == "CASH_ENCASHMENT">
      						<option value='${holiday.encashmentStatus}'>Cash Encash</option>
      						<option value=''></option>
      						<#else>
      						<#list encashmentList as encashment>
	      						<option value=''></option>
	                			<option value='${encashment.enumId}'>${encashment.description?if_exists}</option>
                			</#list> 
                			</#if>
					</select>
          		</td>
			<td><input type="checkbox" id="checkBox" name="checkBox" value ="Y"<#if holiday.encashmentStatus?has_content && holiday.encashmentStatus == "CASH_ENCASHMENT">checked = "checked"</#if>/></td>
			<td><input type="submit" value='${uiLabelMap.CommonUpdate}'/></td>
            </tr>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        </#list>
      </tbody>
    </table>
  </form>
<#else>
  <h3>No Holidays Found...</h3>
</#if>
