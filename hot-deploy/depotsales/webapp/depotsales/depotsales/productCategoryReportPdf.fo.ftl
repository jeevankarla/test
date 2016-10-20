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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-left=".3in" margin-right=".3in" margin-top=".5in">
                <fo:region-body margin-top="1.3in"/>
                <fo:region-before extent="0.2in"/>
				<fo:region-after extent="1.5in"/>  
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "ProductCategoryReport.pdf")}
        <#if errorMessage?has_content>
	<fo:page-sequence master-reference="main">
	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	   <fo:block font-size="14pt">
	           ${errorMessage}.
        </fo:block>
	</fo:flow>
	</fo:page-sequence>	
	<#else>
       <#if categoryWiseProductsMap?has_content> 
	        <fo:page-sequence master-reference="main" font-size="12pt">	
	        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
	        		<!--<fo:block text-align="center" font-weight="bold" keep-together="always"  font-family="Courier,monospace" white-space-collapse="false">${uiLabelMap.KMFDairyHeader}</fo:block>-->
	        		<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "DEPOT_SALES","propertyName" : "reportHeaderLable"}, true)>
	        		<#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "DEPOT_SALES","propertyName" : "reportSubHeaderLable"}, true)>
        			<fo:block keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${reportHeader.description?if_exists}</fo:block>
        			<fo:block keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${reportSubHeader.description?if_exists}</fo:block>				
	        		<!--<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>-->
                	<fo:block text-align="center"  keep-together="always"  white-space-collapse="false" font-size="18pt" font-weight="bold">CATEGORY WISE PRODUCTS REPORT</fo:block>
          			<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
          			
          			<fo:block>
          			<fo:table>
	                    <fo:table-column column-width="10%"/>
	                    <fo:table-column column-width="35%"/>
	                    <fo:table-column column-width="40%"/>
	                    <fo:table-column column-width="15%"/>
	                    <fo:table-body>
	                    	<fo:table-row>
			                    <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="center" font-size="17pt" white-space-collapse="false" font-weight="bold"> ProductId </fo:block>  
					            </fo:table-cell>
					             <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="center" font-size="17pt" white-space-collapse="false" font-weight="bold">Product Name</fo:block>  
					            </fo:table-cell>
					             <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="center" font-size="17pt" white-space-collapse="false" font-weight="bold">Product Specification</fo:block>  
					            </fo:table-cell>
					             <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="center" font-size="17pt" white-space-collapse="false" font-weight="bold">UOM</fo:block>  
					            </fo:table-cell>
							</fo:table-row>
      					</fo:table-body>
      				</fo:table>
          			</fo:block>
          			
          		</fo:static-content>	        	
	        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
        			<fo:block>
        				<fo:table>
		                    <fo:table-column column-width="10%"/>
		                    <fo:table-column column-width="35%"/>
		                    <fo:table-column column-width="40%"/>
		                    <fo:table-column column-width="15%"/>
		                    <fo:table-body>
       							
								<#if categoryWiseProductsMap?has_content>
      								<#assign categoryWiseProductsList=categoryWiseProductsMap.entrySet()>
                                <#list categoryWiseProductsList as categoryWiseProducts>
							    <#assign ProductsList=categoryWiseProducts.getValue()>
								<#assign productcategory=categoryWiseProducts.getKey()>
								<fo:table-row>
									<fo:table-cell number-columns-spanned="4">
							            	<fo:block  keep-together="always" text-align="left" font-size="15pt" white-space-collapse="false" font-weight="bold">Product Category:  ${productcategory?if_exists}</fo:block>  
							        </fo:table-cell>
								</fo:table-row>
								<#list ProductsList as Product>
								<#assign uomDetails = delegator.findOne("Uom", {"uomId" : Product.quantityUomId}, false)>
								<fo:table-row>
					                    <fo:table-cell border-style="solid">
							            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" >${Product.productId?if_exists}</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell border-style="solid">
							            	<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >${Product.productName?if_exists}</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell border-style="solid">
							            	<fo:block   text-align="left" font-size="12pt" white-space-collapse="false">${Product.longDescription?if_exists}</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell border-style="solid">
							            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">${uomDetails.description?if_exists}</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
								 </#list>
							     </#list>
								 </#if>  
									
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