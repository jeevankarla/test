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
            margin-top="0.2in" margin-bottom=".3in" margin-left=".2in" margin-right=".2in">
        <fo:region-body margin-top="1.4in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "cashBookReport.pdf")}
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
			        <#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
                    <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
					<fo:block  keep-together="always" text-align="center" font-weight = "bold" font-family="Courier,monospace" font-size="10pt" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${uiLabelMap.CommonPage}- <fo:page-number/> </fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
                    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >  ${reportHeader.description?if_exists} </fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >  ${reportSubHeader.description?if_exists}  </fo:block>
					<#assign finAccountId=parameters.finAccountId>
		          <#assign finAccountDetails = delegator.findOne("FinAccount", {"finAccountId" : finAccountId}, true)>
                    <fo:block text-align="center" font-size="11pt" keep-together="always"  white-space-collapse="false" font-weight="bold">${(finAccountDetails.finAccountName)?if_exists} Book From ${fromDateStr} To ${thruDateStr}	</fo:block>
              		<fo:block>
	                 	<fo:table border-style="solid">
                    	<fo:table-column column-width="85pt"/>
                    	<fo:table-column column-width="130pt"/>
                    	<fo:table-column column-width="175pt"/>
                    	<fo:table-column column-width="120pt"/>
               	    	<fo:table-column column-width="80pt"/>
            			<fo:table-column column-width="150pt"/> 		
            			<fo:table-column column-width="150pt"/>
            			<fo:table-column column-width="150pt"/>
	                    <fo:table-body>
	                    <fo:table-row >
	                    		<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false" font-weight="bold">Sequence Id</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false" font-weight="bold">PartyName</fo:block> 
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false" font-weight="bold">Payment Details</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false" font-weight="bold">Payment.MethType</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false" font-weight="bold">Inst.No</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">Payment Amount(Rs.)</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">Debit Amount(Rs.)</fo:block>  
                       			</fo:table-cell>
                        		<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">Credit Amount(Rs.)</fo:block>   
                        		</fo:table-cell>
                			</fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block> 		
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
                 	<fo:table border-style="solid">
                    	<fo:table-column column-width="85pt"/>
                    	<fo:table-column column-width="130pt"/>
                    	<fo:table-column column-width="175pt"/>
                    	<fo:table-column column-width="120pt"/>
               	    	<fo:table-column column-width="80pt"/>
            			<fo:table-column column-width="150pt"/> 		
            			<fo:table-column column-width="150pt"/>
            			<fo:table-column column-width="150pt"/>
                    <fo:table-body>
                    	<#assign lineNo = 0>
                    	<#assign oldTransactionDate =  "nodate">
                    	<#assign oldpaymentGroupId =  "no">
                    	<#list dayFinAccountTransList as finAcctngDetails>
                    		<#assign transactionDate = (finAcctngDetails.get("transactionDate")?if_exists)/>
                    		<#assign paymentTransSequenceId = (finAcctngDetails.get("paymentTransSequenceId")?if_exists)/>
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
	                		<#assign finAccountOwnerPartyId = (finAcctngDetails.get("finAccountOwnerPartyId")?if_exists)/>
	                		<#assign finAccountPartyName = (finAcctngDetails.get("finAccountPartyName")?if_exists)/>
	                		<#assign finAccountTypeDes = (finAcctngDetails.get("finAccountTypeDes")?if_exists)/>
	                		<#assign openingBal = (finAcctngDetails.get("openingBal")?if_exists)/>
	                		<#assign closingBal = (finAcctngDetails.get("closingBal")?if_exists)/>
	                		
	                		<#assign paymentGroupId = (finAcctngDetails.get("paymentGroupId")?if_exists)/>
	                		<#assign paymentGroupPartyId = (finAcctngDetails.get("paymentGroupPartyId")?if_exists)/>
	                		<#assign paymentGroupPartyName = (finAcctngDetails.get("paymentGroupPartyName")?if_exists)/>
	                		<#assign paymentGroupComments = (finAcctngDetails.get("paymentGroupComments")?if_exists)/>
	                		<#assign paymentGroupMethodTypeDes = (finAcctngDetails.get("paymentGroupMethodTypeDes")?if_exists)/>
	                		<#assign paymentGroupDescription = (finAcctngDetails.get("paymentGroupDescription")?if_exists)/>
	                		<#assign paymentGroupDebitAmount = (finAcctngDetails.get("paymentGroupDebitAmount")?if_exists)/>
	                		<#assign paymentGroupCreditAmount = (finAcctngDetails.get("paymentGroupCreditAmount")?if_exists)/>
	                		<#assign paymentGroupInstrumentNum = (finAcctngDetails.get("paymentGroupInstrumentNum")?if_exists)/>
	                		
	                		<#assign paymentDebitAmount = (finAcctngDetails.get("paymentDebitAmount")?if_exists)/>
	                		<#assign paymentCreditAmount = (finAcctngDetails.get("paymentCreditAmount")?if_exists)/>
	                		
	                		
	                		<#if ((paymentId)?has_content)>
								<#if (paymentId != "DAY TOTAL")>
								<#if oldTransactionDate == "nodate" ||  oldTransactionDate != transactionDate>
	                		<fo:table-row>
									<fo:table-cell>
	                            		<fo:block  text-align="left" keep-together="always" font-weight = "bold" font-size="12pt" white-space-collapse="false"> 
                                             Date: ${transactionDate?if_exists}
                                      </fo:block>  
	                       			</fo:table-cell>
				       				<fo:table-cell>
				            			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       				</fo:table-cell>
				       				<fo:table-cell>
	                                    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	                                </fo:table-cell>
				       				<fo:table-cell>
				            			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       				</fo:table-cell>
				       				<fo:table-cell>
				            			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       				</fo:table-cell>
				       				<fo:table-cell>
				            			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       				</fo:table-cell>
				       				<fo:table-cell>
	                                    <fo:block text-align="right" font-size="12pt" font-weight = "bold" keep-together="always"> 
	                                        Opening Balance:
	                                    </fo:block>
	                                </fo:table-cell>
	                       			<fo:table-cell>
	                                    <fo:block text-align="right" font-size="12pt" font-weight = "bold" keep-together="always"> 
	                                         <#if openingBal?has_content>${(openingBal)?string("##0.00")}<#else>0.00</#if>
	                                    </fo:block>
	                                </fo:table-cell>
	                       	</fo:table-row>
	                       	<#else>
	                       		<fo:table-row>
		                       		<fo:table-cell>
		                            		<fo:block  text-align="left" keep-together="always" font-size="12pt" white-space-collapse="false"> 
	                                      </fo:block>  
		                       		</fo:table-cell>
		                       	</fo:table-row>
	                       	</#if>
	                       	</#if>
	                       	</#if>
	                       	<fo:table-row>
									<fo:table-cell>
				            			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       				</fo:table-cell>
								</fo:table-row>
	                       <#if oldpaymentGroupId == "no" ||  oldpaymentGroupId != paymentGroupId>
                              <#if paymentGroupId?has_content>
                              <fo:table-row>
	                                <fo:table-cell>
				            				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       				</fo:table-cell>
			       					<fo:table-cell>
			            				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       					</fo:table-cell>
			       					<#if paymentGroupDescription?has_content>
	                                <fo:table-cell  >
	                                    <fo:block text-align="left" font-size="12pt" keep-together = "always">${paymentGroupDescription?if_exists}(${paymentGroupId?if_exists})
	                                    </fo:block>
	                                </fo:table-cell>
	                                <#elseif paymentGroupComments?has_content>
	                                <fo:table-cell  >
	                                    <fo:block text-align="left" font-size="12pt" keep-together = "always">${paymentGroupComments?if_exists}
	                                    </fo:block>
	                                </fo:table-cell>
	                                </#if>
	                                <fo:table-cell  >
	                                    <fo:block text-align="center" font-size="12pt" keep-together = "always">${paymentGroupMethodTypeDes?if_exists}
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell  >
	                                    <fo:block text-align="center" font-size="12pt" keep-together = "always">${paymentGroupInstrumentNum?if_exists}
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                    <fo:block text-align="right" font-size="12pt" keep-together = "always">
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="right" font-size="12pt" keep-together = "always">${paymentGroupDebitAmount?if_exists?string("##0.00")}
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                    <fo:block text-align="right" font-size="12pt" keep-together = "always">${paymentGroupCreditAmount?if_exists?string("##0.00")}
	                                    </fo:block>
	                                </fo:table-cell>
	                                <#assign oldpaymentGroupId = paymentGroupId> 
                              </fo:table-row>
                              </#if>
                              </#if>
								<fo:table-row>
									<#if ((paymentId)?has_content)>
										<#if (paymentId != "DAY TOTAL")>
		                       			<#if paymentTransSequenceId?has_content>
		                       			<fo:table-cell >
		                            		<fo:block  text-align="left" font-size="10pt" white-space-collapse="false"> 
	                                             ${paymentTransSequenceId?if_exists}
	                                      </fo:block>  
		                       			</fo:table-cell>
		                       			<#else>
		                       			<fo:table-cell font-weight="bold">
		                            		<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"> 
	                                      </fo:block>  
		                       			</fo:table-cell>
		                       			</#if>
		                       			<#if (paymentId != "DAY TOTAL")>
                                		<#if finAccountPartyName?has_content>
                                		<fo:table-cell >
                                    		<fo:block text-align="left" font-size="12pt">
                                            	${partyName?if_exists}/${finAccountPartyName?if_exists}<#if ((partyId)?has_content)>(${(partyId)}) 	<#else>(${finAccountOwnerPartyId?if_exists})</#if>
                                            	
                                    		</fo:block>
	                                	</fo:table-cell>
	                                	 <#else>
	                                	 <fo:table-cell >
                                    		<fo:block text-align="left" font-size="12pt">
                                            	${partyName?if_exists}<#if ((partyId)?has_content)>(${(partyId)}) 	<#else>(${finAccountOwnerPartyId?if_exists})</#if>
                                    		</fo:block>
	                                	</fo:table-cell>
	                                	</#if>
		                                <#else>
		                                	<fo:table-cell >
	                                    		<fo:block text-align="left" font-size="12pt">
	                                    		</fo:block>
	                                    	 </fo:table-cell>
	                                	</#if>
		                       			<fo:table-cell >
		                            		<fo:block  text-align="left" font-size="10pt" white-space-collapse="false" keep-together = "always"> 
	                                             ${paymentId?if_exists}/${description?if_exists}
	                                      </fo:block>  
		                       			</fo:table-cell>
		                       			</#if>
	                       			<#else>
	                       			<fo:table-cell >
	                                    <fo:block text-align="center"></fo:block>
	                                </fo:table-cell>
	                                </#if>
	                                <#if paymentGroupId?has_content>
	                                <fo:table-cell >
	                                    <fo:block text-align="center"></fo:block>
	                                </fo:table-cell>
	                                <#else>
                                	<#if ((paymentMethodTypeDes)?has_content)>
                                	<fo:table-cell>
	                                    <fo:block font-size="12pt" text-align="center">${(paymentMethodTypeDes)}<#if finAccountTypeDes?has_content>/${finAccountTypeDes}</#if></fo:block>
	                                </fo:table-cell>
                                 	<#else>
                                 	<fo:table-cell >
	                                    <fo:block text-align="center"></fo:block>
	                                </fo:table-cell>
                                	</#if>
                                	</#if>
                                	<#if ((instrumentNum)?has_content)>
                                	<fo:table-cell >
	                                    <fo:block font-size="12pt" text-align="center">${(instrumentNum)}</fo:block>
	                                </fo:table-cell>
                                 	<#else>
                                 	<fo:table-cell >
	                                    <fo:block text-align="center"></fo:block>
	                                </fo:table-cell>
                                	</#if>
                                	<#if paymentGroupId?has_content>
	                                	<#if (paymentId != "DAY TOTAL")>
		                                	<#if paymentDebitAmount?has_content && paymentDebitAmount !=0>
				                                <fo:table-cell >
				                                    <fo:block text-align="right" font-size="12pt">
				                                          <#if paymentDebitAmount?has_content>${(paymentDebitAmount)?string("##0.00")}<#else>0.00</#if>
				                                    </fo:block>
				                                </fo:table-cell>
				                            <#else>
				                            	<#if paymentCreditAmount?has_content && paymentCreditAmount !=0>
					                                <fo:table-cell>
					                                	<fo:block text-align="right" font-size="12pt">
					                                  		 <#if paymentCreditAmount?has_content>${(paymentCreditAmount)?string("##0.00")}<#else>0.00</#if>
					                                    </fo:block>
					                                </fo:table-cell>
			                                	</#if>
			                          	 </#if>
		                           	</#if>
		                           </#if>
	                                <#if paymentGroupId?has_content>
	                                <fo:table-cell>
	                                    <fo:block text-align="right" font-size="12pt" keep-together = "always">
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                    <fo:block text-align="right" font-size="12pt" keep-together = "always">
	                                    </fo:block>
	                                </fo:table-cell>
	                                <#else>
	                                	<#if (paymentId != "DAY TOTAL")>
	                                	<fo:table-cell>
				            				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       					</fo:table-cell>
		                                <fo:table-cell >
		                                    <fo:block text-align="right" font-size="12pt">
		                                             <#if debitAmount?has_content>${(debitAmount)?string("##0.00")}<#else>0.00</#if>
		                                    </fo:block>
		                                </fo:table-cell>
		                                <fo:table-cell >
		                                    <fo:block text-align="right" font-size="12pt">
		                                             <#if creditAmount?has_content>${(creditAmount)?string("##0.00")}<#else>0.00</#if>
		                                    </fo:block>
		                                </fo:table-cell>
		                                <#else>
					       				<fo:table-cell>
				            				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       					</fo:table-cell>
				       					<fo:table-cell>
				            				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       					</fo:table-cell>
				       					<fo:table-cell>
				            				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       					</fo:table-cell>
					       				<fo:table-cell font-weight="bold">
			                            		<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false"> 
		                                             ${paymentId?if_exists}
		                                      </fo:block>
		                                </fo:table-cell>
		                                <fo:table-cell  font-weight="bold">
		                                    <fo:block text-align="right" font-size="12pt">
		                                             <#if debitAmount?has_content>${(debitAmount)?string("##0.00")}<#else>0.00</#if>
		                                    </fo:block>
		                                </fo:table-cell>
		                                <fo:table-cell  font-weight="bold">
		                                    <fo:block text-align="right" font-size="12pt">
		                                             <#if creditAmount?has_content>${(creditAmount)?string("##0.00")}<#else>0.00</#if>
		                                    </fo:block>
		                                </fo:table-cell>
		                                </#if>
	                                </#if>
                              </fo:table-row>
                              <fo:table-row>
                              <#if ((comments)?has_content)>
                                	<fo:table-cell>
	                                    <fo:block text-align="left" font-weight = "bold" keep-together="always" font-size="12pt">Description: ${(comments)}</fo:block>
	                                </fo:table-cell>
                                 	<#else>
                                 	<fo:table-cell>
	                                    <fo:block text-align="center"></fo:block>
	                                </fo:table-cell>
                                	</#if>
                                	</fo:table-row>
                              <#if (paymentId == "DAY TOTAL")>
                              <fo:table-row>
				       				<fo:table-cell  font-weight="bold">
	                                    <fo:block text-align="right" font-size="12pt" keep-together = "always">
	                                    </fo:block>
	                                </fo:table-cell>
				       				<fo:table-cell  font-weight="bold">
	                                    <fo:block text-align="right" font-size="12pt" keep-together = "always">
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
				            			<fo:block linefeed-treatment="preserve"></fo:block>
				       				</fo:table-cell>
	                                <fo:table-cell  font-weight="bold">
	                                    <fo:block text-align="right" font-size="12pt" keep-together = "always">
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
				            			<fo:block linefeed-treatment="preserve"></fo:block>
				       				</fo:table-cell>
				       				<fo:table-cell>
	                                    <fo:block text-align="center" font-size="12pt" keep-together = "always">
	                                    </fo:block>
	                                </fo:table-cell>
				       				<fo:table-cell>
	                                    <fo:block text-align="right" font-size="12pt" keep-together="always" font-weight = "bold"> 
	                                        Closing Balance:
	                                    </fo:block>
	                                </fo:table-cell>
                              		<fo:table-cell>
	                                    <fo:block text-align="right" font-size="12pt" keep-together="always" font-weight = "bold"> 
	                                      	<#if closingBalance?has_content>${(closingBalance)?string("##0.00")}<#else>0.00</#if>
	                                    </fo:block>
	                                </fo:table-cell>
	                           </fo:table-row>     
							<fo:table-row>
								<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
							</fo:table-row>
                              </#if>
                              <#assign oldTransactionDate = transactionDate>
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
</fo:root>
</#escape>