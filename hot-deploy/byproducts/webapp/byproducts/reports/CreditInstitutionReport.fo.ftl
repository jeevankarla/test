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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-bottom="1in" margin-left=".3in" margin-right=".3in">
        <fo:region-body margin-top="1.5in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "prdctRetrnReport.txt")}
 <#if boothPaymentsMap?has_content> 
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
					<fo:block  keep-together="always"  text-align="left" font-weight="bold" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160; &#160;&#160; &#160; &#160;        ${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="left" font-weight="bold" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160; &#160; &#160;&#160; &#160;        ${uiLabelMap.KMFDairySubHeader}</fo:block>
                    <fo:block text-align="left"  keep-together="always"  font-weight="bold" white-space-collapse="false">&#160;&#160; &#160; &#160;        Credit Institution Payment Check List From :: ${fromDate?if_exists}  To:: ${thruDate?if_exists}</fo:block>
                    <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
              		<fo:block >-----------------------------------------------------------------------------------------------</fo:block>
              		<fo:block font-size="12pt" font-weight="bold" >    Payment Id      &#160;Date    		 &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Instrument No 		 &#160; Payment Method Type  		&#160; &#160; Amount  &#160; Narration		</fo:block>
            		<fo:block >-----------------------------------------------------------------------------------------------</fo:block>
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
                 	<fo:table>
                    <fo:table-column column-width="80pt"/>
                    <fo:table-column column-width="100pt"/>
                    <fo:table-column column-width="120pt"/> 
               	    <fo:table-column column-width="90pt"/>
            		<fo:table-column column-width="120pt"/> 		
            		<fo:table-column column-width="20pt"/>
            		<fo:table-column column-width="80pt"/>
                    <fo:table-body>
                    	<#assign paymentsList = boothPaymentsMap.entrySet()>
  							<#list paymentsList as payments>
  							<#assign paymentsLst=payments.getValue()>
  							<fo:table-row>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="left"   font-weight="bold" font-size="12pt" white-space-collapse="false">${payments.getKey()}</fo:block>  
	                       			</fo:table-cell>
	                       			<#assign  partyName="">
			            			<#if (payments.getKey())?exists>
			            			<#assign partyId=payments.getKey()>
			            			<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)>
			            			</#if>     
	                       			<fo:table-cell>
	                            		<fo:block  text-align="left"  font-weight="bold" font-size="12pt" white-space-collapse="false">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(partyName?if_exists)),20)}</fo:block>  
	                       			</fo:table-cell>
	                       </fo:table-row>
	                          <fo:table-row>
		                			<fo:table-cell>
		                    		<fo:block >-----------------------------------------------------------------------------------------------</fo:block>
          					 	</fo:table-cell>
		                    	</fo:table-row>		
  							<#list paymentsLst as payment>
								<fo:table-row>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="left"  font-size="12pt" white-space-collapse="false">${payment.paymentId}</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="left"  font-size="12pt" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(payment.paymentDate, "dd-MMM-yyyy")}</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="left"  font-size="12pt" white-space-collapse="false">${payment.paymentRefNum?if_exists}</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                       			<#assign paymentType="">
	                       			<#if payment?has_content && payment.paymentMethodTypeId?has_content>
							            <#assign paymentMethodType = delegator.findOne("PaymentMethodType", {"paymentMethodTypeId" : payment.paymentMethodTypeId}, true)>
							            <#assign paymentType = paymentMethodType.description>
							        </#if>
	                            		<fo:block  text-align="left"  keep-together="always" font-size="12pt" white-space-collapse="false">${paymentType}</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="right" font-size="12pt" white-space-collapse="false">${payment.amount}</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="left"  font-size="12pt" white-space-collapse="false">&#160;</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="left"  font-size="12pt" white-space-collapse="false">${(payment.comments)?if_exists}</fo:block>  
	                       			</fo:table-cell>
                				</fo:table-row>
                		</#list>
                		<fo:table-row>
                			<fo:table-cell>
                    		<fo:block >-----------------------------------------------------------------------------------------------</fo:block>
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
			<fo:block font-size="12pt">
	            	${uiLabelMap.NoOrdersFound}.
	       		 </fo:block>
		</fo:flow>
	</fo:page-sequence>	
    </#if>  
</fo:root>
</#escape>