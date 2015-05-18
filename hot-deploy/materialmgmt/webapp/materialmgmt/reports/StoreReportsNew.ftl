<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />
<script type="text/javascript">
	function appendParams(formName, action) {
	var formId = "#" + formName;
	jQuery(formId).attr("action", action);	
	jQuery(formId).submit();
    }
    
    function oldMenumakeDatePicker(fromDateId ,thruDateId){
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
    
//one year restriction
	function makeDatePicker(fromDateId ,thruDateId){
		$( "#"+fromDateId ).datepicker({
		dateFormat:'MM d, yy',
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
	    dateFormat:'MM d, yy',
		changeMonth: true,
		changeYear: true,
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
	//call one method for one time fromDATE And thruDATE

	$(document).ready(function(){

	    
		makeDatePicker("fromDate","thruDate");
		makeDatePicker("icpStockFDate","icpStockTDate");
		makeDatePicker("storeAbstFromDate","storeAbstThruDate");
		makeDatePicker("storeFromDate","storeThruDate");
		makeDatePicker("fromDateMr","thruDateMr");
		makeDatePicker("fromDateStock","thruDateStock");
		makeDatePicker("fromDateArc","thruDateArc");
	    makeDatePicker("datependingPOs");
		makeDatePicker("stockDate");
		makeDatePicker("fromDateScrap","thruDateScrap");
		
		
		oldMenumakeDatePicker("purchaseFromDate","purchaseThruDate");
	    oldMenumakeDatePicker("purchaseFDate","purchaseTDate");
		oldMenumakeDatePicker("purchaseTaxFDate","purchaseTaxTDate");
		oldMenumakeDatePicker("purchaseTaxProdFDate","purchaseTaxProdTDate");
		oldMenumakeDatePicker("purchaseSumFDate","purchaseSumTDate");
		oldMenumakeDatePicker("purchaseSumFDateNew","purchaseSumTDateNew");
	//	makeDatePicker3("purchaseSumFDateNew","purchaseSumTDateNew");
		oldMenumakeDatePicker("cwsFDate","cwsTDate");
		oldMenumakeDatePicker("purchaseVatFDate","purchaseVatTDate");
		oldMenumakeDatePicker("purchaseVatCatFDate","purchaseVatCatTDate");
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
<div>
  <div class="screenlet">
	<div class="screenlet-title-bar">
		<h3>New Reports</h3>
	</div>
	<div class="screenlet-body">
		<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
			<tr class="alternate-row"> 
				<form id="stockPositionReport" name="stockPositionReport" mothed="post" action="<@ofbizUrl>stockPositionReport.pdf</@ofbizUrl>" target="_blank">
					<table class="basic-table" cellspacing="5">
						<tr class="alternate-row">
							<td width="20%"><span class='h3'>Stock Position Report</span></td>
							<td width="20%">
							     <span class='h3'>
									Date <input  type="text" size="18pt" id="stockDate"   name="fromDate"/>
								 </span>
							</td>	
							<td width="15%"><span class='h3'>Type :<select name="reportTypeFlag" id="reportTypeFlag">
							        <option value="WITHOUTZEROS">Greater Than Zeros</option>
							        <option value="WITHZEROS">Zeros</option>
							    </select></span></td>		
							<td width="15%"><span class='h3'>Store
							    <select name="issueToFacilityId" id="issueToFacilityId">
							        <option value=""></option>
							        <#list  storeList as store>
							          <option value='${store.facilityId?if_exists}'>${store.facilityId?if_exists}</option>
							        </#list> 
							    </select>    								
						   </span></td>
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
			<tr class="alternate-row"> 
				<form id="StoreIssueReport" name="StoreIssueReport" mothed="post" action="<@ofbizUrl>StoreIssueReport.pdf</@ofbizUrl>" target="_blank">
					<table class="basic-table" cellspacing="5">
						<tr class="alternate-row">
							<td width="20%"><span class='h3'>Store Receipts And Issue Register</span></td>
							
							<td width="25%">
							     <span class='h3'>
									From <input  type="text" size="18pt" id="fromDate"   name="fromDate"/>
									To   <input  type="text" size="18pt" id="thruDate"   name="thruDate"/>
								 </span>
							</td>
							
						    <td width="15%"><span class='h3'>Material Code *<@htmlTemplate.lookupField size="10" maxlength="22" formName="StoreIssueReport" name="productId" id="productId" fieldFormName="LookupProduct"/>
							</span></td>
							<td width="15%"><span class='h3'>Store
							    <select name="issueToFacilityId" id="issueToFacilityId">
							        <option value=""></option>
							        <#list  storeList as store>
							          <option value='${store.facilityId?if_exists}'>${store.facilityId?if_exists}</option>
							        </#list> 
							    </select>    								
						   </span></td>
						    <td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
						</tr>
					</table>
				</form>
			</tr>
			<tr class="alternate-row"> 
				<form id="StoreIssueAbstractReport" name="StoreIssueAbstractReport" mothed="post" action="<@ofbizUrl>StoreIssueAbstractReport.pdf</@ofbizUrl>" target="_blank">
					<table class="basic-table" cellspacing="5">
						<tr class="alternate-row">
							<td width="20%"><span class='h3'>Store Issue Abstract Report</span></td>
							<td width="25%">
							     <span class='h3'>
									From <input  type="text" size="18pt" id="storeAbstFromDate"   name="storeAbstFromDate"/>
									To   <input  type="text" size="18pt" id="storeAbstThruDate"   name="storeAbstThruDate"/>
								 </span>
							</td>
							<td width="15%"><span class='h3'>ledgerFolioNos
							    <select name="ledgerFolioNo" id="ledgerFolioNo">
							        <option value=""></option>
							        <#list  ledgerFolioList as ledgerFolioNos>
							          <option value='${ledgerFolioNos?if_exists}'>${ledgerFolioNos?if_exists}</option>
							        </#list> 
							    </select>    								
					  	 </span></td>
							<td width="15%"><span class='h3'>Store
							    <select name="issueToFacilityId" id="issueToFacilityId">
							        <option value=""></option>
							        <#list  storeList as store>
							          <option value='${store.facilityId?if_exists}'>${store.facilityId?if_exists}</option>
							        </#list> 
							    </select>    								
						   </span></td>
						    <td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
						</tr>
					 </table>
				</form>
			</tr>
			<tr class="alternate-row"> 
				<form id="StoreReport" name="StoreReport" mothed="post" action="<@ofbizUrl>StoreReport.pdf</@ofbizUrl>" target="_blank">
				   <table class="basic-table" cellspacing="5">
					  <tr class="alternate-row">
					     <td width="10%"><span class='h3'>Store Issue Report(Category)</span></td>
						 <td width="25%">
						 <span class='h3'>
						    From <input  type="text" size="18pt" id="storeFromDate"   name="storeFromDate"/>
							To   <input  type="text" size="18pt" id="storeThruDate"   name="storeThruDate"/>
						 </span>
						 </td>
						 <td width="12%"><span class='h3'>Department
							    <select name="partyId" id="partyId">
							        <option value=""></option>
							        <#list  finalDepartmentList as eachDeparment>
							          <option value='${eachDeparment.partyId?if_exists}'>${eachDeparment.groupName?if_exists}</option>
							        </#list> 
							    </select>    								
					  	 </span></td>
						 <td width="6%"><span class='h3'>Store
							    <select name="issueToFacilityId" id="issueToFacilityId">
							        <option value=""></option>
							        <#list  storeList as store>
							          <option value='${store.facilityId?if_exists}'>${store.facilityId?if_exists}</option>
							        </#list> 
							    </select>    								
						   </span></td>
						   <td width="8%"><span class='h3'>Type :<select name="reportTypeFlag" id="reportTypeFlag">
							        <option value="Detailed">Detailed</option>
							         <option value="Abstract">Abstract</option>
							    </select></span></td>
						 <td width="8%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
		 		    </tr>
				 </table>
			 </form>
		  </tr>
		  <tr class="alternate-row"> 
				<form id="storeStockDetails" name="storeStockDetails" mothed="post" action="<@ofbizUrl>storeStockDetails.pdf</@ofbizUrl>" target="_blank">
					<table class="basic-table" cellspacing="5">
						<tr class="alternate-row">
							<td width="20%"><span class='h3'>Store Stock Status</span></td>
							<td width="20%"><span class='h3'>Type :<select name="reportTypeFlag" id="reportTypeFlag">
							        <option value="WITHOUTZEROS">Greater Than Zeros</option>
							        <option value="WITHZEROS">Zeros</option>
							    </select></span></td>
							<td width="15%"><span class='h3'>							</span></td>
                            <td width="15%"><span class='h3'>Store
							    <select name="issueToFacilityId" id="issueToFacilityId">
							        <option value=""></option>
							        <#list  storeList as store>
							          <option value='${store.facilityId?if_exists}'>${store.facilityId?if_exists}</option>
							        </#list> 
							    </select>    								
						   </span></td>						    <td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
						</tr>
					 </table>
				</form>
			</tr>
			<tr class="alternate-row"> 
				<form id="MRRregister" name="MRRregister" mothed="post" action="<@ofbizUrl>MRRregister.pdf</@ofbizUrl>" target="_blank">
				   <table class="basic-table" cellspacing="5">
					  <tr class="alternate-row">
					     <td width="20%"><span class='h3'>MRRregister</span></td>
						 <td width="25%">
						 <span class='h3'>
						    From <input  type="text" size="18pt" id="fromDateMr"   name="fromDateMr"/>
							To   <input  type="text" size="18pt" id="thruDateMr"   name="thruDateMr"/>
						 </span>
						 </td>
						    <td width="8%"><span class='h3'>Material Code<@htmlTemplate.lookupField size="10" maxlength="22" formName="MRRregister" name="productId" id="productId" fieldFormName="LookupProduct"/>
                            <td width="7%"><span class='h3'>Unions <input type="checkbox" id="Unions" size="10pt"  name="Unions">
                           
                           <td width="15%"><span class='h3'>Store
							    <select name="issueToFacilityId" id="issueToFacilityId">
							        <option value=""></option>
							        <#list  storeList as store>
							          <option value='${store.facilityId?if_exists}'>${store.facilityId?if_exists}</option>
							        </#list> 
							    </select>    								
						   </span></td>							
                           <td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
		 		    </tr>
				 </table>
			 </form>
		  </tr>
		  
		 <tr class="alternate-row"> 
				<form id=" ListofpendingPOs" name="ListofpendingPOs" mothed="post" action="<@ofbizUrl>listofpendingPOs.pdf</@ofbizUrl>" target="_blank">
				   <table class="basic-table" cellspacing="5">
					  <tr class="alternate-row">
					     <td width="20%"><span class='h3'> List of pending PO's</span></td>
                          <td width="25%">
						 <span class='h3'>
						    Date <input  type="text" size="18pt" id="datependingPOs"   name="datependingPOs"/>
						 </span>
						 </td>				   
					 <td width="15%"><span class='h3'>	</span></td>
				     <td width="15%"><span class='h3'>  </span></td>	
						 <td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
		 		    </tr>
				 </table>
			 </form>
		  </tr>  	
		  		  
		  <tr class="alternate-row"> 
				<form id="PurchaseOrder" name="PurchaseOrder" mothed="post" action="<@ofbizUrl>PurchaseOrderView.pdf</@ofbizUrl>" target="_blank">
				   <table class="basic-table" cellspacing="5">
					  <tr class="alternate-row">
					     <td width="20%"><span class='h3'>Purchase Order</span></td>
					     <td width="25%"><span class='h3'>PO No<input type="textfield" id="orderId"name="orderId"></span></td>
				    <td width="15%"><span class='h3'>Signature<input type="textfield" id="sign" name="sign"></span></td>
				     <td width="15%"><span class='h3'>   				       </span></td>	
						 <td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
		 		    </tr>
				 </table>
			 </form>
		  </tr>
		  
		  <tr class="alternate-row"> 
				<form id="ArcOrder" name="ArcOrder" mothed="post" action="<@ofbizUrl>arcOrder.pdf</@ofbizUrl>" target="_blank">
				   <table class="basic-table" cellspacing="5">
					  <tr class="alternate-row">
					     <td width="20%"><span class='h3'>ARC Order</span></td>
		     	     
				    <td width="25%"><span class='h3'>PO No<input type="textfield" id="orderId"name="orderId"></span></td>
				    <td width="15%"><span class='h3'>Signature<input type="textfield" id="sign" name="sign"></span></td>
				   
				    <td width="15%"><span class='h3'>Subject Line<input type="textfield" id="subject" name="subject"></span></td>
				    
						 <td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
		 		    </tr>
				 </table>
			 </form>
		  </tr>
		  
		   <tr class="alternate-row"> 
				<form id="pendingQuantityARC" name="pendingQuantityARC" mothed="post" action="<@ofbizUrl>pendingQuantityARC.pdf</@ofbizUrl>" target="_blank">
				   <table class="basic-table" cellspacing="5">
					  <tr class="alternate-row">
					     <td width="20%"><span class='h3'>PO against ARC/CPC</span></td>
		     	     
				    <td width="25%"><span class='h3'>PO No<input type="textfield" id="orderId"name="orderId"></span></td>
				    <td width="15%"><span class='h3'>							</span></td>
				     <td width="15%"><span class='h3'>   				       </span></td>	
				    
						 <td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
		 		    </tr>
				 </table>
			 </form>
		  </tr>
		  
		  <tr class="alternate-row"> 
				<form id="letterOfAcceptance" name="letterOfAcceptance" mothed="post" action="<@ofbizUrl>letterOfAcceptance.pdf</@ofbizUrl>" target="_blank">
				   <table class="basic-table" cellspacing="5">
					  <tr class="alternate-row">
					     <td width="20%"><span class='h3'>Letter Of Acceptance</span></td>
					    <td width="25%"><span class='h3'>PO No<input type="textfield" id="orderId"name="orderId"></span></td>
				    <td width="15%"><span class='h3'>							</span></td>
				     <td width="15%"><span class='h3'>   				       </span></td>	
						 <td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
		 		    </tr>
				 </table>
			 </form>
		  </tr>
		  
		  <tr class="alternate-row"> 
				<form id="extensionPurchaseOrder" name="extensionPurchaseOrder" mothed="post" action="<@ofbizUrl>extensionPurchaseOrder.pdf</@ofbizUrl>" target="_blank">
				   <table class="basic-table" cellspacing="5">
					  <tr class="alternate-row">
					     <td width="20%"><span class='h3'>Extension Purchase Order</span></td>
					    						</span></td>
				    <td width="25%"><span class='h3'>PO No<input type="textfield" id="orderId"name="orderId"></span></td>
				     <td width="15%"><span class='h3'>	
						 <td width="15%"><span class='h3'>Type 
					<select name='subscriptionTypeId' class='h4'>
					<option value='poReport'>PO REPORT</option>
					<option value='poSpecific'>PO SPECIFICATON</option>
					</select></span>
				</td>
				 <td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
		 		    </tr>
				 </table>
			 </form>
		  </tr>
		  <tr class="alternate-row"> 
				<form id="AmendedPOReport" name="AmendedPOReport" mothed="post" action="<@ofbizUrl>AmendedPOReport.pdf</@ofbizUrl>" target="_blank">
				   <table class="basic-table" cellspacing="5">
					  <tr class="alternate-row">
					     <td width="20%"><span class='h3'>Amended PO Report</span></td>
						 <td width="25%">
							 <span class='h3'>PO No.<input type="textfield"  id="orderId"  name="orderId"/></span>   								
						  </td>
						  <td width="15%"><span class='h3'>							</span></td>
						      <td width="15%"><span class='h3'>   				       </span></td>	
						 <td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
		 		    </tr>
				 </table>
			 </form>
		  </tr>
		  <tr class="alternate-row"> 
				<form id="LetterOfIndentReport" name="LetterOfIndentReport" mothed="post" action="<@ofbizUrl>LetterOfIndentReport.pdf</@ofbizUrl>" target="_blank">
				   <table class="basic-table" cellspacing="5">
					  <tr class="alternate-row">
					     <td width="20%"><span class='h3'>Letter Of Intent </span></td>
						 <td width="25%">
							 <span class='h3'>PO No.<input type="textfield"  id="orderId"  name="orderId"/></span>   								
						  </td>
						  <td width="15%"><span class='h3'>							</span></td>
						      <td width="15%"><span class='h3'>   				       </span></td>	
						 <td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
		 		    </tr>
				 </table>
			 </form>
		  </tr>
		  <tr class="alternate-row"> 
				<form id="SupplierEvaluationReport" name="SupplierEvaluationReport" mothed="post" action="<@ofbizUrl>SupplierEvaluationReport.pdf</@ofbizUrl>" target="_blank">
				   <table class="basic-table" cellspacing="5">
					  <tr class="alternate-row">
					     <td width="20%"><span class='h3'>Supplier Evaluation Report</span></td>
						 <td width="25%">
							 <span class='h3'>PO No.<input type="textfield"  id="orderId"  name="orderId"/></span>   								
						  </td>
						  <td width="15%"><span class='h3'>							</span></td>
						      <td width="15%"><span class='h3'>   				       </span></td>	
						 <td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
		 		    </tr>
				 </table>
			 </form>
		  </tr>
		  <tr class="alternate-row"> 
				<form id="MaterialEnquiryReport" name="PurchaseEnquiryReport" mothed="post" action="<@ofbizUrl>MaterialEnquiryReport.pdf</@ofbizUrl>" target="_blank">
				   <table class="basic-table" cellspacing="5">
					  <tr class="alternate-row">
					     <td width="20%"><span class='h3'>Material Enquiry Report</span></td>
					     
						 <td width="25%">
							 <span class='h3'>Enquiry No.<input type="textfield"  id="issueToCustReqId"  name="issueToCustReqId"/></span>   	
							 							
						  </td>
						   <td width="15%"><span class='h3'>Signature<input type="textfield" id="signature" name="signature"></span></td>
						   <td width="15%"><span class='h3'>   				       </span></td>	
						  
						 <td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
		 		    </tr>
				 </table>
			 </form>
		  </tr>
		  <tr class="alternate-row"> 
				<form id="EnquiryAbstractReport" name="EnquiryAbstractReport" mothed="post" action="<@ofbizUrl>EnquiryReport.pdf</@ofbizUrl>" target="_blank">
				   <table class="basic-table" cellspacing="5">
					  <tr class="alternate-row">
					     <td width="20%"><span class='h3'>Enquiry Abstract Report</span></td>
						 <td width="25%">
							 <span class='h3'>Enquiry No.<input type="textfield"  id="issueToCustReqId"  name="issueToCustReqId"/></span>   								
						  </td>
						   <td width="15%"><span class='h3'>Signature<input type="textfield" id="signature" name="signature">							</span></td>
						      <td width="15%"><span class='h3'>   				       </span></td>	
						 <td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
		 		    </tr>
				 </table>
			 </form>
		  </tr>
		  <tr class="alternate-row"> 
				<form id="EnquiryNoReport" name="EnquiryNoReport" mothed="post" action="<@ofbizUrl>EnquiryNoReport.pdf</@ofbizUrl>" target="_blank">
				   <table class="basic-table" cellspacing="5">
					  <tr class="alternate-row">
					     <td width="20%"><span class='h3'>Comparative Statement Report</span></td>
						 <td width="25%">
							 <span class='h3'>Enquiry No.<input type="textfield"  id="issueToEnquiryNo"  name="issueToEnquiryNo"/></span>   								
						  </td>
						   <td width="15%"><span class='h3'>Signature<input type="textfield" id="sign" name="sign"></span></td>
						      <td width="15%"><span class='h3'>   				       </span></td>	
						 <td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
		 		    </tr>
				 </table>
			 </form>
		  </tr>
		  <tr class="alternate-row"> 
				<form id="MaterialIndentReport" name="MaterialIndentReport" mothed="post" action="<@ofbizUrl>MaterialIndentReport.pdf</@ofbizUrl>" target="_blank">
				   <table class="basic-table" cellspacing="5">
					  <tr class="alternate-row">
					     <td width="20%"><span class='h3'>Material Indent Report</span></td>
						 <td width="25%">
							 <span class='h3'>Indent No.<input type="textfield"  id="IndentNo"  name="IndentNo"/></span>   								
						  </td>
						  <td width="15%"><span class='h3'>							</span></td>
						      <td width="15%"><span class='h3'>   				       </span></td>	
						 <td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
		 		    </tr>
				 </table>
			 </form>
		  </tr>
         <tr class="alternate-row"> 
				<form id="ScrapDCandGatePassReport" name="ScrapDCandGatePassReport" mothed="post" action="<@ofbizUrl>ScrapDCandGatePassReport.pdf</@ofbizUrl>" target="_blank">
				   <table class="basic-table" cellspacing="5">
					  <tr class="alternate-row">
					     <td width="20%"><span class='h3'>Scrap DC and Gate Pass </span></td>
						 <td width="25%">
						 <span class='h3'>
						    From <input  type="text" size="18pt" id="fromDateScrap"   name="fromDateScrap"/>
							To   <input  type="text" size="18pt" id="thruDateScrap"   name="thruDateScrap"/>
						 </span>
						 </td>
						  <td width="15%"><span class='h3'>							</span></td>
						      <td width="15%"><span class='h3'>   				       </span></td>	
						 <td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
		 		    </tr>
				 </table>
			 </form>
		  </tr>
		    <tr class="alternate-row">
				<form id="icpStockStatement" name="icpStockStatement" method="post" action="<@ofbizUrl>icpStockStatement.pdf</@ofbizUrl>" target="_blank">        
                   <table class="basic-table" cellspacing="5">
                         <tr class="alternate-row">
                               <td width="20%"><span class='h3'>Icp Stock Statement Report</span></td>
                               <td width="17%"><span class='h3'>From<input  type="text" size="18pt" id="icpStockFDate" readonly  name="fromDate"/></span></td>
                               <td width="17%"><span class='h3'>To<input  type="text" size="18pt" id="icpStockTDate" readonly  name="thruDate"/></span></td>
                               <td width="20%">By<select name="categoryType">
				      			<option value="ICE_CREAM_NANDINI">Nandini Ice Cream</option>
				      			<option value="ICP_AMUL_CHANNEL">Amul Ice Cream</option>
									</select>
                               </td>
                               <td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"/></span></td> 		 	
                        </tr>
				  </table>
                </form>
		   </tr>
	  </table>
    </div>
  </div>
  <div class="screenlet">
	<div class="screenlet-title-bar">
		<h3>Old Menu Reports</h3>
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
									     <option value='VAT5PT0'>Vat(5.0)</option>
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
									    <option value='VAT5PT0'>Vat(5.0)</option>
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
                                                                               <option value='Detailed'>Detailed</option>
                                                                                 <option value='ProductWise'>ProductWise</option>
                                                                       </select>
                                                               </td>
                                                               <td width="15%"></td>
                                                               <td width="10%"><input type="submit" value="Download" class="buttontext"/></td> 
                                                       </form>
		                </tr>
		                	</#if>
		               <#if (security.hasEntityPermission("MMPURCHSSMMRY", "_VIEW", session) )>  
		                 <tr class="alternate-row">
		                <form id="purchaseSummeryReportNew" name="purchaseSummeryReportNew" method="post" action="<@ofbizUrl>purchaseAccountSummeryNew.pdf</@ofbizUrl>" target="_blank">        
                                                               <td width="30%">Purchase Analysis Report Summary New</td>
                                                               <td width="15%">From<input  type="text" size="18pt" id="purchaseSumFDateNew" readonly  name="fromDate"/></td>
                                                           <td width="15%">To<input  type="text" size="18pt" id="purchaseSumTDateNew" readonly  name="thruDate"/></td>
                                                           <td width="15%">ReportType
                                                                       <select name='reportNameFlag' id = "reportNameFlag">
                                                                           <option value=''></option>
                                                                               <option value='Detailed'>Detailed</option>
                                                                                 <option value='ProductWise'>ProductWise</option>
                                                                       </select>
                                                               </td>
                                                               <td width="15%"></td>
															<td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('purchaseSummeryReportNew', '<@ofbizUrl>purchaseAccountSummeryNew.pdf</@ofbizUrl>');" class="buttontext"/>
							   								 <input type="submit" value="CSV" onClick="javascript:appendParams('purchaseSummeryReportNew', '<@ofbizUrl>purchaseAccountSummeryNew.csv</@ofbizUrl>');" class="buttontext"/></td>         			                                                       </form>
		                </tr>
		                </#if> 
	  			       <#if (security.hasEntityPermission("MMPURCHASEVAT", "_VIEW", session) )>
		                 <tr class="alternate-row">
							<form id="purchaseVatReport" name="purchaseVatReport" method="post" action="<@ofbizUrl>purchaseVatReport.pdf</@ofbizUrl>" target="_blank">        
                               <td width="30%">Purchase Vat Report</td>
                               <td width="15%">From<input  type="text" size="18pt" id="purchaseVatFDate" readonly  name="fromDate"/></td>
                               <td width="15%">To<input  type="text" size="18pt" id="purchaseVatTDate" readonly  name="thruDate"/></td>
                               <td width="15%">ReportType
                                       <select name='reportTypeFlag' id = "reportTypeFlag">
                                             <option value='ELIGIBLE'>Eligible</option>
                                             <option value='INELIGIBLE'>Ineligible</option>
                                       </select>
                                </td>
                                <td width="15%"></td>
                                <td width="10%"><input type="submit" value="Download" class="buttontext"/></td> 
                           </form>
		                </tr>
		                </#if>
				       <tr class="alternate-row">
							<form id="purchaseVatCategoryReport" name="purchaseVatCategoryReport" method="post" action="<@ofbizUrl>purchaseVatCategoryReport.pdf</@ofbizUrl>" target="_blank">        
                               <td width="30%">Purchase Vat Catogiry Wise Report</td>
                               <td width="15%">From<input  type="text" size="18pt" id="purchaseVatCatFDate" readonly  name="fromDate"/></td>
                               <td width="15%">To<input  type="text" size="18pt" id="purchaseVatCatTDate" readonly  name="thruDate"/></td>
                               <td width="15%">ReportType
                                       <select name='reportTypeFlag' id = "reportTypeFlag">
                                             <option value='ELIGIBLE'>Eligible</option>
                                             <option value='INELIGIBLE'>Ineligible</option>
                                       </select>
                                </td>
                                <td width="15%"></td>
                          <td width="10%"><input type="submit" value="PDF" onClick="javascript:appendParams('purchaseVatCategoryReport', '<@ofbizUrl>purchaseVatCategoryReport.pdf</@ofbizUrl>');" class="buttontext"/>
							   								 <input type="submit" value="CSV" onClick="javascript:appendParams('purchaseVatCategoryReport', '<@ofbizUrl>purchaseVatCategoryReport.csv</@ofbizUrl>');" class="buttontext"/></td>         			                                                       </form>
                              </form>
		                </tr>
		                
		
				<tr class="alternate-row">
						<form id="purchasechannelWiseSales" name="purchasechannelWiseSales" method="post" action="<@ofbizUrl>purchaseChannelWiseSales.pdf</@ofbizUrl>" target="_blank">	
							<td width="30%">Purchase Channel Wise Sales Report</td>
							<td width="15%">From<input  type="text" size="18pt" id="cwsFDate" readonly  name="fromDate"/></td>
						    <td width="15%">To<input  type="text" size="18pt" id="cwsTDate" readonly  name="thruDate"/></td>
						    <td width="15%">Product Type<select name='productType' id = "productType" >
						        <option value="RAW_MATERIAL">Raw Material</option>
				      			<option value="FINISHED_GOOD">Finished Goods</option>
				      			</select>
				      		</td>
							<td width="15%">Product<@htmlTemplate.lookupField size="10" maxlength="22" formName="purchasechannelWiseSales" name="productId" id="productId" fieldFormName="LookupProduct"/></td>
							 <td width="15%">Role Type<select name='roleType' id = "roleType" >
		                	    <option value="All">All</option>
		                	    <option value="UNITS">Inter Unit Transfer</option>
				      			<option value="UNION">Sale to Union</option>
				      			<option value="VENDOR">Vendor</option>
				      			<option value="SUPPLIER">Supplier</option>
				      			<option value="TRADE_CUSTOMER">Trade Customer</option>  
				      			
				      			</select>
				      		</td>
							<tr><td width="75%"></td>
							<td width="75%"></td>
							<td width="75%"></td>
							<td width="75%"></td>
							<td width="15%">Category<select name="categoryType">${productCategoryTypeList}
				      			 <#list productCategoryTypeList as productCategoryType>   
		                  	    	<option value='${productCategoryType.productCategoryId}'>
			                    		${productCategoryType.productCategoryId?if_exists}
			                        </option>
		                	     </#list>     
			      			</select></td>
							<td width="10%"><input type="submit" value="Download" class="buttontext"/></td> 
							</tr>      			
						
						</form>
				    </tr>
      			</table>
	</div>
	</div>
</div> 		