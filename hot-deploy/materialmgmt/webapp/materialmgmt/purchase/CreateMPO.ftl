<#include "CreateMPOInc.ftl"/>
<#--
<link href="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.css</@ofbizContentUrl>" rel="stylesheet">
<script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.js</@ofbizContentUrl>"></script>
-->

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
                	if(currentIndex == 0 && newIndex == 1){
                		var supplierId = $("#supplierId").val();
                		
                	    if( (supplierId).length < 1 ) {
					    	$('#supplierId').css('background', 'yellow'); 
					       	setTimeout(function () {
					           	$('#supplierId').css('background', 'white').focus(); 
					       	}, 800);
					    	return false;
				    	}
					      //alert("==supplierId="+supplierId);
					    //populate SlickGrid starts here
						 if(supplierId){
						    gridShowCall();
						 	setupGrid1();
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
		
		makeDayDatePicker("effectiveDate","fromDateId");
		makeDayDatePicker("SInvoiceDate","fromDateId");
	
		
		$('#ui-datepicker-div').css('clip', 'auto');	
		$("#supplierId").autocomplete({ source: partyAutoJson }).keydown(function(e){
			
		});	
		$( "input[name*='paymentTermTypeId']" ).autocomplete({ source: paymentTermsJSON });
		$( "input[name*='deliveryTermTypeId']" ).autocomplete({ source: deliveryTermsJSON });
	});
