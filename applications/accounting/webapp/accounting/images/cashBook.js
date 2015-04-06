function appendParams(formName, existingUrl) {
	var updatedUrl = $(existingUrl).attr('href');
	var url = updatedUrl.split("&finAccountId");
	var resultUrl = url[0] + "&finAccountId="  + $('#finAccountId').val() + "&fromDate=" + $('#fromDate').val() + "&thruDate=" + $('#thruDate').val()
	+ "&isPosted=" + $('#isPosted').val();
	var formId = "#" + formName;
	jQuery(formId).attr("action", FinancialAccountTransForMonth.pdf);	
	$('a[href="'+$(existingUrl).attr('href')+'"]').attr('href', resultUrl);
	location.reload();
}
jQuery(document).ready(function(){
	appendParams();
});