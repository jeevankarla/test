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
                <fo:region-body margin-top=".9in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "ChannelWiseSales(basicValue).txt")}
       <#if periodTotalsMap?has_content>        
		        
		        <fo:page-sequence master-reference="main" font-size="10pt">	
		        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
		        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">     </fo:block>
		        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">     ${uiLabelMap.KMFDairyHeader}</fo:block>
					    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">  ${uiLabelMap.KMFDairySubHeader}</fo:block>
					    
                    	<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false">TIN ::                                                           PRODUCTS CHANNELWISE SALES BASIC VALUE STATEMENT FOR THE MONTH OF :: ${parameters.customTimePeriodId}</fo:block>
              			<fo:block font-size="7pt" align-text="left">=======================================================================================================================================================================================================================================</fo:block>  
            			<fo:block>
                 	<fo:table border-width="1pt" border-style="dotted">
                    <fo:table-column column-width="20pt"/>
                    <fo:table-column column-width="45pt"/> 
               	    <fo:table-column column-width="60pt"/>
               	    <#list facilityCategoryList as facilityCategory>
	                     <fo:table-column column-width="40pt"/>  
	                     <fo:table-column column-width="30pt"/>  
	                     <fo:table-column column-width="40pt"/>  			
	                </#list>
            		 <fo:table-column column-width="40pt"/>  
            		<fo:table-column column-width="40pt"/>	
            		<fo:table-column column-width="40pt"/>
            		   <fo:table-header>
	                			<fo:table-cell>
	                			  <fo:block text-align="left" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">SL </fo:block>
	                			  <fo:block text-align="left" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">NO </fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell>
	                            	 <fo:block text-align="left" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">PROD</fo:block>
	                			     <fo:block text-align="left" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">CODE</fo:block> 
	                        	</fo:table-cell>
	                			<fo:table-cell>
	                            	<fo:block text-align="left" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">PRODUCT</fo:block>
	                			     <fo:block text-align="left" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">NAME</fo:block>  
	                        	</fo:table-cell>
		                		<#list facilityCategoryList as facilityCategory>
		                		<fo:table-cell>
	                            	 <fo:block text-align="right" font-size="7pt" keep-together="always"   font-family="Courier,monospace" white-space-collapse="false">&#160;</fo:block>
	                			      <fo:block text-align="right" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">QTY</fo:block>  
	                        	</fo:table-cell>
	                        	<fo:table-cell>
	                            	 <fo:block text-align="center" font-size="7pt" keep-together="always"   font-family="Courier,monospace" white-space-collapse="false">${facilityCategory}</fo:block>
	                			      <fo:block text-align="right" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">&#160;</fo:block>  
	                        	</fo:table-cell>
	                        	<fo:table-cell>
	                            	<fo:block text-align="right" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">&#160; </fo:block>  
	                			     <fo:block text-align="right" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">VALUE</fo:block>  
	                        	 </fo:table-cell>
	                	       	</#list>
	                        	<fo:table-cell>
	                            	 <fo:block text-align="right" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">&#160;</fo:block>
	                			     <fo:block text-align="right" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">QTY</fo:block>  
	                        	 </fo:table-cell>
	                        	 <fo:table-cell>
	                            	 <fo:block text-align="center" font-size="7pt" keep-together="always"   font-family="Courier,monospace" white-space-collapse="false">TOTAL</fo:block>
	                			      <fo:block text-align="right" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">&#160; </fo:block>  
	                        	</fo:table-cell>
	                        	 <fo:table-cell>
	                            	  <fo:block text-align="center" font-size="7pt" keep-together="always"   font-family="Courier,monospace" white-space-collapse="false">&#160;</fo:block>
	                			     <fo:block text-align="right" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">VALUE</fo:block>  
	                        	 </fo:table-cell>
	                		</fo:table-header>
	                		<fo:table-body>
	                		  <fo:table-row>
	                    			<fo:table-cell>
		                            	<fo:block font-size="7pt" align-text="left"></fo:block>  
		                        	</fo:table-cell>
							  </fo:table-row>
	                	 </fo:table-body>
	                    </fo:table>
	                    </fo:block> 	  
		        		<fo:block font-size="7pt" align-text="left">=======================================================================================================================================================================================================================================</fo:block>  
		        	</fo:static-content>	  	        	
		        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
                 	<fo:table border-width="1pt" border-style="dotted">
                    <fo:table-column column-width="20pt"/>
                    <fo:table-column column-width="45pt"/> 
               	    <fo:table-column column-width="60pt"/>
               	    <#list facilityCategoryList as facilityCategory>
	                     <fo:table-column column-width="40pt"/>  
	                     <fo:table-column column-width="30pt"/>  
	                     <fo:table-column column-width="40pt"/>  			
	                </#list>
            		 <fo:table-column column-width="40pt"/>  
            		<fo:table-column column-width="40pt"/>	
            		<fo:table-column column-width="40pt"/>	
	                    <fo:table-body>
	                    	<#assign periodTotals = periodTotalsMap.entrySet()>
	                    	<#assign serialNo = 1>
	                    	<#assign totalQty = 0>
							<#assign basicValue = 0>
							
							<#assign grandTotalQty = 0>
							<#assign grandTotalValue = 0>
	                    	
	                    	<#list periodTotals as periodTotalsEntry>
	                    		<#assign periodProductTotals = periodTotalsEntry.getValue().entrySet()>
	                    		<#list periodProductTotals as periodProductTotalsEntry>
	                    			<#if periodProductTotalsEntry.getKey() == "prodCategoryTotals">
	                    				<#assign saleTotalsMap = (periodProductTotalsEntry.getValue()).get("sale")>
	                    				<#assign facCatTotals = (saleTotalsMap).get("CategoryWiseTotals")>
	                    				<fo:table-row>
	                    					<fo:table-cell>
		                            			<fo:block font-size="7pt" align-text="left">=======================================================================================================================================================================================================================================</fo:block>  
		                        			</fo:table-cell>
										</fo:table-row>
										<fo:table-row>
											<#assign totalQty = 0>
											<#assign basicValue = 0>
	                    					<fo:table-cell>
		                            			<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">TOTAL (KGS/LTRS)</fo:block>  
		                        			</fo:table-cell>
				                        	<fo:table-cell>
				                            	<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false"></fo:block>  
				                        	</fo:table-cell>
				                        	<fo:table-cell>
				                            	<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false"></fo:block>  
				                        	</fo:table-cell>
				                        	<#list facilityCategoryList as facilityCategory>
				                        		<#if (facCatTotals.get(facilityCategory)?has_content)>
				                        			<#assign categoryWiseTotals = (facCatTotals).get(facilityCategory)>
				                        			<fo:table-cell>
				                            			<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${categoryWiseTotals.get("quantity")?string("#0")}</fo:block>  
				                        			</fo:table-cell>
				                        			<fo:table-cell>
				                                         <fo:block text-align="right" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">&#160;</fo:block> 
				                        			</fo:table-cell>
				                        			<fo:table-cell>
				                            			<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${categoryWiseTotals.get("basicValue")?if_exists?string("#0.00")}</fo:block>  
				                        			</fo:table-cell>
				                        			<#assign totalQty = totalQty + (categoryWiseTotals.get("quantity"))>
				                        			<#assign basicValue = basicValue + (categoryWiseTotals.get("basicValue"))>
				                        		<#else>
				                        			<fo:table-cell>
				                            			<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false"></fo:block>  
				                        			</fo:table-cell>
				                        			<fo:table-cell>
				                                         <fo:block text-align="right" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">&#160;</fo:block> 
				                        			</fo:table-cell>
				                        			<fo:table-cell>
				                            			<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false"></fo:block>  
				                        			</fo:table-cell>
				                        		</#if>
				                        	</#list>
				                        	<fo:table-cell>
				                            	<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${totalQty?string("#0")}</fo:block>  
				                        	</fo:table-cell>
				                        	<fo:table-cell>
				                                         <fo:block text-align="right" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">&#160;</fo:block> 
				                        	</fo:table-cell>
				                        	<fo:table-cell>
				                            	<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${basicValue?if_exists?string("#0.00")}</fo:block>  
				                        	</fo:table-cell>
										</fo:table-row>
										<fo:table-row>
	                    					<fo:table-cell>
		                            			<fo:block font-size="7pt" align-text="left">=======================================================================================================================================================================================================================================</fo:block>   
		                        			</fo:table-cell>
										</fo:table-row>
	                    				
	                    			<#else>
	                    				<#assign salesMap = (periodProductTotalsEntry.getValue()).get("sale")>
			                        	<#assign salesTotals = (salesMap).get("CategoryWiseTotals")>
			                    		<fo:table-row>
			                    			<#assign totalQty = 0>
											<#assign basicValue = 0>
			                    			<fo:table-cell>
				                            	<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${serialNo}</fo:block>  
				                        	</fo:table-cell>
			                    			<fo:table-cell>
				                            	<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${periodProductTotalsEntry.getKey()}</fo:block>  
				                        	</fo:table-cell>
				                        	<#assign productEnt = delegator.findOne("Product", {"productId" : periodProductTotalsEntry.getKey()}, true)>
				                        	<fo:table-cell>
	                            				<fo:block text-align="left" keep-together="always" font-size="7pt"  white-space-collapse="false">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(productEnt.brandName)),12)}</fo:block>        
	                        				</fo:table-cell>
				                        	<#list facilityCategoryList as facilityCategory>
				                        		<#if (salesTotals.get(facilityCategory)?has_content)>
				                        			<#assign categoryWiseTotals = (salesTotals).get(facilityCategory)>
				                        			<fo:table-cell>
				                            			<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${categoryWiseTotals.get("quantity")?string("#0")}</fo:block>  
				                        			</fo:table-cell>
				                        			<fo:table-cell>
				                                         <fo:block text-align="right" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">&#160;</fo:block> 
				                        			</fo:table-cell>
				                        			<fo:table-cell>
				                            			<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${categoryWiseTotals.get("basicValue")?if_exists?string("#0.00")}</fo:block>  
				                        			</fo:table-cell>
				                        			<#assign totalQty = totalQty + (categoryWiseTotals.get("quantity"))>
				                        			<#assign basicValue = basicValue + (categoryWiseTotals.get("basicValue"))>
				                        		<#else>
				                        			<fo:table-cell>
				                            			<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false"></fo:block>  
				                        			</fo:table-cell>
				                        			<fo:table-cell>
				                                         <fo:block text-align="right" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">&#160;</fo:block> 
				                        			</fo:table-cell>
				                        			<fo:table-cell>
				                            			<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false"></fo:block>  
				                        			</fo:table-cell>
				                        		</#if>
				                        	</#list>
				                        	<fo:table-cell>
				                            	<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${totalQty?string("#0")}</fo:block>  
				                        	</fo:table-cell>
				                        	<fo:table-cell>
				                                         <fo:block text-align="right" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">&#160;</fo:block> 
				                        	</fo:table-cell>
				                        	<fo:table-cell>
				                            	<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${basicValue?if_exists?string("#0.00")}</fo:block>  
				                        	</fo:table-cell>
										</fo:table-row>
										<#assign serialNo = serialNo + 1>
									</#if>
								</#list>
	                		</#list>
	                		<#-- <fo:table-row>
	                			<fo:table-cell>
	                            	<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false"></fo:block>  
	                        	</fo:table-cell>
	                        	<fo:table-cell>
	                            	<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false"></fo:block>  
	                        	</fo:table-cell>
	                			<fo:table-cell>
	                            	<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">GRAND TOTALS:</fo:block>  
	                        	</fo:table-cell>
		                		<#list facilityCategoryList as facilityCategory>
		                			<#if (grandTotalsMap.get(facilityCategory)?has_content)>
	                        			<#assign categoryWiseTotals = (grandTotalsMap).get(facilityCategory)>
	                        			<fo:table-cell>
	                            			<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false"></fo:block>  
	                        			</fo:table-cell>
	                        			<fo:table-cell>
	                            			<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${categoryWiseTotals.get("basicValue")?if_exists?string("#0.00")}</fo:block>  
	                        			</fo:table-cell>
	                        			<#assign grandTotalQty = grandTotalQty + (categoryWiseTotals.get("quantity"))>
	                        			<#assign grandTotalValue = grandTotalValue + (categoryWiseTotals.get("basicValue"))>
	                        		<#else>
	                        			<fo:table-cell>
	                            			<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false"></fo:block>  
	                        			</fo:table-cell>
	                        			<fo:table-cell>
	                            			<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false"></fo:block>  
	                        			</fo:table-cell>
	                        		</#if>
	                        	</#list>
	                        	<fo:table-cell>
                            			<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false"></fo:block>  
                        		</fo:table-cell>
                        		<fo:table-cell>
                            			<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${grandTotalValue?if_exists?string("#0.00")}</fo:block>  
                        		</fo:table-cell>
	                		</fo:table-row> -->
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