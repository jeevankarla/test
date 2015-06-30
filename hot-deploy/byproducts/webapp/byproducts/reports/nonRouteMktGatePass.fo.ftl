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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-right=".3in" margin-top=".5in">
                <fo:region-body margin-top="1.3in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "deliveryChallan.pdf")}
        <#if orderDetailMap?has_content>
        <#assign ordersList = orderDetailMap.entrySet()> 
        <#list  ordersList as eachOrder>
        <#assign orderId = eachOrder.getKey()>
        <#assign orderDetail = eachOrder.getValue()>
        <#assign orderHeader = orderDetail.get('orderHeader')>
        
        <#assign shipment = {}>
        <#if orderDetail.get('shipment')?has_content>
        	<#assign shipment = orderDetail.get('shipment')>
        </#if>
        <#assign orderItems = orderDetail.get('orderItems')>
        <#assign billingAddress = orderDetail.get('partyAddress')>
        <#assign partyName = orderDetail.get('partyName')>
        <#assign invoice = {}>
        <#if orderDetail.get('invoice')?has_content>
        	<#assign invoice = orderDetail.get('invoice')>
        </#if>
        <#assign partyCode = orderDetail.get('partyCode')>
        <#assign reportFlag = orderDetail.get('reportFlag')>
        <#assign screenFlag = orderDetail.get('screenFlag')>
        <#assign companyDetail = orderDetail.get('companyDetail')>
        <fo:page-sequence master-reference="main" font-size="12pt">	
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        		<fo:block text-align="left" font-size="13pt" keep-together="always"  white-space-collapse="false">
        			<fo:table>
			            <fo:table-column column-width="150pt"/>
			            <fo:table-column column-width="150pt"/>
			            <fo:table-column column-width="150pt"/>
			            <fo:table-column column-width="150pt"/> 
			            <fo:table-body>
			                <fo:table-row>
			                    <fo:table-cell number-columns-spanned="4">
			                    	<fo:block  keep-together="always"  text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size = "10pt">&#160;
			                    	<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "DEPOT_SALES","propertyName" : "reportHeaderLable"}, true)>
			                    	<#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "DEPOT_SALES","propertyName" : "reportSubHeaderLable"}, true)>                                                                                                   UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
					            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">${reportHeader.description?if_exists}</fo:block>
					            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">${reportSubHeader.description?if_exists}</fo:block>
					            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
					            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold"><#if reportFlag?has_content && reportFlag == "gatePass">GATE PASS<#else>DELIVERY CHALLAN</#if></fo:block>
					            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">TIN : ${companyDetail.get('TIN_NUMBER')?if_exists}</fo:block>
					            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">Date:  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy HH:mm:ss")?if_exists}                CST : ${companyDetail.get('CST_NUMBER')?if_exists}</fo:block>
					            </fo:table-cell>
							</fo:table-row>
			            </fo:table-body>
			        </fo:table>
        		</fo:block>
        	</fo:static-content>	      
        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
        	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
        		<fo:block>
             		<fo:table border-style="solid">
			            <fo:table-column column-width="150pt"/>
			            <fo:table-column column-width="150pt"/>
			            <fo:table-column column-width="150pt"/>
			            <fo:table-column column-width="150pt"/>
			            <fo:table-body>
			                <fo:table-row>
			                    <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">CODE : </fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${partyCode?if_exists}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">GP NO : </fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>  
					            </fo:table-cell>
							</fo:table-row>
							<fo:table-row>
			                    <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">NAME : </fo:block>
					            </fo:table-cell>
					            <fo:table-cell>	
					            	<fo:block text-align="left" font-size="12pt" white-space-collapse="false" wrap-option="wrap">${partyName.get('groupName')?if_exists}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">GP DATE : </fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"><#if shipment?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(shipment.estimatedShipDate, "dd-MMM-yyyy")}</#if></fo:block>  
					            </fo:table-cell>
							</fo:table-row>
							<fo:table-row>
			                    <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">ADDRESS : </fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block text-align="left" font-size="12pt" white-space-collapse="false" wrap-option="wrap">${billingAddress.get('address1')?if_exists}</fo:block>
					            	<fo:block text-align="left" font-size="12pt" white-space-collapse="false" wrap-option="wrap">${billingAddress.get('address2')?if_exists}</fo:block>
					            	<fo:block text-align="left" font-size="12pt" white-space-collapse="false" wrap-option="wrap">${billingAddress.get('city')?if_exists},${billingAddress.get('countryGeoId')?if_exists}, ${billingAddress.get('postalCode')?if_exists}</fo:block>
								</fo:table-cell>
								<fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">DC No : </fo:block>
					            </fo:table-cell>
					            <fo:table-cell>	
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"><#if shipment?has_content>${shipment.shipmentId?if_exists}</#if></fo:block>  
					            </fo:table-cell>
							</fo:table-row>
							<fo:table-row>
			                    <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">REF NO : </fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"> <#if invoice?has_content>${invoice.invoiceId?if_exists}<#else>NIL</#if></fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">DC DATE : </fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(orderHeader.estimatedDeliveryDate, "dd-MMM-yyyy")}</fo:block>  
					            </fo:table-cell>
							</fo:table-row>
							<fo:table-row>
			                    <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">VEHICLE NO : </fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"><#if shipment?has_content>${shipment.vehicleId?if_exists}</#if></fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">REF DATE : </fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"><#if invoice?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invoice.dueDate, "dd-MMM-yyyy")}<#else></#if></fo:block>  
					            </fo:table-cell>
							</fo:table-row>
							<fo:table-row>
			                    <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">CARRIER NO : </fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"><#if shipment?has_content>${shipment.carrierName?if_exists}</#if></fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">L/R NO : </fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"><#if shipment?has_content>${shipment.lrNumber?if_exists}</#if></fo:block>  
					            </fo:table-cell>
							</fo:table-row>
							<fo:table-row>
			                    <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"> </fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">PO NO : </fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"><#if orderHeader?has_content>${orderHeader.externalId?if_exists}</#if></fo:block>  
					            </fo:table-cell>
							</fo:table-row>
							<fo:table-row>
			                    <fo:table-cell>
					            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            </fo:table-cell>
							</fo:table-row>
							<fo:table-row border-style="solid" >
			                    <fo:table-cell number-columns-spanned="4">
					            	<fo:block text-align="center" font-size="12pt" white-space-collapse="false">
					            		<fo:table border-style="solid">
								            <fo:table-column column-width="40pt"/>
								            <fo:table-column column-width="200pt"/>
								            <fo:table-column column-width="90pt"/>
								            
								            <#if screenFlag?exists && (screenFlag != 'fgsSales' && screenFlag != 'InterUnitTransferSale')>
								            	<fo:table-column column-width="60pt"/>
								            	<fo:table-column column-width="60pt"/>
								            	<fo:table-column column-width="70pt"/>
								            	<fo:table-column column-width="80pt"/>
								            </#if>
								            <#if screenFlag?exists && (screenFlag == 'fgsSales' || screenFlag == 'InterUnitTransferSale')>
								            	<fo:table-column column-width="60pt"/>
								            	<fo:table-column column-width="60pt"/>
								            	<fo:table-column column-width="70pt"/>
								            	<fo:table-column column-width="80pt"/>
								            </#if>
								            
								            <fo:table-body>
								                <fo:table-row>
								                    <fo:table-cell border-style="solid">
										            	<fo:block text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">SL</fo:block>
										            	<fo:block text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">NO</fo:block>
										            </fo:table-cell>
													<fo:table-cell border-style="solid">
										            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">PRODUCT</fo:block>
										            </fo:table-cell>
										            <fo:table-cell border-style="solid">
										            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">BATCH NO</fo:block>  
										            </fo:table-cell>
										            <#if screenFlag?exists && (screenFlag != 'fgsSales' && screenFlag != 'InterUnitTransferSale')>
								            			<fo:table-cell border-style="solid" >
										            		<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">NO. OF</fo:block>
										            		<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">CRATES</fo:block>  
										            	</fo:table-cell>
										            	<fo:table-cell border-style="solid">
										            		<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">QTY/CRT</fo:block>  
										            	</fo:table-cell>
										            	<fo:table-cell border-style="solid">
										            		<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">TOTAL QTY</fo:block>
										            		<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">(LTR/KG)</fo:block>  
										            	</fo:table-cell>
										            	<fo:table-cell border-style="solid">
										            		<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">REMARKS</fo:block>  
										            	</fo:table-cell>
										            <#else>
										            	<fo:table-cell border-style="solid">
										            		<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">RATE</fo:block>  
										            		<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">(Per Pck)</fo:block>
										            	</fo:table-cell>
										            	<fo:table-cell border-style="solid">
										            		<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">QTY</fo:block>
										            		<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold"> IN PCKS</fo:block>  
										            	</fo:table-cell>
										            	<fo:table-cell border-style="solid">
										            		<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">QTY</fo:block>
										            		<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">(LTR/KG)</fo:block>  
										            	</fo:table-cell>
										            	<fo:table-cell border-style="solid">
										            		<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">AMOUNT</fo:block>
										            	</fo:table-cell>
								            		</#if>
												</fo:table-row>
												<#assign totalCrates = 0>
												<#assign totalLtrs = 0>
												<#assign totalAmount = 0>
												<#assign slNo = 1>
													
												<#if orderItems?has_content>
													<#list orderItems as eachItem>
														<fo:table-row>
														<#if screenFlag?exists && (screenFlag != 'fgsSales' && screenFlag != 'InterUnitTransferSale')>
										                    <fo:table-cell border-style="solid">
												            	<fo:block  keep-together="always" text-align="center" font-size="14pt" white-space-collapse="false">${slNo?if_exists} </fo:block>
												            </fo:table-cell>
												            <fo:table-cell border-style="solid" >	
												            	<fo:block  keep-together="always" text-align="left" font-size="14pt" white-space-collapse="false">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(eachItem.get('description')?if_exists)),28)}</fo:block>
												            </fo:table-cell>
												            <fo:table-cell border-style="solid" >
												            	<fo:block  keep-together="always" text-align="left" font-size="14pt" white-space-collapse="false">${eachItem.get('batchNo')?if_exists}</fo:block>
												            </fo:table-cell>
												            <#else>
                                                             <fo:table-cell >
												            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">${slNo?if_exists} </fo:block>
												            </fo:table-cell>
												            <fo:table-cell  >	
												            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(eachItem.get('description')?if_exists)),28)}</fo:block>
												            </fo:table-cell>
												            <fo:table-cell  >
												            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${eachItem.get('batchNo')?if_exists}</fo:block>
												            </fo:table-cell>  
                                                            </#if>
												            <#if screenFlag?exists && (screenFlag != 'fgsSales' && screenFlag != 'InterUnitTransferSale')>
												            	<fo:table-cell border-style="solid" hight="10pt">
												            		<fo:block  keep-together="always" text-align="right" font-size="14pt" white-space-collapse="false">${eachItem.get('qtyInCrate')?if_exists?string("#0.00")}</fo:block>
												           		</fo:table-cell>
												           		<fo:table-cell border-style="solid">
												            		<fo:block  keep-together="always" text-align="center" font-size="14pt" white-space-collapse="false">${eachItem.get('qtyPerCrate')?if_exists}</fo:block>  
												            	</fo:table-cell>
												           		<fo:table-cell border-style="solid">	
												            		<fo:block  keep-together="always" text-align="right" font-size="14pt" white-space-collapse="false">${eachItem.get('qtyLtr')?if_exists?string("#0.00")}</fo:block>
												            	</fo:table-cell>
												            	<fo:table-cell border-style="solid">
												            		<fo:block  keep-together="always" text-align="center" font-size="14pt" white-space-collapse="false"></fo:block>  
												            	</fo:table-cell>
												            <#else>
												           		 <fo:table-cell>
												            		<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${eachItem.get('unitPrice')?if_exists?string("#0.00")}</fo:block>
												           		</fo:table-cell>
												           		<fo:table-cell>
												            		<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">${eachItem.get('qty')?if_exists}</fo:block>  
												            	</fo:table-cell>
												           		<fo:table-cell>	
												            		<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${eachItem.get('qtyLtr')?if_exists?string("#0.00")}</fo:block>
												            	</fo:table-cell>
												            	<fo:table-cell>
												            		<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${eachItem.get('totalAmt')?if_exists?string("#0.00")}</fo:block>  
												            	</fo:table-cell>
												            	<#assign totalAmount = totalAmount+eachItem.get('totalAmt')>
												            </#if>
														</fo:table-row>
														<#assign totalCrates = totalCrates+eachItem.get('qtyInCrate')>
														<#assign totalLtrs = totalLtrs+eachItem.get('qtyLtr')>
														<#assign slNo = slNo+1>
													</#list>
													</#if>
													<#if screenFlag?exists && (screenFlag != 'fgsSales' && screenFlag != 'InterUnitTransferSale')>
														<fo:table-row border-style="solid">
										                    <fo:table-cell>
												            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false"></fo:block>
												            </fo:table-cell>
												            <fo:table-cell>	
												            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">TOTAL</fo:block>
												            </fo:table-cell>
												            <fo:table-cell>
												            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">CRATES:</fo:block>
												            </fo:table-cell>
									            			<fo:table-cell>
												            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${totalCrates?if_exists?string("#0.00")}</fo:block>  
												            </fo:table-cell>
												            <fo:table-cell>
												            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">LTRS:</fo:block>
												            </fo:table-cell>
												            <fo:table-cell>
												            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${totalLtrs?if_exists?string("#0.00")}</fo:block>  
												            </fo:table-cell>
												            <fo:table-cell>
												            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>  
												            </fo:table-cell>
														</fo:table-row>
													<#else>
													 	<fo:table-row border-style="solid">
										                    <fo:table-cell>
												            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false"></fo:block>
												            </fo:table-cell>
												            <fo:table-cell>	
												            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>
												            </fo:table-cell>
												            <fo:table-cell>
												            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>
												            </fo:table-cell>
									            			<fo:table-cell>
												            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>  
												            </fo:table-cell>
												            <fo:table-cell>
												            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>
												            </fo:table-cell>
												            <fo:table-cell>
												            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">GRAND TOTAL</fo:block>  
												            </fo:table-cell>
												            <fo:table-cell>
												            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${totalAmount?if_exists?string("#0.00")}</fo:block>  
												            </fo:table-cell>
														</fo:table-row>
														<fo:table-row border-style="solid">
			                    							<fo:table-cell number-columns-spanned="7" >
					            								<fo:block  text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold" wrap-option="wrap">(In Words:${Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble(totalAmount?string("#0.00")), "%rupees-and-paise", locale).toUpperCase()} ONLY)</fo:block>
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
          		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
          		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
          		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
          		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
          		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">
					<fo:table>
			            <fo:table-column column-width="300pt"/>
			            <fo:table-column column-width="300pt"/>
			            <fo:table-body>
			                <fo:table-row>
			                    <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">SIGNATURE &amp; SEAL OF</fo:block>
					            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">ISSUING AUTHORITY</fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">SIGNATURE &amp; SEAL</fo:block>
					            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">OF RECEIVER</fo:block>
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