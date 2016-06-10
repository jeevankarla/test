
<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/flexselect.css</@ofbizContentUrl>">
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/liquidmetal.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/jquery.flexselect.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.js</@ofbizContentUrl>"></script>
<link type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.css</@ofbizContentUrl>" rel="Stylesheet" />

<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />
<link href="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.css</@ofbizContentUrl>" rel="stylesheet">
<script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.js</@ofbizContentUrl>"></script>





<style type="text/css">
.myTable { 
  width: 100%;
  text-align: left;
  background-color: lemonchiffon;
  border-collapse: collapse; 
  }
.myTable th { 
  background-color: goldenrod;
  color: white; 
  }
.myTable td, 
.myTable th { 
  padding: 10px;
  border: 1px solid goldenrod; 
  }
  
  
  
  
  
</style>



<script>

var data;

function getFecilityAddressDetailAjax(){

          var supplierId = $("#createdSupplierId").val()

          //alert(supplierId);
          
          $('div#orderSpinn').html('<img src="/images/gears.gif" height="50" width="50">');

         var dataJson = {"supplierId": supplierId};
					      
			   jQuery.ajax({
                url: 'getFacilityAddressAjax',
                type: 'POST',
                data: dataJson,
                dataType: 'json',
               success: function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					    alert("Error While getting Details");
					}else{
						data = result["facilityAddressJSON"];
						
//						alert(data);

                       if(data.length != 0)
                       {
                        $('div#orderSpinn').html('');
						drawTable(data);
					   }
					   else
					   {
					    $('div#orderSpinn').html('');
					    $("#supplierInfo").show().delay(5000).fadeOut();
						}
						
               		}
               	}					        
					        
		});
         


}



function drawTable(data) {

    for (var i = 0; i < data.length; i++) {
            drawRow(data[i]);
    }
    
}



function drawRow(rowData) {



    var row = $("<tr />")
    $("#addressTable").append(row); 
    
    
    //=====view Address =========
    
    
    var facilityName = '\'' + rowData.facilityName + '\'';
    var Naddress1 = '\'' + rowData.Naddress1 + '\'';
    var Naddress2 = '\'' + rowData.Naddress2 + '\'';
    var Ncity = '\'' + rowData.Ncity + '\'';
    var NcountryGeoId = '\'' + rowData.NcountryGeoId + '\'';
    var NstateProvinceGeoId = '\'' + rowData.NstateProvinceGeoId + '\'';
    var NcontactMechPurposeTypeId = '\'' + rowData.NcontactMechPurposeTypeId + '\'';
    var NpostalCode = '\'' + rowData.NpostalCode + '\'';
    var Taddress1 = '\'' + rowData.Taddress1 + '\'';
    var Taddress2 = '\'' + rowData.Taddress2 + '\'';
    var Tcity = '\'' + rowData.Tcity + '\'';
    var TcountryGeoId = '\'' + rowData.TcountryGeoId + '\'';
    var TstateProvinceGeoId = '\'' + rowData.TstateProvinceGeoId + '\'';
    var TcontactMechPurposeTypeId = '\'' + rowData.TcontactMechPurposeTypeId + '\'';
    var TpostalCode = '\'' + rowData.TpostalCode + '\'';
    var facilityName = '\'' + rowData.facilityName + '\'';
    var facilityId = '\'' + rowData.facilityId + '\'';
    var facilityTypeId = '\'' + rowData.facilityTypeId + '\'';
    
    var viewButton = rowData.facilityName+"/"+rowData.Ncity;
    
    var customMethod = "javascript:viewFacilityAddressDetail("+ Naddress1 + ","+ Naddress2 + ","+ Ncity + ","+ NcountryGeoId + ","+ NstateProvinceGeoId + ","+ NcontactMechPurposeTypeId + ","+ NpostalCode + ","+ Taddress1 + ","+ Taddress2 + ","+ Tcity + ","+ TcountryGeoId + ","+ TstateProvinceGeoId + ","+ TcontactMechPurposeTypeId + ","+ TpostalCode + ","+facilityName+")";
    var inputbox ='<input type=button name="viewAddress" id=viewAddress value='+viewButton+' onclick="'+customMethod+'">';
    row.append($("<td>" +  inputbox  +"</td>"));
     
  
     //=====Edit Address =========
     
     var NcontactMechId = '\'' + rowData.NcontactMechId + '\'';
      var TcontactMechId = '\'' + rowData.TcontactMechId + '\'';
     
      var editAddress = "javascript:editFaciAddress("+ Naddress1 + ","+ Naddress2 + ","+ Ncity + ","+ NcountryGeoId + ","+ NstateProvinceGeoId + ","+ NcontactMechPurposeTypeId + ","+ NpostalCode + ","+ Taddress1 + ","+ Taddress2 + ","+ Tcity + ","+ TcountryGeoId + ","+ TstateProvinceGeoId + ","+ TcontactMechPurposeTypeId + ","+ TpostalCode + ","+facilityName+","+NcontactMechId+","+TcontactMechId+","+facilityId+","+facilityTypeId+")";
    var editFaciAddress ='<input type=button name="EditFac" id=EditFac value=EditFacilityAddress onclick="'+editAddress+'">';
    row.append($("<td>" +  editFaciAddress  +"</td>"));
  
  
    //================Remove Row ==============
  
     

    var NcontactMechId = '\'' + rowData.NcontactMechId + '\'';    
    
    var TcontactMechId = '\'' + rowData.TcontactMechId + '\'';    
    
    var removeAddress = "javascript:removeFacilityAddress("+ facilityId + ","+ NcontactMechId + ","+TcontactMechId+")";
    var removeFaciAddress ='<input type=button name="removeFaci" id=removeFaci value="Remove" onclick="'+removeAddress+'">';
    
    row.append($("<td>" +  removeFaciAddress  +"</td>"));
  
   
     $("#approveOrder").hide();
  
}



