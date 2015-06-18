
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.js</@ofbizContentUrl>"></script>
<link type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.css</@ofbizContentUrl>" rel="Stylesheet" />


<#--
<link href="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.css</@ofbizContentUrl>" rel="stylesheet">
<script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.js</@ofbizContentUrl>"></script>
-->
<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/flexselect.css</@ofbizContentUrl>">
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/liquidmetal.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/jquery.flexselect.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.js</@ofbizContentUrl>"></script>
<link type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.css</@ofbizContentUrl>" rel="Stylesheet" />

<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />
<link href="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.css</@ofbizContentUrl>" rel="stylesheet">
<script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.js</@ofbizContentUrl>"></script>



<script type="application/javascript">
		
	<#--		
	function populatePayrollEmployees() {
		
		var customTimePeriodId=$('#customTimePeriodId').val();	
		var paramName = 'parties';
		
		$.ajax({
				 type: "POST",
	             url: 'populatePayrollEmployeesAjax',
	             data: {customTimePeriodId : customTimePeriodId},
	             dataType: 'json',
		            
				 success:function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
	                    alert('Error in fetching stream and degree');
					}else{
						
						payrollEmployeeList =result["payrollEmployeeList"];
						var payrollEmployee=[];
       	  				 
       	  				if(payrollEmployeeList != undefined && payrollEmployeeList != ""){
							$.each(payrollEmployeeList, function(key, item){
								payrollEmployee.push('<option value="'+item.partyId+'">' +item.employeeName+'</option>');
							});
				        }
				        $('#parties').html(payrollEmployee.join(''));   
				        $("#parties").multiselect({
					   		minWidth : 280,
					   		height: 200,
					   		selectedList: 100,
					   		show: ["bounce", 100],
					   		position: {
					      		my: 'left bottom',
					      		at: 'left top'
					      	}
					   	});
					   	$("#parties").multiselect("refresh");
						
					}								 
				},
				error: function(){
					alert("record not found");
				}							
			});
	}-->
	
</script>

   <form id="ExcludeSalary" name="ExcludeSalary" method="post" action="<@ofbizUrl>ExcludeSalaryDisbursementInit</@ofbizUrl>">
           <fieldset>
			  <table cellpadding="5" cellspacing="15">
			  	
			  	<tr>
			  		<td class="label"><FONT COLOR="#045FB4"><b>Select CustomTime PeriodId</b></FONT></td>
			  		<td>
		      			<select name="customTimePeriodId" id="customTimePeriodId">
		      				<option value=''>Select</option>
					        <#list customTimeList as eachCustomTime>
					            <option value='${eachCustomTime.customTimePeriodId}'>${eachCustomTime.get("periodName")}</option>
					        </#list>
	      				</select>
		    		</td>			  	
			  	</tr>
			    <#--<tr>
		      		<td align='left' valign='left' nowrap="nowrap" class="label"><div class='h4'><FONT COLOR="#045FB4">Employees</FONT></div></td>
		      		<td align='left' valign='left'>
		      		<#if parties?has_content>
		      			<div class='tabletext h3'>${parties?if_exists}</div>
		      		<#else>
		      			<select id="parties" name="parties" class='h4' multiple="multiple" >
						</select>
		      		</#if>
					</td>
				</tr>-->
				<tr>
			      <td></td>
			      <td><input type="submit" class="smallSubmit" value="Submit"/></td>
			    </tr>
				
      		</table>
	  	</fieldset>
	  	
	  	
 </form>