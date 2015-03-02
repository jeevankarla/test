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
	
	
/*
	function toggleQuoteId(master) {
        var quotes = jQuery("#listQuote :checkbox[name='quoteCheckBoxId']");
        jQuery.each(quotes, function() {
            this.checked = master.checked;
        });
    }
*/  
   function processQuotes(current){
    	jQuery(current).attr( "disabled", "disabled");
    	var index = 0;
    	var quotes = jQuery("#listQuote :checkbox[name='quoteCheckBoxId']");
    	var appendStr = "<table id=parameters>";
        jQuery.each(quotes, function() {
            if (jQuery(this).is(':checked')) {
            	var domObj = $(this).parent().parent();
            	var quoteIdObj = $(domObj).find("#quoteId");
            	var quoteItemSeqIdObj = $(domObj).find("#quoteItemSeqId");
            	var custRequestIdObj = $(domObj).find("#custRequestId");
            	var statusIdObj = $(domObj).find("#statusId");
            	var commentsObj = $(domObj).find("#comments");
            	
            	var quoteId = $(quoteIdObj).val();
            	var quoteItemSeqId = $(quoteItemSeqIdObj).val();
            	var custRequestId = $(custRequestIdObj).val();
            	var statusId = $(statusIdObj).val();
            	var comments = $(commentsObj).val();
            	
            	appendStr += "<tr><td><input type=hidden name=quoteId_o_"+index+" id=quoteId_o_"+index+" value="+quoteId+" />";
            	appendStr += "<input type=hidden name=quoteItemSeqId_o_"+index+" id=quoteItemSeqId_o_"+index+" value="+quoteItemSeqId+" />";
            	appendStr += "<input type=hidden name=custRequestId_o_"+index+" id=custRequestId_o_"+index+" value="+custRequestId+" />";
            	appendStr += "<input type=hidden name=statusId_o_"+index+" id=statusId_o_"+index+" value="+statusId+" /></td></tr>";
            }
            index = index+1;
        });
        appendStr += "</table>";
        $("#updateQuoteStatusForm").append(appendStr);
     jQuery('#updateQuoteStatusForm').submit();
        
    }
    
       function processQuotesReject(current){
    	jQuery(current).attr( "disabled", "disabled");
    	var index = 0;
    	var quotes = jQuery("#listQuote :checkbox[name='quoteCheckBoxId']");
    	var appendStr = "<table id=parameters>";
        jQuery.each(quotes, function() {
            if (jQuery(this).is(':checked')) {
            	var domObj = $(this).parent().parent();
            	var quoteIdObj = $(domObj).find("#quoteId");
            	var quoteItemSeqIdObj = $(domObj).find("#quoteItemSeqId");
            	var custRequestIdObj = $(domObj).find("#custRequestId");
            	var commentsObj = $(domObj).find("#comments");
            	
            	var quoteId = $(quoteIdObj).val();
            	var quoteItemSeqId = $(quoteItemSeqIdObj).val();
            	var custRequestId = $(custRequestIdObj).val();
            	var statusId = "QTITM_REJECTED";
            	var comments = $(commentsObj).val();
            	
            	appendStr += "<tr><td><input type=hidden name=quoteId_o_"+index+" id=quoteId_o_"+index+" value="+quoteId+" />";
            	appendStr += "<input type=hidden name=quoteItemSeqId_o_"+index+" id=quoteItemSeqId_o_"+index+" value="+quoteItemSeqId+" />";
            	appendStr += "<input type=hidden name=custRequestId_o_"+index+" id=custRequestId_o_"+index+" value="+custRequestId+" />";
            	appendStr += "<input type=hidden name=statusId_o_"+index+" id=statusId_o_"+index+" value="+statusId+" /></td></tr>";
            }
            index = index+1;
        });
        appendStr += "</table>";
        $("#updateQuoteStatusForm").append(appendStr);
     jQuery('#updateQuoteStatusForm').submit();
        
    }
       
//]]>
</script>

