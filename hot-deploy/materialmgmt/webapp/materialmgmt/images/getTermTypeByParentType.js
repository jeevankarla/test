function termTypesByParent(){
	var parentTypeId = jQuery("select[name='parentTypeId']").val();
	showOrderTax();
	$.ajax({
         type: "POST",
         url: 'getTermTypesByParentType',
         data: {parentTypeId : parentTypeId},
         dataType: 'json',
         success: function(result) {
           if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
        	   //populateError(result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]);
           }else{               			
        	    resultList = result["termTypeIdList"];
        	   	var optionList = "";
        	   	var list= resultList;
				if (list) {		       				        	
		        	for(var i=0 ; i<list.length ; i++){
						var innerList=list[i];	              			             
		                optionList += "<option value = " + innerList.termTypeId + " >" + innerList.description + "</option>";          			
		      		}
		      	}	
        	   	jQuery("#termTypeId").html(optionList);
           }
         } 
    });
}
jQuery(document).ready(function() {
	$('#orderTaxTypeId').parent().parent().parent().hide();
	
});
function showOrderTax(){
	var parentTypeId = jQuery("select[name='parentTypeId']").val();
	var uomId = jQuery("select[name='uomId']").val();
	var termTypeId = jQuery("select[name='termTypeId']").val();
	if(parentTypeId=="TAX" && uomId=="PERCENT"){
	$('#termValue').parent().parent().hide();
	$('#orderTaxTypeId').parent().parent().parent().show();
		$.ajax({
	        type: "POST",
	        url: 'getOrderTaxTypeByTermType',
	        data: {termTypeId : termTypeId},
	        dataType: 'json',
	        success: function(result) {
	          if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
	       	   //populateError(result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]);
	          }else{               			
	       	    resultList = result["orderTaxTypeList"];
	       	   	var optionList = "";
	       	   	var list= resultList;
					if (list) {		       				        	
			        	for(var i=0 ; i<list.length ; i++){
							var innerList=list[i];	              			      
			                optionList += "<option value = " + innerList.taxRate + " >" + innerList.taxRate + "</option>";
			      		}
			      	}	
	       	   	jQuery("#orderTaxTypeId").html(optionList);
	          }
	        } 
	   });
	}else{
		$('#orderTaxTypeId').parent().parent().parent().hide();
		$('#termValue').parent().parent().show();
	}
	
}
function setTermValue(){
	var orderTaxTypeId = jQuery("select[name='orderTaxTypeId']").val();
	var parentTypeId = jQuery("select[name='parentTypeId']").val();
	var uomId = jQuery("select[name='uomId']").val();
	var termTypeId = jQuery("select[name='termTypeId']").val();
	if(parentTypeId=="TAX" && uomId=="PERCENT"){
		$('#termValue').val(orderTaxTypeId);
	}
}