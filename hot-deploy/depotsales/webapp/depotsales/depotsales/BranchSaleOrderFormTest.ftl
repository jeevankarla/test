	<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
	<link type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.css</@ofbizContentUrl>" rel="Stylesheet" />
	
	
	<style type="text/css">
	 	.labelFontCSS {
	    	font-size: 13px;
		}
		.form-style-8{
		    max-width: 650px;
		    max-height: 185px;
		    max-right: 10px;
		    margin-top: 10px;
			margin-bottom: -15px;
		    padding: 15px;
		    box-shadow: 1px 1px 25px rgba(0, 0, 0, 0.35);
		    border-radius: 20px;
		    border: 1px solid #305A72;
		}
		hr.style17 { 
			  display: block; 
			  content: ""; 
			  height: 5px; 
			  margin-top: -5px; 
			  border-style: solid; 
			  border-color: #8c8b8b; 
			  border-width: 0 0 0 0; 
			  border-radius: 20px; 
  
		} 
		hr.style17:before { 
		  height: 1px; 
		  border-style: solid; 
		  border-color: #8c8b8b; 
		  border-width: 1px 0 0 0; 
		  border-radius: 40px; 
		}
		
		hr.style18 { 
		  height: 1px; 
		  border-style: solid; 
		  border-color: #8c8b8b; 
		  border-width: 1px 0 0 0; 
		  border-radius: 40px; 
		} 
		hr.style18:before { 
			  display: block; 
			  content: ""; 
			  height: 30px; 
			  margin-top: -31px; 
			  border-style: solid; 
			  border-color: #8c8b8b; 
			  border-width: 0 0 1px 0; 
			  border-radius: 20px; 
		}
		
		
		
