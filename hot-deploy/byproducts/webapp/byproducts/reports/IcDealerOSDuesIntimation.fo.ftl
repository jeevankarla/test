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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-right=".3in" margin-top=".5in">
                <fo:region-body margin-top="1.7in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "PartywiseLedgerAbstract.pdf")}
        <#if errorMessage?has_content>
	<fo:page-sequence master-reference="main">
	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	   <fo:block font-size="14pt">
	           ${errorMessage}.
        </fo:block>
	</fo:flow>
	</fo:page-sequence>	
	<#else>
       <#if partyWiseLedger?has_content>
       <#assign partyLedger = partyWiseLedger.entrySet()>
       <#list partyLedger as eachPartyLedger>
       <#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, eachPartyLedger.getKey()?if_exists, false)>
		<#assign totalDebit=0>	
	    <#assign totalCredit=0>
		        <fo:page-sequence master-reference="main" font-size="10pt">	
		        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
		        		<fo:block text-align="center" font-size="13pt" keep-together="always"  white-space-collapse="false" font-weight="bold">&#160;${uiLabelMap.KMFDairyHeader}</fo:block>
                    	<fo:block text-align="center" font-size="12pt" keep-together="always"  white-space-collapse="false" font-weight="bold">&#160;${uiLabelMap.KMFDairySubHeader}</fo:block>
                    	<fo:block text-align="center" font-size="12pt" keep-together="always"  white-space-collapse="false" font-weight="bold">&#160;SUBLEDGER MONTHWISE SUMMARY FOR THE PERIOD: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")} </fo:block>
              			<fo:block text-align="left" font-size="12pt" keep-together="always" white-space-collapse="false">UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
              			<fo:block font-size="12pt" text-align="left">======================================================================================</fo:block>  
            			<fo:block text-align="left" font-size="12pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160; PARTY CODE: ${eachPartyLedger.getKey()?if_exists}                                PARTY NAME:  ${partyName?if_exists}                  </fo:block>
              			<fo:block font-size="12pt" text-align="left">--------------------------------------------------------------------------------------</fo:block>  
            			<fo:block text-align="left" font-size="12pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">&#160;DATE            OPENING                Tr.Debit       Tr.Credit      Closing Balance</fo:block>
		        		<fo:block font-size="12pt" text-align="left">--------------------------------------------------------------------------------------</fo:block>  
		        	</fo:static-content>	        	
		        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
                 	<fo:table>
                    <fo:table-column column-width="70pt"/> 
                    <fo:table-column column-width="100pt"/>
            		<fo:table-column column-width="170pt"/> 	
            		<fo:table-column column-width="120pt"/>	
            		<fo:table-column column-width="150pt"/>
            		<fo:table-column column-width="140pt"/>
                    <fo:table-body>
                    <#assign ledgerDetails = eachPartyLedger.getValue()>
                    	<#list ledgerDetails as eachDateDetail>
                         <#assign ledgerDetails = eachDateDetail.entrySet()>
                         <#list ledgerDetails as eachPartyDetail>
	                      <fo:table-row>
	                    	<#assign ob = 0>
				            <#assign saleAmt = 0>
				            <#assign totalClosingBal=0>	
	                    	<#if eachPartyDetail.getValue().get("openingBalance")?has_content>
	                    		<#assign ob = eachPartyDetail.getValue().get("openingBalance")>
	                    	</#if>
	                    	<#if eachPartyDetail.getValue().get("debitAmount")?has_content>
	                    		<#assign debitAmount = eachPartyDetail.getValue().get("debitAmount")>
	                    	</#if>
	                    	<#if eachPartyDetail.getValue().get("creditAmount")?has_content>
	                    		<#assign creditAmount = eachPartyDetail.getValue().get("creditAmount")>
	                    	</#if>
	                    	<#if eachPartyDetail.getValue().get("closingBalance")?has_content>
	                    		<#assign closingBalance = eachPartyDetail.getValue().get("closingBalance")>
	                    	</#if>
	                    	<#assign totalDebit=totalDebit+debitAmount>	
	                    	<#assign totalCredit=totalCredit+creditAmount>	
	                    	<#assign totalClosingBal=totalClosingBal+closingBalance>	
	                    	<fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(eachPartyDetail.getKey(), "MMM-yy")}</fo:block>  
					            </fo:table-cell>
                        	<fo:table-cell>
                            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${ob?if_exists?string("#0.00")}</fo:block>  
                        	</fo:table-cell>
                        	<fo:table-cell>
		                    	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false"><#if debitAmount !=0>${debitAmount?if_exists?string("#0.00")}</#if></fo:block>  
		                    </fo:table-cell>
		                    <fo:table-cell>
		                    	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false"><#if creditAmount !=0>${creditAmount?if_exists?string("#0.00")}</#if></fo:block>  
		                    </fo:table-cell>
		                    <fo:table-cell>
		                    	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${closingBalance?if_exists?string("#0.00")}</fo:block>  
		                    </fo:table-cell>
                        </fo:table-row>
                        </#list>
                        </#list>
                         <fo:table-row>
                            <fo:table-cell>
                        	  <fo:block font-size="12pt" text-align="left">--------------------------------------------------------------------------------------</fo:block>  
                           </fo:table-cell>
                         </fo:table-row>
                        <fo:table-row>
	                    	<fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">Total</fo:block>  
					            </fo:table-cell>
                        	<fo:table-cell>
                            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false"></fo:block>  
                        	</fo:table-cell>
                        	<fo:table-cell>
		                    	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false"><#if totalDebit !=0>${totalDebit?if_exists?string("#0.00")}</#if></fo:block>  
		                    </fo:table-cell>
		                    <fo:table-cell>
		                    	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false"><#if totalCredit !=0>${totalCredit?if_exists?string("#0.00")}</#if></fo:block>  
		                    </fo:table-cell>
		                    <fo:table-cell>
		                    	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false"></fo:block>  
		                    </fo:table-cell>
                        </fo:table-row>
                         <fo:table-row>
                            <fo:table-cell>
                        	  <fo:block font-size="12pt" text-align="left">--------------------------------------------------------------------------------------</fo:block>  
                           </fo:table-cell>
                         </fo:table-row>
                    </fo:table-body>
                </fo:table>
              </fo:block> 
               
              <fo:block>
                 	<fo:table>
                    <fo:table-column column-width="70pt"/> 
                    <fo:table-column column-width="100pt"/>
            		<fo:table-column column-width="170pt"/> 	
            		<fo:table-column column-width="120pt"/>	
            		<fo:table-column column-width="150pt"/>
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
	                       <fo:block  text-align="left" keep-together="always" font-size="12pt" white-space-collapse="false">TO</fo:block>
	                    </fo:table-cell>
                    </fo:table-row>   
	                   <#assign partyAddressResult = dispatcher.runSync("getPartyPostalAddress", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", eachPartyLedger.getKey(), "userLogin", userLogin))/>
                        <fo:table-row>
                               <fo:table-cell>
                                        <#if (partyName?has_content)>
							   				<fo:block text-align="left" wrap-option="wrap" keep-together="always" font-size="12pt" white-space-collapse="false">&#160;${partyName?if_exists}</fo:block>
										</#if>
	            	                    <#if (partyAddressResult.address1?has_content)>
							   				<fo:block text-align="left" wrap-option="wrap" keep-together="always" font-size="12pt" white-space-collapse="false">&#160;${partyAddressResult.address1?if_exists}</fo:block>
										</#if>
										<#if (partyAddressResult.address2?has_content)>
											<fo:block  text-align="left" wrap-option="wrap" keep-together="always" font-size="12pt" white-space-collapse="false">&#160;${partyAddressResult.address2?if_exists}</fo:block>
										</#if>
										<#if (partyAddressResult.city?has_content)>
											<fo:block  text-align="left" keep-together="always" font-size="12pt" white-space-collapse="false">&#160;${partyAddressResult.city?if_exists} ${partyAddressResult.stateProvinceGeoId?if_exists}</fo:block>
										</#if>
										<#if (partyAddressResult.countryGeoId?has_content)>
											<fo:block  text-align="left" keep-together="always" font-size="12pt" white-space-collapse="false">&#160;${partyAddressResult.countryGeoId?if_exists}</fo:block>
										</#if>
										<#if (partyAddressResult.postalCode?has_content)>
										  	<fo:block  text-align="left" keep-together="always" font-size="12pt" white-space-collapse="false">&#160;${partyAddressResult.postalCode?if_exists}</fo:block>
										</#if> 
					            </fo:table-cell>
					    </fo:table-row>	
					     <fo:table-row>
		                    <fo:table-cell>
		                         <fo:block  text-align="left" keep-together="always" font-size="12pt" white-space-collapse="false">&#160;</fo:block>
		                    </fo:table-cell>
                       </fo:table-row>   
					    <fo:table-row>
		                    <fo:table-cell>
		                        <fo:block  text-indent="250pt" keep-together="always" font-size="12pt" white-space-collapse="false" font-weight="bold"><fo:inline text-decoration="underline">Reg.Post Ack.due</fo:inline></fo:block>
		                    </fo:table-cell>
                    </fo:table-row>  
                    <fo:table-row>
	                    <fo:table-cell>
	                       <fo:block  text-align="left" keep-together="always" font-size="12pt" white-space-collapse="false">Sir,</fo:block>
	                    </fo:table-cell>
                    </fo:table-row>  
                    <fo:table-row>
		                    <fo:table-cell>
		                        <fo:block  text-align="left" keep-together="always" font-size="12pt" white-space-collapse="false">&#160;</fo:block>
		                    </fo:table-cell>
                       </fo:table-row>   
                        <fo:table-row>
		                    <fo:table-cell>
		                         <fo:block  text-align="left" keep-together="always" font-size="12pt" white-space-collapse="false">&#160;</fo:block>
		                
		                    </fo:table-cell>
                       </fo:table-row>   
                    <fo:table-row>
                               <fo:table-cell>
										  	<fo:block  text-indent="21pt" text-align="left" keep-together="always" font-size="12pt" white-space-collapse="false">Sub:- Confirmation of balance as on ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")}</fo:block>
					            </fo:table-cell>
					    </fo:table-row>	 
					    <fo:table-row>
		                    <fo:table-cell>
		                         <fo:block  text-align="left" keep-together="always" font-size="12pt" white-space-collapse="false">&#160;</fo:block>
		                
		                    </fo:table-cell>
                       </fo:table-row>   
                        <fo:table-row>
		                    <fo:table-cell>
		                        <fo:block  text-align="left" keep-together="always" font-size="12pt" white-space-collapse="false">&#160;</fo:block>
		                
		                    </fo:table-cell>
                       </fo:table-row>   
                       <fo:table-row>
		                    <fo:table-cell>
		                            <fo:block  text-align="left" keep-together="always" font-size="12pt" white-space-collapse="false">&#160;</fo:block>
		                    </fo:table-cell>
                       </fo:table-row>   
                        <fo:table-row>
		                    <fo:table-cell>
		                      <fo:block  text-align="left" keep-together="always" font-size="12pt" white-space-collapse="false">&#160;</fo:block>
		                    </fo:table-cell>
                       </fo:table-row>    
					    <fo:table-row>
                               <fo:table-cell>
										  	<fo:block  text-indent="21pt" text-align="left" keep-together="always" font-size="12pt" white-space-collapse="false">With reference to above, your account in our books shows a <#if (totalClosingBal > 0) >Debit</#if><#if (totalClosingBal < 0)>Credit</#if> balance of <#if (totalClosingBal > 0) >${totalClosingBal}</#if><#if (totalClosingBal < 0) >${totalClosingBal*-1}</#if> </fo:block>
										  	<fo:block  text-indent="21pt" text-align="left" keep-together="always" font-size="12pt" white-space-collapse="false">rupees as on ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")}.Rupees in words:</fo:block>
										  	<fo:block  text-indent="21pt" text-align="left" keep-together="always" font-size="12pt" white-space-collapse="false" font-weight="bold">${Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble(totalClosingBal?string("#0.00")), "%rupees-and-paise", locale).toUpperCase()} ONLY  </fo:block>
										  	<fo:block  text-indent="21pt" text-align="left" keep-together="always" font-size="12pt" white-space-collapse="false">We request to confirm the balance immediatly by return post.</fo:block>
										  	<fo:block  text-indent="21pt" text-align="left" keep-together="always" font-size="12pt" white-space-collapse="false">If no confirmation is Recieved,we presume that the balance shown by us as correct.</fo:block>
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
										  	<fo:block  text-indent="21pt" text-align="left" keep-together="always" font-size="12pt" white-space-collapse="false">Thanking you,</fo:block>
					         
					            </fo:table-cell>
					              <fo:table-cell>
										  	<fo:block  text-indent="320pt" text-align="left" keep-together="always" font-size="12pt" white-space-collapse="false">Yours faithfully,</fo:block>
										  	<fo:block  text-indent="320pt" text-align="left" keep-together="always" font-size="12pt" white-space-collapse="false">For MOTHER DAIRY,</fo:block>
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
										  	<fo:block  text-indent="21pt" text-align="left" keep-together="always" font-size="12pt" white-space-collapse="false">&#160;</fo:block>
					         
					            </fo:table-cell>
					              <fo:table-cell>
										  	<fo:block  text-indent="320pt" text-align="left" keep-together="always" font-size="12pt" white-space-collapse="false">&#160;</fo:block>
										  	<fo:block  text-indent="320pt" text-align="left" keep-together="always" font-size="12pt" white-space-collapse="false">Authorised Signature.</fo:block>
					            </fo:table-cell>
					    </fo:table-row>	
					    <fo:table-row>
		                    <fo:table-cell>
		                       <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		                    </fo:table-cell>
                        </fo:table-row>     	
					    <fo:table-row>
                               <fo:table-cell>
										  	<fo:block  text-indent="21pt" text-align="left" keep-together="always" font-size="12pt" white-space-collapse="false" font-weight="bold">The above balance is confirmed.</fo:block>
					         
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
		                       <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		                    </fo:table-cell>
                        </fo:table-row>     	 	
					    <fo:table-row>
                               <fo:table-cell>
									<fo:block  text-indent="21pt" text-align="left" keep-together="always" font-size="12pt" white-space-collapse="false" font-weight="bold">Customer Signature</fo:block>
					                <fo:block  text-indent="21pt" text-align="left" keep-together="always" font-size="12pt" white-space-collapse="false" font-weight="bold">Customer Name With Seal</fo:block>
					            </fo:table-cell>
					             
					    </fo:table-row>	 	  	 
					    </fo:table-body>
                </fo:table>
              </fo:block> 
			</fo:flow>
		</fo:page-sequence>
		</#list>>
	<#else>
	<fo:page-sequence master-reference="main">
    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
       		 <fo:block font-size="14pt">
            	${uiLabelMap.NoOrdersFound}.
       		 </fo:block>
    	</fo:flow>
	</fo:page-sequence>	
  </#if>   
 </#if>
 </fo:root>
</#escape>