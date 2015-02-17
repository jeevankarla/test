
$(function() {
       $('input[name=boothId]').autocomplete({ source: boothsList }).keydown(function(e){
    	   $.ui.autocomplete.filter = function (array, term) { 
               var matcher = new RegExp($.ui.autocomplete.escapeRegex(term), "i");
               return $.grep(array, function (value) {
                   return matcher.test(value.label || value.value || value);
               });
           };

			if (e.keyCode === 13){
		      	 $('input[name=boothId]').autocomplete('close');
		      	 if( typeof $("input[name=boothId]").val() != "undefined"   && $("input[name=boothId]").val() != ""){ 
		      	 }
				return false;
			}
		});
       // booth auto Complete
     $('input[name=boothId]').keypress(function (e) {
			$('input[name=boothId]').autocomplete({ source: boothsList ,filterOpt : ""});	
			$.ui.autocomplete.filter = function (array, term) { 
                var matcher = new RegExp($.ui.autocomplete.escapeRegex(term), "i");
                return $.grep(array, function (value) {
                    return matcher.test(value.label || value.value || value);
                });
            };
	 });
     
  
     // route auto Complete
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
  
     $('#routeId').focus(function (e) {    	 	
			$("#routeId").autocomplete({ source: routesList });	
	 }); 
     
     changeRowColor();
});

//we have to use color scheme for vehcileStatus List
function changeRowColor(){
	   /* var cell;
	    var result = $('tr').find("td:contains('VEHICLE_RETURNED')");
	    alert("==text="+$(result).text()+"=afterTrim="+$.trim($(result).text()));
	    var compText=$.trim($(result).text());
	    if(compText=="VEHICLE_RETURNED"){
	    	var resParent = $(result).parent();
	    	$(resParent).css("background-color", "#D76871");
	   // $(this).css("background-color", "#FFCC88");
        }else{
        	$(resParent).css("background-color", "green");
        }
	    */
	
	
	var tableObj = $('#_col table tr');
	$(tableObj).each( function( index, element ){
	    var result = $(this).find("td:contains('Finalized')");
	    var compText=$.trim($(result).text());
		$(this).css("background-color", "#FF8000");//initlly set to this color
	    if (compText==("Finalized")){
	    	 $(this).css("background-color", "#77BA72");//if finalized then reset
	    }
	    var resultCR = $(this).find("td:contains('CratesReturned')");
	    var compTextCr=$.trim($(resultCR).text());
	    if(compTextCr==("CratesReturned")){
	    	 $(this).css("background-color", "#98928F");// if Crates returned 
	    } 
	    var resultDI = $(this).find("td:contains('Dispatched')");
	    var compTextDi=$.trim($(resultDI).text());
	    if(compTextDi==("Dispatched")){
	    	 $(this).css("background-color", "#FFCC88");// if Crates returned 
	    } 
	    
	   
	});
	
   // 
}
 function checkAllRouteStatus(master){
	 var shipIds = $('input[name="bulkStatusChange"]');
    jQuery.each(shipIds, function() {
         this.checked = master.checked;
     });
	
 }
 function checkAllIndentApprovalStatus(master){
	 var shipIds = $('input[name="IndentHeadApproval"]');
    jQuery.each(shipIds, function() {
         this.checked = master.checked;
     });
 }
 function getAllRouteShipments(){
	 var shipIdsList = $('input[name=bulkStatusChange]:checked');
	 if(shipIdsList.size() <=0) {
	 alert("Please Select at least One Vehicle..!")
		 return false;
	 }
      jQuery.each(shipIdsList, function() {
    	 var shipmentId=$(this).val();
    	  $('#ListSubmitBulk').append('<input type="hidden" name="shipmentIds" value="'+shipmentId+'" />');
     });
      jQuery('#ListSubmitBulk').submit();
 }
 function getAllIndentApprovals(){
	 var custRqstList = $('input[name=IndentHeadApproval]:checked');
	 if(custRqstList.size() <=0) {
	 alert("Please Select at least One Indent..!")
		 return false;
	 }
	 var paramMap='finallist';
	 var finallist=[];
	 var index = 0;
      jQuery.each(custRqstList, function() {
    	  var curreElem = $(this);
  		var varform = curreElem.parent().parent();
  	    var form = curreElem.parent().parent().find("form");
  	    var formId = form.attr('id');
  	    var str = "#"+formId;
  	    var tempQty=$(varform).find("[name='"+"tempQty"+"']");
  	    var tempQty=$(tempQty).val();
    	 var custRqst=$(this).val();
         var inputElementIdSplit = custRqst.split('_');
    	 var statusId="CRQ_SUBMITTED";   	 
    	 var appendStr = "<input type=hidden name=custRequestId_o_"+index+" value="+inputElementIdSplit[0]+" />";
         appendStr += "<input type=hidden name=description_o_"+index+"  value="+inputElementIdSplit[3]+" />";
         appendStr += "<input type=hidden name=quantity_o_"+index+"  value="+tempQty+" />";
         appendStr += "<input type=hidden name=custRequestItemSeqId_o_"+index+"  value="+inputElementIdSplit[1]+" />";
         appendStr += "<input type=hidden name=statusId_o_"+index+"  value="+statusId+" />";
  	$("#ListIndentSubmit").append(appendStr);
  	index = index+1;
     });
      jQuery('#ListIndentSubmit').submit();
 }
 function getAllIndentRejects(){
	 var custRqstList = $('input[name=IndentHeadApproval]:checked');
	 if(custRqstList.size() <=0) {
	 alert("Please Select at least One Indent..!")
		 return false;
	 }
	 var paramMap='finallist';
	 var finallist=[];
	 var index = 0;
      jQuery.each(custRqstList, function() {
    	  var curreElem = $(this);
  		var varform = curreElem.parent().parent();
  	    var form = curreElem.parent().parent().find("form");
  	    var formId = form.attr('id');
  	    var str = "#"+formId;
  	    var tempQty=$(varform).find("[name='"+"tempQty"+"']");
  	    var tempQty=$(tempQty).val();
    	 var custRqst=$(this).val();
         var inputElementIdSplit = custRqst.split('_');
    	 var statusId="CRQ_REJECTED";
    	 var appendStr = "<input type=hidden name=custRequestId_o_"+index+" value="+inputElementIdSplit[0]+" />";
         appendStr += "<input type=hidden name=description_o_"+index+"  value="+inputElementIdSplit[3]+" />";
         appendStr += "<input type=hidden name=quantity_o_"+index+"  value="+tempQty+" />";
         appendStr += "<input type=hidden name=custRequestItemSeqId_o_"+index+"  value="+inputElementIdSplit[1]+" />";
         appendStr += "<input type=hidden name=statusId_o_"+index+"  value="+statusId+" />";
  	$("#ListIndentReject").append(appendStr);
  	index = index+1;
     });
      jQuery('#ListIndentReject').submit();
 }






