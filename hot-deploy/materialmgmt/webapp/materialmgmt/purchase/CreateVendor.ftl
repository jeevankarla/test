
<link href="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.css</@ofbizContentUrl>" rel="stylesheet">
<script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.js</@ofbizContentUrl>"></script>


<script type="application/javascript">
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
                		var groupName = $("#groupName").val();
                		var roleTypeId = $("#roleTypeId").val();
				    	 if( (groupName).length < 1 ) {
					    	$('#groupName').css('background', 'yellow'); 
					       	setTimeout(function () {
					        $('#groupName').css('background', 'white').focus(); 
					       	}, 800);
					    	return false;
				    	}
				    	if (roleTypeId== "SERVICE_VENDOR") {
					    	 var serviceTax = $("#USER_SERVICETAXNUM").val();
							 if( (serviceTax).length < 1 ) {
						    	$('#USER_SERVICETAXNUM').css('background', 'yellow'); 
						       	setTimeout(function () {
						        $('#USER_SERVICETAXNUM').css('background', 'white').focus(); 
						       	}, 800);
						    	return false;
					    	 }
				    	}else if (roleTypeId== "MATERIAL_VENDOR") {
					    	 var tin=$("#USER_TINNUMBER").val();
							 if( (tin).length < 1 ) {
						    	$('#USER_TINNUMBER').css('background', 'yellow'); 
						       	setTimeout(function () {
						        $('#USER_TINNUMBER').css('background', 'white').focus(); 
						       	}, 800);
						    	return false;
					    	}
						 }
				    	else{
					    	 var panId=jQuery("#USER_PANID").val();
							 if( (panId).length < 1 ) {
						    	$('#USER_PANID').css('background', 'yellow'); 
						       	setTimeout(function () {
						        $('#USER_PANID').css('background', 'white').focus(); 
						       	}, 800);
						    	return false;
					    	}
                        }
				    	 var flag =partyIdentificationVal();
				    	 var tin=$("#USER_TINNUMBER").val();
				    	 if( flag==false ) {
						    	$('#groupName').css('background', 'yellow'); 
						       	setTimeout(function () {
						        $('#groupName').css('background', 'white').focus(); 
						       	}, 800);
						    	return false;
					    }
                		return true;
                	}
                },
                onFinishing: function (event, currentIndex)
                {	
	                	 var address1 = $("#address1").val();
	                	 var city = $("#city").val();
	                	 var email = $("#emailAddress").val();
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
						    if((email).length!=0&&!validateEmail(email)){
						    	alert("invalid email");
						    	$('#email').css('background', 'yellow'); 
						       	setTimeout(function () {
						           	$('#email').css('background', 'white').focus(); 
						       	}, 800);
						    	return false;
						    }
                    return true;
                },
                onFinished: function (event, currentIndex)
                {
					var form = ($(this)).parent();
					
                	form.submit();
                }
            });
	}); 
	
</script>
	<form id="EditPartyGroup"  action="<@ofbizUrl>createVendor</@ofbizUrl>" name="EditPartyGroup" method="post">
	    <div id="wizard-2">
        <h3>Group Information</h3>
        <section>
          <fieldset>
	            <table cellpadding="2" cellspacing="1">
	                    <tr>
				        <td class="label"><b> group Name*</b></td>
				        <td>
        		 			<input class="h3" type="text" size="18" maxlength="100" name="groupName" id="groupName"  />
          				</td>
				        </tr>
						<tr>
						    <td class="label"><b> Role Type Id</b></td>
						    <#assign roleTypes = delegator.findByAnd("RoleType", Static["org.ofbiz.base.util.UtilMisc"].toMap("parentTypeId", "VENDOR"))>
						   <td>
						    <select name="roleTypeId" id="roleTypeId"  onchange="javascript:vendorValidation();" >
				              <#list roleTypes as roleType>
	                            <option value="${roleType.roleTypeId}" <#if "${roleType.roleTypeId}" == roleTypeId?if_exists>selected="selected"</#if>>${roleType.description}</option>
	                          </#list>
				            </select>
				            <td>
						</tr>
						<tr>
				        <td class="label"><b>Pan Number</b></td>
				        <td>
        		 			<input class="h3" type="text" size="18" maxlength="100" name="USER_PANID" id="USER_PANID" onblur="javascript:partyIdentificationVal();" />
          				</td>
				        </tr>
				        <tr>
				         <td class="label"><b>Tin Number</b></td>
				        <td>
        		 			<input class="h3" type="text" size="18" maxlength="100" name="USER_TINNUMBER" id="USER_TINNUMBER" onblur="javascript:partyIdentificationVal();" />
          				</td>
				        </tr>
				        <tr>
				         <td class="label"><b>Cst Number</b></td>
				        <td>
        		 			<input class="h3" type="text" size="18" maxlength="100" name="USER_CSTNUMBER" id="USER_CSTNUMBER"  />
          				</td>
				        </tr>
				         <tr>
				         <td class="label"><b>Service Tax Number</b></td>
				        <td>
        		 			<input class="h3" type="text" size="18" maxlength="100" name="USER_SERVICETAXNUM" id="USER_SERVICETAXNUM"  onblur="javascript:partyIdentificationVal();"/>
          				</td>
				        </tr>
				        <input type="hidden" name="stateProvinceGeoId" id="stateProvinceGeoId" size="30" maxlength="60" value="IND" autocomplete="off"/>
	              </table>
            </fieldset>  
          </section>
            <h3>Personal Information</h3>
            <section>
            	<fieldset>
				    <table cellpadding="2" cellspacing="1">
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
									      	<input type="text" name="postalCode" id="postalCode" size="30" maxlength="60" value="0" autocomplete="off" />
									    </td>
									</tr>
									<tr>
									    <td class="label"><b> email Address</b></td>
									    <td>
									      	<input type="text" name="emailAddress" id="emailAddress" size="30" maxlength="60" autocomplete="off" />
									    </td>
									</tr>
									<tr>
									    <td class="label"><b>Mobile Number</b></td>
									    <td>
									      	<input type="text" name="mobileNumber" id="mobileNumber" size="30" maxlength="60" autocomplete="off" />
									    </td>
								   </tr>
									<tr>
									    <td class="label"><b>Country Code</b></td>
									    <td>
									      	<input type="text" name="countryCode" id="countryCode" size="5" maxlength="60" autocomplete="off" />
									    </td>
									</tr>
									<tr>
									    <td class="label"><b>Contact Number</b></td>
									    <td>
									      	<input type="text" name="contactNumber" id="contactNumber" size="30" maxlength="60" autocomplete="off"/>
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
                </form>
			