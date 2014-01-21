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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in" margin-left=".5in" margin-right=".5in">
                <fo:region-body margin-top=".4in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        <fo:page-sequence master-reference="main">		        	
		<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
		<#assign prodLineNum = 0>
        <#assign routeMapEntries = routeMap.entrySet()>        
        <#list routeMapEntries as routeValues>
        	<#assign boothValuesList = routeValues.getValue()>
        	<#list boothValuesList as boothValues>
        		<#assign boothsList = boothValues.get("boothList")>        		
        		<#list boothsList as boothEntries>
		        	<fo:block  border-style="solid" font-family="Courier,monospace">
		        			<fo:table  table-layout="fixed" width="100%" space-before="0.2in">
		        				<fo:table-column column-width="50pt"/>
        						<fo:table-column column-width="50pt"/>
        						<fo:table-column column-width="50pt"/>
        						<fo:table-column column-width="50pt"/>
        						<fo:table-column column-width="50pt"/>
        						<fo:table-column column-width="50pt"/>
        						<fo:table-body>
        							<fo:table-row width="100%">
        								<fo:table-cell> 
        									<fo:block >    
        										<fo:table  table-layout="fixed" width="100%" space-before="0.2in">
							        				<fo:table-column column-width="19%"/>
					        						<fo:table-column column-width="59%"/>
					        						<fo:table-column column-width="32.5%"/>
					        						<fo:table-column column-width="18.5%"/>
					        						<fo:table-column column-width="27%"/>
					        						<fo:table-column column-width="24%"/>
					        						<fo:table-column column-width="32.5%"/>
					        						<fo:table-column column-width="27%"/>
					        						<fo:table-column column-width="32.5%"/>
					        						<fo:table-header>
						        						<fo:table-row >
					        								<fo:table-cell>
					        									<fo:block font-size="7pt">.</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							<fo:table-row >
					        								<fo:table-cell>
					        									<fo:block font-size="7pt">.</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							<fo:table-row >
					        								<fo:table-cell>
					        									<fo:block font-size="7pt">.</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							<fo:table-row >
					        								<fo:table-cell>
					        									<fo:block font-size="7pt">.</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							<fo:table-row >
					        								<fo:table-cell>
					        									<fo:block font-size="7pt" keep-together="always"  white-space-collapse="false">.                                                                             ${boothEntries.get("invoiceNumber")?if_exists}</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							<fo:table-row >
					        								<fo:table-cell>
					        									<fo:block font-size="7pt" keep-together="always"  white-space-collapse="false">.                                                                             ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(estimatedDeliveryDate, "dd/MM/yyyy")?if_exists}</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							<fo:table-row >
					        								<fo:table-cell>
					        									<fo:block font-size="7pt">.</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							<fo:table-row >
					        								<fo:table-cell>
					        									<fo:block font-size="7pt" keep-together="always"  white-space-collapse="false">.              ${boothEntries.get("booth")?if_exists}</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							<fo:table-row >
					        								<fo:table-cell>
					        									<fo:block font-size="7pt" keep-together="always"  white-space-collapse="false">.              ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, boothEntries.get("ownerPartyId"), false)}</fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell>
					        									<fo:block font-size="7pt"></fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell>
					        									<fo:block font-size="7pt"></fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell>
					        									<fo:block font-size="7pt"></fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell>
					        									<fo:block font-size="7pt"></fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell>
					        									<fo:block font-size="7pt"></fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell>
					        									<fo:block font-size="7pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${boothValues.get("route")} </fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							<fo:table-row >
					        								<fo:table-cell>
					        									<fo:block font-size="7pt" keep-together="always"  white-space-collapse="false">.              ${boothEntries.get("area")?if_exists}</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							<fo:table-row >
					        								<fo:table-cell>
					        									<fo:block font-size="7pt">.</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							<fo:table-row >
					        								<fo:table-cell>
					        									<fo:block font-size="7pt">.</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
						        						<fo:table-row font-weight="bold">
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="left">----------------------------------------------------------------------------------------------------------</fo:block>
					        							</fo:table-cell>					        							
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="left"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
					        							</fo:table-cell>
					        							</fo:table-row>
					        						</fo:table-header>
					        						<fo:table-body>
					        							<#assign productList = boothEntries.get("productList")>
										        		<#assign totQty =0>
										        		<#assign totBasicValue =0>
										        		<#assign totVatValue =0>
										        		<#assign totalValue =0>
										        		<#assign invoiceLineNum =0>
										        		<#assign rowNum = 1>
					        						    <#list productList as productEntries>
					        						    <#assign prodLineNum = prodLineNum+1>
					        						    <#assign invoiceLineNum = invoiceLineNum+1>
					        						    <#if (invoiceLineNum == 9||invoiceLineNum == 1)>
					        							<fo:table-row font-weight="bold">
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="center"></fo:block>
					        							</fo:table-cell>					        							
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="left"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right">B/F</fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right">${totBasicValue?if_exists?string("#0.00")}</fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right">${totVatValue?if_exists?string("#0.00")}</fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right">${totalValue?if_exists?string("#0.00")}</fo:block>
					        							</fo:table-cell>
					        							</fo:table-row>
					        							</#if>
										        		<#assign totQty =totQty+productEntries.get("quantity")>
										        		<#assign totBasicValue =totBasicValue+productEntries.get("basicValue")>
										        		<#assign totVatValue =totVatValue+productEntries.get("vatValue")>
										        		<#assign totalValue =totalValue+productEntries.get("totalValue")>
					        							<fo:table-row>
					        								<fo:table-cell border-style="solid">
					        									<fo:block text-align="center" font-size="7pt">${productEntries.get("productId")}</fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell border-style="solid">
					        									<fo:block font-size="7pt">&#160;&#160;${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(productEntries.get("productName"))),16)}</fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell border-style="solid">
					        									<fo:block text-align="right" font-size="7pt">${productEntries.get("VAT_Percentage")?string("#0.0")}</fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell border-style="solid">
					        									<fo:block text-align="right" font-size="7pt">${productEntries.get("quantity")}</fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell border-style="solid">
					        									<fo:block text-align="right" font-size="7pt">&#160;${productEntries.get("basicRate")?string("#0.00")}</fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell border-style="solid">
					        									<fo:block text-align="right" font-size="7pt">${productEntries.get("vatRate")?string("#0.00")}</fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell border-style="solid">
					        									<fo:block text-align="right" font-size="7pt">${(productEntries.get("basicValue"))?string("#0.00")}</fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell border-style="solid">
					        									<fo:block text-align="right" font-size="7pt">${(productEntries.get("vatValue"))?string("#0.00")}</fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell border-style="solid">
					        									<fo:block text-align="right" font-size="7pt">${((productEntries.get("basicRate")*productEntries.get("quantity"))+(productEntries.get("quantity")*productEntries.get("vatRate")))?string("#0.00")}</fo:block>
					        								</fo:table-cell>	
					        								<#assign rowNum = rowNum+1>				        								
					        							</fo:table-row>
					        				<#if invoiceLineNum == 8>
					        				<#assign rowNum = 1>
					        						<fo:table-row>
        												<fo:table-cell>
        												<fo:block font-size="7pt">.</fo:block>
        												</fo:table-cell>
        											</fo:table-row>
        											<fo:table-row>
        												<fo:table-cell>
        												<fo:block font-size="7pt">.</fo:block>
        												</fo:table-cell>
        											</fo:table-row>
					        						<fo:table-row font-weight="bold">
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="center"></fo:block>
					        							</fo:table-cell>					        							
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="left">&#160;AMOUNT INCLUSIVE OF EXCISE DUTY</fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right">C/O</fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right">${totBasicValue?if_exists?string("#0.00")}</fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right">${totVatValue?if_exists?string("#0.00")}</fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right">${totalValue?if_exists?string("#0.00")}</fo:block>
					        							</fo:table-cell>
						        						</fo:table-row>
						        						<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<#if prodLineNum == 16 >
						        						<#assign prodLineNum = 0 >
				        								<fo:table-row >
				        									<fo:table-cell>
				        										<fo:block font-size="7pt" page-break-after="always"></fo:block>
				        									</fo:table-cell>
				        								</fo:table-row>
				        								<#else>
				        								<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row >
					        								<fo:table-cell>
					        									<fo:block font-size="7pt" keep-together="always"  white-space-collapse="false">.                                                                             ${boothEntries.get("invoiceNumber")?if_exists}</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							<fo:table-row >
					        								<fo:table-cell>
					        									<fo:block font-size="7pt" keep-together="always"  white-space-collapse="false">.                                                                             ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(estimatedDeliveryDate, "dd/MM/yyyy")?if_exists}</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							<fo:table-row >
					        								<fo:table-cell>
					        									<fo:block font-size="7pt">.</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							<fo:table-row >
					        								<fo:table-cell>
					        									<fo:block font-size="7pt" keep-together="always"  white-space-collapse="false">.              ${boothEntries.get("booth")?if_exists}</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							<fo:table-row >
					        								<fo:table-cell>
					        									<fo:block font-size="7pt" keep-together="always"  white-space-collapse="false">.              ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, boothEntries.get("ownerPartyId"), false)}</fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell>
					        									<fo:block font-size="7pt"></fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell>
					        									<fo:block font-size="7pt"></fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell>
					        									<fo:block font-size="7pt"></fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell>
					        									<fo:block font-size="7pt"></fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell>
					        									<fo:block font-size="7pt"></fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell>
					        									<fo:block font-size="7pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${boothValues.get("route")} </fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							<fo:table-row >
					        								<fo:table-cell>
					        									<fo:block font-size="7pt" keep-together="always"  white-space-collapse="false">.              ${boothEntries.get("area")?if_exists}</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							<fo:table-row >
					        								<fo:table-cell>
					        									<fo:block font-size="7pt">.</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							<fo:table-row >
					        								<fo:table-cell>
					        									<fo:block font-size="7pt">.</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
						        						<fo:table-row font-weight="bold">
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="left">----------------------------------------------------------------------------------------------------------</fo:block>
					        							</fo:table-cell>					        							
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="left"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
					        							</fo:table-cell>
					        							</fo:table-row>
        												</#if>
				        						<#elseif invoiceLineNum == 16>
				        						<#assign rowNum = 1>
				        						<#assign invoiceLineNum =0>
					        							<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
					        								<fo:table-row font-weight="bold">
						        							<fo:table-cell border-style="solid">
						        								<fo:block keep-together="always" font-size="7pt" text-align="center"></fo:block>
						        							</fo:table-cell>					        							
						        							<fo:table-cell border-style="solid">
						        								<fo:block keep-together="always" font-size="7pt" text-align="left">&#160;AMOUNT INCLUSIVE OF EXCISE DUTY</fo:block>
						        							</fo:table-cell>
						        							<fo:table-cell border-style="solid">
						        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
						        							</fo:table-cell>
						        							<fo:table-cell border-style="solid">
						        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
						        							</fo:table-cell>
						        							<fo:table-cell border-style="solid">
						        								<fo:block keep-together="always" font-size="7pt" text-align="right">C/O</fo:block>
						        							</fo:table-cell>
						        							<fo:table-cell border-style="solid">
						        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
						        							</fo:table-cell>
						        							<fo:table-cell border-style="solid">
						        								<fo:block keep-together="always" font-size="7pt" text-align="right">${totBasicValue?if_exists?string("#0.00")}</fo:block>
						        							</fo:table-cell>
						        							<fo:table-cell border-style="solid">
						        								<fo:block keep-together="always" font-size="7pt" text-align="right">${totVatValue?if_exists?string("#0.00")}</fo:block>
						        							</fo:table-cell>
						        							<fo:table-cell border-style="solid">
						        								<fo:block keep-together="always" font-size="7pt" text-align="right">${totalValue?if_exists?string("#0.00")}</fo:block>
						        							</fo:table-cell>
						        						</fo:table-row>
						        						<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											
						        						<#if prodLineNum == 16 >
						        						<#assign prodLineNum = 0 >
				        								<fo:table-row >
				        									<fo:table-cell>
				        										<fo:block font-size="7pt" page-break-after="always"></fo:block>
				        									</fo:table-cell>
				        								</fo:table-row>
        												</#if>
        												<#if invoiceLineNum == 0 && prodLineNum == 8 >
        												<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row >
					        								<fo:table-cell>
					        									<fo:block font-size="7pt" keep-together="always"  white-space-collapse="false">.                                                                             ${boothEntries.get("invoiceNumber")?if_exists}</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							<fo:table-row >
					        								<fo:table-cell>
					        									<fo:block font-size="7pt" keep-together="always"  white-space-collapse="false">.                                                                             ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(estimatedDeliveryDate, "dd/MM/yyyy")?if_exists}</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							<fo:table-row >
					        								<fo:table-cell>
					        									<fo:block font-size="7pt">.</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							<fo:table-row >
					        								<fo:table-cell>
					        									<fo:block font-size="7pt" keep-together="always"  white-space-collapse="false">.              ${boothEntries.get("booth")?if_exists}</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							<fo:table-row >
					        								<fo:table-cell>
					        									<fo:block font-size="7pt" keep-together="always"  white-space-collapse="false">.              ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, boothEntries.get("ownerPartyId"), false)}</fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell>
					        									<fo:block font-size="7pt"></fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell>
					        									<fo:block font-size="7pt"></fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell>
					        									<fo:block font-size="7pt"></fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell>
					        									<fo:block font-size="7pt"></fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell>
					        									<fo:block font-size="7pt"></fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell>
					        									<fo:block font-size="7pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${boothValues.get("route")} </fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							<fo:table-row >
					        								<fo:table-cell>
					        									<fo:block font-size="7pt" keep-together="always"  white-space-collapse="false">.              ${boothEntries.get("area")?if_exists}</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							<fo:table-row >
					        								<fo:table-cell>
					        									<fo:block font-size="7pt">.</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							<fo:table-row >
					        								<fo:table-cell>
					        									<fo:block font-size="7pt">.</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
						        						<fo:table-row font-weight="bold">
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="left">----------------------------------------------------------------------------------------------------------</fo:block>
					        							</fo:table-cell>					        							
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="left"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
					        							</fo:table-cell>
					        							</fo:table-row>
        												</#if>
					        							</#if>
					        						</#list>	
					        						<#assign positionCount = 8-rowNum >
					        						<#assign posCount = positionCount-1>
					        						<#list -1..posCount as pc>
					        						<#assign prodLineNum = prodLineNum+1>
					        						<fo:table-row>
        												<fo:table-cell>
        													<fo:block keep-together="always" font-size="7pt" text-align="right">.</fo:block>
        												</fo:table-cell>
        											</fo:table-row> 
        											</#list> 
					        						<#--<#if invoiceLineNum == 0 || invoiceLineNum == 8>
					        						<#else>-->
					        						<fo:table-row>
	        											<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        											</fo:table-cell>
	        										</fo:table-row>
					        						<fo:table-row >
				        								<fo:table-cell>
				        									<fo:block font-size="7pt">.</fo:block>
				        								</fo:table-cell>
				        							</fo:table-row>
					        						<fo:table-row font-weight="bold">
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="center"></fo:block>
					        							</fo:table-cell>					        							
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="left">&#160;AMOUNT INCLUSIVE OF EXCISE DUTY</fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right">${totBasicValue?if_exists?string("#0.00")}</fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right">${totVatValue?if_exists?string("#0.00")}</fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always" font-size="7pt" text-align="right">${totalValue?if_exists?string("#0.00")}</fo:block>
					        							</fo:table-cell>
					        						</fo:table-row>
					        						<fo:table-row>
	        											<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        											</fo:table-cell>
	        										</fo:table-row>
        											<fo:table-row>
        												<fo:table-cell>
        													<fo:block font-size="7pt" keep-together="always"  white-space-collapse="false">${Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble(totalValue?string("#0.00")), "%rupees-and-paise", locale).toUpperCase()} ONLY</fo:block>
        												</fo:table-cell>
        											</fo:table-row>
        											<#if prodLineNum == 16 >
						        						<#assign prodLineNum = 0 >
				        								<fo:table-row >
				        									<fo:table-cell>
				        										<fo:block font-size="7pt" page-break-after="always"></fo:block>
				        									</fo:table-cell>
				        								</fo:table-row>
				        							<#else>
				        							<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
	        											<fo:table-row>
	        												<fo:table-cell>
	        												<fo:block font-size="7pt">.</fo:block>
	        												</fo:table-cell>
	        											</fo:table-row>
        											</#if>
					        						</fo:table-body>
					        					</fo:table>								
        									</fo:block>
        								</fo:table-cell>
        							</fo:table-row>        							
        						</fo:table-body>
		        			</fo:table>
		        		</fo:block>		                      
		          
		      	</#list>
		    </#list>
		    

       </#list>
      	</fo:flow>
		        </fo:page-sequence>
     </fo:root>
</#escape>