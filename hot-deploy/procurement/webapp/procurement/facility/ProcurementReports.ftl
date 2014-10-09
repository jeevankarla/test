<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	

<script type="text/javascript">
function makeDatePicker(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			changeYear: true,
			numberOfMonths: 1,
			maxDate:fromDateId,
			onSelect: function( selectedDate ) {
				$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
			}
		});
			}

	$(document).ready(function(){
		makeDatePicker("avgRateFromDate","thruDate");
		makeDatePicker("avgRateThruDate","thruDate");
		makeDatePicker("centerFromDate","thruDate");
		makeDatePicker("centerThruDate","thruDate");
        makeDatePicker("unitCenterFromDate","thruDate"); 
        makeDatePicker("unitCenterThruDate","thruDate");  
        makeDatePicker("procurementDate","thruDate");
        makeDatePicker("procurementSmsDate","thruDate");
        makeDatePicker("selectFromDate","thruDate");
        makeDatePicker("selectThruDate","thruDate");
        makeDatePicker("AcknowledgementFromDate","thruDate");
        makeDatePicker("AcknowledgementThruDate","thruDate");
        makeDatePicker("AnnualKgFatKgSnfFromDate","thruDate");
        makeDatePicker("AnnualKgFatKgSnfThruDate","thruDate");
        makeDatePicker("shedWiseAnnualAbstractFromDate","thruDate");
        makeDatePicker("shedWiseAnnualAbstractThruDate","thruDate");
        makeDatePicker("ShedWiseMonthWiseMilkPaymentFromDate","thruDate");
        makeDatePicker("ShedWiseMonthWiseMilkPaymentThruDate","thruDate");
        makeDatePicker("CenterWiseBillsAbstractFromDate","thruDate");
        makeDatePicker("CenterWiseBillsAbstractThruDate","thruDate");
        makeDatePicker("UnitWiseKgFatKgSnfTotalSolidsFromDate","thruDate");
        makeDatePicker("UnitWiseKgFatKgSnfTotalSolidsThruDate","thruDate");
        makeDatePicker("UnitWiseMonthWiseMilkPaymentFromDate","thruDate");
        makeDatePicker("UnitWiseMonthWiseMilkPaymentThruDate","thruDate");
        makeDatePicker("consolidatedReportFromDate","thruDate");
        makeDatePicker("consolidatedReportThruDate","thruDate");
        makeDatePicker("UnitMonthKgFatKgSnfTotalSolidsFromDate","thruDate");
        makeDatePicker("UnitMonthKgFatKgSnfTotalSolidsThruDate","thruDate");
        makeDatePicker("FortnightKgfatAccountFromDate","thruDate");
        makeDatePicker("FortnightKgfatAccountThruDate","thruDate");
     	makeDatePicker("FNCenterWiseKgFatKgSnfFromDate","thruDate");
        makeDatePicker("FNCenterWiseKgFatKgSnfThruDate","thruDate");
        makeDatePicker("centerWiseFatSnfDeductionFromDate","thruDate");
        makeDatePicker("centerWiseFatSnfDeductionThruDate","thruDate");
        makeDatePicker("MonthWiseUnitWiseDetailsListFromDate","thruDate");
		makeDatePicker("MonthWiseUnitWiseDetailsListThruDate","thruDate");
        makeDatePicker("FNCollectionCentrewisePaymentFromDate","thruDate");
		makeDatePicker("FNCollectionCentrewisePaymentThruDate","thruDate");
	    makeDatePicker("Unit-wiseProcurementComparingFromDate","thruDate");
		makeDatePicker("Unit-wiseProcurementComparingThruDate","thruDate");
		makeDatePicker("MonthWiseAverageRateStatementFromDate","thruDate");
		makeDatePicker("MonthWiseAverageRateStatementThruDate","thruDate");
		makeDatePicker("CenterWiseBillsAbstractFromDate","thruDate");
		makeDatePicker("CenterWiseBillsAbstractThruDate","thruDate");
		makeDatePicker("LeanFlushSeasonYearWiseFromDate","thruDate");
		makeDatePicker("LeanFlushSeasonYearWiseThruDate","thruDate");
		makeDatePicker("UnitwiseCenterwiseSelectedlistFromDate","thruDate");
		makeDatePicker("UnitwiseCenterwiseSelectedlistThruDate","thruDate");
		makeDatePicker("ProcurementAbstractFromDate","thruDate");
		makeDatePicker("ProcurementAbstractThruDate","thruDate");
		makeDatePicker("SheUserChargesFromDate","thruDate");
		makeDatePicker("SheUserChargesThruDate","thruDate");
		makeDatePicker("ProcurementPaymentFromDate","thruDate");
		makeDatePicker("ProcurementPaymentThruDate","thruDate");
		makeDatePicker("UnitWiseComparingwithLastYearFromDate","thruDate");
		makeDatePicker("UnitWiseComparingwithLastYearThruDate","thruDate");
		makeDatePicker("RegionWiseThreeYearsComparisionFromDate","thruDate");
		makeDatePicker("ShedwiseUnitwiseCapacityUtilizationFromDate","thruDate");
		makeDatePicker("ShedwiseUnitwiseCapacityUtilizationThruDate","thruDate");
        $('#ui-datepicker-div').css('clip', 'auto');		
	});
	function appendParams(formName, action) {
		var formId = "#" + formName;
			jQuery(formId).attr("action", action);
			jQuery(formId).submit();
	}
	
	function appendRecoveryFlag(formName){
		var formId = "#" + formName;
		var action = jQuery(formId).attr("action");
		var shed='';
		if(($('[name=shedId]').val())!=="undefined"){
			shed = new String ($('[name=shedId]').val());
		}
		var index = action.indexOf("recoveryFromDDAccount");
		if(shed =="WGD" || shed =="NZB"){
			var qIndex = action.indexOf("?");
			if(index<0){
				if(qIndex>0){
					action = action+"&";
				}else{
					action = action+"?";
				}
				action = action+"recoveryFromDDAccount=YES";
			}
			
		}else{
			if(index>0){
				action = action.substring(0,index);
			}
		}
		jQuery(formId).attr("action",action);
	}
	
	
</script>
<#if reportFrequencyFlag =="UnitReports">
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Procurement Reports</h3>
    </div>
    <div class="screenlet-body">
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">    	
      	<tr class="alternate-row">
      		<form id="procurementCheckList" name="procurementCheckList" method="post" action="<@ofbizUrl>procCheckListReport.pdf?userLoginId=${userLogin.get("userLoginId")}&&all=Y</@ofbizUrl>">	
      			<td>Proc Check List</td>
      			<td><input type="hidden" name="unitId" value=""/>
      			    <input type="hidden" name="shedId" value=""/>
					Time<select name="purchaseTime">
                		<option value='AM' >Morning</option>
                		<option value='PM' >Evening</option>          
					</select>
      				fromDate<input  type="text" size="10pt" id="selectFromDate" name="selectFromDate"/>
      				thruDate<input type="text" size="10pt" id="selectThruDate" name="selectThruDate"/>
      		        pageStart<input type="text" size="3pt" id="selectPageStart" name="pageStart"/>
      				pageEnd<input type="text" size="3pt" id="selectPageEnd" name="pageEnd"/>
      		        <select name="checkListType">
                  		<option value='All' >All</option>
                  	    <option value='My' >My</option>
					</select><input type="submit" formtarget="_blank" value="Download" class="buttontext"/>      				
      			 </td>
      		</form>
      	 </tr> 
      	<tr class="alternate-row">     
            <form name="UnitWiseDayWiseTotals" action="<@ofbizUrl>UnitDayWiseTotals.txt</@ofbizUrl>">
                    <td>Unit,Day Wise Totals</td>
                    <td><input type="hidden" name="unitId" value=""/> 
                    Time<select name="supplyTypeEnumId">
                		<option value=''></option>
                		<option value='AM'>Morning</option>
                		<option value='PM'>Evening</option>          
					</select>
                    Period<select name="customTimePeriodId" class='h4'>
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
                        <input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
                    </td>
            </form>
        </tr> 	
      	<tr class="alternate-row">
      		<form id="MilkBillReport" name="MilkBillReport" method="post" action="<@ofbizUrl>milkBillReport.pdf</@ofbizUrl>">
      			<td> BM/CM Milk Bill Report</td>
      			<td><input type="hidden" name="unitId" value=""/>      			
      			Route 
      				<select name="routeId">
      				<#if routesList?has_content>
                		<#list routesList as routes>    
                  	    	<option value='${routes}' >
	                    		${routes}
	                  		</option>
                		</#list>  
                	</#if>	           
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
					    pageStart<input type="text" size="3pt" id="selectPageStart" name="pageStart"/> 
      				    pageEnd<input type="text" size="3pt" id="selectPageEnd" name="pageEnd"/>   
					    <input type="submit"  size="5pt" formtarget="_blank" value="Download" class="buttontext"/>					
          		</td>
          	</form>
      	</tr>
      	<tr class="alternate-row">
      		<form id="PrintProcValidationEntries" name="PrintProcValidationEntries" method="post" action="<@ofbizUrl>printProcValidations.pdf</@ofbizUrl>">
      			<td>Print Validation</td>
      			<input type="hidden" name="shedId" value=""/>
      			<td><input type="hidden" name="unitId" value=""/>  							
      		  Period<select name="customTimePeriodId">
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
				Status<select name="statusId">
                		<#list statusItemList as status>               			  
		                  	    <option value='${status.statusId}' >
		                    		${status.description?if_exists}
		                  		 </option>		                  	
                		</#list>             
					</select>	
					<input type="submit"  size="5pt" formtarget="_blank" value="Download" class="buttontext"/>					
          		</td>
          	</form>
      	</tr>
      	<tr class="alternate-row">       
            <form name="unitWiseCenterWiseMilkCollections" action="<@ofbizUrl>unitWiseCenterWiseMilkCollections.pdf</@ofbizUrl>">
                <td>Unit,Center wise Milk-year</td>
                <td><input type="hidden" name="unitId" value=""/> 
                    From<input  type="text" size="8pt" id="unitCenterFromDate" name="fromDate"/>
                    To<input  type="text" size="8pt" id="unitCenterThruDate" name="thruDate"/>                           
                    Ltrs From<input  type="text" size="8pt" id="fromRange" name="fromRange"/>
                   	Ltrs To<input  type="text" size="8pt" id="toRange" name="toRange"/>
                   	InActiveCenters<input type="checkbox" name="centerFlag" value="Y"/>
                    <input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
                </td>   
            </form>     
        </tr>
      		<tr class="alternate-row">
            <form id="CentreWiseKgfatAccount" name="CentreWiseKgfatAccount" method="post" action="<@ofbizUrl>CentreWiseKgfatAccount.txt</@ofbizUrl>">
               	<td> <span class='h3'>Centre-wise kgfat-A/C</span></td>
                    <td>
                      <table>
                         	<tr>
                        		<input type="hidden" name="shedId" value=""/> 
                        		<input type="hidden" name="unitId" value=""/>
                        		<td><span class='h3'>Center: </span><input  size="10pt" type="text" id="facilityCode" name="facilityCode"/></td>
                        		<td><span class='h3'>From: </span><input  size="10pt" type="text" id="centerFromDate" name="fromDate"/></td>
                         		<td><span class='h3'>To: </span><input size="10pt" type="text" id="centerThruDate" name="thruDate"/></td>
                        		<td><input type="submit" formtarget="_blank" value="Download" class="buttontext h1"/> </td>
                   			</tr>
                   	</table>
                  </td>
            </form>
        </tr>
        
      </table>
    </div>
 </div>
 </div>

