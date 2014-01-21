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

<div id="findOrdersList" class="screenlet">
  <div class="screenlet-title-bar">
    <ul>
      <li class="h3">${uiLabelMap.OrderGatepassOrderFor} ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(filterDate)}</li>
      <#assign listSize = state.getSize()>
      <#if (listSize > 10)>
        <li><a href="/ordermgr/control/orderlist?viewIndex=${state.getViewIndex() + 1}&amp;viewSize=${state.getViewSize()}&amp;filterDate=${filterDate?if_exists}">${uiLabelMap.CommonMore}</a></li>
      </#if>
	  <#if orderHeaderList?has_content>
        <li>1-${orderHeaderList.size()} ${uiLabelMap.CommonOf} ${state.getSize()}</li>
      </#if>
    </ul>
    <br class="clear"/>
  </div>
  <div class="screenlet-body">
  	<ul>
    <#if orderHeaderList?has_content>
      <table class="basic-table hover-bar" cellspacing='0'>
        <tr class="header-row">
          <td width="10%">${uiLabelMap.OrderOrder} ${uiLabelMap.CommonNbr}</td>
          <td width="15%">${uiLabelMap.OrderOrderBillToParty}</td>
          <td width="10%">${uiLabelMap.OrderOriginFacility}</td>                      
          <td width="15%">${uiLabelMap.OrderDesiredDeliveryDate}</td>                      
          <td width="10%">${uiLabelMap.CommonAmount}</td>
          <td>${uiLabelMap.CommonStatus}</td>
          <td width="10%">
             <li class="h3">&nbsp;[<a href="<@ofbizUrl>order.pdf?orderId=${orderId}</@ofbizUrl>" target="_blank">PDF</a>&nbsp;]</li>	 
          </td>
          <td>${uiLabelMap.OrderCancelOrder}</td>
        </tr>
        <#assign alt_row = false>
        <#list orderHeaderList as orderHeader>
          <#assign status = orderHeader.getRelatedOneCache("StatusItem")>
          <#assign orh = Static["org.ofbiz.order.order.OrderReadHelper"].getHelper(orderHeader)>
          <#assign billToParty = orh.getBillToParty()?if_exists>
          <#if billToParty?has_content>
            <#assign billToPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", billToParty.partyId, "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
            <#assign billTo = billToPartyNameResult.fullName?default("[${uiLabelMap.OrderPartyNameNotFound}]")/>
          </#if>
          <#assign originFacility = orh.getOriginFacilityId()?if_exists />                      
          <#assign productStore = orderHeader.getRelatedOneCache("ProductStore")?if_exists />
          <tr<#if alt_row> class="alternate-row"</#if>>
            <#assign alt_row = !alt_row>
            <td><a href="/ordermgr/control/orderview?orderId=${orderHeader.orderId}" class="buttontext">${orderHeader.orderId}</a></td>
            <td>${billTo?if_exists}</td>
            <td>${originFacility?if_exists}</td>                          
            <td><#if orderHeader.estimatedDeliveryDate?has_content>${orderHeader.estimatedDeliveryDate.toString()}<#else> </#if></td>                          
            <td><@ofbizCurrency amount=orderHeader.grandTotal isoCode=orderHeader.currencyUom/></td>
            <td>${orderHeader.getRelatedOneCache("StatusItem").get("description",locale)}</td>
			<td>
			<li class="h3">[&nbsp;<a href="<@ofbizUrl>order.pdf?orderId=${orderId}</@ofbizUrl>" target="_blank">PDF</a>&nbsp;]</li>
			</td>
			<td>
			<li><a href="javascript:document.OrderCancel.submit()">${uiLabelMap.OrderCancelOrder}</a>
			<form name="OrderCancel" method="post" action="<@ofbizUrl>changeOrderStatus/orderview</@ofbizUrl>">
			  <input type="hidden" name="statusId" value="ORDER_CANCELLED"/>
			  <input type="hidden" name="setItemStatus" value="Y"/>
			  <input type="hidden" name="workEffortId" value="${workEffortId?if_exists}"/>
			  <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
			  <input type="hidden" name="partyId" value="${assignPartyId?if_exists}"/>
			  <input type="hidden" name="roleTypeId" value="${assignRoleTypeId?if_exists}"/>
			  <input type="hidden" name="fromDate" value="${fromDate?if_exists}"/>
			</form>
			</li>
	      </td> 
          </tr>
        </#list>
      </table>
    <#else>
      <h3>${uiLabelMap.OrderNoOrderFound}</h3>
    </#if>
	</ul>
  </div>
</div>