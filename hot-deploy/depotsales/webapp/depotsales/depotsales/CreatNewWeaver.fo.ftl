
<link href="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.css</@ofbizContentUrl>" rel="stylesheet">
<script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.js</@ofbizContentUrl>"></script>
	
	
	
<style type="text/css">
	 	
		.table-style-9{
		    max-width: 650px;
		    max-height: 350px;
		    max-right: 10px;
			margin-bottom: -30px;
		    padding: 30px;
		    box-shadow: 1px 1px 25px rgba(0, 0, 0, 0.35);
		    border-radius: 10px;
		    border: 2px solid #305A72;
		}
</style>
<script type="application/javascript">
function datepick()	{
		$( "#daoDate" ).datetimepicker({
			dateFormat:'yy-mm-dd',
			showSecond: true,
			timeFormat: 'hh:mm:ss',
			//onSelect: function(onlyDate){ // Just a work around to append current time without time picker
	        //    var nowTime = new Date(); 
	        //    onlyDate=onlyDate+" "+nowTime.getHours()+":"+nowTime.getMinutes()+":"+nowTime.getSeconds();
	        //    $('#transactionDate').val(onlyDate);
	        //},
	        changeMonth: false,
			numberOfMonths: 1});		
		$('#ui-datepicker-div').css('clip', 'auto');
	}		
		
	
	function validateEmail(email) { 
    	var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    	return re.test(email);
	} 
    $(document).ready(function(){
 				  $('#firstName').hide();
			      $('#frstNamelabel').hide();
				  $("#midName").hide();
				  $('#midNamelabel').hide();
				  $("#lastName").hide();
				  $('#lastNamelabel').hide();
  				  $('#daoDatelabel').hide();
			      $('#daoDate').hide();
			      $('#salutationlabel').hide();
			      $('#salutation').hide();
				  $('#USER_ADHNUMBER').hide();
			      $('#ADHLABEL').hide();
      	      $("#wizard-2").steps({
                headerTag: "h3",
                bodyTag: "section",
                transitionEffect: "slideLeft",
                onStepChanging: function (event, currentIndex, newIndex)
                {	
                	if(currentIndex==0){	
                			var  partyClassificationTypeId      =$( "#partyClassificationTypeId      option:selected" ).val();
                	
                			if(partyClassificationTypeId == "INDIVIDUAL_WEAVERS"){
                			
                			var firstName1=$('#firstName').val();
			                	if( (firstName1).length < 1 ) {
								    	$('#firstName').css('background', 'red'); 
								        jQuery('#firstName').after("<div class='FNLabel'><font color='red'>Please Enter FirstName.</font></div>");
								       	setTimeout(function () {
								        $('#firstName').css('background', 'white').focus(); 
								        $('.FNLabel').remove();
								       	}, 900);
								    	return false;
							    	}
				    		}else{
				    		var groupName=$('#groupName').val();
			                	if( (groupName).length < 1 ) {
								    	$('#groupName').css('background', 'red'); 
								    	jQuery('#groupName').after("<div class='groupLabel'><font color='red'>Please Enter GroupName.</font></div>");
								    	
								       	setTimeout(function () {
								        $('#groupName').css('background', 'white').focus(); 
								        $('.groupLabel').remove();
								       	}, 900);
								    	return false;
							    	}
				    		
				    		}  
				    		
				    		          	
                 	 }
                 	 if(currentIndex==1){
                 	                 	
                 	       var address1=$('#address1').val();
			               if( (address1).length < 1 ) {
									 $('#address1').css('background', 'red'); 
								        jQuery('#address1').after("<div class='ADDLabel'><font color='red'>Please Enter Address.</font></div>");
									 setTimeout(function () {
									        $('#address1').css('background', 'white').focus(); 
								       		 $('.ADDLabel').remove();
									     	}, 900);
								  	return false;
							   }
							    var city=$('#city').val();
			             		  if( (city).length < 1 ) {
									 $('#city').css('background', 'red'); 
									  jQuery('#city').after("<div class='CITYLabel'><font color='red'>Please Enter City.</font></div>");
									 setTimeout(function () {
									        $('#city').css('background', 'white').focus(); 
									        $('.CITYLabel').remove();
									     	}, 800);
								  	return false;
							  	 }
							  	 
							  	 
                 	                 	
                 	   }	
                 	  if(currentIndex==2){
                 	  	var  partyClassificationTypeId      =$( "#partyClassificationTypeId      option:selected" ).val();
                		if(partyClassificationTypeId == "INDIVIDUAL_WEAVERS"){
		                 	    var adrNum=$('#USER_ADHNUMBER').val();
					             		  if( (adrNum).length < 1 ) {
											 $('#USER_ADHNUMBER').css('background', 'red');
											  jQuery('#USER_ADHNUMBER').after("<div class='ADHRLabel'><font color='red'>Please Enter Adhar Number.</font></div>");
											 setTimeout(function () {
											        $('#USER_ADHNUMBER').css('background', 'white').focus(); 
											        $('.ADHRLabel').remove();
											     	}, 800);
										  	return false;
									  	 }
		               		 }
		               }
                	return true;
                },
                onFinishing: function (event, currentIndex)
                {	
	                <#--	 var passBook=$('#passBook').val();
			                	if( (passBook).length < 1 ) {
								    	$('#passBook').css('background', 'red'); 
								    	 jQuery('#passBook').after("<div class='PSBLabel'><font color='red'>Please Enter PassBook.</font></div>");
								       	setTimeout(function () {
								        $('#passBook').css('background', 'white').focus();
								        $('.PSBLabel').remove();
								       	}, 800);
								    	return false;
							    	}-->
                    return true;
                },
                onFinished: function (event, currentIndex)
                {
					var form = ($(this)).parent();
					
                	form.submit();
                }
            });
	}); 
	
	function setFeilds(){
		var  partyClassificationTypeId      =$( "#partyClassificationTypeId      option:selected" ).val();
		if(partyClassificationTypeId == "INDIVIDUAL_WEAVERS"){
			$("#firstName").show();
				 $('#salutationlabel').show();
			     $('#salutation').show();
			     $('#frstNamelabel').show();
				 $("#midName").show();
				 $('#midNamelabel').show();
				 $("#lastName").show();
				 $('#lastNamelabel').show();
			      $('#USER_ADHNUMBER').show();
			      $('#ADHLABEL').show();	      
				  $('#daoDatelabel').hide();
			      $('#daoDate').hide();
			      $('#Depotlabel').hide();
			      $('#Depot').hide();
			       $('#groupNamelabel').hide();
			      $('#groupName').hide();
			
			}else{
				  $('#salutationlabel').hide();
			      $('#salutation').hide();
				  $('#firstName').hide();
			      $('#frstNamelabel').hide();
				  $("#midName").hide();
				  $('#midNamelabel').hide();
				  $("#lastName").hide();
				  $('#lastNamelabel').hide();
			      $('#USER_ADHNUMBER').hide();
			      $('#ADHLABEL').hide();
				  $('#Depotlabel').show();
			      $('#Depot').show();
			       $('#groupNamelabel').show();
			      $('#groupName').show();
			}
		 
	}
		function setDAO(){
				var  Depot      =$( "#Depot      option:selected" ).val();
		  if(Depot=='Y'){
		  		 $('#daoDatelabel').show();
				 $("#daoDate").show();
		  }else{
		  		 $('#daoDatelabel').hide();
			      $('#daoDate').hide();
		  }
		}
		
	var stateListJSON;
 function setServiceName(selection) {
 var country=selection.value;
  jQuery.ajax({
                url: 'getCountryStateList',
                type: 'POST',
                async: true,
                data: {countryGeoId:country} ,
 				success: function(result){
 				stateListJSON = result["stateListJSON"];
 				if (stateListJSON) {	
                     var optionList;	       				        	
			        	for(var i=0 ; i<stateListJSON.length ; i++){
							var innerList=stateListJSON[i];	              			             
			                optionList += "<option value = " + innerList['value'] + " >" + innerList['label'] + "</option>";          			
			      		}//end of main list for loop
	  			}else{
			                optionList += "<option value = " + "_NA_" + " >" + "_NA_" + "</option>";          			

					 }
 					 jQuery("[name='stateProvinceGeoId']").html(optionList);

            }    
                   });
 
}


    $(document).ready(function(){
    
    var BankListJSON = ${StringUtil.wrapString(BankListJSON)!'[]'};
    
    var brachesGeoLIst = ${StringUtil.wrapString(brachesGeoLIst)!'[]'};
    
		$("#bankName").autocomplete({					
			source:  BankListJSON,
			select: function(event, ui) {
		     var selectedValue = ui.item.value;
		       $("#bankName").val(selectedValue);	
					      
					    }
		});
		
		
		$("#branch").autocomplete({					
			source:  brachesGeoLIst,
			select: function(event, ui) {
			  var selectedValue = ui.item.value;
				
				var branch = selectedValue;
				var bankName = $("#bankName").val();	
				
				var dataJson = {"branch": branch, "bankName":bankName};
					      
			   jQuery.ajax({
                url: 'getIfscCode',
                type: 'POST',
                data: dataJson,
                dataType: 'json',
               success: function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					    alert("No Branches Available");
					}else{
						var ifscCode = result["ifscCode"];
						$("#ifscCode").val(ifscCode.IfscCode);
               		}
               	}					        
					        
		});
					    }
		});
		
		
					        
			  
    
  });
  



