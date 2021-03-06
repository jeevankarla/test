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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="8in"  margin-left=".3in" margin-right=".3in" margin-top=".5in" margin-bottom="0.5in">
                <fo:region-body margin-top="0.1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
      <#if finalList?has_content> 	    
        ${setRequestAttribute("OUTPUT_FILENAME", "channelWiseDespatch.txt")}
        <fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">		
        <fo:static-content flow-name="xsl-region-before">
              		<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"> &#160;${uiLabelMap.CommonPage}- <fo:page-number/></fo:block>
              		<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
            </fo:static-content>	
              	
		        	<fo:flow flow-name="xsl-region-body"  font-family="Courier,monospace">
		        	<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
                    <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
		        		
		        	<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">&#160;     ${reportHeader.description?if_exists}.</fo:block>
                    	<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">&#160;    ${reportSubHeader.description?if_exists}.</fo:block>
                    	<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date :${printDate?if_exists}</fo:block>
                    	<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">&#160;         LIST OF SHOPPEES TO PAY RENT FOR THE MONTH OF ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayStartfromDate, "MMMM-yyyy")}   </fo:block>
              			<fo:block text-align="left"  keep-together="always"  white-space-collapse="false">========================================================================</fo:block> 
		        		<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">SL   BVB	   BVB 										       RENT	       TAX  		   TOTAL 				REMARKS</fo:block> 
		        		<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">NO   CODE   NAME 									       AMOUNT 	    AMOUNT 		&#160; 				&#160;</fo:block> 
		        		<fo:block text-align="left"  keep-together="always"   white-space-collapse="false">========================================================================</fo:block> 
            	<fo:block>
                 	<fo:table>
                    <fo:table-column column-width="35pt"/>
                    <fo:table-column column-width="55pt"/> 
               	    <fo:table-column column-width="120pt"/>
            		<fo:table-column column-width="80pt"/> 	
            		<fo:table-column column-width="80pt"/>	
            		<fo:table-column column-width="80pt"/>
            		<fo:table-column column-width="75pt"/>
            		<fo:table-column column-width="75pt"/>
            		<fo:table-column column-width="75pt"/>
            		<fo:table-column column-width="75pt"/>
            		<fo:table-column column-width="75pt"/>
                    <fo:table-body>
                    <#assign sno=0>
                    <#assign rateAmt=0>
                    <#assign taxAmt=0>
                    <#assign totAmt=0>
                    <#assign totRentAmt=0>
                    <#assign totBasicAmt=0>
                    <#list finalList as shoppeRent>
                    <#if shoppeRent.get("rateAmount")!=0>
					<#assign sno=sno+1>
            			<fo:table-row>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" text-align="left" white-space-collapse="false">&#160;${sno}</fo:block>  
                       		</fo:table-cell>
                       		<fo:table-cell>
                           		<fo:block  keep-together="always" text-align="left" white-space-collapse="false">${shoppeRent.get("boothId")}</fo:block>  
                       		</fo:table-cell>
                       		<fo:table-cell>
                           		<fo:block  keep-together="always" text-align="left" white-space-collapse="false">${shoppeRent.get("facilityName")}</fo:block>  
                       		</fo:table-cell>
                       		<#assign rateAmt=(rateAmt+shoppeRent.get("rateAmount"))>
                       		<#assign totAmt=shoppeRent.get("rateAmount")>
           					<fo:table-cell>
	            				<fo:block  keep-together="always" text-align="right" white-space-collapse="false">${(shoppeRent.get("basicRateAmount"))?if_exists?string("#0.00")}</fo:block>  
	            			</fo:table-cell>
	            			<fo:table-cell>
	            				<fo:block  keep-together="always" text-align="right" white-space-collapse="false">${(totAmt-shoppeRent.get("basicRateAmount"))?if_exists?string("#0.00")}</fo:block>  
	            			</fo:table-cell>
	            			<#assign totBasicAmt = totBasicAmt+shoppeRent.get("basicRateAmount")>
	            			<#assign totRentAmt = totRentAmt+totAmt>
	           				<fo:table-cell>
		                		<fo:block  keep-together="always" text-align="right" white-space-collapse="false">${totAmt?if_exists?string("#0.00")}</fo:block>  
		                	</fo:table-cell>
						</fo:table-row>
						</#if>
						</#list>
						<fo:table-row>
						<fo:table-cell>
		            		<fo:block  keep-together="always">-----------------------------------------------------------------------------------------------------</fo:block>  
		            	</fo:table-cell>
				        </fo:table-row>
				            <fo:table-row>
				            	<fo:table-cell>
                           		<fo:block  keep-together="always" text-align="left" white-space-collapse="false"></fo:block>  
                       		</fo:table-cell>
                       		<fo:table-cell>
                           		<fo:block  keep-together="always" text-align="left"  white-space-collapse="false"></fo:block>  
                       		</fo:table-cell>
                       		<fo:table-cell>
                           		<fo:block  keep-together="always" text-align="left"  white-space-collapse="false">TOTALS</fo:block>  
                       		</fo:table-cell>
	            			<fo:table-cell>
		                		<fo:block  keep-together="always" text-align="right"  white-space-collapse="false">${(totBasicAmt)?if_exists?string("#0.00")}</fo:block>  
		                	</fo:table-cell>
	            			<fo:table-cell>
	            				<fo:block  keep-together="always" text-align="right"  white-space-collapse="false">${(totRentAmt-totBasicAmt)?if_exists?string("#0.00")}</fo:block>  
	            			</fo:table-cell>
	            			<fo:table-cell>
		                		<fo:block  keep-together="always" text-align="right"  white-space-collapse="false">${totRentAmt?if_exists?string("#0.00")}</fo:block>  
		                	</fo:table-cell>
				        </fo:table-row>
				        <fo:table-row>
						<fo:table-cell>
		            		<fo:block  keep-together="always">-----------------------------------------------------------------------------------------------------</fo:block>  
		            	</fo:table-cell>
				        </fo:table-row>
				         <fo:table-row>	
				            	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				            	</fo:table-cell>
				            	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				            	</fo:table-cell>
				        </fo:table-row>
				        <fo:table-row>	
				            <fo:table-cell>
				            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">Certification : This is certified that the above ${sno} milk shopee have to pay shopee rent </fo:block> 
				            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">for the Month of ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayStartfromDate, "MMMM-yyyy")} </fo:block>  
				            	</fo:table-cell>
				        </fo:table-row>
				       
						<fo:table-row>	
				            	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				            	</fo:table-cell>
				        </fo:table-row>
						<fo:table-row>	
				            	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				            	</fo:table-cell>
				        </fo:table-row>
				        <fo:table-row>	
				            	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				            	</fo:table-cell>
				        </fo:table-row>
						<fo:table-row>	
				            	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				            	</fo:table-cell>
				        </fo:table-row>
										        				        
				        <fo:table-row>
				            	<fo:table-cell>
				            		<fo:block text-align="left"  keep-together="always"  white-space-collapse="false">Prepared By    	    BVB I/C			    Preauditor	       Gen.Mgr(MKtg)</fo:block>  
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