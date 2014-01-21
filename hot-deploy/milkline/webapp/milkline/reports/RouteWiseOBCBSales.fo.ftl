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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in" margin-left=".4in" margin-right=".2in">
                <fo:region-body margin-top="1.2in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
     </fo:layout-master-set>
     	<#if allRoutesList?has_content>
		<fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        		<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="15pt" font-weight="bold">&#160;                   ${reportHeader.description?if_exists}</fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold">&#160;                        ROUTEWISE O.B,C.B &amp; SALE, RECEIPT PARTICULARS</fo:block> 		
            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
            	<fo:block keep-together="always" white-space-collapse="false">&#160;      FROM DATE : ${parameters.fromDate}          TO DATE : ${parameters.thruDate}</fo:block>
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">        				
     				<fo:block font-family="Courier,monospace"> 
     					<fo:table>
                    		<fo:table-column column-width="90pt"/>
                   			<fo:table-column column-width="90pt"/>                
                    		<fo:table-column column-width="100pt"/>
                    		<fo:table-column column-width="100pt"/>
                    		<fo:table-column column-width="100pt"/>
                    		<fo:table-column column-width="100pt"/>
                    		<fo:table-column column-width="100pt"/>
                    		<fo:table-column column-width="70pt"/>
                    		<fo:table-column column-width="90pt"/>
                    		<fo:table-column column-width="100pt"/>      
                    		<fo:table-header>
                    			<fo:table-cell><fo:block border-style="solid" text-align="center" font-weight="bold">ROUTE</fo:block></fo:table-cell>
                    			<fo:table-cell><fo:block border-style="solid" text-align="center" font-weight="bold">O.B</fo:block></fo:table-cell>
                    			<fo:table-cell><fo:block border-style="solid" text-align="center" font-weight="bold">C.B</fo:block></fo:table-cell>
                    			<fo:table-cell><fo:block border-style="solid" text-align="center" font-weight="bold">VARIATION</fo:block></fo:table-cell>
                    			<fo:table-cell><fo:block border-style="solid" keep-together="always" font-weight="bold" text-align="center">SALE VALUE</fo:block></fo:table-cell>
                    			<fo:table-cell><fo:block border-style="solid" text-align="center" font-weight="bold">COLLECTED</fo:block></fo:table-cell>
                    			<fo:table-cell><fo:block border-style="solid" text-align="center" font-weight="bold">VARIATION</fo:block></fo:table-cell>
                    		</fo:table-header>             
                   		    <fo:table-body>   
                   		    <#assign grTotOpening = (Static["java.math.BigDecimal"].ZERO)>
                   		    <#assign grTotSale = (Static["java.math.BigDecimal"].ZERO)>
                   		    <#assign grTotRecpts = (Static["java.math.BigDecimal"].ZERO)>
                   		    <#assign grTotClosing = (Static["java.math.BigDecimal"].ZERO)>
     						<#list abstRouteList as allRoutes>   						
                    				<#assign grTotOpening = grTotOpening.add(allRoutes.get("totOpeningAmt"))>
                    				<#assign grTotSale = grTotSale.add(allRoutes.get("totSaleAmt"))>
                    				<#assign grTotRecpts = grTotRecpts.add(allRoutes.get("totRecpts"))>
                    				<#assign grTotClosing = grTotClosing.add(allRoutes.get("totClosing"))>                    				
                    			<fo:table-row>
                    				<#assign routeDetails = delegator.findOne("Facility", {"facilityId" : allRoutes.get("route")}, true)>
                    				<fo:table-cell><fo:block keep-together="always" white-space-collapse="false" border-style="solid">${routeDetails.get("facilityName")?if_exists}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right" border-style="solid">${allRoutes.get("totOpeningAmt").toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right" border-style="solid">${allRoutes.get("totClosing").toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right" border-style="solid">${(allRoutes.get("totOpeningAmt")-allRoutes.get("totClosing"))?if_exists?string("##0.00#")}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right" border-style="solid">${allRoutes.get("totSaleAmt").toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right" border-style="solid">${allRoutes.get("totRecpts").toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right" border-style="solid">${(allRoutes.get("totSaleAmt")-allRoutes.get("totRecpts"))?if_exists?string("##0.00#")}</fo:block></fo:table-cell>
                    			</fo:table-row>                   			
                    		</#list>            				
                   				<fo:table-row>
                    				<fo:table-cell><fo:block keep-together="always" white-space-collapse="false" border-style="solid" font-weight="bold">TOTAL</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right" border-style="solid">${grTotOpening.toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right" border-style="solid">${grTotClosing.toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right" border-style="solid">${(grTotOpening-grTotClosing)?if_exists?string("##0.00#")}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right" border-style="solid">${grTotSale.toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right" border-style="solid">${grTotRecpts.toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right" border-style="solid">${(grTotSale-grTotRecpts)?if_exists?string("##0.00#")}</fo:block></fo:table-cell>
                    			</fo:table-row>                    			
    						</fo:table-body>
                		</fo:table>
     				</fo:block>
     			</fo:flow>
    	</fo:page-sequence> 
    	</#if>   	
</fo:root>
</#escape>