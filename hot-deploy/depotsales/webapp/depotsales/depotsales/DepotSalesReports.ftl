
<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
<script type= "text/javascript">
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
	        	maxDate.setDate(maxDate.getDate() + 335);
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

$(document).ready(function(){

	    makeDatePicker1("regularIceCreamfDate","regularIceCreamtDate");
	    makeDatePicker1("regularIceCreamfDate","regularIceCreamtDate"); 
	    makeDatePicker1("pendingShipmentsReportFrom","pendingShipmentsReportT0");
	    makeDatePicker1("SupplierwiseCountwiseReportDateFrom","SupplierwiseCountwiseReportDateThru");
	    makeDatePicker1("MillwisecountwisePurchaseofYarnReportDateFrom","MillwisecountwisePurchaseofYarnReportDateThru");
	    makeDatePicker1("m1ReportDateFrom","m1ReportDateThru");
	    makeDatePicker1("FCWSReportDateFrom","FCWSReportDateThru");
	    makeDatePicker1("amulIceCreamfDate","amulIceCreamtDate");
	    makeDatePicker1("amulIceCreamfDate","amulIceCreamtDate");
	    makeDatePicker1("stockFromDate","stockThruDate");
	    makeDatePicker1("mobileHitsFromDate","mobileHitsThruDate");
	    makeDatePicker1("ivdFromDate","ivdThruDate");
	    makeDatePicker1("eObFromDate");
	    makeDatePicker2("subsidyFromDate","subsidyThruDate"); 
	    makeDatePicker2("claimFromDateTD","claimThruDateTD");
	    makeDatePicker2("claimFromDate","claimThruDate");
	    makeDatePicker4("PFHFromDateCrDr","PFHThruDateCrDr");
	    makeDatePicker1("reimburcentTransporterFRO","reimburcentTransporterTHRU");
	    makeDatePicker1("depotReimburcentReportFRO","depotReimburcentReportTHRU");
	    makeDatePicker1("stateWiseBranchWiseSaleReportFro","stateWiseBranchWiseSaleReportTHRU");
	    makeDatePicker2("abstrctFromDate","abstrctThruDate");
	    makeDatePicker2("salesPurchaseReportFRO","salesPurchaseReportTHRU");
	    makeDatePicker1("stockDate");
	    makeDatePicker1("AWIORDate");
	    makeDatePicker1("CASHFromDateId","");
	    makeDatePicker1("billWiseSalesReportFrom","billWiseSalesReportThru");
	    makeDatePicker1("billWisePurchaseReportFrom","billWisePurchaseReportThru");
	    makeDatePicker1("IndentRegisterFromDate","IndentRegisterThruDate");
	    makeDatePicker1("IndentRegisterEntryFromDate","IndentRegisterEntryThruDate");
	    makeDatePicker1("purchaseRegisterReportDateFrom","purchaseRegisterReportDateThru");
	    makeDatePicker1("TaxReportFRO","TaxReportTHRU");
	    makeDatePicker1("SalesTaxReportFRO","SalesTaxReportTHRU");  
		$('#ui-datepicker-div').css('clip', 'auto');		
	});
function makeDatePicker3(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat:'yy, MM dd',
			changeMonth: true,
			changeYear: true,
			yearRange: "-20:+0", 
			onSelect: function(selectedDate) {
			$("#"+thruDateId).datepicker( "option", {minDate: selectedDate, maxDate: null}).datepicker('setDate', date);
			}
		});
	$( "#"+thruDateId ).datepicker({
			dateFormat:'yy, MM dd',
			onSelect: function( selectedDate ) {
				//$( "#"+fromDateId ).datepicker( "option", "maxDate", selectedDate );
			}
		});
	}
	
	function makeDatePicker2(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat:'yy, MM dd',
			changeMonth: true,
			changeYear: true,
			yearRange: "-20:+0",
			onSelect: function(selectedDate) {
			date = $(this).datepicker('getDate');
			var maxDate = new Date(date.getTime());
	        	maxDate.setDate(maxDate.getDate() + 366);
				$("#"+thruDateId).datepicker( "option", {minDate: selectedDate, maxDate: maxDate}).datepicker('setDate', date);
				//$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
			}
		});
	$( "#"+thruDateId ).datepicker({
			dateFormat:'yy, MM dd',
			changeMonth: true,
			changeYear: true,
			onSelect: function( selectedDate ) {
				//$( "#"+fromDateId ).datepicker( "option", "maxDate", selectedDate );
			}
		});
	}
	
	function makeDatePicker1(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			changeYear: true,
			yearRange: "-20:+0",
			onSelect: function(selectedDate) {
			date = $(this).datepicker('getDate');
			var maxDate = new Date(date.getTime());
	        	maxDate.setDate(maxDate.getDate() + 366);
				$("#"+thruDateId).datepicker( "option", {minDate: selectedDate, maxDate: maxDate}).datepicker('setDate', date);
				//$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
			}
		});
	$( "#"+thruDateId ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			changeYear: true,
			onSelect: function( selectedDate ) {
				//$( "#"+fromDateId ).datepicker( "option", "maxDate", selectedDate );
			}
		});
	}
	
	function makeDatePicker4(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat:'yy, MM dd',
			changeMonth: true,
			changeYear: true,
			yearRange: "-20:+0", 
			onSelect: function(selectedDate) {
			//$("#"+thruDateId).datepicker( "option", {minDate: selectedDate, maxDate: null}).datepicker('setDate', date);
			}
		});
	$( "#"+thruDateId ).datepicker({
			dateFormat:'yy, MM dd',
			changeMonth: true,
			changeYear: true,
			onSelect: function( selectedDate ) {
				//$( "#"+fromDateId ).datepicker( "option", "maxDate", selectedDate );
			}
		});
	}
	
</script>

