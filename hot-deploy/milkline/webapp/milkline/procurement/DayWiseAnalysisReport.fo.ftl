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
	<#assign numberOfLines=65>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-left=".3in"  margin-right=".3in" margin-bottom=".5in"  margin-top=".2in">
                <fo:region-body margin-top="1.3in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
     <#if unitRouteMap?has_content>
     	<#assign rowCount=0>
     	<#assign dayTotalsEntries = unitRouteMap.entrySet()>  
     	<#assign grTotLtrs =0>
     	<#assign grTotKgs = 0>
        <#assign grTotKgFat = 0>
        <#assign grTotKgSnf = 0>
        <#assign grTotSLtrs = 0>
        <#assign grTotSKgs = 0>
        <#assign grTotSFat = 0>
        <#assign grTotCurdLtrs =0>
         <#list dayTotalsEntries as unitRouteEntries>
         	<#assign unitRouteEntry = unitRouteEntries.getValue().entrySet()>         	  
        		<fo:page-sequence master-reference="main">
        			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">        				
        				<fo:block text-align="left" white-space-collapse="false" font-size="8pt" keep-together="always">STATEMENT OF DAY WISE ANALYSIS FOR LTS,KGS,KGFAT,KGSNF AND AVERAGE FAT , AVERAGE SNF </fo:block>
        				<fo:block font-size="8pt">------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
        				<#assign unitDetails = delegator.findOne("Facility", {"facilityId" : unitRouteEntries.getKey()}, true)>
        				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="8pt">.         UNIT CODE    :  ${(unitDetails.facilityCode)?if_exists}                UNIT NAME               :${unitDetails.description?if_exists}</fo:block>
        				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="8pt">.         TRANSACTIONS DATE      :  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "dd/MM/yyyy")}</fo:block>	 	 	  
        				<fo:block font-size="8pt">------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
        				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="8pt">.                            R    D    T                        GOOD MILK                                   SOUR MILK                        TOTAL         CURDLED</fo:block>
        				<fo:block keep-together="always" white-space-collapse="false" font-size="8pt">.                            N    A    Y     ------------------------------------------------  ----------------------------  -------------------------------- -------</fo:block>
        				<fo:block keep-together="always" white-space-collapse="false" font-size="8pt">CODE   NAME OF OWNER         O    Y    P      LTS      KGS     FAT%   SNF    KGFAT    KGSNF    LTS    KGS    FAT%   KGFAT    LTS     KGS     KGFAT    KGSNF   LTS </fo:block>
        				<fo:block font-size="8pt">------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
        			</fo:static-content>
       				<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
       				<#assign rowCount = 9> 
       					<#assign timeMap = purchageTimeMap.entrySet()>
       				<#list timeMap as time>
       					<#list unitRouteEntry as routeEntries>
       						<#assign routeDetails = delegator.findOne("Facility", {"facilityId" : routeEntries.getKey()}, true)>
       						<#assign routeEntry = routeEntries.getValue().entrySet()>
       						<#assign rtBMTotLtrs =0>
       						<#assign rtBMTotKgs =0>
       						<#assign rtBMTotKgFat =0>
       						<#assign rtBMTotKgSnf =0>
       						<#assign rtBMTotSltrs =0>
       						<#assign rtBMTotSKgs =0>
       						<#assign rtBMTotSKgFat =0>       						
       						<#assign rtBMCurLdLts =0>
       						
       						<#assign rtCMTotLtrs =0>
       						<#assign rtCMTotKgs =0>
       						<#assign rtCMTotKgFat =0>
       						<#assign rtCMTotKgSnf =0>
       						<#assign rtCMTotSltrs =0>
       						<#assign rtCMTotSKgs =0>
       						<#assign rtCMTotSKgFat =0>
       						<#assign rtCMCurLdLts =0>
       						<#list routeEntry as agentEntries>	
       							<#assign facility = delegator.findOne("Facility", {"facilityId" : agentEntries.getKey()}, true)>  
       							<#assign agentDayEntries = agentEntries.getValue().entrySet()> 
       							<#list agentDayEntries as agentDayEntry>
       								<#assign agentValues =  agentDayEntry.getValue().entrySet()>
       								<#list agentValues as  agentEntryDetails>
       									<#if agentEntryDetails.getKey() !="TOT">
       										<#assign agentEntry =agentEntryDetails.getValue().entrySet()>       									
       										<#assign rowCount=rowCount+2>
       									<#list agentEntry as agentDayWiseEntries>
       										<#assign day = agentDayWiseEntries.getKey()> 
       									<#if (rowCount>=numberOfLines)>
       											<fo:block font-size="8pt" page-break-after="always">
       											<#assign rowCount=9>
       										<#else>
       											<fo:block font-size="8pt">
       									</#if>					
       								<fo:table>
                    					<fo:table-column column-width="30pt"/>
                   						<fo:table-column column-width="40pt"/>                
                    					<fo:table-column column-width="75pt"/>
                    					<fo:table-column column-width="40pt"/>
                    					<fo:table-column column-width="40pt"/>
                    					<fo:table-body>
                    					<#if agentDayWiseEntries.getKey() == time.getKey()>
                    						<#assign agentAmEntries = agentDayWiseEntries.getValue().entrySet()>
                    						<fo:table-row>
                    							                    							
                    							<fo:table-cell>
                    								<#assign milkType = milkTypeMap.entrySet()>
                    								<#list milkType as milkTypeValue>
                    								<#list agentAmEntries as agentEntryValues>
                    									<#if agentEntryValues.getKey() !="TOT">
                    										<#if agentEntryValues.getKey() == milkTypeValue.getKey()>                    											
                    											<fo:block keep-together="always">
                    												<fo:table>
                    													<fo:table-column column-width="35pt"/>
                   														<fo:table-column column-width="17pt"/>
                   														<fo:table-column column-width="40pt"/>
                   														<fo:table-column column-width="40pt"/>
                   														<fo:table-column column-width="40pt"/>
                   														<fo:table-column column-width="40pt"/>
                   														<fo:table-column column-width="30pt"/>
                   														<fo:table-column column-width="40pt"/>
                   														<fo:table-column column-width="40pt"/>
                   														<fo:table-column column-width="40pt"/>
                   														<fo:table-column column-width="40pt"/>
                   														<fo:table-column column-width="40pt"/>
                   														<fo:table-column column-width="40pt"/>
                   														<fo:table-column column-width="30pt"/>
                   														<fo:table-column column-width="30pt"/>
                   														<fo:table-column column-width="40pt"/>
                   														<fo:table-column column-width="40pt"/>
                   														<fo:table-column column-width="40pt"/>
                   														<fo:table-column column-width="40pt"/>
                   														<fo:table-column column-width="40pt"/>
                   														<fo:table-column column-width="35pt"/>
                   														<fo:table-body>
                   														<#if agentEntryValues.getValue().get("qtyKgs") !=0>
                   															<fo:table-row>
                   																<fo:table-cell>
								                    								<fo:block>${facility.facilityCode}</fo:block>
								                    							</fo:table-cell>
								                    							<fo:table-cell> 
								                    								<fo:block keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, facility.ownerPartyId, false))),15)}</fo:block>
								                    							</fo:table-cell>
								                    							<#assign parentFacilityDetails = delegator.findOne("Facility", {"facilityId" : facility.parentFacilityId}, true)>
                   																<fo:table-cell/>
                   																<fo:table-cell>
								                    								<fo:block keep-together="always" text-align="left" text-indent="45pt">${(parentFacilityDetails.facilityCode?if_exists)}</fo:block>
								                    							</fo:table-cell>
								                    							<fo:table-cell>
								                    								<fo:block keep-together="always" text-align="left" text-indent="30pt">${agentDayWiseEntries.getKey()}</fo:block>
								                    							</fo:table-cell>
                   																<fo:table-cell>
                   																	<#if milkTypeValue.getKey()=="Buffalo Milk">
                   																		<#assign rtBMTotLtrs= rtBMTotLtrs+(agentEntryValues.getValue().get("qtyLtrs"))>
                   																		<#assign rtBMTotKgs = rtBMTotKgs+(agentEntryValues.getValue().get("qtyKgs"))>
                   																		<#assign rtBMTotKgFat= rtBMTotKgFat+(agentEntryValues.getValue().get("kgFat"))>
                   																		<#assign rtBMTotKgSnf= rtBMTotKgSnf+(agentEntryValues.getValue().get("kgSnf"))>
                   																		<#assign rtBMTotSltrs= rtBMTotSltrs+(agentEntryValues.getValue().get("sQtyLtrs"))>
                   																		<#assign rtBMTotSKgs= rtBMTotSKgs+((agentEntryValues.getValue().get("sQtyLtrs"))*1.03)>
                   																		<#assign rtBMTotSKgFat= rtBMTotSKgFat+((((agentEntryValues.getValue().get("sQtyLtrs"))*1.03)*(agentEntryValues.getValue().get("sFat")))/100)>
                   																		<#assign rtBMCurLdLts = rtBMCurLdLts+((agentEntryValues.getValue().get("cQtyLtrs")))>
                   																		<fo:block text-align="left" text-indent="15pt">BM</fo:block>
                   																	<#else>	
                   																		<#assign rtCMTotLtrs= rtCMTotLtrs+(agentEntryValues.getValue().get("qtyLtrs"))>                   																		
                   																		<#assign rtCMTotKgs = rtCMTotKgs+(agentEntryValues.getValue().get("qtyKgs"))>
                   																		<#assign rtCMTotKgFat= rtCMTotKgFat+(agentEntryValues.getValue().get("kgFat"))>
                   																		<#assign rtCMTotKgSnf= rtCMTotKgSnf+(agentEntryValues.getValue().get("kgSnf"))>
                   																		<#assign rtCMTotSltrs= rtCMTotSltrs+(agentEntryValues.getValue().get("sQtyLtrs"))>
                   																		<#assign rtCMTotSKgs= rtCMTotSKgs+((agentEntryValues.getValue().get("sQtyLtrs"))*1.03)>
                   																		<#assign rtCMTotSKgFat= rtCMTotSKgFat+((((agentEntryValues.getValue().get("sQtyLtrs"))*1.03)*(agentEntryValues.getValue().get("sFat")))/100)>
                   																		<#assign rtCMCurLdLts = rtCMCurLdLts+((agentEntryValues.getValue().get("cQtyLtrs")))>
                   																		<fo:block text-align="left" text-indent="15pt">CM</fo:block>
                   																	</#if>	
                   																</fo:table-cell>
                   																<fo:table-cell>
                   																	<fo:block text-align="right">${(agentEntryValues.getValue().get("qtyLtrs"))?if_exists?string("##0.0")}</fo:block>
                   																</fo:table-cell>
                   																<fo:table-cell>
                   																	<fo:block text-align="right">${(agentEntryValues.getValue().get("qtyKgs"))?if_exists?string("##0.0")}</fo:block>
                   																</fo:table-cell>
                   																<fo:table-cell>
                   																	<fo:block text-align="right">${(agentEntryValues.getValue().get("fat"))?if_exists?string("##0.0")}</fo:block>
                   																</fo:table-cell>
                   																<fo:table-cell>
                   																	<fo:block text-align="right">${(agentEntryValues.getValue().get("snf"))?if_exists?string("##0.00")}</fo:block>
                   																</fo:table-cell>
                   																<fo:table-cell>
                   																	<fo:block text-align="right">${(agentEntryValues.getValue().get("kgFat"))?if_exists?string("##0.00")}</fo:block>
                   																</fo:table-cell>
                   																<fo:table-cell>
                   																	<fo:block text-align="right">${(agentEntryValues.getValue().get("kgSnf"))?if_exists?string("##0.00")}</fo:block>
                   																</fo:table-cell>
                   																<fo:table-cell>
                   																	<fo:block text-align="right">${(agentEntryValues.getValue().get("sQtyLtrs"))?if_exists?string("##0.0")}</fo:block>
                   																</fo:table-cell>
                   																<fo:table-cell>
                   																	<fo:block text-align="right">${((agentEntryValues.getValue().get("sQtyLtrs"))*1.03)?if_exists?string("##0.0")}</fo:block>
                   																</fo:table-cell>
                   																<fo:table-cell>
                   																	<fo:block text-align="right">${(agentEntryValues.getValue().get("sFat"))?if_exists?string("##0.0")}</fo:block>
                   																</fo:table-cell>
                   																<fo:table-cell>
                   																	<fo:block text-align="right">${((((agentEntryValues.getValue().get("sQtyLtrs"))*1.03)*(agentEntryValues.getValue().get("sFat")))/100)?if_exists?string("##0.00")}</fo:block>
                   																</fo:table-cell>
                   																<fo:table-cell>
                   																	<fo:block text-align="right">${((agentEntryValues.getValue().get("qtyLtrs"))+(agentEntryValues.getValue().get("sQtyLtrs")))?if_exists?string("##0.0")}</fo:block>
                   																</fo:table-cell>
                   																<fo:table-cell>
                   																	<fo:block text-align="right">${(agentEntryValues.getValue().get("qtyKgs")+((agentEntryValues.getValue().get("sQtyLtrs"))*1.03))?if_exists?string("##0.0")}</fo:block>
                   																</fo:table-cell>
                   																<fo:table-cell>
                   																	<fo:block text-align="right">${((agentEntryValues.getValue().get("kgFat"))+((((agentEntryValues.getValue().get("sQtyLtrs"))*1.03)*(agentEntryValues.getValue().get("sFat")))/100))?if_exists?string("##0.00")}</fo:block>
                   																</fo:table-cell>
                   																<fo:table-cell>
                   																	<fo:block text-align="right">${(agentEntryValues.getValue().get("kgSnf"))?if_exists?string("##0.00")}</fo:block>
                   																</fo:table-cell>
                   																<fo:table-cell>
                   																	<fo:block text-align="right">${(agentEntryValues.getValue().get("cQtyLtrs"))?if_exists?string("##0.0")}</fo:block>
                   																</fo:table-cell>
                   															</fo:table-row>
                   															</#if>
                   														</fo:table-body>
                    												</fo:table>
                    											</fo:block>
                    										</#if>                    											
                    									</#if>	
                    								</#list>
                    								</#list>
                    							</fo:table-cell>
                    						</fo:table-row>
                    						</#if>                    						
                    					</fo:table-body>
       								</fo:table>
       							</fo:block>       							
       							</#list>
       							</#if>
       							</#list>
       							</#list>
        					</#list>
        					<#assign rowCount=rowCount+6>
        					<#if (rowCount>numberOfLines)>
								<fo:block font-size="8pt" page-break-before="always">------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
								<#assign rowCount=9>
							<#else>
       							<fo:block font-size="8pt">------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
       						</#if>
        					<fo:block font-size="8pt">
        						<fo:table>
        							<fo:table-column column-width="30pt"/>
                   					<fo:table-column column-width="17pt"/>
                   					<fo:table-column column-width="35pt"/>
                   					<fo:table-column column-width="40pt"/>
                   					<fo:table-column column-width="50pt"/>
                   					<fo:table-column column-width="75pt"/>
                   					<fo:table-column column-width="40pt"/>
                   					<fo:table-column column-width="40pt"/>
                   					<fo:table-column column-width="40pt"/>
                   					<fo:table-column column-width="40pt"/>
                   					<fo:table-column column-width="40pt"/>
                   					<fo:table-column column-width="5pt"/>
                   					<fo:table-column column-width="35pt"/>
                   					<fo:table-column column-width="27pt"/>
                   					<fo:table-column column-width="35pt"/>
                   					<fo:table-column column-width="35pt"/>
                   					<fo:table-column column-width="45pt"/>
                   					<fo:table-column column-width="40pt"/>
                   					<fo:table-column column-width="37pt"/>
                   					<fo:table-column column-width="45pt"/>
                   					<fo:table-column column-width="30pt"/>
                   					<fo:table-column column-width="40pt"/>
                   					<fo:table-column column-width="40pt"/>
                   					<fo:table-body>
                   						<fo:table-row>
                   							<fo:table-cell>
                   								<fo:block>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "dd/MM/yyyy")}</fo:block>
                   							</fo:table-cell>
                   							<fo:table-cell></fo:table-cell>
                   							<fo:table-cell></fo:table-cell>
                   							<fo:table-cell></fo:table-cell>
                   							<fo:table-cell>
                   								<fo:block text-align="left" keep-together="always" text-indent="65pt" white-space-collapse="false">BM</fo:block>
        										<fo:block text-align="left" keep-together="always" text-indent="65pt" white-space-collapse="false">CM</fo:block>
                   							</fo:table-cell>                   							             							
                   							<fo:table-cell>
                   								<fo:block text-align="right" white-space-collapse="false">${rtBMTotLtrs?if_exists?string("##0.0")}</fo:block>
        										<fo:block text-align="right" white-space-collapse="false">${rtCMTotLtrs?if_exists?string("##0.0")}</fo:block>
                   							</fo:table-cell>
                   							<fo:table-cell>
                   								<fo:block text-align="right" keep-together="always" white-space-collapse="false">${rtBMTotKgs?if_exists?string("##0.0")}</fo:block>
        										<fo:block text-align="right" keep-together="always" white-space-collapse="false">${rtCMTotKgs?if_exists?string("##0.0")}</fo:block>
                   							</fo:table-cell>
                   							<fo:table-cell>
                   								<fo:block text-align="right" keep-together="always" white-space-collapse="false"><#if rtBMTotKgs !=0>${((rtBMTotKgFat*100)/rtBMTotKgs)?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
        										<fo:block text-align="right" keep-together="always" white-space-collapse="false"><#if rtCMTotKgs !=0>${((rtCMTotKgFat*100)/rtCMTotKgs)?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
                   							</fo:table-cell>
                   							<fo:table-cell>
                   								<fo:block text-align="right" keep-together="always" white-space-collapse="false"><#if rtBMTotKgs !=0>${((rtBMTotKgSnf*100)/rtBMTotKgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
        										<fo:block text-align="right" keep-together="always" white-space-collapse="false"><#if rtCMTotKgs !=0>${((rtCMTotKgSnf*100)/rtCMTotKgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
                   							</fo:table-cell>
                   							<fo:table-cell>
                   								<fo:block text-align="right" keep-together="always" white-space-collapse="false">${rtBMTotKgFat?if_exists?string("##0.00")}</fo:block>
        										<fo:block text-align="right" keep-together="always" white-space-collapse="false">${rtCMTotKgFat?if_exists?string("##0.00")}</fo:block>
                   							</fo:table-cell>
                   							<fo:table-cell>
                   								<fo:block text-align="right" keep-together="always" white-space-collapse="false">${rtBMTotKgSnf?if_exists?string("##0.00")}</fo:block>
        										<fo:block text-align="right" keep-together="always" white-space-collapse="false">${rtCMTotKgSnf?if_exists?string("##0.00")}</fo:block>
                   							</fo:table-cell>
                   							<fo:table-cell></fo:table-cell>
                   							<fo:table-cell>
                   								<fo:block text-align="right" keep-together="always" white-space-collapse="false">${rtBMTotSltrs?if_exists?string("##0.0")}</fo:block>
        										<fo:block text-align="right" keep-together="always" white-space-collapse="false">${rtCMTotSltrs?if_exists?string("##0.0")}</fo:block>
                   							</fo:table-cell>
                   							<fo:table-cell>
                   								<fo:block text-align="right" keep-together="always" white-space-collapse="false">${rtBMTotSKgs?if_exists?string("##0.0")}</fo:block>
        										<fo:block text-align="right" keep-together="always" white-space-collapse="false">${rtCMTotSKgs?if_exists?string("##0.0")}</fo:block>
                   							</fo:table-cell>
                   							<fo:table-cell>
                   								<fo:block text-align="right" keep-together="always" white-space-collapse="false"><#if rtBMTotSKgs !=0>${((rtBMTotSKgFat*100)/rtBMTotSKgs)?if_exists?string("##0.00")}<#else>0.0</#if></fo:block>
        										<fo:block text-align="right" keep-together="always" white-space-collapse="false"><#if rtCMTotSKgs !=0>${((rtCMTotSKgFat*100)/rtCMTotSKgs)?if_exists?string("##0.00")}<#else>0.0</#if></fo:block>
                   							</fo:table-cell>
                   							<fo:table-cell>
                   								<fo:block text-align="right" keep-together="always" white-space-collapse="false">${rtBMTotSKgFat?if_exists?string("##0.00")}</fo:block>
        										<fo:block text-align="right" keep-together="always" white-space-collapse="false">${rtCMTotSKgFat?if_exists?string("##0.00")}</fo:block>
                   							</fo:table-cell>
                   							<fo:table-cell>
                   								<fo:block text-align="right" keep-together="always" white-space-collapse="false">${(rtBMTotLtrs+rtBMTotSltrs)?if_exists?string("##0.0")}</fo:block>
        										<fo:block text-align="right" keep-together="always" white-space-collapse="false">${(rtCMTotLtrs+rtCMTotSltrs)?if_exists?string("##0.0")}</fo:block>
                   							</fo:table-cell>
                   							<fo:table-cell>
                   								<fo:block text-align="right" keep-together="always" white-space-collapse="false">${(rtBMTotKgs+rtBMTotSKgs)?if_exists?string("##0.0")}</fo:block>
        										<fo:block text-align="right" keep-together="always" white-space-collapse="false">${(rtCMTotKgs+rtCMTotSKgs)?if_exists?string("##0.0")}</fo:block>
                   							</fo:table-cell>
                   							<fo:table-cell>
                   								<fo:block text-align="right" keep-together="always" white-space-collapse="false">${(rtBMTotKgFat+rtBMTotSKgFat)?if_exists?string("##0.00")}</fo:block>
        										<fo:block text-align="right" keep-together="always" white-space-collapse="false">${(rtCMTotKgFat+rtCMTotSKgFat)?if_exists?string("##0.00")}</fo:block>
                   							</fo:table-cell>
                   							<fo:table-cell>
                   								<fo:block text-align="right" keep-together="always" white-space-collapse="false">${(rtBMTotKgSnf)?if_exists?string("##0.00")}</fo:block>
        										<fo:block text-align="right" keep-together="always" white-space-collapse="false">${(rtCMTotKgSnf)?if_exists?string("##0.00")}</fo:block>
                   							</fo:table-cell>
                   							<fo:table-cell>
                   								<fo:block text-align="right" keep-together="always" white-space-collapse="false">${(rtBMCurLdLts)?if_exists?string("##0.0")}</fo:block>
        										<fo:block text-align="right" keep-together="always" white-space-collapse="false">${(rtCMCurLdLts)?if_exists?string("##0.0")}</fo:block>
                   							</fo:table-cell>
                   						</fo:table-row>
                   						<fo:table-row>
                   							<fo:table-cell>
                   								<fo:block font-size="8pt"  keep-together="always" white-space-collapse="false">.                                     ----------------------------------------------------------------------------------------------------------------------------</fo:block>	
                   							</fo:table-cell>
                   						</fo:table-row>
                   						<fo:table-row>
                   							<fo:table-cell><fo:block keep-together="always">${routeDetails.description?if_exists}</fo:block></fo:table-cell>
                   							<fo:table-cell></fo:table-cell>
                   							<fo:table-cell></fo:table-cell>
                   							
                   							<fo:table-cell><fo:block keep-together="always" white-space-collapse="false" text-align="left" text-indent="5pt">ROUTE TOTALS :</fo:block></fo:table-cell>
                   							<fo:table-cell></fo:table-cell>
                   							<fo:table-cell>
                   								<#assign grTotLtrs = grTotLtrs+(rtBMTotLtrs+rtCMTotLtrs)>
                   								<#assign grTotKgs = grTotKgs+(rtBMTotKgs+rtCMTotKgs)>
                   								<#assign grTotKgFat = grTotKgFat+(rtBMTotKgFat+rtCMTotKgFat)>
                   								<#assign grTotKgSnf = grTotKgSnf+(rtBMTotKgSnf+rtCMTotKgSnf)>
                   								
                   								<#assign grTotSLtrs = grTotSLtrs+(rtBMTotSltrs+rtCMTotSltrs)>
                   								<#assign grTotSKgs = grTotSKgs+(rtBMTotSKgs+rtCMTotSKgs)>
                   								<#assign grTotSFat = grTotSFat+(rtBMTotSKgFat+rtCMTotSKgFat)>
                   								<#assign grTotCurdLtrs = grTotCurdLtrs+(rtBMCurLdLts+rtCMCurLdLts)>
                   								<fo:block text-align="right">${(rtBMTotLtrs+rtCMTotLtrs)?if_exists?string("##0.0")}</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell>
                   								<fo:block text-align="right" white-space-collapse="false">${(rtBMTotKgs+rtCMTotKgs)?if_exists?string("##0.0")}</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell>
                   								<fo:block text-align="right" white-space-collapse="false"><#if (rtBMTotKgs+rtCMTotKgs) !=0>${(((rtBMTotKgFat+rtCMTotKgFat)*100)/(rtBMTotKgs+rtCMTotKgs))?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
        									</fo:table-cell>
                   							<fo:table-cell>
                   								<fo:block text-align="right" white-space-collapse="false"><#if (rtBMTotKgs+rtCMTotKgs) !=0>${(((rtBMTotKgSnf+rtCMTotKgSnf)*100)/(rtBMTotKgs+rtCMTotKgs))?if_exists?string("##0.00")}<#else>0.0</#if></fo:block>
                   							</fo:table-cell>
                   							<fo:table-cell>
                   								<fo:block text-align="right" white-space-collapse="false">${(rtBMTotKgFat+rtCMTotKgFat)?if_exists?string("##0.00")}</fo:block>
        									</fo:table-cell>
                   							<fo:table-cell>
                   								<fo:block text-align="right" white-space-collapse="false">${(rtBMTotKgSnf+rtCMTotKgSnf)?if_exists?string("##0.00")}</fo:block>
        									</fo:table-cell>
                   							<fo:table-cell></fo:table-cell>
                   							<fo:table-cell>
                   								<fo:block text-align="right" white-space-collapse="false">${(rtBMTotSltrs+rtCMTotSltrs)?if_exists?string("##0.0")}</fo:block>
        									</fo:table-cell>
                   							<fo:table-cell>
                   								<fo:block text-align="right" white-space-collapse="false">${(rtBMTotSKgs+rtCMTotSKgs)?if_exists?string("##0.0")}</fo:block>
        									</fo:table-cell>
        									<fo:table-cell>
        										<fo:block text-align="right" white-space-collapse="false"><#if (rtBMTotSKgs+rtCMTotSKgs) !=0>${(((rtBMTotSKgFat+rtCMTotSKgFat)*100)/(rtBMTotSKgs+rtCMTotSKgs))?if_exists?string("##0.00")}<#else>0.0</#if></fo:block>
        									</fo:table-cell>
                   							<fo:table-cell>
                   								<fo:block text-align="right" white-space-collapse="false">${(rtBMTotSKgFat+rtCMTotSKgFat)?if_exists?string("##0.00")}</fo:block>
        									</fo:table-cell>
                   							<fo:table-cell>
                   								<fo:block text-align="right" white-space-collapse="false">${((rtBMTotLtrs+rtBMTotSltrs)+(rtCMTotLtrs+rtCMTotSltrs))?if_exists?string("##0.0")}</fo:block>
        									</fo:table-cell>
                   							<fo:table-cell>
                   								<fo:block text-align="right" white-space-collapse="false">${((rtBMTotKgs+rtBMTotSKgs)+(rtCMTotKgs+rtCMTotSKgs))?if_exists?string("##0.0")}</fo:block>
        									</fo:table-cell>
        									<fo:table-cell>
                   								<fo:block text-align="right" white-space-collapse="false">${((rtBMTotKgFat+rtBMTotSKgFat)+(rtCMTotKgFat+rtCMTotSKgFat))?if_exists?string("##0.00")}</fo:block>
        									</fo:table-cell>
                   							<fo:table-cell>
                   								<fo:block text-align="right" white-space-collapse="false">${(rtBMTotKgSnf+rtCMTotKgSnf)?if_exists?string("##0.00")}</fo:block>
                   							</fo:table-cell>
                   							<fo:table-cell>
                   								<fo:block text-align="right" keep-together="always" white-space-collapse="false">${(rtBMCurLdLts+rtCMCurLdLts)?if_exists?string("##0.0")}</fo:block>
        									</fo:table-cell>
                   						</fo:table-row>
                   						<fo:table-row>
                   							<fo:table-cell>
                   								<fo:block font-size="8pt">------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                   							</fo:table-cell>
                   						</fo:table-row>
                   					</fo:table-body>
        						</fo:table>
        					</fo:block>      					
        				</#list>
        				</#list>   
        				<fo:block linefeed-treatment="preserve" font-size="8pt">&#xA;</fo:block>
        				<#assign dayTotalEntries = DayGrandTotalsMap.entrySet()>        				
        				<#list dayTotalEntries as dayWiseValues>
        					<#if dayWiseValues.getKey() !="TOT">
        						<#assign purchageTimeWiseValues = dayWiseValues.getValue().entrySet()>
        						<#list purchageTimeWiseValues as purchageTimeEntries>
        							<#if purchageTimeEntries.getKey() !="TOT">
        								<#assign milkTypeEntries = purchageTimeEntries.getValue().entrySet()>        						
                    					<#list milkTypeEntries as milkTypeGrTotValues>
                    						<#if milkTypeGrTotValues.getKey() !="TOT">
                    							<#assign milkType = milkTypeMap.entrySet()>
                    							<#list procurementProductList as milkTypeValue>
                    								<#if milkTypeGrTotValues.getKey() == milkTypeValue.productName>
								        				<fo:block font-size="8pt">
								        					<fo:table>
								        							<fo:table-column column-width="30pt"/>
								                   					<fo:table-column column-width="20pt"/>
								                   					<fo:table-column column-width="35pt"/>
								                   					<fo:table-column column-width="40pt"/>
								                   					<fo:table-column column-width="40pt"/>
								                   					<fo:table-column column-width="40pt"/>
								                   					<fo:table-column column-width="40pt"/>
								                   					<fo:table-column column-width="48pt"/>
								                   					<fo:table-column column-width="33pt"/>
								                   					<fo:table-column column-width="35pt"/>
								                   					<fo:table-column column-width="45pt"/>
								                   					<fo:table-column column-width="45pt"/>
								                   					<fo:table-column column-width="15pt"/>
								                   					<fo:table-column column-width="15pt"/>
								                   					<fo:table-column column-width="30pt"/>
								                   					<fo:table-column column-width="35pt"/>
								                   					<fo:table-column column-width="35pt"/>
								                   					<fo:table-column column-width="45pt"/>
								                   					<fo:table-column column-width="45pt"/>
								                   					<fo:table-column column-width="45pt"/>
								                   					<fo:table-column column-width="43pt"/>
								                   					<fo:table-column column-width="25pt"/>
								                   					<fo:table-column column-width="40pt"/>
								                   				<fo:table-body>
								                   					<fo:table-row>
								                   						<fo:table-cell>
								                   							<fo:block></fo:block>
								                   						</fo:table-cell>
								                   						<fo:table-cell></fo:table-cell>
								                   						<fo:table-cell></fo:table-cell>
								                   						<fo:table-cell></fo:table-cell>                   						
								                   						<fo:table-cell>
								                   							<fo:block text-indent="40pt">${purchageTimeEntries.getKey()}</fo:block>
								                   						</fo:table-cell>
								                   						<fo:table-cell>
								                   							<fo:block keep-together="always" text-indent="23pt">${milkTypeValue.brandName}</fo:block>
								                   						</fo:table-cell>
								                   						<fo:table-cell>
								                   							<fo:block keep-together="always" text-align="right">${milkTypeGrTotValues.getValue().get("qtyLtrs")?string("##0.0#")}</fo:block>
								                   						</fo:table-cell>
								                   						<fo:table-cell>
								                   							<fo:block keep-together="always" text-align="right">${milkTypeGrTotValues.getValue().get("qtyKgs")?string("##0.0#")}</fo:block>
								                   						</fo:table-cell>
								                   						<fo:table-cell>
								                   							<fo:block keep-together="always" text-align="right">${milkTypeGrTotValues.getValue().get("fat")?string("##0.0#")}</fo:block>
								                   						</fo:table-cell>
								                   						<fo:table-cell>
								                   							<fo:block keep-together="always" text-align="right">${milkTypeGrTotValues.getValue().get("snf")?string("##0.0#")}</fo:block>
								                   						</fo:table-cell>
								                   						<fo:table-cell>
								                   							<fo:block keep-together="always" text-align="right">${milkTypeGrTotValues.getValue().get("kgFat")?string("##0.0#")}</fo:block>
								                   						</fo:table-cell>
								                   						<fo:table-cell>
								                   							<fo:block keep-together="always" text-align="right">${milkTypeGrTotValues.getValue().get("kgSnf")?string("##0.0#")}</fo:block>
								                   						</fo:table-cell>
								                   						<fo:table-cell></fo:table-cell>
								                   						<fo:table-cell>
																			<fo:block text-align="right">${(milkTypeGrTotValues.getValue().get("sQtyLtrs"))?if_exists?string("##0.0")}</fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="right">${((milkTypeGrTotValues.getValue().get("sQtyLtrs"))*1.03)?if_exists?string("##0.0")}</fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="right">${(milkTypeGrTotValues.getValue().get("sFat"))?if_exists?string("##0.0")}</fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="right">${((((milkTypeGrTotValues.getValue().get("sQtyLtrs"))*1.03)*(milkTypeGrTotValues.getValue().get("sFat")))/100)?if_exists?string("##0.00")}</fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="right">${((milkTypeGrTotValues.getValue().get("qtyLtrs"))+(milkTypeGrTotValues.getValue().get("sQtyLtrs")))?if_exists?string("##0.0")}</fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="right">${(milkTypeGrTotValues.getValue().get("qtyKgs")+((milkTypeGrTotValues.getValue().get("sQtyLtrs"))*1.03))?if_exists?string("##0.0")}</fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="right">${((milkTypeGrTotValues.getValue().get("kgFat"))+((((milkTypeGrTotValues.getValue().get("sQtyLtrs"))*1.03)*(milkTypeGrTotValues.getValue().get("sFat")))/100))?if_exists?string("##0.00")}</fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="right">${(milkTypeGrTotValues.getValue().get("kgSnf"))?if_exists?string("##0.00")}</fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="right">${(milkTypeGrTotValues.getValue().get("cQtyLtrs"))?if_exists?string("##0.0")}</fo:block>
																		</fo:table-cell>
								                   					</fo:table-row>
								                   				</fo:table-body>
								        					</fo:table>
								        				</fo:block>
        											</#if>
        										</#list>
        									</#if>
        								</#list>
        							</#if>
        						</#list>
        					<#else>
        					<fo:block linefeed-treatment="preserve" font-size="8pt">&#xA;</fo:block>
        					<#list procurementProductList as milkTypeValue>
		        				<fo:block font-size="8pt">
		        					<fo:table>
		        							<fo:table-column column-width="30pt"/>
		                   					<fo:table-column column-width="20pt"/>
		                   					<fo:table-column column-width="35pt"/>
		                   					<fo:table-column column-width="40pt"/>
		                   					<fo:table-column column-width="40pt"/>
		                   					<fo:table-column column-width="40pt"/>
		                   					<fo:table-column column-width="40pt"/>
		                   					<fo:table-column column-width="48pt"/>
		                   					<fo:table-column column-width="33pt"/>
		                   					<fo:table-column column-width="35pt"/>
		                   					<fo:table-column column-width="45pt"/>
		                   					<fo:table-column column-width="45pt"/>
		                   					<fo:table-column column-width="15pt"/>
		                   					<fo:table-column column-width="15pt"/>
		                   					<fo:table-column column-width="30pt"/>
		                   					<fo:table-column column-width="35pt"/>
		                   					<fo:table-column column-width="35pt"/>
		                   					<fo:table-column column-width="45pt"/>
		                   					<fo:table-column column-width="45pt"/>
		                   					<fo:table-column column-width="45pt"/>
		                   					<fo:table-column column-width="43pt"/>
		                   					<fo:table-column column-width="25pt"/>
		                   					<fo:table-column column-width="40pt"/>
		                   				<fo:table-body>
		                   					<fo:table-row>
		                   						<fo:table-cell>
		                   							<fo:block></fo:block>
		                   						</fo:table-cell>
		                   						<fo:table-cell></fo:table-cell>
		                   						<fo:table-cell></fo:table-cell>
		                   						<fo:table-cell>
		                   							<fo:block text-indent="40pt">AM&amp;PM</fo:block>
		                   						</fo:table-cell>                   						
		                   						<fo:table-cell>								                   							
		                   						</fo:table-cell>
		                   						<fo:table-cell>
		                   							<fo:block keep-together="always" text-indent="23pt">${milkTypeValue.brandName}</fo:block>
		                   						</fo:table-cell>
		                   						<fo:table-cell>
		                   							<fo:block keep-together="always" text-align="right">${dayWiseValues.getValue().get("TOT")[milkTypeValue.productName].get("qtyLtrs")?string("##0.0#")}</fo:block>
		                   						</fo:table-cell>
		                   						<fo:table-cell>
		                   							<fo:block keep-together="always" text-align="right">${dayWiseValues.getValue().get("TOT")[milkTypeValue.productName].get("qtyKgs")?string("##0.0#")}</fo:block>
		                   						</fo:table-cell>
		                   						<fo:table-cell>
		                   							<fo:block keep-together="always" text-align="right">${dayWiseValues.getValue().get("TOT")[milkTypeValue.productName].get("fat")?string("##0.0#")}</fo:block>
		                   						</fo:table-cell>
		                   						<fo:table-cell>
		                   							<fo:block keep-together="always" text-align="right">${dayWiseValues.getValue().get("TOT")[milkTypeValue.productName].get("snf")?string("##0.0#")}</fo:block>
		                   						</fo:table-cell>
		                   						<fo:table-cell>
		                   							<fo:block keep-together="always" text-align="right">${dayWiseValues.getValue().get("TOT")[milkTypeValue.productName].get("kgFat")?string("##0.0#")}</fo:block>
		                   						</fo:table-cell>
		                   						<fo:table-cell>
		                   							<fo:block keep-together="always" text-align="right">${dayWiseValues.getValue().get("TOT")[milkTypeValue.productName].get("kgSnf")?string("##0.0#")}</fo:block>
		                   						</fo:table-cell>
		                   						<fo:table-cell></fo:table-cell>
		                   						<fo:table-cell>
													<fo:block text-align="right">${(dayWiseValues.getValue().get("TOT")[milkTypeValue.productName].get("sQtyLtrs"))?if_exists?string("##0.0")}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block text-align="right">${((dayWiseValues.getValue().get("TOT")[milkTypeValue.productName].get("sQtyLtrs"))*1.03)?if_exists?string("##0.0")}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block text-align="right">${(dayWiseValues.getValue().get("TOT")[milkTypeValue.productName].get("sFat"))?if_exists?string("##0.0")}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block text-align="right">${((((dayWiseValues.getValue().get("TOT")[milkTypeValue.productName].get("sQtyLtrs"))*1.03)*(dayWiseValues.getValue().get("TOT")[milkTypeValue.productName].get("sFat")))/100)?if_exists?string("##0.00")}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block text-align="right">${((dayWiseValues.getValue().get("TOT")[milkTypeValue.productName].get("qtyLtrs"))+(dayWiseValues.getValue().get("TOT")[milkTypeValue.productName].get("sQtyLtrs")))?if_exists?string("##0.0")}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block text-align="right">${(dayWiseValues.getValue().get("TOT")[milkTypeValue.productName].get("qtyKgs")+((dayWiseValues.getValue().get("TOT")[milkTypeValue.productName].get("sQtyLtrs"))*1.03))?if_exists?string("##0.0")}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block text-align="right">${((dayWiseValues.getValue().get("TOT")[milkTypeValue.productName].get("kgFat"))+((((dayWiseValues.getValue().get("TOT")[milkTypeValue.productName].get("sQtyLtrs"))*1.03)*(dayWiseValues.getValue().get("TOT")[milkTypeValue.productName].get("sFat")))/100))?if_exists?string("##0.00")}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block text-align="right">${(dayWiseValues.getValue().get("TOT")[milkTypeValue.productName].get("kgSnf"))?if_exists?string("##0.00")}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block text-align="right">${(dayWiseValues.getValue().get("TOT")[milkTypeValue.productName].get("cQtyLtrs"))?if_exists?string("##0.0")}</fo:block>
												</fo:table-cell>
		                   					</fo:table-row>
		                   				</fo:table-body>
		        					</fo:table>
		        				</fo:block>
        					</#list>
        					</#if>
        				</#list> 
        				<fo:block font-size="8pt" white-space-collapse="false">.                                  ---------------------------------------------------------------------------------------------------------------------------------</fo:block>				
        				<fo:block font-size="8pt">
        					<fo:table>
        						<fo:table-column column-width="30pt"/>
                   					<fo:table-column column-width="17pt"/>
                   					<fo:table-column column-width="35pt"/>
                   					<fo:table-column column-width="35pt"/>
                   					<fo:table-column column-width="40pt"/>
                   					<fo:table-column column-width="50pt"/>
                   					<fo:table-column column-width="40pt"/>
                   					<fo:table-column column-width="45pt"/>
                   					<fo:table-column column-width="35pt"/>
                   					<fo:table-column column-width="35pt"/>
                   					<fo:table-column column-width="45pt"/>
                   					<fo:table-column column-width="45pt"/>
                   					<fo:table-column column-width="3pt"/>
                   					<fo:table-column column-width="15pt"/>
                   					<fo:table-column column-width="40pt"/>
                   					<fo:table-column column-width="38pt"/>
                   					<fo:table-column column-width="33pt"/>
                   					<fo:table-column column-width="45pt"/>
                   					<fo:table-column column-width="45pt"/>
                   					<fo:table-column column-width="45pt"/>
                   					<fo:table-column column-width="43pt"/>
                   					<fo:table-column column-width="25pt"/>
                   					<fo:table-column column-width="40pt"/>
                   				<fo:table-body>
                   					<fo:table-row>
                   						<fo:table-cell>
                   							<fo:block>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "dd/MM/yyyy")}</fo:block>
                   						</fo:table-cell>
                   						<fo:table-cell></fo:table-cell>
                   						<fo:table-cell></fo:table-cell>
                   						<fo:table-cell></fo:table-cell>
                   						<fo:table-cell>
                   							<fo:block keep-together="always">DAY GRAND TOTAL :</fo:block>
                   						</fo:table-cell>
                   						<fo:table-cell>
                   							<fo:block keep-together="always"></fo:block>
                   						</fo:table-cell>
                   						<fo:table-cell>
                   							<fo:block text-align="right">${grTotLtrs?if_exists?string("##0.0")}</fo:block>
                   						</fo:table-cell>
                   						<fo:table-cell>
                   							<fo:block text-align="right">${grTotKgs?if_exists?string("##0.0")}</fo:block>
                   						</fo:table-cell>
                   						<fo:table-cell>
                   							<fo:block text-align="right"><#if grTotKgs!=0>${((grTotKgFat*100)/grTotKgs)?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
                   						</fo:table-cell>
                   						<fo:table-cell>
                   							<fo:block text-align="right"><#if grTotKgs!=0>${((grTotKgSnf*100)/grTotKgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
                   						</fo:table-cell>
                   						<fo:table-cell>
                   							<fo:block text-align="right">${grTotKgFat?if_exists?string("##0.00")}</fo:block>
                   						</fo:table-cell>
                   						<fo:table-cell>
                   							<fo:block text-align="right">${(grTotKgSnf)?if_exists?string("##0.00")}</fo:block>
                   						</fo:table-cell>
                   						<fo:table-cell></fo:table-cell>
                   						<fo:table-cell>
                   							 <fo:block text-align="left" text-indent="10pt">${(grTotSLtrs)?if_exists?string("##0.0")}</fo:block>
                   						</fo:table-cell>
                   						<fo:table-cell>
                   							<fo:block text-align="right">${(grTotSKgs)?if_exists?string("##0.0")}</fo:block>
                   						</fo:table-cell>
                   						<fo:table-cell>
                   							<fo:block keep-together="always" text-align="right"><#if grTotSKgs !=0>${((grTotSFat*100)/grTotSKgs)?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
                   						</fo:table-cell>
                   						<fo:table-cell>
                   							<fo:block keep-together="always" text-align="right">${grTotSFat?if_exists?string("##0.00")}</fo:block>
                   						</fo:table-cell>
                   						<fo:table-cell>
                   							<fo:block keep-together="always" text-align="right">${(grTotLtrs+grTotSLtrs)?if_exists?string("##0.0")} </fo:block>
                   						</fo:table-cell>
                   						<fo:table-cell>
                   							<fo:block keep-together="always" text-align="right">${(grTotKgs+grTotSKgs)?if_exists?string("##0.0")}</fo:block>
                   						</fo:table-cell>
                   						<fo:table-cell>
                   							<fo:block keep-together="always" text-align="right">${(grTotKgFat+grTotSFat)?if_exists?string("##0.00")}</fo:block>
                   						</fo:table-cell>
                   						<fo:table-cell>
                   							<fo:block keep-together="always" text-align="right">${(grTotKgSnf)?if_exists?string("##0.00")}</fo:block>
                   						</fo:table-cell>
                   						<fo:table-cell>
                   							<fo:block keep-together="always" text-align="right">${(grTotCurdLtrs)?if_exists?string("##0.0")}</fo:block>
                   						</fo:table-cell>
                   					</fo:table-row>
                   				</fo:table-body>
        					</fo:table>
        				</fo:block>
        				<fo:block font-size="8pt">------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>     				
        			</fo:flow>		
	   			</fo:page-sequence>	   	
			</#list>
		<#else>
			<fo:page-sequence master-reference="main">
    			<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
       		 		<fo:block font-size="14pt">
            			${uiLabelMap.NoOrdersFound}.
       		 		</fo:block>
    			</fo:flow>
			</fo:page-sequence>
		</#if> 
</fo:root>
</#escape>