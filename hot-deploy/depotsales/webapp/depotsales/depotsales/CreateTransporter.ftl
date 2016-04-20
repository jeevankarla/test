
<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/flexselect.css</@ofbizContentUrl>">
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/liquidmetal.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/jquery.flexselect.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.js</@ofbizContentUrl>"></script>
<link type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.css</@ofbizContentUrl>" rel="Stylesheet" />

<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />
<link href="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.css</@ofbizContentUrl>" rel="stylesheet">
<script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.js</@ofbizContentUrl>"></script>


<script type="application/javascript">
	function validateEmail(email) { 
    	var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    	return re.test(email);
	} 
	
	  var branchList;
	var finalAddressMap = {};
	var finalAddressList = [];
	
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
						/*
							 if( (panId).length < 1 ) {
						    	$('#USER_PANID').css('background', 'yellow'); 
						       	setTimeout(function () {
						        $('#USER_PANID').css('background', 'white').focus(); 
						       	}, 800);
						    	return false;
					    	}
					    */	
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
                    return true;
                },
                onFinished: function (event, currentIndex)
                {
					var form = ($(this)).parent();
					
					     var address1 = $("#address1").val();
	                	 var city = $("#city").val();
	                	 var email = $("#emailAddress").val();
	                	 var Altemail = $("#AltemailAddress").val();
	                	 
                       var groupName = $("#groupName").val();
				    	 if( (groupName).length < 1 ) {
					    	$('#groupName').css('background', 'yellow'); 
					       	setTimeout(function () {
					        $('#groupName').css('background', 'white').focus(); 
					       	}, 800);
					    	return false;
				    	}
				    	
				    	
				    	 var productStoreId = $("#productStoreId").val();
				    	 
				    	 //alert(productStoreId);
				    	 
				    	 if( (productStoreId) == null ) {
					      /*	$('#productStoreId').css('background', 'yellow'); 
					       	setTimeout(function () {
					        $('#productStoreId').css('background', 'white').focus(); 
					       	}, 800); */
					       	
					       	alert("Please Select Branch");
					       	
					    	return false;
				    	}
                	
                	
                	         $("#personalDetailsId").val(JSON.stringify(finalAddressMap)); 
					   
					    
                	form.submit();
                }
            });
	}); 
	
	
			$(document).ready(function(){
	
	      	var branchAutoJSON = ${StringUtil.wrapString(branchJSON)!'[]'};
			var catgoryOptionList=[];
			if(branchAutoJSON != undefined && branchAutoJSON != ""){
			
				$.each(branchAutoJSON, function(key, item){
					catgoryOptionList.push('<option value="'+item.value+'">' +item.label+'</option>');
				});
            }
	     $('#productStoreId').html(catgoryOptionList.join('')); 
	
	                $("#productStoreId").multiselect({
					    minWidth : 250,
						height: 300,
						selectedList: 4,
						show: ["bounce", 100],
						position: {
							my: 'left bottom',
							at: 'left top'
			          }
		            });
                return false;
		});
	
	  
	    function populateDropDown(){
	    
	      branchList = $("#noFoBranches").val();
                	    
           var tempList=[];
      	 if(branchList != undefined && branchList != ""){
			 var i;
			for (i = 1; i <= branchList; i++) { 
                tempList.push('<option value="'+i+'">Branch  '+i+' Details</option>');
             }
    	    }
   	      $('#selectBranch').html(tempList.join('')); 
	    }
	    
	   function addAdressList(){
	  
	     var map= {};
	     var tempList = [];
	     
	     var selectedBranch = $("#selectBranch").val();
	     var address1 = $("#address1").val();
	     var address2 = $("#address2").val();
	     var city = $("#city").val();
	     var postalCode = $("#postalCode").val();
	     var emailAddress = $("#emailAddress").val();
	     var AltemailAddress = $("#AltemailAddress").val();
	     var countryCode = $("#countryCode").val();
	     var mobileNumber = $("#mobileNumber").val();
	     var contactNumber = $("#contactNumber").val();
	     var countryGeoId = $("#countryGeoId").val();
	     
          	     
	     map['selectedBranch'] = selectedBranch;
	     map['address1'] = address1;
	     map['address2'] = address2;
	     map['city'] = city;
	     map['postalCode'] = postalCode;
	     map['emailAddress'] = emailAddress;
	     map['AltemailAddress'] = AltemailAddress;
	     map['countryCode'] = countryCode;
	     map['mobileNumber'] = mobileNumber;
	     map['contactNumber'] = contactNumber;
	     map['countryGeoId'] = countryGeoId;
	      
         tempList = map;
	   
	    finalAddressMap[selectedBranch] = tempList;
	   
	   }
	
	   function addressStoredList(){
	   
	   
	   var selectedBranch = $("#selectBranch").val();
	   var addressList = finalAddressMap[selectedBranch];
	 
	   if(addressList != undefined && addressList != ""){
	     $("#selectBranch").val(addressList.selectedBranch);
	     $("#address1").val(addressList.address1);
	     $("#address2").val(addressList.address2);
	     $("#city").val(addressList.city);
	     $("#postalCode").val(addressList.postalCode);
	     $("#emailAddress").val(addressList.emailAddress);
	     $("#AltemailAddress").val(addressList.AltemailAddress);
	     $("#countryCode").val(addressList.countryCode);
	     $("#mobileNumber").val(addressList.mobileNumber);
	     $("#contactNumber").val(addressList.contactNumber);
	     $("#countryGeoId").val(addressList.countryGeoId);
	   
	  }else{
  	   
  	    // $("#selectBranch").val('');
  	     $("#address1").val('');
  	     $("#address2").val('');
  	     $("#city").val('');
  	     $("#postalCode").val(0);
  	     $("#emailAddress").val('');
  	     $("#AltemailAddress").val('');
  	     $("#countryCode").val('');
  	     $("#mobileNumber").val('');
  	     $("#contactNumber").val('');
  	     //$("#countryGeoId").val('');
  	   
  	   }
	   
	   }	
	   
	   
	   
	    function clearallSelectedData(){
	    
	      
	       finalAddressMap = [];
   	      
   	     $('#selectBranch').html(''); 
  	     $("#address1").val('');
  	     $("#address2").val('');
  	     $("#city").val('');
  	     $("#postalCode").val(0);
  	     $("#emailAddress").val('');
  	     $("#AltemailAddress").val('');
  	     $("#countryCode").val('');
  	     $("#mobileNumber").val('');
  	     $("#contactNumber").val('');
  	     //$("#countryGeoId").val('');
  	     
  	     $("#noFoBranches").val('');
	    
	    
	    }
	
