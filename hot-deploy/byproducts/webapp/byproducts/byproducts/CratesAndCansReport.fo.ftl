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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".5in" margin-right=".3in" margin-bottom=".3in" margin-top=".5in">
        <fo:region-body margin-top="0.2in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "prdctRetrnReport.txt")}
 <#if routeWiseSaleMap?has_content> 
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
			    <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"> &#160;${uiLabelMap.CommonPage}- <fo:page-number/></fo:block>
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
            <#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
            <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
            	
            <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">${reportHeader.description?if_exists}.</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">${reportSubHeader.description?if_exists}.</fo:block>
                    <fo:block  text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold">CANS AND CRATES ACCOUNT</fo:block>
                    <fo:block  text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold">From :: ${dayBegin?if_exists}  To:: ${dayEnd?if_exists}</fo:block>
              		<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
              		<fo:block font-size="10pt">-----------------------------------------------------------------------------------------------------------</fo:block>
              		<fo:block font-weight="bold" font-size="10pt">Route     &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Crates  		          &#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Crates   								&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;                 Difference         &#160;&#160;&#160; &#160;&#160;&#160;Cans   &#160;&#160;&#160;&#160;&#160;  &#160;&#160;&#160;&#160;Cans&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Difference</fo:block>
              		<fo:block font-weight="bold" font-size="10pt">Code&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Sent	&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; &#160;&#160;Returned   					&#160;&#160;&#160;&#160;&#160; &#160; &#160;&#160;&#160;                       &#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Sent  &#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;Returned&#160;&#160;&#160;&#160;&#160;</fo:block>
            		<fo:block font-size="10pt">-----------------------------------------------------------------------------------------------------------</fo:block>
            	<fo:block>
                    <fo:table>
				    <fo:table-column column-width="8%"/>
			        <fo:table-column column-width="14%"/>
			        <fo:table-column column-width="18%"/>
			        <fo:table-column column-width="15%"/>
			        <fo:table-column column-width="12%"/>
			        <fo:table-column column-width="16%"/>
			        <fo:table-column column-width="13%"/>
			        <fo:table-column column-width="13%"/>
                    <fo:table-body>
                    	<#assign routeWiseSaleMap = routeWiseSaleMap.entrySet()>
                    	<#assign cratesReceived=0>
                    	<#assign cansReceived=0>
                    	<#assign cratesSentTot=0>
                    	<#assign cratesReceivedTot=0>
                    	<#assign cratesDiffTot=0>
                    	<#assign cansDiffTot=0>
                    	<#assign cansSentTot=0>
                    	<#assign cansReceivedTot=0>
                   <#list routeWiseSaleMap as routeWiseSaleDetails>
                    	<#assign cratesDiff=0>
                    	<#assign cansDiff=0>
                    	<#assign saleDetails =routeWiseSaleDetails.getValue()>
                		<#assign cratesReceived=saleDetails.get("cratesReceived")?if_exists>
                    	<#assign cansReceived=saleDetails.get("cansReceived")?if_exists>
	                    	   <#assign cratesDiff =saleDetails.get("cratesReceived")-saleDetails.get("cratesSent")>
	                    	   <#assign cansDiff =saleDetails.get("cansReceived")-saleDetails.get("cansSent")>
                    	<#assign cratesSentTot=cratesSentTot+saleDetails.get("cratesSent")>
                    	<#assign cratesReceivedTot=cratesReceivedTot+saleDetails.get("cratesReceived")>
                    				<#assign cratesDiffTot=cratesDiffTot+cratesDiff>
                    	<#assign cansSentTot=cansSentTot+saleDetails.get("cansSent")>
                    	<#assign cansReceivedTot=cansReceivedTot+saleDetails.get("cansReceived")>
                    				<#assign cansDiffTot=cansDiffTot+cansDiff>
                			<fo:table-row>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${routeWiseSaleDetails.getKey()?if_exists}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${saleDetails.get("cratesSent")?if_exists}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${saleDetails.get("cratesReceived")?if_exists}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${cratesDiff}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${saleDetails.get("cansSent")?if_exists}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${saleDetails.get("cansReceived")?if_exists}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${cansDiff}</fo:block>  
                    			</fo:table-cell>
                        	</fo:table-row>
	                  			<fo:table-row>
	                             <fo:table-cell>
	                                  <fo:block font-size="10pt">-----------------------------------------------------------------------------------------------------------</fo:block>
	                            </fo:table-cell>
	                         </fo:table-row>
	                 </#list>
	                     <fo:table-row>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="left" font-size="10pt" font-weight="bold" white-space-collapse="false">Grand Total:</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${cratesSentTot}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${cratesReceivedTot}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${cratesDiffTot}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${cansSentTot}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${cansReceivedTot}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${cansDiffTot}</fo:block>  
                    			</fo:table-cell>
                        	</fo:table-row>
	                  			<fo:table-row>
	                             <fo:table-cell>
	                                  <fo:block font-size="10pt">----------------------------------------------------------------------------------------------------------</fo:block>
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
</fo:root>
</#escape>