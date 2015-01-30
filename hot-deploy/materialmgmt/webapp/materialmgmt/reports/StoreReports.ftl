<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	

	
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

	    makeDatePicker("purchaseFromDate","purchaseThruDate");
	    makeDatePicker("purchaseFDate","purchaseTDate");
		makeDatePicker("purchaseTaxFDate","purchaseTaxTDate");
		makeDatePicker("purchaseTaxProdFDate","purchaseTaxProdTDate");
		makeDatePicker("purchaseSumFDate","purchaseSumTDate");
		
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
<#if screenFlag?exists && screenFlag.equals("StoreReports")  && (security.hasEntityPermission("MATERIALMNG", "_MNTHREPOR", session))>
<div class="full">

<div class="screenlet">
    <div class="screenlet-title-bar">
      <h2><center>Store Reports</center></h2>
    </div>
    <div class="screenlet-body">
    	
    			<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
    			 
      				 <#if security.hasEntityPermission("MATERIALMNG", "_MNTHREPOR", session)>
      				 <tr class="alternate-row">
						<form id="purchaseRegisterBook" name="purchaseRegisterBook" method="post" action="<@ofbizUrl>purchaseRegisterBook.pdf</@ofbizUrl>" target="_blank">	
							<td width="30%">Purchase Register Book Report</td>
							<td width="15%">From<input  type="text" size="18pt" id="purchaseFromDate" readonly  name="fromDate"/></td>
						    <td width="15%">To<input  type="text" size="18pt" id="purchaseThruDate" readonly  name="thruDate"/></td>
						    <td width="15%"></td>
	      					<td width="15%"></td>
							<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('purchaseRegisterBook', '<@ofbizUrl>purchaseRegisterBook.pdf</@ofbizUrl>');" class="buttontext"/>
							<input type="submit" value="CSV" onClick="javascript:appendParams('purchaseRegisterBook', '<@ofbizUrl>purchaseRegisterBook.csv</@ofbizUrl>');" class="buttontext"/></td>         			
						</form>
	                  </tr> 
		              <tr class="alternate-row">
							<form id="purchaseReport" name="purchaseReport" method="post" action="<@ofbizUrl>purchaseReport.pdf</@ofbizUrl>" target="_blank">	
								<td width="30%">Purchase Analysis Report</td>
								<td width="15%">From<input  type="text" size="18pt" id="purchaseFDate" readonly  name="fromDate"/></td>
							    <td width="15%">To<input  type="text" size="18pt" id="purchaseTDate" readonly  name="thruDate"/></td>
				      			<td width="15%">Report Type 
									<select name='reportTypeFlag' id = "reportTypeFlag">
										<option value='PurchaseSummary'>PurchaseSummary</option>
										<option value='PurchaseDetails'>PurchaseDetails</option>
									</select>
								</td>
								<td width="15%"></td>
								<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('purchaseReport', '<@ofbizUrl>purchaseReport.pdf</@ofbizUrl>');" class="buttontext"/>
							    <input type="submit" value="CSV" onClick="javascript:appendParams('purchaseReport', '<@ofbizUrl>purchaseReport.csv</@ofbizUrl>');" class="buttontext"/></td>         			
							</form>
		                </tr>
				         <tr class="alternate-row">
							<form id="purchaseTaxReport" name="purchaseReport" method="post" action="<@ofbizUrl>purchaseTaxReport.pdf</@ofbizUrl>" target="_blank">	
								<td width="30%">Purchase Tax Classification Report</td>
								<td width="15%">From<input  type="text" size="18pt" id="purchaseTaxFDate" readonly  name="fromDate"/></td>
							    <td width="15%">To<input  type="text" size="18pt" id="purchaseTaxTDate" readonly  name="thruDate"/></td>
				      			<td width="15%">Tax Type
									<select name='taxType' id = "taxType">
									    <option value=''>All</option>
									    <option value='VAT5PT5'>Vat(5.5)</option>
										<option value='VAT14PT5'>Vat(14.5)</option>
										<option value='CST'>CST</option>
										
									</select>
								</td>
								<td width="15%"></td><td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('purchaseTaxReport', '<@ofbizUrl>purchaseTaxReport.pdf</@ofbizUrl>');" class="buttontext"/>
							    <input type="submit" value="CSV" onClick="javascript:appendParams('purchaseTaxReport', '<@ofbizUrl>purchaseTaxReport.csv</@ofbizUrl>');" class="buttontext"/></td> 
								
								</form>
		                </tr>  
		                 <tr class="alternate-row">
							<form id="purchaseReportProductWise" name="purchaseReportProductWise" method="post" action="<@ofbizUrl>purchaseTaxProductReport.pdf</@ofbizUrl>" target="_blank">	
								<td width="30%">Purchase Tax Classification Report(ProductWise)</td>
								<td width="15%">From<input  type="text" size="18pt" id="purchaseTaxProdFDate" readonly  name="fromDate"/>
								<input  type="hidden"  name="purchaseTaxDeptFlag" value="purchaseTaxDeptFlag" /></td>
							    <td width="15%">To<input  type="text" size="18pt" id="purchaseTaxProdTDate" readonly  name="thruDate"/></td>
				      			<td width="15%">Tax Type
									<select name='taxType' id = "taxType">
									    <option value=''>All</option>
									    <option value='VAT5PT5'>Vat(5.5)</option>
										<option value='VAT14PT5'>Vat(14.5)</option>
										<option value='CST'>CST</option>
									</select>
								</td>
								<td width="18%">Dept:
									<select name="issueToDeptId"  id="issueToDeptId"  >
		                               <option value=""></option>
		                               <#list orgList as org>  
		                                 <option value='${org.partyId}'>${org.groupName?if_exists}</option>
		                               </#list>             
		                           </select>
								</td>
								<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('purchaseReportProductWise', '<@ofbizUrl>purchaseTaxProductReport.pdf</@ofbizUrl>');" class="buttontext"/>
							    <input type="submit" value="CSV" onClick="javascript:appendParams('purchaseReportProductWise', '<@ofbizUrl>purchaseTaxProductReport.csv</@ofbizUrl>');" class="buttontext"/></td>         			
								 
							</form>
		                </tr>  
		                <tr class="alternate-row">
							<form id="purchaseSummeryReport" name="purchaseSummeryReport" method="post" action="<@ofbizUrl>purchaseAccountSummery.pdf</@ofbizUrl>" target="_blank">	
								<td width="30%">Purchase Analysis Report Summary</td>
								<td width="15%">From<input  type="text" size="18pt" id="purchaseSumFDate" readonly  name="fromDate"/></td>
							    <td width="15%">To<input  type="text" size="18pt" id="purchaseSumTDate" readonly  name="thruDate"/></td>
							    <td width="15%">ReportType
									<select name='reportNameFlag' id = "reportNameFlag">
									    <option value=''></option>
									    <option value='ProductWise'>ProductWise</option>
										<option value='Detailed'>Detailed</option>
									</select>
								</td>
								<td width="15%"></td>
								<td width="10%"><input type="submit" value="Download" class="buttontext"/></td> 
							</form>
		                </tr>
	  			
					</#if>
				
      			</table>
    		</div>
   	
</div>
</div>
</#if>
  </div>