<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Procurement Reports</h3>
    </div>
    <div class="screenlet-body">
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;" >  
      	<#--<tr><#if security.hasEntityPermission("SENDPROCSMS", "_VIEW", session)>
      		<form id="ProcurementSummarySms" name="ProcurementSummarySms" method="post" action="<@ofbizUrl>ProcurementSummarySms</@ofbizUrl>">
      			<td> Procurement Summary Sms</td>
      			<td>
      				<input type="hidden" name="shedId" value=""/>
      				Date<input  type="text" size="15pt" id="procurementSmsDate" name="procurementSmsDate"/>
					Time<select name="purchaseTime">
                		<option value='AM' >Morning</option>
                		<option value='PM' >Evening</option>          
					</select>      				
      				<input type="submit" value="Send" class="buttontext"/>
          		</td>
          	</form>
          	</#if>
      	</tr>-->     	          	     	   
      	<tr class="alternate-row">
      		<form id="DayWiseAnalysisReport" name="DayWiseAnalysisReport" method="post" action="<@ofbizUrl>dayWiseAnalysisReport.pdf</@ofbizUrl>">
      			<td> Day Wise PMR Report</td>
      			<td >
      				<input type="hidden" name="unitId" value=""/>      				
      				Date<input  type="text" size="15pt" id="procurementDate" name="procurementDate"/>
      				<input type="submit" formtarget="_blank" value="Download" class="buttontext"/>
          		</td>
          	</form>
      	</tr> 
      	<tr class="alternate-row"> 
      		<form id="feedRecoveriesList" name="feedRecoveriesList" method="post" action="<@ofbizUrl>feedRecoveriesList.txt</@ofbizUrl>">	
      			<td>Feed & Other recoveries</td>
      			<td><input type="hidden" name="unitId" value=""/>				
      				Period<select name="customTimePeriodId" class='h4'>
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
      				<input type="submit" formtarget="_blank" value="Download" class="buttontext"/>
      			</td>
      		</form>
      	</tr>
      	<tr class="alternate-row">
      		<form id="billsAbstract" name="BillsAbstract" method="post" action="<@ofbizUrl>billsAbstract.txt</@ofbizUrl>">	
      			<td>Unit Milk Bills Abst</td>
      			<td><input type="hidden" name="unitId" value=""/>
      			Period<select name="customTimePeriodId" class='h4'>
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
					 pageStart<input type="text" size="3pt" id="selectPageStart" name="pageStart"/> 
      				 pageEnd<input type="text" size="3pt" id="selectPageEnd" name="pageEnd"/>
     				<input type="submit" formtarget="_blank" value="Download" class="buttontext"/>
      			</td>
      		</form>
      	</tr>
      	<tr class="alternate-row">
      		<form id="routeWiseBillsAbstract" name="routeWiseBillsAbstract" method="post" action="<@ofbizUrl>routeWiseBillsAbstract.txt</@ofbizUrl>">	
      			<td>Route-Wise Milk Bills Abst</td>
      			<td><input type="hidden" name="unitId" value=""/>
					Period<select name="customTimePeriodId" class='h4'>
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
					 pageStart<input type="text" size="3pt" id="selectPageStart" name="pageStart"/> 
      				 pageEnd<input type="text" size="3pt" id="selectPageEnd" name="pageEnd"/>
      				<input type="submit" formtarget="_blank" value="Download" class="buttontext"/>
      			</td>
      		</form>
      	</tr>
		<tr  class="alternate-row"> 
			<form name="UnitWiseCenterWiseBankStmt" action="<@ofbizUrl>UnitWiseCenterWiseBankStmt.pdf</@ofbizUrl>">
				<td>Unit,Center Wise Bank Stmt</td>
				<td><input type="hidden" name="shedId" value=""/>
				<input type="hidden" name="reportTypeFlag" value="unitWise"/>
				<input type="hidden" name="unitId" value=""/>  
					Period<select name="customTimePeriodId" class='h4'>
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
					 pageStart<input type="text" size="3pt" id="selectPageStart" name="pageStart"/> 
      				 pageEnd<input type="text" size="3pt" id="selectPageEnd" name="pageEnd"/>          	
					<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
				</td>	
			</form>		
		</tr> 
		<tr class="alternate-row"> 
			<form name="UnitWiseMilkBillSummary" action="<@ofbizUrl>unitWiseMilkBillSummary.pdf</@ofbizUrl>">
				<td>Unit Wise Milk Bill Summary</td>
				<td><input type="hidden" name="unitId" value=""/>  
					Period<select name="customTimePeriodId" class='h4'>  
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
					<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
				</td>	
			</form>		
		</tr>
		<tr  class="alternate-row">     
            <form name="UnitWisePeriodAnalys" action="<@ofbizUrl>UnitWiseDayWiseKgFatAccount.txt</@ofbizUrl>">
                    <td>Unit,Day wise kg-fat A/C</td>
                    <td><input type="hidden" name="unitId" value=""/>
                    Period<select name="customTimePeriodId" class='h4'>
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
                        <input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
                    </td>
            </form>
        </tr>        
        <tr class="alternate-row"> 
      		<form id="unitWisePtcRecoveryAbstract" name="unitWisePtcRecoveryAbstract" method="post" action="<@ofbizUrl>unitWisePtcRecoveryAbstract.pdf</@ofbizUrl>">	
      			<td>Unit-Wise PTC Recovery Abst</td>
      			<td><input type="hidden" name="unitId" value=""/>				
      				Period<select name="customTimePeriodId" class='h4'>
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
      				<input type="submit" formtarget="_blank" value="Download" class="buttontext"/>
      			</td>
      		</form>
      	</tr>
      	<tr class="alternate-row"> 
      		<form id="bankAbstarct" name="bankAbstarct" method="post" action="<@ofbizUrl>bankAbstarct.txt</@ofbizUrl>">	
      			<td>Bank Abstract</td>
      			<td><input type="hidden" name="unitId" value=""/>				
      				Period<select name="customTimePeriodId" class='h4'>
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
      				<input type="submit" formtarget="_blank" value="Download" class="buttontext"/>
      			</td>
      		</form>
      	</tr> 
      	<tr class="alternate-row">     
            <form id="UnitWiseInputOutputAbstract" name="UnitWiseInputOutputAbstract" action="<@ofbizUrl>UnitWiseInputOutputAbstract.txt</@ofbizUrl>">
                    <td>Unit-Wise I/O Abs</td>
                    <td><input type="hidden" name="unitId" value=""/>
                    Period<select name="customTimePeriodId" class='h4'>
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
                     <input type="hidden" name="shedId" value=""/>
                     <input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext" />
                    </td>
            </form>
        </tr>        
	</table>
</div>
</div>
</#if>

