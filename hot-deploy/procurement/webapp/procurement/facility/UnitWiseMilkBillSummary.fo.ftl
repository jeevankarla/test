<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-left=".5in" margin-top=".2in">
                <fo:region-body margin-top=".7in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "UnitMilkBillSummery.txt")}
<#if errorMessage?has_content>
<fo:page-sequence master-reference="main">
   <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
      <fo:block font-size="14pt">
              ${errorMessage}.
   	  </fo:block>
   </fo:flow>
</fo:page-sequence>        
<#else>        
       <#if unitWiseValues?has_content>  
       <#assign unitWiseGrandTot = 0>  
		<fo:page-sequence master-reference="main">
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
			<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="11pt">&#160;      ${reportHeader.description?if_exists}                            PAGE NO:<fo:page-number/></fo:block>
				<#assign unitDetails = delegator.findOne("Facility", {"facilityId" : parameters.unitId}, true)>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="11pt">&#160;         UNIT :  ${unitDetails.facilityName}</fo:block>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="11pt">&#160;      GRAND TOTAL FOR ALL BANKS  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block>
				<fo:block >---------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">   
				<#assign totalqty=0>   
				<#assign milkValue=0> 
				<#assign totAdditions=0>
				<#assign totDeductions=0>	
				<#assign cartage =0>
				<#assign commAmount =0>
				<#assign others = 0>
 		   		<#if MilkBillValuesMap?has_content>
					<#assign totalqty=(MilkBillValuesMap.get("totalqty"))>
					<#assign milkValue=(MilkBillValuesMap.get("milkValue"))>
					<#assign totAdditions=(MilkBillValuesMap.get("totAdditions"))>
					<#assign totDeductions=(MilkBillValuesMap.get("totDeductions"))>
					<#assign cartage = MilkBillValuesMap.get("cartage")>
		    		<#assign commAmount = MilkBillValuesMap.get("commission")>
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
								<fo:table-cell>
									<fo:block white-space-collapse="false"  keep-together="always">TOTAL QUANTITY : ${totalqty?if_exists?string("##0.0")}</fo:block>
		    						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		    						<fo:block white-space-collapse="false" keep-together="always" font-size="10pt">A D D I T I O N S</fo:block>
		    						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		    						
		    						
		    						<#--<#if billingValuesMap?exists>
		    							<#assign facilityBilling = billingValuesMap.entrySet()>
		    							<#list facilityBilling as billingTotals>
		    								<#assign totalValues = billingTotals.getValue()>
		    								<#assign cartage = totalValues.get("tot").get("cartage")>
		    								<#assign commAmount = totalValues.get("tot").get("commAmt")>	
		    							</#list>
		    						</#if>-->
		    						<#assign totAdditions =totAdditions+commAmount>
		    						<fo:block font-size="9pt">
		    							<fo:table>
		    								<fo:table-column column-width="90pt"/>
		    								<fo:table-column column-width="30pt"/>
		    								<fo:table-column column-width="40pt"/>
		    								<fo:table-column column-width="90pt"/>
		    								<fo:table-body>
		    									<fo:table-row>
		    										<fo:table-cell>
		    											<fo:block text-align="left" keep-together="always" >TOTAL CARTAGE</fo:block>
		    										</fo:table-cell>
		    										<fo:table-cell>
		    											<fo:block text-align="center" >:</fo:block>
		    										</fo:table-cell>
		    										<fo:table-cell>
		    											<fo:block text-align="right" keep-together="always">${cartage?if_exists?string("##0.00")}</fo:block>
		    										</fo:table-cell>
		    									</fo:table-row>
		    									<#if (commAmount>0)>
		    									<fo:table-row>
		    										<fo:table-cell>
		    											<fo:block text-align="left" keep-together="always" >TOTAL COMM AMT</fo:block>
		    										</fo:table-cell>
		    										<fo:table-cell>
		    											<fo:block text-align="center" >:</fo:block>
		    										</fo:table-cell>
		    										<fo:table-cell>
		    											<fo:block text-align="right" keep-together="always">${commAmount?if_exists?string("##0.00")}</fo:block>
		    										</fo:table-cell>
		    									</fo:table-row>
		    									</#if>
		    									<fo:table-row>
		    										<fo:table-cell>
		    											<fo:block text-align="left" keep-together="always" >TOTAL OPCOST</fo:block>
		    										</fo:table-cell>
		    										<fo:table-cell>
		    											<fo:block text-align="center" >:</fo:block>
		    										</fo:table-cell>
		    										<fo:table-cell>
		    											<fo:block text-align="right" keep-together="always">${opCost?if_exists?string("##0.00")}</fo:block>
		    										</fo:table-cell>
		    										<fo:table-cell>
		    											<#if uomId== "VLIQ_TS">
							    						 	<fo:block white-space-collapse="false" keep-together="always" text-align="left">(BM KGFAT : ${(solidsMap.get("BM"))?string("##0.0")} CM SLDS : ${(solidsMap.get("CM"))?string("##0.0")})</fo:block>
							    						<#else>	
							    							<fo:block white-space-collapse="false" keep-together="always" text-align="left">(AM Ltrs : ${(qtyMap.get("AM"))?string("##0.0")} PM Ltrs : ${(qtyMap.get("PM"))?string("##0.0")})</fo:block>
							    						</#if>
		    										</fo:table-cell>
		    									</fo:table-row>
		    									<fo:table-row>
		    										<fo:table-cell>
		    											<fo:block text-align="left" keep-together="always" >TOTAL OTHERS</fo:block>
		    										</fo:table-cell>
		    										<fo:table-cell>
		    											<fo:block text-align="center" >:</fo:block>
		    										</fo:table-cell>
		    										<fo:table-cell>
		    											<fo:block text-align="right" keep-together="always">${others?if_exists?string("##0.00")}</fo:block>
		    										</fo:table-cell>
		    									</fo:table-row>
		    									<fo:table-row>
		    										<fo:table-cell>
		    											<fo:block text-align="left" keep-together="always" >TOT.ADDITIONS </fo:block>
		    										</fo:table-cell>
		    										<fo:table-cell>
		    											<fo:block text-align="center" >:</fo:block>
		    										</fo:table-cell>
		    										<fo:table-cell>
		    											<fo:block text-align="right" keep-together="always">${(totAdditions+cartage+(opCost))?if_exists?string("##0.00")}</fo:block>
		    										</fo:table-cell>
		    									</fo:table-row>
		    								</fo:table-body>
		    							</fo:table>
		    						
		    						</fo:block>
		    								    						
		    						<fo:block>-------------------------------------------------------------------</fo:block>
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
												<fo:table-row>
													<fo:table-cell></fo:table-cell>
													<fo:table-cell></fo:table-cell>
													<fo:table-cell><fo:block>CERTIFICATE</fo:block></fo:table-cell>
													<fo:table-cell></fo:table-cell>
													<fo:table-cell><fo:block white-space-collapse="false" keep-together="always">F.N.E: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block></fo:table-cell>
												</fo:table-row>
												<fo:table-row>
													<fo:table-cell>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block white-space-collapse="false" keep-together="always" font-size="10pt">01. Certified that the bill was not drawn and paid previously.</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block white-space-collapse="false" keep-together="always" font-size="10pt">02. Certified that the amounts showned in respect of collection centres and </fo:block>
														<fo:block keep-together="always" white-space-collapse="false" font-size="10pt">&#160;   cooperative societies have been credited in respective bank accounts.</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block white-space-collapse="false" keep-together="always" font-size="10pt">03. Certified  that  quantity of milk has been verified with R.M.R.D.</fo:block>
														<fo:block keep-together="always" white-space-collapse="false" font-size="10pt">&#160;   register together with KG Fat and SNF.</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block white-space-collapse="false" keep-together="always" font-size="10pt">04. Certified that payments have been effected to the collection centres </fo:block>
														<fo:block keep-together="always" white-space-collapse="false" font-size="10pt">&#160;   and cooperative societies as per rules in force.</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block white-space-collapse="false" keep-together="always" font-size="10pt">05. Certified that the KG Fat/SNF amd quantity are checked at lab throughly.</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block white-space-collapse="false" keep-together="always" font-size="10pt">06. Certified that any discrepencies are noticed on KG Fat/SNF and </fo:block>
														<fo:block keep-together="always" white-space-collapse="false" font-size="10pt">&#160;   quantities etc., we are the held responsible according to the justice.</fo:block>
													</fo:table-cell>
												</fo:table-row>
												<fo:table-row>
													<fo:table-cell></fo:table-cell>
												</fo:table-row>
												<fo:table-row>
													<fo:table-cell></fo:table-cell>
												</fo:table-row>
												<fo:table-row>
													<fo:table-cell>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block white-space-collapse="false" keep-together="always" font-size="10pt">R.M.R.D CLERK               LAB. ASSISTANT            PROCESSING SUPERVISOR</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block white-space-collapse="false" keep-together="always" font-size="10pt">FIELD SUPERVISOR                                             MANAGER.</fo:block>
														
													</fo:table-cell>
												</fo:table-row>
											</fo:table-body>
		    							</fo:table>
		    						</fo:block>
								</fo:table-cell>
								<fo:table-cell></fo:table-cell>
								<fo:table-cell></fo:table-cell>								
								<fo:table-cell>
									<fo:block white-space-collapse="false"  text-align="center" keep-together="always">TOTAL MILK VALUE : ${milkValue?if_exists?string("##0.00")}</fo:block>
		    						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		    						<fo:block white-space-collapse="false" text-align="center" keep-together="always" font-size="8pt">R E C O V E R I E S</fo:block>
									<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									<#assign totDeductVal =0>
									<#list deductionsValuesList as deductions>
										<#assign deductionEntries = deductions.entrySet()>										
											<fo:block >
												<fo:table>
													<fo:table-column column-width="60pt"/>
													<fo:table-column column-width="60pt"/>
													<fo:table-column column-width="450pt"/>
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
																	<#assign orderAdjustmentDesc=Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].getShedOrderAdjustmentDescription( dctx,Static["org.ofbiz.base.util.UtilMisc"].toMap("shedId",unitDetails.get("parentFacilityId"))).get("shedAdjustmentDescriptionMap")>
																	<fo:block white-space-collapse="false" text-align="left" text-indent="360pt" keep-together="always">TOTAL ${orderAdjustmentDesc[(adjType.getValue()).orderAdjustmentTypeId]?if_exists}</fo:block>
																</fo:table-cell>
																<fo:table-cell>
																<fo:block white-space-collapse="false" text-align="left" text-indent="420" keep-together="always">:</fo:block>
																</fo:table-cell>
																<fo:table-cell>
																	<fo:block white-space-collapse="false" text-align="right" text-indent="400" keep-together="always">${(dedVal)?if_exists?string("##0.00")}</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</#list>
													</fo:table-body>
												</fo:table>
											</fo:block>											
										<fo:block linefeed-treatment="preserve">&#xA;</fo:block>											
									</#list>		
									<fo:block>
										<fo:table>
											<fo:table-column column-width="60pt"/>
											<fo:table-column column-width="510pt"/>
											<fo:table-column column-width="60pt"/>
											<fo:table-body>
												<fo:table-row>
													<fo:table-cell>
														<fo:block white-space-collapse="false" text-align="left" text-indent="350pt" keep-together="always">TOTAL DEDUCTIONS         :</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block white-space-collapse="false" text-align="right">${(totDeductVal)?if_exists?string("##0.00")}</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
													</fo:table-cell>
												</fo:table-row>												
												<fo:table-row>
													<fo:table-cell>
														<fo:block white-space-collapse="false" text-align="left" text-indent="350pt" keep-together="always">TOTAL TECH FUND(T.I.P)   :</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block white-space-collapse="false" text-align="right">${tipAmount?string("##0.00")}</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
													</fo:table-cell>
												</fo:table-row>
												<fo:table-row>
													<fo:table-cell>
														<fo:block text-align="left" text-indent="345pt">----------------------------------------------</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
													</fo:table-cell>
												</fo:table-row>
												<fo:table-row>
													<fo:table-cell>
														<fo:block text-align="left" text-indent="400pt" white-space-collapse="false" keep-together="always">SUMMARY OF BANK LEDGER</fo:block>
														<fo:block text-align="left"  text-indent="380pt">-------------------------------------</fo:block>	
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
													</fo:table-cell>
												</fo:table-row>
												<fo:table-row>
													<fo:table-cell>
														<fo:block white-space-collapse="false" text-align="left" text-indent="350pt" keep-together="always">TOTAL MILK VALUE         :</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block white-space-collapse="false" text-align="right">${(milkValue)?if_exists?string("##0.00")}</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
													</fo:table-cell>
												</fo:table-row>
												<fo:table-row>
													<fo:table-cell>
														<fo:block white-space-collapse="false" text-align="left" text-indent="350pt" keep-together="always">TOTAL TECH FUND(T.I.P)   :</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block white-space-collapse="false" text-align="right">${tipAmount?string("##0.00")}</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
													</fo:table-cell>
												</fo:table-row>
												<fo:table-row>
													<fo:table-cell>
														<fo:block white-space-collapse="false" text-align="left" text-indent="350pt" keep-together="always">TOTAL ADDITIONS          :</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block white-space-collapse="false" text-align="right">${(totAdditions+cartage+(opCost))?if_exists?string("##0.00")}</fo:block>
														<fo:block text-align="left" text-indent="285pt">-------------------------------------------</fo:block>
													</fo:table-cell>
												</fo:table-row>
												<fo:table-row>
													<fo:table-cell>
														<fo:block white-space-collapse="false" text-align="left" text-indent="350pt" keep-together="always">TOTAL                    :</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block white-space-collapse="false" text-align="right">${(milkValue+(totAdditions+cartage+(opCost))+tipAmount)?if_exists?string("##0.00")}</fo:block>
														<fo:block text-align="left" text-indent="285pt">-------------------------------------------</fo:block>	
													</fo:table-cell>
												</fo:table-row>
												<fo:table-row>
													<fo:table-cell>
														<fo:block white-space-collapse="false" text-align="left" text-indent="350pt" keep-together="always">TOTAL RECOVERIES         :</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block white-space-collapse="false" text-align="right">${(totDeductVal)?if_exists?string("##0.00")}</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
													</fo:table-cell>
												</fo:table-row>
												<fo:table-row>
													<fo:table-cell>
														<fo:block white-space-collapse="false" text-align="left" text-indent="348pt" keep-together="always">TOTAL TECH FUND(T.I.P)   :</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block white-space-collapse="false" text-align="right">${tipAmount?string("##0.00")}</fo:block>
														<fo:block text-align="left" text-indent="285pt">-------------------------------------------</fo:block>
													</fo:table-cell>
												</fo:table-row>
												<fo:table-row>
													<fo:table-cell>
														<fo:block white-space-collapse="false" text-align="left" text-indent="350pt" keep-together="always">TOTAL                   :</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block white-space-collapse="false" text-align="right">${(totDeductVal+tipAmount)?if_exists?string("##0.00")}</fo:block>
														<fo:block text-align="left" text-indent="285pt">-------------------------------------------</fo:block>	
													</fo:table-cell>
												</fo:table-row>
												<fo:table-row>
													<fo:table-cell>
														<fo:block white-space-collapse="false" text-align="left" text-indent="350pt" keep-together="always">N  E  T  A M O U N T    :</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block white-space-collapse="false" text-align="right">${((milkValue+(totAdditions+cartage+(opCost))+tipAmount)-(totDeductVal+tipAmount))?if_exists?string("##0.00")}</fo:block>
														<fo:block text-align="left" text-indent="285pt">-------------------------------------------</fo:block>	
													</fo:table-cell>
												</fo:table-row>
												<fo:table-row>
													<fo:table-cell>
														<fo:block white-space-collapse="false" text-align="left" text-indent="350pt" keep-together="always">ROUNDED NET AMT         :</fo:block>
													</fo:table-cell>
													
													<fo:table-cell>
														<fo:block white-space-collapse="false" text-align="right"><#if unitTotals.get("netRndAmountWithOp")?has_content>${unitTotals.get("netRndAmountWithOp")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
														<fo:block text-align="left" text-indent="285pt">-------------------------------------------</fo:block>	
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
	</#if>	
	</fo:root>
</#escape>