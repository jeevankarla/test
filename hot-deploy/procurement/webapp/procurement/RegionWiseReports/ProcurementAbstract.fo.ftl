<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-top="0.5in" >
                <fo:region-body margin-top=".9in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
            ${setRequestAttribute("OUTPUT_FILENAME", "RegionWiseProcAbstract.txt")}
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
     <#if regionMap?has_content>  
		<fo:page-sequence master-reference="main">
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="8pt">
			   	<fo:block font-size="5pt">VST_ASCII-015 VST_ASCII-027VST_ASCII-077</fo:block>        
				<fo:block text-align="left" white-space-collapse="false" keep-together="always">.                             REGION WISE REPORT OF MILK PROCUREMENT, KG-FAT,KG-SNF,TOTAL SOLIDS PERIOD FROM : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block>
				<fo:block font-size="6pt">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">.                                          BUFFALO MILK                                                                 COW MILK                                                                      TOTAL MIXED MILK                                  CURD MILK BOURNED BY</fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">--------------------------------------------------------------------------------------    ------------------------------------------------------------------------------    --------------------------------------------------------------------------- -------------------------</fo:block>
				<fo:block keep-together="always" font-size="5pt" white-space-collapse="false">SHED           QTY        QTY         KG.         KG.         TOTAL 	      AVG 	 AVG       QTY         QTY          KG.		        KG.			       TOTAL        AVG   AVG          QTY         QTY           KG.           KG.         TOTAL         AVG   AVG    PRODUCER   P.T.C</fo:block>
				<fo:block keep-together="always" font-size="5pt" white-space-collapse="false">.              KGS        LTS         FAT         SNF         SOLIDS       FAT   SNF       KGS         LTS          FAT          SNF          SOLIDS       FAT   SNF          KGS         LTS           FAT           SNF         SOLIDS        FAT   SNF     LTS        LTS </fo:block>
				<fo:block font-size="6pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">  
				<fo:block font-family="Courier,monospace" font-size="5pt">
					<fo:table>
						<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="33pt"/>
                       	<fo:table-column column-width="35pt"/>
                       	<fo:table-column column-width="40pt"/>
                       	<fo:table-column column-width="38pt"/>
                       	<fo:table-column column-width="27pt"/>
                       	<fo:table-column column-width="22pt"/>
                       	<fo:table-column column-width="35pt"/>
                       	<fo:table-column column-width="38pt"/>
                       	<fo:table-column column-width="38pt"/>
                       	<fo:table-column column-width="38pt"/>
                       	<fo:table-column column-width="42pt"/>
                       	<fo:table-column column-width="26pt"/>
                       	<fo:table-column column-width="21pt"/>
                       	<fo:table-column column-width="45pt"/>
                       	<fo:table-column column-width="38pt"/>
                       	<fo:table-column column-width="42pt"/>
                       	<fo:table-column column-width="42pt"/>
                       	<fo:table-column column-width="42pt"/>
                       	<fo:table-column column-width="26pt"/>
                       	<fo:table-column column-width="23pt"/>
                       	<fo:table-column column-width="27pt"/>
                       	<fo:table-column column-width="27pt"/>
						<#assign regionEntries = regionMap.entrySet()>
						<fo:table-body>
							<#list regionEntries as RegionNames>
								<#list RegionNames.getValue() as shedNames>
									<#assign shedEntries = finalshedMap.entrySet()>
									<#list shedEntries as shedValues>
										<#if shedValues.getKey()==shedNames>
											<#assign MixLtrs = 0>
											<#assign MixKgs  = 0>
											<#assign MixKgFat=0>
											<#assign MixKgSnf= 0>
											<#assign MixCurdLtrs= 0>
											<#assign MixPtcLtrs=0>
			           						<fo:table-row>
				           						<fo:table-cell>
				           							<fo:block keep-together="always" font-size="5pt" text-align="left">${shedValues.getKey()}</fo:block>
				           						</fo:table-cell>
				           						<#assign shedTotValues = shedValues.getValue().entrySet()>
				           						<#list shedTotValues as shedTotals>
				           							<#if shedTotals.getKey()!="TOT" && shedTotals.getKey()!="_NA_">
						           						<#assign Kgs = (shedTotals.getValue().get("qtyKgs"))>
														<#assign Ltrs = (shedTotals.getValue().get("qtyLtrs"))>
														<#assign KgFat = (shedTotals.getValue().get("kgFat"))>
														<#assign KgSnf = (shedTotals.getValue().get("kgSnf"))>
														<#assign CurdLtrs = (shedTotals.getValue().get("cQtyLtrs"))>
														<#assign PtcLtrs = (shedTotals.getValue().get("ptcCurd"))>
														<#assign MixLtrs = MixLtrs+Ltrs>
													 	<#assign MixKgs = MixKgs+ Kgs>
									    			 	<#assign MixKgFat = MixKgFat+KgFat>
	                                                 	<#assign MixKgSnf = MixKgSnf+KgSnf>
	                                                 	<#assign MixCurdLtrs=MixCurdLtrs+CurdLtrs>
					                                 	<#assign MixPtcLtrs=MixPtcLtrs+PtcLtrs>
														<fo:table-cell>           																	 																		
															<fo:block font-size="5pt" text-align="right">${Kgs?if_exists?string("##0.0")}</fo:block>     																	
														</fo:table-cell>
														<fo:table-cell>          
															<fo:block font-size="5pt" text-align="right">${Ltrs?if_exists?string("##0.0")}</fo:block>     																	
														</fo:table-cell>
														<fo:table-cell>           																	 																		
															<fo:block font-size="5pt" text-align="right">${KgFat?if_exists?string("##0.000")}</fo:block>     																	
														</fo:table-cell>
														<fo:table-cell>          
															<fo:block font-size="5pt" text-align="right">${KgSnf?if_exists?string("##0.000")}</fo:block>     																	
														</fo:table-cell>
														<fo:table-cell>          
															<fo:block font-size="5pt" text-align="right">${(KgFat+KgSnf)?if_exists?string("##0.000")}</fo:block>     																	
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
													<fo:block font-size="5pt"  text-align="right">${MixKgs?if_exists?string("##0.0")}</fo:block>     																	
												</fo:table-cell>
												<fo:table-cell>          
													<fo:block font-size="5pt"  text-align="right">${MixLtrs?if_exists?string("##0.0")}</fo:block>     																	
												</fo:table-cell>
												<fo:table-cell>           																	 																		
													<fo:block font-size="5pt" text-indent="40pt" text-align="right">${MixKgFat?if_exists?string("##0.000")}</fo:block>     																	
												</fo:table-cell>
												<fo:table-cell>          
													<fo:block font-size="5pt"  text-align="right">${MixKgSnf?if_exists?string("##0.000")}</fo:block>     																	
												</fo:table-cell>
												<fo:table-cell>          
													<fo:block font-size="5pt"  text-align="right">${(MixKgFat+MixKgSnf)?if_exists?string("##0.000")}</fo:block>     																	
												</fo:table-cell>
												<fo:table-cell>          
													<fo:block font-size="5pt"  text-align="right"><#if MixKgs !=0>${((MixKgFat*100)/MixKgs)?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>     																	
												</fo:table-cell>
												<fo:table-cell>          
													<fo:block  text-align="right"><#if MixKgs !=0>${((MixKgSnf*100)/MixKgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>     																	
												</fo:table-cell> 
												<fo:table-cell>          
													<fo:block font-size="5pt"  text-align="right">${MixCurdLtrs?if_exists?string("##0.0")}</fo:block>     																	
												</fo:table-cell>
												<fo:table-cell>          
													<fo:block font-size="5pt"  text-align="right">${(MixPtcLtrs)?if_exists?string("##0.0")}</fo:block>     																	
												</fo:table-cell>
			           						</fo:table-row>
			           					</#if>
			           				</#list>
	           					</#list>
	           					<fo:table-row>
		           					<fo:table-cell>
		           						<fo:block font-size="5pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		           					</fo:table-cell>
		           				</fo:table-row>
		           				<fo:table-row>
			           				<#assign regiontotMap = regiontotalMap.entrySet()>
			           				<#list regiontotMap as eachRegiontot>
			           					<#if eachRegiontot.getKey()==RegionNames.getKey()>
			           						<#assign TotMixLtrs = 0>
											<#assign TotMixKgs  = 0>
											<#assign TotMixKgFat=0>
											<#assign TotMixKgSnf= 0>
											<#assign TotMixCurdLtrs= 0>
											<#assign TotMixPtcLtrs=0>
			           						<fo:table-cell>           																	 																		
												<fo:block font-size="5pt" text-align="left">${eachRegiontot.getKey()}</fo:block>     																	
											</fo:table-cell>
			           						<#assign regionValueMap = eachRegiontot.getValue().entrySet()>
			           						<#list regionValueMap as regionValues>
			           							<#if regionValues.getKey()!="TOT" && regionValues.getKey()!="_NA_">
		           									<#assign regiontotKgs = (regionValues.getValue().get("qtyKgs"))>
													<#assign regiontotLtrs = (regionValues.getValue().get("qtyLtrs"))>
													<#assign regiontotKgFat = (regionValues.getValue().get("kgFat"))>
													<#assign regiontotKgSnf = (regionValues.getValue().get("kgSnf"))>
													<#assign regiontotCurdLtrs = (regionValues.getValue().get("cQtyLtrs"))>
													<#assign regiontotPtcLtrs = (regionValues.getValue().get("ptcCurd"))>
													<#assign TotMixLtrs = TotMixLtrs+regiontotLtrs>
												 	<#assign TotMixKgs = TotMixKgs+ regiontotKgs>
								    			 	<#assign TotMixKgFat = TotMixKgFat+regiontotKgFat>
                                                 	<#assign TotMixKgSnf = TotMixKgSnf+regiontotKgSnf>
                                                 	<#assign TotMixCurdLtrs=TotMixCurdLtrs+regiontotCurdLtrs>
				                                 	<#assign TotMixPtcLtrs=TotMixPtcLtrs+regiontotPtcLtrs>
													<fo:table-cell>           																	 																		
														<fo:block font-size="5pt" text-align="right">${regiontotKgs?if_exists?string("##0.0")}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>          
														<fo:block font-size="5pt" text-align="right">${regiontotLtrs?if_exists?string("##0.0")}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>           																	 																		
														<fo:block font-size="5pt" text-align="right">${regiontotKgFat?if_exists?string("##0.000")}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>          
														<fo:block font-size="5pt" text-align="right">${regiontotKgSnf?if_exists?string("##0.000")}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>          
														<fo:block font-size="5pt" text-align="right">${(regiontotKgFat+regiontotKgSnf)?if_exists?string("##0.000")}</fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>          
														<fo:block font-size="5pt" text-align="right"><#if regiontotKgs !=0>${((regiontotKgFat*100)/regiontotKgs)?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>     																	
													</fo:table-cell>
													<fo:table-cell>          
														<fo:block font-size="5pt" text-align="right"><#if regiontotKgs !=0>${((regiontotKgSnf*100)/regiontotKgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>     																	
													</fo:table-cell>
												</#if>
											</#list>
											<fo:table-cell>           																	 																		
												<fo:block font-size="5pt"  text-align="right">${TotMixKgs?if_exists?string("##0.0")}</fo:block>     																	
											</fo:table-cell>
											<fo:table-cell>          
												<fo:block font-size="5pt"  text-align="right">${TotMixLtrs?if_exists?string("##0.0")}</fo:block>     																	
											</fo:table-cell>
											<fo:table-cell>           																	 																		
												<fo:block font-size="5pt" text-indent="40pt" text-align="right">${TotMixKgFat?if_exists?string("##0.000")}</fo:block>     																	
											</fo:table-cell>
											<fo:table-cell>          
												<fo:block font-size="5pt"  text-align="right">${TotMixKgSnf?if_exists?string("##0.000")}</fo:block>     																	
											</fo:table-cell>
											<fo:table-cell>          
												<fo:block font-size="5pt"  text-align="right">${(TotMixKgFat+TotMixKgSnf)?if_exists?string("##0.000")}</fo:block>     																	
											</fo:table-cell>
											<fo:table-cell>          
												<fo:block font-size="5pt"  text-align="right"><#if TotMixKgs !=0>${((TotMixKgFat*100)/TotMixKgs)?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>     																	
											</fo:table-cell>
											<fo:table-cell>          
												<fo:block  text-align="right"><#if TotMixKgs !=0>${((TotMixKgSnf*100)/TotMixKgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>     																	
											</fo:table-cell> 
											<fo:table-cell>          
												<fo:block font-size="5pt"  text-align="right">${TotMixCurdLtrs?if_exists?string("##0.0")}</fo:block>     																	
											</fo:table-cell>
											<fo:table-cell>          
												<fo:block font-size="5pt"  text-align="right">${(TotMixPtcLtrs)?if_exists?string("##0.0")}</fo:block>     																	
											</fo:table-cell>
										</#if>
									</#list>			
	           					</fo:table-row>
	           					<fo:table-row>
		           					<fo:table-cell>
		           						<fo:block font-size="5pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		           					</fo:table-cell>
	           					</fo:table-row>
	           				</#list>
	           				<fo:table-row>
	           					<fo:table-cell>
	           						<fo:block font-size="5pt">TOTAL:</fo:block>
	           					</fo:table-cell>
	           					<#assign grTotMixLtrs = 0>
							 	<#assign grTotMixKgs = 0>
			    			 	<#assign grTotMixKgFat = 0>
                             	<#assign grTotMixKgSnf = 0>
                             	<#assign grTotMixCurdLtrs=0>
                             	<#assign grTotMixPtcLtrs=0>
	           					<#assign grandTotals = finaltotalMap.get("grandtotValues")>
	           					<#if grandTotals?has_content >
	           						<#assign productKeys = grandTotals.keySet()>
	           						<#list productKeys as prodKey>
	           							<#if prodKey!="TOT" && prodKey!="_NA_">
	           								<#assign grandtotKgs = (grandTotals.get(prodKey).get("qtyKgs"))>
											<#assign grandtotLtrs = (grandTotals.get(prodKey).get("qtyLtrs"))>
											<#assign grandtotKgFat = (grandTotals.get(prodKey).get("kgFat"))>
											<#assign grandtotKgSnf = (grandTotals.get(prodKey).get("kgSnf"))>
											<#assign grandCurdLtrs = (grandTotals.get(prodKey).get("cQtyLtrs"))>
											<#assign grandPtcLtrs = (grandTotals.get(prodKey).get("ptcCurd"))>
											<#assign grTotMixLtrs = grTotMixLtrs+grandtotLtrs>
										 	<#assign grTotMixKgs = grTotMixKgs+ grandtotKgs>
						    			 	<#assign grTotMixKgFat = grTotMixKgFat+grandtotKgFat>
                                         	<#assign grTotMixKgSnf = grTotMixKgSnf+grandtotKgSnf>
                                         	<#assign grTotMixCurdLtrs=grTotMixCurdLtrs+grandCurdLtrs>
		                                 	<#assign grTotMixPtcLtrs=grTotMixPtcLtrs+grandPtcLtrs>
		           							<fo:table-cell>
		           								<fo:block font-size="5pt" text-align="right">${grandtotKgs?if_exists?string("##0.0")}</fo:block>
		           							</fo:table-cell>
		           							<fo:table-cell>          
												<fo:block font-size="5pt" text-align="right">${grandtotLtrs?if_exists?string("##0.0")}</fo:block>     																	
											</fo:table-cell>
											<fo:table-cell>           																	 																		
												<fo:block font-size="5pt" text-align="right">${grandtotKgFat?if_exists?string("##0.000")}</fo:block>     																	
											</fo:table-cell>
											<fo:table-cell>          
												<fo:block font-size="5pt" text-align="right">${grandtotKgSnf?if_exists?string("##0.000")}</fo:block>     																	
											</fo:table-cell>
											<fo:table-cell>          
												<fo:block font-size="5pt" text-align="right">${(grandtotKgFat+grandtotKgSnf)?if_exists?string("##0.000")}</fo:block>     																	
											</fo:table-cell>
											<fo:table-cell>          
												<fo:block font-size="5pt" text-align="right"><#if grandtotKgs !=0>${((grandtotKgFat*100)/grandtotKgs)?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>     																	
											</fo:table-cell>
											<fo:table-cell>          
												<fo:block font-size="5pt" text-align="right"><#if grandtotKgs !=0>${((grandtotKgSnf*100)/grandtotKgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>     																	
											</fo:table-cell>
		           						</#if>
	           						</#list>
	           					</#if>
	           					<fo:table-cell>           																	 																		
									<fo:block font-size="5pt"  text-align="right">${grTotMixKgs?if_exists?string("##0.0")}</fo:block>     																	
								</fo:table-cell>
								<fo:table-cell>          
									<fo:block font-size="5pt"  text-align="right">${grTotMixLtrs?if_exists?string("##0.0")}</fo:block>     																	
								</fo:table-cell>
								<fo:table-cell>           																	 																		
									<fo:block font-size="5pt" text-indent="40pt" text-align="right">${grTotMixKgFat?if_exists?string("##0.000")}</fo:block>     																	
								</fo:table-cell>
								<fo:table-cell>          
									<fo:block font-size="5pt"  text-align="right">${grTotMixKgSnf?if_exists?string("##0.000")}</fo:block>     																	
								</fo:table-cell>
								<fo:table-cell>          
									<fo:block font-size="5pt"  text-align="right">${(grTotMixKgFat+grTotMixKgSnf)?if_exists?string("##0.000")}</fo:block>     																	
								</fo:table-cell>
								<fo:table-cell>          
									<fo:block font-size="5pt"  text-align="right"><#if grTotMixKgs !=0>${((grTotMixKgFat*100)/grTotMixKgs)?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>     																	
								</fo:table-cell>
								<fo:table-cell>          
									<fo:block  text-align="right"><#if grTotMixKgs !=0>${((grTotMixKgSnf*100)/grTotMixKgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>     																	
								</fo:table-cell> 
								<fo:table-cell>          
									<fo:block font-size="5pt"  text-align="right">${grTotMixCurdLtrs?if_exists?string("##0.0")}</fo:block>     																	
								</fo:table-cell>
								<fo:table-cell>          
									<fo:block font-size="5pt"  text-align="right">${(grTotMixPtcLtrs)?if_exists?string("##0.0")}</fo:block>     																	
								</fo:table-cell>	
           					</fo:table-row>
           					<fo:table-row>
	           					<fo:table-cell>
	           						<fo:block font-size="5pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
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