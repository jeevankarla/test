 <#assign changeRowTitle = "Changes">                
<#include "vehicleStatusInc.ftl"/>    
                    <div id="wizard-2">
                  

     <h3>Product Returns</h3>
    <section>
     <div class="full">
     <div class="lefthalf">
	<div class="screenlet">
		<div class="grid-header" style="width:100%">
		<label>&nbsp;&nbsp;&nbsp;&nbsp;</label>
			<label> <h2>Product ReturnEntry</h2> &nbsp;&nbsp;&nbsp;&nbsp;</label>
		</div>
    	<div class="screenlet-body">
			<form name="reconcilEntryInit" id="reconcilEntryInit">  
				<input type="hidden" name="screenFlag" id="screenFlag" value="${screenFlag}"/>
				<input type="hidden" name="receiveInventory" id="receiveInventory" value="N"/>
				<input type="hidden" name="returnHeaderTypeId" id="returnHeaderTypeId" value="CUSTOMER_RETURN"/>
				<input type="hidden" name="shipmentId" id="shipmentId" value="${parameters.shipmentId?if_exists}"/>
				
      			<table width="60%" border="0" cellspacing="0" cellpadding="0">    
      			<#-- 
        			<tr>
          				<td>&nbsp;</td>
          				<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>${uiLabelMap.ShipmentDate}:</div></td>
          				<td>&nbsp;</td>
          				<td valign='middle'>
	  	  					<div class='tabletext h3'>
            					${shipDate?if_exists}
            				</div>
            				<input class="h2" type="text" name="effectiveDate" id="effectiveDate" value="${shipDate?if_exists}" readonly>       
       					</td>
        			</tr>
        			<tr><td><br/></td></tr> -->
        			<#-- 
        			<tr>
          				<td>&nbsp;</td>
          				<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Return Header Type: </div></td>
          				<td>&nbsp;</td>
       					<td valign='middle'>
            						<input class="h2" type="hidden" size="18" maxlength="20" name="returnHeaderTypeId" id="returnHeaderTypeId" value="${returnHeader.returnHeaderTypeId?if_exists}" readonly/>
       					</td>
        			</tr>
        			<tr><td><br/></td></tr> 
        			<tr>
          				<td>&nbsp;</td>
	          			<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Trip:</div></td>
	          			<td>&nbsp;</td>
          				<td valign='middle'>
       							<div class='tabletext h3'>
            						<input class="h2" type="text" size="18" maxlength="20" name="tripId" id="tripId" value="${tripId?if_exists}" readonly/>
            					</div>
          				</td>
        			</tr>
        			<tr><td><br/></td></tr> -->
       				<tr>
       					<td>&nbsp;</td>
          				<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Route:</div></td>
          				<td>&nbsp;</td>
       	  				<td valign='middle'>
            						<input class="h2" type="text" name="routeId" id="routeId" size="8" value="${parameters.facilityId?if_exists}"   onblur='this.value=this.value.toUpperCase(),setRouteBoothsDropDown(this);' readonly/>
          				</td>
			        </tr>
			        <tr><td><br/></td></tr>
			        <tr>
		       			<td>&nbsp;</td>
		          				<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Vehicle:</div></td>
		          				<td>&nbsp;</td>
		       	  				<td valign='middle'>
		       							<div class='tabletext h3'>
		            						<input class="h2" type="text" size="18" maxlength="20" name="vehicleId" id="vehicleId" value="${parameters.vehicleId?if_exists}" readonly />
		            					</div>
		          				</td>
		          					<td align='left'><span class="tooltipbold" id="vehicleTooltip"></span></td>
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
    	
	</div><!--screen let close -->
	</div><!--left half close -->
	</div>
	
	<div class="righthalf"> 
		<div class="screenlet">
			    	<div class="screenlet-body">
			 				<div class="grid-header" style="width:100%">
								<label> Return Items Entry &nbsp;&nbsp;&nbsp;&nbsp;</label>
							</div>
							<div id="myGrid1"  style="width:100%;height:200px;"></div>
					    	<div align="center">
					    	<table width="60%" border="0" cellspacing="0" cellpadding="0">  
					    	<tr><td></td><td></td></tr>
					    	<tr><td> &nbsp;<input type="button" style="padding:.3em" name="changeSave" id="changeSave" value="Save" /></td>
					    	<td> &nbsp;<input type="button" style="padding:.3em" id="changeCancel" value="Cancel"/> </td></tr>
					    	 
					    	</table>
					    		 
					    	</div>    
					</div>  
		    </div>
    </div>
	</div>           
    </section>
            <h3>CrateReturns</h3>
            <section>
             <div class="lefthalf">
		    <div class="screenlet">
				<div class="grid-header" style="width:100%">
					<label> <h2>Return Crates</h2> &nbsp;&nbsp;&nbsp;&nbsp;</label>
				</div>
		    	<div class="screenlet-body">
					<form name="returnCrate" id="ReturnCrate" action="createReturnCrate">  
		        	<table width="60%" border="0" cellspacing="0" cellpadding="0">     
        			<tr><td><br/></td></tr>
       				<tr>
       					<td>
				            <input type="hidden" name="shipmentId" id="shipmentId" value="${parameters.shipmentId?if_exists}" />
          				</td>
          				<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Route:</div></td>
          				<td>&nbsp;</td>
       	  				<td valign='middle'>
       							<div class='tabletext h3'>
            						<input class="h2" type="text" size="18" maxlength="20" name="routeId" id="routeId" value="${parameters.facilityId?if_exists}" readonly/>
            					</div>
          				</td>
			        </tr>
			        <tr><td><br/></td></tr>
			        <tr>
		       			<td>&nbsp;</td>
		          				<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Vehicle:</div></td>
		          				<td>&nbsp;</td>
		       	  				<td valign='middle'>
		       							<div class='tabletext h3'>
		            						<input class="h2" type="text" size="18" maxlength="20" name="vehicleId" id="vehicleId" value="${parameters.vehicleId?if_exists}" readonly />
		            					</div>
		          				</td>
		          				<td align='left'><span class="tooltipbold" id="vehicleTooltip"></span></td>
			        </tr>
			        <tr><td><br/></td></tr>
			         <tr>
		       			<td>&nbsp;</td>
		          				<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>No.Of Crates Returned:</div></td>
		          				<td>&nbsp;</td>
		       	  				<td valign='middle'>
		       							<div class='tabletext h3'>
		            						<input class="h2" type="text" size="18" maxlength="20" name="crQuantity" id="crQuantity"  />
		            					</div>
		          				</td>
		          				<td align='left'><span class="tooltipbold" id="vehicleTooltip"></span></td>
			        </tr>
			        <tr><td><br/></td></tr>
			         <tr>
		       			<td>&nbsp;</td>
		          				<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>No.Of Cans Returned:</div></td>
		          				<td>&nbsp;</td>
		       	  				<td valign='middle'>
		       							<div class='tabletext h3'>
		            						<input class="h2" type="text" size="18" maxlength="20" name="cnQuantity" id="cnQuantity"  />
		            					</div>
		          				</td>
		          				<td align='left'><span class="tooltipbold" id="vehicleTooltip"></span></td>
			        </tr>
			        <tr><td><br/></td></tr>
		      	</table>
			  </form>
				<br/>
		    	</div><!--screenletBody close -->
		  </div><!--screenlet close -->
		</div><!--left half close -->
                        </section>

                        <h3>Finalize</h3>
                        <section>
             <div class="lefthalf">
		    <div class="screenlet">
				<div class="grid-header" style="width:100%">
					<label> <h2>Finalize And Vehicle StatusChange</h2> &nbsp;&nbsp;&nbsp;&nbsp;</label>
				</div>
		    	<div class="screenlet-body">
					<form name="FinalizeVehicle" id="FinalizeVehicle" method="post" action="FinalizeOrders">  
		        	<table width="60%" border="0" cellspacing="0" cellpadding="0">     
        				<tr><td><br/></td></tr>
       				<tr>
       					<td>
				            <input type="hidden" name="shipmentId" id="shipmentId" value="${parameters.shipmentId?if_exists}" />
          				</td>
          				<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Route:</div></td>
          				<td>&nbsp;</td>
       	  				<td valign='middle'>
       							<div class='tabletext h3'>
            						<input class="h2" type="text" size="18" maxlength="20" name="routeId" id="routeId" value="${parameters.facilityId?if_exists}" readonly/>
            					</div>
          				</td>
			        </tr>
			        <tr><td><br/></td></tr>
			        <tr>
		       			<td>&nbsp;</td>
		          				<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Vehicle:</div></td>
		          				<td>&nbsp;</td>
		       	  				<td valign='middle'>
		       							<div class='tabletext h3'>
		            						<input class="h2" type="text" size="18" maxlength="20" name="vehicleId" id="vehicleId" value="${parameters.vehicleId?if_exists}" readonly />
		            					</div>
		          				</td>
		          				<td align='left'><span class="tooltipbold" id="vehicleTooltip"></span></td>
			        </tr>
			        <tr><td><br/></td></tr>
		      	</table>
			  </form>
				<br/>
		    	</div><!--screenletBody close -->
		  </div><!--screenlet close -->
		</div><!--left half close -->                    
                        </section>
                    </div>
                

 