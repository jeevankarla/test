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
	<fo:simple-page-master master-name="main" page-height="15in" page-width="12in"
            margin-top="0.3in" margin-bottom=".3in" margin-left=".1in" margin-right=".1in">
        <fo:region-body margin-top="1.75in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "DTCBankReport.pdf")}
 <#if finalMap?has_content> 
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
					<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" font-size="10pt" white-space-collapse="false">&#160;${uiLabelMap.CommonPage}- <fo:page-number/> </fo:block>
					<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false">    UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
					<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false">&#160;      Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      ${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      ${uiLabelMap.KMFDairySubHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;STATEMENT SHOWING THE PAYMENT TOWARDS TRANSPORTATION CHARGES</fo:block>
              		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;TO BE CREDITED TO DTC CONTRACTORS AS PER DETAILS BELOW</fo:block>
              		<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;${parameters.finAccountName?if_exists}</fo:block>
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
	                 	<fo:table border-style="solid">
	                    <fo:table-column column-width="30pt"/>
	                    <fo:table-column column-width="40pt"/>
	                    <fo:table-column column-width="55pt"/>  
	               	    <fo:table-column column-width="130pt"/>
	            		<fo:table-column column-width="110pt"/>
	            		<fo:table-column column-width="110pt"/> 		
	            		<fo:table-column column-width="90pt"/>
	            		<fo:table-column column-width="80pt"/>
	            		<fo:table-column column-width="90pt"/>
	            		<fo:table-column column-width="110pt"/>
	                    <fo:table-body>
	                    <fo:table-row >
	                    		<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold">S.No</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold">Route</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold">Code</fo:block>
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold">Name</fo:block> 
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold">Pan Num</fo:block> 
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false" font-weight="bold">Account No.</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false" font-weight="bold">Sale Amount</fo:block>  
                       			</fo:table-cell>
                        		<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold">Total Dedn.</fo:block>   
                        		</fo:table-cell>
                        		<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false" font-weight="bold">Net Amount</fo:block>  
                        		</fo:table-cell>
                        		<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false" font-weight="bold">Billing Period</fo:block>  
                        		</fo:table-cell>
                			</fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block> 
            	<fo:block>
                 	<fo:table border-style="solid">
                    <fo:table-column column-width="30pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="55pt"/>  
               	    <fo:table-column column-width="130pt"/>
            		<fo:table-column column-width="110pt"/>
            		<fo:table-column column-width="110pt"/> 		
            		<fo:table-column column-width="90pt"/>
            		<fo:table-column column-width="80pt"/>
            		<fo:table-column column-width="90pt"/>
            		<fo:table-column column-width="110pt"/>
                    <fo:table-body>
                    <#assign totalSale = 0>
                    <#assign grandTotalFine = 0>
                    <#assign totalNetAmount = 0>
                    <#assign routeDetails = finalMap.entrySet()>
                    <#assign sno=1>
                    	<#list routeDetails as eachRoute>
                    		<#assign routeId = eachRoute.getKey()?if_exists>
                    		<#assign saleAmount = eachRoute.getValue().get("routeAmount")?if_exists>
                    		<#assign totalFine = eachRoute.getValue().get("totalFine")?if_exists>
                    		<#assign netAmount = eachRoute.getValue().get("netAmount")?if_exists>
                    		<#assign facilityName = eachRoute.getValue().get("facilityName")?if_exists>
                    		<#assign facilityCode = eachRoute.getValue().get("facilityCode")?if_exists>
                    		<#assign facilityPan = eachRoute.getValue().get("facilityPan")?if_exists>
                    		<#assign facilityFinAccount = eachRoute.getValue().get("facilityFinAccount")?if_exists>
								<fo:table-row border-style="solid">
									<fo:table-cell border-style="solid">
	                            		<fo:block  text-align="left" font-size="9pt" white-space-collapse="false"> 
                                             ${sno?if_exists}
                                      </fo:block>  
	                       			</fo:table-cell>
									<fo:table-cell border-style="solid">
	                            		<fo:block  text-align="left" font-size="9pt" white-space-collapse="false"> 
                                             ${routeId?if_exists}
                                      </fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell border-style="solid">
                                    <fo:block text-align="left" font-weight="bold">
                                            ${facilityCode?if_exists}
                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid">
	                                    <fo:block text-align="left">
	                                            ${facilityName?if_exists}
	                                    </fo:block>
	                                </fo:table-cell>
	                                 <fo:table-cell border-style="solid">
	                                    <fo:block text-align="left">
	                                            ${facilityPan?if_exists}
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid">
	                                    <fo:block text-align="right">
	                                            ${facilityFinAccount?if_exists}
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid">
	                                    <fo:block text-align="right">
	                                    <#assign totalSale = totalSale+saleAmount>
	                                             <#if saleAmount?has_content>${(saleAmount)?string("##0.00")}<#else>0.00</#if>
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid">
	                                    <fo:block text-align="right">
	                                    <#assign grandTotalFine = grandTotalFine+totalFine>
	                                            <#if totalFine?has_content>${(totalFine)?string("##0.00")}<#else>0.00</#if>
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid">
	                                    <fo:block text-align="right">
	                                    <#assign totalNetAmount = totalNetAmount+netAmount>
	                                            <#if netAmount?has_content>${(netAmount)?string("##0.00")}<#else>0.00</#if>
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid">
	                                    <fo:block text-align="right" keep-together="always">
	                                            ${fromDateStr}${thruDateStr}
	                                    </fo:block>
	                                </fo:table-cell>
                              </fo:table-row>
                              <#assign sno=sno+1>
                          </#list>
                          <fo:table-row border-style="solid">
								<fo:table-cell>
                            		<fo:block  text-align="left" keep-together="always" font-size="10pt" font-weight="bold" white-space-collapse="false">Grand Total 
                                  </fo:block>  
                       			</fo:table-cell>
								<fo:table-cell>
                            		<fo:block  text-align="left" font-size="9pt" white-space-collapse="false"> 
                                  </fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                                <fo:block text-align="left" font-weight="bold">
                                </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right">
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-style="solid">
                                    <fo:block text-align="right">
                                             <#if totalSale?has_content>${(totalSale)?string("##0.00")}<#else>0.00</#if>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-style="solid">
                                    <fo:block text-align="right">
                                            <#if grandTotalFine?has_content>${(grandTotalFine)?string("##0.00")}<#else>0.00</#if>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-style="solid">
                                    <fo:block text-align="right">
                                            <#if totalNetAmount?has_content>${(totalNetAmount)?string("#0")}.00<#else>0.00</#if>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block text-align="right" keep-together="always">
                                    </fo:block>
                                </fo:table-cell>
                          </fo:table-row>
                          <fo:table-row>
                          		<#assign amountWords = Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(totalNetAmount?string("#0"), "%indRupees-and-paise", locale).toUpperCase()>
			                   <fo:table-cell>
			                        	<fo:block keep-together="always" font-size="12pt" font-weight="bold">Amount Payable:(${StringUtil.wrapString(amountWords?default(""))}  ONLY)</fo:block>
			                   </fo:table-cell>
			               </fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block>
          		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="14pt">&#160;DEDUCTION DETAILS</fo:block>
          		<fo:block>
                 	<fo:table>
                    <fo:table-column column-width="60pt"/>
	            	<fo:table-column column-width="60pt"/>
	            	<fo:table-column column-width="60pt"/>
	            	<fo:table-column column-width="80pt"/>  
	       	    	<fo:table-column column-width="100pt"/>
	       	    	<fo:table-column column-width="120pt"/>
	       	    	<fo:table-column column-width="140pt"/>
                    <fo:table-body>
                    	<fo:table-row >
                    		<fo:table-cell>
                        		<fo:block></fo:block>  
                   			</fo:table-cell>
                   			<fo:table-cell>
                        		<fo:block></fo:block>  
                   			</fo:table-cell>
                   			<fo:table-cell>
                        		<fo:block></fo:block>  
                   			</fo:table-cell>
                    		<fo:table-cell border-style="solid">
                        		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold">ROUTE</fo:block>  
                   			</fo:table-cell>
                    		<fo:table-cell border-style="solid">
                        		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold">CODE</fo:block>  
                   			</fo:table-cell>
                   			<fo:table-cell border-style="solid">
                        		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold">DESCRIPTION</fo:block>  
                   			</fo:table-cell>
                   			<fo:table-cell border-style="solid">
                        		<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold">AMOUNT</fo:block>
                   			</fo:table-cell>
            			</fo:table-row>
                </fo:table-body>
            </fo:table>
           </fo:block> 		
        	<fo:block>
             	<fo:table>
                <fo:table-column column-width="60pt"/>
            	<fo:table-column column-width="60pt"/>
            	<fo:table-column column-width="60pt"/>
            	<fo:table-column column-width="80pt"/>  
       	    	<fo:table-column column-width="100pt"/>
       	    	<fo:table-column column-width="120pt"/>
       	    	<fo:table-column column-width="140pt"/>
           	    <fo:table-body>
                <#assign grandTotalFine = 0>
                <#assign routeDetails = finalMap.entrySet()>
                	<#list routeDetails as eachRoute>
                		<#assign routeId = eachRoute.getKey()?if_exists>
                		<#assign totalFine = eachRoute.getValue().get("totalFine")?if_exists>
                		<#assign facilityCode = eachRoute.getValue().get("facilityCode")?if_exists>
							<fo:table-row>
								<fo:table-cell>
	                        		<fo:block></fo:block>  
	                   			</fo:table-cell>
	                   			<fo:table-cell>
	                        		<fo:block></fo:block>  
	                   			</fo:table-cell>
	                   			<fo:table-cell>
	                        		<fo:block></fo:block>  
	                   			</fo:table-cell>
								<fo:table-cell border-style="solid">
	                            		<fo:block  text-align="left" font-size="10pt" white-space-collapse="false"> 
                                             ${routeId?if_exists}
                                      </fo:block>  
	                       			</fo:table-cell>
								<fo:table-cell border-style="solid">
                            		<fo:block  text-align="left" font-size="10pt" white-space-collapse="false"> 
                                         ${facilityCode?if_exists}
                                  </fo:block>  
                       			</fo:table-cell>
								<fo:table-cell border-style="solid"> 
                            		<fo:block  text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always"> 
                                        Fines And Penalties 
                                  </fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                                <fo:block text-align="right" font-weight="bold" font-size="10pt">
                                       <#assign grandTotalFine = grandTotalFine+totalFine>
                                            <#if totalFine?has_content>${(totalFine)?string("##0.00")}<#else>0.00</#if>
                                </fo:block>
                                </fo:table-cell>
                          </fo:table-row>
                      </#list>
                      <fo:table-row>
                      		<fo:table-cell>
	                        		<fo:block></fo:block>  
                   			</fo:table-cell>
                   			<fo:table-cell>
                        		<fo:block></fo:block>  
                   			</fo:table-cell>
                   			<fo:table-cell>
                        		<fo:block></fo:block>  
                   			</fo:table-cell>
							<fo:table-cell border-style="solid">
                        		<fo:block  text-align="left" keep-together="always" font-size="10pt" font-weight="bold" white-space-collapse="false">Grand Total 
                              </fo:block>  
                   			</fo:table-cell>
                   			<fo:table-cell>
                        		<fo:block></fo:block>  
                   			</fo:table-cell>
							<fo:table-cell>
                        		<fo:block  text-align="left" font-size="10pt" white-space-collapse="false"> 
                              </fo:block>  
                   			</fo:table-cell>
                            <fo:table-cell border-style="solid">
                                <fo:block text-align="right" font-weight="bold" font-size="10pt">
                                        <#if grandTotalFine?has_content>${(grandTotalFine)?string("##0.00")}<#else>0.00</#if>
                                </fo:block>
                             </fo:table-cell>
                      </fo:table-row>
                      <fo:table-row border-style="solid">>
                      		<#assign fineWords = Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(grandTotalFine, "%indRupees-and-paise", locale).toUpperCase()>
		                   <fo:table-cell>
		                        	<fo:block keep-together="always" font-size="12pt" font-weight="bold">Amount Payable:(${StringUtil.wrapString(fineWords?default(""))}  ONLY)</fo:block>
		                   </fo:table-cell>
		              </fo:table-row>
		              </fo:table-body>
                	</fo:table>
               		</fo:block>
		              <fo:block>
	             	  <fo:table>
	       	    	  <fo:table-column column-width="100pt"/>
	       	    	  <fo:table-column column-width="120pt"/>
	       	    	  <fo:table-column column-width="140pt"/>
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
			            		<fo:block  keep-together="always" font-weight="bold">Prepared By</fo:block>  
			       			</fo:table-cell>
			       			<fo:table-cell>
			            		<fo:block  keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Pre-Audit</fo:block>  
			       			</fo:table-cell>
			       			<fo:table-cell>
			            		<fo:block  keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Manager(Finance)</fo:block>  
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
			            		<fo:block  keep-together="always"></fo:block>  
			       			</fo:table-cell>
			       			<fo:table-cell>
			            		<fo:block  keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Director</fo:block>  
			       			</fo:table-cell>
			       			<fo:table-cell>
			            		<fo:block  keep-together="always"></fo:block>  
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
	            	${uiLabelMap.NoOrdersFound}.
	       		 </fo:block>
		</fo:flow>
	</fo:page-sequence>	
    </#if>  
</fo:root>
</#escape>