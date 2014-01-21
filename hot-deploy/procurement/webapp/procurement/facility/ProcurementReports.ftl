<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	

<script type="text/javascript">
function makeDatePicker(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
			}
		});
			}

	$(document).ready(function(){
		makeDatePicker("centerFromDate","thruDate");
		makeDatePicker("centerThruDate","thruDate");
        makeDatePicker("unitCenterFromDate","thruDate"); 
        makeDatePicker("unitCenterThruDate","thruDate");          
        makeDatePicker("procurementDate","thruDate");
        makeDatePicker("selectFromDate","thruDate");
        makeDatePicker("selectThruDate","thruDate");
		$('#ui-datepicker-div').css('clip', 'auto');		
	});
	
</script>
<div class="lefthalf">
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Procurement Reports</h3>
    </div>
    <div class="screenlet-body">
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;" >      	          	     	   
      	<tr class="alternate-row">
      		<form id="DayWiseAnalysisReport" name="DayWiseAnalysisReport" method="post" action="<@ofbizUrl>dayWiseAnalysisReport.pdf</@ofbizUrl>">
      			<td> Day Wise PMR Report</td>
      			<td >
      				Unit
      				<select name="unitId">
      					<option value=""/>
                		<#list unitsList as units>    
                  	    	<option value='${units}' >
	                    		${units}
	                  		</option>
                		</#list>             
					</select>      				
      				Date<input  type="text" size="15pt" id="procurementDate" name="procurementDate"/>
      				<input type="submit" formtarget="_blank" value="Download" class="buttontext"/>
          		</td>
          	</form>
      	</tr> 
      	<tr> 
      		<form id="feedRecoveriesList" name="feedRecoveriesList" method="post" action="<@ofbizUrl>feedRecoveriesList.txt</@ofbizUrl>">	
      			<td>Feed & Other recoveries</td>
      			<td>Unit<select name="unitId" class='h4'>
                		<#list unitsList as units>    
                  	    	<option value='${units}' >
	                    		${units}
	                  		</option>
                		</#list>             
					</select>				
      				Period<select name="customTimePeriodId" class='h4'>
                		<#list timePeriodList as timePeriod>    
                			 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
                			 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${fromDate}-${thruDate}
	                  		</option>
                		</#list>           
					</select>
      				<input type="submit" formtarget="_blank" value="Download" class="buttontext"/>
      			</td>
      		</form>
      	</tr>
      	<tr class="alternate-row">
      		<form id="billsAbstract" name="BillsAbstract" method="post" action="<@ofbizUrl>billsAbstract.txt</@ofbizUrl>">	
      			<td>Unit Milk Bills Abst</td>
      			<td>Unit<select name="unitId">
                		<#list unitsList as units>    
                  	    	<option value='${units}' >
	                    		${units}
	                  		</option>
                		</#list>             
					</select>Period<select name="customTimePeriodId" class='h4'>
                		<#list timePeriodList as timePeriod>    
                			 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
                			 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${fromDate}-${thruDate}
	                  		</option>
                		</#list>            
					</select><input type="submit" formtarget="_blank" value="Download" class="buttontext"/>
      			</td>
      		</form>
      	</tr>
      	<tr>
      		<form id="routeWiseBillsAbstract" name="routeWiseBillsAbstract" method="post" action="<@ofbizUrl>routeWiseBillsAbstract.txt</@ofbizUrl>">	
      			<td>Route-Wise Milk Bills Abst</td>
      			<td>Unit<select name="unitId">
                		<#list unitsList as units>    
                  	    	<option value='${units}' >
	                    		${units}
	                  		</option>
                		</#list>             
					</select>
					Period<select name="customTimePeriodId" class='h4'>
                		<#list timePeriodList as timePeriod>    
                			 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
                			 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${fromDate}-${thruDate}
	                  		</option>
                		</#list>           
					</select>
      				<input type="submit" formtarget="_blank" value="Download" class="buttontext"/>
      			</td>
      		</form>
      	</tr>
		<tr  class="alternate-row"> 
			<form name="UnitWiseCenterWiseBankStmt" action="<@ofbizUrl>UnitWiseCenterWiseBankStmt.pdf</@ofbizUrl>">
				<td>Unit,Center Wise Bank Stmt</td>
				<td>Unit<select name="unitId">
      					<option value=""/>
                		<#list unitsList as units>    
                  	    	<option value='${units}' >
	                    		${units}
	                  		</option>
                		</#list>             
					</select>  
					Period<select name="customTimePeriodId" class='h4'>
                		<#list timePeriodList as timePeriod>    
                			 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
                			 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${fromDate}-${thruDate}
	                  		</option>
                		</#list>            
					</select>    			          	
					<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
				</td>	
			</form>		
		</tr> 
		<tr> 
			<form name="UnitWiseMilkBillSummary" action="<@ofbizUrl>unitWiseMilkBillSummary.pdf</@ofbizUrl>">
				<td>Unit Wise Milk Bill Summary</td>
				<td>Unit<select name="unitId">
      					<option value=""/>
                		<#list unitsList as units>    
                  	    	<option value='${units}' >
	                    		${units}
	                  		</option>
                		</#list>             
					</select>  
					Period<select name="customTimePeriodId" class='h4'>
                		<#list timePeriodList as timePeriod>    
                			 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
                			 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${fromDate}-${thruDate}
	                  		</option>
                		</#list>            
					</select>    			          	
					<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
				</td>	
			</form>		
		</tr>
		<tr  class="alternate-row">     
            <form name="UnitWisePeriodAnalys" action="<@ofbizUrl>UnitWiseDayWiseKgFatAccount.txt</@ofbizUrl>">
                    <td>Unit,Day wise kg-fat A/C</td>
                    <td>Unit<select name="unitId">
                        <option value=""/>
                        <#list unitsList as units>    
                            <option value='${units}' >
                                ${units}
                            </option>
                        </#list>             
                   </select>  
                    Period<select name="customTimePeriodId" class='h4'>
                        <#list timePeriodList as timePeriod>    
                			 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
                			 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${fromDate}-${thruDate}
	                  		</option>
                		</#list>           
                    </select>                           
                        <input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
                    </td>
            </form>
        </tr>        
        <tr> 
      		<form id="unitWisePtcRecoveryAbstract" name="unitWisePtcRecoveryAbstract" method="post" action="<@ofbizUrl>unitWisePtcRecoveryAbstract.pdf</@ofbizUrl>">	
      			<td>Unit-Wise PTC Recovery Abst</td>
      			<td>Unit<select name="unitId" class='h4'>
                		<#list unitsList as units>    
                  	    	<option value='${units}' >
	                    		${units}
	                  		</option>
                		</#list>             
					</select>				
      				Period<select name="customTimePeriodId" class='h4'>
                		<#list timePeriodList as timePeriod>    
                			 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
                			 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${fromDate}-${thruDate}
	                  		</option>
                		</#list>            
					</select>
      				<input type="submit" formtarget="_blank" value="Download" class="buttontext"/>
      			</td>
      		</form>
      	</tr>
      	<tr class="alternate-row"> 
      		<form id="bankAbstarct" name="bankAbstarct" method="post" action="<@ofbizUrl>bankAbstarct.txt</@ofbizUrl>">	
      			<td>Bank Abstarct</td>
      			<td>Unit<select name="unitId" class='h4'>
                		<#list unitsList as units>    
                  	    	<option value='${units}' >
	                    		${units}
	                  		</option>
                		</#list>             
					</select>				
      				Period<select name="customTimePeriodId" class='h4'>
                		<#list timePeriodList as timePeriod>    
                			 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
                			 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${fromDate}-${thruDate}
	                  		</option>
                		</#list>            
					</select>
      				<input type="submit" formtarget="_blank" value="Download" class="buttontext"/>
      			</td>
      		</form>
      	</tr>    	
      	<tr>     
            <form id="UnitWiseInputOutputAbstract" name="UnitWiseInputOutputAbstract" action="<@ofbizUrl>UnitWiseInputOutputAbstract.txt</@ofbizUrl>">
                    <td>Unit-Wise I/O Abs</td>
                    <td>Unit<select name="unitId">
                        <option value=""/>
                        <#list unitsList as units>    
                            <option value='${units}' >
                                ${units}
                            </option>
                        </#list>             
                    </select> 
                    Period<select name="customTimePeriodId" class='h4'>
                       <#list timePeriodList as timePeriod>    
                			 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
                			 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${fromDate}-${thruDate}
	                  		</option>
                		</#list>
                    </select>   
                     <input type="hidden" name="shedId" value=""/>
                     <input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext" />
                    </td>
            </form>
        </tr>      	                 
	</table>
