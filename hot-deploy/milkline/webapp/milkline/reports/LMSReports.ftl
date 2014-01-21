<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	

<script type="application/javascript">
    
	<#if (showBoothAutoSuggest?has_content) && (showBoothAutoSuggest != 'N')>
			var routeBoothsData = ${StringUtil.wrapString(facilityItemsJSON)}
			var boothsList ;
			function setRouteBoothsDropDown(selection){	
				boothsList = routeBoothsData[selection.value]; 
			}	
			<#if routeId?exists && routeId?has_content> 
				boothsList = routeBoothsData["${routeId}"]; 
			</#if>
			$(function() {
				$('#boothId').keypress(function (e) { 
					$("#boothId").autocomplete({ source: boothsList });	
				});
			});
	</#if>	
	
</script>		
<script type="text/javascript">
function makeDatePicker(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
			}
		});
			}

	$(document).ready(function(){
		makeDatePicker("centerFromDate","thruDate");
		makeDatePicker("centerThruDate","thruDate");
		makeDatePicker("selectFromDate","thruDate");
        makeDatePicker("selectThruDate","thruDate");
       
		$('#ui-datepicker-div').css('clip', 'auto');		
	});

</script>
<div class="lefthalf">
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Daily reports for ${reportDateStr}</h3>
    </div>
    <div class="screenlet-body">
      <table class="basic-table hover-bar h3" style="border-spacing: 0 15px;">
      	<tr>
      		<td> Changes CSV For FoxPro</td>
      		<td> <a href="<@ofbizUrl>changesReportCsv.csv</@ofbizUrl>" class="buttontext">Download</a></td>
      	</tr>
      	<tr class="alternate-row">
      		<td> Net Sales </td>
      		<td> <a href="<@ofbizUrl>NetSalesReport.txt</@ofbizUrl>" class="buttontext">Download</a></td>
      	</tr>
      	
      	<tr>
      	   <form id="ReconsilationReport" name="ReconsilationReport" method="post"  target="_blank" action="<@ofbizUrl>ReconsilationReport.pdf</@ofbizUrl>">	
      			<td>Reconsilation Report</td>
      			<td align="center">Indent Date<@htmlTemplate.renderDateTimeField name="indentDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd" size="5" maxlength="15" id="indentDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      				
      				<input type="submit" value="Download" class="buttontext"/>
      			</td>
      		</form>
      	</tr>
      	
      	<!--<tr>
      		<form id="ValueSheetTotals" name="ValueSheetTotals" method="post" action="<@ofbizUrl>ValueSheetTotals.txt</@ofbizUrl>">
      			<td> Value Sheet Totals </td>
      			<td >Route
      				<select name="facilityId" class='h4'>
      				    <option value=""></option> 
                		<#list facilityList as facility> 
                		    <option value='${facility.facilityId}'>
	                    		${facility.facilityId}
	                  		</option>
                		</#list>            
					</select>
					<input type="submit" value="Download" class="buttontext"/>
          		</td>
          	</form>
      	</tr>--> 
      	<!--<tr>
      		<form id="VehicleShortPaymentReport" name="VehicleShortPaymentReport" method="post" action="<@ofbizUrl>VehicleShortPaymentReport.txt</@ofbizUrl>">	
      			<td >Vehicle Short Payment</td>
      			<td>Payment Date<@htmlTemplate.renderDateTimeField name="paymentDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="22" maxlength="25" id="shotPaymentDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/><input type="submit" value="Download" class="buttontext"/></td>
      		</form>
      	</tr>-->
      	<tr>
      		<form id="ConsolidatedRouteCollectionSheet" name="ConsolidatedRouteCollectionSheet" method="post"  target="_blank" action="<@ofbizUrl>consolidatedRouteCollection.pdf?reportTypeFlag=consolidatedRouteCollection</@ofbizUrl>">	
      			<td>Consolidated Route Collection Sheet</td>
      			<td>Route
      				<select name="facilityId" class='h4' allow-empty="true">
      					<option value="All-Routes">All-Routes</option> 
                		<#list facilityList as facility> 
                			<option value='${facility.facilityId}'>
	                    		${facility.facilityId}
	                  		</option>
                		</#list> 
	         		</select>				
      				Supply Date<@htmlTemplate.renderDateTimeField name="supplyDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd" size="5" maxlength="15" id="supplyDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      				
      				<input type="submit" value="Download" class="buttontext"/>
      			</td>
      		</form>
      	</tr>
      	<tr class="alternate-row">
      		<form id="DailySalesAndCollectionReport" name="DailySalesAndCollectionReport" method="post"  target="_blank" action="<@ofbizUrl>DailySalesAndCollectionReport</@ofbizUrl>">	
      			<td>Daily/Monthly Sales And Collection Stmt</td>
      			<td>Route
      				<select name="facilityId" class='h4' allow-empty="true">
      					<option value="All-Routes">All-Routes</option> 
                		<#list facilityList as facility> 
                			<option value='${facility.facilityId}'>
	                    		${facility.facilityId}
	                  		</option>
                		</#list> 
	         		</select>
	         		<input type="hidden" name="reportNameFlag" value="DailySalesAndCollectionReport" />				
      				 From<@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd" size="5" maxlength="15" id="saleFromDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      				 To<@htmlTemplate.renderDateTimeField name="thruDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd" size="5" maxlength="15" id="saleThruDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      				<input type="submit" value="Download" class="buttontext"/>
      			</td>
      		</form>
      	</tr>
      	<tr>
      		<form id="AgentwiseSalesReport" name="AgentwiseSalesReport" method="post" action="<@ofbizUrl>agentWiseSales.txt</@ofbizUrl>">	
      			
      			<td>AgentWise Sales&Dues</td>
      			<td>Route
      				<select name="facilityId" class='h4' allow-empty="true">
      					<option value=""></option> 
                		<#list facilityList as facility> 
                			<option value='${facility.facilityId}'>
	                    		${facility.facilityId}
	                  		</option>
                		</#list> 
	         		</select>				
      				From<@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd" size="5" maxlength="15" id="agentFromDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      				To<@htmlTemplate.renderDateTimeField name="thruDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd" size="5" maxlength="15" id="agentThruDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      				<input type="submit" value="Download" class="buttontext"/>
      			</td>
      		</form>
      	</tr>      	
      	<tr class="alternate-row">
      		<form id="DayWiseSalesAndCollection" name="DayWiseSalesAndCollectionReport" target="_blank" method="post" action="<@ofbizUrl>DayWiseSalesAndCollection.pdf</@ofbizUrl>">	
      			<td>DayWiseSalesAndCollection</td>
      			<td>Route
      				<select name="facilityId" class='h4' allow-empty="true">
      					<option value=""></option> 
                		<#list facilityList as facility> 
                			<option value='${facility.facilityId}'>
	                    		${facility.facilityId}		
	                  		</option>
                		</#list> 
	         		</select>				
      				From<@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd" size="5" maxlength="15" id="saleDayFromDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      				To<@htmlTemplate.renderDateTimeField name="thruDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd" size="5" maxlength="15" id="saleDayThruDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      				<input type="submit" value="Download" class="buttontext"/>
      			</td>
      		</form>
      	</tr>
      	<tr>
      		<form id="AgentwiseSaleVariation" name="AgentwiseSaleVariation" method="post" action="<@ofbizUrl>agentSalesVariation.txt</@ofbizUrl>">	
      			<td>Agent Wise Sales Variation</td>
      			<td>Route
      				<select name="facilityId" class='h4' allow-empty="true">
      					<option value=""></option> 
                		<#list facilityList as facility> 
                			<option value='${facility.facilityId}'>
	                    		${facility.facilityId}
	                  		</option>
                		</#list> 
	         		</select>				
      				From<@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd" size="5" maxlength="15" id="agentSaleFromDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      				To<@htmlTemplate.renderDateTimeField name="thruDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd" size="5" maxlength="15" id="agentSaleThruDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
						<input type="submit" formtarget="_blank" value="Download" class="buttontext"/>
      				
      			</td>
      		</form>
      	</tr>
      	<tr class="alternate-row" >
      		<form id="LeaksFreesReport" name="LeaksFreesReport" method="post"  target="_blank" action="<@ofbizUrl>LeaksFreesReport.pdf</@ofbizUrl>">	
      			<td >Leaks/Frees/Samples..Spoilages Report</td>
      			<td>Route
      				<select name="facilityId" class='h4' allow-empty="true">
      					<option value="All-Routes">All-Routes</option> 
                		<#list facilityList as facility> 
                			<option value='${facility.facilityId}'>
	                    		${facility.facilityId}
	                  		</option>
                		</#list> 
	         		</select>				
      				From<@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd" size="5" maxlength="15" id="leakFromDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      				To<@htmlTemplate.renderDateTimeField name="thruDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd" size="5" maxlength="15" id="leakThruDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
						<input type="submit" formtarget="_blank" value="Download" class="buttontext"/>
      				
      			</td>
      		</form>
      	</tr>
      	 
      	<tr>
    		<form id="RouteWiseOBCBSalesReport" name="RouteWiseOBCBSalesReport" method="post" action="<@ofbizUrl>routeWiseOBCBSales.txt</@ofbizUrl>">	
      			<td>RouteWise OB,CB&amp;SALE</td>
      			<td><#--Route
      				<select name="facilityId" class='h4' allow-empty="true">
      					<option value=""></option> 
                		<#list facilityList as facility> 
                			<option value='${facility.facilityId}'>
	                    			${facility.facilityId}
	                  		</option>
                			</#list> 
	         		</select>-->				
      				From<@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd" size="10" maxlength="15" id="routeFromDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      				To<@htmlTemplate.renderDateTimeField name="thruDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd" size="10" maxlength="15" id="routeThruDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      				<input type="submit" formtarget="_blank" value="Download" class="buttontext"/>
      			</td>
      		</form>
      	</tr>
      	<tr>
    		<form id="cashTransaction" name="cashTransaction" method="post" action="<@ofbizUrl>cashTransaction.txt</@ofbizUrl>">	
      			<td>Cash Transactions</td>
      			<td>From<@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd" size="10" maxlength="15" id="cashFromDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      				To<@htmlTemplate.renderDateTimeField name="thruDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd" size="10" maxlength="15" id="cashThruDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      				<input type="submit" value="Download" class="buttontext"/>
      			</td>
      		</form>
      	</tr>
      	<tr>
    		<form id="RouteWiseMilkSalesParticulars" name="RouteWiseMilkSalesParticulars" method="post" action="<@ofbizUrl>routeWiseMilkSales.txt</@ofbizUrl>">	
      			<td>RouteWise Milk Sales Particulars</td>
      			<td>		
      				From<@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd" size="10" maxlength="15" id="milkFromDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      				To<@htmlTemplate.renderDateTimeField name="thruDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd" size="10" maxlength="15" id="milkThruDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      				<input type="submit" value="Download" class="buttontext"/>
      			</td>
      		</form>
      	</tr>
      </table> 		
 	</div>
