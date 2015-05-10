<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
<script type= "text/javascript">
	function appendParams(formName, action) {
    var glAccountId = $("#glAccountId").val();
    if( (glAccountId).length < 1 ) {
    	$('#glAccountId').css('background', 'yellow'); 
       	setTimeout(function () {
           	$('#glAccountId').css('background', 'white').focus(); 
       	}, 800);
    	return false;
	}  

	var formId = "#" + formName;
	jQuery(formId).attr("action", action);	
	jQuery(formId).submit();
    }
    
    function appendParameters(formName, action){
    	var formId = "#" + formName;
		jQuery(formId).attr("action", action);	
		jQuery(formId).submit();
    }
    var glAccountType = ${StringUtil.wrapString(glAccountTypeJSON)!'[]'};
    var glAccountName = ${StringUtil.wrapString(glAccountName)!'[]'};

    var AccountName;
	function dispglAccountName(selection){
	   value = $("#glAccountId").val();
	   AccountName = glAccountName[value];
	   $("#AccountName").html("<h6>"+AccountName+"</h6>");
	} 	
function makeDatePicker(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat:'yy, MM dd',
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
			dateFormat:'yy, MM dd',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				//$( "#"+fromDateId ).datepicker( "option", "maxDate", selectedDate );
			}
		});
	}
function makeDatePicker1(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat:'dd MM, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
			}
		});
	$( "#"+thruDateId ).datepicker({
			dateFormat:'dd MM, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				//$( "#"+fromDateId ).datepicker( "option", "maxDate", selectedDate );
			}
		});
	}
	function makeDatePicker2(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat: 'M-yy',
			changeMonth: true,
			changeYear: true,
			showButtonPanel: true,
			onClose: function( selectedDate ) {
			    var month = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
	            var year = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
	            $(this).datepicker('setDate', new Date(year, month, 1));
	            $( "#"+thruDateId).datepicker('setDate', new Date(year, month, 1));
			}
		});
	$( "#"+thruDateId ).datepicker({
			dateFormat: 'M-yy',
			changeMonth: true,
			changeYear: true,
			showButtonPanel: true,
			onClose: function( selectedDate ) {
			    var month = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
	            var year = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
	            $(this).datepicker('setDate', new Date(year, month, 1));
			}
		});
	$(".FDate").focus(function () {
	        $(".ui-datepicker-calendar").hide();
	        $("#ui-datepicker-div").position({
	            my: "center top",
	            at: "center bottom",
	            of: $(this)
	        });    
	     });
	     $(".TDate").focus(function () {
	        $(".ui-datepicker-calendar").hide();
	        $("#ui-datepicker-div").position({
	            my: "center top",
	            at: "center bottom",
	            of: $(this)
	        });    
	     });
	}
	//no restriction for thruDate
	function makeDatePicker3(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat:'yy, MM dd',
			changeMonth: true,
			changeYear: true,
			onSelect: function(selectedDate) {
				$("#"+thruDateId).datepicker( "option", {minDate: selectedDate}).datepicker('setDate', selectedDate);
			}
		});
	$( "#"+thruDateId ).datepicker({
			dateFormat:'yy, MM dd',
			onSelect: function( selectedDate ) {
				//$( "#"+fromDateId ).datepicker( "option", "maxDate", selectedDate );
			}
		});
	}
//one year restriction
	function makeDatePicker4(fromDateId ,thruDateId){
		$( "#"+fromDateId ).datepicker({
		dateFormat:'yy, MM dd',
		changeMonth: true,
		changeYear: true,
		onSelect: function(selectedDate) {
		date = $(this).datepicker('getDate');
		y = date.getFullYear(),
		m = date.getMonth();
		d = date.getDate();
		    var maxDate = new Date(y+1, m, d);
		
		$("#"+thruDateId).datepicker( "option", {minDate: selectedDate, maxDate: maxDate}).datepicker('setDate', date);
		//$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
		}
		});
		$( "#"+thruDateId ).datepicker({
		dateFormat:'yy, MM dd',
		changeMonth: true,
		changeYear: true,
		onSelect: function( selectedDate ) {
		//$( "#"+fromDateId ).datepicker( "option", "maxDate", selectedDate );
		}
		});
	}	
	
// for Vat Invoice Sequence and Invoice sale reports

function reportTypeChangeFunc() {
	var vatTypeValue = $('#vatType').val();
	if(vatTypeValue == "centralExcise"){
		$('#reportTypeFlag').parent().show();
		$('#categoryType').parent().show();
	}
	else{
	   	$('#reportTypeFlag').parent().hide();
	   	$('#categoryType').parent().hide();
	}
}


//call one method for one time fromDATE And thruDATE

	$(document).ready(function(){
	    makeDatePicker4("SubLedgersFromDate","SubLedgersThruDate");
	    makeDatePicker4("SaleAnalysisFromDate","SaleAnalysisThruDate");
	    $('#ui-datepicker-div').css('clip', 'auto');	
	    $("#glAccountId").autocomplete({ source: glAccountType }).keydown(function(e){});	
	});
