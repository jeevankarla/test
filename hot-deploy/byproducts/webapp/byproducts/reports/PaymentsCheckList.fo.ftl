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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="12in"
                     margin-left="1in" margin-right="1in">
                <fo:region-body margin-top="1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "chlst.txt")}
        <#assign totalPaymentAmont = 0>     
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before">
        		<fo:block text-align="left" white-space-collapse="false">${uiLabelMap.CommonPage}:<fo:page-number/>                 MOTHER DAIRY</fo:block>
        		<fo:block text-align="center" keep-together="always" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "MMM d, yyyy HH:mm:ss")}  UserLogin:${userLogin.get("userLoginId")}</fo:block>	 	 	  
        		<fo:block text-align="center" keep-together="always">   Payments CheckList</fo:block>
        	</fo:static-content>
            <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
            	 <fo:block font-family="Courier,monospace"  font-size="12pt">                
                <fo:table border-width="1pt" border-style="dotted">
                    <fo:table-column column-width="85pt"/>
                    <fo:table-column column-width="75pt"/>                
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="45pt"/>
                    <fo:table-column column-width="45pt"/>
                    <fo:table-column column-width="85pt"/>                   
                    <fo:table-header border-width="1pt" border-style="dotted">
		            	<fo:table-cell  border-width="1pt" border-style="dotted"><fo:block  white-space-collapse="false" keep-together="always">SupplyDate      User  Time      Route  Booth     Amount</fo:block></fo:table-cell>		                    	                  		            
		           	</fo:table-header>		           
                    <fo:table-body>        
                    		<fo:table-row><fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell></fo:table-row>
                       <#list checkListReportList as checkListReport>
	                        <fo:table-row border-width="1pt" border-style="dotted">
	                        	<fo:table-cell border-width="1pt" border-style="dotted">	
	                            	<fo:block text-align="left" keep-together="always">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "MMM d, yyyy")}</fo:block>                               
	                            </fo:table-cell>
	                        	<fo:table-cell border-width="1pt" border-style="dotted">	
	                            	<fo:block text-align="right" text-indent="0.05in" padding="1pt">${checkListReport.lastModifiedBy?if_exists}</fo:block>                               
	                            </fo:table-cell>	
	                        	<fo:table-cell border-width="1pt" border-style="dotted">	
	                            	<fo:block text-align="right" text-indent="0.05in" padding="1pt">${checkListReport.lastModifiedDate?if_exists}</fo:block>                               
	                            </fo:table-cell>		                                                    
	                            <fo:table-cell border-width="1pt" border-style="dotted">
	                            	<fo:block text-align="right" padding="1pt">${checkListReport.routeId?if_exists}</fo:block>	                               
	                            </fo:table-cell>	                            
	                            <fo:table-cell border-width="1pt" border-style="dotted">
	                            	<fo:block text-align="right" padding="1pt">${checkListReport.boothId?if_exists}</fo:block>	                               
	                            </fo:table-cell>
	                            <fo:table-cell border-width="1pt" border-style="dotted">
	                            	<#assign totalPaymentAmont =(totalPaymentAmont+checkListReport.amount) >
	                            	<fo:block text-align="right" padding="1pt"><@ofbizCurrency amount=checkListReport.amount isoCode=defaultOrganizationPartyCurrencyUomId/></fo:block>	                               
	                            </fo:table-cell>	  	                            	                            
	                        </fo:table-row>
                       </#list>
                       <fo:table-row border-width="1pt" border-style="dotted">
	                        	<fo:table-cell border-width="1pt" border-style="dotted"/>	                            	                           
	                           
	                        	<fo:table-cell border-width="1pt" border-style="dotted"/>	
	                            	                               
	                            
	                        	<fo:table-cell border-width="1pt" border-style="dotted"/>	
	                            		                                                    
	                            <fo:table-cell border-width="1pt" border-style="dotted">
	                            	<fo:block text-align="right" padding="1pt"> Total : </fo:block>	                               
	                            </fo:table-cell>	                            
	                            <fo:table-cell border-width="1pt" border-style="dotted"/>	                            
	                            <fo:table-cell border-width="1pt" border-style="dotted">	                            	
	                            	<fo:block text-align="right" padding="1pt"><@ofbizCurrency amount=totalPaymentAmont isoCode=defaultOrganizationPartyCurrencyUomId/></fo:block>	                               
	                            </fo:table-cell>	  	                            	                            
	                        </fo:table-row> 
                    </fo:table-body>
                </fo:table>
                 </fo:block>
           </fo:flow>
        </fo:page-sequence>
     </fo:root>
</#escape>