#popup{
    position: fixed;
    background: white;
    display: none;
    top: 300px;
    right: 30px;
    left: 680px;
    width: 200px;
    height: 200px;
    border: 1px solid #000;
    border-radius: 5px;
    padding: 10px;
    color: black;
} 
		
	</style>
	
	<script type="text/javascript">
			var supplierAutoJson = ${StringUtil.wrapString(supplierJSON)!'[]'};	
			var societyAutoJson = ${StringUtil.wrapString(societyJSON)!'[]'};

		$(document).ready(function(){
		
		    $("#open_popup").click(function(){
               	    
		       getShipmentAddress();
		    
             $("#popup").css("display", "block");
            });

           $("#close_popup").click(function(){
             $("#popup").css("display", "none");
           }); 
		
	
		
		
			 $("#societyfield").hide();
			 	fillPartyData();
			 	$("#editServChgButton").hide();
			if(indententryinit.schemeCategory.value.length > 0){
	  			if ($('#schemeCategory').val() == "General"){
	  				$("#editServChgButton").show();
	  				$('#serviceChargePercent').val(2);
	  				var scPerc = $('#serviceChargePercent').val();
	  				$("#serviceCharge").html("<b>"+scPerc+"% Service Charge is applicable</b>");
	  			}
	  		} 	
			$( "#effectiveDate" ).datepicker({
				dateFormat:'d MM, yy',
				changeMonth: true,
				numberOfMonths: 1,
				//minDate: new Date(),
				maxDate: 14,
				onSelect: function( selectedDate ) {
					$( "#effectiveDate" ).datepicker("option", selectedDate);
				}
			});
			$( "#orderDate" ).datepicker({
				dateFormat:'d MM, yy',
				changeMonth: true,
				numberOfMonths: 1,
				//minDate: new Date(),
				//maxDate: 14,
				onSelect: function( selectedDate ) {
					$( "#orderDate" ).datepicker("option", selectedDate);
				}
			});
			$( "#indentReceivedDate" ).datepicker({
				dateFormat:'d MM, yy',
				changeMonth: true,
				numberOfMonths: 1,
				//minDate: new Date(),
				//maxDate: 14,
				onSelect: function( selectedDate ) {
					$( "#indentReceivedDate" ).datepicker("option", selectedDate);
				}
			});
			
			$( "#chequeDate" ).datepicker({
				dateFormat:'d MM, yy',
				changeMonth: true,
				numberOfMonths: 1,
				onSelect: function( selectedDate ) {
					$( "#effectiveDate" ).datepicker(selectedDate);
				}
			});
			$('#ui-datepicker-div').css('clip', 'auto');
			 $('#suplierPartyId').keypress(function (e) { 
				$("#suplierPartyId").autocomplete({ source: supplierAutoJson , select: function( event, ui ) {
					$('span#suplierPartyName').html('<label>'+ui.item.label+'</label>');
				} });	
				if (e.keyCode === 13){
				
					calculateTaxApplicability();
					
					// Validation
					if(indententryinit.partyId.value.length < 1){
			  			alert("Customer is Mandatory");
			  			$('#partyId').css('background', 'red'); 
				       	
				       	setTimeout(function () {
				           	$('#partyId').css('background', 'white').focus(); 
				       	}, 800);
				       	$("#partyId").prev().css('color', 'yellow');
				       	
			  			return false;  
			  		}
			  		else if(indententryinit.productStoreId.value.length < 1){
			  			alert("Branch is Mandatory");
			  			$('#productStoreId').css('background', 'red'); 
				       	
				       	setTimeout(function () {
				           	$('#productStoreId').css('background', 'white').focus(); 
				       	}, 800);
				       	$("#productStoreId").prev().css('color', 'yellow');
				       	
			  			return false;  
			  		}
			  		else if(indententryinit.salesChannel.value.length < 1){
			  			alert("Sales Channel is Mandatory");
			  			$('#salesChannel').css('background', 'red'); 
				       	
				       	setTimeout(function () {
				           	$('#salesChannel').css('background', 'white').focus(); 
				       	}, 800);
				       	$("#salesChannel").prev().css('color', 'yellow');
				       	
			  			return false;  
			  		}
					else if(indententryinit.suplierPartyId.value.length < 1){
			  			alert("Supplier is Mandatory");
			  			$('#suplierPartyId').css('background', 'red'); 
				       	
				       	setTimeout(function () {
				           	$('#suplierPartyId').css('background', 'white').focus(); 
				       	}, 800);
				       	$("#suplierPartyId").prev().css('color', 'yellow');
				       	
			  			return false;  
			  		}
	    			
	    			if(indententryinit.taxTypeApplicable.value.length < 1){
			  			$('#taxTypeApplicable').val("VAT_SALE");
			  		}
	    			
	    			var transporterId = $("#transporterId").val();
			        var transporte = jQuery("<input>").attr("type", "hidden").attr("name", "transporterId").val(transporterId);
		
	    			jQuery(indententryinit).append(jQuery(transporte));
	    			
	    			$('#indententryinit').submit();
	    			return false;   
			}
		 });
		  $('#productStoreId').keypress(function (e) { 
				$("#productStoreId").autocomplete({ source: branchAutoJson , select: function( event, ui ) {
					$('span#branchName').html('<label>'+ui.item.label+'</label>');
				} });	
		 });
		  $('#societyPartyId').keypress(function (e) { 
				$("#societyPartyId").autocomplete({ source: societyAutoJson , select: function( event, ui ) {
					$('span#societyPartyName').html('<label>'+ui.item.label+'</label>');
				} });	
		 });
			var productStoreObjOnload=$('#productStoreIdFrom');
			if (productStoreObjOnload != null && productStoreObjOnload.val() != undefined ){
				showStoreCatalog(productStoreObjOnload);
			}
			
			
			$("#partyId").blur(function() {
				fillPartyData();
			});
			
			$("#productStoreId").blur(function() {
				getCfcList();
			});
			
		});
		
		function getCfcList(){
			var productStoreId = $('#productStoreId').val();
		
			if( productStoreId != undefined && productStoreId != ""){
				$('.CFC_TD').hide();
				var dataString="productStoreId=" + productStoreId ;
	      		$.ajax({
		             type: "POST",
		             url: "getCfcListAjax",
		           	 data: dataString ,
		           	 dataType: 'json',
		           	 async: false,
		        	 success: function(result) {	
	              		if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){            	  
	       	  		 		alert(result["_ERROR_MESSAGE_"]);
	          			}else{
	       	  				 var cfcList = result["cfcList"];
							 var optionList = '';   	
						     if (cfcList) {	
						     	optionList += "<option value = '' >SELECT CFC</option>"; 
					        	 for(var i=0 ; i<cfcList.length ; i++){
									var innerList = cfcList[i];	              			             
					                optionList += "<option value = " + innerList.productStoreId + " >" + innerList.facilityName + "</option>";          			
					      		 }
				      		 }
	       	  				 
					      	jQuery("#cfcs").html(optionList);
					      	 
					      	 if(cfcList.length > 0){
					      	 	$('.CFC_TD').show();
					      	 }
					      	 else{
					      	 	$('.CFC_TD').hide();
					      	 }
					      	 
	      				}
	               
	          		 } ,
		         	 error: function() {
		          	 	alert(result["_ERROR_MESSAGE_"]);
		         	 }
	         	 });
	         }
	    }
	    
		function fillPartyData(){
					var partyId = $('[name=partyId]').val();
					
					
		
				       	  				 if( partyId != undefined && partyId != ""){
			$('.partyLoom').remove();
				var dataString="partyId=" + partyId ;
	      	$.ajax({
	             type: "POST",
	             url: "getpartyContactDetails",
	           	 data: dataString ,
	           	 dataType: 'json',
	           	 async: false,
	        	 success: function(result) {
	              if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){            	  
	       	  		 alert(result["_ERROR_MESSAGE_"]);
	          			}else{
	       	  				  contactDetails =result["partyJSON"];
	       	  				 if( contactDetails != undefined && contactDetails != ""){
		       	  				  var address1=contactDetails["address1"];
		       	  				   address1 +=contactDetails["address2"];
		       	  				   address1 +=contactDetails["city"];
		       	  				   
		       	  				   var custName=contactDetails["custPartyName"];
		       	  				  var LoomDetails=contactDetails["LoomDetails"];
		       	  				  var LoomList=contactDetails["LoomList"];
		       	  				  var silkLooms=0;
		       	  				  var cottonLooms=0;
		       	  				  var WoolLooms=0;
		       	  				  var obj ={};
		       	  				 // alert(JSON.stringify(LoomDetails));
		       	  				  $.each(LoomList, function(key, item){
		       	  				  obj [item.loomType]=0;
		       	  				  	for(var i=0 ; i<LoomDetails.length ; i++){
			       	  				  if(LoomDetails[i].loomType==item.loomType){
			       	  				 		 obj [item.loomType] = LoomDetails[i].loomQty;
          									  
			       	  				 	}			       	  				 	
		       	  				  }
		       	  				  
								});		       	  				   
		       	  				  var tableElement;
		       	  				  var totLooms = 0;
		       	  				  
		       	  				  tableElement += '<tr class="partyLoom"><td width="20%" align="left" class="label"><font color="green">Loom Type</font></td>';
		       	  				   //tableElement += '<td width="20%" align="left" class="label"><font color="green">Loom Quota</font></td>';
		       	  				  tableElement += '<td width="20%" align="left" class="label"><font color="green">No of Looms</font></td></tr>';
		       	  				   
		       	  				 $.each(LoomList, function(key, item){
		       	  				    tableElement += '<tr class="partyLoom"><td width="20%" align="left" class="label"><font color="blue">'+item.loomType+'</font></td>';
		       	  				    tableElement += '<td width="20%" align="left" class="label"><font color="blue">'+obj[item.loomType]+'</font></td></tr>';
		       	  				 	totLooms = totLooms+parseInt(obj[item.loomType]);
		       	  				     
		       	  				 });
		       	  				  		       	  				   
		       	  				   var Depo=contactDetails["Depo"];
		       	  				   var DAO=contactDetails["DAO"];
		       	  				   var issueDate=contactDetails["issueDate"];
		       	  				   var psbNo=contactDetails["psbNo"];
		       	  				   var prodStoreId=contactDetails["productStoreId"];
		       	  				   
		       	  				  
		       	  				   
		       	  				   
		       	  				   var partyType=contactDetails["partyType"];
			       	  			   if( prodStoreId != undefined && prodStoreId != ""){
			       	  					//$("#productStoreId").autocomplete("select", prodStoreId);
			       	  					$('#productStoreId').focus().val(prodStoreId);
			       	  					jQuery("#branchName").html(prodStoreId);
			       	  					$('#salesChannel').focus();
	    								//$('#productStoreId').autocomplete('close');
			       	  			   }
			       	  				
		       	  				  
		       	  				   
		       	  				  var postalCode=contactDetails["postalCode"];
		       	  				 // $("#postalCode").html("<h4>"+postalCode+"</h4>");
		   						   $("#address").html("<h4>"+address1+"</h4>");
		       	  				   $("#partyName").html("<h4>"+custName+"</h4>");
		       	  				    $("#psbNo").html("<h4>"+psbNo+"</h4>");
		       	  				   	$("#DAO").html("<h4>"+DAO+"</h4>");
		       	  				   	$("#issueDate").html("<h4>"+issueDate+"</h4>");		       	  				   	
		       	  				   	$("#Depo").html("<h4>"+Depo+"</h4>");
		       	  				   	$("#partyType").html("<h4>"+partyType+"</h4>");
		       	  				    $("#totLooms").html("<h4>"+totLooms+"</h4>");
		       	  				    $('#loomTypes tr:last').after(tableElement);	
		       	  				  
		       	  				  
		       	  				  
					    var transprotersList;
						 
						 if(prodStoreId != undefined && prodStoreId !=""){
						 
						  var dataJson = {"prodStoreId":prodStoreId};
						 
							jQuery.ajax({
					                url: 'getTransportersList',
					                type: 'POST',
					                data: dataJson,
					                dataType: 'json',
					               success: function(result){
										if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
										    alert("Error in order Items");
										}else{
											transprotersList = result["transporterJSON"];
										    if(transprotersList.length != 0) 
										    {
										     var transporterJSON = ${StringUtil.wrapString(transprotersList)!'[]'};
												$(document).ready(function(){
										             $("#transporterId").autocomplete({ source: transprotersList }).keydown(function(e){});     
												});
										   }
					               		}
					               	}							
							});
							
							}
							
							
							       	  				  
	       	  				   }
	      			}
	               
	          	} ,
	         	 error: function() {
	          	 	alert(result["_ERROR_MESSAGE_"]);
	         	 }
	         	 });
	         	 }
	        }
		
		
		
		
		var partyName;
		function dispSuppName(selection){
		   var value = $("#partyId").val();
		   partyName = partyNameObj[value];
		   $("span#partyName").html('<label>'+partyName+'<label>');
		}   
		 function addSocietyField(selection){
			 if(selection.value=="onBehalfOf"){
			 $("#societyfield").show();
			 }else{
			 		 $("#societyPartyId").val("");
			 		 $("#societyfield").hide();
			 }
		 }
		
		var globalCatgoryOptionList=[];
		var catagoryList=[];
		function showStoreCatalog(productStoreObj) {
			
			var productStoreId=$(productStoreObj).val();
			
	       	var dataString="productStoreId=" + productStoreId ;
	      	$.ajax({
	             type: "POST",
	             url: "getDepotStoreCatalogCatagory",
	           	 data: dataString ,
	           	 dataType: 'json',
	           	 async: false,
	        	 success: function(result) {
	              if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){            	  
	       	  		 alert(result["_ERROR_MESSAGE_"]);
	          			}else{
	       	  				  catagoryList =result["catagoryList"];
	       	  				  var catgoryOptionList=[];
	       	  				 
	       	  				 // alert("catagoryList=========="+catagoryList);
	       	  					if(catagoryList != undefined && catagoryList != ""){
									$.each(catagoryList, function(key, item){
									if(item.value=="BYPROD"){
										catgoryOptionList.push('<option value="'+item.value+'" selected="selected">' +item.text+'</option>');
									}else{
									 catgoryOptionList.push('<option value="'+item.value+'">' +item.text+'</option>');
									 }
										});
					           }
					           $('#productCatageoryId').html(catgoryOptionList.join(''));   
						            $("#productCatageoryId").multiselect({
						   			minWidth : 180,
						   			height: 100,
						   			selectedList: 4,
						   			show: ["bounce", 100],
						   			position: {
						      			my: 'left bottom',
						      			at: 'left top'
						      		}
						   		});
						   		 $("#productCatageoryId").multiselect("refresh");
					          // alert("==globalCatgoryOptionList=="+globalCatgoryOptionList);
					         
	      	 	 
	      			}
	               
	          	} ,
	         	 error: function() {
	          	 	alert(result["_ERROR_MESSAGE_"]);
	         	 }
	        }); 
	           
	    }
	     
	  	function validateParty(){
	  		if(indententryinit.productStoreId.value.length < 1){
	  			alert("Branch is Mandatory");
	  			indententryinit.isFormSubmitted.value="";
	  		}
	  		if(indententryinit.suplierPartyId.value.length < 1){
	  			alert("Supplier Party ID is Mandatory");
	  			indententryinit.isFormSubmitted.value="";
	  		}
	  		if(indententryinit.partyId.value.length < 1){
	  			alert("Party ID is Mandatory");
	  			indententryinit.isFormSubmitted.value="";
	  		}
	  		if(indententryinit.productCatageoryId.value == ""){
	  			alert("Product Category is Mandatory");
	  			indententryinit.isFormSubmitted.value="";
	  		}
	  	}
	  	
	  	function calculateTaxApplicability() {
			var productStoreId = $("#productStoreId").val();
			var supplierPartyId = $("#suplierPartyId").val();
			var partyId = $("#partyId").val();
			
			if( productStoreId != undefined && productStoreId != ""  &&  supplierPartyId != undefined && supplierPartyId != ""  &&    partyId != undefined && partyId != ""  ){
			
				$.ajax({
		             type: "POST",
		             url: "getTaxApplicabilityDetails",
		           	 data: {productStoreId : productStoreId, supplierPartyId: supplierPartyId, partyId: partyId } ,
		           	 dataType: 'json',
		           	 async: false,
		        	 success: function(result) {
			             if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){            	  
			       	  		 alert(result["_ERROR_MESSAGE_"]);
			          	 }
			          	 else{
	       	  				 var geoIdsMap =result["geoIdsMap"];
	       	  				 var checkForE2Form = geoIdsMap["checkForE2Form"];
	       	  				 var taxTypeApplicable = geoIdsMap["taxTypeApplicable"];
	       	  				 //alert(taxTypeApplicable);
	       	  				 //jQuery('#taxTypeValue').html(taxTypeApplicable);
	       	  				 $('#orderTaxType').val(taxTypeApplicable).change();
	       	  				 //$('#orderTaxType option[value=CST_SALE]').prop('selected', 'selected').change();
	       	  				 
	       	  				 $('#taxTypeApplicable').val(taxTypeApplicable);
	       	  				 $('#partyGeoId').val(geoIdsMap["partyGeoId"]);
	       	  				 $('#supplierGeoId').val(geoIdsMap["supplierGeoId"]);
	       	  				 $('#branchGeoId').val(geoIdsMap["branchGeoId"]);
	       	  				 
	       	  				 $('#orderTaxType').val(geoIdsMap["taxType"]);
	       	  				 
	       	  				 if(checkForE2Form == "Y"){
	       	  				 	$('#e2FormCheck').val("Y");
	       	  				 }
	       	  				 else{
	       	  				 	$('#e2FormCheck').val("N");
	       	  				 }
	       	  				 
	       	  				 
	       	  				 <#--
	       	  				 if(checkForE2Form == "Y"){
	       	  				 	$("#E2FormCheck").attr("checked",false);
	       	  				 	jQuery('#E2FormChecktd').show();
	       	  				 }
	       	  				 else{
	       	  				 	$("#E2FormCheck").attr("checked","checked");
	       	  				 	jQuery('#E2FormChecktd').hide();
	       	  				 }
	       	  				 -->
	       	  				 
							         
			      		 }
		          	} ,
		         	error: function() {
		          		alert(result["_ERROR_MESSAGE_"]);
		         	}
		        }); 
			
			
			}
			
			
	    }
	  	
	  	
		function formSubmit(selection){
			 $('#indententryinit').submit();
			 return false; 
		}
		
		function autoCompletePartyId(){
			var productStoreId = $("#productStoreId").val();
		      $("#partyId").autocomplete({ 
		      
		      		
		      		source: function( request, response ) {
	        			$.ajax({
	          					url: "LookupBranchCustomers",
	          					dataType: "html",
	          					data: {
	            					ajaxLookup: "Y",
	            					term : request.term,
	            					productStoreId : productStoreId
	          					},
	          					success: function( data ) {
	          						var dom = $(data);
	        						dom.filter('script').each(function(){
	            						$.globalEval(this.text || this.textContent || this.innerHTML || '');
	        						});
	            					response($.map(autocomp, function(v,i){
	    								return {
	                						label: v.label,
	                						value: v.value
	               						};
									}));
									
	          					}
	        			});
	        			
	      			},
	      			select: function(e, ui) {
			        	$('span#partyTooltip').html('<label>'+ui.item.label+'</label>');
			        }
					
			  });	
		 }


