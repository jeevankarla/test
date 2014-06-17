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
    <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
            margin-top="0.5in" margin-bottom="1in" margin-left="1in" margin-right="1in">
        <fo:region-body margin-top="1.4in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>
    </fo:simple-page-master>
</fo:layout-master-set>

			<#if masterList?has_content>	
				<#assign partyFacilityMapList = (partyFacilityMap).entrySet()>		   
				<fo:page-sequence master-reference="main" >
					<fo:static-content flow-name="xsl-region-before"  font-weight="7pt" font-family="Courier,monospace">
						<fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-weight="bold" font-size="10pt" keep-together="always"> MOTHER DAIRY, KMF UNIT	</fo:block>
						<fo:block text-align="center" font-weight="bold" font-size="10pt" white-space-collapse="false" keep-together="always">BANGALORE - 560065.</fo:block>
						<fo:block text-align="center" keep-together="always">CONTRCTOR WISE DISTRIBUTION TRANSPORT COST ABSTRACT REPORT</fo:block>
						<fo:block text-align="center" keep-together="always" white-space-collapse="false">FROMDATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "dd/MM/yyyy")}   TO  DATE :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "dd/MM/yyyy")}       ${uiLabelMap.CommonPage}:<fo:page-number/></fo:block>				    		
		            <fo:block >------------------------------------------------------------------------------------</fo:block>
		            <fo:block>
		            <fo:table >
                    			<fo:table-column column-width="100pt"/>
                   				<fo:table-column column-width="115pt"/>                
                    			<fo:table-column column-width="115pt"/>
                    			<fo:table-column column-width="115pt"/>
                    			<fo:table-column column-width="115pt"/>
                    			<fo:table-column column-width="115pt"/>
                    			<fo:table-column column-width="115pt"/>
                    			<fo:table-column column-width="115pt"/>                    										
                    			<fo:table-body>
                    			<fo:table-row>
                    		       <fo:table-cell>
                    		       <fo:block text-align="left" >ROUTE</fo:block>
                    		       </fo:table-cell>
                    		       
                    		        <fo:table-cell>
                    		        <fo:block text-align="right" > Distribution</fo:block>
                    		        <fo:block text-align="right" >Cost(Gross)</fo:block>
                    		        </fo:table-cell>
                    		        
                    		        <fo:table-cell>
                    		        <fo:block text-align="right" > Crates&amp;Cans</fo:block>
                    		         <fo:block text-align="right" >Recovery</fo:block>
                    		        </fo:table-cell>
                    		        
                    		        <fo:table-cell>
                    		          <fo:block text-align="right" > Penalties</fo:block>
                    		         <fo:block text-align="right" ></fo:block>
                    		        </fo:table-cell>
                    		        
                    		         <fo:table-cell>
                    		         <fo:block text-align="right" > NET-Amount</fo:block>
                    		         <fo:block text-align="right" ></fo:block>
                    		        </fo:table-cell>
                    		     </fo:table-row>
                    		     </fo:table-body>
                    		     </fo:table>
                        </fo:block>
		            <fo:block >------------------------------------------------------------------------------------</fo:block>
		            </fo:static-content>
					<fo:flow flow-name="xsl-region-body" font-weight="7pt" font-family="Courier,monospace">
						<fo:block>
							<fo:table >
                    			<fo:table-column column-width="100pt"/>
                   				<fo:table-column column-width="115pt"/>                
                    			<fo:table-column column-width="115pt"/>
                    			<fo:table-column column-width="115pt"/>
                    			<fo:table-column column-width="115pt"/>
                    			<fo:table-column column-width="115pt"/>
                    			<fo:table-column column-width="115pt"/>
                    			<fo:table-column column-width="115pt"/>                     										
                    			<fo:table-body>
                    			<#assign totGrTotRtAmt = (Static["java.math.BigDecimal"].ZERO)>  
                    			<#assign totCRandCanAmt = (Static["java.math.BigDecimal"].ZERO)>  
                    			<#assign totGrOthersAmt = (Static["java.math.BigDecimal"].ZERO)>  
                    			<#assign totGrTotNetPayable = (Static["java.math.BigDecimal"].ZERO)>   
                    			           			
                    			<#list partyFacilityMapList as eachPartyMap>
                    			
                    			<#assign totPartyAmt = (Static["java.math.BigDecimal"].ZERO)>  
                    			<#assign totPartyCrAndCanAmt = (Static["java.math.BigDecimal"].ZERO)>  
                    			<#assign totPartyOthersAmt = (Static["java.math.BigDecimal"].ZERO)>  
                    			<#assign totPartyNetPayable = (Static["java.math.BigDecimal"].ZERO)>
                    			 
                    			<#assign partyId= eachPartyMap.getKey()>
            			        <#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)>
            			       
                    			<#assign partyRouteIdsList= eachPartyMap.getValue()>
            			         <fo:table-row>
									<fo:table-cell  number-columns-spanned="4"><fo:block text-align="left"  >********ContractorId/Name:${partyId}/${partyName}********</fo:block></fo:table-cell>
								</fo:table-row>  
													
                    			<#list masterList as trnsptMarginReportEntry>
									<#assign trnsptMarginReportEntries = (trnsptMarginReportEntry).entrySet()>	
										<#list trnsptMarginReportEntries as trnsptMarginValues> 
										<#assign routeId= trnsptMarginValues.getKey()>
                    											
										<#if partyRouteIdsList.contains(routeId)><#-- filter  by Partyhere-->
											<#assign trnsptMarginEntries = (trnsptMarginValues.getValue())>
												<#list trnsptMarginEntries as trnsptMarginEntry>
													<#assign daywiseTrnsptMarginEntries = trnsptMarginEntry.entrySet()>
														<#assign grTotPaidAmt = (Static["java.math.BigDecimal"].ZERO)>
															<#list daywiseTrnsptMarginEntries as daywiseTrnsptEntry>
                    											<#if daywiseTrnsptEntry.getKey() =="Tot">                    							
                    												<#assign grTotRtAmt = daywiseTrnsptEntry.getValue().get("grTotRtAmount")>
                    												<#-- accessing fines And Penalities-->
                    												<#assign facRecvoryMap=facilityRecoveryInfoMap.get(trnsptMarginValues.getKey())?if_exists>
                    												<#assign totalDeduction=0>
													                   <#if facRecvoryMap?has_content>
													                   <#assign totalDeduction=facRecvoryMap.get("totalFine")?if_exists>
													                   </#if>
													                   
													                   <#assign totalCrAndCan=0>
													                    <#assign othersFine=0>
													                   <#assign crateDeduction=0>
													                    <#assign canDeduction=0>
													                    <#if facRecvoryMap?has_content>
													                   <#assign crateDeduction=facRecvoryMap.get("cratesFine")?if_exists>
													                   </#if>
													                   
													                     <#if facRecvoryMap?has_content>
													                   <#assign canDeduction=facRecvoryMap.get("cansFine")?if_exists>
													                   </#if>
													                     <#assign totalCrAndCan=crateDeduction+canDeduction>
													                   <#if facRecvoryMap?has_content>
													                   <#assign othersFine=facRecvoryMap.get("othersFine")?if_exists>
													                   </#if>
													                  
													                    <#assign totalCrAndCan=crateDeduction+canDeduction>
                    												   <#assign netPayable = grTotRtAmt.subtract(totalDeduction)>
                    												
                    												<#assign totPartyAmt = totPartyAmt.add(grTotRtAmt)>
                    												<#assign totPartyCrAndCanAmt = totPartyCrAndCanAmt.add(totalCrAndCan)>
                    												<#assign totPartyOthersAmt=  totPartyOthersAmt.add(othersFine)>
                    												<#assign totPartyNetPayable = totPartyNetPayable.add(netPayable)>
                    												
                    												 <#assign totGrTotRtAmt = totGrTotRtAmt.add(grTotRtAmt)>
                    												<#assign totCRandCanAmt = totCRandCanAmt.add(totalCrAndCan)>
                    												 <#assign totGrOthersAmt=totGrOthersAmt.add(othersFine)>
                    												<#assign totGrTotNetPayable = totGrTotNetPayable.add(netPayable)>
                    												
                    												
                    												 
                    											<fo:table-row>
                    												<fo:table-cell><fo:block>${trnsptMarginValues.getKey()?if_exists}</fo:block></fo:table-cell>
                    												<fo:table-cell><fo:block text-align="right">${grTotRtAmt.toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    												<fo:table-cell><fo:block text-align="right">${totalCrAndCan?string("#0.00")?if_exists}</fo:block></fo:table-cell>
                    												<fo:table-cell><fo:block text-align="right">${othersFine?string("#0.00")?if_exists}</fo:block></fo:table-cell>
                    												<#--><fo:table-cell><fo:block text-align="right">${grTotpendingDue.toEngineeringString()?if_exists}</fo:block></fo:table-cell>-->
                    												<fo:table-cell><fo:block text-align="right">${netPayable.toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    											</fo:table-row>
                    										</#if>	
                    									</#list>	
													</#list>
													 </#if>
												</#list>
		 	    							</#list>
		 	    							<fo:table-row>
												<fo:table-cell><fo:block >------------------------------------------------------------------------------------</fo:block></fo:table-cell>
											</fo:table-row>
		 	    							<fo:table-row>
												<fo:table-cell><fo:block>SubTot:</fo:block></fo:table-cell>
												<fo:table-cell><fo:block text-align="right">${totPartyAmt.toEngineeringString()?if_exists}</fo:block></fo:table-cell>
												<fo:table-cell><fo:block text-align="right">${totPartyCrAndCanAmt?string("#0.00")?if_exists}</fo:block></fo:table-cell>
												<fo:table-cell><fo:block text-align="right">${totPartyOthersAmt?string("#0.00")?if_exists}</fo:block></fo:table-cell>
												<fo:table-cell><fo:block text-align="right">${totPartyNetPayable?string("#0")?if_exists}</fo:block></fo:table-cell>
											</fo:table-row>
											<fo:table-row>
												<fo:table-cell><fo:block >------------------------------------------------------------------------------------</fo:block></fo:table-cell>
											</fo:table-row>
		 	    							</#list>
                    											
                    						<fo:table-row>
                    							<fo:table-cell><fo:block>TOTAL</fo:block></fo:table-cell>
                    							<fo:table-cell><fo:block text-align="right">${totGrTotRtAmt.toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    							<fo:table-cell><fo:block text-align="right">${totCRandCanAmt.toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    							<fo:table-cell><fo:block text-align="right">${totGrOthersAmt.toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    							<fo:table-cell><fo:block text-align="right">${totGrTotNetPayable?string("#0")?if_exists}</fo:block></fo:table-cell>
                    						</fo:table-row> 
                    						<fo:table-row>
                    							<fo:table-cell><fo:block >------------------------------------------------------------------------------------</fo:block></fo:table-cell>
                    						</fo:table-row>                   							
                    					</fo:table-body>
                    				</fo:table>								
								</fo:block>
							</fo:flow>	
		 				</fo:page-sequence>
					<#else>
						<fo:page-sequence master-reference="main">
			    			<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			       				<fo:block font-size="14pt">
			            			${uiLabelMap.OrderNoOrderFound}.
			       				</fo:block>
			    			</fo:flow>
						</fo:page-sequence>
					</#if>						
				</fo:root>
		</#escape>