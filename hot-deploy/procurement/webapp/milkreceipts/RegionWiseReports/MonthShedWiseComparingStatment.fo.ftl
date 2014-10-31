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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-left=".5in" margin-top="0.5in">
                <fo:region-body margin-top="0.9in"/>
                <fo:region-before extent="0.5in"/>
                <fo:region-after extent="0.5in"/>
            </fo:simple-page-master>
       </fo:layout-master-set>
    ${setRequestAttribute("OUTPUT_FILENAME", "MonthWiseShedWiseMilkReceiptsCompStmt.txt")}
<#if errorMessage?has_content>
<fo:page-sequence master-reference="main">
   <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
      <fo:block font-size="14pt">
              ${errorMessage}.
   	  </fo:block>
   </fo:flow>
</fo:page-sequence>        
<#else> 
   <fo:page-sequence master-reference="main">
  	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="6pt">
  		<fo:block text-align="left" white-space-collapse="false" font-size="6pt" keep-together="always">&#160;					             MILK RECEIPTS COMPARING WITH LAST YEAR (LITRES IN LAKHS)</fo:block>
  		<fo:block text-align="left" white-space-collapse="false" font-size="6pt" keep-together="always">&#160;                 ----------------------------------------------------------</fo:block>
  		<#assign shedList  = delegator.findOne("Facility", {"facilityId" : parameters.shedId}, true)>
  		<fo:block text-align="left" white-space-collapse="false" font-size="6pt" keep-together="always">&#160;					       SHED/UNION NAME :${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(shedList.get("facilityName")?if_exists)),18)}</fo:block>
  		<fo:block text-align="left" white-space-collapse="false" font-size="6pt" keep-together="always">&#160;</fo:block>
  		<fo:block text-align="left" white-space-collapse="false" font-size="6pt" keep-together="always">-------------------------------|------------------------------|------------------------|----------------|</fo:block>
  		<fo:block text-align="left" white-space-collapse="false" font-size="6pt" keep-together="always">&#160;        PREVIOUS YEAR       &#160; |	    CURRENT YEAR            &#160;| VAR WITH PR.YEAR       |    TARGET    &#160;.|</fo:block>
  		<fo:block text-align="left" white-space-collapse="false" font-size="6pt" keep-together="always">----------|---------|----------|---------|-----------|--------|---------|--------------|-------|--------|</fo:block>
  		<fo:table>  
			<fo:table-column column-width="30pt"/>
		   	<fo:table-column column-width="31pt"/>
			<fo:table-column column-width="30pt"/>
			<fo:table-column column-width="45pt"/>
			<fo:table-column column-width="35pt"/>
			<fo:table-column column-width="37pt"/>
			<fo:table-column column-width="40pt"/>
			<fo:table-column column-width="25pt"/>
			<fo:table-column column-width="65pt"/>
			<fo:table-column column-width="35pt"/>
			<fo:table-column column-width="5pt"/>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell>
  						<fo:block text-align="left"  font-size="6pt" keep-together="always">&#160;MONTH    </fo:block>
					</fo:table-cell>
					<fo:table-cell>
  						<fo:block text-align="right"  font-size="6pt" keep-together="always">&#160;    | RECTS</fo:block>
					</fo:table-cell>
					<fo:table-cell>
  						<fo:block text-align="right"  font-size="6pt" keep-together="always">| AVG </fo:block>
					</fo:table-cell>
					<fo:table-cell>
  						<fo:block text-align="right"  font-size="6pt" keep-together="always">| MONTH  </fo:block>
					</fo:table-cell>
					<fo:table-cell>
  						<fo:block text-align="right"  font-size="6pt" keep-together="always">| RECTS </fo:block>
					</fo:table-cell>
					<fo:table-cell>
  						<fo:block text-align="right"  font-size="6pt" keep-together="always">| AVG </fo:block>
					</fo:table-cell>
					<fo:table-cell>
  						<fo:block text-align="right"  font-size="6pt" keep-together="always">| LITRS</fo:block>
					</fo:table-cell>
					<fo:table-cell>
  						<fo:block text-align="right"  font-size="6pt" keep-together="always">&#160;       |%GE </fo:block>
					</fo:table-cell>
					<fo:table-cell>
  						<fo:block text-align="right" font-size="6pt" keep-together="always">&#160;| LITRS</fo:block>
					</fo:table-cell>
					<fo:table-cell>
  						<fo:block text-align="right" font-size="6pt" keep-together="always">&#160;| DIFRNC</fo:block>
					</fo:table-cell>
					<fo:table-cell>
  						<fo:block text-align="right" font-size="6pt" keep-together="always">&#160;|</fo:block>
					</fo:table-cell>
  				</fo:table-row>
			</fo:table-body>
		</fo:table>
		<fo:block text-align="left" white-space-collapse="false" font-size="6pt" keep-together="always">----------|---------|----------|---------|-----------|--------|---------|--------------|-------|--------|</fo:block>
 	</fo:static-content>
	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" font-size="6pt">
	<fo:block  font-size="6pt" font-family="Courier,monospace" >
		<fo:table>  
			<fo:table-column column-width="25pt"/>
		   	<fo:table-column column-width="35pt"/>
			<fo:table-column column-width="40pt"/>
			<fo:table-column column-width="40pt"/>
			<fo:table-column column-width="35pt"/>
			<fo:table-column column-width="35pt"/>
			<fo:table-column column-width="45pt"/>
			<fo:table-column column-width="45pt"/>
			<fo:table-column column-width="35pt"/>
			<fo:table-column column-width="30pt"/>
			<fo:table-body>
			<#assign totPrevMonthsQty = 0>
			<#assign totPrevMonthsAvg = 0>
			<#assign totCurrMonthsQty = 0>
			<#assign totCurrMonthsAvg = 0>
			<#assign totDiffQty = 0>
			<#assign totAgvQty = 0>
				<#list prevMonthKeyList as prevMonthKey>
				<#assign year =0>
					<fo:table-row>
						<fo:table-cell>
							<fo:block text-align="left" font-size="6pt" keep-together="always" linefeed-treatment="preserve">.</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<#assign yearString = StringUtil.split(prevMonthKey,"/")[1]>
						<#assign monthString = StringUtil.split(prevMonthKey,"/")[0]>
							<fo:table-cell>
								<fo:block text-align="left" font-size="6pt" keep-together="always">${prevMonthKey}</fo:block>
							</fo:table-cell>
							<#if (previousQtyDateMap.get(prevMonthKey))?has_content>
	        					<#assign previousMonthsQty=(previousQtyDateMap.get(prevMonthKey))>
	        					<#assign prevMonthsQty=previousMonthsQty.get("qtyLtrs")>
	        					<#assign prevMonthsAvg=previousMonthsQty.get("avgQty")>
	        				<#else>
	        					<#assign prevMonthsQty =0>
	        					<#assign prevMonthsAvg =0>
	        				</#if>
	        				<#assign totPrevMonthsQty= totPrevMonthsQty+prevMonthsQty>
	        				<#assign totPrevMonthsAvg= totPrevMonthsAvg+prevMonthsAvg>
							<fo:table-cell>
								<fo:block text-align="right" font-size="6pt" keep-together="always">${(prevMonthsQty)?if_exists?string('##0.00')}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block text-align="right" font-size="6pt" keep-together="always">${(prevMonthsAvg)?if_exists?string('##0.00')}</fo:block>
							</fo:table-cell>
							<#assign year=year+Static["java.lang.Integer"].parseInt(yearString)+1>
							<#assign curKey = monthString+"/"+year> 
							<fo:table-cell>
								<fo:block text-align="right" font-size="6pt" keep-together="always">${curKey}</fo:block>
							</fo:table-cell>
							<#if (currentQtyDateMap.get(curKey))?has_content>
	        					<#assign currentMonthsQty=(currentQtyDateMap.get(curKey))>
	        					<#assign currMonthsQty=currentMonthsQty.get("qtyLtrs")>
	        					<#assign currMonthsAvg=currentMonthsQty.get("avgQty")>
	        				<#else>
	        					<#assign currMonthsQty =0>
	        					<#assign currMonthsAvg =0>
	        				</#if>
	        				<#assign totCurrMonthsQty= totCurrMonthsQty+currMonthsQty>
	        				<#assign totCurrMonthsAvg= totCurrMonthsAvg+currMonthsAvg>
	        				<fo:table-cell>
								<fo:block text-align="right" font-size="6pt" keep-together="always">${(currMonthsQty)?if_exists?string('##0.00')}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block text-align="right" font-size="6pt" keep-together="always">${(currMonthsAvg)?if_exists?string('##0.00')}</fo:block>
							</fo:table-cell>
							<#assign diffQty = (currMonthsQty-prevMonthsQty)>
							<#assign totDiffQty= totDiffQty+diffQty>
							<fo:table-cell>
								<fo:block text-align="right" font-size="6pt" keep-together="always">${(diffQty)?if_exists?string('##0.00')}</fo:block>
							</fo:table-cell>
							<#if prevMonthsQty != 0>
								<#assign qtyGrowth = ((diffQty/prevMonthsQty)*(100))>
							<#else>
								<#assign qtyGrowth = 0>
							</#if>	
							<#assign totAgvQty= totAgvQty+qtyGrowth>
							<fo:table-cell>
								<fo:block text-align="right" font-size="6pt" keep-together="always">${(qtyGrowth)?if_exists?string('##0.00')}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block text-align="right" font-size="6pt" keep-together="always">0.00</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block text-align="right" font-size="6pt" keep-together="always">0.00</fo:block>
							</fo:table-cell>
						</fo:table-row>
				</#list>
					<fo:table-row>
						<fo:table-cell>
							<fo:block text-align="left" font-size="6pt" keep-together="always">--------------------------------------------------------------------------------------------------------</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell>
								<fo:block text-align="left" font-size="6pt" keep-together="always">TOTAL</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block text-align="right" font-size="6pt" keep-together="always">${(totPrevMonthsQty)?if_exists?string('##0.00')}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block text-align="right" font-size="6pt" keep-together="always">${(totPrevMonthsAvg)?if_exists?string('##0.00')}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block text-align="right" font-size="6pt" keep-together="always"></fo:block>
							</fo:table-cell>
	        				<fo:table-cell>
								<fo:block text-align="right" font-size="6pt" keep-together="always">${(totCurrMonthsQty)?if_exists?string('##0.00')}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block text-align="right" font-size="6pt" keep-together="always">${(totCurrMonthsAvg)?if_exists?string('##0.00')}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block text-align="right" font-size="6pt" keep-together="always">${(totDiffQty)?if_exists?string('##0.00')}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block text-align="right" font-size="6pt" keep-together="always">${totAgvQty?if_exists?string('##0.00')}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block text-align="right" font-size="6pt" keep-together="always">0.00</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block text-align="right" font-size="6pt" keep-together="always">0.00</fo:block>
							</fo:table-cell>
					</fo:table-row>
			</fo:table-body>
		</fo:table>
	</fo:block>
	<fo:block text-align="left" white-space-collapse="false" font-size="6pt" keep-together="always">---------------------------------------------------------------------------------------------------------</fo:block>
</fo:flow>
</fo:page-sequence>
	</#if>
	</fo:root>
</#escape>