<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	


<script type="text/javascript">
function makeDatePicker(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			numberOfMonths: 1,
			maxDate:fromDateId,
			onSelect: function( selectedDate ) {
				$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
			}
		});
			}
			
	$(document).ready(function(){
		makeDatePicker("UnitWiseMilkReceiptFromDate","thruDate");
		makeDatePicker("UnitWiseMilkReceiptThruDate","thruDate");
		makeDatePicker("UnionFromDate","thruDate");
		makeDatePicker("UnionThruDate","thruDate");
		makeDatePicker("UnionReportFromDate","thruDate");
		makeDatePicker("UnionReportThruDate","thruDate");
		makeDatePicker("MilkReceiptsFromDate","thruDate");
		makeDatePicker("MilkReceiptsThruDate","thruDate");
		makeDatePicker("MkrsFromDate","thruDate");
		makeDatePicker("MkrsThruDate","thruDate");
		makeDatePicker("MilkReceiptWeeklyAnalysisFromDate","thruDate");
		makeDatePicker("MilkReceiptWeeklyAnalysisThruDate","thruDate");
		makeDatePicker("RequiredShedwiseAbstractFromDate","thruDate");
		makeDatePicker("RequiredShedwiseAbstractThruDate","thruDate");
		makeDatePicker("QualityQuantityReportFromDate","thruDate");
		makeDatePicker("QualityQuantityReportThruDate","thruDate");		
		makeDatePicker("FederationFromDate","thruDate");
		makeDatePicker("FederationThruDate","thruDate");
		makeDatePicker("FederationbudgetFromDate","thruDate");
		makeDatePicker("FederationbudgetThruDate","thruDate");
		makeDatePicker("MilkReceiptChecklistFromDate","thruDate");
		makeDatePicker("MilkReceiptChecklistThruDate","thruDate");	
		makeDatePicker("ErrorListFromDate","thruDate");	
		makeDatePicker("AbstractforUnitWiseMilkReceiptFromDate","thruDate");
		makeDatePicker("AbstractforUnitWiseMilkReceiptThruDate","thruDate");
		makeDatePicker("DayWiseAbstractFromDate","thruDate");
		makeDatePicker("DayWiseAbstractThruDate","thruDate");		
		makeDatePicker("shedUnitMilkReceiptsFromDate","thruDate");
		makeDatePicker("shedUnitMilkReceiptsThruDate","thruDate");
		makeDatePicker("day-WiseShed-WiseMilkReceiptsFromDate","thruDate");
		makeDatePicker("day-WiseShed-WiseMilkReceiptsThruDate","thruDate");
		makeDatePicker("MilkReceiptsFairListAbstractFromDate","thruDate");
		makeDatePicker("MilkReceiptsFairListAbstractThruDate","thruDate");
		makeDatePicker("unitMonthWiseReceiptAccountFromDate","thruDate");
		makeDatePicker("unitMonthWiseReceiptAccountThruDate","thruDate");
		makeDatePicker("MonthWiseShedWiseMilkReceiptsFromDate","thruDate");
		makeDatePicker("MonthWiseShedWiseMilkReceiptsThruDate","thruDate");
		makeDatePicker("MonthWiseShedWiseKgFatAccountFromDate","thruDate");
		makeDatePicker("MonthWiseShedWiseKgFatAccountThruDate","thruDate");
		makeDatePicker("DayWiseUnitWiseMilkReceiptFromDate","thruDate");
		makeDatePicker("DayWiseUnitWiseMilkReceiptThruDate","thruDate");
		makeDatePicker("MonthWiseUnitWiseMilkReceiptFromDate","thruDate");
		makeDatePicker("MonthWiseUnitWiseMilkReceiptThruDate","thruDate");
		makeDatePicker("acknowledgementDetailReportFromDate","thruDate");
		makeDatePicker("acknowledgementDetailReportThruDate","thruDate");
		makeDatePicker("MonthShedWiseComparingStatmentFromDate","thruDate");
		makeDatePicker("MonthShedWiseComparingStatmentThruDate","thruDate");
		makeDatePicker("YearWiseMilkReceiptsAnalysisFromDate","thruDate");
		makeDatePicker("YearWiseMilkReceiptsAnalysisThruDate","thruDate");
		makeDatePicker("comparativeStatementOfMilkReceiptsFromDate","thruDate");
		makeDatePicker("comparativeStatementOfMilkReceiptsThruDate","thruDate");
		makeDatePicker("ShedwiseUnitwiseCapacityUtilizationFromDate","thruDate");
		makeDatePicker("ShedwiseUnitwiseCapacityUtilizationThruDate","thruDate");
		makeDatePicker("UnionsPrivateFromDate","thruDate");
		makeDatePicker("UnionsPrivateThruDate","thruDate");
		makeDatePicker("finalizeMilkReceiptsFromDate","thruDate");
		$('#ui-datepicker-div').css('clip', 'auto');		
	});			
