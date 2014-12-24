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

<#-- do not display columns associated with values specified in the request, ie constraint values -->
<fo:layout-master-set>
	<fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
            margin-top="0.05in" margin-bottom=".7in" margin-left=".2in" margin-right=".2in">
        <fo:region-body margin-top="1.86in"/>
        <fo:region-before extent="1.in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "LoanAvailedReport.pdf")}

 <#if loanTypeList?has_content> 
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			        <fo:static-content flow-name="xsl-region-before">
					<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false">UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
					<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false">Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
				<#assign loanTypeId=parameters.loanTypeId>
		        <#assign loanTypeName = delegator.findOne("LoanType", {"loanTypeId" : loanTypeId}, true)>
		           <#if loanTypeName?has_content> 
                    <fo:block text-align="center" font-size="13pt" keep-together="always"  white-space-collapse="false" font-weight="bold">&#160;&#160;LOAN SANCTION REPORT FOR: ${(loanTypeName.description)?if_exists}  </fo:block>
              		</#if>
              		 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" font-weight="bold">Period From :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate?if_exists, "dd/MM/yy ")} To : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate?if_exists, "dd/MM/yy")} </fo:block>
              		 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" font-weight="bold"> &#160;&#160;  </fo:block>
              		 <fo:block>
	                 	<fo:table border-style="solid">
                    <fo:table-column column-width="30pt"/>
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="120pt"/>  
               	    <fo:table-column column-width="160pt"/>
               	    <fo:table-column column-width="80pt"/>
            		<fo:table-column column-width="80pt"/>
            		<fo:table-column column-width="80pt"/>
            		<fo:table-column column-width="80pt"/>
	                 <fo:table-body>
	                    <fo:table-row >
	                    		<fo:table-cell border-style="solid">
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">S No</fo:block>  
                       			</fo:table-cell>                     
                       			<fo:table-cell border-style="solid">
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">Emp Code </fo:block>
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">Employee Name</fo:block> 
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">Designation</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">App.No</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">App.Date</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block   text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">Required Amount</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block   text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">Sanctioned Amount</fo:block>  
                       			</fo:table-cell>
                			</fo:table-row>
                    </fo:table-body>
                </fo:table>
              </fo:block> 		                
            </fo:static-content>		
           <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
                 	<fo:table border-style="solid">
                    <fo:table-column column-width="30pt"/>
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="120pt"/>  
               	    <fo:table-column column-width="160pt"/>
               	    <fo:table-column column-width="80pt"/>
            		<fo:table-column column-width="80pt"/>
            		<fo:table-column column-width="80pt"/>
            		<fo:table-column column-width="80pt"/>
                    <fo:table-body>
                		<#assign sNo=1>
                		<#assign totalPrincipalAmount=0> 
						<#assign totalMaxAmount=0> 
                    	<#list loanTypeList as loanAcctngDetails>
                    		<#assign loanId = (loanAcctngDetails.get("loanId")?if_exists)/> 
                            <#assign disbDate = (loanAcctngDetails.get("disbDate")?if_exists)/> 
	                		<#assign employeeId = (loanAcctngDetails.get("employeeId")?if_exists)/>
	                		<#assign employeeName = (loanAcctngDetails.get("employeeName")?if_exists)/>
	                		<#assign principalAmount = (loanAcctngDetails.get("principalAmount")?if_exists)/>
	                		<#assign maxAmount = (loanAcctngDetails.get("maxAmount")?if_exists)/>
	                		
	                		
	                		<#assign totalPrincipalAmount=totalPrincipalAmount+principalAmount> 
						    <#assign totalMaxAmount=totalMaxAmount+maxAmount> 
						    
						    <#assign emplPositionAndFulfilment=delegator.findByAnd("EmplPositionAndFulfillment", {"employeePartyId" : employeeId})/>
						                		
								<fo:table-row border-style="solid">
									<fo:table-cell border-style="solid">
	                            	 	<fo:block  text-align="left"  font-size="12pt" > ${sNo?if_exists}</fo:block>                 			  
	                       			</fo:table-cell>
									<fo:table-cell border-style="solid" >
	                                    <fo:block font-size="12pt" text-align="left">${employeeId?if_exists}</fo:block>
	                                </fo:table-cell>
									<fo:table-cell border-style="solid">
	                                    <fo:block text-align="left">${employeeName?if_exists}</fo:block>
	                                </fo:table-cell>
	                                 <#if emplPositionAndFulfilment[0].emplPositionTypeId?has_content>
                     					<#assign designationName=emplPositionAndFulfilment[0].name?if_exists>
			                                <fo:table-cell border-style="solid" keep-together = "always">
			                                    <fo:block text-align="left">${designationName?if_exists}</fo:block>
			                                </fo:table-cell>
	                                </#if>
	                                <fo:table-cell border-style="solid">
	                                    <fo:block text-align="left">${loanId?if_exists}</fo:block>
	                                </fo:table-cell>
									<fo:table-cell border-style="solid">
	                            	 	<fo:block  text-align="left"  font-size="12pt" > ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(disbDate?if_exists, "dd/MM/yy ")}</fo:block>                 			  
	                       			</fo:table-cell>
	                       			<fo:table-cell border-style="solid">
	                                    <fo:block text-align="right" font-size="12pt" >
	                                            <#if principalAmount?has_content>${(principalAmount)?if_exists?string("##0.00")}<#else>0.00</#if>
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid">
	                                    <fo:block text-align="right" font-size="12pt" >
	                                              <#if maxAmount?has_content>${(maxAmount?if_exists?string("##0.00"))}<#else>0.00</#if>
	                                    </fo:block>
	                                </fo:table-cell>
	                              	<#assign sNo=sNo+1>
                              </fo:table-row>
                          </#list>
                          	<fo:table-row border-style="solid">
									<fo:table-cell border-style="solid">
	                            	    <fo:block text-align="left" font-size="12pt" font-weight="bold"></fo:block>                 			  
	                       			</fo:table-cell>
                                	<fo:table-cell border-style="solid" font-weight="bold">
	                                    <fo:block font-size="12pt" text-align="left"></fo:block>
	                                </fo:table-cell>
                                 	<fo:table-cell border-style="solid">
	                                    <fo:block text-align="left" font-size="12pt" font-weight="bold">TOTAL</fo:block>
	                                </fo:table-cell>
	                       			<fo:table-cell border-style="solid">
                                    <fo:block text-align="right" font-size="12pt" font-weight="bold">
                                       
                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid">
                                    <fo:block text-align="right" font-size="12pt" font-weight="bold">
                                    </fo:block>
	                                </fo:table-cell>
	                                 <fo:table-cell border-style="solid">
                                    <fo:block text-align="right" font-size="12pt" font-weight="bold">
                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid">
	                                 <fo:block text-align="right" font-size="12pt" font-weight="bold">
	                                              <#if totalPrincipalAmount?has_content>${(totalPrincipalAmount?if_exists?string("##0.00"))}<#else>0.00</#if>
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid">
	                                	<fo:block text-align="right" font-size="12pt" font-weight="bold">
	                                              <#if totalMaxAmount?has_content>${(totalMaxAmount?if_exists?string("##0.00"))}<#else>0.00</#if>
	                                    </fo:block>
	                                </fo:table-cell>
                              </fo:table-row>
                              <fo:table-row>
							<fo:table-cell>
			            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
			       			</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell>
			            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
			       			</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell>
			            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
			       			</fo:table-cell>
						</fo:table-row>
                              <fo:table-row font-weight = "bold">
			       			<fo:table-cell>
			            		<fo:block  keep-together="always">&#160;&#160;PROCESSOR</fo:block>  
			       			</fo:table-cell>
			       			<fo:table-cell>
			            		<fo:block  keep-together="always">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Asst/Dpty Manager(Finance)</fo:block>  
			       			</fo:table-cell>
			       			<fo:table-cell>
			            		<fo:block  keep-together="always">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Pre.Audit</fo:block>  
			       			</fo:table-cell>
			       			<fo:table-cell>
			            		<fo:block  keep-together="always">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;MF/GMF</fo:block>  
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
				                      No loans found...!      	
	       		 </fo:block>
		</fo:flow>
	</fo:page-sequence>	
    </#if>  
</fo:root>
</#escape>