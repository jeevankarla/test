
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/css/smoothness/jquery-ui-1.8.5.custom.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/examples/examples.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.columnpicker.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<style type="text/css">
	.cell-title {
		font-weight: normal;
	}
	.cell-effort-driven {
		text-align: center;
	}
	
	.tooltip { /* tooltip style */
    background-color: #ffffbb;
    border: 0.1em solid #999999;
    color: #000000;
    font-style: arial;
    font-size: 110%;
    font-weight: normal;
    margin: 0.4em;
    padding: 0.1em;
}
.tooltipWarning { /* tooltipWarning style */
    background-color: #ffffff;
    border: 0.1em solid #FF0000;
    color: #FF0000;
    font-style: arial;
    font-size: 80%;
    font-weight: bold;
    margin: 0.4em;
    padding: 0.1em;
}	

.messageStr {
    background:#e5f7e3;
    background-position:7px 7px;
    border:4px solid #c5e1c8;
    font-weight:700;
    color:#005e20;    
    text-transform:uppercase;
}

</style>			
			
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/validate/jquery.validate.js</@ofbizContentUrl>"></script>

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/autoNumeric/autoNumeric-1.6.2.js</@ofbizContentUrl>"></script>

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/validate/jquery.validate.js</@ofbizContentUrl>"></script>

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/autoTab/jquery.autotab-1.1b.js</@ofbizContentUrl>"></script>


<script type="application/javascript">
function makeDatePicker(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat:'dd-mm-yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
			}
		});
}

	$(document).ready(function() {	
		makeDatePicker("findFromDate","thruDate");
		makeDatePicker("findThruDate","thruDate");
	
		var vehicleCodeJson = ${StringUtil.wrapString(vehicleCodeJson)}
		$("input").keyup(function(e){
		  		if(e.target.name == "tankerName"){
	  			$('[name=tankerName]').val(($('[name=tankerName]').val()).toUpperCase());
	  			populateVehicleName();
				populateVehicleSpan();
	  			
	  			}
				if(e.target.name == "productId"){
	  				$('[name=productId]').val(($('[name=productId]').val()).toUpperCase());
	  			}
	  			if(e.target.name == "partyId"){
	  				$('[name=partyId]').val(($('[name=partyId]').val()).toUpperCase());
	  			}
		}); 
		
	});
function populateVehicleSpan(){
	var vehicleCodeJson = ${StringUtil.wrapString(vehicleCodeJson)};
	var tempVehJson = vehicleCodeJson[$('[name=tankerName]').val()];
	if(tempVehJson){
		$('span#tankerToolTip').addClass("tooltip");
		$('span#tankerToolTip').removeClass("tooltipWarning");
		var vehicleName = tempVehJson["vehicleName"];
		var vehicleId = tempVehJson["vehicleId"];
		if(!vehicleName){
			vehicleName = vehicleId;
		}
		$('span#tankerToolTip').html(vehicleName);
		//$('[name=tankerNo]').val(vehicleId);
		
		//fetchWeighmentDetails();
		
	}else{
		var newTankerName=$('[name=tankerName]').val();
		$('span#tankerToolTip').addClass("tooltip");
		$('span#tankerToolTip').removeClass("tooltipWarning");
		$('span#tankerToolTip').html(newTankerName);
	}

}
function populateVehicleName(){
	var availableTags = ${StringUtil.wrapString(vehItemsJSON)!'[]'};
				$("#tankerName").autocomplete({					
						source:  availableTags,
						select: function(event, ui) {
					        var selectedValue = ui.item.value;
					        $('[name=tankerName]').val(selectedValue);
					        populateVehicleSpan();
					    }
				});
}



</script>
<div class="screenlet">
	 <div class="screenlet-title-bar">
      <h3>Find Non Milk Receipts</h3>
    </div>
    <div class="screenlet-body">
 		<form method="post" name="findNoNMilkReceipts" id="findNoNMilkReceipts" action="<@ofbizUrl>findNoNMilkReceipts</@ofbizUrl>">  
      		<table width="60%" border="0" cellspacing="0" cellpadding="0">     
        	<tr>
			  <td>&nbsp;</td>
	          <td align='left' valign='middle' nowrap="nowrap">
	          	<table>
	          		<tr>
	          			<table>
	          				<tr>
	          				<td><span class='h3'>Record No: </span></td>
	        					<td><input  size="12" type="text" id="weighmentId" name="weighmentId" <#if weighmentId?has_content> value="${weighmentId}" </#if>/></td>
	        				</tr>
                         	 <tr>
                         		<td><span class='h3'>From Party : </span></td>
                         		<td><@htmlTemplate.lookupField  formName="findNoNMilkReceipts" name="partyId" id="partyId"  fieldFormName="LookupPartyName"  />
                               <input type="hidden" size="6" maxlength="6" name="hideSearch" value="N"/>    
                                </td>
		                   	 </tr>
		                   	 <tr>
                         		<td><span class='h3'>Vehicle : </span></td>
                         		<td>
								<input  name="tankerName"  size="12pt" type="text" id="tankerName"  autocomplete="off"  onblur="javascript:"populateVehicleSpan();" <#if vehicleId?has_content> value="${vehicleId}" </#if> /><span class="tooltip h4" id ="tankerToolTip">none</span>
                                <input type="hidden" size="6" maxlength="6" name="hideSearch" value="N"/>
                                </td>
		                   	 </tr>
        					<tr>	        	
					        	<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Product :</td>
					        	<td>
			                       <@htmlTemplate.lookupField name="productId" id="productId" formName="findNoNMilkReceipts" fieldFormName="LookupProduct"/>
       						    </td>
        					</tr>
	        				<tr>
	        					<td><span class='h3'>From Date: </span></td>
	        					<td><input  size="12" type="text" id="findFromDate" name="fromDate" <#if shiftDate?has_content> value="${shiftDate}" </#if>/></td>
	        				</tr>
	        				<tr>
	        					<td align='left' ><span class='h3'> Shift:</span></td>
	        		     		<td>
			                       <select name="shiftId" id="shiftId">  
			        				 <option value=""></option>
			    					 <#if allShiftsList?has_content>
						                <#list allShiftsList as shiftDetails>
						                	<#if !shiftId?exists>    
							                  	<#assign isDefault = false>                  
						                    </#if>
						                    	<#if shiftId?exists && shiftId == shiftDetails.shiftTypeId>
						      						<option  value='${shiftId}' selected="selected">${shiftDetails.description}</option>
						      				    <#else>
						      				 <option value='${shiftDetails.shiftTypeId}'<#if isDefault> selected="selected"</#if>>
						                    		${shiftDetails.description}
						                	  </option>
						                	  </#if>	
						      			  </#list>
		       					      </#if>
							       </select> 
       						    </td>
	        				</tr>
						    	  
						    <tr><td align='right'><span class='h2'><input type="submit"  size="10" value="Find" class="buttontext h1"/></span> </td></tr>      					 
                      	</table>
	          		</tr>
	          	</table>
	         </td>  
	       </tr>
      	</table>
	</form>
   </div>
</div>  

