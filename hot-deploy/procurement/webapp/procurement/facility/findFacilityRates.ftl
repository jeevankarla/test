<script type="application/javascript">
	<#if (showBoothAutoSuggest?has_content) && (showBoothAutoSuggest != 'N')>
			var routeBoothsData = ${StringUtil.wrapString(facilityItemsJSON)}
			var boothsList =${StringUtil.wrapString(boothsJSON)};

			function setRouteBoothsDropDown(selection){	
				boothsList = routeBoothsData[selection.value]; 
				if(selection.value =="" || typeof selection.value == "undefined"){
						boothsList =  ${StringUtil.wrapString(boothsJSON)};
				}
				
			}	
			<#if routeId?exists && routeId?has_content> 
				boothsList = routeBoothsData["${routeId}"]; 
			</#if>
			$(function() {
				$('#facilityId').keypress(function (e) { 
					$("#facilityId").autocomplete({ source: boothsList });	
				});
			});
	</#if>	
	
	$(document).ready(function(){
	
	var productAutoJSON = ${StringUtil.wrapString(productsJSON)!'[]'};	
	$("#productId").autocomplete({ source: productAutoJSON });
	
	});
	
</script>	

<form method="post" name="findFacilityRates" action="<@ofbizUrl>FindProcFacilityRates</@ofbizUrl>">  
      <input type="hidden" name="hideSearch" id="hideSearch" value="N"/>
      <table width="60%" border="0" cellspacing="0" cellpadding="0">     
        <tr><td><br/></td></tr> 
        <#if (showBoothAutoSuggest?has_content) && (showBoothAutoSuggest != 'N')>
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Route:</div></td>
          <td>&nbsp;</td>
       	  <td valign='middle'>
          	<#assign isDefault = false> 
      		<select name="routeId" onchange="javascript:setRouteBoothsDropDown(this);" id="routeId">  
      		<option value="" selected="selected"></option>   			     			
                <#list routesList as route>    
      					<option  value="${route.facilityId}" >${route.facilityName}</option>
      			</#list>            
			</select>
          </td>
        </tr> 
        </#if>          
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Retailer:</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>          
             <input class='h2' type="text" size="17" name="facilityId" id="facilityId" value="${parameters.facilityId?if_exists}" readonly/>          
          </td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Product:</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>          
             <input class='h2' type="text" size="17" name="productId" id="productId" value="${productId?if_exists}"/>          
          </td>
        </tr>      
        
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Rate Type:</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>          
             <select name="rateTypeId" id="rateTypeId">
	             <option value=""></option>   			     			
	                <#list rateTypeList as rateType>    
	      					<option  value="${rateType.rateTypeId}" >${rateType.description}</option>
	      			</#list>            
			</select>            
          </td>
        </tr> 
         <tr><td><br/></td></tr>
        <tr>
        <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h3'>	</div></td>
           <td>&nbsp;</td>
           <td valign='middle'>
             <input type="submit" style="padding:.3em" name="submit" value="Find"/>
           </td>
         </tr>        
      </table>
</form>