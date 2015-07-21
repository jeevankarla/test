
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
    font-size: 80%;
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
	
		var mccCodeJson = ${StringUtil.wrapString(mccCodeJson)};
		var partyCodeJson = ${StringUtil.wrapString(partyCodeJson)};
		var vehicleCodeJson = ${StringUtil.wrapString(vehicleCodeJson)}
		$("input").keyup(function(e){
		  		if(e.target.name == "mccCode" ){
					var tempUnitJson = mccCodeJson[$('[name=mccCode]').val()];
		  			if(tempUnitJson){
		  				$('span#unitToolTip').addClass("tooltip");
		  				$('span#unitToolTip').removeClass("tooltipWarning");
		  				unitName = tempUnitJson["name"];
		  				unitId = tempUnitJson["facilityId"];
		  				$('span#unitToolTip').html(unitName);
		  				$('[name=facilityId]').val(unitId);
		  			}else{
		  				$('[name=facilityId]').val('');
		  				$('span#unitToolTip').removeClass("tooltip");
		  				$('span#unitToolTip').addClass("tooltipWarning");
		  				$('span#unitToolTip').html('Code not found');
		  			}	  			
		  		}
		  		
			  	if(e.target.name == "partyName"){
		  			$('[name=partyName]').val(($('[name=partyName]').val()).toUpperCase());
		  			populatePartyName();
					var tempPartyJson = partyCodeJson[$('[name=partyName]').val()];
		  			if(tempPartyJson){
		  				$('span#partyToolTip').addClass("tooltip");
		  				$('span#partyToolTip').removeClass("tooltipWarning");
		  				var partyName = tempPartyJson["partyName"];
		  				var partyId = tempPartyJson["partyId"];
		  				if(!partyName){
		  					partyName = partyId;
		  				}
		  				$('span#partyToolTip').html(partyName);
		  				$('[name=partyId]').val(partyId);
		  			}else{
		  				$('[name=partyId]').val('');
		  				$('span#partyToolTip').removeClass("tooltip");
		  				$('span#partyToolTip').addClass("tooltipWarning");
		  				$('span#partyToolTip').html('Code not found');
		  			}
		  		}
		  		if(e.target.name == "vehicleName"){
		  			$('[name=vehicleName]').val(($('[name=vehicleName]').val()).toUpperCase());
		  			populateVehicleName();
					var tempVehicleJson = vehicleCodeJson[$('[name=vehicleName]').val()];
		  			if(tempVehicleJson){
		  				$('span#vehicleToolTip').addClass("tooltip");
		  				$('span#vehicleToolTip').removeClass("tooltipWarning");
		  				var vehicleName = tempVehicleJson["vehicleName"];
		  				var vehicleId = tempVehicleJson["vehicleId"];
		  				if(!vehicleName){
		  					vehicleName = vehicleId;
		  				}
		  				$('span#vehicleToolTip').html(vehicleName);
		  				$('[name=vehicleId]').val(vehicleId);
		  			}else{
		  				$('[name=vehicleId]').val('');
		  				$('span#vehicleToolTip').removeClass("tooltip");
		  				$('span#vehicleToolTip').addClass("tooltipWarning");
		  				$('span#vehicleToolTip').html('Code not found');
		  			}
		  		}
		}); 
		
	});
	
function populatePartyName(){
	var availableTags = ${StringUtil.wrapString(partyItemsJSON)!'[]'};
				$("#partyId").autocomplete({					
						source:  availableTags
				});
				
}
function populateVehicleName(){
	var availableTags = ${StringUtil.wrapString(vehItemsJSON)!'[]'};
				$("#vehicleId").autocomplete({					
						source:  availableTags
				});
				
}

function deleteTransferEntry(thisValue,milkTransferId){	
	var confirmationFlag=false;
	if(confirm('Dou u want to delete this Record?')){	
		confirmationFlag=true;
	}else{
		confirmationFlag=false;
	}	
	if(confirmationFlag){		
			if((milkTransferId !='')){	
				var action = "deleteTransferEntryAjax";
				var dataJson = {"milkTransferId":milkTransferId,													
							   };
				$.ajax({
					 type: "POST",
		             url: action,
		             data: dataJson,
		             dataType: 'json',	            
					success:function(result){
						if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){						
						}else{						
							alert("Sucessfully deleted");						
							$(thisValue).parent().parent().hide();	
						}								 
					},
					error: function(){
						
					}							
				});
			}	
			$(thisValue).parent().parent().hide();		
		}else{
			alert('cancelled');
			return false;
		}	
}

