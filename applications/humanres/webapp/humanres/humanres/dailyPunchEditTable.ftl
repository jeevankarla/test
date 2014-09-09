<!-- <link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/glDatePicker-2.0/styles/glDatePicker.default.css</@ofbizContentUrl>">

<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/media/css/jquery.dataTables.css</@ofbizContentUrl>">
<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/extensions/TableTools/css/dataTables.tableTools.css</@ofbizContentUrl>">

<!--[if lte IE 8]><script language="javascript" type="text/javascript" src="../excanvas.min.js"></script><![endif]-->

<!-- <script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/media/js/jquery.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/media/js/jquery.dataTables.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/extensions/TableTools/js/dataTables.tableTools.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/media/js/dataTables.plugins.js</@ofbizContentUrl>"></script>-->

<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/glDatePicker-2.0/styles/glDatePicker.default.css</@ofbizContentUrl>">

<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/extensions/JQuery-DataTables-Editable-1.3/media/css/demo_page.css</@ofbizContentUrl>">
<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/extensions/JQuery-DataTables-Editable-1.3/media/css/demo_table.css</@ofbizContentUrl>">
<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/extensions/JQuery-DataTables-Editable-1.3/media/css/demo_table_jui.css</@ofbizContentUrl>">
<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/extensions/JQuery-DataTables-Editable-1.3/media/css/themes/base/jquery-ui.css</@ofbizContentUrl>">
<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/extensions/JQuery-DataTables-Editable-1.3/media/css/themes/smoothness/jquery-ui-1.7.2.custom.css</@ofbizContentUrl>">
<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/flexselect.css</@ofbizContentUrl>">


<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/extensions/JQuery-DataTables-Editable-1.3/media/js/complete.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/extensions/JQuery-DataTables-Editable-1.3/media/js/jquery-1.4.4.min.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/glDatePicker-2.0/glDatePicker.js</@ofbizContentUrl>"></script>

<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/extensions/JQuery-DataTables-Editable-1.3/media/js/jquery.dataTables.min.js</@ofbizContentUrl>"></script>

<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/extensions/JQuery-DataTables-Editable-1.3/media/js/jquery.dataTables.editable.js</@ofbizContentUrl>"></script>

<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/extensions/JQuery-DataTables-Editable-1.3/media/js/jquery.jeditable.js</@ofbizContentUrl>"></script>

<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/extensions/JQuery-DataTables-Editable-1.3/media/js/jquery-ui.js</@ofbizContentUrl>"></script>

<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/extensions/JQuery-DataTables-Editable-1.3/media/js/jquery.validate.js</@ofbizContentUrl>"></script>

<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/liquidmetal.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/jquery.flexselect.js</@ofbizContentUrl>"></script>
<script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datetimepicker/jquery-ui-timepicker-addon-0.9.3.js</@ofbizContentUrl>"></script>


<style>
.mycalendar {
	width:200px;
 	display:inline;
	margin:5px;
}
.dataTables_info { padding-top: 0; }
			.dataTables_paginate { padding-top: 0; }
			.css_right { float: right; }
			#example_wrapper .fg-toolbar { font-size: 0.8em }
			#theme_links span { float: left; padding: 2px 10px; }
</style>

<script type="text/javascript">

$(document).ready(function(){

currentDate = new Date();
$("#punchDate").val(currentDate.getDate() + "/" + (currentDate.getMonth() + 1) + "/" + currentDate.getFullYear());

 $("#punchDate").glDatePicker({
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
            { punchDate: $("#punchDate").val() , partyId : $("#employeeId").val() },  
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
    
     var employees = ${StringUtil.wrapString(employeesJSON!'[]')};
  //alert("employees="+employees);
  $.each(employees, function(key, val){
    $("#employeeId").append('<option value="' + val.employeeId + '">' + val.name + " [" + val.employeeId + "]" + '</option>');
  });
  $("#employeeId").flexselect({
  						preSelection: false,
  						hideDropdownOnEmptyInput: true});	
    
    
    
});

function setTimepicker(){
       //alert("hi");
		$('#punchtime').timepicker({ 
		showSecond: true,	
		timeFormat: 'hh:mm:ss',
		showOn: 'button',
	        buttonImage: '/vasista/images/cal.gif',
	 });
}

</script>

<div class="screenlet">
	<div class="screenlet-title-bar">
      	<h3>Select Date</h3>	
     </div>
    <div class="screenlet-body">
	  <form name="attendance" action="#" method="post">
		<table class="basic-table" cellspacing="0">
        	<tr>
        		<td align="right" width="10%"><span class='h3'>Attendance Date: </span></td>
            	<td width="20%"><input class="mycalendar" type="text" id="punchDate" name="punchDate"/></td>
            	<td align="right" width="10%"><span class='h3'>Employee Id: </span></td>
            	<td width="20%">
            	     <select id="employeeId" name="employeeId" class="flexselect" required>
            	     </select>
                  </td>
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