
<div class="lefthalf">
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h2><center>Daily Reports</center></h2>
    </div>
    <div class="screenlet-body">
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
      	<tr>
      	   <form id="RouteQuantityAbst" name="RouteQuantityAbst" method="post" action="<@ofbizUrl>routeWiseQtyAbstract.txt</@ofbizUrl>" target="_blank">	
      		  <td width="33%">Route Wise Sales Abst</td>
      		  <td width="33%">Date<@htmlTemplate.renderDateTimeField name="supplyDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="18" maxlength="22" id="rtSupplyDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/></td>
      	   	  <td width="33%"><input type="submit" target="_blank" value="Download" class="buttontext"/></td>
      	   </form>
      	</tr>
      	<tr>
      		<form id="routeIndentReport" name="routeIndentReport" method="post" action="<@ofbizUrl>routewiseIndentReport.txt</@ofbizUrl>" target="_blank">	
      			<td width="34%">Total Indent</td>
      			<td width="33%">Date<@htmlTemplate.renderDateTimeField name="supplyDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="18" maxlength="22" id="routeSupplyDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/></td>
      			<td width="33%"><input type="submit" value="Download" class="buttontext"/></td>
      		</form>
      	</tr>
      	<tr class="alternate-row">
      		<form id="indentPartyListing" name="indentPartyListing" method="post" action="<@ofbizUrl>indentPartyListing.txt</@ofbizUrl>" target="_blank">	
      			<td width="34%">Indent Party Listing</td>
      			<td width="33%">Date<@htmlTemplate.renderDateTimeField name="supplyDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="18" maxlength="22" id="indentPartyDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/></td>
      			<td width="33%"><input type="submit" value="Download" class="buttontext"/></td>
      		</form>
      	</tr> 
      	<tr>
      	   <form id="AbstractIndentReport" name="AbstractIndentReport" method="post" action="<@ofbizUrl>abstractIndentReport.txt</@ofbizUrl>" target="_blank">	
      		  <td width="33%">Abstract Indent</td>
      		  <td width="33%">Date<@htmlTemplate.renderDateTimeField name="supplyDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="18" maxlength="22" id="abstractSupplyDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/></td>
      	   	  <td width="33%"><input type="submit" target="_blank" value="Download" class="buttontext"/></td>
      	   </form>
      	</tr>
      	<tr>
      	   <form id="DeliveryNoteCumGatePassReport" name="DeliveryNoteCumGatePassReport" method="post" action="<@ofbizUrl>deliveryNoteCumGatePassReport.txt</@ofbizUrl>" target="_blank">	
      		  <td width="33%">Delivery Note-Cum GatePass</td>
      		  <td width="33%">Date<@htmlTemplate.renderDateTimeField name="supplyDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="18" maxlength="22" id="deliveryNoteGatePassSupplyDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/><input type="hidden" name="shipmentTypeId" value="BYPRODUCTS"></td>
      	   	  <td >Route 
				<select name="routeId" class='h4'>
					<#list routesList as route>    
  	    				<option value='${route}'>${route}</option>
					</#list>            
				</select>
				<input type="submit" value="Download" class="buttontext"/></td>
      	   </form>
      	</tr>
      	<#--
      	<tr class="alternate-row">
      		<form id="UnionMarketingIndent" name="UnionMarketingIndent" method="post" action="<@ofbizUrl>abstractIndentReport.txt?reportTypeFlag=UNION</@ofbizUrl>" target="_blank">	
      			<td width="34%">Union Marketing Indent</td>
      			<td width="33%">Date<@htmlTemplate.renderDateTimeField name="supplyDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="18" maxlength="22" id="abstractSupplyDate1" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/></td>
      			<td width="33%"><input type="submit" target="_blank" value="Download" class="buttontext"/></td>
      		</form>
      	</tr>
      	<tr>
      		<form id="DairyMarketingIndent" name="DairyMarketingIndent" method="post" action="<@ofbizUrl>abstractIndentReport.txt?reportTypeFlag=DAIRY</@ofbizUrl>" target="_blank">	
      			<td width="34%">Dairy Marketing Indent</td>
      			<td width="33%">Date<@htmlTemplate.renderDateTimeField name="supplyDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="18" maxlength="22" id="abstractSupplyDate2" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/></td>
      			<td width="33%"><input type="submit" target="_blank" value="Download" class="buttontext"/></td>
      		</form>
      	</tr> 
		<tr class="alternate-row">
      		<form id="routeWiseUnionIndentReport" name="routeWiseUnionIndentReport" method="post" action="<@ofbizUrl>routeWiseUnionIndentReport.txt</@ofbizUrl>" target="_blank">	
      			<td width="34%">RouteWise Union Indent</td>
      			<td width="33%">Date<@htmlTemplate.renderDateTimeField name="supplyDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="18" maxlength="22" id="routeDairySupplyDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/></td>
      			<td width='33%'>Route <input type="text" name="routeId" id="routeId" size="5" maxlength="5"/><input type="submit" value="Download" class="buttontext"/></td>
      		</form>
      	</tr> -->
      	<tr>
      		<form id="routeWiseDairyIndentReport" name="routeWiseDairyIndentReport" method="post" action="<@ofbizUrl>routeWiseDairyIndentReport.txt</@ofbizUrl>" target="_blank">	
      			<td width="34%">RouteWise Dairy Indent</td>
      			<td width="33%">Date<@htmlTemplate.renderDateTimeField name="supplyDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="18" maxlength="22" id="routeUnionSupplyDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/></td>
				<td width='33%'>Route <input type="text" name="routeId" id="routeId" size="5" maxlength="5"/><input type="submit" value="Download" class="buttontext"/></td>
      		</form>
      	</tr>      	
      	<tr  class="alternate-row">
      		<form id="routeChequeValue" name="routeChequeValue" method="post" action="<@ofbizUrl>routeChequeValue.txt</@ofbizUrl>" target="_blank">	
      			<td width="34%">Route Abstract for Cheque &amp; Value</td>
      			<td width="33%">Date<@htmlTemplate.renderDateTimeField name="supplyDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="18" maxlength="22" id="routeChequeDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/></td>
      			<td width='33%'>Route <input type="text" name="routeId" id="routeId" size="5" maxlength="5"/><input type="submit" value="Download" class="buttontext"/></td>       			
      		</form>
      	</tr>
      	<#--
      	<tr>
      		<form id="cd15union" name="CDUnion" method="post" action="<@ofbizUrl>CD15Union</@ofbizUrl>" target="_blank">	
      			<td width="34%">CD-15 Union</td>
      			<td width="33%">Date<@htmlTemplate.renderDateTimeField name="estimatedShipDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="18" maxlength="22" id="cduDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/><input type="hidden" name="shipmentTypeId" value="BYPRODUCTS"><input type="hidden" name="reportTypeFlag" value="CD-15_UNION"></td>
      			<td width="33%"><input type="submit" value="Download" class="buttontext"/></td>
      		</form>
      	</tr>
      	<tr class="alternate-row">
      		<form id="cd15dairy" name="CDDairy" method="post" action="<@ofbizUrl>CD15Dairy</@ofbizUrl>">	
      			<td width="34%">CD-15 Dairy</td>
      			<td width="33%">Date<@htmlTemplate.renderDateTimeField name="estimatedShipDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="18" maxlength="22" id="cddDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/><input type="hidden" name="shipmentTypeId" value="BYPRODUCTS"><input type="hidden" name="reportTypeFlag" value="CD-15_DAIRY"></td>       			
      			<td width="33%"><input type="submit" value="Download" class="buttontext"/></td>
      		</form>
      	</tr>
      	-->
      	<tr>
      		<form id="PaymentReport" name="PaymentReport" method="post" action="<@ofbizUrl>bankRemittanceStatement.txt</@ofbizUrl>" target="_blank">	
      			<td width="34%">Bank Remittances</td>
      			<td width="33%">Remit Dt<@htmlTemplate.renderDateTimeField name="remittanceDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="9" maxlength="22" id="remittanceDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/></td>       			
      			<td width="33%">Pmt Dt<@htmlTemplate.renderDateTimeField name="paymentDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="9" maxlength="22" id="paymentDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/><input type="submit" value="Download" class="buttontext"/></td>
      		</form>
      	</tr>	
      	<tr class="alternate-row">
      		<form id="customPartyList" name="customPartyList" method="post" action="<@ofbizUrl>customPartyList.pdf</@ofbizUrl>" target="_blank">	
      			<td width="34%">Custom Party Listing</td>
      			<td width="33%">From<@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="10" maxlength="22" id="fromDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/></td>       			
      			<td width="33%">Thru<@htmlTemplate.renderDateTimeField name="thruDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="10" maxlength="22" id="thruDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/><input type="submit" value="Download" class="buttontext"/></td>
      		</form>
      	</tr> 
      	<tr>
      		<form id="indentVsDispatchReport" name="indentVsDispatchReport" method="post" action="<@ofbizUrl>IndentVsDispatchReport.pdf</@ofbizUrl>" target="_blank">	
      			<td width="34%">Indent vs Despatch</td>
      			<td width="33%">From<@htmlTemplate.renderDateTimeField name="indentDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="18" maxlength="22" id="indentDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/></td>       			
      			<td width="33%">Thru<@htmlTemplate.renderDateTimeField name="indentThruDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="18" maxlength="22" id="indentThruDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/><input type="submit" value="Download" class="buttontext"/></td>
      		</form>
      	</tr> 
      	<#--
      	<tr class="alternate-row">
      		<form id="FIRReport" name="FIRReport" method="post" action="<@ofbizUrl>FIRReport.csv</@ofbizUrl>" target="_blank">	
      			<td width="34%">FIR Report</td>
      			<td width="33%">Date<@htmlTemplate.renderDateTimeField name="saleDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="18" maxlength="22" id="saleDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/></td>       			
      			<td width="33%"><input type="submit" value="Download" class="buttontext"/></td>
      		</form>
      	</tr>
      	
      	<tr>
      		<form id="parlourCollectionStmtForDay" name="parlourCollectionStmtForDay" method="post" action="<@ofbizUrl>parlourCollectionStmtForDay.txt</@ofbizUrl>" target="_blank">	
      			<td width="34%">Parlour Collection Statement</td>
      			<td width="33%">Date<@htmlTemplate.renderDateTimeField name="saleDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="18" maxlength="22" id="saleParlourDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/></td>       			
      			<td width="33%"><input type="submit" value="Download" class="buttontext"/></td>
      		</form>
      	</tr>
      	-->
      	<tr class="alternate-row">
      		<form id="dSCorrectionCheckList" name="dSCorrectionCheckList" method="post" action="<@ofbizUrl>dSCorrectionCheckList.txt</@ofbizUrl>" target="_blank">	
      			<td width="34%">D.S. Correction CheckList</td>
      			<td width="33%">Date<@htmlTemplate.renderDateTimeField name="saleDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="18" maxlength="22" id="correctionDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/></td>       			
      			<td width="33%"><input type="submit" value="Download" class="buttontext"/></td>
      		</form>
      	</tr>
      	<tr>
      		<form id="inventorySummaryReport" name="inventorySummaryReport" method="post" action="<@ofbizUrl>inventorySummaryReport.pdf</@ofbizUrl>" target="_blank">	
      			<td width="34%">Inventory Summary Report</td>
      			<td width="33%">Fr Dt<@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="18" maxlength="22" id="invFromDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      			Tr Dt<@htmlTemplate.renderDateTimeField name="thruDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="18" maxlength="22" id="invThruDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/></td>
      			<td width="33%">PRD Code<input type="text" name="productId" id="productId"/>PTY Code<input type ="text" name="facilityId" id="facilityId"><input type="submit" value="Download" class="buttontext"/></td>
      		</form>
      	</tr>
      	<tr><#if security.hasEntityPermission("SENDLVDSMS", "_VIEW", session)>
      			<td> Last Vehicle Despatch(LVD) Sms</td>
      			<td> <a href="<@ofbizUrl>sendLVDSms</@ofbizUrl>" class="buttontext">Send</a></td></#if>
      	</tr>  
      </table>
      <#-->
          <div class="screenlet">
    		<div class="screenlet-title-bar">
      			<h3>Testing Reports</h3>
    		</div>
    		<div class="screenlet-body">
    			<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
      				<tr>
      					<form id="invoicePrePrnt" name="invoicePrePrnt" method="post" action="<@ofbizUrl>invoicePrePrnt</@ofbizUrl>" target="_blank">	
      						<td>Invoice Pre-Printed</td>
      						<td width="33%">Date<@htmlTemplate.renderDateTimeField name="estimatedShipDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="18" maxlength="22" id="invoiceSupplyDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/></td>
      						<td width='33%'>Route <input type="text" name="facilityId" id="routeId" size="5" maxlength="5"/>
      						<input type="hidden" name="reportTypeFlag" value="invoiceDeliveryNote">
      						<input type="hidden" name="shipmentTypeId" value="BYPRODUCTS">
      						<input type="submit" value="Download" class="buttontext"/></td>
          					</td>       			
      					</form>
      				</tr>
      			</table>
      		</div>
      	</div>	
	</div>-->
    </div>
