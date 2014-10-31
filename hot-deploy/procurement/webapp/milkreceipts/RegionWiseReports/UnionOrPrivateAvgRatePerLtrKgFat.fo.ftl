<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-top=".2in">
                <fo:region-body margin-top=".7in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
         ${setRequestAttribute("OUTPUT_FILENAME", "UnionOrPrivate.txt")}
<#if errorMessage?has_content>
<fo:page-sequence master-reference="main">
   <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
      <fo:block font-size="14pt">
              ${errorMessage}.
   	  </fo:block>
   </fo:flow>
</fo:page-sequence>        
<#else>      
	<#if unionPrivateDetailsMap?has_content>  
		<fo:page-sequence master-reference="main">
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="7pt">
			    <fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;     </fo:block>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;                                         Milk Receipts at MPF: Hyderabad  gor the period from  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}   </fo:block>
                <fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;     </fo:block>
				<fo:block>------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                                                                                                                               |      RATE           |         .</fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false">NAME OF THE DAIRY 	  MILK TYPE		  QTY.LTS	     QTY.KGS	    KG FAT      KGSNF    TOT.SOLIDS   AVG.FAT  AVG.SNF      VALUE        | P/LTR        KG.FAT  | AVG.RATE</fo:block>
				<fo:block>------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">       				   
				<fo:block font-family="Courier,monospace" font-size="7pt">
			 		<fo:table>
						<fo:table-column column-width="100pt"/>
							<fo:table-body> 
								<#assign recdTotQtyLtrs = 0>
								<#assign recdTotQtyKgs = 0>
								<#assign recdTotKgFat = 0>
								<#assign recdTotKgSnf = 0>
								<#assign recdTotKgSolid = 0>	
								<#assign mlkTotAmt = 0>
								<#assign totalRatePerLtrs = 0>
								<#assign totalRatePerKgFat = 0>
								<#assign totalAvgPerDay = 0>
								<#list productWiseList as productId>
									<#assign prodTotQtyLtrs = 0>
									<#assign prodTotQtyKgs = 0>
									<#assign prodTotKgFat = 0>
									<#assign prodTotKgSnf = 0>
									<#assign recdProductTotKgSolid=0>
									<#assign mlkProductTotAmt=0>
									<#assign totalProductRatePerLtrs=0>
									<#assign totalProductRatePerKgFat=0>
									<#assign avgProductPerDay =0>
						           		<fo:table-row>						           			
						           			<fo:table-cell>
						           				<fo:block >
						           					<fo:table>
														<fo:table-column column-width="90pt"/>
						           						<fo:table-column column-width="30pt"/>
						           						<fo:table-column column-width="50pt"/>
						           						<fo:table-column column-width="53pt"/>
						           						<fo:table-column column-width="45pt"/>
						           						<fo:table-column column-width="50pt"/>
						           						<fo:table-column column-width="55pt"/>
						           						<fo:table-column column-width="40pt"/>
						           						<fo:table-column column-width="30pt"/>
						           						<fo:table-column column-width="70pt"/>	
						           						<fo:table-column column-width="50pt"/>	
						           						<fo:table-column column-width="60pt"/>	
						           						<fo:table-column column-width="40pt"/>	
						           						<fo:table-column column-width="55pt"/>	
						           						<fo:table-column column-width="60pt"/>	
						           						<fo:table-column column-width="50pt"/>						           						
						           						<fo:table-body>	
							           						<#assign privateDairiesList = unionPrivateDetailsMap.entrySet()>
			   												<#list privateDairiesList as privateDairies>
			   													<#assign productWiseDetailsList = privateDairies.getValue().entrySet()>
																<#list productWiseDetailsList as productDetails>
																	<#if productDetails.getKey() == productId>
																		<#assign product = delegator.findOne("Product", {"productId" : productDetails.getKey()}, true)>
																		<#assign unitWiseDetailsList = productDetails.getValue()>
																		<#list unitWiseDetailsList as unitWiseDetails>
																			<#assign unitsWiseDetails = unitWiseDetails.entrySet()>
																			<#list unitsWiseDetails as unitDetails>
										           								<fo:table-row>
											           							    <#assign facility = delegator.findOne("Facility", {"facilityId" : unitDetails.getKey()}, true)>
											           							    <#assign recdTotQtyLtrs=recdTotQtyLtrs+unitDetails.getValue().get("recdQtyLtrs")>
										           									<#assign recdTotQtyKgs=recdTotQtyKgs+unitDetails.getValue().get("recdQtyKgs")>
										           									<#assign recdTotKgFat=recdTotKgFat+unitDetails.getValue().get("recdKgFat")>
										           									<#assign recdTotKgSnf=recdTotKgSnf+unitDetails.getValue().get("recdKgSnf")>	
											           								<fo:table-cell>
											           									<fo:block text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("facilityName")?if_exists)),17)}</fo:block>
											           								</fo:table-cell>
												           							<fo:table-cell>
									           											<fo:block text-align="left">${product.brandName?if_exists}</fo:block>
									           										</fo:table-cell>								           							
											           								<fo:table-cell>
											           									<fo:block text-align="right">${unitDetails.getValue().get("recdQtyLtrs")?if_exists?string("##0.0")}</fo:block>
											           								</fo:table-cell>
											           								<fo:table-cell>
											           									<fo:block text-align="right">${unitDetails.getValue().get("recdQtyKgs")?if_exists?string("##0.0")}</fo:block>
											           								</fo:table-cell>
											           								<fo:table-cell>
											           									<fo:block text-align="right">${unitDetails.getValue().get("recdKgFat")?if_exists?string("##0.00")}</fo:block>
											           								</fo:table-cell>
											           								<fo:table-cell>
											           									<fo:block text-align="right">${unitDetails.getValue().get("recdKgSnf")?if_exists?string("##0.00")}</fo:block>
											           								</fo:table-cell>
										           									<#assign kgTotalSolids = (unitDetails.getValue().get("recdKgFat")+unitDetails.getValue().get("recdKgSnf"))>
									           										<fo:table-cell>
									           											<fo:block text-align="right">${(kgTotalSolids)?if_exists?string("##0.00")}</fo:block>
									           										</fo:table-cell>
											           								<#assign recdFat=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(unitDetails.getValue().get("recdKgFat"),unitDetails.getValue().get("recdQtyKgs")))>
							           												<#assign recdSnf=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(unitDetails.getValue().get("recdKgSnf"),unitDetails.getValue().get("recdQtyKgs")))>
											           								<fo:table-cell>
											           									<fo:block text-align="right">${recdFat?if_exists?string("##0.0")}0</fo:block>
											           								</fo:table-cell>
											           								<fo:table-cell>
											           									<fo:block text-align="right">${recdSnf?if_exists?string("##0.00")}</fo:block>
											           								</fo:table-cell>
											           								<#assign milkAmount=0>		
																					<#assign milkAmt = (unitDetails.getValue().get("milkValue"))>
																					<#assign opCost = (unitDetails.getValue().get("opCost"))>	
																					<#assign milkAmount = (milkAmt+opCost)>	
																					<#assign mlkTotAmt=mlkTotAmt+milkAmount>
											           								<fo:table-cell>
											           									<fo:block text-align="right">${milkAmount?if_exists?string("##0.00")}</fo:block>
											           								</fo:table-cell>
											           								<#assign ratePerLtrs=0>
											           								<#if !(unitDetails.getValue().get("recdQtyLtrs")==0)>
												           								<#assign ratePerLtrs =(milkAmount/unitDetails.getValue().get("recdQtyLtrs"))>	
												           							</#if>
											           								<fo:table-cell>
											           									<fo:block text-align="right">${(ratePerLtrs)?if_exists?string("##0.00")}</fo:block>
											           								</fo:table-cell>
											           								<#assign ratePerKgFat =0>
											           								<#if (productDetails.getKey() == "111") && !(unitDetails.getValue().get("recdKgFat")==0)>
											           									<#assign ratePerKgFat = (unitDetails.getValue().get("milkValue")/unitDetails.getValue().get("recdKgFat"))>
											           									<fo:table-cell>
											           										<fo:block text-align="right">${(ratePerKgFat)?if_exists?string("##0.00")}</fo:block>
											           									</fo:table-cell>
											           								<#else>	
												           								<#if (productDetails.getKey() == "211") && !(kgTotalSolids==0)>
												           									<#assign ratePerKgFat = (unitDetails.getValue().get("milkValue")/kgTotalSolids)>
												           									<fo:table-cell>
												           										<fo:block text-align="right">${(ratePerKgFat)?if_exists?string("##0.00")}</fo:block>
												           									</fo:table-cell>
												           								<#else>
											           										<fo:table-cell>
											           											<fo:block text-align="right">${(ratePerKgFat)?if_exists?string("##0.00")}</fo:block>
											           										</fo:table-cell>
											           									</#if>
											           								</#if>	
												           							<#assign averagePerDay = (unitDetails.getValue().get("recdQtyLtrs")/totalDays)> 
												           							<#if averagePerDay!=0>
																						<#assign avgPerDay = ((averagePerDay)/100000)> 
																					</#if>
											           								<fo:table-cell>
											           									<fo:block text-align="right">${(avgPerDay)?if_exists?string("##0.00")}</fo:block>
											           								</fo:table-cell>
									           									</fo:table-row>
																				<#assign prodTotQtyLtrs=prodTotQtyLtrs+unitDetails.getValue().get("recdQtyLtrs")>
																				<#assign prodTotQtyKgs=prodTotQtyKgs+unitDetails.getValue().get("recdQtyKgs")>
																				<#assign prodTotKgFat=prodTotKgFat+unitDetails.getValue().get("recdKgFat")>
																				<#assign prodTotKgSnf=prodTotKgSnf+unitDetails.getValue().get("recdKgSnf")>
										           								<#assign milkProductAmount=0>		
																				<#assign milkProductAmount = (unitDetails.getValue().get("milkValue")+unitDetails.getValue().get("opCost"))>	
																				<#assign mlkProductTotAmt=mlkProductTotAmt+milkProductAmount>
																				<#assign rateProductPerLtrs=0>
										           								<#if !(unitDetails.getValue().get("recdQtyLtrs")==0)>
											           								<#assign rateProductPerLtrs =(milkProductAmount/prodTotQtyLtrs)>	
											           								<#assign totalProductRatePerLtrs=totalProductRatePerLtrs+rateProductPerLtrs>
											           							</#if>
									           								</#list>
									           							</#list>
							           								</#if>
						           								</#list>
					           								</#list>
					           								<#if !(prodTotQtyLtrs==0)>
					           									<fo:table-row>
					           										<fo:table-cell>
																		<fo:block font-size="7pt">------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
							           								</fo:table-cell>
							           							</fo:table-row>
					           									<fo:table-row>
					           										<fo:table-cell>
							           									<fo:block text-align="left"></fo:block>
							           								</fo:table-cell>
								           							<fo:table-cell>
					           											<fo:block text-align="left"></fo:block>
					           										</fo:table-cell>
							           								<fo:table-cell>
							           									<fo:block text-align="right">${prodTotQtyLtrs?if_exists?string("##0.0")}</fo:block>
							           								</fo:table-cell>
							           								<fo:table-cell>
							           									<fo:block text-align="right">${prodTotQtyKgs?if_exists?string("##0.0")}</fo:block>
							           								</fo:table-cell>
							           								<fo:table-cell>
							           									<fo:block text-align="right">${prodTotKgFat?if_exists?string("##0.00")}</fo:block>
							           								</fo:table-cell>
							           								<fo:table-cell>
							           									<fo:block text-align="right">${prodTotKgSnf?if_exists?string("##0.00")}</fo:block>
							           								</fo:table-cell>
							           								<#assign recdProductTotKgSolid = (prodTotKgFat+prodTotKgSnf)>
							           								<fo:table-cell>
						           										<fo:block text-align="right">${(recdProductTotKgSolid)?if_exists?string("##0.00")}</fo:block>
						           									</fo:table-cell>
						           									<#assign recdProductFat=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(prodTotKgFat,prodTotQtyKgs))>
			           												<#assign recdProductSnf=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(prodTotKgSnf,prodTotQtyKgs))>
							           								<fo:table-cell>
							           									<fo:block text-align="right">${recdProductFat?if_exists?string("##0.0")}0</fo:block>
							           								</fo:table-cell>
							           								<fo:table-cell>
							           									<fo:block text-align="right">${recdProductSnf?if_exists?string("##0.00")}</fo:block>
							           								</fo:table-cell>
							           								<fo:table-cell>
							           									<fo:block text-align="right">${mlkProductTotAmt?if_exists?string("##0.00")}</fo:block>
							           								</fo:table-cell>
							           								<#if !(prodTotQtyLtrs==0)>
								           								<#assign totalProductRatePerLtrs =(mlkProductTotAmt/prodTotQtyLtrs)>	
								           							</#if>
							           								<fo:table-cell>
							           									<fo:block text-align="right">${(totalProductRatePerLtrs)?if_exists?string("##0.00")}</fo:block>
							           								</fo:table-cell>
							           								<#if (productId == "111") && !(prodTotKgFat==0)>
							           									<#assign rateProductPerKgFat = (mlkProductTotAmt/prodTotKgFat)>
							           									<#assign totalProductRatePerKgFat = totalProductRatePerKgFat+rateProductPerKgFat>
								           							<#else>	
									           							<#if (productId == "211") && !(recdProductTotKgSolid==0)>
									           								<#assign rateProductPerKgFat = (mlkProductTotAmt/recdProductTotKgSolid)>
									           								<#assign totalProductRatePerKgFat = totalProductRatePerKgFat+rateProductPerKgFat>
									           							<#else>
									           								<#assign totalProductRatePerKgFat =0>
									           							</#if>
							           								</#if>
						           									<fo:table-cell>
						           										<fo:block text-align="right">${(totalProductRatePerKgFat)?if_exists?string("##0.00")}</fo:block>
						           									</fo:table-cell>
							           								<#assign averagePerDay = (prodTotQtyLtrs/totalDays)> 
								           							<#if averagePerDay!=0>
																		<#assign avgProductPerDay = ((averagePerDay)/100000)> 
																	</#if>
							           								<fo:table-cell>
							           									<fo:block text-align="right">${(avgProductPerDay)?if_exists?string("##0.00")}</fo:block>
							           								</fo:table-cell>
						           								</fo:table-row>
																<fo:table-row>
					           										<fo:table-cell>
																		<fo:block font-size="7pt">------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
							           								</fo:table-cell>
							           							</fo:table-row>
							           								<fo:table-row>
				           										<fo:table-cell>
			    													<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
						           								</fo:table-cell>
						           							</fo:table-row>
							           						</#if>
						           						</fo:table-body>
						           					</fo:table>
						           				</fo:block>
						           			</fo:table-cell>						           			
						           		</fo:table-row> 
								</#list>	
							</fo:table-body>
			 			</fo:table>
					</fo:block>
					<fo:block font-family="Courier,monospace" font-size="7pt">
						 <fo:table>
					 		<fo:table-column column-width="100pt"/>
       						<fo:table-body> 
					           	<fo:table-row>
					           		<fo:table-cell>
					           			<fo:block >
					           				<fo:table >
				           						<fo:table-column column-width="90pt"/>
				           						<fo:table-column column-width="30pt"/>
				           						<fo:table-column column-width="50pt"/>
				           						<fo:table-column column-width="53pt"/>
				           						<fo:table-column column-width="45pt"/>
				           						<fo:table-column column-width="50pt"/>
				           						<fo:table-column column-width="55pt"/>
				           						<fo:table-column column-width="40pt"/>
				           						<fo:table-column column-width="30pt"/>
				           						<fo:table-column column-width="70pt"/>	
				           						<fo:table-column column-width="50pt"/>	
				           						<fo:table-column column-width="60pt"/>	
				           						<fo:table-column column-width="40pt"/>	
				           						<fo:table-column column-width="55pt"/>	
				           						<fo:table-column column-width="55pt"/>	
				           						<fo:table-column column-width="50pt"/>					           						
					           					<fo:table-body>
					           						<fo:table-row>
					           							<fo:table-cell>
										           			<fo:block text-align="left"></fo:block>
										           		</fo:table-cell>
									           			<fo:table-cell>
									           				<fo:block text-align="left" white-space-collapse="false" keep-together="always">TOTAL</fo:block>
									           			</fo:table-cell>
				           								<fo:table-cell>
				           									<fo:block text-align="right">${recdTotQtyLtrs?if_exists?string("##0.0")}</fo:block>
				           								</fo:table-cell>
				           								<fo:table-cell>
				           									<fo:block text-align="right">${recdTotQtyKgs?if_exists?string("##0.0")}</fo:block>
				           								</fo:table-cell>
				           								<fo:table-cell>
				           									<fo:block text-align="right">${recdTotKgFat?if_exists?string("##0.00")}</fo:block>
				           								</fo:table-cell>
				           								<fo:table-cell>
				           									<fo:block text-align="right">${recdTotKgSnf?if_exists?string("##0.00")}</fo:block>
				           								</fo:table-cell>
				           								<#assign recdTotKgSolid = (recdTotKgFat+recdTotKgSnf)>
				           								<fo:table-cell>
				           									<fo:block text-align="right">${(recdTotKgSolid)?if_exists?string("##0.00")}</fo:block>
				           								</fo:table-cell>
				           								<#assign grandFat=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(recdTotKgFat,recdTotQtyKgs))>
				           								<#assign grandSnf=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(recdTotKgSnf,recdTotQtyKgs))>
				           								<fo:table-cell>
				           									<fo:block text-align="right">${grandFat?if_exists?string("##0.0")}0</fo:block>
				           								</fo:table-cell>
				           								<fo:table-cell>
				           									<fo:block text-align="right">${grandSnf?if_exists?string("##0.00")}</fo:block>
				           								</fo:table-cell>
				           								<fo:table-cell>
						           							<fo:block text-align="right">${mlkTotAmt?if_exists?string("##0.00")}</fo:block>
						           						</fo:table-cell>
						           						<#if !(recdTotQtyLtrs==0)>
					           								<#assign totalRatePerLtrs =(mlkTotAmt/recdTotQtyLtrs)>	
					           							</#if>
				           								<fo:table-cell>
					           								<fo:block text-align="right">${totalRatePerLtrs?if_exists?string("##0.00")}</fo:block>
					           							</fo:table-cell>
					           							<fo:table-cell>
					           								<fo:block text-align="right">${totalRatePerKgFat?if_exists?string("##0.00")}</fo:block>
					           							</fo:table-cell>			        
					           							<#assign averagePerDayTotal = 0 >
					           							<#assign averagePerDayTotal = (recdTotQtyLtrs/totalDays)> 
					           							<#if averagePerDayTotal!=0>
															<#assign totalAvgPerDay = ((averagePerDayTotal)/100000)> 
														</#if>
					           							<fo:table-cell>
					           								<fo:block text-align="right">${totalAvgPerDay?if_exists?string("##0.00")}</fo:block>
					           							</fo:table-cell>
					           						</fo:table-row>
					           					</fo:table-body>
					           				</fo:table>
					           			</fo:block>
					           		</fo:table-cell>						           			
					           		</fo:table-row> 
					           		<fo:table-row>
					           			<fo:table-cell>
					           				<fo:block font-size="7pt">------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
					           			</fo:table-cell>
					           		</fo:table-row>
					           	</fo:table-body> 
					        </fo:table>
						 </fo:block>
	    				</fo:flow>		
	   				</fo:page-sequence>	
	   			 <#else>
	                <fo:page-sequence master-reference="main">
	                    <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	                        <fo:block font-size="14pt">
	                            ${uiLabelMap.NoOrdersFound}.
	                        </fo:block>
	                    </fo:flow>
	                </fo:page-sequence>	
				</#if>		
			</#if> 
	</fo:root>
</#escape>