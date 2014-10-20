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
      <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
        margin-top="0.5in" margin-bottom="0.3in" margin-left=".5in" margin-right="1in">
          <fo:region-body margin-top="1.2in"/>
          <fo:region-before extent="1in"/>
          <fo:region-after extent="1in"/>
      </fo:simple-page-master>
    </fo:layout-master-set>
  <#if payRollSummaryMap?has_content>
     <fo:page-sequence master-reference="main"> 	 <#-- the footer -->
     		<fo:static-content flow-name="xsl-region-before">
     		<#if deptId?exists>
     			<#assign department=deptId/>
     		<#else>
     			<#assign department= parameters.partyId/>
     		</#if> 
     			<#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" :department}, true)>
     			<#assign partyAddressResult = dispatcher.runSync("getPartyPostalAddress", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", department, "userLogin", userLogin))/>
     			<fo:block white-space-collapse="false" font-weight="bold" text-align="center" keep-together="always">${partyGroup.groupName?if_exists?upper_case}  <#if partyAddressResult.address1?has_content>${partyAddressResult.address1?if_exists}</#if><#if (partyAddressResult.address2?has_content)>${partyAddressResult.address2?if_exists}</#if></fo:block>
        	 	<fo:block text-align="center" white-space-collapse="false" font-weight="bold">&#160; SUMMARY OF EARNINGS AND DEDUCTIONS FOR THE MONTH OF :  ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodEnd, "MMMMM-yyyy")).toUpperCase()}</fo:block>
        		<fo:block white-space-collapse="false" keep-together="always">&#160;${uiLabelMap.CommonPage}No: <fo:page-number/>                                                                                                                                     Date: ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd MMM, yyyy"))?upper_case}</fo:block>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>	 	 	  
        		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-weight="bold">Earnings                                                             Amount      Deductions                                                             Amount</fo:block>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
        	</fo:static-content>       
          <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
           		<fo:block>
           			<#assign summaryReportList =payRollSummaryMap.entrySet()>
           			<#assign totalEarnings =0 />
        			<#assign totalDeductions =0 />
           			<fo:table>
       					 <fo:table-column column-width="30pt"/>
       					 <fo:table-column column-width="280pt"/>
       					 <fo:table-column column-width="60pt"/>
       					 <fo:table-column column-width="270pt"/>
       					 <fo:table-body>
       					 	<fo:table-row>
       					 		<fo:table-cell>
       					 			<fo:block>
       					 				<fo:table>
       					 					<fo:table-column column-width="30pt"/>
       					 					<fo:table-column column-width="270pt"/>
       					 					<fo:table-body>       					 						
       					 						<#list benefitTypeIds as benefitType>
						                    		<#assign value=0>
						                    		<#if payRollSummaryMap.get(benefitType)?has_content>
						                    			<#assign value=payRollSummaryMap.get(benefitType)>	
						                    		</#if>
						                    		<#if value !=0>
						                    		<#assign extrnalGlCodeGroup = delegator.findOne("BenefitType", {"benefitTypeId" :benefitType}, true)>
							                    		<fo:table-row>                      
								                    		<fo:table-cell >                    		
								                      			<#assign totalEarnings=(totalEarnings+(value))>  
								                      			<#if extrnalGlCodeGroup.externalGlCode?exists >               			
								                    			<fo:block keep-together="always">${benefitDescMap[benefitType]?if_exists}&#160;${extrnalGlCodeGroup.externalGlCode?if_exists}</fo:block>
								                    			<#else>
								                    			<#assign extrnalGlCodeGroup = delegator.findOne("InvoiceItemType", {"invoiceItemTypeId" :benefitType}, true)>
								                    			<fo:block keep-together="always">${benefitDescMap[benefitType]?if_exists}&#160;${extrnalGlCodeGroup.defaultGlAccountId?if_exists}</fo:block>
								                    			</#if>                			
								                    		</fo:table-cell>                 	              		
								                    		<fo:table-cell><fo:block text-align="right">${value?if_exists?string("#0")}&#160;&#160;</fo:block></fo:table-cell>
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
       					 					<fo:table-column column-width="30pt"/>
       					 					<fo:table-column column-width="280pt"/>
       					 					<fo:table-body>       					 						
       					 						<#list dedTypeIds as deductionType>
						                    		<#assign dedValue=0>
						                    		<#if payRollSummaryMap.get(deductionType)?has_content>
						                    			<#assign dedValue=payRollSummaryMap.get(deductionType)>	
						                    		</#if>
						                    		<#if dedValue !=0>
						                    		<#assign extrnalGlCodeGroup = delegator.findOne("DeductionType", {"deductionTypeId" :deductionType}, true)>
							                    		<fo:table-row>                      
								                    		<fo:table-cell>                    		
								                      			<#assign totalDeductions=(totalDeductions+(dedValue))>	
								                      			<#if extrnalGlCodeGroup.externalGlCode?exists >  							                      			               			
								                    			<fo:block keep-together="always">${dedDescMap[deductionType]?if_exists}&#160;${extrnalGlCodeGroup.externalGlCode?if_exists}</fo:block>  
								                    			<#else>        
								                    			<#assign extrnalGlCodeGroup = delegator.findOne("InvoiceItemType", {"invoiceItemTypeId" :deductionType}, true)>
								                    			<fo:block keep-together="always">${dedDescMap[deductionType]?if_exists}&#160;${extrnalGlCodeGroup.defaultGlAccountId?if_exists}</fo:block>
								                    			</#if>      			
								                    		</fo:table-cell>                 	              		
								                    		<fo:table-cell><fo:block text-align="right">${((-1)*(dedValue))?if_exists?string("#0")}&#160;&#160;</fo:block></fo:table-cell>
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
           		<fo:block keep-together="always" white-space-collapse="false" font-weight="bold">NET SALARY  :   <#if totalEarnings?has_content>
                   					<#assign net = netAmt?if_exists />
                   			<@ofbizCurrency amount=net?string("#0") /></#if></fo:block>
         		<fo:block linefeed-treatment="preserve"> &#xA;</fo:block>
         		<fo:block linefeed-treatment="preserve"> &#xA;</fo:block>
         		<fo:block keep-together="always" white-space-collapse="false" font-weight="bold">Director                                                                                   Manager/Deputy Manager Finance</fo:block>
          </fo:flow>          
        </fo:page-sequence> 
        <#else>    	
		<fo:page-sequence master-reference="main">
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
   		 		<fo:block font-size="14pt">
        			No Earnigs and Deductions Found.......!
   		 		</fo:block>
			</fo:flow>
		</fo:page-sequence>		
    </#if>  
  </fo:root>
</#escape>