var orderAddres;

      function getShipmentAddress(){
         
        var contactMechId = $("#contactMechId").val();
         var partyId = $("#partyId").val();
	 
	      if(contactMechId.length != 0){
		
		   var dataJson = {"partyId": partyId,"contactMechId":contactMechId};
		
			jQuery.ajax({
                url: 'getShipmentAddress',
                type: 'POST',
                data: dataJson,
                dataType: 'json',
               success: function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					    alert("Error in order Items");
					}else{
						OrderAddress = result["OrderAddress"];
						
						 $('#addressTable tbody').remove();
						
					    var row = $("<tr />")
                        $("#addressTable").append(row); 
                        row.append($("<td>Adress1 :</td>"));
                        row.append($("<td>" + OrderAddress.address1 + "</td>"));
					    
					     var row = $("<tr />") 
					     $("#addressTable").append(row); 
                        row.append($("<td>Adress2 :</td>"));
                        row.append($("<td>" + OrderAddress.address2 + "</td>"));
                        
                          var row = $("<tr />") 
					     $("#addressTable").append(row); 
                        row.append($("<td>Country :</td>"));
                        row.append($("<td> India </td>"));
                        
                         var row = $("<tr />") 
					     $("#addressTable").append(row); 
                        row.append($("<td>State   :</td>"));
                        row.append($("<td>" + OrderAddress.state + "</td>"));
                        
                        var row = $("<tr />") 
					     $("#addressTable").append(row); 
                        row.append($("<td>City    :</td>"));
                        row.append($("<td>" + OrderAddress.city + "</td>"));
                        
                         var row = $("<tr />") 
					     $("#addressTable").append(row); 
                        row.append($("<td>PostalCode:</td>"));
                        row.append($("<td>" + OrderAddress.postalCode + "</td>"));
						
                 	}	
                 	
                 }							
		      });
		
		    }
      
      }

	 
	</script>
	
	<#assign changeRowTitle = "Changes">   
	<#if parameters.formAction?has_content && (parameters.formAction=="SilkBranchSalesOrder" || parameters.formAction=="OtherBranchSalesOrder")>
		<#include "SilkBranchSalesOrderInternalForm.ftl"/>
	<#else>
		<#include "BranchSalesOrderInternalForm.ftl"/>
	</#if>
	<#include "EditUDPPriceDepot.ftl"/>
	<div class="top">
	
