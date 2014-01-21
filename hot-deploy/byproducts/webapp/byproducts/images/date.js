$(document).ready(function(){
	
	//for date Picker 
	makeDatePicker("date","thruDate");
	$('#ui-datepicker-div').css('clip', 'auto');
});

function makeDatePicker(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
		dateFormat:'d MM, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
			}
	});
}