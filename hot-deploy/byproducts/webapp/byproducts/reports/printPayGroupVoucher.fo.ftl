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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-right="1.1in" margin-top=".3in">
                <fo:region-body margin-top="0.9in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
         ${setRequestAttribute("OUTPUT_FILENAME", "paymentVoucher.pdf")}
        <#if partyMap?has_content>      
		        <fo:page-sequence master-reference="main" font-size="10pt">	
		        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
		        		<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" font-size="10pt" white-space-collapse="false">&#160;${uiLabelMap.CommonPage}- <fo:page-number/> </fo:block>
		        	<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
                    <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
        			<fo:block  keep-together="always" text-align="center" font-size="12pt" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${reportHeader.description?if_exists}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-size="12pt" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${reportSubHeader.description?if_exists}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-size="12pt" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold"> PAYMENT VOUCHER</fo:block>
            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        	</fo:static-content>	        	
		        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">
		        	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
            		<fo:block>
                 	<fo:table>
                    <fo:table-column column-width="50%"/>
        			<fo:table-column column-width="35%"/>
        			<fo:table-column column-width="35%"/>
        			<fo:table-column column-width="35%"/>
        			<fo:table-column column-width="35%"/>
        			<fo:table-column column-width="35%"/>
        			<fo:table-column column-width="35%"/>
        			<fo:table-column column-width="35%"/>
	                    <fo:table-body>
	        					<#assign finalMapList = partyMap.entrySet()>
        						<#list finalMapList as eachh>
        						
        						<#assign paymentGroupDetails = {}>
        						 	<#if paymentGroupId?has_content>
								    	<#assign paymentGroupDetails = delegator.findOne("PaymentGroup", {"paymentGroupId" : paymentGroupId}, true)?if_exists/>
								   </#if>
								   <#assign paymentGroupTypeDetails = {}>
								   <#if paymentGroupDetails.paymentGroupTypeId?has_content>
								    	<#assign paymentGroupTypeDetails = delegator.findOne("PaymentGroupType", {"paymentGroupTypeId" : paymentGroupDetails.paymentGroupTypeId}, true)?if_exists/>
								   </#if>
							   <fo:table-row> 
							   <fo:table-cell>
        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
        						 	</fo:table-cell>
        						 	<fo:table-cell>
        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
        						 	</fo:table-cell>
        					   </fo:table-row> 
							  <fo:table-row> 
        						 	<fo:table-cell>
        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;<#if paymentGroupTypeDetails.description?has_content></#if></fo:block>
        						 	</fo:table-cell>
        						 	<fo:table-cell>
        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
        						 	</fo:table-cell>
        						 	<fo:table-cell>
        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
        						 	</fo:table-cell>
		        				</fo:table-row>
		        				<fo:table-row> 
        						 	<fo:table-cell>
        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
        						 	</fo:table-cell>
        						 	<fo:table-cell>
        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"><#if paymentGroupDetails.paymentMethodTypeId?has_content && (paymentGroupDetails.paymentMethodTypeId == "CHEQUE_PAYIN" || paymentGroupDetails.paymentMethodTypeId == "CHEQUE_PAYOUT")></#if><#if paymentGroupDetails.paymentMethodTypeId?has_content && (paymentGroupDetails.paymentMethodTypeId == "CASH_PAYIN" || paymentGroupDetails.paymentMethodTypeId == "CASH_PAYOUT")>CASH</#if></fo:block>
        						 	</fo:table-cell>
        						 	<fo:table-cell>
        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">PAYMENT DATE:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(paymentGroupDetails.paymentDate?if_exists, "dd-MM-yyyy")}</fo:block>
        						 	</fo:table-cell>
		        				</fo:table-row>
        						 <#if paymentGroupDetails.paymentMethodTypeId?has_content && (paymentGroupDetails.paymentMethodTypeId == "CHEQUE_PAYIN" || paymentGroupDetails.paymentMethodTypeId == "CHEQUE_PAYOUT")> 
									 <#if (paymentGroupDetails.paymentMethodTypeId == "CHEQUE_PAYIN" || paymentGroupDetails.paymentMethodTypeId == "CHEQUE_PAYOUT")>   
	        						 <fo:table-row> 
	        						 	<fo:table-cell>
	        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;CHEQUE NO:${paymentGroupDetails.paymentRefNum?if_exists}</fo:block>
	        						 	</fo:table-cell>
	        						 	<fo:table-cell>
	        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
	        						 	</fo:table-cell>
	        						 	<fo:table-cell>
	        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">CHEQUE DATE:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(paymentGroupDetails.instrumentDate?if_exists, "dd-MM-yyyy")}</fo:block>
	        						 	</fo:table-cell>
	        						 </fo:table-row>
	        						 <#if (paymentGroupDetails.paymentMethodTypeId == "CHEQUE_PAYIN" || paymentGroupDetails.paymentMethodTypeId == "CHEQUE_PAYOUT")>
	        						 <#assign facilityDetails = delegator.findOne("FinAccount", {"finAccountId" : paymentGroupDetails.finAccountId}, true)>
	        						 <fo:table-row> 
	        						 	<fo:table-cell>
	        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"><#if facilityDetails?has_content>&#160;CHEQUE BANK DETAILS:${facilityDetails.finAccountName?if_exists}</#if></fo:block>
	        						 	</fo:table-cell>
	        						 	<fo:table-cell>
	        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
	        						 	</fo:table-cell>
	        						 	<fo:table-cell>
	        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
	        						 	</fo:table-cell>
	        						 </fo:table-row>
	        						 </#if>
	        					 </#if>
		        				</#if>
		        				
        						<#assign  partyAddress="">
        						<#assign partyId =eachh.getKey()>
        						<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)>
        						
        						  <#assign partyaddr=delegator.findByAnd("PartyAndPostalAddress", {"partyId" : partyId})/>
        						
        						<fo:table-row>         		  
        						<fo:table-cell>
        						<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
		        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">&#160;Party Name:  ${partyName?if_exists}</fo:block>
        										<fo:block text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">&#160;Address: ${partyaddr[0].address1?if_exists} </fo:block>
        										<fo:block text-align="left" font-size="13pt" white-space-collapse="false" font-weight="bold">&#160;Dear sir, </fo:block>
        										<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;WE ARE ENCLOSING HERE WITH OUR DEMAND DRAFT/CHEQUE/RTGS TOWARDS THE PAYMENT OF FOLLOWING BILLS:-</fo:block>
		        				</fo:table-cell>
		        				</fo:table-row> 
		        					<fo:table-row>
		        						<fo:table-cell>
		        		                    <fo:table  table-layout="fixed" width="65%" space-before="0.2in">
				    								 <fo:table-column column-width="10%"/>
				   									 <fo:table-column column-width="40%"/>
				   									 <fo:table-column column-width="25%"/>
				   									 <fo:table-column column-width="25%"/>
				   									 <fo:table-column column-width="30%"/>
				   									  <fo:table-column column-width="23%"/>
				   									 <fo:table-column column-width="30%"/>	
				   									 <fo:table-body>
				   									 <fo:table-row>
				   									 <fo:table-cell border-style="solid">
		        											<fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
		        											<fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;S.No</fo:block>
		        							  		  </fo:table-cell>
		        							  		  <fo:table-cell border-style="solid">
		        											<fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
		        											<fo:block text-align="center" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; DESCRIPTION </fo:block>
		        											<fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
		        							  		  </fo:table-cell>
		        							  		  <fo:table-cell border-style="solid">
		        											<fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
		        											<fo:block text-align="center" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; INVOICE ID </fo:block>
		        											<fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
		        							  		  </fo:table-cell>
		        							  		   <fo:table-cell border-style="solid">
		        							  		  		<fo:block text-align="center" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
		        											<fo:block text-align="center" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">Miller Number</fo:block>
		        							  		  </fo:table-cell>
		        							  		    <fo:table-cell border-style="solid">
		        							  		  		<fo:block text-align="center" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
		        											<fo:block text-align="center" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">Miller Date</fo:block>
		        							  		  </fo:table-cell>
		        							  		  <fo:table-cell border-style="solid">
		        											<fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
		        											<fo:block text-align="center" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; PARTY CODE </fo:block>
		        											<fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
		        							  		  </fo:table-cell>
		        							  		  <fo:table-cell border-style="solid">
		        											<fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
		        											<fo:block text-align="center" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; AMOUNT Rs. </fo:block>
		        											<fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
		        							  		  </fo:table-cell>
				   									 </fo:table-row>
				   									 </fo:table-body>
				   		                    </fo:table>
		        						</fo:table-cell>
						        	</fo:table-row>
						        	<#assign sno=0>
        							<#assign totalAmount = 0>
						        	<#assign party =eachh.getKey()>
					            	<#assign printPaymentsList =eachh.getValue()>
					             
        						  <#list printPaymentsList as paymentListReport>
        						  <#assign sno=sno+1>
        						  <#assign  partyName="">
        						  <#assign  partyId="">
        						  <#if paymentListReport.partyIdTo?exists>
			            			  <#assign partyId = paymentListReport.partyIdTo>
			            		  	  <#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)> 
			            		  </#if>
						        	<fo:table-row>
		        						<fo:table-cell>
	        		                    <fo:table  table-layout="fixed" width="65%" space-before="0.2in">
			    								<fo:table-column column-width="10%"/>
				   									 <fo:table-column column-width="40%"/>
				   									 <fo:table-column column-width="25%"/>
				   									 <fo:table-column column-width="25%"/>
				   									 <fo:table-column column-width="30%"/>
				   									  <fo:table-column column-width="23%"/>
				   									 <fo:table-column column-width="30%"/>	
			   									 <fo:table-body>
			   									 <fo:table-row>
			   									 <fo:table-cell border-style="solid">
	        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always">&#160;${sno}</fo:block>
	        							  		  </fo:table-cell>
	        							  		  <fo:table-cell border-style="solid">
	        											<fo:block text-align="center" font-size="12pt" white-space-collapse="false">&#160; ${partyName?if_exists} </fo:block>
	        							  		  </fo:table-cell>
	        							  		  <fo:table-cell border-style="solid">
	        											<fo:block text-align="center" font-size="12pt" white-space-collapse="false">&#160; ${paymentListReport.invoiceId?if_exists}</fo:block>
	        							  		  </fo:table-cell>
	        							  		  <fo:table-cell border-style="solid">
	        											<fo:block text-align="center" font-size="12pt" white-space-collapse="false" >${paymentListReport.millernumber?if_exists}</fo:block>
	        							  		  </fo:table-cell>
	        							  		   <fo:table-cell border-style="solid">
	        											<fo:block text-align="center" font-size="12pt" white-space-collapse="false" >${paymentListReport.millerDate?if_exists}</fo:block>
	        							  		  </fo:table-cell>
	        							  		  <fo:table-cell border-style="solid">
	        											<fo:block text-align="center" font-size="12pt" white-space-collapse="false">&#160; ${partyId?if_exists}</fo:block>
	        							  		  </fo:table-cell>
	        							  		  <#assign paymentAmount = paymentListReport.amount?if_exists>
	        							  		  <#assign totalAmount = totalAmount+paymentAmount>
	        							  		  <fo:table-cell border-style="solid">
	        											<fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always">&#160; <@ofbizCurrency amount=paymentAmount isoCode=currencyUomId/> </fo:block>
	        							  		  </fo:table-cell>
			   									 </fo:table-row>
			   									 </fo:table-body>
			   		                    </fo:table>
	        						</fo:table-cell> 
	        					</fo:table-row>
							</#list>
							<fo:table-row>
        						<fo:table-cell>
        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
        						</fo:table-cell>
        					</fo:table-row>	
        					<fo:table-row>		
		        				<fo:table-cell></fo:table-cell>
        							<fo:table-cell>
        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;                               Total=&#160;<@ofbizCurrency amount=totalAmount isoCode=currencyUomId/></fo:block>
        							</fo:table-cell>
        					</fo:table-row>
								 	<#assign cheqFav = "">
								 <#if (paymentGroupDetails.paymentMethodTypeId == "CHEQUE_PAYIN" || paymentGroupDetails.paymentMethodTypeId == "CHEQUE_PAYOUT")>
									 <fo:table-row>
		        						<fo:table-cell>
		        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
		        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">Cheque in favour of:${paymentGroupDetails.inFavor?if_exists}</fo:block>
		        						</fo:table-cell>
	        						</fo:table-row>
        						</#if>
        						<fo:table-row>
        						<#assign amountWords = Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(totalAmount, "%indRupees-and-paise", locale).toUpperCase()>
        							<fo:table-cell>
		        						<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160;AMOUNT IN WORDS:${StringUtil.wrapString(amountWords?default(""))} </fo:block>
		        					</fo:table-cell>
		        				</fo:table-row>
		        				<fo:table-row>
        							<fo:table-cell>
		        						<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
		        					</fo:table-cell>
        						</fo:table-row>
        						<fo:table-row>
        								<fo:table-cell>
		        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160;YOU ARE REQUESTED TO SEND THE MONEY REICEPT TO THIS OFFICE FOR OUR REFERENCE AND RECORDS</fo:block>
		        						</fo:table-cell>
		        				</fo:table-row>	
		        				<fo:table-row>
		        						<fo:table-cell>
		        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160;Thanking you </fo:block>
		        						</fo:table-cell>
		        				</fo:table-row>
		        				  		    		
		        				<fo:table-row>		
		        						<fo:table-cell></fo:table-cell>
		        						<fo:table-cell>
		        								<fo:block  font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;                        Yours faithfully </fo:block>
		        						</fo:table-cell>
		        						<fo:table-cell>
		        								<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        								<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        								<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        								<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        						</fo:table-cell>
			        			</fo:table-row>
		        				<fo:table-row>		
	        						<fo:table-cell></fo:table-cell>		
        							<fo:table-cell>
        								<fo:block  font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;                      Authorized Signature</fo:block>
        							</fo:table-cell>		        						
	        						</fo:table-row>
        						<fo:table-row>
									<fo:table-cell>
									<fo:block></fo:block>
										<fo:block></fo:block>
					            		<fo:block>-------------------------------------------------------------------------------------------------------------------</fo:block>
					       			</fo:table-cell>
								 </fo:table-row>
								 <fo:table-row>
									<fo:table-cell>
					            		<fo:block page-break-after="always"></fo:block>
					       			</fo:table-cell>
								 </fo:table-row>
						</#list>
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