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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-right=".3in" margin-top=".5in" margin-bottom=".5in">
                <fo:region-body margin-top="1.7in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "buyerInvoice.pdf")}
        <#if invoiceSlipsMap?has_content>
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
        <fo:page-sequence master-reference="main" font-size="10pt">	
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        		<fo:block text-align="left" font-size="13pt" keep-together="always"  white-space-collapse="false">
        			<fo:table>
			            <fo:table-column column-width="200pt"/>
			            <fo:table-column column-width="200pt"/>
			            <fo:table-column column-width="200pt"/> 
			            <fo:table-body>
			                <fo:table-row>
			                    <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">MOTHER DAIRY-ICE CREAM DIVISION</fo:block>
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">(A UNIT OF KMF LTD.) GKVK POST : BANGALORE - 65</fo:block>
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">(ISSUED UNDER RULE 52A &amp; 173G)</fo:block>
					            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">ECC NO: <#if fromPartyDetail?has_content>${fromPartyDetail.get('PLA_NUMBER')?if_exists}</#if></fo:block>
					            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">DEPO: </fo:block>
					            	
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            	<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">AUTHENTICATED BY: </fo:block>
					            	<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">Date: </fo:block> 
					            	<fo:block  keep-together="always" text-align="center" font-size="13pt" white-space-collapse="false" font-weight="bold">TAX INVOICE</fo:block>
					            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">ORIGINAL FOR BUYER</fo:block>
					            	<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">DUPLICATE FOR TRANSPORTER</fo:block>
					            	<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">EXTRA COPY</fo:block>
					            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
					            	<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">Service Tax No: <#if fromPartyDetail?has_content>${fromPartyDetail.get('SERVICETAX_NUMBER')?if_exists}</#if></fo:block> 
					            	<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">TIN: <#if fromPartyDetail?has_content>${fromPartyDetail.get('TIN_NUMBER')?if_exists}</#if></fo:block>
					            	<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">CST: <#if fromPartyDetail?has_content>${fromPartyDetail.get('CST_NUMBER')?if_exists}</#if></fo:block> 
					            </fo:table-cell>
							</fo:table-row>
			            </fo:table-body>
			        </fo:table>
        		</fo:block>
        	</fo:static-content>	        	
        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
        		<fo:block>
             		<fo:table border-width="1pt" border-style="dotted">
			            <fo:table-column column-width="600pt"/>
			            <fo:table-body>
			                <fo:table-row>
			                    <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">
					            		<fo:table border-width="1pt" border-style="dotted">
			            					<fo:table-column column-width="200pt"/>
			            					<fo:table-column column-width="200pt"/>
			            					<fo:table-column column-width="200pt"/>
			            					<fo:table-body>
			                					<fo:table-row>
			                    					<fo:table-cell>
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">To: ${billingAddress.get("toName")?if_exists} </fo:block>
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${billingAddress.get("address1")?if_exists} </fo:block>
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${billingAddress.get("address2")?if_exists} </fo:block>
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${billingAddress.get("city")?if_exists} - ${billingAddress.get("postalCode")?if_exists} </fo:block>  
					            					</fo:table-cell>
					            					<fo:table-cell>
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">TIN: <#if toPartyDetail?has_content>${toPartyDetail.get('TIN_NUMBER')?if_exists}</#if></fo:block>
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">CST: <#if toPartyDetail?has_content>${toPartyDetail.get('CST_NUMBER')?if_exists}</#if></fo:block>
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">AREA CODE: </fo:block>
					            						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
					            					</fo:table-cell>
					            					<fo:table-cell>
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">Inv No: ${invoice.get('invoiceId')?if_exists}</fo:block>
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">Date: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString((invoice.get('invoiceDate')), "dd-MMM-yyyy")}</fo:block>
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">Time: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString((invoice.get('invoiceDate')), "HH:mm:ss")}</fo:block>
					            						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
					            					</fo:table-cell>
												</fo:table-row>
			            					</fo:table-body>
			        					</fo:table>
					            	</fo:block>  
					            </fo:table-cell>
							</fo:table-row>
							<fo:table-row>
			                    <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">
					            		<fo:table border-width="1pt" border-style="dotted">
			            					<fo:table-column column-width="110pt"/>
			            					<fo:table-column column-width="110pt"/>
			            					<fo:table-column column-width="130pt"/>
			            					<fo:table-column column-width="80pt"/>
			            					<fo:table-column column-width="80pt"/>
			            					<fo:table-column column-width="90pt"/>
			            					<fo:table-body>
			                					<fo:table-row>
			                    					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">PLA No.</fo:block>  
					            					</fo:table-cell>
					            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">Registration</fo:block>
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">No.</fo:block>   
					            					</fo:table-cell>
					            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">Chapter No./Tarrif</fo:block>
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">Sub-Heading</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">Mode of</fo:block>
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">Despatch</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">Vehicle</fo:block>
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">Reg. No.</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">Name of the</fo:block>
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">Commodity</fo:block>
					            					</fo:table-cell>
												</fo:table-row>
												<fo:table-row>
			                    					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false"><#if fromPartyDetail?has_content>${fromPartyDetail.get('PLA_NUMBER')?if_exists}</#if></fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false"><#if fromPartyDetail?has_content>${fromPartyDetail.get('REGISTRATION_NUMBER')?if_exists}</#if></fo:block>
					            						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>   
					            					</fo:table-cell>
					            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false"></fo:block>
					            						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">By Road</fo:block>
					            						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${shipment.get('vehicleId')?if_exists}</fo:block>
					            						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false"></fo:block>
					            						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            					</fo:table-cell>
												</fo:table-row>
			            					</fo:table-body>
			        					</fo:table>
					            	</fo:block>  
					            </fo:table-cell>
							</fo:table-row>
							<fo:table-row>
			                    <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">
					            		<fo:table border-width="1pt" border-style="dotted">
			            					<fo:table-column column-width="170pt"/>
			            					<fo:table-column column-width="180pt"/>
			            					<fo:table-column column-width="150pt"/>
			            					<fo:table-column column-width="100pt"/>
			            					<fo:table-body>
			                					<fo:table-row>
			                    					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">RANGE: </fo:block>
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false"><#if taxAuthority?has_content && taxAuthority.get('taxRange')?exists><#if ((taxAuthority.get('taxRange')).length()>26)>${taxAuthority.get('taxRange')?if_exists.substring(0,26)}<#else>${taxAuthority.get('taxRange')?if_exists}</#if></#if></fo:block>
					            						<#if (taxAuthority?has_content && taxAuthority.get('taxRange')?exists && (taxAuthority.get('taxRange')).length()>26)>
					            							<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false"><#if ((taxAuthority.get('taxRange')).length()>52)>${taxAuthority.get('taxRange')?if_exists.substring(26, 52)}<#else>${taxAuthority.get('taxRange')?if_exists.substring(26, ((taxAuthority.get('taxRange')).length()-1))}</#if></fo:block>   
					            						</#if>
					            						<#if (taxAuthority?has_content && taxAuthority.get('taxRange')?exists && (taxAuthority.get('taxRange')).length()>52)>
					            							<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${taxAuthority.get('taxRange')?if_exists.substring(52, ((taxAuthority.get('taxRange')).length()-1))}</fo:block>   
					            						</#if>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">DIVISION: </fo:block>
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false"><#if taxAuthority?has_content && taxAuthority.get('taxDivision')?exists><#if ((taxAuthority.get('taxDivision')).length()>28)>${taxAuthority.get('taxDivision')?if_exists.substring(0,28)}<#else>${taxAuthority.get('taxDivision')?if_exists}</#if></#if></fo:block>
					            						<#if (taxAuthority?has_content && taxAuthority.get('taxDivision')?exists && (taxAuthority.get('taxDivision')).length()>28)>
					            							<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false"><#if ((taxAuthority.get('taxDivision')).length()>56)>${taxAuthority.get('taxDivision')?if_exists.substring(28, 56)}<#else>${taxAuthority.get('taxDivision')?if_exists.substring(28, ((taxAuthority.get('taxDivision')).length()-1))}</#if></fo:block>   
					            						</#if>
					            						<#if (taxAuthority?has_content && taxAuthority.get('taxDivision')?exists && (taxAuthority.get('taxDivision')).length()>56)>
					            							<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false"><#if ((taxAuthority.get('taxDivision')).length()>84)>${taxAuthority.get('taxDivision')?if_exists.substring(56, 84)}<#else>${taxAuthority.get('taxDivision')?if_exists.substring(56, ((taxAuthority.get('taxDivision')).length()-1))}</#if></fo:block>   
					            						</#if>
					            						<#if (taxAuthority?has_content && taxAuthority.get('taxDivision')?exists && (taxAuthority.get('taxDivision')).length()>84)>
					            							<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false"><#if ((taxAuthority.get('taxDivision')).length()>112)>${taxAuthority.get('taxDivision')?if_exists.substring(84, 112)}<#else>${taxAuthority.get('taxDivision')?if_exists.substring(84, ((taxAuthority.get('taxDivision')).length()-1))}</#if></fo:block>   
					            						</#if>
					            						<#if (taxAuthority?has_content && taxAuthority.get('taxDivision')?exists && (taxAuthority.get('taxDivision')).length()>112)>
					            							<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false"><#if ((taxAuthority.get('taxDivision')).length()>140)>${taxAuthority.get('taxDivision')?if_exists.substring(112, 140)}<#else>${taxAuthority.get('taxDivision')?if_exists.substring(112, ((taxAuthority.get('taxDivision')).length()-1))}</#if></fo:block>   
					            						</#if>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">DC No: ${shipment.get('shipmentId')?if_exists}</fo:block>
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">Removal Time:</fo:block>
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">PLA/CENVAT</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">Date : </fo:block>
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">Date : </fo:block>
					            					</fo:table-cell>
												</fo:table-row>
			            					</fo:table-body>
			        					</fo:table>
					            	</fo:block>  
					            </fo:table-cell>
							</fo:table-row>
							<fo:table-row>
			                    <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">
					            		<fo:table border-width="1pt" border-style="dotted">
			            					<fo:table-column column-width="20pt"/>
			            					<fo:table-column column-width="130pt"/>
			            					<fo:table-column column-width="50pt"/>
			            					<fo:table-column column-width="40pt"/>
			            					<fo:table-column column-width="40pt"/>
			            					<fo:table-column column-width="40pt"/>
			            					<fo:table-column column-width="40pt"/>
			            					<fo:table-column column-width="80pt"/>
			            					<fo:table-column column-width="50pt"/>
			            					<fo:table-column column-width="100pt"/>
			            					<fo:table-body>
			                					<fo:table-row>
			                    					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">SL.</fo:block>
					            						<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">No.</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="dotted" border-width="center" border-color="black">
					            						<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">Description</fo:block>
					            						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">Batch</fo:block>
					            						<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">No.</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">Qty</fo:block>
					            						<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">(Ltrs)</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">Qty</fo:block>
					            						<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">(Packs)</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">FreeQty</fo:block>
					            						<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">(Ltrs)</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">MRP</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">MRP Value</fo:block>
					            						<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false"> Rs.</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">Exfactory</fo:block>
					            						<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">Rate/Pack</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">Amount</fo:block>
					            						<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">Rs </fo:block>
					            					</fo:table-cell>
												</fo:table-row>
												<#assign totalAmt = 0>
												<#assign grandTotal = 0>
												<#assign totalMRPValue = 0>
												<#assign totalLtr = 0>
												<#if invoiceItems?has_content>
													<#assign slNo = 1>
													<#list invoiceItems as eachItem>
														<fo:table-row>
					                    					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
							            						<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">${slNo?if_exists}</fo:block>
							            					</fo:table-cell>
							            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
							            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(eachItem.get('itemDescription')?if_exists)),21)}</fo:block>
							            					</fo:table-cell>
							            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
							            						<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">${eachItem.get('batchNo')?if_exists}</fo:block>
							            					</fo:table-cell>
							            					<#assign totalLtr = totalLtr+eachItem.get('quantityLtr')>
							            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
							            						<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">${eachItem.get('quantityLtr')?if_exists?string('#0.00')}</fo:block>
							            					</fo:table-cell>
							            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
							            						<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">${eachItem.get('quantity')?if_exists}</fo:block>
							            					</fo:table-cell>
							            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
							            						<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">0.00</fo:block>
							            					</fo:table-cell>
							            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
							            						<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${eachItem.get('mrpPrice')?if_exists?string("#0.00")}</fo:block>
							            					</fo:table-cell>
							            					<#assign mrpValue = eachItem.get('mrpPrice') * eachItem.get('quantity')>
							            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
							            						<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${mrpValue?if_exists?string("#0.00")}</fo:block>
							            					</fo:table-cell>
							            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
							            						<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${eachItem.get('defaultPrice')?if_exists?string("#0.00")}</fo:block>
							            					</fo:table-cell>
							            					
							            					<#assign totalMRPValue = totalMRPValue+mrpValue>
							            					<#assign totalItemAmt = eachItem.get('defaultPrice')*eachItem.get('quantity')>
							            					<#assign grandTotal = grandTotal+totalItemAmt>
							            					<#assign totalAmt = totalAmt+totalItemAmt>
							            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
							            						<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${totalItemAmt?if_exists?string("#0.00")}</fo:block>
							            					</fo:table-cell>
							            					<#assign slNo = slNo+1>
														</fo:table-row>
													</#list>
												</#if>
												<fo:table-row>
			                    					<fo:table-cell border-bottom-style="dotted" border-bottom-width="thin" border-color="black">
					            						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-bottom-style="dotted" border-bottom-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">Total</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-bottom-style="dotted" border-bottom-width="thin" border-color="black">
					            						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-bottom-style="dotted" border-bottom-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false" font-weight="bold">${totalLtr?if_exists?string("#0.00")}</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-bottom-style="dotted" border-bottom-width="thin" border-color="black">
					            						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-bottom-style="dotted" border-bottom-width="thin" border-color="black">
					            						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-bottom-style="dotted" border-bottom-width="thin" border-color="black">
					            						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-bottom-style="dotted" border-bottom-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false" font-weight="bold">${totalMRPValue?if_exists?string("#0.00")}</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-bottom-style="dotted" border-bottom-width="thin" border-color="black">
					            						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false" font-weight="bold">${totalAmt?if_exists?string("#0.00")}</fo:block>
					            					</fo:table-cell>
												</fo:table-row>
												<#if invoiceTaxItems?has_content>
													<#assign taxDetails = invoiceTaxItems.entrySet()>
													<#list taxDetails as eachTax>
														<fo:table-row>
					                    					<fo:table-cell number-columns-spanned="3" border-bottom-style="dotted" border-bottom-width="thin" border-color="black">
							            						<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false"></fo:block>
							            					</fo:table-cell>
							            					<#assign text = "">
							            					<#if eachTax.getKey()=="BED_SALE">
							            						<#assign text = "Excise Duty (Deposit) Including CESS 2.06%">
							            					<#elseif eachTax.getKey()=="CST_SALE">
							            						<#assign text = "Central Sales Tax(CST) 2.0%">
							            					<#else>
							            						<#assign text = "Value Added Tax(VAT) ">
							            					</#if>
							            					<fo:table-cell number-columns-spanned="6" border-bottom-style="dotted" border-bottom-width="thin" border-color="black">
							            						<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false"> ${text?if_exists}</fo:block>
							            					</fo:table-cell>
							            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
							            						<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false">${eachTax.getValue()?if_exists?string("#0.00")}</fo:block>
							            					</fo:table-cell>
							            					<#assign grandTotal = grandTotal+eachTax.getValue()>
														</fo:table-row>
													</#list>
												</#if>
												<#-- <fo:table-row>
			                    					<fo:table-cell number-columns-spanned="4" border-bottom-style="dotted" border-bottom-width="thin" border-color="black">
					            						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell number-columns-spanned="5" border-bottom-style="dotted" border-bottom-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false">CST  2.0%</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false"><#if invoiceTaxItem?has_content && invoiceTaxItem.get('VAT_SALE')?exists>${invoiceTaxItem.get("VAT_SALE")?if_exists?string("#0.00")}</#if></fo:block>
					            					</fo:table-cell>
												</fo:table-row>-->
												<fo:table-row >
			                    					<fo:table-cell number-columns-spanned="9" border-bottom-style="dotted" border-bottom-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">GRAND TOTAL</fo:block>
					            					</fo:table-cell>
					            					<fo:table-cell border-style="dotted" border-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false" font-weight="bold">${grandTotal?if_exists?string("#0.00")}</fo:block>
					            					</fo:table-cell>
												</fo:table-row>
												<fo:table-row >
			                    					<fo:table-cell number-columns-spanned="10" border-bottom-style="dotted" border-bottom-width="thin" border-color="black">
					            						<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold">(In Words:${Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble(grandTotal?string("#0")), "%rupees-and-paise", locale).toUpperCase()} ONLY)</fo:block>
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
				<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">1. Certificate that the particulars given above are true &amp; correct &amp; the amount indicated represents the actual charged &amp; that there is no  </fo:block>
				<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">flow of additional consideration directly or indirectly from the buyer.  </fo:block>
				<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">2. All disputes regarding this bill shall be decided at Bangalore &amp; Bangalore Courts alone shall have jurisdiction.</fo:block>
				<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">3. Please settle payment of this bill within fifteen days. </fo:block>
				<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">4. In case of CST rate of tax, if the 'C' form is not given, the difference in tax rate is to your account.  </fo:block>
				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				<fo:block  keep-together="always" text-align="right" font-size="9pt" white-space-collapse="false">for KCMPF Ltd, Unit: Mother Dairy</fo:block>
				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				<fo:block  keep-together="always" text-align="right" font-size="9pt" white-space-collapse="false">AUTHORISED SIGNATORY</fo:block>
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