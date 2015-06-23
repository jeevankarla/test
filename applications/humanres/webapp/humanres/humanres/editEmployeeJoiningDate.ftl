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

	$("#newJoiningDate").datepicker({
			dateFormat:'yy-mm-dd 00:00:00.000',
			changeMonth: true,
			numberOfMonths: 1,
			maxDate: new Date(),
			onSelect: function( selectedDate ) {
				$("#newJoiningDate").datepicker("option", selectedDate);
			}
		});
	});


	$(document).ready(function confirmFields(){
		$('input[name=submitButton]').click (function confirmFields(){
			var appointmentDate = $('#appointmentDate').val();
			var newJoiningDate = $('#newJoiningDate').val();
			
			var date1 = new Date(appointmentDate); 
			var date2 = new Date(newJoiningDate); 
			
			if( (date1.getTime() > date2.getTime())){
				alert("You can not choose before appointment date");
				return false;
			}
		});
	});

	

</script>

<div class="screenlet">
	<div class="screenlet-title-bar">
      	<h3>Update Employee Joining Date</h3>	
     </div>
    <div class="screenlet-body">
	  <form name="updateEmployeeJoiningDate" method="post" action="<@ofbizUrl>updateEmployeeJoiningDate</@ofbizUrl>">
		<table class="basic-table" cellspacing="0">
			<input type="hidden" id="appointmentDate" name="appointmentDate" value="${appointmentDate?if_exists}"/>
			<tr>
				<td align="left" width="20%"><h4>PartyId</h4></td>
				<td width="80%"><input type="text" name="partyId" id="partyId" value="${partyId?if_exists}" size="30" maxlength="60" readonly /></td>
			</tr>
			<tr><td align="left" width="15%"><h4>Existing Joining Date:</h4></td>
				<td width="85%"><input type="text" id="oldJoiningDate" name="oldJoiningDate" value="${oldJoiningDate?if_exists}" readonly/> </td>
			</tr>
			<tr><td align="left" width="15%"><h4>New Joining Date:</h4></td>
				<td width="85%"><input type="text" id="newJoiningDate" name="newJoiningDate"/> </td>
			</tr>
			<tr>
				<td></td>
				<td><input type="submit" name="submitButton" id="submitButton" value="Update" onClick="javascript:confirmFields();" style="buttontext"/></td>
			</tr>
			
    	</table>	
	</form>
	</div>
</div>


