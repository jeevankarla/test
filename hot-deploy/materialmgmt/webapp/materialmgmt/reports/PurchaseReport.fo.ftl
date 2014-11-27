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
        ${setRequestAttribute("OUTPUT_FILENAME", "IceCreamSalesReport.pdf")}
        <#if errorMessage?has_content>
	<fo:page-sequence master-reference="main">
	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	   <fo:block font-size="14pt">
	           ${errorMessage}.
        </fo:block>
	</fo:flow>
	</fo:page-sequence>	
	<#else>
       <#if prodMap?has_content>
	        <fo:page-sequence master-reference="main" font-size="12pt">	
	        <fo:static-content flow-name="xsl-region-before">
              		<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"> &#160;${uiLabelMap.CommonPage}- <fo:page-number/></fo:block>
               </fo:static-content>
	        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
          			<fo:block text-align="center" font-weight="bold"  keep-together="always"  white-space-collapse="false">PURCHASE ANALYSIS - SUMMARY REPORT - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")} </fo:block>-->
          			<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
          			<fo:block>------------------------------------------------------------------------------------------------</fo:block>
            	    <fo:block text-align="left" font-weight="bold" font-size="12pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">VoucherCode       Narration&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;                                         Amount</fo:block>
	        		<fo:block>------------------------------------------------------------------------------------------------</fo:block>
        			<fo:block>
        				<fo:table>
		                    <fo:table-column column-width="130pt"/>
		                    <fo:table-column column-width="200pt"/>
		                    <fo:table-column column-width="320pt"/> 
		               	    <fo:table-column column-width="50pt"/>
		            		<fo:table-column column-width="130pt"/> 		
		            		<fo:table-column column-width="120pt"/>
		                    <fo:table-body>
		                    <#assign prodDetails = prodMap.entrySet()>
		                    <#assign totalRevenue=0>
       							<#list prodDetails as prod>
       							  <#assign totalRevenue=totalRevenue+prod.getValue().get("totalRevenue")?if_exists>
       							  <#assign productCategory = delegator.findOne("ProductCategory", {"productCategoryId" : prod.getKey()}, true)?if_exists/>
       							<fo:table-row>
       							       <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">Analysis Code :</fo:block>  
							            </fo:table-cell>
					                    <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">${productCategory.description}</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${prod.getValue().get("totalRevenue")?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
								</#list>
								<fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
          						  </fo:table-row> 
								<fo:table-row>
					                    <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">Account Code Total</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${totalRevenue?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
								<fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>------------------------------------------------------------------------------------------------</fo:block>
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
			        	${uiLabelMap.NoOrdersFound}.
			   		 </fo:block>
				</fo:flow>
			</fo:page-sequence>	
		</#if>   
 </#if>
 </fo:root>
</#escape>