function appendParams(formName, action) {
		var formId = "#" + formName;
			jQuery(formId).attr("action", action);
			jQuery(formId).submit();
	}
</script>
<#if reportFrequencyFlag =="UnitMilkReceiptReports">
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Receipt Reports</h3>
    </div>
    <div class="screenlet-body">
        <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">   
      	   	<tr class="alternate-row"> 
	      		<form id="QualityQuantityReport" name="QualityQuantityReport" method="post" action="<@ofbizUrl>QualityQuantityReport.txt</@ofbizUrl>">
		    		<td>Quality Quantity between Dispatches and Ack</td>
		   			<td><input type="hidden" name="shedId" value=""/>
		   		 		<input type="hidden" name="unitId" value=""/> 
		        		From<input  type="text" size="20pt" id="QualityQuantityReportFromDate" name="fromDate"/>
			        	To<input  type="text" size="20pt" id="QualityQuantityReportThruDate" name="thruDate"/>  
		        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
		    		</td>   
		 		</form>
		 	</tr>	 
         </table>
       </div>
    </div>
</#if>

<#if reportFrequencyFlag =="ShedMilkReceiptReports">
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Receipt Reports</h3>
    </div>
    <div class="screenlet-body">
        <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;"> 
        <tr> 
	      	<form id="finalizeMilkReceipts" name="finalizeMilkReceipts" method="post" action="<@ofbizUrl>MilkReceiptSummerySms.pdf</@ofbizUrl>">
		    	<td>Finalize MilkReceipts</td>
		   		<td> 
		        	Date <input  type="text" size="15pt" id="finalizeMilkReceiptsFromDate" name="fromDate"/>
		        	<input type="submit" formtarget="_blank" value="SMS" id="button1" class="buttontext"/>
		    	</td>   
			</form>
		</tr>
        <tr class="alternate-row"> 
			<form id="UnitWiseMilkReceipt" name="UnitWiseMilkReceipt" method="post" action="<@ofbizUrl>UnitWiseMilkReceipt.txt</@ofbizUrl>">
			    <td>Unit wise Milk Receipt</td>
			   	<td><input type="hidden" name="shedId" value=""/>
		   		 	<input type="hidden" name="unitId" value=""/> 
		        	From<input  type="text" size="15pt" id="UnitWiseMilkReceiptFromDate" name="fromDate"/>
		        	To<input  type="text" size="15pt" id="UnitWiseMilkReceiptThruDate" name="thruDate"/>                           
		        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
			    </td>   
			</form>
		</tr>
         <tr class="alternate-row"> 
				<form id="AbstractforUnitWiseMilkReceipt" name="AbstractforUnitWiseMilkReceipt" method="post" action="<@ofbizUrl>AbstractforUnitWiseMilkReceipt.txt</@ofbizUrl>">
			    	<td> <span class='h3'>Abstract for Unit wise Milk Receipt</span></td>
                    <td><input type="hidden" name="shedId" value=""/> 
                        <input type="hidden" name="unitId" value=""/>
                      <table>
	                  	<tr>
	                    	From<input  type="text" size="15pt" id="AbstractforUnitWiseMilkReceiptFromDate" name="fromDate"/>
				        	To<input  type="text" size="15pt" id="AbstractforUnitWiseMilkReceiptThruDate" name="thruDate"/>
				        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
	                   	</tr>
                      </table>
                  </td>   
			 </form>     
	    </tr> 
      	<tr> 
	      	<form id="MilkReceiptChecklist" name="MilkReceiptChecklist" method="post" action="<@ofbizUrl>MilkReceiptChecklist.pdf</@ofbizUrl>">
		    	<td>Milk Receipt Checklist</td>
		   		<td><input type="hidden" name="shedId" value=""/>
		   		 	<input type="hidden" name="unitId" value=""/> 
		        	Date<input  type="text" size="15pt" id="MilkReceiptChecklistFromDate" name="fromDate"/>
		        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
		    	</td>   
		 	</form>
		 </tr>	
	  	<tr class="alternate-row"> 
			<form id="MilkReceiptsFairListAbstract" name="MilkReceiptsFairListAbstract" method="post" action="<@ofbizUrl>MilkReceiptsFairListAbstract.txt</@ofbizUrl>">
		    	<td>Milk Receipts FairList & Abstract</td>
		   		<td><input type="hidden" name="shedId" value=""/>
		   		 	<input type="hidden" name="unitId" value=""/> 
		        	Date<input  type="text" size="15pt" id="MilkReceiptsFairListAbstractFromDate" name="fromDate"/>
		        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
		    	</td>   
		 	</form>
		 </tr>
		 <tr> 
		 	<form id="MilkReceiptErrorList" name="MilkReceiptErrorList" method="post" action="<@ofbizUrl>MilkReceiptErrorList.pdf</@ofbizUrl>">
			    <td>Milk Receipt Error List</td>
			    <td>Date<input  type="text" size="15pt" id="ErrorListFromDate" name="fromDate"/>
			    <input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
			    </td>   
			</form>
		</tr>
		 <tr class="alternate-row"> 
				  <form id="DayWiseAbstract" name="DayWiseAbstract" method="post" action="<@ofbizUrl>DayWiseAbstract.txt</@ofbizUrl>">
			    	<td>Day-Wise Abstract</td>
			   		<td><input type="hidden" name="shedId" value=""/>
			   		 	<input type="hidden" name="unitId" value=""/> 
			        	From<input  type="text" size="15pt" id="DayWiseAbstractFromDate" name="fromDate"/>
			        	To<input  type="text" size="15pt" id="DayWiseAbstractThruDate" name="thruDate"/>                           
			        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
			    	</td>   
			 	 </form>
			   </tr>
			   <tr class="alternate-row"> 
	      		<form id="RequiredShedwiseAbstract" name="RequiredShedwiseAbstract" method="post" action="<@ofbizUrl>RequiredShedwiseAbstract.txt</@ofbizUrl>">
		    		<td>Required Shed-wise Abstract</td>
		   			<td><input type="hidden" name="shedId" value=""/>
		   		 		<input type="hidden" name="unitId" value=""/> 
		        		From<input  type="text" size="15pt" id="RequiredShedwiseAbstractFromDate" name="fromDate"/>
			        	To<input  type="text" size="15pt" id="RequiredShedwiseAbstractThruDate" name="thruDate"/>  
		        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
		    		</td>   
		 		</form>
		 		</tr>
		 		<tr class="alternate-row"> 
	      		<form id="MilkReceiptWeeklyAnalysis" name="MilkReceiptWeeklyAnalysis" method="post" action="<@ofbizUrl>MilkReceiptWeeklyAnalysis.txt</@ofbizUrl>">
		    		<td>Milk Receipt Weekly Analysis</td>
		   			<td><input type="hidden" name="shedId" value=""/>
		   		 		<input type="hidden" name="unitId" value=""/> 
		        		From<input  type="text" size="15pt" id="MilkReceiptWeeklyAnalysisFromDate" name="fromDate"/>
			        	To<input  type="text" size="15pt" id="MilkReceiptWeeklyAnalysisThruDate" name="thruDate"/>  
		        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
		    		</td>   
		 		</form>
		 		</tr>
        	   	<tr class="alternate-row"> 
				  <form id="day-WiseShed-WiseMilkReceipts" name="day-WiseShed-WiseMilkReceipts" method="post" action="<@ofbizUrl>day-WiseShed-WiseMilkReceipts.txt</@ofbizUrl>">
			    	<td>day-Wise Shed-Wise MilkReceipts</td>
			   		<td><input type="hidden" name="shedId" value=""/>
			   		 	<input type="hidden" name="unitId" value=""/> 
			        	From<input  type="text" size="15pt" id="day-WiseShed-WiseMilkReceiptsFromDate" name="fromDate"/>
			        	To<input  type="text" size="15pt" id="day-WiseShed-WiseMilkReceiptsThruDate" name="thruDate"/>                           
			        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
			    	</td>   
			 	 </form>
			   </tr> 
			   <#--<tr> 
	      		<form id="shedWiseUnitWiseMilkReceipts" name="shedWiseUnitWiseMilkReceipts" method="post" action="<@ofbizUrl>shedWiseUnitWiseMilkReceipts.txt</@ofbizUrl>">
		    	<td>ShedWise UnitWise MilkReceipts</td>
		   		<td><input type="hidden" name="shedId" value=""/>
		   		 	<input type="hidden" name="unitId" value=""/> 
		        	From<input  type="text" size="15pt" id="shedUnitMilkReceiptsFromDate" name="fromDate"/>
		        	To<input  type="text" size="15pt" id="shedUnitMilkReceiptsThruDate" name="thruDate"/>
		        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
		    	</td>   
			</form>
		</tr>-->
		 <tr class="alternate-row"> 
			<form id="MonthWiseShedWiseMilkReceipts" name="MonthWiseShedWiseMilkReceipts" method="post" action="<@ofbizUrl>MonthWiseShedWiseMilkReceipts.txt</@ofbizUrl>">
		    	<td>Month-Wise Shed-Wise Milk Receipts</td>
		   		<td><input type="hidden" name="shedId" value=""/>
		   		 	<input type="hidden" name="unitId" value=""/> 
		        	From<input  type="text" size="15pt" id="MonthWiseShedWiseMilkReceiptsFromDate" name="fromDate"/>
		        	To<input  type="text" size="15pt" id="MonthWiseShedWiseMilkReceiptsThruDate" name="thruDate"/>
		        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
		    	</td>   
		 	</form>
	 	</tr>
	 	<tr class="alternate-row"> 
				  <form id="ShedwiseUnitwiseCapacityUtilization" name="ShedwiseUnitwiseCapacityUtilization" method="post" action="<@ofbizUrl>ShedwiseUnitwiseCapacityUtilization.txt</@ofbizUrl>">
			    	<td>Shed-wise Unit-wise Capacity Utilization</td>
			   		<td><input type="hidden" name="shedId" value=""/>
			   		 	<input type="hidden" name="unitId" value=""/> 
			        	From<input  type="text" size="15pt" id="ShedwiseUnitwiseCapacityUtilizationFromDate" name="fromDate"/>
			        	To<input  type="text" size="15pt" id="ShedwiseUnitwiseCapacityUtilizationThruDate" name="thruDate"/>                           
			        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
			    	</td>   
			 	 </form>
			   </tr> 
	 	 <tr class="alternate-row"> 
			<form id="MonthWiseShedWiseKgFatAccount" name="MonthWiseShedWiseKgFatAccount" method="post" action="<@ofbizUrl>MonthWiseShedWiseKgFatAccount.txt</@ofbizUrl>">
		    	<td>Month-Wise Shed-Wise KgFat Account</td>
		   		<td><input type="hidden" name="shedId" value=""/>
		   		 	<input type="hidden" name="unitId" value=""/> 
		        	From<input  type="text" size="15pt" id="MonthWiseShedWiseKgFatAccountFromDate" name="fromDate"/>
		        	To<input  type="text" size="15pt" id="MonthWiseShedWiseKgFatAccountThruDate" name="thruDate"/>
		        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
		    	</td>   
		 	</form>
	 	</tr> 	 
		 <tr class="alternate-row"> 
			  <form id="DayWiseUnitWiseMilkReceipt" name="DayWiseUnitWiseMilkReceipt" method="post" action="<@ofbizUrl>DayWiseUnitWiseMilkReceipt.txt</@ofbizUrl>">
		    	<td>Day-Wise,Unit-Wise Milk Receipts</td>
		   		<td><input type="hidden" name="shedId" value=""/>
		   		 	<input type="hidden" name="unitId" value=""/> 
		        	From<input  type="text" size="15pt" id="DayWiseUnitWiseMilkReceiptFromDate" name="fromDate"/>
		        	To<input  type="text" size="15pt" id="DayWiseUnitWiseMilkReceiptThruDate" name="thruDate"/>                           
		        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
		    	</td>   
		 	 </form>
		 </tr> 
		 <tr class="alternate-row"> 
		 	<form id="DayWiseMpf" name="DayWiseMpf" method="post" action="<@ofbizUrl>DayWiseMpf.txt</@ofbizUrl>">
	    		<td>Day Wise MPF MilkReceipts</td>
	   			<td><input type="hidden" name="shedId" value=""/>
	   		 		<input type="hidden" name="unitId" value=""/> 
	        		From<input  type="text" size="15pt" id="MilkReceiptsFromDate" name="fromDate"/>
	        		To<input  type="text" size="15pt" id="MilkReceiptsThruDate" name="thruDate"/>                           
	        		<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
	    		</td>   
			</form>
		</tr> 
		 <tr> 
	      	<form id="FederationUnitsMilkRcptsValue" name="FederationUnitsMilkRcptsValue" method="post" action="<@ofbizUrl>FederationUnitsMlkRcptsValue.txt</@ofbizUrl>">
		    	<td>Federation Units Milk Receipts Value From Shed</td>
		   		<td>
		        	From<input  type="text" size="15pt" id="FederationFromDate" name="fromDate"/>
		        	To<input  type="text" size="15pt" id="FederationThruDate" name="thruDate"/>
		        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
		    	</td>   
		 	</form>
		 </tr>
		 <tr> 
	      	<form id="FederationBudgetSupport" name="FederationBudgetSupport" method="post" action="<@ofbizUrl>FederationBudgetSupport.txt</@ofbizUrl>">
		    	<td>Federation Budget Support</td>
		   		<td>
		        	From<input  type="text" size="15pt" id="FederationbudgetFromDate" name="fromDate"/>
		        	To<input  type="text" size="15pt" id="FederationbudgetThruDate" name="thruDate"/>
		        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
		    	</td>   
		 	</form>
		 </tr>
		 <tr class="alternate-row"> 
			  <form id="MonthWiseUnitWiseMilkReceipt" name="MonthWiseUnitWiseMilkReceipt" method="post" action="<@ofbizUrl>MonthWiseUnitWiseMilkReceipt.txt</@ofbizUrl>">
		    	<td>Month-Wise,Unit-Wise Milk Receipts</td>
		   		<td><input type="hidden" name="shedId" value=""/>
		   		 	<input type="hidden" name="unitId" value=""/> 
		        	From<input  type="text" size="20pt" id="MonthWiseUnitWiseMilkReceiptFromDate" name="fromDate"/>
		        	To<input  type="text" size="20pt" id="MonthWiseUnitWiseMilkReceiptThruDate" name="thruDate"/>                           
		        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
		    	</td>   
		 	 </form>
		 </tr>  		 		 
		</table>
       </div>
    </div>


