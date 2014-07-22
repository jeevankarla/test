function appendParams(formName, existingUrl) {
	var updatedUrl = $(existingUrl).attr('href');
	var resultUrl = updatedUrl + "&finAccountId="  + $('#finAccountId').val() + "&fromDate=" + $('#fromDate').val() + "&thruDate=" + $('#thruDate').val()
	+ "&isPosted=" + $('#isPosted').val() + "&openingBalance=" + $('#openingBalance').val();
	var formId = "#" + formName;
	jQuery(formId).attr("action", FinancialAccountTransForMonth.pdf);	
	$('a[href="'+$(existingUrl).attr('href')+'"]').attr('href', resultUrl);
}
jQuery(document).ready(function(){
	appendParams();
});