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
            <#--><fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-right=".1in" margin-top=".5in"> -->
              <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
                     margin-left=".3in" margin-right=".1in">
                <fo:region-body margin-top="1.5in"/>
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
       <#if partyDayWiseDetailMap?has_content>
       <#assign partyLedgerList = partyDayWiseDetailMap.entrySet()>
		        <fo:page-sequence master-reference="main" font-size="12pt">	
		        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
		        <!--	<fo:block text-align="center" font-size="13pt" keep-together="always"  white-space-collapse="false">&#160;${uiLabelMap.KMFDairyHeader}</fo:block>
                    	<fo:block text-align="center" font-size="12pt" keep-together="always"  white-space-collapse="false">&#160;${uiLabelMap.KMFDairySubHeader}</fo:block> -->
                    	<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "DEPOT_SALES","propertyName" : "reportHeaderLable"}, true)>
        				<#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "DEPOT_SALES","propertyName" : "reportSubHeaderLable"}, true)>
        				<fo:block keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${reportHeader.description?if_exists}</fo:block>				
                    	<fo:block keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${reportSubHeader.description?if_exists}</fo:block>
                    	<fo:block text-align="center" font-size="12pt" keep-together="always"  white-space-collapse="false" font-weight="bold">&#160;SUBLEDGER REPORT FOR THE PERIOD FROM: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} to ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")} </fo:block>
              			<fo:block font-size="11pt" text-align="left">=======================================================================================================</fo:block> 
	                    <#--><#assign  partyName="">
            			<#if parameters.partyId?exists>
            			<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, parameters.partyId, false)>
            			</#if>
            			<fo:block text-align="left" font-size="12pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160; PARTY CODE: ${parameters.partyId?if_exists}                  PARTY NAME:  ${partyName?if_exists}                  </fo:block>
              			<fo:block font-size="11pt" text-align="left">-------------------------------------------------------------------------------------------------------</fo:block>  
            			<fo:block text-align="left" font-size="12pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">&#160;                INVOICE-INFO                          PAYMENT-INFO       </fo:block> -->
		        	<fo:block>
                 	<fo:table>
                    <fo:table-column column-width="90pt"/>
            		<fo:table-column column-width="230pt"/> 	
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="70pt"/>	
            		<fo:table-column column-width="70pt"/>
                    <fo:table-body>
                    <fo:table-row>
		                    <fo:table-cell>
				            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">DATE</fo:block>  
				            </fo:table-cell>
				            <fo:table-cell>
				            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">Particulars</fo:block>  
				            </fo:table-cell>
				             <fo:table-cell>
				            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">Invoice</fo:block> 
				            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">&#160;&#160;Id</fo:block>   
				            </fo:table-cell>
				            <#-->
				            <fo:table-cell>
		                    	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">Type</fo:block>  
		                    </fo:table-cell> -->
				             <fo:table-cell>
				            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">Payment</fo:block> 
				            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">&#160;&#160;Id</fo:block>  
				            </fo:table-cell>
				             <fo:table-cell>
				            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">Debit</fo:block>  
				            </fo:table-cell>
				            <fo:table-cell>
		                    	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">Credit</fo:block>  
		                    </fo:table-cell>
		                    <fo:table-cell>
		                    	<fo:block text-align="right" font-size="12pt" white-space-collapse="false">Closing Balance</fo:block>  
		                    </fo:table-cell>
                        </fo:table-row>
                       </fo:table-body>
                </fo:table>
              </fo:block> 
              <fo:block font-size="11pt" text-align="left">-------------------------------------------------------------------------------------------------------</fo:block>	
		        	</fo:static-content>	        	
		        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
                 	<fo:table>
                     <fo:table-column column-width="90pt"/>
            		<fo:table-column column-width="230pt"/> 	
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="70pt"/>	
            		<fo:table-column column-width="70pt"/>
                    <fo:table-body>
                    <#list partyLedgerList as eachPartyLedger >
                         <#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, eachPartyLedger.getKey(), false)>
                         <#if openingBalanceMap?has_content>
                         <#assign partDebitTotal=0>
                         <#assign partyCreditTotal=0>
                         <#assign closingBalance=0>
                     <fo:table-row>
                        <fo:table-cell>
                    	   <fo:block font-size="11pt" text-align="left" keep-together="always" font-weight="bold">${partyName}</fo:block>
	                	</fo:table-cell>
	                	 <fo:table-cell>
                    	    <fo:block font-size="11pt" text-align="left" white-space-collapse="false">&#xA;</fo:block>
	                	 </fo:table-cell>
	                	  <fo:table-cell>
                    	    <fo:block font-size="11pt" text-align="left" white-space-collapse="false">&#xA;</fo:block>
	                	 </fo:table-cell>
	                    <fo:table-cell font-weight="bold">
	                    	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">Opening Balance:</fo:block>  
	                    </fo:table-cell>
	                    <#assign debitValue=openingBalanceMap.get(eachPartyLedger.getKey()).debitValue?if_exists>
	                    <fo:table-cell>
                    	    <fo:block font-size="11pt" text-align="right" white-space-collapse="false">${debitValue?string("#0.00")}</fo:block>
	                	 </fo:table-cell>
	                	 <#assign creditValue = openingBalanceMap.get(eachPartyLedger.getKey()).creditValue?if_exists>
	                	 <#assign closingBalance=closingBalance+debitValue-creditValue>
	                	 <fo:table-cell>
                    	    <fo:block font-size="11pt" text-align="right" white-space-collapse="false">${creditValue?string("#0.00")}</fo:block>
	                	 </fo:table-cell>
                      </fo:table-row>
                      <#assign dayWiseLedgerList = eachPartyLedger.getValue()>
                      <#assign allDays = dayWiseLedgerList.keySet()>
                      <#list allDays as eachDay >
                      <#assign partyLedgerDetails = dayWiseLedgerList.get(eachDay)>
					   <#list partyLedgerDetails as eachLedger >
					  <fo:table-row>
                    	 <fo:table-cell>
                    	    <fo:block font-size="11pt" text-align="left">${eachDay}</fo:block>
	                	 </fo:table-cell>
	                	 <fo:table-cell>
                    	    <fo:block font-size="11pt" text-align="left">${eachLedger.description}</fo:block>
	                	 </fo:table-cell>
	                	 <#if eachLedger.invoiceId?has_content>
		                	 <fo:table-cell>
	                    	    <fo:block font-size="11pt" text-align="left">${eachLedger.invoiceId}</fo:block>
		                	 </fo:table-cell>
		                 <#else> 	 
		                     <fo:table-cell>
	                    	    <fo:block font-size="11pt" text-align="left" white-space-collapse="false">&#xA;</fo:block>
		                	 </fo:table-cell>
		                 </#if> 
		                 <#if eachLedger.paymentId?has_content>	 
		                	 <fo:table-cell>
	                    	    <fo:block font-size="11pt" text-align="left">${eachLedger.paymentId}</fo:block>
		                	 </fo:table-cell>
		                 <#else> 	 
		                     <fo:table-cell>
	                    	    <fo:block font-size="11pt" text-align="left" white-space-collapse="false">&#xA;</fo:block>
		                	 </fo:table-cell>
		                 </#if>
		                 <#assign partDebitTotal =partDebitTotal+eachLedger.debitValue?if_exists>
	                	 <fo:table-cell>
                    	    <fo:block font-size="11pt" text-align="right">${eachLedger.debitValue?if_exists?string("#0.00")}</fo:block>
	                	 </fo:table-cell>
	                	  <#assign partyCreditTotal =partyCreditTotal+eachLedger.creditValue?if_exists>
	                	 <fo:table-cell>
                    	    <fo:block font-size="11pt" text-align="right">${eachLedger.creditValue?if_exists?string("#0.00")}</fo:block>
	                	 </fo:table-cell>
					  </fo:table-row>
					   </#list>
					   </#list>
					   </#if>
					    <fo:table-row>
	                	 <fo:table-cell>
                    	    <fo:block font-size="11pt" text-align="left" white-space-collapse="false"></fo:block>
	                	 </fo:table-cell>
	                	  <fo:table-cell>
                    	    <fo:block font-size="11pt" text-align="left" white-space-collapse="false"></fo:block>
	                	 </fo:table-cell>
	                	  <fo:table-cell>
                    	    <fo:block font-size="11pt" text-align="left" white-space-collapse="false"></fo:block>
	                	 </fo:table-cell>
	                	 <fo:table-cell>
                    	    <fo:block font-size="11pt" text-align="left" keep-together="always" font-weight="bold">--------------------------------------------</fo:block>
	                	 </fo:table-cell>
	                	</fo:table-row> 
					    <fo:table-row>
                        <fo:table-cell>
                    	   <fo:block font-size="11pt" text-align="left" keep-together="always" font-weight="bold"></fo:block>
	                	</fo:table-cell>
	                	 <fo:table-cell>
                    	    <fo:block font-size="11pt" text-align="left" white-space-collapse="false"></fo:block>
	                	 </fo:table-cell>
	                	  <fo:table-cell>
                    	    <fo:block font-size="11pt" text-align="left" white-space-collapse="false"></fo:block>
	                	 </fo:table-cell>
	                    <fo:table-cell font-weight="bold">
	                    	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">Totals:</fo:block>  
	                    </fo:table-cell>
	                    <#assign closingBalance=closingBalance+partDebitTotal-partyCreditTotal>
	                    <fo:table-cell>
                    	    <fo:block font-size="11pt" text-align="right" white-space-collapse="false">${partDebitTotal?string("#0.00")}</fo:block>
	                	 </fo:table-cell>
	                	 <fo:table-cell>
                    	    <fo:block font-size="11pt" text-align="right" white-space-collapse="false">${partyCreditTotal?string("#0.00")}</fo:block>
	                	 </fo:table-cell>
	                	 <fo:table-cell>
                    	    <fo:block font-size="11pt" text-align="right" white-space-collapse="false">${closingBalance?string("#0.00")}</fo:block>
	                	 </fo:table-cell>
                      </fo:table-row>
                      <fo:table-row>
	                	 <fo:table-cell>
                    	    <fo:block font-size="11pt" text-align="left" white-space-collapse="false"></fo:block>
	                	 </fo:table-cell>
	                	  <fo:table-cell>
                    	    <fo:block font-size="11pt" text-align="left" white-space-collapse="false"></fo:block>
	                	 </fo:table-cell>
	                	  <fo:table-cell>
                    	    <fo:block font-size="11pt" text-align="left" white-space-collapse="false"></fo:block>
	                	 </fo:table-cell>
	                	 <fo:table-cell>
                    	    <fo:block font-size="11pt" text-align="left" keep-together="always" font-weight="bold">--------------------------------------------</fo:block>
	                	 </fo:table-cell>
	                	</fo:table-row> 
					  </#list>
					  <fo:table-row>
	                    	<fo:table-cell>
	                    	   <fo:block font-size="11pt" text-align="left"></fo:block>
		                	</fo:table-cell>
					  </fo:table-row>
                    </fo:table-body>
                </fo:table>
              </fo:block> 		
			</fo:flow>
		</fo:page-sequence>
		</#if>
		
			<#if !(partyDayWiseDetailMap?has_content) >
			<fo:page-sequence master-reference="main">
				<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			   		 <fo:block font-size="14pt">
			        	${uiLabelMap.NoOrdersFound}
			   		 </fo:block>
				</fo:flow>
			</fo:page-sequence>	
		</#if>   
 </#if>
 </fo:root>
</#escape>