//for Month Picker
	$(document).ready(function(){
    	$(".monthPicker").datepicker( {
	        changeMonth: true,
	        changeYear: true,
	        showButtonPanel: true,
	        dateFormat: 'yy-mm',
	        onClose: function(dateText, inst) { 
	            var month = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
	            var year = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
	            $(this).datepicker('setDate', new Date(year, month, 1));
	        }
		});
		$(".monthPicker").focus(function () {
	        $(".ui-datepicker-calendar").hide();
	        $("#ui-datepicker-div").position({
	            my: "center top",
	            at: "center bottom",
	            of: $(this)
	        });    
	     });
	});
	
	$(document).ready(function verifyFields(){
		$('input[name=submitButton]').click (function verifyFields(){
		var fromMonth = $('#fromMonth').val();
		var thruMonth = $('#thruMonth').val();
	 	if((fromMonth == "")|| (thruMonth == "")){
			alert("Please select dates");
			return false;
			}
		});
 	
	});
	
	$(document).ready(function(){
    	$(".monthPickerTDS").datepicker( {
	        changeMonth: true,
	        changeYear: true,
	        showButtonPanel: true,
	        dateFormat: 'mm-yy',
	        onClose: function(dateText, inst) { 
	            var month = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
	            var year = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
	            $(this).datepicker('setDate', new Date(year, month, 1));
	        }
		});
		$(".monthPickerTDS").focus(function () {
	        $(".ui-datepicker-calendar").hide();
	        $("#ui-datepicker-div").position({
	            my: "center top",
	            at: "center bottom",
	            of: $(this)
	        });    
	     });
	});


	$(document).ready(function verifyFields(){
		$('input[name=submitButton]').click (function verifyFields(){
		var fromMonth = $('#fromMonth').val();
		var thruMonth = $('#thruMonth').val();
	 	if((fromMonth == "")|| (thruMonth == "")){
			alert("Please select dates");
			return false;
			}
		});
 	
	});
</script>
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h2><center>SubLedger Reports</center></h2>
    </div>
    <div class="screenlet-body">
		<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
             <tr class="alternate-row">
				<form id="SubLedgers" name="SubLedgers" method="post" action="<@ofbizUrl>SubLedgersReport.pdf</@ofbizUrl>" target="_blank">	
					<td width="15%">Sub-Ledger Report</td>
					<td width="10%">From<input  type="text" size="18pt" id="SubLedgersFromDate" readonly  name="fromDate"/></td>
				    <td width="10%">To<input  type="text" size="18pt" id="SubLedgersThruDate" readonly  name="thruDate"/></td>
  					<td width="15%">Gl Account :<input type="text" name="glAccountId" id="glAccountId" size="11" maxlength="60"  required onblur='javascript:dispglAccountName(this);'/>
  					<span  class="tooltip" id="AccountName"></span>
                     </td>  
                      <td width="10%">Report Type 
					    <select name="reportTypeFlag" id="reportTypeFlag">
						   <option value='Ledger'>Ledger</option>
						   <option value='Abstract'>Abstract</option>
					   </select>
                     </td> 
					  <td width="20%">Party Code :<@htmlTemplate.lookupField size="10" maxlength="22" formName="SubLedgers" name="partyId" id="partyId" fieldFormName="LookupPartyName"/> </td>
					  <td width="5%" align="right"><input type="submit" value="PDF" onClick="javascript:appendParams('SubLedgers', '<@ofbizUrl>SubLedgersReport.pdf</@ofbizUrl>');" class="buttontext"/> </td>
					  <td width="5%" align="left"><input type="submit" value="CSV" onClick="javascript:appendParams('SubLedgers', '<@ofbizUrl>SubLedgersReport.csv</@ofbizUrl>');" class="buttontext"/></td>
				</form>
              </tr>
              <tr class="alternate-row">
				<form id="SalesAnalysis" name="SalesAnalysis" method="post" action="<@ofbizUrl>SalesAnalysisReport.pdf</@ofbizUrl>" target="_blank">	
					<td width="15%">Sales Analysis Report</td>
					<td width="15%">From<input  type="text" size="18pt" id="SaleAnalysisFromDate" readonly  name="fromDate"/></td>
				    <td width="15%">To<input  type="text" size="18pt" id="SaleAnalysisThruDate" readonly  name="thruDate"/></td>
  					<td width="15%">
                     </td>  
                      <td width="10%">
                     </td> 
					  <td width="20%"> </td>
					  <td width="5%" align="right"><input type="submit" value="PDF" onClick="javascript:appendParameters('SalesAnalysis', '<@ofbizUrl>SalesAnalysisReport.pdf</@ofbizUrl>');" class="buttontext"/> </td>
					  <td width="5%" align="left"><input type="submit" value="CSV" onClick="javascript:appendParameters('SalesAnalysis', '<@ofbizUrl>SalesAnalysisReport.csv</@ofbizUrl>');" class="buttontext"/></td>  
				</form>
              </tr>
		</table>     			     
	</div> 	
</div>
</div>