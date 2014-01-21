
<#assign changeRowTitle = "Changes">                

<#include "reconcilEntryInc.ftl"/>
<div class="full">
  <div class="lefthalf">
	<div class="screenlet">
		<div class="grid-header" style="width:100%">
			<label> <h2><font color="indianred" face="Comic Sans MS">Dispatch Reconciliation Entry</font></h2> &nbsp;&nbsp;&nbsp;&nbsp;</label>
		</div>
    	<div class="screenlet-body">
			<form name="reconcilEntryInit" id="reconcilEntryInit">  
      			<table width="60%" border="0" cellspacing="0" cellpadding="0">     
        			<tr>
          				<td>&nbsp;</td>
          				<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>${uiLabelMap.SupplyDate}:</div></td>
          				<td>&nbsp;</td>
          				<#if effectiveDate?exists && effectiveDate?has_content>  
	  	  					<input type="hidden" name="effectiveDate" id="effectiveDate" value="${effectiveDate}"/>  
          					<td valign='middle'>
            					<div class='tabletext h2'>${effectiveDate}</div>
          					</td>       
       	  				<#else>               
          					<td valign='middle'>          
            	 				<input class='h2' type="text" name="effectiveDate" id="effectiveDate" value="${defaultEffectiveDate}"/>          
          					</td>
       					</#if>
        			</tr>
        			<tr><td><br/></td></tr>
        			<tr>
          				<td>&nbsp;</td>
	          			<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Trip:</div></td>
	          			<td>&nbsp;</td>
          				<td valign='middle'>
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
          				</td>
        			</tr>
        			<tr><td><br/></td></tr>
       				<tr>
       					<td>&nbsp;</td>
          				<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Route:</div></td>
          				<td>&nbsp;</td>
       	  				<td valign='middle'>
   							<input type="text" name="routeId" id="routeId" size="8" onblur='this.value=this.value.toUpperCase()'/>
   	    					<span class="tooltipbold" id="routeTooltip"></span>
          				</td>
			        </tr>
			        <tr><td><br/></td></tr>
      			</table>
       		<div name ="displayMsg" id="changeIndentEntry_spinner"/>   
		</form>
		<br/>
    	</div>
	</div>
	</div> 
 	<div class="screenlet">
    	<div class="screenlet-body">
 			<div>
 		<div class="grid-header" style="width:60%">
			<label> Product-Quantity Entry &nbsp;&nbsp;&nbsp;&nbsp;</label>
		</div>
		<div id="myGrid1" style="width:52%;height:250px;"></div>
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
</div>

  