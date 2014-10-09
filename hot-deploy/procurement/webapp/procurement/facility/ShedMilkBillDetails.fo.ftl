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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".5in" margin-top=".5in">
                <fo:region-body margin-top="1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
       </fo:layout-master-set>
       ${setRequestAttribute("OUTPUT_FILENAME", "ShedMilkBillDetails.txt")}
       <#if errorMessage?has_content>
		<fo:page-sequence master-reference="main">
		   <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
		      <fo:block font-size="14pt">
		              ${errorMessage}.
		   	  </fo:block>
		   </fo:flow>
		</fo:page-sequence>        
		<#else>
        <#if facility?has_content>
       <fo:page-sequence master-reference="main">
        <fo:static-content flow-name="xsl-region-before">
            <fo:block text-align="left" white-space-collapse="false" font-size="10pt" keep-together="always">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;MILK BILL STATEMENT ABSTRACT </fo:block>
            <fo:block font-size="8pt">-------------------------------------------------------------------------------------------------------------</fo:block>
            <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="9pt">&#160;       SHED NAME          :  ${facility.facilityName}                                                                  </fo:block>
            <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="9pt">&#160;       PERIOD FROM      :  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} To ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}                                                                                     </fo:block>             
            <fo:block font-size="8pt">-------------------------------------------------------------------------------------------------------------</fo:block>
            <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt"> 
            	<fo:table>
                    <fo:table-column column-width="55pt" />
                           <#list productRatesList as productRate>
                                <fo:table-column column-width="105pt" />
                           </#list>         
                    <fo:table-body>
                        <fo:table-row>
                            <fo:table-cell>
                                <fo:block white-space-collapse="false" text-align="center">APDDCF  </fo:block>
                            </fo:table-cell>
                            <#list productRatesList as productRate>
                    	        <fo:table-cell>
                                    <fo:block text-align="center">${productRate.productName}(${productRate.using})&#160;:&#160;${productRate.defaultRate?if_exists?string("##0.00")}&#160;&#160;&#160;&#160;   </fo:block>
                                </fo:table-cell>
                            </#list>
                        </fo:table-row>
                    </fo:table-body>    
                </fo:table> 
            </fo:block>
            <fo:block font-size="8pt">-------------------------------------------------------------------------------------------------------------</fo:block>
        </fo:static-content>
       <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
       <fo:block font-size="9pt">
                <fo:table width="270pt">
                    <fo:table-column/>
                    <fo:table-column/> 
                    <fo:table-body>
                        <fo:table-row>
                            <fo:table-cell>
                                <fo:block keep-together="always" text-align="left">
                                        <#assign products = productsBrandMap.entrySet()>
                                       <fo:table width = "140pt">
                                            <fo:table-column size = "90pt"/>
                                            <fo:table-column size = "20pt"/>
                                            <fo:table-column size = "30pt"/>
                                            <fo:table-body>
                                                 <#assign grossAmt = 0>
                                                 <#assign products = productsBrandMap.entrySet()>
                                                  <#list products as product>
                                                  	<#assign productKey = product.getKey()>
                                                    <fo:table-row>
                                                          <fo:table-cell> 
                                                             <fo:block text-align="left" keep-together="always" font-size="4pt"> ${productKey} TOTAL MILK AMOUNT</fo:block>
                                                          </fo:table-cell>
                                                          <fo:table-cell> 
                                                             <fo:block text-align="right" font-size="4pt"> :</fo:block>
                                                          </fo:table-cell>
                                                          <fo:table-cell> 
                                                             <fo:block text-align="right" font-size="4pt"> ${(totAmountsMap.get(productKey))?if_exists?string("##0.00")}</fo:block>
                                                             <#assign grossAmt = grossAmt+((totAmountsMap.get(productKey)))>
                                                          </fo:table-cell>
                                                    </fo:table-row>
                                                  </#list>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="left" keep-together="always" font-size="4pt"> TOTAL OP-COST </fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right" font-size="4pt"> :</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <#--here we are displaying commission amount as op-cost -->   
                                                     <#assign opCostAmt = ((totAmountsMap.get("opCost"))+(totAmountsMap.get("commAmt")))>
                                                     <#assign grossAmt = grossAmt+opCostAmt>
                                                     
                                                     <fo:block text-align="right" font-size="4pt">${(opCostAmt)?if_exists?string("##0.00")} </fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="left" keep-together="always" font-size="4pt"> TOTAL CARTAGE </fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right" font-size="4pt"> :</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                        <#assign grossAmt = grossAmt+((totAmountsMap.get("cartage")))>
                                                     <fo:block text-align="right" font-size="4pt"> ${((totAmountsMap.get("cartage")))?if_exists?string("##0.00")}</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="left" keep-together="always" font-size="4pt"> TOTAL ADDITIONS</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right" font-size="4pt"> :</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                        <#assign grossAmt = grossAmt+((totAmountsMap.get("addnAmt")+shedMaintAmount))>
                                                     <fo:block text-align="right" font-size="4pt"> ${((totAmountsMap.get("addnAmt")+shedMaintAmount))?if_exists?string("##0.00")}</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="left" font-size="4pt"> TOTAL TIP AMOUNT</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right" font-size="4pt"> :</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                        <#assign grossAmt = grossAmt+((totAmountsMap.get("tipAmt")))>
                                                     <fo:block text-align="right" font-size="4pt"> ${((totAmountsMap.get("tipAmt")))?if_exists?string("##0.00")}</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="left" font-size="4pt"> TOTAL DIF AMOUNT</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right" font-size="4pt"> :</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                        <#assign grossAmt = grossAmt+difAmt>
                                                     <fo:block text-align="right" font-size="4pt"> ${(difAmt)?if_exists?string("##0.00")}</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                  </fo:table-cell>
                                                  <fo:table-cell><fo:block></fo:block> 
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right" font-size="4pt"> -------------</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="left" font-size="4pt"> GROSS AMOUNT</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right" font-size="4pt"> :</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right" font-size="4pt"> ${grossAmt?if_exists?string("##0.00")}</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                  <fo:table-cell><fo:block></fo:block> 
                                                  </fo:table-cell>
                                                  <fo:table-cell><fo:block></fo:block> 
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right" font-size="4pt"> -------------</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                    <fo:table-cell><fo:block></fo:block></fo:table-cell><fo:table-cell><fo:block></fo:block></fo:table-cell><fo:table-cell><fo:block></fo:block></fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="left" keep-together="always" font-size="4pt" white-space-collapse="false"> FEED RECOVERY AMOUNT</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right" font-size="4pt"> :</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right" font-size="4pt">${(feedAmt*(-1))?if_exists?string("##0.00")}</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="left" keep-together="always" font-size="4pt"> CESS ON LOCAL SALE </fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right" font-size="4pt"> :</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right" font-size="4pt"> ${(cessOnSaleAmt*(-1))?if_exists?string("##0.00")}</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="left" keep-together="always" font-size="4pt"> LESS KGFAT AMOUNT </fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right" font-size="4pt"> :</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right" font-size="4pt"> ${(totshrtKgFatAmt)?if_exists?string("##0.00")}</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="left" keep-together="always" font-size="4pt"> LESS KGSNF AMOUNT </fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right" font-size="4pt"> :</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right" font-size="4pt"> ${(totshrtKgSnfAmt)?if_exists?string("##0.00")}</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <#assign sourAmt = 0>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="left" keep-together="always" font-size="4pt"> SOUR AMOUNT </fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right" font-size="4pt"> :</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
						                            <#if GrandTotalsMap?has_content>
						                            	<#if (GrandTotalsMap.get("sourAmt"))?has_content>
						                            		<#assign sourAmt = sourAmt-GrandTotalsMap.get("sourAmt")>
						                            	</#if>
						                            </#if>
                                                     <fo:block text-align="right" font-size="4pt"> ${sourAmt?if_exists?string("##0.00")}</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <#if userCharges!=0 >
                                                 <fo:table-row>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="left" keep-together="always" font-size="4pt"> USER CHARGES([${shedTotLtrs} *0.025]+ST)</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right" font-size="4pt"> :</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right" font-size="4pt"> -${(userCharges)?if_exists?string("##0.00")}</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                </#if>
                                                <fo:table-row>
                                                  <fo:table-cell> <fo:block></fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> <fo:block></fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                    <fo:block text-align="right" font-size="4pt"> -------------</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <#assign totDedAmt = (sourAmt*(-1)+(totshrtKgFatAmt*(-1))+(totshrtKgSnfAmt*(-1))+(feedAmt)+(cessOnSaleAmt)+(userCharges))>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="left" keep-together="always" font-size="4pt"> TOTAL DEDUCTION</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right" font-size="4pt"> :</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right" font-size="4pt"> ${(totDedAmt*(-1))?if_exists?string("##0.00")}</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                  <fo:table-cell><fo:block></fo:block> 
                                                  </fo:table-cell>
                                                  <fo:table-cell><fo:block></fo:block> 
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right" font-size="4pt"> -------------</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="left" keep-together="always" font-size="4pt">NET AMOUNT PAYABLE</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right" font-size="4pt"> :</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right" font-size="4pt">${(grossAmt-totDedAmt)?if_exists?string("##0.00")}</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                  <fo:table-cell> <fo:block></fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell><fo:block></fo:block> 
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right" font-size="4pt"> -------------</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                            </fo:table-body>
                                       </fo:table> 
                                </fo:block>    
                            </fo:table-cell>
                            <fo:table-cell>
                                <fo:block font-size="6pt" text-align = "left">
                                        <fo:table width = "120pt">
                                            <fo:table-column size = "30pt"/>
                                            <fo:table-column size = "30pt"/>
                                            <fo:table-column size = "30pt"/>
                                            <fo:table-column size = "30pt"/>
                                            <fo:table-header>
                                                <fo:table-row font-weight="bold">
                                                    <fo:table-cell>
                                                        <fo:block text-align = "right" keep-together="always" font-size="4pt">MILK</fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell>
                                                        <fo:block text-align = "right" keep-together="always" font-size="4pt">QTY-LTS</fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell>
                                                        <fo:block text-align = "right" keep-together="always" font-size="4pt">AVG-LTS</fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell>
                                                        <fo:block text-align = "right" keep-together="always" font-size="4pt">AVG-RATE</fo:block>
                                                    </fo:table-cell>
                                                </fo:table-row>
                                            </fo:table-header>  
                                            <fo:table-body >
                                                  <#assign products = productsBrandMap.entrySet()>
                                                  <#assign totLtrs = 0>
                                                  <#assign totAvgLtrs = 0>
                                                  <#assign totPrice = 0>
                                                  <#list products as product>
                                                    <fo:table-row>
                                                          <fo:table-cell> 
                                                             <fo:block text-align="right" font-size="4pt"> ${product.getKey()}</fo:block>
                                                          </fo:table-cell>
                                                          <fo:table-cell> 
                                                             <#assign productName = product.getValue()>
                                                             <#assign qtyLtrs = shedTotLtrsMap.get(productName) >
                                                             <#assign qtyLtrs = Static["java.lang.Math"].round(qtyLtrs)>
                                                             <#assign totLtrs = totLtrs+qtyLtrs>
                                                             <fo:block text-align="right" font-size="4pt"> ${qtyLtrs?if_exists?string("##0")}</fo:block>
                                                          </fo:table-cell>
                                                          <fo:table-cell> 
                                                              <#assign avgQty = (qtyLtrs/noOfDays)> 
                                                              <#assign tempTip = totAmountsMap.get(product.getKey()+"TipAmt")>
                                                              <#assign price = ((((totAmountsMap.get(product.getKey())))).setScale(0,1))>
                                                              <#assign price = price+tempTip>
                                                              <#assign totPrice = totPrice+price>
                                                              <#if qtyLtrs!=0>
                                                              	<#assign avgRate = price/qtyLtrs>
                                                              	<#else>
                                                              	<#assign avgRate = 0>
                                                              </#if>
                                                             <fo:block text-align="right" font-size="4pt"> ${avgQty?if_exists?string("#0")}</fo:block>
                                                          </fo:table-cell>
                                                          <fo:table-cell> 
                                                             <fo:block text-align="right" font-size="4pt">${avgRate?if_exists?string("##0.00")}</fo:block>
                                                          </fo:table-cell>
                                                    </fo:table-row>
                                                  </#list>
                                                   <fo:table-row>
                                                          <fo:table-cell> 
                                                             <fo:block text-align="right" font-size="4pt"> MM</fo:block>
                                                          </fo:table-cell>
                                                          <fo:table-cell> 
                                                             <fo:block text-align="right" font-size="4pt"> ${totLtrs?if_exists?string("##0")}</fo:block>
                                                          </fo:table-cell>
                                                          <fo:table-cell> 
                                                          		<#assign totAvgLtrs = totLtrs/noOfDays>
                                                             <fo:block text-align="right" font-size="4pt"> ${totAvgLtrs?if_exists?string("#0")}</fo:block>
                                                          </fo:table-cell>
                                                            <fo:table-cell>
                                                             <#assign avgRate = totPrice/totLtrs>
                                                             <fo:block text-align="right" font-size="4pt"> ${avgRate?if_exists?string("##0.00")}</fo:block>
                                                          </fo:table-cell>
                                                    </fo:table-row> 
                                            </fo:table-body>
                                        </fo:table>
                                </fo:block>    
                            </fo:table-cell>                        
                        </fo:table-row>
                    </fo:table-body>
                </fo:table>
          </fo:block>     
           <fo:block font-size="8pt">-------------------------------------------------------------------------------------------------------------</fo:block>
           <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="9pt">&#160;       PERIOD FROM :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} To ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}                                                                                     </fo:block>
           <fo:block font-size="8pt">-------------------------------------------------------------------------------------------------------------</fo:block>
           <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="9pt">&#160;       QUALITY VARIATION BETWEEN UNIT-WISE BILLING AND MILK RECEIPTS AT MPF HYD </fo:block> 
           <fo:block font-size="8pt">-------------------------------------------------------------------------------------------------------------</fo:block>
           <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="4pt">UNIT                                  ASPER MILK BILL        MPF RECTS            VARIATION </fo:block>
           <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="4pt">&#160;      NAME OF THE SHED            ------------------   ---------------     -------------------</fo:block> 
           <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="4pt">CODE                                FAT(%)   SNF(%)     FAT(%)    SNF(%)     FAT(%)       SNF(%)</fo:block> 
           <fo:block font-size="8pt">-------------------------------------------------------------------------------------------------------------</fo:block>
           <fo:block font-size="5pt">
                    <fo:table>
                        <fo:table-column column-width="20pt"/>
						<fo:table-column column-width="50pt"/>
						<fo:table-column column-width="25pt"/>
						<fo:table-column column-width="25pt"/>
						<fo:table-column column-width="25pt"/>
						<fo:table-column column-width="25pt"/>
						<fo:table-column column-width="25pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="25pt"/>
                        <fo:table-body>
                        <#if centersFatSnfList?has_content>
                            <#list centersFatSnfList as centerFatSnf>
                            <#if centerFatSnf.facilityName?has_content>
                            <fo:table-row>
                                <fo:table-cell>
                                	<#if centerFatSnf.facilityCode != -1>
                                    	<fo:block text-align="left" font-size="4pt">${centerFatSnf.facilityCode}</fo:block>
                                   </#if> 	
                                </fo:table-cell>
                                <fo:table-cell>
                                   <#assign place = (centerFatSnf.facilityName).toUpperCase().replace("MILK","")>
                                    <fo:block text-align="left" keep-together="always" font-size="4pt">${place}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right" font-size="4pt">${centerFatSnf.procFat?if_exists?string("##0.0")} </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right" font-size="4pt">${centerFatSnf.procSnf?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right" font-size="4pt">${centerFatSnf.recvFat?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right" font-size="4pt">${centerFatSnf.recvSnf?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right" font-size="4pt">${centerFatSnf.varFat?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                	<fo:block text-align="right" font-size="4pt">${centerFatSnf.varSnf?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <#if centerFatSnf.facilityCode == -1>
	                                <fo:table-cell>
	                                    <fo:block text-align="left" font-size="4pt">*</fo:block>
	                                </fo:table-cell>
	                             <#else>
	                                <fo:table-cell>
	                                    <fo:block text-align="left" font-size="4pt">&#160;</fo:block>
	                                </fo:table-cell>
                                </#if>
                            </fo:table-row>
                            <fo:table-row>
                            	<fo:table-cell>
                                    <fo:block linefeed-treatment="preserve" font-size="4pt">&#xA;</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            </#if>
                           </#list>
                          </#if> 
                        </fo:table-body>
                    </fo:table>
            </fo:block>
          </fo:flow>
        </fo:page-sequence>
        <#else>
         <fo:page-sequence master-reference="main">
           <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
              <fo:block font-size="14pt">${uiLabelMap.NoOrdersFound}. </fo:block>
           </fo:flow>
         </fo:page-sequence>
     </#if>
     </#if>
   </fo:root> 
 </#escape>     