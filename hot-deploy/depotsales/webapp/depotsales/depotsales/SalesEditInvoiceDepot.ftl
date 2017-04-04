<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	

<script type="text/javascript">
	
$(document).ready(function(){
		

		$( "#effectiveDate" ).datepicker({
			dateFormat:'d MM, yy',
			changeMonth: true,
			numberOfMonths: 1,
			minDate: new Date(${milliseconds?if_exists}),
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

<style type="text/css">
.myTable { 
  width: 100%;
  text-align: left;
  background-color: lemonchiffon;
  border-collapse: collapse; 
  }
.myTable th { 
  background-color: green;
  color: white; 
  }
.myTable td, 
.myTable th { 
  padding: 10px;
  border: 1px solid goldenrod; 
  }
  
</style>

<style type="text/css">
input[type=button] {
	color: white;
    padding: .5x 7px;
    background:#008CBA;
    border: .8px solid green;
    border:0 none;
    cursor:pointer;
    -webkit-border-radius: 5px;
    border-radius: 5px; 
}
input[type=button]:hover {
    background-color: #3e8e41;
}

</style>

<#assign changeRowTitle = "Changes">                

<#include "SalesEditInvoiceDepotInc.ftl"/>
<#include "EditSaleInvoicePrice.ftl"/>

<div class="full">
	
		<div class="screenlet-title-bar">
	         <div class="grid-header" style="width:100%">
				<label>Sales Invoice Entry </label>
			</div>
	     </div>
	      
	    <div class="screenlet-body">
	    <form method="post" name="purchaseEntryInit" action="<@ofbizUrl>MaterialInvoiceInit</@ofbizUrl>" id="purchaseEntryInit">  
	      <table width="60%" border="0" cellspacing="0" cellpadding="0">
			    	<tr>
				        <td width="40%">
			    			<table  border="0" cellspacing="0" cellpadding="0" class="form-style-8">
				        		<tr>
	          						<input type="hidden" name="isFormSubmitted"  value="YES" />
							        <input type="hidden" id="invoiceId" name="invoiceId"  value="${invoiceId}" />
	         						<td align='left' valign='middle' nowrap="nowrap"><div class='h4'>Invoice Date :</div></td>
							        <#if effectiveDate?exists && effectiveDate?has_content>  
								  	 	<input type="hidden" name="effectiveDate" id="effectiveDate" value="${effectiveDate}"/>  
							          	<td valign='middle'>
							            	<div class='tabletext h4'><font color="green">${effectiveDate}         
							            	</div>
							          	</td>       
							       	<#else> 
							          	<td valign='middle'><font color="green">          
							           		<input class='h4' type="text" name="effectiveDate" id="effectiveDate" value="${invoDate}"/>           		
							           	</td>
							       	</#if>
							       	<td><br/></td>
							       	<td align='left' valign='middle' nowrap="nowrap"><div class='h4'>Shipment Date:</div></td>
									<#if shipmentDate?exists && shipmentDate?has_content>  
							  	  		<input type="hidden" name="shipmentDate" id="shipmentDate" value="${shipmentDate?if_exists}"/>  
						          		<td valign='middle'><font color="green">
	            							<div class='tabletext h4'>${shipmentDate?if_exists}</div> 
	              						</td>  
	              						<#else> 
	              						    <td></td>
	          						</#if>
	        				   </tr>
	        				   
	        				   
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
	        <td align='left' valign='middle' nowrap="nowrap"><div class='h4'>Branch:</div></td>
	        <#if branchPartyId?exists && branchPartyId?has_content>  
	      		<td valign='middle'>
	        		<div class='tabletext h3'>
	        			<#assign branchName = delegator.findOne("PartyNameView", {"partyId" : branchPartyId}, true) />
	           			${branchPartyId?if_exists} [ ${branchName.groupName?if_exists} ${branchName.firstName?if_exists} ${branchName.lastName?if_exists}]             
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
	       	  <#if tallyRefNo?exists && tallyRefNo?has_content>  
	       	  <td valign='middle'><font color="green">          
	            		<input class='h4' type="text" name="tallyrefNo" id="tallyrefNo" value="${tallyRefNo}"/>           		
	            	</td>
	            	<#else>
	            	<td valign='middle'><font color="green">          
	            		<input class='h4' type="text" name="tallyrefNo" id="tallyrefNo" />           		
	            	</td>
	            	</#if>
	       	  
	        </tr>
	       <tr>
	            <td align='left' valign='middle' nowrap="nowrap"><div class='h4'>invoiceId:</div></td>
				<#if invoiceId?exists && invoiceId?has_content>  
		  	  		<input type="hidden" name="invoiceId" id="invoiceId" value="${invoiceId?if_exists}"/>  
	          		<td valign='middle'><font color="green">
	            		<div class='tabletext h4'>${invoiceId?if_exists}</div> 
	          		</td>       
	          	</#if>
	        </tr>  
	       	
	        <tr><td><br/></td></tr>
	       	<tr>
	            <td align='left' valign='middle' nowrap="nowrap"><div class='h4'>Sales Order No:</div></td>
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
	        	
		       	  		<td align='left' nowrap="nowrap"><div class='h4'>Sale Tax Type:</div></td>
		       	  		<td valign='middle'>
	          				<select name="saleTaxType" id="saleTaxType" class='h4' style="width:120px">
	          					<#if saleTaxType?exists && saleTaxType?has_content>
	          						<#if saleTaxType == "Intra-State">
	          							<option value="Intra-State" selected>With In State</option>
	          						<#else>
	          							<option value="Inter-State" selected>Inter State</option>
	          						</#if> 
	          					</#if> 
	          					<option value="Intra-State">With In State</option>
	          					<option value="Inter-State">Inter State</option>
	          				</select>
	          				<#if customerGeoId?exists && customerGeoId?has_content>
					    		<input type="hidden" name="customerGeoId" id="customerGeoId" size="18" maxlength="60" value="${customerGeoId}" readonly/>
					    	</#if>
					      	<#if branchGeoId?exists && branchGeoId?has_content>
					    		<input type="hidden" name="branchGeoId" id="branchGeoId" size="18" maxlength="60" value="${branchGeoId}" readonly/>
					    	</#if>
	          			</td>
		          		<td>&nbsp;</td>
		          		<td align='left' nowrap="nowrap"><div class='h4'>Tax Form:</div></td>
		       			<td valign='middle'>
	          				<select name="saleTitleTransferEnumId" id="saleTitleTransferEnumId" class='h4' style="width:205px">
	          					<#if saleTitleTransferEnumId?exists && saleTitleTransferEnumId?has_content>
	          						<#if saleTitleTransferEnumId == "CST_CFORM">
	          							<option value="CST_CFORM" selected>Transaction With C Form</option>
	          						</#if>
	          						<#if saleTitleTransferEnumId == "CST_NOCFORM">
	          							<option value="CST_NOCFORM" selected>Transaction Without C Form</option>
	          						</#if>
	          						<#if saleTitleTransferEnumId == "NO_E2_FORM">
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
		       	
		       </table>	
	    	<div id="sOFieldsDiv" >
	      </div> 
	</form>
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
		//<input type="hidden" name="shipmentDate" id="shipmentDate" value="${shipmentDate?if_exists}"/>
		<br>
	</form>
	</div>

<div class="full">
	<div class="screenlet">
	    <div class="screenlet-title-bar">
	 		<div class="grid-header" style="width:100%">
				<label>Sales Items</label><span id="totalAmount"></span>
			</div>
			 <div class="screenlet-body" id="FieldsDIV" >
				<div id="myGrid1" style="width:100%;height:150px;">
					<div class="grid-header" style="width:100%">
					</div>
				</div>
			  <#--	<div class="lefthalf">
				<div class="screenlet-title-bar">
					<div class="grid-header" style="width:100%">
						<label>Additional Charges</label><span id="totalAmount"></span>
					</div>
					<div id="myGrid2" style="width:100%;height:180px;">
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
					<div id="myGrid3" style="width:100%;height:180px;">
						<div class="grid-header" style="width:100%">
						</div>
					</div>-->
				</div>
				</div>
				<#assign formAction ='processEditSalesInvoice'>	
				<#if invoiceId?exists>
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
 