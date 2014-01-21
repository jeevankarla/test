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
            <fo:simple-page-master master-name="main" page-height="11in" page-width="10in"
        margin-top=".4in" margin-bottom=".2in" margin-left=".3in" margin-right=".3in">
          <fo:region-body margin-top=".3in"/>
          <fo:region-before extent=".3in"/>
          <fo:region-after extent=".3in"/>
      </fo:simple-page-master>
        </fo:layout-master-set>
        
       <#if truckSheetReportList?has_content>
       <#assign temp = 0/>
       	<#list truckSheetReportList as truckSheetReport>
       	   <#assign facilityTypeId = truckSheetReport.get("facilityType")>
       	  <#if facilityTypeId == "BOOTH">
       	   <#if temp == 0>        	
        	<fo:page-sequence master-reference="main">
        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
            </#if>
           <#if temp == 2>
        	<#assign temp = 0/>
        	 </fo:flow>
        	</fo:page-sequence>
        	<fo:page-sequence master-reference="main">
        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
           </#if>
           
        	<fo:block border-style="solid" font-family="Helvetica" >
        	 <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace"  font-weight="bold" font-size="16pt" keep-together="always">SUPRAJA DAIRY PVT. LTD., </fo:block>
			   <fo:block text-align="center" border-style="solid">
			   <fo:block text-align="center"  white-space-collapse="false"  font-size="9pt" keep-together="always">SRI SATYA, 6-18-3/3, SRI SAI GYANA MANDIR STREET, EAST POINT, COLONY, VISAKHAPATNAM – 530017</fo:block>
			   <fo:block text-align="center" white-space-collapse="false"  font-size="9pt" keep-together="always">Ph.Nos: 2543020(Off), 2703725(Off), Fax: 0891 2703703</fo:block>
				</fo:block>  
            	<#assign facility = delegator.findOne("Facility", {"facilityId" : truckSheetReport.get("facilityId")}, true)> 
            	<fo:block text-align="center" white-space-collapse="false" font-weight="bold" font-size="12pt" keep-together="always">&#160;</fo:block>		 		
            	<fo:block white-space-collapse="false" font-family="Courier,monospace" font-weight="bold" font-size="13pt"  keep-together="always"  text-align="center"><fo:inline text-decoration="underline">LORRY/VAN DELIVERY CHALLAN</fo:inline></fo:block>
            	<fo:block text-align="center" white-space-collapse="false" font-weight="bold" font-size="12pt" keep-together="always">&#160;</fo:block>
            	<fo:block text-align="center" border-style="solid">	
        		<fo:block white-space-collapse="false" font-size="12pt"  font-family="Courier,monospace"  text-align="left">To: BoothId:${facility.facilityId}   &#160;                             NO.  RSO.   </fo:block>
        		<fo:block white-space-collapse="false" font-size="12pt"  font-family="Courier,monospace"  text-align="left">Party Name   : ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, facility.ownerPartyId, false)}                     &#160;DATE:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(estimatedDeliveryDate, "dd/MM/yyyy")}</fo:block>
        		<#assign routeDetails = delegator.findOne("Facility", {"facilityId" : facility.parentFacilityId}, true)> 
        		<fo:block white-space-collapse="false" font-size="12pt"  font-family="Courier,monospace"  text-align="left">ROUTE:${routeDetails.get("facilityName")}            &#160;        TIME: <#assign shipment = delegator.findOne("Shipment", {"shipmentId" : parameters.shipmentId}, true)>
							<#if shipment.shipmentTypeId.startsWith("AM")>         		
              	                <fo:inline font-weight="bold"> AM</fo:inline>  &#160;   &#160; PM             
            			    </#if>
            			   	<#if shipment.shipmentTypeId.startsWith("PM")>   
            				  AM  &#160;   &#160;<fo:inline font-weight="bold"> PM</fo:inline>   
            			</#if>
            			
        		</fo:block>
        		 </fo:block>
        	 
            		<#assign productEntries = (truckSheetReport).entrySet()> 
            	
		                                		<fo:block white-space-collapse="false" font-size="9pt"  font-family="Courier,monospace"  text-align="left">	                              
				                              		<fo:table  table-layout="fixed" width="100%"  >
			             						 		<fo:table-column column-width="90pt"/>
			             						  		<fo:table-column column-width="286pt"/>
			             						   	    <fo:table-column column-width="150pt"/>
			             						    	<fo:table-column column-width="150pt"/>
			             						   		<fo:table-header>
			             						   		<fo:table-row> 
			             						               <fo:table-cell >
			             						               <fo:block text-align="right" font-family="Courier,monospace"  font-size="12pt"></fo:block>
			             						               </fo:table-cell > 
			             						               <fo:table-cell  >
			             						               <fo:block text-align="right" font-family="Courier,monospace"  font-size="12pt"></fo:block>
			             						               </fo:table-cell > 
			             						               <fo:table-cell  >
			             						               <fo:block text-align="right" font-family="Courier,monospace"  font-size="12pt">QUANTITY IN</fo:block>
			             						               </fo:table-cell > 
			             						               <fo:table-cell  >
			             						               <fo:block text-align="left" font-family="Courier,monospace"  font-size="12pt">&#160;LITRS</fo:block>
			             						               </fo:table-cell > 
			             						            </fo:table-row>
			             						            <fo:table-row> 
			             						               <fo:table-cell border-style="solid" >
			             						               <fo:block text-align="center" font-family="Courier,monospace"  font-size="12pt">S.No</fo:block>
			             						               </fo:table-cell > 
			             						               <fo:table-cell border-style="solid" >
			             						               <fo:block text-align="center" font-family="Courier,monospace"  font-size="12pt">Type Of Product</fo:block>
			             						               </fo:table-cell > 
			             						               <fo:table-cell border-style="solid" >
			             						               <fo:block text-align="center" font-family="Courier,monospace"  font-size="12pt">SACHETS</fo:block>
			             						               </fo:table-cell > 
			             						               <fo:table-cell border-style="solid" >
			             						               <fo:block text-align="center" font-family="Courier,monospace"  font-size="12pt">LOOSE</fo:block>
			             						               </fo:table-cell > 
			             						            </fo:table-row>
			             						   		</fo:table-header>
			             						        <fo:table-body> 
			             						        <#assign sNo =1>
		                                			<#list productEntries as productEntry>
		                      							<#if productEntry.getKey() != "facilityId" && productEntry.getKey() != "facilityType" && productEntry.getKey() != "PREV_DUE" && productEntry.getKey() != "paidAmount">
		                      								<#assign product = delegator.findOne("Product", {"productId" : productEntry.getKey()}, true)> 
			             						             <fo:table-row>  
					              							   <fo:table-cell border-style="solid">
								                                	<fo:block  text-align="center" font-family="Courier,monospace" font-size="12pt"  keep-together="always">${sNo}</fo:block>
								                            	</fo:table-cell>                  
								                            	<fo:table-cell border-style="solid" >
								                                	<fo:block  text-align="left" font-family="Courier,monospace" font-size="12pt" text-indent="30pt"  keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((product.get("brandName")))),7)}</fo:block>
								                            	</fo:table-cell>
								                            	<#assign sNo = sNo+1>							                            
								                      			<#assign typeEntries = (productEntry.getValue()).entrySet()>
								                      			<#list typeEntries as typeEntry>
								                      			  <#if !bulkMilkProducts.contains(productEntry.getKey())>	
								                      			   <#if typeEntry.getKey() == "TOTAL">
												                  <fo:table-cell border-style="solid" >								                            	
												                 	<fo:block  text-align="right" font-family="Courier,monospace"  font-size="12pt">${typeEntry.getValue()?string("##0.0")}</fo:block>
												                 </fo:table-cell>
												                  <fo:table-cell border-style="solid">								                            	
												                 	<fo:block  text-align="right" font-family="Courier,monospace"  font-size="12pt"></fo:block>
												                 </fo:table-cell>
												                  </#if>
												                  <#else>
												                   <#if typeEntry.getKey() == "LITRES">
												                  <fo:table-cell border-style="solid">								                            	
												                 	<fo:block  text-align="right" font-family="Courier,monospace"  font-size="12pt"></fo:block>
												                 </fo:table-cell>
												                 <fo:table-cell border-style="solid">							                            	
												                 	<fo:block  text-align="right" font-family="Courier,monospace"  font-size="12pt">${typeEntry.getValue()?string("##0.0")}</fo:block>
												                 </fo:table-cell>
												                 </#if> 
												                 </#if>
										                        </#list>
								                         	</fo:table-row>
								                         	</#if>
		                      				 	       </#list> 
								                          </fo:table-body>
						                         	</fo:table>
						                         	<fo:table  table-layout="fixed" width="100%" >
			             						 		
			             						  		<fo:table-column column-width="376pt"/>
			             						   	    <fo:table-column column-width="300pt"/>
			             						   		<fo:table-body>
			             						   		<fo:table-row> 
			             						               <fo:table-cell border-style="solid" >
			             						               <fo:block text-align="right" font-family="Courier,monospace"  font-size="12pt">Received the above in good condition</fo:block>
			             						               <fo:block text-align="center" white-space-collapse="false" font-weight="bold" font-size="12pt" keep-together="always">&#160;</fo:block>
			             						               <fo:block text-align="center" white-space-collapse="false" font-weight="bold" font-size="12pt" keep-together="always">&#160;</fo:block>
			             						               
			             						                <fo:block text-align="left" font-family="Courier,monospace"  font-size="12pt">Receiver's Signature</fo:block>
			             						               </fo:table-cell > 
			             						               <fo:table-cell border-style="solid" >
			             						               <fo:block text-align="right" font-family="Courier,monospace"  font-size="12pt">&#160;</fo:block>
			             						               <fo:block text-align="center" white-space-collapse="false" font-weight="bold" font-size="12pt" keep-together="always">&#160;</fo:block>
			             						               <fo:block text-align="center" white-space-collapse="false" font-weight="bold" font-size="12pt" keep-together="always">&#160;</fo:block>
			             						           
			             						                <fo:block text-align="center" font-family="Courier,monospace"  font-size="12pt">Sign.of Dispatcher</fo:block>
			             						               </fo:table-cell >
			             						          </fo:table-row>
			             						         </fo:table-body>
						                         	 </fo:table>
		                                		</fo:block>
		                          </fo:block> 
		                         <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
         	                     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
         	                     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
         	                     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		                         <#assign temp = temp+1/>
                             </#if>  
                           </#list>
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