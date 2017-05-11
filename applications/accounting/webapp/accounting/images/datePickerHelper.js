$(document).ready(function(){
	
	//for date Picker 
	makeDatePicker("remitDate","thruDate");
	makeDatePicker("onlineFromDate","thruDate");
	makeDatePicker("onlineThruDate","thruDate");
	$('#ui-datepicker-div').css('clip', 'auto');
	
});

function makeDatePicker(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat:'yy-mm-dd',
			changeMonth: true,
			numberOfMonths: 1,
			
			maxDate:0,
			onSelect: function( selectedDate ) {
				$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
			}
	});
}
function setFormParams(){
		$("#onlineFromDate").datepicker( "option", "dateFormat", "yy-mm-dd 00:00:00");
}
