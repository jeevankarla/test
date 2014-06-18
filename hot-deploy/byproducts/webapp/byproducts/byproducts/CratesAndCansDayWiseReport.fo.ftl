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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-right=".3in" margin-bottom=".3in" margin-top=".5in">
        <fo:region-body margin-top="0.2in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "prdctRetrnReport.txt")}
 <#if routeWiseCratesMap?has_content> 
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
			    <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"> &#160;${uiLabelMap.CommonPage}- <fo:page-number/></fo:block>
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
            <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">${uiLabelMap.KMFDairySubHeader}</fo:block>
                    <fo:block  text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold">CANS AND CRATES ACCOUNT</fo:block>
                    <fo:block  text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold">From :: ${dayBegin?if_exists}  To:: ${dayEnd?if_exists}</fo:block>
              		<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
              		<fo:block font-size="10pt">-------------------------------------------------------------------------------------------------------------</fo:block>
              		<fo:block font-weight="bold" font-size="10pt">Route     &#160;Dispatch     &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Crates  		          &#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;Crates   								&#160;&#160;&#160;&#160;&#160;                 Difference         &#160;&#160;&#160;  Cans   &#160;&#160;&#160;&#160;&#160;  Cans&#160;&#160;&#160;&#160;&#160;&#160;&#160;Difference</fo:block>
              		<fo:block font-weight="bold" font-size="10pt">Code&#160;&#160;&#160;Date	&#160; &#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; Sent	&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; Returned   					&#160;&#160;&#160;&#160;&#160; &#160;                         &#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Sent  &#160;&#160;&#160;&#160;&#160;  Returned&#160;&#160;&#160;&#160;</fo:block>
            		<fo:block font-size="10pt">-------------------------------------------------------------------------------------------------------------</fo:block>
            	<fo:block>
                    <fo:table>
				    <fo:table-column column-width="6%"/>
			        <fo:table-column column-width="11%"/>
			        <fo:table-column column-width="13%"/>
			        <fo:table-column column-width="18%"/>
			        <fo:table-column column-width="11%"/>
			        <fo:table-column column-width="11%"/>
			        <fo:table-column column-width="12%"/>
			        <fo:table-column column-width="12%"/>
                    <fo:table-body>
                    	<#assign routeWiseSaleMap = routeWiseCratesMap.entrySet()>
                    	<#assign cratesSentGTot=0>
                    	<#assign cratesReceivedGTot=0>
                    	<#assign cratesDiffGTot=0>
                    	<#assign cansDiffGTot=0>
                    	<#assign cansSentGTot=0>
                    	<#assign cansReceivedGTot=0>
                    	<#list routeWiseSaleMap as routeWiseSaleDetails>
                    	 <fo:table-row>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" font-weight="bold" text-align="left" font-size="10pt" white-space-collapse="false">${routeWiseSaleDetails.getKey()?if_exists}</fo:block>  
                    			</fo:table-cell>
                        	</fo:table-row>
                    	<#assign saleDetails =routeWiseSaleDetails.getValue().entrySet()>
                    	<#assign cratesDiff=0>
                    	<#assign cansDiff=0>
                    	<#assign cratesReceived=0>
                    	<#assign cansReceived=0>
                    	<#assign cratesSentTot=0>
                    	<#assign cratesReceivedTot=0>
                    	<#assign cratesDiffTot=0>
                    	<#assign cansDiffTot=0>
                    	<#assign cansSentTot=0>
                    	<#assign cansReceivedTot=0>
                    <#list saleDetails as sale>
                    	<#assign cratesReceived=sale.getValue().get("cratesReceived")?if_exists>
                    	<#assign cansReceived=sale.getValue().get("cansReceived")?if_exists>
	                    	<#if cratesReceived &gt;0>
	                    	     <#assign cratesDiff =sale.getValue().get("cratesSent")-sale.getValue().get("cratesReceived")>
	                    	</#if>
	                    	<#if cansReceived &gt; 0>
	                    	      <#assign cansDiff =sale.getValue().get("cansSent")-sale.getValue().get("cansReceived")>
	                    	</#if>
                    	
                    	<#assign cratesSentTot=cratesSentTot+sale.getValue().get("cratesSent")>
                    	<#assign cratesReceivedTot=cratesReceivedTot+sale.getValue().get("cratesReceived")>
			                   <#if cratesReceivedTot &gt;0>
                    				<#assign cratesDiffTot=cratesDiffTot+cratesDiff>
                    	      </#if>
                    	<#assign cansSentTot=cansSentTot+sale.getValue().get("cansSent")>
                    	<#assign cansReceivedTot=cansReceivedTot+sale.getValue().get("cansReceived")>
                    		 <#if cansReceivedTot &gt;0>
                    				<#assign cansDiffTot=cansDiffTot+cansDiff>
                    	      </#if>
                    	
                			<fo:table-row>
                				<fo:table-cell>
                    			<fo:block keep-together="always" text-align="left"    white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(sale.getKey(), "dd-MMMMM-yyyy")}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${sale.getValue().get("cratesSent")?if_exists}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${sale.getValue().get("cratesReceived")?if_exists}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${cratesDiff}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${sale.getValue().get("cansSent")?if_exists}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${sale.getValue().get("cansReceived")?if_exists}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${cansDiff}</fo:block>  
                    			</fo:table-cell>
                        	</fo:table-row>
	                    </#list>
	                    <#assign cratesSentGTot=cratesSentGTot+cratesSentTot>
                    	<#assign cratesReceivedGTot=cratesReceivedGTot+cratesReceivedTot>
			                   <#if cratesReceivedGTot &gt;0>
                    				<#assign cratesDiffGTot=cratesDiffGTot+cratesDiff>
                    	      </#if>
                    	<#assign cansSentGTot=cansSentGTot+cansSentTot>
                    	<#assign cansReceivedGTot=cansReceivedGTot+cansReceivedTot>
                    		 <#if cansReceivedGTot &gt;0>
                    				<#assign cansDiffGTot=cansDiffGTot+cansDiff>
                    	      </#if>
	                    	<fo:table-row>
	                             <fo:table-cell>
	                                  <fo:block font-size="10pt">------------------------------------------------------------------------------------------------------------</fo:block>
	                            </fo:table-cell>
	                        </fo:table-row>
	                       <fo:table-row>
                				<fo:table-cell>
                    			<fo:block keep-together="always" text-align="left"    font-weight="bold" white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="left" font-weight="bold" font-size="10pt" white-space-collapse="false">&#xA;</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-weight="bold" font-size="10pt" white-space-collapse="false">${cratesSentTot}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-weight="bold" font-size="10pt" white-space-collapse="false">${cratesReceivedTot}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-weight="bold" font-size="10pt" white-space-collapse="false">${cratesDiffTot}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-weight="bold" font-size="10pt" white-space-collapse="false">${cansSentTot}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-weight="bold" font-size="10pt" white-space-collapse="false">${cansReceivedTot}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-weight="bold" font-size="10pt" white-space-collapse="false">${cansDiffTot}</fo:block>  
                    			</fo:table-cell>
                        	</fo:table-row>  
                        	<fo:table-row>
	                             <fo:table-cell>
	                                  <fo:block font-size="10pt">------------------------------------------------------------------------------------------------------------</fo:block>
	                            </fo:table-cell>
	                        </fo:table-row>
	                     </#list>
	                    
	                     <fo:table-row>
	                             <fo:table-cell>
	                                  <fo:block font-size="10pt">------------------------------------------------------------------------------------------------------------</fo:block>
	                            </fo:table-cell>
	                        </fo:table-row>
	                       <fo:table-row>
                				<fo:table-cell>
                    			<fo:block keep-together="always" text-align="left"    font-weight="bold" white-space-collapse="false" linefeed-treatment="preserve">Grand Total :</fo:block> 
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="left" font-weight="bold" font-size="10pt" white-space-collapse="false">&#xA;</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-weight="bold" font-size="10pt" white-space-collapse="false">${cratesSentGTot}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-weight="bold" font-size="10pt" white-space-collapse="false">${cratesReceivedGTot}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-weight="bold" font-size="10pt" white-space-collapse="false">${cratesDiffGTot}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-weight="bold" font-size="10pt" white-space-collapse="false">${cansSentGTot}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-weight="bold" font-size="10pt" white-space-collapse="false">${cansReceivedGTot}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-weight="bold" font-size="10pt" white-space-collapse="false">${cansDiffGTot}</fo:block>  
                    			</fo:table-cell>
                        	</fo:table-row>  
                        	<fo:table-row>
	                             <fo:table-cell>
	                                  <fo:block font-size="10pt">------------------------------------------------------------------------------------------------------------</fo:block>
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