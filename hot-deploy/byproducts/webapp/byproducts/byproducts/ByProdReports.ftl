
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
      	<tr  class="alternate-row">
      		<form id="routeChequeValue" name="routeChequeValue" method="post" action="<@ofbizUrl>routeChequeValue.txt</@ofbizUrl>" target="_blank">	
      			<td width="34%">Route Abstract for Cheque &amp; Value</td>
      			<td width="33%">Date<@htmlTemplate.renderDateTimeField name="supplyDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="18" maxlength="22" id="routeChequeDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/></td>
      			<td width='33%'>Route <input type="text" name="routeId" id="routeId" size="5" maxlength="5"/><input type="submit" value="Download" class="buttontext"/></td>       			
      		</form>
      	</tr>
      	<tr>
      		<form id="PaymentReport" name="PaymentReport" method="post" action="<@ofbizUrl>bankRemittanceStatement.txt</@ofbizUrl>" target="_blank">	
      			<td width="34%">Bank Remittances</td>
      			<td width="33%">Remit Dt<@htmlTemplate.renderDateTimeField name="remittanceDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="9" maxlength="22" id="remittanceDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/></td>       			
      			<td width="33%">Pmt Dt<@htmlTemplate.renderDateTimeField name="paymentDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="9" maxlength="22" id="paymentDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/><input type="submit" value="Download" class="buttontext"/></td>
      		</form>
      	</tr>	
      	<tr>
      		<form id="indentVsDispatchReport" name="indentVsDispatchReport" method="post" action="<@ofbizUrl>IndentVsDispatchReport.pdf</@ofbizUrl>" target="_blank">	
      			<td width="34%">Indent vs Despatch</td>
      			<td width="33%">From<@htmlTemplate.renderDateTimeField name="indentDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="18" maxlength="22" id="indentDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/></td>       			
      			<td width="33%">Thru<@htmlTemplate.renderDateTimeField name="indentThruDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="18" maxlength="22" id="indentThruDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/><input type="submit" value="Download" class="buttontext"/></td>
      		</form>
      	</tr> 
      	<tr><#if security.hasEntityPermission("SENDLVDSMS", "_VIEW", session)>
      			<td> Last Vehicle Despatch(LVD) Sms</td>
      			<td> <a href="<@ofbizUrl>sendLVDSms</@ofbizUrl>" class="buttontext">Send</a></td></#if>
      	</tr>  
      </table>
	 </div>
    </div>
    
</div><!-- left half Div End -->





<div class="righthalf">

