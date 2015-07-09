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
							<td width="20%"><span class='h3'>PTC Vehicle Contractor Wise Report</span></td>
							
							<td width="25%">
							
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
						    <td align='left' width="30%"><span class="h3">Contractor </span>
					            <select name="partyId" id="partyId">
						     <option value="">All</option>  
                             <#list vehicleRoleList as vehicles>
						     <option value='${vehicles.partyId?if_exists}' >${vehicles.partyId?if_exists}</option>
						     </#list>
						     </select>
						     </td>
						    <td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
						</tr>
					</table>
				</form>
			</tr>
			<tr class="alternate-row"> 
				<form id="PTCVehicleReport" name="PTCVehicleReport" mothed="post" action="<@ofbizUrl>ptcVehicleWiseReport.pdf</@ofbizUrl>" target="_blank">
					<table class="basic-table" cellspacing="5">
						<tr class="alternate-row">
							<td width="20%"><span class='h3'>PTC vehicle Wise Report</span></td>
							
							<td width="25%">
							     <span class='h3'>
									From <input  type="text" size="18pt" id="vehicleFromDate"   name="fromDate"/>
									To   <input  type="text" size="18pt" id="vehicleThruDate"   name="thruDate"/>
								 </span>
							</td>
	                        <td align='left' width="30%"><span class="h3">Vehicle No </span>
					            <select name="vehicleId" id="vehicleId">
						     <option value="all">All</option>  
                             <#list vehicleRoleList as vehicles>
						     <option value='${vehicles.vehicleId?if_exists}' >${vehicles.vehicleId?if_exists}</option>
						     </#list>
						     </select>
						     </td>
						     
						    <td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
						</tr>
					</table>
				</form>
			</tr>
				<tr class="alternate-row"> 
				<form id="PTCUnionsReport" name="PTCUnionsReport" mothed="post" action="<@ofbizUrl>ptcUnionsReport.pdf</@ofbizUrl>" target="_blank">
					<table class="basic-table" cellspacing="5">
						<tr class="alternate-row">
							<td width="20%"><span class='h3'>Unions PTC Report</span></td>
							
							<td width="25%">
							     <span class='h3'>
									From <input  type="text" size="18pt" id="unionsFromDate"   name="fromDate"/>
									To   <input  type="text" size="18pt" id="unionsThruDate"   name="thruDate"/>
								 </span>
							</td>
							<td align='left' width="30%"><span class="h3">PurposeType Id</span>
					            <select name="purposeTypeId" id="purposeTypeId">
						     <option value='All'>All</option>  
						     <option value='INTERNAL' >IDR</option>
 						     <option value='CONVERSION' >CONVERSION</option>
						     </select>
						    </td>
						    <td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
						</tr>
					</table>
				</form>
			</tr>
			
       </table>
    </div>
  </div>
</div> 		