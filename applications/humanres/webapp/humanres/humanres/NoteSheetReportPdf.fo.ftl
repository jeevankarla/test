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
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
      <fo:simple-page-master master-name="main" page-height="11.69in" page-width="8.27in"
        margin-top="0.3in" margin-bottom="0.3in" margin-left=".5in" margin-right=".5in">
          <fo:region-body margin-top="1.2in"/>
          <fo:region-before extent="1in"/>
          <fo:region-after extent="1in"/>
      </fo:simple-page-master>
    </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "NoteSheet.pdf")}
        <#if errorMessage?has_content>
		
	<#else>
       <#if finalRegionPaySheetMap?has_content>
	        <fo:page-sequence master-reference="main" font-size="12pt">	
	        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
	        		<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
                    <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
                    <#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
				    <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
				    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >${reportHeader.description?if_exists} </fo:block>
				    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">${reportSubHeader.description?if_exists}                             </fo:block>
				    <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold" text-indent="50pt">
        			<#if parameters.billingTypeId=="SP_LEAVE_ENCASH">   
        			<#assign timePeriodEnd=basicSalDate?if_exists>
					</#if>                                                  																					<#-->Page No: <fo:page-number/>--></fo:block>	
        			  
                	
          		<#-->	<fo:block text-align="center" keep-together="always"  white-space-collapse="false" font-family="Arial" font-size = "10pt"> From ${fromDate} - ${thruDate} </fo:block> -->

            	</fo:static-content>	        	
	        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
	        	<fo:block text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold" font-size = "12pt" font-family="Arial">NOTE SHEET for the month: ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodEnd, "MMMMM-yyyy")).toUpperCase()}</fo:block>
	        	<fo:table font-family="Arial">
		                    <fo:table-column column-width="5%"/>
		                    <fo:table-column column-width="20%"/>
		                    <fo:table-column column-width="10%"/>
		                    <fo:table-column column-width="14%"/>
		                    <fo:table-column column-width="10%"/>
		                    <fo:table-column column-width="10%"/>
		                    <fo:table-column column-width="14%"/>
		                    <fo:table-column column-width="14%"/>
		                    
		                    <fo:table-header>
								<fo:table-row>
					                    <fo:table-cell border-style="solid">
							            	<fo:block  text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Sl.No</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell border-style="solid">
							            	<fo:block  text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Name of Office</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block  text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">No. of Employees</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell border-style="solid">
							            	<fo:block  text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Gross Salary</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">CPF</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">FPF</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Employer's Contribution to CPF</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Gross Total</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
							</fo:table-header>
		                    <fo:table-body>
		        <#assign slNo = 1>         
		        <#assign regionPaySheetMapKeys = finalRegionPaySheetMap.keySet()>
		        <#assign benefitTotal = 0>
		        <#assign empCpfTotal = 0>
		        <#assign totalOfGrossTotal = 0>
		        <#assign cpfTotal = 0>
		        <#assign fpfTotal = 0>
		        <#assign totalEmp = 0>
		        <#list regionPaySheetMapKeys as region>
		        <#assign regionPaySheetEntry = finalRegionPaySheetMap.get(region)>
				 	<fo:table-row>
	                    <fo:table-cell border-style="solid">
			            	<fo:block  text-align="left" font-size="8pt" white-space-collapse="false">${slNo}</fo:block>  
			            </fo:table-cell>
			             <fo:table-cell border-style="solid">
			            	<fo:block  text-align="left" font-size="8pt" white-space-collapse="false">${region?if_exists}</fo:block>  
			            </fo:table-cell>
			            <fo:table-cell border-style="solid">
			            	<fo:block  text-align="right" font-size="8pt" white-space-collapse="false">${regionPaySheetEntry.get("totalEmpPerRegion")}</fo:block>  
			            </fo:table-cell>
			            <#assign totalEmp = totalEmp + regionPaySheetEntry.get("totalEmpPerRegion")>
			            
			             <fo:table-cell border-style="solid">
			            	<fo:block  text-align="right" font-size="8pt" white-space-collapse="false">${Static["java.lang.Math"].round(regionPaySheetEntry.get("totalBenifit"))}</fo:block>  
			            </fo:table-cell>
			            <#assign benefitTotal = benefitTotal + regionPaySheetEntry.get("totalBenifit")>
			            
			            <#assign cpf = regionPaySheetEntry.get("empCpf") - regionPaySheetEntry.get("empFpf")>
			            <#assign cpfTotal = cpfTotal + cpf>
			            <fo:table-cell border-style="solid">
			            	<fo:block text-align="right" font-size="8pt" white-space-collapse="false">${Static["java.lang.Math"].round(cpf)}</fo:block>  
			            </fo:table-cell>
			            <fo:table-cell border-style="solid">
			            	<fo:block text-align="right" font-size="8pt" white-space-collapse="false">${Static["java.lang.Math"].round(regionPaySheetEntry.get("empFpf"))}</fo:block>  
			            </fo:table-cell>
			            <#assign fpfTotal = fpfTotal + regionPaySheetEntry.get("empFpf")>
			            <fo:table-cell border-style="solid">
			            	<fo:block text-align="right" font-size="8pt" white-space-collapse="false">${Static["java.lang.Math"].round(regionPaySheetEntry.get("empCpf"))}</fo:block>  
			            </fo:table-cell>
			            <#assign empCpfTotal = empCpfTotal + regionPaySheetEntry.get("empCpf")>
			            <fo:table-cell border-style="solid">
			            	<fo:block text-align="right" font-size="8pt" white-space-collapse="false">${Static["java.lang.Math"].round(regionPaySheetEntry.get("grossTotal"))}</fo:block>  
			            </fo:table-cell>
			            <#assign totalOfGrossTotal = totalOfGrossTotal + regionPaySheetEntry.get("grossTotal")>
				     </fo:table-row>
				     <#assign slNo = slNo + 1>
		         </#list>            
		       		<fo:table-row>
		       			<fo:table-cell border-style="solid">
			            	<fo:block  text-align="left" font-size="8pt" white-space-collapse="false" font-weight="bold">TOTAL</fo:block>  
			            </fo:table-cell>
			            <fo:table-cell border-style="solid">
			            	<fo:block  text-align="left" font-size="8pt" white-space-collapse="false"></fo:block>  
			            </fo:table-cell>
			            <fo:table-cell border-style="solid">
			            	<fo:block  text-align="right" font-size="8pt" white-space-collapse="false" font-weight="bold">${totalEmp}</fo:block>  
			            </fo:table-cell>
			            <fo:table-cell border-style="solid">
			            	<fo:block  text-align="right" font-size="8pt" white-space-collapse="false" font-weight="bold">${Static["java.lang.Math"].round(benefitTotal)}</fo:block>  
			            </fo:table-cell>
			            
			            <fo:table-cell border-style="solid">
			            	<fo:block  text-align="right" font-size="8pt" white-space-collapse="false" font-weight="bold">${Static["java.lang.Math"].round(cpfTotal)}</fo:block>  
			            </fo:table-cell>
			            <fo:table-cell border-style="solid">
			            	<fo:block  text-align="right" font-size="8pt" white-space-collapse="false" font-weight="bold">${Static["java.lang.Math"].round(fpfTotal)}</fo:block>  
			            </fo:table-cell>
			            
			            <fo:table-cell border-style="solid">
			            	<fo:block  text-align="right" font-size="8pt" white-space-collapse="false" font-weight="bold">${Static["java.lang.Math"].round(empCpfTotal)}</fo:block>  
			            </fo:table-cell>
			            <fo:table-cell border-style="solid">
			            	<fo:block  text-align="right" font-size="8pt" white-space-collapse="false" font-weight="bold">${Static["java.lang.Math"].round(totalOfGrossTotal)}</fo:block>  
			            </fo:table-cell>
		       		</fo:table-row>
			</fo:table-body>
			</fo:table>
			
			<fo:block page-break-inside="avoid">
			
			<fo:block text-align="center" white-space-collapse="false" font-weight="bold" font-size="12pt">&#160; SUMMARY OF EARNINGS AND DEDUCTIONS FOR THE MONTH OF :  ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodEnd, "MMMMM-yyyy")).toUpperCase()}</fo:block>
        		<#--><fo:block white-space-collapse="false" keep-together="always" font-size="11pt">&#160;${uiLabelMap.CommonPage}No: <fo:page-number/>                                                                                                                                     Date: ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd MMM, yyyy"))?upper_case}</fo:block>-->
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
        		<#if (parameters.netPayglCode?exists && parameters.netPayglCode == "Yes")>	 	 	  
        			<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-weight="bold">Gl Code   Earnings                                         Amount      |  Gl Code    Deductions                                          Amount</fo:block>
        		<#elseif (parameters.netPayglCode?exists && parameters.netPayglCode == "No")>
        			<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;    Earnings                                         Amount               		Deductions                                          Amount</fo:block>
        		<#else>
        			<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-weight="bold">Earnings              Amount       Deductions            Amount</fo:block>	
        		</#if>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			<fo:block>
           			<#assign summaryReportList =payRollSummaryMap.entrySet()>
           			<#assign totalEarnings =0 />
        			<#assign totalDeductions =0 />
           			<fo:table>
       					 <fo:table-column column-width="30pt"/>
       					 <fo:table-column column-width="220pt"/>
       					 <fo:table-column column-width="50pt"/>
       					 <fo:table-column column-width="220pt"/>
       					 <fo:table-body>
       					 	<fo:table-row>
       					 		<fo:table-cell>
       					 			<fo:block>
       					 				<fo:table>
       					 					<fo:table-column column-width="120pt"/>
       					 					<#--><fo:table-column column-width="23pt"/>-->
       					 					<fo:table-column column-width="100pt"/>
       					 					<fo:table-column column-width="140pt"/>
       					 					<fo:table-body>       					 						
       					 						<#list benefitTypeIds as benefitType>
						                    		<#assign value=0>
						                    		<#if payRollSummaryMap.get(benefitType)?has_content>
						                    			<#assign value=payRollSummaryMap.get(benefitType)>	
						                    		</#if>
						                    		<#if value !=0>
							                    		<fo:table-row>                     
								                    		<#--><fo:table-cell >  
								                    		<#if (parameters.netPayglCode?exists && parameters.netPayglCode == "Yes")>                  		
								                      			<#assign extrnalGlCodeGroup = delegator.findOne("BenefitType", {"benefitTypeId" :benefitType}, true)> 
								                      			<#if extrnalGlCodeGroup.externalGlCode?exists >               			
								                    			<fo:block text-align="left" keep-together="always">${extrnalGlCodeGroup.externalGlCode?if_exists}</fo:block>
								                    			<#else>
								                    			<#assign extrnalGlCodeGroup = delegator.findOne("InvoiceItemType", {"invoiceItemTypeId" :benefitType}, true)>
								                    			<fo:block text-align="left" keep-together="always">${extrnalGlCodeGroup.defaultGlAccountId?if_exists}</fo:block>
								                    			</#if> 
								                    		<#else>	 
								                    		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
								                    		</#if>	  
								                    		</fo:table-cell>-->
								                    		<#--><fo:table-cell > 
								                    		<fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell >-->
								                    		 <fo:table-cell>
								                    		 <#assign totalEarnings=(totalEarnings+(value))> 
								                    		 <fo:block keep-together="always" font-size="10pt">${benefitDescMap[benefitType]?if_exists}</fo:block></fo:table-cell>               	              		
								                    		<fo:table-cell><fo:block text-align="right" font-size="10pt">${value?if_exists?string("#0")}&#160;&#160;</fo:block></fo:table-cell>
								                    	</fo:table-row>
							                    	</#if>
						                    	</#list>
       					 					</fo:table-body>
       					 				</fo:table>
       					 			</fo:block>
       					 		</fo:table-cell>
       					 		<fo:table-cell></fo:table-cell>
       					 		<fo:table-cell>
       					 			<fo:block>
       					 				<fo:table>
       					 					<fo:table-column column-width="120pt"/>
       					 					<#--><fo:table-column column-width="30pt"/>-->
       					 					<fo:table-column column-width="100pt"/>
       					 					<fo:table-column column-width="160pt"/>
       					 					<fo:table-body>       					 						
       					 						<#list dedTypeIds as deductionType>
						                    		<#assign dedValue=0>
						                    		<#if payRollSummaryMap.get(deductionType)?has_content>
						                    			<#assign dedValue=payRollSummaryMap.get(deductionType)>	
						                    		</#if>
						                    		<#if dedValue !=0>
							                    		<fo:table-row>         
								                    		<#--><fo:table-cell> 
								                    		<#if (parameters.netPayglCode?exists && parameters.netPayglCode == "Yes")>  
								                    			<#assign extrnalGlCodeGroup = delegator.findOne("DeductionType", {"deductionTypeId" :deductionType}, true)>                 		
								                      			<#if extrnalGlCodeGroup.externalGlCode?exists >  							                      			               			
								                    			<fo:block keep-together="always">|&#160;&#160;${extrnalGlCodeGroup.externalGlCode?if_exists}</fo:block>  
								                    			<#else>        
								                    			<#assign extrnalGlCodeGroup = delegator.findOne("InvoiceItemType", {"invoiceItemTypeId" :deductionType}, true)>
								                    			<fo:block keep-together="always">|&#160;&#160;${extrnalGlCodeGroup.defaultGlAccountId?if_exists}</fo:block>
								                    			</#if> 
								                    		<#else>
								                    		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
								                    		</#if>	     			
								                    		</fo:table-cell>-->
								                    		<#--><fo:table-cell > 
								                    		<fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell >-->
								                    		 <fo:table-cell>
								                    		 <#assign totalDeductions=(totalDeductions+(dedValue))>
								                    		 <fo:block keep-together="always" font-size="10pt">${dedDescMap[deductionType]?if_exists}</fo:block></fo:table-cell>                	              		
								                    		<fo:table-cell><fo:block text-align="right" font-size="10pt">${((-1)*(dedValue))?if_exists?string("#0")}&#160;&#160;&#160;&#160;</fo:block></fo:table-cell>
								                    	</fo:table-row>
							                    	</#if>
						                    	</#list>
       					 					</fo:table-body>
       					 				</fo:table>
       					 			</fo:block>
       					 		</fo:table-cell>
       					 	</fo:table-row>
       					 	<fo:table-row>
       					 		<fo:table-cell>
       					 			<fo:block>-------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
       					 		</fo:table-cell>
       					 	</fo:table-row>
       					 	<fo:table-row font-weight="bold">
       					 		<fo:table-cell><fo:block>Total</fo:block></fo:table-cell>       					 		
       					 		<fo:table-cell><fo:block text-align="right"><#if totalEarnings?has_content>
                   					<#assign total = totalEarnings?if_exists />
                   			<@ofbizCurrency amount=total?string("#0") /></#if></fo:block></fo:table-cell>  
       					 		<fo:table-cell><fo:block></fo:block></fo:table-cell>
       					 		<fo:table-cell><fo:block text-align="right"> <#if totalDeductions?has_content>
                   					<#assign totalAmt = totalDeductions?if_exists />
                   					<#assign totalAmt=(totalAmt*-1)>
                   			<@ofbizCurrency amount=totalAmt?string("#0") /></#if></fo:block></fo:table-cell>       					 		
       					 	</fo:table-row>
       					 	<fo:table-row>
       					 		<fo:table-cell>
       					 			<fo:block>-------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
       					 		</fo:table-cell>
       					 	</fo:table-row>
       					 </fo:table-body>
           			</fo:table>
           		</fo:block>
			
			<#assign netAmt= totalEarnings+totalDeductions>
           		<#if (parameters.netPayglCode?exists && parameters.netPayglCode == "Yes")>
           			<fo:block keep-together="always" white-space-collapse="false" font-weight="bold">09101    NET SALARY  :  <#if totalEarnings?has_content><#assign net = netAmt?if_exists />
                   			<@ofbizCurrency amount=net?string("#0") /></#if></fo:block>
                 <#elseif (parameters.netPayglCode?exists && parameters.netPayglCode == "No")>
                 	<fo:block keep-together="always" white-space-collapse="false" font-weight="bold">        NET SALARY  :  <#if totalEarnings?has_content><#assign net = netAmt?if_exists />
                   			<@ofbizCurrency amount=net?string("#0") /></#if></fo:block>
                 <#else>  
                 	<fo:block keep-together="always" white-space-collapse="false" font-weight="bold">    NET SALARY  :  <#if totalEarnings?has_content><#assign net = netAmt?if_exists /><@ofbizCurrency amount=net?string("#0") /></#if></fo:block>			
                 </#if>
                 <fo:block keep-together="always" white-space-collapse="false" font-weight="bold">Kindly approve the above.</fo:block>
         		<fo:block linefeed-treatment="preserve"> &#xA;</fo:block>
         		<fo:block linefeed-treatment="preserve"> &#xA;</fo:block>
         		<fo:block keep-together="always" white-space-collapse="false" font-weight="bold">Executive Director(Finance)                              Managing Director</fo:block>
			</fo:block>
			
			</fo:flow>
		</fo:page-sequence>
			
			<#else>
			
			<fo:page-sequence master-reference="main">
				<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			   		 <fo:block font-size="14pt">
			        	No Note Sheet Found
			   		 </fo:block>
				</fo:flow>
			</fo:page-sequence>	
		</#if>   
 </#if>
 </fo:root>
</#escape>