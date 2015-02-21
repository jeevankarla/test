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

<#assign extInfo = parameters.extInfo?default("N")>
<#-- Only allow the search fields to be hidden when we have some results -->
<#if partyList?has_content>
  <#assign hideFields = parameters.hideFields?default("N")>
<#else>
  <#assign hideFields = "N">
</#if>
<h2>${uiLabelMap.PageTitleFindVendor}</h2>
<#if (parameters.groupName?has_content)>
  <#assign createUrl = "editpartygroup?create_new=Y&amp;groupName=${parameters.groupName?if_exists}"/>
<#else>
  <#assign createUrl = "createnew"/>
</#if>
<div class="screenlet">
  <div class="screenlet-title-bar">
<#if partyList?has_content>
    <ul>
  <#if hideFields == "Y">
      <li class="collapsed"><a href="<@ofbizUrl>findVendor?hideFields=N${paramList}</@ofbizUrl>" title="${uiLabelMap.CommonShowLookupFields}">&nbsp;</a></li>
  <#else>
      <li class="expanded"><a href="<@ofbizUrl>findVendor?hideFields=Y${paramList}</@ofbizUrl>" title="${uiLabelMap.CommonHideFields}">&nbsp;</a></li>
  </#if>
  <#if (partyListSize > 0)>
    <#if (partyListSize > highIndex)>
      <li><a class="nav-next" href="<@ofbizUrl>findVendor?VIEW_SIZE=${viewSize}&amp;VIEW_INDEX=${viewIndex+1}&amp;hideFields=${hideFields}${paramList}</@ofbizUrl>">${uiLabelMap.CommonNext}</a></li>
    <#else>
      <li class="disabled">${uiLabelMap.CommonNext}</li>
    </#if>
      <li>${lowIndex} - ${highIndex} ${uiLabelMap.CommonOf} ${partyListSize}</li>
    <#if (viewIndex > 0)>
      <li><a class="nav-previous" href="<@ofbizUrl>findVendor?VIEW_SIZE=${viewSize}&amp;VIEW_INDEX=${viewIndex-1}&amp;hideFields=${hideFields}${paramList}</@ofbizUrl>">${uiLabelMap.CommonPrevious}</a></li>
    <#else>
      <li class="disabled">${uiLabelMap.CommonPrevious}</li>
    </#if>
  </#if>
    </ul>
    <br class="clear"/>
</#if>
  </div>
  <div class="screenlet-body">
    <div id="findPartyParameters" <#if hideFields != "N"> style="display:none" </#if> >
      <h2>${uiLabelMap.CommonSearchOptions}</h2>
      <#-- NOTE: this form is setup to allow a search by partial partyId or userLoginId; to change it to go directly to
          the viewprofile page when these are entered add the follow attribute to the form element:

           onsubmit="javascript:lookupParty('<@ofbizUrl>viewprofile</@ofbizUrl>');"
       -->
      <form method="post" name="lookupparty" action="<@ofbizUrl>findVendor</@ofbizUrl>" class="basic-form">
        <input type="hidden" name="lookupFlag" value="Y"/>
        <input type="hidden" name="hideFields" value="Y"/>
        <table class="basic-table" cellspacing="0">
          <tr>
            <td class="label">${uiLabelMap.ContactInformation}</td>
            <td>
              <input type="radio" name="extInfo" value="N" onclick="javascript:refreshInfo();" <#if extInfo == "N">checked="checked"</#if>/>${uiLabelMap.CommonNone}&nbsp;
              <input type="radio" name="extInfo" value="P" onclick="javascript:refreshInfo();" <#if extInfo == "P">checked="checked"</#if>/>${uiLabelMap.Postal}&nbsp;
              <input type="radio" name="extInfo" value="T" onclick="javascript:refreshInfo();" <#if extInfo == "T">checked="checked"</#if>/>${uiLabelMap.Telecom}&nbsp;
              <input type="radio" name="extInfo" value="O" onclick="javascript:refreshInfo();" <#if extInfo == "O">checked="checked"</#if>/>${uiLabelMap.CommonOther}&nbsp;
            </td>
          </tr>
          <tr>
            <td class="label">${uiLabelMap.VendorId}</td>
            <td><input type="text" name="partyId" value="${parameters.partyId?if_exists}"/></td>
          </tr>
          <tr>
            <td class="label">${uiLabelMap.GroupName}</td>
            <td><input type="text" name="groupName" value="${parameters.groupName?if_exists}"/></td>
          </tr>
           <tr>
               <td class="label">${uiLabelMap.RoleType}</td>
	            <td>
	             <#assign roleType = delegator.findByAnd("RoleType", {"parentTypeId" : "Vendor"})/>
	              <select name="roleTypeId" id="roleTypeId">
	               <#if currentRole?has_content>
                     <option value="${currentRole.roleTypeId}">${currentRole.get("description",locale)}</option>
                     <option value="${currentRole.roleTypeId}">---</option>
					</#if>
					<option value="Supplier" selected="selected">${uiLabelMap.CommonAnyRoleType}</option>
	                <#if roleType?has_content>
		                <#list roleType as type>
		                	<option  value="${type.roleTypeId}">${type.description}</option>
		                </#list>
	                </#if>
	             </select>
	           </td>
          </tr>
          <input type="hidden" name="partyTypeId" value="PARTY_GROUP"/>
