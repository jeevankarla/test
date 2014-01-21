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

<#-- do not display columns associated with values specified in the request, ie constraint values -->
<fo:layout-master-set>
	<fo:simple-page-master master-name="main" page-height="12in" page-width="9in"  margin-bottom="1in" margin-left=".3in" margin-right="1in">
        <fo:region-body margin-top=".7in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "Ex-Duty.txt")}
<#assign totalSalesBasicPrice = 0>
<#assign grandTotalBed = 0>
<#assign grandTotalBedCess = 0>
<#assign grandTotalSecCess = 0>
<#assign finalGrandTotalEx = 0>
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
					<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false">&#160;    ${uiLabelMap.aavinDairyMsg}</fo:block>
					<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false">&#160;     MARKETING UNIT: METRO PRODUCTS: NANDANAM: CHENNAI=35</fo:block>				
              		<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false">&#160;    EX-DUTY FOR THE MONTH OF: ${parameters.customTimePeriodId}             ${uiLabelMap.CommonPage}:<fo:page-number/></fo:block>
            		<fo:block font-size="7pt">-----------------------------------------------------------------------------</fo:block>
              		<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">DATE          TOTAL VALUE        EXDUTY      EDC       HSC    TOTAL EXD</fo:block>
              		<fo:block font-size="7pt">-----------------------------------------------------------------------------</fo:block>
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
                 	<fo:table border-width="1pt" border-style="dotted">
                    <fo:table-column column-width="70pt"/>
                    <fo:table-column column-width="70pt"/>
                    <fo:table-column column-width="45pt"/> 
               	    <fo:table-column column-width="37pt"/>
            		<fo:table-column column-width="37pt"/> 		
            		<fo:table-column column-width="50pt"/>
                    	<fo:table-body>
                    		<#assign salesBasicPrice = 0>
                    		<#assign totalBed = 0>
		                    <#assign totalBedCess = 0>
		                    <#assign totalSecCess = 0>
		                    <#assign grandTotalEx = 0>
                    		<#assign dayWiseCategorySales = dayWiseCategorySalesMap.entrySet()>
                    		<#list dayWiseCategorySales as dayWiseProdSales>
                    			<#assign dayWisesaleDetails = dayWiseProdSales.getValue()>
                    			<fo:table-row>
                    				<fo:table-cell>
                            			<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">SUPPLY CHANNEL :</fo:block>        
                        			</fo:table-cell>
               	     				<fo:table-cell>
                            			<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">${dayWiseProdSales.getKey()}</fo:block>        
                        			</fo:table-cell>
                				</fo:table-row>
                				<fo:table-row>
                    				<fo:table-cell>
                            			<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">============================================================================</fo:block>        
                        			</fo:table-cell>
                				</fo:table-row>
                				<#assign dayWiseSales = dayWisesaleDetails.entrySet()>
                				<#list dayWiseSales as saleDetails>
                					<#assign sales = saleDetails.getValue()>
                					
                					<#if dayWiseProdSales.getKey() == "Gulabjmun/Chocolate">
		                        				<#assign salesBasicPrice = sales.get("basicPrice")*0.70>
		                        			<#else>	
		                        				<#assign salesBasicPrice = sales.get("basicPrice")>
		                        			</#if>
	                    			<#if saleDetails.getKey() != "totalSales">
	                    				<fo:table-row>
		                    				<fo:table-cell>
		                            			<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">${saleDetails.getKey()}</fo:block>        
		                        			</fo:table-cell>
		                        			
		                        			<fo:table-cell>
		                            			<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">${salesBasicPrice?string("#0.00")}</fo:block>        
		                        			</fo:table-cell>
		                        			<#assign bedPer = sales.get("exDutyPrecent")>
		                    				<#assign bedCessPer = sales.get("edCessPrecent")>
		                    				<#assign secCessPer = sales.get("higherSecCessPer")>
		                        			<#assign bedPrecent = (salesBasicPrice*bedPer)/100>
		                        			<#assign bedCessPrecent = (bedPrecent*bedCessPer)/100>
		                        			<#assign secCessPrecent = (bedPrecent*secCessPer)/100>
		                        			<#assign totalExDuty = bedPrecent + bedCessPrecent + secCessPrecent>
		                        			
		                        			<#assign totalBed = bedPrecent + totalBed>
		                        			<#assign totalBedCess = bedCessPrecent + totalBedCess>
		                        			<#assign totalSecCess = secCessPrecent + totalSecCess>
		                        			<#assign grandTotalEx = totalExDuty + grandTotalEx>
		                        			
		                        			<#assign totalSalesBasicPrice = salesBasicPrice + totalSalesBasicPrice>
											<#assign grandTotalBed = bedPrecent + grandTotalBed>
											<#assign grandTotalBedCess = bedCessPrecent + grandTotalBedCess>
											<#assign grandTotalSecCess = secCessPrecent + grandTotalSecCess>
											<#assign finalGrandTotalEx = totalExDuty + finalGrandTotalEx>
		                        			
		                        			<fo:table-cell>
		                            			<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">${bedPrecent?string("#0.00")}</fo:block>        
		                        			</fo:table-cell>
		                        			<fo:table-cell>
		                            			<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">${bedCessPrecent?string("#0.00")}</fo:block>        
		                        			</fo:table-cell>
		                        			<fo:table-cell>
		                            			<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">${secCessPrecent?string("#0.00")}</fo:block>        
		                        			</fo:table-cell>
		                        			<fo:table-cell>
		                            			<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">${totalExDuty?string("#0.00")}</fo:block>        
		                        			</fo:table-cell>
		                				</fo:table-row>
	                				</#if>
	                				<#if saleDetails.getKey() == "totalSales">
                						<fo:table-row>
	                						<fo:table-cell>
	                        					<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">=============================================================================</fo:block>        
	                    					</fo:table-cell>
	                    				</fo:table-row>
	                    				<fo:table-row>
	                    					<fo:table-cell>
	                            				<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">TOTAL SALES:</fo:block>        
	                        				</fo:table-cell>
	                        				<fo:table-cell>
		                            			<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">${salesBasicPrice?string("#0.00")}</fo:block>        
		                        			</fo:table-cell>
		                        			<fo:table-cell>
		                            			<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">${totalBed?string("#0.00")}</fo:block>        
		                        			</fo:table-cell>
		                        			<fo:table-cell>
		                            			<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">${totalBedCess?string("#0.00")}</fo:block>        
		                        			</fo:table-cell>
		                        			<fo:table-cell>
		                            			<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">${totalSecCess?string("#0.00")}</fo:block>        
		                        			</fo:table-cell>
		                        			<fo:table-cell>
		                            			<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">${grandTotalEx?string("#0.00")}</fo:block>        
		                        			</fo:table-cell>
	                        			</fo:table-row>	
	                        			<fo:table-row>
                    						<fo:table-cell>
                            					<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">=============================================================================</fo:block>        
                        					</fo:table-cell>
                						</fo:table-row>
                						<#assign salesBasicPrice = 0>
                    					<#assign totalBed = 0>
		                   				<#assign totalBedCess = 0>
		                    			<#assign totalSecCess = 0>
		                    			<#assign grandTotalEx = 0>
                					</#if>
                				</#list>
                    		</#list>
                    		<fo:table-row>
        						<fo:table-cell>
                					<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">=============================================================================</fo:block>        
            					</fo:table-cell>
            				</fo:table-row>
            				<fo:table-row>
            					<fo:table-cell>
                    				<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">GRAND TOTALS:</fo:block>        
                				</fo:table-cell>
                				<fo:table-cell>
                        			<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">${totalSalesBasicPrice?string("#0.00")}</fo:block>        
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">${grandTotalBed?string("#0.00")}</fo:block>        
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">${grandTotalBedCess?string("#0.00")}</fo:block>        
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">${grandTotalSecCess?string("#0.00")}</fo:block>        
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">${finalGrandTotalEx?string("#0.00")}</fo:block>        
                    			</fo:table-cell>
                			</fo:table-row>	
                			<fo:table-row>
        						<fo:table-cell>
                					<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">=============================================================================</fo:block>        
            					</fo:table-cell>
    						</fo:table-row>
                    	</fo:table-body>
                	</fo:table>
               	</fo:block> 		
			 </fo:flow>
		 </fo:page-sequence>	
</fo:root>
</#escape>