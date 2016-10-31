<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	

<style type="text/css">

	.grid-header {
		    border: 1px solid gray;
		    border-bottom: 0;
		    border-top: 0;
		    background: url('../images/header-bg.gif') repeat-x center top;
		    color: black;
		    height: 24px;
		    line-height: 24px;
		    border-radius: 7px;
		    background-color: #FFFFFF;
		}


</style>



<script type="text/javascript">
	
$(document).ready(function(){
		

		$( "#effectiveDate" ).datepicker({
			dateFormat:'d MM, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#effectiveDate" ).datepicker("option", selectedDate);
			}
		});
		$( "#suppInvoiceDate" ).datepicker({
			dateFormat:'d MM, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#SInvoiceDate" ).datepicker(selectedDate);
			}
		});
		$('#ui-datepicker-div').css('clip', 'auto');
			$("#partyId").autocomplete({ source: partyAutoJson }).keydown(function(e){
			if (e.keyCode === 13){
		      	 $('#partyId').autocomplete('close');
	    			$('#purchaseEntryInit').submit();
	    			return false;   
			}
		});
		//prepareApplicableOptions();
    	//setupGrid2();
	});
</script>
<#assign changeRowTitle = "Changes">                

<#include "PurchaseInvoiceDepotInc.ftl"/>
<#include "EditPurchaseInvoicePrice.ftl"/>

