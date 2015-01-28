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
	var supplierJson = ${StringUtil.wrapString(supplierJSON)!'[]'};
    function toggleSelectId(master) {
        var enquiryItems = jQuery("#listEnquiryItems :checkbox[name='check']");
		jQuery.each(enquiryItems, function() {
            this.checked = master.checked;
        });
        itemSubmit(master);        
    }
          
    function massItemsSubmit(current){
    	//jQuery(current).attr( "disabled", "disabled");    	
    	//jQuery('#listTenderItems').submit();
    }
    
    $(document).ready(function(){
    	var partyId = $("#partyId").val();
        $("#partyId").autocomplete({ source: supplierJson }).keydown(function(e){});        		
	    if( (partyId).length < 1 ) {
	    	$('#partyId').css('background', 'yellow'); 
	       	setTimeout(function () {
	           	$('#partyId').css('background', 'white').focus(); 
	       	}, 800);
	    	return false;
    	}
    });
    
    
   function itemsSubmit(current){
   		
        var ischecked=false;
        
        var index = 0;
        var partyId = $("[name='partyId']").val();
        var issueDate = $("#issueDate").val();
        var validFromDate = $("#validFromDate").val();
        var validThruDate = $("#validThruDate").val();
        var quoteName = $("#quoteName").val();
        var quoteType = "MATERIAL_PUR_QUOTE";
        var enquires = jQuery("#listEnquiryItems :checkbox[name='check']");
        var quoteId = $("#quoteId").val();
        
        jQuery.each(enquires, function() {
            if (jQuery(this).is(':checked')) {
            	var domObj = $(this).parent().parent();
            	
            	var priceObj = $(domObj).find("[name='price']");
            	var price = $(priceObj).val();
            	if(price && !isNaN(price) && price >0){
            		var appendPriceStr = "<input type=hidden name=price_o_"+index+" value="+price+" />";
	                $("#processEnquiryItems").append(appendPriceStr);
	                
	            	var requestObj = $(domObj).find("[name='custRequestId']");
	            	var custRequestId = $(requestObj).val();
	            	var appendStr = "<input type=hidden name=custRequestId_o_"+index+" value="+custRequestId+" />";
	                $("#processEnquiryItems").append(appendStr);
	                
	                var requestItemObj = $(domObj).find("[name='custRequestItemSeqId']");
	            	var custRequestItemSeqId = $(requestItemObj).val();
	            	var appendItemStr = "<input type=hidden name=custRequestItemSeqId_o_"+index+" value="+custRequestItemSeqId+" />";
	                $("#processEnquiryItems").append(appendItemStr);
	                
	                var productObj = $(domObj).find("[name='productId']");
	            	var productId = $(productObj).val();
	            	var appendProdStr = "<input type=hidden name=productId_o_"+index+" value="+productId+" />";
	                $("#processEnquiryItems").append(appendProdStr);
	                
	                var qtyObj = $(domObj).find("[name='quantity']");
	            	var qty = $(qtyObj).val();
	            	var appendQtyStr = "<input type=hidden name=quantity_o_"+index+" value="+qty+" />";
	                $("#processEnquiryItems").append(appendQtyStr);
	                
	                var quoteItemSeqIdObj = $(domObj).find("[name='quoteItemSeqId']");
	            	var quoteItemSeqId = $(quoteItemSeqIdObj).val();
	            	var appendquoteItemSeqIdObjStr = "<input type=hidden name=quoteItemSeqId_o_"+index+" value="+quoteItemSeqId+" />";
	                $("#processEnquiryItems").append(appendquoteItemSeqIdObjStr);
	               	
	               	ischecked = true; 
	                index = index+1;
            	}
            }
            
        });
        var appStr = "";
   		appStr += "<input type=hidden name=partyId value='"+ partyId +"' />";
    	appStr += "<input type=hidden name=validFromDate value='"+ validFromDate +"' />";
    	appStr += "<input type=hidden name=validThruDate value='"+ validThruDate +"' />";
    	appStr += "<input type=hidden name=quoteName value='"+ quoteName +"' />";
    	appStr += "<input type=hidden name=quoteType value='"+ quoteType +"' />";
    	appStr += "<input type=hidden name=issueDate value='"+ issueDate +"' />";
    	
    	if(quoteId && !isNaN(quoteId)){
    		appStr += "<input type=hidden name=quoteId value='"+ quoteId +"' />";
    	}
    	$("#processEnquiryItems").append(appStr);
           
        if(ischecked==true){
            jQuery('#processEnquiryItems').submit();
        }
    }
    
    function datepick()
	{		
		$( "#issueDate" ).datepicker({
			dateFormat:'dd MM, yy',
			changeMonth: true,			
			
			numberOfMonths: 1});
		$('#ui-datepicker-div').css('clip', 'auto');
		
		$( "#validFromDate" ).datepicker({
			dateFormat:'dd MM, yy',
			changeMonth: true,			
			
						
			numberOfMonths: 1});
		$('#ui-datepicker-div').css('clip', 'auto');
		
		$( "#validThruDate" ).datepicker({
			dateFormat:'dd MM, yy',
			
			changeMonth: true,			
			numberOfMonths: 1});
		$('#ui-datepicker-div').css('clip', 'auto');
		
	}
	
	function getName(){
		var partyId=$("#partyId").val();
	    var custRequestId = $("#custRequestId").val();
	    var data= "partyId="+partyId+"&custRequestId="+custRequestId;
		$.ajax({
	        type: "POST",
	        url: "getSupplierName",
	        data: data,
	        dataType: 'json',
	        success: function(result) {
	        partyName=result["partyName"];
	        jQuery("#partyName").html(partyName);
	       	 },
	       error: function() {
	       	 	alert(result["_ERROR_MESSAGE_"]);
	       	 }
		});
	}
