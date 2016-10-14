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
            <fo:simple-page-master master-name="main" page-width="15in" page-height="12in"
                margin-top="0.1in" margin-bottom="0.5in" margin-left="0.3in" margin-right="0.3in">
        <fo:region-body margin-top="0.2in" margin-bottom="0.2in" border-style="solid" />
        <fo:region-before extent="1.5in"/>
        <fo:region-after extent="1.5in"/>     
            </fo:simple-page-master>
        </fo:layout-master-set>
        <fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">
            <fo:static-content flow-name="xsl-region-before">
              	<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"> &#160;${uiLabelMap.CommonPage}- <fo:page-number/></fo:block>
            </fo:static-content>						
            <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
                    <#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
                    <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
                    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">  ${reportHeader.description?if_exists} </fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">  ${reportSubHeader.description?if_exists}  </fo:block>
          			<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" font-weight="bold"    white-space-collapse="false">INDENT OVERVIEW</fo:block>
          			<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" font-weight="bold"    font-size="15pt"  white-space-collapse="false">${orderId?if_exists}</fo:block>
          			<fo:block  keep-together="always"  text-align="left" font-family="Courier,monospace" font-weight="bold" white-space-collapse="false"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
          	 <fo:block >
          	 	  <fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block> 
          	 	  <fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block>
          	 	  <fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block>  
    			 <fo:table width="100%" align="right" table-layout="fixed"  font-size="12pt">
		               <fo:table-column column-width="50%"/>               
		               <fo:table-column column-width="50%"/>               
			           <fo:table-body>
	                     	<fo:table-row >
	                     		<fo:table-cell border-style="solid" ><fo:block text-align="left" font-size="14pt" background="pink" font-weight="bold">INDENT DETAILS</fo:block></fo:table-cell>       			
	                     		<fo:table-cell  border-style="solid" ><fo:block text-align="left"  font-size="14pt" font-weight="bold" >INDENT STATUS</fo:block></fo:table-cell>       		
                       		</fo:table-row>
                       		<fo:table-row >
	                     		<fo:table-cell>
	                     			<fo:block>
	                     				<fo:table width="100%" align="right" table-layout="fixed"  font-size="12pt">
					                    	<fo:table-column column-width="50%"/>             
					                        <fo:table-column column-width="50%"/>               
						           			<fo:table-body>
				                     			<fo:table-row>
				                     				<fo:table-cell><fo:block text-align="left" font-size="12pt"  font-weight="bold" >  Status History :</fo:block>
				                     				<fo:block text-align="left" font-size="12pt">Current Status :<#if currentStatus.statusCode?has_content>  ${currentStatus.description?if_exists} </#if></fo:block>
													<#list orderHeaderStatuses as orderHeaderStatus>
                   										<#assign loopStatusItem = orderHeaderStatus.getRelatedOne("StatusItem")>
                    									<#assign userlogin = orderHeaderStatus.getRelatedOne("UserLogin")>
                    		                            <fo:block text-align="left" font-size="12pt"> ${loopStatusItem.get("description",locale)} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(orderHeaderStatus.statusDatetime, "dd-MMM-yyyy")} 
                      									${uiLabelMap.CommonBy} -[${orderHeaderStatus.statusUserLogin}]</fo:block>	
                  									</#list>
													</fo:table-cell>       			
				                     				<fo:table-cell><fo:block text-align="left"  font-size="12pt"  ><fo:inline  font-weight="bold" > Indent Date : </fo:inline>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(orderHeader.orderDate, "dd-MMM-yyyy")}</fo:block></fo:table-cell>       		
                                  				</fo:table-row>
												
			                                    <fo:table-row>
								                	<fo:table-cell  ><fo:block text-align="left" font-size="12pt"  ><fo:inline  font-weight="bold" >Location :</fo:inline><#if orderHeader.productStoreId?has_content>  ${orderHeader.productStoreId} <#else> ${uiLabelMap.CommonNA} </#if></fo:block></fo:table-cell>       
							                     	<fo:table-cell  ><fo:block text-align="left"  font-size="12pt"  > <fo:inline  font-weight="bold" >Created By :</fo:inline>  ${orderHeader.createdBy?if_exists}</fo:block></fo:table-cell>       		
		                                        </fo:table-row>
			                	            </fo:table-body>
			                		    </fo:table>	
	                     			</fo:block>
                     			</fo:table-cell>       			
	                     		<fo:table-cell>
	                     			<fo:block>
	                     				<fo:table width="100%" align="right" table-layout="fixed"  font-size="12pt">
					                    	<fo:table-column column-width="33%"/>             
					                        <fo:table-column column-width="33%"/>
					                        <fo:table-column column-width="33%"/>
						           			<fo:table-body>
						           				<fo:table-row>
				                     				<fo:table-cell>
				                     					<fo:block text-align="left" font-size="12pt" font-weight="bold" >  Status ID  </fo:block>
													</fo:table-cell>       			
				                     				<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt" font-weight="bold">  Status Datetime  </fo:block>
			                     					</fo:table-cell>
													<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt" font-weight="bold">  Entry by </fo:block>
			                     					</fo:table-cell>	
                                  				</fo:table-row>
						           			    <#list statusHistory as status>
				                     			<fo:table-row>
				                     				<fo:table-cell>
				                     					<fo:block text-align="left" font-size="12pt">${status.statusId?if_exists}</fo:block>
													</fo:table-cell>       			
				                     				<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt">${status.statusDatetime?if_exists}</fo:block>
				                     				</fo:table-cell>
													<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt">${status.userLogin?if_exists}</fo:block>
				                     				</fo:table-cell>
                                  				</fo:table-row>
												</#list>
			                                   
			                	            </fo:table-body>
			                		    </fo:table>	
	                     			</fo:block> 
                     			</fo:table-cell>       		
                       		</fo:table-row>
                       		<fo:table-row >
	                     		<fo:table-cell ><fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block><fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block></fo:table-cell>       			
	                     		<fo:table-cell  ><fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block><fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block></fo:table-cell>       		
                       		</fo:table-row>
                       		<fo:table-row>
                       			<fo:table-cell number-columns-spanned="2" border-style="solid" ><fo:block text-align="center" font-size="14pt" font-weight="bold" >INDENT ITEMS</fo:block></fo:table-cell>       			
                       		</fo:table-row>
                       		
                       		<fo:table-row >
	                     		<fo:table-cell number-columns-spanned="2">
	                     			<fo:block>
	                     				<fo:table width="100%" align="right" table-layout="fixed"  font-size="12pt">
					                    	<fo:table-column column-width="50%"/>             
					                        <fo:table-column column-width="50%"/>               
						           			<fo:table-body>
				                     			<fo:table-row>
				                     				<fo:table-cell number-columns-spanned="2">
				                     					<fo:block>
				                     						<fo:table width="100%" align="right" table-layout="fixed"  font-size="12pt">
				                     							<fo:table-column column-width="20%"/>   
				                     							<fo:table-column column-width="20%"/>   
				                     							<fo:table-column column-width="7.5%"/>   
				                     							<fo:table-column column-width="7.5%"/>   
				                     							<fo:table-column column-width="7.5%"/>   
				                     							<fo:table-column column-width="15%"/>   
				                     							<fo:table-column column-width="7.5%"/>   
				                     							<fo:table-column column-width="7.5%"/>   
				                     							<fo:table-column column-width="7.5%"/>   
				                     							<fo:table-body>
				                     							<fo:table-row>

				                     								    <fo:table-cell>
			                     											<fo:block text-align="center"  font-size="12pt" font-weight="bold" >PName</fo:block>	 
				                     								    </fo:table-cell> 
				                     								    <fo:table-cell>
			                     											<fo:block text-align="center"  font-size="12pt" font-weight="bold" >Prod Spec</fo:block>	 
				                     								    </fo:table-cell> 
				                     								    <fo:table-cell>
			                     											<fo:block text-align="center"  font-size="12pt" font-weight="bold" >Status</fo:block>	 
				                     								    </fo:table-cell> 
				                     								    <fo:table-cell>
			                     											<fo:block text-align="center"  font-size="12pt" font-weight="bold" >Qty</fo:block>	 
				                     								    </fo:table-cell> 
				                     								    <fo:table-cell>
			                     											<fo:block text-align="center"  font-size="12pt" font-weight="bold" >Price</fo:block>	 
				                     								    </fo:table-cell> 
				                     								    <fo:table-cell>
			                     											<fo:block text-align="center"  font-size="12pt" font-weight="bold" >Sub Total</fo:block>	 
				                     								    </fo:table-cell> 
				                     								    <fo:table-cell>
			                     											<fo:block text-align="center" font-size="12pt" font-weight="bold" >Ed</fo:block>	 
				                     								    </fo:table-cell> 
				                     								    <fo:table-cell>
			                     											<fo:block text-align="center"  font-size="12pt" font-weight="bold" >Vat</fo:block>	 
				                     								    </fo:table-cell> 
				                     								    <fo:table-cell>
			                     											<fo:block text-align="center"  font-size="12pt" font-weight="bold" >CST</fo:block>	 
				                     								    </fo:table-cell> 
				                     								</fo:table-row>
				                     							<#list orderItemList as orderItem>
																<#assign orderItemContentWrapper = Static["org.ofbiz.order.order.OrderContentWrapper"].makeOrderContentWrapper(orderItem, request)>
										                        <#assign orderItemShipGrpInvResList = orderReadHelper.getOrderItemShipGrpInvResList(orderItem)>
										                        <#if orderHeader.orderTypeId == "SALES_ORDER"><#assign pickedQty = orderReadHelper.getItemPickedQuantityBd(orderItem)></#if>
										                        <#assign orderItemType = orderItem.getRelatedOne("OrderItemType")?if_exists>
										                        <#assign productId = orderItem.productId?if_exists>
										                        <#assign productDetails = delegator.findOne("Product", {"productId" : productId}, true)>
	
				                     								<fo:table-row>
				                     								    <fo:table-cell>
			                     											<fo:block text-align="left"  fofnt-size="12pt"  >${orderItem.itemDescription?if_exists}</fo:block>	 
				                     								    </fo:table-cell> 
				                     								    <fo:table-cell>
			                     											<fo:block text-align="left"  font-size="12pt"><#if productDetails?has_content>  ${productDetails.longDescription?if_exists}</#if> </fo:block>	 
				                     								    </fo:table-cell> 
				                     								    <fo:table-cell>
																			<#assign statusDesc = delegator.findOne("StatusItem", {"statusId" : orderItem.statusId}, true) />
			                     											<fo:block text-align="left"  font-size="12pt" font-weight="bold" > ${statusDesc.description?if_exists}</fo:block>	 
				                     								    </fo:table-cell> 
				                     								    <fo:table-cell>
			                     											<fo:block text-align="center"  font-size="12pt"  > ${orderItem.quantity?if_exists}</fo:block>	 
				                     								    </fo:table-cell> 
				                     								    <fo:table-cell>
			                     											<fo:block text-align="center"  font-size="12pt"  ><@ofbizCurrency amount=Static["java.lang.Math"].round(orderItem.unitPrice) isoCode=currencyUomId/></fo:block>	 
				                     								    </fo:table-cell> 
				                     								    <#assign exciseAmount=0>
										                                <#if orderItem.bedAmount?exists > <#assign exciseAmount=exciseAmount+orderItem.bedAmount >  </#if>
										                                <#if orderItem.bedcessAmount?exists> <#assign exciseAmount=exciseAmount+orderItem.bedcessAmount>  </#if>
										                                <#if orderItem.bedseccessAmount?exists> <#assign exciseAmount=exciseAmount+orderItem.bedseccessAmount>  </#if>
																	    <#assign  itemSubTotal=Static["org.ofbiz.order.order.OrderReadHelper"].getPurchaseOrderItemTotal(orderItem,false)-exciseAmount>
                                         								<#assign  orderExTaxTotal=orderExTaxTotal.add(itemSubTotal)>
                                        										
				                     								    <fo:table-cell>
			                     											<fo:block text-align="center"  font-size="12pt"  > <@ofbizCurrency amount=Static["java.lang.Math"].round(itemSubTotal) isoCode=currencyUomId/> </fo:block>	 
				                     								    </fo:table-cell> 
				                     								    <fo:table-cell>
			                     											<fo:block text-align="center"  font-size="12pt" > <@ofbizCurrency amount=exciseAmount isoCode=currencyUomId/> </fo:block>	 
				                     								    </fo:table-cell> 
				                     								    <fo:table-cell>
				                     								    	<#assign orderItemAdjs = delegator.findOne("OrderItem", {"orderId" : orderItem.orderId,"orderItemSeqId": orderItem.orderItemSeqId}, true) />
			                     											<fo:block text-align="center"  font-size="12pt" ><@ofbizCurrency amount=orderItemAdjs.vatAmount isoCode=currencyUomId/>  </fo:block>	 
				                     								    </fo:table-cell>left
																		<fo:table-cell>
			                     											<fo:block text-align="center"   font-size="12pt" ><@ofbizCurrency amount=orderItemAdjs.cstAmount isoCode=currencyUomId/></fo:block>	 
				                     								    </fo:table-cell>  
				                     								</fo:table-row>
			                     								</#list>
				                     							</fo:table-body>
				                     						</fo:table>	
				                     					</fo:block>
				                     				</fo:table-cell> 
                                  				</fo:table-row>
												
			                	            </fo:table-body>
			                		    </fo:table>	
	                     			</fo:block>
                     			</fo:table-cell>       			
                       		</fo:table-row>

                       		<fo:table-row >
	                     		<fo:table-cell ><fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block><fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block></fo:table-cell>       			
	                     		<fo:table-cell  ><fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block><fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block></fo:table-cell>       		
                       		</fo:table-row>
                      		<fo:table-row>
					  			<fo:table-cell border-style="solid"><fo:block text-align="left" font-size="14pt" font-weight="bold">INDENT ATTRIBUTES</fo:block></fo:table-cell>
	                     		<fo:table-cell border-style="solid" ><fo:block text-align="left" font-size="14pt" font-weight="bold" >INDENT ASSOCIATIONS</fo:block> </fo:table-cell>       		
                      		</fo:table-row>
                      		
							<fo:table-row >
	                     		<fo:table-cell>
	                     			<fo:block>
	                     				<fo:table width="100%" align="right" table-layout="fixed"  font-size="12pt">
					                    	<fo:table-column column-width="50%"/>             
					                        <fo:table-column column-width="50%"/>
						           			<fo:table-body>
						           				<fo:table-row>
				                     				<fo:table-cell>
				                     					<fo:block text-align="left" font-size="12pt" font-weight="bold" > Attribute Name </fo:block>
													</fo:table-cell>       			
				                     				<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt" font-weight="bold">Value / Date </fo:block>
			                     					</fo:table-cell>
                                  				</fo:table-row>
						           			    <#list OrderAttributeList as OrderAttribute>
				                     			<fo:table-row>
				                     				<fo:table-cell>
				                     					<fo:block text-align="left" font-size="12pt"> ${OrderAttribute.attrName?if_exists}</fo:block>
													</fo:table-cell>       			
				                     				<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt">${OrderAttribute.attrValue?if_exists}</fo:block>
				                     				</fo:table-cell>
                                  				</fo:table-row>
												</#list>
			                                   
			                	            </fo:table-body>
			                		    </fo:table>	
	                     			</fo:block>
                     			</fo:table-cell>       			
	                     		<fo:table-cell>
	                     			<fo:block>
	                     				<fo:table width="100%" align="right" table-layout="fixed"  font-size="12pt">
					                    	<fo:table-column column-width="33%"/>             
					                        <fo:table-column column-width="33%"/>
					                        <fo:table-column column-width="33%"/>
						           			<fo:table-body>
						           				<fo:table-row>
				                     				<fo:table-cell>
				                     					<fo:block text-align="left" font-size="12pt" font-weight="bold" > OrderId </fo:block>
													</fo:table-cell>       			
				                     				<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt" font-weight="bold"> Associated Orders  </fo:block>
			                     					</fo:table-cell>
													<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt" font-weight="bold">  Order Type  </fo:block>
			                     					</fo:table-cell>	
                                  				</fo:table-row>
						           			    <#list OrderAssocList as OrderAssoc>
				                     			<fo:table-row>
				                     				<fo:table-cell>
				                     					<fo:block text-align="left" font-size="12pt"> ${OrderAssoc.orderId?if_exists}</fo:block>
													</fo:table-cell>       			
				                     				<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt">${OrderAssoc.toOrderId?if_exists}</fo:block>
				                     				</fo:table-cell>
													<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt">${OrderAssoc.orderAssocTypeId?if_exists}</fo:block>
				                     				</fo:table-cell>
                                  				</fo:table-row>
												</#list>
			                                    
			                	            </fo:table-body>
			                		    </fo:table>	
	                     			</fo:block>
                     			</fo:table-cell>       		
                       		</fo:table-row>
                      		<fo:table-row >
	                     		<fo:table-cell ><fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block><fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block></fo:table-cell>       			
	                     		<fo:table-cell  ><fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block><fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block></fo:table-cell>       		
                       		</fo:table-row>
                      		<fo:table-row>
					  			<fo:table-cell border-style="solid"><fo:block text-align="left" font-size="14pt" font-weight="bold">INVOICES FOR INDENT</fo:block></fo:table-cell>
	                     		<fo:table-cell border-style="solid" ><fo:block text-align="left" font-size="14pt"  font-weight="bold">PAYMENT INFORMATION</fo:block> </fo:table-cell>       		
                      		</fo:table-row>
                      		
                      		<fo:table-row >
	                     		<fo:table-cell>
	                     			<fo:block>
	                     				<fo:table width="100%" align="right" table-layout="fixed"  font-size="12pt">
					                    	<fo:table-column column-width="20%"/>             
					                        <fo:table-column column-width="20%"/>
					                        <fo:table-column column-width="20%"/>
					                        <fo:table-column column-width="20%"/>
					                        <fo:table-column column-width="20%"/>
					                        
						           			<fo:table-body>
						           				<fo:table-row>
				                     				<fo:table-cell>
				                     					<fo:block text-align="left" font-size="12pt" font-weight="bold" >  Invoice ID </fo:block>
													</fo:table-cell>       			
				                     				<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt" font-weight="bold">Status</fo:block>
			                     					</fo:table-cell>
			                     					<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt" font-weight="bold">invoiceType</fo:block>
			                     					</fo:table-cell>
													<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt" font-weight="bold">Date </fo:block>
			                     					</fo:table-cell>
			                     					<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt" font-weight="bold"> Entry by </fo:block>
			                     					</fo:table-cell>
			                     					
                                  				</fo:table-row>
						           			    <#list invoiceDetailList as invoiceDetail>
				                     			<fo:table-row>
				                     				<fo:table-cell>
				                     					<fo:block text-align="left" font-size="12pt"> ${invoiceDetail.invoiceId?if_exists}</fo:block>
													</fo:table-cell>       			
				                     				<fo:table-cell>
				                     					<#assign status = delegator.findOne("StatusItem", {"statusId" :invoiceDetail.statusId}, true)>
				                     					<fo:block text-align="left"  font-size="12pt">${status.description?if_exists}</fo:block>
				                     				</fo:table-cell>
													<fo:table-cell>
														<#assign invoiceType = delegator.findOne("InvoiceType", {"invoiceTypeId" :invoiceDetail.invoiceTypeId} , true)>
				                     					<fo:block text-align="left"  font-size="12pt">${invoiceType.description?if_exists}</fo:block>
				                     				</fo:table-cell>
				                     				<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt">${invoiceDetail.invoiceDate?if_exists}</fo:block>
				                     				</fo:table-cell>
				                     				<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt">${invoiceDetail.createdByUserLogin?if_exists}</fo:block>
				                     				</fo:table-cell>
                                  				</fo:table-row>
												</#list>
			                                    
			                	            </fo:table-body>
			                		    </fo:table>	
	                     			</fo:block>
                     			</fo:table-cell>       			
	                     		<fo:table-cell>
	                     			<fo:block>
	                     				<fo:table width="100%" align="right" table-layout="fixed"  font-size="12pt">
					                    	<fo:table-column column-12ptwidth="16.6%"/>             
					                       <fo:table-column column-width="16.6%"/> 
					                        <fo:table-column column-width="16.6%"/> 
					                        <fo:table-column column-width="16.6%"/> 
					                        <fo:table-column column-width="16.6%"/> 
					                        <fo:table-column column-width="16.6%"/> 
						           			<fo:table-body>
						           				<fo:table-row>
				                     				<fo:table-cell>
				                     					<fo:block text-align="left" font-size="12pt" font-weight="bold" > PaymentID </fo:block>
													</fo:table-cell>       			
				                     				<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt" font-weight="bold"> Amount  </fo:block>
			                     					</fo:table-cell>
													<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt" font-weight="bold">  Payment Date  </fo:block>
			                     					</fo:table-cell>
			                     					<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt" font-weight="bold"> Pament Method Type </fo:block>
			                     					</fo:table-cell>
			                     					<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt" font-weight="bold"> Status </fo:block>
			                     					</fo:table-cell>
			                     					<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt" font-weight="bold"> Entry by </fo:block>
			                     					</fo:table-cell>	
                                  				</fo:table-row>
						           			    <#list PaymentList as payment>
				                     			<fo:table-row>
				                     				<fo:table-cell>
				                     					<fo:block text-align="left" font-size="12pt"> ${payment.paymentId?if_exists}</fo:block>
													</fo:table-cell> 
													<fo:table-cell>
				                     					<fo:block te12ptxt-align="left"  font-size="12pt">${payment.amount?if_exists?string("##0.00")}</fo:block>
				                     				</fo:table-cell>      			
				                     				<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(payment.paymentDate, "dd-MMM-yyyy")}</fo:block>
				                     				</fo:table-cell>
													<fo:table-cell>
														<#assign paymentMethodType = delegator.findOne("PaymentMethodType", {"paymentMethodTypeId" : payment.paymentMethodTypeId}, true)>
				                     					<fo:block text-align="left"  font-size="12pt">${paymentMethodType.description?if_exists}</fo:block>
				                     				</fo:table-cell>
				                     				<fo:table-cell>
														<#assign status = delegator.findOne("StatusItem", {"statusId" : payment.statusId}, true)>
				                     					<fo:block text-align="left"  font-size="12pt">${status.description?if_exists}</fo:block>
				                     				</fo:table-cell>
				                     				
				                     				<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt">${payment.createdByUserLogin?if_exists}</fo:block>
				                     				</fo:table-cell>
                                  				</fo:table-row>
												</#list>
			                                    
			                	            </fo:table-body>
			                		    </fo:table>	
	                     			</fo:block>
                     			</fo:table-cell>       		
                       		</fo:table-row>
                      		
                      		<fo:table-row >
	                     		<fo:table-cell ><fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block><fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block></fo:table-cell>       			
	                     		<fo:table-cell  ><fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block><fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block></fo:table-cell>       		
                       		</fo:table-row>
                       		
							<fo:table-row>
					  			<fo:table-cell number-columns-spanned="2" border-style="solid"><fo:block text-align="center" font-size="14pt" font-weight="bold">RECEIPTS OF INDENT</fo:block></fo:table-cell>
                      		</fo:table-row>
                      		<fo:table-row>
					  			<fo:table-cell number-columns-spanned="2" border-style="solid">
					  				<fo:block>
	                     				<fo:table width="100%" align="right" table-layout="fixed"  font-size="12pt">
					                    	<fo:table-column column-width="11.1%"/>             
					                       <fo:table-column column-width="11.1%"/> 
					                        <fo:table-column column-width="11.1%"/> 
					                        <fo:table-column column-width="11.1%"/> 
					                        <fo:table-column column-width="11.1%"/> 
					                        <fo:table-column column-width="11.1%"/>
											<fo:table-column column-width="11.1%"/>
											<fo:table-column column-width="11.1%"/>
											<fo:table-column column-width="11.1%"/>	 	 
						           			<fo:table-body>
						           				<fo:table-row>
				                     				<fo:table-cell>
				                     					<fo:block text-align="left" font-size="12pt" font-weight="bold" >  GRN Item No  </fo:block>
													</fo:table-cell>       			
				                     				<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt" font-weight="bold">  Code- Name [UOM]   </fo:block>
			                     					</fo:table-cell>
													<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt" font-weight="bold">   Received Date   </fo:block>
			                     					</fo:table-cell>
			                     					<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt" font-weight="bold">  GRN ShipmentId  </fo:block>
			                     					</fo:table-cell>
			                     					<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt" font-weight="bold">  PO No  </fo:block>
			                     					</fo:table-cell>
			                     					<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt" font-weight="bold"> Status </fo:block>
			                     					</fo:table-cell>
			                     					<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt" font-weight="bold"> Quantity </fo:block>
			                     					</fo:table-cell>
			                     					<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt" font-weight="bold">  Qty Rejected  </fo:block>
			                     					</fo:table-cell>
													<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt" font-weight="bold"> Entry by </fo:block>
			                     					</fo:table-cell>	
                                  				</fo:table-row>
						           			    <#list shipmentReceipts as shipmentReceipt>
				                     			<fo:table-row>
				                     				<fo:table-cell>
				                     					<fo:block text-align="left" font-size="12pt"> ${shipmentReceipt.receiptId?if_exists}</fo:block>
													</fo:table-cell> 
													<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt">${shipmentReceipt.productId?if_exists}</fo:block>
				                     				</fo:table-cell>      			
				                     				<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(shipmentReceipt.datetimeReceived, "dd-MMM-yyyy")}</fo:block>
				                     				</fo:table-cell>
													<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt">${shipmentReceipt.shipmentId?if_exists}</fo:block>
				                     				</fo:table-cell>
				                     				<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt">${shipmentReceipt.orderId?if_exists}</fo:block>
				                     				</fo:table-cell>
				                     				
				                     				<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt">${shipmentReceipt.statusId?if_exists}</fo:block>
				                     				</fo:table-cell>
													<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt">${shipmentReceipt.quantityAccepted?if_exists}</fo:block>
				                     				</fo:table-cell>
				                     				<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt">${shipmentReceipt.quantityRejected?if_exists}</fo:block>
				                     				</fo:table-cell>
													<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt">${shipmentReceipt.receivedByUserLoginId?if_exists}</fo:block>
				                     				</fo:table-cell>
                                  				</fo:table-row>
												</#list>12pt
			                                    
			                	            </fo:table-body>
			                		    </fo:table>	
	                     			</fo:block>
					  			
					  			</fo:table-cell>
                      		</fo:table-row>
                      		<fo:table-row >
	                     		<fo:table-cell ><fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block><fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block></fo:table-cell>       			
	                     		<fo:table-cell  ><fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block><fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block></fo:table-cell>       		
                       		</fo:table-row>
						<#-- 	<fo:table-row>
					  			<fo:table-cell border-style="solid"><fo:block text-align="left"  font-size="14pt"  font-weight="bold">CONTACT INFORMATION</fo:block></fo:table-cell>
	                     		<fo:table-cell border-style="solid" ><fo:block text-align="left" font-size="14pt"  font-weight="bold">SUPPLY PRODUCTS TILL DATE</fo:block></fo:table-cell>       		
                      		</fo:table-row>
							
							<fo:table-row >
	                     		<fo:table-cell>
	                     			<fo:block>
	                     				
	                     			</fo:block>
                     			</fo:table-cell>       			
	                     		<fo:table-cell>
	                     			<fo:block>
	                     				<fo:table width="100%" align="right" table-layout="fixed"  font-size="12pt">
					                    	<fo:table-column column-width="25%"/>             
					                       <fo:table-column column-width="25%"/> 
					                        <fo:table-column column-width="25%"/> 
					                        <fo:table-column column-width="25%"/> 
						           			<fo:table-body>
						           				<fo:table-row>
				                     				<fo:table-cell>
				                     					<fo:block text-align="left" font-size="12pt" font-weight="bold" >  Code- Name [UOM]  </fo:block>
													</fo:table-cell>       			
				                     				<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt" font-weight="bold"> Supplier  </fo:block>
			                     					</fo:table-cell>
													<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt" font-weight="bold">  Supply Till Date  </fo:block>
			                     					</fo:table-cell>
			                     					<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt" font-weight="bold">  Entry by  </fo:block>
			                     					</fo:table-cell>
			                     						
                                  				</fo:table-row>
						           			    <#list listIt as supply>
				                     			<fo:table-row>
				                     				<fo:table-cell>
				                     					<fo:block text-align="left" font-size="12pt"> ${supply.productId?if_exists}</fo:block>
													</fo:table-cell> 
													<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt">${supply.productId?if_exists}</fo:block>
				                     				</fo:table-cell>      			
				                     				<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt">${supply.productId?if_exists} </fo:block>
				                     				</fo:table-cell>
													<fo:table-cell>
				                     					<fo:block text-align="left"  font-size="12pt">${supply.receivedByUserLoginId?if_exists}</fo:block>
				                     				</fo:table-cell>
				                     				
                                  				</fo:table-row>
												</#list>
			                                    
			                	            </fo:table-body>
			                		    </fo:table>	
	                     			</fo:block>
                     			</fo:table-cell>       		
                       		</fo:table-row>  -->
							
                	</fo:table-body>
        		 </fo:table>
			  </fo:block>	
            </fo:flow>
        </fo:page-sequence>
    </fo:root>
</#escape>  
  