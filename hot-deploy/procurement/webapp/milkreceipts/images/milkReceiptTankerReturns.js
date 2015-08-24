jQuery(document).ready(function() {
	var focusables = $(":focusable");
	focusables.keyup(function(e) {  
		var current = focusables.index(this);
		if(e.keyCode==9){
			var tempObject = focusables.eq(current-1).length ? focusables.eq(current-1) : focusables.eq(0);
    		current = focusables.index(tempObject);
    	}
    	var curentElName = focusables.eq(current).attr("name");
    	var prevEl = focusables.eq(current-1).length ? focusables.eq(current-1) : focusables.eq(0);
		var prevElName = prevEl.attr("name");
		if(curentElName == "sendTemp"){
			var productId = $('#productId').val();
			if(!productId){
				alert('please enter valid product');
				$('input[name='+curentElName+']').val('');
				prevEl.val('');
				prevEl.focus();
				return false;
			}
		}
		if(prevElName == "exitTime" ||prevElName == "entryTime" || prevElName == "testTime" || prevElName == "sendTime" || prevElName == "grossTime" || prevElName == "tareTime" || prevElName == "cipTime"){
    		var tempTime = prevEl.val();
    		if(tempTime.length==0){
				alert('invalid Time formate. length should be 4');
				$('input[name='+curentElName+']').val('');
				prevEl.val('');
				prevEl.focus();
				return false;
			}
    		if(tempTime!=''){
    			if(tempTime.length<4 || tempTime.length==0){
    				alert('invalid Time formate. length should be 4');
    				$('input[name='+curentElName+']').val('');
    				prevEl.val('');
    				prevEl.focus();
    				return false;
    			}
    			var hh = tempTime.substring(0,2);
    			var mm = tempTime.substring(2,4);
    			if(hh>23 || mm>59){
    				alert('invalid Time formate');
    				prevEl.val('');
    				$('input[name='+curentElName+']').val('');
    				prevEl.focus();
    				return false;
    			}
    		}
		}
		// auto tab  for time tabs and those which need only number input
    	if(curentElName == "recdMBRT" || curentElName == "exitTime" || curentElName == "cipTime" ||curentElName == "entryTime" ||curentElName == "tareTime" || curentElName == "testTime" || curentElName == "sendTime" || curentElName == "grossTime" || curentElName == "numberOfCells" || curentElName == "sealNumber"  ){
    		if(e.which == 110 || e.which == 190){
    			$(this).val( $(this).val().replace('.',''));
    		}
    		$(this).val( $(this).val().replace(/[^0-9\.]/g,''));
    		var timeVal = $('input[name='+curentElName+']').val();
    		var valLength = timeVal.length;
    		if(curentElName == "numberOfCells"){
    			if(valLength == 1){
        			next = focusables.eq(current+1).length ? focusables.eq(current+1) : focusables.eq(0);
        			next.focus();
        		}
    		}
    		
    		if(valLength == 4 && curentElName != "sealNumber"){
    			if(curentElName == "exitTime"){
    				var tempTime = $(this).val();
    				var hh = tempTime.substring(0,2);
        			var mm = tempTime.substring(2,4);
        			if(hh>23 || mm>59){
        				alert('invalid Time formate');
        				$(this).val('');
        				return false;
        			}
    				
    			}
    			
    			next = focusables.eq(current+1).length ? focusables.eq(current+1) : focusables.eq(0);
    			next.focus();
    		}
    	}
    	
    	prev = focusables.eq(current-1).length ? focusables.eq(current-1) : focusables.eq(0);
		next = focusables.eq(current+1).length ? focusables.eq(current+1) : focusables.eq(0);
    	
    	
    	if (e.keyCode == 38  && curentElName!="product") {    		
           prev.focus();
   		}
   		if (e.keyCode == 9 && curentElName!="product") {
   			next.focus();
   		}
    });
	
	
});