<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3></h3>
    </div>
    <div class="screenlet-body">
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;" >  

	    <#if ReportsType?has_content && ReportsType=="MANAGEMENT_REPORTS">
		 
		 <tr class="alternate-row">
		 	<form id="stockreport" name="stockreport" method="post" action="<@ofbizUrl>stockreport.pdf</@ofbizUrl>" target="_blank">
		 	<td width="40%">Stock Report</td>
		 	<td width="15%"><span class='h3'>Branch
				 <select name="branchId" id="branchId">
					<option value=''></option>
				     <#list  formatList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					 </#list> 
				  </select>    								
			  </span></td>
  			<td width="15%">ShipmentId <input  type="text" size="18pt" id="shipmentId" name="shipmentId"/></td>
  			
  			<td width="15%">FacilityId<@htmlTemplate.lookupField size="10" maxlength="22" formName="stockreport" name="facilityId" id="facilityId" fieldFormName="ProductionLookupFacility"/></td>
  			<td width="10%"></td>
		 	<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('stockreport', '<@ofbizUrl>stockreport.pdf</@ofbizUrl>');" class="buttontext"/></td>
			<td width="10%"><input type="submit" value="CSV" onClick="javascript:appendParams('stockreport', '<@ofbizUrl>stockreport.xls</@ofbizUrl>');" class="buttontext"/></td>
		 </form>
		 </tr>
		<tr class="alternate-row">
			<form id="SupplierwiseCountwisePurchaseReport" name="SupplierwiseCountwisePurchaseReport" method="post" action="<@ofbizUrl>SupplierwiseCountwisePurchaseReport.pdf</@ofbizUrl>" target="_blank">	
				<td width="40%">Supplier wise Count wise Purchase Report</td>
				<td width="15%">From</br><input  type="text" size="18pt" id="SupplierwiseCountwiseReportDateFrom" readonly  name="fromDate"/></td>
			    <td width="15%">To</br><input  type="text" size="18pt" id="SupplierwiseCountwiseReportDateThru" readonly  name="thruDate"/></td>
      			<td width="15%"><span class='h3'>Branch
				 <select name="branchId" id="branchId">
					<option value=''></option>
				     <#list  formatList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					 </#list> 
				  </select>    								
			  </span></td>
      			 <td width="15%"><span class='h3'>Category
				    <select name="categoryId" id="categoryId">
				          <option value="ALL">ALL</option>
				          <option value='COTTON'>COTTON</option>
				       	  <option value='SILK'>SILK</option>
				       	  <option value='OTHER'>OTHERS</option>
				    </select>  </span>  								
			  	 </td>
			  	 <td width="10%">
				<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('SupplierwiseCountwisePurchaseReport', '<@ofbizUrl>SupplierwiseCountwisePurchaseReport.pdf</@ofbizUrl>');" class="buttontext"/></td>
				<td width="10%"><input type="submit" value="CSV" onClick="javascript:appendParams('SupplierwiseCountwisePurchaseReport', '<@ofbizUrl>SupplierwiseCountwisePurchaseReport.xls</@ofbizUrl>');" class="buttontext"/></td>         			
				
			</form>
          </tr>

 		<tr class="alternate-row">
			<form id="fiberAndCountWiseSalesReport" name="fiberAndCountWiseSalesReport" method="post" action="<@ofbizUrl>fiberAndCountWiseSalesReport.pdf</@ofbizUrl>" target="_blank">	
				<td width="30%">Fiber And Count Wise Sales Report</td>
				<td width="15%">From</br><input  type="text" size="18pt" id="FCWSReportDateFrom" readonly  name="fromDate"/></td>
			    <td width="15%">To</br><input  type="text" size="18pt" id="FCWSReportDateThru" readonly  name="thruDate"/></td>
			    <td width="15%"><span class='h3'>Branch
				 <select name="branchId" id="branchId">
					<option value=''></option>
				     <#list  formatList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					 </#list> 
				  </select>    								
			  </span></td>
      			 <td width="15%"><span class='h3'>Category
				    <select name="categoryId" id="categoryId">
				          <option value="ALL">ALL</option>
				          <option value='COTTON'>COTTON</option>
				       	  <option value='SILK'>SILK</option>
				       	  <option value='OTHER'>OTHERS</option>
				    </select>  </span>  								
			  	 </td>
			  	 <td width="10%">
				<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('fiberAndCountWiseSalesReport', '<@ofbizUrl>fiberAndCountWiseSalesReport.pdf</@ofbizUrl>');" class="buttontext"/></td>
				<td width="10%"><input type="submit" value="CSV" onClick="javascript:appendParams('fiberAndCountWiseSalesReport', '<@ofbizUrl>fiberAndCountWiseSalesReport.xls</@ofbizUrl>');" class="buttontext"/></td>         			
				
			</form>
          </tr>
          <tr class="alternate-row">
      	   <form id="PartyFinancialHistoryWithDrCr" name="PartyFinancialHistoryWithDrCr" method="post" action="<@ofbizUrl>PartyFinancialHistoryWithDrCrDepot.pdf</@ofbizUrl>" target="_blank">        
             <td width="10%">Party Financial History With Dr/Cr</td>
             <td width="10%">From</br><input  type="text" size="15pt" id="PFHFromDateCrDr" readonly  name="partyfromDate"/></br>
      		 To</br><input  type="text" size="15pt" id="PFHThruDateCrDr" readonly  name="partythruDate"/></td>
             <td width="5%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="PartyFinancialHistoryWithDrCr" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>
            <#--> <input type="text" name="partyId" id="partyId" size="10" maxlength="22"> --></br>
            Party Type :</br><select name="partyClassificationGroupId" id="partyClassificationGroupId">
			    <option value=''></option>
                <option value='INDIVIDUAL_WEAVERS'>Individual Weavers</option>
                <option value='OTHERS'>Others</option>
  			 </select></td>	
            <td width="10%">Party Group :<select name="roleTypeId" id="roleTypeId">
  						<#list roleTypeAttrList as list>
                         <option value='${list.roleTypeId}'>${list.description?if_exists}</option>
                         </#list> 
  						</select></br>
             <span class='h3'>Branch
				 <select name="branchId" id="branchId">
				 <option value=""></option>
				     <#list  formatList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					 </#list> 
				  </select>    								
			  </span></td>
			   <td width="10%"></td>
             <td width="10%"><input type="submit" value="PDF" class="buttontext"/></td>
             <td width="10%"></td>
           </form>
        </tr>
        <tr class="alternate-row">
    	<form id="CashPaymentCheckList" name="CashierPaymentCheckList" method="post"  target="_blank" action="<@ofbizUrl>CashierPaymentCheckListDepot</@ofbizUrl>">	
  			<td width="30%">Cash Payment CheckList Report<input  type="hidden"  value="CashPaymentCheckList"   name="reportTypeFlag"/> 
  				<input  type="hidden"  value="CASH_PAYIN"   name="paymentMethodTypeId"/>
  			</td>
  			<td width="15%">Date<input  type="text" size="18pt" id="CASHFromDateId" readonly  name="paymentDate"/></td>
  			<td width="15%">
  			    <input  type="hidden"  name="routeId" value="All"/>
  			</td>
  			<td width="10%"></td>
  			<td width="10%"></td>
			<td width="10%"><input type="submit" value="PDF" class="buttontext"/></td>
			<td width="10%"></td>
		</form>	
      </tr>
      <tr class="alternate-row">
    	<form id="agencyWiseInvoiceOutstandingReport" name="agencyWiseInvoiceOutstandingReport" method="post"  target="_blank" action="<@ofbizUrl>agencyWiseInvoiceOutstandingReport</@ofbizUrl>">	
  			<td width="30%"> Agency Wise Invoice Outstanding Report</td>
  			<td width="15%">Date<input  type="text" size="18pt" id="AWIORDate"   name="AWIORDate"/></td>
  			
			<td width="10%">ReportType :<select name="reportType" id="reportType">
                <option value='CREDITORS'>CREDITORS</option>
                <option value='DEBITORS'>DEBITORS</option>
  			 </select></td>	
			<td width="10%"></td>
			<td width="10%"></td>
				<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('agencyWiseInvoiceOutstandingReport', '<@ofbizUrl>agencyWiseInvoiceOutstandingReport.pdf</@ofbizUrl>');" class="buttontext"/></td> 
				<td width="10%"><input type="submit" value="CSV" onClick="javascript:appendParams('agencyWiseInvoiceOutstandingReport', '<@ofbizUrl>agencyWiseInvoiceOutstandingReport.xls?header=required</@ofbizUrl>');" class="buttontext"/></td> 	       			
				
		</form>	
      </tr>
		</#if>
		<#if ReportsType?has_content &&  ReportsType=="INDENT_REPORTS">
			 <tr class="alternate-row">
			<form id="indentListing" name="indentListing" method="post" action="<@ofbizUrl>IndentPDF.pdf</@ofbizUrl>" target="_blank">	
				<td width="30%">Indent Register Report</td>
				<td width="15%">From</br><input  type="text" size="18pt" id="IndentRegisterFromDate" readonly  name="IndentRegisterFromDate"/></br>
  			    To</br><input  type="text" size="18pt" id="IndentRegisterThruDate" readonly  name="IndentRegisterThruDate"/></td>
  			    <td width="15%">Entry From Date</br><input  type="text" size="18pt" id="IndentRegisterEntryFromDate" readonly  name="IndentRegisterEntryFromDate"/></br>
  			    Entry To Date</br><input  type="text" size="18pt" id="IndentRegisterEntryThruDate" readonly  name="IndentRegisterEntryThruDate"/></td>
  			    
			    <td width="15%">Customer<@htmlTemplate.lookupField size="10" maxlength="22" formName="indentListing" name="partyId" id="partyId" fieldFormName="LookupPartyName"/></td>
			    <td width="15%"><span class='h3'>Branch
							    <select name="branchId" id="branchId">
							        <option value=""></option>
							        <#list  formatList as formatList>
							          <option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
							        </#list> 
							    </select>    								
					  	 </span></td>
					  	 
				 <td width="15%"><span class='h3'>Sales Channel
			          <select name="salesChannel" id="salesChannel" class='h4' style="width:162px">
			                    <option value=""></option>
	          					<option value="WALKIN_SALES_CHANNEL">Walk-In Sales Channel</option>
	          					<option value="MOBILE_SALES_CHANNEL">e-Dhaga Channel</option>
	          					<option value="WEB_SALES_CHANNEL">Web Channel</option>
	          					<option value="POS_SALES_CHANNEL">POS Channel</option>
	          					<option value="PHONE_SALES_CHANNEL">Phone Channel</option>
	          					<option value="FAX_SALES_CHANNEL">Fax Channel</option>
	          					<option value="EMAIL_SALES_CHANNEL">E-Mail Channel</option>	          					
	          				</select> 								
					  	 </span></td>	  	 
					  	 <td width="10%"></td>
				<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('indentListing', '<@ofbizUrl>IndentPDF.pdf</@ofbizUrl>');" class="buttontext"/></td>         			
				<td width="10%"><input type="submit" value="CSV" onClick="javascript:appendParams('indentListing', '<@ofbizUrl>IndentCSV.xls?header=required</@ofbizUrl>');" class="buttontext"/></td>        			
			</form>
          </tr>

