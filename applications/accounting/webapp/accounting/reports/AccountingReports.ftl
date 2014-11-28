<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
<script type= "text/javascript">
	function appendParams(formName, action) {
	var formId = "#" + formName;
	jQuery(formId).attr("action", action);	
	jQuery(formId).submit();
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

	    makeDatePicker("FinacialFromDate","FinacialThruDate");
		
		$('#ui-datepicker-div').css('clip', 'auto');		
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
</script>
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h2><center>Accounting Reports</center></h2>
    </div>
    <div class="screenlet-body">
    	
    			<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
    			  <tr class="alternate-row">
						<form id="BankReconciliationReports" name="BankReconciliationReports" method="post" action="<@ofbizUrl>recStatemetn.pdf</@ofbizUrl>" target="_blank">	
							<td width="30%"> Bank  Reconciliation  Report</td>
							<td width="15%">From<input  type="text" size="18pt" id="FinacialFromDate" readonly  name="fromDate"/></td>
						    <td width="15%">To<input  type="text" size="18pt" id="FinacialThruDate" readonly  name="thruDate"/></td>
						    <td width="15%">Bank<select name='finAccountId' id ="finAccountId">	
									<option value=""></option>								
								<#list finAccounts as finAcunt> 	
									<option value='${finAcunt.finAccountId}'>${finAcunt.finAccountName?if_exists}</option>
	                  		   </#list>
									</select>
								</td>
	      					<td width="15%"></td>
							<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('BankReconciliationReports', '<@ofbizUrl>recStatemetn.pdf</@ofbizUrl>');" class="buttontext"/>
							<input type="submit" value="CSV" onClick="javascript:appendParams('BankReconciliationReports', '<@ofbizUrl>FinAccountTransForReconsile.csv</@ofbizUrl>');" class="buttontext"/></td>         			
						</form>
	                  </tr> 
      			</table>     			     
    		</div> 	
</div>
</div>
