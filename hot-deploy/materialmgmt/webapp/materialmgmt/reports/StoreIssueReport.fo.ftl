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
			<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
					 margin-left="0.5in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.2in" >
				<fo:region-body margin-top="2.61in"/>
				<fo:region-before extent="1in"/>
				<fo:region-after extent="1in"/>
			</fo:simple-page-master>
		</fo:layout-master-set>
		        <#if allDetailsMap?has_content>
		       <#assign recQtyTotal = 0>
               <#assign recAmtTotal = 0>
               <#assign issQtyTotal = 0>
               <#assign issAmtTotal = 0>
		<fo:page-sequence master-reference="main">
			<fo:static-content font-size="13pt" font-family="Courier,monospace"  flow-name="xsl-region-before" >
				<fo:block  keep-together="always" text-align="left"  font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;                                       UserLogin: <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
		   <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;                                   Date     : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block> 
		    			      
				<fo:block  keep-together="always" text-align="center" font-weight = "bold" font-family="Courier,monospace" white-space-collapse="false">${uiLabelMap.KMFDairyHeader}</fo:block>
				 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
				<fo:block text-align="center" keep-together="always"  >&#160;---------------------------------------------------------------------</fo:block>
				<fo:block text-align="center" white-space-collapse="false" font-weight = "bold">&#160;      STORE RECEIPTS-ISSUES BETWEEN ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} AND ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")}                </fo:block>				
			    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			    <fo:block  keep-together="always" text-align="left" white-space-collapse="false" font-weight = "bold"> <#if internalName?has_content>MATERIAL CODE: ${internalName}<#else></#if>          STORE: <#if issueToFacilityId?has_content>${issueToFacilityId?if_exists}<#else></#if>         <#if materialName?has_content>MATERIAL NAME: ${materialName}<#else></#if>        <#if unit?has_content>Units: ${unit}<#else></#if> </fo:block>   			   
			    <fo:block font-family="Courier,monospace">		 
			    <fo:table border-style="solid">
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="90pt"/>
					<fo:table-column column-width="90pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="90pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="65pt"/>
					<fo:table-column column-width="25pt"/>
					
					   <fo:table-body>
					      <fo:table-row>
					           <fo:table-cell border-style="solid" number-columns-spanned="6">
					               <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					               <fo:block text-align="center" font-size="13pt" white-space-collapse="false" font-weight="bold">RECEIPTS</fo:block>
					           </fo:table-cell>
					           <fo:table-cell border-style="solid" number-columns-spanned="5">
					               <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					               <fo:block text-align="center" font-size="13pt" white-space-collapse="false" font-weight="bold">ISSUES</fo:block>
					           </fo:table-cell>
					      </fo:table-row>
					      <fo:table-row >
					            <fo:table-cell border-style="solid">
									<fo:block text-align="center" keep-together="always" font-weight = "bold">DATE</fo:block>
								</fo:table-cell>
								<fo:table-cell border-style="solid">
									<fo:block text-align="center" keep-together="always" font-weight = "bold">BILL NO.</fo:block>
								</fo:table-cell>
								<fo:table-cell border-style="solid">
									<fo:block text-align="center" keep-together="always" font-weight = "bold">MRR NUMBER</fo:block>
								</fo:table-cell>
								<fo:table-cell border-style="solid">
									<fo:block text-align="center" keep-together="always" font-weight = "bold" >QUANTITY</fo:block>
								</fo:table-cell>
								<fo:table-cell border-style="solid">
									<fo:block text-align="center" keep-together="always" font-weight = "bold">RATE</fo:block>
								</fo:table-cell>
								<fo:table-cell border-style="solid" >
									<fo:block text-align="center" keep-together="always" font-weight = "bold">AMOUNT</fo:block>
								</fo:table-cell>
								<fo:table-cell border-style="solid">
									<fo:block text-align="center" keep-together="always" font-weight = "bold">INDENT NO.</fo:block>
								</fo:table-cell>
								<fo:table-cell border-style="solid">
									<fo:block text-align="center" keep-together="always" font-weight = "bold">MRR NUMBER</fo:block>
								</fo:table-cell>
								<fo:table-cell border-style="solid" >
								    <fo:block text-align="center" keep-together="always" font-weight = "bold">ISSUE QTY</fo:block>
								</fo:table-cell>
								<fo:table-cell border-style="solid">
								    <fo:block text-align="center" keep-together="always" font-weight = "bold">RATE</fo:block>
								</fo:table-cell>
								<fo:table-cell border-style="solid">
								    <fo:block text-align="center" keep-together="always" font-weight = "bold">AMOUNT</fo:block>
								</fo:table-cell>
								<fo:table-cell >
								    <fo:block text-align="left" keep-together="always" font-weight = "bold">Day Closing </fo:block>
								    <fo:block text-align="center" keep-together="always" font-weight = "bold">Balance</fo:block>
								</fo:table-cell>
							</fo:table-row>
					</fo:table-body>
				</fo:table>
  			</fo:block>
	   </fo:static-content>
	   <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">
	        <fo:block font-family="Courier,monospace"  font-size="10pt">
 
 

