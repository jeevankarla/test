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
				minDate: new Date(),
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
	<#include "BranchSalesOrderInternalForm.ftl"/>
	<#include "EditUDPPriceDepot.ftl"/>
	
	<div class="full">
	<div>
	<div class="screenlet" style="width:95%">
		<div class="screenlet-title-bar">
	        <div class="grid-header" style="width:100%">
				<label>Branch Sales Entry </label>
			</div>
	    </div>

	    <div class="screenlet-body">
	    	<form method="post" name="indententryinit" action="<@ofbizUrl>BranchSalesOrder</@ofbizUrl>" id="indententryinit" onsubmit="validateParty()">
		
	      		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	               	
	               	<tr>
			           	<td>&nbsp;</td>
			           	<td>&nbsp;</td>
			          	<td>&nbsp;</td>
		       	  		<td>&nbsp;</td>
						<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Branch:<font color="red">*</font></div></td>
			          	<#if changeFlag?exists && changeFlag=='EditDepotSales'>
							<#if productStoreId?exists && productStoreId?has_content>  
					  	  		<input type="hidden" name="productStoreId" id="productStoreId" value="${productStoreId?if_exists}"/>  
				          		<td valign='middle'>
				            		<div class='tabletext h3'>
				               			${productStoreId}    <#--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="javascript:processChangeIndentParty()" class="buttontext">Party Change</a>-->             
				            		</div>
				          		</td>       
				          	</#if>
				    	<#else>
							<#if parameters.productStoreId?exists && parameters.productStoreId?has_content>  
					  	  		<input type="hidden" name="productStoreId" id="productStoreId" value="${parameters.productStoreId?if_exists}"/>  
				          		<td valign='middle'>
				            		<div class='tabletext h3'>
				               			${parameters.productStoreId}  <#--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="javascript:processChangeIndentParty()" class="buttontext">Party Change</a>-->             
				            		</div>
				          		</td>       
				          	<#else>
				          		<td valign='middle'>
				          			<input type="text" name="productStoreId" id="productStoreId" onblur= 'javascript:getParties(this);' />
				          		</td>
				          	</#if>
			        	</#if>
		       	  		<td><span class="tooltip" id="branchName"></span></td>
	               	</tr>
	               	
	               	
	               	<tr>
	               		<td>&nbsp;</td>
	               		
	               		<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Scheme Category</div></td>
		       			<#if parameters.schemeCategory?exists && parameters.schemeCategory?has_content>  
			  	  			<input type="hidden" name="schemeCategory" id="schemeCategory" value="${parameters.schemeCategory?if_exists}"/>  
		          			<td valign='middle'>
		            			<div class='tabletext h3'>${parameters.schemeCategory?if_exists}</div>
		          			</td>       	
		       			<#else>      	         
		          			<td valign='middle'>
		          				<select name="schemeCategory" id="schemeCategory" class='h3' style="width:162px">
		          					<option value="MGPS">MGPS</option>
		          					<option value="General">General</option>
		          					<option value="MGPS_10Pecent">MGPS + 10%</option>
		          				</select>
		          			</td>
		       			</#if>
	               					           	
			           	<td>&nbsp;</td>
			           	
			           	<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Indent Date:</div></td>
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
				            		<div class='tabletext h3'>${effectiveDate}         
				            		</div>
				          		</td>       
			       	  		<#else> 
				          		<td valign='left'>          
				            		<input class='h3' type="text" name="effectiveDate" id="effectiveDate" value="${defaultEffectiveDate}"/>           		
				            	</td>
			       	  		</#if>
		       	  		
		       	  		<td>&nbsp;</td>
			           	
			           	<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Supplier :<font color="red">*</font></div></td>
			          	<#if changeFlag?exists && changeFlag=='EditDepotSales'>
							<#if suplierPartyId?exists && suplierPartyId?has_content>  
					  	  		<input type="hidden" name="suplierPartyId" id="suplierPartyId" value="${suplierPartyId?if_exists}"/>  
				          		<td valign='middle'>
				            		<div class='tabletext h3'>
				               			${suplierPartyId}  [${suplierPartyName}]  <#--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="javascript:processChangeIndentParty()" class="buttontext">Party Change</a>-->             
				            		</div>
				          		</td>       
				          	</#if>
				    	<#else>
							<#if parameters.suplierPartyId?exists && parameters.suplierPartyId?has_content>  
					  	  		<input type="hidden" name="suplierPartyId" id="suplierPartyId" value="${parameters.suplierPartyId?if_exists}"/>  
				          		<td valign='middle'>
				            		<div class='tabletext h3'>
				               			${parameters.suplierPartyId} [${suppPartyName?if_exists}] <#--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="javascript:processChangeIndentParty()" class="buttontext">Party Change</a>-->             
				            		</div>
				          		</td>       
				          	<#else>
				          		<td valign='middle'>
				          			<input type="text" name="suplierPartyId" id="suplierPartyId"/>
				          		</td>
				          	</#if>
			        	</#if>
		       	  		<td><span class="tooltip" id="suplierPartyName"></span></td>
		       	  		
		       	  		
	               	</tr> 
	               	
	               	<tr><td><br/></td></tr>
	               	
	               	<tr>
		       	  		
		       	  		<td>&nbsp;</td>
		       	  		
		       	  		<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Indent Tax Type:</div></td>
		       			<#if orderTaxType?exists && orderTaxType?has_content>  
			  	  			<input type="hidden" name="orderTaxType" id="orderTaxType" value="${orderTaxType?if_exists}"/>  
		          			<td valign='middle'>
		            			<div class='tabletext h3'>${orderTaxType?if_exists}</div>
		          			</td>       	
		       			<#else>      	         
		          			<td valign='middle'>
		          				<select name="orderTaxType" id="orderTaxType" class='h3' style="width:162px">
		          					<option value="INTRA">With in State</option>
		          					<option value="INTER">Out of State</option>
		          				</select>
		          			</td>
		       			</#if>
		       			
		       			<td>&nbsp;</td>
		       	  		
		       	  		<#if changeFlag?exists && changeFlag != "EditDepotSales">
							<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Received Date:</div></td>
							<#if indentReceivedDate?exists && indentReceivedDate?has_content>  
				  				<input type="hidden" name="indentReceivedDate" id="indentReceivedDate" value="${indentReceivedDate}"/>  
				   				<td valign='middle'>
									<div class='h3'>${indentReceivedDate}         
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
		       	  		
		       	  		<td align='left' valign='middle' nowrap="nowrap"><div class='h3'> Indent Type:</div></td>
			   			<#if parameters.billingType?exists && parameters.billingType?has_content>  
			  	  			<input type="hidden" name="billingType" id="billingType" value="${parameters.billingType?if_exists}"/>  
			      			<td valign='middle'>
			        			<div class='tabletext h3'>${parameters.billingType?if_exists}</div>
			      			</td>       	
			   			<#else>      	         
			      			<td valign='middle'>
			      				<select name="billingType" id="billingType" class='h3' style="width:162px" onchange="addSocietyField(this)" >
			      					<option value="Direct"> Direct </option>
			      					<option value="onBehalfOf"> On Behalf Of </option>
			      				</select>
			      			</td>
			   			</#if>
		       			
		       			<td>&nbsp;</td>
		       			
		       			<td align='left' valign='middle' nowrap="nowrap"><div class='h3'><#if changeFlag?exists && changeFlag=='AdhocSaleNew'>Retailer:<#elseif changeFlag?exists && changeFlag=='InterUnitTransferSale'>KMF Unit ID:<#else>Party:</#if><font color="red">*</font></div></td>
				        <#if changeFlag?exists && changeFlag=='EditDepotSales'>
							<#if partyId?exists && partyId?has_content>  
					  	  		<input type="hidden" name="partyId" id="partyId" value="${partyId?if_exists}"/>  
				          		<td valign='middle'>
				            		<div class='tabletext h3'>
				               			${partyId} [ ${partyName?if_exists} ] ${partyAddress?if_exists}  <#--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="javascript:processChangeIndentParty()" class="buttontext">Party Change</a>-->             
				            		</div>
				          		</td>       
				          	</#if>
				    	<#else>
						 	<#if party?exists && party?has_content>  
					  	  		<input type="hidden" name="partyId" id="partyId" value="${party.partyId.toUpperCase()}"/>  
				          		<td valign='middle' colspan="2">
				            		<div class='tabletext h3'>
				            		    <#assign partyIdentification = delegator.findOne("PartyIdentification", {"partyId" :party.partyId,"partyIdentificationTypeId":"PSB_NUMBER"}, true)?if_exists>
         								<#assign passBookDetails=partyIdentification?if_exists>
				               			${party.groupName?if_exists} ${party.firstName?if_exists}${party.lastName?if_exists} [ ${passBookDetails.idValue?if_exists}] ${partyAddress?if_exists} <#--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="javascript:processChangeIndentParty()" class="buttontext">Party Change</a>-->             
				            		</div>
				          		</td>       
				       		<#else>               
				          		<td valign='middle'>
                 					 <@htmlTemplate.lookupField value='${requestParameters.partyId?if_exists}' formName="indententryinit" name="partyId" id="partyId" fieldFormName="LookupEmpanelledPartyName"/>
				          			<#--<input type="text" name="partyId" id="partyId" onblur= 'javascript:dispSuppName(this);' />-->
				          		</td>
				          		<td colspan="2"><span class="tooltip" id="partyName"><input type="hidden" name="disableAcctgFlag" id="disableAcctgFlag" value="${disableAcctgFlag?if_exists}"/></td></span></td>
			        			<#--><td><span class="tooltip">Input party code and press Enter</span></td>-->
				          	</#if>
			        	</#if>
						
	               	</tr>
	             <#if parameters.societyPartyId?exists && parameters.societyPartyId?has_content>  
					<tr>
		       	  		<td>&nbsp;</td>
		       	  		
		       	  		<td align='left' valign='middle' nowrap="nowrap"><div class='h3'></div></td>
			   		     	         
			      			<td valign='middle'>
			      				
			      			</td>
			   		
		       			<td>&nbsp;</td>
		       			
		       			<td align='left' valign='middle' nowrap="nowrap"><div class='h3'><#if changeFlag?exists && changeFlag=='AdhocSaleNew'>Retailer:<#elseif changeFlag?exists && changeFlag=='InterUnitTransferSale'>KMF Unit ID:<#else> Society Party:</#if><font color="red">*</font></div></td>
				        <#if changeFlag?exists && changeFlag=='EditDepotSales'>
							<#if societyPartyId?exists && societyPartyId?has_content>  
					  	  		<input type="hidden" name="societyPartyId" id="societyPartyId" value="${societyPartyId?if_exists}"/>  
				          		<td valign='middle'>
				            		<div class='tabletext h3'>
				               			${societyPartyId} [ ${societyPartyName?if_exists} ] ${societyPartyAddress?if_exists}  <#--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="javascript:processChangeIndentParty()" class="buttontext">Party Change</a>-->             
				            		</div>
				          		</td>       
				          	</#if>
				    	<#else>
					  	  		<input type="hidden" name="societyPartyId" id="societyPartyId" value="${parameters.societyPartyId.toUpperCase()}"/>  
				          		<td valign='middle' colspan="2">
				            		<div class='tabletext h3'>
		            				 <#assign societyPartyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, parameters.societyPartyId, false)>
 				               			${societyPartyName?if_exists} [ ${parameters.societyPartyId?if_exists}] <#--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="javascript:processChangeIndentParty()" class="buttontext">Party Change</a>-->             
				            		</div>
				          		</td>       
				       		
			        	</#if>
						
	               	</tr>

					<#else>

	               	  	<tr id='societyfield'>
		       	  		<td>&nbsp;</td>
		       	  		
		       	  		<td align='left' valign='middle' nowrap="nowrap"><div class='h3'></div></td>
			   		     	         
			      			<td valign='middle'>
			      				
			      			</td>
			   		
		       			<td>&nbsp;</td>
		       			
		       			<td align='left' valign='middle' nowrap="nowrap"><div class='h3'><#if changeFlag?exists && changeFlag=='AdhocSaleNew'>Retailer:<#elseif changeFlag?exists && changeFlag=='InterUnitTransferSale'>KMF Unit ID:<#else> Society Party:</#if><font color="red">*</font></div></td>
				        <#if changeFlag?exists && changeFlag=='EditDepotSales'>
							<#if societyPartyId?exists && societyPartyId?has_content>  
					  	  		<input type="hidden" name="societyPartyId" id="societyPartyId" value="${societyPartyId?if_exists}"/>  
				          		<td valign='middle'>
				            		<div class='tabletext h3'>
				               			${societyPartyId} [ ${societyPartyName?if_exists} ] ${societyPartyAddress?if_exists}  <#--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="javascript:processChangeIndentParty()" class="buttontext">Party Change</a>-->             
				            		</div>
				          		</td>       
				          	</#if>
				    	<#else>
						 	<#if parameters.societyPartyId?exists && parameters.societyPartyId?has_content>  
					  	  		<input type="hidden" name="societyPartyId" id="societyPartyId" value="${parameters.societyPartyId.toUpperCase()}"/>  
				          		<td valign='middle' colspan="2">
				            		<div class='tabletext h3'>
				               			${parameters.societyPartyName?if_exists} [ ${parameters.societyPartyId?if_exists}] <#--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="javascript:processChangeIndentParty()" class="buttontext">Party Change</a>-->             
				            		</div>
				          		</td>       
				       		<#else>               
				          		<td valign='middle'>
				          			<input type="text" name="societyPartyId" id="societyPartyId" onblur= 'javascript:dispSuppName(this);' />
				          		</td>
				          		<td colspan="2"><span class="tooltip" id="societyPartyName"><input type="hidden" name="disableAcctgFlag" id="disableAcctgFlag" value="${disableAcctgFlag?if_exists}"/></td></span></td>
				          	</#if>
			        	</#if>
						
	               	</tr>
	               	</#if>
				<#if party?exists && party?has_content>
					<tr>
					</tr>
					<#else>
	               		<tr>
		       	  		<td>&nbsp;</td>
		       	  		<td>&nbsp;</td>
		       	  		<td>&nbsp;</td>
			   			<td>&nbsp;</td>
		       			<td>&nbsp;</td>
		       			<td align='left' valign='middle' nowrap="nowrap">
		       					<input type="submit" style="padding:.3em" value="submit" name="submit" id="submit" onclick= 'javascript:formSubmit(this);' />
		       			</td>
	               		
						</tr>
	               	</#if>
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
	
	<#if changeFlag?exists && changeFlag=='AdhocSaleNew'>			
		<div class="screenlet">
		    <div class="screenlet-body">
		 		<div class="grid-header" style="width:100%">
					<label>Last Change <#if lastChangeSubProdMap?exists && lastChangeSubProdMap?has_content>[made by ${lastChangeSubProdMap.modifiedBy?if_exists} at ${lastChangeSubProdMap.modificationTime?if_exists}] &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Entries Made by ${parameters.userLogin.userLoginId} Today: ${entrySize?if_exists}</#if></label>
				</div>
				<div id="myGrid2" style="width:100%;height:75px;"></div>		
		    </div>
		</div>
	</#if>     
	</div>
	
	<div>
		<div class="screenlet" >
		    <div class="screenlet-body">
		 		<div class="grid-header" style="width:100%">
					<span id="totalAmount"></span>
				</div>
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
				    		<input type="submit" style="padding:.3em" id="changeSave" value="Submit" onclick="javascript:processIndentEntry('indententry','<@ofbizUrl>${formAction}</@ofbizUrl>');"/>
				    		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				    		<input type="submit" style="padding:.3em" id="changeCancel" value="Cancel" onclick="javascript:processIndentEntry('indententry','<@ofbizUrl>BranchSalesOrder</@ofbizUrl>');"/>   	
				    	</div>     
					</#if>
					
				</div>
			</div>     
		</div>
	</div>
	 