</div>

<!--<div class="screenlet">
	<div class="screenlet-title-bar">
      <h3>Past Reports</h3>
	</div>
	<div class="screenlet-body">
		<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
			<tr>
      			<form id="pastPaymentsCsv" name="PastPaymentsCsv" method="post" action="<@ofbizUrl>boothPayments.csv?statusId=PAID</@ofbizUrl>">	
      				<td>Past Payment Report</td>
      				<td>Payment Date<@htmlTemplate.renderDateTimeField name="paymentDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd" size="22" maxlength="25" id="pastPaymentDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/></td>
      				<td><input type="submit" value="Download" class="buttontext"/></td>
      			</form>
      		</tr>
      		<tr>
				<form id="valueSheetTotals" name="valueSheetTotals" method="post" action="<@ofbizUrl>ValueSheetTotals.txt</@ofbizUrl>">	
      		    	<td> ValueSheet-Totals </td>
      		    	<td>Supply Date<@htmlTemplate.renderDateTimeField name="estimatedDeliveryDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="22" maxlength="25" id="estimatedDeliveryDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/></td>
      			    <td >Route
      			    <select name="facilityId" class='h4'>
      				    <option value=""></option> 
                		<#list facilityList as facility> 
                		    <option value='${facility.facilityId}'>
	                    		${facility.facilityId}
	                  		</option>
                		</#list>            
					</select>
				    <input type="submit" value="Download" class="buttontext"/>
				  </td>
      			</form>
        	</tr>
      	</table>		
    </div>  
 </div>-->
 <div class="screenlet">
	<div class="screenlet-title-bar">
      <h3>Monthly Reports</h3>
	</div>
	<div class="screenlet-body">
		<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
      		<tr>
				<form id="agentWiseCommission" name="agentWiseCommission" method="post" action="<@ofbizUrl>agentWiseCommission</@ofbizUrl>">	
      				<td>Agent Wise Commision</td>
      				
      				<td >Route
	      				<select name="facilityId" class='h4' allow-empty="true">
	      					<option value="All-Routes"></option> 
	                		<#list facilityList as facility> 
	                			<option value='${facility.facilityId}'>
		                    			${facility.facilityId}
		                  		</option>
	                			</#list> 
		         		</select>
      				From<input  type="text" size="10pt" id="centerFromDate" name="fromDate"/>
					To<input  type="text" size="10pt" id="centerThruDate" name="thruDate"/>
						<input type="submit" value="Download" class="buttontext"/>
					</td>      			
      			</form>
        	</tr>
      		<tr>
				<form id="summaryReportMonthly" name="summaryReportMonthly" method="post" action="<@ofbizUrl>SummaryReportMonthly.txt</@ofbizUrl>">	
      				<td>Net Sales Summary</td>
      				<td >From Date<@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" value="" className="" alert="" title="Format: dd/MM/yyyy" size="10" maxlength="20" id="NetSalesfromDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
					 	 Thru Date<@htmlTemplate.renderDateTimeField name="thruDate" event="" action="" value="" className="" alert="" title="Format: dd/MM/yyyy" size="10" maxlength="20" id="NetSalesthruDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
						<input type="submit" value="Download" class="buttontext"/>
					</td>      			
      			</form>
        	</tr>
        	
        	 <tr>
        	<form id="CRInstBill" name="CRInstBill" target="_blank" method="post" action="<@ofbizUrl>CRInstBillInvoice.pdf?categoryTypeEnum=CR_INST</@ofbizUrl>">	
      			<td>CreditInst Bill/Invoice</td>
      			<td >From Date<@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" value="" className="" alert="" title="Format: dd/MM/yyyy" size="10" maxlength="20" id="CRInstfromDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
					 Thru Date<@htmlTemplate.renderDateTimeField name="thruDate" event="" action="" value="" className="" alert="" title="Format: dd/MM/yyyy" size="10" maxlength="20" id="CRInstthruDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
					<input type="submit" value="Download" class="buttontext"/>
				</td>
				</tr>
      		</form>	
        </tr>
        <tr class="alternate-row">
      		<form id="AgentTargetAndPerformanceReport" name="AgentTargetAndPerformanceReport" method="post"  target="_blank" action="<@ofbizUrl>AgentTargetAndPerformanceReport.pdf</@ofbizUrl>">	
      			<td>Agent Wise Target and Performance</td>
      			<td>Route
      				<select name="facilityId" class='h4' allow-empty="true">
      					<option value="All-Routes">All-Routes</option> 
                		<#list facilityList as facility> 
                			<option value='${facility.facilityId}'>
	                    		${facility.facilityId}
	                  		</option>
                		</#list> 
	         		</select>				
      				 From<@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd" size="5" maxlength="15" id="targetFromDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      				 To<@htmlTemplate.renderDateTimeField name="thruDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd" size="5" maxlength="15" id="targetThruDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      				<input type="submit" value="Download" class="buttontext"/>
      			</td>
      		</form>
      	</tr>       	  
      	<tr class="alternate-row">
      		<form id="DayWiseRouteSalesReport" name="DayWiseRouteSalesReport" method="post"  target="_blank" action="<@ofbizUrl>DayWiseRouteSalesReport.pdf</@ofbizUrl>">	
      			<td>Day Wise Route Sales And Collection</td>
      				
      			<td>Route
      				<select name="facilityId" class='h4' allow-empty="true">
      					<option value="All-Routes">All-Routes</option> 
                		<#list facilityList as facility> 
                			<option value='${facility.facilityId}'>
	                    		${facility.facilityId}
	                  		</option>
                		</#list> 
	         		</select>
	         		<input type="hidden" name="reportNameFlag" value="DayWiseRouteSalesReport" />	
      				 From<@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd" size="5" maxlength="15" id="routeWiseFromDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      				 To<@htmlTemplate.renderDateTimeField name="thruDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd" size="5" maxlength="15" id="routeWiseThruDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      				<input type="submit" value="Download" class="buttontext"/>
      			
      			</td>
      		</form>
      	</tr> 
      	 <tr >
      		<form id="AgentDailySalesAndCollectionReport" name="AgentDailySalesAndCollectionReport" method="post"  target="_blank" action="<@ofbizUrl>AgentDailySalesAndCollectionReport.pdf</@ofbizUrl>">	
      			<td>Agent Wise Daily Sales And Collection</td>
      			<td>Route
      				<select name="routeId" class='h4'  onchange="javascript:setRouteBoothsDropDown(this);"  id="routeId">
                		<option value="">Select Route</option>
                		<#list facilityList as facility> 
                			<option value='${facility.facilityId}'>
	                    		${facility.facilityId}
	                  		</option>
                		</#list> 
	         		</select>
	         		Booth Code <input class='h2' type="text" size="8" maxlength="4" name="boothId" id="boothId" value=""/>  
      				 From<@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd" size="5" maxlength="15" id="agentWiseFromDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      				 To<@htmlTemplate.renderDateTimeField name="thruDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd" size="5" maxlength="15" id="agentWiseThruDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      				<input type="submit" value="Download" class="buttontext"/>
      			
      			</td>
      		</form>
      	</tr>
      	</table>		
    </div>  
 </div>
