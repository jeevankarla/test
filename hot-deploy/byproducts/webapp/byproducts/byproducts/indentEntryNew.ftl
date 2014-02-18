
<#assign changeRowTitle = "Changes">                

<#include "indentEntryInc.ftl"/>
<#if screenFlag?exists && screenFlag == 'tempRoute'>
	<#include "TempRouteChangeToolTip.ftl"/>
</#if>
<div class="full">
  <div class="lefthalf">
	<div class="screenlet">
		<#if screenFlag?exists && screenFlag == 'DSCorrection'><div class="grid-header" style="width:100%;" id="test"><#else><div class="screenlet-title-bar"></#if>
		
			<h3><#if screenFlag?exists && screenFlag == 'DSCorrection'>Trucksheet Correction<#else>Indent Entry</#if> 
        	<#if screenFlag?exists && screenFlag != 'DSCorrection'>
        	<ul>
   				<li>
   					<a href="<@ofbizUrl>checkListReport.txt?userLoginId=${userLogin.get("userLoginId")}&&checkListType=changeindent&&all=Y</@ofbizUrl>" target="_blank">All Check List</a>
            	</li>  
   				<li>
   					<a href="<@ofbizUrl>checkListReport.txt?userLoginId=${userLogin.get("userLoginId")}&&checkListType=changeindent</@ofbizUrl>" target="_blank">My Check List</a>
            	</li>                      
         	</ul>
         	</#if>
         	</h3>
     	</div>
    
    	<div class="screenlet-body">
			<form name="changeindentinit" id="changeindentinit">  
      			<#-- <input type="hidden" name="effectiveDate" id="effectiveDate" value="${defaultEffectiveDateTime!''}"/>-->
   	  			<input type="hidden" name="tempRouteId" id="tempRouteId"/>
   	  			<input type="hidden" name="tempTripId" id="tempTripId"/>
      			<table width="60%" border="0" cellspacing="0" cellpadding="0">     
        			<tr>
          				<td>&nbsp;</td>
          				<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>${uiLabelMap.SupplyDate}:</div></td>
          				<td>&nbsp;</td>
       					<#if screenFlag?exists && screenFlag != 'DSCorrection'>
       						<input type="hidden" name="effectiveDate" id="effectiveDate" value="${defaultEffectiveDateTime!''}"/>
       						<td valign='middle'>
       							<div class='tabletext h2'>${defaultEffectiveDate}</div>
       						</td>
	       				<#else>
	       					<td>
            					<div class='tabletext h3'>
            						<input class="h2" type="text" size="18" maxlength="20" name="effectiveDate" id="effectiveDate" value="${defaultEffectiveDate}"/>
            					</div>
          					</td>        
            			</#if>
          			</tr>
          			<tr><td><br/></td></tr>
          			<tr>
          				<td>&nbsp;</td>
	          			<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Supply Time:</div></td>
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
	      					<select name="subscriptionTypeId" class='h2' id="subscriptionTypeId" onchange="javascript:setRouteDropDown(this);">
	      						    			     			
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
        			<#if enableLmsPmSales?exists && enableLmsPmSales>        
	        			<tr><td><br/></td></tr>             
	        			<tr></tr>
	    			<#else>
	    				<input type="hidden" name="subscriptionTypeId" id="subscriptionTypeId" value="AM"/> 
	    			</#if>    
        			<tr><td><br/></td></tr>
        			<input type="hidden" name="productSubscriptionTypeId" id="productSubscriptionTypeId" value="CASH"/>             
       				<tr>
          				<td>&nbsp;</td>
	          			<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Trip:</div></td>
	          			<td>&nbsp;</td>
	       				<#if tripId?exists && booth?exists>
		  	  				<input type="hidden" name="tripId" id="tripId" value="${parameters.tripId}"/>   	   	   	   
	          				<td valign='middle'>
	            				<div class='tabletext h2'>
	               					${tripId}             
	            				</div>
	          				</td>       
	       				<#else>
	          				<td valign='middle'>
	          				<#assign isDefault = false> 
	      					<select name="tripId" class='h2' id="tripId">
								<#if prodSubTrips?exists && prodSubTrips?has_content>
	                			<#list prodSubTrips as eachTrip>
	                	    		<#if tripId?exists && (tripId == eachTrip.enumId)>
	      								<option  value="${tripId}" selected="selected">${tripId}</option>
	      							<#else>
	      								<option value='${eachTrip.enumId}'>
	                    					${eachTrip.description}
	                  					</option>
	      							</#if>
	      						</#list>
	      						</#if>            
							</select>
	          				</td>
	       				</#if>   
        			</tr>
       				<tr><td><br/></td></tr>
       				<tr>
       					<td>&nbsp;</td>
          				<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Route:</div></td>
          				<td>&nbsp;</td>
       	  				<td valign='middle'>
          					<#assign isDefault = false> 
          					<#if routeId?exists && (routeId == route.facilityId)>
				 				<input type="hidden" name="routeId" id="routeId" size="15"/>
          					<#else>
          						<#if screenFlag?exists && screenFlag != 'indent'>
          							<input type="text" name="routeId" id="routeId" size="8" onblur='this.value=this.value.toUpperCase()'/>
          						<#else>
          							<input type="text" name="routeId" id="routeId" size="8" onblur='this.value=this.value.toUpperCase(),setRouteBoothsDropDown(this);'/>
          						</#if>	
      	    					<span class="tooltipbold" id="routeTooltip"></span>
      	    				</#if>
      	    				
          				</td>
          				<#if screenFlag?exists && screenFlag == 'tempRoute'>
         				<td>
         					<div id="tempRouteDiv" style="display:none">	
				        	 	<a class="button" href="javascript:showRouteToChange();">Route Change </a>
				        	</div>
				        </td>
				        </#if>
			        </tr>
			        <input type="hidden" name="routeChangeFlag" id="routeChangeFlag"/>           
			        <tr><td><br/></td></tr>
			        <#if (showBoothAutoSuggest?has_content) && (showBoothAutoSuggest != 'N')>
			        	<tr>
			          
			        	</tr> 
			        	<tr><td><br/></td></tr> 
			        </#if>          
			        <tr>
			          <td>&nbsp;</td>
			          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>${uiLabelMap.Dealer}:</div></td>
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
				             <input  type="text" size="8" maxlength="10" name="boothId" id="boothId" value="" onblur='this.value=this.value.toUpperCase()'/>        
				             <span class="tooltipbold" id="boothTooltip">Input name and press Enter</span>*
				             <div id="errorMsg"></div> 
				          </td>
				       </#if>
			        </tr>        
      			</table>
       		<div name ="displayMsg" id="changeIndentEntry_spinner"/>   
		</form>
		<br/>
    	</div>
	</div>
	</div>
	<#if screenFlag?exists && screenFlag != 'DSCorrection'> 
 	<div class="screenlet">
    	<div class="screenlet-body">
 			<div class="grid-header" style="width:100%">
				<label>Route:: <font color="red"><span id="routeName"></span></font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				Trip:: <font color="red"><span id="tripName"></span></font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				Total Crates:: <font color="red"><span id="routeCrateTotal"></span></font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				Capacity:: <font color="red"><span id="routeCapacity"></span></font></label>
				</label> 
			</div>    
		<div id="myGrid2" style="width:100%;height:150px;"></div>		
    </div>
    </#if>
</div>
</div>
<div class="righthalf">
<div class="screenlet">
    <div class="screenlet-body">
    <div>
 		<div class="grid-header" style="width:100%">
 			 
			<label>
				<#if screenFlag?exists && screenFlag != 'DSCorrection'> 
				Change Indent &nbsp;&nbsp;&nbsp;&nbsp;
				<span class="toolTipNotice" id="boothDepositTip"></span>&nbsp;&nbsp;
				<#else>
					Order Correction &nbsp;&nbsp;&nbsp;&nbsp;
				</#if>
				<span id="totalAmount">&nbsp;&nbsp;
			</label>
		</div>
		<div id="myGrid1" style="width:85%;height:500px;"></div>
			</br>
		</div>	
    	<div align="center">
    		<input type="button" style="padding:.3em" name="changeSave" id="changeSave" value="Save" />
    		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    		<input type="button" style="padding:.3em" id="changeCancel" value="Cancel"/>   	
    	</div>    
	</div>
</div>     
</div>
 	

</div>

  