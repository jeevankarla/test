<#include "CreateMPOInc.ftl"/>
<#--
<link href="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.css</@ofbizContentUrl>" rel="stylesheet">
<script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.js</@ofbizContentUrl>"></script>
-->
<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/flexselect.css</@ofbizContentUrl>">
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/liquidmetal.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/jquery.flexselect.js</@ofbizContentUrl>"></script>
<script type="application/javascript">

		
		
function makeDatePicker(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat:'dd MM, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
			}
		});
	$( "#"+thruDateId ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				//$( "#"+fromDateId ).datepicker( "option", "maxDate", selectedDate );
			}
		});
	}
	function makeDayDatePicker(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat:'d MM, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
			}
		});
	$( "#"+thruDateId ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				//$( "#"+fromDateId ).datepicker( "option", "maxDate", selectedDate );
			}
		});
	}
	function validateEmail(email) { 
    	var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    	return re.test(email);
	} 
	
	
    $(document).ready(function(){
            $("#wizard-2").steps({
                headerTag: "h3",
                bodyTag: "section",
                transitionEffect: "slideLeft",
                onStepChanging: function (event, currentIndex, newIndex)
                {	
                		if(currentIndex==1 && newIndex==0){
                			newGrid();
                		}
                	if(currentIndex == 0 && newIndex == 1){
                		var supplierId = $("#supplierId").val();
                		
                	    if( (supplierId).length < 1 ) {
					    	$('#supplierId').css('background', 'yellow'); 
					       	setTimeout(function () {
					           	$('#supplierId').css('background', 'white').focus(); 
					       	}, 800);
					    	return false;
				    	}
					    //  alert("==supplierId="+supplierId);
					    //populate SlickGrid starts here
						 if(supplierId){
						    gridShowCall();
						 	setupGrid1();
						 	addToInvoiceAmount();
					     }else{ 
					        gridHideCall();
					     }
				        jQuery(".grid-header .ui-icon")
				            .addClass("ui-state-default ui-corner-all")
				            .mouseover(function(e) {
				                jQuery(e.target).addClass("ui-state-hover")
				            })
				            .mouseout(function(e) {
				                jQuery(e.target).removeClass("ui-state-hover")
				            });		
						jQuery("#gridContainer").resizable();	   			
				    	var tabindex = 1;
				    	jQuery('input,select').each(function() {
				        	if (this.type != "hidden") {
				            	var $input = $(this);
				            	$input.attr("tabindex", tabindex);
				            	tabindex++;
				        	}
				    	});
				
				    	var rowCount = jQuery('#myGrid1 .slick-row').length;
						if (rowCount > 0) {			
							$(mainGrid.getCellNode(rowCount-1, 0)).click();		   
				    	}
    	 		      //populate SlickGrid ends here
                		return true;
                		
                	}
                	if(currentIndex == 1 && newIndex == 2){
                	     

	                	 /* if( (address1).length < 1 ) {
						    	$('#address1').css('background', 'yellow'); 
						       	setTimeout(function () {
						           	$('#address1').css('background', 'white').focus(); 
						       	}, 800);
						    	return false;
					      }*/
	                	  
					    	
						    
                		return true;
                		
                	}
                	if(currentIndex == 2 && newIndex == 3){
                	
                		return true;
                		
                	}
                	
                	if(currentIndex == 3 && newIndex == 4){
                		
                		return true;
                		
                		
                	}
                	return true;
                },
                onFinishing: function (event, currentIndex)
                {	
                    return true;
                },
                onFinished: function (event, currentIndex)
                {
                   var employee = {
					    "firstName": "hai",
					    "lastName": "babu" 
					}
					var form = ($(this)).parent();
					var OrderDetails = jQuery("<input>").attr("type", "hidden").attr("name", "OrderDetails").val(employee);	
				    jQuery(form).append(jQuery(OrderDetails));
				    processPOEntryInternal("CreateMPO", "CreateMaterialPO");
                	form.submit();
                }
            });
	}); 
	
	$(document).ready(function(){
		makeDatePicker("estimatedDeliveryDate","fromDateId");
		makeDatePicker("orderDate","fromDateId");
		makeDatePicker("refDate","fromDateId");
		makeDatePicker("quoteDate","fromDateId");
		
		makeDayDatePicker("effectiveDate","fromDateId");
		makeDayDatePicker("SInvoiceDate","fromDateId");
		
		makeDatePicker("fromDate","fromDateId");
        makeDatePicker("thruDate","fromDateId");
	
		//$(this.target).find('input').autocomplete();
		
		$("#supplierId").autocomplete({ source: partyAutoJson }).keydown(function(e){
			
		});	
		$( "input[name*='paymentTermTypeId']" ).autocomplete({ source: paymentTermsJSON });
		$( "input[name*='deliveryTermTypeId']" ).autocomplete({ source: deliveryTermsJSON });
		$( "input[name*='otherTermTypeId']" ).autocomplete({ source: otherTermsJSON });
		$('#ui-datepicker-div').css('clip', 'auto');
		hideExtPO();	
		getPayTermDes();
		getDelTermDes();
		getOtherTermDes();
	});
	function hideExtPO(){
		var orderTypeId = $("#orderTypeId").val();
		var formObj = jQuery("#CreateMPO");
		var poNumberObj  = $(formObj).find("#PONumber");
	    var quoteNumObj  = $(formObj).find("#quoteNum");
        var quoteDateObj  = $(formObj).find("#quoteDate");
        var billToPartyIdObj  = $(formObj).find("#0_lookupId_billToPartyId");

		if(orderTypeId && orderTypeId == "PURCHASE_ORDER"){
			$(poNumberObj).parent().parent().show();
			$(quoteNumObj).parent().parent().show();
			$(quoteDateObj).parent().parent().show();
			$(billToPartyIdObj).parent().parent().parent().show();
		}
		else{
			$(poNumberObj).parent().parent().hide();
			$(quoteNumObj).parent().parent().hide();
			$(quoteDateObj).parent().parent().hide();
			$(billToPartyIdObj).parent().parent().parent().hide();	
		}
	}    
	
	
	var supplierName;
	function dispSuppName(selection){
	   value = $("#supplierId").val();
	   supplierName = partyNameObj[value];
	   $("#supplierName").html("<h4>"+supplierName+"</h4>");
	}    
	    
	 var payTermDes;
	 var delTermDes;
	 var otherTermDes;
	 var paymentTerms = ${StringUtil.wrapString(paymentTermsJSON!'[]')};
	 var deliveryTerms = ${StringUtil.wrapString(deliveryTermsJSON!'[]')};
	 var otherTerms = ${StringUtil.wrapString(otherTermsJSON!'[]')};
	 function getPayTermDes(){
		  $.each(paymentTerms, function(key, val){
		    $("#payTermDes").append('<option value="' + val.value + '">' + val.label + " [" + val.value + "]" + '</option>');
		  });
		  $("#payTermDes").flexselect({
		  								preSelection: false,
		  								allowClear: false,
		  								allowMismatch:false,
	  								    allowEmpty:true,
	  								    allowNewElements:false,
	  								    placeholder: "select",
	  								    hideDropdownOnEmptyInput: false});	
	
	}	 
	function getDelTermDes(){
		  $.each(deliveryTerms, function(key, val){
		    $("#delTermDes").append('<option value="' + val.value + '">' + val.label + " [" + val.value + "]" + '</option>');
		  });
		  $("#delTermDes").flexselect({
		  								preSelection: false,
		  								allowClear: false,
		  								allowMismatch:false,
	  								allowEmpty:true,
	  								allowNewElements:false,
	  								placeholder: "select",
	  								hideDropdownOnEmptyInput: false});	
	}  
	function getOtherTermDes(){
		  $.each(otherTerms, function(key, val){
		    $("#otherTermDes").append('<option value="' + val.value + '">' + val.label + " [" + val.value + "]" + '</option>');
		  });
		  $("#otherTermDes").flexselect({
		  								preSelection: false,
		  								allowClear: false,
		  								allowMismatch:false,
	  								allowEmpty:true,
	  								allowNewElements:false,
	  								placeholder: "select",
	  								hideDropdownOnEmptyInput: false});	
	}  
	    
