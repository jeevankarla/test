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
      <fo:simple-page-master master-name="main" page-height="11in" page-width="7in">
        margin-top="1in" margin-bottom=".5in" margin-left=".1in" margin-right=".1in">
          <fo:region-body margin-top="1in"/>
          <fo:region-before extent="1in"/>
          <fo:region-after extent="1in"/>
      </fo:simple-page-master>
    </fo:layout-master-set>
    <#assign tinNumber="">
    <#assign fssaiNumber="">
    <#if invoiceDetailList?has_content>
    	<#assign temp = 0/>
    <fo:page-sequence master-reference="main">
        <fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        
        </fo:static-content>
           <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">	
           <fo:block></fo:block>
           
      <#list invoiceDetailList as invoiceDetail>
	      	<#assign temp = temp +1>
	        <#assign invoice = invoiceDetail.invoice />
	        <#if (tinNumber ="") && (fssaiNumber ="")>
	        	<#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" : invoice.partyIdFrom}, true)>
	        	<#assign tinNumber = (partyGroup.tinNumber)?if_exists>
	    		<#assign fssaiNumber = (partyGroup.fssaiNumber)?if_exists>
	        </#if>	
	        <#assign dispalyParty = (invoiceDetail.dispalyParty)?if_exists />
	        <#assign billingAddress = (invoiceDetail.billingAddress)?if_exists />
	        <#assign sendingParty = (invoiceDetail.sendingParty)?if_exists />
	        <#if invoiceDetail.billingParty?has_content>
	          <#assign billingParty = invoiceDetail.billingParty />
	          <#assign partyName = delegator.findOne("PartyNameView", {"partyId" : billingParty.partyId}, true)>
	        </#if>              	
         	<fo:block font-family="Courier,monospace">          
           		<fo:table width="100%" table-layout="fixed">
                	<fo:table-column column-width="2.0in"/>
                	<fo:table-column column-width="1.in"/>
                	<fo:table-body>
                  		<fo:table-row>
                    		<fo:table-cell>
                    			<fo:table width="100%" table-layout="fixed">
                         			 <fo:table-column column-width="1in"/>
                         			 <fo:table-column column-width="1in"/>
                            			<fo:table-body>
			                            	<fo:table-row>
			                              		<fo:table-cell>
											        <fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;  VST_ASCII-015   KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD.</fo:block>
			                                        <fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;       UNIT : MOTHER DAIRY:G.K.V.K POST : YELAHANKA:BANGALORE : 560065</fo:block>
											        <fo:block font-family="Courier,monospace" >----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
											        <#if reportTitle?has_content>
											        <#assign reportVal = reportTitle.get(invoice.invoiceId)>
											        <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold">&#160; <#if reportVal?exists && reportVal == "TAX">&#160;       TAX INVOICE<#else>&#160;       BILL OF SALE</#if></fo:block>
											        </#if>                 						
			                              		</fo:table-cell>
			                            	</fo:table-row>
			                            	<fo:table-row>
			                              		<fo:table-cell>
			        					  			<fo:block font-size="4pt" text-align="left" keep-together="always" white-space-collapse="false">TIN NO: ${tinNumber}</fo:block>
			        					  			<fo:block font-size="4pt" text-align="left" keep-together="always" white-space-collapse="false">BILL NO.</fo:block>
			                              		</fo:table-cell>
			                              		<fo:table-cell>
							                        <fo:block font-size="4pt" text-align="left" keep-together="always" white-space-collapse="false">Supply Date:${invoiceDetail.dueDate?if_exists}</fo:block>
							          			</fo:table-cell>  
			                            	</fo:table-row>
			                            	<fo:table-row>
			  								    <fo:table-cell>
			  										<fo:block></fo:block>
			  									</fo:table-cell>
											</fo:table-row>
													<#assign facility = invoice.getRelatedOne("Facility")/>
							                     		<#if facility.get("facilityName",locale)?has_content>
							                        		<#assign facilityName=facility.get("facilityName",locale)/>
							                      		</#if>
							                 <fo:table-row>
							                    <fo:table-cell>
							                      	 <fo:block font-size="4pt"  white-space-collapse="false">To</fo:block>
							                         <fo:block font-size="4pt"  white-space-collapse="false" keep-together="always">Booth: ${invoice.facilityId} [ ${facilityName} ], Route:${(parameters.routeId)?if_exists}</fo:block>
							                      	 <fo:block font-size="4pt"  white-space-collapse="false">Buyer's TIN:</fo:block>
							                     </fo:table-cell>    
							                 </fo:table-row>
                            			</fo:table-body>
                        			</fo:table>
                   			                  </fo:table-cell>
							                 </fo:table-row>
											</fo:table-body>
									</fo:table>
			            			</fo:block>              
						            <#if billingParty?has_content>
						              <fo:block>
						                <fo:table width="100%" table-layout="fixed" space-after="0.3in">
						                  <fo:table-column column-width="2.5in"/>
						                  <fo:table-body>
						            </fo:table-body>
						            </fo:table>
						          </fo:block>
       						 </#if>
       					
			            <fo:block  font-size="6pt"  font-family="Courier,monospace">  
			              <fo:table width="100%" table-layout="fixed">
			                  <fo:table-column column-width="58pt"/>
			                  <fo:table-column column-width="40pt"/>
			                  <fo:table-column column-width="40pt"/>
			                  <fo:table-column column-width="46pt"/>
			                  <fo:table-column column-width="46pt"/> 
			                  <fo:table-column column-width="40pt"/> 
			                  <fo:table-column column-width="50pt"/> 
			                  <fo:table-column column-width="50pt"/> 
			                  <fo:table-body font-size="7pt"> 
			                  <fo:table-row>
			                      <fo:table-cell>
			                        <fo:block  font-family="Courier,monospace" text-align="left">================================================================================</fo:block>	
			                      </fo:table-cell>
			                    </fo:table-row>
			                  <fo:table-row>
			                        <fo:table-cell>
			                          <fo:block text-align="left" text-indent="4pt" keep-together="always"> Product&amp;</fo:block>
			                           <fo:block text-align="left" text-indent="4pt" keep-together="always"> Unit</fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="right" >Vat.</fo:block>
			                          <fo:block text-align="right" >%age.</fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="right" text-indent="4pt" keep-together="always">Qty.</fo:block>
			                           <fo:block text-align="right" text-indent="4pt" keep-together="always">Unit</fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                           <fo:block text-align="right" text-indent="4pt" keep-together="always">Rate/</fo:block>
			                           <fo:block text-align="right" text-indent="4pt" keep-together="always">Amt.</fo:block>
			                        </fo:table-cell>
			                       
			                        <fo:table-cell>
			                           <fo:block text-align="right" text-indent="4pt" keep-together="always">Net</fo:block>
			                           <fo:block text-align="right" text-indent="4pt" keep-together="always">Amt.</fo:block>
			                        </fo:table-cell>
			                         
			                        <fo:table-cell>
			                          <fo:block text-align="right" text-indent="4pt" keep-together="always">Vat</fo:block>
			                           <fo:block text-align="right" text-indent="4pt" keep-together="always">Amt.</fo:block>
			                        </fo:table-cell>
			                       
			                        <fo:table-cell>
			                           <fo:block text-align="right" text-indent="4pt" keep-together="always">Total</fo:block>
			                           <fo:block text-align="right" text-indent="4pt" keep-together="always">Amt.</fo:block>
			                        </fo:table-cell>
			                      </fo:table-row>  
			                      <fo:table-row>
			                      <fo:table-cell>
			                        <fo:block  font-family="Courier,monospace" text-align="left">================================================================================</fo:block>	
			                      </fo:table-cell>
			                    </fo:table-row>                      
			              <#if invoiceDetail.invoiceItems?has_content>
			                <#assign invoiceItems = invoiceDetail.invoiceItems?if_exists />
			              
			                    <#assign currentShipmentId = "">
			                    <#assign newShipmentId = "">
			                    <#assign vatTotal = (Static["java.math.BigDecimal"].ZERO)>
						           <#assign grandTotal = (Static["java.math.BigDecimal"].ZERO)>
						           <#assign netTotal = (Static["java.math.BigDecimal"].ZERO)>
			                    <#-- if the item has a description, then use its description.  Otherwise, use the description of the invoiceItemType -->
			                    <#list invoiceItems as invoiceItem>
			                    
			                      <#assign itemType = invoiceItem.getRelatedOne("InvoiceItemType")>
			                      <#if invoiceItem.invoiceItemTypeId != "VAT_SALE">
			                      <#assign taxRate = invoiceItem.getRelatedOne("TaxAuthorityRateProduct")?if_exists>
			                      <#assign itemBillings = invoiceItem.getRelated("OrderItemBilling")?if_exists>
			                      <#if itemBillings?has_content>
			                        <#assign itemBilling = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(itemBillings)>
			                        <#if itemBilling?has_content>
			                          <#assign itemIssuance = itemBilling.getRelatedOne("ItemIssuance")?if_exists>
			                          <#if itemIssuance?has_content>
			                            <#assign newShipmentId = itemIssuance.shipmentId>
			                          </#if>
			                        </#if>
			                      </#if>
			                      <#if invoiceItem.description?has_content>
			                        <#assign description=invoiceItem.description>
			                      <#elseif taxRate?has_content & taxRate.get("description",locale)?has_content>
			                        <#assign description=taxRate.get("description",locale)>
			                      <#elseif itemType.get("description",locale)?has_content>
			                        <#assign description=itemType.get("description",locale)>
			                      </#if>
			  
			                      <#if newShipmentId?exists & newShipmentId != currentShipmentId>
			                        <#-- the shipment id is printed at the beginning for each
			                           group of invoice items created for the same shipment
			                        -->
			                        <fo:table-row>
			                          <fo:table-cell number-columns-spanned="6">
			                            <fo:block></fo:block>
			                           </fo:table-cell>
			                        </fo:table-row>
			                        <fo:table-row>
			                          <fo:table-cell number-columns-spanned="6">
			                            <fo:block > ${uiLabelMap.ProductShipmentId}: ${newShipmentId} </fo:block>
			                          </fo:table-cell>
			                        </fo:table-row>
			                        <#assign currentShipmentId = newShipmentId>
			                      </#if>
			                      <fo:table-row>
			                        <fo:table-cell number-columns-spanned="6">
			                          <fo:block></fo:block>
			                        </fo:table-cell>
			                      </fo:table-row>
			                       <#assign product = invoiceItem.getRelatedOne("Product")?if_exists/>
			                 		<#if  product?exists>
			                    		<#assign productName=product.productName?if_exists >
			                    	<#else>
			                    		<#assign productName=invoiceItem.invoiceItemTypeId?if_exists >
			                   		</#if>
			                        <#assign vatAmount = (Static["java.math.BigDecimal"].ZERO)>
			                        <#assign vatPercent = (Static["java.math.BigDecimal"].ZERO)>
			                  
			                        <#if invoiceVatMap?has_content>
			                        <#assign vatList = invoiceVatMap.get(invoiceItem.invoiceId)>
			                         <#if vatList?has_content && invoiceItem.productId?exists >
			                        <#list vatList as vat>
			                        <#if vat.productId == invoiceItem.productId>
			                        <#assign vatAmount = vat.vatAmount?if_exists>
			                        <#assign vatPercent = vat.vatPercent?if_exists>
			                        </#if>
			                        </#list>
			                         </#if>
			                        </#if> 
			                      <fo:table-row>
			                        <fo:table-cell>
			                          <fo:block text-align="left" text-indent="4pt" keep-together="always"> <#if  product?has_content> ${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(product.description?if_exists)),8)} <#else> ${invoiceItem.invoiceItemTypeId?if_exists} </#if>></fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="right" >${vatPercent?if_exists}</fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="right"> <#if invoiceItem.quantity?exists>${invoiceItem.quantity?string.number}</#if> </fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="right"> <#if invoiceItem.quantity?exists>${invoiceItem.amount}</#if> </fo:block>
			                        </fo:table-cell>
			                       <#assign netAmount = (Static["org.ofbiz.accounting.invoice.InvoiceWorker"].getInvoiceItemTotal(invoiceItem))?if_exists > 
			                        <fo:table-cell>
			                          <fo:block keep-together="always" text-align="right"> ${netAmount?if_exists?string("#0.00")} </fo:block>
			                        </fo:table-cell>
			                         <#assign netTotal = (netTotal.add(netAmount))>
			                        <fo:table-cell>
			                          <fo:block text-align="right" >${vatAmount?string("#0.00")?if_exists}</fo:block>
			                        </fo:table-cell>
			                        <#assign vatTotal = (vatTotal.add(vatAmount))>
			                         <#assign totalAmount = (Static["java.math.BigDecimal"].ZERO)>
			                         
			                        <#assign totalAmount = (netAmount + vatAmount)?if_exists >
			                        <fo:table-cell>
			                          <fo:block text-align="right" >${totalAmount?string("#0.00")?if_exists} </fo:block>
			                        </fo:table-cell>
			                      </fo:table-row>
			                      </#if>
			                    </#list>
			                    <#-- the grand total -->
			                    <fo:table-row>
			                      <fo:table-cell>
			                        <fo:block  font-family="Courier,monospace" text-align="left">================================================================================</fo:block>	
			                      </fo:table-cell>
			                    </fo:table-row>
			                     <fo:table-row>
                                   <fo:table-cell>
                                   </fo:table-cell>
                                 </fo:table-row>
			                    <fo:table-row>
			                      <fo:table-cell>
			                        <fo:block >${uiLabelMap.AccountingTotalCapital}</fo:block>
			                      </fo:table-cell>
			                      <fo:table-cell text-align="right" number-columns-spanned="4">
			                        <fo:block >
			                         ${netTotal?string("#0.00")}
			                        </fo:block>
			                      </fo:table-cell>
			                      <fo:table-cell text-align="right">
			                        <fo:block >
			                         ${vatTotal?if_exists}
			                        </fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell text-align="right">
			                        <fo:block >
			                        <#assign grandTotal = (netTotal + vatTotal)>
			                         ${grandTotal?string("#0.00")}
			                        </fo:block>
			                        </fo:table-cell>
			                    </fo:table-row>
			                    
			                     <fo:table-row>
			                      <fo:table-cell>
			                        <fo:block  font-family="Courier,monospace" text-align="left">================================================================================</fo:block>	
			                      </fo:table-cell>
			                    </fo:table-row>	
			                    
			                    <fo:table-row>
			                      <fo:table-cell   number-columns-spanned="5">
			                       <#if grandTotal?has_content>
				                      <fo:block keep-together="always" text-align="left" font-size="7pt">&#160;${Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble(grandTotal?string("#0.00")), "%rupees-and-paise", locale).toUpperCase()}</fo:block>
			                      </#if>
			                      </fo:table-cell>
			                    </fo:table-row>
			                    <fo:table-row>
			                      <fo:table-cell text-align="right" number-columns-spanned="3">
			                      <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			                      <fo:block font-size="4pt" text-align="right" keep-together="always">AUTHORISED SIGNATORY</fo:block>
			                      </fo:table-cell>
			                    </fo:table-row>
			          	         <fo:table-row>
			                      <fo:table-cell>
			                        <fo:block  font-family="Courier,monospace" text-align="left">================================================================================</fo:block>	
			                      </fo:table-cell>
			                    </fo:table-row>	
			         	        </#if>
			                  </fo:table-body>
			                </fo:table>
			            </fo:block>    
			         	<fo:block linefeed-treatment="preserve">-------------------cut here-------------------</fo:block>
       				<#if temp ==2>
       					<fo:block page-break-after="always"></fo:block>
       					<#assign temp=0>
       				</#if>
      			</#list>
      		</fo:flow>
      	</fo:page-sequence>
      <#else>
		<fo:page-sequence master-reference="main">
	    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	       		 <fo:block font-size="14pt">
	            	${uiLabelMap.NoOrderFound}.
	       		 </fo:block>
	    	</fo:flow>
		</fo:page-sequence>
     </#if>   
  </fo:root>
</#escape>

 

