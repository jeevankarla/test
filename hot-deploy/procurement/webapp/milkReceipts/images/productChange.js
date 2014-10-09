
jQuery(document).ready(function() {
	jQuery('[name= productId]').parent().parent().parent().hide();
	jQuery('[name= salesQtyKgs]').parent().parent().hide();
});
function amountTypeChange(){
	var amountTypeId = jQuery("[name='amountTypeId']").val();
	if(amountTypeId == "MLK_PROD_VAL"){		
		jQuery('[name= productId]').parent().parent().parent().show();
		jQuery('[name= salesQtyKgs]').parent().parent().show();
		jQuery('[name= amount]').parent().parent().hide();
	}else{
		jQuery('[name= productId]').parent().parent().parent().hide();
		jQuery('[name= salesQtyKgs]').parent().parent().hide();
	}
}