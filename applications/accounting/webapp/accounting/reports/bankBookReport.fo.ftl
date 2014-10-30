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
            margin-top="0.2in" margin-bottom=".3in" margin-left=".7in" margin-right=".5in">
        <fo:region-body margin-top="1.4in"/>
        <fo:region-before extent="1.5in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "cashBookReport.pdf")}
 <#if dayFinAccountTransList?has_content> 
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
					<fo:block  keep-together="always" text-align="center" font-weight = "bold" font-family="Courier,monospace" font-size="10pt" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${uiLabelMap.CommonPage}- <fo:page-number/> </fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
					<#assign finAccountId=parameters.finAccountId>
		          <#assign finAccountDetails = delegator.findOne("FinAccount", {"finAccountId" : finAccountId}, true)>
                    <fo:block text-align="center" font-size="12pt" keep-together="always"  white-space-collapse="false" font-weight="bold">&#160;&#160; ${(finAccountDetails.finAccountName)?if_exists} Book From ${fromDateStr} To ${thruDateStr}	</fo:block>
              		<fo:block>
	                 	<fo:table border-style="solid">
	                    <fo:table-column column-width="70pt"/>
                    	<fo:table-column column-width="110pt"/>
                    	<fo:table-column column-width="95pt"/>
                    	<fo:table-column column-width="65pt"/>
                    	<fo:table-column column-width="80pt"/>  
               	    	<fo:table-column column-width="110pt"/>
            			<fo:table-column column-width="100pt"/> 		
            			<fo:table-column column-width="90pt"/>
            			<fo:table-column column-width="90pt"/>
            			<fo:table-column column-width="100pt"/>
            			<fo:table-column column-width="110pt"/>
	                    <fo:table-body>
	                    <fo:table-row >
	                    		<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false" font-weight="bold">Date</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false" font-weight="bold">PaymentId/Type </fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false" font-weight="bold">Pay.MethodType</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false" font-weight="bold">Inst.No</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false" font-weight="bold">PartyId </fo:block>
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false" font-weight="bold">PartyName</fo:block> 
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">Opening Balance</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">Debit Amount</fo:block>  
                       			</fo:table-cell>
                        		<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">Credit Amount</fo:block>   
                        		</fo:table-cell>
                        		<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">Closing Balance</fo:block>  
                        		</fo:table-cell>
                        		<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false" font-weight="bold">Description</fo:block>  
                        		</fo:table-cell>
                			</fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block> 		
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
                 	<fo:table border-style="solid">
                        <fo:table-column column-width="70pt"/>
                    	<fo:table-column column-width="110pt"/>
                    	<fo:table-column column-width="95pt"/>
                    	<fo:table-column column-width="65pt"/>
                    	<fo:table-column column-width="80pt"/>  
               	    	<fo:table-column column-width="110pt"/>
            			<fo:table-column column-width="100pt"/> 		
            			<fo:table-column column-width="90pt"/>
            			<fo:table-column column-width="90pt"/>
            			<fo:table-column column-width="100pt"/>
            			<fo:table-column column-width="110pt"/>
                    <fo:table-body>
                    	<#assign lineNo = 0>
                    	<#list dayFinAccountTransList as finAcctngDetails>
                    		<#assign transactionDate = (finAcctngDetails.get("transactionDate")?if_exists)/>
	                		<#assign paymentId = (finAcctngDetails.get("paymentId")?if_exists)/>
	                		<#assign partyId = (finAcctngDetails.get("partyId")?if_exists)/>
	                		<#assign openingBalance = (finAcctngDetails.get("openingBalance")?if_exists)/>
	                		<#assign debitAmount = (finAcctngDetails.get("debitAmount")?if_exists)/>
	                		<#assign creditAmount = (finAcctngDetails.get("creditAmount")?if_exists)/>
	                		<#assign closingBalance = (finAcctngDetails.get("closingBalance")?if_exists)/>
	                		<#assign partyName = (finAcctngDetails.get("partyName")?if_exists)/>
	                		<#assign description = (finAcctngDetails.get("description")?if_exists)/>
	                		<#assign comments = (finAcctngDetails.get("comments")?if_exists)/>
	                		<#assign paymentMethodTypeDes = (finAcctngDetails.get("paymentMethodTypeDes")?if_exists)/>
	                		<#assign instrumentNum = (finAcctngDetails.get("instrumentNum")?if_exists)/>
	                		
								<fo:table-row border-style="solid">
									<fo:table-cell border-style="solid">
	                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                             ${transactionDate?if_exists}
                                      </fo:block>  
	                       			</fo:table-cell>
									<#if ((paymentId)?has_content)>
										<#if (paymentId != "DAY TOTAL")>
		                       			<fo:table-cell border-style="solid">
		                            		<fo:block  text-align="left" font-size="11pt" white-space-collapse="false"> 
	                                             ${paymentId?if_exists}/${description?if_exists}
	                                      </fo:block>  
		                       			</fo:table-cell>
		                       			<#else>
		                       			<fo:table-cell border-style="solid" font-weight="bold">
		                            		<fo:block  keep-together="always" text-align="left" font-size="13pt" white-space-collapse="false"> 
	                                             ${paymentId?if_exists}
	                                      </fo:block>  
		                       			</fo:table-cell>
		                       			</#if>
	                       			<#else>
	                       			<fo:table-cell border-style="solid">
	                                    <fo:block text-align="center"></fo:block>
	                                </fo:table-cell>
	                                </#if>	
                                	<#if ((paymentMethodTypeDes)?has_content)>
                                	<fo:table-cell border-style="solid" font-weight="bold">
	                                    <fo:block font-size="13pt" text-align="left">${(paymentMethodTypeDes)}</fo:block>
	                                </fo:table-cell>
                                 	<#else>
                                 	<fo:table-cell border-style="solid">
	                                    <fo:block text-align="center"></fo:block>
	                                </fo:table-cell>
                                	</#if>
                                	<#if ((instrumentNum)?has_content)>
                                	<fo:table-cell border-style="solid" font-weight="bold">
	                                    <fo:block font-size="13pt" text-align="left">${(instrumentNum)}</fo:block>
	                                </fo:table-cell>
                                 	<#else>
                                 	<fo:table-cell border-style="solid">
	                                    <fo:block text-align="center"></fo:block>
	                                </fo:table-cell>
                                	</#if>
                                	<#if ((partyId)?has_content)>
                                	<fo:table-cell border-style="solid" font-weight="bold">
	                                    <fo:block font-size="13pt" text-align="left">${(partyId)}</fo:block>
	                                </fo:table-cell>
                                 	<#else>
                                 	<fo:table-cell border-style="solid">
	                                    <fo:block text-align="center"></fo:block>
	                                </fo:table-cell>
                                	</#if>
	                       			<fo:table-cell border-style="solid">
                                    <fo:block text-align="left" font-size="13pt" font-weight="bold">
                                            ${partyName?if_exists}
                                    </fo:block>
	                                </fo:table-cell>
	                                <#if (paymentId != "DAY TOTAL")>
	                                <fo:table-cell border-style="solid">
	                                    <fo:block text-align="right" font-size="13pt">
	                                            <#if openingBalance?has_content>${(openingBalance)?string("##0.00")}<#else>0.00</#if>
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid">
	                                    <fo:block text-align="right" font-size="13pt">
	                                             <#if debitAmount?has_content>${(debitAmount)?string("##0.00")}<#else>0.00</#if>
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid">
	                                    <fo:block text-align="right" font-size="13pt">
	                                             <#if creditAmount?has_content>${(creditAmount)?string("##0.00")}<#else>0.00</#if>
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid">
	                                    <fo:block text-align="right" font-size="13pt">
	                                            <#if closingBalance?has_content>${(closingBalance)?string("##0.00")}<#else>0.00</#if>
	                                    </fo:block>
	                                </fo:table-cell>
	                                <#else>
	                                <fo:table-cell border-style="solid" font-weight="bold">
	                                    <fo:block text-align="right" font-size="13pt"> 
	                                            <#if openingBalance?has_content>${(openingBalance)?string("##0.00")}<#else>0.00</#if>
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid" font-weight="bold">
	                                    <fo:block text-align="right" font-size="13pt">
	                                             <#if debitAmount?has_content>${(debitAmount)?string("##0.00")}<#else>0.00</#if>
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid" font-weight="bold">
	                                    <fo:block text-align="right" font-size="13pt">
	                                             <#if creditAmount?has_content>${(creditAmount)?string("##0.00")}<#else>0.00</#if>
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid" font-weight="bold">
	                                    <fo:block text-align="right" font-size="13pt">
	                                            <#if closingBalance?has_content>${(closingBalance)?string("##0.00")}<#else>0.00</#if>
	                                    </fo:block>
	                                </fo:table-cell>
	                                </#if>
	                                <#if ((comments)?has_content)>
                                	<fo:table-cell border-style="solid">
	                                    <fo:block text-align="left" font-size="13pt">${(comments)}</fo:block>
	                                </fo:table-cell>
                                 	<#else>
                                 	<fo:table-cell border-style="solid">
	                                    <fo:block text-align="center"></fo:block>
	                                </fo:table-cell>
                                	</#if>
                              </fo:table-row>
                              <fo:table-row>
								<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
							</fo:table-row>
                          </#list>
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
						</fo:table-row><fo:table-row>
							<fo:table-cell>
			            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
			       			</fo:table-cell>
						</fo:table-row>
						<fo:table-row font-weight = "bold">
							<fo:table-cell>
			            		<fo:block  keep-together="always"></fo:block>  
			       			</fo:table-cell>
			       			<fo:table-cell>
			            		<fo:block  keep-together="always">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Asst/Dpty Manager(Finance)</fo:block>  
			       			</fo:table-cell>
			       			<fo:table-cell>
			            		<fo:block  keep-together="always">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Pre.Audit</fo:block>  
			       			</fo:table-cell>
			       			<fo:table-cell>
			            		<fo:block  keep-together="always">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;MF/GMF</fo:block>  
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
</fo:root>
</#escape>