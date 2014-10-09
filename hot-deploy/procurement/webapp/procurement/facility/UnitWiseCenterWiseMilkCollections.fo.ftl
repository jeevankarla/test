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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in" >
                <fo:region-body margin-top=".7in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
       </fo:layout-master-set>
       ${setRequestAttribute("OUTPUT_FILENAME", "UnitCenterwiseMilkColl.txt")}
<#if errorMessage?has_content>
<fo:page-sequence master-reference="main">
   <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
      <fo:block font-size="14pt">
              ${errorMessage}.
   	  </fo:block>
   </fo:flow>
</fo:page-sequence>        
<#else>        
        <#if centerTotalsMap?has_content>
        <#assign centerTotalsList = centerTotalsMap.getValue().entrySet()>
       <fo:page-sequence master-reference="main">
                    <fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="5pt">
                        <fo:block text-align="left" white-space-collapse="false" font-size="5pt" keep-together="always">&#160;    UNIT WISE, CENTER WISE, MILK TYPE-WISE COLLECTION                                            </fo:block>
                        <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">     UNIT CODE    : ${unitTotalsMap.unitCode}                             UNIT NAME:  ${unitTotalsMap.unitName}                                                                  </fo:block>
                        <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">     PERIOD       :  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} To ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}                           PAGE NO:  <fo:page-number/></fo:block>             
                        <fo:block font-size="5pt">--------------------------------------------------------------------------------</fo:block>
                        <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">CENTER    CENTER                          B.M            C.M            TOTAL</fo:block>
						<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">CODE      NAME                          LITRES         LITRES           LITRES</fo:block>
                        <fo:block font-size="5pt">--------------------------------------------------------------------------------</fo:block>
                    </fo:static-content>
                    <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
                       <#list centerTotalsList as centerTotals>
                        <fo:block>
                           <fo:table>
                                <fo:table-column column-width="5pt"/>
                                <fo:table-column column-width="20pt"/>
                                <fo:table-column column-width="40pt"/>                
                                <fo:table-column column-width="70pt"/>
                                <fo:table-column column-width="50pt"/>
                                <fo:table-column column-width="50pt"/>
                                <fo:table-body>
                                    <fo:table-row>
                                      <fo:table-cell></fo:table-cell>
                                      <fo:table-cell>
                                        <fo:block text-align="left" font-size="5pt">${centerTotals.get("centerCode")}</fo:block>
                                      </fo:table-cell>
                                      <fo:table-cell>
                                        <fo:block text-align="left" font-size="5pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(centerTotals.get("centerName")?if_exists)),13)}</fo:block>
                                      </fo:table-cell>
                                      <fo:table-cell>
                                        <fo:block text-align="right" font-size="5pt">${centerTotals.get("BMTotal")?if_exists?string("##0.0")}</fo:block>
                                      </fo:table-cell>
                                      <fo:table-cell>
                                        <fo:block text-align="right" font-size="5pt">${centerTotals.get("CMTotal")?if_exists?string("##0.0")}</fo:block>
                                      </fo:table-cell>
                                      <fo:table-cell>
                                        <fo:block text-align="right" font-size="5pt">${centerTotals.get("totalqty")?if_exists?string("##0.0")}</fo:block>
                                      </fo:table-cell> 
                                    </fo:table-row>
                                </fo:table-body>
                            </fo:table>  
                        </fo:block>
                         </#list>
                         <fo:block font-size="5pt">--------------------------------------------------------------------------------</fo:block> 
                        <fo:block>
                           <fo:table>
                                <fo:table-column column-width="10pt"/>
                                <fo:table-column column-width="15pt"/>
                                <fo:table-column column-width="60pt"/>                
                                <fo:table-column column-width="50pt"/>
                                <fo:table-column column-width="50pt"/>
                                <fo:table-column column-width="50pt"/>
                                <fo:table-body>
                                    <fo:table-row>
                                      <fo:table-cell></fo:table-cell>
                                      <fo:table-cell>
                                        <fo:block text-align="left" font-size="5pt">${unitTotalsMap.unitCode} </fo:block>
                                      </fo:table-cell>
                                      <fo:table-cell>
                                        <fo:block text-align="left" font-size="5pt">${unitTotalsMap.unitName} </fo:block>
                                      </fo:table-cell>
                                      <fo:table-cell>
                                        <fo:block text-align="right" font-size="5pt">${unitTotalsMap.grBMTotals?if_exists?string("##0.0")} </fo:block>
                                      </fo:table-cell>
                                      <fo:table-cell>
                                        <fo:block text-align="right" font-size="5pt">${unitTotalsMap.grCMTotals?if_exists?string("##0.0")} </fo:block>
                                      </fo:table-cell>
                                      <fo:table-cell>
                                        <fo:block text-align="right" font-size="5pt">${unitTotalsMap.grTotalQty?if_exists?string("##0.0")} </fo:block>
                                      </fo:table-cell> 
                                    </fo:table-row>
                                </fo:table-body>
                            </fo:table>  
                        </fo:block>
                         <fo:block font-size="5pt">--------------------------------------------------------------------------------</fo:block>
                    </fo:flow>
      </fo:page-sequence>
    <#else>
                <fo:page-sequence master-reference="main">
                    <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
                        <fo:block font-size="14pt">
                            ${uiLabelMap.NoOrdersFound}.
                        </fo:block>
                    </fo:flow>
                </fo:page-sequence>
     </#if>
     </#if>
   </fo:root> 
 </#escape>     