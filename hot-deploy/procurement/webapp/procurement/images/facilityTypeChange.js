jQuery(document).ready(function() {
	facilityTypeChangeHandler();
	$("input").keyup(function(e){
		var fieldName =e.target.name; 
		if(fieldName== "shortName" || fieldName=="finAccountBranch"){
			var fieldValue = $(this).val();
			if(fieldValue !='' ){
				$('[name='+fieldName+']').val((e.target.value).toUpperCase());
			}
			
		}
		if(fieldName== "finAccountCode"){
			if(e.which == 110 || e.which == 190){
    			$(this).val( $(this).val().replace('.',''));
    		}
    		$(this).val( $(this).val().replace(/[^0-9\.]/g,''));
		}
		if(fieldName== "bCode"){
			if(e.which == 110 || e.which == 190){
    			$(this).val( $(this).val().replace('.',''));
    		}
    		$(this).val( $(this).val().replace(/[^0-9\.]/g,''));
		}
		if(fieldName== "gbCode"){
			if(e.which == 110 || e.which == 190){
    			$(this).val( $(this).val().replace('.',''));
    		}
    		$(this).val( $(this).val().replace(/[^0-9\.]/g,''));
		}
		
	});
});

function checkBankDetails(){
	var facilityType = $('[name = facilityTypeId]').val();
	if(facilityType){
		if(facilityType == "CENTER" || facilityType=="UNIT"){
			jQuery('name= bCode').addClass("required");
			jQuery('name=gbCode').addClass("required");
			jQuery('name = finAccountBranch').addClass("required");
			var bano = $('[name = finAccountCode]').val();
			if(bano){
				jQuery('name= bCode').addClass("required");
				jQuery('name=gbCode').addClass("required");
				jQuery('name = finAccountBranch').addClass("required");
				
				var branchName = jQuery('[name= finAccountBranch]').val();
				var bCode = jQuery('[name = bCode]').val();
				var gbCode = jQuery('[name = gbCode]').val();
				if(branchName){
					branchName = branchName.trim();
				}
				if(bCode && gbCode && branchName){
					if(bCode!=0 && gbCode!=0){
						return true;
					}	
					
				}
			}
			alert('please Check Bank Details ===');
			return false;
		}else{
			return true;
		}
			
	}
	
	return true;
	
}

function facilityTypeChangeHandler(){
	var facilityTypeValue=jQuery('[name=facilityTypeId]').val();
	if(facilityTypeValue == 'CENTER' ){
		jQuery('[name=unitId]').parent().parent().parent().show();
		jQuery('[name=routeId]').parent().parent().parent().show();
		jQuery('[name= bankDetails]').parent().parent().show();
		jQuery('[name= bankName]').parent().parent().parent().show();
		jQuery('[name= shortName]').parent().parent().show();
		jQuery('[name= finAccountBranch]').parent().parent().show();
		jQuery('[name= finAccountCode]').parent().parent().show();
		jQuery('[name= gbCode]').parent().parent().show();
		jQuery('[name= bCode]').parent().parent().show();
		jQuery('[name= bPlace]').parent().parent().show();
		jQuery('[name= ifscCode]').parent().parent().show();
		
		jQuery('[name= opCost]').parent().parent().hide();
		jQuery('[name= opCostUomId]').parent().parent().parent().hide();
		jQuery('[name= eOpCost]').parent().parent().hide();
		jQuery('[name= eOpCostUomId]').parent().parent().parent().hide();
		jQuery('[name= cartageUomId]').parent().parent().parent().show();
		jQuery('[name= commissionUomId]').parent().parent().parent().show();
		
		jQuery('[name= commission]').parent().parent().show();
		jQuery('[name= cartage]').parent().parent().show();	
		
		jQuery('[name= bankName]').addClass("required");
		jQuery('[name= finAccountBranch]').addClass("required");
		jQuery('[name= finAccountCode]').addClass("required");
		jQuery('[name= gbCode]').addClass("required");
		jQuery('[name= bCode]').addClass("required");
	}else if(facilityTypeValue=='UNIT'){
		jQuery('[name= opCost]').parent().parent().show();
		jQuery('[name= opCostUomId]').parent().parent().parent().show();
		jQuery('[name= eOpCost]').parent().parent().show();
		jQuery('[name= eOpCostUomId]').parent().parent().parent().show();
		jQuery('[name= unitId]').parent().parent().parent().hide();
		jQuery('[name= routeId]').parent().parent().parent().hide();
		jQuery('[name= bankDetails]').parent().parent().show();
		jQuery('[name= bankName]').parent().parent().parent().show();;
		jQuery('[name= shortName]').parent().parent().show();
		jQuery('[name= finAccountBranch]').parent().parent().show();
		jQuery('[name= finAccountCode]').parent().parent().show();
		jQuery('[name= gbCode]').parent().parent().show();
		jQuery('[name= bCode]').parent().parent().show();
		jQuery('[name= bPlace]').parent().parent().show();
		jQuery('[name= ifscCode]').parent().parent().show();
		jQuery('[name= commission]').parent().parent().show();
		jQuery('[name= cartage]').parent().parent().show();
		jQuery('[name= cartageUomId]').parent().parent().parent().show();
		jQuery('[name= commissionUomId]').parent().parent().parent().show();
		
		jQuery('[name= bankName]').addClass("required");
		jQuery('[name= finAccountBranch]').addClass("required");
		jQuery('[name= finAccountCode]').addClass("required");
		jQuery('[name= gbCode]').addClass("required");
		jQuery('[name= bCode]').addClass("required");
	}else if(facilityTypeValue=='PROC_ROUTE'){
		jQuery('[name=unitId]').parent().parent().parent().show();
		jQuery('[name= routeId]').parent().parent().parent().hide();
		jQuery('[name= bankDetails]').parent().parent().hide();
		jQuery('[name= bankName]').parent().parent().parent().hide();
		jQuery('[name= shortName]').parent().parent().hide();
		jQuery('[name= finAccountBranch]').parent().parent().hide();
		jQuery('[name= finAccountCode]').parent().parent().hide();
		jQuery('[name= gbCode]').parent().parent().hide();
		jQuery('[name= bCode]').parent().parent().hide();
		jQuery('[name= bPlace]').parent().parent().hide();
		jQuery('[name= ifscCode]').parent().parent().hide();
		jQuery('[name= commission]').parent().parent().hide();
		jQuery('[name= cartage]').parent().parent().hide();
		
		jQuery('[name= opCost]').parent().parent().hide();
		jQuery('[name= opCostUomId]').parent().parent().parent().hide();
		jQuery('[name= eOpCost]').parent().parent().hide();
		jQuery('[name= eOpCostUomId]').parent().parent().parent().hide();
		jQuery('[name= cartageUomId]').parent().parent().parent().hide();
		jQuery('[name= commissionUomId]').parent().parent().parent().hide();
		
		jQuery('[name= bankName]').removeClass("required");
		jQuery('[name= shortName]').removeClass("required");
		jQuery('[name= finAccountBranch]').removeClass("required");
		jQuery('[name= finAccountCode]').removeClass("required");
		jQuery('[name= gbCode]').removeClass("required");
		jQuery('[name= bCode]').removeClass("required");
		
	}
}
