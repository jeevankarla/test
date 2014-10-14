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
	        	//maxDate.setDate(maxDate.getDate() + 31);
				$("#"+thruDateId).datepicker( "option", {setDate: '0',minDate: '-1y', maxDate: '+1y'}).datepicker('setDate', date);
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
		makeDatePicker("larfromDate","larfromDate");
		makeDatePicker("larthruDate","larthruDate");
		makeDatePicker("ITEarningsfromDate","ITEarningsfromDate");
		makeDatePicker("ITEarningsthruDate","ITEarningsthruDate");
		makeDatePicker("ITDeductionsfromDate","ITDeductionsfromDate");
		makeDatePicker("ITDeductionsthruDate","ITDeductionsthruDate");
		makeDatePicker("MPfromDate","MPfromDate");
		makeDatePicker("MPthruDate","MPthruDate");
		makeDatePicker("LOPfromDate","LOPfromDate");
		makeDatePicker("LOPthruDate","LOPthruDate");
		makeDatePicker("EditedLateHoursfromDate","EditedLateHoursfromDate");
		makeDatePicker("EPfromDate","EPfromDate");
		makeDatePicker("EPthruDate","EPthruDate");
		makeDatePicker("BusfromDate","BusfromDate");
		makeDatePicker("BusthruDate","BusthruDate");
		makeDatePicker("OODfromDate","OODfromDate");
		makeDatePicker("OODthruDate","OODthruDate");
		makeDatePicker("ITAXfromDate","ITAXfromDate");
		makeDatePicker("ITAXthruDate","ITAXthruDate");
		makeDatePicker("ESIForm6fromDate","ESIForm6fromDate");
		makeDatePicker("ESIForm6thruDate","ESIForm6thruDate");
		
		$('#ui-datepicker-div').css('clip', 'auto');		
	});
