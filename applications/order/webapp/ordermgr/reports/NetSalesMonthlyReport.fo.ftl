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
<#if summaryDetailsMap?has_content>
<fo:page-sequence master-reference="main" force-page-count="no-force">					
	<fo:static-content flow-name="xsl-region-before"> 
		<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
        <fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                        ${reportHeader.description?if_exists}</fo:block>				
		<fo:block keep-together="always" font-family="Courier,monospace" white-space-collapse="false"> ${uiLabelMap.CommonPage}:<fo:page-number/>      MILK DESPATCHES/NETSLS FOR THE MONTH OF  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "MMMMM/yyyy")}</fo:block>  
        <fo:block>---------------------------------------------------------------------------------------------------------------------------------</fo:block>
        <fo:block white-space-collapse="false" font-size="10pt" font-family="Courier,monospace"  text-align="left">DATE   ${uiLabelMap.ProductTypeName}         ${uiLabelMap.TypeCredit}    ${uiLabelMap.TypeCard}     ${uiLabelMap.TypeSpecialOrder}      CASH   GP    LITRES  RMRDRTN  NET SLS</fo:block>
        <fo:block>---------------------------------------------------------------------------------------------------------------------------------</fo:block>
	</fo:static-content>
	
	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" font-size="9pt">
		<#assign summaryDetailEntries = summaryDetailsMap.get("PM").entrySet()>	
		<#list summaryDetailEntries as summaryDetailEntry>			
		<#assign summaryDetails = summaryDetailEntry.getValue()>
		<fo:block>
       		<fo:table width="100%" table-layout="fixed" space-after="0.0in">
        		<fo:table-column column-width="100pt"/>
				<fo:table-column column-width="115pt"/>
				<fo:table-column column-width="115pt"/>
				<fo:table-column column-width="40pt"/>
				<fo:table-column column-width="60pt"/>
				<fo:table-column column-width="70pt"/>
				<fo:table-column column-width="70pt"/>
            	<fo:table-body>
				<fo:table-row column-width="100%">
			    	<fo:table-cell column-width="100%">
            			<fo:table  table-layout="fixed" >                
				    		<fo:table-column column-width="45pt"/>
				    		<fo:table-column column-width="30pt"/>
				    		<fo:table-column column-width="25pt"/>
				    		<fo:table-column column-width="30pt"/>
				    		<fo:table-column column-width="35pt"/>
				    		<fo:table-column column-width="40pt"/>
				    		<fo:table-column column-width="65pt"/>
				    		<fo:table-column column-width="30pt"/>
				    		<fo:table-column column-width="60pt"/>
				    		<fo:table-column column-width="40pt"/>
				    		<fo:table-column column-width="60pt"/>
				    		<fo:table-body>
                				<fo:table-row>                            
                        			<fo:table-cell>
                            			<fo:block text-align="left">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(summaryDetailEntry.getKey(), "dd/MM")}</fo:block>
                       				</fo:table-cell>
                       			</fo:table-row>	
                       				<#assign productEntries = summaryDetails.entrySet()>
                       				<#assign  totalLtrs=0>                       				
                       				<#list productEntries as productEntry>
                       				<#assign prodcutEntryValue= productEntry.getValue().entrySet()>
                       				<#assign  totalLtrs=totalLtrs+productEntry.getValue().get("totalQuantity")>                      				
                            	<fo:table-row>
                            		<fo:table-cell>${productEntry.getValue().get("PM")}</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block keep-together="always">${productEntry.getValue().get("productName")}</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell></fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="right">${productEntry.getValue().get("CREDIT")}</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="right">${productEntry.getValue().get("CARD")}</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="right">${productEntry.getValue().get("SPECIAL_ORDER")}</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="right">${productEntry.getValue().get("CASH")}</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="right">0</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="right">${productEntry.getValue().get("totalQuantity")}</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="right">0.0*</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="right">${productEntry.getValue().get("totalQuantity")}</fo:block>
                            		</fo:table-cell>
                                </fo:table-row>                                	
                               </#list>                                                              
							</fo:table-body>
						</fo:table>
					</fo:table-cell>					
				</fo:table-row>	
				<fo:table-row>
					<fo:table-cell></fo:table-cell>
					<fo:table-cell></fo:table-cell>
					<fo:table-cell></fo:table-cell>									
					<fo:table-cell><fo:block>${totalLtrs?if_exists}*</fo:block></fo:table-cell>
					<fo:table-cell></fo:table-cell>
					<fo:table-cell><fo:block>${totalLtrs?if_exists}*</fo:block></fo:table-cell>
				</fo:table-row>				
				</fo:table-body>
			</fo:table>
		</fo:block>		
		</#list>		
		<#assign AMSummaryDetailEntries = summaryDetailsMap.get("AM").entrySet()>
		<#list AMSummaryDetailEntries as AMsummaryDetailEntry>
		<#assign AMsummaryDetails = AMsummaryDetailEntry.getValue()>
		<fo:block>
       		<fo:table width="100%" table-layout="fixed" space-after="0.0in">        	
				<fo:table-column column-width="115pt"/>
				<fo:table-column column-width="115pt"/>
				<fo:table-column column-width="40pt"/>
				<fo:table-column column-width="40pt"/>
				<fo:table-column column-width="70pt"/>
				<fo:table-column column-width="70pt"/>
				<fo:table-column column-width="70pt"/>
            	<fo:table-body>
				<fo:table-row column-width="100%">
			    	<fo:table-cell column-width="100%">
            			<fo:table  table-layout="fixed" >                
				    		<fo:table-column column-width="40pt"/>
				    		<fo:table-column column-width="35pt"/>
				    		<fo:table-column column-width="20pt"/>
				    		<fo:table-column column-width="35pt"/>
				    		<fo:table-column column-width="45pt"/>
				    		<fo:table-column column-width="50pt"/>
				    		<fo:table-column column-width="60pt"/>
				    		<fo:table-column column-width="20pt"/>
				    		<fo:table-column column-width="60pt"/>
				    		<fo:table-column column-width="40pt"/>
				    		<fo:table-column column-width="70pt"/>
				    		<fo:table-body>
                				<fo:table-row> 
                					<fo:table-cell>
                            			<fo:block text-align="left">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(AMsummaryDetailEntry.getKey(), "dd/MM")}</fo:block>
                       				</fo:table-cell>
                       			</fo:table-row>	
                       				<#assign AMproductEntries = AMsummaryDetails.entrySet()>
                       				<#assign  totalAmLtrs=0> 
                       				<#list AMproductEntries as AMproductEntry>
                       				<#assign AMprodcutEntryValue= AMproductEntry.getValue().entrySet()>  
                       				<#assign  totalAmLtrs=totalAmLtrs+AMproductEntry.getValue().get("totalQuantity")>                          		
                            		<fo:table-row>
                            		<fo:table-cell></fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block keep-together="always">${AMproductEntry.getValue().get("productName")}</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell></fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="right">${AMproductEntry.getValue().get("CREDIT")}</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="right">${AMproductEntry.getValue().get("CARD")}</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="right">${AMproductEntry.getValue().get("SPECIAL_ORDER")}</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="right">${AMproductEntry.getValue().get("CASH")}</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="right">0</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="right">${AMproductEntry.getValue().get("totalQuantity")}</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="right">0.0*</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="right">${AMproductEntry.getValue().get("totalQuantity")}</fo:block>
                            		</fo:table-cell>
                                	</fo:table-row>
                                	</#list>								
							</fo:table-body>
						</fo:table>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell></fo:table-cell>
					<fo:table-cell></fo:table-cell>
					<fo:table-cell></fo:table-cell>	
					<fo:table-cell></fo:table-cell>									
					<fo:table-cell><fo:block text-align="center" text-indent="20pt">${totalAmLtrs?if_exists}*</fo:block></fo:table-cell>
					<fo:table-cell></fo:table-cell>
					<fo:table-cell><fo:block>${totalAmLtrs?if_exists}*</fo:block></fo:table-cell>
				</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block>	
		</#list>
		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		<#assign GrtotalValues = GrTotalMap.entrySet()>		
		<fo:block>
       		<fo:table width="100%" table-layout="fixed" space-after="0.0in">
        		<fo:table-column column-width="115pt"/>
				<fo:table-column column-width="115pt"/>
				<fo:table-column column-width="40pt"/>
				<fo:table-column column-width="40pt"/>
				<fo:table-column column-width="70pt"/>
				<fo:table-column column-width="70pt"/>
				<fo:table-column column-width="70pt"/>
            	<fo:table-body>
				<fo:table-row column-width="100%">
			    	<fo:table-cell column-width="100%">
            			<fo:table  table-layout="fixed" >                
				    		<fo:table-column column-width="40pt"/>
				    		<fo:table-column column-width="35pt"/>
				    		<fo:table-column column-width="20pt"/>
				    		<fo:table-column column-width="35pt"/>
				    		<fo:table-column column-width="45pt"/>
				    		<fo:table-column column-width="50pt"/>
				    		<fo:table-column column-width="60pt"/>
				    		<fo:table-column column-width="20pt"/>
				    		<fo:table-column column-width="60pt"/>
				    		<fo:table-column column-width="40pt"/>
				    		<fo:table-column column-width="70pt"/>
				    		<fo:table-body>
                				<fo:table-row> 
                					<fo:table-cell>
                            			<fo:block text-align="left">GRTot</fo:block>
                       				</fo:table-cell>
                       			</fo:table-row>	
                       			<#assign  GrtotalLtrs=0>
                       			<#list GrtotalValues as GrTotalEntries>
                       			<#assign GrtotalLtrs=GrtotalLtrs+GrTotalEntries.getValue().get("totalQuantity")>
                       			<fo:table-row>
                            		<fo:table-cell></fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block keep-together="always">${GrTotalEntries.getValue().get("productName")}</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell></fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="right">${GrTotalEntries.getValue().get("CREDIT")}</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="right">${GrTotalEntries.getValue().get("CARD")}</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="right">${GrTotalEntries.getValue().get("SPECIAL_ORDER")}</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="right">${GrTotalEntries.getValue().get("CASH")}</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="right">0</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="right">${GrTotalEntries.getValue().get("totalQuantity")}</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="right">0.0*</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="right">${GrTotalEntries.getValue().get("totalQuantity")}</fo:block>
                            		</fo:table-cell> 
                                	</fo:table-row>
									</#list>                              							
							</fo:table-body>
						</fo:table>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell></fo:table-cell>
					<fo:table-cell></fo:table-cell>
					<fo:table-cell></fo:table-cell>	
					<fo:table-cell></fo:table-cell>									
					<fo:table-cell><fo:block text-align="center" text-indent="20pt">${GrtotalLtrs?if_exists}*</fo:block></fo:table-cell>
					<fo:table-cell></fo:table-cell>
					<fo:table-cell><fo:block>${GrtotalLtrs?if_exists}*</fo:block></fo:table-cell>
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