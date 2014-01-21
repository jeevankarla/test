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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="12in" margin-left=".5in"  margin-right=".5in" margin-top=".2in" margin-bottom=".6in">
                <fo:region-body margin-top="1.26in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
       </fo:layout-master-set>
        <#if facilityShortagesList?has_content>
       <fo:page-sequence master-reference="main">
            <fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
                <fo:block text-align="left" white-space-collapse="false" font-size="7pt" keep-together="always">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;STATEMENT SHOWING THE RECOVERABLE AMOUNT OF SHORTAGE KG-FAT, KG-SNF&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
                <fo:block font-size="7pt">----------------------------------------------------------------------------------------------------------------</fo:block>
                <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160;     MILK SHED NAME    :    ${facility.facilityName}</fo:block>
                <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160;     PERIOD  FROM      :    ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")}   To   ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}               PAGE NO         :  <fo:page-number/></fo:block>             
                <fo:block font-size="7pt">----------------------------------------------------------------------------------------------------------------</fo:block>
                <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">        UNIT       NAME OF THE UNIT      SHORT         SHORT          SHORT       SHORT          SOUR/         TOTAL</fo:block>
                <fo:block keep-together="always" white-space-collapse="false" font-size="7pt" text-align="left">&#160;                                KG-FAT        KG-SNF         KG-FAT      KG-SNF         OTHER      RECOVERABLE  </fo:block>
                <fo:block keep-together="always" white-space-collapse="false" font-size="7pt" text-align="left">         CODE                                               		         AMOUNT      AMOUNT         AMOUNT        AMOUNT </fo:block>
                <fo:block font-size="7pt">----------------------------------------------------------------------------------------------------------------</fo:block>
            </fo:static-content>
            <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
                   <#assign totKgFat = 0 >
                   <#assign totKgSnf = 0 > 
                   <#assign totKgFatAmt = 0>
                   <#assign totKgSnfAmt = 0>
                   <#assign totSPrice = 0>
                   <#assign totRecoverableAmt = 0 >
                   
                    <fo:block font-size="6pt" keep-together="always">
                       <fo:table>
                                <fo:table-column column-width="25pt"/>
                                <fo:table-column column-width="70pt"/>
                                <fo:table-column column-width="60pt"/>                
                                <fo:table-column column-width="60pt"/>
                                <fo:table-column column-width="60pt"/>
                                <fo:table-column column-width="60pt"/>
                                <fo:table-column column-width="60pt"/>
                                <fo:table-column column-width="20mm"/>
                              
                                
                          <fo:table-body>
                          <#list facilityShortagesList as shortagesList>
                            <#if shortagesList.get("facilityCode")!="TOT">     
                                <#assign totRecAmt = (shortagesList.kgFatAmt+shortagesList.kgSnfAmt+shortagesList.sPrice)>
                                	<#assign kgFat = shortagesList.kgFat>
                              		<#if (kgFat<0)>
                              			<#assign totKgFat = (shortagesList.get("kgFat")+totKgFat)>
                              			<#else>
                              			<#assign kgFat = 0>
                              		</#if>
                              		<#assign kgSnf = shortagesList.kgSnf>
                              		<#if (kgSnf<0)>
                              			<#assign totKgSnf = (shortagesList.get("kgSnf")+totKgSnf)>
                              			<#else>
                              			<#assign kgSnf = 0>
                              		</#if>
                                 <#if totRecAmt!=0 > 	
                                	<#assign totKgFatAmt = (shortagesList.get("kgFatAmt")+totKgFatAmt)>
                                	<#assign totKgSnfAmt = (shortagesList.get("kgSnfAmt")+totKgSnfAmt)>
                                	<#assign totSPrice = (shortagesList.get("sPrice")+totSPrice)>
	                             	<fo:table-row>
	                                  <fo:table-cell>
	                                        <fo:block font-size="6pt" text-align="left">${shortagesList.facilityCode} </fo:block>
	                                  </fo:table-cell>
	                                  <fo:table-cell>
	                                        <fo:block font-size="6pt" text-align="left"  keep-together="always">${shortagesList.facilityName}  </fo:block>
	                                  </fo:table-cell>
	                                  <fo:table-cell>
	                                        <fo:block font-size="6pt" text-align="right">${kgFat?if_exists?string("##0.000")}  </fo:block>
	                                  </fo:table-cell>
	                                  <fo:table-cell>
	                                        <fo:block font-size="6pt" text-align="right">${kgSnf?if_exists?string("##0.000")}  </fo:block>
	                                  </fo:table-cell>
	                                  <fo:table-cell>
	                                        <fo:block font-size="6pt" text-align="right">${shortagesList.kgFatAmt?if_exists?string("##0.00")} </fo:block>
	                                  </fo:table-cell>
	                                  <fo:table-cell>
	                                        <fo:block font-size="6pt" text-align="right">${shortagesList.kgSnfAmt?if_exists?string("##0.00")}  </fo:block>
	                                  </fo:table-cell>
	                                  <fo:table-cell>
	                                        <fo:block font-size="6pt" text-align="right"> ${shortagesList.sPrice?if_exists?string("##0.00")}</fo:block>
	                                  </fo:table-cell>
	                                  <fo:table-cell>
	                                        <fo:block font-size="6pt" text-align="right">${totRecAmt?if_exists?string("##0.00")} </fo:block>
	                                  </fo:table-cell>
	                               	</fo:table-row>
	                              </#if> 	
                              <#else>
                                <#assign totRecAmt = (shortagesList.kgFatAmt+shortagesList.kgSnfAmt+shortagesList.sPrice)>
                                <#if totRecAmt!=0>
                                 <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block font-size="7pt">----------------------------------------------------------------------------------------------------------------</fo:block>
                                        <fo:block font-size="6pt" linefeed-treatment="preserve">&#xA;</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                  <fo:table-cell>
                                        <fo:block font-size="6pt" ></fo:block>
                                  </fo:table-cell>
                                  <fo:table-cell>
                                        <fo:block font-size="6pt"  keep-together="always" text-align="left">${shortagesList.facilityName}  </fo:block>
                                  </fo:table-cell>
                                  <fo:table-cell>
                                        <fo:block font-size="6pt" text-align="right">${shortagesList.kgFat?if_exists?string("##0.000")}  </fo:block>
                                  </fo:table-cell>
                                  <fo:table-cell>
                                        <fo:block font-size="6pt" text-align="right">${shortagesList.kgSnf?if_exists?string("##0.000")}  </fo:block>
                                  </fo:table-cell>
                                  <fo:table-cell>
                                        <fo:block font-size="6pt" text-align="right">${shortagesList.kgFatAmt?if_exists?string("##0.00")} </fo:block>
                                  </fo:table-cell>
                                  <fo:table-cell>
                                        <fo:block font-size="6pt" text-align="right">${shortagesList.kgSnfAmt?if_exists?string("##0.00")}  </fo:block>
                                  </fo:table-cell>
                                  <fo:table-cell>
                                        <fo:block font-size="6pt" text-align="right"> ${shortagesList.sPrice?if_exists?string("##0.00")}</fo:block>
                                  </fo:table-cell>
                                  <fo:table-cell>
                                        <fo:block font-size="6pt" text-align="right">${(shortagesList.kgFatAmt+shortagesList.kgSnfAmt+shortagesList.sPrice)?if_exists?string("##0.00")}* </fo:block>
                                  </fo:table-cell>
                             </fo:table-row>
                             <fo:table-row> 
                                 <fo:table-cell>
                                        <fo:block font-size="7pt">----------------------------------------------------------------------------------------------------------------</fo:block>
                                  </fo:table-cell>
                             </fo:table-row>
                             </#if>
                            </#if>  
                           </#list>   
                           <fo:table-row>
                           		<fo:table-cell>
                           			<fo:block font-size="7pt">----------------------------------------------------------------------------------------------------------------</fo:block>
                           		</fo:table-cell>
                           </fo:table-row>
                           <#assign totRecoverableAmt = totRecoverableAmt+(totKgFatAmt+totKgSnfAmt+totSPrice)>
                           <fo:table-row>
                           		<fo:table-cell></fo:table-cell>
                                <fo:table-cell>
                                    <fo:block font-size="6pt" keep-together="always">${facility.facilityName}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block font-size="6pt"  text-align="right"> ${totKgFat?if_exists?string("##0.000")}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block font-size="6pt" text-align="right">${totKgSnf?if_exists?string("##0.000")} </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block font-size="6pt" text-align="right">${totKgFatAmt?if_exists?string("##0.00")} </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block font-size="6pt" text-align="right">${totKgSnfAmt?if_exists?string("##0.00")} </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block font-size="6pt" text-align="right">${totSPrice?if_exists?string("##0.00")} </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block font-size="6pt" text-align="right">${totRecoverableAmt?if_exists?string("##0.00")}* </fo:block>
                                </fo:table-cell>
                           </fo:table-row>
                           <fo:table-row>
                           		<fo:table-cell>
                           			<fo:block font-size="7pt">----------------------------------------------------------------------------------------------------------------</fo:block>
                           		</fo:table-cell>
                           </fo:table-row>
                           <fo:table-row>
                           		<fo:table-cell></fo:table-cell>
                                <fo:table-cell>
                                    <fo:block font-size="6pt" keep-together="always">* LESS TIP AMOUNT </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                </fo:table-cell>
                                <fo:table-cell>
                                </fo:table-cell>
                                <fo:table-cell>
                                </fo:table-cell>
                                <fo:table-cell>
                                </fo:table-cell>
                                <fo:table-cell>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block font-size="6pt" text-align="right">${tipAmt?if_exists?string("##0")} *</fo:block>
                                </fo:table-cell>
                           </fo:table-row>
                          </fo:table-body>
                       </fo:table>
                    </fo:block>
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
   </fo:root> 
 </#escape>     