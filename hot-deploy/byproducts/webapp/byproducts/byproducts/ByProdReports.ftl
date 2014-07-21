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
		makeDatePicker("RLAFromDateId","RLAThruDateId");
		makeDatePicker("CLRFromDateId","thuDateId");
		makeDatePicker("DBCFromDateId","thuDateId");
		makeDatePicker("exportDate","");
		makeDatePicker1("bulkSmsDate","");
		makeDatePicker("NIMCDate","");
		makeDatePicker("rtFromDateId","rtThruDateId");
		makeDatePicker("RouteIndentAbstDate","");
		makeDatePicker("ProductIndentAbstDate","");
		makeDatePicker("RouteTrCorrDate","");
		makeDatePicker("RouteTrDetDate","");
		makeDatePicker("retailerDetailDate","");
		makeDatePicker("bsFromDateId","bsThruDateId");
		makeDatePicker("prodReturnDateId","thuDateId");
		makeDatePicker("saleFromDateId","saleThruDateId");
		makeDatePicker("chequeFromDateId","chequeThruDateId");
		//makeDatePicker("materialFromDateId","materialThruDateId");
		makeDatePicker("FDRDateId","");
		makeDatePicker("vatFromDateId","vatThruDateId");
		makeDatePicker("subsidyFromDateId","subsidyThruDateId");
		makeDatePicker("effFromDate","effThruDate");
		makeDatePicker("smsNotify","");
		makeDatePicker("CASHFromDateId","");
		makeDatePicker("CashReceiptDateId","");
		makeDatePicker("DUCRFromDateId","DUCRThruDateId");
		makeDatePicker("DueFromDateId","DueThruDateId");
		makeDatePicker("DueAbsFromDateId","DueAbsThruDateId");
		makeDatePicker("catSalesFromDateId","catSalesThruDateId")
		makeDatePicker("fwsFromDateId","fwsThruDateId");
		makeDatePicker("cacFromDateId","cacThruDateId");
		makeDatePicker("cacdFromDateId","cacdThruDateId");
		makeDatePicker("subGheeFromDateId","subGheeThruDateId");
		makeDatePicker("crInstFromDateId","crInstThruDateId");
		
		
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
<#if screenFlag?exists && screenFlag.equals("DailyReports")  && (security.hasEntityPermission("BYPRODUCTS", "_DAILREPOR", session) || security.hasEntityPermission("ACCOUNTING", "_CASHIER", session))>
<div class="full">
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h2><center>Daily Reports</center></h2>
    </div>
    <div class="screenlet-body">
    
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
      <#if security.hasEntityPermission("BYPRODUCTS", "_MKTREPOR", session)>
      <#--
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
	        </tr> -->
	        <tr class="alternate-row">
	      	   	<form id="RouteQuantityAbst" name="RouteQuantityAbst" method="post" action="<@ofbizUrl>routeWiseQtyAbstract.txt</@ofbizUrl>" target="_blank">	
	      		  <td width="30%">Route Wise Sales Abstract</td>
	      		  <td width="15%">From<input  type="text" size="18pt" id="rtFromDateId" readonly  name="fromDate"/>
	      		  <td width="15%">Thru<input  type="text" size="18pt" id="rtThruDateId" readonly  name="thruDate"/>
	      		  <td width="15%"><input type="checkbox" name="summeryOnly" value="summeryOnly">Summary Report Only<br></td>
	      		  <td width="15%"></td>
	      	   	  <td width="10%"><input type="submit" target="_blank" value="Download" class="buttontext"/></td>
	      	   </form>
      		</tr>
      	<#--
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
      	</tr> -->
      
      	 <tr class="alternate-row">
            <form id="BoothWiseSales" name="BoothWiseSales" method="post" action="<@ofbizUrl>boothWiseSales.csv</@ofbizUrl>" target="_blank">	
      			<td width="30%">Booth Wise Sales</td>
      			<td width="15%">From<input  type="text" size="18pt" id="bsFromDateId" readonly  name="bsFromDate"/>
      			<td width="15%">Thru<input  type="text" size="18pt" id="bsThruDateId" readonly  name="bsThruDate"/>
      			<td width="15%">Type 
					<select name='subscriptionTypeId'>
					<option value='AM'>AM</option>
					<option value='PM'>PM</option>
					</select>
				</td>
				<td width="15%">&#160;</td>
				<td width="10%"><input type="submit" value="Download" class="buttontext"></td> 
      			
      	  </form>
         </tr> 	
         <#-- <tr>
            <form id="CategoryWiseSales" name="CategoryWiseSales" method="post" action="<@ofbizUrl>categoryWiseSales.txt</@ofbizUrl>" target="_blank">	
      			<td width="34%">Category Wise Sales</td>
      			<td width="33%">From<input  type="text" size="18pt" id="cwsFromDateId" readonly  name="cwsFromDate"/>
      			<td width="33%">Thru<input  type="text" size="18pt" id="cwsThruDateId" readonly  name="cwsThruDate"/>
      		    <input type="submit" value="Download" class="buttontext"/></td>
      	  </form>
         </tr> 
       <tr>
      		<form id="indentVsDispatchReport" name="indentVsDispatchReport" method="post" action="<@ofbizUrl>IndentVsDispatchReport.pdf</@ofbizUrl>" target="_blank">	
      			<td width="34%">Indent vs Despatch</td>
      			<td width="33%">From<@htmlTemplate.renderDateTimeField name="indentDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="18" maxlength="22" id="indentDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/></td>       			
      			<td width="33%">Thru<@htmlTemplate.renderDateTimeField name="indentThruDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="18" maxlength="22" id="indentThruDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/><input type="submit" value="Download" class="buttontext"/></td>
      		</form>
      	</tr> 	-->
      	 <tr class="alternate-row">
        	<form id="TruckSheetCorrectionsReport" name="TruckSheetCorrectionsReport" method="post"  target="_blank" action="<@ofbizUrl>TruckSheetCorrectionsReport</@ofbizUrl>">	
      			<td width="30%">TruckSheet Corrections Report </td>
      			<td width="15%">Date<input  type="text" size="18pt" id="RouteTrCorrDate" readonly  name="supplyDate"></td>
      			<#--><input type="checkbox" name="summeryOnly" value="summeryOnly">Summary Report Only<br> -->
      			<td width="15%">Route 
				 	<select name="routeId" class='h4'>
						<option value='All-Routes'>All</option>
						<#list routesList as route>    
  	    					<option value='${route}'>${route}</option>
						</#list> 
					</select>
				</td>	
				<td width="15%">Type 
					<select name='subscriptionTypeId'>
						<option value='ALL'>All</option>
						<option value='AM'>AM</option>
						<option value='PM'>PM</option>
					</select>
				</td>
				<td width="15%"></td>	
				<td width="10%"><input type="submit" value="Download" class="buttontext"/></td>
      		</form>	
        </tr>
        <tr class="alternate-row">
        	<form id="TruckSheetDetailReport" name="TruckSheetDetailReport" method="post"  target="_blank" action="<@ofbizUrl>TruckSheetDetailReport.csv</@ofbizUrl>">	
      			<td width="30%">TruckSheet Detail Report </td>
      			<td width="15%">Date<input  type="text" size="18pt" id="RouteTrDetDate" readonly  name="supplyDate"/>
      			<#--><input type="checkbox" name="summeryOnly" value="summeryOnly">Summary Report Only<br> -->
      			</td>			
      			<td width="15%">Route Type 
					<select name='subscriptionTypeId' class='h4'>
					<option value='ALL'>All</option>
					<option value='AM'>AM</option>
					<option value='PM'>PM</option>
					</select>
				</td>
				<td width="15%"></td>
				<td width="15%"></td>	
				<td width="10%"><input type="submit" value="Download" class="buttontext"/></td>
      		</form>	
        </tr>
        <tr class="alternate-row">
        	<form id="FacilityDetailReport" name="FacilityDetailReport" method="post"  target="_blank" action="<@ofbizUrl>RetailerDetailReport.csv</@ofbizUrl>">	
      			<td width="30%">Retailer Detail Report </td>
      			<td width="15%">Date<input  type="text" size="18pt" id="retailerDetailDate" readonly  name="supplyDate"/></td>			
      			<td width="15%"></td>
				<td width="15%"></td>
				<td width="15%"></td>	
				<td width="10%"><input type="submit" value="Download" class="buttontext"/></td>
      		</form>	
        </tr>
        <tr class="alternate-row">
      		<form id="ProductReturnsReport" name="ProductReturnsReport" method="post" action="<@ofbizUrl>productReturnsReport.txt</@ofbizUrl>" target="_blank">	
      			<td width="30%">Product Returns Report</td>
      			<td width="15%">Date<input  type="text" size="18pt" id="prodReturnDateId" readonly  name="prodReturnDate"/></td>
      			<td width="15%">Route 
				 	<select name="routeId" class='h4'>
						<option value='All-Routes'>All</option>
						<#list routesList as route>    
  	    					<option value='${route}'>${route}</option>
						</#list> 
					</select>
				</td>
				<td width="15%">Type 
					<select name='subscriptionTypeId' class='h4'>
					<option value='ALL'>All</option>
					<option value='AM'>AM</option>
					<option value='PM'>PM</option>
					</select>
				</td>
				<td width="15%"></td>
				<td width="10%"><input type="submit" value="Download" class="buttontext"/></td>
      		</form>
      	</tr> 
      	<tr class="alternate-row">
      		<form id="SalesReport" name="SalesReport" method="post" action="<@ofbizUrl>salesReport.txt</@ofbizUrl>" target="_blank">	
      			<td width="30%">Sales Report</td>
      			<td width="15%">From<input  type="text" size="18pt" id="saleFromDateId" readonly  name="fromDate"/></td>
      			<td width="15%">Thru<input  type="text" size="18pt" id="saleThruDateId" readonly  name="thruDate"/></td>
      			<td width="15%"></td>
      			<td width="15%"></td>
				<td width="15%"><input type="submit" value="Download" class="buttontext"/></td>       			
      		</form>
      	</tr>
		<tr class="alternate-row">
			<form id="FieldOfficerWiseSalesReport" name="FieldOfficerWiseSalesReport" method="post" action="<@ofbizUrl>FieldOfficerWiseSalesReport.pdf</@ofbizUrl>" target="_blank">	
				<td width="30%">Field Officer wise Sales Report</td>
				<td width="15%">From<input  type="text" size="18pt" id="fwsFromDateId" readonly  name="fromDate"/></td>
	  			<td width="15%">To<input  type="text" size="18pt" id="fwsThruDateId" readonly  name="thruDate"/></td>
	  			<td width="15%"></td>
      			<td width="15%"></td>
				<td width="10%"><input type="submit" value="Download" class="buttontext"/></td>        			
			</form>
		</tr>
      	 <tr class="alternate-row">
        	<form id="DealerBankChallan" name="DealerBankChallan" method="post"  target="_blank" action="<@ofbizUrl>DealerBankChallan.txt</@ofbizUrl>">	
      			<td width="30%">Dealer BankChallan Report </td>
      			<td width="15%">Date<input  type="text" size="18pt" id="DBCFromDateId" readonly  name="supplyDate"/></td>
      			<#-->
      			<td width="33%">Date<@htmlTemplate.renderDateTimeField name="supplyDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="18" maxlength="22" id="bankChallanSupplyDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/></td> -->
      			<td width="15%">Route <select name="routeId" class='h4'><option value='All-Routes'>All-Routes</option>
					<#list routesList as route>    
  	    				<option value='${route}'>${route}</option>
					</#list>  
					</select>
				</td>
				<td width="15%"></td>
      			<td width="15%"></td>
				<td width="10%"><input type="submit" value="Download" class="buttontext"/></td>
      		</form>	
        </tr>  
        </#if>
        <#if security.hasEntityPermission("BYPRODUCTS", "_DISREPORT", session) || security.hasEntityPermission("BYPRODUCTS", "_MKTREPOR", session)>
        	
      	<tr class="alternate-row">
			<form id="CategoryWiseSalesReport" name="CategoryWiseSalesReport" method="post" action="<@ofbizUrl>CategoryWiseSalesReport.pdf</@ofbizUrl>" target="_blank">	
				<td width="30%">CategoryWise Sales Report</td>
				<td width="15%">From<input  type="text" size="18pt" id="catSalesFromDateId" readonly  name="fromDate"/></td>
	  			<td width="15%">To<input  type="text" size="18pt" id="catSalesThruDateId" readonly  name="thruDate"/></td>
	  			<td width="15%"></td>
      			<td width="15%"></td>
				<td width="10%"><input type="submit" value="Download" class="buttontext"/></td>        			
			</form>
		</tr>
        <tr class="alternate-row">
        	<form id="IndentAbstractReport" name="IndentAbstractReport" method="post"  target="_blank" action="<@ofbizUrl>IndentAbstractReport</@ofbizUrl>">	
      			<td width="30%">RouteWise Indent Abstract Report </td>
      			<td width="15%">Date<input  type="text" size="18pt" id="RouteIndentAbstDate" readonly  name="supplyDate"/></td>
      			<td width="15%"><input type="checkbox" name="summeryOnly" value="summeryOnly">Summary Report Only<br>
      			</td>			
      			<td width="15%">Route 
				 	<select name="routeId" class='h4'>
						<option value='All-Routes'>All</option>
						<#list routesList as route>    
  	    				<option value='${route}'>${route}</option>
						</#list> 
					</select>
				</td>
				<td width="15%">Type 
					<select name='subscriptionTypeId' class='h4'>
					<option value=''>All</option>
					<option value='AM'>AM</option>
					<option value='PM'>PM</option>
					</select>
				</td>
				<td width="10%"><input type="submit" value="Download" class="buttontext"/></td>
      		</form>	
        </tr>
        <tr class="alternate-row">
        	<form id="ProductIndentAbstractReport" name="ProductIndentAbstractReport" method="post"  target="_blank" action="<@ofbizUrl>ProductIndentAbstractReport</@ofbizUrl>">	
      			<td width="30%">Product Indent Abstract Report</td>
      			<td width="15%">Date<input  type="text" size="18pt" id="ProductIndentAbstDate" readonly  name="supplyDate"/></td>
      			<td width="15%"><input type="checkbox" name="summeryOnly" value="summeryOnly">Summary Report Only</td>
      			<td width="15%">Route 
				 	<select name="routeId" class='h4'>
					<option value='All-Routes'>All</option>
					<#list routesList as route>    
  	    				<option value='${route}'>${route}</option>
					</#list> 
					</select>
				</td>
				<td width="15%">Type 
					<select name='subscriptionTypeId' class='h4'>
					<option value=''>All</option>
					<option value='AM'>AM</option>
					<option value='PM'>PM</option>
					</select>
				</td>
				<td width="10%"><input type="submit" value="Download" class="buttontext"/></td>
      		</form>	
        </tr>
     </#if>
     <#if security.hasEntityPermission("BYPRODUCTS", "_ACTGRPRT", session)> 
     <#--
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
      	</tr> -->
      	<tr class="alternate-row">
        	<form id="DailyPaymentCheckList" name="DailyPaymentCheckList" method="post"  target="_blank" action="<@ofbizUrl>DailyPaymentCheckList</@ofbizUrl>">	
      			<td width="30%">Daily Payments Report<input  type="hidden"  value="DailyPaymentCheckList"   name="reportTypeFlag"/>
      				<input  type="hidden"  value="All"   name="routeId"/>
      			</td>
      			<td width="15%">Date<input  type="text" size="18pt" id="CLRFromDateId" readonly  name="paymentDate"/></td>
      			<td width="15%">SearchBy 
					<select name="searchBy" class='h4'>
					<option value='findByCreatedDate'>EntryDate</option>
					<option value='findByPaymentDate'>PaymentDate</option>
					</select>
				</td>
      			<#-->
      			Route 
				<select name="routeId" class='h4'>
				<option value='All'>All </option>
					<#list routesList as route>    
  	    				<option value='${route}'>${route}</option>
					</#list>  
					</select> -->
      			<td width="15%">PaymentType 
					<select name="paymentMethodTypeId" class='h4'>
						<#list paymentMethodList as paymentMethod>   
	  	    				<option value='${paymentMethod.paymentMethodTypeId}'>${paymentMethod.description}</option>
						</#list> 
					</select>
				</td>
				<td width="15%"></td>
					<#--><input type="submit" value="Download" class="buttontext"/>-->
				<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('DailyPaymentCheckList', '<@ofbizUrl>DailyPaymentCheckList.pdf</@ofbizUrl>');" class="buttontext"/>
					<input type="submit" value="CSV" onClick="javascript:appendParams('DailyPaymentCheckList', '<@ofbizUrl>DailyPaymentCheckListCsv.csv</@ofbizUrl>');" class="buttontext"/>
					</td>
      		</form>	
        </tr>
        <tr class="alternate-row">
        	<form id="DailyUndepositedCheckList" name="DailyUndepositedCheckList" method="post"  target="_blank" action="<@ofbizUrl>DailyPaymentCheckList.pdf</@ofbizUrl>">	
      			<td width="30%">Daily Undeposited/Deposited Cheques Report<input  type="hidden"  value="DailyPaymentCheckList"   name="reportTypeFlag"/>
      				<input  type="hidden"  name="paymentMethodTypeId"   value="CHEQUE_PAYIN"   />
      				<input  type="hidden" name="routeId"  value="All" />
      			</td>
      			<td width="15%">From<input  type="text" size="18pt" id="DUCRFromDateId" readonly  name="paymentDate"/></td>
	      		<td width="15%">Thru<input  type="text" size="18pt" id="DUCRThruDateId" readonly  name="thruDate"/></td>
      			<td width="15%">Type
					<select name="unDepositedCheques" class='h4'>
						<option value='TRUE'>UnDeposited</option>
						<option value='FALSE'>Deposited</option>
					</select>
				</td>
				<td width="15%"></td>
				<td width="10"><input type="submit" value="Download" class="buttontext"/></td>
      		</form>	
        </tr>
     </#if>
      <#if security.hasEntityPermission("TRNSMRGN", "_VIEW", session)>
					 	<tr class="alternate-row">
							<form id="CratesAndCansReport" name="CratesAndCansReport" method="post" action="<@ofbizUrl>CratesAndCansReport.pdf</@ofbizUrl>" target="_blank">	
								<td width="30%">Crates And Cans Report</td>
								<td width="15%">From<input  type="text" size="18pt" id="cacFromDateId" readonly  name="fromDate"/></td>
					  			<td width="15%">To<input  type="text" size="18pt" id="cacThruDateId" readonly  name="thruDate"/></td>
					  			<td width="15%"></td>
				      			<td width="15%"></td>
								<td width="10%"><input type="submit" value="Download" class="buttontext"/></td>        			
							</form>
						</tr>
						<tr class="alternate-row">
							<form id="CratesAndCansDayWiseReport" name="CratesAndCansDayWiseReport" method="post" action="<@ofbizUrl>CratesAndCansDayWiseReport.pdf</@ofbizUrl>" target="_blank">	
								<td width="30%">Crates And Cans Detailed(DayWise) Report</td>
								<td width="15%">From<input  type="text" size="18pt" id="cacdFromDateId" readonly  name="fromDate"/></td>
					  			<td width="15%">To<input  type="text" size="18pt" id="cacdThruDateId" readonly  name="thruDate"/></td>
					  			<td width="15%"></td>
				      			<td width="15%"></td>
								<td width="10%"><input type="submit" value="Download" class="buttontext"/></td>        			
							</form>
						</tr>
		</#if>
      	 <#if security.hasEntityPermission("ACCOUNTING", "_CASHIER", session)>
        <tr class="alternate-row">
        	<form id="CashPaymentCheckList" name="CashierPaymentCheckList" method="post"  target="_blank" action="<@ofbizUrl>CashierPaymentCheckList</@ofbizUrl>">	
      			<td width="30%">Cash Payment CheckList Report<input  type="hidden"  value="CashPaymentCheckList"   name="reportTypeFlag"/> 
      				<input  type="hidden"  value="CASH_PAYIN"   name="paymentMethodTypeId"/>
      			</td>
      			<td width="15%">Date<input  type="text" size="18pt" id="CASHFromDateId" readonly  name="paymentDate"/></td>
      			<td width="15%">
      			    <input  type="hidden"  name="routeId" value="All"/>
      			</td>
      			<td width="15%"></td>
      			<td width="15%"></td>
				<td width="10%"><input type="submit" value="Download" class="buttontext"/></td>
			</form>	
          </tr>
           <tr class="alternate-row">
        	<form id="CashPaymentTranscationList" name="CashierPaymentTransactionList" method="post"  target="_blank" action="<@ofbizUrl>CashierPaymentReceiptList</@ofbizUrl>">	
      			<td width="30%">Cash PaymentGroup CheckList Report<input  type="hidden"  value="CashPaymentCheckList"   name="reportTypeFlag"/> 
      				<input  type="hidden"  value="CASH_PAYIN"   name="paymentMethodTypeId"/>
      			</td>
      			<td width="15%">Date<input  type="text" size="18pt" id="CashReceiptDateId" readonly  name="paymentDate"/></td>
      			<td width="15%">
      			    <input  type="hidden"  name="routeId" value="All"/>
      			</td>
      			<td width="15%"></td>
      			<td width="15%"></td>
				<td width="10%"><input type="submit" value="Download" class="buttontext"/></td>
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
       <tr class="alternate-row">
      		<form id="sendSMSNotify" name="sendSMSNotify" method="post" action="<@ofbizUrl>sendSMSNotification</@ofbizUrl>">	
      			<td width="34%">Product Sale Notification SMS</td>
      			<td width="30%">Date<input  type="text" size="18pt" id="smsNotify" readOnly  name="supplyDate"/></td>
      			<td width="33%"><select name="subscriptionTypeId" id="subscriptionTypeId" class='h4'>
					<option value='AM'>AM</option><option value='PM'>PM</option></select>
					<input type="submit" value="Send SMS" class="buttontext"/></td>		
      		</form>
      	</tr>
      <!--	<tr>
      	   <form id="sendIndentSmsBulk" name="sendIndentSmsBulk" method="post" action="<@ofbizUrl>sendIndentSmsBulk</@ofbizUrl>" target="_blank">	
      		  <td width="33%">Indent Bulk Sms</td>
      		    <td>
      		      Route:<select name="routeId" class='h4'><option value=''></option>
					<#list routesList as route>    
  	    				<option value='${route}'>${route}</option>
					</#list>  
				</select>
      		 </td>
      	   	  <td width="33%"> SupplyTime:
				<select name="subscriptionTypeId" class='h4'>
					<#list subscriptionTypeList as subscriptionType>    
  	    				<option value='${subscriptionType.subscriptionTypeId}'>${subscriptionType.description}</option>
					</#list>            
				</select>
      	   	   Date<input type="text" name="supplyDate" id="bulkSmsDate" size="15"/>
      	   	  <input type="submit" target="_blank" value="send" class="buttontext"/></td>
      	   </form>
      	</tr>	-->
      </table>
	 </div>
    </div>
    </#if>
   
