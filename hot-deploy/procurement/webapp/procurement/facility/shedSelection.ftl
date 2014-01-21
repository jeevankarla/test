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
      				Select Shed:
      				<select name="shedId" onchange="javascript:setShedUnitsDropDown(this);" id="masterShedDropDown">
      					<option value=""/>
                		<#list shedList as shed>    
                  	    	<option value='${shed.facilityId}' >
	                    		${shed.facilityName}
	                  		</option>
                		</#list>             
					</select>
          		</td> 
          	</tr>      	   	      	                 
	</table>
</div>
</div>
</div> 
    