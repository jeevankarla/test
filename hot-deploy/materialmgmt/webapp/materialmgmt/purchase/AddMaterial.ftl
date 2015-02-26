<#--
<link href="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.css</@ofbizContentUrl>" rel="stylesheet">
<script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.js</@ofbizContentUrl>"></script>
-->
<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/flexselect.css</@ofbizContentUrl>">
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/liquidmetal.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/jquery.flexselect.js</@ofbizContentUrl>"></script>
<link href="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.css</@ofbizContentUrl>" rel="stylesheet">
<script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.js</@ofbizContentUrl>"></script>
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
    $(document).ready(function(){
            $("#wizard-2").steps({
                headerTag: "h3",
                bodyTag: "section",
                transitionEffect: "slideLeft",
                onStepChanging: function (event, currentIndex, newIndex)
                {	
                	if(currentIndex == 0 && newIndex == 1){
                	var materialCode = $("#materialCode").val();
                		
                	    if( (materialCode).length < 1 ) {
					    	$('#materialCode').css('background', 'yellow'); 
					       	setTimeout(function () {
					           	$('#materialCode').css('background', 'white').focus(); 
					       	}, 800);
					    	return false;
					    	}
                	 
                		return true;
                	}
                	if(currentIndex == 1 && newIndex == 2){
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
					var form = ($(this)).parent();
                	form.submit();
                }
            });
	}); 
	
	$(document).ready(function(){
		makeDatePicker("fromDate","fromDateId");
	});
	
</script>
	
	
	<form id="AddMaterial"  action="<@ofbizUrl>AddMaterialData</@ofbizUrl>" name="AddMaterial" method="post">
	    <div id="wizard-2">
        <h3>Basic Information</h3>
        <section>
          <fieldset>
	            <table cellpadding="15" cellspacing="15" class='h3' width="50%">
	            
	          		    <tr>
							<td></td>
						    <td>
							 <input class='h4' type='hidden' id='productTypeId' name='productTypeId' value='RAW_MATERIAL'/>
						    </td>
						</tr>
						<tr>
							<td class="label">Primary Category :</td>
						    <td>
							<select name="primaryCategoryId" id="primaryCategoryId">
						      	   <#list primaryCategoryList as primaryCategory>
						      	   			<option value='${primaryCategory.productCategoryId}'>${primaryCategory.description}</option>
								    </#list> 
						      	</select>
						    </td>
						</tr>
	                   <tr>
							<td class="label">Material Category :</td>
						    <td>
							<select name="productCategoryId" id="productCategoryId" multiple="multiple" onblur="getMultiple(document.AddMaterial.productCategoryId);">
						      	   <#list materialCategoryList as materialCategory>
						      	   			<option value='${materialCategory.productCategoryId}'>${materialCategory.description}</option>
								    </#list> 
						      	</select>
						      	<span class="tooltip">press 'Ctrl' key to select multiple </span>
						    </td>
						</tr>
						<tr>
          		        	<td class="label"><b>Material Code(<font color="red">*</font>): </b></td>
					    	<td>
						      	<input type="text" name="materialCode" id="materialCode" size="18" maxlength="60" autocomplete="off"/>
        				 	</td>
						</tr>
						<tr>
          		        	<td class="label"><b>Description: </b></td>
					    	<td>
         						<textarea cols="40" rows="5" name="description" id="description"></textarea>
     					    </td>
						</tr>
	                  </table>
                    </fieldset>  
                 </section>
                 
                     <h3>UOM and Specifications</h3>
			          <section>
				          <fieldset>
				            <table cellpadding="15" cellspacing="15" class='h2'>
								
								<tr>
									<td class="label">Quantity Uom Id</td>
								    <td>
									<select name="productUOMtypeId" id="productUOMtypeId">
													<option></option>
								      	   <#list productUOMList as productUOM>
								      	   			<option value='${productUOM.uomId}'>${productUOM.description}</option>
										    </#list> 
								      	</select>
								    </td>
								</tr>
								<tr>
		          		        	<td class="label"><b>Specification: </b></td>
							    	<td>
		         						<textarea cols="40" rows="5" name="specification" id="specification"></textarea>
		     					    </td>
								</tr>			
								<tr>
									<td class="label">Facility Id</td>
								    <td>
										<select name="facilityId" id="facilityId">
													<option></option>
								      	   			<option value='STORE' >STORE</option>
								      	   			<option value='ICP_STORE' >ICP STORE</option>
								      	</select>
								    </td>
								</tr>
								<tr>
									<td></td>
								    <td>
									 <input class='h4' type='hidden' id='attributeName' name='attributeName' value='LEDGERFOLIONO'/>
								    </td>
								</tr>
								<tr>
		          		        	<td class="label"><b>Material Attribute Value: </b></td>
							    	<td>
								      	<input type="text" name="attributeValue" id="attributeValue" size="18" maxlength="60" autocomplete="off"/>
		        				 	</td>
								</tr>
						  </table>
						</fieldset>
                     </section>
                </form>
                
 <script type="application/javascript">
   var selected = new Array();
function getMultiple(productCategory)
{
    for (var i = 0; i < productCategory.options.length; i++)
    	{
    	  if (productCategory.options[ i ].selected)
    		  selected.push(productCategory.options[ i ].value);
    	}
    	alert(selected);
}
 </script>               
			