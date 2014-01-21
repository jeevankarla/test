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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in" margin-top=".5in" margin-left=".5in" margin-right=".5in" >
                <fo:region-body margin-top="1.5in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
     </fo:layout-master-set>

<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
			
					<fo:block text-align="center" keep-together="always" white-space-collapse="false"  font-size="14pt"  font-weight="bold">SUPRAJA DAIRY PRIVATE LIMITED : VISAKHAPATNAM</fo:block>
                    <fo:block text-align="center" white-space-collapse="false" font-weight="bold" font-size="10pt" keep-together="always">&#160;</fo:block>
                    <fo:block text-align="center"  white-space-collapse="false" font-weight="bold" font-size="10pt" keep-together="always">SRI SATYA, 6-18-3/3, SRI SAI GYANA MANDIR STREET, EAST POINT, COLONY, VISAKHAPATNAM – 530017</fo:block>
                    <fo:block text-align="center" white-space-collapse="false" font-weight="bold" font-size="10pt" keep-together="always">&#160;</fo:block>
                    <fo:block text-align="center" font-size="12pt" keep-together="always" font-weight="bold"  white-space-collapse="false"><fo:inline text-decoration="underline">RECONSILATION REPORT </fo:inline> : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(effectiveDate, "dd/MM/yyyy")}</fo:block>
              		<fo:block font-size="10pt">---------------------------------------------------------------------------------------------------------</fo:block>
              		<fo:block font-size="10pt" white-space-collapse="false">Product Code     Product Name               Dispatch Qty    Delivered Qty    Diff.Qty      </fo:block>
              		<fo:block font-size="10pt">---------------------------------------------------------------------------------------------------------</fo:block>
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
            	<#if routeProdDispatchAndDeliveredMap?has_content>
     			<#assign routeTotalList =routeProdDispatchAndDeliveredMap.entrySet()>
     				 <#list routeTotalList as routeTotal>
            	  <fo:block text-align="left" font-size="12pt" keep-together="always" font-weight="bold"  white-space-collapse="false">ROUTE:<fo:inline text-decoration="underline">${routeTotal.getKey()} </fo:inline> </fo:block>
            		<#assign productDetailList = routeTotal.getValue().entrySet()>
                 	<fo:table border-width="1pt" >
                    <fo:table-column column-width="100pt"/>
                    <fo:table-column column-width="115pt"/> 
               	    <fo:table-column column-width="90pt"/>
            		<fo:table-column column-width="90pt"/> 		
            		<fo:table-column column-width="105pt"/>
            		<fo:table-column column-width="80pt"/>
                    <fo:table-body>
                    	<#list productDetailList as productDetail>
                    		<#assign dispatchQty = (productDetail.getValue()).get("dispatchQty")>
	                        <#assign deliveredQty = (productDetail.getValue()).get("deliveredQty")>
	                      <#if dispatchQty != 0>
		                        <#assign product = delegator.findOne("Product", {"productId" : productDetail.getKey()}, true)>
								<fo:table-row>
                    				<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">${productDetail.getKey()}</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${product.productName?if_exists}</fo:block>  
	                       			</fo:table-cell>
	                        		<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${dispatchQty}</fo:block>  
	                        		</fo:table-cell>
	                        		<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${deliveredQty}</fo:block>  
	                        		</fo:table-cell>
	                        		<#assign diffQty = deliveredQty-dispatchQty>
	                        		 <#if deliveredQty != 0 && dispatchQty != 0>
	                        		<#assign percentDiff = Static["java.lang.Integer"].valueOf((diffQty/dispatchQty)*100)>
	                        		<#else>
	                        		<#assign percentDiff = 0>
	                        		 </#if>
	                        		<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${diffQty?if_exists}</fo:block>  
	                       			</fo:table-cell>
	                       			<#-- <fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${percentDiff?if_exists}</fo:block>  
	                       			</fo:table-cell>-->
                				</fo:table-row>
							</#if>
                		</#list>
                    </fo:table-body>
                </fo:table>
                <fo:block font-size="10pt">---------------------------------------------------------------------------------------------------------</fo:block>
                 </#list>
               </#if>
               </fo:block> 	
			 </fo:flow>
			 </fo:page-sequence>	
</fo:root>
</#escape>