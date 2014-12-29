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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-left="1in"  margin-right=".5in" margin-top=".2in" margin-bottom=".2in">
                <fo:region-body margin-top="1.6in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
       </fo:layout-master-set>
       ${setRequestAttribute("OUTPUT_FILENAME", "DepartmentTotalReport.txt")}
		<#if payRollSummaryMap?has_content>
       <fo:page-sequence master-reference="main">
            <fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-weight="bold">
            	<fo:block text-align="left" white-space-collapse="false" font-size="7pt">&#160;                                                  ANDHRA PRADESH DAIRY DEVELOPMENT CO-OP, FEDERATION LIMITED</fo:block>
                <#assign shedCode = delegator.findOne("PartyGroup", {"partyId" : ShedId}, true)>
                <fo:block text-align="left" white-space-collapse="false" font-size="7pt">&#160;                           DEPARTMENT TOTALS FOR ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodEnd?if_exists, "MMMMM, yyyy"))?upper_case}             SHED CODE AND NAME : <#if shedCode?has_content>${shedCode.groupName?upper_case?if_exists}</#if></fo:block>
                <fo:block font-size="7pt">--------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                 <fo:block font-size="7pt">
                    <fo:table>
                    	<fo:table-column column-width="30pt"/>
                        <fo:table-column column-width="72pt"/>
                        <fo:table-column column-width="72pt"/>
                        <fo:table-column column-width="72pt"/>
                        <fo:table-column column-width="72pt"/>
                        <fo:table-column column-width="72pt"/>
                        <fo:table-column column-width="72pt"/>
                        <fo:table-column column-width="72pt"/>
                        <fo:table-column column-width="72pt"/>
	                        <fo:table-body>
		                        <fo:table-row><fo:table-cell><fo:block keep-together="always" text-align="right">DEPT</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">PAY</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">RISK ALW</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">SUPP.ARRS</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">EPF</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">WATER</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">CONV.ADV</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">TOT.DEDNS</fo:block></fo:table-cell> </fo:table-row>
		                        <fo:table-row><fo:table-cell><fo:block keep-together="always" text-align="right"></fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">SPL.PAY</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">OT ALW</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">MISC.I</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">GPF</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">IT</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">MRG.LN</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">NET PAY</fo:block></fo:table-cell> </fo:table-row>
		                        <fo:table-row><fo:table-cell><fo:block keep-together="always" text-align="right"></fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">PNL.PAY</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">HD ALW</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">MISC.II</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">VOL.PF</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">COURT</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">MISC.APP</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">RND.NET PAY</fo:block></fo:table-cell> </fo:table-row>
		                        <fo:table-row><fo:table-cell><fo:block keep-together="always" text-align="right"></fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">FPP</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">OP ALW</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">MISC.III</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">GPF.LN</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">ELECT</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">DEPT.DUES</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right"> </fo:block></fo:table-cell> </fo:table-row>
		                        <fo:table-row><fo:table-cell><fo:block keep-together="always" text-align="right"></fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">DA</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">IC ALW</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">GROSS</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">EPF.RFND</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">PAY.ADV</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">WEL.FUND</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right"></fo:block></fo:table-cell> </fo:table-row>
		                        <fo:table-row><fo:table-cell><fo:block keep-together="always" text-align="right"></fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">HRA</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">CONV.ALW</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right"></fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">ESIC</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">TOUR.ADV</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">BANK LOANS</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right"></fo:block></fo:table-cell> </fo:table-row>
		                        <fo:table-row><fo:table-cell><fo:block keep-together="always" text-align="right"></fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">CCA</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">MED.ALW</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right"></fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">GIS</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">FEST.ADV</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">MILK CARDS</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right"></fo:block></fo:table-cell> </fo:table-row>
		                        <fo:table-row><fo:table-cell><fo:block keep-together="always" text-align="right"></fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">IR</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">SPL.ALW</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right"></fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">P.TAX</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">EDN.ADV</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">MILK DUES</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right"></fo:block></fo:table-cell> </fo:table-row>
		                        <fo:table-row><fo:table-cell><fo:block keep-together="always" text-align="right"></fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">ND.ALW</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">WASH.ALW</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right"></fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">SSS</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">MED.ADV</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">APGLIF AND LOAN</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right"></fo:block></fo:table-cell> </fo:table-row>
		                        <fo:table-row><fo:table-cell><fo:block keep-together="always" text-align="right"></fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">CB.ALW</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">EXTV.ALW</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right"></fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">HRR</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right">DPT.HB LN</fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right"></fo:block></fo:table-cell> <fo:table-cell><fo:block keep-together="always" text-align="right"></fo:block></fo:table-cell> </fo:table-row>
	                        </fo:table-body>
                    </fo:table>
                 </fo:block>
                 <fo:block font-size="7pt">--------------------------------------------------------------------------------------------------------------------------------------</fo:block>
             </fo:static-content> 
            <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
                 <fo:block font-size="7pt">
                 <#assign TOTAL_BEN_SALARY = 0>
                 <#assign TOTAL_BEN_RISKALW = 0>
                 <#assign TOTAL_BEN_SUPARRS = 0>
                 <#assign TOTAL_DD_EPF = 0>
                 <#assign TOTAL_DD_WATER = 0>
                 <#assign TOTAL_DD_CONADV  = 0>
                 <#assign TOTAL_DEDUCTION = 0>
                 
                 <#assign TOTAL_BEN_SPLPAY = 0>
                 <#assign TOTAL_BEN_OTALW = 0>
                 <#assign TOTAL_BEN_MISCONE = 0>
                 <#assign TOTAL_DD_GPF = 0>
                 <#assign TOTAL_DD_IT = 0>
                 <#assign TOTAL_DD_MRGLN = 0>
                 <#assign TOTAL_NET_AMOUNT = 0>
                 
                 <#assign TOTAL_BEN_PNLPAY = 0>
                 <#assign TOTAL_BEN_HDALW = 0>
                 <#assign TOTAL_BEN_MISCTWO = 0>
                 <#assign TOTAL_DD_VOLPF = 0>
                 <#assign TOTAL_DD_COURT = 0>
                 <#assign TOTAL_DD_MISAPP = 0>
                 <#assign TOTAL_RND_AMOUNT = 0>
                 
                 <#assign TOTAL_BEN_FPP = 0>
                 <#assign TOTAL_BEN_OPALW = 0>
                 <#assign TOTAL_BEN_MISCTHREE = 0>
                 <#assign TOTAL_DD_GPFLN  = 0>
                 <#assign TOTAL_DD_ELECT = 0>
                 <#assign TOTAL_DD_DPTDUES = 0>
                 
                 <#assign TOTAL_BEN_DA = 0>
                 <#assign TOTAL_BEN_ICALW = 0>
                 <#assign TOTAL_EARNINGS = 0>
                 <#assign TOTAL_DD_EPFREF = 0>
                 <#assign TOTAL_DD_PAYADV = 0>
                 <#assign TOTAL_DD_WELFARE = 0>
                 
                 <#assign TOTAL_BEN_HRA = 0>
                 <#assign TOTAL_BEN_CONALW = 0>
                 <#assign TOTAL_DD_ESI = 0>
                 <#assign TOTAL_DD_TOURADV = 0>
                 <#assign TOTAL_LOAN_AMOUNT = 0>
                            
                 <#assign TOTAL_BEN_CCA = 0>
                 <#assign TOTAL_BEN_MEDALW = 0>
                 <#assign TOTAL_DD_GIS = 0>
                 <#assign TOTAL_DD_FESTADV = 0>
                 <#assign TOTAL_DD_MILKCARDS = 0>
                            
 				 <#assign TOTAL_BEN_IR = 0>
                 <#assign TOTAL_BEN_SPLALW = 0>
                 <#assign TOTAL_DD_PTAX = 0>
                 <#assign TOTAL_DD_EDNADV = 0>
                 <#assign TOTAL_DD_MILKDUES = 0>                            
                            
                  <#assign TOTAL_BEN_NDALW = 0>
                 <#assign TOTAL_BEN_WASHALW= 0>
                 <#assign TOTAL_DD_SSS = 0>
                 <#assign TOTAL_DD_MEDADV = 0>
                 <#assign TOTAL_DD_APGLIF  = 0>
                            
                 <#assign TOTAL_BEN_CBALW = 0>
     			 <#assign TOTAL_BEN_EXTALW  = 0>
                 <#assign TOTAL_DD_HRR = 0>
                 <#assign TOTAL_DD_DPTHB = 0>
                 <#assign count=0>
                 <#assign DeptTotals=payRollSummaryMap.entrySet()>
                 <#list DeptTotals as deptTotals>
                    <fo:table >
                        <fo:table-column column-width="30pt"/>
                        <fo:table-column column-width="72pt"/>
                        <fo:table-column column-width="72pt"/>
                        <fo:table-column column-width="72pt"/>
                        <fo:table-column column-width="72pt"/>
                        <fo:table-column column-width="72pt"/>
                        <fo:table-column column-width="72pt"/>
                        <fo:table-column column-width="72pt"/>
                        <fo:table-column column-width="72pt"/>
                        <fo:table-body>
	                        <#assign count=count+1>
	                        <#if count==5>
	                        <fo:table-row>
	                        	<fo:table-cell>
	                        		 <fo:block page-break-before="always" font-size="7pt">--------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	                        	</fo:table-cell>
	                        </fo:table-row>
	                        <#assign count=0>
	                        <#else>
	                        <fo:table-row>
	                        	<fo:table-cell>
	                        		 <fo:block font-size="7pt">--------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	                        	</fo:table-cell>
	                        </fo:table-row>
	                        </#if>
	                            <fo:table-row>
	                                <fo:table-cell>
	                                <fo:block keep-together="always" text-align="right">${unitIdMap.get(deptTotals.getKey())}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_BEN_SALARY=0>
	                                <#if deptTotals.getValue().get("PAYROL_BEN_SALARY")?has_content>
	                                <#assign PAYROL_BEN_SALARY=deptTotals.getValue().get("PAYROL_BEN_SALARY")>
	                                </#if>
	                                 <#assign TOTAL_BEN_SALARY=TOTAL_BEN_SALARY + PAYROL_BEN_SALARY>
	                                  <fo:block keep-together="always" text-align="right">${PAYROL_BEN_SALARY?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_BEN_RISKALW=0>
	                                <#if deptTotals.getValue().get("PAYROL_BEN_RISKALW")?has_content>
	                                <#assign PAYROL_BEN_RISKALW=deptTotals.getValue().get("PAYROL_BEN_RISKALW")>
	                                </#if>
	                                <#assign TOTAL_BEN_RISKALW = TOTAL_BEN_RISKALW + PAYROL_BEN_RISKALW>
	                                    <fo:block keep-together="always" text-align="right">${PAYROL_BEN_RISKALW?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_BEN_SUPARRS=0>
	                                <#if deptTotals.getValue().get("PAYROL_BEN_SUPARRS")?has_content>
	                                <#assign PAYROL_BEN_SUPARRS=deptTotals.getValue().get("PAYROL_BEN_SUPARRS")>
	                                </#if>
	                                <#assign TOTAL_BEN_SUPARRS = TOTAL_BEN_SUPARRS + PAYROL_BEN_SUPARRS>
	                                    <fo:block keep-together="always" text-align="right">${PAYROL_BEN_SUPARRS?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_DD_EPF=0>
	                                <#if deptTotals.getValue().get("PAYROL_DD_EPF")?has_content>
	                                <#assign PAYROL_DD_EPF=deptTotals.getValue().get("PAYROL_DD_EPF")>
	                                </#if>
	                                <#assign TOTAL_DD_EPF = TOTAL_DD_EPF + PAYROL_DD_EPF>
	                                    <fo:block keep-together="always" text-align="right">${((-1)*PAYROL_DD_EPF)?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_DD_WATER=0>
	                                <#if deptTotals.getValue().get("PAYROL_DD_WATER")?has_content>
	                                <#assign PAYROL_DD_WATER=deptTotals.getValue().get("PAYROL_DD_WATER")>
	                                </#if>
	                                <#assign TOTAL_DD_WATER = TOTAL_DD_WATER + PAYROL_DD_WATER>
	                                    <fo:block keep-together="always" text-align="right">${((-1)*PAYROL_DD_WATER)?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_DD_CONADV=0>
	                                <#if deptTotals.getValue().get("PAYROL_DD_CONADV")?has_content>
	                                <#assign PAYROL_DD_CONADV=deptTotals.getValue().get("PAYROL_DD_CONADV")>
	                                </#if>
	                                 <#assign TOTAL_DD_CONADV =TOTAL_DD_CONADV + PAYROL_DD_CONADV>
	                                    <fo:block keep-together="always" text-align="right">${((-1)*PAYROL_DD_CONADV)?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#if finalMap.get(deptTotals.getKey())?has_content>
	                                <#assign totDeductions=finalMap.get(deptTotals.getKey()).get("totDeductions")>
	                                </#if>
	                                 <#assign TOTAL_DEDUCTION = TOTAL_DEDUCTION + totDeductions>
	                                    <fo:block keep-together="always" text-align="right">${((-1)*totDeductions)?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                            </fo:table-row>
	                            <fo:table-row>
	                                <fo:table-cell>
	                                <fo:block keep-together="always" text-align="right">${deptTotals.getKey()}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_BEN_SPLPAY=0>
	                                <#if deptTotals.getValue().get("PAYROL_BEN_SPLPAY")?has_content>
	                                <#assign PAYROL_BEN_SPLPAY=deptTotals.getValue().get("PAYROL_BEN_SPLPAY")>
	                                </#if>
	                                 <#assign TOTAL_BEN_SPLPAY = TOTAL_BEN_SPLPAY + PAYROL_BEN_SPLPAY>
	                                  <fo:block keep-together="always" text-align="right">${PAYROL_BEN_SPLPAY?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_BEN_OTALW=0>
	                                <#if deptTotals.getValue().get("PAYROL_BEN_OTALW")?has_content>
	                                <#assign PAYROL_BEN_OTALW=deptTotals.getValue().get("PAYROL_BEN_OTALW")>
	                                </#if>
	                                <#assign TOTAL_BEN_OTALW = TOTAL_BEN_OTALW + PAYROL_BEN_OTALW>
	                                    <fo:block keep-together="always" text-align="right">${PAYROL_BEN_OTALW?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_BEN_MISCONE=0>
	                                <#if deptTotals.getValue().get("PAYROL_BEN_MISCONE")?has_content>
	                                <#assign PAYROL_BEN_MISCONE=deptTotals.getValue().get("PAYROL_BEN_MISCONE")>
	                                </#if>
	                                 <#assign TOTAL_BEN_MISCONE = TOTAL_BEN_MISCONE + PAYROL_BEN_MISCONE>
	                                    <fo:block keep-together="always" text-align="right">${PAYROL_BEN_MISCONE?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_DD_GPF=0>
	                                <#if deptTotals.getValue().get("PAYROL_DD_GPF")?has_content>
	                                <#assign PAYROL_DD_GPF=deptTotals.getValue().get("PAYROL_DD_GPF")>
	                                </#if>
	                                <#assign TOTAL_DD_GPF = TOTAL_DD_GPF + PAYROL_DD_GPF>
	                                    <fo:block keep-together="always" text-align="right">${((-1)*PAYROL_DD_GPF)?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_DD_IT=0>
	                                <#if deptTotals.getValue().get("PAYROL_DD_IT")?has_content>
	                                <#assign PAYROL_DD_IT=deptTotals.getValue().get("PAYROL_DD_IT")>
	                                </#if>
	                                <#assign TOTAL_DD_IT =TOTAL_DD_IT + PAYROL_DD_IT>
	                                    <fo:block keep-together="always" text-align="right">${((-1)*PAYROL_DD_IT)?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_DD_MRGLN=0>
	                                <#if deptTotals.getValue().get("PAYROL_DD_MRGLN")?has_content>
	                                <#assign PAYROL_DD_MRGLN=deptTotals.getValue().get("PAYROL_DD_MRGLN")>
	                                </#if>
	                                <#assign TOTAL_DD_MRGLN =TOTAL_DD_MRGLN + PAYROL_DD_MRGLN>
	                                    <fo:block keep-together="always" text-align="right">${((-1)*PAYROL_DD_MRGLN)?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign netAmount=0>
	                                <#if finalMap.get(deptTotals.getKey())?has_content>
	                                <#assign netAmount=finalMap.get(deptTotals.getKey()).get("netAmount")>
	                                </#if>
	                                <#assign TOTAL_NET_AMOUNT =TOTAL_NET_AMOUNT + netAmount>
	                                    <fo:block keep-together="always" text-align="right">${netAmount?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                            </fo:table-row>
	                            <fo:table-row>
	                                <fo:table-cell>
	                                <fo:block keep-together="always" text-align="right"></fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_BEN_PNLPAY=0>
	                                <#if deptTotals.getValue().get("PAYROL_BEN_PNLPAY")?has_content>
	                                <#assign PAYROL_BEN_PNLPAY=deptTotals.getValue().get("PAYROL_BEN_PNLPAY")>
	                                </#if>
	                                <#assign TOTAL_BEN_PNLPAY = TOTAL_BEN_PNLPAY + PAYROL_BEN_PNLPAY>
	                                  <fo:block keep-together="always" text-align="right">${PAYROL_BEN_PNLPAY?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_BEN_HDALW=0>
	                                <#if deptTotals.getValue().get("PAYROL_BEN_HDALW")?has_content>
	                                <#assign PAYROL_BEN_HDALW=deptTotals.getValue().get("PAYROL_BEN_HDALW")>
	                                </#if>
	                                <#assign TOTAL_BEN_HDALW = TOTAL_BEN_HDALW + PAYROL_BEN_HDALW >
	                                    <fo:block keep-together="always" text-align="right">${PAYROL_BEN_HDALW?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_BEN_MISCTWO=0>
	                                <#if deptTotals.getValue().get("PAYROL_BEN_MISCTWO")?has_content>
	                                <#assign PAYROL_BEN_MISCTWO=deptTotals.getValue().get("PAYROL_BEN_MISCTWO")>
	                                </#if>
	                                <#assign TOTAL_BEN_MISCTWO = TOTAL_BEN_MISCTWO + PAYROL_BEN_MISCTWO>
	                                    <fo:block keep-together="always" text-align="right">${PAYROL_BEN_MISCTWO?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_DD_VOLPF=0>
	                                <#if deptTotals.getValue().get("PAYROL_DD_VOLPF")?has_content>
	                                <#assign PAYROL_DD_VOLPF=deptTotals.getValue().get("PAYROL_DD_VOLPF")>
	                                </#if>
	                                <#assign TOTAL_DD_VOLPF = TOTAL_DD_VOLPF + PAYROL_DD_VOLPF>
	                                    <fo:block keep-together="always" text-align="right">${((-1)*PAYROL_DD_VOLPF)?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_DD_COURT=0>
	                                <#if deptTotals.getValue().get("PAYROL_DD_COURT")?has_content>
	                                <#assign PAYROL_DD_COURT=deptTotals.getValue().get("PAYROL_DD_COURT")>
	                                </#if>
	                             	<#assign TOTAL_DD_COURT = TOTAL_DD_COURT + PAYROL_DD_COURT>
	                                    <fo:block keep-together="always" text-align="right">${((-1)*PAYROL_DD_COURT)?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_DD_MISAPP=0>
	                                <#if deptTotals.getValue().get("PAYROL_DD_MISAPP")?has_content>
	                                <#assign PAYROL_DD_MISAPP=deptTotals.getValue().get("PAYROL_DD_MISAPP")>
	                                </#if>
	                                 <#assign TOTAL_DD_MISAPP = TOTAL_DD_MISAPP + PAYROL_DD_MISAPP>
	                                    <fo:block keep-together="always" text-align="right">${((-1)*PAYROL_DD_MISAPP)?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign rndNetAmt=0>
	                                <#if finalMap.get(deptTotals.getKey())?has_content>
	                                <#assign rndNetAmt=finalMap.get(deptTotals.getKey()).get("rndNetAmt")>
	                                </#if>
	                                 <#assign TOTAL_RND_AMOUNT = TOTAL_RND_AMOUNT + rndNetAmt>
	                                    <fo:block keep-together="always" text-align="right">${rndNetAmt?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                            </fo:table-row>
	                            <fo:table-row>
	                                <fo:table-cell>
	                                <fo:block keep-together="always" text-align="right"></fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_BEN_FPP=0>
	                                <#if deptTotals.getValue().get("PAYROL_BEN_FPP")?has_content>
	                                <#assign PAYROL_BEN_FPP=deptTotals.getValue().get("PAYROL_BEN_FPP")>
	                                </#if>
	                                <#assign TOTAL_BEN_FPP = TOTAL_BEN_FPP + PAYROL_BEN_FPP>
	                                  <fo:block keep-together="always" text-align="right">${PAYROL_BEN_FPP?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_BEN_OPALW=0>
	                                <#if deptTotals.getValue().get("PAYROL_BEN_OPALW")?has_content>
	                                <#assign PAYROL_BEN_OPALW=deptTotals.getValue().get("PAYROL_BEN_OPALW")>
	                                </#if>
	                                 <#assign TOTAL_BEN_OPALW = TOTAL_BEN_OPALW + PAYROL_BEN_OPALW>
	                                    <fo:block keep-together="always" text-align="right">${PAYROL_BEN_OPALW?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_BEN_MISCTHREE=0>
	                                <#if deptTotals.getValue().get("PAYROL_BEN_MISCTHREE")?has_content>
	                                <#assign PAYROL_BEN_MISCTHREE=deptTotals.getValue().get("PAYROL_BEN_MISCTHREE")>
	                                </#if>
	                                 <#assign TOTAL_BEN_MISCTHREE = TOTAL_BEN_MISCTHREE + PAYROL_BEN_MISCTHREE>
	                                    <fo:block keep-together="always" text-align="right">${PAYROL_BEN_MISCTHREE?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_DD_GPFLN=0>
	                                <#if deptTotals.getValue().get("PAYROL_DD_GPFLN")?has_content>
	                                <#assign PAYROL_DD_GPFLN=deptTotals.getValue().get("PAYROL_DD_GPFLN")>
	                                </#if>
	                                 <#assign TOTAL_DD_GPFLN = TOTAL_DD_GPFLN + PAYROL_DD_GPFLN>
	                                    <fo:block keep-together="always" text-align="right">${((-1)*PAYROL_DD_GPFLN)?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_DD_ELECT=0>
	                                <#if deptTotals.getValue().get("PAYROL_DD_ELECT")?has_content>
	                                <#assign PAYROL_DD_ELECT=deptTotals.getValue().get("PAYROL_DD_ELECT")>
	                                </#if>
	                                 <#assign TOTAL_DD_ELECT = TOTAL_DD_ELECT + PAYROL_DD_ELECT>
	                                    <fo:block keep-together="always" text-align="right">${((-1)*PAYROL_DD_ELECT)?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_DD_DPTDUES=0>
	                                <#if deptTotals.getValue().get("PAYROL_DD_DPTDUES")?has_content>
	                                <#assign PAYROL_DD_DPTDUES=deptTotals.getValue().get("PAYROL_DD_DPTDUES")>
	                                </#if>
	                                 <#assign TOTAL_DD_DPTDUES = TOTAL_DD_DPTDUES + PAYROL_DD_DPTDUES>
	                                    <fo:block keep-together="always" text-align="right">${((-1)*PAYROL_DD_DPTDUES)?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                    <fo:block keep-together="always" text-align="right"></fo:block>
	                                </fo:table-cell>
	                            </fo:table-row>
	                            <fo:table-row>
	                                <fo:table-cell>
	                                <fo:block keep-together="always" text-align="right"></fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_BEN_DA=0>
	                                <#if deptTotals.getValue().get("PAYROL_BEN_DA")?has_content>
	                                <#assign PAYROL_BEN_DA=deptTotals.getValue().get("PAYROL_BEN_DA")>
	                                </#if>
	                                 <#assign TOTAL_BEN_DA  = TOTAL_BEN_DA  + PAYROL_BEN_DA>
	                                  <fo:block keep-together="always" text-align="right">${PAYROL_BEN_DA?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_BEN_ICALW=0>
	                                <#if deptTotals.getValue().get("PAYROL_BEN_ICALW")?has_content>
	                                <#assign PAYROL_BEN_ICALW=deptTotals.getValue().get("PAYROL_BEN_ICALW")>
	                                </#if>
	                                <#assign TOTAL_BEN_ICALW = TOTAL_BEN_ICALW + PAYROL_BEN_ICALW>
	                                    <fo:block keep-together="always" text-align="right">${PAYROL_BEN_ICALW?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign totEarnings=0>
	                                <#if finalMap.get(deptTotals.getKey())?has_content>
	                                <#assign totEarnings=finalMap.get(deptTotals.getKey()).get("totEarnings")>
	                                </#if>
	                                 <#assign TOTAL_EARNINGS = TOTAL_EARNINGS + totEarnings>
	                                    <fo:block keep-together="always" text-align="right">${(totEarnings)?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_DD_EPFREF=0>
	                                <#if deptTotals.getValue().get("PAYROL_DD_EPFREF")?has_content>
	                                <#assign PAYROL_DD_EPFREF=deptTotals.getValue().get("PAYROL_DD_EPFREF")>
	                                </#if>
	                                 <#assign TOTAL_DD_EPFREF = TOTAL_DD_EPFREF + PAYROL_DD_EPFREF>
	                                    <fo:block keep-together="always" text-align="right">${((-1)*PAYROL_DD_EPFREF)?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_DD_PAYADV=0>
	                                <#if deptTotals.getValue().get("PAYROL_DD_PAYADV")?has_content>
	                                <#assign PAYROL_DD_PAYADV=deptTotals.getValue().get("PAYROL_DD_PAYADV")>
	                                </#if>
	                                <#assign TOTAL_DD_PAYADV = TOTAL_DD_PAYADV + PAYROL_DD_PAYADV>
	                                    <fo:block keep-together="always" text-align="right">${((-1)*PAYROL_DD_PAYADV)?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_DD_WELFARE=0>
	                                <#if deptTotals.getValue().get("PAYROL_DD_WELFARE")?has_content>
	                                <#assign PAYROL_DD_WELFARE=deptTotals.getValue().get("PAYROL_DD_WELFARE")>
	                                </#if>
	                                <#assign TOTAL_DD_WELFARE  = TOTAL_DD_WELFARE + PAYROL_DD_WELFARE>
	                                    <fo:block keep-together="always" text-align="right">${((-1)*PAYROL_DD_WELFARE)?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                    <fo:block keep-together="always" text-align="right"></fo:block>
	                                </fo:table-cell>
	                            </fo:table-row>
	                            <fo:table-row>
	                                <fo:table-cell>
	                                <fo:block keep-together="always" text-align="right"></fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_BEN_HRA=0>
	                                <#if deptTotals.getValue().get("PAYROL_BEN_HRA")?has_content>
	                                <#assign PAYROL_BEN_HRA=deptTotals.getValue().get("PAYROL_BEN_HRA")>
	                                </#if>
	                                 <#assign TOTAL_BEN_HRA  = TOTAL_BEN_HRA + PAYROL_BEN_HRA>
	                                  <fo:block keep-together="always" text-align="right">${PAYROL_BEN_HRA?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_BEN_CONALW=0>
	                                <#if deptTotals.getValue().get("PAYROL_BEN_CONALW")?has_content>
	                                <#assign PAYROL_BEN_CONALW=deptTotals.getValue().get("PAYROL_BEN_CONALW")>
	                                </#if>
	                                <#assign TOTAL_BEN_CONALW  = TOTAL_BEN_CONALW + PAYROL_BEN_CONALW>
	                                    <fo:block keep-together="always" text-align="right">${PAYROL_BEN_CONALW?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                    <fo:block keep-together="always" text-align="right"></fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_DD_ESI=0>
	                                <#if deptTotals.getValue().get("PAYROL_DD_ESI")?has_content>
	                                <#assign PAYROL_DD_ESI=deptTotals.getValue().get("PAYROL_DD_ESI")>
	                                </#if>
	                                <#assign TOTAL_DD_ESI  = TOTAL_DD_ESI + PAYROL_DD_ESI>
	                                    <fo:block keep-together="always" text-align="right">${((-1)*PAYROL_DD_ESI)?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_DD_TOURADV=0>
	                                <#if deptTotals.getValue().get("PAYROL_DD_TOURADV")?has_content>
	                                <#assign PAYROL_DD_TOURADV=deptTotals.getValue().get("PAYROL_DD_TOURADV")>
	                                </#if>
	                                <#assign TOTAL_DD_TOURADV  = TOTAL_DD_TOURADV + PAYROL_DD_TOURADV>
	                                    <fo:block keep-together="always" text-align="right">${((-1)*PAYROL_DD_TOURADV)?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign loanAmount=0>
	                                <#if finalMap.get(deptTotals.getKey())?has_content>
	                                <#assign loanAmount=finalMap.get(deptTotals.getKey()).get("loanAmount")>
	                                </#if>
	                                <#assign TOTAL_LOAN_AMOUNT  = TOTAL_LOAN_AMOUNT + loanAmount>
	                                    <fo:block keep-together="always" text-align="right">${((-1)*loanAmount)?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                    <fo:block keep-together="always" text-align="right"></fo:block>
	                                </fo:table-cell>
	                            </fo:table-row>
	                            <fo:table-row>
	                                <fo:table-cell>
	                                <fo:block keep-together="always" text-align="right"></fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_BEN_CCA=0>
	                                <#if deptTotals.getValue().get("PAYROL_BEN_CCA")?has_content>
	                                <#assign PAYROL_BEN_CCA=deptTotals.getValue().get("PAYROL_BEN_CCA")>
	                                </#if>
	                                 <#assign TOTAL_BEN_CCA = TOTAL_BEN_CCA + PAYROL_BEN_CCA>
	                                  <fo:block keep-together="always" text-align="right">${PAYROL_BEN_CCA?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_BEN_MEDALW=0>
	                                <#if deptTotals.getValue().get("PAYROL_BEN_MEDALW")?has_content>
	                                <#assign PAYROL_BEN_MEDALW=deptTotals.getValue().get("PAYROL_BEN_MEDALW")>
	                                </#if>
	                                 <#assign TOTAL_BEN_MEDALW  = TOTAL_BEN_MEDALW + PAYROL_BEN_MEDALW>
	                                    <fo:block keep-together="always" text-align="right">${PAYROL_BEN_MEDALW?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                    <fo:block keep-together="always" text-align="right"></fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_DD_GIS=0>
	                                <#if deptTotals.getValue().get("PAYROL_DD_GIS")?has_content>
	                                <#assign PAYROL_DD_GIS=deptTotals.getValue().get("PAYROL_DD_GIS")>
	                                </#if>
	                                 <#assign TOTAL_DD_GIS  = TOTAL_DD_GIS + PAYROL_DD_GIS>
	                                    <fo:block keep-together="always" text-align="right">${((-1)*PAYROL_DD_GIS)?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_DD_FESTADV=0>
	                                <#if deptTotals.getValue().get("PAYROL_DD_FESTADV")?has_content>
	                                <#assign PAYROL_DD_FESTADV=deptTotals.getValue().get("PAYROL_DD_FESTADV")>
	                                </#if>
	                                 <#assign TOTAL_DD_FESTADV  = TOTAL_DD_FESTADV  + PAYROL_DD_FESTADV>
	                                    <fo:block keep-together="always" text-align="right">${((-1)*PAYROL_DD_FESTADV)?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_DD_MILKCARDS=0>
	                                <#if deptTotals.getValue().get("PAYROL_DD_MILKCARDS")?has_content>
	                                <#assign PAYROL_DD_MILKCARDS=deptTotals.getValue().get("PAYROL_DD_MILKCARDS")>
	                                </#if>
	                                 <#assign TOTAL_DD_MILKCARDS  = TOTAL_DD_MILKCARDS + PAYROL_DD_MILKCARDS>
	                                    <fo:block keep-together="always" text-align="right">${((-1)*PAYROL_DD_MILKCARDS)?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                    <fo:block keep-together="always" text-align="right"></fo:block>
	                                </fo:table-cell>
	                            </fo:table-row>
	                            <fo:table-row>
	                                <fo:table-cell>
	                                <fo:block keep-together="always" text-align="right"></fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_BEN_IR=0>
	                                <#if deptTotals.getValue().get("PAYROL_BEN_IR")?has_content>
	                                <#assign PAYROL_BEN_IR=deptTotals.getValue().get("PAYROL_BEN_IR")>
	                                </#if>
	                                <#assign TOTAL_BEN_IR = TOTAL_BEN_IR + PAYROL_BEN_IR>
	                                  <fo:block keep-together="always" text-align="right">${PAYROL_BEN_IR?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_BEN_SPLALW=0>
	                                <#if deptTotals.getValue().get("PAYROL_BEN_SPLALW")?has_content>
	                                <#assign PAYROL_BEN_SPLALW=deptTotals.getValue().get("PAYROL_BEN_SPLALW")>
	                                </#if>
	                                <#assign TOTAL_DD_SPLALW  = TOTAL_DD_SPLALW + PAYROL_BEN_SPLALW>
	                                    <fo:block keep-together="always" text-align="right">${PAYROL_BEN_SPLALW?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                    <fo:block keep-together="always" text-align="right"></fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_DD_PTAX=0>
	                                <#if deptTotals.getValue().get("PAYROL_DD_PTAX")?has_content>
	                                <#assign PAYROL_DD_PTAX=deptTotals.getValue().get("PAYROL_DD_PTAX")>
	                                </#if>
	                                <#assign TOTAL_DD_PTAX = TOTAL_DD_PTAX + PAYROL_DD_PTAX>
	                                    <fo:block keep-together="always" text-align="right">${((-1)*PAYROL_DD_PTAX)?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_DD_EDNADV=0>
	                                <#if deptTotals.getValue().get("PAYROL_DD_EDNADV")?has_content>
	                                <#assign PAYROL_DD_EDNADV=deptTotals.getValue().get("PAYROL_DD_EDNADV")>
	                                </#if>
	                                <#assign TOTAL_DD_EDNADV  = TOTAL_DD_EDNADV  + PAYROL_DD_EDNADV>
	                                    <fo:block keep-together="always" text-align="right">${((-1)*PAYROL_DD_EDNADV)?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_DD_MILKDUES=0>
	                                <#if deptTotals.getValue().get("PAYROL_DD_MILKDUES")?has_content>
	                                <#assign PAYROL_DD_MILKDUES=deptTotals.getValue().get("PAYROL_DD_MILKDUES")>
	                                </#if>
	                                <#assign TOTAL_DD_MILKDUES  = PAYROL_DD_MILKDUES + PAYROL_DD_MILKDUES>
	                                    <fo:block keep-together="always" text-align="right">${((-1)*PAYROL_DD_MILKDUES)?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                    <fo:block keep-together="always" text-align="right"></fo:block>
	                                </fo:table-cell>
	                            </fo:table-row>
	                            <fo:table-row>
	                                <fo:table-cell>
	                                <fo:block keep-together="always" text-align="right"></fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_BEN_NDALW=0>
	                                <#if deptTotals.getValue().get("PAYROL_BEN_NDALW")?has_content>
	                                <#assign PAYROL_BEN_NDALW=deptTotals.getValue().get("PAYROL_BEN_NDALW")>
	                                </#if>
	                                 <#assign TOTAL_BEN_NDALW  = TOTAL_BEN_NDALW + PAYROL_BEN_NDALW>
	                                  <fo:block keep-together="always" text-align="right">${PAYROL_BEN_NDALW?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_BEN_WASHALW=0>
	                                <#if deptTotals.getValue().get("PAYROL_BEN_WASHALW")?has_content>
	                                <#assign PAYROL_BEN_WASHALW=deptTotals.getValue().get("PAYROL_BEN_WASHALW")>
	                                </#if>
	                                 <#assign TOTAL_BEN_WASHALW  = TOTAL_BEN_WASHALW + PAYROL_BEN_WASHALW>
	                                    <fo:block keep-together="always" text-align="right">${PAYROL_BEN_WASHALW?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                    <fo:block keep-together="always" text-align="right"></fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_DD_SSS=0>
	                                <#if deptTotals.getValue().get("PAYROL_DD_SSS")?has_content>
	                                <#assign PAYROL_DD_SSS=deptTotals.getValue().get("PAYROL_DD_SSS")>
	                                </#if>
	                                 <#assign TOTAL_DD_SSS  = TOTAL_DD_SSS + PAYROL_DD_SSS>
	                                    <fo:block keep-together="always" text-align="right">${((-1)*PAYROL_DD_SSS)?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_DD_MEDADV=0>
	                                <#if deptTotals.getValue().get("PAYROL_DD_MEDADV")?has_content>
	                                <#assign PAYROL_DD_MEDADV=deptTotals.getValue().get("PAYROL_DD_MEDADV")>
	                                </#if>
	                                 <#assign TOTAL_DD_MEDADV  = TOTAL_DD_MEDADV + PAYROL_DD_MEDADV>
	                                    <fo:block keep-together="always" text-align="right">${((-1)*PAYROL_DD_MEDADV)?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_DD_APGLIF=0>
	                                <#if deptTotals.getValue().get("PAYROL_DD_APGLIF")?has_content>
	                                <#assign PAYROL_DD_APGLIF=deptTotals.getValue().get("PAYROL_DD_APGLIF")>
	                                </#if>
	                                 <#assign TOTAL_DD_APGLIF  = TOTAL_DD_APGLIF + PAYROL_DD_APGLIF>
	                                    <fo:block keep-together="always" text-align="right">${((-1)*PAYROL_DD_APGLIF)?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                    <fo:block keep-together="always" text-align="right"></fo:block>
	                                </fo:table-cell>
	                            </fo:table-row>
	                            <fo:table-row>
	                                <fo:table-cell>
	                                <fo:block keep-together="always" text-align="right"></fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_BEN_CBALW=0>
	                                <#if deptTotals.getValue().get("PAYROL_BEN_CBALW")?has_content>
	                                <#assign PAYROL_BEN_CBALW=deptTotals.getValue().get("PAYROL_BEN_CBALW")>
	                                </#if>
	                                <#assign TOTAL_BEN_CBALW  = TOTAL_BEN_CBALW + PAYROL_BEN_CBALW>
	                                  <fo:block keep-together="always" text-align="right">${PAYROL_BEN_CBALW?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_BEN_EXTALW=0>
	                                <#if deptTotals.getValue().get("PAYROL_BEN_EXTALW")?has_content>
	                                <#assign PAYROL_BEN_EXTALW=deptTotals.getValue().get("PAYROL_BEN_EXTALW")>
	                                </#if>
	                                <#assign TOTAL_BEN_EXTALW  = TOTAL_BEN_EXTALW + PAYROL_BEN_EXTALW>
	                                    <fo:block keep-together="always" text-align="right">${PAYROL_BEN_EXTALW?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                    <fo:block keep-together="always" text-align="right"></fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_DD_HRR=0>
	                                <#if deptTotals.getValue().get("PAYROL_DD_HRR")?has_content>
	                                <#assign PAYROL_DD_HRR=deptTotals.getValue().get("PAYROL_DD_HRR")>
	                                </#if>
	                                <#assign TOTAL_DD_HRR = TOTAL_DD_HRR + PAYROL_DD_HRR>
	                                    <fo:block keep-together="always" text-align="right">${((-1)*PAYROL_DD_HRR)?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                <#assign PAYROL_DD_DPTHB=0>
	                                <#if deptTotals.getValue().get("PAYROL_DD_DPTHB")?has_content>
	                                <#assign PAYROL_DD_DPTHB=deptTotals.getValue().get("PAYROL_DD_DPTHB")>
	                                </#if>
	                                <#assign TOTAL_DD_DPTHB  = TOTAL_DD_DPTHB + PAYROL_DD_DPTHB>
	                                    <fo:block keep-together="always" text-align="right">${((-1)*PAYROL_DD_DPTHB)?if_exists?string("##0.00")}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                    <fo:block keep-together="always" text-align="right"></fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell>
	                                    <fo:block keep-together="always" text-align="right"></fo:block>
	                                </fo:table-cell>
	                            </fo:table-row>
	                            <fo:table-row>
	                        	<fo:table-cell>
	                        		 <fo:block font-size="7pt">--------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	                        	</fo:table-cell>
	                        </fo:table-row>
                        </fo:table-body>                        
                    </fo:table>  
                    </#list>  
                </fo:block>
                <fo:block font-size="7pt">
                    <fo:table >
                        <fo:table-column column-width="30pt"/>
                        <fo:table-column column-width="72pt"/>
                        <fo:table-column column-width="72pt"/>
                        <fo:table-column column-width="72pt"/>
                        <fo:table-column column-width="72pt"/>
                        <fo:table-column column-width="72pt"/>
                        <fo:table-column column-width="72pt"/>
                        <fo:table-column column-width="72pt"/>
                        <fo:table-column column-width="72pt"/>
                        <fo:table-body>
	                        <fo:table-row>
	                        	<fo:table-cell>
	                        		 <fo:block font-size="7pt">--------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	                        	</fo:table-cell>
	                        </fo:table-row>
	                        <fo:table-row>
	                        	<fo:table-cell>
	                        		  <fo:block keep-together="always" text-align="left">GrandTotals</fo:block>
	                        	</fo:table-cell>
	                            <fo:table-cell>
	                        		  <fo:block keep-together="always" text-align="right">${TOTAL_BEN_SALARY?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell>
	                        		  <fo:block keep-together="always" text-align="right">${TOTAL_BEN_RISKALW?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell>
	                        		  <fo:block keep-together="always" text-align="right">${TOTAL_BEN_SUPARRS?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell>
	                        		  <fo:block keep-together="always" text-align="right">${((-1)*TOTAL_DD_EPF)?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell>
	                        		  <fo:block keep-together="always" text-align="right">${((-1)*TOTAL_DD_WATER)?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell>
	                        		  <fo:block keep-together="always" text-align="right">${((-1)*TOTAL_DD_CONADV)?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell>
	                        		  <fo:block keep-together="always" text-align="right">${((-1)*TOTAL_DEDUCTION)?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                        </fo:table-row>   
	                        <fo:table-row>
	                      		<fo:table-cell>
		                        	<fo:block keep-together="always" text-align="right"></fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                          <fo:block keep-together="always" text-align="right">${TOTAL_BEN_SPLPAY?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${TOTAL_BEN_OTALW?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${TOTAL_BEN_MISCONE?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${((-1)*TOTAL_DD_GPF)?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${((-1)*TOTAL_DD_IT)?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${((-1)*TOTAL_DD_MRGLN)?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${TOTAL_NET_AMOUNT?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
	                      	</fo:table-row>
		                    <fo:table-row>
		                        <fo:table-cell>
		                        <fo:block keep-together="always" text-align="right"></fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                          <fo:block keep-together="always" text-align="right">${TOTAL_BEN_PNLPAY?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${TOTAL_BEN_HDALW?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${TOTAL_BEN_MISCTWO?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${((-1)*TOTAL_DD_VOLPF)?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${((-1)*TOTAL_DD_COURT)?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${((-1)*TOTAL_DD_MISAPP)?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${TOTAL_RND_AMOUNT?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                    </fo:table-row>
		                    <fo:table-row>
		                        <fo:table-cell>
		                        <fo:block keep-together="always" text-align="right"></fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                          <fo:block keep-together="always" text-align="right">${TOTAL_BEN_FPP?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${TOTAL_BEN_OPALW?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${TOTAL_BEN_MISCTHREE ?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${((-1)*TOTAL_DD_GPFLN)?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${((-1)*TOTAL_DD_ELECT)?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${((-1)*TOTAL_DD_DPTDUES)?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right"></fo:block>
		                        </fo:table-cell>
		                    </fo:table-row>
		                    <fo:table-row>
		                        <fo:table-cell>
		                        <fo:block keep-together="always" text-align="right"></fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                          <fo:block keep-together="always" text-align="right">${TOTAL_BEN_DA?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${TOTAL_BEN_ICALW?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${TOTAL_EARNINGS?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${((-1)*TOTAL_DD_EPFREF)?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${((-1)*TOTAL_DD_PAYADV)?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${((-1)*TOTAL_DD_WELFARE)?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right"></fo:block>
		                        </fo:table-cell>
		                    </fo:table-row>
		                    <fo:table-row>
		                        <fo:table-cell>
		                        <fo:block keep-together="always" text-align="right"></fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                          <fo:block keep-together="always" text-align="right">${TOTAL_BEN_HRA?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${TOTAL_BEN_CONALW?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right"></fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${((-1)*TOTAL_DD_ESI)?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${((-1)*TOTAL_DD_TOURADV)?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${((-1)*TOTAL_LOAN_AMOUNT)?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right"></fo:block>
		                        </fo:table-cell>
		                    </fo:table-row>
		                    <fo:table-row>
		                        <fo:table-cell>
		                        <fo:block keep-together="always" text-align="right"></fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                          <fo:block keep-together="always" text-align="right">${TOTAL_BEN_CCA?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${TOTAL_BEN_MEDALW?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right"></fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${((-1)*TOTAL_DD_GIS)?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${((-1)*TOTAL_DD_FESTADV)?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${((-1)*TOTAL_DD_MILKCARDS)?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right"></fo:block>
		                        </fo:table-cell>
		                    </fo:table-row>
		                    <fo:table-row>
		                        <fo:table-cell>
		                        <fo:block keep-together="always" text-align="right"></fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                          <fo:block keep-together="always" text-align="right">${TOTAL_BEN_IR?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${TOTAL_BEN_SPLALW ?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right"></fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${((-1)*TOTAL_DD_PTAX)?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${((-1)*TOTAL_DD_EDNADV)?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${((-1)*TOTAL_DD_MILKDUES )?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right"></fo:block>
		                        </fo:table-cell>
		                    </fo:table-row>
		                    <fo:table-row>
		                        <fo:table-cell>
		                        <fo:block keep-together="always" text-align="right"></fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                          <fo:block keep-together="always" text-align="right">${TOTAL_BEN_NDALW?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${TOTAL_BEN_WASHALW?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right"></fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${((-1)*TOTAL_DD_SSS)?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${((-1)*TOTAL_DD_MEDADV)?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${((-1)*TOTAL_DD_APGLIF)?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right"></fo:block>
		                        </fo:table-cell>
		                    </fo:table-row>
		                    <fo:table-row>
		                        <fo:table-cell>
		                        <fo:block keep-together="always" text-align="right"></fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                          <fo:block keep-together="always" text-align="right">${TOTAL_BEN_CBALW?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${TOTAL_BEN_EXTALW?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right"></fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${((-1)*TOTAL_DD_HRR)?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right">${((-1)*TOTAL_DD_DPTHB)?if_exists?string("##0.00")}</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right"></fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <fo:block keep-together="always" text-align="right"></fo:block>
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