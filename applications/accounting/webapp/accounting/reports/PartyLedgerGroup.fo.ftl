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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-left=".3in" margin-right=".3in" margin-bottom=".3in" margin-top=".3in">
        <fo:region-body margin-top="1.3in" margin-bottom=".6in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>     
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "PartyLedgerGroupReport.pdf")}
 <#if partyMap?has_content>
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
				 <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
				<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;                                         MOTHER DAIRY, YALAHANKA KMF UNIT : GKVK POST.BANGALORE-560 065                          Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MM-yyyy")}</fo:block>
				<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" font-weight="bold"  font-size="10pt" white-space-collapse="false">&#160;${uiLabelMap.CommonPage}- <fo:page-number/> </fo:block>
				<fo:block  text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold">PARTY LEDGER FOR THE PERIOD FROM ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")} </fo:block>
				<fo:block font-size="10pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
				<#assign partyWiseList = partyMap.entrySet()>
				<#assign grdDebit=0>
                <#assign grdCredit=0>
				<#list partyWiseList as partyList>
				<fo:block  keep-together="always" text-align="center" font-weight="bold"  font-size="11pt" white-space-collapse="false">PARTY NAME : ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyList.getKey(), false)}[${partyList.getKey()}]</fo:block>
               <fo:block font-size="10pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
               <fo:block>
                    <fo:table>
				    <fo:table-column column-width="9%"/>
			        <fo:table-column column-width="5%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="19%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="10%"/>
                    <fo:table-body>
                    	<fo:table-row>
                    	   <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false">Acctg TransId</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false">Seq Id</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false">Trans Date</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false">Trans Type</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false">GlAccountId</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false">GlAccount Description</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false">Invoice Id</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false">Payment Id</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false">Debit Amount</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false">Credit Amount</fo:block>  
                			</fo:table-cell>
                		</fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block> 
			<fo:block font-size="10pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			<#assign acctgDetails=partyList.getValue()>
			<#assign totDebit=0>
            <#assign totCredit=0>			
			<#assign closingTot=0>
			<fo:block>
                    <fo:table>
				    <fo:table-column column-width="9%"/>
			        <fo:table-column column-width="5%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="11%"/>
			        <fo:table-column column-width="9%"/>
			        <fo:table-column column-width="20%"/>
			        <fo:table-column column-width="9%"/>
			        <fo:table-column column-width="9%"/>
			        <fo:table-column column-width="9%"/>
			        <fo:table-column column-width="10%"/>
                    <fo:table-body>
                        <fo:table-row>
                    	   <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block   text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="11pt" white-space-collapse="false"></fo:block> 
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">OPENING BALANCE:</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<#assign openBal=0>
							<#assign openCredit=0>
                            <#assign openDebit=0>
                            <#if openingBalMap?has_content>
                			<#assign openBal=openingBalMap.get(partyList.getKey())>
                            <#if openBal gte 0>
                             <#assign openDebit=openBal>
                            <#else>
                              <#assign openCredit=openBal>
                            </#if> 
                            </#if>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="11pt" white-space-collapse="false">${openDebit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="11pt" white-space-collapse="false">${((-1)*openCredit)?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                		</fo:table-row>
                		<fo:table-row>
                			<fo:table-cell>
                			<fo:block font-size="10pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                			</fo:table-cell>
                		</fo:table-row>
                    <#list acctgDetails as acctgTrans>
                    	<fo:table-row>
                    	   <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">${acctgTrans.get("acctgTransId")?if_exists}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">${acctgTrans.get("acctgTransEntrySeqId")?if_exists}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(acctgTrans.get("transactionDate"), "dd-MM-yyyy")}</fo:block>  
                			</fo:table-cell>
                			<#assign acctgTransType=delegator.findOne("AcctgTransType", {"acctgTransTypeId" :acctgTrans.get("acctgTransTypeId")}, true)>
                			<fo:table-cell>
                    			<fo:block   text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">${acctgTransType.description?if_exists}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">${acctgTrans.get("glAccountId")?if_exists}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">${acctgTrans.get("glAccDescription")?if_exists}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">${acctgTrans.get("invoiceId")?if_exists}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">${acctgTrans.get("paymentId")?if_exists}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${acctgTrans.get("debit")?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                          <#assign totDebit=totDebit+acctgTrans.get("debit")>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${acctgTrans.get("credit")?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                          <#assign totCredit=totCredit+acctgTrans.get("credit")>
                		</fo:table-row>
						</#list>
                        <fo:table-row>
                			<fo:table-cell>
                			<fo:block font-size="10pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                			</fo:table-cell>
                		</fo:table-row>
						<fo:table-row>
                    	   <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block   text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block   text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">TRANSACTIONS TOTALS :</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                             <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                            <#assign grdDebit=grdDebit+totDebit>
                            <#assign grdCredit=grdCredit+totCredit>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${totDebit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${totCredit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                		</fo:table-row>
                		<fo:table-row>
                			<fo:table-cell>
                			<fo:block font-size="10pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                			</fo:table-cell>
                		</fo:table-row>
                		<fo:table-row>
                    	   <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block   text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block   text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">CLOSING TRANSACTION TOTAL:</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                             <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                            <#assign closingTot=totDebit-totCredit>
                            <#assign closingDebit=0>
                            <#assign closingCredit=0>   
                            <#if closingTot gte 0>
                             <#assign closingDebit=closingTot>
                             <#else>
                             <#assign closingCredit=((-1)*(closingTot))>
                             </#if>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${closingDebit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${closingCredit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                		</fo:table-row>
                		<fo:table-row>
                			<fo:table-cell>
                			<fo:block font-size="10pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                			</fo:table-cell>
                		</fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block> 
             </#list>
	          <fo:block>
                    <fo:table>
				    <fo:table-column column-width="9%"/>
			        <fo:table-column column-width="5%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="11%"/>
			        <fo:table-column column-width="9%"/>
			        <fo:table-column column-width="20%"/>
			        <fo:table-column column-width="9%"/>
			        <fo:table-column column-width="9%"/>
			        <fo:table-column column-width="9%"/>
			        <fo:table-column column-width="10%"/>
                    <fo:table-body>
                    	<fo:table-row>
                			<fo:table-cell>
                    			<fo:block   text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block   text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">GRAND TOTALS :</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                             <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${grdDebit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${grdCredit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                		</fo:table-row>
                		<fo:table-row>
                			<fo:table-cell>
                			<fo:block font-size="10pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                			</fo:table-cell>
                		</fo:table-row>
                		<fo:table-row>
                			<fo:table-cell>
                    			<fo:block   text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block   text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">CLOSING GRAND TOTALS :</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                             <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<#assign clsGrdDebit=0>
                            <#assign clsGrdCredit=0>
                            <#assign balance=grdDebit-grdCredit> 
                            <#if balance gte 0>
                             <#assign clsGrdDebit=balance> 
                            <#else>
                              <#assign clsGrdCredit=((-1)*(balance))>
                            </#if>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${clsGrdDebit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${clsGrdCredit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                		</fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block>
               <fo:block font-size="10pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
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