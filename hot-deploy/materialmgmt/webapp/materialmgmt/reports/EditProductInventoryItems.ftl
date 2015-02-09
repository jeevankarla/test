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
<#assign externalKeyParam = "&amp;externalLoginKey=" + requestAttributes.externalLoginKey?if_exists>
<div class="screenlet">
  <#if product?exists>
    <div class="screenlet-title-bar">
        <h3>${uiLabelMap.ProductInventoryItems} ${uiLabelMap.CommonFor} <#if product?exists>${(product.internalName)?if_exists} </#if> [${uiLabelMap.CommonId}:${productId?if_exists}]</h3>
    </div>
    <div class="screenlet-body">
       <#-- <#if productId?has_content>
            <a href="/facility/control/EditInventoryItem?productId=${productId}${externalKeyParam}" class="buttontext">${uiLabelMap.ProductCreateNewInventoryItemProduct}</a>
            <#if showEmpty>
                <a href="<@ofbizUrl>EditProductInventoryItems?productId=${productId}</@ofbizUrl>" class="buttontext">${uiLabelMap.ProductHideEmptyItems}</a>
            <#else>
                <a href="<@ofbizUrl>EditProductInventoryItems?productId=${productId}&amp;showEmpty=true</@ofbizUrl>" class="buttontext">${uiLabelMap.ProductShowEmptyItems}</a>
            </#if>
        </#if>
        <br /> -->
        <#if productId?exists>
            <table cellspacing="0" class="basic-table">
            <tr class="header-row">
                <td><b>${uiLabelMap.ProductItemId}</b></td>
                <td><b>${uiLabelMap.CommonReceived}</b></td>
                <td><b>${uiLabelMap.ProductPerUnitPrice}</b></td>
                <td><b>${uiLabelMap.OrderId}</b></td>
                <td align="right"><b>Quantity</b></td>
            </tr>
            <#assign rowClass = "2">
            <#list productInventoryItems as inventoryItem>
               <#-- NOTE: Delivered for serialized inventory means shipped to customer so they should not be displayed here any more -->
               <#if showEmpty || (inventoryItem.inventoryItemTypeId?if_exists == "SERIALIZED_INV_ITEM" && inventoryItem.statusId?if_exists != "INV_DELIVERED")
                              || (inventoryItem.inventoryItemTypeId?if_exists == "NON_SERIAL_INV_ITEM" && ((inventoryItem.availableToPromiseTotal?exists && inventoryItem.availableToPromiseTotal != 0) || (inventoryItem.quantityOnHandTotal?exists && inventoryItem.quantityOnHandTotal != 0)))>
                    <#assign curInventoryItemType = inventoryItem.getRelatedOne("InventoryItemType")>
                    <#assign curStatusItem = inventoryItem.getRelatedOneCache("StatusItem")?if_exists>
                    <#assign facilityLocation = inventoryItem.getRelatedOne("FacilityLocation")?if_exists>
                    <#assign facilityLocationTypeEnum = (facilityLocation.getRelatedOneCache("TypeEnumeration"))?if_exists>
                    <#assign inventoryItemDetailFirst = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(inventoryItem.getRelated("InventoryItemDetail", Static["org.ofbiz.base.util.UtilMisc"].toList("effectiveDate")))?if_exists>
                    <#if curInventoryItemType?exists>
                        <tr valign="middle"<#if rowClass == "1"> class="alternate-row"</#if>>
                            <td><#--<a href="/facility/control/EditInventoryItem?inventoryItemId=${(inventoryItem.inventoryItemId)?if_exists}${externalKeyParam}" class="buttontext">-->${(inventoryItem.inventoryItemId)?if_exists}<#--</a>--></td>
                            <td><#if inventoryItem.datetimeReceived?has_content>&nbsp;${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(inventoryItem.datetimeReceived?if_exists, "dd/MM/yyyy")}</#if></td>
                            <td><@ofbizCurrency amount=inventoryItem.unitCost isoCode=inventoryItem.currencyUomId/></td>
                            <td>
                                <#if inventoryItemDetailFirst?exists && inventoryItemDetailFirst.workEffortId?exists>
                                    <b>${uiLabelMap.ProductionRunId}</b> ${inventoryItemDetailFirst.workEffortId}
                                <#elseif inventoryItemDetailFirst?exists && inventoryItemDetailFirst.orderId?exists>
                                     ${inventoryItemDetailFirst.orderId}
                                </#if>
                            </td>
                            <#if inventoryItem.inventoryItemTypeId?if_exists == "NON_SERIAL_INV_ITEM">
                                <td align="right">
                                    <div>${(inventoryItem.quantityOnHandTotal)?default("NA")}</div>
                                </td>
                            <#elseif inventoryItem.inventoryItemTypeId?if_exists == "SERIALIZED_INV_ITEM">
                                <td align="right">&nbsp;${(inventoryItem.serialNumber)?if_exists}</td>
                            <#else>
                                <td align="right" style="color: red;">${uiLabelMap.ProductErrorType} ${(inventoryItem.inventoryItemTypeId)?if_exists} ${uiLabelMap.ProductUnknownSerialNumber} (${(inventoryItem.serialNumber)?if_exists})
                                    ${uiLabelMap.ProductAndQuantityOnHand} (${(inventoryItem.quantityOnHandTotal)?if_exists} ${uiLabelMap.CommonSpecified}</td>
                            </#if>
                        </tr>
                    </#if>
                </#if>
                <#-- toggle the row color -->
                <#if rowClass == "2">
                    <#assign rowClass = "1">
                <#else>
                    <#assign rowClass = "2">
                </#if>
            </#list>
          </table>
        </#if>
    </div>
  <#else>
    <h2>${uiLabelMap.ProductProductNotFound} ${productId?if_exists}!</h2>
  </#if>
</div>