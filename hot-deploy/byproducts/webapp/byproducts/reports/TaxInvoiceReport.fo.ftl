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
${setRequestAttribute("OUTPUT_FILENAME", "TaxInvoice.txt")}
<#if itemsReturnListMap?has_content> 
<#assign itemsList=itemsReturnListMap.entrySet()>
<#assign tinNumber="">
<#assign cstNumber="">
 <#list itemsList as itemlst>
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
	         <#assign  facilityId=itemlst.getKey()>   
	         <#assign invoiceList=invoiceListMap.get(facilityId)>	
	          <#if (tinNumber ="") && (cstNumber ="")>
	        	<#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" :invoiceList.getString("partyId")}, true)>
	        	<#assign tinNumber = (partyGroup.tinNumber)?if_exists>
	    		<#assign cstNumber = (partyGroup.cstNumber)?if_exists>
	         </#if>	
	<fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-weight="bold"   keep-together="always"> MOTHER DAIRY, GKVK POST, YELAHANKA, BANGALORE 560 065</fo:block>
 	<fo:block text-align="center" border-style="solid">
 	<fo:table  table-layout="fixed" width="100%" space-before="0.2in">
		 <fo:table-column column-width="40%"/>
	     <fo:table-column column-width="20%"/>
	     <fo:table-column column-width="20%"/>
	     <fo:table-column column-width="20%"/>
	     <fo:table-body>
		     <fo:table-row> 
			     <fo:table-cell number-columns-spanned="4">   						
			 	     <fo:block text-align="center" white-space-collapse="false" font-weight="bold" keep-together="always">CONSOLIDATED TAX INVOICE</fo:block>
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
										<#if (partyAddressResult.address2?has_content)>
										<fo:block  text-align="left" keep-together="always" text-indent="5pt">&#160;${partyAddressResult.address2?if_exists}</fo:block>
										</#if>
										<#if (partyAddressResult.city?has_content)>
										<fo:block  text-align="left" keep-together="always" text-indent="5pt">&#160;${partyAddressResult.city?if_exists} ${partyAddressResult.stateProvinceGeoId?if_exists}</fo:block>
										</#if>
										<#if (partyAddressResult.countryGeoId?has_content)>
										<fo:block  text-align="left" keep-together="always" text-indent="5pt">&#160;${partyAddressResult.countryGeoId?if_exists}</fo:block>
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
		 <fo:table-column column-width="30%"/>
		 <fo:table-column column-width="15%"/>
	     <fo:table-column column-width="10%"/>
	     <fo:table-column column-width="18%"/>
	     <fo:table-column column-width="13%"/>
	     <fo:table-column column-width="13%"/>
		     <fo:table-body>
			     <fo:table-row> 
				     <fo:table-cell border-style="solid">   						
				 	     <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" 
				 	     font-weight="bold" keep-together="always">Description of Goods</fo:block>
				 	</fo:table-cell>
				 	  <fo:table-cell border-style="solid">   						
				 	     <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" 
				 	     font-weight="bold" keep-together="always">Vat Percentage</fo:block>
				 	</fo:table-cell>
				 	  <fo:table-cell border-style="solid">   						
				 	     <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" 
				 	     font-weight="bold" keep-together="always">DC No</fo:block>
				 	</fo:table-cell>
				 	<fo:table-cell border-style="solid">   						
				 	     <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" 
				 	     font-weight="bold" keep-together="always">Quantity(Kg/Ltr)</fo:block>
				 	</fo:table-cell>
				 	  <fo:table-cell border-style="solid">   						
				 	     <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" 
				 	     font-weight="bold" keep-together="always">Unit Rate</fo:block>
				 	</fo:table-cell>
				 	  <fo:table-cell border-style="solid">   						
				 	     <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" 
				 	     font-weight="bold" keep-together="always">Amount (Rs)</fo:block>
				 	</fo:table-cell>
			 	  </fo:table-row>
			 	    <#assign totalAmount=0>
              	    <#assign netTotalAmount=0>
              	    <#assign finalAmount=0>
              	    <#assign taxAmount=0>
              	    <#assign afterReturnTotal=0>
						<#list vatList as eachVat>
              	    <#assign returnTotal=0>
					<#assign saleTotal = 0>
						<#assign totalFlag = 0>
					<#if (eachVat==0)>
						<fo:table-row> 
				     <fo:table-cell>   						
				 	     <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" 
				 	     font-size="10pt" font-weight="bold" keep-together="always">NON Taxable Goods </fo:block>
                  </fo:table-cell>
                  </fo:table-row>
						</#if>
			 		    <#assign  itList=itemlst.getValue()>
			        	<#list itList as eachItem>
				        
				        <#assign quantity=eachItem.get("quantity")>
						<#assign unitListPrice=eachItem.get("unitListPrice")>
						<#assign amount=(quantity)*(unitListPrice)>
						<#assign returnQuantity=eachItem.get("returnQuantity")>
						<#assign returnAmount=eachItem.get("returnAmount")>
						<#assign vatPercentage=eachItem.get("vatPercentage")>
						
						<#if (vatPercentage==eachVat)>
						<#assign totalFlag = 1>
						<#if (eachVat!=0)>
						<fo:table-row> 
				     <fo:table-cell>   						
				 	     <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" 
				 	     font-size="10pt" font-weight="bold" keep-together="always">Taxable Goods (WITH ${vatPercentage}%) </fo:block>
                  </fo:table-cell>
                  </fo:table-row>
						</#if>
						
			 	   
			 	  <fo:table-row> 
				     <fo:table-cell >  
					    <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" 
					    font-size="10pt" keep-together="always">${eachItem.get("productName")}</fo:block>
				 	</fo:table-cell>
				 	<fo:table-cell >  
					   <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" 
					   keep-together="always">${eachItem.get("vatPercentage")}</fo:block>
				 	</fo:table-cell>
				 	<fo:table-cell >  
					   <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" 
					   keep-together="always">${eachItem.get("orderId")}</fo:block>
				 	</fo:table-cell>
				 	<fo:table-cell >  
					   <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" 
					   keep-together="always">${eachItem.get("quantity")}   </fo:block>
				 	</fo:table-cell>
				 	<fo:table-cell >  
						<fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" 
						keep-together="always">${eachItem.get("unitListPrice")}</fo:block>
				 	</fo:table-cell>
				 	<fo:table-cell>  
						<fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" 
						keep-together="always">${amount?string("#0.00")}</fo:block>
				 	</fo:table-cell>
				 	<#assign saleTotal = (saleTotal + amount?if_exists)>
			 	  </fo:table-row>
			 	    <#assign totalAmount=totalAmount+amount>
				 	</#if>
			 	   </#list> 
			 	   <#if totalFlag == 1>
			 	   <fo:table-row> 
				     
                  <fo:table-cell number-columns-spanned="4">   						
				 	     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				 	</fo:table-cell>
                   <fo:table-cell >   						
				 	     <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" 
				 	     font-size="10pt" keep-together="always" font-weight="bold">Total</fo:block>
                  </fo:table-cell>
                   <fo:table-cell >   						
				 	     <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" 
				 	     keep-together="always">${saleTotal?string("#0.00")}</fo:block>
                  </fo:table-cell>
                   </fo:table-row>
                   <#assign netTotalAmount=netTotalAmount+totalAmount> 
			 	   </#if>
                  
                  <#assign returnTotalAmount=0>
                  
                  <#assign returnTotalFalg=0>
                  <#assign  itList=itemlst.getValue()>
			        	<#list itList as eachItem>
				        <#assign quantity=eachItem.get("quantity")>
						<#assign unitListPrice=eachItem.get("unitListPrice")>
						<#assign amount=(quantity)*(unitListPrice)>
						<#assign returnQuantity=eachItem.get("returnQuantity")>
						<#assign returnAmount=eachItem.get("returnAmount")>
                      <#assign vatPercentage=eachItem.get("vatPercentage")>
						
						<#if (vatPercentage==eachVat)>
				 	<#if (returnQuantity>0)>
					<#assign returnTotalFalg=1>                  
                   <fo:table-row> 
				     <fo:table-cell>   						
				 	     <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" 
				 	     font-size="10pt" font-weight="bold" keep-together="always">Less-Returns</fo:block>
                  </fo:table-cell>
                  </fo:table-row>
                  <fo:table-row > 
				     <fo:table-cell >  
					    <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" 
					    font-size="10pt" keep-together="always">${eachItem.get("returnProductName")}</fo:block>
				 	</fo:table-cell>
				 	<fo:table-cell >  
					   <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" 
					   keep-together="always">${eachItem.get("vatPercentage")} </fo:block>
				 	</fo:table-cell>
				 	<fo:table-cell >  
					   <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" 
						keep-together="always">${eachItem.get("orderId")}</fo:block>
				 	</fo:table-cell>
				 	<fo:table-cell >  
					   <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" 
					   keep-together="always"> ${eachItem.get("returnQuantity")}</fo:block>
				 	</fo:table-cell>
				 	<fo:table-cell >  
						<fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" 
					   keep-together="always">${eachItem.get("unitListPrice")}</fo:block>
				 	</fo:table-cell>
				 	<fo:table-cell>  
						<fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" 
						keep-together="always">${eachItem.get("returnAmount")?string("#0.00")}</fo:block>
				 	</fo:table-cell>
				 	<#assign returnTotal = (returnTotal + returnAmount?if_exists)>
				 	
				   </fo:table-row>
				   
                   <#assign afterReturnTotal = (saleTotal - returnTotal?if_exists)>
					<#if (returnTotalFalg==1)>
                  	<fo:table-row> 
					<fo:table-cell number-columns-spanned="4">   						
				 	     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				 	</fo:table-cell>
				 	<fo:table-cell >   						
				 	     <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" 
				 	     font-size="10pt" keep-together="always" font-weight="bold">RETURN Total</fo:block>
                  </fo:table-cell>
                   <fo:table-cell >   						
				 	     <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" 
				 	     keep-together="always">${returnTotal?string("#0.00")}</fo:block>
                  </fo:table-cell>
                   </fo:table-row>
                    <#--fo:table-cell number-columns-spanned="5">
                        <fo:block font-weight="bold"> ${uiLabelMap.ProductShipmentId}: ${newShipmentId}<#if issuedDateTime?exists> ${uiLabelMap.CommonDate}: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(issuedDateTime)}</#if></fo:block>
                   </fo:table-cell-->
                   <fo:table-row> 
                  <fo:table-cell number-columns-spanned="4">   						
				 	     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				 	</fo:table-cell>
				 	<fo:table-cell >   						
				 	     <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" 
				 	     font-size="10pt" keep-together="always" font-weight="bold">Net Amount</fo:block>
                  </fo:table-cell>
                   <fo:table-cell >   						
				 	     <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" 
				 	     keep-together="always">${afterReturnTotal?string("#0.00")}</fo:block>
                  </fo:table-cell>
                  <#assign taxAmount=afterReturnTotal*vatPercentage/100>
                  <#assign netTotalAmount=netTotalAmount-returnTotal+taxAmount>
                   </fo:table-row>
                   
                   </#if>
                   <#if (vatPercentage>1)>
                  <fo:table-row> 
				     <fo:table-cell >   						
				 	     <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt"
				 	      keep-together="always">ADD : VAT @ ${vatPercentage} % of Amount</fo:block>
                  </fo:table-cell>
                  <fo:table-cell number-columns-spanned="4">   						
				 	     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				 	</fo:table-cell>
                   <fo:table-cell >   						
				 	     <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" 
				 	     keep-together="always">${taxAmount?string("#0.00")}</fo:block>
                  </fo:table-cell>
                  </fo:table-row>
                   </#if>
				 	</#if>
				 	</#if>
                  </#list>
				 	
				</#list>
                  
                  
                  <fo:table-row> 
				     <fo:table-cell >   						
				 	     <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" 
				 	     font-size="10pt" keep-together="always" font-weight="bold">Final Total</fo:block>
                  </fo:table-cell>
                  <fo:table-cell number-columns-spanned="4">   						
				 	     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				 	</fo:table-cell>
                   <fo:table-cell >   						
				 	     <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" 
				 	     keep-together="always">${netTotalAmount?string("#0.00")}</fo:block>
                  </fo:table-cell>	
                   </fo:table-row>
                  
                  
                  
                  
                  
                  
                  
                  
                  
                   
                  <#--fo:table-row> 
				     <fo:table-cell >   						
				 	     <fo:block text-align="left" text-indent="12pt" white-space-collapse="false" font-family="Courier,monospace" 
				 	     font-size="10pt" keep-together="always">Sub Total Value</fo:block>
                  </fo:table-cell>
                  <fo:table-cell number-columns-spanned="4">   						
				 	     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				 	</fo:table-cell>
                   <fo:table-cell >   						
				 	     <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" 
				 	     keep-together="always">${finalAmount?string("#0.00")}</fo:block>
                  </fo:table-cell>
                   </fo:table-row--> 
			 	   <fo:table-row> 
				     <fo:table-cell border-style="solid">   						
				 	     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" 
				 	     font-weight="bold" keep-together="always">Grand Total</fo:block>
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
				 	     <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				 	</fo:table-cell>
				 	  <fo:table-cell border-style="solid">   
				 	     <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" 
				 	     font-weight="bold" keep-together="always">${netTotalAmount?string("#0.00")}</fo:block>
				 	</fo:table-cell>
			 	  </fo:table-row>
			 	 <fo:table-row> 
			     <fo:table-cell number-columns-spanned="5">   						
			 	     <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">Amount chargeable (in words)</fo:block>
					 <fo:block text-align="left" text-indent="5pt" keep-together="always" font-family="Courier,monospace" font-size="10pt" white-space-collapse="false" >Rupees ${Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble(netTotalAmount?string("#0.00")), "%rupees-and-paise", locale)} ONLY  </fo:block>
					 <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			         <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">Declaration</fo:block>
			         <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">We declare that this invoice shows the actual price of the goods</fo:block>
			         <fo:block text-align="left" text-indent="5pt" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">described and that all particulars are true and correct</fo:block>
			         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			        </fo:table-cell>
		 	  </fo:table-row>	
		 	   <fo:table-row> 
		 	      <fo:table-cell number-columns-spanned="3">   						
			 	     <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				 </fo:table-cell>
		 	      <fo:table-cell border-style="solid" number-columns-spanned="2">   						
			 	      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" font-weight="bold"
			 	       keep-together="always">For MOTHER DAIRY</fo:block>
			 	    <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			 	      <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" font-weight="bold"
			 	       keep-together="always">AUTHORISED SIGNATORY</fo:block>
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
 </fo:flow>
 </fo:page-sequence>
 </#list>	
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
