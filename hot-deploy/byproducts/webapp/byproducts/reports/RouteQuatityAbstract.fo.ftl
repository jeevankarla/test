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

<#-- do not display columns associated with values specified in the request, ie constraint values -->

<fo:layout-master-set>
	<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
            margin-top="0.3in">
        <fo:region-body margin-top="1.8in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>	
<#if routeWiseMap?has_content> 		
	<fo:page-sequence master-reference="main" force-page-count="no-force" font-size="8pt">					
		<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" >
			<fo:block text-align="center" keep-together="always" white-space-collapse="false">VST_ASCII-015   KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD.</fo:block>
			<fo:block text-align="center" keep-together="always" white-space-collapse="false">          UNIT : MOTHER DAIRY:G.K.V.K POST : YELAHANKA:BANGALORE : 560065</fo:block>
			<fo:block text-align="center" keep-together="always" white-space-collapse="false">Date : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayBegin, "dd/MM/yyyy")}&lt;ROUTE-WISE&gt;</fo:block>
			<fo:block text-align="left" keep-together="always" white-space-collapse="false">======================================================================================================================================================================================================================</fo:block>
			<fo:block>
				<fo:table>
					<fo:table-column column-width="30pt"/>
					<fo:table-column column-width="50pt"/>						
					<fo:table-column column-width="350pt"/>
					<fo:table-column column-width="40pt"/>
					<fo:table-column column-width="40pt"/>
					<fo:table-column column-width="350pt"/>
					<fo:table-column column-width="40pt"/>
					<fo:table-column column-width="40pt"/>
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell><fo:block keep-together="always">Route Name</fo:block></fo:table-cell>
							<fo:table-cell></fo:table-cell>							
								<fo:table-cell>
									<fo:block text-align="left" keep-together="always" white-space-collapse="false">N A N D I N I    M I L K    A N D    M I L K    P R O D U C T S                                                                                     Receivable    cash Amt.   Cheque Amt.  Balance</fo:block>
									<fo:block text-align="left" keep-together="always" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------VST_ASCII-027VST_ASCII-069</fo:block>
									<fo:block >
										<fo:table>
											<#list lmsProductList as product>
												<fo:table-column column-width="60pt"/>
											</#list>	
											<fo:table-column column-width="60pt"/>
											<#list byProdList as byProd>
												<fo:table-column column-width="60pt"/>
												<fo:table-column column-width="60pt"/>
											</#list>
											<fo:table-column column-width="30pt"/>
											<fo:table-body>												
												<fo:table-row>												
													<#list lmsProductList as product>													
														<fo:table-cell>		
															<#assign productDetails = delegator.findOne("Product", {"productId" : product}, true)>											
															<fo:block>${productDetails.get("brandName")?if_exists}</fo:block>																									
														</fo:table-cell>												
													</#list>	
													<fo:table-cell>
														<fo:block >TotalQty</fo:block>
													</fo:table-cell>
													<#assign i=0>
													<#list byProdList as byProd>
														<#assign i=i+1>	
														<#if i<8>												
														<fo:table-cell>		
															<#assign productDetails = delegator.findOne("Product", {"productId" : byProd}, true)>											
															<fo:block>${productDetails.get("brandName")?if_exists}</fo:block>																									
														</fo:table-cell>
														</#if>												
													</#list>	
													<fo:table-cell/>
												</fo:table-row>	
												
												<fo:table-row>						
													<#assign j=0>
													<#list byProdList as byProd>
														<#assign j=j+1>	
														<#if (j>=8) && (j<24)>												
														<fo:table-cell>		
															<#assign productDetails = delegator.findOne("Product", {"productId" : byProd}, true)>											
															<fo:block>${productDetails.get("brandName")?if_exists}</fo:block>																									
														</fo:table-cell>
														</#if>												
													</#list>	
													<fo:table-cell/>
												</fo:table-row>	
												
												<fo:table-row>						
													<#assign k=0>
													<#list byProdList as byProd>
														<#assign k=k+1>	
														<#if (k>=24) && (k<40)>												
														<fo:table-cell>		
															<#assign productDetails = delegator.findOne("Product", {"productId" : byProd}, true)>											
															<fo:block>${productDetails.get("brandName")?if_exists}</fo:block>																									
														</fo:table-cell>
														</#if>												
													</#list>	
													<fo:table-cell/>
												</fo:table-row>	
												<fo:table-row>						
													<#assign a=0>
													<#list byProdList as byProd>
														<#assign a=a+1>	
														<#if (a>=40) && (a<56)>												
														<fo:table-cell>		
															<#assign productDetails = delegator.findOne("Product", {"productId" : byProd}, true)>											
															<fo:block>${productDetails.get("brandName")?if_exists}</fo:block>																									
														</fo:table-cell>
														</#if>												
													</#list>	
													<fo:table-cell/>
												</fo:table-row>
												<fo:table-row>						
													<#assign b=0>
													<#list byProdList as byProd>
														<#assign b=b+1>	
														<#if (b>=56) && (b<72)>												
														<fo:table-cell>		
															<#assign productDetails = delegator.findOne("Product", {"productId" : byProd}, true)>											
															<fo:block>${productDetails.get("brandName")?if_exists}</fo:block>																									
														</fo:table-cell>
														</#if>												
													</#list>	
													<fo:table-cell/>
												</fo:table-row>										
											</fo:table-body>
										</fo:table>
									</fo:block>
								</fo:table-cell>	
						</fo:table-row>						
					</fo:table-body>
				</fo:table>
			</fo:block>
			<fo:block text-align="left" keep-together="always" white-space-collapse="false">VST_ASCII-027VST_ASCII-070----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		</fo:static-content>
		<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" >	
			<#assign amRouteDetailsList =amRouteWiseMap.entrySet()>
			<#list amRouteDetailsList as routesMap>			
				<#assign routeProdTotals = routesMap.getValue().get("routeWiseTotals")>
			 <fo:block>
				<fo:table>
					<fo:table-column column-width="30pt"/>
					<fo:table-column column-width="50pt"/>						
					<fo:table-column column-width="350pt"/>
					<fo:table-column column-width="40pt"/>
					<fo:table-column column-width="40pt"/>
					<fo:table-column column-width="350pt"/>
					<fo:table-column column-width="40pt"/>
					<fo:table-column column-width="40pt"/>
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell>
							   <#if routesMap.getKey() !="amGrandTotals">
							   		<#assign routeDetails = delegator.findOne("Facility", {"facilityId" : routesMap.getKey()}, true)>
									<fo:block keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(routeDetails.facilityName)),15)}</fo:block>
								<#else>
									<fo:block keep-together="always">AM Total </fo:block>
								</#if>	
							</fo:table-cell>
							<fo:table-cell></fo:table-cell>							
								<fo:table-cell>
									<fo:block >
										<fo:table>
											<#list lmsProductList as product>
												<fo:table-column column-width="60pt"/>
											</#list>	
											<fo:table-column column-width="60pt"/>
											<#list byProdList as byProd>
												<fo:table-column column-width="60pt"/>
												<fo:table-column column-width="60pt"/>
											</#list>
											<fo:table-column column-width="60pt"/>
											<fo:table-column column-width="60pt"/>
											<fo:table-column column-width="60pt"/>
											<fo:table-column column-width="60pt"/>
											<fo:table-column column-width="60pt"/>
											<fo:table-column column-width="60pt"/>
											<fo:table-column column-width="60pt"/>
											<fo:table-body>												
												<fo:table-row>												
													<#list lmsProductList as product>													
														<#assign qty=0>
														<#if routeProdTotals.get(product)?has_content>
															<#assign qty= routeProdTotals.get(product).get("total")>
														</#if>
															<#if qty !=0>
																<fo:table-cell>		
																	<fo:block text-align="center">${qty?if_exists}</fo:block>																									
																</fo:table-cell>
															<#else>
																<fo:table-cell>		
																	<fo:block text-align="center" linefeed-treatment="preserve">&#160;</fo:block>																									
																</fo:table-cell>
															</#if>													
														</#list>	
													<fo:table-cell>
														<fo:block text-align="center">${routesMap.getValue().get("routeTotQty")?if_exists}</fo:block>
													</fo:table-cell>
													<#assign l=0>
													<#list byProdList as byProd>
														<#assign l=l+1>	
														<#if l<8>												
															<#assign qty=0>
															<#if routeProdTotals.get(byProd)?has_content>
																<#assign qty= routeProdTotals.get(byProd).get("total")>
															</#if>
															<#if qty !=0>
																<#assign product = (delegator.findOne("Product", {"productId" : byProd}, false))!>
																<fo:table-cell>		
																	<fo:block text-align="center"><#if product.quantityIncluded !=0>${(qty/product.quantityIncluded)?if_exists}</#if></fo:block>																									
																</fo:table-cell>
															<#else>
																<fo:table-cell>		
																	<fo:block text-align="center" linefeed-treatment="preserve">&#160;</fo:block>																									
																</fo:table-cell>
															</#if>
														</#if>												
													</#list>	
												</fo:table-row>	
												<fo:table-row>						
													<#assign m=0>
													<#list byProdList as byProd>
														<#assign m=m+1>	
														<#if (m>=8) && (m<24)>												
															<#assign qty=0>
															<#if routeProdTotals.get(byProd)?has_content>
																<#assign qty= routeProdTotals.get(byProd).get("total")>
															</#if>
															<#if qty !=0>
																<#assign product = (delegator.findOne("Product", {"productId" : byProd}, false))!>
																<fo:table-cell>		
																	<fo:block text-align="center"><#if product.quantityIncluded !=0>${(qty/product.quantityIncluded)?if_exists}</#if></fo:block>																									
																</fo:table-cell>
															<#else>
																<fo:table-cell>		
																	<fo:block text-align="center" linefeed-treatment="preserve">&#160;</fo:block>																									
																</fo:table-cell>
															</#if>
														</#if>												
													</#list>	
													<fo:table-cell/>
												</fo:table-row>	
												<fo:table-row>						
													<#assign n=0>
													<#list byProdList as byProd>
														<#assign n=n+1>	
														<#if (n>=24) && (n<40)>												
															<#assign qty=0>
															<#if routeProdTotals.get(byProd)?has_content>
																<#assign qty= routeProdTotals.get(byProd).get("total")>
															</#if>
															<#if qty !=0>
																<#assign product = (delegator.findOne("Product", {"productId" : byProd}, false))!>
																<fo:table-cell>		
																	<fo:block text-align="center"><#if product.quantityIncluded !=0>${(qty/product.quantityIncluded)?if_exists}</#if></fo:block>																									
																</fo:table-cell>
															<#else>
																<fo:table-cell>		
																	<fo:block text-align="center" linefeed-treatment="preserve">&#160;</fo:block>																									
																</fo:table-cell>
															</#if>
														</#if>												
													</#list>	
													<fo:table-cell/>
												</fo:table-row>	
												<fo:table-row>						
													<#assign p=0>
													<#list byProdList as byProd>
														<#assign p=p+1>	
														<#if (p>=40) && (p<56)>												
															<#assign qty=0>
															<#if routeProdTotals.get(byProd)?has_content>
																<#assign qty= routeProdTotals.get(byProd).get("total")>
															</#if>
															<#if qty !=0>
																<#assign product = (delegator.findOne("Product", {"productId" : byProd}, false))!>
																<fo:table-cell>		
																	<fo:block text-align="center"><#if product.quantityIncluded !=0>${(qty/product.quantityIncluded)?if_exists}</#if></fo:block>																									
																</fo:table-cell>
															<#else>
																<fo:table-cell>		
																	<fo:block text-align="center" linefeed-treatment="preserve">&#160;</fo:block>																									
																</fo:table-cell>
															</#if>
														</#if>												
													</#list>	
													<fo:table-cell/>
												</fo:table-row>
												<fo:table-row>						
													<#assign c=0>
													<#list byProdList as byProd>
														<#assign c=c+1>	
														<#if (c>=56) && (c<72)>												
															<#assign qty=0>
															<#if routeProdTotals.get(byProd)?has_content>
																<#assign qty= routeProdTotals.get(byProd).get("total")>
															</#if>
															<#if qty !=0>
																<#assign product = (delegator.findOne("Product", {"productId" : byProd}, false))!>
																<fo:table-cell>		
																	<fo:block text-align="center"><#if product.quantityIncluded !=0>${(qty/product.quantityIncluded)?if_exists}</#if></fo:block>																									
																</fo:table-cell>
															<#else>
																<fo:table-cell>		
																	<fo:block text-align="center" linefeed-treatment="preserve">&#160;</fo:block>																									
																</fo:table-cell>
															</#if>
														</#if>												
													</#list>	
													<fo:table-cell/>
												</fo:table-row>	
												<fo:table-row>							
													<fo:table-cell/>
													<fo:table-cell/>
													<fo:table-cell/>
													<fo:table-cell/>
													<fo:table-cell/>
													<fo:table-cell/>
													<fo:table-cell/>
													<fo:table-cell/>
													<fo:table-cell/>
													<fo:table-cell/>
													<fo:table-cell/>
													<fo:table-cell/>
													<fo:table-cell>
														<fo:block text-align="center">${routesMap.getValue().get("routeAmount")?if_exists?string("#0.00")}</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="center">${routesMap.getValue().get("reciepts")?if_exists?string("#0.00")}</fo:block>
													</fo:table-cell>
													<fo:table-cell/>
													<fo:table-cell>
														<fo:block text-align="center">${(routesMap.getValue().get("routeAmount")-routesMap.getValue().get("reciepts"))?if_exists?string("#0.00")} VST_ASCII-027VST_ASCII-070</fo:block>
													</fo:table-cell>
												</fo:table-row>										
											</fo:table-body>
										</fo:table>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>						
						</fo:table-body>
					</fo:table>
				</fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			</#list>
			<#assign pmRouteDetailsList =pmRouteWiseMap.entrySet()>
			<#list pmRouteDetailsList as pmRoutesMap>			
				<#assign pmRouteProdTotals = pmRoutesMap.getValue().get("routeWiseTotals")>
			 <fo:block>
				<fo:table>
					<fo:table-column column-width="30pt"/>
					<fo:table-column column-width="50pt"/>						
					<fo:table-column column-width="350pt"/>
					<fo:table-column column-width="40pt"/>
					<fo:table-column column-width="40pt"/>
					<fo:table-column column-width="350pt"/>
					<fo:table-column column-width="40pt"/>
					<fo:table-column column-width="40pt"/>
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell>
								<#if pmRoutesMap.getKey() !="pmGrandTotals">
							   		<#assign routeDetails = delegator.findOne("Facility", {"facilityId" : pmRoutesMap.getKey()}, true)>
									<fo:block keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(routeDetails.facilityName)),15)}</fo:block>
								<#else>
									<fo:block keep-together="always">VST_ASCII-027VST_ASCII-069PM Total</fo:block>
								</#if>	
							</fo:table-cell>
							<fo:table-cell></fo:table-cell>							
								<fo:table-cell>
									<fo:block >
										<fo:table>
											<#list lmsProductList as product>
												<fo:table-column column-width="60pt"/>
											</#list>	
											<fo:table-column column-width="60pt"/>
											<#list byProdList as byProd>
												<fo:table-column column-width="60pt"/>
											</#list>
											<fo:table-column column-width="60pt"/>
											<fo:table-column column-width="60pt"/>
											<fo:table-column column-width="60pt"/>
											<fo:table-column column-width="60pt"/>
											<fo:table-column column-width="60pt"/>
											<fo:table-column column-width="60pt"/>
											<fo:table-column column-width="60pt"/>
											<fo:table-body>												
												<fo:table-row>												
													<#list lmsProductList as product>													
														<#assign qty=0>
														<#if pmRouteProdTotals.get(product)?has_content>
															<#assign qty= pmRouteProdTotals.get(product).get("total")>
														</#if>
															<#if qty !=0>
																<fo:table-cell>		
																	<fo:block text-align="center">${qty?if_exists}</fo:block>																									
																</fo:table-cell>
															<#else>
																<fo:table-cell>		
																	<fo:block text-align="center" linefeed-treatment="preserve">&#160;</fo:block>																									
																</fo:table-cell>
															</#if>													
														</#list>	
													<fo:table-cell>
														<fo:block text-align="center">${pmRoutesMap.getValue().get("routeTotQty")?if_exists}</fo:block>
													</fo:table-cell>
													<#assign l=0>
													<#list byProdList as byProd>
														<#assign l=l+1>	
														<#if l<8>												
															<#assign qty=0>
															<#if pmRouteProdTotals.get(byProd)?has_content>
																<#assign qty= pmRouteProdTotals.get(byProd).get("total")>
															</#if>
															<#if qty !=0>
																<#assign product = (delegator.findOne("Product", {"productId" : byProd}, false))!>
																<fo:table-cell>		
																	<fo:block text-align="center"><#if product.quantityIncluded !=0>${(qty/product.quantityIncluded)?if_exists}</#if></fo:block>																									
																</fo:table-cell>
															<#else>
																<fo:table-cell>		
																	<fo:block text-align="center" linefeed-treatment="preserve">&#160;</fo:block>																									
																</fo:table-cell>
															</#if>
														</#if>												
													</#list>	
												</fo:table-row>	
												<fo:table-row>						
													<#assign m=0>
													<#list byProdList as byProd>
														<#assign m=m+1>	
														<#if (m>=8) && (m<24)>												
															<#assign qty=0>
															<#if pmRouteProdTotals.get(byProd)?has_content>
																<#assign qty= pmRouteProdTotals.get(byProd).get("total")>
															</#if>
															<#if qty !=0>
																<#assign product = (delegator.findOne("Product", {"productId" : byProd}, false))!>
																<fo:table-cell>		
																	<fo:block text-align="center"><#if product.quantityIncluded !=0>${(qty/product.quantityIncluded)?if_exists}</#if></fo:block>																									
																</fo:table-cell>
															<#else>
																<fo:table-cell>		
																	<fo:block text-align="center" linefeed-treatment="preserve">&#160;</fo:block>																									
																</fo:table-cell>
															</#if>
														</#if>												
													</#list>	
													<fo:table-cell/>
												</fo:table-row>	
												<fo:table-row>						
													<#assign n=0>
													<#list byProdList as byProd>
														<#assign n=n+1>	
														<#if (n>=24) && (n<40)>												
															<#assign qty=0>
															<#if pmRouteProdTotals.get(byProd)?has_content>
																<#assign qty= pmRouteProdTotals.get(byProd).get("total")>
															</#if>
															<#if qty !=0>
																<#assign product = (delegator.findOne("Product", {"productId" : byProd}, false))!>
																<fo:table-cell>		
																	<fo:block text-align="center"><#if product.quantityIncluded !=0>${(qty/product.quantityIncluded)?if_exists}</#if></fo:block>																									
																</fo:table-cell>
															<#else>
																<fo:table-cell>		
																	<fo:block text-align="center" linefeed-treatment="preserve">&#160;</fo:block>																									
																</fo:table-cell>
															</#if>
														</#if>												
													</#list>	
													<fo:table-cell/>
												</fo:table-row>	
												<fo:table-row>						
													<#assign p=0>
													<#list byProdList as byProd>
														<#assign p=p+1>	
														<#if (p>=40) && (p<56)>												
															<#assign qty=0>
															<#if pmRouteProdTotals.get(byProd)?has_content>
																<#assign qty= pmRouteProdTotals.get(byProd).get("total")>
															</#if>
															<#if qty !=0>
																<#assign product = (delegator.findOne("Product", {"productId" : byProd}, false))!>
																<fo:table-cell>		
																	<fo:block text-align="center"><#if product.quantityIncluded !=0>${(qty/product.quantityIncluded)?if_exists}</#if></fo:block>																									
																</fo:table-cell>
															<#else>
																<fo:table-cell>		
																	<fo:block text-align="center" linefeed-treatment="preserve">&#160;</fo:block>																									
																</fo:table-cell>
															</#if>
														</#if>												
													</#list>	
													<fo:table-cell/>
												</fo:table-row>
												<fo:table-row>						
													<#assign c=0>
													<#list byProdList as byProd>
														<#assign c=c+1>	
														<#if (c>=56) && (c<72)>												
															<#assign qty=0>
															<#if pmRouteProdTotals.get(byProd)?has_content>
																<#assign qty= pmRouteProdTotals.get(byProd).get("total")>
															</#if>
															<#if qty !=0>
																<#assign product = (delegator.findOne("Product", {"productId" : byProd}, false))!>
																<fo:table-cell>		
																	<fo:block text-align="center"><#if product.quantityIncluded !=0>${(qty/product.quantityIncluded)?if_exists}</#if></fo:block>																									
																</fo:table-cell>
															<#else>
																<fo:table-cell>		
																	<fo:block text-align="center" linefeed-treatment="preserve">&#160;</fo:block>																									
																</fo:table-cell>
															</#if>
														</#if>												
													</#list>	
													<fo:table-cell/>
												</fo:table-row>	
												<fo:table-row>							
													<fo:table-cell/>
													<fo:table-cell/>
													<fo:table-cell/>
													<fo:table-cell/>
													<fo:table-cell/>
													<fo:table-cell/>
													<fo:table-cell/>
													<fo:table-cell/>
													<fo:table-cell/>
													<fo:table-cell/>
													<fo:table-cell/>
													<fo:table-cell/>
													<fo:table-cell>
														<fo:block text-align="center">${pmRoutesMap.getValue().get("routeAmount")?if_exists?string("#0.00")}</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="center">${pmRoutesMap.getValue().get("reciepts")?if_exists?string("#0.00")}</fo:block>
													</fo:table-cell>
													<fo:table-cell/>
													<fo:table-cell>
														<fo:block text-align="center">${(pmRoutesMap.getValue().get("routeAmount")-pmRoutesMap.getValue().get("reciepts"))?if_exists?string("#0.00")} VST_ASCII-027VST_ASCII-070</fo:block>
													</fo:table-cell>
												</fo:table-row>										
											</fo:table-body>
										</fo:table>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>						
						</fo:table-body>
					</fo:table>
				</fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			</#list>
    	</fo:flow>
	</fo:page-sequence>		
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