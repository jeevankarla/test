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
            margin-top="0.2in" margin-bottom=".3in" margin-left=".1in" margin-right=".1in">
        <fo:region-body margin-top="1.2in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "cashBookReport.pdf")}
 <#if dayFinAccountTransList?has_content> 
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
					<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false">&#160;      UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
					<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false">&#160;      ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      ${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      ${uiLabelMap.KMFDairySubHeader}</fo:block>
                    <fo:block text-align="center" font-size="12pt" keep-together="always"  white-space-collapse="false" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Cash Book For ${fromDateStr}:								&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${uiLabelMap.CommonPage}- <fo:page-number/></fo:block>
              		<fo:block>
	                 	<fo:table border-style="solid">
	                    <fo:table-column column-width="120pt"/>
	                    <fo:table-column column-width="80pt"/>
	                    <fo:table-column column-width="140pt"/>  
	               	    <fo:table-column column-width="100pt"/>
	            		<fo:table-column column-width="80pt"/> 		
	            		<fo:table-column column-width="80pt"/>
	            		<fo:table-column column-width="100pt"/>
	                    <fo:table-body>
	                    <fo:table-row >
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold">PaymentId/Type </fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold">PartyId </fo:block>
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold">PartyName</fo:block> 
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false" font-weight="bold">Opening Balance</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false" font-weight="bold">Debit Amount</fo:block>  
                       			</fo:table-cell>
                        		<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold">Credit Amount</fo:block>   
                        		</fo:table-cell>
                        		<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false" font-weight="bold">Closing Balance</fo:block>  
                        		</fo:table-cell>
                			</fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block> 		
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
                 	<fo:table border-style="solid">
                    <fo:table-column column-width="120pt"/>
                    <fo:table-column column-width="80pt"/>
                    <fo:table-column column-width="140pt"/> 
               	    <fo:table-column column-width="100pt"/>
            		<fo:table-column column-width="80pt"/> 		
            		<fo:table-column column-width="80pt"/>
            		<fo:table-column column-width="100pt"/>
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
								<fo:table-row border-style="solid">
									<#if ((paymentId)?has_content)>
										<#if (paymentId != "DAY TOTAL")>
		                       			<fo:table-cell border-style="solid">
		                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false"> 
		                            			<#assign paymentType = delegator.findOne("PaymentAndType", {"paymentId" : paymentId}, true)?if_exists/>
	                                             ${paymentId?if_exists}/${paymentType.description?if_exists}
	                                      </fo:block>  
		                       			</fo:table-cell>
		                       			<#else>
		                       			<fo:table-cell border-style="solid" font-weight="bold">
		                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false"> 
	                                             ${paymentId?if_exists}
	                                      </fo:block>  
		                       			</fo:table-cell>
		                       			</#if>
	                       			<#else>
	                       			<fo:table-cell border-style="solid">
	                                    <fo:block text-align="center"></fo:block>
	                                </fo:table-cell>
	                                </#if>	
	                       			<#if ((partyId)?has_content)>
                                	<fo:table-cell border-style="solid" font-weight="bold">
	                                    <fo:block text-align="center">${(partyId)}</fo:block>
	                                </fo:table-cell>
                                 	<#else>
                                 	<fo:table-cell border-style="solid">
	                                    <fo:block text-align="center"></fo:block>
	                                </fo:table-cell>
                                	</#if>
	                       			<fo:table-cell border-style="solid">
                                    <fo:block text-align="left" keep-together="always" font-weight="bold">
                                            <#if partyId?has_content>${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)} </#if>
                                    </fo:block>
	                                </fo:table-cell>
	                                <#if (paymentId != "DAY TOTAL")>
	                                <fo:table-cell border-style="solid">
	                                    <fo:block text-align="right">
	                                            <#if openingBalance?has_content>${(openingBalance)?string("##0.0")}<#else>0.0</#if>
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid">
	                                    <fo:block text-align="right">
	                                             <#if debitAmount?has_content>${(debitAmount)?string("##0.0")}<#else>0.0</#if>
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid">
	                                    <fo:block text-align="right">
	                                             <#if creditAmount?has_content>${(creditAmount)?string("##0.0")}<#else>0.0</#if>
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid">
	                                    <fo:block text-align="right">
	                                            <#if closingBalance?has_content>${(closingBalance)?string("##0.0")}<#else>0.0</#if>
	                                    </fo:block>
	                                </fo:table-cell>
	                                <#else>
	                                <fo:table-cell border-style="solid" font-weight="bold">
	                                    <fo:block text-align="right">
	                                            <#if openingBalance?has_content>${(openingBalance)?string("##0.0")}<#else>0.0</#if>
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid" font-weight="bold">
	                                    <fo:block text-align="right">
	                                             <#if debitAmount?has_content>${(debitAmount)?string("##0.0")}<#else>0.0</#if>
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid" font-weight="bold">
	                                    <fo:block text-align="right">
	                                             <#if creditAmount?has_content>${(creditAmount)?string("##0.0")}<#else>0.0</#if>
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid" font-weight="bold">
	                                    <fo:block text-align="right">
	                                            <#if closingBalance?has_content>${(closingBalance)?string("##0.0")}<#else>0.0</#if>
	                                    </fo:block>
	                                </fo:table-cell>
	                                </#if>
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
						<fo:table-row>
							<fo:table-cell>
			            		<fo:block  keep-together="always">Cashier/Accounts Asst</fo:block>  
			       			</fo:table-cell>
			       			<fo:table-cell>
			            		<fo:block  keep-together="always">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Asst Manager(Finance)</fo:block>  
			       			</fo:table-cell>
			       			<fo:table-cell>
			            		<fo:block  keep-together="always">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Pre.Audit</fo:block>  
			       			</fo:table-cell>
			       			<fo:table-cell>
			            		<fo:block  keep-together="always">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;MF/GMF</fo:block>  
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