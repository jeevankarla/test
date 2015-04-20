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
			<fo:simple-page-master master-name="main" page-height="11.69in" page-width="8.27in"  margin-bottom=".1in" margin-left="0.5in" margin-right=".3in">
		        <fo:region-body margin-top="1.5in"/>
		        <fo:region-before extent="1in"/>
		        <fo:region-after extent="1in"/>        
		    </fo:simple-page-master>   
		</fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "StockStatementReport.pdf")}
        <#if errorMessage?has_content>
	<fo:page-sequence master-reference="main">
	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	   <fo:block font-size="14pt">
	           ${errorMessage}.
        </fo:block>
	</fo:flow>
	</fo:page-sequence>	
	<#else>
       <#if finalStockStatementMap?has_content>
	        <fo:page-sequence master-reference="main" font-size="12pt">	
	        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
	        		<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "DEPOT_SALES","propertyName" : "reportHeaderLable"}, true)>
	        		<#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "DEPOT_SALES","propertyName" : "reportSubHeaderLable"}, true)>
        			<fo:block keep-together="always" text-align="center" font-family="Arial" white-space-collapse="false" font-weight="bold">${reportHeader.description?if_exists}</fo:block>
        			<fo:block keep-together="always" text-align="center" font-family="Arial" white-space-collapse="false" font-weight="bold">${reportSubHeader.description?if_exists}</fo:block>				
                	<fo:block text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold" font-size = "12pt" font-family="Arial">STOCK SUMMARY</fo:block>
          			<fo:block text-align="center" keep-together="always"  white-space-collapse="false" font-family="Arial" font-size = "10pt"> From ${fromDate} - ${thruDate} </fo:block>
          			<fo:block text-align="left"  keep-together="always" font-family="Arial" white-space-collapse="false" font-size="8pt"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;    &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;                 &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
          			<fo:table font-family="Arial">
		                    <fo:table-column column-width="25%"/>
		                    <fo:table-column column-width="18%"/>
		                    <fo:table-column column-width="18%"/>
		                    <fo:table-column column-width="18%"/>
		                    <fo:table-column column-width="18%"/>
		                    <fo:table-body>
		                    	<fo:table-row>
					                    <fo:table-cell border-style="solid">
							            	<fo:block  text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">Particulars</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell border-style="solid">
							            	<fo:block  text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">Opening Balance</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell border-style="solid">
							            	<fo:block  text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">Inwards</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell border-style="solid">
							            	<fo:block  text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">Outwards</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">Closing Balance</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
							     <fo:table-row>
					                    <fo:table-cell >
							            	<fo:block  text-align="center" white-space-collapse="false" font-weight="bold"></fo:block>  
							            </fo:table-cell>
							             <fo:table-cell >
							            	<fo:block  text-align="center" font-size="6pt" white-space-collapse="false">Quantity          Rate          Value</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell >
							            	<fo:block  text-align="center" font-size="6pt" white-space-collapse="false">Quantity          Rate          Value</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell >
							            	<fo:block  text-align="center" font-size="6pt" white-space-collapse="false">Quantity          Rate          Value</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell >
							            	<fo:block text-align="center" font-size="6pt" white-space-collapse="false">Quantity          Rate          Value</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
						 </fo:table-body>
					</fo:table>
					<fo:block>_________________________________________________________________________</fo:block>
            	</fo:static-content>	        	
	        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
	        	<#assign StockStatementKeys = finalStockStatementMap.keySet()>
	        	<#list StockStatementKeys as category>
	        	<#assign StockStatementEntry = finalStockStatementMap.get(category)>
	        	<#assign StockStatementList = StockStatementEntry.entrySet()>
	        	<#assign categoryEntry = delegator.findOne("ProductCategory", {"productCategoryId" : category}, true)>
	        	<fo:table font-family="Arial">
		                    <fo:table-column column-width="25%"/>
		                     <fo:table-column column-width="6%"/>
		                    <fo:table-column column-width="6%"/>
		                    <fo:table-column column-width="6%"/>
		                    <fo:table-column column-width="6%"/>
		                    <fo:table-column column-width="6%"/>
		                    <fo:table-column column-width="6%"/>
		                    <fo:table-column column-width="6%"/>
		                    <fo:table-column column-width="6%"/>
		                    <fo:table-column column-width="6%"/>
		                    <fo:table-column column-width="6%"/>
		                    <fo:table-column column-width="6%"/>
		                    <fo:table-column column-width="6%"/>
		                    <fo:table-body>
		                    	<fo:table-row>
					                    <fo:table-cell border-style="solid">
							            	<fo:block  text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold">${categoryEntry.description?if_exists}</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell >
							            	<fo:block  text-align="left" font-size="8pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  text-align="left" font-size="8pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  text-align="left" font-size="8pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block text-align="left" font-size="8pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block text-align="left" font-size="8pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block text-align="left" font-size="8pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block text-align="left" font-size="8pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block text-align="left" font-size="8pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block text-align="left" font-size="8pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block text-align="left" font-size="8pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block text-align="left" font-size="8pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block text-align="left" font-size="8pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
							    							     	
							     <#list StockStatementList as stockStatement>
							     
							     <fo:table-row>
					                    <fo:table-cell border-style="solid">
							            	<fo:block  text-align="left" font-size="8pt" white-space-collapse="false">${stockStatement.getValue().get("productName")?if_exists}</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell border-style="solid">
							            	<fo:block  text-align="left" font-size="8pt" white-space-collapse="false">${stockStatement.getValue().get("openingBalance")}</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell border-style="solid">
							            	<fo:block  text-align="left" font-size="8pt" white-space-collapse="false"></fo:block>  
							            </fo:table-cell>
							             <fo:table-cell border-style="solid">
							            	<fo:block  text-align="left" font-size="8pt" white-space-collapse="false"></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block text-align="left" font-size="8pt" white-space-collapse="false">${stockStatement.getValue().get("receivedQty")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block text-align="left" font-size="8pt" white-space-collapse="false">${stockStatement.getValue().get("rate")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block text-align="left" font-size="8pt" white-space-collapse="false">${stockStatement.getValue().get("val")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block text-align="left" font-size="8pt" white-space-collapse="false">${stockStatement.getValue().get("issuedQty")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block text-align="left" font-size="8pt" white-space-collapse="false">${stockStatement.getValue().get("rate")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block text-align="left" font-size="8pt" white-space-collapse="false">${stockStatement.getValue().get("val")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block text-align="left" font-size="8pt" white-space-collapse="false">${stockStatement.getValue().get("closingBalance")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block text-align="left" font-size="8pt" white-space-collapse="false"></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block text-align="left" font-size="8pt" white-space-collapse="false"></fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
							     </#list>
						 </fo:table-body>
					</fo:table>
        		</#list>	
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