</div>
</div>




<div class="righthalf">
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h2><center>Monthly Reports</center></h2>
    </div>
    <div class="screenlet-body">
    	
    	<div class="screenlet">
    		<div class="screenlet-title-bar">
      			<h3>ChannelWise Reports</h3>
    		</div>
    		<div class="screenlet-body">
    			<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
      				<tr>
      					<form id="channelWiseSales" name="channelWiseSales" method="post" action="<@ofbizUrl>channelWiseSales.txt</@ofbizUrl>" target="_blank" >	
      						<td>ChannelWise Sales</td>
      						<td >Time Period
      							<select name="customTimePeriodId" class='h4'>
                					<#list timePeriodList as timePeriod>    
                  	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.customTimePeriodId}</option>
                					</#list>            
								</select>
								<input type="hidden" name="reportTypeFlag" value="sales">
								<input type="submit" value="Download" class="buttontext"/>
          					</td>
      					</form>
      				</tr>
      				
      				<tr class="alternate-row">
      					<form id="channelWiseSalesBasicValue" name="channelWiseSalesBasicValue" method="post" action="<@ofbizUrl>channelWiseSalesBasicValue.txt</@ofbizUrl>"  target="_blank">	
      						<td>ChannelWise Sales(Basic Value)</td>
      						<td >Time Period
      							<select name="customTimePeriodId" class='h4'>
                					<#list timePeriodList as timePeriod>    
                  	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.customTimePeriodId}</option>
                					</#list>            
								</select>
								<input type="hidden" name="reportTypeFlag" value="basicValueSales">
								<input type="submit" value="Download" class="buttontext"/>
          					</td>
      					</form>
      				</tr>
      				
      				<tr>
      					<form id="channelWiseSalesMRPValue" name="channelWiseSalesMRPValue" method="post" action="<@ofbizUrl>channelWiseSalesMRPValue.txt</@ofbizUrl>" target="_blank" >	
      						<td>ChannelWise Sales(SALE Value)</td>
      						<td >Time Period
      							<select name="customTimePeriodId" class='h4'>
                					<#list timePeriodList as timePeriod>    
                  	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.customTimePeriodId}</option>
                					</#list>            
								</select>
								<input type="hidden" name="reportTypeFlag" value="MRPSales">
								<input type="submit" value="Download" class="buttontext"/>
          					</td>
      					</form>
      				</tr>
      				<#--
      				<tr class="alternate-row">
      					<form id="channelWisePruchases" name="channelWisePruchases" method="post" action="<@ofbizUrl>channelWisePruchases.txt</@ofbizUrl>" >	
      						<td>ChannelWise Purchases</td>
      						<td >Time Period
      							<select name="customTimePeriodId" class='h4'>
                					<#list timePeriodList as timePeriod>    
                  	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.customTimePeriodId}</option>
                					</#list>            
								</select>
								<input type="hidden" name="reportTypeFlag" value="purchases">
								<input type="submit" value="Download" class="buttontext"/>
          					</td>
      					</form>
      				</tr>
       	            
      				<tr>
      					<form id="channelWiseCheckList" name="channelWiseCheckList" method="post" action="<@ofbizUrl>channelWiseCheckList.txt</@ofbizUrl>" target="_blank">	
      						<td>ChannelWise CheckList</td>
      						<td >Time Period
      							<select name="customTimePeriodId" class='h4'>
                					<#list timePeriodList as timePeriod>    
                  	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.customTimePeriodId}</option>
                					</#list>            
								</select>
								<input type="submit" value="Download" class="buttontext"/>
          					</td>
      					</form>
      				</tr>
      	             -->
      				<tr class="alternate-row">
      					<form id="channelWiseDespatch" name="channelWiseDespatch" method="post" action="<@ofbizUrl>channelWiseDespatch.txt</@ofbizUrl>" target="_blank">	
      						<td>ChannelWise Despatch</td>
      						<td >Time Period
      							<select name="customTimePeriodId" class='h4'>
                					<#list timePeriodList as timePeriod>    
                  	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.customTimePeriodId}</option>
                					</#list>            
								</select>
								<input type="submit" value="Download" class="buttontext"/>
          					</td>       			
      					</form>
      				</tr>
      				<tr>
      					<form id="DespatchReport" name="DespatchReport" method="post" action="<@ofbizUrl>DespatchReport.csv</@ofbizUrl>" target="_blank">	
      						<td>Despatch Report CSV</td>
      						<td >Time Period
      							<select name="customTimePeriodId" class='h4'>
                					<#list timePeriodList as timePeriod>    
                  	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.customTimePeriodId}</option>
                					</#list>            
								</select>
								<input type="submit" value="Download" class="buttontext"/>
          					</td>       			
      					</form>
      				</tr>
      				<tr class="alternate-row">
		        	<form id="duesAbstract" name="duesAbstract" method="post" action="<@ofbizUrl>duesAbstract.csv</@ofbizUrl>">	
		      			<td>Party Ledger</td>
		      			<td >BoothId<input type="text" name="facilityId" Id='facilityId'size="7pt"/>&nbsp;&nbsp;      Category:<select name="categoryTypeEnum" id="categoryTypeEnum" class='h4'>
		                		<#list categoryTypeList as categoryType>    
		                  	    	<option value='${categoryType.enumId}'>
			                    		${categoryType.description}
			                        </option>
		                		</#list>            
							</select>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							From<@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" value="" className="" alert="" title="Format: dd/MM/yyyy" size="7" maxlength="20" id="dueAbsfromDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
							 To<@htmlTemplate.renderDateTimeField name="thruDate" event="" action="" value="" className="" alert="" title="Format: dd/MM/yyyy" size="7" maxlength="20" id="dueAbsthruDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
							<input type="submit" value="Download" class="buttontext"/>				
						</td>
					</tr>	
      				<#--<tr>
      					<form id="SaleReport" name="SaleReport" method="post" action="<@ofbizUrl>SalesReport.csv</@ofbizUrl>" target="_blank">	
      						<td>Sales Report CSV</td>
      						<td >Time Period
      							<select name="customTimePeriodId" class='h4'>
                					<#list timePeriodList as timePeriod>    
                  	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.customTimePeriodId}</option>
                					</#list>            
								</select>
								<input type="submit" value="Download" class="buttontext"/>
          					</td>       			
      					</form>
      				</tr>-->
      			</table>
    		</div>
    	</div>
    	
    	<div class="screenlet">
    		<div class="screenlet-title-bar">
      			<h3>Transfer Reports</h3>
    		</div>
    		<div class="screenlet-body">
    			<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
      				<tr>
      					<form id="stockTransferIn" name="stockTransferIn" method="post" action="<@ofbizUrl>stockTransferInOut.pdf</@ofbizUrl>" target="_blank">	
      						<td>Transfer IN</td>
      						<td >Time Period
      							<select name="customTimePeriodId" class='h4'>
                					<#list timePeriodList as timePeriod>    
                  	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.customTimePeriodId}</option>
                					</#list>            
								</select>
								<input type="hidden" name="transferType" id="transferType" value="transferIn">
								<input type="submit" value="Download" class="buttontext"/>
          					</td>       			
      					</form>
      				</tr>
      	
      				<tr class="alternate-row">
      					<form id="stockTransferOut" name="stockTransferOut" method="post" action="<@ofbizUrl>stockTransferInOut.pdf</@ofbizUrl>" target="_blank">	
      						<td>Transfer OUT</td>
      						<td >Time Period
      							<select name="customTimePeriodId" class='h4'>
                					<#list timePeriodList as timePeriod>    
                  	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.customTimePeriodId}</option>
                					</#list>            
								</select>
								<input type="hidden" name="transferType" id="transferType" value="transferOut">
								<input type="submit" value="Download" class="buttontext"/>
          					</td>       			
      					</form>
      				</tr>
      			</table>
      		</div>
      	</div>	
    	<#--
    	<div class="screenlet">
    		<div class="screenlet-title-bar">
      			<h3>Parlour Reports</h3>
    		</div>
    		<div class="screenlet-body">
    			<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
      				<tr>
      					<form id="ParlourRecieptAndSalesStmt" name="indentPartyListing" method="post" action="<@ofbizUrl>parlourRecieptAndSalesStmt</@ofbizUrl>" target="_blank">	
      						<td>Parlour Sales</td>
      						<td >Time Period
      							<select name="customTimePeriodId" class='h4'>
                					<#list timePeriodList as timePeriod>    
                  	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.customTimePeriodId}</option>
                					</#list>            
								</select>
								<input type="submit" value="Download" class="buttontext"/>
          					</td>       			
      					</form>
      				</tr>
      				
      				<tr class="alternate-row">
      					<form id="ParlourRecieptStmt" name="parlorReceipt" method="post" action="<@ofbizUrl>parlourRecieptStmt</@ofbizUrl>" target="_blank">	
      						<td>Parlour Receipts</td>
      						<td >Time Period
      							<select name="customTimePeriodId" class='h4'>
                					<#list timePeriodList as timePeriod>    
                  	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.customTimePeriodId}</option>
                					</#list>            
								</select>
								<input type="submit" value="Download" class="buttontext"/>
          					</td>       			
      					</form>
      				</tr>
      	
      				<tr>
      					<form id="salesDetails" name="salesDetails" method="post" action="<@ofbizUrl>ParlorWiseDetails.txt</@ofbizUrl>" >	
      						<td>Parlour Wise Sales Details</td>
      						<td >Time Period
      							<select name="customTimePeriodId" class='h4'>
                					<#list timePeriodList as timePeriod>    
                  	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.customTimePeriodId}</option>
                					</#list>            
								</select>
								Party Code<input type="text" name="parlourId" id="parlourId" size="8"/>
								<input type="submit" value="Download" class="buttontext"/>
          					</td>
      					</form>
      				</tr>
      				
      				<tr class="alternate-row">
      					<form id="parlourCollectionStmt" name="parlourCollectionStmt" method="post" action="<@ofbizUrl>parlourCollectionStmt.txt</@ofbizUrl>" target="_blank">	
      						<td>Parlour Collection</td>
      						<td >Time Period
      							<select name="customTimePeriodId" class='h4'>
                					<#list timePeriodList as timePeriod>    
                  	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.customTimePeriodId}</option>
                					</#list>            
								</select>
								<input type="submit" value="Download" class="buttontext"/>
          					</td>       			
      					</form>
      				</tr>
      				
      				<tr>
      					<form id="parlourSalesDaywiseForMonth" name="parlourSalesDaywiseForMonth" method="post" action="<@ofbizUrl>parlourSalesDaywiseForMonth.txt</@ofbizUrl>" >	
      						<td>Parlour Sales DayWise Collection</td>
      						<td >Time Period
      							<select name="customTimePeriodId" class='h4'>
                					<#list timePeriodList as timePeriod>    
                  	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.customTimePeriodId}</option>
                					</#list>            
								</select>
								Party Code<input type="text" name="parlourId" id="parlourId" size="8"/>
								<input type="submit" value="Download" class="buttontext"/>
          					</td>
      					</form>
      				</tr>
      			</table>
      		</div>
      	</div>
      	
      	<div class="screenlet">
    		<div class="screenlet-title-bar">
      			<h3>Special Supply Reports</h3>
    		</div>
    		<div class="screenlet-body">
    			<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
      				<tr class="alternate-row">
      					<form id="giftAbstract" name="giftAbstract" method="post" action="<@ofbizUrl>giftAbstract</@ofbizUrl>" target="_blank">	
      						<td>Free Gift Abstract</td>
      						<td >Time Period
      							<select name="customTimePeriodId" class='h4'>
                					<#list timePeriodList as timePeriod>    
                  	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.customTimePeriodId}</option>
                					</#list>            
								</select>
								<input type="submit" value="Download" class="buttontext"/>
          					</td>       			
      					</form>
      				</tr>
      	
      				<tr>
      					<form id="giftSuppliedDetails" name="giftSuppliedDetails" method="post" action="<@ofbizUrl>giftSuppliedDetails</@ofbizUrl>" target="_blank">	
      						<td>Gift Supplied Details</td>
      						<td >Time Period
      							<select name="customTimePeriodId" class='h4'>
                					<#list timePeriodList as timePeriod>    
                  	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.customTimePeriodId}</option>
                					</#list>            
								</select>
								<input type="submit" value="Download" class="buttontext"/>
          					</td>       			
      					</form>
      				</tr>
      	
      				<tr class="alternate-row">
      					<form id="specialOrderSuppliedDetails" name="specialOrderSuppliedDetails" method="post" action="<@ofbizUrl>specialOrderSuppliedDetails.pdf</@ofbizUrl>" target="_blank">	
      						<td>Special Order Supplied Details</td>
      						<td >Time Period
      							<select name="customTimePeriodId" class='h4'>
                					<#list timePeriodList as timePeriod>    
                  	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.customTimePeriodId}</option>
                					</#list>            
								</select>
								<input type="submit" value="Download" class="buttontext"/>
          					</td>       			
      					</form>
      				</tr>
      				<tr class="alternate-row">
      					<form id="hotMilkStatement" name="hotMilkStatement" method="post" action="<@ofbizUrl>hotMilkStatement</@ofbizUrl>" target="_blank">	
      						<td>Hot Milk Statement</td>
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
      	-->
      	<div class="screenlet">
    		<div class="screenlet-title-bar">
      			<h3>Tax Reports</h3>
    		</div>
    		<div class="screenlet-body">
    			<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
    			<#-->
      				<tr>
      					<form id="monthlyVATReturn" name="monthlyVATReturn" method="post" action="<@ofbizUrl>MonthlyVATReturn</@ofbizUrl>" target="_blank">	
      						<td>VAT Return</td>
      						<td >Time Period
      							<select name="customTimePeriodId" class='h4'>
                					<#list timePeriodList as timePeriod>    
                  	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.customTimePeriodId}</option>
                					</#list>            
								</select>
								<input type="submit" value="Download" class="buttontext"/>
          					</td>       			
      					</form>
      				</tr>
      				-->
      				<tr class="alternate-row">
      					<form id="vatAbstractMonthly" name="vatAbstractMonthly" method="post" action="<@ofbizUrl>vatAbstractMonthly.txt</@ofbizUrl>" >	
      						<td>VAT Abstract</td>
      						<td >Time Period
      							<select name="customTimePeriodId" class='h4'>
                					<#list timePeriodList as timePeriod>    
                  	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.customTimePeriodId}</option>
                					</#list>            
								</select>
								<input type="submit" value="Download" class="buttontext"/>
          					</td>
      					</form>
      				</tr>
      				<#-->
      				<tr>
      					<form id="exdutyMonthly" name="exdutyMonthly" method="post" action="<@ofbizUrl>ExdutyMonthly</@ofbizUrl>" target="_blank">	
      						<td>Excise Duty</td>
      						<td >Time Period
      							<select name="customTimePeriodId" class='h4'>
                					<#list timePeriodList as timePeriod>    
                  	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.customTimePeriodId}</option>
                					</#list>            
								</select>
								<input type="submit" value="Download" class="buttontext"/>
          					</td>       			
      					</form>
      				</tr>
      				-->
      			</table>
      		</div>
      	</div>
      	<div class="screenlet">
    		<div class="screenlet-title-bar">
      			<h3>Misc Reports</h3>
    		</div>
    		<div class="screenlet-body">
    			<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
    		     <#--
      				<tr class="alternate-row">
      					<form id="cd15DairyMonthly" name="cd15DairyMonthly" method="post" action="<@ofbizUrl>cd15DairyMonthly</@ofbizUrl>" target="_blank">	
      						<td>CD-15 Dairy</td>
      						<td >Time Period
      							<select name="customTimePeriodId" class='h4'>
                					<#list timePeriodList as timePeriod>    
                  	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.customTimePeriodId}</option>
                					</#list>            
								</select>
								<input type="submit" value="Download" class="buttontext"/>
          					</td>       			
      					</form>
      				</tr>
      	
      				<tr>
      					<form id="cd15UnionMonthly" name="cd15UnionMonthly" method="post" action="<@ofbizUrl>cd15UnionMonthly</@ofbizUrl>" target="_blank">	
      						<td>CD-15 Union</td>
      						<td >Time Period
      							<select name="customTimePeriodId" class='h4'>
                					<#list timePeriodList as timePeriod>    
                  	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.customTimePeriodId}</option>
                					</#list>            
								</select>
								<input type="submit" value="Download" class="buttontext"/>
          					</td>       			
      					</form>
      				</tr> -->
      				
      				<tr class="alternate-row">
      					<form id="dueParticulars" name="dueParticulars" method="post" action="<@ofbizUrl>dueParticulars.txt</@ofbizUrl>" target="_blank">	
      						<td>Due Particulars</td>
      						<td >Time Period
      							<select name="customTimePeriodId" class='h4'>
                					<#list timePeriodList as timePeriod>    
                  	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.customTimePeriodId}</option>
                					</#list>            
								</select>
								<input type="submit" value="Download" class="buttontext"/>
          					</td>       			
      					</form>
      				</tr>
      				<tr>
      					<form id="indentVsDispatchMonthly" name="indentVsDispatchMonthly" method="post" action="<@ofbizUrl>IndentVsDispatchMonthly.csv</@ofbizUrl>" target="_blank">	
      						<td>Indent vs Despatch</td>
      						<td >Time Period
      							<select name="customTimePeriodId" class='h4'>
                					<#list timePeriodList as timePeriod>    
                  	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.customTimePeriodId}</option>
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
</div>
