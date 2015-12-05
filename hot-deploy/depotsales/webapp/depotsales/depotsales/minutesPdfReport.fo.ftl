
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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-right=".3in" margin-top=".5in">
                <fo:region-body margin-top="1.1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "IndentReport.pdf")}
        <#if dayWiseEntriesLidast?has_content>
        <fo:page-sequence master-reference="main" font-size="12pt">	
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        		<fo:block text-align="left" font-size="13pt" keep-together="always"  white-space-collapse="false">
        			<fo:table>
			            <fo:table-column column-width="150pt"/>
			            <fo:table-column column-width="150pt"/>
			            <fo:table-column column-width="150pt"/>
			            <fo:table-column column-width="150pt"/> 
			            <fo:table-body>
			                <fo:table-row>
			                    <fo:table-cell number-columns-spanned="4">
					            	<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
                                    <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
					            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">${reportHeader.description?if_exists}</fo:block>
					            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false"> ${reportSubHeader.description?if_exists}</fo:block>
					            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
					            </fo:table-cell>
							</fo:table-row>
			            </fo:table-body>
			        </fo:table>
        		</fo:block>
        		
             	<fo:block  text-align="left" font-size="12pt" white-space-collapse="false">Minute of Purchase and Sales Committee meeting for the purchase of following item(s).</fo:block>
             	
             	<fo:block  text-align="left" font-size="12pt" white-space-collapse="false">he committee recommended/approved purchse of following items(s) as per the rates mention</fo:block>
             	
             	<fo:block  text-align="left" font-size="12pt" font-style="bold">against each to be procured from M/S : <fo:inline font-weight="bold">${partyName}</fo:inline></fo:block>
        			
        						
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">towards the requirement of user agency M/s : <fo:inline font-weight="bold"><#if supplierPartyId?has_content>${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, supplierPartyId, false)}<#else>&#160;</#if></fo:inline></fo:block>  
        		
        		<fo:block  text-align="left" font-size="12pt" font-style="bold">vide their Indent No:      dated :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MMM/yyyy")?if_exists}</fo:block>
        		
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
        		        	<fo:block>&#160;&#160;&#160;&#160;&#160;</fo:block>
        		        	<fo:block>&#160;&#160;&#160;&#160;&#160;</fo:block>
        		        	<fo:block>&#160;&#160;&#160;&#160;&#160;</fo:block>	
        		        					<fo:block>&#160;&#160;&#160;&#160;&#160;</fo:block>
        		        	        	
        		
        		<fo:block  text-align="left" font-size="14pt" font-style="bold">PRICE FIXATION CHART :</fo:block>
        		<fo:block>
             		<fo:table border-style="solid">
             		    <fo:table-column column-width="4%"/>
			            <fo:table-column column-width="35%"/>
			            <fo:table-column column-width="10%"/>
			            <fo:table-column column-width="10%"/>
			            <fo:table-column column-width="10%"/>
	                    <fo:table-column column-width="10%"/>
			            <fo:table-column column-width="10%"/>
			            <fo:table-column column-width="10%"/>
			            <fo:table-body>
			            
			                <fo:table-row>
			                    <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">SNo</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">Item</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">Unit</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="12pt" white-space-collapse="false">Quantity</fo:block>
					            	<fo:block   text-align="center" font-size="12pt" white-space-collapse="false">(KGS)</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="12pt" white-space-collapse="false">Purchase Rate</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="12pt" white-space-collapse="false">Int for 0 Days @0.00% per Annum</fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="12pt" white-space-collapse="false">Handling Charges @ 0.00%</fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block   text-align="left" font-size="12pt" white-space-collapse="false">Sale Price/Unit</fo:block>
					            </fo:table-cell>
					           
							</fo:table-row>
			            
			            
			                     <#assign sr=1>
			                    
			                     
			                  <#list OrderItemList as orderList>
			            
			                <fo:table-row>
			                    <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${sr} </fo:block>
					            </fo:table-cell>
					             <#assign productDetails = delegator.findOne("Product", {"productId" :orderList.productId}, true)>  
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${productDetails.get("productName")?if_exists} </fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">&#160;</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${orderList.get("quantity")?if_exists} </fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${orderList.get("unitPrice")?if_exists}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"> </fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${orderList.get("unitPrice")?if_exists}</fo:block>
					            </fo:table-cell>
					           
							</fo:table-row>
							<#assign sr=sr+1>
							</#list>
							
							
							
						</fo:table-body>
					</fo:table>
				</fo:block>
				
				<fo:block>&#160;&#160;&#160;&#160;&#160;</fo:block>
				<fo:block>&#160;&#160;&#160;&#160;&#160;</fo:block>
				<fo:block>&#160;&#160;&#160;&#160;&#160;</fo:block>
				<fo:block>&#160;&#160;&#160;&#160;&#160;</fo:block>
	             <fo:block>
	             <fo:block  text-align="left" font-size="14pt" font-style="bold">DETAILS OF PURCHASE &amp; SALES :</fo:block>
             		<fo:table border-style="solid">
             		    <fo:table-column column-width="4%"/>
			            <fo:table-column column-width="35%"/>
			            <fo:table-column column-width="10%"/>
			            <fo:table-column column-width="10%"/>
			            <fo:table-column column-width="10%"/>
	                    <fo:table-column column-width="10%"/>
			            <fo:table-column column-width="10%"/>
			            <fo:table-column column-width="10%"/>
			            <fo:table-body>
			            
			                <fo:table-row>
			                    <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">SNo</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">Item</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">Unit</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="12pt" white-space-collapse="false">Quantity</fo:block>
					            	<fo:block   text-align="center" font-size="12pt" white-space-collapse="false">(KGS)</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="12pt" white-space-collapse="false">Purchase Rate</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="12pt" white-space-collapse="false">Int for 0 Days @0.00% per Annum</fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="12pt" white-space-collapse="false">Handling Charges @ 0.00%</fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block   text-align="left" font-size="12pt" white-space-collapse="false">Sale Price/Unit</fo:block>
					            </fo:table-cell>
					           
							</fo:table-row>
			            
			            
			                     <#assign sr=1>
			                     <#assign totquantityKgs = 0>
			                      <#assign toTunitPrice = 0>
			                       <#assign totSalesValue = 0>
			                  <#list OrderItemList as orderList>
			            
			                <fo:table-row>
			                    <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${sr} </fo:block>
					            </fo:table-cell>
					             <#assign productDetails = delegator.findOne("Product", {"productId" :orderList.productId}, true)>  
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${productDetails.get("productName")?if_exists} </fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">&#160;</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            <#assign totquantityKgs=totquantityKgs+orderList.get("quantity")>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${orderList.get("quantity")?if_exists} </fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${orderList.get("unitPrice")?if_exists}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            
					                <#assign purchValue = 0>
					                <#if  orderList.get("quantity")?has_content>
					                      <#assign purchValue = orderList.get("quantity")*orderList.get("unitPrice")>
					                 </#if>
					                 
					                 <#assign toTunitPrice = toTunitPrice+purchValue>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"><#if purchValue!=0>${purchValue?if_exists}<#else>&#160;</#if></fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${orderList.get("unitPrice")?if_exists}</fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"><#if purchValue!=0>${purchValue?if_exists}<#else>&#160;</#if></fo:block>
					            </fo:table-cell>
					           
							</fo:table-row>
							<#assign sr=sr+1>
							</#list>
							
							 <fo:table-row>
			                    <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"> </fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">Total</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">&#160;</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${totquantityKgs} </fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					           
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${toTunitPrice}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${toTunitPrice} </fo:block>
					            </fo:table-cell>
					           
							</fo:table-row>
							
						</fo:table-body>
					</fo:table>
				</fo:block>
								<fo:block>&#160;&#160;&#160;&#160;&#160;</fo:block>
				
				<fo:block>3. Summary</fo:block>
				<fo:block>a) Actual Purchase Value</fo:block>
				<fo:block>b) Total Sale Value</fo:block>
				<fo:block>c) Difference of the Sale</fo:block>
				<fo:block>e) Value &amp;actual payment made to Mill:</fo:block>
				<fo:block>f) 0 days interest on the credit:</fo:block>
				<fo:block>g) Percentage of Trading Contribution: 0%</fo:block>
				<fo:block>4. Goods will be despatched on freight to-pay basis to: <fo:inline font-weight="bold">${partyName}</fo:inline> </fo:block>
				<fo:block>
				
				
				
				</fo:block>
				
				
				
				
				
				
				
				
					<fo:block>5. Payment will be made by user agency within BACK TO BACK/ ON CREDIT days / immediately failing which interest  11 per annum will be charged for the total number of days payment delayed.</fo:block>
	<fo:block>6. One total financial outflow in this transaction is Rs.</fo:block>
	<fo:block>7. Total supply including this transaction to the agency will Rs</fo:block>
	<fo:block>8. Payment dues with interest from the party: <fo:inline font-weight="bold">${partyName}</fo:inline>  as on  <fo:inline font-weight="bold">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MMM/yyyy")?if_exists}</fo:inline> is Rs. </fo:block>
	<fo:block>9. Payment Mill to be paid Cheque/Demand Draft for Rs.<fo:inline font-weight="bold">${toTunitPrice} </fo:inline></fo:block>
	<fo:block>10. No. of Days credit extended by Mills to NHDC from date of despatch ........</fo:block>
	<fo:block>11. No. of Days credit extended by NHDC to Agency from date of despatch </fo:block>
	<fo:block>12. Any other specific information ...................</fo:block>
	<fo:block>13. Local Taxes as applicable.</fo:block>
	<fo:block>.</fo:block>
	<fo:block>Advance Details: Cheque/DD No :Cr on Account amounting  received from user agency</fo:block>
				
				
				
				
				
				
				
				
				
				
				
				
				
				
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