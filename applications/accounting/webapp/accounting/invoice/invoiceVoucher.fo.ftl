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
					     <fo:block text-align="center"  white-space-collapse="false" font-family="Courier,monospace" font-weight="bold" font-size="12pt" keep-together="always">INVOICE AND PAYMENT VOUCHER</fo:block>
					    </fo:block>	

					    <#--
		        		 Table Start -->
		        		<fo:table  table-layout="fixed" width="100%" space-before="0.2in">
		        				    <fo:table-column column-width="20%"/>
        						    <fo:table-column column-width="52%"/>
        						    <fo:table-column column-width="28%"/>

        						   	<fo:table-header>
        						   	<fo:table-row>
		    								<fo:table-cell border-style="solid">
		    									 <fo:block text-align="right" font-family="Courier,monospace"  font-size="12pt">&#160;</fo:block>
				                                <fo:block text-align="center" white-space-collapse="false" font-weight="bold" font-size="12pt" keep-together="always">&#160;VOUCHER NO:${invoice.invoiceId}</fo:block>
				                             </fo:table-cell>
		    								<fo:table-cell border-style="solid">
		    									 <fo:block text-align="right" font-family="Courier,monospace"  font-size="12pt">&#160;</fo:block>
				                                <fo:block text-align="center" white-space-collapse="false" font-weight="bold" font-size="12pt" keep-together="always">&#160;INVOICE DETAILS</fo:block>
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
		        				    				<fo:table-column column-width="300pt"/>
					        						<fo:table-column column-width="200pt"/>
					        						<fo:table-column column-width="192pt"/>
        						   	        	<fo:table-header>
						        					<fo:table-row>
						        						<fo:table-cell>
						        								<fo:block font-size="12pt"  white-space-collapse="false" font-weight="bold" >&#160;&#160;VOUCHER DATE: ${invoiceDate?if_exists}  </fo:block>
						        						</fo:table-cell>
						        						<#assign partyFullName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, dispalyParty.partyId?if_exists, false)>
						        						<fo:table-cell>
						        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together = "always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;PAYEE NAME:<#if billingParty?has_content>${partyFullName}</#if></fo:block>
						        						</fo:table-cell> 
						        					</fo:table-row>	
						        					<fo:table-row>
						        						<fo:table-cell>
						        								<fo:block font-size="12pt"  white-space-collapse="false" font-weight="bold" >&#160;</fo:block>
						        						</fo:table-cell>
						        						<fo:table-cell>
						        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;AMOUNT:${invoiceTotal?if_exists?string("#0.00")}</fo:block>
						        						</fo:table-cell> 
						        					</fo:table-row>	
						        					 <fo:table-row>
															<fo:table-cell>
											            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
											       			</fo:table-cell>
														 </fo:table-row>
						        					<fo:table-row>
						        						<fo:table-cell>
						        								<fo:block font-size="12pt"  white-space-collapse="false" font-weight="bold" keep-together="always">&#160;&#160;AMOUNT(in words):${Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble(invoiceTotal?string("#0")), "%rupees-and-paise", locale).toUpperCase()} ONLY </fo:block>
						        						</fo:table-cell>
						        						<fo:table-cell>
						        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
						        						</fo:table-cell> 
						        						<fo:table-cell>
						        								<fo:block font-size="12pt"  white-space-collapse="false" font-weight="bold" >&#160;  </fo:block>
						        						</fo:table-cell>
						        					</fo:table-row>	
						        				  <fo:table-row>
						        						<fo:table-cell bottom="">
						        		                    <fo:table  table-layout="fixed" width="100%" space-before="0.2in">
		        				    								 <fo:table-column column-width="20%"/>
        						   									 <fo:table-column column-width="80%"/>	
        						   									 <fo:table-body>
        						   									 <fo:table-row>
        						   									 <fo:table-cell border-style="solid">
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;S.No</fo:block>
						        							  		  </fo:table-cell>
						        							  		  <fo:table-cell border-style="solid">
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; PARTICULARS </fo:block>
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        							  		  </fo:table-cell>
        						   									 </fo:table-row>
        						   									 </fo:table-body>
        						   		                    </fo:table>
						        						</fo:table-cell>
						        						<fo:table-cell border-style="solid">
						        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; &#160; &#160; GL ACCOUNT </fo:block>
						        						</fo:table-cell> 
							        					<fo:table-cell border-style="solid">
												        		<fo:table  table-layout="fixed" width="100%" space-before="0.2in">
		        				    								 <fo:table-column column-width="50%"/>
        						   									 <fo:table-column column-width="50%"/>	
        						   									 <fo:table-body>
        						   									 <fo:table-row>
        						   									   <fo:table-cell >
        						   									 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;&#160;</fo:block>
						        							  		  </fo:table-cell>
						        							  		  <fo:table-cell border-style="solid">
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; </fo:block>
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;Amount Rs. </fo:block>
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        							  		  </fo:table-cell>
        						   									 </fo:table-row>
        						   									 </fo:table-body>
						        						   		 </fo:table>
							        						</fo:table-cell> 
						        						</fo:table-row>	
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
														   <#assign invoiceItemTypeDetails = delegator.findOne("InvoiceItemType", {"invoiceItemTypeId" : invoiceItem.invoiceItemTypeId}, true)?if_exists/>         
														   <#assign glAccountId =   invoiceItemTypeDetails.defaultGlAccountId?if_exists>   
														    <#if glAccountId?has_content>
														    	<#assign glAccountDetails = delegator.findOne("GlAccount", {"glAccountId" : glAccountId}, true)?if_exists/>
														    </#if>         						
						        						<fo:table-row>
						        						<fo:table-cell bottom="">
						        		                    <fo:table  table-layout="fixed" width="100%" space-before="0.2in">
		        				    								 <fo:table-column column-width="20%"/>
        						   									 <fo:table-column column-width="80%"/>	
        						   									 <fo:table-body>
        						   									 <fo:table-row>
        						   									 <fo:table-cell >
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always">&#160; ${sno}</fo:block>
						        							  		  </fo:table-cell>
						        							  		  <fo:table-cell >
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always">${description} </fo:block>
						        							  		  </fo:table-cell>
        						   									 </fo:table-row>
        						   									 </fo:table-body>
        						   		                    </fo:table>
						        						</fo:table-cell>
							        					<fo:table-cell>
												        		<fo:table  table-layout="fixed" width="100%" space-before="0.2in">
	        						   									 <fo:table-column column-width="30%"/>
	        						   									 <fo:table-column column-width="25%"/>
	        						   									 <fo:table-column column-width="85%"/>	
	        						   									 <fo:table-body>
	        						   									 <fo:table-row>
	        						   									 <fo:table-cell >
	        						   									 	<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always">&#160;<#if glAccountDetails?has_content>${glAccountDetails.description?if_exists}(${glAccountId})</#if></fo:block>
							        							  		  </fo:table-cell>
							        							  		  <fo:table-cell>
											            						<fo:block linefeed-treatment="preserve">&#160;</fo:block>
											       						</fo:table-cell>
							        							  		  <fo:table-cell >
							        							  		  <fo:block text-align="right" font-weight="bold"> <@ofbizCurrency amount=(Static["org.ofbiz.accounting.invoice.InvoiceWorker"].getInvoiceItemTotal(invoiceItem)) isoCode=invoice.currencyUomId?if_exists/> </fo:block>
							        							  		  </fo:table-cell>
	        						   									 </fo:table-row>
	        						   									 </fo:table-body>
						        						   		 </fo:table>
							        						</fo:table-cell> 
						        						</fo:table-row>	
						        						 </#list>
						        						 <fo:table-row>
															<fo:table-cell>
											            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
											       			</fo:table-cell>
														 </fo:table-row>
						        						  <fo:table-row>
						   									 <fo:table-cell>
				        										<fo:block font-size="12pt"  white-space-collapse="false" font-weight="bold" >&#160;SANCTIONED BY :</fo:block>
				        									</fo:table-cell>
						   									 <fo:table-cell >
				        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
				        							  		 </fo:table-cell>
			        							  		 	<fo:table-cell >
			        											<fo:block text-align="right" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">Total:&#160;<@ofbizCurrency amount=invoiceTotal isoCode=invoice.currencyUomId?if_exists/></fo:block>
			        							  		 	</fo:table-cell>
						        						</fo:table-row>
						        						
						        						<fo:table-row>
															<fo:table-cell>
											            		<fo:block>------------------------------------------------------------------------------------------------</fo:block>
											       			</fo:table-cell>
														 </fo:table-row>
						        						// payment details here
						        						 <#if printPaymentsList?has_content> 
						        						<#assign sno=0>
						        						  <#list printPaymentsList as paymentListReport>
						        						  <#assign sno=sno+1>
						        						  <#assign  partyName="">
						        						  <#assign  partyId="">
						        						  <#if paymentListReport.partyIdFrom?exists && paymentListReport.partyIdFrom == "Company">
									            			  <#assign partyId = paymentListReport.partyIdTo>
									            		  <#else>
									            			  <#assign partyId = paymentListReport.partyIdFrom>
									            		  </#if>
						        						 <#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)>
						        						 <fo:table-row> 
						        						 	<fo:table-cell>
						        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
						        						 	</fo:table-cell>
						        						 	<fo:table-cell>
						        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">PAYMENT DETAILS</fo:block>
						        						 	</fo:table-cell>
						        						 	<fo:table-cell>
						        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        						 	</fo:table-cell>
						        						 </fo:table-row>
						        						 <fo:table-row> 
						        						 	<fo:table-cell>
						        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;PAYMENT REF NO:${paymentListReport.paymentId?if_exists}</fo:block>
						        						 	</fo:table-cell>
						        						 	<fo:table-cell>
						        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"><#if paymentListReport.paymentMethodTypeId?has_content && (paymentListReport.paymentMethodTypeId == "CHEQUE_PAYIN" || paymentListReport.paymentMethodTypeId == "CHEQUE_PAYOUT")>CHEQUE VOUCHER</#if><#if paymentListReport.paymentMethodTypeId?has_content && (paymentListReport.paymentMethodTypeId == "CASH_PAYIN" || paymentListReport.paymentMethodTypeId == "CASH_PAYOUT")>CASH VOUCHER</#if></fo:block>
						        						 	</fo:table-cell>
						        						 	<fo:table-cell>
						        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">PAYMENT DATE:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(paymentListReport.paymentDate?if_exists, "MMMM dd,yyyy")}</fo:block>
						        						 	</fo:table-cell>
						        						 </fo:table-row>
						        						 <#if paymentListReport.paymentMethodTypeId?has_content && (paymentListReport.paymentMethodTypeId == "CHEQUE_PAYIN" || paymentListReport.paymentMethodTypeId == "CHEQUE_PAYOUT")> 
						        							
						        							 <#if paymentListReport.paymentMethodId?has_content>
														    	<#assign paymentMethodDetails = delegator.findOne("PaymentMethod", {"paymentMethodId" : paymentListReport.paymentMethodId}, true)?if_exists/>
														    </#if>
														 <#if paymentListReport.paymentRefNum?has_content>   
						        						 <fo:table-row> 
						        						 	<fo:table-cell>
						        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;CHEQUE NO:${paymentListReport.paymentRefNum?if_exists}</fo:block>
						        						 	</fo:table-cell>
						        						 	<fo:table-cell>
						        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
						        						 	</fo:table-cell>
						        						 	<fo:table-cell>
						        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">CHEQUE DATE:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(paymentListReport.instrumentDate?if_exists, "MMMM dd,yyyy")}</fo:block>
						        						 	</fo:table-cell>
						        						 </fo:table-row>
						        						 <#if paymentMethodDetails?has_content>
						        						 <fo:table-row> 
						        						 	<fo:table-cell>
						        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;CHEQUE BANK DETAILS:${paymentMethodDetails.description?if_exists}</fo:block>
						        						 	</fo:table-cell>
						        						 	<fo:table-cell>
						        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
						        						 	</fo:table-cell>
						        						 	<fo:table-cell>
						        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
						        						 	</fo:table-cell>
						        						 </fo:table-row>
						        						 </#if>
						        						 </#if>
						        						 </#if>
						        						 
						        						 <fo:table-row>
						        						<fo:table-cell bottom="">
						        		                    <fo:table  table-layout="fixed" width="100%" space-before="0.2in">
		        				    								 <fo:table-column column-width="20%"/>
        						   									 <fo:table-column column-width="80%"/>	
        						   									 <fo:table-body>
        						   									 <fo:table-row>
        						   									 <fo:table-cell border-style="solid">
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;S.No</fo:block>
						        							  		  </fo:table-cell>
						        							  		  <fo:table-cell border-style="solid">
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; DESCRIPTION </fo:block>
						        							  		  </fo:table-cell>
        						   									 </fo:table-row>
        						   									 </fo:table-body>
        						   		                    </fo:table>
						        						</fo:table-cell>
						        						<fo:table-cell border-style="solid">
						        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; &#160; &#160; PARTY CODE</fo:block>
						        						</fo:table-cell> 
							        					<fo:table-cell border-style="solid">
												        		<fo:table  table-layout="fixed" width="100%" space-before="0.2in">
		        				    								 <fo:table-column column-width="50%"/>
        						   									 <fo:table-column column-width="150%"/>	
        						   									 <fo:table-body>
        						   									 <fo:table-row>
						        							  		  <fo:table-cell border-style="solid">
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; </fo:block>
						        											<fo:block text-align="right" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; &#160;Amount Rs.</fo:block>
						        							  		  </fo:table-cell>
        						   									 </fo:table-row>
        						   									 </fo:table-body>
						        						   		 </fo:table>
							        						</fo:table-cell> 
						        						</fo:table-row>
						        						<fo:table-row>
															<fo:table-cell>
											            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
											       			</fo:table-cell>
				       									</fo:table-row>
						        						<fo:table-row>
						        						<fo:table-cell bottom="">
						        		                    <fo:table  table-layout="fixed" width="100%" space-before="0.2in">
		        				    								 <fo:table-column column-width="20%"/>
        						   									 <fo:table-column column-width="80%"/>	
        						   									 <fo:table-body>
        						   									 <fo:table-row>
        						   									 <fo:table-cell >
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always">&#160; ${sno}</fo:block>
						        							  		  </fo:table-cell>
						        							  		  <fo:table-cell >
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always">${partyName}</fo:block>
						        							  		  </fo:table-cell>
        						   									 </fo:table-row>
        						   									 </fo:table-body>
        						   		                    </fo:table>
						        						</fo:table-cell>
							        					<fo:table-cell>
												        		<fo:table  table-layout="fixed" width="100%" space-before="0.2in">
			        				    								 <fo:table-column column-width="30%"/>
	        						   									 <fo:table-column column-width="25%"/>
	        						   									 <fo:table-column column-width="85%"/>	
	        						   									 <fo:table-body>
	        						   									 <fo:table-row>
	        						   									 <fo:table-cell >
	        						   									 	<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${partyId}</fo:block>
							        							  		  </fo:table-cell>
							        							  		 <fo:table-cell >
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always">&#160;</fo:block>
						        							  		  	</fo:table-cell> 
						        							  		  	<#assign paymentAmount = paymentListReport.amount?if_exists>
							        							  		  <fo:table-cell >
							        							  		  <fo:block text-align="right" font-weight="bold">&#160;&#160;<@ofbizCurrency amount=paymentAmount isoCode=currencyUomId/></fo:block>
							        							  		  </fo:table-cell>
	        						   									 </fo:table-row>
	        						   									 </fo:table-body>
						        						   		 </fo:table>
							        						</fo:table-cell> 
						        						</fo:table-row>	
						        						 </#list>
						        						</#if>
						        						<fo:table-row>
															<fo:table-cell>
											            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
											       			</fo:table-cell>
														</fo:table-row>
						        						<fo:table-row>
															<fo:table-cell>
											            		<fo:block>------------------------------------------------------------------------------------------------</fo:block>
											       			</fo:table-cell>
														 </fo:table-row>
						        						<fo:table-row>
							        						<fo:table-cell>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">PROCD.  A.A.O:  &#160;&#160;&#160;ACCOUNTANT</fo:block>
							        						</fo:table-cell>
							        							<fo:table-cell>
							        							 	<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160;</fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;&#160;&#160;For Mother Dairy</fo:block>
							        						</fo:table-cell>
							        						<fo:table-cell>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160;</fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="right" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; D.Mgr(Finance)/Mgr(Finance)</fo:block>
							        						</fo:table-cell>  
						        						</fo:table-row>
						        						<fo:table-row>
							        						<fo:table-cell>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
							        						</fo:table-cell>
							        							<fo:table-cell>
							        							 	<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160;</fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
							        						</fo:table-cell>
							        						<fo:table-cell>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160;</fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="right" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; Signature of Recipient </fo:block>
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