<tr class="alternate-row">
      	   <form id="pendingShipmentsReport" name="pendingShipmentsReport" method="post" action="<@ofbizUrl>pendingShipmentsReport.pdf</@ofbizUrl>" target="_blank">        
             <td width="10%">Pending Shipments Report</td>
             <td width="10%">&nbsp;From</br><input  type="text" size="15pt" id="pendingShipmentsReportFrom" readonly  name="partyfromDate" required /></br>
      		 	To</br><input  type="text" size="15pt" id="pendingShipmentsReportT0" readonly  name="partythruDate" required /></td>
                      <td width="15%"><span class='h3'>Branch
				 <select name="branchId" id="branchId" required>
					<option value=''></option>
				     <#list  formatList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					 </#list> 
				  </select>    								
			  </span></td>
			  <td width="15%"><span class='h3'>Category 
				 <select name="productCategory" id="productCategory">
				     <option value="ALL">ALL</option>
				     <option value='COTTON'>COTTON</option>
				     <option value='SILK'>SILK</option>
				     <option value='OTHER'>OTHERS</option>
				  </select>    								
			  </span></td>
			 	<td width="15%"><span class='h3'>Shipment Status
				 <select name="shipmentstate" id="shipmentstate">
				     <option value='Pending'>Pending</option>
				     <option value='Completed'>Completed</option>
				  </select>    								
			  </span></td> 
			  
			  	<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('pendingShipmentsReport', '<@ofbizUrl>pendingShipmentsReport.pdf</@ofbizUrl>');" class="buttontext"/></td>
			  	<td width="10%"><input type="submit" value="CSV" onClick="javascript:appendParams('pendingShipmentsReport', '<@ofbizUrl>pendingShipmentsReport.xls?header=required</@ofbizUrl>');" class="buttontext"/></td>
				
           </form>
        </tr> 

 	<tr class="alternate-row">
    	<form id="indentVsDispatch" name="indentVsDispatch" method="post"  target="_blank" action="<@ofbizUrl>IndentVsDispatch.pdf</@ofbizUrl>">	
  			<td width="30%" nowrap>Indent Vs Dispatch Report</td>
  			<td width="10%">From</br><input  type="text" size="18pt" id="ivdFromDate" readonly  name="ivdFromDate"/></td>
  			<td width="10%">To</br><input  type="text" size="18pt" id="ivdThruDate" readonly  name="ivdThruDate"/></td>
  			<td width="15%"><span class='h3'>Branch
				 <select name="branchId" id="branchId" required>
					<option value=''></option>
				     <#list  formatList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					 </#list> 
				  </select>    								
			  </span></td>
			  <td width="15%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="indentVsDispatch" name="partyId" id="partyId" fieldFormName="LookupPartyName"/></td>	
  			<td width="15%"><span class='h3'>Category 
				 <select name="productCategory" id="productCategory">
				     <option value="ALL">ALL</option>
				     <option value='COTTON'>COTTON</option>
				     <option value='SILK'>SILK</option>
				     <option value='OTHER'>OTHERS</option>
				  </select>
			</td>
  			<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('indentVsDispatch', '<@ofbizUrl>IndentVsDispatch.pdf</@ofbizUrl>');" class="buttontext"/></td>        			
			<td width="10%"><input type="submit" value="CSV" onClick="javascript:appendParams('indentVsDispatch', '<@ofbizUrl>IndentVsDispatchCSV.xls</@ofbizUrl>');" class="buttontext"/></td>        			
		</form>	
      </tr>
      <tr class="alternate-row">
      	   <form id="partyAbsractDetails" name="partyAbsractDetails" method="post" action="<@ofbizUrl>partyAbsractDetails.xls</@ofbizUrl>" target="_blank">        
             <td width="10%">Party Quota Details</td>
             <td width="10%">&nbsp;From</br><input  type="text" size="15pt" id="abstrctFromDate" readonly  name="abstrctFromDate"/></br>
      		 To</br><input  type="text" size="15pt" id="abstrctThruDate" readonly  name="abstrctThruDate"/></td>
             <td width="5%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="partyAbsractDetails" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>
            <#--> <input type="text" name="partyId" id="partyId" size="10" maxlength="22"> --></td>
           <#-->  <td width="10%">Party Group :<select name="roleTypeId" id="roleTypeId">
  						<#list roleTypeAttrList as list>
                         <option value='${list.roleTypeId}'>${list.description?if_exists}</option>
                         </#list> 
  						</select></td>
  		   <td width="10%">Party Type :<select name="partyClassificationGroupId" id="partyClassificationGroupId">
			    <option value=''></option>
                <option value='INDIVIDUAL_WEAVERS'>Individual Weavers</option>
                <option value='OTHERS'>Others</option>
  			 </select></td>	 -->			
             <td width="15%"><span class='h3'>Branch
				 <select name="branchId" id="branchId">
				 	<option value=""></option>
				     <#list  formatList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					 </#list> 
				  </select>    								
			  </span></td>
			  <td width="10%"></td>
			  <td width="10%"></td>
             <td width="10%"><input type="submit" value="CSV" class="buttontext"/></td>
           </form>
        </tr>
        <tr class="alternate-row">
    	<form id="mobilehits" name="mobilehits" method="post"  target="_blank" action="<@ofbizUrl>MobileHitsReport.csv</@ofbizUrl>">	
  			<td width="30%" nowrap>Mobile Hits Report</td>
  			<td width="15%">From</br><input  type="text" size="18pt" id="mobileHitsFromDate" readonly  name="mobileHitsFromDate"/></td>
  			<td width="15%">To</br><input  type="text" size="18pt" id="mobileHitsThruDate" readonly  name="mobileHitsThruDate"/></td>
  			<td width="10%"></td>
  			<td width="10%"></td>
			<td width="10%"><input type="submit" value="CSV" class="buttontext"/></td>
			<td width="10%"></td>
		</form>	
      </tr>
		</#if>
		<#if ReportsType?has_content &&  ReportsType=="PURCHASE_REPORTS">
			    <tr class="alternate-row">
			<form id="purchaseRegister" name="purchaseRegister" method="post" action="<@ofbizUrl>purchaseRegister.xls</@ofbizUrl>" target="_blank">	
				<td width="30%">Purchase Register Report</td>
						<td width="15%">From<input  type="text" size="18pt" id="purchaseRegisterReportDateFrom" readonly  name="fromDate"/></td>
			    <td width="15%">Thru<input  type="text" size="18pt" id="purchaseRegisterReportDateThru" readonly  name="thruDate"/></td>
				<td width="15%"></td>
			    <td width="15%">Customer<@htmlTemplate.lookupField size="10" maxlength="22" formName="purchaseRegister" name="partyId" id="partyId" fieldFormName="LookupPartyName"/></td>
			    <td width="15%"><span class='h3'>Branch
							    <select name="branchId" id="branchId">
							        <option value=""></option>
							        <#list  formatList as formatList>
							          <option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
							        </#list> 
							    </select>    								
					  	 </span></td>
					  	 <td width="10%"></td>
				<td width="10%"></td>
				<td width="10%"></td>
				<td width="10%"><input type="submit" value="CSV" onClick="javascript:appendParams('purchaseRegister', '<@ofbizUrl>purchaseRegister.xls</@ofbizUrl>');" class="buttontext"/></td>         			
			</form>
          </tr>

  <tr class="alternate-row">
			<form id="m1Report" name="m1Report" method="post" action="<@ofbizUrl>m1Report.pdf</@ofbizUrl>" target="_blank">	
				<td width="30%">M1 Report</td>
				<td width="15%">From</br><input  type="text" size="18pt" id="m1ReportDateFrom" readonly  name="fromDate"/></td>
			    <td width="15%">To</br><input  type="text" size="18pt" id="m1ReportDateThru" readonly  name="thruDate"/></td>
      			 <td width="15%"><span class='h3'>Branch
							    <select name="branchId" id="branchId">
							        <option value=""></option>
							        <#list  formatList as formatList>
							          <option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
							        </#list> 
							    </select>    								
					  	 </span></td>
      			 <td width="15%"><span class='h3'>Category
				    <select name="categoryId" id="categoryId">
				          <option value="ALL">ALL</option>
				          <option value='COTTON'>COTTON</option>
				       	  <option value='SILK'>SILK</option>
				       	  <option value='OTHER'>OTHERS</option>
				    </select>  </span>  								
			  	 </td>
				<td width="10%">
				<input type="submit" value="PDF" onClick="javascript:appendParams('m1Report', '<@ofbizUrl>m1Report.pdf</@ofbizUrl>');" class="buttontext"/>
				<input type="submit" value="CSV" onClick="javascript:appendParams('m1Report', '<@ofbizUrl>m1Report.xls</@ofbizUrl>');" class="buttontext"/></td>   			
				
			</form>
          </tr>

		<tr class="alternate-row">
			<form id="MillwisecountwisePurchaseofYarnReport" name="MillwisecountwisePurchaseofYarnReport" method="post" action="<@ofbizUrl>MillwisecountwisePurchaseofYarnReport.pdf</@ofbizUrl>" target="_blank">	
				<td width="40%">Mill wise count wise Purchase of Yarn Report</td>
				<td width="15%">From</br><input  type="text" size="18pt" id="MillwisecountwisePurchaseofYarnReportDateFrom" readonly  name="fromDate"/></td>
			    <td width="15%">To</br><input  type="text" size="18pt" id="MillwisecountwisePurchaseofYarnReportDateThru" readonly  name="thruDate"/></td>
      			 <td width="15%"><span class='h3'>Branch
							    <select name="branchId" id="branchId">
							        <option value=""></option>
							        <#list  formatList as formatList>
							          <option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
							        </#list> 
							    </select>    								
					  	 </span></td>
      			 <td width="15%"><span class='h3'>Category
				    <select name="categoryId" id="categoryId">
				          <option value="ALL">ALL</option>
				          <option value='COTTON'>COTTON</option>
				       	  <option value='SILK'>SILK</option>
				       	  <option value='OTHER'>OTHERS</option>
				    </select>  </span>  								
			  	 </td>				
				<td width="10%">
				<input type="submit" value="PDF" onClick="javascript:appendParams('MillwisecountwisePurchaseofYarnReport', '<@ofbizUrl>MillwisecountwisePurchaseofYarnReport.pdf</@ofbizUrl>');" class="buttontext"/>
				<input type="submit" value="CSV" onClick="javascript:appendParams('MillwisecountwisePurchaseofYarnReport', '<@ofbizUrl>MillwisecountwisePurchaseofYarnReport.xls</@ofbizUrl>');" class="buttontext"/></td>       			
				
			</form>
          </tr>
             
          
      <tr class="alternate-row">
      	   <form id="billWisePurchaseReport" name="billWisePurchaseReport" method="post" action="<@ofbizUrl>billWisePurchaseReport.csv</@ofbizUrl>" target="_blank">        
             <td width="10%">Bill Wise Purchase Report</td>
             <td width="10%">&nbsp;From</br><input  type="text" size="15pt" id="billWisePurchaseReportFrom" readonly  name="billReportfromDate" required /></br>
      		 To</br><input  type="text" size="15pt" id="billWisePurchaseReportThru" readonly  name="billReportthruDate" required /></td>
             <td width="15%"><span class='h3'>Branch
				 <select name="branchId" id="branchId" required>
					<option value=''></option>
				     <#list  formatList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					 </#list> 
				 </select>    								
			  </span>
			 </td>
			 <#--<td width="15%"><span class='h3'>Category 
				 <select name="productCategory" id="productCategory">
				     <option value="ALL">ALL</option>
				     <option value='COTTON'>COTTON</option>
				     <option value='SILK'>SILK</option>
				     <option value='OTHER'>OTHERS</option>
				  </select>    								
			  </span>
			 </td>
				<td width="15%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="billWisePurchaseReport" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>-->	
				<td width="10%"><span class='h3'>Type</br>
				 <select name="purposeType" id="purposeType">
				 		<option value=''></option>
						<option value='YARN_SALE'>Branch Sales</option>
						<option value='DEPOT_YARN_SALE'>Depot Sales</option>
				  </select>    								
			  </span></td>	
				<td width="10%"></td> 
				<td width="10%"><input type="submit" value="CSV" onClick="javascript:appendParams('billWisePurchaseReport', '<@ofbizUrl>billWisePurchaseReport.csv</@ofbizUrl>');" class="buttontext"/></td>         			
				<td width="10%"></td> 
           </form>
        </tr>
		</#if>
		<#if ReportsType?has_content &&  ReportsType=="SALE_REPORTS">
			 <tr class="alternate-row">
      	   <form id="salesPurchaseReport" name="salesPurchaseReport" method="post" action="<@ofbizUrl>indentHeadReport.csv</@ofbizUrl>" target="_blank">        
             <td width="10%">Sales and Purchase Report</td>
             <td width="10%">&nbsp;From</br><input  type="text" size="15pt" id="salesPurchaseReportFRO" readonly  name="partyfromDate"/></br>
      		 To</br><input  type="text" size="15pt" id="salesPurchaseReportTHRU" readonly  name="partythruDate"/></td>
                      <td width="15%"><span class='h3'>Branch
				 <select name="branchId" id="branchId">
				 	<option value=""></option>
				     <#list  formatList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					 </#list> 
				  </select>    								
			  </span></br>
			  
			    <span class='h3'>Type</br>
				 <select name="purposeType" id="purposeType">
				 		<option value=''></option>
						<option value='YARN_SALE'>Branch Sales</option>
						<option value='DEPOT_YARN_SALE'>Depot Sales</option>
				  </select>    								
			  </span></td>
			  
			  <td width="15%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="salesPurchaseReport" name="partyId" id="partyId" fieldFormName="LookupPartyName"/></td>	
			  <td width="15%">InvoiceId <input  type="text" size="18pt" id="invoceId" name="invoceId"/></td>
             <td width="10%"></td>
             <td width="10%"><input type="submit" value="CSV" class="buttontext"/></td>
           </form>
        </tr> 

