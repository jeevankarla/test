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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-right=".3in" margin-top=".1in">
                <fo:region-body margin-top="0.3in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
         ${setRequestAttribute("OUTPUT_FILENAME", "paymentVoucher.pdf")}
       <#if printPaymentsList?has_content>      
		        <fo:page-sequence master-reference="main" font-size="10pt">	
		        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
		        		<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" font-size="10pt" white-space-collapse="false">&#160;${uiLabelMap.CommonPage}- <fo:page-number/> </fo:block>
		        	</fo:static-content>	        	
		        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">
        			<fo:block  keep-together="always" text-align="center" font-size="12pt" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-size="12pt" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-size="12pt" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">PAYMENT GROUP VOUCHER</fo:block>
            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
            		<fo:block>
                 	<fo:table>
                    <fo:table-column column-width="50%"/>
        			<fo:table-column column-width="25%"/>
        			<fo:table-column column-width="25%"/>
	                    <fo:table-body>
        						<#assign sno=0>
        						  <#list printPaymentsList as paymentListReport>
        						  <#assign sno=sno+1>
        						  <#assign  partyName="">
        						  <#assign  partyId="">
        						  <#if paymentListReport.partyIdFrom?exists && paymentListReport.partyIdFrom == "Company">
			            			  <#assign partyId = paymentListReport.partyIdTo>
			            		  <#else>
			            			  <#assign partyId = paymentListReport.partyIdFrom>
			            		  </#if>
        						 <#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)>
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
        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;<#if paymentGroupTypeDetails.description?has_content>PAYMENT GROUP TYPE:${paymentGroupTypeDetails.description?if_exists}</#if></fo:block>
        						 	</fo:table-cell>
        						 	<fo:table-cell>
        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
        						 	</fo:table-cell>
        						 	<fo:table-cell>
        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">PAYMENT GROUP ID : ${paymentGroupId?if_exists}</fo:block>
        						 	</fo:table-cell>
		        				</fo:table-row>
		        				<fo:table-row> 
        						 	<fo:table-cell>
        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;PAYMENT ID:${paymentListReport.paymentId?if_exists}</fo:block>
        						 	</fo:table-cell>
        						 	<fo:table-cell>
        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"><#if paymentListReport.paymentMethodTypeId?has_content && (paymentListReport.paymentMethodTypeId == "CHEQUE_PAYIN" || paymentListReport.paymentMethodTypeId == "CHEQUE_PAYOUT")>CHEQUE</#if><#if paymentListReport.paymentMethodTypeId?has_content && (paymentListReport.paymentMethodTypeId == "CASH_PAYIN" || paymentListReport.paymentMethodTypeId == "CASH_PAYOUT")>CASH</#if></fo:block>
        						 	</fo:table-cell>
        						 	<fo:table-cell>
        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">PAYMENT DATE:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(paymentGroupDetails.paymentDate?if_exists, "dd-MM-yyyy")}</fo:block>
        						 	</fo:table-cell>
		        				</fo:table-row>
        						 <#if paymentListReport.paymentMethodTypeId?has_content && (paymentListReport.paymentMethodTypeId == "CHEQUE_PAYIN" || paymentListReport.paymentMethodTypeId == "CHEQUE_PAYOUT")> 
									 <#if (paymentListReport.paymentMethodTypeId == "CHEQUE_PAYIN" || paymentListReport.paymentMethodTypeId == "CHEQUE_PAYOUT")>   
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
	        						 <#if (paymentListReport.paymentMethodTypeId == "CHEQUE_PAYIN" || paymentListReport.paymentMethodTypeId == "CHEQUE_PAYOUT")>
	        						 <fo:table-row> 
	        						 	<fo:table-cell>
	        						 		<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"><#if paymentGroupDetails?has_content>&#160;CHEQUE BANK DETAILS:${paymentGroupDetails.issuingAuthority?if_exists}</#if></fo:block>
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
		        					<fo:table-row>
		        						<fo:table-cell>
		        		                    <fo:table  table-layout="fixed" width="100%" space-before="0.2in">
				    								 <fo:table-column column-width="10%"/>
				   									 <fo:table-column column-width="60%"/>
				   									 <fo:table-column column-width="45%"/>
				   									 <fo:table-column column-width="27%"/>	
				   									 <fo:table-body>
				   									 <fo:table-row>
				   									 <fo:table-cell border-style="solid">
		        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
		        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;S.No</fo:block>
		        							  		  </fo:table-cell>
		        							  		  <fo:table-cell border-style="solid">
		        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
		        											<fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; DESCRIPTION </fo:block>
		        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
		        							  		  </fo:table-cell>
		        							  		  <fo:table-cell border-style="solid">
		        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
		        											<fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; PARTY CODE </fo:block>
		        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
		        							  		  </fo:table-cell>
		        							  		  <fo:table-cell border-style="solid">
		        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
		        											<fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; AMOUNT Rs. </fo:block>
		        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
		        							  		  </fo:table-cell>
				   									 </fo:table-row>
				   									 </fo:table-body>
				   		                    </fo:table>
		        						</fo:table-cell>
						        	</fo:table-row>
						        	<fo:table-row>
		        						<fo:table-cell>
	        		                    <fo:table  table-layout="fixed" width="100%" space-before="0.2in">
			    								 <fo:table-column column-width="10%"/>
			   									 <fo:table-column column-width="60%"/>
			   									 <fo:table-column column-width="45%"/>
			   									 <fo:table-column column-width="27%"/>	
			   									 <fo:table-body>
			   									 <fo:table-row>
			   									 <fo:table-cell border-style="solid">
	        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always">&#160;${sno}</fo:block>
	        							  		  </fo:table-cell>
	        							  		  <fo:table-cell border-style="solid">
	        											<fo:block text-align="center" font-size="12pt" white-space-collapse="false">&#160; ${partyName?if_exists} </fo:block>
	        							  		  </fo:table-cell>
	        							  		  <fo:table-cell border-style="solid">
	        											<fo:block text-align="center" font-size="12pt" white-space-collapse="false">&#160; ${partyId?if_exists}</fo:block>
	        							  		  </fo:table-cell>
	        							  		  <#assign paymentAmount = paymentListReport.amount?if_exists>
	        							  		  <fo:table-cell border-style="solid">
	        											<fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always">&#160; <@ofbizCurrency amount=paymentAmount isoCode=currencyUomId/> </fo:block>
	        							  		  </fo:table-cell>
			   									 </fo:table-row>
			   									 </fo:table-body>
			   		                    </fo:table>
	        						</fo:table-cell> 
	        					</fo:table-row>
	        					<fo:table-row>
									<fo:table-cell>
					            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					       			</fo:table-cell>
								</fo:table-row>
								 <#assign cheqFav = "">
								 <#if (paymentListReport.paymentMethodTypeId == "CHEQUE_PAYIN" || paymentListReport.paymentMethodTypeId == "CHEQUE_PAYOUT")>
									 <fo:table-row>
		        						<fo:table-cell>
		        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
		        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">Cheque in favour of:${paymentGroupDetails.inFavor?if_exists}</fo:block>
		        						</fo:table-cell>
	        						</fo:table-row>
        						</#if>
        							<fo:table-row>
									<fo:table-cell>
					            		<fo:block>-------------------------------------------------------------------------------------------------------------------</fo:block>
					       			</fo:table-cell>
								 </fo:table-row>
								 <fo:table-row>
									<fo:table-cell>
					            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					       			</fo:table-cell>
								</fo:table-row>
							</#list>
							<fo:table-row>
        						<fo:table-cell>
        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">PROCD.&#160;&#160;&#160;&#160;&#160;&#160;D.Mgr(Finance)/Mgr(Finance)/GM(Finance)</fo:block>
        						</fo:table-cell>
        							<fo:table-cell>
        							 	<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160;</fo:block>
        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Pre Audit.&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Director</fo:block>
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
									<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160;</fo:block>
									<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
									<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
									<fo:block text-align="right" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160;</fo:block>
									<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
									<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
									<fo:block text-align="right" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
								</fo:table-cell>
								<fo:table-cell border-style = "solid">
									<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160;</fo:block>
									<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
									<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
									<fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">Signature of Recipient</fo:block>
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