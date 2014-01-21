<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	

<script type="text/javascript">

$(document).ready(function(){
		$( "#fromDate" ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			numberOfMonths: 2,
			onSelect: function( selectedDate ) {
				$( "#thruDate" ).datepicker( "option", "minDate", selectedDate );
			}
		});
		$( "#thruDate" ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			numberOfMonths: 2,
			onSelect: function( selectedDate ) {
				$( "#fromDate" ).datepicker( "option", "maxDate", selectedDate );
			}
		});
		$('#ui-datepicker-div').css('clip', 'auto');		
	});
</script>

<div class="screenlet">
	<div class="screenlet-title-bar">
      	<h3>Select Period</h3>	
     </div>
    <div class="screenlet-body">
	  <form name="byproductsAnalysis" action="<@ofbizUrl>${reportTarget}</@ofbizUrl>">
		<table class="basic-table" cellspacing="0">
        	<tr>
        		<td align="right" width="10%"><span class='h3'>From: </span></td>
            	<td width="20%"><input class='h2' type="text" id="fromDate" name="fromDate"/></td>
				<td width="2%"><span class='h3'>To: </span></td>
				<td width="20%"><input class='h2' type="text" id="thruDate" name="thruDate"/></td>
			</tr>		
			<tr>
				<td width="10%"><span class='h3'>By Product Code: </span></td>
				<td align="left" width="10%"><@htmlTemplate.lookupField value="${productId?if_exists}" formName="byproductsAnalysis" name="productId" id="productId" fieldFormName="LookupProduct"/></td>
				<td><input type="submit" value="Submit" id="button1" class="smallSubmit"/></td>
				
			</tr>
    	</table> 
	</form>
	</div>
</div>