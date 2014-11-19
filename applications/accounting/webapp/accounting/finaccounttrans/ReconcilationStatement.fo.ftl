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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="14in"  margin-left=".2in" margin-right=".1in" margin-top=".2in" margin-bottom="0.2in">
                <fo:region-body margin-top="0.5in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        <#if finAccountReconciliationList?has_content && parameters.noConditionFind?exists && parameters.noConditionFind == 'Y'>
        ${setRequestAttribute("OUTPUT_FILENAME", "ReconcilationStatement.txt")}
        <fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">		
        <fo:static-content flow-name="xsl-region-before">
              		<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"> &#160;Page - <fo:page-number/></fo:block>
              		<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
            </fo:static-content>	
              	
		        	<fo:flow flow-name="xsl-region-body"  font-family="Courier,monospace" >	
		        	<fo:block text-align="center"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">     KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD</fo:block>
                    	<fo:block text-align="center"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">    UNIT : MOTHER DAIRY , G.K.V.K POST : YELAHANKA, BANGALORE -560065.</fo:block>
                    	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block>
                    	<fo:block text-align="center"  font-family="Courier,monospace" font-weight="bold"  white-space-collapse="false">BANK RECONCILIATION STATEMENT   AS ON ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy HH:mm:ss")} </fo:block>
                    	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block>
                    	<fo:block text-align="center"  font-family="Courier,monospace" font-weight="bold"  white-space-collapse="false">Name of the Financial Account : ${finAccount.finAccountName?if_exists}</fo:block>
                    	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block>  
                    	<fo:block text-align="center"  font-family="Courier,monospace"  white-space-collapse="false">&#160;                                                         Balance As Per Books : ${finAccount.actualBalance?if_exists?string("#0.00")}</fo:block>
              			<fo:block text-align="left"  keep-together="always"  white-space-collapse="false">_________________________________________________________________________________________________________________________________________</fo:block>
              			<fo:block text-align="left"  keep-together="always"  white-space-collapse="false">Unreconciled Deposits</fo:block> 
		        		<fo:block text-align="left"  keep-together="always"  white-space-collapse="false">_________________________________________________________________________________________________________________________________________</fo:block> 
		        		<fo:block text-align="left"  keep-together="always" font-size="11pt" font-family="Courier,monospace"  white-space-collapse="false">Fin Account Fin Account  Payment Party  Transaction  Instrument      Amount   Payment     Payment    PaymentMethod    Status       Comments </fo:block>
		        		<fo:block text-align="left"  keep-together="always" font-size="11pt" font-family="Courier,monospace"  white-space-collapse="false">&#160;Trans Id   TransType                       Date      No                         Id         Type       Type                     </fo:block>  
		        		<fo:block text-align="left"  keep-together="always"  white-space-collapse="false">_________________________________________________________________________________________________________________________________________</fo:block>		        
            	<fo:block>
                 	<fo:table>
                 	<fo:table-column column-width="70pt"/>
                    <fo:table-column column-width="90pt"/>
                    <fo:table-column column-width="100pt"/> 
            		<fo:table-column column-width="70pt"/> 
            		<fo:table-column column-width="100pt"/>
            		<fo:table-column column-width="80pt"/> 	
            		<fo:table-column column-width="60pt"/>
            		<fo:table-column column-width="90pt"/>
            		<fo:table-column column-width="90pt"/>
            		<fo:table-column column-width="90pt"/> 
            		<fo:table-column column-width="149pt"/> 
            		<#assign depositFinal_Amount=0>
            		<#assign withdrawFinal_Amount=0>
            		<fo:table-body>
            		<#list finAccountReconciliationList as finAccountTrans>            		  
			          <#assign finAccountTypeId=finAccountTrans.finAccountTransTypeId>
			         <#if finAccountTypeId == "DEPOSIT">
						<fo:table-row border-style="solid">						
               				<fo:table-cell border-style="solid">
                           		<fo:block  keep-together="always"  text-align="center"  font-size="10pt">${finAccountTrans.finAccountTransId}</fo:block>  
                       		</fo:table-cell>                   			                   			
                       		<fo:table-cell border-style="solid">
                           		<fo:block  keep-together="always" font-size="10pt">${finAccountTrans.finAccountTransTypeId?if_exists}</fo:block>  
                       		</fo:table-cell>                   			
                   			<#if finAccountTrans.paymentPartyName?has_content>
                   			<fo:table-cell border-style="solid">
                       			<fo:block   font-size="9pt" >${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(finAccountTrans.paymentPartyName?if_exists)),20)}</fo:block>  
                   			</fo:table-cell>
                   			</#if>
                   			<#if finAccountTrans.transactionDate?has_content>
                   				<fo:table-cell border-style="solid">
                       			<fo:block  keep-together="always" font-size="10pt">${finAccountTrans.transactionDate?if_exists}</fo:block>  
                   			</fo:table-cell>
                   			</#if>  
                  			<fo:table-cell border-style="solid">
                       			<fo:block  font-size="10pt" >${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(finAccountTrans.instrumentNo?if_exists)),15)}</fo:block>  
                   			</fo:table-cell>                   		                 		
                   			<#if finAccountTrans.amount?has_content>
                   			<#assign depositFinal_Amount=depositFinal_Amount+finAccountTrans.amount>
                   			<fo:table-cell border-style="solid">
                       			<fo:block  keep-together="always" font-size="10pt" text-align="right" white-space-collapse="false">${finAccountTrans.amount?if_exists?string("##0.00")}</fo:block>  
                   			</fo:table-cell>
                   			</#if> 
                   			<fo:table-cell border-style="solid">
                       			<fo:block  keep-together="always" font-size="10pt" text-align="center" white-space-collapse="false">${finAccountTrans.paymentId?if_exists}</fo:block>  
                   			</fo:table-cell>
                   			<fo:table-cell border-style="solid">
                       			<fo:block  font-size="10pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(finAccountTrans.paymentType?if_exists)),15)}</fo:block>  
                   			</fo:table-cell>
							<fo:table-cell border-style="solid">
                       			<fo:block  keep-together="always" font-size="10pt" >${finAccountTrans.paymentMethodTypeId?if_exists}</fo:block>  
                   			</fo:table-cell>
                   			 <#assign glAccountType = delegator.findOne("StatusItem", {"statusId" : finAccountTrans.statusId}, false)>
							<fo:table-cell border-style="solid">
                       			<fo:block  keep-together="always" font-size="10pt" text-align="left" white-space-collapse="false">${glAccountType.description?if_exists}</fo:block>  
                   			</fo:table-cell>
							<fo:table-cell border-style="solid">
                       			<fo:block  keep-together="always" font-size="10pt" text-align="left" white-space-collapse="false">${finAccountTrans.comments?if_exists}</fo:block>  
                   			</fo:table-cell>                   			
                   		</fo:table-row>
                   </#if>
	                </#list>
					</fo:table-body>
		       </fo:table>
                 <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block>
            <#--<fo:block text-align="left"  keep-together="always"  white-space-collapse="false">_________________________________________________________________________________________________________________________________________________________</fo:block>-->
		       	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false">&#160;                                                  Total   : ${depositFinal_Amount?if_exists?string("##0.00")}</fo:block> 
		 </fo:block>         
         <#-->
            <fo:block>
            <fo:block text-align="left"  keep-together="always"  white-space-collapse="false">_________________________________________________________________________________________________________________________________________________________</fo:block>
		     <fo:block text-align="center"  keep-together="always"  white-space-collapse="false">&#160;                                                                           Total : ${depositFinal_Amount?if_exists?string("##0.00")}</fo:block> 
            </fo:block>	-->                        
 <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block>
 <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block>
 <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block>
            <fo:block>
                <fo:block text-align="left"  keep-together="always"  white-space-collapse="false">_________________________________________________________________________________________________________________________________________</fo:block>
        		<fo:block text-align="left"  keep-together="always"  white-space-collapse="false">Unreconciled Withdrawals</fo:block> 
        		<fo:block text-align="left"  keep-together="always"  white-space-collapse="false">_________________________________________________________________________________________________________________________________________</fo:block>
            </fo:block>	           
            <fo:block>
                 	<fo:table>
                 	<fo:table-column column-width="70pt"/>
                    <fo:table-column column-width="90pt"/>
                    <fo:table-column column-width="100pt"/> 
            		<fo:table-column column-width="70pt"/> 
            		<fo:table-column column-width="100pt"/>
            		<fo:table-column column-width="80pt"/> 	
            		<fo:table-column column-width="60pt"/>
            		<fo:table-column column-width="90pt"/>
            		<fo:table-column column-width="90pt"/>
            		<fo:table-column column-width="90pt"/> 
            		<fo:table-column column-width="149pt"/> 
            		<fo:table-body>
            		<#list finAccountReconciliationList as finAccountTrans>            		  
			          <#assign finAccountTypeId=finAccountTrans.finAccountTransTypeId>
			          <#if finAccountTypeId == "WITHDRAWAL">
					<fo:table-row>
               				<#if finAccountTrans.finAccountTransId?has_content>
               				<fo:table-cell border-style="solid">
                           		<fo:block  keep-together="always" font-size="10pt" text-align="center" white-space-collapse="false">${finAccountTrans.finAccountTransId?if_exists}</fo:block>  
                       		</fo:table-cell>
                   			</#if>
                   			<#if finAccountTypeId?has_content>
                       		<fo:table-cell border-style="solid">
                           		<fo:block  keep-together="always" font-size="10pt" text-align="left" white-space-collapse="false">${finAccountTrans.finAccountTransTypeId?if_exists}</fo:block>  
                       		</fo:table-cell>
                   			</#if>                  			
                   			<#if finAccountTrans.paymentPartyName?has_content>
                   			<fo:table-cell border-style="solid">
                       			<fo:block   font-size="9pt" >${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(finAccountTrans.paymentPartyName?if_exists)),20)}</fo:block>  
                   			</fo:table-cell>
                   			</#if>
                   		<#if finAccountTrans.transactionDate?has_content>
                   				<fo:table-cell border-style="solid">
                       			<fo:block  keep-together="always" font-size="10pt" text-align="left" white-space-collapse="false">${finAccountTrans.transactionDate?if_exists}</fo:block>  
                   			</fo:table-cell>
                   		</#if>    
                   		<fo:table-cell border-style="solid">
                       			<fo:block  keep-together="always" font-size="10pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(finAccountTrans.instrumentNo?if_exists)),15)}</fo:block>  
                   			</fo:table-cell>               
                       			<#if finAccountTrans.amount?has_content>
                   			<#assign withdrawFinal_Amount=withdrawFinal_Amount+finAccountTrans.amount>
                   			<fo:table-cell border-style="solid">
                       			<fo:block  keep-together="always" font-size="10pt" text-align="right" white-space-collapse="false">${finAccountTrans.amount?if_exists?string("##0.00")}</fo:block>  
                   			</fo:table-cell>
                   			</#if>                 
                   			<fo:table-cell border-style="solid">
                       			<fo:block  keep-together="always" font-size="10pt" text-align="center" white-space-collapse="false">${finAccountTrans.paymentId?if_exists}</fo:block>  
                   			</fo:table-cell>
                   			<fo:table-cell border-style="solid">
                       			<fo:block  keep-together="always" font-size="10pt" >${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(finAccountTrans.paymentType?if_exists)),15)}</fo:block>  
                   			</fo:table-cell>
                   			           			<fo:table-cell border-style="solid">
                       			<fo:block  keep-together="always" font-size="10pt" text-align="center" white-space-collapse="false">${finAccountTrans.paymentMethodTypeId?if_exists}</fo:block>  
                   			</fo:table-cell>
                   			 <#assign glAccountType = delegator.findOne("StatusItem", {"statusId" : finAccountTrans.statusId}, false)>           
                   			<fo:table-cell border-style="solid">
                       			<fo:block  keep-together="always" font-size="10pt" text-align="left" white-space-collapse="false">${glAccountType.description?if_exists}</fo:block>  
                   			</fo:table-cell>                 			
                   			<fo:table-cell border-style="solid">
                       			<fo:block  keep-together="always" font-size="10pt" text-align="left" white-space-collapse="false">${finAccountTrans.comments?if_exists}</fo:block>  
                   			</fo:table-cell>                  
                   	</fo:table-row>
                   	</#if>
	                </#list>               			
				</fo:table-body>
                </fo:table>
            </fo:block>
            <fo:block>
            <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block>
          <#--  <fo:block text-align="left"  keep-together="always"  white-space-collapse="false">_________________________________________________________________________________________________________________________________________________________</fo:block>-->
		        		<fo:block text-align="left"  keep-together="always"  white-space-collapse="false">&#160;                                                  Total   :   ${withdrawFinal_Amount?if_exists?string("##0.00")}</fo:block>
		        	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false">_________________________________________________________________________________________________________________________________________</fo:block> 
            </fo:block>	
            <fo:block>
		        		<fo:block text-align="right"  keep-together="always"  white-space-collapse="false">&#160;   Balance As Per Bank : ${((finAccount.actualBalance-depositFinal_Amount)+withdrawFinal_Amount)?if_exists?string("##0.00")}                &#160;&#160;&#160;&#160;</fo:block>
		        		<fo:block text-align="left"  keep-together="always"  white-space-collapse="false">_________________________________________________________________________________________________________________________________________</fo:block> 
            </fo:block>
		</fo:flow>
		</fo:page-sequence>
	<#else>
	<fo:page-sequence master-reference="main">
    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
       		 <fo:block font-size="14pt" text-align="center">
            	"No Orders Found".
       		 </fo:block>
    	</fo:flow>
	</fo:page-sequence>	
	</#if>
 </fo:root>
</#escape>