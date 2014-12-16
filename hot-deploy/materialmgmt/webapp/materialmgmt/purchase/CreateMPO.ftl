
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
            $("#supplierId").focus(function () {
	      alert("ooo");
	         });
	         
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
						      <input type="text" name="poNumber" id="poNumber" size="18" maxlength="60" autocomplete="off"/>
	        				 </td>
						</tr>
						<tr>
						    <td class="label"><b>PO Date:* </b></td>
						    <td>
						      <input type="text" name="poDate" id="poDate" size="18" maxlength="60" autocomplete="off"/>
	        				 </td>
						</tr>
						<tr>
						    <td class="label"><b>Expected Delivery Date:* </b></td>
						    <td>
						      <input type="text" name="expectedDeliveryDate" id="expectedDeliveryDate" size="18" maxlength="60" autocomplete="off"/>
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
	        				   <input class="h3" type="textarea" size="40" maxlength="500" name="description" id="description"/>
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
		          				<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>we need to add product slick grid here</div></td>
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
				          				 <td class="label"><b>Payment</b></td>
									     <td>
									        
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
					          				 <td class="label"><b>FDR NO</b></td>
										     <td>
										      	<input type="text" name="fdrNumber" id="fdrNumber" size="30" maxlength="60" autocomplete="off" />
										    </td>
								        </tr>
								         <tr>
					          				 <td class="label"><b>bank Name</b></td>
										     <td>
										      	<input type="text" name="bankName" id="bankName" size="30" maxlength="60" autocomplete="off"/>
										    </td>
								        </tr>
								        <tr>
					          				 <td class="label"><b>Branch Name</b></td>
										     <td>
										      	<input type="text" name="branchName" id="branchName" size="30" maxlength="60" autocomplete="off"/>
										    </td>
								        </tr>
								        <tr>
					          				 <td class="label"><b>Acrrued balance in the deposit</b></td>
										     <td>
										      	<input type="text" name="amount" id="amount" size="30" maxlength="60" autocomplete="off"/>
										    </td>
								        </tr>
								        <tr>
					          				 <td class="label"><b>Date of opening of the deposit</b></td>
										     <td>
										      	<input type="text" name="fDateStr" id="fDateStr" size="30" maxlength="60" autocomplete="off"/>
										    </td>
								        </tr>
								         <tr>
					          				 <td class="label"><b>Date of closure of the deposit</b></td>
										     <td>
										      	<input type="text" name="tDateStr" id="tDateStr" size="30" maxlength="60" autocomplete="off"/>
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
        table.append('<tr><td> <input type="text" name="productId_o_'+(productLength/2)+'" value="" /></td><td> <input type="text" name="quantity_o_'+(productLength/2)+'" value="" /> </td><td> <input type="text" name="unitPrice_o_'+(productLength/2)+'" value="" /> </td></tr>');
    }
});
$('#delProduct').click(function () {
    var table = $(this).closest('table');
    if (table.find('input:text').length > 1) {
        table.find('input:text').last().closest('tr').remove();
    }
});
	   });       
	         
	         
 </script>               
			