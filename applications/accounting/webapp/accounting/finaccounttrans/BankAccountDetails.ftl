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

<style>
.loader {
  border: 16px solid #f3f3f3;
  border-radius: 50%;
  border-top: 16px solid #3498db;
  width: 50px;
  height: 50px;
  -webkit-animation: spin 2s linear infinite;
  animation: spin 2s linear infinite;
}

@-webkit-keyframes spin {
  0% { -webkit-transform: rotate(0deg); }
  100% { -webkit-transform: rotate(360deg); }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>

<script type="text/javascript">

function setContent(){
	var printBankAccountList = $('input[name=pickRecord]').val("Y");
	if(printBankAccountList.size() <=0) {
	 alert("No Rows Process..!")
		 return false;
	 }
	 var index = 0;
	 var action;
     jQuery.each(printBankAccountList, function() {
   	 var curreElem = $(this);
 	 var varform = curreElem.parent().parent();
 	 var form = curreElem.parent().parent().find("form");
 	 var formId = form.attr('id');
 	 var str = "#"+formId;
 	 
 	 var finAccountIdObj=$(varform).find("[name='"+"finAccountId"+"']");
 	 var finAccountId=$(finAccountIdObj).val();
 	 
 	 var finAccountNameObj=$(varform).find("[name='"+"finAccountName"+"']");
 	 var finAccountName=$(finAccountNameObj).val();
 	 
 	 var finAccountCodeObj=$(varform).find("[name='"+"finAccountCode"+"']");
 	 var finAccountCode=$(finAccountCodeObj).val();
 	 
 	 var isOperativeObj=$(varform).find("[name='"+"isOperative"+"']");
 	 var isOperative=$(isOperativeObj).val();
 	 
 	 var balanceObj=$(varform).find("[name='"+"balance"+"']");
 	 var balance=$(balanceObj).val();
 	 
 	 var balanceConfirmationObj=$(varform).find("[name='"+"balanceConfirmation"+"']");
 	 var balanceConfirmation=$(balanceConfirmationObj).val();
 	 
 	 var balanceObj=$(varform).find("[name='"+"balance"+"']");
 	 var balance=$(balanceObj).val();
 	 
 	 var realisationDateObj=$(varform).find("[name='"+"realisationDate"+"']");
 	 var realisationDate=$(realisationDateObj).val();
 	 
 	 var remarksObj=$(varform).find("[name='"+"remarks"+"']");
 	 var remarks=$(remarksObj).val();
 	 
	 var finAccountNameSplit = finAccountName.split(' ');
   	 var appendStr = "<input type=hidden name=finAccountId_o_"+index+" value="+finAccountId+" />";
  		 appendStr += "<input type=hidden name=finAccountNameSplit_o_"+index+"  value="+finAccountNameSplit+" />";
         appendStr += "<input type=hidden name=finAccountCode_o_"+index+"  value="+finAccountCode+" />";
         appendStr += "<input type=hidden name=isOperative_o_"+index+"  value="+isOperative+" />";
         appendStr += "<input type=hidden name=balance_o_"+index+"  value="+balance+" />";
         appendStr += "<input type=hidden name=balanceConfirmation_o_"+index+"  value="+balanceConfirmation+" />";
         appendStr += "<input type=hidden name=realisationDate_o_"+index+"  value="+realisationDate+" />";
         appendStr += "<input type=hidden name=remarks_o_"+index+"  value="+remarks+" />";
 	$("#listBankAccountDetails").append(appendStr);
 	index = index+1;
    });
     action= 'printBankAccountDetails';
     jQuery('#listBankAccountDetails').attr("action", action);
     //jQuery('#listBankAccountDetails').attr("target", "blank");
     jQuery('#listBankAccountDetails').submit();
}

</script>


<#if bankAccountDetailList?has_content>
    <form name="listBankAccountDetails" id="listBankAccountDetails"  method="post" action="printBankAccountDetails">
    <table class="basic-table hover-bar" cellspacing="0">
      <thead>
        <tr class="header-row-2">
          <td>Account ID</td>
          <td>Name of Bank</td>
          <td>Bank Acc No</td>
          <td>Is Operative(Y or N)</td>
          <td>Bank balance</td>
          <td>Balance confirmation is on record Yes/No</td>
          <td>BRS available as on</td>
          <td>Remarks</td>
      </thead>
      <tbody>
        <#assign alt_row = false>
        <#assign index = 0>
        <#list bankAccountDetailList as bankAccountDetailEntry>
 			<#assign realisationDate = ""/>   
 			<#if bankAccountDetailEntry.realisationDate?has_content>    
        	<#assign realisationDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(bankAccountDetailEntry.realisationDate, "MMMM")/>
        	</#if>
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
              <td>${bankAccountDetailEntry.finAccountId?if_exists}<input type="hidden" name="finAccountId" id="finAccountId" value="${bankAccountDetailEntry.finAccountId?if_exists}"/></td>
              <td>${bankAccountDetailEntry.finAccountName?if_exists}<input type="hidden" name="finAccountName" id="finAccountName" value="${bankAccountDetailEntry.finAccountName?if_exists}"/></td>
              <td>${(bankAccountDetailEntry.finAccountCode)?if_exists}<input type="hidden" name="finAccountCode" id="finAccountCode" value="${bankAccountDetailEntry.finAccountCode?if_exists}"/></td>
			  <td align="center"><input class='h3' text-align="center" type='text' id='isOperative' name='isOperative' size='1'  value="${bankAccountDetailEntry.isOperative?if_exists}"/><input type="hidden" name="isOperative" id="isOperative" value="${bankAccountDetailEntry.isOperative?if_exists}"/></td>
			  <td><@ofbizCurrency amount=bankAccountDetailEntry.balance?if_exists/><input type="hidden" name="balance" id="balance" value="${bankAccountDetailEntry.balance?if_exists}"/></td>
			  <td><input class='h3'  type='text' id='balanceConfirmation' name='balanceConfirmation' size='10'/><input type="hidden" name="pickRecord" id="pickRecord" value="Y"/> </td>
              <td><#if realisationDate?has_content>${realisationDate?if_exists}<#else><input class='h3'  type='text' id='realisationDate' name='realisationDate' size='10'/></#if><input type="hidden" name="realisationDate" id="realisationDate" value="${bankAccountDetailEntry.realisationDate?if_exists}"/></td>
              <td><input class='h3'  type='text' id='remarks' name='remarks' size='10'/></td>
            </tr>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
            <#assign index = index+1>
        </#list>
        <tr>
        	<td></td>
        	<td><input type="hidden" name="asOnDate" id="asOnDate" value="${parameters.asOnDate?if_exists}"/></td>
        	<td> <input type="hidden" name="printBankAccountDetails" id="printBankAccountDetails" value="Y"/> <input type="submit" class="smallSubmit" id="viewPrint" value="Print" onclick="javascript:setContent();"/></td>
        	<td><div class="loader" id="loader" ></div></td>
        	<td></td>
        	<td></td>
        	<td></td>
        	<td></td>
        </tr>	
      </tbody>
    </table>
  </form>
<#else>
  <h3>No Records Found</h3>
</#if>

<script>
$(document).ready(function(){
	$('#loader').hide();
	
	$('#viewPrint').click(function() {
	    $('#loader').show();
	 });


});


</script>