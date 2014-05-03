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
	<fo:simple-page-master master-name="main" page-height="10in" page-width="12in"  margin-bottom=".3in" margin-left=".1in" margin-right=".1in">
        <fo:region-body margin-top="2in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "cashReceiptReport.pdf")}
 <#if paymentGrpMap?has_content> 
 <#assign paymentGroupList = paymentGrpMap.entrySet()>
 <#list paymentGroupList as paymentGroup>
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
					<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">&#160;      ${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">&#160;      ${uiLabelMap.KMFDairySubHeader}</fo:block>
                    <fo:block text-align="left"  keep-together="always"  white-space-collapse="false">Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(receiptDate, "MMMM dd,yyyy")}    &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>   </fo:block>
              		<fo:block>-------------------------------------------------------------------------</fo:block>
              		<fo:block >Received with thanks the Receipt of Cash													&#160;&#160;&#160;&#160;Receipt - CSH</fo:block>
            		<#assign  partyName="">
            			<#if paymentGrpPartyMap.get(paymentGroup.getKey())?exists>
            			<#assign partyId=paymentGrpPartyMap.get(paymentGroup.getKey())>
            			<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)>
            			</#if>
            		<fo:block >From:${partyName?if_exists}																	&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Receipt Number:${paymentGroupId?if_exists}</fo:block>
            		<fo:block >By RT NO:${paymentGrpFacMap.get(paymentGroup.getKey())?if_exists}																&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Receipt Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(receiptDate, "MMMM dd,yyyy")}</fo:block>
            		<fo:block>-------------------------------------------------------------------------</fo:block>
            		<fo:block >Receipt Id													&#160;&#160;&#160;&#160;&#160;&#160;&#160;Description 								&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Retailer Code			&#160;&#160;&#160;&#160;&#160;&#160;&#160;Amount</fo:block>
            		<fo:block>-------------------------------------------------------------------------</fo:block>
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
            	<fo:block>
                 	<fo:table>
                    <fo:table-column column-width="130pt"/>
                    <fo:table-column column-width="135pt"/>
                    <fo:table-column column-width="90pt"/>
                    <fo:table-column column-width="140pt"/> 
            		<fo:table-column column-width="300pt"/>
                    <fo:table-body>
                    		<#assign totalAmount = 0>
                    		<#assign paymentList = paymentGroup.getValue().entrySet()>
                    		 <#list paymentList as payment>
                    		 <#assign facility = delegator.findOne("Facility", {"facilityId" : payment.getValue().get("partyIdFrom")}, true)?if_exists/>
							<fo:table-row>
                				<fo:table-cell>
                            		<fo:block  text-align="left"  white-space-collapse="false">${payment.getValue().get("paymentId")?if_exists}</fo:block>  
                       			</fo:table-cell>
                				<fo:table-cell>
                            		<fo:block  keep-together="always" text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.description?if_exists)),22)}</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                            		<fo:block  text-align="right"  white-space-collapse="false">${payment.getValue().get("partyIdFrom")?if_exists}</fo:block>  
                       			</fo:table-cell>
                       			<#assign amount = payment.getValue().get("amount")?if_exists>
                       			<#assign totalAmount = totalAmount+amount>
                       			<fo:table-cell>
                            		<fo:block  text-align="right"  white-space-collapse="false">${payment.getValue().get("amount")?if_exists?string("#0.00")}</fo:block>  
                       			</fo:table-cell>
            				</fo:table-row>
            				</#list>
            				<fo:table-row>
               					<fo:table-cell>
                    				<fo:block>-------------------------------------------------------------------------</fo:block>
               					</fo:table-cell>
		  					</fo:table-row>
            				<fo:table-row>
                				<fo:table-cell>
                            		<fo:block  keep-together="always" text-align="left"  white-space-collapse="false">Total Amount</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                            		<fo:block  text-align="right"  white-space-collapse="false"></fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                            		<fo:block  text-align="right"  white-space-collapse="false"></fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                            		<fo:block  text-align="right"  white-space-collapse="false">${totalAmount?if_exists?string("#0.00")}</fo:block>  
                       			</fo:table-cell>
            				</fo:table-row>
                			<fo:table-row>
               					<fo:table-cell>
                    				<fo:block>-------------------------------------------------------------------------</fo:block>
               					</fo:table-cell>
		  					</fo:table-row>
						  	<fo:table-row>
			                   	<fo:table-cell>
			                        <#assign amountWords = Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(totalAmount, "%dollars-and-hundredths", locale)>
                   					<fo:block white-space-collapse="false" keep-together="always">(In Words:Rupees ${amountWords?if_exists.substring(0,(amountWords.length()-10)).toUpperCase()}  only)</fo:block>
			                   	</fo:table-cell>
						  	</fo:table-row>
						  	<fo:table-row>
               					<fo:table-cell>
                    				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
               					</fo:table-cell>
		  					</fo:table-row>
						  	<fo:table-row>
			                   <fo:table-cell>
			                        	<fo:block keep-together="always">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;For Mother Dairy</fo:block>
			                   </fo:table-cell>
						  	</fo:table-row>
						  	<fo:table-row>
               					<fo:table-cell>
                    				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
               					</fo:table-cell>
		  					</fo:table-row>
						  	<fo:table-row>
			                   <fo:table-cell>
			                        	<fo:block keep-together="always">SUPDT/DMF/AM                     &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Cashier</fo:block>
			                   </fo:table-cell>
						  	</fo:table-row>
		              </fo:table-body>
		                </fo:table>
		               </fo:block> 		
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