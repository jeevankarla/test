<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />
<script type="text/javascript">
	function appendParams(formName, action) {
	var formId = "#" + formName;
	jQuery(formId).attr("action", action);	
	jQuery(formId).submit();
    }
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
	//call one method for one time fromDATE And thruDATE

	$(document).ready(function(){

	    
		makeDatePicker("fromDate","thruDate");
		
		
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
<div>
  <div class="screenlet">
	<div class="screenlet-title-bar">
		<h3>New Reports</h3>
	</div>
	<div class="screenlet-body">
		<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
			<tr class="alternate-row"> 
				<form id="StoreIssueReport" name="StoreIssueReport" mothed="post" action="<@ofbizUrl>StoreIssueReport.pdf</@ofbizUrl>" target="_blank">
					<table class="basic-table" cellspacing="5">
						<tr class="alternate-row">
							<td width="10%"><span class='h3'>Store Issue Report</span></td>
							<td width="8%">
							  <span class='h3'> From <input  type="text" size="18pt" id="fromDate"   name="fromDate"/>
							   </span>
						    </td>
						    <td width="8%">
							  <span class='h3'>To  <input  type="text" size="18pt" id="thruDate"   name="thruDate"/>
							   </span>
						    </td>
						    <td width="15%"><span class='h3'>Material Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="StoreIssueReport" name="productId" id="productId" fieldFormName="LookupProduct"/>
							</span></td>
							<td width="8%"><span class='h3'>Store
								<select name='store' id = "store">
								 <option value=''>All</option>
								 <option value='MAIN STORE'>MAIN STORE</option>										
							    </select>
						   </span></td>
						    <td width="6%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
						</tr>
					</table>
				</form>
			</tr>
			<tr class="alternate-row"> 
				<form id="StoreIssueAbstractReport" name="StoreIssueAbstractReport" mothed="post" action="<@ofbizUrl>StoreIssueAbstractReport.pdf</@ofbizUrl>" target="_blank">
					<table class="basic-table" cellspacing="5">
						<tr class="alternate-row">
							<td width="20%"><span class='h3'>Store Issue Abstract Report</span></td>
							<td width="50%">
							     <span class='h3'>
									From <input  type="text" size="18pt" id="fromDate"   name="fromDate"/>
									To   <input  type="text" size="18pt" id="thruDate"   name="thruDate"/>
								 </span>
							</td>
						    <td width="25%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
						</tr>
					 </table>
				</form>
			</tr>
			<tr class="alternate-row"> 
				<form id="StoreReport" name="StoreReport" mothed="post" action="<@ofbizUrl>StoreReport.pdf</@ofbizUrl>" target="_blank">
				   <table class="basic-table" cellspacing="5">
					  <tr class="alternate-row">
					     <td width="20%"><span class='h3'>Store Report</span></td>
						 <td width="50%">
						 <span class='h3'>
						    From <input  type="text" size="18pt" id="fromDate"   name="fromDate"/>
							To   <input  type="text" size="18pt" id="thruDate"   name="thruDate"/>
						 </span>
						 </td>
						 <td width="25%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
		 		    </tr>
				 </table>
			 </form>
		  </tr>
	  </table>
    </div>
  </div>
</div> 		