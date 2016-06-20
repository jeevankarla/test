
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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-right=".3in" margin-top=".1in">
                <fo:region-body margin-top=".1"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1.5in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "IndentReport.pdf")}
        <#if OrderItemList?has_content>
        <fo:page-sequence master-reference="main" font-size="10pt">	
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
        	    <fo:block text-align="left" font-size="10pt" keep-together="always"  white-space-collapse="false">
        			<fo:table>
			            <fo:table-column column-width="150pt"/>
			            <fo:table-column column-width="150pt"/>
			            <fo:table-column column-width="150pt"/>
			            <fo:table-column column-width="150pt"/> 
			            <fo:table-body>
			                <fo:table-row>
			                    <fo:table-cell number-columns-spanned="4">
					            	<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
                                    <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
					            	<fo:block  keep-together="always" text-align="center"  font-weight="bold"   font-size="12pt" white-space-collapse="false">${reportHeader.description?if_exists} </fo:block>
					            	<fo:block  keep-together="always" text-align="center"  font-weight="bold"   font-size="10pt" white-space-collapse="false"> ${BOAddress?if_exists}</fo:block>
					            <#--	<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false"> S-13/36, SRI RAM MARKET, TELIA BAGH </fo:block>
					            	<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false"> VARANSI-221002 </fo:block>-->
					            	<fo:block  keep-together="always" text-align="center"  font-weight="bold"  font-size="10pt" white-space-collapse="false"> ${BOEmail?if_exists} </fo:block>
					            </fo:table-cell>
							</fo:table-row>
			            </fo:table-body>
			        </fo:table>
        		</fo:block> 
        		<fo:block  text-align="left" font-size="10pt" white-space-collapse="false">Proposal No : ${orderId}                                                    Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(orderDate, "dd-MMM-yyyy")?if_exists}</fo:block>
        		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
             	<fo:block  text-align="center" font-size="10pt" font-weight="bold"  white-space-collapse="false">Minutes of Purchase and Sales Committee meeting held on :<#if heldOnDate?has_content> ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(heldOnDate, "dd-MMM-yyyy")?if_exists}</#if></fo:block>
             	<fo:block  text-align="left" font-size="10pt" white-space-collapse="false">The committee recommended/approved purchse of following items(s) as per the rates mention</fo:block>
             	<fo:block  text-align="left" font-size="10pt" font-style="bold">against each to be procured from M/S : <fo:inline font-weight="bold"><#if supplierPartyId?has_content>${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, supplierPartyId, false)} [${SupplierCity?if_exists}]<#else>&#160;</#if></fo:inline></fo:block>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">towards the requirement of user agency M/s : <fo:inline font-weight="bold">${partyName} [${weaverCity?if_exists}]</fo:inline></fo:block>  
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">vide their indent No: ${orderId} date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(orderDate, "dd-MMM-yyyy")?if_exists} ref.no.${externalOrderId?if_exists} <#-->Meeting held on ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(orderDate, "dd-MMM-yyyy")?if_exists} --></fo:block>  
        		<fo:block  text-align="left" font-size="12pt" font-weight="bold">PRICE FIXATION CHART :</fo:block>
        		<fo:block>
             		<fo:table border-style="solid">
             		    <fo:table-column column-width="4%"/>
			            <fo:table-column column-width="25%"/>
			            <fo:table-column column-width="10%"/>
			            <fo:table-column column-width="10%"/>
			            <fo:table-column column-width="17%"/>
	                    <fo:table-column column-width="10%"/>
			            <fo:table-column column-width="10%"/>
			            <fo:table-column column-width="15%"/>
			            <#--<fo:table-column column-width="10%"/>-->
			            
			            <fo:table-body>
			                <fo:table-row>
			                    <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false">SNo</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false">Items</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false">Remarks</fo:block>
					            	<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false">Items</fo:block>
					            </fo:table-cell >
					            <#--<fo:table-cell border-style="solid">

					            	<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">Remarks</fo:block>
					            </fo:table-cell > -->
					            <#--<fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">Unit</fo:block>
					            </fo:table-cell>-->
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Quantity</fo:block>
					            	<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">(KGS)</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Purchase Rate/Unit</fo:block>
					            	<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">(Rs)</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Int for 0 Days @ 0.00% per Annum</fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="11pt" white-space-collapse="false">Handling Charges @ 0.00%</fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block   text-align="left" font-size="11pt" white-space-collapse="false">Sale Price/Unit</fo:block>
					            	<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">(Rs)</fo:block>
					            </fo:table-cell>
							</fo:table-row>
			                     <#assign sr=1>
			                  <#list OrderItemList as orderList>
			                <fo:table-row>
			                    <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${sr} </fo:block>
					            </fo:table-cell>
					             <#assign productDetails = delegator.findOne("Product", {"productId" :orderList.productId}, true)>  
					            <fo:table-cell border-style="solid">
					            	<fo:block text-align="left" font-size="11pt" white-space-collapse="false">${productDetails.get("productName")?if_exists} </fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="11pt" white-space-collapse="false">${orderList.get("remarks")?if_exists}</fo:block>
					            </fo:table-cell>  
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false">${orderList.get("quantity")?if_exists?string("#0.00")} </fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false"><#if orderList.get("unitPrice")?has_content>${orderList.get("unitPrice")?if_exists?string("#0.00")}${orderList.get("Uom")?if_exists}<#else>${"0.00"}</#if></fo:block>
					            	<fo:block  keep-together="always" text-align="right" font-size="9pt" white-space-collapse="false"><#if orderList.get("Uom") == "/Bale">${orderList.get("baleQuantity")*40}(Bundles)<#elseif orderList.get("Uom") == "/Half-Bale">${orderList.get("baleQuantity")*20}(Bundles)<#elseif orderList.get("Uom") == "/Bundle">${orderList.get("baleQuantity")}(Bundles)</#if></fo:block> 
						         </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false"> </fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false"></fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false"><#if orderList.get("unitPrice")?has_content>${orderList.get("unitPrice")?if_exists?string("#0.00")}/kgs<#else>${"0.00"}</#if></fo:block>
   					            	<fo:block  keep-together="always" text-align="right" font-size="9pt" white-space-collapse="false"><#if orderList.get("Uom") == "/Bale">${orderList.get("baleQuantity")*40}(Bundles)<#elseif orderList.get("Uom") == "/Half-Bale">${orderList.get("baleQuantity")*20}(Bundles)<#elseif orderList.get("Uom") == "/Bundle">${orderList.get("baleQuantity")}(Bundles)</#if></fo:block>
   					            	<#if orderList.get("bundleUnitPrice") != 0>
   					            	<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${orderList.get("bundleUnitPrice")?if_exists}(Bundle Price)</fo:block>
   					            	</#if>
					            </fo:table-cell>
							</fo:table-row>
							<#assign sr=sr+1>
							</#list>
						</fo:table-body>
					</fo:table>
				</fo:block>
				<fo:block>&#160;&#160;&#160;&#160;&#160;</fo:block>
				<fo:block>&#160;&#160;&#160;&#160;&#160;</fo:block>
				<#assign purchaeTot =0>
                <#assign salesTot =0>  
	             <fo:block>
	             <fo:block  text-align="left" font-size="12pt" font-weight="bold">DETAILS OF PURCHASE &amp; SALES :</fo:block>
             		<fo:table border-style="solid">
             		    <fo:table-column column-width="4%"/>
			            <fo:table-column column-width="15%"/>
			            <fo:table-column column-width="10%"/>
			             <#--<fo:table-column column-width="10%"/>-->
			            <fo:table-column column-width="10%"/>
			            <fo:table-column column-width="10%"/>
	                    <fo:table-column column-width="11%"/>
			            <fo:table-column column-width="15%"/>
			            <fo:table-column column-width="11%"/>
			            <fo:table-column column-width="15%"/>
			            <fo:table-body>
			                <fo:table-row>
			                    <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false">SNo</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false">Items</fo:block>
					            </fo:table-cell >
					            <#--<fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">Unit</fo:block>
					            </fo:table-cell>-->
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Quantity</fo:block>
					            	<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">(KGS)</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false">10 %</fo:block>
					            	<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false">Qty(Kgs)</fo:block>
					            </fo:table-cell >
					             <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false">MGPS</fo:block>
					            	<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false">Qty(Kgs)</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Purchase Rate/Unit</fo:block>
					            	<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">(Rs.)</fo:block>
					            	
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Purchase Value</fo:block>
					            	<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">(Rs.)</fo:block>
					            	
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="11pt" white-space-collapse="false">Selling Rate/Unit</fo:block>
					            	<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">(Rs.)</fo:block>
					            	
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block   text-align="left" font-size="11pt" white-space-collapse="false">Sale Value</fo:block>
					            	<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">(Rs.)</fo:block>
					            	
					            </fo:table-cell>
							</fo:table-row>
			                     <#assign sr=1>
			                     <#assign totquantityKgs = 0>
			                      <#assign toTunitPrice = 0>
			                       <#assign totSalesValue = 0>
			                       <#assign tenPreTOT = 0>
			                       <#assign mgpsQtyTOT = 0>
			                  <#list OrderItemList as orderList>
			                <fo:table-row>
			                    <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false">${sr} </fo:block>
					            </fo:table-cell>
					             <#assign productDetails = delegator.findOne("Product", {"productId" :orderList.productId}, true)>  
					            <fo:table-cell border-style="solid">
					            	<fo:block  text-align="left" font-size="11pt" white-space-collapse="false">${orderList.get("productName")?if_exists} </fo:block>
					            </fo:table-cell >
					           <#--> <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="11pt" white-space-collapse="false">${orderList.get("remarks")?if_exists}</fo:block>
					            </fo:table-cell> -->
					            <fo:table-cell border-style="solid">
					            <#assign totquantityKgs=totquantityKgs+orderList.get("quantity")>
					            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false">${orderList.get("quantity")?if_exists?string("#0.00")}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            <#assign tenPreTOT=tenPreTOT+orderList.get("tenPerQty")>
					            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false">${orderList.get("tenPerQty")?if_exists?string("#0.00")} </fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					               <#assign mgpsQtyTOT=mgpsQtyTOT+orderList.get("mgpsQty")>
					            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false">${orderList.get("mgpsQty")?if_exists?string("#0.00")} </fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" font-size="11pt" white-space-collapse="false"><#if orderList.get("unitPrice")?has_content>${orderList.get("unitPrice")?if_exists?string("#0.00")}${orderList.get("Uom")?if_exists}<#else>${"0.00"}</#if></fo:block>
					            	<fo:block  keep-together="always" text-align="right" font-size="9pt" white-space-collapse="false"><#if orderList.get("Uom") == "/Bale">${orderList.get("baleQuantity")*40}(Bundles)<#elseif orderList.get("Uom") == "/Half-Bale">${orderList.get("baleQuantity")*20}(Bundles)<#elseif orderList.get("Uom") == "/Bundle">${orderList.get("baleQuantity")}(Bundles)</#if></fo:block> 
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
                                      <#assign purchaeTot =purchaeTot+orderList.get("totalCost")>
					                 <#assign toTunitPrice = toTunitPrice+orderList.get("totalCost")>
					            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false">${orderList.get("totalCost")?if_exists?string("#0.00")}</fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	 <fo:block   text-align="right" font-size="11pt" white-space-collapse="false"><#if orderList.get("unitPrice")?has_content>${orderList.get("unitPrice")?if_exists?string("#0.00")}${orderList.get("Uom")?if_exists}<#else>${"0.00"}</#if></fo:block>
					            	 <fo:block  keep-together="always" text-align="right" font-size="9pt" white-space-collapse="false"><#if orderList.get("Uom") == "/Bale">${orderList.get("baleQuantity")*40}(Bundles)<#elseif orderList.get("Uom") == "/Half-Bale">${orderList.get("baleQuantity")*20}(Bundles)<#elseif orderList.get("Uom") == "/Bundle">${orderList.get("baleQuantity")}(Bundles)</#if></fo:block> 
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false">${orderList.get("totalCost")?if_exists?string("#0.00")}</fo:block>
					            </fo:table-cell>
							</fo:table-row>
							<#assign sr=sr+1>
							</#list>
							 <fo:table-row>
			                    <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false"> </fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false">Total</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false">${totquantityKgs?string("#0.000")} </fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false">${tenPreTOT?string("#0.000")} </fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false">${mgpsQtyTOT?string("#0.000")} </fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false"></fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false">${toTunitPrice?string("#0.00")}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false"></fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false">${toTunitPrice?string("#0.00")} </fo:block>
					            </fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				</fo:block>
				<fo:block text-align="left" font-size="10pt"   white-space-collapse="false"><#if scheme == "MGPS_10Pecent">MGP 10% Scheme<#elseif scheme == "MGPS">MGPS<#elseif scheme == "General">General</#if> Scheme</fo:block>
				
				
		        <#assign grandToT = 0>
		        <#assign typeBase=typeBasedMap.entrySet()>
			      <#list typeBase as typeBaseList>
			       <#assign typeOFListValues=typeBaseList.getValue().entrySet()>
			        <#list typeOFListValues as eaValue>
			         <#assign grandToT = grandToT+eaValue.getValue()>
		           <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold"> &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;                                     ${typeBaseList.getKey()?if_exists} as ${eaValue.getKey()} % : &#160;${eaValue.getValue()?if_exists}  </fo:block>
		            </#list>
		        </#list>
				
		        <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold"> &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;    TOTAL VALUE (RS) : &#160;${(toTunitPrice+grandToT)?if_exists?string("##0.00")}</fo:block> 
				
				<fo:block>&#160;&#160;&#160;&#160;&#160;</fo:block>
				<fo:block font-weight="bold" font-size="10pt">Summary</fo:block>
				<fo:block>a) Actual Purchase Value (Rs): ${purchaeTot?string("#0.00")} </fo:block>
				<fo:block>b) Total Sale Value     (Rs): ${purchaeTot?string("#0.00")} </fo:block>
				<fo:block>c) Difference of the Sale</fo:block>
				<fo:block> &#160;&#160; Value &amp; actual payment made to Mill: Nil</fo:block>
				<fo:block>d) 0 days interest on the credit:</fo:block>
				<fo:block>e) Percentage of Trading Contribution: 0%</fo:block>
			    <fo:block>	
			        <fo:table border-style="solid">
             		    <fo:table-column column-width="100%"/>
			           <#-- <fo:table-column column-width="50%"/>	-->
			            <fo:table-body> 
			                <fo:table-row>
			                    <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false" font-weight="bold">Sales Terms</fo:block>
					            </fo:table-cell>
					            <#--><fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold"                                                                                  >Purchase Terms</fo:block>
					            </fo:table-cell>-->
							</fo:table-row>			
				            <fo:table-row>
					             <fo:table-cell border-style="solid">
									<fo:block>1. Goods will be despatched on Freight To-Pay basis to M/S: <fo:inline font-weight="bold">${partyName}</fo:inline> </fo:block>
				                    <fo:block white-space-collapse="false">2. Payment will be made by user agency within BACK TO BACK/ ON CREDIT days / immediately failing which interest @    per annum will be charged for the total number of days payment delayed.</fo:block>
	                                <fo:block>3. One total financial outflow in this transaction is Rs.</fo:block>
	                                <fo:block>4. Total outstanding of M/S ${partyName} is Rs <fo:inline font-weight="bold">${toTunitPrice?string("#0.00")} </fo:inline></fo:block>
	                                <fo:block>5. Payment dues with interest from the party M/S: <fo:inline font-weight="bold">${partyName}</fo:inline>  as on  <fo:inline font-weight="bold">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(orderDate, "dd-MMM-yyyy")?if_exists}</fo:inline> is Rs.${balanceAmt?if_exists} </fo:block>
	                                <fo:block>6. Payment to the Mill to be paid Cheque/Demand Draft for Rs.<fo:inline font-weight="bold">${toTunitPrice?string("#0.00")} </fo:inline> after receipt of Mill invoice/LR</fo:block>
	                                <fo:block>7. No. of Days credit extended by Mills to NHDC from date of despatch ........</fo:block>
	                                <fo:block>8. No. of Days credit extended by NHDC to Agency from date of despatch </fo:block>
	                                <fo:block>9. Any other specific information ...................</fo:block>
	                               <fo:block>10. Local Taxes as applicable.</fo:block>
					           </fo:table-cell>
                               <#--<fo:table-cell border-style="solid">
									<fo:block>1. Yarn will be received with self certification subject to testing of the yarn at Textile committee /our QC department.If the test results are differed to our specifications, the material will be rejected at the cost of the supplier.</fo:block>
									<fo:block>2. Delivery Schedule: <fo:inline font-weight="bold">Immediately</fo:inline>.</fo:block>
									<fo:block>3. Mode of Despatch: Material should be depatched through reliable transporters only.</fo:block>
									<fo:block>4. The above material should be despatched as per our Quality specifications from <fo:inline font-weight="bold">${partyName}</fo:inline>, under Mill Gate Price Scheme to <fo:inline font-weight="bold">PCQC Administator, EOCP, Gadag-Betageri</fo:inline> OR As per despatch instructions.</fo:block>
									<fo:block>5. Freight Charges: Material should be despatched on <fo:inline font-weight="bold">FREIGHT TO PAY &amp; DOOR DELIVERY</fo:inline>.</fo:block>
	                                <fo:block>6. Mode of payment: <fo:inline font-weight="bold">Advance payment</fo:inline>.</fo:block>
	                                <fo:block>7. Insurance: Please cover transit risk.</fo:block>
	                                <fo:block>8. Price: NET RATE, Freight charges extra.</fo:block>
	                                <fo:block>9. Invoices in triplicate and L R should be sent to our Unit address mentioned above along with goods and one copy to M (PUR), KHDC Ltd, Hubli- 31.</fo:block>
	                                <fo:block>10. Supply should be completed as mentioned above from the date of receipt of purchase order.</fo:block>
	                                <fo:block>11. Puchase Order number and date must appear on all your invoices / correspondences. </fo:block>
	                                <fo:block font-weight="bold">12. For having accepted the purchase order and terms &amp; conditions contained therein you have to return the second copy of the this purchse Order duly your seal &amp; signature immediately after receipt of the same.</fo:block>
					          </fo:table-cell>-->     
	                     </fo:table-row>
					</fo:table-body>
			   </fo:table>
		  </fo:block>
	<fo:block>.</fo:block>
    <#assign size = paymentRefNumList.size()>
	<fo:block>Advance Details: Cheque/DD No : <#assign count = 0><#list paymentRefNumList as paymentRefNum><#assign count = count+1> ${paymentRefNum?if_exists} <#if count ==size><#else>,</#if> </#list> Cr on Account amounting (Rs) ${totAmt?string("#0.00")}  received from user agency M/S:<fo:inline font-weight="bold">${partyName}</fo:inline></fo:block>
	<fo:block>&#160;&#160;&#160;&#160;&#160;</fo:block>
    <fo:block>&#160;&#160;&#160;&#160;&#160;</fo:block> 
    <fo:block white-space-collapse="false" keep-together="always" text-align="left"><fo:inline text-decoration="underline">Sr.Officer(C)/AM(C)/DM(C)      Sr.Officer(F&amp;A)/A.M(F&amp;A)/Dy.M(F&amp;A)/Manager(F&amp;A)   Mgr(C)/Sr.Mgr(C)/Ch.Mgr(C)/D.G.M(C)</fo:inline></fo:block>  			
    <fo:block text-align="center">Head Office : 10th &amp; 11th Floor, Vikas Deep , 22 Station Road , Lucknow-226001</fo:block>				
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