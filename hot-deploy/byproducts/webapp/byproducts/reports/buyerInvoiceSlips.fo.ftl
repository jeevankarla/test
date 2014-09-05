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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".2in" margin-right=".3in" margin-top=".5in" margin-bottom=".5in">
                <fo:region-body margin-top="0.5in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "buyerInvoice.pdf")}
        <#if invoiceSlipsMap?has_content>
        
        <fo:page-sequence master-reference="main" font-size="12pt">	
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        		<fo:block text-align="left" font-size="13pt" keep-together="always"  white-space-collapse="false">
        			<fo:block  keep-together="always" text-align="center" font-size="13pt" white-space-collapse="false" font-weight="bold">TAX INVOICE</fo:block>
        		</fo:block>
        	</fo:static-content>	        	
        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
        		<#assign partyInvoiceMap = invoiceSlipsMap.entrySet()> 
		        <#list  partyInvoiceMap as eachInvoice>
		        <#assign invoiceDetail = eachInvoice.getValue()>
		        <#assign fromPartyDetail = invoiceDetail.get('fromPartyDetail')>
		        <#assign toPartyDetail = invoiceDetail.get('toPartyDetail')>
		        <#assign shipment = invoiceDetail.get('shipment')>
		        <#assign billingAddress = invoiceDetail.get('billingAddress')>
		        <#assign invoice = invoiceDetail.get('invoice')>
		        <#assign invoiceItems = invoiceDetail.get('invoiceItems')>
		        <#assign invoiceTaxItems = invoiceDetail.get('invoiceTaxItems')>
		        <#assign chapterMap = invoiceDetail.get('chapterMap')>
        		<fo:block>
        			<fo:table width="100%">
			            <fo:table-column column-width="33%"/>
			            <fo:table-column column-width="33%"/>
			            <fo:table-column column-width="34%"/> 
			            <fo:table-body>
			                <fo:table-row>
			                    <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">MOTHER DAIRY-ICE CREAM DIVISION</fo:block>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">(A UNIT OF KMF LTD.) GKVK POST : BANGALORE - 65</fo:block>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">(ISSUED UNDER RULE 52A &amp; 173G)</fo:block>
					            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">ECC NO: <#if fromPartyDetail?has_content>${fromPartyDetail.get('PLA_NUMBER')?if_exists}</#if></fo:block>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">FSSAI: <#if fromPartyDetail?has_content>${fromPartyDetail.get('FSSAI_NUMBER')?if_exists}</#if></fo:block>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">DEPO: </fo:block>
					            	
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            	<#assign nowTime = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()>
					            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">AUTHENTICATED BY: </fo:block>
					            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">Date: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTime, "dd-MMM-yyyy")}</fo:block> 
					            	<#--<fo:block  keep-together="always" text-align="center" font-size="13pt" white-space-collapse="false" font-weight="bold">TAX INVOICE</fo:block>-->
					            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">ORIGINAL FOR BUYER</fo:block>
					            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">DUPLICATE FOR TRANSPORTER</fo:block>
					            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">EXTRA COPY</fo:block>
					            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
					            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">Service Tax No: <#if fromPartyDetail?has_content>${fromPartyDetail.get('SERVICETAX_NUMBER')?if_exists}</#if></fo:block> 
					            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">TIN: <#if fromPartyDetail?has_content>${fromPartyDetail.get('TIN_NUMBER')?if_exists}</#if></fo:block>
					            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">CST: <#if fromPartyDetail?has_content>${fromPartyDetail.get('CST_NUMBER')?if_exists}</#if></fo:block> 
					            </fo:table-cell>
							</fo:table-row>
							
							<fo:table-row border-style="solid">
            					<fo:table-cell >
            						<fo:block text-align="left" font-size="12pt" white-space-collapse="false" wrap-option="wrap">To: <#if billingAddress?has_content>${billingAddress.get("toName")?if_exists} </#if></fo:block>
            						<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"><#if billingAddress?has_content>${billingAddress.get("address1")?if_exists} </#if></fo:block>
            						<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"><#if billingAddress?has_content>${billingAddress.get("address2")?if_exists} </#if></fo:block>
            						<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"><#if billingAddress?has_content>${billingAddress.get("city")?if_exists} - ${billingAddress.get("postalCode")?if_exists} </#if></fo:block>  
            					</fo:table-cell>
            					<fo:table-cell>
            						<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">TIN: <#if toPartyDetail?has_content>${toPartyDetail.get('TIN_NUMBER')?if_exists}</#if></fo:block>
            						<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">CST: <#if toPartyDetail?has_content>${toPartyDetail.get('CST_NUMBER')?if_exists}</#if></fo:block>
            						<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">AREA CODE: </fo:block>
            						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
            					</fo:table-cell>
            					<fo:table-cell>
            						<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">Inv No: ${invoice.get('invoiceId')?if_exists}</fo:block>
            						<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">Date: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString((invoice.get('invoiceDate')), "dd-MMM-yyyy")}</fo:block>
            						<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">Time: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString((invoice.get('createdStamp')), "HH:mm:ss")?if_exists}</fo:block>
            						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
            					</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
			                    <fo:table-cell number-columns-spanned="3">
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">
					            		<fo:table>
			            					<fo:table-column column-width="18%"/>
			            					<fo:table-column column-width="18%"/>
			            					<fo:table-column column-width="22%"/>
			            					<fo:table-column column-width="13%"/>
			            					<fo:table-column column-width="14%"/>
			            					<fo:table-column column-width="15%"/>
			            					<fo:table-body>
			                					<fo:table-row>
			                    					<fo:table-cell border-style="solid" >
					            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">PLA No.</fo:block>  
					            					</fo:table-cell>
					            					<fo:table-cell border-style="solid" >
					            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">Registration No.</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="solid" >
					            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">Chapter No./Tarrif</fo:block>
					            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">Sub-Heading</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="solid" >
					            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">Mode of</fo:block>
					            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">Despatch</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="solid">
					            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">Vehicle</fo:block>
					            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">Reg. No.</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="solid" >
					            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">Name of the</fo:block>
					            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">Commodity</fo:block>
					            					</fo:table-cell>
												</fo:table-row>
												<fo:table-row>
			                    					<fo:table-cell border-style="solid" >
					            						<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"><#if fromPartyDetail?has_content>${fromPartyDetail.get('PLA_NUMBER')?if_exists}</#if></fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="solid" >
					            						<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"><#if fromPartyDetail?has_content>${fromPartyDetail.get('REGISTRATION_NUMBER')?if_exists}</#if></fo:block>
					            						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>   
					            					</fo:table-cell>
					            					<fo:table-cell border-style="solid" >
				            							<#if chapterMap?has_content>
				            								<#assign chapterDetails = chapterMap.entrySet()>
				            								<#list chapterDetails as eachDetail>
				            									<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">${eachDetail.getValue()?if_exists}</fo:block>
				            								</#list>
				            							</#if>
					            						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="solid">
					            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">By Road</fo:block>
					            						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="solid">
					            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">${shipment.get('vehicleId')?if_exists}</fo:block>
					            						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="solid">
					            						<#if chapterMap?has_content>
				            								<#assign chapterDetails = chapterMap.entrySet()>
				            								<#list chapterDetails as eachDetail>
				            									<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">${eachDetail.getKey()?if_exists}</fo:block>
				            								</#list>
				            							</#if>
					            						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            					</fo:table-cell>
												</fo:table-row>
			            					</fo:table-body>
			        					</fo:table>
					            	</fo:block>  
					            </fo:table-cell>
							</fo:table-row>
							
							<fo:table-row>
			                    <fo:table-cell number-columns-spanned="3">
					            	<fo:block text-align="left" font-size="12pt" white-space-collapse="false">
					            		<fo:table border-width="1pt" border-style="solid">
			            					<fo:table-column column-width="30%"/>
			            					<fo:table-column column-width="30%"/>
			            					<fo:table-column column-width="25%"/>
			            					<fo:table-column column-width="15%"/>
			            					<fo:table-body>
			                					<fo:table-row>
			                    					<fo:table-cell border-style="solid">
					            						<fo:block text-align="left" font-size="12pt" white-space-collapse="false" >RANGE: </fo:block>
					            						<fo:block text-align="left" font-size="12pt" white-space-collapse="false" wrap-option="wrap">${taxAuthority.get('taxRange')?if_exists}</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="solid">
					            						<fo:block  text-align="left" font-size="12pt" white-space-collapse="false" wrap-option="wrap">DIVISION: </fo:block>
					            						<fo:block  text-align="left" font-size="12pt" white-space-collapse="false" wrap-option="wrap">${taxAuthority.get('taxDivision')?if_exists}</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="solid">
					            						<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">DC No: ${shipment.get('shipmentId')?if_exists}</fo:block>
					            						<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">Removal Time:</fo:block>
					            						<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">PLA/CENVAT</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="solid">
					            						<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">Date : </fo:block>
					            						<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTime, 'dd/MM/yy')}</fo:block>
					            					</fo:table-cell>
												</fo:table-row>
			            					</fo:table-body>
			        					</fo:table>
					            	</fo:block>  
					            </fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell number-columns-spanned="3">
									<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
								</fo:table-cell>
							</fo:table-row>
							
							<fo:table-row>
			                    <fo:table-cell number-columns-spanned="3">
					            	<fo:block text-align="left" font-size="12pt" white-space-collapse="false">
					            		<fo:table border-width="1pt" border-style="solid">
			            					<fo:table-column column-width="4%"/>
			            					<fo:table-column column-width="22%"/>
			            					<fo:table-column column-width="10%"/>
			            					<fo:table-column column-width="8%"/>
			            					<fo:table-column column-width="8%"/>
			            					<fo:table-column column-width="8%"/>
			            					<fo:table-column column-width="8%"/>
			            					<fo:table-column column-width="12%"/>
			            					<fo:table-column column-width="8%"/>
			            					<fo:table-column column-width="12%"/>
			            					<fo:table-body>
			                					<fo:table-row>
			                    					<fo:table-cell border-style="solid">
					            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">SL.</fo:block>
					            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">No.</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="solid">
					            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">Description</fo:block>
					            						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="solid">
					            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">Batch</fo:block>
					            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">No.</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="solid">
					            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">Qty</fo:block>
					            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">(Ltrs)</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="solid">
					            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">Qty</fo:block>
					            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">(Packs)</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="solid">
					            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">FreeQty</fo:block>
					            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">(Ltrs)</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="solid">
					            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">MRP</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="solid">
					            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">MRP Value</fo:block>
					            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold"> Rs.</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="solid">
					            						<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">Exfactory</fo:block>
					            						<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">Rate/Pck</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="solid">
					            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">Amount</fo:block>
					            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">Rs </fo:block>
					            					</fo:table-cell>
												</fo:table-row>
												<#assign totalAmt = 0>
												<#assign grandTotal = 0>
												<#assign totalMRPValue = 0>
												<#assign totalLtr = 0>
												<#assign totalQty = 0>
												<#if invoiceItems?has_content>
													<#assign slNo = 1>
													<#list invoiceItems as eachItem>
														<fo:table-row>
					                    					<fo:table-cell >
							            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">${slNo?if_exists}</fo:block>
							            					</fo:table-cell>
							            					<fo:table-cell >
							            						<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(eachItem.get('itemDescription')?if_exists)),21)}</fo:block>
							            					</fo:table-cell>
							            					<fo:table-cell >
							            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">${eachItem.get('batchNo')?if_exists}</fo:block>
							            					</fo:table-cell>
							            					<#assign totalLtr = totalLtr+eachItem.get('quantityLtr')>
							            					<fo:table-cell >
							            						<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${eachItem.get('quantityLtr')?if_exists?string('#0.00')}</fo:block>
							            					</fo:table-cell>
							            					<#assign totalQty = totalQty+eachItem.get('quantity')>
							            					<fo:table-cell >
							            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">${eachItem.get('quantity')?if_exists}</fo:block>
							            					</fo:table-cell>
							            					<fo:table-cell >
							            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">0.00</fo:block>
							            					</fo:table-cell>
							            					<fo:table-cell >
							            						<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${eachItem.get('mrpPrice')?if_exists?string("#0.00")}</fo:block>
							            					</fo:table-cell>
							            					<#assign mrpValue = eachItem.get('mrpPrice') * eachItem.get('quantity')>
							            					<fo:table-cell >
							            						<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${mrpValue?if_exists?string("#0.00")}</fo:block>
							            					</fo:table-cell>
							            					<fo:table-cell >
							            						<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${eachItem.get('defaultPrice')?if_exists?string("#0.00")}</fo:block>
							            					</fo:table-cell>
							            					
							            					<#assign totalMRPValue = totalMRPValue+mrpValue>
							            					<#assign totalItemAmt = eachItem.get('defaultPrice')*eachItem.get('quantity')>
							            					<#assign grandTotal = grandTotal+totalItemAmt>
							            					<#assign totalAmt = totalAmt+totalItemAmt>
							            					<fo:table-cell >
							            						<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${totalItemAmt?if_exists?string("#0.00")}</fo:block>
							            					</fo:table-cell>
							            					<#assign slNo = slNo+1>
														</fo:table-row>
													</#list>
												</#if>
												<fo:table-row border-style="solid">
			                    					<fo:table-cell>
					            						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell>
					            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">Total</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell>
					            						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell >
					            						<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${totalLtr?if_exists?string("#0.00")}</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell>
					            						<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${totalQty?if_exists?string("#0.00")}</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell>
					            						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell >
					            						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell>
					            						<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${totalMRPValue?if_exists?string("#0.00")}</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell>
					            						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell>
					            						<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${totalAmt?if_exists?string("#0.00")}</fo:block>
					            					</fo:table-cell>
												</fo:table-row>
												<#if invoiceTaxItems?has_content>
													<#assign taxDetails = invoiceTaxItems.entrySet()>
													<#list taxDetails as eachTax>
														<fo:table-row border-style="solid">
					                    					<fo:table-cell number-columns-spanned="3" >
							            						<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false"></fo:block>
							            					</fo:table-cell>
							            					<#assign text = "">
							            					<#if eachTax.getKey()=="BED_SALE">
							            						<#assign text = "Excise Duty (Deposit) Including CESS 2.06%">
							            					<#elseif eachTax.getKey()=="CST_SALE">
							            						<#assign text = "Central Sales Tax(CST) 2.0%">
							            					<#elseif eachTax.getKey()=="SERTAX_SALE">
							            						<#assign text = "Service Tax ">
							            					<#else>
							            						<#assign text = "Value Added Tax(VAT) ">
							            					</#if>
							            					<fo:table-cell number-columns-spanned="6" >
							            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false"> ${text?if_exists}</fo:block>
							            					</fo:table-cell>
							            					<fo:table-cell >
							            						<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${eachTax.getValue()?if_exists?string("#0.00")}</fo:block>
							            					</fo:table-cell>
							            					<#assign grandTotal = grandTotal+eachTax.getValue()>
														</fo:table-row>
													</#list>
												</#if>
												<fo:table-row border-style="solid">
			                    					<fo:table-cell number-columns-spanned="9" >
					            						<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">GRAND TOTAL</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell>
					            						<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${grandTotal?if_exists?string("#0.00")}</fo:block>
					            					</fo:table-cell>
												</fo:table-row>
												<fo:table-row border-style="solid">
			                    					<fo:table-cell number-columns-spanned="10" >
					            						<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">(In Words:${Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble(grandTotal?string("#0")), "%rupees-and-paise", locale).toUpperCase()} ONLY)</fo:block>
					            					</fo:table-cell>
												</fo:table-row>
			            					</fo:table-body>
			        					</fo:table>
					            	</fo:block>  
					            </fo:table-cell>
							</fo:table-row>
			            </fo:table-body>
			        </fo:table>
          		</fo:block>
          		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				<fo:block text-align="left" font-size="12pt" white-space-collapse="false">1. Certificate that the particulars given above are true &amp; correct &amp; the amount indicated represents the actual charged &amp; that there is no flow of additional consideration directly or indirectly from the buyer.</fo:block>
				<fo:block text-align="left" font-size="12pt" white-space-collapse="false">2. All disputes regarding this bill shall be decided at Bangalore &amp; Bangalore Courts alone shall have jurisdiction.</fo:block>
				<fo:block text-align="left" font-size="12pt" white-space-collapse="false">3. Please settle payment of this bill within fifteen days. </fo:block>
				<fo:block text-align="left" font-size="12pt" white-space-collapse="false">4. In case of CST rate of tax, if the 'C' form is not given, the difference in tax rate is to your account.</fo:block>
				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">for KCMPF Ltd, Unit: Mother Dairy</fo:block>
				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">AUTHORISED SIGNATORY</fo:block>
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