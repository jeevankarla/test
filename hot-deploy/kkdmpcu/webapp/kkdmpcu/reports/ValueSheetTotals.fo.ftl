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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
            margin-top="0.5in" margin-bottom=".5in" margin-left=".3in" margin-right="1in">
        <fo:region-body margin-top="1in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "VS_tot.txt")}
<#assign lineNumber = 5>
<#assign numberOfLines = 70>
<#assign facilityNumberInPage = 0>

<#if routeWiseList?has_content>
<#macro header routeId>

<fo:page-sequence master-reference="main" force-page-count="no-force">					
			<fo:static-content flow-name="xsl-region-before" font-family="Courier">
				        <fo:block text-align="left" keep-together="always" white-space-collapse="false">KRISHNAVENI  KKDMPCU  LTD                                        VIJAYAWADA</fo:block>
							<fo:block text-align="left" keep-together="always" white-space-collapse="false">.                                             VALUE SHEET TOTALS</fo:block>				    		
            			  	<fo:block text-align="left" keep-together="always" white-space-collapse="false">Route No: ${routeId}                   Date: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(estimatedDeliveryDate, "dd/MM/yyyy")}</fo:block>
            			  	<fo:block>------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                            <fo:block text-align="left" keep-together="always" white-space-collapse="false">AGENT CODE/NAME       DTM200   DTM500   TM500   STD    WM500   GOLD    GROSS AMT     OLD DUE    PAID AMT    DUE AMT    NET REC.  TRANS.DUES    NET</fo:block>
                            <fo:block>------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>  
            </fo:static-content>
             
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">	
</#macro>

           <#list routeWiseList as eachRoute>
				<#assign grossAmount = (Static["java.math.BigDecimal"].ZERO)>
           		<#assign oldDues = (Static["java.math.BigDecimal"].ZERO)>
            	<#assign cratesIssued = (Static["java.math.BigDecimal"].ZERO)>
            	<#assign paidAmount=(Static["java.math.BigDecimal"].ZERO)>
            	<#assign dueAmount=(Static["java.math.BigDecimal"].ZERO)>
           		<#assign facilityId = 0>
           		<#assign description = 0>
           		<#assign wholeMilkSale = (Static["java.math.BigDecimal"].ZERO)>
				<#assign goldSale = (Static["java.math.BigDecimal"].ZERO)>
				<#assign TM500Sale = (Static["java.math.BigDecimal"].ZERO)>
				<#assign STDSale = (Static["java.math.BigDecimal"].ZERO)>
				<#assign DTM500Sale = (Static["java.math.BigDecimal"].ZERO)>
				<#assign DTM200Sale = (Static["java.math.BigDecimal"].ZERO)>
				<#assign WMBULKSale =(Static["java.math.BigDecimal"].ZERO)>
				<#assign GOLDBLKSale = (Static["java.math.BigDecimal"].ZERO)>
				<#assign TMBULKSale = (Static["java.math.BigDecimal"].ZERO)>
				<#assign STDBLKSale = (Static["java.math.BigDecimal"].ZERO)>
				<#assign DTMBULKSale = (Static["java.math.BigDecimal"].ZERO)>
           		<#assign routeEntries = (eachRoute).entrySet()>
           		<#list routeEntries as routeEntry> 
           			  <#assign route = routeEntry.getKey()>
           			  <@header routeId=route />
           			  <#assign boothEntries = routeEntry.getValue()>
           			  <#assign boothEntry = (boothEntries).entrySet()>     
           			 
           			  <#list boothEntry as booths> 
                          <#assign facilityIds = booths.getKey()>
                          <#assign eachFacilityDetails = booths.getValue()> 
                          <#assign eachItem = (eachFacilityDetails).entrySet()>
                          <#list eachItem as itemDetails>     		                          
                            
            			                 <#if itemDetails.getKey() == "facilityId" >
            			                      <#assign facilityId = itemDetails.getValue()>
            			                      <#assign facility = delegator.findOne("Facility", {"facilityId" : facilityId}, true)>
            			                      <#assign description = facility.get("description")>
            			                 </#if>
            			                 <#if itemDetails.getKey() == "facilityType" >
            			                      <#assign facilityType = itemDetails.getValue()>
            			                 </#if>
            			                 <#if itemDetails.getKey() == "paidAmount" >
            			                      <#assign paidAmount = itemDetails.getValue()>
            			                 </#if>
            			                 <#if itemDetails.getKey() == "oldDues" >
            			                      <#assign oldDues = itemDetails.getValue()>
            			                 </#if>
            			                 <#if itemDetails.getKey() == "CARD_AMOUNT" >
            			                      <#assign cardAmount = itemDetails.getValue()>
            			                 </#if>
            			                 <#if itemDetails.getKey() == "dueAmount" >
            			                      <#assign dueAmount = itemDetails.getValue()>
            			                 </#if>
            			                 <#if itemDetails.getKey() == "total" >
            			                      <#assign total = itemDetails.getValue()>
            			                 </#if>
            			                 <#if itemDetails.getKey() == "totalAmount" >
            			                      <#assign grossAmount = itemDetails.getValue()>
            			                 </#if>
            			                 <#if itemDetails.getKey() == "cratesIssued" >
            			                      <#assign cratesIssued = itemDetails.getValue()>
            			                 </#if>
            			                 <#if itemDetails.getKey() == "18" >
            			                      <#assign wholeMilkDetails = itemDetails.getValue()>
            			                      <#assign wholeMilkSale = wholeMilkDetails.get("prodTotalSale")>
            			                 </#if>
            			                 <#if itemDetails.getKey() == "11" >
            			                      <#assign DTM200Details = itemDetails.getValue()>
            			                      <#assign DTM200Sale = DTM200Details.get("prodTotalSale")>
            			                 </#if>
            			                 <#if itemDetails.getKey() == "12" >
            			                      <#assign DTM500Details = itemDetails.getValue()>
            			                      <#assign DTM500Sale = DTM500Details.get("prodTotalSale")>
            			                 </#if>
            			                 <#if itemDetails.getKey() == "14" >
            			                      <#assign TM500Details = itemDetails.getValue()>
            			                      <#assign TM500Sale = TM500Details.get("prodTotalSale")>
            			                 </#if>
            			                 <#if itemDetails.getKey() == "16" >
            			                      <#assign STDDetails = itemDetails.getValue()>
            			                      <#assign STDSale = STDDetails.get("prodTotalSale")>
            			                 </#if>
            			                 <#if itemDetails.getKey() == "20" >
            			                      <#assign goldDetails = itemDetails.getValue()>
            			                      <#assign goldSale = goldDetails.get("prodTotalSale")>
            			                 </#if>
            			                 <#if itemDetails.getKey() == "15" >
            			                      <#assign TMBULKDetails = itemDetails.getValue()>
            			                      <#assign TMBULKSale = TMBULKDetails.get("prodTotalSale")>
            			                 </#if>
            			                 <#if itemDetails.getKey() == "19" >
            			                      <#assign WMBULKDetails = itemDetails.getValue()>
            			                      <#assign WMBULKSale = WMBULKDetails.get("prodTotalSale")>
            			                 </#if>
            			                 <#if itemDetails.getKey() == "21" >
            			                      <#assign GOLDBLKDetails = itemDetails.getValue()>
            			                      <#assign GOLDBLKSale = GOLDBLKDetails.get("prodTotalSale")>
            			                 </#if>
            			                 <#if itemDetails.getKey() == "17" >
            			                      <#assign STDBLKDetails = itemDetails.getValue()>
            			                      <#assign STDBLKSale = STDBLKDetails.get("prodTotalSale")>
            			                 </#if>
            			                 <#if itemDetails.getKey() == "13" >
            			                      <#assign DTMBULKDetails = itemDetails.getValue()>
            			                      <#assign DTMBULKSale = DTMBULKDetails.get("prodTotalSale")>
            			                 </#if>
            			                 <#if itemDetails.getKey() == "transporterDues" >
            			                      <#assign transporterDues = itemDetails.getValue()>
            			                 </#if>
            			                 
						                 <#assign netReceived = paidAmount>
            			           </#list>  
            			           
            			       <fo:table width="100%" table-layout="fixed" space-after="0.0in">
            			          <fo:table-column column-width="170pt"/>
            			          <fo:table-column column-width="25pt"/>
				                  <fo:table-column column-width="55pt"/>
				                  <fo:table-column column-width="58pt"/>
				                  <fo:table-column column-width="58pt"/>
				                  <fo:table-column column-width="58pt"/>
				                  <fo:table-column column-width="58pt"/>
				                  <fo:table-column column-width="86pt"/>
				                  <fo:table-column column-width="87pt"/>
				                  <fo:table-column column-width="88pt"/>
				                  <fo:table-column column-width="85pt"/>
				                  <fo:table-column column-width="79pt"/>
				                  <fo:table-column column-width="75pt"/>
				                  <fo:table-column column-width="75pt"/>
            			              <fo:table-body> 
            			                
            			                 <#if eachFacilityDetails.get("facilityType") == "BOOTH" >
                                                      <fo:table-row>                             
                            	                         <fo:table-cell>
                            		                          <fo:block text-align="left" keep-together="always">${facilityId?if_exists}  ${(Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(description?if_exists)),15))}</fo:block>
                                                         </fo:table-cell>
                                                         <fo:table-cell>
                            		                          <fo:block text-align="right">${DTM200Sale?string("##0.0")?if_exists}</fo:block>
                                                         </fo:table-cell>
                                                         <fo:table-cell>
                            		                         <fo:block text-align="right">${DTM500Sale?string("##0.0")?if_exists}</fo:block>
                                                         </fo:table-cell>
                                                         <fo:table-cell>
                            		                          <fo:block text-align="right">${TM500Sale?string("##0.0")?if_exists}</fo:block>
                                                         </fo:table-cell>
                                                         <fo:table-cell>
                            		                          <fo:block text-align="right">${STDSale?string("##0.0")?if_exists}</fo:block>
                                                         </fo:table-cell>
                                                         <fo:table-cell>
                            		                          <fo:block text-align="right">${wholeMilkSale?string("##0.0")?if_exists}</fo:block>
                                                         </fo:table-cell>
                                                         <fo:table-cell>
                            		                         <fo:block text-align="right">${goldSale?string("##0.0")?if_exists}</fo:block>
                                                         </fo:table-cell>
                                                         
                                                         <fo:table-cell>
                            		                          <fo:block text-align="right">${grossAmount?string("##0.00")}</fo:block>
                                                         </fo:table-cell>
                                                         <fo:table-cell>
                            		                          <fo:block text-align="right">${oldDues?string("##0.00")}</fo:block>
                                                         </fo:table-cell>
                                                         <fo:table-cell>
                            		                          <fo:block text-align="right">${paidAmount?string("##0.00")}</fo:block>
                                                         </fo:table-cell>
                                                         
                                                         <fo:table-cell>
                            		                          <fo:block text-align="right">${dueAmount?string("##0.00")}</fo:block>
                                                         </fo:table-cell>
                                                         <fo:table-cell>
                            		                          <fo:block text-align="right">${netReceived?string("##0.00")}</fo:block>
                                                         </fo:table-cell>
                                                        
                                                         <#assign wholeMilkSale = 0>
				                                         <#assign goldSale = (Static["java.math.BigDecimal"].ZERO)>
				                                         <#assign TM500Sale = 0>
				                                         <#assign STDSale = 0>
				                                         <#assign DTM500Sale = 0>
				                                         <#assign DTM200Sale = 0>
                                                    </fo:table-row>
                                                    <#if WMBULKSale != 0 || DTMBULKSale != 0 || STDBLKSale != 0 || GOLDBLKSale != 0 || TMBULKSale != 0 >
                                                         <fo:table-row>
                                                            <fo:table-cell>
                            		                          <fo:block text-align="left">BULK</fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell>
                            		                          <fo:block text-align="right">${DTMBULKSale?string("##0.0")?if_exists}</fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell>
                            		                          <fo:block text-align="right"></fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell>
                            		                          <fo:block text-align="right">${TMBULKSale?string("##0.0")?if_exists}</fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell>
                            		                          <fo:block text-align="right">${STDBLKSale?string("##0.0")?if_exists}</fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell>
                            		                          <fo:block text-align="right">${WMBULKSale?string("##0.0")?if_exists}</fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell>
                            		                          <fo:block text-align="right">${GOLDBLKSale?string("##0.0")?if_exists}</fo:block>
                                                            </fo:table-cell>
                                                         </fo:table-row>
                                                         <#assign WMBULKSale = 0>
				                                         <#assign STDBLKSale = 0>
				                                         <#assign TMBULKSale = 0>
				                                         <#assign DTMBULKSale = 0>
				                                         <#assign GOLDBLKSale = 0>
				                                        
                                                    </#if>
                                                </fo:table-body>
				                            </fo:table> 
						                </#if>		                         
                                        <#if eachFacilityDetails.get("facilityType") == "ROUTE" >
                                                      <fo:table-row>
                                                            <fo:table-cell><fo:block>------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
                                                      </fo:table-row>
                                                      <fo:table-row>                             
                            	                         <fo:table-cell>
                            		                          <fo:block text-align="left" keep-together="always">${facilityId?if_exists}  ${(Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(description?if_exists)),15))}</fo:block>
                                                         </fo:table-cell>
                                                         <fo:table-cell>
                            		                          <fo:block text-align="right">${DTM200Sale?string("##0.0")?if_exists}</fo:block>
                                                         </fo:table-cell>
                                                         <fo:table-cell>
                            		                         <fo:block text-align="right">${DTM500Sale?string("##0.0")?if_exists}</fo:block>
                                                         </fo:table-cell>
                                                         <fo:table-cell>
                            		                          <fo:block text-align="right">${TM500Sale?string("##0.0")?if_exists}</fo:block>
                                                         </fo:table-cell>
                                                         <fo:table-cell>
                            		                          <fo:block text-align="right">${STDSale?string("##0.0")?if_exists}</fo:block>
                                                         </fo:table-cell>
                                                         <fo:table-cell>
                            		                          <fo:block text-align="right">${wholeMilkSale?string("##0.0")?if_exists}</fo:block>
                                                         </fo:table-cell>
                                                         <fo:table-cell>
                            		                         <fo:block text-align="right">${goldSale?string("##0.0")?if_exists}</fo:block>
                                                         </fo:table-cell>
                                                         
                                                         <fo:table-cell>
                            		                          <fo:block text-align="right">${grossAmount?string("##0.00")}</fo:block>
                                                         </fo:table-cell>
                                                         <fo:table-cell>
                            		                          <fo:block text-align="right">${oldDues?string("##0.00")}</fo:block>
                                                         </fo:table-cell>
                                                         <fo:table-cell>
                            		                          <fo:block text-align="right">${paidAmount?string("##0.00")}</fo:block>
                                                         </fo:table-cell>
                                                         
                                                         <fo:table-cell>
                            		                          <fo:block text-align="right">${dueAmount?string("##0.00")}</fo:block>
                                                         </fo:table-cell>
                                                         <fo:table-cell>
                            		                          <fo:block text-align="right">${netReceived?string("##0.00")}</fo:block>
                                                         </fo:table-cell>
                                                         <fo:table-cell>
                            		                          <fo:block text-align="right">${transporterDues?string("##0.00")}</fo:block>
                                                         </fo:table-cell>
                                                         <fo:table-cell>
                            		                          <fo:block text-align="right">${(netReceived - transporterDues)?string("##0.00")}</fo:block>
                                                         </fo:table-cell>
                                                      </fo:table-row>  
                                                      <#if WMBULKSale != 0 || DTMBULKSale != 0 || STDBLKSale != 0 || GOLDBLKSale != 0 || TMBULKSale != 0 >
                                                         <fo:table-row>
                                                            <fo:table-cell>
                            		                          <fo:block text-align="left">BULK</fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell>
                            		                          <fo:block text-align="right">${DTMBULKSale?string("##0.0")?if_exists}</fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell>
                            		                          <fo:block text-align="right"></fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell>
                            		                          <fo:block text-align="right">${TMBULKSale?string("##0.0")?if_exists}</fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell>
                            		                          <fo:block text-align="right">${STDBLKSale?string("##0.0")?if_exists}</fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell>
                            		                          <fo:block text-align="right">${WMBULKSale?string("##0.0")?if_exists}</fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell>
                            		                          <fo:block text-align="right">${GOLDBLKSale?string("##0.0")?if_exists}</fo:block>
                                                            </fo:table-cell>
                                                         </fo:table-row>
                                                         <#assign WMBULKSale = 0>
				                                         <#assign STDBLKSale = 0>
				                                         <#assign TMBULKSale = 0>
				                                         <#assign DTMBULKSale = 0>
				                                         <#assign GOLDBLKSale = 0>
				                                        
                                                    </#if>  
                                                         
					                                  <fo:table-row>
                                                           <fo:table-cell>
                                                               <fo:block >
                                                                   <fo:block>------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                      				 		                   </fo:block>
                                                           </fo:table-cell>
                                                     </fo:table-row>
                                                     <fo:table-row>
                                                           <fo:table-cell>
                                                              <fo:table width="100%" table-layout="fixed" space-after="0.0in">
            			                                              <fo:table-column column-width="200pt"/>
            			                                              <fo:table-column column-width="200pt"/>
            			                                              <fo:table-body>
            			                                                      <fo:table-row>                             
                            	                                                   <fo:table-cell>
				                                                                       <fo:block text-align="left">CRATES ISSUED : ${cratesIssued}</fo:block>
				                                                                   </fo:table-cell>
				                                                                   <fo:table-cell>
				                                               						   <fo:block text-align="left">CRATES RECEIVED :</fo:block>
				                                          						   </fo:table-cell>
                                                                              </fo:table-row>
                                                                              <fo:table-row>
                                                                                   <fo:table-cell>
				                                                                       <fo:block text-align="left">CANS ISSUED :</fo:block>
				                                                                   </fo:table-cell>
				                                                                   <fo:table-cell>
				                                                                       <fo:block text-align="left">CANS RECEIVED :</fo:block>
				                                                                   </fo:table-cell>
                                                                              </fo:table-row>
                                                                     </fo:table-body>
				                                             </fo:table>
				                                          </fo:table-cell>   
				                                      </fo:table-row>  
                                                 </fo:table-body>
				                             </fo:table> 
				                              <#assign facilityId = 0>
                                              <#assign wholeMilkSale = (Static["java.math.BigDecimal"].ZERO)>
				                              <#assign goldSale = (Static["java.math.BigDecimal"].ZERO)>
				                              <#assign TM500Sale = (Static["java.math.BigDecimal"].ZERO)>
				                              <#assign STDSale = (Static["java.math.BigDecimal"].ZERO)>
				                              <#assign DTM500Sale = (Static["java.math.BigDecimal"].ZERO)>
				                              <#assign DTM200Sale = (Static["java.math.BigDecimal"].ZERO)>           		
                                              <#assign totalWMBULK = (Static["java.math.BigDecimal"].ZERO)>
					                  </#if>
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