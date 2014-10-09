$(function(){
	$('input[name=submitButton]').click (function (){ 	 
		$('input[name=centerCode]').focus();
 	   $('input[name=submitButton]').attr("disabled","disabled");
		$('div#AdjustmentsEntry_spinner').removeClass("errorMessage");
  	   	$('div#AdjustmentsEntry_spinner').html('<img src="/images/ajax-loader64.gif">');
		var dataString = $("#AdjustmentsEntry").serialize();
		var action = "createBillingAdjustmentAjax";		
		$.ajax({
			 type: "POST",
             url: action,
             data: dataString,
             dataType: 'json',
             async: false,	            
			success:function(result){
				if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					populateError(result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]);
					$('input[name=submitButton]').removeAttr("disabled");
				}else{			
						clearEditEntryFields();
					var adjChange = result["orderAdjMap"];            	   
            	   		setUpRecentList(adjChange);   
            	   		$("div#AdjustmentsEntry_spinner").fadeIn();
            	   		$('div#AdjustmentsEntry_spinner').html();            	 
            	   		$('div#AdjustmentsEntry_spinner').addClass("messageStr");
            	   		$('div#AdjustmentsEntry_spinner').html('<label>succesfully added.</label>');
				}								 
			},
			error: function(){
				populateError(result["_ERROR_MESSAGE_"]);
			}							
		});
		return false; 
	});		
});

function populateError(msg){
	$("div#AdjustmentsEntry_spinner").fadeIn();
	$('div#AdjustmentsEntry_spinner').removeClass("messageStr");
	$('div#AdjustmentsEntry_spinner').addClass("errorMessage");
	$('div#AdjustmentsEntry_spinner')
	  .html('<label>'+msg +'</label>');	
}

function clearEditEntryFields(){	
	$('input[name=submitButton]').removeAttr("disabled");
	$('input[name=centerCode]').focus();
	$('[name=centerCode]').val('');
	$('#amountValue').val('');
	$('#centerCode').focus();
	
}	