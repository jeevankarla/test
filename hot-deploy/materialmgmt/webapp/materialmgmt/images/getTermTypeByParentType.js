function termTypesByParent(){
	var parentTypeId = jQuery("select[name='parentTypeId']").val();
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