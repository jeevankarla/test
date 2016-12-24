jQuery(document).ready(function() {
	setPaymentType();
	
});

function setPaymentType(){
	var finAccountTransType=jQuery("[name='finAccountTransTypeId']").val();
	if(finAccountTransType == 'DEPOSIT' ){
		jQuery("[name='parentTypeId']").val('RECEIPT');	
	}
	if(finAccountTransType == 'WITHDRAWAL' ){
		jQuery("[name='parentTypeId']").val('DISBURSEMENT');				
	}

}
