
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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-right=".2in" margin-top=".1in">
                <fo:region-body margin-top="0.1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1.5in"/>
            </fo:simple-page-master> 
        </fo:layout-master-set>
              <#if finalMap?has_content>
              
        <fo:page-sequence master-reference="main" font-size="9pt">	
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
				<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
                <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
				<fo:block  keep-together="always" text-align="center"  font-weight="bold"   font-size="12pt" white-space-collapse="false">${reportHeader.description?if_exists} </fo:block>
				<fo:block  text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">${BOAddress?if_exists}</fo:block>
        		<fo:block text-align="center" font-size="12pt" font-weight="bold" >PURCHASE Tax REPORT</fo:block>
				<fo:block text-align="center" font-size="12pt" font-weight="bold" >FOR THE PERIOD ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(daystart, "dd/MM/yyyy")?if_exists} To:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayend, "dd/MM/yyyy")?if_exists} </fo:block>
        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                <fo:block text-align="right"    font-size="9pt" >Page - <fo:page-number/></fo:block>
                
				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
        		<fo:block>
             		<fo:table >
             		    <fo:table-column column-width="4%"/>
			            <fo:table-column column-width="13%"/>
			            <fo:table-column column-width="14%"/>
			            <fo:table-column column-width="9%"/>
	                    <fo:table-column column-width="9%"/>
			            <fo:table-column column-width="9%"/>
			            <fo:table-column column-width="10%"/>
			            <fo:table-column column-width="6%"/>
			            <fo:table-column column-width="10%"/>
			            <fo:table-column column-width="10%"/>
			             <fo:table-column column-width="10%"/>
			            <fo:table-column column-width="10%"/>
			            
			            <fo:table-body>
			            	
			                 <fo:table-row>
								
			                    <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">S.No</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="10pt"  font-weight="bold" white-space-collapse="false">Seller</fo:block>
					            	<fo:block  text-align="center" font-size="10pt"  font-weight="bold" white-space-collapse="false">Taxpayer</fo:block>
					            	<fo:block  text-align="center" font-size="10pt"  font-weight="bold" white-space-collapse="false">Identification</fo:block>
					            	<fo:block  text-align="center" font-size="10pt"  font-weight="bold" white-space-collapse="false">Number</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Name of Seller</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Invoice No</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Invoice</fo:block>
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Date</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">commodity</fo:block>
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">code</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Purchase</fo:block>
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Value</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Rate of</fo:block>
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Tax</fo:block>
					            </fo:table-cell>
					            <#if taxType=="VAT_PUR">
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Vat Amount</fo:block>
					            </fo:table-cell>
					             </#if>
					             <#if taxType=="CST_PUR">
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Cst Amount</fo:block>
					            </fo:table-cell>
					            </#if>
					            <#if taxType=="EXCISE_DUTY">
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Excise Amount</fo:block>
					            </fo:table-cell>
					            </#if>
					             <#if taxType=="ENTRY_TAX">
						            <fo:table-cell border-style="solid">
						            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Entry Tax</fo:block>
						            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Amount</fo:block>
						            </fo:table-cell>
					            </#if>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Total Amount</fo:block>
					            </fo:table-cell>
					             
							</fo:table-row>
			                  
								
								<#assign sr = 0>
								<#assign totVal = 0>
								<#assign totTaxVal = 0>
								<#assign totTaxSurcharge = 0>
								<#assign totPurVal = 0>
								<#assign finalMapEntryList = finalMap.entrySet()>
								<#list finalMapEntryList as finalMapEntry>
								<#assign  taxCharge= finalMapEntry.getKey()>
								<#assign branchWiseList = finalMapEntry.getValue()>
								<#assign EntryList = branchWiseList .entrySet()>
								<#list EntryList as Entry>
								<#assign  invList= Entry.getKey()>
								<#assign invoiceWiseList = Entry.getValue()>
								<#list invoiceWiseList as eachEntry>
								<#assign sr = sr+1> 
								
							   <fo:table-row>
								
			                    <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="9pt" white-space-collapse="false">${sr}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="9pt" white-space-collapse="false">${eachEntry.get("partyTinNo")?if_exists}</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="left" font-size="9pt" white-space-collapse="false">${eachEntry.get("partyIdFromName")?if_exists}</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="left" font-size="9pt" white-space-collapse="false">${eachEntry.get("supplierInvId")?if_exists}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="left" font-size="9pt" white-space-collapse="false">${eachEntry.get("invoiceDate")?if_exists}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${eachEntry.get("productId")?if_exists}</fo:block>
					            </fo:table-cell>
					            <#assign totPurVal = totPurVal+eachEntry.get("baseValue")?if_exists>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${eachEntry.get("baseValue")?if_exists}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${eachEntry.get("taxPercentage")?if_exists}</fo:block>
					            	<#if eachEntry.get("taxSurChgPer")?has_content && eachEntry.get("taxSurChgPer")!=0>
					            	<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">&#160;</fo:block>
					            	<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${eachEntry.get("taxSurChgPer")?if_exists}</fo:block>
					            	</#if>
					            </fo:table-cell>
					            <#if taxType?has_content>
					            <fo:table-cell border-style="solid">
					            	<#assign totTaxVal = totTaxVal+eachEntry.get("taxValue")?if_exists>
					            	<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${eachEntry.get("taxValue")?if_exists}</fo:block>
					            	<#if eachEntry.get("taxSurChargeValue")?has_content && eachEntry.get("taxSurChargeValue")!=0>
					            	<#assign totTaxSurcharge = totTaxSurcharge+eachEntry.get("taxSurChargeValue")?if_exists>
					            	<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">&#160;</fo:block>
					            	<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${eachEntry.get("taxSurChargeValue")?if_exists}</fo:block>
					            	</#if>
					            </#if>
					            </fo:table-cell>
					           <#assign totVal = totVal+eachEntry.get("total")?if_exists>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${eachEntry.get("total")?if_exists}</fo:block>
					            </fo:table-cell>
					             
							</fo:table-row>
							 
							  </#list>
			                  </#list>
			                  </#list>
							<fo:table-row>
								
			                    <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="9pt" white-space-collapse="false" font-weight="bold"></fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="9pt" white-space-collapse="false" font-weight="bold">Total</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="left" font-size="9pt" white-space-collapse="false" font-weight="bold"></fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" font-size="9pt" white-space-collapse="false" font-weight="bold"></fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="left" font-size="9pt" white-space-collapse="false" font-weight="bold"></fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" font-size="9pt" white-space-collapse="false" font-weight="bold"></fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" font-size="9pt" white-space-collapse="false" font-weight="bold">${totPurVal}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" font-size="9pt" white-space-collapse="false" font-weight="bold"></fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" font-size="9pt" white-space-collapse="false" font-weight="bold">${totTaxVal+totTaxSurcharge}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" font-size="9pt" white-space-collapse="false" font-weight="bold">${totVal?if_exists}</fo:block>
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
 	            	No Orders Found
	       		 </fo:block>
	    	</fo:flow>
		</fo:page-sequence>	
    </#if> 
 </fo:root>
</#escape>