<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>FOXPRO DBF REPORT</h3>
    </div>
    <div class="screenlet-body">
        <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
        <tr class="alternate-row">
      		<form id="MilkReceiptEntriesCsv" name="MilkReceiptEntriesCsv" method="post" action="<@ofbizUrl>MKRS1.DBF</@ofbizUrl>">
      			<td> MilkReceipt Entries </td>
      			<td >
					From Date <input  type="text" size="15pt" id="MkrsFromDate" name="fromDate"/>
					Thru Date <input  type="text" size="15pt" id="MkrsThruDate" name="thruDate"/>
					
      				<input type="submit" value="Download Dbf" onClick="javascript:appendParams('MilkReceiptEntriesCsv', '<@ofbizUrl>MKRS1.DBF</@ofbizUrl>');" class="buttontext"/>
					<#if security.hasEntityPermission("CSV", "_VIEW", session)><input type="submit" value="CSV" onClick="javascript:appendParams('MilkReceiptEntriesCsv', '<@ofbizUrl>MKRS1.csv</@ofbizUrl>');" class="buttontext"/> </#if>
          		</td>
          	</form>  	   
         </tr>   
		</table>
    </div>
    </div>

</#if>

 <#if reportFrequencyFlag =="AnnualMilkReceiptReports">
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Receipt Reports</h3>
    </div>
    <div class="screenlet-body">
        <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">   
      		 <tr class="alternate-row"> 
				  <form id="UnitWiseMonthWiseMilkReceiptsAccount" name="UnitWiseMonthWiseMilkReceiptsAccount" method="post" action="<@ofbizUrl>unitMonthWiseReceiptAccount.txt</@ofbizUrl>">
			    	<td>Unit-Wise,Month-Wise MilkReceipts Account</td>
			   		<td>
			   		 	<input type="hidden" name="unitId" value=""/> 
			        	From<input  type="text" size="20pt" id="unitMonthWiseReceiptAccountFromDate" name="fromDate"/>
			        	To<input  type="text" size="20pt" id="unitMonthWiseReceiptAccountThruDate" name="thruDate"/>                           
			        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
			    	</td>   
			 	 </form>
		   </tr>
		   <tr class="alternate-row">
		   <form id="ComparativeStatementOfMilkReceipts" name="ComparativeStatementOfMilkReceipts" method="post" action="<@ofbizUrl>comparativeStatementOfMilkReceipts.txt</@ofbizUrl>">
		   	<td>Comparative Statement of M.P.F Milk Receipts</td>
		   	<td>From<input type="text" size="20pt" id="comparativeStatementOfMilkReceiptsFromDate" name="fromDate"/>
		   		To<input type="text" size="20pt" id="comparativeStatementOfMilkReceiptsThruDate" name="thruDate"/>
		   		<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
		   		</td>   
		   </form>
		   </tr>
		   <tr class="alternate-row"> 
				  <form id="MonthWiseShedWiseMilkReceiptsComparingStatment" name="MonthWiseShedWiseMilkReceiptsComparingStatment" method="post" action="<@ofbizUrl>MonthShedWiseComparingStatment.txt</@ofbizUrl>">
			    	<td>MonthWise ShedWise MilkReceipts Comparing Statment</td>
			   		<td>
			   		 	<input type="hidden" name="shedId" value=""/>
			   		 	<input type="hidden" name="unitId" value=""/> 
			        	From<input  type="text" size="20pt" id="MonthShedWiseComparingStatmentFromDate" name="fromDate"/>
			        	To<input  type="text" size="20pt" id="MonthShedWiseComparingStatmentThruDate" name="thruDate"/>                           
			        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
			    	</td>   
			 	 </form>
		   </tr>
		   <tr class="alternate-row"> 
			  <form id="YearWiseMilkReceiptsAnalysis" name="YearWiseMilkReceiptsAnalysis" method="post" action="<@ofbizUrl>YearWiseMilkReceiptsAnalysis.txt</@ofbizUrl>">
		    	<td>Year-Wise Milk Receipts Analysis</td>
		   		<td>
		   		 	<input type="hidden" name="unitId" value=""/> 
		        	From<input  type="text" size="10pt" name="fromDate"/>
		        	To<input  type="text" size="10pt"  name="thruDate"/>                           
		        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
		    	</td>   
		 	 </form>
		   </tr>
		   
		</table>
    </div>
    </div>
