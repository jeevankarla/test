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
                <fo:region-body margin-top="0.5in"/>
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
  <fo:page-sequence master-reference="main">
  	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="5pt">
		<fo:block font-size="5pt" >VST_ASCII-015 VST_ASCII-027VST_ASCII-077</fo:block>
		<fo:block text-align="left" white-space-collapse="false" font-size="5pt" keep-together="always">&#160;&#160;&#160;&#160;																																																														DAY-WISE MPF.HYDERABAD MILK RECEIPTS PERIOD FROM :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate,"dd-MM-yyyy")} to ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate,"dd-MM-yyyy")} (LAKHS IN LITRES)</fo:block>
        <fo:block font-size="5pt">---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
        <fo:block font-size="5pt">
			<fo:table font-size="5pt">
			<fo:table-column column-width="30pt"/>
			<#list currentDateKeysList as currDayKey>
			<fo:table-column column-width="23pt"/>
			</#list>
			<fo:table-column column-width="24pt"/>
			<fo:table-column column-width="24pt"/>
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell>
							<fo:block text-align="center" font-size="5pt" keep-together="always">SHED/UNION</fo:block>
						</fo:table-cell>
						<#list currentDateKeysList as currDayKey>
							<fo:table-cell>
								<fo:block text-align="right" font-size="5pt" keep-together="always">${currDayKey}</fo:block>
							</fo:table-cell>
						</#list>
						<fo:table-cell>
							<fo:block text-align="right" font-size="5pt" keep-together="always">CUR</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block text-align="right" font-size="5pt" keep-together="always">PREV</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell>
							<fo:block text-align="center" font-size="5pt" keep-together="always">NAME</fo:block>
						</fo:table-cell>
						<#list currentDateKeysList as currDayKey>
							<fo:table-cell>
								<fo:block text-align="right" font-size="5pt" keep-together="always"></fo:block>
							</fo:table-cell>
						</#list>
						<fo:table-cell>
							<fo:block text-align="right" font-size="5pt" keep-together="always">Month</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block text-align="right" font-size="5pt" keep-together="always">Month</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
	    </fo:block>
        <fo:block font-size="5pt">---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
 	</fo:static-content>
    <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" font-size="5pt">
	 	<fo:block font-size="5pt">
			<fo:table>
			<fo:table-column column-width="30pt"/>
			<#list currentDateKeysList as currDayKey>
			<fo:table-column column-width="23pt"/>
			</#list>
			<fo:table-column column-width="24pt"/>
			<fo:table-column column-width="24pt"/>
			<#assign mccShedWiseDetailList = mccTypeShedMap.entrySet()>
	        <fo:table-body>
	        <#list mccShedWiseDetailList as mccShedWiseList>
				<#assign mccShedsList = mccShedWiseList.getValue()>
				<#if mccShedWiseList.getKey() == "FEDERATION">
    			<#list andhraSheds as shed>
		            <fo:table-row>
		            	<#assign facility = delegator.findOne("Facility", {"facilityId" : shed}, true)>
		            	<fo:table-cell>
		            		<fo:block text-align="left" font-size="5pt" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((facility.get("facilityName")))),10)}</fo:block>
		            	</fo:table-cell>   
		            	<#assign shedCurrDaysTotQty= 0>
		            	<#list currentDateKeysList as currDayKey>
		            		<#assign shedCurrDayQty= 0>
		            		<#if (currentShedFinalMap?has_content)>
		            			<#if (currentShedFinalMap.get(shed)?has_content) &&( currentShedFinalMap.get(shed).get(currDayKey)?has_content)>
		            				<#assign shedCurrDayQty = currentShedFinalMap.get(shed).get(currDayKey)>
		            			</#if>
		            			<#if (currentShedFinalMap.get(shed)?has_content) && (currentShedFinalMap.get(shed).get("CUR.YEAR"))?has_content>
		            				<#assign shedCurrDaysTotQty = (currentShedFinalMap.get(shed).get("CUR.YEAR"))>
			            		</#if>
		            		<#else>
		            			<#assign shedCurrDayQty= 0>
		            		</#if>
		            		<fo:table-cell>
		            			<fo:block text-align="right" font-size="5pt" keep-together="always">${shedCurrDayQty?if_exists?string("#0.00")}</fo:block>
		            		</fo:table-cell>
		            	</#list>
		            	<fo:table-cell>
		            		<fo:block text-align="right" font-size="5pt" keep-together="always">${shedCurrDaysTotQty?if_exists?string("#0.00")}</fo:block>
		            	</fo:table-cell>
		            	<#assign shedPrevDaysTotQty= 0>
		            	<#list currentDateKeysList as currDayKey>
		            		<#assign shedPrevDayQty= 0>
		            		<#if (previousShedFinalMap?has_content)>
		            			<#if (previousShedFinalMap.get(shed)?has_content) && (previousShedFinalMap.get(shed).get(currDayKey))?has_content>
		            				<#assign shedPrevDayQty= previousShedFinalMap.get(shed).get(currDayKey)>
		            			</#if>
		            			<#if (previousShedFinalMap.get(shed)?has_content) && (previousShedFinalMap.get(shed).get("PRE.YEAR"))?has_content>
		            				<#assign shedPrevDaysTotQty= (previousShedFinalMap.get(shed).get("PRE.YEAR"))>
			            		</#if>
		            		<#else>
		            			<#assign shedPrevDaysTotQty= 0>
		            		</#if>
		            	</#list>
		            	<fo:table-cell>
		            		<fo:block text-align="right" font-size="5pt" keep-together="always">${shedPrevDaysTotQty?if_exists?string("#0.00")}</fo:block>
		            	</fo:table-cell>
            		</fo:table-row>
            	</#list>
            		<fo:table-row>
	   	    			<fo:table-cell>
							<fo:block  font-size="5pt">---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
						</fo:table-cell>
		   	    	</fo:table-row>
		   	    	<#assign andhraCurrTotal = 0>
		   	    	<#assign andhraPrevTotal = 0>
		   	    	<#if (currentAndhraFinalTotMonthQtyMap?has_content) && (currentAndhraFinalTotMonthQtyMap.get("CUR.YEAR")?has_content)>
		   	    		<#assign andhraCurrTotal = currentAndhraFinalTotMonthQtyMap.get("CUR.YEAR")>
		   	    	</#if>
		   	    	<#if (previousAndhraFinalTotMonthQtyMap?has_content) && (previousAndhraFinalTotMonthQtyMap.get("PRE.YEAR")?has_content)>
			   	    	<#assign andhraPrevTotal = previousAndhraFinalTotMonthQtyMap.get("PRE.YEAR")>
		   	    	</#if>
			   	    <#if ((andhraCurrTotal+andhraPrevTotal)!=0)>	
						<fo:table-row>
		   	    			<fo:table-cell>
			            		<fo:block text-align="left" font-size="5pt" keep-together="always">ANDHRA TOT:</fo:block>
							</fo:table-cell>
							<#assign andhraShedsCurrMonthQty= 0>
			            	<#list currentDateKeysList as currDayKey>
			            		<#assign andhraShedsCurrDaysQty= 0>
			            		<#if (currentAndhraFinalTotMonthQtyMap?has_content) && (currentAndhraFinalTotMonthQtyMap.get(currDayKey))?has_content>
			            			<#assign andhraShedsCurrDaysQty =(currentAndhraFinalTotMonthQtyMap.get(currDayKey))>
			            		</#if>
			            		<#if (currentAndhraFinalTotMonthQtyMap?has_content) &&(currentAndhraFinalTotMonthQtyMap.get("CUR.YEAR")?has_content)>
			            			<#assign andhraShedsCurrMonthQty = (currentAndhraFinalTotMonthQtyMap.get("CUR.YEAR"))>
			            		</#if>
			            		<fo:table-cell>
			            			<fo:block text-align="right" font-size="5pt" keep-together="always">${(andhraShedsCurrDaysQty)?if_exists?string("#0.00")}</fo:block>
			            		</fo:table-cell>
			            	</#list>
			            	<fo:table-cell>
			            		<fo:block text-align="right" font-size="5pt" keep-together="always">${andhraShedsCurrMonthQty?if_exists?string("#0.00")}</fo:block>
			            	</fo:table-cell>
			            	<#assign andhraShedsPrevMonthQty= 0>
			            	<#list currentDateKeysList as currDayKey>
			            		<#assign shedCurrTotDayQty= 0>
			            		<#if (previousAndhraFinalTotMonthQtyMap?has_content)  && (previousAndhraFinalTotMonthQtyMap.get(currDayKey))?has_content>
			            			<#assign andhraShedsPrevDaysQty =(previousAndhraFinalTotMonthQtyMap.get(currDayKey))>
			            		</#if>
			            		<#if (previousAndhraFinalTotMonthQtyMap?has_content) && (previousAndhraFinalTotMonthQtyMap.get("PRE.YEAR")?has_content)>
			            			<#assign andhraShedsPrevMonthQty = (previousAndhraFinalTotMonthQtyMap.get("PRE.YEAR"))>
			            		</#if>
			            	</#list>
			            	<fo:table-cell>
			            		<fo:block text-align="right" font-size="5pt" keep-together="always">${andhraShedsPrevMonthQty?if_exists?string("#0.00")}</fo:block>
			            	</fo:table-cell>
				            <#assign andhraShedsPrevMonthQty=0>
				   	    </fo:table-row>
	            		<fo:table-row>
	            			<fo:table-cell>
								<fo:block  font-size="5pt">---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
							</fo:table-cell>
			   	    	</fo:table-row>
			   	    </#if>
            		<#list telanganaSheds as shed>
			            <fo:table-row>
			            	<#assign facility = delegator.findOne("Facility", {"facilityId" : shed}, true)>
			            	<fo:table-cell>
			            		<fo:block text-align="left" font-size="5pt" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((facility.get("facilityName")))),10)}</fo:block>
			            	</fo:table-cell>   
			            	<#assign shedCurrDaysTotQty= 0>
			            	<#list currentDateKeysList as currDayKey>
			            		<#assign shedCurrDayQty= 0>
			            		<#if (currentShedFinalMap.get(shed)?has_content) && (currentShedFinalMap.get(shed).get(currDayKey)?has_content)>
			            			<#assign shedCurrDayQty = currentShedFinalMap.get(shed).get(currDayKey)>
			            		</#if>
			            		<#if (currentShedFinalMap.get(shed)?has_content) && (currentShedFinalMap.get(shed).get("CUR.YEAR")?has_content)>
			            			<#assign shedCurrDaysTotQty = (currentShedFinalMap.get(shed).get("CUR.YEAR"))>
			            		</#if>
			            		<fo:table-cell>
			            			<fo:block text-align="right" font-size="5pt" keep-together="always">${shedCurrDayQty?if_exists?string("#0.00")}</fo:block>
			            		</fo:table-cell>
			            	</#list>
			            	<fo:table-cell>
			            		<fo:block text-align="right" font-size="5pt" keep-together="always">${shedCurrDaysTotQty?if_exists?string("#0.00")}</fo:block>
			            	</fo:table-cell>
				            <#assign shedPrevDaysTotQty= 0>
			            	<#list currentDateKeysList as currDayKey>
			            		<#assign shedPrevDayQty= 0>
			            		<#if (previousShedFinalMap.get(shed)?has_content) && (previousShedFinalMap.get(shed).get(currDayKey))?has_content>
			            			<#assign shedPrevDayQty= previousShedFinalMap.get(shed).get(currDayKey)>
			            		</#if>
			            		<#if (previousShedFinalMap.get(shed)?has_content) && (previousShedFinalMap.get(shed).get("PRE.YEAR"))?has_content>
			            			<#assign shedPrevDaysTotQty= (previousShedFinalMap.get(shed).get("PRE.YEAR"))>
			            		</#if>	
			            	</#list>
			            	<fo:table-cell>
			            		<fo:block text-align="right" font-size="5pt" keep-together="always">${shedPrevDaysTotQty?if_exists?string("#0.00")}</fo:block>
			            	</fo:table-cell>
		            	</fo:table-row>
            		</#list> 
	        			<fo:table-row>
			   	    		<fo:table-cell>
								<fo:block  font-size="5pt">---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
							</fo:table-cell>
			   	    	</fo:table-row>
			   	    	<#assign telanganaCurrTotal = 0>
			   	    	<#assign telanganaPrevTotal = 0>
			   	    	<#assign telanganaCurrTotal = currentTelanganaFinalTotMonthQtyMap.get("CUR.YEAR")>
			   	    	<#assign telanganaPrevTotal = previousTelanganaFinalTotMonthQtyMap.get("PRE.YEAR")>
			   	    	<#if ((telanganaCurrTotal+telanganaPrevTotal)!=0)>	
		            		<fo:table-row>
	   	    					<fo:table-cell>
		            				<fo:block text-align="left" font-size="4pt" keep-together="always">TELANGANA TOT:</fo:block>
								</fo:table-cell>
							<#assign telanganaShedsCurrMonthQty= 0>
			            	<#list currentDateKeysList as currDayKey>
			            		<#assign telanganaShedsCurrDaysQty= 0>
			            		<#if ((currentTelanganaFinalTotMonthQtyMap)?has_content && (currentTelanganaFinalTotMonthQtyMap.get(currDayKey)?has_content))>
			            			<#assign telanganaShedsCurrDaysQty =(currentTelanganaFinalTotMonthQtyMap.get(currDayKey))>
			            		</#if>
			            		<#if (currentTelanganaFinalTotMonthQtyMap.get("CUR.YEAR"))?has_content>
			            			<#assign telanganaShedsCurrMonthQty = (currentTelanganaFinalTotMonthQtyMap.get("CUR.YEAR"))>
				            		</#if>
			            		<fo:table-cell>
			            			<fo:block text-align="right" font-size="5pt" keep-together="always">${(telanganaShedsCurrDaysQty)?if_exists?string("#0.00")}</fo:block>
			            		</fo:table-cell>
			            	</#list>
			            	<fo:table-cell>
			            		<fo:block text-align="right" font-size="5pt" keep-together="always">${telanganaShedsCurrMonthQty?if_exists?string("#0.00")}</fo:block>
			            	</fo:table-cell>
			            	<#assign telanganaShedsPrevMonthQty= 0>
			            	<#list currentDateKeysList as currDayKey>
			            		<#assign telanganaShedsPrevDaysQty= 0>
			            		<#if ((previousTelanganaFinalTotMonthQtyMap)?has_content && (previousTelanganaFinalTotMonthQtyMap.get(currDayKey)?has_content))>
			            			<#assign telanganaShedsPrevDaysQty =(previousTelanganaFinalTotMonthQtyMap.get(currDayKey))>
			            		</#if>
			            		<#if (previousTelanganaFinalTotMonthQtyMap.get("PRE.YEAR"))?has_content>
			            			<#assign telanganaShedsPrevMonthQty = (previousTelanganaFinalTotMonthQtyMap.get("PRE.YEAR"))>
			            		</#if>
			            	</#list>
			            	<fo:table-cell>
			            		<fo:block text-align="right" font-size="5pt" keep-together="always">${telanganaShedsPrevMonthQty?if_exists?string("#0.00")}</fo:block>
			            	</fo:table-cell>
				            <#assign telanganaShedsPrevMonthQty=0>
			   	    	</fo:table-row>
	            		<fo:table-row>
	            			<fo:table-cell>
								<fo:block  font-size="5pt">---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
							</fo:table-cell>
			   	    	</fo:table-row>
			   	    	</#if>
		   	    	</#if>
		   	    	<#if (mccShedWiseList.getKey())!= "FEDERATION">
			   	    	<#list mccShedsList as shed>
				   	    	<#assign shedCurrTotQty= 0>
			            	<#list currentDateKeysList as currDayKey>
			            		<#assign shedCurrQty= 0>
			            			<#if (currentShedFinalMap.get(shed)?has_content) &&( currentShedFinalMap.get(shed).get(currDayKey)?has_content)>
			            				<#assign shedCurrQty = currentShedFinalMap.get(shed).get(currDayKey)>
			            			</#if>
			            			<#if (currentShedFinalMap.get(shed)?has_content)>
			            				<#assign shedCurrTotQty = (currentShedFinalMap.get(shed).get("CUR.YEAR"))>
		            				</#if>
			            	</#list>
			            	<#assign shedPrevTotQty= 0>
				            	<#list currentDateKeysList as currDayKey>
				            		<#assign shedPrevQty= 0>
				            		<#if (previousShedFinalMap.get(shed)?has_content) && (previousShedFinalMap.get(shed).get(currDayKey))?has_content>
				            			<#assign shedPrevQty= previousShedFinalMap.get(shed).get(currDayKey)>
				            		</#if>
				            		<#if (previousShedFinalMap.get(shed)?has_content)>
				            			<#assign shedPrevTotQty= (previousShedFinalMap.get(shed).get("PRE.YEAR"))>
				            		</#if>
				            	</#list>
				        	<#if ((shedCurrTotQty+shedPrevTotQty)!=0)>
					   	    	 <fo:table-row>
					            	<#assign facility = delegator.findOne("Facility", {"facilityId" : shed}, true)>
					            	<fo:table-cell>
					            		<fo:block text-align="left" font-size="5pt" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((facility.get("facilityName")))),10)}</fo:block>
					            	</fo:table-cell>   
					            	<#assign shedCurrDaysTotQty= 0>
					            	<#list currentDateKeysList as currDayKey>
					            		<#assign shedCurrDayQty= 0>
					            		<#if (currentShedFinalMap.get(shed)?has_content) &&( currentShedFinalMap.get(shed).get(currDayKey)?has_content)>
					            			<#assign shedCurrDayQty = currentShedFinalMap.get(shed).get(currDayKey)>
					            		</#if>
					            		<#assign shedCurrDaysTotQty = (currentShedFinalMap.get(shed).get("CUR.YEAR"))>
					            		<fo:table-cell>
					            			<fo:block text-align="right" font-size="5pt" keep-together="always">${shedCurrDayQty?if_exists?string("#0.00")}</fo:block>
					            		</fo:table-cell>
				            		</#list>
				            		<fo:table-cell>
				            			<fo:block text-align="right" font-size="5pt" keep-together="always">${shedCurrDaysTotQty?if_exists?string("#0.00")}</fo:block>
				            		</fo:table-cell>
				            		<#assign shedPrevDaysTotQty= 0>
				            		<#list currentDateKeysList as currDayKey>
				            			<#assign shedPrevDayQty= 0>
				            			<#if (previousShedFinalMap.get(shed)?has_content) && (previousShedFinalMap.get(shed).get(currDayKey))?has_content>
				            					<#assign shedPrevDayQty= previousShedFinalMap.get(shed).get(currDayKey)>
				            			</#if>
				            			<#if (previousShedFinalMap.get(shed)?has_content)>
				            				<#assign shedPrevDaysTotQty= (previousShedFinalMap.get(shed).get("PRE.YEAR"))>
				            			</#if>
				            		</#list>
				            		<fo:table-cell>
				            			<fo:block text-align="right" font-size="5pt" keep-together="always">${shedPrevDaysTotQty?if_exists?string("#0.00")}</fo:block>
				            		</fo:table-cell>
		            			</fo:table-row>
		            			<fo:table-row>
					   	    		<fo:table-cell>
										<fo:block  font-size="5pt">---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
									</fo:table-cell>
					   	    	</fo:table-row>
					   	    </#if>	
			   	    	</#list>
		   	    	</#if>
		   	    	<#assign mccTypeTot =CurrentMccTotalMap.get(mccShedWiseList.getKey())>
						<#assign fedCurrentDayTotQty=0>	
		            	<#list currentDateKeysList as currDayKey>
		            		<#assign fedCurrentDayQty=0>
		            		<#if mccTypeTot?has_content>
		            		<#if (mccTypeTot.get(currDayKey))?has_content>
		            			<#assign fedCurrentDayQty=(mccTypeTot.get(currDayKey))>
    						</#if>
    						</#if>
		            			<#assign fedCurrentDayTotQty=(mccTypeTot.get("CUR.YEAR"))>
			       		</#list>
			           <#assign fedPreviousDayTotQty=0>
			            <#if previousMccTotalMap.get(mccShedWiseList.getKey())?has_content>
				            <#assign mccTypeTot =previousMccTotalMap.get(mccShedWiseList.getKey())>
							<#assign fedPreviousDayTotQty=0>
							<#assign fedPreviousDayTotQty=(mccTypeTot.get("PRE.YEAR"))>	
			            	<#list currentDateKeysList as currDayKey>
			            		<#assign fedPreviousDayQty=0>
			            		<#if mccTypeTot?has_content>
				            		<#if (mccTypeTot.get(currDayKey))?has_content>
				            			<#assign fedPreviousDayQty=(mccTypeTot.get(currDayKey))>
		    						</#if>
	    						</#if>
				            </#list>
			            </#if>
			            <#if ((fedCurrentDayTotQty+fedPreviousDayTotQty)!=0)>	
				             <fo:table-row>
			            		<fo:table-cell>
				            		<fo:block text-align="left" font-size="4pt" keep-together="always">${mccShedWiseList.getKey()} TOT:</fo:block>
				            	</fo:table-cell>
				            	<#assign mccTypeTot =CurrentMccTotalMap.get(mccShedWiseList.getKey())>
				            	<#assign fedCurrDayTotQty=0>
				            	<#if mccTypeTot?has_content>
				            		<#assign fedCurrDayTotQty=(mccTypeTot.get("CUR.YEAR"))>	
				            	</#if>
				            	<#list currentDateKeysList as currDayKey>
				            		<#assign fedCurrDayQty=0>
				            		<#if mccTypeTot?has_content>
				            		<#if (mccTypeTot.get(currDayKey))?has_content>
				            			<#assign fedCurrDayQty=(mccTypeTot.get(currDayKey))>
									</#if>
									</#if>
					            	<fo:table-cell>
					            		<fo:block text-align="right" font-size="5pt" keep-together="always">${(fedCurrDayQty)?if_exists?string('##0.00')}</fo:block>
					            	</fo:table-cell>
					       		</#list>
					       		<fo:table-cell>
					            		<fo:block text-align="right" font-size="5pt" keep-together="always">${(fedCurrDayTotQty)?if_exists?string('##0.00')}</fo:block>
					            </fo:table-cell>
					           <#assign mccTypeTot={}>
					           <#assign fedPrevDayTotQty=0>
					            <#if previousMccTotalMap.get(mccShedWiseList.getKey())?has_content>
						            <#assign mccTypeTot =previousMccTotalMap.get(mccShedWiseList.getKey())>
									<#assign fedPrevDayTotQty=0>
									<#assign fedPrevDayTotQty=(mccTypeTot.get("PRE.YEAR"))>	
					            	<#list currentDateKeysList as currDayKey>
					            		<#assign fedPrevDayQty=0>
					            		<#if mccTypeTot?has_content>
						            		<#if (mccTypeTot.get(currDayKey))?has_content>
						            			<#assign fedPrevDayQty=(mccTypeTot.get(currDayKey))>
				    						</#if>
			    						</#if>
						            </#list>
				             	</#if>
								<fo:table-cell>
				            		<fo:block text-align="right" font-size="5pt" keep-together="always">${(fedPrevDayTotQty)?if_exists?string('##0.00')}</fo:block>
				            	</fo:table-cell>
				           		 <#assign fedPrevDayTotQty=0>
							</fo:table-row>
							<fo:table-row>
		    					<fo:table-cell>
									<fo:block font-size="5pt">---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
								</fo:table-cell>
		   	    			</fo:table-row>
		   	    		</#if>
		   	    	</#list>
		   	    	</fo:table-body>
				</fo:table> 
			</fo:block>
		<fo:block  font-size="5pt">
			<fo:table>
			<fo:table-column column-width="30pt"/>
			<#list currentDateKeysList as currDayKey>
			<fo:table-column column-width="23pt"/>
			</#list>
			<fo:table-column column-width="24pt"/>
			<fo:table-column column-width="24pt"/>
			<#assign grandPrevTotDayQty=0>
	        <fo:table-body>
             	<fo:table-row>
           			<fo:table-cell>
			            <fo:block text-align="left" font-size="5pt" keep-together="always">GRAND TOT:</fo:block>
			        </fo:table-cell>
			       <#assign grandCurrTotalQty = 0>
			       <#assign grandCurrTotalQty =CurrentGrandTotalMap.get("CUR.YEAR")>
			       <#list currentDateKeysList as currDayKey>
				       <#assign grandCurrQty = 0>
				       <#if CurrentGrandTotalMap.get(currDayKey)?has_content>
				       		<#assign grandCurrQty = CurrentGrandTotalMap.get(currDayKey)>
				       	</#if>
		        		<fo:table-cell>
							<fo:block text-align="right" font-size="5pt" keep-together="always">${grandCurrQty?if_exists?string('#0.00')}</fo:block>
						</fo:table-cell>
					</#list>
					<fo:table-cell>
						<fo:block text-align="right" font-size="5pt" keep-together="always">${grandCurrTotalQty?if_exists?string('#0.00')}</fo:block>
					</fo:table-cell>
				   <#assign grandPrevTotalQty = 0>
				   <#if previousGrandTotalMap?has_content>
				   		<#assign grandPrevTotalQty = previousGrandTotalMap.get("PRE.YEAR")>	
			       		<#list currentDateKeysList as currDayKey>
				       		<#assign grandPrevQty = 0>
				       		<#if previousGrandTotalMap.get(currDayKey)?has_content>
				       			<#assign grandPrevQty = previousGrandTotalMap.get(currDayKey)>
				       		</#if>
						</#list>
					<#else>
						<#assign grandPrevTotalQty = 0.00>
					</#if>
					<fo:table-cell>
						<fo:block text-align="right" font-size="5pt" keep-together="always">${grandPrevTotalQty?if_exists?string('#0.00')}</fo:block>
					</fo:table-cell>
				</fo:table-row>
	            <fo:table-row>
	               <fo:table-cell>
	               	  <fo:block font-size="5pt">---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	               </fo:table-cell>
	            </fo:table-row>
	            </fo:table-body>
				</fo:table> 
			</fo:block>
	        <fo:block  font-size="5pt">
			<fo:table>
			<fo:table-column column-width="80pt"/>
			<fo:table-column column-width="40pt"/>
			<fo:table-column column-width="10pt"/>
			<fo:table-column column-width="50pt"/>
	        <fo:table-body>
               <fo:table-row>
               		<fo:table-cell>
                    	<fo:block text-align="left" font-size="7pt" keep-together="always"></fo:block>
                    </fo:table-cell>
               		<fo:table-cell>
                    	<fo:block text-align="left" font-size="5pt" keep-together="always">CONTRIBUTION</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                    	<fo:block text-align="left" font-size="7pt" keep-together="always">:</fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
              		 <fo:table-cell>
               	 		 <fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block>
              		 </fo:table-cell>
               </fo:table-row>
               <#if (CurrentMccTotalMap.get("FEDERATION").get("CUR.YEAR"))!=0>
               <#assign grandAndhraQty = 0>
               <#assign grandAndhraQty = currentAndhraFinalTotMonthQtyMap.get("CUR.YEAR")>
               		<#assign andhraContribution = ((grandAndhraQty/(CurrentMccTotalMap.get("FEDERATION").get("CUR.YEAR")))*(100))>
	    		<#else>
	    			<#assign andhraContribution = 100>
	    		</#if>
            	 <fo:table-row>
            	 	<fo:table-cell>
                    	<fo:block text-align="left" font-size="7pt" keep-together="always"></fo:block>
                    </fo:table-cell>
               		<fo:table-cell>
               	  		<fo:block text-align="left" font-size="5pt" keep-together="always">ANDHRA</fo:block>
               		</fo:table-cell>
               		<fo:table-cell>
               	  		<fo:block text-align="left" font-size="5pt" keep-together="always">:</fo:block>
               		</fo:table-cell>
               		<fo:table-cell>
               	  		<fo:block text-align="left" font-size="5pt" keep-together="always">${andhraContribution?if_exists?string('#0')}%</fo:block>
               		</fo:table-cell>
               </fo:table-row>
               <fo:table-row>
              		 <fo:table-cell>
               	 		 <fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block>
              		 </fo:table-cell>
               </fo:table-row>
               <#if (CurrentMccTotalMap.get("FEDERATION").get("CUR.YEAR"))!=0>
               <#assign grandTelanganaQty = 0>
               <#assign grandTelanganaQty = currentTelanganaFinalTotMonthQtyMap.get("CUR.YEAR")>
               		<#assign TelanganaContribution = ((grandTelanganaQty/(CurrentMccTotalMap.get("FEDERATION").get("CUR.YEAR")))*(100))>
	    		<#else>
	    			<#assign TelanganaContribution = 100>
	    		</#if>
	    		<fo:table-row>
	    			<fo:table-cell>
                    	<fo:block text-align="left" font-size="7pt" keep-together="always"></fo:block>
                    </fo:table-cell>
               		<fo:table-cell>
               	  		<fo:block text-align="left" font-size="5pt" keep-together="always">TELANGANA</fo:block>
               		</fo:table-cell>
               		<fo:table-cell>
               	  		<fo:block text-align="left" font-size="5pt" keep-together="always">:</fo:block>
               		</fo:table-cell>
               		<fo:table-cell>
               	  		<fo:block text-align="left" font-size="5pt" keep-together="always">${TelanganaContribution?if_exists?string('#0')}%</fo:block>
               		</fo:table-cell>
               </fo:table-row>	
               <fo:table-row>
              		 <fo:table-cell>
               	 		 <fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block>
              		 </fo:table-cell>
               </fo:table-row>
               <#if CurrentGrandTotalMap.get("CUR.YEAR")!=0>
               <#assign grandFederationQty = 0>
               <#assign grandFederationQty = CurrentMccTotalMap.get("FEDERATION").get("CUR.YEAR")>
               		<#assign federationContribution = ((grandFederationQty/CurrentGrandTotalMap.get("CUR.YEAR"))*(100))>
	    		<#else>
	    			<#assign federationContribution = 100>
	    		</#if>
	    		<fo:table-row>
	    			<fo:table-cell>
                    	<fo:block text-align="left" font-size="7pt" keep-together="always"></fo:block>
                    </fo:table-cell>
               		<fo:table-cell>
               	  		<fo:block text-align="left" font-size="5pt" keep-together="always">FEDERATION</fo:block>
               		</fo:table-cell>
               		<fo:table-cell>
               	  		<fo:block text-align="left" font-size="5pt" keep-together="always">:</fo:block>
               		</fo:table-cell>
               		<fo:table-cell>
               	  		<fo:block text-align="left" font-size="5pt" keep-together="always">${federationContribution?if_exists?string('#0')}%</fo:block>
               		</fo:table-cell>
               </fo:table-row>
               <fo:table-row>
              		 <fo:table-cell>
               	 		 <fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block>
              		 </fo:table-cell>
               </fo:table-row>
               <#if CurrentGrandTotalMap.get("CUR.YEAR")!=0>
               <#assign grandOtherQty = 0>
               <#assign grandOtherQty = CurrentMccTotalMap.get("OTHERS").get("CUR.YEAR")>
               		<#assign otherContribution = ((grandOtherQty/CurrentGrandTotalMap.get("CUR.YEAR"))*(100))>
	    		<#else>
	    			<#assign otherContribution = 100>
	    		</#if>
	    		<fo:table-row>
	    		<fo:table-cell>
               	  		<fo:block text-align="left" font-size="5pt" keep-together="always"></fo:block>
               		</fo:table-cell>
               		<fo:table-cell>
               	  		<fo:block text-align="left" font-size="5pt" keep-together="always">OTHERS</fo:block>
               		</fo:table-cell>
               		<fo:table-cell>
               	  		<fo:block text-align="left" font-size="5pt" keep-together="always">:</fo:block>
               		</fo:table-cell>
               		<fo:table-cell>
               	  		<fo:block text-align="left" font-size="5pt" keep-together="always">${otherContribution?if_exists?string('#0')}%</fo:block>
               		</fo:table-cell>
               </fo:table-row>
               <fo:table-row>
              		 <fo:table-cell>
               	 		 <fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block>
              		 </fo:table-cell>
               </fo:table-row>
			</fo:table-body>
		</fo:table> 
	</fo:block>
    <fo:block font-size="5pt">VST_ASCII-018</fo:block>
    </fo:flow>
    </fo:page-sequence>
</#if>
</fo:root>
</#escape>