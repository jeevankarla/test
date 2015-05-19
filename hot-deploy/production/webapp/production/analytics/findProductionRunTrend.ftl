<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
<script type="text/javascript">

$(document).ready(function(){

	$("input").keypress(function(e){
		if (e.which == 13 && e.target.name =="facilityId") {
				$("#getCharts").click();
		}
	});

	$( "#fromDate" ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
					
				date = $(this).datepicker('getDate');
	        	var maxDate = new Date(date.getTime());
	        	maxDate.setDate(maxDate.getDate() + 31);
				$( "#thruDate" ).datepicker( "option", {minDate: selectedDate, maxDate: maxDate}).datepicker('setDate', date);
			}
		});
		$( "#thruDate" ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#fromDate" ).datepicker( "option", "maxDate", selectedDate );
			}
		});
		$('#ui-datepicker-div').css('clip', 'auto');

        
});

</script>

<div class="screenlet">
	<div class="screenlet-title-bar">
      	<h3>Search Field</h3>	
     </div>
    <div class="screenlet-body">
	  <form name="productionRunAnalysis" method="post" action="productionRunTrends">
		<table class="basic-table" cellspacing="0">
			<tr>
        		<td align="right" width="10%"><span class='h3'>From: </span></td>
            	<td width="20%"><input class='h2' type="text" id="fromDate" name="fromDate"/></td>
				<td width="2%"><span class='h3'>To: </span></td>
				<td width="20%"><input class='h2' type="text" id="thruDate" name="thruDate"/></td>
				<td align="right" width="10%"><span class='h3'>Plant/Unit: </span></td>
				<td align="left" width="10%">
					<select name="facilityId" class='h3'>
						<#list facilityList  as eachFac>
							<option value='${eachFac.facilityId}'>${eachFac.facilityName?if_exists}</option>
						</#list>
					</select>
				</td>
				<td align="center"><input type="submit" value="Submit" id="getCharts" class="smallSubmit" /></td>
			</tr>
    	</table> 
    					 		
    	
	</form>
	</div>
</div>
