jQuery(document).ready(function() {
	datePicker1();
	datePicker2(this);
});

function datePicker1(){
	jQuery("#DateOfBirth").datepicker({
		dateFormat:'dd-mm-yy',
		changeMonth: true,
		changeYear: true,
		yearRange: "-30:+0",
		onSelect: function(selectedDate) {
			jQuery("#DateOfBirth").datepicker('setDate', selectedDate);
		}
	});
	jQuery("#employmentDate").datepicker({
		dateFormat:'dd-mm-yy',
		changeMonth: true,
		changeYear: true,
		yearRange: "-30:+0",
		onSelect: function(selectedDate) {
			jQuery("#employmentDate").datepicker('setDate', selectedDate);
		}
	});
	jQuery("#issueDate").datepicker({
		dateFormat:'dd-mm-yy',
		changeMonth: true,
		changeYear: true,
		yearRange: "-30:+0",
		onSelect: function(selectedDate) {
			jQuery("#issueDate").datepicker('setDate', selectedDate);
		}
	});
	jQuery("#expiryDate").datepicker({
		dateFormat:'dd-mm-yy',
		changeMonth: true,
		changeYear: true,
		yearRange: "-30:+0",
		onSelect: function(selectedDate) {
			jQuery("#expiryDate").datepicker('setDate', selectedDate);
		}
	});
	jQuery("#expiryDate_o_0").datepicker({
		dateFormat:'dd-mm-yy',
		changeMonth: true,
		changeYear: true,
		yearRange: "-30:+0",
		onSelect: function(selectedDate) {
			jQuery("#expiryDate_o_0").datepicker('setDate', selectedDate);
		}
	});
	jQuery("#geoFromDate").datepicker({
		dateFormat:'dd-mm-yy',
		changeMonth: true,
		changeYear: true,
		yearRange: "-30:+0",
		onSelect: function(selectedDate) {
			jQuery("#geoFromDate").datepicker('setDate', selectedDate);
		}
	});
	jQuery("#dependentBirthDate").datepicker({
		dateFormat:'dd-mm-yy',
		changeMonth: true,
		changeYear: true,
		yearRange: "-30:+0",
		onSelect: function(selectedDate) {
			jQuery("#dependentBirthDate").datepicker('setDate', selectedDate);
		}
	});
	jQuery("#thruDate").datepicker({
		dateFormat:'dd-mm-yy',
		changeMonth: true,
		changeYear: true,
		yearRange: "-30:+0",
		onSelect: function(selectedDate) {
			jQuery("#thruDate").datepicker('setDate', selectedDate);
		}
	});
	jQuery("#fromDate").datepicker({
		dateFormat:'dd-mm-yy',
		changeMonth: true,
		changeYear: true,
		yearRange: "-30:+0",
		onSelect: function(selectedDate) {
			jQuery("#fromDate").datepicker('setDate', selectedDate);
		}
	});
	jQuery("#leaveFromDate").datepicker({
		dateFormat:'dd-mm-yy',
		showSecond: true,
		timeFormat: 'hh:mm:ss',
		changeMonth: true,
		changeYear: true,
		yearRange: "-30:+0",
		onSelect: function(selectedDate) {
			jQuery("#leaveFromDate").datepicker('setDate', selectedDate);
		}
	});
}
function datePicker2(monthYearpckr){
	var monthYearpckrId = $(monthYearpckr).id;
	jQuery("#"+monthYearpckrId).datepicker({
		dateFormat:'dd-mm-yy',
		changeMonth: true,
		changeYear: true,
		yearRange: "-30:+0",
		onSelect: function(selectedDate) {
			jQuery("#"+monthYearpckrId).datepicker('setDate', selectedDate);
		}
	});
}