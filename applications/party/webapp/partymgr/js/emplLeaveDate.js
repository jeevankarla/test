jQuery(document).ready(function() {
	picker();
});

function picker(){
	jQuery("#fromDate").datepicker({
			dateFormat:'dd-mm-yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function(selectedDate) {
				$('[name=leaveTypeId]').val( '' );
				$('#leaveSpan').remove();
				$('#chghss').remove();
				$('#ghssDropDown').hide();
				$("#thruDate").datepicker( "option", {minDate: selectedDate}).datepicker();
			}
	});
	jQuery("#thruDate").datepicker({
		dateFormat:'dd-mm-yy',
		changeMonth: true,
		numberOfMonths: 1,
	});
	
	jQuery("#holidayDate").datepicker({
		dateFormat:'dd-mm-yy',
		changeMonth: true,
		numberOfMonths: 1,
		onSelect: function(selectedDate) {
			jQuery("#holidayDate").datepicker('setDate', selectedDate);
		}
	});
	jQuery("#date").datepicker({
		dateFormat:'dd-mm-yy',
		changeMonth: true,
		numberOfMonths: 1,
		onSelect: function(selectedDate) {
			jQuery("#date").datepicker('setDate', selectedDate);
		}
	});
	jQuery("#punchTypeFromDate").datepicker({
		dateFormat:'dd-mm-yy',
		changeMonth: true,
		numberOfMonths: 1,
		onSelect: function(selectedDate) {
			jQuery("#punchTypeFromDate").datepicker('setDate', selectedDate);
		}
	});
}
