<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
           ${setRequestAttribute("OUTPUT_FILENAME", "CenterWiseMonth.txt")}
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" >
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
     <#if finalMap?has_content>  
		<fo:page-sequence master-reference="main">
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="8pt">
			    <fo:block font-size="5pt">VST_ASCII-015 VST_ASCII-027VST_ASCII-077</fo:block>
				<#assign facility = delegator.findOne("Facility", {"facilityId" : parameters.unitId}, true)>
				<#assign centerFacility = delegator.findOne("Facility", {"facilityId" : centerFacilityId}, true)>
				<fo:block font-size="5pt" text-align="left" white-space-collapse="false" keep-together="always">.									                                                      YEARLY CONSOLIDATED REPORT OF MILK PROCUREMENT, KG-FAT,KG-SNF,TOTAL SOLIDS PERIOD FROM: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "MMM d, yyyy")}   TO   ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "MMM d, yyyy")}</fo:block>
				<fo:block font-size="5pt" text-align="left" white-space-collapse="false" keep-together="always">.								                                                          MILK SHED NAME:  ${parameters.shedId}  						          UNIT NAME:${facility.getString("facilityName")?if_exists}                         CENTER NAME:  ${centerFacility.getString("facilityName")?if_exists}                     </fo:block>
				<fo:block font-size="6pt">---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">.                                            BUFFALO MILK                                                                   COW MILK                                                                     TOTAL MIXED MILK                             CURD MILK BOURNED BY</fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">MONTH             -----------------------------------------------------------------------        --------------------------------------------------------------------------  ----------------------------------------------------------------------   ----------------------</fo:block>
				<fo:block keep-together="always" font-size="6pt" white-space-collapse="false">YEAR              KGS         LTS        KGFAT       KGSNF      TOT.SOLIDS  AV.FAT AV.SNF  		   KGS          LTS         KGFAT       KGSNF     TOT.SOLIDS  AV.FAT  AV.SNF       KGS         LTS        KGFAT        KGSNF     TOT.SOLIDS  AV.FAT  AV.SNF    PROD.LTS   P.T.C LTS </fo:block>
				<fo:block font-size="6pt">---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">       				   
				<#assign monthTotals = finalMap.entrySet()>
				<#assign grTotMixLtrs = 0>
				<#assign grTotMixKgs  = 0>
				<#assign grTotMixKgFat=0>
				<#assign grTotMixKgSnf= 0>
				<#assign grTotMixCurdLtrs= 0>
				<#assign grTotMixPtcLtrs=0>
				<#list monthTotals as monthTot> 
					<#assign monthTotalEntries = monthTot.getValue().entrySet()>				
						<fo:block font-family="Courier,monospace" font-size="5pt">
						 	<fo:table>
						 		<fo:table-column column-width="15pt"/>
           						<fo:table-column column-width="20pt"/>
           						<fo:table-column column-width="40pt"/>
           						<fo:table-column column-width="40pt"/>
           						<fo:table-column column-width="40pt"/>
           						<fo:table-column column-width="40pt"/>
           						<fo:table-column column-width="40pt"/>
           						<fo:table-body>                   						
           							<fo:table-row>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left">${monthTot.getKey()}</fo:block>
           								</fo:table-cell>
	           								<fo:table-cell>
											<fo:block>
												<fo:table>          															
														 <fo:table-column column-width="52pt"/>
                                                       <fo:table-column column-width="35pt"/>
                                                       <fo:table-column column-width="35pt"/>
                                                       <fo:table-column column-width="38pt"/>
                                                       <fo:table-column column-width="40pt"/>
                                                       <fo:table-column column-width="22pt"/>
                                                       <fo:table-column column-width="25pt"/>
                                                       <fo:table-column column-width="36pt"/>
                                                       <fo:table-column column-width="35pt"/>
                                                       <fo:table-column column-width="45pt"/>
                                                       <fo:table-column column-width="37pt"/>
                                                       <fo:table-column column-width="40pt"/>
                                                       <fo:table-column column-width="20pt"/>
                                                       <fo:table-column column-width="24pt"/>
                                                       <fo:table-column column-width="35pt"/>
                                                       <fo:table-column column-width="37pt"/>
                                                       <fo:table-column column-width="40pt"/>
                                                       <fo:table-column column-width="42pt"/>
                                                       <fo:table-column column-width="40pt"/>
                                                       <fo:table-column column-width="17pt"/>
                                                       <fo:table-column column-width="22pt"/>
                                                       <fo:table-column column-width="40pt"/>
                                                       <fo:table-column column-width="30pt"/>
                                                       <fo:table-column column-width="40pt"/>
                                                       <fo:table-column column-width="40pt"/>
                                                       <fo:table-column column-width="40pt"/>
                                                       <fo:table-column column-width="36pt"/>                  																												
													<fo:table-body>
														<fo:table-row>
															<#list procurementProductList as procProducts>
 																<#list monthTotalEntries as month>     																
 																	<#if month.getKey() == procProducts.productId>
 																	 <#assign Kgs = (month.getValue().get("qtyKgs"))>
																	 <#assign Ltrs = (month.getValue().get("qtyLtrs"))>
																	 <#assign KgFat = (month.getValue().get("kgFat"))>
																	 <#assign KgSnf = (month.getValue().get("kgSnf"))> 
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
														</#list>
														<#list monthTotalEntries as month> 
															<#if  month.getKey() =="TOT">
																 <#assign mixKgs = (month.getValue().get("qtyKgs"))>
																 <#assign mixLtrs = (month.getValue().get("qtyLtrs"))>
																 <#assign mixKgFat = (month.getValue().get("kgFat"))>
																 <#assign mixKgSnf = (month.getValue().get("kgSnf"))>
																 <#assign mixCurdLtrs=(month.getValue().get("cQtyLtrs"))>
		     													 <#assign mixPtcLtrs=(month.getValue().get("ptcCurd"))> 
																 
																 <#assign grTotMixLtrs = grTotMixLtrs+mixLtrs>
																 <#assign grTotMixKgs = grTotMixKgs+ mixKgs>
												    			 <#assign grTotMixKgFat = grTotMixKgFat+mixKgFat>
			                                                     <#assign grTotMixKgSnf = grTotMixKgSnf+mixKgSnf>
			                                                     <#assign grTotMixCurdLtrs=grTotMixCurdLtrs+mixCurdLtrs>
		        				                                 <#assign grTotMixPtcLtrs=grTotMixPtcLtrs+mixPtcLtrs>	
																<fo:table-cell>           																	 																		
																	<fo:block font-size="5pt" text-align="right">${mixKgs?if_exists?string("##0.0")}</fo:block>     																	
																</fo:table-cell>
																<fo:table-cell>          
																	<fo:block font-size="5pt" text-align="right">${mixLtrs?if_exists?string("##0.0")}</fo:block>     																	
																</fo:table-cell>
																<fo:table-cell>           																	 																		
																	<fo:block font-size="5pt" text-align="right">${mixKgFat?if_exists?string("##0.000")}</fo:block>     																	
																</fo:table-cell>
																<fo:table-cell>          
																	<fo:block font-size="5pt" text-align="right">${mixKgSnf?if_exists?string("##0.000")}</fo:block>     																	
																</fo:table-cell>
																<fo:table-cell>          
																	<fo:block font-size="5pt" text-align="right">${(mixKgFat+mixKgSnf)?if_exists?string("##0.000")}</fo:block>     																	
																</fo:table-cell>
																<fo:table-cell>          
																	<fo:block font-size="5pt" text-align="right"><#if mixKgs !=0>${((mixKgFat*100)/mixKgs)?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>     																	
																</fo:table-cell>
																<fo:table-cell>          
																	<fo:block font-size="5pt" text-align="right"><#if mixKgs !=0>${((mixKgSnf*100)/mixKgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>     																	
																</fo:table-cell> 
																<fo:table-cell>          
																	<fo:block font-size="5pt" text-align="right">${mixCurdLtrs?if_exists?string("##0.0")}</fo:block>     																	
																</fo:table-cell>
																<fo:table-cell>          
																	<fo:block font-size="5pt" text-align="right">${(mixPtcLtrs)?if_exists?string("##0.0")}</fo:block>     																	
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
										</fo:table-cell>
									</fo:table-row>                   				
							</fo:table-body>
			 			</fo:table>
					</fo:block>
					  </#list>      				
     				<fo:block font-size="6pt">---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
     				<#if totalMap?has_content>
         				<fo:block font-size="5pt">
        					<fo:table>
        						<#assign totals = totalMap.entrySet()>
									<fo:table-column column-width="37pt"/>
									<fo:table-column column-width="30pt"/>
                                                       <fo:table-column column-width="35pt"/>
                                                       <fo:table-column column-width="35pt"/>
                                                       <fo:table-column column-width="38pt"/>
                                                       <fo:table-column column-width="40pt"/>
                                                       <fo:table-column column-width="22pt"/>
                                                       <fo:table-column column-width="25pt"/>
                                                       <fo:table-column column-width="35pt"/>
                                                       <fo:table-column column-width="37pt"/>
                                                       <fo:table-column column-width="45pt"/>
                                                       <fo:table-column column-width="35pt"/>
                                                       <fo:table-column column-width="40pt"/>
                                                       <fo:table-column column-width="20pt"/>
                                                       <fo:table-column column-width="25pt"/>
                                                       <fo:table-column column-width="35pt"/>
                                                       <fo:table-column column-width="35pt"/>
                                                       <fo:table-column column-width="41pt"/>
                                                       <fo:table-column column-width="42pt"/>
                                                       <fo:table-column column-width="42pt"/>
                                                       <fo:table-column column-width="17pt"/>
                                                       <fo:table-column column-width="23pt"/>
                                                       <fo:table-column column-width="38pt"/>
                                                       <fo:table-column column-width="30pt"/>
                                                       <fo:table-column column-width="40pt"/>
                                                       <fo:table-column column-width="40pt"/>
                                                       <fo:table-column column-width="40pt"/>
                                                       <fo:table-column column-width="36pt"/>
                   				<fo:table-body>
                   					<fo:table-row>           						             						
                   						<fo:table-cell>
                   							<fo:block keep-together="always"> TOTAL :</fo:block>
                   						</fo:table-cell>
                   					<#list procurementProductList as procProducts>
                   					    <#list totals as tot>
                   					     <#if tot.getKey() == procProducts.productId>
										 <#assign totKgs = (tot.getValue().get("qtyKgs"))>
										 <#assign totLtrs = (tot.getValue().get("qtyLtrs"))>
										 <#assign totKgFat = (tot.getValue().get("kgFat"))>
										 <#assign totKgSnf = (tot.getValue().get("kgSnf"))>       							   						
                   						<fo:table-cell>
                   							<fo:block text-align="right">${totKgs?if_exists?string("##0.0")}</fo:block>
                   						</fo:table-cell>
                   						<fo:table-cell>
                   							<fo:block text-align="right">${totLtrs?if_exists?string("##0.0")}</fo:block>
                   						</fo:table-cell>
                   						<fo:table-cell>
                   							<fo:block text-align="right">${totKgFat?if_exists?string("##0.000")}</fo:block>
                   						</fo:table-cell>
                   						<fo:table-cell>
                   							<fo:block text-align="right">${totKgSnf?if_exists?string("##0.000")}</fo:block>
                   						</fo:table-cell>
                   						<fo:table-cell>          
											<fo:block font-size="5pt" text-align="right">${(totKgFat+totKgSnf)?if_exists?string("##0.000")}</fo:block>     																	
										</fo:table-cell>
										<fo:table-cell>          
											<fo:block font-size="5pt" text-align="right"><#if totKgs !=0>${((totKgFat*100)/totKgs)?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>     																	
										</fo:table-cell>
										<fo:table-cell>          
											<fo:block font-size="5pt" text-align="right"><#if totKgs !=0>${((totKgSnf*100)/totKgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>     																	
										</fo:table-cell>             						
                   					</#if>																		
								 </#list>
								  </#list>                  						
                   						<fo:table-cell>
                   							<fo:block text-align="right">${grTotMixKgs?if_exists?string("##0.0")}</fo:block>
                   						</fo:table-cell>
                   						<fo:table-cell>
                   							<fo:block text-align="right">${grTotMixLtrs?if_exists?string("##0.0")}</fo:block>
                   						</fo:table-cell>
                   						<fo:table-cell>
                   							<fo:block text-align="right">${grTotMixKgFat?if_exists?string("##0.000")}</fo:block>
                   						</fo:table-cell>
                   						<fo:table-cell>
                   							<fo:block text-align="right">${grTotMixKgSnf?if_exists?string("##0.000")}</fo:block>
                   						</fo:table-cell>
                   						<fo:table-cell>
                   							<fo:block text-align="right">${(grTotMixKgFat+grTotMixKgSnf)?if_exists?string("##0.000")}</fo:block>
                   						</fo:table-cell>
                   						<fo:table-cell>
                   							<fo:block text-align="right"><#if grTotMixKgs !=0>${((grTotMixKgFat*100)/grTotMixKgs)?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
                   						</fo:table-cell>
                   						<fo:table-cell>
                   							<fo:block text-align="right"><#if grTotMixKgs !=0>${((grTotMixKgSnf*100)/grTotMixKgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
                   						</fo:table-cell>    
                   						<fo:table-cell>
                   							<fo:block text-align="right">${grTotMixCurdLtrs?if_exists?string("##0.0")}</fo:block>
                   						</fo:table-cell>
                   						<fo:table-cell>
                   							<fo:block text-align="right">${grTotMixPtcLtrs?if_exists?string("##0.0")}</fo:block>
                   						</fo:table-cell>         					        						
                   					</fo:table-row>
                   				</fo:table-body>
        					</fo:table>
        				</fo:block>
        			</#if>	
    			<fo:block font-size="6pt">---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>     				
        		<fo:block font-size="5pt" linefeed-treatment="preserve">&#xA;</fo:block>
        		<fo:block font-size="5pt" linefeed-treatment="preserve">&#xA;</fo:block>
        		<fo:block font-size="5pt" linefeed-treatment="preserve">&#xA;</fo:block>
                <fo:block font-size="5pt" text-align="left" white-space-collapse="false" keep-together="always">.             									                                          YEARLY CONSOLIDATED REPORT OF MILK PAYMENT, DEDUCTIONS, NET AMOUNT PAYABLE AND T.I.P DEDUCTION, SHARE-CAPITAL DEDUCTIONS PERIOD FROM: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "MMM d, yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "MMM d, yyyy")}</fo:block>
				<fo:block font-size="5pt" text-align="left" white-space-collapse="false" keep-together="always">.                                           			                              MILK SHED NAME:  ${parameters.shedId}  				         UNIT NAME:${facility.getString("facilityName")?if_exists}                         CENTER NAME:  ${centerFacility.getString("facilityName")?if_exists}                     </fo:block>
				<fo:block font-size="6pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                 <fo:block font-size="7pt">
                 	<fo:table >
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="15pt"/>
                    <fo:table-column column-width="0.1pt"/>  
               	    <fo:table-column column-width="35pt"/>
                    <fo:table-column column-width="40pt"/>  
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="45pt"/>  
                    <fo:table-column column-width="40pt"/>
                    <#list orderAdjItemsList as orderAdjItems>
                    <fo:table-column column-width="47pt"/>
                    </#list>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="15pt"/>
                    <fo:table-column column-width="30pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="50pt"/>       
                    <fo:table-column column-width="40pt"/>             
		          	<fo:table-header border-width="1pt" >
		          	    
		            	<fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="left" >MONTH</fo:block><fo:block font-size="7pt" text-align="left">YEAR</fo:block></fo:table-cell>	
		            	<fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block></fo:block></fo:table-cell>	                    	                  
		            	<fo:table-cell><fo:block font-size="2pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="left" keep-together="always" white-space-collapse="false">.            DETAILS OF GROSS AMOUNT</fo:block></fo:table-cell>
		            	<#list procurementProductList as procProducts>
		            	<fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="left">--------</fo:block><fo:block font-size="7pt" text-align="right">${procProducts.brandName}</fo:block><fo:block font-size="7pt" text-align="right">AMOUNT</fo:block></fo:table-cell>		                    	                  		            
	                    </#list>
		           		<fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="left">----------</fo:block><fo:block font-size="7pt" text-align="center">COMSN</fo:block><fo:block font-size="7pt" text-align="center">AMOUNT</fo:block></fo:table-cell>
		                <fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="left">----------</fo:block><fo:block font-size="7pt" text-align="center">OP</fo:block><fo:block font-size="7pt" text-align="left">&#160;AMOUNT</fo:block></fo:table-cell>
		                <fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="left">----------</fo:block><fo:block font-size="7pt" text-align="center" >CART</fo:block><fo:block font-size="7pt" text-align="left">&#160;AMOUNT</fo:block></fo:table-cell>
		                <fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="left">----------</fo:block><fo:block font-size="7pt" text-align="center">ADDN</fo:block><fo:block font-size="7pt" text-align="left">AMOUNT</fo:block></fo:table-cell>
		                <fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="left">------</fo:block><fo:block font-size="7pt" text-align="left" >&#160;GROSS</fo:block><fo:block font-size="7pt" text-align="left">&#160;AMOUNT</fo:block></fo:table-cell>
		                <fo:table-cell><fo:block font-size="2pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block text-align="left" keep-together="always" white-space-collapse="false">.                                                           DETAILS OF RECOVERIES</fo:block><fo:block font-size="7pt" text-align="left"></fo:block>----</fo:table-cell>
		               	<#assign orderAdjustmentDesc=Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].getShedOrderAdjustmentDescription( dctx,Static["org.ofbiz.base.util.UtilMisc"].toMap("shedId",parameters.shedId)).get("shedAdjustmentDescriptionMap")>
		               	<#list orderAdjItemsList as orderAdjItems>
		               	<fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="left">----------</fo:block><fo:block font-size="7pt" text-align="left" >${orderAdjItems.description?if_exists}</fo:block><fo:block font-size="7pt" text-align="left">AMT</fo:block></fo:table-cell>
		                </#list>
		                <fo:table-cell><fo:block font-size="2pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block text-align="left" keep-together="always" white-space-collapse="false">.         OTHER DEDUCTIONS</fo:block></fo:table-cell>
		                <fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="left">---------------------------</fo:block><fo:block font-size="7pt" text-align="right" >TOTAL</fo:block><fo:block font-size="7pt" text-align="right">Deduction Amount</fo:block></fo:table-cell>
		               	<fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="right" >NET&#160;</fo:block><fo:block font-size="7pt" text-align="right">AMOUNT</fo:block></fo:table-cell>
		                <fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="6pt" text-align="center" >T.I.P</fo:block><fo:block font-size="7pt" text-align="center">AMOUNT</fo:block></fo:table-cell>
				    </fo:table-header>	
				  	<fo:table-body>
				  	 	<fo:table-row> <fo:table-cell><fo:block></fo:block></fo:table-cell></fo:table-row>
                    </fo:table-body>
				  </fo:table>
				  <fo:block font-size="7pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				 </fo:block>   
				 <fo:block font-size="7pt">
                 	<fo:table>
                    	<fo:table-column column-width="60pt"/>
                    	<fo:table-column column-width="50pt"/>  
               	    	<fo:table-column column-width="45pt"/>
               	    	<fo:table-column column-width="35pt"/>
                    	<fo:table-column column-width="40pt"/>  
                    	<fo:table-column column-width="35pt"/>
                    	<fo:table-column column-width="45pt"/>
                    	<fo:table-column column-width="55pt"/>  
                    	<fo:table-column column-width="50pt"/>
                    	<fo:table-column column-width="50pt"/>
                    	<#list orderAdjItemsList as orderAdjItems>
                    		<fo:table-column column-width="47pt"/>
                    	</#list>
                    	<fo:table-column column-width="45pt"/>
                    	<fo:table-column column-width="35pt"/>
                    	<fo:table-column column-width="45pt"/>
                    	<fo:table-column column-width="35pt"/>
                    	<fo:table-body>
		                    <#assign unitDetail = finalMap.entrySet()>
			                <#list unitDetail as unitData>
			                <#assign unitAnnualAbstractData = unitData.getValue()>
	                        <fo:table-row>
	                        
	                            <fo:table-cell >
	                        		<fo:block font-size="6pt" text-align="left" font-weight="bold">${unitData.getKey()}</fo:block>
	                        	</fo:table-cell>
	                        <#list procurementProductList as procProducts>
	                        	<fo:table-cell >
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${unitAnnualAbstractData.get(procProducts.productId).get("price")?if_exists}</fo:block>
	                        	</fo:table-cell>
	                         </#list>	
	                        	<fo:table-cell >
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${unitAnnualAbstractData.get("TOT").get("commissionAmount")?if_exists}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell >	
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${unitAnnualAbstractData.get("TOT").get("opCost")?if_exists}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell >
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${unitAnnualAbstractData.get("TOT").get("cartage")?if_exists}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell >
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${unitAnnualAbstractData.get("TOT").get("grsAddn")?if_exists}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell >
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${unitAnnualAbstractData.get("TOT").get("grossAmt")?if_exists}</fo:block>
	                        	</fo:table-cell>
	                        	<#list orderAdjItemsList as orderAdjItems>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="right" font-weight="bold">${unitAnnualAbstractData.get("TOT").get(orderAdjItems.orderAdjustmentTypeId)?if_exists}</fo:block>                               
	                            </fo:table-cell>
	                             </#list>
	                             <fo:table-cell >
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${unitAnnualAbstractData.get("TOT").get("grsDed")?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell >
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${unitAnnualAbstractData.get("TOT").get("netAmt")?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell >
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${unitAnnualAbstractData.get("TOT").get("tipAmt")?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                        </fo:table-row>	
						     </#list>                  
                    	</fo:table-body>
                	</fo:table>
               </fo:block>
               <fo:block font-size="7pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
               <fo:block font-size="7pt">
                 	<fo:table>
                    	<fo:table-column column-width="25pt"/>
                    	<fo:table-column column-width="85pt"/>
                    	<fo:table-column column-width="45pt"/> 
               	    	<fo:table-column column-width="36pt"/>
                    	<fo:table-column column-width="40pt"/> 
                    	<fo:table-column column-width="35pt"/> 
                    	<fo:table-column column-width="45pt"/>
                    	<fo:table-column column-width="55pt"/>
                    	<fo:table-column column-width="49pt"/> 
                    	 <fo:table-column column-width="49pt"/> 
                    	<fo:table-column column-width="50pt"/>
                    	<fo:table-column column-width="45pt"/>
                    	<#list orderAdjItemsList as orderAdjItems>
                    		<fo:table-column column-width="47pt"/>
                    	</#list>
                    	<fo:table-column column-width="35pt"/>
                    	<fo:table-column column-width="33pt"/>
                    	<fo:table-column column-width="30pt"/>
                    	<fo:table-body>
		                    <#assign totalValue = totalMap.entrySet()>
	                         <fo:table-row>
	                            <fo:table-cell >
	                        		<fo:block font-size="7pt" text-align="left" font-weight="bold">TOTAL:</fo:block>
	                        	</fo:table-cell>
	                      <#list procurementProductList as procProducts>
	                        	<fo:table-cell >
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${totalMap.get(procProducts.productId).get("price")?if_exists}</fo:block>
	                        	</fo:table-cell>
	                         </#list>	
	                        	<fo:table-cell >
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${totalMap.get("TOT").get("commissionAmount")?if_exists}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell >	
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${totalMap.get("TOT").get("opCost")?if_exists}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell >
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${totalMap.get("TOT").get("cartage")?if_exists}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell >
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${totalMap.get("TOT").get("grsAddn")?if_exists}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell >
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${totalMap.get("TOT").get("grossAmt")?if_exists}</fo:block>
	                        	</fo:table-cell>
	                        	<#list orderAdjItemsList as orderAdjItems>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="right" font-weight="bold">${totalMap.get("TOT").get(orderAdjItems.orderAdjustmentTypeId)?if_exists}</fo:block>                               
	                            </fo:table-cell>
	                             </#list>
	                            
	                             <fo:table-cell >
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${totalMap.get("TOT").get("grsDed")?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell >
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${totalMap.get("TOT").get("netAmt")?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell >
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${totalMap.get("TOT").get("tipAmt")?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell> 
	                        </fo:table-row>	
                    	</fo:table-body>
                	</fo:table>
               </fo:block>
               <fo:block font-size="7pt">------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
               <fo:block font-size="5pt">VST_ASCII-012 VST_ASCII-027VST_ASCII-080</fo:block>
           </fo:flow>
        </fo:page-sequence>	
        <#else>
				<fo:page-sequence master-reference="main">
	    			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	       		 		<fo:block font-size="14pt">
	            			${uiLabelMap.OrderNoOrderFound}.
	       		 		</fo:block>
	    			</fo:flow>
				</fo:page-sequence>
		</#if> 
</#if>
     </fo:root>
</#escape>
