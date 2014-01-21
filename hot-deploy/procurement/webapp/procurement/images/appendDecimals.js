$(document).ready(function(){
	
	jQuery("#centerCode").focus();  
	
	//for date Picker 
	makeDatePicker("orderDate","thruDate");
	$('#ui-datepicker-div').css('clip', 'auto');
	
	var focusables = $(":focusable");
    focusables.keyup(function(e) {  
    	if(this.id == "quantity"){
    		if ($('#quantity').val() > 0){
		    	$('#ptcQuantity').parent().parent().parent().show();
		    	$('#sourMilk').parent().parent().parent().show();
		    	$('#curdMilk').parent().parent().parent().show();
			}else{
				$('#ptcQuantity').val("");
				$('#ptcQuantity').parent().parent().parent().hide();
				$('#sourMilk').parent().parent().parent().hide();
		    	$('#curdMilk').parent().parent().parent().hide();
			}
    	}
    	if(this.id == "sQuantity"){
    		if ($('#sQuantity').val() > 0){
    			$('#sFat').addClass("required");
 		   	}else{
 			   $('#sFat').removeClass("required");
 		   	}
    	}
    	e.preventDefault();     
    	var current = focusables.index(this);
    	var activeFlag = $("#accordion" ).accordion("option","active");
    	var curentElName = focusables.eq(current).attr("name");    	
    	if(curentElName == "snf" && activeFlag.toString() != "0" ){    		
	    	next = $('input[name=submitButton]');    		
    	}else{    		
    		next = focusables.eq(current+1).length ? focusables.eq(current+1) : focusables.eq(0);
    	}
    	if(curentElName == "submitButton" && activeFlag.toString() != "0" ){    		
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
   	
    });    
	
});

function makeDatePicker(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat:'yy-mm-dd',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
			}
	});
}