</script>
	
	<#assign orderInfo = {}>
	<#assign quoteInfo = {}>
	<#assign orderAdjInfo = {}>
	<#assign orderTermInfo = {}>
	<#assign orderPayTermInfo = []>
	<#assign orderShipTermInfo = []>
	<#assign bedCheck = "">
	<#assign orderOtherTermInfo = []>
	<#assign quoteTerm = "">
	<#assign orderTypeId = "">
	<#if orderEditParam?has_content>
		<#assign orderInfo = orderEditParam.get("orderHeader")?if_exists>
		<#assign quoteInfo = orderEditParam.get("quoteDetails")?if_exists>
		<#if quoteInfo.quoteId?has_content>
		<#assign quoteTerm = delegator.findByAnd("QuoteTerm", {"quoteId" : quoteInfo.quoteId, "termTypeId" : "BED_PUR" })>
		<#if quoteTerm!= "undefined" && quoteTerm?has_content>
			<#assign bedCheck = quoteTerm[0].termTypeId?if_exists>
		</#if>
		</#if>
		<#assign orderAdjInfo = orderEditParam.get("orderAdjustment")?if_exists>
		<#assign orderTermInfo = orderEditParam.get("orderTerms")?if_exists>
		<#assign orderTypeId = orderInfo.orderTypeId>
		<#if orderTermInfo?has_content>
			<#assign orderPayTermInfo = orderTermInfo.get("paymentTerms")>
			<#assign orderShipTermInfo = orderTermInfo.get("deliveryTerms")>
			<#assign orderOtherTermInfo = orderTermInfo.get("otherTerms")>
		</#if>
	</#if>
	
	<form id="CreateMPO"  action="<@ofbizUrl>CreateMaterialPO</@ofbizUrl>" name="CreateMPO" method="post">
	    <div id="wizard-2">
        <h3>PO Information</h3>
        <section>
          <fieldset>
	            <table cellpadding="15" cellspacing="15" class='h3' width="50%">
	                   <tr>
							<td class="label">Order Type(<font color="red">*</font>) :</td>
						    <td>
							<select name="orderTypeId" id="orderTypeId" onchange="javascript: hideExtPO();">
						      	   <#list orderTypes as orderType>
						      	   		<#if orderId?exists && (orderTypeId == orderType.orderTypeId)>
						      	   			<option value='${orderType.orderTypeId}' selected>${orderType.description}</option> 
						      	   		<#else>
						      	   			<option value='${orderType.orderTypeId}'>${orderType.description}</option>
						      	   		</#if>   
  	    								
								    </#list> 
						      	</select>
						    </td>
						</tr>
    					<tr>
						    <td class="label">Vendor(<font color="red">*</font>) : </td>
						    <input type="hidden" name="orderId" id="orderId"  value="${orderId?if_exists}" />
						     <input type="hidden" name="productStoreId"  value="${productStoreId?if_exists}" />
						    <#if changeFlag?exists && changeFlag=='InterUnitPurchase'>
						       <input type="hidden" name="salesChannel" id="salesChannel" value="INTER_PRCHSE_CHANNEL"/> 
						    <#else>
						       <input type="hidden" name="salesChannel" id="salesChannel" value="MATERIAL_PUR_CHANNEL"/>  
						    </#if>
						    <td>
						    	<#if orderId?exists && orderInfo.get("supplierId")?exists>
						    		<input type="text" name="supplierId" id="supplierId" size="18" maxlength="60" value="${orderInfo.get("supplierId")}" readonly onblur= 'javascript:dispSuppName(this);'/>
						    		<span class="tooltip"> ${orderInfo.get("supplierName")?if_exists}</span>
						    	<#else>
						    		<input type="text" name="supplierId" id="supplierId" size="18" maxlength="60"  onblur= 'javascript:dispSuppName(this);'/>
						    		<span class="tooltip" id="supplierName"></span>
						    	</#if>
						      	
						    </td>
						</tr>
						<tr>
						    <td class="label"><b>PO Date : </b></td>
						    <td>
						    	<#if orderId?exists && orderInfo.get("orderDate")?exists>
						    		<input type="text" name="orderDate" id="orderDate" size="18" maxlength="60" autocomplete="off" value="${orderInfo.get("orderDate")?if_exists}"/>
						    	<#else>
							      	<input type="text" name="orderDate" id="orderDate" size="18" maxlength="60" autocomplete="off"/>
						      	</#if>
	        				 </td>
						</tr>
						<#assign purchaseTypeFlag = parameters.purchaseTypeFlag?if_exists>
						<#if purchaseTypeFlag?has_content && purchaseTypeFlag == "contractPurchase">
						<tr>
							<td class="label"><b>From Date : </b></td>
							<td>
								<#if orderId?exists && orderInfo.get("validFromDate")?exists>
						    		<input type="text" name="fromDate" id="fromDate" size="18" maxlength="60" autocomplete="off" value="${orderInfo.get("validFromDate")?if_exists}"/>
						    	<#else>
						    		<input type="text" name="fromDate" id="fromDate" size="18" maxlength="60" autocomplete="off"/>
						    	</#if>
							</td>
						</tr>
						<tr>
							<td class="label"><b>Thru Date : </b></td>
							<td>
								<#if orderId?exists && orderInfo.get("validThruDate")?exists>
						    		<input type="text" name="thruDate" id="thruDate" size="18" maxlength="60" autocomplete="off" value="${orderInfo.get("validThruDate")?if_exists}"/>
						    	<#else>
						    		<input type="text" name="thruDate" id="thruDate" size="18" maxlength="60" autocomplete="off"/>
						    	</#if>
							</td>
						</tr>
						</#if>
          		        <tr>
          		        	<td class="label"><b>Quote No: </b></td>
						    <td>
						    	<#if orderId?exists && quoteInfo.get("quoteId")?exists>
						    		<input type="text" name="quoteNum" id="quoteNum" size="18" maxlength="60" autocomplete="off" value="${quoteInfo.get("quoteId")?if_exists}"/>
						    	<#else>
							      	<input type="text" name="quoteNum" id="quoteNum" size="18" maxlength="60" autocomplete="off"/>
						      	</#if>
	        				 </td>
						</tr>
						<tr>
						    <td class="label"><b>Quote Date : </b></td>
						    <td>
						    	<#if orderId?exists && quoteInfo.get("quoteIssueDate")?exists>
						    		<input type="text" name="quoteDate" id="quoteDate" size="18" maxlength="60" autocomplete="off" value="${quoteInfo.get("quoteIssueDate")?if_exists}"/>
						    	<#else>
							      	<input type="text" name="quoteDate" id="quoteDate" size="18" maxlength="60" autocomplete="off"/>
						      	</#if>
	        				 </td>
						</tr>
          		        <tr>
						    <td class="label">Bill To Party :</td>
						    <td>
						    	<#if orderId?exists && orderInfo.get("billToPartyId")?exists>
						    	<#assign billToPartyId=orderInfo.get("billToPartyId")> 
						    	<@htmlTemplate.lookupField value="${billToPartyId?if_exists}" formName="CreateMPO" size="18" maxlength="60" name="billToPartyId"  id="billToPartyId" fieldFormName="LookupPartyName"/>
						    	<#else>
						      	<@htmlTemplate.lookupField  formName="CreateMPO" size="18" maxlength="60" name="billToPartyId" id="billToPartyId" fieldFormName="LookupPartyName"/>
          		                </#if>
          		                <span class="tooltip">If billing and vendor party are different, invoice will be raise against this Party </span>
						    </td>
						 </tr>
 						 <tr>
						    <td class="label"><b>Ref No. :</b></td>
						    <td>
						    	<#if orderId?exists && orderInfo.get("refNo")?exists>
						    		<input type="text" name="refNo" id="refNo" size="18" maxlength="60" autocomplete="off" value="${orderInfo.get("refNo")?if_exists}"/>
						    	<#else>
							      	<input type="text" name="refNo" id="refNo" size="18" maxlength="60" autocomplete="off"/>
						      	</#if>
						      
	        				 </td>
						</tr>
						<tr>
						    <td class="label"><b>Estimated Delivery Date :</b></td>
						    <td>
						    	<#if orderId?exists && orderInfo.get("estimatedDeliveryDate")?exists>
						    		<input type="text" name="estimatedDeliveryDate" id="estimatedDeliveryDate" size="18" maxlength="60" autocomplete="off" value="${orderInfo.get("estimatedDeliveryDate")?if_exists}"/>
						    	<#else>
						    		<input type="text" name="estimatedDeliveryDate" id="estimatedDeliveryDate" size="18" maxlength="60" autocomplete="off"/>
							   	</#if> 
	        				 </td>
						</tr>
						<!--<tr>
						    <td class="label"><b>Ref Date:* </b></td>
						    <td>
						      <input type="text" name="refDate" id="refDate" size="18" maxlength="60" autocomplete="off"/>
	        				 </td>
						</tr>-->
						<tr>
						    <td class="label"><b>ARC No / CPC No / Ext. PO :</b></td>
						    <td>
						    	<#if orderId?exists && orderInfo.get("PONumber")?exists>
						    		<input type="text" name="PONumber" id="PONumber" size="18" maxlength="60" autocomplete="off" value="${orderInfo.get("PONumber")?if_exists}"/>
						    	<#else>
						    		<input type="text" name="PONumber" id="PONumber" size="18" maxlength="60" autocomplete="off"/>
						    	</#if>
	        				 </td>
						</tr>
						<tr>
						    <td class="label"><b>File No : </b></td>
						    <td>
						    	<#if orderId?exists && orderInfo.get("fileNo")?exists>
						    		<input type="text" name="fileNo" id="fileNo" size="18" maxlength="60" autocomplete="off" value="${orderInfo.get("fileNo")?if_exists}"/>
						    	<#else>
						    		<input type="text" name="fileNo" id="fileNo" size="18" maxlength="60" autocomplete="off"/>
						    	</#if>
						      
	        				 </td>
						</tr>
				        <tr>
				            <td class="label"><b> Description :</b></td>
				            <td>
				            	<#if orderId?exists && orderInfo.get("orderName")?exists>
						    		<input type="text" name="orderName" id="orderName" size="18" maxlength="60" autocomplete="off" value="${orderInfo.get("orderName")?if_exists}"/>
						    	<#else>
						    		<input class="h3" type="textarea" size="30" maxlength="100" name="orderName" id="orderName" style="width: 200px; height: 50px" />
						    	</#if>
	        				   
	          				</td>
				        </tr>
	                  </table>
                    </fieldset>  
                 </section>
            <h3>Add Material </h3>
            <section>
             <div class="full">
				     <div class="screenlet">
					    	<div class="screenlet-body">
					    	 <div class="screenlet-body" id="FieldsDIV" >
								<table width="50%" border="0" cellspacing="0" cellpadding="0">
										
					 		       <tr><td><br/></td></tr>
					 		        <tr>
							        	<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Inc Tax: <input class='h3' type="checkbox" id="incTax" name="incTax" value="true" onClick="javascript: updateGridAmount();"/></div></td>
							         	
					 		         </tr>
					 		          <tr><td><br/></td></tr>
					        	</table>
							</div>
							<div class="grid-header" style="width:100%">
								<label>Purchase Entry </label><span id="totalAmount"></span>
							</div>
							<div id="myGrid1" style="width:100%;height:250px;"></div>
							
				             </div>  
			          </div>
			</div>        
               </section>
                      <h3>Payment Terms</h3>
			          <section>
				          <fieldset>
				            <table cellpadding="15" cellspacing="15" class='h2'>
								<tr>
				          			<td align='left' valign='middle' nowrap="nowrap"></td>
					                <td>
					               	
					               	<table border="0" cellspacing="10" cellpadding="10" id="paymentTermsTable" style="width:200px;" align="center">
						                <tr>
									       <td>
									       		<table class='h3'>
									       			<tr>
									           			<td><input type="button" id="addPaymentTerm" value="Add" style="padding: 6px;"/></td>
									        			<td> <input type="button" id="delPaymentTerm" value="Delete" style="padding: 6px;"/></td>
									        		</tr> 
									        	</table>
									        </td>
									        <td></td>
									        <td></td>
									    </tr>
									    <tr></tr>
									    <tr>
								        	<td align="center">Term Type</td>
								         	<#--<td align="center">Term Days</td>
								          	<td align="center">Term Value</td>
								          	<td align="center">UOM</td>-->
								          	<td align="center">Description</td>
								    	</tr>
								    	<#if orderId?exists && orderPayTermInfo?has_content>
								    		<#assign rowCount = 0>
								    		<#list orderPayTermInfo as eachPayTerm>
										    	<tr>
										    		<input type="hidden"  name="paymentTermTypeId_o_${rowCount}" value="${eachPayTerm.get("termTypeId")?if_exists}"/>
										        	<td>
										          		<input type="text"  name="paymentTermDesc" value="${eachPayTerm.get("termTypeDescription")?if_exists}" size="40"/>
										        	</td>
									            	<#--<td>
									                	<input type="text" name="paymentTermDays_o_${rowCount}" value="${eachPayTerm.get("termDays")?if_exists}" size="10"/>
									            	</td>
									            	<td>
									                	<input type="text" name="paymentTermValue_o_${rowCount}" value="${eachPayTerm.get("termValue")?if_exists}" size="10"/>
									            	</td>
									            	<td>
									            		<select name="paymentTermUom_o_${rowCount}">
									            			<#if eachPayTerm.get("uomId") == "INR">
									            				<option value="INR" selected>Rupees</option>
									            				<option value="PERCENT">Percent</option>
									            			<#else>
									            				<option value="INR">Rupees</option>
									            				<option value="PERCENT" selected>Percent</option>
									            			</#if>
									            			
									            		</select>
									            	</td>-->
									            	<td>
									                	<input type="textarea" cols="40" rows="5" maxlength="255" name="paymentTermDescription_o_${rowCount}" value="${eachPayTerm.get("description")?if_exists}" />
									            	</td>
										    	</tr>
										    	<#assign rowCount = rowCount+1>
									    	</#list> 
								    	<#else>
								    		<tr>
								        	<td>
								        		<select id='payTermDes' name="paymentTermTypeId_o_0" class='flexselect' ></select>
								        	</td>
							            	<#--<td>
							                	<input type="text" name="paymentTermDays_o_0" value=""  size="10"/>
							            	</td>
							            	<td>
							                	<input type="text" name="paymentTermValue_o_0" value="" size="10"/>
							            	</td>
							            	<td>
							            		<select name="paymentTermUom_o_0">
							            			<option value="INR">Rupees</option>
							            			<option value="PERCENT">Percent</option>
							            		</select>
							            	</td>-->
							            	<td>
							                	<input type="textarea" name="paymentTermDescription_o_0" value="" maxlength="255"/>
							            	</td>
								    	</tr>
								    	</#if>
									</table>
	          				     </td>
							  </tr>
						  </table>
						</fieldset>
                     </section>
                     <h3>Delivery Terms</h3>
			         <section>
				          <fieldset>
				            <table cellpadding="15" cellspacing="15" class='h2'>
							         <tr>
				          				<td align='left' valign='middle' nowrap="nowrap"></td>
					                 <td>
					               <table border="2" cellspacing="10" cellpadding="10" id="deliveryTermsTable" style="width:200px;" align="center">
					                <tr>
								       <td><table><tr>
								           <td><input type="button" id="addDeliveryTerm" value="Add" style="padding: 6px;"/>  </td>
								        	<td> <input type="button" id="delDeliveryTerm" value="Delete" style="padding: 6px;"/></td>
								        </tr> </table>
								          </td>
								         <td> </td>
								          <td></td>
								    </tr>
								    <tr>
							        	<td align="center">Term Type</td>
							         	<#--<td align="center">Term Days</td>
							          	<td align="center">Term Value</td>
							          	<td align="center">UOM</td>-->
							          	<td align="center">Description</td>
								    </tr>
								    <#if orderId?exists && orderShipTermInfo?has_content>
							    		<#assign rowCount = 0>
							    		<#list orderShipTermInfo as eachShipTerm>
									    	<tr>
									    		<input type="hidden"  name="deliveryTermTypeId_o_${rowCount}" value="${eachShipTerm.get("termTypeId")?if_exists}"/>
									        	<td>
									          		<input type="text"  name="deliveryTermDesc" value="${eachShipTerm.get("termTypeDescription")?if_exists}" size="40"/>
									        	</td>
								            	<#--<td>
								                	<input type="text" name="deliveryTermDays_o_${rowCount}" value="${eachShipTerm.get("termDays")?if_exists}" size="10"/>
								            	</td>
								            	<td>
								                	<input type="text" name="deliveryTermValue_o_${rowCount}" value="${eachShipTerm.get("termValue")?if_exists}" size="10"/>
								            	</td>
								            	<td>
								            		<select name="deliveryTermUom_o_${rowCount}">
								            			<#if eachShipTerm.get("uomId") == "INR">
								            				<option value="INR" selected>Rupees</option>
								            				<option value="PERCENT">Percent</option>
								            			<#else>
								            				<option value="INR">Rupees</option>
								            				<option value="PERCENT" selected>Percent</option>
								            			</#if>
								            			
								            		</select>
								            	</td>-->
								            	<td>
								                	<input type="textarea" name="deliveryTermDescription_o_${rowCount}" value="${eachShipTerm.get("description")?if_exists}" maxlength="255"/>
								            	</td>
									    	</tr>
									    	<#assign rowCount = rowCount+1>
								    	</#list>
								    <#else>
									    <tr>
									        <td>
								        		<select id='delTermDes' name="deliveryTermTypeId_o_0" class='flexselect' ></select>
									        </td>
								            <#--<td>
								                <input type="text" name="deliveryTermDays_o_0" value="" size="10"/>
								            </td>
								            <td>
								                <input type="text" name="deliveryTermValue_o_0" value="" size="10"/>
								            </td>
							            	<td>
							            		<select name="deliveryTermUom_o_0">
							            			<option value="INR">Rupees</option>
							            			<option value="PERCENT">Percent</option>
							            		</select>
							            	</td>-->
							            	<td>
							                	<input type="textarea" name="deliveryTermDescription_o_0" value="" maxlength="255"/>
							            	</td>
									    </tr>
									 </#if> 
								</table>
	          				     </td>
							        </tr>
						  </table>
						  </fieldset>
                        </section>
	                    <h3>Other Terms</h3>
	                     <section>
				          <fieldset>
				            <table cellpadding="15" cellspacing="15" class='h2'>
							         <tr>
				          				<td align='left' valign='middle' nowrap="nowrap"></td>
					                 <td>
					               <table border="2" cellspacing="10" cellpadding="10" id="otherTermsTable" style="width:200px;" align="center">
					                <tr>
								       <td><table><tr>
								           <td><input type="button" id="addOtherTerm" value="Add" style="padding: 6px;"/>  </td>
								        	<td> <input type="button" id="delOtherTerm" value="Delete" style="padding: 6px;"/></td>
								        </tr> </table>
								          </td>
								         <td> </td>
								          <td></td>
								    </tr>
								    <tr>
							        	<td align="center">Term Type</td>
							         	<td align="center">Term Days</td>
							          	<td align="center">Term Value</td>
							          	<td align="center">UOM</td>
							          	<td align="center">Description</td>
								    </tr>
								    <#if orderId?exists && orderOtherTermInfo?has_content>
							    		<#assign rowCount = 0>
							    		<#list orderOtherTermInfo as eachShipTerm>
									    	<tr>
									    		<input type="hidden"  name="otherTermTypeId_o_${rowCount}" value="${eachShipTerm.get("termTypeId")?if_exists}"/>
									        	<td>
									          		<input type="text"  name="otherTermDesc" value="${eachShipTerm.get("termTypeDescription")?if_exists}" size="40"/>
									        	</td>
								            	<td>
								                	<input type="text" name="otherTermDays_o_${rowCount}" value="${eachShipTerm.get("termDays")?if_exists}" size="10"/>
								            	</td>
								            	<td>
								                	<input type="text" name="otherTermValue_o_${rowCount}" value="${eachShipTerm.get("termValue")?if_exists}" size="10"/>
								            	</td>
								            	<td>
								            		<select name="otherTermUom_o_${rowCount}">
								            			<#if eachShipTerm.get("uomId") == "INR">
								            				<option value="INR" selected>Rupees</option>
								            				<option value="PERCENT">Percent</option>
								            			<#elseif eachShipTerm.get("uomId") == "PERCENT">
								            				<option value="INR">Rupees</option>
								            				<option value="PERCENT" selected>Percent</option>
								            			<#else>
								            				<option value="INR">Rupees</option>
								            				<option value="PERCENT">Percent</option>
								            			</#if>
								            			
								            		</select>
								            	</td>
								            	<td>
								                	<input type="textarea" name="otherTermDescription_o_${rowCount}" value="${eachShipTerm.get("description")?if_exists}" maxlength="255"/>
								            	</td>
									    	</tr>
									    	<#assign rowCount = rowCount+1>
								    	</#list>
								    <#else>
									    <tr>
									        <td>
								        		<select id='otherTermDes' name="otherTermTypeId_o_0" class='flexselect' ></select>
									        </td>
								            <td>
								                <input type="text" name="otherTermDays_o_0" value="" size="10"/>
								            </td>
								            <td>
								                <input type="text" name="otherTermValue_o_0" value="" size="10"/>
								            </td>
							            	<td>
							            		<select name="otherTermUom_o_0">
							            			<option value="INR">Rupees</option>
							            			<option value="PERCENT">Percent</option>
							            		</select>
							            	</td>
							            	<td>
							                	<input type="textarea" name="otherTermDescription_o_0" value="" maxlength="255"/>
							            	</td>
									    </tr>
									 </#if> 
								</table>
	          				     </td>
							        </tr>
						  </table>
						  </fieldset>
                        </section>
                </form>
                
 <script type="application/javascript">
 
 $(document).ready(function(){
    getPayTermDes();
    getDelTermDes();
    getOtherTermDes();
    $('#addPaymentTerm').click(function () {
        getPayTermDes();
	    var table = $("#paymentTermsTable");
	    var rows = document.getElementById("paymentTermsTable").getElementsByTagName("tr").length;
	    if (rows > 0) {
        var rowCount = rows-4;
        table.append('<tr><td> <select id ="payTermDes" name="paymentTermTypeId_o_'+rowCount+'" class = "flexselect" /></td><#--<td> <input type="text" size="10" name="paymentTermDays_o_'+rowCount+'" value="" /> </td><td> <input type="text" size="10" name="paymentTermValue_o_'+rowCount+'" value="" /> </td><td><select name="paymentTermUom_o_'+rowCount+'"><option value="INR">Rupees</option><option value="PERCENT">Percent</option></select></td>--><td><input type="textarea" name="paymentTermDescription_o_'+rowCount+'" value="" maxlength="255"/>	</td></tr>');
    }
  	$.each(paymentTerms, function(key, val){
    	$("select[name*='paymentTermTypeId']").append('<option value="' + val.value + '">' + val.label + " [" + val.value + "]" + '</option>');
  	});
    $("select[name*='paymentTermTypeId']").flexselect({
		  								preSelection: false,
		  								allowClear: false,
		  								allowMismatch:false,
	  								    allowEmpty:true,
	  								    allowNewElements:false,
	  								    placeholder: "select",
	  								    hideDropdownOnEmptyInput: false});	
});
	$('#delPaymentTerm').click(function () {
	     var table = $("#paymentTermsTable");
	    if (table.find('input:text').length > 1) {
	        table.find('input:text').last().closest('tr').remove();
	    }
	});

	
	 $('#addDeliveryTerm').click(function () {
	 getDelTermDes();
    var table = $("#deliveryTermsTable");
     var rows = document.getElementById("deliveryTermsTable").getElementsByTagName("tr").length;
    if (rows >0) {
        var rowCount = rows-3;
        table.append('<tr><td> <select id ="delTermDes" name="deliveryTermTypeId_o_'+rowCount+'" class = "flexselect" /></td><#--<td> <input type="text" size="10" name="deliveryTermDays_o_'+rowCount+'" value="" /> </td><td> <input type="text" size="10" name="deliveryTermValue_o_'+rowCount+'" value="" /> </td><td><select name="deliveryTermUom_o_'+rowCount+'"><option value="INR">Rupees</option><option value="PERCENT">Percent</option></select></td>--><td><input type="textarea" name="deliveryTermDescription_o_'+rowCount+'" value="" maxlength="255"/></td></tr>');
    }
    $.each(deliveryTerms, function(key, val){
    	$("select[name*='deliveryTermTypeId']").append('<option value="' + val.value + '">' + val.label + " [" + val.value + "]" + '</option>');
  	});
    $("select[name*='deliveryTermTypeId']").flexselect({
		  								preSelection: false,
		  								allowClear: false,
		  								allowMismatch:false,
	  								    allowEmpty:true,
	  								    allowNewElements:false,
	  								    placeholder: "select",
	  								    hideDropdownOnEmptyInput: false});
	});
	
	$('#delDeliveryTerm').click(function () {
	     var table = $("#deliveryTermsTable");
	    if (table.find('input:text').length > 1) {
	        table.find('input:text').last().closest('tr').remove();
	    }
	});
	
	$('#addOtherTerm').click(function () {
		getOtherTermDes();
	    var table = $("#otherTermsTable");
	    var rows = document.getElementById("otherTermsTable").getElementsByTagName("tr").length;
	    if (rows >0) {
        var rowCount = rows-3;
	        table.append('<tr><td> <select id ="otherTermDes" name="otherTermTypeId_o_'+rowCount+'" class = "flexselect" /></td><td> <input type="text" size="10" name="otherTermDays_o_'+rowCount+'" value="" /> </td><td> <input type="text" size="10" name="otherTermValue_o_'+rowCount+'" value="" /> </td><td><select name="otherTermUom_o_'+rowCount+'"><option value="INR">Rupees</option><option value="PERCENT">Percent</option></select></td><td><input type="textarea" name="otherTermDescription_o_'+rowCount+'" value="" maxlength="255"/></td></tr>');
	    }
	    $.each(otherTerms, function(key, val){
    		$("select[name*='otherTermTypeId']").append('<option value="' + val.value + '">' + val.label + " [" + val.value + "]" + '</option>');
  		});
   		 $("select[name*='otherTermTypeId']").flexselect({
		  								preSelection: false,
		  								allowClear: false,
		  								allowMismatch:false,
	  								    allowEmpty:true,
	  								    allowNewElements:false,
	  								    placeholder: "select",
	  								    hideDropdownOnEmptyInput: false});
	});
	
	$('#delOtherTerm').click(function () {
	     var table = $("#otherTermsTable");
	    if (table.find('input:text').length > 1) {
	        table.find('input:text').last().closest('tr').remove();
	    }
	});
	
});  
	         
 </script>               
			