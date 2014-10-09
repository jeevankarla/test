function approveValidationEntry(thisValue,unitId,customTimePeriodId,validationTypeId,sequenceNum){
	if((unitId !='')&&(customTimePeriodId !='')&&(validationTypeId !='')&&(sequenceNum !='')){
		var action = "approveValidationEntryAjax";
		var dataJson = {"unitId":unitId,						
						"customTimePeriodId":customTimePeriodId,
						"validationTypeId":validationTypeId,
						"sequenceNum":sequenceNum,							
					   };
			$.ajax({
				 type: "POST",
	             url: action,
	             data: dataJson,
	             dataType: 'json',	            
				success:function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
						alert("error occured");
					}else{						
						alert("Sucessfully Approved");						
						$(thisValue).parent().parent().hide();	
					}								 
				},
				error: function(){
					
				}							
			});
		}
	
}
