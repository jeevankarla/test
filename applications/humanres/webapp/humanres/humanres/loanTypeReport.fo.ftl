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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
            margin-top="0.1in" margin-bottom=".7in" margin-left=".5in" margin-right=".5in">
        <fo:region-body margin-top="2.15in"/>
        <fo:region-before extent="1.in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "LoanAvailedReport.pdf")}

 <#if loanTypeList?has_content> 
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" font-weight="bold">  MOTHER DAIRY A UNIT OF K.M.F </fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" font-weight="bold">  G.K.V.K POST, BANGALORE, KARNATAKA - 560065 </fo:block>
			
					<#assign loanTypeId=parameters.loanTypeId>
		          <#assign loanTypeName = delegator.findOne("LoanType", {"loanTypeId" : loanTypeId}, true)>
		           <#if loanTypeName?has_content> 
		          
                    <fo:block text-align="center" font-size="13pt" keep-together="always"  white-space-collapse="false" font-weight="bold">&#160;&#160;LOAN AND ADVANCES REPORT PRINCIPAL AND INTEREST FOR: ${(loanTypeName.description)?if_exists}  </fo:block>
              		   </#if>
              		 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" font-weight="bold">  For the month of :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate?if_exists, "dd/MM/yy ")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate?if_exists, "dd/MM/yy")} </fo:block>
              		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" font-weight="bold"> &#160;&#160;  </fo:block>
              		   
              		 <fo:block>
	                 	<fo:table border-style="solid">
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="140pt"/>  
               	    <fo:table-column column-width="75pt"/>
               	    <fo:table-column column-width="80pt"/>
            		<fo:table-column column-width="70pt"/> 		
            		<fo:table-column column-width="30pt"/>
            		<fo:table-column column-width="80pt"/>
            		<fo:table-column column-width="30pt"/>
            		<fo:table-column column-width="75pt"/>
            		<fo:table-column column-width="70pt"/> 		
            		<fo:table-column column-width="85pt"/>
            		<fo:table-column column-width="85pt"/>
            		<fo:table-column column-width="60pt"/>
            		<fo:table-column column-width="60pt"/>
	                    <fo:table-body>
	                    <fo:table-row >
	                    		<fo:table-cell border-style="solid">
                            		<fo:block   text-align="center" font-size="13pt" white-space-collapse="false" font-weight="bold">Disb Date d/m/y</fo:block>  
                       			</fo:table-cell>                     
                       			<fo:table-cell border-style="solid">
                            		<fo:block   text-align="center" font-size="13pt" white-space-collapse="false" font-weight="bold">Emp Code </fo:block>
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block   text-align="center" font-size="13pt" white-space-collapse="false" font-weight="bold">Employee Name</fo:block> 
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block   text-align="center" font-size="13pt" white-space-collapse="false" font-weight="bold">Principal Amount</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block   text-align="center" font-size="13pt" white-space-collapse="false" font-weight="bold">Interest Amount</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block   text-align="center" font-size="13pt" white-space-collapse="false" font-weight="bold">PRN INST  EMI Rs </fo:block>  
                       			</fo:table-cell>
                        		<fo:table-cell border-style="solid">
                            		<fo:block   text-align="center" font-size="13pt" white-space-collapse="false" font-weight="bold">PRN INST NO</fo:block>   
                        		</fo:table-cell>
                        		<fo:table-cell border-style="solid">
                            		<fo:block   text-align="center" font-size="13pt" white-space-collapse="false" font-weight="bold">INT  INST  EMI Rs </fo:block>  
                        		</fo:table-cell>
                        		<fo:table-cell border-style="solid">
                            		<fo:block  text-align="center" font-size="13pt" white-space-collapse="false" font-weight="bold">INT INST NO</fo:block>  
                        		</fo:table-cell>
                        		<fo:table-cell border-style="solid">
                            		<fo:block   text-align="center" font-size="13pt" white-space-collapse="false" font-weight="bold">TOT DEDN PRN </fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block   text-align="center" font-size="13pt" white-space-collapse="false" font-weight="bold">TOT DEDN INT </fo:block>  
                       			</fo:table-cell>
                        		<fo:table-cell border-style="solid">
                            		<fo:block   text-align="center" font-size="13pt" white-space-collapse="false" font-weight="bold">BAL AMOUNT PRN </fo:block>   
                        		</fo:table-cell>
                        		<fo:table-cell border-style="solid">
                            		<fo:block   text-align="center" font-size="13pt" white-space-collapse="false" font-weight="bold">BAL AMOUNT INT </fo:block>  
                        		</fo:table-cell>
                        		<fo:table-cell border-style="solid">
                            		<fo:block  text-align="center" font-size="13pt" white-space-collapse="false" font-weight="bold">BAL EMI PRN</fo:block>  
                        		</fo:table-cell>
                        		<fo:table-cell border-style="solid">
                            		<fo:block   text-align="center" font-size="13pt" white-space-collapse="false" font-weight="bold">BAL EMI INT</fo:block>  
                       			</fo:table-cell>
                			</fo:table-row>
                    </fo:table-body>
                </fo:table>
              </fo:block> 		                
            </fo:static-content>		
           <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
                 	<fo:table border-style="solid">
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="140pt"/>  
               	    <fo:table-column column-width="75pt"/>
               	    <fo:table-column column-width="80pt"/>
            		<fo:table-column column-width="70pt"/> 		
            		<fo:table-column column-width="30pt"/>
            		<fo:table-column column-width="80pt"/>
            		<fo:table-column column-width="30pt"/>
            		<fo:table-column column-width="75pt"/>
            		<fo:table-column column-width="70pt"/> 		
            		<fo:table-column column-width="85pt"/>
            		<fo:table-column column-width="85pt"/>
            		<fo:table-column column-width="60pt"/>
            		<fo:table-column column-width="60pt"/>
                    <fo:table-body>
                		<#assign sNo=1>
                		<#assign totalPRNAMT=0>
                		<#assign totalINTAMT=0>
                		<#assign totalprinAmtEmi=0>
                		<#assign totalintrstAmtEmi=0>
                		<#assign totaloftotalRecPrinAmount=0>
                		<#assign totaloftotalRecIntAmount=0>
                		<#assign totalnetPrinAmount=0>
                		<#assign totalnetIntAmount=0>
                		<#assign totalnetPrinInst=0>
                		<#assign totalnetIntInst=0>
                		
                		
                    	<#list loanTypeList as loanAcctngDetails>
                    	    <#assign principalAmount =0>
	                		<#assign interestAmount = 0>
	                		<#assign prinAmtEmi = 0>
	                		<#assign numPrincipalInst = 0>
	                		<#assign intrstAmtEmi = 0>
	                		<#assign numInterestInst = 0>
	                		<#assign totalRecPrinAmount = 0>
							<#assign totalRecIntAmount = 0>
				     		<#assign netPrinAmount = 0>
						    <#assign netIntAmount =0>
							<#assign netPrinInst = 0>
						    <#assign netIntInst =0>
                    	
                          <#assign disbDate = (loanAcctngDetails.get("disbDate")?if_exists)/> 
	                		<#assign employeeId = (loanAcctngDetails.get("employeeId")?if_exists)/>
	                		<#assign employeeName = (loanAcctngDetails.get("employeeName")?if_exists)/>
	                		<#assign principalAmount = (loanAcctngDetails.get("principalAmount")?if_exists)/>
	                		<#assign interestAmount = (loanAcctngDetails.get("interestAmount")?if_exists)/>
	                		<#assign prinAmtEmi = (loanAcctngDetails.get("prinAmtEmi")?if_exists)/>
	                		<#assign numPrincipalInst = (loanAcctngDetails.get("numPrincipalInst")?if_exists)/>
	                		<#assign intrstAmtEmi = (loanAcctngDetails.get("intrstAmtEmi")?if_exists)/>
	                		<#assign numInterestInst = (loanAcctngDetails.get("numInterestInst")?if_exists)/>
	                		<#assign totalRecPrinAmount = (loanAcctngDetails.get("totalRecPrinAmount")?if_exists)/>
							<#assign totalRecIntAmount = (loanAcctngDetails.get("totalRecIntAmount")?if_exists)/>
				     		<#assign netPrinAmount = (loanAcctngDetails.get("netPrinAmount")?if_exists)/>
						    <#assign netIntAmount = (loanAcctngDetails.get("netIntAmount")?if_exists)/>
							<#assign netPrinInst = (loanAcctngDetails.get("netPrinInst")?if_exists)/>
						    <#assign netIntInst = (loanAcctngDetails.get("netIntInst")?if_exists)/>
						                		
						    <#assign totalPRNAMT=totalPRNAMT+principalAmount> 
						    <#assign totalINTAMT=totalINTAMT+interestAmount>       		
						    <#assign totalprinAmtEmi=totalprinAmtEmi+prinAmtEmi>
                		    <#assign totalintrstAmtEmi=totalintrstAmtEmi+intrstAmtEmi>
                		    <#assign totaloftotalRecPrinAmount=totaloftotalRecPrinAmount+totalRecPrinAmount>
                		    <#assign totaloftotalRecIntAmount=totaloftotalRecIntAmount+totalRecIntAmount>
                		    <#assign totalnetPrinAmount=totalnetPrinAmount+netPrinAmount>
                		    <#assign totalnetIntAmount=totalnetIntAmount+netIntAmount>
                		    <#assign totalnetPrinInst=totalnetPrinInst+netPrinInst>
                		    <#assign totalnetIntInst=totalnetIntInst+netIntInst>
						                     		
								<fo:table-row border-style="solid">
								
									<fo:table-cell border-style="solid">
	                            	 <fo:block  text-align="left"  font-size="12pt" > ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(disbDate?if_exists, "dd/MM/yy ")}</fo:block>                 			  
	                       			</fo:table-cell>
                                	<fo:table-cell border-style="solid" >
	                                    <fo:block font-size="12pt" text-align="center">${employeeId?if_exists}</fo:block>
	                                </fo:table-cell>
                                 	<fo:table-cell border-style="solid">
	                                    <fo:block text-align="left">${employeeName?if_exists}</fo:block>
	                                </fo:table-cell>
	                       			<fo:table-cell border-style="solid">
                                    <fo:block text-align="right" font-size="12pt" >
                                            <#if principalAmount?has_content>${(principalAmount)?if_exists?string("##0.00")}<#else>0.00</#if>
                                            
                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid">
                                    <fo:block text-align="right" font-size="12pt" >
                                              <#if interestAmount?has_content>${(interestAmount?if_exists?string("##0.00"))}<#else>0.00</#if>
                                            
                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid">
	                                    <fo:block text-align="right" font-size="12pt">
	                                               <#if prinAmtEmi?has_content>${(prinAmtEmi)?if_exists?string("##0.00")}<#else>0.00</#if>
	                                       
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid">
	                                    <fo:block text-align="right" font-size="12pt">
	                                         <#if numPrincipalInst?has_content>${(numPrincipalInst)?if_exists}<#else>0</#if>
	                                     
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid">
	                                    <fo:block text-align="right" font-size="12pt">
	                                        <#if intrstAmtEmi?has_content>${(intrstAmtEmi)?if_exists?string("##0.00")}<#else>0.00</#if>
	                                       
	                                    </fo:block>
	                                </fo:table-cell>
	                                
	                                <fo:table-cell border-style="solid">
	                                    <fo:block text-align="right" font-size="12pt">
                                           <#if numInterestInst?has_content>${(numInterestInst)?if_exists}<#else>0</#if>
	                                     
	                                    </fo:block>
	                                </fo:table-cell>
	                               <fo:table-cell border-style="solid">
	                                    <fo:block text-align="right" font-size="12pt">
                               <#if totalRecPrinAmount?has_content>${(totalRecPrinAmount)?if_exists?string("##0.00")}<#else>0.00</#if>
	                                     
	                                    </fo:block>
	                                </fo:table-cell>
	                               <fo:table-cell border-style="solid">
	                                    <fo:block text-align="right" font-size="12pt">
                             <#if totalRecIntAmount?has_content>${(totalRecIntAmount)?if_exists?string("##0.00")}<#else>0.00</#if>
	                                     
	                                    </fo:block>
	                                </fo:table-cell>
	                               <fo:table-cell border-style="solid">
	                                    <fo:block text-align="right" font-size="12pt">
                               <#if netPrinAmount?has_content>${(netPrinAmount)?if_exists?string("##0.00")}<#else>0.00</#if>
	                                     
	                                    </fo:block>
	                                </fo:table-cell>
	                               <fo:table-cell border-style="solid">
	                                    <fo:block text-align="right" font-size="12pt">
                            <#if netIntAmount?has_content>${(netIntAmount)?if_exists?string("##0.00")}<#else>0.00</#if>
	                                     
	                                    </fo:block>
	                                </fo:table-cell>
	                               <fo:table-cell border-style="solid">
	                                    <fo:block text-align="right" font-size="12pt">
                        <#if netPrinInst?has_content>${(netPrinInst)?if_exists?string("##0.00")}<#else>0.00</#if>
	                                     
	                                    </fo:block>
	                                </fo:table-cell>
	                               <fo:table-cell border-style="solid">
	                                    <fo:block text-align="right" font-size="12pt">
                       <#if netIntInst?has_content>${(netIntInst)?if_exists?string("##0.00")}<#else>0.00</#if>
	                                     
	                                    </fo:block>
	                                </fo:table-cell>
	                              	<#assign sNo=sNo+1>
                              </fo:table-row>
                          </#list>
                          <fo:table-row border-style="solid">
								
									<fo:table-cell border-style="solid">
	                            	 <fo:block  text-align="left"  font-size="12pt" > 	</fo:block>                 			  
	                       			</fo:table-cell>
                                	<fo:table-cell border-style="solid" font-weight="bold">
	                                    <fo:block font-size="12pt" text-align="left"></fo:block>
	                                </fo:table-cell>
                                 	<fo:table-cell border-style="solid">
	                                    <fo:block text-align="left" font-size="12pt" font-weight="bold">TOTAL</fo:block>
	                                </fo:table-cell>
	                       			<fo:table-cell border-style="solid">
                                    <fo:block text-align="right" font-size="12pt" font-weight="bold">
                                  <#if totalPRNAMT?has_content>${(totalPRNAMT)?if_exists?string("##0.00")}<#else>0.00</#if>
                                       
                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid">
                                    <fo:block text-align="right" font-size="12pt" font-weight="bold">
                                  <#if totalINTAMT?has_content>${(totalINTAMT)?if_exists?string("##0.00")}<#else>0.00</#if>
                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid">
	                                 <fo:block text-align="right" font-size="12pt" font-weight="bold">
                                <#if totalprinAmtEmi?has_content>${(totalprinAmtEmi)?if_exists?string("##0.00")}<#else>0.00</#if>
	                                    
	                                  </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid">
	                                 <fo:block text-align="right" font-size="12pt" font-weight="bold">
	                                </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid">
	                                    <fo:block text-align="right" font-size="12pt" font-weight="bold">
                               <#if totalintrstAmtEmi?has_content>${(totalintrstAmtEmi)?if_exists?string("##0.00")}<#else>0.00</#if>
	                                     
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid">
	                                    <fo:block text-align="right" font-size="12pt" font-weight="bold">
	                                    </fo:block>
	                                </fo:table-cell>
	                               <fo:table-cell border-style="solid">
	                                    <fo:block text-align="right" font-size="12pt" font-weight="bold">
	                              <#if totaloftotalRecPrinAmount?has_content>${(totaloftotalRecPrinAmount)?if_exists?string("##0.00")}<#else>0.00</#if>
	                                     
	                                    </fo:block>
	                                </fo:table-cell>
	                               <fo:table-cell border-style="solid">
	                                    <fo:block text-align="right" font-size="12pt" font-weight="bold">
                              <#if totaloftotalRecIntAmount?has_content>${(totaloftotalRecIntAmount)?if_exists?string("##0.00")}<#else>0.00</#if>
	                                        
	                                    </fo:block>
	                                </fo:table-cell>
	                               <fo:table-cell border-style="solid">
	                                    <fo:block text-align="right" font-size="12pt" font-weight="bold">
                               <#if totalnetPrinAmount?has_content>${(totalnetPrinAmount)?if_exists?string("##0.00")}<#else>0.00</#if>
	                                         
	                                    </fo:block>
	                                </fo:table-cell>
	                               <fo:table-cell border-style="solid">
	                                    <fo:block text-align="right" font-size="12pt" font-weight="bold">
                             <#if totalnetIntAmount?has_content>${(totalnetIntAmount)?if_exists?string("##0.00")}<#else>0.00</#if>
	                                     
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
				                      NO DATA FOUND      	
	       		 </fo:block>
		</fo:flow>
	</fo:page-sequence>	
    </#if>  
</fo:root>
</#escape>