function editFaciAddress( Naddress1 , Naddress2 , Ncity , NcountryGeoId , NstateProvinceGeoId , NcontactMechPurposeTypeId , NpostalCode , Taddress1 , Taddress2 , Tcity , TcountryGeoId , TstateProvinceGeoId , TcontactMechPurposeTypeId , TpostalCode , facilityName,NcontactMechId,TcontactMechId,facilityId,facilityTypeId){


          $("#facicontactMechType").val(facilityTypeId);
	     var address1 = $("#Faddress1").val(Naddress1);
	     var address2 = $("#Faddress2").val(Naddress2);
	     var city = $("#Fcity").val(Ncity);
	     var postalCode = $("#postalCode").val(NpostalCode);
	     var facilityName = $("#facilityName").val(facilityName);
	     var FcontactNumber = $("#FcontactNumber").val();
	     
	     document.getElementById("facicontactMechType").value = facilityTypeId;
	    
	    
	    var countryelement = document.getElementById('editcontactmechform_countryId');
             countryelement.value = NcountryGeoId;
	     var stateelement = document.getElementById('editcontactmechform_stateId');
             
          /*   if(NstateProvinceGeoId.includes('IN'))
             stateelement.value = NstateProvinceGeoId;
             else
             stateelement.value = 'IN-'+NstateProvinceGeoId;
             */
          var Tcountryelement = document.getElementById('TFeditcontactmechform_countryId');
             Tcountryelement.value = TcountryGeoId;
	     var Tstateelement = document.getElementById('TFeditcontactmechform_stateId');
            /* 
             if(TstateProvinceGeoId.includes('IN'))
             Tstateelement.value = TstateProvinceGeoId;
             else
	          Tstateelement.value = 'IN-'+TstateProvinceGeoId;
	         */
	     
	     var TFaddress1 = $("#TFaddress1").val(Taddress1);
	     var TFaddress2 = $("#TFaddress2").val(Taddress2);
	     var TFcity = $("#TFcity").val(Tcity);
	     var TFpostalCode = $("#TFpostalCode").val(TpostalCode);
          
          $("#TcontactMechId").val(TcontactMechId);
          $("#NcontactMechId").val(NcontactMechId);
          $("#facilityId").val(facilityId);
          
          


}


//================Remove row=============


function removeFacilityAddress(facilityId,NcontactMechId,TcontactMechId){
      
         var dataJson = {"facilityId": facilityId,"NcontactMechId":NcontactMechId,"TcontactMechId":TcontactMechId};
					      
			   jQuery.ajax({
                url: 'deleteFacilityAjax',
                type: 'POST',
                data: dataJson,
                dataType: 'json',
               success: function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					    alert("Error While getting Details");
					}else{
					  
					  $("#facilityCreated").html("Facility Has Been Removed Successfully..");
		               $("#facilityCreated").show().delay(5000).fadeOut();
					  
					  
               		}
               	}					        
					        
		}); 
         

         $('#addressTable tr').click(function () {
           var rowIndex = $('#addressTable tr').index(this); 
             if(rowIndex != -1)
              document.getElementById("addressTable").deleteRow(rowIndex);  
          });

}


