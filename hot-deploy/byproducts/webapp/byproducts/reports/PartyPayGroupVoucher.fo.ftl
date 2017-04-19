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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-right=".3in" margin-top=".3in">
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
						<fo:block text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">&#160;${BOAddress?if_exists}</fo:block>
						<fo:block  keep-together="always" text-align="center" font-size="12pt" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">PARTY WISE GROUP PAYMENT VOUCHER</fo:block>
		        	</fo:static-content>
		        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">
		        	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        	
		        	<#assign partyMap = partyMap.entrySet()>
	                <#list partyMap as eachParty>
		        	
		        	<fo:block>
                 	<fo:table>
                    <fo:table-column column-width="50%"/>
        			<fo:table-column column-width="25%"/>
        			<fo:table-column column-width="25%"/>
	                    <fo:table-body>
	                    
	                    <#if paymentGroupId?has_content>
							<#assign paymentGroupDetails = delegator.findOne("PaymentGroup", {"paymentGroupId" : paymentGroupId}, true)?if_exists/>
						</#if>
						<#if paymentGroupDetails.paymentGroupTypeId?has_content>
							<#assign paymentGroupTypeDetails = delegator.findOne("PaymentGroupType", {"paymentGroupTypeId" : paymentGroupDetails.paymentGroupTypeId}, true)?if_exists/>
						</#if>
							<fo:table-row> 
        						<fo:table-cell>
        						 	<fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; <#if paymentGroupTypeDetails.description?has_content>PAYMENT GROUP TYPE: ${paymentGroupTypeDetails.description?if_exists}</#if></fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        						 	<fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        						 	<fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">PAYMENT GROUP ID : ${paymentGroupId?if_exists}</fo:block>
        						</fo:table-cell>
		        			</fo:table-row>
		        			<fo:table-row>
		        			    <fo:table-cell>
        						 	<fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        						 	<fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold"><#if paymentGroupDetails.paymentMethodTypeId?has_content && (paymentGroupDetails.paymentMethodTypeId == "CHEQUE_PAYIN" || paymentGroupDetails.paymentMethodTypeId == "CHEQUE_PAYOUT")>CHEQUE</#if><#if paymentGroupDetails.paymentMethodTypeId?has_content && (paymentGroupDetails.paymentMethodTypeId == "CASH_PAYIN" || paymentGroupDetails.paymentMethodTypeId == "CASH_PAYOUT")>CASH</#if></fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        						 	<fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">PAYMENT DATE:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(paymentGroupDetails.paymentDate?if_exists, "dd-MM-yyyy")}</fo:block>
        						</fo:table-cell>
		        			</fo:table-row>
		        			<#if paymentGroupDetails.paymentMethodTypeId?has_content && (paymentGroupDetails.paymentMethodTypeId == "CHEQUE_PAYIN" || paymentGroupDetails.paymentMethodTypeId == "CHEQUE_PAYOUT")> 
							  <#if (paymentGroupDetails.paymentMethodTypeId == "CHEQUE_PAYIN" || paymentGroupDetails.paymentMethodTypeId == "CHEQUE_PAYOUT")>   
	        				<fo:table-row> 
	        					<fo:table-cell>
	        						 <fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;CHEQUE NO:${paymentGroupDetails.paymentRefNum?if_exists}</fo:block>
	        					</fo:table-cell>
	        					<fo:table-cell>
	        						 <fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
	        					</fo:table-cell>
	        						 <fo:table-cell>
	        					<fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">CHEQUE DATE:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(paymentGroupDetails.instrumentDate?if_exists, "dd-MM-yyyy")}</fo:block>
	        						 </fo:table-cell>
	        					</fo:table-row>
	        				<#if (paymentGroupDetails.paymentMethodTypeId == "CHEQUE_PAYIN" || paymentGroupDetails.paymentMethodTypeId == "CHEQUE_PAYOUT")>
	        					<fo:table-row> 
	        						<#if paymentGroupDetails.finAccountId?has_content>
	        							<#assign finAccountDetails = delegator.findOne("FinAccount", {"finAccountId" : paymentGroupDetails.finAccountId}, true)?if_exists/>
		        						 <fo:table-cell>
		        						 	<fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold"><#if finAccountDetails?has_content>&#160;CHEQUE BANK DETAILS:${finAccountDetails.finAccountName?if_exists}</#if></fo:block>
		        						 </fo:table-cell>
	        						</#if>
	        						 <fo:table-cell>
	        						 	<fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
	        						 </fo:table-cell>
	        						 <fo:table-cell>
	        						 	<fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
	        						 </fo:table-cell>
	        					 </fo:table-row>
	        				 </#if>
	        				</#if>
		        		  </#if>
		        		</fo:table-body>
                		</fo:table>
               		</fo:block>
               		
            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
            		<fo:block>
                 	<fo:table>
                    <fo:table-column column-width="50%"/>
        			<fo:table-column column-width="25%"/>
        			<fo:table-column column-width="25%"/>
	                    <fo:table-body>
		        		  <fo:table-row>
		        						<fo:table-cell>
		        		                    <fo:table  table-layout="fixed" width="60%" space-before="0.2in">
				    								 <fo:table-column column-width="6.5%"/>
				    								 <fo:table-column column-width="15.5%"/>
				    								 <fo:table-column column-width="20%"/>
				    								  <fo:table-column column-width="18%"/>
				    								   <fo:table-column column-width="22%"/>
				    								   <fo:table-column column-width="20%"/>
				   									 <fo:table-column column-width="40%"/>
				   									 <fo:table-column column-width="15.5%"/>
				   									 <fo:table-column column-width="24.5%"/>	
				   									 <fo:table-body>
				   									 <fo:table-row>
				   									 <fo:table-cell border-style="solid">
		        											<fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
		        											<fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">S.No</fo:block>
		        							  		  </fo:table-cell>
		        							  		  <fo:table-cell border-style="solid">
		        							  		  		<fo:block text-align="center" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
		        											<fo:block text-align="center" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">Payment Id</fo:block>
		        							  		  </fo:table-cell>
		        							  		  <fo:table-cell border-style="solid">
		        							  		  		<fo:block text-align="center" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
		        											<fo:block text-align="center" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">Invoice Id</fo:block>
		        							  		  </fo:table-cell>
		        							  		   <fo:table-cell border-style="solid">
		        							  		  		<fo:block text-align="center" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
		        											<fo:block text-align="center" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">Status</fo:block>
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
		        											<fo:block text-align="center" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; DESCRIPTION </fo:block>
		        											<fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
		        							  		  </fo:table-cell>
		        							  		  <fo:table-cell border-style="solid">
		        											<fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
		        											<fo:block text-align="center" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold"> PARTY CODE </fo:block>
		        											<fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
		        							  		  </fo:table-cell>
		        							  		  <fo:table-cell border-style="solid">
		        											<fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
		        											<fo:block text-align="center" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold"> AMOUNT Rs. </fo:block>
		        											<fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
		        							  		  </fo:table-cell>
				   									 </fo:table-row>
				   									 </fo:table-body>
				   		                    </fo:table>
		        						</fo:table-cell>
						        	</fo:table-row>
						        	
						        	 <fo:table-row>
		        						<fo:table-cell>
	        		                    <fo:table  table-layout="fixed" width="60%" space-before="0.2in">
			    								 <fo:table-column column-width="6.5%"/>
			    								 <fo:table-column column-width="15.5%"/>
			    								 <fo:table-column column-width="20%"/>
			    								  <fo:table-column column-width="18%"/>
			    								  <fo:table-column column-width="22%"/>
			    								   <fo:table-column column-width="20%"/>
			   									 <fo:table-column column-width="40%"/>
			   									 <fo:table-column column-width="15.5%"/>
			   									 <fo:table-column column-width="24.5%"/>	
			   									 <fo:table-body>
			   									 <#assign sno=0>
						           <#assign partyDts = eachParty.getValue()>
						           <#assign totalAmount = 0>
	                                
	                               <#list partyDts as eachPayment>
	                                  <#assign sno=sno+1>
	                                  <#assign  partyName="">
        						      <#assign  partyId="">
        						      <#if eachPayment.partyIdTo?exists>
			            			    <#assign partyId = eachPayment.partyIdTo>
			            		        <#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)>
			            		      </#if>  
			   									 <fo:table-row>
			   									 <fo:table-cell border-style="solid">
	        											<fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always">${sno?if_exists}</fo:block>
	        							  		  </fo:table-cell>
	        							  		  <fo:table-cell border-style="solid">
	        											<fo:block text-align="center" font-size="10pt" white-space-collapse="false" keep-together="always">${eachPayment.paymentId?if_exists}</fo:block>
	        							  		  </fo:table-cell>
	        							  		  <fo:table-cell border-style="solid">
	        											<fo:block text-align="center" font-size="10pt" white-space-collapse="false" keep-together="always">${eachPayment.invoiceId?if_exists}</fo:block>
	        							  		  </fo:table-cell>
	        							  		   <fo:table-cell border-style="solid">
	        											<fo:block text-align="center" font-size="10pt" white-space-collapse="false" keep-together="always">${eachPayment.statusId?if_exists}</fo:block>
	        							  		  </fo:table-cell>
	        							  		   <fo:table-cell border-style="solid">
	        											<fo:block text-align="center" font-size="10pt" white-space-collapse="false" >${eachPayment.millernumber?if_exists}</fo:block>
	        							  		  </fo:table-cell>
	        							  		   <fo:table-cell border-style="solid">
	        											<fo:block text-align="center" font-size="10pt" white-space-collapse="false" >${eachPayment.millerDate?if_exists}</fo:block>
	        							  		  </fo:table-cell>
	        							  		  <fo:table-cell border-style="solid">
	        											<fo:block text-align="center" font-size="10pt" white-space-collapse="false">${partyName?if_exists} </fo:block>
	        							  		  </fo:table-cell>
	        							  		  <fo:table-cell border-style="solid">
	        											<fo:block text-align="center" font-size="10pt" white-space-collapse="false">${partyId?if_exists} </fo:block>
	        							  		  </fo:table-cell>
	        							  		  <#assign paymentAmount = eachPayment.amount?if_exists>
	        							  		  <#assign totalAmount = totalAmount+paymentAmount>
	        							  		  <fo:table-cell border-style="solid">
	        											<fo:block text-align="right" font-size="10pt" white-space-collapse="false" keep-together="always">${(paymentAmount)?if_exists?string("##0.00")}</fo:block>
	        							  		  </fo:table-cell>
			   									 </fo:table-row>
			   									 </#list>
			   									 <fo:table-row>
			   									 <fo:table-cell border-bottom-style="solid" border-left-style="solid">
	        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always"></fo:block>
	        							  		  </fo:table-cell>
	        							  		  <fo:table-cell border-bottom-style="solid">
	        											<fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always"></fo:block>
	        							  		  </fo:table-cell>
	        							  		   <fo:table-cell border-bottom-style="solid">
	        											<fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always"></fo:block>
	        							  		  </fo:table-cell>
	        							  		  <fo:table-cell border-bottom-style="solid" >
	        											<fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always"></fo:block>
	        							  		  </fo:table-cell>
	        							  		   <fo:table-cell border-bottom-style="solid" >
	        											<fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always"></fo:block>
	        							  		  </fo:table-cell>
	        							  		   <fo:table-cell border-bottom-style="solid" >
	        											<fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always"></fo:block>
	        							  		  </fo:table-cell>
	        							  		  <fo:table-cell border-bottom-style="solid" >
	        											<fo:block text-align="center" font-size="12pt" line-height="10pt" font-weight="bold" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; Total </fo:block>
	        							  		  </fo:table-cell>
	        							  		  <fo:table-cell border-bottom-style="solid" border-right-style="solid">
	        											<fo:block text-align="center" font-size="12pt" white-space-collapse="false"></fo:block>
	        							  		  </fo:table-cell>
	        							  		  <fo:table-cell border-bottom-style="solid"  border-right-style="solid">
	        							  		  <fo:block>&#160;</fo:block>
	        											<fo:block text-align="right" font-size="12pt" line-height="10pt"  white-space-collapse="false" font-weight="bold" keep-together="always">${totalAmount?if_exists?string("##0.00")}</fo:block>
	        							  		  </fo:table-cell>
			   									 </fo:table-row>
			   									<fo:table-row>
        							<fo:table-cell>
		        						<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
		        					</fo:table-cell>
		        				</fo:table-row>
        						<fo:table-row>
        							<fo:table-cell>
		        						<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
		        					</fo:table-cell>
        						</fo:table-row>
        						<fo:table-row>
        							<fo:table-cell>
		        						<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
		        					</fo:table-cell>
        						</fo:table-row>
        						<fo:table-row>
        							<fo:table-cell>
		        						<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
		        					</fo:table-cell>
        						</fo:table-row>
        						<fo:table-row>
        							<fo:table-cell>
		        						<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
		        					</fo:table-cell>
        						</fo:table-row>
        						<fo:table-row>
        								<fo:table-cell>
		        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">  </fo:block>
		        						</fo:table-cell>
		        						<fo:table-cell>
		        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
		        						</fo:table-cell>
		        						<fo:table-cell>
		        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
		        						</fo:table-cell>
		        						<fo:table-cell>
		        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
		        						</fo:table-cell>
		        						<fo:table-cell>
		        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
		        						</fo:table-cell>
		        						<fo:table-cell>
		        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
		        						</fo:table-cell>
		        						<fo:table-cell>
		        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
		        						</fo:table-cell>
		        						<fo:table-cell>
		        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"></fo:block>
		        						</fo:table-cell>
		        						<fo:table-cell>
		        								<fo:block text-align="right" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> Authorized Signature</fo:block>
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
			   									 </fo:table-body>
			   		                    </fo:table>
	        						</fo:table-cell> 
	        					</fo:table-row>
				   		</fo:table-body>
                	</fo:table>
               </fo:block>
              </#list> 	
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