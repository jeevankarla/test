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
				$("#"+thruDateId).datepicker( "option", {setDate: '0',minDate: '-5y', maxDate: '+1y'}).datepicker('setDate', date);
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
		makeDatePicker("lerfromDate","lerfromDate");
		makeDatePicker("lerthruDate","lerthruDate");
		makeDatePicker("ITEarningsfromDate","ITEarningsfromDate");
		makeDatePicker("ITEarningsthruDate","ITEarningsthruDate");
		makeDatePicker("ITDeductionsfromDate","ITDeductionsfromDate");
		makeDatePicker("ITDeductionsthruDate","ITDeductionsthruDate");
		makeDatePicker("MPfromDate","MPfromDate");
		makeDatePicker("MPthruDate","MPthruDate");
		makeDatePicker("lbDate","lbDate");
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
		makeDatePicker("ESIForm7fromDate","ESIForm7fromDate");
		makeDatePicker("ESIForm7thruDate","ESIForm7thruDate");
		makeDatePicker("PFform7fromDate","PFform7fromDate");
		makeDatePicker("PFform7thruDate","PFform7tthruDate");		
		makeDatePicker("EDLISfromDate","EDLISfromDate");
		makeDatePicker("EDLISthruDate","EDLISthruDate");
		makeDatePicker("GSfromDate","GSfromDate");
		makeDatePicker("GSthruDate","GSthruDate");
		makeDatePicker("GdrFromDate","GdrFromDate");
		makeDatePicker("CadrefromDate","CadrefromDate");
		makeDatePicker("CadrethruDate","CadrethruDate");
		makeDatePicker("DepartmentCadrefromDate","DepartmentCadrefromDate");
		makeDatePicker("DepartmentCadrethruDate","DepartmentCadrethruDate");
		makeDatePicker("NewEmplfromDate","NewEmplfromDate");
		makeDatePicker("NewEmplthruDate","NewEmplthruDate");
		makeDatePicker("loanFromDate","loanFromDate");
		makeDatePicker("loanThruDate","loanThruDate");
		makeDatePicker("AttMyfromDate","AttMyfromDate");
		makeDatePicker("AttMythruDate","AttMythruDate");
		makeDatePicker("AttAllfromDate","AttAllfromDate");
		makeDatePicker("AttAllthruDate","AttAllthruDate");
		makeDatePicker("AttfromDate","AttfromDate");
		makeDatePicker("AttthruDate","AttthruDate");
		makeDatePicker("BenDedMyfromDate","BenDedMyfromDate");
		makeDatePicker("BenDedMythruDate","BenDedMythruDate");
		makeDatePicker("BenDedAllfromDate","BenDedAllfromDate");
		makeDatePicker("BenDedAllthruDate","BenDedAllthruDate");
		makeDatePicker("payMasterDataMyfromDate","payMasterDataMyfromDate");
		makeDatePicker("payMasterDataMythruDate","payMasterDataMythruDate");
		makeDatePicker("payMasterDataAllfromDate","payMasterDataAllfromDate");
		makeDatePicker("payMasterDataAllthruDate","payMasterDataAllthruDate");
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
	     setOrgPartyId();
	     
	     jQuery("#assessmentFromDate").datepicker({
		dateFormat:'dd-mm-yy',
		showSecond: true,
		changeMonth: true,
		changeYear: true,
		yearRange: "-30:+0",
		onSelect: function(selectedDate) {
			//jQuery("#leaveFromDate").datepicker('setDate', selectedDate);
		}
	});	
	jQuery("#assessmentThruDate").datepicker({
		dateFormat:'dd-mm-yy',
		showSecond: true,
		changeMonth: true,
		changeYear: true,
		yearRange: "-30:+0",
		onSelect: function(selectedDate) {
			//jQuery("#leaveFromDate").datepicker('setDate', selectedDate);
		}
	});	
	 jQuery("#TrainingFromDate").datepicker({
		dateFormat:'dd-mm-yy',
		showSecond: true,
		changeMonth: true,
		changeYear: true,
		yearRange: "-30:+0",
		onSelect: function(selectedDate) {
			//jQuery("#leaveFromDate").datepicker('setDate', selectedDate);
		}
	});	   
	
	 jQuery("#TrainingThruDate").datepicker({
		dateFormat:'dd-mm-yy',
		showSecond: true,
		changeMonth: true,
		changeYear: true,
		yearRange: "-30:+0",
		onSelect: function(selectedDate) {
			//jQuery("#leaveFromDate").datepicker('setDate', selectedDate);
		}
	});	   
	  
	     
	});


</script>
<div>
  <div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Organization Selection</h3>
    </div>
    <div class="screenlet-body">                                            
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;" >      	          	     	   
      	<tr class="alternate-row">     		
      			<td></td>
      			<td align="center">
	   				  Organization Id :
      				<select name="partyId"  id="partyId"  onchange="javascript:setOrgPartyId();">
      					<#if security.hasEntityPermission("HUMANRES", "_ADMIN", session)>
      					<#list PartyGroupList as PartyList>
                			<option value='Company'>${PartyList.groupName?if_exists}</option>
                		</#list> 
                		<#list roPartyList as org>
                			<option value='${org.partyId}'>${org.groupName?if_exists}</option>
                		</#list>
                		<#else>
                			<#list roPartyList as org>
                				<option value='${org.partyId}'>${org.groupName?if_exists}</option>
                			</#list>
                		</#if>
                			<#-- <option value='INT58'>Pochampally</option> -->           
					</select>
          		</td> 
          	</tr>      	   	      	                 
	  </table>
    </div>
  </div>
</div> 

<script type="text/javascript">
function setOrgPartyId() {

	$(".commonPartyId").each(function() {
		$(this).val($("#partyId").val());
    });
}	
</script>


