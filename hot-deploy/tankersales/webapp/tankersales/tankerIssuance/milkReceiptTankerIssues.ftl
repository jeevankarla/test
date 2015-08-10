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
	if($('#displayScreen').val()=="LOADING_DETAILS"){
		//$('#displayRecord').hide();
		//$('#displayRecievedFrom').hide();
		dispatchDateFormat = $('#sendDate').datepicker( "option", "dateFormat" );
		
		//$('#dispatchDate').datepicker().datepicker('setDate', new Date()); 
		$('#sendDate').datepicker( "option", "dateFormat", "dd-mm-yy" );
		//$('#entryDate').datepicker( "option", "dateFormat", "dd-mm-yy" );
	}
	if($('#displayScreen').val()=="ISSUE_CIP"){
		$('#displayRecord').hide();
		$('#displayRecievedFrom').hide();
		//dispatchDateFormat = $('#sealCheckDate').datepicker( "option", "dateFormat" );
		
		//$('#dispatchDate').datepicker().datepicker('setDate', new Date()); 
		//$('#sealCheckDate').datepicker( "option", "dateFormat", "dd-mm-yy" );
		//$('#entryDate').datepicker( "option", "dateFormat", "dd-mm-yy" );
	}
	if($('#displayScreen').val()=="VEHICLE_OUT"){
		dispatchDateFormat = $('#exitDate').datepicker( "option", "dateFormat" );
		$('#exitDate').datepicker( "option", "dateFormat", "dd-mm-yy" );
	}
	if($('#displayScreen').val()=="ISSUE_TARWEIGHT"){
		dispatchDateFormat = $('#tareDate').datepicker( "option", "dateFormat" );
		$('#tareDate').datepicker( "option", "dateFormat", "dd-mm-yy" );
	}
	if($('#displayScreen').val()=="ISSUE_GRSWEIGHT"){
		dispatchDateFormat = $('#grossDate').datepicker( "option", "dateFormat" );
		$('#grossDate').datepicker( "option", "dateFormat", "dd-mm-yy" );
	}
	if($('#displayScreen').val()=="ISSUE_QC"){
		dispatchDateFormat = $('#testDate').datepicker( "option", "dateFormat" );
		$('#testDate').datepicker( "option", "dateFormat", "dd-mm-yy" );
		$('#sendDate').datepicker( "option", "dateFormat", "dd-mm-yy" );
		$('#sealDate').datepicker( "option", "dateFormat", "dd-mm-yy" );
	}
	if($('#displayScreen').val()=="ISSUE_AQC"){
		dispatchDateFormat = $('#testDate').datepicker( "option", "dateFormat" );
		$('#testDate').datepicker( "option", "dateFormat", "dd-mm-yy" );
	}
});
var productJson = ${StringUtil.wrapString(productJson)}
var tareWeight = 0;
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
  
  <#--
  $('#recdSnf').attr("readonly","readonly");
  $('#sendSnf').attr("readonly","readonly");
  -->
  
  $('#tareWeight').autoNumeric({mDec: 2 ,mRound: 'A' ,	aSep:'' , autoTab : true}).trigger('focusout');
  $('#grossWeight').autoNumeric({mDec: 2 ,mRound: 'A' ,	aSep:'' , autoTab : true}).trigger('focusout');
  $('#dispatchWeight').autoNumeric({mDec: 2 ,mRound: 'A' ,	aSep:'' , autoTab : true}).trigger('focusout');
 
   //$(':input').autotab_magic();	
	
	//$('#fcQtyLtrs').autotab({ target: 'fcAckQtyLtrs'});
	//$('#fcAckQtyLtrs').autotab({ target: 'fcFat',previous: 'fcQtyLtrs'});
	//$('#fcFat').autotab({ target: 'fcAckFat', previous: 'fcAckQtyLtrs'});
	
	if($('#displayScreen').val()=="LOADING_DETAILS"){
		makeDatePicker1("sendDate","fromDate");
 		//makeDatePicker("entryDate","fromDate");
	}
	if($('#displayScreen').val()=="ISSUE_CIP"){
 		//makeDatePicker("sealCheckDate","fromDate");
 	}
 	if($('#displayScreen').val()=="VEHICLE_OUT"){
 		$('#dcNo').attr("readonly","readonly");
 		makeDatePicker("exitDate","fromDate");
 	}
 	if($('#displayScreen').val()=="ISSUE_TARWEIGHT"){
 		makeDatePicker("tareDate","fromDate");
 	}
 	if($('#displayScreen').val()=="ISSUE_GRSWEIGHT"){
 		$('#dcNo').attr("readonly","readonly");
 		makeDatePicker("grossDate","fromDate");
 	}
 	if($('#displayScreen').val()=="ISSUE_QC"){
 		$('#dcNo').removeAttr("readonly");
 		$('#sendDate').attr("readonly","readonly");
 		$('#sendTime').attr("readonly","readonly");
 		makeDatePicker("testDate","fromDate");
 		makeDatePicker("sendDate","fromDate");
 		makeDatePicker("sealCheckDate","fromDate");
 	}
 	if($('#displayScreen').val()=="ISSUE_AQC"){
 		$('#dcNo').removeAttr("readonly");
 		$('#sendDate').attr("readonly","readonly");
 		$('#sendTime').attr("readonly","readonly");
 		makeDatePicker("testDate","fromDate");
 	}
 	if($('#displayScreen').val()=="VEHICLE_CIP"){
 		$('#dcNo').attr("readonly","readonly");	
 		makeDatePicker("cipDate","fromDate");
 	}
 	
	$('#ui-datepicker-div').css('clip', 'auto');
	
	var vehicleCodeJson = ${StringUtil.wrapString(vehicleCodeJson)} ;
	var partyCodeJson = ${StringUtil.wrapString(partyCodeJson)} ;
	
	<#--
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
	-->
	$("input").keyup(function(e){
	  		
	  		if(e.target.name == "orderId"){
	  			$('[name=orderId]').val(($('[name=orderId]').val()).toUpperCase());
	  			populateOrderName();
	  		}
	  		if(e.target.name == "routeId"){
	  			$('[name=routeId]').val(($('[name=routeId]').val()).toUpperCase());
	  			populateRouteName();
	  		}
	  		if(e.target.name == "grossWeight"){
	  			
	  			var grossWeight = $('[name=grossWeight]').val();
	  			
	  			if(typeof(grossWeight)!= "undefined"){	
	  				var netWeight = grossWeight-tareWeight ;
					$('#netWeightToolTip').val(netWeight);
	  			}else{
					$('#netWeightToolTip').val('0');
	  			}
	  			
	  		}
	  		if(e.target.name == "tankerName"){
	  			$('[name=tankerName]').val(($('[name=tankerName]').val()).toUpperCase());
	  			populateVehicleName();
				populateVehicleSpan();
	  		}
	  		if(e.target.name == "extTankerName"){
	  			$('[name=extTankerName]').val(($('[name=extTankerName]').val()).toUpperCase());
	  			populateExtVehicleName();
				populateExtVehicleSpan();
	  		}
	  		
	  		if(e.target.name == "partyName"){
	  			$('[name=partyName]').val(($('[name=partyName]').val()).toUpperCase());
	  			populatePartyName();
				populatePartySpan();
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
				populateProductSpan();	  			
	  		}
	}); 
});

function populateRouteName(){
	var availableTags = ${StringUtil.wrapString(routeListJSON)!'[]'};
	$("#routeId").autocomplete({					
			source:  availableTags,
			select: function(event, ui) {
		        var selectedValue = ui.item.value;
		        $('[name=routeId]').val(selectedValue);
				populateRouteSpan();
				
		    }
	});
}

