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
            margin-top="0.5in" margin-bottom="1in" margin-left=".3in" margin-right="1in">
        <fo:region-body margin-top="1in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "BankRemittanceReport.txt")}
<#assign lineNumber = 5>
<#assign numberOfLines = 52>
<#assign facilityNumberInPage = 0>
<#if categoryPaymentMap?has_content>
<fo:page-sequence master-reference="main" force-page-count="no-force">					
		<fo:static-content flow-name="xsl-region-before">
			<#assign facilityNumberInPage = 0>
					<fo:block text-align="left" font-size="8pt" white-space-collapse="false">&#160;                                      ${uiLabelMap.aavinDairyMsg}</fo:block>
					<fo:block text-align="left" font-size="8pt" white-space-collapse="false">&#160;                                                      MARKETING UNIT TCMPF LTD., CHENNAI-35</fo:block>
					<fo:block linefeed-treatment="preserve" font-size="8pt">&#xA;</fo:block>
              		<fo:block keep-together="always" font-size="8pt" font-family="Courier,monospace" white-space-collapse="false">&#160;                      BANK REMITTANCE STATEMENT          DATE: ${effectiveDate?if_exists}</fo:block>
  	              	<fo:block font-size="12pt">-------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            		<fo:block white-space-collapse="false" font-size="8pt"  font-family="Courier,monospace"  text-align="left"> SNO  PCD      PARTY NAME            BANK NAME              BRANCH NAME           CHQ-NO      CHQ-DT      CHQ-AMOUNT</fo:block>
            		<fo:block font-size="12pt">-------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
			<#assign eachCategory=categoryPaymentMap.entrySet()?if_exists>
				<fo:block  border-style="solid" font-family="Courier,monospace">
		        			<fo:table  table-layout="fixed" width="100%" space-before="0.2in">
            				<fo:table-column column-width="10pt"/>
				   		 	<fo:table-column column-width="50pt"/>
				    		<fo:table-column column-width="110pt"/>
				    		<fo:table-column column-width="110pt"/>
				    		<fo:table-column column-width="110pt"/>
				    		<fo:table-column column-width="50pt"/>
				    		<fo:table-column column-width="55pt"/>
				    		<fo:table-column column-width="55pt"/>
				    		<fo:table-body>
				    			<#assign serialNo = 1>
				    			<#list eachCategory as eachIter>
				    			<#assign facilityCategoryList = eachIter.getValue()?if_exists>
				    			<#list facilityCategoryList as eachListItem>
                				<fo:table-row>                            
                            		<fo:table-cell>
                                		<fo:block text-align="left" font-size="8pt">${serialNo}</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="center" font-size="8pt">${eachListItem.get("facilityId").toUpperCase()?if_exists}</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="left" font-size="8pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(eachListItem.get("partyId"))),20)}</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="left" font-size="8pt"><#if eachListItem.get("bankName")?exists>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(eachListItem.get("bankName").toUpperCase())),20)}</#if></fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="left" font-size="8pt"><#if eachListItem.get("branchName")?exists>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(eachListItem.get("branchName").toUpperCase())),15)}</#if></fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="left" font-size="8pt">${eachListItem.get("paymentRefNum")?if_exists}</fo:block>
                            		</fo:table-cell>	 
                            		<fo:table-cell>
                            			<fo:block text-align="left" font-size="8pt">${eachListItem.get("effectiveDate")?if_exists}</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="right" font-size="8pt">${eachListItem.get("amount")?if_exists?string("##0.00")}</fo:block>
                            		</fo:table-cell>                          
                       			</fo:table-row>
                       			<#assign serialNo = serialNo+1>
                       			
                       			<#if (lineNumber >= numberOfLines)>
                    				<#assign lineNumber = 5>
			                    		<fo:table-row>
			                   	     		<fo:table-cell>
				                            	<fo:block font-size="7pt" page-break-after="always"></fo:block>        
				                        	</fo:table-cell>
				                    	</fo:table-row>
			                    	<#else>
			                    </#if>
			                    	<#assign lineNumber = lineNumber+1>
                       			</#list>
                       			<fo:table-row>                            
                            		<fo:table-cell>
                                		<fo:block text-align="left" font-size="12pt">-------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                            		</fo:table-cell>
                            	</fo:table-row>
                            	<#assign category = eachIter.getKey()?if_exists>
                            	<fo:table-row> 
                            		<fo:table-cell/>
                            		<fo:table-cell/>
                            		<fo:table-cell/>
                            		<fo:table-cell/>
                            		<fo:table-cell/>
                            		<fo:table-cell/>
                            		<fo:table-cell>
                            			<fo:block text-align="left" font-size="8pt" keep-together="always" white-space-collapse="false">Total : </fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="right" font-size="8pt" keep-together="always" white-space-collapse="false">${totalMap.get(category)?if_exists?string("##0.00")}</fo:block>
                            		</fo:table-cell>
                            	</fo:table-row>
                            	<fo:table-row>
                            		<fo:table-cell>
                            			<fo:block text-align="left" font-size="12pt">-------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                            		</fo:table-cell>
                       			</fo:table-row>
                       		    </#list>
                       		    <fo:table-row>
                       		    <fo:table-cell/>
                            		<fo:table-cell/>
                            		<fo:table-cell/>
                            		<fo:table-cell/>
                            		<fo:table-cell/>
                            		<fo:table-cell>
                            			<fo:block text-align="left" font-size="8pt" keep-together="always" white-space-collapse="false"> Grand Total : </fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell/>
                            		<fo:table-cell>
                            			<fo:block text-align="right" font-size="8pt" keep-together="always" white-space-collapse="false">${grandTotalMap.grandTotal?if_exists?string("##0.00")}</fo:block>
                            		</fo:table-cell>
                       			</fo:table-row>	
                       			<fo:table-row>
                            		<fo:table-cell>
                            			<fo:block text-align="left" font-size="12pt">-------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
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
	            	${uiLabelMap.OrderNoOrderFound}.
	       		 </fo:block>
	    	</fo:flow>
		</fo:page-sequence>
</#if>					
</fo:root>
</#escape>