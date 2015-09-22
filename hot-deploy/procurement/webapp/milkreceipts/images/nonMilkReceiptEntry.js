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
    			if(curentElName != "sealNumber"){
    				next = focusables.eq(current+1).length ? focusables.eq(current+1) : focusables.eq(0);
        			next.focus();
    			}
    			
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
    	doAction();
    	return false;
	});
    $('#rejectEntry').click (function (){
    	var confirmationFlag=false;
    	if(confirm('Dou u want to reject this Record?')){	
    		confirmationFlag=true;
    	}else{
    		confirmationFlag=false;
    	}	
    	if(confirmationFlag){
    		$('[name=qcReject]').val('Y');	
    		doAction();	
    	}
    	return false;
	});
});
function doAction(){
	var displayScreen = $('[name=displayScreen]').val();
	var action = "createNonMilkReceiptEntry";
	if(displayScreen == "VEHICLE_OUT"){
		if(!$("#nonMilkReceiptEntry").validate({messages:{
 		   exitDate:"" , exitTime:"" , tankerName:"" 
 	   }}).form()) return;
		
		action = "updateNonMilkReceiptEntryOut";
	}else if(displayScreen == "VEHICLE_GROSSWEIGHT"){
		if(!$("#nonMilkReceiptEntry").validate({messages:{
  		   grossDate:"" ,grossTime:"" , weighmentId:"" , tankerName:"",dispatchWeight:"",grossWeight:""
  	   }}).form()) return;
		action = "updateNonMilkReceiptEntryGrsWeight";
	}else if(displayScreen == "VEHICLE_TAREWEIGHT"){
		if(!$("#nonMilkReceiptEntry").validate({messages:{
   		   tareDate:"" ,tareTime:"" , weighmentId:"" , tankerName:"",tareWeight:""
   	   }}).form()) return;
		action = "updateNonMilkReceiptEntryTareWeight";
	}else{
		if(!$("#nonMilkReceiptEntry").validate({messages:{
    		   entryDate:"" ,entryTime:"" , tankerName:"",sendDate:"" ,sendTime:"" ,
    		   dcNo:"",noOfProducts:"",partyId:"",sealCheck:""
    	   }}).form()) return;
	}
	var dataString = $("#nonMilkReceiptEntry").serialize();
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
               $("div#nonMilkReceiptEntry_spinner").fadeIn();               	         	   
         	   $('div#nonMilkReceiptEntry_spinner').html(); 
         	   $('div#nonMilkReceiptEntry_spinner').removeClass("errorMessage");           	 
         	   $('div#nonMilkReceiptEntry_spinner').addClass("messageStr");
         	   $('div#nonMilkReceiptEntry_spinner').html('<label><h1>successfully updated. </h1></label>'); 
         	   $('div#nonMilkReceiptEntry_spinner').delay(7000).fadeOut('slow');
         	   $('input[name=submitButton]').removeAttr("disabled");

            }
          },
          error: function(result) {
        	  populateError(result["_ERROR_MESSAGE_"]+","+result["_ERROR_MESSAGE_LIST_"]);
      	 }
	});
}


function populateError(msg){
	$("div#nonMilkReceiptEntry_spinner").fadeIn();
	$('div#nonMilkReceiptEntry_spinner').removeClass("messageStr");
	$('div#nonMilkReceiptEntry_spinner').addClass("errorMessage");
	$('div#nonMilkReceiptEntry_spinner').html('<label><h1>'+msg +'</h1></label>');
	$('div#nonMilkReceiptEntry_spinner').delay(7000).fadeOut('slow');
	$('input[name=submitButton]').removeAttr("disabled");
	
}
	
function clearFields(){
	  var displayScreen= $('[name=displayScreen]').val(); 
	  	  if(typeof(displayScreen)!= 'undefined'){
		  if(displayScreen=="VEHICLE_TAREWEIGHT"){
			 var rowCount=  parseInt($('#noOfProduct').val())+2;
			 for(i=0;i<=rowCount;i++){
				 $("#productsTable tr:last-child").remove();
			 }
			  fetchWeighmentDetails();  
		  }
		   if((displayScreen=="VEHICLE_OUT") || (displayScreen=="VEHICLE_GROSSWEIGHT")){ 
			   var frm = document.getElementsByName('nonMilkReceiptEntry')[0];
			   frm.reset();
			   reloadingPage();
		   }
	   }
}