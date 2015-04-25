
<link href="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.css</@ofbizContentUrl>" rel="stylesheet">
<script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.js</@ofbizContentUrl>"></script>


<script type="application/javascript">


function makeDatePicker(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat:'yy-mm-dd',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
			}
		});
	$( "#"+thruDateId ).datepicker({
			dateFormat:'yy-mm-dd',
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
	
   function changeToUpperCase(){
		var pName = $("#PNAME").val();
		if(pName != null || pName != undefined){
			pName = pName.toUpperCase();
			$("#PNAME").val(pName);
		}
	}
	
	var partyAutoJson = ${StringUtil.wrapString(partyJSON)!'[]'};
    var partyNameObj = ${StringUtil.wrapString(partyNameObj)!'[]'};
	
	
	
    $(document).ready(function(){
         	
            $("#wizard-2").steps({
                headerTag: "h3",
                bodyTag: "section",
                transitionEffect: "slideLeft",
                onStepChanging: function (event, currentIndex, newIndex)
                {	
               
                	if(currentIndex == 0 && newIndex == 1){
                		
                		var productCategoryId = $("#productCategoryId").val();
                		var custRequestTypeId = $("#custRequestTypeId").val();
                		var categoryId = $("#categoryId").val();
                		var productId = $("#productId").val();
                		var assetMapping = $("#assetMapping").val();
                		
                	
				    	<#--if( (productCategoryId).length < 1 ) {
					    	$('#productCategoryId').css('background', 'yellow'); 
					       	setTimeout(function () {
					           	$('#productCategoryId').css('background', 'white').focus(); 
					       	}, 800);
					    	return false;
				    	}-->
				    	if( (custRequestTypeId).length < 1 ) {
					    	$('#custRequestTypeId').css('background', 'yellow'); 
					       	setTimeout(function () {
					           	$('#custRequestTypeId').css('background', 'white').focus(); 
					       	}, 800);
					    	return false;
				    	}
				    	if( (categoryId).length < 1 ) {
					    	$('#categoryId').css('background', 'yellow'); 
					       	setTimeout(function () {
					           	$('#categoryId').css('background', 'white').focus(); 
					       	}, 800);
					    	return false;
				    	}
				    	if( (productId).length < 1 ) {
					    	$('#productId').css('background', 'yellow'); 
					       	setTimeout(function () {
					           	$('#productId').css('background', 'white').focus(); 
					       	}, 800);
					    	return false;
				    	}
				    	<#--if( (assetMapping).length < 1 ) {
					    	$('#assetMapping').css('background', 'yellow'); 
					       	setTimeout(function () {
					           	$('#assetMapping').css('background', 'white').focus(); 
					       	}, 800);
					    	return false;
				    	}-->
                		return true;
                	}
                	
                	if(currentIndex == 1 && newIndex == 2){
                		
                		var severity = $("#severity").val();
                		var environment = $("#environment").val();
                		var project = $("#project").val();
                		
				    	if( (severity).length < 1 ) {
					    	$('#severity').css('background', 'yellow'); 
					       	setTimeout(function () {
					           	$('#severity').css('background', 'white').focus(); 
					       	}, 800);
					    	return false;
				    	}
				    	if( (environment).length < 1 ) {
					    	$('#environment').css('background', 'yellow'); 
					       	setTimeout(function () {
					           	$('#environment').css('background', 'white').focus(); 
					       	}, 800);
					    	return false;
				    	}
				    	if( (project).length < 1 ) {
					    	$('#project').css('background', 'yellow'); 
					       	setTimeout(function () {
					           	$('#project').css('background', 'white').focus(); 
					       	}, 800);
					    	return false;
				    	}
                		return true;
                	}
                	
                	if(currentIndex == 3 && newIndex == 4){
	               		
                		var email = $("#emailAddress").val();
                		
                	    if( (assignedTo).length < 1 ) {
					    	$('#assignedTo').css('background', 'yellow'); 
					       	setTimeout(function () {
					           	$('#assignedTo').css('background', 'white').focus(); 
					       	}, 800);
					    	return false;
				    	}
				   
                  		 if((email).length!=0 && !validateEmail(email)){
						    	alert("invalid email");
						    	$('#email').css('background', 'yellow'); 
						       	setTimeout(function () {
						           	$('#email').css('background', 'white').focus(); 
						       	}, 800);
						    	return false;
						    }
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
	//	makeDatePicker("custRequestDate","fromDateId");
		makeDatePicker("fDateStr","fromDateId");
		makeDatePicker("tDateStr","thruDateId");
		makeDatePicker("fDate","fromDateId");
		$('#ui-datepicker-div').css('clip', 'auto');
		
		$("#assignedPartyId").autocomplete({ source: partyAutoJson }).keydown(function(e){
       
       	});        
   
	});
	   var supplierName;
       function displaySuppName(selection){
          value = $("#assignedPartyId").val();
          assignerName = partyNameObj[value];
          $("#assignerName").html("<h4>"+assignerName+"</h4>");
       }          
  
</script>
	<form id="createComplaint"  action="<@ofbizUrl>createComplaint</@ofbizUrl>" name="createComplaint" method="post">
	    <div id="wizard-2">
       
            <h3>Ticket Type</h3>
            <section>
            	<fieldset>
				    <table cellpadding="2" cellspacing="1">
					    
					         <tr>
						    <td class="label"><b> Date</b></td>
						
					         <input type="hidden" name="custRequestDate" id="custRequestDate" value="${Static["org.ofbiz.base.util.UtilDateTime"].nowDateString("dd-MM-yyyy hh:mm:ss.SSS")}"  />  
					           	<td>
					            	<div class='tabletext h3'>${Static["org.ofbiz.base.util.UtilDateTime"].nowDateString("dd-MM-yyyy")}         
					            	</div>
					          	</td>        
						</tr>
						      <tr>
					          	<td class="label"><b>Category</b></td></td>
							    <td>
		            			 <select name="categoryId"  id="categoryId">
						       <option value='SOFTWARE'>Software</option>
							   <option value='HARDWARE'>Hardware</option>
					      	   </select>
		          				</td>
					        </tr>
					         <tr>
					       	<td class="label"><b>Sub-Category</b></td></td>
					    	<td>
					            <select name="productCategoryId" id="productCategoryId">
						     <option value=""></option>  
                         <#list ProductCategories as ProductCategory>
						     <option value='${ProductCategory.enumId?if_exists}' >${ProductCategory.description?if_exists}</option>
						     </#list>
						     </select>
						     </td>
					        </tr>
					         <tr>
					            <td class="label"><b>Type</b></td></td>
		       	  				 <td>
						     <select name="custRequestTypeId" id="custRequestTypeId">
						     <option value=""></option>  
						     <#list complaintTypeList as complaintTypes>
						     <option value='${complaintTypes.custRequestTypeId?if_exists}' >${complaintTypes.custRequestTypeId?if_exists}</option>
						     </#list>
						     </select>
	        				 </td>
					        </tr>
					         <tr>
					            <td class="label"><b>Product / Component</b></td></td> 
                             <td> <select name="productId" id="productId">
						     <option value=""></option>  
						     <#list ProductIds as productIds1>
						     <option value='${productIds1.productId}' >${productIds1.description?if_exists}</option>
						     </#list>
						     </select>	 </td>
		          				</tr>
		          				
                                  <#--<tr>
					           <td class="label"><b>Asset Mapping</b></td>
                             <td> <select name="assetMapping" id="assetMapping">
						     <option value=""></option>  
						     <#list assertMappingProds as assertMappingProd>
						     <option value='${assertMappingProd.productId}' >${assertMappingProd.description?if_exists}</option>
						     </#list>
						     </select>	 </td>
							  </tr>
									
	          				<tr>
		       					<td>&nbsp;</td>
					        </tr>
					        </tr>-->
		                 </table>
                    </fieldset>  
               </section>
                      <h3>Ticket Details</h3>
			          <section>
				          <fieldset>
				            <table cellpadding="2" cellspacing="1">
							      
							   <tr>
					            <td class="label"><b>Severity</b></td></td>
		          				<td>
						     <select name="severity" id="severity">
						     <option value=""></option>  
						     <#list severityTypes as severityType>
						     <option value='${severityType.statusId?if_exists}' >${severityType.description?if_exists}</option>
						     </#list>
						     </select>
	        				 </td>
	          				</tr>
	          				 <tr>
					            <td class="label"><b>Environment</b></td></td>
		          				<td>
                            <select name="environment" id="environment">
						     <option value=""></option>  
						     <#list environmentDetails as environmentDetail>
						     <option value='${environmentDetail.enumId?if_exists}' >${environmentDetail.description?if_exists}</option>
						     </#list>
						     </select>
		          				</td>
	          				</tr> 
	          				<tr>
					            <td class="label"><b>Project</b></td></td>
		          				<td>
                          <select name="project" id="project">
						     <option value=""></option>  
						     <#list projectDetails as projectDetail>
						     <option value='${projectDetail.enumId?if_exists}' >${projectDetail.description?if_exists}</option>
						     </#list>
						     </select>
		          				</td>
	          				</tr>   
	          				<tr>
					            <td class="label"><b>Subject</b></td></td>
		          				<td>
									<input class="h3" type="textfield" size="30" maxlength="150" name="subject" id="subject"  style="width: 400px; height: 20px"/>
		          				</td>
	          				</tr>  
                           <tr>
					            <td class="label"><b>Remarks</b></td></td>
		          				<td>
									<input class="h3" type="textarea" size="30" maxlength="150"  name="remarks" id="remarks" style="width: 400px; height:35px"/>
		          				</td>
	          				</tr>    
						  </table>
						  </fieldset>
                        </section>
                 <#-->    <h3>Contact Details</h3>
			          <section>
					        <fieldset>
					            <table cellpadding="2" cellspacing="1">
								   
								        <tr>
					          				 <td class="label"><b>SLA</b></td>
										     <td>
										      	<input type="text" name="SLA" id="SLA" size="30" maxlength="60" autocomplete="off"/>
										    </td>
								        </tr>
                                          <tr>
					          				 <td class="label"><b>Email CC*</b></td>
										     <td>
										      	<input type="text" name="emailAddress" id="emailAddress" size="30" maxlength="60" autocomplete="off"/>
										    </td>
								        </tr>
								        <tr>
								            <td class="label"><b>Description</b></td></td>
					          				<td>
												<input class="h3" type="text" size="18" maxlength="20" name="description" id="description" />
					          				</td>
	          							</tr>
							     </table>
							 
							</fieldset>
                        </section>    -->
                </form>
			