<#if extInfo == "P">
          <tr><td colspan="3"><hr /></td></tr>
          <tr>
            <td class="label">${uiLabelMap.CommonAddress1}</td>
            <td><input type="text" name="address1" value="${parameters.address1?if_exists}"/></td>
          </tr>
          <tr>
            <td class="label">${uiLabelMap.CommonAddress2}</td>
            <td><input type="text" name="address2" value="${parameters.address2?if_exists}"/></td>
          </tr>
          <tr>
            <td class="label">${uiLabelMap.CommonCity}</td>
            <td><input type="text" name="city" value="${parameters.city?if_exists}"/></td>
          </tr>
          <tr>
            <td class="label">${uiLabelMap.CommonStateProvince}</td>
            <td>
              <select name="stateProvinceGeoId">
  <#if currentStateGeo?has_content>
                <option value="${currentStateGeo.geoId}">${currentStateGeo.geoName?default(currentStateGeo.geoId)}</option>
                <option value="${currentStateGeo.geoId}">---</option>
  </#if>
                <option value="ANY">${uiLabelMap.CommonAnyStateProvince}</option>
                ${screens.render("component://common/widget/CommonScreens.xml#states")}
              </select>
            </td>
          </tr>
          <tr>
            <td class="label">${uiLabelMap.PostalCode}</td>
            <td><input type="text" name="postalCode" value="${parameters.postalCode?if_exists}"/></td>
          </tr>
</#if>
<#if extInfo == "T">
          <tr><td colspan="3"><hr /></td></tr>
          <tr>
            <td class="label">${uiLabelMap.CountryCode}</td>
            <td><input type="text" name="countryCode" value="${parameters.countryCode?if_exists}"/></td>
          </tr>
          <tr>
            <td class="label">${uiLabelMap.AreaCode}</td>
            <td><input type="text" name="areaCode" value="${parameters.areaCode?if_exists}"/></td>
          </tr>
          <tr>
            <td class="label">${uiLabelMap.ContactNumber}</td>
            <td><input type="text" name="contactNumber" value="${parameters.contactNumber?if_exists}"/></td>
          </tr>
</#if>
<#if extInfo == "O">
          <tr><td colspan="3"><hr /></td></tr>
          <tr>
            <td class="label">${uiLabelMap.ContactInformation}</td>
            <td><input type="text" name="infoString" value="${parameters.infoString?if_exists}"/></td>
          </tr>
</#if>
          <tr>
            <td>&nbsp;</td>
            <td>
              <input type="submit" value="${uiLabelMap.CommonFind}" onclick="javascript:document.lookupparty.submit();"/>
            </td>
          </tr>
        </table>
      </form>
    </div>
    <script language="JavaScript" type="text/javascript">
      document.lookupparty.partyId.focus();
    </script>

