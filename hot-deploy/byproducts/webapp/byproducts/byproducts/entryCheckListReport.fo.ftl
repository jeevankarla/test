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
            <fo:simple-page-master master-name="main" page-height="24in" page-width="96in"
                     margin-top="0.5in" margin-bottom="1in" margin-left="1in" margin-right="1in">
                <fo:region-body margin-top="1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>  
         <#assign productQty = 0>
         ${setRequestAttribute("OUTPUT_FILENAME", "chlst.txt")}       
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before">
        		<fo:block text-align="left" white-space-collapse="false">${uiLabelMap.CommonPage}:<fo:page-number/>                                                                ${uiLabelMap.aavinDairyMsg}</fo:block>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">.                                                                                                                              <#if checkListType = "indentEntry">Indent Entry<#else>Parlor Entry</#if> CheckList</fo:block>
        		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">.                                                                                 Entry Date: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp?if_exists, "MMM d, yyyy")}               UserLogin:${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((userLogin.get("userLoginId")))),12)}	 	 	  
        		</fo:block>        		
        	</fo:static-content>
            <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" font-size="12pt">
            	 <fo:block font-family="Courier,monospace"   >                
                <fo:table border-width="1pt" border-style="solid">
                    <fo:table-column column-width="60pt"   />
                    <fo:table-column column-width="65pt"/>  
               		<fo:table-column column-width="80pt"/>
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="45pt"/>
                    <fo:table-column column-width="45pt"/>                   
                <#if checkListType != "cardsale">
                   <#list productList as product>            
		             <fo:table-column column-width="45pt"/>      
		           </#list>
                </#if>                    
		          	<fo:table-header>
		            			<fo:table-cell padding="3pt" border-width="1pt" border-style="dotted"><fo:block  text-align="center" white-space-treatment="preserve">User</fo:block></fo:table-cell>
		            			<fo:table-cell padding="3pt" border-width="1pt" border-style="dotted"><fo:block   text-align="center" white-space-treatment="preserve">Time</fo:block></fo:table-cell>
		            			<fo:table-cell padding="3pt" border-width="1pt" border-style="dotted"><fo:block   text-align="center" white-space-treatment="preserve"><#if checkListType = "indentEntry">Supply Date<#else>Order Date</#if></fo:block></fo:table-cell>		                    	                  		            
		                        <fo:table-cell padding="3pt" border-width="1pt" border-style="dotted"><fo:block   text-align="center">Route</fo:block></fo:table-cell>
		                        <fo:table-cell padding="3pt" border-width="1pt" border-style="dotted"><fo:block   text-align="center">Party Code</fo:block></fo:table-cell>
		                     <#if checkListType != "cardsale">
			                    <#list productList as product>            
			                     <fo:table-cell border-width="1pt" border-style="dotted" width ="40pt">
			                       	<fo:block text-align="center"   padding="3pt" wrap-option="nowrap"  white-space-collapse="false">${product}</fo:block>
			                       	<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			                     </fo:table-cell>
			                    </#list>
			                 </#if> 
				         	</fo:table-header>		           
                    <fo:table-body>                    	
			           <#list checkListReportList as checkListReport>
	                        <fo:table-row border-width="1pt" border-style="dotted">
	                        	<fo:table-cell border-width="1pt" border-style="dotted">	
	                            	<fo:block text-align="center" text-indent="0.05in" padding="1pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((checkListReport.lastModifiedBy?if_exists))),10)}</fo:block>                               
	                            </fo:table-cell>	
	                            <fo:table-cell border-width="1pt" border-style="dotted">	
	                            	<fo:block text-align="right" text-indent="0.05in" padding="1pt">${checkListReport.lastModifiedDate?if_exists}</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell border-width="1pt" border-style="dotted">	
	                            	<fo:block text-align="center" text-indent="0.05in" padding="1pt">${checkListReport.supplyDate?if_exists}</fo:block>                               
	                            </fo:table-cell>
	                            
	                            <fo:table-cell border-width="1pt" border-style="dotted">
	                               	<fo:block text-align="center"  padding="1pt">${checkListReport.routeId?if_exists}</fo:block>	                               
	                            </fo:table-cell>
	                                                       
	                            <fo:table-cell border-width="1pt" border-style="dotted">
	                            	<fo:block text-align="center"  padding="1pt">${checkListReport.boothId?if_exists}</fo:block>	                               
	                            </fo:table-cell>	  
	                           <#if checkListType != "cardsale">
		                         <#list productList as product>
		                           <#assign productQty = checkListReport[product]>            
			                       		<fo:table-cell border-width="1pt" border-style="dotted"><fo:block   text-align="right" padding="1pt" linefeed-treatment="preserve">${productQty}</fo:block></fo:table-cell>
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
		                            	<#assign productQty = checkListReport[product]>            
			                       		<fo:table-cell border-width="1pt" border-style="dotted"><fo:block  text-align="center" padding="1pt"></fo:block></fo:table-cell>
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