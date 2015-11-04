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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-left=".3in" margin-right=".3in" margin-bottom="1in" margin-top=".5in">
                <fo:region-body margin-top="1.7in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "SaleDCandGatePassReport.pdf")}
<#if finalMap?has_content>
        <fo:page-sequence master-reference="main" font-size="12pt">	
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        		<fo:block text-align="left" font-size="13pt" keep-together="always"  white-space-collapse="false">
        			<fo:table>
			            <fo:table-column column-width="150pt"/>
			            <fo:table-column column-width="150pt"/>
			            <fo:table-column column-width="150pt"/>
			            <fo:table-column column-width="150pt"/> 
			            <fo:table-body>
			                <fo:table-row>
			                <fo:table-cell><fo:block></fo:block></fo:table-cell>
			                <fo:table-cell><fo:block></fo:block></fo:table-cell>
			                <fo:table-cell><fo:block></fo:block></fo:table-cell>
			                    <fo:table-cell>
			                    	<fo:block  keep-together="always"  text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size = "10pt">&#160;                                                                                                                                            UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
			                    	<fo:block  keep-together="always"  text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size = "10pt">&#160;                                                                                                                                                Date : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
			                    	<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
                                    <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
					            	<fo:block  keep-together="always" text-align="center" font-weight="bold"  font-size="12pt" white-space-collapse="false">${reportHeader.description?if_exists}</fo:block>
					            	<fo:block  keep-together="always" text-align="center" font-weight="bold" font-size="12pt" white-space-collapse="false"> ${reportSubHeader.description?if_exists}</fo:block>
					            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
                                   <fo:block  keep-together="always" font-weight="bold"  text-align="center" font-size="12pt" white-space-collapse="false">SALE DC AND GATE PASS FROM ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(formDate, "dd-MMM-yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")}</fo:block> 
					            </fo:table-cell>
							</fo:table-row>
			            </fo:table-body>
			        </fo:table>
        		</fo:block>
				
 			<fo:block text-align="left" keep-together="always" >-------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>	
        		<fo:block font-weight="bold">
             		<fo:table>
			            <fo:table-column column-width="43pt"/>
			            <fo:table-column column-width="65pt"/>
			            <fo:table-column column-width="90pt"/>
			            <fo:table-column column-width="80pt"/>
                        <fo:table-column column-width="90pt"/>
			            <fo:table-column column-width="160pt"/>
			            <fo:table-column column-width="42pt"/>
			            <fo:table-column column-width="60pt"/>
                        <fo:table-column column-width="160pt"/>
			            <fo:table-column column-width="80pt"/>
			            <fo:table-column column-width="50pt"/>
			            <fo:table-column column-width="55pt"/>
                        <fo:table-column column-width="50pt"/>
			            <fo:table-body>
			                <fo:table-row>
			                    <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false">SL.NO</fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false">DC/GP NO</fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false">VEHICLE NO</fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false">INVOICE NO</fo:block>  
					            </fo:table-cell>
                                <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false">REF DATE</fo:block>  
					            </fo:table-cell>
                                <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false">MATERIAL DESCRIPTION</fo:block>  
					            </fo:table-cell>
                                <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false">QTY</fo:block>  
					            </fo:table-cell>
                                <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false">AMOUNT</fo:block>  
					            </fo:table-cell>
                                <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false">CUSTOMER NAME</fo:block>  
					            </fo:table-cell>
                                <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" fon-size="11pt" white-space-collapse="false">GP DATE</fo:block>  
					            </fo:table-cell>
                                <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false">UOM</fo:block>  
					            </fo:table-cell>
                                <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" fon-size="11pt" white-space-collapse="false">GR AMOUNT</fo:block>  
					            </fo:table-cell>
							</fo:table-row>
			            </fo:table-body>
			        </fo:table>
          		</fo:block>
                <fo:block text-align="left" keep-together="always" >-------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                </fo:static-content>	    
             <#assign DcValues=finalMap.entrySet()>  
        	<#assign sno=1>
        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
			<#list  DcValues as productValues>
                   <#assign productDetails= productValues.getValue()> 
                  <#assign totalQty=0>
                  <#assign totOrderAmt=0>
                  <#assign totPmtAmt=0> 
                 <#list productDetails as productValue>
                <#assign shipment={}> 
                 <#assign payment={}>
                <#if productValue.get('orderHeader')?has_content>
                 	<#assign orderHeader=productValue.get('orderHeader')>
                </#if>
	            <#if productValue.get('invoice')?has_content>
                 	<#assign invoice=productValue.get('invoice')>
                </#if>
                <#if productValue.get('shipment')?has_content>
                 	<#assign shipment=productValue.get('shipment')>
                </#if>
               <#if productValue.get('payment')?has_content>
                   <#assign payment=productValue.get('payment')>   
               </#if> 
               <#if productValue.get('quantity')?has_content>
               <#assign totalQty=totalQty+productValue.get('quantity')>
               </#if>
               <#if orderHeader.grandTotal?has_content> 
               <#assign totOrderAmt=totOrderAmt+orderHeader.grandTotal>
               </#if>
              <#if payment.amount?has_content>
               <#assign totPmtAmt=totPmtAmt+payment.amount> 
              </#if>     
                <fo:block>
             		<fo:table>
			            <fo:table-column column-width="43pt"/>
			            <fo:table-column column-width="65pt"/>
			            <fo:table-column column-width="90pt"/>
			            <fo:table-column column-width="80pt"/>
                        <fo:table-column column-width="90pt"/>
			            <fo:table-column column-width="160pt"/>
			            <fo:table-column column-width="70pt"/>
			            <fo:table-column column-width="35pt"/>
                        <fo:table-column column-width="160pt"/>
			            <fo:table-column column-width="80pt"/>
			            <fo:table-column column-width="39pt"/>
			            <fo:table-column column-width="80pt"/>
                        <fo:table-column column-width="53pt"/>
			            <fo:table-body>
			                <fo:table-row>
			                    <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false">${sno}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false">${shipment.shipmentId?if_exists}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false">${shipment.vehicleId}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
				            	<fo:block   text-align="left" font-size="11pt" white-space-collapse="false"><#if invoice.invoiceId?has_content>${invoice.invoiceId?if_exists}</#if></fo:block>  
					            </fo:table-cell>
                                <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false"><#if orderHeader.orderDate?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(orderHeader.orderDate, "dd/MM/yyyy")}</#if></fo:block>  
					            </fo:table-cell>
					            <#if productValue.get("comments")?has_content>
                                <fo:table-cell>
					            	<fo:block   text-align="left" font-size="11pt" white-space-collapse="false">${productValue.get("comments")?if_exists}</fo:block>  
					            </fo:table-cell>
                                <#else>
                                <fo:table-cell>
					            	<fo:block   text-align="left" font-size="11pt" white-space-collapse="false">${productValue.get("productName")?if_exists}</fo:block>  
					            </fo:table-cell>
					             </#if> 
                                <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false">${productValue.get("quantity")?if_exists}</fo:block>  
					            </fo:table-cell>
                                <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false"><#if orderHeader.grandTotal?has_content>${orderHeader.grandTotal?if_exists?string("##0.00")}</#if></fo:block>  
					            </fo:table-cell>
					            
                                <fo:table-cell>
					            	<fo:block   text-align="left" font-size="11pt" white-space-collapse="false">&#160;&#160;${productValue.get("Name")?if_exists}</fo:block>  
					            </fo:table-cell>
                               
                                <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false"><#if shipment.estimatedShipDate?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(shipment.estimatedShipDate, "dd/MM/yyyy")}</#if></fo:block>  
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false">${productValue.get("unit")?if_exists}</fo:block>  
					            </fo:table-cell>
                                 <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false"><#if payment.amount?has_content>${payment.amount?if_exists?string("##0.00")}</#if></fo:block>  
					            </fo:table-cell>
							</fo:table-row>
			            </fo:table-body>
			        </fo:table>
          		</fo:block>
               <#assign sno=sno+1>
				</#list>
				<fo:block text-align="left" keep-together="always" >-------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>	
        		<fo:block font-weight="bold">
             		<fo:table>
			            <fo:table-column column-width="43pt"/>
			            <fo:table-column column-width="65pt"/>
			            <fo:table-column column-width="90pt"/>
			            <fo:table-column column-width="80pt"/>
                        <fo:table-column column-width="90pt"/>
			            <fo:table-column column-width="160pt"/>
			            <fo:table-column column-width="70pt"/>
			            <fo:table-column column-width="35pt"/>
                        <fo:table-column column-width="160pt"/>
			            <fo:table-column column-width="80pt"/>
			            <fo:table-column column-width="39pt"/>
			            <fo:table-column column-width="80pt"/>
                        <fo:table-column column-width="53pt"/>
			            <fo:table-body>
			                <fo:table-row>
			                    <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>  
					            </fo:table-cell>
                                <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>  
					            </fo:table-cell>
                                <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">SUB TOTAL :</fo:block>  
					            </fo:table-cell>
                                <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false">${totalQty?if_exists}</fo:block>  
					            </fo:table-cell>
                                <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false">${totOrderAmt?if_exists?string("##0.00")}</fo:block>  
					            </fo:table-cell>
                                <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false"></fo:block>  
					            </fo:table-cell>
                                <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" fon-size="12pt" white-space-collapse="false"></fo:block>  
					            </fo:table-cell>
                                <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>  
					            </fo:table-cell>
                                <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>  
					            </fo:table-cell>
                                <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="right" fon-size="11pt" white-space-collapse="false">${totPmtAmt?if_exists?string("##0.00")}</fo:block>  
					            </fo:table-cell>
							</fo:table-row>
			            </fo:table-body>
			        </fo:table>
          		</fo:block>
                <fo:block text-align="left" keep-together="always" >-------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
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