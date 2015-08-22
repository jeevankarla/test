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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-right=".3in" margin-top=".5in">
                <fo:region-body margin-top=".1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "purchaseVatCategoryReport.pdf")}
        <#if errorMessage?has_content>
	<fo:page-sequence master-reference="main">
	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	   <fo:block font-size="14pt">
	           ${errorMessage}.
        </fo:block>
	</fo:flow>
	</fo:page-sequence>	
	<#else>
       <#if vatReturnMap?has_content>
	        <fo:page-sequence master-reference="main" font-size="12pt">	
	        <fo:static-content flow-name="xsl-region-before">
              		<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"> &#160;${uiLabelMap.CommonPage}- <fo:page-number/></fo:block>
               </fo:static-content>
	        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">
	        		<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
                    <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${reportHeader.description?if_exists}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${reportSubHeader.description?if_exists}</fo:block>
          			<fo:block text-align="center" font-weight="bold"  keep-together="always"  white-space-collapse="false">PURCHASE ${reportTypeFlag} VAT CATEGORY REPORT - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")} </fo:block>-->
          			<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
          	<#-->	<fo:block>------------------------------------------------------------------------------------------------</fo:block>
            	    <fo:block text-align="left" font-weight="bold" font-size="12pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">VoucherCode       Narration&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;                                         Amount</fo:block>
	        		<fo:block>------------------------------------------------------------------------------------------------</fo:block>
        		-->
        		   <fo:block text-align="left" font-size="12pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">------------------------------------------------------------------------------------------------ </fo:block>
        		    <fo:block>
        				<fo:table border-style="solid">
		                    <fo:table-column column-width="50pt"/>
		                    <fo:table-column column-width="100pt"/>
		                    <fo:table-column column-width="100pt"/> 
		               	    <fo:table-column column-width="150pt"/>	
   		               	    <fo:table-column column-width="180pt"/>		 
		               	    <fo:table-column column-width="100pt"/>
		                    <fo:table-body>
		                    <fo:table-row border-style="solid">
       							       <fo:table-cell border-style="solid">
							            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">SINO</fo:block>  
							            </fo:table-cell>
					                    <fo:table-cell border-style="solid">
							            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold"> INVOICE DATE</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell border-style="solid">
							            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">INVOICE NO</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">PARTY NAME</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell border-style="solid">
							            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold" >PRODUCT NAME</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">VAT AMOUNT&#160;&#160;</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
		                    <#assign vatReturnMapDetails = vatReturnMap.entrySet()>
		                    <#list vatReturnMapDetails as vatReturnMapDetail>
		                    <#assign productId= vatReturnMapDetail.getValue().get("productId")?if_exists >
		                    <#assign productNameDetails = delegator.findOne("Product", {"productId" : productId}, true)>
		           
       							 <fo:table-row>
       							       <fo:table-cell border-style="dotted">
							            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" >${vatReturnMapDetail.getKey()?if_exists}</fo:block>  
							            </fo:table-cell>
					                    <fo:table-cell border-style="dotted">
							            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(vatReturnMapDetail.getValue().get("invoiceDate"), "dd/MM/yyyy")}</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell border-style="dotted">
							            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" >${vatReturnMapDetail.getValue().get("invoiceId")?if_exists}</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell border-style="dotted">
							            	<fo:block  text-align="left" font-size="12pt" white-space-collapse="false" ><#if vatReturnMapDetail.getValue().get("partyId")?has_content>${vatReturnMapDetail.getValue().get("partyName")?if_exists} [${vatReturnMapDetail.getValue().get("partyId")?if_exists}] </#if></fo:block>  
							            </fo:table-cell>
							             <fo:table-cell border-style="dotted">
							            	<fo:block  text-align="left" font-size="12pt" white-space-collapse="false" >${productNameDetails.get("description")?if_exists} [${productNameDetails.get("internalName")?if_exists}]</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="dotted">
							            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${vatReturnMapDetail.getValue().get("vatAmount")?if_exists?string("##0.00")}</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
							     </#list>
							      <fo:table-row>
       							      <fo:table-cell border-style="solid" number-columns-spanned="5">
						                    <fo:block keep-together="always" font-weight="bold" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt"  >Total Vat Amount</fo:block>
						              </fo:table-cell>
							           <fo:table-cell border-style="solid">
							            	<fo:block  keep-together="always" font-weight="bold" text-align="right" font-size="12pt" white-space-collapse="false" >${totVatAmount?if_exists?string("##0.00")}</fo:block>  
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
                        No Records Found
			   		 </fo:block>
				</fo:flow>
			</fo:page-sequence>	
		</#if>   
 </#if>
 </fo:root>
</#escape>