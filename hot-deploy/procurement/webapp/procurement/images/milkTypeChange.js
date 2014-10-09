jQuery(document).ready(function() {
	
	jQuery('#gQty').focus();
	var focusables = $(":focusable");
	focusables.keyup(function(e) {  
		var current = focusables.index(this);
    	var curentElName = focusables.eq(current).attr("name");    	
    	if(curentElName == "gheeYeild" || curentElName=="cQuantityLtrs"){    		
	    	next = $('input[name=submitButton]');    		
    	}else{    		
    		next = focusables.eq(current+1).length ? focusables.eq(current+1) : focusables.eq(0);
    	}
    	if(curentElName == "submitButton"){    		
	    	prev = $('input[name=snf]');    		
    	}else{    		
    		prev = focusables.eq(current-1).length ? focusables.eq(current-1) : focusables.eq(0);
    	}   
    	if (e.keyCode == 38) {    		
           prev.focus();
   		}
   		if (e.keyCode == 40) {   			
           next.focus();
   		}
   		if(jQuery('[name = sQuantityLtrs]').val()>0){
   			jQuery('#addSourDistribution').show();
   		}else{
   			jQuery('#addSourDistribution').hide();
   		}
    });
	milkTypeChangeHandler();
	//datepick();
	
});
function milkTypeChangeHandler(){
	jQuery('#addSourDistribution').hide();
	var milkTypeValue=jQuery("[name='milkType']").val();
	jQuery('[name= cQuantityLtrs]').val('0');
	if(milkTypeValue == 'S' ){
		jQuery('[name=gheeYield]').parent().parent().show();
		//jQuery('[name=sFat]').parent().parent().show();
		jQuery('[name=sKgFat]').parent().parent().show();
		jQuery('[name=sKgSnf]').parent().parent().show();
		jQuery('[name = sQuantityLtrs]').parent().parent().show();
		//jQuery('[name = sFat]').addClass("required");
		jQuery('[name = sKgFat]').addClass("required");
		jQuery('[name = sQuantityLtrs]').addClass("required");
		jQuery('[name=sQuantityLtrs]').val('');
		jQuery('[name= cQuantityLtrs]').parent().parent().hide();
		jQuery('[name= cQuantityLtrs]').val('');
		jQuery('[name= gheeYield]').removeClass("required");
		jQuery('[name= cQuantityLtrs]').removeClass("required");
	}else if(milkTypeValue=='C'){
		jQuery('[name= gheeYield]').parent().parent().hide();
		jQuery('[name= sQuantityLtrs]').parent().parent().hide();
		//jQuery('[name= sFat]').parent().parent().hide();
		jQuery('[name= sKgFat]').parent().parent().hide();
		jQuery('[name= sKgSnf]').parent().parent().hide();
		jQuery('[name= sQuantityLtrs]').removeClass("required");
		//jQuery('[name = sFat]').removeClass("required");
		jQuery('[name = sKgFat]').removeClass("required");
		jQuery('[name= gheeYield]').removeClass("required");
		jQuery('[name= cQuantityLtrs]').parent().parent().show();
		jQuery('#addSourDistribution').hide();
	}else{
		jQuery('[name = sQuantityLtrs]').parent().parent().hide();
		//jQuery('[name=sFat]').parent().parent().hide();
		jQuery('[name=sKgFat]').parent().parent().hide();
		jQuery('[name=sKgSnf]').parent().parent().hide();
		jQuery('[name= cQuantityLtrs]').parent().parent().hide();
		jQuery('[name = gheeYield]').parent().parent().hide();
		jQuery('[name= sQuantityLtrs]').removeClass("required");
		jQuery('[name= sKgFat]').removeClass("required");
		//jQuery('[name= sFat]').removeClass("required");
		jQuery('[name= gheeYield]').removeClass("required");
		jQuery('[name= cQuantityLtrs]').removeClass("required");
		jQuery('#addSourDistribution').hide();
	}
}
function datepick()
{		
	$( "#receiveDate" ).datetimepicker({
		dateFormat:'MM dd, yy',
		timeFormat: 'hh:mm:ss',
		changeMonth: false,
		numberOfMonths: 1});		
	$('#ui-datetimepicker-div').css('clip', 'auto');
}

function addSourDistribution(){
	jQuery('[name = sourSubmit]').click(function(event) {
/*		event.preventDefault();*/
		if((jQuery('[name = facilityId_o_0]').val()!='')||(jQuery('[name = facilityId_o_1]').val()!='')||(jQuery('[name = facilityId_o_2]').val()!='')||(jQuery('[name = facilityId_o_3]').val()!='')||(jQuery('[name = facilityId_o_4]').val()!='')||(jQuery('[name = facilityId_o_5]').val()!='')||(jQuery('[name = facilityId_o_6]').val()!='')||(jQuery('[name = facilityId_o_7]').val()!='')||(jQuery('[name = facilityId_o_8]').val()!='')||(jQuery('[name = facilityId_o_9]').val()!='')){
			jQuery('[name = containsSourAdviceValue]').val('true');
		}
	});
	/*var action = "AddSourDistributionAdvice";
    var dataString = $("#addSourDistributionAdvice").serialize();
    $.ajax({
		  type: "POST",
		  url: action,
		  data: dataString,
		  dataType: 'json',
		  success: function(result) {
		    if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){            	  
		 	  alert('error while adding Sour Distribution advice '+result["_ERROR_MESSAGE_"]+' '+result["_ERROR_MESSAGE_LIST_"]);
		    }else{
		    	alert('successfully added');
		    }
		    
		  } ,
			error: function(){
				alert("record not found");
			}				
		    });*/
}