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
                <fo:region-body margin-top="1.4in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "VatInvoiceSeqNoReport.pdf")}
        <#if errorMessage?has_content>
	<fo:page-sequence master-reference="main">
	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	   <fo:block font-size="14pt">
	           ${errorMessage}.
        </fo:block>
	</fo:flow>
	</fo:page-sequence>	
	<#else>
       <#if crInvoices?has_content>
	        <fo:page-sequence master-reference="main" font-size="10pt">	
	        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
	        	    <#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
                    <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
	        	    
	        		<fo:block text-align="center" font-weight="bold" font-size="12pt" keep-together="always"  white-space-collapse="false">&#160;${reportHeader.description?if_exists}.</fo:block>
                	<fo:block text-align="center" font-weight="bold" font-size="12pt" keep-together="always"  white-space-collapse="false">&#160;${reportSubHeader.description?if_exists}.</fo:block>
                	<fo:block text-align="center" font-size="12pt" keep-together="always"  white-space-collapse="false" font-weight="bold">&#160;VAT Invoice Sequence No Report  - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")} </fo:block>
          			<fo:block font-size="12pt" text-align="left">==============================================================================================</fo:block>  
        			<fo:block text-align="left" font-weight="bold" font-size="12pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">&#160;Retailer Retailer                        Invoice    BoS-No       Basic      Tax        Net</fo:block>
        			<fo:block text-align="left" font-weight="bold" font-size="12pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">&#160;Code     Name                            Number                  Amount     Amount     Amount</fo:block>
	        		<fo:block font-size="12pt" text-align="left">==============================================================================================</fo:block>  
	        	</fo:static-content>	        	
	        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
        			<fo:block>
        				<fo:table>
		                    <fo:table-column column-width="75pt"/>
		            		<fo:table-column column-width="225pt"/>
		            		<fo:table-column column-width="90pt"/>
		            		<fo:table-column column-width="65pt"/>	
		            		<fo:table-column column-width="70pt"/>
		                    <fo:table-column column-width="70pt"/>	
		            		<fo:table-column column-width="80pt"/>
		                    <fo:table-body>
		                    	<#assign grandTotal = 0>
		                    	<#assign dayWiseInvoices = crInvoices.entrySet()>
       							<#list dayWiseInvoices as eachDayDetails>
       								<#assign eachDayInvoiceDetail = eachDayDetails.getValue()>
       								<#assign dayTotal = 0>
       								<fo:table-row>
					                    <fo:table-cell number-columns-spanned="7">
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">Invoice Date: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(eachDayDetails.getKey(), "dd/MM/yyyy")}</fo:block>  
							            </fo:table-cell>
									</fo:table-row>
									<fo:table-row>
					                    <fo:table-cell number-columns-spanned="7">
							            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
							            </fo:table-cell>
									</fo:table-row>
									<#assign returnAmtMap = {}>
									<#if creditNoteMap?has_content && creditNoteMap.get(eachDayDetails.getKey())?exists>
										<#assign returnAmtMap = creditNoteMap.get(eachDayDetails.getKey())>
									</#if>
       								<#list eachDayInvoiceDetail as eachInvoiceDetail>
					                    <fo:table-row>
								            <fo:table-cell>
								            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${eachInvoiceDetail.get("facilityId")?if_exists}</fo:block>  
								            </fo:table-cell>
								            <fo:table-cell>
								            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(eachInvoiceDetail.get("facilityName")?if_exists)),30)?if_exists}</fo:block>  
								            </fo:table-cell>
								            <#if invoiceSequenceNumMap?has_content && invoiceSequenceNumMap.get(eachInvoiceDetail.get('invoiceId'))?exists>
								            	<fo:table-cell>
								            		<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${invoiceSequenceNumMap.get(eachInvoiceDetail.get("invoiceId"))?if_exists}</fo:block>  
								            	</fo:table-cell>
								            	<fo:table-cell>
								            		<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>  
								            	</fo:table-cell>
								            <#else>
								            	<fo:table-cell>
								            		<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>  
								            	</fo:table-cell>
								            	<fo:table-cell>
								            		<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${eachInvoiceDetail.get("invoiceId")?if_exists}</fo:block>  
								            	</fo:table-cell>
								            </#if>
								            <fo:table-cell>
								            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${eachInvoiceDetail.get("amount")?if_exists?string("#0.00")}</fo:block>  
								            </fo:table-cell>
								            
								            <#assign taxAmt = 0>
								            	<#assign taxAmt = eachInvoiceDetail.get("taxAmount")>
								            	<fo:table-cell>
								            		<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${eachInvoiceDetail.get("taxAmount")?if_exists}</fo:block>  
								            	</fo:table-cell>
								            
								            <#assign netAmt = eachInvoiceDetail.get('amount')+taxAmt>
								            <fo:table-cell>
								            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${netAmt?if_exists?string("#0.00")}</fo:block>  
								            </fo:table-cell>
								            <#assign dayTotal = dayTotal+netAmt>
										</fo:table-row>
									</#list>
									<fo:table-row>
						            	<fo:table-cell>
							            	<fo:block>-----------------------------------------------------------------------------------------------------------------</fo:block>  
							            </fo:table-cell>
							        </fo:table-row>
									<fo:table-row>
						            	<fo:table-cell number-columns-spanned="6">
								            <fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false"> Sub Total</fo:block>  
								        </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${dayTotal?if_exists?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							        </fo:table-row>
							        <fo:table-row>
						            	<fo:table-cell>
							            	<fo:block>-----------------------------------------------------------------------------------------------------------------</fo:block>
							            </fo:table-cell>
							        </fo:table-row>
							        <#assign grandTotal = grandTotal+dayTotal>
								</#list>
								<fo:table-row>
						        	<fo:table-cell number-columns-spanned="6">
								    	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold"> Grand Total</fo:block>  
								    </fo:table-cell>
							        <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${grandTotal?if_exists?string("#0.00")}</fo:block>  
							        </fo:table-cell>
							    </fo:table-row>
							    <fo:table-row>
					            	<fo:table-cell>
						            	<fo:block>-----------------------------------------------------------------------------------------------------------------</fo:block>
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
 </#if>
 </fo:root>
</#escape>