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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="11in"  margin-left="0.5in" margin-right="0.5in" margin-top=".1in">
                <fo:region-body margin-top="0.1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1.5in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
   
        <#if orderList?has_content>
        <fo:page-sequence master-reference="main" font-size="12pt">	
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
				<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
 			        <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
        			<fo:block keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${reportHeader.description?if_exists}</fo:block>
        			<fo:block keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${reportSubHeader.description?if_exists}</fo:block>				
				<fo:block text-align="center" font-size="12pt" font-weight="bold" >Indent Register Report</fo:block>
				<fo:block text-align="center" keep-together="always"  white-space-collapse="false" font-family="Courier,monospace" font-size = "10pt"> From ${fromDate} - ${thruDate} </fo:block>
          			<fo:block text-align="left"  keep-together="always" font-family="Courier,monospace" white-space-collapse="false" font-size="8pt"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;    Print Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
                <#--<fo:block linefeed-treatment="preserve">&#xA;</fo:block>-->
		       
        		<fo:block>
             		<fo:table >
             			
             		    <fo:table-column column-width="11%"/>
             		    <fo:table-column column-width="14%"/>
             		    <fo:table-column column-width="15%"/>
             		    <fo:table-column column-width="13%"/>
             		    <fo:table-column column-width="10%"/>
             		    <fo:table-column column-width="10%"/>
             		    <fo:table-column column-width="11%"/>
             		    <fo:table-column column-width="15%"/>
             		    <#--<fo:table-column column-width="9%"/>
             		    <fo:table-column column-width="7%"/>
             		    <fo:table-column column-width="6%"/>
             		    <fo:table-column column-width="4%"/>
             		    <fo:table-column column-width="4%"/>
             		    <fo:table-column column-width="7%"/>
             		    <fo:table-column column-width="7%"/>
             		    <fo:table-column column-width="5%"/>
			             <fo:table-column column-width="5%"/>-->
			            <fo:table-body>
			            	
			                <fo:table-row>
								
			                    <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="11pt" font-weight="bold" white-space-collapse="false">Indent Date</fo:block>	
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="11pt"  font-weight="bold" white-space-collapse="false">IndentSeqId</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="11pt" font-weight="bold" white-space-collapse="false">Product Name</fo:block>	
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="11pt" font-weight="bold" white-space-collapse="false">Weaver Name</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="11pt" font-weight="bold" white-space-collapse="false">Indent Qty(Kgs)</fo:block>	
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="11pt" font-weight="bold" white-space-collapse="false">Indent Rate</fo:block>	
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="11pt" font-weight="bold" white-space-collapse="false">Indent Value</fo:block>	
					            </fo:table-cell>
					            <#--<fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="8pt" font-weight="bold" white-space-collapse="false">PO Date</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="8pt" font-weight="bold" white-space-collapse="false">PoSeqId</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="9pt" font-weight="bold" white-space-collapse="false">PO Qty</fo:block>	
					            </fo:table-cell>-->
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="11pt" font-weight="bold" white-space-collapse="false">Supplier Name</fo:block>
					            </fo:table-cell>
					            <#--<fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="8pt" font-weight="bold" white-space-collapse="false">Sal Date</fo:block>	
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="8pt" font-weight="bold" white-space-collapse="false">Sal Invoice</fo:block>	
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="8pt" font-weight="bold" white-space-collapse="false">Sal Value</fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="8pt" font-weight="bold" white-space-collapse="false">Transporter</fo:block>	
					            </fo:table-cell>
 								<fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="8pt" font-weight="bold" white-space-collapse="false">Mil Invoice</fo:block>	
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="8pt" font-weight="bold" white-space-collapse="false">Payment Receipt</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="8pt" font-weight="bold" white-space-collapse="false">Amount</fo:block>
					            </fo:table-cell>-->   
							</fo:table-row>
			                  <#list orderList as OrderIdList>
			              
	                             <fo:table-row>
								
			                    <fo:table-cell border-style="solid"> 
					            	<fo:block  text-align="left" white-space-collapse="false" font-size="11pt">${OrderIdList.orderDate?if_exists}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  text-align="left"  <#if OrderIdList.orderNo=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false" font-size="11pt">${OrderIdList.orderNo?if_exists}</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					             <#assign productIds=[]>
					            <#assign productIds = orderPrepMap.get(OrderIdList.orderId)>
					            <#list productIds as productId>
					            <#assign productDetails = delegator.findOne("Product", {"productId" :productId}, true)>
					            	<fo:block   text-align="left" white-space-collapse="false" font-size="11pt">
					            	${productDetails.get("productName")?if_exists}
					            	</fo:block>
					            	<fo:block   text-align="left" white-space-collapse="false" font-size="11pt">&#160;</fo:block>
					            </#list>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block   text-align="left" white-space-collapse="false" font-size="11pt">${OrderIdList.weaverName?if_exists}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" <#if OrderIdList.orderNo=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false" font-size="11pt">${OrderIdList.Qty?if_exists}</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" white-space-collapse="false" font-size="11pt">${OrderIdList.indentPrice?if_exists}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" <#if OrderIdList.orderNo=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false" font-size="11pt">${OrderIdList.indentValue?if_exists}</fo:block>
					            </fo:table-cell>
					            <#--<fo:table-cell border-style="solid">
					            	<fo:block   text-align="left" white-space-collapse="false" font-size="8pt">${OrderIdList.poDate?if_exists}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="left" white-space-collapse="false" font-size="8pt">${OrderIdList.poSquenceNo?if_exists}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" font-size="8pt"  white-space-collapse="false">${OrderIdList.poQty?if_exists}</fo:block>
					            </fo:table-cell>-->
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="left" white-space-collapse="false" font-size="11pt">${OrderIdList.supplierName?if_exists}</fo:block>
					            </fo:table-cell>
					            <#--<fo:table-cell border-style="solid">
					            	<fo:block   text-align="left" white-space-collapse="false" font-size="8pt">${OrderIdList.salDate?if_exists}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" white-space-collapse="false" font-size="8pt">${OrderIdList.salInv?if_exists}</fo:block>
					            </fo:table-cell>					             
					             <fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" white-space-collapse="false" font-size="8pt">${OrderIdList.salVal?if_exists}</fo:block>
					            </fo:table-cell>
 								<fo:table-cell border-style="solid">
					            	<fo:block   text-align="left" white-space-collapse="false" font-size="8pt">${OrderIdList.transporter?if_exists}</fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block   text-align="left" white-space-collapse="false" font-size="8pt">${OrderIdList.milInv?if_exists}</fo:block>
					            </fo:table-cell> 
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" font-size="8pt"  white-space-collapse="false">${OrderIdList.paymentReceipt?if_exists}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" font-size="8pt"  white-space-collapse="false">${OrderIdList.amount?if_exists}</fo:block>
					            </fo:table-cell>-->					            					            
							</fo:table-row>
							</#list>
						</fo:table-body>
					</fo:table>
				</fo:block>
			</fo:flow>
		</fo:page-sequence>
		<#else>
    	<fo:page-sequence master-reference="main">
	    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	       		 <fo:block font-size="14pt">
 	            	No Orders Found.
	       		 </fo:block>
	    	</fo:flow>
		</fo:page-sequence>	
    </#if> 
 </fo:root>
</#escape>