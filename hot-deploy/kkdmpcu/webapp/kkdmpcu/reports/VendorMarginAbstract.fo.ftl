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

<#-- do not display columns associated with values specified in the request, ie constraint values -->

<fo:layout-master-set>
    <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
            margin-top="0.5in" margin-bottom="1in">
        <fo:region-body margin-top="1.2in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>
    </fo:simple-page-master>
</fo:layout-master-set>
<#assign WMBulk =(Static["java.math.BigDecimal"].ZERO)>
<#assign DTBulk =(Static["java.math.BigDecimal"].ZERO)>
<#assign TMBulk =(Static["java.math.BigDecimal"].ZERO)>


        <fo:page-sequence master-reference="main" >
            <fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
<fo:block text-align="center" keep-together="always">KRISHNAVENI  KKDMPCU  LTD                             VIJAYAWADA</fo:block>
<fo:block text-align="center"  keep-together="always" white-space-collapse="false">Abstract of Vendor Margin for the month of ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "MM/yyyy")}</fo:block>
<fo:block keep-together="always" >--------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>	   	
            <fo:block white-space-collapse="false" keep-together="always">AGENT          WHM        GOLD       T500       STD        D500      D200       TOTAL       AVG       MAINT       DUE       NET     SIGNATURE</fo:block>
            <fo:block keep-together="always" white-space-collapse="false">--------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            </fo:static-content>
            <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
                    <fo:block  font-size="10pt">
                        
                              <fo:table width="100%" table-layout="fixed" space-after="0.0in">
                                  <fo:table-column column-width="60pt"/>
                                  <fo:table-column column-width="55pt"/>
                                  <fo:table-column column-width="62pt"/>
                                  <fo:table-column column-width="67pt"/>
                                  <fo:table-column column-width="67pt"/>
                                  <fo:table-column column-width="67pt"/>
                                  <fo:table-column column-width="67pt"/>
                                  <fo:table-column column-width="67pt"/>
                                  <fo:table-column column-width="67pt"/>
                                  <fo:table-column column-width="67pt"/>
                                  <fo:table-column column-width="67pt"/>
                                  <fo:table-column column-width="67pt"/>
                                  <fo:table-column column-width="67pt"/>
                                      <fo:table-body> 
                                      <#list masterList as routeWiseDetail>
                             	<#assign vendorAbstractReportEntries = (routeWiseDetail).entrySet()>
                             	<#if vendorAbstractReportEntries?has_content>
                               <#list vendorAbstractReportEntries as tempVendorAbstractReportEntrie>
                                <#assign vendorAbstractReportList=tempVendorAbstractReportEntrie.getValue() >
                                <#if tempVendorAbstractReportEntrie.getKey()  !=  "GrandTotal">
                                <#assign vendorAbstractFacilityWise = (vendorAbstractReportList).entrySet()>
                                 <#list vendorAbstractFacilityWise as vendorAbstractFacility><#--<boothwise>-->
                                 <#assign boothWiseData = (vendorAbstractFacility).getValue()>
                                 <#if vendorAbstractFacility.getKey() == "RouteTotals">
                                  <#assign facility = delegator.findOne("Facility", {"facilityId" : vendorAbstractFacility.getKey()}, true)>
                                          <fo:table-row>
                                               <fo:table-cell>
                                               	<fo:block >${tempVendorAbstractReportEntrie.getKey()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	<fo:block text-align="right">${(boothWiseData.get("18")).toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	<fo:block text-align="right">${(boothWiseData.get("20")).toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	<fo:block text-align="right">${(boothWiseData.get("14")).toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	<fo:block text-align="right">${(boothWiseData.get("16")).toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	<fo:block text-align="right">${(boothWiseData.get("12")).toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	<fo:block text-align="right">${(boothWiseData.get("11")).toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	<fo:block text-align="right">${boothWiseData.get("TOTAL").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	<fo:block text-align="right">${boothWiseData.get("AVG").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	<fo:block text-align="right">${boothWiseData.get("MAINTENANCE").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	<fo:block text-align="right">${boothWiseData.get("DUES").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	<fo:block text-align="right">${boothWiseData.get("NET").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <#assign WMBulk = boothWiseData.get("19")>
                                               <#assign TMBulk = boothWiseData.get("15")>
                                               <#assign DTBulk = boothWiseData.get("13")>
                                          </fo:table-row>
                                          <#if WMBulk != 0 || TMBulk != 0 || DTBulk != 0>
                                                         <fo:table-row>
                                                            <fo:table-cell>
                            		                          <fo:block>BULK</fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell>
                            		                          <fo:block text-align="right">${WMBulk.toEngineeringString()}</fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell>
                            		                          <fo:block></fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell>
                            		                          <fo:block text-align="right">${TMBulk.toEngineeringString()}</fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell>
                            		                          <fo:block></fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell>
                            		                          <fo:block text-align="right">${DTBulk.toEngineeringString()}</fo:block>
                                                            </fo:table-cell>
                                                         </fo:table-row>
                                                         <#assign WMBulk = 0>
				                                         <#assign TMBulk = 0>
				                                         <#assign DTBulk = 0>
                                             </#if>
                                          <fo:table-row>
                                          <fo:table-cell>
                                               <fo:block keep-together="always" >--------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>	   	
                                          </fo:table-cell>
                                        </fo:table-row>
                                    
                                            </#if>
                                        </#list>
                                        <#else>
                                           <#assign grandTotalsList = tempVendorAbstractReportEntrie.getValue()>
                                           <fo:table-row>
                                               <fo:table-cell>
                                               	<fo:block>GRND TOT</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	<fo:block text-align="right">${vendorAbstractReportList.get("18").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	<fo:block text-align="right">${vendorAbstractReportList.get("20").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	<fo:block text-align="right">${vendorAbstractReportList.get("14").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	<fo:block text-align="right">${vendorAbstractReportList.get("16").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	<fo:block text-align="right">${vendorAbstractReportList.get("12").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	<fo:block text-align="right">${vendorAbstractReportList.get("11").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	<fo:block text-align="right">${vendorAbstractReportList.get("TOTAL").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	<fo:block text-align="right">${vendorAbstractReportList.get("AVG").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	<fo:block text-align="right">${vendorAbstractReportList.get("MAINTENANCE").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	<fo:block text-align="right">${vendorAbstractReportList.get("DUES").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	<fo:block text-align="right">${vendorAbstractReportList.get("NET").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <#assign WMBulk = vendorAbstractReportList.get("19")>
                                               <#assign TMBulk = vendorAbstractReportList.get("15")>
                                               <#assign DTBulk = vendorAbstractReportList.get("13")>
                                               
                                               
                                          </fo:table-row>
                                          <#if WMBulk != 0 || TMBulk != 0 || DTBulk != 0>
                                                         <fo:table-row>
                                                            <fo:table-cell>
                            		                          <fo:block>BULK</fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell>
                            		                          <fo:block text-align="right">${WMBulk.toEngineeringString()}</fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell>
                            		                          <fo:block></fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell>
                            		                          <fo:block text-align="right">${TMBulk.toEngineeringString()}</fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell>
                            		                          <fo:block></fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell>
                            		                          <fo:block text-align="right">${DTBulk.toEngineeringString()}</fo:block>
                                                            </fo:table-cell>
                                                         </fo:table-row>
                                                         <#assign WMBulk = 0>
				                                         <#assign TMBulk = 0>
				                                         <#assign DTBulk = 0>
                                             </#if>
                          
                                        </#if>
                                      </#list>    
                                      </#if>
                                      </#list> 
                                      </fo:table-body>
                              </fo:table> 
                            
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
           
         </fo:root>
</#escape>