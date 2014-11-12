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
    <fo:simple-page-master master-name="main" page-height="12in" page-width="12in"
            margin-top="0.3in" margin-bottom="1in" margin-left="0.3in" margin-right="0.3in">
        <fo:region-body margin-top="1.4in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>
    </fo:simple-page-master>
</fo:layout-master-set>
			<#if masterList?has_content>		   
				<fo:page-sequence master-reference="main" >
					<fo:static-content flow-name="xsl-region-before"  font-weight="7pt" font-family="Courier,monospace">
						<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      ${uiLabelMap.KMFDairyHeader}</fo:block>
						<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      ${uiLabelMap.KMFDairySubHeader}</fo:block>
						<fo:block text-align="center" font-weight="bold" font-size="10pt" keep-together="always"> ROUTE WISE DISTRIBUTION TRANSPORT COST ABSTRACT REPORT</fo:block>
						<fo:block text-align="center" font-weight="bold" font-size="10pt" keep-together="always" white-space-collapse="false">FROM: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "dd/MM/yyyy")}   TO:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "dd/MM/yyyy")}</fo:block>				    		
		            <fo:block >-------------------------------------------------------------------------------------------------------</fo:block>
		            <fo:block>
		            <fo:table >
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="70pt"/>
                    <fo:table-column column-width="90pt"/>  
               	    <fo:table-column column-width="100pt"/>
            		<fo:table-column column-width="80pt"/>
            		<fo:table-column column-width="90pt"/> 		
            		<fo:table-column column-width="90pt"/>
            		<fo:table-column column-width="80pt"/>
            		<fo:table-column column-width="90pt"/>                 										
                    			<fo:table-body>
                    			<fo:table-row font-weight = "bold" >
                    				<fo:table-cell>
                    		       		<fo:block text-align="left" font-size="10pt" keep-together="always">S.No</fo:block>
                    		       </fo:table-cell>
                    		       <fo:table-cell>
                    		       		<fo:block text-align="left" font-size="10pt" keep-together="always">ROUTE</fo:block>
                    		       </fo:table-cell>
                    		       <fo:table-cell>
                    		      		 <fo:block text-align="left" font-size="10pt" keep-together="always">Unit Rate</fo:block>
                    		       </fo:table-cell>
                    		       <fo:table-cell>
                    		       		<fo:block text-align="left" font-size="10pt" keep-together="always">Route Length</fo:block>
                    		       </fo:table-cell>
                    		        <fo:table-cell>
                    		        	<fo:block text-align="right" font-size="10pt" keep-together="always">Distribution</fo:block>
                    		        	<fo:block text-align="right" font-size="10pt" keep-together="always">Cost(Gross)</fo:block>
                    		        </fo:table-cell>
                    		        <fo:table-cell>
                    		        	<fo:block text-align="right" font-size="10pt" keep-together="always">Crates&amp;Cans</fo:block>
                    		         	<fo:block text-align="right" font-size="10pt" keep-together="always">Recovery</fo:block>
                    		        </fo:table-cell>
                    		        <fo:table-cell>
                		         	 	<fo:block text-align="right" font-size="10pt" keep-together="always">Penalties</fo:block>
                		        		<fo:block text-align="right" font-size="10pt" keep-together="always"></fo:block>
                    		        </fo:table-cell>
                    		        <fo:table-cell>
                		         	 	<fo:block text-align="right" font-size="10pt" keep-together="always">Diesel Hike</fo:block>
                		        		<fo:block text-align="right" font-size="10pt" keep-together="always">&amp; Payments</fo:block>
                    		        </fo:table-cell>
                    		         <fo:table-cell>
                    		         	<fo:block text-align="right" font-size="10pt" keep-together="always">NET-Amount</fo:block>
                    		         	<fo:block text-align="right" font-size="10pt" keep-together="always"></fo:block>
                    		        </fo:table-cell>
                    		     </fo:table-row>
                    		     </fo:table-body>
                    		     </fo:table>
                        </fo:block>
		           <fo:block >-------------------------------------------------------------------------------------------------------</fo:block>
		            </fo:static-content>
					<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
						<fo:block>
							<fo:table>
                    			<fo:table-column column-width="50pt"/>
			                    <fo:table-column column-width="70pt"/>
			                    <fo:table-column column-width="60pt"/>  
			               	    <fo:table-column column-width="70pt"/>
			            		<fo:table-column column-width="120pt"/>
			            		<fo:table-column column-width="90pt"/> 		
			            		<fo:table-column column-width="90pt"/>
			            		<fo:table-column column-width="100pt"/>
			            		<fo:table-column column-width="90pt"/>                    										
                    			<fo:table-body>
                    			<#assign totGrTotRtAmt = (Static["java.math.BigDecimal"].ZERO)>  
                    			<#assign totCRandCanAmt = (Static["java.math.BigDecimal"].ZERO)>  
                    			<#assign totGrOthersAmt = (Static["java.math.BigDecimal"].ZERO)> 
                    			<#assign totGrHikesAmt = (Static["java.math.BigDecimal"].ZERO)>  
                    			<#assign totGrTotNetPayable = (Static["java.math.BigDecimal"].ZERO)>   
                    			<#assign sno=1>        			
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
                    												<#assign facilitySize = daywiseTrnsptEntry.getValue().get("distance")>
                    												<#assign facilityRate = daywiseTrnsptEntry.getValue().get("margin")>
                    												<#-- accessing fines And Penalities-->
                    												<#assign facRecvoryMap=facilityRecoveryInfoMap.get(trnsptMarginValues.getKey())?if_exists>
                    												<#assign totalDeduction=0>
                    												<#assign totalHikeAmnt=0>
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
													                     
													                   <#if facRecvoryMap?has_content>
													                   <#assign othersFine=facRecvoryMap.get("othersFine")?if_exists>
													                   </#if>
													                   <#if facRecvoryMap?has_content>
													                   <#assign totalHikeAmnt=facRecvoryMap.get("totalHikeAmount")?if_exists>
													                   </#if>
													                   
													                  
													                    <#assign totalCrAndCan=crateDeduction+canDeduction>
                    												   <#assign netPayable = grTotRtAmt.subtract(totalDeduction)>
                    												<#-- after Deduction again needs to Hike if Any -->
                    												 <#assign netPayable = netPayable.add(totalHikeAmnt)>
                    												 
                    												 <#assign totGrTotRtAmt = totGrTotRtAmt.add(grTotRtAmt)>
                    												<#assign totCRandCanAmt = totCRandCanAmt.add(totalCrAndCan)>
                    												 <#assign totGrOthersAmt=totGrOthersAmt.add(othersFine)>
                    												 <#assign totGrHikesAmt=totGrHikesAmt.add(totalHikeAmnt)>
                    												 
                    												<#assign totGrTotNetPayable = totGrTotNetPayable.add(netPayable)>
                    												 
                    											<fo:table-row border-style="solid">
                    												<fo:table-cell><fo:block>${sno?if_exists}</fo:block></fo:table-cell>
                    												<fo:table-cell><fo:block>${trnsptMarginValues.getKey()?if_exists}</fo:block></fo:table-cell>
                    												<fo:table-cell><fo:block text-align="right">${facilityRate?if_exists}</fo:block></fo:table-cell>
                    												<#if facilitySize?has_content && facilitySize!=0>
                    													<fo:table-cell><fo:block text-align="right">${facilitySize?if_exists}</fo:block></fo:table-cell>
                    												<#else>
                    													<fo:table-cell><fo:block text-align="right">0</fo:block></fo:table-cell>
                    												</#if>
                    												<fo:table-cell><fo:block text-align="right" font-size="10pt" keep-together="always">${grTotRtAmt.toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    												<fo:table-cell><fo:block text-align="right" font-size="10pt" keep-together="always">${totalCrAndCan?string("#0.00")?if_exists}</fo:block></fo:table-cell>
                    												<fo:table-cell><fo:block text-align="right" font-size="10pt" keep-together="always">${othersFine?string("#0.00")?if_exists}</fo:block></fo:table-cell>
                    												<fo:table-cell><fo:block text-align="right" font-size="10pt" keep-together="always">${totalHikeAmnt?string("#0.00")?if_exists}</fo:block></fo:table-cell>
                    												<#--><fo:table-cell><fo:block text-align="right">${grTotpendingDue.toEngineeringString()?if_exists}</fo:block></fo:table-cell>-->
                    												<fo:table-cell><fo:block text-align="right" font-size="10pt" keep-together="always">${netPayable?string("#0")?if_exists}.00</fo:block></fo:table-cell>
                    											</fo:table-row>
                    											<#assign sno=sno+1>
                    										</#if>	
                    									</#list>	
													</#list>
												</#list>
		 	    							</#list>
		 	    							<fo:table-row>
                    							<fo:table-cell><fo:block >-------------------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
                    						</fo:table-row> 
                    						<fo:table-row font-weight = "bold">
                    							<fo:table-cell><fo:block>TOTAL</fo:block></fo:table-cell>
                    							<fo:table-cell><fo:block text-align="right"></fo:block></fo:table-cell>
                    							<fo:table-cell><fo:block text-align="right"></fo:block></fo:table-cell>
                    							<fo:table-cell><fo:block text-align="right"></fo:block></fo:table-cell>
                    							<fo:table-cell><fo:block text-align="right" font-size="10pt" keep-together="always">${totGrTotRtAmt?if_exists?string("#0.00")}</fo:block></fo:table-cell>
                    							<fo:table-cell><fo:block text-align="right" font-size="10pt" keep-together="always">${totCRandCanAmt?if_exists?string("#0.00")}</fo:block></fo:table-cell>
                    							<fo:table-cell><fo:block text-align="right" font-size="10pt" keep-together="always">${totGrOthersAmt?if_exists?string("#0.00")}</fo:block></fo:table-cell>
                    							<fo:table-cell><fo:block text-align="right" font-size="10pt" keep-together="always">${totGrHikesAmt?if_exists?string("#0.00")}</fo:block></fo:table-cell>
                    							<fo:table-cell><fo:block text-align="right" font-size="10pt" keep-together="always">${totGrTotNetPayable?string("#0")?if_exists}.00</fo:block></fo:table-cell>
                    						</fo:table-row> 
                    						<fo:table-row>
                    							<fo:table-cell><fo:block >-------------------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
                    						</fo:table-row>                   							
                    					</fo:table-body>
                    				</fo:table>								
								</fo:block>
				<fo:block>
		           <fo:table width="100%" table-layout="fixed" space-after="0.0in">
		           <fo:table-column column-width="270pt"/>
		           <fo:table-column column-width="240pt"/>
		           <fo:table-column column-width="160pt"/>
		              <fo:table-body>
				        <fo:table-row>	
				            	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				            	</fo:table-cell>
				        </fo:table-row>
						<fo:table-row>	
				            	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				            	</fo:table-cell>
				        </fo:table-row>
				        <fo:table-row>	
				            	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				            	</fo:table-cell>
				        </fo:table-row>
				         <fo:table-row>	
				            	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				            	</fo:table-cell>
				        </fo:table-row>
			              <fo:table-row>
							  <fo:table-cell>
									<fo:block text-align="left" font-size="10pt" keep-together="always" white-space-collapse="false" font-weight="bold">PREPARED BY </fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="left" font-size="10pt" keep-together="always" white-space-collapse="false" font-weight="bold">PRE AUDIT</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="left" font-size="10pt" keep-together="always" white-space-collapse="false" font-weight="bold">Gen.Mgr(MKtg) </fo:block>
								</fo:table-cell>
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