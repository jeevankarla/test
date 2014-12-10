<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/glDatePicker-2.0/styles/glDatePicker.default.css</@ofbizContentUrl>">
<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/flexselect.css</@ofbizContentUrl>">

<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/media/css/jquery.dataTables.css</@ofbizContentUrl>">
<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/extensions/TableTools/css/dataTables.tableTools.css</@ofbizContentUrl>">

<!--[if lte IE 8]><script language="javascript" type="text/javascript" src="../excanvas.min.js"></script><![endif]-->


<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/media/js/jquery.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/media/js/jquery.dataTables.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/extensions/TableTools/js/dataTables.tableTools.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/media/js/dataTables.plugins.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/glDatePicker-2.0/glDatePicker.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/liquidmetal.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/jquery.flexselect.js</@ofbizContentUrl>"></script>

<style>
.mycalendar {
	width:200px;
 	display:inline;
	margin:5px;
}
.flexselect {
	width:200px;
}
</style>

<script type="text/javascript">

$(document).ready(function(){

  var employees = ${StringUtil.wrapString(employeesJSON!'[]')};
  //alert("employees="+employees);
//$("#employeeId").append('<option value=""></option>');
  $.each(employees, function(key, val){
    $("#employeeId").append('<option value="' + val.employeeId + '">' + val.name + " [" + val.employeeId + "]" + '</option>');
  });
  $("#employeeId").flexselect({
  								preSelection: false,
  								hideDropdownOnEmptyInput: true});	

thruDate = new Date();
$("#thruDate").val(thruDate.getDate() + "/" + (thruDate.getMonth() + 1) + "/" + thruDate.getFullYear());

fromDate = new Date(thruDate);
fromDate.setDate(thruDate.getDate() - 45);
$("#fromDate").val(fromDate.getDate() + "/" + (fromDate.getMonth() + 1) + "/" + fromDate.getFullYear());

$("#thruDate").glDatePicker(
{
	selectedDate: thruDate,
    onClick: function(target, cell, date, data) {
        target.val(date.getDate() + "/" + (date.getMonth() + 1) + "/" +date.getFullYear());

        if(data != null) {
            alert(data.message + '\n' + date);
        }
    }
});	

$("#fromDate").glDatePicker(
{
	selectedDate: fromDate,
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
				$("#getEmployeeAttendance").click();
		}
	});
	
	// also set the click handler
  	$("#getEmployeeAttendance").click(function(){  
  	    $('#loader').show();
     	$('#result').hide(); 
  	    
        $.get(  
            "${ajaxUrl}",  
            { employeeId: $("#employeeId").val(),
              fromDate: $("#fromDate").val(),
              thruDate: $("#thruDate").val()},                
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
    
    $('#loader').hide();
    
/*
    $('#loader').show();
	$('#result').hide(); 
    $.get(  
    "${ajaxUrl}",  
    { employeeId: $("#employeeId").val(),
      fromDate: $("#fromDate").val(),
      thruDate: $("#thruDate").val()},  
    function(responseText){  
        $("#result").html(responseText); 
		var reponse = jQuery(responseText);
		var reponseScript = reponse.filter("script");
		// flot does not work well with hidden elements, so we unhide here itself       			
		jQuery.each(reponseScript, function(idx, val) { eval(val.text); } );  
		$('#loader').hide();
		$('#result').show();               
    },  
    "html");  
    
*/

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
        		<td align="right" width="10%"><span class='h3'>Employee: </span></td>
        		<#if security.hasEntityPermission("MYPORTAL", "_HREMPLVIEW", session)>
                	<td width="20%"><input type="display" name="employeeId" id="employeeId" value="${userLogin.partyId?if_exists}" readonly/></td> 
                <#else>
                	<td width="20%"><select id="employeeId" name="employeeId" class="flexselect"></select></td> 
                </#if>        	
        		<td align="right" width="10%"><span class='h3'>From Date: </span></td>
            	<td width="20%"><input class="mycalendar" type="text" id="fromDate" name="fromDate"/></td>        	
        		<td align="right" width="10%"><span class='h3'>Thru Date: </span></td>
            	<td width="20%"><input class="mycalendar" type="text" id="thruDate" name="thruDate"/></td>
				<td><input type="submit" value="Submit" id="getEmployeeAttendance" class="smallSubmit" /></td>
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