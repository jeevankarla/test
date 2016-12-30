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
			<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
					 margin-left="0.5in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.8in" >
				<fo:region-body margin-top="1.7in"/>
				<fo:region-before extent="1in"/>
				<fo:region-after extent="1in"/>
			</fo:simple-page-master>
		</fo:layout-master-set>
		<#if lossChildWiseMap?has_content || profitChildWiseMap?has_content>	
		<fo:page-sequence master-reference="main">
			<fo:static-content font-size="13pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">
				<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
	      		<#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
				<#assign reportSecSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable_01"}, true)>
				<fo:block font-family="Courier,monospace">
				   <fo:table>
					 <fo:table-column column-width="20%"/>
					 <fo:table-column column-width="20%"/>
		      		 <fo:table-column column-width="20%"/>
					  <fo:table-body>
					    <fo:table-row>
					      <fo:table-cell>
					        <fo:block-container >
	                           <fo:block text-align="left" font-size="13pt"><fo:external-graphic src="http://localhost:22080//vasista/complogos/aavin_logo.png" content-height="scale-to-fit" scaling="uniform" height="45" width="35"/></fo:block>
	                        </fo:block-container>
					      </fo:table-cell>
					      <fo:table-cell>
					        <fo:block text-align="center" font-weight = "bold"  white-space-collapse="false">  </fo:block>
					      </fo:table-cell>
					    </fo:table-row>
					  </fo:table-body>
					</fo:table>
			    </fo:block>
				<fo:block text-align="center" keep-together="always"  >&#160;TAMILNADU CO-OPERATIVE MILK PRODUCERS' FEDERATION LIMITED, CHENNAI-51</fo:block>
				<#if parameters.glAccountCategoryTypeId?has_content>
					<#if parameters.glAccountCategoryTypeId == "BS">
               		 <fo:block text-align="center" white-space-collapse="false">&#160;  BALANCE SHEET AS ON 31.03.2016 </fo:block>
                <#elseif parameters.glAccountCategoryTypeId == "MFA">
                	<fo:block text-align="center" white-space-collapse="false">&#160;   FINAL AUDIT FOR THE YEAR ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "yy")}</fo:block>
                     <fo:block text-align="center" white-space-collapse="false">&#160;   MANUFACTURING ACCOUNT FOR THE YEAR ENDED ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "dd.MM.yyyy")}</fo:block>
                 <#elseif parameters.glAccountCategoryTypeId == "PL">
                 <fo:block text-align="center" white-space-collapse="false">&#160;   FINAL AUDIT FOR THE YEAR ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "yy")}</fo:block>
                     <fo:block text-align="center" white-space-collapse="false">&#160; PROFIT AND LOSS ACCOUNT FOR THE YEAR ENDED ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "dd.MM.yyyy")}</fo:block>
                  <#elseif parameters.glAccountCategoryTypeId == "TRA">
                  <fo:block text-align="center" white-space-collapse="false">&#160;   FINAL AUDIT FOR THE YEAR ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "yy")}</fo:block>
                     <fo:block text-align="center" white-space-collapse="false">&#160;  TRADING ACCOUNT FOR THE YEAR ENDED ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "dd.MM.yyyy")}</fo:block>
                   <#elseif parameters.glAccountCategoryTypeId == "COST_CENTER">
                     <fo:block text-align="center" white-space-collapse="false">&#160;   COST CENTER FOR THE YEAR ENDED 31.3.2016</fo:block>
                   </#if>
                    <#if parameters.glAccountCategoryTypeId =="RC">
                    	<fo:block text-align="center" white-space-collapse="false" keep-together="always">&#160;   RECEIPTS AND PAYMENTS</fo:block>
                    </#if>
                 </#if>
                                 
                <fo:block text-align="left" keep-together="always"  >&#160;-------------------------------------------------------------------------------------------------------------------------------</fo:block>				   	
            </fo:static-content>
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">
                <fo:block font-family="Courier,monospace" font-size="11pt">
                 <fo:table>
					  <fo:table-column column-width="505pt"/>
					  <fo:table-column column-width="505pt"/>
					      <fo:table-body>
						      <fo:table-row >
						      	  <fo:table-cell border="solid">
							      	  <fo:block>
										   <fo:table>
											  <fo:table-column column-width="165pt"/>
											  <fo:table-column column-width="70pt"/>
											  <fo:table-column column-width="135pt"/>
											  <fo:table-column column-width="135pt"/>
											      <fo:table-body>
												      <fo:table-row >
											             <fo:table-cell border="solid">
															 <fo:block text-align="center" keep-together="always" font-weight="bold">LOSS</fo:block>
														 </fo:table-cell>
														 <fo:table-cell border="solid">
															 <fo:block text-align="center" keep-together="always" font-weight="bold">SCHEDULE</fo:block>
														 </fo:table-cell>
														 <fo:table-cell border="solid">
															 <fo:block text-align="center" keep-together="always" font-weight="bold">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "yy")}</fo:block>
														 </fo:table-cell>
							                             <fo:table-cell border="solid">
							                             	 <fo:block text-align="center" keep-together="always" font-weight="bold">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(prevFromDate, "yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(prevThruDate, "yy")}</fo:block>
														 </fo:table-cell>
													 </fo:table-row>
											     </fo:table-body>
										     </fo:table>
									     </fo:block>
								     </fo:table-cell>
								      <fo:table-cell border="solid">
							      	  <fo:block>
										   <fo:table>
											  <fo:table-column column-width="165pt"/>
											  <fo:table-column column-width="70pt"/>
											  <fo:table-column column-width="135pt"/>
											  <fo:table-column column-width="135pt"/>
											      <fo:table-body>
												      <fo:table-row >
											             <fo:table-cell border="solid">
															 <fo:block text-align="center" keep-together="always" font-weight="bold">PROFIT</fo:block>
														 </fo:table-cell>
														 <fo:table-cell border="solid">
															 <fo:block text-align="center" keep-together="always" font-weight="bold">SCHEDULE</fo:block>
														 </fo:table-cell>
														 <fo:table-cell border="solid">
															 <fo:block text-align="center" keep-together="always" font-weight="bold">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "yy")}</fo:block>
														 </fo:table-cell>
							                             <fo:table-cell border="solid">
							                             	 <fo:block text-align="center" keep-together="always" font-weight="bold">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(prevFromDate, "yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(prevThruDate, "yy")}</fo:block>
														 </fo:table-cell>
													 </fo:table-row>
											     </fo:table-body>
										     </fo:table>
									     </fo:block>
								     </fo:table-cell>
			    				</fo:table-row>
			    				<fo:table-row>
			    				<#assign presentYearTradeProfitTotVal=0>
			    				<#assign presentYearTradelossTotVal=0>
			    				<#assign prevYearTradeProfitTotVal=0>
			    				<#assign prevYearTradelossTotVal=0>
			    				<#assign presentYrTradeTempVal=0>
			    				<#assign prevYrTradeTempVal=0>
			    				
			    				
			    				 <#if GrandTradingProfitTotal.get(presentYear)?has_content>
			    					<#assign presentYearTradeProfitTotVal=GrandTradingProfitTotal.get(presentYear)?if_exists>
			    				</#if>
								 <#if GrandTradingProfitTotal.get(prevsYear)?has_content>
								 	<#assign prevYearTradeProfitTotVal=GrandTradingProfitTotal.get(prevsYear)?if_exists>
								 </#if>
								 <#if GrandTradingLossTotal.get(presentYear)?has_content>
			    					<#assign presentYearTradelossTotVal=GrandTradingLossTotal.get(presentYear)?if_exists>
			    				</#if>
								 <#if GrandTradingLossTotal.get(prevsYear)?has_content>
								 	<#assign prevYearTradelossTotVal=GrandTradingLossTotal.get(prevsYear)?if_exists>
								 </#if>
								 <#assign presentYrTradeTempVal = presentYearTradeProfitTotVal-presentYearTradelossTotVal>
								 <#assign prevYrTradeTempVal = prevYearTradeProfitTotVal-prevYearTradelossTotVal>
								 <#assign presentYearTradeProfitTotVal=0>
			    				<#assign presentYearTradelossTotVal=0>
			    				<#assign prevYearTradeProfitTotVal=0>
			    				<#assign prevYearTradelossTotVal=0>
			    				<#if presentYrTradeTempVal gte 0>
			    					<#assign presentYearTradeProfitTotVal=presentYrTradeTempVal>
								 <#else>
								 	<#assign presentYearTradelossTotVal=((-1)*presentYrTradeTempVal)>
								 </#if>
								 <#if prevYrTradeTempVal gte 0>
			    					<#assign  prevYearTradeProfitTotVal=prevYrTradeTempVal>
								 <#else>
								 	<#assign prevYearTradelossTotVal=((-1)*prevYrTradeTempVal)>
								 </#if>
								 <fo:table-cell border="solid">
							      	  <fo:block>
										   <fo:table>
											  <fo:table-column column-width="165pt"/>
											  <fo:table-column column-width="70pt"/>
											  <fo:table-column column-width="135pt"/>
											  <fo:table-column column-width="135pt"/>
											      <fo:table-body>
												      <fo:table-row >
											             <fo:table-cell border="solid" border-right-style="hidden">
															 <fo:block text-align="left" keep-together="always" font-weight="bold">BY GROSS LOSS</fo:block>
														 </fo:table-cell>
														 <fo:table-cell border="solid" border-left-style="hidden">
															 <fo:block text-align="left" keep-together="always" font-weight="bold"></fo:block>
														 </fo:table-cell>
														 <fo:table-cell border="solid">
															 <fo:block text-align="right" keep-together="always" font-weight="bold"><#if presentYearTradeProfitTotVal !=0>${presentYearTradeProfitTotVal?string("#0.00")}</#if></fo:block>
														 </fo:table-cell>
							                             <fo:table-cell border="solid">
							                             	 <fo:block text-align="right" keep-together="always" font-weight="bold"><#if prevYearTradeProfitTotVal !=0>${prevYearTradeProfitTotVal?string("#0.00")}&#160;</#if></fo:block>
														 </fo:table-cell>
													 </fo:table-row>
											     </fo:table-body>
										     </fo:table>
									     </fo:block>
								     </fo:table-cell>
								 <fo:table-cell border="solid">
							      	  <fo:block>
										   <fo:table>
											  <fo:table-column column-width="165pt"/>
											  <fo:table-column column-width="70pt"/>
											  <fo:table-column column-width="135pt"/>
											  <fo:table-column column-width="135pt"/>
											      <fo:table-body>
												      <fo:table-row >
											             <fo:table-cell border="solid" border-right-style="hidden">
															 <fo:block text-align="left" keep-together="always" font-weight="bold">BY GROSS PROFIT</fo:block>
														 </fo:table-cell>
														 <fo:table-cell border="solid" border-left-style="hidden">
															 <fo:block text-align="left" keep-together="always" font-weight="bold"></fo:block>
														 </fo:table-cell>
														 <fo:table-cell border="solid">
															 <fo:block text-align="right" keep-together="always" font-weight="bold"><#if presentYearTradelossTotVal !=0>${presentYearTradelossTotVal?string("#0.00")}</#if></fo:block>
														 </fo:table-cell>
							                             <fo:table-cell border="solid">
							                             	 <fo:block text-align="right" keep-together="always" font-weight="bold"><#if prevYearTradelossTotVal !=0>${prevYearTradelossTotVal?string("#0.00")}&#160;</#if></fo:block>
														 </fo:table-cell>
													 </fo:table-row>
											     </fo:table-body>
										     </fo:table>
									     </fo:block>
								     </fo:table-cell>
			    				</fo:table-row>
			    				 <fo:table-row >
			    				 
						      	  <fo:table-cell border="solid">
							      	  <fo:block>
										   <fo:table>
											  <fo:table-column column-width="165pt"/>
											  <fo:table-column column-width="70pt"/>
											  <fo:table-column column-width="135pt"/>
											  <fo:table-column column-width="135pt"/>
											  <#assign profitCategoryList = profitChildWiseMap.entrySet()>
											      <fo:table-body>
												     <#list profitCategoryList as category>
													 <#assign profitValues=category.getValue()>
													 <#assign presentYearVal=0>
													 <#if profitValues.get(presentYear)?has_content>
													 <#assign presentYearVal=profitValues.get(presentYear)?if_exists>
													 </#if>
													 <#assign prevsYearVal=0>
													 <#if profitValues.get(prevsYear)?has_content>
													 <#assign prevsYearVal=profitValues.get(prevsYear)?if_exists>
													 </#if>
												 	<fo:table-row >
											             <fo:table-cell border="solid" >
															 <fo:block text-align="left"  font-weight="bold">${profitValues.get("childCatName")}</fo:block>
														 </fo:table-cell>
														 <fo:table-cell border="solid" >
															 <fo:block text-align="center" keep-together="always" font-weight="bold">${profitValues.get("scheduleNo")}</fo:block>
														 </fo:table-cell>
														 <fo:table-cell border="solid">
															 <fo:block text-align="right" keep-together="always" font-weight="bold">${presentYearVal?if_exists?string("#0.00")}</fo:block>
														 </fo:table-cell>
							                             <fo:table-cell border="solid">
							                             	 <fo:block text-align="right" keep-together="always" font-weight="bold">${prevsYearVal?if_exists?string("#0.00")}&#160;</fo:block>
														 </fo:table-cell>
												 	</fo:table-row>
												 	</#list>
											     </fo:table-body>
										     </fo:table>
									     </fo:block>
								     </fo:table-cell>
								      <fo:table-cell border="solid">
							      	  <fo:block>
										   <fo:table>
											  <fo:table-column column-width="165pt"/>
											  <fo:table-column column-width="70pt"/>
											  <fo:table-column column-width="135pt"/>
											  <fo:table-column column-width="135pt"/>
											      <fo:table-body>
											            <#assign lossCategoryList= lossChildWiseMap.entrySet()>
											             <#list lossCategoryList as category>
														 <#assign values=category.getValue()>
														 <#assign presentYearVal=0>
														 <#if values.get(presentYear)?has_content>
														 <#assign presentYearVal=values.get(presentYear)?if_exists>
														 </#if>
														 <#assign prevsYearVal=0>
														 <#if values.get(prevsYear)?has_content>
														 <#assign prevsYearVal=values.get(prevsYear)?if_exists>
														 </#if>
													 	<fo:table-row>
												             <fo:table-cell border="solid" >
																 <fo:block text-align="left"  font-weight="bold">${values.get("childCatName")}</fo:block>
															 </fo:table-cell>
															 <fo:table-cell border="solid" >
																 <fo:block text-align="center" keep-together="always" font-weight="bold">${values.get("scheduleNo")}</fo:block>
															 </fo:table-cell>
															 <fo:table-cell border="solid">
																 <fo:block text-align="right" keep-together="always" font-weight="bold">${presentYearVal?if_exists?string("#0.00")}</fo:block>
															 </fo:table-cell>
								                             <fo:table-cell border="solid">
								                             	 <fo:block text-align="right" keep-together="always" font-weight="bold">${prevsYearVal?if_exists?string("#0.00")}&#160;</fo:block>
															 </fo:table-cell>
													 	</fo:table-row>
													 	</#list>
											     </fo:table-body>
										     </fo:table>
									     </fo:block>
								     </fo:table-cell>
			    				</fo:table-row>
			    				<fo:table-row>
			    				<#assign presentYearProfitTotVal=0>
			    				<#assign presentYearlossTotVal=0>
			    				<#assign prevYearProfitTotVal=0>
			    				<#assign prevYearlossTotVal=0>
			    				<#assign presentYrTempVal=0>
			    				<#assign prevYrTempVal=0>
			    				<#assign presentYearProfitGrdTotVal=0>
			    				<#assign presentYearlossGrdTotVal=0>
			    				<#assign prevYearProfitGrdTotVal=0>
			    				<#assign prevYearlossGrdTotVal=0>
			    				
			    				
			    				 <#if GrandProfitTotal.get(presentYear)?has_content>
			    					<#assign presentYearProfitTotVal=GrandProfitTotal.get(presentYear)?if_exists>
			    					<#assign presentYearProfitTotVal=presentYearProfitTotVal+presentYearTradeProfitTotVal>
			    					<#assign presentYearProfitGrdTotVal=presentYearProfitTotVal>
			    				</#if>
								 <#if GrandProfitTotal.get(prevsYear)?has_content>
								 	<#assign prevYearProfitTotVal=GrandProfitTotal.get(prevsYear)?if_exists>
								 	<#assign prevYearProfitTotVal=prevYearProfitTotVal+prevYearTradeProfitTotVal>
								 	<#assign prevYearProfitGrdTotVal=prevYearProfitTotVal>
								 </#if>
								 <#if GrandLossTotal.get(presentYear)?has_content>
			    					<#assign presentYearlossTotVal=GrandLossTotal.get(presentYear)?if_exists>
			    					<#assign presentYearlossTotVal=presentYearTradelossTotVal+presentYearlossTotVal>
			    					<#assign presentYearlossGrdTotVal=presentYearlossTotVal>
			    				</#if>
								 <#if GrandLossTotal.get(prevsYear)?has_content>
								 	<#assign prevYearlossTotVal=GrandLossTotal.get(prevsYear)?if_exists>
								 	<#assign prevYearlossTotVal=prevYearTradelossTotVal+prevYearlossTotVal>
								 	<#assign prevYearlossGrdTotVal=prevYearlossTotVal>
								 </#if>
								 <#assign presentYrTempVal = presentYearProfitTotVal-presentYearlossTotVal>
								 <#assign prevYrTempVal = prevYearProfitTotVal-prevYearlossTotVal>
								 <#assign presentYearProfitTotVal=0>
			    				<#assign presentYearlossTotVal=0>
			    				<#assign prevYearProfitTotVal=0>
			    				<#assign prevYearlossTotVal=0>
			    				<#if presentYrTempVal gte 0>
			    					<#assign presentYearlossTotVal=presentYrTempVal>
								 <#else>
								 	<#assign presentYearProfitTotVal=((-1)*presentYrTempVal)>
								 </#if>
								 <#if prevYrTempVal gte 0>
			    					<#assign  prevYearlossTotVal=prevYrTempVal>
								 <#else>
								 	<#assign prevYearProfitTotVal=((-1)*prevYrTempVal)>
								 </#if>
								 <#assign presentYearProfitGrdTotVal=presentYearProfitTotVal+presentYearProfitGrdTotVal>
								 <#assign prevYearProfitGrdTotVal=prevYearProfitGrdTotVal+prevYearProfitTotVal>
								 <#assign presentYearlossGrdTotVal=presentYearlossGrdTotVal+presentYearlossTotVal>
								 <#assign prevYearlossGrdTotVal=prevYearlossGrdTotVal+prevYearlossTotVal>
			    					<fo:table-cell border="solid">
							      	  <fo:block>
										   <fo:table>
											  <fo:table-column column-width="165pt"/>
											  <fo:table-column column-width="70pt"/>
											  <fo:table-column column-width="135pt"/>
											  <fo:table-column column-width="135pt"/>
											      <fo:table-body>
												      <fo:table-row >
											             <fo:table-cell border="solid" border-right-style="hidden">
															 <fo:block text-align="left" keep-together="always" font-weight="bold">TO NET PROFIT</fo:block>
														 </fo:table-cell>
														 <fo:table-cell border="solid" border-left-style="hidden">
															 <fo:block text-align="left" keep-together="always" font-weight="bold"></fo:block>
														 </fo:table-cell>
														 <fo:table-cell border="solid">
															 <fo:block text-align="right" keep-together="always" font-weight="bold"><#if presentYearProfitTotVal !=0>${presentYearProfitTotVal?string("#0.00")}</#if></fo:block>
														 </fo:table-cell>
							                             <fo:table-cell border="solid">
							                             	 <fo:block text-align="right" keep-together="always" font-weight="bold"><#if prevYearProfitTotVal !=0>${prevYearProfitTotVal?string("#0.00")}&#160;</#if></fo:block>
														 </fo:table-cell>
													 </fo:table-row>
											     </fo:table-body>
										     </fo:table>
									     </fo:block>
								     </fo:table-cell>
								      <fo:table-cell border="solid">
							      	  <fo:block>
										   <fo:table>
											  <fo:table-column column-width="165pt"/>
											  <fo:table-column column-width="70pt"/>
											  <fo:table-column column-width="135pt"/>
											  <fo:table-column column-width="135pt"/>
											      <fo:table-body>
												      <fo:table-row >
											             <fo:table-cell border="solid" border-right-style="hidden">
															 <fo:block text-align="left" keep-together="always" font-weight="bold">BY NET LOSS</fo:block>
														 </fo:table-cell>
														 <fo:table-cell border="solid" border-left-style="hidden">
															 <fo:block text-align="left" keep-together="always" font-weight="bold"></fo:block>
														 </fo:table-cell>
														 <fo:table-cell border="solid">
															 <fo:block text-align="right" keep-together="always" font-weight="bold"><#if presentYearlossTotVal !=0>${presentYearlossTotVal?string("#0.00")}</#if></fo:block>
														 </fo:table-cell>
							                             <fo:table-cell border="solid">
							                             	 <fo:block text-align="right" keep-together="always" font-weight="bold"><#if prevYearlossTotVal !=0>${prevYearlossTotVal?string("#0.00")}&#160;</#if></fo:block>
														 </fo:table-cell>
													 </fo:table-row>
											     </fo:table-body>
										     </fo:table>
									     </fo:block>
								     </fo:table-cell>
			    				</fo:table-row>
			    				<fo:table-row>
			    					
								      <fo:table-cell border="solid">
							      	  <fo:block>
										   <fo:table>
											 <fo:table-column column-width="165pt"/>
											  <fo:table-column column-width="70pt"/>
											  <fo:table-column column-width="135pt"/>
											  <fo:table-column column-width="135pt"/>
											      <fo:table-body>
												      <fo:table-row >
											             <fo:table-cell border="solid" border-right-style="hidden">
															 <fo:block text-align="left" keep-together="always" font-weight="bold">TOTALS</fo:block>
														 </fo:table-cell>
														 <fo:table-cell border="solid" border-left-style="hidden">
															 <fo:block text-align="left" keep-together="always" font-weight="bold"></fo:block>
														 </fo:table-cell>
														  <fo:table-cell border="solid">
															 <fo:block text-align="right" keep-together="always" font-weight="bold">${presentYearProfitGrdTotVal?string("#0.00")}</fo:block>
														 </fo:table-cell>
							                             <fo:table-cell border="solid">
							                             	 <fo:block text-align="right" keep-together="always" font-weight="bold">${prevYearProfitGrdTotVal?string("#0.00")}</fo:block>
														 </fo:table-cell>
													 </fo:table-row>
											     </fo:table-body>
										     </fo:table>
									     </fo:block>
								     </fo:table-cell>
								     <fo:table-cell border="solid">
							      	  <fo:block>
										   <fo:table>
											  <fo:table-column column-width="165pt"/>
											  <fo:table-column column-width="70pt"/>
											  <fo:table-column column-width="135pt"/>
											  <fo:table-column column-width="135pt"/>
											      <fo:table-body>
												      <fo:table-row >
											             <fo:table-cell border="solid" border-right-style="hidden">
															 <fo:block text-align="left" keep-together="always" font-weight="bold">TOTALS</fo:block>
														 </fo:table-cell>
														 <fo:table-cell border="solid" border-left-style="hidden">
															 <fo:block text-align="left" keep-together="always" font-weight="bold"></fo:block>
														 </fo:table-cell>
														 <fo:table-cell border="solid">
															 <fo:block text-align="right" keep-together="always" font-weight="bold">${presentYearlossGrdTotVal?string("#0.00")}</fo:block>
														 </fo:table-cell>
							                             <fo:table-cell border="solid">
							                             	 <fo:block text-align="right" keep-together="always" font-weight="bold">${prevYearlossGrdTotVal?string("#0.00")}&#160;</fo:block>
														 </fo:table-cell>
													 </fo:table-row>
											     </fo:table-body>
										     </fo:table>
									     </fo:block>
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
       		 		<fo:block font-size="14pt" text-align="center">
            			 No Records Found....!
       		 		</fo:block>
    			</fo:flow>
	  </fo:page-sequence>	
	</#if>	
</fo:root>
</#escape>	 	    