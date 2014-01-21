//this Js will get the all roletype Map for the selected invoiceType

var roleTypeListValues;
function getRoleTypes() { 	
   var request = 'getInvoiceTypeRoleTypes';      
   jQuery.ajax({
        url: request,
        data: {invoiceTypeId : jQuery("[name='invoiceTypeId']").val()} , 
        dataType: 'json',
        async: false,
        type: 'POST',
        success: function(result){ roleTypeListValues=result;       	
		
        } //end of success function function
    });
	setRoleTypeDropdown();
 }


//new function

function setRoleTypeDropdown()
{	var optionList = '';
   	var invoiceTypeId =jQuery("[name='invoiceTypeId']").val();
	var list=roleTypeListValues['roleTypeList'];
	if (list) {        				        	
        	for(var i=0 ; i<list.length ; i++){
			var innerList=list[i];
			if(invoiceTypeId == innerList['invoiceTypeId']){
				var roleTypeOptions=innerList['roleType'];				
				if (roleTypeOptions) { 					       				        	
        				for(var j=0 ; j<roleTypeOptions.length ; j++){
        					jQuery.each(roleTypeOptions[j], function(key, value){                   			             
                            			optionList += "<option value = " + value['roleTypeId'] + " >" + value['description'] + "</option>";                    
                  
                		})  //end of roleTypeOptions for each loop
                  }//end of roleTypeOptions for loop          
            }//end of list	 				
	     }// end of invoiceTypeId Check         			
      }//end of main list for loop		
         jQuery("[name='roleTypeId']").html(optionList);
   }//end of main list
}

function updateTimePeriodField()
{	
	var allInvoiceTypes=roleTypeListValues['roleTypeList'];
	for(var i=0 ; i < allInvoiceTypes.length ; i++){
		var invoiceType = allInvoiceTypes[i];
		if(jQuery("[name='invoiceTypeId']").val() == invoiceType['invoiceTypeId']) {
			if ('N' == invoiceType['isPeriodInvoice']) {
				jQuery("[name='timePeriodId']").parent().parent().parent().hide();					
				jQuery("[name='timePeriodId']").attr("disabled","disabled");
			}	
			else {
				jQuery("[name='timePeriodId']").parent().parent().parent().show();					
				jQuery("[name='timePeriodId']").removeAttr("disabled");				
			}
		}
	}
}

