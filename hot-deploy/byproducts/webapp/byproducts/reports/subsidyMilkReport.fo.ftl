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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="8in"  margin-left=".3in" margin-right=".3in" margin-bottom=".3in" margin-top=".3in">
        <fo:region-body margin-top="0.2in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "subsidyMilkReport.pdf")}
<#assign lineNumber = 5>
<#assign numberOfLines = 56>
 <#if unionFacilityPartyMap?has_content> 
 <#assign unionFacilityList = unionFacilityPartyMap.entrySet()>
  <#list unionFacilityList as unionFacility>
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
			    <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"> &#160;${uiLabelMap.CommonPage}- <fo:page-number/></fo:block>
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
					<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false">&#160;      Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
					<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false">    UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">${uiLabelMap.KMFDairySubHeader}</fo:block>
                    <fo:block  text-align="left"  font-size="9pt" keep-together="always"  white-space-collapse="false" font-weight="bold">LIST OF ${unionFacility.getKey()} EMPLOYEES GETTING SUBSIDISED MILK FROM MOTHER DAIRY From :: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayBegin, "dd/MM/yyyy")} TO: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayEnd, "dd/MM/yyyy")}</fo:block>
              		<fo:block font-size="10pt">-----------------------------------------------------------------------------------------</fo:block>
              		<fo:block font-weight="bold" font-size="10pt">Route 			          &#160; &#160;&#160;&#160;  Code No   	 &#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Name of the Agent           &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Employee Name</fo:block>
            		<fo:block font-size="10pt">-----------------------------------------------------------------------------------------</fo:block>
            	<fo:block>
                    <fo:table>
				    <fo:table-column column-width="15%"/>
			        <fo:table-column column-width="25%"/>
			        <fo:table-column column-width="35%"/>
			        <fo:table-column column-width="25%"/>
			        <fo:table-column column-width="18%"/>
                    <fo:table-body>
                    	<fo:table-row>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, unionFacility.getKey(), false)}</fo:block>  
                			</fo:table-cell>
                		</fo:table-row>
                		<fo:table-row>
                			<fo:table-cell>
                    			<fo:block font-size="10pt">-----------------------------------------------------------------------------------------</fo:block>
                			</fo:table-cell>
                    	</fo:table-row>	
                		 <#assign totalSize = 0>
                		<#assign routeValues = unionFacility.getValue().entrySet()>
                    		<#list routeValues as facilityRoute>
                    	 		<#assign facilityDetails = facilityRoute.getValue().entrySet()>
				                	 <#list facilityDetails as eachFacility>
				                	   <#assign partyDetails = eachFacility.getValue()>
					                	   <#list partyDetails as eachParty>
					                		<fo:table-row>	
					                			<fo:table-cell>
					                    			<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false">${facilityRoute.getKey()}</fo:block>  
					                			</fo:table-cell>
					                			<fo:table-cell>
					                    			<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false">${eachFacility.getKey()}</fo:block>  
					                			</fo:table-cell>
					                			<fo:table-cell>
					                    			<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false">${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, eachFacility.getKey(), false)}</fo:block>  
					                			</fo:table-cell>
					                			<fo:table-cell>
					                    			<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false">${eachParty.partyName?if_exists}</fo:block>  
					                			</fo:table-cell>
					                			<#assign totalSize  = totalSize + 1>
					                    	</fo:table-row>
	                    					</#list>
                    				</#list>
                    		</#list>
                    			<#assign lineNumber = lineNumber + 1>
	                   			<#if (lineNumber >= numberOfLines)>
                    			<#assign lineNumber = 5 >
                    			<fo:table-row>
                   	     			<fo:table-cell>
	    	                        	<fo:block font-size="7pt" page-break-after="always"></fo:block>        
			                        </fo:table-cell>
	        		            </fo:table-row>
                    			</#if>
                    			<fo:table-row>
		                			<fo:table-cell>
		                    			<fo:block font-size="10pt">-----------------------------------------------------------------------------------------</fo:block>
		                			</fo:table-cell>
		                    	</fo:table-row>
		                    	<fo:table-row>
		                			<fo:table-cell>
		                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">No of Employees              =</fo:block>  
		                			</fo:table-cell>
		                			<fo:table-cell>
		                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
		                			</fo:table-cell>
		                			<fo:table-cell>
		                    			<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">&#160;&#160;${totalSize}</fo:block>  
		                			</fo:table-cell>
		                		</fo:table-row>
		                		<fo:table-row>
		                			<fo:table-cell>
		                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">Litres of Milk Supplied      =</fo:block>  
		                			</fo:table-cell>
		                			<fo:table-cell>
		                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
		                			</fo:table-cell>
		                			<#assign milkLtrs = (totalSize*totalDays)>
		                			<fo:table-cell>
		                    			<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">&#160;&#160;${milkLtrs}</fo:block>  
		                			</fo:table-cell>
		                		</fo:table-row>	
		                		<fo:table-row>
		                			<fo:table-cell>
		                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">Amount of Debit Advice       =</fo:block>  
		                			</fo:table-cell>
		                			<fo:table-cell>
		                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
		                			</fo:table-cell>
		                			<#assign costPerLtr = (milkLtrs*costPerLitre?if_exists)>
		                			<#if costPerLtr?has_content>
		                			<fo:table-cell>
		                    			<fo:block  keep-together="always" text-align="left"  font-size="12pt" white-space-collapse="false">&#160;&#160;${costPerLtr?if_exists?string("#0.00")}</fo:block>  
		                			</fo:table-cell>
		                			<#else>
		                			<fo:table-cell>
		                    			<fo:block  keep-together="always" text-align="left"  font-size="12pt" white-space-collapse="false">&#160;&#160;${milkLtrs?if_exists?string("#0.00")}</fo:block>  
		                			</fo:table-cell>
		                			</#if>
		                		</fo:table-row>
		                		<fo:table-row>
		                			<fo:table-cell>
		                    			<fo:block font-size="10pt">-----------------------------------------------------------------------------------------</fo:block>
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
					            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
					       			</fo:table-cell>
				  				</fo:table-row>
                        		<fo:table-row>
					       			<fo:table-cell>
					            		<fo:block  keep-together="always" font-size="11pt" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Authorised Signatory</fo:block>  
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