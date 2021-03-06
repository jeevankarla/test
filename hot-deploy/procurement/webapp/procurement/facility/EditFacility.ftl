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
var amRouteList = ${StringUtil.wrapString(amRouteList)}
var pmRouteList = ${StringUtil.wrapString(pmRouteList)}
      function setClosedDateNull(){      
      	$('#closedDate').val(''); 
      	$('#reopen').val('Y');     
      } 
      function hideorshow(){
	    var categoryTypeEnum =$( "#categoryTypeEnum option:selected" ).val();
		if(categoryTypeEnum == "CR_INST"){
		    jQuery("#marginOnMilk").parent().parent().show();
			jQuery("#marginOnProduct").parent().parent().show();
		}else{
			jQuery("#marginOnMilk").parent().parent().hide();
			jQuery("#marginOnProduct").parent().parent().hide();
		}
		if(categoryTypeEnum == "SHP_RTLR"){
		    jQuery("#rateAmount").parent().parent().show();
		}else{
			jQuery("#rateAmount").parent().parent().hide();
		}
	}
	  $(document).ready(function(){
	   hideorshow();
	}); 
</script>

<#if facility?exists && facilityId?has_content>
  <#assign ownerPartyName = (delegator.findOne("PartyNameView", {"partyId" : facility.ownerPartyId}, true))?if_exists />

  <form action="<@ofbizUrl>UpdateBooth</@ofbizUrl>" name="EditFacilityForm" method="post">
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
  <form action="<@ofbizUrl>CreateFacility</@ofbizUrl>" name="EditFacilityForm" method="post" style='margin: 0;'>
  <#if facilityId?exists>
    <h3>${uiLabelMap.ProductCouldNotFindFacilityWithId} "${facilityId?if_exists}".</h3>
  </#if>
  <table class="basic-table" cellspacing='0'>
</#if>
  <tr>
    <td class="label">${uiLabelMap.ProductFacilityTypeId}</td>
    <td>
      <select name="facilityTypeId">
        <option selected="selected" value='${facilityType.facilityTypeId?if_exists}'>${facilityType.get("description",locale)?if_exists}</option>
        <option value='${facilityType.facilityTypeId?if_exists}'>----</option>
        <#list facilityTypes as nextFacilityType>
          <option value='${nextFacilityType.facilityTypeId?if_exists}'>${nextFacilityType.get("description",locale)?if_exists}</option>
        </#list>
      </select>
    </td>
  </tr>
  <#if facility.facilityTypeId?exists && facility.facilityTypeId == 'BOOTH'>  
        <tr>
          <td class="label">${uiLabelMap.ProductFacilityCategoryType}</td>
          <td>
            <#assign enumerations = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "BOOTH_CAT_TYPE"))>
            <select name="categoryTypeEnum" id="categoryTypeEnum" onchange="javascript:hideorshow();">
                <#assign categoryTypeEnum = facility.categoryTypeEnum?if_exists>
        		<option selected="selected" value='${categoryTypeEnum?if_exists}'>${categoryTypeEnum?if_exists}</option>
        		<option value=""></option>                
                <#list enumerations as enumeration>
                  <option value="${enumeration.enumId}" <#if "${enumeration.enumId}" == categoryTypeEnum?if_exists>selected="selected"</#if>>${enumeration.description}</option>
                </#list>
            </select>
          </td>
        </tr>  
  </#if>
      <tr>
		    <td class="label"><b>AM Route*</b></td>
		    <td>
		     <select name="amRoute" id="amRoute">
		     <option selected="selected" value="${amRoute?if_exists}">${amRoute?if_exists}</option>
		     <option value=""></option>  
		     <#list amRouteList as eachRoute>
		     <option value='${eachRoute.facilityId?if_exists}' >${eachRoute.facilityId?if_exists}</option>
		     </#list>
		     </select>
			 </td>
	</tr>
	<#--<tr>
		    <td class="label"><b>PM Route*</b></td>
		    <td>
		     <select name="pmRoute" id="pmRoute">
		     <option selected="selected" value='${pmRoute?if_exists}'>${pmRoute?if_exists}</option>
		     <option value=""></option>  
		     <#list pmRouteList as eachRoute>
		     <option value='${eachRoute.facilityId?if_exists}' <#if "${eachRoute.facilityId}" == pmRoute?if_exists>selected="selected"</#if>>${eachRoute.facilityId?if_exists}</option>
		     </#list>
		     </select>
			 </td>
	  </tr> -->
  <tr>
    <td class="label">${uiLabelMap.ProductFacilityOwner}</td>
    <td>
      <@htmlTemplate.lookupField value="${facility.ownerPartyId?if_exists}" formName="EditFacilityForm" name="ownerPartyId" id="ownerPartyId" fieldFormName="LookupPartyName"/>
      <span class="tooltip"><#if ownerPartyName?has_content>${ownerPartyName.groupName?if_exists}${ownerPartyName.firstName?if_exists} ${ownerPartyName.lastName?if_exists}</#if> [${uiLabelMap.CommonRequired}]</span>
    </td>
  </tr>
  <#--<tr>
    <td class="label">Party Group Name</td>
    <td><input type="text" name="lastName" value="${ownerPartyName.groupName?if_exists}" maxlength="50" /></td>
  </tr>
  <tr>
    <td class="label">Address1</td>
    <td><input type="text" name="address1" value="${partyPostalAddress.address1?if_exists}" maxlength="50" /></td>
  </tr>  
   <tr>
    <td class="label">Address2</td>
    <td><input type="text" name="address2" value="${partyPostalAddress.address2?if_exists}"  maxlength="50" /></td>
  </tr>  
   <tr>
    <td class="label">City</td>
    <td><input type="text" name="city" value="${partyPostalAddress.city?if_exists}"  maxlength="20" /></td>
  </tr> 
   <tr>
    <td class="label">PostalCode</td>
    <td><input type="text" name="postalCode" value="${partyPostalAddress.postalCode?if_exists}" maxlength="10" /></td>
  </tr>  
  <tr>
    <td class="label">Phone Number</td>
    <td><input type="text" name="contactNumber" value="${contactNumber?if_exists}"  maxlength="10" /></td>
  </tr> 
  <tr>
	<td class="label"><b>Country Code</b></td>
	  <td><input type="text" name="countryCode" id="countryCode" value="${countryCode?if_exists}" size="5" maxlength="60" autocomplete="off" /></td>
  </tr>
  <tr>
    <td class="label">Mobile Number</td>
    <td><input type="text" name="mobileNumber" value="${mobileNumber?if_exists}"  maxlength="10" /></td>
  </tr>  
   <tr>
    <td class="label">Email</td>
    <td><input type="text" name="emailAddress" value="${emailAddress?if_exists}" maxlength="30" /></td>
  </tr>
  <tr>
    <td class="label">Pan Number</td>
    <td><input type="text" name="panNumber" value="${panNumber?if_exists}" maxlength="30" /></td>
 </tr>
 <tr>
    <td class="label">Bank Account Number</td>
    <td><input type="text" name="finAccountCode" value="${finAccountCode?if_exists}" maxlength="30" /></td>
 </tr>--> 
