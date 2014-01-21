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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="12in" margin-left="1in"  margin-right=".5in" margin-top=".2in" margin-bottom=".2in">
                <fo:region-body margin-top="1.4in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
       </fo:layout-master-set>
       ${setRequestAttribute("OUTPUT_FILENAME", "unitwiseIOAbst.txt")}
        <#if results == "Y">
       <fo:page-sequence master-reference="main">
            <fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
            	<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
                <fo:block text-align="left" white-space-collapse="false" font-size="9pt">&#160;                ${reportHeader.description?if_exists},  </fo:block>
                <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="9pt">&#160;     NAME OF THE MILK SHED    :    ${shed.facilityName}                                                                                                           </fo:block>
                <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="9pt">&#160;     NAME OF THE MCC/DAIRY    :    ${facility.facilityName}                                                                                                           </fo:block>
                <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="9pt">&#160;          PERIOD  FROM        :    ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")}   To   ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}      PAGE NO : <fo:page-number/></fo:block>  
                <fo:block font-size="7pt">-----------------------------------------------------------------------------------------------------</fo:block>
                <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="9pt">&#160;       STATEMENT SHOWING THE INPUT/OUTPUT KG-FAT AND KG-SNF ACCOUNT</fo:block>
                <fo:block font-size="7pt">-----------------------------------------------------------------------------------------------------</fo:block>
                <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160;  UNIT   NAME OF THE               QUANTITY   QUANTITY     TOTAL      TOTAL       AVG      AVG</fo:block>
                <fo:block keep-together="always" white-space-collapse="false" font-size="7pt" text-align="left">&#160;  CODE   MCC/DAIRY                 (LTS)       (KGS)       KG-FAT     KG-SNF      FAT      SNF</fo:block>
                <fo:block font-size="7pt">-----------------------------------------------------------------------------------------------------</fo:block>
            </fo:static-content>
            <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
                 <fo:block font-size="7pt">
                    <fo:table >
                        <fo:table-column column-width="30pt"/>
                        <fo:table-column column-width="55pt"/>
                        <fo:table-column column-width="100pt"/>
                        <fo:table-column column-width="49pt"/>
                        <fo:table-column column-width="49pt"/>
                        <fo:table-column column-width="49pt"/>
                        <fo:table-column column-width="35pt"/>
                        <fo:table-column column-width="35pt"/>
                        <fo:table-body>
                        <#assign totQtyLtrs =0>
                        <#assign totQtyKgs =0>
                        <#assign totKgFat =0>
                        <#assign totKgSnf =0>
                        <#if openingBalMap?has_content>
                            <#assign openingBal =openingBalMap.get("openingBalance")>
                            <fo:table-row>
                                <fo:table-cell>
                                </fo:table-cell>
                                <fo:table-cell>
                                  <fo:block keep-together="always" text-align="left">Opening Balance(MM) </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${openingBal.qtyLtrs?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${openingBal.get("qtyKgs")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${openingBal.get("kgFat")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${openingBal.get("kgSnf")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${openingBal.get("fat")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${openingBal.get("snf")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </#if> 
                        <#if tmPreparationMap?has_content>
                            <#if (tmPreparationMap.qtyKgs)?has_content && (tmPreparationMap.qtyKgs)!=0>
                            <fo:table-row>
                                <fo:table-cell>
                                </fo:table-cell>
                                <fo:table-cell>
                                  <fo:block keep-together="always" text-align="left">TM Preparation </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${tmPreparationMap.qtyLtrs?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${tmPreparationMap.get("qtyKgs")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${tmPreparationMap.get("kgFat")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${tmPreparationMap.get("kgSnf")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${tmPreparationMap.get("fat")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${tmPreparationMap.get("snf")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            </#if>
                        </#if> 
                          <#if procTotalsMap?has_content>
                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block text-align="left">${facility.facilityCode}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block keep-together="always" text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("facilityName").toUpperCase())),20)}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${procTotalsMap.get("qtyLtrs")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${procTotalsMap.get("qtyKgs")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${procTotalsMap.get("kgFat")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${procTotalsMap.get("kgSnf")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${procTotalsMap.get("fat")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${procTotalsMap.get("snf")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </#if>
                        <#if procThruTransfers?has_content> 
                          <#list procThruTransfers as procThruTransfer>   
                           <#if (procThruTransfer.qtyKgs)?has_content && (procThruTransfer.qtyKgs)!=0>	
                           <fo:table-row>
                                <fo:table-cell>
                                    <fo:block text-align="left">${procThruTransfer.facilityCode}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block keep-together="always" text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(procThruTransfer.get("facilityName").toUpperCase())),20)}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${procThruTransfer.get("qtyLtrs")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${procThruTransfer.get("qtyKgs")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${procThruTransfer.get("kgFat")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${procThruTransfer.get("kgSnf")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${procThruTransfer.get("fat")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${procThruTransfer.get("snf")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                             </#if>	
                            </#list>
                         </#if> 
                            <fo:table-row>
                                <fo:table-cell>
                                        <fo:block font-size="7pt">-----------------------------------------------------------------------------------------------------</fo:block>                     
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block text-align="left">${facility.facilityCode}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block keep-together="always" text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("facilityName").toUpperCase())),20)}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${dairyMap.get("qtyLtrs")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${dairyMap.get("qtyKgs")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${dairyMap.get("kgFat")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${dairyMap.get("kgSnf")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${dairyMap.get("fat")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${dairyMap.get("snf")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <#if iutTransfers?has_content>
	                            <fo:table-row>
	                                <fo:table-cell>
	                                        <fo:block font-size="7pt" text-align="left" keep-together="always">IUT RECEIPTS :</fo:block>                     
	                                </fo:table-cell>
	                            </fo:table-row>
	                            <#list iutTransfers as iutTransfer>
		                            <fo:table-row>
		                                <fo:table-cell>
		                                	<fo:block text-align="left">${iutTransfer.facilityCode}</fo:block>
		                                </fo:table-cell>
		                                <fo:table-cell>
		                                    <fo:block keep-together="always" text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(iutTransfer.get("facilityName").toUpperCase())),20)}</fo:block>
		                                </fo:table-cell>
		                                <fo:table-cell>
		                                    <fo:block text-align="right">${iutTransfer.get("qtyLtrs")?if_exists?string("##0.00")}</fo:block>
		                                </fo:table-cell>
		                                <fo:table-cell>
		                                    <fo:block text-align="right">${iutTransfer.get("qtyKgs")?if_exists?string("##0.00")}</fo:block>
		                                </fo:table-cell>
		                                <fo:table-cell>
		                                    <fo:block text-align="right">${iutTransfer.get("kgFat")?if_exists?string("##0.00")}</fo:block>
		                                </fo:table-cell>
		                                <fo:table-cell>
		                                    <fo:block text-align="right">${iutTransfer.get("kgSnf")?if_exists?string("##0.00")}</fo:block>
		                                </fo:table-cell>
		                                <fo:table-cell>
		                                    <fo:block text-align="right">${iutTransfer.get("fat")?if_exists?string("##0.00")}</fo:block>
		                                </fo:table-cell>
		                                <fo:table-cell>
		                                    <fo:block text-align="right">${iutTransfer.get("snf")?if_exists?string("##0.00")}</fo:block>
		                                </fo:table-cell>
		                            </fo:table-row>
	                            </#list>
                            </#if>
                            
                            
                            <fo:table-row>
                                <fo:table-cell>
                                        <fo:block font-size="7pt">-----------------------------------------------------------------------------------------------------</fo:block>                     
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell>
                                        <fo:block font-size="7pt">-----------------------------------------------------------------------------------------------------</fo:block>        
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell><fo:block text-align="left" keep-together="always" white-space-collapse="false">TOTAL INPUT     : </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${totInMap.get("qtyLtrs")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${totInMap.get("qtyKgs")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${totInMap.get("kgFat")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${totInMap.get("kgSnf")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${totInMap.get("fat")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${totInMap.get("snf")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell>
                                        <fo:block font-size="7pt">-----------------------------------------------------------------------------------------------------</fo:block>        
                                </fo:table-cell>
                            </fo:table-row>
                              <#if mpfReceiptsMap?has_content>
                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block text-align="left" keep-together="always">MPF RECEIPTS : </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block text-align="center">${facility.facilityCode} </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block keep-together="always" text-align="left">${facility.facilityName} </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${mpfReceiptsMap.get("qtyLtrs")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${mpfReceiptsMap.get("qtyKgs")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${mpfReceiptsMap.get("kgFat")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${mpfReceiptsMap.get("kgSnf")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${mpfReceiptsMap.get("fat")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${mpfReceiptsMap.get("snf")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                         </#if> 
                         <#if closingBalList?has_content>
                            <#list closingBalList as closingBalance>
                               <#if closingBalance.outputType!="CLOSING_BALANCE">
                               <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block text-align="left" keep-together="always">${closingBalance.outputType}</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block text-align="right">${closingBalance.get("qtyLtrs")?if_exists?string("##0.00")}</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block text-align="right">${closingBalance.get("qtyKgs")?if_exists?string("##0.00")}</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block text-align="right">${closingBalance.get("kgFat")?if_exists?string("##0.00")}</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block text-align="right">${closingBalance.get("kgSnf")?if_exists?string("##0.00")}</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block text-align="right">${closingBalance.get("fat")?if_exists?string("##0.00")}</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block text-align="right">${closingBalance.get("snf")?if_exists?string("##0.00")}</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <#else>
                                    <#assign outputType = closingBalance.outputType>
                                    <#assign qtyLtrs = closingBalance.qtyLtrs>
                                    <#assign qtyKgs = closingBalance.qtyKgs>
                                    <#assign kgFat = closingBalance.kgFat>
                                    <#assign kgSnf = closingBalance.kgSnf>
                                    <#assign fat = closingBalance.fat>
                                    <#assign snf = closingBalance.snf>
                                </#if>
                            </#list>
                            <#if qtyKgs?has_content && qtyKgs!=0>
                            <fo:table-row>
                                    <fo:table-cell>
                                       <fo:block text-align="left" keep-together="always">${outputType}</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block text-align="right">${(qtyLtrs?if_exists?string("##0.00"))?default("0.00")}</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block text-align="right">${(qtyKgs?if_exists?string("##0.00"))?default("0.00")}</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block text-align="right">${(kgFat?if_exists?string("##0.00"))?default("0.00")}</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block text-align="right">${(kgSnf?if_exists?string("##0.00"))?default("0.00")}</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block text-align="right">${(fat?if_exists?string("##0.00"))?default("0.00")}</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block text-align="right">${(snf?if_exists?string("##0.00"))?default("0.00")}</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                               </#if>                                
                        </#if> 
                             <fo:table-row>
                                <fo:table-cell>
                                        <fo:block font-size="7pt">-----------------------------------------------------------------------------------------------------</fo:block>        
                                </fo:table-cell>
                            </fo:table-row>
                             <fo:table-row>
                                <fo:table-cell>
                                    <fo:block text-align="left" keep-together="always" white-space-collapse="false">TOTAL OUTPUT    : </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${totOutMap.get("qtyLtrs")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${totOutMap.get("qtyKgs")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${totOutMap.get("kgFat")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${totOutMap.get("kgSnf")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${totOutMap.get("fat")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${totOutMap.get("snf")?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                            </fo:table-row> 
                            <fo:table-row>
                                <fo:table-cell>
                                        <fo:block font-size="7pt">-----------------------------------------------------------------------------------------------------</fo:block>                     
                                </fo:table-cell>
                            </fo:table-row> 
                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block text-align="left" keep-together="always">EXCESS/SHORTAGE : </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${(totOutMap.get("qtyLtrs")-totInMap.get("qtyLtrs"))?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${(totOutMap.get("qtyKgs")-totInMap.get("qtyKgs"))?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${(totOutMap.get("kgFat")-totInMap.get("kgFat"))?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${(totOutMap.get("kgSnf")-totInMap.get("kgSnf"))?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${(totOutMap.get("fat")-totInMap.get("fat"))?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">${(totOutMap.get("snf")-totInMap.get("snf"))?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>                        
                    </fo:table>    
                </fo:block>
                 <fo:block font-size="7pt">-----------------------------------------------------------------------------------------------------</fo:block>
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