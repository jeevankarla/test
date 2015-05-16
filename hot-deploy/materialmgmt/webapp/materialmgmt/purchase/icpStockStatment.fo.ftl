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
specific language governing permissions and limitations`
under the License.
-->

<#escape x as x?xml>
	<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
		<fo:layout-master-set>
			<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
					 margin-left="0.2in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.2in" >
				<fo:region-body margin-top="2.8in"/>
				<fo:region-before extent="1in"/>
				<fo:region-after extent="1in"/>
			</fo:simple-page-master>
		</fo:layout-master-set>
		<#if productCategoryMap?has_content>
 		 <#assign grandTotalIssueValue = 0>
       <fo:page-sequence master-reference="main">
		    <fo:static-content font-size="13pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">	 
			   <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" font-size="10pt" white-space-collapse="false">&#160;${uiLabelMap.CommonPage}- <fo:page-number/> </fo:block>			
			    <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false">    UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
				<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false">&#160;      Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
                <fo:block  keep-together="always" text-align="center" font-weight = "bold" font-family="Courier,monospace" white-space-collapse="false">${uiLabelMap.KMFDairyHeader}</fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
				<fo:block text-align="center" keep-together="always"  >&#160;------------------------------------------------------------------------------------------</fo:block>
				<fo:block text-align="center" white-space-collapse="false" font-size="12pt"  font-weight="bold" >&#160;   DAILY STOCK STATEMENT OF <#if categoryType=="ICE_CREAM_NANDINI">NANDINI</#if><#if categoryType=="ICE_CREAM_AMUL">AMUL</#if> ICE CREAM ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} AND ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")} </fo:block>                
				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			    <fo:block text-align="left" white-space-collapse="false">&#160;&#160;DATE :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}                                                              BATCH NO: </fo:block>                
			    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>	
    			<fo:block>
                   <fo:table border-style="solid">
                       <fo:table-column column-width="20pt"/>                      
					  <#-- <fo:table-column column-width="110pt"/> -->
					   <fo:table-column column-width="150pt"/>
					   <fo:table-column column-width="90pt"/>
					   <fo:table-column column-width="90pt"/>
					   <fo:table-column column-width="80pt"/>
					   <fo:table-column column-width="100pt"/>
					   <fo:table-column column-width="90pt"/>
						<fo:table-column column-width="100pt"/>
					   <fo:table-column column-width="90pt"/>
					   <fo:table-column column-width="90pt"/>
					   <fo:table-body> 
					       <fo:table-row height="30pt">
					           <fo:table-cell border-style="solid">
					                     <fo:block text-align="center" padding-before="0.6cm" font-weight="bold" >NO</fo:block>
					           </fo:table-cell>
					          <#--<fo:table-cell border-style="solid">
					                     <fo:block text-align="center" padding-before="0.6cm" font-weight="bold" >Product CODE</fo:block>
					           </fo:table-cell> -->
					           <fo:table-cell border-style="solid"> 
					                     <fo:block text-align="center" padding-before="0.6cm" font-weight="bold"  >PARTICULARS</fo:block>
					           </fo:table-cell>
					           <fo:table-cell border-style="solid">
									     <fo:block text-align="center"  font-weight="bold"  >OPENING BALANCE (LTRS)</fo:block>
					           </fo:table-cell>
                               <fo:table-cell border-style="solid">
										 <fo:block text-align="center"  font-weight="bold"  >RECEIPTS (LTRS)</fo:block>
					           </fo:table-cell>
                               <fo:table-cell border-style="solid">
										 <fo:block text-align="center"  font-weight="bold"  >CUMILATIVE RECEIPTS (LTRS)</fo:block>
					           </fo:table-cell>
					           <fo:table-cell border-style="solid">
										 <fo:block text-align="center"  font-weight="bold"  >TOTAL</fo:block>
					           </fo:table-cell>
					           <fo:table-cell border-style="solid">
										 <fo:block text-align="center"  font-weight="bold"  >ISSUES (LTRS)</fo:block>
					           </fo:table-cell>
 								<fo:table-cell border-style="solid">
										 <fo:block text-align="center"  font-weight="bold"  >CUMILATIVE ISSUES (LTRS)</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
									     <fo:block text-align="center"  font-weight="bold"  >CLOSING BALANCE (LTRS)</fo:block>
					            </fo:table-cell>
 								 <fo:table-cell border-style="solid">
									     <fo:block text-align="center"  font-weight="bold"  >TOTAL/CRATES/BOXES </fo:block>
					            </fo:table-cell>
					       </fo:table-row>
				      </fo:table-body>
				   </fo:table>
				   
			   </fo:block>  
            </fo:static-content>
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
            
                            
			   <fo:block>
                   <fo:table border-style="solid">
                        <fo:table-column column-width="20pt" border-style="solid"/>                      
					   <#-- <fo:table-column column-width="110pt" border-style="solid"/> -->
					   <fo:table-column column-width="150pt" border-style="solid"/>
					   <fo:table-column column-width="90pt" border-style="solid"/>
					   <fo:table-column column-width="90pt" border-style="solid"/>
					   <fo:table-column column-width="80pt" border-style="solid"/>
					   <fo:table-column column-width="100pt" border-style="solid"/>
					   <fo:table-column column-width="90pt" border-style="solid"/>
						<fo:table-column column-width="100pt" border-style="solid"/>
					   <fo:table-column column-width="90pt" border-style="solid"/>
					   <fo:table-column column-width="90pt" border-style="solid"/>
					  
					   <fo:table-body>  
					        <#assign sno=1>	
		                    <#assign prodCategoryDetails = productCategoryMap.entrySet()>
		                    <#list prodCategoryDetails as prodCategory>
				   		<#assign seqno= seqno+1>

		                    <#if prodCategory.getKey()=="Milk" || prodCategory.getKey()=="Other Products">
		                    <#assign productCategory = delegator.findOne("ProductCategory", {"productCategoryId" : prodCategory.getKey()}, true)?if_exists/>
		                    <fo:table-row>
	                        	<fo:table-cell>
	                        	  <fo:table >
                      					  <fo:table-column column-width="20pt"/>
                      					   <fo:table-body>
                      					   <fo:table-row>
                      					   <fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">${productCategory.description?if_exists}</fo:block>  
											</fo:table-cell>
											</fo:table-row>
											</fo:table-body>
											</fo:table >
	                        	</fo:table-cell>
		                     </fo:table-row>
		                     <fo:table-row>
	                        	 <fo:table-cell>
	                            		<fo:block font-size="10pt"></fo:block>
	                        	 </fo:table-cell>
		                     </fo:table-row>
		                     </#if>
							 <#assign productDetails = prodCategory.getValue().entrySet()>
		                     <#list productDetails as prod>
		                     <#assign catProdDetails = prod.getValue().entrySet()>
                                <#assign product=delegator.findOne("ProductCategory",{"productCategoryId":prod.getKey()},true)>
							<fo:table-row>
	                        	<fo:table-cell>
	                        	<fo:table >
                      					  <fo:table-column column-width="20pt"/>
                      					  <fo:table-column column-width="110pt"/>
                      					   <fo:table-body>
                      					   <fo:table-row>
                      					   		<fo:table-cell>
	                            					<fo:block   text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
												</fo:table-cell>
                      					   
                      					  		 <fo:table-cell>
	                            					<fo:block   text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">${product.description?if_exists}</fo:block>  
												</fo:table-cell>
											</fo:table-row>
											 <fo:table-row>
                      					  		 <fo:table-cell>
	                            						<fo:block  text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
												</fo:table-cell>
											</fo:table-row>
											</fo:table-body>
											</fo:table >	                        	
								</fo:table-cell>
		                    </fo:table-row>
		                    <fo:table-row>
	                        	<fo:table-cell>
	                            		<fo:block font-size="10pt"></fo:block>
	                        	</fo:table-cell>
		                     </fo:table-row>
		                     <#assign TotalIssueValue =0>
							<#list catProdDetails as catProd>
		                    <#assign product = delegator.findOne("Product", {"productId" : catProd.getKey()}, true)?if_exists/>
 							<#assign grandTotalIssueValue = grandTotalIssueValue+catProd.getValue().get("quantity")?if_exists>
 							<#assign TotalIssueValue = TotalIssueValue+catProd.getValue().get("quantity")?if_exists>

						     <fo:table-row height="30pt" >
					            <fo:table-cell>
									    <fo:block text-align="left" keep-together="always" font-size="10pt" >${sno?if_exists}</fo:block>
								     </fo:table-cell>
					            <#--<fo:table-cell > 
					                     <fo:block text-align="left" font-size="10pt" >${prod.getKey()}</fo:block>
					            </fo:table-cell>-->	
                                <fo:table-cell >
					                     <fo:block text-align="left" font-size="10pt" white-space-collapse="false">${product.description?if_exists}</fo:block>
					            </fo:table-cell>	
                                <fo:table-cell >
								    <fo:block text-align="right"  font-size="10pt" ></fo:block>
							    </fo:table-cell>
							    <fo:table-cell >
								   <fo:block text-align="right" font-size="10pt"  ></fo:block>
							    </fo:table-cell>
                                <fo:table-cell >
								   <fo:block text-align="right" font-size="10pt"  ></fo:block>
							    </fo:table-cell>
							    <fo:table-cell >
								   <fo:block text-align="right" font-size="10pt"  ></fo:block>
							    </fo:table-cell>
                   				<fo:table-cell >
								   <fo:block text-align="right" font-size="10pt"  >${catProd.getValue().get("quantity")?if_exists?string("#0.00")}</fo:block>
							    </fo:table-cell>
							   <fo:table-cell >
								   <fo:block text-align="right" font-size="10pt"  ></fo:block>
							    </fo:table-cell>
							    <fo:table-cell >
							 	   <fo:block text-align="right" font-size="10pt"  ></fo:block>
							    </fo:table-cell>
							    <fo:table-cell >
								   <fo:block text-align="right" font-size="10pt"  ></fo:block>
							    </fo:table-cell>
					       <#assign sno=sno+1>
						</fo:table-row>
					
					</#list> 
						<fo:table-row border-style="solid">
						<fo:table-cell>
									    <fo:block text-align="left" keep-together="always" font-size="10pt" ></fo:block>
								     </fo:table-cell>
					            <#--<fo:table-cell > 
					                     <fo:block text-align="left" font-size="10pt" ></fo:block>
					            </fo:table-cell>-->	
                                <fo:table-cell >
					                     <fo:block text-align="left" font-size="10pt" white-space-collapse="false">Total:</fo:block>
					            </fo:table-cell>	
                                <fo:table-cell >
								    <fo:block text-align="right"  font-size="10pt" ></fo:block>
							    </fo:table-cell>
							    <fo:table-cell >
								   <fo:block text-align="right" font-size="10pt"  ></fo:block>
							    </fo:table-cell>
                                <fo:table-cell >
								   <fo:block text-align="right" font-size="10pt"  ></fo:block>
							    </fo:table-cell>
							    <fo:table-cell >
								   <fo:block text-align="right" font-size="10pt"  ></fo:block>
							    </fo:table-cell>
                   				<fo:table-cell >
								   <fo:block text-align="right" font-size="10pt"  >${TotalIssueValue?if_exists?string("#0.00")}</fo:block>
							    </fo:table-cell>
							   <fo:table-cell >
								   <fo:block text-align="right" font-size="10pt"  ></fo:block>
							    </fo:table-cell>
							    <fo:table-cell >
							 	   <fo:block text-align="right" font-size="10pt"  ></fo:block>
							    </fo:table-cell>
							    <fo:table-cell >
								   <fo:block text-align="right" font-size="10pt"  ></fo:block>
							    </fo:table-cell>
		            </fo:table-row>
			        <fo:table-row border-style="solid">
		                 <fo:table-cell >
		                         <fo:block font-size="10pt"></fo:block>
		                  </fo:table-cell>
		            </fo:table-row>
					</#list> 
					</#list> 
			       </fo:table-body>
				  </fo:table>
			   </fo:block> 	
			   <fo:block page-break-after="always"></fo:block>
               <fo:block>
                   <fo:table border-style="solid">
                          <fo:table-column column-width="20pt" border-style="solid"/>                      
					   <#-- <fo:table-column column-width="110pt" border-style="solid"/> -->
					   <fo:table-column column-width="150pt" border-style="solid"/>
					   <fo:table-column column-width="90pt" border-style="solid"/>
					   <fo:table-column column-width="90pt" border-style="solid"/>
					   <fo:table-column column-width="80pt" border-style="solid"/>
					   <fo:table-column column-width="100pt" border-style="solid"/>
					   <fo:table-column column-width="90pt" border-style="solid"/>
						<fo:table-column column-width="100pt" border-style="solid"/>
					   <fo:table-column column-width="90pt" border-style="solid"/>
					   <fo:table-column column-width="90pt" border-style="solid"/>
					   <fo:table-body>
					   <fo:table-row >
					   <fo:table-cell >
						 <fo:block text-align="left" keep-together="always" font-size="10pt" ></fo:block>
					   </fo:table-cell>
					   <fo:table-cell >
						 <fo:block text-align="cemter" keep-together="always" font-size="10pt" font-weight="bold" > GRAND TOTALS :</fo:block>
					   </fo:table-cell>
					   <#--<fo:table-cell >
						 <fo:block text-align="right" keep-together="always" font-size="10pt" font-weight="bold" ></fo:block>
					   </fo:table-cell>-->
					   <fo:table-cell >
						 <fo:block text-align="right" keep-together="always" font-size="10pt" font-weight="bold" ></fo:block>
					   </fo:table-cell>
					   <fo:table-cell>
						 <fo:block text-align="left" keep-together="always" font-size="10pt" ></fo:block>
					   </fo:table-cell>
					   <fo:table-cell >
						 <fo:block text-align="right" keep-together="always" font-size="10pt" font-weight="bold" ></fo:block>
					   </fo:table-cell>
					   <fo:table-cell >
						 <fo:block text-align="left" keep-together="always" font-size="10pt" ></fo:block>
					   </fo:table-cell>
					   <fo:table-cell>
						 <fo:block text-align="right" keep-together="always" font-size="10pt" font-weight="bold" >${grandTotalIssueValue}</fo:block>
					   </fo:table-cell>
					   <fo:table-cell>
						 <fo:block text-align="left" keep-together="always" font-size="10pt" ></fo:block>
					   </fo:table-cell>
					   <fo:table-cell>
						 <fo:block text-align="right" keep-together="always" font-size="10pt" font-weight="bold" ></fo:block>
					   </fo:table-cell>
					   <fo:table-cell>
						 <fo:block text-align="right" keep-together="always" font-size="10pt" font-weight="bold" ></fo:block>
					   </fo:table-cell>
					   </fo:table-row>
				   </fo:table-body>
				 </fo:table>
			   </fo:block>	
			   <fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
			   <fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
			   <fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160; ABSTRACT</fo:block>
			   <fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
			   <fo:block text-align="center" keep-together="always" font-size="12pt" > TOTAL PRODUCTION: &#160;&#160;&#160;&#160;&#160;&#160;AVG PRODUCTION: </fo:block>
			   <fo:block text-align="center" keep-together="always" font-size="12pt" >TOTAL SALES: &#160;&#160;&#160;&#160;&#160;&#160;AVG SALES:</fo:block>
			   <fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always" >&#160;&#160;CUPS%:</fo:block>
			   
			   <fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always" >&#160;PACKS%:</fo:block>
			   
			   <fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always" >STICKS%:</fo:block>
			   
			   <fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always" >&#160;CONES%:</fo:block>

			   <fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always" >&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>

			   <fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always" >&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>

			   <fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;CASE WORKER:&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;PRE AUDIT:&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;INCHARGE ICP:</fo:block>
		   </fo:flow>	
       </fo:page-sequence>
       <#else>
           <fo:page-sequence master-reference="main">
	    			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	       		 		<fo:block font-size="14pt" text-align="center">
	            			 No Records Found....!
	       		 		</fo:block>
	    			</fo:flow>
		  </fo:page-sequence>				
	    </#if>  
</fo:root>
</#escape>	