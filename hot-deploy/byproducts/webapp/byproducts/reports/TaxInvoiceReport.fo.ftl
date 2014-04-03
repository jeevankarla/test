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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in" margin-left=".5in" margin-right=".5in">
                <fo:region-body margin-top=".4in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "GenerateTrnsptMarginReport.txt")}
<#if itemsListMap?has_content> 
<#assign itemsList=itemsListMap.entrySet()>
<#assign tinNumber="">
<#assign cstNumber="">
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
  <#list itemsList as itemlst>
	         <#assign  facilityId=itemlst.getKey()>   
	         <#assign invoiceList=invoiceListMap.get(facilityId)>	
	          <#if (tinNumber ="") && (cstNumber ="")>
	        	<#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" :invoiceList.getString("partyId")}, true)>
	        	<#assign tinNumber = (partyGroup.tinNumber)?if_exists>
	    		<#assign cstNumber = (partyGroup.cstNumber)?if_exists>
	         </#if>	
	<fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-weight="bold"   keep-together="always"> MOTHER DAIRY</fo:block>
    <fo:block text-align="center"  white-space-collapse="false" font-family="Courier,monospace" font-weight="bold"  keep-together="always">GKVK POST </fo:block>
	<fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace"  font-weight="bold"  keep-together="always">YELAHANKA</fo:block>
    <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace"  font-weight="bold"  keep-together="always">BANGALORE 560 065</fo:block>
 	<fo:block text-align="center" border-style="solid">
 	<fo:table  table-layout="fixed" width="100%" space-before="0.2in">
		 <fo:table-column column-width="40%"/>
	     <fo:table-column column-width="20%"/>
	     <fo:table-column column-width="20%"/>
	     <fo:table-column column-width="20%"/>
	     <fo:table-body>
		     <fo:table-row> 
			     <fo:table-cell number-columns-spanned="4">   						
			 	     <fo:block text-align="center" white-space-collapse="false" font-weight="bold" keep-together="always">TAX INVOICE</fo:block>
			 	</fo:table-cell>
		 	  </fo:table-row>	
		 	  <fo:table-row> 
			     <fo:table-cell border-style="solid" >   						
					<fo:block>
						<fo:table  table-layout="fixed" width="100%" space-before="0.2in">
							 <fo:table-body>
								 <fo:table-row> 
									 <fo:table-cell border-style="solid" > 
									    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									    <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
									 </fo:table-cell>   
								 </fo:table-row> 
								  <fo:table-row> 
									 <fo:table-cell border-style="solid" > 
									    <fo:block font-weight="bold">BUYER'S NAME </fo:block>  
									 </fo:table-cell>   
								 </fo:table-row> 
								  <fo:table-row> 
									 <fo:table-cell>
									    <fo:block text-align="left" text-indent="5pt">${facilityId} ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, facilityId, false)} </fo:block> 
					                     <#assign partyAddressResult = dispatcher.runSync("getPartyPostalAddress", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", invoiceList.getString("partyId"), "userLogin", userLogin))/>
										 <#assign partyTelephoneResult = dispatcher.runSync("getPartyTelephone", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", invoiceList.getString("partyId"), "userLogin", userLogin))/>
										 <#if (partyAddressResult.address1?has_content)>
										   <fo:block text-indent="5pt" text-align="left" keep-together="always">&#160;(${partyAddressResult.address1?if_exists})</fo:block>
										</#if>
										<#if (partyTelephoneResult.contactNumber?has_content)>
										  <fo:block  text-indent="5pt" text-align="left" keep-together="always">&#160;(${partyTelephoneResult.contactNumber?if_exists})</fo:block>
										</#if>
										<#if (partyAddressResult.postalCode?has_content)>
										  <fo:block  text-indent="5pt" text-align="left" keep-together="always">&#160;(${partyAddressResult.postalCode?if_exists})</fo:block>
										</#if>
										
									 </fo:table-cell>   
								 </fo:table-row> 
							  </fo:table-body>
						</fo:table>
					</fo:block>
			 	</fo:table-cell>
			 	 <fo:table-cell border-style="solid">   						
			 	    <fo:block>
						<fo:table table-layout="fixed" width="100%" space-before="0.2in">
							 <fo:table-body>
								  <fo:table-row> 
									 <fo:table-cell> 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">Invice No</fo:block>
									       <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" font-weight="bold" keep-together="always">${invoiceList.getString("invoiceId")}</fo:block>
			   							  <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">Delivery Note No</fo:block>
									 </fo:table-cell>   
								 </fo:table-row> 
								  <fo:table-row> 
									 <fo:table-cell border-style="solid" > 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">Supplier's Ref</fo:block>
									      <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
									 </fo:table-cell>   
								 </fo:table-row> 
								 <fo:table-row> 
									 <fo:table-cell border-style="solid" > 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">Buyer's Order No</fo:block>
									      <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
									 </fo:table-cell>   
								 </fo:table-row> 
								 <fo:table-row> 
									 <fo:table-cell border-style="solid" > 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">Despatch Document No</fo:block>
									      <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
									 </fo:table-cell>   
								 </fo:table-row> 
								  <fo:table-row> 
									 <fo:table-cell border-style="solid" > 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">Despatched Through</fo:block>
									      <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
									 </fo:table-cell>   
								 </fo:table-row> 
								  <fo:table-row> 
									 <fo:table-cell > 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">Terms of Delivery</fo:block>
									      <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
									 </fo:table-cell>   
								 </fo:table-row> 
							  </fo:table-body>
						</fo:table>
					</fo:block>
				</fo:table-cell>
			 	 <fo:table-cell border-style="solid">   						
			 	    <fo:block>
						<fo:table table-layout="fixed" width="100%" space-before="0.2in">
							 <fo:table-body>
								  <fo:table-row> 
									 <fo:table-cell border-style="solid" > 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">Dated</fo:block>
									       <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invoiceList.getTimestamp("invoiceDate"), "dd-MMM-yyyy")}</fo:block>
			   							  <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">Terms of Payment</fo:block>
									 </fo:table-cell>   
								 </fo:table-row> 
								  <fo:table-row> 
									 <fo:table-cell border-style="solid" > 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">Other Reference</fo:block>
									      <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
									 </fo:table-cell>   
								 </fo:table-row> 
								 <fo:table-row> 
									 <fo:table-cell border-style="solid" > 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">Dated</fo:block>
									       <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invoiceList.getTimestamp("invoiceDate"), "dd-MMM-yyyy")}</fo:block>
									 </fo:table-cell>   
								 </fo:table-row> 
								 <fo:table-row> 
									 <fo:table-cell border-style="solid" > 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">Dated</fo:block>
									       <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invoiceList.getTimestamp("invoiceDate"), "dd-MMM-yyyy")}</fo:block>
									 </fo:table-cell>   
								 </fo:table-row>
 								<fo:table-row> 
									 <fo:table-cell border-style="solid" > 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">Destination</fo:block>
									      <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
									 </fo:table-cell>   
								 </fo:table-row> 
								  <fo:table-row> 
									 <fo:table-cell > 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">Ref:-Sale Order No</fo:block>
									      <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
									 </fo:table-cell>   
								 </fo:table-row> 
							  </fo:table-body>
						</fo:table>
					</fo:block>
				</fo:table-cell>
				 <fo:table-cell border-style="solid">   						
			 	    <fo:block>
						<fo:table table-layout="fixed" width="100%" space-before="0.2in">
							 <fo:table-body>
								  <fo:table-row> 
									 <fo:table-cell border-style="solid" > 
									      <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always" >TIN NO:${tinNumber}</fo:block>
			   							  <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always" >CST NO:${cstNumber}</fo:block>
			   							  <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
									 </fo:table-cell>   
								 </fo:table-row> 
								 <fo:table-row> 
									 <fo:table-cell border-style="solid" > 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">&#160;</fo:block>
									      <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
									 </fo:table-cell>   
								 </fo:table-row> 
								 <fo:table-row> 
									 <fo:table-cell border-style="solid" > 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">&#160;</fo:block>
									      <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
									 </fo:table-cell>   
								 </fo:table-row> 
								 <fo:table-row> 
									 <fo:table-cell border-style="solid" > 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">&#160;</fo:block>
									      <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
									 </fo:table-cell>   
								 </fo:table-row>
								  <fo:table-row> 
									 <fo:table-cell border-style="solid" > 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">&#160;</fo:block>
									      <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
									 </fo:table-cell>   
								 </fo:table-row> 
								  <fo:table-row> 
									 <fo:table-cell border-style="solid" > 
									      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">&#160;</fo:block>
									      <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
									 </fo:table-cell>   
								 </fo:table-row>  
							  </fo:table-body>
						</fo:table>
					</fo:block>
				</fo:table-cell>
		 	  </fo:table-row>	
	 	 </fo:table-body> 
 	</fo:table>
 	<fo:table  table-layout="fixed" width="100%">
		 <fo:table-column column-width="35%"/>
	     <fo:table-column column-width="20%"/>
	     <fo:table-column column-width="15%"/>
	     <fo:table-column column-width="15%"/>
	     <fo:table-column column-width="15%"/>
		     <fo:table-body>
			     <fo:table-row> 
				     <fo:table-cell border-style="solid">   						
				 	     <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" font-weight="bold" keep-together="always">Description of Goods</fo:block>
				 	</fo:table-cell>
				 	  <fo:table-cell border-style="solid">   						
				 	     <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" font-weight="bold" keep-together="always">Quantity(Kg/Ltr)</fo:block>
				 	</fo:table-cell>
				 	  <fo:table-cell border-style="solid">   						
				 	     <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" font-weight="bold" keep-together="always">DC No</fo:block>
				 	</fo:table-cell>
				 	  <fo:table-cell border-style="solid">   						
				 	     <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" font-weight="bold" keep-together="always">Unit Rate</fo:block>
				 	</fo:table-cell>
				 	  <fo:table-cell border-style="solid">   						
				 	     <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" font-weight="bold" keep-together="always">Amount (Rs)</fo:block>
				 	</fo:table-cell>
			 	  </fo:table-row>
			 	  <fo:table-row> 
				     <fo:table-cell>   						
				 	     <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" font-weight="bold" keep-together="always">Taxable Goods</fo:block>
                  </fo:table-cell>
                  </fo:table-row> 
			 	    <#assign totalAmount=0>
              	    <#assign taxAmount=0>
              	    <#assign finalAmount=0>
				   <#assign  itList=itemlst.getValue()>
				 <#list itList as orderLst>
              	     <#assign quantity=orderLst.getBigDecimal("quantity")>
					 <#assign unitListPrice=orderLst.getBigDecimal("unitListPrice")>
              	    <#assign amount=(quantity)*(unitListPrice)>
			 	  <fo:table-row > 
				     <fo:table-cell >  
					    <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">${orderLst.getString("productName")}</fo:block>
				 	</fo:table-cell>
				 	<fo:table-cell >  
					   <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">${orderLst.getBigDecimal("quantity")}</fo:block>
				 	</fo:table-cell>
				 	<fo:table-cell >  
					   <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">${orderLst.getString("orderId")}</fo:block>
				 	</fo:table-cell>
				 	<fo:table-cell >  
						<fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">${orderLst.getString("unitListPrice")}</fo:block>
				 	</fo:table-cell>
				 	<fo:table-cell>  
						<fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">${amount?string("#0.00")}</fo:block>
				 	</fo:table-cell>
			 	  </fo:table-row>
			 	    <#assign totalAmount=totalAmount+amount>
				 	<#assign taxAmount=totalAmount*14.50/100>
			 	   </#list> 
			 	   <#assign finalAmount=finalAmount+(totalAmount)+taxAmount>
			 	   <fo:table-row> 
				     <fo:table-cell >   						
				 	     <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">Total</fo:block>
                  </fo:table-cell>
                  <fo:table-cell >   						
				 	     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				 	</fo:table-cell>
				 	  <fo:table-cell >   						
				 	     <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				 	</fo:table-cell>
				 	  <fo:table-cell >   						
				 	     <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				 	</fo:table-cell>
                   <fo:table-cell >   						
				 	     <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">${totalAmount?string("#0.00")}</fo:block>
                  </fo:table-cell>
                   </fo:table-row> 
                  <fo:table-row> 
				     <fo:table-cell >   						
				 	     <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">ADD : VAT @ 14.50 % of Amount</fo:block>
                  </fo:table-cell>
                  <fo:table-cell >   						
				 	     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				 	</fo:table-cell>
				 	  <fo:table-cell >   						
				 	     <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				 	</fo:table-cell>
				 	  <fo:table-cell >   						
				 	     <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				 	</fo:table-cell>
                   <fo:table-cell >   						
				 	     <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">${taxAmount?string("#0.00")}</fo:block>
                  </fo:table-cell>
                  </fo:table-row> 
                  <fo:table-row> 
				     <fo:table-cell >   						
				 	     <fo:block text-align="left" text-indent="12pt" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">Sub Total Value</fo:block>
                  </fo:table-cell>
                  <fo:table-cell >   						
				 	     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				 	</fo:table-cell>
				 	  <fo:table-cell >   						
				 	     <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				 	</fo:table-cell>
				 	  <fo:table-cell >   						
				 	     <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				 	</fo:table-cell>
                   <fo:table-cell >   						
				 	     <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">${finalAmount?string("#0.00")}</fo:block>
                  </fo:table-cell>
                   </fo:table-row> 
			 	   <fo:table-row> 
				     <fo:table-cell border-style="solid">   						
				 	     <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" font-weight="bold" keep-together="always">Grand Total</fo:block>
				 	</fo:table-cell>
				 	  <fo:table-cell border-style="solid">   						
				 	     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				 	</fo:table-cell>
				 	  <fo:table-cell border-style="solid">   						
				 	     <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				 	</fo:table-cell>
				 	  <fo:table-cell border-style="solid">   						
				 	     <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				 	</fo:table-cell>
				 	  <fo:table-cell border-style="solid">   
				 	     <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" font-weight="bold" keep-together="always">${finalAmount?string("#0.00")}</fo:block>
				 	</fo:table-cell>
			 	  </fo:table-row>
			 	 <fo:table-row> 
			     <fo:table-cell number-columns-spanned="5">   						
			 	     <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">Amount chargeable (in words)</fo:block>
					 <fo:block text-align="left" text-indent="5pt" keep-together="always" font-family="Courier,monospace" font-size="10pt" white-space-collapse="false" >Rupees ${Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble(totalAmount?string("#0.00")), "%rupees-and-paise", locale)} ONLY  </fo:block>
					 <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			         <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">Declaration</fo:block>
			         <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">We declare that this invoice shows the actual price of the goods</fo:block>
			         <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">described and that all particulars are true and correct</fo:block>
			         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			        </fo:table-cell>
		 	  </fo:table-row>	
		 	   <fo:table-row> 
		 	      <fo:table-cell number-columns-spanned="2">   						
			 	     <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				 </fo:table-cell>
		 	      <fo:table-cell border-style="solid" number-columns-spanned="2">   						
			 	      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" font-weight="bold" keep-together="always">For MOTHER DAIRY</fo:block>
			 	    <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			 	      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" font-weight="bold" keep-together="always">AUTHORISED SIGNATORY</fo:block>
			      </fo:table-cell>
			       <fo:table-cell number-columns-spanned="1">   						
			 	     <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			      </fo:table-cell>
			 </fo:table-row>	
			  <fo:table-row> 
		 	         <fo:table-cell number-columns-spanned="3">   						
			 	       <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always" font-weight="bold">Details as per enclosure</fo:block>
			 	   </fo:table-cell>
			 </fo:table-row>	
			  <fo:table-row> 
			       <fo:table-cell number-columns-spanned="1">   						
						<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			      </fo:table-cell>
		 	         <fo:table-cell number-columns-spanned="3">   						
			 	         <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always" font-weight="bold">SUBJECT TO BANGALORE JURISDICTION</fo:block>
			 	   </fo:table-cell>
			 </fo:table-row>	
			 </fo:table-body> 
 	</fo:table>
 	</fo:block>
 	</#list>
 </fo:flow>
 </fo:page-sequence>	
	<#else>	
	<fo:page-sequence master-reference="main">
    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
       		 <fo:block font-size="14pt">
            	${uiLabelMap.OrderNoOrderFound}.
       		 </fo:block>
    	</fo:flow>
	</fo:page-sequence>
</#if>
</fo:root>
</#escape>