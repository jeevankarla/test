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
	<fo:simple-page-master master-name="main" page-height="9in" page-width="12in"  margin-bottom="1in" margin-left=".3in" margin-right="1in">
        <fo:region-body margin-top="1.1in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "BoothWiseSales.txt")}
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
                    <fo:block text-align="left" font-size="10pt" keep-together="always"  white-space-collapse="false">&#160;                                         Booth Wise Sales Report </fo:block>
              		<fo:block font-size="10pt">----------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            </fo:static-content>		
   <fo:flow flow-name="xsl-region-body"  >	
     <fo:block >
				<fo:table>
					<fo:table-column column-width="60pt"/>
					<#list productSet as product> 
					<fo:table-column column-width="50pt"/>
					</#list> 
					<fo:table-column column-width="40pt"/>
						<fo:table-body>
							<fo:table-row > 
							     <fo:table-cell >  
								    <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">BoothId</fo:block>
							 	</fo:table-cell>
							 	<#list productSet as product> 
							    <#assign productDtl = delegator.findOne("Product", {"productId" : product}, true)>
							 	<fo:table-cell >  
								    <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">${productDtl.brandName}</fo:block>
							 	</fo:table-cell>
							 	</#list>
							 	<fo:table-cell >  
								    <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">Total</fo:block>
							 	</fo:table-cell>
							</fo:table-row > 
							
						</fo:table-body>
	                </fo:table>
         </fo:block> 		
 		<fo:block >
				<fo:table>
					<fo:table-column column-width="60pt"/>
					<#list productSet as product> 
					<fo:table-column column-width="50pt"/>
					</#list> 
					<fo:table-column column-width="40pt"/>
					<fo:table-body>
					   <#assign total=0>
						<#list boothWiseSalesList as orderLst>
						<fo:table-row > 
						     <fo:table-cell >  
							    <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">${orderLst.get("boothId")}</fo:block>
						 	</fo:table-cell>
						 	<#list productSet as product> 
						 	<fo:table-cell >  
							    <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">${orderLst.get(product)}</fo:block>
						 	</fo:table-cell>
						 	<#assign total=total+orderLst.get(product)>
						 	</#list>
						 	<fo:table-cell >  
							    <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">${total}</fo:block>
						 	</fo:table-cell>
						</fo:table-row > 
						</#list>
					</fo:table-body>
	                </fo:table>
        </fo:block> 	
     </fo:flow>
</fo:page-sequence>		
</fo:root>
</#escape>