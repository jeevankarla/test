$(document).ready(function() {
	//callig to grid Hide
	gridHideCall();
	  
	$( "#effectiveDate" ).datepicker({
		dateFormat:'d MM, yy',
		changeMonth: true,
		numberOfMonths: 1,
		onSelect: function( selectedDate ) {
			$( "#effectiveDate" ).datepicker("option", selectedDate );
		}
	});
	$('#ui-datepicker-div').css('clip', 'auto');
	
	$("input").keypress(function(e){
	  if (e.which == $.ui.keyCode.ENTER) {
		  if(e.target.name == "boothId" ){
			  if( typeof $("#boothId").val() != "undefined"   && $("#boothId").val() != ""){ 
		      		updateGrid();
		      }					
	  	  }	 
      	   e.stopPropagation();
      	   e.preventDefault();        	
      }		
	  		
	});
	
});

$(function() {
       $('input[name=changeSave]').click(function (event){
    	   if(!$("#changeindentinit").validate({messages:{
    		   boothId:"" 
    	   }}).form()) return;
    	   _grid.getEditController().commitCurrentEdit();
    	   $('input[name=changeSave]').attr('disabled','disabled');
    	   $('div#changeIndentEntry_spinner').removeClass("errorMessage");
    	   $('div#changeIndentEntry_spinner')
    		  .html('<img src="/images/ajax-loader64.gif">');
    	   	
    	   	var action;
    	   	if(screenFlag == 'DSCorrection'){
    	   		action = "processDSCorrectionMISAjax";
    	   	}
    	   	else{
    	   		action = "processChangeIndentMISAjax";
    	   	}
    	   	var dataString = prepareAjaxDataString();
               $.ajax({
             type: "POST",
             url: action,
             data: dataString,
             dataType: 'json',
             success: function(result) {
            	 var changeFlag = result["indentChangeFlag"];
          	   
               if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){            	  
            	   alert(result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]);
               }else{
            	
            	   $("div#changeIndentEntry_spinner").fadeIn();
            	   $('div#changeIndentEntry_spinner').html();
            	   $('div#changeIndentEntry_spinner').addClass("messageStr");
            	   if(changeFlag == "Changed"){
                	   $('div#changeIndentEntry_spinner').html('<span style="color:green; font-size:10pt; font-stlye:bold">Entry added succesfully for Dealer : "'+$("#boothTooltip").text()+'"</span>');
                	   $('div#changeIndentEntry_spinner').delay(7000).fadeOut('slow');
            	   }
            	   else{
            		   $('div#changeIndentEntry_spinner').delay(20).fadeOut('slow');
            	   }


               }
               cleanUpGrid();
               $("#routeId").focus();
               $('input[name=changeSave]').removeAttr('disabled');
              
             } ,
             error: function() {
            	 	alert(result["_ERROR_MESSAGE_"]);
            	 }
             });   
               gridHideCall();
               return false;
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
       // booth auto Complete
     $('#boothId').keypress(function (e) {
			$("#boothId").autocomplete({ source: boothsList});	
	 });
     
     $('#boothId').keyup(function (e) {
	 	if (e.keyCode == 8 || e.keyCode == 46) {
	 		updateGrid1([]); 
	 		jQuery("#boothId").focus(); 
	      }	
	 });
     $('#routeId').keyup(function (e) {
 	 	if (e.keyCode == 8 || e.keyCode == 46) {
 	 		updateGrid1([]); 
 	 		$("#boothId").val("");
 	 		$('span#boothTooltip').html('<label>'+''+'</label>');
 	 		jQuery("#routeId").focus();
 	 		
 	      }	
 	 });
     
     $('#routeId').keyup(function (e) {
 	 	if (e.which == $.ui.keyCode.ENTER) {
 	 		 $('#routeId').autocomplete('close');
 	 		 $("#boothId").focus();
 	 		 
 	      }	
 	 });
     $('#routeId').blur(function (e) {    	 	
			//$("#routeId").autocomplete({ source: routesList , onblur: function( event, ui ) {
		var rt = $("#routeId").val();		
    	 $('span#routeTooltip').html('<label>'+rt+'</label>');
    	 
			//} });	
	 });
     // route auto Complete
     $('#routeId').keypress(function (e) {    	 	
			$("#routeId").autocomplete({ source: routesList , select: function( event, ui ) {
				$('span#routeTooltip').html('<label>'+ui.item.label+'</label>');
			} });	
	 });
  // route auto Complete
     $('#routeId').focus(function (e) {    	 	
			$("#routeId").autocomplete({ source: routesList });	
	 }); 
});



function prepareAjaxDataString(){
	formId = changeindentinit;		
	 rowCount = 0;	
	 var querystring = "";
	for(i=0; i< data.length;i++){
		var changeItem = data[i];
		var sequenceNum = jQuery("#tempRouteId").val();
		var qty = parseFloat(changeItem["cQuantity"]);
		if( typeof changeItem["cProductId"] != "undefined"   && changeItem["cProductId"] != "" && !isNaN(qty)){				
			
			  querystring += "productId_o_" + rowCount + "=" + changeItem["cProductId"] + "&";
			  querystring +=  "quantity_o_" + rowCount + "=" + qty + "&";
			  querystring += "sequenceNum_o_" + rowCount + "=" + sequenceNum + "&";
			rowCount++; 
		}
		qty = parseFloat(changeItem["dQuantity"]);
		if( typeof changeItem["dProductId"] != "undefined"   && changeItem["dProductId"] != "" && !isNaN(qty)){				
		
			querystring += "productId_o_" + rowCount + "=" + changeItem["dProductId"] + "&";
			querystring +=  "quantity_o_" + rowCount + "=" + qty + "&";
			querystring += "sequenceNum_o_" + rowCount + "=" + sequenceNum + "&";
			rowCount++; 
		}		
	}	 
	
var dataString = $(formId).serialize();
querystring +=dataString;
return querystring;

}







