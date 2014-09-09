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
		$( "#chequeDate" ).datepicker({
			dateFormat:'d MM, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#effectiveDate" ).datepicker(selectedDate);
			}
		});
		$('#ui-datepicker-div').css('clip', 'auto');
		<#if changeFlag?exists && changeFlag=='PurchaseOrder'>
			$("#partyId").autocomplete({ source: partyAutoJson }).keydown(function(e){
    	<#else>
		 	$("#boothId").autocomplete({ source: boothAutoJson }).keydown(function(e){ 
		</#if>
			if (e.keyCode === 13){
		      	 $('#boothId').autocomplete('close');
	    			$('#purchaseEntryInit').submit();
	    			return false;   
			}
		});
		
		
	});
</script>
<#assign changeRowTitle = "Changes">                

<#include "PurchaseOrderInc.ftl"/>
			

<div class="full">
<div class="lefthalf">
<div class="screenlet">
	<div class="screenlet-title-bar">
         <div class="grid-header" style="width:100%">
			<label>Purchase Order Entry </label>
		</div>
		<!-- 
         <#if orderStatus?exists && orderStatus?has_content>
         	<li><a  target="_blank" href="<@ofbizUrl>invoiceDeliveryNote?facilityId=${orderRoute}&&estimatedShipDate=${defaultEffectiveDate}&&orderBooth=${orderFacility}&&shipmentTypeId=BYPRODUCTS</@ofbizUrl>" >Invoice[${orderFacility}][${orderRoute}]</a></li>
         </#if>
         <#if changeFlag?exists && changeFlag!='supplDeliverySchedule'>
 			 <li><a  target="_blank" href="<@ofbizUrl>checkListReport.pdf?userLoginId=${userLogin.get("userLoginId")}&&checkListType=indentEntry&&all=Y</@ofbizUrl>" >All Check List</a></li>
        	 <li><a  target="_blank" href="<@ofbizUrl>checkListReport.pdf?userLoginId=${userLogin.get("userLoginId")}&&checkListType=indentEntry</@ofbizUrl>">My Check List</a></li>
		</#if>
         </ui>         
         </h3> -->
         
     </div>
      
    <div class="screenlet-body">
    <form method="post" name="purchaseEntryInit" action="<@ofbizUrl>PurchaseOrderInit</@ofbizUrl>" id="purchaseEntryInit">  
	
      <table width="100%"  border="0" cellspacing="0" cellpadding="0">  
        <tr>
          <td>&nbsp;<input type="hidden" name="productSubscriptionTypeId"  value="CASH" />
          <input type="hidden" name="isFormSubmitted"  value="YES" />
            <input type="hidden" name="changeFlag"  value="${changeFlag?if_exists}" />
            <input type="hidden" name="productStoreId"  value="${productStoreId?if_exists}" />
            
            <#if changeFlag?exists && changeFlag=='IcpSalesAmul'>
              <input type="hidden" name="shipmentTypeId" id="shipmentTypeId" value="ICP_DIRECT_SHIPMENT"/> 
              <input type="hidden" name="salesChannel" id="salesChannel" value="ICP_AMUL_CHANNEL"/> 
            <#elseif changeFlag?exists && changeFlag=='IcpSales'>
             <input type="hidden" name="shipmentTypeId" id="shipmentTypeId" value="ICP_DIRECT_SHIPMENT"/> 
               <input type="hidden" name="salesChannel" id="salesChannel" value="ICP_NANDINI_CHANNEL"/> 
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
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Supplier Id:</div></td>
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
         <#-- 
           <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap" ><div class='h2'>Name :</div></td>
          <td>&nbsp;</td>
      	<#assign name = "">
       <#if (parameters.name?has_content)>  	 
          <td valign='middle'>
            <div class='tabletext h2'>
               	${parameters.name}
            </div>
          </td>       
       <#else>               
          <td valign='middle'>          
             <input  type="text" size="25" maxlength="25" readonly name="name" id="name" value=""  />  <em></em>          
          </td>
       </#if>
        </tr>     
          <tr>
	          <td>&nbsp;</td>
	          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>${uiLabelMap.Address1}:</div></td>
	          <td>&nbsp;</td>
	       <#if parameters.address1?exists>	  	  
	          <td valign='middle'>
	            <div class='tabletext h2'>
	               	${parameters.address1}            
	            </div>
	          </td>       
	       <#else>               
	          <td valign='middle'>          
	             <input class='h2' type="text" size="25" readonly maxlength="25" name="address1" id="address1" value=""  />   <em></em>            
	          </td>
	       </#if>
        </tr>
        <tr>
	          <td>&nbsp;</td>
	          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>${uiLabelMap.Address2}:</div></td>
	          <td>&nbsp;</td>
	       <#if parameters.address2?exists>	  	  
	          <td valign='middle'>
	            <div class='tabletext h2'>
	               	${parameters.address2}            
	            </div>
	          </td>       
	       <#else>               
	          <td valign='middle'>          
	             <input class='h2' type="text" size="25" readonly maxlength="25" name="address2" id="address2" value="" removeClass="required" />            
	          </td>
	       </#if>
        </tr>        
        <tr>
	          <td>&nbsp;</td>
	          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>${uiLabelMap.ContactNumber}:</div></td>
	          <td>&nbsp;</td>
	       <#if parameters.contactNumber?exists>	  	  
	          <td valign='middle'>
	            <div class='tabletext h2'>
	               	${parameters.contactNumber}            
	            </div>
	          </td>       
	       <#else>               
	          <td valign='middle'>          
	             <input class='h2' type="text" size="12" maxlength="12" name="contactNumber" id="contactNumber" value=""/> &nbsp;  &nbsp; &nbsp;${uiLabelMap.Pin}: &nbsp; <input class='h2' type="text" size="8" maxlength="8" name="pinNumber" id="pinNumber" value=""  /> <em></em> <span class="tooltip"> Press Enter to Save</span>          
	          </td>
	       </#if>
        </tr>   -->                              
      </table>
      <div id="sOFieldsDiv" >
      </div> 