<div class="full" style="width:100%">
	
		<div class="screenlet-title-bar">
	         <div class="grid-header" style="width:100%">
				<label>Purchase Invoice Entry</label>
			</div>
	     </div>
	      
	    <div class="screenlet-body">
	    <form method="post" name="purchaseEntryInit" action="<@ofbizUrl>MaterialInvoiceInit</@ofbizUrl>" id="purchaseEntryInit">  
	      <table width="100%" border="0" cellspacing="0" cellpadding="0">
			    	<tr>
				        <td width="40%">
			    			<table  border="0" cellspacing="0" cellpadding="0" class="form-style-8">
				        		<tr>
	          						<input type="hidden" name="isFormSubmitted"  value="YES" />
							        <td align='left' valign='middle' nowrap="nowrap"><div class='h4'>Invoice Date :</div></td>
							        <#if effectiveDate?exists && effectiveDate?has_content>  
								  	 	<input type="hidden" name="effectiveDate" id="effectiveDate" value="${effectiveDate}"/>  
							          	<td valign='middle'>
							            	<div class='tabletext h4'><font color="green">${effectiveDate}         
							            	</div>
							          	</td>       
							       	<#else> 
							          	<td valign='middle'><font color="green">          
							           		<input class='h4' type="text" name="effectiveDate" id="effectiveDate" value="${defaultEffectiveDate}"/>           		
							           	</td>
							       	</#if>
							       	
							       	<td><br/></td>
							       	<td align='left' valign='middle' nowrap="nowrap"><div class='h4'>Shipment Date:</div></td>
									<#if shipmentDate?exists && shipmentDate?has_content>  
					  	  				<input type="hidden" name="estimatedShipDate" id="estimatedShipDate" value="${shipmentDate?if_exists}"/>  
						          		<td valign='middle'>
						            		<div class='tabletext h4'><font color="green">
						            			${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(shipmentDate, "dd MMMM, yyyy")?if_exists}
						            		</div>
						          		</td>
				          			</#if>
	        				   </tr>
	        				   
	        				   <#--
	        <tr>
	          <td align='left' valign='middle' nowrap="nowrap"><div class='h4'>Shipment Date:</div></td>
				<#if shipmentDate?exists && shipmentDate?has_content>  
		  	  		<input type="hidden" name="estimatedShipDate" id="estimatedShipDate" value="${shipmentDate?if_exists}"/>  
	          		<td valign='middle'>
	            		<div class='tabletext h4'>
	            			${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(shipmentDate, "dd MMMM, yyyy")?if_exists}
	            		</div>
	          		</td>       
	          	</#if>
	        </tr> 
	        -->
	        <tr><td><br/></td></tr>
	        <tr>
	          <td align='left' valign='middle' nowrap="nowrap"><div class='h4'>Supplier:</div></td>
				<#if partyId?exists && partyId?has_content>  
		  	  		  <#--  <input type="hidden" name="partyId" id="partyId" value="${partyId?if_exists}"/>  -->
	          		<td valign='middle' colspan="5">
	            		<div class='tabletext h4'><font color="green">
	            			<#assign supplierName = delegator.findOne("PartyNameView", {"partyId" : partyId}, true) />
	               			${partyId?if_exists} [ ${supplierName.groupName?if_exists} ${supplierName.firstName?if_exists} ${supplierName.lastName?if_exists}]             
	            		</div>
	          		</td>       
	          	</#if>
	        </tr>
	         <#-- Showing BillToParty: -->
	         <tr>
	          <td align='left' valign='middle' nowrap="nowrap"><div class='h4'>BillToParty:</div></td>
				<#if billToPartyId?exists && billToPartyId?has_content>  
		  	  	  <#--	<input type="hidden" name="partyId" id="partyId" value="${billToPartyId?if_exists}"/>  -->
	          		<td valign='middle' colspan="5">
	            		<div class='tabletext h4'><font color="green">
	            			<#assign supplierName = delegator.findOne("PartyNameView", {"partyId" : billToPartyId}, true) />
	               			${billToPartyId?if_exists} [ ${supplierName.groupName?if_exists} ${supplierName.firstName?if_exists} ${supplierName.lastName?if_exists}]             
	            		</div>
	          		</td>       
	          	</#if>
	        </tr>
	        <tr>
	        <td align='left' valign='middle' nowrap="nowrap"><div class='h4'>Customer:</div></td>
	        <#if weaverPartyId?exists && weaverPartyId?has_content>  
	      		<td valign='middle' colspan="5">
	        		<div class='tabletext h4'><font color="green">
	        			<#assign cutomerName = delegator.findOne("PartyNameView", {"partyId" : weaverPartyId}, true) />
	           			${weaverPartyId?if_exists} [ ${cutomerName.groupName?if_exists} ${cutomerName.firstName?if_exists} ${cutomerName.lastName?if_exists}]             
	        		</div>
	      		</td>       
	         </#if>
	        </tr>
	        <tr><td><br/></td></tr>
	        <tr>
	          <input type="hidden" name="isFormSubmitted"  value="YES" />
	          <td align='left' valign='middle' nowrap="nowrap"><div class='h4'>Tally Reference No :</div></td>
            <#-->	<#if tallyRefNo?exists && tallyRefNo?has_content>  
		  	  	<input type="hidden" name="tallyrefNo" id="tallyrefNo" value="${tallyRefNo?if_exists}"/>  
	          	<td valign='middle'>
	            	<div class='tabletext h3'>${tallyRefNo?if_exists}         
	            	</div>
	          	</td>       
	       	  <#else> 
	          	  	<td valign='middle'>          
	            		<input class='h3' type="text" name="tallyrefNo" id="tallyrefNo" value="${tallyRefNo}"/>           		
	            	</td>
	       	  </#if> -->
	       	  
	       	  <td valign='middle'><font color="green">          
	            		<input class='h4' type="text" name="tallyrefNo" id="tallyrefNo" />           		
	            	</td>
	       	  
	        </tr>
	       	<tr>
	            <td align='left' valign='middle' nowrap="nowrap"><div class='h4'>Vehicle No:</div></td>
				<#if vehicleId?exists && vehicleId?has_content>  
		  	  		<input type="hidden" name="vehicleId" id="vehicleId" value="${vehicleId?if_exists}"/>  
	          		<td valign='middle' colspan="5"><font color="green">
	            		<div class='tabletext h4'>${vehicleId?if_exists}</div> 
	          		</td>       
	          	</#if>
	        </tr> 
	        <tr><td><br/></td></tr>
	       	<tr>
	            <td align='left' valign='middle' nowrap="nowrap"><div class='h4'>Purchase Order No:</div></td>
				<#if orderId?exists && orderId?has_content>  
		  	  		<input type="hidden" name="orderId" id="orderId" value="${orderId?if_exists}"/> 
		  	  		<#if orderNo?exists && orderNo?has_content>   
		          		<td valign='middle' colspan="5"><font color="green">
		            		<div class='tabletext h4'>${orderNo?if_exists}</div> 
		          		</td>   
		          	<#else>	
			           <td valign='middle' colspan="5"><font color="green">
		                   <div class='tabletext h4'>${orderId?if_exists}</div> 
		          	   </td> 
	          		</#if>   
	          	</#if>
	        </tr>
	        
	        <tr>
	        	
		       	  		<td align='left' nowrap="nowrap"><div class='h4'>Purchase Tax Type:</div></td>
		       	  		<td valign='middle'>
	          				<select name="purchaseTaxType" id="purchaseTaxType" class='h4' style="width:120px">
	          					<#if purchaseTaxType?exists && purchaseTaxType?has_content>
	          						<#if purchaseTaxType == "Intra-State">
	          							<option value="Intra-State" selected>With In State</option>
	          						<#else>
	          							<option value="Inter-State" selected>Inter State</option>
	          						</#if> 
	          					</#if> 
	          					<option value="Intra-State">With In State</option>
	          					<option value="Inter-State">Inter State</option>
	          				</select>
	          				<#if supplierGeoId?exists && supplierGeoId?has_content>
					    		<input type="hidden" name="supplierGeoId" id="supplierGeoId" size="18" maxlength="60" value="${supplierGeoId}" readonly/>
					    	</#if>
					      	<#if branchGeoId?exists && branchGeoId?has_content>
					    		<input type="hidden" name="branchGeoId" id="branchGeoId" size="18" maxlength="60" value="${branchGeoId}" readonly/>
					    	</#if>
	          			</td>
		          		<td>&nbsp;</td>
		          		<td align='left' nowrap="nowrap"><div class='h4'>Tax Form:</div></td>
		       			<td valign='middle'>
	          				<select name="purchaseTitleTransferEnumId" id="purchaseTitleTransferEnumId" class='h4' style="width:205px">
	          					<#if purchaseTitleTransferEnumId?exists && purchaseTitleTransferEnumId?has_content>
	          						<#if purchaseTitleTransferEnumId == "CST_CFORM">
	          							<option value="CST_CFORM" selected>Transaction With C Form</option>
	          						</#if>
	          						<#if purchaseTitleTransferEnumId == "CST_NOCFORM">
	          							<option value="CST_NOCFORM" selected>Transaction Without C Form</option>
	          						</#if>
	          						<#if purchaseTitleTransferEnumId == "NO_E2_FORM">
	          							<option value="NO_E2_FORM" selected></option>
	          						</#if> 
	          					</#if> 
	          				
	          				
	          					<option value="CST_CFORM">Transaction With C Form</option>
	          					<option value="CST_NOCFORM">Transaction Without C Form</option>
	          					<option value="NO_E2_FORM"></option>
	          				</select>
	          			</td>
		       		</tr>  
		       		</table>
	    			</td>
	    			<td border="1">
	    			<#if ShipmentDetail?has_content>
	    				<table  class="form-style-7">
				      	  <tr>
				      	  
				      		  <td align="center" colspan="5">
				       			<font ><b><u> Shipment History</u></b></font>
				       		 </td>
	    				 </tr>
	    				 <tr>
	    				 	<td width="10%" align="center">
								<b>ShipmentId</b>	    						 
			    			</td>
	    					<td width="40%" align="center">
								<b>Supplier Invoice No.</b>	    						 
			    			</td>
			    			<td width="20%" align="center">
								<b>Supplier Invoice Date</b>    						 
			    			</td>
			    			<td width="40%" align="center">
			    				<b>Delivery Challan No.</b>
			    			</td>
			    			<td width="20%" align="center">
			    				<b>Delivery Challan Date</b>
			    			</td>
			    			<td width="40%" align="center">
			    				<b>LR No</b>
			    			</td>
			    			<td width="20%" align="center">
			    				<b>LR Date</b>
			    			</td>
			    			<td width="40%" align="center">
								<b>Carrier/Courier Name</b>  						 
			    			</td>
			    			<td width="20%" align="center">
								<b>Freight Charges</b>  						 
			    			</td>
	    				 </tr>
	    				 <tr>
	    				 	<td width="10%">
	    						 ${ShipmentDetail.get("shipmentId")}
			    			</td>
	    					<td width="40%" align="left">
	    						  ${ShipmentDetail.get("supplierInvoiceId")?if_exists}
			    			</td>
			    			<td width="20%" align="center">
	    						  ${ShipmentDetail.get("supplierInvoiceDate")?if_exists}
			    			</td>
			    			<td  width="40%" align="left">
	    						${ShipmentDetail.get("deliveryChallanNumber")?if_exists}
			    			</td>
			    			<td width="20%" align="center">
	    						 ${ShipmentDetail.get("deliveryChallanDate")?if_exists}
			    			</td>
			    			<td  width="40%" align="left">
	    						 ${ShipmentDetail.get("lrNumber")?if_exists}
			    			</td>
			    			<td width="20%" align="center">
	    						 ${ShipmentDetail.get("estimatedReadyDate")?if_exists}
			    			</td>
			    			<td  width="40%" align="center">
	    						 ${ShipmentDetail.get("carrierName")?if_exists}
			    			</td>
			    			<td width="20%" align="right">
	    						 ${ShipmentDetail.get("estimatedShipCost")?if_exists}
			    			</td>		    			
	    				 </tr>
	    				</table>
	    			</#if>
	    			</td>
	    			</tr>        
	      </table>
	      <div id="sOFieldsDiv" >
	      </div> 
	</form>
	</div>
		</div>
	<br/>
    <form method="post" id="indententry" action="<@ofbizUrl>purchaseEntryInit</@ofbizUrl>">  
	<#-- passing BillToPartyId: -->
       	<#if billToPartyId?exists>
				<input type="hidden" name="partyId" id="partyId" value="${billToPartyId?if_exists}"/>
		 <#else> 
		 		<input type="hidden" name="partyId" id="partyId" value="${partyId?if_exists}"/>
		 </#if>
		<input type="hidden" name="shipmentId" id="billToPartyId" value="${parameters.shipmentId?if_exists}"/>
		<input type="hidden" name="vehicleId" id="vehicleId" value="${vehicleId?if_exists}"/>
		<input type="hidden" name="orderId" id="orderId" value="${orderId?if_exists}"/>
		<input type="hidden" name="isDisableAcctg" id="isDisableAcctg" value="N"/>
		<br>
	</form>
	</div>

