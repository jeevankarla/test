<script type="text/javascript">

var data = ${StringUtil.wrapString(headItemsJson)!'[]'};
periodBillingId="${context.periodBillingId?if_exists}";
var payrollInProgress = false;
function refresh() {	
    // make Ajax call here
     
     $.ajax({
			 type: "POST",
             url: 'FindPayrollBillingStatus',
             data: {periodBillingId : periodBillingId},
             dataType: 'json',
     		
	   		success: function(result) {
               if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){            	  
            	   alert(result["_ERROR_MESSAGE_"]);
               }else{
               		statusId = result["statusId"];
               		var payrollInProgress = false;
               		if (statusId == "IN_PROCESS" || statusId == "CANCEL_INPROCESS") {
	    				payrollInProgress = true;
	    				setTimeout(refresh, 5000);
	    			}
               		if (payrollInProgress == false) {
						$('#loader').hide();		
					}
					$('#result').show();  
               }
           }
	},
	"html"  
	); 
}

$(document).ready(function() {
	payrollStatus="${context.statusId?if_exists}";
	$(".screenlet").wrap("<div id='result'></div>");
	var html = "<div id='loader' > <p align='center' style='font-size: large;'> " + 
			   "<img src='<@ofbizContentUrl>/images/ajax-loader64.gif</@ofbizContentUrl>'></p></div>";
  	$("#result").before(html);	
	$('#loader').hide();
	if (payrollStatus == "IN_PROCESS" || payrollStatus == "CANCEL_INPROCESS") {
		payrollInProgress = true;
		setTimeout(refresh, 3000);
		$('#loader').show();		
	}
	
	
});
</script>