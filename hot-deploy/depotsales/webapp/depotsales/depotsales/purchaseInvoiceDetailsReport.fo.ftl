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
      <fo:simple-page-master master-name="main" page-height="12in" page-width="10in" margin-top=".1in" margin-bottom=".1in" margin-left=".3in" margin-right=".5in">
          <fo:region-body margin-top="1.66in"/>
          <fo:region-before extent="1in"/>
          <fo:region-after extent="1in"/>
      </fo:simple-page-master>
    </fo:layout-master-set>

    <fo:page-sequence master-reference="main">
    <fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
    	<fo:block text-align="center" font-size="12pt" font-weight="bold"  white-space-collapse="false">PURCHASE INVOICE</fo:block>
    	<fo:block text-align="center" font-size="12pt" font-weight="bold"  white-space-collapse="false">NATIONAL HANDLOOM DEVELOPMENT CORPORATION LIMITED.</fo:block>
    	<fo:block text-align="center" font-size="10pt" font-weight="bold"  white-space-collapse="false">${BOAddress?if_exists}</fo:block>
        <fo:block text-align="center" font-size="10pt" font-weight="bold"  white-space-collapse="false">E-MAIL:${BOEmail?if_exists}</fo:block>
           
    </fo:static-content>
    <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
    <#if purchasePartyDetailsMap?has_content>
    	<fo:block>     
    		<fo:table>
                <fo:table-column column-width="60%"/>
                <fo:table-column column-width="40%"/>
                <fo:table-body>
                	<fo:table-row>
	                    <fo:table-cell>
			            	<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">${purchasePartyDetailsMap.get("RoName")?if_exists}</fo:block>  
			            </fo:table-cell>
			             <fo:table-cell>
			            	<fo:block  text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">CIN NUMBER:${purchasePartyDetailsMap.get("CIN_NUMBER")?if_exists} </fo:block>  
			            </fo:table-cell>
					</fo:table-row>
					<fo:table-row>
	                    <fo:table-cell>
			            	<fo:block  text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">${purchasePartyDetailsMap.get("address")?if_exists}</fo:block>  
			            </fo:table-cell>
			             <fo:table-cell>
			            	<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">CST NUMBER:${purchasePartyDetailsMap.get("CST_NUMBER")?if_exists}</fo:block>  
			            </fo:table-cell>
					</fo:table-row>
					<fo:table-row>
	                    <fo:table-cell>
			            	<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">${purchasePartyDetailsMap.get("postalCode")?if_exists}</fo:block>  
			            </fo:table-cell>
			             <fo:table-cell>
			            	<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">PAN NUMBER:${purchasePartyDetailsMap.get("PAN_NUMBER")?if_exists}</fo:block>  
			            </fo:table-cell>
					</fo:table-row>
					<fo:table-row>
	                    <fo:table-cell>
			            	<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">${purchasePartyDetailsMap.get("contactNumber")?if_exists},${purchasePartyDetailsMap.get("telecomNumber")?if_exists}</fo:block>  
			            </fo:table-cell>
			             <fo:table-cell>
			            	<fo:block  text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">TIN NUMBER:${purchasePartyDetailsMap.get("TIN_NUMBER")?if_exists}</fo:block>  
			            </fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>	
		</fo:block>
		<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
		<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
		<fo:block>     
    		<fo:table>
    			<fo:table-column column-width="10%"/>
                <fo:table-column column-width="40%"/>
                <fo:table-column column-width="15%"/>
                <fo:table-column column-width="15%"/>
                <fo:table-column column-width="20%"/>
                <fo:table-body>
                	<fo:table-row>
	                    <fo:table-cell border-style="solid" >
			            	<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">Product Code</fo:block>
			            </fo:table-cell>
			             <fo:table-cell border-style="solid">
			            	<fo:block  text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">Product Name </fo:block>  
			            </fo:table-cell>
			            <fo:table-cell border-style="solid">
			            	<fo:block  text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">Quantity</fo:block>  
			            </fo:table-cell>
			            <fo:table-cell border-style="solid">
			            	<fo:block  text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">Unit Price</fo:block>  
			            </fo:table-cell>
			            <fo:table-cell border-style="solid">
			            	<fo:block  text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">Amount</fo:block>  
			            </fo:table-cell>
					</fo:table-row>
					<#if purchaseInvoiceItemList?has_content>
	                <#assign totalValue=0>
		            <#list purchaseInvoiceItemList as eachItem>	
					<#assign subTotalValue=0>
		            <#assign itemAdjustments=purchaseInvoiceAdjustmtsMap.get(eachItem.productId)>
					<fo:table-row>
	                    <fo:table-cell border-style="solid">
			            	<fo:block  text-align="left" font-size="12pt" white-space-collapse="false">${eachItem.get("productId")?if_exists}</fo:block>
							  
			            </fo:table-cell>
			             <fo:table-cell border-style="solid">
							
			            	<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >${eachItem.get("productName")?if_exists}</fo:block>
			            	<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
							<fo:block>     
					    		<fo:table>
					                <fo:table-column column-width="100%"/>
					                <fo:table-body>
					                <#list itemAdjustments as eachAdjustment>
					                	<fo:table-row>
						                    <fo:table-cell>
								            	<fo:block   text-align="left" font-size="10pt" white-space-collapse="false" >${eachAdjustment.get("taxTerm")?if_exists}(${eachAdjustment.get("taxPer")?if_exists})</fo:block>  
								            </fo:table-cell>
										</fo:table-row>
									</#list>
									<fo:table-row>
					                    <fo:table-cell>
					                    	<fo:block   text-align="left" font-size="10pt"  white-space-collapse="false">--------------------</fo:block>
							            	<fo:block   text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold">Item SubTotal</fo:block> 
							            	<fo:block   text-align="left" font-size="10pt"  white-space-collapse="false">--------------------</fo:block> 
							            </fo:table-cell>
									</fo:table-row>
									</fo:table-body>
								</fo:table>	
						</fo:block>  
			            </fo:table-cell>
						<fo:table-cell border-style="solid">
			            	<fo:block   text-align="left" font-size="12pt"  white-space-collapse="false" >${eachItem.get("quantity")?if_exists}</fo:block>  
			            </fo:table-cell>
			              <fo:table-cell border-style="solid">
			            	<fo:block   text-align="left" font-size="12pt"  white-space-collapse="false">${eachItem.get("unitPrice")?if_exists}</fo:block>  
			            </fo:table-cell>
			              <fo:table-cell border-style="solid">
			              	<#assign subTotalValue=eachItem.get("amount")>
			            	<fo:block   text-align="left" font-size="12pt"  white-space-collapse="false">${eachItem.get("amount")?if_exists}</fo:block>
			            	<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
							<fo:block>     
					    		<fo:table>
					                <fo:table-column column-width="100%"/>
					                <fo:table-body>
					                <#list itemAdjustments as eachAdjustment>
					                	<fo:table-row>
						                    <fo:table-cell>
								            	<fo:block   text-align="left" font-size="10pt"  white-space-collapse="false">${eachAdjustment.get("taxAmount")?if_exists}</fo:block>  
								            </fo:table-cell>
										</fo:table-row>
										<#assign subTotalValue=subTotalValue+eachAdjustment.get("taxAmount")>
									</#list>
									<fo:table-row>
					                    <fo:table-cell>
					                    	<fo:block   text-align="left" font-size="10pt"  white-space-collapse="false">--------------------</fo:block>
							            	<fo:block   text-align="left" font-size="10pt"  white-space-collapse="false"  font-weight="bold">${subTotalValue?if_exists?string("#0.00")}</fo:block>
											<fo:block   text-align="left" font-size="10pt"  white-space-collapse="false">--------------------</fo:block>  
							            </fo:table-cell>
									</fo:table-row>
									</fo:table-body>
								</fo:table>	
						</fo:block>  
			            </fo:table-cell>
					</fo:table-row>
						<#assign totalValue=totalValue+subTotalValue>
					 </#list>
					</#if>
					<fo:table-row>
	                    <fo:table-cell>
			            	<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>  
			            </fo:table-cell>
			             <fo:table-cell>
			            	<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>  
			            </fo:table-cell>
			             
			             <fo:table-cell number-columns-spanned="2">
			             	<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
			            	<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">Total Purchase value(Rs): </fo:block>  
			            </fo:table-cell>
			             <fo:table-cell>
			             	<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
			            	<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">${totalValue?if_exists?string("#0.00")}</fo:block>  
			            </fo:table-cell>
					</fo:table-row>
					<fo:table-row> 
	                    <fo:table-cell>
			            	<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>  
			            </fo:table-cell>
			             <fo:table-cell>
			            	<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>  
			            </fo:table-cell>
			             
			             <fo:table-cell number-columns-spanned="3">
			             	<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
			            	<fo:block text-align="left" font-weight="bold"   font-size="10pt" >${Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble(totalValue?string("#0.00")), "%indRupees-and-paiseRupees", locale).toUpperCase()} RUPEES ONLY.</fo:block>  
			            </fo:table-cell>
					</fo:table-row>
					<fo:table-row>
	                    <fo:table-cell  number-columns-spanned="5">
			            	<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">
			            		<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
			            		<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
			            		<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
			            		<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
			            		<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
			            		<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
			            		<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
		            		</fo:block>  
			            </fo:table-cell>
					</fo:table-row>
					<fo:table-row>
	                    <fo:table-cell>
			            	<fo:block   text-align="center" font-size="10pt" white-space-collapse="false">Prepared By</fo:block>  
			            </fo:table-cell>
			             <fo:table-cell>
			            	<fo:block   text-align="center" font-size="10pt" white-space-collapse="false" >Sr.Officer(C) </fo:block>  
			            </fo:table-cell>
			             
			             <fo:table-cell>
			            	<fo:block   text-align="center" font-size="10pt" white-space-collapse="false">AM(C)/DM(C)/Manager(C)</fo:block>  
			            </fo:table-cell>
			             <fo:table-cell  number-columns-spanned="2">
			            	<fo:block   text-align="center" font-size="10pt" white-space-collapse="false">Checked By F &amp; A Section</fo:block>  
			            </fo:table-cell>
					</fo:table-row>
					<fo:table-row>
	                    <fo:table-cell  number-columns-spanned="5">
			            	<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">
			            		<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
			            		<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
			            		<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
			            		<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
		            		</fo:block>  
			            </fo:table-cell>
					</fo:table-row>
					<fo:table-row>
	                    <fo:table-cell>
			            	<fo:block   text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>  
			            </fo:table-cell>
			             <fo:table-cell>
			            	<fo:block   text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold"> </fo:block>  
			            </fo:table-cell>
			            
			             <fo:table-cell  number-columns-spanned="3">
			            	<fo:block   text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold">for NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD</fo:block>  
			            </fo:table-cell>
					</fo:table-row>
					<fo:table-row>
	                    <fo:table-cell  number-columns-spanned="5">
			            	<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">
			            		<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
			            		<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
			            		<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
		            		</fo:block>  
			            </fo:table-cell>
					</fo:table-row>
					<fo:table-row>
	                    <fo:table-cell>
			            	<fo:block   text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>  
			            </fo:table-cell>
			             <fo:table-cell>
			            	<fo:block   text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold"> </fo:block>  
			            </fo:table-cell>
			            
			             <fo:table-cell  number-columns-spanned="3">
			            	<fo:block   text-align="center" font-size="10pt" white-space-collapse="false">(Authorised Signatory)</fo:block>  
			            </fo:table-cell>
					</fo:table-row>
					<fo:table-row>
	                    <fo:table-cell  number-columns-spanned="5">
			            	<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">
			            		<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
			            		<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
		            		</fo:block>  
			            </fo:table-cell>
					</fo:table-row>
					<fo:table-row>
			             <fo:table-cell  number-columns-spanned="5">
			            	<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">TERMS CONDITIONS:</fo:block>
			            	<fo:block text-align="left" white-space-collapse="false"   font-size="10pt" > * All payment  should be made by crossed cheque/draft in favour of 'National Handloom Development Corp Ltd'.</fo:block>
			            	<fo:block text-align="left" white-space-collapse="false"    font-size="10pt" >* INTEREST will be charged  ____________per annum on overdue Amount.</fo:block>
			            	<fo:block text-align="left" white-space-collapse="false"   font-size="10pt" > * In case of any dispute,the case will be referred to an arbitrator mutually agreed upon </fo:block>
			            	<fo:block text-align="left" white-space-collapse="false"   font-size="10pt" >&#160; whose will be final and binding E.&amp;.O.E</fo:block>  
			            </fo:table-cell>
					</fo:table-row>
					
				</fo:table-body>
			</fo:table>	
		</fo:block>
    </#if>
	</fo:flow>
      		
      		
      		
      		
  	</fo:page-sequence>
    
  </fo:root>
</#escape>
