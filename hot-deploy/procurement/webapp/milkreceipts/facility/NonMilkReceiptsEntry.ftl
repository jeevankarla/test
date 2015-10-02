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
var grossWeight = 0;
var productsCount =0;
var grsWeight = 0;
var itemsList = [];
$(document).ready(function() {	
  hideDiv();
  $('#tareWeight').autoNumeric({mDec: 2 ,mRound: 'A' ,	aSep:'' , autoTab : true}).trigger('focusout');
  $('#grossWeight').autoNumeric({mDec: 2 ,mRound: 'A' ,	aSep:'' , autoTab : true}).trigger('focusout');
  $('#dispatchWeight').autoNumeric({mDec: 2 ,mRound: 'A' ,	aSep:'' , autoTab : true}).trigger('focusout');

	if($('#displayScreen').val()=="VEHICLE_IN"){
 		makeDatePicker1("sendDate","fromDate");
 		//makeDatePicker("entryDate","fromDate");
 		$('#dcNo').removeAttr("readonly");
 		$('#tankerName').focusout(function (){
 			var vehicleId = $('#tankerName').val();
 			if(typeof(vehicleId)!= "undefined" && vehicleId!='' && vehicleId != null ){
 			var otherVehicleCodeJson = ${StringUtil.wrapString(otherVehicleCodeJson)};
 				var newVehicleId = otherVehicleCodeJson[vehicleId];
 				if(newVehicleId){
 					alert("Please check vehicle. This is Ptc or Route Vechile.!");
 					$('#tankerName').val('');
 					$('span#tankerToolTip').html('');
 					return false;
 				}
 			}
 		});
 	}
 	if($('#displayScreen').val()=="VEHICLE_OUT"){
 		$('#dcNo').attr("readonly","readonly");
 		makeDatePicker("exitDate","fromDate");
 	}
 	if($('#displayScreen').val()=="VEHICLE_TAREWEIGHT"){
 		$('#dcNo').attr("readonly","readonly");
 		$('#grossWeightToolTip').attr("readonly","readonly");
 		$('#sendWeightToolTip').attr("readonly","readonly");
 		$('#netWeightToolTip').attr("readonly","readonly");
 		makeDatePicker("tareDate","fromDate");
 		$('#tareWeight').focusout(function (){
    		if(grsWeight>0){
    			var checkVal = parseInt($('#tareWeight').val());
    			if(typeof(checkVal)!= "undefined" && checkVal!='' && checkVal != null ){
    				if(checkVal>=grsWeight){
    					alert("Please check tare Weight.!");
    					$('#tareWeight').val('');
    					return false;
    				}
    			}
    		}
		});
 	}
 	if($('#displayScreen').val()=="VEHICLE_GROSSWEIGHT"){
 		$('#dcNo').attr("readonly","readonly");
 		makeDatePicker("grossDate","fromDate");
 		$('#grossWeight').focusout(function (){
    		if(grsWeight>0){
    			var checkVal = parseInt($('#grossWeight').val());
    			if(typeof(checkVal)!= "undefined" && checkVal!='' && checkVal != null ){
    				if(checkVal>=grsWeight){
    					alert("Please check Gross Weight.!");
    					$('#grossWeight').val('');
    					return false;
    				}
    			}
    		}
		});
		$('#productId').focusout(function(){
			var productIdVal = $('[name=productId]').val();
			if(typeof(itemsList)!= "undefined" && itemsList!='' && itemsList != null ){
				for(var i=0 ; i<itemsList.length ; i++){
					var innerItems=itemsList[i];
					var prodId = innerItems['productId'];
					if(prodId == productIdVal){
						alert("This Product Already Entered.!");
						$('[name=productId]').val('');
						return false;
					}
			     }
			}
		});
 		
 	}
 	if($('#displayScreen').val()=="VEHICLE_QC"){
 		$('#dcNo').removeAttr("readonly");
 		$('#sendDate').attr("readonly","readonly");
 		$('#sendTime').attr("readonly","readonly");
 		makeDatePicker("testDate","fromDate");
 	}
 	if($('#displayScreen').val()=="VEHICLE_CIP" || $('#displayScreen').val()=="VEHICLE_CIPNEW"){
 		$('#dcNo').attr("readonly","readonly");	
 		makeDatePicker("cipDate","fromDate");
 	}
 	
	$('#ui-datepicker-div').css('clip', 'auto');
	
	$("input").keyup(function(e){
	  		
	  		if(e.target.name == "tareWeight"){
	  			populateNetWeight();
	  		}
	  		if(e.target.name == "tankerName"){
	  			$('[name=tankerName]').val(($('[name=tankerName]').val()).toUpperCase());
	  			populateVehicleName();
				populateVehicleSpan();
	  			
	  		}
	  		if(e.target.name == "productId"){
	  			$('[name=productId]').val(($('[name=productId]').val()).toUpperCase());
	  			populateProductNames();
				populateProductSpan();	
	  		}
	  		if(e.target.name == "partyId"){
	  			$('[name=partyId]').val(($('[name=partyId]').val()).toUpperCase());
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
	  		if(e.target.name == "noOfProduct"){
	  			var prodCount = $('#noOfProduct').val(); 		
	  			   if(typeof(prodCount)!= "undefined" && prodCount!='' && prodCount != null ){
	  			   		if(typeof(productsCount)!= "undefined" && productsCount!='' && productsCount != null ){
	  			   			if(prodCount<=productsCount){
	  			   				alert("Please check no Of Products..!");
	  			   				$('#noOfProduct').val('');
	  			   				return false;
	  			   			}
	  			   		}
	  			   }
	  		}
	  		if(e.target.name == "noOfProducts"){
	  			var prodCount = $('#noOfProducts').val(); 		
	  			   if(typeof(prodCount)!= "undefined" && prodCount!='' && prodCount != null && prodCount<1){
		   				alert("Please check no Of Products..!");
		   				$('#noOfProducts').val('1');
		   				return false;
	  			   }
	  		}
	  		if(e.target.name == "product" ){
	  			populateProductNames();
				populateProductSpan();	  			
	  		}
	  		if(e.target.name == "dcNo" || e.target.name == "sealNumber1" || e.target.name == "sealNumber2" || e.target.name == "sealNumber3" || e.target.name == "sealNumber4" || e.target.name == "sealNumber5" || e.target.name == "sealNumber6"){
	  			//alert(e.which);
	  			if(e.which == 110 || e.which == 190){
    				$(this).val( $(this).val().replace('.',''));
	    		}
	    			$(this).val( $(this).val().replace(/[^0-9\.]/g,''));
		  	}
			if(e.target.name == "tankerName"){
	  			if(e.which == 110 || e.which == 190 || e.which ==32 || e.which==188 ){
    				$(this).val( $(this).val().replace('.',''));
    				$(this).val( $(this).val().replace(' ',''));
    				$(this).val( $(this).val().replace(',',''));
	    		}
		  	}
	}); 
});
function populateProductNames(){
	var availableTags = ${StringUtil.wrapString(productItemsJSON)!'[]'};
		$("#productId").autocomplete({					
			source:  availableTags,
			select: function(event, ui) {
					        var selectedValue = ui.item.value;
					        $('#productId').val(selectedValue);
					        populateProductSpan();
					    }
		});
}
function populateProductSpan(){
	var productJson = ${StringUtil.wrapString(productJson)}
	var tempProductJson = productJson[$('[name=productId]').val()];
	if(tempProductJson){
		$('span#productToolTip').addClass("tooltip");
		$('span#productToolTip').removeClass("tooltipWarning");
		productName = tempProductJson["name"];
		$('[name=productId]').val($('[name=productId]').val());
		$('span#productToolTip').html(productName);
		
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
		
		fetchWeighmentDetails();
		
	}else{
		var newTankerName=$('[name=tankerName]').val();
		$('span#tankerToolTip').addClass("tooltip");
		$('span#tankerToolTip').removeClass("tooltipWarning");
		$('span#tankerToolTip').html(newTankerName);
	}

}
function populateNetWeight(){
	var tareWeight = $('[name=tareWeight]').val();
	if(typeof(tareWeight)!= "undefined" ){	
		var netWeight = grossWeight-tareWeight ;
		$('#netWeightToolTip').val(netWeight);
		if(netWeight <0){
			alert('Please check the tareWeight . ');
			$('[name=tareWeight]').val('');
			$('#netWeightToolTip').val(grossWeight);
		}
	}else{
		$('#netWeightToolTip').val('0');
	}
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
function fetchWeighmentDetails(){
	var action = "fetchWeighmentDetails";
	var tankerName = $('[name=tankerName]').val();
	var dataString = {"tankerName": tankerName};
	var displayScreen = $('[name="displayScreen"]').val();
	$.ajax({
         type: "POST",
         url: action,
         data: dataString,
         dataType: 'json',
         success: function(result) { 
           if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){    
           			
           			var displayScreen = $('[name=displayScreen]').val();
           			if(displayScreen == "VEHICLE_IN"){
           				$('#sendDate').val('');
           				$('#sendTime').val('');
           				$('#dcNo').val('');
           				$('#productId').val('');
           				$('#product').val('');	
           				$('#partyId').val('');	
           			}
           			if(displayScreen == "VEHICLE_GRSWEIGHT"){
           				$('#grossWeight').val('');
           				$('#grossWeight').removeAttr("readonly");
           				$('#partyId').val('');
           				$('#dcNo').val('');
           			}
           			if(displayScreen == "VEHICLE_TAREWEIGHT"){
           				$('#tareWeight').val('');
           				$('#tareWeight').removeAttr("readonly");
           			}
           			if(displayScreen == "VEHICLE_IN"){
           				$('#sendDate').val('');
           				$('#sendTime').val('');
           				$('#dcNo').val('');
           				$('#productId').val('');
           				$('#product').val('');	
           				$('#partyId').val('');	
           			}
           			
           			
           			$('span#tankerIdToolTip').removeClass("tooltip");
	  				$('span#tankerIdToolTip').addClass("tooltipWarning");
	  				$('span#tankerIdToolTip').html('none');
	  				$('span#partyIdFromToolTip').removeClass("tooltip");
	  				$('span#partyIdFromToolTip').addClass("tooltipWarning");
	  				$('span#partyIdFromToolTip').html('none');
	  				           	   
           }else{
           		var  weighmentId= result['weighmentId'];
           		var displayScreen = $('[name=displayScreen]').val();
           		var  weighmentDetails= result['weighmentDetails'];
           		var  weighmentItems = [];
           		weighmentItems=result['weighmentItemDetails'];
           		itemsList = result['weighmentItemDetails'];
           		if(typeof(weighmentItems)!= "undefined" && weighmentItems!='' && weighmentItems != null ){
           			productsCount = weighmentItems.length;
	           		$("#productsTable").append('<tr><td></td><td align="right"><u><span class="h2">UN LOADED PRODUCTS LIST</span></u></td></tr>');
	           		$("#productsTable").append('<tr><td></td><td><u><span class="h4"><b>NAME</b></span></u></td><td><u><span class="h4"><b>DISP WEIGHT</b></span></u></td>&nbsp;<td><u><span class="h4"><b>GRS WEIGHT</b></span></u></td>&nbsp;<td><u><span class="h4"><b>TR WEIGHT</b></span></u></td><td><u><span class="h4"><b>QUANTITY</b></span></u></td></tr>');
	           		for(var i=0 ; i<weighmentItems.length ; i++){
							var innerList=weighmentItems[i];
							var prodName = 	innerList['prodName'];
							var itemDispatchWeight = "";
							var itemGrsWeight = "";
							var itemTareWeight = "";
							var qty = 0;
							if(typeof(innerList['dispatchWeight'])!= "undefined" && innerList['dispatchWeight']!='' && innerList['dispatchWeight'] != null ){
								itemDispatchWeight=innerList['dispatchWeight'];
							}
							if(typeof(innerList['grossWeight'])!= "undefined" && innerList['grossWeight']!='' && innerList['grossWeight'] != null ){
								itemGrsWeight=innerList['grossWeight'];
								grsWeight = parseInt(itemGrsWeight);
								qty = parseInt(itemGrsWeight);
							}
							if(typeof(innerList['tareWeight'])!= "undefined" && innerList['tareWeight']!='' && innerList['tareWeight'] != null ){
								itemTareWeight=innerList['tareWeight'];
                                if(parseInt(itemTareWeight)>0){
									qty = qty - parseInt(itemTareWeight);
								}else{
									qty= 0;
								}
							}
							
	                    $("#productsTable").append('<tr><td></td><td><span id="random_"+i+"" class="tooltip h4">'+prodName+'</span></td><td><span id="rdmDisptch_"+i+"" class="tooltip h4">'+itemDispatchWeight+'</span></td><td><span id="rdmgrs_"+i+"" class="tooltip h4">'+itemGrsWeight+'</span></td><td><span id="rdmtare_"+i+"" class="tooltip h4">'+itemTareWeight+'</span></td><td><span id="rdmQty_"+i+"" class="tooltip h4">'+qty+'</span></td></tr>');	
					}
				}	
           	    if(displayScreen != "VEHICLE_OUT"){
		   			if(typeof(weighmentDetails) != 'undefined'){
	   					var trnStatusId =  weighmentDetails['statusId'];
	   					if(typeof(trnStatusId) != 'undefined'){
	   						if(trnStatusId == 'MXF_REJECTED'){
	   							alert('This Receipt is rejected . you cannot update further.');
	   						}
	   					}
       			    }
	   			}
           		
	   			if(displayScreen == "VEHICLE_TAREWEIGHT"){
	   				grossWeight = result['grossWeight'];
	   				var tareweightVal = weighmentDetails['tareWeight']
           				if(typeof(tareweightVal)!= "undefined" && tareweightVal!='' && tareweightVal != null ){
           					$('[name=tareWeight]').val(tareweightVal);
           					$('#tareWeight').attr("readonly","readonly");
           					populateNetWeight();
           				}else{
           					$('#tareWeight').val('');
           					$('#tareWeight').removeAttr("readonly");
           				}
	   				
	   				var sendWeight = 0;
	   				
	   				
	   				var  weighmentDetails= result['weighmentDetails'];

	   				if(typeof(weighmentDetails) != 'undefined'){
	   					sendWeight = weighmentDetails['dispatchWeight'];
	   				}
	   				$('#grossWeightToolTip').val(grossWeight);
	   				$('#sendWeightToolTip').val(sendWeight);
	   				var noOfProduct = weighmentDetails['noOfProducts'];
           				if(typeof(noOfProduct)!= "undefined" && noOfProduct!='' && noOfProduct!= null ){
           					$('#noOfProduct').val(noOfProduct);
           				}
	   			}
           
           		if($('[name=sealCheck]').length !=0){
           			if(displayScreen == "VEHICLE_GROSSWEIGHT"){
           				var grossweightVal = weighmentDetails['grossWeight'];
           				var noOfProduct = weighmentDetails['noOfProducts'];
           				if(typeof(noOfProduct)!= "undefined" && noOfProduct!='' && noOfProduct!= null ){
           					$('#noOfProduct').val(noOfProduct);
           				}
           				if(typeof(grossweightVal)!= "undefined" && grossweightVal!='' && grossweightVal != null ){
           					$('[name=grossWeight]').val(grossweightVal);
           					$('#grossWeight').attr("readonly","readonly");
           				}else{
           					$('#grossWeight').val('');
           					$('#grossWeight').removeAttr("readonly");
           				}
           				var isSealChecked = weighmentDetails['isSealChecked'];
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
	           		}
           		}
           		if($('[name=dcNo]').length !=0){
           			var dcNo = result['dcNo'];
           			if(typeof(dcNo)!= "undefined"){
           				$('[name=dcNo]').val(dcNo);
           				$('#dcNo').attr("readonly","readonly");
           			}
           		
           		}
           		if($('[name=sendDate]').length !=0){
           			var sendDate = result['sendDateStr'];
           			if(typeof(sendDate)!= "undefined"){
           				$('[name=sendDate]').val(sendDate);
           			}
           		
           		}
           		if($('[name=sendTime]').length !=0){
           			var sendTime = result['sendTimeStr'];
           			if(typeof(sendTime)!= "undefined"){
           				$('[name=sendTime]').val(sendTime);
           			}
           		
           		}
           		
           		var partyId = result['partyId'];
           		
           		var partyName = result['partyName'];
  				if(!partyName){
  					partyName = partyId;
  				}
  				partyName = partyName+' ['+partyId+']'
           		if(weighmentId){
           			$('[name = weighmentId]').val(weighmentId);
           			if(displayScreen != "VEHICLE_IN"){
           				$('span#tankerIdToolTip').addClass("tooltip");
	  					$('span#tankerIdToolTip').removeClass("tooltipWarning");
	  					$('span#tankerIdToolTip').html(weighmentId);
	  					
	  					$('span#partyIdFromToolTip').addClass("tooltip");
	  					$('span#partyIdFromToolTip').removeClass("tooltipWarning");
	  					$('span#partyIdFromToolTip').html(partyName);
           			}
           			var displayScreen = $('[name=displayScreen]').val();
           			if(displayScreen == "VEHICLE_IN"){
           				$('#sendDate').val('');
           				$('#sendTime').val('');
           				$('#dcNo').val('');
           				$('#productId').val('');
           				$('#product').val('');	
           				$('#partyId').val('');
           				alert('this tanker is already in process');
           				$('#tankerName').val('');
           				$('#tankerNo').val('');
           				$('#milkTransferId').val('');
           				populateVehicleSpan();	
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


function setVehicleId(selected){
	var vehicleId = selected.value;
	var check = confirm("Please Confirm The Vehicle No :"+vehicleId);
	 if (check == false) {
            return false;
        }
     $("#DetailsDiv").show();
     var selectedValue = vehicleId;
	$('[name=tankerName]').val(selectedValue);
	 populateVehicleSpan();
    $("#newVehicleDiv").hide();
}	
function hideDiv(){
	var displayScreen = $('[name="displayScreen"]').val()
 	if((displayScreen != "VEHICLE_IN")){
		$("#DetailsDiv").hide();
 	}
}	
function reloadingPage(){
	setTimeout("location.reload(true);", 1000);
}
</script>
<#if displayScreen != "VEHICLE_IN" >
<div id="newVehicleDiv" style="float: left;width: 90%; background:transparent;border: #F97103 solid 0.1em; valign:middle">
	<div class="screenlet" background:transparent;border: #F97103 solid 0.1em;> 
		<div class="grid-header h2" style="width:100%">
		<#if displayScreen == "VEHICLE_CIPNEW">
			<label>VEHICLES WAITING FOR CIP</label>
		<#elseif displayScreen == "VEHICLE_CIP">
   			<label>VEHICLES WAITING FOR UNLOAD</label>
   		<#elseif displayScreen == "VEHICLE_QC">
   			<label>VEHICLES WAITING FOR QC</label>	
   		<#elseif displayScreen == "VEHICLE_GROSSWEIGHT">	
   			<label>VEHICLES WAITING FOR GROSS WEIGHT</label>
		<#elseif displayScreen == "VEHICLE_TAREWEIGHT">	
   			<label>VEHICLES WAITING FOR TARE WEIGHT</label>
   		<#elseif displayScreen == "VEHICLE_OUT">	
   			<label>VEHICLES WAITING TO EXIT</label>		
        </#if>	
		</div>
	</div>
	<div class="screenlet-body">
	<form id="listPendingVehicles" name="listPendingVehicles" action="" method="post">
	<table class="basic-table hover-bar h3" widht='80%' style="border-spacing: 50px 2px;" border="1"> 
		<tr><td><h2><u>VEHICLE NO</u></h2></td>
		    <td><h2><u>IN TIME</u><h2></td>
			<td><h2><u> FROM<u><h2></td>
		</tr>
		 <#if vehicleList?has_content>
         <#list vehicleList as vehicle>
		<tr>
            <td><h2><input type="button" id="newVehicleId" name="newVehicleId"  value="${vehicle.vehicleId}" onclick="javascript:setVehicleId(this);"/></h2></td>
            <td><h3>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(vehicle.inTime, "dd-MM-yyyy HH:mm")}</h3></td>
	        <td><h3>${vehicle.partyId?if_exists}</h3></td>	
		</tr>
        </#list>
        <#else>
       <tr>
         <td><span class="h2">No Vehicles Available.</span></td>
       </tr>
        </#if>
	</table>
	</form>
	</div>
</div>
</#if>
<div id="wrapper" style="width: 90%; height:100%"></div>
<div name ="displayMsg" id="nonMilkReceiptEntry_spinner" style="width:30%;  height:40%"> </div>
<div id="DetailsDiv" style="float: left;width: 90%; background:transparent;border: #F97103 solid 0.1em; valign:middle">
	
	<div class="screenlet" background:transparent;border: #F97103 solid 0.1em;>      
      <div class="grid-header h2" style="width:100%">
      			<#assign velhicleStatus = "VEHICLE ENTRY DETAILS">
      			<#if displayScreen == "VEHICLE_OUT">
      				<#assign velhicleStatus = " VEHICLE OUT DETAILS">
      			</#if>
      			<#if displayScreen == "VEHICLE_GROSSWEIGHT">
      				<#assign velhicleStatus = "VEHICLE  GROSS WEIGHT DETAILS">
      			</#if>
      			<#if displayScreen == "VEHICLE_TAREWEIGHT">
      				<#assign velhicleStatus = "VEHICLE  TARE WEIGHT DETAILS">
      			</#if>
      			<#if displayScreen == "VEHICLE_QC">
      				<#assign velhicleStatus = "QUALITY CONTROL DETAILS">
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
    	<form method="post" name="nonMilkReceiptEntry"  id="nonMilkReceiptEntry" >
      	<table style="border-spacing: 0 10px;" border="1" >     
	        <tr>
			  <td>&nbsp;</td>
	          <td align='left' valign='middle' nowrap="nowrap">
	          <table>
	          		<tr>
	          			<table id="mainTable">
	          				<tr>
					        	<td align='left'><span class="h2">Vehicle No</span> </td><td>
                                    <#if displayScreen =="VEHICLE_IN">
					        	    <input  name="tankerName"  size="12pt" type="text" id="tankerName"  autocomplete="off" required="required" onblur="javascript:"populateVehicleSpan();"/><span class="tooltip h4" id ="tankerToolTip">none</span></td>
					        	    <#else>
                                     <input  name="tankerName"  size="12pt" type="text" id="tankerName"  autocomplete="off" readOnly "/><span class="tooltip h4" id ="tankerToolTip">none</span></td>
                                    </#if>
                                    <input  name="weighmentId" id="weighmentId" size="12pt" type="hidden"   autocomplete="off"/>
					        		<input  name="displayScreen" size="10pt" type="hidden" id="displayScreen" value="${displayScreen?if_exists}"/> 
					        	</td>
					        </tr>
					        <#if displayScreen == "VEHICLE_OUT">
							    <tr>
	        						<input  name="statusId" size="10pt" type="hidden" id="statusId" value="WMNT_VCL_OUT" />
	        						<td align='left' ><span class='h2'>Exit Date</span></td><td><input  type="text" size="15pt" id="exitDate" name="exitDate" autocomplete="off" required/></td>
	        					</tr>
	        					<tr>
	        						<td align='left' ><span class="h2">Exit Time(HHMM)[24 hour format]</span> </td><td><input  name="exitTime" size="10" class="onlyNumber" maxlength="4" type="text" id="exitTime" value="${setTime}" autocomplete="off" required/></td>
					        	</tr>
					        </#if>
					        <#if displayScreen !="VEHICLE_IN">
					        	<tr>
					        			<td id="displayRecievedFrom" align ="left"><span class="h2">From Party :</span></td><td> <span class="tooltip h2" id ="partyIdFromToolTip">none</span> </td>
					        	</tr>
					        	<tr>
								    <td align='left'><span class="h2">DC No </span></td><td><input  name="dcNo" size="12" maxlength="10" id= "dcNo" type="text" autocomplete="off"  required/><em>*<em></td>
								</tr>		
					        </#if>
					        	
					        <#if displayScreen == "VEHICLE_IN">
		                        <tr>
	        						<td align='left' ><span class="h2">Entry Date</span></td><td><input  type="text" size="15pt" id="entryDate" value="${setDate}" name="entryDate" autocomplete="off" required="required" readonly="readonly"/></td>
	        					</tr>
	        					<tr>
	        						<td align='left' ><span class="h2">Entry Time(HHMM)[24 hour format]</span> </td><td><input  name="entryTime" size="10" class="onlyNumber" maxlength="4" type="text" id="exitTime" value="${setTime}" autocomplete="off" required/></td>
					        	</tr>
	                         	<tr>
		        					<td align='left' ><span class="h2">Dispatch Date</span></td><td><input  type="text" size="15pt" id="sendDate" name="sendDate" autocomplete="off" required/></td>
		        					
		        				</tr>
		        				<tr>
		        					<td align='left' ><span class="h2">Dispatch Time(HHMM)[24 hour format]</span> </td><td><input  name="sendTime"  size="10" class="onlyNumber" maxlength="4" type="text" id="sendTime" autocomplete="off" required/>
		        					</td>
						        </tr>
						        <tr>
							       	<td align='left'><span class="h2">DC No</span> </td><td><input  name="dcNo" size="12" maxlength="10" id= "dcNo" type="text" autocomplete="off"  required/><em>*<em></td>
							    </tr>
							   <#--  <tr>
        							<td align='left' valign='middle' nowrap="nowrap"><span class="h2">Proudct </span></td><td>
						        		<@htmlTemplate.lookupField name="productId" id="productId" formName="nonMilkReceiptEntry" fieldFormName="LookupProduct"/>
									</td>
				        		</tr> -->
                                <tr>
                                	<td align='left' valign='middle' nowrap="nowrap"><span class="h2">No Of Products </span></td><td><input type="text" size="3pt" id="noOfProducts" name="noOfProducts" value="1" required="required"/></td>
                                </tr> 
	                         	<tr>
	                         		<td><span class="h2">From Party :</span></td><td>
	                         		<@htmlTemplate.lookupField  formName="nonMilkReceiptEntry" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>
	                   			</tr>
	                   			<tr>
	                   				<td><span class='h2'>To:</span></td><td><span class='h3'> MOTHER DAIRY</span><input type="hidden" size="6" id="partyIdTo" maxlength="6" name="partyIdTo" autocomplete="off" value="MD" /></td>
	                   			</tr>
	                   			<tr>
	                   				<td><span class='h2'>Seal check:</span></td><td> 
	                   				<input type="radio" name="sealCheck" id="sealCheckY" value="Y"/> YES   <input type="radio" name="sealCheck" id="sealCheckN" value="N"/> NO</td>
	                   			</tr>
		                   	</#if>		
					        <#if displayScreen == "VEHICLE_GROSSWEIGHT">	
					            <tr>
					            	<td align='left' ><span class="h2">No Of Products </span> </td><td><input  name="noOfProduct"  size="4"   type="text" id="noOfProduct" required="required"/></td>
					            </tr>
	        					<tr>
	        					<td>&nbsp;</td>
                                    <td>
                                    	<table style="border-spacing: 10px 0;" id="productsTable">
                                    	
                                    	</table>
                                    </td>
	        					</tr>
	        					<tr>
	        						<input  name="statusId" size="10pt" type="hidden" id="statusId" value="WMNT_VCL_GRSWEIGHT" />
	        						<td align='left' ><span class="h2">Gross Weight Date</span></td><td><input  type="text" size="15pt" id="grossDate" name="grossDate" value="${setDate}" autocomplete="off" /></td>
	        					</tr>
	        					<tr>
	        						<td align='left' ><span class="h2">Gross Weight Time(HHMM)[24 hour format]</span> </td><td><input  name="grossTime" value="${setTime}" size="10" class="onlyNumber" maxlength="4" type="text" id="grossTime" autocomplete="off" required/></td>
					        	</tr>
							    <tr>
	        						<td align='left' ><span class="h2">Product Dispatch Weight(Kgs)</span></td><td><input  type="text" size="15pt" id="dispatchWeight" name="dispatchWeight" autocomplete="off" required="required"/></td>
	        					</tr>
							    <tr>
	        						<td align='left' ><span class="h2"> Gross Weight(Kgs)</span> </td><td><input  type="text" size="15pt" id="grossWeight" name="grossWeight" autocomplete="off" required="required"/></td>
	        					</tr>
                                <tr>
        							<td align='left' valign='middle' nowrap="nowrap"><span class="h3">Proudct </span></td><td>
						        		<input type="text" size="15" id="productId"  name="productId" autocomplete="off" /><span class="tooltip" id ="productToolTip">none</span></td>
									</td>
				        		</tr>
                                <tr>
        							<td align='left' style="vertical-align:middle" ><span  class="h2">Description </span></td><td>
						        		<textarea cols="30" rows="5"   name="comments" id="comments" ></textarea>
									</td>
				        		</tr>
	        				<#--	<tr>
	        						<td align='left' ><span class="h2">Number of compartments</span></td><td><input  type="text" size="15pt" id="numberOfCells" name="numberOfCells" autocomplete="off" required="required"/></td>
	        					</tr>
	        					<tr>
							       	<td align='left'><span class="h2">Driver Name</span> </td><td><input  name="driverName" size="25" maxlength="20" id= "driverName" type="text" autocomplete="off"  required/><em>*<em></td>
							    </tr> -->
	        					<tr>
	                   				<td><span class='h2'>Seal check:</span></td><td> <input type="radio" name="sealCheck" id="sealCheckY" value="Y"/> YES   <input type="radio" name="sealCheck" id="sealCheckN" value="N"/> NO</td>
	                   			</tr>
	        				</#if>	
	        				<#if displayScreen == "VEHICLE_TAREWEIGHT">
                              <#--  <tr>
	        						<td align='left' ><span class="h2">Is Cip Checked</span></td>
	        						<td align='left'><input type="text" readOnly size="3pt" id="isCipChecked" name="isCipChecked" required/><em>*<em><span class="h4" id="isCipCheckedDes" name="isCipCheckedDes"/></td>
	        					</tr> 
	        					
	        					<tr>
	        						<td align='left' ><span class="h2">Dispatch Weight(Kgs)</span></td>
	        						<td><input  type="text" class="onlyNumber" size="15pt" id="sendWeightToolTip" autocomplete="off" value="0"/></td>
	        					</tr>
	        					<tr>
	        						<td align='left' ><span class="h2">Gross Weight(Kgs)</span></td>
	        						<td><input  type="text" class="onlyNumber" size="15pt" id="grossWeightToolTip" autocomplete="off" value="0"/></td>
	        					</tr> -->
                                <tr>
								<input type="hidden" size="3pt" id="noOfProduct" name="noOfProduct" />
	        					<td>&nbsp;</td>
                                    <td>
                                    	<table style="border-spacing: 10px 0;" id="productsTable">
                                    	
                                    	</table>
                                    </td>
	        					</tr>
	        					<tr>
	        						<input  name="statusId" size="10pt" type="hidden" id="statusId" value="WMNT_VCL_TAREWEIGHT" />
	        						<td align='left' ><span class="h2">Tare Weight Date</span></td><td><input  type="text" size="15pt" id="tareDate" name="tareDate" value="${setDate}" autocomplete="off" required/></td>
	        					</tr>
	        					<tr>
	        						<td align='left' ><span class="h2">Tare Time(HHMM)[24 hour format]</span> </td><td><input  name="tareTime" class="onlyNumber" value="${setTime}" size="10" maxlength="4" type="text" id="tareTime" autocomplete="off" required/></td>
					        	</tr>
							    <tr>
	        						<td align='left' ><span class="h2">Tare Weight(Kgs)</span></td><td><input  type="text" class="onlyNumber" size="15pt" id="tareWeight" name="tareWeight" autocomplete="off" required/></td>
	        					</tr>
	        				  <#--	<tr>
	        						<td align='left' ><span class="h2">Net Weight(Kgs)</span></td>
	        						
	        						<td><input  type="text" class="onlyNumber" size="15pt" id="netWeightToolTip" autocomplete="off" value="0"/></td>
	        					</tr> -->
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
	       
	      <#if displayScreen == "VEHICLE_TAREWEIGHT"> 
	        <td valign = "middle" align="center"></td>
   		    <td valign = "middle" align="center"></td>
	        <td>
	        	<div class='tabletext h2'>
	        	<#assign url = ""/>
	            <a class="buttontext" id="hrefSub" target="_BLANK" onclick="javascript: setUrl();">Report</a>
	      		</div>
	        </td>
	      </#if>
      	</tr>
      </table>
	       
   </form>
  </div>
 </div>
</div>
<script type='application/javascript'>
	function setUrl(){
		var weighmentId = $("#weighmentId").val();
		var urlStr = "<@ofbizUrl>NonMilkIncomingReport.pdf?weighmentId="+weighmentId+"</@ofbizUrl>"
		$("#hrefSub").attr("href",urlStr)
		setTimeout("location.reload(true);", 20000);
	}
</script>