<form name="updateQuoteStatusForm" id="updateQuoteStatusForm" method="post" action="updateQuotesForEvaluation" ></form>

  
  <form name="listQuote" id="listQuote"  method="post" >
  
    <div align="right" width="100%">
    <#assign flag = "false">
    <#list quotesList as eachQuote>
    <#if (eachQuote.qiStatusId).equals("QTITM_ACCEPTED") || (eachQuote.qiStatusId).equals("QTITM_TECH_EVAL")>
    <#assign flag = "true">
    </#if>
    </#list>
    	<table width="25%">
    		<tr>
    			<td></td>
    			<td></td>
    			<#if flag == "true">
    			<td align="right"><h2><input id="submitButton" type="button"  onclick="javascript:processQuotes(this);" value="Accept"/></h2></td>
    			<td align="center"><h2><input id="submitButton" type="button"  onclick="javascript:processQuotesReject(this);" value="Reject"/></h2></td>
    			</#if>
    		</tr>
    	</table>
    </div>
    <table class="basic-table hover-bar" cellspacing="0">
      <thead>
        <tr class="header-row-2">
          <td>Quote No</td>
          <td>Item No</td>
          <td>Supplier</td>
          <td>Material Name-[Code][UOM]</td>
          <td>Quantity</td>
          <td>Quote Price</td>
          <td>Status</td>
          <td>Comments</td>
          <td>Tech.Eval</td>
          <td>Fin.Eval</td>
          <td>Neg.UnitPrice</td>
          <td>Negotiation</td>
        </tr>
      </thead>
      <tbody>
      <#assign alt_row = true>
      <#list quotesList as eachQuote>
      <tr valign="middle"<#if alt_row> class="alternate-row"</#if> <#if (eachQuote.qiStatusId).equals("QTITM_FIN_EVAL")> style="background-color: rgb(255, 128, 0);" </#if> <#if (eachQuote.qiStatusId).equals("QTITM_TECH_EVAL")> style="background-color: rgb(255, 128, 128);" </#if> <#if (eachQuote.qiStatusId).equals("QTITM_ACCEPTED")> style="background-color: #e5eecc" </#if>>
         	<td><h2><a class="buttontext" href="<@ofbizUrl>ViewMaterialQuote?quoteId=${eachQuote.quoteId?if_exists}</@ofbizUrl>" target="_blank"/>${eachQuote.quoteId?if_exists}</h2></td>
          	<td>${eachQuote.quoteItemSeqId?if_exists}</td>
          <#assign partyName = (delegator.findOne("PartyNameView", {"partyId" : eachQuote.partyId}, true))!>
            <td>${partyName.firstName?if_exists} ${partyName.lastName?if_exists}${partyName.groupName?if_exists}</td>
          <#assign productName = (delegator.findOne("Product", {"productId" : eachQuote.productId}, true))!>
           <#assign uom = (delegator.findOne("Uom", {"uomId" : productName.quantityUomId}, true))!>
          <td>${productName.productName?if_exists} [${productName.productId?if_exists}][${uom.description?if_exists}]</td>
          <td>${eachQuote.quantity?if_exists}</td>
          <td>${eachQuote.quoteUnitPrice?if_exists}</td>
          <#assign statusEntry = (delegator.findOne("StatusItem", {"statusId" : eachQuote.qiStatusId}, true))!>
          <td>${statusEntry.description?if_exists}</td>
          <td><#if !(eachQuote.qiStatusId).equals("QTITM_NEGOTIATION")><input type="text"  id="comments"  name="comments"/></#if></td>
          <td><#if (eachQuote.qiStatusId).equals("QTITM_ACCEPTED")><input type="checkbox" id="quoteCheckBoxId_${eachQuote_index}" name="quoteCheckBoxId" value="${eachQuote?if_exists}"/></#if></td>
          <td><#if (eachQuote.qiStatusId).equals("QTITM_TECH_EVAL")><input type="checkbox" id="quoteCheckBoxId_${eachQuote_index}" name="quoteCheckBoxId" value="${eachQuote.quoteId?if_exists}"/></#if></td>
          <td><#if (eachQuote.qiStatusId).equals("QTITM_FIN_EVAL")><input type="text"  id="quoteUnitPrice"  name="quoteUnitPrice" value = "${eachQuote.quoteUnitPrice}"/></#if></td>
         <td><#if (eachQuote.qiStatusId).equals("QTITM_FIN_EVAL")><h2><a class="buttontext" href="<@ofbizUrl>updateQuotesForNegotiation?quoteId=${eachQuote.quoteId?if_exists}&amp;quoteItemSeqId=${eachQuote.quoteItemSeqId?if_exists}&amp;custRequestId=${eachQuote.custRequestId?if_exists}&amp;quoteUnitPrice=${eachQuote.quoteUnitPrice?if_exists}</@ofbizUrl>"/>Negotiation</h2></#if></td>
         
         <#if (eachQuote.qiStatusId).equals("QTITM_ACCEPTED")>
          <input type = "hidden" name = "quoteId" id = "quoteId" value = "${eachQuote.quoteId}">
          <input type = "hidden" name = "quoteItemSeqId" id = "quoteItemSeqId" value = "${eachQuote.quoteItemSeqId}">
          <input type = "hidden" name = "custRequestId" id = "custRequestId" value = "${eachQuote.custRequestId}">
          <input type = "hidden" name = "statusId" id = "statusId" value = "QTITM_TECH_EVAL">
         </#if>
         <#if (eachQuote.qiStatusId).equals("QTITM_TECH_EVAL")>
          <input type = "hidden" name = "quoteId" id = "quoteId" value = "${eachQuote.quoteId}">
          <input type = "hidden" name = "quoteItemSeqId" id = "quoteItemSeqId" value = "${eachQuote.quoteItemSeqId}">
          <input type = "hidden" name = "custRequestId" id = "custRequestId" value = "${eachQuote.custRequestId}">
          <input type = "hidden" name = "statusId" id = "statusId" value = "QTITM_FIN_EVAL">
         </#if>
        </tr>
     </#list>
      </tbody>
    </table>
  </form>