<tr class="alternate-row">
			<form id="iceCreamSaleReport" name="iceCreamSaleReport" method="post" action="<@ofbizUrl>DepotSalesBookAbstractReport.pdf</@ofbizUrl>" target="_blank">	
				<td width="30%">Sale Book Report Abstract </td>
				<td width="15%">From</br><input  type="text" size="18pt" id="amulIceCreamfDate" readonly  name="fromDate"/></td>
			    <td width="15%">To</br><input  type="text" size="18pt" id="amulIceCreamtDate" readonly  name="thruDate"/></td>
      			<td></td>
				<td width="15%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="iceCreamSaleReport" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>	
				<td width="10%"><input type="submit" value="PDF" class="buttontext"/></td>
				<td width="10%"></td> 
			</form>
          </tr>

 <tr class="alternate-row">
      	   <form id="stateWiseBranchWiseSaleReport" name="stateWiseBranchWiseSaleReport" method="post" action="<@ofbizUrl>stateWiseBranchWiseSaleReport.xls</@ofbizUrl>" target="_blank">        
             <td width="10%">State agency wise sales</td>
             <td width="10%">&nbsp;From</br><input  type="text" size="15pt" id="stateWiseBranchWiseSaleReportFro" readonly  name="partyfromDate" required /></br>
      			To</br><input  type="text" size="15pt" id="stateWiseBranchWiseSaleReportTHRU" readonly  name="partythruDate" required /></td>
                      <td width="15%"><span class='h3'>Branch
				 <select name="branchId" id="branchId" required>
					<option value=''></option>
				     <#list  formatList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					 </#list> 
				  </select>    								
			  </span></td>
			   <#--<span class='h3'>State</br> 
				 <select name="state" id="state">
				     <option value=''></option>
				     <#list  stateListJSON as stateListJSON>
						<option value='${stateListJSON.value?if_exists}'>${stateListJSON.label?if_exists}</option>
					 </#list> 
				  </select>    								
			  </span></td>-->
			  <td width="15%"><span class='h3'>Category 
				 <select name="productCategory" id="productCategory">
				     <option value="ALL">ALL</option>
				     <option value='COTTON'>COTTON</option>
				     <option value='SILK'>SILK</option>
				     <option value='OTHER'>OTHERS</option>
				  </select>    								
			  </span></td>
				<td width="15%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="stateWiseBranchWiseSaleReport" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>	
				<td width="10%"></td>
				<td width="10%"><input type="submit" value="CSV" class="buttontext"/></td> 
           </form>
        </tr> 

 <tr class="alternate-row">
      	   <form id="billWiseSalesReport" name="billWiseSalesReport" method="post" action="<@ofbizUrl>billWiseSalesReport.pdf</@ofbizUrl>" target="_blank">        
             <td width="10%">Bill Wise Sales Report</td>
             <td width="10%">&nbsp;From</br><input  type="text" size="15pt" id="billWiseSalesReportFrom" readonly  name="billReportfromDate" required /></br>
      		 To</br><input  type="text" size="15pt" id="billWiseSalesReportThru" readonly  name="billReportthruDate" required /></td>
             <td width="15%"><span class='h3'>Branch
				 <select name="branchId" id="branchId" required>
					<option value=''></option>
				     <#list  formatList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					 </#list> 
				 </select>    								
			  </span>
			 
			<#--<span class='h3'>State</br>
				 <select name="state" id="state">
				     <option value=''></option>
				     <#list  stateListJSON as stateListJSON>
						<option value='${stateListJSON.value?if_exists}'>${stateListJSON.label?if_exists}</option>
					 </#list> 
				  </select>    								
			  </span>-->
			 </td>
			 <td width="15%"><span class='h3'>Category 
				 <select name="productCategory" id="productCategory">
				     <option value="ALL">ALL</option>
				     <option value='COTTON'>COTTON</option>
				     <option value='SILK'>SILK</option>
				     <option value='OTHER'>OTHERS</option>
				  </select>    								
			  </span>
			 </td>
				<td width="15%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="billWiseSalesReport" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>	
				<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('billWiseSalesReport', '<@ofbizUrl>billWiseSalesReport.pdf</@ofbizUrl>');" class="buttontext"/></td>
				<td width="10%"><input type="submit" value="CSV" onClick="javascript:appendParams('billWiseSalesReport', '<@ofbizUrl>billWiseSalesReport.xls</@ofbizUrl>');" class="buttontext"/></td>         			
				<td width="10%"></td> 
           </form>
        </tr>
		</#if>
		<#if ReportsType?has_content &&  ReportsType=="REIMBURSMENT_REPORTS">
			 <tr class="alternate-row">
    	<form id="claimReportDetails" name="claimReportDetails" method="post"  target="_blank" action="<@ofbizUrl>claimReportDetails.pdf</@ofbizUrl>">	
  			<td width="20%" nowrap>State Of claim ForReimbursementOf 10% </br>Subsidy Report</td>
  			<td width="15%">From</br><input  type="text" size="18pt" id="claimFromDate" readonly  name="claimFromDate"/></br>
  			To</br><input  type="text" size="18pt" id="claimThruDate" readonly  name="claimThruDate"/></td>
  			 <td width="15%"><span class='h3'>Branch
				<select name="branchId" id="branchId">
					<option value=""></option>
					<option value="HO">Head Office</option>
					<#list  formatList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					</#list> 
				</select>    								
			</span></br>
  			<span class='h3'>State</br>
				<select name="geoId" id="geoId">
					<option value=""></option>
					<#list  statesList as eachState>
						<option value='${eachState.geoId?if_exists}'>${eachState.geoName?if_exists}</option>
					</#list> 
				</select>    								
			</span></td>
			<td width="10%">
			    <span class='h3'>Type :<select name="reportTypeFlag" id="reportTypeFlag">
					<option value="Detailed">Detailed</option>
					<option value="Summary">Summary</option>
				</select></span>
			</td>
			<td width="10%"></td>			
			<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('claimReportDetails', '<@ofbizUrl>claimReportDetails.pdf</@ofbizUrl>');" class="buttontext"/></td>
			<td width="10%"><input type="submit" value="CSV" onClick="javascript:appendParams('claimReportDetails', '<@ofbizUrl>claimReportDetails.xls?header=required</@ofbizUrl>');" class="buttontext"/></td>         			
		</form>	
      </tr>
      
      <tr class="alternate-row">
    	<form id="claimReportDetailsTD" name="claimReportDetailsTD" method="post"  target="_blank" action="<@ofbizUrl>claimReportDetailsTD.pdf</@ofbizUrl>">	
  			<td width="20%" nowrap>State Of claim For Reimbursement Of Transport And Depot </br>Subsidy Report</td>
  			<td width="15%">From</br><input  type="text" size="18pt" id="claimFromDateTD" readonly  name="claimFromDateTD"/></br>
  			To</br><input  type="text" size="18pt" id="claimThruDateTD" readonly  name="claimThruDateTD"/></td>
  			 <td width="15%"><span class='h3'>Branch
				<select name="branchId" id="branchId">
					<option value=""></option>
					<option value="HO">Head Office</option>
					<#list  formatList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					</#list> 
				</select>    								
			</span></br>
  			<span class='h3'>State</br>
				<select name="geoId" id="geoId">
					<option value="ALL">ALL</option>
					<#list  statesList as eachState>
						<option value='${eachState.geoId?if_exists}'>${eachState.geoName?if_exists}</option>
					</#list> 
				</select>    								
			</span></td>
			<td width="10%">
			    <span class='h3'>Type :<select name="reportTypeFlag" id="reportTypeFlag">
					<option value="Detailed">Detailed</option>
					<option value="Summary">Summary</option>
				</select></span>
			</td>
			<td width="10%"></td>			
			<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('claimReportDetailsTD', '<@ofbizUrl>claimReportDetailsTD.pdf</@ofbizUrl>');" class="buttontext"/></td>
			<td width="10%"><input type="submit" value="CSV" onClick="javascript:appendParams('claimReportDetailsTD', '<@ofbizUrl>claimReportDetailsTD.xls?header=required</@ofbizUrl>');" class="buttontext"/></td>         			
		</form>	
      </tr>
