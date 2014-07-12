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
        <fo:region-body margin-top="1.1in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>	
<#if errorMessage?has_content>
<fo:page-sequence master-reference="main">
   <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
      <fo:block font-size="14pt">
              ${errorMessage}.
   	  </fo:block>
   </fo:flow>
</fo:page-sequence> 
<#else>
<#if routeWiseMap?has_content || adhocBoothTotals?has_content> 
<#if !(parameters.summeryOnly?exists)>		
	<fo:page-sequence master-reference="main" force-page-count="no-force" font-size="6pt">					
		<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" >
			<fo:block text-align="center" keep-together="always" white-space-collapse="false">VST_ASCII-015   KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD.</fo:block>
			<fo:block text-align="center" keep-together="always" white-space-collapse="false">          UNIT : MOTHER DAIRY:G.K.V.K POST : YELAHANKA:BANGALORE : 560065</fo:block>
			<fo:block text-align="center" keep-together="always" white-space-collapse="false">&lt;ROUTE-WISE&gt; From: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayBegin, "dd/MM/yyyy")}-To:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayEnd, "dd/MM/yyyy")}</fo:block>
			<fo:block text-align="left" keep-together="always" white-space-collapse="false">======================================================================================================================================================================================================================</fo:block>
			<fo:block>
				<fo:table>
					<fo:table-column column-width="40pt"/>
					<fo:table-column column-width="630pt"/>						
					<fo:table-column column-width="40pt"/>
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell><fo:block keep-together="always">Route Name</fo:block></fo:table-cell>
							<fo:table-cell>
									<#-- <fo:block text-align="left" keep-together="always" white-space-collapse="false">N A N D I N I    M I L K    A N D    M I L K    P R O D U C T S                                                                                     Receivable    cash Amt.   Cheque Amt.  Balance</fo:block> -->
									 <fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;    &#160;  &#160;    &#160;  &#160;  &#160;  &#160;    &#160;              							                                                        &#160;    Receivable    Paid-Amt.     Balance</fo:block> 
									<fo:block text-align="left" keep-together="always" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------VST_ASCII-027VST_ASCII-069</fo:block>
									<fo:block >
										<fo:table>
											<#list lmsProductList as product>
												<fo:table-column column-width="50pt"/>
											</#list>	
											<#list byProdList as byProd>
												<fo:table-column column-width="50pt"/>
											</#list>
											<fo:table-body>		
											<fo:table-row>
											<#assign columnCounter =0>
											<#list lmsProductList as product>
													<#assign columnCounter = columnCounter+1>
													<#if (columnCounter > 15) > <#--  11 products for each row and if morethan 11 then we will wrap to next line -->
														<#assign columnCounter =1>
														</fo:table-row>	
														<fo:table-row>
													</#if>
													<fo:table-cell>	
														<fo:block text-align="right">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(productNames.get(product)?if_exists)),8)} </fo:block>																									
													</fo:table-cell>
											</#list>
											<#list byProdList as product>
												<#assign columnCounter = columnCounter+1>
												<#if (columnCounter > 15) > <#--  11 products for each row and if morethan 11 then we will wrap to next line -->
														<#assign columnCounter =1>
														</fo:table-row>	
														<fo:table-row>
												</#if>
												<fo:table-cell>
														<fo:block text-align="right">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(productNames.get(product)?if_exists)),8)} </fo:block>
												</fo:table-cell>
											</#list>
										    </fo:table-row>
											</fo:table-body>
										</fo:table>
									</fo:block>
								</fo:table-cell>	
						</fo:table-row>						
					</fo:table-body>
				</fo:table>
			</fo:block>
			<fo:block text-align="left" keep-together="always" white-space-collapse="false">VST_ASCII-027VST_ASCII-070---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		</fo:static-content>
		<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace"  font-size="6pt" >	
			<#assign amRouteDetailsList =amRouteWiseMap.entrySet()>
				<#assign noOfRoots=0>
			<#list amRouteDetailsList as routesMap>			
			<#assign routeProdTotals = routesMap.getValue().get("routeWiseTotals")>
    		<#assign noOfRoots=noOfRoots+1>	
    		<#if (noOfRoots==7) >
    			<fo:block  break-after="page"></fo:block>
    			<#assign noOfRoots=0>
    		</#if>
			 <fo:block>
				<fo:table>
					<fo:table-column column-width="40pt"/>
					<fo:table-column column-width="630pt"/>						
					<fo:table-column column-width="40pt"/>
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell>
							   <#if routesMap.getKey() !="amGrandTotals">
							   		<#assign routeDetails = delegator.findOne("Facility", {"facilityId" : routesMap.getKey()}, true)>
									<fo:block keep-together="always" text-align="center">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(routeDetails.facilityName)),15)}</fo:block>
								<#else>
									<fo:block text-align="center"  keep-together="always">AM Total </fo:block>
								</#if>	
							</fo:table-cell>
							<fo:table-cell>
									<fo:block >
										<fo:table>
											<#list lmsProductList as product>
												<fo:table-column column-width="50pt"/>
											</#list>	
											<#list byProdList as byProd>
												<fo:table-column column-width="50pt"/>
											</#list>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-body>		
											<fo:table-row>
											<#assign columnCounter = 0>
											<#list lmsProductList as product>
												<#assign columnCounter = columnCounter+1>
												 <#if (columnCounter > 15) > <#--  11 products for each row and if morethan 11 then we will wrap to next line -->
														<#assign columnCounter =1>
														</fo:table-row>	
														<fo:table-row>
												</#if>
												<#assign qty=0>
												<#if routeProdTotals.get(product)?has_content>
													<#assign qty= routeProdTotals.get(product).get("total")>
												</#if>
												<#if qty !=0>
													<fo:table-cell>		
														<fo:block text-align="right">${qty?if_exists}</fo:block>																									
													</fo:table-cell>
												<#else>
													<fo:table-cell>		
														<fo:block text-align="right" linefeed-treatment="preserve">-</fo:block>																									
													</fo:table-cell>
												</#if>
											</#list>
											<#list byProdList as product>
											<#assign columnCounter = columnCounter+1>
											   <#if (columnCounter > 15) > <#--  11 products for each row and if morethan 11 then we will wrap to next line -->
														<#assign columnCounter =1>
														</fo:table-row>	
														<fo:table-row>
												</#if>
												<#assign byProdQty=0>
												<#if routeProdTotals.get(product)?has_content>
													<#assign byProdQty= routeProdTotals.get(product).get("total")>
												</#if>
												<#if byProdQty !=0>
													<fo:table-cell>		
														<fo:block text-align="right">${byProdQty?if_exists}</fo:block>																									
													</fo:table-cell>
												<#else>
													<fo:table-cell>		
														<fo:block text-align="right" linefeed-treatment="preserve">-</fo:block>																									
													</fo:table-cell>
												</#if>
											</#list>
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
													<#--
													<fo:table-cell>
														<fo:block text-align="center">${routesMap.getValue().get("routeTotQty")?if_exists}</fo:block>
													</fo:table-cell> -->
													<fo:table-cell>
														<fo:block text-align="center">${routesMap.getValue().get("routeAmount")?if_exists?string("#0.00")}</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="center">${routesMap.getValue().get("reciepts")?if_exists?string("#0.00")}</fo:block>
													</fo:table-cell>
													<#--<fo:table-cell/> -->
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
				<fo:block text-align="left" keep-together="always" white-space-collapse="false">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			</#list>
			<#assign noOfRoots=0>	
			<#assign pmRouteDetailsList =pmRouteWiseMap.entrySet()>
			<#list pmRouteDetailsList as pmRoutesMap>			
				<#assign pmRouteProdTotals = pmRoutesMap.getValue().get("routeWiseTotals")>
				<#assign noOfRoots=noOfRoots+1>	
    		   <#if (noOfRoots==7) >
    			<fo:block  break-after="page"></fo:block>
    			<#assign noOfRoots=0>
    		  </#if>
			 <fo:block>
				<fo:table>
				    <fo:table-column column-width="40pt"/>
					<fo:table-column column-width="630pt"/>						
					<fo:table-column column-width="40pt"/>
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell>
								<#if pmRoutesMap.getKey() !="pmGrandTotals">
							   		<#assign routeDetails = delegator.findOne("Facility", {"facilityId" : pmRoutesMap.getKey()}, true)>
									<fo:block  text-align="center" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(routeDetails.facilityName)),15)}</fo:block>
								<#else>
									<fo:block text-align="center" keep-together="always">PM Total</fo:block>
								</#if>	
							</fo:table-cell>
							<fo:table-cell>
									<fo:block >
										<fo:table>
											<#list lmsProductList as product>
												<fo:table-column column-width="50pt"/>
											</#list>	
											<#list byProdList as byProd>
												<fo:table-column column-width="50pt"/>
											</#list>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-body>		
											<fo:table-row>
											<#assign columnCounter = 0>
											<#list lmsProductList as product>
												<#assign columnCounter = columnCounter+1>
												 <#if (columnCounter > 15) > 
														<#assign columnCounter =1>
														</fo:table-row>	
														<fo:table-row>
												</#if>
												<#assign qty=0>
												<#if pmRouteProdTotals.get(product)?has_content>
															<#assign qty= pmRouteProdTotals.get(product).get("total")>
												</#if>
												<#if qty !=0>
													<fo:table-cell>		
														<fo:block text-align="right">${qty?if_exists}</fo:block>																									
													</fo:table-cell>
												<#else>
													<fo:table-cell>		
														<fo:block text-align="right" linefeed-treatment="preserve">-</fo:block>																									
													</fo:table-cell>
												</#if>
											</#list>
											<#list byProdList as product>
											<#assign columnCounter = columnCounter+1>
											   <#if (columnCounter > 15) >
														<#assign columnCounter =1>
														</fo:table-row>	
														<fo:table-row>
												</#if>
												<#assign byProdQty=0>
												<#if pmRouteProdTotals.get(product)?has_content>
													<#assign byProdQty= pmRouteProdTotals.get(product).get("total")>
												</#if>
												<#if byProdQty !=0>
													<fo:table-cell>		
														<fo:block text-align="right">${byProdQty?if_exists}</fo:block>																									
													</fo:table-cell>
												<#else>
													<fo:table-cell>		
														<fo:block text-align="right" linefeed-treatment="preserve">-</fo:block>																									
													</fo:table-cell>
												</#if>
											</#list>
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
													<#--
													<fo:table-cell>
														<fo:block text-align="center">${pmRoutesMap.getValue().get("routeTotQty")?if_exists}</fo:block>
													</fo:table-cell> -->
													<fo:table-cell>
														<fo:block text-align="center">${pmRoutesMap.getValue().get("routeAmount")?if_exists?string("#0.00")}</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="center">${pmRoutesMap.getValue().get("reciepts")?if_exists?string("#0.00")}</fo:block>
													</fo:table-cell>
												<#--<fo:table-cell/> -->
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
				<fo:block text-align="left" keep-together="always" white-space-collapse="false">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			</#list> 
			<#--Adhoc TotalsStrts here -->
			<#assign adhocBoothTotalsList = adhocBoothTotals.entrySet()>
			<#if adhocBoothTotals?has_content>
			<fo:block text-align="left" keep-together="always" white-space-collapse="false">--------Counter Sale **RetailerWise**------</fo:block>
			</#if>
			<#assign noOfBooths=0>	
			<#list adhocBoothTotalsList as adhocBoothMap>			
				<#assign adhocBoothProdTotals = adhocBoothMap.getValue().get("productTotals")>
				<#assign noOfBooths=noOfBooths+1>	
    		   <#if (noOfBooths==7) >
    			<fo:block  break-after="page"></fo:block>
    			<#assign noOfBooths=0>
    		  </#if>
			 <fo:block>
				<fo:table>
				    <fo:table-column column-width="40pt"/>
					<fo:table-column column-width="630pt"/>						
					<fo:table-column column-width="40pt"/>
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell>
							<#if adhocBoothMap.getKey()!="Total">
									<fo:block text-align="center"  font-size="3pt" keep-together="always">RetailerId:${adhocBoothMap.getKey()}</fo:block>
							<#else>
							<fo:block text-align="center"  font-size="3pt" keep-together="always">CounterSale Total:</fo:block>
							</#if>
							</fo:table-cell>
							<fo:table-cell>
									<fo:block >
										<fo:table>
											<#list lmsProductList as product>
												<fo:table-column column-width="50pt"/>
											</#list>	
											<#list byProdList as byProd>
												<fo:table-column column-width="50pt"/>
											</#list>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-body>		
											<fo:table-row>
											<#assign columnCounter = 0>
											<#list lmsProductList as product>
												<#assign columnCounter = columnCounter+1>
												 <#if (columnCounter > 15) > 
														<#assign columnCounter =1>
														</fo:table-row>	
														<fo:table-row>
												</#if>
												<#assign qty=0>
												<#if adhocBoothProdTotals.get(product)?has_content>
															<#assign qty= adhocBoothProdTotals.get(product).get("total")>
												</#if>
												<#if qty !=0>
													<fo:table-cell>		
														<fo:block text-align="right">${qty?if_exists}</fo:block>																									
													</fo:table-cell>
												<#else>
													<fo:table-cell>		
														<fo:block text-align="right" linefeed-treatment="preserve">-</fo:block>																									
													</fo:table-cell>
												</#if>
											</#list>
											<#list byProdList as product>
											<#assign columnCounter = columnCounter+1>
											   <#if (columnCounter > 15) >
														<#assign columnCounter =1>
														</fo:table-row>	
														<fo:table-row>
												</#if>
												<#assign byProdQty=0>
												<#if adhocBoothProdTotals.get(product)?has_content>
													<#assign byProdQty= adhocBoothProdTotals.get(product).get("total")>
												</#if>
												<#if byProdQty !=0>
													<fo:table-cell>		
														<fo:block text-align="right">${byProdQty?if_exists}</fo:block>																									
													</fo:table-cell>
												<#else>
													<fo:table-cell>		
														<fo:block text-align="right" linefeed-treatment="preserve">-</fo:block>																									
													</fo:table-cell>
												</#if>
											</#list>
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
													<#--
													<fo:table-cell>
														<fo:block text-align="center">${pmRoutesMap.getValue().get("routeTotQty")?if_exists}</fo:block>
													</fo:table-cell> -->
													<fo:table-cell>
														<fo:block text-align="center">${adhocBoothMap.getValue().get("totalRevenue")?if_exists?string("#0.00")}</fo:block>
													</fo:table-cell>
													<#assign adhocPaidAmnt=0>
														<#if adhocBoothPaymentMap.get(adhocBoothMap.getKey())?has_content>
														<#assign adhocPaidAmnt=adhocBoothPaymentMap.get(adhocBoothMap.getKey())>
														</#if>
													<#if adhocBoothMap.getKey()!="Total">
													<fo:table-cell>
														<fo:block text-align="center">${adhocPaidAmnt?string("#0.00")}</fo:block>
													</fo:table-cell>
													<#else>
							                         <fo:table-cell>
														<fo:block text-align="center">${adhocBoothMap.getValue().get("adhocPaidAmount")?if_exists?string("#0.00")}</fo:block>
													</fo:table-cell>
							                       </#if>
													<fo:table-cell/>
													<fo:table-cell>
														<fo:block text-align="center"></fo:block>
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
				<fo:block text-align="left" keep-together="always" white-space-collapse="false">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			</#list>
			
			<#assign grandProdTotals = grandProdTotals>
			<#if grandProdTotals?has_content>
			<fo:block>
				<fo:table>
					<fo:table-column column-width="40pt"/>
					<fo:table-column column-width="630pt"/>						
					<fo:table-column column-width="40pt"/>
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell>
									<fo:block keep-together="always">Grand Total</fo:block>
							</fo:table-cell>
							<fo:table-cell>
									<fo:block >
										<fo:table>
											<#list lmsProductList as product>
												<fo:table-column column-width="50pt"/>
											</#list>	
											<#list byProdList as byProd>
												<fo:table-column column-width="50pt"/>
											</#list>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-body>	
											<fo:table-row>
											<#assign columnCounter = 0>
											<#list lmsProductList as product>
												<#assign columnCounter = columnCounter+1>
												 <#if (columnCounter > 15) > <#--  11 products for each row and if morethan 11 then we will wrap to next line -->
														<#assign columnCounter =1>
														</fo:table-row>	
														<fo:table-row>
												</#if>
												<#assign qty=0>
												<#if grandProdTotals.get(product)?has_content>
															<#assign qty= grandProdTotals.get(product).get("total")>
												</#if>
												<#if qty !=0>
													<fo:table-cell>		
														<fo:block text-align="right">${qty?if_exists}</fo:block>																									
													</fo:table-cell>
												<#else>
													<fo:table-cell>		
														<fo:block text-align="right" linefeed-treatment="preserve">-</fo:block>																									
													</fo:table-cell>
												</#if>
											</#list>
											<#list byProdList as product>
											<#assign columnCounter = columnCounter+1>
											   <#if (columnCounter > 15) > <#--  11 products for each row and if morethan 11 then we will wrap to next line -->
														<#assign columnCounter =1>
														</fo:table-row>	
														<fo:table-row>
												</#if>
												<#assign byProdQty=0>
												<#if grandProdTotals.get(product)?has_content>
															<#assign byProdQty= grandProdTotals.get(product).get("total")>
												</#if>
												<#if byProdQty !=0>
													<fo:table-cell>		
														<fo:block text-align="right">${byProdQty?if_exists}</fo:block>																									
													</fo:table-cell>
												<#else>
													<fo:table-cell>		
														<fo:block text-align="right" linefeed-treatment="preserve">-</fo:block>																									
													</fo:table-cell>
												</#if>
											</#list>
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
													<#--
													<fo:table-cell>
														<fo:block text-align="center">${(routeTotQty)?if_exists}</fo:block>
													</fo:table-cell> -->
													<fo:table-cell>
														<fo:block text-align="center">${(routeAmount)?if_exists?string("#0.00")}</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="center">${(reciepts)?if_exists?string("#0.00")}</fo:block>
													</fo:table-cell>
													<#--<fo:table-cell/> -->
													<fo:table-cell>
														<fo:block text-align="center">${((routeAmount)-(reciepts))?if_exists?string("#0.00")} VST_ASCII-027VST_ASCII-070</fo:block>
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
				<fo:block text-align="left" keep-together="always" white-space-collapse="false">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
    	</#if>
    	</fo:flow>
	</fo:page-sequence>	
	</#if>
	<#-- Summery report logic strts here-->
	<#if (parameters.summeryOnly?exists)>
	<fo:page-sequence master-reference="main" force-page-count="no-force" font-size="6pt">					
		<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" >
			<fo:block text-align="center" keep-together="always" white-space-collapse="false">VST_ASCII-015   KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD.</fo:block>
			<fo:block text-align="center" keep-together="always" white-space-collapse="false">          UNIT : MOTHER DAIRY:G.K.V.K POST : YELAHANKA:BANGALORE : 560065</fo:block>
			<fo:block text-align="center" keep-together="always" white-space-collapse="false">&lt;ROUTE-WISE&gt; From: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayBegin, "dd/MM/yyyy")}-To:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayEnd, "dd/MM/yyyy")}</fo:block>
			<fo:block text-align="left" keep-together="always" white-space-collapse="false">======================================================================================================================================================================================================================</fo:block>
			<fo:block>
				<fo:table>
					<fo:table-column column-width="40pt"/>
					<fo:table-column column-width="630pt"/>						
					<fo:table-column column-width="40pt"/>
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell><fo:block keep-together="always">Route Name</fo:block></fo:table-cell>
							<fo:table-cell>
									<#-- <fo:block text-align="left" keep-together="always" white-space-collapse="false">N A N D I N I    M I L K    A N D    M I L K    P R O D U C T S                                                                                     Receivable    cash Amt.   Cheque Amt.  Balance</fo:block> -->
									 <fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;    &#160;  &#160;    &#160;  &#160;  &#160;  &#160;    &#160;              							                                                        &#160;    Receivable    Paid-Amt.     Balance</fo:block> 
									<fo:block text-align="left" keep-together="always" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------VST_ASCII-027VST_ASCII-069</fo:block>
									<fo:block >
										<fo:table>
											<#list lmsProductList as product>
												<fo:table-column column-width="50pt"/>
											</#list>	
											<#list byProdList as byProd>
												<fo:table-column column-width="50pt"/>
											</#list>
											<fo:table-body>		
											<fo:table-row>
											<#assign columnCounter =0>
											<#list lmsProductList as product>
													<#assign columnCounter = columnCounter+1>
													<#if (columnCounter > 15) > <#--  11 products for each row and if morethan 11 then we will wrap to next line -->
														<#assign columnCounter =1>
														</fo:table-row>	
														<fo:table-row>
													</#if>
													<fo:table-cell>	
														<fo:block text-align="right">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(productNames.get(product)?if_exists)),8)} </fo:block>																									
													</fo:table-cell>
											</#list>
											<#list byProdList as product>
												<#assign columnCounter = columnCounter+1>
												<#if (columnCounter > 15) > <#--  11 products for each row and if morethan 11 then we will wrap to next line -->
														<#assign columnCounter =1>
														</fo:table-row>	
														<fo:table-row>
												</#if>
												<fo:table-cell>
														<fo:block text-align="right">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(productNames.get(product)?if_exists)),8)} </fo:block>
												</fo:table-cell>
											</#list>
										    </fo:table-row>
											</fo:table-body>
										</fo:table>
									</fo:block>
								</fo:table-cell>	
						</fo:table-row>						
					</fo:table-body>
				</fo:table>
			</fo:block>
			<fo:block text-align="left" keep-together="always" white-space-collapse="false">VST_ASCII-027VST_ASCII-070---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		</fo:static-content>
		<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace"  font-size="6pt" >	
			 <fo:block>
				<fo:table>
				    <fo:table-column column-width="40pt"/>
					<fo:table-column column-width="630pt"/>						
					<fo:table-column column-width="40pt"/>
					<fo:table-body>
					<fo:table-row>
							<fo:table-cell>
									<fo:block text-align="center"  keep-together="always"></fo:block>
							</fo:table-cell>
				   </fo:table-row>
					<#assign amRouteDetailsList =amRouteWiseMap.entrySet()>
			         <#list amRouteDetailsList as routesMap>			
			        <#if routesMap.getKey() =="amGrandTotals">
				   <#assign routeProdTotals = routesMap.getValue().get("routeWiseTotals")>
					<fo:table-row>
							<fo:table-cell>
									<fo:block text-align="center"  keep-together="always">AM Total </fo:block>
							</fo:table-cell>
							<fo:table-cell>
									<fo:block >
										<fo:table>
											<#list lmsProductList as product>
												<fo:table-column column-width="50pt"/>
											</#list>	
											<#list byProdList as byProd>
												<fo:table-column column-width="50pt"/>
											</#list>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-body>		
											<fo:table-row>
											<#assign columnCounter = 0>
											<#list lmsProductList as product>
												<#assign columnCounter = columnCounter+1>
												 <#if (columnCounter > 15) > <#--  11 products for each row and if morethan 11 then we will wrap to next line -->
														<#assign columnCounter =1>
														</fo:table-row>	
														<fo:table-row>
												</#if>
												<#assign qty=0>
												<#if routeProdTotals.get(product)?has_content>
													<#assign qty= routeProdTotals.get(product).get("total")>
												</#if>
												<#if qty !=0>
													<fo:table-cell>		
														<fo:block text-align="right">${qty?if_exists}</fo:block>																									
													</fo:table-cell>
												<#else>
													<fo:table-cell>		
														<fo:block text-align="right" linefeed-treatment="preserve">-</fo:block>																									
													</fo:table-cell>
												</#if>
											</#list>
											<#list byProdList as product>
											<#assign columnCounter = columnCounter+1>
											   <#if (columnCounter > 15) > <#--  11 products for each row and if morethan 11 then we will wrap to next line -->
														<#assign columnCounter =1>
														</fo:table-row>	
														<fo:table-row>
												</#if>
												<#assign byProdQty=0>
												<#if routeProdTotals.get(product)?has_content>
													<#assign byProdQty= routeProdTotals.get(product).get("total")>
												</#if>
												<#if byProdQty !=0>
													<fo:table-cell>		
														<fo:block text-align="right">${byProdQty?if_exists}</fo:block>																									
													</fo:table-cell>
												<#else>
													<fo:table-cell>		
														<fo:block text-align="right" linefeed-treatment="preserve">-</fo:block>																									
													</fo:table-cell>
												</#if>
											</#list>
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
													<#--
													<fo:table-cell>
														<fo:block text-align="center">${routesMap.getValue().get("routeTotQty")?if_exists}</fo:block>
													</fo:table-cell> -->
													<fo:table-cell>
														<fo:block text-align="center">${routesMap.getValue().get("routeAmount")?if_exists?string("#0.00")}</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="center">${routesMap.getValue().get("reciepts")?if_exists?string("#0.00")}</fo:block>
													</fo:table-cell>
													<#--<fo:table-cell/> -->
													<fo:table-cell>
														<fo:block text-align="center">${(routesMap.getValue().get("routeAmount")-routesMap.getValue().get("reciepts"))?if_exists?string("#0.00")} VST_ASCII-027VST_ASCII-070</fo:block>
													</fo:table-cell>
												</fo:table-row>										
											</fo:table-body>
										</fo:table>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
							
							<fo:table-row>
							<fo:table-cell>
							<fo:block text-align="left" keep-together="always" white-space-collapse="false">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
							</fo:table-cell>
				            </fo:table-row>
					  </#if>	
			         </#list>
					<#assign pmRouteDetailsList =pmRouteWiseMap.entrySet()>
			         <#list pmRouteDetailsList as pmRoutesMap>	
			        <#if pmRoutesMap.getKey() =="pmGrandTotals">		
				    <#assign pmRouteProdTotals = pmRoutesMap.getValue().get("routeWiseTotals")>
						<fo:table-row>
							<fo:table-cell>
									<fo:block text-align="center" keep-together="always">PM Total</fo:block>
							</fo:table-cell>
							<fo:table-cell>
									<fo:block >
										<fo:table>
											<#list lmsProductList as product>
												<fo:table-column column-width="50pt"/>
											</#list>	
											<#list byProdList as byProd>
												<fo:table-column column-width="50pt"/>
											</#list>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-body>		
											<fo:table-row>
											<#assign columnCounter = 0>
											<#list lmsProductList as product>
												<#assign columnCounter = columnCounter+1>
												 <#if (columnCounter > 15) > 
														<#assign columnCounter =1>
														</fo:table-row>	
														<fo:table-row>
												</#if>
												<#assign qty=0>
												<#if pmRouteProdTotals.get(product)?has_content>
															<#assign qty= pmRouteProdTotals.get(product).get("total")>
												</#if>
												<#if qty !=0>
													<fo:table-cell>		
														<fo:block text-align="right">${qty?if_exists}</fo:block>																									
													</fo:table-cell>
												<#else>
													<fo:table-cell>		
														<fo:block text-align="right" linefeed-treatment="preserve">-</fo:block>																									
													</fo:table-cell>
												</#if>
											</#list>
											
											<#list byProdList as product>
											<#assign columnCounter = columnCounter+1>
											   <#if (columnCounter > 15) >
														<#assign columnCounter =1>
														</fo:table-row>	
														<fo:table-row>
												</#if>
												<#assign byProdQty=0>
												<#if pmRouteProdTotals.get(product)?has_content>
													<#assign byProdQty= pmRouteProdTotals.get(product).get("total")>
												</#if>
												<#if byProdQty !=0>
													<fo:table-cell>		
														<fo:block text-align="right">${byProdQty?if_exists}</fo:block>																									
													</fo:table-cell>
												<#else>
													<fo:table-cell>		
														<fo:block text-align="right" linefeed-treatment="preserve">-</fo:block>																									
													</fo:table-cell>
												</#if>
											</#list>
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
													<#--
													<fo:table-cell>
														<fo:block text-align="center">${pmRoutesMap.getValue().get("routeTotQty")?if_exists}</fo:block>
													</fo:table-cell> -->
													<fo:table-cell>
														<fo:block text-align="center">${pmRoutesMap.getValue().get("routeAmount")?if_exists?string("#0.00")}</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="center">${pmRoutesMap.getValue().get("reciepts")?if_exists?string("#0.00")}</fo:block>
													</fo:table-cell>
													<#--<fo:table-cell/> -->
													<fo:table-cell>
														<fo:block text-align="center">${(pmRoutesMap.getValue().get("routeAmount")-pmRoutesMap.getValue().get("reciepts"))?if_exists?string("#0.00")} VST_ASCII-027VST_ASCII-070</fo:block>
													</fo:table-cell>
												</fo:table-row>										
											</fo:table-body>
										</fo:table>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
							
							<fo:table-row>
							<fo:table-cell>
							<fo:block text-align="left" keep-together="always" white-space-collapse="false">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
							</fo:table-cell>
				            </fo:table-row>	
							 </#if>
			                 </#list>
			                 
			                 <#--Adhoc TotalsStrts here -->
							<#assign adhocBoothTotalsList = adhocBoothTotals.entrySet()>
							<#assign noOfBooths=0>	
							<#list adhocBoothTotalsList as adhocBoothMap>		
							<#if adhocBoothMap.getKey()=="Total">	
								<#assign adhocBoothProdTotals = adhocBoothMap.getValue().get("productTotals")>
										<fo:table-row>
											<fo:table-cell>
											<fo:block text-align="center"  font-size="3pt" keep-together="always">CounterSale Total</fo:block>
											</fo:table-cell>
											<fo:table-cell>
													<fo:block >
														<fo:table>
															<#list lmsProductList as product>
																<fo:table-column column-width="50pt"/>
															</#list>	
															<#list byProdList as byProd>
																<fo:table-column column-width="50pt"/>
															</#list>
															<fo:table-column column-width="50pt"/>
															<fo:table-column column-width="50pt"/>
															<fo:table-column column-width="50pt"/>
															<fo:table-column column-width="50pt"/>
															<fo:table-column column-width="50pt"/>
															<fo:table-column column-width="50pt"/>
															<fo:table-column column-width="50pt"/>
															<fo:table-column column-width="50pt"/>
															<fo:table-column column-width="50pt"/>
															<fo:table-column column-width="50pt"/>
															<fo:table-column column-width="50pt"/>
															<fo:table-column column-width="50pt"/>
															<fo:table-column column-width="50pt"/>
															<fo:table-column column-width="50pt"/>
															<fo:table-column column-width="50pt"/>
															<fo:table-column column-width="50pt"/>
															<fo:table-column column-width="50pt"/>
															<fo:table-column column-width="50pt"/>
															<fo:table-body>		
															<fo:table-row>
															<#assign columnCounter = 0>
															<#list lmsProductList as product>
																<#assign columnCounter = columnCounter+1>
																 <#if (columnCounter > 15) > 
																		<#assign columnCounter =1>
																		</fo:table-row>	
																		<fo:table-row>
																</#if>
																<#assign qty=0>
																<#if adhocBoothProdTotals.get(product)?has_content>
																			<#assign qty= adhocBoothProdTotals.get(product).get("total")>
																</#if>
																<#if qty !=0>
																	<fo:table-cell>		
																		<fo:block text-align="right">${qty?if_exists}</fo:block>																									
																	</fo:table-cell>
																<#else>
																	<fo:table-cell>		
																		<fo:block text-align="right" linefeed-treatment="preserve">-</fo:block>																									
																	</fo:table-cell>
																</#if>
															</#list>
															<#list byProdList as product>
															<#assign columnCounter = columnCounter+1>
															   <#if (columnCounter > 15) >
																		<#assign columnCounter =1>
																		</fo:table-row>	
																		<fo:table-row>
																</#if>
																<#assign byProdQty=0>
																<#if adhocBoothProdTotals.get(product)?has_content>
																	<#assign byProdQty= adhocBoothProdTotals.get(product).get("total")>
																</#if>
																<#if byProdQty !=0>
																	<fo:table-cell>		
																		<fo:block text-align="right">${byProdQty?if_exists}</fo:block>																									
																	</fo:table-cell>
																<#else>
																	<fo:table-cell>		
																		<fo:block text-align="right" linefeed-treatment="preserve">-</fo:block>																									
																	</fo:table-cell>
																</#if>
															</#list>
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
																	<#--
																	<fo:table-cell>
																		<fo:block text-align="center">${pmRoutesMap.getValue().get("routeTotQty")?if_exists}</fo:block>
																	</fo:table-cell> -->
																	<fo:table-cell>
																		<fo:block text-align="center">${adhocBoothMap.getValue().get("totalRevenue")?if_exists?string("#0.00")}</fo:block>
																	</fo:table-cell>
																	<fo:table-cell>
																		<fo:block text-align="center">${adhocBoothMap.getValue().get("adhocPaidAmount")?if_exists?string("#0.00")}</fo:block>
																	</fo:table-cell>
																	<#--<fo:table-cell/> -->
																	<fo:table-cell>
																		<fo:block text-align="center"></fo:block>
																	</fo:table-cell> 
																</fo:table-row>										
															</fo:table-body>
														</fo:table>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>						
									<fo:table-row>
									<fo:table-cell>
									<fo:block text-align="left" keep-together="always" white-space-collapse="false">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
									</fo:table-cell>
						            </fo:table-row>		
							</#if>
							</#list>
			
				            <#assign grandProdTotals = grandProdTotals>
			                <#if grandProdTotals?has_content>
				            <fo:table-row>
							<fo:table-cell>
									<fo:block keep-together="always">Grand Total</fo:block>
							</fo:table-cell>
							<fo:table-cell>
									<fo:block >
										<fo:table>
											<#list lmsProductList as product>
												<fo:table-column column-width="50pt"/>
											</#list>	
											<#list byProdList as byProd>
												<fo:table-column column-width="50pt"/>
											</#list>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-body>	
											<fo:table-row>
											<#assign columnCounter = 0>
											<#list lmsProductList as product>
												<#assign columnCounter = columnCounter+1>
												 <#if (columnCounter > 15) > <#--  11 products for each row and if morethan 11 then we will wrap to next line -->
														<#assign columnCounter =1>
														</fo:table-row>	
														<fo:table-row>
												</#if>
												<#assign qty=0>
												<#if grandProdTotals.get(product)?has_content>
															<#assign qty= grandProdTotals.get(product).get("total")>
												</#if>
												<#if qty !=0>
													<fo:table-cell>		
														<fo:block text-align="right">${qty?if_exists}</fo:block>																									
													</fo:table-cell>
												<#else>
													<fo:table-cell>		
														<fo:block text-align="right" linefeed-treatment="preserve">-</fo:block>																									
													</fo:table-cell>
												</#if>
											</#list>
											<#list byProdList as product>
											<#assign columnCounter = columnCounter+1>
											   <#if (columnCounter > 15) > <#--  11 products for each row and if morethan 11 then we will wrap to next line -->
														<#assign columnCounter =1>
														</fo:table-row>	
														<fo:table-row>
												</#if>
												<#assign byProdQty=0>
												<#if grandProdTotals.get(product)?has_content>
															<#assign byProdQty= grandProdTotals.get(product).get("total")>
												</#if>
												<#if byProdQty !=0>
													<fo:table-cell>		
														<fo:block text-align="right">${byProdQty?if_exists}</fo:block>																									
													</fo:table-cell>
												<#else>
													<fo:table-cell>		
														<fo:block text-align="right" linefeed-treatment="preserve">-</fo:block>																									
													</fo:table-cell>
												</#if>
											</#list>
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
													<#--
													<fo:table-cell>
														<fo:block text-align="center">${(routeTotQty)?if_exists}</fo:block>
													</fo:table-cell> -->
													<fo:table-cell>
														<fo:block text-align="center">${(routeAmount)?if_exists?string("#0.00")}</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="center">${(reciepts)?if_exists?string("#0.00")}</fo:block>
													</fo:table-cell>
													<#--<fo:table-cell/> -->
													<fo:table-cell>
														<fo:block text-align="center">${((routeAmount)-(reciepts))?if_exists?string("#0.00")} VST_ASCII-027VST_ASCII-070</fo:block>
													</fo:table-cell>
												</fo:table-row>										
											</fo:table-body>
										</fo:table>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>						
		                  </#if>
						</fo:table-body>
					</fo:table>
				</fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
    	</fo:flow>
	</fo:page-sequence>
	</#if>
<#else>	
	<fo:page-sequence master-reference="main">
    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
       		 <fo:block font-size="14pt">
            	${uiLabelMap.OrderNoOrderFound}.
       		 </fo:block>
    	</fo:flow>
	</fo:page-sequence>
</#if>		
</#if>
     </fo:root>
</#escape>