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
            <fo:simple-page-master master-name="main" page-width="8.27in" page-height="11.69in"
                margin-top="0.1in" margin-bottom="0.5in" margin-left="0.3in" margin-right="0.3in">
        <fo:region-body margin-top="0.5in" margin-bottom="0.5in"/>
        <fo:region-before extent="0.5in"/>
        <fo:region-after extent="0.5in"/>     
            </fo:simple-page-master>
        </fo:layout-master-set>
        
        <#if finalInvoiceItemList?has_content>
        <fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">
              <fo:static-content flow-name="xsl-region-before">
              		<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"> &#160;CITD- <fo:page-number/></fo:block>
               </fo:static-content>						
            <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
            
            			<fo:block text-align="center" white-space-collapse="false"  font-size="12pt" keep-together="always">&#160;MSME- TOOL ROOM,HYDERABAD</fo:block>
               			<fo:block text-align="center" white-space-collapse="false"  font-size="12pt" keep-together="always">&#160;CENTRAL INSTITUTE OF TOOL DESIGN</fo:block>
               			<fo:block text-align="center" white-space-collapse="false"  font-size="12pt" keep-together="always">&#160;(Ministry of MSME - A Government of India Society)</fo:block>
               			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                  		<fo:block text-align="left" font-size="11pt" white-space-collapse="false">Internal Organization	     					   						         :${intOrgName?if_exists} - ${partyId}</fo:block>
                  		<fo:block text-align="left" font-size="11pt"  white-space-collapse="false">From Date    												                             : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")}      To Date  : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block>
                  		<fo:block text-align="left" white-space-collapse="false">Total for the period for GL Account :${glTotal?if_exists}</fo:block>
                    	<#if glAccountId?has_content>
                    	<#assign glAccount = delegator.findOne("GlAccount", {"glAccountId" : glAccountId}, true)>
                    	<fo:block text-align="left" white-space-collapse="false">Gl Account Name																												:		${glAccount.accountName?if_exists} - ${glAccountId}</fo:block>
                        <#else>
                        <fo:block text-align="left" white-space-collapse="false">Gl Account Name																												:		</fo:block>
                        </#if>
                        <fo:block>-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                  		
                  		
                  		<fo:block>
                        <fo:table>
                            <fo:table-column column-width="19%"/>
                            <fo:table-column column-width="19%"/>
                            <fo:table-column column-width="17%"/>
                            <fo:table-column column-width="15%"/>
                            <fo:table-column column-width="8%"/>
                            <fo:table-column column-width="6%"/>
                            <fo:table-column column-width="6%"/>
                            <fo:table-column column-width="10%"/>
                            <fo:table-body>
                            
                            	<fo:table-row>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">InvoiceItemType</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">InvoiceType</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">invoiceId</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">invoiceDate</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">Status</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="right" font-size="11pt">dr</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="right" font-size="11pt">cr</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="right" font-size="11pt">amount</fo:block>
	                                </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">----------------------------------</fo:block>
	                                </fo:table-cell>
                                </fo:table-row>
                            
                            
                            
                            <#list finalInvoiceItemList as finalInvoiceItem>
                            
                            <#if finalInvoiceItem.get("invoiceItemTypeId")== "TOTAL">
                            	<fo:table-row>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">----------------------------------</fo:block>
	                                </fo:table-cell>
                                </fo:table-row>
                               <fo:table-row>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">${finalInvoiceItem.get("invoiceItemTypeId")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">${finalInvoiceItem.get("invoiceTypeId")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">${finalInvoiceItem.get("invoiceId")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">&#160;</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">${finalInvoiceItem.get("status")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="right" font-size="11pt">${finalInvoiceItem.get("drFlag")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="right" font-size="11pt">${finalInvoiceItem.get("crFlag")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="right" font-size="11pt">${finalInvoiceItem.get("amount")?if_exists}</fo:block>
	                                </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">----------------------------------</fo:block>
	                                </fo:table-cell>
                                </fo:table-row>
                           <#elseif finalInvoiceItem.get("invoiceItemTypeId")== "Subtotal"> 
                           		<fo:table-row>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">----------------------------------</fo:block>
	                                </fo:table-cell>
                                </fo:table-row>   
                           		<fo:table-row>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">${finalInvoiceItem.get("invoiceItemTypeId")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">${finalInvoiceItem.get("invoiceTypeId")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">${finalInvoiceItem.get("invoiceId")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">&#160;</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">${finalInvoiceItem.get("status")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="right" font-size="11pt">${finalInvoiceItem.get("drFlag")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="right" font-size="11pt">${finalInvoiceItem.get("crFlag")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="right" font-size="11pt">${finalInvoiceItem.get("amount")?if_exists}</fo:block>
	                                </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">---------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">----------------------------------</fo:block>
	                                </fo:table-cell>
                                </fo:table-row>
                           
                           <#else>
                           		<fo:table-row>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">${finalInvoiceItem.get("invoiceItemTypeId")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">${finalInvoiceItem.get("invoiceTypeId")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">${finalInvoiceItem.get("invoiceId")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt"><#if finalInvoiceItem.get("invoiceDate")?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString((finalInvoiceItem.get("invoiceDate")), "dd/MM/yyyy")}</#if></fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">${finalInvoiceItem.get("status")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="right" font-size="11pt">${finalInvoiceItem.get("drFlag")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="right" font-size="11pt">${finalInvoiceItem.get("crFlag")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="right" font-size="11pt">${finalInvoiceItem.get("amount")?if_exists}</fo:block>
	                                </fo:table-cell>
                                </fo:table-row>
                           
                           </#if> 
                           </#list>   
                           		<fo:table-row>
	                                <fo:table-cell >
	                                    <fo:block text-align="right" font-size="11pt">Unreconciled </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">&#160;Amount</fo:block>
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
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">&#160;</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="right" font-size="11pt">${glTotal - grandTotal}</fo:block>
	                                </fo:table-cell>
                                </fo:table-row>
                             
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
