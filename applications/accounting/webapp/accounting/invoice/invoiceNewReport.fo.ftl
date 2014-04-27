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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in" margin-left=".2in" margin-right=".2in">
                <fo:region-body margin-top=".4in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
		        <fo:page-sequence master-reference="main">		        	
		        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
		        	    <fo:block  border-style="solid" font-family="Courier,monospace">
        	            <fo:block text-align="center" border-style="solid">
        	            <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="9pt" keep-together="always">&#160;                                                      &#160;                                           </fo:block>
					    <fo:block text-align="center"  white-space-collapse="false" font-family="Courier,monospace" font-weight="bold" font-size="12pt" keep-together="always">${companyName}</fo:block>
					     <#--
					     <#if postalAddress?exists>
					        <#if postalAddress?has_content>
					            <fo:block font-weight="bold" >${postalAddress.address1?if_exists}</fo:block>
					            <#if postalAddress.address2?has_content><fo:block>${postalAddress.address2?if_exists}</fo:block></#if>
					            <fo:block font-weight="bold" >${postalAddress.city?if_exists}, ${stateProvinceAbbr?if_exists} ${postalAddress.postalCode?if_exists}, ${countryName?if_exists}</fo:block>
					        </#if>
					    <#else>
					        <fo:block>${uiLabelMap.CommonNoPostalAddress}</fo:block>
					        <fo:block>${uiLabelMap.CommonFor}: ${companyName}</fo:block>
					    </#if> -->
					    </fo:block>	

					    <#--<fo:block text-align="center" white-space-collapse="false" font-weight="bold" font-size="12pt" keep-together="always">&#160;(Government Of India Society): Balanagar Hyderabad.  </fo:block>
		        		 Table Start -->
		        		<fo:table  table-layout="fixed" width="100%" space-before="0.2in">
		        				    <fo:table-column column-width="20%"/>
        						    <fo:table-column column-width="55%"/>
        						    <fo:table-column column-width="25%"/>

        						   	<fo:table-header>
        						   	<fo:table-row>
		    								<fo:table-cell border-style="solid">
		    									 <fo:block text-align="right" font-family="Courier,monospace"  font-size="12pt">&#160;</fo:block>
				                                <fo:block text-align="center" white-space-collapse="false" font-weight="bold" font-size="12pt" keep-together="always">&#160; VOUCHER NO:${invoice.invoiceId}</fo:block>
				                             </fo:table-cell>
		    								<fo:table-cell border-style="solid">
		    									 <fo:block text-align="right" font-family="Courier,monospace"  font-size="12pt">&#160;</fo:block>
				                                <fo:block text-align="center" white-space-collapse="false" font-weight="bold" font-size="12pt" keep-together="always">&#160; ${invoice.prefPaymentMethodTypeId} PAYMENT VOUCHER</fo:block>
				                               </fo:table-cell>
		    								<fo:table-cell border-style="solid">
				                               <fo:block text-align="center" font-family="Courier,monospace"  font-size="12pt">&#160;</fo:block>
		    									<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; RECEIVED PAYMENT </fo:block>
		    									</fo:table-cell>
		    						</fo:table-row>	
        						   	</fo:table-header>
        						   		<fo:table-body>
        				                   <fo:table-row width="100%">
        						   				<fo:table-cell>
        						   				<fo:table  table-layout="fixed" width="100%" space-before="0.2in">
		        				    				<fo:table-column column-width="200pt"/>
					        						<fo:table-column column-width="300pt"/>
					        						<fo:table-column column-width="192pt"/>
        						   	        	<fo:table-header>
        						  				 	<fo:table-row>
						        						<fo:table-cell border-style="solid">
						        								<fo:block font-size="12pt" keep-together="never"  white-space-collapse="false" >&#160;  </fo:block>
						        						</fo:table-cell>
						        							<fo:table-cell border-style="solid">
						        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">PAYEE CODE:<#if billingParty?has_content>${dispalyParty.partyId}</#if></fo:block>
						        						</fo:table-cell> 
						        					</fo:table-row>	
						        					<fo:table-row>
						        						<fo:table-cell border-style="solid">
						        								<fo:block font-size="10pt"  white-space-collapse="false" font-weight="bold" >&#160;VOUCHER DATE: ${invoiceDate?if_exists}  </fo:block>
						        						</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">ON A/C:</fo:block>
					        							</fo:table-cell> 
						        					</fo:table-row>	
						        					<fo:table-row>
						        						<fo:table-cell border-style="solid">
						        								<fo:block font-size="12pt"  white-space-collapse="false" font-weight="bold" >&#160;</fo:block>
						        						</fo:table-cell>
						        						<fo:table-cell border-style="solid">
						        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">AMOUNT:${invoiceTotal}</fo:block>
						        						</fo:table-cell> 
						        					</fo:table-row>	
						        	
						        					<fo:table-row>
						        						<fo:table-cell border-style="solid">
						        								<fo:block font-size="10pt"  white-space-collapse="false" font-weight="bold" >AMOUNT(in words):${Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble(invoiceTotal?string("#0")), "%rupees-and-paise", locale).toUpperCase()} ONLY </fo:block>
						        						</fo:table-cell>
						        						<fo:table-cell border-style="solid">
						        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
						        						</fo:table-cell> 
						        						<fo:table-cell border-top-style="hidden">
						        								<fo:block font-size="12pt"  white-space-collapse="false" font-weight="bold" >&#160;  </fo:block>
						        						</fo:table-cell>

						        					</fo:table-row>	
						        	
							        			 	<fo:table-row>
							        					<fo:table-cell border-style="solid">
						        								<fo:block font-size="12pt"  white-space-collapse="false" font-weight="bold" >&#160; </fo:block>
						        						</fo:table-cell>
						        						
						        					    <fo:table-cell border-bottom-style="hidden" border-right-style="solid">
						        								<fo:block font-size="12pt"  white-space-collapse="false" font-weight="bold" > </fo:block>
						        						</fo:table-cell>
						        						<fo:table-cell border-top-style="hidden">
						        								<fo:block font-size="12pt"  white-space-collapse="false" font-weight="bold" >&#160;Signature of recipient </fo:block>
						        						</fo:table-cell>
						        				  </fo:table-row>	
						        				  <fo:table-row>
						        						<fo:table-cell bottom="">
						        		                    <fo:table  table-layout="fixed" width="100%" space-before="0.2in">
		        				    								 <fo:table-column column-width="50%"/>
        						   									 <fo:table-column column-width="50%"/>	
        						   									 <fo:table-body>
        						   									 <fo:table-row>
        						   									 <fo:table-cell border-style="solid">
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        							  		  </fo:table-cell>
						        							  		  <fo:table-cell border-style="solid">
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; DEBIT </fo:block>
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        							  		  </fo:table-cell>
        						   									 </fo:table-row>
        						   									 </fo:table-body>
        						   		                    </fo:table>
						        						</fo:table-cell>
						        						<fo:table-cell border-style="solid">
						        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; PARTICULARS</fo:block>
						        						</fo:table-cell> 
							        					<fo:table-cell border-style="solid">
												        		<fo:table  table-layout="fixed" width="100%" space-before="0.2in">
								        				    								 <fo:table-column column-width="50%"/>
						        						   									 <fo:table-column column-width="50%"/>	
						        						   									 <fo:table-body>
						        						   									 <fo:table-row>
						        						   									 <#if invoice.prefPaymentMethodTypeId=="CASH">
						        						   									   <fo:table-cell >
						        						   									 <fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
												        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
												        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;&#160;</fo:block>
												        							  		  </fo:table-cell>
												        							  		  <#else>
												        							  		   <fo:table-cell border-style="solid">
						        						   									 <fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; Ch.no &amp;</fo:block>
												        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
												        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;&#160; Date</fo:block>
												        							  		  </fo:table-cell>
												        							  		  </#if>
												        							  		  <fo:table-cell border-style="solid">
												        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; Amount</fo:block>
												        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; &#160; Rs. </fo:block>
												        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
												        							  		  </fo:table-cell>
						        						   									 </fo:table-row>
						        						   									 </fo:table-body>
						        						   		 </fo:table>
							        						</fo:table-cell> 
						        						</fo:table-row>	
						        						<#-->
						        						<fo:table-row width="100%">
						        						<fo:table-cell  >
											        		<fo:table  table-layout="fixed" width="100%" space-before="0.2in" border-right-style="hidden">
							        				    								<fo:table-column column-width="50%"/>
					        						   									 <fo:table-column column-width="50%"/>	
					        						   									 <fo:table-body >
					        						   									 <fo:table-row>
					        						   									 <fo:table-cell >
											        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
											        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
											        							  		  </fo:table-cell>
											        							  		  <fo:table-cell >
											        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; By payment of amount towards</fo:block>
											        							  		  </fo:table-cell>
					        						   									 </fo:table-row>
					        						   									 </fo:table-body>
					        						   	     </fo:table>
						        						</fo:table-cell>
						        					    <fo:table-cell >
						        								<fo:block font-size="12pt"  white-space-collapse="false" font-weight="bold" >&#160;</fo:block>
						        						</fo:table-cell>
						        						<fo:table-cell >
											     			<fo:table  table-layout="fixed" width="100%" space-before="0.2in" >
							        				    								<fo:table-column column-width="50%"/>
					        						   									 <fo:table-column column-width="50%"/>	
					        						   									 <fo:table-body >
					        						   									 <fo:table-row>
					        						   									 <fo:table-cell >
											        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
											        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
											        							  		  </fo:table-cell>
											        							  		  <fo:table-cell border-right-style="hidden">
											        							  		  </fo:table-cell>
					        						   									 </fo:table-row>
					        						   									 </fo:table-body>
					        						   		 </fo:table>
						        						</fo:table-cell>
														</fo:table-row> -->	
						        						<#assign sno=0>
						        						  <#list invoiceItems as invoiceItem>
						        						  <#assign sno=sno+1>
												            <#assign itemType = invoiceItem.getRelatedOne("InvoiceItemType")>
												            <#assign isItemAdjustment = Static["org.ofbiz.entity.util.EntityTypeUtil"].hasParentType(delegator, "InvoiceItemType", "invoiceItemTypeId", itemType.getString("invoiceItemTypeId"), "parentTypeId", "INVOICE_ADJ")/>
																 <#if invoiceItem.description?has_content>
														                <#assign description=invoiceItem.description>
														            <#elseif taxRate?has_content & taxRate.get("description",locale)?has_content>
														                <#assign description=taxRate.get("description",locale)>
														            <#elseif itemType.get("description",locale)?has_content>
														                <#assign description=itemType.get("description",locale)>
														            </#if>        						
						        						<fo:table-row>
						        						<fo:table-cell bottom="">
						        		                    <fo:table  table-layout="fixed" width="100%" space-before="0.2in">
		        				    								 <fo:table-column column-width="50%"/>
        						   									 <fo:table-column column-width="50%"/>	
        						   									 <fo:table-body>
        						   									 <fo:table-row>
        						   									 <fo:table-cell >
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        							  		  </fo:table-cell>
						        							  		  <fo:table-cell >
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; ${sno} </fo:block>
						        							  		  </fo:table-cell>
        						   									 </fo:table-row>
        						   									 </fo:table-body>
        						   		                    </fo:table>
						        						</fo:table-cell>
						        						<fo:table-cell >
						        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; &#160; &#160; &#160; ${description?if_exists}</fo:block>
						        						</fo:table-cell> 
							        					<fo:table-cell>
												        		<fo:table  table-layout="fixed" width="100%" space-before="0.2in">
			        				    								 <fo:table-column column-width="50%"/>
	        						   									 <fo:table-column column-width="50%"/>	
	        						   									 <fo:table-body>
	        						   									 <fo:table-row>
	        						   									 <fo:table-cell >
	        						   									 <fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
							        							  		  </fo:table-cell>
							        							  		  <fo:table-cell >
							        							  		  <fo:block text-align="right" > <@ofbizCurrency amount=(Static["org.ofbiz.accounting.invoice.InvoiceWorker"].getInvoiceItemTotal(invoiceItem)) isoCode=invoice.currencyUomId?if_exists/> </fo:block>
							        							  		  </fo:table-cell>
	        						   									 </fo:table-row>
	        						   									 </fo:table-body>
						        						   		 </fo:table>
							        						</fo:table-cell> 
						        						</fo:table-row>	
						        						 </#list>
						        							<fo:table-row>
						        						<fo:table-cell bottom="">
						        		                    <fo:table  table-layout="fixed" width="100%" space-before="0.2in">
		        				    								 <fo:table-column column-width="50%"/>
        						   									 <fo:table-column column-width="50%"/>	
        						   									 <fo:table-body>
        						   									 <fo:table-row>
        						   									 <fo:table-cell >
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        							  		  </fo:table-cell>
						        							  		  <fo:table-cell >
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; </fo:block>
						        							  		  </fo:table-cell>
        						   									 </fo:table-row>
        						   									 </fo:table-body>
        						   		                    </fo:table>
						        						</fo:table-cell>
						        						<fo:table-cell >
						        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; &#160; &#160; &#160; </fo:block>
						        						</fo:table-cell> 
							        					<fo:table-cell >
												        		<fo:table  table-layout="fixed" width="100%" space-before="0.2in">
			        				    								 <fo:table-column column-width="50%"/>
	        						   									 <fo:table-column column-width="50%"/>	
	        						   									 <fo:table-body>
	        						   									 <fo:table-row>
	        						   									 <fo:table-cell >
	        						   									 <fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
							        							  		  </fo:table-cell>
							        							  		  <fo:table-cell >
							        							  		  <fo:block text-align="right" > </fo:block>
							        							  		  </fo:table-cell>
	        						   									 </fo:table-row>
	        						   									 </fo:table-body>
						        						   		 </fo:table>
							        						</fo:table-cell> 
						        						</fo:table-row>
							       						<fo:table-row>
						        						<fo:table-cell border-bottom-style="solid" border-right-style="hidden">
						        								<fo:block font-size="12pt"  white-space-collapse="false" font-weight="bold" >&#160; SANCTIONED BY :</fo:block>
						        						</fo:table-cell>
						        						<fo:table-cell >
						        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        						</fo:table-cell> 
						        							<fo:table-cell border-top-style="hidden">
												        		<fo:table  table-layout="fixed" width="100%" space-before="0.2in">
								        				    								 <fo:table-column column-width="50%"/>
						        						   									 <fo:table-column column-width="50%"/>	
						        						   									 <fo:table-body>
						        						   									 <fo:table-row>
						        						   									 <fo:table-cell >
												        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;Total:</fo:block>
												        							  		 </fo:table-cell>
												        							  		 <fo:table-cell >
												        											<fo:block text-align="right" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;<@ofbizCurrency amount=invoiceTotal isoCode=invoice.currencyUomId?if_exists/></fo:block>
												        							  		 </fo:table-cell>
						        						   									 </fo:table-row>
						        						   									 </fo:table-body>
						        						   	   </fo:table>
						        						</fo:table-cell>
						        						</fo:table-row>	
						        						<fo:table-row>
						        						<fo:table-cell border-right-style="hidden">
						        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
						        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
						        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
						        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
						        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; PROCD.  A.A.O:</fo:block>
						        								
						        						</fo:table-cell>
						        							<fo:table-cell border-bottom-style="hidden" border-style="solid">
						        							 	<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160;</fo:block>
						        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
						        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
						        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
						        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; ACCOUNTANT &#160; &#160; &#160;&#160;&#160;&#160; &#160; SECRETARY</fo:block>
						        						</fo:table-cell>
						        						<fo:table-cell border-left-style="hidden" border-top-style="solid">
						        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160;</fo:block>
						        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
						        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
						        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
						        								<fo:block text-align="right" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; PRINCIPAL DIRECTOR &#160;</fo:block>
						        						</fo:table-cell>  

						        						</fo:table-row>	
        						   			</fo:table-header>
        						   			<fo:table-body>
        						   			<fo:table-row>
        						   			<fo:table-cell>
        						   		    </fo:table-cell>
        						   			</fo:table-row>
        						   		</fo:table-body>
        				         </fo:table>

        				</fo:table-cell>
        			</fo:table-row>
        		</fo:table-body>
        	</fo:table>

		        		<#-- Table End -->
		        		
		        		
		        		</fo:block>

		          	</fo:flow>
		          	</fo:page-sequence>
		      
		        
		    
     </fo:root>
</#escape>