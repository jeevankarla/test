
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
         
           <fo:block text-align="left"    font-size="10pt" >T.I.N No     : ${allDetailsMap.get("tinNumber")?if_exists}</fo:block>
           <fo:block text-align="left"  white-space-collapse="false"  font-size="10pt" >C.S.T No : ${allDetailsMap.get("cstNumber")?if_exists}                                          C.I.N No : ${allDetailsMap.get("cinNumber")?if_exists} </fo:block>
           <fo:block text-align="center" font-size="14pt" font-weight="bold"  white-space-collapse="false">SALE INVOICE <#if parameters.docType?has_content><#if parameters.docType=="Original">(Original Copy)<#else> (Duplicate Copy)</#if><#else> (Original Copy)</#if> </fo:block>
           <#--<fo:block text-align="center" font-size="14pt"   white-space-collapse="false">Under : <#if scheme == "MGPS_10Pecent">MGP 10% Scheme<#elseif scheme == "MGPS">MGPS<#elseif scheme == "General">General</#if><#if scheme != "General"><#if isDepot=="Y">(Depot)<#else>(Non Depot)</#if></#if></fo:block> -->
           <fo:block text-align="center" font-size="14pt" font-weight="bold"  white-space-collapse="false">NATIONAL HANDLOOM DEVELOPMENT CORPORATION LIMITED.</fo:block>
   		   <fo:block text-align="center" font-size="10pt" font-weight="bold"  white-space-collapse="false">${BOAddress?if_exists}</fo:block>
           <fo:block text-align="center" font-size="10pt" font-weight="bold"  white-space-collapse="false">E-MAIL:${BOEmail?if_exists}</fo:block>
           
        </fo:static-content>
                 				
        
        <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
           <fo:block>     
    <fo:table width="100%"   align="right" table-layout="fixed"  font-size="10pt"> 
	<fo:table-column column-width="35%"/>
	<fo:table-column column-width="40%"/>
	<fo:table-column column-width="25%"/>

     

		<fo:table-body>
			<fo:table-row white-space-collapse="false">
				<fo:table-cell >
				<fo:block text-align="left"  font-weight="bold"  font-size="10pt" >${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, soceity, true)}</fo:block>
				<#list finalAddresList as eachDetail>
				<fo:block text-align="left"   font-weight="bold" font-size="10pt" >${eachDetail.key2?if_exists}</fo:block>
				</#list>
				<fo:block text-align="left" font-weight="bold" font-size="10pt" >Tin No : ${CustomerTinNo?if_exists}</fo:block>
				<#--<fo:block text-align="left" font-weight="bold" font-size="10pt" >PassBook No : ${passNo?if_exists}</fo:block>-->
				</fo:table-cell>
				<fo:table-cell >
				<fo:block text-align="right"    font-size="10pt" keep-together="always" white-space-collapse="false" font-weight="bold">&#160;&#160;&#160;&#160;NHDC BILL NO   :${invoiceId?if_exists}</fo:block>
				<fo:block text-align="right"    font-size="10pt" keep-together="always" white-space-collapse="false">&#160;&#160;&#160;&#160;NHDC Indent No :${indentNo?if_exists}</fo:block>
				<fo:block text-align="right"    font-size="10pt" keep-together="always" white-space-collapse="false">&#160;&#160;&#160;&#160;NHDC PO No     :${poNumber?if_exists}</fo:block>
				<#--<fo:block text-align="right"    font-size="10pt" keep-together="always" white-space-collapse="false">&#160;&#160;&#160;&#160;User Agency Indent No/Date  :${externalOrderId?if_exists}</fo:block>-->
				</fo:table-cell>
				<fo:table-cell >
				<fo:block text-align="right"     font-size="10pt" font-weight="bold">DATE :<#if invoiceDate?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invoiceDate, "dd-MMM-yyyy")}</#if></fo:block>
				<fo:block text-align="right"     font-size="10pt" >DATE :<#if indentDate?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(indentDate, "dd-MMM-yyyy")}</#if></fo:block>
				<fo:block text-align="right"     font-size="10pt" >DATE :<#if poDate?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(poDate, "dd-MMM-yyyy")}</#if></fo:block>
				<#--<fo:block text-align="right"     font-size="10pt" >Tally Sale No : ${tallyRefNo?if_exists}</fo:block>-->
				</fo:table-cell>
			</fo:table-row>
		</fo:table-body>
	</fo:table>
	</fo:block>
		<fo:block text-align="left" font-size="11pt">Your above confirmed indent goods dispatched through M/S :${carrierName?if_exists} LR No :${lrNumber?if_exists} Dt:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(estimatedShipDate, "dd-MMM-yyyy")}  </fo:block>
        <fo:block text-align="center"    font-size="10pt" >&#160;&#160;&#160;&#160;</fo:block>
         <fo:block>     
    <fo:table width="100%" align="right" table-layout="fixed" font-size="10pt"> 
	<fo:table-column column-width="4%"/>
	<fo:table-column column-width="8%"/>
	<fo:table-column column-width="9%"/>
	<#if scheme == "MGPS_10Pecent">
	<fo:table-column column-width="7%"/>
	</#if>
	<#if (scheme == "MGPS_10Pecent") || (scheme == "MGPS")>
	<fo:table-column column-width="7%"/>
	</#if>
	<fo:table-column column-width="8%"/>
	<fo:table-column column-width="7%"/>
	<fo:table-column column-width="7%"/>
	<fo:table-column column-width="10%"/>
	<fo:table-column column-width="8%"/>
	<fo:table-column column-width="11%"/>
	<fo:table-column column-width="8%"/>
	<fo:table-column column-width="8%"/>
	<fo:table-column column-width="8%"/>
	<fo:table-column column-width="8%"/>


		<fo:table-body>
			<fo:table-row white-space-collapse="false">
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"    font-size="10pt" >S.No</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid" number-columns-spanned="3">
				<fo:block text-align="center"     font-size="10pt" >Description of Products</fo:block>
				</fo:table-cell>
				<#--><fo:table-cell border-style="solid">
				<fo:block text-align="center"     font-size="10pt" >Bale/</fo:block>
				<fo:block text-align="center"     font-size="10pt" >Bag No.</fo:block>
				<fo:block text-align="center"     font-size="10pt" >Box/marketing</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"    font-size="10pt" >Unit</fo:block>
				<!-- <fo:block text-align="center"    font-size="10pt" >(RS)</fo:block> -->
				<#--></fo:table-cell>-->
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"    font-size="10pt" >Total Qty</fo:block>
				<fo:block text-align="center"     font-size="10pt" >(Kgs)</fo:block>
				</fo:table-cell>
				<#if scheme == "MGPS_10Pecent">
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"    font-size="10pt" >10%</fo:block>
				<fo:block text-align="center"     font-size="10pt" >Qty(Kgs)</fo:block>
				</fo:table-cell>
				</#if>
				<#if (scheme == "MGPS_10Pecent") || (scheme == "MGPS")>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"    font-size="10pt" >MGPS</fo:block>
				<fo:block text-align="center"     font-size="10pt" >Qty(Kgs)</fo:block>
				</fo:table-cell>
				</#if>
				<fo:table-cell border-style="solid">
				<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Packing</fo:block>
				<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Size</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Packing</fo:block>
				<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">No</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				 <fo:block text-align="center"    font-size="10pt" >Rate/</fo:block>
				 <fo:block text-align="center"    font-size="10pt" >Kg</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				 <fo:block text-align="center"    font-size="10pt" >Amount</fo:block>
				 <fo:block text-align="center"    font-size="10pt" >(Rs)</fo:block>
				</fo:table-cell>
				
				<#if isItVatOrCst?has_content>
				<fo:table-cell border-style="solid">
				<#if isItVatOrCst?has_content && isItVatOrCst=="VAT_SALE">
				 <fo:block text-align="center"    font-size="10pt" >VAT</fo:block>
				 <#elseif isItVatOrCst?has_content && isItVatOrCst=="CST_SALE">
				 <fo:block text-align="center"    font-size="10pt" >CST</fo:block>
				 </#if>
				 <fo:block text-align="center"    font-size="10pt" >Amount</fo:block>
				  <fo:block text-align="center"    font-size="10pt" >(${taxPercentage?if_exists}%)</fo:block>
				</fo:table-cell>
				</#if>
				<#if isVatSurOrCstSur?has_content>
				<fo:table-cell border-style="solid">
				<#if isItVatOrCst?has_content && isItVatOrCst=="VAT_SALE">
				 <fo:block text-align="center"    font-size="10pt" >VAT</fo:block>
				 <#elseif isItVatOrCst?has_content && isItVatOrCst=="CST_SALE">
				 <fo:block text-align="center"    font-size="10pt" >CST</fo:block>
				 </#if>
				 <fo:block text-align="center"    font-size="10pt" >Surcharge</fo:block>
				 <fo:block text-align="center"    font-size="10pt" >Amount</fo:block>
				  <fo:block text-align="center"    font-size="10pt" >(${taxSurchargePur?if_exists}%)</fo:block>
				</fo:table-cell>
				 </#if>
				<#--<#if isExciseDuty?has_content>
				<fo:table-cell border-style="solid">
				 <fo:block text-align="center"    font-size="10pt" >Excise</fo:block>
				 <fo:block text-align="center"    font-size="10pt" >Duty</fo:block>
				  <fo:block text-align="center"    font-size="10pt" >Amount</fo:block>
				</fo:table-cell>
				</#if>-->
				<fo:table-cell border-style="solid">
				 <fo:block text-align="center"    font-size="10pt" >Total</fo:block>
				 <fo:block text-align="center"    font-size="10pt" >Amount</fo:block>
				</fo:table-cell>
			</fo:table-row>
		     <#assign sr = 1>
		     <#assign totQuantity = 0>
		     <#assign totPackingSizekgs = 0>
			  <#assign totPackets= 0>
		      <#assign totSchemeQty = 0>
		      <#assign totAmount = 0>
		      <#assign tempScheamQty = 0>
		      <#assign tempTotAmount = 0>
		      <#assign mgpsAndTotalDeductions = 0>
		      <#assign TotalmgpsQty=0>
		      <#assign totVatOrCstAmt= 0>
		      <#assign totVatOrCstSur = 0>
		       <#assign totExciseDuty = 0>
		      <#assign i = 0>
		       <#assign adjamt = 0>
		    <#list finalDetails as invoiceDetail>
		   
		     <fo:table-row white-space-collapse="false">
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"  font-size="10pt" >${sr}</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid" number-columns-spanned="3">
				<fo:block text-align="left"  font-size="10pt" >${invoiceDetail.get("prodDescription")}&#160;<#if invoiceDetail.get("remarks")?has_content>(${invoiceDetail.get("remarks")})</#if></fo:block>
				
				<#assign tempTotAmount = tempTotAmount+(invoiceDetail.get("schemeQty")*invoiceDetail.get("amount"))>
				
				<#assign tempScheamQty = tempScheamQty+invoiceDetail.get("schemeQty")>
					
				
                 <#if invoiceItemLevelAdjustments?has_content && !kanAndKalRo?has_content>		
                   <#assign alladjustList = invoiceItemLevelAdjustments.entrySet()>		 
				   <#list alladjustList as eachOne>
				       <#if eachOne.getKey() == i>				       
				        <#list eachOne.getValue() as each>  
				        
				        <#if each.invoiceItemTypeId == "TEN_PERCENT_SUBSIDY">
				         <#assign mgpsAndTotalDeductions = mgpsAndTotalDeductions+each.amount>
				        </#if>
				        
				        <#if each.invoiceItemTypeId != "TEN_PERCENT_SUBSIDY">
				        
				        <#--<fo:block text-align="left" font-weight="bold"  font-size="10pt" >&#160;</fo:block>
				        <fo:block text-align="left"  font-weight="bold"   font-size="10pt" ><#if each.description?has_content>${each.description?if_exists}<#else>${each.invoiceItemTypeId?if_exists}</#if><#if each.percentage?has_content>(${each.percentage?if_exists?string("#0")}%)</#if></fo:block>-->
				       
				       </#if>
				        </#list>
				       </#if>
				  </#list>
				 </#if>
				  <#if invoiceItemLevelAdjustments?has_content && kanAndKalRo?has_content>	
                   <#assign alladjustList = invoiceItemLevelAdjustments.entrySet()>		 
				   <#list alladjustList as eachOne>
				       <#if eachOne.getKey() == i>				       
				        <#list eachOne.getValue() as each>  
				        <#--><fo:block text-align="left" font-weight="bold"  font-size="10pt" >&#160;</fo:block>
				        <fo:block text-align="left"  font-weight="bold"   font-size="10pt" ><#if each.description?has_content>${each.description?if_exists}<#else>${each.invoiceItemTypeId?if_exists}</#if><#if each.percentage?has_content>(${each.percentage?if_exists?string("#0.00")}%)</#if></fo:block>-->
				        </#list>
				       </#if>
				  </#list>
				 </#if>
				</fo:table-cell>    
				
				<#--><fo:table-cell border-style="solid">
				<fo:block text-align="center"  font-size="10pt" ><#if invoiceDetail.get("baleQty")?has_content>${invoiceDetail.get("baleQty")?if_exists}<#else>0.00</#if></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"  font-size="10pt" ></fo:block>
				
				</fo:table-cell>-->
				<fo:table-cell border-style="solid">
				
				<#if invoiceDetail.get("quantity")?has_content>
				<#assign totQuantity = totQuantity+invoiceDetail.get("quantity")>
				</#if>
				<fo:block text-align="center"  font-size="10pt" ><#if invoiceDetail.get("quantity")?has_content>${invoiceDetail.get("quantity")?if_exists}<#else>&#160;</#if></fo:block>
				<#--<fo:block text-align="center"  font-size="10pt" ><#if invoiceDetail.get("baleQty")?has_content>${invoiceDetail.get("baleQty")?if_exists}(${invoiceDetail.get("unit")?if_exists})<#else>&#160;</#if></fo:block>-->
				
				
                <#assign totServiceCharge = 0>
				 <#assign serviceCharge = 0>
				 
                 <#if invoiceItemLevelAdjustments?has_content>		
                   <#assign alladjustList = invoiceItemLevelAdjustments.entrySet()>		 
				   <#list alladjustList as eachOne>
				       <#if eachOne.getKey() == i>				       
				        <#list eachOne.getValue() as each>  
				        
				        <#if each.description == "Service Charge">
				           <#assign serviceCharge = each.amount>
				           
				        </#if>
				        
				         <#if each.invoiceItemTypeId != "TEN_PERCENT_SUBSIDY">
				         <#--><fo:block text-align="center" font-weight="bold"  font-size="10pt" >${each.quantity}</fo:block>-->
				         </#if>
				        </#list>
				       </#if>
				  </#list>
				 </#if>
				 
				 				
				</fo:table-cell>
				<#if scheme == "MGPS_10Pecent">
				<fo:table-cell border-style="solid">
				<#if invoiceDetail.get("schemeQty")?has_content>
				<#assign totSchemeQty = totSchemeQty+invoiceDetail.get("schemeQty")>
				</#if>
				
				<fo:block text-align="center"  font-size="10pt" ><#if invoiceDetail.get("schemeQty")?has_content>${invoiceDetail.get("schemeQty")?if_exists}<#else>${0.00}</#if></fo:block>
				</fo:table-cell>
				</#if>
				<#if (scheme == "MGPS_10Pecent") || (scheme == "MGPS")>
				<#if invoiceDetail.get("mgpsQty")?has_content>
				<#assign TotalmgpsQty=TotalmgpsQty+(invoiceDetail.get("mgpsQty"))>
				</#if>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"  font-size="10pt" >${invoiceDetail.get("mgpsQty")}</fo:block>
				</fo:table-cell>
				</#if>
				<#assign totPackingSizekgs=totPackingSizekgs+invoiceDetail.get("packQuantity")>
				<fo:table-cell border-style="solid">
				<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false">${invoiceDetail.get("packQuantity")?if_exists} </fo:block>
				</fo:table-cell>
				<#--<#assign totPackets=totPackets+invoiceDetail.get("packets")>-->
				<#assign packetsNo = 0>
				<#assign packetsNo=invoiceDetail.get("quantity")/invoiceDetail.get("packQuantity")>
				<fo:table-cell border-style="solid">
				<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false">${packetsNo?string("#0.00")}</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
					
			    <#if kanAndKalRo?has_content> 
					<fo:block text-align="center"  font-size="10pt" ><#if invoiceDetail.get("unitPriceIncTax")?has_content>${invoiceDetail.get("unitPriceIncTax")?if_exists?string("#0.00")}<#else>${0.00}</#if></fo:block>
			    <#else>
					<fo:block text-align="center"  font-size="10pt" ><#if invoiceDetail.get("amount")?has_content>${invoiceDetail.get("amount")?if_exists?string("#0.00")} <#else>${0.00}</#if></fo:block>
				</#if>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<#if invoiceDetail.get("ToTamount")?has_content>
				<#assign totAmount = totAmount+(invoiceDetail.get("ToTamount"))>
				<#assign totWithoutTax=invoiceDetail.get("ToTamount")>	
				<fo:block text-align="center"  font-size="10pt" >${(invoiceDetail.get("ToTamount"))?string("#0.00")}</fo:block>
				</#if>
                 
				 <#--<#if invoiceItemLevelAdjustments?has_content  && !kanAndKalRo?has_content>		
                   <#assign alladjustList = invoiceItemLevelAdjustments.entrySet()>		 
				   <#list alladjustList as eachOne>
				       <#if eachOne.getKey() == i>				       
				        <#list eachOne.getValue() as each> 
				        <#if each.invoiceItemTypeId != "TEN_PERCENT_SUBSIDY">
		                <fo:block text-align="left" font-weight="bold"  font-size="10pt" >&#160;</fo:block>
				         <fo:block text-align="left" font-weight="bold"  font-size="10pt" >&#160;</fo:block>
				         <fo:block text-align="center" font-weight="bold"  font-size="10pt" >${each.itemValue?string("#0.00")}</fo:block>
				        
				         </#if>
				        </#list>
				       </#if>
				  </#list>
				 </#if>-->
				 
				  
				</fo:table-cell>
				<#assign cstOrVat=0>
				<#assign cstOrVatSur=0>	
				<#if isItVatOrCst?has_content>
					<fo:table-cell border-style="solid">
					
					<#if invoiceItemLevelAdjustments?has_content && kanAndKalRo?has_content>	
	                   <#assign alladjustList = invoiceItemLevelAdjustments.entrySet()>		 
					   <#list alladjustList as eachOne>
					       <#if eachOne.getKey() == i>	
					      		       
					        <#list eachOne.getValue() as each>
					    
					        <#if (each.invoiceItemTypeId =="CST_SALE") || (each.invoiceItemTypeId=="VAT_SALE")>	
							 <#assign totVatOrCstAmt=totVatOrCstAmt+(each.itemValue)>
							 <#assign cstOrVat=each.itemValue>	
							<fo:block text-align="right"  font-size="10pt" >${each.itemValue?string("#0.00")}</fo:block>
							
							</#if>
							
							
						
			                <#--<fo:block text-align="left" font-weight="bold"  font-size="10pt" >&#160;</fo:block>
					         <fo:block text-align="left" font-weight="bold"  font-size="10pt" >&#160;</fo:block>
					         <fo:block text-align="center" font-weight="bold"  font-size="10pt" >${each.itemValue?string("#0.00")}</fo:block>-->
					          <#assign adjamt=adjamt+each.itemValue> 
					        </#list>
					       </#if>
					  </#list>
					 </#if>
					 </fo:table-cell>
				  </#if>
				 <#if isVatSurOrCstSur?has_content>
					 <fo:table-cell border-style="solid">
						<#if invoiceItemLevelAdjustments?has_content && kanAndKalRo?has_content>	
	                   <#assign alladjustList = invoiceItemLevelAdjustments.entrySet()>		 
					   <#list alladjustList as eachOne>
					       <#if eachOne.getKey() == i>				       
					        <#list eachOne.getValue() as each>
					        
							  
							<#if (each.invoiceItemTypeId =="CST_SURCHARGE") || (each.invoiceItemTypeId=="VAT_SURCHARGE")>
							<#assign totVatOrCstSur =totVatOrCstSur+(each.itemValue)>
							<#assign cstOrVatSur=each.itemValue>
							<fo:block text-align="right"  font-size="10pt" >${each.itemValue?string("#0.00")}</fo:block>
							</#if>
						
			                <#--<fo:block text-align="left" font-weight="bold"  font-size="10pt" >&#160;</fo:block>
					         <fo:block text-align="left" font-weight="bold"  font-size="10pt" >&#160;</fo:block>
					         <fo:block text-align="center" font-weight="bold"  font-size="10pt" >${each.itemValue?string("#0.00")}</fo:block>-->
					          <#assign adjamt=adjamt+each.itemValue> 
					        </#list>
					       </#if>
					  </#list>
					 </#if>
					 </fo:table-cell>
				 </#if>
				 <#assign exciseAmt=0>
				<#--<#if isExciseDuty?has_content>
				 <fo:table-cell border-style="solid">
					
				<#if invoiceItemLevelAdjustments?has_content && kanAndKalRo?has_content>	
                   <#assign alladjustList = invoiceItemLevelAdjustments.entrySet()>		 
				   <#list alladjustList as eachOne>
				       <#if eachOne.getKey() == i>	
				      		       
				        <#list eachOne.getValue() as each>
				    
				        <#if (each.invoiceItemTypeId =="EXCISE_DUTY")>	
						 <#assign totExciseDuty=totExciseDuty+(each.itemValue)>
						 <#assign exciseAmt=each.itemValue>	
						<fo:block text-align="right"  font-size="10pt" >${each.itemValue?string("#0.00")}</fo:block>
						
						</#if>
						
						
					
		                <#--<fo:block text-align="left" font-weight="bold"  font-size="10pt" >&#160;</fo:block>
				         <fo:block text-align="left" font-weight="bold"  font-size="10pt" >&#160;</fo:block>
				         <fo:block text-align="center" font-weight="bold"  font-size="10pt" >${each.itemValue?string("#0.00")}</fo:block>-->
				          
				        <#--</#list>
				       </#if>
				  </#list>
				 </#if>
				  
				</fo:table-cell>
				</#if>-->
				 <fo:table-cell border-style="solid">
					<fo:block text-align="right"  font-size="10pt" >${totWithoutTax+cstOrVat+cstOrVatSur}</fo:block>
				</fo:table-cell>
				
				</fo:table-row>

    			<#assign i = i+1>
              
		      <#assign sr = sr+1> 
		   
		   </#list> 
			
			<fo:table-row white-space-collapse="false">
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="10pt" ></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid" number-columns-spanned="3">
				<fo:block text-align="center"  font-size="10pt" >TOTAL</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"  font-size="10pt" >${totQuantity} </fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<#--<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false">${totPackingSizekgs} </fo:block>-->
				<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false"></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<#--<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false">${totPackets} </fo:block>-->
				<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false"></fo:block>
				</fo:table-cell>
				<#if scheme == "MGPS_10Pecent">
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"  font-size="10pt" >${totSchemeQty}</fo:block>
				
				</fo:table-cell>
				</#if>
				<#if (scheme == "MGPS_10Pecent") || (scheme == "MGPS")>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"  font-size="10pt" >${TotalmgpsQty}</fo:block>
				</fo:table-cell>
				</#if>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"  font-size="10pt" ></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<#assign finalGrndToal=0>
				<#if kanAndKalRo?has_content>
					<#assign finalGrndToal=totAmount+totTaxAmount>
		   			<fo:block text-align="center"  font-size="10pt" >${(totAmount)?string("#0.00")}</fo:block>
				<#else>
					<#assign finalGrndToal=grandTotal+totTaxAmount2>
					<fo:block text-align="center"  font-size="10pt" >${(totAmount)?string("#0.00")}</fo:block>
	            </#if>
				</fo:table-cell>
				<#if isItVatOrCst?has_content>
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="10pt" >${totVatOrCstAmt}</fo:block>
				</fo:table-cell>
				</#if>
				<#if isVatSurOrCstSur?has_content>
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="10pt" >${totVatOrCstSur}</fo:block>
				</fo:table-cell>
				</#if>
				<#--<#if isExciseDuty?has_content>
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="10pt" >${totExciseDuty}</fo:block>
				</fo:table-cell>
				</#if>-->
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="10pt" >${(totAmount+totVatOrCstAmt+totVatOrCstSur)}</fo:block>
				</fo:table-cell>
								
				</fo:table-row>
				
		</fo:table-body>
				
	</fo:table>
	
	</fo:block>
	 <#if cFormAgnst?has_content && cFormAgnst == 2> 
		<fo:block text-align="left" font-weight="bold"  font-size="10pt" >AGAINST C FORM</fo:block>
	</#if>
	<#if scheme == "MGPS_10Pecent">                                                                      
	<#if bhuneshwarRo?has_content && bhuneshwarRo=="Yes">      
		<fo:block text-align="left" font-weight="bold"  font-size="12pt" >Subsidy allowed @ 10% on :${tempScheamQty?if_exists} Kgs on Rs.${(finalGrndToal?if_exists+ adjmntsTot?if_exists)?string("#0.00")}</fo:block>
	<#else>
		<fo:block text-align="left" font-weight="bold"  font-size="12pt" >Subsidy allowed @ 10% on :${tempScheamQty?if_exists} Kgs on Rs.${finalGrndToal?if_exists?string("#0.00")}</fo:block>
	</#if>
	
	</#if>
	<fo:block text-align="left" font-weight="bold"  font-size="10pt" ><#if C2E2Form?has_content><#if C2E2Form == "NO_E2_FORM">Transaction with out E2 form<#elseif C2E2Form == "E2_FORM">Transaction with E2 form<#elseif C2E2Form == "CST_NOCFORM">Transaction with out C form<#elseif C2E2Form == "CST_CFORM">AGAINST C FORM</#if></#if></fo:block>
	<fo:block text-align="left"    font-size="10pt" >&#160;</fo:block>
	<#if !kanAndKalRo?has_content>
	<fo:block text-align="left"  white-space-collapse="false" font-weight="bold"   font-size="10pt" >Supplier :                                                                         OTHER CHARGES :</fo:block>
	</#if>
	 <fo:block>
	      
    <fo:table width="100%"   align="right" table-layout="fixed"  font-size="10pt"> 
	<fo:table-column column-width="33%"/>
	<fo:table-column column-width="33%"/>
	<fo:table-column column-width="34%"/>


		<fo:table-body>
			<fo:table-row white-space-collapse="false">
				<fo:table-cell >
				<fo:block text-align="left"    font-size="10pt" >${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, supplier, true)}</fo:block>
				</fo:table-cell>
			</fo:table-row>

             <#assign remainingAdjustMents = 0>
             <#assign entryTax = 0>
             
             
		
            <#list invoiceRemainigAdjItemList as eachList>
            
            <#--<#if eachList.invoiceItemTypeId != "PRICE_DISCOUNT" && eachList.invoiceItemTypeId != "EXCISE_DUTY">-->
            <#if eachList.invoiceItemTypeId == "ENTRY_TAX" || eachList.invoiceItemTypeId == "EXCISE_DUTY" || eachList.invoiceItemTypeId == "FREIGHT_CHARGES">
            <#assign remainingAdjustMents = remainingAdjustMents+eachList.itemValue>
			<fo:table-row white-space-collapse="false">
				
			    <fo:table-cell >
					<fo:block text-align="left"    font-size="10pt" ></fo:block>
				</fo:table-cell>
				<fo:table-cell >
				
				</fo:table-cell>
				<fo:table-cell >
				
			<#--	<#if eachList.invoiceItemTypeId == "ENTRY_TAX">
				<fo:block text-align="right"    font-size="10pt" ><#if eachList.amount?has_content>${(eachList.itemValue)?string("#0.00")}</#if></fo:block>
				<#else>
				<fo:block text-align="right"    font-size="10pt" ><#if eachList.amount?has_content>${(eachList.itemValue)?string("#0.00")}</#if></fo:block>
				</#if>-->
				<fo:block text-align="right"    font-size="10pt" > <#if eachList.description?has_content>${eachList.description?if_exists}<#else>${eachList.invoiceItemTypeId?if_exists}</#if> :<#if eachList.itemValue?has_content>${(eachList.itemValue)?string("#0.00")}</#if></fo:block>
				</fo:table-cell>
			</fo:table-row>
			</#if>
			</#list>
		  
			<fo:table-row white-space-collapse="false">
				<fo:table-cell number-columns-spanned="2" >
         			 <fo:block text-align="center"    font-size="10pt" >&#160;&#160;&#160;&#160;</fo:block>
	   	          <#--  <#if scheme != "General"> --> 
	                <#--<fo:block text-align="left"    font-size="10pt" >Purchase Value (RS):<#if purInvoiceTOt?has_content>${purInvoiceTOt?string("#0.00")}</#if></fo:block>-->
	   			<#--	</#if>-->
	   				<fo:block text-align="left">Transaction Type: ${TransactionTypevalue?if_exists}</fo:block>
	   				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	   				<fo:block text-align="left"    font-size="10pt" >Mill Inv No/Date :${supplierInvoiceId?if_exists}<#if supplierInvoiceDate?has_content>/ ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(supplierInvoiceDate, "dd-MMM-yyyy")}</#if></fo:block>	
	   				 <fo:block text-align="left"    font-size="10pt" >Delivery Challan Date : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(deliveryChallanDate, "dd-MMM-yyyy")} </fo:block>
	        		 <fo:block text-align="left"    font-size="10pt" >Delivery Challan No : ${deliveryChallanNumber?if_exists}</fo:block>
	        </fo:table-cell>
	        <fo:table-cell  >
						<fo:block text-align="right" font-size="10pt">Sale Value(RS):${(finalGrndToal+remainingAdjustMents)?string("#0.00")?if_exists}</fo:block>
						
                
                <#if !kanAndKalRo?has_content && serchar?has_content>
               	 	<fo:block text-align="left"    font-size="10pt" >Service Charge: ${serchar?if_exists}</fo:block>
                </#if>

	        </fo:table-cell>
			</fo:table-row>
			<#if scheme == "MGPS_10Pecent">
			<fo:table-row white-space-collapse="false">
			 <fo:table-cell >
				<fo:block text-align="left"    font-size="10pt" ></fo:block>
				</fo:table-cell>
				
				<fo:table-cell number-columns-spanned="2">
				
				<fo:block text-align="right"    font-size="10pt" >&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;<#if scheme == "MGPS_10Pecent">MGP 10% Scheme<#elseif scheme == "MGPS">MGPS<#elseif scheme == "General">General</#if> Deduction
				${mgpsAmt?if_exists?string("#0.00")}</fo:block>
				
				</fo:table-cell>
				
			</fo:table-row>
			</#if>
			
			<fo:table-row white-space-collapse="false">
			 <fo:table-cell >
				<fo:block text-align="left"    font-size="10pt" ></fo:block>
				</fo:table-cell>
				
				<#--<fo:table-cell number-columns-spanned="2">
				<#list invoiceRemainigAdjItemList as eachList>
				<#if eachList.invoiceItemTypeId == "ENTRY_TAX">
				<fo:block text-align="right"    font-size="10pt" >&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;<#if eachList.description?has_content>${eachList.description?if_exists}<#else>${eachList.invoiceItemTypeId?if_exists}</#if>&#160;&#160;&#160;&#160;&#160;&#160;${eachList.itemValue?if_exists}</fo:block>
				</#if>
				</#list>
				</fo:table-cell>-->
				
			</fo:table-row>
			
			<fo:table-row white-space-collapse="false">
			 <fo:table-cell >
				<fo:block text-align="right"    font-size="10pt" ></fo:block>
				</fo:table-cell>
				<fo:table-cell >
				<fo:block text-align="right"    font-size="10pt" ></fo:block>
				</fo:table-cell>
				<fo:table-cell >
				<fo:block text-align="right"    font-size="10pt" >--------------</fo:block>
				<#assign finalTOtal = (grandTotal+mgpsAndTotalDeductions)>
				<#assign finalTOtal = (finalTOtal+remainingAdjustMents)>
				
					<fo:block text-align="right" font-weight="bold"   font-size="10pt" >TOTAL VALUE (RS.):   ${((finalGrndToal)+remainingAdjustMents)?string("#0.00")}</fo:block>
				
  				
				<fo:block text-align="right"    font-size="10pt" >--------------</fo:block>
				</fo:table-cell>
			</fo:table-row>
			
			
			
	       </fo:table-body>
       	</fo:table>
	   </fo:block>
	   	<fo:block>  
		    <fo:table width="100%" border-style="solid"  align="right" table-layout="fixed"  font-size="10pt"> 
			<fo:table-column column-width="40%"/>
			<fo:table-column column-width="60%"/>
		
				<fo:table-body>
					<fo:table-row white-space-collapse="false">
						<fo:table-cell number-columns-spanned="2" >
						<fo:block text-align="left" font-weight="bold"   font-size="10pt" >${Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble(((finalGrndToal)+remainingAdjustMents)?string("#0.00")), "%indRupees-and-paiseRupees", locale).toUpperCase()} RUPEES ONLY.</fo:block>
						</fo:table-cell>
						
					</fo:table-row>
					<fo:table-row white-space-collapse="false" >
						<fo:table-cell  border-style-right="hidden">
						<fo:block text-align="left"  font-size="10pt" >Destination       :${destination?if_exists}</fo:block>
						</fo:table-cell>
						<fo:table-cell  border-style-right="hidden">
						<fo:block text-align="left" keep-together="always"    font-size="10pt" >&#160;&#160;</fo:block>
						</fo:table-cell>
						
					</fo:table-row>	
					<fo:table-row white-space-collapse="false" >
						<fo:table-cell  border-style-right="hidden">
						<#--<fo:block text-align="left" keep-together="always"    font-size="10pt" >Freight (RS.)     :<#if estimatedShipCost?has_content>${estimatedShipCost?if_exists?string("#0.00")}</#if>   </fo:block>-->
						</fo:table-cell>
						
						<fo:table-cell  border-style-right="hidden">
						<fo:block text-align="left" keep-together="always"   font-size="10pt" >Due Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(DueDate, "dd-MMM-yyyy")}</fo:block>
						</fo:table-cell>
						
				  </fo:table-row>
			       </fo:table-body>
		       	</fo:table>
	       </fo:block>
	       	   	        <fo:block text-align="center"    font-size="10pt" >&#160;&#160;&#160;&#160;</fo:block>
	       	   	        <fo:block text-align="center"    font-size="10pt" >&#160;&#160;&#160;&#160;</fo:block>
	       	   	        <fo:block text-align="center"    font-size="10pt" >&#160;&#160;&#160;&#160;</fo:block>
	       
	        	<fo:block text-align="left" white-space-collapse="false"   font-size="10pt" >Prepared By     Sr.Officer(C)   AM(C)/DM(C)/Manager(C)    Checked By F &amp; A Section </fo:block>
	       	   	        <fo:block text-align="center"    font-size="10pt" >&#160;&#160;&#160;&#160;</fo:block>
      		<fo:block>     
		    <fo:table width="100%"   align="right" table-layout="fixed"  font-size="8pt"> 
			<fo:table-column column-width="40%"/>
			<fo:table-column column-width="60%"/>
		
				<fo:table-body>
					<fo:table-row white-space-collapse="false">
						<fo:table-cell border-style-left="hidden" number-columns-spanned="2" >
							<fo:block text-align="right"  keep-together="always"  font-weight="bold"  font-size="10pt" >for NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD.</fo:block>
						</fo:table-cell>
						
					</fo:table-row>
					<fo:table-row white-space-collapse="false">
						<fo:table-cell border-style-left="hidden" number-columns-spanned="2" >
							<fo:block text-align="left"    font-size="10pt" >&#160;</fo:block>
							<fo:block text-align="left"    font-size="10pt" >&#160;</fo:block>
						</fo:table-cell>
						
					</fo:table-row>
					<fo:table-row white-space-collapse="false">
						<fo:table-cell border-style-left="hidden" number-columns-spanned="2">
							<fo:block text-align="center" font-size="10pt" >&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;                                            (Authorised Signatory)</fo:block>
						</fo:table-cell>
						
					</fo:table-row>
					
			       </fo:table-body>
		       	</fo:table>
	       </fo:block>
      	
              
			   <fo:block text-align="left" white-space-collapse="false"   font-size="10pt" > * All payment  should be made by RTGS/NEFT/CHECK/DRAFT in favour of 'National Handloom Development Corp Ltd'.</fo:block>
			   <fo:block text-align="left" white-space-collapse="false"    font-size="10pt" >* INTEREST will be charged  @___________ % per annum on overdue Amount.</fo:block> 
			   
			   <#-- <fo:block text-align="left" white-space-collapse="false"   font-size="10pt" > * In case of any dispute,the case will be referred to an arbitrator mutually agreed upon </fo:block>
			   <fo:block text-align="left" white-space-collapse="false"   font-size="10pt" >&#160; whose will be final and binding E.&amp;.O.E</fo:block>-->
				<fo:block text-align="left" font-weight="bold"    font-size="10pt" >TERMS &amp; CONDITIONS:</fo:block>
	       	  <fo:block text-align="left" white-space-collapse="false"   font-size="10pt" > 1. Rates indicated are subjects to change without notice and the rates applicable will be the one ruling at the time of delivery.</fo:block>
	       	  <fo:block text-align="left" white-space-collapse="false"   font-size="10pt" > 2. Freight, Octroi and other charges if any will be extra and has to be borne by the buyer.</fo:block>
	       	   <fo:block text-align="left" white-space-collapse="false"   font-size="10pt" > 3. Packing charges for small packings are charged extra.</fo:block>
	       	   <fo:block text-align="left" white-space-collapse="false"   font-size="10pt" > 4. Supplies are subject to availability.</fo:block>
	       	   <fo:block text-align="left" white-space-collapse="false"   font-size="10pt" > 5. The weight recorded in our invoice shall be final and will be binding on buyer.</fo:block>
	       	   <fo:block text-align="center"   font-weight="bold" font-size="10pt" >&#160;&#160;&#160;&#160;</fo:block>
	       	   <#if roIDForAdd?has_content && roIDForAdd=="INT2">
	       	   <fo:block text-align="left"   font-weight="bold" font-size="10pt" >&#160;&#160;&#160;&#160;RTGS/NEFT Detail:</fo:block>
	       	   <fo:block text-align="left"   font-weight="bold" font-size="10pt" >&#160;&#160;&#160;&#160;NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD.</fo:block>
	       	   <fo:block text-align="left"   font-weight="bold" font-size="10pt" >&#160;&#160;&#160;&#160;AXIS BANK LTD.</fo:block>
	       	    <fo:block text-align="left"   font-weight="bold" font-size="10pt" >&#160;&#160;&#160;&#160;G.T ROAD PANIPAT-132 103</fo:block>
	       	    <fo:block text-align="left"   font-weight="bold" font-size="10pt" >&#160;&#160;&#160;&#160;A/C No. 240010200012865</fo:block>
	       	   <fo:block text-align="left"   font-weight="bold" font-size="10pt" >&#160;&#160;&#160;&#160;IFSC Code No: UTIB0000240</fo:block>
	       	  
	       	   
      		   </#if>
      		 <fo:block page-break-after="always"></fo:block>  
      		 
      		
      		<#if onbehalf == true>
      		
      		<fo:block>     
      		
      		
    <fo:table width="100%"   align="right" table-layout="fixed"  font-size="10pt"> 
	<fo:table-column column-width="57%"/>
	<fo:table-column column-width="43%"/>

		<fo:table-body>
			<fo:table-row white-space-collapse="false">
				<#--<fo:table-cell >
				<fo:block text-align="left"  font-weight="bold"  font-size="10pt" >${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, true)}</fo:block>
				<#list finalAddresList as eachDetail>
				<fo:block text-align="left"    font-size="10pt" >${eachDetail.key2?if_exists}</fo:block>
				</#list>
				<fo:block text-align="left"    font-size="10pt" >Your above confirmed indent goods despatched through M/S :${carrierName?if_exists}</fo:block>
				<fo:block text-align="left"    font-size="10pt" >LR/RRNo : ${carrierName?if_exists}       Dt : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invoiceDate, "dd-MMM-yyyy")}  </fo:block>
				</fo:table-cell>-->
				<fo:table-cell >
				<fo:block text-align="left"  font-weight="bold"  font-size="10pt" >Name of Depot Operating Agency(DOA):${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, true)}</fo:block>
				<fo:block text-align="left"    font-size="10pt" >DOA Order Number               :</fo:block>
				<fo:block text-align="left"    font-size="10pt" >DOA Order Date                 : <#if supplier?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(indentDate, "dd-MMM-yyyy")}</#if></fo:block>
				<fo:block text-align="left"    font-size="10pt" >NHDC Indent Date               : <#if supplier?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(indentDate, "dd-MMM-yyyy")}</#if></fo:block>
				<fo:block text-align="left"    font-size="10pt" >NHDC Sale Inv Date             : <#if supplier?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invoiceDate, "dd-MMM-yyyy")}</#if></fo:block>
				<fo:block text-align="left"    font-size="10pt" >NHDC Indent No.                : ${indentNo?if_exists}</fo:block>
				<fo:block text-align="left"    font-size="10pt" >NHDC Sale Inv No.              : ${invoiceId?if_exists}</fo:block>
				   
				</fo:table-cell>
				<fo:table-cell >
				<fo:block text-align="left"     font-size="10pt" >Supplier Name         : <#if supplier?has_content>${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, supplier, true)}</#if></fo:block>
				<fo:block text-align="left"     font-size="10pt" >NHDC PO Date          : <#if poDate?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(poDate, "dd-MMM-yyyy")}</#if></fo:block>
				<fo:block text-align="left"     font-size="10pt" >NHDC PO Number        : ${poNumber?if_exists}</fo:block>
				<fo:block text-align="left"     font-size="10pt" >NHDC Supplier Invoice : ${supplierInvoiceId?if_exists}</fo:block>
				<fo:block text-align="left"     font-size="10pt" >NHDC Supplier Invoice</fo:block>
				<fo:block text-align="left"     font-size="10pt" >Date                  :<#if supplierInvoiceDate?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(supplierInvoiceDate, "dd-MMM-yyyy")?if_exists}</#if></fo:block>
				
				 
				
				<fo:block text-align="left"      font-size="10pt" >LR Number             : ${lrNumber?if_exists}</fo:block>
				<fo:block text-align="left"      font-size="10pt" >LR Date               : <#if deliveryChallanDate?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(deliveryChallanDate, "dd-MMM-yyyy")}</#if></fo:block>
				</fo:table-cell>
		
			</fo:table-row>
		</fo:table-body>   
	</fo:table>
	</fo:block>
          		     <fo:block text-align="center"    font-size="10pt" >&#160;&#160;&#160;&#160;</fo:block>
          		                       		     
          		     
          
         <fo:block>     
    <fo:table width="100%" border-style="solid"  align="right" table-layout="fixed"  font-size="10pt"> 
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
				<fo:block text-align="center"    font-size="10pt" >S. No</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"    font-size="10pt" >Name of Individual Weaver</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"     font-size="10pt" >Passbook</fo:block>
				<fo:block text-align="center"     font-size="10pt" >Number</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"    font-size="10pt" >Item Name</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"    font-size="10pt" >Quantity</fo:block>
				<fo:block text-align="center"     font-size="10pt" >(Kgs)</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"    font-size="10pt" >value Before</fo:block>
				<fo:block text-align="center"     font-size="10pt" >Subsidy (RS)</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				 <fo:block text-align="center"    font-size="10pt" >10% Subsidy</fo:block>
     		     <fo:block text-align="center"     font-size="10pt" >Value(RS)</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				 <fo:block text-align="center"    font-size="10pt" >Value (Net of Subsidy)</fo:block>
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
			    <fo:table width="100%" border-style="solid"  align="right" table-layout="fixed"  font-size="10pt"> 
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
				<fo:block text-align="center"  font-size="10pt" >${sr}</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="left"  font-size="10pt" >${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, invoiceDetail.get("partyId"), true)}</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="left"  font-size="10pt" ><#if invoiceDetail.get("passNo")?has_content>${invoiceDetail.get("passNo")?if_exists}</#if></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="left"  font-size="10pt" ><#if invoiceDetail.get("itemDescription")?has_content>${invoiceDetail.get("itemDescription")?if_exists}</#if></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<#assign totQuantity = totQuantity+invoiceDetail.get("quantity")>
				<fo:block text-align="center"  font-size="10pt" ><#if invoiceDetail.get("quantity")?has_content>${invoiceDetail.get("quantity")?if_exists}<#else>${0.00}</#if></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<#assign totAmount = totAmount+invoiceDetail.get("amount")>
				<fo:block text-align="center"  font-size="10pt" ><#if invoiceDetail.get("amount")?has_content>${invoiceDetail.get("amount")?if_exists}<#else>${0.00}</#if></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<#assign totquotaQty = totquotaQty+invoiceDetail.get("quotaQty")>
				<fo:block text-align="center"  font-size="10pt" ><#if invoiceDetail.get("quotaQty")?has_content>${invoiceDetail.get("quotaQty")?if_exists}<#else>${0.00}</#if></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<#assign amt = invoiceDetail.get("amount")>
				<#assign quotaQty = invoiceDetail.get("quotaQty")>
				<#assign grandTot = grandTot+(amt-quotaQty)>
				<fo:block text-align="center"  font-size="10pt" >${(amt-quotaQty)?if_exists}</fo:block>
				</fo:table-cell>
				</fo:table-row>   
		   
		      <#assign sr = sr+1> 
		   
		   </#list>
			
			<fo:table-row white-space-collapse="false">
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="10pt" ></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"  font-size="10pt" >TOTAL</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="10pt" ></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="right"  font-size="10pt" ></fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"  font-size="10pt" >${totQuantity}</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"  font-size="10pt" >${totAmount}</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"  font-size="10pt" >${totquotaQty}</fo:block>
				</fo:table-cell>
				<fo:table-cell border-style="solid">
				<fo:block text-align="center"  font-size="10pt" >${grandTot}</fo:block>
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