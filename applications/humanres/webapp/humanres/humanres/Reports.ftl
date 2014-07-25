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

//call one method for one time fromDATE And thruDATE

	$(document).ready(function(){
		
		makeDatePicker("fromDate","");
		makeDatePicker("thruDate","");
		makeDatePicker("mclfromDate","mclfromDate");
		makeDatePicker("mclthruDate","mclthruDate");
		
		
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
<div >
<div class="screenlet">
    		<div class="screenlet-title-bar">
      			<h2><center>Reports</center></h2>
    		</div>
    <div class="screenlet-body">
    	<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
      		<tr>
      	   		<form id="masterEmployees" name="HR-Master" method="post" action="<@ofbizUrl>EmployeesListCsv.csv</@ofbizUrl>" target="_blank">	
	      		  	<td width="20%">HR Master Information</td>
	      		  	<td width="25%">Organization Id 
				 	<select name="partyId" class='h4'>
						<#list orgList as org>    
  	    					<option value='${org.partyId}'>${org.groupName}</option>
						</#list> 
					</select>
				</td>	
	      			<td width="30%">From Date<input  type="text" size="18pt" id="fromDate"   name="FromDate"/>Thru Date<input  type="text" size="18pt" id="thruDate"   name="ThruDate"/></td>
					<td width="10%"><input type="submit" value="Download" class="buttontext"></td> 
      	   		</form>
      	   </tr>
      	   <tr class="alternate-row">
      	   	<form id="MonthlyAttendenceChecklist" name="MonthlyAttendenceChecklist" mothed="post" action="<@ofbizUrl>MonthlyAttendenceChecklist.pdf</@ofbizUrl>" target="_blank">
      	   	<td width="20%">Monthly Attendence Checklist</td>
      	   	<td width="15%">From Date<input  type="text"  id="mclfromDate"   name="mclFromDate"/></td>
  			<td width="15%">Thru Date<input  type="text"  id="mclthruDate"   name="mclThruDate"/></td>
			<td width="10%"><input type="submit" value="Download" class="buttontext"></td> 
					</td>
      	   	
      	   </form>
      	   </tr>
      	   <tr class="alternate-row">
      	   	<form id="EmployeePersonalProfile" name="EmployeePersonalProfile" mothed="post" action="<@ofbizUrl>EmployeeProfilePdf.pdf</@ofbizUrl>" target="_blank">
      	   	<td width="25%">Employee Personal Profile</td>
      	   	<td width="35%">Organization Id 
				 	<select name="partyIdFrom" class='h4'>
						<#list orgList as org>    
  	    					<option value='${org.partyId}'>${org.groupName}</option>
						</#list> 
					</select>
				</td>	
  			<td width="25%">Employee Id<input type="text" id="PartyId" name="employeeId"/></td>
  			<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('EmployeePersonalProfile', '<@ofbizUrl>EmployeeProfilePdf.pdf</@ofbizUrl>');" class="buttontext"/>
					<input type="submit" value="CSV" onClick="javascript:appendParams('EmployeePersonalProfile', '<@ofbizUrl>EmployeeProfileCsv.csv</@ofbizUrl>');" class="buttontext"/>
					</td>
      	   </form>
      	   </tr>
      	   <tr class="alternate-row">
      	   	<form id="paySlipEmployeewise" name="paySlipEmployeewise" mothed="post" action="<@ofbizUrl>PrintPaySlipsPdf.pdf</@ofbizUrl>" target="_blank">
      	   	<td width="25%">Pay Slip Employee Wise</td>
      	   	<td width="35%">Organization Id 
				 	<select name="partyIdFrom" class='h4'>
						<#list orgList as org>    
  	    					<option value='${org.partyId}'>${org.groupName}</option>
						</#list> 
					</select>
				</td>	
  			<td width="35%">Custom Time Period Id
  			<select name="customTimePeriodId" id="customTimePeriodId" class='h5' >
  			<#list customTimePeriodList as customTimePeriod><option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option></#list>
  			</select>
  			</td>
  			<td width="20%">Employee Id<input type="text" id="PartyId" name="employeeId"/></td>
  			<td width="15%"><input type="submit" value="PDF" onClick="javascript:appendParams('paySlipEmployeewise', '<@ofbizUrl>PrintPaySlipsPdf.pdf</@ofbizUrl>');" class="buttontext"/>
					</td>
      	   </form>
      	   </tr>
      	   <tr class="alternate-row">
      	   	<form id="EmployeeWiseSalaryDetails" name="EmployeeWiseSalaryDetails" mothed="post" action="<@ofbizUrl>EmployeeWiseSalaryDetails</@ofbizUrl>" target="_blank">
      	   	<td width="25%">Employee Wise Salary Details</td>
  			<td width="20%">Employee Id<input type="text" id="PartyId" name="employeeId"/></td>
  			<td width="40%">Custom Time Period Id
  			<select name="customTimePeriodId" id="customTimePeriodId" class='h5' >
  			<#list customTimePeriodList as customTimePeriod><option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option></#list>
  			</select>
  			</td>
  			<td width="15%"><input type="submit" value="PDF" onClick="javascript:appendParams('EmployeeWiseSalaryDetails', '<@ofbizUrl>EmployeeWiseSalaryDetailsPdf.pdf</@ofbizUrl>');" class="buttontext"/>
							<input type="submit" value="CSV" onClick="javascript:appendParams('EmployeeWiseSalaryDetails', '<@ofbizUrl>EmployeeWiseSalaryDetailsCsv.csv</@ofbizUrl>');" class="buttontext"/>
					</td>
      	   </form>
      	   </tr>
      	   <tr>
	  	   		<form id="MonthlyBankAdviceStatement" name="MonthlyBankAdviceStatement" mothed="post" action="<@ofbizUrl>EmployeeBankDetailsPdf.pdf</@ofbizUrl>" target="_blank">
	  	   			<td width="20%">Monthly Bank Advice Statement</td>
	  	   			<td width="35%">Organization Id 
				 	<select name="partyIdFrom" class='h4'>
						<#list orgList as org>    
  	    					<option value='${org.partyId}'>${org.groupName}</option>
						</#list> 
					</select>
					</td>
	  	   			<td width="40%">Period Id
			  			<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
			  				<#list customTimePeriodList as customTimePeriod><option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option></#list>
			  			</select>
			  		</td>
					<td width="10%"><input type="submit" value="Download" class="buttontext"></td> 
					</td>
	  	   	
	  	   		</form>
      	   </tr>
     <!-- 	<tr class="alternate-row">
      	   	<form id="AttendanceMonthlyReport" name="AttendanceMonthlyReport" mothed="post" action="<@ofbizUrl>EmplMonthlyPunchReport.csv</@ofbizUrl>" target="_blank">
      	   	<td width="25%">Monthly Attendance Report</td>
  			<td width="35%">Organization Id 
				 	<select name="partyId" class='h4'>
				 	
						<#list orgList as org>    
  	    					<option value='${org.partyId}'>${org.groupName}</option>
						</#list> 
					</select>
				</td>
  			<td width="40%">Custom Time Period Id
  			<select name="customTimePeriodId" id="customTimePeriodId" class='h5' >
  			<#list customTimePeriodList as customTimePeriod><option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option></#list>
  			</select>
  			</td>
  			<td width="15%"><input type="submit" value="DownloadCSV" onClick="javascript:appendParams('AttendanceMonthlyReport', '<@ofbizUrl>EmplMonthlyPunchReport.csv</@ofbizUrl>');" class="buttontext"/>
					</td>
      	   </form>
      	   </tr>-->
    	</table>
    </div>
</div>
</div>

 