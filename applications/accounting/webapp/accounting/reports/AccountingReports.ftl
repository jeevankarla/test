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
				$("#"+thruDateId).datepicker( "option", {minDate: selectedDate}).datepicker('setDate', selectedDate);
			}
		});
	$( "#"+thruDateId ).datepicker({
			dateFormat:'yy, MM dd',
			onSelect: function( selectedDate ) {
				//$( "#"+fromDateId ).datepicker( "option", "maxDate", selectedDate );
			}
		});
	}
//one year restriction
	function makeDatePicker4(fromDateId ,thruDateId){
		$( "#"+fromDateId ).datepicker({
		dateFormat:'yy, MM dd',
		changeMonth: true,
		changeYear: true,
		onSelect: function(selectedDate) {
		date = $(this).datepicker('getDate');
		y = date.getFullYear(),
		m = date.getMonth();
		d = date.getDate();
		    var maxDate = new Date(y+1, m, d);
		
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
	    makeDatePicker("prFromDate","prThruDate");
	    makeDatePicker("aprFromDate","aprThruDate");
	    makeDatePicker("TrlLedgerFromDate","TrlLedgerThruDate");
	    makeDatePicker("glLedgerFromDate","glLedgerThruDate");
	    makeDatePicker3("PFHFromDateCrDr","PFHThruDateCrDr");
	    makeDatePicker4("EMPAdvSehFromDate","EMPAdvSehThruDate");
	    makeDatePicker3("IULFromDateCrDr","IULThruDateCrDr");
	    makeDatePicker3("IULAcntFromDateCrDr","IULAcntThruDateCrDr");
		makeDatePicker4("OtherAdvSehFromDate","OtherAdvSehThruDate");
		makeDatePicker4("TransFromDate","TransThruDate");
		makeDatePicker4("PLAFinFromDate","PLAFinThruDate");
		makeDatePicker4("ASLFinFromDate","PLAFinThruDate");
		makeDatePicker4("invFromDate","invThruDate");
	    makeDatePicker4("invSummFromDate","invSummThruDate");
	    makeDatePicker4("invFromDate","invThruDate");
	    makeDatePicker4("invTmpSummFromDate","invTmpSummThruDate");
	    makeDatePicker4("finSummFromDate","finSummThruDate");
	    makeDatePicker4("GlLedgerFromDate","GlLedgerThruDate");
	    makeDatePicker4("invAppFromDate","invAppFromDate");
	    makeDatePicker4("invAppThruDate","invAppThruDate");
	    makeDatePicker4("PartyLedgerFromDate","PartyLedgerThruDate");
		makeDatePicker4("PartyLedgerCreditorFromDate","PartyLedgerCreditorThruDate");
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
					<td width="15%"><#-- From<input  type="text" size="18pt" id="FinacialFromDate" readonly  name="fromDate"/> --></td>
				    <td width="15%"><#--To<input  type="text" size="18pt" id="FinacialThruDate" readonly  name="thruDate"/> --></td>
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
               		   <form id="GlLedgerReconciliation" name="GlLedgerReconciliation" method="post" action="<@ofbizUrl>GlLedgerReconciliation.pdf</@ofbizUrl>" target="_blank">        
			             <td width="15%" nowrap>GL Ledger Reconciliation </td>
			             <td width="10%">From<input  type="text" size="15pt" id="GlLedgerFromDate" readonly  name="fromDate"/></td>
			      		 <td width="10%">To<input  type="text" size="15pt" id="GlLedgerThruDate" readonly  name="thruDate"/></td>
			             <td width="20%">Channel Type:<div>
					              <select name="purposeTypeId">
					                <option value="">-All-</option>
					                <#list purposeTypeIdsList as purposeType>
					                   <option value="${purposeType.enumId}">${purposeType.description?if_exists}</option>
					                 </#list>
					              </select>
					         </div>
					      </td>
			             <td width="20%">Invoice Type :
			                <div>
				              <select name="invoiceType">
				                <option value="SALES_INVOICE">Sales Invoice</option>
				                <option value="PURCHASE_INVOICE">Purchase Invoice</option>
				              </select>
					       </div>
			             </td>
						 <td width="10%">
					                    <input type="submit" value="CSV" onClick="javascript:appendParams('GlLedgerReconciliation', '<@ofbizUrl>GlLedgerReconciliation.csv</@ofbizUrl>');" class="buttontext"/>
					     </td>			           
                       </form>
			        </tr> 
               <tr class="alternate-row">
			      	   <form id="PartyFinancialHistoryWithDrCr" name="PartyFinancialHistoryWithDrCr" method="post" action="<@ofbizUrl>PartyFinancialHistoryWithDrCr.pdf</@ofbizUrl>" target="_blank">        
			             <td width="30%" nowrap>Party Financial History With Dr/Cr</td>
			             <td width="15%">&nbsp;From<input  type="text" size="15pt" id="PFHFromDateCrDr" readonly  name="partyfromDate"/></td>
			      		 <td width="15%">Thru<input  type="text" size="15pt" id="PFHThruDateCrDr" readonly  name="partythruDate"/></td>
			             <td width="20%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="PartyFinancialHistoryWithDrCr" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>
			            <#--> <input type="text" name="partyId" id="partyId" size="10" maxlength="22"> --></td>
			             <td width="15%"></td>
						 <td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('PartyFinancialHistoryWithDrCr', '<@ofbizUrl>PartyFinancialHistoryWithDrCr.pdf</@ofbizUrl>');" class="buttontext"/>
					                    <input type="submit" value="CSV" onClick="javascript:appendParams('PartyFinancialHistoryWithDrCr', '<@ofbizUrl>PartyFinancialHistoryWithDrCr.csv</@ofbizUrl>');" class="buttontext"/>
					     </td>			           </form>
			        </tr>
			        <#--
              	   <tr class="alternate-row">
			      	   <form id="InterUnitPartyHistoryWithDrCr" name="InterUnitPartyHistoryWithDrCr" method="post" action="<@ofbizUrl>InterUnitPartyHistoryWithDrCr.pdf</@ofbizUrl>" target="_blank">        
			             <td width="30%">Inter Unit Party Account Transactions</td>
			             <td width="15%">From<input  type="text" size="15pt" id="IULFromDateCrDr" readonly  name="fromDate"/></td>
			      		 <td width="15%">Thru<input  type="text" size="15pt" id="IULThruDateCrDr" readonly  name="thruDate"/></td>
			             <td width="20%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="InterUnitPartyHistoryWithDrCr" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>
			             <td width="15%"></td>
			             <td width="10%"><input type="submit" value="Download" class="buttontext"/></td>
			           </form>
			        </tr>
			        -->
			          <tr class="alternate-row">
			      	   <form id="InterUnitLedgerAndAcntHistoryWithDrCr" name="InterUnitLedgerAndAcntHistoryWithDrCr" method="post" action="<@ofbizUrl>InterUnitLedgerAndAcntHistoryWithDrCr.pdf</@ofbizUrl>" target="_blank">        
			             <td width="30%">Inter Unit Party Ledger And Account History</td>
			             <td width="15%">From<input  type="text" size="15pt" id="IULAcntFromDateCrDr" readonly  name="partyfromDate"/></td>
			      		 <td width="15%">Thru<input  type="text" size="15pt" id="IULAcntThruDateCrDr" readonly  name="partythruDate"/></td>
			             <td width="20%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="InterUnitLedgerAndAcntHistoryWithDrCr" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>
			             <td width="15%"></td>
			             
			             <#--><td width="10%"><input type="submit" value="Download" class="buttontext"/></td> -->
			             <td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('InterUnitLedgerAndAcntHistoryWithDrCr', '<@ofbizUrl>InterUnitLedgerAndAcntHistoryWithDrCr.pdf</@ofbizUrl>');" class="buttontext"/>
					                    <input type="submit" value="CSV" onClick="javascript:appendParams('InterUnitLedgerAndAcntHistoryWithDrCr', '<@ofbizUrl>InterUnitLedgerAndAcntHistoryWithDrCr.csv</@ofbizUrl>');" class="buttontext"/>
					     </td>
			           </form>
			        </tr>
			        <tr class="alternate-row">
			      	   <form id="PartyLedgerAbstaracFintHistory" name="PartyLedgerAbstaracFintHistory" method="post" action="<@ofbizUrl>PartyLedgerAbstaracFintHistory.pdf</@ofbizUrl>" target="_blank">        
			             <td width="30%">Party Ledger Abstract</td>
			             <td width="15%">From<input  type="text" size="15pt" id="PLAFinFromDate" readonly  name="partyfromDate"/></td>
			      		 <td width="15%">Thru<input  type="text" size="15pt" id="PLAFinThruDate" readonly  name="partythruDate"/></td>
			             <td width="20%">${uiLabelMap.PartyRoleTypeId} <div>
					              <select name="partyRoleTypeId">
					                <option value="">- ${uiLabelMap.CommonAnyRoleType} -</option>
					                <#list roleTypes as roleType>
					                   <option value="${roleType.roleTypeId}">${roleType.description?if_exists}</option>
					                 </#list>
					              </select>
					            </div>
                           </td> 
                         
			             <td width="15%">
			                <div>
				              <select name="isLedgerCallFor">
				                <option value="ArOnly">AR Ledger</option>
				                <option value="ApOnly">AP Ledger</option>
				              </select>
					       </div>
			             </td>
			             <td width="10%">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<#--> <input type="submit" value="PDF" onClick="javascript:appendParams('PartyLedgerAbstaracFintHistory', '<@ofbizUrl>PartyLedgerAbstaracFintHistory.pdf</@ofbizUrl>');" class="buttontext"/>-->
					                    <input type="submit" value="CSV" onClick="javascript:appendParams('PartyLedgerAbstaracFintHistory', '<@ofbizUrl>PartyLedgerAbstaracFintHistory.csv</@ofbizUrl>');" class="buttontext"/>
					     </td>
			           </form>
			  </tr>
			  <tr class="alternate-row">
			      	   <form id="PartySubLedgerByPurposeType" name="PartySubLedgerByPurposeType" method="post" action="<@ofbizUrl>PartySubLedgerByPurposeType.pdf</@ofbizUrl>" target="_blank">        
			             <td width="30%" nowrap>AR/AP Sub-ledger By Type</td>
			             <td  colspan='3'> 
			             	<table>
			             	<tr>
			             	<td width="20%">Date:<input  type="text" size="15pt" id="ASLFinFromDate" readonly  name="partyfromDate"/></td>
			             	<td>Channel Type:<div>
					              <select name="purposeTypeId">
					                <option value="">-All-</option>
					                <#list purposeTypeIdsList as purposeType>
					                   <option value="${purposeType.enumId}">${purposeType.description?if_exists}</option>
					                 </#list>
					              </select>
					         </div>
					         </td>
					         <td>
					     ${uiLabelMap.PartyRoleTypeId} <div>
					              <select name="partyRoleTypeId">
					                <option value="">- ${uiLabelMap.CommonAnyRoleType} -</option>
					                <#list roleTypes as roleType>
					                   <option value="${roleType.roleTypeId}">${roleType.description?if_exists}</option>
					                 </#list>
					              </select>
					            </div>
					           </td>
					           <td>
					            <div>
						              <select name="isLedgerCallFor">
						                <option value="ArOnly">AR Ledger</option>
						                <option value="ApOnly">AP Ledger</option>
						              </select>
							       </div>
							    </td>
					            </tr>
					            </table>
                          </td>
                          <td width="15%"></td> 
                          <td width="10%">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<#--> <input type="submit" value="PDF" onClick="javascript:appendParams('PartyLedgerAbstaracFintHistory', '<@ofbizUrl>PartySubLedgerByPurposeType.pdf</@ofbizUrl>');" class="buttontext"/>-->
					                    <input type="submit" value="CSV" onClick="javascript:appendParams('PartySubLedgerByPurposeType', '<@ofbizUrl>PartySubLedgerByPurposeType.csv</@ofbizUrl>');" class="buttontext"/>
					     </td>
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
					<tr class="alternate-row">
				<form id="EmployeeAdvancesAndSubSchedule" name="EmployeeAdvancesAndSubSchedule" method="post" action="<@ofbizUrl>EmployeeAdvancesAndSubScheduleReport.pdf</@ofbizUrl>" target="_blank">	
					<td width="30%"> Employee Advances And Sub Sechedule</td>
					<td width="10%">From<input  type="text" size="18pt" id="EMPAdvSehFromDate" readonly  name="fromDate"/></td>
				    <td width="10%">To<input  type="text" size="18pt" id="EMPAdvSehThruDate" readonly  name="thruDate"/></td>
  					<td width="10%">Loan Type :<select name='finAccountTypeId' id ="finAccountTypeId">	
						<#list FinAccountTypeList as finAcunt> 	
							<option value='${finAcunt.finAccountTypeId}'>${finAcunt.description?if_exists}</option>
              		   </#list>
							</select>
						</td>
					  <td width="20%">Employee Id<@htmlTemplate.lookupField size="10" maxlength="22" formName="EmployeeAdvancesAndSubSchedule" name="partyId" id="partyId" fieldFormName="LookupEmployeeName"/> </td>
					   <td width="5%">Report Type 
					    <select name="reportTypeFlag" id="reportTypeFlag1">
						   <option value='Abstract'>Abstract</option>
						   <option value='Detailed'>Detailed</option>
					   </select>
				     </td>
					  <td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('OthersncesAndSubSchedule', '<@ofbizUrl>EmployeeAdvancesAndSubScheduleReport.pdf</@ofbizUrl>');" class="buttontext"/> <input type="submit" value="CSV" onClick="javascript:appendParams('EmployeeAdvancesAndSubSchedule', '<@ofbizUrl>EmployeeAdvancesAndSubScheduleReport.csv</@ofbizUrl>');" class="buttontext"/></td>
					</td>         			
				</form>
              </tr>
              <tr class="alternate-row">
				<form id="OthersncesAndSubSchedule" name="OthersAdvancesAndSubSchedule" method="post" action="<@ofbizUrl>EmployeeAdvancesAndSubScheduleReport.pdf</@ofbizUrl>" target="_blank">	
					<td width="30%"> Others Advances And Sub Sechedule</td>
					<td width="10%">From<input  type="text" size="18pt" id="OtherAdvSehFromDate" readonly  name="fromDate"/></td>
				    <td width="10%">To<input  type="text" size="18pt" id="OtherAdvSehThruDate" readonly  name="thruDate"/></td>
  					<td width="10%">Type :<select name='finAccountTypeId' id ="finAccountTypeId">	
						<#list depositTypesList	as finAcunt>	
	                       <option value='${finAcunt.finAccountTypeId}'>${finAcunt.description?if_exists}</option>
              		   </#list>
							</select>
						</td>
					  <td width="20%">Party Code :<@htmlTemplate.lookupField size="10" maxlength="22" formName="EmployeeAdvancesAndSubSchedule" name="partyId" id="partyId" fieldFormName="LookupPartyName"/> </td>
					   <td width="5%">Report Type 
					    <select name="reportTypeFlag" id="reportTypeFlag1">
						   <option value='Abstract'>Abstract</option>
						   <option value='Detailed'>Detailed</option>
					   </select>
				     </td>
					  <td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('OthersncesAndSubSchedule', '<@ofbizUrl>EmployeeAdvancesAndSubScheduleReport.pdf</@ofbizUrl>');" class="buttontext"/> <input type="submit" value="CSV" onClick="javascript:appendParams('OthersncesAndSubSchedule', '<@ofbizUrl>EmployeeAdvancesAndSubScheduleReport.csv</@ofbizUrl>');" class="buttontext"/></td>
					</td>         			
				</form>
              </tr>
              <tr class="alternate-row">
				<form id="PartyLedgerGroup" name="PartyLedgerGroup" method="post" action="<@ofbizUrl>PartyLedgerGroupReport.pdf</@ofbizUrl>" target="_blank">	
					<td width="30%">Party Ledger Debtor Report</td>
					<td width="10%">From<input  type="text" size="18pt" id="PartyLedgerFromDate" readonly  name="fromDate"/></td>
				    <td width="10%">To<input  type="text" size="18pt" id="PartyLedgerThruDate" readonly  name="thruDate"/></td>
  					<td width="10%">Party Group :<select name="roleTypeId" id="roleTypeId">
  					    <option value=''></option>
  						<#list roleTypeAttrList as list>
                         <option value='${list.roleTypeId}'>${list.description?if_exists}</option>
                         </#list> 
                         <option value='NONROLE'>Non-Role</option>
  						</select></td> 
  						<td width="10%"></td> 
                 <#--    <td width="10%">Report Type 
					    <select name="reportTypeFlag" id="reportTypeFlag">
						   <option value='Ledger'>Ledger</option>
						   <option value='Abstract'>Abstract</option>
					   </select>
                     </td> -->
					  <td width="20%">Party Code :<@htmlTemplate.lookupField size="10" maxlength="22" formName="PartyLedgerGroup" name="partyId" id="partyId" fieldFormName="LookupPartyName"/> </td>
					   <td width="5%">   </td>
					  <td width="10%" align="right"><input type="submit" value="PDF" onClick="javascript:appendParams('PartyLedgerGroup', '<@ofbizUrl>PartyLedgerGroupReport.pdf</@ofbizUrl>');" class="buttontext"/> </td>
					  <td width="5%" align="left"><input type="submit" value="CSV" onClick="javascript:appendParams('PartyLedgerGroup', '<@ofbizUrl>PartyLedgerGroupReport.csv</@ofbizUrl>');" class="buttontext"/></td>
				</form>
              </tr>
             <tr class="alternate-row">
				<form id="PartyLedgerCreditors" name="PartyLedgerCreditors" method="post" action="<@ofbizUrl>PartyLedgerCreditorsReport.pdf</@ofbizUrl>" target="_blank">	
					<td width="30%">Party Ledger Creditors Report</td>
					<td width="10%">From<input  type="text" size="18pt" id="PartyLedgerCreditorFromDate" readonly  name="fromDate"/></td>
				    <td width="10%">To<input  type="text" size="18pt" id="PartyLedgerCreditorThruDate" readonly  name="thruDate"/></td>
  					<td width="10%">Party Group :<select name="roleTypeId" id="roleTypeId">
  					     <option value=''></option>
  						<#list roleTypeAttrList as list>
                         <option value='${list.roleTypeId}'>${list.description?if_exists}</option>
                         </#list> 
                         <option value='NONROLE'>Non-Role</option>
  						</select></td> 
                      <td width="10%"></td> 
					  <td width="20%">Party Code :<@htmlTemplate.lookupField size="10" maxlength="22" formName="PartyLedgerGroup" name="partyId" id="partyId" fieldFormName="LookupPartyName"/> </td>
					   <td width="5%">   </td>
					  <td width="10%" align="right"><input type="submit" value="PDF" onClick="javascript:appendParams('PartyLedgerCreditors', '<@ofbizUrl>PartyLedgerCreditorsReport.pdf</@ofbizUrl>');" class="buttontext"/> </td>
					  <td width="5%" align="left"><input type="submit" value="CSV" onClick="javascript:appendParams('PartyLedgerCreditors', '<@ofbizUrl>PartyLedgerCreditorsReport.csv</@ofbizUrl>');" class="buttontext"/></td>
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
			  	<td width="20%">
					<table>
					<tr><td><input type="submit" value="PDF" onClick="javascript:appendParams('advancesReport', '<@ofbizUrl>AdvancesReport.pdf</@ofbizUrl>');" class="buttontext"/></td></tr>
					<tr><td><input type="submit" value="CSV" onClick="javascript:appendParams('advancesReport', '<@ofbizUrl>AdvancesReport.csv</@ofbizUrl>');" class="buttontext"/></td></tr>
					</table>   
			 	</td>
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
          		<td width="20%">
					<table>
					<tr><td><input type="submit" value="PDF" onClick="javascript:appendParams('subLedgerReport', '<@ofbizUrl>SubLedgerReport.pdf</@ofbizUrl>');" class="buttontext"/></td></tr>
					<tr><td><input type="submit" value="CSV" onClick="javascript:appendParams('subLedgerReport', '<@ofbizUrl>SubLedgerReport.csv</@ofbizUrl>');" class="buttontext"/></td></tr>
					</table>   
			 	</td>
      		</form>
      	</tr>
	</table>
</div>
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Payment Register</h3>
    </div>
    <div class="screenlet-body">
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;" >  
      	<tr class="alternate-row"> 
      		<form id="arPaymentRegister" name="arPaymentRegister" method="post" action="<@ofbizUrl>ArPaymentRegister.pdf</@ofbizUrl>" target="_blank">	
      		  	<td width="10%">Ar Payment Register</td>
			  	<td width="25%">Payment Method Type
			  	  	<select name='paymentMethodTypeId' id ="paymentMethodTypeId">	
					 	<option value=""></option>								
						<#list paymentMethodTypes as paymentMethodType> 	
							<option value='${paymentMethodType.paymentMethodTypeId}'>${paymentMethodType.description?if_exists}</option>
          		   		</#list>
				 	</select>
			  	</td>
				<td width="15%">From<input  type="text" size="18pt" id="prFromDate" readonly  name="fromDate"/></td>
				<td width="15%">To<input  type="text" size="18pt" id="prThruDate" readonly  name="thruDate"/></td>
          		<td width="5%"><input type="submit" value="PDF" class="buttontext"/></td>
      		</form>
      	</tr>
      	<tr> 
      		<form id="apPaymentRegister" name="apPaymentRegister" method="post" action="<@ofbizUrl>ApPaymentRegister.pdf</@ofbizUrl>" target="_blank">	
      		  	<td width="10%">Ap Payment Register</td>
			  	<td width="25%">Fin Account
			  	  	<select name='finAccountId' id ="finAccountId">	
					 	<option value=""></option>								
						<#list finAccountList as finAccount> 	
							<option value='${finAccount.finAccountId}'>${finAccount.finAccountName?if_exists}</option>
          		   		</#list>
				 	</select>
			  	</td>
				<td width="15%">From<input  type="text" size="18pt" id="aprFromDate" readonly  name="fromDate"/></td>
				<td width="15%">To<input  type="text" size="18pt" id="aprThruDate" readonly  name="thruDate"/></td>
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
       	<tr class="alternate-row"> 
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
			 <tr class="alternate-row">
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
   <div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Accounting Reconciliation Reports</h3>
    </div>
    <div class="screenlet-body">
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;" >  
       	
      	<tr> 
      		<form id="GlAccountMasterReport" name="GlAccountMasterReport" method="post" action="<@ofbizUrl>GlAccountMasterReport.pdf</@ofbizUrl>" target="_blank">	
      		  	<td width="10%">GlAccountMaster Report</td>
			  	<td width="25%">Internal Org
			  	  	<select name='partyId' id ="partyId">	
					 	<option value=""></option>								
						<#list intOrgList as intOrg> 	
							<option value='${intOrg.partyId}'>${intOrg.groupName?if_exists}</option>
          		   		</#list>
				 	</select>
			  	</td>
			  	<td width="20%">Gl Account Id<@htmlTemplate.lookupField size="10" maxlength="22" formName="GlAccountMasterReport" name="glAccountId" id="glAccountId" fieldFormName="LookupGlAccount"/>
				<td width="15%">From<input  type="text" size="18pt" id="invFromDate"   name="fromDate"/></td>
				<td width="15%">To<input  type="text" size="18pt" id="invThruDate"   name="thruDate"/></td>
          		
          		<td width="5%"><input type="submit" value="PDF" class="buttontext"/></td>
      		</form>
      	</tr>
      	<#--<tr> 
      		<form id="InvoiceSummaryReport" name="InvoiceSummaryReport" method="post" action="<@ofbizUrl>InvoiceSummaryReport.pdf</@ofbizUrl>" target="_blank">	
      		  	<td width="10%">Invoice Summary Report</td>
			  	<td width="25%">Internal Org
			  	  	<select name='partyId' id ="partyId">	
					 	<option value=""></option>								
						<#list intOrgList as intOrg> 	
							<option value='${intOrg.partyId}'>${intOrg.groupName?if_exists}</option>
          		   		</#list>
				 	</select>
			  	</td>
			  	<td width="25%">Parent Invoice Type
			  	  	<select name='parentTypeId' id ="parentTypeId" >	
			  	  		<option value="">Select</option>
					 	<option value="PURCHASE_INVOICE">Payable</option>
					 	<option value="SALES_INVOICE">Receivable</option>								
				 	</select>
			  	</td>
			  	<td width="25%">Invoice Type
			  	  	<select name='invoiceTypeId' id ="invoiceTypeId">	
					 	<option value=""></option>								
						<#list invoiceTypeList as invoiceType> 	
							<option value='${invoiceType.invoiceTypeId}'>${invoiceType.description?if_exists}</option>
          		   		</#list>
				 	</select>
			  	</td>
				<td width="15%">From<input  type="text" size="18pt" id="invSummFromDate"   name="fromDate"/></td>
				<td width="15%">To<input  type="text" size="18pt" id="invSummThruDate"   name="thruDate"/></td>
          		
          		
          		<td width="20%"><input type="submit" value="PDF" onClick="javascript:appendParams('InvoiceSummaryReport', '<@ofbizUrl>InvoiceSummaryReport.pdf</@ofbizUrl>');" class="buttontext"/>
				                <input type="submit" value="CSV" onClick="javascript:appendParams('InvoiceSummaryReport', '<@ofbizUrl>InvoiceSummaryReport.csv</@ofbizUrl>');" class="buttontext"/>    
                </td>
          		
      		</form>
      	</tr>
      	<tr> 
      		<form id="TempInvoiceSummaryReport" name="TempInvoiceSummaryReport" method="post" action="<@ofbizUrl>TempInvoiceSummaryReport.pdf</@ofbizUrl>" target="_blank">	
      		  	<td width="10%">Temp Invoice Summary Report</td>
			  	<td width="25%">Internal Org
			  	  	<select name='partyId' id ="partyId">	
					 	<option value=""></option>								
						<#list intOrgList as intOrg> 	
							<option value='${intOrg.partyId}'>${intOrg.groupName?if_exists}</option>
          		   		</#list>
				 	</select>
			  	</td>
			  	<td width="25%">Parent Invoice Type
			  	  	<select name='parentTypeId' id ="parentTypeId" >	
			  	  		<option value="">Select</option>
					 	<option value="PURCHASE_INVOICE">Payable</option>
					 	<option value="SALES_INVOICE">Receivable</option>								
				 	</select>
			  	</td>
			  	<#--<td width="25%">Invoice Type
			  	  	<select name='invoiceTypeId' id ="invoiceTypeId">	
					 	<option value=""></option>								
						<#list invoiceTypeList as invoiceType> 	
							<option value='${invoiceType.invoiceTypeId}'>${invoiceType.description?if_exists}</option>
          		   		</#list>
				 	</select>
			  	</td>-->
			  	<#--<td width="25%">Gl Account Class
			  	  	<select name='glAccountClassId' id ="glAccountClassId">	
					 	<option value=""></option>								
						<#list glAccountClassList as glclass> 	
							<option value='${glclass.glAccountClassId}'>${glclass.description?if_exists}</option>
          		   		</#list>
				 	</select>
			  	</td>
			  	
				<td width="15%">From<input  type="text" size="18pt" id="invTmpSummFromDate"   name="fromDate"/></td>
				<td width="15%">To<input  type="text" size="18pt" id="invTmpSummThruDate"   name="thruDate"/></td>
          		
          		
          		<td width="20%"><input type="submit" value="PDF" onClick="javascript:appendParams('TempInvoiceSummaryReport', '<@ofbizUrl>TempInvoiceSummaryReport.pdf</@ofbizUrl>');" class="buttontext"/>
				                <input type="submit" value="CSV" onClick="javascript:appendParams('TempInvoiceSummaryReport', '<@ofbizUrl>TempInvoiceSummaryReport.csv</@ofbizUrl>');" class="buttontext"/>    
                </td>
          		
      		</form>
      	</tr>-->
      	<tr class="alternate-row">
      	   <form id="InvoiceItemTypes" name="InvoiceItemTypes" method="post" action="<@ofbizUrl>invoiceItemTypes.pdf</@ofbizUrl>" target="_blank">        
             <td width="30%">Invoice Item Type</td>
             <td width="25%">Internal Org
			  	  	<select name='partyId' id ="partyId">	
					 	<option value=""></option>								
						<#list intOrgList as intOrg> 	
							<option value='${intOrg.partyId}'>${intOrg.groupName?if_exists}</option>
          		   		</#list>
				 	</select>
			  	</td>
             	<td width="25%">Invoice Type
			  	  	<select name='parentTypeId' id ="parentTypeId">	
			  	  		<option value="">Select</option>
					 	<option value="PURCHASE_INVOICE">Payable</option>
					 	<option value="SALES_INVOICE">Receivable</option>								
				 	</select>
			  	</td>
      		<td width="15%"></td>
             <td width="10%"><input type="submit" value="PDF" class="buttontext"/></td>
           </form>
		</tr>
		<tr class="alternate-row">
      	   <form id="FinAccountSummary" name="FinAccountSummary" method="post" action="<@ofbizUrl>FinAccountSummary.pdf</@ofbizUrl>" target="_blank">        
             <td width="30%">FinAccount Summary</td>
             <td width="25%">Internal Org
			  	  	<select name='partyId' id ="partyId">	
					 	<option value=""></option>								
						<#list intOrgList as intOrg> 	
							<option value='${intOrg.partyId}'>${intOrg.groupName?if_exists}</option>
          		   		</#list>
				 	</select>
			  	</td>
			  	<td width="25%">FinAccount Type
			  	  	<select name='finAccountTypeId' id ="finAccountTypeId">	
					 	<option value=""></option>								
						<#list FinAccountTypesList as finAccountType> 	
							<option value='${finAccountType.finAccountTypeId}'>${finAccountType.description?if_exists}</option>
          		   		</#list>
				 	</select>
			  	</td>
			<td width="15%">From<input  type="text" size="18pt" id="finSummFromDate"   name="fromDate"/></td>
			<td width="15%">To<input  type="text" size="18pt" id="finSummThruDate"   name="thruDate"/></td>
          		  	
			<td width="20%">Gl Account Id<@htmlTemplate.lookupField size="10" maxlength="22" formName="FinAccountSummary" name="glAccountId" id="glAccountId" fieldFormName="LookupGlAccount"/>
            <td width="10%"><input type="submit" value="PDF" class="buttontext"/></td>
           </form>
		</tr>
		<tr class="alternate-row">
      	   <form id="FinAccountTransDetails" name="FinAccountTransDetails" method="post" action="<@ofbizUrl>FinAccountTransDetails.csv</@ofbizUrl>" target="_blank">        
             <td width="30%">Other Units Loan Recovery FinAccountTrans Details</td>
			  	<td width="25%">
               <#--  FinAccount Type
			  	  	<select name='finAccountTypeId' id ="finAccountTypeId">	
					 	<option value=""></option>								
						<#list FinAccountTypesList as finAccountType> 	
							<option value='${finAccountType.finAccountTypeId}'>${finAccountType.description?if_exists}</option>
          		   		</#list>
				 	</select> -->
			  	</td>
			<td width="20%">From<input  type="text" size="18pt" id="TransFromDate"   name="fromDate"/></td>
			<td width="20%">To<input  type="text" size="18pt" id="TransThruDate"   name="thruDate"/></td>
            <td width="20%"></td>		  	
			<td width="20%"></td>
            <td width="10%"><input  type="submit" value="CSV" onClick="javascript:appendParams('FinAccountTransDetails', '<@ofbizUrl>FinAccountTransDetails.csv</@ofbizUrl>');" class="buttontext"/></td>
           </form>
		</tr>
	</table>
   </div>
   <#if security.hasEntityPermission("ACCOUNTING", "_ADMIN", session)>
  <div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Test Reports</h3>
    </div>
    <div class="screenlet-body">
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;" >  
      	<tr class="alternate-row"> 
      		<form id="invoiceAppReport" name="invoiceAppReport" method="post" action="<@ofbizUrl>ArApInvoiceApplication.csv</@ofbizUrl>" target="_blank">	
      		  	<td width="10%">AR/AP Invoice Application Details</td>
			  	<td width="25%">Purpose Type
			  	  	<select name='purposeTypeId' id ="purposeTypeId">	
						<#list purposeTypeIdsList as purposeType>
					        <option value="${purposeType.enumId}">${purposeType.description?if_exists}</option>
					    </#list>
						<#-- <option value='INTUNIT_TR_CHANNEL'>Inter Unit Sale</option>
						<option value='INTER_PRCHSE_CHANNEL'>Inter Unit Purchase</option> -->
				 	</select>
			  	</td>
			  	
				<td width="15%">From<input  type="text" size="18pt" id="invAppFromDate" readonly  name="fromDate"/></td>
				<td width="15%">To<input  type="text" size="18pt" id="invAppThruDate" readonly  name="thruDate"/></td>
          		<td width="5%">&#160;</td>
			  	<td width="25%">
      		  		&#160;
			  	</td>
			  	<td width="20%">
					<table>
					<tr><td><input type="submit" value="CSV" onClick="javascript:appendParams('invoiceAppReport', '<@ofbizUrl>ArApInvoiceApplication.csv</@ofbizUrl>');" class="buttontext"/></td></tr>
					</table>   
			 	</td>
      		</form>
      	</tr>
	</table>
	</div>
	</#if>
</div>