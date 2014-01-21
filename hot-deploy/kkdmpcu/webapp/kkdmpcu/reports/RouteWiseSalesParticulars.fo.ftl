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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="12in" >
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
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold">.                           ROUTEWISE MILK SALES PARTICULARS</fo:block> 		
            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
            	<fo:block keep-together="always" white-space-collapse="false">.        FROM DATE : ${parameters.fromDate}          TO DATE : ${parameters.thruDate}</fo:block>
            	<fo:block>--------------------------------------------------------------------------------------------------------------</fo:block>
            	<fo:block keep-together="always" white-space-collapse="false">ROUTECODE        OPENING     GROSS VAL    RECEIPTS   CLOSING BAL <#if parameters.fromDate == parameters.thruDate> DAY CLOSING</#if>   QUANTITY    VH.PEND   VH.PAID</fo:block>
        		<fo:block>--------------------------------------------------------------------------------------------------------------</fo:block>
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">        				
     				<fo:block font-family="Courier,monospace"> 
     					<fo:table>
                    		<fo:table-column column-width="35pt"/>
                   			<fo:table-column column-width="40pt"/>                
                    		<fo:table-column column-width="45pt"/>
                    		<fo:table-column column-width="50pt"/>
                    		<fo:table-column column-width="85pt"/>
                    		<fo:table-column column-width="90pt"/>
                    		<fo:table-column column-width="85pt"/>
                    		<fo:table-column column-width="90pt"/>
                    		<fo:table-column column-width="90pt"/>
                    		<#if parameters.fromDate == parameters.thruDate>
                    			<fo:table-column column-width="90pt"/>
                    		</#if>
                    		<fo:table-column column-width="80pt"/>
                    		<fo:table-column column-width="80pt"/>                   
                   		    <fo:table-body>   
                   		    <#assign grTotOpening = (Static["java.math.BigDecimal"].ZERO)>
                   		    <#assign grTotSale = (Static["java.math.BigDecimal"].ZERO)>
                   		    <#assign grTotRecpts = (Static["java.math.BigDecimal"].ZERO)>
                   		    <#assign grTotClosing = (Static["java.math.BigDecimal"].ZERO)>
                   		    <#assign grVehicleDue = (Static["java.math.BigDecimal"].ZERO)>
                   		    <#assign grTotDue = (Static["java.math.BigDecimal"].ZERO)>   
                   		    <#assign grTotQty = (Static["java.math.BigDecimal"].ZERO)>  
                   		    <#assign grTotCardQty = (Static["java.math.BigDecimal"].ZERO)>
                   		    <#assign grTrnsptpaidAmt = (Static["java.math.BigDecimal"].ZERO)> 
                   		    <#assign bulkQty =  (Static["java.math.BigDecimal"].ZERO)>      		    
     					<#list allRoutesList as allRoutes>     	
     						<#assign agentPaymentDetails =allRoutes.entrySet()> 
     						<#list agentPaymentDetails as agentPaymentEntries>
                   		    	<#if agentPaymentEntries.getKey() =="routeTotals">
                    				<#assign routeTotalEntries = agentPaymentEntries.getValue()>
                    				<#assign grTotOpening = grTotOpening.add(routeTotalEntries.get("totOpeningAmt"))>
                    				<#assign grTotSale = grTotSale.add(routeTotalEntries.get("totSaleAmt"))>
                    				<#assign grTotRecpts = grTotRecpts.add(routeTotalEntries.get("totRecpts"))>
                    				<#assign grTotClosing = grTotClosing.add(routeTotalEntries.get("totClosing"))>
                    				<#assign grVehicleDue = grVehicleDue.add(routeTotalEntries.get("totalTrnsptDue"))>
                    				<#assign grTrnsptpaidAmt = grTrnsptpaidAmt.add(routeTotalEntries.get("VHpaid"))>
                    				<#assign grTotQty = grTotQty.add(routeTotalEntries.get("QTY"))>
                    				<#assign grTotCardQty = grTotCardQty.add(routeTotalEntries.get("CARDQTY"))>
                    				<#assign bulkQty = bulkQty.add(routeTotalEntries.get("bulkQty"))>
                    			<fo:table-row>
                    				<fo:table-cell><fo:block keep-together="always" white-space-collapse="false">${routeTotalEntries.get("route")?if_exists}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right"></fo:block></fo:table-cell>
                    				<fo:table-cell></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right">${routeTotalEntries.get("totOpeningAmt").toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right">${routeTotalEntries.get("totSaleAmt").toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right">${routeTotalEntries.get("totRecpts").toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right">${routeTotalEntries.get("totClosing").toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    				<#if parameters.fromDate == parameters.thruDate>
                    					<fo:table-cell><fo:block text-align="right">${(routeTotalEntries.get("totSaleAmt")).subtract((routeTotalEntries.get("totRecpts"))).toEngineeringString()}</fo:block></fo:table-cell>
                    				</#if>
                    				<fo:table-cell><fo:block text-align="right">${routeTotalEntries.get("QTY").toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right">${routeTotalEntries.get("totalTrnsptDue").toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right">${routeTotalEntries.get("VHpaid").toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    			</fo:table-row>                    			                  			
                    			</#if>
                    		</#list>                    			
                   		</#list>
                   				<fo:table-row>
                    				<fo:table-cell>
                    					<fo:block>--------------------------------------------------------------------------------------------------------------</fo:block>
                    				</fo:table-cell>
                    			</fo:table-row>
                   				<fo:table-row>
                    				<fo:table-cell><fo:block keep-together="always" white-space-collapse="false">GRAND TOTAL</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right"></fo:block></fo:table-cell>
                    				<fo:table-cell></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right">${grTotOpening.toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right">${grTotSale.toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right">${grTotRecpts.toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right">${grTotClosing.toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    				<#if parameters.fromDate == parameters.thruDate>
                    					<fo:table-cell><fo:block text-align="right">${(grTotSale.subtract(grTotRecpts)).toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    				</#if>
                    				<fo:table-cell><fo:block text-align="right">${grTotQty.toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right">${grVehicleDue.toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right">${grTrnsptpaidAmt.toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    			</fo:table-row>
                    			<fo:table-row>
                    				<fo:table-cell>
                    					<fo:block>--------------------------------------------------------------------------------------------------------------</fo:block>
                    				</fo:table-cell>
                    			</fo:table-row>
                    			<fo:table-row>
                    				<fo:table-cell>
                    					<fo:block keep-together="always" white-space-collapse="false">CANS OPN: ${(crateCanTotalMap.get("cansSent"))?if_exists}   CANS RECP: ${crateCanTotalMap.get("cansReceived")?if_exists}   CANS DIFF: ${((crateCanTotalMap.get("cansSent")).subtract(crateCanTotalMap.get("cansReceived")))?if_exists}   CRAT OPN: ${(crateCanTotalMap.get("cratesSent"))?if_exists}   CRAT RECP: ${(crateCanTotalMap.get("cratesReceived"))?if_exists}   CRAT DIFF: ${((crateCanTotalMap.get("cratesSent")).subtract(crateCanTotalMap.get("cratesReceived")))?if_exists}</fo:block>
                    				</fo:table-cell>                    				
                    			</fo:table-row>
                    			<fo:table-row>
                    				<fo:table-cell>
                    					<fo:block>--------------------------------------------------------------------------------------------------------------</fo:block>
                    				</fo:table-cell>
                    			</fo:table-row>
                    			<fo:table-row>
                    				<fo:table-cell>
                    					<fo:block keep-together="always">CASH DETAILS :</fo:block>
                    					<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                    					<fo:block>
                    						<fo:table>
                    							<fo:table-column column-width="30pt"/>
                   								<fo:table-column column-width="60pt"/>                
                    							<fo:table-column column-width="80pt"/>
                    							<fo:table-column column-width="100pt"/>
                    							<fo:table-column column-width="85pt"/>
                    							<fo:table-column column-width="90pt"/>
                    							<fo:table-column column-width="85pt"/>
                    							<fo:table-column column-width="90pt"/>
                    							<fo:table-column column-width="85pt"/>
                    							<fo:table-column column-width="90pt"/>
                    							<fo:table-body>
                    								<fo:table-row>
                    									<fo:table-cell>                    									
                    										<#assign total = (grTotRecpts.subtract(grVehicleDue)).add(grTrnsptpaidAmt)>
                    										<fo:block keep-together="always" white-space-collapse="false">RT.CASH      : ${total?if_exists}</fo:block>
                    										<#assign totPaymentType = totPaymentTypeWise.entrySet()>
                    											<#assign totOtherCash =0>
															<#list totPaymentType as totPayment>
																<#if (totPayment.getKey() != "RT-MILK") && (totPayment.getKey() != "TRANSPORTER_PAYIN") && (totPayment.getKey() != "RT-MILKCASH")>
																	<#assign totOtherCash =totOtherCash+totPayment.getValue()> 
 																	<#assign PaymentType = delegator.findOne("PaymentType", {"paymentTypeId" : totPayment.getKey()}, true)>
 																	<fo:block text-align="left" keep-together="always" white-space-collapse="false">${PaymentType.get("description")?if_exists}   : ${totPayment.getValue()?string("##0.00")?if_exists}</fo:block>
 																</#if>
    														</#list>
    														<fo:block keep-together="always" white-space-collapse="false">.            :-----------</fo:block>
    														<fo:block keep-together="always" white-space-collapse="false">TOTAL        : ${total+totOtherCash}</fo:block>
    													</fo:table-cell>    													
    													<fo:table-cell></fo:table-cell>
    													<fo:table-cell></fo:table-cell>
    													<fo:table-cell></fo:table-cell>
    													<fo:table-cell>
    														<fo:block keep-together="always" white-space-collapse="false">CASH OPENING  : ${remitOpening?if_exists?string("##0.00")}</fo:block>
    														<fo:block keep-together="always" white-space-collapse="false">RECEIPT       : ${(total+totOtherCash)?if_exists?string("##0.00")}</fo:block>
    														<fo:block keep-together="always" white-space-collapse="false">.             : -----------</fo:block>
    														<fo:block keep-together="always" white-space-collapse="false">TOT.CASH      : ${(remitOpening.add((total+totOtherCash)))?if_exists?string("##0.00")}</fo:block>
    														<fo:block keep-together="always" white-space-collapse="false">BANK REMIT    : ${cashTransactionTotalsMap.get("totRemittance")?if_exists?string("##0.00")} </fo:block>
    														<fo:block keep-together="always" white-space-collapse="false">EXPENDITURE   :  0.00</fo:block>
    														<fo:block keep-together="always" white-space-collapse="false">.             : -----------</fo:block>
    														<fo:block keep-together="always" white-space-collapse="false">TOTAL         : ${(remitOpening.add((total+totOtherCash))).subtract(cashTransactionTotalsMap.get("totRemittance"))?if_exists?string("##0.00")}</fo:block>
    														<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
    														<fo:block keep-together="always" white-space-collapse="false">CASH BALANCE   : ${(remitOpening.add((total+totOtherCash))).subtract(cashTransactionTotalsMap.get("totRemittance"))?if_exists?string("##0.00")}</fo:block>
    													</fo:table-cell>
                    									<fo:table-cell></fo:table-cell>
    													<fo:table-cell></fo:table-cell>
    													<fo:table-cell>
    														<fo:block keep-together="always" white-space-collapse="false">TOTAL PUF QTY SACHETS : ${grTotQty.toEngineeringString()?if_exists}</fo:block>
    														<fo:block keep-together="always" white-space-collapse="false">TOTAL PUF LEAKS       : 0.0</fo:block>
    														<fo:block keep-together="always" white-space-collapse="false">% OF PUF LEAKS        : 0.00</fo:block>
    														<fo:block keep-together="always" white-space-collapse="false">TOTAL NET QTY SACHETS : ${(grTotQty.subtract(bulkQty)).toEngineeringString()?if_exists}</fo:block>
    														<fo:block keep-together="always" white-space-collapse="false">TOTAL CARD QTY        : ${grTotCardQty.toEngineeringString()?if_exists}</fo:block>
    														<fo:block keep-together="always" white-space-collapse="false">TOTAL BULK QTY        : ${bulkQty.toEngineeringString()?if_exists}</fo:block>
    														
    														<fo:block keep-together="always" white-space-collapse="false">TOTAL NET QTY         : ${(grTotQty.add(grTotCardQty)).toEngineeringString()?if_exists}</fo:block>
    													</fo:table-cell>
                    								</fo:table-row>
                    							</fo:table-body>
                    						</fo:table>
                    					</fo:block>
                    				</fo:table-cell>                    				                 				
                    			</fo:table-row>                    			
    						</fo:table-body>
                		</fo:table>
     				</fo:block>
     			</fo:flow>
    	</fo:page-sequence> 
    	</#if>   	
</fo:root>
</#escape>