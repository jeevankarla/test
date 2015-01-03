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
            <fo:simple-page-master master-name="main" page-width="15in" page-height="12in"
                margin-top="0.1in" margin-bottom="0.5in" margin-left="0.3in" margin-right="0.3in">
        <fo:region-body margin-top="1in" margin-bottom="0.5in"/>
        <fo:region-before extent="1.5in"/>
        <fo:region-after extent="1.5in"/>     
            </fo:simple-page-master>
        </fo:layout-master-set>
        <fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">
              <fo:static-content flow-name="xsl-region-before">
              		<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"> &#160;${uiLabelMap.CommonPage}- <fo:page-number/></fo:block>
               </fo:static-content>						
            <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
                    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
          			<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" font-weight="bold"    white-space-collapse="false"> LEDGER EXTRACT FOR THE PERIOD FROM ${fromDate!} - ${thruDate!} </fo:block>
          			<fo:block  keep-together="always"  text-align="left" font-family="Courier,monospace" font-weight="bold" white-space-collapse="false"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
          			<fo:block>---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            	
          			 <#if acctgTransEntryList?has_content>
                    <#assign count=0>
                     <fo:block text-align="center">${uiLabelMap.FormFieldTitle_glAccountId}: ${parameters.glAccountId} </fo:block>
                    <#list acctgTransEntryList as acctgTransEntry>
                    <fo:block text-align="center"><#if count==0>Gl Account Name:${acctgTransEntry.accountName!}</#if></fo:block>
                     <#assign count=count+1>
                     </#list>
                    <fo:block>---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                    <fo:block>
                        <fo:table>
                            <fo:table-column column-width="7%"/>
                            <fo:table-column column-width="6%"/>
                            <fo:table-column column-width="6%"/>
                            <fo:table-column column-width="6%"/>
                            <fo:table-column column-width="15%"/>
                            <fo:table-column column-width="5%"/>
                            <fo:table-column column-width="10%"/>
                            <fo:table-column column-width="5%"/>
                            <fo:table-column column-width="6%"/>
                            <fo:table-column column-width="8%"/>
                            <fo:table-column column-width="8%"/>
                            <fo:table-column column-width="10%"/>
                            <fo:table-column column-width="1%"/>
                            <fo:table-column column-width="9%"/>
                            <fo:table-body>
                               <fo:table-row>
                                <fo:table-cell >
                                    <fo:block text-align="left" font-size="11pt">${uiLabelMap.AccountingAcctgTrans}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell >
                                    <fo:block text-align="left" font-size="11pt">${uiLabelMap.FormFieldTitle_transactionDate}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell >
                                    <fo:block text-align="left" font-size="11pt">${uiLabelMap.FormFieldTitle_invoiceId}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell >
                                    <fo:block text-align="left" font-size="11pt">${uiLabelMap.FormFieldTitle_paymentId} - (${uiLabelMap.AccountingPaymentType})</fo:block>
                                </fo:table-cell>
                                 <fo:table-cell >
                                    <fo:block text-align="left" font-size="11pt">${uiLabelMap.FormFieldTitle_transDescription}</fo:block>
                                </fo:table-cell>
                                 <fo:table-cell >
                                    <fo:block text-align="left" font-size="11pt">${uiLabelMap.CommonPartyId}</fo:block>
                                </fo:table-cell>
                                 <fo:table-cell >
                                    <fo:block text-align="left" font-size="11pt">${uiLabelMap.CommonPartyName}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell >
                                    <fo:block text-align="left" font-size="11pt">${uiLabelMap.AccountingProductId}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell >
                                    <fo:block text-align="left" font-size="11pt">${uiLabelMap.FormFieldTitle_isPosted}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell >
                                    <fo:block text-align="left" font-size="11pt">${uiLabelMap.FormFieldTitle_postedDate}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell >
                                    <fo:block text-align="left" font-size="11pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${uiLabelMap.Debit}(RS)</fo:block>
                                </fo:table-cell>
                                <fo:table-cell >
                                    <fo:block text-align="left" font-size="11pt" >&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${uiLabelMap.Credit}(RS)</fo:block>
                                </fo:table-cell>
                                <fo:table-cell >
                                    <fo:block text-align="left" font-size="11pt">&#160;</fo:block>
                                </fo:table-cell>
                                <fo:table-cell >
                                    <fo:block text-align="left" font-size="11pt">${uiLabelMap.FormFieldTitle_acctgTransTypeId}</fo:block>
                                </fo:table-cell>
                              </fo:table-row>
                              <fo:table-row>
                                <fo:table-cell >
                                   <fo:block>---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                                </fo:table-cell>
                              </fo:table-row>
                             <#assign creditTotal=0>
                             <#assign debitTotal=0>
                             <#assign closingBal=0>
                               <fo:table-row>
                                <fo:table-cell>
	                                  <fo:block   text-align="left" keep-together="always" font-size="11pt" white-space-collapse="false"> Opening Balance</fo:block>
								</fo:table-cell>
                                 <#if openingBal &gt; 0 >
	                                 <fo:table-cell number-columns-spanned="5">
	                                  <fo:block   text-align="right" keep-together="always" font-size="11pt" white-space-collapse="false"> ${openingBal?string("#0.00")}</fo:block>
									 </fo:table-cell>
								 </#if>
								 <#if openingBal &lt; 0>
									 <fo:table-cell number-columns-spanned="7">
	                                  <fo:block  text-align="right" keep-together="always" font-size="11pt" white-space-collapse="false"> ${(openingBal*-1)?string("#0.00")}</fo:block>
									 </fo:table-cell>	
								 </#if>
								</fo:table-row>	  
								<fo:table-row>
	                                <fo:table-cell>
		                                  <fo:block   text-align="left" keep-together="always" font-size="11pt" white-space-collapse="false"> &#160;</fo:block>
									</fo:table-cell>
								</fo:table-row>	
                                <#list acctgTransEntryList as acctgTransEntry>
                                    <fo:table-row>
                                        <fo:table-cell >
                                            <fo:block text-align="left" font-size="11pt">${(acctgTransEntry.acctgTransId)!} - ${(acctgTransEntry.acctgTransEntrySeqId)!}</fo:block>        
                                        </fo:table-cell>
                                        <fo:table-cell >
                                            <fo:block text-align="left" font-size="11pt">
                                            <#if acctgTransEntry.transactionDate?has_content>
                                                ${(acctgTransEntry.transactionDate!)?string("dd/MM/yyyy")!}
                                            </fo:block>
                                            </#if>
                                        </fo:table-cell>
                                        <fo:table-cell >
                                            <fo:block text-align="left" font-size="11pt">
                                                ${(acctgTransEntry.invoiceId)!}
                                            </fo:block>     
                                        </fo:table-cell>
                                        <fo:table-cell >
                                            <fo:block text-align="left" font-size="11pt">
                                                <#if (acctgTransEntry.paymentId)??>
                                                    <#assign paymentType = (delegator.findOne("Payment", {"paymentId" : (acctgTransEntry.paymentId)!}, false)).getRelatedOne("PaymentType")/>
                                                    ${(acctgTransEntry.paymentId)!}<#if (paymentType?has_content)> -(${(paymentType.description)!})</#if>
                                                </#if>
                                            </fo:block>
                                        </fo:table-cell>
                                         <fo:table-cell >
                                            <fo:block text-align="left" font-size="11pt">
                                           <#if acctgTransEntry.invoiceId?has_content>
                                             <#assign invoice = delegator.findOne("Invoice", {"invoiceId" : acctgTransEntry.invoiceId!}, true)>
          						              <#if invoice.description?has_content>
          						                 ${invoice.description}
          						              </#if>
                                            <#elseif acctgTransEntry.paymentId?has_content>
                                               <#assign payment = delegator.findOne("Payment", {"paymentId" : acctgTransEntry.paymentId!}, true)>
                                                <#if payment.comments?has_content>
                                                ${payment.comments}
                                                </#if>
                                            <#elseif acctgTransEntry.finAccountTransId?has_content>
                                             <#assign finAccountTrans = delegator.findOne("FinAccountTrans", {"finAccountTransId" : (acctgTransEntry.finAccountTransId)!}, true)>
									           <#if finAccountTrans.comments?has_content>
									            ${finAccountTrans.comments}
									           </#if>
									         <#elseif acctgTransEntry.transDescription?has_content>
                                               &#160;&#160;&#160;&#160;&#160;&#160;${acctgTransEntry.transDescription}
                                            </#if>
                                            </fo:block>  
                                        </fo:table-cell>
                                          <fo:table-cell>
		                                      <fo:block text-align="left" font-size="11pt"><#if acctgTransEntry.partyId?has_content && organizationPartyId!=acctgTransEntry.partyId>${acctgTransEntry.partyId}</#if></fo:block>
		                                   </fo:table-cell>
					                        <#assign  partyName="">
						            		<#if acctgTransEntry.partyId?has_content && organizationPartyId!=acctgTransEntry.partyId>
						            			<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, acctgTransEntry.partyId!, false)>
						            		</#if>
		                                 <fo:table-cell>
		                                    <fo:block text-align="left" font-size="11pt"><#if partyName?exists>${partyName}</#if></fo:block>
		                                </fo:table-cell>
                                        <fo:table-cell >
                                            <fo:block text-align="left" font-size="11pt">${(acctgTransEntry.productId)!}</fo:block>        
                                        </fo:table-cell>
                                        <fo:table-cell >
                                            <fo:block text-align="left" font-size="11pt">${(acctgTransEntry.isPosted)!}</fo:block>        
                                        </fo:table-cell>
                                        <fo:table-cell >
                                            <fo:block text-align="left" font-size="11pt">
                                                <#if acctgTransEntry.postedDate?has_content>
                                                    <!--<#assign dateFormat = Static["java.text.DateFormat"].LONG>
                                                    <#assign postedDate = Static["java.text.DateFormat"].getDateInstance(dateFormat,locale).format((acctgTransEntry.postedDate)!)>-->
                                                    ${(acctgTransEntry.postedDate!)?string("dd/MM/yyyy")}
                                                </#if>
                                            </fo:block>        
                                        </fo:table-cell>
                                        <#if acctgTransEntry.debitCreditFlag?exists && acctgTransEntry.debitCreditFlag=="C">
                                            <#assign creditTotal = creditTotal+acctgTransEntry.amount>
                                         </#if>
                                         <#if acctgTransEntry.debitCreditFlag?exists && acctgTransEntry.debitCreditFlag=="D">
                                             <#assign debitTotal = debitTotal+acctgTransEntry.amount>
                                         </#if>
                                        <#if (openingBal!=0)>
                                           <#assign closingBal=openingBal+(debitTotal-creditTotal)>
                                        <#else>
                                           <#assign closingBal=debitTotal-creditTotal>
                                        </#if>
                                        <fo:table-cell >
                                            <fo:block text-align="right" font-size="11pt"><#if acctgTransEntry.debitCreditFlag?exists && acctgTransEntry.debitCreditFlag=="D">${acctgTransEntry.amount?string("#0.00")}</#if></fo:block>        
                                        </fo:table-cell>
                                         <fo:table-cell >
                                            <fo:block text-align="right" font-size="11pt"><#if acctgTransEntry.debitCreditFlag?exists && acctgTransEntry.debitCreditFlag=="C">${acctgTransEntry.amount?string("#0.00")}</#if></fo:block>        
                                        </fo:table-cell>
                                         <fo:table-cell >
                                          <fo:block text-align="left" font-size="11pt">&#160;</fo:block>
                                       </fo:table-cell>
                                        <fo:table-cell>
                                            <fo:block text-align="left" font-size="11pt">
                                                <#if (acctgTransEntry.acctgTransTypeId)??>
                                                    <#assign acctgTransType = (delegator.findOne("AcctgTransType", {"acctgTransTypeId" : (acctgTransEntry.acctgTransTypeId)!}, false))!/>
                                                    <#if acctgTransType?has_content>${acctgTransType.description}</#if>
                                                </#if>
                                            </fo:block>        
                                        </fo:table-cell>
                                    </fo:table-row>
                                </#list>
                                <fo:table-row>
								   <fo:table-cell>
                                        <fo:block>---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                                  </fo:table-cell>
                                 </fo:table-row>
                                <fo:table-row>
                                        <fo:table-cell   number-columns-spanned="9">
                                            <fo:block text-align="left" font-size="11pt"></fo:block>        
                                        </fo:table-cell>
                                        <fo:table-cell >
                                            <fo:block text-align="left" font-size="11pt">total</fo:block>        
                                        </fo:table-cell>
                                         <fo:table-cell >
                                            <fo:block text-align="right" font-size="11pt">${debitTotal?string("#0.00")}</fo:block>        
                                        </fo:table-cell>
                                         <fo:table-cell >
                                            <fo:block text-align="right" font-size="11pt">${creditTotal?string("#0.00")}</fo:block>        
                                        </fo:table-cell>
                                        <fo:table-cell  number-columns-spanned="2">
                                            <fo:block text-align="left" font-size="11pt"></fo:block>        
                                        </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>
                         <fo:block>---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                        <fo:table  table-layout="fixed" width="100%" space-before="0.2in">
							 <fo:table-column column-width="10%"/>
							 <fo:table-column column-width="90%"/>	
							 <fo:table-body>
							 <fo:table-row>
								 <fo:table-cell >
								   <fo:block text-align="left" font-size="11pt" white-space-collapse="false" keep-together="always">Closing Balance:</fo:block>
						  		  </fo:table-cell>
						  		  <fo:table-cell >
						  		  <fo:block text-align="left" font-size="11pt">${closingBal?string("#0.00")}</fo:block>
						  		  </fo:table-cell>
							 </fo:table-row>
							 </fo:table-body>
			   		 </fo:table>
                    </fo:block>
                <#else>
                    <fo:block text-align="left">${uiLabelMap.AccountingNoAcctgTransFound}</fo:block>
                </#if>
            </fo:flow>
        </fo:page-sequence>
    </fo:root>
</#escape>
