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
                <fo:region-body margin-top="0.4in"/>
                <fo:region-before extent="0.5in"/>
                <fo:region-after extent="0.5in"/>
            </fo:simple-page-master>
       </fo:layout-master-set>
    ${setRequestAttribute("OUTPUT_FILENAME", "DayWiseShedWiseMilkReceipts.txt")}
<#if errorMessage?has_content>
<fo:page-sequence master-reference="main">
   <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
      <fo:block font-size="14pt">
      	${errorMessage}.
   	  </fo:block>
   </fo:flow>
</fo:page-sequence>        
<#else> 
  <#if currentShedFinalMap?has_content> 
  <fo:page-sequence master-reference="main">
  	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="5pt">
		<fo:block font-size="5pt" >VST_ASCII-015 VST_ASCII-027VST_ASCII-103</fo:block>
		<fo:block text-align="left" white-space-collapse="false" font-size="5pt" keep-together="always">&#160;&#160;&#160;&#160;																																																														DAY-WISE MPF.HYDERABAD MILK RECEIPTS PERIOD FROM :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate,"dd-MM-yyyy")} to ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate,"dd-MM-yyyy")} (LAKHS IN LITRES)</fo:block>
        <fo:block font-size="5pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
        <fo:block font-size="5pt">
			<fo:table font-size="5pt">
			<fo:table-column column-width="45pt"/>
			<#list dateKeysList as currDayKey>
			<fo:table-column column-width="26pt"/>
			</#list>
			<fo:table-column column-width="28pt"/>
			<fo:table-column column-width="26pt"/>
			<fo:table-column column-width="26pt"/>
			<fo:table-column column-width="27pt"/>
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell>
							<fo:block text-align="left" font-size="5pt" keep-together="always">SHED/UNION NAME</fo:block>
						</fo:table-cell>
						<#list dateKeysList as currDayKey>
							<fo:table-cell>
								<fo:block text-align="right" font-size="5pt" keep-together="always">${currDayKey}</fo:block>
							</fo:table-cell>
						</#list>
						<fo:table-cell>
							<fo:block text-align="right" font-size="5pt" keep-together="always">CUR.YEAR</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block text-align="right" font-size="5pt" keep-together="always">PRE.YEAR</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block text-align="right" font-size="5pt" keep-together="always">DIFRNCE</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block text-align="right" font-size="5pt" keep-together="always">%GE.GRTH</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
	    </fo:block>
        <fo:block font-size="5pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
 	</fo:static-content>
    <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" font-size="5pt">
	 	<fo:block font-size="5pt">
			<fo:table>
			<fo:table-column column-width="45pt"/>
			<#list dateKeysList as currDayKey>
			<fo:table-column column-width="26pt"/>
			</#list>
			<#assign pageBreak = "N">
			<fo:table-column column-width="28pt"/>
			<fo:table-column column-width="26pt"/>
			<fo:table-column column-width="26pt"/>
			<fo:table-column column-width="30pt"/>
	        <fo:table-body>
	        <#assign mccShedWiseDetailList = currentShedFinalMap.entrySet()>
			<#list mccShedWiseDetailList as mccShedWiseList>
        		<#assign dayWiseDetails = mccShedWiseList.getValue()>
        		<#assign unitKey = mccShedWiseList.getKey()>
        		<#assign facility = {}>
		        <#assign facility = delegator.findOne("Facility", {"facilityId" : unitKey}, true)>
		        <#assign facilityTypeId = ''>
        		<#if facility?has_content>
        			<#assign facilityTypeId = facility.facilityTypeId>
        		</#if>
	        		<#if facilityTypeId == "SHED" && (!mccTypes.contains(unitKey))>
	        			<fo:table-row>
            				<fo:table-cell>
	        			 	<fo:block font-size="5pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	        				</fo:table-cell>
	        			</fo:table-row>	
	        		</#if>
	        		<#if mccTypes?has_content && mccTypes.contains(mcckey)>
		        		<fo:table-row>
            				<fo:table-cell>
            					<#if pageBreak == "Y">
	        			 		<fo:block font-size="5pt">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	        					</#if>
	        				</fo:table-cell>
                 			<#if "OTHERS"!= mcckey && pageBreak == "Y">		
             					<fo:table-cell>
             						<fo:block page-break-after="always"></fo:block>
             					</fo:table-cell>
                 			</#if>
                 			<#assign pageBreak = "N">		
		        		</fo:table-row>
			         </#if>	
			        	<#assign totShedCurrTot = 0>
			        		<#list dateKeysList as currDayKey>
	            				<#assign shedCurrentMonthsQty = 0>
		            			<#if (currentShedFinalMap.get(unitKey))?has_content>
		            				<#assign currentShedUnitValues = {}>
		            				<#assign tempValues = {}>
		            				<#assign tempValues = currentShedFinalMap.get(unitKey)>
		            				<#if (tempValues.get(currDayKey))?has_content>
			            				<#assign currentShedUnitValues = (tempValues.get(currDayKey))>
				            			<#if currentShedUnitValues?has_content>
					            			<#assign shedCurrentMonthsQty=(currentShedFinalMap.get(unitKey).get(currDayKey))>
					            			<#assign shedCurrTotMonthsQty=(shedCurrTotMonthsQty)+(shedCurrentMonthsQty)>
			            				</#if>
			            			</#if>
		            			</#if>
		            			<#assign totShedCurrTot = totShedCurrTot + shedCurrentMonthsQty>
							</#list>	
			        		<#assign prevMonthsQty=(prevYearMapData.get(unitKey))>
			        		
			        		<#if (totShedCurrTot == 0 && prevMonthsQty == 0)>
			        		
			        		<#else>
			        		<#assign pageBreak = "Y">
		            		<fo:table-row>
				            	<fo:table-cell>
		            				<#assign mcckey = mccShedWiseList.getKey()>
		            				<fo:block text-align="left" font-size="5pt" keep-together="always"><#if facilityTypeId =="UNIT" || facilityTypeId =="SHED"> ${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((facility.get("facilityName")))),15)}<#else>  <#if (mccShedWiseList.getKey()?has_content) && (mccShedWiseList.getKey() !="dayTotals") >${mccShedWiseList.getKey()?if_exists}<#if mccTypes?has_content && mccTypes.contains(mcckey)> TOTAL:</#if>  </#if> <#if mccShedWiseList.getKey() =="dayTotals" >GRAND TOTAL :</#if></#if></fo:block>
		            			</fo:table-cell>                                                                      
		            				<#assign shedCurrTotMonthsQty = 0>                                                                                                                                                                                                                                                                                                                      
		            			<#list dateKeysList as currDayKey>
		            				<#assign shedCurrentMonthsQty = 0>
			            			<#if (currentShedFinalMap.get(unitKey))?has_content>
			            				<#assign currentShedUnitValues = {}>
			            				<#assign tempValues = {}>
			            				<#assign tempValues = currentShedFinalMap.get(unitKey)>
			            				<#if (tempValues.get(currDayKey))?has_content>
				            				<#assign currentShedUnitValues = (tempValues.get(currDayKey))>
					            			<#if currentShedUnitValues?has_content>
						            			<#assign shedCurrentMonthsQty=(currentShedFinalMap.get(unitKey).get(currDayKey))>
						            			<#assign shedCurrTotMonthsQty=(shedCurrTotMonthsQty)+(shedCurrentMonthsQty)>
				            				</#if>
				            			</#if>
			            			</#if>
			            			<fo:table-cell>
			            				<fo:block text-align="right" font-size="5pt" keep-together="always">${shedCurrentMonthsQty?if_exists?string('##0.00')}</fo:block>
			            			</fo:table-cell>
								</#list>
									<fo:table-cell>
					            		<fo:block text-align="right" font-size="5pt" keep-together="always">${shedCurrTotMonthsQty?if_exists?string('##0.00')}</fo:block>
					            	</fo:table-cell>
					            	<#assign shedPreviousMonthsQty=(prevYearMapData.get(unitKey))>
					            	<fo:table-cell>
					            		<fo:block text-align="right" font-size="5pt" keep-together="always">${shedPreviousMonthsQty?if_exists?string('##0.00')}</fo:block>
					            	</fo:table-cell>
					            	<#assign difference = 0>
					            	<#assign difference =((shedCurrTotMonthsQty)-(shedPreviousMonthsQty))>
						   	    	<fo:table-cell>
										<fo:block text-align="right" font-size="5pt" keep-together="always">${difference?if_exists?string('#0.00')}</fo:block>
						   	    	</fo:table-cell>
						   	    	<#if shedCurrTotMonthsQty == 0>
				 						<#assign percentage = -100 >
				 					</#if>	
				 					<#if  shedPreviousMonthsQty!=0>
				 						<#assign percentage =((difference/shedPreviousMonthsQty)*(100))>
				 					<#else>
				 						<#assign percentage = 100>
									</#if> 
					   	    		<fo:table-cell>
										<fo:block text-align="right" font-size="5pt" keep-together="always">${percentage?if_exists?string('##0.00')}</fo:block>
									</fo:table-cell>
		            		</fo:table-row>
		            		</#if>
		            		<#if facilityTypeId == "SHED">
			        			<fo:table-row>
		            				<fo:table-cell>
			        			 	<fo:block font-size="5pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			        				</fo:table-cell>
			        			</fo:table-row>	
		        			</#if>
    		</#list>
			 <fo:table-row>
				<fo:table-cell>
			 	<fo:block font-size="5pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------VST_ASCII-018</fo:block>
				</fo:table-cell>
			</fo:table-row>		
         </fo:table-body>
	  	</fo:table> 
	</fo:block>
    </fo:flow>
    </fo:page-sequence>
    </#if>
</#if>
</fo:root>
</#escape>