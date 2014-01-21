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

<fo:page-sequence master-reference="main" force-page-count="no-force">					
			<fo:static-content flow-name="xsl-region-before">
				<fo:block text-align="left" keep-together="always" font-size="11pt" white-space-collapse="false">.            ${uiLabelMap.ApDairyMsg}Helvitica</fo:block>
				<fo:block keep-together="always"  text-align="left" font-size="11pt" white-space-collapse="false">.       STATEMENT SHOWING ZONE WISE VENDOR'S CASH REALISATION STATEMENT OF LIQUID MILK SALES ON ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(pmShipDate, "dd/MM/yyyy")} E &amp; ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(supplyDateTime, "dd/MM/yyyy")} M</fo:block>  
              	<fo:block font-size="10pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            	<fo:block text-align="left" white-space-collapse="false" font-size="10pt" linefeed-treatment="preserve" font-family="Courier,monospace">.               &lt;----- Quantity --------&gt;   CASH   MARGIN  &lt;----- Realisation value -------&gt; &lt;---- Vendor's Margin Liability-------------------&gt;&lt;-------Net Realisation-----&gt;</fo:block>
            	<fo:block white-space-collapse="false" font-size="10pt" linefeed-treatment="preserve" font-family="Courier,monospace">ZONE              CS       CD      S.O     VALUE   Adujstd    CS   CD(dis[-30ps/ltr])  S.O   CS/Inctv  CD/Inctv  S.O/Inctv InctvTOTAL  TOTAL     CS         CD         S.O</fo:block> 
            	<fo:block font-size="10pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            </fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			<#if zoneWiseTotalsMap?has_content>
				<#assign zoneWiseTotalEntry=zoneWiseTotalsMap.entrySet()>
			<#list zoneWiseTotalEntry as zoneWiseTotal>
		<fo:block  font-size="10pt"  font-family="Times" font-style="normal" font-weight="normal">
            <fo:table width="100%" table-layout="fixed" space-after="0.0in">
        		<fo:table-column column-width="110pt"/>
				<fo:table-column column-width="25pt"/>
				<fo:table-column column-width="55pt"/>
				<fo:table-column column-width="40pt"/>
				<fo:table-column column-width="60pt"/>
				<fo:table-column column-width="50pt"/>
				<fo:table-column column-width="65pt"/>
				<fo:table-column column-width="70pt"/>
				<fo:table-column column-width="50pt"/>
				<fo:table-column column-width="70pt"/>
				<fo:table-column column-width="65pt"/>
				<fo:table-column column-width="50pt"/>
				<fo:table-column column-width="65pt"/>
				<fo:table-column column-width="65pt"/>
				<fo:table-column column-width="70pt"/>
				<fo:table-column column-width="70pt"/>
				<fo:table-column column-width="49pt"/>
				<fo:table-column column-width="50pt"/>				    		    		
				<fo:table-body>
					<#if zoneWiseTotal.getKey() =="GRTOTAL">
					<fo:table-row>
						<fo:table-cell>
							<fo:block>-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
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
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell/>
					</fo:table-row>
					</#if>
                	<fo:table-row> 
                		<fo:table-cell>
                			<#if zoneWiseTotal.getKey() !="GRTOTAL">
                   			<#assign facility = delegator.findOne("Facility", {"facilityId" : zoneWiseTotal.getKey()}, true)>
                   			</#if>
                            <#if zoneWiseTotal.getKey() !="GRTOTAL">
                            	<fo:block text-align="left" keep-together="always" font-size="12pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.description)),12)}</fo:block>
                            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                            <#else>
                            <fo:block text-align="left" font-size="12pt">Day Totals: </fo:block>
							<fo:block>-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                            </#if>
                        </fo:table-cell>
                        <#assign zoneWiseTotalValue=zoneWiseTotal.getValue()>
                        <fo:table-cell>
                            <fo:block text-align="right" >${zoneWiseTotalValue.get("CASH_QTY")}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block text-align="right">${zoneWiseTotalValue.get("CARD_QTY")}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block text-align="right">${zoneWiseTotalValue.get("SPECIAL_ORDER_QTY")}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block text-align="right">${zoneWiseTotalValue.get("CASH_VAL")}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block text-align="right">${zoneWiseTotalValue.get("MRADJ")}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block text-align="right">${zoneWiseTotalValue.get("CASH_REVAL")}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block text-align="right">${zoneWiseTotalValue.get("CARD_REVAL")}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block text-align="right">${zoneWiseTotalValue.get("SPECIAL_ORDER_REVAL")}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block text-align="right">${zoneWiseTotalValue.get("CASH_MR")}</fo:block>
                            <fo:block text-align="right">${zoneWiseTotalValue.get("TOTALCASH_MR")}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block text-align="right">${zoneWiseTotalValue.get("CARD_MR")}</fo:block>
                            <fo:block text-align="right">${zoneWiseTotalValue.get("TOTALCARD_MR")}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block text-align="right">${zoneWiseTotalValue.get("SPECIAL_ORDER_MR")}</fo:block>
                            <fo:block text-align="right">${zoneWiseTotalValue.get("TOTALSO_MR")}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block text-align="right">${zoneWiseTotalValue.get("TOTAL_INCENTIVE")}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block text-align="right">${zoneWiseTotalValue.get("TOTAL_MR")}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block text-align="right">${zoneWiseTotalValue.get("CASH_NETVAL")}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block text-align="right">${zoneWiseTotalValue.get("CARD_NETVAL")}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block text-align="right">${zoneWiseTotalValue.get("SPECIAL_ORDER_NETVAL")}</fo:block>
                        </fo:table-cell>
				   </fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block>	
		<fo:block></fo:block>
		</#list>
		</#if>
	  </fo:flow>						        	
   </fo:page-sequence>
   </fo:root>
</#escape>