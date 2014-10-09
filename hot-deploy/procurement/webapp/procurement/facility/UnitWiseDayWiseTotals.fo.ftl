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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in" margin-left=".3in" margin-top=".2in" margin-bottom="2in">
                <fo:region-body margin-top="1.5in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
       </fo:layout-master-set>
       ${setRequestAttribute("OUTPUT_FILENAME", "unitDayWiseTotals.txt")}
<#if errorMessage?has_content>
	<fo:page-sequence master-reference="main">
	       <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	          <fo:block font-size="14pt">
	                  ${errorMessage}.
	       </fo:block>
	       </fo:flow>
	</fo:page-sequence>        
<#else>
		<#assign rowCount = 0> 
		<#assign numberOfLines =57>
        <#if dayWiseEntriesList?has_content>
       <fo:page-sequence master-reference="main">
        <fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" >
            <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
            <fo:block text-align="left" white-space-collapse="false"  keep-together="always">&#160;           STATEMENT OF UNIT WISE TOTAL LTS,KGS      PAGE NO:<fo:page-number/>  </fo:block>
            <fo:block font-size="8pt">--------------------------------------------------------------------------------------------</fo:block>
            <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="8pt">&#160; UNIT CODE           :${unitCode}                            UNIT NAME       :  ${unitName} </fo:block>
            <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="8pt">&#160; TRANSACTIONS DATE   :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} To ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}   		COLLECTION      :  MORNING AND EVENING</fo:block>             
            <fo:block font-size="8pt">--------------------------------------------------------------------------------------------</fo:block>
            <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="8pt">&#160;        D      T         GOOD MILK                   SOUR MILK            TOTAL      CURD</fo:block>
            <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="8pt">&#160;        A      Y  ------------------------------  ------------------  ---------------  -------</fo:block>
            <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="8pt">DATE     Y      P    LTS    KGS    FAT   SNF     LTS    KGS   FAT      LTS     KGS    LTS </fo:block>
            <fo:block font-size="8pt">--------------------------------------------------------------------------------------------</fo:block>
        </fo:static-content>
        <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
                    <fo:block font-size="7pt">
                      <fo:table>
                        <fo:table-column column-width="33pt"/><#--Date-->
                        <fo:table-column column-width="15pt"/><#--Day-->
                        <fo:table-column column-width="15pt"/><#--Type-->
                        <fo:table-column column-width="42pt"/><#--Lts-->
                        <fo:table-column column-width="40pt"/><#--kgS-->
                        <fo:table-column column-width="35pt"/><#--Fat-->
                        <fo:table-column column-width="30pt"/><#--Snf-->
                        <fo:table-column column-width="40pt"/><#--gKgFat-->
                        <fo:table-column column-width="45pt"/><#--kgSnf-->
                        <fo:table-column column-width="30pt"/><#--sLts-->
                        <fo:table-column column-width="35pt"/><#--sKgs-->
                        <fo:table-column column-width="35pt"/><#--sFat-->
                        <fo:table-column column-width="40pt"/><#--sKgFat-->
                        <fo:table-column column-width="44pt"/><#--tot Lts-->
                        <fo:table-column column-width="44pt"/><#--tot Kgs-->
                        <fo:table-column column-width="45pt"/><#--tot KgFat-->
                        <fo:table-column column-width="45pt"/><#--totKgSnf-->
                        <fo:table-column column-width="40pt"/><#--cLts-->
                      <fo:table-body>
                        <#assign rowCount=9>
                        <#list dayWiseEntriesList as dayWiseEntry>
                        	<#assign rowCount=rowCount+1>
                            	<#if (rowCount>=numberOfLines)>
                        	  		<#assign rowCount=9>
                        	  		<fo:table-row>
                                		<fo:table-cell>
                        	  				<fo:block font-size="8pt" page-break-before="always"/>
                        	  			</fo:table-cell>
                        	  		</fo:table-row>	
                        	  </#if>
                        <#if dayWiseEntry.milkType != "TOT">
                        <#assign date = dayWiseEntry.date?date("yyyy/mm/dd")> 
                        <fo:table-row>
                            <fo:table-cell><fo:block text-align = "left"> ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(date,"dd/mm")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell><fo:block text-align = "left"> ${dayWiseEntry.day}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell><fo:block text-align = "left">${dayWiseEntry.milkType}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell><fo:block text-align = "right">${(dayWiseEntry.qtyLtrs)?if_exists?string("##0.0")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell><fo:block text-align = "right">${(dayWiseEntry.qtyKgs)?if_exists?string("##0.0")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell><fo:block text-align="right">${(dayWiseEntry.fat)?if_exists?string("##0.0")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell><fo:block text-align="right">${(dayWiseEntry.snf)?if_exists?string("##0.00")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell><fo:block text-align="right">${(dayWiseEntry.sQtyLtrs)?if_exists?string("##0.0")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell><fo:block text-align="right">${(dayWiseEntry.sQtyKgs)?if_exists?string("##0.0")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell><fo:block text-align="right">${(dayWiseEntry.sFat)?if_exists?string("##0.0")}</fo:block>
                            </fo:table-cell>
                           
                            <fo:table-cell><fo:block text-align="right">${((dayWiseEntry.qtyLtrs)+((dayWiseEntry.sQtyLtrs)))?if_exists?string("##0.0")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell> <fo:block text-align="right">${((dayWiseEntry.qtyKgs)+((dayWiseEntry.sQtyKgs)))?if_exists?string("##0.0")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell><fo:block text-align="right">${(dayWiseEntry.cQtyLtrs)?if_exists?string("##0.0")}</fo:block>
                            </fo:table-cell>
                        </fo:table-row> 
                        <#else>
                        <fo:table-row>
                            <fo:table-cell>
                            </fo:table-cell>
                            <fo:table-cell><fo:block text-align = "right">${dayWiseEntry.milkType} :</fo:block>
                            </fo:table-cell>
                            <fo:table-cell>
                            </fo:table-cell>
                            <fo:table-cell><fo:block text-align = "right">${(dayWiseEntry.qtyLtrs)?if_exists?string("##0.0")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell><fo:block text-align = "right">${(dayWiseEntry.qtyKgs)?if_exists?string("##0.0")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell><fo:block text-align="right">${(dayWiseEntry.fat)?if_exists?string("##0.0")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell><fo:block text-align="right">${(dayWiseEntry.snf)?if_exists?string("##0.00")}</fo:block>
                            </fo:table-cell>
                           
                            <fo:table-cell><fo:block text-align="right">${(dayWiseEntry.sQtyLtrs)?if_exists?string("##0.0")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell><fo:block text-align="right">${(dayWiseEntry.sQtyKgs)?if_exists?string("##0.0")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell><fo:block text-align="right">${(dayWiseEntry.sFat)?if_exists?string("##0.0")}</fo:block>
                            </fo:table-cell>
                           
                            <fo:table-cell><fo:block text-align="right">${((dayWiseEntry.qtyLtrs)+((dayWiseEntry.sQtyLtrs)))?if_exists?string("##0.0")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell> <fo:block text-align="right">${((dayWiseEntry.qtyKgs)+((dayWiseEntry.sQtyKgs)))?if_exists?string("##0.0")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell><fo:block text-align="right">${(dayWiseEntry.cQtyLtrs)?if_exists?string("##0.0")}</fo:block>
                            </fo:table-cell>
                          </fo:table-row>
                          <fo:table-row> 
                             <fo:table-cell>
                             <#assign rowCount=rowCount+1>
                                <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                             </fo:table-cell>   
                          </fo:table-row>  
                        </#if>
                        </#list>
                        <#assign printLine=1>
                          <#list grandTotalsList as grandTotal>
                          <#if grandTotal.milkType != "TOT">
                          	<#assign rowCount = rowCount+8>
                            	<#if (rowCount>=numberOfLines)>
                        	  		<#assign rowCount=9>
                        	  		<fo:table-row>
                                		<fo:table-cell>
                        	  				<fo:block font-size="8pt" page-break-before="always"/>
                        	  			</fo:table-cell>
                        	  		</fo:table-row>	
                        	  		<#if printLine==1>
                        	  			<fo:table-row>
				                    		<fo:table-cell>
				            	  				<fo:block font-size="8pt">--------------------------------------------------------------------------------------------</fo:block>
				            	  			</fo:table-cell>
				            	  		</fo:table-row>
				            	  		<#assign printLine=printLine+1>
                        	  		</#if>
                        	  </#if>
                            <fo:table-row>
                                <fo:table-cell>
                                </fo:table-cell>
                                <fo:table-cell><fo:block text-align = "left"> ${grandTotal.day}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell><fo:block text-align = "left">${grandTotal.milkType}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell><fo:block text-align = "right">${(grandTotal.qtyLtrs)?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell><fo:block text-align = "right">${(grandTotal.qtyKgs)?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell><fo:block text-align="right">${(grandTotal.fat)?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell><fo:block text-align="right">${(grandTotal.snf)?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell><fo:block text-align="right">${(grandTotal.sQtyLtrs)?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell><fo:block text-align="right">${(grandTotal.sQtyKgs)?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell><fo:block text-align="right">${(grandTotal.sFat)?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                               
                                <fo:table-cell><fo:block text-align="right">${((grandTotal.qtyLtrs)+((grandTotal.sQtyLtrs)))?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell> <fo:block text-align="right">${((grandTotal.qtyKgs)+((grandTotal.sQtyKgs)))?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                               
                                <fo:table-cell><fo:block text-align="right">${(grandTotal.cQtyLtrs)?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <#else>
                            <#assign rowCount = rowCount+1>
                             <fo:table-row>
                                <fo:table-cell>
                                    <fo:block font-size="8pt">--------------------------------------------------------------------------------------------</fo:block>
                                </fo:table-cell>
                             </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell><fo:block text-align = "left" keep-together="always" white-space-collapse="false"  font-weight="bold">Grand TOT: </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                </fo:table-cell>
                                <fo:table-cell>
                                </fo:table-cell>
                                <fo:table-cell><fo:block text-align = "right" text-indent = "100pt">${(grandTotal.qtyLtrs)?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell><fo:block text-align = "right">${(grandTotal.qtyKgs)?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell><fo:block text-align="right">${(grandTotal.fat)?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell><fo:block text-align="right">${(grandTotal.snf)?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>                            
                                <fo:table-cell><fo:block text-align="right">${(grandTotal.sQtyLtrs)?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell><fo:block text-align="right">${(grandTotal.sQtyKgs)?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell><fo:block text-align="right">${(grandTotal.sFat)?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell><fo:block text-align="right">${((grandTotal.qtyLtrs)+((grandTotal.sQtyLtrs)))?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell> <fo:block text-align="right">${((grandTotal.qtyKgs)+((grandTotal.sQtyKgs)))?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                               	<fo:table-cell><fo:block text-align="right">${(grandTotal.cQtyLtrs)?if_exists?string("##0.0")}</fo:block>
                                </fo:table-cell>
                            </fo:table-row> 
                        </#if>
                        </#list>      
                       </fo:table-body>   
                     </fo:table> 
                   </fo:block>
                <fo:block font-size="8pt">--------------------------------------------------------------------------------------------</fo:block>
          </fo:flow>
        </fo:page-sequence>
        <#else>
         <fo:page-sequence master-reference="main">
           <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
              <fo:block font-size="14pt">${uiLabelMap.NoOrdersFound}. </fo:block>
           </fo:flow>
         </fo:page-sequence>
     </#if>
     </#if>
   </fo:root> 
 </#escape>     