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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="12in" margin-left=".5in" margin-right=".5in" >
                <fo:region-body margin-top="1.6in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
     </fo:layout-master-set>
     <#if routeWiseTotalMap?has_content>
     	<#assign routeWiseTotalList =routeWiseTotalMap.entrySet()>
     	<fo:page-sequence master-reference="main">
     		<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        		<fo:block text-align="center" keep-together="always" white-space-collapse="false" font-weight="bold">SUPRAJA DAIRY PRIVATE LIMITED</fo:block>
        		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				<fo:block text-align="center" keep-together="always" white-space-collapse="false" >DAY WISE STATEMENT  OF LEAKS , FREES, SAMPLES, AUTO SHORT, VAN SHORT AND SPOILAGES</fo:block> 		
            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
            	<fo:block keep-together="always" white-space-collapse="false">&#160;         FROM DATE : ${parameters.fromDate}          TO DATE : ${parameters.thruDate}</fo:block>
            	<fo:block>-----------------------------------------------------------------------------------------------------------</fo:block>
            	<fo:block keep-together="always" white-space-collapse="false" font-size="10pt">&#160;    ROUTE                LEAKS           FREES        SAMPLES       AUTO SHORTAGE    VAN SHORTAGE     SPOILAGE     TOTAL  </fo:block>
        		<fo:block>-----------------------------------------------------------------------------------------------------------</fo:block>
        	  </fo:static-content>
     			<fo:flow flow-name="xsl-region-body" font-family="Helvetica" font-size="8pt">       
     				<fo:block   > 
     					<fo:table>
                   			<fo:table-column column-width="90pt"/>                
                    		<fo:table-column column-width="90pt"/>
                    		<fo:table-column column-width="90pt"/>
                    		<fo:table-column column-width="90pt"/>
                    		<fo:table-column column-width="90pt"/>
                    		<fo:table-column column-width="90pt"/>
                    		<fo:table-column column-width="90pt"/>
                    		<fo:table-column column-width="90pt"/>
                    		<fo:table-column column-width="90pt"/> 
                   		    <fo:table-body>                   		    
                   		    	 <#list routeWiseTotalList as routeTotal>
                   		    	<#assign routeDayWiseSales =routeTotal.getValue().entrySet()>
                   		    	<fo:table-row> 
                   		    	<fo:table-cell>
                    					<fo:block keep-together="always"   text-align="center" white-space-collapse="false" >${routeTotal.getKey()}</fo:block>
                    			</fo:table-cell>
                    			<#list routeDayWiseSales as dayWiseSales>
                    			<fo:table-cell>
                    					<fo:block keep-together="always"   text-align="right" white-space-collapse="false" >${dayWiseSales.getValue()}</fo:block>
                    			</fo:table-cell>
                    			</#list>
                    			</fo:table-row>
                    			<fo:table-row> 
                    		    <fo:table-cell>
                    			<fo:block>-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                    			 </fo:table-cell>
                    			 </fo:table-row>
                    			 </#list>
                    			 <#if grandTotalMap?has_content>
                    			 <#assign routeDayWiseSales =grandTotalMap.entrySet()>
                   		    	<fo:table-row> 
                   		    	<fo:table-cell>
                    					<fo:block keep-together="always"   text-align="center" white-space-collapse="false" font-weight="bold">GRAND TOTAL</fo:block>
                    			</fo:table-cell>
                    			<#list routeDayWiseSales as dayWiseSales>
                    			<fo:table-cell>
                    					<fo:block keep-together="always"   text-align="right" white-space-collapse="false" font-weight="bold" >${dayWiseSales.getValue()}</fo:block>
                    			</fo:table-cell>
                    			</#list>
                    			</fo:table-row>
                    			 </#if>
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