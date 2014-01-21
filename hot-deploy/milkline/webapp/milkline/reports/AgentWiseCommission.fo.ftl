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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="17in"
                   margin-top="0.3in"  margin-left=".2in">
                <fo:region-body margin-top="1.3in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent=".5in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
      		<#assign routeWiseList =routeWiseMap.entrySet()>
		    <#if routeWiseList?has_content>  
		    	<#list routeWiseList as routeWiseEntries>
			        <fo:page-sequence master-reference="main">
			        	<fo:static-content flow-name="xsl-region-before">
			        		<fo:block text-align="center" white-space-collapse="false" font-size="12pt" keep-together="always" font-weight="bold">SUPRAJA DAIRY PVT.LTD</fo:block>
			        		<fo:block text-align="center" white-space-collapse="false" font-size="10pt" keep-together="always" font-weight="bold">AGENT SALES COMMISSION REPORT</fo:block>
			        		<fo:block text-align="center" white-space-collapse="false" font-size="12pt" keep-together="always" font-weight="bold">&#160;                                                                                                                        Date   ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}</fo:block>                                                   
			        		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			        		<#assign routeDetails = delegator.findOne("Facility", {"facilityId" : routeWiseEntries.getKey()}, true)>
			        		<fo:block white-space-collapse="false" font-size="13pt" keep-together="always" font-weight="bold">&#160;    Period : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayBegin, "dd-MMM-yyyy")} -  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayEnd, "dd-MMM-yyyy")}                                               (Qty. in Liters)                                             Route : ${routeDetails.get("facilityName")?if_exists}</fo:block>
			        		<fo:block>
			        			 <fo:table width="100%" table-layout="fixed" border-style="solid" font-size="8pt">
					                 <fo:table-column column-width="80pt"/>               
						            <#list lmsproductList as product>            
						             	<fo:table-column column-width="40pt"/> 						             	
						            </#list>						            
						            <fo:table-column column-width="40pt"/>
						            <fo:table-column column-width="50pt"/>
						            <fo:table-header>						            	
						            	<fo:table-cell><fo:block text-align="center" keep-together="always" border-style="solid">AGENT NAME</fo:block></fo:table-cell>       		
				                       <#list lmsproductList as product>                       		
				                       		<fo:table-cell>
				                       			<fo:block keep-together="always" text-align="center" white-space-collapse="false" border-style="solid" font-weight="bold">${product.brandName?if_exists}</fo:block>
				                       		</fo:table-cell>
				                       	</#list>				                       			                       	
				                       	<fo:table-cell border-style="solid">
			                       			<fo:block text-align="left" white-space-collapse="false" font-weight="bold" text-indent="3pt">TOTAL</fo:block>
			                       		</fo:table-cell>			                       		
			                       		<fo:table-cell border-style="solid">
			                       			<fo:block text-align="left" white-space-collapse="false" font-weight="bold">TOTSPCOMM</fo:block>
			                       		</fo:table-cell> 
				                     </fo:table-header> 
				                     <fo:table-body>
				                     	<fo:table-row>
				                			<fo:table-cell></fo:table-cell>				                				
	                					</fo:table-row>
				                     </fo:table-body> 	
			        			 </fo:table>
			        		</fo:block>
			        	</fo:static-content>
			       		<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			       			<#assign agentWiseList = (routeWiseEntries.getValue()).entrySet()>
			       			<#list agentWiseList as agentWiseEntries>
			       				<#if agentWiseEntries.getValue().get("TotComn") !=0>
				        		<fo:block font-size="8pt">
				        			<fo:table>
						        		<fo:table-column column-width="1pt"/>					        		
					                              
						             	<fo:table-column column-width="80pt"/>					             	
						            							            	
					                     <fo:table-body>
						                    <#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : agentWiseEntries.getKey()}, true)>
					                    	<fo:table-row>
					                    		<fo:table-cell>
					                    			<fo:block text-align="left" keep-together="always" font-size="10pt">${agentWiseEntries.getKey()?if_exists} ${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facilityDetails.get("description").toUpperCase())),12)}</fo:block>
					                    			<fo:block text-align="left" keep-together="always" font-size="10pt">DISCOUNTS</fo:block>
					                    		</fo:table-cell>
					                    		<fo:table-cell >
					                    			<fo:block font-family="Courier,monospace" font-size="10pt">                
										                <fo:table width="100%" table-layout="fixed" space-after="0.0in">
										                    <fo:table-column column-width="80pt"/>											                
														    <#list lmsproductList as product>            
												            	<fo:table-column column-width="40pt"/>      
												            </#list> 
												            <fo:table-column column-width="40pt"/>
												            <fo:table-column column-width="50pt"/>
												            <fo:table-body>
												            	<fo:table-row border-style="solid">									           		
									                        		<fo:table-cell></fo:table-cell>
									                        		<#list lmsproductList as product>
										                            	<#assign productQty = agentWiseEntries.getValue().get(product.productId).get("Qty")>   
										                            	<#assign disc = agentWiseEntries.getValue().get(product.productId).get("disc")>
										                            	<fo:table-cell border-style="solid">
										                            		<fo:block text-align="right" font-weight="bold" >${productQty?if_exists?string("##0.0#")}</fo:block>
										                            		<fo:block text-align="right" font-weight="bold">${disc?if_exists?string("##0.00#")}</fo:block>
										                            	</fo:table-cell>
											                       	</#list> 
											                       	<fo:table-cell>
											                       		<fo:block text-align="right" font-weight="bold">${agentWiseEntries.getValue().get("TOTAL")}</fo:block>
											                       	</fo:table-cell>
											                       	<fo:table-cell border-style="solid">
											                       		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
											                       		<fo:block text-align="right" font-weight="bold">${agentWiseEntries.getValue().get("TotComn")?string("##0.00#")}</fo:block>
											                       	</fo:table-cell>
												            	</fo:table-row>
												            </fo:table-body>
											            </fo:table>
											        </fo:block>				                    		
					                    		</fo:table-cell>					                    		
					                    	</fo:table-row>	
					                     </fo:table-body>	
					                </fo:table>    
				        		</fo:block>
				        		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				        		</#if>
				        	</#list>
				        		<#assign routeTotals =routeTotalsMap[routeWiseEntries.getKey()]>
				        		<#assign routeEntries = routeTotals.entrySet()>
				        		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				        		<#if routeTotals.get("TotComn") !=0>
				        		<fo:block font-size="8pt">
				        			<fo:table>
						        		<fo:table-column column-width="1pt"/>						        								        		
					                    <#list lmsproductList as product>            
						             		<fo:table-column column-width="40pt"/>					             	
						            	</#list>
					                     <fo:table-body>						                   
					                    	<fo:table-row>
					                    		<fo:table-cell>
					                    			<fo:block text-align="left" keep-together="always" font-size="10pt" font-weight="bold">Grand Total</fo:block>
					                    			<fo:block text-align="left" keep-together="always" font-size="10pt" font-weight="bold">DISCOUNTS</fo:block>
					                    		</fo:table-cell>
					                    		<fo:table-cell>
					                    			<fo:block font-family="Courier,monospace" font-size="10pt">                
										                <fo:table width="100%" table-layout="fixed">
										                    <fo:table-column column-width="80pt"/>											                
														    <#list lmsproductList as product>            
												            	<fo:table-column column-width="40pt"/>      
												            </#list> 
												            <fo:table-column column-width="40pt"/>
												            <fo:table-column column-width="50pt"/>
												            <fo:table-body>
												            	<fo:table-row border-style="solid">									           		
									                        		<fo:table-cell></fo:table-cell>
									                        		<#list lmsproductList as product>		
									                        			<#assign routeWiseDetails=routeTotals.get(product.productId)>	                        			
										                            	<fo:table-cell border-style="solid">
										                            		<#assign totQty = routeWiseDetails.get("Qty")>   
										                            		<#assign totdisc = routeWiseDetails.get("disc")>
										                            		<fo:block text-align="right" font-weight="bold">${totQty?if_exists?string("##0.0#")}</fo:block>
										                            		<fo:block text-align="right" font-weight="bold">${totdisc?if_exists?string("##0.00#")}</fo:block>
										                            	</fo:table-cell>										                           
											                       	</#list>
											                       	<fo:table-cell border-style="solid">
											                       		<fo:block text-align="right" font-weight="bold">${routeTotals.get("TOTAL")?if_exists?string("##0.0#")}</fo:block>
											                       	</fo:table-cell>
											                       	<fo:table-cell border-style="solid">
											                       		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
											                       		<fo:block text-align="right" font-weight="bold">${routeTotals.get("TotComn")?string("##0.00#")}</fo:block>
											                       	</fo:table-cell>
												            	</fo:table-row>
												            </fo:table-body>
											            </fo:table>
											        </fo:block>				                    		
					                    		</fo:table-cell>					                    		
					                    	</fo:table-row>	
					                     </fo:table-body>	
					                </fo:table>    
				        		</fo:block>
				        		</#if>							
						</fo:flow>
				 	</fo:page-sequence>
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