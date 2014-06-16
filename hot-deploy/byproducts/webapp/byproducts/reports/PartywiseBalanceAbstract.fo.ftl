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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-left=".3in" margin-right=".3in" margin-top=".5in">
                <fo:region-body margin-top="1.7in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "PartywiseLedgerAbstract.pdf")}
        <#if errorMessage?has_content>
	<fo:page-sequence master-reference="main">
	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	   <fo:block font-size="14pt">
	           ${errorMessage}.
        </fo:block>
	</fo:flow>
	</fo:page-sequence>	
	<#else>
       <#if partyWiseLedger?has_content>
       <#assign partyLedger = partyWiseLedger.entrySet()>
       <#list partyLedger as eachPartyLedger>
       <#assign booth = eachPartyLedger.getKey()>        
		        <fo:page-sequence master-reference="main" font-size="10pt">	
		        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
		        		<fo:block text-align="center" font-size="13pt" keep-together="always"  white-space-collapse="false">&#160;${uiLabelMap.KMFDairyHeader}</fo:block>
                    	<fo:block text-align="center" font-size="12pt" keep-together="always"  white-space-collapse="false">&#160;${uiLabelMap.KMFDairySubHeader}</fo:block>
                    	<fo:block text-align="center" font-size="12pt" keep-together="always"  white-space-collapse="false" font-weight="bold">&#160;PARTY LEDGER ABSTRACT FOR: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")} </fo:block>
              			<fo:block font-size="12pt" text-align="left">======================================================================================================================================</fo:block>  
            			<fo:block text-align="left" font-size="12pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160; RETAILER CODE: ${eachPartyLedger.getKey()?if_exists}                                        PARTY NAME:  ${facilityDesc.get(eachPartyLedger.getKey())?if_exists}                  </fo:block>
              			<fo:block font-size="12pt" text-align="left">--------------------------------------------------------------------------------------------------------------------------------------</fo:block>  
            			<fo:block text-align="left" font-size="12pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">&#160;DATE        OPENING        SALES        PROD-RET     RECEIPTS       TYPE/CQ-NO      RET-CQ-NO     RET-AMT      PENALTY      CLOSING</fo:block>
		        		<fo:block font-size="12pt" text-align="left">--------------------------------------------------------------------------------------------------------------------------------------</fo:block>  
		        	</fo:static-content>	        	
		        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
                 	<fo:table>
                    <fo:table-column column-width="50pt"/> 
                    <fo:table-column column-width="100pt"/>
                    <#--<fo:table-column column-width="70pt"/> -->
            		<fo:table-column column-width="100pt"/> 	
            		<fo:table-column column-width="100pt"/>	
            		<fo:table-column column-width="100pt"/>
            		<fo:table-column column-width="100pt"/>
            		<fo:table-column column-width="100pt"/>
            		<fo:table-column column-width="100pt"/>
            		<fo:table-column column-width="100pt"/>
            		<fo:table-column column-width="100pt"/>	
            		<fo:table-column column-width="150pt"/>
                    <fo:table-body>
                    <#assign ledgerDetails = eachPartyLedger.getValue()>
                    	<#list ledgerDetails as eachDateDetail>
	                    <fo:table-row>
	                    	<#assign ob = 0>
				            <#assign saleAmt = 0>
				            <#assign rcpt = 0>
				            <#assign displayFlag = "no">
	                    	<#if eachDateDetail.get("openingBalance")?has_content>
	                    		<#assign ob = eachDateDetail.get("openingBalance")>
	                    	</#if>
	                    	<#if eachDateDetail.get("saleAmount")?has_content>
	                    		<#assign saleAmt = eachDateDetail.get("saleAmount")>
	                    	</#if>
	                    	<#if eachDateDetail.get("receipts")?has_content>
	                    		<#assign rcpt = eachDateDetail.get("receipts")>
	                    	</#if>
	                    	<#if eachDateDetail.get("returnAmt")?has_content>
	                    		<#assign returnAmt = eachDateDetail.get("returnAmt")>
	                    	</#if>
	                    	<#if eachDateDetail.get("penaltyAmt")?has_content>
	                    		<#assign penaltyAmt = eachDateDetail.get("penaltyAmt")>
	                    	</#if>
	                    	<#if eachDateDetail.get("closingBalance")?has_content>
	                    		<#assign closingBalance = eachDateDetail.get("closingBalance")>
	                    	</#if>
	                    	<#if eachDateDetail.get("stDate")?has_content>
	                    		<#assign startDate = eachDateDetail.get("stDate")>
	                    		<#assign displayFlag = "yes">
	                    	</#if>
		                    <fo:table-cell>
				            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${eachDateDetail.get("stDate")?if_exists}</fo:block>  
				            </fo:table-cell>
                        	<fo:table-cell>
                            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false"><#if displayFlag = "yes">${ob?if_exists?string("#0.00")}</#if></fo:block>  
                        	</fo:table-cell>
                        	<#--<fo:table-cell>
                            	<fo:block text-align="right" font-size="8pt"><#if eachDateDetail.get("invoiceId")?has_content></#if></fo:block>  
                        	</fo:table-cell>-->
                        	<fo:table-cell>
		                    	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false"><#if saleAmt !=0>${saleAmt?if_exists?string("#0.00")}</#if></fo:block>  
		                    </fo:table-cell>
				            
				            <#-- <fo:table-cell>
				            	<fo:block text-align="right" font-size="8pt" white-space-collapse="true">${eachDateDetail.get("chequeDate")?if_exists}</fo:block>  
				            </fo:table-cell>-->
				            <#assign prodRetAmt = 0>
				            <#if eachDateDetail.get("prodReturnAmt")?has_content>
				            	<#assign prodRetAmt = eachDateDetail.get("prodReturnAmt")>
				            </#if>
				            <fo:table-cell>
				            	<fo:block text-align="right" font-size="12pt" white-space-collapse="true"><#if (prodRetAmt>0)>${prodRetAmt?if_exists}</#if></fo:block>  
				            </fo:table-cell>
				            <fo:table-cell>
				            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false"><#if rcpt != 0 >${rcpt?if_exists?string("#0.00")}</#if></fo:block>  
				            </fo:table-cell>
				            <fo:table-cell>
				            	<fo:block text-align="right" font-size="12pt" white-space-collapse="true">${eachDateDetail.get("chequeNo")?if_exists}</fo:block>  
				            </fo:table-cell>
				            <fo:table-cell>
				            	<fo:block text-align="right" font-size="12pt" white-space-collapse="true">${eachDateDetail.get("chequeReturn")?if_exists}</fo:block>  
				            </fo:table-cell>
				            <fo:table-cell>
				            	<fo:block text-align="right" font-size="12pt" white-space-collapse="true"><#if returnAmt != 0>${returnAmt?if_exists?string("#0.00")}</#if></fo:block>  
				            </fo:table-cell>
				            <fo:table-cell>
				            	<fo:block text-align="right" font-size="12pt" white-space-collapse="true"><#if penaltyAmt != 0>${penaltyAmt?if_exists?string("#0.00")}</#if></fo:block>  
				            </fo:table-cell>
				            <fo:table-cell>
				               	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${closingBalance?if_exists?string("#0.00")}</fo:block>  
				            </fo:table-cell>
                        </fo:table-row>
                        </#list>
                        <#assign grandTotalMap = boothSummary.get(booth)>
						<fo:table-row>
	                    	<fo:table-cell>
		                		<fo:block font-size="12pt" text-align="left">======================================================================================================================================</fo:block>     
		                	</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
		                    <fo:table-cell>
				            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">SUMMARY::</fo:block>  
				            </fo:table-cell>
                        	<fo:table-cell>
                            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${grandTotalMap.get("periodOB")?if_exists?string("#0.00")}</fo:block>  
                        	</fo:table-cell>
                        	<#--<fo:table-cell>
				            	<fo:block  keep-together="always" text-align="left" font-size="8pt" white-space-collapse="false"></fo:block>  
				            </fo:table-cell>-->
                        	<fo:table-cell>
		                    	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${grandTotalMap.get("totalSale")?if_exists?string("#0.00")}</fo:block>  
		                    </fo:table-cell>
				            <fo:table-cell>
				            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${grandTotalMap.get("totalProdReturn")?if_exists?string("#0.00")}</fo:block>  
				            </fo:table-cell>
				            <fo:table-cell>
				            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${grandTotalMap.get("totalReceipt")?if_exists?string("#0.00")}</fo:block>  
				            </fo:table-cell>
				            <fo:table-cell>
				            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>  
				            </fo:table-cell>
				            <fo:table-cell>
				            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>  
				            </fo:table-cell>
				            <fo:table-cell>
				            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${grandTotalMap.get("totalReturn")?if_exists?string("#0.00")}</fo:block>  
				            </fo:table-cell>
				            <fo:table-cell>
				            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${grandTotalMap.get("totalPenalty")?if_exists?string("#0.00")}</fo:block>  
				            </fo:table-cell>
				            <fo:table-cell>
				               	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${grandTotalMap.get("periodCB")?if_exists?string("#0.00")}</fo:block>  
				            </fo:table-cell>
						</fo:table-row>
						<fo:table-row>
	                    	<fo:table-cell>
		                		<fo:block font-size="12pt" text-align="left">======================================================================================================================================</fo:block>     
		                	</fo:table-cell>
						</fo:table-row>
                    </fo:table-body>
                </fo:table>
              </fo:block> 		
			</fo:flow>
		</fo:page-sequence>
		</#list>>
	<#else>
	<fo:page-sequence master-reference="main">
    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
       		 <fo:block font-size="14pt">
            	${uiLabelMap.NoOrdersFound}.
       		 </fo:block>
    	</fo:flow>
	</fo:page-sequence>	
  </#if>   
 </#if>
 </fo:root>
</#escape>