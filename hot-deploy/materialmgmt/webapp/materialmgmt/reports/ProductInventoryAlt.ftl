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
<div class="screenlet">
  <#if product?exists>
    <div class="screenlet-body">
                   <#-->     <#assign rowClass = "2">   -->
                 <#assign sNo=1>     
             <#list quantitySummaryByFacility.values() as quantitySummary>
                <#if quantitySummary.facilityId?exists>
                    <#assign facilityId = quantitySummary.facilityId>
                    <#assign facility = delegator.findByPrimaryKey("Facility", Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId", facilityId))>
                   
                    <#assign totalQuantityOnHand = quantitySummary.totalQuantityOnHand?if_exists>
                    <#assign totalAvailableToPromise = quantitySummary.totalAvailableToPromise?if_exists>
                    <#assign mktgPkgATP = quantitySummary.mktgPkgATP?if_exists>
                    <#assign mktgPkgQOH = quantitySummary.mktgPkgQOH?if_exists>
                    <#assign incomingShipmentAndItemList = quantitySummary.incomingShipmentAndItemList?if_exists>
                    <#assign totalQuantityInQcHand = quantitySummary.totalQuantityInQcHand?if_exists>
                    <#assign receivedQty = quantitySummary.receivedQty?if_exists> 
                    <#assign minimumStock = quantitySummary.minimumStock?if_exists> 
                   <#if sNo != 1>
               <fo:block   text-align="center" font-size="15pt" white-space-collapse="false" font-weight="bold">----------------------------------------</fo:block>  
                   </#if>
            <table style="width:100%; font-size:1.3em">
                 <tr class="header-row">
                <td style="text-align: left"><b>${uiLabelMap.ProductFacility} &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;:</b></td>
                <td style="padding-top: 1em; color: green; font-weight: bold; font-size:1.1em text-align: right" width="50%">${(facility.facilityName)?if_exists}</td>
                 </tr>
                 <tr>
                <td style="text-align: left"><b>Quantity &nbsp;&nbsp;&nbsp;:</b></td>
                <td style="padding-top: 1em; color: green; font-weight: bold; font-size:1.1em; text-align: right"><#if totalQuantityOnHand?exists>${totalQuantityOnHand}<#else>&nbsp;</#if></td>
                 </tr>
               <tr>
                <td style="text-align: left"><b>In-QC &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;:</b></td>
                <td style="padding-top: 1em; color: green; font-weight: bold; font-size:1.1em; text-align: right"><#if totalQuantityInQcHand?exists>${totalQuantityInQcHand}<#else>&nbsp;</#if></td>
              </tr>
                <tr>
                <td style="text-align: left"><b>Received &nbsp;&nbsp;:</b></td>
                <td style="padding-top: 1em; color: green; font-weight: bold; font-size:1.1em; text-align: right"><#if receivedQty?exists>${receivedQty}<#else>&nbsp;</#if></td>
                </tr>
                   <tr>
                <td style="text-align: left"><b>Min. Qty&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;:</b></td>
                <td style="padding-top: 1em; color: green; font-weight: bold; font-size:1.1em; text-align: right"><#if minimumStock?exists>${minimumStock}<#else>&nbsp;</#if></td>
                   </tr>
         </table>
        <#assign sNo=sNo+1>
                </#if>
                <#-- toggle the row color 
                <#if rowClass == "2">
                    <#assign rowClass = "1">
                <#else> 
                    <#assign rowClass = "2">
                 </#if>  -->
            </#list>
    </div>
     <div class="clear"></div>
  <#else>
    <h3>No inventory found for :${productId?if_exists}!</h3>
  </#if>
</div>
