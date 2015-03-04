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
		var issuances = $('input[name="UpdateCrates"]');
	    jQuery.each(issuances, function() {
	    this.checked = master.checked;
	     });
     }
 }

function issueSelected(){
	 var issuanceList = $('input[name=UpdateCrates]:checked');
	 if(issuanceList.size() <=0) {
	 alert("Please Select at least One Request..!")
		 return false;
	 }
	 var paramMap='finallist';
	 var finallist=[];
	 var index = 0;
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
 	 var tempQty=$(tempQtyObj).val();
   	 var issuance=$(this).val();
     var inputElementIdSplit = issuance.split('_');
   	 var appendStr = "<input type=hidden name=custRequestId_o_"+index+" value="+inputElementIdSplit[0]+" />";
  		 appendStr += "<input type=hidden name=custRequestItemSeqId_o_"+index+"  value="+inputElementIdSplit[1]+" />";
         appendStr += "<input type=hidden name=toBeIssuedQty_o_"+index+"  value="+tempQty+" />";
 	$("#submitIssuance").append(appendStr);
 	index = index+1;
    });
     action= issueSelectedRequests;
     jQuery('#submitIssuance').attr("action", action);
     jQuery('#submitIssuance').submit();
}

</script>

<#if custRequestItemsList?has_content>

<#assign flag = "false">
<#list custRequestItemsList as custRequestItem>
<#if (custRequestItem.QOH >= 1) >
<#assign flag = "true">
</#if>
</#list>
<#if flag == "true">
<div align="right">
  <form name="submitIssuance" id="submitIssuance"  method="post" align="right">

   <td align="right" >    
      <input id="submitButton" type="button"  onclick="javascript:issueSelected();" value="Issue Selected"/>
      <input type="checkbox" id="bulkCheckBox" name="submitBulkStatus" onchange="javascript:checkAllIndentApprovalStatus(this);">Select All</input>   
  </td>
    
 <#-- </form>
   <form name="submitBulkRejectStatus" id="ListIndentReject"  method="post" action="makeMassReject">-->

  </form>
</div>
</#if>
</#if>
