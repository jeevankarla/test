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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="12in"  margin-left=".3in" margin-right=".3in" margin-top=".5in">
                <fo:region-body margin-top=".7in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
         ${setRequestAttribute("OUTPUT_FILENAME", "VATReturns.txt")}
         <#assign totalBasicValue = 0>
		<#assign totalVatValue = 0>
		<#assign grandTotalValue = 0>
		
		<#assign catTotalBasicValue = 0>
		<#assign catTotalVatValue = 0>
		<#assign catGrandTotalValue = 0>
       <#if categoryProductMap?has_content>        
		        
		        <fo:page-sequence master-reference="main" font-size="10pt">	
		        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
		        		<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false">&#160;            ${uiLabelMap.aavinDairyMsg}</fo:block>
                    	<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false">&#160;          CORPORATE OFFICE , MARKETING UNIT , NANDANAM , CHENNAI - 35.</fo:block>
                    	<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false">&#160;          MONTHLY VALUE ADDED TAX RETURN UNDER VAT ACT :: ${parameters.customTimePeriodId}</fo:block>
              			<fo:block font-size="13pt">================================================================================</fo:block>
            			<fo:block text-align="left" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">&#160;S NO      DESCRIPTION         VAT%     BASIC VALUE     VAT VALUE    TOTAL VALUE</fo:block>	 	 	  
		        	</fo:static-content>	        	
		        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
                 	<fo:table border-width="1pt" border-style="dotted">
                    <fo:table-column column-width="30pt"/>
                    <fo:table-column column-width="100pt"/> 
               	    <fo:table-column column-width="40pt"/>
            		<fo:table-column column-width="60pt"/> 	
            		<fo:table-column column-width="60pt"/>	
            		<fo:table-column column-width="60pt"/>
	                    <fo:table-body>
	                    	<#assign serialNo = 1>
	                    	<#list categoryGroupingList as eachCategoryGroup>
		                    	<#assign catGroupDetails = categoryProductMap.get(eachCategoryGroup)>
		                    	
		                    	<#assign tempDis = StringUtil.split(eachCategoryGroup,"_")>
		                    		<fo:table-row>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">--------------------------------------------------------------------------------</fo:block>  
		                        			</fo:table-cell>
		                        	</fo:table-row>
	                    			<fo:table-row>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">EXEMPTED TURNOVER (${tempDis[2]}%)</fo:block>  
		                        			</fo:table-cell>
		                        	</fo:table-row>
		                        	<fo:table-row>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">--------------------------------------------------------------------------------</fo:block>  
		                        			</fo:table-cell>
		                        	</fo:table-row>
		                    	<#list catGroupDetails as eachCatGroupDetails>
		                    				<#assign totalBasicValue = totalBasicValue + eachCatGroupDetails.get("basicValue")>
											<#assign totalVatValue = totalVatValue + eachCatGroupDetails.get("vatValue")>
											<#assign grandTotalValue = grandTotalValue + eachCatGroupDetails.get("totalValue")>
											
											<#assign catTotalBasicValue = catTotalBasicValue + eachCatGroupDetails.get("basicValue")>
											<#assign catTotalVatValue = catTotalVatValue + eachCatGroupDetails.get("vatValue")>
											<#assign catGrandTotalValue = catGrandTotalValue + eachCatGroupDetails.get("totalValue")>
											
											<fo:table-row>
												<fo:table-cell>
			                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${serialNo}</fo:block>  
			                        			</fo:table-cell>
												<fo:table-cell>
			                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${eachCatGroupDetails.get("prodCategory")}</fo:block>  
			                        			</fo:table-cell>
			                        			<fo:table-cell>
			                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${eachCatGroupDetails.get("vatPercentage")}</fo:block>  
			                        			</fo:table-cell>
			                        			<fo:table-cell>
			                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${eachCatGroupDetails.get("basicValue")}</fo:block>  
			                        			</fo:table-cell>
			                        			<fo:table-cell>
			                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${eachCatGroupDetails.get("vatValue")}</fo:block>  
			                        			</fo:table-cell>
			                        			<fo:table-cell>
			                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${eachCatGroupDetails.get("totalValue")}</fo:block>  
			                        			</fo:table-cell>
			                        		</fo:table-row>
			                        		<#assign serialNo = serialNo+1>
			                      </#list>
			                      <fo:table-row>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">--------------------------------------------------------------------------------</fo:block>  
		                        			</fo:table-cell>
		                        	</fo:table-row>
	                    			<fo:table-row>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false"></fo:block>  
		                        			</fo:table-cell>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">&lt;SUB TOTAL&gt;</fo:block>  
		                        			</fo:table-cell>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false"></fo:block>  
		                        			</fo:table-cell>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${totalBasicValue}</fo:block>  
		                        			</fo:table-cell>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${totalVatValue}</fo:block>  
		                        			</fo:table-cell>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${grandTotalValue}</fo:block>  
		                        			</fo:table-cell>
		                        	</fo:table-row>
		                        	<#assign totalBasicValue = 0>
									<#assign totalVatValue = 0>
									<#assign grandTotalValue = 0>
									<#if eachCategoryGroup == "UNION_TAXABLE_14.5" || eachCategoryGroup == "DAIRY_TAXABLE_14.5">
										<fo:table-row>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">--------------------------------------------------------------------------------</fo:block>  
		                        			</fo:table-cell>
		                        		</fo:table-row>
	                    				<fo:table-row>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false"></fo:block>  
		                        			</fo:table-cell>
		                        			<#if tempDis[0] == "UNION">
			                        			<fo:table-cell>
			                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">DISTRICT UNION PRODUCTS</fo:block>  
			                        			</fo:table-cell>
			                        		<#else>
			                        			<fo:table-cell>
			                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">AMBATTUR DAIRY PRODUCTS</fo:block>  
			                        			</fo:table-cell>
		                        			</#if>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">&lt;SUB TOT&gt;</fo:block>  
		                        			</fo:table-cell>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${catTotalBasicValue}</fo:block>  
		                        			</fo:table-cell>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${catTotalVatValue}</fo:block>  
		                        			</fo:table-cell>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${catGrandTotalValue}</fo:block>  
		                        			</fo:table-cell>
		                        		</fo:table-row>	
		                        		<#assign catTotalBasicValue = 0>
										<#assign catTotalVatValue = 0>
										<#assign catGrandTotalValue = 0>
									</#if>
		                      </#list>		
	                    		 <fo:table-row>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">--------------------------------------------------------------------------------</fo:block>  
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
            	${uiLabelMap.NoOrdersFound}.
       		 </fo:block>
    	</fo:flow>
	</fo:page-sequence>	
  </#if>   
 </fo:root>
</#escape>