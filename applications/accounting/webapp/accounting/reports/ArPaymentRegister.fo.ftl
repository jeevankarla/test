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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="13in"
            margin-top="0.5in" margin-bottom=".3in" margin-left="1.2in" margin-right=".5in">
        <fo:region-body margin-top="1.42in"/>
        <fo:region-before extent="1.5in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "ArPmntRegister.pdf")}
 <#if paymentRegisterList?has_content> 
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
                    <#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
				    <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
				    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >${reportHeader.description?if_exists} </fo:block>
				    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">${reportSubHeader.description?if_exists}                             </fo:block>	
                    <fo:block text-align="center" font-size="12pt" keep-together="always"  white-space-collapse="false" font-weight="bold">&#160;&#160; AR Payment Register Report</fo:block>
              		<fo:block text-align="center" font-size="12pt" keep-together="always"  white-space-collapse="false" font-weight="bold">&#160;&#160; From ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} To ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}	</fo:block>
              		
              		<fo:block>
	                 	<fo:table border-style="solid">
	                    <fo:table-column column-width="200pt"/>
	                    <fo:table-column column-width="90pt"/>
	                    <fo:table-column column-width="90pt"/>  
	               	    <fo:table-column column-width="90pt"/>
	            		<fo:table-column column-width="60pt"/> 		
	            		<fo:table-column column-width="220pt"/>
	                    <fo:table-body>
	                    <fo:table-row >
	                    		<fo:table-cell >
                            		<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false" font-weight="bold" border-style="solid">Payment Voucher No</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell >
                            		<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false" font-weight="bold" border-style="solid">Amount</fo:block>   
                       			</fo:table-cell>
                       			<fo:table-cell >
                            		<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false" font-weight="bold" border-style="solid">Amt Applied </fo:block>
                       			</fo:table-cell>
                       			<fo:table-cell >
                            		<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false" font-weight="bold" border-style="solid">Amt Open</fo:block> 
                       			</fo:table-cell>
                       			<fo:table-cell >
                            		<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false" font-weight="bold" border-style="solid">Inv Id</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                            		<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false" wrap-option="wrap" font-weight="bold" border-style="solid">Invoice Item Type</fo:block>  
                       			</fo:table-cell>
                			</fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block> 		
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
                 	<fo:table border-style="solid">
                    <fo:table-column column-width="200pt"/>
                    <fo:table-column column-width="90pt"/>
                    <fo:table-column column-width="90pt"/>  
               	    <fo:table-column column-width="90pt"/>
            		<fo:table-column column-width="60pt"/> 		
            		<fo:table-column column-width="220pt"/>
                    <fo:table-body>
                    	
	                	<#list paymentRegisterList as paymentRegister>
								
								<fo:table-row border-style="solid">
									<fo:table-cell >
	                            		<#if paymentRegister.get("paymentId")?has_content>
											<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
	                                             ${paymentRegister.get("paymentId")?if_exists}
	                                      	</fo:block>
	                                      <#else>
	                                      	<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
	                                      	</fo:block>
								 		</#if> 
	                            		  
	                       			</fo:table-cell>
	                       			<fo:table-cell >
	                            		<#if paymentRegister.get("amount")?has_content>
											<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
	                                             ${paymentRegister.get("amount")?if_exists}
	                                      	</fo:block>
	                                      <#else>
	                                      	<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
	                                      	</fo:block>
								 		</#if> 
	                            		  
	                       			</fo:table-cell>
	                       			<fo:table-cell >
	                            		<#if paymentRegister.get("amountApplied")?has_content>
											<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
	                                             ${paymentRegister.get("amountApplied")?if_exists}
	                                      	</fo:block>
	                                      <#else>
	                                      	<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
	                                      	</fo:block>
								 		</#if> 
	                            		  
	                       			</fo:table-cell>
	                       			<fo:table-cell >
	                            		<#if paymentRegister.get("amountOpen")?has_content>
											<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
	                                             ${paymentRegister.get("amountOpen")?if_exists}
	                                      	</fo:block>
	                                      <#else>
	                                      	<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
	                                      	</fo:block>
								 		</#if> 
	                            		  
	                       			</fo:table-cell>
	                       			<fo:table-cell >
	                            		<#if paymentRegister.get("invoiceId")?has_content>
											<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
	                                             ${paymentRegister.get("invoiceId")?if_exists}
	                                      	</fo:block>
	                                      <#else>
	                                      	<fo:block  text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false"> 
	                                      	</fo:block>
								 		</#if> 
	                            		  
	                       			</fo:table-cell>
	                       			<fo:table-cell >
	                            		<#if paymentRegister.get("invoiceItemTypeId")?has_content>
											<fo:block  text-align="left" font-size="13pt" wrap-option="wrap" white-space-collapse="false"> 
	                                             ${paymentRegister.get("invoiceItemTypeId")?if_exists}
	                                      	</fo:block>
	                                      <#else>
	                                      	<fo:block  text-align="left" font-size="13pt" wrap-option="wrap" white-space-collapse="false"> 
	                                      	</fo:block>
								 		</#if> 
	                            		  
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
			<fo:block font-size="14pt">
	            	No Records Found For The Given Duration

   		 </fo:block>
		</fo:flow>
	</fo:page-sequence>	
    </#if>  
</fo:root>
</#escape>