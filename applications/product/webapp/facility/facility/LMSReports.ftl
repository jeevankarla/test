
<div class="lefthalf">
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Daily reports for ${reportDateStr}</h3>
    </div>
    <div class="screenlet-body">
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
      	<tr>
      		<td> Changes CSV For FoxPro</td>
      		<td> <a href="<@ofbizUrl>changesReportCsv.csv</@ofbizUrl>" class="buttontext">Download</a></td>
      	</tr>
      	<tr class="alternate-row">
      		<td> E-Seva/APOnline Booth Cash Dues </td>
      		<td> <a href="<@ofbizUrl>esevaAponlinePaymentReport.csv</@ofbizUrl>" class="buttontext">Download</a></td>
      	</tr>
      	<tr>
      		<td> Cash Remittances </td>
      		<td> <a href="<@ofbizUrl>DisplayDuesReport.pdf</@ofbizUrl>" class="buttontext">Download</a></td>
      	</tr>  
      	<tr class="alternate-row">
      		<td> Net Sales </td>
      		<td> <a href="<@ofbizUrl>NetSalesReport.txt</@ofbizUrl>" class="buttontext">Download</a></td>
      	</tr>
      	<tr >
      		<td> Distributor Wise Sales Statement </td>
      		<td> <a href="<@ofbizUrl>DisplayDistributorReport.txt</@ofbizUrl>" class="buttontext">Download</a></td>
      	</tr>
      	<tr class="alternate-row">
      		<td> Zone Wise Vendor Cash Realisation Statement </td>
      		<td> <a href="<@ofbizUrl>zoners.pdf</@ofbizUrl>" class="buttontext">Download</a></td>
      	</tr>
      	<tr>
      		<td> Distributor Cash Realisation </td>
      		<td> <a href="<@ofbizUrl>DistributorWiseCashRealisationReport.txt?isPreviousFlag=Y</@ofbizUrl>" class="buttontext">Download</a></td>
      	</tr>
      	<tr>
      		<td> Distributor Cash Realisation Summary</td>
      		<td> <a href="<@ofbizUrl>DistributorWiseCashRealisationReport.txt?isPreviousFlag=N</@ofbizUrl>" class="buttontext">Download</a></td>
      	</tr>    
      	<tr>
      		<td> Route Wise Quota Summary</td>
      		<td> <a href="<@ofbizUrl>RouteWiseQuotaSummary.txt</@ofbizUrl>" class="buttontext">Download</a></td>
      	</tr>     
      	<tr>
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
      	</tr> 
      	<tr>
      		<form id="RouteWiseDeliverySchedule" name="RouteWiseDeliverySchedule" method="post" action="<@ofbizUrl>RouteWiseDeliverySchedule.txt</@ofbizUrl>">	
      			<td>Route Wise Delivery Schedule </td>
      			<td >Route
      				<select name="facilityId" class='h4' allow-empty="true">
      					<option value=""/>
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
      	<tr>
      		<form id="VehicleShortPaymentReport" name="VehicleShortPaymentReport" method="post" action="<@ofbizUrl>VehicleShortPaymentReport.txt</@ofbizUrl>">	
      			<td>Vehicle Short Payment Report</td>
      			<td>Payment Date<@htmlTemplate.renderDateTimeField name="paymentDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="22" maxlength="25" id="shotPaymentDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/><input type="submit" value="Download" class="buttontext"/></td>
      		</form>
      	</tr>	     	    	
      </table> 		
    </div>
