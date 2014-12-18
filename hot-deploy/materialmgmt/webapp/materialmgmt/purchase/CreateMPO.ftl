
<link href="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.css</@ofbizContentUrl>" rel="stylesheet">
<script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.js</@ofbizContentUrl>"></script>


<script type="application/javascript">

function makeDatePicker(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat:'MM d, yy',
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
					//form.append();
                	form.submit();
                }
            });
	}); 
	
	$(document).ready(function(){
		makeDatePicker("expectedDeliveryDate","fromDateId");
		makeDatePicker("poDate","fromDateId");
		makeDatePicker("refDate","fromDateId");
		$('#ui-datepicker-div').css('clip', 'auto');		
	});
</script>
	<form id="CreateMPO"  action="<@ofbizUrl>CreateMaterialPO</@ofbizUrl>" name="CreateMPO" method="post">
	    <div id="wizard-2">
        <h3>PO Information</h3>
        <section>
          <fieldset>
	            <table cellpadding="2" cellspacing="1" class='h2'>
    					<tr>
						    <td class="label"><b>Vendor Id :*</b></td>
						    <td>
						      	<input type="text" name="supplierId" id="supplierId" size="18" maxlength="60" autocomplete="off"/>
						    </td>
						</tr>
 						 <tr>
						    <td class="label"><b>Ref No:*</b></td>
						    <td>
						      <input type="text" name="refNo" id="refNo" size="18" maxlength="60" autocomplete="off"/>
	        				 </td>
						</tr>
						<tr>
						    <td class="label"><b>Ref Date:* </b></td>
						    <td>
						      <input type="text" name="refDate" id="refDate" size="18" maxlength="60" autocomplete="off"/>
	        				 </td>
						</tr>
						<tr>
						    <td class="label"><b>PO No:*</b></td>
						    <td>
						      <input type="text" name="externalId" id="externalId" size="18" maxlength="60" autocomplete="off"/>
	        				 </td>
						</tr>
						<tr>
						    <td class="label"><b>PO Date:* </b></td>
						    <td>
						      <input type="text" name="orderDate" id="orderDate" size="18" maxlength="60" autocomplete="off"/>
	        				 </td>
						</tr>
						<tr>
						    <td class="label"><b>Estimated Delivery Date:* </b></td>
						    <td>
						      <input type="text" name="estimatedDeliveryDate" id="estimatedDeliveryDate" size="18" maxlength="60" autocomplete="off"/>
	        				 </td>
						</tr>
						<tr>
						    <td class="label"><b>File No:* </b></td>
						    <td>
						      <input type="text" name="fileNo" id="fileNo" size="18" maxlength="60" autocomplete="off"/>
	        				 </td>
						</tr>
				        <tr>
				            <td class="label"><b> Description :</b></td>
				            <td>
	        				   <input class="h3" type="textarea" size="40" maxlength="500" name="orderName" id="orderName"/>
	          				</td>
				        </tr>
	                  </table>
                    </fieldset>  
                 </section>
            <h3>Add Material </h3>
            <section>
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
                    </fieldset>  
               </section>
                      <h3>Payment Terms</h3>
			          <section>
				          <fieldset>
				            <table cellpadding="2" cellspacing="1" class='h2'>
							         <tr>
				          				<td align='left' valign='middle' nowrap="nowrap"></td>
					                 <td>
					               <table border="0" cellspacing="2" id="paymentTermsTable" style="width:200px;" align="center">
					                <tr>
								       <td><table><tr>
								           <td><input type="button" id="addPaymentTerm" value="Add" />  </td>
								        	<td> <input type="button" id="delPaymentTerm" value="Del" /></td>
								        </tr> </table>
								          </td>
								         <td> </td>
								          <td></td>
								    </tr>
								    <tr>
								        <td>Term Type</td>
								         <td>Term Days</td>
								          <td>Term Value</td>
								    </tr>
								    <tr>
								        <td>
								          <input type="text"  name="paymentTermTypeId_o_0" value=""/>
								        <!-- <select  name="paymentTermTypeId_o_0">
								            <list>
								               <option > </option>
								            </list>
								         </select>-->
								        </td>
							            <td>
							                <input type="text" name="paymentTermDays_o_0" value="" />
							            </td>
							            <td>
							                <input type="text" name="paymentTermValues_o_0" value="" />
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
				            <table cellpadding="2" cellspacing="1" class='h2'>
							         <tr>
				          				<td align='left' valign='middle' nowrap="nowrap"></td>
					                 <td>
					               <table border="0" cellspacing="2" id="paymentTermsTable" style="width:200px;" align="center">
					                <tr>
								       <td><table><tr>
								           <td><input type="button" id="addPaymentTerm" value="Add" />  </td>
								        	<td> <input type="button" id="delPaymentTerm" value="Del" /></td>
								        </tr> </table>
								          </td>
								         <td> </td>
								          <td></td>
								    </tr>
								    <tr>
								        <td>Term Type</td>
								         <td>Term Days</td>
								          <td>Term Value</td>
								    </tr>
								    <tr>
								        <td>
								         <input type="text"  name="paymentTermTypeId_o_0" value=""/>
								        <!-- <select  name="paymentTermTypeId_o_0">
								            <list>
								               <option > </option>
								            </list>
								         </select>-->
								        </td>
							            <td>
							                <input type="text" name="paymentTermDays_o_0" value="" />
							            </td>
							            <td>
							                <input type="text" name="paymentTermValues_o_0" value="" />
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
            
            $('#addProduct').click(function () {
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


    $('#addPaymentTerm').click(function () {
    var table = $("#paymentTermsTable");
    if (table.find('input:text').length < 16) {
        var rowLength = table.find('input:text').length;
        var rowCount = rowLength/3;
        table.append('<tr><td> <input type="text" name="paymentTermTypeId_o_'+rowCount+'" value="" /></td><td> <input type="text" name="paymentTermDays_o_'+rowCount+'" value="" /> </td><td> <input type="text" name="paymentTermValue_o_'+rowCount+'" value="" /> </td></tr>');
    }
});
	$('#delPaymentTerm').click(function () {
	     var table = $("#paymentTermsTable");
	    if (table.find('input:text').length > 1) {
	        table.find('input:text').last().closest('tr').remove();
	    }
	});


   });  
	         
 </script>               
			