</div>
</div>
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3> Foxpro Export</h3>
    </div>
    <div class="screenlet-body">
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;" >      	          	     	   
      	<tr class="alternate-row">
      		<form id="ProcurementEntriesCsv" name="ProcurementEntriesCsv" method="post" action="<@ofbizUrl>MPROC.DBF</@ofbizUrl>">
      			<td> Procurement Entries </td>
      			<td >
					<input type="hidden" name="shedId" value=""/>
					Period
      				<select name="customTimePeriodId" class='h4'>
                		<#list timePeriodList as timePeriod>    
                			 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
                			 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${fromDate}-${thruDate}
	                  		</option>
                		</#list>            
					</select>    				
      				<input type="submit" formtarget="_blank" value="Download Dbf" class="buttontext"/>
          		</td>
          	</form>
      	</tr> 
      	<tr class="alternate-row">
      		<form id="MpAdditions" name="MpAdditions" method="post" action="<@ofbizUrl>MPADDN.DBF</@ofbizUrl>">
      			<td>Mp Additions</td>
      			<td >
      			   <input type="hidden" name="shedId" value=""/>
					<input type="hidden" name="adjustmentType" value="additions"/> 
					Period
      				<select name="customTimePeriodId" class='h4'>
                		<#list timePeriodList as timePeriod>    
                			 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
                			 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${fromDate}-${thruDate}
	                  		</option>
                		</#list>            
					</select>    				
      				<input type="submit" formtarget="_blank" value="Download Dbf" class="buttontext"/>
          		</td>
          	</form>
      	</tr> 
      	<tr class="alternate-row">
      		<form id="MpDeductions" name="MpDeductions" method="post" action="<@ofbizUrl>MPDED.DBF</@ofbizUrl>">
      			<td>Mp Deductions</td>
      			<td >
					<input type="hidden" name="shedId" value=""/>
					<input type="hidden" name="adjustmentType" value="deductions"/>
					Period
      				<select name="customTimePeriodId" class='h4'>
                		<#list timePeriodList as timePeriod>    
                			 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
                			 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${fromDate}-${thruDate}
	                  		</option>
                		</#list>            
					</select>    				
      				<input type="submit" formtarget="_blank" value="Download Dbf" class="buttontext"/>
          		</td>
          	</form>
      	</tr> 
      	<tr class="alternate-row">
      		<form id="MpDeductions" name="MpAbstract" method="post" action="<@ofbizUrl>MPABS.DBF</@ofbizUrl>">
      			<td>Mp ABS</td>
      			<td >    			
					<input type="hidden" name="shedId" value=""/>					
					Period
      				<select name="customTimePeriodId" class='h4'>
                		<#list timePeriodList as timePeriod>    
                			 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
                			 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${fromDate}-${thruDate}
	                  		</option>
                		</#list>           
					</select>    				
      				<input type="submit" formtarget="_blank" value="Download Dbf" class="buttontext"/>
          		</td>
          	</form>
      	</tr> 
      	<tr class="alternate-row">
      		<form id="UnitMasterCsv" name="UnitMasterCsv" method="post" action="<@ofbizUrl>UNTMAS.DBF</@ofbizUrl>">
      			<td>UNIT MASTER</td>
      			<td >    			
					<input type="hidden" name="shedId" value=""/>					
					Period
      				<select name="customTimePeriodId" class='h4'>
                		<#list timePeriodList as timePeriod>    
                			 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
                			 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${fromDate}-${thruDate}
	                  		</option>
                		</#list>            
					</select>    				
      				<input type="submit" formtarget="_blank" value="Download Dbf" class="buttontext"/>
          		</td>
          	</form>
      	</tr>      	
      	<tr class="alternate-row">
      		<form id="MPMasterCsv" name="MPMasterCsv" method="post" action="<@ofbizUrl>MPMAS.DBF</@ofbizUrl>">
      			<td>MPMAS</td>
      			<td >    			
					<input type="hidden" name="shedId" value=""/>					
					Period
      				<select name="customTimePeriodId" class='h4'>
                		<#list timePeriodList as timePeriod>    
                			 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
                			 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${fromDate}-${thruDate}
	                  		</option>
                		</#list>           
					</select>    				
      				<input type="submit" formtarget="_blank" value="Download DBF" class="buttontext"/>
          		</td>
          	</form>
      	</tr>
      	<tr class="alternate-row">
      		<form id="RouteMasCsv" name="RouteMasCsv" method="post" action="<@ofbizUrl>ROUTEMAS.DBF</@ofbizUrl>">
      			<td>ROUTE MASTER</td>
      			<td >    			
					<input type="hidden" name="shedId" value=""/>					
      				<input type="submit" formtarget="_blank" value="Download Dbf" class="buttontext"/>
          		</td>
          	</form>
      	</tr>
      	<tr class="alternate-row">
      		<form id="BANKMAS" name="BANKMASCsv" method="post" action="<@ofbizUrl>BANKMAS.DBF</@ofbizUrl>">
      			<td>BANKMAS</td>
      			<td >    			
					<input type="hidden" name="shedId" value=""/>					
      				<input type="submit" formtarget="_blank" value="Download Dbf" class="buttontext"/>
          		</td>
          	</form>
      	</tr>
      </table>
     </div> 	
   </div>
