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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
            margin-top="0.5in" margin-bottom=".5in" margin-left=".2in">
        <fo:region-body margin-top="1.5in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>

<#assign routeWiseList =routeWiseMap.entrySet()>
<#if routeWiseList?has_content>
<#list routeWiseList as routesEntry>
	<#assign boothWiseEntries = routesEntry.getValue()>
<#if boothWiseEntries?has_content>	
<fo:page-sequence master-reference="main" force-page-count="no-force">					
	<fo:static-content flow-name="xsl-region-before"> 
				<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                            		                    SUPRAJA DAIRY PRIVATE LIMITED</fo:block>
				<#assign routeDetails = delegator.findOne("Facility", {"facilityId" : routesEntry.getKey()}, true)>
				<#if   parameters.reportTypeFlag =="RouteCollectionSheet">
					<#assign shipment = delegator.findOne("Shipment", {"shipmentId" : parameters.shipmentId}, true)>
					<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                                           <#if shipment.shipmentTypeId.startsWith("AM")?if_exists>MORNING.<#else>EVENING.</#if>ROUTE COLLECTION SHEET</fo:block>
					<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;  Route No: ${routeDetails.get("facilityName")}                          Date: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(supplyDateTime, "dd/MM/yyyy")}</fo:block>
				</#if>
				<#if   parameters.reportTypeFlag =="RouteDispatchSheet">
					<#assign shipment = delegator.findOne("Shipment", {"shipmentId" : parameters.shipmentId}, true)>
					<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                                           <#if shipment.shipmentTypeId.startsWith("AM")?if_exists>MORNING.<#else>EVENING.</#if>  ROUTE DISPATCH SHEET</fo:block>
					<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;  Route No: ${routeDetails.get("facilityName")}                          Date: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(supplyDateTime, "dd/MM/yyyy")}</fo:block>
				</#if>	
				<#if  parameters.reportTypeFlag =="consolidatedRouteCollection">
					<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                                           CONSOLIDATED ROUTE COLLECTION SHEET : VISAKHAPATNAM</fo:block>
					<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;  Route No: ${routeDetails.get("facilityName")}                          Date: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(supplyDateTime, "dd/MM/yyyy")}</fo:block>
				</#if>	
            	<fo:block font-family="Courier,monospace">----------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
        		<fo:block font-family="Courier,monospace" font-size="10pt">                
                <fo:table width="100%" table-layout="fixed" space-after="0.0in">
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="20pt"/>               
	            	<#list lmsproductList as product>            
	            	  <fo:table-column column-width="50pt"/>      
	            	</#list>
	            	<#list byProductsList as product>            
	            	  <fo:table-column column-width="50pt"/>      
	            	</#list>           			              		          
		            <fo:table-header>
		            	<fo:table-cell ><fo:block text-align="left" keep-together="always">BoothNo. AgentName</fo:block></fo:table-cell>
		            	<fo:table-cell></fo:table-cell>
		            	<fo:table-cell></fo:table-cell>	 
		            	<fo:table-cell></fo:table-cell>           		
                       <#list lmsproductList as product>                       		
                       		<fo:table-cell>
                       			<fo:block keep-together="always" white-space-collapse="false">${product.brandName?if_exists}</fo:block>
                       		</fo:table-cell>                       		
                       	</#list>                       	
                       	<fo:table-cell></fo:table-cell>
                       	<fo:table-cell></fo:table-cell>
                       	<fo:table-cell></fo:table-cell>
                       	<fo:table-cell></fo:table-cell>    
                       	<fo:table-cell></fo:table-cell>	   
                       <#if parameters.reportTypeFlag !="RouteDispatchSheet">	                
	                    	<fo:table-cell><fo:block >Value</fo:block></fo:table-cell>  
	                    </#if>	
		              <#if   parameters.reportTypeFlag !="RouteCollectionSheet" > 
		              		<#if parameters.reportTypeFlag !="RouteDispatchSheet">
			                <fo:table-cell><fo:block >Pre.Due</fo:block></fo:table-cell> 
			                <fo:table-cell><fo:block >Tot.Due</fo:block></fo:table-cell> 
			                </#if> 
	                  </#if>      
	                </fo:table-header>
	                <fo:table-body>
	               	  <fo:table-row>
                			<fo:table-cell><fo:block>Products</fo:block></fo:table-cell>
                			<fo:table-cell></fo:table-cell>
                			<fo:table-cell></fo:table-cell>
                			<fo:table-cell></fo:table-cell>
                			<#list byProductsList as allProducts>
                       		<fo:table-cell>
                       			<fo:block keep-together="always" white-space-collapse="false">${allProducts.brandName}</fo:block>
                      		</fo:table-cell>
                      		</#list>	
	                	</fo:table-row>
	                </fo:table-body>
	        	</fo:table>
	        </fo:block>	
	        <fo:block font-family="Courier,monospace">----------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	</fo:static-content>
	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">		
			<#assign boothEntriesList = boothWiseEntries.entrySet()>
			<#assign totValue=0>
			<#assign totPreDue=0>
			<#assign totAmount=0>
			<#list boothEntriesList as bothEntries>
			<fo:block>
			        <fo:table>
		        		<fo:table-column column-width="40pt"/>
	                    <fo:table-column column-width="50pt"/> 
	                    <fo:table-body>
	                    	<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : bothEntries.getKey()}, true)>
	                    	<fo:table-row>
	                    		<fo:table-cell>
	                    			<fo:block text-align="left" keep-together="always" font-size="10pt">${bothEntries.getKey()?if_exists} ${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facilityDetails.get("description").toUpperCase())),15)}</fo:block>
	                    		</fo:table-cell>
	                    		<fo:table-cell>
	    							<fo:block font-family="Courier,monospace" font-size="10pt">                
						                <fo:table width="100%" table-layout="fixed" space-after="0.0in">
						                    <fo:table-column column-width="75pt"/>
							                    <fo:table-column column-width="20pt"/>                
							                   	<#list lmsproductList as product>            
									            	<fo:table-column column-width="50pt"/>      
									            </#list>
									            <#list byProductsList as products>            
									            	<fo:table-column column-width="50pt"/>      
									            </#list>		            
								           	<fo:table-body> 
								           		<#assign boothEntryDetails = bothEntries.getValue().entrySet() >	
								           			<#assign todayDue =bothEntries.getValue().get("TOTAMT")>
								           			<#assign prevDue =bothEntries.getValue().get("PREVDUE")>
								           			<#assign totValue=totValue+todayDue>
													<#assign totPreDue=totPreDue+prevDue>
													<#assign totAmount=totAmount+(todayDue+prevDue)>					                            
						                            <#list boothEntryDetails as boothDetails>
							                            <#if boothDetails.getKey() =="CASH">
											           		<fo:table-row >									           		
									                        	<fo:table-cell></fo:table-cell>
									                            <fo:table-cell ><fo:block text-indent="5pt">CS</fo:block></fo:table-cell >
									                            	<#list lmsproductList as product>
										                            	<#assign productQty = boothDetails.getValue().get(product.productId)>   
										                            	<fo:table-cell ><fo:block text-align="right"   white-space-collapse="false">${productQty?if_exists}</fo:block></fo:table-cell>
											                       	</#list>      					
											                   </fo:table-row>
										                 </#if> 
										                 <#if boothDetails.getKey() =="CREDIT">
											           		<fo:table-row >									           		
									                        	<fo:table-cell></fo:table-cell>
									                            <fo:table-cell ><fo:block text-indent="5pt">CR</fo:block></fo:table-cell >
									                            	<#list lmsproductList as product>
										                            	<#assign productQty = boothDetails.getValue().get(product.productId)>   
										                            	<fo:table-cell ><fo:block text-align="right"   white-space-collapse="false">${productQty?if_exists}</fo:block></fo:table-cell>
											                       	</#list>      					
											                   </fo:table-row>
										                 </#if> 
							                         </#list>							                        
							                        <fo:table-row>			
							                        	<fo:table-cell><fo:block font-weight="bold">PRODUCTS</fo:block></fo:table-cell>				                        	
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
               											<fo:table-cell></fo:table-cell>	
               											<fo:table-cell></fo:table-cell>
               											<fo:table-cell></fo:table-cell>
               											<fo:table-cell></fo:table-cell>
               											<#if parameters.reportTypeFlag !="RouteDispatchSheet">
							                    			<fo:table-cell><fo:block text-align="right" white-space-collapse="false" keep-together="always">${todayDue}</fo:block></fo:table-cell>
							                    		</#if>
							                    		<#if   parameters.reportTypeFlag !="RouteCollectionSheet" > 
		              										<#if parameters.reportTypeFlag !="RouteDispatchSheet">
							                    				<fo:table-cell><fo:block text-align="right" white-space-collapse="false" keep-together="always">${prevDue}</fo:block></fo:table-cell>
							                    				<fo:table-cell><fo:block text-align="right" white-space-collapse="false" keep-together="always">${todayDue+prevDue}</fo:block></fo:table-cell>
					                              			</#if>
					                              		</#if>	
					                              </fo:table-row>					                              
							                      <#list boothEntryDetails as boothDetails>
							                            <#if boothDetails.getKey() =="CASH">
											           		<fo:table-row >									           		
									                        	<fo:table-cell></fo:table-cell>
									                            <fo:table-cell ><fo:block text-indent="5pt">CS</fo:block></fo:table-cell >
									                            	<#list byProductsList as product>
										                            	<#assign productQty = boothDetails.getValue().get(product.productId)>   
										                            	<fo:table-cell ><fo:block text-align="right"   white-space-collapse="false">${productQty?if_exists}</fo:block></fo:table-cell>
											                       	</#list>      					
											                   </fo:table-row>
										                 </#if> 
										                 <#if boothDetails.getKey() =="CREDIT">
											           		<fo:table-row >									           		
									                        	<fo:table-cell></fo:table-cell>
									                            <fo:table-cell ><fo:block text-indent="5pt">CR</fo:block></fo:table-cell >
									                            	<#list byProductsList as product>
										                            	<#assign productQty = boothDetails.getValue().get(product.productId)>   
										                            	<fo:table-cell ><fo:block text-align="right"   white-space-collapse="false">${productQty?if_exists}</fo:block></fo:table-cell>
											                       	</#list>      					
											                   </fo:table-row>
										                 </#if> 
							                         </#list>							                        							                        
							                	</fo:table-body>
						               		</fo:table>
                						</fo:block>                						
		                    		</fo:table-cell>
		                    	</fo:table-row>
		                    	<fo:table-row>							                     
		                        	<fo:table-cell>
		                        		<fo:block font-family="Courier,monospace">----------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		                       		</fo:table-cell>
		                        </fo:table-row>
		                    </fo:table-body>
			        	</fo:table>
			        </fo:block>
			       </#list>			       
			       <fo:block>
			       		<fo:table>
			       			<fo:table-column column-width="40pt"/>
	                    	<fo:table-column column-width="50pt"/>
	                    	<fo:table-body>
	                    	<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : routesEntry.getKey()}, true)>
	                    	<fo:table-row>
	                    		<fo:table-cell>
	                    			<fo:block text-align="left" keep-together="always" font-size="10pt" font-weight="bold">${routeDetails.get("facilityName")} Totals</fo:block>
	                    		</fo:table-cell>
	                    		<fo:table-cell>
	    							<fo:block font-family="Courier,monospace" font-size="10pt">                
						                <fo:table width="100%" table-layout="fixed" space-after="0.0in">
						                    <fo:table-column column-width="75pt"/>
							                    <fo:table-column column-width="20pt"/>                
							                   	<#list lmsproductList as product>            
									            	<fo:table-column column-width="50pt"/>      
									            </#list>
									            <#list byProductsList as products>            
									            	<fo:table-column column-width="50pt"/>      
									            </#list>		            
								           	<fo:table-body> 
								           		<#assign routeEntryDetails = routeTotalsMap[routesEntry.getKey()].entrySet() >	
								           							                            
						                            <#list routeEntryDetails as routeDetails>
							                            <#if routeDetails.getKey() =="CASH">
											           		<fo:table-row >									           		
									                        	<fo:table-cell></fo:table-cell>
									                            <fo:table-cell ><fo:block text-indent="5pt">CS</fo:block></fo:table-cell >
									                            	<#list lmsproductList as product>
										                            	<#assign productQty = routeDetails.getValue().get(product.productId)>   
										                            	<fo:table-cell ><fo:block text-align="right"   white-space-collapse="false">${productQty?if_exists}</fo:block></fo:table-cell>
											                       	</#list>      					
											                   </fo:table-row>
										                 </#if> 
										                 <#if routeDetails.getKey() =="CREDIT">
											           		<fo:table-row >									           		
									                        	<fo:table-cell></fo:table-cell>
									                            <fo:table-cell ><fo:block text-indent="5pt">CR</fo:block></fo:table-cell >
									                            	<#list lmsproductList as product>
										                            	<#assign productQty = routeDetails.getValue().get(product.productId)>   
										                            	<fo:table-cell ><fo:block text-align="right"   white-space-collapse="false">${productQty?if_exists}</fo:block></fo:table-cell>
											                       	</#list>      					
											                   </fo:table-row>
										                 </#if> 
							                         </#list>							                        
							                       <fo:table-row>			
							                        	<fo:table-cell><fo:block font-weight="bold">PRODUCTS</fo:block></fo:table-cell>				                        	
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
               											<fo:table-cell></fo:table-cell>	
               											<fo:table-cell></fo:table-cell>
               											<fo:table-cell></fo:table-cell>
               											<fo:table-cell></fo:table-cell>
               										<#if parameters.reportTypeFlag !="RouteDispatchSheet">	
							                    		<fo:table-cell><fo:block text-align="right" white-space-collapse="false" keep-together="always">${totValue}</fo:block></fo:table-cell>
							                    	</#if>	
							                    	<#if parameters.reportTypeFlag !="RouteCollectionSheet" > 
		              										<#if parameters.reportTypeFlag !="RouteDispatchSheet">
							                    				<fo:table-cell><fo:block text-align="right" white-space-collapse="false" keep-together="always">${totPreDue}</fo:block></fo:table-cell>
							                    				<fo:table-cell><fo:block text-align="right" white-space-collapse="false" keep-together="always">${totAmount}</fo:block></fo:table-cell>
					                              	 		</#if>
					                              	 </#if>		
					                              </fo:table-row>					                              
							                      <#list routeEntryDetails as routeDetails>
							                            <#if routeDetails.getKey() =="CASH">
											           		<fo:table-row >									           		
									                        	<fo:table-cell></fo:table-cell>
									                            <fo:table-cell ><fo:block text-indent="5pt">CS</fo:block></fo:table-cell >
									                            	<#list byProductsList as product>
										                            	<#assign productQty = routeDetails.getValue().get(product.productId)>   
										                            	<fo:table-cell ><fo:block text-align="right"   white-space-collapse="false">${productQty?if_exists}</fo:block></fo:table-cell>
											                       	</#list>      					
											                   </fo:table-row>
										                 </#if> 
										                 <#if routeDetails.getKey() =="CREDIT">
											           		<fo:table-row >									           		
									                        	<fo:table-cell></fo:table-cell>
									                            <fo:table-cell ><fo:block text-indent="5pt">CR</fo:block></fo:table-cell >
									                            	<#list byProductsList as product>
										                            	<#assign productQty = routeDetails.getValue().get(product.productId)>   
										                            	<fo:table-cell ><fo:block text-align="right"   white-space-collapse="false">${productQty?if_exists}</fo:block></fo:table-cell>
											                       	</#list>      					
											                   </fo:table-row>
										                 </#if> 
							                         </#list>							                        							                        
							                	</fo:table-body>
						               		</fo:table>
						               		<fo:block font-family="Courier,monospace" >&#160; </fo:block>
						               		<fo:table width="100%" table-layout="fixed" space-after="0.0in">
						               		  <fo:table-column column-width="200pt"/>
									            	<fo:table-column column-width="100pt"/>
									            	<fo:table-column column-width="50pt"/>
									            	<fo:table-column column-width="50pt"/>
									            	<fo:table-column column-width="50pt"/>
									            	<fo:table-column column-width="50pt"/>
									            	<fo:table-column column-width="50pt"/>
									            	<fo:table-column column-width="50pt"/>
									            	<fo:table-column column-width="50pt"/>
									            	<fo:table-column column-width="50pt"/>
									            	<fo:table-column column-width="50pt"/>
									            	<fo:table-header>
									            	<fo:table-cell><fo:block font-weight="bold" >Crates and Cans</fo:block></fo:table-cell>
									            	  	<fo:table-cell><fo:block >&#160; </fo:block></fo:table-cell>
									            	  	<fo:table-cell><fo:block text-align="center" >Crates</fo:block></fo:table-cell>
							                        	<fo:table-cell><fo:block >&#160; </fo:block></fo:table-cell>
							                        	<fo:table-cell><fo:block text-align="center" >CANS-</fo:block></fo:table-cell>
							                        	
							                        	<fo:table-cell><fo:block  text-align="center">20Lt</fo:block></fo:table-cell>
							                        	<fo:table-cell><fo:block text-align="center">30Lt </fo:block></fo:table-cell>
							                        	<fo:table-cell><fo:block text-align="center">40Lt</fo:block></fo:table-cell>
							                        
									            	</fo:table-header>
									            		<fo:table-body> 
									            		<fo:table-row>
							                        	<fo:table-cell><fo:block font-family="Courier,monospace" >&#160;      </fo:block>
							                        	</fo:table-cell>	
							                        	</fo:table-row>
									            		 <fo:table-row>
							                        	<fo:table-cell><fo:block font-weight="bold"></fo:block></fo:table-cell>	
							                        	<fo:table-cell><fo:block font-weight="bold">&#160; </fo:block></fo:table-cell>	
							                        	<#assign routeCratesCans = routeCratesCansMap[routesEntry.getKey()].entrySet() >
							                        		   <#list routeCratesCans as cratesCans>
							                        		    <#if cratesCans.getKey() =="NOCRATES">	
							                        		<fo:table-cell><fo:block font-weight="bold" text-align="center">${cratesCans.getValue()}</fo:block></fo:table-cell>
							                        		<fo:table-cell><fo:block font-weight="bold">&#160; </fo:block></fo:table-cell>
							                        	    <fo:table-cell><fo:block font-weight="bold">&#160; </fo:block></fo:table-cell>
							                        	
							                        	     <#else>
							                        		<fo:table-cell><fo:block font-weight="bold" text-align="center">${cratesCans.getValue()}</fo:block></fo:table-cell>
							                        			</#if>
							                        			</#list>	
									            		</fo:table-row>			                        							                        
							                	</fo:table-body>
						               		</fo:table>
                						</fo:block>                						
		                    		</fo:table-cell>
		                    	</fo:table-row>
		                    	<fo:table-row>							                     
		                        	<fo:table-cell>
		                        		<fo:block font-family="Courier,monospace" break-after="page">----------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
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
  </#list> 
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