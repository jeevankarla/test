
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
        <#if finalList?has_content>
        <fo:page-sequence master-reference="main" font-size="10pt">	
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
				<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
                <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
				<fo:block  keep-together="always" text-align="center"  font-weight="bold"   font-size="12pt" white-space-collapse="false">${reportHeader.description?if_exists} </fo:block>
				<fo:block  text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">${BOAddress?if_exists}</fo:block>
        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                <fo:block text-align="right"    font-size="10pt" >Page - <fo:page-number/></fo:block>
                <#-- <fo:block text-align="left"    font-size="10pt" >STATEMENT SHOWING THE AGENCYWISE DETAILS OF COTTON/SILK/JUTE YARN SUPPLIED BY NATIONAL HANDLOOM DEVELOPMENT CORPORATION LIMITED UNDER THE SCHEME FOR SUPPLY OF YARN AT :<fo:inline font-weight="bold" > ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(daystart, "dd-MMM-yyyy")?if_exists} To:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayend, "dd-MMM-yyyy")?if_exists} </fo:inline></fo:block> -->
				<fo:block text-align="center" font-size="10pt" font-weight="bold" >STATE WISE SCHEME WISE CONSOLIDATED REPORT</fo:block>
				<fo:block text-align="center" font-size="10pt" font-weight="bold" > FOR THE PERIOD ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(daystart, "dd-MMM-yyyy")?if_exists} To:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayend, "dd-MMM-yyyy")?if_exists} </fo:block>
                <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		       
        		<fo:block>
             		<fo:table >
			            <fo:table-column column-width="20%"/>
             		    <fo:table-column column-width="13.3%"/>
			            <fo:table-column column-width="13.3%"/>
			            <fo:table-column column-width="13.3%"/>
			            <fo:table-column column-width="13.3%"/>
	                    <fo:table-column column-width="13.3%"/>
			            <fo:table-column column-width="13.3%"/>
			            <fo:table-body>
			            	<fo:table-row> 
								 <fo:table-cell  number-columns-spanned="6">
				            		<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold" >Product Category : ${prodCatName?if_exists}</fo:block>
				                 </fo:table-cell>
				  			 </fo:table-row>
				  			 
			                <fo:table-row>
					            <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="10pt"  font-weight="bold" white-space-collapse="false">STATE</fo:block>
					            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">TOTAL</fo:block>
					            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            	<fo:block>
					            		<fo:table>
								            <fo:table-column column-width="50%"/>
					             		    <fo:table-column column-width="50%"/>
								            <fo:table-body>
								            	<fo:table-row> 
													 <fo:table-cell border-style="solid">
									            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold" >VALUE</fo:block>
									            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 <fo:table-cell border-style="solid">
									            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold" >QTY</fo:block>
									            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 
									  			 </fo:table-row>
									  		</fo:table-body>
								  		</fo:table>
				            		</fo:block>
					            </fo:table-cell >
					            
					            
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">GENERAL SCHEME</fo:block>
					            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            	
					            	<fo:block>
					            		<fo:table>
								            <fo:table-column column-width="50%"/>
					             		    <fo:table-column column-width="50%"/>
								            <fo:table-body>
								            	<fo:table-row> 
													 <fo:table-cell border-style="solid">
									            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold" >VALUE</fo:block>
									            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									            		
									                 </fo:table-cell>
									                 <fo:table-cell border-style="solid">
									            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold" >QTY</fo:block>
									            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 
									  			 </fo:table-row>
									  		</fo:table-body>
								  		</fo:table>
				            		</fo:block>
					            </fo:table-cell>
					            
					            
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">MGP 10% Scheme (Depot)</fo:block>
					            	<fo:block>
					            		<fo:table>
								            <fo:table-column column-width="50%"/>
					             		    <fo:table-column column-width="50%"/>
								            <fo:table-body>
								            	<fo:table-row> 
													 <fo:table-cell border-style="solid">
									            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold" >VALUE</fo:block>
									            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 <fo:table-cell border-style="solid">
									            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold" >QTY</fo:block>
									            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 
									  			 </fo:table-row>
									  		</fo:table-body>
								  		</fo:table>
				            		</fo:block>
					            </fo:table-cell>
					            
					            
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">MGP 10% Scheme (Non Depot)</fo:block>
					            	<fo:block>
					            		<fo:table>
								            <fo:table-column column-width="50%"/>
					             		    <fo:table-column column-width="50%"/>
								            <fo:table-body>
								            	<fo:table-row> 
													 <fo:table-cell border-style="solid">
									            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold" >VALUE</fo:block>
									            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 <fo:table-cell border-style="solid">
									            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold" >QTY</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>	
									                 </fo:table-cell>
									                 
									  			 </fo:table-row>
									  		</fo:table-body>
								  		</fo:table>
				            		</fo:block>
					            </fo:table-cell>
								
								
					             <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">MGP Scheme (Depot)</fo:block>
					            	<fo:block>
					            		<fo:table>
								            <fo:table-column column-width="50%"/>
					             		    <fo:table-column column-width="50%"/>
								            <fo:table-body>
								            	<fo:table-row> 
													 <fo:table-cell border-style="solid">
									            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold" >VALUE</fo:block>
									            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 <fo:table-cell border-style="solid">
									            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold" >QTY</fo:block>
									            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 
									  			 </fo:table-row>
									  		</fo:table-body>
								  		</fo:table>
				            		</fo:block>
					            </fo:table-cell>
					          
					          <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">MGP Scheme (Non Depot)</fo:block>
					            	<fo:block>
					            		<fo:table>
								            <fo:table-column column-width="50%"/>
					             		    <fo:table-column column-width="50%"/>
								            <fo:table-body>
								            	<fo:table-row> 
													 <fo:table-cell border-style="solid">
									            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold" >VALUE</fo:block>
									            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 <fo:table-cell border-style="solid">
									            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold" >QTY</fo:block>
									            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 
									  			 </fo:table-row>
									  		</fo:table-body>
								  		</fo:table>
				            		</fo:block>
					            </fo:table-cell>
							</fo:table-row>
			                  <#list finalList as eachList>
	                            <#assign eachStateDetails = eachList.entrySet()>
								<#list eachStateDetails as eachBranchDetails>
									<#assign branch = eachBranchDetails.getKey()>
							        <#assign branchPartiesList = eachBranchDetails.getValue()>
                 				 <#if reportType=="DETAIL"> 		
									 <fo:table-row>
											 <fo:table-cell >
							            		<fo:block  text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold" >${branch?if_exists}</fo:block>
							                 </fo:table-cell>
						  			 </fo:table-row>
						  			 </#if>
							  	<#assign branchPartiesList2 = branchPartiesList.entrySet()>
								 <#list branchPartiesList2 as eachState>
								<#assign state = eachState.getKey()>
								<#assign stateDetails = eachState.getValue()>
							   <#if reportType=="DETAIL">
							  <fo:table-row>
										 <fo:table-cell >
						            		<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold" >${state?if_exists}</fo:block>
						                 </fo:table-cell>
						           
							  </fo:table-row>
							  </#if>
							  <#list stateDetails as eachCustomer>
							  	 <fo:table-row>
								
			                    <fo:table-cell border-style="solid">
					            	<fo:block  text-align="left" font-size="10pt" <#if eachCustomer.get("partyName") == "BRANCH SUB-TOTAL"> font-weight="bold" </#if> white-space-collapse="false">${eachCustomer.get("partyName")?if_exists} </fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block>
					            		<fo:table>
								            <fo:table-column column-width="50%"/>
					             		    <fo:table-column column-width="50%"/>
								            <fo:table-body>
								            	<fo:table-row> 
													 <fo:table-cell  border-style="solid" border-bottom="hidden" border-left="hidden">
									            		<fo:block text-align="right" font-size="7pt" white-space-collapse="false" <#if eachCustomer.get("partyName") == "BRANCH SUB-TOTAL"> font-weight="bold" </#if> >${eachCustomer.get("totInvoiceAMT")?if_exists?string("#0.00")} </fo:block>
									            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
								  						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 <fo:table-cell  border-style="solid" border-bottom="hidden" border-right="hidden">
									            		<fo:block  text-align="right" font-size="7pt" white-space-collapse="false"  <#if eachCustomer.get("partyName") == "BRANCH SUB-TOTAL"> font-weight="bold" </#if>>${eachCustomer.get("totInvoiceQTY")?if_exists?string("#0.00")} </fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
								  						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 
									  			 </fo:table-row>
									  		</fo:table-body>
								  		</fo:table>
								  		
				            		</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block>
					            		<fo:table>
								            <fo:table-column column-width="50%"/>
					             		    <fo:table-column column-width="50%"/>
								            <fo:table-body>
								            	<fo:table-row> 
													 <fo:table-cell  border-style="solid" border-bottom="hidden" border-left="hidden">
									            		<fo:block  text-align="right" font-size="7pt" white-space-collapse="false"  <#if eachCustomer.get("partyName") == "BRANCH SUB-TOTAL"> font-weight="bold" </#if>>${eachCustomer.get("generalInvoiceAMT")?if_exists?string("#0.00")}   </fo:block>
									            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 <fo:table-cell  border-style="solid" border-bottom="hidden" border-right="hidden">
									            		<fo:block text-align="right" font-size="7pt" white-space-collapse="false" <#if eachCustomer.get("partyName") == "BRANCH SUB-TOTAL"> font-weight="bold" </#if>>${eachCustomer.get("generalInvoiceQTY")?if_exists?string("#0.00")} </fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 
									  			 </fo:table-row>
									  		</fo:table-body>
								  		</fo:table>

				            		</fo:block>
					            </fo:table-cell>  
					            
					              <fo:table-cell border-style="solid">
					            	<fo:block>
					            		<fo:table>
								           <fo:table-column column-width="50%"/>
					             		    <fo:table-column column-width="50%"/>
								            <fo:table-body>
								            	<fo:table-row> 
													 <fo:table-cell  border-style="solid" border-bottom="hidden"  border-left="hidden"  >
									            		<fo:block  text-align="right" font-size="7pt" white-space-collapse="false" <#if eachCustomer.get("partyName") == "BRANCH SUB-TOTAL"> font-weight="bold" </#if> > ${eachCustomer.get("mgpsTenPerDepotInvoiceAMT")?if_exists?string("#0.00")} </fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 <fo:table-cell  border-style="solid" border-bottom="hidden"  border-right="hidden">
									                     <fo:block text-align="right" font-size="7pt" white-space-collapse="false" <#if eachCustomer.get("partyName") == "BRANCH SUB-TOTAL"> font-weight="bold" </#if>> ${eachCustomer.get("mgpsTenPerDepotInvoiceQTY")?if_exists?string("#0.00")} </fo:block>																				
									            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 
									  			 </fo:table-row>
									  		</fo:table-body>
								  		</fo:table>
				            		</fo:block>
					            </fo:table-cell>
					             
                                  <fo:table-cell border-style="solid">
					            	<fo:block>
					            		<fo:table>
								            <fo:table-column column-width="50%"/>
					             		    <fo:table-column column-width="50%"/>
								            <fo:table-body>
								            	<fo:table-row> 
													 <fo:table-cell  border-style="solid" border-bottom="hidden"  border-left="hidden" >
									            		<fo:block  text-align="right" font-size="7pt" white-space-collapse="false" <#if eachCustomer.get("partyName") == "BRANCH SUB-TOTAL"> font-weight="bold" </#if>>${eachCustomer.get("mgpsTenPerInvoiceAMT")?if_exists?string("#0.00")} </fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 <fo:table-cell  border-style="solid" border-bottom="hidden" border-right="hidden">
									            		<fo:block  text-align="right" font-size="7pt" white-space-collapse="false" <#if eachCustomer.get("partyName") == "BRANCH SUB-TOTAL"> font-weight="bold" </#if>>${eachCustomer.get("mgpsTenPerInvoiceQTY")?if_exists?string("#0.00")} </fo:block>
									                 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
													</fo:table-cell>
									                 
									  			 </fo:table-row>
									  		</fo:table-body>
								  		</fo:table>
				            		</fo:block>
						         </fo:table-cell> 
	
					            <fo:table-cell border-style="solid">
					            	<fo:block>
					            		<fo:table>
								            <fo:table-column column-width="50%"/>
					             		    <fo:table-column column-width="50%"/>
								            <fo:table-body>
								            	<fo:table-row> 
													 <fo:table-cell  border-style="solid" border-bottom="hidden"  border-left="hidden">
									            		<fo:block  text-align="right" font-size="7pt" white-space-collapse="false"  <#if eachCustomer.get("partyName") == "BRANCH SUB-TOTAL"> font-weight="bold" </#if>>${eachCustomer.get("mgpsDepotInvoiceAMT")?if_exists?string("#0.00")} </fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 <fo:table-cell  border-style="solid" border-bottom="hidden" border-right="hidden">
									            		<fo:block text-align="right" font-size="7pt" white-space-collapse="false" <#if eachCustomer.get("partyName") == "BRANCH SUB-TOTAL"> font-weight="bold" </#if>>${eachCustomer.get("mgpsDepotInvoiceQTY")?if_exists?string("#0.00")} </fo:block>
								            			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 
									  			 </fo:table-row>
									  		</fo:table-body>
								  		</fo:table>
				            		</fo:block>
					            </fo:table-cell>
					            
					            <fo:table-cell border-style="solid">
					            	<fo:block>
					            		<fo:table>
								            <fo:table-column column-width="50%"/>
					             		    <fo:table-column column-width="50%"/>
								            <fo:table-body>
								            	<fo:table-row> 
													 <fo:table-cell  border-style="solid" border-bottom="hidden"  border-left="hidden" >
									            		<fo:block text-align="right" font-size="7pt" white-space-collapse="false" <#if eachCustomer.get("partyName") == "BRANCH SUB-TOTAL"> font-weight="bold" </#if> >${eachCustomer.get("mgpsInvoiceAMT")?if_exists?string("#0.00")} </fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 <fo:table-cell  border-style="solid" border-bottom="hidden"  border-right="hidden">
									            		<fo:block text-align="right" font-size="7pt" white-space-collapse="false" <#if eachCustomer.get("partyName") == "BRANCH SUB-TOTAL"> font-weight="bold" </#if> >${eachCustomer.get("mgpsInvoiceQTY")?if_exists?string("#0.00")} </fo:block>
								            			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 
									  			 </fo:table-row>
									  		</fo:table-body>
								  		</fo:table>
				            		</fo:block>
					            </fo:table-cell>
					           
					          
					           
							</fo:table-row>
							</#list>
							</#list> 
							<#assign stateTotalsDetails= StateTotals.get(branch)>
								 <fo:table-row>
								
			                    <fo:table-cell border-style="solid">
					            	<fo:block  text-align="left" font-size="10pt"  font-weight="bold" white-space-collapse="false">${stateTotalsDetails.get("partyName")?if_exists} </fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block>
					            		<fo:table>
								            <fo:table-column column-width="50%"/>
					             		    <fo:table-column column-width="50%"/>
								            <fo:table-body>
								            	<fo:table-row> 
													 <fo:table-cell  border-style="solid" border-bottom="hidden" border-left="hidden">
									            		<fo:block text-align="right" font-size="7pt" white-space-collapse="false" font-weight="bold"> ${stateTotalsDetails.get("totInvoiceAMT")?if_exists?string("#0.00")} </fo:block>
									            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
								  						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 <fo:table-cell  border-style="solid" border-bottom="hidden" border-right="hidden">
									            		<fo:block  text-align="right" font-size="7pt" white-space-collapse="false"   font-weight="bold" >${stateTotalsDetails.get("totInvoiceQTY")?if_exists?string("#0.00")} </fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
								  						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 
									  			 </fo:table-row>
									  		</fo:table-body>
								  		</fo:table>
								  		
				            		</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block>
					            		<fo:table>
								            <fo:table-column column-width="50%"/>
					             		    <fo:table-column column-width="50%"/>
								            <fo:table-body>
								            	<fo:table-row> 
													 <fo:table-cell  border-style="solid" border-bottom="hidden" border-left="hidden">
									            		<fo:block  text-align="right" font-size="7pt" white-space-collapse="false"   font-weight="bold">${stateTotalsDetails.get("mgpsTenPerDepotInvoiceAMT")?if_exists?string("#0.00")} </fo:block>
									            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 <fo:table-cell  border-style="solid" border-bottom="hidden" border-right="hidden">
									            		<fo:block text-align="right" font-size="7pt" white-space-collapse="false" font-weight="bold" >${stateTotalsDetails.get("mgpsTenPerDepotInvoiceQTY")?if_exists?string("#0.00")} </fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 
									  			 </fo:table-row>
									  		</fo:table-body>
								  		</fo:table>

				            		</fo:block>
					            </fo:table-cell>  
					             <fo:table-cell border-style="solid">
					            	<fo:block>
					            		<fo:table>
								            <fo:table-column column-width="50%"/>
					             		    <fo:table-column column-width="50%"/>
								            <fo:table-body>
								            	<fo:table-row> 
													 <fo:table-cell  border-style="solid" border-bottom="hidden"  border-left="hidden" >
									            		<fo:block  text-align="right" font-size="7pt" white-space-collapse="false" font-weight="bold" >${stateTotalsDetails.get("mgpsTenPerInvoiceAMT")?if_exists?string("#0.00")} </fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 <fo:table-cell  border-style="solid" border-bottom="hidden" border-right="hidden">
									            		<fo:block  text-align="right" font-size="7pt" white-space-collapse="false"  font-weight="bold">${stateTotalsDetails.get("mgpsTenPerInvoiceQTY")?if_exists?string("#0.00")} </fo:block>
									                 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
													</fo:table-cell>
									                 
									  			 </fo:table-row>
									  		</fo:table-body>
								  		</fo:table>
				            		</fo:block>
						         </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block>
					            		<fo:table>
								            <fo:table-column column-width="50%"/>
					             		    <fo:table-column column-width="50%"/>
								            <fo:table-body>
								            	<fo:table-row> 
													 <fo:table-cell  border-style="solid" border-bottom="hidden"  border-left="hidden">
									            		<fo:block  text-align="right" font-size="7pt" white-space-collapse="false"  font-weight="bold" >${stateTotalsDetails.get("mgpsDepotInvoiceAMT")?if_exists?string("#0.00")} </fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 <fo:table-cell  border-style="solid" border-bottom="hidden" border-right="hidden">
									            		<fo:block text-align="right" font-size="7pt" white-space-collapse="false"  font-weight="bold">${stateTotalsDetails.get("mgpsDepotInvoiceQTY")?if_exists?string("#0.00")} </fo:block>
								            			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 
									  			 </fo:table-row>
									  		</fo:table-body>
								  		</fo:table>
				            		</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block>
					            		<fo:table>
								            <fo:table-column column-width="50%"/>
					             		    <fo:table-column column-width="50%"/>
								            <fo:table-body>
								            	<fo:table-row> 
													 <fo:table-cell  border-style="solid" border-bottom="hidden"  border-left="hidden" >
									            		<fo:block text-align="right" font-size="7pt" white-space-collapse="false" font-weight="bold" >${stateTotalsDetails.get("mgpsInvoiceAMT")?if_exists?string("#0.00")} </fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 <fo:table-cell  border-style="solid" border-bottom="hidden"  border-right="hidden">
									            		<fo:block text-align="right" font-size="7pt" white-space-collapse="false" font-weight="bold">${stateTotalsDetails.get("mgpsInvoiceQTY")?if_exists?string("#0.00")} </fo:block>
								            			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 
									  			 </fo:table-row>
									  		</fo:table-body>
								  		</fo:table>
				            		</fo:block>
					            </fo:table-cell>
					           
					            <fo:table-cell border-style="solid">
					            	<fo:block>
					            		<fo:table>
								           <fo:table-column column-width="50%"/>
					             		    <fo:table-column column-width="50%"/>
								            <fo:table-body>
								            	<fo:table-row> 
													 <fo:table-cell  border-style="solid" border-bottom="hidden"  border-left="hidden"  >
									            		<fo:block  text-align="right" font-size="7pt" white-space-collapse="false" font-weight="bold" >${stateTotalsDetails.get("generalInvoiceAMT")?if_exists?string("#0.00")} </fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 <fo:table-cell  border-style="solid" border-bottom="hidden"  border-right="hidden">
									                     <fo:block text-align="right" font-size="7pt" white-space-collapse="false" font-weight="bold" >${stateTotalsDetails.get("generalInvoiceQTY")?if_exists?string("#0.00")} </fo:block>																				
									            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                 </fo:table-cell>
									                 
									  			 </fo:table-row>
									  		</fo:table-body>
								  		</fo:table>
				            		</fo:block>
					            </fo:table-cell>
					           
							</fo:table-row>
								
							
							</#list>
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