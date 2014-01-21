<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-top=".5in">
                <fo:region-body margin-top=".7in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
     <#if routeBillingList?has_content>
        	<#list routeBillingList as routeBilling>
        		<#assign pageNo = 0>	
        		<#assign agentEntryDetails = routeBilling.agentEntryDetails>
        		<#assign tipAmtRateMap = routeBilling.tipAmtRateMap>
        		<#assign agentWiseCommnMap = routeBilling.agentWiseCommnMap>
        		<#assign adjustments = routeBilling.adjustments>
        		<#assign useTotSolidsMap = routeBilling.useTotSolidsMap>   
        
        <#assign size =0>
        <#assign centerDetails =0>
            	<#assign dayTotalsEntries = agentEntryDetails.entrySet()>  
            	<#assign agentDetails =0>
    	<#list dayTotalsEntries as dayTotalsEntry>  
    	  	<#if dayTotalsEntry.getKey() !="totalQtyLtrs" && dayTotalsEntry.getKey() !="totalQtyKgs" && dayTotalsEntry.getKey() !="totalKgFat" && dayTotalsEntry.getKey() !="totalKgSnf" && dayTotalsEntry.getKey() !="totalPrice">          
				<#assign agentDetails = Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].getCenterDtails(dctx ,Static["org.ofbiz.base.util.UtilMisc"].toMap("centerId", dayTotalsEntry.getKey())).get("unitFacility")>
				<#assign centerDetails = delegator.findOne("Facility", {"facilityId" : dayTotalsEntry.getKey()}, true)>
				<#assign adjustmentEntries = adjustments.entrySet()>
		  			<#assign additions=0>
		  			<#assign deductions=0>
		  			<#assign cartage =0>	
		  			<#assign deductionsMap = 0>
		  			<#assign additionsMap = 0>	  			
					<#list adjustmentEntries as adjustmentEntry>
						<#if adjustmentEntry.getKey() == dayTotalsEntry.getKey()>
							<#assign additions= adjustmentEntry.getValue().get("ADDITIONS")>
							<#assign deductions= adjustmentEntry.getValue().get("DEDUCTIONS")>
							<#assign deductionsMap = adjustmentEntry.getValue().get("dedValuesList")>
							<#assign additionsMap = adjustmentEntry.getValue().get("additionsList")>
							<#assign cartage = adjustmentEntry.getValue().get("cartage")>
						</#if>
					</#list>
		  	</#if>	   
		  	<#assign pageNo = pageNo+1>                 							 	
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        		<#assign routeDetails = delegator.findOne("Facility", {"facilityId" : centerDetails.get("parentFacilityId")}, true)>
        		<fo:block text-align="left" white-space-collapse="false" font-size="8pt" keep-together="always">&#160;                    ${centerDetails.get("facilityName")}</fo:block>
        		<fo:block text-align="left" white-space-collapse="false" font-size="8pt" keep-together="always">MP Payment Payable Details From ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "dd/MM/yyyy")}  PAGE NO:${pageNo}</fo:block>
        		<fo:block font-size="8pt">------------------------------------------------------------------------------------</fo:block>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="8pt">ROUTE : ${routeDetails.get("facilityCode")} ${routeDetails.get("facilityName")}            Representative : ${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, centerDetails.get("ownerPartyId"), false))),18)}</fo:block>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="8pt">UNIT  : ${agentDetails.get("facilityCode")} ${agentDetails.get("facilityName")}           Village : ${centerDetails.get("facilityCode")} ${centerDetails.get("facilityName")}</fo:block>	 	 	  
        	</fo:static-content>
         <#assign dayTotalValues= dayTotalsEntry.getValue().entrySet()> 
        <#list dayTotalValues as dayTotals>  
        	<#assign dayWiseAgentTotals= dayTotals.getValue().entrySet()>   
        	  <#assign totComn =0>   	
       	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace"> 	 
       	<#list procurementProductList as procProducts>	
       			<fo:block font-size="8pt">------------------------------------------------------------------------------------</fo:block>
        		<fo:block keep-together="always" white-space-collapse="false" font-size="7pt">&#160;Dt Shf  QTY.Kgs QTY.Lts ST  FAT%  SNF% Fat(Kg) Snf(Kg)  COMMN   Tot.Amt Ltr.Rate</fo:block>
        		<fo:block font-size="8pt">------------------------------------------------------------------------------------</fo:block>
       		<fo:block>MILK TYPE : ${procProducts.productName}</fo:block>  
           <#list dayWiseAgentTotals as dayWiseTotals>	   			
            <fo:block>
            	<fo:table>
                    <fo:table-column column-width="15pt"/>
                    <fo:table-column column-width="30pt"/>  
               	    <fo:table-column column-width="30pt"/>
               	    <fo:table-column column-width="30pt"/>  
               	    <fo:table-column column-width="30pt"/>
               	    <fo:table-column column-width="30pt"/>  
               	    <fo:table-column column-width="30pt"/>
               	    <fo:table-column column-width="30pt"/>  
               	    <fo:table-column column-width="30pt"/>
               	    <fo:table-column column-width="30pt"/>  
               	    <fo:table-column column-width="30pt"/>
               	    <fo:table-column column-width="30pt"/>  
               	    <fo:table-column column-width="30pt"/>
               	    <fo:table-column column-width="30pt"/>  
               	    <fo:table-column column-width="30pt"/>                             	          			           
                    <fo:table-body>  
                    	<fo:table-row>                    		
	                    	<fo:table-cell>	
	                        <#assign dayWiseTotalsEntries = (dayWiseTotals.getValue()).entrySet()>                            	
	                        	<#list dayWiseTotalsEntries  as dayWiseTotalsEntry>
	                       			<#if dayWiseTotalsEntry.getKey() !="TOT">                      
	                           			<fo:block font-size="8pt">
	                           				<fo:table>
                    							<fo:table-column column-width="5pt"/>
                    							<fo:table-column column-width="15pt"/>
                    							<fo:table-column column-width="15pt"/>
                    							<fo:table-column column-width="35pt"/>
                    							<fo:table-column column-width="40pt"/>
                    							<fo:table-column column-width="18pt"/>
                    							<fo:table-column column-width="30pt"/>
                    							<fo:table-column column-width="33pt"/>
                    							<fo:table-column column-width="37pt"/>
                    							<fo:table-column column-width="35pt"/>
                    							<fo:table-column column-width="47pt"/>
                    							<fo:table-column column-width="50pt"/>
                    							<fo:table-column column-width="40pt"/>
                    							<fo:table-column column-width="35pt"/>
                    							<fo:table-column column-width="37pt"/>
                    							<fo:table-column column-width="35pt"/>
                    							<fo:table-column column-width="42pt"/>
                    							<fo:table-column column-width="42pt"/>
                    							<fo:table-column column-width="50pt"/>
                    							 <fo:table-body>                
                    						<#if dayWiseTotals.getKey() !="TOT">     							
                    							 	<fo:table-row>
	                    								<fo:table-cell></fo:table-cell>                    								            									
	                        						<#assign size = dayWiseTotalsEntries.size()>
	                        							<#assign billReportEntries = (dayWiseTotalsEntry.getValue()).get(procProducts.productName)>
	                        							<#if billReportEntries.get("qtyKgs") !=0>
	                        							<fo:table-cell >	
	                        								<fo:block text-align="left" >${dayWiseTotals.getKey().substring(8)}</fo:block>
	                        							</fo:table-cell>
	                        							<fo:table-cell >	
	                        								<fo:block >${dayWiseTotalsEntry.getKey()}</fo:block>
	                        							</fo:table-cell>
	                        							<fo:table-cell>
	                        								<fo:block text-align="right">${(billReportEntries.get("qtyKgs"))?string("##0.00")}</fo:block>
	                        							</fo:table-cell>	                        							               	  
	                        							<fo:table-cell>
	                        								<fo:block text-align="right">${(billReportEntries.get("qtyLtrs"))?string("##0.00")}</fo:block>
	                        							</fo:table-cell>
	                        							<fo:table-cell>
	                        									<fo:block text-align="right">G</fo:block>
	                        							</fo:table-cell>	                        							
	                        							<fo:table-cell>
	                        									<fo:block text-align="right">${(billReportEntries.get("fat"))?string("##0.0")}</fo:block>
	                        							</fo:table-cell>
	                        							<fo:table-cell>
	                        									<fo:block text-align="right">${(billReportEntries.get("snf"))?string("##0.00")}</fo:block>
	                        							</fo:table-cell>
	                        							<fo:table-cell>
	                        									<fo:block text-align="right">${((billReportEntries.get("qtyKgs")*billReportEntries.get("fat"))/100)?string("##0.00")}</fo:block>
	                        							</fo:table-cell>
	                        							<fo:table-cell>
	                        									<fo:block text-align="right">${((billReportEntries.get("qtyKgs")*billReportEntries.get("snf"))/100)?string("##0.00")}</fo:block>
	                        							</fo:table-cell>
	                        							<fo:table-cell>
	                        									<fo:block text-align="right">${(agentWiseCommnMap[centerDetails.get("facilityId")].get(procProducts.productName)*billReportEntries.get("qtyLtrs"))?string("##0.00")}</fo:block>	 
	                        							</fo:table-cell>	                        							
	                        							<fo:table-cell>
	                        								<fo:block text-align="right">${(billReportEntries.get("price"))?string("##0.00")}</fo:block>
	                        							</fo:table-cell>
	                        							<fo:table-cell>
	                        								<fo:block text-align="right"><#if billReportEntries.get("qtyLtrs") !=0>${(billReportEntries.get("price")/billReportEntries.get("qtyLtrs"))?string("##0.00")}</#if></fo:block>
	                        							</fo:table-cell>	                        								                        						
	                        						</#if>	
	                        						</fo:table-row>	                        					
	                        					</#if>		
	                        					</fo:table-body>
	                        				</fo:table>			
	                           			</fo:block>
	                           		</#if> 	                           		              		
	                        	 </#list> 
	                        </fo:table-cell>	                        	
	                        </fo:table-row>	                          
                       		</fo:table-body>
                		</fo:table>
					</fo:block> 
				 </#list>
				   <fo:block font-size="8pt" page-break-after="always">------------------------------------------------------------------------------------</fo:block>
				</#list>
				<#list dayWiseAgentTotals as dayWiseTotals>		   
		       					<#if dayWiseTotals.getKey() =="TOT">	
		       							<fo:block font-size="8pt">------------------------------------------------------------------------------------</fo:block>	       			 			
		       							<fo:block>
		       								<fo:table>
		       									<fo:table-column column-width="30pt"/>
		       									<fo:table-column column-width="95pt"/>
		       									<fo:table-column column-width="75pt"/>
		       									<fo:table-column column-width="55pt"/>
		       									<fo:table-column column-width="55pt"/>
		       									<fo:table-column column-width="70pt"/>
		       									<fo:table-column column-width="90pt"/>
		       									<fo:table-column column-width="60pt"/>
		       									<fo:table-column column-width="60pt"/>
		       									<fo:table-header> 
		       										<fo:table-cell>
		       											<fo:block keep-together="always" white-space-collapse="false"  font-size="8pt">&#160;            KGS       LTRS       FAT    SNF      COM     TOT.AMT    AVG PRICE</fo:block>
		       										</fo:table-cell>
		       									</fo:table-header>
		       									<fo:table-body>
		       									<fo:table-row>
		       										<fo:table-cell>
		       											<fo:block font-size="8pt">------------------------------------------------------------------------------------</fo:block>
		       										</fo:table-cell>
		       									</fo:table-row>
		       									<#list procurementProductList as procProducts>
		       										<#assign totComn = totComn + (agentWiseCommnMap[centerDetails.get("facilityId")].get(procProducts.productName)*dayWiseTotals.getValue().get("TOT").get(procProducts.productName).get("qtyLtrs"))>
		       										<fo:table-row>
		       											<fo:table-cell>
		       												<fo:block keep-together="always" white-space-collapse="false">TOTAL ${procProducts.brandName} :</fo:block>
		       											</fo:table-cell>
		       											<fo:table-cell>		       												
		       												<fo:block text-align="right">${dayWiseTotals.getValue().get("TOT").get(procProducts.productName).get("qtyKgs")?string("##0.00")}</fo:block>
		       											</fo:table-cell>
		       											<fo:table-cell>
		       												<fo:block text-align="right">${dayWiseTotals.getValue().get("TOT").get(procProducts.productName).get("qtyLtrs")?string("##0.00")}</fo:block>
		       											</fo:table-cell>
		       											<fo:table-cell>
		       												<fo:block text-align="right">${dayWiseTotals.getValue().get("TOT").get(procProducts.productName).get("fat")?string("##0.00")}</fo:block>
		       											</fo:table-cell>
		       											<fo:table-cell>
		       												<fo:block text-align="right">${dayWiseTotals.getValue().get("TOT").get(procProducts.productName).get("snf")?string("##0.00")}</fo:block>
		       											</fo:table-cell>
		       											<fo:table-cell>
		       												<fo:block text-align="right">${(agentWiseCommnMap[centerDetails.get("facilityId")].get(procProducts.productName)*dayWiseTotals.getValue().get("TOT").get(procProducts.productName).get("qtyLtrs"))?string("##0.00")}</fo:block>
		       											</fo:table-cell>
		       											<fo:table-cell>
		       												<fo:block text-align="right">${dayWiseTotals.getValue().get("TOT").get(procProducts.productName).get("price")?string("##0.00")}</fo:block>
		       											</fo:table-cell>
		       											<fo:table-cell>
		       												<fo:block text-align="right"><#if dayWiseTotals.getValue().get("TOT").get(procProducts.productName).get("qtyLtrs") !=0>${(dayWiseTotals.getValue().get("TOT").get(procProducts.productName).get("price")/dayWiseTotals.getValue().get("TOT").get(procProducts.productName).get("qtyLtrs"))?string("##0.00")}</#if></fo:block>
		       											</fo:table-cell>
		       										</fo:table-row>
		       									</#list>
		       									<fo:table-row>
		       										<fo:table-cell>
		       											<fo:block >------------------------------------------------------------------------------</fo:block>
		       										</fo:table-cell>
		       									</fo:table-row>	
		       									<fo:table-row>
	       											<fo:table-cell>
	       												<fo:block keep-together="always" white-space-collapse="false">TOTAL    :</fo:block>
	       											</fo:table-cell>
	       											<fo:table-cell>		       												
	       												<fo:block text-align="right">${dayWiseTotals.getValue().get("TOT").get("TOT").get("qtyKgs")?string("##0.00")}</fo:block>
	       											</fo:table-cell>
	       											<fo:table-cell>
	       												<fo:block text-align="right">${dayWiseTotals.getValue().get("TOT").get("TOT").get("qtyLtrs")?string("##0.00")}</fo:block>
	       											</fo:table-cell>
	       											<fo:table-cell>
	       												<fo:block text-align="right">${dayWiseTotals.getValue().get("TOT").get("TOT").get("fat")?string("##0.00")}</fo:block>
	       											</fo:table-cell>
	       											<fo:table-cell>
	       												<fo:block text-align="right">${dayWiseTotals.getValue().get("TOT").get("TOT").get("snf")?string("##0.00")}</fo:block>
	       											</fo:table-cell>
	       											<fo:table-cell>
		       											<fo:block text-align="right">${totComn?string("##0.00")}</fo:block>
		       										</fo:table-cell>
		       										<fo:table-cell>
	       												<fo:block text-align="right">${dayWiseTotals.getValue().get("TOT").get("TOT").get("price")?string("##0.00")}</fo:block>
	       											</fo:table-cell>
	       											<fo:table-cell>
	       												<fo:block text-align="right"><#if dayWiseTotals.getValue().get("TOT").get("TOT").get("qtyLtrs") !=0>${(dayWiseTotals.getValue().get("TOT").get("TOT").get("price")/dayWiseTotals.getValue().get("TOT").get("TOT").get("qtyLtrs"))?string("##0.00")}</#if></fo:block>
	       											</fo:table-cell>
	       										</fo:table-row>
		       									<fo:table-row>
		       										<fo:table-cell>
		       											<fo:block >------------------------------------------------------------------------------</fo:block>
		       										</fo:table-cell>
		       									</fo:table-row>	
		       									<fo:table-row>
		       										<fo:table-cell>
		       											<fo:block keep-together="always" font-weight="bold">ADDITIONAL AMOUNT</fo:block>
		       										</fo:table-cell>
		       									</fo:table-row>
		       									<fo:table-row>
		       										<fo:table-cell>
		       											<fo:block keep-together="always">1) MILK VALUE :</fo:block>
		       											<fo:block keep-together="always">2) COMMISSION :</fo:block>
		       											<fo:block keep-together="always" white-space-collapse="false">3) CARTAGE    :</fo:block>
		       											<#assign additionsList = additionsMap.entrySet()>
		       											<#assign num=4>
		       											<#list additionsList as additionValues>
		       												
		       												<#assign adjustmentType = delegator.findOne("OrderAdjustmentType", {"orderAdjustmentTypeId" : additionValues.getKey()}, true)>
		       												<fo:block keep-together="always" white-space-collapse="false">${num}) ${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(adjustmentType.description?if_exists)),7)}    :</fo:block>
		       												<#assign num=num+1>
		       											</#list>
		       										</fo:table-cell>
		       										<fo:table-cell/>
		       										<fo:table-cell>
		       											<fo:block text-align="right">${dayWiseTotals.getValue().get("TOT").get("TOT").get("price")?string("##0.00")}</fo:block>
		       											<fo:block text-align="right"> ${totComn?string("##0.00")}</fo:block>
		       											<fo:block text-align="right">${cartage?string("##0.00")}</fo:block>
		       											<#assign additionsList = additionsMap.entrySet()>
		       											<#list additionsList as additionValues>
		       												<fo:block text-align="right">${additionValues.getValue()?string("##0.00")}</fo:block>
		       												<#assign additionsList =0>
		       											</#list>
		       										</fo:table-cell>
		       									</fo:table-row>
		       									<fo:table-row>
		       										<fo:table-cell>
		       											<fo:block >-------------------------------</fo:block>
		       										</fo:table-cell>
		       									</fo:table-row>	
		       									<fo:table-row>
		       										<fo:table-cell>
		       											<fo:block keep-together="always" white-space-collapse="false">GROSS AMOUNT  :</fo:block>
		       										</fo:table-cell>
		       										<fo:table-cell/>
		       										<fo:table-cell>
		       											<fo:block text-align="right">${(dayWiseTotals.getValue().get("TOT").get("TOT").get("price")+totComn+additions)?string("##0.00")}</fo:block>
		       										</fo:table-cell>
		       									</fo:table-row>
		       									<fo:table-row>
		       										<fo:table-cell>
		       											<fo:block >-------------------------------</fo:block>
		       										</fo:table-cell>
		       									</fo:table-row>	
		       									<fo:table-row>
		       										<fo:table-cell>
		       											<fo:block keep-together="always" font-weight="bold">DEDUCTION AMOUNT</fo:block>
		       										</fo:table-cell>
		       									</fo:table-row>
		       									<fo:table-row>
		       										<fo:table-cell>
		       											
		       											<#assign deductionsList = deductionsMap.entrySet()>
		       											<#assign num=1>
		       											<#list deductionsList as dedutionValues>
		       												<#assign adjustmentType = delegator.findOne("OrderAdjustmentType", {"orderAdjustmentTypeId" : dedutionValues.getKey()}, true)>
		       												<fo:block keep-together="always" white-space-collapse="false">${num}) ${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(adjustmentType.description?if_exists)),9)}    :</fo:block>
		       												<#assign num=num+1>
		       											</#list>
		       										</fo:table-cell>
		       										<fo:table-cell/>
		       										<fo:table-cell>     											
		       											<#list deductionsList as dedutionValues>
		       												<fo:block text-align="right">${dedutionValues.getValue()?string("##0.00")}</fo:block>
		       												<#assign deductionsList =0>
		       											</#list>
		       										</fo:table-cell>
		       									</fo:table-row>
		       									<fo:table-row>
		       										<fo:table-cell>
		       											<fo:block >-------------------------------</fo:block>
		       										</fo:table-cell>
		       									</fo:table-row>	
		       									<fo:table-row>
		       										<fo:table-cell>
		       											<fo:block keep-together="always" white-space-collapse="false">TOTAL DEDUCTION :</fo:block>
		       										</fo:table-cell>
		       										<fo:table-cell/>
		       										<fo:table-cell>
		       											<fo:block text-align="right">${deductions?string("##0.00")}</fo:block>
		       										</fo:table-cell>
		       									</fo:table-row>
		       									<fo:table-row>
		       										<fo:table-cell>
		       											<fo:block >-------------------------------</fo:block>
		       										</fo:table-cell>
		       									</fo:table-row>
		       									<fo:table-row>
		       										<fo:table-cell/>
		       										<fo:table-cell/>
		       										<fo:table-cell/>
		       										<fo:table-cell>
		       											<fo:block keep-together="always" white-space-collapse="false" font-weight="bold">NET AMOUNT PAYABLE :</fo:block>
		       										</fo:table-cell>
		       										<fo:table-cell/>
		       										<fo:table-cell/>
		       										<fo:table-cell>
		       											<fo:block text-align="right" font-weight="bold">${((dayWiseTotals.getValue().get("TOT").get("TOT").get("price")+totComn+additions)-deductions)?string("##0.00")}</fo:block>
		       										</fo:table-cell>
		       									</fo:table-row>
		       								</fo:table-body>
		       							</fo:table>
		       							</fo:block> 	       						  						
		       						</#if>		       				
		       					</#list>			
            			</fo:flow>	
					</#list>	
				</fo:page-sequence>
			</#list> 
			</#list> 
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