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
   ${setRequestAttribute("OUTPUT_FILENAME", "payrcpt.pdf")}
        <#assign totalPaymentAmount = 0>
        <#assign SNO=0> 
        <#if printPaymentsList?has_content> 
        <#list printPaymentsList as paymentListReport>
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
					<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">&#160;     KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD</fo:block>
					<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">&#160;      UNIT: MOTHER DAIRY: G.K.V.K POST,YELAHANKA,BENGALORE:560065</fo:block>
                    <fo:block text-align="left"  keep-together="always"  white-space-collapse="false">Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(paymentListReport.paymentDate, "MMMM dd,yyyy HH:MM:SS")}    &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>   </fo:block>
              		<fo:block>-------------------------------------------------------------------------</fo:block>
              		<fo:block >Received with thanks the ${reportType} of Cash													&#160;&#160;&#160;&#160;${reportType}- CSH</fo:block>
            		<#assign  partyName="">
            			
            		<fo:block><fo:table>
                    <fo:table-column column-width="38%"/>
                    <fo:table-column column-width="50%"/>
                    <fo:table-body>
                    <fo:table-row>
                              <#if partyName?has_content>
                				<fo:table-cell>
                            		<fo:block  text-align="left"  >From:${partyName?if_exists}</fo:block>  
                       			</fo:table-cell>
                       			<#else>
                       			<fo:table-cell>
                            		<fo:block  text-align="left"  >&#160;</fo:block>  
                       			</fo:table-cell>
                       			</#if>
                				<fo:table-cell>
                            		<fo:block  keep-together="always" text-align="left">Receipt Number:${paymentListReport.paymentId?if_exists}</fo:block>  
                       			</fo:table-cell>
                    </fo:table-row>	
                     <fo:table-row>
                				<fo:table-cell>
                            		<fo:block  text-align="left"  ></fo:block>  
                       			</fo:table-cell>
                				<fo:table-cell>
                            		<fo:block  keep-together="always" text-align="left">Receipt Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(paymentListReport.paymentDate, "MMMM dd,yyyy")}</fo:block>  
                       			</fo:table-cell>
                    </fo:table-row>		
                     </fo:table-body>
                      </fo:table>
            		</fo:block>
            		<fo:block>-------------------------------------------------------------------------</fo:block>
            		<fo:block >Receipt Id													&#160;&#160;&#160;&#160;&#160;&#160;&#160;Description 								&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Party Code			&#160;&#160;&#160;&#160;&#160;&#160;&#160;Amount</fo:block>
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
							<fo:table-row>
                				<fo:table-cell>
                            		<fo:block  text-align="left"  white-space-collapse="false">${paymentListReport.paymentId?if_exists}</fo:block>  
                       			</fo:table-cell>
                       			<#if paymentListReport.partyIdFrom?exists>
            			<#assign partyId=paymentListReport.partyIdFrom>
            			<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)>
            			</#if>
                				<fo:table-cell>
                				
                            		<fo:block  keep-together="always" text-align="left">${partyName}</fo:block>  
                       			
                       			</fo:table-cell>
                       			
                       			<fo:table-cell>
                            		<fo:block  text-align="right"  white-space-collapse="false">${partyId}</fo:block>  
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
               					<fo:table-cell number-columns-spanned="3">
               					<fo:block>${paymentListReport.comments?if_exists}</fo:block>
               					</fo:table-cell>
		  					</fo:table-row>
            				<fo:table-row>
               					<fo:table-cell >
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
                   					<fo:block white-space-collapse="false" keep-together="always">(In Words: ${paymentListReport.amountWords} only)</fo:block>
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