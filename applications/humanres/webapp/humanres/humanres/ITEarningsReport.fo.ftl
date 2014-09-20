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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-left=".3in" margin-right=".3in" margin-top=".5in">
                <fo:region-body margin-top="1.8in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "PurchaseRegisterBookReport.pdf")}
        <#if errorMessage?has_content>
	<fo:page-sequence master-reference="main">
	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	   <fo:block font-size="14pt">
	           ${errorMessage}.
        </fo:block>
	</fo:flow>
	</fo:page-sequence>	
	<#else>
       <#if partyBenefitsList?has_content>
	        <fo:page-sequence master-reference="main" font-size="12pt">	
	        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" font-size="12pt" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${uiLabelMap.CommonPage}- <fo:page-number/> </fo:block>
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
                	<fo:block text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold">DETAILS OF SALARY EARNED FROM ${fromDate?if_exists} TO ${thruDate?if_exists}</fo:block>
          			<#assign emplPositionAndFulfilment = delegator.findByAnd("EmplPositionAndFulfillment", {"employeePartyId" : employeeId})/>
          			<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : emplPositionAndFulfilment[0].emplPositionTypeId?if_exists}, true)>
          			<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
          			<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">EMPLOYEE NAME : ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, employeeId, false)}                     EMPLOYEE ID:${employeeId}              												DESIGNATION: <#if designation?has_content>${designation.description?if_exists}</#if></fo:block>
          			<fo:block>------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            	    <fo:block text-align="left" font-weight="bold" font-size="12pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">MONTH																		BASIC  														DA  												HRA 											CNV 												CCA 												OTHERS             		TOTAL</fo:block>
	        		<fo:block>------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>	
            	</fo:static-content>	        	
	        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
        			<fo:block>
        				<fo:table>
		                    <fo:table-column column-width="50pt"/>
		                    <fo:table-column column-width="160pt"/>
		                    <fo:table-column column-width="140pt"/> 
		               	    <fo:table-column column-width="120pt"/>
		            		<fo:table-column column-width="100pt"/> 		
		            		<fo:table-column column-width="120pt"/>
		            		<fo:table-column column-width="120pt"/>
		            		<fo:table-column column-width="160pt"/>
		            		<fo:table-column column-width="105pt"/>
		            		<fo:table-column column-width="130pt"/>
		                    <fo:table-body>
		                    
	                    	<#assign totalBasic = 0>
	                    	<#assign totalDA = 0>
	                    	<#assign totalHRA = 0>
	                    	<#assign totalConvey = 0>
	                    	<#assign totalCityComp = 0>
	                    	<#assign totalOthers = 0>
	                    	<#assign totalBenefits = 0>
	                    	
		                    <#list partyBenefitsList as partyBenefitsMap>
			                    <#assign partyBenefitList = partyBenefitsMap.entrySet()>
			                    <#list partyBenefitList as partyBenefits>
			                    	<#assign basic = 0>
			                    	<#assign attendanceBonus = 0>
			                    	<#assign cityComp =0>
			                    	<#assign convey =0>
			                    	<#assign coldAllowance =0>
			                    	<#assign dearnessAllowance =0>
			                    	<#assign holidayAllowance =0>
			                    	<#assign houseRentAllowance =0>
			                    	<#assign personalPay =0>
			                        <#assign secndSatDay =0>
			                        <#assign shift =0>
			                        <#assign washing =0>
			                        <#assign others = 0>
			                        <#assign benefits = 0>
			                    
				                    <#assign monthKey = partyBenefits.getKey()>
				                    
				                    <#assign customTimePeriod = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : monthKey}, true)>
				                    <#if customTimePeriod?has_content>
				                    	<#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "MMM(dd")/>
                						<#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd),yyyy")/>
				                    </#if>
				                    
				                    <#assign basic = partyBenefits.getValue().get("basic")?if_exists>
				                    <#assign attendanceBonus = partyBenefits.getValue().get("attendanceBonus")?if_exists>
				                    <#assign cityComp = partyBenefits.getValue().get("cityComp")?if_exists>
				                    <#assign convey = partyBenefits.getValue().get("convey")?if_exists>
				                    <#assign coldAllowance = partyBenefits.getValue().get("coldAllowance")?if_exists>
				                    <#assign dearnessAllowance = partyBenefits.getValue().get("dearnessAllowance")?if_exists>
				                    <#assign holidayAllowance = partyBenefits.getValue().get("holidayAllowance")?if_exists>
				                    <#assign houseRentAllowance = partyBenefits.getValue().get("houseRentAllowance")?if_exists>
				                    <#assign personalPay = partyBenefits.getValue().get("personalPay")?if_exists>
				                    <#assign secndSatDay = partyBenefits.getValue().get("secndSatDay")?if_exists>
				                    <#assign shift = partyBenefits.getValue().get("shift")?if_exists>
				                    <#assign washing = partyBenefits.getValue().get("washing")?if_exists>
				                    <#assign others = partyBenefits.getValue().get("others")?if_exists>
			                    	<#assign benefits = partyBenefits.getValue().get("totalBenefits")?if_exists>
			                    	
			                    	
			                    	<#assign totalBasic = totalBasic + basic>
			                    	<#assign totalDA = totalDA + dearnessAllowance>
			                    	<#assign totalHRA = totalHRA + houseRentAllowance>
			                    	<#assign totalConvey = totalConvey + convey>
			                    	<#assign totalCityComp = totalCityComp + cityComp>
			                    	<#assign totalOthers = totalOthers + others>
			                    	<#assign totalBenefits = totalBenefits + benefits>
			                    	
       							<fo:table-row>
       								<fo:table-cell>
							            <fo:block  keep-together="always" font-weight = "bold" text-align="left" font-size="12pt" white-space-collapse="false" >${fromDate?if_exists}-${thruDate?if_exists}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${basic?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${dearnessAllowance?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${houseRentAllowance?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${convey?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${cityComp?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${others?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${benefits?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							    </fo:table-row>
							    </#list>
							    </#list>
							    <fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
          						</fo:table-row>
							    <fo:table-row font-weight= "bold">
       								<fo:table-cell>
							            <fo:block  keep-together="always" font-weight = "bold" text-align="left" font-size="12pt" white-space-collapse="false" >Grand Total</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${totalBasic?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${totalDA?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${totalHRA?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${totalConvey?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${totalCityComp?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${totalOthers?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${totalBenefits?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							    </fo:table-row>
							    <fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
          						</fo:table-row>
          						<#assign totalBasicDA = 0>
          						<#assign othersTotal = 0>
          						<#assign totalBasicDA = totalBasic+totalDA>
          						<#assign othersTotal = totalHRA+totalConvey+totalCityComp+totalOthers>
          						<fo:table-row font-weight= "bold">
       								<fo:table-cell>
							            <fo:block  keep-together="always" font-weight = "bold" text-align="left" font-size="12pt" white-space-collapse="false" >TOTAL(BASIC+DA)</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" > = ${totalBasicDA?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" ></fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >OTHERS</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >= ${othersTotal?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" ></fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" ></fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" ></fo:block>  
							         </fo:table-cell>
							    </fo:table-row>
							    <fo:table-row> 
						 	         <fo:table-cell >   						
							 	         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
								 </fo:table-row>
							    <fo:table-row font-weight= "bold">
       								<fo:table-cell>
							            <fo:block  keep-together="always" font-weight = "bold" text-align="left" font-size="12pt" white-space-collapse="false" ></fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" ></fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" ></fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >GROSS SALARY</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >= ${totalBenefits?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" ></fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" ></fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" ></fo:block>  
							         </fo:table-cell>
							    </fo:table-row>
							    <fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
          						</fo:table-row>
							    <fo:table-row> 
							      <fo:table-cell>   						
									<fo:block keep-together="always" font-weight = "bold" font-style="italic">*All figures are in Rupees.</fo:block>
          						  </fo:table-cell>
          						  </fo:table-row>
							    <fo:table-row> 
						 	         <fo:table-cell >   						
							 	         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
								 </fo:table-row>
								 <fo:table-row> 
						 	         <fo:table-cell >   						
							 	         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
								 </fo:table-row>
								 <fo:table-row> 
						 	         <fo:table-cell >   						
							 	         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
								 </fo:table-row>
								<fo:table-row> 
							      <fo:table-cell number-columns-spanned="3" >   						
							 	         <fo:block text-align="left" white-space-collapse="false" font-size="12pt" keep-together="always" font-weight="bold">Signature(ACCNT ASST)</fo:block>
							 	   </fo:table-cell>
						 	         <fo:table-cell number-columns-spanned="2">   						
							 	         <fo:block text-align="right" white-space-collapse="false" font-size="12pt"  keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Signature(D.M.F)</fo:block>
							 	   </fo:table-cell>
							 	   <fo:table-cell number-columns-spanned="3">   						
							 	         <fo:block text-align="right" white-space-collapse="false" font-size="12pt"  keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;Signature(PRE AUDIT)</fo:block>
							 	   </fo:table-cell>
							 </fo:table-row>
	    					</fo:table-body>
                		</fo:table>
        			</fo:block> 		
				</fo:flow>
			</fo:page-sequence>
		<#else>
    	<fo:page-sequence master-reference="main">
		<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			<fo:block font-size="14pt">
	            	${uiLabelMap.NoOrdersFound}.
	       		 </fo:block>
		</fo:flow>
	</fo:page-sequence>
		</#if>   
 </#if>
 </fo:root>
</#escape>