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
	
	
	function toggleQuoteId(master) {
        var quotes = jQuery("#listQuote :checkbox[name='quoteCheckBoxId']");
        jQuery.each(quotes, function() {
            this.checked = master.checked;
        });
    }
    
    function processQuotes(current){
    	jQuery(current).attr( "disabled", "disabled");
    	var actionFlag = $(current).val();
    	var comments =$(description).val();
    	
    	var quotes = jQuery("#listQuote :checkbox[name='quoteCheckBoxId']");
        var index = 0;
        
        jQuery.each(quotes, function() {
            if (jQuery(this).is(':checked')) {
            	var domObj = $(this).parent().parent();
            	var quoteObj = $(domObj).find("[name='quoteCheckBoxId']");
            	var quoteId = $(quoteObj).val();
            	var appendStr = "<input type=hidden name=quoteId_o_"+index+" value="+quoteId+" />";
                   appendStr += "<input type=hidden name=comments value="+comments+" />";
            	
            	$("#updateQuoteStatusForm").append(appendStr);
            	index = index+1;
            }
            
        });
        var appStr = "";
        if(actionFlag && actionFlag == "Accept"){
        	appStr += "<input type=hidden name=quoteStatusId value=QUO_ACCEPTED />";
    		appStr += "<input type=hidden name=quoteItemStatusId value=QTITM_ACCEPTED />";
        	$("#updateQuoteStatusForm").append(appStr);
        }
        else{
        	appStr += "<input type=hidden name=quoteStatusId value=QUO_REJECTED />";
    		appStr += "<input type=hidden name=quoteItemStatusId value=QTITM_REJECTED />";
    	
    		
        	$("#updateQuoteStatusForm").append(appStr);
        }
    	var strApp = "<input type=hidden name=custRequestId value='${custRequestId}' />";
    	$("#updateQuoteStatusForm").append(strApp);
        jQuery('#updateQuoteStatusForm').submit();
    }
       
//]]>
</script>

<form name="updateQuoteStatusForm" id="updateQuoteStatusForm" method="post" action="updateQuotesStatusOfEnquiry" ></form>

<#if quotesList?has_content>
  
  <form name="listQuote" id="listQuote"  method="post" >
    <div align="right" width="100%">
    	<table width="25%">
    		<tr>
    			<td></td>
    			<td></td>
    			<td align="right"><h2><input id="submitButton" type="button"  onclick="javascript:processQuotes(this);" value="Accept"/></h2></td>
    			<td align="center"><h2><input id="submitButton" type="button"  onclick="javascript:processQuotes(this);" value="Reject"/></h2></td>
    		</tr>
    	</table>
    </div>
    <table class="basic-table hover-bar" cellspacing="0">
      <thead>
        <tr class="header-row-2">
          <td>Quote No</td>
          <td>Supplier Name - [Code]</td>
          <td>Quote Issue Date</td>
          <td>Quote Valid From</td>
          <td>Valid Thru</td>
          <td>Comments</td>
		  <td align="right" cell-padding>${uiLabelMap.CommonSelect} <input type="checkbox" id="checkAllQuotes" name="checkAllQuotes" onchange="javascript:toggleQuoteId(this);"/></td>
        </tr>
      </thead>
      <tbody>
      <#assign alt_row = false>
      <#assign index = 0>
      <#list quotesList as eachQuote>
      		<tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
            	<td><h2><a class="buttontext" href="<@ofbizUrl>ViewMaterialQuote?quoteId=${eachQuote.quoteId?if_exists}</@ofbizUrl>" target="_blank"/>${eachQuote.quoteId?if_exists}</h2></td>
              	<#assign partyName = (delegator.findOne("PartyNameView", {"partyId" : eachQuote.partyId}, true))!>
              	<td>${partyName.groupName?if_exists}${partyName.firstName?if_exists} ${partyName.lastName?if_exists} - [${eachQuote.partyId}]</td>
              	<td>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(eachQuote.issueDate, "dd/MM/yyyy")}</td>
              	<td>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(eachQuote.validFromDate, "dd/MM/yyyy")}</td>
              	<#if eachQuote.validThruDate?has_content>
              		<td>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(eachQuote.validThruDate, "dd/MM/yyyy")}</td>
              	<#else>
              		<td></td>
              	</#if>
              	 <td> <input type="text"  id="description"  name="description"/> </td>
           		<td><input type="checkbox" id="quoteCheckBoxId_${eachQuote_index}" name="quoteCheckBoxId" value="${eachQuote.quoteId?if_exists}"/></td>
           <td><input type="hidden" name="description" value="${description?if_exists}"/></td>
            </tr>
            <#assign index = index+1>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        </#list>
      </tbody>
    </table>
  </form>
<#else>
  <h3>No Quote Found To Accept</h3>
</#if>
