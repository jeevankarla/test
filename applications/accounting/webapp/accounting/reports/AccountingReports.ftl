<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
<script type= "text/javascript">
//Get AccountCodeList for AccountingTransactions
var accountCodeList;
    function setAccountCode(customTimePeriodId)
    {
    //alert("customTimePeriodId   "+customTimePeriodId);
    var customTimePeriodId=customTimePeriodId;
    var organizationPartyId="Company";
    var methodOptionList =[];
    //var accountCodeList = ${StringUtil.wrapString(dataJSON)!'[]'};
    //alert(accountCodeList);
    var dataString="customTimePeriodId=" + customTimePeriodId + "&organizationPartyId=" + organizationPartyId ;
    $.ajax({
             type: "POST",
             url: "getAccountCode",
             data: dataString ,
             dataType: 'json',
             async: false,
         success: function(result) {
               if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){            	  
            	   alert(result["_ERROR_MESSAGE_"]);
               }else{
               		// alert("=result==="+result);
            	   var glAccountCodelist =result["dataJSON"];
            	  // alert((result["dataJSON"]).stringify());
                   if(isEmpty(glAccountCodelist))
                      {
            	 			 //            	 alert("There is No Acccount Transactions");
			   				  methodOptionList.push('<option value=""></option>');
			 		  		
			  
            			 // alert("=glAccountCodelist=in loop===="+methodOptionList);
            			  $('#AccountCode').html(methodOptionList.join(''));
            	              	 
            	 	 }
            	 	 else
            	 	 {
            			// alert("=glAccountCodelist==="+glAccountCodelist);
            			$.each(glAccountCodelist, function(key, item){
			    		 methodOptionList.push('<option value="'+item.value+'">'+item.text+'</option>');
			  				 });
			  
            				 // alert("=glAccountCodelist=in loop===="+methodOptionList);
            	 		 $('#AccountCode').html(methodOptionList.join(''));
            		}   
               }
               
             } ,
             error: function() {
            	 	alert(result["_ERROR_MESSAGE_"]);
            	 }
            }); 
           
       }
  //end of AccountCodeList ajax      
	function appendParams(formName, action) {
	var formId = "#" + formName;
	jQuery(formId).attr("action", action);	
	jQuery(formId).submit();
    }
function makeDatePicker(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat:'yy, MM dd',
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
			dateFormat:'yy, MM dd',
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
	function makeDatePicker2(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat: 'M-yy',
			changeMonth: true,
			changeYear: true,
			showButtonPanel: true,
			onClose: function( selectedDate ) {
			    var month = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
	            var year = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
	            $(this).datepicker('setDate', new Date(year, month, 1));
	            $( "#"+thruDateId).datepicker('setDate', new Date(year, month, 1));
			}
		});
	$( "#"+thruDateId ).datepicker({
			dateFormat: 'M-yy',
			changeMonth: true,
			changeYear: true,
			showButtonPanel: true,
			onClose: function( selectedDate ) {
			    var month = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
	            var year = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
	            $(this).datepicker('setDate', new Date(year, month, 1));
			}
		});
	$(".FDate").focus(function () {
	        $(".ui-datepicker-calendar").hide();
	        $("#ui-datepicker-div").position({
	            my: "center top",
	            at: "center bottom",
	            of: $(this)
	        });    
	     });
	     $(".TDate").focus(function () {
	        $(".ui-datepicker-calendar").hide();
	        $("#ui-datepicker-div").position({
	            my: "center top",
	            at: "center bottom",
	            of: $(this)
	        });    
	     });
	}
	//no restriction for thruDate
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
// for Vat Invoice Sequence and Invoice sale reports

function reportTypeChangeFunc() {
	var vatTypeValue = $('#vatType').val();
	if(vatTypeValue == "centralExcise"){
		$('#reportTypeFlag').parent().show();
		$('#categoryType').parent().show();
	}
	else{
	   	$('#reportTypeFlag').parent().hide();
	   	$('#categoryType').parent().hide();
	}
}


