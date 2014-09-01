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
		<#if changeFlag?exists && changeFlag=='AdhocSaleNew'>
			$("#boothId").autocomplete({ source: boothAutoJson }).keydown(function(e){
    	<#else>
		 	$("#partyId").autocomplete({ source: partyAutoJson }).keydown(function(e){ 
		</#if>
			if (e.keyCode === 13){
		      	 $('#boothId').autocomplete('close');
	    			$('#indententryinit').submit();
	    			return false;   
			}
		});
		
		
	});
</script>
<#assign changeRowTitle = "Changes">                

<#include "indentAdhocInc.ftl"/>
<#include "EditUDPPrice.ftl"/>
<#--
<#assign initAction =''>	
	<#if changeFlag?exists && changeFlag=='supplDeliverySchedule'>
 		<#assign initAction='SupplDeliveryScheduleNew'>
 	<#elseif changeFlag?exists && changeFlag=='ByProdGatePass'>
 		<#assign initAction='byProdGatePassNew'>	
 	<#else>
 		<#assign initAction='IndentEntryNew'>	 	
	</#if>	
-->
					

<div class="full">
<div class="lefthalf">
<div class="screenlet">
	<div class="screenlet-title-bar">
         <div class="grid-header" style="width:100%">
			<label>Indent Entry </label>
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
    <#if changeFlag?exists && changeFlag=='IcpSalesAmul'>
     	<form method="post" name="indententryinit" action="<@ofbizUrl>IcpSalesAmul</@ofbizUrl>" id="indententryinit">  
    <#elseif changeFlag?exists && changeFlag=='IcpSales'>
    	<form method="post" name="indententryinit" action="<@ofbizUrl>IcpSales</@ofbizUrl>" id="indententryinit">
    	<#elseif changeFlag?exists && changeFlag=='IcpSalesBellary'>
    	<form method="post" name="indententryinit" action="<@ofbizUrl>IcpSalesBellary</@ofbizUrl>" id="indententryinit">
    <#elseif changeFlag?exists && changeFlag=='PowderSales'>
    	<form method="post" name="indententryinit" action="<@ofbizUrl>PowderPlantSale</@ofbizUrl>" id="indententryinit">  
    <#elseif changeFlag?exists && changeFlag=='FgsSales'>
    	<form method="post" name="indententryinit" action="<@ofbizUrl>FGSProductSale</@ofbizUrl>" id="indententryinit">  
    <#elseif changeFlag?exists && changeFlag=='InterUnitTransferSale'>
    	<form method="post" name="indententryinit" action="<@ofbizUrl>InterUnitStkTr</@ofbizUrl>" id="indententryinit">
    <#elseif changeFlag?exists && changeFlag=='ConvCharges'>
    	<form method="post" name="indententryinit" action="<@ofbizUrl>ConversionChargesEntry</@ofbizUrl>" id="indententryinit">	  
    <#else>
    	<form method="post" name="indententryinit" action="<@ofbizUrl>AdhocSaleNew</@ofbizUrl>" id="indententryinit">  
    </#if>
	
      <table width="100%" border="0" cellspacing="0" cellpadding="0">  
       <#if  (changeFlag?exists && changeFlag =='AdhocSaleNew')>   
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Products Type:</div></td>
          <td>&nbsp;</td>
       <#if productCatageoryId?exists && booth?exists>
	  	  <input type="hidden" name="productCatageoryId" id="productCatageoryId" value="${parameters.productCatageoryId}"/>   	   	   	   
          <td valign='middle'>
            <div class='tabletext h2'>
               	${productCatageoryId}             
            </div>
          </td>       
       <#else>
          <td valign='middle'>
          	<#assign isDefault = false> 
      		<select name="productCatageoryId" class='h2'>
      		<option  value="" >Milk&Products</option>      			     			
                <#list productCategoryIds as prodCategory>
                	<#if !productCatageoryId?exists>    
	                  	<#assign isDefault = false>                
	                    <#if prodCategory == "INDENT">
	                      <#assign isDefault = true>
	                    </#if> 
                    </#if>
                    <#if productCatageoryId?exists && (productCatageoryId == prodCategory)>
      					<option  value="${productCatageoryId}" selected="selected">${prodCategory}</option>
      					<#else>
      						<option value='${prodCategory}'<#if isDefault> selected="selected"</#if>>
                    			${prodCategory}
                  		</option>
      				</#if>
      			</#list>            
			</select>
          </td>
       </#if>  
       </tr>    
       </#if>
        <tr>
        	<td>&nbsp;<input type="hidden" name="productSubscriptionTypeId"  value="CASH" />
		      	<input type="hidden" name="isFormSubmitted"  value="YES" />
		      	<input type="hidden" name="changeFlag"  value="${changeFlag?if_exists}" />
		        <#if changeFlag?exists && changeFlag=='IcpSalesAmul'>
		        	<input type="hidden" name="shipmentTypeId" id="shipmentTypeId" value="ICP_AMUL_SHIPMENT"/> 
		          	<input type="hidden" name="salesChannel" id="salesChannel" value="ICP_AMUL_CHANNEL"/> 
		        <#elseif changeFlag?exists && changeFlag=='IcpSales'>
		         	<input type="hidden" name="shipmentTypeId" id="shipmentTypeId" value="ICP_NANDINI_SHIPMENT"/> 
		           	<input type="hidden" name="salesChannel" id="salesChannel" value="ICP_NANDINI_CHANNEL"/>
		        <#elseif changeFlag?exists && changeFlag=='IcpSalesBellary'>
		         	<input type="hidden" name="shipmentTypeId" id="shipmentTypeId" value="ICP_BELLARY_SHIPMENT"/> 
		           	<input type="hidden" name="salesChannel" id="salesChannel" value="ICP_BELLARY_CHANNEL"/>    	 
		        <#elseif changeFlag?exists && changeFlag=='PowderSales'>
		         	<input type="hidden" name="shipmentTypeId" id="shipmentTypeId" value="POWDER_SHIPMENT"/> 
		           	<input type="hidden" name="salesChannel" id="salesChannel" value="POWDER_PLANT_CHANNEL"/>
		        <#elseif changeFlag?exists && changeFlag=='FgsSales'>
		         	<input type="hidden" name="shipmentTypeId" id="shipmentTypeId" value="FGS_SHIPMENT"/> 
		           	<input type="hidden" name="salesChannel" id="salesChannel" value="FGS_PRODUCT_CHANNEL"/> 
		        <#elseif changeFlag?exists && changeFlag=='InterUnitTransferSale'>
		         	<input type="hidden" name="shipmentTypeId" id="shipmentTypeId" value="INTUNIT_TR_SHIPMENT"/> 
		           	<input type="hidden" name="salesChannel" id="salesChannel" value="INTUNIT_TR_CHANNEL"/>
		        <#elseif changeFlag?exists && changeFlag=='ConvCharges'>
		         	<input type="hidden" name="shipmentTypeId" id="shipmentTypeId" value="PROCESSING_SHIPMENT"/> 
		           	<input type="hidden" name="salesChannel" id="salesChannel" value="PROCESSING_CHANNEL"/> 
		        <#else>
		          	<input type="hidden" name="shipmentTypeId" id="shipmentTypeId" value="RM_DIRECT_SHIPMENT"/>
		          	<input type="hidden" name="salesChannel" id="salesChannel" value="RM_DIRECT_CHANNEL"/>
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
            	<input class='h2' type="text" name="effectiveDate" id="effectiveDate" value="${defaultEffectiveDate}"/>           		
            </td>
       	  </#if>
        </tr>
        <tr><td><br/></td></tr>
        <#if changeFlag?exists && changeFlag != "AdhocSaleNew">
	      	<tr>
	      		<td>&nbsp;</td>
	      		<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>PO Number:</div></td>
	      		<td>&nbsp;</td>
	   			<#if PONumber?exists && PONumber?has_content>  
	  	  			<input type="hidden" name="PONumber" id="PONumber" value="${PONumber?if_exists}"/>  
	      			<td valign='middle'>
	        			<div class='tabletext h2'>${PONumber?if_exists}</div>
	      			</td>       	
	   			<#else>      	         
	      			<td valign='middle'>
	      				<input type="text" name="PONumber" id="PONumber" />    
	         			<span class="tooltip">Fill if PO Number exists</span>       
	      			</td>
	   			</#if>
	    	</tr>
	    	<tr><td><br/></td></tr>
    	</#if>
        
        <#if changeFlag?exists && changeFlag == "AdhocSaleNew">
          	<tr>
          		<td>&nbsp;</td>
          		<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Vehicle Number:</div></td>
          		<td>&nbsp;</td>
       			<#if vehicleId?exists && vehicleId?has_content>  
	  	  			<input type="hidden" name="?if_exists" id="?if_exists" value="${vehicleId?if_exists}"/>  
          			<td valign='middle'>
            			<div class='tabletext h2'>${vehicleId?if_exists}</div>
          			</td>       	
       			<#else>      	         
          			<td valign='middle'>
          				<input type="text" name="vehicleId" id="vehicleId" />    
             			<span class="tooltip">Fill if Vehicle Number exists</span>       
          			</td>
       			</#if>
        	</tr>
        <#else>
        	<#if changeFlag?exists && changeFlag !='ConvCharges' && changeFlag !='InterUnitTransferSale'>
        		<tr>
	          		<td>&nbsp;</td>
	          		<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Order Tax Type:</div></td>
	          		<td>&nbsp;</td>
	       			<#if orderTaxType?exists && orderTaxType?has_content>  
		  	  			<input type="hidden" name="orderTaxType" id="orderTaxType" value="${orderTaxType?if_exists}"/>  
	          			<td valign='middle'>
	            			<div class='tabletext h2'>${orderTaxType?if_exists}</div>
	          			</td>       	
	       			<#else>      	         
	          			<td valign='middle'>
	          				<select name="orderTaxType" id="orderTaxType" class='h2'>
	          					<option value="INTRA">With in State</option>
	          					<option value="INTER">Out of State</option>
	          				</select>
	          			</td>
	       			</#if>
        		</tr>
        	</#if>
        	
		</#if>
          
        <tr><td><br/></td></tr>
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'><#if changeFlag?exists && changeFlag=='AdhocSaleNew'>Retailer Id:<#elseif changeFlag?exists && changeFlag=='InterUnitTransferSale'>KMF Unit ID:<#else>Wholesaler Id:</#if></div></td>
          <td>&nbsp;</td>
        <#if changeFlag?exists && changeFlag=='AdhocSaleNew'>
			<#if booth?exists && booth?has_content>  
	  	  		<input type="hidden" name="boothId" id="boothId" value="${booth.facilityId.toUpperCase()}"/>  
          		<td valign='middle'>
            		<div class='tabletext h2'>
               			${booth.facilityId.toUpperCase()} [ ${booth.facilityName?if_exists} ] ${partyAddress?if_exists} <#--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="javascript:processChangeIndentParty()" class="buttontext">Party Change</a>-->             
            		</div>
          		</td>       
       		<#else>               
          		<td valign='middle'>
          			<input type="text" name="boothId" id="boothId" />
          			 <span class="tooltip">Input party code and press Enter</span>
          		</td>
          	</#if>
    	<#else>
    		<input type="hidden" name="productStoreId" id="productStoreId" value="${productStoreId?if_exists}"/>
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
<form method="post" id="indententry" action="<@ofbizUrl>IndentEntryInit</@ofbizUrl>">  
	<input type="hidden" name="effectiveDate" id="effectiveDate" value="${parameters.effectiveDate?if_exists}"/>
	<input type="hidden" name="boothId" id="boothId" value="${parameters.boothId?if_exists}"/>
	<input type="hidden" name="productSubscriptionTypeId" id="productSubscriptionTypeId" value="${parameters.productSubscriptionTypeId?if_exists}"/>   	   	   	   
	<input type="hidden" name="subscriptionTypeId" id="subscriptionTypeId" value="${parameters.subscriptionTypeId?if_exists}"/>
	<input type="hidden" name="destinationFacilityId" id="destinationFacilityId" value="${parameters.destinationFacilityId?if_exists}"/>
	<input type="hidden" name="shipmentTypeId" id="shipmentTypeId" value="${parameters.shipmentTypeId?if_exists}"/>
	<input type="hidden" name="vehicleId" id="vehicleId" value="${parameters.vehicleId?if_exists}"/>
	<input type="hidden" name="salesChannel" id="salesChannel" value="${parameters.salesChannel?if_exists}"/>
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