//]]>
</script>
<#if flag?exists && flag=="Y">
	<form name="processEnquiryItems" id="processEnquiryItems"  method="post" action="<@ofbizUrl>updateQuoteForEnquiry</@ofbizUrl>">
<#else>
	<form name="processEnquiryItems" id="processEnquiryItems"  method="post" action="<@ofbizUrl>createQuoteForEnquiry</@ofbizUrl>">
</#if>
</form>
<#if itemList?has_content>
	
  	<form name="listEnquiryItems" id="listEnquiryItems"  method="post" action="<@ofbizUrl>createQuoteForEnquiry</@ofbizUrl>">
  		<table width="50%">
  			<#if quoteId?exists && quoteId?has_content>
  			<tr><td><span class="label h3"> Quote Id :</span></td><td><input class="h3" type="text" name="quoteId" id="quoteId" value="${quoteId}" readonly /></td></tr>
  			</#if>
    		<tr>
    		<#if partyId?exists && partyId?has_content>
    			<td><span class="label h3"> Supplier (<font color='red'>*</font>):</span></td><td><input class="h3" type="text" name="partyId" id="partyId" value="${partyId}"  maxlength="60" /></td>
    		<#else>	
  				<td><span class="label h3"> Supplier (<font color='red'>*</font>):</span></td><td><input class="h3" type="text" name="partyId" id="partyId"  maxlength="60"  /><span style="border: 1px solid #888;background-color:yellow;" id="partyName"></span></td>
  			</#if>	
    		</tr>
    		<tr>
    		<#if issueDate?exists && issueDate?has_content>
    			<td><span class="label h3">Quote Received Date :</span></td><td><input class='h3' type='text' id='issueDate' name='issueDate' value="${issueDate}" onmouseover='datepick()'/></td>
    		<#else>	
    			<td><span class="label h3">Quote Received Date :</span></td><td><input class='h3' type='text' id='issueDate' name='issueDate' onmouseover='datepick()' onclick="getName()" ></td>
    		</#if>	
    		</tr>
    		<tr>
    		<#if quoteName?exists && quoteName?has_content>
    			<td><span class="label h3">Quote Ref No :</span></td><td><input class='h3' type='text' id='quoteName' name='quoteName' value="${quoteName}" /></td>
    		<#else>
    			<td><span class="label h3">Quote Ref No :</span></td><td><input class='h3' type='text' id='quoteName' name='quoteName'/></td>
    		</#if>
    			
    		</tr>
    		<tr>
    		<#if validFromDate?exists && validFromDate?has_content>
    			<td><span class="label h3">Valid From Date :</span></td><td><input class='h3' type='text' id='validFromDate' name='validFromDate' value="${validFromDate}" onmouseover='datepick()'/></td>
    		<#else>
    			<td><span class="label h3">Valid From Date :</span></td><td><input class='h3' type='text' id='validFromDate' name='validFromDate' onmouseover='datepick()'/></td>
    		</#if>	
    		</tr>
    		<tr>
    		<#if validThruDate?exists && validThruDate?has_content>
    			<td><span class="label h3">Valid Thru Date :</span></td><td><input class='h3' type='text' id='validThruDate' name='validThruDate' value="${validThruDate}" onmouseover='datepick()'/></td>
    		<#else>
    			<td><span class="label h3">Valid Thru Date :</span></td><td><input class='h3' type='text' id='validThruDate' name='validThruDate' onmouseover='datepick()'/></td>
    		</#if>	
    		</tr>
    		
    	</table>
    	<#if flag?exists && flag=="Y">
	    	<div align="center">     
	    		<h3><input id="submitButton" type="button" onclick="javascript:itemsSubmit(this);" value="Update Quote" /></h3>
	    	</div>
    	<#else>
	    	<div align="center">     
	    		<h3><input id="submitButton" type="button" onclick="javascript:itemsSubmit(this);" value="Create Quote" /></h3>
	    	</div>
    	</#if>
		<br />
		
    	<table class="basic-table hover-bar" cellspacing="0">
      		<thead>
        		<tr class="header-row-2">
          			<td>Item</td>
          			<td>Quantity</td>
          			<td>Supply Qty</td>
          			<td>Price per piece</td>
          			<td align="right" width="6%">Select All <input type="checkbox" id="checkAllItems" name="checkAllItems" onchange="javascript:toggleSelectId(this);"/></td>
        		</tr>
      		</thead>
      <tbody>
        <#assign alt_row = false>
        <#list itemList as items>          
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
            <#if items.custRequestId?exists && items.custRequestId?has_content>
              <input type="hidden" name="custRequestId" id="custRequestId" value="${items.custRequestId}">
              </#if>
              <#if items.custRequestItemSeqId?exists && items.custRequestItemSeqId?has_content>
              <input type="hidden" name="custRequestItemSeqId" id="custRequestItemSeqId" value="${items.custRequestItemSeqId}">
              </#if>
              <#if items.productId?exists && items.productId?has_content>
              <input type="hidden" name="productId" id="productId" value="${items.productId}">
              </#if>
              <#if items.quoteItemSeqId?exists && items.quoteItemSeqId?has_content>
              <input type="hidden" name="quoteItemSeqId" id="quoteItemSeqId" value="${items.quoteItemSeqId}">
              </#if>
              <td><input type="button" name="productName" id="productName" value="${items.productName}(${items.productId})" style="border:0;background-color:transparent;color:#3B5998;"/></td>
              <td><input type="button" id="enqQty" name="enqQty" style="background-color:transparent;border:0;color:#3B5998;" value="${items.quantity}"/ ></td>
              <td><input type="text" id="quantity" name="quantity" value="${items.quantity}"/></td>
              <#if items.unitPrice?exists && items.unitPrice?has_content><td><input type="text" id="price" name="price" value=${items.unitPrice}></td>
              <#else><td><input type="text" id="price" name="price"/></td>
              </#if>
              <td><input type="checkbox" id="check_${items_index}" name="check" value="${items.custRequestId}::${items.custRequestItemSeqId}"/></td>
            </tr>
            <#assign alt_row = !alt_row>
        </#list>
      </tbody>
    </table>
  </form>
</#if>
