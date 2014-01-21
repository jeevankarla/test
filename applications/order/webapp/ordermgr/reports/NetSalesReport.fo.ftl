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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
            margin-top="0.5in" margin-bottom="1in" margin-left=".3in" margin-right="1in">
        <fo:region-body margin-top="1in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "netsales.txt")}
<#if netSalesList?has_content>
<fo:page-sequence master-reference="main" force-page-count="no-force">					
			<fo:static-content flow-name="xsl-region-before"> 
				<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                        ${reportHeader.description?if_exists}</fo:block>				
              	<fo:block keep-together="always" font-family="Courier,monospace" white-space-collapse="false"> ${uiLabelMap.CommonPage}:<fo:page-number/>      MILK DESPATCHES/NETSLS FOR THE MONTH OF  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "MMMMM/yyyy")}</fo:block>  
              	              	<fo:block>---------------------------------------------------------------------------------------------------------------------------------</fo:block>
            	<fo:block white-space-collapse="false" font-size="9pt"  font-family="Courier,monospace"  text-align="left">DATE     ${uiLabelMap.ProductTypeName}        ${uiLabelMap.TypeCredit}        ${uiLabelMap.TypeCard}       ${uiLabelMap.TypeSpecialOrder}      CASH     GP      LITRES     RMRDRTN    NET SLS</fo:block>
            
            	<fo:block>---------------------------------------------------------------------------------------------------------------------------------</fo:block>
			    </fo:static-content>
			     
			<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			
	   <#list netSalesList as netSales>
	   	<#assign netSalesEntry = netSales.entrySet()>
	   	<#assign gTotLitres=0>
	   <#list netSalesEntry as netSalesEntrie>
       <fo:block>
       		<fo:table width="100%" table-layout="fixed" space-after="0.0in">
        	<fo:table-column column-width="100%"/>
            	<fo:table-body>
				<fo:table-row column-width="100%">
			    	<fo:table-cell column-width="100%">
            			<fo:table  table-layout="fixed" >                
				    		<fo:table-column column-width="45pt"/>
				    		<fo:table-column column-width="43pt"/>
				    		<fo:table-column column-width="30pt"/>
				    		<fo:table-column column-width="37pt"/>
				    		<fo:table-column column-width="40pt"/>
				    		<fo:table-column column-width="40pt"/>
				    		<fo:table-column column-width="40pt"/>
				    		<fo:table-column column-width="40pt"/>
				    		<fo:table-column column-width="40pt"/>
				    		<fo:table-column column-width="40pt"/>
				    		<fo:table-column column-width="40pt"/>
				    		<fo:table-column column-width="40pt"/>
				    		<fo:table-column column-width="40pt"/>
				    		
				    		<fo:table-body>
                			<fo:table-row>                            
                        		<fo:table-cell>
                            		<fo:block text-align="left"><#if netSalesEntrie.getKey()?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(netSalesEntrie.getKey(), "dd/MM")} </#if></fo:block>
                       			</fo:table-cell>
                            	<fo:table-cell>
                            		 <#list netSalesEntry as netSalesEntrie>
            						 	<#assign netSalesValues=(netSalesEntrie.getValue()).entrySet()>
        							 <#list netSalesValues as netSalesReport>
        							 	<#assign netValues=netSalesReport.getValue().entrySet()>
	   								 <#list netValues as netEntry>
	                                 <#if netEntry.getKey() != "facilityId" && netEntry.getKey() != "facilityType">
        							 	<#assign product = delegator.findOne("Product", {"productId" : netEntry.getKey()}, true)>
                                 <fo:block>
        							<fo:table>
	           							<fo:table-column column-width="28pt"/>
	           							<fo:table-column column-width="50pt"/>
	           							<fo:table-column column-width="57pt"/>
	           							<fo:table-column column-width="47pt"/>
	           							<fo:table-column column-width="57pt"/>
	           							<fo:table-column column-width="95pt"/>
	           							<fo:table-column column-width="60pt"/>
	           							<fo:table-column column-width="70pt"/>
	           							<fo:table-body> 
			      							<fo:table-row >                    
					 							<fo:table-cell>
					       							<fo:block  text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((product.get("brandName")))),7)}</fo:block>
					   							</fo:table-cell>								                            
											<#assign typeEntries = (netEntry.getValue()).entrySet()>
											<#list typeEntries as typeEntry>
												<#if typeEntry.getKey() == "LITRES">
													<#assign netLitres=(typeEntry.getValue())>
						                      		<#assign gTotLitres=(gTotLitres+netLitres)>
						                      	</#if> 
						                      	<#if typeEntry.getKey() != "CRATES" && typeEntry.getKey() != "AGNTCS" && typeEntry.getKey() != "PTCCS" && typeEntry.getKey() != "TOTAL" && typeEntry.getKey() != "TOTALAMOUNT" && typeEntry.getKey() != "NOPKTS" && typeEntry.getKey() != "NOCRATES">
							                      	<fo:table-cell >
														<fo:block  font-size="8.2pt" text-align="right">${typeEntry.getValue()}</fo:block>
													</fo:table-cell>
												</#if>
											</#list>
												<fo:table-cell >
														<fo:block  text-align="right">0.0*</fo:block>
												</fo:table-cell>
												<fo:table-cell >
														<fo:block  text-align="right">${netLitres}</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</fo:table-body>
				    				</fo:table>
					 			</fo:block>
					 			</#if>
					 			</#list>
					 			</#list>
					 			</#list>
					 			</fo:table-cell>
							</fo:table-row>
							<fo:table-row >
                  					<fo:table-cell >
	                     				<fo:block ></fo:block>
	                 				</fo:table-cell>
	                 				<fo:table-cell>
	                     				<fo:block></fo:block>
	                 				</fo:table-cell>	
	                 				<fo:table-cell>
	                     				<fo:block ></fo:block>
	                 				</fo:table-cell>	
	                 				<fo:table-cell >
	                     				<fo:block></fo:block>
	                 				</fo:table-cell>
	                  				<fo:table-cell>
	                     				<fo:block ></fo:block>
	                 				</fo:table-cell>	
	                 				<fo:table-cell >
	                     				<fo:block></fo:block>
	                 				</fo:table-cell>
	                 				<fo:table-cell >
	                     				<fo:block></fo:block>
	                 				</fo:table-cell>
	                 				<fo:table-cell >
	                     				<fo:block></fo:block>
	                 				</fo:table-cell>
	                 				<fo:table-cell >
	                     				<fo:block></fo:block>
	                 				</fo:table-cell>
	                 				<fo:table-cell >
	                     				<fo:block>${gTotLitres}*</fo:block>
	                 				</fo:table-cell>	
	                 				<fo:table-cell >
	                     				<fo:block></fo:block>
	                 				</fo:table-cell>	
	                 				<fo:table-cell >
	                     				<fo:block></fo:block>
	                 				</fo:table-cell>	
	                 				<fo:table-cell>
	                     				<fo:block text-align="center">${gTotLitres}*</fo:block>
	                 				</fo:table-cell>
	                  			</fo:table-row>
							<fo:table-row>
	            				<fo:table-cell>
	            					<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	                 			</fo:table-cell>
	            			</fo:table-row>
	            			</fo:table-body>
						</fo:table>
					</fo:table-cell>
				</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block>		
        </#list>
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