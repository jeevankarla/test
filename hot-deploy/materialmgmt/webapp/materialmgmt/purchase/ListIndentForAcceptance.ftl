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
<script type="text/javascript">
//<![CDATA[
	
	
	function toggleRequestId(master) {
        var indents = jQuery("#listIndent :checkbox[name='indentCheckBoxId']");
        jQuery.each(indents, function() {
            this.checked = master.checked;
        });
    }
    
    function processIndent(current){
    	jQuery(current).attr( "disabled", "disabled");
    	var actionFlag = $(current).val();
    	var indents = jQuery("#listIndent :checkbox[name='indentCheckBoxId']");
        var index = 0;
        jQuery.each(indents, function() {
            if (jQuery(this).is(':checked')) {
            	var domObj = $(this).parent().parent();
            	var custRequestObj = $(domObj).find("[name='custRequestId']");
            	var indentId = $(custRequestObj).val();
            	var custRequestItemObj = $(domObj).find("[name='custRequestItemSeqId']");
            	var indentItemId = $(custRequestItemObj).val();
            	var appendStr = "<input type=hidden name=custRequestId_o_"+index+" value="+indentId+" />";
            	$("#updateRequestStatusForm").append(appendStr);
            	var appendStr1 = "<input type=hidden name=custRequestItemSeqId_o_"+index+" value="+indentItemId+" />";
            	$("#updateRequestStatusForm").append(appendStr1);
            	index = index+1;
            }
            
        });
        var appStr = "";
    	appStr += "<input type=hidden name=custRequestItemStatusId value=CRQ_COMPLETED />";
        $("#updateRequestStatusForm").append(appStr);
        jQuery('#updateRequestStatusForm').submit();
    }
       
//]]>
</script>

<form name="updateRequestStatusForm" id="updateRequestStatusForm" method="post" action="updateRequestAcknowledgmentStatus" ></form>

<#if indentItems?has_content>
  
  <form name="listIndent" id="listIndent"  method="post" >
    <div align="right" width="100%">
    	<table width="25%">
    		<tr>
    			<td></td>
    			<td></td>
    			<td align="right"><h2><input id="submitButton" type="button"  onclick="javascript:processIndent(this);" value="Received"/></h2></td>
    		</tr>
    	</table>
    </div>
    <table class="basic-table hover-bar" cellspacing="0">
      <thead>
        <tr class="header-row-2">
          <td>Indent No</td>
          <td>Item No</td>
          <td>Indent Ref.</td>
          <td>Indent Date</td>
          <td>From Department</td>
          <td>Material Name - [Code][UOM]</td>
          <td>quantity</td>
          
		  <td align="right" cell-padding>${uiLabelMap.CommonSelect} <input type="checkbox" id="checkAllIndents" name="checkAllIndents" onchange="javascript:toggleRequestId(this);"/></td>
        </tr>
      </thead>
      <tbody>
      <#assign alt_row = false>
      <#assign index = 0>
      <#list indentItems as eachItem>
      		<tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
            	<input type=hidden name=custRequestId value='${eachItem.custRequestId?if_exists}'>
            	<input type=hidden name=custRequestItemSeqId value='${eachItem.custRequestItemSeqId?if_exists}'>
            	<td><h2><a class="buttontext" href="<@ofbizUrl>ViewMaterialRequest?custRequestId=${eachItem.custRequestId?if_exists}</@ofbizUrl>" target="_blank"/>${eachItem.custRequestId?if_exists}</h2></td>
              	<td>${eachItem.custRequestItemSeqId?if_exists}</td>
              	<td>${eachItem.custRequestName?if_exists}</td>
              	<td>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(eachItem.custRequestDate, "dd/MM/yyyy")}</td>
				<#assign partyName = (delegator.findOne("PartyNameView", {"partyId" : eachItem.fromPartyId}, false))!>
              	<td>${partyName.groupName?if_exists}${partyName.firstName?if_exists} ${partyName.lastName?if_exists} - [${eachItem.fromPartyId}]</td>
              	<#assign product = (delegator.findOne("Product", {"productId" : eachItem.productId}, false))!>
              	<#assign uom = (delegator.findOne("Uom", {"uomId" : product.quantityUomId}, false))!>
              	<td>${product.productName?if_exists} - [${eachItem.productId?if_exists}][${uom.description?if_exists}]</td>
              	<td>${eachItem.quantity?if_exists}</td>
           		<td><input type="checkbox" id="indentCheckBoxId_${eachItem_index}" name="indentCheckBoxId"/></td>
            </tr>
            <#assign index = index+1>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        </#list>
      </tbody>
    </table>
  </form>
<#else>
  <h3>No Indent Item Found To Accept</h3>
</#if>