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
            <fo:simple-page-master master-name="main" page-height="10in" page-width="12in"  margin-left=".3in" margin-right=".3in" margin-top=".5in">
                <fo:region-body margin-top=".7in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
         ${setRequestAttribute("OUTPUT_FILENAME", "VATReturns.txt")}
          <#if errorMessage?has_content>
	<fo:page-sequence master-reference="main">
	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	   <fo:block font-size="14pt">
	           ${errorMessage}.
        </fo:block>
	</fo:flow>
	</fo:page-sequence>	
	<#else>
         <#assign totalBasicRevenue = 0>
		 <#assign totalVatRevenue = 0>
		 <#assign totalRevenueValue = 0>
		 
		 <#assign grandTotalBasicRevenue = 0>
		 <#assign grandTotalVatRevenue = 0>
		 <#assign grandTotalRevenueValue = 0>
       <#if finalVatList?has_content>        
		        <fo:page-sequence master-reference="main" font-size="10pt">	
		        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
		        		<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">&#160;      ${uiLabelMap.KMFDairyHeader}</fo:block>
						<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">&#160;      ${uiLabelMap.KMFDairySubHeader}</fo:block>
                    	<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false">&#160;     MONTHLY VALUE ADDED TAX RETURN UNDER VAT ACT FROM: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayBegin, "dd/MM/yyyy")} TO: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayEnd, "dd/MM/yyyy")}</fo:block>
              			<fo:block font-size="13pt">====================================================================================</fo:block>
            			<fo:block text-align="left" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">&#160;S NO      DESCRIPTION         VAT%     BASIC VALUE     VAT VALUE    TOTAL VALUE</fo:block>	 	 	  
		        	</fo:static-content>	        	
		        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
                 	<fo:table border-width="1pt" border-style="dotted">
                    <fo:table-column column-width="30pt"/>
                    <fo:table-column column-width="100pt"/> 
               	    <fo:table-column column-width="40pt"/>
            		<fo:table-column column-width="40pt"/> 	
            		<fo:table-column column-width="60pt"/>	
            		<fo:table-column column-width="70pt"/>
	                    <fo:table-body>
	                    	<#assign serialNo = 1>
	                    	<#list finalVatList as finalVat>
		                    	<#assign vatDetails = finalVat.entrySet()>
		                    	<#list vatDetails as vat>
		                    		<fo:table-row>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">-----------------------------------------------------------------------------------</fo:block>  
		                        			</fo:table-cell>
		                        	</fo:table-row>
	                    			<fo:table-row>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">EXEMPTED TURNOVER (${vat.getKey()})</fo:block>  
		                        			</fo:table-cell>
		                        	</fo:table-row>
		                        	<fo:table-row>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">-----------------------------------------------------------------------------------</fo:block>  
		                        			</fo:table-cell>
		                        	</fo:table-row>
		                    		<#assign prodDetails = vat.getValue().entrySet()>
		                    			<#list prodDetails as product>
		                    				 <#assign productDetails = delegator.findOne("Product", {"productId" : product.getKey()}, true)?if_exists/>
		                    				 
		                    				 <#assign totalRevenue = product.getValue().get("totalRevenue")?if_exists>
		                    				 <#assign vatRevenue = ((totalRevenue * vat.getKey())/100)>
		                    				 <#assign basicRevenue = (totalRevenue - vatRevenue)>
											<fo:table-row>
												<fo:table-cell>
			                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${serialNo}</fo:block>  
			                        			</fo:table-cell>
												<fo:table-cell>
			                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${productDetails.brandName?if_exists}</fo:block>  
			                        			</fo:table-cell>
			                        			<fo:table-cell>
			                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${vat.getKey()?if_exists}</fo:block>  
			                        			</fo:table-cell>
			                        			<#assign totalBasicRevenue = (totalBasicRevenue + basicRevenue?if_exists)>
			                        			<fo:table-cell>
			                            			<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${basicRevenue?if_exists?string("#0.00")}</fo:block>  
			                        			</fo:table-cell>
			                        			<#assign totalVatRevenue = (totalVatRevenue + vatRevenue?if_exists)>
			                        			<fo:table-cell>
			                            			<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${vatRevenue?if_exists?string("#0.00")}</fo:block>  
			                        			</fo:table-cell>
			                        			<#assign totalRevenueValue = (totalRevenueValue + totalRevenue?if_exists)>
			                        			<fo:table-cell>
			                            			<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${totalRevenue?if_exists?string("#0.00")}</fo:block>  
			                        			</fo:table-cell>
			                        		</fo:table-row>
			                        		<#assign serialNo = serialNo+1>
			                        	</#list>
			                      </#list>
			                      <fo:table-row>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">-----------------------------------------------------------------------------------</fo:block>
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
		                        			<#assign grandTotalBasicRevenue = (grandTotalBasicRevenue + totalBasicRevenue?if_exists)>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${totalBasicRevenue?if_exists?string("#0.00")}</fo:block>  
		                        			</fo:table-cell>
		                        			<#assign grandTotalVatRevenue = (grandTotalVatRevenue + totalVatRevenue?if_exists)>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${totalVatRevenue?if_exists?string("#0.00")}</fo:block>  
		                        			</fo:table-cell>
		                        			<#assign grandTotalRevenueValue = (grandTotalRevenueValue + totalRevenueValue?if_exists)>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${totalRevenueValue?if_exists?string("#0.00")}</fo:block>  
		                        			</fo:table-cell>
		                        	</fo:table-row>
		                      </#list>		
	                    		 <fo:table-row>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">-----------------------------------------------------------------------------------</fo:block>
		                        			</fo:table-cell>
		                        </fo:table-row>
		                        <fo:table-row>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false"></fo:block>  
		                        			</fo:table-cell>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">GRAND TOTAL</fo:block>  
		                        			</fo:table-cell>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false"></fo:block>  
		                        			</fo:table-cell>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${grandTotalBasicRevenue?if_exists?string("#0.00")}</fo:block>  
		                        			</fo:table-cell>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${grandTotalVatRevenue?if_exists?string("#0.00")}</fo:block>  
		                        			</fo:table-cell>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${grandTotalRevenueValue?if_exists?string("#0.00")}</fo:block>  
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
  </#if>   
 </fo:root>
</#escape>