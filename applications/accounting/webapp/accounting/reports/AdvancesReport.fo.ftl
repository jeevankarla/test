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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="13in"
            margin-top="0.2in" margin-bottom=".3in" margin-left=".7in" margin-right=".5in">
        <fo:region-body margin-top="1.6in"/>
        <fo:region-before extent="1.5in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "abstractReport.pdf")}
 <#if partyPaymentsMap?has_content> 
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION  LTD</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">UNIT: MOTHER DAIRY: G.K.V.K POST,YELAHANKA,BANGALORE:560065</fo:block>
                    <fo:block text-align="center" font-size="12pt" keep-together="always"  white-space-collapse="false" font-weight="bold">&#160;&#160; STATEMENT FOR ${GlAccount.get("accountCode")} - ${GlAccount.get("accountName")}</fo:block>
              		<fo:block text-align="center" font-size="12pt" keep-together="always"  white-space-collapse="false" font-weight="bold">&#160;&#160; ${paymentType.get("description")} Abstract From ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} To ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}	</fo:block>
              		<fo:block>
	                 	<fo:table border-style="solid">
	                    <fo:table-column column-width="80pt"/>
	                    <fo:table-column column-width="200pt"/>
	                    <fo:table-column column-width="90pt"/>  
	               	    <fo:table-column column-width="90pt"/>
	            		<fo:table-column column-width="90pt"/> 		
	            		<fo:table-column column-width="90pt"/>
	            		<fo:table-column column-width="90pt"/>
	            		<fo:table-column column-width="90pt"/>
	                    <fo:table-body>
	                    <fo:table-row >
	                    		<fo:table-cell >
                            		<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false" font-weight="bold">&#160;</fo:block>
                            		<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false" font-weight="bold" border-style="solid">Party Code</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell >
                            		<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false" font-weight="bold">&#160;</fo:block>
                            		<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false" font-weight="bold" border-style="solid">Party Name</fo:block>   
                       			</fo:table-cell>
                       			<fo:table-cell >
                       				<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">Opening</fo:block> 
                            		<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false" font-weight="bold" border-style="solid">Debit </fo:block>
                       			</fo:table-cell>
                       			<fo:table-cell >
                       				<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false" font-weight="bold">&#160;Balance</fo:block> 
                            		<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false" font-weight="bold" border-style="solid">Credit</fo:block> 
                       			</fo:table-cell>
                       			<fo:table-cell >
                       				<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">During the</fo:block> 
                            		<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false" font-weight="bold" border-style="solid">Debit</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                       				<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false" font-weight="bold">&#160;Period</fo:block> 
                            		<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false" font-weight="bold" border-style="solid">Credit</fo:block>  
                       			</fo:table-cell>
                        		<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">Closing</fo:block> 
                            		<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false" font-weight="bold" border-style="solid">Debit</fo:block>   
                        		</fo:table-cell>
                        		<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false" font-weight="bold">&#160;Balance</fo:block> 
                            		<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false" font-weight="bold" border-style="solid">Credit</fo:block>  
                        		</fo:table-cell>
                			</fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block> 		
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
                 	<fo:table border-style="solid">
                    <fo:table-column column-width="80pt"/>
                    <fo:table-column column-width="200pt"/>
                    <fo:table-column column-width="90pt"/>  
               	    <fo:table-column column-width="90pt"/>
            		<fo:table-column column-width="90pt"/> 		
            		<fo:table-column column-width="90pt"/>
            		<fo:table-column column-width="90pt"/>
            		<fo:table-column column-width="90pt"/>
                    <fo:table-body>
                    	<#assign totalOBCredit = 0>
                    	<#assign totalOBDebit = 0>
                    	<#assign totalduCredit = 0>
                    	<#assign totalduDebit = 0>
                    	<#assign totalCBCredit = 0>
                    	<#assign totalCBDebit = 0>
	                	<#assign partyAdvanceDetails = partyPaymentsMap.entrySet()>	
	                	<#list partyAdvanceDetails as partyPayments>
							<#assign partyId = partyPayments.getKey()>
							<#assign paymentDetails = partyPayments.getValue()>
								
								<#assign totalOBDebit = totalOBDebit + (paymentDetails.get("openingBalance").get("debit"))>
        						<#assign totalOBCredit = totalOBCredit + (paymentDetails.get("openingBalance").get("credit"))>
        						 
        						<#assign totalduDebit = totalduDebit + (paymentDetails.get("duringPeriod").get("debit"))>
        						<#assign totalduCredit = totalduCredit + (paymentDetails.get("duringPeriod").get("credit"))>
        						 
        						<#assign totalCBDebit = totalCBDebit + (paymentDetails.get("closingBalance").get("debit"))>
        						<#assign totalCBCredit = totalCBCredit + (paymentDetails.get("closingBalance").get("credit"))>
								<fo:table-row border-style="solid">
									<fo:table-cell border-style="solid">
	                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                             ${partyId?if_exists}
                                      </fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell border-style="solid">
	                            		<fo:block  text-align="left" wrap-option="wrap" font-size="13pt" white-space-collapse="false"> 
                                             ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)}
                                      	</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell border-style="solid">
	                            		<fo:block  text-align="right" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                             ${paymentDetails.get("openingBalance").get("debit")?if_exists?string("#0.00")}
                                      </fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell border-style="solid">
	                            		<fo:block  text-align="right" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                             ${paymentDetails.get("openingBalance").get("credit")?if_exists?string("#0.00")}
                                      </fo:block>  
	                       			</fo:table-cell><fo:table-cell border-style="solid">
	                            		<fo:block  text-align="right" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                             ${paymentDetails.get("duringPeriod").get("debit")?if_exists?string("#0.00")}
                                      </fo:block>  
	                       			</fo:table-cell><fo:table-cell border-style="solid">
	                            		<fo:block  text-align="right" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                             ${paymentDetails.get("duringPeriod").get("credit")?if_exists?string("#0.00")}
                                      </fo:block>  
	                       			</fo:table-cell><fo:table-cell border-style="solid">
	                            		<fo:block  text-align="right" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                             ${paymentDetails.get("closingBalance").get("debit")?if_exists?string("#0.00")}
                                      </fo:block>  
	                       			</fo:table-cell><fo:table-cell border-style="solid">
	                            		<fo:block  text-align="right" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                             ${paymentDetails.get("closingBalance").get("credit")?if_exists?string("#0.00")}
                                      </fo:block>  
	                       			</fo:table-cell>
								</fo:table-row>
						</#list>	
						<fo:table-row border-style="solid">
								<fo:table-cell border-style="solid">
                            		<fo:block font-weight="bold" text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                         TOTALS
                                  </fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  text-align="left" wrap-option="wrap" font-size="13pt" white-space-collapse="false"> 
                                         
                                  	</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block font-weight="bold" text-align="right" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                         ${totalOBDebit?if_exists?string("#0.00")}
                                  </fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block font-weight="bold" text-align="right" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                         ${totalOBCredit?if_exists?string("#0.00")}
                                  </fo:block>  
                       			</fo:table-cell><fo:table-cell border-style="solid">
                            		<fo:block font-weight="bold" text-align="right" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                         ${totalduDebit?if_exists?string("#0.00")}
                                  </fo:block>  
                       			</fo:table-cell><fo:table-cell border-style="solid">
                            		<fo:block font-weight="bold" text-align="right" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                         ${totalduDebit?if_exists?string("#0.00")}
                                  </fo:block>  
                       			</fo:table-cell><fo:table-cell border-style="solid">
                            		<fo:block font-weight="bold" text-align="right" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                         ${totalCBDebit?if_exists?string("#0.00")}
                                  </fo:block>  
                       			</fo:table-cell><fo:table-cell border-style="solid">
                            		<fo:block  font-weight="bold" text-align="right" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                         ${totalCBDebit?if_exists?string("#0.00")}
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
		<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			<fo:block font-size="14pt">
	            	No Records Found For The Given Duration

   		 </fo:block>
		</fo:flow>
	</fo:page-sequence>	
    </#if>  
</fo:root>
</#escape>