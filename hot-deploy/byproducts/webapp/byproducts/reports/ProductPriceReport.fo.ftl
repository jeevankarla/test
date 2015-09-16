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
	<fo:simple-page-master master-name="main" page-height="10in" page-width="12in"  margin-left=".3in" margin-right=".3in" margin-bottom=".3in" margin-top=".3in">
        <fo:region-body margin-top="1.7in" margin-bottom=".6in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>     
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "PartyLedgerGroupReport.pdf")}
 <#if categoryWiseMap?has_content>
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
				 <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
				 <#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
                 <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
				 
				<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;                                ${reportHeader.description?if_exists}.             Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MM-yyyy")}</fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;                            ${reportSubHeader.description?if_exists}.                 ${uiLabelMap.CommonPage}- <fo:page-number/> </fo:block> 
                <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace"  font-weight="bold"  white-space-collapse="false">PRODUCT PRICE LIST AS ON ${fromDateTime?if_exists}</fo:block>
				<fo:block font-size="10pt">-------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				<fo:block>
                    <fo:table>
                    <fo:table-column column-width="4%"/>
				    <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="20%"/>
			        <fo:table-column column-width="12%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="14%"/>
			        <fo:table-column column-width="12%"/>
                    <fo:table-body>
                    	<fo:table-row>
                    		<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false">S.NO</fo:block>  
                			</fo:table-cell>
                    	   <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false">PRODUCT ID</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="center" font-weight="bold"  font-size="10pt" white-space-collapse="false">DESCRIPTION</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="center" font-weight="bold"  font-size="10pt" white-space-collapse="false">Default Price</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                			<fo:block  keep-together="always" text-align="center" font-weight="bold"  font-size="10pt" white-space-collapse="false">M.R.P</fo:block> 
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="center" font-weight="bold"  font-size="10pt" white-space-collapse="false">WSD DEPOT RATE</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			 <fo:block  keep-together="always" text-align="center" font-weight="bold"  font-size="10pt" white-space-collapse="false">UTP RATE</fo:block>
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="center" font-weight="bold"  font-size="10pt" white-space-collapse="false">M.R.P INTRA STATE</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="center" font-weight="bold"  font-size="10pt" white-space-collapse="false">M.R.P INTER STATE</fo:block>  
                			</fo:table-cell>
                		</fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block>
               <fo:block font-size="10pt">-------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">
            <#assign categoryWise=categoryWiseMap.entrySet()>	
               <#list categoryWise as category>
                <#assign productDetails=category.getValue()> 
                <#assign sno=1>
                <fo:block  keep-together="always" text-align="center"  font-size="11pt" white-space-collapse="false">${category.getKey()}</fo:block>
	            <fo:block font-size="10pt">-------------------------------------------------------------------------------------------------------------------------------------------</fo:block> 
               <fo:block>
                    <fo:table>
                    <fo:table-column column-width="4%"/>
				    <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="20%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="14%"/>
			        <fo:table-column column-width="12%"/>
                    <fo:table-body>
                     <#list productDetails as product>
                    	<fo:table-row>
                    	   <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left"  font-size="10pt" white-space-collapse="false">${sno}</fo:block>  
                			</fo:table-cell>
                    	   <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left"  font-size="10pt" white-space-collapse="false">${product.internalName?if_exists}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block   text-align="left" font-size="10pt" white-space-collapse="true">${product.description?if_exists}</fo:block>  
                			</fo:table-cell>
                			<#if product.In_DEFAULT_PRICE?has_content>
	                            <#if product.In_DEFAULT_PRICE =="N">
	                			<fo:table-cell>
	                    			<fo:block  keep-together="always" text-align="right"  font-size="10pt" white-space-collapse="false">${product.DEFAULT_PRICE?if_exists?string("##0.00")} (Ex)</fo:block>  
	                			</fo:table-cell>
	                            <#else>
	                             <fo:table-cell>
	                    			<fo:block  keep-together="always" text-align="right"  font-size="10pt" white-space-collapse="false">${product.DEFAULT_PRICE?if_exists?string("##0.00")}</fo:block>  
	                			</fo:table-cell>
	                            </#if>
                            <#else>
	                             <fo:table-cell>
	                    			<fo:block  keep-together="always" text-align="right"  font-size="10pt" white-space-collapse="false"></fo:block>  
	                			</fo:table-cell>
                            </#if>
                            <#if product.In_MRP_PRICE?has_content>
	                			<#if product.In_MRP_PRICE =="N">
	                			<fo:table-cell>
	                    			<fo:block  keep-together="always" text-align="right"  font-size="10pt" white-space-collapse="false">${product.MRP_PRICE?if_exists?string("##0.00")} (Ex)</fo:block>  
	                			</fo:table-cell>
	                            <#else>
	                             <fo:table-cell>
	                    			<fo:block  keep-together="always" text-align="right"  font-size="10pt" white-space-collapse="false">${product.MRP_PRICE?if_exists?string("##0.00")}</fo:block>  
	                			</fo:table-cell>
	                            </#if>
                            <#else>
	                             <fo:table-cell>
	                    			<fo:block  keep-together="always" text-align="right"  font-size="10pt" white-space-collapse="false"></fo:block>  
	                			</fo:table-cell>
                            </#if>
                            <#if product.In_UTP_PRICE?has_content>
	                            <#if product.In_UTP_PRICE =="N">
	                			<fo:table-cell>
	                    			<fo:block  keep-together="always" text-align="right"  font-size="10pt" white-space-collapse="false">${product.UTP_PRICE?if_exists?string("##0.00")} (Ex)</fo:block>  
	                			</fo:table-cell>
	                            <#else>
	                             <fo:table-cell>
	                    			<fo:block  keep-together="always" text-align="right"  font-size="10pt" white-space-collapse="false">${product.UTP_PRICE?if_exists?string("##0.00")}</fo:block>  
	                			</fo:table-cell>
	                            </#if>
                            <#else>
	                             <fo:table-cell>
	                    			<fo:block  keep-together="always" text-align="right"  font-size="10pt" white-space-collapse="false"></fo:block>  
	                			</fo:table-cell>
                            </#if>
                            <#if product.In_WSD_PRICE?has_content>
	                			<#if product.In_WSD_PRICE =="N">
	                			<fo:table-cell>
	                    			<fo:block  keep-together="always" text-align="right"  font-size="10pt" white-space-collapse="false">${product.WSD_PRICE?if_exists?string("##0.00")} (Ex)</fo:block>  
	                			</fo:table-cell>
	                            <#else>
	                             <fo:table-cell>
	                    			<fo:block  keep-together="always" text-align="right"  font-size="10pt" white-space-collapse="false">${product.WSD_PRICE?if_exists?string("##0.00")}</fo:block>  
	                			</fo:table-cell>
	                            </#if>
                            <#else>
	                             <fo:table-cell>
	                    			<fo:block  keep-together="always" text-align="right"  font-size="10pt" white-space-collapse="false"></fo:block>  
	                			</fo:table-cell>
                            </#if>
                            <#if product.In_MRP_IS?has_content>
	                			<#if product.In_MRP_IS=="N">
	                			<fo:table-cell>
	                    			<fo:block  keep-together="always" text-align="right"  font-size="10pt" white-space-collapse="false">${product.MRP_IS?if_exists?string("##0.00")} (Ex)</fo:block>  
	                			</fo:table-cell>
	                            <#else>
	                             <fo:table-cell>
	                    			<fo:block  keep-together="always" text-align="right"  font-size="10pt" white-space-collapse="false">${product.MRP_IS?if_exists?string("##0.00")}</fo:block>  
	                			</fo:table-cell>
	                            </#if>
                           <#else>
	                             <fo:table-cell>
	                    			<fo:block  keep-together="always" text-align="right"  font-size="10pt" white-space-collapse="false"></fo:block>  
	                			</fo:table-cell>
                           </#if>
                            <#if product.In_MRP_OS?has_content>
	                			<#if product.In_MRP_OS=="N">
	                			<fo:table-cell>
	                    			<fo:block  keep-together="always" text-align="right"  font-size="10pt" white-space-collapse="false">${product.MRP_OS?if_exists?string("##0.00")} (Ex)</fo:block>  
	                			</fo:table-cell>
	                            <#else>
	                             <fo:table-cell>
	                    			<fo:block  keep-together="always" text-align="right"  font-size="10pt" white-space-collapse="false">${product.MRP_OS?if_exists?string("##0.00")}</fo:block>  
	                			</fo:table-cell>
	                            </#if>
                            <#else>
	                             <fo:table-cell>
	                    			<fo:block  keep-together="always" text-align="right"  font-size="10pt" white-space-collapse="false"></fo:block>  
	                			</fo:table-cell>
                            </#if>
                		</fo:table-row>
                        <#assign sno=sno+1>
                        </#list>
                    </fo:table-body>
                </fo:table>
               </fo:block> 
               <fo:block font-size="10pt">-------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
               </#list>
			 </fo:flow>
			 </fo:page-sequence>
			  <#else>
    	<fo:page-sequence master-reference="main">
		<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			<fo:block font-size="14pt">
	            	${uiLabelMap.NoOrdersFound}.
	       		 </fo:block>
		</fo:flow>
	</fo:page-sequence>	
    </#if>  
</fo:root>
</#escape>