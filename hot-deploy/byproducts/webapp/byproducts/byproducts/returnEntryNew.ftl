<#assign changeRowTitle = "Changes">                
<#include "returnEntryInc.ftl"/>
<div class="full">
  <div class="lefthalf">
	<div class="screenlet">
		<div class="grid-header" style="width:100%">
			<label> <h2>Return Entry</h2> &nbsp;&nbsp;&nbsp;&nbsp;</label>
		</div>
    	<div class="screenlet-body">
			<form name="reconcilEntryInit" id="reconcilEntryInit">  
				<input type="hidden" name="screenFlag" id="screenFlag" value="${screenFlag}"/>
      			<table width="60%" border="0" cellspacing="0" cellpadding="0">     
        			<tr>
          				<td>&nbsp;</td>
          				<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>${uiLabelMap.ShipmentDate}:</div></td>
          				<td>&nbsp;</td>
          				<td valign='middle'>
          				<#if returnHeader?has_content>  
	  	  					<div class='tabletext h3'>
            					${shipDate?if_exists}
            				</div>
            				<input class="h2" type="hidden" name="effectiveDate" id="effectiveDate" value="${shipDate?if_exists}"/>       
       	  				<#else>               
            	 			<input class='h2' type="text" name="effectiveDate" id="effectiveDate" value="${defaultEffectiveDate}"/>          
       					</#if>
       					</td>
        			</tr>
        			<tr><td><br/></td></tr>
        			
        			<tr>
          				<td>&nbsp;</td>
          				<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Return Header Type: </div></td>
          				<td>&nbsp;</td>
       					<td valign='middle'>
       						<#if returnHeader?has_content>
       							<div class='tabletext h3'>
            						<input class="h2" type="text" size="18" maxlength="20" name="returnHeaderTypeId" id="returnHeaderTypeId" value="${returnHeader.returnHeaderTypeId?if_exists}" readonly/>
            					</div>
       						<#else>
       							<select name="returnHeaderTypeId" class='h2' id="returnHeaderTypeId">
	       							<#list headerType as eachType>
	       								<option value="${eachType.returnHeaderTypeId}">${eachType.description?if_exists}</option>
	       							</#list>	
								</select>  
       						</#if>
       						        
       					</td>
        			</tr>
        			<tr><td><br/></td></tr>	
        			<tr>
          				<td>&nbsp;</td>
          				<td align='left' valign='middle' nowrap="nowrap" ><div class='h2'>Return Type:</div></td>
          				<td>&nbsp;</td>
       					<td valign='middle'>
       						<#if returnHeader?has_content>
       							<div class='tabletext h3'>
            						<input class="h2" type="text" size="18" maxlength="20" name="returnType" id="returnType" value="${returnType?if_exists}" readonly/>
            					</div>
       						<#else>
	       						<select name="returnType" class='h2' id="returnType" onchange="javascript: hideReturnToggle(this);">
	       							<option value='sales'>Sale Return</option>
	       							<option value='crate'>Crate Return</option>   
								</select>
							</#if>          
       					</td>
        			</tr>
        			<tr><td><br/></td></tr>
        			<tr>
          				<td>&nbsp;</td>
          				<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Needs Inventory Receive:</div></td>
          				<td>&nbsp;</td>
       					<td valign='middle'>
       						<#if returnHeader?has_content>
       							<div class='tabletext h3'>
            						<input class="h2" type="text" size="18" maxlength="20" name="receiveInventory" id="receiveInventory" value="${returnHeader.needsInventoryReceive?if_exists}" readonly/>
            					</div>
       						<#else>
	       						<select name="receiveInventory" class='h2' id="receiveInventory">
	       							<option value='N'>No</option>
	       							<option value='Y'>Yes</option>   
								</select>
							</#if>          
       					</td>
        			</tr>
        			<tr><td><br/></td></tr>
        			<tr>
          				<td>&nbsp;</td>
	          			<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Trip:</div></td>
	          			<td>&nbsp;</td>
          				<td valign='middle'>
          					<#if returnHeader?has_content && tripId?has_content>
       							<div class='tabletext h3'>
            						<input class="h2" type="text" size="18" maxlength="20" name="tripId" id="tripId" value="${tripId?if_exists}" readonly/>
            					</div>
       						<#else>
		          				<#assign isDefault = false> 
		      					<select name="tripId" class='h2' id="tripId">
		                			<#list prodSubTrips as eachTrip>
		                	    		<#if tripId?exists && (tripId == eachTrip.enumId)>
		      								<option  value="${tripId}" selected="selected">${tripId}</option>
		      							<#else>
		      								<option value='${eachTrip.enumId}'>
		                    					${eachTrip.description}
		                  					</option>
		      							</#if>
		      						</#list>            
								</select>
							</#if>
          				</td>
        			</tr>
        			<tr><td><br/></td></tr>
       				<tr>
       					<td>&nbsp;</td>
          				<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Route:</div></td>
          				<td>&nbsp;</td>
       	  				<td valign='middle'>
       	  					<#if returnHeader?has_content && routeId?has_content>
       							<div class='tabletext h3'>
            						<input class="h2" type="text" size="18" maxlength="20" name="routeId" id="routeId" value="${routeId?if_exists}" readonly/>
            					</div>
       						<#else>
       							<input type="text" name="routeId" id="routeId" size="8" onblur='this.value=this.value.toUpperCase()'/>
   	    						<span class="tooltipbold" id="routeTooltip"></span>
       						</#if>
   							
          				</td>
			        </tr>
			        <tr><td><br/></td></tr>
			        <tr>
       					<td>&nbsp;</td>
          				<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Dealer:</div></td>
          				<td>&nbsp;</td>
       	  				<td valign='middle'>
       	  					<#if returnHeader?has_content>
       							<div class='tabletext h3'>
            						<input class="h2" type="text" size="18" maxlength="20" name="boothId" id="boothId" value="${returnHeader.originFacilityId?if_exists}" readonly/>
            					</div>
       						<#else>
       							<input type="text" name="boothId" id="boothId" size="8" onblur='this.value=this.value.toUpperCase()'/>
   	    						<span class="tooltipbold" id="boothTooltip"></span>
       						</#if>
   							
          				</td>
			        </tr>
      			</table>
       		<div name ="displayMsg" id="changeIndentEntry_spinner"/>   
		</form>
		<br/>
    	</div>
	</div>
	</div>
	</div>
	<div class="righthalf"> 
	 	<div class="screenlet">
	    	<div class="screenlet-body">
	 			<div>
	 				<div class="grid-header" style="width:100%">
						<label> Return Items Entry &nbsp;&nbsp;&nbsp;&nbsp;</label>
					</div>
					<div id="myGrid1" style="width:52%;height:300px;"></div>
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

  