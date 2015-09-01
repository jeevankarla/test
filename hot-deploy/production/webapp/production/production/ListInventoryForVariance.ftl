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
	
	var inventoryQtyMap = ${StringUtil.wrapString(inventoryQtyJSON)!'{}'};
	var varianceReasonJSON = ${StringUtil.wrapString(varianceReasonTypeJSON)!'{}'};
	
	jQuery(document).ready( function() {
      $("#varianceTypeId").trigger("onchange");
      datepick(undefined);
    });
	
	function datepick(thisObj)
	{	
		var rowObj = $(thisObj).parent();
		var varId = $(rowObj).find('[name=varianceDate]').attr('id');
		
		$("#"+varId).datetimepicker({
		dateFormat:'dd-mm-yy',
		showSecond: true,
		timeFormat: 'hh:mm:ss',
		minDate: '-1d',
		maxDate: '0d',
		changeMonth: false,
		numberOfMonths: 1});		
		$('#ui-datepicker-div').css('clip', 'auto');
		
		if(varId){
			var dateExists = $("#"+varId).val();
			if(!dateExists){
				$("#"+varId).datepicker().datepicker("setDate", new Date());
			}
		}
	}
	
    function changeBatchQty(thisObj){
    	var selectVal = $(thisObj).val();
    	var thisRow = $(thisObj).parent().parent();
    	var changeQty = 0;
    	if(inventoryQtyMap){
    		if(selectVal && selectVal != 'LIFO' && selectVal != 'FIFO'){
    			changeQty  = inventoryQtyMap[selectVal];
    		}
    	}
    	if(changeQty > 0){
    		var varianceObj = $(thisRow).find('#variance');
    		$(varianceObj).val(changeQty);
    		$(varianceObj).prop('readonly', true);
    		$(varianceObj).css('background-color', '#EFEEEE');
    	}
		if(changeQty == 0){
			var varObj = $(thisRow).find('#variance');
			$(varObj).val('');
    		$(varObj).prop('readonly', false);
    		$(varObj).css('background-color', 'white');
		}    	
    }
    function changeVarianceType(thisObj){
    	var index = 0;
    	$('#varianceTab tr').each(function (i, row) {
			if(index != 0){
				var $row = $(row);
		    	var selectVarType = $row.find("#varianceTypeId");
		    	var selectVal = $(selectVarType).val();
		    	if(varianceReasonJSON && selectVal){
		    		var reasonByType = varianceReasonJSON[selectVal];
					if(reasonByType){
						$row.find('#varianceReasonId').find('option').remove();
						for(var i=0;i<reasonByType.length;i++){
							$row.find('#varianceReasonId').append($('<option>', { 
							 	value: reasonByType[i]['varianceReasonId'],
		        				text : reasonByType[i]['description'] 
		    				}));
						}
					}
					    		
		    	}
			}
			index++;
    	});
    }
//]]>
</script>
<#if facilityInventoryList?has_content >
	
    <#-- <div align="right">
    	<table>
    		<tr>
    			<td><input class="myButton" type="button" name="acceptXfer" id="acceptXfer" value="Accept" onclick="javascript:massTransferAcknowledgementSubmit(this, 'IXF_COMPLETE');"/></td>
    			<td>&nbsp;</td>
    			<td><input class="myButton" type="button" name="rejectXfer" id="rejectXfer" value="Reject" onclick="javascript:massTransferAcknowledgementSubmit(this, 'IXF_CANCELLED');"/></td>
    		</tr>
    	</table>
    </div> -->
	
    <table class="basic-table hover-bar" id="varianceTab" cellspacing="1">
      <thead>
        <tr class="header-row-2">
          <td>Product</td>
          <td>Facility</td>
          <td>Inventory Avl.</td>
          <td>Batch [Inv. Avl.]</td>
          <td>Type</td>
          <td>Variance Reason</td>
          <td>Variance Qty</td>
          <td>Date</td>
          <td>Comment</td>
          <td></td>
        </tr>
      </thead>
      <tbody>
        <#assign alt_row = false>
        <#assign index=0>
        <#list facilityInventoryList as eachProd>
        	<form name="inventoryVarianceForm" id="inventoryVarianceForm"  method="post" action='createProductVarianceForFacility'>
        	<input type='hidden' name='productId' id='productId' value='${eachProd.productId?if_exists}'>
        	<input type='hidden' name='facilityId' id='facilityId' value='${eachProd.facilityId?if_exists}'>
        	<#assign facility = delegator.findOne("Facility", {"facilityId" : eachProd.get('facilityId')}, false)>
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
            	<td>${(eachProd.productName)?if_exists} [${(eachProd.productId)?if_exists}]</td>
              	<td>${facility.facilityName?if_exists} [${facility.facilityId?if_exists}]</td>
              	<td>${eachProd.quantity?if_exists}</td>
              	<#assign productId = eachProd.get('productId')>
              	<#assign productBatch = productBatchMap.get(productId)>
              	<td>
              		<select name='inventoryItemId' id='inventoryItemId' onchange= 'javascript: changeBatchQty(this);'>
              			<option value='LIFO'>LIFO</option>
              			<option value='FIFO'>FIFO</option>
              			<#list productBatch as eachBatch>
              				<option value='${eachBatch.get('inventoryItemId')?if_exists}'>${eachBatch.get('productBatchId')?if_exists} [${eachBatch.get('quantity')?if_exists}]</option>
              			</#list>
              		</select>
              	</td>
              	
              	<td>
              		<select name='varianceTypeId' id='varianceTypeId' onchange= 'javascript: changeVarianceType(this);'>
              			<#list varianceTypes as eachType>
              				<option value='${eachType.enumId?if_exists}'>${eachType.description?if_exists}</option>
              			</#list>
              		</select>
              	</td>
              	<td>
              		<select name='varianceReasonId' id='varianceReasonId'>
              		</select>
              	</td>
              	<td><input type='text' name='variance' id='variance' size='8'></td>
              	<td><input type='text' name='varianceDate' id='varianceDate_${index}' onmouseover="datepick(this)" readOnly></td>
              	<td><input type='text' name='comment' id='comment'/></td>
              	<td><input type="submit" value="Submit" id="button1" class="smallSubmit" /></td>
            </tr>
            </form>
            <#assign index = index+1>
            <#assign alt_row = !alt_row>
        </#list>
      </tbody>
    </table>
<#else>
  		<h3>No Inventory Found</h3>
</#if>
