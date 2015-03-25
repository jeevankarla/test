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
            <fo:simple-page-master master-name="main" page-width="16.69in" page-height="8.27in"
                margin-top="0.1in" margin-bottom="0.5in" margin-left="0.3in" margin-right="0.3in">
        <fo:region-body margin-top="0.5in" margin-bottom="0.5in"/>
        <fo:region-before extent="0.5in"/>
        <fo:region-after extent="0.5in"/>     
            </fo:simple-page-master>
        </fo:layout-master-set>
        
        <#if glFinalList?has_content>
        <fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">
              <fo:static-content flow-name="xsl-region-before">
              		<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"> &#160;- <fo:page-number/></fo:block>
               </fo:static-content>						
            <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
            			<fo:block text-align="center" white-space-collapse="false"  font-size="12pt" keep-together="always" >&#160;KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD</fo:block>
		       			  <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		                  <fo:block text-align="center" font-size="11pt">Gl account summary for period : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block>
		                  <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		       			  <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		       			   
		                  <fo:block>
		                        <fo:table>
		                        	<fo:table-column column-width="3%"/>
		                            <fo:table-column column-width="8%"/>
		                            <fo:table-column column-width="27%"/>
		                            <fo:table-column column-width="26%"/>
		                            <fo:table-column column-width="26%"/>
		                            <fo:table-body>
		                            	<fo:table-row>
		                            		<fo:table-cell border-style="solid">
		                            			<fo:block text-align="left" font-size="11pt">&#160;</fo:block>
		                            			<fo:block text-align="left" font-size="11pt">&#160;</fo:block>
			                                    <fo:block text-align="left" font-size="11pt">S.NO</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
			                                	<fo:block text-align="left" font-size="11pt">&#160;</fo:block>
		                            			<fo:block text-align="left" font-size="11pt">&#160;</fo:block>
			                                    <fo:block text-align="left" font-size="11pt">GL ACCOUNT ID</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
			                                	<fo:block text-align="left" font-size="11pt">&#160;</fo:block>
		                            			<fo:block text-align="left" font-size="11pt">&#160;</fo:block>
			                                    <fo:block text-align="left" font-size="11pt">DESCRIPTION</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
			                                	<fo:block text-align="center" font-size="11pt">DEBIT</fo:block>
			                                	<fo:block>
						                        <fo:table>
						                        	<fo:table-column column-width="50%"/>
						                            <fo:table-column column-width="50%"/>
			                                		<fo:table-body>
						                            	<fo:table-row>
						                            		<fo:table-cell border-style="solid">
							                                    <fo:block text-align="center" font-size="11pt">POSTED</fo:block>
							                                    <fo:block>
											                        <fo:table>
											                        	<fo:table-column column-width="50%"/>
											                            <fo:table-column column-width="50%"/>
								                                		<fo:table-body>
											                            	<fo:table-row>
							                                    				<fo:table-cell border-style="solid" border-bottom-style="hidden">
												                                    <fo:block text-align="center" font-size="11pt">INVOICE</fo:block>
												                                </fo:table-cell>
												                                <fo:table-cell border-style="solid" border-bottom-style="hidden">
												                                    <fo:block text-align="center" font-size="11pt">JV AMOUNT</fo:block>
												                                </fo:table-cell>
							                                    			 </fo:table-row>
												                  		</fo:table-body>
												                  	</fo:table>	
												                  </fo:block>	
							                                </fo:table-cell>
							                                <fo:table-cell border-style="solid">
							                                    <fo:block text-align="center" font-size="11pt">UNPOSTED</fo:block>
							                                    <fo:block>
											                        <fo:table>
											                        	<fo:table-column column-width="50%"/>
											                            <fo:table-column column-width="50%"/>
								                                		<fo:table-body>
											                            	<fo:table-row>
							                                    				<fo:table-cell border-style="solid" border-bottom-style="hidden">
												                                    <fo:block text-align="center" font-size="11pt">INVOICE</fo:block>
												                                </fo:table-cell>
												                                <fo:table-cell border-style="solid" border-bottom-style="hidden">
												                                    <fo:block text-align="center" font-size="11pt">JV AMOUNT</fo:block>
												                                </fo:table-cell>
							                                    			 </fo:table-row>
												                  		</fo:table-body>
												                  	</fo:table>	
												                  </fo:block>
							                                </fo:table-cell>
			                                			 </fo:table-row>
							                  		</fo:table-body>
							                  	</fo:table>	
							                  </fo:block>	
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
			                                	<fo:block text-align="center" font-size="11pt">CREDIT</fo:block>
			                                	<fo:block>
						                        <fo:table>
						                        	<fo:table-column column-width="50%"/>
						                            <fo:table-column column-width="50%"/>
			                                		<fo:table-body>
						                            	<fo:table-row>
						                            		<fo:table-cell border-style="solid">
							                                    <fo:block text-align="center" font-size="11pt">POSTED</fo:block>
							                                    <fo:block>
											                        <fo:table>
											                        	<fo:table-column column-width="50%"/>
											                            <fo:table-column column-width="50%"/>
								                                		<fo:table-body>
											                            	<fo:table-row>
							                                    				<fo:table-cell border-style="solid" border-bottom-style="hidden">
												                                    <fo:block text-align="center" font-size="11pt">INVOICE</fo:block>
												                                </fo:table-cell>
												                                <fo:table-cell border-style="solid" border-bottom-style="hidden">
												                                    <fo:block text-align="center" font-size="11pt">JV AMOUNT</fo:block>
												                                </fo:table-cell>
							                                    			 </fo:table-row>
												                  		</fo:table-body>
												                  	</fo:table>	
												                  </fo:block>	
							                                </fo:table-cell>
							                                <fo:table-cell border-style="solid">
							                                    <fo:block text-align="center" font-size="11pt">UNPOSTED</fo:block>
							                                    <fo:block>
											                        <fo:table>
											                        	<fo:table-column column-width="50%"/>
											                            <fo:table-column column-width="50%"/>
								                                		<fo:table-body>
											                            	<fo:table-row>
							                                    				<fo:table-cell border-style="solid" border-bottom-style="hidden">
												                                    <fo:block text-align="center" font-size="11pt">INVOICE</fo:block>
												                                </fo:table-cell>
												                                <fo:table-cell border-style="solid" border-bottom-style="hidden">
												                                    <fo:block text-align="center" font-size="11pt">JV AMOUNT</fo:block>
												                                </fo:table-cell>
							                                    			 </fo:table-row>
												                  		</fo:table-body>
												                  	</fo:table>	
												                  </fo:block>
							                                </fo:table-cell>
			                                			 </fo:table-row>
							                  		</fo:table-body>
							                  	</fo:table>	
							                  </fo:block>	
			                                </fo:table-cell>
		                                </fo:table-row>
		                                
		                                <#assign srn = 0>
		                                <#list glFinalList as eachGlFinal>
		                                <#assign srn = srn + 1>
		                                
		                                <#if (eachGlFinal.get("postedDrAmount")?has_content) || (eachGlFinal.get("JpostedDrAmount")?has_content) || (eachGlFinal.get("unPostedDrAmount")?has_content) || (eachGlFinal.get("JunPostedCrAmount")?has_content) || (eachGlFinal.get("postedDrAmount")?has_content) || (eachGlFinal.get("JpostedCrAmount")?has_content) || (eachGlFinal.get("unPostedCrAmount")?has_content) || (eachGlFinal.get("JunPostedCrAmount")?has_content)>
			                                
		                                <fo:table-row>
		                            		<fo:table-cell border-style="solid">
			                                    <fo:block text-align="left" font-size="11pt">${srn}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
			                                    <fo:block text-align="left" font-size="11pt">${eachGlFinal.get("glAccountId")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
			                                    <fo:block text-align="left" font-size="11pt" white-space-collapse="false">${eachGlFinal.get("glAccountIdDes")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid" border-left-style="hidden">
			                                	<fo:block>
						                        <fo:table>
						                        	<fo:table-column column-width="50%"/>
						                            <fo:table-column column-width="50%"/>
			                                		<fo:table-body>
						                            	<fo:table-row>
						                            		<fo:table-cell border-style="solid" border-bottom-style="hidden" border-top-style="hidden" border-left-style="hidden">
							                                    <fo:block>
											                        <fo:table>
											                        	<fo:table-column column-width="50%"/>
											                            <fo:table-column column-width="50%"/>
								                                		<fo:table-body>
											                            	<fo:table-row>
							                                    				<fo:table-cell border-style="solid" border-bottom-style="hidden" border-top-style="hidden">
												                                    <fo:block text-align="right" font-size="11pt">${eachGlFinal.get("postedDrAmount")?if_exists}</fo:block>
												                                </fo:table-cell>
												                                <fo:table-cell border-style="solid" border-bottom-style="hidden" border-top-style="hidden">
												                                    <fo:block text-align="right" font-size="11pt">${eachGlFinal.get("JpostedDrAmount")?if_exists}</fo:block>
												                                </fo:table-cell>
							                                    			 </fo:table-row>
												                  		</fo:table-body>
												                  	</fo:table>	
												                  </fo:block>	
							                                </fo:table-cell>
							                                <fo:table-cell border-style="solid" border-bottom-style="hidden" border-top-style="hidden" border-left-style="hidden">
							                                    <fo:block>
											                        <fo:table>
											                        	<fo:table-column column-width="50%"/>
											                            <fo:table-column column-width="50%"/>
								                                		<fo:table-body>
											                            	<fo:table-row>
							                                    				<fo:table-cell border-style="solid" border-bottom-style="hidden" border-top-style="hidden">
												                                    <fo:block text-align="right" font-size="11pt">${eachGlFinal.get("unPostedDrAmount")?if_exists}</fo:block>
												                                </fo:table-cell>
												                                <fo:table-cell border-style="solid" border-bottom-style="hidden" border-top-style="hidden">
												                                    <fo:block text-align="right" font-size="11pt">${eachGlFinal.get("JunPostedDrAmount")?if_exists}</fo:block>
												                                </fo:table-cell>
							                                    			 </fo:table-row>
												                  		</fo:table-body>
												                  	</fo:table>	
												                  </fo:block>
							                                </fo:table-cell>
			                                			 </fo:table-row>
							                  		</fo:table-body>
							                  	</fo:table>	
							                  </fo:block>	
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid" border-left-style="hidden">
			                                	<fo:block>
						                        <fo:table>
						                        	<fo:table-column column-width="50%"/>
						                            <fo:table-column column-width="50%"/>
			                                		<fo:table-body>
						                            	<fo:table-row>
						                            		<fo:table-cell border-style="solid" border-bottom-style="hidden" border-top-style="hidden" border-left-style="hidden">
							                                    <fo:block>
											                        <fo:table>
											                        	<fo:table-column column-width="50%"/>
											                            <fo:table-column column-width="50%"/>
								                                		<fo:table-body>
											                            	<fo:table-row>
							                                    				<fo:table-cell border-style="solid" border-bottom-style="hidden" border-top-style="hidden">
												                                    <fo:block text-align="right" font-size="11pt">${eachGlFinal.get("postedCrAmount")?if_exists}</fo:block>
												                                </fo:table-cell>
												                                <fo:table-cell border-style="solid" border-bottom-style="hidden" border-top-style="hidden">
												                                    <fo:block text-align="right" font-size="11pt">${eachGlFinal.get("JpostedCrAmount")?if_exists}</fo:block>
												                                </fo:table-cell>
							                                    			 </fo:table-row>
												                  		</fo:table-body>
												                  	</fo:table>	
												                  </fo:block>	
							                                </fo:table-cell>
							                                <fo:table-cell border-style="solid" border-bottom-style="hidden" border-top-style="hidden" border-left-style="hidden">
							                                    <fo:block>
											                        <fo:table>
											                        	<fo:table-column column-width="50%"/>
											                            <fo:table-column column-width="50%"/>
								                                		<fo:table-body>
											                            	<fo:table-row>
							                                    				<fo:table-cell border-style="solid" border-bottom-style="hidden" border-top-style="hidden">
												                                    <fo:block text-align="right" font-size="11pt">${eachGlFinal.get("unPostedCrAmount")?if_exists}</fo:block>
												                                </fo:table-cell>
												                                <fo:table-cell border-style="solid" border-bottom-style="hidden" border-top-style="hidden">
												                                    <fo:block text-align="right" font-size="11pt">${eachGlFinal.get("JunPostedCrAmount")?if_exists}</fo:block>
												                                </fo:table-cell>
							                                    			 </fo:table-row>
													                            	
												                  		</fo:table-body>
												                  	</fo:table>	
												                  </fo:block>
							                                </fo:table-cell>
			                                			 </fo:table-row>
							                  		</fo:table-body>
							                  	</fo:table>	
							                  </fo:block>	
			                                </fo:table-cell>
		                                </fo:table-row>
		                              </#if>
		                           </#list>  
                  		</fo:table-body>
                  	</fo:table>	
                  </fo:block>
                  
                  <fo:block break-before="page"/> 
	   			  <fo:block text-align="center" font-size="11pt">INVOICE SUMMARY REPORT</fo:block>
                  <fo:block>
                        <fo:table>
                            <fo:table-column column-width="10%"/>
                            <fo:table-column column-width="8%"/>
                            <fo:table-column column-width="8%"/>
                            <fo:table-column column-width="16%"/>
                            <fo:table-column column-width="15%"/>
                            <fo:table-column column-width="8%"/>
                            <fo:table-column column-width="9%"/>
                            <fo:table-body>
                                
                                <#assign flag = "Y">
                                <#list invoiceFinalList as eachInvoiceList>
                                		<#if eachInvoiceList.get("GlAccountId") == "TOTAL">
                               			<fo:table-row>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">TOTAL</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">&#160;</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">&#160;</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt">${eachInvoiceList.get("postedDrAmount")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt">${eachInvoiceList.get("unPostedDrAmount")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt">${eachInvoiceList.get("postedCrAmount")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt">${eachInvoiceList.get("unPostedCrAmount")?if_exists}</fo:block>
			                                </fo:table-cell>
		                                </fo:table-row>
		                                <fo:table-row>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">--------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-----------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-----------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">---------------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">--------------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">--------------------------------------</fo:block>
			                                </fo:table-cell>
		                                 </fo:table-row>
                               			
                                		<#elseif eachInvoiceList.get("GlAccountId") == "SUBTOTAL">
                                		<#assign flag = "Y">
                                		<#if (eachInvoiceList.get("postedDrAmount") != 0 )||  (eachInvoiceList.get("unPostedDrAmount") != 0) || (eachInvoiceList.get("postedCrAmount") != 0) || (eachInvoiceList.get("unPostedCrAmount") != 0)>
	                                		<fo:table-row>
				                                <fo:table-cell >
				                                    <fo:block text-align="left" font-size="11pt">--------------------------------</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="left" font-size="11pt">-----------------------------------------</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="left" font-size="11pt">-----------------------------------------</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="left" font-size="11pt">---------------------------------------------------</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="left" font-size="11pt">--------------------------------------------------</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="left" font-size="11pt">-------------------------------------</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="left" font-size="11pt">--------------------------------------</fo:block>
				                                </fo:table-cell>
			                                 </fo:table-row>
			                                <fo:table-row>
				                                <fo:table-cell >
				                                    <fo:block text-align="left" font-size="11pt">SUBTOTAL</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="left" font-size="11pt">&#160;</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="left" font-size="11pt">&#160;</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="right" font-size="11pt">${eachInvoiceList.get("postedDrAmount")?if_exists}</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="right" font-size="11pt">${eachInvoiceList.get("unPostedDrAmount")?if_exists}</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="right" font-size="11pt">${eachInvoiceList.get("postedCrAmount")?if_exists}</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="right" font-size="11pt">${eachInvoiceList.get("unPostedCrAmount")?if_exists}</fo:block>
				                                </fo:table-cell>
			                                </fo:table-row>
			                                <fo:table-row>
				                                <fo:table-cell >
				                                    <fo:block text-align="left" font-size="11pt">--------------------------------</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="left" font-size="11pt">-----------------------------------------</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="left" font-size="11pt">-----------------------------------------</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="left" font-size="11pt">---------------------------------------------------</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="left" font-size="11pt">--------------------------------------------------</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="left" font-size="11pt">-------------------------------------</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="left" font-size="11pt">--------------------------------------</fo:block>
				                                </fo:table-cell>
			                                 </fo:table-row>
		                                 </#if>
                                		<#else>
                                			<#if flag == "Y">
                                			<#assign flag = "N">
                                			<fo:table-row>
				                                <fo:table-cell >
				                                	<fo:block text-align="left" font-size="11pt">&#160;</fo:block>
				                                	<fo:block text-align="left" font-size="11pt">&#160;</fo:block>
				                                    <fo:block text-align="center" font-size="11pt">Gl AccountId:</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                	<fo:block text-align="left" font-size="11pt">&#160;</fo:block>
				                                	<fo:block text-align="left" font-size="11pt">&#160;</fo:block>
				                                    <fo:block text-align="left" font-size="11pt">${eachInvoiceList.get("GlAccountId")?if_exists}</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                	<fo:block text-align="left" font-size="11pt">&#160;</fo:block>
				                                	<fo:block text-align="left" font-size="11pt">&#160;</fo:block>
				                                    <fo:block text-align="left" font-size="11pt">&#160;</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="left" font-size="11pt">&#160;</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="left" font-size="11pt">&#160;</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="left" font-size="11pt">&#160;</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="left" font-size="11pt">&#160;</fo:block>
				                                </fo:table-cell>
			                                </fo:table-row>
                                			<fo:table-row>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">--------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-----------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-----------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">---------------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">--------------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">--------------------------------------</fo:block>
			                                </fo:table-cell>
		                                 </fo:table-row>
		                                	<fo:table-row>
				                                <fo:table-cell >
				                                    <fo:block text-align="left" font-size="11pt">TransactionDate</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="left" font-size="11pt">InvoiceId</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="left" font-size="11pt">AccountTransId</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="right" font-size="11pt">PostedDr</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="right" font-size="11pt">UnpostedDr</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="right" font-size="11pt">PostedCr</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="right" font-size="11pt">UnpotsedCr</fo:block>
				                                </fo:table-cell>
			                                </fo:table-row>
			                                <fo:table-row>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">--------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-----------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-----------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">---------------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">--------------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">--------------------------------------</fo:block>
			                                </fo:table-cell>
		                                 </fo:table-row>
			                                <fo:table-row>
				                                <fo:table-cell >
				                                    <fo:block text-align="left" font-size="11pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(eachInvoiceList.get("InvoiceDate"), "dd/MM/yyyy")}</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="left" font-size="11pt">${eachInvoiceList.get("InvoiceId")?if_exists}</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="left" font-size="11pt">${eachInvoiceList.get("accountTransId")?if_exists}</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="right" font-size="11pt">${eachInvoiceList.get("postedDrAmount")?if_exists}</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="right" font-size="11pt">${eachInvoiceList.get("unPostedDrAmount")?if_exists}</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="right" font-size="11pt">${eachInvoiceList.get("postedCrAmount")?if_exists}</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="right" font-size="11pt">${eachInvoiceList.get("unPostedCrAmount")?if_exists}</fo:block>
				                                </fo:table-cell>
			                                </fo:table-row>
		                                <#else>
		                                	<#assign flag = "N">
	                                		<fo:table-row>
				                                <fo:table-cell >
				                                    <fo:block text-align="left" font-size="11pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(eachInvoiceList.get("InvoiceDate"), "dd/MM/yyyy")}</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="left" font-size="11pt">${eachInvoiceList.get("InvoiceId")?if_exists}</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="left" font-size="11pt">${eachInvoiceList.get("accountTransId")?if_exists}</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="right" font-size="11pt">${eachInvoiceList.get("postedDrAmount")?if_exists}</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="right" font-size="11pt">${eachInvoiceList.get("unPostedDrAmount")?if_exists}</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="right" font-size="11pt">${eachInvoiceList.get("postedCrAmount")?if_exists}</fo:block>
				                                </fo:table-cell>
				                                <fo:table-cell >
				                                    <fo:block text-align="right" font-size="11pt">${eachInvoiceList.get("unPostedCrAmount")?if_exists}</fo:block>
				                                </fo:table-cell>
			                                </fo:table-row>
			                           </#if>
                               </#if>
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
	            	No Records Found For The Given Duration!
	       		 </fo:block>
		</fo:flow>
	</fo:page-sequence>	
    </#if>  
    </fo:root>
</#escape>
