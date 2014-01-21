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
                <fo:region-body margin-top="1.3in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "BM/CMMilkBill.txt")}
        <#assign size =0>
        <#assign centerDetails =0>
        <#assign pageNo = 0>
        <#if routeBillingList?has_content>
        	<#list routeBillingList as routeBilling>
        		<#assign pageNo = 0>	
        		<#assign agentEntryDetails = routeBilling.agentEntryDetails>
        		<#assign tipAmtRateMap = routeBilling.tipAmtRateMap>
        		<#assign agentWiseCommnMap = routeBilling.agentWiseCommnMap>
        		<#assign adjustments = routeBilling.adjustments>
        		<#assign useTotSolidsMap = routeBilling.useTotSolidsMap>
        		
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
        		  			<#assign deductionsMap = (Static["javolution.util.FastMap"])>
        					<#list adjustmentEntries as adjustmentEntry>
        						<#if adjustmentEntry.getKey() == dayTotalsEntry.getKey()>
        							<#assign additions= adjustmentEntry.getValue().get("ADDITIONS")>
        							<#assign deductions= adjustmentEntry.getValue().get("DEDUCTIONS")>
        							<#assign deductionsMap = adjustmentEntry.getValue().get("dedValuesList")>
        							<#assign cartage = adjustmentEntry.getValue().get("cartage")>
        						</#if>
        					</#list>
        		  	</#if>
        		  	<#assign pageNo = pageNo+1> 
        		  	<#assign prodQtyKgs = 1>
        		  	<#assign displayDed = "N">
        		  	<#list loopProductList as productDetails>	
        		  		<#assign productDayTotals = dayTotalsEntry.getValue().get("dayTotals").get("TOT").get("TOT").get(productDetails.productName)>
        		  		<#assign prodQtyKgs = productDayTotals.get("qtyKgs")>
        		  	<#if prodQtyKgs!=0>	
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        		<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
        		<fo:block text-align="left" white-space-collapse="false" font-size="8pt" keep-together="always">${reportHeader.description?if_exists}. MILL BILL FOR THE PERIOD FROM ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "dd/MM/yyyy")}</fo:block>
        		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
        		<#assign routeDetails = delegator.findOne("Facility", {"facilityId" : centerDetails.get("parentFacilityId")}, true)>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="8pt">UNIT :  ${agentDetails.get("facilityCode")} ${agentDetails.get("facilityName")}   ROUTE :${routeDetails.get("facilityCode")}  ${routeDetails.get("facilityName")}   CENTER:${centerDetails.get("facilityCode")} ${centerDetails.get("facilityName")}     MILK TYPE : ${productDetails.productName}    BILL NO:${pageNo}</fo:block>	 	 	  
        		<fo:block font-size="8pt">------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="8pt">.                         GOOD MILK                                                            SOUR MILK             CURDLED           TOTAL</fo:block>
        		<fo:block keep-together="always" white-space-collapse="false" font-size="8pt">&#160;DTD   MOR   --------------------------------------------------------------------  ---------------------------------- ------- -------------------------------</fo:block>
        		<fo:block keep-together="always" white-space-collapse="false" font-size="8pt">&#160;      EVE   QTY-LTS  QTY-KG  FAT%  RATE    VALUE    SNF     PREM/DED    AMOUNT    QTY-LTS   QTY-KG   FAT%   VALUE   QTY-LTS    LTS      KGS      AMOUNT</fo:block>
        		<fo:block font-size="8pt">------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
        	</fo:static-content>
       	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace"> 	   
        <#assign dayTotalValues= dayTotalsEntry.getValue().entrySet()> 
        <#list dayTotalValues as dayTotals>  
        	<#assign dayWiseAgentTotals= dayTotals.getValue().entrySet()>  
        	<#list dayWiseAgentTotals as dayWiseTotals >       			
            <fo:block>
            	<fo:table >
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
                	<#if dayWiseTotals.getKey() =="TOT"> 
                    	<fo:table-row>
                    		<fo:table-cell >	
	                    		<fo:block font-size="7pt">------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	                        </fo:table-cell>
                    	</fo:table-row>
                    </#if>                  
                    	<fo:table-row>                    		
	                    	<fo:table-cell>	
	                        <#assign dayWiseTotalsEntries = (dayWiseTotals.getValue()).entrySet()>                            	
	                        	<#list dayWiseTotalsEntries  as dayWiseTotalsEntry>
	                       			<#if dayWiseTotalsEntry.getKey() !="TOT">                      
	                           			<fo:block font-size="8pt">
	                           				<fo:table >
                    							<fo:table-column column-width="5pt"/>
                    							<fo:table-column column-width="20pt"/>
                    							<fo:table-column column-width="25pt"/>
                    							<fo:table-column column-width="30pt"/>
                    							<fo:table-column column-width="35pt"/>
                    							<fo:table-column column-width="25pt"/>
                    							<fo:table-column column-width="35pt"/>
                    							<fo:table-column column-width="50pt"/>
                    							<fo:table-column column-width="27pt"/>
                    							<fo:table-column column-width="47pt"/>
                    							<fo:table-column column-width="47pt"/>
                    							<fo:table-column column-width="35pt"/>
                    							<fo:table-column column-width="35pt"/>
                    							<fo:table-column column-width="35pt"/>
                    							<fo:table-column column-width="37pt"/>
                    							<fo:table-column column-width="35pt"/>
                    							<fo:table-column column-width="42pt"/>
                    							<fo:table-column column-width="42pt"/>
                    							<fo:table-column column-width="50pt"/>
                    							 <fo:table-body>                    							
                    							 <#list procurementProductList as procProducts>                    							 	
                    								<fo:table-row>
	                    								<fo:table-cell ></fo:table-cell>                    								            									
	                        						<#if  productDetails.productName == procProducts.productName>
	                        						<#assign size = dayWiseTotalsEntries.size()>
	                        							<#assign billReportEntries = (dayWiseTotalsEntry.getValue()).get(productDetails.productName)>
	                        							<#if billReportEntries.get("qtyKgs") !=0>
	                        							<fo:table-cell >	
	                        							<#if dayWiseTotals.getKey() !="TOT">
	                        								<fo:block text-align="left" >${dayWiseTotals.getKey().substring(8)}</fo:block>
	                        								<#else>
	                        									<fo:block text-align="left">${dayWiseTotals.getKey()}</fo:block>
	                        								</#if>	                        								
	                        							</fo:table-cell>
	                        							<fo:table-cell >	
	                        								<fo:block >${dayWiseTotalsEntry.getKey()}</fo:block>
	                        							</fo:table-cell>
	                        								                        							               	  
	                        							<fo:table-cell>
	                        								<fo:block text-align="right">${(billReportEntries.get("qtyLtrs"))?string("##0.0")}</fo:block>
	                        							</fo:table-cell>
	                        							<fo:table-cell>
	                        								<fo:block text-align="right">${(billReportEntries.get("qtyKgs"))?string("##0.0")}</fo:block>
	                        							</fo:table-cell>
	                        							<fo:table-cell>
	                        								<#if dayWiseTotals.getKey() !="TOT">
	                        									<fo:block text-align="right">${(billReportEntries.get("fat"))?string("##0.0")}</fo:block>
	                        								<#else>
	                        									<fo:block text-align="right">0.0</fo:block>
	                        								</#if>
	                        							</fo:table-cell>
	                        							<fo:table-cell>
	                        								<#if dayWiseTotals.getKey() !="TOT">	
	                        									<#assign slabRate= Static["in.vasista.vbiz.procurement.PriceServices"].getProcurementProductPrice(dctx,Static["org.ofbiz.base.util.UtilMisc"].toMap("userLogin",userLogin,"facilityId",centerDetails.get("facilityId"),"productId",procProducts.productId,"fatPercent",billReportEntries.get("fat"),"snfPercent",billReportEntries.get("snf")))>                								                     								
	                        									<fo:block text-align="right">${(slabRate.get("defaultRate"))?if_exists?string("##0.00")} </fo:block>
	                        								</#if>	
	                        							</fo:table-cell>
	                        							<fo:table-cell>
	                        								<fo:block text-align="right">${(billReportEntries.get("price")-(billReportEntries.get("totPrem")))?string("##0.00")}</fo:block>
	                        							</fo:table-cell>
	                        							<fo:table-cell>
	                        								<#if dayWiseTotals.getKey() !="TOT">
	                        									<fo:block text-align="right">${(billReportEntries.get("snf"))?string("##0.00")}</fo:block>
	                        								
	                        								</#if>
	                        							</fo:table-cell>
	                        							<fo:table-cell>
	                        							<#if dayWiseTotals.getKey() !="TOT">
	                        								<fo:block text-align="right">${(billReportEntries.get("totPrem"))?string("##0.00")}</fo:block>
	                        							<#else>
	                        								<fo:block text-align="right">${(billReportEntries.get("totPrem"))?string("##0.00")}</fo:block>
	                        							</#if>	
	                        							</fo:table-cell>
	                        							<fo:table-cell>
	                        								<fo:block text-align="right">${(billReportEntries.get("price"))?string("##0.00")}</fo:block>
	                        							</fo:table-cell>
	                        							<fo:table-cell>
	                        								<fo:block text-align="right">${(billReportEntries.get("sQtyLtrs"))?string("##0.0")}</fo:block>
	                        							</fo:table-cell>
	                        							<fo:table-cell>
	                        								<fo:block text-align="right">${(billReportEntries.get("sQtyLtrs")*1.03)?string("##0.0")}</fo:block>
	                        							</fo:table-cell>
	                        							<fo:table-cell>
	                        								<fo:block text-align="right">${(billReportEntries.get("sFat"))?string("##0.0")}</fo:block>
	                        							</fo:table-cell>
	                        							<fo:table-cell>
	                        								<fo:block text-align="right">${(billReportEntries.get("sPrice"))?string("##0.00")}</fo:block>
	                        							</fo:table-cell>
	                        							<fo:table-cell>
	                        								<fo:block text-align="right">${(billReportEntries.get("cQtyLtrs"))?string("##0.0")}</fo:block>
	                        							</fo:table-cell>
	                        							<fo:table-cell>
	                        								<fo:block text-align="right">${(billReportEntries.get("qtyLtrs")+billReportEntries.get("sQtyLtrs"))?string("##0.00")}</fo:block>
	                        							</fo:table-cell>
	                        							<fo:table-cell>
	                        								<fo:block text-align="right">${((billReportEntries.get("sQtyLtrs")*1.03)+billReportEntries.get("qtyKgs"))?string("##0.00")}</fo:block>
	                        							</fo:table-cell>
	                        							<fo:table-cell>
	                        								<fo:block text-align="right">${((billReportEntries.get("price")+billReportEntries.get("sPrice")))?string("##0.00")}</fo:block>
	                        							</fo:table-cell>	                        						
	                        						</#if>	
	                        						</#if>
	                        						</fo:table-row>
	                        					</#list>	
	                        					</fo:table-body>
	                        				</fo:table>			
	                           			</fo:block>
	                           		</#if> 	                           		              		
	                        	 </#list> 
	                        	 <fo:block linefeed-treatment="preserve" font-size="8pt">&#xA;</fo:block>          
	                        </fo:table-cell>	                        	
	                        </fo:table-row>	                          
                       		</fo:table-body>
                		</fo:table>
					</fo:block> 
					<#assign totalEntries=(dayWiseTotals.getValue()).get("TOT")>
					<#list procurementProductList as procProducts>
						<#if  productDetails.productName == procProducts.productName>
							<#assign GrTotEntries= totalEntries.get(procProducts.productName)>				
						</#if>
					</#list>											
				</#list>														
					<fo:block font-size="8pt">------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
					<fo:block font-size="8pt">
						<fo:table>
							<fo:table-column column-width="15pt"/>
                    		<fo:table-column column-width="20pt"/>
                    		<fo:table-column column-width="50pt"/>
                    		<fo:table-column column-width="47pt"/>
                    		<fo:table-column column-width="40pt"/>
                    		<fo:table-column column-width="80pt"/>
                    		<fo:table-column column-width="55pt"/>
                    		<fo:table-column column-width="30pt"/>
                    		<fo:table-column column-width="54pt"/>
                    		<fo:table-column column-width="37pt"/>
                    		<fo:table-column column-width="45pt"/>
                    		<fo:table-column column-width="35pt"/>
                    		<fo:table-column column-width="40pt"/>
                    		<fo:table-column column-width="45pt"/>
                    		<fo:table-column column-width="52pt"/>
                    		<fo:table-column column-width="51pt"/>  
                    		<fo:table-column column-width="45pt"/>
                    		<fo:table-column column-width="50pt"/>
                    		<fo:table-column column-width="50pt"/>
                    		<fo:table-column column-width="50pt"/>                 		
                    		<fo:table-body>                    			
                    			<fo:table-row font-size="8pt">
                    				<fo:table-cell><fo:block >TOTAL</fo:block></fo:table-cell>
                    				<fo:table-cell></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right">${(GrTotEntries.get("qtyLtrs"))?if_exists?string("##0.0")}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="left" text-indent="2pt">${(GrTotEntries.get("qtyKgs"))?if_exists?string("##0.0")}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="left" text-indent="5pt">0.0</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right">${(GrTotEntries.get("price")-(GrTotEntries.get("totPrem")))?if_exists?string("##0.00")}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right"></fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right">${(GrTotEntries.get("totPrem"))?if_exists?string("##0.00")}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right">${(GrTotEntries.get("price"))?if_exists?string("##0.00")}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right">${(GrTotEntries.get("sQtyLtrs"))?string("##0.0")}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right">${((GrTotEntries.get("sQtyLtrs")*1.03))?if_exists?string("##0.0")}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right" text-indent="5pt">0.0</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right">${(GrTotEntries.get("sPrice"))?if_exists?string("##0.00")}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right" text-indent="8pt">${(GrTotEntries.get("cQtyLtrs"))?string("##0.0")}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right">${(GrTotEntries.get("qtyLtrs")+GrTotEntries.get("sQtyLtrs"))?if_exists?string("##0.00")}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right">${((GrTotEntries.get("qtyKgs")+GrTotEntries.get("sQtyLtrs")*1.03))?if_exists?string("##0.00")}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right">${((GrTotEntries.get("price")+GrTotEntries.get("sPrice")))?if_exists?string("##0.00")}</fo:block></fo:table-cell>
                    			</fo:table-row>
                    			<fo:table-row>
                    				<fo:table-cell><fo:block font-size="8pt">------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
                    			</fo:table-row>
                    			<fo:table-row>
                    				<fo:table-cell>
                       				<fo:block keep-together="always" white-space-collapse="false">KGFAT  :  ${(GrTotEntries.get("kgFat").setScale(2,2))?string("##0.000")}</fo:block>
                       				<fo:block keep-together="always" white-space-collapse="false">KGSNF  :  ${GrTotEntries.get("kgSnf")?string("##0.000")}</fo:block>
                       				<fo:block keep-together="always" white-space-collapse="false">SOLIDS: ${(GrTotEntries.get("kgFat")+GrTotEntries.get("kgSnf"))?string("##0.000")}</fo:block>
                       				<#assign tipAmount=0>
                       				<#if tipAmtRateMap?has_content>
                       					<#if useTotSolidsMap[productDetails.productName] !="Y">
                       						<#assign tipAmount = (tipAmtRateMap[productDetails.productName]*(GrTotEntries.get("kgFat")-GrTotEntries.get("zeroKgFat")))>
                       					<#else>	
                       						<#assign tipAmount = (tipAmtRateMap[productDetails.productName]*((GrTotEntries.get("kgFat")+GrTotEntries.get("kgSnf"))-(GrTotEntries.get("zeroKgFat")+GrTotEntries.get("zeroKgSnf"))))>
                       					</#if>                       					
                       				</#if>
                       				<fo:block keep-together="always" white-space-collapse="false">T.I.P  : ${tipAmount?string("##0.00")}</fo:block>
                       			</fo:table-cell> 
                       			<fo:table-cell></fo:table-cell> 
                       			<fo:table-cell></fo:table-cell> 
                       			                   			
                       			<fo:table-cell>
                       				<fo:block keep-together="always" white-space-collapse="false" text-indent="12pt">SHAR-CAP: 0.00 </fo:block>
                       				<fo:block keep-together="always" white-space-collapse="false" text-indent="14pt">CONSN   : 0.00</fo:block>
                       				<fo:block keep-together="always" white-space-collapse="false" text-indent="12pt">CARTAGE : ${cartage?if_exists?string("##0.00")}</fo:block>
                       				<fo:block keep-together="always" white-space-collapse="false" text-indent="12pt">ADDTINS  :  <#if useTotSolidsMap[productDetails.productName] =="N">${additions?string("##0.00")}<#else>0.00</#if></fo:block>
                       			</fo:table-cell>
                       			   <fo:table-cell></fo:table-cell>             			
                       			<#assign dedTypes = adjustmentDedTypes.entrySet()>                     			
                       			<fo:table-cell>
                       				<#list dedTypes as adjType>
	        							<#if (adjType.getKey() <= 3)>
			        						<#if deductionsMap?has_content>	
			        							<#assign dedEntry = deductionsMap.entrySet()>	        							
			        							<#assign value =0>    
				        							<#list dedEntry as deduction>
			    										<#if ((adjType.getValue()).orderAdjustmentTypeId) == deduction.getKey()>
			    											<#assign value = deduction.getValue()>
			    										</#if>
			    										<#assign dedEntry =0>
			        								</#list>   	
		        								<fo:block text-align="left" keep-together="always" white-space-collapse="false" text-indent="28pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((adjType.getValue()).description?if_exists)),7)} : <#if useTotSolidsMap[productDetails.productName] =="N" || displayDed =="Y">${value?string("##0.00")}<#else>0.00</#if></fo:block>
	        								</#if>
	        							</#if>
	        						</#list>	        						                    				
                       			</fo:table-cell>
                       			<fo:table-cell></fo:table-cell>
                       			<fo:table-cell>
                       				<#list dedTypes as adjType>
                       					<#if (adjType.getKey() > 3) && (adjType.getKey() < 8)>	 
	                       					<#if deductionsMap?has_content>  
	                       						<#assign dedEntry = deductionsMap.entrySet()>                       					
			        							<#assign dedValue = 0>    
			        							<#list dedEntry as deduction>
		    										<#if ((adjType.getValue()).orderAdjustmentTypeId) == deduction.getKey()>
		    											<#assign dedValue = deduction.getValue()>
		    										</#if>
		    										<#assign dedEntry =0>	
		        								</#list>     		        								 							
	        								<fo:block text-align="left" keep-together="always" white-space-collapse="false">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((adjType.getValue()).description?if_exists)),7)} : <#if useTotSolidsMap[productDetails.productName] =="N" || displayDed =="Y">${dedValue?string("##0.00")}<#else>0.00</#if></fo:block>
	        								</#if>	
	        							</#if>
	        						</#list>                       				
                       			</fo:table-cell>
                       			<fo:table-cell></fo:table-cell>                       			
                       			<fo:table-cell>                       				
	        						<#list dedTypes as adjType>
	        							<#if (adjType.getKey() > 7) && (adjType.getKey() < 11)>
	        								<#if deductionsMap?has_content>
	        									<#assign dedEntry = deductionsMap.entrySet()>
		        								<#assign dedVal =0>    
		        								<#list dedEntry as deduction>
		    										<#if ((adjType.getValue()).orderAdjustmentTypeId) == deduction.getKey()>
		    											<#assign dedVal = deduction.getValue()>
		    										</#if>
		    										<#assign dedEntry =0>
	        									</#list>
	        									<fo:block text-align="left" keep-together="always" white-space-collapse="false">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((adjType.getValue()).description?if_exists)),7)} : <#if useTotSolidsMap[productDetails.productName] =="N" || displayDed =="Y">${dedVal?string("##0.00")}<#else>0.00</#if></fo:block>
	        								</#if>
	        							</#if>
	        						</#list>                       				
                       			</fo:table-cell>                       			
                       			<fo:table-cell></fo:table-cell>
                       			<fo:table-cell>
                       				<fo:block keep-together="always" white-space-collapse="false" text-aling="left">TOT.AMOUNT   :</fo:block>
                       				<fo:block keep-together="always" white-space-collapse="false" text-aling="left">TOT.DEDCTN   :</fo:block>
                       				<fo:block keep-together="always" white-space-collapse="false" text-aling="left">NET AMOUNT   :</fo:block>
                       				<#assign ltrCost =(Static["java.math.BigDecimal"].ZERO)>	
                       				<#if GrTotEntries.get("qtyKgs") !=0>
                       					<#assign ltrCost = (((GrTotEntries.get("price")+GrTotEntries.get("sPrice")))/(GrTotEntries.get("qtyKgs")+(GrTotEntries.get("sQtyLtrs")*1.03)))>                       					
                       				</#if>
                       				<#assign ltrAmount = Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateLtrAmt(ltrCost,(GrTotEntries.get("qtyLtrs")+(GrTotEntries.get("sQtyLtrs"))))>
                       				<#assign ltrCostValue = ltrCost?string("##0.00")>
                       				<fo:block keep-together="always" white-space-collapse="false" text-aling="left">AVERAGE.RATE  :</fo:block>
                       			</fo:table-cell>
                       			<fo:table-cell></fo:table-cell>
                       			<fo:table-cell>
                       				<fo:block keep-together="always" white-space-collapse="false" text-align="right"><#if useTotSolidsMap[productDetails.productName] =="N" || displayDed =="Y" >${(((GrTotEntries.get("price")+GrTotEntries.get("sPrice"))+(additions+cartage)))?if_exists?string("##0.00")}<#else>${(GrTotEntries.get("price")+GrTotEntries.get("sPrice")+cartage)?if_exists?string("##0.00")}</#if></fo:block>
                       				<fo:block keep-together="always" white-space-collapse="false" text-align="right"><#if useTotSolidsMap[productDetails.productName] =="N" || displayDed =="Y">${(deductions)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
                       				<fo:block keep-together="always" white-space-collapse="false" text-align="right"><#if useTotSolidsMap[productDetails.productName] =="N" || displayDed =="Y">${(((GrTotEntries.get("price")+GrTotEntries.get("sPrice"))+(additions+cartage))-(deductions))?if_exists?string("##0.00")}<#else>${(GrTotEntries.get("price")+GrTotEntries.get("sPrice")+cartage)?if_exists?string("##0.00")}</#if></fo:block>
                       				<fo:block keep-together="always" white-space-collapse="false" text-align="right"> ${ltrCostValue}</fo:block>
                       			</fo:table-cell>                       			
                       			<fo:table-cell>
                       				<#assign kgsAmt=Static["java.lang.Math"].round(GrTotEntries.get("price")+GrTotEntries.get("sPrice"))>
                       				<#assign ltrsAmt = Static["java.lang.Math"].round(ltrAmount)>
                       				<fo:block keep-together="always" white-space-collapse="false" text-align="left" text-indent="15pt">KGS-AMT  :</fo:block>
                       				<fo:block keep-together="always" white-space-collapse="false" text-align="left" text-indent="15pt">LTS-AMT  :</fo:block>
                       				<fo:block keep-together="always" white-space-collapse="false" text-align="left" text-indent="15pt">.  </fo:block>
                       				<fo:block keep-together="always" white-space-collapse="false" text-align="left" text-indent="15pt">COMN-AMT :</fo:block>
                       			</fo:table-cell>
                       			<fo:table-cell>
                       				<fo:block keep-together="always" white-space-collapse="false" text-align="right">${(kgsAmt)?string("##0.00")}</fo:block>
                       				<fo:block keep-together="always" white-space-collapse="false" text-align="right">${(ltrsAmt)?string("##0.00")}</fo:block>
                       				<fo:block keep-together="always" white-space-collapse="false" text-align="right">-----------</fo:block>
                       				<fo:block keep-together="always" white-space-collapse="false" text-align="right">${(kgsAmt-ltrsAmt)?string("##0.00")}</fo:block>
                       			</fo:table-cell>
                    			</fo:table-row>
                    		</fo:table-body>					
						</fo:table>
					</fo:block>
					</#list>
            	</fo:flow>		
			</fo:page-sequence>
				<#else>
					<#assign displayDed = "Y">
			  </#if>	
			</#list>
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