//for Month Picker
	$(document).ready(function(){
    	$(".monthPicker").datepicker( {
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
<#if reportFrequencyFlag =="MastersReports">
	<div>
		<div class="screenlet">
			<div class="screenlet-title-bar">
				<h3>Reports</h3>
			</div>
			<div class="screenlet-body">
				<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
					<tr class="alternate-row">
						<form id="masterEmployees" name="HR-Master" method="post" action="<@ofbizUrl>EmployeesListCsv.csv</@ofbizUrl>" target="_blank">	
							<td width="20%">HR Master Information</td>
							<td width="25%">Organization Id 
								<select name="partyId" class='h4'>
									<#list orgList as org>    
									<option value='${org.partyId?if_exists}'>${org.groupName?if_exists}</option>
									</#list> 
								</select>
							</td>	
							<td width="30%">From Date<input  type="text" size="18pt" id="fromDate"   name="FromDate"/>Thru Date<input  type="text" size="18pt" id="thruDate"   name="ThruDate"/></td>
							<td width="10%"><input type="submit" value="Download" class="buttontext"></td> 
						</form>
					</tr>
					<tr class="alternate-row">
						<form id="EmployeePersonalProfile" name="EmployeePersonalProfile" mothed="post" action="<@ofbizUrl>EmployeeProfilePdf.pdf</@ofbizUrl>" target="_blank">
							<td width="25%">Employee Personal Profile</td>
							<td width="35%">Organization Id 
								<select name="partyIdFrom" class='h4'>
									<#list orgList as org>    
									<option value='${org.partyId}'>${org.groupName?if_exists}</option>
									</#list> 
								</select>
							</td>	
							<td width="25%">Employee Id<input type="text" id="PartyId" name="employeeId"/></td>
							<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('EmployeePersonalProfile', '<@ofbizUrl>EmployeeProfilePdf.pdf</@ofbizUrl>');" class="buttontext"/>
							<input type="submit" value="CSV" onClick="javascript:appendParams('EmployeePersonalProfile', '<@ofbizUrl>EmployeeProfileCsv.csv</@ofbizUrl>');" class="buttontext"/></td>
					   </form>
					</tr>
					<tr class="alternate-row">
						<form id="LIC/RDReport" name="LIC/RDReport" mothed="post" action="<@ofbizUrl>LICRDReport.pdf</@ofbizUrl>" target="_blank">
							<td width="20%">LIC/RD Report</td>
							<td width="25%">Insurance Type 
								<select name="insuranceTypeId" sclass='h4'>
									<#list finalInsuranceTypeList as org>    
										<option value='${org.insuranceTypeId}'>${org.description?if_exists}</option>
									</#list> 
								</select>
							</td>
							<td width="40%">Period Id
								<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
									<#list customTimePeriodList as customTimePeriod><option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option></#list>
								</select>
							</td>	
							<td width="10%"><input type="submit" value="Download" class="buttontext"></td>
						</form>
					</tr>
				</table>
			</div>
		</div>
	</div>
</#if>
<#if reportFrequencyFlag =="LeaveReports">
	<div>
		<div class="screenlet">
			<div class="screenlet-title-bar">
				<h3>Reports</h3>
			</div>
			<div class="screenlet-body">
				<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
					<tr class="alternate-row">
						<form id="LeaveAvailedReport" name="LeaveAvailedReport" mothed="post" action="<@ofbizUrl>LeaveAvailedReport.pdf</@ofbizUrl>" target="_blank">
							<td width="15%">Leave Availed Report</td>
							<td width="15%">Organization Id 
								<select name="partyId" class='h5'>
									<#list orgList as org>    
									<option value='${org.partyId}'>${org.groupName?if_exists}</option>
									</#list> 
								</select>
							</td>	
							<td width="20%">Leave Type
								<select name="leaveTypeId" id="leaveTypeId" class='h5' >
									<option value="ALL">ALL</option>
										<#list leaveTypeList as leave>
									<option value='${leave.leaveTypeId}'>${leave.description?if_exists}</option></#list>
								</select>
							</td>
							<td width="15%">From Date<input  type="text"  id="larfromDate"   name="larFromDate"/></td>
							<td width="15%">Thru Date<input  type="text"  id="larthruDate"   name="larThruDate"/></td>
							<td width="10%"></td>
							<td width="10%"><input type="submit" value="Download" class="buttontext"></td> 
							</td>
						</form>
					</tr>
					<tr class="alternate-row">
						<form id="CashEncashmentReport" name="CashEncashmentReport" mothed="post" action="<@ofbizUrl>CashEncashmentReport.pdf</@ofbizUrl>" target="_blank">
							<td width="30%">Cash Encashment Report</td>
							<td width="15%">Organization Id 
								<select name="deptId" class='h5'>
									<#list orgList as org>    
									<option value='${org.partyId}'>${org.groupName?if_exists}</option>
									</#list> 
								</select>
							</td>	
							<td width="15%">Period Id
								<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
									<#list customTimePeriodList as customTimePeriod><option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option></#list>
								</select>
							</td>	
							<td width="10%"></td>
							<td width="10%"></td>
							<td width="10%"></td>
							<td width="10%"><input type="submit" value="Download" class="buttontext"></td>
						
						</form>											
					</tr>
				</table>
			</div>
		</div>
	</div>
</#if>
<#if reportFrequencyFlag =="AttendanceReports">
	<div>
		<div class="screenlet">
			<div class="screenlet-title-bar">
				<h3>Reports</h3>
			</div>
			<div class="screenlet-body">
				<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
					<tr class="alternate-row">
						<table class="basic-table" cellspacing="2">
							<form id="MonthlyAttendenceChecklist" name="MonthlyAttendenceChecklist" mothed="post" action="<@ofbizUrl>MonthlyAttendenceChecklist.pdf</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="18%"><span class='h3'>Attendence And Leave Checklist</span></td>
										<td width="13%"><span class="h3">Employee Id</span><input type="text" size="10pt" id="prtyId" name="partyIdTo"></td>
										<td width="15%"><span class='h3'>From Date</span><input  type="text" size="18pt" id="mclfromDate"   name="mclFromDate"/></td>
										<td width="15%"><span class='h3'>Thru Date</span><input  type="text" size="18pt" id="mclthruDate"   name="mclThruDate"/></td>
										<td width="20%"><span class='h3'></span><input type="submit" value="Download" class="buttontext"></td> 
									</tr>
								</table>	
							</form>
						</table>
					</tr>
					<tr class="alternate-row">
						<form id="EmployeeMisPunchData" name="EmployeeMisPunchData" mothed="post" action="<@ofbizUrl>EmployeeMisPunchData.pdf</@ofbizUrl>" target="_blank">
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="20%"><span class='h3'>Employee Mis Punch Data</span></td>
									<td width="20%"><span class='h3'>From Date</span><input  type="text" size="18pt" id="MPfromDate"   name="MPfromDate"/></td>
									<td width="20%"><span class='h3'>Thru Date</span><input  type="text" size="18pt" id="MPthruDate"   name="MPthruDate"/></td>
									<td width="20%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
								</tr>
							</table>
						</form>
					 </tr>
					 <tr class="alternate-row">
					 	<form id="EmployeePunchData" name="EmployeePunchData" mothed="post" action="<@ofbizUrl>EmployeePunchData.pdf</@ofbizUrl>" target="_blank">
	      	   				<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
						      	   	<td width="20%"><span class='h3'>Employee Punch Data</td></span></td>
						      	   	<td width="20%"><span class='h3'>From Date</span><input  type="text"  id="EPfromDate" size="18pt" name="EPfromDate"/></td>
						  			<td width="20%"><span class='h3'>Thru Date</span><input  type="text"  id="EPthruDate" size="18pt" name="EPthruDate"/></td>
									<td width="20%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
								</tr>
							</table>	
      	   				</form>
      	  			</tr>
					<tr class="alternate-row">
						<table class="basic-table" cellspacing="3">
							<form id="EditedLateHoursReport" name="EditedLateHoursReport" mothed="post" action="<@ofbizUrl>EditedLateHoursReport.pdf</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="12%"><span class='h3'>Edited Late Hours Report</span></td>
										<td width="24%"><span class='h3'>Period Id</span>
											<select name="customTimePeriodId" class='h4'>
												<#list timePeriodList as timePeriod>    
													<option value='${timePeriod.customTimePeriodId}'>${timePeriod.periodName?if_exists}:${timePeriod.fromDate?if_exists}-${timePeriod.thruDate?if_exists}</option>
												</#list>      
											</select>
										</td>	
										<td width="12%"><input type="submit" value="Download" class="buttontext"></td> 
									</tr>
								</table>	
							</form>
						</table>
					</tr>
					<tr class="alternate-row">
						<form id="EmployeesLOPdays" name="EmployeeMisPunchData" mothed="post" action="<@ofbizUrl>EmployeesLOPdays.pdf</@ofbizUrl>" >
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="12%"><span class='h3'>LOP days Report</span></td>
									<td width="24%"><span class='h3'>Period Id</span>
									<select name="customTimePeriodId" class='h4'>
										<#list timePeriodList as timePeriod>    
											<option value='${timePeriod.customTimePeriodId}'>${timePeriod.fromDate?if_exists}-${timePeriod.thruDate?if_exists}</option>
										</#list>      
									</select>
									</td>	
									<td width="12%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
								</tr>
							</table>
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
					
					<#-- <tr class="alternate-row">
						<form id="EmployeeWiseAttendanceDetails" name="EmployeeWiseAttendanceDetails" mothed="post" action="<@ofbizUrl>EmplMonthAttendanceDetails</@ofbizUrl>" target="_blank">
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="19%"><span class='h3'>Employee Attendance Details</span></td>
									<td width="15%"><span class='h3'>Employee Id</span><input type="text" size="10pt" id="PartyId" name="partyIdTo"/></td>
									<td width="27%"><span class='h3'>Period Id</span>
										<select name="customTimePeriodId" class='h4'>
											<#list timePeriodList as timePeriod>    
												<option value='${timePeriod.customTimePeriodId}'>${timePeriod.fromDate?if_exists}-${timePeriod.thruDate?if_exists}</option>
											</#list>      
										</select>
									</td>
									<td width="20%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
								</tr>
							</table>
						</form>
					</tr> -->
					<tr class="alternate-row">
						<form id="AttendanceExceptionReport" name="AttendanceExceptionReport" mothed="post" action="<@ofbizUrl>AttendanceExceptionReport.pdf</@ofbizUrl>" target="_blank">
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="12%"><span class='h3'>Attendance Exception Report</span></td>
									<td width="24%"><span class='h3'>Period Id</span>
									<select name="customTimePeriodId" class='h4'>
										<#list timePeriodList as timePeriod>    
											<option value='${timePeriod.customTimePeriodId}'>${timePeriod.fromDate?if_exists}-${timePeriod.thruDate?if_exists}</option>
										</#list>      
									</select>
									</td>	
									<td width="12%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
								</tr>
							</table>
						</form>
					</tr>
					<tr class="alternate-row">
					 	<form id="BusArrivalReport" name="BusArrivalReport" mothed="post" action="<@ofbizUrl>BusArrivalReport.pdf</@ofbizUrl>" target="_blank">
	      	   				<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
						      	   	<td width="20%"><span class='h3'>Bus Arrival Report</td></span></td>
						      	   	<td width="20%"><span class='h3'>From Date</span><input  type="text"  id="BusfromDate" size="18pt" name="BusfromDate"/></td>
						  			<td width="20%"><span class='h3'>Thru Date</span><input  type="text"  id="BusthruDate" size="18pt" name="BusthruDate"/></td>
									<td width="20%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
								</tr>
							</table>	
      	   				</form>
      	  			</tr>
      	  			<tr class="alternate-row">
					 	<form id="OODReport" name="OODReport" mothed="post" action="<@ofbizUrl>OODReport.pdf</@ofbizUrl>" target="_blank">
	      	   				<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
						      	   	<td width="20%"><span class='h3'>OOD Report</td></span></td>
						      	   	<td width="20%"><span class='h3'>From Date</span><input  type="text"  id="OODfromDate" size="18pt" name="OODfromDate"/></td>
						  			<td width="20%"><span class='h3'>Thru Date</span><input  type="text"  id="OODthruDate" size="18pt" name="OODthruDate"/></td>
									<td width="15%"><span class='h3'>Employee Id<input type="text" id="PartyId" size="10pt" name="employeeId"/></span></td>
									<td width="20%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
								</tr>
							</table>	
      	   				</form>
      	  			</tr>
      	  			<tr class="alternate-row">
						<form id="PayableDaysReport" name="PayableDaysReport" mothed="post" action="<@ofbizUrl>PayableDaysReport.pdf</@ofbizUrl>" >
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="12%"><span class='h3'>Payable Days Report</span></td>
									<td width="24%"><span class='h3'>Period Id</span>
									<select name="customTimePeriodId" class='h4'>
										<#list timePeriodList as timePeriod>    
											<option value='${timePeriod.customTimePeriodId}'>${timePeriod.fromDate?if_exists}-${timePeriod.thruDate?if_exists}</option>
										</#list>      
									</select>
									</td>	
									<td width="12%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
								</tr>
							</table>
						</form>
				 	</tr>
				</table>
			</div>
		</div>
	</div>
</#if>
<#if reportFrequencyFlag =="PayrollReports">
	<div>
		<div class="screenlet">
			<div class="screenlet-title-bar">
				<h3>Reports</h3>
			</div>
			<div class="screenlet-body">
				<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
					<tr class="alternate-row">
						<form id="PayrollExceptionReport" name="PayrollExceptionReport" mothed="post" action="<@ofbizUrl>PayrollExceptionReport.pdf</@ofbizUrl>" target="_blank">
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="11%"><span class='h3'>Payroll Exception Report</span></td>
										<td width="25%"><span class='h3'>Organization Id </span>
											<select name="partyId" class='h4'>
											    <option></option>
												<#list orgList as org>    
													<option value='${org.partyId}'>${org.groupName?if_exists}</option>
												</#list> 
											</select>
										</td>
										<td width="40%"><span class='h3'>Period Id</span>
										<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
											<#list customTimePeriodList as customTimePeriod><option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option></#list>
										</select>
									</td>	
									<td width="7%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
								</tr>
							</table>	
						</form>
				   </tr>
					<tr class="alternate-row">
						<form id="MonthlyBankAdviceStatement" name="MonthlyBankAdviceStatement" mothed="post" action="<@ofbizUrl>EmployeeBankDetailsPdf.pdf</@ofbizUrl>" target="_blank">
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="17%"><span class='h3'>Monthly Bank Advice Statement</span></td>
									<td width="25%"><span class='h3'>Organization Id </span>
										<select name="bankAdvise_deptId" class='h4'>
											<#list orgList as org>    
												<option value='${org.partyId}'>${org.groupName?if_exists}</option>
											</#list> 
										</select>
									</td>
									<td width="25%"><span class='h3'>Bank</span>
										<select name="finAccountId" class='h4'>
											<option value='All'>All</option>
											<#list companyAccList as bank>    
												<option value='${bank.finAccountId?if_exists}'>${bank.finAccountName?if_exists}</option>
											</#list> 
										</select>
									</td>
									<td width="35%"><span class='h3'>Period Id
										<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
											<#list customTimePeriodList?sort_by("fromDate") as customTimePeriod><option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option></#list>
										</select></span>
									</td>
									<td width="10%"><span class='h4'><input type="submit" value="PDF" onClick="javascript:appendParams('MonthlyBankAdviceStatement', '<@ofbizUrl>EmployeeBankDetailsPdf.pdf</@ofbizUrl>');" class="buttontext"/>
									 <input type="submit" value="CSV" onClick="javascript:appendParams('MonthlyBankAdviceStatement', '<@ofbizUrl>EmployeeBankDetailsCsv.csv</@ofbizUrl>');" class="buttontext"/></span></td> 
									</td>
								</tr>
							</table>
						</form>
					</tr>
				   <tr class="alternate-row">
						<form id="paySlipEmployeewise" name="paySlipEmployeewise" mothed="post" action="<@ofbizUrl>PrintPaySlipsPdf.pdf</@ofbizUrl>" target="_blank">
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="20%"><span class='h3'>Pay Slip Employee Wise</span></td>
									<td width="30%"><span class='h3'>Organization Id </span>
										<select name="partyIdFrom" class='h4'>
											<option value=''></option>
											<#list orgList as org>    
												<option value='${org.partyId}'>${org.groupName}</option>
											</#list> 
										</select>
									</td>	
									<td width="35%"><span class='h3'>Period Id</span>
										<select name="customTimePeriodId" id="customTimePeriodId" class='h5'>
											<#assign customTimePeriodList=customTimePeriodList?sort>
											<#list customTimePeriodList?sort_by("fromDate") as customTimePeriod><option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option></#list>
										</select>
									</td>
									<td width="15%"><span class='h3'>Employee Id<input type="text" id="PartyId" size="10pt" name="employeeId"/></span></td>
									<td width="15%"><span class='h3'><input type="submit" value="PDF" onClick="javascript:appendParams('paySlipEmployeewise', '<@ofbizUrl>PrintPaySlipsPdf.pdf</@ofbizUrl>');" class="buttontext"/></span></td>
								</tr>
							</table>
						</form>
				   	</tr>
				   	 <tr class="alternate-row">
						<form id="EmployeeWiseSalaryDetails" name="EmployeeWiseSalaryDetails" mothed="post" action="<@ofbizUrl>EmployeeWiseSalaryDetails</@ofbizUrl>" target="_blank">
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="19%"><span class='h3'>Employee Wise Salary Details</span></td>
									<td width="29%"><span class='h3'>Employee Id<input type="text" id="PartyId" size="10pt" name="employeeId"/></span></td>
									<td width="40%"><span class='h3'>Period Id</span>
										<select name="customTimePeriodId" id="customTimePeriodId" class='h5' >
											<#list customTimePeriodList?sort_by("fromDate") as customTimePeriod><option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option></#list>
										</select>
									</td>
									<td width="15%"><span class='h3'><input type="submit" value="PDF" onClick="javascript:appendParams('EmployeeWiseSalaryDetails', '<@ofbizUrl>EmployeeWiseSalaryDetailsPdf.pdf</@ofbizUrl>');" class="buttontext"/>
									<input type="submit" value="CSV" onClick="javascript:appendParams('EmployeeWiseSalaryDetails', '<@ofbizUrl>EmployeeWiseSalaryDetailsCsv.csv</@ofbizUrl>');" class="buttontext"/></span></td>
								</tr>
							</table>
						</form>
					</tr>
				   	<tr class="alternate-row">
						<table class="basic-table" cellspacing="3">
							<form id="CanteenReport" name="CanteenReport" mothed="post" action="<@ofbizUrl>CanteenReport.pdf</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="13%"><span class='h3'>Canteen Report</span></td>
										<td width="45%"><span class='h3'>Period Id</span>
											<select name="customTimePeriodId" class='h4'>
												<#list timePeriodList as timePeriod>    
													<option value='${timePeriod.customTimePeriodId}'>${timePeriod.fromDate?if_exists}-${timePeriod.thruDate?if_exists}</option>
												</#list>      
											</select>
										</td>	
										<td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
									</tr>
								</table>	
							</form>
						</table>
					</tr>
					<tr class="alternate-row"> 
						<form id="GSLISReport" name="GSLISReport" mothed="post" action="<@ofbizUrl>GSLISReport.pdf</@ofbizUrl>" target="_blank">
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="13%"><span class='h3'>GSLIS Report</span></td>
									<td width="45%"><span class='h3'>Period Id</span>
										<select name="customTimePeriodId" id="customTimePeriodId" class='h5' >
											<#list customTimePeriodList?sort_by("fromDate") as customTimePeriod><option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option></#list>
										</select>
									</td>
									<td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
								</tr>
							</table>
						</form>
					</tr>
					<#--  <tr class="alternate-row">
						<form id="ProfessionalTaxReport" name="ProfessionalTaxReport" mothed="post" action="<@ofbizUrl>ProfessionalTaxReport.pdf</@ofbizUrl>" target="_blank">
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="13%"><span class='h3'>Professional Tax Report</span></td>
									<td width="45%"><span class='h3'>Period Id</span>
										<select name="customTimePeriodId" id="customTimePeriodId" class='h5' >
											<#list customTimePeriodList?sort_by("fromDate") as customTimePeriod><option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option></#list>
										</select>
									</td>
									<td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
								</tr>
							</table>
						</form>
					</tr>   -->
					<tr class="alternate-row">
						<form id="PayrollConsolidatedSummaryReport" name="PayrollConsolidatedSummaryReport" mothed="post" action="<@ofbizUrl>PayrollConsolidatedSummaryReport.pdf</@ofbizUrl>" target="_blank">
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="20%"><span class='h3'>Payroll Consolidated Summary Report</span></td>
									<td width="30%"><spam class='h3'>Organization Id 
										<select name="deptId" class='h4'>
											<#list orgList as org>    
												<option value='${org.partyId}'>${org.groupName?if_exists}</option>
											</#list> 
										</select></span>
									</td>	
									<td width="30%"><span class='h3'>Period Id
										<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
											<#list customTimePeriodList as customTimePeriod><option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option></#list>
										</select></span>
									</td>	
									<td width="5%"></td>
									<td width="30%"><span class="h3"><input type="submit" value="Download" class="buttontext"></span></td>
								</tr>
							</table>
						</form>
					</tr>
				   	<tr class="alternate-row">
						<form id="BenefitsOrDeductionsExport" name="BenefitsOrDeductionsExport" mothed="post" action="<@ofbizUrl>ExportEmployeeBenefitsOrDeductions</@ofbizUrl>">
							<table class="basic-table" cellspacing="0">
								<tr>
									<td><span class='h3'>BenefitsOrDeductions</span></td>
									<td width="30%"><span class='h3'>Organization Id </span>
										<select name="partyId" class='h4'>
											<option value=''></option>
											<#list orgList as org>    
												<option value='${org.partyId}'>${org.groupName}</option>
											</#list> 
										</select>
									</td>
									<td><span class='h3'>Type</span></td>
									<td>
										<span class='h6'>
											<select name="type" class='h4'>
												<option value=''></option>
												<option value='benefits'>benefits</option>
												<option value='deductions'>deductions</option>
											</select>
										</span>
									</td>
									<td ><span class='h3'>Benefits</span></td>
									<td >
										<span class='h6'>
											<select name="benefitTypeId" class='h6'>
												<option value=''></option>
												<#list allBenefitsTypeList as benefits>
													<option value='${benefits.benefitTypeId}'>${benefits.benefitName?if_exists}</option>
												</#list>	
											</select>
										</span>
									</td>
									<td ><span class='h3'>Deduction</span></td>
									<td>
										<span class='h6'>
											<select name="dedTypeId" class='h6'>
												<option value=''></option>
												<#list allDeductionTypeList as deductions>
													<option value='${deductions.deductionTypeId}'>${deductions.deductionName?if_exists}</option>
												</#list>	
											</select>
										</span>
									</td>
									<td><span class='h3'>Period</span></td>
									<td>
									<span class='h6'>
										<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
											<#list customTimePeriodList?sort_by("fromDate") as customTimePeriod><option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option></#list>
										</select>
									</span>
									</td>
									<td ><input type="submit" value="Download" class="buttontext"></td>
								</tr>
								
							</table>
						</form>
					</tr>
			   	</table>
			</div>
		</div>
	</div>
</#if>
<#if reportFrequencyFlag =="StatutoryReports">
	<div>
		<div class="screenlet">
			<div class="screenlet-title-bar">
				<h3>Reports</h3>
			</div>
			<div class="screenlet-body">
				<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
				   <tr class="alternate-row">
						<form id="ITEarningsReport" name="ITEarningsReport" mothed="post" action="<@ofbizUrl>ITEarningsReport.pdf</@ofbizUrl>" target="_blank">
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="19%"><span class='h3'>IT Earnings Report<input  type="hidden"  value="ITEarningsReport"   name="reportTypeFlag"/></span></td>
									<td width="29%"><span class='h3'>Employee Id<@htmlTemplate.lookupField formName="ITEarningsReport" name="employeeId" id="PartyId" fieldFormName="LookupEmployeeName"/></span></td>
									<td width="15%"><span class='h3'>From Date<input  type="text"  id="ITEarningsfromDate"   name="fromDate"/></span></td>
									<td width="15%"><span class='h3'>Thru Date<input  type="text"  id="ITEarningsthruDate"   name="thruDate"/></span></td>
									<td width="15%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
								</tr>
							</table>
						</form>
					</tr>
					<tr class="alternate-row">
						<form id="ITDeductionsReport" name="ITDeductionsReport" mothed="post" action="<@ofbizUrl>ITEarningsReport.pdf</@ofbizUrl>" target="_blank">
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="19%"><span class='h3'>IT Deductions Report<input  type="hidden"  value="ITDeductionsReport"   name="reportTypeFlag"/></span></td>
									<td width="29%"><span class='h3'>Employee Id<@htmlTemplate.lookupField formName="ITDeductionsReport" name="employeeId" id="PartyId" fieldFormName="LookupEmployeeName"/></span></td>
									<td width="15%"><span class='h3'>From Date<input  type="text"  id="ITDeductionsfromDate"   name="fromDate"/></span></td>
									<td width="15%"><span class='h3'>Thru Date<input  type="text"  id="ITDeductionsthruDate"   name="thruDate"/></span></td>
									<td width="15%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
								</tr>
							</table>
						</form>
					</tr>
					<tr class="alternate-row">
						<form id="PFMonthlyStatement" name="PFMonthlyStatement" method="post" action="<@ofbizUrl>PFMonthlyStatement.pdf</@ofbizUrl>" >	
							<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="20%"><span class='h3'>PF Monthly Statement</span></td>
										<td width="30%">
											<span class='h6'>
												<select name="EmplType" class='h6'>
													<option value=''></option>
													<option value='MDStaff'>MDStaff</option>
													<option value='DeputationStaff'>DeputationStaff</option>
												</select>
											</span>
										</td>
										<td width="32%"><span class='h3'>Period Id</span>
											<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
											<#list customTimePeriodList as customTimePeriod><option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option></#list>
										</select>
										</td>	
										<td width="15%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
									</tr>
								</table>	
							</form>
						</table>
					</tr>
					<tr class="alternate-row">
						<form id="ITAXStatement" name="ITAXStatement" mothed="post" action="<@ofbizUrl>ITAXStatement.pdf</@ofbizUrl>" target="_blank">
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="19%"><span class='h3'>Monthly IncomeTax Statement<input  type="hidden"  value="ITAXStatement"   name="reportTypeFlag"/></span></td>
									<td width="29%"></td>
									<td width="30%"><span class='h3'>Period Id
										<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
											<#list customTimePeriodList as customTimePeriod><option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option></#list>
										</select></span>
									</td>
									<td width="15%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
								</tr>
							</table>
						</form>
					</tr>
					<tr class="alternate-row">
						<form id="ESIMonthlystatement" name="ESIMonthlystatement" method="post" action="<@ofbizUrl>ESIMonthlystatement.pdf</@ofbizUrl>" target="_blank" >	
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="20%"><span class='h3'>ESI Monthly statement</span></td>
									<td width="30%"><span class='h3'>Period Id
										<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
											<#list customTimePeriodList as customTimePeriod><option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option></#list>
										</select></span>
									</td>	
									<td width="15%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
								</tr>
							</table>	
						</form>
					</tr>
					<tr class="alternate-row">
						<form id="ITAXQuarterlyStatement" name="ITAXQuarterlyStatement" mothed="post" action="<@ofbizUrl>ITAXQuarterlyStatementCsv.csv</@ofbizUrl>" target="_blank">
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="19%"><span class='h3'>Quarterly IncomeTax Statement<input  type="hidden"  value="ITAXQuaerlyrtStatement"   name="reportTypeFlag"/></span></td>
									<td width="15%"><span class='h3'>From: <input type='text' id='fromMonth' name='fromMonth' onmouseover='monthPicker()' class="monthPicker"/></span></td>
			      		 			<td width="15%"><span class='h3'>Thru: <input type='text' id='thruMonth' name='thruMonth' onmouseover='monthPicker()' class="monthPicker"/></span></td>
									<td width="15%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
								</tr>
							</table>
						</form>
					</tr>
					<tr class="alternate-row">
						<form id="ESIFormSix" name="ESIFormSix" method="post" action="<@ofbizUrl>ESIFormSix.pdf</@ofbizUrl>" >	
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="20%"><span class='h3'>ESI Form 6</span></td>
									<td width="32%"><span class='h3'>From Date<input  type="text"  id="ESIForm6fromDate"   name="fromDate"/></span></td>
									<td width="32%"><span class='h3'>Thru Date<input  type="text"  id="ESIForm6thruDate"   name="thruDate"/></span></td>	
									<td width="15%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
								</tr>
							</table>	
						</form>
					</tr>
			   	</table>
			</div>
		</div>
	</div>
</#if>

 