</script>


	<form id="CreateMPO"  action="<@ofbizUrl>CreateMaterialPO</@ofbizUrl>" name="CreateMPO" method="post">
	    <div id="wizard-2">
        <h3>PO Information</h3>
        <section>
          <fieldset>
	            <table cellpadding="15" cellspacing="15" class='h3' width="50%">
	                   <tr>
							<td class="label">Order Type(<font color="red">*</font>) :</td>
						    <td>
						      	<select name="orderTypeId">
						      	   <#list orderTypes as orderType>    
  	    								<option value='${orderType.orderTypeId}'>${orderType.description}</option>
								    </#list> 
						      	</select>
						    </td>
						</tr>
    					<tr>
						    <td class="label">Vendor(<font color="red">*</font>) :</td>
						     <input type="hidden" name="productStoreId"  value="${productStoreId?if_exists}" />
						    <#if changeFlag?exists && changeFlag=='InterUnitPurchase'>
						       <input type="hidden" name="salesChannel" id="salesChannel" value="INTER_PRCHSE_CHANNEL"/> 
						    <#else>
						       <input type="hidden" name="salesChannel" id="salesChannel" value="MATERIAL_PUR_CHANNEL"/>  
						    </#if>
						    <td>
						      	<input type="text" name="supplierId" id="supplierId" size="18" maxlength="60" />
						    </td>
						</tr>
          		        <tr>
						    <td class="label">Bill To Party :</td>
						    <td>
						      	<@htmlTemplate.lookupField  formName="CreateMPO" size="18" maxlength="60" name="billToPartyId" id="billToPartyId" fieldFormName="LookupPartyName"/>
          		                <span class="tooltip">If billing and vendor party are different, invoice will be raise against this Party </span>
						    </td>
						 </tr>
 						 <tr>
						    <td class="label"><b>Ref No. :</b></td>
						    <td>
						      <input type="text" name="refNo" id="refNo" size="18" maxlength="60" autocomplete="off"/>
	        				 </td>
						</tr>
						<tr>
						    <td class="label"><b>Estimated Delivery Date :</b></td>
						    <td>
						      <input type="text" name="estimatedDeliveryDate" id="estimatedDeliveryDate" size="18" maxlength="60" autocomplete="off"/>
	        				 </td>
						</tr>
						<!--<tr>
						    <td class="label"><b>Ref Date:* </b></td>
						    <td>
						      <input type="text" name="refDate" id="refDate" size="18" maxlength="60" autocomplete="off"/>
	        				 </td>
						</tr>-->
						<tr>
						    <td class="label"><b>PO No :</b></td>
						    <td>
						      <input type="text" name="PONumber" id="PONumber" size="18" maxlength="60" autocomplete="off"/>
	        				 </td>
						</tr>
						<tr>
						    <td class="label"><b>PO Date : </b></td>
						    <td>
						      <input type="text" name="orderDate" id="orderDate" size="18" maxlength="60" autocomplete="off"/>
	        				 </td>
						</tr>
						<tr>
						    <td class="label"><b>File No : </b></td>
						    <td>
						      <input type="text" name="fileNo" id="fileNo" size="18" maxlength="60" autocomplete="off"/>
	        				 </td>
						</tr>
				        <tr>
				            <td class="label"><b> Description :</b></td>
				            <td>
	        				   <input class="h3" type="textarea" size="30" maxlength="100" name="orderName" id="orderName" style="width: 200px; height: 50px" />
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
							        	<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Freight Charges: </div></td>
					       				<td valign='middle' align='left'> 
					           				<input class='h3' type="text" size="20" maxlength="30" name="freightCharges" id="freightCharges" onblur="javascript:addToInvoiceAmount();"/>          
					       				</td>
					       				<td>&nbsp;&nbsp;&nbsp;</td>
					       				<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Discount: </div></td>
							         	<td valign='middle' align='left'> 
					             			<input class='h3' type="text" size="20" maxlength="30" name="discount" id="discount" onblur="javascript:addToInvoiceAmount();"  />          
					          			</td>
					          			<td>&nbsp;&nbsp;&nbsp;</td>
					       				<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Insurance: </div></td>
							         	<td valign='middle' align='left'> 
					             			<input class='h3' type="text" size="20" maxlength="30" name="insurence" id="insurence" onblur="javascript:addToInvoiceAmount();"/>          
					          			</td>
					          			<td>&nbsp;&nbsp;&nbsp;</td>
					          			<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Pack.&Fowdg: </div></td>
							         	<td valign='middle' align='left'> 
							         	 <input class='h3' type="text" size="20" maxlength="30" name="packAndFowdg" id="packAndFowdg" onblur="javascript:addToInvoiceAmount();"/>
					          			</td>
					 		         </tr>
					 		        <tr><td><br/></td></tr>
					 		        <tr>
							        	<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Other Charges: </div></td>
					       				<td valign='middle' align='left'> 
					           				<input class='h3' type="text" size="20" maxlength="30" name="otherCharges" id="otherCharges" onblur="javascript:addToInvoiceAmount();"/>          
					       				</td>
					          			<td>&nbsp;&nbsp;&nbsp;</td>
					          			<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Add BED: </div></td>
							         	<td valign='middle' align='left'> 
							         	<input class='h3' type="checkbox" size="20" id="addBED" name="addBED" value="" onclick="javascript:addBedColumns();"/>
					          			</td>
					 		         </tr>
					 		          <tr><td><br/></td></tr>
					 		          <tr><td colspan="6"> <span class="tooltip"> Note:once BED columns added and input given to BED columns You cant remove them</span></tr>
					        	</table>
							</div>
							<div class="grid-header" style="width:100%">
								<label>Purchase Entry </label><span id="totalAmount"></span>
							</div>
							<div id="myGrid1" style="width:100%;height:250px;"></div>
							<#-->
						    	<div align="center">
						    	<table width="60%" border="0" cellspacing="0" cellpadding="0">  
						    	<tr><td></td><td></td></tr>
						    	<tr><td> &nbsp;<input type="button" style="padding:.3em" name="changeSave" id="changeSave" value="Save" /></td>
						    	<td> &nbsp;<input type="button" style="padding:.3em" id="changeCancel" value="Cancel"/> </td></tr>
						    	 
						    	</table>
						    	</div>    -->
				             </div>  
			          </div>
			</div>        
           
            <#-->
            	<fieldset>
            	
            	
				    <table cellpadding="2" cellspacing="1" class='h2'>
					          <tr>
		          				<td align='left' valign='middle' nowrap="nowrap"></td>
					            <td>
					               <table border="0" cellspacing="2" id="productTable" style="width:200px;" align="center">
					               <input type="button" id="addProduct" value="Add" />    <input type="button" id="delProduct" value="Del" />
								    <tr>
								        <td>Product</td>
								         <td>Quantity</td>
								          <td>UnitPrice</td>
								    </tr>
								    <tr>
								        <td>
								          <input type="text"  value="" name="productId_o_0"/>
								        </td>
							            <td>
							                <input type="text" name="quantity_o_0" value="" />
							            </td>
							            <td>
							                <input type="text" name="unitPrice_o_0" value="" />
							            </td>
								    </tr> 
								</table>
	          				     </td>
					        </tr>
		                 </table> 
                    </fieldset>  -->
               </section>
                      <h3>Payment Terms</h3>
			          <section>
				          <fieldset>
				            <table cellpadding="15" cellspacing="15" class='h3'>
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
								         	<td align="center">Term Days</td>
								          	<td align="center">Term Value</td>
								          	<td align="center">UOM</td>
								          	<td align="center">Description</td>
								    	</tr>
								    	<tr>
								        	<td>
								          		<input type="text"  name="paymentTermTypeId_o_0" value=""/>
								        	</td>
							            	<td>
							                	<input type="text" name="paymentTermDays_o_0" value="" />
							            	</td>
							            	<td>
							                	<input type="text" name="paymentTermValue_o_0" value="" />
							            	</td>
							            	<td>
							            		<select name="paymentTermUom_o_0">
							            			<option value="INR">Rupees</option>
							            			<option value="PERCENT">Percent</option>
							            		</select>
							            	</td>
							            	<td>
							                	<input type="textarea" name="paymentTermDescription_o_0" value="" maxlength="255"/>
							            	</td>
								    	</tr> 
									</table>
	          				     </td>
							  </tr>
						  </table>
						</fieldset>
                     </section>
                     <h3>Delivery Terms</h3>
			         <section>
				          <fieldset>
				            <table cellpadding="15" cellspacing="15" class='h3'>
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
							         	<td align="center">Term Days</td>
							          	<td align="center">Term Value</td>
							          	<td align="center">UOM</td>
							          	<td align="center">Description</td>
								    </tr>
								    <tr>
								        <td>
								         <input type="text"  name="deliveryTermTypeId_o_0" value=""/>
								        <!-- <select  name="paymentTermTypeId_o_0">
								            <list>
								               <option > </option>
								            </list>
								         </select>-->
								        </td>
							            <td>
							                <input type="text" name="deliveryTermDays_o_0" value="" />
							            </td>
							            <td>
							                <input type="text" name="deliveryTermValue_o_0" value="" />
							            </td>
						            	<td>
						            		<select name="deliveryTermUom_o_0">
						            			<option value="INR">Rupees</option>
						            			<option value="PERCENT">Percent</option>
						            		</select>
						            	</td>
						            	<td>
						                	<input type="textarea" name="deliveryTermDescription_o_0" value="" maxlength="255"/>
						            	</td>
								    </tr> 
								</table>
	          				     </td>
							        </tr>
						  </table>
						  </fieldset>
                        </section>
                </form>
                
 <script type="application/javascript">
 		 $(document).ready(function(){
            
  /*          $('#addProduct').click(function () {
    var table = $("#productTable");
    if (table.find('input:text').length < 16) {
        var productLength = table.find('input:text').length;
        var rowProdCount = productLength/3;
        table.append('<tr><td> <input type="text" name="productId_o_'+rowProdCount+'" value="" /></td><td> <input type="text" name="quantity_o_'+rowProdCount+'" value="" /> </td><td> <input type="text" name="unitPrice_o_'+rowProdCount+'" value="" /> </td></tr>');
    }
});
$('#delProduct').click(function () {
    var table = $(this).closest('table');
    if (table.find('input:text').length > 1) {
        table.find('input:text').last().closest('tr').remove();
    }
});
*/

    $('#addPaymentTerm').click(function () {
    var table = $("#paymentTermsTable");
    if (table.find('input:text').length < 16) {
        var rowLength = table.find('input:text').length;
        var rowCount = rowLength/3;
        table.append('<tr><td> <input type="text" name="paymentTermTypeId_o_'+rowCount+'" value="" /></td><td> <input type="text" name="paymentTermDays_o_'+rowCount+'" value="" /> </td><td> <input type="text" name="paymentTermValue_o_'+rowCount+'" value="" /> </td><td><select name="paymentTermUom_o_'+rowCount+'"><option value="INR">Rupees</option><option value="PERCENT">Percent</option></select></td><td><input type="textarea" name="paymentTermDescription_o_'+rowCount+'" value="" maxlength="255"/>	</td></tr>');
    }
    $( "input[name*='paymentTermTypeId']" ).autocomplete({ source: paymentTermsJSON });
});
	$('#delPaymentTerm').click(function () {
	     var table = $("#paymentTermsTable");
	    if (table.find('input:text').length > 1) {
	        table.find('input:text').last().closest('tr').remove();
	    }
	});

	
	 $('#addDeliveryTerm').click(function () {
    var table = $("#deliveryTermsTable");
    if (table.find('input:text').length < 16) {
        var rowLength = table.find('input:text').length;
        var rowCount = rowLength/3;
        table.append('<tr><td> <input type="text" name="deliveryTermTypeId_o_'+rowCount+'" value="" /></td><td> <input type="text" name="deliveryTermDays_o_'+rowCount+'" value="" /> </td><td> <input type="text" name="deliveryTermValue_o_'+rowCount+'" value="" /> </td><td><select name="deliveryTermUom_o_'+rowCount+'"><option value="INR">Rupees</option><option value="PERCENT">Percent</option></select></td><td><input type="textarea" name="deliveryTermDescription_o_'+rowCount+'" value="" maxlength="255"/></td></tr>');
    }
    
    $( "input[name*='deliveryTermTypeId']" ).autocomplete({ source: deliveryTermsJSON });
});
	$('#delDeliveryTerm').click(function () {
	     var table = $("#deliveryTermsTable");
	    if (table.find('input:text').length > 1) {
	        table.find('input:text').last().closest('tr').remove();
	    }
	});
	
   });  
	         
 </script>               
			