<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-left=".5in" margin-top=".2in">
                <fo:region-body margin-top=".9in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
       <#if unitWiseValues?has_content>    
		<fo:page-sequence master-reference="main">
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
			<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="11pt" text-indent="200pt">&#160;      ${reportHeader.description?if_exists}</fo:block>
				<#assign unitDetails = delegator.findOne("Facility", {"facilityId" : parameters.unitId}, true)>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="11pt" text-indent="200pt">&#160;         UNIT :${unitDetails.facilityId}- ${unitDetails.facilityName}</fo:block>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="11pt" text-indent="200pt">&#160;    UNIT WISE CONSOLIDATED STATEMENT OF MILK PROCURMENT PAYMENT AND</fo:block>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="11pt" text-indent="200pt">&#160;      RECOVERY DETAILS FOR THE PERIOD FROM  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} AND ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block>
				<fo:block >-----------------------------------------------------------------------------------------------------------------------------</fo:block>
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">   
				<#assign totalqty=0>   
				<#assign milkValue=0> 
				<#assign totAdditions=0>
				<#assign totDeductions=0>	
				<#assign opCostAmount=0>
 		   		<#if MilkBillValuesMap?has_content>
					<#assign totalqty=(MilkBillValuesMap.get("totalqty"))>
					<#assign milkValue=(MilkBillValuesMap.get("milkValue"))>
					<#assign totAdditions=(MilkBillValuesMap.get("totAdditions"))>
					<#assign totDeductions=(MilkBillValuesMap.get("totDeductions"))>
					<#assign opCostAmount=((MilkBillValuesMap.get("opCostAmount"))*totalqty)>
				</#if>
				<#assign amOpCost=0>
				<#assign pmOpCost=0>
				<#if supplyTypeMap?exists>
					<#assign amOpCost=supplyTypeMap["AM"]>
					<#assign pmOpCost=supplyTypeMap["PM"]>
				</#if>
 		   		<fo:block font-family="Courier,monospace" font-size="9pt">
 		   			<fo:table>
 		   				<fo:table-column column-width="40pt"/>
						<fo:table-column column-width="50pt"/>
						<fo:table-column column-width="60pt"/>
						<fo:table-column column-width="1100pt"/>
						<fo:table-column column-width="90pt"/>
						<fo:table-column column-width="90pt"/>
						<fo:table-body>
							<fo:table-row>
		    					<fo:table-cell></fo:table-cell>
		    					<fo:table-cell></fo:table-cell>
		    					<fo:table-cell></fo:table-cell>
		    					<fo:table-cell>	
		    						<fo:block font-size="8pt" font-family="Courier,monospace">
		    							<fo:table>
		    								<fo:table-column column-width="40pt"/>
											<fo:table-column column-width="60pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="60pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="60pt"/>
											<fo:table-column column-width="60pt"/>
											<fo:table-header>
													<fo:table-cell></fo:table-cell>
													<fo:table-cell><fo:block keep-together="always"> QTY-KGS</fo:block></fo:table-cell>
													<fo:table-cell><fo:block keep-together="always"> QTY-LTS</fo:block></fo:table-cell>
													<fo:table-cell><fo:block keep-together="always"> KG-FAT</fo:block></fo:table-cell>
													<fo:table-cell><fo:block keep-together="always"> KG-SNF</fo:block></fo:table-cell>
													<fo:table-cell><fo:block keep-together="always"> MILK VALUE</fo:block></fo:table-cell>
												</fo:table-header>
											<fo:table-body>												
												<fo:table-row>
													<fo:table-cell><fo:block>-------------------------------------------------------------------</fo:block></fo:table-cell>
												</fo:table-row>
											<#assign unitValues =unitWiseValues.entrySet()>
											<#list unitValues as unitEntries>
												<fo:table-row>												
														<fo:table-cell>
															<#list procurementProductList as procProducts>
																<#if unitEntries.getKey() == procProducts.productName> 
																	<fo:block keep-together="always">${procProducts.brandName} :</fo:block>
																	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>															
																</#if>	
															</#list>	
															<#if unitEntries.getKey() =="TOT">
																<fo:block>-------------------------------------------------------------------</fo:block>
																<fo:block keep-together="always">TOTAL:</fo:block>
															</#if>	
														</fo:table-cell>
														<fo:table-cell>
														<#list procurementProductList as procProducts>
															<#if unitEntries.getKey() == procProducts.productName> 
																<fo:block>${(unitEntries.getValue().get("qtyKgs")+((unitEntries.getValue().get("sQtyLtrs")*1.03)))?string("##0.0")}</fo:block>																															
															</#if>	
														</#list>
														<#if  unitEntries.getKey() =="TOT">	
															<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
															<fo:block keep-together="always">${(unitEntries.getValue().get("qtyKgs")+(unitEntries.getValue().get("sQtyLtrs")*1.03))?string("##0.0")}</fo:block>
														</#if>																
														</fo:table-cell>
														<fo:table-cell>
														<#list procurementProductList as procProducts>
															<#if unitEntries.getKey() ==procProducts.productName> 
																<fo:block>${(unitEntries.getValue().get("qtyLtrs")+unitEntries.getValue().get("sQtyLtrs"))?string("##0.0")}</fo:block>					
															</#if>
														</#list>
														<#if  unitEntries.getKey() =="TOT">
															<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
															<fo:block keep-together="always">${(unitEntries.getValue().get("qtyLtrs")+unitEntries.getValue().get("sQtyLtrs"))?string("##0.0")}</fo:block>
														</#if>																	
														</fo:table-cell>
														<fo:table-cell>
															<#list procurementProductList as procProducts>
																<#if unitEntries.getKey() ==procProducts.productName> 
																	<fo:block>${(unitEntries.getValue().get("kgFat"))?string("##0.00")}</fo:block>					
																</#if>
															</#list>
															<#if unitEntries.getKey() =="TOT">						
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
																<fo:block keep-together="always">${(unitEntries.getValue().get("kgFat"))?string("##0.00")}</fo:block>
															</#if>																
														</fo:table-cell>
														<fo:table-cell>
															<#list procurementProductList as procProducts>
																<#if unitEntries.getKey() ==procProducts.productName> 
																	<fo:block>${(unitEntries.getValue().get("kgSnf"))?string("##0.00")}</fo:block>					
																</#if>
															</#list>
															<#if unitEntries.getKey() =="TOT">						
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
																<fo:block keep-together="always">${(unitEntries.getValue().get("kgSnf"))?string("##0.00")}</fo:block>
															</#if>																															
														</fo:table-cell>
														<fo:table-cell>
															<#list procurementProductList as procProducts>
																<#if unitEntries.getKey() ==procProducts.productName> 
																	<fo:block>${(unitEntries.getValue().get("price")+unitEntries.getValue().get("sPrice"))?string("##0.00")}</fo:block>					
																</#if>
															</#list>
															<#if unitEntries.getKey() =="TOT">						
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
																<fo:block keep-together="always">${(unitEntries.getValue().get("price")+unitEntries.getValue().get("sPrice"))?string("##0.00")}</fo:block>
															</#if>																														
														</fo:table-cell>													
												</fo:table-row>
												</#list>
												<fo:table-row>
													<fo:table-cell><fo:block>-------------------------------------------------------------------</fo:block></fo:table-cell>
												</fo:table-row>
											</fo:table-body>
		    							</fo:table>
		    						</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row>	
								<fo:table-cell>
		    						<fo:block white-space-collapse="false" keep-together="always" font-size="10pt" text-indent="80pt">ADDITIONS</fo:block>
		    						<fo:block>==================================</fo:block>
		    						<#assign cartage =0>
		    						<#if billingValuesMap?exists>
		    							<#assign facilityBilling = billingValuesMap.entrySet()>
		    							<#list facilityBilling as billingTotals>
		    								<#assign totalValues = billingTotals.getValue()>
		    								<#assign cartage = totalValues.get("tot").get("cartage")>	
		    							</#list>
		    						</#if>
		    						<fo:block white-space-collapse="false" keep-together="always">MILK VALUE       :   ${milkValue?if_exists?string("##0.00")}</fo:block>		    						
		    						<fo:block white-space-collapse="false" keep-together="always">CARTAGE          :   ${cartage?if_exists?string("##0.00")}</fo:block>
		    						<fo:block white-space-collapse="false" keep-together="always">OTHERS           :   0.00</fo:block>
		    					</fo:table-cell>
		    					<fo:table-cell/>
								<fo:table-cell>									
		    						<fo:block white-space-collapse="false" keep-together="always" font-size="10pt" text-indent="200pt">DEDUCTIONS</fo:block>
									<fo:block  text-indent="170pt">=======================================</fo:block>
									<#assign totDeductVal =0>
									<#list deductionsValuesList as deductions>
										<#assign deductionEntries = deductions.entrySet()>										
											<fo:block >
												<fo:table>
													<fo:table-column column-width="60pt"/>
													<fo:table-column column-width="60pt"/>
													<fo:table-column column-width="220pt"/>
													<fo:table-body>
													<#assign dedTypes = adjustmentDedTypes.entrySet()>
													<#list dedTypes as adjType>
															<fo:table-row>
																<fo:table-cell>
																<#assign dedVal =0>
																<#list deductionEntries as deduction>
																	<#if ((adjType.getValue()).orderAdjustmentTypeId) == deduction.getKey()>
																		<#assign dedVal = deduction.getValue()>
																		<#assign totDeductVal = totDeductVal + deduction.getValue()>																	
																	</#if>																
																</#list>
																	<fo:block white-space-collapse="false" text-align="left" text-indent="200pt" keep-together="always">${(adjType.getValue()).description?if_exists}</fo:block>
																</fo:table-cell>
																<fo:table-cell>
																<fo:block white-space-collapse="false" text-align="left" text-indent="230" keep-together="always">:</fo:block>
																</fo:table-cell>
																<fo:table-cell>
																	<fo:block white-space-collapse="false" text-align="right" text-indent="300" keep-together="always">${(dedVal)?if_exists?string("##0.00")}</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</#list>
													</fo:table-body>
												</fo:table>
											</fo:block>											
										</#list>		
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-indent="400pt">NET AMOUNT</fo:block>
									<fo:block text-indent="350pt">=========================</fo:block>
								</fo:table-cell>								
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
		    						<fo:block></fo:block>
		    					</fo:table-cell>
		    					<fo:table-cell>
		    						<fo:block text-indent="30pt">----------------------------</fo:block>
		    					</fo:table-cell>
		    					<fo:table-cell>
		    						<fo:block text-indent="200pt">----------------------------</fo:block>
		    					</fo:table-cell>
		    					<fo:table-cell>
		    						<fo:block text-indent="350pt">----------------------------</fo:block>
		    					</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
		    					<fo:table-cell>
		    						<fo:block>TOTAL</fo:block>
		    					</fo:table-cell>
		    					<fo:table-cell>
		    						<fo:block text-indent="70pt"> ${totAdditions+milkValue}</fo:block>
		    					</fo:table-cell>
		    					<fo:table-cell>
		    						<fo:block text-indent="320pt">${(totDeductVal)?if_exists?string("##0.00")}</fo:block>
		    					</fo:table-cell>
		    					<fo:table-cell>
		    						<fo:block text-indent="400pt">${((totAdditions+milkValue)-(totDeductVal))?if_exists?string("##0.00")}</fo:block>
		    					</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
		    						<fo:block></fo:block>
		    					</fo:table-cell>
		    					<fo:table-cell>
		    						<fo:block text-indent="30pt">----------------------------</fo:block>
		    					</fo:table-cell>
		    					<fo:table-cell>
		    						<fo:block text-indent="200pt">----------------------------</fo:block>
		    					</fo:table-cell>
		    					<fo:table-cell>
		    						<fo:block text-indent="350pt">----------------------------</fo:block>
		    					</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
 		   			</fo:table>
 		   		</fo:block>
 		   </fo:flow>		
		</fo:page-sequence>
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