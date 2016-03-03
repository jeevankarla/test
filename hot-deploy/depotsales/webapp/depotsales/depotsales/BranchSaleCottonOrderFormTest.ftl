	<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
	<link type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.css</@ofbizContentUrl>" rel="Stylesheet" />
	
	
	<style type="text/css">
	 	.labelFontCSS {
	    	font-size: 13px;
		}
	
	</style>
	
	<script type="text/javascript">
			var supplierAutoJson = ${StringUtil.wrapString(supplierJSON)!'[]'};	
			var societyAutoJson = ${StringUtil.wrapString(societyJSON)!'[]'};

		$(document).ready(function(){
			 $("#societyfield").hide();
	
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
				
					// Validation
					
					if(indententryinit.partyId.value.length < 1){
			  			alert("Customer is Mandatory");
			  			$('#0_lookupId_partyId').css('background', 'red'); 
				       	
				       	setTimeout(function () {
				           	$('#0_lookupId_partyId').css('background', 'white').focus(); 
				       	}, 800);
				       	$("#0_lookupId_partyId").prev().css('color', 'yellow');
				       	
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
					else if(indententryinit.suplierPartyId.value.length < 1){
			  			alert("Supplier is Mandatory");
			  			$('#suplierPartyId').css('background', 'red'); 
				       	
				       	setTimeout(function () {
				           	$('#suplierPartyId').css('background', 'white').focus(); 
				       	}, 800);
				       	$("#suplierPartyId").prev().css('color', 'yellow');
				       	
			  			return false;  
			  		}
				
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
			var partyId = $('[name=partyId]').val();
			
			
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
		       	  				   var loomType=contactDetails["loomType"];
		       	  				   var loomQty=contactDetails["loomQty"];
		       	  				   var loomQuota=contactDetails["loomQuota"];
		       	  				   var Depo=contactDetails["Depo"];
		       	  				   var psbNo=contactDetails["psbNo"];
		       	  				   
		       	  				   
		       	  				   
		       	  				  var postalCode=contactDetails["postalCode"];
		       	  				  $("#postalCode").html("<h4>"+postalCode+"</h4>");
		   						   $("#address").html("<h4>"+address1+"</h4>");
		       	  				   $("#partyName").html("<h4>"+custName+"</h4>");
		       	  				   $("#loomType").html("<h4>"+loomType+"</h4>");
		       	  				    $("#psbNo").html("<h4>"+psbNo+"</h4>");
		       	  				   
		       	  				    $("#loomQty").html("<h4>"+loomQty+"</h4>");
		       	  				   	$("#Depo").html("<h4>"+Depo+"</h4>");
		       	  				   	$("#loomQuota").html("<h4>"+loomQuota+"</h4>");
		       	  				   
	       	  				   }
	      			}
	               
	          	} ,
	         	 error: function() {
	          	 	alert(result["_ERROR_MESSAGE_"]);
	         	 }
	        }); 
			
			});
			
		});
		
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
	  	
	function formSubmit(selection){
		 $('#indententryinit').submit();
			    return false; 
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
	    
	    
	    	<form method="post" name="indententryinit" action="<@ofbizUrl>${frmAction}</@ofbizUrl>" id="indententryinit" onsubmit="validateParty()">
		
	      		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	               	
	               	<tr>
		       	  		
		       			<td>&nbsp;</td>
		       			<input type="hidden" name="billingType" id="billingType" value="Direct"/>  
		       			
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
                 					 <@htmlTemplate.lookupField value='${requestParameters.partyId?if_exists}' formName="indententryinit" name="partyId" id="partyId" fieldFormName="LookupEmpanelledPartyName"/>
				          			<#--<input type="text" name="partyId" id="partyId" onblur= 'javascript:dispSuppName(this);' />-->
				          		</td>
				          		<#--<td colspan="2"><span class="tooltip" id="partyName"></td></span></td>-->
			        			<#--<td><span class="tooltip">Input party code and press Enter</span></td>-->
				          	</#if>
			        	</#if>
						
	               	</tr>
	               	
	               	
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
				               			${parameters.productStoreId}  <#--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="javascript:processChangeIndentParty()" class="buttontext">Party Change</a>-->             
				            		</div>
				          		</td>       
				          	<#else>
				          		<td valign='middle'>
				          			<input type="text" name="productStoreId" id="productStoreId"/>
				          			<span class="tooltip" id="branchName"></span>
				          		</td>
				          	</#if>
			        	</#if>
		       	  		<#--<td><span class="tooltip" id="branchName"></span></td>-->
	               	</tr>
	               	<tr>
		       	  		<td>&nbsp;</td>
		       	  		<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>${uiLabelMap.SchemeCategory}</div></td>
		       			<#if parameters.schemeCategory?exists && parameters.schemeCategory?has_content>  
			  	  			<input type="hidden" name="schemeCategory" id="schemeCategory" value="${parameters.schemeCategory?if_exists}"/>  
		          			<td valign='middle'>
		            			<div><font color="green">${parameters.schemeCategory?if_exists}</div>
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
                    <tr>  
		       	  		<td>&nbsp;</td>
		       	  		<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>${uiLabelMap.IndentDate}:</div></td>
			           		<input type="hidden" name="productSubscriptionTypeId"  value="CASH" />
		          			<input type="hidden" name="isFormSubmitted"  value="YES" />
					      	<input type="hidden" name="changeFlag"  value="${changeFlag?if_exists}" />
					      	<#if changeFlag?exists && changeFlag=="EditDepotSales">
							 	<input type="hidden" name="productStoreId" id="productStoreId" value="${productStoreId?if_exists}"/>  
							 	<input type="hidden" name="shipmentTypeId" id="shipmentTypeId" value="BRANCH_SHIPMENT"/> 
				           		<input type="hidden" name="salesChannel" id="salesChannel" value="BRANCH_CHANNEL"/>
						  	</#if>
					        <#if changeFlag?exists && changeFlag=='DepotSales'>
					         	<input type="hidden" name="shipmentTypeId" id="shipmentTypeId" value="BRANCH_SHIPMENT"/> 
					           	<input type="hidden" name="salesChannel" id="salesChannel" value="BRANCH_CHANNEL"/>
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
		<input type="hidden" name="salesChannel" id="salesChannel" value="BRANCH_CHANNEL"/>
		<input type="hidden" name="billToCustomer" id="billToCustomer" value="${parameters.billToCustomer?if_exists}"/>
		<br>
	</form>    
		</div>
		</div>
	</div>
	
	<div class="righthalf" >
		<div class="screenlet" style="width:71%; margin-left:29%">
			<div class="screenlet-title-bar">
         		<div class="grid-header" style="width:100%">
					<label>Customer Details</label>
				</div>
		     </div>
    		<div class="screenlet-body">
		 		
				 <form  name="partyDetails" id="partyDetails">
	      				<table width="50%" border="0" cellspacing="0" cellpadding="0">
		               		<#if parameters.custName?exists && parameters.custName?has_content> 
		               		 <tr>
			       				<td width="35%" keep-together="always"><font color="green">PartyName       : </font></td><td width="85%"><font color="blue"><b>${parameters.custName}</b></font></td>
			       			</tr>
			       			<#else>
		               		
		               		<tr>
			       				<td width="35%" keep-together="always"><font color="green">PartyName       : </font></td><td width="85%"> <label  align="left" id="partyName"style="color: blue" ></label></td>
			       			</tr>
			       			</#if>
			       			<#if parameters.psbNo?exists && parameters.psbNo?has_content> 
			       			 <tr>
			       				<td width="35%" keep-together="always"><font color="green">PassBook Num        : </font></td><td width="85%"><font color="blue"><b>${parameters.psbNo}</b></font></td>
			       			</tr>
			       			<#else>
			       			<tr>
			       				<td width="35%" keep-together="always"><font color="green">PassBook Num    : </font></td><td width="85%"> <label  align="left" id="psbNo" style="color: blue"></label></td>
			       			</tr>
			       			</#if>
			       			<#if parameters.address?exists && parameters.address?has_content> 
			       			<tr>
			       				<td width="35%" keep-together="always"><font color="green">Address         : </font></td><td width="85%"> <font color="blue"><b>${parameters.address}</b></font></td>
			       			</tr>
			       			<#else>
		               		<tr>
			       				<td width="35%" keep-together="always"><font color="green">Address         : </font></td><td width="85%"> <label  align="left" id="address" style="color: blue"></label></td>
			       			</tr>
			       			</#if>
			       			<#if parameters.postalCode?exists && parameters.postalCode?has_content> 
			       			<tr>
			       				<td width="35%" keep-together="always"><font color="green">postal Code     : </font></td><td width="85%"> <font color="blue"><b>${parameters.postalCode}</b></font></td>
			       			</tr>
			       			<#else>
			       			<tr>
			       				<td width="35%" keep-together="always"><font color="green">postal Code     : </font></td><td width="85%"> <label  align="left" id="postalCode" style="color: blue"></label></td>
			       			</tr>
			       			</#if>
			       			<#if parameters.Depo?exists && parameters.Depo?has_content> 
			       			<tr>
			       				<td width="35%" keep-together="always"><font color="green">Depo Holder     : </font></td><td width="85%"><font color="blue"><b> ${parameters.Depo}</b></font></td>
			       			</tr>
			       			<#else>
			       			<tr>
			       				<td width="35%" keep-together="always"><font color="green">Depo Holder     : </font></td><td width="85%"> <label  align="left" id="Depo" style="color: blue"></label></td>
			       			</tr>
			       			</#if>
			       			<#if parameters.loomType?exists && parameters.loomType?has_content> 
			       			
			       			<tr>
			       				<td width="35%" keep-together="always"><font color="green">Party Loom Type : </font></td><td width="85%"><font color="blue"><b>${parameters.loomType}</b></font></td>
			       			</tr>
			       			<#else>
			       			<tr>
			       				<td width="35%" keep-together="always"><font color="green">Party Loom Type : </font></td><td width="85%"> <label  align="left" id="loomType" style="color: blue"></label></td>
			       			</tr>
			       			</#if>
			       			<#if parameters.loomQuota?exists && parameters.loomQuota?has_content> 
			       			
			       			<tr>
			       				<td width="35%" keep-together="always"><font color="green">QuotaPerLoom    : </font></td><td width="85%"><font color="blue"><b>${parameters.loomQuota}</b></font></td>
			       			</tr>
			       			<#else>
			       			<tr>
			       				<td width="35%" keep-together="always"><font color="green">QuotaPerLoom    : </font></td><td width="85%"> <label  align="left" id="loomQuota" style="color: blue"></label></td>
			       			</tr>
			       			</#if>
			       			<#if parameters.loomQty?exists && parameters.loomQty?has_content> 
			       			<tr>
			       				<td width="35%" keep-together="always"><font color="green">Quantity        : </font></td><td width="85%"><font color="blue"><b> ${parameters.loomQty}</b></font></td>
			       			</tr> 
			       			<#else>
			       			<tr>
			       				<td width="35%" keep-together="always"><font color="green">Quantity        : </font></td><td width="85%"> <label  align="left" id="loomQty" style="color: blue"></label></td>
			       			</tr>
			       			</#if>
			       		</table>
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
			<div class="screenlet-title-bar">
         		<div class="grid-header" style="width:100%">
					<label>Indent Items</label>
					<span id="totalAmount"></span>
				</div>
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