</script>


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
            
            $("#supplierInfo").hide();
             $("#facilityCreated").hide();
            
            
             $("#open_popup").click(function(){
		    
             	$("#popup").css("display", "block");
             });
			 $("#close_popup").click(function(){
             	$("#popup").css("display", "none");
           	 });
            
            
            
            
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
	    
	//===========Faci Address==============
	
	    var addressFaciMap = {};
    
    function storeFacilityValues(){
	   
	   
	      $('div#orderSpinn').html('<img src="/images/gears.gif" height="50" width="50">');
	   
	     
	     var supplierId = $("#createdSupplierId").val();
	     var address1 = $("#Faddress1").val();
	     var address2 = $("#Faddress2").val();
	     var city = $("#Fcity").val();
	     var postalCode = $("#postalCode").val();
	     var facilityName = $("#facilityName").val();
	     var facicontactMechType = $("#facicontactMechType").val();
	     var FcontactNumber = $("#FcontactNumber").val();
	     
	     var country = $("#editcontactmechform_countryId").find('option:selected').val();
         var state = $("#editcontactmechform_stateId").find('option:selected').val();	  
          var TFcountry = $("#TFeditcontactmechform_countryId").find('option:selected').val();
         var TFstate = $("#TFeditcontactmechform_stateId").find('option:selected').val();	     
	     
	     var TFaddress1 = $("#TFaddress1").val();
	     var TFaddress2 = $("#TFaddress2").val();
	     var TFcity = $("#TFcity").val();
	     var TFpostalCode = $("#TFpostalCode").val();
	     
	     var NcontactMechId = $("#NcontactMechId").val();
	     var TcontactMechId = $("#TcontactMechId").val();
	     var facilityId = $("#facilityId").val();
	     
	     	     
	     addressFaciMap['supplierId'] = supplierId;
	     addressFaciMap['address1'] = address1;
	     addressFaciMap['address2'] = address2;
	     addressFaciMap['city'] = city;
	     addressFaciMap['postalCode'] = postalCode;
	     addressFaciMap['country'] = country;
	     addressFaciMap['state'] = state;
	     addressFaciMap['FcontactNumber'] = FcontactNumber;
	     addressFaciMap['facicontactMechType'] = facicontactMechType;
	     addressFaciMap['facilityName'] = facilityName;
	     addressFaciMap['facilityId'] = facilityId;
	     addressFaciMap['TFcountry'] = TFcountry;
	     addressFaciMap['TFstate'] = TFstate;
	     addressFaciMap['TFaddress1'] = TFaddress1;
	     addressFaciMap['TFaddress2'] = TFaddress2;
	     addressFaciMap['TFcity'] = TFcity;
	     addressFaciMap['TFpostalCode'] = TFpostalCode;
	     addressFaciMap['NcontactMechId'] = NcontactMechId;
	     addressFaciMap['TcontactMechId'] = TcontactMechId;
	     
	   
	     submitFacilityAddress();
	     
	    
	     
	     
	   }
     var orderFaciData;
	 var contactctMechFeciId;
	 function submitFacilityAddress() {
	
	 	var count = Object.keys(addressFaciMap).length;
	 
	 
	    var supplierId = addressFaciMap.supplierId;
	 	var city = addressFaciMap.city;
	 	var address1 = addressFaciMap.address1;
	 	var address2 = addressFaciMap.address2;
		var countryName = addressFaciMap.countryName;
	 	var postalCode = addressFaciMap.postalCode;
	 	var stateName = addressFaciMap.stateName;
	    var facilityName = addressFaciMap.facilityName;
	    var facicontactMechType = addressFaciMap.facicontactMechType;
	 	
	 	
	 	
	 
if(count != 0 && supplierId.length !=0 && facicontactMechType.length !=0 && facilityName.length !=0 && city.length !=0 && address1.length !=0 && postalCode.length != 0 && supplierId.length != 0){
		
		    $('div#orderSpinn').html('<img src="/images/gears.gif" height="50" width="50">');
		   jQuery.ajax({
                url: 'otherAddressStore',
                type: 'POST',
                data: addressFaciMap,
                dataType: 'json',
               success: function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					    alert("Error in order Items");
					}else{
						orderFaciData = result["orderList"];
					    
					     if(orderFaciData.lenght != 0){
					     
					       $("#addressTable").find("tr:not(:first)").remove();
					     
					        createdStatus = orderFaciData[0].createdStatus;
					       
					        if(createdStatus == "C")
					        {
					          $("#facilityCreated").html("Facility Has Been Created Successfully..");
					        }else{
					          $("#facilityCreated").html("Facility Has Been Edited Successfully..");
					        }
					       
					       $("#facilityCreated").show().delay(5000).fadeOut();
					      
	                      getFecilityAddressDetailAjax();
	                      $("input[type=text], textarea").val("");
	                      $("#postalCode").val("0");
	                      $("#TFpostalCode").val("0");
	                      
	                      
					     }
					    
               		}
               	}							
		}); 
		
		   
		}
		else{
		  alert("Please Fill The Values");
		}
	}
	
	
	
	
	
	///edit Facility Detailssssss=============================
	
	
	var supplierFacilityListJSON = [];
	
	
	function editFacilityAddress(){
	
	
	
	 
	          var supplierId = $("#createdSupplierId").val();
	  		var dataJson = {"partyId": supplierId};
	  
	      jQuery.ajax({
                url: 'editFacility',
                type: 'POST',
                data: dataJson,
                dataType: 'json',
               success: function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					    alert("Error in order Items");
					}else{
						supplierFacilityListJSON = result["supplierFacilityListJSON"];
    
					$("#editFacility").autocomplete({					
						source:  supplierFacilityList,
						select: function(event, ui) {
					     var selectedValue = ui.item.value;
					       $("#bankName").val(selectedValue);	
								      
								    }
					});
					   
               		}
               	}							
		}); 
		
	
	
	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
