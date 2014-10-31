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
    ${setRequestAttribute("OUTPUT_FILENAME", "MonthWiseShedWiseKgFatAccount.txt")}
<#if errorMessage?has_content>
<fo:page-sequence master-reference="main">
   <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
      <fo:block font-size="14pt">
              ${errorMessage}.
   	  </fo:block>
   </fo:flow>
</fo:page-sequence>        
<#else> 
<#if (noofDays >= 3)>
   <fo:page-sequence master-reference="main">
  	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="5pt">
  			<fo:block font-size="5pt">VST_ASCII-015 VST_ASCII-027VST_ASCII-077</fo:block>
  			<fo:block text-align="left" white-space-collapse="false" font-size="5pt" keep-together="always">&#160;&#160;&#160;&#160;						MONTH-WISE MPF.HYDERABAD MILK RECEIPTS (KG-FAT) PERIOD FROM :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate,"dd-MM-yyyy")} to ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate,"dd-MM-yyyy")} (TONES)</fo:block>
            <fo:block font-size="5pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            <fo:block font-size="5pt">
			<fo:table font-size="5pt">
				<fo:table-column column-width="20pt"/>
				<fo:table-column column-width="55pt"/>
				<#list currMonthKeyList as currMonthKey>
				<fo:table-column column-width="32pt"/>
				</#list>
				<fo:table-column column-width="34pt"/>
				<fo:table-column column-width="34pt"/>
				<fo:table-column column-width="34pt"/>
				<fo:table-column column-width="34pt"/>
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell>
							<fo:block text-align="left" font-size="5pt" keep-together="always">CODE</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block text-align="left" font-size="5pt" keep-together="always">SHED/UNION NAME</fo:block>
						</fo:table-cell>
						<#list currMonthKeyList as currMonthKey>
						<fo:table-cell>
							<fo:block text-align="right" font-size="5pt" keep-together="always">${currMonthKey}</fo:block>
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
         <fo:block font-size="5pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
 	</fo:static-content>
      <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" font-size="5pt">
	 	<fo:block  font-size="5pt">
			<fo:table>
				<fo:table-column column-width="12pt"/>
				<fo:table-column column-width="63pt"/>
				<#list currMonthKeyList as currMonthKey>
				<fo:table-column column-width="32pt"/>
				</#list>
				<fo:table-column column-width="34pt"/>
				<fo:table-column column-width="34pt"/>
				<fo:table-column column-width="34pt"/>
				<fo:table-column column-width="34pt"/>
				<fo:table-body>
		        <#assign mccShedWiseDetailList = mccTypeShedMap.entrySet()>
	            	<#list mccShedWiseDetailList as mccShedWiseList>
						<#assign mccShedsList = mccShedWiseList.getValue()>
            			<#list mccShedsList as shed>
	            			<#assign currentYearMapValues = {}>
	            			<#assign prevYearMapValues = {}>
	            			<#assign currentValues = 0>
	            			<#assign previousValues = 0>
	            			<#assign currentYearMapValues = shedCurrentFinalMap.get(shed)>
	            			<#assign currentMonthValues = {}>
	            			<#if currentYearMapValues?has_content>
			        			<#assign currentMonthValues = currentYearMapValues.entrySet()>
			        			<#list currentMonthValues as currMonthValues>
			        			<#if currMonthValues?has_content>
			        			<#assign currDetails = 0>
			        					<#assign currDetails = currMonthValues.getValue().get("kgFat")>
			        					<#assign currentValues = currentValues+currDetails>
			        				</#if>
			        			</#list>
			        		</#if>	
	            			<#assign prevYearMapValues = shedPreviousFinalMap.get(shed)>
	            			<#assign previousMonthValues = {}>
	            			<#if prevYearMapValues?has_content>
	            			<#assign previousMonthValues = prevYearMapValues.entrySet()>
	            			<#list previousMonthValues as prevMonthValues>
	            			<#assign prevDetails = 0>
	            			<#if prevMonthValues?has_content>
	            				<#assign prevDetails =  prevMonthValues.getValue().get("kgFat")>
	            				<#assign previousValues = previousValues+prevDetails>
	            				</#if>
	            			</#list>
	            			</#if>
	            			<#if ((currentValues+previousValues)!=0)>
		            		<#assign facility = delegator.findOne("Facility", {"facilityId" : shed}, true)>
		            			<fo:table-row>
				                    <fo:table-cell>
				                        <fo:block text-align="right" font-size="5pt">${facility.mccCode?if_exists}</fo:block>
				                    </fo:table-cell>
			            			<fo:table-cell>
			            				<fo:block text-align="left" text-indent='2pt' font-size="5pt" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((facility.get("facilityName")))),20)}</fo:block>
			            			</fo:table-cell>
			           				<#assign shedCurrTotMonthsQty = 0>	
		            				<#list currMonthKeyList as currMonthKey>
		            					<#assign shedCurrMonthsQty=0>
		            					<#if (shedCurrentFinalMap.get(shed))?has_content>
		            						<#assign shedCurrentMonthsQty=(shedCurrentFinalMap.get(shed).get(currMonthKey))>
		            							<#assign shedCurrMonthsQty = shedCurrentMonthsQty.get("kgFat")>
		            					<#else>
		            							<#assign shedCurrMonthsQty = (Static["java.math.BigDecimal"].ZERO)>
		            					</#if>
		            						<#assign shedCurrTotMonthsQty=(shedCurrTotMonthsQty)+(shedCurrMonthsQty)>
						            	<fo:table-cell>
						            		<fo:block text-align="right" font-size="5pt" keep-together="always">${(shedCurrMonthsQty)?if_exists?string('##0.00')}</fo:block>
						            	</fo:table-cell>
						            	<#assign shedCurrentMonthsQty=0>
			            			</#list>	
					            	<fo:table-cell>
					            		<fo:block text-align="right" font-size="5pt" keep-together="always">${shedCurrTotMonthsQty?if_exists?string('##0.00')}</fo:block>
					            	</fo:table-cell>
			            			<#assign shedPrevTotMonthsQty = 0>	
		            				<#list prevMonthKeyList as preMonthKey>
		            					<#assign shedPreviousMonthsQty=0>
			            				<#if (shedPreviousFinalMap.get(shed))?has_content>
			            					<#assign shedPreviousMonthsQty={}>
			            					<#assign shedPreviousMonthsQty=(shedPreviousFinalMap.get(shed).get(preMonthKey))>
			            						<#if shedPreviousMonthsQty?has_content>
			            							<#assign shedPrevMonthsQty = shedPreviousMonthsQty.get("kgFat")>
												<#else>
													<#assign shedPrevMonthsQty = 0>
												</#if>
		            					<#else>
		            						<#assign shedPrevMonthsQty = 0>
		            					</#if>
		            					<#assign shedPrevTotMonthsQty=((shedPrevTotMonthsQty)+(shedPrevMonthsQty))>
		            				 </#list>
					            	<fo:table-cell>
					            		<fo:block text-align="right" font-size="5pt" keep-together="always">${shedPrevTotMonthsQty?if_exists?string('#0.00')}</fo:block>
					            	</fo:table-cell>
					   	    		<#assign difference =((shedCurrTotMonthsQty)-(shedPrevTotMonthsQty))>
						   	    	<fo:table-cell>
										<fo:block text-align="right" font-size="5pt" keep-together="always">${difference?if_exists?string('#0.00')}</fo:block>
						   	    	</fo:table-cell>
			   	    				<#if shedCurrTotMonthsQty == 0>
						   	    		<#assign percentage = -100 >
						   	    	</#if>	
			   	    				<#if  shedPrevTotMonthsQty!=0>
				   	    				<#assign percentage =((difference/shedPrevTotMonthsQty)*(100))>
				   	    			<#else>
				   	    				<#assign percentage = 100>
				   	    			</#if>
					   	    		<fo:table-cell>
										<fo:block text-align="right" font-size="5pt" keep-together="always">${percentage?if_exists?string('##0.00')}</fo:block>
									</fo:table-cell>
			   	    			</fo:table-row>
		   	    			</#if>
		   	    		</#list>
			   	    		<#assign currentMccListValues = {}>
	            			<#assign previousMccListValues = {}>
	            			<#assign currentMccValues = 0>
	            			<#assign previousMccValues = 0>
	            			<#assign currentMccListValues = tempCurrentMccMap.get(mccShedWiseList.getKey())>
	            			<#assign currentMccMonthValues = {}>
	            			<#if currentMccListValues?has_content>
			        			<#assign currentMccMonthValues = currentMccListValues.entrySet()>
			        			<#list currentMccMonthValues as currMccMonthValues>
			        			<#if currMccMonthValues?has_content>
			        			<#assign currentDetails = 0>
			        					<#assign currentDetails = currMccMonthValues.getValue().get("kgFat")>
			        					<#assign currentMccValues = currentMccValues+currentDetails>
			        				</#if>
			        			</#list>
			        		</#if>
			        		<#assign previousMccListValues = tempPreviousMccMap.get(mccShedWiseList.getKey())>
	            			<#assign previousMccMonthValues = {}>
	            			<#if previousMccListValues?has_content>
	            			<#assign previousMccMonthValues = previousMccListValues.entrySet()>
	            				<#list previousMccMonthValues as prevMccMonthValues>
	            				<#if prevMccMonthValues?has_content>
	            				<#assign previousDetails = 0>
	            					<#assign previousDetails =  prevMccMonthValues.getValue().get("kgFat")>
	            					<#assign previousMccValues = previousMccValues+previousDetails>
	            				</#if>
	            			</#list>
	            			</#if>
	            			<#if ((currentMccValues+previousMccValues)!=0)>
				   	    		<fo:table-row>
				   	    			<fo:table-cell>
										<fo:block  font-size="5pt" >-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
									</fo:table-cell>
					   	    	</fo:table-row>
				   				<fo:table-row>
						            <fo:table-cell>
					            		<fo:block text-align="left" font-size="5pt" keep-together="always">${mccShedWiseList.getKey()} TOTAL:</fo:block>
					            	</fo:table-cell>
					            	<fo:table-cell>
					            		<fo:block text-align="left" font-size="5pt" keep-together="always"></fo:block>
					            	</fo:table-cell>
					            	<#assign mccTypeTot =tempCurrentMccMap.get(mccShedWiseList.getKey())>
						            <#assign fedCurrTotMonthsQty=0>	
					            	<#list currMonthKeyList as currMonthKey>
					            		<#assign fedCurrMonthsQty=0>
					            		<#if (mccTypeTot.get(currMonthKey))?has_content>
					            			<#assign fedCurrentMonthsQty=(mccTypeTot.get(currMonthKey))>
					            			<#assign fedCurrMonthsQty=fedCurrentMonthsQty.get("kgFat")>
					            		<#else>
	            							<#assign fedCurrMonthsQty = (Static["java.math.BigDecimal"].ZERO)>
	            						</#if>
					            			<#assign fedCurrTotMonthsQty=(fedCurrTotMonthsQty)+(fedCurrMonthsQty)>
						            	<fo:table-cell>
						            		<fo:block text-align="right" font-size="5pt" keep-together="always">${(fedCurrMonthsQty)?if_exists?string('##0.00')}</fo:block>
						            	</fo:table-cell>
						            	<#assign fedCurrentMonthsQty=0>
						       		</#list>
						       		<fo:table-cell>
						            		<fo:block text-align="right" font-size="5pt" keep-together="always">${(fedCurrTotMonthsQty)?if_exists?string('##0.00')}</fo:block>
						            </fo:table-cell>
						            <#assign mccTypeTot={}>
						            <#assign mccTypeTot =tempPreviousMccMap.get(mccShedWiseList.getKey())>
									<#assign fedPrevTotMonthsQty=0>	
					            	<#list prevMonthKeyList as preMonthKey>
					            		<#assign fedPrevMonthsQty=0>
					            		<#if (mccTypeTot.get(preMonthKey))?has_content>
					            			<#assign fedPreviousMonthsQty=(mccTypeTot.get(preMonthKey))>
					            			<#assign fedPrevMonthsQty=fedPreviousMonthsQty.get("kgFat")>
					            		<#else>
	            							<#assign fedPrevMonthsQty = (Static["java.math.BigDecimal"].ZERO)>
	            						</#if>
	            						<#assign fedPrevTotMonthsQty=(fedPrevTotMonthsQty)+(fedPrevMonthsQty)>
						            </#list>
									<fo:table-cell>
						            		<fo:block text-align="right" font-size="5pt" keep-together="always">${(fedPrevTotMonthsQty)?if_exists?string('##0.00')}</fo:block>
						            </fo:table-cell>
						            <#assign diffQty =(fedCurrTotMonthsQty)-(fedPrevTotMonthsQty)>
						            <fo:table-cell>
						            		<fo:block text-align="right" font-size="5pt" keep-together="always">${diffQty?if_exists?string('##0.00')}</fo:block>
						            </fo:table-cell>
						            <#if fedCurrTotMonthsQty == 0>
					   	    			<#assign percentage = -100 >
					   	    		</#if>	
						            <#if  fedPrevTotMonthsQty!=0>
			   	    					<#assign percentage =((diffQty/fedPrevTotMonthsQty)*(100))>
			   	    				<#else>
			   	    					<#assign percentage = 100>
			   	    				</#if> 
						            <fo:table-cell>
						            		<fo:block text-align="right" font-size="5pt" keep-together="always">${percentage?if_exists?string('##0.00')}</fo:block>
						            </fo:table-cell>
						            <#assign fedPrevTotMonthsQty=0>
		    				</fo:table-row>
		    				<fo:table-row>
		   	    				<fo:table-cell>
									<fo:block font-size="5pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
								</fo:table-cell>
			   	    		</fo:table-row>
			   	    	</#if>	
			 		</#list>
		        </fo:table-body>
		    </fo:table> 
		</fo:block>
	 <fo:block  font-size="5pt">
			<fo:table>
			<fo:table-column column-width="20pt"/>
			<fo:table-column column-width="55pt"/>
			<#list currMonthKeyList as currMonthKey>
			<fo:table-column column-width="32pt"/>
			</#list>
			<fo:table-column column-width="34pt"/>
			<fo:table-column column-width="34pt"/>
			<fo:table-column column-width="34pt"/>
			<fo:table-column column-width="34pt"/>
			<fo:table-body>
	        	<fo:table-row>
                    <fo:table-cell>
	            		<fo:block text-align="left" font-size="5pt" keep-together="always">GRAND TOTALS:</fo:block>
	            	</fo:table-cell>
	            	<fo:table-cell>
		            		<fo:block text-align="left" font-size="5pt" keep-together="always"></fo:block>
		            </fo:table-cell>
		            <#assign grandCurrTotalQty = 0>
			      	<#list currMonthKeyList as currMonthKey>
			      	<#assign grandCurrQty = 0>
			      		<#assign grandCurrValues = 0>
				       	<#assign grandCurrValues = tempCurrentGrndMap.get(currMonthKey)>
				       	<#if grandCurrValues?has_content>
				       		<#assign grandCurrQty=grandCurrValues.get("kgFat")>
				       	<#else>
				       		<#assign grandCurrQty = 0>
				       	</#if>	
				       	<#assign grandCurrTotalQty = grandCurrTotalQty+grandCurrQty>
		        		<fo:table-cell>
							<fo:block text-align="right" font-size="5pt" keep-together="always">${grandCurrQty?if_exists?string('#0.00')}</fo:block>
						</fo:table-cell>
					</#list>
					<fo:table-cell>
						<fo:block text-align="right" font-size="5pt" keep-together="always">${grandCurrTotalQty?if_exists?string('#0.00')}</fo:block>
					</fo:table-cell>
					<#assign grandPrevTotalQty = 0>
			       	<#list prevMonthKeyList as preMonthKey>
			       	<#assign grandPrevQty = 0>
			       	<#assign grandPrevValues = 0>
				       	<#assign grandPrevValues = tempPreviousGrndMap.get(preMonthKey)>
				       	<#if grandPrevValues?has_content>
				       		<#assign grandPrevQty=grandPrevValues.get("kgFat")>
				       	<#else>
				       		<#assign grandPrevQty = 0>
				       	</#if>
					<#assign grandPrevTotalQty = grandPrevTotalQty+grandPrevQty>
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
						<fo:block text-align="left" font-size="5pt" keep-together="always">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
					</fo:table-cell>
   	    		</fo:table-row>
	         </fo:table-body>
	     </fo:table> 
	</fo:block>
	<fo:block font-size="5pt">VST_ASCII-012 VST_ASCII-027VST_ASCII-080</fo:block>
    </fo:flow>
 </fo:page-sequence>
 <#else>
    <fo:page-sequence master-reference="main">
  	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="5pt">
  			<fo:block text-align="left" white-space-collapse="false" font-size="5pt" keep-together="always">&#160;&#160;&#160;&#160;						MONTH-WISE MPF.HYDERABAD MILK RECEIPTS (KG-FAT) PERIOD FROM :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate,"dd-MM-yyyy")} to ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate,"dd-MM-yyyy")} (TONES)</fo:block>
            <fo:block font-size="5pt">------------------------------------------------------------------------------------------------------</fo:block>
            <fo:block font-size="5pt">
			<fo:table font-size="5pt">
				<fo:table-column column-width="20pt"/>
				<fo:table-column column-width="55pt"/>
				<#list currMonthKeyList as currMonthKey>
				<fo:table-column column-width="32pt"/>
				</#list>
				<fo:table-column column-width="34pt"/>
				<fo:table-column column-width="34pt"/>
				<fo:table-column column-width="34pt"/>
				<fo:table-column column-width="34pt"/>
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell>
							<fo:block text-align="left" font-size="5pt" keep-together="always">CODE</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block text-align="left" font-size="5pt" keep-together="always">SHED/UNION NAME</fo:block>
						</fo:table-cell>
						<#list currMonthKeyList as currMonthKey>
						<fo:table-cell>
							<fo:block text-align="right" font-size="5pt" keep-together="always">${currMonthKey}</fo:block>
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
         <fo:block font-size="5pt">------------------------------------------------------------------------------------------------------</fo:block>
 	</fo:static-content>
      <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" font-size="5pt">
	 	<fo:block  font-size="5pt">
			<fo:table>
				<fo:table-column column-width="12pt"/>
				<fo:table-column column-width="63pt"/>
				<#list currMonthKeyList as currMonthKey>
				<fo:table-column column-width="32pt"/>
				</#list>
				<fo:table-column column-width="34pt"/>
				<fo:table-column column-width="34pt"/>
				<fo:table-column column-width="34pt"/>
				<fo:table-column column-width="34pt"/>
				<fo:table-body>
		        <#assign mccShedWiseDetailList = mccTypeShedMap.entrySet()>
	            	<#list mccShedWiseDetailList as mccShedWiseList>
						<#assign mccShedsList = mccShedWiseList.getValue()>
            			<#list mccShedsList as shed>
	            			<#assign currentYearMapValues = {}>
	            			<#assign prevYearMapValues = {}>
	            			<#assign currentValues = 0>
	            			<#assign previousValues = 0>
	            			<#assign currentYearMapValues = shedCurrentFinalMap.get(shed)>
	            			<#assign currentMonthValues = {}>
	            			<#if currentYearMapValues?has_content>
			        			<#assign currentMonthValues = currentYearMapValues.entrySet()>
			        			<#list currentMonthValues as currMonthValues>
			        			<#if currMonthValues?has_content>
			        			<#assign currDetails = 0>
			        					<#assign currDetails = currMonthValues.getValue().get("kgFat")>
			        					<#assign currentValues = currentValues+currDetails>
			        				</#if>
			        			</#list>
			        		</#if>	
	            			<#assign prevYearMapValues = shedPreviousFinalMap.get(shed)>
	            			<#assign currentMonthValues = {}>
	            			<#if prevYearMapValues?has_content>
	            			<#assign previousMonthValues = prevYearMapValues.entrySet()>
	            			<#list previousMonthValues as prevMonthValues>
	            			<#assign prevDetails = 0>
	            			<#if prevMonthValues?has_content>
	            				<#assign prevDetails =  prevMonthValues.getValue().get("kgFat")>
	            				<#assign previousValues = previousValues+prevDetails>
	            				</#if>
	            			</#list>
	            			</#if>
	            			<#if ((currentValues+previousValues)!=0)>
		            		<#assign facility = delegator.findOne("Facility", {"facilityId" : shed}, true)>
		            			<fo:table-row>
				                    <fo:table-cell>
				                        <fo:block text-align="right" font-size="5pt">${facility.mccCode?if_exists}</fo:block>
				                    </fo:table-cell>
			            			<fo:table-cell>
			            				<fo:block text-align="left" text-indent='2pt' font-size="5pt" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((facility.get("facilityName")))),20)}</fo:block>
			            			</fo:table-cell>
			           				<#assign shedCurrTotMonthsQty = 0>	
		            				<#list currMonthKeyList as currMonthKey>
		            					<#assign shedCurrMonthsQty=0>
		            					<#if (shedCurrentFinalMap.get(shed))?has_content>
		            						<#assign shedCurrentMonthsQty=(shedCurrentFinalMap.get(shed).get(currMonthKey))>
		            							<#assign shedCurrMonthsQty = shedCurrentMonthsQty.get("kgFat")>
		            					<#else>
		            							<#assign shedCurrMonthsQty = (Static["java.math.BigDecimal"].ZERO)>
		            					</#if>
		            						<#assign shedCurrTotMonthsQty=(shedCurrTotMonthsQty)+(shedCurrMonthsQty)>
						            	<fo:table-cell>
						            		<fo:block text-align="right" font-size="5pt" keep-together="always">${(shedCurrMonthsQty)?if_exists?string('##0.00')}</fo:block>
						            	</fo:table-cell>
						            	<#assign shedCurrentMonthsQty=0>
			            			</#list>	
					            	<fo:table-cell>
					            		<fo:block text-align="right" font-size="5pt" keep-together="always">${shedCurrTotMonthsQty?if_exists?string('##0.00')}</fo:block>
					            	</fo:table-cell>
			            			<#assign shedPrevTotMonthsQty = 0>	
		            				<#list prevMonthKeyList as preMonthKey>
		            					<#assign shedPreviousMonthsQty=0>
			            				<#if (shedPreviousFinalMap.get(shed))?has_content>
			            					<#assign shedPreviousMonthsQty={}>
			            					<#assign shedPreviousMonthsQty=(shedPreviousFinalMap.get(shed).get(preMonthKey))>
			            						<#if shedPreviousMonthsQty?has_content>
			            							<#assign shedPrevMonthsQty = shedPreviousMonthsQty.get("kgFat")>
												<#else>
													<#assign shedPrevMonthsQty = 0>
												</#if>
		            					<#else>
		            						<#assign shedPrevMonthsQty = 0>
		            					</#if>
		            					<#assign shedPrevTotMonthsQty=((shedPrevTotMonthsQty)+(shedPrevMonthsQty))>
		            				 </#list>
					            	<fo:table-cell>
					            		<fo:block text-align="right" font-size="5pt" keep-together="always">${shedPrevTotMonthsQty?if_exists?string('#0.00')}</fo:block>
					            	</fo:table-cell>
					   	    		<#assign difference =((shedCurrTotMonthsQty)-(shedPrevTotMonthsQty))>
						   	    	<fo:table-cell>
										<fo:block text-align="right" font-size="5pt" keep-together="always">${difference?if_exists?string('#0.00')}</fo:block>
						   	    	</fo:table-cell>
			   	    				<#if shedCurrTotMonthsQty == 0>
						   	    		<#assign percentage = -100 >
						   	    	</#if>	
			   	    				<#if  shedPrevTotMonthsQty!=0>
				   	    				<#assign percentage =((difference/shedPrevTotMonthsQty)*(100))>
				   	    			<#else>
				   	    				<#assign percentage = 100>
				   	    			</#if>
					   	    		<fo:table-cell>
										<fo:block text-align="right" font-size="5pt" keep-together="always">${percentage?if_exists?string('##0.00')}</fo:block>
									</fo:table-cell>
			   	    			</fo:table-row>
		   	    			</#if>
		   	    		</#list>
			   	    		<#assign currentMccListValues = {}>
	            			<#assign previousMccListValues = {}>
	            			<#assign currentMccValues = 0>
	            			<#assign previousMccValues = 0>
	            			<#assign currentMccListValues = tempCurrentMccMap.get(mccShedWiseList.getKey())>
	            			<#assign currentMccMonthValues = {}>
	            			<#if currentMccListValues?has_content>
			        			<#assign currentMccMonthValues = currentMccListValues.entrySet()>
			        			<#list currentMccMonthValues as currMccMonthValues>
			        			<#if currMccMonthValues?has_content>
			        			<#assign currentDetails = 0>
			        					<#assign currentDetails = currMccMonthValues.getValue().get("kgFat")>
			        					<#assign currentMccValues = currentMccValues+currentDetails>
			        				</#if>
			        			</#list>
			        		</#if>
			        		<#assign previousMccListValues = tempPreviousMccMap.get(mccShedWiseList.getKey())>
	            			<#assign previousMccMonthValues = {}>
	            			<#if previousMccListValues?has_content>
	            			<#assign previousMccMonthValues = previousMccListValues.entrySet()>
	            				<#list previousMccMonthValues as prevMccMonthValues>
	            				<#if prevMccMonthValues?has_content>
	            				<#assign previousDetails = 0>
	            					<#assign previousDetails =  prevMccMonthValues.getValue().get("kgFat")>
	            					<#assign previousMccValues = previousMccValues+previousDetails>
	            				</#if>
	            			</#list>
	            			</#if>
	            			<#if ((currentMccValues+previousMccValues)!=0)>
				   	    		<fo:table-row>
				   	    			<fo:table-cell>
										<fo:block  font-size="5pt" >-----------------------------------------------------------------------------------------------------</fo:block>
									</fo:table-cell>
					   	    	</fo:table-row>
				   				<fo:table-row>
						            <fo:table-cell>
					            		<fo:block text-align="left" font-size="5pt" keep-together="always">${mccShedWiseList.getKey()} TOTAL:</fo:block>
					            	</fo:table-cell>
					            	<fo:table-cell>
					            		<fo:block text-align="left" font-size="5pt" keep-together="always"></fo:block>
					            	</fo:table-cell>
					            	<#assign mccTypeTot =tempCurrentMccMap.get(mccShedWiseList.getKey())>
						            <#assign fedCurrTotMonthsQty=0>	
					            	<#list currMonthKeyList as currMonthKey>
					            		<#assign fedCurrMonthsQty=0>
					            		<#if (mccTypeTot.get(currMonthKey))?has_content>
					            			<#assign fedCurrentMonthsQty=(mccTypeTot.get(currMonthKey))>
					            			<#assign fedCurrMonthsQty=fedCurrentMonthsQty.get("kgFat")>
					            		<#else>
	            							<#assign fedCurrMonthsQty = (Static["java.math.BigDecimal"].ZERO)>
	            						</#if>
					            			<#assign fedCurrTotMonthsQty=(fedCurrTotMonthsQty)+(fedCurrMonthsQty)>
						            	<fo:table-cell>
						            		<fo:block text-align="right" font-size="5pt" keep-together="always">${(fedCurrMonthsQty)?if_exists?string('##0.00')}</fo:block>
						            	</fo:table-cell>
						            	<#assign fedCurrentMonthsQty=0>
						       		</#list>
						       		<fo:table-cell>
						            		<fo:block text-align="right" font-size="5pt" keep-together="always">${(fedCurrTotMonthsQty)?if_exists?string('##0.00')}</fo:block>
						            </fo:table-cell>
						            <#assign mccTypeTot={}>
						            <#assign mccTypeTot =tempPreviousMccMap.get(mccShedWiseList.getKey())>
									<#assign fedPrevTotMonthsQty=0>	
					            	<#list prevMonthKeyList as preMonthKey>
					            		<#assign fedPrevMonthsQty=0>
					            		<#if (mccTypeTot.get(preMonthKey))?has_content>
					            			<#assign fedPreviousMonthsQty=(mccTypeTot.get(preMonthKey))>
					            			<#assign fedPrevMonthsQty=fedPreviousMonthsQty.get("kgFat")>
					            		<#else>
	            							<#assign fedPrevMonthsQty = (Static["java.math.BigDecimal"].ZERO)>
	            						</#if>
	            						<#assign fedPrevTotMonthsQty=(fedPrevTotMonthsQty)+(fedPrevMonthsQty)>
						            </#list>
									<fo:table-cell>
						            		<fo:block text-align="right" font-size="5pt" keep-together="always">${(fedPrevTotMonthsQty)?if_exists?string('##0.00')}</fo:block>
						            </fo:table-cell>
						            <#assign diffQty =(fedCurrTotMonthsQty)-(fedPrevTotMonthsQty)>
						            <fo:table-cell>
						            		<fo:block text-align="right" font-size="5pt" keep-together="always">${diffQty?if_exists?string('##0.00')}</fo:block>
						            </fo:table-cell>
						            <#if fedCurrTotMonthsQty == 0>
					   	    			<#assign percentage = -100 >
					   	    		</#if>	
						            <#if  fedPrevTotMonthsQty!=0>
			   	    					<#assign percentage =((diffQty/fedPrevTotMonthsQty)*(100))>
			   	    				<#else>
			   	    					<#assign percentage = 100>
			   	    				</#if> 
						            <fo:table-cell>
						            		<fo:block text-align="right" font-size="5pt" keep-together="always">${percentage?if_exists?string('##0.00')}</fo:block>
						            </fo:table-cell>
						            <#assign fedPrevTotMonthsQty=0>
		    				</fo:table-row>
		    				<fo:table-row>
		   	    				<fo:table-cell>
									<fo:block font-size="5pt">-----------------------------------------------------------------------------------------------------</fo:block>
								</fo:table-cell>
			   	    		</fo:table-row>
			   	    	</#if>	
			 		</#list>
		        </fo:table-body>
		    </fo:table> 
		</fo:block>
	 <fo:block  font-size="5pt">
			<fo:table>
			<fo:table-column column-width="20pt"/>
			<fo:table-column column-width="55pt"/>
			<#list currMonthKeyList as currMonthKey>
			<fo:table-column column-width="32pt"/>
			</#list>
			<fo:table-column column-width="34pt"/>
			<fo:table-column column-width="34pt"/>
			<fo:table-column column-width="34pt"/>
			<fo:table-column column-width="34pt"/>
			<fo:table-body>
	        	<fo:table-row>
                    <fo:table-cell>
	            		<fo:block text-align="left" font-size="5pt" keep-together="always">GRAND TOTALS:</fo:block>
	            	</fo:table-cell>
	            	<fo:table-cell>
		            		<fo:block text-align="left" font-size="5pt" keep-together="always"></fo:block>
		            </fo:table-cell>
		            <#assign grandCurrTotalQty = 0>
			      	<#list currMonthKeyList as currMonthKey>
			      	<#assign grandCurrQty = 0>
			      		<#assign grandCurrValues = 0>
				       	<#assign grandCurrValues = tempCurrentGrndMap.get(currMonthKey)>
				       	<#if grandCurrValues?has_content>
				       		<#assign grandCurrQty=grandCurrValues.get("kgFat")>
				       	<#else>
				       		<#assign grandCurrQty = 0>
				       	</#if>	
				       	<#assign grandCurrTotalQty = grandCurrTotalQty+grandCurrQty>
		        		<fo:table-cell>
							<fo:block text-align="right" font-size="5pt" keep-together="always">${grandCurrQty?if_exists?string('#0.00')}</fo:block>
						</fo:table-cell>
					</#list>
					<fo:table-cell>
						<fo:block text-align="right" font-size="5pt" keep-together="always">${grandCurrTotalQty?if_exists?string('#0.00')}</fo:block>
					</fo:table-cell>
					<#assign grandPrevTotalQty = 0>
			       	<#list prevMonthKeyList as preMonthKey>
			       	<#assign grandPrevQty = 0>
			       	<#assign grandPrevValues = 0>
				       	<#assign grandPrevValues = tempPreviousGrndMap.get(preMonthKey)>
				       	<#if grandPrevValues?has_content>
				       		<#assign grandPrevQty=grandPrevValues.get("kgFat")>
				       	<#else>
				       		<#assign grandPrevQty = 0>
				       	</#if>
					<#assign grandPrevTotalQty = grandPrevTotalQty+grandPrevQty>
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
						<fo:block text-align="left" font-size="5pt" keep-together="always">------------------------------------------------------------------------------------------------------</fo:block>
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