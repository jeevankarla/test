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
    <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
            margin-top="0.5in" margin-bottom="1in" margin-left="1in" margin-right="1in">
        <fo:region-body margin-top="1.2in"/>
        <fo:region-before extent=".5in"/>
        <fo:region-after extent="1in"/>
    </fo:simple-page-master>
</fo:layout-master-set>

		<#if masterList?has_content>
		<#list masterList as trnsptMarginReportEntry>
			<#assign trnsptMarginReportEntries = (trnsptMarginReportEntry).entrySet()>	
			<#list trnsptMarginReportEntries as trnsptMarginValues>    
				<fo:page-sequence master-reference="main" >
					<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
						<fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-weight="bold" font-size="10pt" keep-together="always"> MOTHER DAIRY, KMF UNIT	</fo:block>
						<fo:block text-align="center" font-weight="bold" font-size="10pt" white-space-collapse="false" keep-together="always">BANGALORE - 560065.</fo:block>
						<fo:block text-align="center" keep-together="always"> ROUTE DESPATCHES &amp; PAYMENT PARTICULARS</fo:block>
						<fo:block text-align="left" keep-together="always" white-space-collapse="false">ROUTE: ${trnsptMarginValues.getKey()}          FROMDATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "dd/MM/yyyy")}   TO  DATE :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "dd/MM/yyyy")}</fo:block>				    		
		            <fo:block >---------------------------------------------------------------------</fo:block>
		            <fo:block text-align="left" keep-together="always"  white-space-collapse="false"> DATE         QUANTITY                  MARGIN AMOUNT</fo:block>
		            <fo:block >---------------------------------------------------------------------</fo:block>
		            </fo:static-content>
					<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
						<#assign trnsptMarginEntries = (trnsptMarginValues.getValue())>
						<#list trnsptMarginEntries as trnsptMarginEntry>
							<#assign daywiseTrnsptMarginEntries = trnsptMarginEntry.entrySet()>
							<#assign netPayable = (Static["java.math.BigDecimal"].ZERO)>
							<#list daywiseTrnsptMarginEntries as daywiseTrnsptEntry>
								<fo:block>
									 <fo:table >
                    					<fo:table-column column-width="60pt"/>
                   						<fo:table-column column-width="85pt"/>                
                    					<fo:table-column column-width="90pt"/>
                    					<fo:table-column column-width="95pt"/>
                    					<fo:table-column column-width="90pt"/>
                    					<fo:table-column column-width="90pt"/>
                    					<fo:table-body>
                    						<#if daywiseTrnsptEntry.getKey() !="Tot">
                    							<#assign routeAmount = daywiseTrnsptEntry.getValue().get("rtAmount")>
                    							<fo:table-row>
                    								<fo:table-cell><fo:block>${daywiseTrnsptEntry.getKey()}</fo:block></fo:table-cell>
                    								<fo:table-cell><fo:block text-align="right">${daywiseTrnsptEntry.getValue().get("totalQuantity").toEngineeringString()}</fo:block></fo:table-cell>
                    								<fo:table-cell></fo:table-cell>
                    								<fo:table-cell><fo:block text-align="right">${routeAmount.toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    							</fo:table-row>
                    						<#else>
                    							<fo:table-row>
                    								<fo:table-cell><fo:block >---------------------------------------------------------------------</fo:block></fo:table-cell>
                    							</fo:table-row>
                    							<fo:table-row>
                    								<#assign grTotRtAmt = daywiseTrnsptEntry.getValue().get("grTotRtAmount")>
                    								<fo:table-cell><fo:block>TOTAL</fo:block></fo:table-cell>
                    								<fo:table-cell><fo:block text-align="right">${daywiseTrnsptEntry.getValue().get("grTotQty").toEngineeringString()}</fo:block></fo:table-cell>
                    								<fo:table-cell></fo:table-cell>
                    								<fo:table-cell><fo:block text-align="right">${grTotRtAmt.toEngineeringString()?if_exists}</fo:block></fo:table-cell>                    								
                    							</fo:table-row>
                    							<fo:table-row>
                    								<fo:table-cell>
                    									<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                    								</fo:table-cell>
                    							</fo:table-row>
                    							<fo:table-row>
                    								
                    								<#assign vehicleDue = daywiseTrnsptEntry.getValue().get("grTotpendingDue")>
                    								<#assign netPayable = grTotRtAmt.subtract(vehicleDue)>
                    								<fo:table-cell><fo:block text-align="left" keep-together="always">MONTH BILL:</fo:block></fo:table-cell>
                    								<fo:table-cell><fo:block text-align="right">${grTotRtAmt.toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    								<fo:table-cell><fo:block text-align="center" keep-together="always">VEHICLE DUE:</fo:block></fo:table-cell>
                    								<fo:table-cell><fo:block text-align="left">${vehicleDue.toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    								<fo:table-cell><fo:block text-align="left" keep-together="always">NET PAYABLE:</fo:block></fo:table-cell>
                    								<fo:table-cell><fo:block text-align="left">${netPayable.toEngineeringString()?if_exists}</fo:block></fo:table-cell>
                    							</fo:table-row>
                    							<fo:table-row>
                    								<fo:table-cell><fo:block >---------------------------------------------------------------------</fo:block></fo:table-cell>
                    							</fo:table-row>
                    						</#if>                    						
                    					</fo:table-body>
                    				 </fo:table>								
								</fo:block>
							</#list>	
						</#list>
		 			</fo:flow>	
		 		</fo:page-sequence>
		 		</#list>
		 	    </#list>
			<#else>
				<fo:page-sequence master-reference="main">
			    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			       		 <fo:block font-size="14pt">
			            	${uiLabelMap.OrderNoOrderFound}.
			       		 </fo:block>
			    	</fo:flow>
				</fo:page-sequence>
			</#if>						
</fo:root>
</#escape>