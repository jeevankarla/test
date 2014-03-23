var screenFlag;
$(document).ready(function() {
	
        $("#wizard-2").steps({
            headerTag: "h3",
            bodyTag: "section",
            transitionEffect: "slideLeft",
            onStepChanging: function (event, currentIndex, newIndex)
            {  
            if(currentIndex==0){
            	//var result=getRouteAndShipmentVehicle();
            	callIssuanceTotal();
            }
            if(currentIndex==1||newIndex==2){
            		 var quantity=$("#quantity").val();
	            	if( (quantity).length < 1 ) {
	                    $('#quantity').css('background', 'yellow'); 
	                       setTimeout(function () {
	                           $('#quantity').css('background', 'white').focus(); 
	                       }, 1000);
	                    return false;
	                   }
           	    submitReturnCrate();
             }
                return true;
            },
            onFinishing: function (event, currentIndex)
            {
                return true;
            },
            onFinished: function (event, currentIndex)
            {
                submitFinalizeOrder();
            }
        });
	

	
	$( "#effectiveDateTemp" ).datepicker({
		dateFormat:'d MM, yy',
		changeMonth: true,
		numberOfMonths: 1,
		onSelect: function( selectedDate ) {
			$( "#effectiveDateTemp" ).datepicker("option", selectedDate );
		}
	});
	$('#ui-datepicker-div').css('clip', 'auto');
	screenFlag = $("#screenFlag").val();
	
	$("input").keypress(function(e){
		if(screenFlag == "returns"){
			if (e.which == $.ui.keyCode.ENTER) {
				if(e.target.name == "boothId" ){
					if( typeof $("#boothId").val() != "undefined"   && $("#boothId").val() != ""){ 
						updateGrid();
					}					
				}
				e.stopPropagation();
		      	e.preventDefault(); 
			}
		}
		else{
			if (e.which == $.ui.keyCode.ENTER) {
				if(e.target.name == "routeId" ){
					if( typeof $("#routeId").val() != "undefined"   && $("#routeId").val() != ""){ 
						updateGrid();
					}					
				}
				e.stopPropagation();
		      	e.preventDefault(); 
			}
		}
	});
	
});

$(function() {
       $('input[name=changeSave]').click(function (event){
    	   if(!$("#reconcilEntryInit").validate({messages:{
    		   routeId:"" 
    	   }}).form()) return;
    	   _grid.getEditController().commitCurrentEdit();
    	   $('input[name=changeSave]').attr('disabled','disabled');
    	   $('div#changeIndentEntry_spinner').removeClass("errorMessage");
    	   $('div#changeIndentEntry_spinner')
    		  .html('<img src="/images/ajax-loader64.gif">');
    	   	   var action;
    	   	   if(screenFlag == "returns"){
    	   		   action = "processReturnItemsMISAjax";
    	   	   }
    	   	   else{
    	   		   action = "processDispatchReconcilMISAjax";
    	   	   }
    	   	   var dataString = prepareAjaxDataString();
               $.ajax({
             type: "POST",
             url: action,
             data: dataString,
             dataType: 'json',
             success: function(result) {
            	 
               if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){            	  
            	   alert(result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]);
               }else{
            	   $("div#changeIndentEntry_spinner").fadeIn();
            	   $('div#changeIndentEntry_spinner').html();
            	   $('div#changeIndentEntry_spinner').addClass("messageStr");
               	   $('div#changeIndentEntry_spinner').html('<span style="color:green; font-size:10pt; font-stlye:bold">Succesfully added Dispatch Reconsiliation Entry for Route : "'+$("#routeTooltip").text()+'"</span>');
               	   $('div#changeIndentEntry_spinner').delay(7000).fadeOut('slow');
               }
               cleanUpGrid();
               $("#routeId").focus();
               $('input[name=changeSave]').removeAttr('disabled');
              
             } ,
             error: function() {
            	 	alert(result["_ERROR_MESSAGE_"]);
            	 }
             });   
              
               return false;
       });
       
       
       $("#routeIdTemp").autocomplete({ source: routesList }).keydown(function(e){
    	   if(screenFlag != "returns"){
				if (e.keyCode === 13){
			      	 $('#routeIdTemp').autocomplete('close');
			      	 if( typeof $("#routeIdTemp").val() != "undefined"   && $("#routeIdTemp").val() != ""){ 
			      		updateGrid();
			      	 }
					return false;
				}
       		}
		});
       $("#boothId").autocomplete({ source: boothsList }).keydown(function(e){
			if (e.keyCode === 13){
		      	 $('#boothId').autocomplete('close');
		      	 if( typeof $("#boothId").val() != "undefined"   && $("#boothId").val() != ""){ 
		      		updateGrid();
		      	 }
				return false;
			}
		});
       $('#boothId').keypress(function (e) {
			$("#boothId").autocomplete({ source: boothsList});	
       });
       // booth auto Complete
      
     $('#routeId').keyup(function (e) {
	 	if (e.keyCode == 8 || e.keyCode == 46) {
	 		updateGrid1([]); 
	 		jQuery("#routeId").focus(); 
	      }	
	 });
     //if(screenFlag != "returns"){
     // route auto Complete
	     $('#routeId').keypress(function (e) {  
				$("#routeId").autocomplete({ source: routesList , select: function( event, ui ) {
					$('span#routeTooltip').html('<label>'+ui.item.value+'</label>');
				} });	
		 });
     
	  // route auto Complete
	     $('#routeIdTemp').focus(function (e) {    	 	
				$("#routeIdTemp").autocomplete({ source: routesList });	
		 });
     //}
});

