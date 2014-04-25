
<link href="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.css</@ofbizContentUrl>" rel="stylesheet">
<script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.js</@ofbizContentUrl>"></script>


<script type="text/javascript">
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
	function hideorshow(){
	  var categoryTypeEnum =$( "#categoryTypeEnum option:selected" ).val();
		if(categoryTypeEnum == "CR_INST"){
		    jQuery("#marginOnMilk").parent().parent().show();
			jQuery("#marginOnProduct").parent().parent().show();
		}else{
			jQuery("#marginOnMilk").parent().parent().hide();
			jQuery("#marginOnProduct").parent().parent().hide();
		}
	}
    $(document).ready(function(){
      	jQuery("#marginOnMilk").parent().parent().hide();
		jQuery("#marginOnProduct").parent().parent().hide();
    		
            $("#wizard-2").steps({
                headerTag: "h3",
                bodyTag: "section",
                transitionEffect: "slideLeft",
                onStepChanging: function (event, currentIndex, newIndex)
                {	
                	if(currentIndex == 0 && newIndex == 1){
                		var facilityId = $("#facilityId").val();
                		var facilityName = $("#facilityName").val();
                		var parentFacilityId=jQuery("[name='"+"parentFacilityId"+"']").val();
                	    if( (facilityId).length < 1 ) {
					    	$('#facilityId').css('background', 'yellow'); 
					       	setTimeout(function () {
					           	$('#facilityId').css('background', 'white').focus(); 
					       	}, 800);
					    	return false;
				    	}
				    	if( (parentFacilityId).length < 1 ) {
					    	$('#0_lookupId_parentFacilityId').css('background', 'yellow'); 
					       	setTimeout(function () {
					           	$('#0_lookupId_parentFacilityId').css('background', 'white').focus(); 
					       	}, 800);
					    	return false;
				    	}
				    	 if( (facilityName).length < 1 ) {
					    	$('#facilityName').css('background', 'yellow'); 
					       	setTimeout(function () {
					           	$('#facilityName').css('background', 'white').focus(); 
					       	}, 800);
					    	return false;
				    	}
				    	
                		return true;
                	}
                	if(currentIndex == 1 && newIndex == 2){
                	     var groupName = $("#groupName").val();
	                	 var address1 = $("#address1").val();
	                	 var city = $("#city").val();
	                	 var postalCode = $("#postalCode").val();
	                	 var email = $("#emailAddress").val();
                		 if( (groupName).length < 1 ) {
					    	$('#groupName').css('background', 'yellow'); 
					       	setTimeout(function () {
					           	$('#groupName').css('background', 'white').focus(); 
					       	}, 800);
					    	return false;
				    	}
	                	  if( (address1).length < 1 ) {
						    	$('#address1').css('background', 'yellow'); 
						       	setTimeout(function () {
						           	$('#address1').css('background', 'white').focus(); 
						       	}, 800);
						    	return false;
					      }
	                	  if( (city).length < 1 ) {
						    	$('#city').css('background', 'yellow'); 
						       	setTimeout(function () {
						           	$('#city').css('background', 'white').focus(); 
						       	}, 800);
						    	return false;
					    	}
					    	if( (postalCode).length < 1 ) {
						    	$('#postalCode').css('background', 'yellow'); 
						       	setTimeout(function () {
						           	$('#postalCode').css('background', 'white').focus(); 
						       	}, 800);
						    	return false;
					    	}
						    if((email).length!=0&&!validateEmail(email)){
						    	alert("invalid email");
						    	$('#email').css('background', 'yellow'); 
						       	setTimeout(function () {
						           	$('#email').css('background', 'white').focus(); 
						       	}, 800);
						    	return false;
						    }
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
		makeDatePicker("openedDate","fromDateId");
		makeDatePicker("fDateStr","fromDateId");
		makeDatePicker("tDateStr","thruDateId");
		makeDatePicker("fDate","fromDateId");
		$('#ui-datepicker-div').css('clip', 'auto');		
	});
</script>
	<form id="EditBoothForm"  action="<@ofbizUrl>CreateBooth</@ofbizUrl>" name="EditBoothForm" method="post">
	    <div id="wizard-2">
        <h3>Facility Information</h3>
        <section>
          <fieldset>
	            <table cellpadding="2" cellspacing="1">
    					<tr>
						    <td class="label"><b> Retailer Id*</b></td>
						    <td>
						      	<input type="text" name="facilityId" id="facilityId" size="18" maxlength="60" autocomplete="off" value="${emailAddress?if_exists}"/>
						    </td>
						</tr>
				        <!--<tr>
				        <td>&nbsp;</td>
					    <td  align='left' valign='middle' nowrap="nowrap" ><div class='h3'>${uiLabelMap.ProductFacilityTypeId}</div></td>
					     <td>&nbsp;</td>
	       	  			 <td valign='middle'>
						      <select name="facilityTypeId">
						        <option selected="selected" value='${facilityType.facilityTypeId?if_exists}'>${facilityType.get("description",locale)?if_exists}</option>
						        <option value='${facilityType.facilityTypeId?if_exists}'>----</option>
						        <#list facilityTypes as nextFacilityType>
						          <option value='${nextFacilityType.facilityTypeId?if_exists}'>${nextFacilityType.get("description",locale)?if_exists}</option>
						        </#list>
						      </select>
					    </td>
 						 </tr>-->
 						 <tr>
						    <td class="label"><b> Route Id*</b></td>
						    <td>
						       <@htmlTemplate.lookupField value="${facility.parentFacilityId?if_exists}" formName="EditBoothForm" name="parentFacilityId" id="parentFacilityId" fieldFormName="LookupFacility" size="18"/>
	        				 </td>
						</tr>
						<tr>
						    <td class="label"><b> ${uiLabelMap.ProductFacilityCategoryType}</b></td>
						    <#assign enumerations = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "BOOTH_CAT_TYPE"))>
						   <td>
						    <select name="categoryTypeEnum" id="categoryTypeEnum" onchange="javascript:hideorshow();">
					        	 <option selected="selected" value='${categoryTypeEnum?if_exists}'>${categoryTypeEnum?if_exists}</option>
					        	 <option value=""></option>    
				                 <#list enumerations as enumeration>
	                            <option value="${enumeration.enumId}" <#if "${enumeration.enumId}" == categoryTypeEnum?if_exists>selected="selected"</#if>>${enumeration.description}</option>
	                           </#list>
				            </select>
				            <td>
						</tr>
						<tr>
						  <td class="label"><b> Sequence</b></td>
						   <td>
		            	   <input class="h3" type="text" size="18" maxlength="20" name="sequenceNum" id="sequenceNum"  />
			          	 </td>
					 </tr>
					   <tr>
				        <td class="label"><b> Booth Name*</b></td>
				        <td>
        		 			<input class="h3" type="text" size="18" maxlength="20" name="facilityName" id="facilityName"  />
          				</td>
				        </tr>
				        <tr>
				        <tr>
				            <td class="label"><b> Description</b></td>
				            <td>
	        				   <input class="h3" type="text" size="18" maxlength="20" name="description" id="description"  />
	          				</td>
				        </tr>
				         <tr>
	       					<td class="label"><b> Security Deposit</b></td>
	       					 <td>
	        				  <input class="h3" type="text" size="18" maxlength="20" name="securityDeposit" id="securityDeposit" />
	          				</td>
				        </tr>
				        <tr>
				           <td class="label"><b> Maximum Credit</b></td>
				           <td>
	            			<input class="h3" type="text" size="18" maxlength="20" name="maximumCredit" id="maximumCredit" />
	          				</td>
				        </tr>
				         <tr>
				          	<td class="label"><b> Date of Commissioning</b></td>
						    <td>
						      	<input class="h3" type="text" name="openedDate" id="openedDate" size="18" maxlength="60" readOnly autocomplete="off" />
						    </td>
						 </tr>
						  <tr>
				          	<td class="label"><b> Margin Allowed On Milk</b></td>
						    <td>
				              <select name="marginOnMilk" id="marginOnMilk" >
					        	<option value="DEFAULT_PRICE">DEFAULT_PRICE</option> 
					        	<option value="MRP_PRICE">MRP_PRICE</option>            
					          </select>   
						  </td>
						 </tr>
						 <tr>
				          	<td class="label"><b> Margin Allowed On Product</b></td>
						    <td>
				               <select name="marginOnProduct"  id="marginOnProduct"  >
							        	<option value="DEFAULT_PRICE">DEFAULT_PRICE</option>   
							        	<option value="MRP_PRICE">MRP_PRICE</option>             
							   </select>
						  </td>
						 </tr>
						  <tr>
				          	<td class="label"><b>${uiLabelMap.UseEcs}</b></td>
						    <td>
				              <select name="useEcs" >
						            <option value="" selected>${facility.useEcs?if_exists}</option>
						        	<option value="Y">Y</option>  
						        	<option value="N">N</option>              
						      </select>
						  </td>
						 </tr>
	                      </table>
                    </fieldset>  
                 </section>
            <h3>Owner and Personal Info</h3>
            <section>
            	<fieldset>
				    <table cellpadding="2" cellspacing="1">
					         <tr>
		          				<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>owner Details</div></td>
					        </tr>
		       				<tr>
					          	<td class="label"><b>First Name</b></td></td>
							    <td>
		            			   <input class="h3" type="text" size="18" maxlength="20" name="firstName" id="firstName" />
		          				</td>
					        </tr>
					         <tr>
					            <td class="label"><b>Middle Name</b></td></td>
		       	  				<td>
		            			   <input class="h3" type="text" size="18" maxlength="20" name="middleName" id="middleName" />
		          				</td>
					        </tr>
					         <tr>
					            <td class="label"><b>Last Name</b></td></td>
		       	  				<td>
		            				<input class="h3" type="text" size="18" maxlength="20" name="lastName" id="lastName" />
		          				</td>
					        </tr>
					         <tr>
		       					<td>&nbsp;</td>
					        </tr>
					         <tr>
		          				<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Group Details</div></td>
					        </tr>
					         <tr>
					            <td class="label"><b>Group Name*</b></td></td>
		          				<td>
									<input class="h3" type="text" size="18" maxlength="20" name="groupName" id="groupName" />
		          				</td>
		          				<input type="hidden" name="stateProvinceGeoId" id="stateProvinceGeoId" size="30" maxlength="60" value="IND" autocomplete="off"/>
	          				<tr>
		       					<td>&nbsp;</td>
					        </tr>
					         <tr>
		          				<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Personal Details</div></td>
					        </tr>
									<tr>
									    <td class="label"><b> Address1</b></td>
									    <td>
									      	<input type="text" name="address1" id="address1" size="30" maxlength="60" autocomplete="off" />
									    </td>
									</tr>
									<tr>
									    <td class="label"><b> Address2</b></td>
									    <td>
									      	<input type="text" name="address2" id="address2" size="30" maxlength="60" autocomplete="off" />
									    </td>
									</tr>
									<tr>
									    <td class="label"><b> City</b></td>
									    <td>
									      	<input type="text" name="city" id="city" size="30" maxlength="60" autocomplete="off" />
									    </td>
									</tr>
									<tr>
									    <td class="label"><b> Postal Code</b></td>
									    <td>
									      	<input type="text" name="postalCode" id="postalCode" size="30" maxlength="60" autocomplete="off" />
									    </td>
									</tr>
									<tr>
									    <td class="label"><b> email Address</b></td>
									    <td>
									      	<input type="text" name="emailAddress" id="emailAddress" size="30" maxlength="60" autocomplete="off" />
									    </td>
									</tr>
									<tr>
									    <td class="label"><b> Phone Number</b></td>
									    <td>
									      	<input type="text" name="contactNumber" id="contactNumber" size="30" maxlength="60" autocomplete="off"/>
									    </td>
									</tr>
									<tr>
									    <td class="label"><b>Mobile Number</b></td>
									    <td>
									      	<input type="text" name="mobileNumber" id="mobileNumber" size="30" maxlength="60" autocomplete="off" />
									    </td>
									</tr>
									<tr>
									    <td>
									      	<input type="hidden" name="countryGeoId" id="countryGeoId" size="30" value="IND" autocomplete="off"/>
									    </td>
									</tr>
					        </tr>
		                 </table>
                    </fieldset>  
               </section>
                      <h3>Facility Payment Default</h3>
			          <section>
				          <fieldset>
				            <table cellpadding="2" cellspacing="1">
							         <tr>
				          				 <td class="label"><b>Payment Method Type</b></td>
									     <td>
									      <select name="paymentMethodTypeId" id="paymentMethodTypeId">
										      <option value='CASH_PAYIN'>CSH</option>
										      <option value='CHALLAN_PAYIN'>CHLN</option>
										      <option value='CHEQUE_PAYIN'>CHQ</option>
					      		          </select>
									    </td>
							        </tr>
							         <tr>
				          				 <td class="label"><b>Bank AccNo</b></td>
									     <td>
									      	<input type="text" name="finAccountCode" id="finAccountCode" size="30" maxlength="60"  autocomplete="off"/>
									    </td>
							        </tr>
							         <tr>
				          				 <td class="label"><b>Bank Name</b></td>
									     <td>
									      	<input type="text" name="finAccountName" id="finAccountName" size="30" maxlength="60" autocomplete="off"/>
									    </td>
							        </tr>
							        <tr>
				          				 <td class="label"><b>Branch Name</b></td>
									     <td>
									      	<input type="text" name="finAccountBranch" id="finAccountBranch" size="30" maxlength="60"  autocomplete="off"/>
									    </td>
							        </tr>
							        <tr>
				          				 <td class="label"><b>IFSC Code</b></td>
									     <td>
									      	<input type="text" name="ifscCode" id="ifscCode" size="30" maxlength="60"  autocomplete="off"/>
									    </td>
							        </tr>
							        <tr>
				          				 <td class="label"><b>From Date</b></td>
									     <td>
									      	<input type="text" name="fDate" id="fDate" size="30" maxlength="60" autocomplete="off"/>
									    </td>
							        </tr>
						  </table>
						  </fieldset>
                        </section>
                     <h3>Fixed Deposit Details</h3>
			          <section>
					        <fieldset>
					            <table cellpadding="2" cellspacing="1">
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
			