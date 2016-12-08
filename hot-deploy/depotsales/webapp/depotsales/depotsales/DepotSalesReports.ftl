
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

	    makeDatePicker("regularIceCreamfDate","regularIceCreamtDate");
	    makeDatePicker("regularIceCreamfDate","regularIceCreamtDate"); 
	    makeDatePicker("pendingShipmentsReportFrom","pendingShipmentsReportT0");
	    makeDatePicker("SupplierwiseCountwiseReportDateFrom","SupplierwiseCountwiseReportDateThru");
	    makeDatePicker("m1ReportDateFrom","m1ReportDateThru");
	    makeDatePicker("FCWSReportDateFrom","FCWSReportDateThru");
	    makeDatePicker("amulIceCreamfDate","amulIceCreamtDate");
	    makeDatePicker("amulIceCreamfDate","amulIceCreamtDate");
	    makeDatePicker("stockFromDate","stockThruDate");
	    makeDatePicker("ivdFromDate","ivdThruDate");
	    makeDatePicker("eObFromDate");
	    makeDatePicker3("subsidyFromDate","subsidyThruDate");
	    makeDatePicker3("claimFromDate","claimThruDate");
	    makeDatePicker3("PFHFromDateCrDr","PFHThruDateCrDr");
	    makeDatePicker3("reimburcentTransporterFRO","reimburcentTransporterTHRU");
	    makeDatePicker3("depotReimburcentReportFRO","depotReimburcentReportTHRU");
	    makeDatePicker3("stateWiseBranchWiseSaleReportFro","stateWiseBranchWiseSaleReportTHRU");
	    makeDatePicker3("abstrctFromDate","abstrctThruDate");
	    makeDatePicker3("salesPurchaseReportFRO","salesPurchaseReportTHRU");
	    makeDatePicker("stockDate");
	    makeDatePicker("CASHFromDateId","");
	    makeDatePicker3("billWiseSalesReportFrom","billWiseSalesReportThru");
	    
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
</script>