function populateRouteSpan(){
	var routeCodeJson = ${StringUtil.wrapString(routesObjectJson)}
	var tempRouteJson = routeCodeJson[$('[name=routeId]').val()];
				
	if(tempRouteJson){
		$('span#routeTooltip').addClass("tooltip");
		$('span#routeTooltip').removeClass("tooltipWarning");
		var routeDescription = tempRouteJson["description"];
		$('[name=routeId]').val(tempRouteJson["routeId"]);
		$('span#routeTooltip').html(routeDescription);
	}else{
		$('[name=routeId]').val('');
		$('span#routeTooltip').removeClass("tooltip");
		$('span#routeTooltip').addClass("tooltipWarning");
		$('span#routeTooltip').html('Code not found');
	}
	
	var productJson = ${StringUtil.wrapString(productJson)}
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

function populateOrderName(){
	var availableTags = ${StringUtil.wrapString(orderItemsJSON)!'[]'};
	$("#orderId").autocomplete({					
			source:  availableTags,
			select: function(event, ui) {
		        var selectedValue = ui.item.value;
		        $('[name=orderId]').val(selectedValue);
		        populateOrderDetails();
		    }
	});
}
function populateOrderDetails(){
	var orderId = $('#orderId').val();
	var action= 'getOrderDetailsAjax';
	var dataString = {"orderId": orderId};
	$.ajax({
         type: "POST",
         url: action,
         data: dataString,
         dataType: 'json',
         success: function(result) { 
       		productsList = result['productsList'];
       		//orderHeaderDetails = result['orderHeader'];
       		shipmentDetails = result['shipmentDetailsMap'];
       		//$('#productId').val(shipmentDetails.productId);
       		//$('#product').val(shipmentDetails.productId);
       		//$('span#productToolTip').html(shipmentDetails.productName);
       		$('span#orderToolTip').html("Ship To: "+shipmentDetails.shipToPartyName);
       		
       		$('span#billToPartyToolTip').html(shipmentDetails.billToPartyName);
       		$('span#partyToolTip').html(shipmentDetails.shipToPartyName);
       		
       		
       		$('#partyIdTo').val(shipmentDetails.shipToParty);
       		$('#billToPartyId').val(shipmentDetails.billToParty);
       		
			var optionList = '';   		
			if (productsList) {		       				        	
	        	for(var i=0 ; i<productsList.length ; i++){
					var innerList=productsList[i];	              			             
	                optionList += "<option value = " + innerList.orderItemSeqId + " >" + innerList.productName + "</option>";          			
	      		}
	      	}		
	      	
	      	$("#orderItemSeqId").html(optionList);
       		
       		
       		
       		
       		
       		//$('[name='+fieldName+']').val(snfQty);
         },
         error: function(XMLHttpRequest, textStatus, errorThrown)
        {
        	$('[name='+fieldName+']').val('');
            alert('Net work failure . Please check network Conection');

        }
	});
}


function populateProductSpan(){
	var productJson = ${StringUtil.wrapString(productJson)}
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
		$('[name=partyIdTo]').val(partyId);
	}else{
		$('[name=partyIdTo]').val('');
		$('span#partyToolTip').removeClass("tooltip");
		$('span#partyToolTip').addClass("tooltipWarning");
		$('span#partyToolTip').html('Code not found');
	}

}
function populateVehicleSpan(){
	var vehicleCodeJson = ${StringUtil.wrapString(vehicleCodeJson)}
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
function populateExtVehicleSpan(){
	var extVehicleCodeJson = ${StringUtil.wrapString(extVehicleCodeJson)}
	var tempExtVehJson = extVehicleCodeJson[$('[name=extTankerName]').val()];
	if(tempExtVehJson){
		$('span#extTankerToolTip').addClass("tooltip");
		$('span#extTankerToolTip').removeClass("tooltipWarning");
		var vehicleName = tempExtVehJson["vehicleName"];
		
		var vehicleId = tempExtVehJson["vehicleId"];
		if(!vehicleName){
			vehicleName = vehicleId;
		}
		$('span#extTankerToolTip').html(vehicleName);
		$('[name=extTankerName]').val(vehicleId);
		
		fetchExtTankerRecordNumber();
		
	}else{
		//$('[name=extTankerNo]').val('');
		$('span#extTankerToolTip').removeClass("tooltip");
		$('span#extTankerToolTip').addClass("tooltipWarning");
		$('span#extTankerToolTip').html('Choose From Suggested Ids or a new vechicle is created');
	}

}

function fetchExtTankerRecordNumber(){
	var action = "getTankerRecordNumberTS";
	var tankerNo = $('#extTankerNo').val();
	var dataString = {"tankerNo": tankerNo};
	var displayScreen = $('[name="displayScreen"]').val();
	$.ajax({
         type: "POST",
         url: action,
         data: dataString,
         dataType: 'json',
         success: function(result) { 
           if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){    
           			
           			var displayScreen = $('[name=displayScreen]').val();
           			$('span#extTankerIdToolTip').removeClass("tooltip");
	  				$('span#extTankerIdToolTip').addClass("tooltipWarning");
	  				$('span#extTankerIdToolTip').html('none');
	  				$('span#partyIdFromToolTip').removeClass("tooltip");
	  				$('span#partyIdFromToolTip').addClass("tooltipWarning");
	  				$('span#partyIdFromToolTip').html('none');
	  				           	   
           }else{
           		var  milkTransferId= result['milkTransferId'];
           		var displayScreen = $('[name=displayScreen]').val();
	   			if(displayScreen == "ISSUE_GRSWEIGHT"){
	   				tareWeight = result['tareWeight'];
	   				$('#tareWeightToolTip').val(tareWeight);
	   			}
	   			if(displayScreen == "ISSUE_TARWEIGHT"){	
           			var isCipCheckedVal = result['isCipChecked'];
	   				 if(isCipCheckedVal == 'Y' && isCipCheckedVal != 'undefined'){
	   				 	$('#isCipChecked').val(isCipCheckedVal);
	   				 	$('#isCipCheckedDes').html("");
	   				 }else{
	   				 	$('#isCipChecked').val('');
	   				    $('#isCipCheckedDes').html("CIP Not Done Please Contact QC Department");
	   				 }
	   				 
	   			}	
	   			if(displayScreen == "ISSUE_AQC"){
	   				
	   				var milkTransfer = result['milkTransfer'];
	   				if(typeof(milkTransfer)!= "undefined"){
	   				  
	   				  $('#sendFat').val(milkTransfer['fat']);
	   				  $('#sendSnf').val(milkTransfer['snf']);
	   				  
	   				  
	   				}
	   				
	   				
	   			}
           		if($('[name=sealCheck]').length !=0){
           			if(displayScreen == "ISSUE_GRSWEIGHT"){
           				var isSealChecked = result['isSealChecked'];
           				if(typeof(isSealChecked)!= "undefined"){
           					if(isSealChecked == 'Y'){
           						$('#sealCheckY').val('Y');
           						$('#sealCheckY').attr('checked', true);
           					}
           					if(isSealChecked == 'N'){
           						$('#sealCheckN').val('N');
           						$('#sealCheckN').attr('checked', true);
           					}
           					
           				}
           			}
           		
           		}
           		
           		
           		if($('[name=product]').length !=0){
	           		var productId = result['productId'];
	           		if(typeof(productId)!= "undefined"){
	           			$('[name=product]').val(productId);
	           			populateProductSpan();
	           		}
           		}
           		if($('[name=dcNo]').length !=0){
           			var dcNo = result['dcNo'];
           			if(typeof(dcNo)!= "undefined"){
           				$('[name=dcNo]').val(dcNo);
           			}
           		
           		}
           		if($('#displayScreen').val() != "RETURN_QC"){
	           		if($('[name=sendDate]').length !=0){
	           			var sendDate = result['sendDateStr'];
	           			if(typeof(sendDate)!= "undefined"){
	           				$('[name=sendDate]').val(sendDate);
	           			}
	           		
	           		}
           		}
           		if($('[name=sendTime]').length !=0){
           			var sendTime = result['sendTimeStr'];
           			if(typeof(sendTime)!= "undefined"){
           				$('[name=sendTime]').val(sendTime);
           			}
           		
           		}
           		
           		var partyId = result['partyIdTo'];
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
	  			$('#partyIdTo').val(partyId);
           		if(milkTransferId){
           			$('[name = milkTransferId]').val(milkTransferId);
           			if(displayScreen != "ISSUE_CIP"){
           				$('span#extTankerIdToolTip').addClass("tooltip");
	  					$('span#extTankerIdToolTip').removeClass("tooltipWarning");
	  					$('span#extTankerIdToolTip').html(milkTransferId);
	  					
	  					$('span#partyIdFromToolTip').addClass("tooltip");
	  					$('span#partyIdFromToolTip').removeClass("tooltipWarning");
	  					$('span#partyIdFromToolTip').html(partyName);
	  					
           			}
	  				
	  				
	  			}else{
	  				$('span#extTankerIdToolTip').removeClass("tooltip");
	  				$('span#extTankerIdToolTip').addClass("tooltipWarning");
	  				$('span#extTankerIdToolTip').html('none');
	  				
	  				$('span#partyIdFromToolTip').removeClass("tooltip");
	  				$('span#partyIdFromToolTip').addClass("tooltipWarning");
	  				$('span#partyIdFromToolTip').html('none');
	  			}
           }
           
         },
          error: function() {
        	 		
        	 		$('span#extTankerIdToolTip').removeClass("tooltip");
	  				$('span#extTankerIdToolTip').addClass("tooltipWarning");
	  				$('span#extTankerIdToolTip').html('none');
        	 } 
          
    });
}

