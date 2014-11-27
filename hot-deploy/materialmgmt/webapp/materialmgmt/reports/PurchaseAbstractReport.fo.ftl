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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-right=".3in" margin-top=".5in" margin-bottom="0.5in">
                <fo:region-body margin-top="0.1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "IceCreamSalesReport.pdf")}
        <#if errorMessage?has_content>
	<fo:page-sequence master-reference="main">
	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	   <fo:block font-size="14pt">
	           ${errorMessage}.
        </fo:block>
	</fo:flow>
	</fo:page-sequence>	
	<#else>
       <#if dayWiseInvoice?has_content>
	        <fo:page-sequence master-reference="main" font-size="12pt">	
	           <fo:static-content flow-name="xsl-region-before">
              		<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"> &#160;${uiLabelMap.CommonPage}- <fo:page-number/></fo:block>
               </fo:static-content>	
	        		<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
	        	    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
          			<fo:block text-align="center" font-weight="bold"  keep-together="always"  white-space-collapse="false"> PURCHASE ANALYSIS - DETAILED REPORT FOR THE PERIOD - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")} </fo:block>-->
          			<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
          			<fo:block>------------------------------------------------------------------------------------------------</fo:block>
            	    <fo:block text-align="left" font-weight="bold" font-size="12pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">VoucherCode     InvoiceId		InvoiceDate      Narration&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;                Amount</fo:block>
	        		<fo:block>------------------------------------------------------------------------------------------------</fo:block>
            	
        			<fo:block>
        				<fo:table>
		                    <fo:table-column column-width="120pt"/>
		                    <fo:table-column column-width="70pt"/>
		                    <fo:table-column column-width="130pt"/> 
		               	    <fo:table-column column-width="190pt"/>
		            		<fo:table-column column-width="80pt"/> 		
		            		<fo:table-column column-width="95pt"/>
		            		<fo:table-column column-width="90pt"/>
		            		<fo:table-column column-width="95pt"/>
		            		<fo:table-column column-width="95pt"/>
		                    <fo:table-body>
			                    <#assign grandTotal=0>
		                        <#assign dayWiseTotalsMap = dayWiseInvoice.entrySet()>
       							<#list dayWiseTotalsMap as dayWiseTotalsDetails>
       							<#assign productCategory = delegator.findOne("ProductCategory", {"productCategoryId" : dayWiseTotalsDetails.getKey()}, true)?if_exists/>
       						
       							<fo:table-row>
       							       <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">Analysis Code :</fo:block>  
							            </fo:table-cell>
       							        <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">${productCategory.description}</fo:block>  
							            </fo:table-cell>
							    </fo:table-row> 
       							<#assign dayWiseTotals = dayWiseTotalsDetails.getValue().entrySet()>
       							<#assign total=0>
       							<#list dayWiseTotals as dayWiseTotal>
       							<#assign purchaseMap = dayWiseTotal.getValue()>
       							<#assign prodMap = dayWiseTotal.getValue().entrySet()>
       							<fo:table-row>
       							        <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" >${purchaseMap.get("supInvNumber")}</fo:block>  
							            </fo:table-cell>
       							        <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" >${dayWiseTotal.getKey()}</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" >${purchaseMap.get("invoiceDate")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
                       			        <fo:block   font-size="12pt" text-align="left" white-space-collapse="false">
		                       			<fo:table>
					            		<fo:table-column column-width="150pt"/> 		
					            		<fo:table-column column-width="200pt"/> 	
					            		<fo:table-column column-width="80pt"/> 
		                                <fo:table-body>
		                                <#list prodMap as prodTotal>
		                                 <#if prodTotal.getKey()!=("supInvNumber") &&  prodTotal.getKey()!=("invoiceDate")>
		                                 <#assign prodDetails = delegator.findOne("Product", {"productId" :prodTotal.getKey()}, true)>
		                                 <#assign total=total+prodTotal.getValue()>
		                                 <#assign grandTotal=grandTotal+prodTotal.getValue()>
			                                <fo:table-row>
			                                <fo:table-cell>
					                           		<fo:block  keep-together="always" font-size="12pt" text-align="left" white-space-collapse="false">${prodDetails.description}</fo:block>  
					                       		</fo:table-cell>
			                                   <fo:table-cell>
					                           		<fo:block   font-size="12pt" text-align="right" white-space-collapse="false">${prodTotal.getValue()?string("#0.00")}</fo:block>  
					                       		</fo:table-cell>
							             	</fo:table-row>
							             	</#if>
						             	 </#list>
						             </fo:table-body>
						             </fo:table>
                       			</fo:block>  
                   			  </fo:table-cell>
							    </fo:table-row> 
							  </#list>
					           <fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
          						  </fo:table-row> 
          						  <fo:table-row>
          						        <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
		                    	        <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
       							        <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
                       			        <fo:block   font-size="12pt" text-align="left" white-space-collapse="false">
		                       			<fo:table>
					            		<fo:table-column column-width="150pt"/> 		
					            		<fo:table-column column-width="200pt"/> 	
					            		<fo:table-column column-width="80pt"/> 
		                                <fo:table-body>
			                                <fo:table-row>
			                                   <fo:table-cell>
					                           		<fo:block   font-size="12pt" text-align="left" white-space-collapse="false" font-weight="bold">total</fo:block>  
					                       		</fo:table-cell>
			                                   <fo:table-cell>
					                           		<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${total?string("#0.00")}</fo:block>  
					                       		</fo:table-cell>
							             	</fo:table-row>
						             </fo:table-body>
						             </fo:table>
                       			</fo:block>  
                   			  </fo:table-cell>
							    </fo:table-row> 
							    <fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
          						  </fo:table-row> 
								</#list>
          						  <fo:table-row>
          						        <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
		                    	        <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
       							        <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
                       			        <fo:block   font-size="12pt" text-align="left" white-space-collapse="false">
		                       			<fo:table>
					            		<fo:table-column column-width="150pt"/> 		
					            		<fo:table-column column-width="200pt"/> 	
					            		<fo:table-column column-width="80pt"/> 
		                                <fo:table-body>
			                                <fo:table-row>
			                                   <fo:table-cell>
					                           		<fo:block   font-size="12pt" text-align="left" white-space-collapse="false" font-weight="bold">Grand Total</fo:block>  
					                       		</fo:table-cell>
			                                   <fo:table-cell>
					                           		<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${grandTotal?string("#0.00")}</fo:block>  
					                       		</fo:table-cell>
							             	</fo:table-row>
						             </fo:table-body>
						             </fo:table>
                       			</fo:block>  
                   			  </fo:table-cell>
							    </fo:table-row> 
							    <fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>------------------------------------------------------------------------------------------------</fo:block>
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