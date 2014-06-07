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


  <#if salaryDetailsList?has_content>
    <table class="basic-table hover-bar" cellspacing="0">
      <tr class="header-row">
        <td>${uiLabelMap.HumanResPayGradeName}</td>
        <td>${uiLabelMap.HumanResPayrollPreferenceFlatAmount}</td>                
      </tr>
      <#assign grossSalary=0>
      <#assign totalDeductions=0> 
      <#list salaryDetailsList as salaryDetail>
        <#assign partyBenefitList = salaryDetail.partyBenefitList>
        <#if partyBenefitList?has_content>
          <tr class="header-row"><th colspan="4"><hr/></th></tr>
          <#assign alt_row = false>
          <#list partyBenefitList as partyBenefit>           
            <tr<#if alt_row> class="alternate-row"</#if>>
              <td>${partyBenefit.payGradeName}</td>
              <#if partyBenefit.amount?has_content>
              <td><@ofbizCurrency amount= partyBenefit.amount isoCode=defaultOrganizationPartyCurrencyUomId/></td> 
              <#assign grossSalary = (grossSalary + partyBenefit.amount?if_exists)>    
              </#if>         
            </tr>
            <#assign alt_row = !alt_row>
          </#list>
        </#if>
        <tr<#if alt_row> class="alternate-row"</#if>>
            <td>Gross Salary</td>
            <td><@ofbizCurrency amount=grossSalary isoCode=defaultOrganizationPartyCurrencyUomId/></td>              
        </tr>
        <#assign partyDeductionList = salaryDetail.partyDeductionList>
        <#if partyDeductionList?has_content>
          <tr class="header-row"><th colspan="4"><hr/> Deductions</th></tr>
          <#assign alt_row = false>
          <#list partyDeductionList as partyDeduction>           
            <tr<#if alt_row> class="alternate-row"</#if>>
              <td>${partyDeduction.payGradeName}</td>
              <#if partyDeduction.amount?has_content>
              <td><@ofbizCurrency amount=partyDeduction.amount isoCode=defaultOrganizationPartyCurrencyUomId/></td> 
              <#assign totalDeductions = (totalDeductions + partyDeduction.amount?if_exists)>  
               </#if>          
            </tr>
            <#assign alt_row = !alt_row>
          </#list>
        </#if>
        <tr<#if alt_row> class="alternate-row"</#if>>
            <td>Total Deductions</td>
            <td><@ofbizCurrency amount=totalDeductions isoCode=defaultOrganizationPartyCurrencyUomId/></td>              
        </tr>
        <tr<#if alt_row> class="alternate-row"</#if>>
            <td>Net Salary</td>
            <td><@ofbizCurrency amount=(grossSalary+totalDeductions) isoCode=defaultOrganizationPartyCurrencyUomId/></td>              
        </tr>
      </#list>
    </table>
  <#else>
    <div class="screenlet-body">${uiLabelMap.NoRecordsFound}.</div>
  </#if>