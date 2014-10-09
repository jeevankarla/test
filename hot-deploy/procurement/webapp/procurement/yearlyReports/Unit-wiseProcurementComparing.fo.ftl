<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
             <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-left="0.09in">
                <fo:region-body margin-top=".9in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "Unit-wise comparision.txt")}
<#if errorMessage?has_content>
<fo:page-sequence master-reference="main">
   <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
      <fo:block font-size="14pt">
              ${errorMessage}.
   	  </fo:block>
   </fo:flow>
</fo:page-sequence>        
<#else>   
		<fo:page-sequence  master-reference="main">
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospae"  font-size="5pt">
        		<fo:block font-size="5pt" white-space-collapse="false">&#160;                   						 UNIT-WISE PROCUREMENT AND VARIATION FOR THE PERIOD FROM ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(CurrYearfromDate, "dd/MM/yyyy")}  TO  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(CurrYearthruDate, "dd/MM/yyyy")}</fo:block> 
        		<#assign shedDetails = delegator.findOne("Facility", {"facilityId" : parameters.shedId}, true)>	
        		<fo:block font-size="5pt" text-align="left" white-space-collapse="false" font-weight="bold">&#160; 				 								&#160;																			MILK SHED NAME :${shedDetails.get("facilityName")?if_exists}                              </fo:block> 	 	  
        		<fo:block font-size="5pt" white-space-collapse="false" text-align="left">---------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
        		<fo:block font-size="5pt" white-space-collapse="false" text-align="left">&#160;                                          LAST YEAR                                       CURRENT YEAR                                   VARIATION              </fo:block>
        		<fo:block font-size="5pt" white-space-collapse="false" text-align="left">&#160;                             ------------------------------------------       -----------------------------------------			  ----------------------------</fo:block>
        		<fo:block font-size="5pt" white-space-collapse="false" text-align="left">&#160;  MCC / DAIRY         MILK       TOTAL        AVG     AVG          AVG  	         TOTAL     AVG       AVG         AVG        %GE          FAT       SNF</fo:block>
        		<fo:block font-size="5pt" white-space-collapse="false" text-align="left">&#160;                      TYPE        LTS         FAT     SNF        PER DAY           LTS      FAT       SNF       PER DAY       VAR         DIF       DIF</fo:block>
        		<fo:block font-size="5pt" white-space-collapse="false" text-align="left">---------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
        		<fo:block font-size="5pt" white-space-collapse="false" text-align="left">&#160;                               ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(prevYearfromDate, "dd/MM/yyyy")}  TO   ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(prevYearthruDate, "dd/MM/yyyy")}                     ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(CurrYearfromDate, "dd/MM/yyyy")} TO  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(CurrYearthruDate, "dd/MM/yyyy")}</fo:block>
        		<fo:block font-size="5pt" white-space-collapse="false" text-align="left">---------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
        	</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospae"> 
				<fo:block font-family="Courier,monospae" font-size="6pt">
					<fo:table>
						<fo:table-column column-width="65pt"/>
						<fo:table-column column-width="15pt"/>
	                    <fo:table-column column-width="45pt"/>
	                    <fo:table-column column-width="30pt"/>
	                    <fo:table-column column-width="30pt"/>
	                    <fo:table-column column-width="35pt"/>
	                    <fo:table-column column-width="45pt"/>
	                    <fo:table-column column-width="30pt"/>
	                    <fo:table-column column-width="30pt"/>
	                    <fo:table-column column-width="35pt"/>
	                    <fo:table-column column-width="30pt"/>
	                    <fo:table-column column-width="35pt"/>
	                    <fo:table-column column-width="35pt"/>
	                    <fo:table-body>
		                    <#if previousYearMap?has_content>
		                    	<#assign previousYearData = previousYearMap.entrySet()>
		                    	<#list previousYearData as previousData>
			                    	<#assign counter = 1>
			                    	<#assign yearTotData = previousData.getValue()>
					                <#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : previousData.getKey()}, true)>
					                <#assign previousProductDetails = yearTotData.entrySet()>
					                <#list previousProductDetails as prevProductData>
						                <#assign currentProductDetails = currentYearMap.get(previousData.getKey())>
				                    	<#assign preProductDetails = previousYearMap.get(previousData.getKey())>
				                    	<#assign currentTotQtyKgs =  currentProductDetails.get(prevProductData.getKey()).get("qtyLtrs")>
				                    	<#assign preTotQtyKgs =  preProductDetails.get(prevProductData.getKey()).get("qtyLtrs")>
					                	<#if ((currentTotQtyKgs+preTotQtyKgs)!=0)>
		                    				<fo:table-row>  
					                   			<#if counter == 1>
					        						<fo:table-cell>
					        			   				<fo:block font-size="6pt" text-align="left" font-weight="bold" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim(facilityDetails.get("facilityName"),17)}</fo:block>
					                    			</fo:table-cell>
					                    		<#else>
					                    			<fo:table-cell>
					        			   				<fo:block font-size="6pt" text-align="right" font-weight="bold"></fo:block>
					                    			</fo:table-cell>
					                    		</#if> 
							                    <#assign counter = counter+1>
							                    <#assign brandName =  prevProductData.getKey()>
							                    <#if (brandName)!="TOT">
							                    <#assign product={}>
							                    <#assign product = delegator.findOne("Product", {"productId" : prevProductData.getKey()}, true)>
					                    		<#if product?has_content>
					                     			<#assign brandName = product.brandName>	
					                   			</#if>
					                   			</#if>
							                   	<fo:table-cell>
							                   	 	 <fo:block font-size="6pt" text-align="right" font-weight="bold">${brandName?if_exists}</fo:block>
							                   	</fo:table-cell>
							                	<fo:table-cell>	
							               			 <fo:block font-size="5pt" text-align="right"><#if (prevProductData.getValue().get("qtyLtrs"))!=0>${prevProductData.getValue().get("qtyLtrs")?if_exists?string('#0.0')}<#else>0.0</#if></fo:block>
							                	</fo:table-cell>
							                	<fo:table-cell>	
							               			 <fo:block font-size="5pt" text-align="right">${prevProductData.getValue().get("fat")?if_exists?string('#0.0')}</fo:block>
							                	</fo:table-cell>
							                	<fo:table-cell>	
							               			 <fo:block font-size="5pt" text-align="right">${prevProductData.getValue().get("snf")?if_exists?string('#0.00')}</fo:block>
							                	</fo:table-cell>
					                			<#assign preQtyPerDay =0>
					                			<#if PrevTotalDays !=0>
					                				<#assign preQtyPerDay = (prevProductData.getValue().get("qtyLtrs")/PrevTotalDays)>
					                			<#else>
					                				<#assign preQtyPerDay = 0>
					                			</#if>
							                	<fo:table-cell>	
							               			 <fo:block font-size="5pt" text-align="right">${preQtyPerDay?if_exists?string('#0')}</fo:block>
							                	</fo:table-cell>
							                	<#assign currentProductDetails = currentYearMap.get(previousData.getKey())>
							                	<#assign currProductData = currentProductDetails.get(prevProductData.getKey())>
							                	<fo:table-cell>	
							               			 <fo:block font-size="5pt" text-align="right"><#if (currProductData.get("qtyLtrs"))!=0>${currProductData.get("qtyLtrs")?if_exists?string('#0.0')}<#else>0.0</#if></fo:block>
							                	</fo:table-cell>
							                	<fo:table-cell>	
							               			 <fo:block font-size="5pt" text-align="right">${currProductData.get("fat")?if_exists?string('#0.0')}</fo:block>
							                	</fo:table-cell>
							                	<fo:table-cell>	
							               			 <fo:block font-size="5pt" text-align="right">${currProductData.get("snf")?if_exists?string('#0.00')}</fo:block>
							                	</fo:table-cell>
							                	<#assign currQtyPerDay =0>
					                			<#if CurrTotalDays !=0>
					                				<#assign currQtyPerDay = (currProductData.get("qtyLtrs")/CurrTotalDays)>
					                			<#else>
					                				<#assign currQtyPerDay = 0>
					                			</#if>
					                			<fo:table-cell>	
					               			 		<fo:block font-size="5pt" text-align="right"><#if (currQtyPerDay)!=0>${currQtyPerDay?if_exists?string('#0')}<#else>0</#if></fo:block>
					                			</fo:table-cell>
					                			<#assign diffQty = ((currProductData.get("qtyLtrs"))-(prevProductData.getValue().get("qtyLtrs")))>
					                			<#if  diffQty !=0>
					                				<#assign percentageVar =0>
					                				<#if prevProductData.getValue().get("qtyLtrs") !=0>
					                					<#assign percentageVar =((diffQty/(prevProductData.getValue().get("qtyLtrs")))*100)>
					                				</#if>
					                			<#else>
					                				<#assign percentageVar = 0>
					                   			</#if>	
							                	<fo:table-cell>	
							               			<fo:block font-size="5pt" text-align="right">${(percentageVar)?if_exists?string('##0.00')}</fo:block>
							                	</fo:table-cell>
							                	<fo:table-cell>	
							               			<fo:block font-size="5pt" text-align="right">${(currProductData.get("fat")-prevProductData.getValue().get("fat"))?if_exists?string('##0.0')}</fo:block>
							                	</fo:table-cell>
							                	<fo:table-cell>	
							               			<fo:block font-size="6pt" text-align="right">${(currProductData.get("snf")-prevProductData.getValue().get("snf"))?if_exists?string('##0.00')}</fo:block>
							                	</fo:table-cell>
											</fo:table-row>
											<#if prevProductData.getKey() == "TOT">
			        						<fo:table-row>
		            							<fo:table-cell>
			        			 					<fo:block font-family="Courier,monospae" font-size="6pt" >&#160;</fo:block>
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
	    		<fo:block font-size="6pt">---------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>     					
	    		<fo:block font-family="Courier,monospae" font-size="6pt">
					<fo:table>
						<fo:table-column column-width="78pt"/>
						<fo:table-column column-width="15pt"/>
	                    <fo:table-column column-width="54pt"/>
	                    <fo:table-column column-width="36pt"/>
	                    <fo:table-column column-width="36pt"/>
	                    <fo:table-column column-width="44pt"/>
	                    <fo:table-column column-width="55pt"/>
	                    <fo:table-column column-width="34pt"/>
	                    <fo:table-column column-width="36pt"/>
	                    <fo:table-column column-width="44pt"/>
	                    <fo:table-column column-width="36pt"/>
	                    <fo:table-column column-width="38pt"/>
	                    <fo:table-column column-width="40pt"/>
	                    <fo:table-body>
	                    <#assign grandPrevFat= 0>
	                    <#assign grandPrevSnf= 0>
	                    <#assign grandCurrFat= 0>
	                    <#assign grandCurrSnf= 0>
		                    <#if grandTotPrevProductMap?has_content>
		                    	<#assign grandPrevTotalMap = grandTotPrevProductMap.entrySet()>
		                    	<#assign counter = 1>
		               			<#list grandPrevTotalMap as grandPrevTotal>
		               			<#assign grandPrev = grandPrevTotal.getValue().get("qtyLtrs")>
		            			<#assign grandCurrTotal = grandTotCurrProductMap.get(grandPrevTotal.getKey())>
		               			<#assign grandCurr = grandCurrTotal.get("qtyLtrs")>
		               				<#if ((grandCurr+grandPrev)!=0)>
			                    		<fo:table-row>
				                    	  	<#if counter == 1>
						        				<fo:table-cell>
						        			   		<fo:block font-size="6pt" text-align="left" font-weight="bold">SHED TOTAL :</fo:block>
						                   		</fo:table-cell>
						                    	<#else>
						                    	<fo:table-cell>
						        			   		<fo:block font-size="6pt" text-align="right" font-weight="bold"></fo:block>
						                    	</fo:table-cell>
						                    </#if> 
					                    	<#assign counter = counter+1>
						                   	<#assign product={}>
					                    	<#if grandPrevTotal.getKey() != "TOT">
					                    		<#assign product = delegator.findOne("Product", {"productId" : grandPrevTotal.getKey()}, true)>
					                   		</#if>
					                    	<#if product?has_content>
					                    	<fo:table-cell>
					                			<fo:block font-size="6pt" text-align="right" font-weight="bold">${product.brandName}</fo:block>
					                   		</fo:table-cell>
					                   		<#else>
					                   			<fo:table-cell>
					                				<fo:block font-size="6pt" text-align="right" font-weight="bold">${grandPrevTotal.getKey()}</fo:block>
					                   			</fo:table-cell>
					                    	</#if>
					                   		<fo:table-cell>
					                   	 		<fo:block font-size="6pt" text-align="right" font-weight="bold">${grandPrevTotal.getValue().get("qtyLtrs")?if_exists?string('#0.0')}</fo:block>
					                   		</fo:table-cell>
					                   		<#assign grandPrevFat=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(grandPrevTotal.getValue().get("kgFat"),grandPrevTotal.getValue().get("qtyKgs")))>
					                   		<fo:table-cell>
					                   	 		<fo:block font-size="6pt" text-align="right" font-weight="bold">${(grandPrevFat)?if_exists?string('#0.0')}</fo:block>
					                   		</fo:table-cell>
					                   		<#assign grandPrevSnf=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(grandPrevTotal.getValue().get("kgSnf"),grandPrevTotal.getValue().get("qtyKgs")))>
					                   		<fo:table-cell>
					                   	    	<fo:block font-size="6pt" text-align="right" font-weight="bold">${(grandPrevSnf)?if_exists?string('#0.00')}</fo:block>
					                   		</fo:table-cell>
					                   		<#assign grandPrevQtyPerDay =0>
					                		<#if PrevTotalDays !=0>
					                			<#assign grandPrevQtyPerDay = (grandPrevTotal.getValue().get("qtyLtrs")/PrevTotalDays)>
					                		<#else>
					                			<#assign grandPrevQtyPerDay = 0>
					                		</#if>
						                	<fo:table-cell>	
						               			 <fo:block font-size="6pt" text-align="right" font-weight="bold">${grandPrevQtyPerDay?if_exists?string('#0')}</fo:block>
						                	</fo:table-cell>
						                	<fo:table-cell>
						                   	 	 <fo:block font-size="6pt" text-align="right" font-weight="bold">${grandCurrTotal.get("qtyLtrs")?if_exists?string('##0.0')}</fo:block>
						                   	</fo:table-cell>
						                    <#assign grandCurrFat=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(grandCurrTotal.get("kgFat"),grandCurrTotal.get("qtyKgs")))>
						                   	<fo:table-cell>
						                   	 	 <fo:block font-size="6pt" text-align="right" font-weight="bold">${(grandCurrFat)?if_exists?string('##0.0')}</fo:block>
						                   	</fo:table-cell>
						                   	<#assign grandCurrSnf=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(grandCurrTotal.get("kgSnf"),grandCurrTotal.get("qtyKgs")))>
						                   	<fo:table-cell>
						                   	 	 <fo:block font-size="6pt" text-align="right" font-weight="bold">${(grandCurrSnf)?if_exists?string('##0.00')}</fo:block>
						                   	</fo:table-cell>
						                   	<#assign grandCurrQtyPerDay =0>
						                	<#if CurrTotalDays !=0>
						                		<#assign grandCurrQtyPerDay = (grandCurrTotal.get("qtyLtrs")/CurrTotalDays)>
						                	<#else>
						                		<#assign currQtyPerDay = 0>
						                	</#if>
						                	<fo:table-cell>	
						               			 <fo:block font-size="6pt" text-align="right" font-weight="bold">${grandCurrQtyPerDay?if_exists?string('#0')}</fo:block>
						                	</fo:table-cell>
						                   	<#assign grandDiffQty = ((grandCurrTotal.get("qtyLtrs"))-(grandPrevTotal.getValue().get("qtyLtrs")))>
						                	<#if  grandDiffQty !=0>
						                		<#assign grandPrecentageVar =0>
						                		<#if grandPrevTotal.getValue().get("qtyLtrs") !=0>
						                			<#assign grandPrecentageVar =((grandDiffQty/(grandPrevTotal.getValue().get("qtyLtrs")))*100)>
						                		</#if>
						                	<#else>
						                		<#assign grandPrecentageVar = 0>
						                   	</#if>	
					                		<fo:table-cell>	
					               				<fo:block font-size="6pt" text-align="right" font-weight="bold">${(grandPrecentageVar)?if_exists?string('##0.00')}</fo:block>
					                		</fo:table-cell>
					                		<fo:table-cell>	
					               				<fo:block font-size="6pt" text-align="right" font-weight="bold">${(grandCurrFat-grandPrevFat)?if_exists?string('##0.0')}</fo:block>
					                		</fo:table-cell>
					                	  	<fo:table-cell>	
					               				<fo:block font-size="6pt" text-align="right" font-weight="bold">${(grandCurrSnf-grandPrevSnf)?if_exists?string('##0.00')}</fo:block>
					                		</fo:table-cell>
										</fo:table-row>
										<#if grandPrevTotal.getKey() == "102">
			        						<fo:table-row>
		            							<fo:table-cell>
			        			 					<fo:block  white-space-collapse="false" font-size="5pt" keep-together="always">&#160;													-------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			        							</fo:table-cell>
			        						</fo:table-row>	
			        						</#if> 
									</#if> 
								</#list>
							</#if>  
						</fo:table-body>
					</fo:table>
				</fo:block>
				<fo:block font-size="6pt">---------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
    		</fo:flow>
		</fo:page-sequence>
	</#if>
</fo:root>
</#escape>