$(function() {
    $('#submitEntry').click (function (){
    	var displayScreen = $('[name=displayScreen]').val();
    	var action = "createReturnMilkTransferAjax";
       if(displayScreen == "RETURN_INITIATE"){
    		if(!$("#milkReceiptReturnEntry").validate({messages:{
        		   testDate:"" ,testTime:"" ,  tankerName:"",sendTemp:"",
        		   productId:"",sendTemp:"",sendAcid:"",sendCLR:"",
        		   sendFat:"",sendSnf:"",sendCob:"",
        		   sendSedimentTest:""
        	   }}).form()) return;
     		action = "createReturnMilkTransferAjax";
     	}else if(displayScreen == "RETURN_GRSWEIGHT"){
    		if(!$("#milkReceiptReturnEntry").validate({messages:{
       		   grossDate:"" ,grossTime:"" , milkTransferId:"" , tankerName:"",numberOfCells:"",dispatchWeight:"",grossWeight:""
       	   }}).form()) return;
     		action = "updateMilkTransferReturnGrossWeight";
     	}else if(displayScreen == "RETURN_QC"){
    		if(!$("#milkReceiptReturnEntry").validate({messages:{
     		   testDate:"" ,testTime:"" ,  tankerName:"",sendTemp:"",
     		   productId:"",sendTemp:"",sendAcid:"",sendCLR:"",
     		   sendFat:"",sendSnf:"",sendCob:"",
     		   sendSedimentTest:""
     	   }}).form()) return;
    		action = "updateMilkTransferReturnQC";
        }else if(displayScreen == "RETURN_UNLOAD"){
        	if(!$("#milkReceiptReturnEntry").validate({messages:{
     		   cipDate:"" ,cipTime:"" , milkTransferId:"" , tankerName:"",silo:""
     	   }}).form()) return;
        	action = "updateMilkTransferReturnUnload";
        }else if(displayScreen == "RETURN_CIP"){
        	if(!$("#milkReceiptReturnEntry").validate({messages:{
      		   cipDate:"" ,cipTime:"" , milkTransferId:"" , tankerName:"",isCipChecked:""
      	   }}).form()) return;
         	action = "updateMilkTransferReturnCIP";
         }else if(displayScreen == "RETURN_TARWEIGHT"){
         	if(!$("#milkReceiptReturnEntry").validate({messages:{
       		   tareDate:"" ,tareTime:"" , milkTransferId:"" , tankerName:"",isCipChecked:""
       	   }}).form()) return;
          	action = "updateMilkTransferReturnTareWeight";
          }else{
        	 
    		if(!$("#milkReceiptReturnEntry").validate({messages:{
        		 tareDate:"" , tareTime:"" , tareWeight:"" , tankerName:"",partyIdTo:""
        	   }}).form()) return;
    	}
    	var dataString = $("#milkReceiptReturnEntry").serialize();
    	$('input[name=submitButton]').attr("disabled","disabled");
    	$.ajax({
            type: "POST",
            url: action,
            data: dataString,
            dataType: 'json',
            success: function(result) { 
            	
            	if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){               	   
            		populateError(result["_ERROR_MESSAGE_"]);
                }else{
              	   clearFields();
                   $("div#milkReceiptReturnEntry_spinner").fadeIn();               	         	   
             	   $('div#milkReceiptReturnEntry_spinner').html(); 
             	   $('div#milkReceiptReturnEntry_spinner').removeClass("errorMessage");           	 
             	   $('div#milkReceiptReturnEntry_spinner').addClass("messageStr");
             	   $('div#milkReceiptReturnEntry_spinner').html('<label>succesfully updated.</label>'); 
             	   $('div#milkReceiptReturnEntry_spinner').delay(7000).fadeOut('slow');
             	   $('input[name=submitButton]').removeAttr("disabled");
             	   
                }
              },
              error: function(result) {
            	  populateError(result["_ERROR_MESSAGE_"]+","+result["_ERROR_MESSAGE_LIST_"]);
          	 }
    	});
    	
    	return false;
	});
});
function populateError(msg){
	$("div#milkReceiptReturnEntry_spinner").fadeIn();
	$('div#milkReceiptReturnEntry_spinner').removeClass("messageStr");
	$('div#milkReceiptReturnEntry_spinner').addClass("errorMessage");
	$('div#milkReceiptReturnEntry_spinner').html('<label>'+msg +'</label>');
	$('div#milkReceiptReturnEntry_spinner').delay(7000).fadeOut('slow');
	$('input[name=submitButton]').removeAttr("disabled");
	
}
	
function clearFields(){
		var displayScreen= $('[name=displayScreen]').val(); 
	   var frm = document.getElementsByName('milkReceiptReturnEntry')[0];
	   frm.reset();
	   if(typeof(displayScreen)!= 'undefined'){
		   if((displayScreen=="RETURN_GRSWEIGHT") || (displayScreen=="RETURN_QC") || (displayScreen=="RETURN_UNLOAD")){ 
		   	reloadingPage();
		   }
	   }
}







