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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-right=".3in" margin-top=".3in" margin-bottom="0.5in">
                <fo:region-body margin-top="0.5in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
      <#if finAccList?has_content> 	    
        ${setRequestAttribute("OUTPUT_FILENAME", "NewOrTerminatedRetailerReport.txt")}
        <fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">		
        <fo:static-content flow-name="xsl-region-before">
              		<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"> &#160;${uiLabelMap.CommonPage}- <fo:page-number/></fo:block>
              		<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
            </fo:static-content>	
              	
		        	<fo:flow flow-name="xsl-region-body"  font-family="Courier,monospace">	
		        	<fo:block text-align="center"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">     KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD</fo:block>
                    	<fo:block text-align="center"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">    UNIT : MOTHER DAIRY , G.K.V.K POST : YELAHANKA, BANGALORE -560065.</fo:block>
                    	<fo:block text-align="center"  font-family="Courier,monospace" font-weight="bold"  white-space-collapse="false">  FUND POSITION AS ON ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")} </fo:block>
                    	<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
              			<fo:block text-align="left"  keep-together="always"  white-space-collapse="false">==========================================================================================</fo:block> 
		        		<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">FIN ACCOUNT  NAME   	   &#160;&#160;&#160; &#160;&#160;&#160; &#160;&#160;&#160; &#160;&#160;&#160; &#160;&#160;&#160;  Actual Balance		   Available Balance   </fo:block> 
		        		<fo:block text-align="left"  keep-together="always"   white-space-collapse="false">==========================================================================================</fo:block> 
            	<fo:block>
                 	<fo:table>
                    <fo:table-column column-width="400pt"/>
                    <fo:table-column column-width="20pt"/> 
            		<fo:table-column column-width="160pt"/> 
            		<fo:table-column column-width="30pt"/> 	
                    <fo:table-body>
                    <#assign actualTotal = 0>
                    <#assign availableTotal = 0>
                    <#list finAccList as finAcc>
                    <#assign actualBalance =finAcc.actualBalance>
                     <#assign availableBalance =finAcc.availableBalance>
                    <#assign actualTotal=actualTotal+actualBalance>
                     <#assign availableTotal=availableTotal+finAcc.availableBalance>
					<fo:table-row>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="12pt" text-align="left" white-space-collapse="false">${finAcc.finAccountName}</fo:block>  
                       		</fo:table-cell>
                       		<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${finAcc.actualBalance?if_exists}</fo:block>  
                       		</fo:table-cell>
                   			<fo:table-cell>
                       			<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${finAcc.availableBalance?if_exists}</fo:block>  
                   			</fo:table-cell>
						</fo:table-row>
						</#list>
						 <fo:table-row>
						<fo:table-cell>
		            		<fo:block  keep-together="always">------------------------------------------------------------------------------------------</fo:block>  
		            	</fo:table-cell>
				        </fo:table-row>
						<fo:table-row>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" text-align="left" font-weight="bold" white-space-collapse="false">Total</fo:block>  
                       		</fo:table-cell>
                       		<fo:table-cell>
                           		<fo:block  keep-together="always" text-align="right" white-space-collapse="false">${actualTotal}</fo:block>  
                       		</fo:table-cell>
                   			<fo:table-cell>
                       			<fo:block  keep-together="always" text-align="right" white-space-collapse="false">${availableTotal}</fo:block>  
                   			</fo:table-cell>
						</fo:table-row>
				        <fo:table-row>
						<fo:table-cell>
		            		<fo:block  keep-together="always">------------------------------------------------------------------------------------------</fo:block>  
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