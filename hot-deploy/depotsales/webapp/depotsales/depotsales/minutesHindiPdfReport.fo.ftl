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
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format" font-family="Courier,Mangal">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="8.69in" margin-left=".1in"  margin-right=".5in" margin-top=".2in" margin-bottom=".2in">
                <fo:region-body margin-top="1.2in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
       </fo:layout-master-set>
       ${setRequestAttribute("OUTPUT_FILENAME", "CenterWiseKgFatAcc.txt")}
       
       <#if OrderItemList?has_content>
       <fo:page-sequence master-reference="main" language="hi_IN">
       		<#assign locale= Static["org.ofbiz.base.util.UtilMisc"].parseLocale("hi_IN")>
            <fo:static-content flow-name="xsl-region-before" font-family="Courier,Mangal"> 
					<fo:block text-align="center" white-space-collapse="false" font-size="12pt" keep-together="always"></fo:block>
    				<fo:block text-align="center" white-space-collapse="false" font-size="12pt" keep-together="always">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "nhdcTitle", locale)}                                                 </fo:block>
    				<fo:block text-align="center" white-space-collapse="false" font-size="12pt" keep-together="always">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "vamsi", locale)}                                                 </fo:block>
    				<fo:block text-align="center" white-space-collapse="false" font-size="12pt" keep-together="always">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "minutes1", locale)}                                                 </fo:block>
    				
    				<#assign periodicReport = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", "Minute of Purchase and Sales Committee meeting for the purchase of following item(s).", "toScript", "devanagari")).get("result")/>
    				<fo:block text-align="center" white-space-collapse="false" font-size="12pt" keep-together="always">${periodicReport?if_exists}                                                 </fo:block>
    				<#assign periodicDate = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", "PERIODIC DATE", "toScript", "devanagari")).get("result")/>
    				<#assign fromDate = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy"), "toScript", "devanagari")).get("result")/>
    				<#assign thruDate = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy"), "toScript", "devanagari")).get("result")/>
    				<#assign reportDate = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yyyy"), "toScript", "devanagari")).get("result")/>
    				<fo:block text-align="left" white-space-collapse="false" font-size="12pt" keep-together="always">  ${periodicDate?if_exists} : ${fromDate?if_exists}  ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("ProcurementUiLabels", "ProcurementTo", locale)}  ${thruDate?if_exists}                                                  </fo:block>
        			<fo:block>&#160;</fo:block>
        			<#assign centerName = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("facilityName").toUpperCase())),20), "toScript", "devanagari")).get("result")/>
        			<#if centerFacilityId?has_content>
    				<#assign facility = delegator.findOne("Facility", {"facilityId" : centerFacilityId}, true)>
	            	<fo:block text-align="left" white-space-collapse="false" font-size="12pt" keep-together="always">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("ProcurementUiLabels", "ProcurementReport", locale)}${Static["org.ofbiz.base.util.UtilProperties"].getMessage("ProcurementUiLabels", "ProcurementDate", locale)}:	 ${reportDate?if_exists} 										${Static["org.ofbiz.base.util.UtilProperties"].getMessage("ProcurementUiLabels", "ProcurementCenterName", locale)}: ${centerName?if_exists}</fo:block>
		            <#else>						
        			<fo:block text-align="left" white-space-collapse="false" font-size="12pt" keep-together="always">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("ProcurementUiLabels", "ProcurementReport", locale)}${Static["org.ofbiz.base.util.UtilProperties"].getMessage("ProcurementUiLabels", "ProcurementDate", locale)}: ${reportDate?if_exists}												${Static["org.ofbiz.base.util.UtilProperties"].getMessage("ProcurementUiLabels", "ProcurementCenterName", locale)}                                                 </fo:block>
            		</#if>
            		<fo:block>&#160;</fo:block>
            </fo:static-content>
            <fo:flow flow-name="xsl-region-body" font-family="Courier,Mangal">
                <fo:block keep-together="always">
                	
                    <fo:table border-style="solid" >
	    					<fo:table-column column-width="30pt"/>
							<fo:table-column column-width="85pt"/>
							<fo:table-column column-width="50pt"/>
							<fo:table-column column-width="100pt"/>
							<fo:table-column column-width="100pt"/>
							<fo:table-column column-width="100pt"/>
							<fo:table-column column-width="100pt"/>
           					<fo:table-body>
           					<fo:table-row>
                                <fo:table-cell border-style="solid">
                                	<fo:block>&#160;</fo:block>
                                    <fo:block text-align = "left" font-size="12pt">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("ProcurementUiLabels", "ProcurementSNO", locale)}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-style="solid">
                                	<fo:block>&#160;</fo:block>
                                    <fo:block text-align = "left" font-size="12pt">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("ProcurementUiLabels", "ProcurementDate", locale)}<#--${Static["org.ofbiz.order.order.OrderServices"].getDevanagari("DATE")}--></fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-style="solid">
                                	<fo:block>&#160;</fo:block>
                                    <fo:block text-align = "left" font-size="12pt">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("ProcurementUiLabels", "ProcurementShift", locale)}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-style="solid">
                                	<fo:block>&#160;</fo:block>
                                    <fo:block text-align = "left" font-size="12pt">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("ProcurementUiLabels", "ProcurementMilk", locale)}(${Static["org.ofbiz.base.util.UtilProperties"].getMessage("ProcurementUiLabels", "ProcurementKg", locale)})</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-style="solid">
                                	<fo:block>&#160;</fo:block>
                                    <fo:block text-align = "left" font-size="12pt">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("ProcurementUiLabels", "ProcurementMilk", locale)}(${Static["org.ofbiz.base.util.UtilProperties"].getMessage("ProcurementUiLabels", "ProcurementLtr", locale)})</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-style="solid">
                                	<fo:block>&#160;</fo:block>
                                    <fo:block text-align = "right" font-size="12pt">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("ProcurementUiLabels", "ProcurementAvgLtrFat", locale)}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-style="solid">
                                	<fo:block>&#160;</fo:block>
                                    <fo:block text-align = "right" font-size="12pt">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("ProcurementUiLabels", "ProcurementAvgLtr", locale)}</fo:block>
                                </fo:table-cell>
           					</fo:table-row>
           			   <#assign srno = 0>	
                       <#list dayWiseEntriesList as dayWiseEntry>
                        	<#assign srno = srno + 1>
                        	    				<#assign periodicDate = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", "PERIODIC DATE", "toScript", "devanagari")).get("result")/>
                        		
                            <fo:table-row>
                                <fo:table-cell border-style="solid">
                                    <fo:block text-align = "left" font-size="12pt">${srno}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-style="solid">
                                	<fo:block>&#160;</fo:block>
                                    <fo:block text-align = "left" font-size="12pt">${dayWiseEntry.date?if_exists}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-style="solid">
                                	<fo:block>&#160;</fo:block>
                                    <fo:block text-align = "left" font-size="12pt">${dayWiseEntry.day}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-style="solid">
                                	<fo:block>&#160;</fo:block>
                                    <fo:block text-align = "right" font-size="12pt">${dayWiseEntry.qtyKgs?if_exists}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-style="solid">
                                	<fo:block>&#160;</fo:block>
                                    <fo:block text-align = "right" font-size="12pt">${dayWiseEntry.qtyLtrs?if_exists}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-style="solid">
                                	<fo:block>&#160;</fo:block>
                                    <fo:block text-align = "right" font-size="12pt">${dayWiseEntry.fat?if_exists}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-style="solid">
                                	<fo:block>&#160;</fo:block>
                                    <fo:block text-align = "right" font-size="12pt">${dayWiseEntry.snf?if_exists}</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            </#list>
                            <fo:table-row>
                                <fo:table-cell border-style="solid"></fo:table-cell>
                                <fo:table-cell border-style="solid"> <fo:block text-align = "right" font-size="12pt"></fo:block></fo:table-cell>
                                <fo:table-cell border-style="solid">
                                	<fo:block>&#160;</fo:block>
                                	<fo:block text-align = "left" font-size="12pt" keep-together="always" text-indent="10pt">${uiLabelMap.ProcurementTotal}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-style="solid">
                                	<fo:block>&#160;</fo:block>
                                    <fo:block text-align = "right" font-size="12pt">${grandTotals.qtyLtrs?if_exists}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-style="solid">
                                	<fo:block>&#160;</fo:block>
                                    <fo:block text-align = "right" font-size="12pt">${grandTotals.qtyKgs?if_exists}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-style="solid">
                                	<fo:block>&#160;</fo:block>
                                    <fo:block text-align = "right" font-size="12pt">${grandTotals.fat?if_exists}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-style="solid">
                                	<fo:block>&#160;</fo:block>
                                    <fo:block text-align = "right" font-size="12pt">${grandTotals.snf?if_exists}</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>    
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