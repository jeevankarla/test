<script type="text/javascript">
var partyAutoJson = ${StringUtil.wrapString(partyJSON)!'[]'};	

$(document).ready(function(){

		$( "#effectiveDate" ).datepicker({
			dateFormat:'d MM, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#effectiveDate" ).datepicker("option", selectedDate);
			}
		});
		$('#ui-datepicker-div').css('clip', 'auto');
		
		 	$("#partyId").autocomplete({ source: partyAutoJson }).keydown(function(e){ 
			if (e.keyCode === 13){
		      	 $('#partyId').autocomplete('close');
		      	   var partyId=$('[name=partyId]').val();
		      	  if(partyId){
					 	setupGrid1();
					 	//setupGrid2();
				     }
	    
	    			//$('#salesReturnEntryInit').submit();
	    			//return false;   
			}
		});
		
	});
	

 
      

</script>



<#assign changeRowTitle = "Changes">                
<#include "SalesReturnEntryInc.ftl"/>
<div class="full">
  <div class="lefthalf">
	<div class="screenlet">
		<div class="grid-header" style="width:100%">
			<label> <h2>Return Entry</h2> &nbsp;&nbsp;&nbsp;&nbsp;</label>
		</div>
    	<div class="screenlet-body">
			<form name="salesReturnEntryInit" id="salesReturnEntryInit">  
				<input type="hidden" name="screenFlag" id="screenFlag" value="${screenFlag}"/>
				<input type="hidden" name="receiveInventory" id="receiveInventory" value="N"/>
				<input type="hidden" name="productStoreId" id="productStoreId" value="1003"/>
				<input type="hidden" name="returnHeaderTypeId" id="returnHeaderTypeId" value="CUSTOMER_RETURN"/>
      			<table width="60%" border="0" cellspacing="0" cellpadding="0">     
      			
      			<tr><td><br/></td></tr>
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
        			
		<#--	        <tr>
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
        			-->
       				
			        <tr><td><br/></td></tr>
			        <tr>
       					<td>&nbsp;</td>
          				<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>PartyId:</div></td>
          				<td>&nbsp;</td>
       	  				<td valign='middle'>
       	  					<#if returnHeader?has_content>
       							<div class='tabletext h3'>
            						<input class="h2" type="text" size="18" maxlength="20" name="partyId" id="partyId" value="${returnHeader.originFacilityId?if_exists}" readonly/>
            					</div>
       						<#else>
       							<input type="text" name="partyId" id="partyId" size="8" onblur='this.value=this.value.toUpperCase()'/>
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
 		<div class="grid-header" style="width:100%">
			<span id="totalAmount"></span>
		</div>
			<div id="myGrid1" style="width:100%;height:350px;"></div>
		         <#assign formAction='processSalesReturnAction'>
 		   
    	<div align="center">
    		<input type="submit" style="padding:.3em" id="changeSave" value="Submit" onclick="javascript:processSalesReturn('salesReturnEntryInit','<@ofbizUrl>${formAction}</@ofbizUrl>');"/>
    		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    		<input type="submit" style="padding:.3em" id="changeCancel" value="Cancel" onclick="javascript:processSalesReturn('salesReturnEntryInit','<@ofbizUrl>SupplDeliverySchedule</@ofbizUrl>');"/>   	
    	</div>     
	</div>
</div>     
</div>
 	

</div>
  