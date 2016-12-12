
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
        <#if prodCatMap?has_content>
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
				<fo:block text-align="center" font-size="12pt" font-weight="bold" >FIBER WISE / COUNT WISE SALES</fo:block>
				<fo:block text-align="center" font-size="12pt" font-weight="bold" >FOR THE PERIOD ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(daystart, "dd-MMM-yyyy")?if_exists} To:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayend, "dd-MMM-yyyy")?if_exists} </fo:block>
                <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		       
        		<fo:block>
             		<fo:table >
             			
             		    <fo:table-column column-width="40%"/>
			            <fo:table-column column-width="15%"/>
			            <fo:table-column column-width="15%"/>
			            <fo:table-column column-width="15%"/>
	                    <fo:table-column column-width="15%"/>
			            <fo:table-body>
		            		 
			                 <fo:table-row>
								
					            <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="10pt"  font-weight="bold" white-space-collapse="false">Customer Name</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Qty</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Bdl Wt</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Rate</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Value</fo:block>
					            </fo:table-cell>
								
							</fo:table-row>  
							  <#assign prodCatMapEntryList = prodCatMap.entrySet()>
							<#list prodCatMapEntryList as prodCatMapEntry>
							  <#assign productCategory = prodCatMapEntry.getKey()>
							  <#assign prodWiseList = prodCatMapEntry.getValue()> 
							   <fo:table-row>
						            <fo:table-cell>
						            	<fo:block  text-align="left" font-size="13pt"  font-weight="bold" white-space-collapse="false">${productCategory?if_exists}</fo:block>
						            </fo:table-cell >
								</fo:table-row>
							
								<#list prodWiseList as eachList>
								<#assign eachEntryList = eachList.entrySet()>
								
								<#list eachEntryList as eachEntry>
								<#assign eachEntryDetaislList = eachEntry.getValue()>
								<fo:table-row>
						            <fo:table-cell>
						            	<fo:block  text-align="left" font-size="11pt"  font-weight="bold" white-space-collapse="false">${eachEntry.getKey()?if_exists}</fo:block>
						            </fo:table-cell >
								</fo:table-row> 
								<#list eachEntryDetaislList as eachEntry>
										<fo:table-row>
								            <fo:table-cell border-style="solid" >
								            	<fo:block  text-align="left" font-size="9pt" <#if eachEntry.get("partyName")=="SUB-TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${eachEntry.get("partyName")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if eachEntry.get("partyName")=="SUB-TOTAL"> font-weight="bold"  </#if>  white-space-collapse="false">${eachEntry.get("orderQty")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"   white-space-collapse="false">${eachEntry.get("BdlWt")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"   white-space-collapse="false"><#if eachEntry.get("rate")?has_content> ${eachEntry.get("rate")?if_exists?string("##0.00")} </#if> </fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if eachEntry.get("partyName")=="SUB-TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${eachEntry.get("orderValue")?if_exists?string("##0.00")}</fo:block>
								            </fo:table-cell >
										</fo:table-row> 
								
							     </#list>
								 </#list>
	                             
							  </#list>
			                  
							  </#list>
	                                  <fo:table-row>
							            <fo:table-cell border-style="solid" >
							            	<fo:block  text-align="left" font-size="10pt" font-weight="bold"  white-space-collapse="false">TOTAL</fo:block>
							            </fo:table-cell >
							            <fo:table-cell  border-style="solid">
							            	<fo:block  text-align="right" font-size="10pt" font-weight="bold"  white-space-collapse="false">${totalsMap.get("orderQty")?if_exists}</fo:block>
							            </fo:table-cell >
							            <fo:table-cell  border-style="solid">
							            	<fo:block  text-align="right" font-size="10pt"   white-space-collapse="false"></fo:block>
							            </fo:table-cell >
							            <fo:table-cell  border-style="solid">
							            	<fo:block  text-align="right" font-size="10pt"   white-space-collapse="false"></fo:block>
							            </fo:table-cell >
							            <fo:table-cell  border-style="solid">
							            	<fo:block  text-align="right" font-size="10pt"  font-weight="bold"  white-space-collapse="false">${totalsMap.get("orderValue")?if_exists?string("##0.00")}</fo:block>
							            </fo:table-cell >
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