</script>
	<form id="EditPartyGroup"  action="<@ofbizUrl>createTransporter</@ofbizUrl>" name="EditPartyGroup" method="post">
	    <div id="wizard-2"  >
                
                 <h3>Supplier Other Address Information</h3>
            <section>
            	<fieldset>
            	
            	     <div class="lefthalf">
            	
				            <table cellpadding="2" cellspacing="1">
							
							<tr>
					        <td class="label"><FONT COLOR="red">*</font><b> Supplier Id</b></td>
					        <#if partyId?has_content>
					         <td>
	        		 			<input style="border-radius: 4px;" type="label" size="18" maxlength="100" name="createdSupplierId"  id="createdSupplierId"  value=${partyId}  readonly/>
	          				</td>
					        <#else>
					        <td>
	        		 			<input style="border-radius: 4px;" type="text" size="18" maxlength="100" name="createdSupplierId"  id="createdSupplierId" />
	          				</td>
	          				
	          				</#if>
	          			   
				        </tr>
                           <tr>	            
						    <td class="label"><b> Address Type</b></td>
						    <td>
						     <select style="border-radius: 4px;" name="facicontactMechType" id="facicontactMechType" >
	                            <option value="MANUFAC_LOCATION" >Manufacture Office Address</option>
	                            <option value="HEAD_LOCATION" >Head Office Address</option>
	                            <option value="BRANCH_LOCATION" >Branch Office Address</option>
	                            <option value="SUBBRANCH_LOCATION" >Sub branch Office Address</option>
	                             <option value="DEPOT_LOCATION" >Depot Office Address</option>
				             </select>
				            <td>
						     </tr>

							<tr>
							    <td class="label"><FONT COLOR="red">*</font><b>Facility Name</b></td>
							    <td>
							      	<input type="text" name="facilityName" id="facilityName" size="30" maxlength="60" autocomplete="off" />
							      	<input type="hidden" name="facilityId" id="facilityId"  />
							    </td>
							</tr>
									
                         
									    <td class="label"><FONT COLOR="red">*</font><b>Address1</b></td>
									    <td>
									      	<input type="text" name="address1" id="Faddress1" size="30" maxlength="60" autocomplete="off" />
									    </td>
									</tr>
									<tr>
									    <td class="label"><b> Address2</b></td>
									    <td>
									      	<input type="text" name="address2" id="Faddress2" size="30" maxlength="60" autocomplete="off" />
									    </td>
									</tr>
									<tr>
									    <td class="label"><FONT COLOR="red">*</font><b> City</b></td>
									    <td>
									      	<input type="text" name="city" id="Fcity" size="30" maxlength="60" autocomplete="off" />
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
									    <td class="label"><b> Postal Code</b></td>
									    <td>
									      	<input type="text" name="postalCode" id="postalCode" size="30" maxlength="60" value="0" autocomplete="off" />
									    </td>
									</tr>
									
									
								<#-->	<tr>
									    <td class="label"><b> Postal Code</b></td>
									    <td>
									      	<input type="text" name="postalCode" id="FpostalCode" size="30" maxlength="60" value="0" autocomplete="off" />
									    </td>
									</tr>
									<tr>
									    <td class="label"><b> E-mail Address</b></td>
									    <td>
									      	<input type="text" name="emailAddress" id="FemailAddress" size="30" maxlength="60" autocomplete="off" />
									    </td>
									</tr>
									<tr>
									    <td class="label"><b>Alternative E-mail Address</b></td>
									    <td>
									      	<input type="text" name="AltemailAddress" id="FAltemailAddress" size="30" maxlength="60" autocomplete="off" />
									    </td>
									</tr>-->
       								<#--><tr>
									    <td class="label"><b>Mobile Number</b></td>
									    <td>
									      	<input type="text" name="mobileNumber" id="FmobileNumber" size="15" maxlength="10" autocomplete="off" />
									    </td>
								   </tr>-->
									<tr>
									    <td class="label"><b>Contact Number</b></td>
									    <td>
									      	<input type="text" name="contactNumber" id="FcontactNumber" size="15" maxlength="15" autocomplete="off"/>
									    </td>
								  </tr>
								     
								      <tr>
									    <td>
									      	<h3>Tax Address:</h3>
									    </td>
									     <td>
									      	<h3>(Optional)</h3>
									    </td>
									</tr>
		       			             <tr>
									    <td class="label"><FONT COLOR="red">*</font><b>Address1</b></td>
									    <td>
									      	<input type="text" name="address1" id="TFaddress1" size="30" maxlength="60" autocomplete="off" />
									    </td>
									</tr>
									<tr>
									    <td class="label"><b> Address2</b></td>
									    <td>
									      	<input type="text" name="address2" id="TFaddress2" size="30" maxlength="60" autocomplete="off" />
									    </td>
									</tr>
									<tr>
									    <td class="label"><FONT COLOR="red">*</font><b> City</b></td>
									    <td>
									      	<input type="text" name="city" id="TFcity" size="30" maxlength="60" autocomplete="off" />
									    </td>
									</tr>
									<tr>
								      <td class="label"><b>${uiLabelMap.CommonCountry} :</b></td>
								      <td>
								        <select name="countryGeoId" id="TFeditcontactmechform_countryId"  onchange="javascript:setServiceName(this);">
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
								        <select name="stateProvinceGeoId" id="TFeditcontactmechform_stateId">
										
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
									<tr>
									    <td class="label"><b> Postal Code</b></td>
									    <td>
									      	<input type="text" name="postalCode" id="TFpostalCode" size="30" maxlength="60" value="0" autocomplete="off" />
									    </td>
									</tr>
									
									<tr>
									    <td>
									      	<input type="hidden" name="NcontactMechId" id="NcontactMechId" />
									      	<input type="hidden" name="TcontactMechId" id="TcontactMechId" />
									    </td>
									</tr>
									
									
								    <tr>
							        <td class="label"></td>
							        <td>
			        		 			<input class="button" type="button" size="18" value="Save" onclick="storeFacilityValues();"  />
			          				
			          				</td>
							        </tr>   	
		                        </table>
								    </div>
								    <div>
								    
								    
								 </div> 



                              <div class="righthalf">
						          
						          
						          <td><input type="button" name="approveOrder" id="approveOrder" value="ViewFacilityAdresses" onclick="javascript: getFecilityAddressDetailAjax();"/></td>
						          
						          
						          <table>
						          <tr>
						            <table class="myTable" id ="addressTable"><tbody>
						            
						            <tr>
										<th>View Address</th>
										<th>Edit Address</th>
										<th>Remove Address</th>
									</tr>
						            
						            </tbody></table>	     
						          <tr>
						          
						          <tr>
						          
						          <div  id="supplierInfo" align='center'  style=" border-radius: 10px;  color:blue; height:20px;   font-size: larger; "><span class="blink_me">The supplier has no facilities..</span></div>
						         
						           <div  id="facilityCreated" align='left'  style=" border-radius: 10px;  color:blue;    font-size: larger; "></div>
						         
						            <div align='center' name ='displayMsg' id='orderSpinn'/></div>
						         
						         
						          </tr>
						         </table>  
						           
						           
						           
							     
                               </div>


                                




    
                    </fieldset>  
               </section>
                
                
                </div> 
    
               
               
                </form>