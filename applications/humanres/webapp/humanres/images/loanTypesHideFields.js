$(function() {
	var $radios = $('input:radio[name=loanType]');
	if($radios.is(':checked') === false) {
	$radios.filter('[value=internal]').prop('checked', true);
	}
});


function hideFields(obj){
	var loanType =  $(obj).val();
	var formObj = $(obj).parent().parent().parent().parent();
	var textForm = $(formObj).html();
	//alert("tets ####"+JSON.stringify(textForm));
	if(loanType == "external"){
		$("#numCompInterestInst").parent().parent().show();
		$("#numCompPrincipalInst").parent().parent().show();
		
		$("#numCompInterestInst").addClass("required");
		$("#numCompPrincipalInst").addClass("required");
    }
    else{
    	$("#numCompInterestInst").parent().parent().hide();
		$("#numCompPrincipalInst").parent().parent().hide();
		
		$("#numCompInterestInst").removeClass("required");
		$("#numCompPrincipalInst").removeClass("required");
	}
	
	$.ajax({
        type: "POST",
        url: 'getLoanTypeIdsForLoanType',
        data: {loanType : loanType},
        dataType: 'json',
        success: function(result) {
          if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
       	   //populateError(result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]);
          }else{               			
        	  loanTypeIdsList = result["loanTypeIdsList"];
				var optionList = '';   		
				var list= loanTypeIdsList;
				if (list) {		
					optionList += "<option value = " + "" + " >" + "" + "</option>";
		        	for(var i=0 ; i<list.length ; i++){
						var innerList=list[i];
		                optionList += "<option value = " + innerList.loanTypeId + " >" + innerList.description + "</option>";             			
		      		}
		      	}	
				jQuery("#loanTypeId").html(optionList);
				
				if(loanType == "external"){
					jQuery("#statusId").val("LOAN_DISBURSED");
				}else{
					jQuery("#statusId").val("LOAN_APPROVED");
				}
          	}
        } 
   });
}