<#--<#if (enableInventory?if_exists && enableInventory)>    
  <tr>
    <td class="label">${uiLabelMap.ProductFacilityDefaultWeightUnit}</td>
    <td>
      <select name="defaultWeightUomId">
          <option value=''>${uiLabelMap.CommonNone}</option>
          <#list weightUomList as uom>
            <option value='${uom.uomId}'
               <#if (facility.defaultWeightUomId?has_content) && (uom.uomId == facility.defaultWeightUomId)>
               selected="selected"
               </#if>
             >${uom.get("description",locale)?default(uom.uomId)}</option>
          </#list>
      </select>
    </td>
  </tr>
  <tr>
    <td class="label">${uiLabelMap.ProductFacilityDefaultInventoryItemType}</td>
    <td>
      <select name="defaultInventoryItemTypeId">
          <#list inventoryItemTypes as nextInventoryItemType>
            <option value='${nextInventoryItemType.inventoryItemTypeId}'
               <#if (facility.defaultInventoryItemTypeId?has_content) && (nextInventoryItemType.inventoryItemTypeId == facility.defaultInventoryItemTypeId)>
               selected="selected"
               </#if>
             >${nextInventoryItemType.get("description",locale)?default(nextInventoryItemType.inventoryItemTypeId)}</option>
          </#list>
      </select>
    </td>
  </tr>
