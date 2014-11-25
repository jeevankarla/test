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
                margin-top="0.1in" margin-bottom="0.5in" margin-left="0.5in" margin-right="0.5in">
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
          			<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
          			 <#if acctgTransEntryList?has_content>
                    <#--<fo:block>${uiLabelMap.AccountingAcctgTransEntriesFor}
                        <#assign partyName = (delegator.findOne("PartyNameView", {"partyId" : organizationPartyId}, false))!>
                        <#if partyName.partyTypeId == "PERSON">
                            ${(partyName.firstName)!} ${(partyName.lastName)!}
                        <#elseif (partyName.partyTypeId)! == "PARTY_GROUP">
                            ${(partyName.groupName)!}
                        </#if>
                    </fo:block>-->
                    <#assign count=0>
                     <fo:block>${uiLabelMap.FormFieldTitle_glAccountId}: ${parameters.glAccountId} </fo:block>
                    <#list acctgTransEntryList as acctgTransEntry>
                    <fo:block><#if count==0>Gl Account Name:${acctgTransEntry.accountName!}</#if></fo:block>
                     <#assign count=count+1>
                     </#list>
                    <fo:block></fo:block>
                    <fo:block>
                        <fo:table>
                            <fo:table-column column-width="20mm"/>
                            <fo:table-column column-width="20mm"/>
                            <#--<fo:table-column column-width="15mm"/>
                            <fo:table-column column-width="35mm"/>
                            <fo:table-column column-width="25mm"/>-->
                            <fo:table-column column-width="25mm"/>
                            <#--<fo:table-column column-width="15mm"/>-->
                            <fo:table-column column-width="25mm"/>
                            <fo:table-column column-width="25mm"/>
                            <fo:table-column column-width="15mm"/>
                            <fo:table-column column-width="25mm"/>
                            <fo:table-column column-width="25mm"/>
                            <fo:table-column column-width="25mm"/>
                            <fo:table-column column-width="25mm"/>
                            <fo:table-column column-width="25mm"/>
                            <#--<fo:table-column column-width="15mm"/>-->
                            <fo:table-column column-width="35mm"/>
                            <fo:table-column column-width="25mm"/>
                             <#-- <fo:table-column column-width="20mm"/>-->
                            <fo:table-header>
                                <fo:table-cell border="1pt solid" border-width=".1mm">
                                    <fo:block text-align="center" font-size="10pt">${uiLabelMap.AccountingAcctgTrans}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="1pt solid" border-width=".1mm">
                                    <fo:block text-align="center" font-size="10pt">${uiLabelMap.FormFieldTitle_transactionDate}</fo:block>
                                </fo:table-cell>
                               <#-- <fo:table-cell border="1pt solid" border-width=".1mm">
                                    <fo:block text-align="center" >${uiLabelMap.FormFieldTitle_glAccountId}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="1pt solid" border-width=".1mm">
                                    <fo:block text-align="center" >${uiLabelMap.FormFieldTitle_glAccountClassId}</fo:block>
                                </fo:table-cell>-->
                                <fo:table-cell border="1pt solid" border-width=".1mm">
                                    <fo:block text-align="center" font-size="10pt">${uiLabelMap.FormFieldTitle_invoiceId}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="1pt solid" border-width=".1mm">
                                    <fo:block text-align="center" font-size="10pt">${uiLabelMap.FormFieldTitle_paymentId} - (${uiLabelMap.AccountingPaymentType})</fo:block>
                                </fo:table-cell>
                                <#--<fo:table-cell border="1pt solid" border-width=".1mm">
                                    <fo:block text-align="center" >${uiLabelMap.FormFieldTitle_workEffortId}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="1pt solid" border-width=".1mm">
                                    <fo:block text-align="center" >${uiLabelMap.FormFieldTitle_shipmentId}</fo:block>
                                </fo:table-cell>-->
                                <#if organizationPartyId?has_content &&(organizationPartyId!="Company") >
                                 <fo:table-cell border="1pt solid" border-width=".1mm">
                                    <fo:block text-align="center" font-size="10pt">${parameters.partyId}</fo:block>
                                </fo:table-cell>
                                 <fo:table-cell border="1pt solid" border-width=".1mm">
                                    <fo:block text-align="center" font-size="10pt">${uiLabelMap.CommonPartyName}</fo:block>
                                </fo:table-cell>
                                 </#if>
                                <fo:table-cell border="1pt solid" border-width=".1mm">
                                    <fo:block text-align="center" font-size="10pt">${uiLabelMap.AccountingProductId}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="1pt solid" border-width=".1mm">
                                    <fo:block text-align="center" font-size="10pt">${uiLabelMap.FormFieldTitle_isPosted}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="1pt solid" border-width=".1mm">
                                    <fo:block text-align="center" font-size="10pt">${uiLabelMap.FormFieldTitle_postedDate}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="1pt solid" border-width=".1mm">
                                    <fo:block text-align="center" font-size="10pt">${uiLabelMap.Debit}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="1pt solid" border-width=".1mm">
                                    <fo:block text-align="center" font-size="10pt" >${uiLabelMap.Credit}</fo:block>
                                </fo:table-cell>
                                <#--<fo:table-cell border="1pt solid" border-width=".1mm">
                                <fo:block text-align="center" >${uiLabelMap.AccountingAmount}</fo:block>
                                </fo:table-cell>-->
                                 <fo:table-cell border="1pt solid" border-width=".1mm">
                                <fo:block text-align="center" font-size="10pt">${uiLabelMap.FormFieldTitle_transDescription}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="1pt solid" border-width=".1mm">
                                    <fo:block text-align="center" font-size="10pt">${uiLabelMap.FormFieldTitle_acctgTransTypeId}</fo:block>
                                </fo:table-cell>
                                <#--<fo:table-cell border="1pt solid" border-width=".1mm">
                                    <fo:block text-align="center" >${uiLabelMap.FormFieldTitle_glFiscalTypeId}</fo:block>
                                </fo:table-cell>-->
                            </fo:table-header>
                            <fo:table-body>
                            <#assign gTotal=0>
                                <#list acctgTransEntryList as acctgTransEntry>
                                <#assign gTotal=gTotal+acctgTransEntry.amount>
                                    <fo:table-row>
                                        <fo:table-cell border="1pt solid" border-width=".1mm">
                                            <fo:block text-align="center" font-size="10pt">${(acctgTransEntry.acctgTransId)!} - ${(acctgTransEntry.acctgTransEntrySeqId)!}</fo:block>        
                                        </fo:table-cell>
                                        <fo:table-cell border="1pt solid" border-width=".1mm">
                                            <fo:block text-align="center" font-size="10pt">
                                            <#if acctgTransEntry.transactionDate?has_content>
                                           <#-- <#assign dateFormat = Static["java.text.DateFormat"].LONG/>
                                                <#assign transactionDate = Static["java.text.DateFormat"].getDateInstance(dateFormat, locale).format((acctgTransEntry.transactionDate)!)/>-->
                                                ${(acctgTransEntry.transactionDate!)?string("dd/MM/yyyy")!}
                                            </fo:block>
                                            </#if>
                                        </fo:table-cell>
                                        <#--<fo:table-cell border="1pt solid" border-width=".1mm">
                                            <fo:block text-align="center" font-size="10pt">${(acctgTransEntry.glAccountId)!}</fo:block>        
                                        </fo:table-cell>
                                         <fo:table-cell border="1pt solid" border-width=".1mm">
                                            <fo:block text-align="center" font-size="10pt">
                                                <#if (acctgTransEntry.glAccountClassId)??>
                                                    <#assign glAccountClass = (delegator.findOne("GlAccountClass", {"glAccountClassId" : (acctgTransEntry.glAccountClassId)!}, false))!/>
                                                    <#if (glAccountClass?has_content)>${(glAccountClass.description)!}</#if>
                                                </#if>
                                            </fo:block>        
                                        </fo:table-cell>-->
                                        <fo:table-cell border="1pt solid" border-width=".1mm">
                                            <fo:block text-align="center" font-size="10pt">
                                                ${(acctgTransEntry.invoiceId)!}
                                            </fo:block>     
                                        </fo:table-cell>
                                        <fo:table-cell border="1pt solid" border-width=".1mm">
                                            <fo:block text-align="center" font-size="10pt">
                                                <#if (acctgTransEntry.paymentId)??>
                                                    <#assign paymentType = (delegator.findOne("Payment", {"paymentId" : (acctgTransEntry.paymentId)!}, false)).getRelatedOne("PaymentType")/>
                                                    ${(acctgTransEntry.paymentId)!}<#if (paymentType?has_content)> -(${(paymentType.description)!})</#if>
                                                </#if>
                                            </fo:block>
                                        </fo:table-cell>
                                       <#-- <fo:table-cell border="1pt solid" border-width=".1mm">
                                            <fo:block text-align="center" font-size="10pt">${(acctgTransEntry.workEffortId)!}</fo:block>        
                                        </fo:table-cell>
                                         <fo:table-cell border="1pt solid" border-width=".1mm">
                                            <fo:block text-align="center" font-size="10pt">
                                                ${(acctgTransEntry.shipmentId)!}
                                            </fo:block>    
                                        </fo:table-cell> -->   
                                        <#if organizationPartyId?has_content &&(organizationPartyId!="Company") >
                                        <fo:table-cell border="1pt solid" border-width=".1mm">
                                            <fo:block text-align="center" font-size="10pt">${(acctgTransEntry.partyId)!}</fo:block>    
                                        </fo:table-cell>
					                     <#assign name = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, acctgTransEntry.partyId?if_exists, false)>
                         				<fo:table-cell border="1pt solid" border-width=".1mm">
                                            <fo:block text-align="center" font-size="10pt"><#if name?has_content> ${name?if_exists}</#if></fo:block>    
                                        </fo:table-cell>
                                         </#if>
                                        <fo:table-cell border="1pt solid" border-width=".1mm">
                                            <fo:block text-align="center" font-size="10pt">${(acctgTransEntry.productId)!}</fo:block>        
                                        </fo:table-cell>
                                        <fo:table-cell border="1pt solid" border-width=".1mm">
                                            <fo:block text-align="center" font-size="10pt">${(acctgTransEntry.isPosted)!}</fo:block>        
                                        </fo:table-cell>
                                        <fo:table-cell border="1pt solid" border-width=".1mm">
                                            <fo:block text-align="center" font-size="10pt">
                                                <#if acctgTransEntry.postedDate?has_content>
                                                    <!--<#assign dateFormat = Static["java.text.DateFormat"].LONG>
                                                    <#assign postedDate = Static["java.text.DateFormat"].getDateInstance(dateFormat,locale).format((acctgTransEntry.postedDate)!)>-->
                                                    ${(acctgTransEntry.postedDate!)?string("dd/MM/yyyy")}
                                                </#if>
                                            </fo:block>        
                                        </fo:table-cell>
                                        <fo:table-cell border="1pt solid" border-width=".1mm">
                                            <fo:block text-align="center" font-size="10pt"><#if acctgTransEntry.debitCreditFlag?exists && acctgTransEntry.debitCreditFlag=="D"><@ofbizCurrency amount=(acctgTransEntry.amount)! isoCode=(acctgTransEntry.currencyUomId)!/></#if></fo:block>        
                                        </fo:table-cell>
                                         <fo:table-cell border="1pt solid" border-width=".1mm">
                                            <fo:block text-align="center" font-size="10pt"><#if acctgTransEntry.debitCreditFlag?exists && acctgTransEntry.debitCreditFlag=="C"><@ofbizCurrency amount=(acctgTransEntry.amount)! isoCode=(acctgTransEntry.currencyUomId)!/></#if></fo:block>        
                                        </fo:table-cell>
                                       <#-- <fo:table-cell border="1pt solid" border-width=".1mm">
                                            <fo:block text-align="center" font-size="10pt"><#if acctgTransEntry.amount?exists><@ofbizCurrency amount=(acctgTransEntry.amount)! isoCode=(acctgTransEntry.currencyUomId)!/></#if></fo:block>        
                                        </fo:table-cell>-->
                                         <fo:table-cell border="1pt solid" border-width=".1mm">
                                            <fo:block text-align="center" font-size="10pt">
                                           <#if acctgTransEntry.invoiceId?has_content>
                                             <#assign invoice = delegator.findOne("Invoice", {"invoiceId" : acctgTransEntry.invoiceId!}, true)>
          						              <#if invoice.comments?has_content>
          						                 ${invoice.comments}
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
                                               ${acctgTransEntry.transDescription}
                                            </#if>
                                            </fo:block>        
                                        </fo:table-cell>
                                        <fo:table-cell border="1pt solid" border-width=".1mm">
                                            <fo:block text-align="center" font-size="10pt">
                                                <#if (acctgTransEntry.acctgTransTypeId)??>
                                                    <#assign acctgTransType = (delegator.findOne("AcctgTransType", {"acctgTransTypeId" : (acctgTransEntry.acctgTransTypeId)!}, false))!/>
                                                    <#if acctgTransType?has_content>${acctgTransType.description}</#if>
                                                </#if>
                                            </fo:block>        
                                        </fo:table-cell>
                                       <#-- <fo:table-cell border="1pt solid" border-width=".1mm">
                                            <fo:block text-align="left" font-size="10pt">
                                                <#if (acctgTransEntry.glFiscalTypeId)??>
                                                    <#assign glFiscalType = (delegator.findOne("GlFiscalType", {"glFiscalTypeId" : (acctgTransEntry.glFiscalTypeId)!}, false))!/>
                                                    ${(glFiscalType.description)!}
                                                </#if>
                                            </fo:block>        
                                        </fo:table-cell>-->
                                    </fo:table-row>
                                </#list>
                            </fo:table-body>
                        </fo:table>
                    </fo:block>
                <#else>
                    <fo:block text-align="center">${uiLabelMap.AccountingNoAcctgTransFound}</fo:block>
                </#if>
            </fo:flow>
        </fo:page-sequence>
    </fo:root>
</#escape>