</form>
<br/>
<form method="post" id="indententry" action="<@ofbizUrl>purchaseEntryInit</@ofbizUrl>">  
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
<#--
 	<div class="screenlet">
    <div class="screenlet-body">
 		<div class="grid-header" style="width:100%">
			<label>Payment Entry</label>
		</div>    
		<div>
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr><td><br/></td></tr>
		        <tr>
		        	<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Bank Name: </div></td>
          			<td>&nbsp;</td>
       				<td valign='middle'> 
           				<input class='h3' type="text" size="25" maxlength="25" name="bankName" id="bankName"/>          
       				</td>
 		       </tr>
 		       <tr><td><br/></td></tr>
 		       <tr>
		        	<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Cheque/Challan No: </div></td>
          			<td>&nbsp;</td>
		         	<td valign='middle'> 
             			<input class='h3' type="text" size="25" maxlength="25" name="chequeNo" id="chequeNo"/>          
          			</td>
 		       </tr>
 		       <tr><td><br/></td></tr>
 		       <tr>
		        	<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Cheque Date: </div></td>
          			<td>&nbsp;</td>
		         	<td valign='middle'> 
             			<input class='h3' type="text" size="25" maxlength="25" name="chequeDate" id="chequeDate"/>          
          			</td>
 		       </tr>
 		       <tr><td><br/></td></tr>
 		       <tr>
		        	<td align='left' valign='middle' nowrap="nowrap"><div  class='h3'>Amount: </div></td>
          			<td>&nbsp;</td>
		         	<td valign='middle'> 
             			<input class='h3' type="text" size="25" maxlength="25" name="amount" id="amount" />
             			<input class='h3' type="hidden" name="totAmt" id="totAmt" />         
          			</td>
 		       </tr>
 		       <tr><td><br/></td></tr>
        	</table>
		</div>		
    </div>
	</div>
 	-->		
 	<#--
<div class="screenlet">
    <div class="screenlet-body">
 		<div class="grid-header" style="width:100%">
			<label>Last Change <#if lastChangeSubProdMap?exists && lastChangeSubProdMap?has_content>[made by ${lastChangeSubProdMap.modifiedBy?if_exists} at ${lastChangeSubProdMap.modificationTime?if_exists}] &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Entries Made by ${parameters.userLogin.userLoginId} Today: ${entrySize?if_exists}</#if></label>
		</div>    
		<div id="myGrid2" style="width:100%;height:75px;"></div>		
    </div>
</div> -->     
</div>

<div class="righthalf">
<div class="screenlet">
    <div class="screenlet-body">
 		<div class="grid-header" style="width:100%">
			<label>Purchase Entry </label><span id="totalAmount"></span>
		</div>
		 <div class="screenlet-body" >
		<table width="50%" border="0" cellspacing="0" cellpadding="0">
				<tr><td><br/></td></tr>
		        <tr>
       				<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>MRN No.: </div></td>
		         	<td valign='middle' align='left'> 
             			<input class='h3' type="text" size="25" maxlength="25" name="mrnNumber" id="mrnNumber"/>          
          			</td>
          			<td>&nbsp;&nbsp;&nbsp;</td>
          			<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>PO No.: </div></td>
		         	<td valign='middle' align='left'> 
             			<input class='h3' type="text" size="25" maxlength="25" name="PONumber" id="PONumber"/>          
          			</td>
          			<td>&nbsp;&nbsp;&nbsp;</td>
          			<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>SUP Invoice No.: </div></td>
		         	<td valign='middle' align='left'> 
             			<input class='h3' type="text" size="25" maxlength="25" name="SInvNumber" id="SInvNumber"/>          
          			</td>
 		       </tr>
 		       <tr><td><br/></td></tr>
 		        <tr>
		        	<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Freight Charges: </div></td>
       				<td valign='middle' align='left'> 
           				<input class='h3' type="text" size="25" maxlength="25" name="freightCharges" id="freightCharges"/>          
       				</td>
       				<td>&nbsp;&nbsp;&nbsp;</td>
       				<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Discount: </div></td>
		         	<td valign='middle' align='left'> 
             			<input class='h3' type="text" size="25" maxlength="25" name="discount" id="discount"/>          
          			</td>
 		       </tr>
 		        <tr><td><br/></td></tr>
        	</table>
		</div>
			<div id="myGrid1" style="width:100%;height:350px;"></div>
			  
			<#assign formAction =''>			
		    <#if changeFlag?exists && changeFlag=='PurchaseOrder'>
		 		<#assign formAction='processPurchaseOrder'>
		 	<#else>
		 			<#assign formAction='processIcpSale'>		 	
			</#if>				
			
	<#if booth?exists || party?exists>
 		<#--	<div align="center"><span class="tooltip">** Check Payment Entry Before Submit **</span></div>		-->		
    	<div align="center">
    		<input type="submit" style="padding:.3em" id="changeSave" value="Submit" onclick="javascript:processIndentEntry('indententry','<@ofbizUrl>${formAction}</@ofbizUrl>');"/>
    		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    		<input type="submit" style="padding:.3em" id="changeCancel" value="Cancel" onclick="javascript:processIndentEntry('indententry','<@ofbizUrl>SupplDeliverySchedule</@ofbizUrl>');"/>   	
    	</div>     
	</#if>  
	</div>
</div>     
</div>
 	

</div>
 
