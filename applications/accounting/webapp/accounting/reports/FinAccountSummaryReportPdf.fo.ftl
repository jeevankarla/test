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
		                  <fo:block text-align="center" font-size="11pt">Fin account summary</fo:block>
		                  <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		       			  <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		       			   
		                  <fo:block>
		                        <fo:table>
		                        	<fo:table-column column-width="8%"/>
		                            <fo:table-column column-width="8%"/>
		                            <fo:table-column column-width="10%"/>
		                            <fo:table-column column-width="6%"/>
		                            <fo:table-column column-width="8%"/>
		                            <fo:table-column column-width="7%"/>
		                            <fo:table-column column-width="15%"/>
		                            <fo:table-column column-width="15%"/>
		                            <fo:table-column column-width="8%"/>
		                            <fo:table-column column-width="8%"/>
		                            <fo:table-column column-width="8%"/>
		                            <fo:table-body>
		                            	<fo:table-row>
		                            		<fo:table-cell border-style="solid">
		                            			<fo:block text-align="left" font-size="11pt">TransactionDate</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
			                                	<fo:block text-align="left" font-size="11pt">finAccountTransId</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
			                                	<fo:block text-align="left" font-size="11pt">paymentType</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
		                            			<fo:block text-align="left" font-size="11pt">paymentId</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
			                                	<fo:block text-align="left" font-size="11pt">partyId</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
			                                	<fo:block text-align="left" font-size="11pt">paymentRefNo</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
		                            			<fo:block text-align="left" font-size="11pt">paymentStatus</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
			                                	<fo:block text-align="left" font-size="11pt">comments</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
			                                	<fo:block text-align="left" font-size="11pt">refNum</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
		                            			<fo:block text-align="left" font-size="11pt">withDraw</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
			                                	<fo:block text-align="left" font-size="11pt">deposit</fo:block>
			                                </fo:table-cell>
			                                
			                                
			                                
		                                </fo:table-row>
		                                
		                                <#assign srn = 0>
		                                <#list glFinalList as eachGlFinal>
		                                <#assign srn = srn + 1>
		                                
		                                <#if eachGlFinal.get("finAccountTransId") == "Total">
		                                <fo:table-row>
		                            		<fo:table-cell border-style="solid">
		                            			<fo:block text-align="left" font-size="11pt">TOTAL</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
			                                	<fo:block text-align="left" font-size="11pt">&#160;</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
			                                	<fo:block text-align="left" font-size="11pt">&#160;</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
			                                	<fo:block text-align="left" font-size="11pt">&#160;</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
		                            			<fo:block text-align="left" font-size="11pt">&#160;</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
			                                	<fo:block text-align="left" font-size="11pt">&#160;</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
			                                	<fo:block text-align="left" font-size="11pt">&#160;</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
		                            			<fo:block text-align="left" font-size="11pt">&#160;</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
			                                	<fo:block text-align="left" font-size="11pt">&#160;</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
			                                	<fo:block text-align="right" font-size="11pt">${eachGlFinal.get("withDraw")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
		                            			<fo:block text-align="right" font-size="11pt">${eachGlFinal.get("deposit")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                
		                                </fo:table-row>
		                                
		                                <#else>
			                                
		                                <fo:table-row>
		                            		<fo:table-cell border-style="solid">
		                            			<fo:block text-align="left" font-size="11pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(eachGlFinal.get("transactionDate"), "dd/MM/yyyy")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
			                                	<fo:block text-align="left" font-size="11pt">${eachGlFinal.get("finAccountTransId")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
			                                	<fo:block text-align="left" font-size="11pt">${eachGlFinal.get("paymentType")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
			                                	<fo:block text-align="left" font-size="11pt">${eachGlFinal.get("paymentId")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
		                            			<fo:block text-align="left" font-size="11pt">${eachGlFinal.get("partyId")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
			                                	<fo:block text-align="left" font-size="11pt">${eachGlFinal.get("paymentRefNo")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
			                                	<fo:block text-align="left" font-size="11pt">${eachGlFinal.get("paymentStatus")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
		                            			<fo:block text-align="left" font-size="11pt">${eachGlFinal.get("comments")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
			                                	<fo:block text-align="left" font-size="11pt">${eachGlFinal.get("refNum")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
			                                	<fo:block text-align="right" font-size="11pt">${eachGlFinal.get("withDraw")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell border-style="solid">
		                            			<fo:block text-align="right" font-size="11pt">${eachGlFinal.get("deposit")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                
		                                </fo:table-row>
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
