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
            margin-top="0.5in" margin-bottom="1in" margin-left=".3in" margin-right="1in">
        <fo:region-body margin-top="1in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "CashTransaction.txt")}
<#if cashTransactionList?has_content>
<fo:page-sequence master-reference="main" force-page-count="no-force">					
	
	<fo:static-content flow-name="xsl-region-before" font-family="Courier">
    	<fo:block text-align="left" keep-together="always" white-space-collapse="false">.                           KRISHNAVENI KKDMPCU  LTD: VIJAYAWADA</fo:block>			
		<fo:block keep-together="always" font-family="Courier,monospace" white-space-collapse="false">.                  CASH TRANSACTION PARTICULARS   FORM  ${parameters.fromDate}  TO  ${parameters.thruDate}</fo:block>
	  	<fo:block>-----------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
        <fo:block text-align="left" keep-together="always" white-space-collapse="false">  DATE    OPENING       MILK VALUE     TR.RECEIPT   VR.RECEIPTS   DETAILS            REMITTANCE    VR.NO    EXPENDITURE    REMARKS/CLOSING    </fo:block>
        <fo:block>-----------------------------------------------------------------------------------------------------------------------------------------------</fo:block>  
     </fo:static-content>

	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" font-size="9pt">
		<fo:block>	
		<fo:table width="100%" table-layout="fixed" space-after="0.0in">
        	<#list cashTransactionList as cashTransaction>
			<fo:table-column column-width="65pt"/>
			<fo:table-column column-width="65pt"/>
			<fo:table-column column-width="70pt"/>
			<fo:table-column column-width="75pt"/>
			<fo:table-column column-width="70pt"/>
			<fo:table-column column-width="30pt"/>
			<fo:table-column column-width="40pt"/>
			<fo:table-column column-width="60pt"/>
			<fo:table-column column-width="20pt"/>
			<fo:table-column column-width="40pt"/>
			<fo:table-column column-width="60pt"/>
			</#list>
			<fo:table-column column-width="115pt"/>
            	<fo:table-body>
            		<#list cashTransactionList as cashTransaction>
						<fo:table-row>
 							<fo:table-cell>
 								<fo:block>
 										${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(cashTransaction.get("date"), "dd/MM/yy"))?if_exists}
 								</fo:block>
 							</fo:table-cell>
 							<fo:table-cell/>
 							<fo:table-cell>
 								<fo:block text-align="left">${cashTransaction.get("milkValue")?if_exists}</fo:block>
 							</fo:table-cell>
 							<fo:table-cell>
 									<fo:block text-align="left">${cashTransaction.get("trReceipt")?if_exists}</fo:block>
 							</fo:table-cell>
 								<#assign cashValue = cashTransaction.entrySet()?if_exists>
 							<fo:table-cell>
 								<#list cashValue as cash>
 								<#if (cash.getKey() !="date")&&(cash.getKey() !="milkValue")&&(cash.getKey() !="remittance")&&(cash.getKey() !="REMIT_CASH")&&(cash.getKey() !="trReceipt")>
								<#if (cash.getKey() == "RT-MILKCASH")>
 									<fo:block text-align="left">${cash.getValue()?if_exists}</fo:block>
 								<#else>
 									<fo:block keep-together="always" text-align="left">${cash.getValue()?if_exists}</fo:block>
 								</#if>
 								</#if>
 								</#list>
 							</fo:table-cell>
 							<fo:table-cell>
								<#list cashValue as cash>
								<#if (cash.getKey() !="date")&&(cash.getKey() !="milkValue")&&(cash.getKey() !="remittance")&&(cash.getKey() !="trReceipt")&&(cash.getKey() !="REMIT_CASH")>
								<#if (cash.getKey() == "RT-MILKCASH")>
 									<fo:block keep-together="always" text-align="left">RT.Milk Cash</fo:block>
 								<#elseif cash.getKey() == "TRANSPORTER_PAYIN">	
 									<fo:block keep-together="always" text-align="left">VEH.Short Recv</fo:block>
 								<#else>
 									<#assign PaymentType = delegator.findOne("PaymentType", {"paymentTypeId" : cash.getKey()}, true)>
 									<fo:block keep-together="always" text-align="left">${PaymentType.get("description")?if_exists}</fo:block>
 								</#if>
 								</#if>
 								</#list>
 							</fo:table-cell>
 							<fo:table-cell/>
 							<fo:table-cell/>
 							<fo:table-cell>
 								<fo:block text-align="left">${cashTransaction.get("REMIT_CASH")?if_exists}</fo:block>
 							</fo:table-cell>	
					</fo:table-row>
					</#list>
					<fo:table-row>
						<fo:table-cell>
							<fo:block>-----------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
    						<fo:block text-align="left" keep-together="always" white-space-collapse="false">Total: </fo:block>
							<#assign totPaymentType = totPaymentTypeWise.entrySet()>
							<#list totPaymentType as totPayment>
								<#if (totPayment.getKey() == "RT-MILKCASH")>
 									<fo:block text-align="left" keep-together="always" white-space-collapse="false">.       RT.Milk Cash   :</fo:block>
 								<#elseif totPayment.getKey() == "TRANSPORTER_PAYIN">	
 									<fo:block text-align="left" keep-together="always" white-space-collapse="false">.       VEH.Short Recv:</fo:block>
 								<#else>
 									<#assign PaymentType = delegator.findOne("PaymentType", {"paymentTypeId" : totPayment.getKey()}, true)>
 									<fo:block text-align="left" keep-together="always" white-space-collapse="false">.       ${PaymentType.get("description")?if_exists}   :</fo:block>
 								</#if>
    						</#list>
    						<fo:block>-----------------------------------------------------------------------------------------------------------------------------------------------</fo:block>  
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell/>	
						<fo:table-cell>
							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
							<#assign totPaymentType = totPaymentTypeWise.entrySet()>
							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
							<#list totPaymentType as totPayment>
								<#if (totPayment.getKey() == "RT-MILKCASH")>
								<#if totTransptDueAmt?has_content>
								 <#assign transDuePaymentTypeWise = totTransptDueAmt>
								</#if>
								<#assign rtTotAfterTrDue = totPayment.getValue() - transDuePaymentTypeWise>
 									<fo:block text-align="left">${rtTotAfterTrDue?string("##0.00")?if_exists}</fo:block>
 								<#elseif totPayment.getKey() == "TRANSPORTER_PAYIN">	
 									<fo:block text-align="left">${totPayment.getValue()?string("##0.00")?if_exists}</fo:block>
 								<#else>
 									<fo:block text-align="left">${totPayment.getValue()?string("##0.00")?if_exists}</fo:block>
 								</#if>
    						</#list>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
 						<fo:table-cell>
 							<fo:block text-align="left" keep-together="always" white-space-collapse="false">.                   ${cashTransactionTotalsMap.get("totMilkValue")?string("##0.00")?if_exists}      ${cashTransactionTotalsMap.get("trReceiptTot")?string("##0.00")?if_exists}       ${cashTransactionTotalsMap.get("totReceipts")?string("##0.00")?if_exists}                       ${cashTransactionTotalsMap.get("totRemittance")?string("##0.00")?if_exists}                  0.00</fo:block>
 							<fo:block text-align="left" keep-together="always" white-space-collapse="false">AGNT.CASH           0.00</fo:block>
        					<fo:block text-align="left" keep-together="always" white-space-collapse="false">CHEQUES             0.00</fo:block>
 							<fo:block>-----------------------------------------------------------------------------------------------------------------------------------------------</fo:block>  
 						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell>
							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
        					<fo:block text-align="left" keep-together="always" white-space-collapse="false">PREPARED-BY           ACCOUNTANT            CASHIER              MANAGER </fo:block>
 						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block>		
    </fo:flow>						        	
  </fo:page-sequence>
  <#else>
		<fo:page-sequence master-reference="main">
	    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	       		 <fo:block font-size="14pt">
	            	${uiLabelMap.OrderNoOrderFound}.
	       		 </fo:block>
	    	</fo:flow>
		</fo:page-sequence> 
	</#if>	
</fo:root>
</#escape>