function fetchTankerRecordNumber(){
	var action = "getTankerRecordNumberTS";
	var tankerNo = $('#tankerNo').val();
	var dataString = {"tankerNo": tankerNo};
	var displayScreen = $('[name="displayScreen"]').val();
	$.ajax({
         type: "POST",
         url: action,
         data: dataString,
         dataType: 'json',
         success: function(result) { 
           if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){    
           			
           			var displayScreen = $('[name=displayScreen]').val();
           			$('span#tankerIdToolTip').removeClass("tooltip");
	  				$('span#tankerIdToolTip').addClass("tooltipWarning");
	  				$('span#tankerIdToolTip').html('none');
	  				$('span#partyIdFromToolTip').removeClass("tooltip");
	  				$('span#partyIdFromToolTip').addClass("tooltipWarning");
	  				$('span#partyIdFromToolTip').html('none');
	  				           	   
           }else{
           		var  milkTransferId= result['milkTransferId'];
           		var displayScreen = $('[name=displayScreen]').val();
	   			if(displayScreen == "ISSUE_GRSWEIGHT"){
	   				tareWeight = result['tareWeight'];
	   				$('#tareWeightToolTip').val(tareWeight);
	   			}
	   			if(displayScreen == "ISSUE_TARWEIGHT"){	
           			var isCipCheckedVal = result['isCipChecked'];
	   				 if(isCipCheckedVal == 'Y' && isCipCheckedVal != 'undefined'){
	   				 	$('#isCipChecked').val(isCipCheckedVal);
	   				 	$('#isCipCheckedDes').html("");
	   				 }else{
	   				 	$('#isCipChecked').val('');
	   				    $('#isCipCheckedDes').html("CIP Not Done Please Contact QC Department");
	   				 }
	   				 
	   			}	
	   			if(displayScreen == "ISSUE_AQC"){
	   				
	   				var milkTransfer = result['milkTransfer'];
	   				if(typeof(milkTransfer)!= "undefined"){
	   				  
	   				  $('#sendFat').val(milkTransfer['fat']);
	   				  $('#sendSnf').val(milkTransfer['snf']);
	   				  
	   				  
	   				}
	   				
	   				
	   			}
           		if($('[name=sealCheck]').length !=0){
           			if(displayScreen == "ISSUE_GRSWEIGHT"){
           				var isSealChecked = result['isSealChecked'];
           				if(typeof(isSealChecked)!= "undefined"){
           					if(isSealChecked == 'Y'){
           						$('#sealCheckY').val('Y');
           						$('#sealCheckY').attr('checked', true);
           					}
           					if(isSealChecked == 'N'){
           						$('#sealCheckN').val('N');
           						$('#sealCheckN').attr('checked', true);
           					}
           					
           				}
           			}
           		
           		}
           		
           		
           		if($('[name=product]').length !=0){
	           		var productId = result['productId'];
	           		if(typeof(productId)!= "undefined"){
	           			$('[name=product]').val(productId);
	           			populateProductSpan();
	           		}
           		}
           		if($('[name=dcNo]').length !=0){
           			var dcNo = result['dcNo'];
           			if(typeof(dcNo)!= "undefined"){
           				$('[name=dcNo]').val(dcNo);
           			}
           		
           		}
           		if($('#displayScreen').val() != "RETURN_QC"){
	           		if($('[name=sendDate]').length !=0){
	           			var sendDate = result['sendDateStr'];
	           			if(typeof(sendDate)!= "undefined"){
	           				$('[name=sendDate]').val(sendDate);
	           			}
	           		
	           		}
           		}
           		if($('[name=sendTime]').length !=0){
           			var sendTime = result['sendTimeStr'];
           			if(typeof(sendTime)!= "undefined"){
           				$('[name=sendTime]').val(sendTime);
           			}
           		
           		}
           		
           		var partyId = result['partyIdTo'];
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
	  			$('#partyIdTo').val(partyId);
           		if(milkTransferId){
           			$('[name = milkTransferId]').val(milkTransferId);
           			if(displayScreen != "ISSUE_CIP"){
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
			source:  availableTags,
			select: function(event, ui) {
					        var selectedValue = ui.item.value;
					        $('#product').val(selectedValue);
					        populateProductSpan();
					    }
		});
}
function populateVehicleName(){
	var availableTags = ${StringUtil.wrapString(vehItemsJSON)!'[]'};
				$("#tankerNo").autocomplete({					
						source:  availableTags,
						select: function(event, ui) {
					        var selectedValue = ui.item.value;
					        $('[name=tankerName]').val(selectedValue);
					        populateVehicleSpan();
					    }
				});
}
function populateExtVehicleName(){
	var availableTags = ${StringUtil.wrapString(extVehItemsJSON)!'[]'};
				$("#extTankerNo").autocomplete({					
						source:  availableTags,
						select: function(event, ui) {
					        var selectedValue = ui.item.value;
					        $("#extTankerNo").val(selectedValue);
					        populateExtVehicleSpan();
					    }
				});
}
function populatePartyName(){
	var availableTags = ${StringUtil.wrapString(partyItemsJSON)!'[]'};
				$("#partyIdTo").autocomplete({					
						source:  availableTags,
						select: function(event, ui) {
					        var selectedValue = ui.item.value;
					        $('[name=partyName]').val(selectedValue);
					        populatePartySpan();
					    }
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
<div >
	
	<div class="screenlet">      
      <div class="grid-header h3" style="width:100%">
      			<#assign velhicleStatus = displayScreen+ "  ::VEHICLE ENTRY DETAILS">
      			
      			<#if displayScreen == "LOADING_DETAILS">
      				<#assign velhicleStatus = "LOADING PLAN">
      			</#if>
      			<#if displayScreen == "ISSUE_CIP">
      				<#assign velhicleStatus = "ISSUING  CIP DETAILS">
      			</#if>
      			
      			<#if displayScreen == "VEHICLE_OUT">
      				<#assign velhicleStatus = " VEHICLE OUT DETAILS">
      			</#if>
      			<#if displayScreen == "ISSUE_GRSWEIGHT">
      				<#assign velhicleStatus = "VEHICLE  GROSS WEIGHT DETAILS">
      			</#if>
      			<#if displayScreen == "ISSUE_TARWEIGHT">
      				<#assign velhicleStatus = "VEHICLE  TARE WEIGHT DETAILS">
      			</#if>
      			<#if displayScreen == "ISSUE_QC">
      				<#assign velhicleStatus = "ISUUING QUALITY CONTROL DETAILS">
      			</#if>
      			<#if displayScreen == "ISSUE_AQC">
      				<#assign velhicleStatus = "ISUUING ACK QUALITY CONTROL DETAILS">
      			</#if>
      			<#if displayScreen == "VEHICLE_CIP">
      				<#assign velhicleStatus = "UN-LOAD AND SILO DETAILS">
      			</#if>
                 <#if displayScreen == "VEHICLE_CIPNEW">
      				<#assign velhicleStatus = "CIP DETAILS">
      			</#if>
				<label>${velhicleStatus}</label>
	  		</div>
    </div>
	<div class="screenlet-body">
	    <#assign setDate = (Static["org.ofbiz.base.util.UtilDateTime"].toDateString(Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp(), "dd-MM-yyyy")).replace(':','')>
		<#assign setTime = (Static["org.ofbiz.base.util.UtilDateTime"].toDateString(Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp(), "HH:mm")).replace(':','')>
    	<form method="post" name="milkReceiptEntry"  id="milkReceiptEntry" >
      	<table style="border-spacing: 0 10px;" border="1">     
	        <tr>
			  <td>&nbsp;</td>
	          <td align='left' valign='middle' nowrap="nowrap">
	          <table>
	          		<tr>
	          			<table>
	          				<#if displayScreen == "LOADING_DETAILS">
		          				<tr>
						        	<td align='left'><span class="h3">Order Id</span> </td><td>
						        		<input  name="orderId" size="10pt" type="text" id="orderId"  autocomplete="off" required="required" /><span class="tooltip h2" id ="orderToolTip">none</span></td>
						        		<input  name="displayScreen" size="10pt" type="hidden" id="displayScreen" value="${displayScreen}" /> 
						        	</td>
						        </tr>
						        <tr>
						        	<td align='left'><span class="h3">Vehicle No</span> </td><td>
						        		<input  name="tankerName" size="10pt" type="text" id="tankerNo"  autocomplete="off" required="required" /><span class="tooltip h1" id ="tankerToolTip">none</span></td>
						        		<input  name="tankerNo" size="10pt" type="hidden"   autocomplete="off" required/></td>
						        		<input  name="milkTransferId" size="10pt" type="hidden"   autocomplete="off"/></td>
						        		<input  name="displayScreen" size="10pt" type="hidden" id="displayScreen" value="${displayScreen}" /> 
						        	</td>
						        	<td>&#160;</td>
						        	<td align='left'><span class="h3">External Vehicle &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</span> 
						        		<input  name="extTankerName" size="10pt" type="text" id="extTankerNo"  autocomplete="off" required="required" /><span class="tooltip h1" id ="extTankerToolTip">Use this field only for tankers sent by the customers</span></td>
						        		
						        		<input  name="extMilkTransferId" size="10pt" type="hidden"   autocomplete="off"/></td>
						        		<input  name="displayScreen" size="10pt" type="hidden" id="displayScreen" value="${displayScreen}" />
						        	</td>
						        </tr>
					        </#if>
					        <#--
	          				<tr>
					        	<td align='left'><span class="h3">Vehicle No</span> </td><td>
					        		<input  name="tankerNo" size="10pt" type="text" id="tankerNo"  autocomplete="off" required="required" /><span class="tooltip h1" id ="tankerToolTip">none</span></td>
					        		<input  name="milkTransferId" size="10pt" type="hidden"   autocomplete="off"/></td>
					        		<input  name="displayScreen" size="10pt" type="hidden" id="displayScreen" value="${displayScreen}" /> 
					        	</td>
					        </tr>
					        -->
					        
					        <#--
					        <tr>
					        	<td align='left'><span class="h3">Product Id</span> </td><td>
					        		<input  name="productId" size="10pt" type="text" id="productId"  autocomplete="off" required="required" /><span class="tooltip h2" id ="orderToolTip">none</span></td>
					        		<input  name="displayScreen" size="10pt" type="hidden" id="displayScreen" value="${displayScreen}" /> 
					        	</td>
					        </tr>
					        -->
					        
					        <#if displayScreen == "VEHICLE_OUT">
							    <tr>
	        						<input  name="statusId" size="10pt" type="hidden" id="statusId" value="MR_VEHICLE_OUT" />
	        						<td align='left' ><span class='h3'>Exit Date</span></td><td><input  type="text" size="15pt" id="exitDate" value="${setDate}" name="exitDate" autocomplete="off" required/></td>
	        					</tr>
	        					<tr>
	        						<td align='left' ><span class="h3">Exit Time(HHMM)[24 hour format]</span> </td><td><input  name="exitTime" size="10" class="onlyNumber" maxlength="4" type="text" id="exitTime" value="${setTime}" autocomplete="off" required/></td>
					        	</tr>
					        </#if>
					        
					        <#if displayScreen == "LOADING_DETAILS">
					        	<tr>
					        		<td>&#160;</td>
					        	</tr>
					        	<tr>
					        		<td align='left' ><span class="h3">Select Product</span></td>
	    							<td>
	                                	<select name="orderItemSeqId" id="orderItemSeqId" onchange="populateDefaultQuantity()">
		      							</select>
		      						</td>
		      						<td>&#160;&#160;&#160;&#160;&#160;</td>
					        		<td align='left' >
					        			<span class="h3">Quantity &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</span> 
					        			<input  name="shipQty"  size="10" class="onlyNumber" maxlength="10" type="text" id="shipQty" autocomplete="off" required/>
					        		</td>
		        					
		      					</tr>
		      						
		                        <tr>
	        						<input  name="vehicleStatusId" size="10pt" type="hidden" id="statusId" value="TS_SHIPMENT_PLANNED" />
	        						<td align='left' ><span class="h3">Dispatch Date</span></td><td><input  type="text" size="10" id="sendDate" name="sendDate" autocomplete="off" required/></td>
	        						<td>&#160;&#160;&#160;&#160;&#160;</td>
	        						<td align='left' >
	        							<span class="h3">Dispatch Time(HHMM)[24 hour format]</span> 
	        							<input  name="sendTime"  size="10" class="onlyNumber" value="${setTime}" maxlength="4" type="text" id="sendTime" autocomplete="off" required/>
	        						</td>
	        					</tr>
	        					<#--
					        	<tr>
								    <td align='left'><span class="h3">DC No </span></td><td><input  name="dcNo" size="12" maxlength="10" id= "dcNo" type="text" autocomplete="off"  required/><em>*<em></td>
								</tr>
								--> 
					        	<tr>
					  				<td align='left' ><span class="h3">Route</span> </td>
					  				<td valign='middle'>
					  					<input type="text" name="routeId" id="routeId" size="10" maxlength="10" onblur='this.value=this.value.toUpperCase();'/>
					  				</td>
					  				<td>&#160;</td>
					  				<td><span  class='h1'  id="routeTooltip"></span></td>
								 </tr>  
				        		
				        		<tr>
					        		<td>&#160;</td>
					        	</tr>
		        				<tr>
					        		<td align='left' ><span class="h3">Insurance Qty</span> </td>
					        		<td><input  name="insuranceQty"  size="10" class="onlyNumber" maxlength="10" type="text" id="insuranceQty" autocomplete="off" required/></td>
					        	</tr>
				        		<tr>
					        		<td>&#160;</td>
					        	</tr>
	                         	<tr>
	                   				<td><span class='h3'>From</span></td><td><span class='h3'> Indapur</span><input type="hidden" size="6" id="partyId" maxlength="15" name="partyId" autocomplete="off" value="Company" /></td>
	                   			</tr>
	                         	<tr>
	                         		<td><span class="h3">Ship To party </span></td><td>
	                         		<input type="text" size="20" maxlength="20" name="partyIdTo" id="partyIdTo" autocomplete="on" required="required"/><span class="tooltip" id ="partyToolTip">none</span></td>
	                   			</tr>
	                   			<tr>
	                         		<td><span class="h3">Bill To party </span></td><td>
	                         		<input type="text" size="20" maxlength="20" name="billToPartyId" id="billToPartyId" autocomplete="on" required="required"/><span class="tooltip" id ="billToPartyToolTip">none</span></td>
	                   			</tr>
	                   			<tr>
					        		<td>&#160;</td>
					        	</tr>
					        	
		                   	</#if>
					        <#if displayScreen == "ISSUE_CIP">
					        	<tr>
						        	<td align='left'><span class="h3">Vehicle No</span> </td><td>
						        		<select name="tankerNo" required="required" id="tankerNo" onchange="fetchTankerRecordNumber()">
				        					<option value="">SELECT</option>
				        					<#if planningClearedShipList?has_content>
					        					<#list planningClearedShipList as vehicle>
					        						<option value="${vehicle.vehicleId}">${vehicle.vehicleName} [${vehicle.vehicleId}]</option>
					        					</#list>
				        					</#if>
	          							</select>
	          							<input  name="milkTransferId" size="10pt" type="hidden"   autocomplete="off"/></td>
						        		<input  name="displayScreen" size="10pt" type="hidden" id="displayScreen" value="${displayScreen}" /> 
						        	</td>
						        </tr>
						        <tr>
					        		<td>&#160;</td>
					        	</tr>
		                        <#--
		                        <tr>
	        						<input  name="vehicleStatusId" size="10pt" type="hidden" id="statusId" value="MR_ISSUE_CIP" />
	        						<td align='left' ><span class="h3">Entry Date</span></td><td><input  type="text" size="10" id="entryDate" name="entryDate" value="${setDate}" autocomplete="off" required/></td>
	        						<td>&#160;&#160;&#160;&#160;&#160;</td>
	        						<td align='left' ><span class="h3">Dispatch Date</span></td><td><input  type="text" size="10" id="sendDate" name="sendDate" autocomplete="off" required/></td>
	        					</tr>
	        					<tr>
	        						<td align='left' ><span class="h3">Entry Time(HHMM)[24 hour format]</span> </td><td><input  name="entryTime" size="10" class="onlyNumber" maxlength="4" type="text" id="exitTime" value="${setTime}" autocomplete="off" required/></td>
					        		<td>&#160;&#160;&#160;&#160;&#160;</td>
					        		<td align='left' ><span class="h3">Dispatch Time(HHMM)[24 hour format]</span> </td><td><input  name="sendTime"  size="10" class="onlyNumber" value="${setTime}" maxlength="4" type="text" id="sendTime" autocomplete="off" required/>
		        					</td>
					        	</tr>
						        <tr>
					        		<td>&#160;</td>
					        	</tr>
					        	-->
						        <tr>
							       	<td align='left'><span class="h3">DC No</span> </td><td><input  name="dcNo" size="10" maxlength="10" id= "dcNo" type="text" autocomplete="off"/></td>
							    </tr>
							    <tr>
        							<td align='left' valign='middle' nowrap="nowrap"><span class="h3">Milk Type</span></td><td>
						        		<input type="hidden" size="10" id="productId" maxlength="6" name="productId" autocomplete="off" value="" />
                     					<input type="text" size="10" maxlength="6" name="product" id="product" autocomplete="on"/><span class="tooltip" id ="productToolTip">none</span></td>
									</td>
				        		</tr> 
				        		<tr>
					        		<td>&#160;</td>
					        	</tr>
	                         	<tr>
	                   				<td><span class='h3'>From</span></td><td><span class='h3'> Indapur</span><input type="hidden" size="6" id="partyId" maxlength="15" name="partyId" autocomplete="off" value="Company" /></td>
	                   			</tr>
	                         	<tr>
	                         		<td><span class="h3">Ship To party </span></td><td>
	                         		<input type="text" size="15" maxlength="15" name="partyIdTo" id="partyIdTo" autocomplete="on" required="required"/><span class="tooltip" id ="partyToolTip">none</span></td>
	                   			</tr>
	                   			<#--
	                   			<tr>
	                         		<td><span class="h3">Bill To party </span></td><td>
	                         		<input type="text" size="6" maxlength="6" name="billToPartyId" id="billToPartyId" autocomplete="on" required="required"/><span class="tooltip" id ="billToPartyToolTip">none</span></td>
	                         		<input type="hidden" size="6" maxlength="6" name="billToPartyId" required="required"/>
	                   			</tr>
	                   			-->
	                   			<tr>
					        		<td>&#160;</td>
					        	</tr>
					        	<#--
	                   			<tr>
	                   				<td><span class='h3'>Seal check:</span></td><td> 
	                   				<input type="radio" name="sealCheck" id="sealCheckY" value="Y"/> YES   <input type="radio" name="sealCheck" id="sealCheckN" value="N"/> NO</td>
	                   			</tr>
	                   			<tr>
	        						<input  name="vehicleStatusId" size="10pt" type="hidden" id="statusId" value="MR_ISSUE_CIP" />
	        						<td align='left' ><span class="h3">Seal Check Date</span></td><td><input  type="text" size="10" id="sealCheckDate" name="sealCheckDate" value="${setDate}" autocomplete="off" required/></td>
	        					</tr>
	        					<tr>
	        						<td align='left' ><span class="h3">Seal Check Time(HHMM)[24 hour format]</span> </td><td><input  name="sealCheckTime" size="10" class="onlyNumber" maxlength="4" type="text" id="sealCheckTime" value="${setTime}" autocomplete="off" required/></td>
		        					</td>
					        	</tr>
					        	-->
	                   			<tr>
	                   				<input  name="vehicleStatusId" size="10pt" type="hidden" id="statusId" value="TS_CIP" />
                                 	<td align='left' ><span class="h3"> Is CIP Checked</span></td><td><input type="checkbox" name="isCipChecked" id="isCipChecked" style="width:20px;height:20px;" value="Y" required/><em>*<em></td>
                                </tr>
		                   	</#if>	
					        <#if displayScreen == "ISSUE_GRSWEIGHT">	
					        	<tr>
						        	<td align='left'><span class="h3">Vehicle No</span> </td><td>
						        		<select name="tankerNo" required="required" id="tankerNo" onchange="fetchTankerRecordNumber()">
				        					<option value="">SELECT</option>
				        					<#if qcClearedShipList?has_content>
					        					<#list qcClearedShipList as vehicle>
					        						<option value="${vehicle.vehicleId}">${vehicle.vehicleName} [${vehicle.vehicleId}]</option>
					        					</#list>
				        					</#if>
	          							</select>
	          							<input  name="milkTransferId" size="10pt" type="hidden"   autocomplete="off"/></td>
						        		<input  name="displayScreen" size="10pt" type="hidden" id="displayScreen" value="${displayScreen}" /> 
						        	</td>
						        </tr>
							    <tr><td>&#160;</td></tr>
							    <tr>
	                   				<td><span class='h3'>From</span></td><td><span class='h3'> Indapur</span><input type="hidden" size="6" id="partyId" maxlength="15" name="partyId" autocomplete="off" value="Company" /></td>
	                   			</tr>
					        	<tr>
								    <td align='left'><span class="h3">DC No </span></td><td><input  name="dcNo" size="12" maxlength="10" id= "dcNo" type="text" autocomplete="off"  required/><em>*<em></td>
								</tr>
								<tr>
					        		<td>&#160;</td>
					        	</tr>	        					
							    <tr>
	        						<td align='left' ><span class="h3"> Tare Weight(Kgs)</span></td>
	        						<td><input  type="text" class="onlyNumber" size="15pt" id="tareWeightToolTip" autocomplete="off" value="0"/></td>
	        					</tr>
							    <tr>
	        						<td align='left' ><span class="h3"> Gross Weight(Kgs)</span> </td><td><input  type="text" size="15pt" id="grossWeight" name="grossWeight" autocomplete="off" required="required"/></td>
	        					</tr>
	        					<tr>
	        						<td align='left' ><span class="h3">Net Weight(Kgs)</span></td>
	        						
	        						<td><input  type="text" class="onlyNumber" size="15pt" id="netWeightToolTip" autocomplete="off" value="0"/></td>
	        					</tr>
	        					<tr><td>&#160;</td></tr>
							    <tr>
	        						<input  name="vehicleStatusId" size="10pt" type="hidden" id="vehicleStatusId" value="TS_GROSS_WEIGHT" />
	        						<td align='left' ><span class="h3">Gross Weight Date</span></td><td><input  type="text" size="15pt" id="grossDate" name="grossDate" value="${setDate}" autocomplete="off" /></td>
	        						<td>&#160;&#160;&#160;&#160;&#160;</td>
	        						<td align='left' ><span class="h3">Gross Weight Time(HHMM)[24 hour format]</span> </td><td><input  name="grossTime" value="${setTime}" size="10" class="onlyNumber" maxlength="4" type="text" id="grossTime" autocomplete="off" required/></td>
	        					</tr>
	        					<tr><td>&#160;</td></tr>
	        					<tr>
	        						<td align='left' ><span class="h3">Number of compartments</span></td><td><input  type="text" size="15pt" id="numberOfCells" name="numberOfCells" autocomplete="off" required="required"/></td>
	        					</tr>
	        					<tr>
							       	<td align='left'><span class="h3">Driver Name</span> </td><td><input  name="driverName" size="25" maxlength="20" id= "driverName" type="text" autocomplete="off"  required/><em>*<em></td>
							    </tr>
							    <tr><td>&#160;</td></tr>
	        					<tr>
	                   				<td><span class='h3'>Seal check:</span></td><td> <input type="radio" name="sealCheck" id="sealCheckY" value="Y"/> YES   <input type="radio" name="sealCheck" id="sealCheckN" value="N"/> NO</td>
	                   			</tr>
	        				</#if>	
	        				<#if displayScreen == "ISSUE_TARWEIGHT">
	        					<tr>
						        	<td align='left'><span class="h3">Vehicle No</span> </td><td>
						        		<select name="tankerNo" required="required" id="tankerNo" onchange="fetchTankerRecordNumber()">
				        					<option value="">SELECT</option>
				        					<#if cipClearedShipList?has_content>
					        					<#list cipClearedShipList as vehicle>
					        						<option value="${vehicle.vehicleId}">${vehicle.vehicleName} [${vehicle.vehicleId}]</option>
					        					</#list>
				        					</#if>
	          							</select>
	          							<input  name="milkTransferId" size="10pt" type="hidden"   autocomplete="off"/></td>
						        		<input  name="displayScreen" size="10pt" type="hidden" id="displayScreen" value="${displayScreen}" /> 
						        	</td>
						        </tr>
						        <tr>
					        		<td>&#160;</td>
					        	</tr>
						        <tr>
	                   				<td><span class='h3'>From</span></td><td><span class='h3'> Indapur</span><input type="hidden" size="6" id="partyId" maxlength="15" name="partyId" autocomplete="off" value="Company" /></td>
	                   			</tr>
					        	<tr>
								    <td align='left'><span class="h3">DC No </span></td><td><input  name="dcNo" size="12" maxlength="10" id= "dcNo" type="text" autocomplete="off"  required/><em>*<em></td>
								</tr>
                                <tr>
	        						<td align='left' ><span class="h3">Is Cip Checked</span></td>
	        						<td align='left'><input type="text" readOnly size="3pt" id="isCipChecked" name="isCipChecked" required/><em>*<em><span class="h4" id="isCipCheckedDes" name="isCipCheckedDes"/></td>
	        					</tr> 
	        					<tr>
					        		<td>&#160;</td>
					        	</tr>
	        					<tr>
	        						<input  name="vehicleStatusId" size="10pt" type="hidden" id="vehicleStatusId" value="TS_TARE_WEIGHT" />
	        						<td align='left' ><span class="h3">Tare Weight Date</span></td><td><input  type="text" size="15pt" id="tareDate" name="tareDate" value="${setDate}" autocomplete="off" required/></td>
	        					</tr>
	        					<tr>
	        						<td align='left' ><span class="h3">Tare Time(HHMM)[24 hour format]</span> </td><td><input  name="tareTime" class="onlyNumber" value="${setTime}" size="10" maxlength="4" type="text" id="tareTime" autocomplete="off" required/></td>
					        	</tr>
					        	<tr>
					        		<td>&#160;</td>
					        	</tr>
							    <tr>
	        						<td align='left' ><span class="h3">Tare Weight(Kgs)</span></td><td><input  type="text" class="onlyNumber" size="15pt" id="tareWeight" name="tareWeight" autocomplete="off" required/></td>
	        					</tr>
	        				</#if>
	        				<#if displayScreen == "VEHICLE_CIP">
	        					<tr>
	        						<input  name="statusId" size="10pt" type="hidden" id="statusId" value="MR_VEHICLE_CIP" />
	        						<td align='left' ><span class='h3'>Un-Loading Date</span></td><td><input  type="text" size="15pt" id="cipDate" name="cipDate" value="${setDate}" autocomplete="off" required/></td>
	        					</tr>
	        					<tr>
	        						<td align='left' ><span class="h3">Un-Loading Time(HHMM)[24 hour format]</span> </td><td><input  name="cipTime" class="onlyNumber" value="${setTime}" size="10" maxlength="4" type="text" id="tareTime" autocomplete="off" required/></td>
					        	</tr>
							    <tr>
	        						<td align='left' ><span class='h3'>UN-LOADED TO SILO</span></td><td> 
		        						<select name="silo" required="required" id="silo" allow-empty="false">
						        					<option value="">SELECT</option>
						        					<#if rawMilkSilosList?has_content>
							        					<#list rawMilkSilosList as rawMilkSilo>
							        						<option value="${rawMilkSilo.facilityId}">${rawMilkSilo.facilityId}</option>
							        					</#list>
						        					</#if>
	          							</select>
          						    </td>
	        					</tr>
	        					<tr>
	        						<td align='left' ><span class='h3'>Milk Used For</span></td><td> 
		        						<select name="purposeTypeId" required="required" id="purposeTypeId" allow-empty="false">
						        					<#if milkPurchasePurposeTypeList?has_content>
							        					<#list milkPurchasePurposeTypeList as purposeType>
							        						<option value="${purposeType.enumId}">${purposeType.enumCode}</option>
							        					</#list>
						        					</#if>
	          							</select>
          						    </td>
	        					</tr>
									        					
	        				</#if>	
	        				<#if displayScreen == "ISSUE_QC">
	        					<tr>
						        	<td align='left'><span class="h3">Vehicle No</span> </td><td>
						        		<select name="tankerNo" required="required" id="tankerNo" onchange="fetchTankerRecordNumber()">
				        					<option value="">SELECT</option>
				        					<#if tareWtClearedShipList?has_content>
					        					<#list tareWtClearedShipList as vehicle>
					        						<option value="${vehicle.vehicleId}">${vehicle.vehicleName} [${vehicle.vehicleId}]</option>
					        					</#list>
				        					</#if>
	          							</select>
	          							<input  name="milkTransferId" size="10pt" type="hidden"   autocomplete="off"/></td>
						        		<input  name="displayScreen" size="10pt" type="hidden" id="displayScreen" value="${displayScreen}" /> 
						        	</td>
						        </tr>
						        <tr>
					        		<td>&#160;</td>
					        	</tr>
					        	<tr>
	                   				<td><span class='h3'>From</span></td><td><span class='h3'> Indapur</span><input type="hidden" size="6" id="partyId" maxlength="15" name="partyId" autocomplete="off" value="Company" /></td>
	                   			</tr>
					        	<tr>
								    <td align='left'><span class="h3">DC No </span></td><td><input  name="dcNo" size="12" maxlength="10" id= "dcNo" type="text" autocomplete="off"  required/><em>*<em></td>
								</tr>
								<tr>
					        		<td>&#160;</td>
					        	</tr>
	        					<tr>
	        						<input  name="vehicleStatusId" size="10pt" type="hidden" id="statusId" value="TS_QC" />
	        						<td align='left' ><span class="h3">QC Date</span></td><td><input  type="text" size="15pt" id="testDate" name="testDate" value="${setDate}" autocomplete="off" /></td>
	        						<td>&#160;&#160;&#160;&#160;&#160;</td>
	        						<td align='left' ><span class="h3">QC Time(HHMM)[24 hour format]</span> </td><td><input  name="testTime" value="${setTime}" size="10" class="onlyNumber" maxlength="4" type="text" id="testTime" autocomplete="off" required/>
			        				</td>
	        					</tr>
	        					<tr>
	        						<td align='left' ><span class="h3">Seal Check Date</span></td><td><input  type="text" size="10" id="sealCheckDate" name="sealCheckDate" value="${setDate}" autocomplete="off" required/></td>
	        						<td>&#160;&#160;&#160;&#160;&#160;</td>
	        						<td align='left' ><span class="h3">Seal Check Time(HHMM)[24 hour format]</span> </td><td><input  name="sealCheckTime" size="10" class="onlyNumber" maxlength="4" type="text" id="sealCheckTime" value="${setTime}" autocomplete="off" required/></td>
		        					</td>
	        					</tr>
	        					
	        					<tr><td>&#160;</td></tr>
					        	<tr>
	        						<td align='left' ><span class="h3">Seal Number</span></td>
	        						<td><input  name="sealNumber"  size="10" class="onlyNumber" maxlength="8" type="text" id="sealNumber" autocomplete="off" required/>
			        					</td>
					        	</tr>
					        	<tr>
	        						<td align='left' valign='middle' nowrap="nowrap"><span class="h3">Milk Type</span></td><td>
							        	<input type="hidden" size="6" id="productId" maxlength="6" name="productId" autocomplete="off" value="" />
                         				<input type="text" size="6" maxlength="6" name="product" id="product" autocomplete="on" required/><span class="tooltip" id ="productToolTip">none</span></td>
									</td>
					        	</tr>
	        					<tr><td>&#160;</td></tr>
						        <tr>
	        						<td align='left' ><span class="h1"> Dispatch Quality</span> </td>
					        	</tr>
					        	<tr><td>&#160;</td></tr>
					        	<tr>
					        		<td align='right' ><span class="h3">Temp</span> </td><td><input  name="sendTemp" size="7pt" maxlength="4" type="text" id="sendTemp" autocomplete="off" required/></td>
					        		<td align='left' > <span class="h3">Acidity% </span></td><td><input  name="sendAcid" size="7pt" maxlength="5" type="text" id="sendAcid" autocomplete="off" required/></td>
					        		<#--<td align='right' ><span class="h3"> CLR </span></td><td ><input  name="sendCLR" size="7pt" maxlength="4" type="text" id="sendCLR" autocomplete="off" required/></td>-->
					        		<td align='left' ><span class="h3"> Fat% </span></td><td><input  name="sendFat" size="7pt" maxlength="4" type="text" id="sendFat" autocomplete="off" required/></td>
					        		<td align='left' ><span class="h3"> Snf% </span></td><td><input  name="sendSnf" size="7pt" maxlength="5" type="text" id="sendSnf" autocomplete="off" required/></td>
					        	</tr>
					        	<tr>
					        		<td align='right' ><span class="h3"> COB(Y/N)</span> </td>
					        		<td><select name="sendCob" required="required" id="sendCob" allow-empty="true">
					        					<option value="">SELECT</option>
					        					
					        					<option value="N">NEGITIVE</option>
            									<option value="Y">POSITIVE</option>
          												</select></td>
					        		<td align='left' ><span class="h3">OT</span> </td>
					        		<td>
					        		<select name="sendOrganoLepticTest" required="required" id="recdOrganismTest" allow-empty="true">
					        					<option value="">SELECT</option>
					        					<option value="NORMAL">NORMAL</option>
            									<option value="ABNORMAL">ABNORMAL</option>
          												</select></td>
					        		<td align='left' ><span class="h3"> SedimentTest(+ve/-ve)</span> </td><td>
					        		<select name="sendSedimentTest" required="required" id="recdSedimentTest" allow-empty="true">
					        					<option value="">SELECT</option>
					        					<option value="N">NEGITIVE</option>
            									<option value="Y">POSITIVE</option>
          												</select></td>
					        	</tr>
						    </#if>
						    <#if displayScreen == "ISSUE_AQC">
						    	<tr>
						        	<td align='left'><span class="h3">Vehicle No</span> </td><td>
						        		<select name="tankerNo" required="required" id="tankerNo" onchange="fetchTankerRecordNumber()">
				        					<option value="">SELECT</option>
				        					<#if grossWtClearedShipList?has_content>
					        					<#list grossWtClearedShipList as vehicle>
					        						<option value="${vehicle.vehicleId}">${vehicle.vehicleName} [${vehicle.vehicleId}]</option>
					        					</#list>
				        					</#if>
	          							</select>
	          							<input  name="milkTransferId" size="10pt" type="hidden"   autocomplete="off"/></td>
						        		<input  name="displayScreen" size="10pt" type="hidden" id="displayScreen" value="${displayScreen}" /> 
						        	</td>
						        </tr>
							    <tr><td>&#160;</td></tr>
							    <tr>
	                   				<td><span class='h3'>From</span></td><td><span class='h3'> Indapur</span><input type="hidden" size="6" id="partyId" maxlength="15" name="partyId" autocomplete="off" value="Company" /></td>
	                   			</tr>
					        	<tr>
								    <td align='left'><span class="h3">DC No </span></td><td><input  name="dcNo" size="12" maxlength="10" id= "dcNo" type="text" autocomplete="off"  required/><em>*<em></td>
								</tr>
						    	<tr><td>&#160;</td></tr>
	        					<tr>
	        						<input  name="vehicleStatusId" size="10pt" type="hidden" id="vehicleStatusId" value="TS_ACK_QC" />
	        						<td align='left' ><span class="h3">QC Date</span></td><td><input  type="text" size="15pt" id="testDate" name="testDate" value="${setDate}" autocomplete="off" /></td>
	        						<td>&#160;&#160;&#160;&#160;&#160;</td>
	        						<td align='left' ><span class="h3">QC Time(HHMM)[24 hour format]</span> </td><td><input  name="testTime" value="${setTime}" size="10" class="onlyNumber" maxlength="4" type="text" id="testTime" autocomplete="off" required/>
			        					</td>
	        					</tr>
	        					
					        	<tr><td>&#160;</td></tr>
					        	<tr>
	        						<td align='left' ><span class="h3">Seal Number</span></td>
	        						<td><input  name="sealNumber"  size="10" class="onlyNumber" maxlength="8" type="text" id="sealNumber" autocomplete="off" required/>
			        					</td>
					        	</tr>
					        	<tr>
	        						<td align='left' valign='middle' nowrap="nowrap"><span class="h3">Milk Type</span></td><td>
							        	<input type="hidden" size="6" id="productId" maxlength="6" name="productId" autocomplete="off" value="" />
                         				<input type="text" size="6" maxlength="6" name="product" id="product" autocomplete="on" required/><span class="tooltip" id ="productToolTip">none</span></td>
									</td>
					        	</tr>
	        					<tr><td>&#160;</td></tr>
					        	<tr>
	        						<td align='left' ><span class="h1"> Recieved Quality</span> </td>
					        	</tr>
					        	<tr><td>&#160;</td></tr>
					        	<tr>
					        		<td align='right' ><span class="h3">Temp</span> </td><td><input  name="recdTemp" size="7pt" maxlength="4" type="text" id="recdTemp" autocomplete="off" required/></td>
					        		<td align='left' > <span class="h3">Acidity% </span></td><td><input  name="recdAcid" size="7pt" maxlength="5" type="text" id="recdAcid" autocomplete="off" required/></td>
					        		<#--<td align='right' ><span class="h3"> CLR </span></td><td ><input  name="recdCLR" size="7pt" maxlength="4" type="text" id="recdCLR" autocomplete="off" required/></td>-->
					        		<td align='left' ><span class="h3"> Fat% </span></td><td><input  name="recdFat" size="7pt" maxlength="4" type="text" id="recdFat" autocomplete="off" required/></td>
					        		<td align='left' ><span class="h3"> Snf% </span></td><td><input  name="recdSnf" size="7pt" maxlength="5" type="text" id="recdSnf" autocomplete="off" required/></td>
					        	</tr>
					        	<tr>
					        		<td align='right' ><span class="h3"> COB(Y/N)</span> </td>
					        		<td><select name="recdCob" required="required" id="recdCob" allow-empty="true">
					        					<option value="">SELECT</option>
					        					
					        					<option value="N">NEGITIVE</option>
            									<option value="Y">POSITIVE</option>
          												</select></td>
					        		<td align='left' ><span class="h3">OT</span> </td>
					        		<td>
					        		<select name="recdOrganoLepticTest" required="required" id="recdOrganismTest" allow-empty="true">
					        					<option value="">SELECT</option>
					        					<option value="NORMAL">NORMAL</option>
            									<option value="ABNORMAL">ABNORMAL</option>
          												</select></td>
					        		<td align='left' ><span class="h3"> SedimentTest(+ve/-ve)</span> </td><td>
					        		<select name="recdSedimentTest" required="required" id="recdSedimentTest" allow-empty="true">
					        					<option value="">SELECT</option>
					        					<option value="N">NEGITIVE</option>
            									<option value="Y">POSITIVE</option>
          												</select></td>
					        	</tr>
					        	
					        	
						    </#if>
						    
                            <#if displayScreen == "VEHICLE_CIPNEW">
                                <tr>
                                    <input  name="statusId" size="10pt" type="hidden" id="statusId" value="MR_VEHICLE_CIPNEW" />
		        					<td align='left' ><span class="h3">Dispatch Date</span></td><td><input  type="text" size="15pt" id="sendDate" name="sendDate" value="${setDate}" autocomplete="off" required/></td>
		        					
		        				</tr>
		        				<tr>
		        					<td align='left' ><span class="h3">Dispatch Time(HHMM)[24 hour format]</span> </td><td><input  name="sendTime"  size="10" class="onlyNumber" maxlength="4" type="text" id="sendTime" autocomplete="off" value="${setTime}" required />
		        					</td>
						        </tr>
                                <tr>
                                 <td align='left' ><span class="h3"> Is CIP Checked</span></td><td><input type="checkbox" name="isCipChecked" id="isCipChecked" style="width:20px;height:20px;" value="Y" required/><em>*<em></td>
                                </tr>
                             </#if>
	          		</table>
	          		<#if displayScreen == "ISSUE_QC">
	          			<table>
	          					<tr>
	          						<td> &nbsp;&nbsp;<td>
	          					</tr>
	          					<tr>
					        		<td valign = "middle" align="center"><span class="h1">Remarks</span></td>
					        	</tr>
					        	<tr>	
					        			<td>&nbsp;</td><td>&nbsp;</td> <td>&nbsp;</td><td>&nbsp;</td>
      								<td>&nbsp;</td><td>&nbsp;</td> <td>&nbsp;</td><td>&nbsp;</td>	
					        		<td valign = "middle" align="center">
					        			<textarea cols="60" rows="3" name="qcComments" maxlength="200" id="qcComments"></textarea>
					        		</td>
					        	</tr>
					    </table>    	
	          		</#if>
	          		
	         	 </td>  
	        </tr>
      </table>
      <table>
      	<tr>
      		<td>&nbsp;</td><td>&nbsp;</td> <td>&nbsp;</td><td>&nbsp;</td>
      		<td>&nbsp;</td><td>&nbsp;</td> <td>&nbsp;</td><td>&nbsp;</td>
      		<td>&nbsp;</td><td>&nbsp;</td> <td>&nbsp;</td><td>&nbsp;</td>
	      	<td valign = "middle" align="center">
	      	<div class='tabletext h2'>
	 			<input type="submit" align="right"  class="button" name="submitButton"  id="submitEntry" <#if displayScreen == "LOADING_DETAILS">value="Add"<#else>value="Update"</#if>/>      
	      		</div>
	      	</td>
      	</tr>
      </table>
	       
   </form>
  </div>
 </div>
