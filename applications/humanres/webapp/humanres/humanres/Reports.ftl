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
		makeDatePicker("MPfromDate","MPfromDate");
		makeDatePicker("MPthruDate","MPthruDate");
		makeDatePicker("LOPfromDate","LOPfromDate");
		makeDatePicker("LOPthruDate","LOPthruDate");
		makeDatePicker("EditedLateHoursfromDate","EditedLateHoursfromDate");
		makeDatePicker("EPfromDate","EPfromDate");
		makeDatePicker("EPthruDate","EPthruDate");
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
									<option value='${org.partyId}'>${org.groupName}</option>
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
									<option value='${org.partyId}'>${org.groupName}</option>
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
							<td width="5%">Leave Availed Report</td>
							<td width="5%">Leave Type
								<select name="leaveTypeId" id="leaveTypeId" class='h5' >
									<option value="ALL">ALL</option>
										<#list leaveTypeList as leave>
									<option value='${leave.leaveTypeId}'>${leave.description}</option></#list>
								</select>
							</td>
							<td width="5%">From Date<input  type="text"  id="larfromDate"   name="larFromDate"/></td>
							<td width="5%">Thru Date<input  type="text"  id="larthruDate"   name="larThruDate"/></td>
							<td width="3%"><input type="submit" value="Download" class="buttontext"></td> 
							</td>
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
										<td width="20%"><span class='h3'>Monthly Attendence Checklist</span></td>
										<td width="20%"><span class='h3'>From Date</span><input  type="text" size="18pt" id="mclfromDate"   name="mclFromDate"/></td>
										<td width="20%"><span class='h3'>Thru Date</span><input  type="text" size="18pt" id="mclthruDate"   name="mclThruDate"/></td>
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
						<form id="EditedLateHoursReport" name="EditedLateHoursReport" mothed="post" action="<@ofbizUrl>EditedLateHoursReport.pdf</@ofbizUrl>" target="_blank">
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="11%"><span class='h3'>Edited Late Hours Report</span></td>
									<td width="23%"><span class='h3'>From Date</span><input  type="text"  id="EditedLateHoursfromDate"  size="18pt" name="fromDate"/></td>
									<td width="11%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
								</tr>
							</table>		 
							</td>
						</form>
					</tr>
					<tr class="alternate-row">
						<table class="basic-table" cellspacing="3">
							<form id="CanteenReport" name="CanteenReport" mothed="post" action="<@ofbizUrl>CanteenReport.pdf</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="12%"><span class='h3'>Canteen Report</span></td>
										<td width="24%"><span class='h3'>Period Id</span>
											<select name="customTimePeriodId" class='h4'>
												<#list timePeriodList as timePeriod>    
													<option value='${timePeriod.customTimePeriodId}'>${timePeriod.fromDate}-${timePeriod.thruDate}</option>
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
											<option value='${timePeriod.customTimePeriodId}'>${timePeriod.fromDate}-${timePeriod.thruDate}</option>
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
					
					 <tr class="alternate-row">
						<form id="EmployeeWiseAttendanceDetails" name="EmployeeWiseAttendanceDetails" mothed="post" action="<@ofbizUrl>EmplMonthAttendanceDetails</@ofbizUrl>" target="_blank">
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="19%"><span class='h3'>Employee Month Attendance Details</span></td>
									<td width="15%"><span class='h3'>Employee Id</span><input type="text" size="10pt" id="PartyId" name="partyIdTo"/></td>
									<td width="27%"><span class='h3'>Period Id</span>
										<select name="customTimePeriodId" class='h4'>
											<#list timePeriodList as timePeriod>    
												<option value='${timePeriod.customTimePeriodId}'>${timePeriod.fromDate}-${timePeriod.thruDate}</option>
											</#list>      
										</select>
									</td>
									<td width="20%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
								</tr>
							</table>
						</form>
					</tr>
					<tr class="alternate-row">
						<form id="AttendanceExceptionReport" name="AttendanceExceptionReport" mothed="post" action="<@ofbizUrl>AttendanceExceptionReport.pdf</@ofbizUrl>" target="_blank">
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="12%"><span class='h3'>Attendance Exception Report</span></td>
									<td width="24%"><span class='h3'>Period Id</span>
									<select name="customTimePeriodId" class='h4'>
										<#list timePeriodList as timePeriod>    
											<option value='${timePeriod.customTimePeriodId}'>${timePeriod.fromDate}-${timePeriod.thruDate}</option>
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
						<form id="MonthlyBankAdviceStatement" name="MonthlyBankAdviceStatement" mothed="post" action="<@ofbizUrl>EmployeeBankDetailsPdf.pdf</@ofbizUrl>" target="_blank">
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="17%"><span class='h3'>Monthly Bank Advice Statement</span></td>
									<td width="25%"><span class='h3'>Organization Id </span>
										<select name="partyIdFrom" class='h4'>
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
									<td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
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
						<form id="BenefitsOrDeductionsExport" name="BenefitsOrDeductionsExport" mothed="post" action="<@ofbizUrl>ExportEmployeeBenefitsOrDeductions</@ofbizUrl>" target="_blank">
							<table class="basic-table" cellspacing="0">
								<tr>
									<td><span class='h3'>BenefitsOrDeductions</span></td>
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

 