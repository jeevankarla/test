function paymentFieldsOnchange(){
	var str=jQuery("select[name='paymentMethodTypeId']").val();	
	if((str.search(/(CASH)+/g) >= 0) ||(str.search(/(CREDITNOTE)+/g) >= 0)){
		
		jQuery("input[name='paymentRefNum']").parent().parent().hide();
		jQuery("input[name='effectiveDate']").parent().parent().hide();
		jQuery("input[name='issuingAuthority']").parent().parent().hide();
		jQuery("input[name='issuingAuthorityBranch']").parent().parent().hide();
		jQuery("input[name='paymentRefNum']").removeClass("required");
		jQuery("input[name='effectiveDate']").removeClass("required");
		jQuery("input[name='issuingAuthority']").removeClass("required");
		jQuery("input[name='issuingAuthorityBranch']").removeClass("required");
	}else{
	
		jQuery("input[name='paymentRefNum']").parent().parent().show();
		jQuery("input[name='effectiveDate']").parent().parent().show();
		jQuery("input[name='issuingAuthority']").parent().parent().show();
		jQuery("input[name='issuingAuthorityBranch']").parent().parent().show();
		jQuery("input[name='paymentRefNum']").addClass("required");
		jQuery("input[name='effectiveDate']").addClass("required");
		jQuery("input[name='issuingAuthority']").addClass("required");	
		jQuery("input[name='issuingAuthorityBranch']").addClass("required");	
	}
	
}
	
jQuery(document).ready(function() {		
	paymentFieldsOnchange();
});	

