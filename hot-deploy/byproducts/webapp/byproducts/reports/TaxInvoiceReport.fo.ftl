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
${setRequestAttribute("OUTPUT_FILENAME", "TaxInvoice.txt")}
<#if facilityMap?has_content && invoiceListMap?has_content> 
<#assign itemsList=facilityMap.entrySet()>
<#assign tinNumber="">
<#assign cstNumber="">
 <#list itemsList as facilityList>
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace" font-size="12pt">	
	         <#assign  facilityId=facilityList.getKey()>   
	         <#assign invoiceList=invoiceListMap.get(facilityId)>	
	          <#if (tinNumber ="") && (cstNumber ="")>
	        	<#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" :invoiceList.getString("partyId")}, true)>
	        	<#assign tinNumber = (partyGroup.tinNumber)?if_exists>
	    		<#assign cstNumber = (partyGroup.cstNumber)?if_exists>
	         </#if>	
	<fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-weight="bold"   keep-together="always"> MOTHER DAIRY, GKVK POST, YELAHANKA, BANGALORE 560 065</fo:block>
 	<fo:block text-align="center" border-style="solid">
 	<fo:table  table-layout="fixed" width="100%" space-before="0.2in">
	     <fo:table-column column-width="70%"/>
	     <fo:table-column column-width="30%"/>
	     <fo:table-body>
		     <fo:table-row> 
			     <fo:table-cell>   						
			 	     <fo:block text-align="center" white-space-collapse="false" font-weight="bold" keep-together="always" text-indent="200pt">CONSOLIDATED TAX INVOICE</fo:block>
			 	</fo:table-cell>
		 	  </fo:table-row>	
		 	  <fo:table-row> 
			     <fo:table-cell border-style="solid">   						
					<fo:block>
						<fo:table  table-layout="fixed" width="100%" space-before="0.2in">
						     <fo:table-column column-width="50%"/>
						     <fo:table-column column-width="50%"/>
							 <fo:table-body>
								  <fo:table-row font-weight="bold"> 
									 <fo:table-cell>
									 <fo:block>
										<fo:table>
											 <fo:table-body>
											 <#if invoiceListMap?has_content>
												  <fo:table-row> 
													 <fo:table-cell> 
													      <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace" font-size="12pt" font-weight="bold" keep-together="always">BUYER NAME</fo:block>
													 </fo:table-cell>
													 <fo:table-cell> 
													      <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace" font-size="12pt" keep-together="always">${facilityId} ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, facilityId, false)} </fo:block>
													 </fo:table-cell>
												 </fo:table-row> 
												 <#assign partyAddressResult = dispatcher.runSync("getPartyPostalAddress", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", invoiceList.getString("partyId"), "userLogin", userLogin))/>
										 		 <#assign partyTelephoneResult = dispatcher.runSync("getPartyTelephone", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", invoiceList.getString("partyId"), "userLogin", userLogin))/>
												 <fo:table-row> 
												 	<fo:table-cell>
										   				<fo:block text-align="left" keep-together="always" font-weight="bold" >ADDRESS</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														 <#if (partyAddressResult.address1?has_content)>
											   				<fo:block text-align="left" wrap-option="wrap" >${partyAddressResult.address1?if_exists}</fo:block>
														</#if>
														<#if (partyAddressResult.address2?has_content)>
															<fo:block  text-align="left" wrap-option="wrap" keep-together="always">${partyAddressResult.address2?if_exists}</fo:block>
														</#if>
														<#if (partyAddressResult.city?has_content)>
															<fo:block  text-align="left" keep-together="always">${partyAddressResult.city?if_exists} ${partyAddressResult.stateProvinceGeoId?if_exists}</fo:block>
														</#if>
														<#if (partyAddressResult.countryGeoId?has_content)>
															<fo:block  text-align="left" keep-together="always">${partyAddressResult.countryGeoId?if_exists}</fo:block>
														</#if>
														<#if (partyTelephoneResult.contactNumber?has_content)>
														  	<fo:block text-align="left" keep-together="always">${partyTelephoneResult.contactNumber?if_exists}</fo:block>
														</#if>
														<#if (partyAddressResult.postalCode?has_content)>
														  	<fo:block  text-align="left" keep-together="always">${partyAddressResult.postalCode?if_exists}</fo:block>
														</#if>   
													</fo:table-cell>
												 </fo:table-row>
												  <fo:table-row> 
													 <fo:table-cell> 
													      <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  font-weight="bold" keep-together="always">BILLING PERIOD</fo:block>
													 </fo:table-cell>
													 <fo:table-cell> 
													      <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"   keep-together="always">${billingPeriodDate?if_exists}</fo:block>
													 </fo:table-cell>
												 </fo:table-row> 
												 </#if>
											</fo:table-body>
											</fo:table>
											</fo:block>
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
							 	 <#if invoiceListMap?has_content>
								  <fo:table-row> 
									 <fo:table-cell> 
									      <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  font-weight="bold" keep-together="always">Invoice No-${invoiceListMap.get(facilityId).get("invoiceId")}</fo:block>
			   							  <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  font-weight="bold" keep-together="always">Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invoiceList.getTimestamp("dueDate"), "dd/MMMM/yy")}</fo:block>
									 </fo:table-cell>   
								 </fo:table-row> 
								  <fo:table-row> 
									 <fo:table-cell> 
									      <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  font-weight="bold" keep-together="always">TIN-29710050983</fo:block>
									      <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  font-weight="bold" keep-together="always">CST-90750068</fo:block>
									      <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
									 </fo:table-cell>   
								 </fo:table-row> 
								 </#if>
							  </fo:table-body>
						</fo:table>
					</fo:block>
				</fo:table-cell>
		 	  </fo:table-row>
	 	 </fo:table-body> 
 	</fo:table>
 	<fo:table  table-layout="fixed">
		 <fo:table-column column-width="10%"/>
	     <fo:table-column column-width="30%"/>
	     <fo:table-column column-width="30%"/>
	     <fo:table-column column-width="30%"/>
	     <fo:table-column column-width="30%"/>
	     <fo:table-column column-width="40%"/>
		     <fo:table-body>
		     			<#assign grandTotal = 0>
				     	<fo:table-row font-weight="bold"> 
					     <fo:table-cell>   						
					 	     <fo:block text-align="left" white-space-collapse="false" font-weight="bold" keep-together="always" text-indent="250pt">DESCRIPTION</fo:block>
					 	</fo:table-cell>
					 	</fo:table-row>
	                  <fo:table-row font-weight="bold"> 
					     <fo:table-cell>   						
					 	     <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" 
					 	      font-weight="bold" keep-together="always">Taxable Goods </fo:block>
	                  	</fo:table-cell>
	                  </fo:table-row>
	                  <fo:table-row font-weight="bold"> 
					     <fo:table-cell border-style="solid">   						
					 	     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  
					 	     font-weight="bold" keep-together="always">S.NO</fo:block>
					 	</fo:table-cell>
					 	<fo:table-cell border-style="solid">   						
					 	     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  
					 	     font-weight="bold" keep-together="always">Product</fo:block>
					 	</fo:table-cell>
					 	<fo:table-cell border-style="solid">   						
					 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  
					 	     font-weight="bold" keep-together="always">QTY(Kg/Ltr)</fo:block>
					 	</fo:table-cell>
					 	<fo:table-cell border-style="solid">   						
					 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  
					 	     font-weight="bold" keep-together="always">Amount</fo:block>
					 	</fo:table-cell>
		 	  		 </fo:table-row>
		 	  		 <#assign taxTotal = 0>
		 	  		 <#assign taxableTotal = 0>
		 	  		 <#assign vatGrandTotal5 = 0>
		 	  		 <#assign vatGrandTotal14 = 0>
		 	  		
		 	  		 <#assign prodTotals = facilityList.getValue().get("prodMap")>
				 	  		 <#assign prod = prodTotals.entrySet()>
				 	  		 <#assign taxSubTotal = 0>
				 	  		 <#assign vatAmount5 = 0>
				 	  		 <#assign vatAmount14 = 0>
				 	  		 <#assign vatno = 0>
				 	  		 <#list prod as productTotals>
				 	  		 <#assign vat = vatMap.get(productTotals.getKey())>
				 	  		 <#if (vat == 5.5)>
								<#assign vatAmount5 = (vatAmount5+productTotals.getValue().get("amount")?if_exists)>
								<#assign vatno = vatno+1>
							</#if>
							<#if (vat == 14.5)>
								<#assign vatAmount14 = (vatAmount14+productTotals.getValue().get("amount")?if_exists)>
								<#assign vatno = vatno+1>
							</#if>
				 	  		 <#if (vat != 0)>
	                  <fo:table-row font-weight="bold"> 
	                  	<#assign productDetails = delegator.findOne("Product", {"productId" : productTotals.getKey()}, true)?if_exists/>
	                  	<fo:table-cell>   						
					 	     <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" 
					 	       keep-together="always">${vatno?if_exists}</fo:block>
	                  	</fo:table-cell>
	                  	<fo:table-cell>   						
					 	     <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" 
					 	      keep-together="always">${productDetails.description?if_exists}</fo:block>
	                  	</fo:table-cell>
	                  	<fo:table-cell>   						
					 	     <fo:block text-align="right" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" 
					 	       keep-together="always">${productTotals.getValue().get("quantity")?if_exists?string("#0.00")}</fo:block>
	                  	</fo:table-cell>
	                  	<#assign taxSubTotal = taxSubTotal+(productTotals.getValue().get("amount")?if_exists)>
	                  	<fo:table-cell>   						
					 	     <fo:block text-align="right" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" 
					 	      keep-together="always">${productTotals.getValue().get("amount")?if_exists?string("#0.00")}</fo:block>
	                  	</fo:table-cell>
	                  </fo:table-row>
	                   </#if>
	                    </#list>
	                    <#if taxSubTotal?has_content && (taxSubTotal!=0)>
	                    <fo:table-row>
		                   <fo:table-cell >   						
						 	     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always"></fo:block>
						 	</fo:table-cell>
						 	<fo:table-cell >   						
						 	     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always"></fo:block>
						 	</fo:table-cell>
						 	<fo:table-cell >   						
						 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always">Sub Total</fo:block>
						 	</fo:table-cell>
						 	<fo:table-cell >   						
						 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always">${taxSubTotal?string("#0.00")?if_exists}</fo:block>
						 	</fo:table-cell>
	                  </fo:table-row>
	                  </#if>
	                  <fo:table-row> 
					     <fo:table-cell>   						
					 	     <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" 
					 	      font-weight="bold" keep-together="always">LESS RETURNS</fo:block>
	                  	</fo:table-cell>
	                  </fo:table-row>
	                  <fo:table-row> 
					     <fo:table-cell border-style="solid">   						
					 	     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  
					 	     font-weight="bold" keep-together="always">S.NO</fo:block>
					 	</fo:table-cell>
					 	<fo:table-cell border-style="solid">   						
					 	     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  
					 	     font-weight="bold" keep-together="always">Product</fo:block>
					 	</fo:table-cell>
					 	<fo:table-cell border-style="solid">   						
					 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  
					 	     font-weight="bold" keep-together="always">QTY(Kg/Ltr)</fo:block>
					 	</fo:table-cell>
					 	<fo:table-cell border-style="solid">   						
					 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  
					 	     font-weight="bold" keep-together="always">Amount</fo:block>
					 	</fo:table-cell>
		 	  		 </fo:table-row>
	                    <#assign prodTotals = facilityList.getValue().get("returnMap")>
	                  	<#assign prod = prodTotals.entrySet()>
	                  		 <#assign vatRetSubTotal = 0>
				 	  		 <#assign retVatAmount5 = 0>
				 	  		 <#assign retVatAmount14 = 0>
	                  		 <#assign vatRetNo = 0>
	                  		 
	                  		  <#assign vatReturnPrice5 =0>
	                  		 
				 	  		 <#list prod as productTotals>
				 	  		 <#assign vat = vatMap.get(productTotals.getKey())>
				 	  		 <#if (vat == 5.5)>
				 	  		 	<#assign retVatAmount5 = (retVatAmount5+productTotals.getValue().get("returnPrice")?if_exists)>
				 	  		 	<#assign vatRetNo = vatRetNo+1>
				 	  		 </#if>
				 	  		 <#if (vat == 14.5)>
				 	  		 	<#assign retVatAmount14 = (retVatAmount14+productTotals.getValue().get("returnPrice")?if_exists)>
				 	  		 	<#assign vatRetNo = vatRetNo+1>
				 	  		 </#if>
				 	  		 <#if (vat != 0)>
				 	  		 <fo:table-row font-weight="bold"> 
	                  	<#assign productDetails = delegator.findOne("Product", {"productId" : productTotals.getKey()}, true)?if_exists/>
	                  	<fo:table-cell>   						
					 	     <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" 
					 	       keep-together="always">${vatRetNo?if_exists}</fo:block>
	                  	</fo:table-cell>
	                  	<fo:table-cell>   						
					 	     <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" 
					 	      keep-together="always">${productDetails.brandName?if_exists}</fo:block>
	                  	</fo:table-cell>
	                  	<fo:table-cell>   						
					 	     <fo:block text-align="right" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" 
					 	       keep-together="always">${productTotals.getValue().get("returnQtyLtrs")?if_exists?string("#0.00")}</fo:block>
	                  	</fo:table-cell>
	                  	<#assign vatRetSubTotal = vatRetSubTotal+(productTotals.getValue().get("returnPrice")?if_exists)>
	                  	<fo:table-cell>   						
					 	     <fo:block text-align="right" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" 
					 	      keep-together="always">${productTotals.getValue().get("returnPrice")?if_exists?string("#0.00")}</fo:block>
	                  	</fo:table-cell>
	                  </fo:table-row>
	                  </#if>
	                  </#list>
	                  <#if vatRetSubTotal?has_content && (vatRetSubTotal!=0)>
	                 <fo:table-row>
		                   <fo:table-cell >   						
						 	     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always"></fo:block>
						 	</fo:table-cell>
						 	<fo:table-cell >   						
						 	     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always"></fo:block>
						 	</fo:table-cell>
						 	<fo:table-cell >   						
						 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always">Sub Total</fo:block>
						 	</fo:table-cell>
						 	<fo:table-cell >   						
						 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always">${(vatRetSubTotal)?string("#0.00")?if_exists}</fo:block>
						 	</fo:table-cell>
	                  </fo:table-row>
	                  </#if>
	                  <fo:table-row>
		                   <fo:table-cell border-style="solid">  	   						
						 	     <fo:block></fo:block>
						 	</fo:table-cell>
						 	<fo:table-cell border-style="solid">  	   						
						 	     <fo:block></fo:block>
						 	</fo:table-cell>
						 	<fo:table-cell border-style="solid">  	   						
						 	     <fo:block></fo:block>
						 	</fo:table-cell>
						 	<fo:table-cell border-style="solid">  	   						
						 	     <fo:block></fo:block>
						 	</fo:table-cell>
			 	       </fo:table-row>
	                 <fo:table-row font-weight="bold">
		                   <fo:table-cell >   						
						 	     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always"></fo:block>
						 	</fo:table-cell>
						 	<fo:table-cell >   						
						 	     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always"></fo:block>
						 	</fo:table-cell>
						 	<fo:table-cell >   						
						 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always">Taxable Sales</fo:block>
						 	</fo:table-cell>
						 	<#assign taxTotal = (taxSubTotal-vatRetSubTotal)>
						 	<fo:table-cell >   						
						 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always">${taxTotal?string("#0.00")?if_exists}</fo:block>
						 	</fo:table-cell>
	                  </fo:table-row>
	                 <fo:table-row font-weight="bold">
		                   <fo:table-cell >   						
						 	     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always"></fo:block>
						 	</fo:table-cell>
						 	<fo:table-cell >   						
						 	     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always"></fo:block>
						 	</fo:table-cell>
						 	<fo:table-cell >   						
						 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always">VAT@5.5</fo:block>
						 	</fo:table-cell>
						 	<#assign vatTotal5 = (vatAmount5-retVatAmount5)>
						 	<#assign vatGrandTotal5 = vatGrandTotal5+((vatTotal5*5.5)/100)>
						 	<fo:table-cell >   						
						 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always">${vatGrandTotal5?string("#0.00")?if_exists}</fo:block>
						 	</fo:table-cell>
	                  </fo:table-row>
	                 <fo:table-row font-weight="bold">
		                   <fo:table-cell >   						
						 	     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always"></fo:block>
						 	</fo:table-cell>
						 	<fo:table-cell >   						
						 	     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always"></fo:block>
						 	</fo:table-cell>
						 	<fo:table-cell >   						
						 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always">VAT@14.5</fo:block>
						 	</fo:table-cell>
						 	<#assign vatTotal14 = (vatAmount14-retVatAmount14)>
						 	<#assign vatGrandTotal14= vatGrandTotal14+((vatTotal14*14.5)/100)>
						 	<fo:table-cell >   						
						 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always">${vatGrandTotal14?string("#0.00")?if_exists}</fo:block>
						 	</fo:table-cell>
	                  </fo:table-row>
							<#assign taxableTotal = (taxTotal+vatGrandTotal5+vatGrandTotal14)>
						 	<#assign grandTotal = grandTotal+taxableTotal>
	                 <#--<fo:table-row>
		                   <fo:table-cell >   						
						 	     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always"></fo:block>
						 	</fo:table-cell>
						 	<fo:table-cell >   						
						 	     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always"></fo:block>
						 	</fo:table-cell>
						 	<fo:table-cell >   						
						 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always">Total</fo:block>
						 	</fo:table-cell>
						 	
						 	<fo:table-cell >   						
						 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always">${taxableTotal?string("#0.00")?if_exists}</fo:block>
						 	</fo:table-cell>
	                  </fo:table-row>
					<fo:table-row> 
					     <fo:table-cell>   						
					 	     <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" 
					 	      font-weight="bold" keep-together="always">NON Taxable Goods </fo:block>
	                  	</fo:table-cell>
	                  </fo:table-row>
	                  <fo:table-row> 
					     <fo:table-cell border-style="solid">   						
					 	     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  
					 	     font-weight="bold" keep-together="always">S.NO</fo:block>
					 	</fo:table-cell>
					 	<fo:table-cell border-style="solid">   						
					 	     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  
					 	     font-weight="bold" keep-together="always">Product</fo:block>
					 	</fo:table-cell>
					 	<fo:table-cell border-style="solid">   						
					 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  
					 	     font-weight="bold" keep-together="always">QTY(Kg/Ltr)</fo:block>
					 	</fo:table-cell>
					 	<fo:table-cell border-style="solid">   						
					 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  
					 	     font-weight="bold" keep-together="always">Amount</fo:block>
					 	</fo:table-cell>
		 	  		 </fo:table-row>
		 	  		 <#assign total = 0>
		 	  		 <#assign prodTotals = facilityList.getValue().get("prodMap")>
				 	  		 <#assign prod = prodTotals.entrySet()>
				 	  		 <#assign sno = 1>
				 	  		 <#assign subTotal = 0>
				 	  		 <#list prod as productTotals>
				 	  		 <#assign vat = vatMap.get(productTotals.getKey())>
				 	  		 <#if (vat == 0)>
	                  <fo:table-row> 
	                  	<#assign productDetails = delegator.findOne("Product", {"productId" : productTotals.getKey()}, true)?if_exists/>
	                  	<fo:table-cell>   						
					 	     <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" 
					 	       keep-together="always">${sno}</fo:block>
	                  	</fo:table-cell>
	                  	<fo:table-cell>   						
					 	     <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" 
					 	      keep-together="always">${productDetails.brandName?if_exists}</fo:block>
	                  	</fo:table-cell>
	                  	<fo:table-cell>   						
					 	     <fo:block text-align="right" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" 
					 	       keep-together="always">${productTotals.getValue().get("quantity")?if_exists?string("#0.00")}</fo:block>
	                  	</fo:table-cell>
	                  	<#assign subTotal = subTotal+(productTotals.getValue().get("amount")?if_exists)>
	                  	<fo:table-cell>   						
					 	     <fo:block text-align="right" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" 
					 	      keep-together="always">${productTotals.getValue().get("amount")?if_exists?string("#0.00")}</fo:block>
	                  	</fo:table-cell>
	                  </fo:table-row>
	                   </#if>
	                   <#assign sno = sno+1>
	                    </#list>
	                    <#if subTotal?has_content && (subTotal!=0)>
	                    <fo:table-row>
		                   <fo:table-cell >   						
						 	     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always"></fo:block>
						 	</fo:table-cell>
						 	<fo:table-cell >   						
						 	     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always"></fo:block>
						 	</fo:table-cell>
						 	<fo:table-cell >   						
						 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always">Sub Total</fo:block>
						 	</fo:table-cell>
						 	<#assign total = total+subTotal>
						 	<fo:table-cell >   						
						 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always">${subTotal?string("#0.00")?if_exists}</fo:block>
						 	</fo:table-cell>
	                  </fo:table-row>
	                   </#if>
	                    <#assign prodTotals = facilityList.getValue().get("returnMap")>
	                  	<#assign prod = prodTotals.entrySet()>
	                  		<#assign subReturnPrice = 0>
	                  		<#assign retSno = 1>
				 	  		 <#list prod as productTotals>
				 	  		 <#assign vat = vatMap.get(productTotals.getKey())>
				 	  		 <#if (vat == 0)>
	                  <fo:table-row> 
					     <fo:table-cell>   						
					 	     <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" 
					 	      font-weight="bold" keep-together="always">LESS RETURNS</fo:block>
	                  	</fo:table-cell>
	                  </fo:table-row>
	                  <fo:table-row> 
					     <fo:table-cell border-style="solid">   						
					 	     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  
					 	     font-weight="bold" keep-together="always">S.NO</fo:block>
					 	</fo:table-cell>
					 	<fo:table-cell border-style="solid">   						
					 	     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  
					 	     font-weight="bold" keep-together="always">Product</fo:block>
					 	</fo:table-cell>
					 	<fo:table-cell border-style="solid">   						
					 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  
					 	     font-weight="bold" keep-together="always">QTY(Kg/Ltr)</fo:block>
					 	</fo:table-cell>
					 	<fo:table-cell border-style="solid">   						
					 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  
					 	     font-weight="bold" keep-together="always">Amount</fo:block>
					 	</fo:table-cell>
		 	  		 </fo:table-row>
				 	  		 <fo:table-row> 
	                  	<#assign productDetails = delegator.findOne("Product", {"productId" : productTotals.getKey()}, true)?if_exists/>
	                  	<fo:table-cell>   						
					 	     <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" 
					 	       keep-together="always">${retSno?if_exists}</fo:block>
	                  	</fo:table-cell>
	                  	<fo:table-cell>   						
					 	     <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" 
					 	      keep-together="always">${productDetails.brandName?if_exists}</fo:block>
	                  	</fo:table-cell>
	                  	<fo:table-cell>   						
					 	     <fo:block text-align="right" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" 
					 	       keep-together="always">${productTotals.getValue().get("returnQtyLtrs")?if_exists?string("#0.00")}</fo:block>
	                  	</fo:table-cell>
	                  	<#assign subReturnPrice = subReturnPrice+(productTotals.getValue().get("returnPrice")?if_exists)>
	                  	<fo:table-cell>   						
					 	     <fo:block text-align="right" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" 
					 	      keep-together="always">${productTotals.getValue().get("returnPrice")?if_exists?string("#0.00")}</fo:block>
	                  	</fo:table-cell>
	                  </fo:table-row>
	                  </#if>
	                  <#assign retSno = retSno+1>
	                  </#list>
	                  <#if subReturnPrice?has_content && (subReturnPrice!=0)>
	                  <fo:table-row>
		                   <fo:table-cell >   						
						 	     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always"></fo:block>
						 	</fo:table-cell>
						 	<fo:table-cell >   						
						 	     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always"></fo:block>
						 	</fo:table-cell>
						 	<fo:table-cell >   						
						 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always">Sub Total</fo:block>
						 	</fo:table-cell>
						 	<#assign total = total+subReturnPrice>
						 	<fo:table-cell >   						
						 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always">${subReturnPrice?string("#0.00")?if_exists}</fo:block>
						 	</fo:table-cell>
	                  </fo:table-row>
	                  </#if>
	                  <#if total?has_content && (total!=0)>
	                 <fo:table-row>
		                   <fo:table-cell >   						
						 	     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always"></fo:block>
						 	</fo:table-cell>
						 	<fo:table-cell >   						
						 	     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always"></fo:block>
						 	</fo:table-cell>
						 	<fo:table-cell >   						
						 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always">Total</fo:block>
						 	</fo:table-cell>
						 	<#assign grandTotal = (grandTotal+total)>
						 	<fo:table-cell >   						
						 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always">${total?if_exists?string("#0.00")}</fo:block>
						 	</fo:table-cell>
	                  </fo:table-row>
					</#if>-->
					
			 	 	<#if grandTotal?has_content && (grandTotal!=0)>
	                 <fo:table-row font-weight="bold">
		                   <fo:table-cell >   						
						 	     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always"></fo:block>
						 	</fo:table-cell>
						 	<fo:table-cell >   						
						 	     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always"></fo:block>
						 	</fo:table-cell>
						 	<fo:table-cell >   						
						 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always">Grand Total</fo:block>
						 	</fo:table-cell>
						 	<fo:table-cell >   						
						 	     <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  
						 	     font-weight="bold" keep-together="always">${grandTotal?string("#0.00")?if_exists}</fo:block>
						 	</fo:table-cell>
	                  </fo:table-row>
					</#if>
					<fo:table-row>
		                   <fo:table-cell border-style="solid">  	   						
						 	     <fo:block></fo:block>
						 	</fo:table-cell>
						 	<fo:table-cell border-style="solid">  	   						
						 	     <fo:block></fo:block>
						 	</fo:table-cell>
						 	<fo:table-cell border-style="solid">  	   						
						 	     <fo:block></fo:block>
						 	</fo:table-cell>
						 	<fo:table-cell border-style="solid">  	   						
						 	     <fo:block></fo:block>
						 	</fo:table-cell>
			 	       </fo:table-row>
			 	 <fo:table-row>
			       <fo:table-cell number-columns-spanned="1">   						
						<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			      </fo:table-cell>
			     </fo:table-row>
			     <fo:table-row font-weight="bold">
			     <fo:table-cell number-columns-spanned="5">   						
			 	     <#-- <fo:block text-align="left" text-indent="5pt" font-weight="bold" white-space-collapse="false" font-family="Courier,monospace"  keep-together="always">Amount Payable:</fo:block>-->
					 <#assign amount = Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(grandTotal, "%indRupees-and-paise", locale).toUpperCase()>
                   	 <fo:block  text-align="left" text-indent="5pt" white-space-collapse="false" keep-together="always">Amount Payable:(${StringUtil.wrapString(amount?default(""))}  ONLY)</fo:block>
					 <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			         <fo:block text-align="left" text-indent="5pt" font-weight="bold" white-space-collapse="false" font-family="Courier,monospace"  keep-together="always">Declaration</fo:block>
			         <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace"  keep-together="always">We declare that this invoice shows the actual price of the goods</fo:block>
			         <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace"  keep-together="always">described and that all particulars are true and correct</fo:block>
			         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			        </fo:table-cell>
		 	  </fo:table-row>	
		 	   <fo:table-row> 
		 	      <fo:table-cell number-columns-spanned="3">   						
			 	     <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				 </fo:table-cell>
		 	      <fo:table-cell border-style="solid">   						
			 	      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace"  font-weight="bold"
			 	       keep-together="always">For MOTHER DAIRY</fo:block>
			 	    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			 	    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
			 	      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace"  font-weight="bold"
			 	       keep-together="always">AUTHORISED SIGNATORY</fo:block>
			      </fo:table-cell>
			       <fo:table-cell number-columns-spanned="1">   						
			 	     <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			      </fo:table-cell>
			 </fo:table-row>	
			  <fo:table-row> 
		 	         <fo:table-cell number-columns-spanned="3">   						
			 	       <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace"  keep-together="always" font-weight="bold">Details as per enclosure</fo:block>
			 	   </fo:table-cell>
			 </fo:table-row>	
			  <fo:table-row> 
			       <fo:table-cell number-columns-spanned="1">   						
						<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			      </fo:table-cell>
		 	         <fo:table-cell number-columns-spanned="3">   						
			 	         <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  keep-together="always" font-weight="bold">SUBJECT TO BANGALORE JURISDICTION</fo:block>
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
            	${uiLabelMap.OrderNoOrderFound}.
       		 </fo:block>
    	</fo:flow>
	</fo:page-sequence>
</#if>
</fo:root>
</#escape>
