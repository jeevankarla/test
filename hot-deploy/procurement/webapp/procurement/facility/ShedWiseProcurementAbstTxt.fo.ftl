<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-top=".2in">
                <fo:region-body margin-top=".7in"/>
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
             <#if finalUnitTotMap?has_content>                           
             ${setRequestAttribute("OUTPUT_FILENAME", "shedProcAbst.txt")}
        		<fo:page-sequence master-reference="main">
        			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="5pt">
        				<fo:block font-size="5pt">VST_ASCII-015 VST_ASCII-027VST_ASCII-077</fo:block>
        				<fo:block text-align="left" white-space-collapse="false" keep-together="always">MILK SHED NAME:  ${parameters.shedId}          FORTINIGHT CONSOLIDATED REPORT OF MILK PROCUREMENT, KG-FAT,KG-SNF,TOTAL SOLIDS PERIOD FROM : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block>
        				<fo:block font-size="5pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
        				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">.             						                		BUFFALO MILK                                                                  COW MILK                                                                    TOTAL MIXED MILK                                         CURD MILK BOURNED BY</fo:block>
        				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">UNIT                 --------------------------------------------------------------------------    ------------------------------------------------------------------------     ----------------------------------------------------------------------  ------------------------</fo:block>
        				<fo:block keep-together="always" white-space-collapse="false">CODE     UNIT NAME        KGS       LTS        KGFAT       KGSNF     TOT.SOLIDS   AV.FAT  AV.SNF      KGS       LTS        KGFAT       KGSNF     TOT.SOLIDS   AV.FAT  AV.SNF      KGS       LTS        KGFAT      KGSNF     TOT.SOLIDS   AV.FAT  AV.SNF  PROD.LTS  P.T.C LTS</fo:block>
        				<fo:block font-size="5pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
        			</fo:static-content>
       				<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">      
       				<fo:block></fo:block> 				   
     					<#assign unitTotals = finalUnitTotMap.entrySet()>				
        				<#assign grTotMixLtrs = 0>
     					<#assign grTotMixKgs  = 0>
    					<#assign grTotMixKgFat=0>
        				<#assign grTotMixKgSnf= 0>
        				<#assign grTotMixCurdLtrs= 0>
        				<#assign grTotMixPtcLtrs=0>
     					<#list unitTotals as units>
     						<#assign facility = delegator.findOne("Facility", {"facilityId" : units.getKey()}, true)>
     						<#assign unitTotalEntries = units.getValue()>
     							<fo:block font-family="Courier,monospace" font-size="5pt">
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
													<fo:block>
														<fo:table>												
															<fo:table-column column-width="35pt"/>
															<fo:table-column column-width="35pt"/>
   															<fo:table-column column-width="35pt"/>
   															<fo:table-column column-width="35pt"/>
   															<fo:table-column column-width="35pt"/>
   															<fo:table-column column-width="25pt"/>
   															<fo:table-column column-width="25pt"/>
   															<fo:table-column column-width="35pt"/>
   															<fo:table-column column-width="35pt"/>
   															<fo:table-column column-width="35pt"/>
															<fo:table-column column-width="40pt"/>
   															<fo:table-column column-width="35pt"/>
   															<fo:table-column column-width="25pt"/>
   															<fo:table-column column-width="25pt"/>
   															<fo:table-column column-width="35pt"/>
   															<fo:table-column column-width="35pt"/>
   															<fo:table-column column-width="35pt"/>
   															<fo:table-column column-width="35pt"/>
   															<fo:table-column column-width="35pt"/>
															<fo:table-column column-width="25pt"/>
   															<fo:table-column column-width="25pt"/>
   															<fo:table-column column-width="25pt"/>
   															<fo:table-column column-width="25pt"/>
   															<fo:table-column column-width="35pt"/>
   															<fo:table-column column-width="45pt"/>
   															<fo:table-column column-width="50pt"/>
   															<fo:table-column column-width="50pt"/>                  																												
														<fo:table-body>
															<fo:table-row>
															<#list procurementProductList as procProducts>	   
 																<#assign kgs= unitTotalEntries.get(procProducts.brandName+"QtyKgs")>
 																<#assign Ltrs= unitTotalEntries.get(procProducts.brandName+"QtyLtrs")>
 																<#assign KgFat= unitTotalEntries.get(procProducts.brandName+"kgFat")>
 																<#assign KgSnf= unitTotalEntries.get(procProducts.brandName+"kgSnf")>
 																<#assign solids= unitTotalEntries.get(procProducts.brandName+"Solids")>     																																				
																<fo:table-cell>           																	 																		
																	<fo:block text-indent="40pt" text-align="right">${kgs?if_exists?string("##0.0")}</fo:block>     																	
																</fo:table-cell>
																<fo:table-cell>          
																	<fo:block text-indent="40pt" text-align="right">${Ltrs?if_exists?string("##0.0")}</fo:block>     																	
																</fo:table-cell>
																<fo:table-cell>           																	 																		
																	<fo:block text-indent="40pt" text-align="right">${KgFat?if_exists?string("##0.000")}</fo:block>     																	
																</fo:table-cell>
																<fo:table-cell>          
																	<fo:block text-indent="40pt" text-align="right">${KgSnf?if_exists?string("##0.000")}</fo:block>     																	
																</fo:table-cell>
																<fo:table-cell>          
																	<fo:block text-indent="40pt" text-align="right">${(solids)?if_exists?string("##0.000")}</fo:block>     																	
																</fo:table-cell>
																<fo:table-cell>          
																	<fo:block text-indent="40pt" text-align="right"><#if kgs !=0>${((KgFat*100)/kgs)?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>     																	
																</fo:table-cell>
																<fo:table-cell>          
																	<fo:block text-indent="40pt" text-align="right"><#if kgs !=0>${((KgSnf*100)/kgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>     																	
																</fo:table-cell>												     																				
															</#list>
																<fo:table-cell>           																	 																		
																	<fo:block text-indent="40pt" text-align="right">${unitTotalEntries.get("totQtyKgs")?if_exists?string("##0.0")}</fo:block>     																	
																</fo:table-cell>
																<fo:table-cell>          
																	<fo:block text-indent="40pt" text-align="right">${unitTotalEntries.get("totQtyLtrs")?if_exists?string("##0.0")}</fo:block>     																	
																</fo:table-cell>
																<fo:table-cell>           																	 																		
																	<fo:block text-indent="40pt" text-align="right">${unitTotalEntries.get("totKgFat")?if_exists?string("##0.000")}</fo:block>     																	
																</fo:table-cell>
																<fo:table-cell>          
																	<fo:block text-indent="40pt" text-align="right">${unitTotalEntries.get("totKgSnf")?if_exists?string("##0.000")}</fo:block>     																	
																</fo:table-cell>
																<fo:table-cell>          
																	<fo:block text-indent="40pt" text-align="right">${(unitTotalEntries.get("totSolids"))?if_exists?string("##0.000")}</fo:block>     																	
																</fo:table-cell>
																<fo:table-cell>          
																	<fo:block text-indent="40pt" text-align="right"><#if unitTotalEntries.get("totQtyKgs") !=0>${((unitTotalEntries.get("totKgFat")*100)/unitTotalEntries.get("totQtyKgs"))?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>     																	
																</fo:table-cell>
																<fo:table-cell>          
																	<fo:block text-indent="40pt" text-align="right"><#if unitTotalEntries.get("totQtyKgs") !=0>${((unitTotalEntries.get("totKgSnf")*100)/unitTotalEntries.get("totQtyKgs"))?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>     																	
																</fo:table-cell> 
																<fo:table-cell>          
																	<fo:block text-indent="40pt" text-align="right">${unitTotalEntries.get("curdQtyLtrs")?if_exists?string("##0.0")}</fo:block>     																	
																</fo:table-cell>
																<fo:table-cell>          
																	<fo:block text-indent="40pt" text-align="right">${(unitTotalEntries.get("ptcCurdQty"))?if_exists?string("##0.0")}</fo:block>     																	
																</fo:table-cell>
													
															</fo:table-row>
															<fo:table-row>
																<fo:table-cell>
																	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
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
         				</#list>
         				<fo:block font-size="5pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
         				<#if shedTotalsMap?has_content>
	         				<fo:block font-size="5pt">
	        					<fo:table>
	        						<#assign ListSize=(procurementProductList.size())>										
										<fo:table-column column-width="35pt"/>
										<fo:table-column column-width="57pt"/>
										<fo:table-column column-width="37pt"/>
										<fo:table-column column-width="35pt"/>
										<fo:table-column column-width="35pt"/>
										<fo:table-column column-width="35pt"/>
										<fo:table-column column-width="25pt"/>
										<fo:table-column column-width="25pt"/>
										<fo:table-column column-width="35pt"/>
										<fo:table-column column-width="35pt"/>
										<fo:table-column column-width="35pt"/>
										<fo:table-column column-width="40pt"/>
										<fo:table-column column-width="35pt"/>
										<fo:table-column column-width="25pt"/>
										<fo:table-column column-width="25pt"/>
										<fo:table-column column-width="35pt"/>
										<fo:table-column column-width="35pt"/>
										<fo:table-column column-width="37pt"/>
										<fo:table-column column-width="35pt"/>
										<fo:table-column column-width="38pt"/>
										<fo:table-column column-width="20pt"/>
										<fo:table-column column-width="25pt"/>
										<fo:table-column column-width="25pt"/>
										<fo:table-column column-width="25pt"/>
										<fo:table-column column-width="45pt"/>
										<fo:table-column column-width="50pt"/>
										<fo:table-column column-width="50pt"/>  
	                   				<fo:table-body>
	                   					<fo:table-row>    
	                   						<fo:table-cell>
	                   							<fo:block keep-together="always" text-align="right"> TOTAL :</fo:block>
	                   						</fo:table-cell>
	                   					<#list procurementProductList as procProducts>            							   						
	                   						<fo:table-cell>
	                   							<fo:block text-align="right">${shedTotalsMap.get(procProducts.brandName+"QtyKgs")?string("##0.0")}</fo:block>
	                   						</fo:table-cell>
	                   						<fo:table-cell>
	                   							<fo:block text-align="right">${shedTotalsMap.get(procProducts.brandName+"QtyLtrs")?string("##0.0")}</fo:block>
	                   						</fo:table-cell>
	                   						<fo:table-cell>
	                   							<fo:block text-align="right">${shedTotalsMap.get(procProducts.brandName+"kgFat")?string("##0.000")}</fo:block>
	                   						</fo:table-cell>
	                   						<fo:table-cell>
	                   							<fo:block text-align="right">${shedTotalsMap.get(procProducts.brandName+"kgSnf")?string("##0.000")}</fo:block>
	                   						</fo:table-cell>
	                   						<fo:table-cell>
	                   							<fo:block text-align="right">${(shedTotalsMap.get(procProducts.brandName+"kgFat")+shedTotalsMap.get(procProducts.brandName+"kgSnf"))?if_exists?string("##0.000")}</fo:block>
	                   						</fo:table-cell>
	                   						<fo:table-cell>
	                   							<fo:block text-align="right"><#if shedTotalsMap.get(procProducts.brandName+"QtyKgs") !=0>${((shedTotalsMap.get(procProducts.brandName+"kgFat")*100)/shedTotalsMap.get(procProducts.brandName+"QtyKgs"))?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
	                   						</fo:table-cell>
	                   						<fo:table-cell>
	                   							<fo:block text-align="right"><#if shedTotalsMap.get(procProducts.brandName+"QtyKgs") !=0>${((shedTotalsMap.get(procProducts.brandName+"kgSnf")*100)/shedTotalsMap.get(procProducts.brandName+"QtyKgs"))?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
	                   						</fo:table-cell>             						
	                   					</#list>                   						
	                   						<fo:table-cell>
	                   							<fo:block text-align="right">${shedTotalsMap.get("totMixQtyKgs")?if_exists?string("##0.0")}</fo:block>
	                   						</fo:table-cell>
	                   						<fo:table-cell>
	                   							<fo:block text-align="right">${shedTotalsMap.get("totMixQtyLtrs")?if_exists?string("##0.0")}</fo:block>
	                   						</fo:table-cell>
	                   						<fo:table-cell>
	                   							<fo:block text-align="right">${shedTotalsMap.get("totMixKgFat")?if_exists?string("##0.000")}</fo:block>
	                   						</fo:table-cell>
	                   						<fo:table-cell>
	                   							<fo:block text-align="right">${shedTotalsMap.get("totMixKgSnf")?if_exists?string("##0.000")}</fo:block>
	                   						</fo:table-cell>
	                   						<fo:table-cell>
	                   							<fo:block text-align="right">${shedTotalsMap.get("totMixSolids")?if_exists?string("##0.000")}</fo:block>
	                   						</fo:table-cell>
	                   						<fo:table-cell>
	                   							<fo:block text-align="right"><#if shedTotalsMap.get("totMixQtyKgs") !=0>${((shedTotalsMap.get("totMixKgFat")*100)/shedTotalsMap.get("totMixQtyKgs"))?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
	                   						</fo:table-cell>
	                   						<fo:table-cell>
	                   							<fo:block text-align="right"><#if shedTotalsMap.get("totMixQtyKgs") !=0>${((shedTotalsMap.get("totMixKgSnf")*100)/shedTotalsMap.get("totMixQtyKgs"))?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
	                   						</fo:table-cell>    
	                   						<fo:table-cell>
	                   							<fo:block text-align="right">${shedTotalsMap.get("totMixCurdLtrs")?if_exists?string("##0.0")}</fo:block>
	                   						</fo:table-cell>
	                   						<fo:table-cell>
	                   							<fo:block text-align="right">${shedTotalsMap.get("totMixPtcCurd")?if_exists?string("##0.0")}</fo:block>
	                   						</fo:table-cell>         					        						
	                   					</fo:table-row>
	                   				</fo:table-body>
	        					</fo:table>
	        				</fo:block>
	        			</#if>	
        				<fo:block font-size="5pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>     				
        			<!--	<fo:block font-size="5pt">VST_ASCII-012 VST_ASCII-027VST_ASCII-080</fo:block> -->
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