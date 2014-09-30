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
            margin-top="0.2in" margin-bottom=".3in" margin-left=".55in" margin-right=".1in">
        <fo:region-body margin-top="2.8in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
   ${setRequestAttribute("OUTPUT_FILENAME", "payrcpt.pdf")}
        <#assign totalPaymentAmount = 0>
        <#assign SNO=0> 
        <#if printPaymentsList?has_content> 
        <#list printPaymentsList as paymentListReport>
           <fo:page-sequence master-reference="main" force-page-count="no-force" font-size="14pt" font-family="Courier,monospace">					
		    	<fo:static-content flow-name="xsl-region-before">
		    	   <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">&#160;  </fo:block>
			       <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">&#160;  </fo:block>
					<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">&#160;  &#160;&#160;   KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD</fo:block>
					<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">&#160;      UNIT: MOTHER DAIRY: G.K.V.K POST,YELAHANKA,BENGALORE:560065</fo:block>
                    <fo:block text-align="left"  keep-together="always"  white-space-collapse="false">&#160;&#160;&#160;&#160;&#160; &#160;Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(paymentListReport.paymentDate, "MMMM dd,yyyy HH:MM:SS")}&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>   </fo:block>
              		<fo:block>------------------------------------------------------------------------</fo:block>
              		
              		<#--
              		<#if reportType == "Receipt">
              			<fo:block >&#160;&#160;&#160;&#160;&#160; Received with thanks, the ${paymentTypeDescription} by ${paymentMethodTypeDesc}											</fo:block>
              		</#if>
              		<#if reportType == "Payment">
              			<fo:block >&#160;&#160;&#160;&#160;&#160; Paid as ${paymentTypeDescription}	</fo:block>
              		</#if>
              		-->
              		
            		<#assign  partyName="">
            			
            		<fo:block><fo:table>
                    <fo:table-column column-width="18%"/>
                    <fo:table-column column-width="2%"/>
                    <fo:table-column column-width="30%"/>
                    <fo:table-column column-width="18%"/>
                    <fo:table-column column-width="2%"/>
                    <fo:table-column column-width="30%"/>
                    
	                    <fo:table-body>
		                    <fo:table-row>
		                          <fo:table-cell>
		                    		   <fo:block  keep-together="always" text-align="left">${reportType} Mode</fo:block>  
		               			  </fo:table-cell>
		               			  <fo:table-cell>
		                        		<fo:block  text-align="left"  >:</fo:block>  
		                   		  </fo:table-cell>
		                   		  <fo:table-cell>
		                        		<fo:block  text-align="left"  >${paymentMethodTypeDesc}</fo:block>  
		                   		  </fo:table-cell>
		        				  <#if (paymentListReport.issuingAuthority)?has_content>
		                   		    	<fo:table-cell>
		                            		<fo:block  keep-together="always" text-align="left">Bank Name</fo:block>  
		                       			</fo:table-cell>
		                       			<fo:table-cell>
			                        		<fo:block  text-align="left"  >:</fo:block>  
			                   		    </fo:table-cell>
			                   		    <fo:table-cell>
			                        		<fo:block  text-align="left"  >${paymentListReport.issuingAuthority?if_exists} ${paymentListReport.issuingAuthorityBranch?if_exists}</fo:block>  
			                   		    </fo:table-cell>
		                   		    </#if>
		                    </fo:table-row>	
		                    <fo:table-row>
		                              <fo:table-cell>
		                            		<fo:block  keep-together="always" text-align="left">${reportType} Number</fo:block>  
		                       			</fo:table-cell>
		                       			<fo:table-cell>
			                        		<fo:block  text-align="left"  >:</fo:block>  
			                   		    </fo:table-cell>
			                   		    <fo:table-cell>
			                        		<fo:block  text-align="left"  >${paymentListReport.paymentId?if_exists}&#160;&#160;&#160;</fo:block>  
			                   		    </fo:table-cell>
			                   		    <#if (paymentListReport.paymentRefNum)?has_content>
			                   		    	<fo:table-cell>
			                            		<fo:block  keep-together="always" text-align="left">Instrument No</fo:block>  
			                       			</fo:table-cell>
			                       			<fo:table-cell>
				                        		<fo:block  text-align="left"  >:</fo:block>  
				                   		    </fo:table-cell>
				                   		    <fo:table-cell>
				                        		<fo:block  text-align="left"  >${paymentListReport.paymentRefNum?if_exists}</fo:block>  
				                   		    </fo:table-cell>
			                   		    </#if>
		                    </fo:table-row>	
		                     <fo:table-row>
		                				<fo:table-cell>
		                            		<fo:block  keep-together="always" text-align="left">${reportType} Date</fo:block>  
		                       			</fo:table-cell>
		                       			<fo:table-cell>
			                        		<fo:block  text-align="left"  >:</fo:block>  
			                   		    </fo:table-cell>
			                   		    <fo:table-cell>
			                        		<fo:block  text-align="left"  >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(paymentListReport.paymentDate, "MMMM dd,yyyy")}</fo:block>  
			                   		    </fo:table-cell>
			                   		    
			                   		    
			                   		    
		                    </fo:table-row>		
	                     </fo:table-body>
                      </fo:table>
            		</fo:block>
            		<fo:block>------------------------------------------------------------------------</fo:block>
            		<fo:block >&#160;&#160;${reportType}													&#160;&#160;&#160;&#160;&#160;&#160;&#160;Description 								&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Party Name			&#160;&#160;&#160;&#160;&#160;&#160;Amount</fo:block>
            		<fo:block>------------------------------------------------------------------------</fo:block>
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
            	<fo:block>
                 	<fo:table>
                    <fo:table-column column-width="100pt"/>
                    <fo:table-column column-width="260pt"/>
                    <fo:table-column column-width="160pt"/>
                    <fo:table-column column-width="80pt"/> 
                    <fo:table-body>
                    		<#assign totalAmount = 0>
							<fo:table-row>
		                          <fo:table-cell>
		                        		<fo:block  text-align="left"  >&#160;</fo:block>  
		                   		  </fo:table-cell>
		                    </fo:table-row>	
							<fo:table-row>
                				<fo:table-cell>
                            		<fo:block  text-align="left"  white-space-collapse="false">&#160;&#160;&#160;${paymentListReport.paymentId?if_exists}</fo:block>  
                       			</fo:table-cell>
                       			<#if paymentListReport.partyIdFrom?exists>
            			
            			
            			<#if reportType == "Receipt">
	              			<#assign partyId=paymentListReport.partyIdFrom>											
	              		</#if>
	              		<#if reportType == "Payment">
	              			<#assign partyId=paymentListReport.partyIdTo>											
	              		</#if>
            			
            			<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)>
            			</#if>
                				<fo:table-cell>
                            		<fo:block  text-align="left">${paymentTypeDescription}</fo:block>  
                       			</fo:table-cell>
                       			
                       			<fo:table-cell>
                            		<fo:block  text-align="center"  white-space-collapse="false">${partyName}</fo:block>  
                       			</fo:table-cell>
                       			<#assign totalAmount =(totalAmount+ paymentListReport.amount) >
                       			<fo:table-cell>
                            		<fo:block  text-align="right"  white-space-collapse="false">${paymentListReport.amount?if_exists?string("#0.00")}</fo:block>  
                       			</fo:table-cell>
            				</fo:table-row>
            				<fo:table-row>
            				     <fo:table-cell>
                    				<fo:block></fo:block>
               					</fo:table-cell>
               					<fo:table-cell number-columns-spanned="2">
               					<fo:block>${paymentListReport.comments?if_exists}</fo:block>
               					</fo:table-cell>
		  					</fo:table-row>
            				<fo:table-row>
               					<fo:table-cell >
                    				<fo:block>------------------------------------------------------------------------</fo:block>
               					</fo:table-cell>
		  					</fo:table-row>
            				<fo:table-row>
                				<fo:table-cell>
                            		<fo:block  keep-together="always" text-align="center"  white-space-collapse="false">Total Amount</fo:block>  
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
                    				<fo:block>------------------------------------------------------------------------</fo:block>
               					</fo:table-cell>
		  					</fo:table-row>
						  	<fo:table-row>
			                   	<fo:table-cell number-columns-spanned="4">
                   					<fo:block white-space-collapse="false" keep-together="always">&#160;&#160;&#160;(In Words: ${paymentListReport.amountWords} only)</fo:block>
			                   	</fo:table-cell>
						  	</fo:table-row>
						  	<#assign cheqFav = "">
								 <#if (paymentListReport.paymentMethodTypeId == "CHEQUE_PAYIN" || paymentListReport.paymentMethodTypeId == "CHEQUE_PAYOUT")>
									 <#if paymentListReport.paymentId?has_content>
									 <#assign paymentAttrDetails = delegator.findOne("PaymentAttribute", {"paymentId" : paymentListReport.paymentId, "attrName" : "INFAVOUR_OF"}, true)?if_exists/>
									 
									  <#if paymentAttrDetails.attrValue?has_content>
									  	<#assign cheqFav = paymentAttrDetails.attrValue?if_exists>
									  <#else>
									  	<#assign cheqFav = partyName?if_exists>
									 </#if>
									 <fo:table-row>
		        						<fo:table-cell>
		        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
		        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">Cheque in favour of:${cheqFav?if_exists}</fo:block>
		        						</fo:table-cell>
	        						</fo:table-row>
	        						</#if>
        						</#if>
		              			<fo:table-row>
	        						<fo:table-cell number-columns-spanned="2">
	        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160; </fo:block>
	        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160; </fo:block>
	        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160; </fo:block>
	        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160; </fo:block>
	        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" >PROCD.&#160;&#160;&#160;&#160;&#160;&#160;D.Mgr(Finance)/Mgr(Finance)/GM(Finance)</fo:block>
	        						</fo:table-cell>
	        							<fo:table-cell>
	        							 	<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160;</fo:block>
	        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160; </fo:block>
	        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160; </fo:block>
	        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160; </fo:block>
	        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" >&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Pre Audit</fo:block>
	        						</fo:table-cell>
	        						<fo:table-cell>
	        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160;</fo:block>
	        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160; </fo:block>
	        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160; </fo:block>
	        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160; </fo:block>
	        								<fo:block text-align="right" font-size="12pt" white-space-collapse="false" keep-together="always" >&#160; Director</fo:block>
	        						</fo:table-cell>  
        						</fo:table-row>								
		              </fo:table-body>
		                </fo:table>
		               </fo:block> 	
		               <#if reportType == "Payment">
			               <fo:block >
								<fo:table  table-layout="fixed" width="46%" space-before="0.2in" font-family="Courier,monospace">
							    <fo:table-column column-width="50%"/>
							    <fo:table-column column-width="50%"/>
							    <fo:table-column column-width="40%"/>
								<fo:table-body>
									<fo:table-row>
										<fo:table-cell>
											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160;</fo:block>
											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160; </fo:block>
											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160; </fo:block>
											<fo:block text-align="right" font-size="12pt" white-space-collapse="false" keep-together="always" >&#160;</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160;</fo:block>
											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160; </fo:block>
											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160; </fo:block>
											<fo:block text-align="right" font-size="12pt" white-space-collapse="false" keep-together="always" >&#160;</fo:block>
										</fo:table-cell>
										<fo:table-cell border-style = "solid">
											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160;</fo:block>
											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160; </fo:block>
											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160; </fo:block>
											<fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always" >Signature of Recipient</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>	
					 </#if> 
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