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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="15in">
        <fo:region-body margin-top="1in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
<#if reportTypeFlag?exists>
	<#if reportTypeFlag == "CD-UNION" || (reportTypeFlag == "CD-15_UNION")>
		${setRequestAttribute("OUTPUT_FILENAME", "CD15UnionProducts.txt")}
	<#elseif reportTypeFlag == "CD-DAIRY" || (reportTypeFlag == "CD-15_DAIRY")>
		${setRequestAttribute("OUTPUT_FILENAME", "CD15DairyProducts.txt")}
	</#if>
	<#else>
		${setRequestAttribute("OUTPUT_FILENAME", "TotalIndentReport.txt")}
	</#if>
<#assign lineNumber = 5>
<#assign numberOfLines = 60>
<#assign facilityNumberInPage = 0>
<fo:page-sequence master-reference="main" force-page-count="no-force">					
			<fo:static-content flow-name="xsl-region-before">
				<#assign facilityNumberInPage = 0>
					<fo:block text-align="left" font-size="7pt" white-space-collapse="false">&#160;                                                                           ${uiLabelMap.aavinDairyMsg}</fo:block>
					<fo:block text-align="left" font-size="7pt" white-space-collapse="false">&#160;                                                                           SMP GODOWN / AMBATTUR PRODUCT DAIRY, CHENNAI-98</fo:block>
					<fo:block linefeed-treatment="preserve" font-size="7pt" font-family="Courier,monospace">&#xA;</fo:block>
					<#if reportTypeFlag?exists>
					<#if reportTypeFlag == "CD-UNION" || (reportTypeFlag == "CD-15_UNION")>
						<fo:block keep-together="always" font-size="7pt" font-family="Courier,monospace" white-space-collapse="false"> ${uiLabelMap.CommonPage}:<fo:page-number/>                             MILK PRODUCTS CD-15 UNION STATEMENT FOR MONTH : ${parameters.customTimePeriodId} </fo:block>
					<#elseif reportTypeFlag == "CD-DAIRY" || (reportTypeFlag == "CD-15_DAIRY")>
						<fo:block keep-together="always" font-size="7pt" font-family="Courier,monospace" white-space-collapse="false"> ${uiLabelMap.CommonPage}:<fo:page-number/>                             MILK PRODUCTS CD-15 DAIRY STATEMENT FOR MONTH : ${parameters.customTimePeriodId} </fo:block>
					</#if>
					<#else>
						<fo:block keep-together="always" font-size="7pt" font-family="Courier,monospace" white-space-collapse="false"> ${uiLabelMap.CommonPage}:<fo:page-number/>                             MILK PRODUCTS TOTAL INDENT STATEMENT FOR MONTH : ${parameters.customTimePeriodId} </fo:block>
					</#if>
					<fo:block linefeed-treatment="preserve" font-size="7pt" font-family="Courier,monospace">&#xA;</fo:block>
					<fo:block keep-together="always" font-size="7pt" font-family="Courier,monospace" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
					<fo:block keep-together="always" font-size="7pt" font-family="Courier,monospace" white-space-collapse="false">SNO PCD PRODUCT NAME           RT1   RT2    RT3    RT4   RT5    RT6    RT7   RT8    RT9   RT10  RT11   RT12   RT13  RT14    OTH    TOT    TOT     RETN</fo:block>
					<fo:block keep-together="always" font-size="7pt" font-family="Courier,monospace" white-space-collapse="false">&#160;                                                                                                                                  QTY KG/LR/NOS   QTY</fo:block>
					<fo:block keep-together="always" font-size="7pt" font-family="Courier,monospace" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">		
            	<fo:block>																						 
                 	<fo:table border-width="1pt" border-style="dotted">
                    <fo:table-column column-width="20pt"/>
                    <fo:table-column column-width="23pt"/>  
               	    <fo:table-column column-width="70pt"/>
               	    <#list routesHeader as eachRoute>
            			<fo:table-column column-width="28pt"/> 		
            		</#list>
            		<fo:table-column column-width="30pt"/>
            		<fo:table-column column-width="45pt"/>
            		<fo:table-column column-width="24pt"/>
                    <fo:table-body>
                    	<#assign serialNo = 1/>
                    	<#assign productWiseRouteWiseSales = productMap.entrySet()>
                    	<#list productWiseRouteWiseSales as productWiseSalesEntry>
	                    	<fo:table-row>
	                    		<fo:table-cell>
		                            <fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false">${serialNo}</fo:block>        
		                        </fo:table-cell>
	                    		<fo:table-cell>
		                            <fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false">${productWiseSalesEntry.getKey()}</fo:block>        
		                        </fo:table-cell> 
		                        <fo:table-cell>
		                            <fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(prodNameMap.get(productWiseSalesEntry.getKey()))),12)}</fo:block>        
		                        </fo:table-cell>
		                        <#list routesHeader as eachRoute>
		                        	<#if (((productWiseSalesEntry.getValue()).get(eachRoute))?has_content)>
	            						<fo:table-cell>
			                            	<fo:block text-align="right" font-size="7pt" keep-together="always" white-space-collapse="false">${(productWiseSalesEntry.getValue()).get(eachRoute)}</fo:block>        
			                        	</fo:table-cell> 
			                         <#else>
	                        			<fo:table-cell>
	                            			<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false"></fo:block>  
	                        			</fo:table-cell>
	                        		</#if>		
            					</#list>
            					<#assign prodTotalSaleDetails = productWiseTotalsMap.get(productWiseSalesEntry.getKey())/>
            					<fo:table-cell>
		                            <fo:block text-align="right" font-size="7pt" keep-together="always" white-space-collapse="false">${prodTotalSaleDetails.get("quantity")}</fo:block>        
		                        </fo:table-cell> 
		                        <fo:table-cell>
		                            <fo:block text-align="right" font-size="7pt" keep-together="always" white-space-collapse="false">${prodTotalSaleDetails.get("qtyInc")?string("#0.00")}</fo:block>        
		                        </fo:table-cell>
	                    	</fo:table-row> 
	                    	<#assign serialNo = serialNo + 1/>
	                    	<#assign lineNumber = lineNumber + 1>
                    		<#if (lineNumber >= numberOfLines)>
                    			<#assign lineNumber = 5>
                    			<fo:table-row>
                   	     			<fo:table-cell>
	                            		<fo:block font-size="7pt" page-break-after="always"></fo:block>        
	                        		</fo:table-cell>
	                    		</fo:table-row>
                    		</#if>
                    	</#list>
                    </fo:table-body>
                </fo:table>
               </fo:block> 	
               <fo:block keep-together="always" font-size="7pt" font-family="Courier,monospace" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>	
               <fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
               <fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false">&#160;                                                          				MATERIALS UTILISE				</fo:block>
               <fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false">&#160;                                                 		        			-----------------				</fo:block>
               <fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
               <fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false">TOTAL NO OF SPOONS - WOODEN  ::</fo:block>
               <fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false">&#160;                  - PLASTIC ::                               TOTAL NO OF PAPER PLATES ::                                DRY ICE IN KGS  :: </fo:block>
               <fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
               <fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
               <fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false">DY.MANAGER (MARKETING) 	 			        				          		     MANAGER / DY.MANAGER (DAIRYING)/AGM (DAIRY)                		  DY.GENERAL MANAGER(ENGG)</fo:block>
			
			</fo:flow>
	</fo:page-sequence>	
</fo:root>
</#escape>