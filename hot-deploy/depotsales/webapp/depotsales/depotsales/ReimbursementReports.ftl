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


	function appendParams(formName, action) {
		var formId = "#" + formName;
		jQuery(formId).attr("action", action);	
		
		
		
		jQuery(formId).submit();
		
		
	   }
$(document).ready(function(){

	    makeDatePicker("regularIceCreamfDate","regularIceCreamtDate");
	    makeDatePicker("regularIceCreamfDate","regularIceCreamtDate");
	    makeDatePicker("amulIceCreamfDate","amulIceCreamtDate");
	    makeDatePicker("amulIceCreamfDate","amulIceCreamtDate");
	    makeDatePicker("stockFromDate","stockThruDate");
	    makeDatePicker("ivdFromDate","ivdThruDate");
	    makeDatePicker3("subsidyFromDate","subsidyThruDate");
	    makeDatePicker3("claimFromDate","claimThruDate");
	    makeDatePicker3("PFHFromDateCrDr","PFHThruDateCrDr");
	    makeDatePicker3("reimburcentTransporterFRO","reimburcentTransporterTHRU");
	    makeDatePicker3("depotReimburcentReportFRO","depotReimburcentReportTHRU");
	    makeDatePicker3("depotReimburcentSummaryReportFRO","depotReimburcentSummaryReportTHRU");
	    makeDatePicker3("abstrctFromDate","abstrctThruDate");
	    makeDatePicker3("salesPurchaseReportFRO","salesPurchaseReportTHRU");
	    makeDatePicker("stockDate");
	    makeDatePicker("CASHFromDateId","");
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
      	   <form id="reimburcentTransporterReport" name="reimburcentTransporterReport" method="post" action="<@ofbizUrl>reimburcentTransporterReport.pdf</@ofbizUrl>" target="_blank">        
             <td width="10%">Reimbursment Transporter Report</td>
             <td width="10%">&nbsp;From<input  type="text" size="15pt" id="reimburcentTransporterFRO" readonly  name="partyfromDate"  /></td>
      		 <td width="10%">Thru<input  type="text" size="15pt" id="reimburcentTransporterTHRU" readonly  name="partythruDate"  /></td>
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
				<td width="15%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="reimburcentTransporterReport" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>	</td> 
				
				<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('reimburcentTransporterReport', '<@ofbizUrl>reimburcentTransporterReport.pdf</@ofbizUrl>');" class="buttontext"/>
				<input type="submit" value="CSV" onClick="javascript:appendParams('reimburcentTransporterReport', '<@ofbizUrl>reimburcentTransporterReport.csv</@ofbizUrl>');" class="buttontext"/></td>         			
				
           </form>
        </tr> 
   
        <tr class="alternate-row">
      	   <form id="depotReimburcentReport" name="depotReimburcentReport" method="post" action="<@ofbizUrl>depotReimburcentReport.csv</@ofbizUrl>" target="_blank">        
             <td width="10%">Depot Reimbursment Report</td>
             <td width="10%">&nbsp;From<input  type="text" size="15pt" id="depotReimburcentReportFRO" readonly  name="partyfromDate" required /></td>
      		 <td width="10%">Thru<input  type="text" size="15pt" id="depotReimburcentReportTHRU" readonly  name="partythruDate" required /></td>
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
				<td width="15%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="reimburcentTransporterReport" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>	<td width="10%"><input type="submit" value="Download" class="buttontext"/></td> 
           </form>
        </tr> 
        
         <tr class="alternate-row">
      	   <form id="depotReimburcentSummaryReport" name="depotReimburcentSummaryReport" method="post" action="<@ofbizUrl>depotReimburcentSummaryReport.pdf</@ofbizUrl>" target="_blank">        
             <td width="10%">Depot Reimbursment Summary Report</td>
             <td width="10%">&nbsp;From<input  type="text" size="15pt" id="depotReimburcentSummaryReportFRO" readonly  name="partyfromDate" required /></td>
      		 <td width="10%">Thru<input  type="text" size="15pt" id="depotReimburcentSummaryReportTHRU" readonly  name="partythruDate" required /></td>
              <#-- <td width="15%"><span class='h3'>Branch
				 <select name="branchId" id="branchId" required>
					<option value=''></option>
				     <#list  formatList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					 </#list> 
				  </select>    								
			  </span></td>  -->
			   <td width="15%"><span class='h3'>State 
				 <select name="state" id="state">
				     <option value="ALL"></option>
				     <#list  stateListJSON as stateListJSON>
						<option value='${stateListJSON.value?if_exists}'>${stateListJSON.label?if_exists}</option>
					 </#list> 
				  </select>    								
			  </span></td>
			  <td width="15%"><span class='h3'>Category 
				 <select name="productCategory" id="productCategory">
				     <option value='SILK'>SILK</option>
				     <option value='JUTE_YARN'>JUTE</option>
				     <option value='OTHER'>OTHERS</option>
				  </select>    								
			  </span></td>
				 <td width="15%"><span class='h3'>Report Type 
				 <select name="reportType" id="reportType">
				     <option value='DEPOT'>Depot</option>
				     <option value='WITHOUT_DEPOT'>Without Depot</option>
				  </select>    								
			  </span></td>
			  <td> </td>
			<#-- 	<td width="15%">Party Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="depotReimburcentSummaryReport" name="partyId" id="partyId" fieldFormName="LookupPartyName"/> -->
			<td width="10%">
				<input type="submit" value="PDF" onClick="javascript:appendParams('depotReimburcentSummaryReport', '<@ofbizUrl>depotReimburcentSummaryReport.pdf</@ofbizUrl>');" class="buttontext"/>
				<input type="submit" value="CSV" onClick="javascript:appendParams('depotReimburcentSummaryReport', '<@ofbizUrl>depotReimburcentSummaryReport.csv</@ofbizUrl>');" class="buttontext"/>
			</td>	
           </form>
        </tr> 
        
        
	</table>
</div>
