function hideQuarterFields(){
	if($('#showQuarters').is(":checked")){
			$("#quarter").parent().parent().parent().show();
			var paramName = 'quarter';
  			var customTimePeriodId = $("#QuarterlyTDSForm_customTimePeriodId").val();
  			
		$.ajax({
             type: "POST",
             url: 'getQuartersForYear',
             data: {customTimePeriodId : customTimePeriodId},
             dataType: 'json',
             success: function(result) {
               if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
            	   //populateError(result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]);
               }else{               			
            	    timePeriodList = result["timePeriodList"];
					var optionList = '';   		
					var list= timePeriodList;
					if (list) {		       				        	
			        	for(var i=0 ; i<list.length ; i++){
							var innerList=list[i];
			                optionList += "<option value = " + innerList + " >" + innerList + "</option>";             			
			      		}
			      	}	
					jQuery("#quarter").html(optionList);
               }
             } 
        });
        }
        else{
			$("#quarter").parent().parent().parent().hide();
		}
	}