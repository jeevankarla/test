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
	width:250px;
}
</style>

<script type="text/javascript">

$(document).ready(function(){

  	var employees = ${StringUtil.wrapString(emplPositionJSON!'[]')};
  	//alert("employees="+employees);
	//$("#emplPositionTypeId").append('<option value=""></option>');
  	$.each(employees, function(key, val){
    	$("#emplPositionTypeId").append('<option value="' + val.emplPositionTypeId + '">' + val.description + " [" + val.emplPositionTypeId + "]" + '</option>');
  	});
  	$("#emplPositionTypeId").flexselect({
  		preSelection: false,
  		hideDropdownOnEmptyInput: true
  	});	

	thruDate = new Date();
	$("#thruDate").val(thruDate.getDate() + "/" + (thruDate.getMonth() + 1) + "/" + thruDate.getFullYear());

	actualFromDate = new Date(thruDate);
	$("#actualFromDate").val(actualFromDate.getDate() + "/" + (actualFromDate.getMonth() + 1) + "/" + actualFromDate.getFullYear());

	$("#actualFromDate").glDatePicker(
	{
		selectedDate: actualFromDate,
    	onClick: function(target, cell, date, data) {
        	target.val(date.getDate() + "/" + (date.getMonth() + 1) + "/" +date.getFullYear());

        	if(data != null) {
           		alert(data.message + '\n' + date);
        	}
    	}
	});		
});

</script>

<div class="screenlet">
	<div class="screenlet-title-bar">
      	<h3>Update Employee Position</h3>	
     </div>
    <div class="screenlet-body">
	  <form name="updateEmployeePosition" method="post" action="<@ofbizUrl>updateEmployeePosition</@ofbizUrl>">
		<table class="basic-table" cellspacing="0">
			<tr>
        		<td align="left" width="20%"><h4>Existing Position: </h4></td>
        		<#list EmplPositionAndFulfillmentList as EmplPositionFulfillment>
        			<td width="80%">${EmplPositionFulfillment.emplPositionTypeId?if_exists}</td>
        		</#list>
        	</tr>
        	<tr>
        		<td align="left" width="20%"><h4>Designation: </h4></td>
        		<td width="80%"><select id="emplPositionTypeId" name="emplPositionTypeId" class="flexselect"></select></td>
			</tr>
			<tr><td align="left" width="15%"><h4>FromDate:</h4></td>
				<td width="85%"><input class="mycalendar" type="text" id="actualFromDate" name="actualFromDate"/> </td>
			</tr>
			<tr><td align="left" width="15%"><h4>&nbsp;</h4></td>
				<#list EmplPositionList as EmplPosition>
        			<input type="hidden" name="partyId" value="${EmplPosition.partyId?if_exists}" />
        		</#list>
        		<input type="hidden" name="emplPositionId" value="${emplPositionId?if_exists}" />
				<td width="85%"><input align="right" type="submit" value="Update" id="updateEmployeePosition" class="smallSubmit" /></td>
			</tr>
    	</table>	
	</form>
	</div>
</div>


