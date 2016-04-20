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
<#-- <#macro maskSensitiveNumber cardNumber>
  <#assign cardNumberDisplay = "">
  <#if cardNumber?has_content>
    <#assign size = cardNumber?length - 4>
    <#if (size > 0)>
      <#list 0 .. size-1 as foo>
        <#assign cardNumberDisplay = cardNumberDisplay + "*">
      </#list>
      <#assign cardNumberDisplay = cardNumberDisplay + cardNumber[size .. size + 3]>
    <#else>
      <#-- but if the card number has less than four digits (ie, it was entered incorrectly), display it in full -->
   <#--   <#assign cardNumberDisplay = cardNumber>
    </#if>
  </#if>
  ${cardNumberDisplay?if_exists}
</#macro> -->

<div class="screenlet">
  <div class="screenlet-title-bar">
      <ul><li class="h3">&nbsp;Payment Information</li></ul>
      <br class="clear"/>
  </div>
  <div class="screenlet-body">
     <table class="basic-table" cellspacing='0'>
     <#assign orderTypeId = orderReadHelper.getOrderTypeId()> 
     <#if orderTypeId == "SALES_ORDER">
       <tr>
         <th>PaymentID</th>
         <th>${uiLabelMap.CommonAmount}</th>
         <th>Payment Date</th>    
         <th>Pament Method Type</th>
         <th>${uiLabelMap.CommonStatus}</th> 
         <th>Entry by</th> 

       </tr>
       <tr><td class="h3" colspan="6"><hr size="30%"  /></td></tr>
       <#if PaymentList?has_content>
       <#list PaymentList as payment>
       <tr>
       	<td><a href="/accounting/control/paymentOverview?paymentId=${payment.paymentId?if_exists}" target="_BLANK" class="buttontext" >${payment.paymentId?if_exists}</a></td>
       	<td>${payment.amount?if_exists?string("##0.00")}</td>
        <td>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(payment.paymentDate, "dd-MMM-yyyy")}</td>
        <#if payment?has_content && payment.paymentMethodTypeId?has_content>
            <#assign paymentMethodType = delegator.findOne("PaymentMethodType", {"paymentMethodTypeId" : payment.paymentMethodTypeId}, true)>
          </#if>
       	<td><#if paymentMethodType?has_content>${paymentMethodType.description?if_exists}<#else></#if></td> 
       	 <#assign status = delegator.findOne("StatusItem", {"statusId" : payment.statusId}, true)>
       	<td><#if status?has_content>${status.description?if_exists}<#else> </#if></td>
       	<td>${payment.createdByUserLogin?if_exists}</td>
       </tr>
       </#list>
       </#if>
       </#if>
</table>
</div>
</div> 