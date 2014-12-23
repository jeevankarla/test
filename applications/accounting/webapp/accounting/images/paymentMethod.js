var paymentMethodListValues;
function getPaymentMethodTypes() { 	
   var request = 'getPaymentMethodTypes';      
   jQuery.ajax({
        url: request,
        data: {paymentMethodId : jQuery("[name='paymentMethodId']").val()} , 
        dataType: 'json',
        async: false,
        type: 'POST',
        success: function(result){ paymentMethodListValues=result;       	
		
        } //end of success function function
    });   
	setPaymentMethodTypeFields();
 }//end of ajax method

function setPaymentMethodTypeFields() { 
			var allPaymentMethods=paymentMethodListValues['paymentMethodList'];
	for(var i=0 ; i < allPaymentMethods.length ; i++){
		var paymentMethod = allPaymentMethods[i];
		if(jQuery("[name='paymentMethodId']").val() == paymentMethod['paymentMethodId']) {
			var str=paymentMethod['paymentMethodTypeId'];
		if((str.search(/(CASH)+/g) >= 0) ||(str.search(/(CREDITNOTE)+/g) >= 0)|| (str.search(/(DEBITNOTE)+/g) >= 0) ){
		
		jQuery("input[name='paymentRefNum']").parent().parent().hide();
		jQuery("input[name='effictiveDate']").parent().parent().hide();
		jQuery("input[name='issuingAuthority']").parent().parent().hide();
		jQuery("input[name='chequeInFavour']").parent().parent().hide();
		jQuery("input[name='paymentRefNum']").removeClass("required");
		jQuery("input[name='effictiveDate_i18n']").removeClass("required");
		jQuery("input[name='issuingAuthority']").removeClass("required");		
		}else{
			
			jQuery("input[name='paymentRefNum']").parent().parent().show();
			jQuery("input[name='effictiveDate']").parent().parent().show();
			jQuery("input[name='issuingAuthority']").parent().parent().show();
			jQuery("input[name='chequeInFavour']").parent().parent().show();
			//jQuery("input[name='paymentRefNum']").addClass("required"); 
			jQuery("input[name='effictiveDate_i18n']").addClass("required");
			//jQuery("input[name='issuingAuthority']").addClass("required");	
		}
	
		}
	}
	
	
	
	}//end of setPaymentMethodTypeFields method
	
	
jQuery(document).ready(function(){
	
		getPaymentMethodTypes();
		
	});