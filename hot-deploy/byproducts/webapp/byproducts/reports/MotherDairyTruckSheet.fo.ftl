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
            margin-top="0.2in" margin-bottom=".3in" margin-left=".3in" margin-right=".3in">
        <fo:region-body margin-top="1.7in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
	${setRequestAttribute("OUTPUT_FILENAME", "gatepass.txt")}
<#if routeWiseMap?has_content> 
	<#assign routeDetailsList =routeWiseMap.entrySet()>
	<#assign numberOfLines = 60>
	<#list routeDetailsList as routesMap>
	<#assign routeTotalCrates = routeWiseTotalCrates.get(routesMap.getKey()?if_exists)>
	<fo:page-sequence master-reference="main" force-page-count="no-force">					
		<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="6pt">
			<fo:block> ${uiLabelMap.CommonPage} <fo:page-number/></fo:block>
			<fo:block text-align="left" keep-together="always" white-space-collapse="false">VST_ASCII-015      &#160;                                                             KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD.</fo:block>
			<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                                                                       UNIT : MOTHER DAIRY:G.K.V.K POST : YELAHANKA:BANGALORE : 560065</fo:block>
			<fo:block text-align="left" keep-together="always" white-space-collapse="false">VST_ASCII-027VST_ASCII-069<fo:inline text-decoration="underline">&#160;                                                                GATEPASS CUM DISTRIBUTION ROUTESHEET : SACHETS</fo:inline>VST_ASCII-027VST_ASCII-070</fo:block>
			<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : routesMap.getKey()}, true)>
			<fo:block text-align="left" keep-together="always" white-space-collapse="false">=================================================================================================================================================================================================================================</fo:block>
			 <fo:block>
			            <fo:table width="100%" table-layout="fixed" space-after="0.0in">
			             <fo:table-column column-width="260pt"/>
			              <fo:table-column column-width="250pt"/>
			               <fo:table-column column-width="250pt"/>
			               <fo:table-body>
			                 <fo:table-row>
				                   <fo:table-cell>
				                         <fo:block  text-indent="15pt" font-size="11pt">ROUTE NUMBER:&#160;&#160;${facilityDetails.get("facilityId")}</fo:block>
				                         <fo:block  text-indent="15pt">VEH NUMBER:&#160;${routesMap.getValue().get("vehicleId")?if_exists}</fo:block>
				                         <fo:block  text-indent="15pt">CONTRACTOR ID:&#160;${facilityDetails.get("ownerPartyId")}</fo:block>
				                          <fo:block  text-indent="15pt">CONTRACTOR NAME:${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, facilityDetails.get("ownerPartyId"), false)}</fo:block>
				                     </fo:table-cell>
				                      <fo:table-cell>
				                         <fo:block>SHIFT/TRIP:<#if parameters.shipmentTypeId="AM_SHIPMENT">MORNING<#elseif  parameters.shipmentTypeId="PM_SHIPMENT">EVENING</#if></fo:block>
				                          <fo:block>DESPATCH DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(estimatedDeliveryDate, "dd/MM/yyyy")}</fo:block>
				                          <fo:block>DESPATCH TIME:</fo:block>
				                  </fo:table-cell>
				                    <fo:table-cell>
				                         <fo:block>G.P.NUMBER: ${routeShipmentMap.get(facilityDetails.get("facilityId"))?if_exists}</fo:block>
				                         <fo:block>G.P.DATE:${Static["org.ofbiz.base.util.UtilDateTime"].nowDateString("dd/MM/yyyy")}</fo:block>
				                          <fo:block>G.P.TIME:${Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()}</fo:block>
				                   </fo:table-cell>
			                     </fo:table-row>
			                     </fo:table-body>
			                    </fo:table>
			              </fo:block>         
			<fo:block text-align="left" keep-together="always" white-space-collapse="false">=================================================================================================================================================================================================================================</fo:block>
			<#assign contractorName= (routesMap.getValue().get("contractorName"))>	
			<#assign lmsProdList = (routesMap.getValue().get("lmsProdList"))>
			<#assign byProdList = (routesMap.getValue().get("byProdList"))>
			<#assign totalCrate = 0>
			<fo:block font-size="6pt">
				<fo:table>
					<fo:table-column column-width="78pt"/>
					<fo:table-column column-width="530pt"/>
					<fo:table-column column-width="25pt"/>
					<fo:table-column column-width="37pt"/>
					<fo:table-column column-width="50pt"/>
					<fo:table-column column-width="50pt"/>
					<fo:table-column column-width="55pt"/>
					<fo:table-body>
					<#-- 
						<fo:table-row>
							<fo:table-cell><fo:block></fo:block></fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="center" keep-together="always" white-space-collapse="false">N A N D I N I    M I L K   AND   M I L K   P R O D U C T S</fo:block>
							</fo:table-cell>
							<fo:table-cell><fo:block></fo:block></fo:table-cell>
							<fo:table-cell><fo:block></fo:block></fo:table-cell>
						</fo:table-row> -->
						<fo:table-row>
							<fo:table-cell>
								<fo:block font-size="6pt" keep-together="always" white-space-collapse="false">Code-Dealer Name</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt" keep-together="always" white-space-collapse="false">------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="right">Subsidy</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="right">VAT</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="right">NET Receivable</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="right">ShopeeRent</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="right">PaymentType</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell><fo:block></fo:block></fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt">
									<fo:table>
										<#assign colCounter = 0>
										<#list lmsProdList as product>
										 <fo:table-column column-width="47pt"/>
									    </#list>
									    <#list byProdList as product>
										 <fo:table-column column-width="47pt"/>
									    </#list>
									    <#-- 
										<fo:table-column column-width="35pt"/>
										<fo:table-column column-width="35pt"/> -->
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
												<#assign columnCounter =0>
												<#list lmsProdList as product>
														<#assign columnCounter = columnCounter+1>
														<#if (columnCounter > 11) > <#--  11 products for each row and if morethan 11 then we will wrap to next line -->
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
													<#if (columnCounter > 11) > <#--  11 products for each row and if morethan 11 then we will wrap to next line -->
														<#assign columnCounter =1>
														</fo:table-row>	
														<fo:table-row>
													    </#if>
													<fo:table-cell>
															<fo:block text-align="right">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(productNames.get(product)?if_exists)),8)} </fo:block>
													</fo:table-cell>
												</#list>
												<#-- 
												<fo:table-cell>
													<fo:block text-align="left"  font-size="6pt">Crates</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block  text-align="center" font-size="6pt">Cans</fo:block>
												</fo:table-cell> -->
											</fo:table-row>	
											<fo:table-row>
												<fo:table-cell>
													<fo:block>VST_ASCII-027VST_ASCII-070</fo:block>
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
			<fo:block text-align="left" keep-together="always" white-space-collapse="false">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		</fo:static-content>
		<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" font-size="6pt">
    		<#assign boothDetailList = routesMap.getValue().get("boothWiseMap").entrySet()>
    		<#assign noOfbooths=0>
    		<#list boothDetailList as boothDetails>    	
    		<#assign noOfbooths=noOfbooths+1>	
    		<#if (noOfbooths==11) >
    			<fo:block  break-after="page"></fo:block>
    			<#assign noOfbooths=0>
    		</#if>
    		<fo:block font-size="6pt">
				<fo:table>
				<fo:table-column column-width="78pt"/>
					<fo:table-column column-width="530pt"/>
					<fo:table-column column-width="25pt"/>
					<fo:table-column column-width="37pt"/>
					<fo:table-column column-width="50pt"/>
					<fo:table-column column-width="50pt"/>
					<fo:table-column column-width="55pt"/>
				<fo:table-body>
						<fo:table-row>
						
						<#--<#assign amountList = shopeeRentAmount.get('amountMap').get(boothDetails.getKey())?if_exists>-->
							<#assign facility = delegator.findOne("Facility", {"facilityId" : boothDetails.getKey()}, true)>
							<#assign partyTelephoneResult = dispatcher.runSync("getPartyTelephone", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", facility.ownerPartyId, "userLogin", userLogin))/>
							<fo:table-cell><fo:block font-size="5pt" keep-together="always">${boothDetails.getKey()}-${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.facilityName?if_exists)),18)}</fo:block>
							<#if (partyTelephoneResult.contactNumber?has_content)>
							<fo:block font-size="5pt" text-align="center" keep-together="always">&#160;(${partyTelephoneResult.contactNumber?if_exists})</fo:block>
							</#if>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt">
									<fo:table>
									<#list lmsProdList as product>
									 <fo:table-column column-width="47pt"/>
								    </#list>
								    <#list byProdList as product>
									 <fo:table-column column-width="47pt"/>
								    </#list>
								    <#-- 
									<fo:table-column column-width="35pt"/>
									<fo:table-column column-width="35pt"/> -->
										<fo:table-body>
											<fo:table-row>
											<#assign columnCounter =0>
												<#list lmsProdList as product>
												<#assign columnCounter = columnCounter+1>
											        <#if (columnCounter > 11) > <#--  11 products for each row and if morethan 11 then we will wrap to next line -->
													<#assign columnCounter =1>
													</fo:table-row>	
													<fo:table-row>
												    </#if>
													<#assign qty=0>
													<#if boothDetails.getValue().get("prodDetails").get(product)?has_content>
														<#assign qty= boothDetails.getValue().get("prodDetails").get(product).get("packetQuantity")>
													</#if>
													<#if qty !=0>
														<fo:table-cell>		
															<fo:block text-align="right">${qty?if_exists}<#if boothDetails.getValue().get("productsCrateMap").get(product)?has_content>/${boothDetails.getValue().get("productsCrateMap").get(product).get("crates")}(${boothDetails.getValue().get("productsCrateMap").get(product).get("loosePkts")})</#if></fo:block>																									
														</fo:table-cell>
													<#else>
														<fo:table-cell>		
															<fo:block text-align="right" >-</fo:block>																									
														</fo:table-cell>
													</#if>
												</#list>
												<#list byProdList as product>
													<#assign columnCounter =columnCounter+1>
													 <#if (columnCounter > 11) > <#--  11 products for each row and if morethan 11 then we will wrap to next line -->
														<#assign columnCounter =0>
														</fo:table-row>	
														<fo:table-row>
													  </#if>
														<#assign byProdQty=0>
														<#if boothDetails.getValue().get("prodDetails").get(product)?has_content>
															<#assign byProdQty= boothDetails.getValue().get("prodDetails").get(product).get("packetQuantity")>
														</#if>
														<#if byProdQty !=0>
															<fo:table-cell>		
																<fo:block text-align="right">${(byProdQty)?if_exists}<#if boothDetails.getValue().get("productsCrateMap").get(product)?has_content>/${boothDetails.getValue().get("productsCrateMap").get(product).get("crates")}(${boothDetails.getValue().get("productsCrateMap").get(product).get("loosePkts")})</#if></fo:block>																									
															</fo:table-cell>
														<#else>
															<fo:table-cell>		
																<fo:block text-align="right" >-</fo:block>																									
															</fo:table-cell>
														</#if>
												</#list>
												<#-- 
												<fo:table-cell>
													<fo:block font-size="6pt" text-align="left" ><#if (boothDetails.getValue().get("crates")+boothDetails.getValue().get("excess")) !=0>${boothDetails.getValue().get("crates")?if_exists}+${boothDetails.getValue().get("excess")?if_exists}</#if></fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block font-size="6pt" text-align="center"><#if (boothDetails.getValue().get("cans"))?has_content>${boothDetails.getValue().get("cans")?if_exists}</#if></fo:block>
												</fo:table-cell> -->
											</fo:table-row>	
									</fo:table-body>
								</fo:table>
							</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="5pt" text-align="right"><#if (boothDetails.getValue().get("subsidy"))?has_content && (boothDetails.getValue().get("subsidy")>0)>${boothDetails.getValue().get("subsidy")?if_exists}<#else>-</#if></fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="right">${boothDetails.getValue().get("vatAmount")?if_exists?string("#0.00")}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="right">${boothDetails.getValue().get("amount")?if_exists?string("#0.00")}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="5pt" text-align="center"><#if shopeeRentAmount?has_content && (shopeeRentAmount.get('amountMap'))?has_content && (shopeeRentAmount.get('amountMap')).get(boothDetails.getKey())?has_content>${shopeeRentAmount.get('amountMap').get(boothDetails.getKey()).get("rentAmount")?string("#0.00")?if_exists}+${shopeeRentAmount.get('amountMap').get(boothDetails.getKey()).get("tax")?string("#0.00")?if_exists}</#if></fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="5pt" text-align="center">${boothDetails.getValue().get("paymentMode")?if_exists}</fo:block>
							</fo:table-cell>
						</fo:table-row>	
					</fo:table-body>
				</fo:table>
			</fo:block>
			<fo:block text-align="left" keep-together="always" white-space-collapse="false">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		</#list>
		<#assign routeTotals = (routesMap.getValue().get("routeWiseTotals"))>
		<fo:block font-size="6pt">
			<fo:table>
				<fo:table-column column-width="78pt"/>
					<fo:table-column column-width="530pt"/>
					<fo:table-column column-width="25pt"/>
					<fo:table-column column-width="37pt"/>
					<fo:table-column column-width="50pt"/>
					<fo:table-column column-width="50pt"/>
					<fo:table-column column-width="55pt"/>
				<fo:table-body>
				<fo:table-row>
					<fo:table-cell>
							<fo:block>VST_ASCII-027VST_ASCII-069</fo:block>
					</fo:table-cell>
				</fo:table-row>
					<fo:table-row>
						<fo:table-cell>
							<fo:block font-size="6pt" keep-together="always">TOTAL</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block font-size="6pt">
								<fo:table>
									<#assign colCounter = 0>
									<#list lmsProdList as product>
									 <fo:table-column column-width="47pt"/>
								    </#list>
								    <#list byProdList as product>
									 <fo:table-column column-width="47pt"/>
								    </#list>
								    <#-- 
									<fo:table-column column-width="35pt"/>
									<fo:table-column column-width="35pt"/> -->
									<fo:table-body>
										<#assign counter =0>
										<#assign lmsProdCounter =0>
										<#assign byProdCounter =0>
										<#assign productSize = lmsProdList.size()+byProdList.size()>
										<fo:table-row>
											<#assign columnCounter =0>
											<#list lmsProdList as product>
													<#assign columnCounter = columnCounter+1>
													<#if (columnCounter > 11) > <#--  11 products for each row and if morethan 11 then we will wrap to next line -->
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
												<#if (columnCounter > 11) > <#--  11 products for each row and if morethan 11 then we will wrap to next line -->
														<#assign columnCounter =1>
														</fo:table-row>	
														<fo:table-row>
												</#if>
												<fo:table-cell>
														<fo:block text-align="right">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(productNames.get(product)?if_exists)),8)} </fo:block>
												</fo:table-cell>
											</#list>
											<#-- 
											<fo:table-cell>
												<fo:block text-align="left"  font-size="6pt">Crates</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block  text-align="center" font-size="6pt">Cans</fo:block>
											</fo:table-cell> -->
										</fo:table-row>	
								</fo:table-body>
							</fo:table>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="right">
						<fo:block> Subsidy</fo:block>
					</fo:table-cell>
					</fo:table-row>	
					<#--
					<fo:table-row>
							<fo:table-cell>
							<fo:block font-size="6pt" keep-together="always">&#160;</fo:block>
							
								<fo:block font-size="6pt" keep-together="always" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
							
							</fo:table-cell>
					</fo:table-row>	-->
					<fo:table-row>
						<fo:table-cell>
							<fo:block>VST_ASCII-027VST_ASCII-069</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell>
							<fo:block font-size="6pt" keep-together="always">&#160;</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block font-size="6pt">
								<fo:table>
								<#list lmsProdList as product>
									<fo:table-column column-width="47pt"/>
								</#list>
								<#list byProdList as product>
									<fo:table-column column-width="47pt"/>
								</#list>
								<#-- 
								<fo:table-column column-width="35pt"/>
								<fo:table-column column-width="35pt"/> -->
									<fo:table-body>
										<fo:table-row>
											<#assign columnCounter = 0>
											<#list lmsProdList as product>
												<#assign columnCounter = columnCounter+1>
												 <#if (columnCounter > 11) > <#--  11 products for each row and if morethan 11 then we will wrap to next line -->
														<#assign columnCounter =1>
														</fo:table-row>	
														<fo:table-row>
												</#if>
												<#assign qty=0>
												<#if routeTotals.get(product)?has_content>
													<#assign qty= routeTotals.get(product).get("packetQuantity")>
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
											   <#if (columnCounter > 11) > <#--  11 products for each row and if morethan 11 then we will wrap to next line -->
														<#assign columnCounter =1>
														</fo:table-row>	
														<fo:table-row>
												</#if>
												<#assign byProdQty=0>
												<#if routeTotals.get(product)?has_content>
													<#assign byProdQty= routeTotals.get(product).get("packetQuantity")>
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
											<#-- 
												<fo:table-cell>
													<fo:block font-size="6pt">${(routesMap.getValue().get("rtCrates"))?if_exists}+${(routesMap.getValue().get("rtExcessPkts"))?if_exists}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block  text-align="center" font-size="6pt">${(routesMap.getValue().get("rtCans"))?if_exists}</fo:block>
												</fo:table-cell> -->
										</fo:table-row>
									</fo:table-body>
								</fo:table>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block text-align="right"><#if routesMap.getValue().get("routeTotalSubsidy")?has_content && (routesMap.getValue().get("routeTotalSubsidy")>0)>${(routesMap.getValue().get("routeTotalSubsidy"))?if_exists}<#else>-</#if></fo:block></fo:table-cell>
						<fo:table-cell><fo:block font-size="6pt" text-align="right" >${(routesMap.getValue().get("routeVatAmount"))?if_exists?string("#0.00")}</fo:block></fo:table-cell>
						<fo:table-cell>
							<fo:block font-size="6pt" text-align="right">${(routesMap.getValue().get("routeAmount"))?if_exists?string("#0.00")}</fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block></fo:block></fo:table-cell>
					</fo:table-row>
					
					<fo:table-row>
						<fo:table-cell>
							<fo:block></fo:block>
						</fo:table-cell>
					</fo:table-row>	
					<fo:table-row>
						<fo:table-cell>
							<fo:block text-align="left" keep-together="always" white-space-collapse="false">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell >
							<fo:block font-size="6pt" keep-together="always">Crates</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block font-size="6pt">
								<fo:table>
								<#list lmsProdList as product>
								 <fo:table-column column-width="47pt"/>
							    </#list>
							    <#list byProdList as product>
								 <fo:table-column column-width="47pt"/>
							    </#list>
							     
								<fo:table-column column-width="35pt"/>
								<#-- <fo:table-column column-width="35pt"/> -->
									<fo:table-body>
									<fo:table-row>
										<#assign columnCounter =0>
												<#list lmsProdList as product>
												<#assign columnCounter = columnCounter+1>
												<#if (columnCounter > 11) > <#--  11 products for each row and if morethan 11 then we will wrap to next line -->
														<#assign columnCounter =1>
														</fo:table-row>	
														<fo:table-row>
												</#if>
												<#assign qty=0>
												<#if routeTotalCrates?has_content>
												<#assign crateDetail = routeTotalCrates.get(product?if_exists)>
												<#if crateDetail?has_content>
													<#assign qty = crateDetail.get('prodCrates')>
												</#if>
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
													<#if (columnCounter > 11) > <#--  11 products for each row and if morethan 11 then we will wrap to next line -->
														<#assign columnCounter =1>
														</fo:table-row>	
														<fo:table-row>
												    </#if>
													<#assign qty=0>
													<#if routeTotalCrates?has_content>
													<#assign crateDetail = routeTotalCrates.get(product?if_exists)>
													<#if crateDetail?has_content>
														<#assign qty = crateDetail.get('prodCrates')>
													</#if>
													</#if>
													<#if qty !=0>
														<fo:table-cell>		
															<fo:block text-align="right">${qty?if_exists}</fo:block>																									
														</fo:table-cell>
													<#else>
														<fo:table-cell>		
															<fo:block text-align="right">-</fo:block>																									
														</fo:table-cell>
													</#if>
												</#list>
												 
												<fo:table-cell>
													<fo:block font-size="6pt"> - </fo:block>
												</fo:table-cell>
												<#--<fo:table-cell>
													<fo:block font-size="6pt"></fo:block>
												</fo:table-cell> -->
											</fo:table-row>	
									</fo:table-body>
								</fo:table>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block font-size="6pt" text-align="right"><#if (routesMap.getValue().get("routeEmpCrates")>0)>${(routesMap.getValue().get("routeEmpCrates"))?if_exists}<#else>-</#if></fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block font-size="6pt" text-align="right">Full</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block font-size="6pt" text-align="left">Crates:</fo:block>
						</fo:table-cell>
						<#assign fullCrate = (routesMap.getValue().get("rtCrates"))+(routesMap.getValue().get("routeEmpCrates"))>
						<fo:table-cell>
							<fo:block font-size="6pt" text-align="left">${fullCrate?if_exists}</fo:block>
								<#assign totalCrate  = fullCrate>
						</fo:table-cell>
						
						<fo:table-cell>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell>
							<fo:block text-align="left" keep-together="always" white-space-collapse="false">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell>
							<fo:block font-size="6pt" keep-together="always">Loose(Ltr/Kgs)</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block font-size="6pt">
								<fo:table>
								<#list lmsProdList as product>
								 <fo:table-column column-width="47pt"/>
							    </#list>
							    <#list byProdList as product>
								 <fo:table-column column-width="47pt"/>
							    </#list>
							      
								<fo:table-column column-width="35pt"/>
								<#-- <fo:table-column column-width="35pt"/> -->
									<fo:table-body>
									<fo:table-row>
										<#assign looseCrats = 0>
									<#assign columnCounter =0>
											<#list lmsProdList as product>
												<#assign columnCounter = columnCounter+1>
												
												<#if (columnCounter > 11) > <#--  11 products for each row and if morethan 11 then we will wrap to next line -->
														<#assign columnCounter =1>
														</fo:table-row>	
														<fo:table-row>
												</#if>
												
												<#assign qty=0>
												<#if routeTotalCrates?has_content>
												<#assign crateDetail = routeTotalCrates.get(product?if_exists)>
												<#if crateDetail?has_content>
													<#assign qty = crateDetail.get('packetsExces')>
												</#if>
												</#if>
												<#if qty !=0>
													<fo:table-cell>		
														<fo:block text-align="right">${qty?if_exists}(1)</fo:block>																									
													</fo:table-cell>
													<#assign looseCrats = looseCrats+1>
												<#else>
													<fo:table-cell>		
													<fo:block text-align="right">-</fo:block>																									
													</fo:table-cell>
												</#if>
											</#list>
											<#list byProdList as product>
											<#assign columnCounter = columnCounter+1>
											    <#if (columnCounter > 11) > <#--  11 products for each row and if morethan 11 then we will wrap to next line -->
														<#assign columnCounter =1>
														</fo:table-row>	
														<fo:table-row>
												</#if>
												<#assign qty=0>
												<#if routeTotalCrates?has_content>
												<#assign crateDetail = routeTotalCrates.get(product?if_exists)>
												<#if crateDetail?has_content>
													<#assign qty = crateDetail.get('packetsExces')>
												</#if>
												</#if>
												<#if qty !=0>
													<fo:table-cell>		
														<fo:block text-align="right">${qty?if_exists}(1)</fo:block>																									
													</fo:table-cell>
													<#assign looseCrats = looseCrats+1>
												<#else>
													<fo:table-cell>		
													<fo:block text-align="right" >-</fo:block>																									
													</fo:table-cell>
												</#if>
											</#list>
											
								           	<#-- <fo:table-cell>
												<fo:block font-size="6pt"></fo:block>
											</fo:table-cell> -->
									</fo:table-row>
									</fo:table-body>
								</fo:table>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<#assign subExcess = (routesMap.getValue().get("routeTotalSubsidy")%12)>
							<fo:block font-size="6pt" text-align="right"><#if (subExcess>0)>${subExcess?if_exists}(1)<#else>-</#if></fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block font-size="6pt" text-align="right">Loose</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block font-size="6pt" text-align="left">Crates:</fo:block>
						</fo:table-cell>
						<#if (routesMap.getValue().get("routeTotalSubsidy") > 0)>
							<#assign looseCrats = looseCrats+1>
						</#if>
						
						<fo:table-cell>
							<fo:block font-size="6pt" text-align="left">${looseCrats}</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell>
							<fo:block>VST_ASCII-027VST_ASCII-070</fo:block>
						</fo:table-cell>
					</fo:table-row>	
					<fo:table-row>
						<fo:table-cell>
							<fo:block text-align="left" keep-together="always" white-space-collapse="false">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell>
							<fo:block>VST_ASCII-027VST_ASCII-069</fo:block>
						</fo:table-cell>
						
						<fo:table-cell>
							<fo:block font-size="6pt" text-align="right">FullCans:${routesMap.getValue().get("rtCans")} ,&#160;
							LooseCans:${(routesMap.getValue().get("rtLooseCans"))?if_exists}  ,&#160;
							TotalCans ->${(routesMap.getValue().get("rtCans")+routesMap.getValue().get("rtLooseCans"))?if_exists} &#160; and </fo:block>
						</fo:table-cell>
						<fo:table-cell>
						
							<fo:block font-size="6pt" text-align="right">Total</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block font-size="6pt" text-align="left">Crates-></fo:block>
						</fo:table-cell>
						<fo:table-cell>
						<#assign totalCrate=totalCrate+looseCrats>
							<fo:block font-size="6pt" text-align="left">${totalCrate}</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block>VST_ASCII-027VST_ASCII-070</fo:block>
						</fo:table-cell>
					</fo:table-row>
					
					</fo:table-body>
				</fo:table>
			</fo:block>
			<#--
			<fo:block text-align="left" keep-together="always" white-space-collapse="false">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			-->
			<fo:block> 
				<fo:table>
					<fo:table-column column-width="400pt"/>
					<fo:table-column column-width="200pt"/>
					<fo:table-column column-width="200pt"/>						
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="left">&#160;</fo:block>
								<fo:block font-size="6pt" text-align="left">&#160;</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="left">&#160;</fo:block>
								<fo:block font-size="6pt" text-align="left">&#160;</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="left">&#160;</fo:block>
								<fo:block font-size="6pt" text-align="left">&#160;</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="left">CreatedBy:${userLogin.userLoginId}</fo:block>
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