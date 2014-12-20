<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.grid.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.pager.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
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
			
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/firebugx.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/jquery-1.4.3.min.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/jquery-ui-1.8.5.custom.min.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.core.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.editors.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/plugins/slick.cellrangedecorator.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/plugins/slick.cellrangeselector.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/plugins/slick.cellselectionmodel.js</@ofbizContentUrl>"></script>		
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.groupitemmetadataprovider.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.dataview.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.pager.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.columnpicker.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/validate/jquery.validate.js</@ofbizContentUrl>"></script>

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/autoNumeric/autoNumeric-1.6.2.js</@ofbizContentUrl>"></script>

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/validate/jquery.validate.js</@ofbizContentUrl>"></script>

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/autoTab/jquery.autotab-1.1b.js</@ofbizContentUrl>"></script>

<script type="application/javascript">
	jQuery(function($) {
	var dispatchDateFormat;
	if($('#displayScreen').val()=="VEHICLE_IN"){
		$('#displayRecord').hide();
		dispatchDateFormat = $('#sendDate').datepicker( "option", "dateFormat" );
		
		//$('#dispatchDate').datepicker().datepicker('setDate', new Date()); 
		$('#sendDate').datepicker( "option", "dateFormat", "dd-mm-yy" );
		$('#entryDate').datepicker( "option", "dateFormat", "dd-mm-yy" );
	}
	if($('#displayScreen').val()=="VEHICLE_OUT"){
		dispatchDateFormat = $('#exitDate').datepicker( "option", "dateFormat" );
		$('#exitDate').datepicker( "option", "dateFormat", "dd-mm-yy" );
	}
	if($('#displayScreen').val()=="VEHICLE_TAREWEIGHT"){
		dispatchDateFormat = $('#tareDate').datepicker( "option", "dateFormat" );
		$('#tareDate').datepicker( "option", "dateFormat", "dd-mm-yy" );
	}
	if($('#displayScreen').val()=="VEHICLE_GRSWEIGHT"){
		dispatchDateFormat = $('#grossDate').datepicker( "option", "dateFormat" );
		$('#grossDate').datepicker( "option", "dateFormat", "dd-mm-yy" );
	}
	if($('#displayScreen').val()=="VEHICLE_QC"){
		dispatchDateFormat = $('#testDate').datepicker( "option", "dateFormat" );
		$('#testDate').datepicker( "option", "dateFormat", "dd-mm-yy" );
	}
});
var productJson = ${StringUtil.wrapString(productJson)}
$(document).ready(function() {	
  $('#recdPH').autoNumeric({mNum: 1,mDec: 1 , autoTab : true}).trigger('focusout');
  
  $('#recdTemp').autoNumeric({mNum: 2,mDec: 1 , autoTab : true}).trigger('focusout');
  $('#sendTemp').autoNumeric({mNum: 2,mDec: 1 , autoTab : true}).trigger('focusout');
  
  
  $('#recdAcid').autoNumeric({mNum: 3,mDec: 1 , autoTab : true}).trigger('focusout');
  $('#sendAcid').autoNumeric({mNum: 3,mDec: 1 , autoTab : true}).trigger('focusout');
  
  $('#recdCLR').autoNumeric({mNum: 2,mDec: 1 , autoTab : true}).trigger('focusout');
  $('#sendCLR').autoNumeric({mNum: 2,mDec: 1 , autoTab : true}).trigger('focusout');
  
  $('#recdFat').autoNumeric({mNum: 2,mDec: 1 , autoTab : true}).trigger('focusout');
  $('#sendFat').autoNumeric({mNum: 2,mDec: 1 , autoTab : true}).trigger('focusout');
  
  $('#recdSnf').autoNumeric({mNum: 2,mDec: 2 , autoTab : true}).trigger('focusout');
  $('#sendSnf').autoNumeric({mNum: 2,mDec: 2 , autoTab : true}).trigger('focusout');
  
  $('#tareWeight').autoNumeric({mDec: 2 ,mRound: 'A' ,	aSep:'' , autoTab : true}).trigger('focusout');
  $('#grossWeight').autoNumeric({mDec: 2 ,mRound: 'A' ,	aSep:'' , autoTab : true}).trigger('focusout');
  $('#dispatchWeight').autoNumeric({mDec: 2 ,mRound: 'A' ,	aSep:'' , autoTab : true}).trigger('focusout');
 
   //$(':input').autotab_magic();	
	
	//$('#fcQtyLtrs').autotab({ target: 'fcAckQtyLtrs'});
	//$('#fcAckQtyLtrs').autotab({ target: 'fcFat',previous: 'fcQtyLtrs'});
	//$('#fcFat').autotab({ target: 'fcAckFat', previous: 'fcAckQtyLtrs'});

	if($('#displayScreen').val()=="VEHICLE_IN"){
 		makeDatePicker1("sendDate","fromDate");
 		makeDatePicker("entryDate","fromDate");
 	}
 	if($('#displayScreen').val()=="VEHICLE_OUT"){
 		makeDatePicker("exitDate","fromDate");
 	}
 	if($('#displayScreen').val()=="VEHICLE_TAREWEIGHT"){
 		makeDatePicker("tareDate","fromDate");
 	}
 	if($('#displayScreen').val()=="VEHICLE_GRSWEIGHT"){
 		makeDatePicker("grossDate","fromDate");
 	}
 	if($('#displayScreen').val()=="VEHICLE_QC"){
 		makeDatePicker("testDate","fromDate");
 	}
 	
	$('#ui-datepicker-div').css('clip', 'auto');
	
	var mccCodeJson = ${StringUtil.wrapString(mccCodeJson)}
	var vehicleCodeJson = ${StringUtil.wrapString(vehicleCodeJson)}
	var partyCodeJson = ${StringUtil.wrapString(partyCodeJson)}
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
	  		
	  		if(e.target.name == "mccCode" ){
				populateMccNames();
				var tempUnitJson = mccCodeJson[$('[name=mccCode]').val()];
	  			if(tempUnitJson){
	  				$('span#unitToolTip').addClass("tooltip");
	  				$('span#unitToolTip').removeClass("tooltipWarning");
	  				unitName = tempUnitJson["name"];
	  				unitId = tempUnitJson["facilityId"];
	  				showQtyKgs = tempUnitJson["showQtyKgs"];
	  				$('span#unitToolTip').html(unitName);
	  				$('[name=facilityId]').val(unitId);
	  				$('[name=qtyKgsFlag]').val(showQtyKgs);
	  			}else{
	  				$('[name=facilityId]').val('');
	  				$('span#unitToolTip').removeClass("tooltip");
	  				$('span#unitToolTip').addClass("tooltipWarning");
	  				$('span#unitToolTip').html('Code not found');
	  			}	  			
	  		}
	  		
	  		if(e.target.name == "product" ){
	  			populateProductNames();
				var tempProductJson = productJson[$('[name=product]').val()];
	  			if(tempProductJson){
	  				$('span#productToolTip').addClass("tooltip");
	  				$('span#productToolTip').removeClass("tooltipWarning");
	  				productName = tempProductJson["name"];
	  				$('[name=productId]').val($('[name=product]').val());
	  				$('span#productToolTip').html(productName);
	  				
	  			}else{
	  				$('[name=productId]').val('');	
	  				$('span#productToolTip').removeClass("tooltip");
	  				$('span#productToolTip').addClass("tooltipWarning");
	  				$('span#productToolTip').html('product not found');
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

function populateProductNames(){
	var availableTags = ${StringUtil.wrapString(productItemsJSON)!'[]'};
		$("#product").autocomplete({					
			source:  availableTags
		});
}
function populateVehicleName(){
	var availableTags = ${StringUtil.wrapString(vehItemsJSON)!'[]'};
				$("#tankerNo").autocomplete({					
						source:  availableTags
				});
}
function populatePartyName(){
	var availableTags = ${StringUtil.wrapString(partyItemsJSON)!'[]'};
				$("#partyId").autocomplete({					
						source:  availableTags
				});
}
function populateMccNames(){
	var availableTags = ${StringUtil.wrapString(mccItemsJSON)!'[]'};
		$("#mccCode").autocomplete({					
			source:  availableTags
		});
		
}

function getProductJson(){
	var tempProductJson = productJson[$('[name=product]').val()];
	return tempProductJson;
}

function makeDatePicker(fromDateId ,thruDateId){
$( "#"+fromDateId ).datepicker({
		dateFormat:'dd-mm-yy',
		changeMonth: true,
		numberOfMonths: 1,
		maxDate:fromDateId,
		onSelect: function( selectedDate ) {
			$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
		}
	});
	$("#"+fromDateId).datepicker().datepicker('setDate', new Date())
}
function makeDatePicker1(fromDateId ,thruDateId){
$( "#"+fromDateId ).datepicker({
		dateFormat:'dd-mm-yy',
		changeMonth: true,
		numberOfMonths: 1,
		maxDate:fromDateId,
		onSelect: function( selectedDate ) {
			$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
		}
	});
}
	
	
</script>
<div id="wrapper" style="width: 90%; height:100%"></div>
<div name ="displayMsg" id="milkReceiptEntry_spinner"> </div>
<div style="float: left;width: 90%; background:transparent;border: #F97103 solid 0.1em; valign:middle">
	
	<div class="screenlet" background:transparent;border: #F97103 solid 0.1em;>      
      <div class="grid-header h2" style="width:100%">
      			<#assign velhicleStatus = "VEHICLE ENTRY DETAILS">
      			<#if displayScreen == "VEHICLE_OUT">
      				<#assign velhicleStatus = " VEHICLE OUT DETAILS">
      			</#if>
      			<#if displayScreen == "VEHICLE_GRSWEIGHT">
      				<#assign velhicleStatus = "VEHICLE  GROSS WEIGHT DETAILS">
      			</#if>
      			<#if displayScreen == "VEHICLE_TAREWEIGHT">
      				<#assign velhicleStatus = "VEHICLE  TARE WEIGHT DETAILS">
      			</#if>
      			<#if displayScreen == "VEHICLE_QC">
      				<#assign velhicleStatus = "QUALITY CONTROL DETAILS">
      			</#if>
				<label>${velhicleStatus}</label>
	  		</div>
    </div>
	<div class="screenlet-body">
		<#assign setTime = (Static["org.ofbiz.base.util.UtilDateTime"].toDateString(Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp(), "HH:mm")).replace(':','')>
    	<form method="post" name="milkReceiptEntry"  id="milkReceiptEntry" >
      	<table class="basic-table hover-bar h3" widht='80%' style="border-spacing: 0 10px;" border="1">     
	        <tr>
			  <td>&nbsp;</td>
	          <td align='left' valign='middle' nowrap="nowrap">
	          <table>
	          		<tr>
	          			<table>
	          				<tr>
					        	<td align='left'>Vehicle No </td><td>
					        		<input  name="tankerName" size="10pt" type="text" id="tankerNo"  autocomplete="off" required="required"/><span class="tooltip h2" id ="tankerToolTip">none</span></td>
					        		<input  name="tankerNo" size="10pt" type="hidden"   autocomplete="off" required/></td>
					        		<input  name="milkTransferId" size="10pt" type="hidden"   autocomplete="off"/></td>
					        		<input  name="displayScreen" size="10pt" type="hidden" id="displayScreen" value="${displayScreen}" /> 
					        	
					        	</td>
					        		<td id="displayRecord" align ="right">Record Number : <span class="tooltip h1" id ="tankerIdToolTip">none</span> </td>
					        </tr>
					        <#if displayScreen !="VEHICLE_IN">
					        	<tr>
					        			<td id="displayRecord" align ="left">Received From :</td><td> <span class="tooltip h1" id ="partyIdFromToolTip">none</span> </td>
					        	</tr>		
					        </#if>
					        <#if displayScreen == "VEHICLE_IN">
		                        <tr>
	        						<td align='left' ><span class='h3'>Entry Date</span></td><td><input  type="text" size="15pt" id="entryDate" name="entryDate" autocomplete="off" /></td>
	        					</tr>
	        					<tr>
	        						<td align='left' >Entry Time </td><td><input  name="entryTime" size="10" class="onlyNumber" maxlength="4" type="text" id="exitTime" value="${setTime}" autocomplete="off" required/></td>
					        	</tr>
		                         	<tr>
			        					<td align='left' ><span class='h3'>Dispatch Date</span></td><td><input  type="text" size="15pt" id="sendDate" name="sendDate" autocomplete="off" required/></td>
			        					
			        				</tr>
			        				<tr>
			        					<td align='left' >Dispatch Time </td><td><input  name="sendTime"  size="10" class="onlyNumber" maxlength="4" type="text" id="sendTime" autocomplete="off" required/>
			        					</td>
							        </tr>
							        <tr>
								       	<td align='left'>DC No </td><td><input  name="dcNo" size="12" maxlength="10" id= "dcNo" type="text" autocomplete="off"  required/><em>*<em></td>
								    </tr> 
		                         	<tr>
		                         		<td><span class='h3'>From:</span></td><td>
		                         		<input type="text" size="6" maxlength="6" name="partyName" id="partyId" autocomplete="on" required="required"/><span class="tooltip" id ="partyToolTip">none</span></td>
		                         		<input type="hidden" size="6" maxlength="6" name="partyId" required="required"/>
		                   			</tr>
		                   			<tr>
		                   				<td><span class='h3'>To:</span></td><td> MOTHER DAIRY<input type="hidden" size="6" id="partyIdTo" maxlength="6" name="partyIdTo" autocomplete="off" value="MD" /></td>
		                   			</tr>
		                   			<tr>
		                   				<td><span class='h3'>Seal check:</span></td><td> <input type="radio" name="sealCheck" id="sealCheckY" value="Y"/> YES   <input type="radio" name="sealCheck" id="sealCheckN" value="N"/> NO</td>
		                   			</tr>
		                   	</#if>		
		                   	<#if displayScreen == "VEHICLE_OUT">
							    
							    <tr>
	        						<input  name="statusId" size="10pt" type="hidden" id="statusId" value="MR_VEHICLE_OUT" />
	        						<td align='left' ><span class='h3'>Exit Date</span></td><td><input  type="text" size="15pt" id="exitDate" name="exitDate" autocomplete="off" /></td>
	        					</tr>
	        					<tr>
	        						<td align='left' >Exit Time </td><td><input  name="exitTime" size="10" class="onlyNumber" maxlength="4" type="text" id="exitTime" value="${setTime}" autocomplete="off" required/></td>
					        	</tr>
					        </#if>
					        <#if displayScreen == "VEHICLE_GRSWEIGHT">	
							    
							    <tr>
	        						<input  name="statusId" size="10pt" type="hidden" id="statusId" value="MR_VEHICLE_GRSWEIGHT" />
	        						<td align='left' >Gross Weight Date</td><td><input  type="text" size="15pt" id="grossDate" name="grossDate" autocomplete="off" /></td>
	        					</tr>
	        					<tr>
	        						<td align='left' >Gross Weight Time </td><td><input  name="grossTime" value="${setTime}" size="10" class="onlyNumber" maxlength="4" type="text" id="grossTime" autocomplete="off" required/></td>
					        	</tr>
							    <tr>
	        						<td align='left' >Number of Compartments </td><td><input  name="numberOfCells"  size="10" class="onlyNumber" maxlength="1" type="text" id="compartments" autocomplete="off" required/></td>
	        					</tr>
							    <tr>
	        						<td align='left' >Dispatch Weight(Kgs)</td><td><input  type="text" size="15pt" id="dispatchWeight" name="dispatchWeight" autocomplete="off" required="required"/></td>
	        					</tr>
							    <tr>
	        						<td align='left' >Gross Weight(Kgs)</td><td><input  type="text" size="15pt" id="grossWeight" name="grossWeight" autocomplete="off" required="required"/></td>
	        					</tr>
	        				</#if>	
	        				<#if displayScreen == "VEHICLE_TAREWEIGHT">
	        					<tr>
	        						<input  name="statusId" size="10pt" type="hidden" id="statusId" value="MR_VEHICLE_TARWEIGHT" />
	        						<td align='left' ><span class='h3'>Tare Weight Date</span></td><td><input  type="text" size="15pt" id="tareDate" name="tareDate" autocomplete="off" /></td>
	        					</tr>
	        					<tr>
	        						<td align='left' >Tare Time </td><td><input  name="tareTime" class="onlyNumber" value="${setTime}" size="10" maxlength="4" type="text" id="tareTime" autocomplete="off" required/></td>
					        	</tr>
							    <tr>
	        						<td align='left' ><span class='h3'>Tare Weight(Kgs)</span></td><td><input  type="text" class="onlyNumber" size="15pt" id="tareWeight" name="tareWeight" autocomplete="off" required/></td>
	        					</tr>
	        				</#if>	
	        				<#if displayScreen == "VEHICLE_QC">
	        					<tr>
	        						<input  name="statusId" size="10pt" type="hidden" id="statusId" value="MR_VEHICLE_QC" />
	        						<td align='left' ><span class='h3'>Testing Date</span></td><td><input  type="text" size="15pt" id="testDate" name="testDate" autocomplete="off" /></td>
	        					</tr>
	        					<tr>
	        						<td align='left' >Testing Time </td><td><input  name="testTime" value="${setTime}" size="10" class="onlyNumber" maxlength="4" type="text" id="testTime" autocomplete="off" required/>
			        					</td>
					        	</tr>
	        					
						        <tr>
	        						<td align='left' ><span class="h2"> Dispatch Quality</span> </td>
					        	</tr>
					        	<tr>
					        		<td align='right' >Temp </td><td><input  name="sendTemp" size="7pt" maxlength="4" type="text" id="sendTemp" autocomplete="off" required/></td>
					        		<td align='left' > Acidity </td><td><input  name="sendAcid" size="7pt" maxlength="5" type="text" id="sendAcid" autocomplete="off" required/></td>
					        		<td align='right' > CLR </td><td><input  name="sendCLR" size="7pt" maxlength="4" type="text" id="sendCLR" autocomplete="off" required/></td>
					        		<td align='left' > Fat </td><td><input  name="sendFat" size="7pt" maxlength="4" type="text" id="sendFat" autocomplete="off" required/></td>
					        		<td align='left' > Snf </td><td><input  name="sendSnf" size="7pt" maxlength="5" type="text" id="sendSnf" autocomplete="off" required/></td>
					        	</tr>
					        	<tr>
					        		<td align='right' > COB(Y/N) </td>
					        			<td> <select name="sendCob" required="required" id="sendCob" allow-empty="true">
					        					<option value="">SELECT</option>
					        					<option value="N">NO</option>
            									<option value="Y">YES</option>
          												</select></td>
					        		<td align='left' > Nutrilisers(+ve/-ve) </td>
					        		<td><select name="sendNutriliser" required="required" id="sendNutriliser" allow-empty="true">
					        					<option value="">SELECT</option>
					        					<option value="N">NEGITIVE</option>
            									<option value="Y">POSITIVE</option>
          												</select></td>
          												
					        	</tr>
					        	
					        	 <tr>
	        						<td align='left'><span class="h2"> Received Quality</span> </td>
					        	</tr>
					        	<tr>
					        		<td align='right' >Temp </td><td><input  name="recdTemp" size="7pt" maxlength="4" type="text" id="recdTemp" autocomplete="off" required/></td>
					        		<td align='left' > Acidity </td><td><input  name="recdAcid" size="7pt" maxlength="5" type="text" id="recdAcid" autocomplete="off" required/></td>
					        		<td align='right' > CLR </td><td ><input  name="recdCLR" size="7pt" maxlength="4" type="text" id="recdCLR" autocomplete="off" required/></td>
					        		<td align='left' > Fat </td><td><input  name="recdFat" size="7pt" maxlength="4" type="text" id="recdFat" autocomplete="off" required/></td>
					        		<td align='left' > Snf </td><td><input  name="recdSnf" size="7pt" maxlength="5" type="text" id="recdSnf" autocomplete="off" required/></td>
					        	</tr>
					        	<tr>
					        		<td align='right' > COB(Y/N) </td>
					        		<td><select name="recdCob" required="required" id="recdCob" allow-empty="true">
					        					<option value="">SELECT</option>
					        					<option value="N">NO</option>
            									<option value="Y">YES</option>
          												</select></td>
					        		<td align='left' > Flavour </td>
					        		<td>
					        		<select name="recdFlavour" required="required" id="recdSedimentTest" allow-empty="true">
					        					<option value="">SELECT</option>
					        					<option value="NORMAL">NORMAL</option>
            									<option value="ABNORMAL">ABNORMAL</option>
          												</select></td>
					        		<td align='left' > SedimentTest(+ve/-ve) </td><td>
					        		<select name="recdSedimentTest" required="required" id="recdSedimentTest" allow-empty="true">
					        					<option value="">SELECT</option>
					        					<option value="N">NEGITIVE</option>
            									<option value="Y">POSITIVE</option>
          												</select></td>
					        		<td align='left' > PH </td><td><input  name="recdPH" size="7pt" maxlength="5" type="text" id="recdPH" autocomplete="off" required/></td>
					        		<td align='left' > MBRT </td><td><input  name="recdMBRT" size="7pt" maxlength="4" type="text" id="recdMBRT" autocomplete="off" required/></td>
					        	</tr>
						    </#if>
	          		</table>
	         	 </td>  
	        </tr>
      </table>
      <table>
      	<tr>
      		<td>&nbsp;</td><td>&nbsp;</td> <td>&nbsp;</td><td>&nbsp;</td>
      		<td>&nbsp;</td><td>&nbsp;</td> <td>&nbsp;</td><td>&nbsp;</td>
      		<td>&nbsp;</td><td>&nbsp;</td> <td>&nbsp;</td><td>&nbsp;</td>
	      	<td valign = "middle" align="center">
	      	<div class='tabletext h1'>
	 			<input type="submit" align="right"  class="button" name="submitButton"  id="submitEntry" <#if displayScreen == "VEHICLE_IN">value="Add"<#else>value="Update"</#if>/>      
	      		</div>
	      	</td>
      	</tr>
      </table>
	       
   </form>
  </div>
 </div>
</div>