</script>
<div class="screenlet">
	 <div class="screenlet-title-bar">
      <h3>Find Milk Receipts</h3>
    </div>
    <div class="screenlet-body">
    	<#if parameters.flag?has_content && parameters.flag=="APPROVE_RECEIPTS"> 
        <form method="post" name="findMilkReceipts" id="findMilkReceipts" action="<@ofbizUrl>MilkReceiptsToApprove</@ofbizUrl>">
    	<#elseif parameters.flag?has_content && parameters.flag == "FINALIZATION">
        <form method="post" name="findMilkReceipts" id="findMilkReceipts" action="<@ofbizUrl>MilkReceiptFinalization</@ofbizUrl>">
        <#else>
 		<form method="post" name="findMilkReceipts" id="findMilkReceipts" action="<@ofbizUrl>FindMilkReceipt</@ofbizUrl>">  
 		</#if>
      		<table width="60%" border="0" cellspacing="0" cellpadding="0">     
        	<tr>
			  <td>&nbsp;</td>
	          <td align='left' valign='middle' nowrap="nowrap">
	          	<table>
	          		<tr>
	          			<table>
	          				<tr>
	          				<td><span class='h3'>Record No: </span></td>
	        					<td><input  size="12" type="text" id="milkTrnsfId" name="milkTransferId" <#if milkTransferId?has_content> value="${milkTransferId}" </#if>/></td>
	        				</tr>
                         	 <tr>
                         		<td><span class='h3'>From Union: </span></td>
                         		<td><input type="text" size="6" maxlength="6" name="partyName" id="partyId" autocomplete="on" <#if partyId?has_content> value="${partyId}" </#if>/><span class="tooltip" id ="partyToolTip">none</span></td>
                         		<input type="hidden" size="6" maxlength="6" name="partyId"/>
                         		<input type="hidden" size="6" maxlength="6" name="hideSearch" value="N"/>
		                   	 </tr>
		                   	 <tr>
                         		<td><span class='h3'>Vehicle : </span></td>
                         		<td><input type="text" size="6" maxlength="6" name="vehicleName" id="vehicleId" autocomplete="on" <#if vehicleId?has_content> value="${vehicleId}" </#if>/><span class="tooltip" id ="vehicleToolTip">none</span></td>
                         		<input type="hidden" size="6" maxlength="6" name="vehicleId"/>
                         		<input type="hidden" size="6" maxlength="6" name="hideSearch" value="N"/>
		                   	 </tr>
        					<tr>	        	
					        	<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Milk Type :</td>
					        	<td>
			                       <select name="productId" id="productId">  
			        				 <option value=""></option>
			    					 <#if productsList?has_content>
						                <#list productsList as product>
						                	<#if !siloId?exists>    
							                  	<#assign isDefault = false>                  
						                    </#if>
						                    	<#if productId?exists && productId == product.productId>
						      						<option  value="${productId}" selected="selected">${product.description}</option>
						      					<#else>
						      						<option value='${product.productId}'<#if isDefault> selected="selected"</#if>>
						                    			${product.description}
						                  			</option>
    					      					</#if>
						      			  </#list>
		       					      </#if>
							        </select> 
       						    </td>
        					</tr>
        					<tr>
	        					<td align='left' ><span class='h3'> Silo :</span></td>
	        					<td>
			                       <select name="siloId" id="siloId">  
			        				 <option value=""></option>
			    					 <#if rawMilkSilosList?has_content>
						                <#list rawMilkSilosList as rawMilkSilo>
						                	<#if !siloId?exists>    
							                  	<#assign isDefault = false>                  
						                    </#if>
						                    	<#if siloId?exists && siloId == rawMilkSilo.facilityId>
						      						<option  value="${siloId}" selected="selected">${rawMilkSilo.facilityId}</option>
						      						<#else>
						      						<option value='${rawMilkSilo.facilityId}'<#if isDefault> selected="selected"</#if>>
						                    			${rawMilkSilo.facilityId}
						                  			</option>
						      					</#if>
						      						
						      			  </#list>
		       					      </#if>
							       </select> 
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
<script type="application/javascript">
	$(document).ready(function() {
		//var partyId = $("#partyId").val();
		var partyCodeJson = ${StringUtil.wrapString(partyCodeJson)};
		var tempPartyJson = partyCodeJson[$('[name=partyName]').val()];
	  			if(tempPartyJson){
	  				$('span#partyToolTip').addClass("tooltip");
	  				$('span#partyToolTip').removeClass("tooltipWarning");
	  				var partyName = tempPartyJson["partyName"];
	  				var partyId = tempPartyJson["partyId"];
	  				if(!partyName){
	  					partyName = partyId;
	  				}
	  				$('span#partyToolTip').html(partyName);
	  				$('[name=partyId]').val(partyId);
	  			}

	  		 	var vehicleCodeJson = ${StringUtil.wrapString(vehicleCodeJson)};
	  			var tempVehicleJson = vehicleCodeJson[$('[name=vehicleName]').val()];
		  			if(tempVehicleJson){
		  				$('span#vehicleToolTip').addClass("tooltip");
		  				$('span#vehicleToolTip').removeClass("tooltipWarning");
		  				var vehicleName = tempVehicleJson["vehicleName"];
		  				var vehicleId = tempVehicleJson["vehicleId"];
		  				if(!vehicleName){
		  					vehicleName = vehicleId;
		  				}
		  				$('span#vehicleToolTip').html(vehicleName);
		  				$('[name=vehicleId]').val(vehicleId);
		  			}
	});	
</script>
