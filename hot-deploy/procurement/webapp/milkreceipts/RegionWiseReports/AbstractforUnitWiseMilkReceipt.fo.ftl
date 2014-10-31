<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-left=".3in" margin-top=".3in">
                <fo:region-body margin-top=".5in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "AbstractUnitMilkReceipts.txt")}
        <#if errorMessage?has_content>
		<fo:page-sequence master-reference="main">
		   <fo:flow flow-name="xsl-region-body" font-family="Courier,monospae">
		      <fo:block font-size="14pt">
		              ${errorMessage}.
		   	  </fo:block>
		   </fo:flow>
		</fo:page-sequence>        
		<#else>
		<#if finalUnitMap?has_content>  
			<fo:page-sequence master-reference="main">
				<fo:static-content flow-name="xsl-region-before">
					<fo:block text-align="left" font-size="5pt" white-space-collapse="false" keep-together="always">&#160;          	  ABSTRACT FOR UNIT-WISE MILK RECEIPTS PERIOD FROM  : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")} </fo:block>                      
					<fo:block white-space-collapse="false" text-align="left" font-size="8pt">--------------------------------------------------------------------------------------------------------------------------------</fo:block>
					<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">CODE    UNOIN/SHED        TYPE OF     QUANTITY            QUANTITY             TOTAL                TOTAL                    AVERAGE            NO.OF  AVERAGE</fo:block>
					<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">&#160;              NAME                   MILK                  (LTS) 	                (KGS)                 KG FAT	 	   	      	     KG SNF	            FAT%       SNF%    DAYS	         QTY(LTS)</fo:block>
					<fo:block white-space-collapse="false" text-align="left" font-size="8pt">--------------------------------------------------------------------------------------------------------------------------------</fo:block> 
				</fo:static-content>
				<fo:flow flow-name="xsl-region-body" font-family="Courier,monospae"> 
						<fo:block font-family="Courier,monospae" font-size="5pt">
						<fo:table>
							<fo:table-column column-width="12pt"/>
							<fo:table-column column-width="50pt"/>
							<fo:table-column column-width="15pt"/>
							<fo:table-column column-width="45pt"/>
							<fo:table-column column-width="42pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="25pt"/>
							<fo:table-column column-width="20pt"/>
							<fo:table-column column-width="15pt"/>
							<fo:table-column column-width="35pt"/>
							<fo:table-body>
							<#assign totavgQty= 0>
							<#assign totReceivedQty=0>
							<#assign totQtyKgs=0>
							<#assign totFedKgFat=0>
						    <#assign totFedKgSnf=0>
						    <#assign totAvgltrs=0>
							<#if finalUnitMap?has_content>					    
								<#assign milkDetail = finalUnitMap.entrySet()>
				                <#list milkDetail as milkData>
				               		<#assign productMilkData = milkData.getValue().entrySet()>
				                	<#list productMilkData as product>
				                		<#if product.getKey()!="TOT">
				                			<#if product.getValue().get('recdQtyLtrs')!=0>
				                				<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : milkData.getKey()}, true)>
				                				<#assign shedDetails = delegator.findOne("Facility", {"facilityId" :facilityDetails.parentFacilityId}, true)>
												<fo:table-row>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold">&#160;${facilityDetails.mccCode?if_exists}</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="left" text-indent="2pt" font-weight="bold"> ${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facilityDetails.get("facilityName")?if_exists)),15)}</fo:block>
													</fo:table-cell>
													<#assign products = delegator.findOne("Product", {"productId" : product.getKey()}, true)>
													<#assign productName= products.brandName>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="left" keep-together="always" font-weight="bold">${productName.replace(" ","-")?if_exists}</fo:block>
													</fo:table-cell>
				                                    <#assign totReceivedQty=totReceivedQty+(product.getValue().get("recdQtyLtrs"))> 
				                                    <#assign rcdQtyLts=product.getValue().get("recdQtyLtrs")>
				                                    <#assign rcdLtrs=0>
				                                    <#if rcdQtyLts?has_content>
				                                    	<#assign rcdLtrs=rcdQtyLts>
				                                    </#if>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold">${rcdLtrs?string("##0.0")}</fo:block>
													</fo:table-cell>
													<#assign totQtyKgs=totQtyKgs+(product.getValue().get("recdQtyKgs"))> 
													<#assign rcdQuantity=product.getValue().get("recdQtyKgs")>
				                                    <#assign rcdQtyKgs=0>
				                                    <#if rcdQuantity?has_content>
				                                    	<#assign rcdQtyKgs=rcdQuantity>
				                                    </#if>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold">${rcdQtyKgs?string("##0.0")}</fo:block>
													</fo:table-cell>
													<#assign totFedKgFat=totFedKgFat+(product.getValue().get("recdKgFat"))> 
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold">${product.getValue().get("recdKgFat")?if_exists?string("##0.00")}</fo:block>
													</fo:table-cell>
													<#assign totFedKgSnf=totFedKgSnf+(product.getValue().get("recdKgSnf"))>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold">${product.getValue().get("recdKgSnf")?if_exists?string("##0.00")}</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold">${product.getValue().get("receivedFat")?if_exists?string("##0.0")}0</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold">${product.getValue().get("receivedSnf")?if_exists?string("##0.00")}</fo:block>
													</fo:table-cell>
													<#assign unitQty=productWiseDaysMap.get(milkData.getKey())>
													<#assign totalDays=(unitQty.get(product.getKey()).get("noofdays"))>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold">${totalDays?if_exists}</fo:block>
													</fo:table-cell>
													<#assign avgQty=(unitQty.get(product.getKey()).get("avgQtyLtrs"))>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold">${(avgQty)?if_exists?string("#0")}</fo:block>
													</fo:table-cell>
												</fo:table-row>
											</#if>
										</#if>
									</#list>
							    </#list>
							</#if>
						</fo:table-body>
					</fo:table>
			     	</fo:block>
					<fo:block font-size="8pt" white-space-collapse="false" text-align="left">--------------------------------------------------------------------------------------------------------------------------------</fo:block>
					<fo:block font-family="Courier,monospae" font-size="5pt">
						<fo:table>
							<fo:table-column column-width="14pt"/>
							<fo:table-column column-width="50pt"/>
							<fo:table-column column-width="15pt"/>
							<fo:table-column column-width="45pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="25pt"/>
							<fo:table-column column-width="20pt"/>
							<fo:table-column column-width="15pt"/>
							<fo:table-column column-width="35pt"/>
							<fo:table-body>
								<#assign fedProductQty = 0>
								<#if unitProductTotalMap?has_content>
									<#assign grandTotals = unitProductTotalMap.entrySet()>
					                <#list grandTotals as grandTot>
					                	<#if grandTot.getValue().get("recdQtyLtrs")!=0>
											<fo:table-row>
												<fo:table-cell>
													<fo:block font-size="5pt" text-align="right" font-weight="bold">${grandTot.getKey()}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block font-size="5pt" text-align="left" text-indent="2pt" font-weight="bold">TOTAL:</fo:block>
												</fo:table-cell>
												<#assign products = delegator.findOne("Product", {"productId" : grandTot.getKey()}, true)>
												<#assign productName= products.brandName>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="left" keep-together="always" font-weight="bold">${productName.replace(" ","-")?if_exists}</fo:block>
													</fo:table-cell>
												<fo:table-cell>
													<fo:block font-size="5pt" text-align="right" font-weight="bold">${grandTot.getValue().get("recdQtyLtrs")?if_exists?string("##0.0")}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block font-size="5pt" text-align="right" font-weight="bold">${grandTot.getValue().get("recdQtyKgs")?if_exists?string("##0.0")}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block font-size="5pt" text-align="right" font-weight="bold">${grandTot.getValue().get("recdKgFat")?if_exists?string("##0.00")}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block font-size="5pt" text-align="right" font-weight="bold">${grandTot.getValue().get("recdKgSnf")?if_exists?string("##0.00")}</fo:block>
												</fo:table-cell>
												<#assign grandFat=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(grandTot.getValue().get("recdKgFat"),grandTot.getValue().get("recdQtyKgs")))>
												<fo:table-cell>
													<fo:block font-size="5pt" text-align="right" font-weight="bold">${grandFat?string("##0.0")}0</fo:block>
												</fo:table-cell>
												<#assign grandSnf=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(grandTot.getValue().get("recdKgSnf"),grandTot.getValue().get("recdQtyKgs")))>
												<fo:table-cell>
													<fo:block font-size="5pt" text-align="right" font-weight="bold">${grandSnf?string("##0.00")}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block font-size="5pt" text-align="right" font-weight="bold"></fo:block>
												</fo:table-cell>
												<#assign productGrandQty= productAvgMap.get(grandTot.getKey()).get("avgQtyLtrs")>
												<#assign fedProductQty = fedProductQty+productGrandQty>
												<fo:table-cell>
													<fo:block font-size="5pt" text-align="right" font-weight="bold">${productGrandQty?if_exists?string("#0")}</fo:block>
												</fo:table-cell>
								     		</fo:table-row>
								    	 </#if>
									 </#list>
								 </#if>
							</fo:table-body>
					    </fo:table>
				    </fo:block>
				    <fo:block font-size="8pt" white-space-collapse="false" text-align="left">&#160;  -------------------------------------------------------------------------------------------------------------------------</fo:block>
 					<fo:block font-family="Courier,monospace" font-size="5pt">
						<fo:table>
							<fo:table-column column-width="64pt"/>
							<fo:table-column column-width="15pt"/>
							<fo:table-column column-width="45pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="25pt"/>
							<fo:table-column column-width="20pt"/>
							<fo:table-column column-width="15pt"/>
							<fo:table-column column-width="35pt"/>
							<fo:table-body>
							    <fo:table-row>
									<fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" font-weight="bold" text-align="left">FEDERATION TOTAL:</fo:block>
							        </fo:table-cell>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" font-weight="bold" text-align="left"></fo:block>
							        </fo:table-cell>
							        <#assign totFedReceivedQty=0>
							        <#if totReceivedQty?has_content>
							        	<#assign totFedReceivedQty=totReceivedQty>
							        </#if>
									<fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right">${totFedReceivedQty?string("##0.0")}</fo:block>
							        </fo:table-cell>
							        <#assign totFedQtyKgs=0>
							        <#if totQtyKgs?has_content>
							        	<#assign totFedQtyKgs=totQtyKgs>
							        </#if>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right">${totFedQtyKgs?string("##0.0")}</fo:block>
							        </fo:table-cell>
							        <#assign fedTotKgFat=0>
							        <#if totFedKgFat?has_content>
							        	<#assign fedTotKgFat=totFedKgFat>
							        </#if>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right">${fedTotKgFat?string("##0.00")}</fo:block>
							        </fo:table-cell>
							        <#assign fedTotKgSnf=0>
							        <#if totFedKgSnf?has_content>
							        	<#assign fedTotKgSnf=totFedKgSnf>
							        </#if>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right">${fedTotKgSnf?string("##0.00")}</fo:block>
							        </fo:table-cell>
							        <#assign fedFat=0>
							        <#assign fat=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(totFedKgFat,totQtyKgs))> 
								        <#if fat?has_content>
								        	<#assign fedFat=fat>
								        </#if>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right">${fedFat?string("##0.0")}0</fo:block>
							        </fo:table-cell>
							        <#assign fedSnf=0>
								    <#assign snf=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(totFedKgSnf,totQtyKgs))>
								     	<#if snf?has_content>
								        	<#assign fedSnf=snf>
								        </#if>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right">${fedSnf?string("##0.00")}</fo:block>
							        </fo:table-cell>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right"></fo:block>
							        </fo:table-cell>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right">${fedProductQty?if_exists?string("#0")}</fo:block>
							        </fo:table-cell>
							    </fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:block>
					<#if finalPrivateUnitMap?has_content>
					<fo:block font-size="8pt" white-space-collapse="false" text-align="left">--------------------------------------------------------------------------------------------------------------------------------</fo:block>
					<fo:block font-family="Courier,monospace" font-size="5pt">
						<fo:table>
							<fo:table-column column-width="12pt"/>
							<fo:table-column column-width="50pt"/>
							<fo:table-column column-width="15pt"/>
							<fo:table-column column-width="45pt"/>
							<fo:table-column column-width="42pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="25pt"/>
							<fo:table-column column-width="20pt"/>
							<fo:table-column column-width="15pt"/>
							<fo:table-column column-width="35pt"/>
							<#assign privatetotQty =0>
							<#assign privatetotQtyKgs =0>
							<#assign privatetotKgFat =0>
							<#assign privatetotKgSnf =0>
							<#assign finalPrivateTotals = finalPrivateUnitMap.entrySet()>
			                <#list finalPrivateTotals as finalPrivateTotal>
			                	<#assign privateTotal = finalPrivateTotal.getValue().entrySet()>
		                		<#list privateTotal as privateTot>
		                			<#if privateTot.getKey()!="TOT">
		                				<#if privateTot.getValue().get('recdQtyLtrs')!=0>
		                				<#assign facilityPrivateDetails = delegator.findOne("Facility", {"facilityId" : finalPrivateTotal.getKey()}, true)>
		                				<#assign shedPrivateDetails = delegator.findOne("Facility", {"facilityId" :facilityPrivateDetails.parentFacilityId}, true)>
				    						<fo:table-body> 
												<fo:table-row>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold">${facilityPrivateDetails.mccCode?if_exists}</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="left" text-indent="2pt" font-weight="bold"> ${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facilityPrivateDetails.get("facilityName")?if_exists)),15)}</fo:block>
													</fo:table-cell>
													<#assign products = delegator.findOne("Product", {"productId" : privateTot.getKey()}, true)>
													<#assign productName= products.brandName>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="left" keep-together="always" font-weight="bold">${productName.replace(" ","-")?if_exists}</fo:block>
													</fo:table-cell>
													<#assign privateQty = (privateTot.getValue().get("recdQtyLtrs"))>
													 <#assign privatetotQty=privatetotQty+privateQty> 
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold"><#if (privateTot.getValue().get("recdQtyLtrs"))!=0>${privateTot.getValue().get("recdQtyLtrs")?if_exists?string("##0.0")}<#else>0.00</#if></fo:block>
													</fo:table-cell>
													<#assign privatetotQtyKgs=privatetotQtyKgs+(privateTot.getValue().get("recdQtyKgs"))> 
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold"><#if (privateTot.getValue().get("recdQtyKgs"))!=0>${privateTot.getValue().get("recdQtyKgs")?if_exists?string("##0.0")}<#else>0.00</#if></fo:block>
													</fo:table-cell>
													<#assign privatetotKgFat=privatetotKgFat+(privateTot.getValue().get("recdKgFat"))> 
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold"><#if (privateTot.getValue().get("recdKgFat"))!=0>${privateTot.getValue().get("recdKgFat")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
													</fo:table-cell>
													<#assign privatetotKgSnf=privatetotKgSnf+(privateTot.getValue().get("recdKgSnf"))>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold"><#if (privateTot.getValue().get("recdKgSnf"))!=0>${privateTot.getValue().get("recdKgSnf")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold"><#if (privateTot.getValue().get("receivedFat"))!=0>${privateTot.getValue().get("receivedFat")?string("##0.0")}0<#else>0.00</#if></fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold"><#if (privateTot.getValue().get("receivedSnf"))!=0>${privateTot.getValue().get("receivedSnf")?string("##0.00")}<#else>0.00</#if></fo:block>
													</fo:table-cell>
													<#assign privateUnitQty=privateProductWiseDaysMap.get(finalPrivateTotal.getKey())>
													<#assign privateTotalDays=(privateUnitQty.get(privateTot.getKey()).get("noofdays"))>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold">${privateTotalDays?if_exists}</fo:block>
													</fo:table-cell>
													<#assign privateAvgQty=(privateUnitQty.get(privateTot.getKey()).get("avgQtyLtrs"))>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold">${(privateAvgQty)?if_exists?string("#0")}</fo:block>
													</fo:table-cell>
						     					</fo:table-row>	
					    					</fo:table-body>
										</#if>
									</#if>
								</#list>
							</#list>
					    </fo:table>
				    </fo:block>
				    </#if>
			        <#if privateProductTotalMap?has_content>
			        <fo:block font-size="8pt" white-space-collapse="false" text-align="left">--------------------------------------------------------------------------------------------------------------------------------</fo:block>
					<fo:block font-family="Courier,monospace" font-size="5pt">
						<fo:table>
							<fo:table-column column-width="14pt"/>
							<fo:table-column column-width="50pt"/>
							<fo:table-column column-width="15pt"/>
							<fo:table-column column-width="45pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="25pt"/>
							<fo:table-column column-width="20pt"/>
							<fo:table-column column-width="15pt"/>
							<fo:table-column column-width="35pt"/>
							<fo:table-body>
								<#assign privateProductQty =0> 
								<#assign grandPrivateTotals = privateProductTotalMap.entrySet()>
				                <#list grandPrivateTotals as grandPrivateTot>
									<#if grandPrivateTot.getValue().get("recdQtyLtrs")!=0>
										<fo:table-row>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold">${grandPrivateTot.getKey()}</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="left" text-indent="2pt" font-weight="bold">TOTAL:</fo:block>
											</fo:table-cell>
											<#assign products = delegator.findOne("Product", {"productId" : grandPrivateTot.getKey()}, true)>
											<#assign productName= products.brandName>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="left" keep-together="always" font-weight="bold">${productName.replace(" ","-")?if_exists}</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold">${grandPrivateTot.getValue().get("recdQtyLtrs")?if_exists?string("##0.0")}</fo:block>
									        </fo:table-cell>
									        <fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold">${grandPrivateTot.getValue().get("recdQtyKgs")?if_exists?string("##0.0")}</fo:block>
									        </fo:table-cell>
									        <fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold">${grandPrivateTot.getValue().get("recdKgFat")?if_exists?string("##0.00")}</fo:block>
									        </fo:table-cell>
									        <fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold">${grandPrivateTot.getValue().get("recdKgSnf")?if_exists?string("##0.00")}</fo:block>
									        </fo:table-cell>
									        <#assign grandFat=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(grandPrivateTot.getValue().get("recdKgFat"),grandPrivateTot.getValue().get("recdQtyKgs")))>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold">${grandFat?if_exists?string("##0.0")}0</fo:block>
											</fo:table-cell>
											<#assign grandSnf=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(grandPrivateTot.getValue().get("recdKgSnf"),grandPrivateTot.getValue().get("recdQtyKgs")))>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold">${grandSnf?if_exists?string("##0.00")}</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold"></fo:block>
											</fo:table-cell>
											<#assign productPrivateGrandQty= productPrivateAvgMap.get(grandPrivateTot.getKey()).get("avgQtyLtrs")>
											<#assign privateProductQty = privateProductQty+productPrivateGrandQty>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold">${productPrivateGrandQty?if_exists?string("#0")}</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</#if>
								</#list>
							</fo:table-body>
						</fo:table>
				 	</fo:block>
				 	<fo:block font-size="8pt" white-space-collapse="false" text-align="left">&#160;  -------------------------------------------------------------------------------------------------------------------------</fo:block>
					<fo:block font-family="Courier,monospace" font-size="5pt">
					<fo:table>
						<fo:table-column column-width="64pt"/>
						<fo:table-column column-width="15pt"/>
						<fo:table-column column-width="45pt"/>
						<fo:table-column column-width="40pt"/>
						<fo:table-column column-width="40pt"/>
						<fo:table-column column-width="40pt"/>
						<fo:table-column column-width="25pt"/>
						<fo:table-column column-width="20pt"/>
						<fo:table-column column-width="15pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-body>
							<#assign privatetotFat =0>
							<#assign privatetotSnf =0>
							<#if privatetotQty!=0>
							 	<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" font-weight="bold" text-align="left">UNION/OTHER TOTAL:</fo:block>
							        </fo:table-cell>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" font-weight="bold" text-align="left"></fo:block>
							        </fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right">${privatetotQty?if_exists?string("##0.0")}</fo:block>
							        </fo:table-cell>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right">${privatetotQtyKgs?if_exists?string("##0.0")}</fo:block>
							        </fo:table-cell>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right">${privatetotKgFat?if_exists?string("##0.00")}</fo:block>
							        </fo:table-cell>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right">${privatetotKgSnf?if_exists?string("##0.00")}</fo:block>
							        </fo:table-cell>
							        <#assign privatetotFat=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(privatetotKgFat,privatetotQtyKgs))>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right">${privatetotFat?if_exists?string("##0.0")}0</fo:block>
							        </fo:table-cell>
							        <#assign privatetotSnf=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(privatetotKgSnf,privatetotQtyKgs))>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right">${privatetotSnf?if_exists?string("##0.00")}</fo:block>
							        </fo:table-cell>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right"></fo:block>
							        </fo:table-cell>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right">${privateProductQty?if_exists?string("#0")}</fo:block>
							        </fo:table-cell>
							    </fo:table-row>
							</#if>
						</fo:table-body>
					</fo:table>
					</fo:block>
					<fo:block font-size="8pt" white-space-collapse="false" text-align="left">--------------------------------------------------------------------------------------------------------------------------------</fo:block>
					</#if>
					<fo:block font-family="Courier,monospace" font-size="5pt">
						<fo:table>
							<fo:table-column column-width="14pt"/>
							<fo:table-column column-width="50pt"/>
							<fo:table-column column-width="15pt"/>
							<fo:table-column column-width="45pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="25pt"/>
							<fo:table-column column-width="20pt"/>
							<fo:table-column column-width="15pt"/>
							<fo:table-column column-width="35pt"/>
							<fo:table-body> 
								<#assign productFinalGrandQty=0>
								<#assign grandQty=0>
				                <#assign grandKgs=0>
				                <#assign grandKgFat=0>
				                <#assign grandKgSnf=0>
				                <#assign grandFat=0>
				                <#assign grandSnf=0>
								<#assign finTotals = finalTotalMap.entrySet()>
			                	<#list finTotals as finTot>
			                		<#if finTot.getValue().get("recdQtyLtrs")!=0>
										<fo:table-row>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="right"  font-weight="bold">${finTot.getKey()}</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="left" text-indent="5pt" font-weight="bold">TOTAL:</fo:block>
											</fo:table-cell>
											<#assign products = delegator.findOne("Product", {"productId" : finTot.getKey()}, true)>
											<#assign productName= products.brandName>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="left" keep-together="always" font-weight="bold">${productName.replace(" ","-")?if_exists}</fo:block>
											</fo:table-cell>
											<#assign grandQty=grandQty+finTot.getValue().get("recdQtyLtrs")>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold">${finTot.getValue().get("recdQtyLtrs")?if_exists?string("##0.0")}</fo:block>
											</fo:table-cell>
											<#assign grandKgs=grandKgs+finTot.getValue().get("recdQtyKgs")>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold">${finTot.getValue().get("recdQtyKgs")?if_exists?string("##0.0")}</fo:block>
											</fo:table-cell>
											<#assign grandKgFat=grandKgFat+finTot.getValue().get("recdKgFat")>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold">${finTot.getValue().get("recdKgFat")?if_exists?string("##0.00")}</fo:block>
											</fo:table-cell>
											<#assign grandKgSnf=grandKgSnf+finTot.getValue().get("recdKgSnf")>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold">${finTot.getValue().get("recdKgSnf")?if_exists?string("##0.00")}</fo:block>
											</fo:table-cell>
											 <#assign grandFat=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(finTot.getValue().get("recdKgFat"),finTot.getValue().get("recdQtyKgs")))>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold">${grandFat?if_exists?string("##0.0")}0</fo:block>
											</fo:table-cell>
											 <#assign grandSnf=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(finTot.getValue().get("recdKgSnf"),finTot.getValue().get("recdQtyKgs")))>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold">${grandSnf?if_exists?string("##0.00")}</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold"></fo:block>
											</fo:table-cell>
											<#assign productFinGrandQty= grandTotalAvgMap.get(finTot.getKey()).get("avgQtyLtrs")>
											<#assign productFinalGrandQty = productFinalGrandQty+productFinGrandQty>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold">${productFinGrandQty?if_exists?string("#0")}</fo:block>
											</fo:table-cell>
										</fo:table-row>
							 		</#if>
								</#list>
						    </fo:table-body>
					    </fo:table>
					</fo:block>
					<fo:block font-size="8pt" white-space-collapse="false" text-align="left">&#160;  -------------------------------------------------------------------------------------------------------------------------</fo:block>
			 		<fo:block font-family="Courier,monospace" font-size="5pt">
						<fo:table>
							<fo:table-column column-width="64pt"/>
							<fo:table-column column-width="15pt"/>
							<fo:table-column column-width="45pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="25pt"/>
							<fo:table-column column-width="20pt"/>
							<fo:table-column column-width="15pt"/>
							<fo:table-column column-width="35pt"/>
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" font-weight="bold" text-align="left">GRAND TOTAL:</fo:block>
							        </fo:table-cell>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" font-weight="bold" text-align="left"></fo:block>
							        </fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right" font-weight="bold">${grandQty?if_exists?string("##0.0")}</fo:block>
							        </fo:table-cell>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right" font-weight="bold">${grandKgs?if_exists?string("##0.0")}</fo:block>
							        </fo:table-cell>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right" font-weight="bold">${grandKgFat?if_exists?string("##0.00")}</fo:block>
							        </fo:table-cell>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right" font-weight="bold">${grandKgSnf?if_exists?string("##0.00")}</fo:block>
							        </fo:table-cell>
							        <#assign grandTotFat=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(grandKgFat,grandKgs))>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right" font-weight="bold">${grandTotFat?if_exists?string("##0.0")}0</fo:block>
							        </fo:table-cell>
							        <#assign grandTotSnf=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(grandKgSnf,grandKgs))>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right" font-weight="bold">${grandTotSnf?if_exists?string("##0.00")}</fo:block>
							        </fo:table-cell>
							        <fo:table-cell> 
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right" font-weight="bold"></fo:block>
							        </fo:table-cell>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right" font-weight="bold">${productFinalGrandQty?if_exists?string("#0")}</fo:block>
							        </fo:table-cell>
							    </fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:block>	
					<fo:block font-size="8pt" white-space-collapse="false" text-align="left">--------------------------------------------------------------------------------------------------------------------------------</fo:block>
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
