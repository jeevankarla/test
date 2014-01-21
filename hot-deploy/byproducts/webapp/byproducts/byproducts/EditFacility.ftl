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
<script>
	
      function setFacilityToUpperCase(){
      	var facilityId = $('#facilityId').val();
      	$('#facilityId').val(facilityId.toUpperCase());      
      } 
       
</script>



<#if facilityId?has_content>
  <h2  style="color:#051798;">Edit Location [${facility.facilityName?if_exists}]</h2>
  <br>
  <#assign ownerPartyName = (delegator.findOne("PartyNameView", {"partyId" : facility.ownerPartyId}, true))?if_exists />
  <form action="<@ofbizUrl>UpdateByProductFacility</@ofbizUrl>" name="EditFacilityForm" method="post">
  	<input type="hidden" name="facilityId" value="${facilityId?if_exists}" />
  	<input type='hidden' name='contactMechId' value='${contactMechId?if_exists}' />
  
  	<table class="basic-table" cellspacing='0'>
 	 <tr>
    	<td class="label">${uiLabelMap.ProductFacilityId}</td>
    	<td>
     	  ${facilityId?if_exists} <span class="tooltip">${uiLabelMap.ProductNotModificationRecrationFacility}</span>
    	</td>
  	 </tr>
<#else>
	<h2 style="color:#051798;">Create New Facility</h2>
	<br>
  <form action="<@ofbizUrl>CreateByProductFacility</@ofbizUrl>" name="EditFacilityForm" method="post" style='margin: 0;'>
  <#if facilityId?exists>
    <h3>${uiLabelMap.ProductCouldNotFindFacilityWithId} "${facilityId?if_exists}".</h3>
  </#if>
  <table class="basic-table" cellspacing='0'>
  <tr>
    	<td class="label">${uiLabelMap.ProductFacilityId}</td>
    	<td>
      		<input type="text" name="facilityId" id="facilityId" value="${facility.facilityName?if_exists}" size="25" maxlength="60" />
      		<span class="tooltip">${uiLabelMap.CommonRequired}</span>
    	</td>
  	</tr>
