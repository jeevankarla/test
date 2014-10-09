<div>
  <div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Shed Selection</h3>
    </div>
    <div class="screenlet-body">
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;" >      	          	     	   
      	<tr class="alternate-row">     		
      			<td></td>
      			<td align="center">
	   				  SHED :
      			  <#if shedId?has_content>
						<input type="hidden" name="shedId" id="shedId" value="${shedId}" />
	      			  	${shedName?if_exists}
      			  <#else>	  
      				<select name="shedId"  onchange="javascript:setShedUnitsDropDown(this);" id="masterShedDropDown">
      					<option value=""/>
                		<#list shedList as shed>    
                  	    	<option value='${shed.facilityId?if_exists}' >
	                    		${shed.facilityName?if_exists}
	                  		</option>
                		</#list>             
					</select>
				  </#if>	
					
      				&#160;&#160;&#160;&#160;&#160;&#160; UNIT :
						
      				  <#if unitId?has_content>
      				  	${unitName?if_exists}
      				  	<input type = "hidden" name = "unitId" id = "unitId" value = "${unitId}" />
      				  <#else>
      				  	<#if unitMapsList?has_content>
      				  		<select name="unitId"  onchange="javascript:setUnitRoutesAndTimePeriods();">
			                        <option value=""/>
			                        <#list unitMapsList as units>    
			                            <option value='${units.facilityId}' >
			                                ${units.facilityName?if_exists}
			                            </option>
			                        </#list>             
		                   		</select>
      				  	<#else>
		      				  <select name="unitId"  onchange="javascript:setUnitRoutesAndTimePeriods();">
			                        <option value=""/>
			                       <#if unitsList?has_content> 
			                        <#list unitsList as units>    
			                            <option value='${units}' >
			                                ${units?if_exists}
			                            </option>
			                        </#list> 
			                       </#if>             
		                   		</select>
		                   	  </#if>	
                   	  </#if>	
          		</td> 
          		<td>allTimePeriods<input type="checkbox" name="timePeriodFlag" id="timePeriodFlag" value="N"  onclick="javascript:timePeriodFlagChecked(this);"/></td>
          	</tr>      	   	      	                 
	  </table>
    </div>
  </div>
</div> 
    