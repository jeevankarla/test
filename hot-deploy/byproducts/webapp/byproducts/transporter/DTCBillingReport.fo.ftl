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
${setRequestAttribute("OUTPUT_FILENAME", "DTCBillingReport.txt")}
 <#if facilityPartyMap?has_content> 
 <#assign serialNo = 1>	
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
			    <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"> &#160;${uiLabelMap.CommonPage}- <fo:page-number/></fo:block>
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
            <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">${uiLabelMap.KMFDairySubHeader}</fo:block>
                    <fo:block  text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold">DTC CONTRACTORS AGREEMENT ENDING FOR THE MONTH OF ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "MMMMM-yyyy")).toUpperCase()}</fo:block>
              		<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
              		<fo:block font-size="10pt">------------------------------------------------------------------------------------------------------------------</fo:block>
              		<fo:block font-weight="bold" font-size="9pt">SINO     &#160;&#160;&#160;ROUTE  &#160;&#160;&#160;&#160;&#160;&#160;CONTRACTOR&#160;&#160; &#160;&#160;CONTRACTOR&#160;&#160; &#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;AGREEMENT&#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;CONTRACTOR</fo:block>
              		<fo:block font-weight="bold" font-size="9pt">&#160; &#160; &#160;&#160;&#160;&#160;NO  &#160;&#160;&#160;&#160; &#160;&#160;&#160; CODE &#160;&#160; &#160;&#160; &#160; &#160; NAME&#160;&#160; &#160;&#160;&#160;&#160; &#160;&#160; &#160; &#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;REFERENCE NO&#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;END DATE</fo:block>
              		<fo:block font-size="10pt">------------------------------------------------------------------------------------------------------------------</fo:block>
            	<fo:block>
                    <fo:table>
				    <fo:table-column column-width="6%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="12%"/>
			        <fo:table-column column-width="26%"/>
			        <fo:table-column column-width="35%"/>
			        <fo:table-column column-width="15%"/>
                    <fo:table-body>
                    <#assign routeList = facilityPartyMap.entrySet()>
                    	<#list routeList as facilityParty>
                			<fo:table-row>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="left" font-size="9pt" white-space-collapse="false">${serialNo}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="left" font-size="9pt" white-space-collapse="false">${facilityParty.getKey()}</fo:block>  
                    			</fo:table-cell>
                    			  <#assign partyList = facilityParty.getValue().entrySet()>
                    			  <#list partyList as party>
                    			  <fo:table-cell>
                        			<fo:block  keep-together="always" text-align="left" font-size="9pt" white-space-collapse="false">${party.getKey()}</fo:block>  
                    			  </fo:table-cell>
                    			  <fo:table-cell>
                        			<fo:block  keep-together="always" text-align="left" font-size="9pt" white-space-collapse="false">${party.getValue().get("partyName")}</fo:block>  
                    			  </fo:table-cell>
                    			  <fo:table-cell>
                        			<fo:block  keep-together="always" text-align="left" font-size="9pt" white-space-collapse="false">${facilityWorkOrdrNumMap.get(facilityParty.getKey())?if_exists}</fo:block>  
                    			  </fo:table-cell>
                    			   <fo:table-cell>
                        			<fo:block  keep-together="always" text-align="left" font-size="9pt" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(party.getValue().get("thruDate"), "dd/MM/yy")}</fo:block>  
                    			  </fo:table-cell>
                    			  </#list>
                        	</fo:table-row>
	                        <#assign serialNo =serialNo+1>
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