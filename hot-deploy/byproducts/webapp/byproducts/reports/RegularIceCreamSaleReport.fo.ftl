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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-left=".3in" margin-right=".3in" margin-top=".5in">
                <fo:region-body margin-top="1.6in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "IceCreamSalesReport.pdf")}
        <#if errorMessage?has_content>
	<fo:page-sequence master-reference="main">
	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	   <fo:block font-size="14pt">
	           ${errorMessage}.
        </fo:block>
	</fo:flow>
	</fo:page-sequence>	
	<#else>
       <#if dayWiseInvoice?has_content>
	        <fo:page-sequence master-reference="main" font-size="12pt">	
	        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
	        		<fo:block text-align="center" font-weight="bold" keep-together="always"  font-family="Courier,monospace" white-space-collapse="false">${uiLabelMap.KMFDairyHeader}</fo:block>
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
                	<fo:block text-align="center" font-weight="bold"  keep-together="always"  white-space-collapse="false"> <#if categoryType=="ICE_CREAM_NANDINI">NANDINI</#if><#if categoryType=="ICE_CREAM_AMUL">AMUL</#if>SALES BOOK FOR THE PERIOD- ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")} </fo:block>
          			<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
          			<fo:block>--------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            	    <fo:block text-align="left" font-weight="bold" font-size="12pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">Invoice Invoice 					Retailer        	Description        Quantity        Ex-factory    ED          VAT(Rs)    		C.S.T(Rs)      Total(Rs)</fo:block>
        			<fo:block text-align="left" font-weight="bold" font-size="12pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">Number  Seq.Number 		Name                               <#if categoryType=="UNITS">(Ltrs/Kgs)<#else> (In Ltrs)</#if>       Value(Rs)     Value(Rs)                               Value</fo:block>
	        		<fo:block>--------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            	</fo:static-content>	        	
	        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
        			<fo:block>
        				<fo:table>
		                    <fo:table-column column-width="60pt"/>
		                    <fo:table-column column-width="90pt"/>
		                    <fo:table-column column-width="120pt"/> 
		               	    <fo:table-column column-width="85pt"/>
		            		<fo:table-column column-width="80pt"/> 		
		            		<fo:table-column column-width="95pt"/>
		            		<fo:table-column column-width="90pt"/>
		            		<fo:table-column column-width="95pt"/>
		            		<fo:table-column column-width="95pt"/>
		            		<fo:table-column column-width="95pt"/>
		            		<fo:table-column column-width="95pt"/>
		                    <fo:table-body>
	                            <#assign totalQty=0> 
			                    <#assign totalBasicRev=0>
			                    <#assign totalBedRev=0>
			                    <#assign totalVatRev=0>
			                    <#assign totalCstRev=0>
			                    <#assign grandTotal=0>
		                        <#assign dayWiseTotalsMap = dayWiseInvoice.entrySet()>
       							<#list dayWiseTotalsMap as dayWiseTotalsDetails>
       							<#assign invoiceMap = dayWiseTotalsDetails.getValue().entrySet()>
       							<fo:table-row>
					                    <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayWiseTotalsDetails.getKey(), "dd-MMM-yyyy")}</fo:block>  
							            </fo:table-cell>
					           </fo:table-row>
					           <fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>--------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
          						  </fo:table-row> 
				                <#assign qty=0>
                                <#assign exVal=0>
                                <#assign edVal=0>
                                <#assign vat=0>
                                <#assign cst=0>
                                <#assign total=0>
                                <#assign sequenceId = null>
          						 <#list invoiceMap as invoiceDetails>
          						 <#assign invoice = invoiceDetails.getValue()>
          						 <#list invoice as invoiceDtls>
          						 <#assign invoice = delegator.findOne("Invoice", {"invoiceId" : invoiceDetails.getKey()}, true)>
          						 <#assign invoiceSeqDetails = delegator.findByAnd("BillOfSaleInvoiceSequence", {"invoiceId" : invoiceDetails.getKey()})/>
          						 <#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, invoice.get("partyId")?if_exists, false)>
							      <fo:table-row>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">${invoiceDetails.getKey()}</fo:block>  
							            </fo:table-cell>
							            <#assign sequenceId="">
							            <#if invoiceSeqDetails?has_content>
          						  			<#assign sequenceId = invoiceSeqDetails[0].get("sequenceId")?if_exists>
          						  	    </#if>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">${sequenceId}</fo:block>  
							            </fo:table-cell>
							           
							             <fo:table-cell>
							            	<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">${partyName}<#if categoryType=="ICE_CREAM_AMUL">[${invoice.get("partyId")?if_exists}]</#if></fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">
							            		<fo:table>
								            		<fo:table-column column-width="80pt"/> 		
								            		<fo:table-column column-width="110pt"/> 	
								            		<fo:table-column column-width="140pt"/> 
								            		<fo:table-column column-width="100pt"/> 
								            		<fo:table-column column-width="75pt"/> 
								            		<fo:table-column column-width="90pt"/> 
								            		<fo:table-column column-width="115pt"/> 
					                                <fo:table-body>
					                                <#assign productTotals=invoiceDtls.get("productTotals").entrySet()>
					                                <#list productTotals as productDtls>
					                               
					                                <#assign qty=qty+productDtls.getValue().get("quantity")>
					                                <#assign exVal=exVal+productDtls.getValue().get("basicRevenue")>
					                                <#assign edVal=edVal+productDtls.getValue().get("bedRevenue")>
					                                <#assign vat=vat+productDtls.getValue().get("vatRevenue")>
					                                <#assign cst=cst+productDtls.getValue().get("cstRevenue")>
					                                <#assign total=total+productDtls.getValue().get("totalRevenue")>
					                                
					                                <#assign totalQty=totalQty+productDtls.getValue().get("quantity")>
					       							<#assign totalBasicRev=totalBasicRev+productDtls.getValue().get("basicRevenue")>
					       							<#assign totalBedRev=totalBedRev+productDtls.getValue().get("bedRevenue")>
					       							<#assign totalVatRev=totalVatRev+productDtls.getValue().get("vatRevenue")>
					       							<#assign totalCstRev=totalCstRev+productDtls.getValue().get("cstRevenue")>
					       							<#assign grandTotal=grandTotal+productDtls.getValue().get("totalRevenue")>
						                                <fo:table-row>
						                                   <fo:table-cell>
								                           		<fo:block  keep-together="always" font-size="12pt" text-align="left" white-space-collapse="false">${productDtls.getKey()}</fo:block>  
								                       		</fo:table-cell>
															<fo:table-cell>
								                           		<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${productDtls.getValue().get("quantity")?string("#0.00")}</fo:block>  
								                       		</fo:table-cell>
								                       		<fo:table-cell>
								                           		<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${productDtls.getValue().get("basicRevenue")?string("#0.00")}</fo:block>  
								                       		</fo:table-cell>
								                       		<fo:table-cell>
								                           		<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${productDtls.getValue().get("bedRevenue")?string("#0.00")}</fo:block>  
								                       		</fo:table-cell>
								                       		<fo:table-cell>
								                           		<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${productDtls.getValue().get("vatRevenue")?string("#0.00")}</fo:block>  
								                       		</fo:table-cell>
								                       		<fo:table-cell>
								                           		<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${productDtls.getValue().get("cstRevenue")?string("#0.00")}</fo:block>  
								                       		</fo:table-cell>
								                       		<fo:table-cell>
								                           		<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${productDtls.getValue().get("totalRevenue")?string("#0.00")}</fo:block>  
								                       		</fo:table-cell>
										             	</fo:table-row>
									             	</#list>
									             </fo:table-body>
									             </fo:table>
							            	</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
							      <fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>--------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
          						  </fo:table-row> 
							    <fo:table-row>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							            </fo:table-cell>
									<fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">
							            		<fo:table>
								            		<fo:table-column column-width="80pt"/> 		
								            		<fo:table-column column-width="110pt"/> 	
								            		<fo:table-column column-width="140pt"/> 
								            		<fo:table-column column-width="100pt"/> 
								            		<fo:table-column column-width="75pt"/> 
								            		<fo:table-column column-width="90pt"/> 
								            		<fo:table-column column-width="115pt"/> 
					                                <fo:table-body>
					                               <#assign invTotals=invoiceDtls.get("invTotals").entrySet()>
					                               <fo:table-row>
						                                   <fo:table-cell>
								                           		<fo:block  keep-together="always" font-size="12pt" text-align="left" white-space-collapse="false">Total</fo:block>  
								                       		</fo:table-cell>
					                                        <#list invTotals as invDtls>
															<fo:table-cell>
								                           		<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${invDtls.getValue()?string("#0.00")}</fo:block>  
								                       		</fo:table-cell>
								                       		</#list>
										            </fo:table-row>
									             	
									             </fo:table-body>
									             </fo:table>
							            	</fo:block>  
							            </fo:table-cell>            
										 </fo:table-row>   
								  <fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>--------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
          						  </fo:table-row>          	
							     </#list>
							      </#list>
							     <#--<fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>--------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
          						  </fo:table-row> -->
							     <#--<fo:table-row>
					                    <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">
							            		<fo:table>
								            		<fo:table-column column-width="100pt"/> 		
								            		<fo:table-column column-width="110pt"/> 	
								            		<fo:table-column column-width="110pt"/> 
								            		<fo:table-column column-width="120pt"/> 
								            		<fo:table-column column-width="85pt"/> 
								            		<fo:table-column column-width="90pt"/> 
								            		<fo:table-column column-width="115pt"/> 
					                                <fo:table-body>
						                                <fo:table-row>
						                                 <fo:table-cell>
								                           		<fo:block  keep-together="always" font-size="12pt" text-align="left" white-space-collapse="false">Total</fo:block>  
								                       		</fo:table-cell>
						                                   <fo:table-cell>
								                           		<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${qty?string("#0.00")}</fo:block>  
								                       		</fo:table-cell>
															<fo:table-cell>
								                           		<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${exVal?string("#0.00")}</fo:block>  
								                       		</fo:table-cell>
								                       		<fo:table-cell>
								                           		<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${edVal?string("#0.00")}</fo:block>  
								                       		</fo:table-cell>
								                       		<fo:table-cell>
								                           		<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${vat?string("#0.00")}</fo:block>  
								                       		</fo:table-cell>
								                       		<fo:table-cell>
								                           		<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${cst?string("#0.00")}</fo:block>  
								                       		</fo:table-cell>
								                       		<fo:table-cell>
								                           		<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${total?string("#0.00")}</fo:block>  
								                       		</fo:table-cell>
										             	</fo:table-row>
									             </fo:table-body>
									             </fo:table>
							            	</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>-->
								</#list>
								 <fo:table-row>
					                    <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">
							            		<fo:table>
								            		<fo:table-column column-width="80pt"/> 		
								            		<fo:table-column column-width="110pt"/> 	
								            		<fo:table-column column-width="140pt"/> 
								            		<fo:table-column column-width="100pt"/> 
								            		<fo:table-column column-width="75pt"/> 
								            		<fo:table-column column-width="90pt"/> 
								            		<fo:table-column column-width="115pt"/> 
					                                <fo:table-body>
													<fo:table-row>
					                                 <fo:table-cell>
							                           		<fo:block  keep-together="always" font-size="12pt" text-align="left" white-space-collapse="false">Grand Total</fo:block>  
							                       		</fo:table-cell>
					                                   <fo:table-cell>
							                           		<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${totalQty?string("#0.00")}</fo:block>  
							                       		</fo:table-cell>
														<fo:table-cell>
							                           		<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${totalBasicRev?string("#0.00")}</fo:block>  
							                       		</fo:table-cell>
							                       		<fo:table-cell>
							                           		<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${totalBedRev?string("#0.00")}</fo:block>  
							                       		</fo:table-cell>
							                       		<fo:table-cell>
							                           		<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${totalVatRev?string("#0.00")}</fo:block>  
							                       		</fo:table-cell>
							                       		<fo:table-cell>
							                           		<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${totalCstRev?string("#0.00")}</fo:block>  
							                       		</fo:table-cell>
							                       		<fo:table-cell>
							                           		<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${grandTotal?string("#0.00")}</fo:block>  
							                       		</fo:table-cell>
									             	</fo:table-row>	
				             						</fo:table-body>
									             </fo:table>
							            	</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
	                     <#if prodTempMap?has_content>  	
                               <fo:table-row>
	                            <fo:table-cell>
                            		<fo:block  keep-together="always" text-align="left"  white-space-collapse="false" font-weight="bold">Total Products</fo:block>  
                       			</fo:table-cell>
                       			</fo:table-row>		
							 <#assign productDetails = prodTempMap.entrySet()>
	                	      <#list productDetails as prodTotals>
		                       <fo:table-row>
                    				<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="left"   font-size="12pt" white-space-collapse="false">${prodTotals.getKey()}</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="right"   font-size="12pt" white-space-collapse="false">${prodTotals.getValue()?string("#0.00")}</fo:block>  
	                       			</fo:table-cell>
	                       		</fo:table-row>
		                    </#list>
		                    </#if>
							<fo:table-row> 
							       <fo:table-cell number-columns-spanned="2">   						
							 	         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
							 	   <fo:table-cell number-columns-spanned="3">   						
							 	         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
							 	   <fo:table-cell number-columns-spanned="2">   						
							 	         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
							 </fo:table-row>	
							 <fo:table-row> 
							       <fo:table-cell number-columns-spanned="2">   						
							 	         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
							 	   <fo:table-cell number-columns-spanned="3">   						
							 	         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
							 	   <fo:table-cell number-columns-spanned="2">   						
							 	         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
							 </fo:table-row>	
								<fo:table-row> 
							      <fo:table-cell number-columns-spanned="2">   						
							 	         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
							 	   <fo:table-cell number-columns-spanned="3">   						
							 	         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
						 	         <fo:table-cell number-columns-spanned="2">   						
							 	         <fo:block text-align="left" white-space-collapse="false" font-size="12pt"  keep-together="always" font-weight="bold">MF/GMF</fo:block>
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
			        	${uiLabelMap.NoOrdersFound}.
			   		 </fo:block>
				</fo:flow>
			</fo:page-sequence>	
		</#if>   
 </#if>
 </fo:root>
</#escape>