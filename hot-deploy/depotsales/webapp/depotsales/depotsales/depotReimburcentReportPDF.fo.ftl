
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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-right=".2in" margin-top=".1in">
                <fo:region-body margin-top="0.1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1.5in"/>
            </fo:simple-page-master> 
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "IndentReport.pdf")}
        <#if customersList?has_content>
        <fo:page-sequence master-reference="main" font-size="7.5pt">	
       
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
				<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
                <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
				<fo:block  keep-together="always" text-align="center"  font-weight="bold"   font-size="7.5pt" white-space-collapse="false">${reportHeader.description?if_exists} </fo:block>
				<fo:block  text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="7.5pt" font-weight="bold">${BOAddress?if_exists}</fo:block>
        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                <fo:block text-align="right"    font-size="7.5pt" >Page - <fo:page-number/></fo:block>
                <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
        	</fo:static-content>
 
          
          
        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
				
                 
				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	   <#list customersList as eachCustomer>    
			<#assign finalList=allCustomersMap.get(eachCustomer)>
				<#assign customerName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, eachCustomer, true)>
				<fo:block>
					<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					<fo:table >
						  <fo:table-column column-width="100%"/>
						  <fo:table-body>
						  		  <fo:table-row>
						  		 	<fo:table-cell>
						  		 		<fo:block  text-align="left" font-size="7.5pt" font-weight="bold" white-space-collapse="false">Name of Agency:${customerName?if_exists}</fo:block>
						  		 	</fo:table-cell>
						  		 </fo:table-row>
						  		  <fo:table-row>
						  		 	<fo:table-cell>
						  		 		<fo:block  text-align="left" font-size="7.5pt" font-weight="bold" white-space-collapse="false">Destination: ${destinationMap.get(eachCustomer)?if_exists}</fo:block>
						  		 	</fo:table-cell>
						  		 </fo:table-row>
						  </fo:table-body>
					</fo:table >
					<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				</fo:block>   
        		<fo:block>
             		<fo:table >
             		    <fo:table-column column-width="3%"/>
                        <fo:table-column column-width="11%"/>
                        <fo:table-column column-width="10%"/>
                        <fo:table-column column-width="10%"/>
                        <fo:table-column column-width="8%"/>
                        <fo:table-column column-width="9%"/>
                        <fo:table-column column-width="11%"/>
                        <fo:table-column column-width="9%"/>
                        <fo:table-column column-width="5%"/>
                        <fo:table-column column-width="9%"/>
                        <fo:table-column column-width="9%"/>
                        <fo:table-column column-width="6%"/>
			            
			            <fo:table-body>
			            	<fo:table-row> 
			                    <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="7.5pt" font-weight="bold" white-space-collapse="false">S.No</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="7.5pt"  font-weight="bold" white-space-collapse="false">Name of </fo:block>
					            	<fo:block  text-align="center" font-size="7.5pt"  font-weight="bold" white-space-collapse="false">the State</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="7.5pt" font-weight="bold" white-space-collapse="false">Opening Stock at </fo:block>
					            	<fo:block   text-align="center" font-size="7.5pt" font-weight="bold" white-space-collapse="false">the beginning of  the Qtr</fo:block>
					            	<fo:block>
											<fo:table >
												 <fo:table-column column-width="50%"/>
												  <fo:table-column column-width="50%"/>
												  <fo:table-body>
												  		  <fo:table-row>
												  		 	<fo:table-cell border-style="solid" border-bottom="hidden">
												  		 		<fo:block  text-align="left" font-size="7.5pt" font-weight="bold" white-space-collapse="false">Qty</fo:block>
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
												  		 	</fo:table-cell>
												  		 
												  		 	<fo:table-cell border-style="solid" border-bottom="hidden">
												  		 		<fo:block  text-align="left" font-size="7.5pt" font-weight="bold" white-space-collapse="false">Value</fo:block>
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
												  		 	</fo:table-cell>
												  		 </fo:table-row>
												  </fo:table-body>
											</fo:table >
										</fo:block> 
					            </fo:table-cell >
					            
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="7.5pt" font-weight="bold" white-space-collapse="false">Inv No.</fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="7.5pt" font-weight="bold" white-space-collapse="false">Date</fo:block>
					            </fo:table-cell>
					           <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="7.5pt" font-weight="bold" white-space-collapse="false">Yarn Received</fo:block>
					            	<fo:block   text-align="center" font-size="7.5pt" font-weight="bold" white-space-collapse="false"> under MGPS</fo:block>
					            	<fo:block>
											<fo:table >
												  <fo:table-column column-width="50%"/>
												  <fo:table-column column-width="50%"/>
												  <fo:table-body>
												  		  <fo:table-row>
												  		 	<fo:table-cell border-style="solid" border-bottom="hidden">
												  		 		<fo:block  text-align="left" font-size="7.5pt" font-weight="bold" white-space-collapse="false">Qty</fo:block>
																 <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
												  		 	</fo:table-cell>
												  		
												  		 	<fo:table-cell border-style="solid" border-bottom="hidden">
												  		 		<fo:block  text-align="left" font-size="7.5pt" font-weight="bold" white-space-collapse="false">Value</fo:block>
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
												  		 	</fo:table-cell>
												  		 </fo:table-row>
												  </fo:table-body>
											</fo:table >
										</fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="7.5pt" font-weight="bold" white-space-collapse="false">Name of Supplier</fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="7.5pt" font-weight="bold" white-space-collapse="false">Yarn Reced.  </fo:block>
					            	<fo:block   text-align="center" font-size="7.5pt" font-weight="bold" white-space-collapse="false">Other than mgps </fo:block>
					            	<fo:block>
											<fo:table >
												   <fo:table-column column-width="50%"/>
												  <fo:table-column column-width="50%"/>
												  <fo:table-body>
												  		  <fo:table-row>
												  		 	<fo:table-cell border-style="solid" border-bottom="hidden">
												  		 		<fo:block  text-align="left" font-size="7.5pt" font-weight="bold" white-space-collapse="false">Qty</fo:block>
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
												  		 	</fo:table-cell>
												  		
												  		 	<fo:table-cell border-style="solid" border-bottom="hidden">
												  		 		<fo:block  text-align="left" font-size="7.5pt" font-weight="bold" white-space-collapse="false">Value</fo:block>
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
												  		 	</fo:table-cell>
												  		 </fo:table-row>
												  </fo:table-body>
											</fo:table >
										</fo:block>
					            </fo:table-cell>
								<fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="7.5pt" font-weight="bold" white-space-collapse="false">Name Of Mill</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="7.5pt" font-weight="bold" white-space-collapse="false">Total Yarn Sold  </fo:block>
					            	<fo:block   text-align="center" font-size="7.5pt" font-weight="bold" white-space-collapse="false">During the Quarter</fo:block>
									<fo:block>
											<fo:table >
												  <fo:table-column column-width="50%"/>
												  <fo:table-column column-width="50%"/>
												  <fo:table-body>
												  		  <fo:table-row>
												  		 	<fo:table-cell border-style="solid" border-bottom="hidden">
												  		 		<fo:block  text-align="left" font-size="7.5pt" font-weight="bold" white-space-collapse="false">Qty</fo:block>
																	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
												  		 	</fo:table-cell>
												  		
												  		 	<fo:table-cell border-style="solid" border-bottom="hidden">
												  		 		<fo:block  text-align="left" font-size="7.5pt" font-weight="bold" white-space-collapse="false">Value</fo:block>
																	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
												  		 	</fo:table-cell>
												  		 </fo:table-row>
												  </fo:table-body>
											</fo:table >
										</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="7.5pt" font-weight="bold" white-space-collapse="false">Closing Stock</fo:block>
					            	<fo:block>
											<fo:table >
												  <fo:table-column column-width="50%"/>
												  <fo:table-column column-width="50%"/>
												  <fo:table-body>
												  		  <fo:table-row>
												  		 	<fo:table-cell border-style="solid" border-bottom="hidden">
												  		 		<fo:block  text-align="left" font-size="7.5pt" font-weight="bold" white-space-collapse="false">Qty</fo:block>
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
												  		 	</fo:table-cell>
												  		
												  		 	<fo:table-cell border-style="solid" border-bottom="hidden">
												  		 		<fo:block  text-align="left" font-size="7.5pt" font-weight="bold" white-space-collapse="false">Value</fo:block>
																	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
												  		 	</fo:table-cell>
												  		 </fo:table-row>
												  </fo:table-body>
											</fo:table >
										</fo:block>
					            </fo:table-cell>
								<fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="7.5pt" font-weight="bold" white-space-collapse="false">Re-imbursement</fo:block>
					            </fo:table-cell>
							</fo:table-row>	
							<#assign reimbursementAmount=0>
			                <#list finalList as eachList>
                         
							<fo:table-row>
			                    <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="7.5pt" font-weight="bold" white-space-collapse="false">${eachList.get("sNo")?if_exists} </fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  text-align="left" font-size="7.5pt"  font-weight="bold" white-space-collapse="false">${eachList.get("stateName")?if_exists} </fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block>
											<fo:table >
												   <fo:table-column column-width="50%"/>
												  <fo:table-column column-width="50%"/>
												  <fo:table-body>
												  		  <fo:table-row>
												  		 	<fo:table-cell border-style="solid" border-bottom="hidden" >
												  		 		<fo:block  text-align="left" font-size="7.5pt" font-weight="bold" white-space-collapse="false">${eachList.get("ob")?if_exists} </fo:block>
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
												  		 	</fo:table-cell>
												  		
												  		 	<fo:table-cell border-style="solid" border-bottom="hidden">
												  		 		<fo:block  text-align="left" font-size="7.5pt" font-weight="bold" white-space-collapse="false">${eachList.get("obValue")?if_exists} </fo:block>
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
												  		 	</fo:table-cell>
												  		 </fo:table-row>
												  </fo:table-body>
											</fo:table >
										</fo:block> 
					            </fo:table-cell >
					            
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="7.5pt" font-weight="bold" white-space-collapse="false">${eachList.get("billno")?if_exists} </fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block   text-align="left" font-size="7.5pt" font-weight="bold" white-space-collapse="false">${eachList.get("invoiceDate")?if_exists} </fo:block>
					            </fo:table-cell>
					           <fo:table-cell border-style="solid">
					            	<fo:block>
											<fo:table >
												   <fo:table-column column-width="50%"/>
												  <fo:table-column column-width="50%"/>
												  <fo:table-body>
												  		  <fo:table-row>
												  		 	<fo:table-cell border-style="solid" border-bottom="hidden">
												  		 		<fo:block  text-align="left" font-size="7.5pt" font-weight="bold" white-space-collapse="false">${eachList.get("invoiceQTY")?if_exists} </fo:block>
																	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
												  		 	</fo:table-cell>
												  		
												  		 	<fo:table-cell border-style="solid" border-bottom="hidden">
												  		 		<fo:block  text-align="left" font-size="7.5pt" font-weight="bold" white-space-collapse="false">${eachList.get("invoiceAmount")?if_exists} </fo:block>
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
												  		 	</fo:table-cell>
												  		 </fo:table-row>
												  </fo:table-body>
											</fo:table >
										</fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block   text-align="left" font-size="7.5pt" font-weight="bold" white-space-collapse="false">${eachList.get("supplierName")?if_exists} </fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block>
											<fo:table >
												  <fo:table-column column-width="50%"/>
												  <fo:table-column column-width="50%"/>
												  <fo:table-body>
												  		  <fo:table-row>
												  		 	<fo:table-cell border-style="solid" border-bottom="hidden" >
												  		 		<fo:block  text-align="right" font-size="7.5pt" font-weight="bold" white-space-collapse="false">${eachList.get("otherQty")?if_exists} </fo:block>
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
												  		 	</fo:table-cell>
												  		
												  		 	<fo:table-cell border-style="solid" border-bottom="hidden">
												  		 		<fo:block  text-align="right" font-size="7.5pt" font-weight="bold" white-space-collapse="false">${eachList.get("otherValue")?if_exists} </fo:block>
																	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
												  		 	</fo:table-cell>
												  		 </fo:table-row>
												  </fo:table-body>
											</fo:table >
										</fo:block>
					            </fo:table-cell>
								<fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="7.5pt" font-weight="bold" white-space-collapse="false">${eachList.get("Mill")?if_exists} </fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
									<fo:block>
											<fo:table >
												  <fo:table-column column-width="50%"/>
												  <fo:table-column column-width="50%"/>
												  <fo:table-body>
												  		  <fo:table-row>
												  		 	<fo:table-cell border-style="solid" border-bottom="hidden">
												  		 		<fo:block  text-align="right" font-size="7.5pt" font-weight="bold" white-space-collapse="false">${eachList.get("invoiceQTY")?if_exists} </fo:block>
																	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
												  		 	</fo:table-cell>
												  		
												  		 	<fo:table-cell border-style="solid" border-bottom="hidden">
												  		 		<fo:block  text-align="right" font-size="7.5pt" font-weight="bold" white-space-collapse="false">${eachList.get("invoiceAmount")?if_exists} </fo:block>
																	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
												  		 	</fo:table-cell>
												  		 </fo:table-row>
												  </fo:table-body>
											</fo:table >
										</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block>
											<fo:table >
												  <fo:table-column column-width="50%"/>
												  <fo:table-column column-width="50%"/>
												  <fo:table-body>
												  		  <fo:table-row>
												  		 	<fo:table-cell border-style="solid" border-bottom="hidden">
												  		 		<fo:block  text-align="right" font-size="7.5pt" font-weight="bold" white-space-collapse="false">${eachList.get("cbQty")?if_exists} </fo:block>
																	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
												  		 	</fo:table-cell>
												  		
												  		 	<fo:table-cell border-style="solid" border-bottom="hidden">
												  		 		<fo:block  text-align="right" font-size="7.5pt" font-weight="bold" white-space-collapse="false">${eachList.get("cbValue")?if_exists} </fo:block>
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
																<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
												  		 	</fo:table-cell>
												  		 </fo:table-row>
												  </fo:table-body>
											</fo:table >
										</fo:block>
					            </fo:table-cell>
								<#assign reimbursementAmount=eachList.get("depotCharges")>
								<fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" font-size="7.5pt" font-weight="bold" white-space-collapse="false">${eachList.get("depotCharges")?if_exists} </fo:block>
					            </fo:table-cell>
						</fo:table-row>					    
						</#list>	
			         	<fo:table-row>	
								<fo:table-cell number-columns-spanned="12">
										<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
										<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
										<fo:block   text-align="left" font-size="11pt" font-weight="bold" white-space-collapse="false"> Certified that the above yarn supplies have actually been made and amount of reimbursement for Depot operation has been paid by NHDC.</fo:block>
										<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
										<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
										<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
										<fo:block   text-align="left" font-size="11pt" font-weight="bold" white-space-collapse="false">Amount of reimbursement claimed for depot operation (2% of value of yarn supply on actuals)Rs. ${reimbursementAmount?if_exists}</fo:block>
								</fo:table-cell>
						</fo:table-row>	

						<fo:table-row>	
								<fo:table-cell number-columns-spanned="12">
										<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
										<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
										<fo:block   text-align="right" font-size="11pt" font-weight="bold" white-space-collapse="false"> Signature of Executive Officer </fo:block>
								</fo:table-cell>
						</fo:table-row>	
						<fo:table-row>	
								<fo:table-cell number-columns-spanned="12">
										<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
										<fo:block   text-align="right" font-size="11pt" font-weight="bold" white-space-collapse="false"> (Name of User agency with Rubber Stamp)</fo:block>
								</fo:table-cell>
						</fo:table-row>	
						<fo:table-row>	
								<fo:table-cell number-columns-spanned="12">
										<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
										<fo:block   text-align="left" font-size="11pt" font-weight="bold" white-space-collapse="false">Chartered Accountant </fo:block>
								</fo:table-cell>
						</fo:table-row>		
					    </fo:table-body>
			            
					</fo:table>
				</fo:block>
			   		 <fo:block page-break-after="always"></fo:block>
			    </#list>
			</fo:flow>
			
			
		</fo:page-sequence>
		<#else>
    	<fo:page-sequence master-reference="main">
	    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	       		 <fo:block font-size="14pt">
 	            	No Orders Found
	       		 </fo:block>
	    	</fo:flow>
		</fo:page-sequence>	
    </#if> 
 </fo:root>
</#escape>