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

<#-- do not display columns associated with values specified in the request, ie constraint values -->

<fo:layout-master-set>
	<fo:simple-page-master master-name="main" page-height="12in" page-width="25in"
            margin-top=".3in" margin-bottom=".5in">
        <fo:region-body margin-top="1in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "QuotaList.txt")}
<fo:page-sequence master-reference="main" force-page-count="no-force">					
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
				<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;              ${reportHeader.description?if_exists}</fo:block>
				<fo:block keep-together="always"  text-align="left" white-space-collapse="false">&#160;          STATEMENT SHOWING ROUTE WISE QUOTA OF LIQUID MILK SALES FOR ${supplyDate} </fo:block>  
              	<fo:block>----------------------------------------------------------------------------------------------------------------------------------</fo:block>
			<fo:block >
				<fo:table width="100%" table-layout="fixed" space-after="0.0in">
					<fo:table-column column-width="40pt"/>
        		<#list productList as product>
        		<fo:table-column column-width="64pt"/>
        		</#list>
        		<fo:table-column column-width="45pt"/>
        		
					<fo:table-body>
					 <fo:table-row>
						<fo:table-cell>
							<fo:block>Route</fo:block>
						</fo:table-cell>
						<#if productList?exists> 
	       				<#list productList as product>				
						<fo:table-cell>
							<fo:block text-align="center" keep-together="always">${product.brandName}<#if product_has_next></#if></fo:block>
						</fo:table-cell>
						</#list>
					</#if>
					    <fo:table-cell>
							<fo:block text-align="right">TOTAL</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
			</fo:block>
			<fo:block>-------------------------------------------------------------------------------------------------------------------------</fo:block>
			</fo:static-content>
			
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
			<fo:block font-family="Courier,monospace">
				<fo:table width="100%">
				<#assign routequota = (routesResultMap).entrySet()>
				<fo:table-column column-width="33pt"/>
				<#list productList as product>
        		<fo:table-column column-width="59pt"/>
        		</#list>
        		<fo:table-column column-width="59pt"/>
        		<fo:table-column column-width="59pt"/>
        		<fo:table-column column-width="59pt"/>        		
        		<fo:table-column column-width="65pt"/>
 				<fo:table-body>
 				    <fo:table-row>
					    <fo:table-cell>
					       <fo:block>-------------------------------------------------------------------------------------------------------------------------</fo:block>
					    </fo:table-cell>	
					</fo:table-row>
					<#list routequota as route>
					<#assign eachRouteTotals = 0>
					<#assign routeName = route.getKey()>
					<fo:table-row>
					<fo:table-cell>
						<fo:block >${routeName}</fo:block>
					</fo:table-cell>
					<#assign routeList= route.getValue()>
					<#assign routeQuant = (routeList).entrySet()>
					<#list routeQuant as routePro>
					<#assign routeTotFin = routePro.getValue()>
					<#assign eachRouteTotals = eachRouteTotals + routePro.getValue()>
					<fo:table-cell>
						<fo:block text-align="right">${routeTotFin}</fo:block>
					</fo:table-cell>
					</#list>
					<fo:table-cell>
						<fo:block></fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block ></fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right">${eachRouteTotals}</fo:block>
					</fo:table-cell>	
					</fo:table-row>
					<fo:table-row>
					    <fo:table-cell>
					       <fo:block>-------------------------------------------------------------------------------------------------------------------------</fo:block>
					    </fo:table-cell>	
					</fo:table-row>
					</#list>
			    </fo:table-body>
			</fo:table>
			</fo:block>	
			</fo:flow>						        	
   </fo:page-sequence>
   </fo:root>
</#escape>