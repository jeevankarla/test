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
    <#if !finaOnbehalflDetails?has_content>
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
         
           <fo:block text-align="center" font-size="14pt"   white-space-collapse="false">Under : <#if scheme == "MGPS_10Pecent">MGP 10% Scheme<#elseif scheme == "MGPS">MGPS<#elseif scheme == "General">General</#if></fo:block> 
           <fo:block text-align="center" font-size="14pt" font-weight="bold"  white-space-collapse="false">NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD.</fo:block>
   		   <fo:block text-align="center" font-size="12pt" font-weight="bold"  white-space-collapse="false">S-13/36, SRI RAM MARKET</fo:block>
           <fo:block text-align="center" font-size="12pt" font-weight="bold" white-space-collapse="false">VARANASI-221002</fo:block>
           <fo:block text-align="center" font-size="12pt" font-weight="bold" text-decoration="underline"  white-space-collapse="false">E-MAIL:nhdccitdvns@yahoo.in</fo:block>
           <fo:block text-align="center" font-size="13pt" font-weight="bold"  white-space-collapse="false">Weaver Details</fo:block>
           
       			
                  		     <fo:block text-align="center"    font-size="12pt" >&#160;&#160;&#160;&#160;</fo:block>
                  		     <fo:block text-align="center"    font-size="12pt" >&#160;&#160;&#160;&#160;</fo:block>
        
        
        <fo:block text-align="center" font-weight="bold"  font-size="10pt" >Details of Individual Weavers Placing Yarn requirement through Depot Operating Agency</fo:block>
        <fo:block text-align="center"    font-size="12pt" >--------------------------------------------------------------------------</fo:block>
               		     <fo:block text-align="center"    font-size="12pt" >&#160;&#160;&#160;&#160;</fo:block>
     
   
    <fo:block>     
    <fo:table width="100%"   align="right" table-layout="fixed"  font-size="12pt"> 
	<fo:table-column column-width="60%"/>
	<fo:table-column column-width="40%"/>

		<fo:table-body>
			<fo:table-row white-space-collapse="false">
				<#--<fo:table-cell >
				<fo:block text-align="left"  font-weight="bold"  font-size="12pt" >${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, true)}</fo:block>
				<#list finalAddresList as eachDetail>
				<fo:block text-align="left"    font-size="12pt" >${eachDetail.key2?if_exists}</fo:block>
				</#list>
				<fo:block text-align="left"    font-size="12pt" >Your above confirmed indent goods despatched through M/S :${carrierName?if_exists}</fo:block>
				<fo:block text-align="left"    font-size="12pt" >LR/RRNo : ${carrierName?if_exists}       Dt : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invoiceDate, "dd-MM-yyyy")}  </fo:block>
				</fo:table-cell>-->
				<fo:table-cell >
				<fo:block text-align="left"  font-weight="bold"  font-size="12pt" >Name of Depot Operating Agency(DAO):${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, true)}</fo:block>
				<fo:block text-align="left"    font-size="12pt" >DAO Order Number               :</fo:block>
				<fo:block text-align="left"    font-size="12pt" >DAO Order Date                 : <#if supplier?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(indentDate, "dd-MMM-yyyy")}</#if></fo:block>
				<fo:block text-align="left"    font-size="12pt" >NHDC Indent No.                : ${indentNo?if_exists}</fo:block>
				<fo:block text-align="left"    font-size="12pt" >NHDC Indent Date               : <#if supplier?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(indentDate, "dd-MMM-yyyy")}</#if></fo:block>
				<fo:block text-align="left"    font-size="12pt" >NHDC Sale Inv No.              : ${invoiceId?if_exists}</fo:block>
				<fo:block text-align="left"    font-size="12pt" >NHDC Sale Inv Date             : <#if supplier?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invoiceDate, "dd-MM-yyyy")}</#if></fo:block>
				</fo:table-cell>
				<fo:table-cell >
				<fo:block text-align="left"     font-size="12pt" >Supplier Name         : <#if supplier?has_content>${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, supplier, true)}</#if></fo:block>
				<fo:block text-align="left"     font-size="12pt" >NHDC PO Number        : ${poNum?if_exists}</fo:block>
				<fo:block text-align="left"     font-size="12pt" >NHDC PO Number        : <#if poDate?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(poDate, "dd-MMM-yyyy")}</#if></fo:block>
				<fo:block text-align="left"      font-size="12pt" >NHDC Sale Inv No.     : ${supplierInvoiceId?if_exists}</fo:block>
				<fo:block text-align="left"      font-size="12pt" >NHDC Sale Inv Date.   : <#if supplierInvoiceDate?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(supplierInvoiceDate, "dd-MM-yyyy")?if_exists}</#if></fo:block>
				<fo:block text-align="left"      font-size="12pt" >LR Number             : ${lrNumber?if_exists}</fo:block>
				<fo:block text-align="left"      font-size="12pt" >LR Date               : <#if shipmentDate?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(shipmentDate, "dd-MM-yyyy")}</#if></fo:block>
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
		   </fo:static-content>	
		     <#assign sr = 1>
		     <#assign totQuantity = 0>
		     <#assign totAmount = 0>
		     <#assign totquotaQty = 0>
		     <#assign grandTot = 0>
		     
		     <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
 <fo:block text-align="center"    font-size="12pt" >&#160;&#160;&#160;&#160;</fo:block>
 <fo:block text-align="center"    font-size="12pt" >&#160;&#160;&#160;&#160;</fo:block>
 <fo:block text-align="center"    font-size="12pt" >&#160;&#160;&#160;&#160;</fo:block>
