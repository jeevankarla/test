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
        margin-top="0.7in" margin-bottom=".2in" margin-left=".3in" margin-right=".2in">
          <fo:region-body margin-top="1in"/>
          <fo:region-before extent="1in"/>
          <fo:region-after extent="1in"/>
      </fo:simple-page-master>
    </fo:layout-master-set>
    <#assign tinNumber="">
    <#assign fssaiNumber="">
    ${setRequestAttribute("OUTPUT_FILENAME", "invoiceSlip.txt")}
    <#if OrderDetailsList?has_content>
    	<#assign temp = 0/>
    <fo:page-sequence master-reference="main">
        <fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        
        </fo:static-content>
         <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">	
         <fo:block></fo:block>
      <#list OrderDetailsList as orderDetail>
	      	<#assign temp = temp +1>
	      	
	        <#if (tinNumber ="") && (fssaiNumber ="")>
	        	<#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" : "Company"}, true)>
	        	<#assign tinNumber = (partyGroup.tinNumber)?if_exists>
	    		<#assign fssaiNumber = (partyGroup.fssaiNumber)?if_exists>
	        </#if>	
	         <#assign facility = delegator.findOne("Facility", {"facilityId" : orderDetail.orderHeader.originFacilityId}, true)>
	        
	          <#assign partyName = delegator.findOne("PartyNameView", {"partyId" : facility.ownerPartyId}, true)>
	                       <#assign currentShipmentId = "">
			              <#assign newShipmentId = "">
	                 	 <#assign newShipmentId =orderDetail.orderHeader.shipmentId>
			             <#if newShipmentId?exists & newShipmentId != currentShipmentId>
			              <#assign currentShipmentId = newShipmentId>
			               <#assign shipmentDeatils = delegator.findOne("Shipment", {"shipmentId" : newShipmentId}, true)>
			              </#if>
         	<fo:block font-family="Courier,monospace">          
           		<fo:table width="100%" table-layout="fixed">
                	<fo:table-column column-width="2.0in"/>
                	<fo:table-column column-width="1.in"/>
                	<fo:table-body>
                  		<fo:table-row>
                    		<fo:table-cell>
                    			<fo:table width="100%" table-layout="fixed">
                         			 <fo:table-column column-width="1in"/>
                         			 <fo:table-column column-width="1in"/>
                            			<fo:table-body>
			                            	<fo:table-row>
			                              		<fo:table-cell>
											        <fo:block text-align="left" keep-together="always" white-space-collapse="false">VST_ASCII-027VST_ASCII-077&#160;    KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD.</fo:block>
			                                        <fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;       UNIT : MOTHER DAIRY:G.K.V.K POST : YELAHANKA:BANGALORE : 560065</fo:block>
											        <fo:block font-family="Courier,monospace" >------------------------------------------------------------------------------</fo:block>
											        <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold">&#160; &#160;     TAX INVOICE</fo:block>
			                              		</fo:table-cell>
			                            	</fo:table-row>
			                            	<fo:table-row>
			                              		<fo:table-cell>
			        					  			<fo:block font-size="4pt" text-align="left" keep-together="always" white-space-collapse="false">TIN NO: ${tinNumber?if_exists}</fo:block>
			        					  			<fo:block font-size="4pt" text-align="left" keep-together="always" white-space-collapse="false">BILL NO: ${orderDetail.orderHeader.orderId}</fo:block>
			                              		</fo:table-cell>
			                              		<fo:table-cell>
							                        <fo:block font-size="4pt" text-align="left" keep-together="always" white-space-collapse="false">Supply Date: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(orderDetail.orderHeader.estimatedDeliveryDate?if_exists, "dd-MMM-yyyy")}</fo:block>
							                        <fo:block font-size="4pt" text-align="left" keep-together="always" white-space-collapse="false">PO No: <#if partyPONumMap?has_content && (partyPONumMap.get(facility.facilityId))?exists>${partyPONumMap.get(facility.facilityId)}</#if></fo:block>
							          			</fo:table-cell>  
			                            	</fo:table-row>
			                            	<fo:table-row>
			  								    <fo:table-cell>
			  										<fo:block></fo:block>
			  									</fo:table-cell>
											</fo:table-row>
							                     		<#if facility.get("facilityName")?has_content>
							                        		<#assign facilityName=facility.get("facilityName")/>
							                      		</#if>
							                 <fo:table-row>
							                    <fo:table-cell>
							                      	 <fo:block font-size="4pt"  white-space-collapse="false">To</fo:block>
							                         <fo:block font-size="4pt"  white-space-collapse="false" keep-together="always">Booth: ${orderDetail.orderHeader.originFacilityId} [ ${facilityName} ], Route:${ shipmentDeatils.routeId?if_exists}, Shift:<#if parameters.shipmentTypeId="AM_SHIPMENT">MORNING<#elseif  parameters.shipmentTypeId="PM_SHIPMENT">EVENING</#if></fo:block>
							                      	 <fo:block font-size="4pt"  white-space-collapse="false">Buyer's TIN:</fo:block>
							                     </fo:table-cell>    
							                 </fo:table-row>
                            			</fo:table-body>
                        			</fo:table>
                   			                  </fo:table-cell>
							                 </fo:table-row>
											</fo:table-body>
									</fo:table>
			            			</fo:block>              
			            <fo:block  font-size="7pt"  font-family="Courier,monospace">  
			              <fo:table width="100%" table-layout="fixed">
			                  <fo:table-column column-width="65pt"/>
			                  <fo:table-column column-width="40pt"/>
			                  <fo:table-column column-width="40pt"/>
			                  <fo:table-column column-width="46pt"/>
			                  <fo:table-column column-width="46pt"/> 
			                  <fo:table-column column-width="40pt"/> 
			                  <fo:table-column column-width="50pt"/> 
			                  <fo:table-column column-width="50pt"/> 
			                  <fo:table-body font-size="7pt"> 
			                  <fo:table-row>
			                      <fo:table-cell>
			                        <fo:block  font-family="Courier,monospace" text-align="left">================================================================================</fo:block>	
			                      </fo:table-cell>
			                    </fo:table-row>
			                  <fo:table-row>
			                        <fo:table-cell>
			                          <fo:block text-align="left" text-indent="4pt" keep-together="always"> ProductName&amp;</fo:block>
			                           <fo:block text-align="left" text-indent="4pt" keep-together="always"> Unit</fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="right" >Vat.</fo:block>
			                          <fo:block text-align="right" >%age.</fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="right" text-indent="4pt" keep-together="always">Qty.</fo:block>
			                           <fo:block text-align="right" text-indent="4pt" keep-together="always">Unit</fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                           <fo:block text-align="right" text-indent="4pt" keep-together="always">Rate/</fo:block>
			                           <fo:block text-align="right" text-indent="4pt" keep-together="always">Amt.</fo:block>
			                        </fo:table-cell>
			                       
			                        <fo:table-cell>
			                           <fo:block text-align="right" text-indent="4pt" keep-together="always">Net</fo:block>
			                           <fo:block text-align="right" text-indent="4pt" keep-together="always">Amt.</fo:block>
			                        </fo:table-cell>
			                         
			                        <fo:table-cell>
			                          <fo:block text-align="right" text-indent="4pt" keep-together="always">Vat</fo:block>
			                           <fo:block text-align="right" text-indent="4pt" keep-together="always">Amt.</fo:block>
			                        </fo:table-cell>
			                       
			                        <fo:table-cell>
			                           <fo:block text-align="right" text-indent="4pt" keep-together="always">Total</fo:block>
			                           <fo:block text-align="right" text-indent="4pt" keep-together="always">Amt.</fo:block>
			                        </fo:table-cell>
			                      </fo:table-row>  
			                      <fo:table-row>
			                      <fo:table-cell>
			                        <fo:block  font-family="Courier,monospace" text-align="left">================================================================================</fo:block>	
			                      </fo:table-cell>
			                    </fo:table-row>                      
			              <#if orderDetail.orderItems?has_content>
			                <#assign orderItems = orderDetail.orderItems?if_exists />
			                    <#assign vatTotal = (Static["java.math.BigDecimal"].ZERO)>
						           <#assign grandTotal = (Static["java.math.BigDecimal"].ZERO)>
						           <#assign netTotal = (Static["java.math.BigDecimal"].ZERO)>
			                    <#-- if the item has a description, then use its description.  Otherwise, use the description of the invoiceItemType -->
			                    <#list orderItems as orderItem>
			                    
			                      
			                        <#-- the shipment id is printed at the beginning for each
			                           group of invoice items created for the same shipment
			                        
			                        <fo:table-row>
			                          <fo:table-cell number-columns-spanned="6">
			                            <fo:block></fo:block>
			                           </fo:table-cell>
			                        </fo:table-row>
			                        -->
			                        <#-->
			                        <fo:table-row>
			                          <fo:table-cell number-columns-spanned="6">
			                            <fo:block > ${uiLabelMap.ProductShipmentId}: ${newShipmentId} </fo:block>
			                          </fo:table-cell>
			                        </fo:table-row> -->
			                       
			                      <fo:table-row>
			                        <fo:table-cell number-columns-spanned="6">
			                          <fo:block></fo:block>
			                        </fo:table-cell>
			                      </fo:table-row>
			                       <#assign product = orderItem.getRelatedOne("Product")?if_exists/>
			                 		<#if  product?exists>
			                    		<#assign productName=product.brandName?if_exists >
			                   		</#if>
			                        <#assign vatAmount = (Static["java.math.BigDecimal"].ZERO)>
			                        <#assign vatPercent = (Static["java.math.BigDecimal"].ZERO)>
			                        <#if orderItem.vatAmount?exists>
			                         <#assign vatAmount = orderItem.vatAmount?if_exists>
			                        </#if>
			                        <#if orderItem.vatAmount?exists>
			                         <#assign vatPercent = orderItem.vatPercent?if_exists> 
			                        </#if>
			                       
			                      <fo:table-row>
			                        <fo:table-cell>
			                          <fo:block text-align="left" text-indent="4pt" keep-together="always"> ${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(product.brandName?if_exists)),10)}</fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="right" >${vatPercent?if_exists}</fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="right"> <#if orderItem.quantity?exists>${orderItem.quantity?string.number}</#if> </fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="right"> <#if orderItem.quantity?exists>${orderItem.unitPrice}</#if> </fo:block>
			                        </fo:table-cell>
			                       <#assign netAmount =(Static["java.math.BigDecimal"].ZERO) > 
			                      <#assign netAmount= netAmount.add(orderItem.quantity*orderItem.unitPrice)>
			                        <fo:table-cell>
			                          <fo:block keep-together="always" text-align="right"> ${netAmount?if_exists?string("#0.00")} </fo:block>
			                        </fo:table-cell>
			                         <#assign netTotal = (netTotal.add(netAmount))>
			                        <fo:table-cell>
			                          <fo:block text-align="right" >${vatAmount?string("#0.00")?if_exists}</fo:block>
			                        </fo:table-cell>
			                        
			                         <#assign vatAmount = (orderItem.quantity)*vatAmount>
			                        <#assign vatTotal = (vatTotal.add(vatAmount))>
			                         <#assign totalAmount = (Static["java.math.BigDecimal"].ZERO)>
			                         
			                        <#assign totalAmount = (netAmount + vatAmount)?if_exists >
			                        <fo:table-cell>
			                          <fo:block text-align="right" >${totalAmount?string("#0.00")?if_exists} </fo:block>
			                        </fo:table-cell>
			                      </fo:table-row>
			                     
			                    </#list>
			                    <#-- the grand total -->
			                    <fo:table-row>
			                      <fo:table-cell>
			                        <fo:block  font-family="Courier,monospace" text-align="left">================================================================================</fo:block>	
			                      </fo:table-cell>
			                    </fo:table-row>
			                     <fo:table-row>
                                   <fo:table-cell>
                                   </fo:table-cell>
                                 </fo:table-row>
			                    <fo:table-row>
			                      <fo:table-cell>
			                        <fo:block >${uiLabelMap.AccountingTotalCapital}</fo:block>
			                      </fo:table-cell>
			                      <fo:table-cell text-align="right" number-columns-spanned="4">
			                        <fo:block >
			                         ${netTotal?string("#0.00")}
			                        </fo:block>
			                      </fo:table-cell>
			                      <fo:table-cell text-align="right">
			                        <fo:block >
			                         ${vatTotal?if_exists}
			                        </fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell text-align="right">
			                        <fo:block >
			                        <#assign grandTotal = (netTotal + vatTotal)>
			                         ${grandTotal?string("#0.00")}
			                        </fo:block>
			                        </fo:table-cell>
			                    </fo:table-row>
			                    
			                     <fo:table-row>
			                      <fo:table-cell>
			                        <fo:block  font-family="Courier,monospace" text-align="left">================================================================================</fo:block>	
			                      </fo:table-cell>
			                    </fo:table-row>	
			                    
			                    <fo:table-row>
			                      <fo:table-cell   number-columns-spanned="5">
			                       <#if grandTotal?has_content>
				                      <fo:block keep-together="always" text-align="left" font-size="7pt">&#160;${Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble(grandTotal?string("#0.00")), "%rupees-and-paise", locale).toUpperCase()}</fo:block>
			                      </#if>
			                      </fo:table-cell>
			                    </fo:table-row>
			                    <fo:table-row>
			                      <fo:table-cell text-align="right">
			                      <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			                      <fo:block font-size="4pt" text-align="right" keep-together="always">RECEIVER'S SIGNATORY</fo:block>
			                      </fo:table-cell>
			                      <fo:table-cell text-align="right" number-columns-spanned="2">
			                      <fo:block font-size="4pt" text-align="center" keep-together="always">sd/-</fo:block>
			                      <fo:block font-size="4pt" text-align="right" keep-together="always">AUTHORISED SIGNATORY</fo:block>
			                      </fo:table-cell>
			                    </fo:table-row>
			                    <fo:table-row>
			                      <fo:table-cell text-align="right" number-columns-spanned="5">
			                      <fo:block font-size="4pt" text-align="left" keep-together="always">**This is a computer generated invoice, hence no signature is required</fo:block>
			                      </fo:table-cell>
			                    </fo:table-row>
			          	         <fo:table-row>
			                      <fo:table-cell>
			                        <fo:block  font-family="Courier,monospace" text-align="left">================================================================================</fo:block>	
			                      </fo:table-cell>
			                    </fo:table-row>	
			         	        </#if>
			                  </fo:table-body>
			                </fo:table>
			            </fo:block>
			         	<fo:block linefeed-treatment="preserve">-------------------cut here-------------------</fo:block>
			         	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
       				<#if temp ==2>
       					<fo:block page-break-after="always"></fo:block>
       					<#assign temp=0>
       				</#if>
      			</#list>
      		</fo:flow>
      	</fo:page-sequence>
      <#else>
		<fo:page-sequence master-reference="main">
	    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	       		 <fo:block font-size="14pt">
	            	${uiLabelMap.NoOrderFound}.
	       		 </fo:block>
	    	</fo:flow>
		</fo:page-sequence>
     </#if>   
  </fo:root>
</#escape>

 

