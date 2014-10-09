<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="10in" page-width="12in" margin-top=".5in">
                <fo:region-body margin-top="1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "MonthWiseAverageRateStatement.txt")}
<#if errorMessage?has_content>
<fo:page-sequence master-reference="main">
   <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
      <fo:block font-size="14pt">
              ${errorMessage}.
   	  </fo:block>
   </fo:flow>
</fo:page-sequence>        
<#else> 
	 <#if tempfinalMap?has_content>   
		<fo:page-sequence master-reference="main">
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="8pt">
			    <fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;                 YEARLY ANALYSIS OF PROCUREMENT,AMOUNT,AVERAGE AMT</fo:block>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;                      FOR THE PERIOD FROM  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}           </fo:block>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always">SHED NAME: ${parameters.shedId}         </fo:block>
				<fo:block font-size="7pt">---------|------------------------------|-----------------------------|-----------------------------|</fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">&#160;MONTH  |       BUFFALOW MILK          |           COW MILK          |        TOTAL MILK           |       </fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">&#160;       |------------------------------|-----------------------------|-----------------------------|         </fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">&#160;  &amp;    |     QTY      AMOUNT   AVG    |     QTY    AMOUNT    AVG    |     QTY    AMOUNT    AVG    |</fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">&#160;YEAR   |     LTS       (RS)    RATE   |     LTS     (RS)     RATE   |     LTS     (RS)     RATE   |        </fo:block>
				<fo:block font-size="7pt">---------|------------------------------|-----------------------------|-----------------------------|</fo:block> 
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace"> 
			    <fo:block>
				 	<fo:table> 
					 	<fo:table-column column-width="20pt"/> 
					 	<fo:table-column column-width="20pt"/> 
					 	<fo:table-column column-width="40pt"/> 
					 	<fo:table-column column-width="40pt"/> 
					 	<fo:table-column column-width="40pt"/> 
					 	<fo:table-column column-width="40pt"/>
					 	<fo:table-column column-width="40pt"/>
					 	<fo:table-column column-width="40pt"/> 
					 	<fo:table-column column-width="40pt"/> 
					 	<fo:table-column column-width="40pt"/>
					 	<fo:table-column column-width="40pt"/> 
					 	<fo:table-column column-width="40pt"/> 
					 	<fo:table-column column-width="40pt"/> 
					 	<fo:table-column column-width="40pt"/>
					 	<fo:table-column column-width="40pt"/>  
				          <fo:table-body>
				          	<#list leanKeysList as monthKey>
					              <#assign monthValue = tempfinalMap.get(monthKey).entrySet()>
					              	<fo:table-row>
										<fo:table-cell>
											<fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block>
										</fo:table-cell>
									</fo:table-row> 
					                <fo:table-row> 
					 					<fo:table-cell>
		   									<fo:block keep-together="always" font-size="5pt" text-align="left">${monthKey?if_exists}</fo:block>
		   								</fo:table-cell> 
		   								<fo:table-cell>
		   									<fo:block font-size="5pt">
											 	<fo:table>
											 		<fo:table-column column-width="33pt"/>
					           						<fo:table-column column-width="30pt"/>
					           						<fo:table-column column-width="30pt"/>
					           						<fo:table-column column-width="35pt"/>
					           						<fo:table-column column-width="27pt"/>
					           						<fo:table-column column-width="30pt"/>
					           						<fo:table-column column-width="35pt"/>
					           						<fo:table-column column-width="30pt"/>
					           						<fo:table-column column-width="30pt"/>
					           						<fo:table-column column-width="20pt"/>
					           						<fo:table-column column-width="20pt"/>
					           						<fo:table-column column-width="30pt"/>
					           						<fo:table-body>
					           							<fo:table-row> 
					           							<#list procurementProductList as procProducts>
					           								 <#list monthValue as monthDetails>
						           								<#if monthDetails.getKey() == procProducts.productId>
						           									<#assign qtyLtrs = (monthDetails.getValue().get("qtyLtrs"))>
						           									 <#assign amount = (monthDetails.getValue().get("amount"))>
					           										 <#assign avgRate = (monthDetails.getValue().get("avgRate"))>
							           								<fo:table-cell>
							           									<fo:block keep-together="always" font-size="5pt" text-align="right">${qtyLtrs?if_exists?string("##0.00")}</fo:block>
							           								</fo:table-cell>
							           								<fo:table-cell>
							           									<fo:block keep-together="always" font-size="5pt" text-align="right">${amount?if_exists?string("##0.00")}</fo:block>
							           								</fo:table-cell>
							           								<fo:table-cell>
							           									<fo:block keep-together="always" font-size="5pt" text-align="right">${avgRate?if_exists?string("##0.00")}</fo:block>
							           								</fo:table-cell>
							           							</#if>	
						           							</#list>	
					           							</#list>
					           							<#list monthValue as monthDetails>
							           						<#if  monthDetails.getKey() =="TOT">
							           							 <#assign mixLtrs = (monthDetails.getValue().get("qtyLtrs"))>
							           							 <#assign mixAmount = (monthDetails.getValue().get("amount"))>
							           							 <#assign mixAvgRate = (monthDetails.getValue().get("avgRate"))>
						           								<fo:table-cell>           																	 																		
																	<fo:block keep-together="always" font-size="5pt"  text-align="right">${mixLtrs?if_exists?string("##0.00")}</fo:block>     																	
																</fo:table-cell>
																<fo:table-cell>          
																	<fo:block keep-together="always" font-size="5pt"  text-align="right">${mixAmount?if_exists?string("##0.00")}</fo:block>     																	
																</fo:table-cell>
																<fo:table-cell>          
																	<fo:block keep-together="always" font-size="5pt"  text-align="right">${mixAvgRate?if_exists?string("##0.00")}</fo:block>     																	
																</fo:table-cell>
															</#if>	
														</#list>	
					           							</fo:table-row>
					           						</fo:table-body>	
					           					 </fo:table>	
					           				</fo:block>		 		
		   								</fo:table-cell>
									</fo:table-row>
								</#list>
								 <fo:table-row>	
	            					<fo:table-cell >	
			                    		<fo:block font-size="5pt" text-align="left">-----------------------------------------------------------------------------------------------------</fo:block>
			                    	</fo:table-cell>
		                         </fo:table-row>
		                         
								<#-- leanTotals  -->
       							<#assign leanTotals = tempFinalLeanMap.entrySet()>
       							<#list leanTotals as leanDetails> 
       								<#assign leanTotValue = leanDetails.getValue().entrySet()> 
       								<fo:table-row>
       									<fo:table-cell>           																	 																		
											<fo:block keep-together="always" font-size="5pt"  text-align="left">${leanDetails.getKey()}</fo:block>     																	
										</fo:table-cell>
										<fo:table-cell>
		   									<fo:block font-size="5pt">
											 	<fo:table>
											 		<fo:table-column column-width="33pt"/>
					           						<fo:table-column column-width="30pt"/>
					           						<fo:table-column column-width="30pt"/>
					           						<fo:table-column column-width="35pt"/>
					           						<fo:table-column column-width="27pt"/>
					           						<fo:table-column column-width="30pt"/>
					           						<fo:table-column column-width="35pt"/>
					           						<fo:table-column column-width="30pt"/>
					           						<fo:table-column column-width="30pt"/>
					           						<fo:table-column column-width="20pt"/>
					           						<fo:table-column column-width="20pt"/>
					           						<fo:table-column column-width="30pt"/>
					           						<fo:table-body>
					           							<fo:table-row> 
					           							<#assign leanGrndQtyLtrs = 0> 
														<#assign leanGrndAmount = 0> 
														<#assign leanGrndAvgRate = 0> 
														
														<#assign lenGrndQtyLtrs = 0> 
														<#assign lenGrndAmount = 0> 
														<#assign lenGrndAvgRate = 0> 
														
					           							<#list procurementProductList as procProducts>
					           								<#list leanTotValue as eachLeanDetails>
					           								
					           								<#assign leanQtyLtrs = 0> 
															<#assign leanAmount = 0> 
															<#assign leanAvgRate = 0> 
															
					           									<#if eachLeanDetails.getKey() == procProducts.productId>
					           										<#assign leanQtyLtrs = (eachLeanDetails.getValue().get("qtyLtrs"))>
					           										<#assign leanAmount = (eachLeanDetails.getValue().get("amount"))>
					           										<#assign leanAvgRate = (eachLeanDetails.getValue().get("avgRate"))>
					           										
					           										<#if procProducts.productId == "101">
						           										<#assign leanGrndQtyLtrs = (leanGrndQtyLtrs+leanQtyLtrs)?if_exists> 
																		<#assign leanGrndAmount = (leanGrndAmount+leanAmount)?if_exists> 
																		<#assign leanGrndAvgRate = (leanGrndAvgRate+leanAvgRate)?if_exists> 
					           										</#if>
					           										
					           										<#if procProducts.productId == "102">
						           										<#assign lenGrndQtyLtrs = (lenGrndQtyLtrs+leanQtyLtrs)?if_exists> 
																		<#assign lenGrndAmount = (lenGrndAmount+leanAmount)?if_exists> 
																		<#assign lenGrndAvgRate = (lenGrndAvgRate+leanAvgRate)?if_exists> 
					           										</#if>
							           								<fo:table-cell>
							           									<fo:block keep-together="always" font-size="5pt" text-align="right">${leanQtyLtrs?if_exists?string("##0.00")}</fo:block>
							           								</fo:table-cell>
							           								<fo:table-cell>
							           									<fo:block keep-together="always" font-size="5pt" text-align="right">${leanAmount?if_exists?string("##0.00")}</fo:block>
							           								</fo:table-cell>
							           								<fo:table-cell>
							           									<fo:block keep-together="always" font-size="5pt" text-align="right">${leanAvgRate?if_exists?string("##0.00")}</fo:block>
							           								</fo:table-cell>
						           								</#if>
					           								</#list>
					           							</#list>
					           							<#assign leanMixedLtrs = 0> 
														<#assign leanMixedAmount = 0> 
														<#assign leanMixedAvgRate = 0> 
					           							
					           							<#list leanTotValue as eachLeanDetails>
							           						<#if  eachLeanDetails.getKey() =="TOT">
							           							 <#assign leanMixLtrs = (eachLeanDetails.getValue().get("qtyLtrs"))>
							           							 <#assign leanMixAmount = (eachLeanDetails.getValue().get("amount"))>
							           							 <#assign leanMixAvgRate = (eachLeanDetails.getValue().get("avgRate"))>
							           							 
							           							 <#assign leanMixedLtrs = (leanMixedLtrs+leanMixLtrs)?if_exists> 
																 <#assign leanMixedAmount = (leanMixedAmount+leanMixAmount)?if_exists> 
																 <#assign leanMixedAvgRate = (leanMixedAvgRate+leanMixAvgRate)?if_exists>
						           								<fo:table-cell>           																	 																		
																	<fo:block keep-together="always" font-size="5pt"  text-align="right">${leanMixLtrs?if_exists?string("##0.00")}</fo:block>     																	
																</fo:table-cell>
																<fo:table-cell>          
																	<fo:block keep-together="always" font-size="5pt"  text-align="right">${leanMixAmount?if_exists?string("##0.00")}</fo:block>     																	
																</fo:table-cell>
																<fo:table-cell>          
																	<fo:block keep-together="always" font-size="5pt"  text-align="right">${leanMixAvgRate?if_exists?string("##0.00")}</fo:block>     																	
																</fo:table-cell>
															</#if>	
														</#list>		
					           							</fo:table-row>
					           						</fo:table-body>	
					           					 </fo:table>	
					           				</fo:block>		 		
		   								</fo:table-cell>
       								</fo:table-row>	
       							</#list>
       							  <fo:table-row>	
	            					<fo:table-cell >	
			                    		<fo:block font-size="5pt" text-align="left">-----------------------------------------------------------------------------------------------------</fo:block>
			                    	</fo:table-cell>
		                         </fo:table-row>
		                         
		                         <fo:table-row>
									<fo:table-cell>
										<fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block>
									</fo:table-cell>
								</fo:table-row>
		                         <#list flushKeysList as monthKey>
					             <#assign monthWiseValue = tempfinalMap.get(monthKey).entrySet()> 	
		                         <fo:table-row>
       									<fo:table-cell>
		   									<fo:block keep-together="always" font-size="5pt" text-align="left">${monthKey?if_exists}</fo:block>
		   								</fo:table-cell> 
		   								<fo:table-cell>
		   									<fo:block font-size="5pt">
											 	<fo:table>
											 		<fo:table-column column-width="33pt"/>
					           						<fo:table-column column-width="30pt"/>
					           						<fo:table-column column-width="30pt"/>
					           						<fo:table-column column-width="35pt"/>
					           						<fo:table-column column-width="27pt"/>
					           						<fo:table-column column-width="30pt"/>
					           						<fo:table-column column-width="35pt"/>
					           						<fo:table-column column-width="30pt"/>
					           						<fo:table-column column-width="30pt"/>
					           						<fo:table-column column-width="20pt"/>
					           						<fo:table-column column-width="20pt"/>
					           						<fo:table-column column-width="30pt"/>
					           						<fo:table-body>
					           							<fo:table-row> 
					           							<#list procurementProductList as procProducts>
					           								 <#list monthWiseValue as eachMonthDetails>
						           								<#if eachMonthDetails.getKey() == procProducts.productId>
						           									<#assign monthQtyLtrs = (eachMonthDetails.getValue().get("qtyLtrs"))>
						           									 <#assign monthAmount = (eachMonthDetails.getValue().get("amount"))>
					           										 <#assign monthAvgRate = (eachMonthDetails.getValue().get("avgRate"))>
							           								<fo:table-cell>
							           									<fo:block keep-together="always" font-size="5pt" text-align="right">${monthQtyLtrs?if_exists?string("##0.00")}</fo:block>
							           								</fo:table-cell>
							           								<fo:table-cell>
							           									<fo:block keep-together="always" font-size="5pt" text-align="right">${monthAmount?if_exists?string("##0.00")}</fo:block>
							           								</fo:table-cell>
							           								<fo:table-cell>
							           									<fo:block keep-together="always" font-size="5pt" text-align="right">${monthAvgRate?if_exists?string("##0.00")}</fo:block>
							           								</fo:table-cell>
							           							</#if>	
						           							</#list>	
					           							</#list>
					           							<#list monthWiseValue as eachMonthDetails>
							           						<#if  eachMonthDetails.getKey() =="TOT">
							           							 <#assign mixMonthLtrs = (eachMonthDetails.getValue().get("qtyLtrs"))>
							           							 <#assign mixMonthAmount = (eachMonthDetails.getValue().get("amount"))>
							           							 <#assign mixMonthAvgRate = (eachMonthDetails.getValue().get("avgRate"))>
						           								<fo:table-cell>           																	 																		
																	<fo:block keep-together="always" font-size="5pt"  text-align="right">${mixMonthLtrs?if_exists?string("##0.00")}</fo:block>     																	
																</fo:table-cell>
																<fo:table-cell>          
																	<fo:block keep-together="always" font-size="5pt"  text-align="right">${mixMonthAmount?if_exists?string("##0.00")}</fo:block>     																	
																</fo:table-cell>
																<fo:table-cell>          
																	<fo:block keep-together="always" font-size="5pt"  text-align="right">${mixMonthAvgRate?if_exists?string("##0.00")}</fo:block>     																	
																</fo:table-cell>
															</#if>	
														</#list>	
					           							</fo:table-row>
					           						</fo:table-body>	
					           					 </fo:table>	
					           				</fo:block>		 		
		   								</fo:table-cell>
								 </fo:table-row>
								 </#list>		
								  <fo:table-row>	
	            					<fo:table-cell >	
			                    		<fo:block font-size="5pt" text-align="left">-----------------------------------------------------------------------------------------------------</fo:block>
			                    	</fo:table-cell>
		                         </fo:table-row>
		                         
		                        <#-- flushTotals  --> 
		                        <#assign flushTotals = tempFinalFlushMap.entrySet()>
       							<#list flushTotals as flushDetails> 
       								<#assign flushTotValue = flushDetails.getValue().entrySet()> 
       								<fo:table-row>
       									<fo:table-cell>           																	 																		
											<fo:block keep-together="always" font-size="5pt"  text-align="left">${flushDetails.getKey()}</fo:block>     																	
										</fo:table-cell>
										<fo:table-cell>
		   									<fo:block font-size="5pt">
											 	<fo:table>
											 		<fo:table-column column-width="33pt"/>
					           						<fo:table-column column-width="30pt"/>
					           						<fo:table-column column-width="30pt"/>
					           						<fo:table-column column-width="35pt"/>
					           						<fo:table-column column-width="27pt"/>
					           						<fo:table-column column-width="30pt"/>
					           						<fo:table-column column-width="35pt"/>
					           						<fo:table-column column-width="30pt"/>
					           						<fo:table-column column-width="30pt"/>
					           						<fo:table-column column-width="20pt"/>
					           						<fo:table-column column-width="20pt"/>
					           						<fo:table-column column-width="30pt"/>
					           						<fo:table-body>
					           							<fo:table-row> 
														<#assign flushGrndQtyLtrs = 0> 
														<#assign flushGrndAmount = 0> 
														<#assign flushGrndAvgRate = 0> 
														
														<#assign flushGrndproQtyLtrs = 0> 
														<#assign flushGrndProAmount = 0> 
														<#assign flushGrndProAvgRate = 0> 
						           							
						           						<#list procurementProductList as procProducts>
					           								<#list flushTotValue as eachFlushDetails>
					           								
					           									<#assign flushQtyLtrs = 0> 
																<#assign flushAmount = 0> 
																<#assign flushAvgRate = 0> 
																
					           									<#if eachFlushDetails.getKey() == procProducts.productId>
					           										<#assign flushQtyLtrs = (eachFlushDetails.getValue().get("qtyLtrs"))>
					           										<#assign flushAmount = (eachFlushDetails.getValue().get("amount"))>
					           										<#assign flushAvgRate = (eachFlushDetails.getValue().get("avgRate"))>
					           										
					           										<#if procProducts.productId == "101">
					           										<#assign flushGrndQtyLtrs = (flushGrndQtyLtrs+flushQtyLtrs)?if_exists> 
																	<#assign flushGrndAmount = (flushGrndAmount+flushAmount)?if_exists> 
																	<#assign flushGrndAvgRate = (flushGrndAvgRate+flushAvgRate)?if_exists>
																	</#if>
																	
																	<#if procProducts.productId == "102">
						           										<#assign flushGrndproQtyLtrs = (flushGrndproQtyLtrs+flushQtyLtrs)?if_exists> 
																		<#assign flushGrndProAmount = (flushGrndProAmount+flushAmount)?if_exists> 
																		<#assign flushGrndProAvgRate = (flushGrndProAvgRate+flushAvgRate)?if_exists> 
					           										</#if>
							           								<fo:table-cell>
							           									<fo:block keep-together="always" font-size="5pt" text-align="right">${flushQtyLtrs?if_exists?string("##0.00")}</fo:block>
							           								</fo:table-cell>
							           								<fo:table-cell>
							           									<fo:block keep-together="always" font-size="5pt" text-align="right">${flushAmount?if_exists?string("##0.00")}</fo:block>
							           								</fo:table-cell>
							           								<fo:table-cell>
							           									<fo:block keep-together="always" font-size="5pt" text-align="right">${flushAvgRate?if_exists?string("##0.00")}</fo:block>
							           								</fo:table-cell>
						           								</#if>
					           								</#list>
					           							</#list>
					           							<#assign flushMixedLtrs = 0> 
														<#assign flushMixedAmount = 0> 
														<#assign flushMixedAvgRate = 0> 
														
					           							<#list flushTotValue as eachFlushDetails>
					           								<#assign flushMixLtrs = 0> 
															<#assign flushMixAmount = 0> 
															<#assign flushMixAvgRate = 0> 
															
							           						<#if  eachFlushDetails.getKey() =="TOT">
							           							 <#assign flushMixLtrs = (eachFlushDetails.getValue().get("qtyLtrs"))>
							           							 <#assign flushMixAmount = (eachFlushDetails.getValue().get("amount"))>
							           							 <#assign flushMixAvgRate = (eachFlushDetails.getValue().get("avgRate"))>
							           							 
							           							 <#assign flushMixedLtrs = (flushMixedLtrs+flushMixLtrs)?if_exists>
							           							 <#assign flushMixedAmount = (flushMixedAmount+flushMixAmount)?if_exists>
							           							 <#assign flushMixedAvgRate = (flushMixedAvgRate+flushMixAvgRate)?if_exists>
						           								<fo:table-cell>           																	 																		
																	<fo:block keep-together="always" font-size="5pt"  text-align="right">${flushMixLtrs?if_exists?string("##0.00")}</fo:block>     																	
																</fo:table-cell>
																<fo:table-cell>          
																	<fo:block keep-together="always" font-size="5pt"  text-align="right">${flushMixAmount?if_exists?string("##0.00")}</fo:block>     																	
																</fo:table-cell>
																<fo:table-cell>          
																	<fo:block keep-together="always" font-size="5pt"  text-align="right">${flushMixAvgRate?if_exists?string("##0.00")}</fo:block>     																	
																</fo:table-cell>
															</#if>	
														</#list>		
					           							</fo:table-row>
					           						</fo:table-body>	
					           					 </fo:table>	
					           				</fo:block>		 		
		   								</fo:table-cell>
       								</fo:table-row>	
       							</#list>
       							  <fo:table-row>	
	            					<fo:table-cell >	
			                    		<fo:block font-size="5pt" text-align="left">-----------------------------------------------------------------------------------------------------</fo:block>
			                    	</fo:table-cell>
		                         </fo:table-row>
       							
       							<#assign grndQtyLtrs = 0> 
								<#assign grndAmount = 0> 
								<#assign grndAvgRate = 0>
								
								<#assign grndQtyLtrsDet = 0> 
								<#assign grndAmountDet = 0> 
								<#assign grndAvgRateDet = 0>
								
								<#assign grndMixQtyLtrs = 0> 
								<#assign grndMixAmount = 0> 
								<#assign grndMixAvgRate = 0>
								
								<fo:table-row>
       									<fo:table-cell>           																	 																		
											<fo:block keep-together="always" font-size="5pt"  text-align="left">GRND TOT</fo:block>     																	
										</fo:table-cell>
										<fo:table-cell>
		   									<fo:block font-size="5pt">
											 	<fo:table>
											 		<fo:table-column column-width="33pt"/>
					           						<fo:table-column column-width="30pt"/>
					           						<fo:table-column column-width="30pt"/>
					           						<fo:table-column column-width="35pt"/>
					           						<fo:table-column column-width="27pt"/>
					           						<fo:table-column column-width="30pt"/>
					           						<fo:table-column column-width="35pt"/>
					           						<fo:table-column column-width="30pt"/>
					           						<fo:table-column column-width="30pt"/>
					           						<fo:table-column column-width="20pt"/>
					           						<fo:table-column column-width="20pt"/>
					           						<fo:table-column column-width="30pt"/>
					           						<fo:table-body>
					           							<fo:table-row> 
														<#list procurementProductList as procProducts>
				       										<#if procProducts.productId == "101">
				       											<#if (leanGrndQtyLtrs?has_content && leanGrndQtyLtrs!=0) && (flushGrndQtyLtrs?has_content && flushGrndQtyLtrs!=0) >
				       												<#assign grndQtyLtrs = leanGrndQtyLtrs+flushGrndQtyLtrs>
						       										<#assign grndAmount = leanGrndAmount+flushGrndAmount>
						       										<#assign grndAvgRate = leanGrndAvgRate+flushGrndAvgRate>
				       											<#elseif flushGrndQtyLtrs?has_content && flushGrndQtyLtrs!=0>
				       												<#assign grndQtyLtrs = flushGrndQtyLtrs>
						       										<#assign grndAmount = flushGrndAmount>
						       										<#assign grndAvgRate = flushGrndAvgRate>
						       									<#elseif leanGrndQtyLtrs?has_content && leanGrndQtyLtrs!=0>	
						       										<#assign grndQtyLtrs = leanGrndQtyLtrs>
						       										<#assign grndAmount = leanGrndAmount>
						       										<#assign grndAvgRate = leanGrndAvgRate>
				       											</#if>
				       										</#if>
				       										
				       										<#if procProducts.productId == "102">
				       											<#if (lenGrndQtyLtrs?has_content && lenGrndQtyLtrs!=0) && (flushGrndproQtyLtrs?has_content && flushGrndproQtyLtrs!=0) >
				       												<#assign grndQtyLtrsDet = lenGrndQtyLtrs+flushGrndproQtyLtrs> 
						       										<#assign grndAmountDet = lenGrndAmount+flushGrndProAmount> 
						       										<#assign grndAvgRateDet = lenGrndAvgRate+flushGrndProAvgRate>
				       											<#elseif flushGrndproQtyLtrs?has_content && flushGrndproQtyLtrs!=0>
				       												<#assign grndQtyLtrsDet = flushGrndproQtyLtrs> 
						       										<#assign grndAmountDet = flushGrndProAmount> 
						       										<#assign grndAvgRateDet = flushGrndProAvgRate>
						       									<#elseif lenGrndQtyLtrs?has_content && lenGrndQtyLtrs!=0>	
						       										<#assign grndQtyLtrsDet = lenGrndQtyLtrs> 
						       										<#assign grndAmountDet = lenGrndAmount> 
						       										<#assign grndAvgRateDet = lenGrndAvgRate>
				       											</#if>
				       										</#if>
				       										
				       										<#if (leanMixedLtrs?has_content && leanMixedLtrs!=0) && (flushMixedLtrs?has_content && flushMixedLtrs!=0) >
						       										<#assign grndMixQtyLtrs = leanMixedLtrs+flushMixedLtrs> 
						       										<#assign grndMixAmount = leanMixedAmount+flushMixedAmount> 
						       										<#assign grndMixAvgRate = leanMixedAvgRate+flushMixedAvgRate>
						       									<#elseif leanMixedLtrs?has_content && leanMixedLtrs!=0>	
						       										<#assign grndMixQtyLtrs = leanMixedLtrs> 
						       										<#assign grndMixAmount = leanMixedAmount> 
						       										<#assign grndMixAvgRate = leanMixedAvgRate>
						       									<#elseif flushMixedLtrs?has_content && flushMixedLtrs!=0>	
						       										<#assign grndMixQtyLtrs = flushMixedLtrs> 
						       										<#assign grndMixAmount = flushMixedAmount> 
						       										<#assign grndMixAvgRate = flushMixedAvgRate>	
				       										</#if>
				       										
				       										<#if procProducts.productId == "101">
						           								<fo:table-cell>
						           									<fo:block keep-together="always" font-size="5pt" text-align="right">${grndQtyLtrs?if_exists?string("##0.00")}</fo:block>
						           								</fo:table-cell>
						           								<fo:table-cell>
						           									<fo:block keep-together="always" font-size="5pt" text-align="right">${grndAmount?if_exists?string("##0.00")}</fo:block>
						           								</fo:table-cell>
						           								<fo:table-cell>
						           									<fo:block keep-together="always" font-size="5pt" text-align="right">${grndAvgRate?if_exists?string("##0.00")}</fo:block>
						           								</fo:table-cell>
					           								</#if>
					           								
					           								<#if procProducts.productId == "102">
						           								<fo:table-cell>
						           									<fo:block keep-together="always" font-size="5pt" text-align="right">${grndQtyLtrsDet?if_exists?string("##0.00")}</fo:block>
						           								</fo:table-cell>
						           								<fo:table-cell>
						           									<fo:block keep-together="always" font-size="5pt" text-align="right">${grndAmountDet?if_exists?string("##0.00")}</fo:block>
						           								</fo:table-cell>
						           								<fo:table-cell>
						           									<fo:block keep-together="always" font-size="5pt" text-align="right">${grndAvgRateDet?if_exists?string("##0.00")}</fo:block>
						           								</fo:table-cell>
					           								</#if>
					           								
					           							</#list>	
					           								<fo:table-cell>
					           									<fo:block keep-together="always" font-size="5pt" text-align="right">${grndMixQtyLtrs?if_exists?string("##0.00")}</fo:block>
					           								</fo:table-cell>
					           								<fo:table-cell>
					           									<fo:block keep-together="always" font-size="5pt" text-align="right">${grndMixAmount?if_exists?string("##0.00")}</fo:block>
					           								</fo:table-cell>
					           								<fo:table-cell>
					           									<fo:block keep-together="always" font-size="5pt" text-align="right">${grndMixAvgRate?if_exists?string("##0.00")}</fo:block>
					           								</fo:table-cell>
														</fo:table-row>
					           						</fo:table-body>	
					           					 </fo:table>	
					           				</fo:block>		 		
		   								</fo:table-cell>
	           					</fo:table-row>	
	           					<fo:table-row>	
	            					<fo:table-cell >	
			                    		<fo:block font-size="5pt" text-align="left">-----------------------------------------------------------------------------------------------------</fo:block>
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