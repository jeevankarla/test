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


<#if errorMessage?exists>
<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

<#-- do not display columns associated with values specified in the request, ie constraint values -->

<fo:layout-master-set>
    <fo:simple-page-master master-name="main" page-height="12in" page-width="8in"
            margin-top="0.5in" margin-bottom="1in" margin-left="1in" margin-right=".2in">
        <fo:region-body margin-top=".8in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>
    </fo:simple-page-master>
</fo:layout-master-set> 
	<fo:page-sequence master-reference="main">
	    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	       		 <fo:block font-size="14pt">
	            	${errorMessage}.
	       		 </fo:block>
	    	</fo:flow>
	</fo:page-sequence>
</fo:root>
</#escape>
<#else>
<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
                     margin-left=".1in" margin-right=".1in" margin-top=".1in" margin-bottom=".3in">
                <fo:region-body margin-top=".7in"/>
                <fo:region-before extent=".4in"/>
                <fo:region-after extent=".4in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "debitNote.pdf")}
        <#if paymentId?has_content || invoice?has_content>   
        
        <#if paymentId?has_content>
        
	        <#assign paymentDetails = delegator.findOne("Payment", {"paymentId" :paymentId}, true)>
	        
	        <#assign paymentId = "">
	        <#assign partyIdFrom = "">
	        <#assign partyIdTo = "">
	        <#assign paymentDate = "">
	        <#assign amount = (Static["java.math.BigDecimal"].ZERO)>
	        <#assign comments = "">
	        
	        <#if paymentDetails?has_content>  
	    	<#assign paymentId = paymentDetails.paymentId?if_exists>
	     	<#assign partyIdFrom = paymentDetails.partyIdFrom?if_exists>
	     	<#assign partyIdTo = paymentDetails.partyIdTo?if_exists>
	     	<#assign paymentDate = paymentDetails.paymentDate?if_exists>
	     	<#assign amount = paymentDetails.amount?if_exists>
	     	<#assign comments = paymentDetails.comments?if_exists>
          	<#assign paymentMethodType = paymentDetails.paymentMethodTypeId?if_exists>
        </#if>
        </#if>   	
          	
	        
	        
          
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before">
					<fo:block  keep-together="always" text-align="right" font-size = "11pt" font-family="Courier,monospace" white-space-collapse="false">    UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
					<fo:block  keep-together="always" text-align="right" font-size = "11pt" font-family="Courier,monospace" white-space-collapse="false">&#160;      Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-size = "13pt" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-size = "13pt" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
					<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					<#assign invoiceTypeId = invoice.invoiceTypeId?if_exists>
					<#assign invPartyIdFrom = invoice.partyIdFrom?if_exists>
					<#assign invPartyId = invoice.partyId?if_exists>
					<fo:block  keep-together="always" text-align="center" font-size = "14pt" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold"><#if invPartyIdFrom == "Company">DEBIT ADVICE <#else>CREDIT ADVICE</#if></fo:block>
        	
        	</fo:static-content>
        
          
        	<fo:flow flow-name="xsl-region-body">
              <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
            
                
          	 
              <fo:block font-size="12pt" margin-left="20pt">   
           
                <fo:table >
                    <fo:table-column column-width="100pt"/>
                    <fo:table-column column-width="100pt"/>                
                    <fo:table-column column-width="100pt"/>
                    <fo:table-body>
                        
                    	<fo:table-row>
							<fo:table-cell>
			            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       			</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell>
			            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       			</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell>
			            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       			</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell>
			            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       			</fo:table-cell>
						</fo:table-row>     
						 <#if paymentId?has_content>
	                        <fo:table-row>
	                        	<fo:table-cell>	
	                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size = "12pt">Advice Payment ID     </fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell>	
	                            	<fo:block text-align="left" keep-together="always" font-size = "12pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;:  &#160;&#160;&#160;&#160;${paymentId?if_exists}</fo:block>                               
	                            </fo:table-cell>
	                        </fo:table-row>
	                        <fo:table-row> 
	                            <fo:table-cell>	
	                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size = "12pt">Date</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell>	
	                            	<fo:block text-align="left" keep-together="always" font-size = "12pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;:   &#160;&#160;&#160;&#160;${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(paymentDate?if_exists, "dd/MM/yyyy")}</fo:block>                               
	                            </fo:table-cell>
	                        </fo:table-row>
	                        <fo:table-row>
							<fo:table-cell>
			            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       			</fo:table-cell>
							</fo:table-row>
							
							<#else>
							<fo:table-row>
	                        	<fo:table-cell>	
	                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size = "12pt">Invoice ID     </fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell>	
	                            	<fo:block text-align="left" keep-together="always" font-size = "12pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;:  &#160;&#160;&#160;&#160;${invoice.invoiceId?if_exists}</fo:block>                               
	                            </fo:table-cell>
	                        </fo:table-row>
	                        <fo:table-row> 
	                            <fo:table-cell>	
	                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size = "12pt">Date</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell>	
	                            	<fo:block text-align="left" keep-together="always" font-size = "12pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;:   &#160;&#160;&#160;&#160;${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invoice.invoiceDate?if_exists, "dd/MM/yyyy")}</fo:block>                               
	                            </fo:table-cell>
	                        </fo:table-row>
	                        <fo:table-row>
							<fo:table-cell>
			            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       			</fo:table-cell>
							</fo:table-row>
							</#if>
							<#if partyIdFrom?exists && partyIdFrom == "Company">
	                             <#assign partyId = partyIdTo>
	                        <#else>
	                             <#assign partyId = partyIdFrom>
	                        </#if>
	                        <#if invPartyIdFrom?exists && invPartyIdFrom == "Company">
	                             <#assign partyId = invpartyIdTo>
	                        <#else>
	                             <#assign partyId = invPartyIdFrom>
	                        </#if>
	                        
							<#assign partyAddressResult = dispatcher.runSync("getPartyPostalAddress", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", partyId?if_exists, "userLogin", userLogin))/> 
	                        <fo:table-row> 
	                            <fo:table-cell>	
	                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size = "12pt">To</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell>	
	                            	<fo:block text-align="left" keep-together="always" font-size = "12pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;:   &#160;&#160;&#160;&#160;${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId?if_exists, false)}<#if paymentId?has_content>(${partyId?if_exists})<#else></#if></fo:block>                               
	                            </fo:table-cell>
	                        </fo:table-row>
	                        <fo:table-row> 
	                            <fo:table-cell>	
	                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size = "12pt"></fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell>
									 <#if (partyAddressResult.address1?has_content)>
						   				<fo:block text-align="left" wrap-option="wrap" keep-together="always">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${partyAddressResult.address1?if_exists}</fo:block>
									</#if>
									<#if (partyAddressResult.address2?has_content)>
										<fo:block  text-align="left" wrap-option="wrap" keep-together="always">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${partyAddressResult.address2?if_exists}</fo:block>
									</#if>
									<#if (partyAddressResult.city?has_content)>
										<fo:block  text-align="left" wrap-option="wrap" keep-together="always">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${partyAddressResult.city?if_exists} ${partyAddressResult.stateProvinceGeoId?if_exists}</fo:block>
									</#if>
									<#if (partyAddressResult.countryGeoId?has_content)>
										<fo:block  text-align="left" wrap-option="wrap" keep-together="always">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${partyAddressResult.countryGeoId?if_exists}</fo:block>
									</#if>
									<#if (partyAddressResult.postalCode?has_content)>
									  	<fo:block  text-align="left" wrap-option="wrap" keep-together="always">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${partyAddressResult.postalCode?if_exists}</fo:block>
									</#if>   
								</fo:table-cell>
	                         </fo:table-row>
	                        <fo:table-row>
								<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
							</fo:table-row>
								 <#if paymentId?has_content>
							<fo:table-row>
								<fo:table-cell>
				            		<fo:block text-align="left" keep-together="always" font-size = "12pt">This is to inform you that your account has been <#if invPartyIdFrom == "Company">DEBITED<#else>CREDITED</#if> with Rs: ${amount?if_exists?string("##0.00")}       on the basis of the </fo:block>     
				       			</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
				            		<fo:block text-align="left" keep-together="always" font-size = "12pt">following transactions.</fo:block>     
				       			</fo:table-cell>
							</fo:table-row>
							
							<fo:table-row>
								<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
				            		<fo:block text-align="left" keep-together="always" font-size = "12pt" font-weight = "bold">Towards:                  ${comments?if_exists}</fo:block>     
				       			</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
							</fo:table-row>
							<#else>
								<fo:table-row>
								<fo:table-cell>
				            		<fo:block text-align="left" keep-together="always" font-size = "12pt">This is to inform you that your account has been <#if invPartyIdFrom == "Company">DEBITED<#else>CREDITED</#if> with Rs: ${invoiceTotal?if_exists?string("##0.00")}       on the basis of the </fo:block>     
				       			</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
				            		<fo:block text-align="left" keep-together="always" font-size = "12pt">following transactions.</fo:block>     
				       			</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
				            		<fo:block text-align="left" keep-together="always" font-size = "12pt" font-weight = "bold">Towards:                  ${comments?if_exists}</fo:block>     
				       			</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
							</fo:table-row>
						</#if>
	               	</fo:table-body>
                </fo:table>
    	 	</fo:block>
    	 	<fo:block margin-left="20pt">
                 	<fo:table border-style="solid" table-layout="fixed">
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="200pt"/>
                    <fo:table-column column-width="80pt"/>
                    <fo:table-column column-width="70pt"/>
                    <fo:table-column column-width="70pt"/>
                    <#if productMrpPriceMap?has_content>
                    <fo:table-column column-width="70pt"/>
                    </#if>
                    <fo:table-column column-width="95pt"/>
                    <fo:table-body>
                    
                    
                    	<fo:table-row >
                   			<fo:table-cell border-style="solid">
                        		<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">S.No</fo:block> 
                   			</fo:table-cell>
                   			<fo:table-cell border-style="solid">
                        		<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">Account Desc</fo:block> 
                   			</fo:table-cell>
                   			<fo:table-cell border-style="solid">
                        		<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">Qty(Ltrs/Kgs)</fo:block> 
                   			</fo:table-cell>
                   			<fo:table-cell border-style="solid">
                        		<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">Qty</fo:block> 
                   			</fo:table-cell>
                   			<fo:table-cell border-style="solid">
                        		<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">Rate</fo:block> 
                   			</fo:table-cell>
                   			<#if productMrpPriceMap?has_content>
	                   			<fo:table-cell border-style="solid">
	                        		<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">MRP</fo:block>  
	                   			</fo:table-cell>
	                   		</#if>
                   			<fo:table-cell border-style="solid">
                        		<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">Amount</fo:block> 
                   			</fo:table-cell>
            			</fo:table-row>
                    </fo:table-body>
                </fo:table>
             </fo:block>
             <fo:block margin-left="20pt">
                 	<fo:table border-style="solid">
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="200pt"/>
                    <fo:table-column column-width="80pt"/>
                    <fo:table-column column-width="70pt"/>
                    <fo:table-column column-width="70pt"/>
                    <#if productMrpPriceMap?has_content>
                    <fo:table-column column-width="70pt"/>
                    </#if>
                    <fo:table-column column-width="95pt"/>
                    <fo:table-body>
                    	<#assign sno=0>
					<#if invoiceItems?has_content>
						<#list invoiceItems as invoiceItem>
						 <#assign sno=sno+1>
				         <#assign itemType = invoiceItem.getRelatedOne("InvoiceItemType")>
				         <#assign isItemAdjustment = Static["org.ofbiz.entity.util.EntityTypeUtil"].hasParentType(delegator, "InvoiceItemType", "invoiceItemTypeId", itemType.getString("invoiceItemTypeId"), "parentTypeId", "INVOICE_ADJ")/>
						 <#if invoiceItem.productId?has_content>
				                <#assign productId=invoiceItem.productId>
				                <#assign productDetails = delegator.findOne("Product", {"productId" :productId}, true)>
				                <#assign productMrp = delegator.findOne("Product", {"productId" :productId}, true)>
				                <#if productDetails?has_content>
				                  <#assign description=productDetails.get("description")>
				                </#if>
				               <#elseif taxRate?has_content & taxRate.get("description",locale)?has_content>
				                <#assign description=taxRate.get("description",locale)>
				              <#elseif itemType.get("description",locale)?has_content>
				                <#assign description=itemType.get("description",locale)>
				          </#if>
				             <#assign prodUnitPriceInc = "">
				           <#if invoiceItem.productId?has_content>
					            <#if invoiceItem.unitPrice?has_content>
					            	<#assign prodUnitPrice = invoiceItem.unitPrice?if_exists>
					            	<#if productDetails.quantityIncluded?has_content && productDetails.quantityIncluded !=0>
					            		<#assign prodUnitPriceInc = (prodUnitPrice/productDetails.quantityIncluded)>
					            	</#if>
					            </#if>
					            <#if invoiceItem.amount?has_content>
					            	<#assign prodUnitPrice = invoiceItem.amount?if_exists>
					            	<#if productDetails.quantityIncluded?has_content && productDetails.quantityIncluded !=0>
					            		<#assign prodUnitPriceInc = (prodUnitPrice/productDetails.quantityIncluded)>
					            	</#if>
					            </#if>
					            <#assign invoiceQtyInc = "">
					            <#assign invoiceQty = "">
					            <#if invoiceItem.quantity?has_content>
					            	<#assign invoiceQty = invoiceItem.quantity?if_exists>
					            	<#if invoiceQty?has_content && invoiceQty !=0>
					            		<#assign invoiceQtyInc = invoiceQty*(productDetails.quantityIncluded?if_exists)>
					            	</#if>
					            </#if>
				            </#if>
							 <fo:table-row>
	                   			<fo:table-cell border-style="solid">
						        	<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always">&#160;${sno}</fo:block>
						     	</fo:table-cell>
						     	<#if description?has_content>
	                   			<fo:table-cell border-style="solid">
	                        		<fo:block text-align="left" font-size="12pt" white-space-collapse="false">${description?if_exists}</fo:block> 
	                   			</fo:table-cell>
	                   			<#else>
	                   			<fo:table-cell border-style="solid">
	                        		<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false"></fo:block> 
	                   			</fo:table-cell>
	                   			</#if>
	                   			<#if invoiceItem.productId?has_content>
	                   			<fo:table-cell border-style="solid">
	                        		<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${invoiceQtyInc?if_exists?string("#0.00")}</fo:block> 
	                   			</fo:table-cell>
	                   			<#else>
	                   			<fo:table-cell border-style="solid">
	                        		<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false"></fo:block> 
	                   			</fo:table-cell>
	                   			</#if>
			        			<#if invoiceItem.productId?has_content>
	                   			<fo:table-cell border-style="solid">
	                        		<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${invoiceQty?if_exists?string("#0.00")}</fo:block> 
	                   			</fo:table-cell>
	                   			<#else>
	                   			<fo:table-cell border-style="solid">
	                        		<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false"></fo:block> 
	                   			</fo:table-cell>
	                   			</#if>
	                   			<#if invoiceItem.productId?has_content>
	                   			<fo:table-cell border-style="solid">
	                        		<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${prodUnitPriceInc?if_exists?string("#0.00")}</fo:block> 
	                   			</fo:table-cell>
	                   			<#else>
	                   			<fo:table-cell border-style="solid">
	                        		<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false"></fo:block> 
	                   			</fo:table-cell>
	                   			</#if>
	                   			<#if productMrpPriceMap?has_content>
	                   			<#if invoiceItem.productId?has_content>
	                   			<#if productMrpPriceMap.get(invoiceItem.productId)?has_content>
	                   			<fo:table-cell border-style="solid">
	                        		<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${productMrpPriceMap.get(invoiceItem.productId)?if_exists?string("#0.00")}</fo:block> 
	                   			</fo:table-cell>
	                   			</#if>
	                   			<#else>
	                   			<fo:table-cell border-style="solid">
	                        		<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false"></fo:block> 
	                   			</fo:table-cell>
	                   			</#if>
	                   			</#if>
	                  	
	                   			<fo:table-cell border-style="solid">
	                        		<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false"><@ofbizCurrency amount=(Static["org.ofbiz.accounting.invoice.InvoiceWorker"].getInvoiceItemTotal(invoiceItem)) isoCode=invoice.currencyUomId?if_exists/></fo:block> 
	                   			</fo:table-cell>
	                   		
            				</fo:table-row>
							</#list>
							<fo:table-row>	
	                   			<#if productMrpPriceMap?has_content>
	                   			<fo:table-cell  number-columns-spanned="7">
	                        		<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">Total:&#160;<@ofbizCurrency amount=invoiceTotal isoCode=invoice.currencyUomId?if_exists/></fo:block> 
	                   			</fo:table-cell>
	                   			<#else>
								<fo:table-cell number-columns-spanned="6">
			        				<fo:block text-align="right" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">Total:&#160;<@ofbizCurrency amount=invoiceTotal isoCode=invoice.currencyUomId?if_exists/></fo:block>
			        			</fo:table-cell>
			        			</#if>
			        		</fo:table-row>	
                    	<#else>
                    	<fo:table-row >
                   			<fo:table-cell border-style="solid">
						        	<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always">&#160;</fo:block>
						     	</fo:table-cell>
                   			<fo:table-cell border-style="solid">
                        		<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">${comments?if_exists}</fo:block> 
                   			</fo:table-cell>
                   			  <#if amount?has_content>
                   			<fo:table-cell border-style="solid">
                        		<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${amount?string("##0.00")}</fo:block> 
                   			</fo:table-cell>
                   			</#if>
            			</fo:table-row>
            			</#if><#--  Invoice Items -->
					</fo:table-body>
                </fo:table>
             </fo:block>  
					<fo:block margin-left="20pt">
                 	<fo:table>
                    <fo:table-column column-width="200pt"/>
                    <fo:table-column column-width="170pt"/>
                    <fo:table-column column-width="195pt"/>  
                    <fo:table-body>
                    	<fo:table-row>
							<fo:table-cell>
			            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
			       			</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell>
			            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
			       			</fo:table-cell>
						</fo:table-row>					
                    	<fo:table-row >
                    			<#if amount?has_content>
                    				<#assign amountWords = Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(amount, "%indRupees-and-paise", locale)>
                    			<#else>
                    				<#assign amountWords = Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(invoiceTotal, "%indRupees-and-paise", locale)>
                    			</#if>
                          		
			                   <fo:table-cell>
			                        	<fo:block keep-together="always" font-size="12pt" font-weight = "bold">Amount Payable:(${StringUtil.wrapString(amountWords?default(""))}  ONLY)</fo:block>
			                   </fo:table-cell>
			             </fo:table-row>
			             <fo:table-row>
							<fo:table-cell>
			            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
			       			</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell>
			            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
			       			</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell>
			            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
			       			</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell>
			            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
			       			</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell>
			            		<fo:block  keep-together="always" font-weight="bold">Prepared By</fo:block>  
			       			</fo:table-cell>
			       			<fo:table-cell>
			            		<fo:block  keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Pre-Auditor</fo:block>  
			       			</fo:table-cell>
			       			<fo:table-cell>
			            		<fo:block  keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;For MotherDairy</fo:block>  
			       			</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell>
			            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
			       			</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell>
			            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
			       			</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
						<fo:table-cell>
			            		<fo:block  keep-together="always" font-weight="bold"></fo:block>  
			       			</fo:table-cell>
			       			<fo:table-cell>
			            		<fo:block  keep-together="always" font-weight="bold"></fo:block>  
			       			</fo:table-cell>
							<fo:table-cell>
			            		<fo:block  keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;D.Mgr(Finance)/Mgr(Finance)</fo:block>  
			       			</fo:table-cell>
						</fo:table-row>
                    </fo:table-body>
                </fo:table>
             </fo:block>  
           </fo:flow>
        </fo:page-sequence>
     <#--   </#if> payment Details -->
        <#else>	
			<fo:page-sequence master-reference="main">
		    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
		       		 <fo:block font-size="14pt">
		            	${uiLabelMap.OrderNoOrderFound}.
		       		 </fo:block>
		    	</fo:flow>
			</fo:page-sequence>
		</#if>  <#-- PaymentId Exist -->
		</fo:root>
</#escape>
</#if>