<tr class="alternate-row">
      	   <form id="reimburcentTransporterReport" name="reimburcentTransporterReport" method="post" action="<@ofbizUrl>reimburcentTransporterReport.pdf</@ofbizUrl>" target="_blank">        
             <td width="10%">Reimbursment Transporter Report</td>
             <td width="10%">&nbsp;From</br><input  type="text" size="15pt" id="reimburcentTransporterFRO" readonly  name="partyfromDate" /></br>
      		 To</br><input  type="text" size="15pt" id="reimburcentTransporterTHRU" readonly  name="partythruDate"  /></td>
                      <td width="15%"><span class='h3'>Branch
				 <select name="branchId" id="branchId" required>
				 	 <option value=''></option> 
				     <#list  formatList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					 </#list> 
				  </select>    								
			  <#--</span></br>
			  <span class='h3'>State</br> 
				 <select name="state" id="state">
				     <option value=''></option>
				     <#list  stateListJSON as stateListJSON>
						<option value='${stateListJSON.value?if_exists}'>${stateListJSON.label?if_exists}</option>
					 </#list> 
				  </select>-->   								
			  </span></td>
			  <td width="15%"><span class='h3'>Category 
				 <select name="productCategory" id="productCategory">
				    <option value='ALL'>ALL</option>
					<option value='COTTON'>COTTON</option>
					<option value='SILK'>SILK</option>
					<option value='OTHER'>OTHERS</option>
				  </select>    								
			  </span></br>
				Party Code <@htmlTemplate.lookupField size="10" maxlength="22" formName="reimburcentTransporterReport" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>	</td> 
				<td width="10%"></td>
				
				<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('reimburcentTransporterReport', '<@ofbizUrl>reimburcentTransporterReport.pdf</@ofbizUrl>');" class="buttontext"/></td>
				<td width="10%"><input type="submit" value="CSV" onClick="javascript:appendParams('reimburcentTransporterReport', '<@ofbizUrl>reimburcentTransporterReport.xls</@ofbizUrl>');" class="buttontext"/></td>         			
				
           </form>
        </tr> 
   
        <tr class="alternate-row">
      	   <form id="depotReimburcentReport" name="depotReimburcentReport" method="post" action="<@ofbizUrl>depotReimburcentReport.xls</@ofbizUrl>" target="_blank">        
             <td width="10%">Depot Reimbursment Report</td>
             <td width="10%">&nbsp;From</br><input  type="text" size="15pt" id="depotReimburcentReportFRO" readonly  name="partyfromDate" required /></br>
      		 To</br><input  type="text" size="15pt" id="depotReimburcentReportTHRU" readonly  name="partythruDate" required /></td>
                      <td width="15%"><span class='h3'>Branch
				 <select name="branchId" id="branchId" required>
					<option value=''></option>
				     <#list  formatList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					 </#list> 
				  </select>    								
			  <#--</span></br>
			   <span class='h3'>State</br> 
				 <select name="state" id="state">
				     <option value=''></option>
				     <#list  stateListJSON as stateListJSON>
						<option value='${stateListJSON.value?if_exists}'>${stateListJSON.label?if_exists}</option>
					 </#list> 
				  </select>-->   								
			  </span></td>
			  <td width="15%"><span class='h3'>Category 
				 <select name="productCategory" id="productCategory">
					<option value='ALL'>ALL</option>
					<option value='COTTON'>COTTON</option>
					<option value='SILK'>SILK</option>
					<option value='OTHER'>OTHERS</option>
				  </select>    								
			  </span></br>
				Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="depotReimburcentReport" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>	
				<td width="10%"></td>
				
				<#--<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('depotReimburcentReport', '<@ofbizUrl>depotReimburcentReport.pdf</@ofbizUrl>');" class="buttontext"/></td>
				<td width="10%"><input type="submit" value="CSV" onClick="javascript:appendParams('depotReimburcentReport', '<@ofbizUrl>depotReimburcentReport.xls?header=required</@ofbizUrl>');" class="buttontext"/></td>-->        			
				
			 
           </form>
        </tr> 
        
         <tr class="alternate-row">
      	   <form id="depotReimburcentSummaryReport" name="depotReimburcentSummaryReport" method="post" action="<@ofbizUrl>depotReimburcentSummaryReport.pdf</@ofbizUrl>" target="_blank">        
             <td width="10%">Depot Reimbursment Summary Report</td>
             <td width="10%">&nbsp;From</br><input  type="text" size="15pt" id="depotReimburcentSummaryReportFRO" readonly  name="partyfromDate" required /></br>
      		 To</br><input  type="text" size="15pt" id="depotReimburcentSummaryReportTHRU" readonly  name="partythruDate" required /></td>
              <#-- <td width="15%"><span class='h3'>Branch
				 <select name="branchId" id="branchId" required>
					<option value=''></option>
				     <#list  formatList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					 </#list> 
				  </select>    								
			  </span></td>  -->
			  <td width="15%"><span class='h3'>Branch <br>
			  	  <select name='branchId2'>
	      		  </select>
			  </br>
			   <span class='h3'>State</br>
				 <select name="state" id="state" onchange="javascript:getbranchesByState(this,'branchId2');">
				     <option value="ALL">ALL</option>
				     <#list  stateListJSON as stateListJSON>
						<option value='${stateListJSON.value?if_exists}'>${stateListJSON.label?if_exists}</option>
					 </#list> 
				  </select> 
			  </span></td>
			  
			  <td width="15%"><span class='h3'>Category 
				 <select name="productCategory" id="productCategory">
					<option value="ALL">ALL</option>
					<option value='COTTON'>COTTON</option>
					<option value='SILK'>SILK</option>
					<option value='OTHER'>OTHERS</option>
				  </select>    								
			  </span></br>
				 <span class='h3'>Report Type 
				 <select name="reportType" id="reportType">
				     <option value='DEPOT'>Depot</option>
				     <option value='WITHOUT_DEPOT'>Without Depot</option>
				  </select>    								
			  </span></td>
			 
			<#-- 	<td width="15%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="depotReimburcentSummaryReport" name="partyId" id="partyId" fieldFormName="LookupPartyName"/> -->
			<td width="10%"></td>
			    
				<#--<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('depotReimburcentSummaryReport', '<@ofbizUrl>depotReimburcentSummaryReport.pdf</@ofbizUrl>');" class="buttontext"/></td>	
				<td width="10%"><input type="submit" value="CSV" onClick="javascript:appendParams('depotReimburcentSummaryReport', '<@ofbizUrl>depotReimburcentSummaryReport.xls</@ofbizUrl>');" class="buttontext"/></td>-->	
               	
           </form>
        </tr> 
        
         
         <tr class="alternate-row">
      	   <form id="StateWiseSchemeWiseSalesConsolidated" name="StateWiseSchemeWiseSalesConsolidated" method="post" action="<@ofbizUrl>StateWiseSchemeWiseSalesConsolidated.pdf</@ofbizUrl>" target="_blank">        
             <td width="10%">State Wise Scheme Wise Sales - Consolidated Report</td>
             <td width="10%">&nbsp;From</br><input  type="text" size="15pt" id="StateWiseSchemeWiseSalesConsolidatedFro" readonly  name="partyfromDate" required /></br>
      		 To</br><input  type="text" size="15pt" id="StateWiseSchemeWiseSalesConsolidatedThru" readonly  name="partythruDate" required /></td>
			   <td width="15%"><span class='h3'>Branch <br>
			  	  <select name='SWSWSCbranchId'>
	      		  </select>
			  </br>
			  <span class='h3'>State 
				 <select name="state" id="state" onchange="javascript:getbranchesByState(this,'SWSWSCbranchId');">
				     <option value="ALL">ALL</option>
				     <#list  stateListJSON as stateListJSON>
						<option value='${stateListJSON.value?if_exists}'>${stateListJSON.label?if_exists}</option>
					 </#list> 
				  </select> 
			  </span></td>
			  
			  <td width="15%"><span class='h3'>Category 
				 <select name="productCategory" id="productCategory">				     
					<option value="ALL">ALL</option>
					<option value='COTTON'>COTTON</option>
					<option value='SILK'>SILK</option>
					<option value='OTHERS'>OTHERS</option>
				  </select>    								
			  </span></br>
				 <span class='h3'>Report Type 
				 <select name="reportType" id="reportType">
				     <option value='DETAIL'>Detail</option>
				     <option value='ABSTRACT'>Abstract</option>
				  </select>    								
			  </span></td>
			 
			<#-- 	<td width="15%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="depotReimburcentSummaryReport" name="partyId" id="partyId" fieldFormName="LookupPartyName"/> -->
			<td width="10%"></td>
			
				<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('StateWiseSchemeWiseSalesConsolidated', '<@ofbizUrl>StateWiseSchemeWiseSalesConsolidated.pdf</@ofbizUrl>');" class="buttontext"/></td>	
				<td width="10%"><input type="submit" value="CSV" onClick="javascript:appendParams('StateWiseSchemeWiseSalesConsolidated', '<@ofbizUrl>StateWiseSchemeWiseSalesConsolidated.xls</@ofbizUrl>');" class="buttontext"/></td>	
			
           </form>
        </tr> 
		</#if>
		<#if ReportsType?has_content && ReportsType=="CLUSTER_REPORTS">
		
		</#if>
		<#if ReportsType?has_content && ReportsType=="MASTER_REPORTS">
				<tr class="alternate-row">
			<form id="CustomerDetails" name="CustomerDetails" method="post" action="<@ofbizUrl>CustomerDetails.csv</@ofbizUrl>" target="_blank">	
				<td width="30%">New Customer Masters</td>
				 <td width="15%"><span class='h3'>Role Type
							    <select name="customerType" id="customerType">
							          <option value="YARN">YARN</option>
							          <option value="DYESANDCHEM">DYES AND CHEM</option>
							    </select>    								
					  	 </span></td>
				<td width="15%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="CustomerDetails" name="partyId" id="partyId" fieldFormName="LookupPartyName"/></td>
      			<td width="15%"><span class='h3'>Branch
							    <select name="branchId" id="branchId">
							        <option value=""></option>
							        <#list  formatList as formatList>
							          <option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
							        </#list> 
							    </select>    								
					  	 </span></td>
				 <td width="15%"><span class='h3'>Party Classification
							    <select name="partyClassification" id="partyClassification">
							        <option value=""></option>
							        <#list  partyClassificationList as partyClassification>
							          <option value='${partyClassification.partyClassificationGroupId?if_exists}'>${partyClassification.description?if_exists}</option>
							        </#list> 
							    </select>    								
					  	 </span></td>  	 
      			<td width="10%"></td>
      			<td width="10%"></td>
				<td width="10%"><input type="submit" value="CSV" onClick="javascript:appendParams('CustomerDetails', '<@ofbizUrl>CustomerDetails.csv</@ofbizUrl>');" class="buttontext"/></td>         			
			</form>
        </tr>
        <tr class="alternate-row">
			<form id="productCategoryReport" name="productCategoryReport" method="post" action="<@ofbizUrl>productCategoryReportPdf</@ofbizUrl>" target="_blank">	
				<td width="30%">Product Category Report</td>
      			 <td width="15%"><span class='h3'>Category
							    <select name="categoryId" id="categoryId">
							          <option value="ALL">ALL</option>
							          <option value='COTTON'>COTTON</option>
							       	  <option value='SILK'>SILK</option>
							       	  <option value='OTHERS'>OTHERS</option>
							    </select>    								
					  	 </span></td>
      			<td width="10%"></td>
      			<td width="10%"></td>
      			<td width="10%"></td>
				<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('productCategoryReport', '<@ofbizUrl>productCategoryReportPdf</@ofbizUrl>');" class="buttontext"/></td>         			
			<td width="10%"></td>
			</form>
        </tr>
      <#--	<tr class="alternate-row">
			<form id="customerMasters" name="customerMasters" method="post" action="<@ofbizUrl>CustomerMasters.csv</@ofbizUrl>" target="_blank">	
				<td width="30%">Customer Masters</td>
      			 <td width="15%"><span class='h3'>Branch
							    <select name="branchId" id="branchId">
							        <option value=""></option>
							        <#list  formatList as formatList>
							          <option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
							        </#list> 
							    </select>    								
					  	 </span></td>
      			<td width="10%"></td>
				<td width="10%"></td>
				<td width="10%"></td>
			    <td width="10%"></td>
				<td width="10%"><input type="submit" value="CSV" onClick="javascript:appendParams('customerMasters', '<@ofbizUrl>CustomerMasters.csv</@ofbizUrl>');" class="buttontext"/></td>         			
			</form>
          </tr> -->
      	<tr class="alternate-row">
			<form id="supplierMasters" name="supplierMasters" method="post" action="<@ofbizUrl>SupplierMasters.csv</@ofbizUrl>" target="_blank">	
				<td width="30%">Supplier Masters</td>        
				<td width="15%"><span class='h3'>Role Type
							    <select name="customerType" id="customerType">
							          <option value="YARN">YARN</option>
							          <option value="DYESANDCHEM">DYES AND CHEM</option>
							    </select>    								
					  	 </span></td>
      			<td width="10%"></td>
      			<td width="10%"></td>
				<td width="10%">
				<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('supplierMasters', '<@ofbizUrl>SupplierMasters.pdf</@ofbizUrl>');" class="buttontext"/></td>
				<td width="10%"><input type="submit" value="CSV" onClick="javascript:appendParams('supplierMasters', '<@ofbizUrl>SupplierMasters.csv</@ofbizUrl>');" class="buttontext"/></td>         			
			</form>
          </tr>
          <tr class="alternate-row">
			<form id="productMasters" name="productMasters" method="post" action="<@ofbizUrl>ProductMasters.csv</@ofbizUrl>" target="_blank">	
				<td width="30%">Product Masters</td>
				<td width="15%"><span class='h3'>Role Type
							    <select name="customerType" id="customerType">
							          <option value="YARN">YARN</option>
							          <option value="DYESANDCHEM">DYES AND CHEM</option>
							    </select>    								
					  	 </span></td>
			    <td width="10%"></td>
      			<td></td>
      			<td width="10%"></td>
				<td width="10%"></td>
				<td width="10%"><input type="submit" value="CSV" onClick="javascript:appendParams('productMasters', '<@ofbizUrl>ProductMasters.csv</@ofbizUrl>');" class="buttontext"/></td>         			
			</form>
          </tr>
          
		</#if>
      
      <#if ReportsType?has_content &&  ReportsType=="TAX_REPORTS">
			 <tr class="alternate-row">
      	   <form id="TaxReport" name="TaxReport" method="post" action="<@ofbizUrl>taxReport.pdf</@ofbizUrl>" target="_blank">        
             <td width="10%">Purchase Tax Report</td>
             <td width="10%">&nbsp;From</br><input  type="text" size="15pt" id="TaxReportFRO" readonly  name="partyfromDate"/></br>
      		 To</br><input  type="text" size="15pt" id="TaxReportTHRU" readonly  name="partythruDate"/></td>
                      <#--<td width="15%"><span class='h3'>Branch
				 <select name="branchId" id="branchId">
				 	<option value=""></option>
				     <#list  formatList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					 </#list> 
				  </select>    								
			  </span></td>-->
			  <td width="15%"><span class='h3'>State</br>
				 <select name="state" id="state">
				     <#list  stateListJSON as stateListJSON>
						<option value='${stateListJSON.value?if_exists}'>${stateListJSON.label?if_exists}</option>
					 </#list> 
				  </select> 
			  </span></td>
			  <td width="15%"><span class='h3'>Purpose Type</br>
				 <select name="purposeTypeId" id="purposeTypeId">
				 <option value="ALL">All</option>
					<option value="YARN_SALE">Yarn</option>
				    <option value="DIES_AND_CHEM_SALE">Dyes and Chemicals</option>
				  </select> 
			  </span></td>
			   <td width="15%"><span class='h3'>Tax Type</br>
				 <select name="taxType" id="taxType">
					<option value="CST_PUR">CST</option>
				    <option value="VAT_PUR">VAT</option>
				    <option value="EXCISE_DUTY">Excise Duty</option>
				    <option value="ENTRY_TAX">Entry Tax</option>
				  </select> 
			  </span></td> 
			  <#--<td width="15%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="TaxReport" name="partyId" id="partyId" fieldFormName="LookupPartyName"/></td>-->	
             <td width="10%"></td>
             <#--<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('TaxReport', '<@ofbizUrl>taxReport.pdf</@ofbizUrl>');" class="buttontext"/>-->
           </form>
        </tr> 
        
        <tr class="alternate-row">
      	   <form id="SalesTaxReport" name="SalesTaxReport" method="post" action="<@ofbizUrl>SalesTaxReport.pdf</@ofbizUrl>" target="_blank">        
             <td width="10%">Sales Tax Report</td>
             <td width="10%">&nbsp;From</br><input  type="text" size="15pt" id="SalesTaxReportFRO" readonly  name="partyfromDate"/></br>
      		 To</br><input  type="text" size="15pt" id="SalesTaxReportTHRU" readonly  name="partythruDate"/></td>
                      <#--<td width="15%"><span class='h3'>Branch
				 <select name="branchId" id="branchId">
				 	<option value=""></option>
				     <#list  formatList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					 </#list> 
				  </select>    								
			  </span></td>-->
			  <td width="15%"><span class='h3'>State</br>
				 <select name="state" id="state">
				     <#list  stateListJSON as stateListJSON>
						<option value='${stateListJSON.value?if_exists}'>${stateListJSON.label?if_exists}</option>
					 </#list> 
				  </select> 
			  </span></td>
			  <td width="15%"><span class='h3'>Purpose Type</br>
				 <select name="purposeTypeId" id="purposeTypeId">
				 <option value="ALL">All</option>
					<option value="YARN_SALE">Yarn</option>
				    <option value="DIES_AND_CHEM_SALE">Dyes and Chemicals</option>
				  </select> 
			  </span></td>
			   <td width="15%"><span class='h3'>Tax Type</br>
				 <select name="taxType" id="taxType">
					<option value="CST_SALE">CST</option>
				    <option value="VAT_SALE">VAT</option>
				    <option value="EXCISE_DUTY">Excise Duty</option>
				    <option value="ENTRY_TAX">Entry Tax</option>
				  </select> 
			  </span></td> 
			  <#--<td width="15%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="TaxReport" name="partyId" id="partyId" fieldFormName="LookupPartyName"/></td>-->	
             <td width="10%"></td>
             <#--<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('SalesTaxReport', '<@ofbizUrl>SalesTaxReport.pdf</@ofbizUrl>');" class="buttontext"/>-->
           </form>
        </tr> 
      
      
      </#if>
      
      
      
          <#--<tr class="alternate-row">
			<form id="regularIceCreamSaleReport" name="regularIceCreamSaleReport" method="post" action="<@ofbizUrl>DepotSalesBookReport.pdf</@ofbizUrl>" target="_blank">	
				<td width="30%">Sale Book Report Detail</td>
				<td width="15%">From</br><input  type="text" size="18pt" id="regularIceCreamfDate" readonly  name="fromDate"/></td>
			    <td width="15%">To</br><input  type="text" size="18pt" id="regularIceCreamtDate" readonly  name="thruDate"/></td>
      			<td width="10%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="regularIceCreamSaleReport" name="partyId" id="partyId" fieldFormName="LookupPartyName"/></td>
				<td width="10%"></td>
				<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('regularIceCreamSaleReport', '<@ofbizUrl>RegularIceCreamSaleBookReport.pdf</@ofbizUrl>');" class="buttontext"/></td>
				<td width="10%"><input type="submit" value="CSV" onClick="javascript:appendParams('regularIceCreamSaleReport', '<@ofbizUrl>RegularIceCreamSaleBookReport.csv</@ofbizUrl>');" class="buttontext"/></td>        			
				
			</form>
          </tr>-->
        		
 		
          
           
          <#--<tr class="alternate-row">
      	   <form id="PartyFinancialHistoryWithDrCr" name="PartyFinancialHistoryWithDrCr" method="post" action="<@ofbizUrl>PartyFinancialHistoryWithDrCrDepot.pdf</@ofbizUrl>" target="_blank">        
             <td width="10%">Party Financial History With Dr/Cr</td>
             <td width="10%">From</br><input  type="text" size="15pt" id="PFHFromDateCrDr" readonly  name="partyfromDate"/></br>
      		 To</br><input  type="text" size="15pt" id="PFHThruDateCrDr" readonly  name="partythruDate"/></td>
             <td width="5%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="PartyFinancialHistoryWithDrCr" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>
            <#--> 
            <#--<input type="text" name="partyId" id="partyId" size="10" maxlength="22"></br>
            Party Type :</br><select name="partyClassificationGroupId" id="partyClassificationGroupId">
			    <option value=''></option>
                <option value='INDIVIDUAL_WEAVERS'>Individual Weavers</option>
                <option value='OTHERS'>Others</option>
  			 </select></td>	
            <td width="10%">Party Group :<select name="roleTypeId" id="roleTypeId">
  						<#list roleTypeAttrList as list>
                         <option value='${list.roleTypeId}'>${list.description?if_exists}</option>
                         </#list> 
  						</select></br>
             <span class='h3'>Branch
				 <select name="branchId" id="branchId">
				 <option value=""></option>
				     <#list  formatList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					 </#list> 
				  </select>    								
			  </span></td>
			   <td width="10%"></td>
             <td width="10%"><input type="submit" value="PDF" class="buttontext"/></td>
             <td width="10%"></td>
           </form>
        </tr>
        
         <tr class="alternate-row">
      	   <form id="partyAbsractDetails" name="partyAbsractDetails" method="post" action="<@ofbizUrl>partyAbsractDetails.csv</@ofbizUrl>" target="_blank">        
             <td width="10%">Party Quota Details</td>
             <td width="10%">&nbsp;From</br><input  type="text" size="15pt" id="abstrctFromDate" readonly  name="abstrctFromDate"/></br>
      		 To</br><input  type="text" size="15pt" id="abstrctThruDate" readonly  name="abstrctThruDate"/></td>
             <td width="5%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="partyAbsractDetails" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>-->
            <#--> <input type="text" name="partyId" id="partyId" size="10" maxlength="22"> --></td>
           <#-->  <td width="10%">Party Group :<select name="roleTypeId" id="roleTypeId">
  						<#list roleTypeAttrList as list>
                         <option value='${list.roleTypeId}'>${list.description?if_exists}</option>
                         </#list> 
  						</select></td>
  		   <td width="10%">Party Type :<select name="partyClassificationGroupId" id="partyClassificationGroupId">
			    <option value=''></option>
                <option value='INDIVIDUAL_WEAVERS'>Individual Weavers</option>
                <option value='OTHERS'>Others</option>
  			 </select></td>	 -->			
             <#--<td width="15%"><span class='h3'>Branch
				 <select name="branchId" id="branchId">
				 	<option value=""></option>
				     <#list  formatList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					 </#list> 
				  </select>    								
			  </span></td>
			  <td width="10%"></td>
			  <td width="10%"></td>
             <td width="10%"><input type="submit" value="CSV" class="buttontext"/></td>
           </form>
        </tr>
		<tr class="alternate-row">
    	<form id="CashPaymentCheckList" name="CashierPaymentCheckList" method="post"  target="_blank" action="<@ofbizUrl>CashierPaymentCheckListDepot</@ofbizUrl>">	
  			<td width="30%">Cash Payment CheckList Report<input  type="hidden"  value="CashPaymentCheckList"   name="reportTypeFlag"/> 
  				<input  type="hidden"  value="CASH_PAYIN"   name="paymentMethodTypeId"/>
  			</td>
  			<td width="15%">Date<input  type="text" size="18pt" id="CASHFromDateId" readonly  name="paymentDate"/></td>
  			<td width="15%">
  			    <input  type="hidden"  name="routeId" value="All"/>
  			</td>
  			<td width="10%"></td>
  			<td width="10%"></td>
			<td width="10%"><input type="submit" value="PDF" class="buttontext"/></td>
			<td width="10%"></td>
		</form>	
      </tr>
      <tr class="alternate-row">
    	<form id="agencyWiseInvoiceOutstandingReport" name="agencyWiseInvoiceOutstandingReport" method="post"  target="_blank" action="<@ofbizUrl>agencyWiseInvoiceOutstandingReport</@ofbizUrl>">	
  			<td width="30%"> Agency Wise Invoice Outstanding Report</td>
  			<td width="15%">Date<input  type="text" size="18pt" id="AWIORDate"   name="AWIORDate"/></td>
  			
			<td width="10%">ReportType :<select name="reportType" id="reportType">
                <option value='CREDITORS'>CREDITORS</option>
                <option value='DEBITORS'>DEBITORS</option>
  			 </select></td>	
			<td width="10%"></td>
			<td width="10%"></td>
				<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('agencyWiseInvoiceOutstandingReport', '<@ofbizUrl>agencyWiseInvoiceOutstandingReport.pdf</@ofbizUrl>');" class="buttontext"/></td> 
				<td width="10%"><input type="submit" value="CSV" onClick="javascript:appendParams('agencyWiseInvoiceOutstandingReport', '<@ofbizUrl>agencyWiseInvoiceOutstandingReport.csv</@ofbizUrl>');" class="buttontext"/></td> 	       			
				
		</form>	
      </tr>-->
      <#--<tr class="alternate-row">
    	<form id="stockStatement" name="stockStatement" method="post"  target="_blank" action="<@ofbizUrl>StockStatementReport.pdf</@ofbizUrl>">	
  			<td width="30%" nowrap>Stock Statement Report</td>
  			<td width="15%">From</br><input  type="text" size="18pt" id="stockFromDate" readonly  name="stockFromDate"/></td>
  			<td width="15%">To</br><input  type="text" size="18pt" id="stockThruDate" readonly  name="stockThruDate"/></td>
  			<td width="10%"></td>
  			<td width="10%"></td>
			<td width="10%"><input type="submit" value="PDF" class="buttontext"/></td>
			<td width="10%"></td>
		</form>	
      </tr>-->
      
      <#--<tr class="alternate-row">
    	<form id="mobilehits" name="mobilehits" method="post"  target="_blank" action="<@ofbizUrl>MobileHitsReport.csv</@ofbizUrl>">	
  			<td width="30%" nowrap>Mobile Hits Report</td>
  			<td width="15%">From</br><input  type="text" size="18pt" id="mobileHitsFromDate" readonly  name="mobileHitsFromDate"/></td>
  			<td width="15%">To</br><input  type="text" size="18pt" id="mobileHitsThruDate" readonly  name="mobileHitsThruDate"/></td>
  			<td width="10%"></td>
  			<td width="10%"></td>
			<td width="10%"><input type="submit" value="CSV" class="buttontext"/></td>
			<td width="10%"></td>
		</form>	
      </tr>-->
     
     
       
      <#--> <tr class="alternate-row">
    	<form id="subsidyDetails" name="subsidyDetails" method="post"  target="_blank" action="<@ofbizUrl>subsidyDetails.csv</@ofbizUrl>">	
  			<td width="30%" nowrap>10% Subsidy Report</td>
  			<td width="15%">From<input  type="text" size="18pt" id="subsidyFromDate" readonly  name="subsidyFromDate"/></td>
  			<td width="15%">Thru<input  type="text" size="18pt" id="subsidyThruDate" readonly  name="subsidyThruDate"/></td>
  			<td width="15%"></td>
  			<td width="15%"></td>
			<td width="10%"><input type="submit" value="CSV" class="buttontext"/></td>
		</form>	
      </tr>-->
     
     
      <#-- <tr class="alternate-row">
      	   <form id="reimburcentTransporterReport" name="reimburcentTransporterReport" method="post" action="<@ofbizUrl>reimburcentTransporterReport.csv</@ofbizUrl>" target="_blank">        
             <td width="10%">Reimbursment Transporter Report</td>
             <td width="10%">&nbsp;From<input  type="text" size="15pt" id="reimburcentTransporterFRO" readonly  name="partyfromDate"/></td>
      		 <td width="10%">Thru<input  type="text" size="15pt" id="reimburcentTransporterTHRU" readonly  name="partythruDate"/></td>
                      <td width="15%"><span class='h3'>Branch
				 <select name="branchId" id="branchId">
				 <option value=''></option>
				     <#list  formatList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					 </#list> 
				  </select>    								
			  </span></td>
			   <td width="15%"><span class='h3'>State 
				 <select name="state" id="state">
				     <option value=''></option>
				     <#list  stateListJSON as stateListJSON>
						<option value='${stateListJSON.value?if_exists}'>${stateListJSON.label?if_exists}</option>
					 </#list> 
				  </select>    								
			  </span></td>
			  <td width="15%"><span class='h3'>Category 
				 <select name="productCategory" id="productCategory">
				     <option value=''></option>
				     <option value='SILK'>SILK</option>
				     <option value='JUTE_YARN'>JUTE</option>
				     <option value='OTHER'>OTHERS</option>
				  </select>    								
			  </span></td>
				<td width="15%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="reimburcentTransporterReport" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>	<td width="10%"><input type="submit" value="Download" class="buttontext"/></td> 
           </form>
        </tr> 
        
        
        
        <tr class="alternate-row">
      	   <form id="depotReimburcentReport" name="depotReimburcentReport" method="post" action="<@ofbizUrl>depotReimburcentReport.csv</@ofbizUrl>" target="_blank">        
             <td width="10%">Depot Reimbursment Report</td>
             <td width="10%">&nbsp;From<input  type="text" size="15pt" id="depotReimburcentReportFRO" readonly  name="partyfromDate"/></td>
      		 <td width="10%">Thru<input  type="text" size="15pt" id="depotReimburcentReportTHRU" readonly  name="partythruDate"/></td>
                      <td width="15%"><span class='h3'>Branch
				 <select name="branchId" id="branchId">
				 <option value=''></option>
				     <#list  formatList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					 </#list> 
				  </select>    								
			  </span></td>
			   <td width="15%"><span class='h3'>State 
				 <select name="state" id="state">
				     <option value=''></option>
				     <#list  stateListJSON as stateListJSON>
						<option value='${stateListJSON.value?if_exists}'>${stateListJSON.label?if_exists}</option>
					 </#list> 
				  </select>    								
			  </span></td>
			  <td width="15%"><span class='h3'>Category 
				 <select name="productCategory" id="productCategory">
				     <option value=''></option>
				     <option value='SILK'>SILK</option>
				     <option value='JUTE_YARN'>JUTE</option>
				     <option value='OTHER'>OTHERS</option>
				  </select>    								
			  </span></td>
				<td width="15%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="reimburcentTransporterReport" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>	<td width="10%"><input type="submit" value="Download" class="buttontext"/></td> 
           </form>
        </tr> -->
        
         
       
        	
	</table>
