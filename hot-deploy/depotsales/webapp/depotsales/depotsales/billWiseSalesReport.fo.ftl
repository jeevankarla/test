
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
        <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-left=".1in" margin-right=".1in" margin-top=".1in">
            <fo:region-body margin-top="0.1in"/>
            <fo:region-before extent="1in"/>
            <fo:region-after extent="1.5in"/>
        </fo:simple-page-master>
    </fo:layout-master-set>
    ${setRequestAttribute("OUTPUT_FILENAME", "IndentReport.pdf")}
    <#if finalList?has_content>
    <fo:page-sequence master-reference="main" font-size="10pt">	
    	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace"></fo:static-content>
    	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
			<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
            <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
			<fo:block keep-together="always" text-align="center"  font-weight="bold"   font-size="12pt" white-space-collapse="false">${reportHeader.description?if_exists} </fo:block>
			<fo:block text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">${BOAddress?if_exists}</fo:block>
    		<fo:block keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            <fo:block text-align="right" font-size="10pt" >Page - <fo:page-number/></fo:block>
            <fo:block text-align="center" font-size="10pt" font-weight="bold">BILL WISE SALE REPORT:<fo:inline font-weight="bold" > ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(daystart, "dd-MMM-yyyy")?if_exists} To:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayend, "dd-MMM-yyyy")?if_exists} </fo:inline></fo:block>
            <fo:block linefeed-treatment="preserve">&#xA;</fo:block><fo:block linefeed-treatment="preserve">&#xA;</fo:block>
        
	        <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	        <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	        <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
         	 
    		<fo:block>
         		<fo:table border-style="solid">
         		    <fo:table-column column-width="2%"/>
		            <fo:table-column column-width="6%"/>
		            <fo:table-column column-width="8%"/>
		            <fo:table-column column-width="10%"/>
		            
		            <fo:table-column column-width="8%"/>
		            <fo:table-column column-width="9%"/>
		            <fo:table-column column-width="7%"/>
		            
		            <fo:table-column column-width="6%"/>
		            <fo:table-column column-width="6%"/>
		            <fo:table-column column-width="6%"/>
		            <fo:table-column column-width="7%"/>
		            
		            <fo:table-column column-width="6%"/>
		            <fo:table-column column-width="6%"/>
		            <fo:table-column column-width="7%"/>
		            <fo:table-column column-width="6%"/>
		            
		            <fo:table-body>
		                <fo:table-row>
		                    <fo:table-cell border-style="solid">
				            	<fo:block text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold">S No</fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">Date</fo:block>
				            </fo:table-cell >
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">Bill No.</fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">Party Name</fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">Type</fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">District</fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">Branch</fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">State</fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">RO</fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">Qty</fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">Amount</fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">Vat/Other Amt</fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">Subsidy Qty</fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">10% Subsidy</fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">Scheme</fo:block>
				            </fo:table-cell>
						</fo:table-row>
						
		                     <#assign sr=1>
		                  <#list finalList as eachInvoice>
		                <fo:table-row>
		                   <fo:table-cell border-style="solid">
				            	<fo:block text-align="left" font-size="10pt" white-space-collapse="false">${sr}</fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${eachInvoice.invoiceDate}</fo:block>
				            </fo:table-cell >
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="left" font-size="10pt" white-space-collapse="false">${eachInvoice.billno}</fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="left" font-size="10pt" white-space-collapse="false">${eachInvoice.partyName}</fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="left" font-size="10pt" white-space-collapse="false">${eachInvoice.partyType}</fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="left" font-size="10pt" white-space-collapse="false">${eachInvoice.district}</fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="left" font-size="10pt" white-space-collapse="false">${eachInvoice.branch}</fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="left" font-size="10pt" white-space-collapse="false">${eachInvoice.state}</fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="left" font-size="10pt" white-space-collapse="false">${eachInvoice.ro}</fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="right" font-size="10pt" white-space-collapse="false">${eachInvoice.qty}</fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="right" font-size="10pt" white-space-collapse="false">${eachInvoice.amount}</fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="right" font-size="10pt" white-space-collapse="false">Vat/Other Amt</fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="right" font-size="10pt" white-space-collapse="false">${eachInvoice.subsidyQty}</fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="right" font-size="10pt" white-space-collapse="false">${eachInvoice.subsidyAmount}</fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="left" font-size="10pt" white-space-collapse="false">${eachInvoice.scheme}</fo:block>
				            </fo:table-cell>
						</fo:table-row>
						<#assign sr=sr+1>
						</#list>
						
						
						<fo:table-row>
		                   <fo:table-cell border-style="solid">
				            	<fo:block text-align="left" font-size="10pt" white-space-collapse="false"></fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold">TOTAL</fo:block>
				            </fo:table-cell >
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="left" font-size="10pt" white-space-collapse="false"></fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="left" font-size="10pt" white-space-collapse="false"></fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="left" font-size="10pt" white-space-collapse="false"></fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="left" font-size="10pt" white-space-collapse="false"></fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="left" font-size="10pt" white-space-collapse="false"></fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="left" font-size="10pt" white-space-collapse="false"></fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="left" font-size="10pt" white-space-collapse="false"></fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="right" font-size="10pt" white-space-collapse="false" font-weight="bold">${totQty}</fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="right" font-size="10pt" white-space-collapse="false" font-weight="bold">${totAmount}</fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="right" font-size="10pt" white-space-collapse="false"></fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="right" font-size="10pt" white-space-collapse="false" font-weight="bold">${totsubquant}</fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="right" font-size="10pt" white-space-collapse="false" font-weight="bold">${totsubAmount?if_exists?string("##0.00")}</fo:block>
				            </fo:table-cell>
				            <fo:table-cell border-style="solid">
				            	<fo:block text-align="left" font-size="10pt" white-space-collapse="false"></fo:block>
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
	        	No Records Found.
	   		 </fo:block>
		</fo:flow>
	</fo:page-sequence>	
</#if> 
 </fo:root>
</#escape>