</script>
	<form id="EditPartyGroup"  action="<@ofbizUrl>createWeaver</@ofbizUrl>" name="EditPartyGroup" method="post">
	    <div id="wizard-2">
        <h3>Group Information</h3>
        <section>
          <fieldset>
	            <table cellpadding="2" cellspacing="1" class="table-style-9"> 
	            		 <tr>
		       			       <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Basic Information</div></td>
					      </tr>
	           			 <tr>
						    <td class="label"><b> Classification :</b></td>
						     <input  type="hidden"  name="roleTypeId" id="roleTypeId" value="EMPANELLED_CUSTOMER" />
						    
						   <td>
						    <select name="partyClassificationTypeId" id="partyClassificationTypeId" onchange="setFeilds()">
				              <#list partyClassList as partyClas>
	                            <option value="${partyClas.partyClassificationTypeId}"  >${partyClas.description}</option>
	                          </#list>
				            </select>
				            </td>
						</tr>
						 <tr>
						    <td class="label" id="salutationlabel"><b>Salutation :</b></td>					    
						   <td>
						    <select name="salutation" id="salutation">
	                            <option value="Mr"  selected="selected" >Mr</option>
	                            <option value="Ms" >Ms</option>
	                            <option value="Mrs">Mrs</option>
	                            <option value="Mis" >Miss</option>
				            </select>
				            </td>
						 </tr>
				       
	                    <tr>
				        <td class="label" id="groupNamelabel"><b><FONT COLOR="red">*</font> Group Name :</b></td>
				        <td>
        		 			<input class="h3" type="text"  class="text" size="18" maxlength="100" name="groupName" id="groupName" value="vamsi" />
				       </td>
				        </tr>
				         <tr>
						    <td width="20%" id="genderlabel"><b>Gender :</b></td>
	    					 <td><input type="radio" id="gender" name="gender" value="M">Male</input> <input type="radio" id="gender" name="gender" value="F" >Female</input></td>
						 </tr>
				        <tr>
						    <td class="label" id="Depotlabel"><b>Depot :</b></td>					    
						   <td>
						    <select name="Depot" id="Depot" onchange="setDAO()">
	                            <option value="Y" >Yes</option>
	                            <option value="N"  selected="selected" >No</option>
	                            
				            </select>
				            </td>
				          <td class="label" id="daoDatelabel"><b>DAO Date :</b></td>		
						    <td >          
				            		<input class='h3' type="text" name="daoDate" id="daoDate" onmouseover='datepick()'/>           		
				            	</td>
				           
						</tr>
	                    <tr>
				         <tr>
				        <td class="label" id="frstNamelabel"><b><FONT COLOR="red">*</font> First Name :</b></td>
				        <td>
        		 			<input class="h3" type="text" size="18" maxlength="100" name="firstName" id="firstName"  />
          				</td>
				        </tr>
				         <tr>
				        <td class="label" id="midNamelabel"><b> Middle Name :</b></td>
				        <td>
        		 			<input class="h3" type="text" size="18" maxlength="100" name="midName" id="midName"  />
          				</td>
				        </tr>
				         <tr>
				        <td class="label" id="lastNamelabel"><b> Last Name :</b></td>
				        <td>
        		 			<input class="h3" type="text" size="18" maxlength="100" name="lastName" id="lastName"  />
          				</td>
				        </tr>
				       <#-- <tr>
						 <td class="label"><b> Weaver Code :</b></td>
				        <td>
        		 			<input class="h3" type="text" size="18" maxlength="100" name="weaverCode" id="weaverCode"  />
          				</td>
				        </tr>-->
				         <#--<tr>
				         <td class="label"><b>Service Tax Number</b></td>
				        <td>
        		 			<input class="h3" type="text" size="18" maxlength="100" name="USER_SERVICETAXNUM" id="USER_SERVICETAXNUM"  onblur="javascript:partyIdentificationVal();"/>
          				</td>
				        </tr>-->
	              </table>
            </fieldset>  
          </section>
            <h3>Personal Information</h3>
            <section>
            	<fieldset>
				    <table cellpadding="2" cellspacing="1" class="table-style-9">
		       			           <tr>
		       			           		<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Personal Details</div></td>
					               </tr>
									<tr>
									    <td class="label"><b><FONT COLOR="red">*</font> Address1 :</b></td>
									    <td>
									      	<input type="text" name="address1" id="address1" size="30" maxlength="60" autocomplete="off" value="hyd"/>
									    </td>
									</tr>
									<tr>
									    <td class="label"><b> Address2 :</b></td>
									    <td>
									      	<input type="text" name="address2" id="address2" size="30" maxlength="60" autocomplete="off" />
									    </td>
									</tr>
									<tr>
									    <td class="label"><b><FONT COLOR="red">*</font> City :</b></td>
									    <td>
									      	<input type="text" name="city" id="city" size="30" maxlength="60" autocomplete="off" value="bang" />
									    </td>
									</tr>
									<tr>
									    <td class="label"><b> Postal Code :</b></td>
									    <td>
									      	<input type="text" name="postalCode" id="postalCode" size="30" maxlength="60" value="0" autocomplete="off" />
									    </td>
									    </tr>
									    <tr>
									    <td class="label"><b>Caste :</b></td>
									    <td>
									    <select name="Cast" id="Cast">
				                            <option value="general" selected="selected">General</option>
				                            <option value="BC">BC</option>
				                            <option value="OBC">OBC</option>
				                            <option value="SC">SC</option>
				                            <option value="ST">ST</option>
							            </select>
							            </td>
				          
									</tr>
									<tr>
									    <td class="label"><b>E-mail Address :</b></td>
									    <td>
									      	<input type="text" name="emailAddress" id="emailAddress" size="30" maxlength="60" autocomplete="off" />
									    </td>
									</tr>
									<tr>
								      <td class="label"><b>${uiLabelMap.CommonCountry} :</b></td>
								      <td>
								        <select name="countryGeoId" id="editcontactmechform_countryId"  onchange="javascript:setServiceName(this);">
										<#assign defaultCountryGeoId = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "country.geo.id.default")>
								          <option selected="selected" value="${defaultCountryGeoId}">
								          <#assign countryGeo = delegator.findByPrimaryKey("Geo",Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId",defaultCountryGeoId))>
								          ${countryGeo.get("geoName",locale)}
								          </option>
								          <option></option>
								          ${screens.render("component://common/widget/CommonScreens.xml#countries")}
								        </select>
								      </td>
	    							</tr>
	    							 <tr>
								      <td class="label"><b>${uiLabelMap.PartyState} :</b></td>
								      <td>
								        <select name="stateProvinceGeoId" id="editcontactmechform_stateId">
										
							   			 <#assign stateAssocs = Static["org.ofbiz.common.CommonWorkers"].getAssociatedStateList(delegator,defaultCountryGeoId)>
								         <#list stateAssocs as stateAssoc>
							   					 <option value='${stateAssoc.geoId}'>${stateAssoc.geoName?default(stateAssoc.geoId)}</option>
										</#list>
								          <option></option>
								      		<#--${screens.render("component://common/widget/CommonScreens.xml#states")}-->
								        </select>
								      </td>
								    </tr>	   								 
	   								 <tr>
								      <td class="label"><b>Branch :</b></td>
								      <td>
								        <select name="productStoreId" id="productStoreId">
								         <#list productStoreDetails as eachstore>
							   					 <option value='${eachstore.payToPartyId}'>${eachstore.productStoreId}</option>
										</#list>
								          <option></option>
								      		<#--${screens.render("component://common/widget/CommonScreens.xml#states")}-->
								        </select>
								      </td>
	   								 </tr>
	   								  <tr>
								      <td class="label"><b>Cluster :</b></td>
								      <td>
								        <input type="text" name="Cluster" id="Cluster" size="15" maxlength="10" autocomplete="off" />
								        
								      </td>
	   								 </tr>
									<tr>
									    <td class="label"><b>Mobile Number :</b></td>
									    <td>
									      	<input type="text" name="mobileNumber" id="mobileNumber" size="15" maxlength="10" autocomplete="off" />
									    </td>
								   </tr>
									<tr>
									    <td class="label"><b>Contact Number :</b></td>
									    <td>
									      	<input type="text" name="contactNumber" id="contactNumber" size="15" maxlength="15" autocomplete="off"/>
									    </td>
								  </tr>
									
									
					        </tr>
		                 </table>
                    </fieldset>  
               </section>
                <h3>BANK AND IDENTIFICATION DETAILS</h3>
            <section>
            	<fieldset>
				    <table cellpadding="2" cellspacing="1" class="table-style-9">
		       			           <tr>
		       			           		<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Bank Details</div></td>
					               </tr>
					               <tr>
									    <td class="label"><b> Bank :</b></td>
									    <td>
									      	<input type="text" name="bankName" id="bankName" size="30" maxlength="60"/>
									    </td>
									</tr>
					               
					               <tr>
									    <td class="label"><b>Account Branch :</b></td>
									     <td>
						                 	<input type="text" name="branch" id="branch" size="30" maxlength="60"/>
							            </td>
									</tr>
									
									<tr>
									    <td class="label"><b> Ifsc Code :</b></td>
									    <td>
									      	<input type="text" name="ifscCode" id="ifscCode" size="30" maxlength="60" value="0"  />
									    </td>
									</tr>
									
									
									<tr>
									    <td class="label"><b> Account No :</b></td>
									    <td>
									      	<input type="text" name="accNo" id="accNo" size="30" maxlength="60" autocomplete="off" />
									    </td>
									</tr>
									<tr>
									    <td class="label"><b> Account Name :</b></td>
									    <td>
									      	<input type="text" name="accName" id="accName" size="30" maxlength="60" autocomplete="off" />
									    </td>
									</tr>
									<tr>
							        <td class="label"><b>Pan Number :</b></td>
							        <td>
			        		 			<input class="h3" type="text" size="18" maxlength="100" name="USER_PANID" id="USER_PANID" onblur="javascript:partyIdentificationVal();" />
			          				</td>
							        </tr>
							        <tr>
							         <td class="label"><b>Tin Number :</b></td>
							        <td>
			        		 			<input class="h3" type="text" size="18" maxlength="100" name="USER_TINNUMBER" id="USER_TINNUMBER" onblur="javascript:partyIdentificationVal();" />
			          				</td>
							        </tr>
							        <tr>
							         
			                        <td class="label"><b>Cst Number :</b></td>
							        <td>
			        		 			<input class="h3" type="text" size="18" maxlength="100" name="USER_CSTNUMBER" id="USER_CSTNUMBER"  />
			          				</td>
							        </tr>
							         <tr>
							         
			                        <td class="label" id="ADHLABEL"><FONT COLOR="red">*</font><b>Aadhar Number :</b></td>
							        <td>
			        		 			<input class="h3" type="text" size="18" maxlength="100" name="USER_ADHNUMBER" id="USER_ADHNUMBER"  />
			          				</td>
							        </tr>
									
					        </tr>
		                 </table>
                    </fieldset>  
               </section>
               <h3>LOOM DETAILS</h3>
            <section>
            	<fieldset>
          			<table cellpadding="2" cellspacing="1" class="table-style-9">
          			 <tr>
		       			       <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Loom Details</div></td>
					      </tr>
					       <tr>
						 <td class="label"><b> PassBook :</b></td>
				        <td>
        		 			<input class="h3" type="text" size="18" maxlength="100" name="passBook" id="passBook"  />
          				</td>
				        </tr>
				     <#list AllLoomDetails as eachloom>
				        <tr>
									    <td class="label"  width="50%"><b>${eachloom.loomTypeId} :</b></td>
									    <td  width="50%">
									      	<input type="text" name=${eachloom.loomTypeId} id=${eachloom.loomTypeId} size="30" maxlength="60" autocomplete="off" value="0"/>
									    </td>
									</tr>
				         </#list>
				         </table>
               </section>
            
                </form>
			