<div class="full">
	<div class="lefthalf">
		<div class="screenlet" style="width:173%">
			<div class="screenlet-title-bar">
         		<div class="grid-header" style="width:100%">
					<label>Branch sale Entry </label>
				</div>
		     </div>
      
    		<div class="screenlet-body">
    		  <#assign frmAction="BranchSalesOrder">
	    <#if parameters.formAction?has_content>
	    	    <#assign frmAction=parameters.formAction>
	    </#if>
	    
	    
	    	<form method="post" class="form-style-8" name="indententryinit" action="<@ofbizUrl>${frmAction}</@ofbizUrl>" id="indententryinit" onsubmit="validateParty()">
		
	      		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	               	
	               	<tr>
			           	<td>&nbsp;</td>
						<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>${uiLabelMap.Branch}:<font color="red">*</font></div></td>
			          	<#if changeFlag?exists && changeFlag=='EditDepotSales'>
							<#if productStoreId?exists && productStoreId?has_content>  
					  	  		<input type="hidden" name="productStoreId" id="productStoreId" value="${productStoreId?if_exists}"/>  
				          		<td valign='middle'>
				            		<div ><font color="green">
				               			${productStoreId}    <#--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="javascript:processChangeIndentParty()" class="buttontext">Party Change</a>-->             
				            		</div>
				          		</td>       
				          	</#if>
				    	<#else>
							<#if parameters.productStoreId?exists && parameters.productStoreId?has_content>  
					  	  		<input type="hidden" name="productStoreId" id="productStoreId" value="${parameters.productStoreId?if_exists}"/>  
				          		<td valign='middle'>
				            		<div><font color="green">
				               			${parameters.productStoreId}           
				            		</div>
				          		</td>       
				          		
				          		<#if parameters.cfcs?exists && parameters.cfcs?has_content>  
				          			<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>CFC:<font color="red">*</font></div></td>
						  	  		<input type="hidden" name="cfcs" id="cfcs" value="${parameters.cfcs?if_exists}"/>  
					          		<td valign='middle'>
					            		<div><font color="green">
					               			${parameters.cfcs}           
					            		</div>
					          		</td>  
				          		</#if>
				          		
				          		
				          	<#else>
				          		<td valign='middle'>
				          			<input type="text" name="productStoreId" id="productStoreId"/>
				          			<span class="tooltip" id="branchName"></span>
				          			<label class='CFC_TD' style='display:none;'><b>CFC:</label>
				          			<select name="cfcs" id="cfcs" style='display:none;' class='CFC_TD' >
	          						          					
			          				</select>
				          		</td>
				          	</#if>
			        	</#if>
		       	  		<#--<td><span class="tooltip" id="branchName"></span></td>-->
	               	</tr>
	               	
	               	<tr>
		       	  		
		       			<td>&nbsp;</td>
		       			
		       			<input type="hidden" name="billingType" id="billingType" value="Direct"/>  
		       			<#if parameters.partyGeoId?exists && parameters.partyGeoId?has_content>  
		       				<input type="hidden" name="partyGeoId" id="partyGeoId" value="${partyGeoId?if_exists}"/>
		       			 <#else>               
			          		<input type="hidden" name="partyGeoId" id="partyGeoId" value=""/>
			          	</#if>
			          	<#if parameters.branchGeoId?exists && parameters.branchGeoId?has_content>  
		       				<input type="hidden" name="branchGeoId" id="branchGeoId" value="${branchGeoId?if_exists}"/>
		       			 <#else>               
			          		<input type="hidden" name="branchGeoId" id="branchGeoId" value=""/>
			          	</#if>
		       			<input type="hidden" name="taxTypeApplicable" id="taxTypeApplicable" value=""/> 
		       			<input type="hidden" name="supplierGeoId" id="supplierGeoId" value=""/>  
		       			<#--<input type="hidden" name="branchGeoId" id="branchGeoId" value=""/>-->
		       			<input type="hidden" name="e2FormCheck" id="e2FormCheck" value=""/>
		       			<input type="hidden" name="orderTaxType" id="orderTaxType" value="${orderTaxType?if_exists}"/>
		       			<input type="hidden" name="serviceChargePercent" id="serviceChargePercent" value="0"/>
		       			<#if parameters.contactMechId?exists && parameters.contactMechId?has_content>  
		       				<input type="hidden" name="contactMechId" id="contactMechId" value="${contactMechId?if_exists}"/>
		       			 <#else>               
			          		<input type="hidden" name="contactMechId" id="contactMechId"/>
			          	</#if>
			          	
			          	
		       			<td align='left' valign='middle' nowrap="nowrap"><div class='h3'><#if changeFlag?exists && changeFlag=='AdhocSaleNew'>Retailer:<#elseif changeFlag?exists && changeFlag=='InterUnitTransferSale'>KMF Unit ID:<#else>${uiLabelMap.Customer}:</#if><font color="red">*</font></div></td>
				        <#if changeFlag?exists && changeFlag=='EditDepotSales'>
							<#if partyId?exists && partyId?has_content>  
					  	  		<input type="hidden" name="partyId" id="partyId" value="${partyId?if_exists}"/>  
				          		<td valign='middle'>
				            		<div ><font color="green">
				               			${partyId} [ ${partyName?if_exists} ] <#--${partyAddress?if_exists}  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="javascript:processChangeIndentParty()" class="buttontext">Party Change</a>-->             
				            		</div>
				          		</td>       
				          	</#if>
				    	<#else>
						 	<#if party?exists && party?has_content>  
					  	  		<input type="hidden" name="partyId" id="partyId" value="${party.partyId.toUpperCase()}"/>  
					  	  		<input type="hidden" name="disableAcctgFlag" id="disableAcctgFlag" value="${disableAcctgFlag?if_exists}"/>
				          		<td valign='middle' colspan="2">
				            		<div ><font color="green">
				            		    <#assign partyIdentification = delegator.findOne("PartyIdentification", {"partyId" :party.partyId,"partyIdentificationTypeId":"PSB_NUMBER"}, true)?if_exists>
         								<#assign passBookDetails=partyIdentification?if_exists>
				               			${party.groupName?if_exists} ${party.firstName?if_exists}${party.lastName?if_exists} [ ${passBookDetails.idValue?if_exists}] <#--${partyAddress?if_exists} &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="javascript:processChangeIndentParty()" class="buttontext">Party Change</a>-->             
				            		</div>
				          		</td>       
				       		<#else>               
				          		<td valign='middle'>
                 					<#-- <@htmlTemplate.lookupField value='${requestParameters.partyId?if_exists}' formName="indententryinit" name="partyId" id="partyId" fieldFormName="LookupEmpanelledPartyName"/>
				          			<input type="text" name="partyId" id="partyId" onblur= 'javascript:dispSuppName(this);' />-->
				          			<input type='text' id='partyId' name='partyId' onfocus='javascript:autoCompletePartyId();' size='13'/><span class="tooltip" id='partyTooltip'></span>
				          		</td>
				          		<#--<td colspan="2"><span class="tooltip" id="partyName"></td></span></td>-->
			        			<#--<td><span class="tooltip">Input party code and press Enter</span></td>-->
				          	</#if>
			        	</#if>
						
	               	</tr>
	               	
	               	
	               	<tr>
		       	  		<td>&nbsp;</td>
		       	  		<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Sales Channel:</div></td>
		       			<#if parameters.salesChannel?exists && parameters.salesChannel?has_content>  
			  	  			<input type="hidden" name="salesChannel" id="salesChannel" value="${parameters.salesChannel?if_exists}"/>  
		          			<td valign='middle'>
		            			<div><font color="green">${parameters.salesChannel?if_exists}</div>
		          			</td>       	
		       			<#else>      	         
		          			<td valign='middle'>
		          				<select name="salesChannel" id="salesChannel" class='h3' style="width:162px">
		          					<option value="WALKIN_SALES_CHANNEL">Walk-In Sales Channel</option>
		          					<option value="WEB_SALES_CHANNEL">Web Channel</option>
		          					<option value="POS_SALES_CHANNEL">POS Channel</option>
		          					<option value="PHONE_SALES_CHANNEL">Phone Channel</option>
		          					<option value="FAX_SALES_CHANNEL">Fax Channel</option>
		          					<option value="EMAIL_SALES_CHANNEL">E-Mail Channel</option>	          					
		          				</select>
		          			</td>
		       			</#if>
		       		</tr>	
	               	<tr>
		       	  		<td>&nbsp;</td>
		       	  		<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>${uiLabelMap.SchemeCategory}:</div></td>
		       			<#if parameters.schemeCategory?exists && parameters.schemeCategory?has_content>  
			  	  			<input type="hidden" name="schemeCategory" id="schemeCategory" value="${parameters.schemeCategory?if_exists}"/>  
		          			<td valign='middle'>
		            			<div><font color="green"><#if parameters.schemeCategory == "MGPS_10Pecent">MGPS + 10% <#else>${parameters.schemeCategory?if_exists}</#if></div>
		          			</td>       	
		       			<#else>      	         
		          			<td valign='middle'>
		          				<select name="schemeCategory" id="schemeCategory" class='h3' style="width:162px">
		          					<option value="MGPS_10Pecent">MGPS + 10%</option>
		          					<option value="MGPS">MGPS</option>
		          					<option value="General">General</option>	          					
		          				</select>
		          			</td>
		       			</#if>
		       		</tr>	
		       		<#--
					<tr>
					<td>&nbsp;</td>
					<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>${uiLabelMap.IndentTaxType}:</div></td>
		       			<#if orderTaxType?exists && orderTaxType?has_content>  
			  	  			<input type="hidden" name="orderTaxType" id="orderTaxType" value="${orderTaxType?if_exists}"/>  
		          			<td valign='middle'>
		            			<div><font color="green">${orderTaxType?if_exists}</div>
		          			</td>       	
		       			<#else>      	         
		          			<td valign='middle'>
		          				<select name="orderTaxType" id="orderTaxType" class='h3' style="width:162px">
		          					<option value="INTRA">With in State</option>
		          					<option value="INTER">Out of State</option>
		          				</select>
		          			</td>
		       			</#if>
	               	</tr>
	               	-->	
                    <tr>  
		       	  		<td>&nbsp;</td>
		       	  		<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>${uiLabelMap.IndentDate}:</div></td>
			           		<input type="hidden" name="productSubscriptionTypeId"  value="CASH" />
		          			<input type="hidden" name="isFormSubmitted"  value="YES" />
					      	<input type="hidden" name="changeFlag"  value="${changeFlag?if_exists}" />
					      	<#if changeFlag?exists && changeFlag=="EditDepotSales">
							 	<input type="hidden" name="productStoreId" id="productStoreId" value="${productStoreId?if_exists}"/>  
							 	<input type="hidden" name="shipmentTypeId" id="shipmentTypeId" value="BRANCH_SHIPMENT"/> 
				           	</#if>
					        <#if changeFlag?exists && changeFlag=='DepotSales'>
					         	<input type="hidden" name="shipmentTypeId" id="shipmentTypeId" value="BRANCH_SHIPMENT"/> 
					        <#else>
					          	<input type="hidden" name="shipmentTypeId" id="shipmentTypeId" value="RM_DIRECT_SHIPMENT"/>
					          	<input type="hidden" name="salesChannel" id="salesChannel" value="RM_DIRECT_CHANNEL"/>
					        </#if>
			          		<#if effectiveDate?exists && effectiveDate?has_content>  
				  	  			<input type="hidden" name="effectiveDate" id="effectiveDate" value="${effectiveDate}"/>  
				          		<td align='left' valign='middle'>
				            		<div><font color="green">${effectiveDate}         
				            		</div>
				          		</td>       
			       	  		<#else> 
				          		<td valign='left'>          
				            		<input class='h3' type="text" name="effectiveDate" id="effectiveDate" value="${defaultEffectiveDate}"/>           		
				            	</td>
			       	  		</#if>
		       	  </tr>	
                      <tr>
		       			<td>&nbsp;</td>
		       	  		<#if changeFlag?exists && changeFlag != "EditDepotSales">
							<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>${uiLabelMap.ReceivedDate}:</div></td>
							<#if indentReceivedDate?exists && indentReceivedDate?has_content>  
				  				<input type="hidden" name="indentReceivedDate" id="indentReceivedDate" value="${indentReceivedDate}"/>  
				   				<td valign='middle'>
									<div ><font color="green">${indentReceivedDate}         
									</div>
				   				</td>  
							<#else> 
				 				<td valign='left'>          
									<input class='h3' type="text" name="indentReceivedDate" id="indentReceivedDate" value="${defaultEffectiveDate}"/>    
				 				</td>
							</#if>
						</#if>
                       <td>&nbsp;</td>
	               	</tr>
	               	<tr>
		       	  		
		       			<td>&nbsp;</td>
		       			<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Reference No :</div></td>
			          	<#if changeFlag?exists && changeFlag=='EditDepotSales'>
							<#if referenceNo?exists && referenceNo?has_content>  
					  	  		<input type="hidden" name="referenceNo" id="referenceNo" value="${referenceNo?if_exists}"/>  
				          		<td valign='middle'>
				            		<div><font color="green">
				               			${referenceNo}               
				            		</div>
				          		</td>       
				          	</#if>
				    	<#else>
							<#if parameters.referenceNo?exists && parameters.referenceNo?has_content>  
					  	  		<input type="hidden" name="referenceNo" id="referenceNo" value="${parameters.referenceNo?if_exists}"/>  
				          		<td valign='middle'>
				            		<div><font color="green">
				               			${parameters.referenceNo}              
				            		</div>
				          		</td>       
				          	<#else>
				          		<td valign='middle'>
				          			<input type="text" name="referenceNo" id="referenceNo"/>
				          			<#--<span class="tooltip">Input Supplier and Press Enter</span>-->
				          		</td>
				          		
				          	</#if>
			        	</#if>
						
	               	</tr>	               	
	               	<tr>
		       	  		
		       			<td>&nbsp;</td>
		       			<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>${uiLabelMap.ProductSupplier} :<font color="red">*</font></div></td>
			          	<#if changeFlag?exists && changeFlag=='EditDepotSales'>
							<#if suplierPartyId?exists && suplierPartyId?has_content>  
					  	  		<input type="hidden" name="suplierPartyId" id="suplierPartyId" value="${suplierPartyId?if_exists}"/>  
				          		<td valign='middle'>
				            		<div><font color="green">
				               			${suplierPartyId}  [${suplierPartyName}]  <#--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="javascript:processChangeIndentParty()" class="buttontext">Party Change</a>-->             
				            		</div>
				          		</td>       
				          	</#if>
				    	<#else>
							<#if parameters.suplierPartyId?exists && parameters.suplierPartyId?has_content>  
					  	  		<input type="hidden" name="suplierPartyId" id="suplierPartyId" value="${parameters.suplierPartyId?if_exists}"/>  
				          		<td valign='middle'>
				            		<div><font color="green">
				               			${parameters.suplierPartyId} [${suppPartyName?if_exists}] <#--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="javascript:processChangeIndentParty()" class="buttontext">Party Change</a>-->             
				            		</div>
				          		</td>       
				          	<#else>
				          		<td valign='middle'>
				          			<input type="text" name="suplierPartyId" id="suplierPartyId"/>
				          			<#--<span class="tooltip">Input Supplier and Press Enter</span>-->
				          		</td>
				          		
				          	</#if>
			        	</#if>
						
	               	</tr>
	                 <#--	
	               	<tr>
	               		<td>&nbsp;</td>
	               		<td>&nbsp;</td>
	               		<td>&nbsp;</td>
	               		<td>&nbsp;</td>
	               		<td>&nbsp;</td>
	               		<td>&nbsp;</td>
	               		<td>&nbsp;</td>
	               		<td><span class="tooltip">Input party code and press Enter</span></td>
	               	</tr>
	               	-->   
	      		</table>
	    	<div id="sOFieldsDiv" >
		</div> 
	</form>
	<br/>
	<form method="post" id="indententry" action="<@ofbizUrl>IndentEntryInit</@ofbizUrl>">  
		<input type="hidden" name="effectiveDate" id="effectiveDate" value="${parameters.effectiveDate?if_exists}"/>
		<input type="hidden" name="boothId" id="boothId" value="${parameters.boothId?if_exists}"/>
		<input type="hidden" name="productSubscriptionTypeId" id="productSubscriptionTypeId" value="${parameters.productSubscriptionTypeId?if_exists}"/>   	   	   	   
		<input type="hidden" name="subscriptionTypeId" id="subscriptionTypeId" value="${parameters.subscriptionTypeId?if_exists}"/>
		<input type="hidden" name="destinationFacilityId" id="destinationFacilityId" value="${parameters.destinationFacilityId?if_exists}"/>
		<input type="hidden" name="shipmentTypeId" id="shipmentTypeId" value="${parameters.shipmentTypeId?if_exists}"/>
		<input type="hidden" name="vehicleId" id="vehicleId" value="${parameters.vehicleId?if_exists}"/>
		<input type="hidden" name="salesChannel" id="salesChannel" value="${parameters.salesChannel?if_exists}"/>
		<input type="hidden" name="referenceNo" id="referenceNo" value="${parameters.referenceNo?if_exists}"/>
		<input type="hidden" name="billToCustomer" id="billToCustomer" value="${parameters.billToCustomer?if_exists}"/>
		<input type="hidden" name="branchGeoId" id="branchGeoId" value="${parameters.branchGeoId?if_exists}"/>
		<input type="hidden" name="serviceChargePercent" id="serviceChargePercent" value="${parameters.serviceChargePercent?if_exists}"/>
		<input type="hidden" name="contactMechId" id="contactMechId" value="${parameters.contactMechId?if_exists}" />
		
		<br>
	</form>    
		</div>
		</div>
	</div>

	<div class="righthalf">
		<div class="screenlet">
			<div class="grid-header" style="width:100%">
	 			<label>Customer Details</label>
			</div>
    		<div class="screenlet-body">
				 <form  name="partyDetails" id="partyDetails">
				 	  	<hr class="style17"></hr>
				 	  	
				 	  	
				 	  	
	      				<table width="100%" border="0" cellspacing="0" cellpadding="0">
				 	  		<#if parameters.custName?exists && parameters.custName?has_content> 
		               		 <tr>
			       				<td width="15%" keep-together="always" align="left"><font color="green"><b>   PartyName       : </b></font></td><td width="85%"><font color="blue"><b>${parameters.custName}</b></font></td>
			       			
			       			</tr>
			       			<#else>
		               		
		               		<tr>
			       				<td width="15%" keep-together="always" align="left"><font color="green"><b>   PartyName       : </b></font></td><td width="70%"> <label  align="left" id="partyName"style="color: blue" ></label></td> 
			       			</tr>
			       			</#if>
				 	 	 	<#if parameters.address?exists && parameters.address?has_content> 
			       			<tr>
			       				<td width="15%" keep-together="always" align="center"><font color="green"><b>   Address         : </b></font></td><td width="85%"> <font color="blue"><b>${parameters.address}</b></font></td>
			       			</tr>
			       			<#else>
		               		<tr>
			       				<td width="15%" keep-together="always" align="left"><font color="green" ><b>   Address         : </b></font></td><td width="85%"> <label  align="left" id="address" style="color: blue"></label></td>
			       			</tr>
			       			</#if>
				 	 	</table>	
				 	 	 
						    <div id="popup" style="border-width: 2px; padding-top: 20px;   border-radius: 10px; border-style: solid; border-color: grey; ">
						        <h1>Address</h1>
						        <table id ="addressTable"><tbody></tbody></table>
						        <a href="#" id="close_popup">Close</a>
						    </div>
						    
					    <table width="100%">
					    <tr>
					    
					   <td> <input type="button" id="open_popup" class="buttontext" value="View Delivery Address"  /> </td>    
					    
					    
					   <td> <input type="button" class="buttontext" value="Edit Delivery Address" onclick="javascript:manualAddress();" /> </td>
					   
					   <td>
					    <#if parameters.transporterId?exists && parameters.transporterId?has_content> <font color="black"><b>Transpoter        : </b></font> <font color="green"><b>${parameters.transporterId}</b></font>  
					    
					    <input type="hidden" name="transporterId" id="transporterId" value="${parameters.transporterId?if_exists}" />
					    <#else>
					    <input type="text"  id="transporterId" name="transporterId" placeholder="Select Transporter"/>   
						</#if>
						</td>		 	 	   
				 	 	 </tr>
				 	 	 </table>
				 	 	 
				 	 	<hr class="style18"></hr>
				 	  <table width="100%" border="2" cellspacing="0" cellpadding="0">
					 	<tr>
						<td width="60%">
		      				<table width="100%" border="1" border-style="solid">
			               
				       			<#if parameters.psbNo?exists && parameters.psbNo?has_content> 
				       			 <tr>
				       				<td width="30%" keep-together="always"><font color="green">PassBook        : </font></td><td width="85%"><font color="blue"><b>${parameters.psbNo}</b></font></td>
				       			</tr>
				       			<#else>
				       			<tr>
				       				<td width="30%" keep-together="always"><font color="green">PassBook    : </font></td><td width="85%"> <label  align="left" id="psbNo" style="color: blue"></label></td>
				       			</tr>
				       			</#if>
				       			<#if parameters.issueDate?exists && parameters.issueDate?has_content> 
				       			<tr>
				       				<td width="20%"><font color="green">IssueDate     : </font></td><td width="50%"><font color="blue"><b> ${parameters.issueDate?if_exists}</b></font></td>
				       			</tr>
				       			<#else>
				       			<tr>
				       				<td width="20%"><font color="green">IssueDate     : </font></td><td width="50%"><font color="blue"><label  align="left" id="issueDate" style="color: blue"></label></font></td>
				       			</tr>
				       			</#if>
				       			
				       			<#--<#if parameters.postalCode?exists && parameters.postalCode?has_content> 
				       			<tr>
				       				<td width="20%" keep-together="always"><font color="green">postal Code     : </font></td><td width="85%"> <font color="blue"><b>${parameters.postalCode}</b></font></td>
				       			</tr>
				       			<#else>
				       			<tr>
				       				<td width="35%" keep-together="always"><font color="green">postal Code     : </font></td><td width="85%"> <label  align="left" id="postalCode" style="color: blue"></label></td>
				       			</tr>
				       			</#if>-->
				       			<#if parameters.Depo?exists && parameters.Depo?has_content> 
				       			<tr>
				       				<td width="20%"><font color="green">${uiLabelMap.Depot}     : </font></td><td width="50%"><font color="blue"><b> ${parameters.Depo}</b></font></td>
				       			</tr>
				       			<#else>
				       			<tr>
				       				<td width="20%"><font color="green">${uiLabelMap.Depot}     : </font></td> <td width="50%"><label  align="left" id="Depo" style="color: blue"></label></td>
				       			</tr>
				       			</#if>
				       			<#if parameters.DOA?exists && parameters.DOA?has_content> 
				       			<tr>
				       				<td width="20%"><font color="green">DOA     : </font></td><td width="50%"><font color="blue"><b> ${parameters.DAO?if_exists}</b></font></td>
				       			</tr>
				       			<#else>
				       			<tr>
				       				<td width="20%"><font color="green">DOA     : </font></td><td width="50%"><font color="blue"><label  align="left" id="DAO" style="color: blue"></label></font></td>
				       			</tr>
				       			</#if>
				       			<#if parameters.partyType?exists && parameters.partyType?has_content> 
				       			<tr>
				       				<td width="25%"><font color="green">partyType     : </font></td><td width="50%"><font color="blue"><b> ${parameters.partyType?if_exists}</b></font></td>
				       			</tr>
				       			<#else>
				       			<tr>
				       				<td width="25%"><font color="green">partyType     : </font></td><td width="50%"><font color="blue"><label  align="left" id="partyType" style="color: blue"></label></font></td>
				       			</tr>
				       			</#if>
				       			<tr>
				       				<td width="25%"><font color="green">Total Looms     : </font></td><td width="50%"><font color="blue"><label  align="left" id="totLooms" style="color: blue"></label></font></td>
				       			</tr>
				       		</table>
			       		</td>
			       		<td width="40%">
			       			<table width="100%" id="loomTypes" border="10%" cellspacing="1" cellpadding="2">
			       				<tr>
			       			
			       				</tr>
			       			
			       			</table>
			       		</td>
			       	</tr>
			       			
			     </table>
			     <hr class="style18"></hr>
		       	</form>
				
		</div>     
	</div>
