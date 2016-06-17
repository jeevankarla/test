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
       <#if partyDayWiseDetailMap?has_content || openingBalanceMap?has_content>
       <#assign partyIdsList = openingBalanceMap.keySet()>
		        <fo:page-sequence master-reference="main" font-size="12pt">	
		        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
         			<fo:block text-align="left"    font-size="10pt" >T.I.N No     : 09152300064</fo:block>
         		    <fo:block text-align="left"  white-space-collapse="false"  font-size="10pt" >C.S.T No : 683925 w.e.f 12.06.1985                                            C.I.N No : U17299UP1983GOI005974 </fo:block>
                    <fo:block text-align="center" font-size="14pt" font-weight="bold"  white-space-collapse="false">NATIONAL HANDLOOM DEVELOPMENT CORPORATION LIMITED.</fo:block>
                    <fo:block text-align="center" font-size="12pt" keep-together="always"  white-space-collapse="false" font-weight="bold">&#160;SUBLEDGER REPORT FOR THE PERIOD FROM: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} to ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")} </fo:block>
              	    <fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">Branch Name:${branchName.toUpperCase()}</fo:block>  
              	    <fo:block font-size="11pt" text-align="left">=======================================================================================================</fo:block> 
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
		        	<fo:static-content flow-name="xsl-region-after">
		        	    <fo:block  keep-together="always" text-align="right" font-weight = "bold" font-family="Courier,monospace">Page - <fo:page-number/></fo:block>
		        	</fo:static-content>	        	
		        	<fo:flow flow-name="xsl-region-body"  font-family="Helvetica">		
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
                    <#assign debitTotals = 0>
                    <#assign creditTotals = 0>
                    <#list partyIdsList as eachParty >
                      <#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, eachParty, false)>
                         <#assign partDebitTotal=0>
                         <#assign partyCreditTotal=0>
                         <#assign closingBalance=0>
                         <#if  (openingBalanceMap.get(eachParty)?has_content) || (partyDayWiseDetailMap.get(eachParty)?has_content)>
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
	                    	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">Op. Bal:</fo:block>  
	                    </fo:table-cell>
	                    <#if openingBalanceMap.get(eachParty).debitValue?has_content>
	                      <#assign debitValue=openingBalanceMap.get(eachParty).debitValue?if_exists>
	                      <#assign debitTotals = debitTotals+debitValue>
	                    <fo:table-cell>
                    	    <fo:block font-size="11pt" text-align="right" white-space-collapse="false">${debitValue?string("#0.00")}</fo:block>
	                	 </fo:table-cell>
	                	 <#else>
	                	  <fo:table-cell>
                    	    <fo:block font-size="11pt" text-align="right" white-space-collapse="false">&#xA;</fo:block>
	                	 </fo:table-cell>
	                	 </#if>
	                	  <#if openingBalanceMap.get(eachParty).creditValue?has_content>
	                	 <#assign creditValue = openingBalanceMap.get(eachParty).creditValue?if_exists>
	                	 <#assign creditTotals = creditTotals+ creditValue>
	                	 <#assign closingBalance=closingBalance+debitValue-creditValue>
	                	 <fo:table-cell>
                    	    <fo:block font-size="11pt" text-align="right" white-space-collapse="false">${creditValue?string("#0.00")}</fo:block>
	                	 </fo:table-cell>
	                	  <#else>
	                	  <fo:table-cell>
                    	    <fo:block font-size="11pt" text-align="right" white-space-collapse="false">&#xA;</fo:block>
	                	 </fo:table-cell>
	                	  </#if>
                      </fo:table-row>
                      </#if>
                      <#if partyDayWiseDetailMap.get(eachParty)?has_content>
                      <#assign partyLedgerDeatils = partyDayWiseDetailMap.get(eachParty)?if_exists>
                       <#assign allDays = partyLedgerDeatils.keySet()>
                        <#list allDays as eachDay >
                         <#assign partyLedgerDetailsList = partyLedgerDeatils.get(eachDay)>
                         <#list partyLedgerDetailsList as eachLedger >
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
		                 <#assign debitTotals = debitTotals+eachLedger.debitValue?if_exists>
	                	 <fo:table-cell>
                    	    <fo:block font-size="11pt" text-align="right">${eachLedger.debitValue?if_exists?string("#0.00")}</fo:block>
	                	 </fo:table-cell>
	                	  <#assign partyCreditTotal =partyCreditTotal+eachLedger.creditValue?if_exists>
	                	  <#assign creditTotals = creditTotals+eachLedger.creditValue?if_exists>
	                	 <fo:table-cell>
                    	    <fo:block font-size="11pt" text-align="right">${eachLedger.creditValue?if_exists?string("#0.00")}</fo:block>
	                	 </fo:table-cell>
					  </fo:table-row>
					    </#list></#list>
					    <#if partDebitTotal?has_content || partyCreditTotal?has_content>
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
                    	    <fo:block font-size="11pt" text-align="left" keep-together="always" font-weight="bold">-------------------------------------------------------------------------------</fo:block>
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
                    	    <fo:block font-size="11pt" text-align="left" keep-together="always" font-weight="bold">-------------------------------------------------------------------------------</fo:block>
	                	 </fo:table-cell>
	                	</fo:table-row> 
	                	</#if>
					     </#list>
					     <#if debitTotals?has_content || creditTotals?has_content>
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
                    	    <fo:block font-size="11pt" text-align="left" keep-together="always" font-weight="bold">-------------------------------------------------------------------------------</fo:block>
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
	                    	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">Grand Totals:</fo:block>  
	                    </fo:table-cell>
	                    <fo:table-cell>
                    	    <fo:block font-size="11pt" text-align="right" white-space-collapse="false">${debitTotals?string("#0.00")}</fo:block>
	                	 </fo:table-cell>
	                	 <fo:table-cell>
                    	    <fo:block font-size="11pt" text-align="right" white-space-collapse="false">${creditTotals?string("#0.00")}</fo:block>
	                	 </fo:table-cell>
	                	 <fo:table-cell>
                    	    <fo:block font-size="11pt" text-align="right" white-space-collapse="false"></fo:block>
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
                    	    <fo:block font-size="11pt" text-align="left" keep-together="always" font-weight="bold">-------------------------------------------------------------------------------</fo:block>
	                	 </fo:table-cell>
	                	</fo:table-row>
	                	</#if> 
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