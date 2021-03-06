function paymentFieldsOnchange(){
	var str=jQuery("#paymentMethodTypeId").val();
	var paymentMethodType = jQuery("select[name='paymentTypeId']").val();
	if(str == undefined){
		return;
	}
	if(str.search(/(CASH)+/g) >= 0){
		jQuery("input[name='issuingAuthority']").parent().parent().hide();
		jQuery("input[name='issuingAuthorityBranch']").parent().parent().hide();
		jQuery("input[name='issuingAuthority']").removeClass("required");
		jQuery("input[name='issuingAuthorityBranch']").removeClass("required");
		
		if(paymentMethodType == "PENALTY_PAYIN"){
			jQuery("input[name='paymentRefNum']").parent().parent().show();
			jQuery("input[name='instrumentDate']").parent().parent().show();
			jQuery("input[name='paymentRefNum']").addClass("required");
			jQuery("input[name='instrumentDate']").addClass("required");
		}else{
			jQuery("input[name='paymentRefNum']").parent().parent().hide();
			jQuery("input[name='paymentRefNum']").removeClass("required"); 
			jQuery("input[name='instrumentDate']").parent().parent().hide();
			jQuery("input[name='instrumentDate']").removeClass("required");
		}
	}else{
	
		jQuery("input[name='paymentRefNum']").parent().parent().show();
		jQuery("input[name='instrumentDate']").parent().parent().show();
		jQuery("input[name='issuingAuthority']").parent().parent().show();
		jQuery("input[name='issuingAuthorityBranch']").parent().parent().show();
		jQuery("input[name='paymentRefNum']").addClass("required");
		jQuery("input[name='instrumentDate']").addClass("required");
		jQuery("input[name='issuingAuthority']").addClass("required");	
		jQuery("input[name='issuingAuthorityBranch']").addClass("required");	
	}
	
}
	
jQuery(document).ready(function() {		
	paymentFieldsOnchange();
});	

