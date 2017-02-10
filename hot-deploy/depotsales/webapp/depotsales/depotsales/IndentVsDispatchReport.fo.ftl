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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in" margin-top=".5in" margin-left=".05in" margin-right=".25in" >
                <fo:region-body margin-top="1.45in"/>
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
       <#if IndentVsDispatchMap?has_content>
	        <fo:page-sequence master-reference="main" font-size="12pt">	
	        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
 <#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
 			        <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
        			<fo:block keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${reportHeader.description?if_exists}</fo:block>
        			<fo:block text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${BOAddress?if_exists}</fo:block>				
                	<fo:block text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold" font-size = "12pt" font-family="Courier,monospace">INDENT VS DISPATCH REPORT</fo:block>
          			<fo:block text-align="center" keep-together="always"  white-space-collapse="false" font-family="Courier,monospace" font-size = "10pt"> From ${fromDate} - ${thruDate} </fo:block>
          			<fo:block text-align="left"  keep-together="always" font-family="Courier,monospace" white-space-collapse="false" font-size="8pt"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;    Print Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
          			
          			<fo:table>
          					<fo:table-column column-width="13%"/>
		                    <fo:table-column column-width="8%"/>
		                    <fo:table-column column-width="13%"/>
		                    <fo:table-column column-width="11%"/>
		                    <fo:table-column column-width="11%"/>
		                    <fo:table-column column-width="8%"/>
		                    <fo:table-column column-width="11%"/>
		                    <fo:table-column column-width="9%"/>
		                    <fo:table-column column-width="11%"/>
		                    <fo:table-column column-width="7%"/>
		                   
		                    <fo:table-body>
		                    	<fo:table-row>
					                   <fo:table-cell border-style="solid">
							            	<fo:block  text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">Order Id</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell border-style="solid">
							            	<fo:block  text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">Order Date</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell border-style="solid">
							            	<fo:block  text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">Party Name</fo:block>  
							            </fo:table-cell>
							            <#--<fo:table-cell border-style="solid">
							            	<fo:block  text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold">Product Code</fo:block>  
							            </fo:table-cell>-->
							           <fo:table-cell border-style="solid">
							            	<fo:block text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">Product Name</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell border-style="solid">
							            	<fo:block text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">Supplier Name</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">Indented Qty</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">Indent Value</fo:block>  
							            </fo:table-cell>
							           <fo:table-cell border-style="solid">
							            	<fo:block text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">Dispatched Qty</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">Dispatch Value</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">Diff Qty</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
							     </fo:table-body>
					</fo:table>
					
          			</fo:static-content>
          			<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
          			<fo:block>
          			<#assign OrderIds = IndentVsDispatchMap.keySet()>
          			<#list OrderIds as orderID>
          			<#assign orderWiseMap = IndentVsDispatchMap.get(orderID)>
          			<#assign orderWiseList = orderWiseMap.entrySet()>
          			<#--  <fo:block text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold">Order: -->
          			<fo:table>
          					<fo:table-column column-width="13%"/>
		                    <fo:table-column column-width="8%"/>
		                    <fo:table-column column-width="13%"/>
		                    <fo:table-column column-width="11%"/>
		                    <fo:table-column column-width="11%"/>
		                    <fo:table-column column-width="8%"/>
		                    <fo:table-column column-width="11%"/>
		                    <fo:table-column column-width="9%"/>
		                    <fo:table-column column-width="11%"/>
		                    <fo:table-column column-width="7%"/>
		                    <fo:table-body>
		                    	
							     <#list orderWiseList as orderWiseEntry>
							     <fo:table-row>
					                   <fo:table-cell border-style="solid">
							            	<fo:block  text-align="left" font-size="10pt" white-space-collapse="false">${orderWiseEntry.getValue().get("orderNo")?if_exists}</fo:block>  
							            </fo:table-cell>
							             <#--<fo:table-cell border-style="solid">																	
							            	<fo:block  text-align="left" font-size="10pt" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(orderWiseEntry.getValue().get("orderDate")?if_exists, "dd-MM-yy")}</fo:block>  
							            </fo:table-cell>-->
							            <fo:table-cell border-style="solid">																	
							            	<fo:block  text-align="left" font-size="10pt" white-space-collapse="false">${orderWiseEntry.getValue().get("orderDate")?if_exists}</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell border-style="solid">
							            	<fo:block  text-align="left" font-size="10pt" white-space-collapse="false">${orderWiseEntry.getValue().get("partyName")?if_exists}</fo:block>  
							            </fo:table-cell>
							             <#--<fo:table-cell border-style="solid">
							            	<fo:block  text-align="left" font-size="10pt" white-space-collapse="false">${orderWiseEntry.getValue().get("productCode")?if_exists}</fo:block>  
							            </fo:table-cell>-->
							             <fo:table-cell border-style="solid">
							            	<fo:block  text-align="left" font-size="10pt" white-space-collapse="false">${orderWiseEntry.getValue().get("productName")?if_exists}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block  text-align="left" font-size="10pt" white-space-collapse="false">${orderWiseEntry.getValue().get("supplierName")?if_exists}</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell border-style="solid">
							            	<fo:block  text-align="center" font-size="10pt" white-space-collapse="false">${orderWiseEntry.getValue().get("initialQty")?if_exists}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block  text-align="center" font-size="10pt" white-space-collapse="false">${orderWiseEntry.getValue().get("indentValue")?if_exists}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block  text-align="center" font-size="10pt" white-space-collapse="false">${orderWiseEntry.getValue().get("finalQty")?if_exists}</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell border-style="solid">
							            	<fo:block  text-align="center" font-size="10pt" white-space-collapse="false">${orderWiseEntry.getValue().get("dispatchValue")?if_exists}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block  text-align="center" font-size="10pt" white-space-collapse="false">${orderWiseEntry.getValue().get("diffQty")?if_exists}</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
							     </#list>
							    
							     </fo:table-body>  
						</fo:table>
          			</#list>
          			 <#if tempTotMap?has_content>
          			<fo:table>
          					<fo:table-column column-width="13%"/>
		                    <fo:table-column column-width="8%"/>
		                    <fo:table-column column-width="13%"/>
		                    <fo:table-column column-width="11%"/>
		                    <fo:table-column column-width="11%"/>
		                    <fo:table-column column-width="8%"/>
		                    <fo:table-column column-width="11%"/>
		                    <fo:table-column column-width="9%"/>
		                    <fo:table-column column-width="11%"/>
		                    <fo:table-column column-width="7%"/>
		                    <fo:table-body>
          			 <fo:table-row>
					                   <fo:table-cell border-style="solid">
							            	<fo:block  text-align="left" font-size="10pt" font-weight="bold" white-space-collapse="false">Total</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">																	
							            	<fo:block  text-align="left" font-size="10pt" white-space-collapse="false"></fo:block>  
							            </fo:table-cell>
							             <fo:table-cell border-style="solid">
							            	<fo:block  text-align="left" font-size="10pt" white-space-collapse="false"></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">																	
							            	<fo:block  text-align="left" font-size="10pt" white-space-collapse="false"></fo:block>  
							            </fo:table-cell>
							             <fo:table-cell border-style="solid">																	
							            	<fo:block  text-align="left" font-size="10pt" white-space-collapse="false"></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">																	
							            	<fo:block  text-align="left" font-size="10pt" font-weight="bold" white-space-collapse="false">${tempTotMap.get("initialQty")?if_exists}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">																	
							            	<fo:block  text-align="left" font-size="10pt" font-weight="bold" white-space-collapse="false">${tempTotMap.get("indentValue")?if_exists}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">																	
							            	<fo:block  text-align="left" font-size="10pt" font-weight="bold" white-space-collapse="false">${tempTotMap.get("finalQty")?if_exists}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">																	
							            	<fo:block  text-align="left" font-size="10pt" font-weight="bold" white-space-collapse="false">${tempTotMap.get("dispatchValue")?if_exists}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">																	
							            	<fo:block  text-align="left" font-size="10pt" font-weight="bold" white-space-collapse="false">${tempTotMap.get("diffQty")?if_exists}</fo:block>  
							            </fo:table-cell>
							            
							     </fo:table-row>
          			</fo:table-body>
          			</fo:table>
          			</#if>
          			</fo:block>
      			</fo:flow>
			</fo:page-sequence>
          			
          			<#else>
			<fo:page-sequence master-reference="main">
				<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			   		 <fo:block font-size="14pt">
			        	No Orders Found.
			   		 </fo:block>
				</fo:flow>
			</fo:page-sequence>	
		</#if>   
		</#if>
 </fo:root>
</#escape>