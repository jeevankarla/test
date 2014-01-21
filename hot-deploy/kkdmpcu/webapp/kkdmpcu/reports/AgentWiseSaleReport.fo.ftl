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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in" >
                <fo:region-body margin-top="1.5in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
     </fo:layout-master-set>
     	<#if allRoutesList?has_content>     	
		<fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        		<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                        ${reportHeader.description?if_exists}</fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold">&#160;                        AGENTWISE SALE, RECEIPT AND DUES PARTICULARS</fo:block> 		
            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
            	<fo:block keep-together="always" white-space-collapse="false">.        FROM DATE : ${parameters.fromDate}          TO DATE : ${parameters.thruDate}</fo:block>
            	<fo:block>---------------------------------------------------------------------------------------------------------------</fo:block>
            	<fo:block keep-together="always" white-space-collapse="false">AGNTCODE    AGENT NAME       OPENING    SALE AMT    RECEIPTS    CLOSING BAL  SL.NO  VEH.DUE  TOT.DUE</fo:block>
        		<fo:block>---------------------------------------------------------------------------------------------------------------</fo:block>
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">        				
     				<#list allRoutesList as allRoutes>     	
     					<#assign agentPaymentDetails =allRoutes.entrySet()>
     				<fo:block > 
     					<fo:table>
                    		<fo:table-column column-width="40pt"/>
                   			<fo:table-column column-width="50pt"/>                
                    		<fo:table-column column-width="80pt"/>
                    		<fo:table-column column-width="80pt"/>
                    		<fo:table-column column-width="80pt"/>
                    		<fo:table-column column-width="85pt"/>
                    		<fo:table-column column-width="85pt"/>
                    		<fo:table-column column-width="120pt"/>
                    		<fo:table-column column-width="80pt"/>
                    		<fo:table-column column-width="80pt"/>                   
                   		    <fo:table-body>                   		    
                   		    	<#assign sno=0>   
                   		    	<#assign totOpnAmt =(Static["java.math.BigDecimal"].ZERO)>
                   		    	<#assign totSaleAmt =(Static["java.math.BigDecimal"].ZERO)>  
                   		    	<#assign totReceipts = (Static["java.math.BigDecimal"].ZERO)>  
                   		    	<#assign totClosing =  (Static["java.math.BigDecimal"].ZERO)>         		    	
                   		    <#list agentPaymentDetails as agentPaymentEntries>                   		    	
                   		    	<#if agentPaymentEntries.getKey() !="routeTotals">
                   		    		
                   		    		<#assign sno=sno+1>
                   		    		<#assign totOpnAmt= totOpnAmt.add(agentPaymentEntries.getValue().get("openingAmt"))>
                   		    		<#assign totSaleAmt = totSaleAmt.add(agentPaymentEntries.getValue().get("salesAmt"))>
                   		    		<#assign totReceipts = totReceipts.add(agentPaymentEntries.getValue().get("reciepts"))>
                   		    		<#assign totClosing = totClosing.add(agentPaymentEntries.getValue().get("closingAmt"))>
                   		    	<fo:table-row>                   		    		
                    				<fo:table-cell>                    					
                    					<fo:block>${agentPaymentEntries.getKey()}</fo:block>                    					
                    				</fo:table-cell>
                    				<fo:table-cell>
                    					<fo:block keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((agentPaymentEntries.getValue().get("agentName")?if_exists))),15)}</fo:block>
                    				</fo:table-cell>
                    				<fo:table-cell></fo:table-cell>
                    				<fo:table-cell>
                    					<fo:block keep-together="always" text-align="right">${agentPaymentEntries.getValue().get("openingAmt").toEngineeringString()?if_exists}</fo:block>
                    				</fo:table-cell>
                    				<fo:table-cell>
                    					<fo:block keep-together="always" text-align="right">${agentPaymentEntries.getValue().get("salesAmt").toEngineeringString()?if_exists}</fo:block>
                    				</fo:table-cell>
                    				<fo:table-cell>
                    					<fo:block keep-together="always" text-align="right">${agentPaymentEntries.getValue().get("reciepts").toEngineeringString()?if_exists}</fo:block>
                    				</fo:table-cell>
                    				<fo:table-cell>
                    					<fo:block keep-together="always" text-align="right">${agentPaymentEntries.getValue().get("closingAmt").toEngineeringString()?if_exists}</fo:block>
                    				</fo:table-cell>
                    				<fo:table-cell>
                    					<fo:block keep-together="always" text-align="center">${sno?if_exists}</fo:block>
                    				</fo:table-cell>                    				
                    			</fo:table-row>                     				
                    			</#if>
                    			<#if agentPaymentEntries.getKey() =="routeTotals">
                    				<#assign routeTotalEntries = agentPaymentEntries.getValue()>
                    				
                    			<fo:table-row>
                    				<fo:table-cell>
                    					<fo:block>==========================================================================================================</fo:block>
                    				</fo:table-cell>
                    			</fo:table-row>            				
                    			<fo:table-row>
                    				<fo:table-cell><fo:block keep-together="always" white-space-collapse="false">KKD ${routeTotalEntries.get("route")?if_exists}   TOTAL</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right"></fo:block></fo:table-cell>
                    				<fo:table-cell></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right">${totOpnAmt.toEngineeringString()?if_exists}*</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right">${totSaleAmt.toEngineeringString()?if_exists}*</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right">${totReceipts.toEngineeringString()?if_exists}*</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right">${totClosing.toEngineeringString()?if_exists}*</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right">${routeTotalEntries.get("TRNSPTDUE").toEngineeringString()?if_exists}*</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right">${(routeTotalEntries.get("TRNSPTDUE")).add((routeTotalEntries.get("totClosing"))).toEngineeringString()}*</fo:block></fo:table-cell>
                    			</fo:table-row>                    			 
                    			<fo:table-row>
                    				<fo:table-cell>
                    					<fo:block>==========================================================================================================</fo:block>
                    				</fo:table-cell>
                    			</fo:table-row>
                    				
                    			</#if>
                    			</#list>
                    		</fo:table-body>
                		</fo:table>
     				</fo:block>
     				</#list>
     			</fo:flow>
    	</fo:page-sequence>    	
    	</#if>
</fo:root>
</#escape>