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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in">
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
  <fo:page-sequence master-reference="main">
  	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="5pt">
		<fo:block font-size="5pt" >VST_ASCII-015 VST_ASCII-027VST_ASCII-077</fo:block>
		<fo:block text-align="left" white-space-collapse="false" font-size="5pt" keep-together="always">&#160;&#160;&#160;&#160;																																																														DAY-WISE MPF.HYDERABAD MILK RECEIPTS PERIOD FROM :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate,"dd-MM-yyyy")} to ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate,"dd-MM-yyyy")} (LAKHS IN LITRES)</fo:block>
        <fo:block font-size="5pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
        <fo:block font-size="3pt">
			<fo:table font-size="3pt">
			<fo:table-column column-width="33pt"/>
			<#list currentDateKeysList as currDayKey>
			<fo:table-column column-width="20pt"/>
			</#list>
			<fo:table-column column-width="21pt"/>
			<fo:table-column column-width="21pt"/>
			<fo:table-column column-width="21pt"/>
			<fo:table-column column-width="21pt"/>
			<fo:table-column column-width="21pt"/>
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell>
							<fo:block font-size="3pt" keep-together="always">SHED/UNION</fo:block>
						</fo:table-cell>
						<#list currentDateKeysList as currDayKey>
							<fo:table-cell>
								<fo:block text-align="center" white-space-collapse="false" font-size="3pt" keep-together="always">${currDayKey}</fo:block>
							</fo:table-cell>
						</#list>
						<fo:table-cell>
							<fo:block text-align="right" font-size="3pt" keep-together="always">CURYEAR</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block text-align="right" font-size="3pt" keep-together="always">PREYEAR</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block text-align="right" font-size="3pt" keep-together="always">DIFF</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block text-align="right" font-size="3pt" keep-together="always">%GE.GW</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block text-align="right" font-size="5pt" keep-together="always">.</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
	    </fo:block>
        <fo:block font-size="5pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
 	</fo:static-content>
    <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" font-size="5pt">
	 	<fo:block font-size="5pt">
			<fo:table>
			<fo:table-column column-width="33pt"/>
			<#list currentDateKeysList as currDayKey>
			<fo:table-column column-width="20pt"/>
			</#list>
			<fo:table-column column-width="23pt"/>
			<fo:table-column column-width="22pt"/>
			<fo:table-column column-width="23pt"/>
			<fo:table-column column-width="25pt"/>
			<#assign mccShedWiseDetailList = mccTypeShedMap.entrySet()>
	        <fo:table-body>
	            <#list mccShedWiseDetailList as mccShedWiseList>
					<#assign mccShedsList = mccShedWiseList.getValue()>
        			<#list mccShedsList as shed>
        			<#assign shedCurrTotQty= 0>
		            	<#list currentDateKeysList as currDayKey>
		            		<#assign shedCurrQty= 0>
		            		<#if (currentShedFinalMap.get(shed)?has_content) &&( currentShedFinalMap.get(shed).get(currDayKey)?has_content)>
		            			<#assign shedCurrQty = currentShedFinalMap.get(shed).get(currDayKey)>
		            		</#if>
		            		<#assign shedCurrTotQty = (currentShedFinalMap.get(shed).get("CUR.YEAR"))>
		            	</#list>
		            	<#assign shedPrevTotQty= 0>
			            	<#list currentDateKeysList as currDayKey>
			            		<#assign shedPrevQty= 0>
			            		<#if (previousShedFinalMap.get(shed)?has_content) && (previousShedFinalMap.get(shed).get(currDayKey))?has_content>
			            			<#assign shedPrevQty= previousShedFinalMap.get(shed).get(currDayKey)>
			            		</#if>
			            		<#assign shedPrevTotQty= (previousShedFinalMap.get(shed).get("PRE.YEAR"))>
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
			            		<#assign shedPrevDaysTotQty= (previousShedFinalMap.get(shed).get("PRE.YEAR"))>
			            	</#list>
			            	<fo:table-cell>
			            		<fo:block text-align="right" font-size="5pt" keep-together="always">${shedPrevDaysTotQty?if_exists?string("#0.00")}</fo:block>
			            	</fo:table-cell>
			            	<#assign difference =((shedCurrDaysTotQty)-(shedPrevDaysTotQty))>
			            	<fo:table-cell>
			            		<fo:block text-align="right" font-size="5pt" keep-together="always">${(difference)?if_exists?string("#0.00")}</fo:block>
			            	</fo:table-cell>
			            	<#if shedCurrDaysTotQty == 0>
					   	    	<#assign percentage = -100 >
					   	    </#if>
			                <#if shedPrevDaysTotQty!=0>
			   	    			<#assign percentage =((difference/shedPrevDaysTotQty)*(100))>
			   	    		<#else>
			   	    			<#assign percentage = 100>
			   	    		</#if> 
				   	    		<fo:table-cell>
									<fo:block text-align="right" font-size="5pt" keep-together="always">${percentage?if_exists?string('##0.00')}</fo:block>
								</fo:table-cell>
	            		</fo:table-row>
	            		</#if>
	            	</#list>
						<#assign mccTypeTot =tempCurrentMccMap.get(mccShedWiseList.getKey())>
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
			            <#if tempPreviousMccMap.get(mccShedWiseList.getKey())?has_content>
				            <#assign mccTypeTot =tempPreviousMccMap.get(mccShedWiseList.getKey())>
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
								<fo:block font-size="5pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
							</fo:table-cell>
			   	    	</fo:table-row>
			             <fo:table-row>
		            		<fo:table-cell>
			            		<fo:block text-align="left" font-size="5pt" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((mccShedWiseList.getKey()))),5)} TOT:</fo:block>
			            	</fo:table-cell>
			            	<#assign mccTypeTot =tempCurrentMccMap.get(mccShedWiseList.getKey())>   
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
				            <#if tempPreviousMccMap.get(mccShedWiseList.getKey())?has_content>
					            <#assign mccTypeTot =tempPreviousMccMap.get(mccShedWiseList.getKey())>
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
				            <#assign mccQtyDiff =(fedCurrDayTotQty)-(fedPrevDayTotQty)>
				            <fo:table-cell>
				            	<fo:block text-align="right" font-size="5pt" keep-together="always">${mccQtyDiff?if_exists?string('##0.00')}</fo:block>
				            </fo:table-cell>
				            <#if fedCurrDayTotQty == 0>
					   	    	<#assign percentage = -100 >
					   	    </#if>
				            <#if  fedPrevDayTotQty!=0>
		    					<#assign mccQtyPer =((mccQtyDiff/fedPrevDayTotQty)*(100))>
		    				<#else>
		    					<#assign mccQtyPer = 100>
		    				</#if> 
				            <fo:table-cell>
				            		<fo:block text-align="right" font-size="5pt" keep-together="always">${mccQtyPer?if_exists?string('##0.00')}</fo:block>
				            </fo:table-cell>
				            <#assign fedPrevDayTotQty=0>
						</fo:table-row>
						<fo:table-row>
		    				<fo:table-cell>
								<fo:block font-size="5pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
							</fo:table-cell>
		   	    		</fo:table-row>
		   	    	</#if>	
	            </#list>
	         </fo:table-body>
		  	</fo:table> 
		</fo:block>
  		<fo:block  font-size="5pt">
			<fo:table>
			<fo:table-column column-width="33pt"/>
			<#list currentDateKeysList as currDayKey>
			<fo:table-column column-width="20pt"/>
			</#list>
			<fo:table-column column-width="23pt"/>
			<fo:table-column column-width="22pt"/>
			<fo:table-column column-width="23pt"/>
			<fo:table-column column-width="25pt"/>
			<#assign grandPrevTotDayQty=0>
	        <fo:table-body>
             	<fo:table-row>
           			<fo:table-cell>
			            <fo:block text-align="left" font-size="5pt" keep-together="always">GRAND TOT:</fo:block>
			        </fo:table-cell>
			       <#assign grandCurrTotalQty = 0>
			       <#assign grandCurrTotalQty =tempCurrentGrndMap.get("CUR.YEAR")>
			       <#list currentDateKeysList as currDayKey>
				       <#assign grandCurrQty = 0>
				       <#assign grandCurrQty = tempCurrentGrndMap.get(currDayKey)>
		        		<fo:table-cell>
							<fo:block text-align="right" font-size="5pt" keep-together="always">${grandCurrQty?if_exists?string('#0.00')}</fo:block>
						</fo:table-cell>
					</#list>
					<fo:table-cell>
						<fo:block text-align="right" font-size="5pt" keep-together="always">${grandCurrTotalQty?if_exists?string('#0.00')}</fo:block>
					</fo:table-cell>
				   <#assign grandPrevTotalQty = 0>
				    <#assign grandPrevTotalQty = tempPreviousGrndMap.get("PRE.YEAR")>	
			       <#list currentDateKeysList as currDayKey>
				       <#assign grandPrevQty = 0>
				       <#assign grandPrevQty = tempPreviousGrndMap.get(currDayKey)>
					</#list>
					<fo:table-cell>
						<fo:block text-align="right" font-size="5pt" keep-together="always">${grandPrevTotalQty?if_exists?string('#0.00')}</fo:block>
					</fo:table-cell>
					<#assign grandTotDiff =((grandCurrTotalQty)-(grandPrevTotalQty))>
					<fo:table-cell>
						<fo:block text-align="right" font-size="5pt" keep-together="always">${grandTotDiff?if_exists?string('#0.00')}</fo:block>
					</fo:table-cell>
					<#if grandCurrTotalQty == 0>
						<#assign percentage = -100 >
					</#if>
					<#if  grandPrevTotalQty!=0>
						<#assign grandPercentage =((grandTotDiff/grandPrevTotalQty)*(100))>
					<#else>
						<#assign grandPercentage = 100>
					</#if> 
					<fo:table-cell>
						<fo:block text-align="right" font-size="5pt" keep-together="always">${grandPercentage?if_exists?string('#0.00')}</fo:block>
					</fo:table-cell>
				</fo:table-row>
	            <fo:table-row>
	               <fo:table-cell>
	               	  <fo:block font-size="5pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
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