<#if reportFrequencyFlag =="ShedReports">
<div class="screenlet">
    <div class="screenlet-title-bar">
     	<h3>Reports for HO Approvel</h3> 
    </div>
    <div class="screenlet-body">
    	<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">    
    		<#if !((ReportConfigMap?has_content)  && ((ReportConfigMap.get("shedWiseProcurementAbstract.pdf"))?exists) && (!(ReportConfigMap.get("shedWiseProcurementAbstract.pdf"))))> 
		      	<tr class="alternate-row">
					<form id="ShedWiseProcurementAbstract" name="ShedWiseProcurementAbstract" method="post" action="<@ofbizUrl>shedWiseProcurementAbstract.pdf</@ofbizUrl>">
						<td width="30%">Shed wise Procurement Abst</td>
						<td width="70%">
							Time Period
		      				<select name="shedCustomTimePeriodId" class='h4'>
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
							<input type="hidden" name="shedId" value=""/>
		      				<input type="submit" formtarget="_blank" value="Download" class="buttontext" />
						</td>	
					</form>		
				</tr>
		   </#if>
		   <#if !((ReportConfigMap?has_content) && ((ReportConfigMap.get("ShedWiseAmountAbstract.pdf"))?exists) && (!(ReportConfigMap.get("ShedWiseAmountAbstract.pdf"))))>    	
		      	<tr class="alternate-row">
		      		<form id="ShedWiseAmountAbstract" name="ShedWiseAmountAbstract" method="post" action="<@ofbizUrl>ShedWiseAmountAbstract.pdf</@ofbizUrl>">
		      			<td width="30%">Shed-Wise Amount Abst</td>
		      			<td width="70%">Time Period	     			
		      				<select name="shedCustomTimePeriodId" class='h4'>
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
							<input type="hidden" name="shedId" value=""/>
		      				<input type="submit" formtarget="_blank" value="Download" class="buttontext" />
		          		</td>
		          	</form>
		      	</tr>
	      </#if> 	
		  <#if !((ReportConfigMap?has_content) && ((ReportConfigMap.get("ShedMilkBillDetails.pdf"))?exists)  && (!(ReportConfigMap.get("ShedMilkBillDetails.pdf"))))> 
		        <tr class="alternate-row">
		            <form id="ShedMilkBillDetails" name="ShedMilkBillDetails" method="post" action="<@ofbizUrl>ShedMilkBillDetails.pdf</@ofbizUrl>">
		                        <td width="30%"> ShedMilk Bill Details</td>
		                        <td width="70%">Time Period
		                    <select name="shedCustomTimePeriodId" class='h4'>
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
		                    <input type="hidden" name="shedId" value=""/>
		      				<input type="submit" formtarget="_blank" value="Download" class="buttontext" />
		                </td>
		            </form>
		        </tr>
	        </#if>
	        <#if !((ReportConfigMap?has_content) && ((ReportConfigMap.get("ShortagesRecovery.pdf"))?exists)  && (!(ReportConfigMap.get("ShortagesRecovery.pdf"))))> 
				<tr class="alternate-row">
		            <form id="ShortagesRecovery" name="ShortagesRecovery" method="post" action="<@ofbizUrl>ShortagesRecovery.pdf</@ofbizUrl>">
		                        <td width="30%"> Shortages Recovery</td>
		                        <td width="70%"> Time Period
		                    <select name="shedCustomTimePeriodId" class='h4'>
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
		                    <input type="hidden" name="shedId" value=""/>
		      				<input type="submit" formtarget="_blank" value="Download" class="buttontext" />
		                     </td>
		            </form>
		        </tr>
	        </#if>
	        <#if !((ReportConfigMap?has_content) && ((ReportConfigMap.get("unitMilkBillNetPayable.txt"))?exists) && (!(ReportConfigMap.get("unitMilkBillNetPayable.txt"))))> 
				<tr class="alternate-row">
					<form id="UnitMilkBillNetPayable" name="UnitMilkBillNetPayable" method="post" action="<@ofbizUrl>unitMilkBillNetPayable.txt</@ofbizUrl>">
						<td width="30%">Unit Milk Bill Net Payable</td>
						<td width="70%">Time Period
		      				<select name="shedCustomTimePeriodId" class='h4'>
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
							<input type="hidden" name="shedId" value=""/>
		      				<input type="submit" formtarget="_blank" value="Download" class="buttontext" />
						</td>	
					</form>		
				</tr>
			</#if>
			<#if !((ReportConfigMap?has_content) && ((ReportConfigMap.get("shedWiseInputOutputAbst.txt"))?exists)  && (!(ReportConfigMap.get("shedWiseInputOutputAbst.txt"))))> 
				<tr class="alternate-row"> 
					<form id="ShedWiseInputOutputAbstract" name="ShedWiseInputOutputAbstract" method="post" action="<@ofbizUrl>shedWiseInputOutputAbst.txt</@ofbizUrl>">
						<td width="30%">Shed Wise Input Output Abst</td>
						<td width="70%">Time Period
		      				<select name="shedCustomTimePeriodId" class='h4'>
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
							<input type="hidden" name="shedId" value=""/>
		      				<input type="submit" formtarget="_blank" value="Download" class="buttontext" />
						</td>	
					</form>		
				</tr>
			</#if>
			<#if !((ReportConfigMap?has_content) && ((ReportConfigMap.get("ShortagesExcel.pdf"))?exists)  && (!(ReportConfigMap.get("ShortagesExcel.pdf"))))> 
		        <tr class="alternate-row">
		            <form id="ShortagesExcel" name="ShortagesExcel" method="post" action="<@ofbizUrl>ShortagesExcel.pdf</@ofbizUrl>">
		                        <td width="30%"> Shortages Excel Formate</td>
		                        <td width="70%"> Time Period
		                    <select name="shedCustomTimePeriodId" class='h4'>
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
		                    <input type="hidden" name="shedId" value=""/>
		      				<input type="submit" formtarget="_blank" value="Download" class="buttontext" />
		                     </td>
		            </form>
		        </tr>
	        </#if>
	        <#if !((ReportConfigMap?has_content) && ((ReportConfigMap.get("MilkBillDifference.txt"))?exists) && (!(ReportConfigMap.get("MilkBillDifference.txt"))))> 
		        <tr class="alternate-row">
		            <form id="MilkBillDifference" name="MilkBillDifference" method="post" action="<@ofbizUrl>MilkBillDifference.txt</@ofbizUrl>">
		                        <td width="30%"> Milk Bill Difference</td>
		                        <td width="70%">Time Period
		                    <select name="shedCustomTimePeriodId" class='h4'>
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
		                    <input type="hidden" name="shedId" value=""/>
		      				<input type="submit" formtarget="_blank" value="Download" class="buttontext" />
		                </td>
		            </form>
		        </tr>
	        </#if>
	        <#if !((ReportConfigMap?has_content) && ((ReportConfigMap.get("WGAcknowledgementReport.txt"))?exists) && (!(ReportConfigMap.get("WGAcknowledgementReport.txt"))))> 
		        <tr  class="alternate-row">
		            <form id="WGAcknowledgementReport" name="WGAcknowledgementReport" method="post" action="<@ofbizUrl>WGAcknowledgementReport.txt</@ofbizUrl>">
		            	<td><span class='h3'>Milk Transfer Ack Report</span></td>
		                 <td>
		                    <table>
		                        <tr>
		                        <td>
		                        From: </td><td><@htmlTemplate.lookupField value="${facilityId?if_exists}" formName="WGAcknowledgementReport" name="facilityId" size="5pt" id="ACKfacilityId" fieldFormName="ProcurementUnitLookupFacility"/></td>
		                        <td> To:</td><td> <@htmlTemplate.lookupField value="${facilityId?if_exists}" formName="WGAcknowledgementReport" name="facilityIdTo" size="5pt" id="ACKfacilityIdTo" fieldFormName="ProcurementUnitLookupFacility"/></td>
		                        <td> Time Period	     			
		      					    	<select name="shedCustomTimePeriodId" class='h4'>
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
									    </select></td>
								 <td> <input type="submit" formtarget="_blank" value="Download" id="button2" class="buttontext h1"/></td>
		                        </tr> 
		                     </table>
		                  </td>
		             </form>
		        </tr>
		        <#else>
		        <tr  class="alternate-row">
		            <form id="AcknowledgementReport" name="AcknowledgementReport" method="post" action="<@ofbizUrl>AcknowledgementReport.txt</@ofbizUrl>">
		            	<td><span class='h3'>Milk Transfer Ack Report</span></td>
		                 <td>
		                    <table>
		                        <tr>
		                        <td> 
		                        From: </td><td><@htmlTemplate.lookupField value="${facilityId?if_exists}" formName="AcknowledgementReport" name="facilityId" size="5pt" id="ACKfacilityId" fieldFormName="ProcurementUnitLookupFacility"/></td>
		                        <td> To:</td><td><@htmlTemplate.lookupField value="${facilityId?if_exists}" formName="AcknowledgementReport" name="facilityIdTo" size="5pt" id="ACKfacilityIdTo" fieldFormName="ProcurementUnitLookupFacility"/></td>
		                        <td> Time Period	     			
		      					    	<select name="shedCustomTimePeriodId" class='h4'>
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
									    </select></td>
								 <td> <input type="submit" formtarget="_blank" value="Download" id="button2" class="buttontext h1"/></td>
		                        </tr> 
		                     </table>
		                  </td>
		             </form>
		        </tr>
	       </#if>  	
        </table>
    </div>
 </div>
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Reports for Bank Payments</h3>
    </div>
    <div class="screenlet-body">
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
      	<#if !((ReportConfigMap?has_content) && ((ReportConfigMap.get("BankWiseTotalPayment.txt"))?exists) && (!(ReportConfigMap.get("BankWiseTotalPayment.txt"))))>   	
	      	<tr class="alternate-row">
	      		<form id="BankWiseTotalPayment" name="BankWiseTotalPayment" method="post" action="<@ofbizUrl>BankWiseTotalPayment.txt</@ofbizUrl>">
	      			<td width="30%">Bank-Wise Total Payment</td>
	      			<td width="70%">Time Period	     			
	      				<select name="shedCustomTimePeriodId" class='h4'>
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
						<input type="hidden" name="shedId" value=""/>
	      				<input type="submit" formtarget="_blank" value="Download" class="buttontext" />
	          		</td>
	          	</form>
	      	</tr>
      	</#if>
       <#if !((ReportConfigMap?has_content) && ((ReportConfigMap.get("unitBankWiseStatement.pdf"))?exists) && (!(ReportConfigMap.get("unitBankWiseStatement.pdf"))))> 
	      	<tr class="alternate-row">
	      		<form id="UnitBankWiseStatement" name="unitBankWiseStatement" method="post" action="<@ofbizUrl>unitBankWiseStatement.pdf</@ofbizUrl>">
	      			<td width="30%">Unit-Bank-Wise Statement</td>
	      			<td width="70%">Time Period	     			
	      				<select name="shedCustomTimePeriodId" class='h4'>
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
						<input type="hidden" name="shedId" value=""/>
	      				<input type="submit" formtarget="_blank" value="Download" class="buttontext" />
	          		</td>
	          	</form>
	      	</tr>
      	</#if>
       <#if !((ReportConfigMap?has_content) && ((ReportConfigMap.get("bankUnitWiseAbst.txt"))?exists)  && (!(ReportConfigMap.get("bankUnitWiseAbst.txt"))))>
         <tr class="alternate-row">
            <form id="BankUnitWiseAbstract" name="BankUnitWiseAbstract" method="post" action="<@ofbizUrl>bankUnitWiseAbst.txt?reportTypeFlag=unitWise</@ofbizUrl>">
                        <td width="30%"> Bank Wise Unit Wise Abstract</td>
                        <td width="70%">Time Period
                    <select  name="shedCustomTimePeriodId" class='h4'>
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
                    <input type="hidden" name="shedId" value=""/>
      				<input type="submit" formtarget="_blank" value="Download" class="buttontext" />
                </td>
            </form>
        </tr>
      </#if>
       <#if !((ReportConfigMap?has_content) && ((ReportConfigMap.get("bankUnitWiseAbst.txt"))?exists) && (!(ReportConfigMap.get("bankUnitWiseAbst.txt"))))>
	         <tr class="alternate-row">
	            <form id="BankCenterWiseAbstract" name="BankCenterWiseAbstract" method="post" action="<@ofbizUrl>bankUnitWiseAbst.txt?reportTypeFlag=centerWise</@ofbizUrl>">
	                <td width="30%"> Bank Wise Center Wise Abstract</td>
	                    
	                    <td width="70%" id="bankName">Bank Name
	                    <select name="bankName" id="finAcc" onchange="copy();" class='h4'>
	                        <#list shedOptionList?if_exists as finAcc>    
		                  	     <option value='${finAcc}'>
		                    		${finAcc}
		                  		 </option>
	                		</#list>        
	                    </select>
	                    Time Period
	                    <select name="shedCustomTimePeriodId" class='h4'>
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
	                    <input type="hidden" name="shedId" value=""/>
	      				<input type="submit" formtarget="_blank" value="Download" class="buttontext"  onClick="javascript:appendRecoveryFlag('BankCenterWiseAbstract');"/>
	                </td>
	            </form>
	        </tr>
       </#if>
       <#if !((ReportConfigMap?has_content) && ((ReportConfigMap.get("BankBranchWiseUnitCenterWiseReport.txt"))?exists) && (!(ReportConfigMap.get("BankBranchWiseUnitCenterWiseReport.txt"))))>
	         <tr class="alternate-row">
	            <form id="BankBranchWiseUnitCenterWiseReport" name="BankBranchWiseUnitCenterWiseReport" method="post" action="<@ofbizUrl>BankBranchWiseUnitCenterWiseReport.txt</@ofbizUrl>">
	                <td width="30%"> BankBranch Wise UnitCenter Wise Abstract</td>
	                    <td width="70%" id="bankName">Bank Name
	                    <select name="bankName" id="finAcc" onchange="copy();" class='h4'>
	                        <#list shedOptionList?if_exists as finAcc>    
		                  	     <option value='${finAcc}'>
		                    		${finAcc}
		                  		 </option>
	                		</#list>        
	                    </select>
	                    Time Period
	                    <select name="shedCustomTimePeriodId" class='h4'>
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
	                    <input type="hidden" name="shedId" value=""/>
	      				<input type="submit" formtarget="_blank" value="Download" class="buttontext"  onClick="javascript:appendRecoveryFlag('BankCenterWiseAbstract');"/>
	                </td>
	            </form>
	        </tr>
       </#if>
       <#if !((ReportConfigMap?has_content) && ((ReportConfigMap.get("bankUnitWiseAbst.txt"))?exists) && (!(ReportConfigMap.get("bankUnitWiseAbst.txt"))))>
        <tr class="alternate-row">
            <form id="BrachCenterWisePayment" name="BrachCenterWisePayment" method="post" action="<@ofbizUrl>bankUnitWiseAbst.txt?reportTypeFlag=centerPayment</@ofbizUrl>">
                        <td width="30%"> Branch Center Wise Payment</td>
                        <td width="70%">Time Period
                    <select name="shedCustomTimePeriodId" class='h4'>
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
                    <input type="hidden" name="shedId" value=""/>
                    <input type="submit" formtarget="_blank" value="Download" class="buttontext" onClick="javascript:appendRecoveryFlag('BrachCenterWisePayment');" />
                </td>
            </form>
        </tr>
         <tr class="alternate-row">
            <form id="WGDBankWiseTotalPayment" name="WGDBankWiseTotalPayment" method="post" action="<@ofbizUrl>bankUnitWiseAbst.txt?reportTypeFlag=bankPayment</@ofbizUrl>">
                        <td width="30%"> Bank Wise Total Payment[WGD]</td>
                        <td width="70%">Time Period
                    <select name="shedCustomTimePeriodId" class='h4'>
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
                    <input type="hidden" name="shedId" value=""/>
                    <input type="submit" formtarget="_blank" value="Download" class="buttontext" onClick="javascript:appendRecoveryFlag('WGDBankWiseTotalPayment');" />
                </td>
            </form>
        </tr>
      </#if>
      <#if !((ReportConfigMap?has_content) && ((ReportConfigMap.get("bankBranchWiseAbst.txt"))?exists)  && (!(ReportConfigMap.get("bankBranchWiseAbst.txt"))))> 
        <tr class="alternate-row">
            <form id="BankBranchWiseAbstract" name="BankBranchWiseAbstract" method="post" action="<@ofbizUrl>bankBranchWiseAbst.txt</@ofbizUrl>">
                        <td width="30%"> Bank Branch Wise Milk Payment</td>
                        <td width="70%">Time Period
                    <select name="shedCustomTimePeriodId" class='h4'>
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
                    <input type="hidden" name="shedId" value=""/>
      				<input type="submit" formtarget="_blank" value="Download" class="buttontext" />
                </td>
            </form>
        </tr>
       </#if>
		<#if !((ReportConfigMap?has_content) && ((ReportConfigMap.get("unitMilkBillNetPayableCenter.txt"))?exists) && (!(ReportConfigMap.get("unitMilkBillNetPayableCenter.txt"))))> 
			<tr class="alternate-row"> 
				<form id="UnitMilkBillNetPayableForCenter" name="UnitMilkBillNetPayableForCenter" method="post" action="<@ofbizUrl>unitMilkBillNetPayableCenter.txt</@ofbizUrl>">
					<td width="30%">Unit Milk Bill Net Payable For CenterWise billing</td>
					<td width="70%">Time Period
	      				<select name="shedCustomTimePeriodId" class='h4'>
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
	                    CenterWise/UnitWise
                    	<select name="rTypeFlag" class='h4'>
                    		<option value='centerWise'>center wise</option>
                    		<option value='unitWise'>unit wise</option>
                    	</select>
						<input type="hidden" name="shedId" value=""/>
	      				<input type="submit" formtarget="_blank" value="Download" class="buttontext" onClick="javascript:appendRecoveryFlag('UnitMilkBillNetPayableForCenter');" class="buttontext"/>
					</td>	
				</form>		
			</tr>
		</#if>
       <#if security.hasEntityPermission("SENDPROCSMS", "_VIEW", session)>
         <tr class="alternate-row">
      		<form id="ProcurementSummarySms" name="ProcurementSummarySms" method="post" action="<@ofbizUrl>ProcurementSummarySms.pdf</@ofbizUrl>">
      			<td>Finalize Procurement Billing</td>
      			<td>Time Period
                    <select name="shedCustomTimePeriodId" class='h4'>
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
                    <input type="hidden" name="shedId" value=""/>
                    <input type="hidden" name="procSms" value="Y"/>
                    <input type="submit" formtarget="_blank" value="SMS" class="buttontext"/>
          	</form>
	        </tr>
	      </#if>   
       <#--<#if !((ReportConfigMap?has_content) && ((ReportConfigMap.get("BankCoverLetter.txt"))?exists)  && (!(ReportConfigMap.get("BankCoverLetter.txt"))))>
	        <tr>
	            <form id="BankCoverLetter" name="BankCoverLetter" method="post" action="<@ofbizUrl>BankCoverLetter.txt</@ofbizUrl>">
	                    <td width="30%"> Bank Cover Letter</td>
	                    <td width="70%">Time Period
	                    <select name="shedCustomTimePeriodId" class='h4'>
	                        <#list timePeriodList as timePeriod>    
	                			  <#if ((timePeriod.fromDate)?has_content) && ((timePeriod.thruDate)?has_content)>
	                			   <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
	                       			<#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
			                  	    <option value='${timePeriod.customTimePeriodId}' >
			                    		${fromDate}-${thruDate}
			                  		 </option>
			                  	 </#if>
	                		</#list>        
	                    </select>
	                    <input type="hidden" name="shedId" value=""/>
	      				<input type="submit" formtarget="_blank" value="Download" class="buttontext" />
	                </td>
	            </form>
	        </tr>
        </#if>-->
        <#--<#if !((ReportConfigMap?has_content) && ((ReportConfigMap.get("MilkBillRequisition.pdf"))?exists)  && (!(ReportConfigMap.get("MilkBillRequisition.pdf"))))>
	        <tr>
	            <form id="MilkBillRequisition" name="MilkBillRequisition" method="post" action="<@ofbizUrl>MilkBillRequisition.pdf</@ofbizUrl>">
	                    <td width="30%">Milk Bill Requisition</td>
	                    <td width="70%">Time Period
	                    <select name="shedCustomTimePeriodId" class='h4'>
	                        <#list timePeriodList as timePeriod>    
	                			  <#if ((timePeriod.fromDate)?has_content) && ((timePeriod.thruDate)?has_content)>
	                			   <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
	                       			<#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
			                  	    <option value='${timePeriod.customTimePeriodId}' >
			                    		${fromDate}-${thruDate}
			                  		 </option>
			                  	 </#if>
	                		</#list>        
	                    </select>
	                    <input type="hidden" name="shedId" value=""/>
	      				<input type="submit" formtarget="_blank" value="Download" class="buttontext" />
	                </td>
	            </form>
	        </tr>
        </#if>-->
      </table>
    </div>
 </div>
 <div class="screenlet">
	<div class="screenlet-title-bar">
		<h3>CD FORMATS</h3> 
	</div>
	<div class="screenlet-body">
		<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">    	
			<#if !((ReportConfigMap?has_content) && ((ReportConfigMap.get("sbi-ecs"))?exists) && (!(ReportConfigMap.get("sbi-ecs"))))>  
		        <tr class="alternate-row">
		    		<form id="sbi-ecs" name="sbi-ecs" method="post" action="<@ofbizUrl>sbi-ecs</@ofbizUrl>">
		                        <td width="30%">ECS-TXT</td>
		                    <td width="70%">
		                    Bank Name
		                    <select name="ecsBankName">
		                		<option value='STATE BANK OF HYDERABAD' >STATE BANK OF HYDERABAD</option>
							</select>  
							Time Period
		                    <select name="shedCustomTimePeriodId" class='h4'>
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
		                    CenterWise/UnitWise
	                    <select name="rTypeFlag" class='h4'>
	                    	<option value='centerWise'>center wise</option>
	                    	<option value='unitWise'>unit wise</option>
	                    </select>
		                    <input type="hidden" name="shedId" value=""/>
		      				<input type="submit" formtarget="_blank" value="Download" class="buttontext" />
		                </td>
		            </form>
		        </tr>
        	</#if>
	       <#if !((ReportConfigMap?has_content) && ((ReportConfigMap.get("sbi-ecs.csv"))?exists) && (!(ReportConfigMap.get("sbi-ecs.csv"))))>
	           <tr class="alternate-row">
	           		<form id="sbi-ecs-csv" name="sbi-ecs-csv" method="post" action="<@ofbizUrl>sbi-ecs.csv</@ofbizUrl>">
	                    <td width="30%">ECS-SBI</td>
	                    <td width="70%">
	                     Bank Name
	                    <select name="ecsBankName">
	                		<option value='STATE BANK OF INDIA' >STATE BANK OF INDIA</option>
						</select>  
	                    Time Period
	                    <select name="shedCustomTimePeriodId" class='h4'>
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
	                    CenterWise/UnitWise
	                    <select name="rTypeFlag" class='h4'>
	                    	<option value='centerWise'>center wise</option>
	                    	<option value='unitWise'>unit wise</option>
	                    </select>
	                    <input type="hidden" name="shedId" value=""/>
	      				<input type="submit" value="TXT" onClick="javascript:appendParams('sbi-ecs-csv', '<@ofbizUrl>sbi-ecs.txt</@ofbizUrl>');" class="buttontext"/>
						<input type="submit" value="CSV" onClick="javascript:appendParams('sbi-ecs-csv', '<@ofbizUrl>sbi-ecs.csv</@ofbizUrl>');" class="buttontext"/>   
	              	  </td>
	            	</form>
	        	</tr>
	       </#if>   
	    </table>
	</div>
