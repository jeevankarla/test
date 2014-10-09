<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in" margin-top=".2in">
                <fo:region-body margin-top="1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
         ${setRequestAttribute("OUTPUT_FILENAME", "fortNightKgFatAcnt.txt")}
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
			<#assign facility = delegator.findOne("Facility", {"facilityId" : parameters.unitId}, true)>
                <fo:block text-align="left" white-space-collapse="false" keep-together="always">.                  ABSTRACT FOR QUANTITY, KG-FAT, KG-SNF,TIP AND DIF RECOVERIES MONTH WISE AND UNITWISE    </fo:block>
				<fo:block font-size="8pt">--------------------------------------------------------------------------------------------------------------------</fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="8pt">UNIT NAME :${facility.getString("facilityName")?if_exists}                                   PERIOD   FROM: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "MM/yyyy")}    </fo:block>
				<fo:block font-size="8pt">---------------------------------------------------------------------------------------------------------------------</fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="8pt">MONTH		         		  TOTAL								      TOTAL				    		TOTAL			   		   TOTAL				    		  TOTAL				    		       TOTAL</fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="8pt">YEAR			            QTY-LTS			         QTY-KGS        KG-FAT          KG-SNF        	  TIP(RS)        	      DIF(RS)</fo:block>
				<fo:block font-size="8pt">---------------------------------------------------------------------------------------------------------------------</fo:block>
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">       				   
				<#assign monthTotals = finalMap.entrySet()>
				<#list monthTotals as monthTot> 
					<#assign monthTotalEntries = monthTot.getValue().entrySet()>				
						<fo:block font-family="Courier,monospace" font-size="8pt">
						 	<fo:table>
						 		<fo:table-column column-width="30pt"/>
           						<fo:table-column column-width="45pt"/>
           						<fo:table-body>                   						
           							<fo:table-row>
           							<#assign periodBilling = delegator.findOne("PeriodBilling", {"periodBillingId" : monthTot.getKey()}, true)>
           							<#assign customTimePeriodId = periodBilling.get("customTimePeriodId")>
           							<#assign customTimePeriod = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : customTimePeriodId}, true)>
           							<#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd/MM/yyyy")/>
                			 		<#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "MMMdd yyyy")/>	
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left">${fromDate}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
											<fo:block>
												<fo:table>          															
														<fo:table-column column-width="98pt"/>
														<fo:table-column column-width="90pt"/>
														<fo:table-column column-width="70pt"/>
														<fo:table-column column-width="78pt"/>
														<fo:table-column column-width="88pt"/>
														<fo:table-column column-width="90pt"/>
														<fo:table-column column-width="40pt"/>
													<fo:table-body>
														<fo:table-row>
 																<#list monthTotalEntries as month>  
 																<#if  month.getKey() =="TOT"> 
 																     <#assign Ltrs = (month.getValue().get("qtyLtrs"))>  																
 																	 <#assign Kgs = (month.getValue().get("qtyKgs"))>
																	 <#assign KgFat = (month.getValue().get("kgFat"))>
																	 <#assign KgSnf = (month.getValue().get("kgSnf"))>
																	 <#assign Tip = (month.getValue().get("tipAmt"))>  
															<fo:table-cell>          
																<fo:block text-align="right" keep-together="always" white-space-collapse="false" font-size="8pt">${Ltrs?if_exists?string("##0.0")}</fo:block>     																	
															</fo:table-cell>
															<fo:table-cell>           																	 																		
																<fo:block text-align="right" keep-together="always" white-space-collapse="false" font-size="8pt">${Kgs?if_exists?string("##0.0")}</fo:block>     																	
															</fo:table-cell>
															<fo:table-cell>           																	 																		
																<fo:block text-align="right" keep-together="always" white-space-collapse="false" font-size="8pt">${KgFat?if_exists?string("##0.000")}</fo:block>     																	
															</fo:table-cell>
															<fo:table-cell>          
																<fo:block text-align="right" keep-together="always" white-space-collapse="false" font-size="8pt">${KgSnf?if_exists?string("##0.000")}</fo:block>     																	
															</fo:table-cell>
															<fo:table-cell>          
																<fo:block text-align="right" keep-together="always" white-space-collapse="false" font-size="8pt">${Tip?if_exists?string("##0.000")}</fo:block>     																	
															</fo:table-cell>
															<fo:table-cell>          
																<fo:block text-align="right" keep-together="always" white-space-collapse="false" font-size="8pt">0.00</fo:block>     																	
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
     				<fo:block font-size="8pt">---------------------------------------------------------------------------------------------------------------------</fo:block>
     				<#if totalMap?has_content>
         				<fo:block font-size="8pt">
        					<fo:table>
        						<#assign totals = totalMap.entrySet()>
								<fo:table-column column-width="28pt"/>
								<fo:table-column column-width="98pt"/>
								<fo:table-column column-width="90pt"/>
								<fo:table-column column-width="70pt"/>
								<fo:table-column column-width="78pt"/>
								<fo:table-column column-width="86pt"/>
								<fo:table-column column-width="90pt"/>
                   				<fo:table-body>
                   					<fo:table-row>           						             						
                   						<fo:table-cell>
                   							<fo:block keep-together="always"> TOTAL :</fo:block>
                   						</fo:table-cell>
                   					    <#list totals as tot>
                   					     <#if  tot.getKey() =="TOT"> 
										 <#assign totLtrs = (tot.getValue().get("qtyLtrs"))>
										 <#assign totKgs = (tot.getValue().get("qtyKgs"))>
										 <#assign totKgFat = (tot.getValue().get("kgFat"))>
										 <#assign totKgSnf = (tot.getValue().get("kgSnf"))>
										 <#assign totTip = (tot.getValue().get("tipAmt"))>       							   						
                   						<fo:table-cell>
                   							<fo:block text-align="right" keep-together="always" white-space-collapse="false" font-size="8pt">${totLtrs?if_exists?string("##0.0")}</fo:block>
                   						</fo:table-cell>
                   						<fo:table-cell>
                   							<fo:block text-align="right" keep-together="always" white-space-collapse="false" font-size="8pt">${totKgs?if_exists?string("##0.0")}</fo:block>
                   						</fo:table-cell>
                   						<fo:table-cell>
                   							<fo:block text-align="right" keep-together="always" white-space-collapse="false" font-size="8pt">${totKgFat?if_exists?string("##0.000")}</fo:block>
                   						</fo:table-cell>
                   						<fo:table-cell>
                   							<fo:block text-align="right" keep-together="always" white-space-collapse="false" font-size="8pt">${totKgSnf?if_exists?string("##0.000")}</fo:block>
                   						</fo:table-cell>
										<fo:table-cell>          
											<fo:block text-align="right" keep-together="always" white-space-collapse="false" font-size="8pt">${totTip?if_exists?string("##0.000")}</fo:block>     																	
										</fo:table-cell>
										<fo:table-cell>          
											<fo:block text-align="right" keep-together="always" white-space-collapse="false" font-size="8pt">0.00</fo:block>     																	
										</fo:table-cell>             						
                   					</#if>	
                   					</#list>																	
                   					</fo:table-row>
                   				</fo:table-body>
        					</fo:table>
        				</fo:block>
        			</#if>	
    				<fo:block font-size="8pt">---------------------------------------------------------------------------------------------------------------------</fo:block>     				
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