<fo:block text-align="center"    font-size="12pt" >&#160;&#160;&#160;&#160;</fo:block>
<fo:block text-align="center"    font-size="12pt" >&#160;&#160;&#160;&#160;</fo:block>
<fo:block text-align="center"    font-size="12pt" >&#160;&#160;&#160;&#160;</fo:block>
<fo:block text-align="center"    font-size="12pt" >&#160;&#160;&#160;&#160;</fo:block>
<fo:block text-align="center"    font-size="12pt" >&#160;&#160;&#160;&#160;</fo:block>
<fo:block text-align="center"    font-size="12pt" >&#160;&#160;&#160;&#160;</fo:block>
<fo:block text-align="center"    font-size="12pt" >&#160;&#160;&#160;&#160;</fo:block>
<fo:block text-align="center"    font-size="12pt" >&#160;&#160;&#160;&#160;</fo:block>
<fo:block text-align="center"    font-size="12pt" >&#160;&#160;&#160;&#160;</fo:block>
<fo:block text-align="center"    font-size="12pt" >&#160;&#160;&#160;&#160;</fo:block>
<fo:block text-align="center"    font-size="12pt" >&#160;&#160;&#160;&#160;</fo:block>
<fo:block text-align="center"    font-size="12pt" >&#160;&#160;&#160;&#160;</fo:block>
<fo:block text-align="center"    font-size="12pt" >&#160;&#160;&#160;&#160;</fo:block>
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
		       <#list finaOnbehalflDetails as invoiceDetail>
		     <fo:table-row white-space-collapse="false">
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"  font-size="12pt" >${sr}</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="left"  font-size="12pt" >${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, invoiceDetail.get("partyId"), true)}</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="12pt" ><#if invoiceDetail.get("passNo")?has_content>${invoiceDetail.get("passNo")?if_exists}</#if></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="left"  font-size="12pt" ><#if invoiceDetail.get("itemDescription")?has_content>${invoiceDetail.get("itemDescription")?if_exists}</#if></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<#assign totQuantity = totQuantity+invoiceDetail.get("quantity")>
				<fo:block text-align="right"  font-size="12pt" ><#if invoiceDetail.get("quantity")?has_content>${invoiceDetail.get("quantity")?if_exists}<#else>${0.00}</#if></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<#assign totAmount = totAmount+invoiceDetail.get("amount")>
				<fo:block text-align="right"  font-size="12pt" ><#if invoiceDetail.get("amount")?has_content>${invoiceDetail.get("amount")?if_exists?string("#0.000")}<#else>${0.00}</#if></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<#assign totquotaQty = totquotaQty+invoiceDetail.get("quotaQty")>
				<fo:block text-align="right"  font-size="12pt" ><#if invoiceDetail.get("quotaQty")?has_content>${invoiceDetail.get("quotaQty")?if_exists?string("#0.000")}<#else>${0.00}</#if></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<#assign amt = invoiceDetail.get("amount")>
				<#assign quotaQty = invoiceDetail.get("quotaQty")>
				<#assign grandTot = grandTot+(amt-quotaQty)>
				<fo:block text-align="right"  font-size="12pt" >${(amt-quotaQty)?if_exists}</fo:block>
				</fo:table-cell>
				</fo:table-row>
		   
		      <#assign sr = sr+1> 
		   
		   </#list>
			
			<fo:table-row white-space-collapse="false">
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="12pt" ></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"  font-size="12pt" >TOTAl</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="12pt" ></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="12pt" ></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="12pt" >${totQuantity}</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="12pt" >${totAmount?string("#0.000")}</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="12pt" >${totquotaQty?string("#0.000")}</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="12pt" >${grandTot?string("#0.00")}</fo:block>
				</fo:table-cell>
				</fo:table-row>
		</fo:table-body>
				
	</fo:table>
	</fo:block>
	        </fo:flow>
      	</fo:page-sequence>
      
     </#if>   
  </fo:root>
</#escape>
