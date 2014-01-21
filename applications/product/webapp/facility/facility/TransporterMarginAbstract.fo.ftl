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
        <fo:region-body margin-top="1.2in"/>
        <fo:region-before extent=".5in"/>
        <fo:region-after extent="1in"/>
    </fo:simple-page-master>
</fo:layout-master-set>
			<#if masterList?has_content>		   
				<fo:page-sequence master-reference="main" >
					<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
						<fo:block text-align="center" keep-together="always"> KRISHNAVENI KRISHNA DISTRICT MPCULTD</fo:block>
						<fo:block text-align="center" keep-together="always">VIJAYAWADA</fo:block>
						<fo:block text-align="center" keep-together="always"> ROUTE DESPATCHES &amp; PAYMENT ABSTRACT</fo:block>
						<fo:block text-align="center" keep-together="always" white-space-collapse="false">FROMDATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "dd/MM/yyyy")}   TO  DATE :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "dd/MM/yyyy")}</fo:block>				    		
		            <fo:block >------------------------------------------------------------------------------------</fo:block>
		            <fo:block text-align="left" keep-together="always"  white-space-collapse="false"> ROUTE        QUANTITY   MARGIN AMOUNT   PENDING     NET</fo:block>
		            <fo:block >------------------------------------------------------------------------------------</fo:block>
		            </fo:static-content>
					<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
						<fo:block>
							<fo:table >
                    			<fo:table-column column-width="60pt"/>
                   				<fo:table-column column-width="85pt"/>                
                    			<fo:table-column column-width="90pt"/>
                    			<fo:table-column column-width="95pt"/>
                    			<fo:table-column column-width="90pt"/>
                    			<fo:table-column column-width="90pt"/>
                    			<fo:table-column column-width="90pt"/>
                    			<fo:table-column column-width="90pt"/>                    										
                    			<fo:table-body>
                    			<#assign totGrTotQty = (Static["java.math.BigDecimal"].ZERO)>
                    			<#assign totGrTotRtAmt = (Static["java.math.BigDecimal"].ZERO)>  
                    			<#assign totGrTotPendingDue = (Static["java.math.BigDecimal"].ZERO)>  
                    			<#assign totGrTotNetPayable = (Static["java.math.BigDecimal"].ZERO)>              			
                    			<#list masterList as trnsptMarginReportEntry>
									<#assign trnsptMarginReportEntries = (trnsptMarginReportEntry).entrySet()>	
										<#list trnsptMarginReportEntries as trnsptMarginValues> 
											<#assign trnsptMarginEntries = (trnsptMarginValues.getValue())>
												<#list trnsptMarginEntries as trnsptMarginEntry>
													<#assign daywiseTrnsptMarginEntries = trnsptMarginEntry.entrySet()>
														<#assign grTotPaidAmt = (Static["java.math.BigDecimal"].ZERO)>
															<#list daywiseTrnsptMarginEntries as daywiseTrnsptEntry>
                    											<#if daywiseTrnsptEntry.getKey() =="Tot">                    							
                    												<#assign grTotRtAmt = daywiseTrnsptEntry.getValue().get("grTotRtAmount")>
                    												<#assign grTotpendingDue = daywiseTrnsptEntry.getValue().get("grTotpendingDue")>
                    												<#assign vehicleDue = daywiseTrnsptEntry.getValue().get("grTotpendingDue")>
                    												<#assign netPayable = grTotRtAmt.subtract(vehicleDue)>
                    												
                    												<#assign totGrTotQty = totGrTotQty.add(daywiseTrnsptEntry.getValue().get("grTotQty"))>
                    												<#assign totGrTotRtAmt = totGrTotRtAmt.add(grTotRtAmt)>
                    												<#assign totGrTotPendingDue = totGrTotPendingDue.add(grTotpendingDue)>
                    												<#assign totGrTotNetPayable = totGrTotNetPayable.add(netPayable)>
                    											<fo:table-row>
                    												<fo:table-cell><fo:block>${trnsptMarginValues.getKey()}</fo:block></fo:table-cell>
                    												<fo:table-cell><fo:block text-align="right">${daywiseTrnsptEntry.getValue().get("grTotQty").toEngineeringString()}</fo:block></fo:table-cell>
                    												<fo:table-cell><fo:block text-align="right">${grTotRtAmt.toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    												<fo:table-cell><fo:block text-align="right">${grTotpendingDue.toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    												<fo:table-cell><fo:block text-align="right">${netPayable.toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    											</fo:table-row>
                    											<fo:table-row>
                    												<fo:table-cell><fo:block >------------------------------------------------------------------------------------</fo:block></fo:table-cell>
                    											</fo:table-row>
                    										</#if>	
                    									</#list>	
													</#list>
												</#list>
		 	    							</#list>
                    						<fo:table-row>
                    							<fo:table-cell><fo:block>TOTAL</fo:block></fo:table-cell>
                    							<fo:table-cell><fo:block text-align="right">${totGrTotQty.toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    							<fo:table-cell><fo:block text-align="right">${totGrTotRtAmt.toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    							<fo:table-cell><fo:block text-align="right">${totGrTotPendingDue.toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    							<fo:table-cell><fo:block text-align="right">${totGrTotNetPayable.toEngineeringString()?if_exists}</fo:block></fo:table-cell>
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