</div><!-- left half Div End -->


</#if>

<#if screenFlag?exists && screenFlag.equals("MonthlyReports")  && (security.hasEntityPermission("BYPRODUCTS", "_MNTHREPOR", session)|| security.hasEntityPermission("TRNSMRGN", "_VIEW", session))>
<div class="full">

<div class="screenlet">
    <div class="screenlet-title-bar">
      <h2><center>Monthly Reports</center></h2>
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
      				 <#if security.hasEntityPermission("BYPRODUCTS", "_MNTHREPOR", session)>
      				 <tr class="alternate-row">
			      	   <form id="paymentOBandCB" name="paymentOBandCB" method="post" action="<@ofbizUrl>PartywiseBalanceAbstract.pdf</@ofbizUrl>" target="_blank">        
			             <td width="30%">Partywise Ledger Abstract</td>
			             <td width="15%">From<input  type="text" size="10pt" id="effFromDate" readonly  name="fromDate"/></td>
			      		 <td width="15%">Thru<input  type="text" size="10pt" id="effThruDate" readonly  name="thruDate"/></td>
			             <td width="15%">Retailer Code <input type="text" name="boothId" id="boothId" size="10" maxlength="22"></td>
			             <td width="15%"></td>
			             <td width="10%"><input type="submit" value="Download" class="buttontext"/></td>
			           </form>
			        </tr>
			        
			        <#-- <tr class="alternate-row">
			      	   <form id="RetailerLedgerAbstract" name="RetailerLedgerAbstract" method="post" action="<@ofbizUrl>RetailerLedgerAbstract.pdf</@ofbizUrl>" target="_blank">        
			             <td width="30%">Retailer Ledger Abstract(Incl Products)</td>
			             <td width="15%" >From<input  type="text" size="10pt" id="RLAFromDateId" readonly  name="fromDate"/></td>
					     <td width="15%">Thru<input  type="text" size="10pt" id="RLAThruDateId"  readonly name="thruDate"/></td>
			             <td width="15%">Retailer Code <input type="text" name="boothId" id="boothId" size="10" maxlength="22"></td>
			             <td width="15%"></td>
			             <td width="10%"><input type="submit" value="Download" class="buttontext"/></td>
			           </form>
			        </tr>--> 
	  				<tr class="alternate-row">
	  					<form id="ShoppeRentReport" name="ShoppeRentReport" method="post" action="<@ofbizUrl>ShoppeRentReport.pdf</@ofbizUrl>" target="_blank">	
	  						<td width="30%">Shoppe Rent Report</td>
	  						<td width="15%">Period
	  							<select name="customTimePeriodId" class='h4'>
	            					<#list shopeeTimePeriodList as timePeriod>    
	              	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.fromDate}-${timePeriod.thruDate}</option>
	            					</#list>            
								</select>
	      					</td>
	      					<td width="15%"></td>
	      					<td width="15%"></td>
	      					<td width="15%"></td>
	      					<td width="10%"><input type="submit" value="Download" class="buttontext"/></td>
	  					</form>
	  				</tr>
	  				<tr class="alternate-row">
      					<form id="ChequeReturnsReport" name="ChequeReturnsReport" method="post" action="<@ofbizUrl>chequeReturnReport.pdf</@ofbizUrl>" target="_blank">	
	      					<td width="30%">Cheque Returns Report</td>
	      					<td width="15%">From<input  type="text" size="18pt" id="chequeFromDateId" readonly  name="fromDate"/></td>
	      					<td width="15%">Thru<input  type="text" size="18pt" id="chequeThruDateId" readonly  name="thruDate"/></td>
	      					<td width="15%">By<select name="dateType"><option value="PAYMENT_DATE">Payment Date</option><option value="BOUNCE_DATE">Bounce Date</option></select></td>
	      					<td width="15%"></td>
							<td width="10%"><input type="submit" value="Download" class="buttontext"/></td>       			
	      				</form>
      				</tr>
      				<tr class="alternate-row">
      					<form id="DuesFDRReport" name="DuesFDRReport" method="post" action="<@ofbizUrl>duesDiffFDR.pdf</@ofbizUrl>" target="_blank">	
      						<td width="30%">Dues in excess of FDR</td>
      						<td width="15%">Date<input  type="text" size="18pt" id="FDRDateId" readonly  name="effectiveDate"/></td>
      						<td width="15%"></td>
	      					<td width="15%"></td>
	      					<td width="15%"></td>
							<td width="10%"><input type="submit" value="Download" class="buttontext"/></td>       			
      					</form>
      				</tr>
      				<#--<tr class="alternate-row">
      					<form id="DuesFDRReport" name="RTMaterialBalance" method="post" action="<@ofbizUrl>RMMaterialBalance.csv</@ofbizUrl>" target="_blank">	
      						<td width="30%">Route Marketing Material Balance Report</td>
      						<td width="15%">From<input  type="text" size="18pt" id="materialFromDateId" readonly  name="fromDate"/></td>
      						<td width="15%">Thru<input  type="text" size="18pt" id="materialThruDateId" readonly  name="thruDate"/></td>
	      					<td width="15%"></td>
	      					<td width="15%"></td>
							<td width="10%"><input type="submit" value="Download" class="buttontext"/></td>       			
      					</form>
      				</tr>-->
			        <tr class="alternate-row">
						<form id="MonthlyVatReport" name="MonthlyVatReport" method="post" action="<@ofbizUrl>MonthlyVatReport.pdf</@ofbizUrl>" target="_blank">	
							<td width="30%">Monthly Vat Report</td>
							<td width="15%">From<input  type="text" size="18pt" id="vatFromDateId" readonly  name="fromDate"/></td>
			      			<td width="15%">Thru<input  type="text" size="18pt" id="vatThruDateId" readonly  name="thruDate"/></td>
							<td width="15%"></td>
	      					<td width="15%"></td>
							<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('MonthlyVatReport', '<@ofbizUrl>MonthlyVatReport.pdf</@ofbizUrl>');" class="buttontext"/>
							<input type="submit" value="CSV" onClick="javascript:appendParams('MonthlyVatReport', '<@ofbizUrl>MonthlyVatReport.csv</@ofbizUrl>');" class="buttontext"/></td>        			
						</form>
					</tr>
					<#if security.hasEntityPermission("SUBSIDY", "_VIEW", session)>
					<tr class="alternate-row">
						<form id="SubsidyMilkReport" name="SubsidyMilkReport" method="post" action="<@ofbizUrl>SubsidyMilkReport.pdf</@ofbizUrl>" target="_blank">	
							<td width="30%">Subsidy Milk Report</td>
							<td width="15%">From<input  type="text" size="18pt" id="subsidyFromDateId" readonly  name="fromDate"/></td>
			      			<td width="15%">Thru<input  type="text" size="18pt" id="subsidyThruDateId" readonly  name="thruDate"/></td>
							<td width="15%"></td>
	      					<td width="15%"></td>
							<td width="10%"><input type="submit" value="Download" class="buttontext"/></td>    			
						</form>
					</tr>
					</#if>
					<tr class="alternate-row">
						<form id="SubsidyGheeReport" name="SubsidyGheeReport" method="post" action="<@ofbizUrl>SubsidyGheeReport.pdf</@ofbizUrl>" target="_blank">	
							<td width="30%">Subsidy Ghee Report</td>
							<td width="15%">From<input  type="text" size="18pt" id="subGheeFromDateId" readonly  name="fromDate"/></td>
			      			<td width="15%">Thru<input  type="text" size="18pt" id="subGheeThruDateId" readonly  name="thruDate"/></td>
							<td width="15%"></td>
	      					<td width="15%"></td>
							<td width="10%"><input type="submit" value="Download" class="buttontext"/></td>    			
						</form>
					</tr>
					<tr class="alternate-row">
						<form id="CrInstAbstReport" name="CrInstAbstReport" method="post" action="<@ofbizUrl>crInstAbstractReport.pdf</@ofbizUrl>" target="_blank">	
							<td width="30%">Credit Institution Abstract Report</td>
							<td width="15%">From<input  type="text" size="18pt" id="crInstFromDateId" readonly  name="fromDate"/></td>
			      			<td width="15%">Thru<input  type="text" size="18pt" id="crInstThruDateId" readonly  name="thruDate"/></td>
							<td width="15%"></td>
	      					<td width="15%"></td>
							<td width="10%"><input type="submit" value="Download" class="buttontext"/></td>    			
						</form>
					</tr>
					<#--<tr class="alternate-row">
      					<form id="dueParticulars" name="dueParticulars" method="post" action="<@ofbizUrl>dueParticulars.txt</@ofbizUrl>" target="_blank">	
      						<td width="30%">Due Particulars<input  type="hidden"  value="DuesParticulers"   name="reportTypeFlag"/></td>
							<td width="15%">From<input  type="text" size="18pt" id="DueFromDateId" readonly  name="fromDate"/></td>
			      			<td width="15%">Thru<input  type="text" size="18pt" id="DueThruDateId" readonly  name="thruDate"/></td>
			      			<td width="15%"></td>
	      					<td width="15%"></td>  
							<td width="10%"><input type="submit" value="Download" class="buttontext"/></td>
      					</form>
      				</tr>-->
      				<tr class="alternate-row">
      					<form id="DuesAbstractReport" name="DuesAbstractReport" method="post" action="<@ofbizUrl>dueParticulars.pdf</@ofbizUrl>" target="_blank">	
      						<td width="30%">Dues Abstract Report with OB
      							<input  type="hidden"  value="DuesAbstractReport"   name="reportTypeFlag"/></td>
							<td width="15%">From<input  type="text" size="18pt" id="DueAbsFromDateId" readonly  name="fromDate"/></td>
			      			<td width="15%">Thru<input  type="text" size="18pt" id="DueAbsThruDateId" readonly  name="thruDate"/></td>
			      			<td width="15%">Category:<select name="categoryTypeEnum" id="categoryTypeEnum" class='h4'>
								<option value="">All Types</option>
		                		<#list categoryTypeList as categoryType>    
		                  	    	<option value='${categoryType.enumId}'>
			                    		${categoryType.description}
			                        </option>
		                		</#list>            
								</select>
			      			</td>
	      					<td width="15%"></td>  
							<td width="10%"><input type="submit" value="Download" class="buttontext"/></td>
      					</form>
      				</tr>
					</#if>
					<#if security.hasEntityPermission("TRNSMRGN", "_VIEW", session)>
					 <tr class="alternate-row">
						<form id="DTCCostReport" name="DTCCostReport" method="post" action="<@ofbizUrl>DTCCostReport.pdf</@ofbizUrl>" target="_blank">	
							<td width="30%">DTC Cost Report</td>
							<td width="15%">Month: <input type='text' id='month' name='month' onmouseover='monthPicker()' class="monthPicker"/></td>
	      					<td width="15%"></td>
	      					<td width="15%"></td>
	      					<td width="15%"></td>
	      					<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('DTCCostReport', '<@ofbizUrl>DTCCostReport.pdf</@ofbizUrl>');" class="buttontext"/>
							<input type="submit" value="CSV" onClick="javascript:appendParams('DTCCostReport', '<@ofbizUrl>DTCCostReport.csv</@ofbizUrl>');" class="buttontext"/></td>         			
						</form>
					</tr>
					<tr class="alternate-row">
						<form id="DTCBankReport" name="DTCBankReport" method="post" action="<@ofbizUrl>DTCBankReport.pdf</@ofbizUrl>" target="_blank">	
							<td width="30%">DTC Bank Report<input  type="hidden"  value="DTCBankReport"   name="reportTypeFlag"/></td>
							<td width="15%">Bank
	  							<select name="finAccountId" class='h4'>
	              	    				<option value='FIN_ACCNT4'>CORPORATION BANK YELAHANKA</option>
	              	    				<option value='FIN_ACCNT7'>AXIS BANK, YNK BRANCH</option>
								</select>
	      					</td>
							<td width="15%">Period
	  							<select name="customTimePeriodId" class='h4'>
	            					<#list dtcTimePeriodList as timePeriod>    
	              	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.fromDate}-${timePeriod.thruDate}</option>
	            					</#list>            
								</select>
	      					</td>
	      					<td width="15%"></td>
	      					<td width="15%"></td>
	      					<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('DTCBankReport', '<@ofbizUrl>DTCBankReport.pdf</@ofbizUrl>');" class="buttontext"/>
							<input type="submit" value="CSV" onClick="javascript:appendParams('DTCBankReport', '<@ofbizUrl>DTCBankReport.csv</@ofbizUrl>');" class="buttontext"/></td>         			
						</form>
					</tr>
					<tr class="alternate-row">
						<form id="DTCTransporterBankReport" name="DTCTransporterBankReport" method="post" action="<@ofbizUrl>DTCBankReport.pdf</@ofbizUrl>" target="_blank">	
							<td width="30%">Transporter Bank Report<input  type="hidden"  value="DTCTransporterReport"   name="reportTypeFlag"/></td>
							<td width="15%">Bank
	  							<select name="finAccountId" class='h4'>
	              	    				<option value='FIN_ACCNT4'>CORPORATION BANK YELAHANKA</option>
	              	    				<option value='FIN_ACCNT7'>AXIS BANK, YNK BRANCH</option>
								</select>
	      					</td>
							<td width="15%">Period
	  							<select name="customTimePeriodId" class='h4'>
	            					<#list dtcTimePeriodList as timePeriod>    
	              	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.fromDate}-${timePeriod.thruDate}</option>
	            					</#list>            
								</select>
	      					</td>
	      					<td width="15%"></td>
	      					<td width="15%"></td>
	      					<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('DTCTransporterBankReport', '<@ofbizUrl>DTCBankReport.pdf</@ofbizUrl>');" class="buttontext"/>
							<input type="submit" value="CSV" onClick="javascript:appendParams('DTCTransporterBankReport', '<@ofbizUrl>DTCBankReport.csv</@ofbizUrl>');" class="buttontext"/></td>         			
						</form>
					</tr>
					<tr class="alternate-row">
						<form id="DTCFinesAndPenaltiesReport" name="DTCFinesAndPenaltiesReport" method="post" action="<@ofbizUrl>DTCFinesAndPenaltiesReport.pdf</@ofbizUrl>" target="_blank">	
							<td width="30%">DTC Fines And Penalties Report</td>
							<td width="15%">Period
	  							<select name="customTimePeriodId" class='h4'>
	            					<#list dtcTimePeriodList as timePeriod>    
	              	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.fromDate}-${timePeriod.thruDate}</option>
	            					</#list>            
								</select>
	      					</td>
	      					<td width="15%"></td>
	      					<td width="15%"></td>
	      					<td width="15%"></td>
	      					<td width="10%"><input type="submit" value="Download" class="buttontext"/></td> 
	      				</form>
					</tr>
					<tr class="alternate-row">
						<form id="DTCBillingReport" name="DTCBillingReport" method="post" action="<@ofbizUrl>DTCBillingReport.pdf</@ofbizUrl>" target="_blank">	
							<td width="30%">DTC Contractor Agreements Ending</td>
							<td width="15%">Period
	  							<select name="customTimePeriodId" class='h4'>
	            					<#list dtcTimePeriodList as timePeriod>    
	              	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.fromDate}-${timePeriod.thruDate}</option>
	            					</#list>            
								</select>
	      					</td>
	      					<td width="15%"></td>
	      					<td width="15%"></td>
	      					<td width="15%"></td>
	      					<td width="10%"><input type="submit" value="Download" class="buttontext"/></td> 
	      				</form>
					</tr>
					<tr class="alternate-row">
						<form id="PCMReport" name="PCMReport" method="post" action="<@ofbizUrl>PCMReport.pdf</@ofbizUrl>" target="_blank">	
							<td width="30%">PCM Report</td>
							<td width="15%">Period
	  							<select name="customTimePeriodId" class='h4'>
	            					<#list shopeeTimePeriodList as timePeriod>    
	              	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.fromDate}-${timePeriod.thruDate}</option>
	            					</#list>            
								</select>
	      					</td>
	      					<td width="15%"></td>
	      					<td width="15%"></td>
	      					<td width="15%"></td>
	      					<td width="10%"><input type="submit" value="Download" class="buttontext"/></td> 
	      				</form>
					</tr>
					<tr class="alternate-row">
						<form id="newOrTerminateRtlReport" name="newOrTerminateRtlReport" method="post" action="<@ofbizUrl>newOrTerminateRtlReport.pdf</@ofbizUrl>" target="_blank">	
							<td width="30%">New Or Terminated Retailers</td>
							<td width="15%">Period
	  							<select name="customTimePeriodId" class='h4'>
	            					<#list shopeeTimePeriodList as timePeriod>    
	              	    				<option value='${timePeriod.customTimePeriodId}'>${timePeriod.fromDate}-${timePeriod.thruDate}</option>
	            					</#list>            
								</select>
	      					</td>
	      					<td width="15%"></td>
	      					<td width="15%"></td>
	      					<td width="15%"></td>
	      					<td width="10%"><input type="submit" value="Download" class="buttontext"/></td> 
	      				</form>
					</tr>
					</#if>
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
      	
</div>
</div>
</#if>
  </div>