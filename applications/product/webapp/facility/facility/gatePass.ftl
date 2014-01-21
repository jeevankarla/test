
<#assign changeRowTitle = "Gate Pass">                

<#include "changeIndentInc.ftl"/>
<script type="text/javascript">
$(document).ready(function(){
	$( "#effectiveDate" ).datepicker({
			dateFormat:'MM dd, yy',
			changeMonth: true,
			numberOfMonths: 1});			
		$('#ui-datepicker-div').css('clip', 'auto');
		
	$('#boothId').keypress(function (e) {
		if (e.which == $.ui.keyCode.ENTER) {
			$('#gatepassinit').submit();
			return false;   
		}
	});	
		
	});	
</script>
<div class="screenlet">
	<div class="screenlet-title-bar">
         <ul>
   			 <li>
   				<a href="<@ofbizUrl>checkListReport?userLoginId=${userLogin.get("userLoginId")}&&checkListType=gatepass</@ofbizUrl>" target="_blank">Gate Pass Check List</a>
             </li>
         </ul>
    </div>
    <div class="screenlet-body">
<form method="post" name="gatepassinit" id="gatepassinit" action="<@ofbizUrl>GatePass</@ofbizUrl>">  
      <!--<input type="hidden" name="effectiveDate" id="effectiveDate" value="${defaultEffectiveDateTime!''}"/>-->
   
      <table width="60%" border="0" cellspacing="0" cellpadding="0">     
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>${uiLabelMap.SupplyDate}:</div></td>
          <td>&nbsp;</td>
           <#if booth?exists && booth?has_content>
          <td valign='middle'>   		
				<div class='tabletext h2'>
               	${parameters.effectiveDate}             
            </div>
		  </td>
		  <#else>
		  	<td valign='middle'>   		
				<input type="text" name="effectiveDate" id="effectiveDate" size="15" value="${Static["org.ofbiz.base.util.UtilDateTime"].nowDateString("MMMM dd, yyyy")}"/>
		  	</td>
		  </#if>
         <!--<#if parameters.shipmentTypeId?exists>
          <td valign='middle'>   
        	<#if parameters.shipmentTypeId = 'PM_SHIPMENT_SUPPL'>       	
				<div id="Shipment_AM" style="display:none;" class='tabletext h2'>${defaultEffectiveDate}</div>
		 		<div id="Shipment_PM" style="display:block;" class='tabletext h2'>${defaultEffectivePrevDate}</div> 
			<#elseif parameters.shipmentTypeId = 'AM_SHIPMENT_SUPPL'>
			<div id="Shipment_AM" style="display:block;" class='tabletext h2'>${defaultEffectiveDate}</div>
		 	<div id="Shipment_PM" style="display:none;" class='tabletext h2'>${defaultEffectivePrevDate}</div> 
		 	</#if> 
          </td>
		  <#else>
          <td valign='middle'>   		
			<div id="Shipment_AM" style="display:block;" class='tabletext h2'>${defaultEffectiveDate}</div>
		 	<div id="Shipment_PM" style="display:none;" class='tabletext h2'>${defaultEffectivePrevDate}</div> 
		  </td>
        </#if>-->
        </tr> 
        <tr><td><br/></td></tr>
         <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>AM/PM:</div></td>
          <td>&nbsp;</td>
       <#if shipmentTypeId?exists && booth?exists>
	  	  <input type="hidden" name="shipmentTypeId" id="shipmentTypeId" value="${parameters.shipmentTypeId}"/>   	   	   	   
          <td valign='middle'>
            <div class='tabletext h2'>
               	${shipmentTypeId}             
            </div>
          </td>       
       <#else>
          <td valign='middle'>
          	<#assign isDefaultShipment = false>
          	<!-- toggle the PM Sales -->          	     	
      		<select name="shipmentTypeId" class='h2' <#if !enableSameDayPmEntry>onchange="changesupplydate(this.value);" </#if>>
                <#list shipmentTypes as shipmentType>
                	<#if !shipmentTypeId?exists>    
	                  	<#assign isDefaultShipment = false>                
                    	<#if shipmentType.shipmentTypeId = "AM_SHIPMENT_SUPPL">
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
          	<#assign isDefault = false>
      		<select name="productSubscriptionTypeId" class='h2'>
      			<#list prodSubTypes as prodSubType>
                	<#if !productSubscriptionTypeId?exists>    
	                  	<#assign isDefault = false>                
	                    <#if prodSubType.enumId = "CASH">
	                      <#assign isDefault = true>
	                    </#if> 
                    </#if>
                    <#if productSubscriptionTypeId?exists && (productSubscriptionTypeId = prodSubType.enumId)>
      					<option  value="${productSubscriptionTypeId}" selected="selected">${prodSubType.description}</option>
      					<#else>
      						 <#if prodSubType.enumId != "CARD">
	      						<option value='${prodSubType.enumId}'<#if isDefault> selected="selected"</#if>>
	                    			${prodSubType.description}
	                  			</option>
                  			</#if>
      				</#if>
      			</#list>                         
			</select>
          </td>
       </#if>   
        </tr>           
        <tr><td><br/></td></tr> 
         <#if (showBoothAutoSuggest?has_content) && (showBoothAutoSuggest != 'N')>
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Route:</div></td>
          <td>&nbsp;</td>
       	  <td valign='middle'>
          	<#assign isDefault = false> 
      		<select name="routeId" onchange="javascript:setRouteBoothsDropDown(this);" id="routeId">  
      		<option value=""></option>   			     			
                <#list routesList as route>    
                	<#if !routeId?exists>    
	                  	<#assign isDefault = false>                
	                    <#if route.facilityId == "">
	                      <#assign isDefault = true>
	                    </#if> 
                    </#if>
                    <#if routeId?exists && (routeId == route.facilityId)>
      					<option  value="${routeId}" selected="selected">${route.facilityId}</option>
      					<#else>
      						<option value='${route.facilityId}'<#if isDefault> selected="selected"</#if>>
                    			${route.facilityName}
                  		</option>
      				</#if>
      			</#list>            
			</select>
          </td>
        </tr> 
        <tr><td><br/></td></tr> 
         </#if>
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>${uiLabelMap.Booth}:</div></td>
          <td>&nbsp;</td>
       <#if booth?exists && booth?has_content>  
	  	  <input type="hidden" name="boothId" id="boothId" value="${booth.facilityId}"/>  
          <td valign='middle'>
            <div class='tabletext h2'>
               	${booth.facilityId} [${booth.parentFacilityId?if_exists}: ${booth.facilityName?if_exists}]             
            </div>
          </td>       
       <#else>               
          <td valign='middle'>          
             <input class='h2' type="text" size="10" maxlength="4" name="boothId" id="boothId" value=""/>          
             <span class="tooltip">Input booth number and press Enter</span>
          </td>
       </#if>
        </tr>        
      </table>
