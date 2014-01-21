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
       ${setRequestAttribute("OUTPUT_FILENAME", "UnitCenterwiseMilkColl.txt")}
        <#if centerTotalsMap?has_content>
        <#assign centerTotalsList = centerTotalsMap.getValue().entrySet()>
      
       <fo:page-sequence master-reference="main">
                    <fo:static-content flow-name="xsl-region-before">
                        <fo:block text-align="center" white-space-collapse="false" font-size="9pt" keep-together="always">UNIT WISE, CENTER WISE, MILK TYPE-WISE COLLECTION  </fo:block>
                        <fo:block font-size="8pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                        <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="9pt">.     UNIT CODE    : ${unitTotalsMap.unitCode}                                                                                                           UNIT NAME      :  ${unitTotalsMap.unitName}                                                                  </fo:block>
                        <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="9pt">.     PERIOD          :  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} To ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}                                                                  PAGE NO         :  <fo:page-number/></fo:block>             
                        <fo:block font-size="8pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                        <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="9pt">.        CENTER                        CENTER                                                                              B.M                       C.M                         TOTAL       </fo:block>
                        <fo:block keep-together="always" white-space-collapse="false" font-size="8pt" text-align="left">.         CODE                                 NAME                                                                                             LITERS                     LITERS                       LITERS     </fo:block>
                        <fo:block font-size="8pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                    </fo:static-content>
                    <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
                       <#list centerTotalsList as centerTotals>
                        <fo:block>
                           <fo:table>
                                <fo:table-column column-width="40pt"/>
                                <fo:table-column column-width="70pt"/>
                                <fo:table-column column-width="180pt"/>                
                                <fo:table-column column-width="80pt"/>
                                <fo:table-column column-width="80pt"/>
                                <fo:table-column column-width="80pt"/>
                                <fo:table-body>
                                    <fo:table-row>
                                      <fo:table-cell></fo:table-cell>
                                      <fo:table-cell>
                                        <fo:block text-align="left">${centerTotals.get("centerCode")}</fo:block>
                                      </fo:table-cell>
                                      <fo:table-cell>
                                        <fo:block text-align="left">${centerTotals.get("centerName")}</fo:block>
                                      </fo:table-cell>
                                      <fo:table-cell>
                                        <fo:block text-align="right">${centerTotals.get("BMTotal")?if_exists?string("##0.0")}</fo:block>
                                      </fo:table-cell>
                                      <fo:table-cell>
                                        <fo:block text-align="right">${centerTotals.get("CMTotal")?if_exists?string("##0.0")}</fo:block>
                                      </fo:table-cell>
                                      <fo:table-cell>
                                        <fo:block text-align="right">${centerTotals.get("totalqty")?if_exists?string("##0.0")}</fo:block>
                                      </fo:table-cell> 
                                    </fo:table-row>
                                </fo:table-body>
                            </fo:table>  
                        </fo:block>
                         </#list>
                        <fo:block font-size="8pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block> 
                        <fo:block>
                           <fo:table>
                                <fo:table-column column-width="40pt"/>
                                <fo:table-column column-width="70pt"/>
                                <fo:table-column column-width="180pt"/>                
                                <fo:table-column column-width="80pt"/>
                                <fo:table-column column-width="80pt"/>
                                <fo:table-column column-width="80pt"/>
                                <fo:table-body>
                                    <fo:table-row>
                                      <fo:table-cell></fo:table-cell>
                                      <fo:table-cell>
                                        <fo:block text-align="left">${unitTotalsMap.unitCode} </fo:block>
                                      </fo:table-cell>
                                      <fo:table-cell>
                                        <fo:block text-align="left">${unitTotalsMap.unitName} </fo:block>
                                      </fo:table-cell>
                                      <fo:table-cell>
                                        <fo:block text-align="right">${unitTotalsMap.grBMTotals?if_exists?string("##0.0")} </fo:block>
                                      </fo:table-cell>
                                      <fo:table-cell>
                                        <fo:block text-align="right">${unitTotalsMap.grCMTotals?if_exists?string("##0.0")} </fo:block>
                                      </fo:table-cell>
                                      <fo:table-cell>
                                        <fo:block text-align="right">${unitTotalsMap.grTotalQty?if_exists?string("##0.0")} </fo:block>
                                      </fo:table-cell> 
                                    </fo:table-row>
                                </fo:table-body>
                            </fo:table>  
                        </fo:block>
                        <fo:block font-size="8pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
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