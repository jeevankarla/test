
<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />
<script type="text/javascript">
	function appendParams(formName, action) {
	var formId = "#" + formName;
	jQuery(formId).attr("action", action);	
	jQuery(formId).submit();
    }
     var facilityProductObj=${StringUtil.wrapString(facilityProductObj)!'[]'};
    
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
		makeDatePicker("smpRegDate","");
		makeDatePicker("temperatureDate","");
		makeDatePicker("mateBalanceFromDate","mateBalanceThruDate");
		makeDatePicker("deptProductIssueFromDate","deptProductIssueThruDate");
		makeDatePicker("deptMilkIssueFromDate","deptMilkIssueThruDate");
		makeDatePicker("deptProductReturnFromDate","deptProductReturnThruDate");
		
		$('#ui-datepicker-div').css('clip', 'auto');		
	});
	function getDeptProducts(facility){
       	var facilityId=facility.value;
       	var optionList = '';
			optionList += "<option value = " + " " + " >" +"All "+ "</option>";
			var list= facilityProductObj[facilityId];
			if (list) {		       				        	
	        	for(var i=0 ; i<list.length ; i++){
					var innerList=list[i];	     
	                optionList += "<option value = " + innerList['productId'] + " >" +innerList['description']+" </option>";          			
	      		}//end of main list for loop
	      	}
	      	jQuery("[name='"+"productId"+"']").html(optionList);
       }		
	
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
			                   <select name="dept" id="dept">
   	        				 <option value=""></option>
                             <#list partyGroupSilo as partyGroupSiloDept>
						     <option value='${partyGroupSiloDept.partyId?if_exists}' >${partyGroupSiloDept.groupName?if_exists}</option>
						     </#list>
						     </select>
						    </td>
						    <td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
						</tr>
					</table>
				</form>
			</tr>
				<tr class="alternate-row"> 
				<form id="smpRegisterReport" name="smpRegisterReport" mothed="post" action="<@ofbizUrl>smpRegisterReport.pdf</@ofbizUrl>" target="_blank">
					<table class="basic-table" cellspacing="5">
						<tr class="alternate-row">
							<td width="20%"><span class='h3'>SMP Register Report</span></td>
							
							<td width="35%">
							     <span class='h3'>
									Date <input  type="text" size="18pt" id="smpRegDate"   name="fromDate"/>
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
						    <td width="35%"><span class="h3"> Department</span>
					           <select name="deptId" id="deptId">
                             <#list partyGroup as facilityDepartment>
						     <option value='${facilityDepartment.partyId?if_exists}' >${facilityDepartment.groupName?if_exists}</option>
						     </#list>
						     </select></td>
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
					     <td width="35%"><span class="h3"> Department</span>
					           <select name="facilityId" id="facilityId">
                             <#list partyGroup as facilityDepartment>
						     <option value='${facilityDepartment.partyId?if_exists}' >${facilityDepartment.groupName?if_exists}</option>
						     </#list>
						     </select>
						  </td>
						 <td width="10%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
						 </tr>
					 </table>
				 </form>
			 </tr>
			 <tr class="alternate-row"> 
				<form id="deptProductWiseIssueReport" name="deptProductWiseIssueReport" mothed="post" action="<@ofbizUrl>deptProductWiseIssueReport.pdf</@ofbizUrl>" target="_blank">
					<table class="basic-table" cellspacing="5">
						<tr class="alternate-row">
							<td width="18%"><span class='h3'>Dept Product Wise Issue Register</span></td>
							<td width="30%">
							     <span class='h3'>
									From <input  type="text" size="18pt" id="deptProductIssueFromDate"   name="fromDate"/>
									To   <input  type="text" size="18pt" id="deptProductIssueThruDate"   name="thruDate"/>
								 </span>
							</td> 
							<td align='left' width="37%"><span class="h3">From Dept</span>
					     <#--      <select name="fromDeptId" id="fromDeptId">
                             <#list facilityDepartments as facilityDepartment>
						     <option value='${facilityDepartment.ownerPartyId?if_exists}' >${facilityDepartment.facilityName?if_exists}</option>
						     </#list>
						     </select>	-->
						     <select name='fromDeptId' onchange="javascript:getDeptProducts(this);" required>
	       						     <option value='' ></option>
				      			 	 <#list partyGroup as facilityDepartment>
				      			 			<option value='${facilityDepartment.get("partyId")}'>${facilityDepartment.get("groupName")}</option>
				      			 		</#list> 
				      			 	</select>
						     
					     <span class="h3">Product</span>
					          <select name='productId'  >
				      			 	</select>
						     
						  <span class="h3">To</span>
					           <select name="thruDeptId" id="thruDeptId">
  						     <option value='' >All</option>
                             <#list partyGroup as facilityDepartment>
						     <option value='${facilityDepartment.partyId?if_exists}' >${facilityDepartment.groupName?if_exists}</option>
						     </#list>						    
						      </select>
						     	</td>
						    <td width="5%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
						</tr>
					</table>
				</form>
			</tr>
		<#-- <tr class="alternate-row"> 
				<form id="deptWiseMilkIssueReport" name="deptWiseMilkIssueReport" mothed="post" action="<@ofbizUrl>deptWiseMilkIssueReport.pdf</@ofbizUrl>" target="_blank">
					<table class="basic-table" cellspacing="5">
						<tr class="alternate-row">
							<td width="23%"><span class='h3'>Department Wise Milk Issue Register</span></td>
							<td width="35%">
							     <span class='h3'>
									From <input  type="text" size="18pt" id="deptMilkIssueFromDate"   name="fromDate"/>
									To   <input  type="text" size="18pt" id="deptMilkIssueThruDate"   name="thruDate"/>
								 </span>
							</td> 
							<td align='left' width="35%"><span class="h3">From Dept</span>
					           <select name="fromDeptId" id="fromDeptId">
                             <#list partyGroup as facilityDepartment>
						     <option value='${facilityDepartment.partyId?if_exists}' >${facilityDepartment.groupName?if_exists}</option>
						     </#list>
						     </select>	
						   <span class="h3">To</span>
					           <select name="thruDeptId" id="thruDeptId">
  						     <option value='All' >All</option>
                             <#list partyGroup as facilityDepartment>
						     <option value='${facilityDepartment.partyId?if_exists}' >${facilityDepartment.groupName?if_exists}</option>
						     </#list>						    
						      </select>
						     	</td>
						    <td width="7%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
						</tr>
					</table>
				</form>
			</tr> -->
			<tr class="alternate-row"> 
				<form id="deptProductWiseReturnReceiptReport" name="deptProductWiseReturnReceiptReport" mothed="post" action="<@ofbizUrl>deptProductWiseReturnReceiptReport.pdf</@ofbizUrl>" target="_blank">
					<table class="basic-table" cellspacing="5">
						<tr class="alternate-row">
							<td width="18%"><span class='h3'>Dept Product Wise Receipts/Returns Register</span></td>
							<td width="30%">
							     <span class='h3'>
									From <input  type="text" size="18pt" id="deptProductReturnFromDate"   name="fromDate"/>
									To   <input  type="text" size="18pt" id="deptProductReturnThruDate"   name="thruDate"/>
								 </span>
							</td> 
							<td align='left' width="37%"><span class="h3">Recd Dept</span>
					     <#--      <select name="fromDeptId" id="fromDeptId">
                             <#list facilityDepartments as facilityDepartment>
						     <option value='${facilityDepartment.ownerPartyId?if_exists}' >${facilityDepartment.facilityName?if_exists}</option>
						     </#list>
						     </select>	-->
						     <select name='fromDeptId' onchange="javascript:getDeptProducts(this);" required>
	       						     <option value='' ></option>
				      			 	 <#list partyGroup as facilityDepartment>
				      			 			<option value='${facilityDepartment.get("partyId")}'>${facilityDepartment.get("groupName")}</option>
				      			 		</#list> 
				      			 	</select>
						     
					     <span class="h3">Product</span>
					          <select name='productId'  >
				      			 	</select>
						     
						  <span class="h3">From Dept</span>
					           <select name="thruDeptId" id="thruDeptId">
  						     <option value='' >All</option>
                             <#list partyGroup as facilityDepartment>
						     <option value='${facilityDepartment.partyId?if_exists}' >${facilityDepartment.groupName?if_exists}</option>
						     </#list>						    
						      </select>
						     	</td>
						    <td width="5%"><span class='h3'><input type="submit" value="Download" class="buttontext"></span></td>
						</tr>
					</table>
				</form>
			</tr>
	  </table>
    </div>
  </div>
</div> 
