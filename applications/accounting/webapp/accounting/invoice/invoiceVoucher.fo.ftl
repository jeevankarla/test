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
						<fo:block>
					    <#--
		        		 Table Start -->
		        		<fo:table  table-layout="fixed" width="100%" space-before="0.2in">
		        				    <fo:table-column column-width="30%"/>
        						    <fo:table-column column-width="30%"/>
        						    <fo:table-column column-width="40%"/>

        						   	<fo:table-header>
        						   	<fo:table-row>
        						   			<fo:table-cell border-style="solid"></fo:table-cell>
		    								<fo:table-cell border-style="solid">
        						   				<fo:block text-align="center"  white-space-collapse="false" font-family="Courier,monospace" font-weight="bold" font-size="12pt" keep-together="always">${companyName}</fo:block>
        						   			</fo:table-cell>
        						   			<fo:table-cell border-style="solid"></fo:table-cell>
        						   	</fo:table-row>
        						   	<fo:table-row>
        						   	<fo:table-cell border-style="solid"></fo:table-cell>
		    								<fo:table-cell border-style="solid">
        						   				<fo:block text-align="center"  white-space-collapse="false" font-family="Courier,monospace" font-weight="bold" font-size="12pt" keep-together="always">INVOICE AND PAYMENT VOUCHER</fo:block>
        						   			</fo:table-cell>
        						   			<fo:table-cell border-style="solid"></fo:table-cell>
        						   	</fo:table-row>	
        						   	<fo:table-row>
		    								<fo:table-cell border-style="solid">
		    									 <fo:block text-align="right" font-family="Courier,monospace"  font-size="12pt">&#160;</fo:block>
				                                <fo:block text-align="center" white-space-collapse="false" font-weight="bold" font-size="12pt" keep-together="always">&#160;INVOICE NO:${invoice.invoiceId}</fo:block>
				                             </fo:table-cell>
		    								<fo:table-cell border-style="solid">
		    									 <fo:block text-align="right" font-family="Courier,monospace"  font-size="12pt">&#160;</fo:block>
				                                <fo:block text-align="center" white-space-collapse="false" font-weight="bold" font-size="12pt" keep-together="always">&#160;INVOICE DETAILS</fo:block>
				                               </fo:table-cell>
		    								<fo:table-cell border-style="solid">
				                               <fo:block text-align="center" font-family="Courier,monospace"  font-size="12pt">&#160;</fo:block>
		    									<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;&#160;VOUCHER DATE: ${invoiceDate?if_exists}</fo:block>
		    									</fo:table-cell>
		    						</fo:table-row>	
        						   	</fo:table-header>
        						   		<fo:table-body>
        				                   <fo:table-row width="100%">
        						   				<fo:table-cell>
        						   				<fo:table  table-layout="fixed" width="100%" space-before="0.2in">
		        				    				<fo:table-column column-width="300pt"/>
					        						<fo:table-column column-width="200pt"/>
					        						<fo:table-column column-width="175pt"/>
        						   	        	<fo:table-header>
						        					<fo:table-row>
						        						<#assign partyFullName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, dispalyParty.partyId?if_exists, false)>
						        						<fo:table-cell>
						        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together = "always" font-weight="bold">PAYEE NAME:<#if billingParty?has_content>${partyFullName}</#if></fo:block>
						        						</fo:table-cell> 
						        					</fo:table-row>	
						        					<fo:table-row>
						        						<fo:table-cell>
						        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">AMOUNT:${invoiceTotal?if_exists?string("#0.00")}</fo:block>
						        						</fo:table-cell> 
						        					</fo:table-row>	
						        					 <fo:table-row>
															<fo:table-cell>
											            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
											       			</fo:table-cell>
														 </fo:table-row>
						        					<fo:table-row>
						        					<#assign amountWords = Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(invoiceTotal, "%indRupees-and-paiseRupees", locale)>
						        						<fo:table-cell>
						        								<fo:block font-size="12pt"  white-space-collapse="false" font-weight="bold" keep-together="always">AMOUNT(in words):${amountWords} ONLY </fo:block>
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
		        				    								 <fo:table-column column-width="10%"/>
        						   									 <fo:table-column column-width="60%"/>
        						   									 <fo:table-column column-width="55%"/>
        						   									 <fo:table-column column-width="27%"/>	
        						   									 <fo:table-body>
        						   									 <fo:table-row>
        						   									 <fo:table-cell border-style="solid">
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;S.No</fo:block>
						        							  		  </fo:table-cell>
						        							  		  <fo:table-cell border-style="solid">
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        											<fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; PARTICULARS </fo:block>
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        							  		  </fo:table-cell>
						        							  		  <fo:table-cell border-style="solid">
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        											<fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; GL ACCOUNT </fo:block>
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        							  		  </fo:table-cell>
						        							  		  <fo:table-cell border-style="solid">
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        											<fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; AMOUNT Rs. </fo:block>
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        							  		  </fo:table-cell>
        						   									 </fo:table-row>
        						   									 </fo:table-body>
        						   		                    </fo:table>
						        						</fo:table-cell>
						        						</fo:table-row>	
						        						<#assign sno=0>
						        						  <#list invoiceItemList as invoiceItem>
						        						  <#assign acctngTransDetails = {}>
						        						  <#assign glAccountDetails = {}>
						        						  <#assign sno=sno+1>
												            <#--<#assign itemType = invoiceItem.getRelatedOne("InvoiceItemType")>
												            <#assign isItemAdjustment = Static["org.ofbiz.entity.util.EntityTypeUtil"].hasParentType(delegator, "InvoiceItemType", "invoiceItemTypeId", itemType.getString("invoiceItemTypeId"), "parentTypeId", "INVOICE_ADJ")/>-->
																 	<#assign invoiceItemType = delegator.findOne("InvoiceItemType", {"invoiceItemTypeId" : invoiceItem.invoiceItemTypeId}, false)?if_exists/>
																 	<#if invoiceItem.description?has_content>
														                <#assign description=invoiceItem.description>
														            <#elseif taxRate?has_content & taxRate.get("description",locale)?has_content>
														                <#assign description=taxRate.get("description",locale)>
														            <#elseif invoiceItemType.get("description",locale)?has_content>
														                <#assign description=invoiceItemType.get("description",locale)>
														            </#if>
														            <#assign glAccountId = "">
														   			<#if invoiceItemType.defaultGlAccountId?has_content>
														   				<#assign glAccountId =   invoiceItemType.defaultGlAccountId?if_exists>
														   			<#elseif invoiceItem.get("glAccountId")?has_content>	
														   				<#assign glAccountId =   invoiceItem.get("glAccountId")?if_exists>  
														   			</#if>
																    <#if glAccountId?has_content>
																    	<#assign glAccountDetails = delegator.findOne("GlAccount", {"glAccountId" : glAccountId}, false)?if_exists/>
																    </#if> 
						        						<fo:table-row>
							        						<fo:table-cell bottom="">
						        		                    <fo:table  table-layout="fixed" width="100%" space-before="0.2in">
		        				    								 <fo:table-column column-width="10%"/>
        						   									 <fo:table-column column-width="60%"/>
        						   									 <fo:table-column column-width="55%"/>
        						   									 <fo:table-column column-width="27%"/>	
        						   									 <fo:table-body>
        						   									 <fo:table-row>
        						   									 <fo:table-cell border-style="solid">
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always">&#160;${sno}</fo:block>
						        							  		  </fo:table-cell>
						        							  		  <fo:table-cell border-style="solid">
						        											<fo:block text-align="center" font-size="12pt" white-space-collapse="false">&#160; ${description?if_exists} </fo:block>
						        							  		  </fo:table-cell>
						        							  		  <fo:table-cell border-style="solid">
						        											<fo:block text-align="center" font-size="12pt" white-space-collapse="false">&#160; <#if glAccountDetails?has_content>${glAccountDetails.description?if_exists}(${glAccountId})</#if> </fo:block>
						        							  		  </fo:table-cell>
						        							  		  <fo:table-cell border-style="solid">
						        											<fo:block text-align="right" font-size="12pt" white-space-collapse="false" keep-together="always">&#160; ${invoiceItem.amount?if_exists?string("#0.00")} </fo:block>
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
				        										<fo:block font-size="12pt"  white-space-collapse="false" font-weight="bold" ></fo:block>
				        									</fo:table-cell>
						   									 <fo:table-cell >
				        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
				        							  		 </fo:table-cell>
			        							  		 	<fo:table-cell >
			        											<fo:block text-align="right" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">Total:<@ofbizCurrency amount=invoiceTotal isoCode=invoice.currencyUomId?if_exists/></fo:block>
			        							  		 	</fo:table-cell>
						        						</fo:table-row>
						        						
						        						<fo:table-row>
															<fo:table-cell>
											            		<fo:block>------------------------------------------------------------------------------------------------</fo:block>
											       			</fo:table-cell>
														 </fo:table-row>
						        						<#-- payment details here -->
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
											            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
											       			</fo:table-cell>
				       									</fo:table-row>
						        						 <fo:table-row> 
						        						 	<fo:table-cell>
						        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;PAYMENT ID:${paymentListReport.paymentId?if_exists}</fo:block>
						        						 	</fo:table-cell>
						        						 	<fo:table-cell>
						        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"><#if paymentListReport.paymentMethodTypeId?has_content && (paymentListReport.paymentMethodTypeId == "CHEQUE_PAYIN" || paymentListReport.paymentMethodTypeId == "CHEQUE_PAYOUT")>CHEQUE</#if><#if paymentListReport.paymentMethodTypeId?has_content && (paymentListReport.paymentMethodTypeId == "CASH_PAYIN" || paymentListReport.paymentMethodTypeId == "CASH_PAYOUT")>CASH</#if></fo:block>
						        						 	</fo:table-cell>
						        						 	<fo:table-cell>
						        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">PAYMENT DATE:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(paymentListReport.paymentDate?if_exists, "dd-MM-yyyy")}</fo:block>
						        						 	</fo:table-cell>
						        						 </fo:table-row>
						        						 <#if paymentListReport.paymentMethodTypeId?has_content && (paymentListReport.paymentMethodTypeId == "CHEQUE_PAYIN" || paymentListReport.paymentMethodTypeId == "CHEQUE_PAYOUT")> 
						        							
						        							 <#if paymentListReport.paymentMethodId?has_content>
														    	<#assign paymentMethodDetails = delegator.findOne("PaymentMethod", {"paymentMethodId" : paymentListReport.paymentMethodId}, true)?if_exists/>
														    </#if>
														 <#if (paymentListReport.paymentMethodTypeId == "CHEQUE_PAYIN" || paymentListReport.paymentMethodTypeId == "CHEQUE_PAYOUT")>   
						        						 <fo:table-row> 
						        						 	<fo:table-cell>
						        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;CHEQUE NO:${paymentListReport.paymentRefNum?if_exists}</fo:block>
						        						 	</fo:table-cell>
						        						 	<fo:table-cell>
						        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
						        						 	</fo:table-cell>
						        						 	<fo:table-cell>
						        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">CHEQUE DATE:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(paymentListReport.instrumentDate?if_exists, "dd-MM-yyyy")}</fo:block>
						        						 	</fo:table-cell>
						        						 </fo:table-row>
						        						 <#if (paymentListReport.paymentMethodTypeId == "CHEQUE_PAYIN" || paymentListReport.paymentMethodTypeId == "CHEQUE_PAYOUT")>
						        						 <fo:table-row> 
						        						 	<fo:table-cell>
						        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"><#if paymentMethodDetails?has_content>&#160;CHEQUE BANK DETAILS:${paymentMethodDetails.description?if_exists}</#if></fo:block>
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
		        				    								 <fo:table-column column-width="10%"/>
        						   									 <fo:table-column column-width="60%"/>
        						   									 <fo:table-column column-width="55%"/>
        						   									 <fo:table-column column-width="27%"/>	
        						   									 <fo:table-body>
        						   									 <fo:table-row>
        						   									 <fo:table-cell border-style="solid">
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;S.No</fo:block>
						        							  		  </fo:table-cell>
						        							  		  <fo:table-cell border-style="solid">
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        											<fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; DESCRIPTION </fo:block>
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        							  		  </fo:table-cell>
						        							  		  <fo:table-cell border-style="solid">
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        											<fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; PARTY CODE </fo:block>
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        							  		  </fo:table-cell>
						        							  		  <fo:table-cell border-style="solid">
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        											<fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; AMOUNT Rs. </fo:block>
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        							  		  </fo:table-cell>
        						   									 </fo:table-row>
        						   									 </fo:table-body>
        						   		                    </fo:table>
						        						</fo:table-cell>
						        						</fo:table-row>	
						        						
						        						
						        						<fo:table-row>
							        						<fo:table-cell bottom="">
						        		                    <fo:table  table-layout="fixed" width="100%" space-before="0.2in">
		        				    								 <fo:table-column column-width="10%"/>
        						   									 <fo:table-column column-width="60%"/>
        						   									 <fo:table-column column-width="55%"/>
        						   									 <fo:table-column column-width="27%"/>	
        						   									 <fo:table-body>
        						   									 <fo:table-row>
        						   									 <fo:table-cell border-style="solid">
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always">&#160;${sno}</fo:block>
						        							  		  </fo:table-cell>
						        							  		  <fo:table-cell border-style="solid">
						        											<fo:block text-align="center" font-size="12pt" white-space-collapse="false">&#160; ${partyName?if_exists} </fo:block>
						        							  		  </fo:table-cell>
						        							  		  <fo:table-cell border-style="solid">
						        											<fo:block text-align="center" font-size="12pt" white-space-collapse="false">&#160; ${partyId?if_exists}</fo:block>
						        							  		  </fo:table-cell>
						        							  		  <#assign paymentAmount = paymentListReport.amount?if_exists>
						        							  		  <fo:table-cell border-style="solid">
						        											<fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always">&#160; <@ofbizCurrency amount=paymentAmount isoCode=currencyUomId/> </fo:block>
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
															<fo:table-cell>
											            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
											       			</fo:table-cell>
														</fo:table-row>
						        						<fo:table-row>
															<fo:table-cell>
											            		<fo:block>------------------------------------------------------------------------------------------------</fo:block>
											       			</fo:table-cell>
														 </fo:table-row>
														 <#assign cheqFav = "">
														 <#if (paymentListReport.paymentMethodTypeId == "CHEQUE_PAYIN" || paymentListReport.paymentMethodTypeId == "CHEQUE_PAYOUT")>
															 <#if paymentListReport.paymentId?has_content>
															 <#assign paymentAttrDetails = delegator.findOne("PaymentAttribute", {"paymentId" : paymentListReport.paymentId, "attrName" : "INFAVOUR_OF"}, true)?if_exists/>
															 
															  <#if paymentAttrDetails.attrValue?has_content>
															  	<#assign cheqFav = paymentAttrDetails.attrValue?if_exists>
															  <#else>
															  	<#assign cheqFav = partyName?if_exists>
															 </#if>
															 
															 <fo:table-row>
								        						<fo:table-cell>
								        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
								        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">Cheque in favour of:${cheqFav?if_exists}</fo:block>
								        						</fo:table-cell>
							        						</fo:table-row>
							        						</#if>
						        						</#if>
						        						 </#list>
						        						</#if>
						        						<fo:table-row>
							        						<fo:table-cell>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">PROCD.&#160;&#160;&#160;&#160;&#160;&#160;D.Mgr(Finance)/Mgr(Finance)/GM(Finance)</fo:block>
							        						</fo:table-cell>
							        							<fo:table-cell>
							        							 	<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160;</fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Pre Audit</fo:block>
							        						</fo:table-cell>
							        						<fo:table-cell>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160;</fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="right" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; Director</fo:block>
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
		<fo:block >
				<fo:table  table-layout="fixed" width="50%" space-before="0.2in">
			    <fo:table-column column-width="50%"/>
			    <fo:table-column column-width="50%"/>
			    <fo:table-column column-width="40%"/>
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell>
							<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160;</fo:block>
							<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							<fo:block text-align="right" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160;</fo:block>
							<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							<fo:block text-align="right" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						</fo:table-cell>
						<fo:table-cell border-style = "solid">
							<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160;</fo:block>
							<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							<fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">Signature of Recipient</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block>
	</fo:flow>
</fo:page-sequence>
		      
		        
		    
     </fo:root>
</#escape>
