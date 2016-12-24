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


function saveContent(element, finAccountId){
	var isBalConfirmed = element.value;
	var finAccountId = finAccountId;
	
	if(isBalConfirmed != "" && finAccountId != ""){
		jQuery.ajax({
	        url: 'setBankAccountDetails',
	        type: 'POST',
	        async: false,
	        data: {"isBalConfirmed" : isBalConfirmed,
	        		"finAccountId": finAccountId,
	        		"setBankAccountDetails": "Y"},
	        success: function(json) {
	           alert("All is well");
	            result = true;
	        },
	        error: function(error) {
	            alert("problem setting balance confirmation.!");
	            result = false;
	        } 
	    });
	    return result;
	}
}
