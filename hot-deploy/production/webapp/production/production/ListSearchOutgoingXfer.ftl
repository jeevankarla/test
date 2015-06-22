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
<style type="text/css">

.myButton {
	border-radius:3px;
	border:2px solid #BAD0EE;
	display:inline-block;
	cursor:pointer;
	color:#ffffff;
	font-family:arial;
	font-size:12px;
	padding:5px 10px;
	text-decoration:none;
	width: 100px;
}
.myButton:hover {
	background-color:#BAD0EE;
}
</style>
<script type="text/javascript">
//<![CDATA[
	
	
	function toggleTransferGroupId(master) {
        var transfers = jQuery("#listOutXfer :checkbox[name='transferGroupIds']");
        jQuery.each(transfers, function() {
            this.checked = master.checked;
        });
    }
        
    function massTransferStatusChangeSubmit(current, statusId){
    	jQuery(current).attr( "disabled", "disabled");
    	var transferGroup = jQuery("#listOutXfer :checkbox[name='transferGroupIds']");
        var index = 0;
        jQuery.each(transferGroup, function() {
            if (jQuery(this).is(':checked')) {
            	var domObj = $(this).parent().parent();
            	var transferObj = $(domObj).find("#transferGroupId");
            	var transId = $(transferObj).val();
            	var appendStr = "";
            	appendStr += "<input type=hidden name=transferGroupId_o_"+index+" value="+transId+" />";
            	appendStr += "<input type=hidden name=statusId_o_"+index+" value="+statusId+" />";
   				$("#updateTransferGroupForm").append(appendStr);
   				index = index+1;
            }
        });
       	jQuery('#updateTransferGroupForm').submit();
    }
    
        
//]]>
</script>

<form name="updateTransferGroupForm" id="updateTransferGroupForm" method="post" action="updateTransferGroupStatus">
</form>
<#if outgoingTransfer?has_content>
  <div id="incomingXfer">

  </div>
  <form name="listOutXfer" id="listOutXfer"  method="post">
    <div align="right">
    	<table>
    		<tr>
    			<td><input class="myButton" type="button" name="approveXfer" id="approveXfer" value="Approve" onclick="javascript:massTransferStatusChangeSubmit(this, 'IXF_EN_ROUTE');"/></td>
    			<td>&nbsp;</td>
    			<td><input class="myButton" type="button" name="cancelXfer" id="cancelXfer" value="Cancel" onclick="javascript:massTransferStatusChangeSubmit(this, 'IXF_CANCELLED');"/></td>
    		</tr>
    	</table>
    </div>
	
    <table class="basic-table hover-bar" cellspacing="0">
      <thead>
        <tr class="header-row-2">
          <td>Transfer Id</td>
          <td>From Plant/Silo</td>
          <td>To Plant/Silo</td>
          <td>Product</td>
          <td>Status</td>
          <td>Quantity</td>
          <td align='center'>${uiLabelMap.CommonSelect} <input type="checkbox" id="checkAllTransfers" name="checkAllTransfers" onchange="javascript:toggleTransferGroupId(this);"/></td>
        </tr>
      </thead>
      <tbody>
        <#assign alt_row = false>
        <#list outgoingTransfer as eachXfer>
        	<#assign fromFacility = delegator.findOne("Facility", {"facilityId" : eachXfer.fromFacilityId}, false)> 
        	<#assign toFacility = delegator.findOne("Facility", {"facilityId" : eachXfer.toFacilityId}, false)>
        	<#assign product = delegator.findOne("Product", {"productId" : eachXfer.productId}, false)>
        	<#assign status = delegator.findOne("StatusItem", {"statusId" : eachXfer.statusId}, false)>   
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
            	<input type="hidden" name="transferGroupId" id="transferGroupId" value="${eachXfer.transferGroupId?if_exists}">
	            <td>${(eachXfer.transferGroupId)?if_exists}</td>
              	<td>${fromFacility.facilityName?if_exists} [${eachXfer.fromFacilityId?if_exists}]</td>
              	<td>${toFacility.facilityName?if_exists} [${eachXfer.toFacilityId?if_exists}]</td>
              	<td>${(product.productName)?if_exists} [${eachXfer.productId}]</td>
              	<td>${status.description?if_exists}</td>
              	<td>${eachXfer.xferQtySum?if_exists}</td>
              	<td align='center'><input type="checkbox" id="transferGroupIds" name="transferGroupIds" value="${eachXfer.transferGroupId?if_exists}"/></td>
            </tr>
            <#assign alt_row = !alt_row>
        </#list>
      </tbody>
    </table>
  </form>
<#else>
  <h3>No Transfers Found</h3>
</#if>
