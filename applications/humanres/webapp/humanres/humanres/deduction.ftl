<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<script type="text/javascript">
	
	function togglePayrollHeaderId(master) {
        var deductions = jQuery("#listDeduction :checkbox[name='payrollHeaderIds']");
        jQuery.each(deductions, function() {
            this.checked = master.checked;
        });
    }
    
    function makeMassDeduction(current){
    	jQuery(current).attr( "disabled", "disabled");
    	var deductions = jQuery("#listDeduction :checkbox[name='payrollHeaderIds']");
        var index = 0;
        var comment;
        var employeeId;
        
        jQuery.each(deductions, function() {
            if (jQuery(this).is(':checked')) {
            
            	var domObj = $(this).parent().parent();
            	
            	var employeeIdObj = $(domObj).find("#employeeId");
            	var commentObj = $(domObj).find("#comment");
            	var payrollHeaderId = $(this).val();
            	
            	employeeId = $(employeeIdObj).val();
            	comment = $(commentObj).val();
            	
            	var appendStr = "";
            	appendStr += "<input type=hidden name=payrollHeaderId_o_"+index+" value="+payrollHeaderId+" />";
            	appendStr += "<input type=hidden name=employeeId_o_"+index+" value="+employeeId+" />";
            	appendStr += "<input type=hidden name=comment_o_"+index+" value="+comment+" />";
            	
                $("#deductionSubmitForm").append(appendStr);
                index = index+1;
            }
        });
    	jQuery('#deductionSubmitForm').submit();
    }
       
</script>
<set field="screenFlag" value=""/>
<#if flag == "MakePayment">
	<form name="deductionSubmitForm" id="deductionSubmitForm" method="post" action="makepaymentForSalaryDisbursedEmpl">
<#else>
	<form name="deductionSubmitForm" id="deductionSubmitForm" method="post" action="updateExcludeSalaryDisbursement">
</#if>
</form>
<#if deductionList?has_content>
	<#if flag == "MakePayment">
		<form name="listDeduction" id="listDeduction"  method="post" action="makepaymentForSalaryDisbursedEmpl">
	<#else>
		<form name="listDeduction" id="listDeduction"  method="post" action="updateExcludeSalaryDisbursement">
	</#if>
    <div align="right">
      <input id="submitButton" type="submit"  onclick="javascript:makeMassDeduction(this);" value="Submit"/>
    </div>
    <table class="basic-table hover-bar" cellspacing="0">
      <thead>
        <tr class="header-row-2">
          <td>PayrollHeaderId</td>
          <td>EmployeeId</td>
          <td>Name</td>
          <td>Comments</td>
  		  <td align="right">${uiLabelMap.CommonSelect} <input type="checkbox" id="checkAllPayrollHeaderIds" name="checkAllPayrollHeaderIds" onchange="javascript:togglePayrollHeaderId(this);"/></td>
        </tr>
      </thead>
      <tbody>
        <#assign alt_row = false>
        <#assign eachCrate_index = 0>
        <#list deductionList as eachDeduct>
        	<#if flag == "MakePayment">
        		<#if eachDeduct.get("colorFlag") == "Y">
		            <tr valign="middle" style='background-color: #66CCCC' <#if alt_row> class="alternate-row" font="red" </#if>>
		              <input type="hidden" name="employeeId" id="employeeId" value="${eachDeduct.employeeId?if_exists}">
		              <td>${eachDeduct.get("payrollHeaderId")?if_exists}</td>
		              <td name="employeeId" id="employeeId">${eachDeduct.get("employeeId")?if_exists}</td>
		              <td>${eachDeduct.get("name")?if_exists}</td>
		              <td><input type="text" size = "30" name="comment" id="comment" value = "${(eachDeduct.comment)?if_exists}"></td>
		              <td><input type="checkbox" id="payrollHeaderId_${eachCrate_index}" name="payrollHeaderIds" value="${eachDeduct.payrollHeaderId}"/></td>
		            </tr>
		       	</#if>
        	<#else>
	        	<#if eachDeduct.get("colorFlag") == "Y">
		            <tr valign="middle" style='background-color: #66CCCC' <#if alt_row> class="alternate-row" font="red" </#if>>
		              <input type="hidden" name="employeeId" id="employeeId" value="${eachDeduct.employeeId?if_exists}">
		              <td>${eachDeduct.get("payrollHeaderId")?if_exists}</td>
		              <td name="employeeId" id="employeeId">${eachDeduct.get("employeeId")?if_exists}</td>
		              <td>${eachDeduct.get("name")?if_exists}</td>
		              <td><input type="text" size = "30" name="comment" id="comment" value = "${(eachDeduct.comment)?if_exists}"></td>
		              <td><input type="checkbox" id="payrollHeaderId_${eachCrate_index}" name="payrollHeaderIds" value="${eachDeduct.payrollHeaderId}"/></td>
		            </tr>
	            <#else>
		            <tr valign="middle"<#if alt_row> class="alternate-row" color="red"</#if>>
		              <input type="hidden" name="employeeId" id="employeeId" value="${eachDeduct.employeeId?if_exists}">
		              <td>${eachDeduct.get("payrollHeaderId")?if_exists}</td>
		              <td name="employeeId" id="employeeId">${eachDeduct.get("employeeId")?if_exists}</td>
		              <td>${eachDeduct.get("name")?if_exists}</td>
		              <td><input type="text" size = "30" name="comment" id="comment" value = "${(eachDeduct.comment)?if_exists}"></td>               
		              <td><input type="checkbox" id="payrollHeaderId_${eachCrate_index}" name="payrollHeaderIds" value="${eachDeduct.payrollHeaderId}"/></td>
		            </tr>
	            </#if>
	       	</#if>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        </#list>
      </tbody>
    </table>
  </form>
<#else>
  <h3>No Records Found</h3>
</#if>
