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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-left=".3in" margin-right=".3in" margin-top=".5in">
                <fo:region-body margin-top=".9in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
         ${setRequestAttribute("OUTPUT_FILENAME", "ChannelWise CheckList.txt")}
       <#if vatWiseProductTotals?has_content>        
		        
		        <fo:page-sequence master-reference="main" font-size="10pt">	
		        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
		        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">     </fo:block>
		        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">     ${uiLabelMap.KMFDairyHeader}</fo:block>
					    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">  ${uiLabelMap.KMFDairySubHeader}</fo:block>
                    	<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false">TIN ::                              CHANNELWISE CHECK LIST STATEMENT FOR THE MONTH :: ${parameters.customTimePeriodId}</fo:block>
              			<fo:block font-size="11pt">====================================================================================================================================================================</fo:block>
            			<fo:block text-align="left" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">&#160;SL   PROD   PRODUCT NAME    QUANTITY    RATE          VAT      VAT            &lt; &lt;  S A L E S  &gt; &gt;              &lt; &lt; P U R C H A S E S &gt; &gt;               COMMISSION</fo:block>
            			<fo:block text-align="left" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">&#160;NO   CODE                                             RATE      %    BASIC VALUE   VAT VALUE   TOTAL VALUE     BASIC VALUE   VAT VALUE   TOTAL VALUE      VALUE</fo:block>	 	 	  
		        		<fo:block font-size="11pt">====================================================================================================================================================================</fo:block>
		        	</fo:static-content>	        	
		        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
                 	<fo:table border-width="1pt" border-style="dotted">
                    <fo:table-column column-width="20pt"/>
                    <fo:table-column column-width="38pt"/> 
               	    <fo:table-column column-width="42pt"/>
            		<fo:table-column column-width="45pt"/> 	
            		<fo:table-column column-width="40pt"/>	
            		<fo:table-column column-width="52pt"/>
            		<fo:table-column column-width="55pt"/>
            		<fo:table-column column-width="50pt"/>
            		<fo:table-column column-width="58pt"/>
            		<fo:table-column column-width="50pt"/>
            		<fo:table-column column-width="50pt"/>
            		<fo:table-column column-width="72pt"/>
            		<fo:table-column column-width="52pt"/>
            		<fo:table-column column-width="52pt"/>
	                    <fo:table-body>
	                    	<#assign serialNo = 1>
	                    	<#assign periodTotals = vatWiseProductTotals.entrySet()>
	                    	<#list facilityCategoryList as facilityCategory>
                    			<fo:table-row>
                					<fo:table-cell>
                            			<fo:block font-size="7pt">PARTY</fo:block>
                        			</fo:table-cell>
                        			<fo:table-cell>
                            			<fo:block font-size="7pt">NAME ::</fo:block>
                        			</fo:table-cell>
                        			<fo:table-cell>
                            			<fo:block font-size="7pt">${facilityCategory}</fo:block>
                        			</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
                					<fo:table-cell>
                            			<fo:block font-size="7pt" align-text="left">====================================================================================================================================================================</fo:block>
                        			</fo:table-cell>
								</fo:table-row>
	                    		<#list vatList as vatTax>
		                    		<#list periodTotals as vatWiseTotals>
		                    			<#if vatWiseTotals.getKey() == vatTax>
		                    				<#assign vatWiseProdTot = vatWiseTotals.getValue().entrySet()>
		                    				<#list vatWiseProdTot as vatWiseDetails>
		                    					<#assign vatWiseSaleTotals = (vatWiseDetails.getValue()).get("sale")>
		                    					<#assign vatWiseCategoryTotals = vatWiseSaleTotals.get("CategoryWiseTotals")>
		                    					
		                    					<#assign vatWisePurchaseTotals = (vatWiseDetails.getValue()).get("purchase")>
		                    					<#assign vatWisePurCatTotals = vatWisePurchaseTotals.get("CategoryWiseTotals")>
		                    					
				                        		<#if (vatWiseCategoryTotals.get(facilityCategory)?has_content)>
				                        			<#assign productSaleDetails = vatWiseCategoryTotals.get(facilityCategory)>
				                        			
				                        			<#if vatWiseDetails.getKey() == "prodCategoryTotals">
				                        				<fo:table-row>	
				                        					<fo:table-cell>
						                            			<fo:block font-size="7pt" align-text="left">====================================================================================================================================================================</fo:block>
						                        			</fo:table-cell>
														</fo:table-row>
						                    			<fo:table-row>
						                					<fo:table-cell>
						                            			<fo:block font-size="7pt" keep-together="always" white-space-collapse="false">&lt; &lt; VAT WISE TOTAL(0% ,5% , 14.5%) &gt; &gt;</fo:block>
						                        			</fo:table-cell>
						                        			<fo:table-cell>
						                            			<fo:block font-size="7pt"></fo:block>
						                        			</fo:table-cell>
						                        			<fo:table-cell>
						                            			<fo:block font-size="7pt"></fo:block>
						                        			</fo:table-cell>
						                        			<fo:table-cell>
						                            			<fo:block font-size="7pt"></fo:block>
						                        			</fo:table-cell>
						                        			<fo:table-cell>
						                            			<fo:block font-size="7pt"></fo:block>
						                        			</fo:table-cell>
						                        			<fo:table-cell>
						                            			<fo:block font-size="7pt"></fo:block>
						                        			</fo:table-cell>
						                        			<fo:table-cell>
						                            			<fo:block font-size="7pt"></fo:block>
						                        			</fo:table-cell>
						                        			<fo:table-cell>
						                            			<fo:block font-size="7pt" text-align="right">${productSaleDetails.get("basicValue")?string("##0.00#")}</fo:block>
						                        			</fo:table-cell>
						                        			<fo:table-cell>
						                            			<fo:block font-size="7pt" text-align="right">${productSaleDetails.get("vatValue")?string("##0.00#")}</fo:block>
						                        			</fo:table-cell>
						                        			<fo:table-cell>
						                            			<fo:block font-size="7pt" text-align="right">${productSaleDetails.get("totalValue")}</fo:block>
						                        			</fo:table-cell>
						                        			<#if (vatWisePurCatTotals.get(facilityCategory)?has_content)>
						                        				<#assign purchaseDetails = vatWisePurCatTotals.get(facilityCategory)>
							                        			<fo:table-cell>
							                            			<fo:block font-size="7pt" text-align="right">${purchaseDetails.get("basicValue")}</fo:block>
							                        			</fo:table-cell>
							                        			<fo:table-cell>
							                            			<fo:block font-size="7pt" text-align="right">${purchaseDetails.get("vatValue")}</fo:block>
							                        			</fo:table-cell>
							                        			<fo:table-cell>
								                            		<fo:block font-size="7pt" text-align="right">${((purchaseDetails.get("basicValue")) + (purchaseDetails.get("vatValue")))?string("##0.00#")}</fo:block>
								                        		</fo:table-cell>
								                        		<fo:table-cell>
					                            					<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${((productSaleDetails).get("basicValue")+((productSaleDetails).get("vatValue")))  -  ((purchaseDetails).get("basicValue")+((purchaseDetails).get("vatValue")))}</fo:block>  
					                        					</fo:table-cell>
					                        				 <#else>
					                        				 	<fo:table-cell>
							                            			<fo:block font-size="7pt" text-align="right">0.00</fo:block>
							                        			</fo:table-cell>
							                        			<fo:table-cell>
							                            			<fo:block font-size="7pt" text-align="right">0.00</fo:block>
							                        			</fo:table-cell>
							                        			<fo:table-cell>
								                            		<fo:block font-size="7pt" text-align="right">0.00</fo:block>
								                        		</fo:table-cell>
								                        		<fo:table-cell>
					                            					<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">0.00</fo:block>  
					                        					</fo:table-cell>	
				                        					</#if>
						                        			
														</fo:table-row>
														<fo:table-row>
						                					<fo:table-cell>
						                            			<fo:block font-size="7pt" align-text="left">====================================================================================================================================================================</fo:block>
						                        			</fo:table-cell>
				                        				</fo:table-row>
				                        				<#else>
				                        				
						                        			<fo:table-row>
						                        				<fo:table-cell>
						                            				<fo:block font-size="7pt">${serialNo}</fo:block>
						                        				</fo:table-cell>
							                        			<fo:table-cell>
						                            				<fo:block font-size="7pt">${vatWiseDetails.getKey()}</fo:block>
						                        				</fo:table-cell>
						                        				<#assign productEnt = delegator.findOne("Product", {"productId" : vatWiseDetails.getKey()}, true)>
						                        				<fo:table-cell>
			                            							<fo:block text-align="left" keep-together="always" font-size="7pt"  white-space-collapse="false">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(productEnt.brandName)),12)}</fo:block>        
			                        							</fo:table-cell>
							                        			<fo:table-cell>
							                            			<fo:block font-size="7pt" text-align="right">${productSaleDetails.get("quantity")?string("#0")}</fo:block>
							                        			</fo:table-cell>
							                        			<fo:table-cell>
							                            			<fo:block font-size="7pt" text-align="right">${((productSaleDetails.get("basicValue"))/(productSaleDetails.get("quantity")))?string("##0.00#")}</fo:block>
							                        			</fo:table-cell>
							                        			<fo:table-cell>
							                            			<fo:block font-size="7pt" text-align="right">${((productSaleDetails.get("vatValue"))/(productSaleDetails.get("quantity")))?string("##0.00#")}</fo:block>
							                        			</fo:table-cell>
							                        			<fo:table-cell>
							                            			<fo:block font-size="7pt" text-align="right">${productSaleDetails.get("vatPercentage")?string("##0.00#")}</fo:block>
							                        			</fo:table-cell>
							                        			<fo:table-cell>
							                            			<fo:block font-size="7pt" text-align="right">${productSaleDetails.get("basicValue")?string("##0.00#")}</fo:block>
							                        			</fo:table-cell>
							                        			<fo:table-cell>
							                            			<fo:block font-size="7pt" text-align="right">${productSaleDetails.get("vatValue")?string("##0.00#")}</fo:block>
							                        			</fo:table-cell>
							                        			<fo:table-cell>
							                            			<fo:block font-size="7pt" text-align="right">${productSaleDetails.get("totalValue")?string("##0.00#")}</fo:block>
							                        			</fo:table-cell>
							                        			<#if (vatWisePurCatTotals.get(facilityCategory)?has_content)>
							                        				<#assign purchaseDetails = vatWisePurCatTotals.get(facilityCategory)>
								                        			<fo:table-cell>
								                            			<fo:block font-size="7pt" text-align="right">${purchaseDetails.get("basicValue")?string("##0.00#")}</fo:block>
								                        			</fo:table-cell>
								                        			<fo:table-cell>
								                            			<fo:block font-size="7pt" text-align="right">${purchaseDetails.get("vatValue")?string("##0.00#")}</fo:block>
								                        			</fo:table-cell>
								                        			<fo:table-cell>
								                            			<fo:block font-size="7pt" text-align="right">${((purchaseDetails.get("basicValue")) + (purchaseDetails.get("vatValue")))?string("##0.00#")}</fo:block>
								                        			</fo:table-cell>
								                        			<fo:table-cell>
					                            						<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${((productSaleDetails).get("basicValue")+((productSaleDetails).get("vatValue")))  -  ((purchaseDetails).get("basicValue")+((purchaseDetails).get("vatValue")))}</fo:block>  
					                        						</fo:table-cell>
					                        					 <#else>
						                        				 	<fo:table-cell>
								                            			<fo:block font-size="7pt" text-align="right">0.00</fo:block>
								                        			</fo:table-cell>
								                        			<fo:table-cell>
								                            			<fo:block font-size="7pt" text-align="right">0.00</fo:block>
								                        			</fo:table-cell>
								                        			<fo:table-cell>
									                            		<fo:block font-size="7pt" text-align="right">0.00</fo:block>
									                        		</fo:table-cell>
									                        		<fo:table-cell>
						                            					<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">0.00</fo:block>  
						                        					</fo:table-cell>	
				                        						</#if>
							                        			
						                        			</fo:table-row>
				                        					<#assign serialNo = serialNo + 1>
				                        			</#if>
				                        	   </#if>
											</#list>	
		                    			</#if>
									</#list>
								</#list>		
							</#list>			
	                    </fo:table-body>
                	</fo:table>
               </fo:block> 		
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