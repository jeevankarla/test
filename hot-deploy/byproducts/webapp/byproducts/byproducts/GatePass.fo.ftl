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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-bottom="1in" margin-left=".3in" margin-right="1in">
        <fo:region-body margin-top=".4in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
<#if parameters.reportTypeFlag =="CD-15_UNION">
	${setRequestAttribute("OUTPUT_FILENAME", "CD-15-UNION.txt")}
<#else>
	${setRequestAttribute("OUTPUT_FILENAME", "CD-15-DAIRY.txt")}
</#if>	
<#assign lineNumber = 5>
<#assign numberOfLines = 60>
<#assign facilityNumberInPage = 0>
<fo:page-sequence master-reference="main" force-page-count="no-force">					
			<fo:static-content flow-name="xsl-region-before">
			 <#assign lineNumber = 5> 
				<#assign facilityNumberInPage = 0>
					<fo:block text-align="left" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">&#160;                                              ${uiLabelMap.aavinDairyMsg}</fo:block>				
              		<fo:block text-align="left" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">&#160;                                              ${uiLabelMap.CommonPage}:<fo:page-number/>  MILK PRODUCTS CD-15 STATEMENT   ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(estimatedDeliveryDate, "dd/MM/yyyy")}</fo:block>
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
            		<#assign routeList = routeMap.get("routeList")>
            		<#assign grandTotals = routeMap.get("grandTotals")>
            		<#assign productTotalsList = grandTotals.get("productList")>
                 	<fo:table border-width="1pt" border-style="dotted">
                    <fo:table-column column-width="20pt"/>
                    <fo:table-column column-width="22pt"/> 
               	    <fo:table-column column-width="45pt"/>
               	    <#list activeRouteList as eachRoute>
            			<fo:table-column column-width="30pt"/> 		
            		</#list>
            		<fo:table-column column-width="28pt"/>
            		<fo:table-column column-width="45pt"/>
            		<fo:table-column column-width="20pt"/>
                     
		          	<fo:table-header>
		            	<fo:table-cell><fo:block keep-together="always" font-size="7pt"  text-align="left" white-space-collapse="false">SNO</fo:block></fo:table-cell>		                    	                  
		            	<fo:table-cell><fo:block keep-together="always" font-size="7pt"  text-align="left" white-space-collapse="false">PCD</fo:block></fo:table-cell>		                    	                  		            
		            	<fo:table-cell ><fo:block keep-together="always" font-size="7pt" text-align="left" white-space-collapse="false">PRODUCT_NAME</fo:block></fo:table-cell>
            			<#list activeRouteList as eachRoute>
            				<fo:table-cell ><fo:block keep-together="always" font-size="7pt" text-align="right" white-space-collapse="false" >RT${eachRoute}</fo:block></fo:table-cell>
            			</#list>	
            			<fo:table-cell><fo:block keep-together="always"  font-size="7pt" text-align="right"  white-space-collapse="false">TOT</fo:block><fo:block keep-together="always"  font-size="7pt" text-align="right"  white-space-collapse="false">QTY</fo:block></fo:table-cell>		                    	                  
		            	<fo:table-cell><fo:block keep-together="always" font-size="7pt" text-align="center"   white-space-collapse="false">TOT</fo:block><fo:block keep-together="always"  font-size="7pt" text-align="right"  white-space-collapse="false">KG/LR/NOS</fo:block></fo:table-cell>		                    	                  		            
		            	<fo:table-cell ><fo:block keep-together="always" font-size="7pt" text-align="right"  white-space-collapse="false">RET</fo:block><fo:block keep-together="always"  font-size="7pt" text-align="right"  white-space-collapse="false">QTY</fo:block></fo:table-cell>	
            		   
				    </fo:table-header>
				    		           
                    <fo:table-body>
                    	<fo:table-row>
                   	     	<fo:table-cell>
	                            <fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">----------------------------------------------------------------------------------------------------------------------------------------</fo:block>        
	                        </fo:table-cell>
	                    </fo:table-row> 
                    	<#assign sNo = 1>
                    	<#list productList as product>
                    	<#assign productEnt = delegator.findOne("Product", {"productId" : product}, true)>
                    	<fo:table-row>
                    		<fo:table-cell>
	                            <fo:block text-align="left" keep-together="always" font-size="7pt"  white-space-collapse="false">${sNo}</fo:block>        
	                        </fo:table-cell> 
	                        <fo:table-cell>
	                            <fo:block text-align="left" keep-together="always" font-size="7pt"  white-space-collapse="false">${product}</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block text-align="left" keep-together="always" font-size="7pt"  white-space-collapse="false">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(productEnt.brandName)),12)}</fo:block>        
	                        </fo:table-cell>
	                        <#assign sNo = sNo + 1>
                    		<#list activeRouteList as eachRoute>
                    			<#assign hasValue = 0>
                    			<#list routeList as derivedRouteMap>
                    				<#if eachRoute == derivedRouteMap.get("route")>
                    					<#assign routeDetailList = derivedRouteMap.get("routeList")>
            							<#assign soldProductList = routeDetailList.get("productList")>
                    						<#list soldProductList as eachProduct>
                    							<#if product == eachProduct.get("productId")>
                    								<#assign hasValue = 1>
                    								<fo:table-cell>
	                            						<fo:block text-align="right" font-size="7pt" keep-together="always"  white-space-collapse="false">${eachProduct.get("quantity")}</fo:block>        
	                        						</fo:table-cell>
						                		</#if> 
                    						</#list>
                    				</#if>
                    			</#list>
                    			<#if hasValue == 0>
                    				<fo:table-cell>
	                            		<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false"></fo:block>        
	                        		</fo:table-cell>
                    			</#if>
                    		</#list>
                    		<#assign hasGrandTotal = 0>
                    		<#list productTotalsList as productTotals>
                    			<#if product == productTotals.get("productId")>
                    				<#assign hasGrandTotal = 1>
                    				<fo:table-cell>
	                            		<fo:block text-align="right" keep-together="always"  font-size="7pt"  white-space-collapse="false">${productTotals.get("quantity")}</fo:block>        
	                        		</fo:table-cell>
	                        		<fo:table-cell>
	                            		<fo:block text-align="right" keep-together="always"  font-size="7pt" white-space-collapse="false">${productTotals.get("incQty")?string("##0.000")}</fo:block>        
	                        		</fo:table-cell>
	                        		<fo:table-cell>
	                            		<fo:block text-align="right" keep-together="always" font-size="7pt"  white-space-collapse="false"></fo:block>        
	                        		</fo:table-cell>
						        </#if> 
                    		</#list>
                    		<#if hasGrandTotal == 0>
                    			<fo:table-cell>
	                            	<fo:block text-align="right" font-size="7pt"  white-space-collapse="false">0</fo:block>        
	                        	</fo:table-cell>
	                        	<fo:table-cell>
	                            	<fo:block text-align="right" font-size="7pt" white-space-collapse="false">0</fo:block>        
	                        	</fo:table-cell>
	                        	<fo:table-cell>
	                            	<fo:block text-align="right" font-size="7pt" white-space-collapse="false"></fo:block>        
	                        	</fo:table-cell>
	                        		
                    		</#if>
                    	</fo:table-row> 	
                    	</#list>
                   	 	<fo:table-row>
                   	     	<fo:table-cell>
	                            	<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false">----------------------------------------------------------------------------------------------------------------------------------------</fo:block>        
	                        </fo:table-cell>
	                    </fo:table-row>    	 
                    </fo:table-body>
                </fo:table>
               </fo:block> 		
			 </fo:flow>
			 </fo:page-sequence>	

				
</fo:root>
</#escape>