
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
			    $('#daoDate').datepicker({
				dateFormat:'d MM, yy',
				changeMonth: true,
				numberOfMonths: 1,
				//minDate: new Date(),
				maxDate: 14,
				onSelect: function( selectedDate ) {
					$( "#effectiveDate" ).datepicker("option", selectedDate);
				}
			});
      	      $("#wizard-2").steps({
                headerTag: "h3",
                bodyTag: "section",
                transitionEffect: "slideLeft",
                onStepChanging: function (event, currentIndex, newIndex)
                {	
                
                
                
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
	
	function setFeilds(){
		var  partyClassificationTypeId      =$( "#partyClassificationTypeId      option:selected" ).val();
		if(partyClassificationTypeId == "INDIVIDUAL_WEAVERS"){
			$("#firstName").show();
			     $('#frstNamelabel').show();
				 $("#midName").show();
				 $('#midNamelabel').show();
				 $("#lastName").show();
				 $('#lastNamelabel').show();
				  $('#daoDatelabel').hide();
			      $('#daoDate').hide();
			      $('#Depotlabel').hide();
			      $('#Depot').hide();
			       $('#groupNamelabel').hide();
			      $('#groupName').hide();
			
			}else{
				  $('#firstName').hide();
			      $('#frstNamelabel').hide();
				  $("#midName").hide();
				  $('#midNamelabel').hide();
				  $("#lastName").hide();
				  $('#lastNamelabel').hide();
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
						    <td class="label"><b> Role Type Id</b></td>
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
						    <td class="label" id="salutationlabel"><b>salutation</b></td>					    
						   <td>
						    <select name="salutation" id="salutation">
	                            <option value="Mrs"  selected="selected" >Mrs</option>
	                            <option value="Mis" >Mis</option>
	                            
				            </select>
				            </td>
						
						 </tr>
				       
	                    <tr>
				        <td class="label" id="groupNamelabel"><b> Group Name*</b></td>
				        <td>
        		 			<input class="h3" type="text"  class="text" size="18" maxlength="100" name="groupName" id="groupName"  />
          				</td>
				        </tr>
				         <tr>
						    <td width="20%" id="genderlabel"><b>Gender :</b></td>
	    					 <td> <input type="radio" id="gender" name="gender" value="F" >Female</input><input type="radio" id="gender" name="gender" value="M">Male</input> </td>
						 </tr>
				        <tr>
						    <td class="label" id="Depotlabel"><b>Depot</b></td>					    
						   <td>
						    <select name="Depot" id="Depot" onchange="setDAO()">
	                            <option value="Y" >Yes</option>
	                            <option value="N"  selected="selected" >No</option>
	                            
				            </select>
				            </td>
				          <td class="label" id="daoDatelabel"><b>DAO Date :</b></td>		
						    <td >          
				            		<input class='h3' type="text" name="daoDate" id="daoDate"/>           		
				            	</td>
				           
						</tr>
	                    <tr>
				         <tr>
				        <td class="label" id="frstNamelabel"><b> First Name*</b></td>
				        <td>
        		 			<input class="h3" type="text" size="18" maxlength="100" name="firstName" id="firstName"  />
          				</td>
				        </tr>
				         <tr>
				        <td class="label" id="midNamelabel"><b> Middle Name</b></td>
				        <td>
        		 			<input class="h3" type="text" size="18" maxlength="100" name="midName" id="midName"  />
          				</td>
				        </tr>
				         <tr>
				        <td class="label" id="lastNamelabel"><b> Last Name</b></td>
				        <td>
        		 			<input class="h3" type="text" size="18" maxlength="100" name="lastName" id="lastName"  />
          				</td>
				        </tr>
				        <tr>
						 <td class="label"><b> Weaver Code*</b></td>
				        <td>
        		 			<input class="h3" type="text" size="18" maxlength="100" name="weaverCode" id="weaverCode"  />
          				</td>
				        </tr>
				         <tr>
						 <td class="label"><b> passBook*</b></td>
				        <td>
        		 			<input class="h3" type="text" size="18" maxlength="100" name="passBook" id="passBook"  />
          				</td>
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
									    <td class="label"><b> E-mail Address</b></td>
									    <td>
									      	<input type="text" name="emailAddress" id="emailAddress" size="30" maxlength="60" autocomplete="off" />
									    </td>
									</tr>
									<tr>
								      <td class="label"><b>${uiLabelMap.CommonCountry}</b></td>
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
								      <td class="label"><FONT COLOR="#045FB4"><b>${uiLabelMap.PartyState}</b></FONT></td>
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
								      <td class="label"><b>Sore :</b></td>
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
									    <td class="label"><b>Mobile Number</b></td>
									    <td>
									      	<input type="text" name="mobileNumber" id="mobileNumber" size="15" maxlength="10" autocomplete="off" />
									    </td>
								   </tr>
									<tr>
									    <td class="label"><b>Contact Number</b></td>
									    <td>
									      	<input type="text" name="contactNumber" id="contactNumber" size="15" maxlength="15" autocomplete="off"/>
									    </td>
								  </tr>
									
									
					        </tr>
		                 </table>
                    </fieldset>  
               </section>
                <h3>BANK DETAILS</h3>
            <section>
            	<fieldset>
				    <table cellpadding="2" cellspacing="1" class="table-style-9">
		       			           <tr>
		       			           		<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Bank Details</div></td>
					               </tr>
									<tr>
									    <td class="label"><b> Account No</b></td>
									    <td>
									      	<input type="text" name="accNo" id="accNo" size="30" maxlength="60" autocomplete="off" />
									    </td>
									</tr>
									<tr>
									    <td class="label"><b> Account Name</b></td>
									    <td>
									      	<input type="text" name="accName" id="accName" size="30" maxlength="60" autocomplete="off" />
									    </td>
									</tr>
									<tr>
									    <td class="label"><b>Account Branch</b></td>
									    <td>
									      	<input type="text" name="accBranch" id="accBranch" size="30" maxlength="60" autocomplete="off" />
									    </td>
									</tr>
									<tr>
									    <td class="label"><b> Ifsc Code</b></td>
									    <td>
									      	<input type="text" name="IfscCode" id="IfscCode" size="30" maxlength="60" value="0" autocomplete="off" />
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
				     <#list AllLoomDetails as eachloom>
				        <tr>
									    <td class="label"  width="50%"><b>${eachloom.loomTypeId}</b></td>
									    <td  width="50%">
									      	<input type="text" name=${eachloom.loomTypeId} id=${eachloom.loomTypeId} size="30" maxlength="60" autocomplete="off" value="0"/>
									    </td>
									</tr>
				         </#list>
				         </table>
               </section>
            
                </form>
			