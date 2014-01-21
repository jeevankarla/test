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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-left=".1in"  margin-right=".5in" margin-top=".2in" margin-bottom=".2in">
                <fo:region-body margin-top="1in"/>
                <fo:region-before extent="0in"/>
                <fo:region-after extent="0in"/>
            </fo:simple-page-master>
       </fo:layout-master-set>
       ${setRequestAttribute("OUTPUT_FILENAME", "CenterWiseKgFatAcc.txt")}
       <#if dayWiseEntriesList?has_content>
        <#assign numberOfLines=58>
        <#assign rowCount=0>
       <fo:page-sequence master-reference="main">
            <fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
                <fo:block text-align="left" white-space-collapse="false"  keep-together="always">&#160;                          STATEMENT FOR MCC CENTER WISE LTS,KGS,KGFAT,KGSNF  </fo:block>
                        <fo:block font-size="6pt">------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                        <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="6pt">&#160; UNIT NAME     :   ${unit.facilityName}                                                  ROUTE NAME       :   ${route.facilityName} </fo:block>
                        <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="6pt">&#160; CENTER NAME   :   ${facility.facilityName}                                                  TRANSACTIONS DATE   :   ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} To ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}                                </fo:block>
                        <fo:block font-size="6pt">------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block> 
                        <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="6pt">&#160;       D    R    M   T            GOOD MILK                                          SOUR MILK                                TOTAL                       CURDLED</fo:block>
                        <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="6pt">&#160;       A    N    C   Y   -------------------------------------------------     -----------------------------      ------------------------------------     ------</fo:block>
                        <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="6pt">  DATE    Y    O    T   P   LTS     KGS     FAT    SNF      KGFAT      KGSNF       LTS    KGS    FAT     KGFAT        LTS       KGS       KGFAT       KGSNF      LTS </fo:block>
                        <fo:block font-size="6pt">------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            </fo:static-content>
            <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
                <fo:block keep-together="always">
                    <fo:table width="100%" table-layout="fixed" space-after="0.0in">
                        <fo:table-column column-width="8mm"/><#--1.Date -->
                        <fo:table-column column-width="5mm"/><#--2.Day -->
                        <fo:table-column column-width="2mm"/><#--3.RNO -->
                        <fo:table-column column-width="3mm"/><#--4.Mct -->
                        <fo:table-column column-width="20pt"/><#--5.type -->
                        <fo:table-column column-width="13mm"/><#--6.gQtyLts -->
                        <fo:table-column column-width="13mm"/><#--7.gQtyKgs-->
                        <fo:table-column column-width="25pt"/><#--8.gFat-->
                        <fo:table-column column-width="25pt"/><#--9.gSnf -->
                        <fo:table-column column-width="14mm"/><#--10.gKgFat -->
                        <fo:table-column column-width="14mm"/><#--11.gKgSnf -->
                        <fo:table-column column-width="10mm"/><#--12sQtyLts-->
                        <fo:table-column column-width="10mm"/><#--13.sQtyKgs -->
                        <fo:table-column column-width="10mm"/><#--14sFat-->
                        <fo:table-column column-width="12mm"/><#--15sKgFat -->
                        <fo:table-column column-width="14mm"/><#--16tLts -->
                        <fo:table-column column-width="14mm"/><#--17tKgs -->
                        <fo:table-column column-width="15mm"/><#--18tKgFat -->
                        <fo:table-column column-width="15mm"/><#--19tKgSnf -->
                        <fo:table-column column-width="10mm"/><#--20curdLts -->
                    <fo:table-body>
                       <#assign rowCount=9>
                       <#list dayWiseEntriesList as dayWiseEntry>
                       <#assign date = (dayWiseEntry.date?date("yyyy/MM/dd"))>   
                        	<#if dayWiseEntry.qtyKgs!=0 || dayWiseEntry.sQtyLtrs!=0 || dayWiseEntry.cQtyLtrs!=0>  
                        	<#assign rowCount=rowCount+1>
                            	<#if (rowCount>=numberOfLines)>
                        	  		<#assign rowCount=9>
                        	  		<fo:table-row>
                                		<fo:table-cell>
                        	  				<fo:block font-size="5pt" page-break-before="always"/>
                        	  			</fo:table-cell>
                        	  		</fo:table-row>	
                        	  </#if>
                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block text-align = "left" font-size="5pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(date, "dd/MM")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "left" font-size="5pt">${dayWiseEntry.day}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "left" font-size="5pt">${route.facilityCode}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "left" font-size="5pt">1</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "left" font-size="5pt">${dayWiseEntry.milkType}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="5pt">${dayWiseEntry.qtyLtrs?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="5pt">${dayWiseEntry.qtyKgs?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="5pt">${dayWiseEntry.fat?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="5pt">${dayWiseEntry.snf?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="5pt">${dayWiseEntry.gKgFat?if_exists?string("##0.000")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="5pt">${dayWiseEntry.kgSnf?if_exists?string("##0.000")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="5pt">${dayWiseEntry.sQtyLtrs?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="5pt">${dayWiseEntry.sQtyKgs?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="5pt">${dayWiseEntry.sFat?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="5pt">${dayWiseEntry.sKgFat?if_exists?string("##0.000")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="5pt">${((dayWiseEntry.qtyLtrs)+(dayWiseEntry.sQtyLtrs))?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="5pt">${((dayWiseEntry.qtyKgs)+(dayWiseEntry.sQtyKgs))?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="5pt">${((dayWiseEntry.gKgFat)+(dayWiseEntry.sKgFat))?if_exists?string("##0.000")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="5pt">${(dayWiseEntry.kgSnf)?if_exists?string("##0.000")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="5pt">${(dayWiseEntry.cQtyLtrs)?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            </#if>
                            </#list>
                            <#assign rowCount=rowCount+6>
                            	<#if (rowCount>=numberOfLines)>
                        	  		<#assign rowCount=9>
                        	  		<fo:table-row>
                                		<fo:table-cell>
                        	  				<fo:block font-size="6pt" page-break-before="always"/>
                        	  			</fo:table-cell>
                        	  		</fo:table-row>	
                        	  </#if>
                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block font-size="6pt">------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <#list totalsList as totals>
                            <fo:table-row>
                                <fo:table-cell> </fo:table-cell>
                                <fo:table-cell> </fo:table-cell>
                                <fo:table-cell> </fo:table-cell>
                                <fo:table-cell> </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "left" font-size="5pt" text-indent="20pt">${totals.milkType}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="6pt">${totals.qtyLtrs?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="6pt">${totals.qtyKgs?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="6pt">${totals.fat?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="6pt">${totals.snf?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="6pt">${totals.gKgFat?if_exists?string("##0.000")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="6pt">${totals.kgSnf?if_exists?string("##0.000")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="6pt">${totals.sQtyLtrs?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="6pt">${totals.sQtyKgs?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="6pt">${totals.sFat?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="6pt">${totals.sKgFat?if_exists?string("##0.000")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="6pt">${((totals.qtyLtrs)+(totals.sQtyLtrs))?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="6pt">${((totals.qtyKgs)+(totals.sQtyKgs))?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="6pt">${((totals.gKgFat)+(totals.sKgFat))?if_exists?string("##0.000")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="6pt">${(totals.kgSnf)?if_exists?string("##0.000")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="6pt">${(totals.cQtyLtrs)?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            </#list>
                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block font-size="6pt">------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>            
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell></fo:table-cell>
                                <fo:table-cell></fo:table-cell>
                                <fo:table-cell></fo:table-cell>
                                <fo:table-cell> <fo:block text-align = "right" font-size="5pt"></fo:block>
                                </fo:table-cell>
                                <fo:table-cell><fo:block text-align = "left" font-size="5pt" keep-together="always" text-indent="10pt">TOT :</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="6pt">${grandTotals.qtyLtrs?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="6pt">${grandTotals.qtyKgs?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="6pt">${grandTotals.fat?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="6pt">${grandTotals.snf?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="6pt">${grandTotals.gKgFat?if_exists?string("##0.000")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="6pt">${grandTotals.kgSnf?if_exists?string("##0.000")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="6pt">${grandTotals.sQtyLtrs?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="6pt">${grandTotals.sQtyKgs?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="6pt">${grandTotals.sFat?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="6pt">${grandTotals.sKgFat?if_exists?string("##0.000")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="6pt">${((grandTotals.qtyLtrs)+(grandTotals.sQtyLtrs))?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="6pt">${((grandTotals.qtyKgs)+(grandTotals.sQtyKgs))?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="6pt">${((grandTotals.gKgFat)+(grandTotals.sKgFat))?if_exists?string("##0.000")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="6pt">${(grandTotals.kgSnf)?if_exists?string("##0.000")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align = "right" font-size="6pt">${(grandTotals.cQtyLtrs)?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>    
                    <fo:block font-size="6pt">------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                </fo:block>
            </fo:flow>
       </fo:page-sequence>
    <#else>
                <fo:page-sequence master-reference="main">
                    <fo:flow flow-name="xsl-region-body">
                        <fo:block font-size="14pt" text-align="center">
                            ${uiLabelMap.NoOrdersFound}.
                        </fo:block>
                    </fo:flow>
                </fo:page-sequence>
     </#if>
   </fo:root> 
 </#escape>     