</div>
<div class="screenlet">
	<div class="screenlet-title-bar">
      <h3>Past Reports</h3>
	</div>
	<div class="screenlet-body">
		<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
			<tr>
				<form id="ListEsevaAponlinePaymentsCSV" name="ListEsevaAponlinePaymentsCSV" method="post" action="<@ofbizUrl>esevaAponlinePaymentReport.csv</@ofbizUrl>">	
      		    	<td> E-Seva/APOnline Booth Cash Dues </td>
      				<td>Supply Date<@htmlTemplate.renderDateTimeField name="paymentDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="22" maxlength="25" id="paymentDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/><input type="submit" value="Download" class="buttontext"/></td>
      			</form>
        	</tr>
			<tr>
				<form id="DuesReport" name="DuesReport" method="post" action="<@ofbizUrl>DisplayDuesReport.pdf</@ofbizUrl>">	
      		    	<td> Cash Remittances </td>
      				<td>Supply Date<@htmlTemplate.renderDateTimeField name="estimatedDeliveryDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="22" maxlength="25" id="estimatedDeliveryDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/><input type="submit" value="Download" class="buttontext"/></td>
      			</form>
        	</tr>
      		<tr>
      			<form id="NetSalesReport" name="NetSalesReport" method="post" action="<@ofbizUrl>NetSalesReport.txt</@ofbizUrl>">	
      				<td> Net Sales Report </td>
      				<td>Supply Date<@htmlTemplate.renderDateTimeField name="estimatedDeliveryDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="22" maxlength="25" id="estimatedDeliveryDate1" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/><input type="submit" value="Download" class="buttontext"/></td>
      			</form>
      		</tr>	
      				
      		<tr>
      			<form id="DistributorReport" name="DistributorReport" method="post" action="<@ofbizUrl>DisplayDistributorReport.txt</@ofbizUrl>">	<td>Distributor Wise Sales Statement </td>
      				<td>Supply Date<@htmlTemplate.renderDateTimeField name="estimatedDeliveryDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="22" maxlength="25" id="estimatedDeliveryDate2" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/><input type="submit" value="Download" class="buttontext"/></td>
      			</form>
      		</tr>
      			
      		<tr>
      			<form id="GenarateZoneWiseVendorMarginReport" name="GenarateZoneWiseVendorMarginReport" method="post" action="<@ofbizUrl>zoners.pdf</@ofbizUrl>">	
      				<td>Zone Wise Vendor Cash Realisation</td>
      				<td>Supply Date<@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="22" maxlength="25" id="fromDate1" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/><input type="submit" value="Download" class="buttontext"/></td>
      			</form>
      		</tr>
      		<tr>
      			<form id="DistributorWiseCashRealisationReport" name="DistributorWiseCashRealisationReport" method="post" action="<@ofbizUrl>DistributorWiseCashRealisationReport.txt?isPreviousFlag=Y</@ofbizUrl>">
      				<td>Distributor Cash Realisation </td>
      				<td>Supply Date<@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="22" maxlength="25" id="fromDate2" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/><input type="submit" value="Download" class="buttontext"/></td>
      			 </form>
      		</tr>	 
      		<tr>
      			<form id="DistributorWiseCashRealisationReport" name="DistributorWiseCashRealisationReport" method="post" action="<@ofbizUrl>DistributorWiseCashRealisationReport.txt?isPreviousFlag=N</@ofbizUrl>">	
      				<td>Distributor Cash Realisation Summary</td>
      				<td>Supply Date<@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="22" maxlength="25" id="fromDate3" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/><input type="submit" value="Download" class="buttontext"/></td>
      			</form>
      		</tr>
      		<tr>
      			<form id="pastPaymentsCsv" name="PastPaymentsCsv" method="post" action="<@ofbizUrl>boothPayments.csv?statusId=PAID</@ofbizUrl>">	
      				<td>Past Payment Report</td>
      				<td>Payment Date<@htmlTemplate.renderDateTimeField name="paymentDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd" size="22" maxlength="25" id="pastPaymentDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/><input type="submit" value="Download" class="buttontext"/></td>
      			</form>
      		</tr>
      	</table>		
    </div>  
 </div>
 <div class="screenlet">
	<div class="screenlet-title-bar">
      <h3>Monthly Reports</h3>
	</div>
	<div class="screenlet-body">
		<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
      	<tr>
			<form id="summaryReportMonthly" name="summaryReportMonthly" method="post" action="<@ofbizUrl>SummaryReportMonthly.txt</@ofbizUrl>">	
      			<td>Net Sales Summary Report</td>
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
        </tr>    
      	</table>		
    </div>  
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
      	<tr>
      		<form id="GenarateVendorMarginReport" name="GenarateVendorMarginReport" method="post" action="<@ofbizUrl>GenarateVendorMarginReport.pdf</@ofbizUrl>">
      			<td> Vendor Margin Report</td>
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
      	</tr>
      	<tr>
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
        </tr>
        <tr>
			<form id="VendorMarginAbstract" name="VendorMarginAbstract" method="post" action="<@ofbizUrl>VendorMarginAbstractReport.txt</@ofbizUrl>">	
      			<td> Vendor Margin Abstract</td>
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
        </tr> 
         <tr>
			<form id="SOAndCRInsReport" name="SOAndCRInsReport" method="post" action="<@ofbizUrl>DisplaySOAndCRInsReport.txt</@ofbizUrl>">	
      			<td> SOInst And CreditInst Report</td>
      			<td >Time Period
      				<select name="customTimePeriodId" class='h4'>
                		<#list timePeriodList as timePeriod>    
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${timePeriod.customTimePeriodId}
	                  		</option>
                		</#list>            
					</select>
				</td>
				<tr>
				<td/>
				<td>
				 categoryType
      				<select name="categoryTypeEnum" class='h4'>
                		<#list categoryTypeList as category>    
                  	    	<option value='${category.enumId}' >
	                    		${category.enumId}
	                  		</option>
                		</#list>            
					</select>
					<input type="submit" value="Download" class="buttontext"/>
				</td>
				</tr>
      		</form>
        </tr>     	       	     	    	
      </table> 	  		
    </div>
    <div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Zone Wise Cash Receivable Report</h3>
    </div>
    <div class="screenlet-body">
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
      	<tr>
      		<form id="ZoneWisePaidAmount" name="ZoneWisePaidAmount" method="post" action="<@ofbizUrl>zoneWisePaidAmount.pdf</@ofbizUrl>">	
      			<td>Cash Receivable Report</td>
      			<td>Payment Date<@htmlTemplate.renderDateTimeField name="paymentDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd" size="22" maxlength="25" id="paidDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/><input type="submit" formtarget="_blank" value="Download" class="buttontext"/></td>
      	    </form>
      	</tr>
      	<tr>
      		<form id="EsevaApOnlineCashReceivable" name="EsevaApOnlineCashReceivable" method="post" action="<@ofbizUrl>zoneWiseEsevaApOnlinePaidAmount.pdf</@ofbizUrl>">	
      			<td>Eseva ApOnline Cash Receivable</td>
      			<td>Payment Date<@htmlTemplate.renderDateTimeField name="paymentDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd" size="22" maxlength="25" id="PaidDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/><input type="submit" formtarget="_blank" value="Download" class="buttontext"/></td>
      	    </form>
      	</tr>
      </table>
     </div> 
     </div>
</div>
<div class="screenlet">
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
      	<tr>
      		<!--<form id="GenarateCardSaleReport" name="GenarateCardSaleReport" method="post" action="<@ofbizUrl>CardSaleReportTXT</@ofbizUrl>">
      			<td> Card Sale Report</td>
      			<td >Card Period
      				<select name="customTimePeriodId" class='h4'>
                		<#list cardPeriodList as timePeriod>    
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${timePeriod.customTimePeriodId}
	                  		</option>
                		</#list>            
					</select>
					<input type="submit" value="Download" class="buttontext"/>
          		</td>
          	</form>-->
      	</tr>
      </table>
     </div> 
     </div>
</div>