</div>

<div class="righthalf">
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Margin Reports</h3>
    </div>
    <div class="screenlet-body">
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
      	<!-- <tr>  
			<form id="DistributorMarginReport" name="DistributorMarginReport" method="post" action="<@ofbizUrl>DistributorMarginReport.txt</@ofbizUrl>">	
      			<td> Distributor Margin Report </td>
      			<td >Time Period
      				<select name="customTimePeriodId" class='h4'>
                		<#list timePeriodList as timePeriod>    
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${timePeriod.customTimePeriodId}
	                  		</option>
                		</#list>            
					</select>
					<input type="submit" value="Download" class="buttontext"/>
          		</td>
      	    </form>
        </tr>-->         
        <tr>
        	<form id="CRInsReport" name="CRInsReport" method="post" action="<@ofbizUrl>DisplaySOAndCRInsReport.txt?categoryTypeEnum=CR_INST</@ofbizUrl>">	
      			<td>CreditInst Report</td>
      			<td >From<@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" value="" className="" alert="" title="Format: dd/MM/yyyy" size="10" maxlength="20" id="CRfromDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
					 To<@htmlTemplate.renderDateTimeField name="thruDate" event="" action="" value="" className="" alert="" title="Format: dd/MM/yyyy" size="10" maxlength="20" id="CRthruDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
					<input type="submit" value="Download" class="buttontext"/>
				</td>
				</tr>
      		</form>	
        </tr>
        <!--<tr>
        	<form id="SOInsReport" name="SOInsReport" method="post" action="<@ofbizUrl>DisplaySOAndCRInsReport.txt?categoryTypeEnum=SO_INST</@ofbizUrl>">	
      			<td>SpcialOrderInst Report</td>
      			<td >From Date<@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" value="" className="" alert="" title="Format: dd/MM/yyyy" size="10" maxlength="20" id="SOfromDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
					 Thru Date<@htmlTemplate.renderDateTimeField name="thruDate" event="" action="" value="" className="" alert="" title="Format: dd/MM/yyyy" size="10" maxlength="20" id="SOthruDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
					<input type="submit" value="Download" class="buttontext"/>
				</td>
				</tr>
      		</form>	
        </tr> -->
      </table> 	  		
    </div>
 <!--<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Card Sale Reports</h3>
    </div>
    <div class="screenlet-body">
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
      	<tr>
      		<form id="GenarateCardSaleReport" name="GenarateCardSaleReport" method="post" action="<@ofbizUrl>CardSaleReport.csv</@ofbizUrl>">
      			<td> Card Sale Report</td>
      			<td >Card Period
      			    <select name="customTimePeriodId" id="customTimePeriodId" class='h4'>
                		<#list cardPeriodList as timePeriod>    
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${timePeriod.customTimePeriodId}
	                  		</option>
                		</#list>            
					</select>
					<input type="submit" value="Download" class="buttontext"/>
          		</td>
          	</form>
      	</tr>
      </table>
     </div> 
     </div>
	</div>-->
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Crates/Cans Report</h3>
    </div>
    <div class="screenlet-body">
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
      	<tr>
      		<form id="CratesReport" name="CratesReport" method="post" action="<@ofbizUrl>CratesReport.csv</@ofbizUrl>">
      	
      			<td>Crates/Cans Report</td>      			
      			<td>From<@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd" size="10" maxlength="20" id="fromDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
					To<@htmlTemplate.renderDateTimeField name="thruDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd" size="10" maxlength="20" id="thruDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
					<input type="submit" value="Download" class="buttontext"/>
				</td>          		
          	</form>
      	</tr>
      </table>
     </div> 
     </div>
	</div>
 </div>
 
 
 
