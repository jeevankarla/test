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
<#assign routeTotal = false>
<#assign zoneTotal = false>
<#assign disTotal = false>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
<fo:layout-master-set>
    <fo:simple-page-master master-name="main" page-height="11in" page-width="8.5in"
            margin-top="0.5in" margin-bottom="1in" margin-left="1in" margin-right="1in">
        <fo:region-body margin-top=".1in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>
    </fo:simple-page-master>
</fo:layout-master-set>
<#if truckSheetReportList?has_content>		
		<fo:page-sequence master-reference="main">
			<fo:flow flow-name="xsl-region-body" font-family="Helvetica">					
						    <fo:block font-size="14pt">${uiLabelMap.OrderReportTruckSheet}</fo:block>						    					           
						     <fo:block font-size="10pt"></fo:block>
						            <#if parameters.fromOrderDate?has_content><fo:block font-size="10pt">${uiLabelMap.CommonFromDate}: ${parameters.fromOrderDate}</fo:block></#if>
						            <#if parameters.thruOrderDate?has_content><fo:block font-size="10pt">${uiLabelMap.CommonThruDate}: ${parameters.thruOrderDate}</fo:block></#if>
						           <#list truckSheetReportList as truckSheetReport>
						           <#assign facilityGrandTotal = 0>	
						            <fo:block space-after.optimum="10pt" font-size="10pt">	
						            <fo:table>                
						                <fo:table-column column-width="60pt"/>
						                <fo:table-column column-width="60pt"/>
						                <fo:table-column column-width="60pt"/>
						                <fo:table-column column-width="60pt"/>	
						                <fo:table-column column-width="60pt"/>
						                <fo:table-column column-width="60pt"/>	
						                <fo:table-column column-width="60pt"/>
						                <fo:table-column column-width="60pt"/>
						                <fo:table-column column-width="60pt"/>		                              
						                <fo:table-header>
						                    <fo:table-row font-weight="bold">                        
						                        <fo:table-cell border-bottom="thin solid grey"><fo:block>${uiLabelMap.BoothName}</fo:block></fo:table-cell>
						                                          
						                        <fo:table-cell border-bottom="thin solid grey"><fo:block>${uiLabelMap.ProductTypeName}</fo:block></fo:table-cell>
						                                     
						                       <fo:table-cell border-bottom="thin solid grey"><fo:block>${uiLabelMap.TypeCard}</fo:block></fo:table-cell>
						                                          
						                        <fo:table-cell border-bottom="thin solid grey"><fo:block>${uiLabelMap.TypeCash}</fo:block></fo:table-cell>
						                                           
						                        <fo:table-cell border-bottom="thin solid grey"><fo:block>${uiLabelMap.TypeCredit}</fo:block></fo:table-cell>
						                                            
						                        <fo:table-cell border-bottom="thin solid grey"><fo:block>${uiLabelMap.TypeSpecialOrder}</fo:block></fo:table-cell>
						                                            
						                        <fo:table-cell border-bottom="thin solid grey"><fo:block>${uiLabelMap.CommonTotal}</fo:block></fo:table-cell>                                          
						                        <fo:table-cell border-bottom="thin solid grey"><fo:block>${uiLabelMap.Crates}</fo:block></fo:table-cell>                                          
						                        <fo:table-cell border-bottom="thin solid grey"><fo:block>${uiLabelMap.CommonAmount}</fo:block></fo:table-cell> -->
						                    </fo:table-row>
						                </fo:table-header>
						                <fo:table-body>
						                   
						                      	<#assign productEntries = (truckSheetReport).entrySet()>						                      	
						                      	<#if productEntries?has_content>
						                      	<#assign facilityId = truckSheetReport.get("facilityId")>			                      	                     	
						                      	
						                      	<#assign facility = delegator.findOne("Facility", {"facilityId" : facilityId}, true)>
						                       <fo:table-row>                            
						                            <fo:table-cell padding="1pt" >
						                                <fo:block>${facility.get("description")?if_exists}(${facilityId?if_exists})</fo:block>
						                            </fo:table-cell>
						                            <fo:table-cell padding="2pt">
						                                <fo:block></fo:block>
						                            </fo:table-cell>	                           
						                       </fo:table-row>
						                       </#if>	                      	                      	
						                      	<#list productEntries as productEntry>
						                      		<#if productEntry.getKey() != "facilityId">
						                      		<#assign product = delegator.findOne("Product", {"productId" : productEntry.getKey()}, true)>
						                        <fo:table-row>
						                             <fo:table-cell padding="2pt" >
						                                <fo:block></fo:block>
						                            </fo:table-cell> 
						                            <fo:table-cell padding="2pt" >
						                                <fo:block>
						                                	<fo:table width="100%" table-layout="fixed" >
					                 						 <fo:table-column column-width="60pt"/>
					                 						  <fo:table-column column-width="60pt"/>
					                 						   <fo:table-column column-width="60pt"/>
					                 						    <fo:table-column column-width="60pt"/>
					                 						     <fo:table-column column-width="60pt"/>
					                 						      <fo:table-column column-width="60pt"/>
					                 						       <fo:table-column column-width="60pt"/>
					                 						        <fo:table-column column-width="60pt"/>
					                 						                        						         
					                  						<fo:table-body>                  						 
					                  						<fo:table-row >                    
								                            <fo:table-cell  >
								                                <fo:block>${product.get("description")?if_exists}(${product.get("productId")})</fo:block>
								                            </fo:table-cell>								                            
								                      			<#assign typeEntries = (productEntry.getValue()).entrySet()>                      	
								                      			<#list typeEntries as typeEntry>                      				
								                      				<fo:table-cell padding="2pt">
										                                <fo:block>${typeEntry.getValue()}</fo:block>
										                            </fo:table-cell>
										                            <#if typeEntry.getKey() == "TOTALAMOUNT">
										                            	<#assign facilityGrandTotal = (facilityGrandTotal+typeEntry.getValue())>
										                            </#if>                      				
								                            	</#list>
								                         </fo:table-row>								                        
								                         </fo:table-body>
								                         </fo:table>    
						                            </fo:block>
						                            </fo:table-cell>                                                       
						                        </fo:table-row>
						                        </#if>
						                        </#list>                       
						                      <fo:table-row >
						                      	<fo:table-cell padding="1pt">
								                     <fo:block></fo:block>
								                 </fo:table-cell>
								                 <fo:table-cell padding="1pt">
								                     <fo:block></fo:block>
								                 </fo:table-cell>	
								                 <fo:table-cell padding="1pt">
								                     <fo:block></fo:block>
								                 </fo:table-cell>	
								                 <fo:table-cell padding="1pt">
								                     <fo:block></fo:block>
								                 </fo:table-cell>	
								                 <fo:table-cell padding="1pt">
								                     <fo:block></fo:block>
								                 </fo:table-cell>	
								                 <fo:table-cell padding="1pt">
								                     <fo:block></fo:block>
								                 </fo:table-cell>	
								                 <fo:table-cell padding="1pt">
								                     <fo:block></fo:block>
								                 </fo:table-cell>	
								                 <fo:table-cell padding="1pt"  text-align="left">
								                     <fo:block>${uiLabelMap.CommonTotal}:</fo:block>
								                 </fo:table-cell>								                 	
								                 <fo:table-cell padding="1pt"  text-align="left">
								                 	<fo:block><@ofbizCurrency amount=facilityGrandTotal isoCode=defaultOrganizationPartyCurrencyUomId/></fo:block>
								                 </fo:table-cell>	
								            </fo:table-row>
						                </fo:table-body>
						            </fo:table>
						            </fo:block>
						            </#list>
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