<div class="screenlet">
    <div class="screenlet-title-bar">
      <h2><center>Monthly Reports</center></h2>
    </div>
    <div class="screenlet-body">
    	
    	<div class="screenlet">
    		<div class="screenlet-title-bar">
      			<h3>Other Reports</h3>
    		</div>
    		<div class="screenlet-body">
    			<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
    			  <#-- 
      				<tr>
      					<form id="channelWiseSales" name="channelWiseSales" method="post" action="<@ofbizUrl>channelWiseSales.txt</@ofbizUrl>" target="_blank" >	
      						<td>ChannelWise Sales</td>
      						<td >Time Period
      							<select name="customTimePeriodId" class='h4'>
                					<#list timePeriodList as timePeriod>    
                  	    				<option value='${timePeriod.customTimePeriodId}'> ${(timePeriod.get("fromDate",locale))?if_exists} - ${(timePeriod.get("thruDate",locale))?if_exists}</option>
                					</#list>            
								</select>
								<input type="hidden" name="reportTypeFlag" value="sales">
								<input type="submit" value="Download" class="buttontext"/>
          					</td>
      					</form>
      				</tr>
      				<tr class="alternate-row">
      					<form id="channelWiseDespatch" name="channelWiseDespatch" method="post" action="<@ofbizUrl>channelWiseDespatch.txt</@ofbizUrl>" target="_blank">	
      						<td>ChannelWise Despatch</td>
      						<td >Time Period
      							<select name="customTimePeriodId" class='h4'>
                					<#list timePeriodList as timePeriod>    
                  	    				<option value='${timePeriod.customTimePeriodId}'> ${(timePeriod.get("fromDate",locale))?if_exists} - ${(timePeriod.get("thruDate",locale))?if_exists}</option>
                					</#list>            
								</select>
								<input type="submit" value="Download" class="buttontext"/>
          					</td>       			
      					</form>
      				</tr> -->
      				<tr class="alternate-row">
			      	   <form id="paymentOBandCB" name="paymentOBandCB" method="post" action="<@ofbizUrl>PartywiseBalanceAbstract.pdf</@ofbizUrl>" target="_blank">        
			             <td>Partywise Ledger Abstract</td>
			             <td width="80%" colspan='2'>
			             From<@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="10" maxlength="22" id="EfffromDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
			             Thru<@htmlTemplate.renderDateTimeField name="thruDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="10" maxlength="22" id="EffthruDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>                               
			             </br>PTY Code <input type="text" name="partyCode" id="ptyCode" size="10" maxlength="22">
			             <input type="submit" value="TXT" onClick="javascript:appendParams('paymentOBandCB', '<@ofbizUrl>PartywiseBalanceAbstract.txt</@ofbizUrl>');" class="buttontext"/>
						 &nbsp;
						 <input type="submit" value="PDF" onClick="javascript:appendParams('paymentOBandCB', '<@ofbizUrl>PartywiseBalanceAbstract.pdf</@ofbizUrl>');" class="buttontext"/>
			             &nbsp;
			             </td>
			           </form>
			        </tr>
			        <#-- 
      				<tr class="alternate-row">
		        	<form id="duesAbstract" name="duesAbstract" method="post" action="<@ofbizUrl>duesAbstract.csv</@ofbizUrl>">	
		      			<td>Party Ledger</td>
		      			<td >BoothId<input type="text" name="facilityId" Id='facilityId'size="7pt"/>&nbsp;&nbsp;    &nbsp;&nbsp;   Category:<select name="categoryTypeEnum" id="categoryTypeEnum" class='h4'>
		                		<#list categoryTypeList as categoryType>    
		                  	    	<option value='${categoryType.enumId}'>
			                    		${categoryType.description}
			                        </option>
		                		</#list>            
							</select>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							From<@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" value="" className="" alert="" title="Format: dd/MM/yyyy" size="7" maxlength="20" id="dueAbsfromDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
							 To<@htmlTemplate.renderDateTimeField name="thruDate" event="" action="" value="" className="" alert="" title="Format: dd/MM/yyyy" size="7" maxlength="20" id="dueAbsthruDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
							<input type="submit" value="Download" class="buttontext"/>				
						</td>
					</tr>	-->
      			
      			</table>
    		</div>
    	</div>
    	
    	<#--
      	
      	<div class="screenlet">
    		<div class="screenlet-title-bar">
      			<h3>Special Supply Reports</h3>
    		</div>
    		<div class="screenlet-body">
    			<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
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
      			</table>
      		</div>
      	</div>
      	
      	<div class="screenlet">
    		<div class="screenlet-title-bar">
      			<h3>Tax Reports</h3>
    		</div>
    		<div class="screenlet-body">
    			<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
      				<tr class="alternate-row">
      					<form id="vatAbstractMonthly" name="vatAbstractMonthly" method="post" action="<@ofbizUrl>vatAbstractMonthly.txt</@ofbizUrl>" >	
      						<td>VAT Abstract</td>
      						<td >Time Period
      							<select name="customTimePeriodId" class='h4'>
                					<#list timePeriodList as timePeriod>    
                  	    				<option value='${timePeriod.customTimePeriodId}'> ${(timePeriod.get("fromDate",locale))?if_exists} - ${(timePeriod.get("thruDate",locale))?if_exists}</option>
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
      			<h3>Misc Reports</h3>
    		</div>
    		<div class="screenlet-body">
    			<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
      				<tr class="alternate-row">
      					<form id="dueParticulars" name="dueParticulars" method="post" action="<@ofbizUrl>dueParticulars.txt</@ofbizUrl>" target="_blank">	
      						<td>Due Particulars</td>
      						<td >Time Period
      							<select name="customTimePeriodId" class='h4'>
                					<#list timePeriodList as timePeriod>    
                  	    				<option value='${timePeriod.customTimePeriodId}'> ${(timePeriod.get("fromDate",locale))?if_exists} - ${(timePeriod.get("thruDate",locale))?if_exists}</option>
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
