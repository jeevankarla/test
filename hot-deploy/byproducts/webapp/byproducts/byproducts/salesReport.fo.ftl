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
	<fo:simple-page-master master-name="main" page-height="10in" page-width="12in"  margin-bottom=".3in" margin-left=".3in" margin-right="1in">
        <fo:region-body margin-top="1in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "salesReport.txt")}
 <#if grandProdTotals?has_content> 
<fo:page-sequence master-reference="main" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
					<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">&#160;      ${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">&#160;      ${uiLabelMap.KMFDairySubHeader}</fo:block>
                    <fo:block text-align="left" font-size="12pt" keep-together="always"  white-space-collapse="false">&#160;        Sales  Report From :: ${effectiveDateStr?if_exists}  To:: ${thruEffectiveDateStr?if_exists}</fo:block>
              		<fo:block>----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            		<fo:block font-size="8pt">&#160;&#160;Product    								&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;TotQty  					&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;TotQty(Ltrs/Kgs)  			&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; 	TotAmount  			&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;		RtrnQty  			&#160;&#160;&#160;&#160; RtrnQty(Ltrs/Kgs)     &#160;&#160;&#160;&#160;TotRetnAmnt				 &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;NetTotal</fo:block>
            		<fo:block>------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
                 	<fo:table>
                    <fo:table-column column-width="80pt"/>
                    <fo:table-column column-width="70pt"/>
                    <fo:table-column column-width="100pt"/> 
               	    <fo:table-column column-width="130pt"/>
            		<fo:table-column column-width="100pt"/> 		
            		<fo:table-column column-width="100pt"/>
            		<fo:table-column column-width="130pt"/>
            		<fo:table-column column-width="110pt"/>
            		<fo:table-column column-width="76pt"/>
                    <fo:table-body>
                    	<#assign grandNetTotal = 0>
                    	<#assign grandTotalQty = 0>
                    	<#assign grandTotalQtyLtrs = 0>
                    	<#assign grandTotalRevenue = 0>
                    	<#assign grandTotalRtrnQty = 0>
                    	<#assign grandTotalRtrnQtyLtrs = 0>
                    	<#assign grandTotalRetrnQtyRevenue = 0>
                    	<#assign productDetails = grandProdTotals.entrySet()>
                    	<#list productDetails as prodTotals>
		                        <#assign product = delegator.findOne("Product", {"productId" : prodTotals.getKey()}, true)?if_exists/>
								<fo:table-row>
                    				<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="left"  white-space-collapse="false">${product.brandName}</fo:block>  
	                       			</fo:table-cell>
	                       			<#assign quantity = (prodTotals.getValue().get("packetQuantity"))?if_exists>
	                       			<#assign grandTotalQty = grandTotalQty+quantity>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="right"  white-space-collapse="false">${quantity?if_exists?string("#0.00")}</fo:block>  
	                       			</fo:table-cell>
	                       			<#assign qtyLtrsKgs = (prodTotals.getValue().get("total"))?if_exists>
	                       			<#assign grandTotalQtyLtrs = grandTotalQtyLtrs+qtyLtrsKgs>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="right"  white-space-collapse="false">${qtyLtrsKgs?if_exists?string("#0.00")}</fo:block>  
	                       			</fo:table-cell>
	                       			<#assign revenue = (prodTotals.getValue().get("totalRevenue"))?if_exists>
	                       			<#assign grandTotalRevenue = grandTotalRevenue+revenue>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="right"  white-space-collapse="false">${revenue?if_exists?string("#0.00")}</fo:block>  
	                       			</fo:table-cell>
	                       			<#assign returnPrice = 0>
	                       			<#assign retrnQtyAmount=0>
	                       			<#assign totalNetPrice = 0>
                   					<#assign returnQty = (productReturnMap[product.productId].get("returnQuantity"))?if_exists>
	                       			<#if returnQty?has_content>
		                       			<#assign grandTotalRtrnQty = grandTotalRtrnQty+returnQty?if_exists>
			                    	</#if>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="right"  white-space-collapse="false"><#if returnQty?has_content>${returnQty?if_exists?string("#0.00")}<#else></#if></fo:block>  
	                       			</fo:table-cell>
	                       			<#assign returnQtyLtrs = (productReturnMap[product.productId].get("returnQtyLtrs"))?if_exists>
	                       			<#if returnQtyLtrs?has_content>
		                       			<#assign grandTotalRtrnQtyLtrs = grandTotalRtrnQtyLtrs+returnQtyLtrs?if_exists>
			                    	</#if>
                       			    <fo:table-cell>
	                            		<fo:block  text-align="right"  white-space-collapse="false"><#if returnQty?has_content>${returnQtyLtrs?if_exists?string("#0.00")}<#else></#if></fo:block>  
	                       			</fo:table-cell>
	                       			<#assign returnPrice = (productReturnMap[product.productId].get("returnPrice"))?if_exists>
	                       			<#assign retrnQtyAmount=0>
	                       			<#assign totalNetPrice = 0>
	                       			<#if returnQty?has_content>
                       			    	<#assign grandTotalRetrnQtyRevenue = grandTotalRetrnQtyRevenue+returnPrice?if_exists>
                       			    </#if>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="right"  white-space-collapse="false"><#if returnQty?has_content>${returnPrice?if_exists?string("#0.00")}<#else></#if></fo:block>  
	                       			</fo:table-cell>
                       				<#if returnQty?has_content>
                       					<#assign totalNetPrice = (revenue-returnPrice)>
                       				<#else>	
                       					<#assign totalNetPrice = revenue>
                       				</#if>
		                       		
                       				<#assign grandNetTotal = grandNetTotal+totalNetPrice>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="right"  white-space-collapse="false">${totalNetPrice?if_exists?string("#0.00")}</fo:block>  
	                       			</fo:table-cell>
                				</fo:table-row>
                			</#list>
                			<fo:table-row>
			                   <fo:table-cell>
			                        	<fo:block>-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			                   </fo:table-cell>
			               </fo:table-row>
                			<fo:table-row>
               					<fo:table-cell>
                        			<fo:block>
		                        	 <fo:table>
									   	 <fo:table-column column-width="80pt"/>
					                     <fo:table-column column-width="70pt"/>
					                     <fo:table-column column-width="100pt"/> 
					               	     <fo:table-column column-width="130pt"/>
					            		 <fo:table-column column-width="100pt"/> 		
					            		 <fo:table-column column-width="100pt"/>
					            		 <fo:table-column column-width="130pt"/>
					            		 <fo:table-column column-width="110pt"/>
					            		 <fo:table-column column-width="76pt"/>
										 <fo:table-body>
							                <fo:table-row>
						                  		 <fo:table-cell>
						                        	<fo:block  keep-together="always" text-align="left" white-space-collapse="false">GrandTotal</fo:block>
						                   		</fo:table-cell>
						                      <fo:table-cell>
						                        	<fo:block text-align="right" white-space-collapse="false">${grandTotalQty?if_exists?string("#0.00")}</fo:block>
						                      </fo:table-cell>
						                      <fo:table-cell>
						                        	<fo:block text-align="right" white-space-collapse="false">${grandTotalQtyLtrs?if_exists?string("#0.00")}</fo:block>
						                      </fo:table-cell>
						                      <fo:table-cell>
						                        	<fo:block text-align="right" white-space-collapse="false">${grandTotalRevenue?if_exists?string("#0.00")}</fo:block>
						                      </fo:table-cell>
						                      <fo:table-cell>
						                        	<fo:block text-align="right" white-space-collapse="false">${grandTotalRtrnQty?if_exists?string("#0.00")}</fo:block>
						                      </fo:table-cell>
						                      <fo:table-cell>
						                        	<fo:block text-align="right" white-space-collapse="false">${grandTotalRtrnQtyLtrs?if_exists?string("#0.00")}</fo:block>
						                      </fo:table-cell>
						                      <fo:table-cell>
						                        	<fo:block text-align="right" white-space-collapse="false">${grandTotalRetrnQtyRevenue?if_exists?string("#0.00")}</fo:block>
						                      </fo:table-cell>
						                      <fo:table-cell>
						                        	<fo:block text-align="right" white-space-collapse="false">${grandNetTotal?if_exists?string("#0.00")}</fo:block>
						                      </fo:table-cell>
							               </fo:table-row>
					            </fo:table-body>
           				   	</fo:table> 
           				</fo:block>
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