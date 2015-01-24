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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
                     margin-left="1in" margin-right="1in">
                <fo:region-body margin-top="1.3in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "paychlst.txt")}
        <#assign totalPaymentAmont = 0>  
        <fo:page-sequence master-reference="main" font-family="Courier,monospace" font-weight="13pt" >
        	<fo:static-content flow-name="xsl-region-before">
        	<#-- 
        		<fo:block text-align="center" keep-together="always">VST_ASCII-015</fo:block> -->
        		<fo:block text-align="left" font-size="10pt" keep-together="always">${uiLabelMap.CommonPage}:<fo:page-number/></fo:block>
        		<fo:block text-align="center"   white-space-collapse="false"> MOTHER DAIRY, KMF UNIT</fo:block>
        		<fo:block text-align="center" keep-together="always" font-size="11pt" white-space-collapse="false"> UserLogin:${userLogin.get("userLoginId")} ,Time: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "MMM d, yyyy HH:mm:ss")} </fo:block>	 	 	  
        		<fo:block text-align="center" keep-together="always" font-size="11pt" > Cash Payment Group CheckList Report For Cash On Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(paymentDate, "dd-MM-yyyy")}</fo:block>
        		 <fo:block font-family="Courier,monospace"  >                
                <fo:table >
                      <fo:table-column column-width="35pt"/>
                    <fo:table-column column-width="65pt"/>
                     <fo:table-column column-width="85pt"/>
                    <fo:table-column column-width="85pt"/>                
                    <fo:table-column column-width="180pt"/>
                    <fo:table-column column-width="90pt"/>   
                    <fo:table-body> 
                     <fo:table-row >
	                           <fo:table-cell >	
	                             <fo:block text-align="left" keep-together="always" font-size="7pt" >---------------------------------------------------------------------------------------------------------------------------------</fo:block>
	                            </fo:table-cell>
	                   </fo:table-row>
                     <fo:table-row >
	                           <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always" >S.No</fo:block>                               
	                            </fo:table-cell>
	                             <fo:table-cell >	
	                             <fo:block text-align="left" keep-together="always" >Payment</fo:block>    
	                            	<fo:block text-align="left" keep-together="always" >&#160;Id</fo:block>
	                            </fo:table-cell>
	                        	<fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always" >Route</fo:block>    
	                            	<fo:block text-align="left" keep-together="always" >&#160;Code</fo:block>                            
	                            </fo:table-cell>
	                        	<fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always" >Retailer/Party</fo:block>    
	                            	<fo:block text-align="left" keep-together="always" >&#160;&#160;&#160;&#160;Code</fo:block>                                  
	                            </fo:table-cell>	
	                        	<fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always" >&#160;&#160;&#160;&#160;Retailer/Party</fo:block>    
	                            	<fo:block text-align="left" keep-together="always" >&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Name</fo:block>                                
	                            </fo:table-cell>
	                            <fo:table-cell >
	                            	<fo:block text-align="right" >Amount</fo:block>	                               
	                            </fo:table-cell>	
	                        </fo:table-row>
	                         <fo:table-row >
	                           <fo:table-cell >	
	                            <fo:block text-align="left" keep-together="always" font-size="7pt" >---------------------------------------------------------------------------------------------------------------------------------</fo:block>
	                            </fo:table-cell>
	                       </fo:table-row>
                    </fo:table-body>
                   </fo:table>
                  </fo:block>
        	</fo:static-content>
            <fo:flow flow-name="xsl-region-body" >
            	 <fo:block font-family="Courier,monospace"  >                
                <fo:table >
                        <fo:table-column column-width="35pt"/>
                    <fo:table-column column-width="65pt"/>
                     <fo:table-column column-width="85pt"/>
                    <fo:table-column column-width="85pt"/>                
                    <fo:table-column column-width="180pt"/>
                    <fo:table-column column-width="90pt"/>             
                    <fo:table-body> 
                    <fo:table-row><fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell></fo:table-row>    
	                     
	                       <fo:table-row><fo:table-cell><fo:block >&#160;</fo:block></fo:table-cell></fo:table-row>  
	                         <fo:table-row >
	                        	<fo:table-cell  number-columns-spanned="4">	
	                            	<fo:block text-align="left" keep-together="always">**************RouteMarketing Payments****************</fo:block>                               
	                            </fo:table-cell>
	                          </fo:table-row>
	                            <#assign sno= 1>
			                     <#assign routeMktgTotal=0>
			                    <#if nonGroupPaymentsList?has_content>
	                      	     <#list nonGroupPaymentsList as checkListReport>
	                         <fo:table-row >
	                            <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always">${sno}</fo:block>                               
	                            </fo:table-cell>
	                             <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always">${checkListReport.paymentId}</fo:block>                               
	                            </fo:table-cell>
	                             <fo:table-cell >
	                             <#if checkListReport.facilityId?exists>
	                            	<fo:block text-align="left">${boothRouteIdsMap.get(checkListReport.facilityId)?if_exists}</fo:block>	
	                            	</#if>                               
	                            </fo:table-cell>	
	                             <fo:table-cell >
	                             	<#if (checkListReport.facilityId)?exists>
	                            	<fo:block text-align="left">${checkListReport.facilityId?if_exists}</fo:block>	
	                            	<#else>
	                            	<fo:block text-align="left">${checkListReport.partyIdFrom?if_exists}</fo:block>	
	                            	</#if>                                 
	                            </fo:table-cell>
	                        	<fo:table-cell >	
                            	<#assign  partyName="">
		            			<#if (checkListReport.partyIdFrom)?exists>
		            			<#assign partyId=checkListReport.partyIdFrom>
		            			<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)>
		            			</#if>     
		            			<fo:block text-align="left"  >${partyName?if_exists}</fo:block>                          
	                            </fo:table-cell>	
	                             <#assign routeMktgTotal=routeMktgTotal+checkListReport.amount>
	                            <fo:table-cell >
	                            	<#assign totalPaymentAmont =(totalPaymentAmont+checkListReport.amount) >
	                            	<fo:block text-align="right">${checkListReport.amount?if_exists?string("#0.00")}</fo:block>	                               
	                            </fo:table-cell> 
	                             <#assign sno= sno+1>
	                        </fo:table-row>
	                        </#list>
	                          </#if>
	                             <#if paymentGrpMap?has_content>
	                              
	                            <#assign groupPaymentsList= paymentGrpMap.entrySet()>
	                               <fo:table-row >
	                        	<fo:table-cell  number-columns-spanned="4">	
	                            	<fo:block text-align="left" keep-together="always">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;*Group/Transporter Payments*&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>                               
	                            </fo:table-cell>
	                          </fo:table-row>
	                      	     <#list groupPaymentsList as groupPayment>
	                      	     <#assign groupPaymentInfo=groupPayment.getValue()>
	                         <fo:table-row >
	                            <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always">${sno}</fo:block>                               
	                            </fo:table-cell>
	                             <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always">${groupPayment.getKey()}</fo:block>                               
	                            </fo:table-cell>
	                             <fo:table-cell >
	                             <#if groupPaymentInfo.routeId?exists>
	                            	<fo:block text-align="left">${(groupPaymentInfo.routeId)?if_exists}</fo:block>	
	                            	</#if>                               
	                            </fo:table-cell>	
	                             <fo:table-cell >
	                             	<#if (groupPaymentInfo.partyIdFrom)?exists>
	                            	<fo:block text-align="left">${groupPaymentInfo.partyIdFrom?if_exists}</fo:block>	
	                            	</#if>                                 
	                            </fo:table-cell>
	                        	<fo:table-cell >	
                            	<#assign  partyName="">
		            			<#if (groupPaymentInfo.partyIdFrom)?exists>
		            			<#assign partyId=groupPaymentInfo.partyIdFrom>
		            			<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)>
		            			</#if>     
		            			<fo:block text-align="left"  >${partyName?if_exists}</fo:block>                          
	                            </fo:table-cell>	
	                             <#assign routeMktgTotal=routeMktgTotal+groupPaymentInfo.amount>
	                            <fo:table-cell >
	                            	<#assign totalPaymentAmont =(totalPaymentAmont+groupPaymentInfo.amount) >
	                            	<fo:block text-align="right">${groupPaymentInfo.amount?if_exists?string("#0.00")}</fo:block>	                               
	                            </fo:table-cell> 
	                             <#assign sno= sno+1>
	                        </fo:table-row>
	                        </#list>
	                          </#if>
	                        <fo:table-row >
	                            <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always">&#160;</fo:block>                               
	                            </fo:table-cell>
	                        </fo:table-row>
	                       <fo:table-row >
	                            <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always"></fo:block>                               
	                            </fo:table-cell>
	                             <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always"></fo:block>                               
	                            </fo:table-cell>
	                              <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always"></fo:block>                               
	                            </fo:table-cell>
	                        	<fo:table-cell  number-columns-spanned="2">	
	                            	<fo:block text-align="left" >RouteMktgTotal:</fo:block>                             
	                            </fo:table-cell>	
	                            <fo:table-cell >
	                            	<fo:block text-align="right" padding="1pt"><@ofbizCurrency amount=routeMktgTotal isoCode=defaultOrganizationPartyCurrencyUomId/></fo:block>	                               
	                            </fo:table-cell> 
	                        </fo:table-row>
	                       
	                        <#-- Non RouteMktg starts here-->
	                         <#if nonRouteCheckListReportList?has_content>
	                       <fo:table-row><fo:table-cell><fo:block >&#160;</fo:block></fo:table-cell></fo:table-row>  
	                         <fo:table-row >
	                        	<fo:table-cell  number-columns-spanned="4">	
	                            	<fo:block text-align="left" keep-together="always">**************NonRouteMarketing Payments******************</fo:block>                               
	                            </fo:table-cell>
	                          </fo:table-row>
	                            <#assign sno= 1>
			                     <#assign nonRouteMktgTotal=0>
	                      		 <#list nonRouteCheckListReportList as checkListReport>
	                         <fo:table-row >
	                            <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always">${sno}</fo:block>                               
	                            </fo:table-cell>
	                              <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always">${checkListReport.paymentId}</fo:block>                               
	                            </fo:table-cell>
	                             <fo:table-cell >
	                             <#if checkListReport.facilityId?exists>
	                            	<fo:block text-align="left">${boothRouteIdsMap.get(checkListReport.facilityId)?if_exists}</fo:block>	
	                            	</#if>                               
	                            </fo:table-cell>	
	                             <fo:table-cell >
	                             	<#if (checkListReport.facilityId)?exists>
	                            	<fo:block text-align="left">${checkListReport.facilityId?if_exists}</fo:block>	
	                            	<#else>
	                            	<fo:block text-align="left">${checkListReport.partyIdFrom?if_exists}</fo:block>	
	                            	</#if>                                 
	                            </fo:table-cell>
	                        	<fo:table-cell >	
                            	<#assign  partyName="">
		            			<#if (checkListReport.partyIdFrom)?exists>
		            			<#assign partyId=checkListReport.partyIdFrom>
		            			<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)>
		            			</#if>     
		            			<fo:block text-align="left" >${partyName?if_exists}</fo:block>                          
	                            </fo:table-cell>	
	                             <#assign nonRouteMktgTotal=nonRouteMktgTotal+checkListReport.amount>
	                            <fo:table-cell >
	                            	<#assign totalPaymentAmont =(totalPaymentAmont+checkListReport.amount) >
	                            	<fo:block text-align="right">${checkListReport.amount?if_exists?string("#0.00")}</fo:block>	                               
	                            </fo:table-cell> 
	                             <#assign sno= sno+1>
	                        </fo:table-row>
	                        </#list>
	                      <fo:table-row >
	                            <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always">&#160;</fo:block>                               
	                            </fo:table-cell>
	                        </fo:table-row>
	                        
	                        <fo:table-row >
	                            <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always"></fo:block>                               
	                            </fo:table-cell>
	                             <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always"></fo:block>                               
	                            </fo:table-cell>
	                              <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always"></fo:block>                               
	                            </fo:table-cell>
	                        	<fo:table-cell  number-columns-spanned="2">	
	                            	<fo:block text-align="left" >NonRouteMktgTotal:</fo:block>                             
	                            </fo:table-cell>	
	                            <fo:table-cell >
	                            	<fo:block text-align="right" padding="1pt"><@ofbizCurrency amount=nonRouteMktgTotal isoCode=defaultOrganizationPartyCurrencyUomId/></fo:block>	                               
	                            </fo:table-cell> 
	                        </fo:table-row>
	                         </#if>
	                         <fo:table-row >
	                            <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always">&#160;</fo:block>                               
	                            </fo:table-cell>
	                        </fo:table-row>
	                        	                        <#-- deposit Account starts here -->
	                         <#if FinAccountTransList?has_content>
	                        <fo:table-row >
         						<fo:table-cell>	
			                            <fo:block text-align="left" keep-together="always">*********************DepositAccount**********************</fo:block>                               
			                    </fo:table-cell>
         					</fo:table-row >
                    		<#assign sno= 1>
			                <#assign depositAccountTotal=0>
         					<#list FinAccountTransList as finTransList>
         					<#list finTransList as finTransEntry>
         					<#assign ownerpartyDetails = delegator.findOne("FinAccountAndType", {"finAccountId" : finTransEntry.finAccountId}, true)?if_exists/>
		 					<#assign ownerPartyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, ownerpartyDetails.ownerPartyId, false)>
		 					<fo:table-row >
		 						<fo:table-cell>	
			                          <fo:block text-align="left" keep-together="always">${sno }</fo:block>                               
			                    </fo:table-cell>
			                    <fo:table-cell>	
			                          <fo:block text-align="left" keep-together="always">${finTransEntry.finAccountTransId?if_exists}</fo:block>                               
			                    </fo:table-cell>
			                    <fo:table-cell>	
			                          <fo:block text-align="left" keep-together="always">&#160;</fo:block>                               
			                    </fo:table-cell>
			                    <#if ownerpartyDetails?has_content>
			                    <fo:table-cell>	
			                         <fo:block text-align="left" keep-together="always">${ownerpartyDetails.ownerPartyId?if_exists}</fo:block>                               
			                    </fo:table-cell>
			                    <fo:table-cell>	
			                          <fo:block text-align="left" keep-together="always">${ownerPartyName?if_exists}</fo:block>                               
			                     </fo:table-cell>
			                    </#if>
			                    <#assign depositAccountTotal=depositAccountTotal+finTransEntry.amount>
			                    <fo:table-cell>	
			                    	<#assign totalPaymentAmont =(totalPaymentAmont+finTransEntry.amount) >
			                           <fo:block text-align="right" keep-together="always">${finTransEntry.amount?if_exists?string("#0.00")}</fo:block>                               
			                    </fo:table-cell>
			               </fo:table-row>
			               <#assign sno= sno+1>
			               </#list>
	                       </#list>
	                       <fo:table-row >
	                            <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always">&#160;</fo:block>                               
	                            </fo:table-cell>
	                       </fo:table-row>
	                        <fo:table-row >
	                            <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always"></fo:block>                               
	                            </fo:table-cell>
	                             <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always"></fo:block>                               
	                            </fo:table-cell>
	                              <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always"></fo:block>                               
	                            </fo:table-cell>
	                        	<fo:table-cell  number-columns-spanned="2">	
	                            	<fo:block text-align="left" >depositAccountTotal:</fo:block>                             
	                            </fo:table-cell>	
	                            <fo:table-cell >
	                            	<fo:block text-align="right" padding="1pt"><@ofbizCurrency amount=depositAccountTotal isoCode=defaultOrganizationPartyCurrencyUomId/></fo:block>	                               
	                            </fo:table-cell> 
	                        </fo:table-row>
	                      	</#if> 
	                        
	                       <fo:table-row >
	                            <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always"></fo:block>                               
	                            </fo:table-cell>
	                             <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always"></fo:block>                               
	                            </fo:table-cell>
	                              <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always"></fo:block>                               
	                            </fo:table-cell>
	                        	<fo:table-cell  number-columns-spanned="2">	
	                            	<fo:block text-align="left" font-weight="bold" >GrandTotal:</fo:block>                             
	                            </fo:table-cell>
	                            <fo:table-cell >
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