<fo:table >
<fo:table-column column-width="80pt"/>
<fo:table-column column-width="870pt"/>
<fo:table-column column-width="90pt"/>

<fo:table-body>
     <#assign allMapDetails = allDetailsMap.entrySet()>
 <#list allMapDetails as storeIssueDetails>
                     <#assign storeDetails1 = storeIssueDetails.getValue().get("MrrMap")?if_exists>                   
                     <#assign storeDetails2 = storeIssueDetails.getValue().get("issueMap")?if_exists>    		       
                     <#assign dayClosingQty = storeIssueDetails.getValue().get("dayClosingQty")?if_exists>                   
 <fo:table-row border-style="solid">
 
	   <fo:table-cell >
			<fo:block text-align="left" >
						 <fo:table>
						  <fo:table-column column-width="80pt"/>
	                      <fo:table-body>
						   <fo:table-row>
					           <fo:table-cell >
								   <fo:block text-align="left" font-size="10pt"> <#if storeIssueDetails.getKey()?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(storeIssueDetails.getKey(), "dd/MM/yyyy")?if_exists} </#if>   </fo:block>
							   </fo:table-cell>
	                          </fo:table-row>
						</fo:table-body>   
					</fo:table>	
	          </fo:block>
	    </fo:table-cell>    
	    <fo:table-cell  border-style="solid">
		    <fo:block text-align="left" >
				 <fo:table>
					 <fo:table-column column-width="440pt"/>
					 <fo:table-column column-width="430pt"/>
					 <fo:table-body>			 
					     <fo:table-row>		   	   
				             <fo:table-cell >
				                 <fo:block text-align="left" >			   	
			                         <fo:table >
										  <fo:table-column column-width="80pt"/>
										  <fo:table-column column-width="80pt"/>
										  <fo:table-column column-width="90pt"/>
										  <fo:table-column column-width="90pt"/>
										  <fo:table-column column-width="100pt"/>					  
                                             <fo:table-body>
												<#if storeDetails1?has_content>
												   <#assign storeDetails11 = storeDetails1.entrySet()?if_exists>												
												   <#list storeDetails11 as storeDetails111>
                                                         <#assign recQtyTotal=recQtyTotal+storeDetails111.getValue().get("ReceiptQty")>
                                                         <#assign recAmtTotal=recAmtTotal+storeDetails111.getValue().get("ReceiptAmount")> 
												   <fo:table-row>				          
													   <fo:table-cell border-style="solid">
														   <fo:block text-align="left" font-size="10pt">${storeDetails111.getValue().get("supplierInvoiceId")?if_exists}</fo:block>
													   </fo:table-cell>
													   <fo:table-cell border-style="solid">
														  <fo:block text-align="left" font-size="10pt">${storeDetails111.getValue().get("mrrNo")?if_exists}</fo:block>
													   </fo:table-cell>
													   <fo:table-cell border-style="solid">
														  <fo:block text-align="right" font-size="10pt" >${storeDetails111.getValue().get("ReceiptQty")?if_exists?string("##0.000")}</fo:block>
													   </fo:table-cell>
													   <fo:table-cell border-style="solid">
														  <fo:block text-align="right" font-size="10pt">${storeDetails111.getValue().get("ReceiptRate")?if_exists?string("##0.00")}</fo:block>
													   </fo:table-cell>
													   <fo:table-cell border-style="solid">
														  <fo:block text-align="right" font-size="10pt">${storeDetails111.getValue().get("ReceiptAmount")?if_exists?string("##0.00")}</fo:block>
													   </fo:table-cell>						   
							                        </fo:table-row>
                                                     </#list>                            
                                                   </#if>                          
					                           </fo:table-body>   
				                      </fo:table>				   
					             </fo:block>
					        </fo:table-cell>						   			
			                <fo:table-cell>
					            <fo:block text-align="left" >			   
									 <fo:table >
										  <fo:table-column column-width="80pt"/>
										  <fo:table-column column-width="80pt"/>										  
										  <fo:table-column column-width="90pt"/>
										  <fo:table-column column-width="80pt"/>
										  <fo:table-column column-width="100pt"/>
					                          <fo:table-body>		  
                                                 <#if storeDetails2?has_content>
                                                    <#assign storeDetails22 = storeDetails2.entrySet()?if_exists>
                                                    <#list storeDetails22 as storeDetails222>
                                                    <#assign issQtyTotal=issQtyTotal+storeDetails222.getValue().get("IssueQty")>
                                                    <#assign issAmtTotal=issAmtTotal+storeDetails222.getValue().get("IssueAmount")>  
												   <fo:table-row>
											           <fo:table-cell border-style="solid">
														  <fo:block text-align="center" font-size="10pt">${storeDetails222.getValue().get("IndentNo")?if_exists}</fo:block> 
													   </fo:table-cell>
                                                       <fo:table-cell border-style="solid">
														  <fo:block text-align="left" font-size="10pt">${storeDetails222.getValue().get("mrrNo")?if_exists}</fo:block>
													   </fo:table-cell>
													   <fo:table-cell border-style="solid">
														  <fo:block text-align="right" font-size="10pt">${storeDetails222.getValue().get("IssueQty")?if_exists?string("##0.000")}</fo:block>
													   </fo:table-cell>
													    <fo:table-cell border-style="solid">
														  <fo:block text-align="right" font-size="10pt">${storeDetails222.getValue().get("IssueRate")?if_exists?string("##0.00")}</fo:block>
													   </fo:table-cell >
													   <fo:table-cell border-style="solid">
														  <fo:block text-align="right" font-size="10pt">${storeDetails222.getValue().get("IssueAmount")?if_exists?string("##0.00")}</fo:block>
													   </fo:table-cell >							   
							                        </fo:table-row>
                                                       </#list>
                                                  </#if>                   
					                            </fo:table-body>   
			     	                      </fo:table>			 
							        </fo:block>
						       </fo:table-cell>		
	                      </fo:table-row>
					  </fo:table-body>   
				  </fo:table>	
             </fo:block>
        </fo:table-cell>				  
       <fo:table-cell >
		   <fo:block text-align="left" >
			  <fo:table>
			     <fo:table-column column-width="90pt"/>
                 <fo:table-body>
			         <fo:table-row>
			             <fo:table-cell >
						     <fo:block text-align="right" font-size="10pt">${dayClosingQty?if_exists?string("##0.000")} </fo:block>
						  </fo:table-cell>
                       </fo:table-row>
				   </fo:table-body>   
			    </fo:table>	
            </fo:block>
         </fo:table-cell>	    	   
      </fo:table-row>
     </#list> 
      <fo:table-row border-style="solid">
		   <fo:table-cell >
				<fo:block text-align="left" >
							 <fo:table>
							  <fo:table-column column-width="80pt"/>
		                      <fo:table-body>
							   <fo:table-row>
						           <fo:table-cell >
									   <fo:block text-align="left" > GRAND TOTAL  </fo:block>
								   </fo:table-cell>
		                          </fo:table-row>
							</fo:table-body>   
						</fo:table>	
		          </fo:block>
		    </fo:table-cell>    
	        <fo:table-cell  border-style="solid">
		       <fo:block text-align="left" >
				   <fo:table>
					   <fo:table-column column-width="440pt"/>
					   <fo:table-column column-width="430pt"/>
					   <fo:table-body>			 
					       <fo:table-row>		   	   
				               <fo:table-cell >
				                   <fo:block text-align="left" >			   	
			                          <fo:table >
										  <fo:table-column column-width="80pt"/>
										  <fo:table-column column-width="80pt"/>
										  <fo:table-column column-width="90pt"/>
										  <fo:table-column column-width="90pt"/>
										  <fo:table-column column-width="100pt"/>						  
                                             <fo:table-body>												
												   <fo:table-row>				          
													   <fo:table-cell >
														   <fo:block text-align="left" > </fo:block>
													   </fo:table-cell>
													   <fo:table-cell >
														  <fo:block text-align="left" ></fo:block>
													   </fo:table-cell>
													   <fo:table-cell border-style="solid">
														  <fo:block text-align="right" font-size="10pt" > ${recQtyTotal?string("##0.000")}</fo:block>
													   </fo:table-cell>
													   <fo:table-cell >
														  <fo:block text-align="right" >            </fo:block>
													   </fo:table-cell>
													   <fo:table-cell border-style="solid">
														  <fo:block text-align="right" font-size="10pt">${recAmtTotal?string("##0.00")}</fo:block>
													   </fo:table-cell>						   
							                        </fo:table-row>                                                                            
					                       </fo:table-body>   
				                      </fo:table>				   
					             </fo:block>
					        </fo:table-cell>						   			
			                <fo:table-cell>
					            <fo:block text-align="left" >			   
									 <fo:table >
										  <fo:table-column column-width="80pt"/>
										  <fo:table-column column-width="80pt"/>										  
										  <fo:table-column column-width="90pt"/>
										  <fo:table-column column-width="80pt"/>
										  <fo:table-column column-width="100pt"/>
					                          <fo:table-body>		                                                   
												   <fo:table-row>
											           <fo:table-cell >
														  <fo:block text-align="center" ></fo:block>  
													   </fo:table-cell>
													   <fo:table-cell >
														  <fo:block text-align="left" ></fo:block>  
													   </fo:table-cell>
													   <fo:table-cell border-style="solid">
														  <fo:block text-align="right" font-size="10pt">${issQtyTotal?string("##0.000")}</fo:block>
													   </fo:table-cell>
													    <fo:table-cell >
														  <fo:block text-align="right" > </fo:block>
													   </fo:table-cell >
													   <fo:table-cell border-style="solid">
														  <fo:block text-align="right" font-size="10pt">${issAmtTotal?string("##0.00")}</fo:block>
													   </fo:table-cell >							   
							                        </fo:table-row>                                                                    
					                            </fo:table-body>   
			     	                      </fo:table>			 
							        </fo:block>
						       </fo:table-cell>		
	                      </fo:table-row>
					  </fo:table-body>   
				  </fo:table>	
             </fo:block>
        </fo:table-cell>				  
       <fo:table-cell >
		   <fo:block text-align="left" >
			   <fo:table>
			      <fo:table-column column-width="90pt"/>
                      <fo:table-body>
			              <fo:table-row>
			                  <fo:table-cell >
						          <fo:block text-align="right" ></fo:block>
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
	    			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	       		 		<fo:block font-size="16pt" text-align="center">
	            		<#if productId?has_content> No Records Found....!<#else> ${errorMessage} </#if>   			   
	            			
	       		 		</fo:block>
	    			</fo:flow>
				</fo:page-sequence>				
	</#if>
</fo:root>
</#escape>	    