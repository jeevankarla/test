var screenFlag;
$(document).ready(function() {
	
	$( "#effectiveDate" ).datepicker({
		dateFormat:'d MM, yy',
		changeMonth: true,
		numberOfMonths: 1,
		onSelect: function( selectedDate ) {
			$( "#effectiveDate" ).datepicker("option", selectedDate );
		}
	});
	$('#ui-datepicker-div').css('clip', 'auto');
	screenFlag = $("#screenFlag").val();
	$("input").keypress(function(e){
		if(screenFlag == "returns"){
			if (e.which == $.ui.keyCode.ENTER) {
				if(e.target.name == "boothId" ){
					if( typeof $("#boothId").val() != "undefined"   && $("#boothId").val() != ""){ 
						updateGrid();
					}					
				}
				e.stopPropagation();
		      	e.preventDefault(); 
			}
		}
		else{
			if (e.which == $.ui.keyCode.ENTER) {
				if(e.target.name == "routeId" ){
					if( typeof $("#routeId").val() != "undefined"   && $("#routeId").val() != ""){ 
						updateGrid();
					}					
				}
				e.stopPropagation();
		      	e.preventDefault(); 
			}
		}
	});
	
});

$(function() {
       $('input[name=changeSave]').click(function (event){
    	   if(!$("#reconcilEntryInit").validate({messages:{
    		   routeId:"" 
    	   }}).form()) return;
    	   _grid.getEditController().commitCurrentEdit();
    	   $('input[name=changeSave]').attr('disabled','disabled');
    	   $('div#changeIndentEntry_spinner').removeClass("errorMessage");
    	   $('div#changeIndentEntry_spinner')
    		  .html('<img src="/images/ajax-loader64.gif">');
    	   	   var action;
    	   	   if(screenFlag == "returns"){
    	   		   action = "processReturnItemsMISAjax";
    	   	   }
    	   	   else{
    	   		   action = "processDispatchReconcilMISAjax";
    	   	   }
    	   	   var dataString = prepareAjaxDataString();
               $.ajax({
             type: "POST",
             url: action,
             data: dataString,
             dataType: 'json',
             success: function(result) {
            	 
               if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){            	  
            	   alert(result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]);
               }else{
            	   $("div#changeIndentEntry_spinner").fadeIn();
            	   $('div#changeIndentEntry_spinner').html();
            	   $('div#changeIndentEntry_spinner').addClass("messageStr");
               	   $('div#changeIndentEntry_spinner').html('<span style="color:green; font-size:10pt; font-stlye:bold">Succesfully added Dispatch Reconsiliation Entry for Route : "'+$("#routeTooltip").text()+'"</span>');
               	   $('div#changeIndentEntry_spinner').delay(7000).fadeOut('slow');
               }
               cleanUpGrid();
               $("#routeId").focus();
               $('input[name=changeSave]').removeAttr('disabled');
              
             } ,
             error: function() {
            	 	alert(result["_ERROR_MESSAGE_"]);
            	 }
             });   
              
               return false;
       });
       
       
       $("#routeId").autocomplete({ source: routesList }).keydown(function(e){
    	   if(screenFlag != "returns"){
				if (e.keyCode === 13){
			      	 $('#routeId').autocomplete('close');
			      	 if( typeof $("#routeId").val() != "undefined"   && $("#routeId").val() != ""){ 
			      		updateGrid();
			      	 }
					return false;
				}
       		}
		});
       $("#boothId").autocomplete({ source: boothsList }).keydown(function(e){
			if (e.keyCode === 13){
		      	 $('#boothId').autocomplete('close');
		      	 if( typeof $("#boothId").val() != "undefined"   && $("#boothId").val() != ""){ 
		      		updateGrid();
		      	 }
				return false;
			}
		});
       $('#boothId').keypress(function (e) {
			$("#boothId").autocomplete({ source: boothsList});	
       });
       // booth auto Complete
      
     $('#routeId').keyup(function (e) {
	 	if (e.keyCode == 8 || e.keyCode == 46) {
	 		updateGrid1([]); 
	 		jQuery("#routeId").focus(); 
	      }	
	 });
     //if(screenFlag != "returns"){
     // route auto Complete
	     $('#routeId').keypress(function (e) {    	 	
				$("#routeId").autocomplete({ source: routesList , select: function( event, ui ) {
					$('span#routeTooltip').html('<label>'+ui.item.name+'</label>');
				} });	
		 });
     
	  // route auto Complete
	     $('#routeId').focus(function (e) {    	 	
				$("#routeId").autocomplete({ source: routesList });	
		 });
     //}
});



function prepareAjaxDataString(){
	formId = reconcilEntryInit;		
	 rowCount = 0;	
	 var querystring = "";
	for(i=0; i< data.length;i++){
		var changeItem = data[i];
		var qty;
		if(screenFlag == "returns"){
			qty = parseFloat(changeItem["returnQuantity"]);
		}
		else{
			 qty = parseFloat(changeItem["cQuantity"]);
		}
		if( typeof changeItem["cProductId"] != "undefined"   && changeItem["cProductId"] != "" && !isNaN(qty)){				
			  querystring += "productId_o_" + rowCount + "=" + changeItem["cProductId"] + "&";
			  querystring +=  "quantity_o_" + rowCount + "=" + qty + "&";
			  rowCount++; 
		}
		/*qty = parseFloat(changeItem["dQuantity"]);
		if( typeof changeItem["dProductId"] != "undefined"   && changeItem["dProductId"] != "" && !isNaN(qty)){				
		
			querystring += "productId_o_" + rowCount + "=" + changeItem["dProductId"] + "&";
			querystring +=  "quantity_o_" + rowCount + "=" + qty + "&";
			rowCount++; 
		}	*/	
	}	 
	
	var dataString = $(formId).serialize();
	querystring +=dataString;
	return querystring;

}




