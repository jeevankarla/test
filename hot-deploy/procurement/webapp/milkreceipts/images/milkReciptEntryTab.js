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
		
		if(prevElName == "exitTime" ||prevElName == "entryTime" || prevElName == "testTime" || prevElName == "sendTime" || prevElName == "grossTime" || prevElName == "tareTime"){
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
    	if(curentElName == "recdMBRT" || curentElName == "exitTime" ||curentElName == "entryTime" ||curentElName == "tareTime" || curentElName == "testTime" || curentElName == "sendTime" || curentElName == "grossTime" || curentElName == "numberOfCells"){
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
    		
    		if(valLength == 4){
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
    	
    	
    	if (e.keyCode == 38 ) {    		
           prev.focus();
   		}
   		if (e.keyCode == 9) {
   			next.focus();
   		}
    });
	
	
});


$(function() {
    $('#submitEntry').click (function (){
    	var displayScreen = $('[name=displayScreen]').val();
    	var action = "createMilkTankerReceiptEntry";
    	if(displayScreen == "VEHICLE_OUT"){
    		if(!$("#milkReceiptEntry").validate({messages:{
     		   exitDate:"" , exitTime:"" , tankerName:"" 
     	   }}).form()) return;
    		
    		action = "updateMilkTankerReceiptEntryOut";
    	}else if(displayScreen == "VEHICLE_GRSWEIGHT"){
    		if(!$("#milkReceiptEntry").validate({messages:{
      		   grossDate:"" ,grossTime:"" , milkTransferId:"" , tankerName:"",numberOfCells:"",dispatchWeight:"",grossWeight:""
      	   }}).form()) return;
    		action = "updateMilkTankerReceiptEntryGrsWeight";
    	}else if(displayScreen == "VEHICLE_TAREWEIGHT"){
    		if(!$("#milkReceiptEntry").validate({messages:{
       		   tareDate:"" ,tareTime:"" , milkTransferId:"" , tankerName:"",tareWeight:""
       	   }}).form()) return;
    		action = "updateMilkTankerReceiptEntryTAREWeight";
    	}else if(displayScreen == "VEHICLE_QC"){
    		if(!$("#milkReceiptEntry").validate({messages:{
       		   testDate:"" ,testTime:"" , milkTransferId:"" , tankerName:"",sendTemp:"",
       		   sendTemp:"",recdTemp:"",sendAcid:"",recdAcid:"",sendCLR:"",recdCLR:"",
       		   sendFat:"",recdFat:"",sendSnf:"",recdSnf:"",sendCob:"",recdCob:"",
       		   sendNutriliser:"",recdFlavour:"",recdSedimentTest:"",recdPH:"",recdMBRT:""
       		   
       		   
       	   }}).form()) return;
    		action = "updateMilkTankerReceiptEntryQC";
    	}else{
    		if(!$("#milkReceiptEntry").validate({messages:{
        		   entryDate:"" ,entryTime:"" , tankerName:"",sendDate:"" ,sendTime:"" ,
        		   dcNo:"",partyId:"",sealCheck:""
        	   }}).form()) return;
    	}
    	var dataString = $("#milkReceiptEntry").serialize();
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
                	$("div#milkReceiptEntry_spinner").fadeIn();               	         	   
             	   $('div#milkReceiptEntry_spinner').html(); 
             	    $('div#milkReceiptEntry_spinner').removeClass("errorMessage");           	 
             	   $('div#milkReceiptEntry_spinner').addClass("messageStr");
             	   $('div#milkReceiptEntry_spinner').html('<label>succesfully updated.</label>'); 
             	   $('div#milkReceiptEntry_spinner').delay(5000).fadeOut('slow');
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
	$("div#milkReceiptEntry_spinner").fadeIn();
	$('div#milkReceiptEntry_spinner').removeClass("messageStr");
	$('div#milkReceiptEntry_spinner').addClass("errorMessage");
	$('div#milkReceiptEntry_spinner').html('<label>'+msg +'</label>');
	$('div#milkReceiptEntry_spinner').delay(5000).fadeOut('slow');
	$('input[name=submitButton]').removeAttr("disabled");
	
}
	






