<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	

<script type="application/javascript">
   var boothsList =  ${StringUtil.wrapString(boothsJSON)}
	var routesList =  ${StringUtil.wrapString(routesJSON)}
	<#if (showBoothAutoSuggest?has_content) && (showBoothAutoSuggest != 'N') >
			var routeBoothsData = ${StringUtil.wrapString(facilityItemsJSON)}
			var supplyRouteList =  ${StringUtil.wrapString(supplyRouteItemsJSON)}
			function setRouteDropDown(selection){	
				//routesList = routesList;
				routesList = supplyRouteList[selection.value];
				if(selection.value =="" || typeof selection.value == "undefined"){
					routesList =  ${StringUtil.wrapString(routesJSON)}
				}				
			}	
			function setRouteBoothsDropDown(selection){	
				boothsList = routeBoothsData[selection.value];
				
				if(selection.value =="" || typeof selection.value == "undefined"){
					boothsList =  ${StringUtil.wrapString(boothsJSON)}
				}				
			}
			<#if subscriptionTypeId?exists && subscriptionTypeId?has_content> 
				routesList =  supplyRouteList["${subscriptionTypeId}"]; 
			</#if>		
			<#if routeId?exists && routeId?has_content> 
				 boothsList = routeBoothsData["${routeId}"];
			</#if>	
						
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
	$( "#"+thruDateId ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				//$( "#"+fromDateId ).datepicker( "option", "maxDate", selectedDate );
			}
		});
	}


	$(document).ready(function(){
		makeDatePicker("RLAFromDateId","RLAThruDateId");
		makeDatePicker("CLRFromDateId","thuDateId");
		makeDatePicker("DBCFromDateId","thuDateId");
		makeDatePicker("exportDate","");
		makeDatePicker("NIMCDate","");
		makeDatePicker("rtSupplyDate","");
		makeDatePicker("RouteIndentAbstDate","");
		makeDatePicker("RouteTrCorrDate","");
		makeDatePicker("bsFromDateId","fromDateId");
		makeDatePicker("bsThruDateId","thuDateId");
		makeDatePicker("smsNotify","");
		$('#ui-datepicker-div').css('clip', 'auto');		
	});

</script>
	<div >
<#if screenFlag?exists && screenFlag.equals("MiscReports") && security.hasEntityPermission("BYPRODUCTS", "_MISCREPOR", session)>
<div class="screenlet">
        
    		<div class="screenlet-title-bar">
      			<h3>Export</h3>
    		</div>
    		<div class="screenlet-body">
    			<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
      				<tr>
      	   <form id="indentExportOracle" name="RouteQuantityAbst" method="post" action="<@ofbizUrl>indentExportOracle.txt</@ofbizUrl>" target="_blank">	
      		  <td width="33%">Indent Export</td>
      		  <td width="33%">populate:<input type="checkbox" name='populateData'>
      		      Route:<select name="routeId" class='h4'><option value=''></option>
					<#list routesList as route>    
  	    				<option value='${route}'>${route}</option>
					</#list>  
				</select>
      		 </td>
      	   	  <td width="33%"> SupplyTime:
				<select name="subscriptionTypeId" class='h4'><option value=''></option>
					<#list subscriptionTypeList as subscriptionType>    
  	    				<option value='${subscriptionType.subscriptionTypeId}'>${subscriptionType.description}</option>
					</#list>            
				</select>
      	   	   Date<input type="text" name="supplyDate" id="exportDate"/>
      	   	  <input type="submit" target="_blank" value="Download" class="buttontext"/></td>
      	   </form>
      	</tr>
    </table>
    </div>
