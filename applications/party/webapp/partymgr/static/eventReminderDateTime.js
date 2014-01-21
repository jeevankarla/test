 function setEventReminderDateTime(){
	if (jQuery('input[name=reminder]').is(':checked')) {
		
		jQuery('[id=reminderDateTime]').parent().parent().parent().show();
    	jQuery('[id=reminderDateTime]').addClass("required");
    	jQuery('[id=reminderDateTime_i18n]').val(jQuery('[id=estimatedStartDate]').val());
    	jQuery('[name=reminderDateTime_c_hour]').val(jQuery('[name=estimatedStartDate_c_hour]').val());
    	jQuery('[name=reminderDateTime_c_minutes]').val(jQuery('[name=estimatedStartDate_c_minutes]').val());   	
		
    }
    else {    	
		jQuery('[id=reminderDateTime]').parent().parent().parent().hide();
		jQuery('[id=reminderDateTime]').removeClass("required");
	
    }	
	
	
}

jQuery(document).ready(function(){
	if(jQuery('[name=hasReminders]') && jQuery('[name=hasReminders]').val() == 'Y' ){
		jQuery('[id=reminderDateTime]').parent().parent().parent().show();
		jQuery('input[name=reminder]').prop("checked", true);				
	}
	else{
		jQuery('[id=reminderDateTime]').parent().parent().parent().hide();
	}	

});


