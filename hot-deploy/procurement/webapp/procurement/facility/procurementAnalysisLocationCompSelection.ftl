<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	

<script type="text/javascript">

$(document).ready(function(){
		$( "#procurementDate" ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			numberOfMonths: 1});
		$('#ui-datepicker-div').css('clip', 'auto');		
	});
</script>

<div class="screenlet">
	<div class="screenlet-title-bar">
      	<h3>Select Date and Plant/Unit</h3>	
     </div>
    <div class="screenlet-body">
		<form name="procurementAnalysisLocationComp" action="<@ofbizUrl>ProcurementAnalysisLocationComp</@ofbizUrl>">
			<table class="basic-table" cellspacing="0">
        	<tr>
        		<td align="left" width="10%"><span class='h3'>Procurement Date: </span></td>
            	<td align="left" width="15%"><input class='h3' type="text" id="procurementDate" name="procurementDate"/></td>
				<td align="left" width="5%"><span class='h3'>Facility:</span></td>
				<td align="left" width="15%"><@htmlTemplate.lookupField value="${facilityId?if_exists}" formName="procurementAnalysisLocationComp" name="facilityId" id="facilityId" fieldFormName="ProcurementLookupFacility"/></td>
				<td><input type="submit" value="Submit" id="button1" class="smallSubmit"/></td>
			</tr>
    	</table>
		</form>
	</div>
</div>
