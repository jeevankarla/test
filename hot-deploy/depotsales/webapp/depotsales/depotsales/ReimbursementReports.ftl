<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
<script type= "text/javascript">
	
	
var stateJSON=${StringUtil.wrapString(stateJSON)!'[]'};
	
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

	function getbranchesByState(state ,branchId){
       	var stateId=state.value;
       	var optionList = '';
			var list= stateJSON[stateId];
			if (list && list.length>0) {	
				optionList += "<option value = " + " " + " >" +"All "+ "</option>";	       				        	
	        	for(var i=0 ; i<list.length ; i++){
					var innerList=list[i];	     
	                optionList += "<option value = " + innerList['value'] + " >" +innerList['label']+" </option>";          			
	      		}//end of main list for loop
	      	}
	      	jQuery("[name='"+branchId+"']").html(optionList);
       }	
       
	function appendParams(formName, action) {
		var formId = "#" + formName;
		jQuery(formId).attr("action", action);	
		
		
		
		jQuery(formId).submit();
		
		
	   }
$(document).ready(function(){

	    makeDatePicker("regularIceCreamfDate","regularIceCreamtDate");
	    makeDatePicker("regularIceCreamfDate","regularIceCreamtDate");
	    makeDatePicker("amulIceCreamfDate","amulIceCreamtDate");
	    makeDatePicker("amulIceCreamfDate","amulIceCreamtDate");
	    makeDatePicker("stockFromDate","stockThruDate");
	    makeDatePicker("ivdFromDate","ivdThruDate");
	    makeDatePicker3("subsidyFromDate","subsidyThruDate");
	    makeDatePicker3("claimFromDate","claimThruDate");
	    makeDatePicker3("PFHFromDateCrDr","PFHThruDateCrDr");
	    makeDatePicker3("reimburcentTransporterFRO","reimburcentTransporterTHRU");
	    makeDatePicker3("depotReimburcentReportFRO","depotReimburcentReportTHRU");
	    makeDatePicker3("depotReimburcentSummaryReportFRO","depotReimburcentSummaryReportTHRU");
	    makeDatePicker3("StateWiseSchemeWiseSalesConsolidatedFro","StateWiseSchemeWiseSalesConsolidatedThru");
	    makeDatePicker3("abstrctFromDate","abstrctThruDate");
	    makeDatePicker3("salesPurchaseReportFRO","salesPurchaseReportTHRU");
	    makeDatePicker("stockDate");
	    makeDatePicker("CASHFromDateId","");
		$('#ui-datepicker-div').css('clip', 'auto');	
		
		
		
		
	});
 
function makeDatePicker3(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat:'yy, MM dd',
			changeMonth: true,
			changeYear: true,
			yearRange: "-20:+0", 
			onSelect: function(selectedDate) {
			$("#"+thruDateId).datepicker( "option", {minDate: selectedDate, maxDate: null}).datepicker('setDate', date);
			}
		});
	$( "#"+thruDateId ).datepicker({
			dateFormat:'yy, MM dd',
			onSelect: function( selectedDate ) {
				//$( "#"+fromDateId ).datepicker( "option", "maxDate", selectedDate );
			}
		});
	}
</script>

<div class="screenlet">
   
    <div class="screenlet-body">
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;" >  
       
        
	</table>
</div>
