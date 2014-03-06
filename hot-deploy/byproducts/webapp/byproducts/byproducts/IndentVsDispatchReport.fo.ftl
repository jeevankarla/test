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
	<fo:simple-page-master master-name="main" page-height="9in" page-width="12in"  margin-bottom="1in" margin-left=".3in" margin-right="1in">
        <fo:region-body margin-top="1.1in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "IndentVsDispatch.txt")}
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
					<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">&#160;      ${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">&#160;      ${uiLabelMap.KMFDairySubHeader}</fo:block>
                    <fo:block text-align="left" font-size="10pt" keep-together="always"  white-space-collapse="false">&#160;                                          Indent vs Despatch Report :: ${reportDate?if_exists}</fo:block>
              		<fo:block font-size="10pt">----------------------------------------------------------------------------------------------------------------------------------------</fo:block>
              		<fo:block font-size="10pt" white-space-collapse="false">   Product Code    Product Name              Capacity  Indent Qty   Despatch Qty   Diff. Qty     Diff.(%)</fo:block>
              		<fo:block font-size="10pt">----------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
            		<#assign productDetailList = (prodIndentAndDispatchMap).entrySet()>
                 	<fo:table>
                    <fo:table-column column-width="90pt"/>
                    <fo:table-column column-width="140pt"/>
                    <fo:table-column column-width="70pt"/> 
               	    <fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="70pt"/> 		
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="80pt"/>
                    <fo:table-body>
                    	<#assign lineNo = 0>
                    	<#list productDetailList as productDetail>
                    		<#assign indentQty = (productDetail.getValue()).get("indentQty")>
	                        <#assign dispatchQty = (productDetail.getValue()).get("dispatchQty")>
	                        <#if indentQty == 0 && dispatchQty == 0>
	                        <#else>
	                        	<#assign lineNo = lineNo + 1>
		                        <#assign product = delegator.findOne("Product", {"productId" : productDetail.getKey()}, true)>
		                        <#--assign productUom = delegator.findOne("Uom", {"uomId" : product.quantityUomId}, true)>-->
		                        <#assign productFacility = delegator.findOne("ProductFacility", {"productId" : productDetail.getKey(),"facilityId" : "STORE"}, true)>
								<fo:table-row>
                    				<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">&#160;    ${productDetail.getKey()}</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${product.productName?if_exists}</fo:block>  
	                       			</fo:table-cell>
	                       			<#--fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${productUom.description?if_exists}</fo:block>  
	                       			</fo:table-cell-->
	                       			<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${productFacility.capacity?if_exists}</fo:block>  
	                       			</fo:table-cell>
	                        		<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${indentQty}</fo:block>  
	                        		</fo:table-cell>
	                        		<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${dispatchQty}</fo:block>  
	                        		</fo:table-cell>
	                        		<#assign diffQty = dispatchQty-indentQty>
	                        		<#if indentQty==0>
	                        		<#assign indentQty=diffQty>
	                        		</#if>
	                        		<#assign percentDiff = Static["java.lang.Integer"].valueOf((diffQty/indentQty)*100)>
	                        		<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${diffQty?if_exists}</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${percentDiff?if_exists}</fo:block>  
	                       			</fo:table-cell>
                				</fo:table-row>
                				<#if (lineNo >= 35)>
	                    			<fo:table-row>
	                   	     			<fo:table-cell>
		    	                        	<fo:block font-size="7pt" page-break-after="always"></fo:block>        
				                        </fo:table-cell>
		        		            </fo:table-row>
		        		            <#assign lineNo = 0 >
                    			</#if>
							</#if>	
                		</#list>
                    </fo:table-body>
                </fo:table>
               </fo:block> 		
			 </fo:flow>
			 </fo:page-sequence>	
</fo:root>
</#escape>