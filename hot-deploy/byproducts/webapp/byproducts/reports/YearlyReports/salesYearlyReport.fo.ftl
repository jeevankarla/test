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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-bottom=".1in" margin-left=".1in" margin-right=".3in">
        <fo:region-body margin-top="1.5in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "salesReport.txt")}
 <#if saleProductTotals?has_content> 
<fo:page-sequence master-reference="main" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
					<fo:block  keep-together="always" text-align="center" font-weight="bold" font-family="Courier,monospace" white-space-collapse="false">      ${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-weight="bold" font-family="Courier,monospace" white-space-collapse="false">      ${uiLabelMap.KMFDairySubHeader}</fo:block>
					<fo:block  text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold">Route Marketing Channel</fo:block>
                    <fo:block text-align="center"  font-weight="bold" keep-together="always"  font-family="Courier,monospace" white-space-collapse="false">        Sales  Report From :: ${fromDate?if_exists}  To:: ${thruDate?if_exists}</fo:block>
                     <fo:block  keep-together="always"  text-align="left" font-family="Courier,monospace" white-space-collapse="false">UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
              		<fo:block>----------------------------------------------------------------------------------------------------------------------------------</fo:block>
            		<fo:block  font-weight="bold" font-size="12pt">Product    			&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;      DspQty  		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;DspQty(L/K)  	&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;DspAmt  		&#160;&#160;&#160;RtnQty   RtnQty(L/K)  &#160; &#160;RtnAmt  &#160;NetQty(L/K) &#160;&#160;&#160; &#160;&#160;&#160;NetAmt  </fo:block>
            		<fo:block>----------------------------------------------------------------------------------------------------------------------------------</fo:block>
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
                 	<fo:table>
                    <fo:table-column column-width="110pt"/>
                    <fo:table-column column-width="90pt"/>
                    <fo:table-column column-width="145pt"/> 
               	    <fo:table-column column-width="158pt"/>
            		<fo:table-column column-width="72pt"/> 		
            		<fo:table-column column-width="72pt"/>
            		<fo:table-column column-width="85pt"/>
            		<fo:table-column column-width="90pt"/>
            		<fo:table-column column-width="110pt"/>
                    <fo:table-body>
                    	<#assign grandNetTotal = 0>
                    	<#assign grandTotalQty = 0>
                    	<#assign grandTotalQtyLtrs = 0>
                    	<#assign grandTotalRevenue = 0>
                    	<#assign grandTotalRtrnQty = 0>
                    	<#assign grandTotalRtrnQtyLtrs = 0>
                    	<#assign grandTotalRetrnQtyRevenue = 0>
                    	<#assign productDetails = saleProductTotals.entrySet()>
                    	<#list productDetails as prodTotals>
		                        <#assign product = delegator.findOne("Product", {"productId" : prodTotals.getKey()}, true)?if_exists/>
		                        <#assign netquantity = (prodTotals.getValue().get("packetQuantity"))?if_exists>
		                        <#assign supplyTypeTotals = (prodTotals.getValue().get("supplyTypeTotals"))?if_exists>
		                        <#assign subsidyMilk = supplyTypeTotals.get("EMP_SUBSIDY")?if_exists>
		                        
		                            <#assign returnQty=0>
		                            <#assign returnQtyLtrs=0>
		                            <#assign returnPrice = 0>
	                       			<#assign retrnQtyAmount=0>
	                       			<#assign totalNetPrice = 0>
									<#assign netQty = 0>
	                       		    <#assign netqtyLtrsKgs = (prodTotals.getValue().get("total"))?if_exists>
	                       			<#assign revenue = (prodTotals.getValue().get("totalRevenue"))?if_exists>
									<#assign netQty = netqtyLtrsKgs?if_exists>
									<#assign totalNetPrice =revenue?if_exists>
		                        <#if (netquantity != 0)>
									<#-- handling returns here -->
	                       			<#assign returnDetailsMap = supplyTypeTotals.get("_NA_")?if_exists>
	                       			
									<#if returnDetailsMap?has_content>
                   					<#assign returnQty = -(returnDetailsMap.get("packetQuantity"))?if_exists>
									<#assign netquantity =netquantity+returnQty?if_exists>
	                       			<#if returnQty?has_content>
		                       			<#assign grandTotalRtrnQty = grandTotalRtrnQty+returnQty?if_exists>
			                    	</#if>
			                    	
			                    	<#assign returnQtyLtrs = -(returnDetailsMap.get("total"))?if_exists>
									<#assign netqtyLtrsKgs = netqtyLtrsKgs+returnQtyLtrs?if_exists>
	                       			<#if returnQtyLtrs?has_content>
		                       			<#assign grandTotalRtrnQtyLtrs = grandTotalRtrnQtyLtrs+returnQtyLtrs?if_exists>
		                       		
			                    	</#if>
			                    	
			                    	<#assign returnPrice = -(returnDetailsMap.get("totalRevenue"))?if_exists>
									<#assign revenue =revenue+returnPrice?if_exists>
	                       			<#if returnQty?has_content>
                       			    	<#assign grandTotalRetrnQtyRevenue = grandTotalRetrnQtyRevenue+returnPrice?if_exists>
                       			    </#if>
                       				<#assign totalNetPrice = (totalNetPrice+returnPrice-returnPrice)>
                       			    
			                    	</#if>

                                   
                                    
	                       			<#assign grandTotalQty = grandTotalQty+netquantity>
	                       			<#assign grandTotalQtyLtrs = grandTotalQtyLtrs+netqtyLtrsKgs>

								<fo:table-row>
                    				<fo:table-cell>
	                            		<fo:block  keep-together="always" font-size="12pt" text-align="left"  white-space-collapse="false">${product.brandName}</fo:block>  
	                       			</fo:table-cell>
	                       
	                       			 <#if subsidyMilk?has_content>
		                       			 <#assign subQty = subsidyMilk.packetQuantity?if_exists>
		                       			 <#assign netSubQty = netquantity-subQty>
	                       			 <fo:table-cell>
	                            		<fo:block  text-align="right" font-size="12pt" white-space-collapse="false">${netSubQty?if_exists?string("#0.00")}/${subQty?if_exists?string("#0.00")}*</fo:block>  
	                       			</fo:table-cell>
	                       			<#else>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="right"  font-size="12pt" white-space-collapse="false">${netquantity?if_exists?string("#0.00")}</fo:block>  
	                       			</fo:table-cell>
	                       			</#if>

	                       			<#if subsidyMilk?has_content>
		                       			 <#assign subQtyLtrs = subsidyMilk.total?if_exists>
		                       			 <#assign netSubQtyLtrs = netqtyLtrsKgs-subQtyLtrs>
	                       			 <fo:table-cell>
	                            		<fo:block  text-align="right"  font-size="12pt" white-space-collapse="false">${netSubQtyLtrs?if_exists?string("#0.00")}/${subQtyLtrs?if_exists?string("#0.00")}</fo:block>  
	                       			</fo:table-cell>
	                       			<#else>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="right"  font-size="12pt" white-space-collapse="false">${netqtyLtrsKgs?if_exists?string("#0.00")}</fo:block>  
	                       			</fo:table-cell>
	                       			</#if>

	                       			<#assign grandTotalRevenue = grandTotalRevenue+revenue>
	                       			<#if subsidyMilk?has_content>
		                       			 <#assign subRevenue = subsidyMilk.totalRevenue?if_exists>
		                       			 <#assign netSubRevenue = revenue-subRevenue>
	                       			 <fo:table-cell>
	                            		<fo:block  text-align="right"  font-size="12pt" white-space-collapse="false">${netSubRevenue?if_exists?string("#0.00")}/${subRevenue?if_exists?string("#0.00")}</fo:block>  
	                       			</fo:table-cell>
	                       			<#else>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="right"  font-size="12pt" white-space-collapse="false">${revenue?if_exists?string("#0.00")}</fo:block>  
	                       			</fo:table-cell>
	                       			</#if>
	                       			
	                       			
	                       			<fo:table-cell>
	                            		<fo:block  text-align="right"  font-size="12pt" white-space-collapse="false"><#if returnQty?has_content>${returnQty?if_exists?string("#0.00")}<#else></#if></fo:block>  
	                       			</fo:table-cell>
	                       			
                       			    <fo:table-cell>
	                            		<fo:block  text-align="right" font-size="12pt" white-space-collapse="false"><#if returnQty?has_content>${returnQtyLtrs?if_exists?string("#0.00")}<#else></#if></fo:block>  
	                       			</fo:table-cell>
	                       			
	                       			<fo:table-cell>
	                            		<fo:block  text-align="right" font-size="12pt" white-space-collapse="false"><#if returnQty?has_content>${returnPrice?if_exists?string("#0.00")}<#else></#if></fo:block>  
	                       			</fo:table-cell>
                       				
		                       		<fo:table-cell>
	                            		<fo:block  text-align="right"  font-size="12pt" white-space-collapse="false">${netQty?if_exists?string("#0.00")}</fo:block>  
	                       			</fo:table-cell>
                       				<#assign grandNetTotal = grandNetTotal+totalNetPrice>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="right" font-size="12pt" white-space-collapse="false">${totalNetPrice?if_exists?string("#0.00")}</fo:block>  
	                       			</fo:table-cell>
                				</fo:table-row>
                				</#if>
                			</#list>
                			<fo:table-row>
			                   <fo:table-cell>
			                        		<fo:block>---------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			                   </fo:table-cell>
			               </fo:table-row>
                			<fo:table-row>
               					<fo:table-cell>
                        			<fo:block>
		                        	 <fo:table>
					                   <fo:table-column column-width="110pt"/>
					                    <fo:table-column column-width="90pt"/>
					                    <fo:table-column column-width="145pt"/> 
					               	    <fo:table-column column-width="158pt"/>
					            		<fo:table-column column-width="72pt"/> 		
					            		<fo:table-column column-width="72pt"/>
					            		<fo:table-column column-width="85pt"/>
					            		<fo:table-column column-width="90pt"/>
					            		<fo:table-column column-width="110pt"/>
										 <fo:table-body>
							                <fo:table-row>
						                  		 <fo:table-cell>
						                        	<fo:block  keep-together="always" font-size="12pt" text-align="left" white-space-collapse="false">GrandTotal</fo:block>
						                   		</fo:table-cell>
						                      <fo:table-cell>
						                        	<fo:block text-align="right" font-size="12pt" white-space-collapse="false"></fo:block>
						                      </fo:table-cell>
						                      <fo:table-cell>
						                        	<fo:block text-align="right" font-size="12pt" white-space-collapse="false"></fo:block>
						                      </fo:table-cell>
						                      <fo:table-cell>
						                        	<fo:block text-align="right" font-size="12pt" white-space-collapse="false">${grandTotalRevenue?if_exists?string("#0.00")}</fo:block>
						                      </fo:table-cell>
						                      <fo:table-cell>
						                        	<fo:block text-align="right" font-size="12pt" white-space-collapse="false"></fo:block>
						                      </fo:table-cell>
						                      <fo:table-cell>
						                        	<fo:block text-align="right" font-size="12pt" white-space-collapse="false"></fo:block>
						                      </fo:table-cell>
						                      <fo:table-cell>
						                        	<fo:block text-align="right" font-size="12pt" white-space-collapse="false">${grandTotalRetrnQtyRevenue?if_exists?string("#0.00")}</fo:block>
						                      </fo:table-cell>
						                      <fo:table-cell>
						                        	<fo:block text-align="right" font-size="12pt" white-space-collapse="false"></fo:block>
						                      </fo:table-cell>
						                      <fo:table-cell>
						                        	<fo:block text-align="right" font-size="12pt" white-space-collapse="false">${grandNetTotal?if_exists?string("#0.00")}</fo:block>
						                      </fo:table-cell>
							               </fo:table-row>
							               <fo:table-row>
							                   <fo:table-cell>
							                        		<fo:block>---------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
         									   </fo:table-cell>
			               					</fo:table-row>
							               <fo:table-row>
						                  		 <fo:table-cell>
						                        	<fo:block  keep-together="always" font-size="14pt" font-style="italic">*Subsidy milk quantity.</fo:block>
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