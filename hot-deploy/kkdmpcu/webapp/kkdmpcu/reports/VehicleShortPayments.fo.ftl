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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in">
                <fo:region-body margin-top=".5in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
     </fo:layout-master-set>
	<fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before">
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">.          KRISHNAVENI  KRISHNA DISTRICT MILK PRODUCERS COOP.UNION LTD</fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold">.                Cash Realisation Statement ON:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate , "dd/MM/yyyy")}</fo:block> 		
            	<fo:block>----------------------------------------------------------------------</fo:block>
        	</fo:static-content>
     	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">  
     			<fo:block font-family="Courier,monospace"  font-size="10pt">                
                <#if routeWisePaidMap?has_content>
                <fo:table>
                    <fo:table-column column-width="80pt"/>
                    <fo:table-column column-width="90pt"/>                
                    <fo:table-column column-width="110pt"/>
                    <fo:table-column column-width="90pt"/>
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="80pt"/>                   
                    <fo:table-header>
                    	<fo:table-cell><fo:block keep-together="always" text-align="center">ROUTE</fo:block></fo:table-cell>
		            	<fo:table-cell><fo:block keep-together="always" text-align="center">TotalReceivedAmt</fo:block></fo:table-cell>
		            	<fo:table-cell><fo:block keep-together="always" text-align="center">VehicleShortAmt</fo:block></fo:table-cell>
		                <fo:table-cell><fo:block keep-together="always" text-align="left"> Net Amount</fo:block></fo:table-cell>                 		            
		           	</fo:table-header>		           
                    <fo:table-body>
                    	<fo:table-row><fo:table-cell><fo:block>----------------------------------------------------------------------</fo:block></fo:table-cell></fo:table-row>        
                    	<#assign routeWisePaidAmountList = routeWisePaidMap.entrySet()>
                    	<#list routeWisePaidAmountList as routeWisePaidAmount>
                    	
                    	<fo:table-row>
	                    	<fo:table-cell>	
	                        	<fo:block text-align="center">${routeWisePaidAmount.getKey()}</fo:block>                               
	                        </fo:table-cell>
	                        <#assign routeWiseAmtList = routeWisePaidAmount.getValue().entrySet()>
	                        <#list routeWiseAmtList as routeWiseAmt>
	                        <fo:table-cell>	
	                        	<fo:block text-align="right">${routeWiseAmt.getValue()}</fo:block>                               
	                        </fo:table-cell>
	                        </#list>	  	                            	                            
	                    </fo:table-row>
	                    </#list>
                    </fo:table-body>
                </fo:table>
                </#if>
              </fo:block>
              <fo:block>----------------------------------------------------------------------</fo:block>
              <#if GrTotalValueMap?has_content>
              <fo:block font-family="Courier,monospace"  font-size="10pt">
               	<fo:table>
                    <fo:table-column column-width="80pt"/>
                    <fo:table-column column-width="40pt"/>                
                    <fo:table-column column-width="75pt"/>
                    <fo:table-column column-width="60pt"/>
                	<fo:table-body>
                		<fo:table-row>
                			<fo:table-cell><fo:block text-align="left">ToTals:</fo:block></fo:table-cell>
                			<fo:table-cell><fo:block text-align="right">${GrTotalValueMap.get("TOTAL_RECBLE")}</fo:block></fo:table-cell>
                			<fo:table-cell><fo:block text-align="right">${GrTotalValueMap.get("TRNSPTDUEAMT")}</fo:block></fo:table-cell>
                			<fo:table-cell><fo:block text-align="right">${GrTotalValueMap.get("NETVALUE")}</fo:block></fo:table-cell>
                		</fo:table-row>
                	</fo:table-body>
                </fo:table>    
              </fo:block>
              </#if>
              <fo:block>----------------------------------------------------------------------</fo:block>
    	</fo:flow>
    </fo:page-sequence>
</fo:root>
</#escape>