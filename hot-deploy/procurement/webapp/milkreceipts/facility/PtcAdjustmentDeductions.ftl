
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
function datetimepick(){
		
 var currentTime = new Date();
 // First Date Of the month 
 var startDateFrom = new Date(currentTime.getFullYear(),currentTime.getMonth(),1);
 // Last Date Of the Month 
 var startDateTo = new Date(currentTime.getFullYear(),currentTime.getMonth() +1,0);
  
 $("#incidentDate").datepicker({
	dateFormat:'dd:mm:yy',
	changeMonth: true,
 });	
			
	$('#ui-datepicker-div').css('clip', 'auto');	
	  }
	
      function numbersOnly(e){
      	var event = e || window.event;
         //alert(event.keyCode);
         if( !(event.keyCode == 8 || event.keyCode == 9 || event.keyCode == 110 || event.keyCode == 190                             // backspace/Tab/"."
          || event.keyCode == 46                              // delete
          || (event.keyCode >= 35 && event.keyCode <= 40)     // arrow keys/home/end
          || (event.keyCode >= 48 && event.keyCode <= 57)     // numbers on keyboard
          || (event.keyCode >= 96 && event.keyCode <= 105))   // number on keypad
         ) {
           event.preventDefault();     // Prevent character input
    		}
      }
	$(document).ready(function() {	
		//makeDatePicker("incidentDate",f);
		//makeDatePicker("findThruDate","thruDate");
        datetimepick();
        numbersOnly();
		$('#ui-datepicker-div').css('clip', 'auto');

		var vehicleCodeJson = ${StringUtil.wrapString(vehicleCodeJson)};
		var mccCodeJson = ${StringUtil.wrapString(mccCodeJson)};
		var partyCodeJson = ${StringUtil.wrapString(partyCodeJson)};
		$("input").keyup(function(e){

                if(e.target.name == "tankerName"){
	  			
	  			$('[name=tankerName]').val(($('[name=tankerName]').val()).toUpperCase());
	  			populateVehicleName();
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
	  				$('[name=tankerNo]').val(vehicleId);
	  				
	  				fetchTankerRecordNumber();
	  				
	  			}else{
	  				$('[name=tankerNo]').val('');
	  				$('span#tankerToolTip').removeClass("tooltip");
	  				$('span#tankerToolTip').addClass("tooltipWarning");
	  				$('span#tankerToolTip').html('Code not found');
	  			}
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
		}); 
		
	});
function fetchTankerRecordNumber(){
	var action = "getTankerRecordNumber";
	var tankerNo = $('[name=tankerNo]').val();
	var dataString = {"tankerNo": tankerNo};
	var displayScreen = $('[name="displayScreen"]').val();
	$.ajax({
         type: "POST",
         url: action,
         data: dataString,
         dataType: 'json',
         success: function(result) { 
           if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){    
           			$('span#tankerIdToolTip').removeClass("tooltip");
	  				$('span#tankerIdToolTip').addClass("tooltipWarning");
	  				$('span#tankerIdToolTip').html('none');
	  				
	  				
	  				$('span#partyIdFromToolTip').removeClass("tooltip");
	  				$('span#partyIdFromToolTip').addClass("tooltipWarning");
	  				$('span#partyIdFromToolTip').html('none');
	  				           	   
           }else{
           		var  milkTransferId= result['milkTransferId'];
           		var partyId = result['partyId'];
           		partyCodeJson = ${StringUtil.wrapString(partyCodeJson)}
           		var tempPartyJson = partyCodeJson[''+partyId];
           		var partyName ;
               	if(tempPartyJson){	
               		partyName = tempPartyJson["partyName"];
	  				if(!partyName){
	  					partyName = partyId;
	  				}
	  				partyName = partyName+' ['+partyId+']'
	  			}	
           		if(milkTransferId){
           			$('[name = milkTransferId]').val(milkTransferId);
           			if(displayScreen != "VEHICLE_IN"){
           				$('span#tankerIdToolTip').addClass("tooltip");
	  					$('span#tankerIdToolTip').removeClass("tooltipWarning");
	  					$('span#tankerIdToolTip').html(milkTransferId);
	  					
	  					$('span#partyIdFromToolTip').addClass("tooltip");
	  					$('span#partyIdFromToolTip').removeClass("tooltipWarning");
	  					$('span#partyIdFromToolTip').html(partyName);
           			}
	  				
	  				
	  			}else{
	  				$('span#tankerIdToolTip').removeClass("tooltip");
	  				$('span#tankerIdToolTip').addClass("tooltipWarning");
	  				$('span#tankerIdToolTip').html('none');
	  				
	  				$('span#partyIdFromToolTip').removeClass("tooltip");
	  				$('span#partyIdFromToolTip').addClass("tooltipWarning");
	  				$('span#partyIdFromToolTip').html('none');
	  			}
           		
           }
           
         },
          error: function() {
        	 		$('span#tankerIdToolTip').removeClass("tooltip");
	  				$('span#tankerIdToolTip').addClass("tooltipWarning");
	  				$('span#tankerIdToolTip').html('none');
        	 } 
          
    });
}
	
	
function populatePartyName(){
	var availableTags = ${StringUtil.wrapString(partyItemsJSON)!'[]'};
				$("#partyId").autocomplete({					
						source:  availableTags
				});
				
}
function populateVehicleName(){
	var availableTags = ${StringUtil.wrapString(vehItemsJSON)!'[]'};
				$("#tankerNo").autocomplete({					
						source:  availableTags
				});
}

