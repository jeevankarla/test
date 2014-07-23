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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-right=".3in" margin-bottom=".3in" margin-top=".3in">
        <fo:region-body margin-top="0.2in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "DtcCostReport.pdf")}
 <#if dTCCostMap?has_content> 
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
					<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" font-size="10pt" font-weight="bold" white-space-collapse="false">&#160;${uiLabelMap.CommonPage}- <fo:page-number/> </fo:block>
			</fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
					<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false">    UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
					<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false">&#160;      Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      ${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      ${uiLabelMap.KMFDairySubHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="11pt">DISTRIBUTION TRANSPORTATION COST PER LTR OF MILK AND CURD SOLD </fo:block>
              		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="11pt">BETWEEN ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(monthBegin, "dd/MMMM/yyyy")} to ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(monthEnd, "dd/MMMM/yyyy")}</fo:block>
              		<fo:block font-size="8pt">----------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
              		<fo:block font-size="9pt" font-weight="bold">Route No    		&#160;&#160;&#160;&#160;&#160;&#160;Period						&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Total Sales*  			&#160;&#160;&#160;&#160;Avg Sales  				&#160;&#160;&#160;&#160;&#160;Distance Per Day  			&#160;&#160;&#160;&#160;&#160;&#160;Rate  			&#160;&#160;&#160;&#160;Cost Per Day     &#160;&#160;&#160;Cost/Ltr</fo:block>
            		<fo:block font-size="8pt">----------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            	<fo:block>
                 	<fo:table>
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="80pt"/>
                    <fo:table-column column-width="90pt"/> 
               	    <fo:table-column column-width="90pt"/>
            		<fo:table-column column-width="95pt"/> 		
            		<fo:table-column column-width="90pt"/>
            		<fo:table-column column-width="75pt"/>
            		<fo:table-column column-width="65pt"/>
                    <fo:table-body>
                    
                    <#assign totalAverageSaleQty =0 >
                    <#assign totalPayment =0 >
                    
                    <#assign periodDetails = dTCCostMap.entrySet()>
                    <#list periodDetails as eachPeriod>
                    	  <#assign routeDetails = eachPeriod.getValue().entrySet()>
                    	   <#list routeDetails as eachRoute>
                    	   	<#assign saleQty = 0>
		                    <#assign averageSaleQty = 0>
		                    <#assign facilitySize = 0>
		                    <#assign facilityRate = 0>
		                    <#assign payment = 0>
		                    <#assign costPerLtr = 0>
		                    
		                    <#assign saleQty = eachRoute.getValue().get("saleQty")?if_exists>
		                    <#assign averageDays = eachRoute.getValue().get("averageDays")?if_exists>
		                    <#if (averageDays!=0)>
		                    	<#assign averageSaleQty = (saleQty/averageDays)>
		                    </#if>
		                    <#assign facilitySize = eachRoute.getValue().get("facilitySize")?if_exists>
		                    <#assign facilityRate = eachRoute.getValue().get("facilityRate")?if_exists>
		                    <#if facilitySize?has_content && facilitySize!=0>
		                    	<#assign payment = (facilitySize?if_exists*facilityRate?if_exists)>
		                    <#else>
		                    	<#assign payment = facilityRate?if_exists>
		                    </#if>
		                     <#if averageSaleQty?has_content && (averageSaleQty != 0)>
		                    	<#assign costPerLtr = (payment/averageSaleQty)>
		                    </#if>
		                    <#assign totalAverageSaleQty = totalAverageSaleQty+averageSaleQty>
		                    <#assign totalPayment = totalPayment+payment>
                    	   
                    	   	<#assign customTimePeriod = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : eachRoute.getKey()?if_exists}, true)>
                    	   	<#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "MMM(dd")/>
                			<#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd)")/>	
                    	   	<fo:table-row>
                				<fo:table-cell>
                            		<fo:block  keep-together="always" text-align="left"  white-space-collapse="false">${eachPeriod.getKey()?if_exists}</fo:block>  
                       			</fo:table-cell>
                				<fo:table-cell>
                            		<fo:block  keep-together="always" text-align="left"  white-space-collapse="false">${fromDate?if_exists}-${thruDate?if_exists}</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                            		<fo:block  text-align="right"  white-space-collapse="false">${(saleQty?if_exists)?string("#0")}.00</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                            		<fo:block  text-align="right"  white-space-collapse="false"><#if (averageDays!=0)>${(saleQty/averageDays)?if_exists?string("#0.00")}</#if></fo:block>  
                       			</fo:table-cell>
                       			 <#if facilitySize?has_content>
                       			<fo:table-cell>
                            		<fo:block  text-align="right"  white-space-collapse="false">${facilitySize?if_exists?string("#0.00")}</fo:block>  
                       			</fo:table-cell>
                       			 <#else>
                       			 <fo:table-cell>
                            		<fo:block  text-align="right"  white-space-collapse="false"></fo:block>  
                       			</fo:table-cell>
                       			</#if>
                       			<fo:table-cell>
                            		<fo:block  text-align="right"  white-space-collapse="false">${facilityRate?if_exists?string("#0.00")}</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                            		<fo:block  text-align="right" white-space-collapse="false">${payment?if_exists?string("#0.00")}</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                            		<fo:block  text-align="right" white-space-collapse="false">${costPerLtr?if_exists?string("#0.00")}</fo:block>  
                       			</fo:table-cell>
            				</fo:table-row>
            				</#list>
                   </#list>
                			<fo:table-row>
                    			<fo:table-cell>
                        			<fo:block font-size="8pt">----------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                    			</fo:table-cell>
	                        </fo:table-row>
                			<fo:table-row font-weight="bold">
                				<fo:table-cell>
                            		<fo:block  keep-together="always" text-align="left"  white-space-collapse="false">Total Cost/Litre</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                            		<fo:block  text-align="right"  white-space-collapse="false"></fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                            		<fo:block  text-align="right"  white-space-collapse="false"></fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                            		<fo:block  text-align="right"  white-space-collapse="false">${totalAverageSaleQty?if_exists?string("#0.00")}</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                            		<fo:block  text-align="right"  white-space-collapse="false"></fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                            		<fo:block  text-align="right"  white-space-collapse="false"></fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                            		<fo:block  text-align="right" white-space-collapse="false">${totalPayment?if_exists?string("#0.00")}</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                            		<fo:block  text-align="right" white-space-collapse="false"><#if (totalAverageSaleQty!=0)>${(totalPayment/totalAverageSaleQty)?if_exists?string("#0.00")}</#if></fo:block>  
                       			</fo:table-cell>
            				</fo:table-row>
            				<fo:table-row>
                        			<fo:table-cell>
                            			<fo:block font-size="8pt">----------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                        			</fo:table-cell>
		                     </fo:table-row>
		                      <fo:table-row>
			       			<fo:table-cell>
			            		<fo:block  keep-together="always" font-style="italic">*Sales figures include MILK and CURD quantity only.</fo:block>  
			       			</fo:table-cell>
							</fo:table-row>
		                     <fo:table-row>
							<fo:table-cell>
			            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
			       			</fo:table-cell>
						  </fo:table-row>
						   <fo:table-row>
							<fo:table-cell>
			            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
			       			</fo:table-cell>
						  </fo:table-row>
						  <fo:table-row>
							<fo:table-cell>
			            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
			       			</fo:table-cell>
						  </fo:table-row>
						  <fo:table-row>
							<fo:table-cell>
			            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
			       			</fo:table-cell>
						  </fo:table-row>
		                  <fo:table-row>
			       			<fo:table-cell>
			            		<fo:block  keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Authorised Signatory</fo:block>  
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