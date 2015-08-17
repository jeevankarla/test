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
		makeDatePicker("convFromDate","convThruDate");
		makeDatePicker("milkProcessRegDate","");
		makeDatePicker("temperatureDate","");
		makeDatePicker("mateBalanceFromDate","mateBalanceThruDate");
		$('#ui-datepicker-div').css('clip', 'auto');		
	});
	
</script>	
<div>
  <div class="screenlet">
	<div class="screenlet-title-bar">
		<h3>Production Reports</h3>
	</div>
	<div class="screenlet-body">
		<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
			<tr class="alternate-row"> 
				<form id="MilkConversionReport" name="MilkConversionReport" mothed="post" action="<@ofbizUrl>MilkConversionReport.pdf</@ofbizUrl>" target="_blank">
					<table class="basic-table" cellspacing="5">
						<tr class="alternate-row">
							<td width="20%"><span class='h3'>Milk Conversion Report</span></td>
							
							<td width="35%">
							     <span class='h3'>
									From <input  type="text" size="18pt" id="convFromDate"   name="convFromDate"/>
									To   <input  type="text" size="18pt" id="convThruDate"   name="convThruDate"/>
								 </span>
							</td>
						    <td width="35%"><span class='h3'></span></td>
						    <td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
						</tr>
					</table>
				</form>
			</tr>
			
			<tr class="alternate-row"> 
				<form id="StockProcessingRegisterReport" name="StockProcessingRegisterReport" mothed="post" action="<@ofbizUrl>StockProcessingRegisterReport.pdf</@ofbizUrl>" target="_blank">
					<table class="basic-table" cellspacing="5">
						<tr class="alternate-row">
							<td width="20%"><span class='h3'>Stock Processing Register Report</span></td>
							
							<td width="35%">
							     <span class='h3'>
									Date <input  type="text" size="18pt" id="milkProcessRegDate"   name="fromDate"/>
								 </span>
							</td>
						    <td width="35%">
						    	 <span class='h3'>Shift </span>
			                    <select name="shiftId" id="shiftId">
	        				 <option value=""></option>
			                    <#if allShiftsList?has_content>	
			                        <#list allShiftsList as shiftDetails>    
					                  	    <option value='${shiftDetails.shiftTypeId}' >
					                    		${shiftDetails.description}
					                  		 </option>
			                		</#list>    
			                	 </#if>	    
			                    </select>
						    </td>
						    <td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
						</tr>
					</table>
				</form>
			</tr>
			
			<tr class="alternate-row"> 
				<form id="MaterialBalanceReport" name="MaterialBalanceReport" mothed="post" action="<@ofbizUrl>MaterialBalanceReport.pdf</@ofbizUrl>" target="_blank">
					<table class="basic-table" cellspacing="5">
						<tr class="alternate-row">
							<td width="20%"><span class='h3'>Material Balance Report</span></td>
							<td width="35%">
							     <span class='h3'>
									From <input  type="text" size="18pt" id="mateBalanceFromDate"   name="mateBalanceFromDate"/>
									To   <input  type="text" size="18pt" id="mateBalanceThruDate"   name="mateBalanceThruDate"/>
								 </span>
							</td>
						    <td width="35%"><span class='h3'></span></td>
						    <td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
						</tr>
					</table>
				</form>
			</tr>
			<tr class="alternate-row">
				 <form id="TemperatureRecord" name="TemperatureRecord" mothed="post" action="<@ofbizUrl>TemperatureRecordReport.pdf</@ofbizUrl>" target="_blank">
					 <table class="basic-table" cellspacing="5">
						 <tr class="alternate-row">
						 <td width="20%"><span class='h3'>Temperature Record </span></td>
						 <td width="35%">
						 <span class='h3'>
						 Date <input type="text" size="18pt" id="temperatureDate" name="temperatureDate"/>
						 </span>
						 </td>
						 <td width="35%">
								 <span class='h3'>Floor </span>
			                    <select name="facilityId" id="facilityId">
			                    <#if floorList?has_content>	
			                        <#list floorList as facilityId>    
					                  	    <option value='${facilityId}' >
					                    		${facilityId}
					                  		 </option>
			                		</#list>    
			                	</#if>	    
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