	<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
	<link type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.css</@ofbizContentUrl>" rel="Stylesheet" />
	
	
	<style type="text/css">
	 	.labelFontCSS {
	    	font-size: 13px;
		}
	
	</style>
	
	<script type="text/javascript">
			var supplierAutoJson = ${StringUtil.wrapString(supplierJSON)!'[]'};
		//	var branchPartyJson = ${StringUtil.wrapString(branchPartyObj)!'[]'};
 		//	function getParties(branch){
	 //	var productStoreId=$(branch).val();
	     //  	var dataString="productStoreId=" + productStoreId ;
	     // alert(JSON.stringify(branchPartyJson[productStoreId]));
	      
	    //  branchCustomersJson=branchPartyJson[productStoreId];
	      
	    //  $("#partyId").autocomplete({ source: branchCustomersJson }).keydown(function(e){ 
		//		if (e.keyCode === 13){
		//	      	$('#boothId').autocomplete('close');
		  //  		$('#indententryinit').submit();
		  //  		return false;   
		//		}
		//	});
	 
		// }

		$(document).ready(function(){
	
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
			$("#partyId").autocomplete({ source: partyAutoJson }).keydown(function(e){ 
				if (e.keyCode === 13){
			      	$('#boothId').autocomplete('close');
		    		$('#indententryinit').submit();
		    		return false;   
				}
			});
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
		 
			var productStoreObjOnload=$('#productStoreIdFrom');
			if (productStoreObjOnload != null && productStoreObjOnload.val() != undefined ){
				showStoreCatalog(productStoreObjOnload);
			}
			
		});
		
		var partyName;
		function dispSuppName(selection){
		   var value = $("#partyId").val();
		   partyName = partyNameObj[value];
		   $("#partyName").html("<h4>"+partyName+"</h4>");
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
		
	      		<table width="70%" border="0" cellspacing="0" cellpadding="0">
			        <tr>
			        	<td>&nbsp;<input type="hidden" name="productSubscriptionTypeId"  value="CASH" />
					      	<input type="hidden" name="isFormSubmitted"  value="YES" />
					      	<input type="hidden" name="changeFlag"  value="${changeFlag?if_exists}" />
					      	<#if changeFlag?exists && changeFlag=="EditDepotSales">
							 	<input type="hidden" name="productStoreId" id="productStoreId" value="${productStoreId?if_exists}"/>  
							 	<input type="hidden" name="shipmentTypeId" id="shipmentTypeId" value="DEPOT_SHIPMENT"/> 
				           		<input type="hidden" name="salesChannel" id="salesChannel" value="DEPOT_CHANNEL"/>
						  	</#if>
					        <#if changeFlag?exists && changeFlag=='DepotSales'>
					         	<input type="hidden" name="shipmentTypeId" id="shipmentTypeId" value="DEPOT_SHIPMENT"/> 
					           	<input type="hidden" name="salesChannel" id="salesChannel" value="DEPOT_CHANNEL"/>
					        <#else>
					          	<input type="hidden" name="shipmentTypeId" id="shipmentTypeId" value="RM_DIRECT_SHIPMENT"/>
					          	<input type="hidden" name="salesChannel" id="salesChannel" value="RM_DIRECT_CHANNEL"/>
					        </#if>
			           	</td>
			          	<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Indent Date:</div></td>
			          	<#if effectiveDate?exists && effectiveDate?has_content>  
				  	  		<input type="hidden" name="effectiveDate" id="effectiveDate" value="${effectiveDate}"/>  
			          		<td align='left' valign='middle'>
			            		<div class='tabletext h3'>${effectiveDate}         
			            		</div>
			          		</td>       
			       	  	<#else> 
			          		<td valign='middle'>          
			            		<input class='h3' type="text" name="effectiveDate" id="effectiveDate" value="${defaultEffectiveDate}"/>           		
			            	</td>
			       	  	</#if>
			          	<td>&nbsp;</td>
			        <#if changeFlag?exists && changeFlag != "EditDepotSales">
			     
							<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Indent Received Date:</div></td>
							<#if indentReceivedDate?exists && indentReceivedDate?has_content>  
				  				<input type="hidden" name="indentReceivedDate" id="indentReceivedDate" value="${indentReceivedDate}"/>  
				   				<td valign='middle'>
									<div class='h3'>${indentReceivedDate}         
									</div>
				   				</td>  
							<#else> 
				 				<td valign='middle'>          
									<input class='h3' type="text" name="indentReceivedDate" id="indentReceivedDate" value="${defaultEffectiveDate}"/>    
				 				</td>
							</#if>
					
					</#if>
			        
			        </tr>
					<tr><td><br/></td></tr>

			        <tr>
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
				          			<span class="tooltip" id="branchName"></span>
				          		</td>
				          		</#if>
			        	</#if>
			        	
			    
			        <#if changeFlag?exists && changeFlag !='EditDepotSales' && changeFlag !='ICPTransferSale'>
		        	
			          		<td>&nbsp;</td>
			          		<td align='left' valign='middle' nowrap="nowrap"><div class='h3'> Tax Type:</div></td>
			       			<#if orderTaxType?exists && orderTaxType?has_content>  
				  	  			<input type="hidden" name="orderTaxType" id="orderTaxType" value="${orderTaxType?if_exists}"/>  
			          			<td valign='middle'>
			            			<div class='tabletext h3'>${orderTaxType?if_exists}</div>
			          			</td>       	
			       			<#else>      	         
			          			<td valign='middle'>
			          				<select name="orderTaxType" id="orderTaxType" class='h3'>
			          					<option value="INTRA">With in State</option>
			          					<option value="INTER">Out of State</option>
			          				</select>
			          			</td>
			       			</#if>

		        	
				      		<td>&nbsp;</td>
				      		<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Customer Indent Number:</div></td>
				   			<#if PONumber?exists && PONumber?has_content>  
				  	  			<input type="hidden" name="PONumber" id="PONumber" value="${PONumber?if_exists}"/>  
				      			<td valign='middle'>
				        			<div class='tabletext h3'>${PONumber?if_exists}</div>
				      			</td>       	
				   			<#else>      	         
				      			<td valign='middle'>
				      				<input type="text" name="PONumber" id="PONumber" />    
				      			</td>
				   			</#if>
				    	
				    	
		        	</#if>
				</tr>
			        
			       
					<tr><td><br/></td></tr>
					<tr>
			      		<td>&nbsp;</td>
			      		<td align='left' valign='middle' nowrap="nowrap"><div class='h3'> Indent Type:</div></td>
			   			<#if billingType?exists && billingType?has_content>  
			  	  			<input type="hidden" name="billingType" id="billingType" value="${billingType?if_exists}"/>  
			      			<td valign='middle'>
			        			<div class='tabletext h3'>${billingType?if_exists}</div>
			      			</td>       	
			   			<#else>      	         
			      			<td valign='middle'>
			      				<select name="billingType" id="billingType" class='h3'>
			      					<option value="Direct"> Direct </option>
			      					<option value="onBehalfOf"> On Behalf Of </option>
			      				</select>
			      			</td>
			   			</#if>
			        
			        <#if changeFlag?exists && changeFlag != "EditDepotSales">
				      	
				    	<#if changeFlag?exists && changeFlag == "IcpSales">
					    	
					      		<td>&nbsp;</td>
					      		<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Promotion Adj. Amt:</div></td>
					      		<td>&nbsp;</td>
					   			<#if promotionAdj?exists && promotionAdj?has_content>  
					  	  			<input type="hidden" name="promotionAdj" id="promotionAdj" value="${promotionAdj?if_exists}"/>  
					      			<td valign='middle'>
					        			<div class='tabletext h3'>Rs. ${promotionAdj?if_exists}</div>
					      			</td>       	
					   			<#else>      	         
					      			<td valign='middle'>
					      				<input type="text" name="promotionAdj" id="promotionAdj" />    
					         			<span class="tooltip">Fill Promotion Adjustment Amount if applicable</span>       
					      			</td>
					   			</#if>
					    	
				    	</#if>
				    	 	<#--
					        <tr>
					      		<td>&nbsp;</td>
					      		<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Sale From Store:</div></td>
					      		<td>&nbsp;</td>
					   			<#if productStoreId?exists && productStoreId?has_content>  
					  	  			<input type="hidden" name="productStoreId" id="productStoreId" value="${productStoreId?if_exists}"/>  
					      			<td valign='middle'>
					        			<div class='tabletext h3'>${productStoreId?if_exists}</div>
					      			</td>       	
					   			<#else>      	         
					      			<td valign='middle'>
					      				<select name="productStoreIdFrom" id="productStoreIdFrom"   onchange="javascript:showStoreCatalog(this)"  class='h3'>
					      					<option value="1003"> STORE </option>
					      				</select>
					      			</td>
					   			</#if>
					        </tr>
					        -->
					    	<#--
					    	<tr>
					      		<td>&nbsp;</td>
					      		<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Product Category:<font color="red">*</font></div></td>
					      		<td>&nbsp;</td>
					      		<td>
					      		<#if productCategoryId?has_content>
					      			<div class='tabletext h3'>${productCategoryId?if_exists}</div>
					      			<input type="hidden" name="productCatageoryId" id="productCatageoryId" value="${productCategoryId?if_exists}"/> 
					      		<#else>
						      			<select id="productCatageoryId" name="productCatageoryId" class='h4' multiple="multiple" >
										</select>
									<input type="hidden" name="productCatageoryId" id="productCatageoryId" value="COTTON"/> 
					      		</#if>
								</td>
							</tr>
							-->
			    	</#if>
          		<td>&nbsp;</td>
		          		<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Scheme Category</div></td>
		       			<#if orderTaxType?exists && orderTaxType?has_content>  
			  	  			<input type="hidden" name="schemeCategory" id="schemeCategory" value="${schemeCategory?if_exists}"/>  
		          			<td valign='middle'>
		            			<div class='tabletext h3'>${schemeCategory?if_exists}</div>
		          			</td>       	
		       			<#else>      	         
		          			<td valign='middle'>
		          				<select name="schemeCategory" id="schemeCategory" class='h3'>
		          					<option value="MGPS">MGPS</option>
		          					<option value="10PercentDiscount">10% Discount</option>
		          					<option value="MGPS">General</option>
		          					<option value="MGPS_10Pecent">MGPS + 10%</option>
		          				</select>
		          			</td>
		       			</#if>
			        
			        <#--<tr><td><br/></td></tr>
			        <tr><td><br/></td></tr>
					<tr>
			        	<td>&nbsp;</td>
			          	<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Party Category:</div></td>
			          	<td>&nbsp;</td>
			          	<td valign='middle'>
			      			<input type="text" name="partyGroup" id="partyGroup"/>
			      			<span class="tooltip" id="branchName"></span>
			      		</td>
			        	
			        </tr>-->

			        
			         <td>&nbsp;</td>
			          <td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Supplier :<font color="red">*</font></div></td>
			        
			          <#if changeFlag?exists && changeFlag=='EditDepotSales'>
							<#if suplierPartyId?exists && suplierPartyId?has_content>  
					  	  		<input type="hidden" name="suplierPartyId" id="suplierPartyId" value="${suplierPartyId?if_exists}"/>  
				          		<td valign='middle'>
				            		<div class='tabletext h3'>
				               			${suplierPartyId}    <#--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="javascript:processChangeIndentParty()" class="buttontext">Party Change</a>-->             
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
				          			<span class="tooltip" id="suplierPartyName"></span>
				          		</td>
				          		</#if>
			        	</#if>
			        
			        
			         </tr>
			        <tr><td></td></tr>
			        <tr><td><input type="hidden" name="disableAcctgFlag" id="disableAcctgFlag" value="${disableAcctgFlag?if_exists}"/><br/></td></tr>    
			        <tr>
			      <td>&nbsp;</td>
			          <td align='left' valign='middle' nowrap="nowrap"><div class='h3'><#if changeFlag?exists && changeFlag=='AdhocSaleNew'>Retailer:<#elseif changeFlag?exists && changeFlag=='InterUnitTransferSale'>KMF Unit ID:<#else>Party:</#if><font color="red">*</font></div></td>
			          <td>&nbsp;</td>
			          
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
				            		    <#assign partyIdentification = delegator.findOne("PartyIdentification", {"partyId" :party.partyId,"partyIdentificationTypeId":"PSB_NUMBER"}, true)>
         								<#assign passBookDetails=partyIdentification?if_exists>
				               			${party.groupName?if_exists} [ ${passBookDetails.idValue?if_exists}] ${partyAddress?if_exists} <#--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="javascript:processChangeIndentParty()" class="buttontext">Party Change</a>-->             
				            		</div>
				          		</td>       
				       		<#else>               
				          		<td valign='middle'>
				          			<input type="text" name="partyId" id="partyId" onblur= 'javascript:dispSuppName(this);' />
				          			<span class="tooltip" id="partyName"></span>
				          			<span class="tooltip">Input party code and press Enter</span>
				          		</td>
				          	</#if>
			        	</#if>
					</tr>
					<#-- Order Message Field Starts -->
				<#-- <#if changeFlag?exists && !(changeFlag=='AdhocSaleNew')>
					
			        	<tr><td><br/></td></tr>
			        	<#if parameters.orderMessage?exists && parameters.orderMessage?has_content>  
					        <tr>
					          <td>&nbsp;</td>
					          <td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Message:</div></td>
					          <td>&nbsp;</td>
						  	  		<input type="hidden" name="orderMessage" id="orderMessage" value="${parameters.orderMessage?if_exists}"/>  
					          		<td valign='middle'>
					            		<div class='tabletext h3'>
					               			${parameters.orderMessage?if_exists}              
					            	</div>
					          	</td> 
					         </tr>
				        <#elseif orderMessage?exists && orderMessage?has_content> 
				         	<tr>
				         		<td colspan="4" >
				         			<table  border="0" cellspacing="0" cellpadding="0">
					         			<tbody>
								            <tr>
								                <td width="20%" align="right" class="label labelFontCSS">&nbsp; Message: &nbsp; &nbsp; &nbsp; &nbsp;</td>
								                <td width="50%"><textarea name="orderMessage" id="orderMessage" cols="25" rows="4">${orderMessage?if_exists}</textarea></td>
								            </tr>
					         			</tbody>
				         			</table>
								</td> 
				        	</tr> 
				        <#else> 
				        	<tr>
				         		<td colspan="4" >
				         			<table  border="0" cellspacing="0" cellpadding="0">
				         				<tbody>
				            				<tr>
								                <td width="20%" align="right" class="label labelFontCSS">&nbsp; Message: &nbsp; &nbsp; &nbsp; &nbsp;</td>
								                <td width="50%"><textarea name="orderMessage" id="orderMessage"  cols="25" rows="4"></textarea></td>
								            </tr>
				         				</tbody>
				         			</table>
								</td> 
				        	</tr> 
				        </#if>
			        </#if>-->
				    
	                   
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
				<div id="myGrid1" style="width:100%;height:350px;"></div>
					  
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
	 