</form>
<br/>
<form method="post" id="gatepass" action="<@ofbizUrl>GatePassInit</@ofbizUrl>">
	<input type="hidden" name="effectiveDate" id="effectiveDate" size="15" value="${parameters.effectiveDate}"/>
	<input type="hidden" name="boothId" id="boothId" value="${parameters.boothId?if_exists}"/>
	<input type="hidden" name="routeId" id="routeId" value="${parameters.routeId?if_exists}"/>
	<input type="hidden" name="shipmentTypeId" id="shipmentTypeId" value="${parameters.shipmentTypeId?if_exists}"/>  
	<input type="hidden" name="productSubscriptionTypeId" id="productSubscriptionTypeId" value="${parameters.productSubscriptionTypeId?if_exists}"/>   	   	   	   

 			<div class="grid-header" style="width:100%">
				<label>Gate Pass</label>
			</div>
			<div id="myGrid1" style="width:100%;height:100px;"></div>
			<br>
<#if dataJSON?exists>					
    <div align="center">
    	<input type="submit" style="padding:.3em" id="changeSave" value="Save" onclick="javascript:processChangeIndent('gatepass','<@ofbizUrl>createGatePassIndent</@ofbizUrl>');"/>
    	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    	<input type="submit" style="padding:.3em" id="changeCancel" value="Cancel"/>   	
    </div>     
</#if> 
</form>
    </div>
</div>  

 
<div class="screenlet">
    <div class="screenlet-body">
 		<div class="grid-header" style="width:100%">
			<label>Last Gate Pass Order <#if lastChangeSubProdMap?exists && lastChangeSubProdMap?has_content>[made by ${lastChangeSubProdMap.modifiedBy?if_exists} at ${lastChangeSubProdMap.modificationTime?if_exists}] </#if></label>
		</div>    
		<div id="myGrid2" style="width:100%;height:80px;"></div>		
    </div>
</div>     
 	