<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Sales Reports</h3>
    </div>
    <div class="screenlet-body">
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;" >  
      	<tr class="alternate-row">
			<form id="CustomerDetails" name="CustomerDetails" method="post" action="<@ofbizUrl>CustomerDetails.csv</@ofbizUrl>" target="_blank">	
				<td width="30%">New Customer Masters</td>
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
      			<td width="15%"></td>
				<td width="10%">
				<input type="submit" value="CSV" onClick="javascript:appendParams('CustomerDetails', '<@ofbizUrl>CustomerDetails.csv</@ofbizUrl>');" class="buttontext"/></td>         			
			</form>
        </tr>
        <tr class="alternate-row">
			<form id="productCategoryReport" name="productCategoryReport" method="post" action="<@ofbizUrl>productCategoryReportPdf</@ofbizUrl>" target="_blank">	
				<td width="30%">Product Category Report</td>
				<td width="15%"></td>
			    <td width="15%"></td>
      			 <td width="15%"><span class='h3'>Category
							    <select name="categoryId" id="categoryId">
							          <option value="ALL">ALL</option>
							          <option value='COTTON'>COTTON</option>
							       	  <option value='SILK'>SILK</option>
							       	  <option value='OTHERS'>OTHERS</option>
							    </select>    								
					  	 </span></td>
      			<td width="15%"></td>
				<td width="10%">
				<input type="submit" value="PDF" onClick="javascript:appendParams('productCategoryReport', '<@ofbizUrl>productCategoryReportPdf</@ofbizUrl>');" class="buttontext"/></td>         			
			</form>
        </tr>
      	<tr class="alternate-row">
			<form id="customerMasters" name="customerMasters" method="post" action="<@ofbizUrl>CustomerMasters.csv</@ofbizUrl>" target="_blank">	
				<td width="30%">Customer Masters</td>
				<td width="15%"></td>
			    <td width="15%"></td>
      			 <td width="15%"><span class='h3'>Branch
							    <select name="branchId" id="branchId">
							        <option value=""></option>
							        <#list  formatList as formatList>
							          <option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
							        </#list> 
							    </select>    								
					  	 </span></td>
      			<td width="15%"></td>
				<td width="10%">
				<input type="submit" value="CSV" onClick="javascript:appendParams('customerMasters', '<@ofbizUrl>CustomerMasters.csv</@ofbizUrl>');" class="buttontext"/></td>         			
			</form>
          </tr>
      	<tr class="alternate-row">
			<form id="supplierMasters" name="supplierMasters" method="post" action="<@ofbizUrl>SupplierMasters.csv</@ofbizUrl>" target="_blank">	
				<td width="30%">Supplier Masters</td>
				<td width="15%"></td>
			    <td width="15%"></td>
      			<td></td>
      			<td width="15%"></td>
				<td width="10%">
				<input type="submit" value="CSV" onClick="javascript:appendParams('supplierMasters', '<@ofbizUrl>SupplierMasters.csv</@ofbizUrl>');" class="buttontext"/></td>         			
			</form>
          </tr>
          <tr class="alternate-row">
			<form id="productMasters" name="productMasters" method="post" action="<@ofbizUrl>ProductMasters.csv</@ofbizUrl>" target="_blank">	
				<td width="30%">Product Masters</td>
				<td width="15%"></td>
			    <td width="15%"></td>
      			<td></td>
      			<td width="15%"></td>
				<td width="10%">
				<input type="submit" value="CSV" onClick="javascript:appendParams('productMasters', '<@ofbizUrl>ProductMasters.csv</@ofbizUrl>');" class="buttontext"/></td>         			
			</form>
          </tr>
           <tr class="alternate-row">
      	   <form id="salesPurchaseReport" name="salesPurchaseReport" method="post" action="<@ofbizUrl>indentHeadReport.csv</@ofbizUrl>" target="_blank">        
             <td width="10%">Sales and Purchase Report</td>
             <td width="10%">&nbsp;From<input  type="text" size="15pt" id="salesPurchaseReportFRO" readonly  name="partyfromDate"/></td>
      		 <td width="10%">Thru<input  type="text" size="15pt" id="salesPurchaseReportTHRU" readonly  name="partythruDate"/></td>
                      <td width="15%"><span class='h3'>Branch
				 <select name="branchId" id="branchId">
				     <#list  formatList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					 </#list> 
				  </select>    								
			  </span></td>
			  
			    <td width="15%"><span class='h3'>Type
				 <select name="purposeType" id="purposeType">
				 		<option value=''></option>
						<option value='YARN_SALE'>Branch Sales</option>
						<option value='DEPOT_YARN_SALE'>Depot Sales</option>
				  </select>    								
			  </span></td>
			  
			  
			  <td width="15%">InvoiceId<input  type="text" size="18pt" id="invoceId" name="invoceId"/></td>
			  <td></td>
             <td width="5%"><input type="submit" value="CSV" class="buttontext"/></td>
           </form>
        </tr> 
          <tr class="alternate-row">
			<form id="regularIceCreamSaleReport" name="regularIceCreamSaleReport" method="post" action="<@ofbizUrl>DepotSalesBookReport.pdf</@ofbizUrl>" target="_blank">	
				<td width="30%">Sale Book Report Detail</td>
				<td width="15%">From<input  type="text" size="18pt" id="regularIceCreamfDate" readonly  name="fromDate"/></td>
			    <td width="15%">To<input  type="text" size="18pt" id="regularIceCreamtDate" readonly  name="thruDate"/></td>
      			<td></td>
      			<td width="15%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="iceCreamSaleReport" name="partyId" id="partyId" fieldFormName="LookupPartyName"/></td>
				<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('regularIceCreamSaleReport', '<@ofbizUrl>RegularIceCreamSaleBookReport.pdf</@ofbizUrl>');" class="buttontext"/>
				<input type="submit" value="CSV" onClick="javascript:appendParams('regularIceCreamSaleReport', '<@ofbizUrl>RegularIceCreamSaleBookReport.csv</@ofbizUrl>');" class="buttontext"/></td>         			
				
			</form>
          </tr>
          <tr class="alternate-row">
			<form id="m1Report" name="m1Report" method="post" action="<@ofbizUrl>m1Report.pdf</@ofbizUrl>" target="_blank">	
				<td width="30%">M1 Report</td>
				<td width="15%">From<input  type="text" size="18pt" id="m1ReportDateFrom" readonly  name="fromDate"/></td>
			    <td width="15%">To<input  type="text" size="18pt" id="m1ReportDateThru" readonly  name="thruDate"/></td>
      			 <td width="15%"><span class='h3'>Category
				    <select name="categoryId" id="categoryId">
				          <option value="ALL">ALL</option>
				          <option value='COTTON'>COTTON</option>
				       	  <option value='SILK'>SILK</option>
				       	  <option value='OTHER'>OTHERS</option>
				    </select>  </span>  								
			  	 </td>
				<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('m1Report', '<@ofbizUrl>m1Report.pdf</@ofbizUrl>');" class="buttontext"/>
				<input type="submit" value="CSV" onClick="javascript:appendParams('m1Report', '<@ofbizUrl>m1Report.csv</@ofbizUrl>');" class="buttontext"/></td>         			
				
			</form>
          </tr>
		<tr class="alternate-row">
			<form id="SupplierwiseCountwisePurchaseReport" name="SupplierwiseCountwisePurchaseReport" method="post" action="<@ofbizUrl>SupplierwiseCountwisePurchaseReport.pdf</@ofbizUrl>" target="_blank">	
				<td width="30%">Supplier wise Count wise Purchase Report</td>
				<td width="15%">From<input  type="text" size="18pt" id="SupplierwiseCountwiseReportDateFrom" readonly  name="fromDate"/></td>
			    <td width="15%">To<input  type="text" size="18pt" id="SupplierwiseCountwiseReportDateThru" readonly  name="thruDate"/></td>
      			 <td width="15%"><span class='h3'>Category
				    <select name="categoryId" id="categoryId">
				          <option value="ALL">ALL</option>
				          <option value='COTTON'>COTTON</option>
				       	  <option value='SILK'>SILK</option>
				       	  <option value='OTHER'>OTHERS</option>
				    </select>  </span>  								
			  	 </td>
				<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('SupplierwiseCountwisePurchaseReport', '<@ofbizUrl>SupplierwiseCountwisePurchaseReport.pdf</@ofbizUrl>');" class="buttontext"/>
				<input type="submit" value="CSV" onClick="javascript:appendParams('SupplierwiseCountwisePurchaseReport', '<@ofbizUrl>SupplierwiseCountwisePurchaseReport.csv</@ofbizUrl>');" class="buttontext"/></td>         			
				
			</form>
          </tr>
 		<tr class="alternate-row">
			<form id="MillwisecountwisePurchaseofYarnReport" name="MillwisecountwisePurchaseofYarnReport" method="post" action="<@ofbizUrl>MillwisecountwisePurchaseofYarnReport.pdf</@ofbizUrl>" target="_blank">	
				<td width="30%">Mill wise count wise Purchase of Yarn Report</td>
				<td width="15%">From<input  type="text" size="18pt" id="MillwisecountwisePurchaseofYarnReportDateFrom" readonly  name="fromDate"/></td>
			    <td width="15%">To<input  type="text" size="18pt" id="MillwisecountwisePurchaseofYarnReportDateThru" readonly  name="thruDate"/></td>
      			 <td width="15%"><span class='h3'>Category
				    <select name="categoryId" id="categoryId">
				          <option value="ALL">ALL</option>
				          <option value='COTTON'>COTTON</option>
				       	  <option value='SILK'>SILK</option>
				       	  <option value='OTHER'>OTHERS</option>
				    </select>  </span>  								
			  	 </td>
				<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('MillwisecountwisePurchaseofYarnReport', '<@ofbizUrl>MillwisecountwisePurchaseofYarnReport.pdf</@ofbizUrl>');" class="buttontext"/>
				<input type="submit" value="CSV" onClick="javascript:appendParams('MillwisecountwisePurchaseofYarnReport', '<@ofbizUrl>MillwisecountwisePurchaseofYarnReport.csv</@ofbizUrl>');" class="buttontext"/></td>         			
				
			</form>
          </tr>
           <tr class="alternate-row">
			<form id="fiberAndCountWiseSalesReport" name="fiberAndCountWiseSalesReport" method="post" action="<@ofbizUrl>fiberAndCountWiseSalesReport.pdf</@ofbizUrl>" target="_blank">	
				<td width="30%">Fiber And Count Wise Sales Report</td>
				<td width="15%">From<input  type="text" size="18pt" id="FCWSReportDateFrom" readonly  name="fromDate"/></td>
			    <td width="15%">To<input  type="text" size="18pt" id="FCWSReportDateThru" readonly  name="thruDate"/></td>
      			 <td width="15%"><span class='h3'>Category
				    <select name="categoryId" id="categoryId">
				          <option value="ALL">ALL</option>
				          <option value='COTTON'>COTTON</option>
				       	  <option value='SILK'>SILK</option>
				       	  <option value='OTHER'>OTHERS</option>
				    </select>  </span>  								
			  	 </td>
				<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('fiberAndCountWiseSalesReport', '<@ofbizUrl>fiberAndCountWiseSalesReport.pdf</@ofbizUrl>');" class="buttontext"/>
				<input type="submit" value="CSV" onClick="javascript:appendParams('fiberAndCountWiseSalesReport', '<@ofbizUrl>fiberAndCountWiseSalesReport.csv</@ofbizUrl>');" class="buttontext"/></td>         			
				
			</form>
          </tr>
           <tr class="alternate-row">
			<form id="iceCreamSaleReport" name="iceCreamSaleReport" method="post" action="<@ofbizUrl>DepotSalesBookAbstractReport.pdf</@ofbizUrl>" target="_blank">	
				<td width="30%">Sale Book Report Abstract </td>
				<td width="15%">From<input  type="text" size="18pt" id="amulIceCreamfDate" readonly  name="fromDate"/></td>
			    <td width="15%">To<input  type="text" size="18pt" id="amulIceCreamtDate" readonly  name="thruDate"/></td>
      			<td></td>
				<td width="15%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="iceCreamSaleReport" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>	<td width="10%"><input type="submit" value="Download" class="buttontext"/></td> 
			</form>
          </tr>
          <tr class="alternate-row">
      	   <form id="PartyFinancialHistoryWithDrCr" name="PartyFinancialHistoryWithDrCr" method="post" action="<@ofbizUrl>PartyFinancialHistoryWithDrCrDepot.pdf</@ofbizUrl>" target="_blank">        
             <td width="10%">Party Financial History With Dr/Cr</td>
             <td width="10%">&nbsp;From<input  type="text" size="15pt" id="PFHFromDateCrDr" readonly  name="partyfromDate"/></td>
      		 <td width="10%">Thru<input  type="text" size="15pt" id="PFHThruDateCrDr" readonly  name="partythruDate"/></td>
             <td width="5%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="PartyFinancialHistoryWithDrCr" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>
            <#--> <input type="text" name="partyId" id="partyId" size="10" maxlength="22"> --></td>
            <td width="10%">Party Group :<select name="roleTypeId" id="roleTypeId">
  						<#list roleTypeAttrList as list>
                         <option value='${list.roleTypeId}'>${list.description?if_exists}</option>
                         </#list> 
  						</select></td>
  		    <td width="10%">Party Type :<select name="partyClassificationGroupId" id="partyClassificationGroupId">
			    <option value=''></option>
                <option value='INDIVIDUAL_WEAVERS'>Individual Weavers</option>
                <option value='OTHERS'>Others</option>
  			 </select></td>				
             <td width="15%"><span class='h3'>Branch
				 <select name="branchId" id="branchId">
				     <#list  formatList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					 </#list> 
				  </select>    								
			  </span></td>
             <td width="5%"><input type="submit" value="PDF" class="buttontext"/></td>
           </form>
        </tr>
        
         <tr class="alternate-row">
      	   <form id="partyAbsractDetails" name="partyAbsractDetails" method="post" action="<@ofbizUrl>partyAbsractDetails.csv</@ofbizUrl>" target="_blank">        
             <td width="10%">Party Quota Details</td>
             <td width="10%">&nbsp;From<input  type="text" size="15pt" id="abstrctFromDate" readonly  name="abstrctFromDate"/></td>
      		 <td width="10%">Thru<input  type="text" size="15pt" id="abstrctThruDate" readonly  name="abstrctThruDate"/></td>
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
				     <#list  formatList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					 </#list> 
				  </select>    								
			  </span></td>
             <td width="5%"><input type="submit" value="CSV" class="buttontext"/></td>
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
  			<td width="15%"></td>
  			<td width="15%"></td>
			<td width="10%"><input type="submit" value="Download" class="buttontext"/></td>
		</form>	
      </tr>
      <tr class="alternate-row">
    	<form id="stockStatement" name="stockStatement" method="post"  target="_blank" action="<@ofbizUrl>StockStatementReport.pdf</@ofbizUrl>">	
  			<td width="30%" nowrap>Stock Statement Report</td>
  			<td width="15%">From<input  type="text" size="18pt" id="stockFromDate" readonly  name="stockFromDate"/></td>
  			<td width="15%">Thru<input  type="text" size="18pt" id="stockThruDate" readonly  name="stockThruDate"/></td>
  			<td width="15%"></td>
  			<td width="15%"></td>
			<td width="10%"><input type="submit" value="Download" class="buttontext"/></td>
		</form>	
      </tr>
      <tr class="alternate-row">
    	<form id="indentVsDispatch" name="indentVsDispatch" method="post"  target="_blank" action="<@ofbizUrl>IndentVsDispatch.pdf</@ofbizUrl>">	
  			<td width="30%" nowrap>Indent Vs Dispatch Report</td>
  			<td width="15%">From<input  type="text" size="18pt" id="ivdFromDate" readonly  name="ivdFromDate"/></td>
  			<td width="15%">Thru<input  type="text" size="18pt" id="ivdThruDate" readonly  name="ivdThruDate"/></td>
  			<td width="15%"></td>
  			<td width="15%"></td>
			<td width="10%"><input type="submit" value="Download" class="buttontext"/></td>
		</form>	
      </tr>
      <tr class="alternate-row">
			<form id="indentListing" name="indentListing" method="post" action="<@ofbizUrl>IndentCSV.csv</@ofbizUrl>" target="_blank">	
				<td width="30%">Indent Register Report</td>
				<td width="15%"></td>
			    <td width="15%">Customer<@htmlTemplate.lookupField size="10" maxlength="22" formName="indentListing" name="partyId" id="partyId" fieldFormName="LookupPartyName"/></td>
			    <td width="15%"><span class='h3'>Branch
							    <select name="branchId" id="branchId">
							        <option value=""></option>
							        <#list  formatList as formatList>
							          <option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
							        </#list> 
							    </select>    								
					  	 </span></td>
					  	 <td width="15%"></td>
				<td width="10%">
				<input type="submit" value="CSV" onClick="javascript:appendParams('indentListing', '<@ofbizUrl>IndentCSV.csv</@ofbizUrl>');" class="buttontext"/></td>         			
			</form>
          </tr>
          <tr class="alternate-row">
			<form id="purchaseRegister" name="purchaseRegister" method="post" action="<@ofbizUrl>purchaseRegister.csv</@ofbizUrl>" target="_blank">	
				<td width="30%">Purchase Register Report</td>
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
					  	 <td width="15%"></td>
				<td width="10%">
				<input type="submit" value="CSV" onClick="javascript:appendParams('purchaseRegister', '<@ofbizUrl>purchaseRegister.csv</@ofbizUrl>');" class="buttontext"/></td>         			
			</form>
          </tr>
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
      <tr class="alternate-row">
    	<form id="claimReportDetails" name="claimReportDetails" method="post"  target="_blank" action="<@ofbizUrl>claimReportDetails.pdf</@ofbizUrl>">	
  			<td width="20%" nowrap>State Of claim For Reimbursement Of 10% Subsidy Report</td>
  			<td width="15%">From<input  type="text" size="18pt" id="claimFromDate" readonly  name="claimFromDate"/></td>
  			<td width="15%">Thru<input  type="text" size="18pt" id="claimThruDate" readonly  name="claimThruDate"/></td>
  			 <td width="15%"><span class='h3'>Branch
				<select name="branchId" id="branchId">
					<option value=""></option>
					<option value="HO">Head Office</option>
					<#list  formatList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					</#list> 
				</select>    								
			</span></td>
  			 <td width="15%"><span class='h3'>State
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
			<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('claimReportDetails', '<@ofbizUrl>claimReportDetails.pdf</@ofbizUrl>');" class="buttontext"/>
				<input type="submit" value="CSV" onClick="javascript:appendParams('claimReportDetails', '<@ofbizUrl>claimReportDetails.csv</@ofbizUrl>');" class="buttontext"/></td>         			
		</form>	
      </tr>
     
      <#--> <tr class="alternate-row">
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
        
          <tr class="alternate-row">
      	   <form id="stateWiseBranchWiseSaleReport" name="stateWiseBranchWiseSaleReport" method="post" action="<@ofbizUrl>stateWiseBranchWiseSaleReport.csv</@ofbizUrl>" target="_blank">        
             <td width="10%">State agency wise sales</td>
             <td width="10%">&nbsp;From<input  type="text" size="15pt" id="stateWiseBranchWiseSaleReportFro" readonly  name="partyfromDate" required /></td>
      		 <td width="10%">Thru<input  type="text" size="15pt" id="stateWiseBranchWiseSaleReportTHRU" readonly  name="partythruDate" required /></td>
                      <td width="15%"><span class='h3'>Branch
				 <select name="branchId" id="branchId" required>
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
				<td width="15%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="stateWiseBranchWiseSaleReport" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>	<td width="10%"><input type="submit" value="Download" class="buttontext"/></td> 
           </form>
        </tr> 
        <tr class="alternate-row">
      	   <form id="billWiseSalesReport" name="billWiseSalesReport" method="post" action="<@ofbizUrl>billWiseSalesReport.pdf</@ofbizUrl>" target="_blank">        
             <td width="10%">Bill Wise Sales Report</td>
             <td width="10%">&nbsp;From<input  type="text" size="15pt" id="billWiseSalesReportFrom" readonly  name="billReportfromDate" required /></td>
      		 <td width="10%">Thru<input  type="text" size="15pt" id="billWiseSalesReportThru" readonly  name="billReportthruDate" required /></td>
             <td width="15%"><span class='h3'>Branch
				 <select name="branchId" id="branchId" required>
					<option value=''></option>
				     <#list  formatList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					 </#list> 
				 </select>    								
			  </span>
			 </td>
			 <td width="15%"><span class='h3'>State 
				 <select name="state" id="state">
				     <option value=''></option>
				     <#list  stateListJSON as stateListJSON>
						<option value='${stateListJSON.value?if_exists}'>${stateListJSON.label?if_exists}</option>
					 </#list> 
				  </select>    								
			  </span>
			 </td>
			 <td width="15%"><span class='h3'>Category 
				 <select name="productCategory" id="productCategory">
				     <option value=''></option>
				     <option value='SILK'>SILK</option>
				     <option value='JUTE_YARN'>JUTE</option>
				     <option value='OTHER'>OTHERS</option>
				  </select>    								
			  </span>
			 </td>
				<td width="15%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="billWiseSalesReport" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>	<td width="10%"><input type="submit" value="Download" class="buttontext"/></td> 
           </form>
        </tr>
        	<tr class="alternate-row">
      	   <form id="pendingShipmentsReport" name="pendingShipmentsReport" method="post" action="<@ofbizUrl>pendingShipmentsReport.pdf</@ofbizUrl>" target="_blank">        
             <td width="10%">Pending Shipments Report</td>
             <td width="10%">&nbsp;From<input  type="text" size="15pt" id="pendingShipmentsReportFrom" readonly  name="partyfromDate" required /></td>
      		 <td width="10%">Thru<input  type="text" size="15pt" id="pendingShipmentsReportT0" readonly  name="partythruDate" required /></td>
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
				     <option value=''></option>
				     <option value='SILK'>SILK</option>
				     <option value='JUTE_YARN'>JUTE</option>
				     <option value='OTHER'>OTHERS</option>
				  </select>    								
			  </span></td>
			  <td width="10%">
			  	<input type="submit" value="PDF" onClick="javascript:appendParams('pendingShipmentsReport', '<@ofbizUrl>pendingShipmentsReport.pdf</@ofbizUrl>');" class="buttontext"/>
			  	<input type="submit" value="CSV" onClick="javascript:appendParams('pendingShipmentsReport', '<@ofbizUrl>pendingShipmentsReport.csv</@ofbizUrl>');" class="buttontext"/>
				
           </form>
        </tr> 
	</table>
</div>
<div class="screenlet">
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
						    <td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
						</tr>
					</table>
				</form>
			</tr>
       
	</table>
   </div>
</div>
