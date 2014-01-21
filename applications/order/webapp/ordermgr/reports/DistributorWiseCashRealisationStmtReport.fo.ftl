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
            margin-top=".5in" margin-bottom="2in">
        <fo:region-body margin-top="1.7in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "distr.txt")}
<fo:page-sequence master-reference="main" force-page-count="no-force">					
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
				<fo:block text-align="left" keep-together="always" white-space-collapse="false">.            ${uiLabelMap.ApDairyMsg}</fo:block>
				<fo:block keep-together="always"  text-align="left" white-space-collapse="false">.          STATEMENT SHOWING CASH REALISATION STATEMENT OF LIQUID MILK SALES ON ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(pmShipDate, "dd/MM/yyyy")} E &amp; ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(supplyDateTime, "dd/MM/yyyy")} M</fo:block>  
              	<fo:block text-align="left" white-space-collapse="false">.                   QUANTITIES AS PER TRUCK SHEETS&amp;MPF HYDERABAD</fo:block>              	
              	<fo:block>------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            	<fo:block text-align="left" white-space-collapse="false" keep-together="always" linefeed-treatment="preserve">.            &lt;---- QTY IN LTRS-------&gt;   CASH VALU/   DDCCASH  C'bleCash  TRANSPTR  TDS  REMIT TO  CHARGES  NET VALUE</fo:block>
            	<fo:block white-space-collapse="false" keep-together="always">TRANSPORTER NAME   TOTAL    CS/CD        RNDDIFF      / PTC   VALUE RND  DISCOUNT   ESEVA/APONLN  ESEVA/APONLN DUE</fo:block> 
            	<fo:block>------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            	<fo:block white-space-collapse="false" keep-together="always">.                                                                 (A)     (B)     (C)     (D)     (E)   (A-B+C-D+E)</fo:block>
            	<fo:block>------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			</fo:static-content>
			<fo:static-content flow-name="xsl-region-after">
				<fo:block>Note:</fo:block>
            	<fo:block text-align="left" keep-white-space-collaplse="false">1) E_Seva commission borned by Distributor&amp;AP Online Commission borned by Federation</fo:block>
            	<fo:block text-align="left">2) In APDDCF Account Excluding P.T.C Booth Sale Proceeds&amp;(*) indicates DDC RndDiff</fo:block>
            	<fo:block text-align="left">3) In 'Net Value Due' RSM-PARLOUR Sale Proceeds are not inclueded</fo:block>
            	<fo:block text-align="left">4) In Total Qty it includes Spl Order Qty&amp;Credit Qty also </fo:block>
            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
            	<fo:block text-align="left" white-space-collapse="false" linefeed-treatment="preserve">This is to certify that the quatities&#xA;and sale proceeds are varified and found &#xA;correct.                                                                                                                   This is to certify that Sale proceeds are correct.</fo:block>
            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
            	<fo:block text-align="left" white-space-collapse="false" linefeed-treatment="preserve">DAIRY MANAGER(Despatch)&#xA;MPF: HYD                                                                                                                  ACCOUNTS OFFICER(MPF: HYD)</fo:block>
        	
        	</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			<#if distributorWiseTotalsMap?has_content>
			<#assign distributorWiseTotalEntry=distributorWiseTotalsMap.entrySet()>
			<#list distributorWiseTotalEntry as distributorWiseTotal>
			<fo:block>
				<fo:table width="100%" table-layout="fixed" space-after="0.0in">
        		<fo:table-column column-width="95pt"/>
				<fo:table-column column-width="80pt"/>
				<fo:table-column column-width="65pt"/>
				<fo:table-column column-width="85pt"/>
				<fo:table-column column-width="86pt"/>
				<fo:table-column column-width="80pt"/>
				<fo:table-column column-width="60pt"/>
				<fo:table-column column-width="56pt"/>
				<fo:table-column column-width="60pt"/>
				<fo:table-column column-width="55pt"/>
				<fo:table-column column-width="85pt"/>
				<fo:table-column column-width="80pt"/>
				<fo:table-body>
					<#if distributorWiseTotal.getKey() =="DayTotals">
					<fo:table-row>
						<fo:table-cell>
							<fo:block>===================================================================================================================================</fo:block>	
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell/>
					</fo:table-row>
					</#if>
                	<fo:table-row>                            
                   		<fo:table-cell>
                   			<#assign distributorFacilityId=distributorWiseTotal.getKey()>
                   			<#assign facility = delegator.findOne("Facility", {"facilityId" :distributorFacilityId}, true)>
                   			<#assign distributorWiseTotalValue=distributorWiseTotal.getValue()>
                            <#if distributorWiseTotal.getKey() =="APDDCF">
                            	<fo:block text-align="left" keep-together="always">Ho-Office Trade</fo:block>
                            	<fo:block text-align="center">E_SEVA :</fo:block>
                            <#elseif distributorWiseTotal.getKey() =="APDDCF-B">
                            	<fo:block text-align="left" keep-together="always">Rsm -Parlours</fo:block>
                            	<fo:block text-align="center">E_SEVA :</fo:block>
                            <#elseif distributorWiseTotal.getKey() =="APDDCF_COL">
                            	<#assign totalCollection=(distributorWiseTotalValue.get("REM_ESEVA")+distributorWiseTotalValue.get("REM_APONLN"))>
                            	<fo:block text-align="left" keep-together="always">APDDCF COLLECTION</fo:block>
                            	<fo:block text-align="center">E_SEVA :</fo:block>
                            	<fo:block text-align="center">AP ONLINE :</fo:block>
                            <#elseif distributorWiseTotal.getKey() =="DayTotals">
                            	<fo:block text-align="left" keep-together="always">TOTAL</fo:block>
                            	<fo:block text-align="left" keep-together="always">E_SEVA :</fo:block>
                            	<fo:block text-align="left">AP ONLINE :</fo:block>
                            	<fo:block>===================================================================================================================================</fo:block>
                            <#else>
                            	<fo:block text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.description)),19)}</fo:block>
                        		<fo:block text-align="center">E_SEVA :</fo:block>
                        		<fo:block text-align="center">AP ONLINE :</fo:block>
                        	</#if>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block text-align="right">${distributorWiseTotalValue.get("TOTAL_QTY")}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block text-align="right">${distributorWiseTotalValue.get("CASH_QTY")}</fo:block>
                            <fo:block text-align="right">${distributorWiseTotalValue.get("CARD_QTY")}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block text-align="right">${distributorWiseTotalValue.get("CASH_VAL")}</fo:block>
                            <fo:block text-align="right">(${distributorWiseTotalValue.get("RNDIFF_VAL")})</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block text-align="right">${distributorWiseTotalValue.get("DDCCASH_VAL")}</fo:block>
                            <fo:block text-align="right">${distributorWiseTotalValue.get("PTC_VAL")}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block text-align="right">${distributorWiseTotalValue.get("CCASH_RND_VAL")}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block text-align="right">${distributorWiseTotalValue.get("TRSP_DISC")}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block text-align="right">${distributorWiseTotalValue.get("TDS")}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                        	<fo:block text-align="right">${totalCollection?if_exists}</fo:block>
                            <fo:block text-align="right">${distributorWiseTotalValue.get("REM_ESEVA")}</fo:block>
                            <fo:block text-align="right">${distributorWiseTotalValue.get("REM_APONLN")}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                        	<fo:block text-align="right">0</fo:block>
                            <fo:block text-align="right">${distributorWiseTotalValue.get("CHRG_ESEVA")}</fo:block>
                            <fo:block text-align="right">${distributorWiseTotalValue.get("CHRG_APONLN")}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block text-align="right">${distributorWiseTotalValue.get("NET_VAL")}</fo:block>
                        </fo:table-cell>
                   </fo:table-row>
			    </fo:table-body>
			</fo:table>
			</fo:block>	
			</#list>
			</#if>
			</fo:flow>						        	
   </fo:page-sequence>
   </fo:root>
</#escape>