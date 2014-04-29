<script type="text/javascript">
var trucksheetInProgress = false;
function refresh() {
    // make Ajax call here
    $.get(  
	    "TruckSheetMotherDairyInternal",  
	    { },  
	    function(responseText){  
	    	trucksheetInProgress = false;
	        $("#result").html(responseText); 
			var reponse = jQuery(responseText);
			var reponseScript = reponse.filter("script");
			// flot does not work well with hidden elements, so we unhide here itself     			
			jQuery.each(reponseScript, function(idx, val) { eval(val.text); } );  	
			$("input[name='statusId']").each(function() {
    			var trucksheetStatus = $(this).val(); 
//alert("trucksheetStatus=" + trucksheetStatus);
    			if (trucksheetStatus == "IN_PROCESS" || trucksheetStatus == "CANCEL_INPROCESS") {
    				trucksheetInProgress = true;
    				setTimeout(refresh, 5000);
    			}
			});     
			if (trucksheetInProgress == false) {
				$('#loader').hide();		
			}
			$('#result').show();  		          
	    },  
	    "html"  
	); 
}

$(document).ready(function() {
	$(".screenlet").wrap("<div id='result'></div>");
	var html = "<div id='loader' > <p align='center' style='font-size: large;'> " + 
			   "<img src='<@ofbizContentUrl>/images/ajax-loader64.gif</@ofbizContentUrl>'></p></div>";
  	$("#result").before(html);	
	$('#loader').hide();
	$("input[name='statusId']").each(function() {
    	var trucksheetStatus = $(this).val(); 
    	if (trucksheetStatus == "IN_PROCESS" || trucksheetStatus == "CANCEL_INPROCESS") {
    		trucksheetInProgress = true;
    		setTimeout(refresh, 5000);
    		$('#loader').show();		
    	}
	});
});
</script>