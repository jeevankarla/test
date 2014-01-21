<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
<script type="application/javascript">

$(document).ready(function(){
		$( "#effectiveDate" ).datepicker({
			dateFormat:'d MM, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#effectiveDate" ).datepicker(selectedDate);
			}
		});
		$('#ui-datepicker-div').css('clip', 'auto');
			
});
</script>
<#assign changeRowTitle = "Changes">                

<#include "DSCorrectionEntryInit.ftl"/>
<#include "IndentRouteChange.ftl"/>
<div class="full">
<div class="lefthalf">
<div class="screenlet">
	<div class="screenlet-title-bar">
        <h3>Delivery Schedule Correction</h3>
     </div>
    <div class="screenlet-body">
<form method="post" name="indententryinit" action="<@ofbizUrl>DSCorrectionEntryNew</@ofbizUrl>" id="indententryinit">  
        
      <table width="100%" border="0" cellspacing="0" cellpadding="0">   
       <tr>
          <td>&nbsp;</td>
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
        <#-- <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>${uiLabelMap.SupplyDate}:</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext h2'>            
            	${defaultEffectiveDate}
            </div>
          </td>
        </tr>-->
         <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>AM/PM:</div></td>
          <td>&nbsp;</td>
       	<#if shipmentTypeId?exists>
	  	  <input type="hidden" name="shipmentTypeId" id="shipmentTypeId" value="${parameters.shipmentTypeId}"/>   	   	   	   
          <td valign='middle'>
            <div class='tabletext h2'>
               	${shipmentTypeId}             
            </div>
          </td>       
       <#else>
          <td valign='middle'>
          	<#assign isDefaultShipment = false>  
      		<select name="shipmentTypeId" class='h2'>
                <#list shipmentTypes as shipmentType>
                	<#if !shipmentTypeId?exists>    
	                  	<#assign isDefaultShipment = false>                
                    	<#if shipmentType.shipmentTypeId = "AM_SHIPMENT">
                      		<#assign isDefaultShipment = true>
                    	</#if> 
                    </#if>    
                  	   <#if shipmentTypeId?exists && (shipmentTypeId = shipmentType.shipmentTypeId)>
      						<option  value="${shipmentTypeId}" selected="selected">${shipmentType.description}</option>
      					<#else>
      						<option value='${shipmentType.shipmentTypeId}'<#if isDefaultShipment> selected="selected"</#if>>
                    			${shipmentType.description}
                  		</option>
      				</#if> 
      			</#list>            
			</select>
          </td>
       </#if>   
        </tr> 
	    
   
        <tr><td><br/></td></tr>             
       <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Supply Type:</div></td>
          <td>&nbsp;</td>
       <#if productSubscriptionTypeId?exists && booth?exists>
	  	  <input type="hidden" name="productSubscriptionTypeId" id="productSubscriptionTypeId" value="${parameters.productSubscriptionTypeId}"/>   	   	   	   
          <td valign='middle'>
            <div class='tabletext h2'>
               	${productSubscriptionTypeId}             
            </div>
          </td>       
       <#else>
          <td valign='middle'> 
      		<select name="productSubscriptionTypeId" class='h2'>
                <#list prodSubTypes as prodSubType>    
                  	<#assign isDefault = false>                
                    <#if prodSubType.enumId = "CASH">
                      <#assign isDefault = true>
                    </#if>
                   	<option value='${prodSubType.enumId}'<#if isDefault> selected="selected"</#if>>
	                    ${prodSubType.description}
	                 </option>                  	
      			</#list>            
			</select>
          </td>
       </#if>   
        </tr>       
        <tr><td><br/></td></tr>    
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>${uiLabelMap.Route}:</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
        <#if booth?exists>
        	<input type="hidden" class='h2' name="routeId" id="routeId" value="${parameters.routeId?if_exists}">
            <div class='tabletext h2'>
               	${parameters.routeId}             
            </div>
        <#else>
             <input class='h2' type="text" size="10" maxlength="10" name="routeId" id="routeId"/><em>*</em>          
             <span class="tooltip">required</span>      
        </#if> 
         </td>
        </tr> 
        <tr><td><br/></td></tr>           
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Party Code:</div></td>
          <td>&nbsp;</td>
       <#if booth?exists && booth?has_content>  
	  	  <input type="hidden" name="boothId" id="boothId" value="${booth.facilityId.toUpperCase()}"/>  
          <td valign='middle'>
            <div class='tabletext h2'>
               	${booth.facilityId.toUpperCase()} [ ${booth.facilityName?if_exists} ]<a href="javascript:processPartyChangeEntry()" class="buttontext"> Party Change</a>             
            </div>
          </td>       
       <#else>               
          <td valign='middle'>          
             <input class='h2' type="text" size="10" maxlength="6" name="boothId" id="boothId" value=""/>          
             <span class="tooltip">Input Party Code and press Enter</span>
          </td>
       </#if>
        </tr>        
      </table>
</form>
<br/>
<form method="post" id="indententry" action="<@ofbizUrl>ProcessDeliveryScheduleCorrection</@ofbizUrl>">  
	<#if effectiveDate?exists && effectiveDate?has_content>
		<input type="hidden" name="effectiveDate" id="effectiveDate" value="${parameters.effectiveDate}"/>
	<#else>
		<input type="hidden" name="effectiveDate" id="effectiveDate" value="${defaultEffectiveDate}"/>
	</#if>
	<input type="hidden" name="shipmentTypeId" id="shipmentTypeId" value="${parameters.shipmentTypeId?if_exists}"/>  
	<input type="hidden" name="boothId" id="boothId" value="${parameters.boothId?if_exists}"/>  
	<input type="hidden" name="productSubscriptionTypeId" id="productSubscriptionTypeId" value="${parameters.productSubscriptionTypeId?if_exists}"/>   	   	   	   
	<input type="hidden" name="subscriptionTypeId" id="subscriptionTypeId" value="${parameters.subscriptionTypeId?if_exists}"/>
 			
			<br>

</form>
    </div>
</div> 
<div class="screenlet">
    <div class="screenlet-body">
 		<div class="grid-header" style="width:100%">
			<label>Last Change <#if lastChangeSubProdMap?exists && lastChangeSubProdMap?has_content>[made by ${lastChangeSubProdMap.modifiedBy?if_exists} at ${lastChangeSubProdMap.modificationTime?if_exists}] </#if></label>
		</div>    
		<div id="myGrid2" style="width:100%;height:75px;"></div>		
    </div>
</div> 
</div>
<div class="righthalf">
<div class="screenlet">
    <div class="screenlet-body">
 		<div class="grid-header" style="width:100%">
				<label>Order Correction</label>
		</div>
		<div id="myGrid1" style="width:100%;height:350px;"></div>		
    <#if booth?exists>					
    <div align="center">
    	<input type="submit" style="padding:.3em" id="changeSave" value="Submit" onclick="javascript:processIndentEntry('indententry','<@ofbizUrl>ProcessDeliveryScheduleCorrection</@ofbizUrl>');"/>
    	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    	<input type="submit" style="padding:.3em" id="changeCancel" value="Cancel" onclick="javascript:processIndentEntry('indententry','<@ofbizUrl>DSCorrectionEntryInit</@ofbizUrl>');"/>   	
    </div>     
	</#if> 
	</div>
</div>     
</div>
</div>	