</script>
	<form id="EditPartyGroup"  action="<@ofbizUrl>createTransporter</@ofbizUrl>" name="EditPartyGroup" method="post">
	    <div id="wizard-2">
        <h3>Transporter Information</h3>
        <section>
          <fieldset>
	            <table cellpadding="2" cellspacing="1">
	            
	                     <#-- <tr>
				        <td class="label"></td>
          				 <td>
						    <select name="suppRole" id="suppRole" >
				              <#assign suppRoles = delegator.findByAnd("PartyClassificationGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyClassificationTypeId", "SUPPLIER_ROLE"))>
	                            
	                             <#list suppRoles as eachRole>
	                            <option value="${eachRole.partyClassificationGroupId}" >${eachRole.description}</option>
	                          </#list>
				            </select>
				            <td>
				        </tr>
						<tr>-->
						
						<tr>
				        <td class="label"><b> Transporter Name*</b></td>
				        <td>
        		 			<input class="h3" type="text" size="18" maxlength="100" name="groupName" id="groupName"  />
          				</td>
				        </tr>
	            
	                    <tr>
				        <td class="label"><b> Branch Name*</b></td>
				        <td>
        		 			<select id="productStoreId" name="productStoreId"  class='h4' multiple="multiple" onblur="javascript:branchesList();>
						</select>
          				</td>
				        </tr>
						
						
						
				   <#--     <tr>
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
				        </tr>-->
				        <input type="hidden" name="stateProvinceGeoId" id="stateProvinceGeoId" size="30" maxlength="60" value="IND" autocomplete="off"/>
	             
	                     <tr>
				        <td class="label"><b>Pan Number</b></td>
				        <td>
        		 			<input class="h3" type="text" size="18" maxlength="100" name="USER_PANID" id="USER_PANID" onblur="javascript:partyIdentificationVal();" />
          				</td>
				        </tr>
				        <tr>	            
						    <td class="label"><b> Role Type Id</b></td>
						    <#assign roleTypes = delegator.findByAnd("RoleType", Static["org.ofbiz.base.util.UtilMisc"].toMap("roleTypeId", "TRANSPORT_CONTRACTOR"))>
						   <td>
						    <select name="roleTypeId" id="roleTypeId"  onchange="javascript:vendorValidation();" >
				              <#list roleTypes as roleType>
	                            <option value="${roleType.roleTypeId}" <#if "${roleType.roleTypeId}" == roleTypeId?if_exists>selected="selected"</#if>>${roleType.description}</option>
	                          </#list>
				            </select>
				            <td>
						</tr>
	             
	              </table>
            </fieldset>  
          </section>
      <#--      <h3>Personal Information</h3>
            <section>
            	<fieldset>
				    <table cellpadding="2" cellspacing="1">
		       			           <tr>
		       			           		<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Personal Details</div></td>
					               </tr>
					               
					              
					              <tr>
									    <td class="label"><FONT COLOR="red">*</font><b>No Of Branches</b></td>
									    <td>
									      	<input type="text" name="noFoBranches" id="noFoBranches" size="30" maxlength="60" autocomplete="off"  onblur="javascript:populateDropDown();"/>
									    </td>
									</tr>
					              
					               <tr>
									    <td class="label"><b>Selected Branches</b></td>
									    <td>
									      	 <select id="selectBranch" name="selectBranch"  class='h4' onchange="javascript:addressStoredList();"> 
									    </td>
									     
									</tr>
					              
					              
					            <#--   <tr>
									    <td class="label"><b>Selected Branches</b></td>
									    <td>
									      	 <select id="selectBranch" name="selectBranch"  class='h4' onchange="javascript:addressStoredList();">
									    </td>
									</tr>-->
					               
							<#--		<tr>
									    <td class="label"><FONT COLOR="red">*</font><b>Address1</b></td>
									    <td>
									      	<input type="text" name="address1" id="address1" size="30" maxlength="60" autocomplete="off"  onblur="javascript:addAdressList();"/>
									    </td>
									</tr>
									<tr>
									    <td class="label"><b> Address2</b></td>
									    <td>
									      	<input type="text" name="address2" id="address2" size="30" maxlength="60" autocomplete="off" onblur="javascript:addAdressList();" />
									    </td>
									</tr>
									<tr>
									    <td class="label"><FONT COLOR="red">*</font><b> City</b></td>
									    <td>
									      	<input type="text" name="city" id="city" size="30" maxlength="60" autocomplete="off" onblur="javascript:addAdressList();"/>
									    </td>
									</tr>
									<tr>
									    <td class="label"><b> Postal Code</b></td>
									    <td>
									      	<input type="text" name="postalCode" id="postalCode" size="30" maxlength="60" value="0" autocomplete="off" onblur="javascript:addAdressList();"/>
									    </td>
									</tr>
									<tr>
									    <td class="label"><b> E-mail Address</b></td>
									    <td>
									      	<input type="text" name="emailAddress" id="emailAddress" size="30" maxlength="60" autocomplete="off" onblur="javascript:addAdressList();"/>
									    </td>
									</tr>
									<tr>
									    <td class="label"><b>Alternative E-mail Address</b></td>
									    <td>
									      	<input type="text" name="AltemailAddress" id="AltemailAddress" size="30" maxlength="60" autocomplete="off" onblur="javascript:addAdressList();"/>
									    </td>
									</tr>
									<tr>
									    <td class="label"><b>Country Code</b></td>
									    <td>
									      	<input type="text" name="countryCode" id="countryCode" size="5" maxlength="60" autocomplete="off" onblur="javascript:addAdressList();"/>
									    </td>
									</tr>
									<tr>
									    <td class="label"><b>Mobile Number</b></td>
									    <td>
									      	<input type="text" name="mobileNumber" id="mobileNumber" size="15" maxlength="10" autocomplete="off" onblur="javascript:addAdressList();"/>
									    </td>
								   </tr>
									<tr>
									    <td class="label"><b>Contact Number</b></td>
									    <td>
									      	<input type="text" name="contactNumber" id="contactNumber" size="15" maxlength="15" autocomplete="off" onblur="javascript:addAdressList();"/>
									    </td>
								  </tr>
									<tr>
									    <td>
									      	<input type="hidden" name="countryGeoId" id="countryGeoId" size="30" value="IND" autocomplete="off"/>
									    </td>
									</tr>
									
									<tr>
									    <td>
									      	<input type="hidden" name="personalDetailsId" id="personalDetailsId" size="30" value="IND" autocomplete="off"/>
									    </td>
									</tr>
									
									<tr>
									    <td class="label"></td>
									    <td>
									      	  <input type="button" class="smallSubmit"  value="Clear All Selected Data" onclick="javascript:clearallSelectedData();"/>
									    </td>
									     
									</tr>
									
									
									
					        </tr>
		                 </table>
                    </fieldset>  
               </section>-->
  
            <#--    <h3>BANK AND IDENTIFICATION DETAILS</h3>
            <section>
            	<fieldset>
				    <table cellpadding="2" cellspacing="1">
		       			           <tr>
		       			           		<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Bank Details</div></td>
					               </tr>
									<tr>
									    <td class="label"><b> Account No :</b></td>
									    <td>
									      	<input type="text" name="accNo" id="accNo" size="30" maxlength="60" autocomplete="off" />
									    </td>
									</tr>
							<#--		<tr>
									    <td class="label"><b> Account Name :</b></td>
									    <td>
									      	<input type="text" name="accName" id="accName" size="30" maxlength="60" autocomplete="off" />
									    </td>
									</tr>
									<tr>
									    <td class="label"><b>Account Branch :</b></td>
									    <td>
									      	<input type="text" name="accBranch" id="accBranch" size="30" maxlength="60" autocomplete="off" />
									    </td>
									</tr>
									<tr>
									    <td class="label"><b> Ifsc Code :</b></td>
									    <td>
									      	<input type="text" name="IfscCode" id="IfscCode" size="30" maxlength="60" value="0" autocomplete="off" />
									    </td>
									</tr>
									
							      <#--   <tr>
							        <td class="label"><b>Pan Number :</b></td>
							        <td>
			        		 			<input class="h3" type="text" size="18" maxlength="100" name="USER_PANID" id="USER_PANID" onblur="javascript:partyIdentificationVal();" />
			          				</td>
							        </tr>-->
							    <#--    <tr>
							         <td class="label"><b>Tin Number :</b></td>
							        <td>
			        		 			<input class="h3" type="text" size="18" maxlength="100" name="USER_TINNUM" id="USER_TINNUM" onblur="javascript:partyIdentificationVal();" />
			          				</td>
							        </tr>
							        <tr>
							         
			                        <td class="label"><b>Cst Number :</b></td>
							        <td>
			        		 			<input class="h3" type="text" size="18" maxlength="100" name="USER_CSTNUM" id="USER_CSTNUM"  />
			          				</td>
							        </tr>
							        							        
							         <tr>
			                        <td class="label" id="ADHLABEL"><b>Aadhar Number :</b></td>
							        <td>
			        		 			<input class="h3" type="text" size="18" maxlength="100" name="ADR_NUMBER" id="ADR_NUMBER"  />
			          				</td>
							        </tr>-->
									
					        </tr>
		                 </table>
                    </fieldset>  
               </section>
               
               
                </form>