<#if partyList?exists>
  <#if hideFields != "Y">
    <hr />
  </#if>
    <div id="findPartyResults">
      <h2>${uiLabelMap.CommonSearchResults}</h2>
    </div>
  <#if partyList?has_content>
    <table class="basic-table hover-bar" cellspacing="0">
      <tr class="header-row-2">
        <td>${uiLabelMap.VendorId}</td>
        <td>${uiLabelMap.GroupName}</td>
    <#if extInfo?default("") == "P" >
        <td>${uiLabelMap.City}</td>
    </#if>
    <#if extInfo?default("") == "P">
        <td>${uiLabelMap.PostalCode}</td>
    </#if>
    <#if extInfo?default("") == "T">
        <td>${uiLabelMap.AreaCode}</td>
    </#if>
        <td>${uiLabelMap.VendorType}</td>
        <td>${uiLabelMap.MainRole}</td>
        <td>&nbsp;</td>
      </tr>
    <#assign alt_row = false>
    <#assign rowCount = 0>
    <#list partyList as partyRow>
      <#assign partyType = partyRow.getRelatedOne("PartyType")?if_exists>
      <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
        <td><a href="<@ofbizUrl>viewprofile?partyId=${partyRow.partyId}</@ofbizUrl>">${partyRow.partyId}</a></td>
        <td>
      <#if partyRow.getModelEntity().isField("groupName") && partyRow.groupName?has_content>
          ${partyRow.groupName}
      <#else>
        <#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(partyRow, false)>
        <#if partyName?has_content>
          ${partyName}
        <#else>
          (${uiLabelMap.PartyNoNameFound})
        </#if>
      </#if>
        </td>
      <#if extInfo?default("") == "T">
        <td>${partyRow.areaCode?if_exists}</td>
      </#if>
      <#if extInfo?default("") == "P" >
        <td>${partyRow.city?if_exists}, ${partyRow.stateProvinceGeoId?if_exists}</td>
      </#if>
      <#if extInfo?default("") == "P">
        <td>${partyRow.postalCode?if_exists}</td>
      </#if>
      <#--<#if inventoryItemId?default("") != "">
        <td>${partyRow.inventoryItemId?if_exists}</td>
      </#if>
      <#if serialNumber?default("") != "">
        <td>${partyRow.serialNumber?if_exists}</td>
      </#if>
      <#if softIdentifier?default("") != "">
        <td>${partyRow.softIdentifier?if_exists}</td>
      </#if>-->
      <#if partyType?exists>
        <td><#if partyType.description?exists>${partyType.get("description", locale)}<#else>???</#if></td>
      <#else>
        <td></td><td></td>
      </#if>
        <td>
      <#assign mainRole = dispatcher.runSync("getPartyMainRole", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", partyRow.partyId, "userLogin", userLogin))/>
              ${mainRole.description?if_exists}
            </td>
            <td class="button-col align-float">
              <a href="<@ofbizUrl>viewprofile?partyId=${partyRow.partyId}</@ofbizUrl>">${uiLabelMap.CommonDetails}</a>
              <#if security.hasRolePermission("ORDERMGR", "_VIEW", "", "", session)>
                  <form name= "searchorders_o_${rowCount}" method= "post" action= "/ordermgr/control/searchorders">
                    <input type= "hidden" name= "lookupFlag" value= "Y" />
                    <input type= "hidden" name= "hideFields" value= "Y" />
                    <input type= "hidden" name= "partyId" value= "${partyRow.partyId}" />
                    <input type= "hidden" name= "viewIndex" value= "1" />
                    <input type= "hidden" name= "viewSize" value= "20" />
                       </form>
              </#if>
              </td>
          </tr>
          <#assign rowCount = rowCount + 1>
          <#-- toggle the row color -->
          <#assign alt_row = !alt_row>
        </#list>
      </table>
    <#else>
      <div id="findPartyResults_2">
        <h3>${uiLabelMap.NoVendorsFound}</h3>
      </div>
    </#if>
    <#if lookupErrorMessage?exists>
      <h3>${lookupErrorMessage}</h3>
    </#if>
  </div>
</#if>
</div>
