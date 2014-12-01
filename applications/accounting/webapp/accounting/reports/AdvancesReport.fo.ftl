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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
            margin-top="0.2in" margin-bottom=".3in" margin-left=".7in" margin-right=".5in">
        <fo:region-body margin-top="1.4in"/>
        <fo:region-before extent="1.5in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "abstractReport.pdf")}
 <#if partyPaymentsMap?has_content> 
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION  LTD</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">UNIT: MOTHER DAIRY: G.K.V.K POST,YELAHANKA,BANGALORE:560065</fo:block>
                    <fo:block text-align="center" font-size="12pt" keep-together="always"  white-space-collapse="false" font-weight="bold">&#160;&#160; Advances Abstract Report From ${fromDateStr} To ${thruDateStr}	</fo:block>
              		<fo:block>
	                 	<fo:table border-style="solid">
	                    <fo:table-column column-width="80pt"/>
	                    <fo:table-column column-width="200pt"/>
	                    <fo:table-column column-width="100pt"/>  
	               	    <fo:table-column column-width="100pt"/>
	            		<fo:table-column column-width="100pt"/> 		
	            		<fo:table-column column-width="100pt"/>
	            		<fo:table-column column-width="100pt"/>
	            		<fo:table-column column-width="100pt"/>
	                    <fo:table-body>
	                    <fo:table-row >
	                    		<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false" font-weight="bold">SL Code</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false" font-weight="bold">SL Description </fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false" font-weight="bold">Debit </fo:block>
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false" font-weight="bold">Credit</fo:block> 
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">Debit</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">Credit</fo:block>  
                       			</fo:table-cell>
                        		<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">Debit</fo:block>   
                        		</fo:table-cell>
                        		<fo:table-cell border-style="solid">
                            		<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">Credit</fo:block>  
                        		</fo:table-cell>
                			</fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block> 		
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
                 	<fo:table border-style="solid">
                    <fo:table-column column-width="80pt"/>
                    <fo:table-column column-width="200pt"/>
                    <fo:table-column column-width="100pt"/>  
               	    <fo:table-column column-width="100pt"/>
            		<fo:table-column column-width="100pt"/> 		
            		<fo:table-column column-width="100pt"/>
            		<fo:table-column column-width="100pt"/>
            		<fo:table-column column-width="100pt"/>
                    <fo:table-body>
	                	<#assign partyAdvanceDetails = partyPaymentsMap.entrySet()>	
	                	<#list partyAdvanceDetails as partyPayments>
							<#assign partyId = partyPayments.getKey()>
							<#assign paymentDetails = partyPayments.getValue()>
								<fo:table-row border-style="solid">
									<fo:table-cell border-style="solid">
	                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                             ${partyId?if_exists}
                                      </fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell border-style="solid">
	                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                             --
                                      </fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell border-style="solid">
	                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                             ${paymentDetails.get("openingBalance").get("debit")?if_exists}
                                      </fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell border-style="solid">
	                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                             ${paymentDetails.get("openingBalance").get("credit")?if_exists}
                                      </fo:block>  
	                       			</fo:table-cell><fo:table-cell border-style="solid">
	                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                             ${paymentDetails.get("duringPeriod").get("debit")?if_exists}
                                      </fo:block>  
	                       			</fo:table-cell><fo:table-cell border-style="solid">
	                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                             ${paymentDetails.get("duringPeriod").get("credit")?if_exists}
                                      </fo:block>  
	                       			</fo:table-cell><fo:table-cell border-style="solid">
	                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                             ${paymentDetails.get("closingBalance").get("debit")?if_exists}
                                      </fo:block>  
	                       			</fo:table-cell><fo:table-cell border-style="solid">
	                            		<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
                                             ${paymentDetails.get("closingBalance").get("credit")?if_exists}
                                      </fo:block>  
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