//call one method for one time fromDATE And thruDATE

	$(document).ready(function(){

	    makeDatePicker3("FinacialFromDate","FinacialThruDate");
	    makeDatePicker3("advFromDate","advThruDate");
	    makeDatePicker3("subLedgerFromDate","subLedgerThruDate");
	    makeDatePicker("TrlLedgerFromDate","TrlLedgerThruDate");
	    makeDatePicker("glLedgerFromDate","glLedgerThruDate");
	    makeDatePicker3("PFHFromDateCrDr","PFHThruDateCrDr");
	    
		
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
	
	$(document).ready(function verifyFields(){
		$('input[name=submitButton]').click (function verifyFields(){
		var fromMonth = $('#fromMonth').val();
		var thruMonth = $('#thruMonth').val();
	 	if((fromMonth == "")|| (thruMonth == "")){
			alert("Please select dates");
			return false;
			}
		});
 	
	});
	
	$(document).ready(function(){
    	$(".monthPickerTDS").datepicker( {
	        changeMonth: true,
	        changeYear: true,
	        showButtonPanel: true,
	        dateFormat: 'mm-yy',
	        onClose: function(dateText, inst) { 
	            var month = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
	            var year = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
	            $(this).datepicker('setDate', new Date(year, month, 1));
	        }
		});
		$(".monthPickerTDS").focus(function () {
	        $(".ui-datepicker-calendar").hide();
	        $("#ui-datepicker-div").position({
	            my: "center top",
	            at: "center bottom",
	            of: $(this)
	        });    
	     });
	});


	$(document).ready(function verifyFields(){
		$('input[name=submitButton]').click (function verifyFields(){
		var fromMonth = $('#fromMonth').val();
		var thruMonth = $('#thruMonth').val();
	 	if((fromMonth == "")|| (thruMonth == "")){
			alert("Please select dates");
			return false;
			}
		});
 	
	});
</script>
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h2><center>Accounting Reports</center></h2>
    </div>
    <div class="screenlet-body">
		<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
		  <tr class="alternate-row">
				<form id="BankReconciliationReports" name="BankReconciliationReports" method="post" action="<@ofbizUrl>recStatemetn.pdf</@ofbizUrl>" target="_blank">	
					<td width="30%"> Bank  Reconciliation  Report</td>
					<td width="15%">From<input  type="text" size="18pt" id="FinacialFromDate" readonly  name="fromDate"/></td>
				    <td width="15%">To<input  type="text" size="18pt" id="FinacialThruDate" readonly  name="thruDate"/></td>
				    <td width="15%">Bank<select name='finAccountId' id ="finAccountId">	
							<option value=""></option>								
						<#list finAccounts as finAcunt> 	
							<option value='${finAcunt.finAccountId}'>${finAcunt.finAccountName?if_exists}</option>
              		   </#list>
							</select>
						</td>
  					<td width="5%"></td>
					<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('BankReconciliationReports', '<@ofbizUrl>recStatemetn.pdf</@ofbizUrl>');" class="buttontext"/>
					<input type="submit" value="CSV" onClick="javascript:appendParams('BankReconciliationReports', '<@ofbizUrl>FinAccountTransForReconsile.csv</@ofbizUrl>');" class="buttontext"/></td>         			
				</form>
              </tr>
               <tr class="alternate-row">
			      	   <form id="PartyFinancialHistoryWithDrCr" name="PartyFinancialHistoryWithDrCr" method="post" action="<@ofbizUrl>PartyFinancialHistoryWithDrCr.pdf</@ofbizUrl>" target="_blank">        
			             <td width="30%">Party Financial History With Dr/Cr</td>
			             <td width="15%">From<input  type="text" size="15pt" id="PFHFromDateCrDr" readonly  name="partyfromDate"/></td>
			      		 <td width="15%">Thru<input  type="text" size="15pt" id="PFHThruDateCrDr" readonly  name="partythruDate"/></td>
			             <td width="20%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="PartyFinancialHistoryWithDrCr" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>
			            <#--> <input type="text" name="partyId" id="partyId" size="10" maxlength="22"> --></td>
			             <td width="15%"></td>
			             <td width="10%"><input type="submit" value="Download" class="buttontext"/></td>
			           </form>
			        </tr>
              	
              
              <tr class="alternate-row">
				   	<form id="MonthlyTDSAnnexure" name="MonthlyTDSAnnexure" method="post" action="<@ofbizUrl>TDSReport.pdf</@ofbizUrl>" target="_blank">
									<td width="30%">Monthly TDS Annexure<input  type="hidden"  value="MonthlyTDSAnnexure"   name="reportTypeFlag"/></td>
									<td width="15%">From <input type='text' size="18pt" id='fromMonth' name='fromMonth' onmouseover='monthPickerTDS()' class="monthPickerTDS"</td>
			      		 			<td width="15%">To <input type='text' size="18pt" id='thruMonth' name='thruMonth' onmouseover='monthPickerTDS()' class="monthPickerTDS"</td>
			      		 			<td width="15%">Section Code:<select name="sectionCode" id="sectionCode">
			      		 			<option value="TDS_194C">TDS_194C</option>
			      		 			<option value="TDS_194H">TDS_194H</option>
			      		 			<option value="TDS_194J">TDS_194J</option>
			      		 			<option value="TCS_206C">TCS_206C</option>
			      		 			
			      		 			</select></td>  
	 			  					<td width="5%"></td>
									<td width="20%">
										<table>
										<tr><td><input type="submit" value="PDF" onClick="javascript:appendParams('MonthlyTDSAnnexure', '<@ofbizUrl>TDSReport.pdf</@ofbizUrl>');" class="buttontext"/></td></tr>
										<tr><td><input type="submit" value="TaxAnnex(CSV)" onClick="javascript:appendParams('MonthlyTDSAnnexure', '<@ofbizUrl>TDSReportTax.csv</@ofbizUrl>');" class="buttontext"/></td></tr>
										<tr><td><input type="submit" value="DeducteeAnnex(CSV)" onClick="javascript:appendParams('MonthlyTDSAnnexure', '<@ofbizUrl>TDSReportCsv.csv</@ofbizUrl>');" class="buttontext"/></td></tr> 
										</table>   
          						 	</td>
								</form>
				</tr>
								
				<tr class="alternate-row">
				   	<form id="FORM27A" name="FORM 27A" method="post" action="<@ofbizUrl>FORM27A.pdf</@ofbizUrl>" target="_blank">
									<td width="19%">FORM 27 A<input  type="hidden"  value="FORM27APDF"   name="reportTypeFlag"/></td>
									<td width="15%"></td>
									<td width="15%"></td>
			      		 			<td width="15%"></td> 
	 			  					<td width="5%"></td>
									<td width="15%"><input type="submit" value="FORM 27A.PDF" class="buttontext"></td>
						</form>
					</tr>	
		</table>     			     
	</div> 	
</div>
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Advances Reports</h3>
    </div>
    <div class="screenlet-body">
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;" >  
      	<tr class="alternate-row"> 
      		<form id="advancesReport" name="advancesReport" method="post" action="<@ofbizUrl>AdvancesReport.pdf</@ofbizUrl>" target="_blank">	
      		  	<td width="10%">Advances Report</td>
			  	<#--
			  	<td width="25%">Payment Type
			  	  	<select name='paymentTypeId' id ="paymentTypeId">	
					 	<option value="ADVTOVENDOR_PAYOUT">Advances To Vendor</option>								
						<option value="EMPLADV_PAYOUT">Employees Advance</option>
				 	</select>
			  	</td>
			  	-->
			  	<td width="25%">Pmnt Type
			  	  	<select name='paymentTypeId' id ="paymentTypeId">	
					 	<option value=""></option>								
						<#list paymentTypes as paymentType> 	
							<option value='${paymentType.paymentTypeId}'>${paymentType.description?if_exists}</option>
          		   		</#list>
				 	</select>
			  	</td>
			  	
				<td width="15%">From<input  type="text" size="18pt" id="advFromDate" readonly  name="fromDate"/></td>
				<td width="15%">To<input  type="text" size="18pt" id="advThruDate" readonly  name="thruDate"/></td>
          		<td width="5%">&#160;</td>
			  	<td width="25%">
      		  		&#160;
			  	</td>
          		<td width="5%"><input type="submit" value="PDF" class="buttontext"/></td>
      		</form>
      	</tr>
      	<tr> 
      		<form id="subLedgerReport" name="subLedgerReport" method="post" action="<@ofbizUrl>SubLedgerReport.pdf</@ofbizUrl>" target="_blank">	
      		  	<td width="10%">Subledger Report</td>
			  	<td width="25%">Pmnt Type
			  	  	<select name='paymentTypeId' id ="paymentTypeId">	
					 	<option value=""></option>								
						<#list paymentTypes as paymentType> 	
							<option value='${paymentType.paymentTypeId}'>${paymentType.description?if_exists}</option>
          		   		</#list>
				 	</select>
			  	</td>
				<td width="15%">From<input  type="text" size="18pt" id="subLedgerFromDate" readonly  name="fromDate"/></td>
				<td width="15%">To<input  type="text" size="18pt" id="subLedgerThruDate" readonly  name="thruDate"/></td>
          		<td width="5%">PartyId</td>
      		  	<td width="25%">
      		  		<@htmlTemplate.lookupField value='' formName="subLedgerReport" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>
			  	</td>
          		<td width="5%"><input type="submit" value="PDF" class="buttontext"/></td>
      		</form>
      	</tr>
	</table>
</div>
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>General ledger History Report</h3>
    </div>
    <div class="screenlet-body">
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;" >  
       	<tr> 
      	   <form id="GlLedgerReport" name="GlLedgerReport" method="post" action="<@ofbizUrl>AcctgTransEntriesSearchResultsNewPdf.pdf</@ofbizUrl>" target="_blank">	
      		  	<td width="30%">GL History Report<input type="hidden" name="reportType" value="byAccount"/></td>
				<#-- <td width="25%">From<input  type="text" size="18pt" id="glLedgerFromDate" readonly  name="fromDate"/></td>
				<td width="25%">To<input  type="text" size="18pt" id="glLedgerThruDate" readonly  name="thruDate"/></td>-->
      		  	  <td width="50%">CustomTimePeriod
			  	  	<select name='customTimePeriodId' id ="customTimePeriodId" onclick="javascript:setAccountCode(this.value);">	
						<#list customtimeperiodlist as ctplist> 	
							<option value='${ctplist.customTimePeriodId}'>${ctplist.periodName}: ${ctplist.fromDate} - ${ctplist.thruDate}</option>
          		   		</#list>
				 	</select>
			  	  </td>
			  	  <td width="25%"> &#160;</td>
			  	  <td width="25%"> &#160;</td>
			  	  <td width="75%"><input type="hidden" name="organizationPartyId" value="Company"/></td>&#160;</td> 
			</tr>
			 <tr>
			  	 <td width="25%"> &#160;</td>
			  	 <td width="50%">AccountCode
			  	  	<select name='AccountCode' id ="AccountCode">	
					 	
				 	</select>
			  	 </td>
			 	 <td width="15%">Report Type 
					<select name='reportTypeFlag'>
						<option value='condensed'>Condensed</option>
						<option value='detailed'>Detailed</option>
					</select>
				</td>
          		 <td width="25%"><input type="submit" value="PDF" class="buttontext"/></td>
          	</tr>
      		</form>
      	</tr>
	</table>
   </div>
</div>
