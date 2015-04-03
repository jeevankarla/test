jQuery(document).ready(function() {
	rateTypeChangeHandler();
});
function rateTypeChangeHandler(){
	var rateTypeValue = jQuery('[name=uomId]').val();
	if(rateTypeValue == 'LEN_km' ){
		jQuery('[name=kilometers]').parent().parent().show();
		jQuery('[name=rateAmount]').parent().parent().show();
		jQuery('[name=fromDate]').parent().parent().show();
		jQuery('[name=thruDate]').parent().parent().show();
	}else{
		jQuery('[name=kilometers]').parent().parent().hide();
		jQuery('[name=rateAmount]').parent().parent().show();
		jQuery('[name=fromDate]').parent().parent().show();
		jQuery('[name=thruDate]').parent().parent().show();
	}
	
}