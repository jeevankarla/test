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

<script>
var currObj;
function holidayCheck(current){
	currObj = current;
	var saveRow = $(currObj).parent().parent();
	var test = $(saveRow).html();
	var checkObj = $(saveRow).find('#checkBox');
	if($(checkObj).is(':checked')){
	
		var formPartyId = $($(saveRow).find('#partyId')).val();
		var formDate = $($(saveRow).find('#date')).val();
		var formSeqId = $($(saveRow).find('#seqId')).val();
		var formTimePeriodId = $($(saveRow).find('#timePeriodId')).val();
		var formEncashmentStatus = $($(saveRow).find('#encashmentStatus')).val();
		var formCheckBox = $($(saveRow).find('#checkBox')).val();
		          
		var formId = $("#encashmentSubmitForm");
		
		var partyId = jQuery("<input>").attr("type", "hidden").attr("name", "partyId").val(formPartyId);
		jQuery(formId).append(jQuery(partyId));
		var date = jQuery("<input>").attr("type", "hidden").attr("name", "date").val(formDate);
		jQuery(formId).append(jQuery(date));
		var seqId = jQuery("<input>").attr("type", "hidden").attr("name", "seqId").val(formSeqId);
		jQuery(formId).append(jQuery(seqId));
		var timePeriodId = jQuery("<input>").attr("type", "hidden").attr("name", "timePeriodId").val(formTimePeriodId);
		jQuery(formId).append(jQuery(timePeriodId));
		var encashmentStatus = jQuery("<input>").attr("type", "hidden").attr("name", "encashmentStatus").val(formEncashmentStatus);
		jQuery(formId).append(jQuery(encashmentStatus));
		var checkBox = jQuery("<input>").attr("type", "hidden").attr("name", "checkBox").val(formCheckBox);
		jQuery(formId).append(jQuery(checkBox));
		jQuery(formId).submit();
	}
	
}
</script>

<form name="encashmentSubmitForm" id="encashmentSubmitForm" method="post" action="updatEncashmentStatus">
</form>
<#if holidaysList?has_content && (parameters.noConditionFind)?if_exists == 'Y'>
  
  <form name="holidaysList" id="holidaysList"  method="post" action="">
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
              <td><input type="display" readOnly size="20" id = "partyName" name="partyName" value="${holiday.partyId?if_exists}(${partyName?if_exists})"/></td>
              <td><input type="display" readOnly size="10" name="date" id = "date" value="${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(holiday.date?if_exists, "dd-MM-yyyy"))}" />
              <input type="hidden" size="20" id = "seqId" name="seqId" value="${holiday.seqId?if_exists}" />
              <input type="hidden" size="20" id = "timePeriodId" name="timePeriodId" value="${timePeriodId?if_exists}" /></td>
              <input type="hidden" size="20" id = "partyId" name="partyId" value="${holiday.partyId?if_exists}" /></td>
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
			<td><input type="checkbox" id="checkBox" name="checkBox" value="Y"<#if holiday.encashmentStatus?has_content && holiday.encashmentStatus == "CASH_ENCASHMENT">checked = "checked"</#if>/></td>
			<td><input type="button" name="update" onclick="javascript: holidayCheck(this);" style="buttontext" value="Update" /></td>
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
