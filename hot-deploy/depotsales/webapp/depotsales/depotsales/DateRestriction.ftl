<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
<link type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.css</@ofbizContentUrl>" rel="Stylesheet" />


<style type="text/css">
 .labelFontCSS {
    font-size: 13px;
}

</style>

<script type="text/javascript">

$(document).ready(function(){

$( "#paymentDateReceipt" ).datetimepicker({
			dateFormat:'yy-mm-dd',
			timeFormat:'hh:mm:ss',
			changeMonth: true,
			numberOfMonths: 1,
			maxDate: new Date(),
			onSelect: function( selectedDate ) {
				$( "#paymentDateReceipt" ).datetimepicker("option", selectedDate);
			}
		});
});

</script>