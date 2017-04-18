<script type="text/javascript">
$(document).ready(function(){
getBOsForRO();

});


function getBOsForRO() {	    
		var ro = $('#organizationPartyId :selected').val();
		var dataJson = {"roId": ro};
		jQuery.ajax({
                url: 'getBOsForRO',
                type: 'POST',
                data: dataJson,
                dataType: 'json',
               success: function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					    alert("Error in order Items");
					}else{
						var tempId = $("#costCenterId").val();
						var orderList = result["orderList"];
						var getROList = result["getROList"];
						var tableElement = "";
						tableElement +="<option value=''></option>";
						tableElement +="<option value='All'>"+"All"+"</option>";						
						 $.each(getROList, function(key, item){
						 			if(item['partyIdTo'] == tempId){
						 				tableElement +="<option selected value='"+item['partyIdTo']+"'>"+item['groupName']+"</option>";
						 			}
						 			else{
						 				tableElement +="<option value='"+item['partyIdTo']+"'>"+item['groupName']+"</option>";
						 			}
		       	  				    
		       	  				 });
						 $.each(orderList, function(key, item){
						 			if(item['partyIdTo'] == tempId){	
						 				tableElement +="<option selected value='"+item['partyIdTo']+"'>"+item['groupName']+"</option>";
						 			}
						 			else{
						 				tableElement +="<option value='"+item['partyIdTo']+"'>"+item['groupName']+"</option>";
						 			}
		       	  				 });
		       	  				 if(tableElement.length > 0)
		       	  			     $('#costCenterId').empty().append(tableElement);
		       	  			     else
		       	  			     $('#costCenterId').empty();	
						
               		}
               	}							
		});
	}
	
	
	

</script>
	