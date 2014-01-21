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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
            margin-top="0.5in" margin-bottom="1in" margin-left=".3in" margin-right="1in">
        <fo:region-body margin-top="1in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "dgatepass.txt")}
<#assign lineNumber = 5>
<#assign numberOfLines = 60>
<#assign facilityNumberInPage = 0>
<#if routeWiseMap?has_content> 
	<#assign routeDetailsList =routeWiseMap.entrySet()>
	<#list routeDetailsList as routesMap>	
	 
	 <#assign boothDetailList = routesMap.getValue().get("boothWiseMap").entrySet()>
    		<#list boothDetailList as boothDetails>    	
				
<fo:page-sequence master-reference="main" force-page-count="no-force">					
			<fo:static-content flow-name="xsl-region-before"> <#assign lineNumber = 5> 
				<#assign facilityNumberInPage = 0>
              	<fo:block white-space-collapse="false" font-size="4pt"  font-family="Courier,monospace"  text-align="left">&#160;       D.K. Co.op Milk Producer's Union Ltd.</fo:block>  
              	<fo:block white-space-collapse="false" font-size="4pt"  font-family="Courier,monospace"  text-align="left">&#160;               MANIPAL - 576 119.</fo:block>
              	<fo:block white-space-collapse="false" font-size="4pt"  font-family="Courier,monospace"  text-align="left">&#160;        	  Finished Goods Division</fo:block>  
              	<fo:block white-space-collapse="false" font-size="4pt"  font-family="Courier,monospace"  text-align="left">&#160;         Delivery Note-Cum-Gate Pass</fo:block>
              	<fo:block>-------------------------------------------------------------------------</fo:block>
              	<#assign boothName = delegator.findOne("Facility", {"facilityId" : boothDetails.getKey()}, true)>
            	<fo:block white-space-collapse="false" font-size="4pt"  font-family="Courier,monospace"  text-align="left">Supply Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayBegin, "dd/MM/yyyy")}               			 			 FGS No:</fo:block>
                <fo:block white-space-collapse="false" font-size="4pt"  font-family="Courier,monospace"  text-align="left">Booth Name: ${boothName.facilityName?if_exists}              					Reference No:    </fo:block>
            	<fo:block>-------------------------------------------------------------------------</fo:block>
			    <fo:table>
					<fo:table-column column-width="35pt"/>
					<fo:table-column column-width="35pt"/>	
					<fo:table-column column-width="35pt"/>
					<fo:table-column column-width="35pt"/>
					<fo:table-column column-width="35pt"/>
					<fo:table-column column-width="35pt"/>
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell>		
								<fo:block text-align="left" font-size="4pt"  font-family="Courier,monospace">PRODUCT UNIT</fo:block>																									
							</fo:table-cell>
							<fo:table-cell>		
								<fo:block text-align="center" font-size="4pt"  font-family="Courier,monospace">FULL CRATES</fo:block>																									
							</fo:table-cell>
							<fo:table-cell>		
								<fo:block text-align="center" font-size="4pt"  font-family="Courier,monospace">LOOSE SACHET**</fo:block>																									
							</fo:table-cell>
							<fo:table-cell>		
								<fo:block text-align="center" font-size="4pt"  font-family="Courier,monospace">PACKED PACKS</fo:block>																									
							</fo:table-cell>
							<fo:table-cell>		
								<fo:block text-align="center" font-size="4pt"  font-family="Courier,monospace">NET - QTY</fo:block>																									
							</fo:table-cell>
						</fo:table-row>
					</fo:table-body>
				</fo:table>
				<fo:block>--------------------------------------------------------------------------</fo:block>
			    </fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Helvetica">	
           <fo:block>
				<fo:table>
					<fo:table-column column-width="35pt"/>
					<fo:table-column column-width="35pt"/>	
					<fo:table-column column-width="35pt"/>
					<fo:table-column column-width="35pt"/>
					<fo:table-column column-width="35pt"/>
					<fo:table-column column-width="35pt"/>
					<fo:table-body>
						<#assign boothList = boothDetails.getValue()>
						<#assign boothIter = boothList.entrySet()>
						<#list allProdList as product>
						<#list boothIter as booth>
						<#if product == booth.getKey()>	
							<fo:table-row>
								<#assign productDetails = delegator.findOne("Product", {"productId" : booth.getKey()}, true)>
								<fo:table-cell>		
									<fo:block text-align="left"  font-size="4pt"  font-family="Courier,monospace">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((productDetails.get("brandName")))),8)?if_exists}</fo:block>																									
								</fo:table-cell>
								<fo:table-cell>		
									<fo:block text-align="center" font-size="4pt"  font-family="Courier,monospace"><#if booth.getValue().get("Crates")?exists && booth.getValue().get("Crates") == 0><#else>${(booth.getValue().get("Crates"))?if_exists}</#if></fo:block>																									
								</fo:table-cell>
								<fo:table-cell>		
									<fo:block text-align="center" font-size="4pt"  font-family="Courier,monospace"><#if booth.getValue().get("excessCrates")?exists && booth.getValue().get("excessCrates") == 0><#else>${(booth.getValue().get("excessCrates"))?if_exists}</#if></fo:block>																								
								</fo:table-cell>
								<fo:table-cell>		
									<fo:block text-align="center" font-size="4pt"  font-family="Courier,monospace"><#if booth.getValue().get("Packs")?exists && booth.getValue().get("Packs") == 0><#else>${(booth.getValue().get("Packs"))?if_exists}</#if></fo:block>
								</fo:table-cell>
								<fo:table-cell>		
									<fo:block text-align="center" font-size="4pt"  font-family="Courier,monospace"><#if booth.getValue().get("qtyValue")?exists && booth.getValue().get("qtyValue") == 0><#else>${(booth.getValue().get("qtyValue"))?if_exists}</#if></fo:block>																									
								</fo:table-cell>
							</fo:table-row>
							</#if>
							</#list>
						</#list>
					</fo:table-body>
				</fo:table>
			</fo:block>
 			</fo:flow>						        	
</fo:page-sequence>
</#list>
</#list>
 <#else>
	<fo:page-sequence master-reference="main">
    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
       		 <fo:block font-size="14pt">
            	${uiLabelMap.OrderNoOrderFound}.
       		 </fo:block>
    	</fo:flow>
	</fo:page-sequence>
</#if>						
</fo:root>
</#escape>