/*function getRouteAndShipmentVehicle(){ 
		if(!$('[name=routeIdTemp]').val()){
			return false;
		}
		var action = "getRouteVehicleForShipment";	
		var dataJson = {"supplyDate":$('[name=effectiveDateTemp]').val(),
							"routeId" : $('[name=routeIdTemp]').val(),
							"returnHeaderTypeId": $("#returnHeaderTypeIdTemp").val(),
							"subscriptionTypeId": $("#subscriptionTypeId").val(),
						};
			$.ajax({
					 type: "POST",
		             url: action,
		             data: dataJson,
		             dataType: 'json',
					 success:function(result){
						if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
							// alert(result["_ERROR_MESSAGE_"]);
							 return false;
							
						}else{
							var vehicleId = result["vehicleId"];
							var shipmentId=result["shipmentId"];
							
							$('[name=effectiveDate]').val($('[name=effectiveDateTemp]').val());
							$('[name=estimatedShipDate]').val($('[name=effectiveDateTemp]').val());//for finalization
							$('[name=tripId]').val($('[name=tripIdTemp]').val());
							$('[name=routeId]').val($('[name=routeIdTemp]').val());
							//$("div.id_100 select").val("val2");
							$("#returnHeaderTypeId select").val($("#returnHeaderTypeIdTemp").val());
							$('[name=vehicleId]').val(vehicleId);
							$('[name=shipmentId]').val(shipmentId);
							
						}								 
					},
					error: function(){
						alert("record not found");
					}							
				});
	}*/
   


function callIssuanceTotal(){ 
	if(!$('[name=routeId]').val()){
		return;
	}
	var shipmentId=$('[name=shipmentId]').val();
	var action = "getIssuanceTotal";	
	var dataJson = {"shipmentIds":shipmentId,
					};
		$.ajax({
				 type: "POST",
	             url: action,
	             data: dataJson,
	             dataType: 'json',
				 success:function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
						 $('div#changeIndentEntry_spinner').html('');
						 alert(result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]);
						
					}else{
						var crateTotal = result["crateTotal"];
						if(typeof crateTotal !== "undefined"){
							$('[name=quantity]').val(crateTotal);
						}
						
					}								 
				},
				error: function(){
					alert("record not found");
				}							
			});
	
	
}


function submitReturnCrate(){ 
	if(!$('[name=routeId]').val()){
		return;
	}
	var action = "createReturnCrate";	
	var dataJson = {"shipmentId":$('[name=shipmentId]').val(),
					"productStoreId" : $('[name=productStoreId]').val(),
					"routeId": $('[name=routeId]').val(),
					"returnQuantity": $('[name=quantity]').val(),
					"productId": $('[name=productId]').val(),
					};
		$.ajax({
				 type: "POST",
	             url: action,
	             data: dataJson,
	             dataType: 'json',
				 success:function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
						 $('div#changeIndentEntry_spinner').html('');
						 alert(result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]);
						
					}else{
					   
					}								 
				},
				error: function(){
					alert("record not found");
				}							
			});
	
	
}

function submitFinalizeOrder(){ 
	if(!$('[name=routeId]').val()){
		return;
	}
	$("form[name='FinalizeVehicle']").submit();
}






function prepareAjaxDataString(){
	formId = reconcilEntryInit;		
	 rowCount = 0;	
	 var querystring = "";
	for(i=0; i< data.length;i++){
		var changeItem = data[i];
		var qty;
		if(screenFlag == "returns"){
			qty = parseFloat(changeItem["returnQuantity"]);
		}
		else{
			 qty = parseFloat(changeItem["cQuantity"]);
		}
		if( typeof changeItem["cProductId"] != "undefined"   && changeItem["cProductId"] != "" && !isNaN(qty)){				
			  querystring += "productId_o_" + rowCount + "=" + changeItem["cProductId"] + "&";
			  querystring +=  "quantity_o_" + rowCount + "=" + qty + "&";
			  rowCount++; 
		}
	}	 
	
	var dataString = $(formId).serialize();
	querystring +=dataString;
	return querystring;

}




