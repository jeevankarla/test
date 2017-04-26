
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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".5in" margin-right=".5in" margin-top=".1in">
                <fo:region-body margin-top="0.1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1.5in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        
        
        ${setRequestAttribute("OUTPUT_FILENAME", "IndentReport.pdf")}
        <#if paymentList?has_content>
        <fo:page-sequence master-reference="main" font-size="10pt">	
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
				<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
                <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
				<fo:block  keep-together="always" text-align="center"  font-weight="bold"   font-size="12pt" white-space-collapse="false">${reportHeader.description?if_exists} </fo:block>
				<fo:block  text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">${BOAddress?if_exists}</fo:block>
				<fo:block  text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">${BOEmail?if_exists}</fo:block>
        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                <fo:block text-align="right"    font-size="10pt" >Page - <fo:page-number/></fo:block>
				<fo:block text-align="center" font-size="12pt" font-weight="bold" >PARTY WISE ADVANCE REPORT</fo:block>
				
                <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		       
        		<fo:block>
             		<fo:table >
             			
             		    <fo:table-column column-width="8%"/>
			            <fo:table-column column-width="15%"/>
			            <fo:table-column column-width="10%"/>
			            <fo:table-column column-width="10%"/>
			            <fo:table-column column-width="10%"/>
			            <#if days == "30days">
			            <fo:table-column column-width="10%"/>
			            </#if>
			            <fo:table-column column-width="10%"/>
			            <fo:table-column column-width="10%"/>
			            <fo:table-column column-width="10%"/>
			            <fo:table-column column-width="10%"/>
			            <fo:table-column column-width="10%"/>
			            <fo:table-body>
		            		 
			                 <fo:table-row>
								
					            <fo:table-cell border-style="solid">
					               
										<fo:block  text-align="center" font-size="10pt"  font-weight="bold" white-space-collapse="false">Party</fo:block>
									<fo:block  text-align="center" font-size="10pt"  font-weight="bold" white-space-collapse="false">Code</fo:block>
					            </fo:table-cell >
					            
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Party</fo:block>
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Name</fo:block>
					            	
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Advance Value</fo:block>
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Between</fo:block>
					            	 <#if days == "30days">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">0-30 Days</fo:block>
					                <#else>
					                <fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">0-45 Days</fo:block>
					             	</#if>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Advance Value</fo:block>
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Between</fo:block>
					            	 <#if days == "30days">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">31-60 Days</fo:block>
					            	<#else>
					                <fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">46-90 Days</fo:block>
					             	</#if>
					            </fo:table-cell>
					            <#if days == "30days">
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Advance Value</fo:block>
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Between</fo:block>
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">61-90 Days</fo:block>
					            </fo:table-cell>
					          </#if>
					          <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Advance Value</fo:block>
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Between</fo:block>
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">91-180 Days</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Advance Value</fo:block>
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Between</fo:block>
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">181 Days-1yr</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Advance Value</fo:block>
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Between</fo:block>
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">1yr-2yrs</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Advance Value</fo:block>
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Between</fo:block>
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">2yrs-3yrs</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Advance Value</fo:block>
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Above 3yrs</fo:block>
					            	
					            </fo:table-cell>
							</fo:table-row>  
								<#list paymentList as payment>
										<fo:table-row>
								            <fo:table-cell border-style="solid" >
								            	<fo:block  text-align="left" font-size="9pt" white-space-collapse="false">${payment.get("partyId")?if_exists}</fo:block>
								            </fo:table-cell >
								            								             
								            <fo:table-cell border-style="solid" >
								            	<fo:block  text-align="left" font-size="9pt" white-space-collapse="false">${payment.get("partyName")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell border-style="solid" >
								            	<fo:block  text-align="right" font-size="9pt" white-space-collapse="false">${payment.get("fstMntAdvPmntTotals")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell border-style="solid" >
								            	<fo:block  text-align="right" font-size="9pt" white-space-collapse="false">${payment.get("secMntAdvPmntTotals")?if_exists}</fo:block>
								            </fo:table-cell >
								            <#if days == "30days">
								            <fo:table-cell border-style="solid" >
								            	<fo:block  text-align="right" font-size="9pt" white-space-collapse="false">${payment.get("thrdMntAdvPmntTotals")?if_exists}</fo:block>
								            </fo:table-cell >
								             </#if>
								             <fo:table-cell border-style="solid" >
								            	<fo:block  text-align="right" font-size="9pt" white-space-collapse="false">${payment.get("frthToSixthMntAdvPmntTotals")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell border-style="solid" >
								            	<fo:block  text-align="right" font-size="9pt" white-space-collapse="false">${payment.get("sixthToOneYrAdvPmntTotals")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell border-style="solid" >
								            	<fo:block  text-align="right" font-size="9pt" white-space-collapse="false">${payment.get("oneToTwoYrsAdvPmntTotals")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell border-style="solid" >
								            	<fo:block  text-align="right" font-size="9pt" white-space-collapse="false">${payment.get("secndToThirdYrAdvPmntTotals")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell border-style="solid" >
								            	<fo:block  text-align="right" font-size="9pt" white-space-collapse="false">${payment.get("above3YrsAdvPmntTotals")?if_exists}</fo:block>
								            </fo:table-cell >
								            
										</fo:table-row> 
								
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

