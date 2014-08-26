
function picker(){
	jQuery("#fromDate").datepicker({
			dateFormat:'dd/mm/yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function(selectedDate) {
				jQuery("#fromDate").datepicker('setDate', selectedDate);
			}
		});
	jQuery("#thruDate").datepicker({
		dateFormat:'dd/mm/yy',
		changeMonth: true,
		numberOfMonths: 1,
		onSelect: function(selectedDate) {
			jQuery("#thruDate").datepicker('setDate', selectedDate);
		}
	});
	}
