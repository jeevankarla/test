<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
<script type= "text/javascript">
	
function makeDatePicker(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function(selectedDate) {
			date = $(this).datepicker('getDate');
			var maxDate = new Date(date.getTime());
	        	maxDate.setDate(maxDate.getDate() + 31);
				$("#"+thruDateId).datepicker( "option", {minDate: selectedDate, maxDate: maxDate}).datepicker('setDate', date);
				//$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
			}
		});
	$( "#"+thruDateId ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				//$( "#"+fromDateId ).datepicker( "option", "maxDate", selectedDate );
			}
		});
	}

$(document).ready(function(){

	    makeDatePicker("regularIceCreamfDate","regularIceCreamtDate");
	    makeDatePicker("regularIceCreamfDate","regularIceCreamtDate");
	    makeDatePicker("amulIceCreamfDate","amulIceCreamtDate");
	    makeDatePicker("amulIceCreamfDate","amulIceCreamtDate");
		$('#ui-datepicker-div').css('clip', 'auto');		
	});

</script>

<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Sales Reports</h3>
    </div>
    <div class="screenlet-body">
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;" >  
      	<tr class="alternate-row">
						<form id="regularIceCreamSaleReport" name="regularIceCreamSaleReport" method="post" action="<@ofbizUrl>RegularIceCreamSaleBookReport.pdf</@ofbizUrl>" target="_blank">	
							<td width="30%">Sale Book Report Detail</td>
							<td width="15%">From<input  type="text" size="18pt" id="regularIceCreamfDate" readonly  name="fromDate"/></td>
						    <td width="15%">To<input  type="text" size="18pt" id="regularIceCreamtDate" readonly  name="thruDate"/></td>
			      			<td width="15%">By<select name="categoryType">
			      				<#--<option value="All">All</option>-->
				      			<option value="ICE_CREAM_NANDINI">Nandini Ice Cream</option>
				      			<option value="ICE_CREAM_AMUL">Amul Ice Cream</option>
				      			<option value="UNITS">Inter Unit Transfer</option>
				      			<option value="UNION">Sale to Union</option>
				      			<option value="DEPOT_CUSTOMER">Depot Sale</option>
			      			</select></td>
			      			<td width="15%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="iceCreamSaleReport" name="partyId" id="partyId" fieldFormName="LookupPartyName"/></td>
							<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('regularIceCreamSaleReport', '<@ofbizUrl>RegularIceCreamSaleBookReport.pdf</@ofbizUrl>');" class="buttontext"/>
							<input type="submit" value="CSV" onClick="javascript:appendParams('regularIceCreamSaleReport', '<@ofbizUrl>RegularIceCreamSaleBookReport.csv</@ofbizUrl>');" class="buttontext"/></td>         			
							
						</form>
	                  </tr>
	                   <tr class="alternate-row">
						<form id="iceCreamSaleReport" name="iceCreamSaleReport" method="post" action="<@ofbizUrl>iceCreamSaleBookReport.pdf</@ofbizUrl>" target="_blank">	
							<td width="30%">Sale Book Report Abstract </td>
							<td width="15%">From<input  type="text" size="18pt" id="amulIceCreamfDate" readonly  name="fromDate"/></td>
						    <td width="15%">To<input  type="text" size="18pt" id="amulIceCreamtDate" readonly  name="thruDate"/></td>
			      			<td width="15%">By<select name="categoryType">
			      				<option value="All">All</option>
				      			<option value="ICE_CREAM_NANDINI">Nandini Ice Cream</option>
				      			<option value="ICE_CREAM_AMUL">Amul Ice Cream</option>
				      			<option value="UNITS">Inter Unit Transfer</option>
				      			<option value="UNION">Sale to Union</option>
				      			<option value="DEPOT_CUSTOMER">Depot Sale</option>
			      			</select></td>
	      					<td width="15%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="iceCreamSaleReport" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>	<td width="10%"><input type="submit" value="Download" class="buttontext"/></td> 
						</form>
	                  </tr>
	</table>
</div>
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Store Report</h3>
    </div>
    <div class="screenlet-body">
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;" >  
       	
	</table>
   </div>
</div>
