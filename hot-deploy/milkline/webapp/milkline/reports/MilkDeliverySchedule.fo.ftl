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
	<#macro header routeId>   
	${setRequestAttribute("OUTPUT_FILENAME", "mlksch.txt")}  
	<#assign  CollectionSheetType =parameters.reportTypeFlag>
	<#assign shipment = delegator.findOne("Shipment", {"shipmentId" : parameters.shipmentId}, true)>
    <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">.                                 SUPRAJA DAIRY PRIVATE LIMITED</fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false">.                                 MILK DELEVERY SCHEDULE : VISAKHAPATNAM </fo:block>				
				<fo:block text-align="left" keep-together="always" white-space-collapse="false">Route No:${routeId}      Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(estimatedShipDate, "dd/MM/yyyy")}   <#if shipment.shipmentTypeId.startsWith("AM")>MORNING.<#else>EVENING.</#if> </fo:block> 		
            	<fo:block font-size="8pt">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
        		<fo:block font-family="Courier,monospace" font-size="10pt">                
                <fo:table width="100%" table-layout="fixed" space-after="0.0in">
                    <fo:table-column column-width="200pt"/>
                    <fo:table-column column-width="30pt"/>               
	            	<#list productList as product>            
	            	  <fo:table-column column-width="50pt"/>      
	            	</#list>
           			<fo:table-column column-width="45pt"/>
           			<fo:table-column column-width="45pt"/>
           			<fo:table-column column-width="45pt"/>
           			<fo:table-column column-width="45pt"/>
            		<fo:table-column column-width="60pt"/>
            		<fo:table-column column-width="60pt"/>                    		          
		            <fo:table-header>
		            	<fo:table-cell ><fo:block text-align="left" keep-together="always">BoothNo. AgentName</fo:block></fo:table-cell>
		            	<fo:table-cell></fo:table-cell>	            		
                       <#list productList as product>                       		
                       		<fo:table-cell>
                       			<fo:block keep-together="always" white-space-collapse="false">${product.brandName?if_exists}</fo:block>
                       		</fo:table-cell>                       		
                       	</#list>
                       	<#if CollectionSheetType=="RouteCollectionSheet">
                       	<fo:table-cell></fo:table-cell>
                       	<fo:table-cell></fo:table-cell>
                       	<fo:table-cell></fo:table-cell>
	                    <fo:table-cell><fo:block text-align="center" text-indent="13pt">Value</fo:block></fo:table-cell>
	                    <fo:table-cell><fo:block text-align="left" text-indent="15pt">Pre.Due</fo:block></fo:table-cell>
	                    <fo:table-cell><fo:block text-align="left" text-indent="20pt">TOT.Amt</fo:block></fo:table-cell>
	                    </#if>
	                </fo:table-header>
	                <fo:table-body>
	               	  <fo:table-row>
                			<fo:table-cell><fo:block>Products</fo:block></fo:table-cell>
                			<fo:table-cell></fo:table-cell>
                			<#list productValuesList as allProducts>
                       		<fo:table-cell>
                       			<fo:block keep-together="always" white-space-collapse="false">${allProducts.brandName}</fo:block>
                      		</fo:table-cell>
                      		</#list>	
	                	</fo:table-row>
	                </fo:table-body>
	        	</fo:table>
	        </fo:block>	
	        <fo:block font-size="8pt">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
        </fo:static-content>
     <fo:flow flow-name="xsl-region-body" font-family="Helvetica">  
  </#macro>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-left=".3in">
                <fo:region-body margin-top="1.5in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        <#if scheduleListReportMap?has_content>
        <#assign lineNumber = 0>
        <#assign productQty = 0>
        <#assign totalValue =0/>
		<#assign preDueTotal =0/>
		<#assign amtTotals =0/>
		<#assign routeTotalValue =0/>
		<#assign routePreDueTotal =0/>
		<#assign routeAmtTotals =0/>
        <#assign facilityDetails = delegator.findOne("Facility", {"facilityId" :facilityId}, false)>
        	<#assign prevRouteId = facilityDetails.parentFacilityId>
        	<@header routeId=prevRouteId />
       		<#assign scheduleListReport = scheduleListReportMap.entrySet()>
        <#if scheduleListReport?has_content>
        	<#list scheduleListReport as scheduleList>
        		<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : scheduleList.getKey()}, true)>
	        	<#assign currentRouteId = facilityDetails.parentFacilityId>
	        	<#if prevRouteId != currentRouteId>	        	    
	        	    <#assign routeTotalsEntries = routeWiseProdTotals.entrySet()>
	        	    <#list routeTotalsEntries as routeTotalsEntry> 
	        	        <#if routeTotalsEntry.getKey() == prevRouteId >
	        	        <#assign tempRouteProdTotals = routeTotalsEntry.getValue()>
	        	        <#assign routeProductTotals = tempRouteProdTotals.get("CASH").entrySet()>
	        	        <fo:block keep-together="always" white-space-collapse="preserve" font-size="10pt">
                	<fo:table width="100%" table-layout="fixed" space-after="0.0in">
                    	<fo:table-column column-width="70pt"/>
                    	<fo:table-column column-width="50pt"/>
                    	<fo:table-column column-width="60pt"/>
                    	<fo:table-column column-width="60pt"/>
                    	<fo:table-column column-width="60pt"/>
                    	<fo:table-column column-width="50pt"/>
                    	<fo:table-column column-width="50pt"/>
                    	<fo:table-column column-width="50pt"/>
                    	<fo:table-column column-width="50pt"/>
                    	<fo:table-column column-width="50pt"/>
                    	<fo:table-column column-width="50pt"/>
                    	<fo:table-column column-width="50pt"/>
                    	<fo:table-column column-width="50pt"/>
                    	<fo:table-column column-width="50pt"/>
                    	<fo:table-column column-width="50pt"/>
                    	<fo:table-column column-width="50pt"/>
                    	<fo:table-column column-width="60pt"/>                    	
                    	<#list routeProductTotals as routeProductValues>
                    	<fo:table-column column-width="50pt"/>
                    	</#list>
                    	<fo:table-body>
                    		<fo:table-row>
                    		    <#assign lineNumber = lineNumber + 1>
                    			<fo:table-cell>
                    				<fo:block>Cash Totals:</fo:block>
                    			</fo:table-cell>
                                <fo:table-cell>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			</fo:table-cell>
                    			<#list routeProductTotals as routeProductValues>                    				
                    			<fo:table-cell>
                    				<#list productList as product>
                    				<#if product.productId == routeProductValues.getKey()>
                    				<fo:block keep-together="always" text-align="right">${routeProductValues.getValue()}</fo:block>
                    				</#if>
                    				</#list>
                    			</fo:table-cell>
                    			</#list>
                    			<fo:table-cell>
                    				<fo:block keep-together="always" text-align="right"></fo:block>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    				<fo:block keep-together="always" text-align="right"></fo:block>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    				<fo:block keep-together="always" text-align="right"></fo:block>
                    			</fo:table-cell>
                    		</fo:table-row>
                    		<fo:table-row>
                    		    <#assign lineNumber = lineNumber + 1>
                    			<fo:table-cell>
                    				<fo:block>Card Totals:</fo:block>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			</fo:table-cell>
                    			<#assign routeProductCardTotals = tempRouteProdTotals.get("CARD").entrySet()>
                    			<#list productList as product>
                    			<fo:table-cell>
                    				<#list routeProductCardTotals as routeCardTotals>
	                    				<#if product.productId == routeCardTotals.getKey()>
	                    					<fo:block keep-together="always" text-align="right">${routeCardTotals.getValue()}</fo:block>
	                    				</#if>
	                    			</#list>	
                    			</fo:table-cell>
                    			</#list>
                    			<fo:table-cell>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			</fo:table-cell>
                    		</fo:table-row>
                    		<fo:table-row>
                    		    <#assign lineNumber = lineNumber + 1>
                    			<fo:table-cell>
                    				<fo:block>Route Totals:</fo:block>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			</fo:table-cell>
                    			<#assign routeCashCardTotals = tempRouteProdTotals.get("CASH_CARD").entrySet()>
                    			<#list productList as product>
                    			<fo:table-cell>
                    				<#list routeCashCardTotals as CashCardTotals>
                    					<#if product.productId == CashCardTotals.getKey()>
                    						<fo:block keep-together="always" text-align="right">${CashCardTotals.getValue()}</fo:block>
                    					</#if>	
                    				</#list>
                    			</fo:table-cell>
                    			</#list>
                    			 <#if CollectionSheetType=="RouteCollectionSheet">
                    		     <fo:table-cell></fo:table-cell>
                    			<fo:table-cell></fo:table-cell>
                    			<fo:table-cell></fo:table-cell> 
                    			<fo:table-cell>
                    				<fo:block keep-together="always" text-indent="30pt" text-align="left">${routeTotalValue?if_exists}</fo:block>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    				<fo:block keep-together="always" text-indent="30pt" text-align="left">${routePreDueTotal?if_exists}</fo:block>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    				<fo:block keep-together="always" text-indent="30pt" text-align="left">${routeAmtTotals?if_exists}</fo:block>
                    			</fo:table-cell>
                    			<#else>
                    			
                    		     <fo:table-cell></fo:table-cell>
                    			<fo:table-cell></fo:table-cell>
                    			<fo:table-cell></fo:table-cell> 
                    			<fo:table-cell>
                    				<fo:block keep-together="always" text-indent="30pt" text-align="left"></fo:block>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    				<fo:block keep-together="always" text-indent="30pt" text-align="left"></fo:block>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    				<fo:block keep-together="always" text-indent="30pt" text-align="left"></fo:block>
                    			</fo:table-cell>
                    			
                    			
                    			</#if>
                    		</fo:table-row>
                    		<fo:table-row>
                    			<#assign lineNumber = lineNumber + 1>
                    			<fo:table-cell>
                    				<fo:block>PRODUCT TOTALS</fo:block>
                    			</fo:table-cell>
                    		</fo:table-row>
                    		<fo:table-row>                    		    
                    			<fo:table-cell>
                    				<fo:block>Cash Totals:</fo:block>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			</fo:table-cell>
                    			<#list productValuesList as product>                    			                  				
                    			<fo:table-cell>
                    				<#list routeProductTotals as routeProductValues>  
	                    				<#if product.productId == routeProductValues.getKey()>
	                    					<fo:block keep-together="always" text-align="right">${routeProductValues.getValue()}</fo:block>
	                    				</#if>
                    				</#list>
                    			</fo:table-cell>
                    			</#list>
                    			<fo:table-cell>
                    				<fo:block keep-together="always" text-align="right"></fo:block>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    				<fo:block keep-together="always" text-align="right"></fo:block>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    				<fo:block keep-together="always" text-align="right"></fo:block>
                    			</fo:table-cell>
                    		</fo:table-row>
                    		<fo:table-row>
                    		    <#assign lineNumber = lineNumber + 1>
                    			<fo:table-cell>
                    				<fo:block>Card Totals:</fo:block>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			</fo:table-cell>
                    			<#assign routeProductCardTotals = tempRouteProdTotals.get("CARD").entrySet()>
                    			<#list productValuesList as product>
                    			<fo:table-cell>
                    				<#list routeProductCardTotals as routeCardTotals>
	                    				<#if product.productId == routeCardTotals.getKey()>
	                    					<fo:block keep-together="always" text-align="right">${routeCardTotals.getValue()}</fo:block>
	                    				</#if>
	                    			</#list>	
                    			</fo:table-cell>
                    			</#list>
                    			<fo:table-cell>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			</fo:table-cell>
                    		</fo:table-row>
                    		<fo:table-row>
                    		    <#assign lineNumber = lineNumber + 1>
                    			<fo:table-cell>
                    				<fo:block>Route Totals:</fo:block>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			</fo:table-cell>
                    			<#assign routeCashCardTotals = tempRouteProdTotals.get("CASH_CARD").entrySet()>
                    			<#list productValuesList as product>
                    			<fo:table-cell>
                    				<#list routeCashCardTotals as CashCardTotals>
                    					<#if product.productId == CashCardTotals.getKey()>
                    						<fo:block keep-together="always" text-align="right">${CashCardTotals.getValue()}</fo:block>
                    					</#if>	
                    				</#list>
                    			</fo:table-cell>
                    			</#list>                    			
                    		</fo:table-row>
                    		<fo:table-row>
                    		<fo:table-cell>
                    		<fo:block>===========================================================================================================================</fo:block>
                    	    </fo:table-cell>
                    	    </fo:table-row>
                    	</fo:table-body>
                    </fo:table>		
                </fo:block>
            			</#if>
	        	    </#list>
                    <#assign lineNumber = 0>
	        		</fo:flow>
	        		</fo:page-sequence>
	        		<@header routeId=currentRouteId />
	        		<#assign prevRouteId=currentRouteId>
	        		<#assign routeTotalValue =0/>
					<#assign routePreDueTotal =0/>
					<#assign routeAmtTotals =0/>
	        	</#if>
	        	<fo:block>
			        <fo:table>
		        		<fo:table-column column-width="40pt"/>
	                    <fo:table-column column-width="50pt"/> 
	                    <fo:table-body>
	                    	<fo:table-row>
	                    		<fo:table-cell>
	                    			<fo:block text-align="left" keep-together="always" font-size="10pt">${scheduleList.getKey()?if_exists} ${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facilityDetails.get("description").toUpperCase())),15)}</fo:block>
	                    			<fo:block>products</fo:block>
	                    		</fo:table-cell>
	                    		<fo:table-cell>
	    							<fo:block font-family="Courier,monospace" font-size="10pt">                
						                <fo:table width="100%" table-layout="fixed" space-after="0.0in">
						                    <fo:table-column column-width="100pt"/>
							                    <fo:table-column column-width="50pt"/>                
							                   	<#list productList as product>            
									            	<fo:table-column column-width="52pt"/>      
									            </#list>
									            <#list productValuesList as products>            
									            	<fo:table-column column-width="52pt"/>      
									            </#list>		            
							            		<fo:table-column column-width="80pt"/>
							            		<fo:table-column column-width="70pt"/>
							            		<fo:table-column column-width="70pt"/>
							            		<fo:table-column column-width="70pt"/>
							            		<fo:table-column column-width="70pt"/>
							            		<fo:table-column column-width="70pt"/>
							            		<fo:table-column column-width="75pt"/>
							            		<fo:table-column column-width="80pt"/>               
								           	<fo:table-body> 
								           		<#assign checkListReportList = scheduleList.getValue() >
						                            <#list checkListReportList as checkListReport>
						                            <#assign value= checkListReport.TODAY_DUE>
									           		<#assign preDue= checkListReport.PREV_DUE>
									           		<#assign TotAmt=value+preDue>
									           		<fo:table-row >
									           		<#assign lineNumber = lineNumber + 1>
							                        	<fo:table-cell></fo:table-cell>
							                            <fo:table-cell ><fo:block text-indent="5pt">${checkListReport.supplyType}</fo:block></fo:table-cell >
							                            	<#list productList as product>
								                            	<#assign productQty = checkListReport[product.productId]>   
								                            	<fo:table-cell ><fo:block text-align="right"   white-space-collapse="false">${productQty?if_exists}</fo:block></fo:table-cell>
									                       	</#list>          					
									                   </fo:table-row>
							                        </#list>
							                        
							                        <#assign totalValue = (totalValue+value)/>
						           					<#assign preDueTotal = (preDueTotal+preDue)/>
						           					<#assign amtTotals = (amtTotals + TotAmt)/>  
						           					<#assign routeTotalValue = (routeTotalValue+value)/>
						           					<#assign routePreDueTotal = (routePreDueTotal+preDue)/>
						           					<#assign routeAmtTotals = (routeAmtTotals + TotAmt)/> 
							                        
							                        
							                        
							                        <fo:table-row>			
							                        	<fo:table-cell></fo:table-cell>				                        	
               											<fo:table-cell></fo:table-cell>
               											<fo:table-cell></fo:table-cell>
               											<fo:table-cell></fo:table-cell>
							                        	<fo:table-cell></fo:table-cell>
               											<fo:table-cell></fo:table-cell>	
               											<fo:table-cell></fo:table-cell>
               											<fo:table-cell></fo:table-cell>
               											<fo:table-cell></fo:table-cell>	
					                        			<fo:table-cell></fo:table-cell>
               											<fo:table-cell></fo:table-cell>	
               											<fo:table-cell></fo:table-cell>
               											<#if CollectionSheetType=="RouteCollectionSheet">
               											<fo:table-cell></fo:table-cell>
               											<fo:table-cell></fo:table-cell>	
               											<fo:table-cell></fo:table-cell>
							                    		<fo:table-cell><fo:block text-align="right" white-space-collapse="false" keep-together="always">${value?if_exists}</fo:block></fo:table-cell>
							                    		<fo:table-cell><fo:block text-align="right" white-space-collapse="false" keep-together="always">${preDue?if_exists}</fo:block></fo:table-cell>
							                     		<fo:table-cell><fo:block text-align="right" white-space-collapse="false" keep-together="always">${TotAmt?if_exists}</fo:block></fo:table-cell>
					                                    <#else>
					                                    <fo:table-cell></fo:table-cell>
               											<fo:table-cell></fo:table-cell>	
               											<fo:table-cell></fo:table-cell>
							                    		<fo:table-cell><fo:block text-align="right" white-space-collapse="false" keep-together="always">&#160;</fo:block></fo:table-cell>
							                    		<fo:table-cell><fo:block text-align="right" white-space-collapse="false" keep-together="always">&#160;</fo:block></fo:table-cell>
							                     		<fo:table-cell><fo:block text-align="right" white-space-collapse="false" keep-together="always">&#160;</fo:block></fo:table-cell>
					                                    </#if>
					                              </fo:table-row>
					                              
							                       <#assign ChangeProductsList = productsReportMap[scheduleList.getKey()]>
							                       <#list ChangeProductsList as prodValues>
								                       <fo:table-row>	
								                        <#assign lineNumber = lineNumber + 1>						                            
								                        	<fo:table-cell>
								                        		<fo:block keep-together="always" font-weight="bold"></fo:block>
								                        	</fo:table-cell>
								                        	<fo:table-cell></fo:table-cell>
								                        	<fo:table-cell ></fo:table-cell >
								                        	<#list productValuesList as products>
									                            <#assign productsQty = prodValues[products.productId]>
								                        		<fo:table-cell><fo:block>${productsQty}</fo:block></fo:table-cell>
								                        	</#list>
								                        </fo:table-row>
							                        </#list>
							                        <fo:table-row>
							                        <#assign lineNumber = lineNumber + 1>
							                        	<fo:table-cell>
							                        		<fo:block>---------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
							                       		</fo:table-cell>
							                        </fo:table-row>							                        
							                         <#if (lineNumber > 47)>
							                             <#assign lineNumber = 0>
							                             <fo:table-row>
							                             <fo:table-cell>
						           			 		          <fo:block  font-size="8pt" break-after="page"></fo:block>
						           			 		     </fo:table-cell>
						           			 		     </fo:table-row>
							                          </#if>                       
							                	</fo:table-body>
						               		</fo:table>
                						</fo:block>
		                    		</fo:table-cell>
		                    	</fo:table-row>
		                    </fo:table-body>
			        	</fo:table>
			        </fo:block>                
                 </#list> 
                 </#if>
                 <#assign productTotals = productTypeTotalsMap.get("CASH").entrySet()>
                <fo:block keep-together="always" font-size="10pt">
                	<fo:table width="100%" table-layout="fixed" space-after="0.0in">
                    	<fo:table-column column-width="70pt"/>
                    	<fo:table-column column-width="50pt"/>
                    	<fo:table-column column-width="60pt"/>
                    	<fo:table-column column-width="60pt"/>
                    	<fo:table-column column-width="60pt"/>
                    	<fo:table-column column-width="50pt"/>
                    	<fo:table-column column-width="50pt"/>
                    	<fo:table-column column-width="50pt"/>
                    	<fo:table-column column-width="50pt"/>
                    	<fo:table-column column-width="50pt"/>
                    	<fo:table-column column-width="50pt"/>
                    	<fo:table-column column-width="50pt"/>
                    	<fo:table-column column-width="60pt"/>
                    	<#list productTotals as productValues>
                    	<fo:table-column column-width="45pt"/>
                    	</#list>
                    	<fo:table-body>
                    		<fo:table-row>
                    			<fo:table-cell>
                    				<fo:block>Cash Totals:</fo:block>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			</fo:table-cell>
                    			<#list productList as product>
                    			<fo:table-cell>                    				
                    				<#list productTotals as productValues>
                    				 <#if product.productId == productValues.getKey()>
                    					<fo:block keep-together="always" text-align="right">${productValues.getValue()}</fo:block>
                    				</#if>
                    				</#list>
                    			</fo:table-cell>
                    			</#list>
                    			<fo:table-cell>
                    				<fo:block keep-together="always" text-align="right"></fo:block>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    				<fo:block keep-together="always" text-align="right"></fo:block>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    				<fo:block keep-together="always" text-align="right"></fo:block>
                    			</fo:table-cell>
                    		</fo:table-row>
                    		<fo:table-row>
                    			<fo:table-cell>
                    				<fo:block>Card Totals:</fo:block>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			</fo:table-cell>
                    			<#assign productCardTotals = productTypeTotalsMap.get("CARD").entrySet()>
                    			<#list productList as product>
                    			<fo:table-cell>                    			
                    			<#list productCardTotals as cardTotals>
                    				 <#if product.productId == cardTotals.getKey()>
                    				<fo:block keep-together="always" text-align="right">${cardTotals.getValue()}</fo:block>
                    				</#if>
                    			</#list>	
                    			</fo:table-cell>
                    			</#list>
                    			<fo:table-cell>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			</fo:table-cell>
                    		</fo:table-row>	
                    		<fo:table-row>
                    			<fo:table-cell>
                    				<fo:block>Grand Totals:</fo:block>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			</fo:table-cell>
                    			<#assign productCashCardTotals = productTypeTotalsMap.get("CASH_CARD").entrySet()>
                    			<#list productList as product>                    			
                    			<fo:table-cell>
                    				<#list productCashCardTotals as CashCardTotals>
                    					<#if product.productId == CashCardTotals.getKey()>
                    						<fo:block keep-together="always" text-align="right">${CashCardTotals.getValue()}</fo:block>
                    					</#if>
                    				</#list>
                    			</fo:table-cell>
                    			</#list>
                    			<#if CollectionSheetType=="RouteCollectionSheet">
                    			<fo:table-cell></fo:table-cell>
                    			<fo:table-cell></fo:table-cell>
                    			<fo:table-cell></fo:table-cell>
                    			<fo:table-cell>
                    				<fo:block keep-together="always" text-indent="45pt" text-align="left">${totalValue?if_exists}</fo:block>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    				<fo:block keep-together="always" text-indent="53pt" text-align="left">${preDueTotal?if_exists}</fo:block>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    				<fo:block keep-together="always" text-indent="55pt" text-align="left">${amtTotals?if_exists}</fo:block>
                    			</fo:table-cell>
                    			<#else>
                    			<fo:table-cell></fo:table-cell>
                    			<fo:table-cell></fo:table-cell>
                    			<fo:table-cell></fo:table-cell>
                    			<fo:table-cell>
                    				<fo:block keep-together="always" text-indent="45pt" text-align="left">&#160;</fo:block>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    				<fo:block keep-together="always" text-indent="53pt" text-align="left">&#160;</fo:block>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    				<fo:block keep-together="always" text-indent="55pt" text-align="left">&#160;</fo:block>
                    			</fo:table-cell>
                    			</#if>
                    		</fo:table-row>
                    		<fo:table-row>
                    			<fo:table-cell>
                    				<fo:block font-weight="bold">Products Totals</fo:block>
                    			</fo:table-cell>
                    		</fo:table-row>
                    		<fo:table-row>
                    			<fo:table-cell>
                    				<fo:block>Cash Totals:</fo:block>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			</fo:table-cell>
                    			<#list productValuesList as product>                    			
                    			<fo:table-cell>
                    				<#list productTotals as productValues>
                    				 <#if product.productId == productValues.getKey()>
                    					<fo:block keep-together="always" text-align="right">${productValues.getValue()}</fo:block>
                    				</#if>
                    				</#list>
                    			</fo:table-cell>
                    			</#list>
                    			<fo:table-cell>
                    				<fo:block keep-together="always" text-align="right"></fo:block>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    				<fo:block keep-together="always" text-align="right"></fo:block>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    				<fo:block keep-together="always" text-align="right"></fo:block>
                    			</fo:table-cell>
                    		</fo:table-row>
                    		<fo:table-row>
                    			<fo:table-cell>
                    				<fo:block>Card Totals:</fo:block>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			</fo:table-cell>
                    			<#assign productCardTotals = productTypeTotalsMap.get("CARD").entrySet()>
                    			<#list productValuesList as product>                    			
                    			<fo:table-cell>
                    			<#list productCardTotals as cardTotals>
                    				 <#if product.productId == cardTotals.getKey()>
                    					<fo:block keep-together="always" text-align="right">${cardTotals.getValue()}</fo:block>
                    				</#if>
                    			</#list>	
                    			</fo:table-cell>
                    			</#list>
                    			<fo:table-cell>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			</fo:table-cell>
                    		</fo:table-row>
                    		<fo:table-row>
                    			<fo:table-cell>
                    				<fo:block>Grand Totals:</fo:block>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			</fo:table-cell>
                    			<#assign productCashCardTotals = productTypeTotalsMap.get("CASH_CARD").entrySet()>
                    			<#list productValuesList as product>                    			
                    			<fo:table-cell>
                    				<#list productCashCardTotals as CashCardTotals>
                    					<#if product.productId == CashCardTotals.getKey()>
                    						<fo:block keep-together="always" text-align="right">${CashCardTotals.getValue()}</fo:block>
                    					</#if>
                    				</#list>
                    			</fo:table-cell>
                    			</#list>                    			
                    		</fo:table-row>
                    		<fo:table-row>
                    		<fo:table-cell>
                    		<fo:block>===========================================================================================================================</fo:block>
                    	    </fo:table-cell>
                    	    </fo:table-row>
                    	</fo:table-body>
                    </fo:table>		
                </fo:block>
           </fo:flow>
        </fo:page-sequence>
     
     <#else>
     	<fo:page-sequence master-reference="main">
	    		<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	       			<fo:block font-size="14pt">
	            		${uiLabelMap.OrderNoOrderFound}.
	       		 	</fo:block>
	    		</fo:flow>
			</fo:page-sequence>
     </#if>
     </fo:root>
</#escape>