</div>
 <div class="screenlet">
	<div class="screenlet-title-bar">
		<h3>TIP REPORTS</h3> 
	</div>
	<div class="screenlet-body">
		<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">    	
			<#if security.hasEntityPermission("SHEDAVGRATE", "_VIEW", session)>
	      		<tr class="alternate-row">
		      		<form id="ShedWiseAverageRate" name="ShedWiseAverageRate" method="post" action="<@ofbizUrl>ShedWiseAverageRate.txt</@ofbizUrl>">
		      			<td width="30%">Shed-Wise Tip And Average Rate</td>
		      			<td width="70%">
		      				From <input  type="text" size="15pt" id="avgRateFromDate" name="fromDate" class="required"/><em>*</em>
	                    	To <input  type="text" size="15pt" id="avgRateThruDate" name="thruDate" class="required"/><em>*</em>
	                    	<select name="rTypeFlag" class='h4'>
	                    		<option value='Period Wise'>Period wise</option>
	                    		<option value='Month Wise'>Month wise</option>
	                    		<option value='Year Wise'>Year wise</option>
	                   		 </select>
							<input type="hidden" name="shedId" value=""/>
		      				<input type="submit" formtarget="_blank" value="Download" class="buttontext" />
		          		</td>
		          	</form>
		      	</tr>
	    	</#if>  	
			<#if !((ReportConfigMap?has_content) && ((ReportConfigMap.get("shedWiseTipAmount.txt"))?exists)  && (!(ReportConfigMap.get("shedWiseTipAmount.txt"))))> 
		        <tr class="alternate-row">
		            <form id="ShedWiseTipAmount" name="ShedWiseTipAmount" method="post" action="<@ofbizUrl>shedWiseTipAmount.txt</@ofbizUrl>">
		                        <td width="30%">Shed Wise Tip Amount</td>
		                        <td width="70%">Time Period
		                    <select name="shedCustomTimePeriodId" class='h4'>
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
		                    <input type="hidden" name="shedId" value=""/>
		      				<input type="submit" formtarget="_blank" value="Download" class="buttontext" />
		                </td>
		            </form>
		        </tr>
       		</#if>	
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
					<input type="hidden" name="unitId" value=""/>
					Period
      				<select name="shedCustomTimePeriodId" class='h4'>
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
      				<input type="submit" value="Download Dbf" onClick="javascript:appendParams('ProcurementEntriesCsv', '<@ofbizUrl>MPROC.DBF</@ofbizUrl>');" class="buttontext"/>
					<#if security.hasEntityPermission("CSV", "_VIEW", session)><input type="submit" value="CSV" onClick="javascript:appendParams('ProcurementEntriesCsv', '<@ofbizUrl>MPROC.csv</@ofbizUrl>');" class="buttontext"/> </#if>
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
      				<select name="shedCustomTimePeriodId" class='h4'>
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
      			     <input type="submit" value="Download Dbf" onClick="javascript:appendParams('MpAdditions', '<@ofbizUrl>MPADDN.DBF</@ofbizUrl>');" class="buttontext"/>
					 <#if security.hasEntityPermission("CSV", "_VIEW", session)><input type="submit" value="CSV" onClick="javascript:appendParams('MpAdditions', '<@ofbizUrl>MPADDN.csv</@ofbizUrl>');" class="buttontext"/></#if>
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
      				<select name="shedCustomTimePeriodId" class='h4'>
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
      				 <input type="submit" value="Download Dbf" onClick="javascript:appendParams('MpDeductions', '<@ofbizUrl>MPDED.DBF</@ofbizUrl>');" class="buttontext"/>
					 <#if security.hasEntityPermission("CSV", "_VIEW", session)><input type="submit" value="CSV" onClick="javascript:appendParams('MpDeductions', '<@ofbizUrl>MPDED.csv</@ofbizUrl>');" class="buttontext"/></#if>
          		</td>
          	</form>
      	</tr> 
      	<tr class="alternate-row">
      		<form id="MpAbstract" name="MpAbstract" method="post" action="<@ofbizUrl>MPABS.DBF</@ofbizUrl>">
      			<td>Mp ABS</td>
      			<td >    			
					<input type="hidden" name="shedId" value=""/>					
					Period
      				<select name="shedCustomTimePeriodId" class='h4'>
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
      				<input type="submit" value="Download Dbf" onClick="javascript:appendParams('MpAbstract', '<@ofbizUrl>MPABS.DBF</@ofbizUrl>');" class="buttontext"/>
					<#if security.hasEntityPermission("CSV", "_VIEW", session)><input type="submit" value="CSV" onClick="javascript:appendParams('MpAbstract', '<@ofbizUrl>MPABS.csv</@ofbizUrl>');" class="buttontext"/></#if>
          		</td>
          	</form>
      	</tr> 
      	<tr class="alternate-row">
      		<form id="MpAbstractNew" name="MpAbstractNew" method="post" action="<@ofbizUrl>MPABSNEW.DBF</@ofbizUrl>">
      			<td>Mp ABS New</td>
      			<td >    			
					<input type="hidden" name="shedId" value=""/>					
					Period
      				<select name="shedCustomTimePeriodId" class='h4'>
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
      				<input type="submit" value="Download Dbf" onClick="javascript:appendParams('MpAbstractNew', '<@ofbizUrl>MPABSNEW.DBF</@ofbizUrl>');" class="buttontext"/>
					<#if security.hasEntityPermission("CSV", "_VIEW", session)><input type="submit" value="CSV" onClick="javascript:appendParams('MpAbstractNew', '<@ofbizUrl>MPABSNEW.csv</@ofbizUrl>');" class="buttontext"/></#if>
          		</td>
          	</form>
      	</tr> 
      	<tr class="alternate-row">
      		<form id="UnitMasterCsv" name="UnitMasterCsv" method="post" action="<@ofbizUrl>UNTMAS.DBF</@ofbizUrl>">
      			<td>UNIT MASTER</td>
      			<td >    			
					<input type="hidden" name="shedId" value=""/>					
					Period
      				<select name="shedCustomTimePeriodId" class='h4'>
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
      				<input type="submit" value="Download Dbf" onClick="javascript:appendParams('UnitMasterCsv', '<@ofbizUrl>UNTMAS.DBF</@ofbizUrl>');" class="buttontext"/>
					<#if security.hasEntityPermission("CSV", "_VIEW", session)><input type="submit" value="CSV" onClick="javascript:appendParams('UnitMasterCsv', '<@ofbizUrl>UNITMAS.csv</@ofbizUrl>');" class="buttontext"/></#if>
          		</td>
          	</form>
      	</tr>      	
      	<tr class="alternate-row">
      		<form id="MPMasterCsv" name="MPMasterCsv" method="post" action="<@ofbizUrl>MPMAS.DBF</@ofbizUrl>">
      			<td>MPMAS</td>
      			<td >    			
					<input type="hidden" name="shedId" value=""/>					
					Period
      				<select name="shedCustomTimePeriodId" class='h4'>
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
      				<input type="submit" value="Download Dbf" onClick="javascript:appendParams('MPMasterCsv', '<@ofbizUrl>MPMAS.DBF</@ofbizUrl>');" class="buttontext"/>
					<#if security.hasEntityPermission("CSV", "_VIEW", session)><input type="submit" value="CSV" onClick="javascript:appendParams('MPMasterCsv', '<@ofbizUrl>MPMAS.csv</@ofbizUrl>');" class="buttontext"/></#if>
          		</td>
          	</form>
      	</tr>
      	<tr class="alternate-row">
      		<form id="RouteMasCsv" name="RouteMasCsv" method="post" action="<@ofbizUrl>RTMAS.DBF</@ofbizUrl>">
      			<td>ROUTE MASTER</td>
      			<td >    			
					<input type="hidden" name="shedId" value=""/>					
      				<input type="submit" value="Download Dbf" onClick="javascript:appendParams('RouteMasCsv', '<@ofbizUrl>RTMAS.DBF</@ofbizUrl>');" class="buttontext"/>
					<#if security.hasEntityPermission("CSV", "_VIEW", session)><input type="submit" value="CSV" onClick="javascript:appendParams('RouteMasCsv', '<@ofbizUrl>ROUTEMAS.csv</@ofbizUrl>');" class="buttontext"/></#if>
          		</td>
          	</form>
      	</tr>
      	<tr class="alternate-row">
      		<form id="BANKMASCsv" name="BANKMASCsv" method="post" action="<@ofbizUrl>BANKMAS.DBF</@ofbizUrl>">
      			<td>BANKMAS</td>
      			<td >    			
					<input type="hidden" name="shedId" value=""/>					
      				<input type="submit" value="Download Dbf" onClick="javascript:appendParams('BANKMASCsv', '<@ofbizUrl>BANKMAS.DBF</@ofbizUrl>');" class="buttontext"/>
					<#if security.hasEntityPermission("CSV", "_VIEW", session)><input type="submit" value="CSV" onClick="javascript:appendParams('BANKMASCsv', '<@ofbizUrl>BANKMAS.csv</@ofbizUrl>');" class="buttontext"/></#if>
          		</td>
          	</form>
      	</tr>
      	<tr class="alternate-row">
      		<form id="GBMASCsv" name="GBMASCsv" method="post" action="<@ofbizUrl>GBMAS.DBF</@ofbizUrl>">
      			<td>GBMAS</td>
      			<td >    			
					<input type="hidden" name="shedId" value=""/>					
      				<input type="submit" value="Download Dbf" onClick="javascript:appendParams('GBMASCsv', '<@ofbizUrl>GBMAS.DBF</@ofbizUrl>');" class="buttontext"/>
					<#if security.hasEntityPermission("CSV", "_VIEW", session)><input type="submit" value="CSV" onClick="javascript:appendParams('GBMASCsv', '<@ofbizUrl>GBMAS.csv</@ofbizUrl>');" class="buttontext"/></#if>
          		</td>
          	</form>
      	</tr>
      </table>
     </div> 	
   </div>
  
 <div class="screenlet">
    <div class="screenlet-title-bar">
     <h3>Name Master Reports</h3> 
    </div>
    <div class="screenlet-body">
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">    	
      	<tr class="alternate-row"> 
      		<form id="UnitWiseNameMaster" name="UnitWiseNameMaster" method="post" action="<@ofbizUrl>UnitWiseNameMaster.txt</@ofbizUrl>">	
      			<td>UnitWise Name Master</td>
      				<td >    			
					<input type="hidden" name="shedId" value=""/>					
					Period
      				<select name="shedCustomTimePeriodId" class='h4'>
      				<#if timePeriodList?has_content>
                		<#list timePeriodList as timePeriod>    
                			 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
                			 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${fromDate}-${thruDate}
	                  		</option>
                		</#list>  
                	</#if>	          
					</select>    				
      				<input type="submit" formtarget="_blank" value="Download" class="buttontext"/>
          		</td>
      			</td>
      		</form>
      	</tr>
      	<tr class="alternate-row"> 
      		<form id="VillageNameMaster" name="VillageNameMaster" method="post" action="<@ofbizUrl>VillageNameMaster.txt</@ofbizUrl>">	
      			<td>Village Name Master</td>
      				<td><input type="hidden" name="unitId" value=""/>
                    Period<select name="customTimePeriodId" class='h4'>
                    <#if timePeriodList?has_content>
                       <#list timePeriodList as timePeriod>    
                			 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
                			 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${fromDate}-${thruDate}
	                  		</option>
                		</#list>
                	</#if>	
                    </select>   
                     <input type="hidden" name="shedId" value=""/>
                     <input type="submit" value="Download Dbf" onClick="javascript:appendParams('VillageNameMaster', '<@ofbizUrl>VillageNameMaster.txt</@ofbizUrl>');" class="buttontext"/>
					 <input type="submit" value="CSV" onClick="javascript:appendParams('VillageNameMaster', '<@ofbizUrl>shedAgenst.csv</@ofbizUrl>');" class="buttontext"/>
                    </td>
      			</td>
      		</form>
      	</tr>
      	<tr class="alternate-row"> 
				<form id="ConsolidatedReport" name="ConsolidatedReport" method="post" action="<@ofbizUrl>ConsolidatedReport.pdf</@ofbizUrl>">
			    	<td>All Sheds Consolidated Report</td>
			   		<td >    			
					<input type="hidden" name="shedId" value=""/>					
					Period
      				<select name="shedCustomTimePeriodId" class='h4'>
      				<#if consolidatedTimePeriodList?has_content>
                		<#list consolidatedTimePeriodList as timePeriod>    
                			 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.fromDate, "MMMdd")/>
                			 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriod.thruDate, "MMMdd yyyy")/>
                  	    	<option value='${timePeriod.customTimePeriodId}' >
	                    		${fromDate}-${thruDate}
	                  		</option>
                		</#list>  
                	</#if>	          
					</select>    				
      				<input type="submit" formtarget="_blank" value="Download" class="buttontext"/>
          		</td>
			 	</form>
			 </tr>
      	   
      </table>
    </div>
 </div>
 </div>
 <div class="screenlet">
    <div class="screenlet-title-bar">
     <h3>Milk Receipts Report</h3> 
    </div>
    <div class="screenlet-body">
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">    	
      	<tr class="alternate-row">
            <form id="UnitWiseMilkReceipt" name="UnitWiseMilkReceipt" method="post" action="<@ofbizUrl>UnitWiseMilkReceipt.txt</@ofbizUrl>">
                   <td width="30%">Unit wise Milk Receipt</td>
	                    <td width="70%">
                    Period<select name="shedCustomTimePeriodId" class='h4'>
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
                    <input type="hidden" name="shedId" value=""/>
                    <input type="hidden" name="unitId" value=""/> 
                    <input type="submit" formtarget="_blank" value="Download" class="buttontext" onClick="javascript:appendRecoveryFlag('WGDBankWiseTotalPayment');" />
                </td>
            </form>
        </tr>
      </table>
    </div>
 </div>
 
 <div class="screenlet">
    <div class="screenlet-title-bar">
     <h3>Utilization Report</h3> 
    </div>
    <div class="screenlet-body">
      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">    	
      	<tr class="alternate-row"> 
		  <form id="ShedwiseUnitwiseCapacityUtilization" name="ShedwiseUnitwiseCapacityUtilization" method="post" action="<@ofbizUrl>ShedwiseUnitwiseCapacityUtilization.txt</@ofbizUrl>">
	    	<td>Shed-wise Unit-wise Capacity Utilization</td>
	   		<td><input type="hidden" name="shedId" value=""/>
	   		 	<input type="hidden" name="unitId" value=""/> 
	        	From<input  type="text" size="15pt" id="ShedwiseUnitwiseCapacityUtilizationFromDate" name="fromDate"/>
	        	To<input  type="text" size="15pt" id="ShedwiseUnitwiseCapacityUtilizationThruDate" name="thruDate"/>                           
	        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
	    	</td>   
	 	 </form>
	   </tr> 
      </table>
    </div>
 </div>

 </div>
 </#if>
 <#if reportFrequencyFlag =="AnnualReports">
	<div class="screenlet">
	    <div class="screenlet-title-bar">
	     <h3>Annual Reports</h3> 
	    </div>
	    <div class="screenlet-body">
	      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">    	
	      	<tr class="alternate-row"> 
				<form id="ShedWiseMonthWiseKgFatKgSnf" name="ShedWiseMonthWiseKgFatKgSnf" method="post" action="<@ofbizUrl>ShedWiseMonthWiseKgFatKgSnf.txt</@ofbizUrl>">
			    	<td>Shed Abstract(Procurement)</td>
			   		<td><input type="hidden" name="shedId" value=""/> 
			        	From<input  type="text" size="20pt" id="AnnualKgFatKgSnfFromDate" name="fromDate"/>
			        	To<input  type="text" size="20pt" id="AnnualKgFatKgSnfThruDate" name="thruDate"/>                           
			        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
			    	</td>   
			 	</form>     
	      	</tr>
	       <tr>       
            <form id="shedWiseAnnualAbstract" name="shedWiseAnnualAbstract"  method="post" action="<@ofbizUrl>shedWiseAnnualAbstract.txt</@ofbizUrl>">
                <td>Shed-Unit-Wise Payment</td>
                <td><input type="hidden" name="shedId" value=""/> 
                    From<input  type="text" size="20pt" id="shedWiseAnnualAbstractFromDate" name="fromDate"/>
                    To<input  type="text" size="20pt" id="shedWiseAnnualAbstractThruDate" name="thruDate"/>                           
                    <input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
                </td>   
            </form>     
          </tr>
	      	<tr class="alternate-row"> 
				<form id="UnitWiseKgFatKgSnfTotalSolids" name="UnitWiseKgFatKgSnfTotalSolids" method="post" action="<@ofbizUrl>UnitWiseKgFatKgSnfTotalSolids.txt</@ofbizUrl>">
			    	<td>Shed-Unit-Wise procurement</td>
			   		<td><input type="hidden" name="shedId" value=""/> 
			       		From<input  type="text" size="20pt" id="UnitWiseKgFatKgSnfTotalSolidsFromDate" name="fromDate"/>
			        	To<input  type="text" size="20pt" id="UnitWiseKgFatKgSnfTotalSolidsThruDate" name="thruDate"/>                           
			        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
			    	</td>   
			 	</form>     
	      	</tr>
	      	<tr class="alternate-row"> 
				<form id="UnitWiseMonthWiseKgFatKgSnf" name="UnitWiseMonthWiseKgFatKgSnf" method="post" action="<@ofbizUrl>UnitWiseMonthWiseKgFatKgSnf.txt</@ofbizUrl>">
			    	<td>Unit-Wise, Month-Wise Procurement Statement</td>
			   		<td><input type="hidden" name="shedId" value=""/>
			   			<input type="hidden" name="unitId" value=""/> 
			        	From<input  type="text" size="20pt" id="UnitMonthKgFatKgSnfTotalSolidsFromDate" name="fromDate"/>
			        	To<input  type="text" size="20pt" id="UnitMonthKgFatKgSnfTotalSolidsThruDate" name="thruDate"/>                           
			        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
			    	</td>   
			 	</form>
			 </tr>
	      	<tr class="alternate-row"> 
				<form id="ShedWiseMonthWiseMilkPayment " name="ShedWiseMonthWiseMilkPayment" method="post" action="<@ofbizUrl>ShedWiseMonthWiseMilkPayment.txt</@ofbizUrl>">
			    	<td>Shed-Abstract(Payment)</td>
			   		<td><input type="hidden" name="shedId" value=""/> 
			        	From<input  type="text" size="20pt" id="ShedWiseMonthWiseMilkPaymentFromDate" name="fromDate"/>
			        	To<input  type="text" size="20pt" id="ShedWiseMonthWiseMilkPaymentThruDate" name="thruDate"/>                           
			        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
			    	</td>   
			 	</form>     
	      	</tr>
	      	<tr class="alternate-row"> 
				<form id="UnitWiseMonthWiseMilkPayment " name="UnitWiseMonthWiseMilkPayment" method="post" action="<@ofbizUrl>UnitWiseMonthWiseMilkPayment.txt</@ofbizUrl>">
			    	<td>Unit-Wise Month-Wise Payment Statement</td>
			   		<td><input type="hidden" name="shedId" value=""/> 
			   			<input type="hidden" name="unitId" value=""/> 
			        	From<input  type="text" size="20pt" id="UnitWiseMonthWiseMilkPaymentFromDate" name="fromDate"/>
			        	To<input  type="text" size="20pt" id="UnitWiseMonthWiseMilkPaymentThruDate" name="thruDate"/>                           
			        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
			    	</td>   
			 	</form>     
	      	</tr>
	      	<tr class="alternate-row"> 
				<form id="FortnightKgfatAccount" name="FortnightKgfatAccount" method="post" action="<@ofbizUrl>FortnightKgfatAccount.txt</@ofbizUrl>">
			    	<td>Frotnightly KgFat A/C</td>
			   		<td><input type="hidden" name="shedId" value=""/>
			   		 	<input type="hidden" name="unitId" value=""/> 
			        	From<input  type="text" size="20pt" id="FortnightKgfatAccountFromDate" name="fromDate"/>
			        	To<input  type="text" size="20pt" id="FortnightKgfatAccountThruDate" name="thruDate"/>                           
			        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
			    	</td>   
			 	</form>
			 </tr>
	      	<tr class="alternate-row"> 
				<form id="FNCenterWiseKgFatKgSnf" name="FNCenterWiseKgFatKgSnf" method="post" action="<@ofbizUrl>FNCenterWiseKgFatKgSnf.txt</@ofbizUrl>">
			    	<td> <span class='h3'>FN Collection Center-Wise Procurement</span></td>
                    <td><input type="hidden" name="shedId" value=""/> 
                        <input type="hidden" name="unitId" value=""/>
                      <table>
                         	<tr>
                        		<td><span class='h3'>Center: </span><input  size="10pt" type="text" id="facilityCode" name="facilityCode"/></td>
                        		<td><span class='h3'>From: </span><input  size="10pt" type="text" id="FNCenterWiseKgFatKgSnfFromDate" name="fromDate"/></td>
                         		<td><span class='h3'>To: </span><input size="10pt" type="text" id="FNCenterWiseKgFatKgSnfThruDate" name="thruDate"/></td>
                        		<td><input type="submit" formtarget="_blank" value="Download" class="buttontext h1"/> </td>
                   			</tr>
                      </table>
                  </td>   
			 	</form>     
	      	</tr>
	      	<tr class="alternate-row"> 
				<form id="FNCollectionCentrewisePayment" name="FNCollectionCentrewisePayment" method="post" action="<@ofbizUrl>FNCollectionCentrewisePayment.txt</@ofbizUrl>">
			    	<td> <span class='h3'>FN Collection Centre-wise Payment</span></td>
                    <td><input type="hidden" name="shedId" value=""/> 
                        <input type="hidden" name="unitId" value=""/>
                      <table>
                         	<tr>
                        		<td><span class='h3'>Center: </span><input  size="10pt" type="text" id="facilityCode" name="facilityCode"/></td>
                        		<td><span class='h3'>From: </span><input  size="10pt" type="text" id="FNCollectionCentrewisePaymentFromDate" name="fromDate"/></td>
                         		<td><span class='h3'>To: </span><input size="10pt" type="text" id="FNCollectionCentrewisePaymentThruDate" name="thruDate"/></td>
                        		<td><input type="submit" formtarget="_blank" value="Download" class="buttontext h1"/> </td>
                   			</tr>
                   	  </table>
                  </td>   
			 	</form>     
	      	</tr>
	      	<tr class="alternate-row"> 
				<form id="centerWiseMonthWiseFatSnfDeduction" name="centerWiseMonthWiseFatSnfDeduction" method="post" action="<@ofbizUrl>centerWiseMonthWiseFatSnfDeduction.txt</@ofbizUrl>">
			    	<td> <span class='h3'>Month-Wise Center-Wise Procurement</span></td>
                    <td><input type="hidden" name="shedId" value=""/> 
                        <input type="hidden" name="unitId" value=""/>
                      <table>
                         	<tr>
                        		<td><span class='h3'>Center: </span><input  size="10pt" type="text" id="facilityCode" name="facilityCode"/></td>
                        		<td><span class='h3'>From: </span><input  size="10pt" type="text" id="centerWiseFatSnfDeductionFromDate" name="fromDate"/></td>
                         		<td><span class='h3'>To: </span><input size="10pt" type="text" id="centerWiseFatSnfDeductionThruDate" name="thruDate"/></td>
                        		<td><input type="submit" formtarget="_blank" value="Download" class="buttontext h1"/> </td>
                   			</tr>
                   	</table>
                  </td>   
			 	</form>     
	      	</tr>
	      	<tr>
	    		<form id="Unit-wiseProcurementComparing" name="Unit-wiseProcurementComparing" method="post" action="<@ofbizUrl>Unit-wiseProcurementComparing.txt</@ofbizUrl>">
	                        <td width="30%">Unit-wise Procurement Comparing</td>
	                    <td width="70%">
	                    Last year:
	                    <select name="lastYearId" class='h4'>
	                     <#if customTimePeriodList?has_content>	
	                        <#list customTimePeriodList as timePeriod>    
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
						Current year:
	                    <select name="currentYearId" class='h4'>
	                     <#if customTimePeriodList?has_content>	
	                        <#list customTimePeriodList as timePeriod>    
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
	                    <input type="hidden" name="shedId" value=""/>
	                    <input type="hidden" name="unitId" value=""/>
	      				<input type="submit" formtarget="_blank" value="Download" class="buttontext" />
	                </td>
	            </form>
	        </tr>
	         <tr class="alternate-row"> 
			<form id="UnitWiseComparingwithLastYear" name="UnitWiseComparingwithLastYear" method="post" action="<@ofbizUrl>UnitWiseComparingwithLastYear.txt</@ofbizUrl>">
		    	<td> <span class='h3'>Unit-wise Comparing with LastYear</span></td>
                <td><input type="hidden" name="shedId" value=""/> 
                    <input type="hidden" name="unitId" value=""/>
                  <table>
                     	<tr>
                    		<td><span class='h3'>From: </span><input  size="10pt" type="text" id="UnitWiseComparingwithLastYearFromDate" name="fromDate"/></td>
                     		<td><span class='h3'>To: </span><input size="10pt" type="text" id="UnitWiseComparingwithLastYearThruDate" name="thruDate"/></td>
                    		<td><input type="submit" formtarget="_blank" value="Download" class="buttontext h1"/></td>
               			</tr>
               	</table>
              </td>   
		 	</form>     
		</tr>
	     <tr class="alternate-row">
	      		<form id="MonthWiseUnitWiseDetailsList" name="MonthWiseUnitWiseDetailsList" method="post" action="<@ofbizUrl>MonthWiseUnitWiseDetailsList.txt</@ofbizUrl>">
	      			<td><span class="h3">MonthWise, UnitWise Details List</td>
	      			<td><input type="hidden" name="shedId" value=""/>
	      				<input type="hidden" name="unitId" value=""/>
	      				From<input type="text" size="20pt" id="MonthWiseUnitWiseDetailsListFromDate" name="fromDate"/>
	      				To  <input type="text" size="20pt" id="MonthWiseUnitWiseDetailsListThruDate" name="thruDate"/>
	      					<input type="submit" formtarger="_blank" id="button1" value="Download" class="buttontext">
	      			</td>
	      		</form>	      	
	      </tr> 
	      <tr class="alternate-row">
	      		<form id="CenterWiseBillsAbstract" name="CenterWiseBillsAbstract" method="post" action="<@ofbizUrl>CenterWiseBillsAbstract.txt</@ofbizUrl>">
	      			<td><span class="h3">Center_wise Abstract (Full details)</td>
	      			<td><input type="hidden" name="shedId" value=""/>
	      				<input type="hidden" name="unitId" value=""/>
	      				From<input type="text" size="20pt" id="CenterWiseBillsAbstractFromDate" name="fromDate"/>
	      				To  <input type="text" size="20pt" id="CenterWiseBillsAbstractThruDate" name="thruDate"/>
	      					<input type="submit" formtarget="_blank" id="button1" value="Download" class="buttontext">
	      			</td>
	      		</form>	      	
	      	</tr>
			<tr class="alternate-row">
	      		<form id="MonthWiseAverageRateStatement" name="MonthWiseAverageRateStatement" method="post" action="<@ofbizUrl>MonthWiseAverageRateStatement.txt</@ofbizUrl>">
	      		<td><span class="h3">Month-Wise Average Rate Statement</td>
	      		<td><input type="hidden" name="shedId" value=""/>
	      			<input type="hidden" name="unitId" value=""/>
	      			From<input type="text" size="20pt" id="MonthWiseAverageRateStatementFromDate" name="fromDate">
	      			To  <input type="text" size="20pt" id="MonthWiseAverageRateStatementThruDate" name="thruDate">
	      			    <input type="submit" formtarget="_blank" id="button1" value="Download" class="buttontext">
	      		</td>
	      		</form>
	      	</tr>	
	      	<tr class="alternate-row">
	      		<form id="UnitwiseCenterwiseSelectedlist" name="UnitwiseCenterwiseSelectedlist" method="post" action="<@ofbizUrl>UnitwiseCenterwiseSelectedlist.txt</@ofbizUrl>">
	      			<td><span class="h3">Unit-wise,Center-wise Selected list</td>
	      			<td><input type="hidden" name="shedId" value=""/>
	      				<input type="hidden" name="unitId" value=""/>
	      				<select name="fieldName" class='h4'>
		      				<#list finalList as final>    
	                  	    	<option value='${final}' >
		                    		${final}
		                  		</option>
	                		</#list>  
                		</select>
	      				From<input type="text" size="20pt" id="UnitwiseCenterwiseSelectedlistFromDate" name="fromDate"/>
	      				To  <input type="text" size="20pt" id="UnitwiseCenterwiseSelectedlistThruDate" name="thruDate"/>
	      					<input type="submit" formtarget="_blank" id="button1" value="Download" class="buttontext">
	      			</td>
	      		</form>	      	
	      	</tr>
	      	<tr class="alternate-row"> 
				<form id="UnitWiseLeanFlushSeason" name="UnitWiseLeanFlushSeason" method="post" action="<@ofbizUrl>UnitWiseLeanFlushSeason.txt</@ofbizUrl>">
   					<td>Unit-wise Lean Flush Season</td>
                   	<td width="70%">
                   	Period:
                   	<select name="firstYearId" class='h4'>
                    	<#if customTimePeriodList?has_content>	
                       		<#list customTimePeriodList as timePeriod>    
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
                   	<input type="hidden" name="shedId" value=""/>
     	 			<input type="submit" formtarget="_blank" value="Download" class="buttontext" />
               </td>
           </form>
       </tr>
	      	<tr class="alternate-row">
	      		<form id="LeanFlushSeasonYearWise" name="LeanFlushSeasonYearWise" method="post" action="<@ofbizUrl>LeanFlushSeasonYearWise.txt</@ofbizUrl>">
		      		<td><span class="h3">Lean-Flush Seanson(Year Wise)A/C</td>
		      		<td><input type="hidden" name="shedId" value=""/>
		      			<input type="hidden" name="unitId" value=""/>
		      			From<input type="text" size="20pt" id="LeanFlushSeasonYearWiseFromDate" name="fromDate">
		      			To  <input type="text" size="20pt" id="LeanFlushSeasonYearWiseThruDate" name="thruDate">
		      			    <input type="submit" formtarget="_blank" id="button1" value="Download" class="buttontext">
		      		</td>
	      		</form>   
	      	</tr>
	      	<tr class="alternate-row"> 
				<form id="shedWiseUserChargesReport" name="shedWiseUserChargesReport" method="post" action="<@ofbizUrl>shedWiseUserChargesReport.txt</@ofbizUrl>">
			    	<td>Sheds User Charges Report</td>
			   		<td>
			        	From<input  type="text" size="20pt" id="SheUserChargesFromDate" name="fromDate"/>
			        	To<input  type="text" size="20pt" id="SheUserChargesThruDate" name="thruDate"/>                           
			        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
			    	</td>   
			 	</form>     
	      	</tr>
	      </table>
	    </div>
	 </div>
	 </div>  
 </#if> 
  <#if reportFrequencyFlag ="RegionWiseReports">
	<div class="screenlet">
	    <div class="screenlet-title-bar">
	     	<h3>Region Wise Reports</h3> 
	    </div>
	    <div class="screenlet-body">
	      <table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">    	
	      	<tr class="alternate-row"> 
				<form id="ProcurementAbstract" name="ProcurementAbstract" method="post" action="<@ofbizUrl>ProcurementAbstract.txt</@ofbizUrl>">
			    	<td>Procurement Abstract</td>
			   		<td>Region<input type="hidden" name="shedId" value=""/> 
			   			<select name="facilityGroupId" class='h4'>
		      				<#list finalRegionList as final>    
	                  	    	<option value='${final}' >
		                    		${final}
		                  		</option>
	                		</#list>  
                		</select>
			        	From<input  type="text" size="20pt" id="ProcurementAbstractFromDate" name="fromDate"/>
			        	To<input  type="text" size="20pt" id="ProcurementAbstractThruDate" name="thruDate"/>                           
			        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
			    	</td>   
			 	</form>     
	      	</tr>
	      	<tr class="alternate-row"> 
				<form id="ProcurementPayment" name="ProcurementPayment" method="post" action="<@ofbizUrl>ProcurementPayment.txt</@ofbizUrl>">
			    	<td>Procurement Payment</td>
			   		<td>Region<input type="hidden" name="shedId" value=""/>
			   			<select name="facilityGroupId" class='h4'>
		      				<#list finalRegionList as final>    
	                  	    	<option value='${final}' >
		                    		${final}
		                  		</option>
	                		</#list>  
                		</select> 
			        	From<input  type="text" size="20pt" id="ProcurementPaymentFromDate" name="fromDate"/>
			        	To<input  type="text" size="20pt" id="ProcurementPaymentThruDate" name="thruDate"/>                           
			        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
			    	</td>   
			 	</form>     
	      	</tr>
	      	<tr class="alternate-row"> 
				<form id="RegionWiseThreeYearsComparision" name="RegionWiseThreeYearsComparision" method="post" action="<@ofbizUrl>RegionWiseThreeYearsComparision.txt</@ofbizUrl>">
			    	<td>Region Wise Three Years Comparision</td>
			   		<td>Region<input type="hidden" name="shedId" value=""/> 
			   			<select name="facilityGroupId" class='h4'>
		      				<#list finalRegionList as final>    
	                  	    	<option value='${final}' >
		                    		${final}
		                  		</option>
	                		</#list>  
                		</select>
			        	Select Date<input  type="text" size="20pt" id="RegionWiseThreeYearsComparisionFromDate" name="fromDate"/>
			        	<input type="submit" formtarget="_blank" value="Download" id="button1" class="buttontext"/>
			    	</td>   
			 	</form>     
	      	</tr>
	        </table>
	    </div>
	 </div>
 </#if> 
 <script type="text/javascript">
		<#if !unitId?has_content>
		<#if shedId?has_content>
			<#if !(unitMapsList?has_content)>
				setShedUnitsDropDown(shedId);
			</#if>
			getShedFinAccounts(shedId);
			
				getUnitRouteByValue($('[name=unitId]').val());
			
			getTimePeriodsByUnit($('[name=shedId]').val(),$('[name=unitId]').val());
			$('[name=shedId]').val($('[name=shedId]').val());
			$('[name=unitId]').val($('[name=unitId]').val());
			
		</#if>
	<#else>
		<#if unitId?has_content>
			getUnitRouteByValue(${unitId});
			getTimePeriodsByUnit($('[name=shedId]').val(),${unitId});
			$('[name=shedId]').val($('[name=shedId]').val());
			$('[name=unitId]').val($('[name=unitId]').val());
		</#if>
	</#if>
	</script>