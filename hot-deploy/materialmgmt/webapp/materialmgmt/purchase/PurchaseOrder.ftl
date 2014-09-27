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
		$( "#SInvoiceDate" ).datepicker({
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

<#include "PurchaseOrderInc.ftl"/>
			


<div class="full">
<div class="screenlet">
	<div class="screenlet-title-bar">
         <div class="grid-header" style="width:100%">
			<label>Purchase Order Entry </label>
		</div>
     </div>
      
    <div class="screenlet-body">
     <#if changeFlag?exists && changeFlag=='PurchaseOrder'>
    <form method="post" name="purchaseEntryInit" action="<@ofbizUrl>PurchaseOrderInit</@ofbizUrl>" id="purchaseEntryInit">  
	<#elseif changeFlag?exists && changeFlag=='InterUnitPurchase'>
	<form method="post" name="purchaseEntryInit" action="<@ofbizUrl>InterUnitPurchaseOrderInit</@ofbizUrl>" id="purchaseEntryInit">
	</#if>
      <table width="100%"  border="0" cellspacing="0" cellpadding="0">  
        <tr>
          <td>&nbsp;<input type="hidden" name="productSubscriptionTypeId"  value="CASH" />
          <input type="hidden" name="isFormSubmitted"  value="YES" />
            <input type="hidden" name="changeFlag"  value="${changeFlag?if_exists}" />
            <input type="hidden" name="productStoreId"  value="${productStoreId?if_exists}" />
          
            <#if changeFlag?exists && changeFlag=='InterUnitPurchase'>
               <input type="hidden" name="salesChannel" id="salesChannel" value="INTER_PRCHSE_CHANNEL"/> 
            <#else>
               <input type="hidden" name="salesChannel" id="salesChannel" value="PROD_PRCHSE_CHANNEL"/>  
            </#if>
           </td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>${uiLabelMap.SupplyDate}:</div></td>
          <td>&nbsp;</td>
          <#if effectiveDate?exists && effectiveDate?has_content>  
	  	  	<input type="hidden" name="effectiveDate" id="effectiveDate" value="${effectiveDate}"/>  
          	<td valign='middle'>
            	<div class='tabletext h2'>${effectiveDate}         
            	</div>
          	</td>       
       	  <#else> 
          	  	<td valign='middle'>          
            		<input class='h2' type="text" name="effectiveDate" id="effectiveDate" value="${defaultEffectiveDate}"/>           		</td>
       	</#if>
        </tr>
        <tr><td><br/></td></tr>
        <tr>
          <td>&nbsp;</td>
            <td align='left' valign='middle' nowrap="nowrap"><div class='h2'><#if changeFlag?exists && changeFlag=='InterUnitPurchase'>KMF Unit ID:<#else>Supplier Id:</#if></div></td>
          <td>&nbsp;</td>
			<#if party?exists && party?has_content>  
	  	  		<input type="hidden" name="partyId" id="partyId" value="${party.partyId.toUpperCase()}"/>  
          		<td valign='middle'>
            		<div class='tabletext h2'>
               			${party.partyId.toUpperCase()} [ ${party.groupName?if_exists} ] ${partyAddress?if_exists} <#--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="javascript:processChangeIndentParty()" class="buttontext">Party Change</a>-->             
            		</div>
          		</td>       
       		<#else>               
          		<td valign='middle'>
          			<input type="text" name="partyId" id="partyId" />
          			 <span class="tooltip">Input party code and press Enter</span>
          		</td>
          	</#if>
        </tr> 
       <tr><td><br/></td></tr>    
                       
      </table>
      <div id="sOFieldsDiv" >
      </div> 
</form>
<br/>
	 <#if changeFlag?exists && changeFlag=='PurchaseOrder'>
	    <form method="post" id="indententry" action="<@ofbizUrl>purchaseEntryInit</@ofbizUrl>">  
	<#elseif changeFlag?exists && changeFlag=='InterUnitPurchase'>
	<form method="post" id="indententry" action="<@ofbizUrl>InterUnitPurchaseOrderInit</@ofbizUrl>">  
	</#if>
	<input type="hidden" name="effectiveDate" id="effectiveDate" value="${parameters.effectiveDate?if_exists}"/>
	<input type="hidden" name="boothId" id="boothId" value="${parameters.boothId?if_exists}"/>
	<input type="hidden" name="partyId" id="partyId" value="${parameters.partyId?if_exists}"/>
	<input type="hidden" name="productSubscriptionTypeId" id="productSubscriptionTypeId" value="${parameters.productSubscriptionTypeId?if_exists}"/>   	   	   	   
	<input type="hidden" name="subscriptionTypeId" id="subscriptionTypeId" value="${parameters.subscriptionTypeId?if_exists}"/>
	<input type="hidden" name="destinationFacilityId" id="destinationFacilityId" value="${parameters.destinationFacilityId?if_exists}"/>
	<input type="hidden" name="shipmentTypeId" id="shipmentTypeId" value="${parameters.shipmentTypeId?if_exists}"/>
	<input type="hidden" name="vehicleId" id="vehicleId" value="${parameters.vehicleId?if_exists}"/>
	<input type="hidden" name="salesChannel" id="salesChannel" value="${parameters.salesChannel?if_exists}"/>
	<input type="hidden" name="productStoreId" id="productStoreId" value="${parameters.productStoreId?if_exists}"/>
	<br>
</form>
</div>
</div>

<div class="full">
<div class="screenlet">
    <div class="screenlet-body">
 		<div class="grid-header" style="width:100%">
			<label>Purchase Entry </label><span id="totalAmount"></span>
		</div>
		 <div class="screenlet-body" id="FieldsDIV" >
		<table width="50%" border="0" cellspacing="0" cellpadding="0">
				<tr><td><br/></td></tr>
		        <tr>
       				<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>MRN No.: </div></td>
		         	<td valign='middle' align='left'> 
             			<input class='h3' type="text" size="20" maxlength="30" name="mrnNumber" id="mrnNumber"/>          
          			</td>
          			<td>&nbsp;&nbsp;&nbsp;</td>
          			<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>PO No.: </div></td>
		         	<td valign='middle' align='left'> 
             			<input class='h3' type="text" size="20" maxlength="30" name="PONumber" id="PONumber"/>          
          			</td>
          			<td>&nbsp;&nbsp;&nbsp;</td>
          			<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>SUP Invoice No.: </div></td>
		         	<td valign='middle' align='left'> 
             			<input class='h3' type="text" size="20" maxlength="30" name="SInvNumber" id="SInvNumber"/>          
          			</td>
          			<td>&nbsp;&nbsp;&nbsp;</td>
          			<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>SUP Invoice Date: </div></td>
		         	<td valign='middle' align='left'> 
             			<input class='h3' type="text" size="20" maxlength="30" name="SInvoiceDate" id="SInvoiceDate"/>          
          			</td>
 		       </tr>
 		       <tr><td><br/></td></tr>
 		        <tr>
		        	<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Freight Charges: </div></td>
       				<td valign='middle' align='left'> 
           				<input class='h3' type="text" size="20" maxlength="30" name="freightCharges" id="freightCharges" onblur="javascript:addToInvoiceAmount();"/>          
       				</td>
       				<td>&nbsp;&nbsp;&nbsp;</td>
       				<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Discount: </div></td>
		         	<td valign='middle' align='left'> 
             			<input class='h3' type="text" size="20" maxlength="30" name="discount" id="discount" onblur="javascript:addToInvoiceAmount();"  />          
          			</td>
          			<td>&nbsp;&nbsp;&nbsp;</td>
       				<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Insurance: </div></td>
		         	<td valign='middle' align='left'> 
             			<input class='h3' type="text" size="20" maxlength="30" name="insurence" id="insurence" onblur="javascript:addToInvoiceAmount();"/>          
          			</td>
          			<td>&nbsp;&nbsp;&nbsp;</td>
          			<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Add BED: </div></td>
		         	<td valign='middle' align='left'> 
		         	<input class='h3' type="checkbox" size="20" id="addBED" name="addBED" value="" onclick="javascript:addBedColumns();"/>
             			      
          			</td>
 		         </tr>
 		          <tr><td><br/></td></tr>
 		          <tr><td colspan="6"> <span class="tooltip"> Note:once BED columns added and input given to BED columns You cant remove them</span></tr>
 		      
        	</table>
		</div>
			<div id="myGrid1" style="width:100%;height:350px;"></div>
			  
			<#assign formAction =''>			
		    <#if changeFlag?exists && changeFlag=='PurchaseOrder'>
		 		<#assign formAction='processPurchaseOrder'>
		 	<#elseif changeFlag?exists && changeFlag=='InterUnitPurchase'>
		         <#assign formAction='processInterUnitPurchaseOrder'>
		 	<#else>
		 			<#assign formAction='processPurchaseOrder'> 	
			</#if>				
			
	<#if booth?exists || party?exists>
 		<#--	<div align="center"><span class="tooltip">** Check Payment Entry Before Submit **</span></div>		-->		
    	<div align="center">
    		<input type="submit" style="padding:.3em" id="changeSave" value="Submit" onclick="javascript:processIndentEntry('indententry','<@ofbizUrl>${formAction}</@ofbizUrl>');"/>
    		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    		<input type="submit" style="padding:.3em" id="changeCancel" value="Cancel" onclick="javascript:processIndentEntry('indententry','<@ofbizUrl>${formAction}</@ofbizUrl>');"/>   	
    	</div>     
	</#if>  
	</div>
</div>     
</div>
 	

</div>
 
