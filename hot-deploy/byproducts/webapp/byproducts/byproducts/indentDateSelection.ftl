<script type="text/javascript">

$(document).ready(function(){	
   
	$( "#indentDate" ).datepicker({
		dateFormat:'MM d, yy',
		changeMonth: true,
		numberOfMonths: 1});
		
	$('#ui-datepicker-div').css('clip', 'auto');

});

</script>
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Select Date</h3>
    </div>
    <div class="screenlet-body" align="center">
    	<form method="post" name="indententryDashBoard" action="<@ofbizUrl>main</@ofbizUrl>">
		<span class='h3'>Select Date: </span><input class='h2' type="text" id="indentDate" name="indentDate" value="${indentDate}"/>
		<input type="submit" value="Submit" id="getCharts" class="smallSubmit"/> 
		</form>		
    </div>
</div>