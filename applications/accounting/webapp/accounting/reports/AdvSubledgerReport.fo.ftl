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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="13in"
            margin-top="0.2in" margin-bottom=".3in" margin-left=".7in" margin-right=".5in">
        <fo:region-body margin-top="1.6in"/>
        <fo:region-before extent="1.5in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "abstractReport.pdf")}
 <#if partyPaymentDetailsMap?has_content> 
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION  LTD</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">UNIT: MOTHER DAIRY: G.K.V.K POST,YELAHANKA,BANGALORE:560065</fo:block>
                    <fo:block text-align="center" font-size="12pt" keep-together="always"  white-space-collapse="false" font-weight="bold">${paymentType.get("description")} Detailed Report From ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} To ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}	</fo:block>
              		<fo:block text-align="left" font-size="12pt" keep-together="always"  white-space-collapse="false" font-weight="bold">GL ACCOUNT CODE: ${GlAccount.get("accountCode")} &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;	${GlAccount.get("accountName")}</fo:block>
              		
              		<fo:block>
	                 	<fo:table border-style="solid">
	                    <fo:table-column column-width="110pt"/>
	                    <fo:table-column column-width="200pt"/>
	                    <fo:table-column column-width="60pt"/>  
	               	    <fo:table-column column-width="60pt"/>
	            		<fo:table-column column-width="60pt"/> 		
	            		<fo:table-column column-width="150pt"/>
	            		<fo:table-column column-width="100pt"/>
	            		<fo:table-column column-width="100pt"/>
	                    <fo:table-body>
	                    <fo:table-row >
	                    		<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false" font-weight="bold">Date</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false" font-weight="bold">Particulars </fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false" font-weight="bold">Invoice</fo:block>
                            		<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false" font-weight="bold">No </fo:block>
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false" font-weight="bold">Payment</fo:block>
                            		<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false" font-weight="bold"> Id</fo:block> 
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false" font-weight="bold">Payment</fo:block>
                            		<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false" font-weight="bold"> Method</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false" font-weight="bold">FinAccount</fo:block>
                            		<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false" font-weight="bold"> TransId</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false" font-weight="bold">Debit</fo:block>   
                        		</fo:table-cell>
                        		<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false" font-weight="bold">Credit</fo:block>  
                        		</fo:table-cell>
                			</fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block> 		
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
                 	<fo:table border-style="solid">
                    <fo:table-column column-width="110pt"/>
                    <fo:table-column column-width="200pt"/>
                    <fo:table-column column-width="60pt"/>  
               	    <fo:table-column column-width="60pt"/>
            		<fo:table-column column-width="60pt"/> 		
            		<fo:table-column column-width="150pt"/>
            		<fo:table-column column-width="100pt"/>
            		<fo:table-column column-width="100pt"/>
                    <fo:table-body>
	                	<#assign partyAdvanceDetails = partyPaymentDetailsMap.entrySet()>	
	                	<#list partyAdvanceDetails as partyPayments>
							<#assign partyId = partyPayments.getKey()>
							<#assign partyOpeningBalance = partyPaymentsMap.get(partyId)>
							
							<#assign paymentDetailsList = partyPayments.getValue()>
							
							<#assign creditTotal = partyOpeningBalance.get("openingBalance").get("debit")>
							<#assign debitTotal = partyOpeningBalance.get("openingBalance").get("credit")>
							
								<fo:table-row border-style="solid">
									<fo:table-cell>
	                            		<fo:block  text-align="left" wrap-option="wrap" font-size="13pt" white-space-collapse="false" font-weight="bold"> 
                                            PartyId: ${partyId?if_exists}
                                      	</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="left" wrap-option="wrap" font-size="13pt" white-space-collapse="false"> 
                                             ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)}
                                      	</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                             -
                                      	</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                             -
                                      	</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                             -
                                      	</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false" font-weight="bold"> 
                                             Opening Balance:
                                      	</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="right" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                             ${partyOpeningBalance.get("openingBalance").get("debit")?if_exists?string("#0.00")}
                                      	</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="right" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                             ${partyOpeningBalance.get("openingBalance").get("credit")?if_exists?string("#0.00")}
                                      	</fo:block>  
	                       			</fo:table-cell>
								</fo:table-row>
								<#list paymentDetailsList as eachPaymentDetails>
									
									<#if eachPaymentDetails.get("paymentId")?has_content>
										<#assign paymentId = eachPaymentDetails.get("paymentId")>
										<#assign paymentGenericValue = (paymentDetailsMap.get(paymentId)).get(0)>
										<fo:table-row border-style="solid">
											<fo:table-cell border-style="solid">
			                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
		                                           ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(paymentGenericValue.get("paymentDate"), "dd/MM/yyyy")}
		                                      </fo:block>  
			                       			</fo:table-cell>
			                       			<fo:table-cell border-style="solid">
			                            		<fo:block  text-align="left" font-size="13pt" wrap-option="wrap" white-space-collapse="false"> 
			                            			${paymentGenericValue.get("comments")?if_exists} <#if paymentGenericValue.get("paymentRefNum")?has_content> :${paymentGenericValue.get("paymentRefNum")?if_exists} </#if>
		                                      	</fo:block>  
			                       			</fo:table-cell>
			                       			<fo:table-cell border-style="solid">
			                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
		                                      		
		                                      	</fo:block>  
			                       			</fo:table-cell>
			                       			<fo:table-cell border-style="solid">
			                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false">
			                            			${paymentGenericValue.get("paymentId")?if_exists} 
		                                      	</fo:block>  
			                       			</fo:table-cell>
			                       			<#assign paymentMethod = delegator.findOne("PaymentMethod", {"paymentMethodId" : paymentGenericValue.get("paymentMethodId")}, true)>
			                       			<fo:table-cell border-style="solid">
			                            		<fo:block  text-align="left" wrap-option="wrap" font-size="13pt" white-space-collapse="false"> 
			                            			${paymentMethod.get("description")?if_exists} 
		                                      	</fo:block>  
			                       			</fo:table-cell>
			                       			<fo:table-cell border-style="solid">
			                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
			                            			 ${finTransSeqMap.get(paymentGenericValue.get("finAccountTransId"))?if_exists} 
		                                      	</fo:block>
		                                    </fo:table-cell>
		                                    <#if (paymentGenericValue.get("paymentTypeId")).indexOf("PAYOUT") != -1>
			                                      <#assign debitTotal = debitTotal + paymentGenericValue.get("amount")>
			                                      <fo:table-cell border-style="solid">
				                            		 <fo:block  text-align="right" keep-together="always" font-size="13pt" white-space-collapse="false"> 
			                                      	 	${paymentGenericValue.get("amount")?if_exists?string("#0.00")}
			                                      	 </fo:block>
			                                      </fo:table-cell>
			                                      <fo:table-cell border-style="solid">
				                            		 <fo:block  text-align="right" keep-together="always" font-size="13pt" white-space-collapse="false"> 
			                                      	 	0.00
			                                      	 </fo:block>
			                                      </fo:table-cell>	
			                                      <#else>
			                                      <#assign creditTotal = creditTotal + paymentGenericValue.get("amount")>
			                                      <fo:table-cell border-style="solid">
				                            		 <fo:block  text-align="right" keep-together="always" font-size="13pt" white-space-collapse="false"> 
			                                      	 	0.00
			                                      	 </fo:block>
			                                      </fo:table-cell>
			                                      <fo:table-cell border-style="solid">
				                            		 <fo:block  text-align="right" keep-together="always" font-size="13pt" white-space-collapse="false"> 
			                                      	 	${paymentGenericValue.get("amount")?if_exists}
			                                      	 </fo:block>
			                                      </fo:table-cell>
			                                      
		                                      </#if>
			                       		</fo:table-row>	
									</#if>
									<#if eachPaymentDetails.get("invoiceId")?has_content>
										<#assign invoiceId = eachPaymentDetails.get("invoiceId")>
										<#assign invoiceList = (invoiceDetailsMap.get(invoiceId))>
										<#list invoiceList as eachInvItem>
											<fo:table-row border-style="solid">
												<fo:table-cell border-style="solid">
				                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
			                                           ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(eachInvItem.get("invoiceDate"), "dd/MM/yyyy")}
			                                      </fo:block>  
				                       			</fo:table-cell>
				                       			<fo:table-cell border-style="solid">
				                            		<fo:block  text-align="left" wrap-option="wrap" font-size="13pt" white-space-collapse="false"> 
				                            			${eachInvItem.get("description")?if_exists}
			                                      	</fo:block>  
				                       			</fo:table-cell>
				                       			<fo:table-cell border-style="solid">
				                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
			                                      		${eachInvItem.get("invoiceId")?if_exists}
			                                      	</fo:block>  
				                       			</fo:table-cell>
				                       			<fo:table-cell border-style="solid">
				                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
			                                      </fo:block>  
				                       			</fo:table-cell><fo:table-cell border-style="solid">
				                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
			                                      </fo:block>  
				                       			</fo:table-cell><fo:table-cell border-style="solid">
				                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
			                                      </fo:block>  
				                       			</fo:table-cell><fo:table-cell border-style="solid">
				                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
			                                      </fo:block>  
				                       			</fo:table-cell>
				                       			<#assign quantity = 1>
				                       			<#if eachInvItem.get("quantity")?has_content>
				                       				<#assign quantity = eachInvItem.get("quantity")>
				                       			</#if>
				                       			<#assign invAmount = (eachInvItem.get("amount"))*(quantity)>
				                       			<#assign creditTotal = creditTotal + invAmount>
				                       			<fo:table-cell border-style="solid">
				                            		<fo:block  text-align="right" keep-together="always" font-size="13pt" white-space-collapse="false"> 
			                                      		${invAmount?string("#0.00")}
			                                      	</fo:block>  
				                       			</fo:table-cell>
											</fo:table-row>
										</#list>
									</#if>
	                			</#list>
	                			
	                			<fo:table-row border-style="solid">
									<fo:table-cell>
	                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                      	</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                      	</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                      	</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                      	</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                      	</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false" font-weight="bold"> 
                                             Transaction Tot:
                                      	</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="right" keep-together="always" font-size="13pt" white-space-collapse="false" font-weight="bold"> 
                                             ${debitTotal?string("#0.00")}
                                      	</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="right" keep-together="always" font-size="13pt" white-space-collapse="false" font-weight="bold"> 
                                             ${creditTotal?string("#0.00")}
                                      	</fo:block>  
	                       			</fo:table-cell>
								</fo:table-row>	
								<fo:table-row border-style="solid">
									<fo:table-cell>
	                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                      	</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                      	</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                      	</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                      	</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                      	</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false" font-weight="bold"> 
                                             Closing Balance:
                                      	</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="right" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                             ${partyOpeningBalance.get("closingBalance").get("debit")?if_exists?string("#0.00")}
                                      	</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="right" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                             ${partyOpeningBalance.get("closingBalance").get("credit")?if_exists?string("#0.00")}
                                      	</fo:block>  
	                       			</fo:table-cell>
								</fo:table-row>
						</#list>		
                    </fo:table-body>
                </fo:table>
               </fo:block> 		
			 </fo:flow>
			 </fo:page-sequence>	
			  <#else>
    	<fo:page-sequence master-reference="main">
		<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			<fo:block font-size="14pt">
	            	No Records Found For The Given Duration!
	       		 </fo:block>
		</fo:flow>
	</fo:page-sequence>	
    </#if>  
</fo:root>
</#escape>