</div>
</div>
</#if>
<#if screenFlag?exists && screenFlag.equals("DailyReports")  && security.hasEntityPermission("BYPRODUCTS", "_DAILREPOR", session)>
<div class="lefthalf">
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h2><center>Daily Reports</center></h2>
    </div>
    <div class="screenlet-body">
    
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
      <#if security.hasEntityPermission("BYPRODUCTS", "_MKTREPOR", session)>
	       <tr>
	        	<form id="nextDayIndentCollection" name="nextDayIndentCollection" method="post"  target="_blank" action="<@ofbizUrl>IndentManualCollection</@ofbizUrl>">	
	      			<td width="34%">NextDay Manual Indent Collection </td>
	      			<td width="33%">Date<input  type="text" size="18pt" id="NIMCDate" readonly  name="supplyDate"/>
	      			<td width="33%">Route 
					<select name="routeId" class='h4'>
					<option value=''>All</option>
						<#list routesList as route>    
	  	    				<option value='${route}'>${route}</option>
						</#list>  
						<input type="submit" value="Download" class="buttontext"/></td>
	      		</form>	
	        </tr>
	        <tr class="alternate-row">
      	   <form id="RouteQuantityAbst" name="RouteQuantityAbst" method="post" action="<@ofbizUrl>routeWiseQtyAbstract.txt</@ofbizUrl>" target="_blank">	
      		  <td width="33%">Route Wise Sales Abst</td>
      		  <td width="33%">Date<input  type="text" size="18pt" id="rtSupplyDate" readonly  name="supplyDate"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      		  <input type="checkbox" name="summeryOnly" value="summeryOnly">Summary Report Only<br></td>
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
      
      <!--	<tr>
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
      	</tr> -->
      
      	 <tr>
            <form id="BoothWiseSales" name="BoothWiseSales" method="post" action="<@ofbizUrl>boothWiseSales.csv</@ofbizUrl>" target="_blank">	
      			<td width="34%">Booth Wise Sales</td>
      			<td width="33%">From<input  type="text" size="18pt" id="bsFromDateId" readonly  name="bsFromDate"/>
      			<td width="33%">Thru<input  type="text" size="18pt" id="bsThruDateId" readonly  name="bsThruDate"/>
      		    <input type="submit" value="Download" class="buttontext"/></td>
      	  </form>
         </tr> 	
       <tr>
      		<form id="indentVsDispatchReport" name="indentVsDispatchReport" method="post" action="<@ofbizUrl>IndentVsDispatchReport.pdf</@ofbizUrl>" target="_blank">	
      			<td width="34%">Indent vs Despatch</td>
      			<td width="33%">From<@htmlTemplate.renderDateTimeField name="indentDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="18" maxlength="22" id="indentDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/></td>       			
      			<td width="33%">Thru<@htmlTemplate.renderDateTimeField name="indentThruDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="18" maxlength="22" id="indentThruDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/><input type="submit" value="Download" class="buttontext"/></td>
      		</form>
      	</tr> 
      	 <tr class="alternate-row">
        	<form id="TruckSheetCorrectionsReport" name="TruckSheetCorrectionsReport" method="post"  target="_blank" action="<@ofbizUrl>TruckSheetCorrectionsReport</@ofbizUrl>">	
      			<td width="34%">TruckSheet Corrections Report </td>
      			<td width="30%">Date<input  type="text" size="18pt" id="RouteTrCorrDate" readonly  name="supplyDate"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      			<#--><input type="checkbox" name="summeryOnly" value="summeryOnly">Summary Report Only<br> -->
      			</td>			
      			<td width="36%">Route 
				 <select name="routeId" class='h4'>
				<option value='All-Routes'>All</option>
					<#list routesList as route>    
  	    				<option value='${route}'>${route}</option>
					</#list> 
					</select>
					Type 
					<select name='subscriptionTypeId' class='h4'>
					<option value='AM'>AM</option>
					<option value='PM'>PM</option>
					</select>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<input type="submit" value="Download" class="buttontext"/></td>
      		</form>	
        </tr>
      	
      	 <tr>
        	<form id="DealerBankChallan" name="DealerBankChallan" method="post"  target="_blank" action="<@ofbizUrl>DealerBankChallan.txt</@ofbizUrl>">	
      			<td width="34%">Dealer BankChallan Report </td>
      			<td width="33%">
      			Date<input  type="text" size="18pt" id="DBCFromDateId" readonly  name="supplyDate"/>
      			<#-->
      			<td width="33%">Date<@htmlTemplate.renderDateTimeField name="supplyDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="18" maxlength="22" id="bankChallanSupplyDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/></td> -->
      			<td width="33%">Route 
				<select name="routeId" class='h4'><option value='All-Routes'>All-Routes</option>
					<#list routesList as route>    
  	    				<option value='${route}'>${route}</option>
					</#list>  
					</select>
					<input type="submit" value="Download" class="buttontext"/></td>
      		</form>	
        </tr>  
        </#if>
        <#if security.hasEntityPermission("BYPRODUCTS", "_DISREPORT", session)>
        <tr class="alternate-row">
        	<form id="IndentAbstractReport" name="IndentAbstractReport" method="post"  target="_blank" action="<@ofbizUrl>IndentAbstractReport</@ofbizUrl>">	
      			<td width="34%">RouteWise Indent Abstract Report </td>
      			<td width="30%">Date<input  type="text" size="18pt" id="RouteIndentAbstDate" readonly  name="supplyDate"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      			<input type="checkbox" name="summeryOnly" value="summeryOnly">Summary Report Only<br>
      			</td>			
      			<td width="36%">Route 
				 <select name="routeId" class='h4'>
				<option value='All-Routes'>All</option>
					<#list routesList as route>    
  	    				<option value='${route}'>${route}</option>
					</#list> 
					</select>
					Type 
					<select name='subscriptionTypeId' class='h4'>
					<option value=''>All</option>
					<option value='AM'>AM</option>
					<option value='PM'>PM</option>
					</select>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<input type="submit" value="Download" class="buttontext"/></td>
      		</form>	
        </tr>
     </#if>
     <#if security.hasEntityPermission("BYPRODUCTS", "_ACTGRPRT", session)> 
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
      	<tr class="alternate-row">
        	<form id="DailyPaymentCheckList" name="DailyPaymentCheckList" method="post"  target="_blank" action="<@ofbizUrl>DailyPaymentCheckList</@ofbizUrl>">	
      			<td width="34%">Daily Payment CheckList Report<input  type="hidden"  value="DailyPaymentCheckList"   name="reportTypeFlag"/> </td>
      			<td>
      			Date<input  type="text" size="18pt" id="CLRFromDateId" readonly  name="paymentDate"/>
      			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;   &nbsp;Route 
				<select name="routeId" class='h4'>
				<option value='All'>All </option>
					<#list routesList as route>    
  	    				<option value='${route}'>${route}</option>
					</#list>  
					</select>
      			</td>
      			<#--
      			<td width="33%">Date<@htmlTemplate.renderDateTimeField name="supplyDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="18" maxlength="22" id="bankChallanSupplyDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/></td>
      			-->
      			<td >
					PaymentType 
					<select name="paymentMethodTypeId" class='h4'>
						<#list paymentMethodList as paymentMethod>    
	  	    				<option value='${paymentMethod.paymentMethodTypeId}'>${paymentMethod.description}</option>
						</#list> 
						</select>
					<input type="submit" value="Download" class="buttontext"/></td>
      		</form>	
        </tr> 
     </#if>
      	
         
      </table>
	 </div>
    </div>
    <#if security.hasEntityPermission("SENDLVDSMS", "_VIEW", session)>
     <div class="screenlet">
    <div class="screenlet-title-bar">
      <h2><center>Notification </center></h2>
    </div>
    <div class="screenlet-body">
    
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
       <tr>
      		<form id="sendSMSNotify" name="sendSMSNotify" method="post" action="<@ofbizUrl>sendSMSNotification</@ofbizUrl>">	
      			<td width="34%">Product Sale Notification SMS</td>
      			<td width="30%">Date<input  type="text" size="18pt" id="smsNotify" readOnly  name="supplyDate"/></td>
      			<td width="33%"><select name="subscriptionTypeId" id="subscriptionTypeId" class='h4'>
					<option value='AM'>AM</option><option value='PM'>PM</option></select>
					<input type="submit" value="Send SMS" class="buttontext"/></td>		
      		</form>
      	</tr>	
      </table>
	 </div>
    </div>
    </#if>
   
</div><!-- left half Div End -->


</#if>

<#if screenFlag?exists && screenFlag.equals("MonthlyReports") && security.hasEntityPermission("BYPRODUCTS", "_MNTHREPOR", session)>
<div class="lefthalf">

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
			        
			        <tr class="alternate-row">
			      	   <form id="RetailerLedgerAbstract" name="RetailerLedgerAbstract" method="post" action="<@ofbizUrl>RetailerLedgerAbstract.pdf</@ofbizUrl>" target="_blank">        
			             <td>Retailer Ledger Abstract(Incl Products)</td>
			             <td width="40%" >
			             	From<input  type="text" size="10pt" id="RLAFromDateId" readonly  name="fromDate"/>
					        To<input  type="text" size="10pt" id="RLAThruDateId"  readonly name="thruDate"/>
			             </td>
			             <td width="40%" >
			             </br>Retailer Code <input type="text" name="boothId" id="boothId" size="10" maxlength="22">
			             <input type="submit" value="Download" class="buttontext"/>
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
</#if>