<div class="full">
	<div class="screenlet">
	    <div class="screenlet-title-bar">
	 		<div class="grid-header" style="width:100%">
				<label>Purchase Items</label><span id="totalAmount"></span>
			</div>
			 <div class="screenlet-body" id="FieldsDIV" >
				<div id="myGrid1" style="width:100%;height:150px;">
					<div class="grid-header" style="width:100%">
					</div>
				</div>
				<#--
				<div class="lefthalf">
				<div class="screenlet-title-bar">
					<div class="grid-header" style="width:100%">
						<label>Additional Charges</label><span id="totalAmount"></span>
					</div>
					<div id="myGrid2" style="width:100%;height:200px;">
						<div class="grid-header" style="width:100%">
						</div>
					</div>
				</div>
				</div>
				<div class="righthalf" >
				<div class="screenlet-title-bar">
					<div class="grid-header" style="width:100%">
						<label>Discounts</label><span id="totalAmount"></span>
					</div>
					<div id="myGrid3" style="width:100%;height:200px;">
						<div class="grid-header" style="width:100%">
						</div>
					</div>
				</div>
				</div>
				-->
				<#assign formAction ='processDepotPurchaseInvoice'>	
				<#if partyId?exists>
			    	<div align="center">
			    		<h3>
			    		<input type="submit" style="padding:.4em" id="changeSave" value="Submit" onclick="javascript:processIndentEntry('indententry','<@ofbizUrl>${formAction}</@ofbizUrl>');"/>
			    		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			    		<input type="submit" style="padding:.4em" id="changeCancel" value="Cancel" onclick="javascript:processIndentEntry('indententry','<@ofbizUrl>FindGRNShipmentsDepot</@ofbizUrl>');"/>
			    		</h3>   	
			    	</div>     
				</#if>  	
			</div>
			</br>
		</div>
	</div>     
</div>
 	

</div>
 