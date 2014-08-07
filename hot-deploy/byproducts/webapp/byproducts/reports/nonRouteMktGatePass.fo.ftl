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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-right=".3in" margin-top=".5in">
                <fo:region-body margin-top="1.1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "deliveryChallan.pdf")}
        <#if orderDetailMap?has_content>
        <#assign ordersList = orderDetailMap.entrySet()> 
        <#list  ordersList as eachOrder>
        <#assign orderId = eachOrder.getKey()>
        <#assign orderDetail = eachOrder.getValue()>
        <#assign orderHeader = orderDetail.get('orderHeader')>
        <#assign shipment = orderDetail.get('shipment')>
        <#assign orderItems = orderDetail.get('orderItems')>
        <#assign billingAddress = orderDetail.get('partyAddress')>
        <#assign partyName = orderDetail.get('partyName')>
        <#assign invoice = orderDetail.get('invoice')>
        <#assign partyCode = orderDetail.get('partyCode')>
        <#assign screenFlag = orderDetail.get('screenFlag')>
        <fo:page-sequence master-reference="main" font-size="10pt">	
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        		<fo:block text-align="left" font-size="13pt" keep-together="always"  white-space-collapse="false">
        			<fo:table>
			            <fo:table-column column-width="150pt"/>
			            <fo:table-column column-width="150pt"/>
			            <fo:table-column column-width="150pt"/>
			            <fo:table-column column-width="150pt"/> 
			            <fo:table-body>
			                <fo:table-row>
			                    <fo:table-cell number-columns-spanned="4">
					            	<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD.</fo:block>
					            	<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false"> UNIT : MOTHER DAIRY:G.K.V.K POST : YELAHANKA:BANGALORE : 560065</fo:block>
					            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
					            	<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold"><#if screenFlag?has_content && screenFlag == "gatePass">GATE PASS<#else>DELIVERY CHALLAN</#if></fo:block>
					            </fo:table-cell>
							</fo:table-row>
			            </fo:table-body>
			        </fo:table>
        		</fo:block>
        	</fo:static-content>	        	
        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
        		<fo:block>
             		<fo:table border-style="dotted" border-width="solid" border-color="black">
			            <fo:table-column column-width="150pt"/>
			            <fo:table-column column-width="150pt"/>
			            <fo:table-column column-width="150pt"/>
			            <fo:table-column column-width="150pt"/>
			            <fo:table-body>
			                <fo:table-row>
			                    <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">CODE : </fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${partyCode?if_exists}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">GP NO : </fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false"><#if shipment?has_content>${shipment.shipmentId?if_exists}</#if></fo:block>  
					            </fo:table-cell>
							</fo:table-row>
							<fo:table-row>
			                    <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">NAME : </fo:block>
					            </fo:table-cell>
					            <fo:table-cell>	
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${partyName.get('groupName')?if_exists}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">GP DATE : </fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false"><#if shipment?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(shipment.estimatedShipDate, "dd-MMM-yyyy")}</#if></fo:block>  
					            </fo:table-cell>
							</fo:table-row>
							<fo:table-row>
			                    <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">ADDRESS : </fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${billingAddress.get('address1')?if_exists}</fo:block>
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${billingAddress.get('address2')?if_exists}</fo:block>
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${billingAddress.get('city')?if_exists},${billingAddress.get('countryGeoId')?if_exists}, ${billingAddress.get('postalCode')?if_exists}</fo:block>
								</fo:table-cell>
								<fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">DC No : </fo:block>
					            </fo:table-cell>
					            <fo:table-cell>	
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${orderId?if_exists}</fo:block>  
					            </fo:table-cell>
							</fo:table-row>
							<fo:table-row>
			                    <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">REF NO : </fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false"> <#if invoice?has_content>${invoice.invoiceId?if_exists}<#else>NIL</#if></fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">DC DATE : </fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(orderHeader.estimatedDeliveryDate, "dd-MMM-yyyy")}</fo:block>  
					            </fo:table-cell>
							</fo:table-row>
							<fo:table-row>
			                    <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">VEHICLE NO : </fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false"><#if shipment?has_content>${shipment.vehicleId?if_exists}</#if></fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">REF DATE : </fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false"><#if invoice?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invoice.dueDate, "dd-MMM-yyyy")}<#else></#if></fo:block>  
					            </fo:table-cell>
							</fo:table-row>
							<fo:table-row>
			                    <fo:table-cell>
					            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            </fo:table-cell>
							</fo:table-row>
							<fo:table-row border-style="dotted" border-width="thin" border-color="black">
			                    <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">SL NO</fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">CODE</fo:block>
								</fo:table-cell>
								<fo:table-cell>
					            	<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">DESCRIPTION</fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">QTY</fo:block>  
					            </fo:table-cell>
							</fo:table-row>
							<#if orderItems?has_content>
								<#assign slNo = 1>
								<#list orderItems as eachItem>
									<fo:table-row>
					                    <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">${slNo?if_exists} </fo:block>
							            </fo:table-cell>
							            <fo:table-cell>	
							            	<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">${eachItem.productId?if_exists}</fo:block>
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">${eachItem.itemDescription?if_exists}</fo:block>
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">${eachItem.quantity?if_exists}</fo:block>  
							            </fo:table-cell>
									</fo:table-row>
									<#assign slNo = slNo+1>
								</#list>
							</#if>
			            </fo:table-body>
			        </fo:table>
          		</fo:block>
          		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
          		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
          		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
          		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
          		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">
					<fo:table>
			            <fo:table-column column-width="300pt"/>
			            <fo:table-column column-width="300pt"/>
			            <fo:table-body>
			                <fo:table-row>
			                    <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">SIGNATURE &amp; SEAL OF</fo:block>
					            	<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">ISSUING AUTHORITY</fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">SIGNATURE &amp; SEAL</fo:block>
					            	<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">OF RECEIVER</fo:block>
					            </fo:table-cell>
							</fo:table-row>
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