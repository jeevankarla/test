$(function() {
       $('input[name=submitButton]').click (function (){    
    	   var dateFormat = $('[name="orderDate"]').datepicker( "option", "dateFormat" );
    	   $('[name="orderDate"]').datepicker( "option", "dateFormat", "yy-mm-dd" );
   		   if(!$("#ProcurementEntry").validate({messages:{
    		   unitCode:"" , centerCode:"" , quantity:"" ,
    		   snf:"" , fat:"" , orderDate :""
    	   }}).form()) return;
    	   $('input[name=centerCode]').focus();  
    	   
    	   if((($('#sFat').val())=='') && (($('#cQuantity').val())=='')){ 
	    	   if(!validateFatSnfValue(Number($('#productId').val()),Number($('#fat').val()),Number($('#snf').val()))){
	    		   return false;
	    	   }
    	   }
    	   $('input[name=submitButton]').attr("disabled","disabled");
    	   // here we hard-coded for MilkLine based on prodctFocusFlag
    	   if(($('#prodctFocusFlag').val()=='TRUE')){
    		   if(((Number($('#productId').val()))==200)&&((Number($('#fat').val())>10)||(Number($('#fat').val())<4.5))){
    				   alert('BM fat should be between 4.5-10');
    				   $('input[name=submitButton]').removeAttr("disabled");
    				   return false;
    		   }
    		   if(((Number($('#productId').val()))==201)&&((Number($('#fat').val())>6.5)||(Number($('#fat').val())<2.5))){
				   alert('CM fat should be between 2.5-6');
				   $('input[name=submitButton]').removeAttr("disabled");
				   return false;
    		   }
    		   if((Number($('#lactoReading').val())>35)||(Number($('#lactoReading').val())<15)){
				   alert('LR  should be between 15-35');
				   $('input[name=submitButton]').removeAttr("disabled");
				   return false;
    		   }
    	   }else{
			   var lacReading = $("#lactoReading").val();
			   if((Number($('#lactoReading').val())>30)){
				   alert('LR  should be less than 31');
				   $('input[name=submitButton]').removeAttr("disabled");
				   return false;
    		   }
		   }
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
             async: false,
             success: function(result) {
               if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){            	  
            	   populateError(result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]);
            	   $('input[name=submitButton]').removeAttr("disabled");
               }else{
            	   var recentChnage = result["orderItem"];            	   
            	   setUpRecentList(recentChnage);
            	   updateFromFields();
            	   
            	   if(($('#prodctFocusFlag').val()=='TRUE')){
            		   jQuery("#productId").focus();
            	   }
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
              $('[name="orderDate"]').datepicker( "option", "dateFormat", "dd-mm-yy" );
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
	$('input[name=submitButton]').removeAttr("disabled");
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
	var dateFormat = $('[name="orderDate"]').datepicker( "option", "dateFormat" );
	if(($('[name=shedCode]').val()!='')&&($('[name=unitCode]').val()!='')&&($('[name=centerCode]').val()!='')&&($('[name=orderDate]').val()!='')&&($('[name=productId]').val()!='')&&($('[name=unitCode]').val()!='')&&($('[name=purchaseTime]').val()!='')){
		var action = "fetchProcurementRecordAjax";
		$('[name="orderDate"]').datepicker( "option", "dateFormat", "yy-mm-dd" );
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
			$('[name="orderDate"]').datepicker( "option", "dateFormat", "dd-mm-yy" );
		}
	
}

function fetchRecentChange(dataJson){
	var action = "fetchRecentChangeAjax";
		$.ajax({
			 type: "POST",
             url: action,
             data: dataJson,
             dataType: 'json',
            
			success:function(result){
					var recentChnage = result["orderItem"]; 
	            	setUpRecentList(recentChnage);	
			}
				
		});
	
}


