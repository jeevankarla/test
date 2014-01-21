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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
                      margin-right=".5in">
                <fo:region-body margin-top="1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
         <#assign productQty = 0>
         ${setRequestAttribute("OUTPUT_FILENAME", "chlst.txt")}       
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before">
        		<fo:block text-align="left" white-space-collapse="false">${uiLabelMap.CommonPage}:<fo:page-number/>                 ${uiLabelMap.ApDairyMsg}</fo:block>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">.                                                          ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "MMM d, yyyy HH:mm:ss")}  UserLogin:${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((userLogin.get("userLoginId")))),12)}</fo:block>	 	 	  
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">.                                                            ${checkListType} CheckList</fo:block>
        		<#if checkListType == "changeindent">        			
        			<fo:block text-align="left" keep-together="always" white-space-collapse="false">.                                                          SupplyDate: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp,1), "MMM d, yyyy")} </fo:block>
        			<#else>
        			<fo:block text-align="left" keep-together="always" white-space-collapse="false">.                                                          SupplyDate: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "MMM d, yyyy")}</fo:block>
        		</#if>        		
        	</fo:static-content>
            <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
            	 <fo:block font-family="Courier,monospace" >                
                <fo:table border-width="1pt" border-style="dotted">
                    <fo:table-column column-width="80pt"/>
                    <fo:table-column column-width="65pt"/>  
               	<#if checkListType == "gatepass" || checkListType == "trucksheetcorrection">              
                    <fo:table-column column-width="100pt"/>
                </#if> 	
                    <fo:table-column column-width="30pt"/>
                    <fo:table-column column-width="45pt"/>
                    <fo:table-column column-width="45pt"/>                   
                <#if checkListType != "cardsale">
                   <#list productList as product>            
		             <fo:table-column column-width="45pt"/>      
		           </#list>
		         <#else>
		           <#list milkCardTypeList as milkCardType>            
		             <fo:table-column column-width="58pt"/>      
		           </#list>		            
                </#if>                    
		          	<fo:table-header border-width="1pt" border-style="dotted">
		            			<fo:table-cell padding="3pt" border-width="1pt" border-style="dotted"><fo:block text-align="center" white-space-treatment="preserve">User</fo:block></fo:table-cell>		                    	                  
		            			<fo:table-cell padding="3pt" border-width="1pt" border-style="dotted"><fo:block text-align="center" white-space-treatment="preserve">Time</fo:block></fo:table-cell>		                    	                  		            
		            	     <#if checkListType == "gatepass" || checkListType == "trucksheetcorrection">
		            			<fo:table-cell padding="3pt" border-width="1pt" border-style="dotted"><fo:block text-align="center" white-space-treatment="preserve">ShipmentType</fo:block></fo:table-cell>
		            		 </#if> 
		            		 <#if checkListType != "cardsale">	
		            			<fo:table-cell padding="3pt" border-width="1pt" border-style="dotted"><fo:block text-align="center" white-space-treatment="preserve">Type</fo:block></fo:table-cell>
		            		</#if> 		                    	                  
		                        <fo:table-cell padding="3pt" border-width="1pt" border-style="dotted"><fo:block text-align="center">Route</fo:block></fo:table-cell>
		                        <fo:table-cell padding="3pt" border-width="1pt" border-style="dotted"><fo:block text-align="center">Booth</fo:block></fo:table-cell>
		                     <#if checkListType != "cardsale">
			                    <#list productList as product>            
			                     <fo:table-cell border-width="1pt" border-style="dotted" width ="40pt">
			                       	<fo:block text-align="right"  padding="3pt" font-size="10pt" wrap-option="nowrap"  white-space-collapse="false">${product.brandName}</fo:block>
			                       	<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			                     </fo:table-cell>
			                    </#list>
			                  <#else>
			                      <#list milkCardTypeList as milkCardType>            
			                       	<fo:table-cell border-width="1pt" border-style="dotted">
			                       		<fo:block text-align="right"  padding="3pt" font-size="10pt" wrap-option="nowrap"  white-space-collapse="false">${milkCardType.name}</fo:block>
			                       		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			                       	</fo:table-cell>
			                       </#list>
			                  </#if> 
				         	</fo:table-header>		           
                    <fo:table-body>                    	
			           <#list checkListReportList as checkListReport>
	                        <fo:table-row border-width="1pt" border-style="dotted">
	                        	<fo:table-cell border-width="1pt" border-style="dotted">	
	                            	<fo:block text-align="center" text-indent="0.05in" padding="1pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((checkListReport.lastModifiedBy))),10)}</fo:block>                               
	                            </fo:table-cell>	
	                        	<fo:table-cell border-width="1pt" border-style="dotted">	
	                            	<fo:block text-align="right" text-indent="0.05in" padding="1pt">${checkListReport.lastModifiedDate}</fo:block>                               
	                            </fo:table-cell>	
	                          <#if checkListType == "gatepass" || checkListType == "trucksheetcorrection">	                                                    
	                        	<fo:table-cell border-width="1pt" border-style="dotted">	
	                            	<fo:block text-align="center" text-indent="0.05in" padding="1pt">${checkListReport.shipmentTypeId}</fo:block>                               
	                            </fo:table-cell>
	                          </#if>
	                          <#if checkListType != "cardsale">	
	                        	<fo:table-cell border-width="1pt" border-style="dotted">	
	                            	<fo:block text-align="right" text-indent="0.05in" padding="1pt">${checkListReport.supplyType}</fo:block>                               
	                            </fo:table-cell>
	                          </#if> 
	                            <fo:table-cell border-width="1pt" border-style="dotted">
	                            	<fo:block text-align="right" padding="1pt">${checkListReport.routeId?if_exists}</fo:block>	                               
	                            </fo:table-cell>	                            
	                            <fo:table-cell border-width="1pt" border-style="dotted">
	                            	<fo:block text-align="right" padding="1pt">${checkListReport.boothId}</fo:block>	                               
	                            </fo:table-cell>	  
	                           <#if checkListType != "cardsale">
		                         <#list productList as product>
		                           <#assign productQty = checkListReport[product.productId]>            
			                       		<fo:table-cell border-width="1pt" border-style="dotted"><fo:block text-align="right" padding="1pt" linefeed-treatment="preserve">${productQty}</fo:block></fo:table-cell>
			                       </#list>
			                   <#else>
			                     <#list  milkCardTypeList as milkCardType>
			                        <#assign productQty = checkListReport[milkCardType.milkCardTypeId]>            
				                       	<fo:table-cell border-width="1pt" border-style="dotted"><fo:block text-align="right" linefeed-treatment="preserve" padding="1pt">${productQty}</fo:block></fo:table-cell>
			                     </#list>	
			                   </#if>    	
	                        </fo:table-row>
	                          <fo:table-row border-width="1pt" border-style="dotted">
	                            <fo:table-cell border-width="1pt" border-style="dotted">
	                            	<fo:block text-align="center" padding="1pt"></fo:block>	                               
	                            </fo:table-cell>
	                            <fo:table-cell border-width="1pt" border-style="dotted">	
	                            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>                              
	                            </fo:table-cell>
	                             <#if checkListType != "cardsale">
		                            <#list productList as product>
		                            	<#assign productQty = checkListReport[product.productId]>            
			                       		<fo:table-cell border-width="1pt" border-style="dotted"><fo:block text-align="center" padding="1pt"></fo:block></fo:table-cell>
			                       	</#list>
			                       	<#else>
			                       		<#list  milkCardTypeList as milkCardType>
			                            	<#assign productQty = checkListReport[milkCardType.milkCardTypeId]>            
				                       		<fo:table-cell border-width="1pt" border-style="dotted"><fo:block text-align="center" padding="1pt"></fo:block></fo:table-cell>
			                       	</#list>
			                    </#if>   	
	                        </fo:table-row>
                       </#list> 
                    </fo:table-body>
                </fo:table>
                 </fo:block>
           </fo:flow>
        </fo:page-sequence>
     </fo:root>
</#escape>