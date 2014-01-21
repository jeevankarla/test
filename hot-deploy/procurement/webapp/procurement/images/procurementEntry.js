$(function() {
       $('input[name=submitButton]').click (function (){
    	   if(!$("#ProcurementEntry").validate({messages:{
    		   unitCode:"" , centerCode:"" , quantity:"" ,
    		   snf:"" , fat:"" , orderDate :""
    	   }}).form()) return;
    	   $('div#ProcurementEntry_spinner').removeClass("errorMessage");
    	   $('div#ProcurementEntry_spinner')
    		  .html('<img src="/images/ajax-loader64.gif">');    	   
               var action = "createProcurementEntryAjax";
               var dataString = $("#ProcurementEntry").serialize();
               if(($('#updateFlag').val()=='update')){
            	  action = "updateProcurementEntryAjax";
               }
               if(($('#updateFlag').val()=='update')&&($('#editRecord').val()=='true')){
            	   action = "updateProcurementEntryRecordAjax";
               }
               $.ajax({
             type: "POST",
             url: action,
             data: dataString,
             dataType: 'json',
             success: function(result) {
               if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){            	  
            	   populateError(result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]);
               }else{
            	   var recentChnage = result["orderItem"];            	   
            	   setUpRecentList(recentChnage);
            	   updateFromFields();
            	   $("div#ProcurementEntry_spinner").fadeIn();
            	   $('div#ProcurementEntry_spinner').html();            	 
            	   $('div#ProcurementEntry_spinner').addClass("messageStr");
            	   if(($('#updateFlag').val()=="update")){
            		   $('div#ProcurementEntry_spinner').html('<label>succesfully updated.</label>');
            		}else{
            			$('div#ProcurementEntry_spinner').html('<label>succesfully added.</label>');
            		}
            	   $('div#ProcurementEntry_spinner').delay(5000).fadeOut('slow'); 
               }
               
             } ,
             error: function() {
            	 	populateError(result["_ERROR_MESSAGE_"]);
            	 }
               });    
               return false;
       });
});


function populateError(msg){
	$("div#ProcurementEntry_spinner").fadeIn();
	$('div#ProcurementEntry_spinner').removeClass("messageStr");
	$('div#ProcurementEntry_spinner').addClass("errorMessage");
	$('div#ProcurementEntry_spinner')
	  .html('<label>'+msg +'</label>');

	
}

function updateFromFields(){    
	$('input[name=centerCode]').focus();
	if(($('#retainCenterCode').val()!="Y")){
		$('input[name=centerCode]').val('');
	}
	$('span#centerToolTip').html('');
	$('input[name=quantity]').val('');
	$('input[name=fat]').val('');
	$('input[name=snf]').val('');
	$('input[name=lactoReading]').val('');
	$('input[name=qtyLtrs]').val('');
	
	$('input[name=sQuantity]').val('');
	$('input[name=sQtyKgs]').val('');
	$('input[name=sFat]').val('');
	$('input[name=cQuantity]').val('');
	$('input[name=ptcQuantity]').val('');	
	$('input[name=ptcMilkType]').attr('checked', false);
	
	$('#ptcQuantity').parent().parent().parent().hide();
	$('[name = ptcMilkType]').parent().parent().parent().hide();
	$( "#accordion" ).accordion({ collapsible: true , active : true});
}
function fetchProurementEntry(){
	if(($('[name=shedCode]').val()!='')&&($('[name=unitCode]').val()!='')&&($('[name=centerCode]').val()!='')&&($('[name=orderDate]').val()!='')&&($('[name=productId]').val()!='')&&($('[name=unitCode]').val()!='')&&($('[name=purchaseTime]').val()!='')){
		var action = "fetchProcurementRecordAjax";
		var dataJson = {"shedCode":$('[name=shedCode]').val(),
						"unitCode":$('[name=unitCode]').val(),
						"centerCode":$('[name=centerCode]').val(),
						"orderDate":$('[name=orderDate]').val(),
						"purchaseTime":$('[name=purchaseTime]').val(),
						"productId":$('[name=productId]').val(),	
					   };
			$.ajax({
				 type: "POST",
	             url: action,
	             data: dataJson,
	             dataType: 'json',
	            
				success:function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
						clearEditEntryFields();
					}else{
						var orderItem = result["orderItem"];
						$('[name=orderId]').val(orderItem["orderId"]);
						$('[name=orderItemSeqId]').val(orderItem["orderItemSeqId"]);
						$('[name=fat]').val(orderItem["fat"]);
						$('[name=snf]').val(orderItem["snf"]);
						$('[name=quantity]').val(orderItem["quantityKgs"]);
						$('[name=cQuantity]').val(orderItem["cQuantityLtrs"]);
						$('[name=sQuantity]').val(orderItem["sQuantityLtrs"]);
						$('[name=sQtyKgs]').val(result["sQtyKgs"]);
						$('[name=sFat]').val(orderItem["sFat"]);
						$('[name=ptcQuantity]').val(orderItem["ptcQuantity"]);
						$('[name=lactoReading]').val(orderItem["lactoReading"]);
						$('[name=qtyLtrs]').val(orderItem["quantityLtrs"]);
						if(($("#accordion" ).accordion("option","active"))){
							$( "#accordion" ).accordion("activate");
						}
						
						if(orderItem["quantity"]>0){
						    $('#ptcQuantity').parent().parent().parent().show();
						    $('#sourMilk').parent().parent().parent().show();
							$('#curdMilk').parent().parent().parent().show();
						}
						if( orderItem["ptcMilkType"]=='S'){
							$('#sourMilk').val(orderItem["ptcMilkType"]).attr('checked', true);
						
						}
						if( orderItem["ptcMilkType"]=='C'){
								$('#curdMilk').val(orderItem["ptcMilkType"]).attr('checked', true);
						}		
					}								 
				},
				error: function(){
					alert("record not found");
				}							
			});
		}
	
}



