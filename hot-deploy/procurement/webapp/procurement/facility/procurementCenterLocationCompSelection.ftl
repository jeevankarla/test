<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	

<script type="text/javascript">

$(document).ready(function(){
		$( "#procurementDate" ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			changeYear: true,
			numberOfMonths: 1});
			$( "#procurementThruDate" ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			changeYear: true,
			numberOfMonths: 1});
		$('#ui-datepicker-div').css('clip', 'auto');		
	});
</script>

<div class="screenlet">
	<div class="screenlet-title-bar">
      	<h3>Select Date and Plant/Unit</h3>	
     </div>
    <div class="screenlet-body">
		<form name="ProcurementCenterLocationComp" action="<@ofbizUrl>ProcurementCenterLocationComp</@ofbizUrl>">
			<table class="basic-table" cellspacing="0">
        	<tr>
        		<td align="left" width="10%"><span class='h3'>From Date: </span></td>
        		<td align="left" width="15%"><input class='h3' type="text" id="procurementDate" name="procurementDate" required /><em>*</em></td>
        		<td align="left" width="10%"><span class='h3'>Thru Date: </span></td>
            	<td align="left" width="15%"><input class='h3' type="text" id="procurementThruDate" name="procurementThruDate" required /><em>*</em></td>
				<td align="left" width="5%"><span class='h3'>Facility:</span></td>
				<td align="left" width="15%">
				<@htmlTemplate.lookupField value="${facilityId?if_exists}" formName="ProcurementCenterLocationComp" name="facilityId" size="25pt" id="facilityId" fieldFormName="ProcurementUnitLookupFacility"/>
				</td>
				<td><input type="submit" value="Submit" id="button1" class="smallSubmit"/></td>
			</tr>
    	</table>
		</form>
	</div>
</div>
