<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/glDatePicker-2.0/styles/glDatePicker.default.css</@ofbizContentUrl>">

<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/media/css/jquery.dataTables.css</@ofbizContentUrl>">
<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/extensions/TableTools/css/dataTables.tableTools.css</@ofbizContentUrl>">

<!--[if lte IE 8]><script language="javascript" type="text/javascript" src="../excanvas.min.js"></script><![endif]-->

<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/media/js/jquery.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/media/js/jquery.dataTables.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/extensions/TableTools/js/dataTables.tableTools.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/media/js/dataTables.plugins.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/glDatePicker-2.0/glDatePicker.js</@ofbizContentUrl>"></script>

<style>
.mycalendar {
	width:200px;
 	display:inline;
	margin:5px;
}
</style>

<script type="text/javascript">

$(document).ready(function(){
currentDate = new Date();
$("#punchDate").val(currentDate.getDate() + "/" + (currentDate.getMonth() + 1) + "/" + currentDate.getFullYear());

$("#punchDate").glDatePicker(
{
	selectedDate: new Date(),
    onClick: function(target, cell, date, data) {
        target.val(date.getDate() + "/" + (date.getMonth() + 1) + "/" +date.getFullYear());

        if(data != null) {
            alert(data.message + '\n' + date);
        }
    }
});		

// enter event handle
	$("input").keypress(function(e){
		if (e.which == 13) {
				$("#getAttendanceTable").click();
		}
	});
	

	// fetch the charts for today
	  	    $('#loader').show();
     		$('#result').hide(); 
	        $.get(  
            "${ajaxUrl}",  
            { punchDate: $("#punchDate").val()},  
            function(responseText){  
                $("#result").html(responseText); 
				var reponse = jQuery(responseText);
       			var reponseScript = reponse.filter("script");
       			// flot does not work well with hidden elements, so we unhide here itself       			
       			jQuery.each(reponseScript, function(idx, val) { eval(val.text); } );  
       			$('#loader').hide();
     			$('#result').show();               
            },  
            "html"  
        );  
        
	// also set the click handler
  	$("#getAttendanceTable").click(function(){  
  	    $('#loader').show();
     	$('#result').hide(); 
  	    
        $.get(  
            "${ajaxUrl}",  
            { punchDate: $("#punchDate").val()},  
            function(responseText){  
                $("#result").html(responseText); 
				var reponse = jQuery(responseText);
       			var reponseScript = reponse.filter("script");
       			// flot does not work well with hidden elements, so we unhide here itself     			
       			jQuery.each(reponseScript, function(idx, val) { eval(val.text); } );       
       			$('#loader').hide();
     			$('#result').show();            
            },  
            "html"  
        );  
        return false;
    });
});

</script>

<div class="screenlet">
	<div class="screenlet-title-bar">
      	<h3>Select Date</h3>	
     </div>
    <div class="screenlet-body">
	  <form name="attendance">
		<table class="basic-table" cellspacing="0">
        	<tr>
        		<td align="right" width="10%"><span class='h3'>Attendance Date: </span></td>
            	<td width="20%"><input class="mycalendar" type="text" id="punchDate" name="punchDate"/></td>
				<td><input type="submit" value="Submit" id="getAttendanceTable" class="smallSubmit" /></td>
			</tr>
    	</table>    	
	</form>
	</div>
</div>

<div id="loader" > 
      <p align="center" style="font-size: large;">
        <img src="<@ofbizContentUrl>/images/ajax-loader64.gif</@ofbizContentUrl>">
      </p>
</div>

<div id="result"/>