</div>
	
	
	</div>
	<div class="full" style="height:250px;">
	</br> 
	
	</div>
	
	<div class="bottom">
		<div class="screenlet" >
			<div class="grid-header" style="width:100%">
				<span style="float:left; margin-left:0px;" id="serviceCharge" class="serviceCharge"></span>
				<a style="float:left; margin-left:0px;" href="javascript:changeServiceChargePercent()" class="buttontext" id="editServChgButton">Edit Service Charge</a>
				<span style="float:right; margin-left: -5px;" id="totalAmount"></span>
			</div>
		    <div class="screenlet-body">
				<div id="myGrid1" style="width:100%;height:210px;"></div>
					  
					<#assign formAction='processBranchSalesOrder'>			
					
					
					<#if booth?exists || party?exists || partyId?exists >
		 		    	<#--
		 		    	<div class="screenlet-title-bar">
							<div class="grid-header" style="width:35%">
								<label>Other Charges</label><span id="totalAmount"></span>
							</div>
							<div id="myGrid2" style="width:35%;height:150px;">
								<div class="grid-header" style="width:35%">
								</div>
							</div>
						</div>	
						-->
				    	<div align="center">
				    		<input type="submit" style="padding:.3em" id="changeSave" value="${uiLabelMap.CommonSubmit}" onclick="javascript:processIndentEntry('indententry','<@ofbizUrl>${formAction}</@ofbizUrl>');"/>
				    		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				    		<input type="submit" style="padding:.3em" id="changeCancel" value="Cancel" onclick="javascript:processIndentEntry('indententry','<@ofbizUrl>processOrdersBranchSales</@ofbizUrl>');"/>   	
				    	</div>     
					</#if>
					
				</div>
			</div>     
		</div>
		</div>
	
