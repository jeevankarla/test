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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-bottom="1in" margin-left=".3in" margin-right=".3in">
        <fo:region-body margin-top="1.5in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "prdctRetrnReport.txt")}
 <#if returnProductList?has_content> 
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
					<fo:block  keep-together="always" font-size="12pt" text-align="left" font-weight="bold" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160; &#160;&#160; &#160; &#160;        ${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" font-size="12pt" text-align="left" font-weight="bold" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160; &#160; &#160;&#160; &#160;        ${uiLabelMap.KMFDairySubHeader}</fo:block>
                    <fo:block text-align="left" font-size="12pt" keep-together="always"  font-weight="bold" white-space-collapse="false">&#160;&#160; &#160; &#160;        Product Returns  Report From :: ${effectiveDateStr?if_exists}  To:: ${thruEffectiveDateStr?if_exists}</fo:block>
                    <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
              		<fo:block font-size="12pt">----------------------------------------------------------------------------------------------------------------------------------------</fo:block>
              		<fo:block font-size="12pt" font-weight="bold" >Date    			         &#160;&#160;&#160;&#160;&#160;&#160;&#160; Route 		 &#160; Retailer  		Shipment		&#160;Product 			&#160; &#160; Return    &#160;&#160;Reason			&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;UserLogin</fo:block>
              		<fo:block font-size="12pt" font-weight="bold" >&#160;&#160;    			         &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;  				Type&#160; &#160;&#160;       		&#160;Name&#160; &#160;&#160;&#160;&#160;&#160;      		Quantity  		</fo:block>
            		<fo:block font-size="12pt">----------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
                 	<fo:table>
                    <fo:table-column column-width="95pt"/>
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="70pt"/> 
               	    <fo:table-column column-width="66pt"/>
            		<fo:table-column column-width="60pt"/> 		
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="20pt"/>
            		<fo:table-column column-width="140pt"/>
            		<fo:table-column column-width="76pt"/>
                    <fo:table-body>
                    	<#list returnProductList as returnItem>
                    	 <#assign shipmentTypeId=returnItem.get("shipmentTypeId")?if_exists>
						 <#if shipmentTypeId=="AM_SHIPMENT_SUPPL" || shipmentTypeId=="AM_SHIPMENT">  
						 <#assign shipmentTypeId="AM">        		
		              	<#elseif shipmentTypeId=="PM_SHIPMENT_SUPPL" || shipmentTypeId=="PM_SHIPMENT">
		              	 <#assign shipmentTypeId="PM">   
              	       </#if> 
		                        <#assign product = delegator.findOne("Product", {"productId" : returnItem.get("productId")}, true)?if_exists/>
								<fo:table-row>
                    				<fo:table-cell>
	                            		<fo:block  keep-together="always" font-size="12pt" text-align="left"  white-space-collapse="false">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(returnItem.get("date"), "dd-MMM-yyyy")}</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="left" font-size="12pt" white-space-collapse="false">${returnItem.get("routeId")}</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="left"  font-size="12pt" white-space-collapse="false">${returnItem.get("boothId")}</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="left"  font-size="12pt" white-space-collapse="false">${shipmentTypeId}</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="left"  font-size="12pt" white-space-collapse="false">${product.brandName?if_exists}</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="right" font-size="12pt" white-space-collapse="false">${returnItem.get("returnQuantity")?if_exists}</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="left"  font-size="12pt" white-space-collapse="false">&#160;</fo:block>  
	                       			</fo:table-cell>
	                       			<#assign returnReason = delegator.findOne("ReturnReason", {"returnReasonId" : returnItem.get("returnReasonId")}, true)?if_exists/>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="left"  font-size="12pt" keep-together="always" white-space-collapse="false">${returnReason.description?if_exists}</fo:block>  
	                       			</fo:table-cell>
	                        		<fo:table-cell>
	                            		<fo:block  text-align="left"  font-size="12pt" white-space-collapse="false">${returnItem.get("userLoginId")?if_exists}</fo:block>  
	                        		</fo:table-cell>
                				</fo:table-row>
                		</#list>
                		<fo:table-row>
	                       		    <fo:table-cell>
	                            			<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
	                       			</fo:table-cell>
		                            <fo:table-cell>
	                            			<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block>   
	                       			</fo:table-cell>
	                       		</fo:table-row>
	                       		<fo:table-row>
	                       		    <fo:table-cell>
	                            			<fo:block  keep-together="always" text-align="left" font-weight="bold" font-size="12pt" white-space-collapse="false"></fo:block>  
	                       			</fo:table-cell>
		                            <fo:table-cell>
	                            			<fo:block  keep-together="always" text-align="left" font-weight="bold" font-size="12pt" white-space-collapse="false">Total</fo:block>  
	                       			</fo:table-cell>
	                       		</fo:table-row>
                		<#assign productDetails = productReturnMap.entrySet()>
                	      <#list productDetails as prodTotals>
                	      	<#assign product = delegator.findOne("Product", {"productId" : prodTotals.getKey()}, true)?if_exists/>
		                       <fo:table-row>
		                            <fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="left"  white-space-collapse="false"></fo:block>  
	                       			</fo:table-cell>
                    				<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="left"   font-size="12pt" white-space-collapse="false">${product.brandName}</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="right"   font-size="12pt" white-space-collapse="false">${(prodTotals.getValue().get("returnQuantity"))?if_exists}</fo:block>  
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
	            	${uiLabelMap.NoOrdersFound}.
	       		 </fo:block>
		</fo:flow>
	</fo:page-sequence>	
    </#if>  
</fo:root>
</#escape>