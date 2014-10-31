<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-top=".3in" margin-left=".3in" margin-bottom=".3in">
                <fo:region-body margin-top="1.7in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
         ${setRequestAttribute("OUTPUT_FILENAME", "FederationUnitsMlkRcptsValueFrmShed.txt")}
    <#if sheWiseProcurementMap?has_content> 
		<fo:page-sequence master-reference="main">
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" >
				<fo:block font-size="5pt" >VST_ASCII-015 VST_ASCII-027VST_ASCII-103</fo:block>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">FEDERATION ABSTRACT        FORTINIGHT CONSOLIDATED REPORT OF MILK PROCUREMENT,KG-FAT,KG-SNF,TOTAL SOLIDS PERIOD FROM :  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MM-yyyy")} TO:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MM-yyyy")}</fo:block>
				<fo:block font-size="5pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">.                                          BUFFALO MILK                                                                              COW MILK                                                            TOTAL MIXED MILK           CURD MILK BOURNED BY</fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">-------------------------------------------------------------------------------------------------------------------  ---------------------------------------------------------------  ---------------------------------------------------------------------------------- --------------------</fo:block>
				<fo:block keep-together="always" font-size="5pt" white-space-collapse="false">SHED   NAME OF THE          QTY        QTY        KG.         KG.        TOTAL 	 AVG 	   AVG        QTY      QTY     KG.		     KG.			   TOTAL    AVG     AVG          QTY        QTY          KG.       KG.      TOTAL    AVG   AVG   PRODUCER  P.T.C</fo:block>
				<fo:block keep-together="always" font-size="5pt" white-space-collapse="false">CODE   MILK SHED            KGS        LTS        FAT         SNF        SOLIDS   FAT    SNF        KGS      LTS     FAT       SNF      SOLIDS   FAT     SNF          KGS        LTS          FAT       SNF      SOLIDS   FAT   SNF    LTS     LTS </fo:block>
				<fo:block font-size="5pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				<fo:block>
					<fo:table>
						<fo:table-column column-width="17pt"/>
						<fo:table-column column-width="50pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="28pt"/>
                       	<fo:table-column column-width="32pt"/>
                       	<fo:table-column column-width="37pt"/>
                       	<fo:table-column column-width="40pt"/>
                       	<fo:table-column column-width="20pt"/>
                       	<fo:table-column column-width="25pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="25pt"/>
                       	<fo:table-column column-width="25pt"/>
                       	<fo:table-column column-width="33pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="22pt"/>
                       	<fo:table-column column-width="27pt"/>
                       	<fo:table-column column-width="36pt"/>
                       	<fo:table-column column-width="36pt"/>
                       	<fo:table-column column-width="35pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="20pt"/>
                       	<fo:table-column column-width="20pt"/>
                       	<fo:table-column column-width="21pt"/>
                       	<fo:table-column column-width="25pt"/>
						<fo:table-body>
							<#assign shedwiseDetails=sheWiseProcurementMap.entrySet()>       						
           					<#list shedwiseDetails as shedwiseData>
	           					<#if shedwiseData.getKey()!="TOT">
	           						<#assign facility = delegator.findOne("Facility", {"facilityId" : shedwiseData.getKey()}, true)>
									<fo:table-row>
										<#assign MixLtrs = 0>
										<#assign MixKgs  = 0>
										<#assign MixKgFat=0>
										<#assign MixKgSnf= 0>
										<#assign MixCurdLtrs= 0>
										<#assign MixPtcLtrs=0>
										<fo:table-cell>
											<fo:block font-size="5pt" text-align="left">${facility.get("facilityCode")}</fo:block>     																	
										</fo:table-cell>
										<fo:table-cell>
											<fo:block font-size="5pt" text-align="left" keep-togehter="always">${(Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("description").toUpperCase())),16))?if_exists}</fo:block>     																	
										</fo:table-cell>
										<#assign shedData=shedwiseData.getValue().entrySet()>
										<#list shedData as shedDetails>
											<#if shedDetails.getKey()!="TOT" && shedDetails.getKey()!="_NA_">
												<#assign Kgs = (shedDetails.getValue().get("qtyKgs"))>
												<#assign Ltrs = (shedDetails.getValue().get("qtyLtrs"))>
												<#assign KgFat = (shedDetails.getValue().get("kgFat"))>
												<#assign KgSnf = (shedDetails.getValue().get("kgSnf"))>
												<#assign CurdLtrs = (shedDetails.getValue().get("cQtyLtrs"))>
												<#assign PtcLtrs = (shedDetails.getValue().get("ptcCurd"))>
												<#assign MixLtrs = MixLtrs+Ltrs>
											 	<#assign MixKgs = MixKgs+ Kgs>
							    			 	<#assign MixKgFat = MixKgFat+KgFat>
                                             	<#assign MixKgSnf = MixKgSnf+KgSnf>
                                             	<#assign MixCurdLtrs=MixCurdLtrs+CurdLtrs>
			                                 	<#assign MixPtcLtrs=MixPtcLtrs+PtcLtrs>
												<fo:table-cell>
													<fo:block font-size="3.5pt" text-align="right">${Kgs?if_exists?string("##0.0")}</fo:block>     																	
												</fo:table-cell>
												<fo:table-cell>
													<fo:block font-size="3.5pt" text-align="right">${Ltrs?if_exists?string("##0.0")}</fo:block>     																	
												</fo:table-cell>
												<fo:table-cell>
													<fo:block font-size="3.5pt" text-align="right">${KgFat?if_exists?string("##0.000")}</fo:block>     																	
												</fo:table-cell>
												<fo:table-cell>
													<fo:block font-size="3.5pt" text-align="right">${KgSnf?if_exists?string("##0.000")}</fo:block>     																	
												</fo:table-cell>
												<fo:table-cell>
													<fo:block font-size="3.5pt" text-align="right">${(KgFat+KgSnf)?if_exists?string("##0.000")}</fo:block>     																	
												</fo:table-cell>
												<fo:table-cell>          
													<fo:block font-size="5pt" text-align="right"><#if Kgs !=0>${((KgFat*100)/Kgs)?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>     																	
												</fo:table-cell>
												<fo:table-cell>          
													<fo:block font-size="5pt" text-align="right"><#if Kgs !=0>${((KgSnf*100)/Kgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>     																	
												</fo:table-cell>
											</#if>
										</#list>
										<fo:table-cell>           																	 																		
											<fo:block font-size="3.5pt"  text-align="right">${MixKgs?if_exists?string("##0.0")}</fo:block>     																	
										</fo:table-cell>
										<fo:table-cell>          
											<fo:block font-size="3.5pt"  text-align="right">${MixLtrs?if_exists?string("##0.0")}</fo:block>     																	
										</fo:table-cell>
										<fo:table-cell>           																	 																		
											<fo:block font-size="3.5pt" text-align="right">${MixKgFat?if_exists?string("##0.000")}</fo:block>     																	
										</fo:table-cell>
										<fo:table-cell>          
											<fo:block font-size="3.5pt"  text-align="right">${MixKgSnf?if_exists?string("##0.000")}</fo:block>     																	
										</fo:table-cell>
										<fo:table-cell>          
											<fo:block font-size="3.5pt"  text-align="right">${(MixKgFat+MixKgSnf)?if_exists?string("##0.000")}</fo:block>     																	
										</fo:table-cell>
										<fo:table-cell>          
											<fo:block font-size="3.5pt"  text-align="right"><#if MixKgs !=0>${((MixKgFat*100)/MixKgs)?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>     																	
										</fo:table-cell>
										<fo:table-cell>          
											<fo:block  font-size="3.5pt" text-align="right"><#if MixKgs !=0>${((MixKgSnf*100)/MixKgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>     																	
										</fo:table-cell> 
										<fo:table-cell>          
											<fo:block font-size="3.5pt"  text-align="right">${MixCurdLtrs?if_exists?string("##0.0")}</fo:block>     																	
										</fo:table-cell>
										<fo:table-cell>          
											<fo:block font-size="5pt"  text-align="right">${(MixPtcLtrs)?if_exists?string("##0.0")}</fo:block>     																	
										</fo:table-cell>
	           						</fo:table-row>
	           					<#else>
	           						<fo:table-row>
			           					<fo:table-cell>
			           						<fo:block font-size="5pt">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			           					</fo:table-cell>
		           					</fo:table-row>
	           						<fo:table-row>
	           							<#assign totMixLtrs =0>
									 	<#assign totMixKgs = 0>
					    			 	<#assign totMixKgFat = 0>
                                     	<#assign totMixKgSnf = 0>
                                     	<#assign totMixCurdLtrs=0>
	                                 	<#assign totMixPtcLtrs=0>
		           						<fo:table-cell>
											<fo:block font-size="5pt" text-align="left"></fo:block>     																	
										</fo:table-cell>
										<fo:table-cell>
											<fo:block font-size="5pt" text-align="left">TOTAL</fo:block>     																	
										</fo:table-cell>
											<#assign shedTotData=shedwiseData.getValue().entrySet()>
											<#list shedTotData as shedTotDetails>
												<#if shedTotDetails.getKey()!="TOT" && shedTotDetails.getKey()!="_NA_">
													<#assign totKgs = (shedTotDetails.getValue().get("qtyKgs"))>
													<#assign totLtrs = (shedTotDetails.getValue().get("qtyLtrs"))>
													<#assign totKgFat = (shedTotDetails.getValue().get("kgFat"))>
													<#assign totKgSnf = (shedTotDetails.getValue().get("kgSnf"))>
													<#assign totCurdLtrs = (shedTotDetails.getValue().get("cQtyLtrs"))>
													<#assign totPtcLtrs = (shedTotDetails.getValue().get("ptcCurd"))>
													<#assign totMixLtrs = totMixLtrs+totLtrs>
												 	<#assign totMixKgs = totMixKgs+ totKgs>
								    			 	<#assign totMixKgFat = totMixKgFat+totKgFat>
                                                 	<#assign totMixKgSnf = totMixKgSnf+totKgSnf>
                                                 	<#assign totMixCurdLtrs=totMixCurdLtrs+totCurdLtrs>
				                                 	<#assign totMixPtcLtrs=totMixPtcLtrs+totPtcLtrs>
				                                 	<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${totKgs?if_exists?string("##0.0")}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${totLtrs?if_exists?string("##0.0")}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${totKgFat?if_exists?string("##0.000")}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${totKgSnf?if_exists?string("##0.000")}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${(totKgFat+totKgSnf)?if_exists?string("##0.000")}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>          
														<fo:block font-size="5pt" text-align="right"><#if totKgs !=0>${((totKgFat*100)/totKgs)?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>          
														<fo:block font-size="5pt" text-align="right"><#if totKgs !=0>${((totKgSnf*100)/totKgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>     																	
													</fo:table-cell>
												</#if>
											</#list>
											<fo:table-cell>           																	 																		
												<fo:block font-size="3.5pt"  text-align="right">${totMixKgs?if_exists?string("##0.0")}</fo:block>     																	
											</fo:table-cell>
											<fo:table-cell>          
												<fo:block font-size="3.5pt"  text-align="right">${totMixLtrs?if_exists?string("##0.0")}</fo:block>     																	
											</fo:table-cell>
											<fo:table-cell>           																	 																		
												<fo:block font-size="3.5pt" text-align="right">${totMixKgFat?if_exists?string("##0.000")}</fo:block>     																	
											</fo:table-cell>
											<fo:table-cell>          
												<fo:block font-size="3.5pt"  text-align="right">${totMixKgSnf?if_exists?string("##0.000")}</fo:block>     																	
											</fo:table-cell>
											<fo:table-cell>          
												<fo:block font-size="3.5pt"  text-align="right">${(totMixKgFat+totMixKgSnf)?if_exists?string("##0.000")}</fo:block>     																	
											</fo:table-cell>
											<fo:table-cell>          
												<fo:block font-size="3.5pt"  text-align="right"><#if totMixKgs !=0>${((totMixKgFat*100)/totMixKgs)?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>     																	
											</fo:table-cell>
											<fo:table-cell>          
												<fo:block  font-size="3.5pt" text-align="right"><#if totMixKgs !=0>${((totMixKgSnf*100)/totMixKgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>     																	
											</fo:table-cell> 
											<fo:table-cell>          
												<fo:block font-size="3.5pt"  text-align="right">${totMixCurdLtrs?if_exists?string("##0.0")}</fo:block>     																	
											</fo:table-cell>
											<fo:table-cell>          
												<fo:block font-size="5pt"  text-align="right">${(totMixPtcLtrs)?if_exists?string("##0.0")}</fo:block>     																	
											</fo:table-cell>
		           						</fo:table-row>
		           						<fo:table-row>
				           					<fo:table-cell>
				           						<fo:block font-size="5pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				           					</fo:table-cell>
			           					</fo:table-row>
		           					</#if>
								</#list>
							</fo:table-body>
						</fo:table>
					</fo:block>
					<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">FEDERATION ABSTRACT        FORTINIGHT CONSOLIDATED REPORT OF MILK PAYMENT,DECUCTIONS, NET AMOUNT PAYABLE AND T.I.P DEDUCTION, SHARE-CAPITAL DEDUCTIONS PERIOD FROM :  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MM-yyyy")} TO:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MM-yyyy")}</fo:block>
					<fo:block font-size="5pt">---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
					<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">.                                          DETAILS OF GROSS AMOUNT                                                                 DETAILS OF RECOVERIES                                                                                                      </fo:block>
					<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">------------------------------------------------------------------------------------------------  -------------------------------------------------------------------------------------------------------------------------------------</fo:block>
					<fo:block keep-together="always" font-size="5pt" white-space-collapse="false">NAME OF THE              B.M         C.M         COMSN       CART      ADDN 	    GROSS 	  VIJAYA(RD)  VACCINE  SEED   VIJAYA(LN)		STORES(T)  STORES(A)	 FEED	  TESTER   SPARES   C.O.SALE   STANRY  OTHERS     TOTAL      NET      T.I.P   GROSS+TIP</fo:block>
					<fo:block keep-together="always" font-size="5pt" white-space-collapse="false">MILK SHED                AMOUNT      AMOUNT      AMOUNT      AMOUNT    AMOUNT    AMOUNT   AMOUNT     AMOUNT  AMOUNT   AMOUNT    AMOUNT   AMOUNT     AMOUNT    AMOUNT   AMOUNT     AMOUNT   AMOUNT   AMOUNT   DEDEUCTION   AMOUNT   M.P.A   (LACKS RS) </fo:block>
					<fo:block font-size="5pt">---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
					<fo:block>
						<fo:table>
							<fo:table-column column-width="50pt"/>
	                       	<fo:table-column column-width="40pt"/>
	                       	<fo:table-column column-width="32pt"/>
	                       	<fo:table-column column-width="35pt"/>
	                       	<fo:table-column column-width="35pt"/>
	                       	<fo:table-column column-width="30pt"/>
	                       	<fo:table-column column-width="35pt"/>
	                       	<fo:table-column column-width="28pt"/>
	                       	<fo:table-column column-width="25pt"/>
	                       	<fo:table-column column-width="22pt"/>
	                       	<fo:table-column column-width="28pt"/>
	                       	<fo:table-column column-width="30pt"/>
	                       	<fo:table-column column-width="30pt"/>
	                       	<fo:table-column column-width="29pt"/>
	                       	<fo:table-column column-width="32pt"/>
	                       	<fo:table-column column-width="30pt"/>
	                       	<fo:table-column column-width="28pt"/>
	                       	<fo:table-column column-width="30pt"/>
	                       	<fo:table-column column-width="23pt"/>
	                       	<fo:table-column column-width="35pt"/>
	                       	<fo:table-column column-width="36pt"/>
	                       	<fo:table-column column-width="34pt"/>
	                       	<fo:table-column column-width="26pt"/>
							<fo:table-body>
								<#assign shedwiseDetails=sheWiseProcurementMap.entrySet()>       						
	           					<#list shedwiseDetails as shedwiseAmount>
		           					<#if shedwiseAmount.getKey()!="TOT">
		           						<#assign facility = delegator.findOne("Facility", {"facilityId" : shedwiseAmount.getKey()}, true)>
										<fo:table-row>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="left" keep-togehter="always">${(Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("description").toUpperCase())),16))?if_exists}</fo:block>     																	
											</fo:table-cell>
											<#assign shedAmounts=shedwiseAmount.getValue().entrySet()>
											<#list shedAmounts as shedAmountDetails>
												<#if shedAmountDetails.getKey()!="TOT" && shedAmountDetails.getKey()!="_NA_">
													<#if shedAmountDetails.getKey()=="101">
														<#assign bmAmount=shedAmountDetails.getValue().get("price")>
														<fo:table-cell>
															<fo:block font-size="3.5pt" text-align="right">${bmAmount?if_exists?string("##0.00")}</fo:block>     																	
														</fo:table-cell>
													<#else>
														<#assign cmAmount=shedAmountDetails.getValue().get("price")>
														<fo:table-cell>
															<fo:block font-size="3.5pt" text-align="right">${cmAmount?if_exists?string("##0.00")}</fo:block>     																	
														</fo:table-cell>
													</#if>
												</#if>
												<#if shedAmountDetails.getKey()=="TOT">
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("commissionAmount")?if_exists?string("##0.00")}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("cartage")?if_exists?string("##0.00")}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("grsAddn")?if_exists?string("##0.00")}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("grossAmt")?if_exists?string("##0.00")}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("MILKPROC_VIJAYARD")?if_exists}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("MILKPROC_VACCINE")?if_exists}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("MILKPROC_SEEDDED")?if_exists}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("MILKPROC_VIJAYALN")?if_exists}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("MILKPROC_STORET")?if_exists}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("MILKPROC_STOREA")?if_exists}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("MILKPROC_FEEDDED")?if_exists}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("MILKPROC_MTESTER")?if_exists}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("MILKPROC_MSPARES")?if_exists}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("MILKPROC_CESSONSALE")?if_exists}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("MILKPROC_STATONRY")?if_exists}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("MILKPROC_OTHERDED")?if_exists}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("grsDed")?if_exists?string("##0.00")}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("netAmt")?if_exists?string("##0.00")}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("tipAmt")?if_exists?string("##0.00")}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${((shedAmountDetails.getValue().get("grossAmt")+shedAmountDetails.getValue().get("tipAmt"))/100000)?if_exists?string("##0.00")}</fo:block>     																	
													</fo:table-cell>
												</#if>
											</#list>
										</fo:table-row>
									</#if>
								</#list>
								<fo:table-row>
		           					<fo:table-cell>
		           						<fo:block font-size="5pt">---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		           					</fo:table-cell>
	           					</fo:table-row>
	           					<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="5pt" text-align="left">TOTAL</fo:block>     																	
									</fo:table-cell>
									<#assign shedwiseDetails=sheWiseProcurementMap.entrySet()>       						
		           					<#list shedwiseDetails as shedwiseAmount>
			           					<#if shedwiseAmount.getKey()=="TOT">
			           						<#assign shedAmounts=shedwiseAmount.getValue().entrySet()>
											<#list shedAmounts as shedAmountDetails>
												<#if shedAmountDetails.getKey()!="TOT" && shedAmountDetails.getKey()!="_NA_">
													<#if shedAmountDetails.getKey()=="101">
														<#assign bmAmount=shedAmountDetails.getValue().get("price")>
														<fo:table-cell>
															<fo:block font-size="3.5pt" text-align="right">${bmAmount?if_exists?string("##0.00")}</fo:block>     																	
														</fo:table-cell>
													<#else>
														<#assign cmAmount=shedAmountDetails.getValue().get("price")>
														<fo:table-cell>
															<fo:block font-size="3.5pt" text-align="right">${cmAmount?if_exists?string("##0.00")}</fo:block>     																	
														</fo:table-cell>
													</#if>
												</#if>
												<#if shedAmountDetails.getKey()=="TOT">
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("commissionAmount")?if_exists?string("##0.00")}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("cartage")?if_exists?string("##0.00")}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("grsAddn")?if_exists?string("##0.00")}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("grossAmt")?if_exists?string("##0.00")}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("MILKPROC_VIJAYARD")?if_exists}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("MILKPROC_VACCINE")?if_exists}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("MILKPROC_SEEDDED")?if_exists}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("MILKPROC_VIJAYALN")?if_exists}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("MILKPROC_STORET")?if_exists}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("MILKPROC_STOREA")?if_exists}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("MILKPROC_FEEDDED")?if_exists}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("MILKPROC_MTESTER")?if_exists}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("MILKPROC_MSPARES")?if_exists}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("MILKPROC_CESSONSALE")?if_exists}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("MILKPROC_STATONRY")?if_exists}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("MILKPROC_OTHERDED")?if_exists}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("grsDed")?if_exists?string("##0.00")}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("netAmt")?if_exists?string("##0.00")}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${shedAmountDetails.getValue().get("tipAmt")?if_exists?string("##0.00")}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="3.5pt" text-align="right">${((shedAmountDetails.getValue().get("grossAmt")+shedAmountDetails.getValue().get("tipAmt"))/100000)?if_exists?string("##0.00")}</fo:block>     																	
													</fo:table-cell>
												</#if>
											</#list>
										</#if>
									</#list>
								</fo:table-row>
								<fo:table-row>
		           					<fo:table-cell>
		           						<fo:block font-size="5pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
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
		            	${uiLabelMap.OrderNoOrderFound}.
		       		 </fo:block>
		    	</fo:flow>
			</fo:page-sequence>	
		</#if>
	</fo:root>
</#escape>