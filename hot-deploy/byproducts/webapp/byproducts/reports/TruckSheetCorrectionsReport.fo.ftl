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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="12in"  margin-bottom="1in" margin-left=".3in" margin-right="1in">
        <fo:region-body margin-top="1.3in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "trCorrection.txt")}
 <#if truckSheetCorrectionList?has_content> 
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
					<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">&#160;      ${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">&#160;      ${uiLabelMap.KMFDairySubHeader}</fo:block>
                    <fo:block text-align="left" font-size="12pt" keep-together="always"  white-space-collapse="false">&#160;                      TruckSheet Corrections Report :: ${effectiveDateStr?if_exists}</fo:block>
              		<fo:block font-size="10pt">----------------------------------------------------------------------------------------------------------------------------------------</fo:block>
              		<fo:block>
	                 	<fo:table>
	                    <fo:table-column column-width="90pt"/>
	                    <fo:table-column column-width="90pt"/>
	                    <fo:table-column column-width="70pt"/> 
	               	    <fo:table-column column-width="70pt"/>
	            		<fo:table-column column-width="70pt"/> 		
	            		<fo:table-column column-width="70pt"/>
	            		<fo:table-column column-width="80pt"/>
	            		<fo:table-column column-width="100pt"/>
	            		<fo:table-column column-width="80pt"/>
	                    <fo:table-body>
	                    <fo:table-row>
                				<fo:table-cell>
                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">&#160;Date</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">RouteNumber</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">Retailer</fo:block>
                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">Code</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">Product</fo:block> 
                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">Code</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                            		<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">Orginal</fo:block>  
                            		<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">Quantity</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                            		<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">Revised&#160;</fo:block>  
                            		<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">Quantity&#160;</fo:block>  
                       			</fo:table-cell>
                        		<fo:table-cell>
                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">&#160;&#160;Created By</fo:block>
                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">&#160;&#160;User Name</fo:block>   
                        		</fo:table-cell>
                        		<fo:table-cell>
                            		<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">Created Date</fo:block>  
                        		</fo:table-cell>
                			</fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block> 		
              <fo:block font-size="10pt">----------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
                 	<fo:table>
                     <fo:table-column column-width="90pt"/>
                    <fo:table-column column-width="90pt"/>
                    <fo:table-column column-width="70pt"/> 
               	    <fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="70pt"/> 		
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="80pt"/>
            		<fo:table-column column-width="100pt"/>
            		<fo:table-column column-width="80pt"/>
                    <fo:table-body>
                    	<#assign lineNo = 0>
                    	<#list truckSheetCorrectionList as correctionItem>
                    		<#assign orginalQuantity = correctionItem.get("orginalQuantity")>
	                        <#assign revisedQuantity = correctionItem.get("revisedQuantity")>
	                        <#if orginalQuantity == 0 && revisedQuantity == 0>
	                        <#else>
	                        	<#assign lineNo = lineNo + 1>
		                        <#assign product = delegator.findOne("Product", {"productId" : correctionItem.get("productId")}, true)?if_exists/>
		                        <#--assign productUom = delegator.findOne("Uom", {"uomId" : product.quantityUomId}, true)>-->
								<fo:table-row>
                    				<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">&#160;${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(correctionItem.get("Date"), "dd-MMM-yyyy")}</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">&#160;${correctionItem.get("routeId")}</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">&#160;${correctionItem.get("boothId")}</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${product.brandName?if_exists}</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${correctionItem.get("orginalQuantity")?if_exists}</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${correctionItem.get("revisedQuantity")?if_exists}&#160;</fo:block>  
	                       			</fo:table-cell>
	                        		<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">&#160;&#160;${correctionItem.get("userLoginId")?if_exists}</fo:block>  
	                        		</fo:table-cell>
	                        		<#if correctionItem.get("changedDate")?has_content>
	                        		<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">&#160;${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(correctionItem.get("changedDate"), "dd-MMM-yyyy")}</fo:block>  
	                        		</fo:table-cell>
	                        		<#else>
	                        		<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false"></fo:block>  
	                        		</fo:table-cell>
	                        		</#if>
	                        		<#--
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
	                       			</fo:table-cell> -->
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