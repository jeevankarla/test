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
<fo:table>
<fo:table-column column-width="10%"/>
<fo:table-column column-width="10%"/>
     <fo:table-body>
         <fo:table-row>
             <fo:table-cell><fo:block text-align="left" keep-together="always" white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
         </fo:table-row>
         <fo:table-row>
	         <fo:table-cell>
		       <#-->  <fo:block  keep-together="always" text-align="center" font-weight = "bold" font-family="Courier,monospace" white-space-collapse="false">${uiLabelMap.KMFDairyHeader}</fo:block>
				 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
	             <fo:block  keep-together="always" text-align="center" font-weight = "bold" font-family="Courier,monospace" white-space-collapse="false">TIN_NUMBER:${companyTinNumber?if_exists}</fo:block>
	        -->
	         <#if postalAddress?exists>
		        <#if postalAddress?has_content>
		            <fo:block keep-together="always" text-align="center" font-weight = "bold" font-family="Courier,monospace" white-space-collapse="false">${postalAddress.address1?if_exists}</fo:block>
		             <#if postalAddress.address2?has_content>
		             <fo:block keep-together="always" text-align="center" font-weight = "bold" font-family="Courier,monospace" white-space-collapse="false">${postalAddress.address2?if_exists}</fo:block></#if>
		            <#if postalAddress.address2?has_content>
		             <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${postalAddress.city?if_exists}:${stateProvinceAbbr?if_exists}${postalAddress.postalCode?if_exists},${countryName?if_exists}TIN_NUMBER:${companyTinNumber?if_exists}</fo:block> 
		             <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${countryName?if_exists}</fo:block>	
		             
		            </#if>
		        </#if>
		    <#else>
		    <fo:block>${companyName?if_exists}</fo:block>
		        <fo:block>${uiLabelMap.CommonNoPostalAddress}</fo:block>
		        <fo:block>${uiLabelMap.CommonFor}: ${companyName}</fo:block>
		    </#if>
	         </fo:table-cell>
         </fo:table-row>	
         <fo:table-row>
             <fo:table-cell><fo:block text-align="left" keep-together="always" white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell><fo:block text-align="left" keep-together="always" white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
        </fo:table-row>
    </fo:table-body>
</fo:table>
<fo:table table-layout="fixed" width="100%">
<fo:table-column column-width="1.5in"/>
<fo:table-column column-width="2in"/>
     <fo:table-body>
          <fo:table-row>
             <fo:table-cell><fo:block></fo:block></fo:table-cell>
 	             <#if invoice.invoiceTypeId =="PAYROL_INVOICE">
 	 	         <fo:table-cell><fo:block font-weight="bold" font-size="16pt" border=".5pt solid" border-width=".05mm" text-align="center">${invoice.getRelatedOne("InvoiceType").get("description",locale)?if_exists}</fo:block><fo:block></fo:block></fo:table-cell>
 	             <#else>
		         <fo:table-cell><fo:block font-weight="bold" font-size="16pt" border=".5pt solid" border-width=".05mm" text-align="center">${parentInvoiceType.get("description",locale)}</fo:block></fo:table-cell>
	             </#if>
          </fo:table-row>
          <fo:table-row>
              <fo:table-cell><fo:block text-align="left" keep-together="always" white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
          </fo:table-row>
     </fo:table-body>
</fo:table>
</#escape>
<#escape x as x?xml>
<fo:table table-layout="fixed" width="100%">
<fo:table-column column-width="1.5in"/>
<fo:table-column column-width="2.5in"/>
    <fo:table-body>
        <fo:table-row>
            <fo:table-cell>
                <#if invoice.invoiceTypeId !="PAYROL_INVOICE">
                <fo:block number-columns-spanned="2" font-weight="bold"></fo:block>
                </#if> 
            </fo:table-cell>
         </fo:table-row>
         <fo:table-row>
             <fo:table-cell><fo:block>${uiLabelMap.AccountingInvoiceDateAbbr}:</fo:block></fo:table-cell>
             <fo:table-cell><fo:block>${invoiceDate?if_exists}</fo:block></fo:table-cell>
         </fo:table-row>
         <fo:table-row>
             <fo:table-cell><fo:block>${uiLabelMap.AccountingCustNr}:</fo:block></fo:table-cell>
                 <fo:table-cell><fo:block><#if billingParty?has_content>${dispalyParty.partyId}</#if></fo:block></fo:table-cell>
         </fo:table-row>
             <#if billingPartyTaxId?has_content>
             <fo:table-row>
                 <fo:table-cell><fo:block>${uiLabelMap.PartyTaxId}:</fo:block></fo:table-cell>
                 <fo:table-cell><fo:block> ${billingPartyTaxId}</fo:block></fo:table-cell>
             </fo:table-row>
             </#if>
             <fo:table-row>
                 <fo:table-cell><fo:block>${uiLabelMap.AccountingInvNr}:</fo:block></fo:table-cell>
                 <fo:table-cell><fo:block><#if invoice?has_content>${invoice.invoiceId}</#if></fo:block></fo:table-cell>
             </fo:table-row>
             <fo:table-row>
                 <fo:table-cell><fo:block>VAT Invoice No.:</fo:block></fo:table-cell>
                 <fo:table-cell><fo:block><#if invoiceSequenceNumMap?has_content>${invoiceSequenceNumMap.get(invoice.invoiceId)}</#if></fo:block></fo:table-cell>
             </fo:table-row>
                  
<!--fo:table-row>
  <fo:table-cell><fo:block>${uiLabelMap.CommonStatus}</fo:block></fo:table-cell>
  <fo:table-cell><fo:block font-weight="bold">${invoiceStatus.get("description",locale)}</fo:block></fo:table-cell>
</fo:table-row-->
</fo:table-body>
</fo:table>
</#escape>
