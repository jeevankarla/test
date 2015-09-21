<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />
<script type="text/javascript">
	function appendParams(formName, action) {
	var formId = "#" + formName;
	jQuery(formId).attr("action", action);	
	jQuery(formId).submit();
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
//one month restriction 
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
		makeDatePicker("contractorFromDate","contractorThruDate");
		makeDatePicker("vehicleFromDate","vehicleThruDate");
		makeDatePicker("unionsFromDate","unionsThruDate");
		makeDatePicker("milkProcessRegDate","");
		makeDatePicker("purchaseBillingFromDate","purchaseBillingThruDate");
		makeDatePicker("mateBalanceFromDate","mateBalanceThruDate");
		$('#ui-datepicker-div').css('clip', 'auto');		
	});

	
	
</script>	
<div>
  <div class="screenlet">
	<div class="screenlet-title-bar">
		<h3>PTC Reports</h3>
	</div>
	<div class="screenlet-body">
		<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
			<tr class="alternate-row"> 
				<form id="PTCTankerReport" name="PTCTankerReport" mothed="post" action="<@ofbizUrl>ptcVehicleContractorWiseReport.pdf</@ofbizUrl>" target="_blank">
					<table class="basic-table" cellspacing="5">
						<tr class="alternate-row">
							<td width="23%"><span class='h3'>PTC Vehicle Contractor Wise Report</span></td>
							
							<td width="35%">
							
							 <span class='h3'>Time Period </span>
	                    <select name="customTimePeriodId" class='h4'>
	                    <#if timePeriodList?has_content>	
	                        <#list timePeriodList as timePeriod>    
	                			  <#if ((timePeriod.fromDate)?has_content) && ((timePeriod.thruDate)?has_content)>
	                			   <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
	                       			<#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
			                  	    <option value='${timePeriod.customTimePeriodId}' >
			                    		${fromDate}-${thruDate}
			                  		 </option>
			                  	 </#if>
	                		</#list>    
	                	</#if>	    
	                    </select>
							
					<#-->		     <span class='h3'>
									From <input  type="text" size="18pt" id="contractorFromDate"   name="fromDate"/>
									To   <input  type="text" size="18pt" id="contractorThruDate"   name="thruDate"/>
								 </span> -->
							</td>
						    <td align='left' width="35%"><span class="h3">Contractor </span>
					            <select name="partyId" id="partyId">
						     <option value="">All</option>  
                             <#list ptcParties as ptcParty>
                             <#assign partyName = ptcParty>
                             <#assign party = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(ptcParty) />
                             <#if party?has_content>
                             	<#assign partyName= partyName+"["+party+"]" >
                             </#if>
						     <option value='${ptcParty?if_exists}' > ${partyName?if_exists}   </option>
						     </#list>
						     </select>
						     </td>
						    <td width="7%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
						</tr>
					</table>
				</form>
			</tr>
			<tr class="alternate-row"> 
				<form id="PTCVehicleReport" name="PTCVehicleReport" mothed="post" action="<@ofbizUrl>ptcVehicleWiseReport.pdf</@ofbizUrl>" target="_blank">
					<table class="basic-table" cellspacing="5">
						<tr class="alternate-row">
							<td width="23%"><span class='h3'>PTC vehicle Wise Report</span></td>
								<td width="35%">
								 <span class='h3'>Time Period </span>
			                    <select name="customTimePeriodId" class='h4'>
			                    <#if timePeriodList?has_content>	
			                        <#list timePeriodList as timePeriod>    
			                			  <#if ((timePeriod.fromDate)?has_content) && ((timePeriod.thruDate)?has_content)>
			                			   <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
			                       			<#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
					                  	    <option value='${timePeriod.customTimePeriodId}' >
					                    		${fromDate}-${thruDate}
					                  		 </option>
					                  	 </#if>
			                		</#list>    
			                	</#if>	    
			                    </select>
							</td>
					<#-->	<td width="25%">
							     <span class='h3'>
									From <input  type="text" size="18pt" id="vehicleFromDate"   name="fromDate"/>
									To   <input  type="text" size="18pt" id="vehicleThruDate"   name="thruDate"/>
								 </span>
							</td>-->
	                        <td align='left' width="35%"><span class="h3">Vehicle No </span>
					            <select name="vehicleId" id="vehicleId">
						     <option value="all">All</option>  
                             <#list vehicleRoleList as vehicles>
						     <option value='${vehicles.vehicleId?if_exists}' >${vehicles.vehicleId?if_exists}</option>
						     </#list>
						     </select>
						     </td>
						     
						    <td width="7%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
						</tr>
					</table>
				</form>
			</tr>
			<tr class="alternate-row"> 
				<form id="PTCBankReport" name="PTCBankReport" mothed="post" action="<@ofbizUrl>ptcBankReport.pdf</@ofbizUrl>" target="_blank">
					<table class="basic-table" cellspacing="5">
						<tr class="alternate-row">
							<td width="23%"><span class='h3'> PTC Bank Report</span></td>
							<td width="35%">
								 <span class='h3'>Time Period </span>
			                    <select name="customTimePeriodId" class='h4'>
			                    <#if timePeriodList?has_content>	
			                        <#list timePeriodList as timePeriod>    
			                			  <#if ((timePeriod.fromDate)?has_content) && ((timePeriod.thruDate)?has_content)>
			                			   <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
			                       			<#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
					                  	    <option value='${timePeriod.customTimePeriodId}' >
					                    		${fromDate}-${thruDate}
					                  		 </option>
					                  	 </#if>
			                		</#list>    
			                	</#if>	    
			                    </select>
							</td>
							<td width="35%">
								 <span class='h3'>Bank </span>
			                    <select name="finAccountId" id="finAccountId">
			                    <#if finAcctIdList?has_content>	
			                        <#list finAcctIdList as finAcctIds>    
					                  	    <option value='${finAcctIds.finAccountId}' >
					                    		${finAcctIds.finAccountName}
					                  		 </option>
			                		</#list>    
			                	</#if>	    
			                    </select>
							</td>
					        <td width="7%"><input type="submit" value="PDF" onClick="javascript:appendParams('PTCBankReport', '<@ofbizUrl>ptcBankReport.pdf</@ofbizUrl>');" class="buttontext"/>
							   				 <input type="submit" value="CSV" onClick="javascript:appendParams('PTCBankReport', '<@ofbizUrl>ptcBankReport.csv</@ofbizUrl>');" class="buttontext"/></td>         			                                                       </form>
						</tr>
					</table>
				</form>
			</tr>
			<tr class="alternate-row"> 
			<form id="UnionPurchaseBillingReportMR" name="UnionPurchaseBillingReportMR" mothed="post" action="<@ofbizUrl>UnionPurchaseBillingReportMR.pdf</@ofbizUrl>" target="_blank">
				<table class="basic-table" cellspacing="5">
					<tr class="alternate-row">
						<td width="23%"><span class='h3'>Union Purchase Billing Report</span></td>
						<#-- <td width="35%">
							 <span class='h3'>Time Period </span>
		                    <select name="customTimePeriodId" class='h4'>
		                    <#if purchaseTimePeriodList?has_content>	
		                        <#list purchaseTimePeriodList as purTimePeriod>    
		                			  <#if ((purTimePeriod.fromDate)?has_content) && ((purTimePeriod.thruDate)?has_content)>
		                			   <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(purTimePeriod.fromDate, "MMMdd")/>
		                       			<#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(purTimePeriod.thruDate, "MMMdd yyyy")/>
				                  	    <option value='${purTimePeriod.customTimePeriodId}' >
				                    		${fromDate}-${thruDate}
				                  		 </option>
				                  	 </#if>
		                		</#list>    
		                	</#if>	    
		                    </select>
						</td> -->
							<td width="35%">
							     <span class='h3'>
									From <input  type="text" size="18pt" id="purchaseBillingFromDate"   name="fromDate"/>
									To   <input  type="text" size="18pt" id="purchaseBillingThruDate"   name="thruDate"/>
								 </span>
							</td>
						<td width="35%">
							 <span class='h3'>Union </span>
		                    <select name="partyId" id="partyId">
		                    <#if unionPartyList?has_content>	
		                        <#list unionPartyList as unionParty>    
				                  	    <option value='${unionParty.partyId}' >
				                    		${unionParty.partyId}
				                  		 </option>
		                		</#list>    
		                	</#if>	    
		                    </select>
		                  <span class='h3'>Product </span>
		                    <select name="productId" id="productId">
		                    <#if productIdList?has_content>	
		                        <#list productIdList as productIds>    
				                  	    <option value='${productIds.productId}' >
				                    		${productIds.productName}
				                  		 </option>
		                		</#list>    
		                	</#if>	    
		                    </select>
						</td>
				    <td width="7%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
					</tr>
				</table>
			</form>
		</tr>
		
		 <tr class="alternate-row"> 
			<form id="PurchaseBillingReportMR" name="PurchaseBillingReportMR" mothed="post" action="<@ofbizUrl>PurchaseBillingReportMR.pdf</@ofbizUrl>" target="_blank">
				<table class="basic-table" cellspacing="5">
					<tr class="alternate-row">
						<td width="23%"><span class='h3'> Chilling Center Wise Purchase BillingReport </span></td>
						<td width="35%">
							 <span class='h3'>Time Period </span>
		                    <select name="customTimePeriodId" class='h4'>
		                    <#if purchaseTimePeriodList?has_content>	
		                        <#list purchaseTimePeriodList as purTimePeriod>    
		                			  <#if ((purTimePeriod.fromDate)?has_content) && ((purTimePeriod.thruDate)?has_content)>
		                			   <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(purTimePeriod.fromDate, "MMMdd")/>
		                       			<#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(purTimePeriod.thruDate, "MMMdd yyyy")/>
				                  	    <option value='${purTimePeriod.customTimePeriodId}' >
				                    		${fromDate}-${thruDate}
				                  		 </option>
				                  	 </#if>
		                		</#list>    
		                	</#if>	    
		                    </select>
						</td>
						<td width="35%">
							 <span class='h3'>Union/ Chilling Center </span>
		                    <select name="partyId" id="partyId">
		                    <#if unionPartyList?has_content>	
		                        <#list unionPartyList as unionParty>    
				                  	    <option value='${unionParty.partyId}' >
				                    		${unionParty.partyId}
				                  		 </option>
		                		</#list>    
		                	</#if>	    
		                    </select>
						</td>
				    <td width="7%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
					</tr>
				</table>
			</form>
		</tr>
		
		<tr class="alternate-row"> 
			<form id="SaleBillingReportMR" name="SaleBillingReportMR" mothed="post" action="<@ofbizUrl>SaleBillingReportMR.pdf</@ofbizUrl>" target="_blank">
				<table class="basic-table" cellspacing="5">
					<tr class="alternate-row">
						<td width="23%"><span class='h3'> Sale Billing Report</span></td>
						<td width="35%">
							 <span class='h3'>Time Period </span>
		                    <select name="customTimePeriodId" class='h4'>
		                    <#if saleTimePeriodList?has_content>	
		                        <#list saleTimePeriodList as saleTimePeriod>    
		                			  <#if ((saleTimePeriod.fromDate)?has_content) && ((saleTimePeriod.thruDate)?has_content)>
		                			   <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(saleTimePeriod.fromDate, "MMMdd")/>
		                       			<#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(saleTimePeriod.thruDate, "MMMdd yyyy")/>
				                  	    <option value='${saleTimePeriod.customTimePeriodId}' >
				                    		${fromDate}-${thruDate}
				                  		 </option>
				                  	 </#if>
		                		</#list>    
		                	</#if>	    
		                    </select>
						</td>
						<td width="35%">
							 <span class='h3'>Union/ Chilling Center </span>
		                    <select name="partyId" id="partyId">
		                    <#if unionsList?has_content>	
		                        <#list unionsList as unionIds>    
				                  	    <option value='${unionIds.partyId}' >
				                    		${unionIds.partyId}
				                  		 </option>
		                		</#list>    
		                	</#if>	    
		                    </select>
						</td>
				    <td width="7%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
					</tr>
				</table>
			</form>
		</tr>
		<tr class="alternate-row"> 
				<form id="PTCUnionsReport" name="PTCUnionsReport" mothed="post" action="<@ofbizUrl>ptcUnionsReport.pdf</@ofbizUrl>" target="_blank">
					<table class="basic-table" cellspacing="5">
						<tr class="alternate-row">
							<td width="23%"><span class='h3'>Union/Chilling Center Wise Report</span></td>
							<td width="35%">
							     <span class='h3'>
									From <input  type="text" size="18pt" id="unionsFromDate"   name="fromDate"/>
									To   <input  type="text" size="18pt" id="unionsThruDate"   name="thruDate"/>
								 </span>
							</td> 
						    <td width="35%">
							 <span class='h3'>PurposeType Id </span>
		                   	 <select name="purposeTypeId" id="purposeTypeId">
 						     <option value='All'>All</option>  
		                   	 <#if milkPurposeList?has_content>	
		                        <#list milkPurposeList as milkPurposes>    
				                  	    <option value='${milkPurposes.enumId}' >
				                    		${milkPurposes.enumCode}
				                  		 </option>
		                		</#list>    
		                		</#if>	    
		                   	 </select>
							</td>
						    <td width="7%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
						</tr>
					</table>
				</form>
			</tr>
       </table>
    </div>
  </div>
</div> 		