</script>

    <div>
    <#if flag?has_content && flag=="ADDITIONS">
 		<form method="post" name="AddAddition" id="AddAddition" action="<@ofbizUrl>AddPtcAddition</@ofbizUrl>"> 
    </#if>
    <#if flag?has_content && flag=="DEDUCTIONS">
 		<form method="post" name="AddDeduction" id="AddDeduction" action="<@ofbizUrl>AddPtcDeduction</@ofbizUrl>"> 
    </#if> 
    <#if flag?has_content && flag=="Find">
 		<form method="post" name="AddDeduction" id="AddDeduction" action="<@ofbizUrl>FindAdjustments</@ofbizUrl>"> 
    </#if>
      		<table class="basic-table hover-bar h3" widht='80%' style="border-spacing: 0 10px;" border="1">    
        	<tr>
			  <td>&nbsp;</td>
	          <td align='left' valign='middle' nowrap="nowrap">
	          	<table>
	          		<tr>
	          			<table>
	          			    <#if flag?has_content && flag=="Find">
                              <tr>
                               <td align='left'>Recovery Id </td><td>:
                               		<input type="text" name="recoveryId" id="recoveryId" size="10pt" />
                               </td>
                              <tr/>
                              <tr>
					        	<td align='left'>Vehicle No </td><td>:
					        		<input  name="tankerName" size="10pt" type="text" id="tankerNo"  autocomplete="off" /><span class="tooltip h6" id ="tankerToolTip">none</span></td>
					        		<input  name="tankerNo" size="15pt" type="hidden"   autocomplete="off" required/></td>
					        	</td>
					        </tr>
					        <tr><td align='left'>Incident Date</td><td>:
								<input type="text" name="incidentDate" id="incidentDate" size="10pt" onclick="datetimepick()"  /></td>
							</tr>
                             <#else>  
	          				<tr>
					        	<td align='left'>Vehicle No </td><td>:
					        		<input  name="tankerName" size="10pt" type="text" id="tankerNo"  autocomplete="off" required="required"/><span class="tooltip h6" id ="tankerToolTip">none</span></td>
					        		<input  name="tankerNo" size="15pt" type="hidden"   autocomplete="off" required/></td>
					        	</td>
					        </tr>
					        
							<tr><td align='left'>Incident Date</td><td>:
								<input type="text" name="incidentDate" id="incidentDate" size="10pt" onclick="datetimepick()"  required="required"/></td>
							</tr>
                            </#if>
                         	<tr>
		                         <td><span>Custom Time PeriodId</span></td><td>: 
									<select name="customTimePeriodId" id="customTimePeriodId">
                                     <#if flag?has_content && flag=="Find">
                                      	<option></option>
                                     </#if>
									<#list customTimeList as timePeriod>				
										<option value='${timePeriod.customTimePeriodId}'>${timePeriod.fromDate} - ${timePeriod.thruDate} </option>
									</#list></select>	
		                         </td>
                   			</tr>
                   			<#assign index=0>
                           <#if flag?has_content && (flag=="DEDUCTIONS" || flag=="ADDITIONS")>
							  <#list AdjustmentTypeList as Adjustment>
							<tr>
				         		<td>
				         			<div >${Adjustment.description?if_exists} </div><input name="adjustmentTypeId_o_${index}" value="${Adjustment.enumId}" type="hidden" size="10"/></input>
				         		</td>
				         		<td>:
				         			<input type="text" size="10" maxlength="15" name="amount_o_${index}" onkeydown="numbersOnly()"/></input>
				         		</td>
         					</tr> 
                            <#assign index=index+1>
							</#list>
                           <tr><td></td><td align='left'><span class='h2'><input type="submit"  size="15" value="Add" class="buttontext h1"/></span> </td></tr>
                          <#else>
                           <tr>
                                <td>Adjustment Type </td>
                                <td>:
                                  <select name="adjustmentTypeId" id="adjustmentTypeId">
                                       <option></option>
                                      <#list AdjustmentTypeList as Adjustment>
                                         <option value="${Adjustment.enumId}">${Adjustment.description?if_exists}</option>
                                       </#list>
                                  </select>
                                </td>
                           </tr>
                           <tr><td></td><td align='left'><span class='h2'>
                            <input type="submit"  size="15" value="Find" class="buttontext h1"/></span> </td></tr>
						  </#if>
						          					 
                      	</table>
	          		</tr>
	          	</table>
	         </td>  
	       </tr>
      	</table>
	</form>
   </div>