</#if>-->
  <tr>
    <td class="label">${uiLabelMap.ProductName}</td>
    <td>
      <input type="text" name="facilityName" value="${facility.facilityName?if_exists}" size="30" maxlength="60" />
      <span class="tooltip">${uiLabelMap.CommonRequired}</span>
    </td>
  </tr>
 
  <#--<tr>
    <td class="label">${uiLabelMap.ProductFacilitySize}</td>
    <td><input type="text" name="facilitySize" value="${facility.facilitySize?if_exists}" size="10" maxlength="20" /></td>
  </tr>
  <tr>
   <td class="label">${uiLabelMap.ProductFacilityDefaultAreaUnit}</td>
    <td>
      <select name="facilitySizeUomId">
          <option value=''>${uiLabelMap.CommonNone}</option>
          <#list areaUomList as uom>
            <option value='${uom.uomId}'
               <#if (facility.facilitySizeUomId?has_content) && (uom.uomId == facility.facilitySizeUomId)>
               selected="selected"
               </#if>
             >${uom.get("description",locale)?default(uom.uomId)}</option>
          </#list>
      </select>
    </td>
  </tr>  -->

          <field name="sequenceNum" title="${uiLabelMap.CommonSequence}"><text maxlength="5" size="5"/></field>
  <tr>
    <td class="label">${uiLabelMap.CommonSequence}</td>
    <td ><input type="text" name="sequenceNum" value="${facility.sequenceNum?if_exists}" size="10" maxlength="10" /></td>
  </tr>  
  <tr>
    <td class="label">${uiLabelMap.Description}</td>
    <td ><input type="text" name="description" value="${facility.description?if_exists}" size="30" maxlength="250" /></td>
  </tr>
  <tr>
    <td class="label">Security Deposit</td>
    <td ><input type="text" name="securityDeposit" value="${facility.securityDeposit?if_exists}" size="10" maxlength="250" /></td>
  </tr>
  <tr>
    <td class="label">Margin Allowed On Milk</td>
    <td > 
          <select name="marginOnMilk" id="marginOnMilk" >
          <#if rateAmountTypes?has_content && rateAmountTypes != null>
              <option value="${rateAmountTypes.lmsProductPriceTypeId?if_exists}" selected>${rateAmountTypes.lmsProductPriceTypeId?if_exists}</option>
            </#if> 
        	<option value="DEFAULT_PRICE">DEFAULT_PRICE</option> 
        	<option value="MRP_PRICE">MRP_PRICE</option>            
          </select>   
  </td>
  </tr>
  <tr>
    <td class="label">Margin Allowed On Product</td>
    <td > 
          <select name="marginOnProduct" id="marginOnProduct" >
            <#if   rateAmountTypes?has_content && rateAmountTypes != null>
            <option value="${rateAmountTypes.byprodProductPriceTypeId?if_exists}" selected>${rateAmountTypes.byprodProductPriceTypeId?if_exists}</option>
            </#if>
        	<option value="DEFAULT_PRICE">DEFAULT_PRICE</option> 
        	<option value="MRP_PRICE" >MRP_PRICE</option>            
          </select>   
  </td>
  </tr>
  <tr>
    <td class="label">Rate Amount</td>
    <td>
      <input type="text" name="rateAmount" id="rateAmount" value="${rateAmount?if_exists}" size="10" maxlength="60" />
    </td>
  </tr>
  
   <tr>
       <td class="label">Date of Commissioning</td>
       <td>	 
         <@htmlTemplate.renderDateTimeField name="openedDate" event="" action="" value="${facility.openedDate?if_exists}" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="20" maxlength="20" id="openedDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
       </td>      
  </tr>
    <td class="label">Closed Date</td>
       <td>	
         <@htmlTemplate.renderDateTimeField name="closedDate" event="" action="" value="${facility.closedDate?if_exists}" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="20" maxlength="20" id="closedDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
       	<#if (facility.closedDate?exists)>  &nbsp; &nbsp;&nbsp;&nbsp;<B>Re Open: </B>   
         	<input type="checkbox" id="reopen" name="reopen"  onclick="javascript:setClosedDateNull();"/>       
       </#if>
       </td>      
  </tr>
  <tr>
    <td class="label">Use ECS</td>
       <td>
          <select name="useEcs" >
          	<option value="" selected>${facility.useEcs?if_exists}</option>
        	<option value="Y">Y</option>  
        	<option value="N">N</option>              
          </select>
       </td>
  </tr>
   <tr>
    <td class="label">Closed Reason </td>
       <td>
          <select name="closedReason" >  
          	<option value="">Select Reason</option>
          <#-- 
          	<option value="${facility.closedReason?if_exists}" selected>${facility.closedReason?if_exists}</option> -->
          	<#list facCloseReasonList as reasonObj>
        			<option value='${reasonObj.enumId}' <#if (facility.closedReason?has_content)&& (reasonObj.enumId==facility.closedReason)> selected="selected"  </#if> > ${reasonObj.get("description",locale)?default(reasonObj.enumId)}</option>  
        	</#list>
          </select>
       </td>
  </tr>
  <tr>
  <td></td></tr>
<#if (enableShipping?if_exists && enableShipping)>  
  <tr>
    <td class="label">${uiLabelMap.ProductDefaultDaysToShip}</td>
    <td><input type="text" name="defaultDaysToShip" value="${facility.defaultDaysToShip?if_exists}" size="10" maxlength="20" /></td>
  </tr>
</#if>
  <tr>
    <td>&nbsp;</td>
    <#if facilityId?has_content>
		
      <td><#if security.hasEntityPermission("","FACILITY_LOC_UPDATE", session)><input type="submit" name="Update" value="${uiLabelMap.CommonUpdate}" /></#if></td>
		
    <#else>
      <td><input type="submit" name="Update" value="${uiLabelMap.CommonSave}" /></td>
    </#if>
  </tr>
</table>
</form>
