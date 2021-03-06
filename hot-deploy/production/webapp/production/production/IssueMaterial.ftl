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



<script type="application/javascript">

function checkAllIndentApprovalStatus(master){
 var flag="false";
    if (confirm("Are You Sure to Check or UnCheck All?") == true) {
        flag = "true";
    } 
    if(flag == "true"){
		var issuances = $('input[name="checkIssuance"]');
	    jQuery.each(issuances, function() {
	    this.checked = master.checked;
	     });
     }
 }
 function datetimepick(){
		
		var currentTime = new Date();
	 	// First Date Of the month 
	 	var startDateFrom = new Date(0,0,currentTime.getDate()+1);
	 	// Last Date Of the Month 
	 	var startDateTo = new Date(currentTime.getFullYear(),currentTime.getMonth() +1,0);
	  
		 $("#issuedDate").datetimepicker({
			dateFormat:'dd-mm-yy',
			changeMonth: true,
		      minDate: '-1d',
		      maxDate: '0d'
		 });	
				
		$('#ui-datepicker-div').css('clip', 'auto');
		
		//$('#startDate').val('2015-04-21 10:50:20');	
	}

function issueSelected(){
	 var issuanceList = $('input[name=checkIssuance]:checked');
	 if(issuanceList.size() <=0) {
	 alert("Please Select at least One Request..!")
		 return false;
	 }
	 var paramMap='finallist';
	 var finallist=[];
	 var index = 0;
	 var submitCount=0;
	 var issueSelectedRequests = 'issueSelectedRequests';
	 var issueMaterial = 'issueMaterial';	
	 var action;
     jQuery.each(issuanceList, function() {
   	 var curreElem = $(this);
 	 var varform = curreElem.parent().parent();
 	 var form = curreElem.parent().parent().find("form");
 	 var formId = form.attr('id');
 	 var str = "#"+formId;
 	 var tempQtyObj=$(varform).find("[name='"+"tempQty"+"']");
 	 var facilityObj=$(varform).find("[name='"+"facilityId"+"']");
 	 var shipmentTypeObj = $(varform).find("[name='"+"shipmentTypeId"+"']");
 	 var qohObj = $(varform).find("[name='"+"QOH"+"']");
 	 var issuedQtyObj = $(varform).find("[name='"+"issuedQty"+"']");
 	 var quantityObj = $(varform).find("[name='"+"quantity"+"']");
 	 var qoh = $(qohObj).val();
 	 var facilityId =$(facilityObj).val();
 	 if(facilityId == undefined || facilityId == ''){
 	  	submitCount=submitCount+1;
 	 	alert("Please select Facility..!");
 	 	return false;
 	 }
 	 if(qoh<=0){
 	 	submitCount=submitCount+1;
 	 	alert("Quantity not available to Isuue..!");
 	 	return false;
 	 }
 	 var shipmentTypeId = $(shipmentTypeObj).val();
 	 var tempQty=$(tempQtyObj).val();
 	 if(tempQty<0){
 	 	submitCount=submitCount+1;
 	 	alert("Please check QtyToIssue..!");
 	 	return false;
 	 }
 	 var issuedQty = $(issuedQtyObj).val();
 	 var quantity = $(quantityObj).val();
 	 var totalQty = parseInt(issuedQty)+parseInt(tempQty);
 	 if(totalQty>quantity){
 	 	submitCount=submitCount+1;
 	 	alert("Please check QtyToIssue.!");
 	 	return false;
 	 }
   	 var issuance=$(this).val();
     var inputElementIdSplit = issuance.split('_');
   	 var appendStr = "<input type=hidden name=custRequestId_o_"+index+" value="+inputElementIdSplit[0]+" />";
  		 appendStr += "<input type=hidden name=custRequestItemSeqId_o_"+index+"  value="+inputElementIdSplit[1]+" />";
         appendStr += "<input type=hidden name=toBeIssuedQty_o_"+index+"  value="+tempQty+" />";
         appendStr += "<input type=hidden name=shipmentTypeId_o_"+index+" value="+shipmentTypeId+" />";
         appendStr += "<input type=hidden name=facilityId_o_"+index+" value="+facilityId+" />";
 	$("#submitIssuance").append(appendStr);
 	index = index+1;
    });
     action= issueSelectedRequests;
     jQuery('#submitIssuance').attr("action", action);
    if(submitCount==0){
     	jQuery('#submitIssuance').submit();
     }
}
function getInvAvailBalance(element){
	var curreElem = $(element);
 	var varform = curreElem.parent().parent().parent();
	var facilityObj=$(varform).find("[name='"+"facilityId"+"']");
	var facilityId =$(facilityObj).val();
	var productIdObj=$(varform).find("[name='"+"productId"+"']");
	var productId =$(productIdObj).val();
	var partyIdFromObj=$(varform).find("[name='"+"partyIdFrom"+"']");
	var partyIdFrom =$(partyIdFromObj).val();
	var qohObj = $(varform).find("[name='"+"QOH"+"']");
	var issuedQtyObj = $(varform).find("[name='"+"issuedQty"+"']");
 	var quantityObj = $(varform).find("[name='"+"quantity"+"']");
 	var tempQtyObj=$(varform).find("[name='"+"tempQty"+"']");
 	var issuedQty = $(issuedQtyObj).val();
 	var quantity = $(quantityObj).val();
 	var quantity = quantity.replace(/,/g, '');
 	var issuedQty = issuedQty.replace(/,/g, '');
 	var toIssueQty = parseInt(quantity)-parseInt(issuedQty);
 	if(typeof(facilityId)!= "undefined" && facilityId!='' && facilityId != null ){
	$.ajax({
         type: "POST",
         url: 'getProductAvailableToPromise',
         data: {productId : productId,
                ownerPartyId : partyIdFrom,
                facilityId : facilityId},
         dataType: 'json',
         success: function(result) {
           if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){

        	   //populateError(result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]);
           }else{              
        	    invQty = parseInt(result["avlToPromise"]);
        	    $(qohObj).val(invQty);
        	    $(qohObj).attr("readOnly","readOnly");
        	    if(invQty>toIssueQty){
        	    	$(tempQtyObj).val(toIssueQty);
        	    }
        	    if(invQty<toIssueQty){
        	    	$(tempQtyObj).val(invQty);
        	    }
        	    
        	    if(invQty == 0){
        	    		$.ajax({
					         type: "POST",
					         url: 'getProductAvailableToPromise',
					         data: {productId : productId,
					                ownerPartyId : "Company",
					                facilityId : facilityId},
					         dataType: 'json',
					         success: function(result) {
					           if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					        	   //populateError(result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]);
					           }else{               			
					        	    invQty = parseInt(result["avlToPromise"]);
					        	    $(qohObj).val(invQty);
					        	    $(qohObj).attr("readOnly","readOnly");
					        	    if(invQty>toIssueQty){
						    	    	$(tempQtyObj).val(toIssueQty);
						    	    }
						    	    if(invQty<toIssueQty){
						    	    	$(tempQtyObj).val(invQty);
						    	    }
					           }
					         } 
					    });
        	    
        	    }
           }
         } 
    });
    }else{
    		$(tempQtyObj).val(0);
    		$(qohObj).val(0);
    }
}

</script>

<#if custRequestItemsList?has_content>

<#assign flag = "false">
<#list custRequestItemsList as custRequestItem>
<#if (custRequestItem.QOH >= 0) >
<#assign flag = "true">
</#if>
</#list>
<#if flag == "true">
<div align="right">
  <form name="submitIssuance" id="submitIssuance"  method="post" >
  <tr>
    <td ><label class="h3">Issue Date :</label><input class='h3' type="text" size="15pt"  id="issuedDate" name="issuedDate" onmouseover='datetimepick()' readOnly/>
	</td>
   <td >    
      <input id="submitButton" type="button"  onclick="javascript:issueSelected();" value="Issue Selected"/>
      <input type="checkbox" id="bulkCheckBox" name="submitBulkStatus" onchange="javascript:checkAllIndentApprovalStatus(this);"><label class="h3">Select All</label></input>   
  </td>
    </tr>
 <#-- </form>
   <form name="submitBulkRejectStatus" id="ListIndentReject"  method="post" action="makeMassReject">-->

  </form>
</div>
</#if>
</#if>
