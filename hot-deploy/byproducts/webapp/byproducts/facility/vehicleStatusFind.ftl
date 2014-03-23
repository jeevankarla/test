<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	




<div class="screenlet">
	 <div class="screenlet-title-bar">
    <ul>
      <li class="h3">VehicleStatus Find</li>    
    </ul>
    <br class="clear"/>
  </div>
    <div class="screenlet-body">
<form method="post" name="VehicleStatusForm" action="VehicleStatus">	 
      <table width="60%" border="0" cellspacing="0" cellpadding="0">    
        <tr>
          <td>&nbsp;<input type="hidden" name="hideSearch"  value="N"/></td>
          <td align='right' valign='middle' nowrap="nowrap"><div class='h3'>${uiLabelMap.SupplyDate}:</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
           <div class='h3'><input type="text" id="date" name="supplyDate" value="${defaultEffectiveDate?if_exists}"/></div></td>
        </tr> 
        <tr><td><br/></td></tr>    
        <tr>
		  <td>&nbsp;</td>
          <td align='right' valign='middle' nowrap="nowrap"><div class='h3'>${uiLabelMap.SupplyTime}:</div></td>
          <td>&nbsp;</td>
			<td valign='middle'>
			<select name="subscriptionTypeId" class='h2' id="subscriptionTypeId" onchange="javascript:setRouteDropDown(this);">
			  <option value=''>All</option>
				<#list subscriptionTypeList as subType>
		    		<#if subscriptionTypeId?exists && (subscriptionTypeId == subType.subscriptionTypeId)>
						<option  value="${subscriptionTypeId}" selected="selected">${subType.description}</option>
					<#else>
						<option value='${subType.subscriptionTypeId}'>
							${subType.description}
						</option>
					</#if>
				</#list>            
			</select>
			</td>
        </tr>  
         <tr><td><br/></td></tr>    
        	<tr>
				<td>&nbsp;</td>
  				<td align='right' valign='middle' nowrap="nowrap"><div class='h2'>Route:</div></td>
  				<td>&nbsp;</td>
  				<td valign='middle'>
  					<input type="text" name="routeId" id="routeId" class='h2' size="8" onblur='this.value=this.value.toUpperCase(),setRouteBoothsDropDown(this);'/>
    				<span  class='h2'  id="routeTooltip"></span>
  				</td>
			 </tr>  
		<tr><td><br/></td></tr>     
	    <tr>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td align='left'>
          <input type='submit' value="Find"  name='findVehicle'/>
        </td>
        </tr>          
      </table>
</form>
<br/>
    </div>
</div>
<script type="application/javascript">
   var boothsList =  ${StringUtil.wrapString(boothsJSON)}
	var routesList =  ${StringUtil.wrapString(routesJSON)}
	<#if (showBoothAutoSuggest?has_content) && (showBoothAutoSuggest != 'N') >
			var routeBoothsData = ${StringUtil.wrapString(facilityItemsJSON)}
			var supplyRouteList =  ${StringUtil.wrapString(supplyRouteItemsJSON)}
			function setRouteDropDown(selection){	
				//routesList = routesList;
				
				routesList = supplyRouteList[selection.value];
				
				if(selection.value =="" || typeof selection.value == "undefined"){
					routesList =  ${StringUtil.wrapString(routesJSON)}
				}				
			}	
			function setRouteBoothsDropDown(selection){	
				boothsList = routeBoothsData[selection.value];
				
				if(selection.value =="" || typeof selection.value == "undefined"){
					boothsList =  ${StringUtil.wrapString(boothsJSON)}
				}				
			}
			<#if subscriptionTypeId?exists && subscriptionTypeId?has_content> 
				routesList =  supplyRouteList["${subscriptionTypeId}"]; 
			</#if>		
			<#if routeId?exists && routeId?has_content> 
				 boothsList = routeBoothsData["${routeId}"];
			</#if>	
						
	</#if>
	</script>	