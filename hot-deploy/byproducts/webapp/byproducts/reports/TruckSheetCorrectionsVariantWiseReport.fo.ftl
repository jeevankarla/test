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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="8in"  margin-bottom="0.3in" margin-left=".3in" margin-right="1in">
        <fo:region-body margin-top="0.2in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "trCorrection.txt")}
 <#if variantProductMap?has_content> 
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
				<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"> &#160;${uiLabelMap.CommonPage}- <fo:page-number/></fo:block>
			 </fo:static-content>	
			 <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
					<fo:block  keep-together="always" text-align="left" font-weight = "bold" font-family="Courier,monospace" white-space-collapse="false">&#160;      ${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="left" font-weight = "bold" font-family="Courier,monospace" white-space-collapse="false">&#160;      ${uiLabelMap.KMFDairySubHeader}</fo:block>
                    <fo:block text-align="center" font-size="11pt" keep-together="always"  font-weight = "bold" white-space-collapse="false">TruckSheet Corrections Variant Report :: ${effectiveDateStr?if_exists}</fo:block>
              		<fo:block font-size="10pt">-------------------------------------------------------------------------------------</fo:block>
              		<fo:block>
	                 	<fo:table>
	                    <fo:table-column column-width="90pt"/>
	                    <fo:table-column column-width="90pt"/>
	                    <fo:table-column column-width="90pt"/> 
	               	    <fo:table-column column-width="100pt"/>
	            		<fo:table-column column-width="110pt"/> 		
	                    <fo:table-body>
	                    <fo:table-row font-weight = "bold">
                				<fo:table-cell>
                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">Product</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false"></fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false"></fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                            		<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">Original</fo:block>
                            		<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">Quantity</fo:block>    
                       			</fo:table-cell>
                       			<fo:table-cell>
                            		<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">Revised</fo:block>
                            		<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">Quantity</fo:block>    
                       			</fo:table-cell>
                			</fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block> 		
              <fo:block font-size="10pt">-------------------------------------------------------------------------------------</fo:block>
            	<fo:block>
                 	<fo:table>
                    <fo:table-column column-width="90pt"/>
                    <fo:table-column column-width="90pt"/>
                    <fo:table-column column-width="70pt"/> 
               	    <fo:table-column column-width="100pt"/>
            		<fo:table-column column-width="110pt"/>  		
                    <fo:table-body>
        			   <#assign grandTotOrignalQty = 0>
                       <#assign grandTotRevisedQty = 0>
                    	
                    	<#assign truckSheetCorrectionList = variantProductMap.entrySet()>
                    	<#list truckSheetCorrectionList as truckSheetCorrectionItem>
                    	<#assign product = delegator.findOne("Product", {"productId" : truckSheetCorrectionItem.getKey()}, true)?if_exists/>
								
		                       <#assign totOrignalQty = 0>
		                       <#assign totRevisedQty = 0>
								<#assign productValues = truckSheetCorrectionItem.getValue()>
								
								<#list productValues as correctionItem>
								<#assign orginalQuantity = correctionItem.get("orginalQuantity")>
	                        	<#assign revisedQuantity = correctionItem.get("revisedQuantity")>
	                        	<#assign totOrignalQty = totOrignalQty+orginalQuantity>
	                        	<#assign totRevisedQty = totRevisedQty+revisedQuantity>
	                        	
	                        		<#--<fo:table-row>
	                    				<fo:table-cell>
	                            			<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(correctionItem.get("Date"), "dd-MMM-yyyy")}</fo:block>  
		                       			</fo:table-cell>
		                       			<fo:table-cell>
		                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${correctionItem.get("routeId")?if_exists}</fo:block>  
		                       			</fo:table-cell>
		                       			<fo:table-cell>
		                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${correctionItem.get("boothId")?if_exists}</fo:block>  
		                       			</fo:table-cell>
		                       			<#assign totOrignalQty = totOrignalQty+orginalQuantity>
		                       			<fo:table-cell>
		                            		<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${correctionItem.get("orginalQuantity")?if_exists}</fo:block>  
		                       			</fo:table-cell>
		                       			<#assign totRevisedQty = totRevisedQty+revisedQuantity>
		                       			<fo:table-cell>
		                            		<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${correctionItem.get("revisedQuantity")?if_exists}</fo:block>  
		                       			</fo:table-cell>
	                				</fo:table-row>-->
                				</#list>
                				<#--<fo:table-row>
	                        			<fo:table-cell>
	                            			<fo:block font-size="10pt">-------------------------------------------------------------------------------------</fo:block>
	                        			</fo:table-cell>
		                       </fo:table-row>-->
		                       	<#if totOrignalQty != totRevisedQty>
		                       <fo:table-row>
	                       			<fo:table-cell>
                            			<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false">${product.description?if_exists}(${product.brandName?if_exists})</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false"></fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false"></fo:block>  
	                       			</fo:table-cell>
	                       			<#assign grandTotOrignalQty = grandTotOrignalQty+totOrignalQty>
	                       			<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false">${totOrignalQty?if_exists}</fo:block>  
	                       			</fo:table-cell>
	                       			<#assign grandTotRevisedQty = grandTotRevisedQty+totRevisedQty>
	                       			<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false">${totRevisedQty?if_exists}</fo:block>  
	                       			</fo:table-cell>
		                    </fo:table-row>
		                 	<fo:table-row>
                    			<fo:table-cell>
                        			<fo:block font-size="10pt">-------------------------------------------------------------------------------------</fo:block>
                    			</fo:table-cell>
		                    </fo:table-row> 
		                    </#if>     
                		</#list>
                		<#--<fo:table-row font-weight = "bold">
                   			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight = "bold" font-size="12pt" white-space-collapse="false">Grand Total</fo:block>  
                   			</fo:table-cell>
                   			<fo:table-cell>
                        		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false"></fo:block>  
                   			</fo:table-cell>
                   			<fo:table-cell>
                        		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false"></fo:block>  
                   			</fo:table-cell>
                   			<fo:table-cell>
                        		<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${grandTotOrignalQty?if_exists}</fo:block>  
                   			</fo:table-cell>
                   			<fo:table-cell>
                        		<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${grandTotRevisedQty?if_exists}</fo:block>  
                   			</fo:table-cell>
		                </fo:table-row>
		                <fo:table-row>
                			<fo:table-cell>
                    			<fo:block font-size="10pt">-------------------------------------------------------------------------------------</fo:block>
                			</fo:table-cell>
		                </fo:table-row>-->
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