
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
              <#if finalMap?has_content>
              <#assign finalMapEntryList = finalMap.entrySet()>
							<#list finalMapEntryList as finalMapEntry>
							  <#assign  taxCharge= finalMapEntry.getKey()>
							  <#assign invoiceWiseList = finalMapEntry.getValue()> 
        <fo:page-sequence master-reference="main" font-size="9pt">	
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
				<#--<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
                <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
				<fo:block  keep-together="always" text-align="center"  font-weight="bold"   font-size="12pt" white-space-collapse="false">${reportHeader.description?if_exists} </fo:block>
				<fo:block  text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">${BOAddress?if_exists}</fo:block>-->
        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                <fo:block text-align="right"    font-size="9pt" >Page - <fo:page-number/></fo:block>
                
				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		       <fo:block linefeed-treatment="preserve">${taxCharge}% ${taxType}</fo:block>
        		<fo:block>
             		<fo:table >
             		    <fo:table-column column-width="15%"/>
			            <fo:table-column column-width="15%"/>
			            <fo:table-column column-width="15%"/>
			            <fo:table-column column-width="15%"/>
	                    <fo:table-column column-width="15%"/>
			            <fo:table-column column-width="15%"/>
			            <fo:table-column column-width="15%"/>
			            <fo:table-column column-width="15%"/>
			            
			            <fo:table-body>
			            	
			                <fo:table-row>
								
			                    <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="9pt" font-weight="bold" white-space-collapse="false">Invoice Id</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="9pt"  font-weight="bold" white-space-collapse="false">Party Id</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="9pt" font-weight="bold" white-space-collapse="false">Party Name</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="9pt" font-weight="bold" white-space-collapse="false">Invoice Value</fo:block>
					            </fo:table-cell>
					            <#if taxType=="VAT_PUR">
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="9pt" font-weight="bold" white-space-collapse="false">Vat Amount</fo:block>
					            </fo:table-cell>
					             </#if>
					             <#if taxType=="CST_PUR">
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="9pt" font-weight="bold" white-space-collapse="false">Cst Amount</fo:block>
					            </fo:table-cell>
					            </#if>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="9pt" font-weight="bold" white-space-collapse="false">Surcharge Amount</fo:block>
					            </fo:table-cell>
					             
							</fo:table-row>
			                  
								
							
								<#list invoiceWiseList as eachEntry>
								
							   <fo:table-row>
								
			                    <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="9pt" font-weight="bold" white-space-collapse="false">${eachEntry.get("invoiceId")?if_exists}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="9pt"  font-weight="bold" white-space-collapse="false">${eachEntry.get("partyIdFrom")?if_exists}</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="9pt" font-weight="bold" white-space-collapse="false">${eachEntry.get("partyIdFromName")?if_exists}</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="9pt" font-weight="bold" white-space-collapse="false">${eachEntry.get("invoiceValue")?if_exists}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="9pt" font-weight="bold" white-space-collapse="false">${eachEntry.get("taxValue")?if_exists}</fo:block>
					            </fo:table-cell>
					           
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="9pt" font-weight="bold" white-space-collapse="false">${eachEntry.get("taxSurChargeValue")?if_exists}</fo:block>
					            </fo:table-cell>
					             
							</fo:table-row>
							 
							  </#list>
			                  
							 
						</fo:table-body>
					</fo:table>
				</fo:block>
			</fo:flow>
		</fo:page-sequence>
		 </#list>
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