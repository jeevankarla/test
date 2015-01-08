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
<#escape x as x?xml>
<#assign partyBenefitList = finalMap.entrySet()>
<#list partyBenefitList as partyBenefit>
	<#assign partyId=partyBenefit.getKey()>
	<fo:table table-layout="fixed" width="100%" space-before="0.2in">  
		<fo:table-header height="14px">
			<fo:table-row border-bottom-style="solid" border-bottom-width="thin" border-bottom-color="black">
				<fo:table-cell text-align="right" number-columns-spanned="2">
					<fo:block font-weight="bold" text-align="right">${uiLabelMap.HumanResPayGradeName}</fo:block>
				</fo:table-cell>              
				<fo:table-cell text-align="right" number-columns-spanned="2">
					<fo:block font-weight="bold" text-align="right">${uiLabelMap.CommonAmount}</fo:block>
				</fo:table-cell>
			 	<fo:table-cell text-align="right" number-columns-spanned="2">
					<fo:block font-weight="bold" text-align="right">${uiLabelMap.employeeId}:${partyId}</fo:block>
				 </fo:table-cell>
			</fo:table-row>
		</fo:table-header>
        <fo:table-body font-size="10pt">
        <#assign grossSalary=0>
        <#assign totalDeductions=0>
        <#-- if the Party benefits -->
        <#assign payrollDetails=partyBenefit.getValue()>
        	<#list payrollDetails as payrollDet>
          		<#assign payrollDetai=payrollDet.get("partyBenefitList")>
          		<#list payrollDetai as payrollDet>
           			<#assign amount = payrollDet.get("amount")>
            		<#if amount?has_content>
            			<#assign grossSalary = (grossSalary + amount)>
             		</#if>               
                	<fo:table-row height="14px" space-start=".15in">
                   		 <fo:table-cell number-columns-spanned="2">
                       		 <fo:block text-align="left">${payrollDet.get("payGradeName")}</fo:block>
                    	 </fo:table-cell>
                   		 <fo:table-cell text-align="right" number-columns-spanned="2">
                        	<fo:block><@ofbizCurrency amount=amount isoCode=defaultOrganizationPartyCurrencyUomId/></fo:block>
                         </fo:table-cell>
                    </fo:table-row>           
         		</#list>
         		 <#-- blank line -->
			        <fo:table-row height="7px">
			            <fo:table-cell number-columns-spanned="5"><fo:block><#-- blank line --></fo:block></fo:table-cell>
			            <fo:table-cell number-columns-spanned="5"><fo:block><#-- blank line --></fo:block></fo:table-cell>            
			        </fo:table-row>
         		<#-- the Salary grossTotal total -->
		        <fo:table-row>
		           <fo:table-cell number-columns-spanned="2">
		              <fo:block font-weight="bold">${uiLabelMap.HumanResGrossSalary}</fo:block>
		           </fo:table-cell>
		           <fo:table-cell text-align="right" border-top-style="solid" border-top-width="thin" border-top-color="black" number-columns-spanned="2">
		              <fo:block text-align="right"><@ofbizCurrency amount=grossSalary isoCode=defaultOrganizationPartyCurrencyUomId/></fo:block>
		           </fo:table-cell>
		        </fo:table-row>
         		<#assign payrollDetai=payrollDet.get("partyDeductionList")>
          		<#list payrollDetai as payrollDet>
           			<#assign amount = payrollDet.get("amount")>
            		<#if amount?has_content>
            			<#assign totalDeductions = (totalDeductions + amount)>
             		</#if>               
                	<fo:table-row height="14px" space-start=".15in">
                   		 <fo:table-cell number-columns-spanned="2">
                       		 <fo:block text-align="left">${payrollDet.get("payGradeName")}</fo:block>
                    	 </fo:table-cell>
                   		 <fo:table-cell text-align="right" number-columns-spanned="2">
                        	<fo:block><@ofbizCurrency amount=amount isoCode=defaultOrganizationPartyCurrencyUomId/></fo:block>
                         </fo:table-cell>
                    </fo:table-row>           
         		</#list>
         		<fo:table-row height="14px">           
			       <fo:table-cell number-columns-spanned="2">
			          <fo:block text-align="left">${uiLabelMap.HumanResDeductionAmount}</fo:block>
			       </fo:table-cell>
			       <fo:table-cell text-align="right" border-top-style="solid" border-top-width="thin" border-top-color="black" number-columns-spanned="2">
			          <fo:block text-align="right">
			             <@ofbizCurrency amount=totalDeductions isoCode=defaultOrganizationPartyCurrencyUomId/>
			          </fo:block>
			       </fo:table-cell>
			    </fo:table-row>
			    <fo:table-row height="14px">           
		           <fo:table-cell number-columns-spanned="2">
		              <fo:block text-align="left" font-weight="bold">${uiLabelMap.HumanResNetSalary}</fo:block>
		           </fo:table-cell>
		           <fo:table-cell text-align="right" border-top-style="solid" border-top-width="thin" border-top-color="black" number-columns-spanned="2" font-weight="bold">
		              <fo:block text-align="right">
		                 <@ofbizCurrency amount=(grossSalary+totalDeductions) isoCode=defaultOrganizationPartyCurrencyUomId/>
		              </fo:block>
		           </fo:table-cell>
		        </fo:table-row>
         	</#list>
   		 </fo:table-body>
 	</fo:table>
  </#list>
</#escape>
