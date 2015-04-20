<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
<script type= "text/javascript">
	
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

$(document).ready(function(){

	    makeDatePicker("regularIceCreamfDate","regularIceCreamtDate");
	    makeDatePicker("regularIceCreamfDate","regularIceCreamtDate");
	    makeDatePicker("amulIceCreamfDate","amulIceCreamtDate");
	    makeDatePicker("amulIceCreamfDate","amulIceCreamtDate");
	    makeDatePicker("stockFromDate","stockThruDate");
	    makeDatePicker3("PFHFromDateCrDr","PFHThruDateCrDr");
	    makeDatePicker("stockDate");
	    makeDatePicker("CASHFromDateId","");
		$('#ui-datepicker-div').css('clip', 'auto');		
	});

function makeDatePicker3(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat:'yy, MM dd',
			changeMonth: true,
			changeYear: true,
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
             <td width="30%" nowrap>Party Financial History With Dr/Cr</td>
             <td width="15%">&nbsp;From<input  type="text" size="15pt" id="PFHFromDateCrDr" readonly  name="partyfromDate"/></td>
      		 <td width="15%">Thru<input  type="text" size="15pt" id="PFHThruDateCrDr" readonly  name="partythruDate"/></td>
             <td width="20%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="PartyFinancialHistoryWithDrCr" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>
            <#--> <input type="text" name="partyId" id="partyId" size="10" maxlength="22"> --></td>
             <td width="15%"></td>
             <td width="10%"><input type="submit" value="Download" class="buttontext"/></td>
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
