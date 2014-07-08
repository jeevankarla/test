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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-right=".3in" margin-top=".1in">
                <fo:region-body margin-top="0.3in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
         ${setRequestAttribute("OUTPUT_FILENAME", "VATReturns.pdf")}
		 
	  	 <#assign grandTotalNetVatExcValue = 0>
    	 <#assign grandTotalVatValue = 0>
    	 <#assign grandTotalNetRevenue = 0>
    	 <#assign grandTotalNetQuantity = 0>
		 
       <#if vatMap?has_content>        
		        <fo:page-sequence master-reference="main" font-size="10pt">	
		        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
		        		<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" font-size="10pt" white-space-collapse="false">&#160;${uiLabelMap.CommonPage}- <fo:page-number/> </fo:block>
		        	</fo:static-content>	        	
		        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">
						<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false">    UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
						<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false">&#160;      Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
		        		<fo:block  keep-together="always" text-align="center" font-weight="bold"  font-size="14pt" font-family="Courier,monospace" white-space-collapse="false">&#160;      ${uiLabelMap.KMFDairyHeader}</fo:block>
						<fo:block  keep-together="always" text-align="center" font-weight="bold"  font-size="14pt" font-family="Courier,monospace" white-space-collapse="false">&#160;      ${uiLabelMap.KMFDairySubHeader}</fo:block>
                    	<fo:block text-align="center" font-size="14pt" font-weight="bold"  keep-together="always"  white-space-collapse="false">&#160;     MONTHLY VAT SUPPORT STATEMENT FOR THE PERIOD: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayBegin, "dd/MM/yyyy")} TO: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayEnd, "dd/MM/yyyy")}</fo:block>
              			<fo:block font-size="10pt">================================================================================================================</fo:block>
            			<fo:block text-align="left" font-size="11pt" keep-together="always" font-weight="bold"  font-family="Courier,monospace" white-space-collapse="false">&#160;S NO      DESCRIPTION              SALE VALUE     		VAT COLLECTED   		 NET SALE   		 QUANTITY(KGS/LTRS)</fo:block>	 	 	  
            	<fo:block>
                 	<fo:table>
                    <fo:table-column column-width="80pt"/>
                    <fo:table-column column-width="100pt"/> 
               	    <fo:table-column column-width="120pt"/>
            		<fo:table-column column-width="110pt"/> 	
            		<fo:table-column column-width="120pt"/>	
            		<fo:table-column column-width="130pt"/>
            		<fo:table-column column-width="120pt"/>
	                    <fo:table-body>
	                    	<#assign serialNo = 1>
		                    	<#assign vatDetails = vatMap.entrySet()>
		                    	<#list vatDetails as vat>
		                    		<fo:table-row>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">----------------------------------------------------------------------------------------------------------------</fo:block>  
		                        			</fo:table-cell>
		                        	</fo:table-row>
	                    			<fo:table-row>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="14pt" white-space-collapse="false"><#if vat.getKey() == 0> EXEMPTED TURNOVER (${vat.getKey()})<#else> TURNOVER WITH VAT (${vat.getKey()})</#if></fo:block>  
		                        			</fo:table-cell>
		                        	</fo:table-row>
		                        	<fo:table-row>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">----------------------------------------------------------------------------------------------------------------</fo:block>  
		                        			</fo:table-cell>
		                        	</fo:table-row>
		                        	
		                        	<#assign vatTotalNetVatExcValue = 0>
		                        	<#assign vatTotalVatValue = 0>
		                        	<#assign vatTotalNetRevenue = 0>
		                        	<#assign vatTotalNetQuantity = 0>
		                        	
		                    		<#assign prodDetails = vat.getValue().entrySet()>
			                    	<#list prodDetails as prodCategory>
			                    		<#assign productCategory = delegator.findOne("ProductCategory", {"productCategoryId" : prodCategory.getKey()}, true)?if_exists/>
		                    		<fo:table-row>
	                        			<fo:table-cell>
	                            			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">${productCategory.description?if_exists}</fo:block>  
	                        			</fo:table-cell>
		                        	</fo:table-row>
		                        	<fo:table-row>
	                        			<fo:table-cell>
	                            			<fo:block font-size="10pt">----------------------------------------------------------------------------------------------------------------</fo:block>
	                        			</fo:table-cell>
		                        	</fo:table-row>
		                        	<#assign totalNetVatExcValue = 0>
		                        	<#assign totalVatValue = 0>
		                        	<#assign totalNetRevenue = 0>
		                        	<#assign totalNetQuantity = 0>
		                        	<#assign serialNo = 1>
		                    		<#assign productDetails = prodCategory.getValue().entrySet()>
		                    			<#list productDetails as prod>
		                    				 <#assign product = delegator.findOne("Product", {"productId" : prod.getKey()}, true)?if_exists/>
		                    				 <#assign netRevenue = (prod.getValue().get("revenue"))?if_exists>
		                    				 <#assign netQuantity = (prod.getValue().get("quantity"))?if_exists>
		                    				 <#assign vatValue = (prod.getValue().get("vatRevenue"))?if_exists>
		                    				 <#if vatValue?has_content>
		                    				 	 <#assign netExcVatValue = (netRevenue - vatValue)>
		                    				 <#else>
		                    				 	<#assign netExcVatValue = (netRevenue?if_exists)>
		                    				 </#if>
		                    				 
											<fo:table-row>
												<fo:table-cell>
			                            			<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${serialNo?if_exists}</fo:block>  
			                        			</fo:table-cell>
			                        			<fo:table-cell>
			                            			<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${product.brandName?if_exists}</fo:block>  
			                        			</fo:table-cell>
			                        			<#assign totalNetVatExcValue = (totalNetVatExcValue + netExcVatValue?if_exists)>
			                        			<fo:table-cell>
			                            			<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${netExcVatValue?if_exists?string("#0.00")}</fo:block>  
			                        			</fo:table-cell>
			                        			<#assign totalVatValue = (totalVatValue + vatValue?if_exists)>
			                        			<fo:table-cell>
			                            			<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${vatValue?if_exists?string("#0.00")}</fo:block>  
			                        			</fo:table-cell>
			                        			<#assign totalNetRevenue = (totalNetRevenue + netRevenue?if_exists)>
			                        			<fo:table-cell>
			                            			<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${netRevenue?if_exists?string("#0.00")}</fo:block>  
			                        			</fo:table-cell>
			                        			<#assign totalNetQuantity = (totalNetQuantity + netQuantity?if_exists)>
			                        			<fo:table-cell>
			                            			<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${netQuantity?if_exists?string("#0.00")}</fo:block>  
			                        			</fo:table-cell>
			                        		</fo:table-row>
			                        		<#assign serialNo = serialNo+1>
			                        	</#list>
			                        		<fo:table-row>
			                        			<fo:table-cell>
			                            			<fo:block font-size="10pt">----------------------------------------------------------------------------------------------------------------</fo:block>
			                        			</fo:table-cell>
				                        	</fo:table-row>
				                        	<fo:table-row>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>  
		                        			</fo:table-cell>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-weight="bold" font-size="12pt" white-space-collapse="false">&lt;SUB TOTAL&gt;</fo:block>  
		                        			</fo:table-cell>
		                        			<#assign vatTotalNetVatExcValue = (vatTotalNetVatExcValue + totalNetVatExcValue?if_exists)>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${totalNetVatExcValue?if_exists?string("#0.00")}</fo:block>  
		                        			</fo:table-cell>
		                        			<#assign vatTotalVatValue = (vatTotalVatValue + totalVatValue?if_exists)>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${totalVatValue?if_exists?string("#0.00")}</fo:block>  
		                        			</fo:table-cell>
		                        			<#assign vatTotalNetRevenue = (vatTotalNetRevenue + totalNetRevenue?if_exists)>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${totalNetRevenue?if_exists?string("#0.00")}</fo:block>  
		                        			</fo:table-cell>
		                        			<#assign vatTotalNetQuantity = (vatTotalNetQuantity + totalNetQuantity?if_exists)>
		                        			<fo:table-cell>
			                            		<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${totalNetQuantity?if_exists?string("#0.00")}</fo:block>  
			                        		</fo:table-cell>
		                        		</fo:table-row>
			                     	 </#list>
			                     	 	<fo:table-row>
			                        			<fo:table-cell>
			                            			<fo:block font-size="10pt">----------------------------------------------------------------------------------------------------------------</fo:block>
			                        			</fo:table-cell>
				                        	</fo:table-row>
				                        	<fo:table-row>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>  
		                        			</fo:table-cell>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-weight="bold" font-size="14pt" white-space-collapse="false">&lt;VAT TOTAL&gt;</fo:block>  
		                        			</fo:table-cell>
		                        			<#assign grandTotalNetVatExcValue = (grandTotalNetVatExcValue + vatTotalNetVatExcValue?if_exists)>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${vatTotalNetVatExcValue?if_exists?string("#0.00")}</fo:block>  
		                        			</fo:table-cell>
		                        			<#assign grandTotalVatValue = (grandTotalVatValue + vatTotalVatValue?if_exists)>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${vatTotalVatValue?if_exists?string("#0.00")}</fo:block>  
		                        			</fo:table-cell>
		                        			<#assign grandTotalNetRevenue = (grandTotalNetRevenue + vatTotalNetRevenue?if_exists)>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${vatTotalNetRevenue?if_exists?string("#0.00")}</fo:block>  
		                        			</fo:table-cell>
		                        			<#assign grandTotalNetQuantity = (grandTotalNetQuantity + vatTotalNetQuantity?if_exists)>
		                        			<fo:table-cell>
			                            		<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${vatTotalNetQuantity?if_exists?string("#0.00")}</fo:block>  
			                        		</fo:table-cell>
		                        		</fo:table-row>
			                      </#list>
			                      		<fo:table-row >
			                        			<fo:table-cell>
			                            			<fo:block font-size="10pt">----------------------------------------------------------------------------------------------------------------</fo:block>
			                        			</fo:table-cell>
				                        	</fo:table-row>
				                        	<fo:table-row font-weight="bold">
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>  
		                        			</fo:table-cell>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-weight="bold" font-size="14pt" white-space-collapse="false">&lt;GRAND TOTAL&gt;</fo:block>  
		                        			</fo:table-cell>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${grandTotalNetVatExcValue?if_exists?string("#0.00")}</fo:block>  
		                        			</fo:table-cell>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${grandTotalVatValue?if_exists?string("#0.00")}</fo:block>  
		                        			</fo:table-cell>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${grandTotalNetRevenue?if_exists?string("#0.00")}</fo:block>  
		                        			</fo:table-cell>
		                        			<fo:table-cell>
			                            		<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${grandTotalNetQuantity?if_exists?string("#0.00")}</fo:block>  
			                        		</fo:table-cell>
		                        		</fo:table-row>
		                        		<fo:table-row>
			                        			<fo:table-cell>
			                            			<fo:block font-size="10pt">----------------------------------------------------------------------------------------------------------------</fo:block>
			                        			</fo:table-cell>
				                        	</fo:table-row>
		                        		<fo:table-row>
											<fo:table-cell>
							            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
							       			</fo:table-cell>
						  				</fo:table-row>
						  				<fo:table-row>
											<fo:table-cell>
							            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
							       			</fo:table-cell>
						  				</fo:table-row>
						  				<fo:table-row>
											<fo:table-cell>
							            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
							       			</fo:table-cell>
						  				</fo:table-row>
						  				<fo:table-row>
											<fo:table-cell>
							            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
							       			</fo:table-cell>
						  				</fo:table-row>
		                        		<fo:table-row>
							       			<fo:table-cell>
							            		<fo:block  keep-together="always" font-size="11pt" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Authorised Signatory</fo:block>  
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