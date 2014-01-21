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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in" margin-left=".5in"  margin-right=".5in" margin-top=".2in" margin-bottom=".6in">
                <fo:region-body margin-top="1.4in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
       </fo:layout-master-set>
        <#if facility?has_content>
       <fo:page-sequence master-reference="main">
        <fo:static-content flow-name="xsl-region-before">
            <fo:block text-align="center" white-space-collapse="false" font-size="10pt" keep-together="always">MILK BILL STATEMENT ABSTRACT </fo:block>
            <fo:block font-size="8pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="9pt">&#160;       SHED NAME          :  ${facility.facilityName}                                                                  </fo:block>
            <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="9pt">&#160;       PERIOD FROM      :  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} To ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}                                                                                     </fo:block>             
            <fo:block font-size="8pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block> 
            <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="9pt"> 
            	<fo:table>
                    <fo:table-column column-width="55pt" />
                           <#list productRatesList as productRate>
                                <fo:table-column column-width="145pt" />
                           </#list>         
                    <fo:table-body>
                        <fo:table-row>
                            <fo:table-cell>
                                <fo:block white-space-collapse="false" text-align="center">APDDCF  </fo:block>
                            </fo:table-cell>
                            <#list productRatesList as productRate>
                    	        <fo:table-cell>
                                    <fo:block text-align="center">${productRate.productName}(${productRate.using})Rate:${productRate.defaultRate?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                            </#list>
                        </fo:table-row>
                    </fo:table-body>    
                </fo:table> 
            </fo:block>
            <fo:block font-size="8pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
        </fo:static-content>
       <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
       <fo:block font-size="9pt" keep-together="always" >
                <fo:table>
                    <fo:table-column size = "50in"/>
                    <fo:table-column size = "50in"/> 
                    <fo:table-body>
                        <fo:table-row>
                            <fo:table-cell>
                                <fo:block font-size="9pt" keep-together="always" text-align="left">
                                        <#assign products = productsBrandMap.entrySet()>
                                       <fo:table>
                                            <fo:table-column size = "100pt"/>
                                            <fo:table-column size = "2pt"/>
                                            <fo:table-column size = "30pt"/>
                                            <fo:table-body>
                                                 <#assign grossAmt = 0>
                                                 <#assign products = productsBrandMap.entrySet()>
                                                  <#list products as product>
                                                  	<#assign productKey = product.getKey()>
                                                    <fo:table-row>
                                                          <fo:table-cell> 
                                                             <fo:block text-align="left"> ${productKey} TOTAL MILK AMOUNT</fo:block>
                                                          </fo:table-cell>
                                                          <fo:table-cell> 
                                                             <fo:block text-align="center"> :</fo:block>
                                                          </fo:table-cell>
                                                          <fo:table-cell> 
                                                             <fo:block text-align="right"> ${(totAmountsMap.get(productKey))?if_exists?string("##0.00")}</fo:block>
                                                             <#assign grossAmt = grossAmt+((totAmountsMap.get(productKey)))>
                                                          </fo:table-cell>
                                                    </fo:table-row>
                                                  </#list>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="left"> TOTAL OP-COST </fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="center"> :</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <#--here we are displaying commission amount as op-cost -->   
                                                     <#assign grossAmt = grossAmt+((totAmountsMap.get("opCost")))>
                                                     <fo:block text-align="right">${(totAmountsMap.get("opCost"))?if_exists?string("##0.00")} </fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="left"> TOTAL CARTAGE </fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="center"> :</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                        <#assign grossAmt = grossAmt+((totAmountsMap.get("cartage")))>
                                                     <fo:block text-align="right"> ${((totAmountsMap.get("cartage")))?if_exists?string("##0.00")}</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="left"> TOTAL ADDITIONS</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="center"> :</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                        <#assign grossAmt = grossAmt+((totAmountsMap.get("addnAmt")))>
                                                     <fo:block text-align="right"> ${((totAmountsMap.get("addnAmt")))?if_exists?string("##0.00")}</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="left"> TOTAL TIP AMOUNT</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="center"> :</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                        <#assign grossAmt = grossAmt+((totAmountsMap.get("tipAmt")))>
                                                     <fo:block text-align="right"> ${((totAmountsMap.get("tipAmt")))?if_exists?string("##0.00")}</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="left"> TOTAL DIF AMOUNT</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="center"> :</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                        <#assign grossAmt = grossAmt+difAmt>
                                                     <fo:block text-align="right"> ${(difAmt)?if_exists?string("##0.00")}</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right"> ------------------------------------</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="left"> GROSS AMOUNT</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="center"> :</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right"> ${grossAmt?if_exists?string("##0.00")}</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right"> ------------------------------------</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                    <fo:table-cell></fo:table-cell><fo:table-cell></fo:table-cell><fo:table-cell></fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="left"> FEED RECOVERY AMOUNT</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="center"> :</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right">${(feedAmt*(-1))?if_exists?string("##0.00")}</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="left"> CESS ON LOCAL SALE </fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="center"> :</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right"> ${(cessOnSaleAmt*(-1))?if_exists?string("##0.00")}</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="left"> LESS KGFAT AMOUNT </fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="center"> :</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right"> ${(totshrtKgFatAmt)?if_exists?string("##0.00")}</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="left"> LESS KGSNF AMOUNT </fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="center"> :</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right"> ${(totshrtKgSnfAmt)?if_exists?string("##0.00")}</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="left"> SOUR AMOUNT </fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="center"> :</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right"> ${((totAmountsMap.get("sprice"))*(-1))?if_exists?string("##0.00")}</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right"> ------------------------------------</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <#assign totDedAmt = (totAmountsMap.get("sprice")+(totshrtKgFatAmt*(-1))+(totshrtKgSnfAmt*(-1))+(feedAmt)+(cessOnSaleAmt))>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="left"> TOTAL DEDUCTION</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="center"> :</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right"> ${(totDedAmt*(-1))?if_exists?string("##0.00")}</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right"> ------------------------------------</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="left">NET AMOUNT PAYABLE</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="center"> :</fo:block>
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right">${(grossAmt-totDedAmt)?if_exists?string("##0.00")}</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                                <fo:table-row>
                                                  <fo:table-cell> 
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                  </fo:table-cell>
                                                  <fo:table-cell> 
                                                     <fo:block text-align="right"> ------------------------------------</fo:block>
                                                  </fo:table-cell>
                                                </fo:table-row>
                                            </fo:table-body>
                                       </fo:table> 
                                </fo:block>    
                            </fo:table-cell>
                            <fo:table-cell>
                                <fo:block font-size="9pt" keep-together="always" text-align ="center"  >
                                        <fo:table>
                                            <fo:table-column size = "30pt"/>
                                            <fo:table-column size = "30pt"/>
                                            <fo:table-column size = "30pt"/>
                                            <fo:table-column size = "30pt"/>
                                            <fo:table-header>
                                                <fo:table-row font-weight="bold">
                                                    <fo:table-cell padding="3pt" >
                                                        <fo:block text-align = "right">MILK</fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell padding="3pt" >
                                                        <fo:block text-align = "right">QTY-LTS</fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell padding="3pt" >
                                                        <fo:block text-align = "right">AVG-LTS</fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell padding="3pt">
                                                        <fo:block text-align = "right">AVG-RATE</fo:block>
                                                    </fo:table-cell>
                                                </fo:table-row>
                                            </fo:table-header>  
                                            <fo:table-body>
                                                  <#assign products = productsBrandMap.entrySet()>
                                                  <#assign totLtrs = 0>
                                                  <#assign totAvgLtrs = 0>
                                                  <#assign totPrice = 0>
                                                  <#list products as product>
                                                    <fo:table-row>
                                                        <fo:table-cell>
                                                            <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                                                        </fo:table-cell>
                                                   </fo:table-row>
                                                    <fo:table-row>
                                                          <fo:table-cell> 
                                                             <fo:block text-align="right"> ${product.getKey()}</fo:block>
                                                          </fo:table-cell>
                                                          <fo:table-cell> 
                                                             <#assign productName = product.getValue()>
                                                             <#assign qtyLtrs = ((totalsMap.get(productName).get("qtyLtrs"))) >
                                                             <#assign totLtrs = totLtrs+qtyLtrs>
                                                             <fo:block text-align="right"> ${qtyLtrs?if_exists?string("##0")}</fo:block>
                                                          </fo:table-cell>
                                                          <fo:table-cell> 
                                                              <#assign avgQty = (qtyLtrs/noOfDays)> 
                                                              <#assign price = ((totalsMap.get(productName).get("price")).setScale(0,1))>
                                                              <#assign totPrice = totPrice+price>
                                                              <#if qtyLtrs!=0>
                                                              	<#assign avgRate = price/qtyLtrs>
                                                              	<#else>
                                                              	<#assign avgRate = 0>
                                                              </#if>
                                                             <fo:block text-align="right"> ${avgQty?if_exists?string("#0")}</fo:block>
                                                          </fo:table-cell>
                                                          <fo:table-cell> 
                                                             <fo:block text-align="right">${avgRate?if_exists?string("##0.00")}</fo:block>
                                                          </fo:table-cell>
                                                    </fo:table-row>
                                                  </#list>
                                                   <fo:table-row>
                                                        <fo:table-cell>
                                                            <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                                                        </fo:table-cell>
                                                   </fo:table-row>
                                                   <fo:table-row>
                                                          <fo:table-cell> 
                                                             <fo:block text-align="right"> MM</fo:block>
                                                          </fo:table-cell>
                                                          <fo:table-cell> 
                                                             <fo:block text-align="right"> ${totLtrs?if_exists?string("##0")}</fo:block>
                                                          </fo:table-cell>
                                                          <fo:table-cell> 
                                                          		<#assign totAvgLtrs = totLtrs/noOfDays>
                                                             <fo:block text-align="right"> ${totAvgLtrs?if_exists?string("#0")}</fo:block>
                                                          </fo:table-cell>
                                                            <fo:table-cell>
                                                             <#assign avgRate = totPrice/totLtrs>
                                                             <fo:block text-align="right"> ${avgRate?if_exists?string("##0.00")}</fo:block>
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
           <fo:block font-size="8pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
           <fo:block>&#160;</fo:block>
           <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="9pt">&#160;       PERIOD FROM      :  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} To ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}                                                                                     </fo:block>
           <fo:block font-size="8pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
           <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="9pt">&#160;       QUALITY VARIATION BETWEEN UNIT-WISE BILLING AND MILK RECEIPTS AT MPF HYD                                                                                      </fo:block> 
           <fo:block font-size="8pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
           <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="9pt">    UNIT                                                                                       ASPER MILK BILL                                MPF RECTS                                              VARIATION                 </fo:block>
           <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="9pt">&#160;                             NAME OF THE SHED                   ------------------------------------------            ------------------------------------------              ------------------------------------------                  </fo:block> 
           <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="9pt">    CODE                                                                              FAT(%)                  SNF(%)                   FAT(%)                     SNF(%)                   FAT(%)                   SNF(%)             </fo:block> 
           <fo:block font-size="8pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>   
            <fo:block font-size="9pt">
                    <fo:table>
                        <fo:table-column size = "10pt"/>
                        <fo:table-column size = "90pt"/>
                        <fo:table-column size = "30pt"/>
                        <fo:table-column size = "30pt"/>
                        <fo:table-column size = "30pt"/>
                        <fo:table-column size = "30pt"/>
                        <fo:table-column size = "30pt"/>
                        <fo:table-column size = "30pt"/>
                        <fo:table-body>
                            <#list centersFatSnfList as centerFatSnf>
                            <#if centerFatSnf.facilityName?has_content>
                            <fo:table-row>
                                <fo:table-cell>
                                	<#if centerFatSnf.facilityCode !="TOT">
                                    	<fo:block text-align="left">${centerFatSnf.facilityCode}</fo:block>
                                   </#if> 	
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="left" keep-together="always">${centerFatSnf.facilityName}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${centerFatSnf.procFat?if_exists?string("##0.0")} </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${centerFatSnf.procSnf?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${centerFatSnf.recvFat?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${centerFatSnf.recvSnf?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${((centerFatSnf.recvFat)-(centerFatSnf.procFat))?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                	<#if centerFatSnf.facilityCode !="TOT">
                                    	<fo:block text-align="right">${((centerFatSnf.recvSnf)-(centerFatSnf.procSnf))?if_exists?string("##0.00")}</fo:block>
                                    <#else>
                                    	<fo:block text-align="right">${((centerFatSnf.recvSnf)-(centerFatSnf.procSnf))?if_exists?string("##0.00")}*</fo:block>
                                    </#if>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                            <fo:table-cell>
                                    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            </#if>
                           </#list>
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
   </fo:root> 
 </#escape>     