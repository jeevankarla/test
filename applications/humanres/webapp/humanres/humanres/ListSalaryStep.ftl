<div class="screenlet">
    <div class="screenlet-title-bar">
      	<ul>
        	<li class="h3">Search Results </li>
      	</ul>
      	<br class="clear"/>
    </div>
    <div class="screenlet-body">
	    <#if salaryStepList?has_content>
	      	<table class="basic-table" cellspacing="0">
	        	<tr class="header-row">
	          		<td width = "20%">salaryStepSeqId</td>
	          		<td width = "20%">payGradeId</td>
	          		<td width = "20%">amount</td>
	          		<td width = "20%">&nbsp;</td>
	        	</tr>
	        </table>
	        <#assign rowCount = 0>
	    	<#list salaryStepList as salaryStepDetails>
	    		<form name="updateHoliday${salaryStepDetails.salaryStepSeqId}" method="post" action="<@ofbizUrl>updateSalaryStep</@ofbizUrl>">
	                <table class="basic-table" cellspacing="0">
	                    <input type="hidden" name="flag" value="N" />
	                    <tr>
	                        <td width = "20%"><input type="display" readOnly size="10" id = "salaryStepSeqId" name="salaryStepSeqId" value="${salaryStepDetails.salaryStepSeqId?if_exists}"/></td>
	          				<td width = "20%"><input type="display" readOnly size="10" id = "payGradeId" name="payGradeId" value="${salaryStepDetails.payGradeId?if_exists}"/></td>
	          				<td width = "20%"><input type="text" name="amount" id = "amount" value="${(salaryStepDetails.amount)?if_exists}"/></td>
	                        <td nowrap="nowrap" width="20%">
	                            <input class="smallSubmit" type="submit" value="${uiLabelMap.CommonUpdate}"/>
	                            <a href="javascript:document.deleteSalaryStep_o_${rowCount}.submit();" class="buttontext">${uiLabelMap.CommonDelete}</a>
	                        </td>
	                    </tr>
	                </table>
	            </form>
	            <#assign rowCount = rowCount + 1>
	    	</#list>
	    	<#assign rowCount = 0>
	    	<#list salaryStepList as salaryStepDetails>
	    		<form name="deleteSalaryStep_o_${rowCount}" method="post" action="<@ofbizUrl>deleteSalaryStep</@ofbizUrl>">
	                <input type="hidden" name="salaryStepSeqId" value="${(salaryStepDetails.salaryStepSeqId)?if_exists}" />
	    			<input type="hidden" name="payGradeId" value="${(salaryStepDetails.payGradeId)?if_exists}" />
	    			<input type="hidden" name="amount" value="${(salaryStepDetails.amount)?if_exists}" />
	            </form>
	            <#assign rowCount = rowCount + 1>
	    	</#list>
	    <#else>
	      	<div class="screenlet-body">${uiLabelMap.AccountingNoChildPeriodsFound}</div>
	    </#if>
	</div>
</div>


