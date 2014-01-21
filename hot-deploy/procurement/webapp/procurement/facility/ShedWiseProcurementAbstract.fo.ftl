<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="16in" margin-top=".2in">
                <fo:region-body margin-top=".8in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
             <#if unitTotalsMap?has_content>  
        		<fo:page-sequence master-reference="main">
        			<fo:static-content flow-name="xsl-region-before" font-family="Helvetica" font-size="8pt">
        				<fo:block text-align="left" white-space-collapse="false" keep-together="always">MILK SHED NAME:  ${parameters.shedId}                                                  FORTINIGHT CONSOLIDATED REPORT OF MILK PROCUREMENT, KG-FAT,KG-SNF,TOTAL SOLIDS PERIOD FROM : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block>
        				<fo:block font-size="8pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
        				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="8pt">.                                                                                           BUFFALO MILK                                                                                                           COW MILK                                                                                                                TOTAL MIXED MILK                                                                     CURD MILK BOURNED BY</fo:block>
        				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="8pt">UNIT                                            ---------------------------------------------------------------------------------------------------------    ------------------------------------------------------------------------------------------------------------------     ---------------------------------------------------------------------------------------------------------        --------------------------------------- </fo:block>
        				<fo:block keep-together="always" white-space-collapse="false">CODE   NAME OF CENTER      KGS       LTS        KGFAT        KGSNF       TOT.SOLIDS    AV.FAT    AV.SNF        KGS             LTS           KGFAT         KGSNF       TOT.SOLIDS    AV.FAT      AV.SNF         KGS        LTS         KGFAT         KGSNF     TOT.SOLIDS    AV.FAT    AV.SNF         PROD.LTS    P.T.C LTS</fo:block>
        				<fo:block font-size="8pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
        			</fo:static-content>
       				<fo:flow flow-name="xsl-region-body" font-family="Helvetica">       				   
     					<#assign unitTotals = unitTotalsMap.entrySet()>				
        				<#assign grTotMixLtrs = 0>
     					<#assign grTotMixKgs  = 0>
    					<#assign grTotMixKgFat=0>
        				<#assign grTotMixKgSnf= 0>
        				<#assign grTotMixCurdLtrs= 0>
        				<#assign grTotMixPtcKgs=0>
     					<#list unitTotals as units>
     						<#assign facility = delegator.findOne("Facility", {"facilityId" : units.getKey()}, true)>
     						<#assign unitTotalEntries = units.getValue().entrySet()>
     						<#list unitTotalEntries as unitTotalEntry>
     							<#assign unitEntries = unitTotalEntry.getValue().entrySet()>     							
     							<fo:block font-family="Helvetica" font-size="8pt">
     							 	<fo:table>
     							 		<fo:table-column column-width="15pt"/>
                   						<fo:table-column column-width="45pt"/>
                   						<fo:table-column column-width="40pt"/>
                   						<fo:table-column column-width="40pt"/>
                   						<fo:table-column column-width="40pt"/>
                   						<fo:table-column column-width="40pt"/>
                   						<fo:table-column column-width="40pt"/>
                   						<fo:table-body>                   						
                   							<fo:table-row>
                   								<fo:table-cell>
                   									<fo:block>${facility.facilityCode?if_exists}</fo:block>
                   								</fo:table-cell>
                   								<fo:table-cell>
                   									<fo:block keep-together="always" text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("facilityName")?if_exists)),15)}</fo:block>
                   								</fo:table-cell>
                   								<fo:table-cell>
                   								<#list unitEntries as unitEntry>
     												<#assign allUnitEntries = unitEntry.getValue().entrySet()>
     												<#list allUnitEntries as allUnitEntry>
     													<#assign allUnitsTotal = allUnitEntry.getValue().entrySet()>    									
     													<#if allUnitEntry.getKey() =="TOT">
     														<#assign allUnitValueEntries = allUnitEntry.getValue().entrySet()>
     														<#list allUnitValueEntries as allUnitValues>
     															<#if allUnitValues.getKey() =="TOT">
     																<#assign  milkTypeTotals = allUnitValues.getValue().entrySet()>												                   																													
                   													<fo:block>
                   														<fo:table>          															
                   															<#--<#assign ListSize=(procurementProductList.size())>
                   															<#assign milkTypeSize=(milkTypeTotals.size())>
				                											<#list 0 .. ListSize-1 as product>
				                												<#list 0 .. milkTypeSize-1 as milkType>
				                													<fo:table-column column-width="70pt"/>
                   																	<fo:table-column column-width="45pt"/>
		                   															<fo:table-column column-width="50pt"/>
		                   															<fo:table-column column-width="50pt"/>
		                   															<fo:table-column column-width="50pt"/>
		                   															<fo:table-column column-width="35pt"/>
		                   															<fo:table-column column-width="45pt"/>
		                   															<fo:table-column column-width="50pt"/>
		                   															<fo:table-column column-width="50pt"/>		                   															
                   																</#list>	
                   															</#list>--> 
                   																<fo:table-column column-width="70pt"/>
               																	<fo:table-column column-width="40pt"/>
	                   															<fo:table-column column-width="50pt"/>
	                   															<fo:table-column column-width="45pt"/>
	                   															<fo:table-column column-width="50pt"/>
	                   															<fo:table-column column-width="30pt"/>
	                   															<fo:table-column column-width="40pt"/>
	                   															<fo:table-column column-width="50pt"/>
	                   															<fo:table-column column-width="45pt"/>
	                   															<fo:table-column column-width="45pt"/>
               																	<fo:table-column column-width="45pt"/>
	                   															<fo:table-column column-width="50pt"/>
	                   															<fo:table-column column-width="35pt"/>
	                   															<fo:table-column column-width="40pt"/>
	                   															<fo:table-column column-width="50pt"/>
	                   															<fo:table-column column-width="45pt"/>
	                   															<fo:table-column column-width="50pt"/>
	                   															<fo:table-column column-width="45pt"/>
	                   															<fo:table-column column-width="50pt"/>
               																	<fo:table-column column-width="35pt"/>
	                   															<fo:table-column column-width="35pt"/>
	                   															<fo:table-column column-width="50pt"/>
	                   															<fo:table-column column-width="50pt"/>
	                   															<fo:table-column column-width="35pt"/>
	                   															<fo:table-column column-width="45pt"/>
	                   															<fo:table-column column-width="50pt"/>
	                   															<fo:table-column column-width="50pt"/>                  																												
                   															<fo:table-body>
                   																<fo:table-row>
                   																	<#list procurementProductList as procProducts>	   
					     																<#list milkTypeTotals as milkTypeEntries>     																
					     																	<#if milkTypeEntries.getKey() == procProducts.productName>
					     																		 <#assign Kgs = (milkTypeEntries.getValue().get("qtyKgs")+(milkTypeEntries.getValue().get("sQtyLtrs")*1.03))>
					     																		 <#assign Ltrs = (milkTypeEntries.getValue().get("qtyLtrs")+milkTypeEntries.getValue().get("sQtyLtrs"))>
					     																		 <#assign KgFat = (milkTypeEntries.getValue().get("kgFat"))>
					     																		 <#assign KgSnf = (milkTypeEntries.getValue().get("kgSnf"))> 																		
                   																	<fo:table-cell>           																	 																		
                   																		<fo:block text-indent="40pt" text-align="right">${Kgs?if_exists?string("##0.0")}</fo:block>     																	
     																				</fo:table-cell>
                   																	<fo:table-cell>          
                   																		<fo:block text-indent="40pt" text-align="right">${Ltrs?if_exists?string("##0.0")}</fo:block>     																	
     																				</fo:table-cell>
     																				<fo:table-cell>           																	 																		
                   																		<fo:block text-indent="40pt" text-align="right">${KgFat}</fo:block>     																	
     																				</fo:table-cell>
                   																	<fo:table-cell>          
                   																		<fo:block text-indent="40pt" text-align="right">${KgSnf?if_exists?string("##0.000")}</fo:block>     																	
     																				</fo:table-cell>
     																				<fo:table-cell>          
                   																		<fo:block text-indent="40pt" text-align="right">${(KgFat+KgSnf)?if_exists?string("##0.000")}</fo:block>     																	
     																				</fo:table-cell>
     																				<fo:table-cell>          
                   																		<fo:block text-indent="40pt" text-align="right"><#if Kgs !=0>${((KgFat*100)/Kgs)?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>     																	
     																				</fo:table-cell>
     																				<fo:table-cell>          
                   																		<fo:block text-indent="40pt" text-align="right"><#if Kgs !=0>${((KgSnf*100)/Kgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>     																	
     																				</fo:table-cell>													     																				
     																				</#if>																		
                   																</#list>
                   															</#list>
                   															<#list milkTypeTotals as milkTypeEntries>
                   																<#if  milkTypeEntries.getKey() =="TOT">
		     																		<#assign mixKgs = (milkTypeEntries.getValue().get("qtyKgs")+(milkTypeEntries.getValue().get("sQtyLtrs")*1.03))>
		     																		 <#assign mixLtrs = (milkTypeEntries.getValue().get("qtyLtrs")+milkTypeEntries.getValue().get("sQtyLtrs"))>
		     																		 <#assign mixKgFat = (milkTypeEntries.getValue().get("kgFat"))>
		     																		 <#assign mixKgSnf = (milkTypeEntries.getValue().get("kgSnf"))>
		     																		 <#assign mixCurdLtrs=(milkTypeEntries.getValue().get("cQtyLtrs"))>
		     																		 <#assign mixPtcKgs=(milkTypeEntries.getValue().get("ptcQtyKgs"))>
		     																		 
		     																		 <#assign grTotMixLtrs = grTotMixLtrs+mixLtrs>
		     																		 <#assign grTotMixKgs = grTotMixKgs+ mixKgs>
		    														    			 <#assign grTotMixKgFat = grTotMixKgFat+mixKgFat>
		        				                                                     <#assign grTotMixKgSnf = grTotMixKgSnf+mixKgSnf>
		        				                                                     <#assign grTotMixCurdLtrs=grTotMixCurdLtrs+mixCurdLtrs>
		        				                                                     <#assign grTotMixPtcKgs=grTotMixPtcKgs+mixPtcKgs>	
     																				<fo:table-cell>           																	 																		
                   																		<fo:block text-indent="40pt" text-align="right">${mixKgs?if_exists?string("##0.0")}</fo:block>     																	
     																				</fo:table-cell>
                   																	<fo:table-cell>          
                   																		<fo:block text-indent="40pt" text-align="right">${mixLtrs?if_exists?string("##0.0")}</fo:block>     																	
     																				</fo:table-cell>
     																				<fo:table-cell>           																	 																		
                   																		<fo:block text-indent="40pt" text-align="right">${mixKgFat?if_exists?string("##0.000")}</fo:block>     																	
     																				</fo:table-cell>
                   																	<fo:table-cell>          
                   																		<fo:block text-indent="40pt" text-align="right">${mixKgSnf?if_exists?string("##0.000")}</fo:block>     																	
     																				</fo:table-cell>
     																				<fo:table-cell>          
                   																		<fo:block text-indent="40pt" text-align="right">${(mixKgFat+mixKgSnf)?if_exists?string("##0.000")}</fo:block>     																	
     																				</fo:table-cell>
     																				<fo:table-cell>          
                   																		<fo:block text-indent="40pt" text-align="right"><#if mixKgs !=0>${((mixKgFat*100)/mixKgs)?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>     																	
     																				</fo:table-cell>
     																				<fo:table-cell>          
                   																		<fo:block text-indent="40pt" text-align="right"><#if mixKgs !=0>${((mixKgSnf*100)/mixKgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>     																	
     																				</fo:table-cell> 
     																				<fo:table-cell>          
                   																		<fo:block text-indent="40pt" text-align="right">${mixCurdLtrs?if_exists?string("##0.0")}</fo:block>     																	
     																				</fo:table-cell>
     																				<fo:table-cell>          
                   																		<fo:block text-indent="40pt" text-align="right">${mixPtcKgs?if_exists?string("##0.0")}</fo:block>     																	
     																				</fo:table-cell>
     																			</#if>	
     																		</#list>	
                   																</fo:table-row>
                   																<fo:table-row>
                   																	<fo:table-cell>
                   																		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                   																	</fo:table-cell>
                   																</fo:table-row>
                   															</fo:table-body>
                   														</fo:table>                   													
                   													</fo:block>                   													
                   												</#if>  
                   											</#list>
                   										</#if>                  					
         											</#list>
         										</#list>	
                   							</fo:table-cell>
                   						</fo:table-row>                   				
                   					</fo:table-body>
     							 </fo:table>
     						</fo:block>        				
         				</#list>
         				</#list>
         				<fo:block font-size="8pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
         				<#if shedWiseTotalValues?has_content>
	         				<#assign shedWiseTotals = shedWiseTotalValues.entrySet()> 
	         				<fo:block font-size="8pt">
	        					<fo:table>
	        						<#assign ListSize=(procurementProductList.size())>
										<#--<#assign milkTypeSize=(shedWiseTotals.size())>
										<#list 0 .. ListSize-1 as product>
											<#list 0 .. milkTypeSize-1 as milkType>
											<fo:table-column column-width="80pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="40pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>
											<fo:table-column column-width="50pt"/>												
											</#list>	
										</#list>-->
										<fo:table-column column-width="80pt"/>
										<fo:table-column column-width="40pt"/>
										<fo:table-column column-width="50pt"/>
										<fo:table-column column-width="45pt"/>
										<fo:table-column column-width="50pt"/>
										<fo:table-column column-width="50pt"/>
										<fo:table-column column-width="30pt"/>
										<fo:table-column column-width="40pt"/>
										<fo:table-column column-width="46pt"/>
										<fo:table-column column-width="45pt"/>
										<fo:table-column column-width="47pt"/>
										<fo:table-column column-width="50pt"/>
										<fo:table-column column-width="50pt"/>
										<fo:table-column column-width="30pt"/>
										<fo:table-column column-width="40pt"/>
										<fo:table-column column-width="53pt"/>
										<fo:table-column column-width="50pt"/>
										<fo:table-column column-width="45pt"/>
										<fo:table-column column-width="50pt"/>
										<fo:table-column column-width="50pt"/>
										<fo:table-column column-width="30pt"/>
										<fo:table-column column-width="35pt"/>
										<fo:table-column column-width="50pt"/>
										<fo:table-column column-width="50pt"/>
										<fo:table-column column-width="45pt"/>
										<fo:table-column column-width="50pt"/>
										<fo:table-column column-width="50pt"/>
	                   				<fo:table-body>
	                   					<fo:table-row>           						             						
	                   						<fo:table-cell>
	                   							<fo:block keep-together="always"> TOTAL :</fo:block>
	                   						</fo:table-cell>
	                   					<#list procurementProductList as procProducts>           						  
	                   						<#list shedWiseTotals as shedWistTotEntries>    
	                   							<#if procProducts.productName == shedWistTotEntries.getKey()>          						
			                   						<fo:table-cell>
			                   							<fo:block keep-together="always" text-indent="40pt" text-align="right">${(shedWistTotEntries.getValue().get("qtyKgs")+(shedWistTotEntries.getValue().get("sQtyLtrs")*1.03))?string("##0.0")}</fo:block>
			                   						</fo:table-cell>
			                   						<fo:table-cell>
			                   							<fo:block keep-together="always" text-indent="40pt" text-align="right">${(shedWistTotEntries.getValue().get("qtyLtrs")+shedWistTotEntries.getValue().get("sQtyLtrs"))?string("##0.0")}</fo:block>
			                   						</fo:table-cell>
			                   						<fo:table-cell>
			                   							<fo:block keep-together="always" text-indent="40pt" text-align="right">${((shedWistTotEntries.getValue().get("kgFat")))?string("##0.000")}</fo:block>
			                   						</fo:table-cell>
			                   						<fo:table-cell>
			                   							<fo:block keep-together="always" text-indent="40pt" text-align="right">${(shedWistTotEntries.getValue().get("kgSnf"))?string("##0.000")}</fo:block>
			                   						</fo:table-cell>
			                   						<fo:table-cell>
			                   							<fo:block keep-together="always" text-indent="40pt" text-align="right">${((shedWistTotEntries.getValue().get("kgSnf"))+(shedWistTotEntries.getValue().get("kgFat")))?if_exists?string("##0.000")}</fo:block>
			                   						</fo:table-cell>
			                   						<fo:table-cell>
			                   							<fo:block keep-together="always" text-indent="40pt" text-align="right">${(shedWistTotEntries.getValue().get("fat"))?string("##0.0")}</fo:block>
			                   						</fo:table-cell>
			                   						<fo:table-cell>
			                   							<fo:block keep-together="always" text-indent="40pt" text-align="right">${(shedWistTotEntries.getValue().get("snf"))?string("##0.00")}</fo:block>
			                   						</fo:table-cell>
	                   							</#if>	
	                   						</#list>	
	                   					</#list>                   						
	                   						<fo:table-cell>
	                   							<fo:block keep-together="always" text-indent="40pt" text-align="right">${grTotMixKgs?if_exists?string("##0.0")}</fo:block>
	                   						</fo:table-cell>
	                   						<fo:table-cell>
	                   							<fo:block keep-together="always" text-indent="40pt" text-align="right">${grTotMixLtrs?if_exists?string("##0.0")}</fo:block>
	                   						</fo:table-cell>
	                   						<fo:table-cell>
	                   							<fo:block keep-together="always" text-indent="40pt" text-align="right">${grTotMixKgFat?if_exists?string("##0.000")}</fo:block>
	                   						</fo:table-cell>
	                   						<fo:table-cell>
	                   							<fo:block keep-together="always" text-indent="40pt" text-align="right">${grTotMixKgSnf?if_exists?string("##0.000")}</fo:block>
	                   						</fo:table-cell>
	                   						<fo:table-cell>
	                   							<fo:block keep-together="always" text-indent="40pt" text-align="right">${(grTotMixKgFat+grTotMixKgSnf)?if_exists?string("##0.000")}</fo:block>
	                   						</fo:table-cell>
	                   						<fo:table-cell>
	                   							<fo:block keep-together="always" text-indent="40pt" text-align="right"><#if grTotMixKgs !=0>${((grTotMixKgFat*100)/grTotMixKgs)?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
	                   						</fo:table-cell>
	                   						<fo:table-cell>
	                   							<fo:block keep-together="always" text-indent="40pt" text-align="right"><#if grTotMixKgs !=0>${((grTotMixKgSnf*100)/grTotMixKgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
	                   						</fo:table-cell>    
	                   						<fo:table-cell>
	                   							<fo:block keep-together="always" text-indent="40pt" text-align="right">${grTotMixCurdLtrs?if_exists?string("##0.0")}</fo:block>
	                   						</fo:table-cell>
	                   						<fo:table-cell>
	                   							<fo:block keep-together="always" text-indent="40pt" text-align="right">${grTotMixPtcKgs?if_exists?string("##0.0")}</fo:block>
	                   						</fo:table-cell>         					        						
	                   					</fo:table-row>
	                   				</fo:table-body>
	        					</fo:table>
	        				</fo:block>
	        			</#if>	
        				<fo:block font-size="8pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>     				
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