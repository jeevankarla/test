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
        ${setRequestAttribute("OUTPUT_FILENAME", "paychlst.txt")}
        <#assign totalPaymentAmont = 0>     
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before">
        		<fo:block text-align="left" white-space-collapse="false">${uiLabelMap.CommonPage}:<fo:page-number/>                 MOTHER DAIRY</fo:block>
        		<fo:block text-align="center" keep-together="always" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "MMM d, yyyy HH:mm:ss")}  UserLogin:${userLogin.get("userLoginId")}</fo:block>	 	 	  
        		<fo:block text-align="center" keep-together="always">   Payments CheckList</fo:block>
        		 <fo:block font-family="Courier,monospace"  >                
                <fo:table >
                       <fo:table-column column-width="30pt"/>
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="60pt"/>                
                    <fo:table-column column-width="120pt"/>
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="65pt"/>
                    <fo:table-column column-width="60pt"/>  
                    <fo:table-body> 
                     <fo:table-row >
	                           <fo:table-cell >	
	                             <fo:block text-align="left" keep-together="always" font-size="7pt" >---------------------------------------------------------------------------------------------------------------</fo:block>
	                            </fo:table-cell>
	                   </fo:table-row>
                     <fo:table-row >
	                           <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always" font-size="7pt">S.No</fo:block>                               
	                            </fo:table-cell>
	                        	<fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always" font-size="7pt">Route</fo:block>    
	                            	<fo:block text-align="left" keep-together="always" font-size="7pt">&#160;Code</fo:block>                            
	                            </fo:table-cell>
	                        	<fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always" font-size="7pt">Retailer</fo:block>    
	                            	<fo:block text-align="left" keep-together="always" font-size="7pt">&#160;Code</fo:block>                                  
	                            </fo:table-cell>	
	                        	<fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always" font-size="7pt">Retailer</fo:block>    
	                            	<fo:block text-align="left" keep-together="always" font-size="7pt">&#160;Name</fo:block>                                
	                            </fo:table-cell>		                                                    
	                            <fo:table-cell >
	                            	<fo:block text-align="left" font-size="7pt">CHEQUE</fo:block>	
	                            	<fo:block text-align="left"  font-size="7pt">&#160; No.</fo:block>	                               
	                            </fo:table-cell>	                            
	                            <fo:table-cell >
	                            	<fo:block text-align="left" font-size="7pt">DATE</fo:block>	                               
	                            </fo:table-cell>
	                            <fo:table-cell >
	                            	<fo:block text-align="right" font-size="7pt">Amount</fo:block>	                               
	                            </fo:table-cell>	
	                        </fo:table-row>
	                         <fo:table-row >
	                           <fo:table-cell >	
	                           <fo:block text-align="left" keep-together="always" font-size="7pt" >---------------------------------------------------------------------------------------------------------------</fo:block>
	                            </fo:table-cell>
	                       </fo:table-row>
                    </fo:table-body>
                   </fo:table>
                  </fo:block>
        	</fo:static-content>
            <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
            	 <fo:block font-family="Courier,monospace"  font-size="7pt">                
                <fo:table >
                      <fo:table-column column-width="30pt"/>
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="60pt"/>                
                    <fo:table-column column-width="120pt"/>
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="65pt"/>
                    <fo:table-column column-width="60pt"/>                
                    <fo:table-body> 
                    <fo:table-row><fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell></fo:table-row>    
	                     <#assign boothPaidCheckList= bankPaidMap.entrySet()>
	                      <#list boothPaidCheckList as bankPaidDetails>
	                       <fo:table-row><fo:table-cell><fo:block >&#160;</fo:block></fo:table-cell></fo:table-row>  
	                         <fo:table-row >
	                        	<fo:table-cell  number-columns-spanned="4">	
	                            	<fo:block text-align="left" keep-together="always"><#if bankPaidDetails.getKey()!="noBankName">${bankPaidDetails.getKey()} </#if></fo:block>                               
	                            </fo:table-cell>
	                          </fo:table-row>
	                            <#assign sno= 1>
	                     <#assign paidCheckList=bankPaidDetails.getValue()>
	                      <#list paidCheckList as checkListReport>
	                         <fo:table-row >
	                            <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always">${sno}</fo:block>                               
	                            </fo:table-cell>
	                             <fo:table-cell >
	                            	<fo:block text-align="left">${boothRouteIdsMap.get(checkListReport.facilityId)?if_exists}</fo:block>	                               
	                            </fo:table-cell>	
	                             <fo:table-cell >
	                            	<fo:block text-align="left">${checkListReport.facilityId?if_exists}</fo:block>	                               
	                            </fo:table-cell>
	                        	<fo:table-cell >	
	                            	<fo:block text-align="left" >${checkListReport.facilityName?if_exists}</fo:block>                               
	                            </fo:table-cell>	
	                        	<fo:table-cell >	
	                            	<fo:block text-align="left" >${checkListReport.paymentRefNum?if_exists}</fo:block>                               
	                            </fo:table-cell>		                                                    
	                            <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(checkListReport.paymentDate, "MMM d, yyyy")}</fo:block>                               
	                            </fo:table-cell>
	                             <fo:table-cell >
	                            	<fo:block text-align="right">${checkListReport.amount?if_exists}</fo:block>	                               
	                            </fo:table-cell>
	                            <#--
	                            <fo:table-cell >
	                            	<#assign totalPaymentAmont =(totalPaymentAmont+checkListReport.amount) >
	                            	<fo:block text-align="right"><@ofbizCurrency amount=checkListReport.amount isoCode=defaultOrganizationPartyCurrencyUomId/></fo:block>	                               
	                            </fo:table-cell> -->
	                             <#assign sno= sno+1>  	  	                            	                            
	                        </fo:table-row>
	                        </#list>
	                      </#list>
                    </fo:table-body>
                </fo:table>
                 </fo:block>
           </fo:flow>
        </fo:page-sequence>
     </fo:root>
</#escape>