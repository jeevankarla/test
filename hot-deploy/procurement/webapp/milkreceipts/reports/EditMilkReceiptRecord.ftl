
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
			dateFormat:'dd-mm-yy hh:mm',
			//timeFormat: 'hh:mm:ss',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
			}
		});
}

$(document).ready(function() {	
 
  $('#recdPH').autoNumeric({mNum: 1,mDec: 1 , autoTab : true}).trigger('focusout');
  
  $('#recdTemp').autoNumeric({mNum: 2,mDec: 1 , autoTab : true}).trigger('focusout');
  $('#sendTemp').autoNumeric({mNum: 2,mDec: 1 , autoTab : true}).trigger('focusout');
  
  
  $('#recdAcid').autoNumeric({mNum: 1,mDec: 3 , autoTab : true}).trigger('focusout');
  $('#sendAcid').autoNumeric({mNum: 1,mDec: 3 , autoTab : true}).trigger('focusout');
  
  $('#recdCLR').autoNumeric({mNum: 2,mDec: 1 , autoTab : true}).trigger('focusout');
  $('#sendCLR').autoNumeric({mNum: 2,mDec: 1 , autoTab : true}).trigger('focusout');
  
  $('#recdFat').autoNumeric({mNum: 2,mDec: 1 , autoTab : true}).trigger('focusout');
  $('#sendFat').autoNumeric({mNum: 2,mDec: 1 , autoTab : true}).trigger('focusout');
  
  $('#recdSnf').autoNumeric({mNum: 2,mDec: 3 , autoTab : true}).trigger('focusout');
  $('#sendSnf').autoNumeric({mNum: 2,mDec: 3 , autoTab : true}).trigger('focusout');
  
  $('#recdSnf').attr("readonly","readonly");
  $('#sendSnf').attr("readonly","readonly");
 
 
  
		makeDatePicker("findFromDate","thruDate");
		makeDatePicker("findThruDate","thruDate");
	
		var mccCodeJson = ${StringUtil.wrapString(mccCodeJson)};
		var partyCodeJson = ${StringUtil.wrapString(partyCodeJson)};
		var vehicleCodeJson = ${StringUtil.wrapString(vehicleCodeJson)}
		var productJson = ${StringUtil.wrapString(productJson)}
	
	
	$("#sendFat").blur(function() {
		var action = "getSnfFromLactoReading";
		var fat = $('[name=sendFat]').val();
		var clr = $('[name=sendCLR]').val();
		populateSnf(fat,clr,'sendSnf');
	});
	
	$("#recdFat").blur(function() {
		var action = "getSnfFromLactoReading";
		var fat = $('[name=recdFat]').val();
		var clr = $('[name=recdCLR]').val();
		populateSnf(fat,clr,'recdSnf');
	});
	
	function populateSnf(fat,lr,fieldName){
		var snfQty = 0;
		var dataString = {"fatQty": fat,
						   "lactoReading":lr
						 };
		var action= 'getSnfFromLactoReading';
		$.ajax({
	         type: "POST",
	         url: action,
	         data: dataString,
	         dataType: 'json',
	         success: function(result) { 
	       		snfQty = result['snfQty'];
	       		$('[name='+fieldName+']').val(snfQty);
	         },
	         error: function(XMLHttpRequest, textStatus, errorThrown)
	        {
	        	$('[name='+fieldName+']').val('');
	            alert('Net work failure . Please check network Conection');
	
	        }
    	});
	}
	
	
		
		$("input").keyup(function(e){
		  	 var siloId = $("#siloId").val();
			 if(siloId != null || siloId != undefined){
				siloId = siloId.toUpperCase();
				$("#siloId").val(siloId);
			 }
				
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
		  			populatePartySpan(); 
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
		  		if(e.target.name == "productName"){
		  			$('[name=productName]').val(($('[name=productName]').val()).toUpperCase());
		  			populateProductName();
					var tempProductJson = productJson[$('[name=productName]').val()];
		  			if(tempProductJson){
		  				$('span#productToolTip').addClass("tooltip");
						$('span#productToolTip').removeClass("tooltipWarning");
						productName = tempProductJson["name"];
						$('[name=productId]').val($('[name=productName]').val());
						$('span#productToolTip').html(productName);
					}else{
						$('[name=productId]').val('');	
						$('span#productToolTip').removeClass("tooltip");
						$('span#productToolTip').addClass("tooltipWarning");
						$('span#productToolTip').html('product not found');
					}
		  		}
		  		
		  		if(e.target.name == "vehicleName"){
		  			$('[name=vehicleName]').val(($('[name=vehicleName]').val()).toUpperCase());
		  			populateVehicleName();
					populateVehicleSpan();
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
						source:  availableTags,
						select: function(event, ui) {
					        var selectedValue = ui.item.value;
					        $('[name=partyName]').val(selectedValue);
					        populatePartySpan();
					    }
				});
				
}
function populateVehicleName(){
	var availableTags = ${StringUtil.wrapString(vehItemsJSON)!'[]'};
				$("#vehicleId").autocomplete({					
						source:  availableTags,
						select: function(event, ui) {
					        var selectedValue = ui.item.value;
					        $('[name=vehicleName]').val(selectedValue);
					        populateVehicleSpan();
					    }
				});
				
}
function populateProductName(){
	var availableTags = ${StringUtil.wrapString(productJSON)!'[]'};
				$("#productId").autocomplete({					
						source:  availableTags
				});
				
}

function populateVehicleSpan(){
	var vehicleCodeJson = ${StringUtil.wrapString(vehicleCodeJson)}
	var tempVehJson = vehicleCodeJson[$('[name=vehicleName]').val()];
	if(tempVehJson){
		$('span#vehicleToolTip').addClass("tooltip");
		$('span#vehicleToolTip').removeClass("tooltipWarning");
		var vehicleName = tempVehJson["vehicleName"];
		var vehicleId = tempVehJson["vehicleId"];
		if(!vehicleName){
			vehicleName = vehicleId;
		}
		$('span#vehicleToolTip').html(vehicleName);
		$('[name=tankerNo]').val(vehicleId);
	}else{
		$('[name=tankerNo]').val('');
		$('span#vehicleToolTip').removeClass("tooltip");
		$('span#vehicleToolTip').addClass("tooltipWarning");
		$('span#vehicleToolTip').html('Code not found');
	}
}
function populatePartySpan(){
	var partyCodeJson = ${StringUtil.wrapString(partyCodeJson)}
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
      <h3> Edit Milk Receipt Record</h3>
    </div>
    <div class="screenlet-body">
 		<form method="post" name="editMilkReceiptRecord" id="editMilkReceiptRecord" action="<@ofbizUrl>UpdateEditMilkReceiptRecord</@ofbizUrl>">  
      		<table width="60%" border="0" cellspacing="0" cellpadding="0">     
        	<tr>
			  <td>&nbsp;</td>
	          <td align='left' valign='middle' nowrap="nowrap">
	          	<table>
	          		<tr>
	          			<table>
	  
	          <#if milkTransferEditDetails?has_content>
	          <#list milkDetailslist as editMrRecord>
	          <tr>
             		<td><span class='h3'>Milk TransId  </span></td>
             		<input type="hidden" size="6" maxlength="6" name="milkTransferId" value="${editMrRecord.milkTransferId?if_exists}"/>
             		<input type="hidden" size="6" maxlength="8" name="sequenceNum" value="${editMrRecord.sequenceNum?if_exists}"/>
             	   <td><span class='h3'> <#if editMrRecord.milkTransferId?has_content> ${editMrRecord.milkTransferId}<span/> </#if></td>
             		
               	 </tr>
               	 <#if editMrRecord.receiveDate?has_content>
               	 <tr>
					<td><span class='h3'>TareWeight Date  </span></td>
					<td><span class='h3'> <#if editMrRecord.receiveDate?has_content> ${editMrRecord.receiveDate}<span/> </#if></td>
				</tr>
				</#if>
	            <tr>
             		<td><span class='h3'>Tanker No  </span></td>
             		<td><input type="text" size="10" maxlength="10" name="vehicleName" id="vehicleId" autocomplete="on" <#if editMrRecord.vehicleId?has_content> value="${editMrRecord.vehicleId}" </#if>/><span class="tooltip" id ="vehicleToolTip">none</span></td>
             		<input type="hidden" size="6" maxlength="6" name="vehicleId"/>
               	 </tr>
	         	 <tr>
             		<td><span class='h3'>From Union </span></td>
             		<td><input type="text" size="6" maxlength="6" name="partyName" id="partyId" autocomplete="on" <#if editMrRecord.partyId?has_content> value="${editMrRecord.partyId}" </#if>/><span class="tooltip" id ="partyToolTip">none</span></td>
             		<input type="hidden" size="6" maxlength="6" name="partyId"/>
               	 </tr>
  			   	 <tr>
             		<td><span class='h3'>Product </span></td>
             		<td><input type="text" size="6" maxlength="6" name="productName" id="productId" autocomplete="on" <#if editMrRecord.productId?has_content> value="${editMrRecord.productId}" </#if>/><span class="tooltip" id ="productToolTip">none</span></td>
             			<input type="hidden" size="6" id="productId" maxlength="6" name="productId" autocomplete="off" value="" />
               	 </tr>
               	 <tr>
             		<td><span class='h3'>Silo Id  </span></td>
             		<td><input type="text" size="6" maxlength="6" name="siloId" id="siloId" autocomplete="on" <#if editMrRecord.siloId?has_content> value="${editMrRecord.siloId}" </#if>/></td>
               	 </tr>
               	<#if editMrRecord.receivedQuantity?has_content>
               	  <tr>
             		<td><span class='h3'>Net Qty  </span></td>
             	   <td><span class='h3'> <#if editMrRecord.receivedQuantity?has_content> ${editMrRecord.receivedQuantity}<span/> </#if></td>
               	 </tr>
				</#if>               	 
				 <tr>
             		<td><span class='h3'>Dispatch Weight  </span></td>
             		<td><input type="text" size="6" maxlength="6" name="dispatchWeight" id="dispatchWeight" autocomplete="on" <#if editMrRecord.dispatchWeight?has_content> value="${editMrRecord.dispatchWeight}" </#if>/></td>
             
               	 </tr>
               	 <tr>
             		<td><span class='h3'>Gross Weight  </span></td>
             		<td><input type="text" size="6" maxlength="6" name="grossWeight" id="grossWeight" autocomplete="on" <#if editMrRecord.grossWeight?has_content> value="${editMrRecord.grossWeight}" </#if>/></td>
               	 </tr>
               	 <tr>
             		<td><span class='h3'>Tare Weight  </span></td>
             		<td><input type="text" size="6" maxlength="6" name="tareWeight" id="tareWeight" autocomplete="on" <#if editMrRecord.tareWeight?has_content> value="${editMrRecord.tareWeight}" </#if>/></td>
               	 </tr>
               	 <tr>
             		<td><span class='h1'>QC Details </span></td>
               	 </tr>
               	  <tr>
             		<td>&#160;</td>
               	 </tr> 
               	 <tr>
	        	    <td align='left' ><span class="h1"> Dispatch Quality</span> </td>
				  </tr>
				  	<tr></tr><tr></tr><tr></tr><tr></tr>
				<tr>
		        		<td align='right' ><span class="h3">Temp </span><input  name="sendTemp" size="7pt" maxlength="4" type="text" id="sendTemp" value="${editMrRecord.sendTemparature?if_exists}" autocomplete="off" required/></td>
		        		<td align='left' ><span class="h3"> Acidity% </span><input  name="sendAcid" size="7pt" maxlength="5" type="text" id="sendAcid" value="${editMrRecord.sendAcidity?if_exists}" autocomplete="off" required/></td>
		        		<td align='right' ><span class="h3"> CLR </span><input  name="sendCLR" size="7pt" maxlength="4" type="text" id="sendCLR" value="${editMrRecord.sendLR?if_exists}" autocomplete="off" required/></td>
		        		<td align='left' ><span class="h3"> Fat% </span><input  name="sendFat" size="7pt" maxlength="4" type="text" id="sendFat" value="${editMrRecord.fat?if_exists}" autocomplete="off" required/></td>
		        		<td align='left' ><span class="h3"> Snf% </span><input  name="sendSnf" size="7pt" maxlength="5" type="text" id="sendSnf" value="${editMrRecord.snf?if_exists}" autocomplete="off" required/></td>
		        		<td align='right'><span class="h3"> COB</span><select name="sendCob" required="required" id="sendCob" allow-empty="true">
		        					<#if editMrRecord.sendCob?exists && "Y" == editMrRecord.sendCob>
							             <option value="N" >NEGATIVE</option>
										 <option  value="Y" selected="selected">POSITIVE</option>
									<#else>
		        				         <option value="Y" >POSITIVE</option>
		        						 <option  value="N" selected="selected">NEGATIVE</option>
									</#if>
								</select></td>
		        	</tr>
		       	    <tr>
	        			<td align='left'><span class="h1"> Received Quality</span> </td>
					</tr>
					<tr></tr><tr></tr><tr></tr><tr></tr>
		        	<tr>
		        		<td align='right' ><span class="h3">Temp</span><input  name="recdTemp" size="7pt" maxlength="4" type="text" id="recdTemp" value="${editMrRecord.receivedTemparature?if_exists}" autocomplete="off" required/></td>
		        		<td align='left' > <span class="h3">Acidity% </span><input  name="recdAcid" size="7pt" maxlength="5" type="text" id="recdAcid" value="${editMrRecord.receivedAcidity}" autocomplete="off" required/></td>
		        		<td align='right' ><span class="h3"> CLR </span><input  name="recdCLR" size="7pt" maxlength="4" type="text" id="recdCLR" value="${editMrRecord.receivedLR?if_exists}" autocomplete="off" required/></td>
		        		<td align='left' ><span class="h3"> Fat% </span><input  name="recdFat" size="7pt" maxlength="4" type="text" id="recdFat" value="${editMrRecord.receivedFat?if_exists}" autocomplete="off" required/></td>
		        		<td align='left' ><span class="h3"> Snf% </span><input  name="recdSnf" size="7pt" maxlength="5" type="text" id="recdSnf" value="${editMrRecord.receivedSnf?if_exists}" autocomplete="off" required/></td>
		        		<td align='left' ><span class="h3"> COB</span><select name="recdCob" required="required" id="recdCob" allow-empty="true">
		        				<#if editMrRecord.receivedCob?exists && "Y" == editMrRecord.receivedCob>
							             <option value="N" >NEGATIVE</option>
										 <option  value="Y" selected="selected">POSITIVE</option>
									<#else>
		        				         <option value="Y" >POSITIVE</option>
		        						 <option  value="N" selected="selected">NEGATIVE</option>
									</#if>
								</select></td>						
					</tr>
					<tr></tr><tr></tr><tr></tr><tr></tr>
					<tr>						
		        		<td align='right' ><span class="h2">OT</span>
		        		<select name="recdOrganoLepticTest" required="required" id="recdOrganismTest" allow-empty="true">
		            <#if editMrRecord.recdOrganoLepticTest?exists >
		         	  <option  value="${editMrRecord.recdOrganoLepticTest}" selected="selected">${editMrRecord.recdOrganoLepticTest}</option>
 				    </#if>
 				   <#if editMrRecord.recdOrganoLepticTest?exists && "NORMAL" == editMrRecord.recdOrganoLepticTest>
						<option value="ABNORMAL" >ABNORMAL</option>
					<#else>
		        		<option value="NORMAL" >NORMAL</option>
					</#if>
 							</select></td>
		        		
		        	<td align='left' ><span class="h2"> SedimentTest</span>
		        		<select name="recdSedimentTest" required="required" id="recdSedimentTest" allow-empty="true">
 				   		<#if editMrRecord.receivedSedimentTest?exists && "Y" == editMrRecord.receivedSedimentTest>
							<option value="N" >ABSENT</option>
							 <option  value="Y" selected="selected">PRESENT</option>
						<#else>
		        			<option value="Y" >PRESENT</option>
		        			 <option  value="N" selected="selected">ABSENT</option>
		        			
						</#if>
							</select></td>
		        	</tr>
               	 
			    <tr><td align='right'><span class='h2'><input type="submit"  size="10" value="Add" class="buttontext h1"/></span> </td></tr>      					 
          	</table>
  		</tr>
  		</#list>
	       </#if>
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
				var productJson = ${StringUtil.wrapString(productJson)}
				var tempProductJson = productJson[$('[name=productName]').val()];
				if(tempProductJson){
					$('span#productToolTip').addClass("tooltip");
					$('span#productToolTip').removeClass("tooltipWarning");
					productName = tempProductJson["name"];
					$('[name=productId]').val($('[name=productName]').val());
					$('span#productToolTip').html(productName);
					
				}else{
					$('[name=productId]').val('');	
					$('span#productToolTip').removeClass("tooltip");
					$('span#productToolTip').addClass("tooltipWarning");
					$('span#productToolTip').html('product not found');
				}
	});	
</script>
