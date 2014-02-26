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
            margin-top="0.3in" margin-bottom=".3in" margin-left=".3in" margin-right=".3in">
        <fo:region-body margin-top="1.6in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
	
<#if routeWiseMap?has_content> 
	<#assign routeDetailsList =routeWiseMap.entrySet()>
	<#assign numberOfLines = 60>
	<#list routeDetailsList as routesMap>
	<#assign routeTotalCrates = routeWiseTotalCrates.get(routesMap.getKey())>
	<fo:page-sequence master-reference="main" force-page-count="no-force">					
		<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="7pt">
			<fo:block text-align="center" keep-together="always" white-space-collapse="false">VST_ASCII-015   KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD.</fo:block>
			<fo:block text-align="center" keep-together="always" white-space-collapse="false">          UNIT : MOTHER DAIRY:G.K.V.K POST : YELAHANKA:BANGALORE : 560065</fo:block>
			<fo:block text-align="center" keep-together="always" white-space-collapse="false">VST_ASCII-027VST_ASCII-069<fo:inline text-decoration="underline">                           GATEPASS CUM DISTRIBUTION ROUTESHEET : SACHETS</fo:inline>VST_ASCII-027VST_ASCII-070</fo:block>
			<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : routesMap.getKey()}, true)>
			<fo:block text-align="left" keep-together="always" white-space-collapse="false">=============================================================================================================================================================================================================================</fo:block>
			 <fo:block>
			            <fo:table width="100%" table-layout="fixed" space-after="0.0in">
			             <fo:table-column column-width="300pt"/>
			              <fo:table-column column-width="300pt"/>
			               <fo:table-column column-width="300pt"/>
			               <fo:table-body>
			                 <fo:table-row>
				                   <fo:table-cell>
				                         <fo:block  text-indent="15pt">ROUTE NUMBER:${facilityDetails.get("facilityId")}</fo:block>
				                         <fo:block  text-indent="15pt">VEH NUMBER:${routesMap.getValue().get("vehicleId")?if_exists}</fo:block>
				                         <fo:block  text-indent="15pt">CONTRACTOR ID:${facilityDetails.get("ownerPartyId")}</fo:block>
				                          <fo:block  text-indent="15pt">CONTRACTOR NAME:${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, facilityDetails.get("ownerPartyId"), false)}</fo:block>
				                     </fo:table-cell>
				                      <fo:table-cell>
				                         <fo:block>SHIFT/TRIP:<#if parameters.shipmentTypeId="AM_SHIPMENT">MORNING<#elseif  parameters.shipmentTypeId="PM_SHIPMENT">EVENING</#if></fo:block>
				                          <fo:block>DESPATCH DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(estimatedDeliveryDate, "dd/MM/yyyy")}</fo:block>
				                          <fo:block>DESPATCH TIME:</fo:block>
				                  </fo:table-cell>
				                    <fo:table-cell>
				                         <fo:block>G.P.NUMBER:</fo:block>
				                         <fo:block>G.P.DATE:${Static["org.ofbiz.base.util.UtilDateTime"].nowDateString("dd/MM/yyyy")}</fo:block>
				                          <fo:block>G.P.TIME:${Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()}</fo:block>
				                   </fo:table-cell>
			                     </fo:table-row>
			                     </fo:table-body>
			                    </fo:table>
			              </fo:block>         
			<fo:block text-align="left" keep-together="always" white-space-collapse="false">=============================================================================================================================================================================================================================</fo:block>
			<#assign contractorName= (routesMap.getValue().get("contractorName"))>	
			<#assign lmsProdList = (routesMap.getValue().get("lmsProdList"))>
			<#assign byProdList = (routesMap.getValue().get("byProdList"))>
			<#assign totalCrate = 0>
			<fo:block font-size="7pt">
				<fo:table>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="760pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="60pt"/>
					<fo:table-body>
					<#-- 
						<fo:table-row>
							<fo:table-cell><fo:block></fo:block></fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="7pt" text-align="center" keep-together="always" white-space-collapse="false">N A N D I N I    M I L K   AND   M I L K   P R O D U C T S</fo:block>
							</fo:table-cell>
							<fo:table-cell><fo:block></fo:block></fo:table-cell>
							<fo:table-cell><fo:block></fo:block></fo:table-cell>
						</fo:table-row> -->
						<fo:table-row>
							<fo:table-cell>
								<fo:block font-size="7pt" keep-together="always" white-space-collapse="false">Code-Dealer Name</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="7pt" keep-together="always" white-space-collapse="false">---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="7pt" text-align="left">NET Receivable</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="7pt" text-align="left">PaymentType</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell><fo:block></fo:block></fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="7pt">
									<fo:table>
										<#assign colCounter = 0>
										<#list lmsProdList as product>
											<#assign colCounter = colCounter+1>
											<fo:table-column column-width="40pt"/>
										</#list>
										<#if (colCounter <20)>
											<#assign colCounter = colCounter+1>
											<fo:table-column column-width="40pt"/>
										</#if>
										<#if (colCounter <20)>
											<#assign colCounter = colCounter+1>
											<fo:table-column column-width="40pt"/>
										</#if>
										<#list byProdList as product>
											<#if (colCounter <20)>
												<#assign colCounter = colCounter+1>
												<fo:table-column column-width="40pt"/>
											</#if>
										</#list>
										
											
										<fo:table-body>
											<#assign counter =0>
											<#assign lmsProdCounter =0>
											<#assign byProdCounter =0>
											<#assign productSize = lmsProdList.size()+byProdList.size()>
											<fo:table-row>
												<fo:table-cell>
													<fo:block>VST_ASCII-027VST_ASCII-069</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<fo:table-row>
												<#list lmsProdList as product>
													<#assign counter = counter+1>
													<#if (counter<20)>
														<#assign lmsProdCounter = lmsProdCounter+1>
														<fo:table-cell>	
															<fo:block>${productNames.get(product)?if_exists}</fo:block>																									
														</fo:table-cell>
													</#if>
												</#list>
												<#if (counter < 20)>
													<#assign counter = counter+1>
													<fo:table-cell>
														<fo:block font-size="7pt">Crates</fo:block>
													</fo:table-cell>
												</#if>
												<#if (counter < 20) >
													<#assign counter = counter+1>
													<fo:table-cell>
														<fo:block font-size="7pt">Cans</fo:block>
													</fo:table-cell>
												</#if>
												<#list byProdList as product>
													<#assign counter = counter+1>
													<#if (counter < 20) >
														<#assign byProdCounter = byProdCounter+1>
														<fo:table-cell>
															<fo:block>${productNames.get(product)?if_exists}</fo:block>
														</fo:table-cell>
													</#if>
												</#list>
												
											</fo:table-row>	
											<#if (productSize > lmsProdCounter+byProdCounter)>
												<fo:table-row>
													<#assign counter = 0>
													<#assign prodCount = 0>
													<#list byProdList as product>
														<#assign counter = counter+1>
														<#if (counter > byProdCounter && prodCount < 19) >
															<#assign byProdCounter = byProdCounter+1>
															<#assign prodCount = prodCount + 1>
															<fo:table-cell>
																<fo:block>${productNames.get(product)?if_exists}</fo:block>
															</fo:table-cell>
														</#if>
													</#list>
												</fo:table-row>
											</#if>
											<#if (productSize > lmsProdCounter+byProdCounter)>
												<fo:table-row>
													<#assign counter = 0>
													<#assign prodCount = 0>
													<#list byProdList as product>
														<#assign counter = counter+1>
														<#if (counter > byProdCounter && prodCount < 19) >
															<#assign byProdCounter = byProdCounter+1>
															<#assign prodCount = prodCount + 1>
															<fo:table-cell>
																<fo:block>${productNames.get(product)?if_exists}</fo:block>
															</fo:table-cell>
														</#if>
													</#list>
												</fo:table-row>
											</#if>
											<#--<fo:table-row>
												<fo:table-cell>
													<fo:block>VST_ASCII-027VST_ASCII-070</fo:block>
												</fo:table-cell>
											</fo:table-row>-->	
									</fo:table-body>
								</fo:table>
							</fo:block>
							</fo:table-cell>
							<fo:table-cell><fo:block></fo:block></fo:table-cell>
							<fo:table-cell><fo:block></fo:block></fo:table-cell>
						</fo:table-row>						
					</fo:table-body>
				</fo:table>
			</fo:block>
			<fo:block text-align="left" keep-together="always" white-space-collapse="false">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		</fo:static-content>
		<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" font-size="7pt">
		
    		<#assign boothDetailList = routesMap.getValue().get("boothWiseMap").entrySet()>
    		<#assign noOfbooths=0>
    		<#list boothDetailList as boothDetails>    	
    		<#assign noOfbooths=noOfbooths+1>	
    		<#if (noOfbooths==12) >
    			<fo:block  break-after="page"></fo:block>
    			<#assign noOfbooths=0>
    		</#if>
    		<fo:block>
				<fo:table>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="760pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="60pt"/>
					<fo:table-body>
						<fo:table-row>
							<#assign facility = delegator.findOne("Facility", {"facilityId" : boothDetails.getKey()}, true)>
							<fo:table-cell><fo:block font-size="5pt" keep-together="always">${boothDetails.getKey()}- ${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.facilityName?if_exists)),10)}</fo:block></fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="7pt">
									<fo:table>
										<#assign colCounter = 0>
										<#list lmsProdList as product>
											<#assign colCounter = colCounter+1>
											<fo:table-column column-width="40pt"/>
										</#list>
										<#if (colCounter <20)>
											<#assign colCounter = colCounter+1>
											<fo:table-column column-width="40pt"/>
										</#if>
										<#if (colCounter <20)>
											<#assign colCounter = colCounter+1>
											<fo:table-column column-width="40pt"/>
										</#if>
										<#list byProdList as product>
											<#if (colCounter <20)>
												<#assign colCounter = colCounter+1>
												<fo:table-column column-width="40pt"/>
											</#if>
										</#list>
										
											
										<fo:table-body>
											<#assign counter =0>
											<#assign lmsProdCounter =0>
											<#assign byProdCounter =0>
											<#assign productSize = lmsProdList.size()+byProdList.size()>
											<fo:table-row>
												<#list lmsProdList as product>
													<#assign counter = counter+1>
													<#if (counter<20)>
														<#assign lmsProdCounter = lmsProdCounter+1>
														<#assign qty=0>
														<#if boothDetails.getValue().get("prodDetails").get(product)?has_content>
															<#assign qty= boothDetails.getValue().get("prodDetails").get(product).get("packetQuantity")>
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
													</#if>
												</#list>
												<#if (counter < 20)>
													<#assign counter = counter+1>
													<fo:table-cell>
														<fo:block font-size="7pt"><#if (boothDetails.getValue().get("crates")+boothDetails.getValue().get("excess")) !=0>${boothDetails.getValue().get("crates")?if_exists}+${boothDetails.getValue().get("excess")?if_exists}</#if></fo:block>
													</fo:table-cell>
												</#if>
												<#if (counter < 20) >
													<#assign counter = counter+1>
													<fo:table-cell>
														<fo:block font-size="7pt"></fo:block>
													</fo:table-cell>
												</#if>
												<#list byProdList as product>
													<#assign counter = counter+1>
													<#if (counter < 20) >
														<#assign byProdCounter = byProdCounter+1>
														<#assign byProdQty=0>
														<#if boothDetails.getValue().get("prodDetails").get(product)?has_content>
															<#assign byProdQty= boothDetails.getValue().get("prodDetails").get(product).get("packetQuantity")>
														</#if>
														<#if byProdQty !=0>
															<fo:table-cell>		
																<fo:block text-align="center">${(byProdQty)?if_exists}</fo:block>																									
															</fo:table-cell>
														<#else>
															<fo:table-cell>		
																<fo:block text-align="center" linefeed-treatment="preserve">&#160;</fo:block>																									
															</fo:table-cell>
														</#if>
													</#if>
												</#list>
												
											</fo:table-row>	
											<#if (productSize > lmsProdCounter+byProdCounter)>
												<fo:table-row>
													<#assign counter = 0>
													<#assign prodCount = 0>
													<#list byProdList as product>
														<#assign counter = counter+1>
														<#if (counter > byProdCounter && prodCount < 19) >
															<#assign byProdCounter = byProdCounter+1>
															<#assign prodCount = prodCount + 1>
															<#assign byProdQty=0>
															<#if boothDetails.getValue().get("prodDetails").get(product)?has_content>
																<#assign byProdQty= boothDetails.getValue().get("prodDetails").get(product).get("packetQuantity")>
															</#if>
															<#if byProdQty !=0>
																<fo:table-cell>		
																	<fo:block text-align="center">${byProdQty?if_exists}</fo:block>																									
																</fo:table-cell>
															<#else>
																<fo:table-cell>		
																	<fo:block text-align="center" linefeed-treatment="preserve">&#160;</fo:block>																									
																</fo:table-cell>
															</#if>
														</#if>
													</#list>
												</fo:table-row>
											</#if>
											<#if (productSize > lmsProdCounter+byProdCounter)>
												<fo:table-row>
													<#assign counter = 0>
													<#assign prodCount = 0>
													<#list byProdList as product>
														<#assign counter = counter+1>
														<#if (counter > byProdCounter && prodCount < 19) >
															<#assign byProdCounter = byProdCounter+1>
															<#assign prodCount = prodCount + 1>
															<#assign byProdQty=0>
															<#if boothDetails.getValue().get("prodDetails").get(product)?has_content>
																<#assign byProdQty= boothDetails.getValue().get("prodDetails").get(product).get("packetQuantity")>
															</#if>
															<#if byProdQty !=0>
																<fo:table-cell>		
																	<fo:block text-align="center">${byProdQty?if_exists}</fo:block>																									
																</fo:table-cell>
															<#else>
																<fo:table-cell>		
																	<fo:block text-align="center" linefeed-treatment="preserve">&#160;</fo:block>																									
																</fo:table-cell>
															</#if>
														</#if>
													</#list>
												</fo:table-row>
											</#if>	
									</fo:table-body>
								</fo:table>
							</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="left">${boothDetails.getValue().get("amount")?if_exists?string("#0.00")}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="5pt" text-align="left"><fo:block font-size="7pt" text-align="left">${boothDetails.getValue().get("paymentMode")?if_exists}</fo:block></fo:block>
							</fo:table-cell>
						</fo:table-row>	
					</fo:table-body>
				</fo:table>
			</fo:block>
			<fo:block text-align="left" keep-together="always" white-space-collapse="false">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		</#list>
		<#assign routeTotals = (routesMap.getValue().get("routeWiseTotals"))>
		<fo:block>
			<fo:table>
				<fo:table-column column-width="80pt"/>
				<fo:table-column column-width="760pt"/>
				<fo:table-column column-width="80pt"/>
				<fo:table-column column-width="20pt"/>
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell>
							<fo:block>VST_ASCII-027VST_ASCII-069</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell>
							<fo:block font-size="7pt" keep-together="always">TOTAL</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block font-size="7pt">
								<fo:table>
									<#assign colCounter = 0>
									<#list lmsProdList as product>
										<#assign colCounter = colCounter+1>
										<fo:table-column column-width="40pt"/>
									</#list>
									<#if (colCounter <20)>
										<#assign colCounter = colCounter+1>
										<fo:table-column column-width="40pt"/>
									</#if>
									<#if (colCounter <20)>
										<#assign colCounter = colCounter+1>
										<fo:table-column column-width="40pt"/>
									</#if>
									<#list byProdList as product>
										<#if (colCounter <20)>
											<#assign colCounter = colCounter+1>
											<fo:table-column column-width="40pt"/>
										</#if>
									</#list>
									
									<fo:table-body>
										<#assign counter =0>
										<#assign lmsProdCounter =0>
										<#assign byProdCounter =0>
										<#assign productSize = lmsProdList.size()+byProdList.size()>
										<fo:table-row>
											<#list lmsProdList as product>
												<#assign counter = counter+1>
												<#if (counter<20)>
													<#assign lmsProdCounter = lmsProdCounter+1>
													<#assign qty=0>
													<#if routeTotals.get(product)?has_content>
														<#assign qty= routeTotals.get(product).get("packetQuantity")>
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
												</#if>
											</#list>
											<#if (counter < 20)>
												<#assign counter = counter+1>
												<fo:table-cell>
													<fo:block font-size="7pt">${(routesMap.getValue().get("rtCrates"))?if_exists}+${(routesMap.getValue().get("rtExcessPkts"))?if_exists}</fo:block>
												</fo:table-cell>
											</#if>
											<#if (counter < 20) >
												<#assign counter = counter+1>
												<fo:table-cell>
													<fo:block font-size="7pt"></fo:block>
												</fo:table-cell>
											</#if>
											<#list byProdList as byProd>
												<#assign counter = counter+1>
												<#if (counter < 20) >
													<#assign byProdCounter = byProdCounter+1>
													<#assign byProdQty=0>
													<#if routeTotals.get(byProd)?has_content>
														<#assign byProdQty= routeTotals.get(byProd).get("packetQuantity")>
													</#if>
													<#if byProdQty !=0>
														<fo:table-cell>		
															<fo:block text-align="center">${byProdQty?if_exists}</fo:block>																									
														</fo:table-cell>
													<#else>
														<fo:table-cell>		
															<fo:block text-align="center" linefeed-treatment="preserve">&#160;</fo:block>																									
														</fo:table-cell>
													</#if>	
												</#if>
											</#list>
											
										</fo:table-row>
										<#if (productSize > lmsProdCounter+byProdCounter)>
											<fo:table-row>
												<#assign counter = 0>
												<#assign prodCount = 0>
												<#list byProdList as byProd>
													<#assign counter = counter+1>
													<#if (counter > byProdCounter && prodCount < 19) >
														<#assign byProdCounter = byProdCounter+1>
														<#assign prodCount = prodCount + 1>
														<#assign byProdQty=0>
														<#if routeTotals.get(byProd)?has_content>
															<#assign byProdQty= routeTotals.get(byProd).get("packetQuantity")>
														</#if>
														<#if byProdQty !=0>
															<fo:table-cell>		
																<fo:block text-align="center">${byProdQty?if_exists}</fo:block>																									
															</fo:table-cell>
														<#else>
															<fo:table-cell>		
																<fo:block text-align="center" linefeed-treatment="preserve">&#160;</fo:block>																									
															</fo:table-cell>
														</#if>	
													</#if>
												</#list>
											</fo:table-row>
										</#if>
										<#if (productSize > lmsProdCounter+byProdCounter)>
											<fo:table-row>
												<#assign counter = 0>
												<#assign prodCount = 0>
												<#list byProdList as byProd>
													<#assign counter = counter+1>
													<#if (counter > byProdCounter && prodCount < 19) >
														<#assign byProdCounter = byProdCounter+1>
														<#assign prodCount = prodCount + 1>
														<#assign byProdQty=0>
														<#if routeTotals.get(byProd)?has_content>
															<#assign byProdQty= routeTotals.get(byProd).get("packetQuantity")>
														</#if>
														<#if byProdQty !=0>
															<fo:table-cell>		
																<fo:block text-align="center">${byProdQty?if_exists}</fo:block>																									
															</fo:table-cell>
														<#else>
															<fo:table-cell>		
																<fo:block text-align="center" linefeed-treatment="preserve">&#160;</fo:block>																									
															</fo:table-cell>
														</#if>
													</#if>
												</#list>
											</fo:table-row>
										</#if>
									</fo:table-body>
								</fo:table>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block font-size="7pt" text-align="left">${(routesMap.getValue().get("routeAmount"))?if_exists?string("#0.00")}</fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block></fo:block></fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell>
							<fo:block text-align="left" keep-together="always" white-space-collapse="false">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell>
							<fo:block font-size="7pt" keep-together="always">Crates</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block font-size="7pt">
								<fo:table>
									<#assign colCounter = 0>
									<#list lmsProdList as product>
										<#assign colCounter = colCounter+1>
										<fo:table-column column-width="40pt"/>
									</#list>
									<#if (colCounter <20)>
										<#assign colCounter = colCounter+1>
										<fo:table-column column-width="40pt"/>
									</#if>
									<#if (colCounter <20)>
										<#assign colCounter = colCounter+1>
										<fo:table-column column-width="40pt"/>
									</#if>
									<#list byProdList as product>
										<#if (colCounter <20)>
											<#assign colCounter = colCounter+1>
											<fo:table-column column-width="40pt"/>
										</#if>
									</#list>
									
									<fo:table-body>
										<#assign counter =0>
										<#assign lmsProdCounter =0>
										<#assign byProdCounter =0>
										<#assign productSize = lmsProdList.size()+byProdList.size()>
										<fo:table-row>
											<#list lmsProdList as product>
												<#assign counter = counter+1>
												<#if (counter<20)>
													<#assign lmsProdCounter = lmsProdCounter+1>
													<#assign qty=0>
													<#assign crateDetail = routeTotalCrates.get(product)>
													<#if crateDetail?has_content>
														<#assign qty = crateDetail.get('prodCrates')>
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
												</#if>
											</#list>
											<#if (counter < 20)>
												<#assign counter = counter+1>
												<fo:table-cell>
													<fo:block font-size="7pt">${(routesMap.getValue().get("rtCrates"))?if_exists}</fo:block>
													<#assign totalCrate=routesMap.getValue().get("rtCrates")>
												</fo:table-cell>
											</#if>
											<#if (counter < 20) >
												<#assign counter = counter+1>
												<fo:table-cell>
													<fo:block font-size="7pt"></fo:block>
												</fo:table-cell>
											</#if>
											<#list byProdList as byProd>
												<#assign counter = counter+1>
												<#if (counter < 20) >
													<#assign byProdCounter = byProdCounter+1>
													<#assign qty=0>
													<#assign crateDetail = routeTotalCrates.get(byProd)>
													<#if crateDetail?has_content>
														<#assign qty = crateDetail.get('prodCrates')>
													</#if>
													<#if qty !=0>
														<#--<#assign product = (delegator.findOne("Product", {"productId" : byProd}, false))!>-->
															
														<fo:table-cell>		
															<fo:block text-align="center">${qty?if_exists}</fo:block>																									
														</fo:table-cell>
													<#else>
														<fo:table-cell>		
															<fo:block text-align="center" linefeed-treatment="preserve">&#160;</fo:block>																									
														</fo:table-cell>
													</#if>
												</#if>
											</#list>
									</fo:table-row>
									<#if (productSize > lmsProdCounter+byProdCounter)>
										<fo:table-row>
											<#assign counter = 0>
											<#assign prodCount = 0>
											<#list byProdList as byProd>
												<#assign counter = counter+1>
												<#if (counter > byProdCounter && prodCount < 19) >
													<#assign byProdCounter = byProdCounter+1>
													<#assign prodCount = prodCount + 1>
													<#assign qty=0>
													<#assign crateDetail = routeTotalCrates.get(byProd)>
													<#if crateDetail?has_content>
														<#assign qty = crateDetail.get('prodCrates')>
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
												</#if>
											</#list>
										</fo:table-row>
									</#if>
									<#if (productSize > lmsProdCounter+byProdCounter)>
										<fo:table-row>
											<#assign counter = 0>
											<#assign prodCount = 0>
											<#list byProdList as byProd>
												<#assign counter = counter+1>
												<#if (counter > byProdCounter && prodCount < 19)>
													<#assign byProdCounter = byProdCounter+1>
													<#assign prodCount = prodCount + 1>
													<#assign qty=0>
													<#assign crateDetail = routeTotalCrates.get(byProd)>
													<#if crateDetail?has_content>
														<#assign qty = crateDetail.get('prodCrates')>
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
												</#if>
											</#list>
											
										</fo:table-row>
									</#if>
									</fo:table-body>
								</fo:table>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block font-size="7pt" text-align="right"></fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block></fo:block></fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell>
							<fo:block text-align="left" keep-together="always" white-space-collapse="false">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell>
							<fo:block font-size="7pt" keep-together="always">Loose(Ltr/Kgs)</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block font-size="7pt">
								<fo:table>
									<#assign colCounter = 0>
									<#list lmsProdList as product>
										<#assign colCounter = colCounter+1>
										<fo:table-column column-width="40pt"/>
									</#list>
									<#if (colCounter <20)>
										<#assign colCounter = colCounter+1>
										<fo:table-column column-width="40pt"/>
									</#if>
									<#if (colCounter <20)>
										<#assign colCounter = colCounter+1>
										<fo:table-column column-width="40pt"/>
									</#if>
									<#list byProdList as product>
										<#if (colCounter <20)>
											<#assign colCounter = colCounter+1>
											<fo:table-column column-width="40pt"/>
										</#if>
									</#list>
									
									<fo:table-body>
										<#assign counter =0>
										<#assign lmsProdCounter =0>
										<#assign byProdCounter =0>
										<#assign productSize = lmsProdList.size()+byProdList.size()>
										<fo:table-row>
											<#list lmsProdList as product>
												<#assign counter = counter+1>
												<#if (counter<20)>
													<#assign lmsProdCounter = lmsProdCounter+1>
													<#assign qty=0>
													<#assign crateDetail = routeTotalCrates.get(product)>
													<#if crateDetail?has_content>
														<#assign qty = crateDetail.get('packetsExces')>
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
												</#if>
											</#list>
											<#if (counter < 20)>
												<#assign counter = counter+1>
												<fo:table-cell>
													<fo:block font-size="7pt">${(routesMap.getValue().get("rtExcessPkts"))?if_exists}</fo:block>
												</fo:table-cell>
											</#if>
											<#if (counter < 20) >
												<#assign counter = counter+1>
												<fo:table-cell>
													<fo:block font-size="7pt"></fo:block>
												</fo:table-cell>
											</#if>
											<#list byProdList as byProd>
												<#assign counter = counter+1>
												<#if (counter < 20) >
													<#assign byProdCounter = byProdCounter+1>
													<#assign qty=0>
													<#assign crateDetail = routeTotalCrates.get(byProd)>
													<#if crateDetail?has_content>
														<#assign qty = crateDetail.get('packetsExces')>
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
												</#if>
											</#list>
										
									</fo:table-row>
									<#if (productSize > lmsProdCounter+byProdCounter)>
										<fo:table-row>
											<#assign counter = 0>
											<#assign prodCount = 0>
											<#list byProdList as byProd>
												<#assign counter = counter+1>
												<#if (counter > byProdCounter && prodCount < 19) >
													<#assign byProdCounter = byProdCounter+1>
													<#assign prodCount = prodCount + 1>
													<#assign qty=0>
													<#assign crateDetail = routeTotalCrates.get(byProd)>
													<#if crateDetail?has_content>
														<#assign qty = crateDetail.get('packetsExces')>
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
												</#if>
											</#list>
										
										</fo:table-row>
									</#if>
									<#if (productSize > lmsProdCounter+byProdCounter)>
										<fo:table-row>
											<#assign counter = 0>
											<#assign prodCount = 0>
											<#list byProdList as byProd>
												<#assign counter = counter+1>
												<#if (counter > byProdCounter && prodCount < 19) >
													<#assign byProdCounter = byProdCounter+1>
													<#assign prodCount = prodCount + 1>
													<fo:table-cell>		
														<fo:block text-align="center" linefeed-treatment="preserve">&#160;</fo:block>																									
													</fo:table-cell>
												</#if>
											</#list>
										
										</fo:table-row>
									</#if>
									</fo:table-body>
								</fo:table>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block font-size="7pt" text-align="right"></fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block></fo:block></fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell>
							<fo:block>VST_ASCII-027VST_ASCII-070</fo:block>
						</fo:table-cell>
					</fo:table-row>	
					</fo:table-body>
				</fo:table>
			</fo:block>
			<fo:block text-align="left" keep-together="always" white-space-collapse="false">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			<fo:block> 
				<fo:table>
					<fo:table-column column-width="400pt"/>
					<fo:table-column column-width="200pt"/>
					<fo:table-column column-width="200pt"/>						
					<fo:table-body>
					<#--
			           <fo:table-row>
							<fo:table-cell>
								<fo:block font-size="7pt" text-align="left">Total Crates -> ${totalCrate?if_exists}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="7pt" text-align="left">Total Cans -> </fo:block>
							</fo:table-cell>
						</fo:table-row> -->
						<fo:table-row>
							<fo:table-cell>
								<fo:block font-size="7pt" text-align="left">&#160;</fo:block>
								<fo:block font-size="7pt" text-align="left">&#160;</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="left">&#160;</fo:block>
								<fo:block font-size="7pt" text-align="left">&#160;</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="left">&#160;</fo:block>
								<fo:block font-size="7pt" text-align="left">&#160;</fo:block>
							</fo:table-cell>
						</fo:table-row>
						
						<fo:table-row>
							<fo:table-cell>
								<fo:block font-size="7pt" text-align="left">CreatedBy:${userLogin.userLoginId}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="left">DESPATCHED BY</fo:block>
								<fo:block font-size="6pt" text-align="left">NAME&amp;SIGNATURE</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="left">CHECKED BY SECURITY</fo:block>
								<fo:block font-size="6pt" text-align="left">NAME&amp;SIGNATURE</fo:block>
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