</div>

<div class="righthalf">
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Procurement Reports</h3>
    </div>
    <div class="screenlet-body">
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">    	
      	<tr>
      		<form id="procurementCheckList" name="procurementCheckList" method="post" action="<@ofbizUrl>checkListReport.pdf?userLoginId=${userLogin.get("userLoginId")}&&all=Y</@ofbizUrl>">	
      			<td>Proc Check List</td>
      			<td>Unit<select name="unitId">
                		<#list unitsList as units>    
                  	    	<option value='${units}'>
	                    		${units}
	                  		</option>
                		</#list>             
					</select>
					Time<select name="purchaseTime">
                		<option value='AM' >Morning</option>
                		<option value='PM' >Evening</option>          
					</select>
      				fromDate<input  type="text" size="10pt" id="selectFromDate" name="selectFromDate"/>
      				thruDate<input  type="text" size="10pt" id="selectThruDate" name="selectThruDate"/>
      				<select name="checkListType">
                  		<option value='All' >All</option>
                  	    <option value='My' >My</option>
					</select><input type="submit" formtarget="_blank" value="Download" class="buttontext"/>      				
      			</td>
      		</form>
      	</tr> 
      	<tr>     
            <form name="UnitWiseDayWiseTotals" action="<@ofbizUrl>UnitDayWiseTotals.txt</@ofbizUrl>">
                    <td>Unit,Day Wise Totals</td>
                    <td>Unit<select name="unitId">
                        <option value=""/>
                        <#list unitsList as units>    
                            <option value='${units}' >
                                ${units}
                            </option>
                        </#list>             
                   </select>  
                    Period<select name="customTimePeriodId" class='h4'>
                        <#list timePeriodList as timePeriod>    
                			 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
                			 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${fromDate}-${thruDate}
	                  		</option>
                		</#list>            
                    </select>                           
                        <input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
                    </td>
            </form>
        </tr> 	
      	<tr class="alternate-row">
      		<form id="MilkBillReport" name="MilkBillReport" method="post" action="<@ofbizUrl>milkBillReport.pdf</@ofbizUrl>">
      			<td> BM/CM Milk Bill Report</td>
      			<td>Unit<select name="unitId" onchange="javascript:getUnitRoute(this);">
                		<#list unitsList as units>    
                  	    	<option value='${units}' >
	                    		${units}
	                  		</option>
                		</#list>             
					</select>      			
      			Route
      				<select name="routeId">
                		<#list routesList as routes>    
                  	    	<option value='${routes}' >
	                    		${routes}
	                  		</option>
                		</#list>             
					</select>
      			Milk
      			<select name="productName">
                		<#list milkProductsList as product>    
                  	    	<option value='${product.productName}'>
	                    		${product.brandName}
	                  		</option>
                		</#list>             
					</select>				
      		  Period<select name="customTimePeriodId">
                		<#list timePeriodList as timePeriod>    
                			 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
                			 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${fromDate}-${thruDate}
	                  		</option>
                		</#list>            
					</select><input type="submit"  size="5pt" formtarget="_blank" value="Download" class="buttontext"/>					
          		</td>
          	</form>
      	</tr>
      	<tr>       
            <form name="unitWiseCenterWiseMilkCollections" action="<@ofbizUrl>unitWiseCenterWiseMilkCollections.pdf</@ofbizUrl>">
                <td>Unit,Center wise Milk-year</td>
                <td>Unit<select name="unitId">
                        <option value=""/>
                        <#list unitsList as units>    
                            <option value='${units}' >
                                ${units}
                            </option>
                        </#list>             
                    </select> 
                    From<input  type="text" size="8pt" id="unitCenterFromDate" name="fromDate"/>
                    To<input  type="text" size="8pt" id="unitCenterThruDate" name="thruDate"/>                           
                    <input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
                </td>   
            </form>     
        </tr>
      	<tr>
            <form id="CentreWiseKgfatAccount" name="CentreWiseKgfatAccount" method="post" action="<@ofbizUrl>CentreWiseKgfatAccount.txt</@ofbizUrl>">
                <table class="basic-table" cellspacing="0">
                    <tr  class="alternate-row">
                    	<td> <span class='h3'>Centre-wise kgfat-A/C</span></td>
                        <td><span class='h3'>Center:</span></td>
                        <td><@htmlTemplate.lookupField value="${facilityId?if_exists}" formName="CentreWiseKgfatAccount" name="centerId" size="10pt" id="facilityId" fieldFormName="ProcurementCenterLookupFacility"/>   </td>                
                        <td><span class='h3'>From: </span><input  size="10pt" type="text" id="centerFromDate" name="fromDate"/>
                         <span class='h3'>To: </span><input size="10pt" type="text" id="centerThruDate" name="thruDate"/>
                        <input type="submit" formtarget="_blank" value="Download" class="buttontext h1"/> </td>
                    </tr>
                </table>
            </form>
        </tr>
      </table>
    </div>
 </div>
 </div>  			
