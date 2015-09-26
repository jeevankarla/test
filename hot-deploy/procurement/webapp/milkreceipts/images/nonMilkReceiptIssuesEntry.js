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
		if(prevElName == "exitTime" ||prevElName == "entryTime" || prevElName == "testTime" || prevElName == "sendTime" || prevElName == "grossTime" || prevElName == "tareTime" ||prevElName == "issueTime" || prevElName == "cipTime"){
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
    	if(curentElName == "recdMBRT" || curentElName == "exitTime" ||curentElName == "issueTime" || curentElName == "cipTime" ||curentElName == "entryTime" ||curentElName == "tareTime" || curentElName == "testTime" || curentElName == "sendTime" || curentElName == "grossTime" || curentElName == "numberOfCells" || curentElName == "sealNumber"  ){
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
    	var action = "createNonMilkReceiptIssuesInit";
    	if(displayScreen == "ISSUE_TARWEIGHT"){
    		if(!$("#nonMilkReceiptIssuesEntry").validate({messages:{
    			tareDate:"" , tareTime:"",tareWeight:"" , tankerName:"", partyIdTo:""
        	   }}).form()) return;
    		action = "updateNonMilkIssuesTareWeight";
    	}else if(displayScreen == "ISSUE_OUT"){
    		if(!$("#nonMilkReceiptIssuesEntry").validate({messages:{
     		   exitDate:"" , exitTime:"" , tankerName:"" 
     	   }}).form()) return;
    		action = "updateNonMilkIssuesOut";
    	}else if(displayScreen == "ISSUE_GRSWEIGHT"){
    		if(!$("#nonMilkReceiptIssuesEntry").validate({messages:{
      		   grossDate:"" ,grossTime:"" , productId:"", tankerName:"",grossWeight:""
      	   }}).form()) return;
    		action = "updateNonMilkIssuesGrsWeight";
    		var weighmentId = $('[name=weighmentId]').val();
    		$("#weighmentId").val(weighmentId);
    	}else{
    		if(!$("#nonMilkReceiptIssuesEntry").validate({messages:{
        		 tareDate:"" , tareTime:"", noOfProducts:"", tankerName:"",partyId:""
        	   }}).form()) return;
    	}
    	var dataString = $("#nonMilkReceiptIssuesEntry").serialize();
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
                   $("div#nonMilkReceiptIssuesEntry_spinner").fadeIn();               	         	   
             	   $('div#nonMilkReceiptIssuesEntry_spinner').html(); 
             	   $('div#nonMilkReceiptIssuesEntry_spinner').removeClass("errorMessage");           	 
             	   $('div#nonMilkReceiptIssuesEntry_spinner').addClass("messageStr");
             	   $('div#nonMilkReceiptIssuesEntry_spinner').html('<label><h1>succesfully updated.</h1></label>'); 
             	   $('div#nonMilkReceiptIssuesEntry_spinner').delay(7000).fadeOut('slow');
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
	$("div#nonMilkReceiptIssuesEntry_spinner").fadeIn();
	$('div#nonMilkReceiptIssuesEntry_spinner').removeClass("messageStr");
	$('div#nonMilkReceiptIssuesEntry_spinner').addClass("errorMessage");
	$('div#nonMilkReceiptIssuesEntry_spinner').html('<label><h1>'+msg +'</h1></label>');
	$('div#nonMilkReceiptIssuesEntry_spinner').delay(7000).fadeOut('slow');
	$('input[name=submitButton]').removeAttr("disabled");
	
}
	
function clearFields(){
	   var displayScreen= $('[name=displayScreen]').val(); 
	   if(typeof(displayScreen)!= 'undefined'){
		   if(displayScreen=="ISSUE_GRSWEIGHT"){
				 var rowCount=  parseInt($('#noOfProduct').val())+2;
				 for(i=0;i<=rowCount;i++){
					 $("#productsTable tr:last-child").remove();
				 }
				  fetchWeighmentDetails();  
				  if(productsCount != $('#noOfProduct').val()){
					  reloadingPage();
				  }else{
					  $('#submitEntry').hide();
				  }
			  }
		   
		   if((displayScreen=="ISSUE_TARWEIGHT") || (displayScreen=="ISSUE_OUT")){
			   var frm = document.getElementsByName('nonMilkReceiptIssuesEntry')[0];
			   frm.reset();
		   	   reloadingPage();
		   }
	   }
}







