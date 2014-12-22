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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in" margin-left=".2in" margin-right=".2in">
                <fo:region-body margin-top=".4in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        
           ${setRequestAttribute("OUTPUT_FILENAME", "payrcpt.pdf")}
        
         <#assign SNO=0> 
          <#if finAccountFinalTransList?has_content> 
            <#list finAccountFinalTransList as finAccountTransList>
            <#assign finAccountTypeId = context.get("finAccountTypeId")?if_exists>
	        <#assign paymentDate = finAccountTransList.get("paymentDate")?if_exists>
	        <#assign finAccountTransTypeId = finAccountTransList.get("finAccountTransTypeId")?if_exists>
	        <#assign partyName = finAccountTransList.get("partyName")?if_exists>
	        <#assign newFinAccountTransId = finAccountTransList.get("newFinAccountTransId")?if_exists>
	        <#assign partyId = finAccountTransList.get("partyId")?if_exists>
	        <#assign amount = finAccountTransList.get("amount")?if_exists>
	        <#assign description = context.get("description")?if_exists>
	        <#assign comments = finAccountTransList.get("comments")?if_exists>
	        <#assign contraRefNum = finAccountTransList.get("contraRefNum")?if_exists>
	        <#assign finAccountName = finAccountTransList.get("finAccountName")?if_exists>
	        <#assign amountWords = finAccountTransList.get("amountWords")?if_exists>
	        <#assign finAccountTransId = finAccountTransList.get("paymentTransSequenceId")?if_exists>
		        <fo:page-sequence master-reference="main">		        	
		        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
						<fo:block>
					    <#--
		        		 Table Start -->
		        		<fo:table  table-layout="fixed" width="100%" space-before="0.2in">
		        					    <fo:table-column column-width="100%"/>
        						  		<fo:table-body>
											<fo:table-row>
    										<fo:table-cell>
			                        	       <fo:block text-align="center" white-space-collapse="false" font-weight="bold" font-size="12pt" keep-together="always">Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(paymentDate?if_exists, "MMMM dd,yyyy HH:MM:SS")}&#160;&#160;&#160;&#160;&#160;&#160;UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
    										</fo:table-cell>
			                             </fo:table-row>	
        								<fo:table-row>
		    								<fo:table-cell border-style="solid">
				                                <fo:block text-align="center" white-space-collapse="false" font-weight="bold" font-size="15pt" keep-together="always">&#160;KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD</fo:block>
				                                <fo:block text-align="center" white-space-collapse="false" font-weight="bold" font-size="12pt" keep-together="always">&#160;UNIT: MOTHER DAIRY: G.K.V.K POST,YELAHANKA,BANGALORE:560065</fo:block>
				                             </fo:table-cell>
			                             </fo:table-row>	
				                             <fo:table-row>
				                            	 <fo:table-cell>
				                            	 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	                                		 		<fo:block text-align="center" white-space-collapse="false" font-weight="bold" font-size="13pt" keep-together="always">DEPOSIT <#if finAccountTransTypeId == "WITHDRAWAL">PAID <#else>RECEIPT </#if>VOUCHER</fo:block>
 				                             </fo:table-cell>
 				                             </fo:table-row>	
				                             
        						   	</fo:table-body>
        						   
        						   		<fo:table-body>
        						   		<fo:table-row>
				                            <fo:table-cell>
				                            	 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				                            	 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
 				                             </fo:table-cell>
 				                             </fo:table-row>	
        				                   <fo:table-row width="100%">
        						   				<fo:table-cell>
        						   				<fo:table  table-layout="fixed" width="100%" space-before="0.2in">
		        				    				<fo:table-column column-width="300pt"/>
					        						<fo:table-column column-width="200pt"/>
					        						<fo:table-column column-width="175pt"/>
        						   	        	<fo:table-body>
						        					<fo:table-row>
						        						<fo:table-cell>
						        								<fo:block text-align="left" font-size="13pt" white-space-collapse="false" keep-together = "always" font-weight="bold"><#if finAccountTransTypeId == "WITHDRAWAL">Paid<#else>Received</#if> with thanks </fo:block>
				        						</fo:table-cell> 
						        					</fo:table-row>	
						        					<fo:table-row>
						        						<#if partyName?has_content>
                											<fo:table-cell>
                            									<fo:block  text-align="left"  font-size="13pt" font-weight = "bold"><#if finAccountTransTypeId == "WITHDRAWAL">To : <#else>From : </#if>${partyName?if_exists}</fo:block>  
                       										</fo:table-cell>
                       									<#else>
                       										<fo:table-cell>
                            									<fo:block  text-align="left" font-size="13pt"  font-weight = "bold">&#160;</fo:block>  
                       										</fo:table-cell>
                       									</#if>
						        					</fo:table-row>	
						        					 <fo:table-row>
															<fo:table-cell>
                            									<fo:block  keep-together="always" text-align="left" font-size="13pt" font-weight = "bold">Receipt Number:${newFinAccountTransId?if_exists}&#160;&#160;&#160;</fo:block>  
                       										</fo:table-cell>
														 </fo:table-row>
						        					<fo:table-row>
						        					
						        							<fo:table-cell>
                            									<fo:block  keep-together="always" text-align="left" font-size="13pt" font-weight = "bold">Receipt Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(paymentDate?if_exists, "MMMM dd,yyyy")}</fo:block>  
                       										</fo:table-cell>
						        						
						        					</fo:table-row>	
						        					<fo:table-row>
				                            <fo:table-cell>
				                            	 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				                            	 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
 				                             </fo:table-cell>
 				                             </fo:table-row>
						        					
						        					
						        				  <fo:table-row>
						        						<fo:table-cell>
						        		                    <fo:table  table-layout="fixed" width="100%" space-before="0.2in">
		        				    								 <fo:table-column column-width="20%"/>
						        		                    		 <fo:table-column column-width="20%"/>
        						   									 <fo:table-column column-width="43%"/>
        						   									 <fo:table-column column-width="40%"/>
        						   									 <fo:table-column column-width="27%"/>	
        						   									 <fo:table-body>
        						   									 <fo:table-row>
        						   									 <fo:table-cell border-style="solid">
						        											<fo:block text-align="left" font-size="13pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;SequenceID</fo:block>
						        							  		  </fo:table-cell>
        						   									 <fo:table-cell border-style="solid">
						        											<fo:block text-align="left" font-size="13pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;ReceiptId</fo:block>
						        							  		  </fo:table-cell>
						        							  		  <fo:table-cell border-style="solid">
						        											<fo:block text-align="left" font-size="13pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        											<fo:block text-align="center" font-size="13pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; Description </fo:block>
						        											<fo:block text-align="left" font-size="13pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        							  		  </fo:table-cell>
						        							  		  <fo:table-cell border-style="solid">
						        											<fo:block text-align="left" font-size="13pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        											<fo:block text-align="center" font-size="13pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; Party Code </fo:block>
						        											<fo:block text-align="left" font-size="13pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        							  		  </fo:table-cell>
						        							  		  <fo:table-cell border-style="solid">
						        											<fo:block text-align="left" font-size="13pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        											<fo:block text-align="center" font-size="13pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; AMOUNT Rs. </fo:block>
						        											<fo:block text-align="left" font-size="13pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						        							  		  </fo:table-cell>
        						   									 </fo:table-row>
        						   									 </fo:table-body>
        						   		                    </fo:table>
						        						</fo:table-cell>
						        						</fo:table-row>	
						        					
						        						<fo:table-row>
							        						<fo:table-cell>
						        		                    <fo:table  table-layout="fixed" width="100%" space-before="0.2in">
		        				    								 <fo:table-column column-width="20%"/>
			        		                    					 <fo:table-column column-width="20%"/>
        						   									 <fo:table-column column-width="43%"/>
        						   									 <fo:table-column column-width="40%"/>
        						   									 <fo:table-column column-width="27%"/>	
        						   									 <fo:table-body>
        						   									 <fo:table-row>
        						   									  <fo:table-cell border-style="solid">
						        											<fo:block text-align="center" font-size="13pt" white-space-collapse="false" keep-together="always">${finAccountTransId?if_exists}</fo:block>
						        							  		  </fo:table-cell>
        						   									 <fo:table-cell border-style="solid">
						        											<fo:block text-align="center" font-size="13pt" white-space-collapse="false" keep-together="always">${newFinAccountTransId?if_exists}</fo:block>
						        							  		  </fo:table-cell>
						        							  		  <fo:table-cell border-style="solid">
						        											<fo:block text-align="center" font-size="13pt" white-space-collapse="false">${partyName?if_exists}</fo:block>
						        							  		  </fo:table-cell>
						        							  		  <fo:table-cell border-style="solid">
						        											<fo:block text-align="center" font-size="13pt" white-space-collapse="false">${partyId?if_exists}</fo:block>
						        							  		  </fo:table-cell>
						        							  		  <fo:table-cell border-style="solid">
						        											<fo:block text-align="center" font-size="13pt" white-space-collapse="false" keep-together="always">${amount?if_exists?string("#0.00")}</fo:block>
						        							  		  </fo:table-cell>
        						   									 </fo:table-row>
        						   									 </fo:table-body>
        						   		                    </fo:table>
						        						</fo:table-cell> 
						        						</fo:table-row>	
						        					
						        						 <fo:table-row>
						        						 <fo:table-cell>
						        						 <fo:table  table-layout="fixed" width="100%" space-before="0.2in">
		        				    								 <fo:table-column column-width="60%"/>
			        		                    					 <fo:table-column column-width="70%"/>
			        		                    					 <fo:table-body>
        						   							<fo:table-row>
															<fo:table-cell font-weight = "bold">
											            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
											       			</fo:table-cell>
														 </fo:table-row>
														 
						        						  <fo:table-row>
															<fo:table-cell font-weight = "bold">
                    											<fo:block keep-together="always" font-size="13pt">Deposit Type:</fo:block>
				        									</fo:table-cell>
						   									<fo:table-cell >
               													<fo:block keep-together="always" font-size="13pt">${description}</fo:block>
			        							  		 	</fo:table-cell>
						        						</fo:table-row>
						        						
						        						<fo:table-row>
            				     							<fo:table-cell font-weight = "bold">
                    											<fo:block keep-together="always" font-size="13pt">Cheque in favour/Comments:</fo:block>
               												</fo:table-cell>
               												<fo:table-cell>
               													<fo:block  font-size="13pt" keep-together="always" >${comments?if_exists}</fo:block>
               												</fo:table-cell>
		  												</fo:table-row>
		  												
		  												<fo:table-row>
            				     							<fo:table-cell font-weight = "bold">
                    											<fo:block keep-together="always" font-size="13pt">Instrument No:</fo:block>
               												</fo:table-cell>
               												<fo:table-cell>
               													<fo:block keep-together="always" font-size="13pt">${contraRefNum?if_exists}</fo:block>
               												</fo:table-cell>
		  												</fo:table-row>
		  												
		  												<fo:table-row>
               					 							<fo:table-cell font-weight = "bold">
                    											<fo:block keep-together="always" font-size="13pt">Transferred To/From:</fo:block>
               												</fo:table-cell>
               												<fo:table-cell>
               													<fo:block keep-together="always" font-size="13pt">${finAccountName?if_exists}</fo:block>
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
						        						
						        						<#-- payment details here -->
						        						
						        						<fo:table-row >
			                   								<fo:table-cell>
                   												<fo:block keep-together="always" text-align="left" white-space-collapse="false" font-size="13pt" font-weight="bold">(In Words: ${amountWords} only)</fo:block>
			                   								</fo:table-cell>
						  								</fo:table-row>
						        					
						        						<fo:table-row>
							        						<fo:table-cell>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">PROCD.&#160;&#160;&#160;&#160;&#160;&#160;Dy.Mgr/Mgr/GM(Finance)</fo:block>
							        						</fo:table-cell>
							        							<fo:table-cell>
							        							 	<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160;</fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Pre Audit</fo:block>
							        						</fo:table-cell>
							        						<fo:table-cell>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160;</fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							        								<fo:block text-align="right" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160; Director</fo:block>
							        						</fo:table-cell>  
						        						</fo:table-row>
        						   			</fo:table-body>
        				         </fo:table>
        				</fo:table-cell>
        			</fo:table-row>
        		</fo:table-body>
        	</fo:table>
		        		<#-- Table End -->
		 </fo:block>
		<fo:block >
				<fo:table  table-layout="fixed" width="50%" space-before="0.2in">
			    <fo:table-column column-width="50%"/>
			    <fo:table-column column-width="50%"/>
			    <fo:table-column column-width="40%"/>
				<fo:table-body>
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
						<fo:table-cell>
							<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160;</fo:block>
							<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold"> &#160; </fo:block>
							<fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block>
	</fo:flow>
</fo:page-sequence>
		      
		        </#list>
		        </#if>
		    
     </fo:root>
</#escape>
