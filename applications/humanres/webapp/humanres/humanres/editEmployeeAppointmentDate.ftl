<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/flexselect.css</@ofbizContentUrl>">
<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	

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

	$("#newAppointmentDate").datepicker({
			dateFormat:'yy-mm-dd 00:00:00.000',
			changeMonth: true,
			numberOfMonths: 1,
			maxDate: new Date(),
			onSelect: function( selectedDate ) {
				$("#newAppointmentDate").datepicker("option", selectedDate);
			}
		});
	});


	$(document).ready(function confirmFields(){
		$('input[name=submitButton]').click (function confirmFields(){
			var newAppointmentDate = $('#newAppointmentDate').val();
			var newJoiningDate = $('#newJoiningDate').val();
			
			var date1 = new Date(newAppointmentDate); 
			var date2 = new Date(newJoiningDate); 
			
			if( (date1.getTime() < date2.getTime())){
				alert("You can not choose after joining date");
				return false;
			}
		});
	});

	

</script>

<div class="screenlet">
	<div class="screenlet-title-bar">
      	<h3>Update Employee Appointment Date</h3>	
     </div>
    <div class="screenlet-body">
	  <form name="updateEmployeeJoiningDate" method="post" action="<@ofbizUrl>updateEmployeeAppointmentDate</@ofbizUrl>">
		<table class="basic-table" cellspacing="0">
			<tr>
				<td align="left" width="20%"><h4>PartyId</h4></td>
				<td width="80%"><input type="text" name="partyId" id="partyId" value="${partyId?if_exists}" size="30" maxlength="60" readonly /></td>
			</tr>
			<tr><td align="left" width="30%"><h4>Existing Appointment Date:</h4></td>
				<td width="85%"><input type="text" id="oldAppointmentDate" name="oldAppointmentDate" value="${oldAppointmentDate?if_exists}" readonly/> </td>
			</tr>
			<tr><td align="left" width="30%"><h4>New Appointment Date:</h4></td>
				<td width="85%"><input type="text" id="newAppointmentDate" name="newAppointmentDate"/> </td>
			</tr>
			<tr>
				<td></td>
				<td><input type="submit" name="submitButton" id="submitButton" value="Update" onClick="javascript:confirmFields();" style="buttontext"/></td>
			</tr>
			
    	</table>	
	</form>
	</div>
</div>


