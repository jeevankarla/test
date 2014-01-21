
<#assign changeRowTitle = "Changes">                

<#include "changeIndentInc.ftl"/>

<div class="screenlet">
	<div class="screenlet-title-bar">
        <ul>
   			<li>
   				<a href="<@ofbizUrl>checkListReport?userLoginId=${userLogin.get("userLoginId")}&&checkListType=changeindent&&all=Y</@ofbizUrl>" target="_blank">All Check List</a>
            </li>  
   			<li>
   				<a href="<@ofbizUrl>checkListReport?userLoginId=${userLogin.get("userLoginId")}&&checkListType=changeindent</@ofbizUrl>" target="_blank">My Check List</a>
            </li>                      
         </ul>
     </div>
    <div class="screenlet-body">
<form method="post" name="changeindentinit" action="<@ofbizUrl>ChangeIndentMIS</@ofbizUrl>">  
      <input type="hidden" name="effectiveDate" id="effectiveDate" value="${defaultEffectiveDateTime!''}"/>
   
      <table width="60%" border="0" cellspacing="0" cellpadding="0">     
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>${uiLabelMap.SupplyDate}:</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext h2'>            
            	${defaultEffectiveDate}
            </div>
          </td>
        </tr>
        <#if enableLmsPmSales?exists && enableLmsPmSales>        
	        <tr><td><br/></td></tr>             
	        <tr>
	          <td>&nbsp;</td>
	          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Supply Type:</div></td>
	          <td>&nbsp;</td>
	       <#if subscriptionTypeId?exists && booth?exists>
		  	  <input type="hidden" name="subscriptionTypeId" id="subscriptionTypeId" value="${parameters.subscriptionTypeId}"/>   	   	   	   
	          <td valign='middle'>
	            <div class='tabletext h2'>
	               	${subscriptionTypeId}             
	            </div>
	          </td>       
	       <#else>
	          <td valign='middle'>
	          	<#assign isDefault = false> 
	      		<select name="subscriptionTypeId" class='h2'>      			     			
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
	       </#if>   
	        </tr>
	    	<#else>
	    	<input type="hidden" name="subscriptionTypeId" id="subscriptionTypeId" value="AM"/> 
	    </#if>    
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
	                    <#if prodSubType.enumId == "CASH">
	                      <#assign isDefault = true>
	                    </#if> 
                    </#if>
                    <#if productSubscriptionTypeId?exists && (productSubscriptionTypeId == prodSubType.enumId)>
      					<option  value="${productSubscriptionTypeId}" selected="selected">${prodSubType.description}</option>
      					<#else>
      						<option value='${prodSubType.enumId}'<#if isDefault> selected="selected"</#if>>
                    			${prodSubType.description}
                  		</option>
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
<form method="post" id="changeindent" action="<@ofbizUrl>ChangeIndentInit</@ofbizUrl>">  
	<input type="hidden" name="effectiveDate" id="effectiveDate" value="${defaultEffectiveDateTime}"/>
	<input type="hidden" name="boothId" id="boothId" value="${parameters.boothId?if_exists}"/>
	<input type="hidden" name="routeId" id="routeId" value="${parameters.routeId?if_exists}"/> 
	<input type="hidden" name="productSubscriptionTypeId" id="productSubscriptionTypeId" value="${parameters.productSubscriptionTypeId?if_exists}"/>   	   	   	   
	<input type="hidden" name="subscriptionTypeId" id="subscriptionTypeId" value="${parameters.subscriptionTypeId?if_exists}"/>
 			<div class="grid-header" style="width:100%">
				<label>Change Indent</label>
			</div>
			<div id="myGrid1" style="width:100%;height:125px;"></div>
			<br>
<#if dataJSON?exists>					
    <div align="center">
    	<input type="submit" style="padding:.3em" id="changeSave" value="Save" onclick="javascript:processChangeIndent('changeindent','<@ofbizUrl>processChangeIndentMIS</@ofbizUrl>');"/>
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
			<label>Last Change <#if lastChangeSubProdMap?exists && lastChangeSubProdMap?has_content>[made by ${lastChangeSubProdMap.modifiedBy?if_exists} at ${lastChangeSubProdMap.modificationTime?if_exists}] </#if></label>
		</div>    
		<div id="myGrid2" style="width:100%;height:75px;"></div>		
    </div>
</div>     
 	