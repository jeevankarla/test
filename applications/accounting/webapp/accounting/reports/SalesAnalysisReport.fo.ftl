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
        <fo:region-body margin-top="1in" margin-bottom=".6in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>     
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "SalesAnalysisReport.pdf")}
 <#if glAccountMap?has_content>
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
				 <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" font-weight="bold" font-size="10pt" white-space-collapse="false">UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
				<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;                        MOTHER DAIRY, YALAHANKA KMF UNIT : GKVK POST.BANGALORE-560 065            Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MM-yyyy")}</fo:block>
                <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" font-weight="bold"  font-size="10pt" white-space-collapse="false">&#160;${uiLabelMap.CommonPage}- <fo:page-number/> </fo:block>
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
               <fo:block  text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold">SALES ANALYSIS FOR THE PERIOD FROM ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")} </fo:block>
				<fo:block font-size="10pt">------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
               <fo:block>
               <#assign glAccounts=glAccountMap.entrySet()>
                    <fo:table>
				    <fo:table-column column-width="25%"/>
			        <fo:table-column column-width="35%"/>
			        <fo:table-column column-width="25%"/>
                    <fo:table-body>
                    	<fo:table-row>
                    	   <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="center" font-weight="bold"  font-size="10pt" white-space-collapse="false">GL ACCOUNT</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="center" font-weight="bold"  font-size="10pt" white-space-collapse="false">SALES SCHEDULE</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false">AMOUNT</fo:block>  
                			</fo:table-cell>
                		</fo:table-row>
                		<fo:table-row>
                			<fo:table-cell>
                				<fo:block font-size="10pt">------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                			</fo:table-cell>
                		</fo:table-row>
                        <#list glAccounts as glAccount>
                		<fo:table-row>
                    	   <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="center"   font-size="11pt" white-space-collapse="false">${glAccount.getKey()?if_exists}</fo:block>  
                			</fo:table-cell>
                			<#assign glAccnt=delegator.findOne("GlAccount",{"glAccountId",glAccount.getKey()},true)>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left"   font-size="11pt" white-space-collapse="false">${glAccnt.description?if_exists}</fo:block>  
                			</fo:table-cell>
                           <#assign value=0>
                           <#if glAccount.getValue()?has_content>
                           <#assign value=glAccount.getValue()>
                           </#if>
                           <#if value gte 0>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right"   font-size="11pt" white-space-collapse="false">${value?if_exists?string("##0.00")} (Dr)</fo:block>  
                			</fo:table-cell>
                          <#else>
                             <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right"   font-size="11pt" white-space-collapse="false">${((-1)*value)?if_exists?string("##0.00")} (Cr)</fo:block>  
                			</fo:table-cell>
                          </#if>
                		</fo:table-row>
                		</#list>
                        <fo:table-row>
                			<fo:table-cell>
                				<fo:block font-size="10pt">------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                			</fo:table-cell>
                		</fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block> 
               <#if channelWiseMap?has_content>
               <fo:block page-break-before="always" text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold">SALES ANALYSIS CHANNEL WISE FOR THE PERIOD FROM ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")} </fo:block>
				<fo:block font-size="10pt">------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	               <fo:block>
	                 <fo:table>
	                 	<fo:table-column column-width="35%"/>
			        	<fo:table-column column-width="35%"/>
			        	<fo:table-column column-width="25%"/>
                    	<fo:table-body>
                    		<fo:table-row>
                                <fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false">CHANNEL/CATEGORY</fo:block> 
                    	   		</fo:table-cell>      
                    	   		<fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false">PRODUCT</fo:block> 
                    	   		</fo:table-cell>
                    	   		<fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false">AMOUNT</fo:block> 
                    	   		</fo:table-cell>
                    	   	</fo:table-row>	
                    	   	<fo:table-row>
                				<fo:table-cell>
                					<fo:block font-size="10pt">------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                				</fo:table-cell>
                			</fo:table-row>	
                				<#assign channelWise=channelWiseMap.entrySet()>
								<#list channelWise as channel>
                                 <#if channel.getValue()?has_content>
							<fo:table-row>
                    	   		<fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="11pt" white-space-collapse="false">${saleTypeMap.get(channel.getKey())?if_exists}</fo:block> 
                    	   		</fo:table-cell>
                                <fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false"></fo:block> 
                    	   		</fo:table-cell>
                    	   		<fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false"></fo:block> 
                    	   		</fo:table-cell>
                    	   	</fo:table-row>
                    	   	<fo:table-row>
                				<fo:table-cell>
                					<fo:block font-size="10pt">------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                				</fo:table-cell>
                			</fo:table-row>
                                <#assign categoryWise=channel.getValue().entrySet()>
                                <#list categoryWise as category>
                                <#assign productCat=delegator.findOne("ProductCategory",{"productCategoryId":category.getKey()},true)>
                                <#assign products=category.getValue().entrySet()>
                            <fo:table-row>
                    	   		<fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="9pt" white-space-collapse="false">${productCat.description?if_exists}</fo:block> 
                    	   		</fo:table-cell>
                                <fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false"></fo:block> 
                    	   		</fo:table-cell>
                    	   		<fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false"></fo:block> 
                    	   		</fo:table-cell>
                    	   	</fo:table-row>
                            <fo:table-row>
                				<fo:table-cell>
                					<fo:block font-size="10pt">------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                				</fo:table-cell>
                			</fo:table-row>
                			    <#assign totValue=0>
                			    <#list products as product>
                                <#assign prod=delegator.findOne("Product",{"productId":product.getKey()},true)>
                                 <#if product.getValue() !=0> 
                            <fo:table-row>
                                <fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false"></fo:block> 
                    	   		</fo:table-cell>
                    	   		<fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="left"   font-size="10pt" white-space-collapse="false">${prod.productName?if_exists}</fo:block> 
                    	   		</fo:table-cell>
                                <#assign prodValue=0>
                                <#if product.getValue()?has_content>
                                  <#assign prodValue=product.getValue()> 
                                </#if>   
                                <#assign totValue=totValue+prodValue>
                                <#if prodValue gte 0>
                    	   		<fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right"   font-size="10pt" white-space-collapse="false">${prodValue?if_exists?string("##0.00")} (Dr)</fo:block> 
                    	   		</fo:table-cell>
                    	   		<#else>
                                <fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right"   font-size="10pt" white-space-collapse="false">${((-1)*prodValue)?if_exists?string("##0.00")} (Cr)</fo:block> 
                    	   		</fo:table-cell>
                                </#if>
                    	   	</fo:table-row>
                    	   	    </#if>
                                </#list>
							<fo:table-row>
                				<fo:table-cell>
                					<fo:block font-size="10pt">------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                				</fo:table-cell>
                			</fo:table-row>
                            <fo:table-row>
                    	   		<fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="9pt" white-space-collapse="false"></fo:block> 
                    	   		</fo:table-cell>
                                <fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false">TOTAL   :</fo:block> 
                    	   		</fo:table-cell>
                    	   		<#if totValue gte 0>
                    	   		<fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false">${totValue?if_exists?string("##0.00")} (Dr)</fo:block> 
                    	   		</fo:table-cell>
                                <#else>
                                 <fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false">${((-1)*totValue)?if_exists?string("##0.00")} (Cr)</fo:block> 
                    	   		</fo:table-cell>
                                 </#if>
                    	   	</fo:table-row>
                            <fo:table-row>
                				<fo:table-cell>
                					<fo:block font-size="10pt">------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                				</fo:table-cell>
                			</fo:table-row>
                    	   	    </#list>
                                </#if>
                                </#list> 
                              
                			
                    	</fo:table-body>   	
	                 </fo:table>
	               </fo:block>
               </#if>
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