<#if reportFrequencyFlag =="MastersReports">
	<div>
		<div class="screenlet">
			<div class="screenlet-title-bar">
				<h3>Reports</h3>
			</div>
			<div class="screenlet-body">
				<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("EmployeesListCsv.xls")?exists) && (reportDetailsMap.get("EmployeesListCsv.xls") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("EmployeesListCsv.xls"))?exists))> 
						<tr class="alternate-row"> 
							<form id="masterEmployees" name="HR-Master" mothed="post" action="<@ofbizUrl>EmployeesListCsv.xls</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="25%"><span class='h3'>HR Master Information</span></td>
										<td><input type="hidden" name="partyId" class="commonPartyId"></td>
										<td width="50%"><span class='h3'>
												From Date<input  type="text" size="18pt" id="fromDate"   name="FromDate"/>
												${uiLabelMap.toDate}<input  type="text" size="18pt" id="thruDate"   name="ThruDate"/>
											</span>
										</td>
										<td width="25%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
									</tr>
								</table>
							</form>
						</tr>
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("EmployeeProfilePdf.pdf")?exists) && (reportDetailsMap.get("EmployeeProfilePdf.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("EmployeeProfilePdf.pdf"))?exists))> 
						<tr class="alternate-row"> 
							<form id="EmployeePersonalProfile" name="EmployeePersonalProfile" mothed="post" action="<@ofbizUrl>EmployeeProfilePdf.pdf</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="25%"><span class='h3'>Employee Personal Profile</span></td>
										<td><input type="hidden" name="partyIdFrom" class="commonPartyId"></td>
										<td width="50%"><span class='h3'>
												Employee Id<@htmlTemplate.lookupField formName="EmployeePersonalProfile" name="employeeId" fieldFormName="LookupEmployeeName"/>
											</span>
										</td>
										
										<td width="25%"><input type="submit" value="PDF" onClick="javascript:appendParams('EmployeePersonalProfile', '<@ofbizUrl>EmployeeProfilePdf.pdf</@ofbizUrl>');" class="buttontext"/>
										<span class='h3'><input type="submit" value="CSV" onClick="javascript:appendParams('EmployeePersonalProfile', '<@ofbizUrl>EmployeeProfileCsv.csv</@ofbizUrl>');" class="buttontext"/></span></td>
									</tr>
								</table>
							</form>
						</tr>
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("LICRDReportPdf.pdf")?exists) && (reportDetailsMap.get("LICRDReportPdf.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("LICRDReportPdf.pdf"))?exists))> 
						<tr class="alternate-row"> 
							<form id="LICRDReport" name="LICRDReport" mothed="post" action="<@ofbizUrl>LICRDReportPdf.pdf</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="25%"><span class='h3'>LIC/RD Report</span></td>
										<td width="30%"><span class='h3'>Insurance Type 
												<select name="insuranceTypeId" sclass='h4'>
													<#list finalInsuranceTypeList as org>    
														<option value='${org.insuranceTypeId}'>${org.description?if_exists}</option>
													</#list> 
												</select>
											</span>
										</td>
										<td width="35%"><span class='h3'>Period Id
												<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
													<#list customTimePeriodList as customTimePeriod><option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option></#list>
												</select>
											</span>
										</td>	
									<td width="25%"><input type="submit" value="PDF" onClick="javascript:appendParams('LICRDReport', '<@ofbizUrl>LICRDReportPdf.pdf</@ofbizUrl>');" class="buttontext"/>
									<span class='h3'><input type="submit" value="CSV" onClick="javascript:appendParams('LICRDReport', '<@ofbizUrl>LICRDReportCsv.csv</@ofbizUrl>');" class="buttontext"/></span></td>
									</tr>
								</table>
							</form>
						</tr>
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("MonthlyLICReport.pdf")?exists) && (reportDetailsMap.get("MonthlyLICReport.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("MonthlyLICReport.pdf"))?exists))> 
						<tr class="alternate-row"> 
							<form id="MonthlyLICReport" name="MonthlyLICReport" mothed="post" action="<@ofbizUrl>MonthlyLICReport.pdf</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="25%"><span class='h3'>Monthly LIC Report</span></td>
										<td width="30%"><span class='h3'>Insurance Type 
												<select name="insuranceTypeId" sclass='h4'>
													<#list InsuranceTypeList as org>    
														<option value='${org.insuranceTypeId}'>${org.description?if_exists}</option>
													</#list> 
												</select>
											</span>
										</td>
										<td width="35%"><span class='h3'>Period Id
												<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
													<#list customTimePeriodList as customTimePeriod><option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option></#list>
												</select>
											</span>
										</td>	
										<td width="25%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
									</tr>
								</table>
							</form>
						</tr>
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("CadreRankingReport.pdf")?exists) && (reportDetailsMap.get("CadreRankingReport.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("CadreRankingReport.pdf"))?exists))> 
						<tr class="alternate-row"> 
							<form id="CadreRankingReport" name="CadreRankingReport" mothed="post" action="<@ofbizUrl>CadreRankingReport.pdf</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="24%"><span class='h3'>Cadre Ranking Report</span></td>
										<td width="45%"><span class='h3'>
												From Date<input  type="text" size="18pt" id="CadrefromDate"   name="CadrefromDate"/>
												${uiLabelMap.toDate}<input  type="text" size="18pt" id="CadrethruDate"   name="CadrethruDate"/>
											</span>
										</td>
										<td width="5%"><input type="hidden" name="partyIdFrom" class="commonPartyId"/> 
										<td width="22%"><input type="submit" value="PDF" onClick="javascript:appendParams('CadreRankingReport', '<@ofbizUrl>CadreRankingReport.pdf</@ofbizUrl>');" class="buttontext"/>
										<span class='h3'><input type="submit" value="CSV" onClick="javascript:appendParams('CadreRankingReport', '<@ofbizUrl>CadreRankingReport.csv</@ofbizUrl>');" class="buttontext"/></span></td>
									</tr>
								</table>
							</form>
						</tr>
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("DepartmentCadreRankingReport.pdf")?exists) && (reportDetailsMap.get("DepartmentCadreRankingReport.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("DepartmentCadreRankingReport.pdf"))?exists))> 
						<tr class="alternate-row"> 
							<form id="DepartmentCadreRankingReport" name="DepartmentCadreRankingReport" mothed="post" action="<@ofbizUrl>CadreRankingReport.pdf</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="24%"><span class='h3'>Department Cadre Ranking Report</span></td>
										<td width="40%"><span class='h3'>
												From Date<input  type="text" size="18pt" id="DepartmentCadrefromDate"   name="CadrefromDate"/>
												${uiLabelMap.toDate}<input  type="text" size="18pt" id="DepartmentCadrethruDate"   name="CadrethruDate"/>
											</span>
										</td>
										<td width="5%"><input type="hidden" name="partyIdFrom" class="commonPartyId"/> 
										<td width="5%"><input type="hidden" name="departmentFlag"  value="department"/> 
										<td width="22%"><input type="submit" value="PDF" onClick="javascript:appendParams('DepartmentCadreRankingReport', '<@ofbizUrl>CadreRankingReport.pdf</@ofbizUrl>');" class="buttontext"/>
										<span class='h3'><input type="submit" value="CSV" onClick="javascript:appendParams('DepartmentCadreRankingReport', '<@ofbizUrl>DepartmentCadreRankingReport.csv</@ofbizUrl>');" class="buttontext"/></span></td>
									</tr>
								</table>
							</form>
						</tr>
					</#if>
				    <#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("ProbationReport.pdf")?exists) && (reportDetailsMap.get("ProbationReport.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("ProbationReport.pdf"))?exists))> 
						<tr class="alternate-row"> 
							<form id="ProbationReport" name="ProbationReport" mothed="post" action="<@ofbizUrl>ProbationReport.pdf</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="24%"><span class='h3'>Probation Report</span></td>
										<td width="50%"><span class='h3'>
												Employee Id<@htmlTemplate.lookupField formName="ProbationReport" name="employeeId" fieldFormName="LookupEmployeeName"/>
											</span>
										</td> 
										<td width="22%"><input type="submit" value="PDF" onClick="javascript:appendParams('ProbationReport', '<@ofbizUrl>ProbationReport.pdf</@ofbizUrl>');" class="buttontext"/>
						                </td>
									</tr>
								</table>
							</form>
						</tr>
					</#if>			
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("QuaterlyProbationReport.pdf")?exists) && (reportDetailsMap.get("QuaterlyProbationReport.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("QuaterlyProbationReport.pdf"))?exists))> 
						<tr class="alternate-row"> 
							<form id="QuaterlyProbationReport" name="QuaterlyProbationReport" mothed="post" action="<@ofbizUrl>QuaterlyProbationReport.pdf</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="24%"><span class='h3'>Quaterly Probation Report</span></td>
										<td width="50%"><span class='h3'>
												Employee Id<@htmlTemplate.lookupField formName="QuaterlyProbationReport" name="employeeId" fieldFormName="LookupEmployeeName"/>
											</span>
										</td> 
										<td width="22%"><input type="submit" value="PDF" onClick="javascript:appendParams('QuaterlyProbationReport', '<@ofbizUrl>QuaterlyProbationReport.pdf</@ofbizUrl>');" class="buttontext"/>
						                </td>
									</tr>
								</table>
							</form>
						</tr>
					</#if>
					
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("EmplJoiningReport.pdf")?exists) && (reportDetailsMap.get("EmplJoiningReport.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("EmplJoiningReport.pdf"))?exists))> 
						<tr class="alternate-row"> 
							<form id="EmplJoiningReport" name="EmplJoiningReport" mothed="post" action="<@ofbizUrl>EmplJoiningReport.pdf</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="24%"><span class='h3'>New Employee Joining Report</span></td>
										<td width="50%"><span class='h3'>
												From Date<input  type="text" size="18pt" id="NewEmplfromDate"   name="NewEmplfromDate"/>
												${uiLabelMap.toDate}<input  type="text" size="18pt" id="NewEmplthruDate"   name="NewEmplthruDate"/>
											</span>
										</td>
										<td width="22%"><input type="submit" value="PDF" onClick="javascript:appendParams('EmplJoiningReport', '<@ofbizUrl>EmplJoiningReport.pdf</@ofbizUrl>');" class="buttontext"/>
						                </td>
									</tr>
								</table>
							</form>
						</tr>
					</#if>		
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("PerformanceRatingReport.csv")?exists) && (reportDetailsMap.get("PerformanceRatingReport.csv") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("PerformanceRatingReport.csv"))?exists))> 
						<tr class="alternate-row"> 
							<form id="PerformanceRatingReport" name="PerformanceRatingReport" mothed="post" action="<@ofbizUrl>PerformanceRatingReport.csv</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="24%"><span class='h3'>Performance Rating Report</span></td>
										<td width="20%"><span class='h3'>
												Employee Id<@htmlTemplate.lookupField formName="PerformanceRatingReport" name="employeeId" fieldFormName="LookupEmployeeName"/>
											</span>
										</td> 
										<td width="40%"><span class='h3'>
												From Date <input type='text' id='assessmentFromDate' name='assessmentFromDate' />
												${uiLabelMap.toDate} <input type='text' id='assessmentThruDate' name='assessmentThruDate' />
											</span>
										</td>
										<td width=22%"><input type="submit" value="Downlaod" onClick="javascript:appendParams('PerformanceRatingReport', '<@ofbizUrl>PerformanceRatingReport.csv</@ofbizUrl>');" class="buttontext"/>
						                </td>
									</tr>
								</table>
							</form>
						</tr>
					</#if>		
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("PersonTrainingReport.csv")?exists) && (reportDetailsMap.get("PersonTrainingReport.csv") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("PersonTrainingReport.csv"))?exists))> 
						<tr class="alternate-row"> 
							<form id="PersonTrainingReport" name="PersonTrainingReport" mothed="post" action="<@ofbizUrl>PersonTrainingReport.csv</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="24%"><span class='h3'>Person Training Report</span></td>
										<td width="20%"><span class='h3'>
												Employee Id<@htmlTemplate.lookupField formName="PersonTrainingReport" name="employeeId" fieldFormName="LookupEmployeeName"/>
											</span>
										</td> 
										<td width="40%"><span class='h3'>
												From Date <input type='text' id='TrainingFromDate' name='TrainingFromDate' />
												${uiLabelMap.toDate} <input type='text' id='TrainingThruDate' name='TrainingThruDate' />
											</span>
										</td>
										<td width=22%"><input type="submit" value="Downlaod" onClick="javascript:appendParams('PersonTrainingReport', '<@ofbizUrl>PersonTrainingReport.csv</@ofbizUrl>');" class="buttontext"/>
						                </td>
									</tr>
								</table>
							</form>
						</tr>
					</#if>		
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
				<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("LeaveAvailedReport.pdf")?exists) && (reportDetailsMap.get("LeaveAvailedReport.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("LeaveAvailedReport.pdf"))?exists))> 
						<tr class="alternate-row">
							<form id="LeaveAvailedReport" name="LeaveAvailedReport" mothed="post" action="<@ofbizUrl>LeaveAvailedReport.pdf</@ofbizUrl>" target="_blank">
								<td width="20%" class='h3'>Leave Availed Report</td>
								<td width="20%"><span class='h3'>Employee Id<@htmlTemplate.lookupField formName="LeaveAvailedReport" name="employeeId" id="employeeId" size="10pt" fieldFormName="LookupEmployeeName"/></span></td>
								<td><input type="hidden" name="partyId" class="commonPartyId"></td>
								<td width="10%">Leave Type
									<select name="leaveTypeId" id="leaveTypeId" class='h5' >
										<option value="ALL">ALL</option>
											<#list leaveTypeList as leave>
										<option value='${leave.leaveTypeId}'>${leave.description?if_exists}</option></#list>
									</select>
								</td>
								<td width="10%">From Date<input  type="text"  id="larfromDate"   name="larFromDate"/></td>
								<td width="10%">${uiLabelMap.toDate}<input  type="text"  id="larthruDate"   name="larThruDate"/></td>
								<td width="10%"><input type="submit" value="Download" class="buttontext"></td> 
								</td>
							</form>
						</tr>
					</#if>
						<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("CashEncashmentReport.pdf")?exists) && (reportDetailsMap.get("CashEncashmentReport.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("CashEncashmentReport.pdf"))?exists))>
						<tr class="alternate-row">
							<form id="CashEncashmentReport" name="CashEncashmentReport" mothed="post" action="<@ofbizUrl>CashEncashmentReport.pdf</@ofbizUrl>" target="_blank">
								<td width="40%" class='h3'>Cash Encashment Report</td>
								<td width="30%"><span class='h3'>Employee Id<@htmlTemplate.lookupField formName="LeaveAvailedReport" name="employeeId" id="employeeId" size="10pt" fieldFormName="LookupEmployeeName"/></span></td>
								<td><input type="hidden" name="deptId" class="commonPartyId"></td>
								<td width="30%">Period Id
									<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
										<#list customTimePeriodList as customTimePeriod>
										 <#if defaultTimePeriodId?exists && (defaultTimePeriodId == customTimePeriod.customTimePeriodId)>
					      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
					      					<#else>
					      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
					                  		</option>
					      				</#if>
										 
										</#list>
									</select>
								</td>	
								<td width="30%"></td>
							<td width="30%"></td>
							<td width="10%"><input type="submit" value="Download" class="buttontext"></td>
							</form>											
						</tr>
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("GHSSDepatmentCountReport.pdf")?exists) && (reportDetailsMap.get("GHSSDepatmentCountReport.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("GHSSDepatmentCountReport.pdf"))?exists))>
						<tr class="alternate-row">
							<form id="GHSSDepatmentCountReport" name="GHSSDepatmentCountReport" mothed="post" action="<@ofbizUrl>GHSSDepatmentCountReport.pdf</@ofbizUrl>" target="_blank">
							<td width="40%"class='h3' >GH and SS Worked Employee's Count </td>
							<td width="30%"></td>
							<td width="30%"></td>
							<td width="30%">Period Id
								<select name="deptCount_TimePeriodId" id="deptCount_TimePeriodId" class='h4'>
									<#assign customTimePeriodList=customTimePeriodList?sort>
										<#list customTimePeriodList as customTimePeriod>
										 <#if defaultTimePeriodId?exists && (defaultTimePeriodId == customTimePeriod.customTimePeriodId)>
					      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
					      					<#else>
					      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
					                  		</option>
					      				</#if>
								 
								      </#list>
								</select>
							</td>	
							<td width="50%"></td>
							<td width="30%"></td>
							<td width="10%"><input type="submit" value="Download" class="buttontext"></td>
							</form>										
						</tr>
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("LeaveBalanceCheckList.pdf")?exists) && (reportDetailsMap.get("LeaveBalanceCheckList.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("LeaveBalanceCheckList.pdf"))?exists))>  
						<tr class="alternate-row">
							<form id="leaveBalanceChecklist" name="leaveBalanceChecklist" mothed="post" action="<@ofbizUrl>LeaveBalanceCheckList.pdf</@ofbizUrl>" target="_blank">
								<td width="40%"><span class='h3'>Leave Balance Report</span></td>
								<td width="30%"><span class='h3'>Employee Id<@htmlTemplate.lookupField formName="leaveBalanceChecklist" name="partyIdTo" id="prtyId" fieldFormName="LookupEmployeeName"/></span></td>
								<td width="30%"></td>
								<td width="30%"><span class='h3'>Date </span><input  type="text" size="18pt" id="lbDate"   name="lbDate"/></td>
								<td width="30%"></td>
								<td width="30%"></td>
								<td width="30%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
							</form>
						 </tr>
					 </#if>
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
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("MonthlyAttendenceChecklist.pdf")?exists) && (reportDetailsMap.get("MonthlyAttendenceChecklist.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("MonthlyAttendenceChecklist.pdf"))?exists))> 
						<tr class="alternate-row">
							<table class="basic-table" cellspacing="2">
								<form id="MonthlyAttendenceChecklist" name="MonthlyAttendenceChecklist" mothed="post" action="<@ofbizUrl>MonthlyAttendenceChecklist.pdf</@ofbizUrl>" target="_blank">
									<table class="basic-table" cellspacing="5">
										<tr class="alternate-row">
											<td width="21%"><span class='h3'>Attendence And Leave Checklist</span></td>
											<td width="20%"><span class='h3'>Employee Id<@htmlTemplate.lookupField formName="MonthlyAttendenceChecklist" name="partyIdTo" id="prtyId" fieldFormName="LookupEmployeeName"/></span></td>
											<td width="15%"><span class='h3'>From Date</span><input  type="text" size="18pt" id="mclfromDate"   name="mclFromDate"/></td>
											<td width="15%"><span class='h3'>${uiLabelMap.toDate}</span><input  type="text" size="18pt" id="mclthruDate"   name="mclThruDate"/></td>
											<td width="20%"><span class='h3'></span><input type="submit" value="Download" class="buttontext"></td> 
										</tr>
									</table>	
								</form>
							</table>
						</tr>
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("EmployeeMisPunchData.pdf")?exists) && (reportDetailsMap.get("EmployeeMisPunchData.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("EmployeeMisPunchData.pdf"))?exists))> 
						<tr class="alternate-row">
							<form id="EmployeeMisPunchData" name="EmployeeMisPunchData" mothed="post" action="<@ofbizUrl>EmployeeMisPunchData.pdf</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="20%"><span class='h3'>Employee Mis Punch Data</span></td>
										<td width="20%"><span class='h3'>From Date</span><input  type="text" size="18pt" id="MPfromDate"   name="MPfromDate"/></td>
										<td width="20%"><span class='h3'>${uiLabelMap.toDate}</span><input  type="text" size="18pt" id="MPthruDate"   name="MPthruDate"/></td>
										<td width="20%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
									</tr>
								</table>
							</form>
						 </tr>
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("EmployeePunchData.pdf")?exists) && (reportDetailsMap.get("EmployeePunchData.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("EmployeePunchData.pdf"))?exists))> 
						<tr class="alternate-row">
						 	<form id="EmployeePunchData" name="EmployeePunchData" mothed="post" action="<@ofbizUrl>EmployeePunchData.pdf</@ofbizUrl>" target="_blank">
		      	   				<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
							      	   	<td width="20%"><span class='h3'>Employee Punch Data</td></span></td>
							      	   	<td width="20%"><span class='h3'>From Date</span><input  type="text"  id="EPfromDate" size="18pt" name="EPfromDate"/></td>
							  			<td width="20%"><span class='h3'>${uiLabelMap.toDate}</span><input  type="text"  id="EPthruDate" size="18pt" name="EPthruDate"/></td>
										<td width="20%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
									</tr>
								</table>	
	      	   				</form>
	      	  			</tr>
	      	  		</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("DayWiseEditedLateHoursReport.pdf")?exists) && (reportDetailsMap.get("DayWiseEditedLateHoursReport.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("DayWiseEditedLateHoursReport.pdf"))?exists))> 
						<tr class="alternate-row">
							<table class="basic-table" cellspacing="3">
								<form id="DayWiseEditedLateHoursReport" name="DayWiseEditedLateHoursReport" mothed="post" action="<@ofbizUrl>DayWiseEditedLateHoursReport.pdf</@ofbizUrl>" target="_blank">
									<table class="basic-table" cellspacing="5">
										<tr class="alternate-row">
										<td width="12%"><span class='h3'>Day Wise Edited Late Hours Report</span></td>
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
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("ConsolidatedEditedLateHoursReport.pdf")?exists) && (reportDetailsMap.get("ConsolidatedEditedLateHoursReport.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("ConsolidatedEditedLateHoursReport.pdf"))?exists))> 
						<tr class="alternate-row">
							<table class="basic-table" cellspacing="3">
								<form id="ConsolidatedEditedLateHoursReport" name="ConsolidatedEditedLateHoursReport" mothed="post" action="<@ofbizUrl>ConsolidatedEditedLateHoursReport.pdf</@ofbizUrl>" target="_blank">
									<table class="basic-table" cellspacing="5">
										<tr class="alternate-row">
										<td width="12%"><span class='h3'>Consolidated Edited Late Hours Report</span></td>
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
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("EmployeesLOPdays.pdf")?exists) && (reportDetailsMap.get("EmployeesLOPdays.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("EmployeesLOPdays.pdf"))?exists))> 
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
			 		</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("EmplMonthlyPunchReport.csv")?exists) && (reportDetailsMap.get("EmplMonthlyPunchReport.csv") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("EmplMonthlyPunchReport.csv"))?exists))>
				 	<tr class="alternate-row">
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
				   </tr>
				   </#if> -->
					<#-- <#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("EmplMonthAttendanceDetails")?exists) && (reportDetailsMap.get("EmplMonthAttendanceDetails") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("EmplMonthAttendanceDetails"))?exists))>
					<tr class="alternate-row">
						<form id="EmployeeWiseAttendanceDetails" name="EmployeeWiseAttendanceDetails" mothed="post" action="<@ofbizUrl>EmplMonthAttendanceDetails</@ofbizUrl>" target="_blank">
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="21%"><span class='h3'>Employee Attendance Details</span></td>
									<td width="15%"><span class='h3'>Employee Id<@htmlTemplate.lookupField formName="EmployeeWiseAttendanceDetails" name="partyIdTo" id="PartyId" fieldFormName="LookupEmployeeName"/></span></td>
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
					</tr> 
					</#if>-->
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("AttendanceExceptionReport.pdf")?exists) && (reportDetailsMap.get("AttendanceExceptionReport.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("AttendanceExceptionReport.pdf"))?exists))>	 
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
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("BusArrivalReport.pdf")?exists) && (reportDetailsMap.get("BusArrivalReport.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("BusArrivalReport.pdf"))?exists))> 
						<tr class="alternate-row">
						 	<form id="BusArrivalReport" name="BusArrivalReport" mothed="post" action="<@ofbizUrl>BusArrivalReport.pdf</@ofbizUrl>" target="_blank">
		      	   				<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
							      	   	<td width="20%"><span class='h3'>Bus Arrival Report</td></span></td>
							      	   	<td width="20%"><span class='h3'>From Date</span><input  type="text"  id="BusfromDate" size="18pt" name="BusfromDate"/></td>
							  			<td width="20%"><span class='h3'>${uiLabelMap.toDate}</span><input  type="text"  id="BusthruDate" size="18pt" name="BusthruDate"/></td>
										<td width="20%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
									</tr>
								</table>	
	      	   				</form>
	      	  			</tr>
	      	  		</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("OODReport.pdf")?exists) && (reportDetailsMap.get("OODReport.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("OODReport.pdf"))?exists))> 
						<tr class="alternate-row">
						 	<form id="OODReport" name="OODReport" mothed="post" action="<@ofbizUrl>OODReport.pdf</@ofbizUrl>" target="_blank">
		      	   				<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
							      	   	<td width="25%"><span class='h3'>OOD Report</td></span></td>
							      	   	<td width="20%"><span class='h3'>From Date</span><input  type="text"  id="OODfromDate" size="18pt" name="OODfromDate"/></td>
							  			<td width="20%"><span class='h3'>${uiLabelMap.toDate}</span><input  type="text"  id="OODthruDate" size="18pt" name="OODthruDate"/></td>
										<td width="20%"><span class='h3'>Employee Id<@htmlTemplate.lookupField formName="OODReport" name="employeeId" id="PartyId" fieldFormName="LookupEmployeeName"/></span></td>
										<td width="20%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
									</tr>
								</table>	
	      	   				</form>
	      	  			</tr>
  	  				</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("PayableDaysReport.pdf")?exists) && (reportDetailsMap.get("PayableDaysReport.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("PayableDaysReport.pdf"))?exists))> 
						<tr class="alternate-row">
							<form id="PayableDaysReport" name="PayableDaysReport" mothed="post" action="<@ofbizUrl>PayableDaysReport.pdf</@ofbizUrl>" target="_blank">
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
					</#if> 
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
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("PayrollExceptionReport.pdf")?exists) && (reportDetailsMap.get("PayrollExceptionReport.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("PayrollExceptionReport.pdf"))?exists))> 
						<tr class="alternate-row">
							<form id="PayrollExceptionReport" name="PayrollExceptionReport" mothed="post" action="<@ofbizUrl>PayrollExceptionReport.pdf</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="25%"><span class='h3'>Payroll Exception Report</span></td>
										<td><input type="hidden" name="partyId" class="commonPartyId"></td>
										<td width="50%"><span class='h3'>Period Id</span>
											<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
												<#list customTimePeriodList as customTimePeriod>
													 <#if defaultTimePeriodId?exists && (defaultTimePeriodId == customTimePeriod.customTimePeriodId)>
								      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								      					<#else>
								      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								                  		</option>
								      				</#if>
												</#list>
											</select>
										</td>	
										<td width="25%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
									</tr>
								</table>	
							</form>
					   </tr>
			   		</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("EmployeeBankDetailsPdf.pdf")?exists) && (reportDetailsMap.get("EmployeeBankDetailsPdf.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("EmployeeBankDetailsPdf.pdf"))?exists))> 
						<tr class="alternate-row">
							<form id="MonthlyBankAdviceStatement" name="MonthlyBankAdviceStatement" mothed="post" action="<@ofbizUrl>EmployeeBankDetailsPdf.pdf</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td><input type="hidden" name="bankAdvise_deptId" class="commonPartyId"></td>
										<td width="25%"><span class='h3'>Monthly Bank Advice Statement</span></td>
										<td width="30%"><span class='h3'>Bank</span>
											<select name="finAccountId" class='h4'>
												<option value='All'>All</option>
												<#list companyAccList as bank>    
													<option value='${bank.finAccountId?if_exists}'>${bank.finAccountName?if_exists}</option>
												</#list> 
											</select>
										</td>
										<td width="35%"><span class='h3'>Period Id
											<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
												<#list customTimePeriodList as customTimePeriod>
													 <#if defaultTimePeriodId?exists && (defaultTimePeriodId == customTimePeriod.customTimePeriodId)>
								      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								      					<#else>
								      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								                  		</option>
								      				</#if>
													 
												</#list>
											</select></span>
										</td>
										<td width="10%"><span class='h4'><input type="submit" value="PDF" onClick="javascript:appendParams('MonthlyBankAdviceStatement', '<@ofbizUrl>EmployeeBankDetailsPdf.pdf</@ofbizUrl>');" class="buttontext"/>
										 <input type="submit" value="Excel" onClick="javascript:appendParams('MonthlyBankAdviceStatement', '<@ofbizUrl>EmployeeBankDetailsCsv.xls</@ofbizUrl>');" class="buttontext"/></span></td> 
										</td>
									</tr>
								</table>
							</form>
						</tr>
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("BankRequestLetterPdf.pdf")?exists) && (reportDetailsMap.get("BankRequestLetterPdf.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("BankRequestLetterPdf.pdf"))?exists))> 
						<tr class="alternate-row">
							<form id="BankRequestLetter" name="BankRequestLetter" mothed="post" action="<@ofbizUrl>BankRequestLetterPdf.pdf</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="23%"><span class='h3'>Bank Request Letter</span></td>
										<td width="30%"><span class='h3'>Bank</span>
											<select name="finAccountId" class='h4'>
												<option value='All'>All</option>
												<#list companyAccList as bank>    
													<option value='${bank.finAccountId?if_exists}'>${bank.finAccountName?if_exists}</option>
												</#list> 
											</select>
										</td>
										<td width="30%"><span class='h3'>Period Id
											<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
												<#list customTimePeriodList as customTimePeriod>
													 <#if defaultTimePeriodId?exists && (defaultTimePeriodId == customTimePeriod.customTimePeriodId)>
								      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								      					<#else>
								      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								                  		</option>
								      				</#if>
													 
												</#list>
											</select></span>
										</td>
										<td width="10%"><span class='h4'><input type="submit" value="PDF" onClick="javascript:appendParams('BankRequestLetter', '<@ofbizUrl>BankRequestLetterPdf.pdf</@ofbizUrl>');" class="buttontext"/>
										 
										</td>
									</tr>
								</table>
							</form>
						</tr>
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("MonthlyBankStatement.pdf")?exists) && (reportDetailsMap.get("MonthlyBankStatement.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("MonthlyBankStatement.pdf"))?exists))> 
						<tr class="alternate-row">
							<form id="MonthlyBankStatement" name="MonthlyBankStatement" mothed="post" action="<@ofbizUrl>MonthlyBankStatement.pdf</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="25%"><span class='h3'>Monthly Bank Statement</span></td>
										<td><input type="hidden" name="bankAdvise_deptId" class="commonPartyId"></td>
										<td width="30%"><span class='h3'>Bank</span>
											<select name="finAccountId" class='h4'>
												<option value='All'>All</option>
												<#list companyAccList as bank>    
													<option value='${bank.finAccountId?if_exists}'>${bank.finAccountName?if_exists}</option>
												</#list> 
											</select>
										</td>
										<td width="35%"><span class='h3'>Period Id
											<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
												<#list customTimePeriodList as customTimePeriod>
													 <#if defaultTimePeriodId?exists && (defaultTimePeriodId == customTimePeriod.customTimePeriodId)>
								      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								      					<#else>
								      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								                  		</option>
								      				</#if>
												</#list>
											</select></span>
										</td>
										<td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
									</tr>
								</table>
							</form>
						</tr>
					</#if>	
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("EcsBankDetailsCsv.csv")?exists) && (reportDetailsMap.get("EcsBankDetailsCsv.csv") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("EcsBankDetailsCsv.csv"))?exists))> 
						<tr class="alternate-row">
							<form id="EcsBankDetailsStatement" name="EcsBankDetailsStatement" mothed="post" action="<@ofbizUrl>EcsBankDetailsCsv.csv</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="25%"><span class='h3'>Ecs Bank Details Statement</span></td>
										<td><input type="hidden" name="bankAdvise_deptId" class="commonPartyId"></td>
										<td width="30%"><span class='h3'>Bank</span>
											<select name="finAccountId" class='h4'>
												<option value='All'>All</option>
												<#list companyAccList as bank>    
													<option value='${bank.finAccountId?if_exists}'>${bank.finAccountName?if_exists}</option>
												</#list> 
											</select>
										</td>
										<td width="35%"><span class='h3'>Period Id
											<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
												<#list customTimePeriodList as customTimePeriod>
													 <#if defaultTimePeriodId?exists && (defaultTimePeriodId == customTimePeriod.customTimePeriodId)>
								      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								      					<#else>
								      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								                  		</option>
								      				</#if>
												</#list>
											</select></span>
										</td>
										<td width="10%"><span class='h4'>
										 <input type="submit" value="CSV" onClick="javascript:appendParams('EcsBankDetailsStatement', '<@ofbizUrl>EcsBankDetailsCsv.csv</@ofbizUrl>');" class="buttontext"/></span></td> 
										</td>
									</tr>
								</table>
							</form>
						</tr>
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("PrintPaySlipsPdf.pdf")?exists) && (reportDetailsMap.get("PrintPaySlipsPdf.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("PrintPaySlipsPdf.pdf"))?exists))> 
						<tr class="alternate-row">	
							<form id="paySlipEmployeewise" name="paySlipEmployeewise" mothed="post" action="<@ofbizUrl>PrintPaySlipsPdf.pdf</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="25%"><span class='h3'>Pay Slip Employee Wise</span></td>
										<td><input type="hidden" name="OrganizationId" class="commonPartyId"></td>
										<td width="40%"><span class='h3'>Period Id</span>
											<select name="customTimePeriodId" id="customTimePeriodId" class='h5'>
												<#assign customTimePeriodList=customTimePeriodList?sort>
												<#list customTimePeriodList as customTimePeriod>
												 <#if defaultTimePeriodId?exists && (defaultTimePeriodId == customTimePeriod.customTimePeriodId)>
							      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
							      					<#else>
							      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
							                  		</option>
							      				</#if>
										      </#list>
											</select>
										</td>
										<td width="20%"><span class='h3'>Employee Id<@htmlTemplate.lookupField formName="paySlipEmployeewise" name="employeeId" fieldFormName="LookupEmployeeName"/></span></td>
										<td width="15%"><span class='h3'><input type="submit" value="PDF" onClick="javascript:appendParams('paySlipEmployeewise', '<@ofbizUrl>PrintPaySlipsPdf.pdf</@ofbizUrl>');" class="buttontext"/></span></td>
									</tr>
								</table>
							</form>
			 			</tr>
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("PrePrintedpaySlip.pdf")?exists) && (reportDetailsMap.get("PrePrintedpaySlip.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("PrePrintedpaySlip.pdf"))?exists))> 
					   	<tr class="alternate-row">
							<form id="PrePrintedpaySlip" name="PrePrintedpaySlip" mothed="post" action="<@ofbizUrl>PrePrintedpaySlip.pdf</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="25%"><span class='h3'>Pre Printed Pay Slip Report</span></td>
										<td><input type="hidden" name="OrganizationId" class="commonPartyId"></td>
										<td width="40%"><span class='h3'>Period Id</span>
											<select name="customTimePeriodId" id="customTimePeriodId" class='h5'>
												<#assign customTimePeriodList=customTimePeriodList?sort>
												<#list customTimePeriodList as customTimePeriod>
												 <#if defaultTimePeriodId?exists && (defaultTimePeriodId == customTimePeriod.customTimePeriodId)>
							      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
							      					<#else>
							      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
							                  		</option>
							      				</#if>
										      </#list>
											</select>
										</td>
										<td width="20%"><span class='h3'>Employee Id<@htmlTemplate.lookupField formName="PrePrintedpaySlip" name="employeeId" fieldFormName="LookupEmployeeName"/></span></td>
										<td width="15%"><span class='h3'><input type="submit" value="PDF" onClick="javascript:appendParams('paySlipEmployeewise', '<@ofbizUrl>PrintPaySlipsPdf.pdf</@ofbizUrl>');" class="buttontext"/></span></td>
									</tr>
								</table>
							</form>
					   	</tr>
			   		</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("CanteenReport.pdf")?exists) && (reportDetailsMap.get("CanteenReport.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("CanteenReport.pdf"))?exists))> 
					   	<tr class="alternate-row">
							<table class="basic-table" cellspacing="3">
								<form id="CanteenReport" name="CanteenReport" mothed="post" action="<@ofbizUrl>CanteenReport.pdf</@ofbizUrl>" target="_blank">
									<table class="basic-table" cellspacing="5">
										<tr class="alternate-row">
											<td width="25%"><span class='h3'>Canteen Report</span></td>
											<td width="50%"><span class='h3'>Period Id
												<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
													<#list customTimePeriodList as customTimePeriod>
														 <#if defaultTimePeriodId?exists && (defaultTimePeriodId == customTimePeriod.customTimePeriodId)>
									      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
									      					<#else>
									      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
									                  		</option>
									      				</#if>
													</#list>
												</select></span>
											</td>	
											<td width="25%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
										</tr>
									</table>	
								</form>
							</table>
						</tr>
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("IncrementCertificate.pdf")?exists) && (reportDetailsMap.get("IncrementCertificate.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("IncrementCertificate.pdf"))?exists))> 
					   	<tr class="alternate-row"> 
							<form id="IncrementCertificate" name="IncrementCertificate" mothed="post" action="<@ofbizUrl>IncrementCertificate.pdf</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="25%"><span class='h3'>Increment Certificate Report</span></td>
										<td width="20%"><span class='h3'>Employee Id<@htmlTemplate.lookupField formName="IncrementCertificate" name="employeeId" fieldFormName="LookupEmployeeName"/></span></td>
										<td width="30%"><span class='h3'>Period Id</span>
											<select name="customTimePeriodId" id="customTimePeriodId" class='h5' >
												<#list customTimePeriodList?sort_by("fromDate") as customTimePeriod><option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option></#list>
											</select>
										</td>
										<td width="25%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
									</tr>
								</table>
							</form>
						</tr>
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("DepartmentTotalsReport.pdf")?exists) && (reportDetailsMap.get("DepartmentTotalsReport.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("DepartmentTotalsReport.pdf"))?exists))> 
						<tr class="alternate-row">
							<form id="DepartmentTotalsReport" name="DepartmentTotalsReport" mothed="post" action="<@ofbizUrl>DepartmentTotalsReport.txt</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="25%"><span class='h3'>Department Totals </span></td>
										<td><input type="hidden" name="ShedId" class="commonPartyId"/> 
										<td width="50%"><span class='h3'>Period Id</span>
											<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
												<#list customTimePeriodList as customTimePeriod>
													 <#if defaultTimePeriodId?exists && (defaultTimePeriodId == customTimePeriod.customTimePeriodId)>
								      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								      					<#else>
								      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								                  		</option>
								      				</#if>
												</#list>
											</select>
										</td>	
										<td width="25%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
									</tr>
								</table>	
							</form>
					   	</tr>
					</#if>
					<tr class="alternate-row">
							<form id="RegionalOfficeTotals" name="RegionalOfficeTotals" mothed="post" action="<@ofbizUrl>RegionalOfficeTotals.pdf</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="25%"><span class='h3'>Regional office Totals </span></td>
										<td width="50%"><span class='h3'>Period Id</span>
										<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
											<#list customTimePeriodList as customTimePeriod>
												 <#if defaultTimePeriodId?exists && (defaultTimePeriodId == customTimePeriod.customTimePeriodId)>
							      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
							      					<#else>
							      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
							                  		</option>
							      				</#if>
											</#list>
										</select>
									</td>	
									<td width="25%"><span class='h3'><input type="hidden" name="isRegionalOfficeTotals" id="isRegionalOfficeTotals" value="Y"/><input type="submit" value="Download" class="buttontext"></span></td>
								</tr>
							</table>	
						</form>
				   	</tr>
				   	<tr class="alternate-row">
							<form id="NoteSheetReport" name="NoteSheetReport" mothed="post" action="<@ofbizUrl>NoteSheetReport.pdf</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="25%"><span class='h3'>Note Sheet Report </span></td>
										<td width="50%"><span class='h3'>Period Id</span>
										<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
											<#list customTimePeriodList as customTimePeriod>
												 <#if defaultTimePeriodId?exists && (defaultTimePeriodId == customTimePeriod.customTimePeriodId)>
							      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
							      					<#else>
							      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
							                  		</option>
							      				</#if>
											</#list>
										</select>
									</td>	
									<td width="25%"><span class='h3'><input type="hidden" name="isRegionalOfficeTotals" id="isRegionalOfficeTotals" value="Y"/><input type="submit" value="Download" class="buttontext"></span></td>
								</tr>
							</table>	
						</form>
				   	</tr>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("IncomeTaxReport.txt")?exists) && (reportDetailsMap.get("IncomeTaxReport.txt") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("IncomeTaxReport.txt"))?exists))> 
						<tr class="alternate-row">
							<form id="IncomeTaxReport" name="IncomeTaxReport" mothed="post" action="<@ofbizUrl>IncomeTaxReport.txt</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="25%"><span class='h3'>Income Tax Report</span></td>
					   					<td width="50%"><span class='h3'>Period Id</span>
											<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
												<#list customTimePeriodIdsList as customTimePeriod>
													 <#if defaultTimePeriodId?exists && (defaultTimePeriodId == customTimePeriod.customTimePeriodId)>
								      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								      					<#else>
								      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								                  		</option>
								      				</#if>
												</#list>
											</select>
										</td>	
										<td width="25%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
									</tr>
								</table>	
							</form>
			   			</tr>
			   		</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("CostCodeReport.txt")?exists) && (reportDetailsMap.get("CostCodeReport.txt") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("CostCodeReport.txt"))?exists))> 
						<tr class="alternate-row">
							<form id="CostCodeReport" name="CostCodeReport" mothed="post" action="<@ofbizUrl>CostCodeReport.txt</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="25%"><span class='h3'>Cost Code Report</span></td>
										<td><input type="hidden" name="ShedId" class="commonPartyId"></td>
										<td width="50%"><span class='h3'>Period Id</span>
											<select name="customTimePeriodId" id="customTimePeriodId" class='h5' >
												<#list customTimePeriodList?sort_by("fromDate") as customTimePeriod><option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option></#list>
											</select>
										</td>
										<td width="25%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
									</tr>
								</table>
							</form>
						</tr>
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("EmployeeWiseSalaryDetails")?exists) && (reportDetailsMap.get("EmployeeWiseSalaryDetails") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("EmployeeWiseSalaryDetails"))?exists))> 
						<tr class="alternate-row">
							<form id="EmployeeWiseSalaryDetails" name="EmployeeWiseSalaryDetails" mothed="post" action="<@ofbizUrl>EmployeeWiseSalaryDetails</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="19%"><span class='h3'>Employee Wise Salary Details</span></td>
										<td width="33%"><span class='h3'>Employee Id<@htmlTemplate.lookupField formName="EmployeeWiseSalaryDetails" name="employeeId" fieldFormName="LookupEmployeeName"/></span></td>
										<td width="37%"><span class='h3'>Period Id</span>
											<select name="customTimePeriodId" id="customTimePeriodId" class='h5' >
												<#list customTimePeriodList as customTimePeriod>
													 <#if defaultTimePeriodId?exists && (defaultTimePeriodId == customTimePeriod.customTimePeriodId)>
								      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								      					<#else>
								      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								                  		</option>
								      				</#if>
										        </#list>
											</select>
										</td>
										<td width="10%"><span class='h3'><input type="submit" value="PDF" onClick="javascript:appendParams('EmployeeWiseSalaryDetails', '<@ofbizUrl>EmployeeWiseSalaryDetailsPdf.pdf</@ofbizUrl>');" class="buttontext"/>
										<input type="submit" value="CSV" onClick="javascript:appendParams('EmployeeWiseSalaryDetails', '<@ofbizUrl>EmployeeWiseSalaryDetailsCsv.csv</@ofbizUrl>');" class="buttontext"/></span></td>
									</tr>
								</table>
							</form>
						</tr>
					</#if>
				<#--	<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("DeductionsReport.txt")?exists) && (reportDetailsMap.get("DeductionsReport.txt") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("DeductionsReport.txt"))?exists))> 
						<tr class="alternate-row">
							<form id="DeductionsReport" name="DeductionsReport" mothed="post" action="<@ofbizUrl>DeductionsReport.txt</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="0">
									<tr>
										<td width="20%"><span class='h3'>Deductions Report</span></td>
										<td><input type="hidden" name="partyId" class="commonPartyId"/> 
										<td width="5%"><span class='h3'>Deduction</span></td>
										<td width="20%">
											<span class='h3'>
												<select name="dedTypeId" class='h3'>
													<option value='' ></option>
													<#list allDeductionTypeList as deductions>
														<option value='${deductions.deductionTypeId}'>${deductions.deductionName?if_exists}</option>
													</#list>	
												</select>
											</span>
										</td>
										<td width="5%"><span class='h3'>Period Id</span></td>
										<td>
										<span class='h3'>
											<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
												<#list customTimePeriodList?sort_by("fromDate") as customTimePeriod><option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option></#list>
											</select>
										</span>
										</td>
										<td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
									</tr>
								</table>
							</form>
						</tr>
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("BenefitReport.txt")?exists) && (reportDetailsMap.get("BenefitReport.txt") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("BenefitReport.txt"))?exists))> 
						<tr class="alternate-row">
							<form id="BenefitReport" name="BenefitReport" mothed="post" action="<@ofbizUrl>BenefitReport.txt</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="0">
									<tr>
										<td width="20%"><span class='h3'>Benefit Report</span></td>
										<td><input type="hidden" name="partyId" class="commonPartyId"></td>
										<td width="5%"><span class='h3'>Benefit</span></td>
										<td width="27%">
											<span class='h3'>
												<select name="benefitTypeId" class='h6'>
													<option value=''></option>
													<#list allBenefitsTypeList as benefits>
														<option value='${benefits.benefitTypeId}'>${benefits.benefitName?if_exists}</option>
													</#list>	
												</select>
											</span>
										</td>
										<td width="5%"><span class='h3'>Period Id</span></td>
										<td>
										<span class='h3'>
											<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
												<#list customTimePeriodList?sort_by("fromDate") as customTimePeriod><option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option></#list>
											</select>
										</span>
										</td>
										<td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
									</tr>
								</table>
							</form>
						</tr>
					</#if> -->
					<#-- <#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("ProfessionalTaxReport.pdf")?exists) && (reportDetailsMap.get("ProfessionalTaxReport.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("ProfessionalTaxReport.pdf"))?exists))> 
					 <tr class="alternate-row">
						<form id="ProfessionalTaxReport" name="ProfessionalTaxReport" mothed="post" action="<@ofbizUrl>ProfessionalTaxReport.pdf</@ofbizUrl" target="_blank">
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
					</tr> 
					</#if>  -->
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("PayrollConsolidatedSummaryReport.pdf")?exists) && (reportDetailsMap.get("PayrollConsolidatedSummaryReport.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("PayrollConsolidatedSummaryReport.pdf"))?exists))> 
						<tr class="alternate-row">
							<form id="PayrollConsolidatedSummaryReport" name="PayrollConsolidatedSummaryReport" mothed="post" action="<@ofbizUrl>PayrollConsolidatedSummaryReport.pdf</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="20%"><span class='h3'>Payroll Consolidated Summary Report</span></td>
										<td width="10%"><span class='h3'>Gl Codes
											<select name="netPayglCode" id="netPayglCode">
												<option value="No" >No</option>
												<option value="Yes">Yes</option>
											</select>
										</span></td>
										<td><input type="hidden" name="deptId" class="commonPartyId"/> 
										<td width="36%"><span class='h3'>Period Id
											<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
												<#list customTimePeriodList as customTimePeriod>
													 <#if defaultTimePeriodId?exists && (defaultTimePeriodId == customTimePeriod.customTimePeriodId)>
								      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								      					<#else>
								      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								                  		</option>
								      				</#if>
												</#list>
											</select></span>
										</td>
										<td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>	
									</tr>
								</table>
							</form>
						</tr>
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("ExportEmployeeBenefitsOrDeductions")?exists) && (reportDetailsMap.get("ExportEmployeeBenefitsOrDeductions") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("ExportEmployeeBenefitsOrDeductions"))?exists))> 
						<tr class="alternate-row">
							<form id="BenefitsOrDeductionsExport" name="BenefitsOrDeductionsExport" mothed="post" action="<@ofbizUrl>ExportEmployeeBenefitsOrDeductions</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="0">
									<tr>
										<td><span class='h3'>BenefitsOrDeductions</span></td>
										<td><input type="hidden" name="partyId" class="commonPartyId"/> 
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
												<#list customTimePeriodList as customTimePeriod>
												 <#if defaultTimePeriodId?exists && (defaultTimePeriodId == customTimePeriod.customTimePeriodId)>
							      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
							      					<#else>
							      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
							                  		</option>
							      				</#if>
												</#list>
											</select>
										</span>
										</td>
										<td ><input type="submit" value="Download" class="buttontext"></td>
									</tr>
								</table>
							</form>
						</tr>
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("editPayableDaysReport")?exists) && (reportDetailsMap.get("editPayableDaysReport") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("editPayableDaysReport"))?exists))> 
						<tr class="alternate-row">
							<form id="editPayableDaysReport" name="editPayableDaysReport" mothed="post" action="<@ofbizUrl>editPayableDaysReport.pdf</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="12%"><span class='h3'>Edited Payable Days Report</span></td>
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
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("IncrementDueReport.pdf")?exists) && (reportDetailsMap.get("IncrementDueReport.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("IncrementDueReport.pdf"))?exists))> 
						<tr class="alternate-row">
							<form id="IncrementDueReport" name="IncrementDueReport" mothed="post" action="<@ofbizUrl>IncrementDueReport.pdf</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="22%"><span class='h3'>Increment Due Report</span></td>
										<td><input type="hidden" name="partyId" class="commonPartyId"/> 
										<td width="45%"><span class='h3'>Period Id</span>
											<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
												<#list customTimePeriodList as customTimePeriod>
													 <#if defaultTimePeriodId?exists && (defaultTimePeriodId == customTimePeriod.customTimePeriodId)>
								      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								      					<#else>
								      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								                  		</option>
								      				</#if>
												</#list>
											</select>
										</td>	
										<td width="20%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
									</tr>
								</table>
							</form>
					 	</tr>
					</#if>
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
				<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("ITEarningsReport.pdf")?exists) && (reportDetailsMap.get("ITEarningsReport.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("ITEarningsReport.pdf"))?exists))> 
				   <tr class="alternate-row">
						<form id="ITEarningsReport" name="ITEarningsReport" mothed="post" action="<@ofbizUrl>ITEarningsReport.pdf</@ofbizUrl>" target="_blank">
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="19%"><span class='h3'>IT Earnings Report<input  type="hidden"  value="ITEarningsReport"   name="reportTypeFlag"/></span></td>
									<td width="29%"><span class='h3'>Employee Id<@htmlTemplate.lookupField formName="ITEarningsReport" name="employeeId" id="PartyId" fieldFormName="LookupEmployeeName"/></span></td>
									<td width="15%"><span class='h3'>From Date<input  type="text"  id="ITEarningsfromDate"   name="fromDate"/></span></td>
									<td width="15%"><span class='h3'>${uiLabelMap.toDate}<input  type="text"  id="ITEarningsthruDate"   name="thruDate"/></span></td>
									<td width="15%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
								</tr>
							</table>
						</form>
					</tr>
				</#if>	
				<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("ITEarningsReport.pdf")?exists) && (reportDetailsMap.get("ITEarningsReport.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("ITEarningsReport.pdf"))?exists))> 
					<tr class="alternate-row">
						<form id="ITDeductionsReport" name="ITDeductionsReport" mothed="post" action="<@ofbizUrl>ITEarningsReport.pdf</@ofbizUrl>" target="_blank">
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="19%"><span class='h3'>IT Deductions Report<input  type="hidden"  value="ITDeductionsReport"   name="reportTypeFlag"/></span></td>
									<td width="29%"><span class='h3'>Employee Id<@htmlTemplate.lookupField formName="ITDeductionsReport" name="employeeId" id="PartyId" fieldFormName="LookupEmployeeName"/></span></td>
									<td width="15%"><span class='h3'>From Date<input  type="text"  id="ITDeductionsfromDate"   name="fromDate"/></span></td>
									<td width="15%"><span class='h3'>${uiLabelMap.toDate}<input  type="text"  id="ITDeductionsthruDate"   name="thruDate"/></span></td>
									<td width="15%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
								</tr>
							</table>
						</form>
					</tr>
				</#if>
				<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("PFMonthlyStatement.pdf")?exists) && (reportDetailsMap.get("PFMonthlyStatement.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("PFMonthlyStatement.pdf"))?exists))> 
					<tr class="alternate-row">
						<form id="PFMonthlyStatement" name="PFMonthlyStatement" method="post" action="<@ofbizUrl>PFMonthlyStatement.pdf</@ofbizUrl>" target="_blank">	
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
											    <#list customTimePeriodList as customTimePeriod>
												 <#if defaultTimePeriodId?exists && (defaultTimePeriodId == customTimePeriod.customTimePeriodId)>
							      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
							      					<#else>
							      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
							                  		</option>
							      				</#if>
												 
											</#list>
										</select>
										</td>	
										<td width="15%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
									</tr>
								</table>	
							</form>
						</table>
					</tr>
				</#if>
				<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("PFform3A.pdf")?exists) && (reportDetailsMap.get("PFform3A.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("PFform3A.pdf"))?exists))> 
					<tr class="alternate-row">
						<form id="PFform3A" name="PFform3A" method="post" action="<@ofbizUrl>PFform3A.pdf</@ofbizUrl>" target="_blank">	
							<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="40%"><span class='h3'>PF Form 3A</span></td>
										<td width="30%"><span class='h3'>Employee Id<@htmlTemplate.lookupField formName="Form16Report" name="employeeId" id="employeeId" size="10pt" fieldFormName="LookupEmployeeName"/></span></td>
										<td width="30%"><span class='h3'>Period Id</span>
											<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
												<#list customTimePeriodIdsList as customTimePeriod>
													 <#if finYearId?exists && (finYearId == customTimePeriod.customTimePeriodId)>
								      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								      					<#else>
								      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								                  		</option>
								      				</#if>
												</#list>
											</select>
										</td>	
										<td width="15%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
									</tr>
								</table>	
							</form>
						</table>
					</tr>
				</#if>
				<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("PFform6A.pdf")?exists) && (reportDetailsMap.get("PFform6A.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("PFform6A.pdf"))?exists))> 
					<tr class="alternate-row">
						<form id="PFform6A" name="PFform6A" method="post" action="<@ofbizUrl>PFform6A.pdf</@ofbizUrl>" target="_blank">	
							<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="20%"><span class='h3'>PF Form 6A</span></td>
										<td width="30%"><span class='h3'>Employee Id<@htmlTemplate.lookupField formName="Form16Report" name="employeeId" id="employeeId" size="10pt" fieldFormName="LookupEmployeeName"/></span></td>
										<td width="32%"><span class='h3'>Period Id</span>
											<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
												<#list customTimePeriodIdsList as customTimePeriod>
													 <#if finYearId?exists && (finYearId == customTimePeriod.customTimePeriodId)>
								      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								      					<#else>
								      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								                  		</option>
								      				</#if>
												</#list>
											</select>
										</td>	
										<td width="15%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
									</tr>
								</table>	
							</form>
						</table>
					</tr>
				</#if>
				<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("PFform7.pdf")?exists) && (reportDetailsMap.get("PFform7.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("PFform7.pdf"))?exists))> 
					<tr class="alternate-row">
						<form id="PFform7" name="PFform7" method="post" action="<@ofbizUrl>PFform7.pdf</@ofbizUrl>" target="_blank">	
							<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="20%"><span class='h3'>PFform7</span></td>
										<td width="30%"><span class='h3'>Employee Id<@htmlTemplate.lookupField formName="PFform7" name="employeeId" id="employeeId" size="10pt" fieldFormName="LookupEmployeeName"/></span></td>
										<td width="32%"><span class='h3'>Period Id</span>
											<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
											    <#list customTimePeriodIdsList as customTimePeriod>
							      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")?if_exists} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")?if_exists}</option>
												</#list>
											</select>
										</td>	
										<td width="15%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
									</tr>
								</table>	
							</form>
						</table>
					</tr>
				</#if>
				<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("ITAXStatement.pdf")?exists) && (reportDetailsMap.get("ITAXStatement.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("ITAXStatement.pdf"))?exists))> 
					<tr class="alternate-row">
						<form id="ITAXStatement" name="ITAXStatement" mothed="post" action="<@ofbizUrl>ITAXStatement.pdf</@ofbizUrl>" target="_blank">
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="13%"><span class='h3'>Monthly IncomeTax Statement<input  type="hidden"  value="ITAXStatement"   name="reportTypeFlag"/></span></td>
									<td width="25%"><span class='h3'>Period Id
										<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
											<#list customTimePeriodList as customTimePeriod>
												 <#if defaultTimePeriodId?exists && (defaultTimePeriodId == customTimePeriod.customTimePeriodId)>
							      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
							      					<#else>
							      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
							                  		</option>
							      				</#if>
											</#list>
										</select></span>
									</td>
									<td width="25%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
								</tr>
							</table>
						</form>
					</tr>
				</#if>
				<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("ESIMonthlystatement.pdf")?exists) && (reportDetailsMap.get("ESIMonthlystatement.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("ESIMonthlystatement.pdf"))?exists))> 
					<tr class="alternate-row">
						<form id="ESIMonthlystatement" name="ESIMonthlystatement" method="post" action="<@ofbizUrl>ESIMonthlystatement.pdf</@ofbizUrl>" target="_blank" >	
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="13%"><span class='h3'>ESI Monthly statement</span></td>
									<td width="25%"><span class='h3'>Period Id
										<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
											<#list customTimePeriodList as customTimePeriod>
												 <#if defaultTimePeriodId?exists && (defaultTimePeriodId == customTimePeriod.customTimePeriodId)>
							      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
							      					<#else>
							      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
							                  		</option>
							      				</#if>
											 
											</#list>
										</select></span>
									</td>	
									<td width="25%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
								</tr>
							</table>	
						</form>
					</tr>
				</#if>
				<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("ITAXQuarterlyStatementCsv.csv")?exists) && (reportDetailsMap.get("ITAXQuarterlyStatementCsv.csv") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("ITAXQuarterlyStatementCsv.csv"))?exists))> 
					<tr class="alternate-row">
						<form id="ITAXQuarterlyStatement" name="ITAXQuarterlyStatement" mothed="post" action="<@ofbizUrl>ITAXQuarterlyStatementCsv.csv</@ofbizUrl>" target="_blank">
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="15%"><span class='h3'>IT Form 24Q<input  type="hidden"  value="ITAXQuaerlyrtStatement"   name="reportTypeFlag"/></span></td>
									<td width="15%"><span class='h3'>From Date: <input type='text' id='fromMonth' name='fromMonth' onmouseover='monthPicker()' class="monthPicker"/></span></td>
			      		 			<td width="15%"><span class='h3'>${uiLabelMap.toDate}: <input type='text' id='thruMonth' name='thruMonth' onmouseover='monthPicker()' class="monthPicker"/></span></td>
			      		 			<td width="15%"><span class='h3'>Report Type:<select name="reportType" id="reportType">
			      		 			<option value="deductee">Deductee</option>
			      		 			<option value="deductor">Deductor</option>
			      		 			<option value="challan">Challan</option>
			      		 			<option value="form24Q">Annual Form 24Q</option>
			      		 			</select></span></td>  
									<td width="15%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
								</tr>
							</table>
						</form>
					</tr>
				</#if>
				<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("ESIFormSix.pdf")?exists) && (reportDetailsMap.get("ESIFormSix.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("ESIFormSix.pdf"))?exists))> 
					<tr class="alternate-row">
						<form id="ESIFormSix" name="ESIFormSix" method="post" action="<@ofbizUrl>ESIFormSix.pdf</@ofbizUrl>" target="_blank">	
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="20%"><span class='h3'>ESI Form 6</span></td>
									<td width="32%"><span class='h3'>From Date<input  type="text"  id="ESIForm6fromDate"   name="fromDate"/></span></td>
									<td width="32%"><span class='h3'>${uiLabelMap.toDate}<input  type="text"  id="ESIForm6thruDate"   name="thruDate"/></span></td>	
									<td width="15%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
								</tr>
							</table>	
						</form>
					</tr>
				</#if>
				<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("ESIFormSeven.pdf")?exists) && (reportDetailsMap.get("ESIFormSeven.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("ESIFormSeven.pdf"))?exists))> 
					<tr class="alternate-row">
						<form id="ESIFormSeven" name="ESIFormSeven" method="post" action="<@ofbizUrl>ESIFormSeven.pdf</@ofbizUrl>" target="_blank">	
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="20%"><span class='h3'>ESI Form 7</span></td>
									<td width="32%"><span class='h3'>From Date<input  type="text"  id="ESIForm7fromDate"   name="fromDate"/></span></td>
									<td width="32%"><span class='h3'>${uiLabelMap.toDate}<input  type="text"  id="ESIForm7thruDate"   name="thruDate"/></span></td>	
									<td width="15%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
								</tr>
							</table>	
						</form>
					</tr>
				</#if>
				<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("GratuitySupportReport.pdf")?exists) && (reportDetailsMap.get("GratuitySupportReport.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("GratuitySupportReport.pdf"))?exists))> 
					<tr class="alternate-row">
						<form id="GratuitySupportReport" name="GratuitySupportReport" method="post" action="<@ofbizUrl>GratuitySupportReportPdf.pdf</@ofbizUrl>" target="_blank">	
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="20%"><span class='h3'>Gratuity Support Report</span></td>
									<td width="32%"><span class='h3'>From Date<input  type="text"  id="GSfromDate"   name="fromDate"/></span></td>
									<td width="32%"><span class='h3'>${uiLabelMap.toDate}<input  type="text"  id="GSthruDate"   name="thruDate"/></span></td>
									<td width="15%"><input type="submit" value="PDF" onClick="javascript:appendParams('GratuitySupportReport', '<@ofbizUrl>GratuitySupportReportPdf.pdf</@ofbizUrl>');" class="buttontext"/>
									<span class='h3'><input type="submit" value="CSV" onClick="javascript:appendParams('GratuitySupportReport', '<@ofbizUrl>GratuitySupportReportCsv.csv</@ofbizUrl>');" class="buttontext"/></span></td> 
								</tr>
							</table>	
						</form>
					</tr>
				</#if>
				<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("EDLISReport.pdf")?exists) && (reportDetailsMap.get("EDLISReport.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("EDLISReport.pdf"))?exists))> 
					<tr class="alternate-row">
						<form id="EDLISReport" name="EDLISReport" method="post" action="<@ofbizUrl>EDLISReportPdf.pdf</@ofbizUrl>" target="_blank">	
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="20%"><span class='h3'>EDLIS Report</span></td>
									<td width="32%"><span class='h3'>From Date<input  type="text"  id="EDLISfromDate"   name="fromDate"/></span></td>
									<td width="32%"><span class='h3'>${uiLabelMap.toDate}<input  type="text"  id="EDLISthruDate"   name="thruDate"/></span></td>
									<td width="15%"><input type="submit" value="PDF" onClick="javascript:appendParams('EDLISReport', '<@ofbizUrl>EDLISReportPdf.pdf</@ofbizUrl>');" class="buttontext"/>
									<span class='h3'><input type="submit" value="CSV" onClick="javascript:appendParams('EDLISReport', '<@ofbizUrl>EDLISReportCsv.csv</@ofbizUrl>');" class="buttontext"/></span></td> 
								</tr>
							</table>	
						</form>
					</tr>
				</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("GSLISReport.pdf")?exists) && (reportDetailsMap.get("GSLISReport.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("GSLISReport.pdf"))?exists))> 
						<tr class="alternate-row"> 
							<form id="GSLISReport" name="GSLISReport" mothed="post" action="<@ofbizUrl>GSLISReport.pdf</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="20%"><span class='h3'>GSLIS Report</span></td>
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
											<select name="customTimePeriodId" id="customTimePeriodId" class='h5' >
												<#list customTimePeriodList as customTimePeriod>
													 <#if defaultTimePeriodId?exists && (defaultTimePeriodId == customTimePeriod.customTimePeriodId)>
								      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								      					<#else>
								      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								                  		</option>
								      				</#if>
										        </#list>
											</select>
										</td>
										<td width="25%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
									</tr>
								</table>
							</form>
						</tr>
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("Form16Report.pdf")?exists) && (reportDetailsMap.get("Form16Report.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("Form16Report.pdf"))?exists))> 
						<tr class="alternate-row"> 
							<form id="Form16Report" name="Form16Report" mothed="post" action="<@ofbizUrl>Form16Report.pdf</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="20%"><span class='h3'>FORM 16</span></td>
										<td><input type="hidden" name="partyIdFrom" class="commonPartyId"></td>
										<td width="20%"><span class='h3'>Employee Id<@htmlTemplate.lookupField formName="Form16Report" name="employeeId" id="employeeId" size="10pt" fieldFormName="LookupEmployeeName"/></span></td>
										<td width="30%"><span class='h3'>Period Id</span>
											<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
												<#list customTimePeriodIdsList as customTimePeriod>
													 <#if finYearId?exists && (finYearId == customTimePeriod.customTimePeriodId)>
								      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								      					<#else>
								      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								                  		</option>
								      				</#if>
												</#list>
											</select>
										</td>
										<td width="25%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
									</tr>
								</table>
							</form>
						</tr>
					</#if>										
			   	</table>
			</div>
		</div>
	</div>
</#if>





<#if reportFrequencyFlag =="PFReports">
	<div>
		<div class="screenlet">
			<div class="screenlet-title-bar">
			
				<h3>Reports</h3>
			</div>
			<div class="screenlet-body">
				<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
				<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("Table9pdf.pdf")?exists) && (reportDetailsMap.get("Table9pdf.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("Table9pdf.pdf"))?exists))> 
				   <tr class="alternate-row">
                      <form id="Table9pdf" name="PF Report" mothed="post" action="<@ofbizUrl>Table9pdf.pdf</@ofbizUrl>" target="_blank">
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
								    <td width="20%"><span class='h3'>PF Report</span></td>
								    <td width="32%"><span class='h3'>Period Id</span>
											<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
											    <#list customTimePeriodList as customTimePeriod>
												 <#if defaultTimePeriodId?exists && (defaultTimePeriodId == customTimePeriod.customTimePeriodId)>
							      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
							      					<#else>
							      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
							                  		</option>
							      				</#if>
												 
											</#list>
										</select>
										</td>	
									<td width="25%"><input type="submit" value="PDF" onClick="javascript:appendParams('Table9pdf', '<@ofbizUrl>Table9pdf.pdf</@ofbizUrl>');" class="buttontext"/>
								</tr>
							</table>
						</form>
					</tr>
				 </#if>	
			    </table>
			</div>
		</div>
	</div>
</#if>





<#if reportFrequencyFlag =="SupplyPayrollReports">
	<div>
		<div class="screenlet">
			<div class="screenlet-title-bar">
				<h3>Reports</h3>
			</div>
			<div class="screenlet-body">
				<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("LeaveEncashmentReport.pdf")?exists) && (reportDetailsMap.get("LeaveEncashmentReport.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("LeaveEncashmentReport.pdf"))?exists))> 
						<tr class="alternate-row">
							<form id="LeaveEncashmentReport" name="LeaveEncashmentReport" mothed="post" action="<@ofbizUrl>LeaveEncashmentReport.pdf</@ofbizUrl>" target="_blank">
								<td width="40%"><span class='h3'>Leave Encashment Report</span></td>
								<td width="20%"><span class='h3'>Employee Id<@htmlTemplate.lookupField formName="LeaveEncashmentReport" name="employeeId" id="employeeId" size="10pt" fieldFormName="LookupEmployeeName"/></span></td>
								<td width="30%" class='h4'>TimePeriod 
									<select name="periodBillingId" id="periodBillingId" class='h4'>
											<#list SupplyCustomTimePeriodList as customTimePeriod>
						      						<option value='${customTimePeriod.periodBillingId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
						                  		</option>
									      </#list>
									</select>
								</td>
								<td width="10%"></td>
								<td width="10%" class='h4'><input type="submit" value="Download" class="buttontext"></td> 
								</td>
							</form>
						</tr>
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("BonusPaySlip.pdf")?exists) && (reportDetailsMap.get("BonusPaySlip.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("BonusPaySlip.pdf"))?exists))> 
						<tr class="alternate-row"> 
							<form id="Form16Report" name="Form16Report" mothed="post" action="<@ofbizUrl>BonusPaySlip.pdf</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="25%"><span class='h3'>Bonus payslip : Detail</span></td>
										<td><input type="hidden" name="partyIdFrom" class="commonPartyId"></td>
										<td width="20%"><span class='h3'>Employee Id<@htmlTemplate.lookupField formName="Form16Report" name="employeeId" id="employeeId" size="10pt" fieldFormName="LookupEmployeeName"/></span></td>
										<td width="30%"><span class='h3'>Period Id</span>
											<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
												<#list customTimePeriodIdsList as customTimePeriod>
													 <#if finYearId?exists && (finYearId == customTimePeriod.customTimePeriodId)>
								      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								      					<#else>
								      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								                  		</option>
								      				</#if>
												</#list>
											</select>
										</td>
										<td width="25%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
									</tr>
								</table>
							</form>
						</tr>
					</#if>
				</table>
			</div>
		</div>
	</div>
</#if>

<#if reportFrequencyFlag =="LoanReports">
	<div>
		<div class="screenlet">
			<div class="screenlet-title-bar">
				<h3>Reports</h3>
			</div>
			<div class="screenlet-body">
				<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("newLoanTypeReport.pdf")?exists) && (reportDetailsMap.get("newLoanTypeReport.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("newLoanTypeReport.pdf"))?exists))> 
						<tr class="alternate-row">
							<form id="newLoanTypeReport" name="newLoanTypeReport" mothed="post" action="<@ofbizUrl>newLoanTypeReport.pdf</@ofbizUrl>" target="_blank">
								<td width="10%" class='h3'>Loan Report</td>
								<td><input type="hidden" name="partyId" class="commonPartyId"></td>
								<td width="15%">Loan Type
									<select name="loanTypeId" id="loanTypeId" class='h5' >
											<#list loanTypeList as loan>
										<option value='${loan.loanTypeId}'>${loan.description?if_exists}</option>
										</#list>
									</select>
								</td>
								<td width="20%"><span class='h3'>From Date<input  type="text"  id="loanFromDate"   name="fromDate"/></span></td>
						    	<td width="15%"><span class='h3'>${uiLabelMap.toDate}<input  type="text"  id="loanThruDate"   name="thruDate"/></span></td>
								<td width="10%"></td>
								<td width="10%"><input type="submit" value="Download" class="buttontext"></td> 
								</td>
							</form>
						</tr>
					</#if> 
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("loanTypeReport.pdf")?exists) && (reportDetailsMap.get("loanTypeReport.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("loanTypeReport.pdf"))?exists))> 
						<tr class="alternate-row">
							<form id="loanTypeReport" name="loanTypeReport" mothed="post" action="<@ofbizUrl>loanTypeReport.pdf</@ofbizUrl>" target="_blank">
								<td width="15%" class='h3'>Loan and Advances Report</td>
								<td><input type="hidden" name="partyId" class="commonPartyId"></td>
								<td width="10%">Loan Type
									<select name="loanTypeId" id="loanTypeId" class='h5' >
											<#list loanTypeList as loan>
										<option value='${loan.loanTypeId}'>${loan.description?if_exists}</option>
										</#list>
									</select>
								</td>
								<td width="10%"></td>
							<td width="20%" class = 'h3'>Period Id
								<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
									<#list customTimePeriodList as customTimePeriod>
									 <#if defaultTimePeriodId?exists && (defaultTimePeriodId == customTimePeriod.customTimePeriodId)>
				      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
				      					<#else>
				      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
				                  		</option>
				      				</#if>									 
									</#list>
								</select>
							</td>
							<td width="10%"></td>
							<td width="10%"><input type="submit" value="Download" class="buttontext"></td> 
								</td>
							</form>
						</tr>
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("disbursedLoanBankReportPdf.pdf")?exists) && (reportDetailsMap.get("disbursedLoanBankReportPdf.pdf") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("disbursedLoanBankReportPdf.pdf"))?exists))> 
						<tr class="alternate-row">
							<form id="disbursedLoanBankReport" name="disbursedLoanBankReport" mothed="post" action="<@ofbizUrl>disbursedLoanBankReportPdf.pdf</@ofbizUrl>" target="_blank">
								<td width="25%" class='h3'>Disbursed loans Bank Advise Report<input  type="hidden"  value="disbursedLoanBankReport"   name="reportTypeFlag"/></td>
								<td><input type="hidden" name="partyId" class="commonPartyId"></td>
								<td width="10%"></td>
								<td width="20%" class = 'h3'>Period Id
									<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
										<#list customTimePeriodList as customTimePeriod>
										 <#if defaultTimePeriodId?exists && (defaultTimePeriodId == customTimePeriod.customTimePeriodId)>
					      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
					      					<#else>
					      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
					                  		</option>
					      				</#if>									 
										</#list>
									</select>
								</td>
								<td width="10%"></td>
								<td width="25%"><input type="submit" value="PDF" onClick="javascript:appendParams('disbursedLoanBankReport', '<@ofbizUrl>disbursedLoanBankReportPdf.pdf</@ofbizUrl>');" class="buttontext"/>
									<span class='h3'><input type="submit" value="CSV" onClick="javascript:appendParams('disbursedLoanBankReport', '<@ofbizUrl>disbursedLoanBankReportCsv.csv</@ofbizUrl>');" class="buttontext"/></span></td> 
								</td>
							</form>
						</tr>
					</#if>			
				</table>
			</div>
		</div>
	</div>
</#if>
<#if reportFrequencyFlag =="CheckListReports">
	<div>
		<div class="screenlet">
			<div class="screenlet-title-bar">
				<h3>Reports</h3>
			</div>
			<div class="screenlet-body">
				<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("attendanceCheckList")?exists) && (reportDetailsMap.get("attendanceCheckList") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("attendanceCheckList"))?exists))> 
						<tr class="alternate-row">
							<form id="AttendanceChcekList" name="AttendanceChcekList" mothed="post" action="<@ofbizUrl>attendanceCheckList</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="20%"><span class='h3'>Attendance My Chcek List<input  type="hidden"  value="attendanceMyCheckList"   name="reportTypeFlag"/></span></td>
										<td><input type="hidden" name="partyId" class="commonPartyId"></td>
										<td width="20%"><span class='h3'>From Date</span><input  type="text" size="18pt" id="AttMyfromDate"   name="AttMyfromDate"/></td>
										<td width="20%"><span class='h3'>Thru Date</span><input  type="text" size="18pt" id="AttMythruDate"   name="AttMythruDate"/></td>
										<td width="20%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
									</tr>
								</table>
							</form>
						 </tr>
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("attendanceCheckList")?exists) && (reportDetailsMap.get("attendanceCheckList") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("attendanceCheckList"))?exists))>
						 <tr class="alternate-row">
							<form id="AttendanceAllChcekList" name="AttendanceAllChcekList" mothed="post" action="<@ofbizUrl>attendanceCheckList</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="20%"><span class='h3'>Attendance All Chcek List<input  type="hidden"  value="attendanceAllCheckList"   name="reportTypeFlag"/></span></td>
										<td><input type="hidden" name="partyId" class="commonPartyId"></td>
										<td width="20%"><span class='h3'>From Date</span><input  type="text" size="18pt" id="AttAllfromDate"   name="AttAllfromDate"/></td>
										<td width="20%"><span class='h3'>Thru Date</span><input  type="text" size="18pt" id="AttAllthruDate"   name="AttAllthruDate"/></td>
										<td width="20%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
									</tr>
								</table>
							</form>
						 </tr>
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("AttendanceNotGiven")?exists) && (reportDetailsMap.get("AttendanceNotGiven") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("AttendanceNotGiven"))?exists))>
						 <tr class="alternate-row">
							<form id="AttendanceNotGivenReport" name="AttendanceNotGivenReport" mothed="post" action="<@ofbizUrl>AttendanceNotGiven</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="20%"><span class='h3'>Attendance Not Given Report</span></td>
										<td><input type="hidden" name="partyId" class="commonPartyId"></td>
										<td width="20%"><span class='h3'>From Date</span><input  type="text" size="18pt" id="AttfromDate"   name="AttfromDate"/></td>
										<td width="20%"><span class='h3'>Thru Date</span><input  type="text" size="18pt" id="AttthruDate"   name="AttthruDate"/></td>
										<td width="20%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
									</tr>
								</table>
							</form>
						 </tr>
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("benDedCheckList")?exists) && (reportDetailsMap.get("benDedCheckList") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("benDedCheckList"))?exists))> 
						 <tr class="alternate-row">
							<form id="BenDedMyChcekList" name="BenDedMyChcekList" mothed="post" action="<@ofbizUrl>benDedCheckList</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="20%"><span class='h3'>Benefits/Deductions My Chcek List<input  type="hidden"  value="BenDedMyChcekList"   name="reportTypeFlag"/></span></td>
										<td><input type="hidden" name="partyId" class="commonPartyId"></td>
										<td width="20%"><span class='h3'>From Date</span><input  type="text" size="18pt" id="BenDedMyfromDate"   name="BenDedMyfromDate"/></td>
										<td width="20%"><span class='h3'>Thru Date</span><input  type="text" size="18pt" id="BenDedMythruDate"   name="BenDedMythruDate"/></td>
										<td width="20%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
									</tr>
								</table>
							</form>
						 </tr>
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("benDedCheckList")?exists) && (reportDetailsMap.get("benDedCheckList") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("benDedCheckList"))?exists))>
						 <tr class="alternate-row">
							<form id="BenDedAllChcekList" name="BenDedAllChcekList" mothed="post" action="<@ofbizUrl>benDedCheckList</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="20%"><span class='h3'>Benefits/Deductions All Chcek List<input  type="hidden"  value="BenDedAllChcekList"   name="reportTypeFlag"/></span></td>
										<td><input type="hidden" name="partyId" class="commonPartyId"></td>
										<td width="20%"><span class='h3'>From Date</span><input  type="text" size="18pt" id="BenDedAllfromDate"   name="BenDedAllfromDate"/></td>
										<td width="20%"><span class='h3'>Thru Date</span><input  type="text" size="18pt" id="BenDedAllthruDate"   name="BenDedAllthruDate"/></td>
										<td width="20%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
									</tr>
								</table>
							</form>
						 </tr>
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("payMasterDataCheckList")?exists) && (reportDetailsMap.get("payMasterDataCheckList") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("payMasterDataCheckList"))?exists))>
						 <tr class="alternate-row">
							<form id="payMasterDataMyCheckList" name="payMasterDataMyCheckList" mothed="post" action="<@ofbizUrl>payMasterDataCheckList</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="20%"><span class='h3'>pay Master Data My Chcek List<input  type="hidden"  value="payMasterDataMyCheckList"   name="reportTypeFlag"/></span></td>
										<td><input type="hidden" name="partyId" class="commonPartyId"></td>
										<td width="20%"><span class='h3'>From Date</span><input  type="text" size="18pt" id="payMasterDataMyfromDate"   name="payMasterDataMyfromDate"/></td>
										<td width="20%"><span class='h3'>Thru Date</span><input  type="text" size="18pt" id="payMasterDataMythruDate"  name="payMasterDataMythruDate"/></td>
										<td width="20%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
									</tr>
								</table>
							</form>
						 </tr>
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("payMasterDataCheckList")?exists) && (reportDetailsMap.get("payMasterDataCheckList") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("payMasterDataCheckList"))?exists))>
						 <tr class="alternate-row">
							<form id="payMasterDataAllCheckList" name="payMasterDataAllCheckList" mothed="post" action="<@ofbizUrl>payMasterDataCheckList</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="20%"><span class='h3'>pay Master Data All Chcek List<input  type="hidden"  value="payMasterDataAllCheckList"   name="reportTypeFlag"/></span></td>
										<td><input type="hidden" name="partyId" class="commonPartyId"></td>
										<td width="20%"><span class='h3'>From Date</span><input  type="text" size="18pt" id="payMasterDataAllfromDate"   name="payMasterDataAllfromDate"/></td>
										<td width="20%"><span class='h3'>Thru Date</span><input  type="text" size="18pt" id="payMasterDataAllthruDate"  name="payMasterDataAllthruDate"/></td>
										<td width="20%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
									</tr>
								</table>
							</form>
						 </tr>
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("AttendanceExceptionReportTxt")?exists) && (reportDetailsMap.get("AttendanceExceptionReportTxt") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("AttendanceExceptionReportTxt"))?exists))>
						 <tr class="alternate-row">
							<form id="AttendanceExceptionReport" name="AttendanceExceptionReport" mothed="post" action="<@ofbizUrl>AttendanceExceptionReportTxt</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="20%"><span class='h3'>Attendance Exception Report</span></td>
										<td><input type="hidden" name="partyId" class="commonPartyId"></td>
										<td width="50%"><span class='h3'>Period Id</span>
											<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
												<#list customTimePeriodList as customTimePeriod>
													 <#if defaultTimePeriodId?exists && (defaultTimePeriodId == customTimePeriod.customTimePeriodId)>
								      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								      					<#else>
								      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								                  		</option>
								      				</#if>
												</#list>
											</select>
										</td>
										<td width="20%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
									</tr>
								</table>
							</form>
						 </tr>
					</#if>
					<#if (((reportDetailsMap?has_content) && (reportDetailsMap.get("SalaryExceptionReportTxt")?exists) && (reportDetailsMap.get("SalaryExceptionReportTxt") == "Y")) || (!(reportDetailsMap?has_content))  || (!(reportDetailsMap.get("SalaryExceptionReportTxt"))?exists))>
						 <tr class="alternate-row">
							<form id="SalaryExceptionReport" name="SalaryExceptionReport" mothed="post" action="<@ofbizUrl>SalaryExceptionReportTxt</@ofbizUrl>" target="_blank">
								<table class="basic-table" cellspacing="5">
									<tr class="alternate-row">
										<td width="20%"><span class='h3'>Salary Exception Report</span></td>
										<td><input type="hidden" name="partyId" class="commonPartyId"></td>
										<td width="50%"><span class='h3'>Period Id</span>
											<select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
												<#list customTimePeriodList as customTimePeriod>
													 <#if defaultTimePeriodId?exists && (defaultTimePeriodId == customTimePeriod.customTimePeriodId)>
								      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								      					<#else>
								      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
								                  		</option>
								      				</#if>
												</#list>
											</select>
										</td>
										<td width="20%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td> 
									</tr>
								</table>
							</form>
						 </tr>
					</#if>
				</table>
			</div>
		</div>
	</div>
</#if>

 