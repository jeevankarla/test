
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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".7in" margin-right=".1in" margin-top=".2in">
                <fo:region-body margin-top="0.1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1.5in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "stockreport.pdf")}
        <#if physicalInventoryPDF?has_content>
        <fo:page-sequence master-reference="main" font-size="10pt">	
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
				<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
				<fo:block  keep-together="always" text-align="center"  font-weight="bold"   font-size="12pt" white-space-collapse="false">${reportHeader.description?if_exists} </fo:block>
				<#--<fo:block  text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">${BOAddress?if_exists}</fo:block>-->
        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                <fo:block text-align="right"    font-size="10pt" >Page - <fo:page-number/></fo:block>
				<fo:block text-align="center" font-size="12pt" font-weight="bold" >STOCK REPORT </fo:block>
                <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		       
        		<fo:block>
             		<fo:table >
             			
             		    <fo:table-column column-width="9%"/>
             		    <fo:table-column column-width="8%"/>
             		    <fo:table-column column-width="10%"/>
             		    <fo:table-column column-width="9%"/>
             		    <fo:table-column column-width="9%"/>
             		    <fo:table-column column-width="11%"/>
             		    <fo:table-column column-width="9%"/>
             		    <fo:table-column column-width="9%"/>
             		    <fo:table-column column-width="9%"/>
             		    <fo:table-column column-width="10%"/>
             		              		    
			            
			            <fo:table-body>
			            	
			                <fo:table-row>
								
			                    <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Shipment ID</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="10pt"  font-weight="bold" white-space-collapse="false">supplier </fo:block>
					            	<fo:block  text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Invoice Id</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Depot</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Supplier</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">PORef</fo:block>
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">No</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Received</fo:block>
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Date</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Product Name</fo:block>
					            </fo:table-cell>
								 <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Stock</fo:block>
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Available</fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Available</fo:block>
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Quantity</fo:block>
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">(Kgs)</fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">unintCost</fo:block>
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">(Kgs)</fo:block>
					            </fo:table-cell>
 								
							</fo:table-row>
						  		<#list physicalInventoryPDF as eachList>
	                             <fo:table-row>
								
			                    <fo:table-cell border-style="solid"> 
					            	<fo:block  text-align="right" font-size="9pt" white-space-collapse="false">${eachList.shipmentId?if_exists}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  text-align="right" font-size="9pt"  white-space-collapse="false">${eachList.supplierInvoiceId?if_exists}</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="left" font-size="9pt"  white-space-collapse="false">${eachList.facilityName?if_exists}</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="left" font-size="9pt"  white-space-collapse="false">${eachList.partyName?if_exists}</fo:block>
					            </fo:table-cell>
								<fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" font-size="9pt"  white-space-collapse="false">${eachList.poRefNum?if_exists}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="left" font-size="9pt"  white-space-collapse="false">${eachList.estimatedShipDate?if_exists}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="left" font-size="9pt"  white-space-collapse="false">${eachList.productName?if_exists}</fo:block>
					            </fo:table-cell>
								 <fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" font-size="9pt"  white-space-collapse="false">${eachList.quantityOnHandTotal?if_exists}</fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" font-size="9pt"  white-space-collapse="false">${eachList.availbleQuantity?if_exists}</fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${eachList.unitCost?if_exists}</fo:block>
					            </fo:table-cell>
 								
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