<div class="righthalf">
<div class="screenlet">
    <div class="screenlet-body">
 		<div class="grid-header" style="width:100%">
			<label>Indent Entry </label><span id="totalAmount"></span>
		</div>
			<div id="myGrid1" style="width:100%;height:350px;"></div>
			  
			<#assign formAction =''>			
		    <#if changeFlag?exists && changeFlag=='AdhocSaleNew'>
		 		<#assign formAction='processAdhocSale'>
		 	<#elseif changeFlag?exists && changeFlag=='IcpSales'>
		         <#assign formAction='processIcpSale'>
		    <#elseif changeFlag?exists && changeFlag=='IcpSalesAmul'>
		         <#assign formAction='processIcpAmulSale'>
		    <#elseif changeFlag?exists && changeFlag=='IcpSalesBellary'>
		         <#assign formAction='processIcpBellarySale'>     
		    <#elseif changeFlag?exists && changeFlag=='PowderSales'>
		         <#assign formAction='processPowderSale'>
		    <#elseif changeFlag?exists && changeFlag=='FgsSales'>
		         <#assign formAction='processFGSProductSale'>     
		 	<#elseif changeFlag?exists && changeFlag=='InterUnitTransferSale'>
		         <#assign formAction='processInterUnitStkTrSale'> 
		    <#elseif changeFlag?exists && changeFlag=='ConvCharges'>
		         <#assign formAction='processConvChargesSale'>     
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
 
