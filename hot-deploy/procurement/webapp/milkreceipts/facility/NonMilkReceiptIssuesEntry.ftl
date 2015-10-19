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
	jQuery(function($) {
	var dispatchDateFormat;
	
	if($('#displayScreen').val()=="ISSUE_OUT"){
		dispatchDateFormat = $('#exitDate').datepicker( "option", "dateFormat" );
		$('#exitDate').datepicker( "option", "dateFormat", "dd-mm-yy" );
	}
	if($('#displayScreen').val()=="ISSUE_INIT"){
		$('#displayRecord').hide();
		$('#displayRecievedFrom').hide();
		dispatchDateFormat = $('#tareDate').datepicker( "option", "dateFormat" );
		$('#tareDate').datepicker( "option", "dateFormat", "dd-mm-yy" );
	}
	if($('#displayScreen').val()=="ISSUE_TARWEIGHT"){
		dispatchDateFormat = $('#tareDate').datepicker( "option", "dateFormat" );
		$('#tareDate').datepicker( "option", "dateFormat", "dd-mm-yy" );
	}
	if($('#displayScreen').val()=="ISSUE_GRSWEIGHT"){
		dispatchDateFormat = $('#grossDate').datepicker( "option", "dateFormat" );
		$('#grossDate').datepicker( "option", "dateFormat", "dd-mm-yy" );
	}
});
var tareWeight = 0;
var itemsList = [];
$(document).ready(function() {	
	hideDiv();
  
  $('#tareWeight').autoNumeric({mDec: 2 ,mRound: 'A' ,	aSep:'' , autoTab : true}).trigger('focusout');
  $('#grossWeight').autoNumeric({mDec: 2 ,mRound: 'A' ,	aSep:'' , autoTab : true}).trigger('focusout');
  $('#dispatchWeight').autoNumeric({mDec: 2 ,mRound: 'A' ,	aSep:'' , autoTab : true}).trigger('focusout');

	
 	if($('#displayScreen').val()=="ISSUE_OUT"){
 		$('#dcNo').attr("readonly","readonly");
 		//makeDatePicker("exitDate","fromDate");
 	}
 	if($('#displayScreen').val()=="ISSUE_TARWEIGHT"){
 		//makeDatePicker("tareDate","fromDate");
 	}
 	if($('#displayScreen').val()=="ISSUE_INIT"){
 		//makeDatePicker("tareDate","fromDate");
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
		$('#noOfProducts').focusout(function (){
 			var noOfProducts = $('#noOfProducts').val();
            var rows = parseInt($('#addProductsTable tr').length);
           	if(rows!=0){
				for(i=0;i<=rows;i++){
					$("#addProductsTable tr:last-child").remove();
                 }
            }
 			for(i=0;i<parseInt(noOfProducts);i++){
 				var ival = i;
 				var idVal = "productId_o_"+ival; 
 				var spanVal = "span_"+idVal;
 				$("#addProductsTable").append('<tr><td><input type="text" class="productsLookup" size="15" id="'+idVal+'" name="'+idVal+'" onkeyup="javascript:populateProd(this);" required="required"/><span class="tooltip h4" id ="'+spanVal+'">none</span></td></tr>');
 			}
 		});
 		$('#noOfParties').focusout(function (){
 			var noOfParties = $('#noOfParties').val();
            var rows = parseInt($('#addPartyTable tr').length);
           	if(rows!=0){
				for(i=0;i<=rows;i++){
					$("#addPartyTable tr:last-child").remove();
                 }
            }
 			for(i=0 ;i<parseInt(noOfParties);i++){
				var ival = i;
 				var idVal = "partyId_o_"+ival; 
 				var spanVal = "span_"+idVal;
 				$("#addPartyTable").append('<tr><td><input type="text" size="15" id="'+idVal+'" name="'+idVal+'" onkeyup="javascript:populatePartyName(this);" required="required"/><span class="tooltip h4" id ="'+spanVal+'">none</span></td></tr>');
 			}
 		});
 	}
 	if($('#displayScreen').val()=="ISSUE_GRSWEIGHT"){
 		$('#dcNo').attr("readonly","readonly");
 		//makeDatePicker("grossDate","fromDate");
 		$('#grossWeight').focusout(function (){
    		if(tareWeight>0){
    			var checkVal = parseInt($('#grossWeight').val());
    			if(typeof(checkVal)!= "undefined" && checkVal!='' && checkVal != null ){
    				if(checkVal<=tareWeight){
    					alert("Please check Gross Weight.!");
    					$('#grossWeight').val('');
    					$('#netWeightToolTip').val('0');
    					return false;
    				}
    			}
    		}
		});
	/*	$('#productId').focusout(function(){
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
		});*/
 	}
 	
	$('#ui-datepicker-div').css('clip', 'auto');
	
	$("input").keyup(function(e){
	  		
	  		if(e.target.name == "grossWeight"){
	  			populateNetWeight();
	  		}
	  		if(e.target.name == "tankerName"){
	  			$('[name=tankerName]').val(($('[name=tankerName]').val()).toUpperCase());
	  			populateVehicleName();
				populateVehicleSpan();
	  		}
	  		if(e.target.name == "partyId"){
	  			$('[name=partyId]').val(($('[name=partyId]').val()).toUpperCase());
	  		}
	  		if(e.target.name == "productId"){
	  			$('[name=productId]').val(($('[name=productId]').val()).toUpperCase());
	  			populateProductNames();
				populateProductSpan();	
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
	  			if(e.which == 110 || e.which == 190){
    				$(this).val( $(this).val().replace('.',''));
	    		}
	  			$(this).val( $(this).val().replace(/[^0-9\.]/g,''));
	  			var prodCount = $('#noOfProducts').val(); 		
	  			   if(typeof(prodCount)!= "undefined" && prodCount!='' && prodCount != null && prodCount<1){
		   				alert("Please check no Of Products..!");
		   				$('#noOfProducts').val('1');
		   				return false;
	  			   }
	  		}
	  		if(e.target.name == "noOfParties"){
	  			if(e.which == 110 || e.which == 190){
    				$(this).val( $(this).val().replace('.',''));
	    		}
	  			$(this).val( $(this).val().replace(/[^0-9\.]/g,''));
	  			var prodCount = $('#noOfParties').val(); 		
	  			   if(typeof(prodCount)!= "undefined" && prodCount!='' && prodCount != null && prodCount<1){
		   				alert("Please check no Of Parties..!");
		   				$('#noOfParties').val('1');
		   				return false;
	  			   }
	  		}
	  		if(e.target.name == "product" ){
	  			var isReadOnly = $(this).attr('readonly');
	  			if(!isReadOnly){
	  				populateProductNames();
					populateProductSpan();
				}	  			
	  		}
	  		if(e.target.name == "dcNo"){
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
function populateProd(current){
	var code = $(current).val();
	var id= $(current).attr("id");
	var availableTags = ${StringUtil.wrapString(productItemsJSON)!'[]'};
		$("#"+id).autocomplete({					
			source:  availableTags,
			select: function(event, ui) {
					        var selectedValue = ui.item.value;
					        $("#"+id).val(selectedValue);
					        populateProdSpan(id);
					    }
		});
}
function populateProdSpan(id){
    var id=id;
    var spanId = "span_"+id;
    var code = $("#"+id).val();
	var noOfProducts = parseInt($("#noOfProducts").val());
	var prodCount =0;
    for(var i=0; i<noOfProducts;i++){
		if(code!= null && code != undefined) {
			var idVal = "productId_o_"+i; 
			var oldCode = $("#"+idVal).val();
			if(oldCode!= null && oldCode != undefined && code==oldCode ){
				prodCount = prodCount+1;
			}
		}
	}	
	if(prodCount>1){
		alert("This Product already entered.");
		$('#'+id).val('');
	}else{
	var productJson = ${StringUtil.wrapString(productJson)}
	var tempProductJson = productJson[$("#"+id).val()];
	if(tempProductJson){
		productName = tempProductJson["name"];
		$("#"+id).val($("#"+id).val());
		$("#"+spanId).html(productName);
	}else{
		$("#"+spanId).html('none');
	}
}
}
function populatePartyName(current){
	var code = $(current).val();
	var id= $(current).attr("id");
 	var availableTags = ${StringUtil.wrapString(partyJSON)!'[]'};
		$("#"+id).autocomplete({					
			source:  availableTags,
			select: function(event, ui) {
					        var selectedValue = ui.item.value;
					        $("#"+id).val(selectedValue);
					        populatePartySpan(id);
					    }
		});
}
function populatePartySpan(id){
	var id=id;
	var idValue= $("#"+id).val();
    var spanId = "span_"+id;
	var code = $("#"+id).val();
	var noOfParties = parseInt($("#noOfParties").val());
	var partyCount =0;
    for(var i=0; i<noOfParties;i++){
		if(code!= null && code != undefined) {
			var idVal = "partyId_o_"+i; 
			var oldCode = $("#"+idVal).val();
			if(oldCode!= null && oldCode != undefined && code==oldCode ){
				partyCount = partyCount+1;
			}
		}
	}	
	if(partyCount>1){
		alert("This Party already entered.");
		$('#'+id).val('');
	}else{
	var partyNameJson = ${StringUtil.wrapString(partyNameObj)}
	if(partyNameJson){
		Name = partyNameJson[idValue];
		$("#"+id).val($("#"+id).val());
		$("#"+spanId).html(Name);
	}else{
		$("#"+spanId).html('none');
	}
	}
}
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
	  				$('span#partyToolTip').removeClass("tooltip");
	  				$('span#partyToolTip').addClass("tooltipWarning");
	  				$('span#partyToolTip').html('none');
	  				$('[name=partyIdTo]').val('');
	  				if(displayScreen == "ISSUE_GRSWEIGHT"){
	  					$('#grossWeight').val('');
       					$('#grossWeight').removeAttr("readonly");
	  				}
	  				if(displayScreen == "ISSUE_TARWEIGHT"){
	  					$('#weighmentId').val('');
	  					$('span#tankerToolTip').removeClass("tooltip");
	  					$('span#tankerToolTip').addClass("tooltipWarning");
	  					$('span#tankerToolTip').html('none');
	  					$('span#partyToolTip').removeClass("tooltip");
	  					$('span#partyToolTip').addClass("tooltipWarning");
	  					$('span#partyToolTip').html('none');
	  					$('[name=partyIdTo]').val('');
	  					
	  					$('#tareWeight').val('');
       					$('#tareWeight').removeAttr("readonly");
	  				}
	  				    	   
           			if(displayScreen == "ISSUE_GRSWEIGHT"){
           			    $('#purposeTypeId').val('');
           				$('span#purposeTypeToolTip').removeClass("tooltip");
						$('span#purposeTypeToolTip').addClass("tooltipWarning");
						$('span#purposeTypeToolTip').html('None');
           			}
           			
           			
           }else{
	           		var weighmentId= result['weighmentId'];
	           		var displayScreen = $('[name=displayScreen]').val();
		  		if(weighmentId){
		  			var  weighmentDetails= result['weighmentDetails'];
           			$('[name = weighmentId]').val(weighmentId);
           			if(displayScreen != "ISSUE_INIT"){
           				var partyToName = result['partyToName'];
           				$('span#tankerIdToolTip').addClass("tooltip");
	  					$('span#tankerIdToolTip').removeClass("tooltipWarning");
	  					$('span#tankerIdToolTip').html(weighmentId);
	  					
	  					$('span#partyToolTip').addClass("tooltip");
	  					$('span#partyToolTip').removeClass("tooltipWarning");
	  					$('span#partyToolTip').html(partyToName);
           			}
           			var displayScreen = $('[name=displayScreen]').val();
           			if(displayScreen == "ISSUE_INIT"){
           				$('#sendDate').val('');
           				$('#sendTime').val('');
           				$('#dcNo').val('');
           				$('#productId').val('');
           				$('#product').val('');	
           				$('#partyId').val('');
           				alert('this tanker is already in process');
           				$('#tankerName').val('');
           				$('#tankerNo').val('');
           				$('#weighmentId').val('');
           				populateVehicleSpan();	
           			}
           			if(displayScreen == "ISSUE_GRSWEIGHT" || displayScreen == "ISSUE_OUT"){
           				var dcNo = weighmentDetails['dcNo'];
           				if(typeof(dcNo)!= "undefined" && dcNo!='' && dcNo!= null ){
           					$('#dcNo').val(dcNo);
							$('#generateDcNo').hide();
           				}
           			}
           			if(displayScreen == "ISSUE_GRSWEIGHT"){
	  					var noOfProduct = weighmentDetails['noOfProducts'];
           				if(typeof(noOfProduct)!= "undefined" && noOfProduct!='' && noOfProduct!= null ){
           					$('#noOfProduct').val(noOfProduct);
           				}
           				var  weighmentItems = [];
           		weighmentItems=result['weighmentItemDetails'];
           		itemsList = result['weighmentItemDetails'];
           		if(typeof(weighmentItems)!= "undefined" && weighmentItems!='' && weighmentItems != null ){
           			var optionList = '';
           			var productsCount =0;
		 				optionList += "<option value = " + "" + " >" +" "+ "</option>";
						var list= weighmentItems;
						if (list) {		       				        	
		        		for(var i=0 ; i<list.length ; i++){
							var innerLis=list[i];	 
							var grossWeight=innerLis['grossWeight'];
							//alert(grossWeight);
							if(grossWeight==0){
		                		optionList += "<option value = " + innerLis['productId'] + " >" +innerLis['prodName']+" </option>";     
		                	}else{
		                		productsCount= productsCount+1;
		                	} 
		      			}//end of main list for loop
		     			$("#productId").html(optionList);
		     			//alert(productsCount);
		     			$("#productsCount").val(productsCount);
           			    }
	           		$("#productsTable").append('<tr><td></td><td align="right"><u><span class="h2">LOADED PRODUCTS LIST</span></u></td></tr>');
	           		$("#productsTable").append('<tr><td></td><td><u><span class="h4"><b>NAME</b></span></u></td>&nbsp;<td><u><span class="h4"><b>GRS WEIGHT</b></span></u></td>&nbsp;<td><u><span class="h4"><b>TR WEIGHT</b></span></u></td><td><u><span class="h4"><b>QUANTITY</b></span></u></td></tr>');
	           		for(var i=0 ; i<weighmentItems.length ; i++){
							var innerList=weighmentItems[i];
							var prodName = 	innerList['prodName'];
							var itemDispatchWeight = "";
							var itemGrsWeight = "";
							var itemTareWeight = "";
							var qty = 0;
							if(typeof(innerList['grossWeight'])!= "undefined" && innerList['grossWeight']!='' && innerList['grossWeight'] != null ){
								itemGrsWeight=innerList['grossWeight'];
								qty = parseInt(itemGrsWeight);
							}
							if(typeof(innerList['tareWeight'])!= "undefined" && innerList['tareWeight']!='' && innerList['tareWeight'] != null ){
								itemTareWeight=innerList['tareWeight'];
								tareWeight = parseInt(itemTareWeight);
								$('#tareWeight').val(tareWeight);
                                if(parseInt(itemTareWeight)>0){
									qty = qty - parseInt(itemTareWeight);
								}else{
									qty= 0;
								}
							}
							if(typeof(itemGrsWeight)!= "undefined" && itemGrsWeight!='' && itemGrsWeight != null && itemGrsWeight>0){
	                    		$("#productsTable").append('<tr><td></td><td><span id="random_"+i+"" class="tooltip h4">'+prodName+'</span></td><td><span id="rdmgrs_"+i+"" class="tooltip h4">'+itemGrsWeight+'</span></td><td><span id="rdmtare_"+i+"" class="tooltip h4">'+itemTareWeight+'</span></td><td><span id="rdmQty_"+i+"" class="tooltip h4">'+qty+'</span></td></tr>');
	                        }	
					}
				}
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
        	 		
        	 		$('span#tankerToolTip').removeClass("tooltip");
	  				$('span#tankerToolTip').addClass("tooltipWarning");
	  				$('span#tankerToolTip').html('none');
        	 } 
          
    });
}
function populateNetWeight(){
	var grossWeight = $('[name=grossWeight]').val();
	if(typeof(grossWeight)!= "undefined" ){	
		var netWeight = grossWeight-tareWeight ;
		$('#netWeightToolTip').val(netWeight);
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
function setVehicleId(selected){
	var vehicleId = selected.value;
	var check = confirm("Please Confirm The Vehicle No :"+vehicleId);
	 if (check == false) {
            return false;
        }
     $("#DetailsDiv").show();
    $("#tankerNo").val(vehicleId);
     var selectedValue = vehicleId;
	$('[name=tankerName]').val(selectedValue);
	 populateVehicleSpan();
	 var displayScreen = $('[name="displayScreen"]').val();
	 if((displayScreen == "ISSUE_TARWEIGHT") || (displayScreen == "ISSUE_GRSWEIGHT")){
 		 populateWeighBridgeWeight();
 		}	
	
    $("#newVehicleDiv").hide();
}
function hideDiv(){
	var displayScreen = $('[name="displayScreen"]').val()
 	if((displayScreen != "ISSUE_INIT")){
		$("#DetailsDiv").hide();
 	}
}	
function reloadingPage(){
	setTimeout("location.reload(true);", 1000);
}	

function generateDC(){
		var weighmentId = $('[name=weighmentId]').val();
		var action = "generateWeighmentDcNo";
		var dataString = {"weighmentId": weighmentId };
		$.ajax({
	         type: "POST",
	         url: action,
	         data: dataString,
	         dataType: 'json',
	         success: function(result) { 
	       		var dcNo	 = result['dcNo'];
	       		$('[name=dcNo]').val(dcNo);
	       		$('#dcNo').attr("readonly","readonly");
	       		$('#generateDcNo').hide();
	         },
	         error:function(){
	         	//$('#dcNo').removeAttr("readonly");
	         	$('#generateDcNo').show();
	         }
    	}); 
	}	
function populateWeighBridgeWeight(){
		var vehicleId = $('[name=tankerName]').val();
		var action = "getWeighBridgeWeight";
		var displayScreen = $('[name="displayScreen"]').val();
		var weighmentType ="";
 		if((displayScreen == "ISSUE_TARWEIGHT")){
 			weighmentType = "T";
 		}
 		if((displayScreen == "ISSUE_GRSWEIGHT")){
 			weighmentType = "G";
 		}	
		var dataString = {"vehicleId": vehicleId,
		                  "weighmentType":weighmentType};
		$.ajax({
	         type: "POST",
	         url: action,
	         data: dataString,
	         dataType: 'json',
	         success: function(result) { 
	         	var weight = result['weight'];
	         	var weighBridgeId = result['weighBridgeId'];
	         	$('#weighBridgeId').val(weighBridgeId);
	       		if((displayScreen == "ISSUE_TARWEIGHT") && (typeof(weighBridgeId)!= "undefined" && weighBridgeId!='' && weighBridgeId != null )){
 					$('[name=tareWeight]').val(weight);
	       			$('#tareWeight').attr("readonly","readonly");
 				}
 				if((displayScreen == "ISSUE_GRSWEIGHT") && (typeof(weighBridgeId)!= "undefined" && weighBridgeId!='' && weighBridgeId != null )){
 					$('[name=grossWeight]').val(weight);
	       			$('#grossWeight').attr("readonly","readonly");
 				}
	         },
	         error:function(){
	         	//$('#dcNo').removeAttr("readonly");
	         	//$('#generateDcNo').show();
	         	alert("Error getting weight");
	         }
    	}); 
}	
</script>
<#if displayScreen != "ISSUE_INIT" >
<div id="newVehicleDiv" style="float: left;width: 90%; background:transparent;border: #F97103 solid 0.1em; valign:middle">
	<div class="screenlet" background:transparent;border: #F97103 solid 0.1em;> 
		<div class="grid-header h2" style="width:100%">
		<#if displayScreen == "ISSUE_GRSWEIGHT">	
   			<label>VEHICLES WAITING FOR GROSS WEIGHT</label>
		<#elseif displayScreen == "ISSUE_TARWEIGHT">	
   			<label>VEHICLES WAITING FOR TARE WEIGHT</label>
   		<#elseif displayScreen == "ISSUE_OUT">	
   			<label>VEHICLES WAITING TO EXIT</label>		
        </#if>	
		</div>
	</div>
	<div class="screenlet-body">
	<form id="listPendingVehicles" name="listPendingVehicles" action="" method="post">
	<table class="basic-table hover-bar h3" widht='80%' style="border-spacing: 50px 2px;" border="1"> 
		<tr><td><h2><u>VEHICLE NO</u></h2></td>
		    <td><h2><u>INITIATION TIME</u><h2></td>
			<td><h2><u> TO <u><h2></td>
		</tr>
		 <#if vehicleList?has_content>
         <#list vehicleList as vehicle>
		<tr>
            <td><h2><input type="button" id="newVehicleId" name="newVehicleId"  value="${vehicle.vehicleId}" onclick="javascript:setVehicleId(this);"/></h2></td>
            <td><h3>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(vehicle.inTime, "dd-MM-yyyy HH:mm")}</h3></td>
	        <td><h4>${vehicle.partyId?if_exists}</h4></td>	
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
<div name ="displayMsg" id="nonMilkReceiptIssuesEntry_spinner" style="width:30%;  height:40%"> </div>
<div id="DetailsDiv" style="float: left;width: 90%; background:transparent;border: #F97103 solid 0.1em; valign:middle">
	
	<div class="screenlet" background:transparent;border: #F97103 solid 0.1em;>      
      <div class="grid-header h2" style="width:100%">
      			<#assign velhicleStatus = displayScreen+ "  ::VEHICLE ENTRY DETAILS">
      			<#if displayScreen=="ISSUE_INIT">
      				<#assign velhicleStatus = "VEHICLE INITIATION DETAILS">
      			</#if>
      			<#if displayScreen == "ISSUE_OUT">
      				<#assign velhicleStatus = " VEHICLE OUT DETAILS">
      			</#if>
      			<#if displayScreen == "ISSUE_GRSWEIGHT">
      				<#assign velhicleStatus = "VEHICLE  GROSS WEIGHT DETAILS">
      			</#if>
      			<#if displayScreen == "ISSUE_TARWEIGHT">
      				<#assign velhicleStatus = "VEHICLE  TARE WEIGHT DETAILS">
      			</#if>
				<label>${velhicleStatus}</label>
	  		</div>
    </div>
	<div class="screenlet-body">
	    <#assign setDate = (Static["org.ofbiz.base.util.UtilDateTime"].toDateString(Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp(), "dd-MM-yyyy")).replace(':','')>
		<#assign setTime = (Static["org.ofbiz.base.util.UtilDateTime"].toDateString(Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp(), "HH:mm")).replace(':','')>
    	<form method="post" name="nonMilkReceiptIssuesEntry"  id="nonMilkReceiptIssuesEntry" >
      	<table style="border-spacing: 0 10px;" border="1">     
	        <tr>
			  <td>&nbsp;</td>
	          <td align='left' valign='middle' nowrap="nowrap">
	          <table>
	          		<tr>
	          			<table>
	          				<tr>
					        	<td align='left'><span class="h3">Vehicle No</span> </td><td width="100%" >
					        		<#if displayScreen == "ISSUE_INIT" >
					        	    <input  name="tankerName"  size="12pt" type="text" id="tankerName"  autocomplete="off" required="required" onblur="javascript:"populateVehicleSpan();"/><span class="tooltip h4" id ="tankerToolTip">none</span></td>
					        	    <#else>
                                     <input  name="tankerName"  size="12pt" type="text" id="tankerName"  autocomplete="off" readOnly "/><span class="tooltip h4" id ="tankerToolTip">none</span></td>
                                    </#if>

                                    <input  name="weighmentId" id="weighmentId" size="12pt" type="hidden"   autocomplete="off"/>
					        		<input  name="displayScreen" size="10pt" type="hidden" id="displayScreen" value="${displayScreen?if_exists}"/> 
					        	</td>
					        </tr>
					        <#if displayScreen == "ISSUE_OUT">
							    <tr>
	        						<input  name="vehicleStatusId" size="10pt" type="hidden" id="statusId" value="WMNT_ISSUE_VCL_OUT" />
	        						<td align='left' ><span class='h3'>Exit Date</span></td><td><input  type="text" size="15pt" id="exitDate" value="${setDate}" name="exitDate" autocomplete="off" required readOnly/></td>
	        					</tr>
	        					<tr>
	        						<td align='left' ><span class="h3">Exit Time(HHMM)[24 hour format]</span> </td><td><input  name="exitTime" size="10" class="onlyNumber" maxlength="4" type="text" id="exitTime" value="${setTime}" autocomplete="off" required readOnly/></td>
					        	</tr>
                                <tr>
							       	<td align='left'><span class="h3">DC No</span> </td><td><input  name="dcNo" size="12" maxlength="10" id= "dcNo" type="text" autocomplete="off" readOnly  required/><em>*<em></td>
							    </tr>
					        </#if>
					        <#if displayScreen !="ISSUE_INIT">
					        	<tr>
					        			<td id="displayRecievedFrom" align ="left"><span class="h3">To Party / Parties </span></td><td> <span class="tooltip h2" id ="partyToolTip">none</span> </td>
					        	</tr>
					        </#if>
					        <#if displayScreen !="ISSUE_INIT" || displayScreen !="ISSUE_OUT" >
					        	<input  name="weighBridgeId" size="10pt" type="hidden" id="weighBridgeId" />
					        </#if>
					        <#if displayScreen == "ISSUE_GRSWEIGHT">	
							    
							    <tr>
	        						<input  name="vehicleStatusId" size="10pt" type="hidden" id="statusId" value="WMNT_ISSUE_VCL_GRS" />
	        						<td align='left' ><span class="h3">Gross Weight Date</span></td><td><input  type="text" size="15pt" id="grossDate" name="grossDate" value="${setDate}" autocomplete="off" readOnly /></td>
	        					</tr>
	        					<tr>
	        						<td align='left' ><span class="h3">Gross Weight Time(HHMM)[24 hour format]</span> </td><td><input  name="grossTime" value="${setTime}" size="10" class="onlyNumber" maxlength="4" type="text" id="grossTime" autocomplete="off" readOnly required/></td>
					        	</tr>
                                <tr>
					            	<td align='left' ><span class="h3">No Of Products </span> </td><td><input  name="noOfProduct"  size="4"   type="text" id="noOfProduct" required="required" readOnly/></td>
					            </tr>
	        					<tr>
	        					<td>&nbsp;</td>
                                    <td>
                                    	<table style="border-spacing: 10px 0;" id="productsTable">
                                    	
                                    	</table>
                                    </td>
	        					</tr>
	        					<tr>
	        						<td align='left' ><span class="h3">Tare Weight(Kgs)</span></td><td><input  type="text" class="onlyNumber" size="15pt" id="tareWeight" name="tareWeight" autocomplete="off" readOnly required/></td>
	        					</tr>
					        	<tr>
        							<td align='left' valign='middle' nowrap="nowrap"><span class="h3">Proudct </span></td>
        							<#--<td>
						        		<input type="text" size="15" id="productId"  name="productId" autocomplete="off" /><span class="tooltip" id ="productToolTip">none</span></td>
									</td>-->
                                    <input type="hidden" id="productsCount" name="productsCount"/> 
                                    <td><select id="productId" name="productId"  required="required" onchange="javascript: populateNetWeight();">
                                    
                                    </select></td>
				        		</tr>
							    <tr>
	        						<td align='left' ><span class="h3"> Gross Weight(Kgs)</span> </td><td><input  type="text" size="15pt" id="grossWeight" name="grossWeight" autocomplete="off" required="required"/></td>
	        					</tr>
	        					 <tr>
							       	<td align='left'><span class="h3">DC No</span> </td><td><input  name="dcNo" size="12" maxlength="10" id= "dcNo" type="text" autocomplete="off"  required/><em>*<em>
							       	 <#if  displayScreen =="ISSUE_GRSWEIGHT"><input type="button" id="generateDcNo" name="generateDcNo"  value="Generate DcNo" onclick="javascript: generateDC();"/></td></#if>
							    </tr>
	        					<tr>
        							<td align='left' style="vertical-align:middle" ><span  class="h2">Description </span></td><td>
						        		<textarea cols="30" rows="5"   name="comments" id="comments" ></textarea>
									</td>
				        		</tr>
	        					<tr>
	        						<td align='left' ><span class="h3">Net Weight(Kgs)</span></td>
	        						
	        						<td><input  type="text" class="onlyNumber" size="15pt" id="netWeightToolTip" autocomplete="off" value="0" readonly='readonly'/></td>
	        					</tr>
	        				</#if>	
	        				<#if displayScreen == "ISSUE_INIT" >
                                
                                <tr>
	        						<input  name="vehicleStatusId" size="10pt" type="hidden" id="vehicleStatusId" value="WMNT_ISSUE_VCL_INIT" />
	        						<td align='left' ><span class="h3">Issue Date</span></td><td><input  type="text" size="15pt" id="tareDate" name="tareDate" value="${setDate}" autocomplete="off" required="required" readonly="readonly"/></td>
	        					</tr>
	        					<tr>
	        						<td align='left' ><span class="h3">Issue Time(HHMM)[24 hour format]</span> </td><td><input  name="tareTime" class="onlyNumber" value="${setTime}" size="10" maxlength="4" type="text" id="tareTime" autocomplete="off" readOnly  required/></td>
					        	</tr>
                                <tr>
                                	<td align='left' valign='middle' nowrap="nowrap"><span class="h3">No Of Products </span></td><td><input type="text" size="3pt" id="noOfProducts" name="noOfProducts"  required="required"/></td>
                                </tr> 
                                <#--<tr>
	                         		<td><span class="h3">To Party </span></td><td>
	                         		<@htmlTemplate.lookupField  formName="nonMilkReceiptIssuesEntry" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>
	                   			</tr> -->
	        					<tr>
	                   				<td><span class='h3'>From  </span></td><td><span class='h3'> MOTHER DAIRY</span><input type="hidden" size="6" id="partyIdFrom" maxlength="6" name="partyIdFrom" autocomplete="off" value="MD" /></td>
	                   			</tr>
                                <tr><td><span class='h3'>Products  </span></td><td>
                                <table id="addProductsTable"></table></td>
                                </tr>
	                   			<tr><td><span class='h3'>No of Parties  </span></td><td><input type="text" id="noOfParties" size="3pt" name="noOfParties" required="required"/></td></tr>
                                <tr><td><span class='h3'>To Party </span></td><td>
                                <table id="addPartyTable"></table></td>
                                </tr>
	        				</#if>
	        				<#if displayScreen == "ISSUE_TARWEIGHT" >
	        					<tr>
	        						<input  name="vehicleStatusId" size="10pt" type="hidden" id="vehicleStatusId" value="WMNT_ISSUE_VCL_TARE" />
	        						<td align='left' ><span class="h3">Tare Weight Date</span></td><td><input  type="text" size="15pt" id="tareDate" name="tareDate" value="${setDate}" autocomplete="off" readOnly required/></td>
	        					</tr>
	        					<tr>
	        						<td align='left' ><span class="h3">Tare Time(HHMM)[24 hour format]</span> </td><td><input  name="tareTime" class="onlyNumber" value="${setTime}" size="10" maxlength="4" type="text" id="tareTime" autocomplete="off" readOnly required/></td>
					        	</tr>
	        					<tr>
	        						<td align='left' ><span class="h3">Tare Weight(Kgs)</span></td><td><input  type="text" class="onlyNumber" size="15pt" id="tareWeight" name="tareWeight" autocomplete="off" required/></td>
	        					</tr>
	        				</#if>
                        </table>  
	        </tr>
      </table>
      <table>
      	<tr>
      		<td>&nbsp;</td><td>&nbsp;</td> <td>&nbsp;</td><td>&nbsp;</td>
      		<td>&nbsp;</td><td>&nbsp;</td> <td>&nbsp;</td><td>&nbsp;</td>
      		<td>&nbsp;</td><td>&nbsp;</td> <td>&nbsp;</td><td>&nbsp;</td>
	      	<td valign = "middle" align="center">
	      	<div class='tabletext h1'>
	 			<input type="submit" align="right"  class="button" name="submitButton"  id="submitEntry" <#if displayScreen == "ISSUE_INIT">value="Create"<#else>value="Update"</#if>/>      
	      		</div>
	      	</td>
	      	<#if displayScreen == "ISSUE_GRSWEIGHT"> 
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