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

<#assign shoppingCartOrderType = "">
<#assign shoppingCartProductStore = "NA">
<#assign shoppingCartChannelType = "">
<#if shoppingCart?exists>
  <#assign shoppingCartOrderType = shoppingCart.getOrderType()>
  <#assign shoppingCartProductStore = shoppingCart.getProductStoreId()?default("NA")>
  <#assign shoppingCartChannelType = shoppingCart.getChannelType()?default("")>
<#else>
<#-- allow the order type to be set in parameter, so only the appropriate section (Sales or Purchase Order) shows up -->
  <#if parameters.orderTypeId?has_content>
    <#assign shoppingCartOrderType = parameters.orderTypeId>
  </#if>
  <#assign shoppingCartProductStore = defaultProductStore.productStoreId>  
</#if>
<!-- Sales Order Entry -->
<#if security.hasEntityPermission("ORDERMGR", "_CREATE", session)>
<#if shoppingCartOrderType != "PURCHASE_ORDER">
<div class="screenlet">
  <div class="screenlet-title-bar">
    <ul>
      <li class="h3">${uiLabelMap.OrderSalesOrder}<#if shoppingCart?exists>&nbsp;${uiLabelMap.OrderInProgress}</#if></li>
      <li><a href="javascript:document.salesentryform.submit();">${uiLabelMap.CommonContinue}</a></li>
    </ul>
    <br class="clear"/>
  </div>
  <div class="screenlet-body">
      <form method="post" name="salesentryform" action="<@ofbizUrl>initsimpleorderentry</@ofbizUrl>">
      <input type="hidden" name="originOrderId" value="${parameters.originOrderId?if_exists}"/>
      <input type="hidden" name="facilityId" value="${parameters.facilityId?if_exists}"/>
      
      <input type="hidden" name="productStoreId" value="${shoppingCartProductStore}"/>      
      <input type="hidden" name="finalizeMode" value="type"/>
      <input type="hidden" name="orderMode" value="SALES_ORDER"/>
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr><td colspan="4">&nbsp;</td></tr>
        <#if partyId?exists>
          <#assign thisPartyId = partyId>
        <#else>
          <#assign thisPartyId = requestParameters.partyId?if_exists>
        </#if>
        <#if originFacilityId?exists>
          <#assign thisoriginFacilityId = originFacilityId>
        <#else>
          <#assign thisoriginFacilityId = requestParameters.originFacilityId?if_exists>
        </#if> 
        <#if shoppingCart?exists>
        	<#assign value = shoppingCart.getEstimatedDeliveryDate()?default("")>
        </#if>	
        <tr>
          <td>&nbsp;</td>
          <td align='right' valign='middle' nowrap="nowrap"><div class='tableheadtext h2'>${uiLabelMap.OrderDesiredDeliveryDate}</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext'>
            	<@htmlTemplate.renderDateTimeField name="estimatedDeliveryDate" value="${value!''}" className="required" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="item1" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>                     
            </div>
          </td>
        </tr>                
        <tr>
          <td>&nbsp;</td>
          <td align='right' valign='middle' nowrap="nowrap"><div class='tableheadtext h2'>${uiLabelMap.OrderFacilityBooth}</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext'>
              <@htmlTemplate.lookupField value='${thisOriginFacilityId?if_exists}' formName="salesentryform" name="originFacilityId" id="originFacilityId" className="required" fieldFormName="LookupFacility"/>
            </div>
          </td>
        </tr>        
      </table>
      </form>
  </div>
</div>
</#if>
</#if>