</#if>
	
	<tr>
    	<td class="label">${uiLabelMap.ProductName}</td>
    	<td>
      		<input type="text" name="facilityName" id="facilityName" value="${facility.facilityName?if_exists}" size="25" maxlength="60" />
      		<#if !facilityId?exists><span class="tooltip">${uiLabelMap.CommonRequired}</span></#if>
    	</td>
  	</tr>
  <tr>
    <#-- <td class="label">${uiLabelMap.ProductFacilityTypeId}</td>
    <td> -->
     <#-- <select name="facilityTypeId">
        <option selected="selected" value='${facilityType.facilityTypeId?if_exists}'>${facilityType.get("description",locale)?if_exists}</option>
        <option value='${facilityType.facilityTypeId?if_exists}'>----</option>
        <#list facilityTypes as nextFacilityType>
          <option value='${nextFacilityType.facilityTypeId?if_exists}'>${nextFacilityType.get("description",locale)?if_exists}</option>
        </#list>
      </select> -->
      <input type="hidden" name="facilityTypeId" id="facilityTypeId" size="25" maxlength="25" value="BOOTH"/>
      <input type="hidden" name="subscriptionTypeId" id="subscriptionTypeId" size="25" maxlength="25" value="BYPRODUCTS"/>
    </td>
  </tr>
  	<#if !facilityId?exists>
       <tr>
          <td class="label">Facility Category Type</td>
          <td>
            <#assign enumerations = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "BYPROD_FA_CAT"))>
            <select name="categoryTypeEnum" >
                <#assign categoryTypeEnum = facility.categoryTypeEnum?if_exists>
        		<option selected="selected" value='${categoryTypeEnum?if_exists}'>${categoryTypeEnum?if_exists}</option>
        		<option value=""></option>                
                <#list enumerations as enumeration>
                  <option value="${enumeration.enumId}" <#if "${enumeration.enumId}" == categoryTypeEnum?if_exists>selected="selected"</#if>>${enumeration.enumCode}</option>
                </#list>
            </select>
            <#if !facilityId?exists><span class="tooltip">${uiLabelMap.CommonRequired}</span></#if>
          </td>
        </tr>
        </#if>
        
  <tr>
    <td class="label">Route</td>
    <td>
       <input type="text" name="byProdRouteId" id="byProdRouteId" size="25" maxlength="25" value="${facility.byProdRouteId?if_exists}"/>
       <#if !facilityId?exists><span class="tooltip">${uiLabelMap.CommonRequired}</span></#if>
    </td>
    <tr>
    <td class="label">Zone</td>
    <td>
    	<select name="zoneId" >
        <option selected="selected" value='${facility.zoneId?if_exists}'>${facility.zoneId?if_exists}</option>
        <option value=""></option>    
        <#list zoneList as eachZone>
          <option value='${eachZone.facilityId?if_exists}'>${eachZone.get("facilityName")?if_exists}</option>
        </#list>
      </select>
		<#if !facilityId?exists><span class="tooltip">${uiLabelMap.CommonRequired}</span></#if>
    </td>
  </tr>
  <tr>
    <td class="label">Opened Date</td>
    <td>
    	<@htmlTemplate.renderDateTimeField name="openedDate" event="" action="" value="${facility.openedDate?if_exists}" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="22" maxlength="25" id="openedDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
    </td>
  </tr>
  <tr>
    <td class="label">Closed Date</td>
    <td>
    	<@htmlTemplate.renderDateTimeField name="closedDate" event="" action="" value="${facility.closedDate?if_exists}" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="22" maxlength="25" id="closedDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
    </td>
  </tr>
  </tr>
  <#if !facilityId?exists>
  <tr>
    <td class="label"><#if facilityId?exists>Party Id<#else>Incharge Name</#if></td>
    <td>
    <#if facilityId?exists>
      	<@htmlTemplate.lookupField value="${facility.ownerPartyId?if_exists}" formName="EditFacilityForm" name="ownerPartyId" id="ownerPartyId" fieldFormName="LookupPartyName"/>
      	<span class="tooltip"><#if ownerPartyName?has_content>${ownerPartyName.groupName?if_exists}${ownerPartyName.firstName?if_exists} ${ownerPartyName.lastName?if_exists}</#if> </span>
     <#else>
     	<input type="text" id="inchargeName" name="inchargeName" value="${facility.ownerPartyId?if_exists}" size="25" maxlength="25" />
     </#if>
     <#if !facilityId?exists><span class="tooltip">${uiLabelMap.CommonRequired}</span></#if>
    </td>
  </tr>
  <#else>
        	 <input type="hidden" name="inchargeName" id="inchargeName" size="25" maxlength="25" value="${facility.ownerPartyId?if_exists}"/>
  </#if>
  <tr>
    <td class="label">Address1</td>
    <td><input type="text" id="address1" name="address1" value="${address1?if_exists}" size="25" maxlength="100" /></td>
  </tr>
  <tr>
    <td class="label">Address2</td>
    <td><input type="text" id="address2" name="address2" value="${address2?if_exists}" size="25" maxlength="100" /></td>
  </tr>
  <tr>
    <td class="label">Contact Number</td>
    <td><input type="text" id="contactNumber" name="contactNumber" value="${contactNumber?if_exists}" size="25" maxlength="15" /></td>
  </tr> 
  <tr>
    <td class="label">City</td>
    <td><input type="text" id="city" name="city" value="${city?if_exists}" size="25" maxlength="15" /></td>
  </tr>  
  <tr>
    <td class="label">Pin Code</td>
    <td><input type="text" id="pinCode" name="pinCode" value="${pinCode?if_exists}" size="25" maxlength="10" /></td>
  </tr>
  <tr>
    <td class="label">Security Deposit</td>
    <td ><input type="text" id="deposit" name="deposit" value="${deposit?if_exists}" size="25" maxlength="25" />
    <#if !facilityId?exists><span class="tooltip">${uiLabelMap.CommonRequired}</span></#if>
    </td>
  </tr>
  
  <tr>
    <td class="label">Bank Name</td>
    <td ><input type="text" id="bankName" name="bankName" value="${bankName?if_exists}" size="25" maxlength="25" />
    <#--<span class="tooltip">${uiLabelMap.CommonRequired}</span>-->
    </td>
  </tr>
  <tr>
    <td class="label">Bank Branch</td>
    <td ><input type="text" id="bankBranch" name="bankBranch" value="${bankBranch?if_exists}" size="25" maxlength="25" />
   	<#--<span class="tooltip">${uiLabelMap.CommonRequired}</span>-->
    </td>
  </tr>
    <td ><input type="hidden" id="finAccountId" name="finAccountId" value="${finAccountId?if_exists}"  />
    </td>
  <tr>
    <td>&nbsp;</td>
    <#if facilityId?has_content>
      <td><input type="submit" name="Update" value="${uiLabelMap.CommonUpdate}" /></td>
    <#else>
      <td><input type="submit" name="Update" value="${uiLabelMap.CommonSubmit}" onclick="javascript: setFacilityToUpperCase();"/></td>
    </#if>
  </tr>
</table>
</form>
