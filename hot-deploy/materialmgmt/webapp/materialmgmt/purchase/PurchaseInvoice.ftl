<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	

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
		
	});
</script>
<#assign changeRowTitle = "Changes">                

<#include "PurchaseInvoiceInc.ftl"/>

<div class="full">
	
		<div class="screenlet-title-bar">
	         <div class="grid-header" style="width:100%">
				<label>Purchase Invoice Entry </label>
			</div>
	     </div>
	      
	    <div class="screenlet-body">
	    <form method="post" name="purchaseEntryInit" action="<@ofbizUrl>MaterialInvoiceInit</@ofbizUrl>" id="purchaseEntryInit">  
	      <table width="100%"  border="0" cellspacing="0" cellpadding="0">  
	        <tr>
	          <input type="hidden" name="isFormSubmitted"  value="YES" />
	          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Invoice Date :</div></td>
	          <#if effectiveDate?exists && effectiveDate?has_content>  
		  	  	<input type="hidden" name="effectiveDate" id="effectiveDate" value="${effectiveDate}"/>  
	          	<td valign='middle'>
	            	<div class='tabletext h2'>${effectiveDate}         
	            	</div>
	          	</td>       
	       	  <#else> 
	          	  	<td valign='middle'>          
	            		<input class='h2' type="text" name="effectiveDate" id="effectiveDate" value="${defaultEffectiveDate}"/>           		
	            	</td>
	       	  </#if>
	        </tr>
	        <tr><td><br/></td></tr>
	        <tr>
	          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Supplier:</div></td>
				<#if partyId?exists && partyId?has_content>  
		  	  		  <#--  <input type="hidden" name="partyId" id="partyId" value="${partyId?if_exists}"/>  -->
	          		<td valign='middle'>
	            		<div class='tabletext h2'>
	            			<#assign supplierName = delegator.findOne("PartyNameView", {"partyId" : partyId}, true) />
	               			${partyId?if_exists} [ ${supplierName.groupName?if_exists} ${supplierName.firstName?if_exists} ${supplierName.lastName?if_exists}]             
	            		</div>
	          		</td>       
	          	</#if>
	        </tr>
	         <#-- Showing BillToParty: -->
	         <tr><td><br/></td></tr> <tr>
	          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>BillToParty:</div></td>
				<#if billToPartyId?exists && billToPartyId?has_content>  
		  	  	  <#--	<input type="hidden" name="partyId" id="partyId" value="${billToPartyId?if_exists}"/>  -->
	          		<td valign='middle'>
	            		<div class='tabletext h2'>
	            			<#assign supplierName = delegator.findOne("PartyNameView", {"partyId" : billToPartyId}, true) />
	               			${billToPartyId?if_exists} [ ${supplierName.groupName?if_exists} ${supplierName.firstName?if_exists} ${supplierName.lastName?if_exists}]             
	            		</div>
	          		</td>       
	          	</#if>
	        </tr>
	         <tr><td><br/></td></tr>
	        <tr>
	          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Shipment Date:</div></td>
				<#if shipmentDate?exists && shipmentDate?has_content>  
		  	  		<input type="hidden" name="estimatedShipDate" id="estimatedShipDate" value="${shipmentDate?if_exists}"/>  
	          		<td valign='middle'>
	            		<div class='tabletext h2'>
	            			${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(shipmentDate, "dd MMMM, yyyy")?if_exists}
	            		</div>
	          		</td>       
	          	</#if>
	        </tr> 
	        <tr><td><br/></td></tr>
	       	<tr>
	            <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Vehicle No:</div></td>
				<#if vehicleId?exists && vehicleId?has_content>  
		  	  		<input type="hidden" name="vehicleId" id="vehicleId" value="${vehicleId?if_exists}"/>  
	          		<td valign='middle'>
	            		<div class='tabletext h2'>${vehicleId?if_exists}</div> 
	          		</td>       
	          	</#if>
	        </tr> 
	       <tr><td><br/></td></tr>
	       	<tr>
	            <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Purchase Order No:</div></td>
				<#if orderId?exists && orderId?has_content>  
		  	  		<input type="hidden" name="orderId" id="orderId" value="${orderId?if_exists}"/>  
	          		<td valign='middle'>
	            		<div class='tabletext h2'>${orderId?if_exists}</div> 
	          		</td>       
	          	</#if>
	        </tr>
	        <tr><td><br/></td></tr>
	        <#--<tr>  
	   			<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Add BED: </div></td>
		      	<td valign='middle' align='left'> 
		         	<input class='h3' type="checkbox" size="20" id="addBED" name="addBED" value="" onclick="javascript:addBedColumns();"/>
		         	<span class="tooltip"> Note:once BED columns added and input given to BED columns You cant remove them</span>
	    		</td>
	    	</tr> -->               
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
				<div id="myGrid1" style="width:100%;height:200px;">
					<div class="grid-header" style="width:100%">
					</div>
				</div>
				<div class="screenlet-title-bar">
					<div class="grid-header" style="width:27%">
						<label>Other Charges</label><span id="totalAmount"></span>
					</div>
					<div id="myGrid2" style="width:27%;height:150px;">
						<div class="grid-header" style="width:27%">
						</div>
					</div>
				</div>
				<#assign formAction ='processPurchaseInvoice'>	
				<#if partyId?exists>
			    	<div align="center">
			    		<h2>
			    		<input type="submit" style="padding:.4em" id="changeSave" value="Submit" onclick="javascript:processIndentEntry('indententry','<@ofbizUrl>${formAction}</@ofbizUrl>');"/>
			    		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			    		<input type="submit" style="padding:.4em" id="changeCancel" value="Cancel" onclick="javascript:processIndentEntry('indententry','<@ofbizUrl>FindGRNShipments</@ofbizUrl>');"/>
			    		</h2>   	
			    	</div>     
				</#if>  	
			</div>
			</br>
		</div>
	</div>     
</div>
 	

</div>
 