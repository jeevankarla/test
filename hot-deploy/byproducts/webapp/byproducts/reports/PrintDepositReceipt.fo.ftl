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
            margin-top="0.2in" margin-bottom=".3in" margin-left=".5in" margin-right=".1in">
        <fo:region-body margin-top="3.3in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
   ${setRequestAttribute("OUTPUT_FILENAME", "payrcpt.pdf")}
        <#assign SNO=0> 
        <#if newFinAccountTransId?has_content> 
           <fo:page-sequence master-reference="main" force-page-count="no-force" font-size="14pt" font-family="Courier,monospace">					
		    	<fo:static-content flow-name="xsl-region-before">
		    	   <fo:block  keep-together="always" text-align="center" font-weight = "bold" font-family="Courier,monospace" white-space-collapse="false">&#160;  </fo:block>
			       <fo:block  keep-together="always" text-align="center" font-weight = "bold" font-family="Courier,monospace" white-space-collapse="false">&#160;  </fo:block>
					<fo:block  keep-together="always" text-align="center" font-weight = "bold" font-family="Courier,monospace" white-space-collapse="false">&#160;  &#160;&#160;   KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD</fo:block>
					<fo:block  keep-together="always" text-align="center" font-weight = "bold" font-family="Courier,monospace" white-space-collapse="false">&#160;      UNIT: MOTHER DAIRY: G.K.V.K POST,YELAHANKA,BENGALORE:560065</fo:block>
                    <fo:block text-align="left"  keep-together="always"  font-weight = "bold" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160; &#160;Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(paymentDate, "MMMM dd,yyyy HH:MM:SS")}&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>   </fo:block>
              		<fo:block>-------------------------------------------------------------------------------</fo:block>
              		<fo:block font-weight = "bold">Received with thanks the RECEIPT by way of ${finAccountTransTypeId?if_exists}										</fo:block>
            		<fo:block>
            		<fo:table>
                    <fo:table-column column-width="38%"/>
                    <fo:table-column column-width="50%"/>
                    <fo:table-body>
                    <fo:table-row>
                              <#if partyName?has_content>
                				<fo:table-cell>
                            		<fo:block  text-align="left"  font-weight = "bold">From:${partyName?if_exists}</fo:block>  
                       			</fo:table-cell>
                       			<#else>
                       			<fo:table-cell>
                            		<fo:block  text-align="left"  font-weight = "bold">&#160;</fo:block>  
                       			</fo:table-cell>
                       			</#if>
                				<fo:table-cell>
                            		<fo:block  keep-together="always" text-align="right" font-weight = "bold">Receipt Number:${newFinAccountTransId?if_exists}&#160;&#160;&#160;</fo:block>  
                       			</fo:table-cell>
                    </fo:table-row>	
                     <fo:table-row>
                				<fo:table-cell>
                            		<fo:block  text-align="left"  ></fo:block>  
                       			</fo:table-cell>
                				<fo:table-cell>
                            		<fo:block  keep-together="always" text-align="right" font-weight = "bold">Receipt Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(paymentDate?if_exists, "MMMM dd,yyyy")}</fo:block>  
                       			</fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
                				<fo:table-cell>
                            		<fo:block  text-align="left" font-weight = "bold" keep-together="always">Instrument No:${contraRefNum?if_exists}</fo:block>  
                       			</fo:table-cell>
                    </fo:table-row>
                     </fo:table-body>
                      </fo:table>
            		</fo:block>
            		<fo:block>-------------------------------------------------------------------------------</fo:block>
            		<fo:block font-weight = "bold">&#160;&#160;&#160;Receipt Id													&#160;&#160;&#160;&#160;&#160;&#160;&#160;Description 								&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Party Code			&#160;&#160;&#160;&#160;&#160;&#160;&#160;Amount</fo:block>
            		<fo:block>-------------------------------------------------------------------------------</fo:block>
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
            	<fo:block>
                 	<fo:table>
                    <fo:table-column column-width="130pt"/>
                    <fo:table-column column-width="165pt"/>
                    <fo:table-column column-width="165pt"/>
                    <fo:table-column column-width="140pt"/> 
                    <fo:table-body>
                    <#if newFinAccountTransId?exists>
							<fo:table-row>
                				<fo:table-cell>
                            		<fo:block  text-align="left"  white-space-collapse="false">&#160;&#160;&#160;${newFinAccountTransId?if_exists}</fo:block>  
                       			</fo:table-cell>
                				<fo:table-cell>
                            		<fo:block  keep-together="always" text-align="left">${partyName?if_exists}</fo:block>  
                       			</fo:table-cell>
                       			
                       			<fo:table-cell>
                            		<fo:block  text-align="right"  white-space-collapse="false">${partyId?if_exists}</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                            		<fo:block  text-align="right"  white-space-collapse="false">${amount?if_exists?string("#0.00")}</fo:block>  
                       			</fo:table-cell>
            				</fo:table-row>
            				<fo:table-row>
               					<fo:table-cell >
                    				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
               					</fo:table-cell>
		  					</fo:table-row>
		  					
		  					<fo:table-row>
            				     <fo:table-cell font-weight = "bold">
                    				<fo:block keep-together="always">Deposit Type:</fo:block>
               					</fo:table-cell>
               					<fo:table-cell >
                    				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
               					</fo:table-cell>
               					<fo:table-cell >
               					<fo:block keep-together="always">${description?if_exists}</fo:block>
               					</fo:table-cell>
		  					</fo:table-row>
		  					
		  					<fo:table-row>
            				     <fo:table-cell font-weight = "bold">
                    				<fo:block keep-together="always">Towards:</fo:block>
               					</fo:table-cell>
               					<fo:table-cell >
                    				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
               					</fo:table-cell>
               					<fo:table-cell>
               					<fo:block keep-together="always">${comments?if_exists}</fo:block>
               					</fo:table-cell>
		  					</fo:table-row>
		  					
		  					<fo:table-row>
               					 <fo:table-cell font-weight = "bold">
                    				<fo:block keep-together="always">Transferred To:</fo:block>
               					</fo:table-cell>
               					<fo:table-cell >
                    				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
               					</fo:table-cell>
               					<fo:table-cell>
               					<fo:block keep-together="always">${finAccountName?if_exists}</fo:block>
               					</fo:table-cell>
		  					</fo:table-row>
		  					</#if>
		  					<fo:table-row>
               					<fo:table-cell >
                    				<fo:block>-------------------------------------------------------------------------------</fo:block>
               					</fo:table-cell>
		  					</fo:table-row>
						  	<fo:table-row>
			                   	<fo:table-cell number-columns-spanned="4">
                   					<fo:block white-space-collapse="false" font-weight = "bold" keep-together="always">&#160;&#160;&#160;(In Words: ${amountWords} only)</fo:block>
			                   	</fo:table-cell>
						  	</fo:table-row>
						  	<fo:table-row>
               					<fo:table-cell>
                    				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
               					</fo:table-cell>
		  					</fo:table-row>
						  	<fo:table-row>
			                   <fo:table-cell>
			                        	<fo:block keep-together="always" font-weight = "bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;For Mother Dairy</fo:block>
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
			                        	<fo:block keep-together="always" font-weight = "bold">&#160;&#160;SUPDT/DMF/AM                     &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Authorised Signatory</fo:block>
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