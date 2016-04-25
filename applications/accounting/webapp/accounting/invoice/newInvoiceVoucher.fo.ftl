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
      <fo:simple-page-master master-name="main" page-height="12in" page-width="10in" margin-top=".1in" margin-bottom=".1in" margin-left=".3in" margin-right=".5in">
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
         
         				<fo:block text-align="left"    font-size="10pt" >T.I.N No     : 09152300064</fo:block>
         				<fo:block text-align="left"  white-space-collapse="false"  font-size="10pt" >C.S.T No : 683925 w.e.f 12.06.1985                                            C.I.N No : U17299UP1983GOI005974 </fo:block>
         				<fo:block text-align="center"    font-size="10pt" >&#160;&#160;&#160;&#160;</fo:block>
         
           <fo:block text-align="center" font-size="14pt"   white-space-collapse="false">Under : <#if scheme == "MGPS_10Pecent">MGP 10% Scheme<#elseif scheme == "MGPS">MGPS<#elseif scheme == "General">General</#if></fo:block> 
           <fo:block text-align="center" font-size="14pt" font-weight="bold"  white-space-collapse="false">NATIONAL HANDLOOM DEVELOPMENT CORPORATION LIMITED.</fo:block>
   		   <fo:block text-align="center" font-size="12pt" font-weight="bold"  white-space-collapse="false">S-13/36, SRI RAM MARKET</fo:block>
           <fo:block text-align="center" font-size="12pt" font-weight="bold" white-space-collapse="false">VARANASI-221002</fo:block>
           <fo:block text-align="center" font-size="12pt" font-weight="bold"  white-space-collapse="false">E-MAIL:nhdcltdvns@yahoo.in</fo:block>
           
        </fo:static-content>
                 				
        
        <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
        <fo:block text-align="center"    font-size="12pt" >&#160;&#160;&#160;&#160;</fo:block>
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
				<fo:block text-align="left"    font-size="11pt" >Your above confirmed indent goods dispatched through M/S :${carrierName?if_exists}</fo:block>
				<fo:block text-align="left"    font-size="11pt" >LR No :${lrNumber?if_exists} Dt:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invoiceDate, "dd-MMM-yyyy")}  </fo:block>
				</fo:table-cell>
				<fo:table-cell >
				<fo:block text-align="rght"    font-size="12pt" >&#160;&#160;&#160;&#160;BILL NO                :${invoiceId?if_exists}</fo:block>
				<fo:block text-align="rght"    font-size="12pt" >&#160;&#160;&#160;&#160;NHDC Indent No         :${indentNo?if_exists}</fo:block>
				<fo:block text-align="rght"    font-size="12pt" >&#160;&#160;&#160;&#160;NHDC PO No             :${poNumber?if_exists}</fo:block>
				<fo:block text-align="rght"    font-size="12pt" >&#160;&#160;&#160;&#160;User Agency Indent No  :</fo:block>
				</fo:table-cell>
				<fo:table-cell >
				<fo:block text-align="right"     font-size="12pt" >DATE :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invoiceDate, "dd-MMM-yyyy")}</fo:block>
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
				<fo:block text-align="center"    font-size="12pt" >(RS)</fo:block>
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
				<fo:block text-align="center"  font-size="12pt" >${sr}</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="left"  font-size="12pt" >${invoiceDetail.get("prodDescription")}</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"  font-size="12pt" ><#if invoiceDetail.get("baleQty")?has_content>${invoiceDetail.get("baleQty")?if_exists}<#else>0.00</#if></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"  font-size="12pt" >KG</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				
				<#if invoiceDetail.get("quantity")?has_content>
				<#assign totQuantity = totQuantity+invoiceDetail.get("quantity")>
				</#if>
				<fo:block text-align="center"  font-size="12pt" ><#if invoiceDetail.get("quantity")?has_content>${invoiceDetail.get("quantity")?if_exists?string("#0.000")}<#else>${0.000}</#if></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<#if invoiceDetail.get("schemeQty")?has_content>
				<#assign totSchemeQty = totSchemeQty+invoiceDetail.get("schemeQty")>
				</#if>
				<fo:block text-align="center"  font-size="12pt" ><#if invoiceDetail.get("schemeQty")?has_content>${invoiceDetail.get("schemeQty")?if_exists?string("#0.00")}<#else>${0.00}</#if></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"  font-size="12pt" ><#if invoiceDetail.get("amount")?has_content>${invoiceDetail.get("amount")?if_exists?string("#0.00")}<#else>${0.00}</#if></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<#if invoiceDetail.get("ToTamount")?has_content>
				<#assign totAmount = totAmount+invoiceDetail.get("ToTamount")>
				</#if>
				<fo:block text-align="center"  font-size="12pt" >${invoiceDetail.get("ToTamount")?string("#0.00")}</fo:block>
				</fo:table-cell>
				</fo:table-row>
		   
		      <#assign sr = sr+1> 
		   
		   </#list>
			
			<fo:table-row white-space-collapse="false">
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="12pt" ></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"  font-size="12pt" >TOTAL</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="12pt" ></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="12pt" ></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"  font-size="12pt" >${totQuantity?string("#0.000")}</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"  font-size="12pt" >${totSchemeQty?string("#0.00")}</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"  font-size="12pt" ></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"  font-size="12pt" >${grandTotal?string("#0.00")}</fo:block>
				</fo:table-cell>
								
				</fo:table-row>
		</fo:table-body>
				
	</fo:table>
	</fo:block>
	        <fo:block text-align="center"    font-size="12pt" >&#160;&#160;&#160;&#160;</fo:block>
	
	<fo:block text-align="left"  white-space-collapse="false" font-weight="bold"   font-size="12pt" >Supplier Information :                          TAXES AND CHARGES OTHER DETAILS:</fo:block>
	
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
				<#if scheme == "MGPS_10Pecent">
				<fo:block text-align="center" font-size="14pt"   white-space-collapse="false">Under : <#if scheme == "MGPS_10Pecent">MGP 10% Scheme<#elseif scheme == "MGPS">MGPS<#elseif scheme == "General">General</#if></fo:block>
				<#else>
				<fo:block text-align="right"    font-size="12pt" >&#160;</fo:block>
				</#if>
				<fo:block text-align="center"    font-size="12pt" >&#160;</fo:block>
				<fo:block text-align="center"    font-size="12pt" >TOTAL VALUE (RS.)</fo:block>
				</fo:table-cell>
				<fo:table-cell >
				<#if scheme == "MGPS_10Pecent">
				<fo:block text-align="right"    font-size="12pt" >(-)${schemeDeductionAmt?string("#0.00")}</fo:block>
				<#else>
				<fo:block text-align="right"    font-size="12pt" >&#160;</fo:block>
				</#if>
				<fo:block text-align="right"    font-size="12pt" >--------------</fo:block>
				<fo:block text-align="right"    font-size="12pt" >   ${(grandTotal-schemeDeductionAmt)?string("#0.00")}</fo:block>
				<fo:block text-align="right"    font-size="12pt" >--------------</fo:block>
				</fo:table-cell>
				
			</fo:table-row>
	       </fo:table-body>
       	</fo:table>
	   </fo:block>
	   	        <fo:block text-align="center"    font-size="12pt" >&#160;&#160;&#160;&#160;</fo:block>
	   
	   				<fo:block text-align="left"    font-size="12pt" >MILL Invo No/Date :${supplierInvoiceId?if_exists} <#if supplierInvoiceDate?has_content>/ ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(supplierInvoiceDate, "dd-MMM-yyyy")}</#if></fo:block>
	   	   	        <fo:block text-align="center"    font-size="12pt" >&#160;&#160;&#160;&#160;</fo:block>
	   
	        <fo:block>     
		    <fo:table width="100%" border-style="solid"  align="right" table-layout="fixed"  font-size="12pt"> 
			<fo:table-column column-width="40%"/>
			<fo:table-column column-width="60%"/>
		
				<fo:table-body>
					<fo:table-row white-space-collapse="false">
						<fo:table-cell  >
						<fo:block text-align="left" font-weight="bold"   font-size="12pt" >${Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble((totAmount-schemeDeductionAmt)?string("#0.00")), "%indRupees-and-paiseRupees", locale).toUpperCase()}RUPEES ONLY.</fo:block>
						</fo:table-cell>
						<fo:table-cell >
						<fo:block text-align="center"    font-size="12pt" ></fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row white-space-collapse="false" >
						<fo:table-cell  border-style-right="hidden">
						<fo:block text-align="left"    font-size="12pt" >Destination       :${destination?if_exists}</fo:block>
						<fo:block text-align="left"    font-size="12pt" >Freight (RS.)     :${estimatedShipCost?if_exists}   </fo:block>
						<fo:block text-align="left"    font-size="12pt" >Due Date          :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invoiceDate, "dd-MMM-yyyy")}</fo:block>
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
	       
	        	<fo:block text-align="left" white-space-collapse="false"   font-size="12pt" >Prepared By              Supdt(Comm)     AM ©/DM©/Manager©         checked By F &amp; A Section </fo:block>
      		
              <fo:block text-align="left" font-weight="bold"    font-size="12pt" >TERMS &amp; CONDITIONS:</fo:block>
			   <fo:block text-align="left" white-space-collapse="false"   font-size="12pt" > * All payment  should be made by crossed cheque/draft in favour of 'National handloom </fo:block>
			   <fo:block text-align="left" white-space-collapse="false"   font-size="12pt" >&#160; Development corporation Limited payable at ____________ </fo:block>
			   <fo:block text-align="left" white-space-collapse="false"    font-size="12pt" >&#160;&#160;INTEREST will be charged @ 13.00% per annum on overdue Amount.</fo:block>
			   
			   
			    <fo:block text-align="left" white-space-collapse="false"   font-size="12pt" > * In case of any dispute,the case will be referred to an arbitrator mutually agreed upon </fo:block>
			   <fo:block text-align="left" white-space-collapse="false"   font-size="12pt" >&#160; whose will be final and binding E.&amp;.O.E</fo:block>
      		
      		
      		 <fo:block page-break-after="always"></fo:block>  
      		
      		<#if onbehalf == true>
      		
      		<fo:block>     
    <fo:table width="100%"   align="right" table-layout="fixed"  font-size="12pt"> 
	<fo:table-column column-width="57%"/>
	<fo:table-column column-width="43%"/>

		<fo:table-body>
			<fo:table-row white-space-collapse="false">
				<#--<fo:table-cell >
				<fo:block text-align="left"  font-weight="bold"  font-size="12pt" >${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, true)}</fo:block>
				<#list finalAddresList as eachDetail>
				<fo:block text-align="left"    font-size="12pt" >${eachDetail.key2?if_exists}</fo:block>
				</#list>
				<fo:block text-align="left"    font-size="12pt" >Your above confirmed indent goods despatched through M/S :${carrierName?if_exists}</fo:block>
				<fo:block text-align="left"    font-size="12pt" >LR/RRNo : ${carrierName?if_exists}       Dt : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invoiceDate, "dd-MMM-yyyy")}  </fo:block>
				</fo:table-cell>-->
				<fo:table-cell >
				<fo:block text-align="left"  font-weight="bold"  font-size="12pt" >Name of Depot Operating Agency(DOA):${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, true)}</fo:block>
				<fo:block text-align="left"    font-size="12pt" >DOA Order Number               :</fo:block>
				<fo:block text-align="left"    font-size="12pt" >DOA Order Date                 : <#if supplier?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(indentDate, "dd-MMM-yyyy")}</#if></fo:block>
				<fo:block text-align="left"    font-size="12pt" >NHDC Indent Date               : <#if supplier?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(indentDate, "dd-MMM-yyyy")}</#if></fo:block>
				<fo:block text-align="left"    font-size="12pt" >NHDC Sale Inv Date             : <#if supplier?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invoiceDate, "dd-MMM-yyyy")}</#if></fo:block>
				<fo:block text-align="left"    font-size="12pt" >NHDC Indent No.                : ${indentNo?if_exists}</fo:block>
				<fo:block text-align="left"    font-size="12pt" >NHDC Sale Inv No.              : ${invoiceId?if_exists}</fo:block>
				
				</fo:table-cell>
				<fo:table-cell >
				<fo:block text-align="left"     font-size="12pt" >Supplier Name         : <#if supplier?has_content>${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, supplier, true)}</#if></fo:block>
				<fo:block text-align="left"     font-size="12pt" >NHDC PO Date          : <#if poDate?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(poDate, "dd-MMM-yyyy")}</#if></fo:block>
				<fo:block text-align="left"     font-size="12pt" >NHDC PO Number        : ${poNumber?if_exists}</fo:block>
				<fo:block text-align="left"      font-size="12pt" >NHDC Supplier Invoice     : ${supplierInvoiceId?if_exists}</fo:block>
				<fo:block text-align="left"      font-size="12pt" >NHDC Supplier Invoice Date:<#if supplierInvoiceDate?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(supplierInvoiceDate, "dd-MMM-yyyy")?if_exists}</#if></fo:block>
				<fo:block text-align="left"      font-size="12pt" >LR Number             : ${lrNumber?if_exists}</fo:block>
				<fo:block text-align="left"      font-size="12pt" >LR Date               : <#if deliveryChallanDate?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(deliveryChallanDate, "dd-MMM-yyyy")}</#if></fo:block>
				<fo:block text-align="left"      font-size="12pt" >Freight               : </fo:block>
				</fo:table-cell>
		
			</fo:table-row>
		</fo:table-body>
	</fo:table>
	</fo:block>
          		     <fo:block text-align="center"    font-size="12pt" >&#160;&#160;&#160;&#160;</fo:block>
          		                       		     
          		     
          
         <fo:block>     
    <fo:table width="100%" border-style="solid"  align="right" table-layout="fixed"  font-size="12pt"> 
	<fo:table-column column-width="3%"/>
	<fo:table-column column-width="20%"/>
	<fo:table-column column-width="13%"/>
	<fo:table-column column-width="20%"/>
	<fo:table-column column-width="10%"/>
	<fo:table-column column-width="11%"/>
	<fo:table-column column-width="12%"/>
	<fo:table-column column-width="12%"/>


		<fo:table-body>
			<fo:table-row white-space-collapse="false">
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"    font-size="12pt" >S. No</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"    font-size="12pt" >Name of Individual Weaver</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"     font-size="12pt" >Passbook</fo:block>
				<fo:block text-align="center"     font-size="12pt" >Number</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"    font-size="12pt" >Item Name</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"    font-size="12pt" >Quantity</fo:block>
				<fo:block text-align="center"     font-size="12pt" >Kgs</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"    font-size="12pt" >value Before</fo:block>
				<fo:block text-align="center"     font-size="12pt" >Subsidy (RS)</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				 <fo:block text-align="center"    font-size="12pt" >10% Subsidy</fo:block>
     		     <fo:block text-align="center"     font-size="12pt" >Value(RS)</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				 <fo:block text-align="center"    font-size="12pt" >Value (Net of Subsidy)</fo:block>
				</fo:table-cell>
			</fo:table-row>
				</fo:table-body>
				
	     </fo:table>
	    </fo:block>
      		
      		
      		 <#assign sr = 1>
		     <#assign totQuantity = 0>
		     <#assign totAmount = 0>
		     <#assign totquotaQty = 0>
		     <#assign grandTot = 0>
		     
 
		     <fo:block>     
			    <fo:table width="100%" border-style="solid"  align="right" table-layout="fixed"  font-size="12pt"> 
				<fo:table-column column-width="3%"/>
				<fo:table-column column-width="20%"/>
				<fo:table-column column-width="13%"/>
				<fo:table-column column-width="20%"/>
				<fo:table-column column-width="10%"/>
				<fo:table-column column-width="11%"/>
				<fo:table-column column-width="12%"/>
				<fo:table-column column-width="12%"/>
			
			
					<fo:table-body>
		       <#list finaOnbehalflDetails as invoiceDetail>
		     <fo:table-row white-space-collapse="false">
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"  font-size="12pt" >${sr}</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="left"  font-size="12pt" >${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, invoiceDetail.get("partyId"), true)}</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="left"  font-size="12pt" ><#if invoiceDetail.get("passNo")?has_content>${invoiceDetail.get("passNo")?if_exists}</#if></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="left"  font-size="12pt" ><#if invoiceDetail.get("itemDescription")?has_content>${invoiceDetail.get("itemDescription")?if_exists}</#if></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<#assign totQuantity = totQuantity+invoiceDetail.get("quantity")>
				<fo:block text-align="center"  font-size="12pt" ><#if invoiceDetail.get("quantity")?has_content>${invoiceDetail.get("quantity")?if_exists}<#else>${0.00}</#if></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<#assign totAmount = totAmount+invoiceDetail.get("amount")>
				<fo:block text-align="center"  font-size="12pt" ><#if invoiceDetail.get("amount")?has_content>${invoiceDetail.get("amount")?if_exists}<#else>${0.00}</#if></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<#assign totquotaQty = totquotaQty+invoiceDetail.get("quotaQty")>
				<fo:block text-align="center"  font-size="12pt" ><#if invoiceDetail.get("quotaQty")?has_content>${invoiceDetail.get("quotaQty")?if_exists}<#else>${0.00}</#if></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<#assign amt = invoiceDetail.get("amount")>
				<#assign quotaQty = invoiceDetail.get("quotaQty")>
				<#assign grandTot = grandTot+(amt-quotaQty)>
				<fo:block text-align="center"  font-size="12pt" >${(amt-quotaQty)?if_exists}</fo:block>
				</fo:table-cell>
				</fo:table-row>
		   
		      <#assign sr = sr+1> 
		   
		   </#list>
			
			<fo:table-row white-space-collapse="false">
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="12pt" ></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"  font-size="12pt" >TOTAL</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="12pt" ></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="12pt" ></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"  font-size="12pt" >${totQuantity}</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"  font-size="12pt" >${totAmount}</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"  font-size="12pt" >${totquotaQty}</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"  font-size="12pt" >${grandTot}</fo:block>
				</fo:table-cell>
				</fo:table-row>
		</fo:table-body>
				
	</fo:table>
	</fo:block>
		     
      	</#if>	
      		
      		</fo:flow>
      		
      		
      		
      		
      	</fo:page-sequence>
      
     </#if>   
  </fo:root>
</#escape>
