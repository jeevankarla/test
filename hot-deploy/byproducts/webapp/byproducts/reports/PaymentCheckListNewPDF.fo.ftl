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
                     margin-left="0.2in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.2in" >
                <fo:region-body margin-top="1.7in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "paychlst.txt")}
        <#assign totalPaymentAmont = 0>  
        <#assign chequeNo=0> 
        <#assign numberOfLines = 7>  
        <fo:page-sequence master-reference="main">
        	<fo:static-content font-size="14pt" font-family="Courier,monospace"  flow-name="xsl-region-before">
        	<#-->	<fo:block text-align="center" keep-together="always">VST_ASCII-015</fo:block> -->
        	    <#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
                <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
        	    
        		<fo:block text-align="left" font-size="12pt" white-space-collapse="false">${uiLabelMap.CommonPage}:<fo:page-number/>                   ${reportHeader.description?if_exists}.</fo:block>
        		<fo:block text-align="left" font-size="12pt" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;         ${reportSubHeader.description?if_exists}.</fo:block>
        		<fo:block text-align="center" keep-together="always" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "MMM d, yyyy HH:mm:ss")}  UserLogin:${userLogin.get("userLoginId")}</fo:block>	 	 	  
        		<#if unDepositedCheques?exists>
        		 <#if unDepositedCheques=="TRUE" >
        		 <fo:block text-align="center" keep-together="always">   UnDeposited Cheques List</fo:block>
        		 <#else>
        		  <fo:block text-align="center" keep-together="always">  Deposited Cheques List</fo:block>
        		 </#if>
        		<#else>
        		<fo:block text-align="center" keep-together="always">   Payments CheckList</fo:block>
        		</#if>
        		 <fo:block font-family="Courier,monospace"  >                
                <fo:table >
                         <fo:table-column column-width="48pt"/>
                    <fo:table-column column-width="82pt"/>
                    <fo:table-column column-width="82pt"/>                
                    <fo:table-column column-width="170pt"/>
                    <fo:table-column column-width="92pt"/>
                    <fo:table-column column-width="87pt"/>
                    <fo:table-column column-width="92pt"/>     
                    <fo:table-body> 
                     <fo:table-row >
	                           <fo:table-cell >	
	                             <fo:block text-align="left" keep-together="always"  >--------------------------------------------------------------------------------</fo:block>
	                            </fo:table-cell>
	                   </fo:table-row>
                     <fo:table-row >
	                           <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always" >S.No</fo:block>                               
	                            </fo:table-cell>
	                        	<fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always" >Route</fo:block>    
	                            	<fo:block text-align="left" keep-together="always" >&#160;Code</fo:block>                            
	                            </fo:table-cell>
	                        	<fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always" >Retailer</fo:block>    
	                            	<fo:block text-align="left" keep-together="always" >&#160;Code</fo:block>                                  
	                            </fo:table-cell>	
	                        	<fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always" >Retailer</fo:block>    
	                            	<fo:block text-align="left" keep-together="always" >&#160;Name</fo:block>                                
	                            </fo:table-cell>		                                                    
	                            <fo:table-cell >
	                            	<fo:block text-align="left" >CHEQUE</fo:block>	
	                            	<fo:block text-align="left"  >&#160; No.</fo:block>	                               
	                            </fo:table-cell>	                            
	                            <fo:table-cell >
	                            	<fo:block text-align="left" >Payment</fo:block>	 
	                            	<fo:block text-align="left" >DATE</fo:block>	                               
	                            </fo:table-cell>
	                            <fo:table-cell >
	                            	<fo:block text-align="right" >Amount</fo:block>	                               
	                            </fo:table-cell>	
	                        </fo:table-row>
	                         <fo:table-row >
	                           <fo:table-cell >	
	                           <fo:block text-align="left" keep-together="always"  >--------------------------------------------------------------------------------</fo:block>
	                            </fo:table-cell>
	                       </fo:table-row>
                    </fo:table-body>
                   </fo:table>
                  </fo:block>
        	</fo:static-content>
            <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
            	 <fo:block font-family="Courier,monospace"  font-size="14pt">                
                <fo:table >
                     <fo:table-column column-width="48pt"/>
                    <fo:table-column column-width="82pt"/>
                    <fo:table-column column-width="82pt"/>                
                    <fo:table-column column-width="170pt"/>
                    <fo:table-column column-width="92pt"/>
                    <fo:table-column column-width="87pt"/>
                    <fo:table-column column-width="92pt"/>            
                    <fo:table-body> 
                    <fo:table-row><fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell></fo:table-row>    
	                     <#assign boothPaidCheckList= bankPaidMap.entrySet()>
	                      <#list boothPaidCheckList as bankPaidDetails>
	                      <#--
	                      <#assign numberOfLines=numberOfLines+2>
	                     <#if (numberOfLines>62) > 
	                      <fo:table-row><fo:table-cell><fo:block  break-after="page"></fo:block></fo:table-cell></fo:table-row>  
    			            <#assign numberOfLines=7>
	                     </#if> 
	                       <fo:table-row><fo:table-cell><fo:block >&#160;</fo:block></fo:table-cell></fo:table-row>  -->
	                         <fo:table-row >
	                        	<fo:table-cell  number-columns-spanned="4">	
	                            	<fo:block text-align="left" keep-together="always"><#if bankPaidDetails.getKey()!="noBankName">${bankPaidDetails.getKey()} </#if></fo:block>                               
	                            </fo:table-cell>
	                          </fo:table-row>
	                            <#assign sno= 1>
			                     <#assign paidCheckList=bankPaidDetails.getValue()>
			                     <#assign bankTotal=0>
	                      		 <#list paidCheckList as checkListReport>
	                      		  <#--
	                      		  <#assign numberOfLines=numberOfLines+1>
	                      		    <#if (numberOfLines>62) > 
				                      <fo:table-row><fo:table-cell><fo:block  break-after="page"></fo:block></fo:table-cell></fo:table-row>  
			    			            <#assign numberOfLines=7>
				                     </#if> -->
	                         <fo:table-row >
	                            <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always">${sno}</fo:block>                               
	                            </fo:table-cell>
	                             <fo:table-cell >
	                             <#if checkListReport.facilityId?exists>
	                            	<fo:block text-align="left">${boothRouteIdsMap.get(checkListReport.facilityId)?if_exists}</fo:block>	
	                            	</#if>                               
	                            </fo:table-cell>	
	                             <fo:table-cell >
	                            	<fo:block text-align="left">${checkListReport.facilityId?if_exists}</fo:block>	                               
	                            </fo:table-cell>
	                        	<fo:table-cell >	
                            	
                            	<#assign  partyName="">
		            			<#if (checkListReport.partyIdFrom)?exists>
		            			<#assign partyId=checkListReport.partyIdFrom>
		            			<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)>
		            			</#if>     
		            			<fo:block text-align="left" font-size="13pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(partyName?if_exists)),20)}</fo:block>                          
	                            </fo:table-cell>	
	                        	<fo:table-cell >	
	                            	<fo:block text-align="left" >${checkListReport.paymentRefNum?if_exists}</fo:block>                               
	                            </fo:table-cell>		                                                    
	                            <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(checkListReport.paymentDate, "dd-MMM-yy")}</fo:block>                               
	                            </fo:table-cell>
	                            <#--
	                             <fo:table-cell >
	                            	<fo:block text-align="right">${checkListReport.amount?if_exists}</fo:block>	                               
	                            </fo:table-cell> -->
	                             <#assign bankTotal=bankTotal+checkListReport.amount>
	                            <fo:table-cell >
	                            	<#assign totalPaymentAmont =(totalPaymentAmont+checkListReport.amount) >
	                            	<fo:block text-align="right">${checkListReport.amount?if_exists?string("#0.00")}</fo:block>	                               
	                            </fo:table-cell> 
	                             <#assign sno= sno+1>
	                              <#if checkListReport.paymentRefNum?has_content>
	                              		<#assign chequeNo= chequeNo+1> 
	                              </#if>
	                        </fo:table-row>
	                        </#list>
	                        <#if bankPaidDetails.getKey()!="noBankName">
	                         <#assign numberOfLines=numberOfLines+3>
	                        <fo:table-row >
	                           <fo:table-cell >	
	                           <fo:block text-align="left" keep-together="always"  >--------------------------------------------------------------------------------</fo:block>
	                            </fo:table-cell>
	                       </fo:table-row>
	                        <fo:table-row >
	                            <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always"></fo:block>                               
	                            </fo:table-cell>
	                             <fo:table-cell >
	                            	<fo:block text-align="left"></fo:block>	                               
	                            </fo:table-cell>	
	                             <fo:table-cell >
	                            	<fo:block text-align="left"></fo:block>	                               
	                            </fo:table-cell>
	                        	<fo:table-cell >	
	                            	<fo:block text-align="left" >Bank Total:</fo:block>                               
	                            </fo:table-cell>	
	                        	<fo:table-cell >	
	                            	<fo:block text-align="left" ></fo:block>                               
	                            </fo:table-cell>		                                                    
	                            <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always"></fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >
	                            	<fo:block text-align="right">${bankTotal?if_exists?string("#0.00")}</fo:block>	                               
	                            </fo:table-cell>
	                        </fo:table-row>
	                        <fo:table-row >
	                           <fo:table-cell >	
	                           <fo:block text-align="left" keep-together="always"  >--------------------------------------------------------------------------------</fo:block>
	                            </fo:table-cell>
	                       </fo:table-row>
	                       </#if>
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
	                            	<fo:block text-align="left"></fo:block>	                               
	                            </fo:table-cell>	
	                             <fo:table-cell >
	                            	<fo:block text-align="left"></fo:block>	                               
	                            </fo:table-cell>
	                        	<fo:table-cell >	
	                            	<fo:block text-align="left" >Total:</fo:block>                               
	                            </fo:table-cell>	
	                        	<fo:table-cell >	
	                            	<fo:block text-align="left" ></fo:block>                               
	                            </fo:table-cell>		                                                    
	                            <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always"></fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >
	                            	<fo:block text-align="right" padding="1pt"><@ofbizCurrency amount=totalPaymentAmont isoCode=defaultOrganizationPartyCurrencyUomId/></fo:block>	                               
	                            </fo:table-cell> 
	                        </fo:table-row>
	                        <#if chequeNo != 0>
	                        <fo:table-row >
	                            <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always"></fo:block>                               
	                            </fo:table-cell>
	                             <fo:table-cell >
	                            	<fo:block text-align="left"></fo:block>	                               
	                            </fo:table-cell>	
	                             <fo:table-cell >
	                            	<fo:block text-align="left"></fo:block>	                               
	                            </fo:table-cell>
	                        	<fo:table-cell >	
	                            	<fo:block text-align="left" >No of Cheques:</fo:block>                               
	                            </fo:table-cell>	
	                        	<fo:table-cell >	
	                            	<fo:block text-align="left" ></fo:block>                               
	                            </fo:table-cell>		                                                    
	                            <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always"></fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >
	                            	<fo:block text-align="right" >${chequeNo}</fo:block>	                               
	                            </fo:table-cell> 
	                        </fo:table-row>
	                        </#if>
                    </fo:table-body>
                   </fo:table>
                 </fo:block>
           </fo:flow>
        </fo:page-sequence>
     </fo:root>
</#escape>