function deleteProcurementEntry(thisValue,orderId,orderItemSeqId){
	if((orderId !='')&&(orderItemSeqId !='')){		
		var action = "deleteProcurementEntryAjax";
		var dataJson = {"orderId":orderId,						
						"orderItemSeqId":orderItemSeqId,												
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
						alert("Sucessfully deleted");						
						$(thisValue).parent().parent().hide();	
					}								 
				},
				error: function(){
					
				}							
			});
		}
	
}
