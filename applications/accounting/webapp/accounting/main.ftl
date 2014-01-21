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
<div>
<table cellspacing="10" cellpadding="15">

	<tr>
    	<td><h1 class="h1">${uiLabelMap.AccountingPaymentsMenu}</h1></td>
     	<td/>
	 	<td style="padding-left:50px;"/>
     	<td><h1 class="h1">${uiLabelMap.AccountingInvoicesMenu}</h1></td>
     	<td/>
  	</tr>

	<tr>
		<td>
			<ul>
				<li><a href="<@ofbizUrl>findPayments?noConditionFind=Y&amp;lookupFlag=Y</@ofbizUrl>">${uiLabelMap.AccountingShowAllPayments}</a></li>
			</ul>
		</td>
		<td>
			<ul>
			<#list paymentStatus as status>
				<li style="margin-bottom:8px;"><a href="<@ofbizUrl>findPayments?lookupFlag=Y&amp;statusId=${status.statusId}</@ofbizUrl>">${uiLabelMap.AccountingShowPayments} ${status.get("description",locale)}</a></li>
			</#list>
			</ul>
		</td>
		<td style="padding-left:50px;"/>
		<td>
			<ul>
				<li><a href="<@ofbizUrl>findInvoices?noConditionFind=Y&amp;lookupFlag=Y</@ofbizUrl>">${uiLabelMap.AccountingShowAllInvoices}</a></li>
			</ul>
		</td>
		<td>
			<ul>
				<#list invoiceStatus as status>
					<#if status.get("description",locale) == "Rejected">
						<#if status.statusId == "INVOICE_RECEIVED">
							<#assign lstatus = uiLabelMap.AccountingShowAPInvoices>
						<#else>				
							<#assign lstatus = uiLabelMap.AccountingShowARInvoices>
						</#if>
					<#else>
						<#assign lstatus = uiLabelMap.AccountingShowInvoices+" "+status.get("description",locale)>
					</#if>
					<li style="margin-bottom:8px;">	
						<a href="<@ofbizUrl>findInvoices?lookupFlag=Y&amp;statusId=${status.statusId}</@ofbizUrl>">	${lstatus} </a>
					</li>
				</#list>
			</ul>
		</td>
	</tr>

</table>
</div>