</div>
<#--<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Store Report</h3>
    </div>
    <div class="screenlet-body">
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;" >  
      <tr class="alternate-row"> 
				<form id="stockPositionReport" name="stockPositionReport" mothed="post" action="<@ofbizUrl>stockPositionReportDepot.pdf</@ofbizUrl>" target="_blank">
					<table class="basic-table" cellspacing="5">
						<tr class="alternate-row">
							<td width="20%"><span class='h3'>Stock Position Report</span></td>
							<td width="25%">
							     <span class='h3'>
									Date <input  type="text" size="18pt" id="stockDate"   name="fromDate"/>
								 </span>
							</td>			
								<td width="15%"><span class='h3'>							</span></td>
							 <td width="15%"><span class='h3'>ledgerFolioNos
							    <select name="ledgerFolioNo" id="ledgerFolioNo">
							        <option value=""></option>
							        <#list  ledgerFolioList as ledgerFolioNos>
							          <option value='${ledgerFolioNos?if_exists}'>${ledgerFolioNos?if_exists}</option>
							        </#list> 
							    </select>    								
					  	 </span></td>
						    <td width="10%"><span class='h3'><input type="submit" value="PDF" class="buttontext"></span></td>
						</tr>     
					</table>
				</form>
			</tr>
       
	</table>
   </div>
</div>-->
