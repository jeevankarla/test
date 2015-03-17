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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in" margin-left=".5in" margin-right=".5in">
                <fo:region-body margin-top=".4in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "TaxInvoice.pdf")}

 <#if invoiceSlipsMap?has_content>
        <#assign partyInvoiceMap = invoiceSlipsMap.entrySet()> 
        <#list  partyInvoiceMap as eachInvoice>
        <#assign invoiceDetail = eachInvoice.getValue()>
        <#assign fromPartyDetail = invoiceDetail.get('fromPartyDetail')>
        <#assign toPartyDetail = invoiceDetail.get('toPartyDetail')>
        <#assign shipment = invoiceDetail.get('shipment')>
        <#assign billingAddress = invoiceDetail.get('billingAddress')>
        <#assign invoice = invoiceDetail.get('invoice')>
        <#assign invoiceNo = invoiceDetail.get('invoiceNo')>
        <#assign invoiceItems = invoiceDetail.get('invoiceItems')>
        <#assign invoiceTaxItems = invoiceDetail.get('invoiceTaxItems')>
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
	<fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-weight="bold"   keep-together="always"> MOTHER DAIRY, GKVK POST, YELAHANKA, BANGALORE 560 065</fo:block>
 	<fo:block text-align="center" border-style="solid">
 	<fo:table  table-layout="fixed" width="100%" space-before="0.2in">
		 <fo:table-column column-width="38%"/>
	     <fo:table-column column-width="23%"/>
	     <fo:table-column column-width="22%"/>
	     <fo:table-column column-width="17%"/>
	     <fo:table-body>
		     <fo:table-row> 
			 	<fo:table-cell> 
			      <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always" font-weight = "bold">TIN NO: <#if fromPartyDetail?has_content>${fromPartyDetail.get('TIN_NUMBER')?if_exists}</#if></fo:block>
				  <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always" font-weight = "bold">CST NO: <#if fromPartyDetail?has_content>${fromPartyDetail.get('CST_NUMBER')?if_exists}</#if></fo:block>
				  <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				</fo:table-cell>
				<fo:table-cell>   						
			 	     <fo:block text-align="center" white-space-collapse="false" font-weight="bold" keep-together="always">TAX INVOICE</fo:block>
			 	</fo:table-cell>
		 	  </fo:table-row>	
		 	  <fo:table-row> 
			     <fo:table-cell border-style="solid" >   						
					<fo:block>
						<fo:table  table-layout="fixed" width="100%" space-before="0.2in">
							 <fo:table-body>
							 	<fo:table-row> 
									 <fo:table-cell border-style="solid" > 
									    <fo:block font-weight="bold">SELLER'S NAME </fo:block>  
									 </fo:table-cell>   
								 </fo:table-row> 
								 <#assign fromPartyAddressResult = dispatcher.runSync("getPartyPostalAddress", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", "Company"?if_exists, "userLogin", userLogin))/>
								 <fo:table-row> 
									<fo:table-cell>
									    <fo:block  text-align="left" font-size="12pt" white-space-collapse="false" font-weight = "bold">From: <#if fromPartyAddressResult?has_content>MOTHER DAIRY, </#if></fo:block>
	            						<fo:block  text-align="left" font-size="12pt" white-space-collapse="false" font-weight = "bold"><#if fromPartyAddressResult.address1?has_content>${fromPartyAddressResult.address1?if_exists} </#if></fo:block>
									 </fo:table-cell>   
								 </fo:table-row> 
								  <fo:table-row> 
									 <fo:table-cell border-style="solid" > 
									    <fo:block font-weight="bold">BUYER'S NAME </fo:block>  
									 </fo:table-cell>   
								 </fo:table-row> 
								  <fo:table-row> 
									 <fo:table-cell>
									    <fo:block text-align="left" font-size="12pt" white-space-collapse="false" font-weight = "bold" wrap-option="wrap">To: <#if billingAddress?has_content>${billingAddress.get("toName")?if_exists} </#if></fo:block>
	            						<fo:block text-align="left" font-size="12pt" white-space-collapse="false" font-weight = "bold" wrap-option="wrap"><#if billingAddress?has_content>${billingAddress.get("address1")?if_exists} </#if></fo:block>
	            						<fo:block text-align="left" font-size="12pt" white-space-collapse="false" font-weight = "bold" wrap-option="wrap"><#if billingAddress?has_content>${billingAddress.get("address2")?if_exists} </#if></fo:block>
	            						<fo:block text-align="left" font-size="12pt" white-space-collapse="false" font-weight = "bold" wrap-option="wrap"><#if billingAddress?has_content>${billingAddress.get("city")?if_exists} - ${billingAddress.get("postalCode")?if_exists} </#if></fo:block>  
					            	</fo:table-cell>   
								 </fo:table-row>
								  <fo:table-row> 
									 <fo:table-cell> 
									      <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace" font-size="12pt" keep-together="always" font-weight = "bold">TIN NO: <#if toPartyDetail?has_content>${toPartyDetail.get('TIN_NUMBER')?if_exists}</#if></fo:block>
			   							  <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace" font-size="12pt" keep-together="always" font-weight = "bold">CST NO: <#if toPartyDetail?has_content>${toPartyDetail.get('CST_NUMBER')?if_exists}</#if></fo:block>
			   							  <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
									 </fo:table-cell>   
								 </fo:table-row>  
							  </fo:table-body>
						</fo:table>
					</fo:block>
			 	</fo:table-cell>
			 	 <fo:table-cell border-style="solid">   						
			 	    <fo:block>
						<fo:table table-layout="fixed" width="100%" space-before="0.2in">
							 <fo:table-body>
								  <fo:table-row> 
									 <fo:table-cell> 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always">Invoice No</fo:block>
									       <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" font-weight="bold" keep-together="always">${invoiceNo?if_exists}</fo:block>
			   							  <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always">Delivery Note No</fo:block>
									 		<fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always" font-weight = "bold">${shipment.get('shipmentId')?if_exists}</fo:block>
									 </fo:table-cell>   
								 </fo:table-row> 
								  <fo:table-row> 
									 <fo:table-cell border-style="solid" > 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always">Supplier's Ref</fo:block>
									      <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
									 </fo:table-cell>   
								 </fo:table-row> 
								 <fo:table-row> 
									 <fo:table-cell border-style="solid" > 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always">Buyer's Order No</fo:block>
									      <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
									 </fo:table-cell>   
								 </fo:table-row> 
								 <fo:table-row> 
									 <fo:table-cell border-style="solid" > 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always">Despatch Document No</fo:block>
									      <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
									 </fo:table-cell>   
								 </fo:table-row> 
								  <fo:table-row> 
									 <fo:table-cell border-style="solid" > 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always">Despatched Through</fo:block>
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" font-weight = "bold" keep-together="always">${shipment.get('modeOfDespatch')?if_exists}</fo:block>
									 </fo:table-cell>   
								 </fo:table-row> 
								  <fo:table-row> 
									 <fo:table-cell > 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always">Terms of Delivery</fo:block>
									      <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
									 </fo:table-cell>   
								 </fo:table-row> 
							  </fo:table-body>
						</fo:table>
					</fo:block>
				</fo:table-cell>
			 	 <fo:table-cell border-style="solid">   						
			 	    <fo:block>
						<fo:table table-layout="fixed" width="100%" space-before="0.2in">
							 <fo:table-body>
								  <fo:table-row> 
									 <fo:table-cell border-style="solid" > 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always">Dated</fo:block>
									       <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always" font-weight = "bold">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString((invoice.get('invoiceDate')), "dd-MMM-yyyy")}</fo:block>
			   							  <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always">Terms of Payment</fo:block>
									 </fo:table-cell>   
								 </fo:table-row> 
								  <fo:table-row> 
									 <fo:table-cell border-style="solid" > 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always">Other Reference</fo:block>
									      <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
									 </fo:table-cell>   
								 </fo:table-row> 
								 <fo:table-row> 
									 <fo:table-cell border-style="solid" > 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always">Dated</fo:block>
									       <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always" font-weight = "bold">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString((invoice.get('invoiceDate')), "dd-MMM-yyyy")}</fo:block>
									 </fo:table-cell>   
								 </fo:table-row> 
								 <fo:table-row> 
									 <fo:table-cell border-style="solid" > 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always">Dated</fo:block>
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always" font-weight="bold">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString((invoice.get('invoiceDate')), "dd-MMM-yyyy")}</fo:block>
									 </fo:table-cell>   
								 </fo:table-row>
 								<fo:table-row> 
									 <fo:table-cell border-style="solid" > 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always" >Destination</fo:block>
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" font-weight="bold" wrap-option="wrap"><#if billingAddress?has_content>${billingAddress.get("toName")?if_exists}</#if></fo:block> 
									 </fo:table-cell>   
								 </fo:table-row> 
								  <fo:table-row> 
									 <fo:table-cell > 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always">Internal No</fo:block>
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always" font-weight="bold">${invoice.get("invoiceId")?if_exists}</fo:block>
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always">Ref:-Sale Order No</fo:block>
									      <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									 </fo:table-cell>   
								 </fo:table-row> 
							  </fo:table-body>
						</fo:table>
					</fo:block>
				</fo:table-cell>
				 <fo:table-cell border-style="solid">   						
			 	    <fo:block>
						<fo:table table-layout="fixed" width="100%" space-before="0.2in">
							 <fo:table-body>
								 <fo:table-row> 
									 <fo:table-cell border-style="solid" > 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always">Vehicle Number</fo:block>
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always" font-weight="bold">${shipment.get('vehicleId')?if_exists}</fo:block>
									 </fo:table-cell>   
								 </fo:table-row> 
								 <fo:table-row> 
									 <fo:table-cell> 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always">&#160;</fo:block>
									      <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
									 </fo:table-cell>   
								 </fo:table-row> 
								 <fo:table-row> 
									 <fo:table-cell> 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always">&#160;</fo:block>
									      <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
									 </fo:table-cell>   
								 </fo:table-row>
								  <fo:table-row> 
									 <fo:table-cell> 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always">&#160;</fo:block>
									      <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
									 </fo:table-cell>   
								 </fo:table-row> 
								  <fo:table-row> 
									 <fo:table-cell> 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always">&#160;</fo:block>
									      <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
									 </fo:table-cell>   
								 </fo:table-row>  
							  </fo:table-body>
						</fo:table>
					</fo:block>
				</fo:table-cell>
		 	  </fo:table-row>	
	 	 </fo:table-body> 
 	</fo:table>
 	<fo:table  table-layout="fixed" width="100%">
		 <fo:table-column column-width="3.5%"/>
		 <fo:table-column column-width="34.5%"/>
	     <fo:table-column column-width="23%"/>
	     <fo:table-column column-width="10%"/>
	     <fo:table-column column-width="14%"/>
	     <fo:table-column column-width="15%"/>
		     <fo:table-body>
			     <fo:table-row> 
				     <fo:table-cell border-style="solid">   						
				 	     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" 
				 	     font-weight="bold" keep-together="always">SNo</fo:block>
				 	</fo:table-cell>
				     <fo:table-cell border-style="solid">   						
				 	     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" 
				 	     font-weight="bold" keep-together="always">Description of Goods</fo:block>
				 	</fo:table-cell>
				 	 <fo:table-cell border-style="solid">   						
				 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" 
				 	     font-weight="bold" keep-together="always">Quantity(UOM)</fo:block>
				 	</fo:table-cell>
				 	<fo:table-cell border-style="solid">   						
				 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" 
				 	     font-weight="bold" keep-together="always">DC No</fo:block>
				 	</fo:table-cell>
				 	  <fo:table-cell border-style="solid">   						
				 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" 
				 	     font-weight="bold" keep-together="always">Unit Rate</fo:block>
				 	</fo:table-cell>
				 	  <fo:table-cell border-style="solid">   						
				 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" 
				 	     font-weight="bold" keep-together="always">Amount (Rs)</fo:block>
				 	</fo:table-cell>
			 	  </fo:table-row>
			 	  <#assign totalAmt = 0>
				<#assign grandTotal = 0>
				<#if invoiceItems?has_content>
					<#assign slNo = 1>
					<#list invoiceItems as eachItem>
						<fo:table-row>
        					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
        						<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false">${slNo?if_exists}</fo:block>
        					</fo:table-cell>
        					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
        						<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false">${eachItem.get('itemDescription')?if_exists}</fo:block>
        					</fo:table-cell>
        					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
        						<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false">${eachItem.get('quantityLtr')?if_exists?string("#0.00")}(${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(eachItem.get('quantityUomId')?if_exists)),7)})</fo:block>
        					</fo:table-cell>
        					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
        						<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false">${shipment.get('shipmentId')?if_exists}</fo:block>
        					</fo:table-cell>
        					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
        						<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false">${eachItem.get('defaultPrice')?if_exists?string("#0.00")}</fo:block>
        					</fo:table-cell>
        					<#assign totalItemAmt = eachItem.get('defaultPrice')*eachItem.get('quantity')>
        					<#assign grandTotal = grandTotal+totalItemAmt>
        					<#assign totalAmt = totalAmt+totalItemAmt>
        					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
        						<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight = "bold">${totalItemAmt?if_exists?string("#0.00")}</fo:block>
        					</fo:table-cell>
        					<#assign slNo = slNo+1>
						</fo:table-row>
					</#list>
				</#if>
				<fo:table-row>
					<fo:table-cell border-bottom-style="dotted" border-bottom-width="thin" border-color="black" number-columns-spanned="6">
						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell border-bottom-style="dotted" border-bottom-width="thin" border-color="black">
						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					</fo:table-cell>
					<fo:table-cell border-bottom-style="dotted" border-bottom-width="thin" border-color="black">
						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					</fo:table-cell>
					<fo:table-cell border-bottom-style="dotted" border-bottom-width="thin" border-color="black">
						<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false" font-weight="bold">Total</fo:block>
					</fo:table-cell>
					<fo:table-cell border-bottom-style="dotted" border-bottom-width="thin" border-color="black">
						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					</fo:table-cell>
					<fo:table-cell border-bottom-style="dotted" border-bottom-width="thin" border-color="black">
						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					</fo:table-cell>
					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
						<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight = "bold">${totalAmt?if_exists?string("#0.00")}</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<#if invoiceTaxItems?has_content>
						<#list invoiceTaxItems as eachTaxItem>
							<#assign taxDetails = eachTaxItem.entrySet()>
							<#list taxDetails as eachTax>
							<fo:table-row>
            					<#assign text = "">
            					<#if eachTax.getKey()=="BED_SALE">
            						<#assign text = "Excise Duty (Deposit) Including CESS 2.06%">
            					<#elseif eachTax.getKey()=="CST_SALE">
            						<#assign text = "Central Sales Tax(CST) 2.0%">
            					<#elseif eachTax.getKey()=="SERTAX_SALE">
            						<#assign text = "Service Tax ">
            					<#elseif eachTax.getKey()=="VAT_SALE" && (eachTax.getValue()>0)>
            						<#assign text = "Value Added Tax(VAT) ">
            					<#elseif eachTax.getKey()=="VAT_SALE" && (eachTax.getValue()<0)>
            						<#assign text = "VAT Adjustment">
            					<#elseif eachTax.getKey()=="PPD_PROMO_ADJ">
            						<#assign text = "PPD Promotion Adjustment">
            					<#else>
            						<#assign text = "Other Adjustments">
            							 <#assign invoiceTaxItemType = delegator.findOne("InvoiceItemType", {"invoiceItemTypeId" : eachTax.getKey()}, false)>
            							 <#if invoiceTaxItemType?has_content>
            						    	<#assign text=invoiceTaxItemType.description?if_exists>
            							 </#if>
            					</#if>
            					<fo:table-cell border-bottom-style="dotted" border-bottom-width="thin" border-color="black">
									<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
								</fo:table-cell>
								<fo:table-cell border-bottom-style="dotted" border-bottom-width="thin" border-color="black">
									<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
								</fo:table-cell>
            					<fo:table-cell number-columns-spanned="3" border-bottom-style="dotted" border-bottom-width="thin" border-color="black">
            						<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false" font-weight="bold"> ${text?if_exists}</fo:block>
            					</fo:table-cell>
            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
            						<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight = "bold">${eachTax.getValue()?if_exists?string("#0.00")}</fo:block>
            					</fo:table-cell>
            					<#assign grandTotal = grandTotal+eachTax.getValue()>
							</fo:table-row>
						</#list>
					</#list>
				</#if>
				<fo:table-row>
					<fo:table-cell border-bottom-style="dotted" border-bottom-width="thin" border-color="black" number-columns-spanned="6">
						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell border-bottom-style="dotted" border-bottom-width="thin" border-color="black">
						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					</fo:table-cell>
					<fo:table-cell border-bottom-style="dotted" border-bottom-width="thin" border-color="black">
						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					</fo:table-cell>
					<fo:table-cell number-columns-spanned="3" border-bottom-style="dotted" border-bottom-width="thin" border-color="black">
						<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false" font-weight="bold">GRAND TOTAL</fo:block>
					</fo:table-cell>
					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
						<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">${grandTotal?if_exists?string("#0.00")}</fo:block>
					</fo:table-cell>
				</fo:table-row>
				
			 	 <fo:table-row> 
			     <fo:table-cell number-columns-spanned="5">   						
			 	     <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" font-size="12pt" keep-together="always" font-weight = "bold">Amount chargeable (in words):</fo:block>
					 <fo:block text-align="left" text-indent="5pt" keep-together="always" font-family="Courier,monospace" font-size="11pt" white-space-collapse="false" font-weight = "bold">Rupees ${Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble(grandTotal?string("#0.00")), "%indRupees-and-paise", locale)} ONLY.</fo:block>
					 <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			         <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always" font-weight = "bold">Declaration :</fo:block>
			         <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always">We declare that this invoice shows the actual price of the goods</fo:block>
			         <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always">described and that all particulars are true and correct.</fo:block>
			         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			        </fo:table-cell>
		 	  </fo:table-row>
		 	  <fo:table-row> 
		 	      <fo:table-cell number-columns-spanned="3">   						
			 	     <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				 </fo:table-cell>
			</fo:table-row>
			 <fo:table-row> 
		 	      <fo:table-cell number-columns-spanned="3">   						
			 	     <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				 </fo:table-cell>
			</fo:table-row>
			 
		 	   <fo:table-row> 
		 	      <fo:table-cell number-columns-spanned="3">   						
			 	     <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				 </fo:table-cell>
				 <fo:table-cell number-columns-spanned="1">   						
			 	     <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			      </fo:table-cell>
		 	      <fo:table-cell border-style="solid" number-columns-spanned="2">   						
			 	      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" font-weight="bold"
			 	       keep-together="always">For MOTHER DAIRY</fo:block>
			 	    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			 	    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			 	    <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			 	      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" font-weight="bold"
			 	       keep-together="always">AUTHORISED SIGNATORY</fo:block>
			      </fo:table-cell>
			 </fo:table-row>	
			  <fo:table-row> 
		 	         <fo:table-cell number-columns-spanned="3">   						
			 	       <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always" font-weight="bold">Details as per enclosure</fo:block>
			 	   </fo:table-cell>
			 </fo:table-row>	
			  <fo:table-row> 
			       <fo:table-cell number-columns-spanned="1">   						
						<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			      </fo:table-cell>
		 	         <fo:table-cell number-columns-spanned="1">   						
			 	         <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace" font-size="11pt" keep-together="always" font-weight="bold">SUBJECT TO BANGALORE JURISDICTION</fo:block>
			 	   </fo:table-cell>
			 </fo:table-row>	
			 </fo:table-body> 
 	</fo:table>
 	</fo:block>
 </fo:flow>
 </fo:page-sequence>
 </#list>
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