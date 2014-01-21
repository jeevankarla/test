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
         ${setRequestAttribute("OUTPUT_FILENAME", "VATAbstract.txt")}
       <#if periodTotalsMap?has_content>        
		        
		        <fo:page-sequence master-reference="main" font-size="10pt">	
		        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
		        		<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false">&#160;                                                ${uiLabelMap.aavinDairyMsg}</fo:block>
                    	<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false">&#160;                                              CORPORATE OFFICE , MARKETING UNIT , NANDANAM , CHENNAI - 35.</fo:block>
                    	<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false">TIN :: 33761080302                                  VALUE ADDED TAX ABSTRACT FOR THE MONTH :: ${parameters.customTimePeriodId}</fo:block>
              			<fo:block font-size="11pt">====================================================================================================================================================================</fo:block>
            			<fo:block text-align="left" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">&#160;                                                          &lt; &lt;  S A L E S  &gt; &gt;                   &lt;  &lt; P U R C H A S E S &gt; &gt;</fo:block>
            			<fo:block text-align="left" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">&#160;S NO P-CODE   PRODUCT NAME   VAT%   QUANTITY   BASIC VALUE   VAT VALUE    TOTAL VALUE    BASIC VALUE VAT VALUE   TOTAL VALUE       COMMISSION</fo:block>	 	 	  
		        		<fo:block font-size="11pt">====================================================================================================================================================================</fo:block>
		        	</fo:static-content>	        	
		        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
                 	<fo:table border-width="1pt" border-style="dotted">
                    <fo:table-column column-width="20pt"/>
                    <fo:table-column column-width="40pt"/> 
               	    <fo:table-column column-width="50pt"/>
            		<fo:table-column column-width="32pt"/> 	
            		<fo:table-column column-width="42pt"/>	
            		<fo:table-column column-width="60pt"/>
            		<fo:table-column column-width="55pt"/>
            		<fo:table-column column-width="58pt"/>
            		<fo:table-column column-width="58pt"/>
            		<fo:table-column column-width="50pt"/>
            		<fo:table-column column-width="58pt"/>
            		<fo:table-column column-width="70pt"/>
	                    <fo:table-body>
	                    	<#assign periodTotals = periodTotalsMap.entrySet()>
	                    	<#assign serialNo = 1>
	                    	<#list periodTotals as periodTotalsEntry>
	                    		<#assign periodProductTotals = periodTotalsEntry.getValue().entrySet()>
	                    		<#list periodProductTotals as periodProductTotalsEntry>
	                    			<#if periodProductTotalsEntry.getKey() == "prodCategoryTotals">
	                    				<#assign salesTotalsMap = (periodProductTotalsEntry.getValue()).get("sale")>
	                    				<#assign catTotals = (salesTotalsMap).get("totals")>
	                    				
	                    				<#assign purchaseTotalsMap = (periodProductTotalsEntry.getValue()).get("purchase")>
	                    				<#assign catPurchaseTotals = (purchaseTotalsMap).get("totals")>
	                    				<fo:table-row>
	                    					<fo:table-cell>
		                            			<fo:block font-size="7pt">====================================================================================================================================================================</fo:block>
		                        			</fo:table-cell>
										</fo:table-row>
										<fo:table-row>
	                    					<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">&lt;&lt; PRODUCTWISE TOTAL &gt;&gt; **</fo:block>  
		                        			</fo:table-cell>
				                        	<fo:table-cell>
				                            	<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false"></fo:block>  
				                        	</fo:table-cell>
				                        	<fo:table-cell>
				                            	<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false"></fo:block>  
				                        	</fo:table-cell>
				                        	<fo:table-cell>
				                            	<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false"></fo:block>  
				                        	</fo:table-cell>
				                        	<fo:table-cell>
				                            	<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${catTotals.get("catSaleQty")}</fo:block>  
				                        	</fo:table-cell>
				                        	<fo:table-cell>
				                            	<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${catTotals.get("catSalebasicValue")}</fo:block>  
				                        	</fo:table-cell>
				                        	<fo:table-cell>
				                            	<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${catTotals.get("catSaleVatValue")}</fo:block>  
				                        	</fo:table-cell>
				                        	<fo:table-cell>
				                            	<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${(catTotals.get("catSalebasicValue"))+((catTotals).get("catSaleVatValue"))}</fo:block>  
				                        	</fo:table-cell>
				                        	<fo:table-cell>
				                            	<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${catPurchaseTotals.get("catPurchasebasicValue")}</fo:block>  
				                        	</fo:table-cell>
				                        	<fo:table-cell>
				                            	<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${catPurchaseTotals.get("catPurchaseVatValue")}</fo:block>  
				                        	</fo:table-cell>
				                        	<fo:table-cell>
				                            	<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${(catPurchaseTotals.get("catPurchasebasicValue"))+(catPurchaseTotals.get("catPurchaseVatValue"))}</fo:block>  
				                        	</fo:table-cell>
				                        	<#if (  ((catTotals).get("catPurchasebasicValue") + (catTotals).get("catPurchaseVatValue")) == 0) >
				                        		<fo:table-cell>
				                            		<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">0.00</fo:block>  
				                        		</fo:table-cell>
				                        		<#else>
				                        		<fo:table-cell>
				                            		<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${((catTotals).get("catSalebasicValue")+((catTotals).get("catSaleVatValue")))  -  ((catPurchaseTotals).get("catPurchasebasicValue")+((catPurchaseTotals).get("catPurchaseVatValue")))}</fo:block>  
				                        		</fo:table-cell>
		                					</#if>
										</fo:table-row>
										<fo:table-row>
	                    					<fo:table-cell>
		                            			<fo:block font-size="7pt">====================================================================================================================================================================</fo:block>  
		                        			</fo:table-cell>
										</fo:table-row>
									<#else>
			                			<fo:table-row>
			                				<fo:table-cell>
			                            		<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${serialNo}</fo:block>  
			                        		</fo:table-cell>
		                    				<fo:table-cell>
			                            		<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${periodProductTotalsEntry.getKey()}</fo:block>  
			                        		</fo:table-cell>
			                        		<#assign productEnt = delegator.findOne("Product", {"productId" : periodProductTotalsEntry.getKey()}, true)>
			                        		<#assign salesMap = (periodProductTotalsEntry.getValue()).get("sale")>
			                        		<#assign salesTotals = (salesMap).get("totals")>
			                        		<fo:table-cell>
	                            				<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(productEnt.brandName)),12)}</fo:block>        
	                        				</fo:table-cell>
			                        		<fo:table-cell>
			                            		<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${salesTotals.get("vatPercentage")?string("#0.00")}</fo:block>  
			                        		</fo:table-cell>
			                        		<fo:table-cell>
			                           			<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${salesTotals.get("quantity")}</fo:block>  
			                        		</fo:table-cell>
			                        		<fo:table-cell>
			                            		<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${salesTotals.get("basicValue")?string("#0.00")}</fo:block>  
			                        		</fo:table-cell>
			                        		<fo:table-cell>
			                            		<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${salesTotals.get("vatValue")?string("#0.00")}</fo:block>  
			                        		</fo:table-cell>
			                        		<fo:table-cell>
			                            		<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${(salesTotals.get("basicValue"))+(salesTotals.get("vatValue"))}</fo:block>  
			                        		</fo:table-cell>
			                        		<#assign purchasesMap = (periodProductTotalsEntry.getValue()).get("purchase")>
			                        		<#assign purchaseTotals = (purchasesMap).get("totals")>
			                        		<fo:table-cell>
			                            		<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${purchaseTotals.get("basicValue")?string("#0.00")}</fo:block>  
			                        		</fo:table-cell>
			                        		<fo:table-cell>
			                          	  		<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${purchaseTotals.get("vatValue")?string("#0.00")}</fo:block>  
			                        		</fo:table-cell>
			                        		<fo:table-cell>
			                            		<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${(purchaseTotals.get("basicValue"))+(purchaseTotals.get("vatValue"))}</fo:block>  
			                        		</fo:table-cell>
			                        		<#if (  ((purchaseTotals.get("basicValue"))+(purchaseTotals.get("vatValue"))) == 0) >
				                        		<fo:table-cell>
				                            		<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">0.00</fo:block>  
				                        		</fo:table-cell>
				                        		<#else>
				                        		<fo:table-cell>
				                            		<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${((salesTotals.get("basicValue"))+(salesTotals.get("vatValue"))) - ((purchaseTotals.get("basicValue"))+(purchaseTotals.get("vatValue")))}</fo:block>  
				                        		</fo:table-cell>
		                					</#if>
		                				</fo:table-row>
	                					<#assign serialNo = serialNo+1>
									</#if> 
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