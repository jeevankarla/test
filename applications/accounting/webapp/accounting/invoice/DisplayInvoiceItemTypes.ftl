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
<style type="text/css">
	.readOnlyColumnClass {
			font-weight: normal;
			background: mistyrose;
		}
	.toolTipNotice { /* tooltip style */
    	background-color: #ffffff;
    	border: 0.1em;
    	color: #f97000;
    	font-size: 100%;
    	margin: 0.4em;
    	padding: 0.1em;
	}	
	.toolTipText { /* tooltip style */
    	//background-color: #ffffbb;
    	border: 0.1em;
    	color: #f97000;
    	font-size: 100%;
    	margin: 0.4em;
    	padding: 0.1em;
	}
</style>	
	<div class="screenlet">
		 <div class="screenlet-title-bar">
		    <ul>
		      <#--><li class="h3"><label><span class="toolTipNotice" id="boothDepositTip">Find 'Available Invoice Item Types' Under Above Invoice Header</span></label></li>-->
		      <li class="h3"><label><span class="toolTipNotice" id="boothDepositTip">Choose Expenditure Head</span></label></li>
		    </ul>
		    <br class="clear"/>
		 </div>
		 <div class="screenlet-body">
				    <table id="customFields" class="basic-table hover-bar" cellspacing="0" width="10">
				      <#--<a id="addnew" href="" >add</a>-->
				      <thead>
				        <tr class="header-row-2">
				          <td>Sl.No:</td>	
				          <#--><td>ItemType Id</td>-->
				           <td>Expenditure Head</td>
				        </tr>
				      </thead>
				      	         
				      <tbody>
				        <#assign alt_row = false>
				        <#assign itemNumber = 1>
				        <#if invoiceItemTypes?exists &&  invoiceItemTypes?has_content >
				        <#list invoiceItemTypes as eachInvoiceItem>
				            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
				             <td>${itemNumber}</td>
				              <#--><td>${eachInvoiceItem.invoiceItemTypeId}</td>-->
				              <td class="h3">><label><span class="toolTipText" id="toolTipText"> ${eachInvoiceItem.description?if_exists}</span></label></td>
				            </tr>
				            <#-- toggle the row color -->
				             <#assign itemNumber = itemNumber+1>
				            <#assign alt_row = !alt_row>
				        </#list>
				        </#if>
				      </tbody>
				    </table>
		 </div>
		 	
	</div>
	
  
