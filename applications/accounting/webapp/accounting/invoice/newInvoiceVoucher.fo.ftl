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
      <fo:simple-page-master master-name="main" page-height="12in" page-width="10in" margin-top="0.5in" margin-bottom=".1in" margin-left=".3in" margin-right=".5in">
          <fo:region-body margin-top="1.66in"/>
          <fo:region-before extent="1in"/>
          <fo:region-after extent="1in"/>
      </fo:simple-page-master>
    </fo:layout-master-set>
    <#if !finalDetails?has_content>
<fo:page-sequence master-reference="main">
   <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
      <fo:block font-size="14pt">
             No Records Found.
   	  </fo:block>
   </fo:flow>
</fo:page-sequence>  
<#else>
    <fo:page-sequence master-reference="main">
        <fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
         
           <fo:block text-align="center" font-size="14pt"   white-space-collapse="false">Under : ${scheme}</fo:block> 
           <fo:block text-align="center" font-size="14pt" font-weight="bold"  white-space-collapse="false">NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD.</fo:block>
   		   <fo:block text-align="center" font-size="12pt" font-weight="bold"  white-space-collapse="false">S-13/36, SRI RAM MARKET</fo:block>
           <fo:block text-align="center" font-size="12pt" font-weight="bold" white-space-collapse="false">VARANASI-221002</fo:block>
           <fo:block text-align="center" font-size="12pt" font-weight="bold"  white-space-collapse="false">E-MAIL:nhdccitdvns@yahoo.in</fo:block>
           
        </fo:static-content>
        
        <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
        
           <fo:block>     
    <fo:table width="100%"   align="right" table-layout="fixed"  font-size="12pt"> 
	<fo:table-column column-width="35%"/>
	<fo:table-column column-width="40%"/>
	<fo:table-column column-width="25%"/>

     

		<fo:table-body>
			<fo:table-row white-space-collapse="false">
				<fo:table-cell >
				<fo:block text-align="left"  font-weight="bold"  font-size="12pt" >${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, soceity, true)}</fo:block>
				<#list finalAddresList as eachDetail>
				<fo:block text-align="left"    font-size="12pt" >${eachDetail.key2?if_exists}</fo:block>
				</#list>
				<fo:block text-align="left"    font-size="12pt" >Your above confirmed indent goods despatched through M/S :${carrierName?if_exists}</fo:block>
				<fo:block text-align="left"    font-size="12pt" >LR/RRNo : ${lrNumber?if_exists}  Dt : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invoiceDate, "dd-MM-yyyy")}  </fo:block>
				</fo:table-cell>
				<fo:table-cell >
				<fo:block text-align="rght"    font-size="12pt" >&#160;&#160;&#160;&#160;BILL NO                :${invoiceId?if_exists}</fo:block>
				<fo:block text-align="rght"    font-size="12pt" >&#160;&#160;&#160;&#160;NHDC Indent No         :${indentNo?if_exists}</fo:block>
				<fo:block text-align="rght"    font-size="12pt" >&#160;&#160;&#160;&#160;NHDC P.O. No           :${poNumber?if_exists}</fo:block>
				<fo:block text-align="rght"    font-size="12pt" >&#160;&#160;&#160;&#160;User Agency Indent No  :</fo:block>
				</fo:table-cell>
				<fo:table-cell >
				<fo:block text-align="right"     font-size="12pt" >DATE : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invoiceDate, "dd-MM-yyyy")}</fo:block>
				<fo:block text-align="right"     font-size="12pt" >DATE :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(indentDate, "dd-MMM-yyyy")}</fo:block>
				<fo:block text-align="right"     font-size="12pt" >DATE :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(poDate, "dd-MMM-yyyy")}</fo:block>
				</fo:table-cell>
		
			</fo:table-row>
		</fo:table-body>
	</fo:table>
	</fo:block>
        <fo:block text-align="center"    font-size="12pt" >&#160;&#160;&#160;&#160;</fo:block>
         <fo:block>     
    <fo:table width="100%" border-style="solid"  align="right" table-layout="fixed"  font-size="12pt"> 
	<fo:table-column column-width="5%"/>
	<fo:table-column column-width="17%"/>
	<fo:table-column column-width="13%"/>
	<fo:table-column column-width="10%"/>
	<fo:table-column column-width="15%"/>
	<fo:table-column column-width="10%"/>
	<fo:table-column column-width="15%"/>
	<fo:table-column column-width="15%"/>


		<fo:table-body>
			<fo:table-row white-space-collapse="false">
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"    font-size="12pt" >S.No</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"    font-size="12pt" >Description of Goods with count</fo:block>
				<fo:block text-align="center"    font-size="12pt" >/denier etc</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"     font-size="12pt" >Bale/Bag No.</fo:block>
				<fo:block text-align="center"     font-size="12pt" >Box/marketing</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"    font-size="12pt" >Unit</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"    font-size="12pt" >Quantity</fo:block>
				<fo:block text-align="center"     font-size="12pt" >Kgs</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"    font-size="12pt" >Scheme</fo:block>
				<fo:block text-align="center"     font-size="12pt" >Qty Kgs</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				 <fo:block text-align="center"    font-size="12pt" >Rate/Kg/bundle</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				 <fo:block text-align="center"    font-size="12pt" >Amount(Rs)</fo:block>
				</fo:table-cell>
			</fo:table-row>
			
			 
		   
		     <#assign sr = 1>
		     <#assign totQuantity = 0>
		      <#assign totSchemeQty = 0>
		      <#assign totAmount = 0>
		    <#list finalDetails as invoiceDetail>
		   
		     <fo:table-row white-space-collapse="false">
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="12pt" >${sr}</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="left"  font-size="12pt" >${invoiceDetail.get("prodDescription")}</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="12pt" ><#if invoiceDetail.get("baleQty")?has_content>${invoiceDetail.get("baleQty")?if_exists}<#else>0.00</#if></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="12pt" ><#if invoiceDetail.get("unit")?has_content>${invoiceDetail.get("unit?if_exists")?if_exists}<#else>${0.00}</#if></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				
				<#if invoiceDetail.get("quantity")?has_content>
				<#assign totQuantity = totQuantity+invoiceDetail.get("quantity")>
				</#if>
				<fo:block text-align="right"  font-size="12pt" ><#if invoiceDetail.get("quantity")?has_content>${invoiceDetail.get("quantity")?if_exists?string("#0.000")}<#else>${0.00}</#if></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<#if invoiceDetail.get("schemeQty")?has_content>
				<#assign totSchemeQty = totSchemeQty+invoiceDetail.get("schemeQty")>
				</#if>
				<fo:block text-align="right"  font-size="12pt" ><#if invoiceDetail.get("schemeQty")?has_content>${invoiceDetail.get("schemeQty")?if_exists?string("#0.000")}<#else>${0.00}</#if></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="12pt" ><#if invoiceDetail.get("rateKg")?has_content>${invoiceDetail.get("rateKg")?if_exists?string("#0.000")}<#else>${0.00}</#if></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<#if invoiceDetail.get("amount")?has_content>
				<#assign totAmount = totAmount+invoiceDetail.get("amount")>
				</#if>
				<fo:block text-align="right"  font-size="12pt" ><#if invoiceDetail.get("amount")?has_content>${invoiceDetail.get("amount")?if_exists?string("#0.00")}<#else>${0.00}</#if></fo:block>
				</fo:table-cell>
				</fo:table-row>
		   
		      <#assign sr = sr+1> 
		   
		   </#list>
			
			<fo:table-row white-space-collapse="false">
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="12pt" ></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="12pt" >TOTAl</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="12pt" ></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="12pt" ></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="12pt" >${totQuantity?string("#0.000")}</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="12pt" >${totSchemeQty?string("#0.000")}</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="12pt" ></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="12pt" >${totAmount?string("#0.00")}</fo:block>
				</fo:table-cell>
								
				</fo:table-row>
		</fo:table-body>
				
	</fo:table>
	</fo:block>
	        <fo:block text-align="center"    font-size="12pt" >&#160;&#160;&#160;&#160;</fo:block>
	
	<fo:block text-align="left"   font-weight="bold"   font-size="12pt" >Supplier Information :</fo:block>
	
	 <fo:block>     
    <fo:table width="100%"   align="right" table-layout="fixed"  font-size="12pt"> 
	<fo:table-column column-width="33%"/>
	<fo:table-column column-width="33%"/>
	<fo:table-column column-width="34%"/>


		<fo:table-body>
			<fo:table-row white-space-collapse="false">
				<fo:table-cell >
				<fo:block text-align="left"    font-size="12pt" >${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, supplier, true)}</fo:block>
				</fo:table-cell>
				<fo:table-cell >
				<fo:block text-align="center"    font-size="12pt" >${scheme}</fo:block>
				<fo:block text-align="center"    font-size="12pt" >&#160;</fo:block>
				<fo:block text-align="center"    font-size="12pt" >TOTAL VALUE (RS.)</fo:block>
				</fo:table-cell>
				<fo:table-cell >
				<fo:block text-align="right"    font-size="12pt" >(-)${schemeDeductionAmt?string("#0.00")}</fo:block>
				<fo:block text-align="right"    font-size="12pt" >--------------</fo:block>
				<fo:block text-align="right"    font-size="12pt" >   ${(totAmount-schemeDeductionAmt)?string("#0.00")}</fo:block>
				<fo:block text-align="right"    font-size="12pt" >--------------</fo:block>
				</fo:table-cell>
				
			</fo:table-row>
	       </fo:table-body>
       	</fo:table>
	   </fo:block>
	   	        <fo:block text-align="center"    font-size="12pt" >&#160;&#160;&#160;&#160;</fo:block>
	   
	   				<fo:block text-align="left"    font-size="12pt" >MILL Invo No/Date :${supplierInvoiceId?if_exists} <#if supplierInvoiceDate?has_content>/ ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(supplierInvoiceDate, "dd-MM-yyyy")?if_exists}</#if></fo:block>
	   	   	        <fo:block text-align="center"    font-size="12pt" >&#160;&#160;&#160;&#160;</fo:block>
	   
	        <fo:block>     
		    <fo:table width="100%" border-style="solid"  align="right" table-layout="fixed"  font-size="12pt"> 
			<fo:table-column column-width="40%"/>
			<fo:table-column column-width="60%"/>
		
				<fo:table-body>
					<fo:table-row white-space-collapse="false">
						<fo:table-cell  >
						<fo:block text-align="left" font-weight="bold"   font-size="12pt" >${Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble((totAmount-schemeDeductionAmt)?string("#0.00")), "%rupees-and-paise", locale).toUpperCase()}</fo:block>
						</fo:table-cell>
						<fo:table-cell >
						<fo:block text-align="center"    font-size="12pt" ></fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row white-space-collapse="false" >
						<fo:table-cell  border-style-right="hidden">
						<fo:block text-align="left"    font-size="12pt" >Destination       :${destination?if_exists}</fo:block>
						<fo:block text-align="left"    font-size="12pt" >Freight (RS.)     :   </fo:block>
						<fo:block text-align="left"    font-size="12pt" >Due Date          :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invoiceDate, "dd-MM-yyyy")}</fo:block>
						</fo:table-cell>
						<fo:table-cell border-style-left="hidden">
						<fo:block text-align="right"    font-size="12pt" >FOR NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD.</fo:block>
						<fo:block text-align="right"    font-size="12pt" >&#160;</fo:block>
						<fo:block text-align="right"    font-size="12pt" >&#160;</fo:block>
						<fo:block text-align="right"    font-size="12pt" >(Authorised Signatory)</fo:block>
						</fo:table-cell>
				  </fo:table-row>
			       </fo:table-body>
		       	</fo:table>
	       </fo:block>
	       	   	        <fo:block text-align="center"    font-size="12pt" >&#160;&#160;&#160;&#160;</fo:block>
	       	   	        <fo:block text-align="center"    font-size="12pt" >&#160;&#160;&#160;&#160;</fo:block>
	       	   	        <fo:block text-align="center"    font-size="12pt" >&#160;&#160;&#160;&#160;</fo:block>
	       
	        	<fo:block text-align="left" white-space-collapse="false"   font-size="12pt" >Prepared By              Supdt(Comm)     AM@/DM@         checked By F &amp; A Section </fo:block>
      		
              <fo:block text-align="left" font-weight="bold"    font-size="12pt" >TERMS &amp; CONDITIONS:</fo:block>
			   <fo:block text-align="left" white-space-collapse="false"   font-size="12pt" > * All payment  should be made by crossed cheque/draft in favour of 'National handloom </fo:block>
			   <fo:block text-align="left" white-space-collapse="false"   font-size="12pt" >&#160; Development corporation Limited'payable at INTEREST will be charged @ 13.00% per annum on</fo:block>
			   <fo:block text-align="left" white-space-collapse="false"    font-size="12pt" >&#160; overdue Amount.</fo:block>
			   
			   
			    <fo:block text-align="left" white-space-collapse="false"   font-size="12pt" > * In case of any dispute,the case will be referred to an arbitrator mutually agreed upon </fo:block>
			   <fo:block text-align="left" white-space-collapse="false"   font-size="12pt" >&#160; whose will be final and binding E.&amp;.O.E</fo:block>
      		</fo:flow>
      		
      		
      		
      		
      	</fo:page-sequence>
      
     </#if>   
  </fo:root>
</#escape>
