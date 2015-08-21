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
specific language governing permissions and limitations`
under the License.
-->

<#escape x as x?xml>
	<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
		<fo:layout-master-set>
			<fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
					 margin-left="0.2in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.2in" >
				<fo:region-body margin-top="2.8in"/>
				<fo:region-before extent="1in"/>
				<fo:region-after extent="1in"/>
			</fo:simple-page-master>
		</fo:layout-master-set>
		<#if productCategorySummary?has_content>
<#assign sno=1>

       <fo:page-sequence master-reference="main">
		    <fo:static-content font-size="13pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">	 
			   <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" font-size="10pt" white-space-collapse="false">&#160;${uiLabelMap.CommonPage}- <fo:page-number/> </fo:block>			
			    <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false">    UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
				<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false">&#160;      Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
                <#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
                <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
                <fo:block  keep-together="always" text-align="center" font-weight = "bold" font-family="Courier,monospace" white-space-collapse="false">${reportHeader.description?if_exists}</fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${reportSubHeader.description?if_exists}</fo:block>
				<fo:block text-align="center" keep-together="always"  >&#160;------------------------------------------------------------------------------------------</fo:block>
				<fo:block text-align="center" white-space-collapse="false" font-size="12pt"  font-weight="bold" >&#160;   DAILY STOCK STATEMENT OF <#if categoryType=="ICE_CREAM_NANDINI">NANDINI</#if><#if categoryType=="ICE_CREAM_AMUL">AMUL</#if> ICE CREAM [${ReportType}] ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} AND ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")} </fo:block>                
				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			    <fo:block text-align="left" white-space-collapse="false">&#160;&#160;DATE :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}                                         BATCH NO: </fo:block>                
			    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>	
    						   <fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;SNO &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;PARTICULARS&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;TOTAL</fo:block>
     						   <fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(Qty Ltrs)</fo:block>
 
            </fo:static-content>
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
            <fo:block>
                    <fo:table>
				    <fo:table-column column-width="120pt"/>
			        <fo:table-column column-width="230pt"/>
			        <fo:table-column column-width="200pt"/>
                    <fo:table-body>
                    <#assign grandTotal = 0>
                    <#list productCategorySummary as prdcat>
		                    <#assign productCategory = prdcat.entrySet()>
                    <#list productCategory as productdtl>
                                <#assign product=delegator.findOne("ProductCategory",{"productCategoryId":productdtl.getKey()},true)>
								 <#assign grandTotal=grandTotal+productdtl.getValue()>
						<fo:table-row>
	                        		<fo:table-cell>
	                            			<fo:block  keep-together="always" text-align="center" font-weight="bold"  font-size="12pt" white-space-collapse="false">${sno?if_exists}</fo:block>  
	                        		</fo:table-cell>
	                        		<fo:table-cell>
	                            			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">${product.description?if_exists}</fo:block>  
	                        		</fo:table-cell>
	                        		<fo:table-cell>
	                            			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${productdtl.getValue()?if_exists?string("#0.00")}</fo:block>  
	                        		</fo:table-cell>
	                        		<#assign sno=sno+1>	
		                    </fo:table-row>
		                    </#list>
		                    </#list>
							<fo:table-row>
	                        		<fo:table-cell>
	                            			<fo:block  keep-together="always" text-align="center" font-weight="bold"  font-size="12pt">------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>  
	                        		</fo:table-cell>
		                    </fo:table-row>
		                    <fo:table-row>
	                        		<fo:table-cell>
	                            			<fo:block  keep-together="always" text-align="center" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
	                        		</fo:table-cell>
		                    </fo:table-row>
							<fo:table-row>
	                        		<fo:table-cell>
	                            			<fo:block  keep-together="always" text-align="center" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
	                        		</fo:table-cell>
	                        		<fo:table-cell>
	                            			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">GrandTotal</fo:block>  
	                        		</fo:table-cell>
	                        		<fo:table-cell>
	                            			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${grandTotal?if_exists?string("#0.00")}</fo:block>  
	                        		</fo:table-cell>
		                    </fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block> 	
            

		   </fo:flow>	
       </fo:page-sequence>
       <#else>
           <fo:page-sequence master-reference="main">
	    			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	       		 		<fo:block font-size="14pt" text-align="center">
	            			 No Records Found....!
	       		 		</fo:block>
	    			</fo:flow>
		  </fo:page-sequence>				
	    </#if>  
</fo:root>
</#escape>	