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
        <#if invoiceApplicationDetailList?has_content>
        <fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">
              <fo:static-content flow-name="xsl-region-before">
              		<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"> &#160;${uiLabelMap.CommonPage}- <fo:page-number/></fo:block>
               </fo:static-content>						
            <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
                    <#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
				    <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
				    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >${reportHeader.description?if_exists} </fo:block>
				    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">${reportSubHeader.description?if_exists}                             </fo:block>	
          			<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" font-weight="bold"    white-space-collapse="false"> Invoice Register Abstract ${dateFrom?if_exists} - ${dateThru?if_exists} </fo:block>
          			<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if> &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
          			<fo:block>---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
 	               
 	               <fo:block>
                        <fo:table>
                            <fo:table-column column-width="8%"/>
                            <fo:table-column column-width="8%"/>
                            <fo:table-column column-width="6%"/>
                            <fo:table-column column-width="10%"/>
                            <fo:table-column column-width="8%"/>
                            <fo:table-column column-width="16%"/>
                            <fo:table-column column-width="6%"/>
                            <fo:table-column column-width="8%"/>
                            <fo:table-column column-width="8%"/>
                            <fo:table-column column-width="6%"/>
                            <fo:table-column column-width="6%"/>
                            <fo:table-column column-width="6%"/>
                            <fo:table-body>
                               <fo:table-row>
                                <fo:table-cell >
                                    <fo:block text-align="left" font-size="11pt">Invoice No</fo:block>
                                </fo:table-cell>
                                <fo:table-cell >
                                    <fo:block text-align="left" font-size="11pt">Seq. No</fo:block>
                                </fo:table-cell>
                                <fo:table-cell >
                                    <fo:block text-align="left" font-size="11pt">Seq. Type</fo:block>
                                </fo:table-cell>
                                <fo:table-cell >
                                    <fo:block text-align="left" font-size="11pt">Invoice Type</fo:block>
                                </fo:table-cell>
                                 <fo:table-cell >
                                    <fo:block text-align="left" font-size="11pt">Invoice Date</fo:block>
                                </fo:table-cell>
                                 <fo:table-cell>
		                            <fo:block text-align="left" font-size="11pt">Party Name</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell >
		                           <fo:block text-align="right" font-size="11pt">Invoice Amount</fo:block>
		                        </fo:table-cell>
                                <fo:table-cell >
                                    <fo:block text-align="center" font-size="11pt">Paid Date</fo:block>
                                </fo:table-cell>
                                 <fo:table-cell >
                                    <fo:block text-align="left" font-size="11pt">Payment Id</fo:block>
                                </fo:table-cell>
                                <fo:table-cell >
                                    <fo:block text-align="left" font-size="11pt">Payment Date</fo:block>
                                </fo:table-cell>
                                <fo:table-cell >
                                    <fo:block text-align="right" font-size="11pt">Payment Amount</fo:block>
                                </fo:table-cell>
                                <fo:table-cell >
                                    <fo:block text-align="right" font-size="11pt">Application Amount</fo:block>
                                </fo:table-cell>
                              </fo:table-row>
                              <fo:table-row>
                                <fo:table-cell >
                                   <fo:block>----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                                </fo:table-cell>
                              </fo:table-row>
								<fo:table-row>
	                                <fo:table-cell>
		                                  <fo:block   text-align="left" keep-together="always" font-size="11pt" white-space-collapse="false"> &#160;</fo:block>
									</fo:table-cell>
								</fo:table-row>	 
								<#assign invoiceTotalAmt = 0>
								<#assign applicationAmt = 0>
                                <#list invoiceApplicationDetailList as eachApp>
                                	<fo:table-row>
                                        <fo:table-cell >
                                            <fo:block text-align="left" font-size="11pt">${eachApp.get('invoiceId')?if_exists}</fo:block>        
                                        </fo:table-cell>
                                         <fo:table-cell >
                                            <fo:block text-align="left" font-size="11pt">${eachApp.get('sequenceId')?if_exists}</fo:block>        
                                        </fo:table-cell>
                                         <fo:table-cell >
                                            <fo:block text-align="left" font-size="11pt">${eachApp.get('sequenceType')?if_exists}</fo:block>        
                                        </fo:table-cell>
                                         <fo:table-cell >
                                            <fo:block text-align="left" font-size="11pt">${eachApp.get('invoiceType')?if_exists}</fo:block>        
                                        </fo:table-cell>
                                         <fo:table-cell >
                                            <fo:block text-align="left" font-size="11pt">${eachApp.get('invoiceDate')?if_exists}</fo:block>        
                                        </fo:table-cell>
                                         <fo:table-cell >
                                            <fo:block text-align="left" font-size="11pt">${eachApp.get('partyName')?if_exists}[${eachApp.get('partyId')?if_exists}]</fo:block>        
                                        </fo:table-cell>
                                         <fo:table-cell >
                                            <fo:block text-align="right" font-size="11pt">${eachApp.get('invoiceAmount')?if_exists?string("##0.00")}</fo:block>        
                                        </fo:table-cell>
                                        <#assign invoiceTotalAmt = invoiceTotalAmt + eachApp.get('invoiceAmount')>
                                        <fo:table-cell >
                                            <fo:block text-align="center" font-size="11pt">${eachApp.get('paidDate')?if_exists}</fo:block>        
                                        </fo:table-cell>
                                         <fo:table-cell >
                                            <fo:block text-align="left" font-size="11pt">${eachApp.get('paymentId')?if_exists}</fo:block>        
                                        </fo:table-cell>
                                         <fo:table-cell >
                                            <fo:block text-align="left" font-size="11pt">${eachApp.get('paymentDate')?if_exists}</fo:block>        
                                        </fo:table-cell>
                                        <#if eachApp.get('paymentAmount')?exists && eachApp.get('paymentAmount')?has_content>
                                        	<fo:table-cell >
                                            	<fo:block text-align="right" font-size="11pt">${eachApp.get('paymentAmount')?string("##0.00")}</fo:block>        
                                        	</fo:table-cell>
                                        <#else>
                                        	<fo:table-cell >
                                           	 <fo:block text-align="right" font-size="11pt"></fo:block>        
                                        	</fo:table-cell>
                                        </#if>
                                        <#if eachApp.get('applicationAmount')?exists && eachApp.get('applicationAmount')?has_content>
                                        	<fo:table-cell >
                                            	<fo:block text-align="right" font-size="11pt">${eachApp.get('applicationAmount')?string("##0.00")}</fo:block>        
                                        	</fo:table-cell>
                                        	<#assign applicationAmt = applicationAmt + eachApp.get('applicationAmount')>
                                        <#else>
                                        	<fo:table-cell >
                                           	 <fo:block text-align="right" font-size="11pt"></fo:block>        
                                        	</fo:table-cell>
                                        </#if>
                                    </fo:table-row>
                                </#list>
                                <fo:table-row>
								   <fo:table-cell>
                                        <fo:block>---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                                  </fo:table-cell>
                                 </fo:table-row>
                                 <fo:table-row>
								   <fo:table-cell number-columns-spanned="6">
                                        <fo:block text-align="center" font-size="11pt" font-weight="bold"> Total :</fo:block>
                                  </fo:table-cell>
                                  <fo:table-cell>
                                        <fo:block text-align="right" font-size="11pt" font-weight="bold">${invoiceTotalAmt?string("##0.00")} </fo:block>
                                  </fo:table-cell>
                                  <fo:table-cell number-columns-spanned="4">
                                        <fo:block text-align="center" font-size="11pt" font-weight="bold"> </fo:block>
                                  </fo:table-cell>
                                  <fo:table-cell>
                                        <fo:block text-align="right" font-size="11pt" font-weight="bold"> ${applicationAmt?string("##0.00")}</fo:block>
                                  </fo:table-cell>
                                 </fo:table-row>
                            </fo:table-body>
                        </fo:table>
                         <fo:block>---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
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
