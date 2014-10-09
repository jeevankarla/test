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
                <fo:region-body margin-top="1.0in"/>
                <fo:region-before extent="0.5in"/>
                <fo:region-after extent="0.5in"/>
            </fo:simple-page-master>
       </fo:layout-master-set>
    ${setRequestAttribute("OUTPUT_FILENAME", "RegionWiseReport.txt")}
<#if errorMessage?has_content>
<fo:page-sequence master-reference="main">
   <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
      <fo:block font-size="14pt">
              ${errorMessage}.
   	  </fo:block>
   </fo:flow>
</fo:page-sequence>        
<#else> 
   <#if finalMap?has_content>  
		<fo:page-sequence master-reference="main">
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="6pt">
				<fo:block font-size="6pt">VST_ASCII-015 VST_ASCII-027VST_ASCII-080</fo:block>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="6pt">------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="6pt">&#160;				                                                                         THE ANDHRA PRADESH DAIRY DEVELOPMENT COOPERATIVE FEDERATION LTD                                          &#160;        </fo:block>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="6pt">------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="6pt">&#160;                               											 					                                   Milk Procurement                                            &#160;  </fo:block>
				<fo:block text-align="left" white-space-collapse="false" font-size="6pt">|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|</fo:block>
				<fo:block text-align="left" white-space-collapse="false" font-size="6pt">|                            &#160; .|            This Day(Ltrs)            &#160;               |                 This Month(upto date)                   &#160;.|           Cumulative from 1st Apr(upto date)               &#160;.|</fo:block>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="6pt">|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|</fo:block>
				<fo:block font-size="6pt">
				<fo:table>
					<fo:table-column column-width="75pt"/>
				   	<fo:table-column column-width="30pt"/>
					<fo:table-column column-width="30pt"/>
					<fo:table-column column-width="35pt"/>
					<fo:table-column column-width="35pt"/>
					<fo:table-column column-width="32pt"/>
					<fo:table-column column-width="35pt"/>
					<fo:table-column column-width="35pt"/>
					<fo:table-column column-width="35pt"/>
					<fo:table-column column-width="35pt"/>
					<fo:table-column column-width="35pt"/>
				   	<fo:table-column column-width="35pt"/>
					<fo:table-column column-width="35pt"/>
					<fo:table-column column-width="35pt"/>
					<fo:table-column column-width="35pt"/>
					<fo:table-column column-width="35pt"/>
					<fo:table-column column-width="35pt"/>
					<fo:table-column column-width="45pt"/>
					<fo:table-column column-width="35pt"/>
					<fo:table-column column-width="35pt"/>
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell>
		            			<fo:block text-align="left" font-size="6pt" keep-together="always">&#160;DISTRICT</fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="left" font-size="6pt" keep-together="always">DATE</fo:block>
		            		</fo:table-cell>
							<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always"></fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always"></fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always"></fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always"></fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always">%ge Gr </fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always">%ge Gr </fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always"></fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always"></fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always"></fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always"></fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always">%ge Gr </fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always">%ge Gr</fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always"></fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always"></fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always"></fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always"></fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always">%ge Gr</fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always">%ge Gr</fo:block>
		            		</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell>
		            			<fo:block text-align="left" font-size="6pt" keep-together="always"></fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="left" font-size="6pt" keep-together="always"></fo:block>
		            		</fo:table-cell>
							<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always">${previousPreviousYear}</fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always">${previousYear}</fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always">Gr% </fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always">${currentYear}</fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always">over</fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always">over</fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always">${previousPreviousYear}</fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always">${previousYear}</fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always">Gr% </fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always">${currentYear}</fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always">over</fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always">over</fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always">${previousPreviousYear}</fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always">${previousYear}</fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always">Gr%</fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always">${currentYear}</fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always">over</fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always">over</fo:block>
		            		</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell>
		            			<fo:block text-align="left" font-size="6pt" keep-together="always"></fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="left" font-size="6pt" keep-together="always"></fo:block>
		            		</fo:table-cell>
							<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always"></fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always"></fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always"></fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always"></fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always">${previousPreviousYear}</fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always"> ${previousYear}</fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always"></fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always"></fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always"></fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always"></fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always">${previousPreviousYear}</fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always">${previousYear}</fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always"></fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always"></fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always"></fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always"></fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always">${previousPreviousYear}</fo:block>
		            		</fo:table-cell>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="6pt" keep-together="always">${previousYear}</fo:block>
		            		</fo:table-cell>
						</fo:table-row>
					</fo:table-body>
				</fo:table>
			</fo:block>
		</fo:static-content>
		<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" font-size="6pt">
			<fo:block font-size="6pt">------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			<#if (parameters.facilityGroupId == "ANDHRA") || (parameters.facilityGroupId == "")>
				<fo:block  font-size="6pt">
					<fo:table>  
						<fo:table-column column-width="75pt"/>
					   	<fo:table-column column-width="30pt"/>
						<fo:table-column column-width="30pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="32pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="35pt"/>
					   	<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="45pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="35pt"/>
						<#assign previousPreviousAndhraTotalDayQty = 0>
						<#assign previousDayAndhraTotalQty = 0>
						<#assign currentDayAndhraTotalQty =  0>
						<#assign previousPreviousMonthAndhraTotalQty = 0>
						<#assign previousMonthAndhraTotalQty = 0>
						<#assign currentMonthAndhraTotalQty = 0>
						<#assign previousPreviousYearAndhraTotalQty = 0>
						<#assign previousYearAndhraTotalQty =0>
						<#assign currentYearAndhraTotalQty = 0>
						<#assign andhraShedsDetails=andhraShedsFinalMap.entrySet()>
						<fo:table-body>
							<#list andhraShedsDetails as andhraDetails>
								<fo:table-row>
									<#assign facility = delegator.findOne("Facility", {"facilityId" : andhraDetails.getKey()}, true)>
									<fo:table-cell>
			            				<fo:block text-align="left" font-size="6pt" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((facility.get("facilityName")))),15)}</fo:block>
			            			</fo:table-cell>
			            			<fo:table-cell>
			            				<fo:block text-align="left" font-size="6pt" keep-together="always">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate,"dd/MM")}</fo:block>
			            			</fo:table-cell>
			            			<#assign previousPreviousAndhraDayQty = andhraDetails.getValue().get("DayWise").get(previousPreviousYear)>
			            			<#assign previousPreviousAndhraTotalDayQty = previousPreviousAndhraTotalDayQty + previousPreviousAndhraDayQty>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${previousPreviousAndhraDayQty?if_exists?string("#0")}</fo:block>
			            			</fo:table-cell>
			            			<#assign previousDayAndhraQty = andhraDetails.getValue().get("DayWise").get(previousYear)>
			            			<#assign previousDayAndhraTotalQty = previousDayAndhraTotalQty + previousDayAndhraQty>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${previousDayAndhraQty?if_exists?string("#0")}</fo:block>
			            			</fo:table-cell>
			            			<#if previousDayAndhraQty == 0>
										<#assign growth = -100 >
									</#if>
			            			<#assign difference =((previousDayAndhraQty)-(previousPreviousAndhraDayQty))>
			            			<#if previousPreviousAndhraDayQty!=0>
			            				<#assign growth =((difference)/(previousPreviousAndhraDayQty)*100)>
			            			<#else>
					   	    			<#assign growth = 100>
			            			</#if>
			            			<#if (previousDayAndhraQty == 0) && (previousPreviousAndhraDayQty == 0)>
			            				<#assign growth = 0 >
			            			</#if>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${growth?if_exists?string("#0.00")}</fo:block>
			            			</fo:table-cell>
			            			<#assign currentDayAndhraQty =  andhraDetails.getValue().get("DayWise").get(currentYear)>
			            			<#assign currentDayAndhraTotalQty =  currentDayAndhraTotalQty + currentDayAndhraQty>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${currentDayAndhraQty?if_exists?string("#0")}</fo:block>
			            			</fo:table-cell>
			            			<#if currentDayAndhraQty == 0>
										<#assign prevPrevgrowth = -100 >
									</#if>
			            			<#assign difference =((currentDayAndhraQty)-(previousPreviousAndhraDayQty))>
			            			<#if previousPreviousAndhraDayQty!=0>
			            				<#assign prevPrevgrowth =((difference)/(previousPreviousAndhraDayQty)*100)>
			            			<#else>
					   	    			<#assign prevPrevgrowth = 100>
			            			</#if>
			            			<#if (currentDayAndhraQty == 0) && (previousPreviousAndhraDayQty == 0)>
			            				<#assign prevPrevgrowth = 0 >
			            			</#if>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${prevPrevgrowth?if_exists?string("#0.00")}</fo:block>
			            			</fo:table-cell>
			            			<#if currentDayAndhraQty == 0>
										<#assign prevGrowth = -100 >
									</#if>
			            			<#assign difference =((currentDayAndhraQty)-(previousDayAndhraQty))>
			            			<#if previousDayAndhraQty!=0>
			            				<#assign prevGrowth =((difference)/(previousDayAndhraQty)*100)>
			            			<#else>
					   	    			<#assign prevGrowth = 100>
			            			</#if>
			            			<#if (currentDayAndhraQty == 0) && (previousDayAndhraQty == 0)>
			            				<#assign prevGrowth = 0 >
			            			</#if>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${prevGrowth?if_exists?string("#0.00")}</fo:block>
			            			</fo:table-cell>
			            			<#assign previousPreviousMonthAndhraQty = andhraDetails.getValue().get("MonthWise").get(previousPreviousYear)>
			            			<#assign previousPreviousMonthAndhraTotalQty = previousPreviousMonthAndhraTotalQty + previousPreviousMonthAndhraQty>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${previousPreviousMonthAndhraQty?if_exists?string("#0.00")}</fo:block>
			            			</fo:table-cell>
			            			<#assign previousMonthAndhraQty = andhraDetails.getValue().get("MonthWise").get(previousYear)>
			            			<#assign previousMonthAndhraTotalQty = previousMonthAndhraTotalQty + previousMonthAndhraQty>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${previousMonthAndhraQty?if_exists?string("#0.00")}</fo:block>
			            			</fo:table-cell>
			            			<#if previousMonthAndhraQty == 0>
										<#assign monthGrowth = -100 >
									</#if>
			            			<#assign monthDifference =((previousMonthAndhraQty)-(previousPreviousMonthAndhraQty))>
			            			<#if previousPreviousMonthAndhraQty!=0>
			            				<#assign monthGrowth =((monthDifference)/(previousPreviousMonthAndhraQty)*100)>
			            			<#else>
					   	    			<#assign monthGrowth = 100>
			            			</#if>
			            			<#if (previousMonthAndhraQty == 0) && (previousPreviousMonthAndhraQty == 0)>
			            				<#assign monthGrowth = 0 >
			            			</#if>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${monthGrowth?if_exists?string("#0.00")}</fo:block>
			            			</fo:table-cell>
			            			<#assign currentMonthAndhraQty =  andhraDetails.getValue().get("MonthWise").get(currentYear)>
			            			<#assign currentMonthAndhraTotalQty = currentMonthAndhraTotalQty + currentMonthAndhraQty>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${currentMonthAndhraQty?if_exists?string("#0.00")}</fo:block>
			            			</fo:table-cell>
			            			<#if currentMonthAndhraQty == 0>
										<#assign prevPrevMonthGrowth = -100>
									</#if>
			            			<#assign monthsDifference =((currentMonthAndhraQty)-(previousPreviousMonthAndhraQty))>
			            			<#if previousPreviousMonthAndhraQty!=0>
			            				<#assign prevPrevMonthGrowth =((monthsDifference)/(previousPreviousMonthAndhraQty)*100)>
			            			<#else>
					   	    			<#assign prevPrevMonthGrowth = 100>
			            			</#if>
			            			<#if (currentMonthAndhraQty == 0) && (previousPreviousMonthAndhraQty == 0)>
			            				<#assign prevPrevMonthGrowth = 0 >
			            			</#if>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${prevPrevMonthGrowth?if_exists?string("#0.00")}</fo:block>
			            			</fo:table-cell>
			            			<#if currentMonthAndhraQty == 0>
										<#assign prevMonthGrowth = -100>
									</#if>
			            			<#assign yearDiff =((currentMonthAndhraQty)-(previousMonthAndhraQty))>
			            			<#if previousMonthAndhraQty!=0>
			            				<#assign prevMonthGrowth =((yearDiff)/(previousMonthAndhraQty)*100)>
			            			<#else>
					   	    			<#assign prevMonthGrowth =100>
			            			</#if>
			            			<#if (currentMonthAndhraQty == 0) && (previousMonthAndhraQty == 0)>
			            				<#assign prevMonthGrowth = 0 >
			            			</#if>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${prevMonthGrowth?if_exists?string("#0.00")}</fo:block>
			            			</fo:table-cell>
			            			<#assign previousPreviousYearAndhraQty = andhraDetails.getValue().get("cummulative").get(previousPreviousYear)>
			            			<#assign previousPreviousYearAndhraTotalQty = previousPreviousYearAndhraTotalQty + previousPreviousYearAndhraQty>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${previousPreviousYearAndhraQty?if_exists?string("#0.00")}</fo:block>
			            			</fo:table-cell>
			            			<#assign previousYearAndhraQty = andhraDetails.getValue().get("cummulative").get(previousYear)>
			            			<#assign previousYearAndhraTotalQty = previousYearAndhraTotalQty  +  previousYearAndhraQty>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${previousYearAndhraQty?if_exists?string("#0.00")}</fo:block>
			            			</fo:table-cell>
			            			<#if previousYearAndhraQty == 0>
										<#assign yearGrowth = -100>
									</#if>
			            			<#assign yearDifference =((previousYearAndhraQty)-(previousPreviousYearAndhraQty))>
			            			<#if previousPreviousYearAndhraQty!=0>
			            				<#assign yearGrowth =((yearDifference)/(previousPreviousYearAndhraQty)*100)>
			            			<#else>
					   	    			<#assign yearGrowth =100>
			            			</#if>
			            			<#if (previousYearAndhraQty == 0) && (previousPreviousYearAndhraQty == 0)>
			            				<#assign yearGrowth = 0 >
			            			</#if>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${yearGrowth?if_exists?string("#0.00")}</fo:block>
			            			</fo:table-cell>
			            			<#assign currentYearAndhraQty =  andhraDetails.getValue().get("cummulative").get(currentYear)>
			            			<#assign currentYearAndhraTotalQty = currentYearAndhraTotalQty + currentYearAndhraQty>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${currentYearAndhraQty?if_exists?string("#0.00")}</fo:block>
			            			</fo:table-cell>
			            			<#if currentYearAndhraQty == 0>
										<#assign prevPrevYearGrowth = -100>
									</#if>
			            			<#assign yearsDifference =((currentYearAndhraQty)-(previousPreviousYearAndhraQty))>
			            			<#if previousPreviousYearAndhraQty!=0>
			            				<#assign prevPrevYearGrowth =((yearsDifference)/(previousPreviousYearAndhraQty)*100)>
			            			<#else>
					   	    			<#assign prevPrevYearGrowth =100>
			            			</#if>
			            			<#if (currentYearAndhraQty == 0) && (previousPreviousYearAndhraQty == 0)>
			            				<#assign prevPrevYearGrowth = 0 >
			            			</#if>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${prevPrevYearGrowth?if_exists?string("#0.00")}</fo:block>
			            			</fo:table-cell>
			            			<#if currentYearAndhraQty == 0>
										<#assign prevYearGrowth = -100>
									</#if>
			            			<#assign yearDiff =((currentYearAndhraQty)-(previousYearAndhraQty))>
			            			<#if previousYearAndhraQty!=0>
			            				<#assign prevYearGrowth =((yearDiff)/(previousYearAndhraQty)*100)>
			            			<#else>
					   	    			<#assign prevYearGrowth =100>
			            			</#if>
			            			<#if (currentYearAndhraQty == 0) && (previousYearAndhraQty == 0)>
			            				<#assign prevYearGrowth = 0 >
			            			</#if>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${prevYearGrowth?if_exists?string("#0.00")}</fo:block>
			            			</fo:table-cell>
								</fo:table-row>
							</#list>
							<fo:table-row>
			            		<fo:table-cell>
				        			<fo:block font-size="6pt">------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				        		</fo:table-cell>
				        	</fo:table-row>	
							<fo:table-row>
								<fo:table-cell>
		            				<fo:block text-align="left" font-size="6pt" keep-together="always">ANDHRA TOTAL:</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell>
		            				<fo:block text-align="left" font-size="6pt" keep-together="always"></fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${previousPreviousAndhraTotalDayQty?if_exists?string("#0")}</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${previousDayAndhraTotalQty?if_exists?string("#0")}</fo:block>
		            			</fo:table-cell>
		            			<#if previousDayAndhraTotalQty == 0>
									<#assign totalGrowth = -100 >
								</#if>
		            			<#assign prevDifference =((previousDayAndhraTotalQty)-(previousPreviousAndhraTotalDayQty))>
		            			<#if previousPreviousAndhraTotalDayQty!=0>
		            				<#assign totalGrowth =((prevDifference)/(previousPreviousAndhraTotalDayQty)*100)>
		            			<#else>
				   	    			<#assign totalGrowth = 100>
		            			</#if>
		            			<#if (previousDayAndhraTotalQty == 0) && (previousPreviousAndhraTotalDayQty == 0)>
			            			<#assign totalGrowth = 0>
			            		</#if>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${totalGrowth?if_exists?string("#0.00")}</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${currentDayAndhraTotalQty?if_exists?string("#0")}</fo:block>
		            			</fo:table-cell>
		            			<#if currentDayAndhraTotalQty == 0>
									<#assign prevPrevTotalGrowth = -100 >
								</#if>
		            			<#assign totalDifference =((currentDayAndhraTotalQty)-(previousPreviousAndhraTotalDayQty))>
		            			<#if previousPreviousAndhraTotalDayQty!=0>
		            				<#assign prevPrevTotalGrowth =((totalDifference)/(previousPreviousAndhraTotalDayQty)*100)>
		            			<#else>
				   	    			<#assign prevPrevTotalGrowth =100>
		            			</#if>
		            			<#if (currentDayAndhraTotalQty == 0) && (previousPreviousAndhraTotalDayQty == 0)>
			            			<#assign prevPrevTotalGrowth = 0>
			            		</#if>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${prevPrevTotalGrowth?if_exists?string("#0.00")}</fo:block>
		            			</fo:table-cell>
		            			<#if currentDayAndhraTotalQty == 0>
									<#assign prevTotalGrowth = -100>
								</#if>
		            			<#assign totDifference =((currentDayAndhraTotalQty)-(previousDayAndhraTotalQty))>
		            			<#if previousDayAndhraTotalQty!=0>
		            				<#assign prevTotalGrowth =((totDifference)/(previousDayAndhraTotalQty)*100)>
		            			<#else>
				   	    			<#assign prevTotalGrowth =100>
		            			</#if>
		            			<#if (currentDayAndhraTotalQty == 0) && (previousDayAndhraTotalQty == 0)>
			            			<#assign prevTotalGrowth = 0>
			            		</#if>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${prevTotalGrowth?if_exists?string("#0.00")}</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${previousPreviousMonthAndhraTotalQty?if_exists?string("#0.00")}</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${previousMonthAndhraTotalQty?if_exists?string("#0.00")}</fo:block>
		            			</fo:table-cell>
		            			<#if previousMonthAndhraTotalQty == 0>
									<#assign monthGrowth = -100>
								</#if>
		            			<#assign monthDifference =((previousMonthAndhraTotalQty)-(previousPreviousMonthAndhraTotalQty))>
		            			<#if previousPreviousMonthAndhraTotalQty!=0>
		            				<#assign monthGrowth =((monthDifference)/(previousPreviousMonthAndhraTotalQty)*100)>
		            			<#else>
				   	    			<#assign monthGrowth = 100>
		            			</#if>
		            			<#if (previousMonthAndhraTotalQty == 0) && (previousPreviousMonthAndhraTotalQty == 0)>
			            			<#assign monthGrowth = 0>
			            		</#if>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${monthGrowth?if_exists?string("#0.00")}</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${currentMonthAndhraTotalQty?if_exists?string("#0.00")}</fo:block>
		            			</fo:table-cell>
		            			<#if currentMonthAndhraTotalQty == 0>
									<#assign prevPrevMonthGrowth = -100>
								</#if>
		            			<#assign monthsDifference =((currentMonthAndhraTotalQty)-(previousPreviousMonthAndhraTotalQty))>
		            			<#if previousPreviousMonthAndhraTotalQty!=0>
		            				<#assign prevPrevMonthGrowth =((monthsDifference)/(previousPreviousMonthAndhraTotalQty)*100)>
		            			<#else>
				   	    			<#assign prevPrevMonthGrowth = 100>
		            			</#if>
		            			<#if (currentMonthAndhraTotalQty == 0) && (previousPreviousMonthAndhraTotalQty == 0)>
			            			<#assign prevPrevMonthGrowth = 0>
			            		</#if>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${prevPrevMonthGrowth?if_exists?string("#0.00")}</fo:block>
		            			</fo:table-cell>
		            			<#if currentMonthAndhraTotalQty == 0>
									<#assign prevMonthGrowth = -100>
								</#if>
		            			<#assign yearDiff =((currentMonthAndhraTotalQty)-(previousMonthAndhraTotalQty))>
		            			<#if previousMonthAndhraTotalQty!=0>
		            				<#assign prevMonthGrowth =((yearDiff)/(previousMonthAndhraTotalQty)*100)>
		            			<#else>
				   	    			<#assign prevMonthGrowth = 100>
		            			</#if>
		            			<#if (currentMonthAndhraTotalQty == 0) && (previousMonthAndhraTotalQty == 0)>
			            			<#assign prevMonthGrowth = 0>
			            		</#if>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${prevMonthGrowth?if_exists?string("#0.00")}</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${previousPreviousYearAndhraTotalQty?if_exists?string("#0.00")}</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${previousYearAndhraTotalQty?if_exists?string("#0.00")}</fo:block>
		            			</fo:table-cell>
		            			<#if previousYearAndhraTotalQty == 0>
									<#assign yearTotalGrowth = -100 >
								</#if>
		            			<#assign yearDifference =((previousYearAndhraTotalQty)-(previousPreviousYearAndhraTotalQty))>
		            			<#if previousPreviousYearAndhraTotalQty!=0>
		            				<#assign yearTotalGrowth =((yearDifference)/(previousPreviousYearAndhraTotalQty)*100)>
		            			<#else>
				   	    			<#assign yearTotalGrowth = 100>
		            			</#if>
		            			<#if (previousYearAndhraTotalQty == 0) && (previousPreviousYearAndhraTotalQty == 0)>
			            			<#assign yearTotalGrowth = 0>
			            		</#if>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${yearTotalGrowth?if_exists?string("#0.00")}</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${currentYearAndhraTotalQty?if_exists?string("#0.00")}</fo:block>
		            			</fo:table-cell>
		            			<#if currentYearAndhraTotalQty == 0>
									<#assign prevPrevYearGrowth = -100 >
								</#if>
		            			<#assign yearsTotDifference =((currentYearAndhraTotalQty)-(previousPreviousYearAndhraTotalQty))>
		            			<#if previousPreviousYearAndhraTotalQty!=0>
		            				<#assign prevPrevYearGrowth =((yearsTotDifference)/(previousPreviousYearAndhraTotalQty)*100)>
		            			<#else>
				   	    			<#assign prevPrevYearGrowth = 0.00>
		            			</#if>
		            			<#if (currentYearAndhraTotalQty == 0) && (previousPreviousYearAndhraTotalQty == 0)>
			            			<#assign prevPrevYearGrowth = 0>
			            		</#if>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${prevPrevYearGrowth?if_exists?string("#0.00")}</fo:block>
		            			</fo:table-cell>
		            			<#if currentYearAndhraTotalQty == 0>
									<#assign prevYearTotalGrowth = -100 >
								</#if>
		            			<#assign yearToatlDiff =((currentYearAndhraTotalQty)-(previousYearAndhraTotalQty))>
		            			<#if previousYearAndhraTotalQty!=0>
		            				<#assign prevYearTotalGrowth =((yearToatlDiff)/(previousYearAndhraTotalQty)*100)>
		            			<#else>
				   	    			<#assign prevYearTotalGrowth = 100>
		            			</#if>
		            			<#if (currentYearAndhraTotalQty == 0) && (previousYearAndhraTotalQty == 0)>
			            			<#assign prevYearTotalGrowth = 0>
			            		</#if>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${prevYearTotalGrowth?if_exists?string("#0.00")}</fo:block>
		            			</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
			            		<fo:table-cell>
				        			<fo:block font-size="6pt">------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				        		</fo:table-cell>
				        	</fo:table-row>	
						</fo:table-body>
					</fo:table>
				</fo:block>
			</#if>
			<#if (parameters.facilityGroupId == "TELANGANA") || (parameters.facilityGroupId == "")>
				<fo:block  font-size="6pt">
					<fo:table>  
						<fo:table-column column-width="75pt"/>
					   	<fo:table-column column-width="30pt"/>
						<fo:table-column column-width="30pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="32pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="35pt"/>
					   	<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="45pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="35pt"/>
						<#assign previousPreviousTelanganaTotalDayQty = 0>
						<#assign previousDayTelanganaTotalQty = 0>
						<#assign currentDayTelanganaTotalQty =  0>
						<#assign previousPreviousMonthTelanganaTotalQty = 0>
						<#assign previousMonthTelanganaTotalQty = 0>
						<#assign currentMonthTelanganaTotalQty = 0>
						<#assign previousPreviousYearTelanganaTotalQty = 0>
						<#assign previousYearTelanganaTotalQty =0>
						<#assign currentYearTelanganaTotalQty = 0>
						<#assign telanganaShedsDetails=telanganaShedsFinalMap.entrySet()>
						<fo:table-body>
							<#list telanganaShedsDetails as telanganaDetails>
								<fo:table-row>
									<#assign facility = delegator.findOne("Facility", {"facilityId" : telanganaDetails.getKey()}, true)>
									<fo:table-cell>
			            				<fo:block text-align="left" font-size="6pt" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((facility.get("facilityName")))),18)}</fo:block>
			            			</fo:table-cell>
			            			<fo:table-cell>
			            				<fo:block text-align="left" font-size="6pt" keep-together="always">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate,"dd/MM")}</fo:block>
			            			</fo:table-cell>
			            			<#assign previousPreviousTelanganaDayQty = telanganaDetails.getValue().get("DayWise").get(previousPreviousYear)>
			            			<#assign previousPreviousTelanganaTotalDayQty = previousPreviousTelanganaTotalDayQty + previousPreviousTelanganaDayQty>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${previousPreviousTelanganaDayQty?if_exists?string("#0")}</fo:block>
			            			</fo:table-cell>
			            			<#assign previousDayTelanganaQty = telanganaDetails.getValue().get("DayWise").get(previousYear)>
			            			<#assign previousDayTelanganaTotalQty = previousDayTelanganaQty + previousDayTelanganaTotalQty>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${previousDayTelanganaQty?if_exists?string("#0")}</fo:block>
			            			</fo:table-cell>
			            			<#if previousDayTelanganaQty == 0>
										<#assign telGrowth = -100 >
									</#if>
			            			<#assign difference =((previousDayTelanganaQty)-(previousPreviousTelanganaDayQty))>
			            			<#if previousPreviousTelanganaDayQty!=0>
			            				<#assign telGrowth =((difference)/(previousPreviousTelanganaDayQty)*100)>
			            			<#else>
					   	    			<#assign telGrowth = 100>
			            			</#if>
			            			<#if (previousDayTelanganaQty == 0) && (previousPreviousTelanganaDayQty == 0)>
			            				<#assign telGrowth = 0>
			            			</#if>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${telGrowth?if_exists?string("#0.00")}</fo:block>
			            			</fo:table-cell>
			            			<#assign currentDayTelanganaQty =  telanganaDetails.getValue().get("DayWise").get(currentYear)>
			            			<#assign currentDayTelanganaTotalQty =  currentDayTelanganaTotalQty + currentDayTelanganaQty>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${currentDayTelanganaQty?if_exists?string("#0")}</fo:block>
			            			</fo:table-cell>
			            			<#if currentDayTelanganaQty == 0>
										<#assign prevPrevgrowth = -100 >
									</#if>
			            			<#assign difference =((currentDayTelanganaQty)-(previousPreviousTelanganaDayQty))>
			            			<#if previousPreviousTelanganaDayQty!=0>
			            				<#assign prevPrevgrowth =((difference)/(previousPreviousTelanganaDayQty)*100)>
			            			<#else>
					   	    			<#assign prevPrevgrowth = 100>
			            			</#if>
			            			<#if (currentDayTelanganaQty == 0) && (previousPreviousTelanganaDayQty == 0)>
			            				<#assign prevPrevgrowth = 0>
			            			</#if>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${prevPrevgrowth?if_exists?string("#0.00")}</fo:block>
			            			</fo:table-cell>
			            			<#if currentDayTelanganaQty == 0>
										<#assign prevGrowth = -100 >
									</#if>
			            			<#assign difference =((currentDayTelanganaQty)-(previousDayTelanganaQty))>
			            			<#if previousDayTelanganaQty!=0>
			            				<#assign prevGrowth =((difference)/(previousDayTelanganaQty)*100)>
			            			<#else>
					   	    			<#assign prevGrowth = 100>
			            			</#if>
			            			<#if (currentDayTelanganaQty == 0) && (previousDayTelanganaQty == 0)>
			            				<#assign prevGrowth = 0>
			            			</#if>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${prevGrowth?if_exists?string("#0.00")}</fo:block>
			            			</fo:table-cell>
			            			<#assign previouspreviousMonthTelanganaQty = telanganaDetails.getValue().get("MonthWise").get(previousPreviousYear)>
			            			<#assign previousPreviousMonthTelanganaTotalQty = previousPreviousMonthTelanganaTotalQty + previouspreviousMonthTelanganaQty>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${previouspreviousMonthTelanganaQty?if_exists?string("#0.00")}</fo:block>
			            			</fo:table-cell>
			            			<#assign previousMonthTelanganaQty = telanganaDetails.getValue().get("MonthWise").get(previousYear)>
			            			<#assign previousMonthTelanganaTotalQty = previousMonthTelanganaTotalQty + previousMonthTelanganaQty>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${previousMonthTelanganaQty?if_exists?string("#0.00")}</fo:block>
			            			</fo:table-cell>
			            			<#if previousMonthTelanganaQty == 0>
										<#assign monthGrowth = -100 >
									</#if>
			            			<#assign monthDifference =((previousMonthTelanganaQty)-(previouspreviousMonthTelanganaQty))>
			            			<#if previouspreviousMonthTelanganaQty!=0>
			            				<#assign monthGrowth =((monthDifference)/(previouspreviousMonthTelanganaQty)*100)>
			            			<#else>
					   	    			<#assign monthGrowth = 100>
			            			</#if>
			            			<#if (previousMonthTelanganaQty == 0) && (previouspreviousMonthTelanganaQty == 0)>
			            				<#assign monthGrowth = 0>
			            			</#if>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${monthGrowth?if_exists?string("#0.00")}</fo:block>
			            			</fo:table-cell>
			            			<#assign currentMonthTelanganaQty =  telanganaDetails.getValue().get("MonthWise").get(currentYear)>
			            			<#assign currentMonthTelanganaTotalQty = currentMonthTelanganaTotalQty + currentMonthTelanganaQty>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${currentMonthTelanganaQty?if_exists?string("#0.00")}</fo:block>
			            			</fo:table-cell>
			            			<#if currentMonthTelanganaQty == 0>
										<#assign prevPrevMonthGrowth = -100 >
									</#if>
			            			<#assign monthsDifference =((currentMonthTelanganaQty)-(previouspreviousMonthTelanganaQty))>
			            			<#if previouspreviousMonthTelanganaQty!=0>
			            				<#assign prevPrevMonthGrowth =((monthsDifference)/(previouspreviousMonthTelanganaQty)*100)>
			            			<#else>
					   	    			<#assign prevPrevMonthGrowth = 100>
			            			</#if>
			            			<#if (currentMonthTelanganaQty == 0) && (previouspreviousMonthTelanganaQty == 0)>
			            				<#assign prevPrevMonthGrowth = 0>
			            			</#if>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${prevPrevMonthGrowth?if_exists?string("#0.00")}</fo:block>
			            			</fo:table-cell>
			            			<#if currentMonthTelanganaQty == 0>
										<#assign prevMonthGrowth = -100 >
									</#if>
			            			<#assign yearDiff =((currentMonthTelanganaQty)-(previousMonthTelanganaQty))>
			            			<#if previousMonthTelanganaQty!=0>
			            				<#assign prevMonthGrowth =((yearDiff)/(previousMonthTelanganaQty)*100)>
			            			<#else>
					   	    			<#assign prevMonthGrowth = 100>
			            			</#if>
			            			<#if (currentMonthTelanganaQty == 0) && (previousMonthTelanganaQty == 0)>
			            				<#assign prevMonthGrowth = 0>
			            			</#if>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${prevMonthGrowth?if_exists?string("#0.00")}</fo:block>
			            			</fo:table-cell>
			            			<#assign previousPreviousYearTelanganaQty = telanganaDetails.getValue().get("cummulative").get(previousPreviousYear)>
			            			<#assign previousPreviousYearTelanganaTotalQty = previousPreviousYearTelanganaTotalQty + previousPreviousYearTelanganaQty>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${previousPreviousYearTelanganaQty?if_exists?string("#0.00")}</fo:block>
			            			</fo:table-cell>
			            			<#assign previousYearTelanganaQty = telanganaDetails.getValue().get("cummulative").get(previousYear)>
			            			<#assign previousYearTelanganaTotalQty = previousYearTelanganaTotalQty  +  previousYearTelanganaQty>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${previousYearTelanganaQty?if_exists?string("#0.00")}</fo:block>
			            			</fo:table-cell>
			            			<#if previousYearTelanganaQty == 0>
										<#assign yearGrowth = -100 >
									</#if>
			            			<#assign yearDifference =((previousYearTelanganaQty)-(previousPreviousYearTelanganaQty))>
			            			<#if previousPreviousYearTelanganaQty!=0>
			            				<#assign yearGrowth =((yearDifference)/(previousPreviousYearTelanganaQty)*100)>
			            			<#else>
					   	    			<#assign yearGrowth = 100>
			            			</#if>
			            			<#if (previousYearTelanganaQty == 0) && (previousPreviousYearTelanganaQty == 0)>
			            				<#assign yearGrowth = 0>
			            			</#if>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${yearGrowth?if_exists?string("#0.00")}</fo:block>
			            			</fo:table-cell>
			            			<#assign currentYearTelanganaQty =  telanganaDetails.getValue().get("cummulative").get(currentYear)>
			            			<#assign currentYearTelanganaTotalQty = currentYearTelanganaTotalQty + currentYearTelanganaQty>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${currentYearTelanganaQty?if_exists?string("#0.00")}</fo:block>
			            			</fo:table-cell>
			            			<#if currentYearTelanganaQty == 0>
										<#assign prevPrevYearGrowth = -100 >
									</#if>
			            			<#assign yearsDifference =((currentYearTelanganaQty)-(previousPreviousYearTelanganaQty))>
			            			<#if previousPreviousYearTelanganaQty!=0>
			            				<#assign prevPrevYearGrowth =((yearsDifference)/(previousPreviousYearTelanganaQty)*100)>
			            			<#else>
					   	    			<#assign prevPrevYearGrowth = 100>
			            			</#if>
			            			<#if (currentYearTelanganaQty == 0) && (previousPreviousYearTelanganaQty == 0)>
			            				<#assign prevPrevYearGrowth = 0>
			            			</#if>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${prevPrevYearGrowth?if_exists?string("#0.00")}</fo:block>
			            			</fo:table-cell>
			            			<#if currentYearTelanganaQty == 0>
										<#assign prevYearGrowth = -100 >
									</#if>
			            			<#assign yearDiff =((currentYearTelanganaQty)-(previousYearTelanganaQty))>
			            			<#if previousYearTelanganaQty!=0>
			            				<#assign prevYearGrowth =((yearDiff)/(previousYearTelanganaQty)*100)>
			            			<#else>
					   	    			<#assign prevYearGrowth = 100>
			            			</#if>
			            			<#if (currentYearTelanganaQty == 0) && (previousYearTelanganaQty == 0)>
			            				<#assign prevYearGrowth = 0>
			            			</#if>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="6pt" keep-together="always">${prevYearGrowth?if_exists?string("#0.00")}</fo:block>
			            			</fo:table-cell>
								</fo:table-row>
							</#list>
							<fo:table-row>
			            		<fo:table-cell>
				        			<fo:block font-size="6pt">------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				        		</fo:table-cell>
				        	</fo:table-row>	
							<fo:table-row>
								<fo:table-cell>
		            				<fo:block text-align="left" font-size="6pt" keep-together="always">TELANGANA TOTAL:</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell>
		            				<fo:block text-align="left" font-size="6pt" keep-together="always"></fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${previousPreviousTelanganaTotalDayQty?if_exists?string("#0")}</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${previousDayTelanganaTotalQty?if_exists?string("#0")}</fo:block>
		            			</fo:table-cell>
		            			<#if previousDayTelanganaTotalQty == 0>
									<#assign totalGrowth = -100 >
								</#if>
		            			<#assign prevDifference =((previousDayTelanganaTotalQty)-(previousPreviousTelanganaTotalDayQty))>
		            			<#if previousPreviousTelanganaTotalDayQty!=0>
		            				<#assign totalGrowth =((prevDifference)/(previousPreviousTelanganaTotalDayQty)*100)>
		            			<#else>
				   	    			<#assign totalGrowth = 100>
		            			</#if>
		            			<#if (previousDayTelanganaTotalQty == 0) && (previousPreviousTelanganaTotalDayQty == 0)>
			            			<#assign totalGrowth = 0>
			            		</#if>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${totalGrowth?if_exists?string("#0.00")}</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${currentDayTelanganaTotalQty?if_exists?string("#0")}</fo:block>
		            			</fo:table-cell>
		            			<#if currentDayTelanganaTotalQty == 0>
									<#assign prevPrevTotalGrowth = -100 >
								</#if>
		            			<#assign totalDifference =((currentDayTelanganaTotalQty)-(previousPreviousTelanganaTotalDayQty))>
		            			<#if previousPreviousTelanganaTotalDayQty!=0>
		            				<#assign prevPrevTotalGrowth =((totalDifference)/(previousPreviousTelanganaTotalDayQty)*100)>
		            			<#else>
				   	    			<#assign prevPrevTotalGrowth = 100>
		            			</#if>
		            			<#if (currentDayTelanganaTotalQty == 0) && (previousPreviousTelanganaTotalDayQty == 0)>
			            			<#assign prevPrevTotalGrowth = 0>
			            		</#if>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${prevPrevTotalGrowth?if_exists?string("#0.00")}</fo:block>
		            			</fo:table-cell>
		            			<#if currentDayTelanganaTotalQty == 0>
									<#assign prevTotalGrowth = -100 >
								</#if>
		            			<#assign totDifference =((currentDayTelanganaTotalQty)-(previousDayTelanganaTotalQty))>
		            			<#if previousDayTelanganaTotalQty!=0>
		            				<#assign prevTotalGrowth =((totDifference)/(previousDayTelanganaTotalQty)*100)>
		            			<#else>
				   	    			<#assign prevTotalGrowth = 100>
		            			</#if>
		            			<#if (currentDayTelanganaTotalQty == 0) && (previousDayTelanganaTotalQty == 0)>
			            			<#assign prevTotalGrowth = 0>
			            		</#if>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${prevTotalGrowth?if_exists?string("#0.00")}</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${previousPreviousMonthTelanganaTotalQty?if_exists?string("#0.00")}</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${previousMonthTelanganaTotalQty?if_exists?string("#0.00")}</fo:block>
		            			</fo:table-cell>
		            			<#if previousMonthTelanganaQty == 0>
									<#assign monthGrowth = -100 >
								</#if>
		            			<#assign monthDifference =((previousMonthTelanganaQty)-(previousPreviousMonthTelanganaTotalQty))>
		            			<#if previousPreviousMonthTelanganaTotalQty!=0>
		            				<#assign monthGrowth =((monthDifference)/(previousPreviousMonthTelanganaTotalQty)*100)>
		            			<#else>
				   	    			<#assign monthGrowth = 100>
		            			</#if>
		            			<#if (previousMonthTelanganaQty == 0) && (previousPreviousMonthTelanganaTotalQty == 0)>
			            			<#assign monthGrowth = 0>
			            		</#if>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${monthGrowth?if_exists?string("#0.00")}</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${currentMonthTelanganaTotalQty?if_exists?string("#0.00")}</fo:block>
		            			</fo:table-cell>
		            			<#if currentMonthTelanganaTotalQty == 0>
									<#assign prevPrevMonthGrowth = -100 >
								</#if>
		            			<#assign monthsDifference =((currentMonthTelanganaTotalQty)-(previousPreviousMonthTelanganaTotalQty))>
		            			<#if previousPreviousMonthTelanganaTotalQty!=0>
		            				<#assign prevPrevMonthGrowth =((monthsDifference)/(previousPreviousMonthTelanganaTotalQty)*100)>
		            			<#else>
				   	    			<#assign prevPrevMonthGrowth = 100>
		            			</#if>
		            			<#if (currentMonthTelanganaTotalQty == 0) && (previousPreviousMonthTelanganaTotalQty == 0)>
			            			<#assign prevPrevMonthGrowth = 0>
			            		</#if>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${prevPrevMonthGrowth?if_exists?string("#0.00")}</fo:block>
		            			</fo:table-cell>
		            			<#if currentMonthTelanganaTotalQty == 0>
									<#assign prevMonthGrowth = -100 >
								</#if>
		            			<#assign yearDiff =((currentMonthTelanganaTotalQty)-(previousMonthTelanganaTotalQty))>
		            			<#if previousMonthTelanganaTotalQty!=0>
		            				<#assign prevMonthGrowth =((yearDiff)/(previousMonthTelanganaTotalQty)*100)>
		            			<#else>
				   	    			<#assign prevMonthGrowth = 100>
		            			</#if>
		            			<#if (currentMonthTelanganaTotalQty == 0) && (previousMonthTelanganaTotalQty == 0)>
			            			<#assign prevMonthGrowth = 0>
			            		</#if>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${prevMonthGrowth?if_exists?string("#0.00")}</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${previousPreviousYearTelanganaTotalQty?if_exists?string("#0.00")}</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${previousYearTelanganaTotalQty?if_exists?string("#0.00")}</fo:block>
		            			</fo:table-cell>
		            			<#if previousYearTelanganaTotalQty == 0>
									<#assign yearTotalGrowth = -100 >
								</#if>
		            			<#assign yearDifference =((previousYearTelanganaTotalQty)-(previousPreviousYearTelanganaTotalQty))>
		            			<#if previousPreviousYearTelanganaTotalQty!=0>
		            				<#assign yearTotalGrowth =((yearDifference)/(previousPreviousYearTelanganaTotalQty)*100)>
		            			<#else>
				   	    			<#assign yearTotalGrowth = 100>
		            			</#if>
		            			<#if (previousYearTelanganaTotalQty == 0) && (previousPreviousYearTelanganaTotalQty == 0)>
			            			<#assign yearTotalGrowth = 0>
			            		</#if>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${yearTotalGrowth?if_exists?string("#0.00")}</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${currentYearTelanganaTotalQty?if_exists?string("#0.00")}</fo:block>
		            			</fo:table-cell>
		            			<#if currentYearTelanganaTotalQty == 0>
									<#assign prevPrevYearGrowth = -100 >
								</#if>
		            			<#assign yearsTotDifference =((currentYearTelanganaTotalQty)-(previousPreviousYearTelanganaTotalQty))>
		            			<#if previousPreviousYearTelanganaTotalQty!=0>
		            				<#assign prevPrevYearGrowth =((yearsTotDifference)/(previousPreviousYearTelanganaTotalQty)*100)>
		            			<#else>
				   	    			<#assign prevPrevYearGrowth = 100>
		            			</#if>
		            			<#if (currentYearTelanganaTotalQty == 0) && (previousPreviousYearTelanganaTotalQty == 0)>
			            			<#assign prevPrevYearGrowth = 0>
			            		</#if>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${prevPrevYearGrowth?if_exists?string("#0.00")}</fo:block>
		            			</fo:table-cell>
		            			<#if currentYearTelanganaTotalQty == 0>
									<#assign prevYearTotalGrowth = -100 >
								</#if>
		            			<#assign yearTotalDiff =((currentYearTelanganaTotalQty)-(previousYearTelanganaTotalQty))>
		            			<#if previousYearTelanganaTotalQty!=0>
		            				<#assign prevYearTotalGrowth =((yearTotalDiff)/(previousYearTelanganaTotalQty)*100)>
		            			<#else>
				   	    			<#assign prevYearTotalGrowth = 100>
		            			</#if>
		            			<#if (currentYearTelanganaTotalQty == 0) && (previousYearTelanganaTotalQty == 0)>
			            			<#assign prevYearTotalGrowth = 0>
			            		</#if>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="6pt" keep-together="always">${prevYearTotalGrowth?if_exists?string("#0.00")}</fo:block>
		            			</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
			            		<fo:table-cell>
				        			<fo:block font-size="6pt">------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				        		</fo:table-cell>
				        	</fo:table-row>	
						</fo:table-body>
					</fo:table>
				</fo:block>
			</#if>
			<#if (parameters.facilityGroupId == "TELANGANA") || (parameters.facilityGroupId == "") || (parameters.facilityGroupId == "ANDHRA")>
			<fo:block font-size="6pt">
				<fo:table>  
				<fo:table-column column-width="75pt"/>
			   	<fo:table-column column-width="30pt"/>
				<fo:table-column column-width="30pt"/>
				<fo:table-column column-width="35pt"/>
				<fo:table-column column-width="35pt"/>
				<fo:table-column column-width="32pt"/>
				<fo:table-column column-width="35pt"/>
				<fo:table-column column-width="35pt"/>
				<fo:table-column column-width="35pt"/>
				<fo:table-column column-width="35pt"/>
				<fo:table-column column-width="35pt"/>
			   	<fo:table-column column-width="35pt"/>
				<fo:table-column column-width="35pt"/>
				<fo:table-column column-width="35pt"/>
				<fo:table-column column-width="35pt"/>
				<fo:table-column column-width="35pt"/>
				<fo:table-column column-width="35pt"/>
				<fo:table-column column-width="45pt"/>
				<fo:table-column column-width="35pt"/>
				<fo:table-column column-width="35pt"/>
				<#assign previousPreviousGrandDayQty = 0>
				<#assign previousDayGrandQty = 0>
				<#assign currentDayGrandQty =  0>
				<#assign previousPreviousMonthGrandQty = 0>
				<#assign previousMonthGrandQty = 0>
				<#assign currentMonthGrandQty = 0>
				<#assign previousPreviousYearGrandQty = 0>
				<#assign previousYearGrandQty =0>
				<#assign currentYearGrandQty = 0>
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell>
            				<fo:block text-align="left" font-size="6pt" keep-together="always">GRAND TOTAL</fo:block>
            			</fo:table-cell>
            			<fo:table-cell>
            				<fo:block text-align="left" font-size="6pt" keep-together="always"></fo:block>
            			</fo:table-cell>
            			<#assign previousPreviousGrandDayQty = previousPreviousAndhraTotalDayQty + previousPreviousTelanganaTotalDayQty>
            			<fo:table-cell>
            				<fo:block text-align="right" font-size="6pt" keep-together="always">${previousPreviousGrandDayQty?if_exists?string("#0")}</fo:block>
            			</fo:table-cell>
            			<#assign previousDayGrandQty = previousDayAndhraTotalQty + previousDayTelanganaTotalQty>
            			<fo:table-cell>
            				<fo:block text-align="right" font-size="6pt" keep-together="always">${previousDayGrandQty?if_exists?string("#0")}</fo:block>
            			</fo:table-cell>
            			<#if previousDayGrandQty == 0>
							<#assign totalDayGrandGrowth = -100 >
						</#if>
        				<#assign prevDayGrandDifference =((previousDayGrandQty)-(previousPreviousGrandDayQty))>
            			<#if previousPreviousGrandDayQty!=0>
            				<#assign totalDayGrandGrowth =((prevDayGrandDifference)/(previousPreviousGrandDayQty)*100)>
            			<#else>
		   	    			<#assign totalDayGrandGrowth = 100>
            			</#if>
            			<#if (previousDayGrandQty == 0) && (previousPreviousGrandDayQty == 0)>
			            	<#assign totalDayGrandGrowth = 0>
			         	</#if>
            			<fo:table-cell>
            				<fo:block text-align="right" font-size="6pt" keep-together="always">${totalDayGrandGrowth?if_exists?string("#0.00")}</fo:block>
            			</fo:table-cell>
            			<#assign currentDayGrandQty = currentDayAndhraTotalQty + currentDayTelanganaTotalQty>
            			<fo:table-cell>
            				<fo:block text-align="right" font-size="6pt" keep-together="always">${currentDayGrandQty?if_exists?string("#0")}</fo:block>
            			</fo:table-cell>
            			<#if currentDayGrandQty == 0>
							<#assign grandGrowth = -100 >
						</#if>
            			<#assign prevGrandDiff =((currentDayGrandQty)-(previousPreviousGrandDayQty))>
            			<#if previousPreviousGrandDayQty!=0>
            				<#assign grandGrowth =((prevGrandDiff)/(previousPreviousGrandDayQty)*100)>
            			<#else>
		   	    			<#assign grandGrowth = 100>
            			</#if>
            			<#if (currentDayGrandQty == 0) && (previousPreviousGrandDayQty == 0)>
			            	<#assign grandGrowth = 0>
			         	</#if>
            			<fo:table-cell>
            				<fo:block text-align="right" font-size="6pt" keep-together="always">${grandGrowth?if_exists?string("#0.00")}</fo:block>
            			</fo:table-cell>
            			<#if currentDayGrandQty == 0>
							<#assign grandGrowthPer = -100 >
						</#if>
            			<#assign prevGrandTotDiff =((currentDayGrandQty)-(previousDayGrandQty))>
            			<#if previousDayGrandQty!=0>
            				<#assign grandGrowthPer =((prevGrandTotDiff)/(previousDayGrandQty)*100)>
            			<#else>
		   	    			<#assign grandGrowthPer = 100>
            			</#if>
            			<#if (currentDayGrandQty == 0) && (previousDayGrandQty == 0)>
			            	<#assign grandGrowthPer = 0>
			         	</#if>
            			<fo:table-cell>
            				<fo:block text-align="right" font-size="6pt" keep-together="always">${grandGrowthPer?if_exists?string("#0.00")}</fo:block>
            			</fo:table-cell>
            			<#assign previousPreviousMonthGrandQty = previousPreviousMonthAndhraTotalQty + previousPreviousMonthTelanganaTotalQty>
            			<fo:table-cell>
            				<fo:block text-align="right" font-size="6pt" keep-together="always">${previousPreviousMonthGrandQty?if_exists?string("#0.00")}</fo:block>
            			</fo:table-cell>
            			<#assign previousMonthGrandQty = previousMonthAndhraTotalQty + previousMonthTelanganaTotalQty>
            			<fo:table-cell>
            				<fo:block text-align="right" font-size="6pt" keep-together="always">${previousMonthGrandQty?if_exists?string("#0.00")}</fo:block>
            			</fo:table-cell>
            			<#if previousMonthGrandQty == 0>
							<#assign grandMonthPercentage = -100 >
						</#if>
            			<#assign grandMonthDifferences =((previousMonthGrandQty)-(previousPreviousMonthGrandQty))>
            			<#if previousPreviousMonthGrandQty!=0>
            				<#assign grandMonthPercentages =((grandMonthDifferences)/(previousPreviousMonthGrandQty)*100)>
            			<#else>
		   	    			<#assign grandMonthPercentages = 100>
            			</#if>
            			<#if (previousMonthGrandQty == 0) && (previousPreviousMonthGrandQty == 0)>
			            	<#assign grandMonthPercentages = 0>
			         	</#if>
            			<fo:table-cell>
            				<fo:block text-align="right" font-size="6pt" keep-together="always">${grandMonthPercentages?if_exists?string("#0.00")}</fo:block>
            			</fo:table-cell>
            			<#assign currentMonthGrandQty = currentMonthAndhraTotalQty + currentMonthTelanganaTotalQty>
            			<fo:table-cell>
            				<fo:block text-align="right" font-size="6pt" keep-together="always">${currentMonthGrandQty?if_exists?string("#0.00")}</fo:block>
            			</fo:table-cell>
            			<#if currentMonthGrandQty == 0>
							<#assign grandMonthPercentage = -100 >
						</#if>
            			<#assign grandMonthDiff =((currentMonthGrandQty)-(previousPreviousMonthGrandQty))>
            			<#if previousPreviousMonthGrandQty!=0>
            				<#assign grandMonthPercentage =((grandMonthDiff)/(previousPreviousMonthGrandQty)*100)>
            			<#else>
		   	    			<#assign grandMonthPercents = 0.00>
            			</#if>
            			<#if (currentMonthGrandQty == 0) && (previousPreviousMonthGrandQty == 0)>
			            	<#assign grandMonthPercentage = 0>
			         	</#if>
            			<fo:table-cell>
            				<fo:block text-align="right" font-size="6pt" keep-together="always">${grandMonthPercents?if_exists?string("#0.00")}</fo:block>
            			</fo:table-cell>
            			<#if currentMonthGrandQty == 0>
							<#assign grandMonthsPercents = -100 >
						</#if>
            			<#assign grandMonthsDiffences =((currentMonthGrandQty)-(previousMonthGrandQty))>
            			<#if previousMonthGrandQty!=0>
            				<#assign grandMonthsPercents =((grandMonthsDiffences)/(previousMonthGrandQty)*100)>
            			<#else>
		   	    			<#assign grandMonthsPercents = 100>
            			</#if>
            			<#if (currentMonthGrandQty == 0) && (previousMonthGrandQty == 0)>
			            	<#assign grandMonthsPercents = 0>
			         	</#if>
            			<fo:table-cell>
            				<fo:block text-align="right" font-size="6pt" keep-together="always">${grandMonthsPercents?if_exists?string("#0.00")}</fo:block>
            			</fo:table-cell>
            			<#assign previousPreviousYearGrandQty = previousPreviousYearAndhraTotalQty + previousPreviousYearTelanganaTotalQty>
            			<fo:table-cell>
            				<fo:block text-align="right" font-size="6pt" keep-together="always">${previousPreviousYearGrandQty?if_exists?string("#0.00")}</fo:block>
            			</fo:table-cell>
            			<#assign previousYearGrandQty = previousYearAndhraTotalQty + previousYearTelanganaTotalQty>
            			<fo:table-cell>
            				<fo:block text-align="right" font-size="6pt" keep-together="always">${previousYearGrandQty?if_exists?string("#0.00")}</fo:block>
            			</fo:table-cell>
            			<#if previousYearGrandQty == 0>
							<#assign grandYearPercents = -100 >
						</#if>
            			<#assign grandYearDiffences =((previousYearGrandQty)-(previousPreviousYearGrandQty))>
            			<#if previousPreviousYearGrandQty!=0>
            				<#assign grandYearPercents =((grandYearDiffences)/(previousPreviousYearGrandQty)*100)>
            			<#else>
		   	    			<#assign grandYearPercents = 100>
            			</#if>
            			<#if (previousYearGrandQty == 0) && (previousPreviousYearGrandQty == 0)>
			            	<#assign grandYearPercents = 0>
			         	</#if>
            			<fo:table-cell>
            				<fo:block text-align="right" font-size="6pt" keep-together="always">${grandYearPercents?if_exists?string("#0.00")}</fo:block>
            			</fo:table-cell>
            			<#assign currentYearGrandQty = currentYearAndhraTotalQty + currentYearTelanganaTotalQty>
            			<fo:table-cell>
            				<fo:block text-align="right" font-size="6pt" keep-together="always">${currentYearGrandQty?if_exists?string("#0.00")}</fo:block>
            			</fo:table-cell>
            			<#if currentYearGrandQty == 0>
							<#assign grandYearsPercents = -100 >
						</#if>
            			<#assign grandYearsDiffences =((currentYearGrandQty)-(previousPreviousYearGrandQty))>
            			<#if previousPreviousYearGrandQty!=0>
            				<#assign grandYearsPercents =((grandYearsDiffences)/(previousPreviousYearGrandQty)*100)>
            			<#else>
		   	    			<#assign grandYearsPercents = 100>
            			</#if>
            			<#if (currentYearGrandQty == 0) && (previousPreviousYearGrandQty == 0)>
			            	<#assign grandYearsPercents = 0>
			         	</#if>
            			<fo:table-cell>
            				<fo:block text-align="right" font-size="6pt" keep-together="always">${grandYearsPercents?if_exists?string("#0.00")}</fo:block>
            			</fo:table-cell>
            			<#if previousYearGrandQty == 0>
							<#assign grandYearPercentages = -100 >
						</#if>
            			<#assign grandYearDiff =((previousYearGrandQty)-(previousYearGrandQty))>
            			<#if previousYearGrandQty!=0>
            				<#assign grandYearPercentages =((grandYearDiff)/(previousYearGrandQty)*100)>
            			<#else>
		   	    			<#assign grandYearPercentages = 100>
            			</#if>
            			<#if (previousYearGrandQty == 0) && (previousYearGrandQty == 0)>
			            	<#assign grandYearPercentages = 0>
			         	</#if>
            			<fo:table-cell>
            				<fo:block text-align="right" font-size="6pt" keep-together="always">${grandYearPercentages?if_exists?string("#0.00")}</fo:block>
            			</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
	            		<fo:table-cell>
		        			<fo:block font-size="6pt">------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		        		</fo:table-cell>
		        	</fo:table-row>	
				</fo:table-body>
			</fo:table>
			</fo:block>
			</#if>
			<fo:block  font-size="6pt">
			<fo:table>
			<fo:table-column column-width="90pt"/>
			<fo:table-column column-width="50pt"/>
			<fo:table-column column-width="20pt"/>
			<fo:table-column column-width="60pt"/>
			<fo:table-column column-width="60pt"/>
	        <fo:table-body>
	        	<#if (parameters.facilityGroupId == "")>
	               <fo:table-row>
	               		<fo:table-cell>
	                    	<fo:block text-align="left" font-size="7pt" keep-together="always"></fo:block>
	                    </fo:table-cell>
	               		<fo:table-cell>
	                    	<fo:block text-align="left" font-size="6pt" keep-together="always">CONTRIBUTION</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell>
	                    	<fo:block text-align="left" font-size="7pt" keep-together="always">:</fo:block>
	                    </fo:table-cell>
	                </fo:table-row>
	                <fo:table-row>
	              		 <fo:table-cell>
	               	 		 <fo:block linefeed-treatment="preserve" font-size="6pt">&#xA;</fo:block>
	              		 </fo:table-cell>
	               </fo:table-row>
               </#if>
               <#if (parameters.facilityGroupId == "")>
            	 	<fo:table-row>
	            	 	<fo:table-cell>
	                    	<fo:block text-align="left" font-size="7pt" keep-together="always"></fo:block>
	                    </fo:table-cell>
	               		<fo:table-cell>
	               	  		<fo:block text-align="left" font-size="6pt" keep-together="always">ANDHRA</fo:block>
	               		</fo:table-cell>
	               		<fo:table-cell>
	               	  		<fo:block text-align="left" font-size="6pt" keep-together="always">:</fo:block>
	               		</fo:table-cell>
	               		<fo:table-cell>
	               	  		<fo:block text-align="right" font-size="6pt" keep-together="always">${currentDayAndhraTotalQty?if_exists?string('#0.00')}</fo:block>
	               		</fo:table-cell>
	               		<#if currentDayAndhraTotalQty == 0>
							<#assign grandTelanganaAndhraPercentage = -100 >
						</#if>
	               		<#if  currentDayTelanganaTotalQty!=0>
							<#assign grandTelanganaAndhraPercentage =((currentDayAndhraTotalQty/currentDayTelanganaTotalQty)*(100))>
						<#else>
							<#assign grandTelanganaAndhraPercentage = 100>
						</#if> 
						<#if (currentDayAndhraTotalQty == 0) && (currentDayTelanganaTotalQty == 0)>
			            	<#assign grandTelanganaAndhraPercentage = 0>
			         	</#if>
	               		<fo:table-cell>
	               	  		<fo:block text-align="right" font-size="6pt" keep-together="always">${grandTelanganaAndhraPercentage?if_exists?string('#0.00')}%</fo:block>
	               		</fo:table-cell>
               		</fo:table-row>
             	</#if>
	            	<fo:table-row>
	              		 <fo:table-cell>
	               	 		 <fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block>
	              		 </fo:table-cell>
	               </fo:table-row>
			   <#if (parameters.facilityGroupId == "")>
	    			<fo:table-row>
		    			<fo:table-cell>
	                    	<fo:block text-align="left" font-size="6pt" keep-together="always"></fo:block>
	                    </fo:table-cell>
	               		<fo:table-cell>
	               	  		<fo:block text-align="left" font-size="6pt" keep-together="always">TELANGANA</fo:block>
	               		</fo:table-cell>
	               		<fo:table-cell>
	               	  		<fo:block text-align="left" font-size="6pt" keep-together="always">:</fo:block>
	               		</fo:table-cell>
	               		<fo:table-cell>
	               	  		<fo:block text-align="right" font-size="6pt" keep-together="always">${currentDayTelanganaTotalQty?if_exists?string('#0.00')}</fo:block>
	               		</fo:table-cell>
	               		<#if currentDayTelanganaTotalQty == 0>
							<#assign grandTelanganaPercentage = -100 >
						</#if>
	               		<#if  currentDayGrandQty!=0>
							<#assign grandTelanganaPercentage =((currentDayTelanganaTotalQty/currentDayGrandQty)*(100))>
						<#else>
							<#assign grandTelanganaPercentage = 100>
						</#if>
						<#if (currentDayTelanganaTotalQty == 0) && (currentDayGrandQty == 0)>
			            	<#assign grandTelanganaPercentage = 0>
			         	</#if>
	               		<fo:table-cell>
	               	  		<fo:block text-align="right" font-size="6pt" keep-together="always">${grandTelanganaPercentage?if_exists?string('#0.00')}%</fo:block>
	               		</fo:table-cell>
               		</fo:table-row>
              </#if>
              <#if (parameters.facilityGroupId == "")> 
	               <fo:table-row>
	               		<fo:table-cell>
	               	 		<fo:block linefeed-treatment="preserve" font-size="6pt">&#xA;</fo:block>
	              		</fo:table-cell>
	               </fo:table-row>
		    	   <fo:table-row>
		    	   		<fo:table-cell>
	                    	<fo:block text-align="left" font-size="6pt" keep-together="always"></fo:block>
	                    </fo:table-cell>
	               		<fo:table-cell>
	               	  		<fo:block text-align="left" font-size="6pt" keep-together="always">STATE TOTAL</fo:block>
	               		</fo:table-cell>
	               		<fo:table-cell>
	               	  		<fo:block text-align="left" font-size="6pt" keep-together="always">:</fo:block>
	               		</fo:table-cell>
	               		<fo:table-cell>
	               	  		<fo:block text-align="right" font-size="6pt" keep-together="always">${currentDayGrandQty?if_exists?string('#0.00')}</fo:block>
	               		</fo:table-cell>
	               </fo:table-row>
               </#if>
			</fo:table-body>
		</fo:table> 
		</fo:block>
		<fo:block font-size="5pt">VST_ASCII-012 VST_ASCII-027VST_ASCII-080</fo:block>
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
</#if>
</fo:root>
</#escape>