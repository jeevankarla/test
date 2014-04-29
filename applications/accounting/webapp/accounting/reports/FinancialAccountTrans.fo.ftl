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
    <#if financialAcctgTransList?has_content>
        <fo:table border="1pt solid" border-width=".1mm" width="19cm">
            <fo:table-column column-number="1" column-width="100%"/>
            <fo:table-header>
                <fo:table-cell border="1pt solid" border-width=".1mm">
                    <fo:block text-align="center" font-style="normal">${uiLabelMap.AccountingSubsidiaryLedger}</fo:block>
                </fo:table-cell>
            </fo:table-header>
            <fo:table-body>
                <fo:table-row>
                    <fo:table-cell width="19cm" border="1pt solid" border-width=".1mm">
                        <fo:block text-align="start" font-weight="normal">${uiLabelMap.FormFieldTitle_companyName} : ${(currentOrganization.groupName)!}</fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                    <fo:table-cell width="16cm" border="1pt solid" border-width=".1mm">
                        <fo:block text-align="start">${uiLabelMap.AccountingTimePeriod} : ${(currentTimePeriod.fromDate)!} ${uiLabelMap.CommonTo} ${(currentTimePeriod.thruDate)!}</fo:block>        
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                    <fo:table-cell border="1pt solid" border-width=".1mm">
                        <fo:block text-align="start" space-after=".01in">${uiLabelMap.AccountingGlAccountNameAndGlAccountCode} : ${(glAccount.accountCode)!} - ${(glAccount.accountName)!}</fo:block><fo:block text-align="right"> ${uiLabelMap.CommonPage} - <fo:page-number-citation ref-id="theEnd"/></fo:block>        
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
        <fo:table border="1pt solid" border-width=".1mm" width="19cm">
            <fo:table-column/>
            <fo:table-column/>
            <fo:table-column/>
            <fo:table-column/>
            <fo:table-column/>
            <fo:table-column/>
            <fo:table-column/>
            <#--<fo:table-column/>
            <fo:table-column/>
            <fo:table-column/>
            <fo:table-column/>-->
            <fo:table-header>
                <fo:table-cell border="1pt solid" border-width=".1mm">
                    <fo:block text-align="center">${uiLabelMap.FormFieldTitle_transactionDate}</fo:block>
                </fo:table-cell>
                <fo:table-cell border="1pt solid" border-width=".1mm">
                    <fo:block text-align="center">Payment Id</fo:block>
                </fo:table-cell>
                <fo:table-cell border="1pt solid" border-width=".1mm">
                    <fo:block text-align="center">Invoice Type</fo:block>
                </fo:table-cell>
                <fo:table-cell border="1pt solid" border-width=".1mm">
                    <fo:block text-align="center">Opening Balance</fo:block>
                </fo:table-cell>
                <#--<fo:table-cell border="1pt solid" border-width=".1mm">
                    <fo:block text-align="center">${uiLabelMap.AccountingTypeOfTheCurrency}</fo:block>
                </fo:table-cell>
                <fo:table-cell border="1pt solid" border-width=".1mm">
                    <fo:block text-align="center">${uiLabelMap.AccountingOriginalCurrency}</fo:block>
                </fo:table-cell>-->
                <fo:table-cell border="1pt solid" border-width=".1mm">
                    <fo:block text-align="center">${uiLabelMap.AccountingDebitAmount}</fo:block>
                </fo:table-cell>
                <fo:table-cell border="1pt solid" border-width=".1mm">
                    <fo:block text-align="center">${uiLabelMap.AccountingCreditAmount}</fo:block>
                </fo:table-cell>
                <fo:table-cell border="1pt solid" border-width=".1mm">
                    <fo:block text-align="center">Closing Balance</fo:block>
                </fo:table-cell>
            </fo:table-header>
            <fo:table-body>
                <#list financialAcctgTransList as finAcctngDetails>
                		<#assign transactionDate = (finAcctngDetails.get("transactionDate")?if_exists)/>
                		<#assign paymentId = (finAcctngDetails.get("paymentId")?if_exists)/>
                		<#assign invoiceItemType = (finAcctngDetails.get("invoiceItemType")?if_exists)/>
                		<#assign openingBalance = (finAcctngDetails.get("openingBalance")?if_exists)/>
                		<#assign debitAmount = (finAcctngDetails.get("debitAmount")?if_exists)/>
                		<#assign creditAmount = (finAcctngDetails.get("creditAmount")?if_exists)/>
                		<#assign closingBalance = (finAcctngDetails.get("closingBalance")?if_exists)/>
                		
                            <fo:table-row border="1pt solid" border-width=".1mm">
                                <fo:table-cell border="1pt solid" border-width=".1mm">
                                    <fo:block text-align="center">
                                            ${transactionDate?if_exists}
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="1pt solid" border-width=".1mm">
                                    <fo:block text-align="center">
                                             ${paymentId?if_exists}
                                    </fo:block>
                                </fo:table-cell>
                                <#if ((invoiceItemType)?has_content)>
                                	<fo:table-cell  border="1pt solid" border-width=".1mm">
	                                    <fo:block text-align="center">${(invoiceItemType)}</fo:block>
	                                </fo:table-cell>
                                 <#else>
                                 	<fo:table-cell  border="1pt solid" border-width=".1mm">
	                                    <fo:block text-align="center"></fo:block>
	                                </fo:table-cell>
                                </#if>
                                <fo:table-cell border="1pt solid" border-width=".1mm">
                                    <fo:block text-align="right">
                                            <#if openingBalance?has_content>${(openingBalance)?string("##0.0")}<#else>0.0</#if>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="1pt solid" border-width=".1mm">
                                    <fo:block text-align="right">
                                             <#if debitAmount?has_content>${(debitAmount)?string("##0.0")}<#else>0.0</#if>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="1pt solid" border-width=".1mm">
                                    <fo:block text-align="right">
                                             <#if creditAmount?has_content>${(creditAmount)?string("##0.0")}<#else>0.0</#if>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="1pt solid" border-width=".1mm">
                                    <fo:block text-align="right">
                                            <#if closingBalance?has_content>${(closingBalance)?string("##0.0")}<#else>0.0</#if>
                                    </fo:block>
                                </fo:table-cell>
                              </fo:table-row>
                          </#list>
            </fo:table-body>
        </fo:table>
    <#else>
        ${uiLabelMap.CommonNoRecordFound}
    </#if>
</#escape>
