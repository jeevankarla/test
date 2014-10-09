<div id="wrapper" style="width: 100%; height:100%">
	<div>
		<form method="post" name="AdjustmentsEntry" id="AdjustmentsEntry" action="<@ofbizUrl>populateProcurementPeriodBilling</@ofbizUrl>">     
      		<table width="35%" border="0" cellspacing="0" cellpadding="0">
      			<tr>
          			<td>&nbsp;</td>
          			<td align='right' valign='middle' nowrap="nowrap"><div class='h2'>shed Code :</div></td>
                    <#if shedName?has_content>
          				<td  align='left'> <div class='h2'>&#160;   ${shedName?if_exists}</div></td>	
          				<input type="hidden" name="shedId" id="shedId" value="${shedId}">
                    <#else>
                    <td align='left'> 
			      		<select name="shedId" class='h2'onchange="javascript:setBillingShedUnitsDropDown(this);">
			      		<option value="">
			                <#list shedList as shed>    
			                  	<#assign isDefault = false>
								<option value='${shed.facilityId}'<#if isDefault> selected="selected"</#if>>
			                    	${shed.facilityName}
			                  	</option>                  	
			      			</#list>            
						</select>
					</td>
					</#if>
				</tr>  
	                </td>
           			<td>&nbsp;</td>
        		</tr>  
        		<tr>
        		    <td>&nbsp;</td>
        			<td align='right' valign='middle' nowrap="nowrap"><div class='h2'>Unit :</div></td>
        			<#if unitId?has_content>
        				<input type="hidden" name="facilityId" id="facilityId" value="${unitId}">
             			<td align='left'>
             				<h2>&#160;   ${unitCode}<span class="tooltip">${unitName?if_exists}</span></h2>
             			</td>
             		<#else>	
        			<td align='left'>
                      <#assign elementName= "facilityId">
                      <select name="facilityId" class='h4'  onchange="javascript:getTimePeriodsByUnit($('[name=shedId]').val(),$('[name=facilityId]').val());">
                		<#list unitsList as units>    
                  	    	<option value='${units.facilityId}' >
	                    		${units.facilityName}
	                  		</option>
                		</#list>             
					</select>				
        			</td>
        			</#if>
        		</tr>
        		<tr>
          			<td>&nbsp;</td>
          			<td align='right' valign='middle' nowrap="nowrap"><div class='h2'>Custom Time Period :</div></td>
                 	<td  align='left'> 
		      			<select name="customTimePeriodId" class='h2'>      			     			
			                <#list timePeriodList as timePeriod>
			                	<#if !timePeriodId?exists>    
				                  	<#assign isDefault = false>                  
			                    </#if>
			                    <#if timePeriodId?exists>
			                    	<#if timePeriodId == timePeriod.customTimePeriodId>
			      						<option  value="${timePeriodId}" selected="selected">${timePeriod.periodName}</option>
			      					</#if>	
			      					<#else>
			      						<option value='${timePeriod.customTimePeriodId}'<#if isDefault> selected="selected"</#if>>
			                    			${timePeriod.periodName}
			                  			</option>
			      					</#if>
			      			</#list>            
						</select>
		          	</td>
         			<td>&nbsp;</td>
         		</tr>   
<!-- Submit Button !-->
      	<div align="center">
      	 	<table width="35%" border="0" cellspacing="0" cellpadding="0">
        		<tr>       
          			<td>&nbsp;</td>
          			<td valign='middle' nowrap="nowrap"><div class='h2'> &nbsp;</div></td>
          			<td align='right'> 
           				<div class='tabletext h2'>            
             				<input type="submit" class="smallSubmit" value="Generate">        	
            			</div>
          			</td>
          			<td valign='middle'>          
          			</td>
         			<td>&nbsp;</td>
        		</tr>             
        		<tr><td><br/></td></tr>
      		</table> 
      	</div>	     
 	</form>
</div>
</div>
<script type="application/javascript">
	<#if !unitName?has_content>
		<#if shedName?has_content>
			setBillingShedUnitsDropDownByValue($('[name=shedId]').val());
			getTimePeriodsByUnit($('[name=shedId]').val(),$('[name=facilityId]').val());
		</#if>
	<#else>
		<#if unitName?has_content>
			getTimePeriodsByUnit($('[name=shedId]').val(),$('[name=facilityId]').val()); 
		</#if>
	</#if>
	<#if !shedName?has_content>
		var options={};
		jQuery("[name='"+"facilityId"+"']").html(options);
	</#if>
</script>