<div class="righthalf">
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3 name = "dispShedId"/h3>
    </div>
    <div class="screenlet-body">
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">    	
      	<tr class="alternate-row">
      		<form id="ShedWiseAmountAbstract" name="ShedWiseAmountAbstract" method="post" action="<@ofbizUrl>ShedWiseAmountAbstract.pdf</@ofbizUrl>">
      			<td>Shed-Wise Amount Abst</td>
      			<td>Time Period	     			
      				<select name="customTimePeriodId" class='h4'>
                		<#list timePeriodList as timePeriod>    
                			 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
                			 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${fromDate}-${thruDate}
	                  		</option>
                		</#list>            
					</select>
					<input type="hidden" name="shedId" value=""/>
      				<input type="submit" formtarget="_blank" value="Download" class="buttontext" />
          		</td>
          	</form>
      	</tr>
      	<tr class="alternate-row">
      		<form id="BankWiseTotalPayment" name="BankWiseTotalPayment" method="post" action="<@ofbizUrl>BankWiseTotalPayment.txt</@ofbizUrl>">
      			<td>Bank-Wise Total Payment</td>
      			<td>Time Period	     			
      				<select name="customTimePeriodId" class='h4'>
                		<#list timePeriodList as timePeriod>    
                			 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
                			 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${fromDate}-${thruDate}
	                  		</option>
                		</#list>            
					</select>
					<input type="hidden" name="shedId" value=""/>
      				<input type="submit" formtarget="_blank" value="Download" class="buttontext" />
          		</td>
          	</form>
      	</tr> 
      	<tr class="alternate-row">
      		<form id="UnitBankWiseStatement" name="unitBankWiseStatement" method="post" action="<@ofbizUrl>unitBankWiseStatement.pdf</@ofbizUrl>">
      			<td>Unit-Bank-Wise Statement</td>
      			<td>Time Period	     			
      				<select name="customTimePeriodId" class='h4'>
                		<#list timePeriodList as timePeriod>    
                			 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
                			 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${fromDate}-${thruDate}
	                  		</option>
                		</#list>            
					</select>
					<input type="hidden" name="shedId" value=""/>
      				<input type="submit" formtarget="_blank" value="Download" class="buttontext" />
          		</td>
          	</form>
      	</tr> 
      	<tr> 
			<form id="ShedWiseProcurementAbstract" name="ShedWiseProcurementAbstract" method="post" action="<@ofbizUrl>shedWiseProcurementAbstract.pdf</@ofbizUrl>">
				<td>Shed wise Procurement Abst</td>
				<td>
					Time Period
      				<select name="customTimePeriodId" class='h4'>
                		<#list timePeriodList as timePeriod>    
                			 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
                			 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${fromDate}-${thruDate}
	                  		</option>
                		</#list>            
					</select>
					<input type="hidden" name="shedId" value=""/>
      				<input type="submit" formtarget="_blank" value="Download" class="buttontext" />
				</td>	
			</form>		
		</tr>
		<tr class="alternate-row"> 
			<form id="ShedWiseInputOutputAbstract" name="ShedWiseInputOutputAbstract" method="post" action="<@ofbizUrl>shedWiseInputOutputAbst</@ofbizUrl>">
				<td>Shed Wise Input Output Abst</td>
				<td>Time Period
      				<select name="customTimePeriodId" class='h4'>
                		<#list timePeriodList as timePeriod>    
                			 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
                			 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${fromDate}-${thruDate}
	                  		</option>
                		</#list>            
					</select>    
					<input type="hidden" name="shedId" value=""/>
      				<input type="submit" formtarget="_blank" value="Download" class="buttontext" />
				</td>	
			</form>		
		</tr>
		<tr> 
			<form id="UnitMilkBillNetPayable" name="UnitMilkBillNetPayable" method="post" action="<@ofbizUrl>unitMilkBillNetPayable</@ofbizUrl>">
				<td>Unit Milk Bill Net Payable</td>
				<td>Time Period
      				<select name="customTimePeriodId" class='h4'>
                		<#list timePeriodList as timePeriod>    
                			 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
                			 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${fromDate}-${thruDate}
	                  		</option>
                		</#list>            
					</select>    			          	
					<input type="hidden" name="shedId" value=""/>
      				<input type="submit" formtarget="_blank" value="Download" class="buttontext" />
				</td>	
			</form>		
		</tr>
		<tr>
            <form id="ShortagesRecovery" name="ShortagesRecovery" method="post" action="<@ofbizUrl>ShortagesRecovery.pdf</@ofbizUrl>">
                        <td> Shortages Recovery</td>
                        <td> Time Period
                    <select name="customTimePeriodId" class='h4'>
                        <#list timePeriodList as timePeriod>    
                			 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
                			 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${fromDate}-${thruDate}
	                  		</option>
                		</#list>            
                    </select>
                    <input type="hidden" name="shedId" value=""/>
      				<input type="submit" formtarget="_blank" value="Download" class="buttontext" />
                     </td>
            </form>
        </tr>
        <tr>
            <form id="ShedMilkBillDetails" name="ShedMilkBillDetails" method="post" action="<@ofbizUrl>ShedMilkBillDetails.pdf</@ofbizUrl>">
                        <td> ShedMilk Bill Details</td>
                        <td>Time Period
                    <select name="customTimePeriodId" class='h4'>
                        <#list timePeriodList as timePeriod>    
                			 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
                			 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${fromDate}-${thruDate}
	                  		</option>
                		</#list>            
                    </select>
                    <input type="hidden" name="shedId" value=""/>
      				<input type="submit" formtarget="_blank" value="Download" class="buttontext" />
                </td>
            </form>
        </tr>
      </table>
    </div>
 </div>
 </div