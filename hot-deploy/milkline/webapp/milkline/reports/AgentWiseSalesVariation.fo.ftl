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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in" margin-left=".4in" margin-right=".4in">
                <fo:region-body margin-top="1.2in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
     </fo:layout-master-set>
     	<#if allRoutesList?has_content>
     	<#list allRoutesList as allRoutes>     	
		<fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        		<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold" font-size="15pt">&#160;                   ${reportHeader.description?if_exists}</fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold" >&#160;                        AGENT SALES AND VARIATION REPORT</fo:block> 		
            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
            	<fo:block keep-together="always" white-space-collapse="false" font-weight="bold">&#160;        FROM DATE : ${parameters.fromDate}          TO DATE : ${parameters.thruDate}</fo:block>
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">        				
     				     	
     					<#assign agentPaymentDetails =allRoutes.entrySet()>
     				<fo:block > 
     					<fo:table >
                    		<fo:table-column column-width="80pt"/>
                   			<fo:table-column column-width="170pt"/>                
                    		<fo:table-column column-width="110pt"/>
                    		<fo:table-column column-width="110pt"/>
                    		<fo:table-column column-width="180pt"/>
                    		<fo:table-column column-width="85pt"/>
                    		<fo:table-header>
                    			<fo:table-cell><fo:block border-style="solid" text-align="center" font-weight="bold">AGNTCODE</fo:block></fo:table-cell>
                    			<fo:table-cell><fo:block border-style="solid" text-align="center" font-weight="bold">AGENT NAME</fo:block></fo:table-cell>
                    			<fo:table-cell><fo:block border-style="solid" text-align="center" font-weight="bold">SALE AMT</fo:block></fo:table-cell>
                    			<fo:table-cell><fo:block border-style="solid" text-align="center" font-weight="bold">RECEIPTS</fo:block></fo:table-cell>
                    			<fo:table-cell><fo:block keep-together="always" border-style="solid" text-align="center" font-weight="bold">VARIATION OF SALE &amp; RECPT</fo:block></fo:table-cell>
                    		</fo:table-header>            
                   		    <fo:table-body>                   		    
                   		    	<#assign sno=0>   
                   		    	<#assign totOpnAmt =(Static["java.math.BigDecimal"].ZERO)>
                   		    	<#assign totSaleAmt =(Static["java.math.BigDecimal"].ZERO)>  
                   		    	<#assign totReceipts = (Static["java.math.BigDecimal"].ZERO)>  
                   		    	<#assign totClosing =  (Static["java.math.BigDecimal"].ZERO)>         		    	
                   		    <#list agentPaymentDetails as agentPaymentEntries>                   		    	
                   		    	<#if agentPaymentEntries.getKey() !="routeTotals">
                   		    		<#assign totSaleAmt = totSaleAmt.add(agentPaymentEntries.getValue().get("salesAmt"))>
                   		    		<#assign totReceipts = totReceipts.add(agentPaymentEntries.getValue().get("reciepts"))>
	                   		    	<fo:table-row>                   		    		
	                    				<fo:table-cell border-style="solid">                    					
	                    					<fo:block text-align="center">${agentPaymentEntries.getKey()}</fo:block>                    					
	                    				</fo:table-cell>
	                    				<fo:table-cell border-style="solid">
	                    					<fo:block keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((agentPaymentEntries.getValue().get("agentName")?if_exists))),15)}</fo:block>
	                    				</fo:table-cell>
	                    				<fo:table-cell border-style="solid">
	                    					<fo:block keep-together="always" text-align="right">${agentPaymentEntries.getValue().get("salesAmt").toEngineeringString()?if_exists}</fo:block>
	                    				</fo:table-cell>
	                    				<fo:table-cell border-style="solid">
	                    					<fo:block keep-together="always" text-align="right">${agentPaymentEntries.getValue().get("reciepts").toEngineeringString()?if_exists}</fo:block>
	                    				</fo:table-cell>
	                    				<fo:table-cell border-style="solid">
	                    					<fo:block keep-together="always" text-align="right">${(agentPaymentEntries.getValue().get("salesAmt")-agentPaymentEntries.getValue().get("reciepts"))?if_exists?string("##0.00#")}</fo:block>
	                    				</fo:table-cell>                    				                 				
	                    			</fo:table-row>                     				
                    			</#if>
                    			<#if totSaleAmt !=0>
	                    			<#if agentPaymentEntries.getKey() =="routeTotals">
	                    				<#assign routeTotalEntries = agentPaymentEntries.getValue()>		                    				
		                    			<fo:table-row >
		                    				<fo:table-cell border-style="solid"><fo:block keep-together="always" white-space-collapse="false">${routeTotalEntries.get("route")?if_exists}</fo:block></fo:table-cell>
		                    				<fo:table-cell><fo:block text-align="center" border-style="solid">TOTAL</fo:block></fo:table-cell>
		                    				<fo:table-cell><fo:block text-align="right" border-style="solid">${totSaleAmt.toEngineeringString()?if_exists}*</fo:block></fo:table-cell>
		                    				<fo:table-cell><fo:block text-align="right" border-style="solid">${totReceipts.toEngineeringString()?if_exists}*</fo:block></fo:table-cell>
		                    				<fo:table-cell><fo:block text-align="right" border-style="solid">${(totSaleAmt-totReceipts)?if_exists?string("##0.00#")}*</fo:block></fo:table-cell>
		                    			</fo:table-row> 
			                    	</#if>	                   			 
                    			</#if>
                    			</#list>
                    		</fo:table-body>
                		</fo:table>
     				</fo:block>     				
     			</fo:flow>
    	</fo:page-sequence>   
    	</#list> 
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