</#if>
<#if reportFrequencyFlag =="BillingReports">
	<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Receipt Reports</h3>
    </div>
    <div class="screenlet-body">
        <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">   
      	<tr> 
      		<form id="acknowledgementDetailReport" name="acknowledgementDetailReport" method="post" action="<@ofbizUrl>acknowledgementDetailReport.txt</@ofbizUrl>">	
      			<td>Unions/Private Unit Milk Bills </td>
      			<td><input type="hidden" name="shedId" value=""/>
      				<input type="hidden" name="unitId" value=""/>
				 	Product	     			
			    	<select name="productId" class='h4'>
			     		<#list milkReceiptProductList as product>    
	  	    				<option value='${product.productId}'>${product.brandName}</option>
						</#list>	        
				    </select>
	             	From<input  type="text" size="15pt" id="acknowledgementDetailReportFromDate" name="fromDate"/>
	    			To<input  type="text" size="15pt" id="acknowledgementDetailReportThruDate" name="thruDate"/>
	    			<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
      			</td>
      		</form>
      	   </tr>	
		<tr> 
	      	<form id="UnionOrPrivateDairyAbst" name="UnionOrPrivateDairyAbst" method="post" action="<@ofbizUrl>unionOrPrivateDairyAbst.txt</@ofbizUrl>">
		    	<td>Unions/Private Units Milk Bills  Abstract</td>
		   		<td>
		        	From<input  type="text" size="15pt" id="UnionFromDate" name="fromDate"/>
		        	To<input  type="text" size="15pt" id="UnionThruDate" name="thruDate"/>
		        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
		    	</td>   
		 	</form>
		 </tr>
		 <tr> 
	      	<form id="UnionOrPrivateDairyAbstSupportingReport" name="UnionOrPrivateDairyAbstSupportReport" method="post" action="<@ofbizUrl>UnionOrPrivateDairyAbstSupport.txt</@ofbizUrl>">
		    	<td>Unions/Private Units Milk Bills  Abstract Support</td>
		   		<td>
		        	From<input  type="text" size="15pt" id="UnionReportFromDate" name="fromDate"/>
		        	To<input  type="text" size="15pt" id="UnionReportThruDate" name="thruDate"/>
		        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
		    	</td>   
		 	</form>
		 </tr>	 
		 <tr> 
	      	<form id="shedWiseUnitWiseMilkReceipts" name="shedWiseUnitWiseMilkReceipts" method="post" action="<@ofbizUrl>shedWiseUnitWiseMilkReceipts.txt</@ofbizUrl>">
		    	<td>ShedWise UnitWise MilkReceipts</td>
		   		<td><input type="hidden" name="shedId" value=""/>
		   		 	<input type="hidden" name="unitId" value=""/> 
		        	From<input  type="text" size="15pt" id="shedUnitMilkReceiptsFromDate" name="fromDate"/>
		        	To<input  type="text" size="15pt" id="shedUnitMilkReceiptsThruDate" name="thruDate"/>
		        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
		    	</td>   
			</form>
		</tr>
		<tr> 
	      	<form id="UnionOrPrivateAvgRatePerLtrKgFat" name="UnionOrPrivateAvgRatePerLtrKgFat" method="post" action="<@ofbizUrl>UnionOrPrivateAvgRatePerLtrKgFat.txt</@ofbizUrl>">
		    	<td>Unions/Private Avg.Rate Per Ltr/Kg Fat</td>
		   		<td>
		        	From<input  type="text" size="15pt" id="UnionsPrivateFromDate" name="fromDate"/>
		        	To<input  type="text" size="15pt" id="UnionsPrivateThruDate" name="thruDate"/>
		        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
		    	</td>   
		 	</form>
		 </tr>
		</table>
    </div>
    </div>
	
</#if>
 
   
    <script type="text/javascript">
	<#if !unitId?has_content>
		<#if shedId?has_content>
			<#if !(unitMapsList?has_content)>
				setShedUnitsDropDown(shedId);
			</#if>
			getShedFinAccounts(shedId);
			getUnitRouteByValue($('[name=unitId]').val());
			getTimePeriodsByUnit($('[name=shedId]').val(),$('[name=unitId]').val());
			$('[name=shedId]').val($('[name=shedId]').val());
			$('[name=unitId]').val($('[name=unitId]').val());
			
		</#if>
	<#else>
		<#if unitId?has_content>
			getUnitRouteByValue(${unitId});
			getTimePeriodsByUnit($('[name=shedId]').val(),${unitId});
			$('[name=shedId]').val($('[name=shedId]').val());
			$('[name=unitId]').val($('[name=unitId]').val());
		</#if>
	</#if>
	</script> 
