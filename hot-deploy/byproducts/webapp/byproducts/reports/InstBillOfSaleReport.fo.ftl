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

<#-- do not display columns associated with values specified in the request, ie constraint values -->
<fo:layout-master-set>
	<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-bottom=".5in">
        <fo:region-body margin-top=".7in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "BILLOFSALEReport.txt")}
<#if itemsReturnListMap?has_content> 
<#assign itemsList=itemsReturnListMap.entrySet()>
<#assign tinNumber="">
<#assign cstNumber="">
<#assign orgTinNumber="">
<#assign orgCstNumber="">
  <#if (orgTinNumber ="") && (orgCstNumber ="")>
	        	<#assign partyGroupOrg = delegator.findOne("PartyGroup", {"partyId" :"Company"}, true)>
	        	<#assign orgTinNumber = (partyGroupOrg.tinNumber)?if_exists>
	    		<#assign orgCstNumber = (partyGroupOrg.cstNumber)?if_exists>
	         </#if>	
	         
<#list itemsList as itemlst>
<fo:page-sequence master-reference="main" force-page-count="no-force" font-size="14pt" font-family="Courier,monospace">					
			 <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	

	         <#assign  facilityId=itemlst.getKey()>   
	         <#assign invoiceList=invoiceListMap.get(facilityId)>	
	          <#if (tinNumber ="") && (cstNumber ="")>
	        	<#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" :invoiceList.getString("partyId")}, true)>
	        	<#assign tinNumber = (partyGroup.tinNumber)?if_exists>
	    		<#assign cstNumber = (partyGroup.cstNumber)?if_exists>
	         </#if>	
			 	     <fo:block>
		            <fo:table width="100%" table-layout="fixed" space-after="0.0in">
		              <fo:table-column column-width="250pt"/>
		              <fo:table-column column-width="500pt"/>
		              <fo:table-column column-width="300pt"/>
		               <fo:table-body>
		                 <fo:table-row>
			                   <fo:table-cell>
			                         <fo:block  font-size = "12pt">&#160;</fo:block>
			                         <fo:block  font-size = "13pt">TIN :${orgTinNumber}</fo:block>
			                         <fo:block  font-size = "13pt">CST :90750068</fo:block>
			                     </fo:table-cell>
			                    <fo:table-cell>
			                        <fo:block text-align="left" font-size = "13pt" keep-together="always" white-space-collapse="false" font-weight="bold">&#160; KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LIMITED</fo:block>
									<fo:block text-align="left" font-size = "13pt" keep-together="always" white-space-collapse="false" font-weight="bold">UNIT:MOTHER DAIRY :G.K.V.K. POST, YELAHANKA, BANGALORE - 560 065</fo:block>
									<#if (reportTypeFlag=="instBillOfSale")>
									<fo:block text-align="left" font-size = "13pt" keep-together="always" white-space-collapse="false" font-weight="bold">&#160;                 BILL OF SALE</fo:block>
									<#else>
									<fo:block text-align="left" font-size = "13pt" keep-together="always" white-space-collapse="false" font-weight="bold">&#160;                 ENCLOSURE FOR TAX INVOICE</fo:block>
									</#if>
									<fo:block >-----------------------------------------------------------</fo:block>
			                   </fo:table-cell>
			                   <fo:table-cell>
			                         <fo:block>&#160;</fo:block>
			                         <fo:block text-align="right" font-size = "13pt" keep-together="always" white-space-collapse="false">INVOICE DATE :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invoiceList.getTimestamp("invoiceDate"), "dd-MMM-yyyy")}</fo:block>
			                         <fo:block text-align="right" font-size = "13pt" keep-together="always" white-space-collapse="false">INVOICE NO   :&#160;&#160;&#160;&#160;&#160;&#160;${invoiceList.get("invoiceId")?if_exists}</fo:block>
			                     </fo:table-cell>
		                     </fo:table-row>
		                     <#if (reportTypeFlag=="enclosureOfTaxInvoice")><#-- extraspace if TAX report-->
		                      <fo:table-row>
								<fo:table-cell>
									<fo:block text-align="right" font-size = "12pt" keep-together="always" white-space-collapse="false" font-weight="bold">&#160;</fo:block>
									<fo:block text-align="left" font-size = "12pt"  keep-together="always" white-space-collapse="false" font-weight="bold">&#160;&#160;</fo:block>
								</fo:table-cell>
						     </fo:table-row>
						     </#if>
		                     </fo:table-body>
		                    </fo:table>
		     </fo:block>  
		     	<#if (reportTypeFlag=="instBillOfSale")>
			     <fo:block>
			           <fo:table width="100%" table-layout="fixed" space-after="0.0in">
			          <fo:table-column column-width="250pt"/>
		              <fo:table-column column-width="500pt"/>
		              <fo:table-column column-width="300pt"/>
			               <fo:table-body>
				               <fo:table-row>
								<fo:table-cell>
									<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;</fo:block>
								</fo:table-cell>
							  </fo:table-row>	
					          <fo:table-row>
					             <fo:table-cell>
									<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="left" font-size = "13pt" keep-together="always" white-space-collapse="false" font-weight="bold">&#160;(ISSUED UNDER KARNATAKA VALUE ADDED TAX ACT 2003 WEF 01-04-2005)</fo:block>
									<fo:block text-align="left" font-size = "13pt" keep-together="always" white-space-collapse="false" font-weight="bold">&#160;&#160;           FOR SALE OF VAT EXEMPTED GOODS</fo:block>
								</fo:table-cell>
							</fo:table-row>
							
							<fo:table-row>
					             <fo:table-cell>
									<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="right" keep-together="always" white-space-collapse="false" font-weight="bold">&#160;</fo:block>
									<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold">&#160;&#160;</fo:block>
								</fo:table-cell>
							</fo:table-row>
						 </fo:table-body>	
						 </fo:table>
						</fo:block> 
						</#if>
			    <fo:block>
		            <fo:table width="100%" table-layout="fixed" space-after="0.0in">
		              <fo:table-column column-width="500pt"/>
		               <fo:table-column column-width="100pt"/>
		               <fo:table-column column-width="500pt"/>
		               <fo:table-body>
		                 <fo:table-row>
		                   <fo:table-cell>
		                   <fo:block>
				               <fo:table >
								<fo:table-column column-width="500pt"/>
								<fo:table-body>
										<fo:table-row>
											<fo:table-cell>
												<fo:block  text-align="left" font-weight="bold" font-size = "13pt">
									 			Buyer Name:${facilityId}/(${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, facilityId, false)})</fo:block>
											</fo:table-cell>
										</fo:table-row>
										<fo:table-row>
	                						<fo:table-cell>
					  						  <#assign partyTelephoneResult = dispatcher.runSync("getPartyTelephone", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", facilityId, "userLogin", userLogin))/>
				                		     <#assign partyAddressResult = dispatcher.runSync("getPartyPostalAddress", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", facilityId, "userLogin", userLogin))/>
										 <#if (partyAddressResult.address1?has_content)>
											<fo:block text-align="left" font-weight="bold" font-size = "13pt">
											 Address:${partyAddressResult.address1?if_exists} </fo:block>
											 </#if>
											 <#if (partyAddressResult.address2?has_content)>
											<fo:block font-weight="bold" text-align="left" keep-together="always" font-size = "13pt">
											&#160;&#160;&#160;&#160;${partyAddressResult.address2?if_exists} </fo:block>
											</#if>
											<#if (partyAddressResult.city?has_content)>
											<fo:block font-weight="bold" text-align="left" keep-together="always" font-size = "13pt">
											&#160;&#160;&#160;&#160;${partyAddressResult.city?if_exists} ${partyAddressResult.stateProvinceGeoId?if_exists}</fo:block>
											</#if>
											<#if (partyAddressResult.countryGeoId?has_content)>
											<fo:block font-weight="bold" text-align="left" keep-together="always" font-size = "13pt">
											&#160;&#160;&#160;&#160;${partyAddressResult.countryGeoId?if_exists}</fo:block>
											</#if>
											 <#if (partyAddressResult.contactNumber?has_content)>
										<fo:block font-weight="bold" text-align="left" keep-together="always" font-size = "13pt">
										&#160;&#160;&#160;&#160;${partyAddressResult.contactNumber?if_exists}</fo:block>
											</#if>
											<#if (partyAddressResult.postalCode?has_content)>
										<fo:block font-weight="bold" text-align="left" keep-together="always" font-size = "13pt">
										&#160;&#160;&#160;&#160;${partyAddressResult.postalCode?if_exists}</fo:block>
											</#if>
										</fo:table-cell>
										</fo:table-row>
										<fo:table-row>
											<fo:table-cell>
												<fo:block number-columns-spanned="2" text-align="left" font-weight="bold"></fo:block>
											</fo:table-cell>	
										</fo:table-row>					
									</fo:table-body>
								  </fo:table>
							  </fo:block>
					       </fo:table-cell>
					       <fo:table-cell><fo:block ></fo:block></fo:table-cell>
					       
		                    <fo:table-cell>
							<fo:block  text-align="right" font-size = "13pt" font-weight="bold"> Billing Period:${billingPeriodDate}&#160;&#160;&#160; </fo:block>
							</fo:table-cell>
							</fo:table-row>
							
							<fo:table-row>
								<fo:table-cell>
								<fo:block number-columns-spanned="2" text-align="left" font-weight="bold"></fo:block>
								</fo:table-cell>
							</fo:table-row>
							
							<fo:table-row>
								<fo:table-cell>
								<fo:block number-columns-spanned="2" text-align="left" font-weight="bold"></fo:block>
								</fo:table-cell>
							</fo:table-row>	
	                	   </fo:table-body>
		              	</fo:table>
		     		</fo:block>  
			<fo:block >-----------------------------------------------------------------------------------------------------------------------------------------------</fo:block>	
			<fo:block >
				<fo:table >
					<fo:table-column column-width="78pt"/>
					<fo:table-column column-width="50pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="210pt"/>
					<fo:table-column column-width="84pt"/>
					<fo:table-column column-width="97pt"/>
					<fo:table-column column-width="82pt"/>
					<fo:table-column column-width="86pt"/>
					<fo:table-column column-width="92pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell>
								<fo:block  text-align="left" font-weight="bold" font-size = "13pt">DATE</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="center" font-size = "13pt" font-weight="bold">SHIFT</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="center" font-size = "13pt" font-weight="bold">DC</fo:block>
								<fo:block  text-align="center" font-size = "13pt" font-weight="bold">No</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="center" font-size = "13pt" font-weight="bold">PO No</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="left" font-size = "13pt" font-weight="bold">&#160;&#160;PRODUCT</fo:block>
								<fo:block  text-align="left" font-size = "13pt" font-weight="bold">&#160;&#160;NAME</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right" font-size = "13pt" font-weight="bold"> QUANTITY</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right" font-size = "13pt" font-weight="bold">AMOUNT&#160;&#160;</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right" font-size = "13pt" font-weight="bold">RETURNS</fo:block>
								<fo:block  text-align="right" font-size = "13pt" font-weight="bold">QTY&#160;&#160;</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right" font-size = "13pt" font-weight="bold">RETURN</fo:block>
								<fo:block  text-align="right" font-size = "13pt" font-weight="bold">AMOUNT</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right" font-size = "13pt" font-weight="bold">TOTAL</fo:block>
								<fo:block  text-align="right" font-size = "13pt" font-weight="bold">QTY&#160;</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right" font-size = "13pt" font-weight="bold">TOTAL</fo:block>
								<fo:block  text-align="right" font-size = "13pt" font-weight="bold">AMOUNT</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
						<fo:table-cell>
							<fo:block >-----------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
						</fo:table-cell>
					</fo:table-row>					
					</fo:table-body>
				</fo:table>
			</fo:block>
			<fo:block >
				<#assign noOfCrReturns=05>	
				<fo:table>
					<fo:table-column column-width="78pt"/>
					<fo:table-column column-width="50pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="210pt"/>
					<fo:table-column column-width="84pt"/>
					<fo:table-column column-width="95pt"/>
					<fo:table-column column-width="82pt"/>
					<fo:table-column column-width="86pt"/>
					<fo:table-column column-width="95pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-body>
					 <#assign totalQuantity=0>
					 <#assign totalAmount=0>
					 <#assign totalReturnQuantity=0>
					 <#assign totalReturnAmount=0>
					 <#assign finalToatlQty=0>
					 <#assign finalTotalAmnt=0>

	              	<#assign orderItemsList= itemlst.getValue()?if_exists>	
					<#list orderItemsList as eachItem>
						<#if eachItem.get("shipmentTypeId")=="AM_SHIPMENT_SUPPL" || eachItem.get("shipmentTypeId")=="AM_SHIPMENT">  
						 <#assign shipmentTypeId="M">        		
		                 	<#elseif eachItem.get("shipmentTypeId")=="PM_SHIPMENT_SUPPL" || eachItem.get("shipmentTypeId")=="PM_SHIPMENT">
		              	 <#assign shipmentTypeId="E">   
	              	    </#if>
						<#assign returnsList =itemsReturnListMap.entrySet()>
						
						  <#assign noOfCrReturns=noOfCrReturns+1>	<#--pageSkip if number of lines more than 31 -->
			    		   <#if (noOfCrReturns==31) >
			    		    <fo:table-row><fo:table-cell><fo:block  page-break-after="always"></fo:block></fo:table-cell></fo:table-row> 
			    			<#assign noOfCrReturns=0>
			    		  </#if>
			    		  
						<#assign quantity=eachItem.get("quantity")>
						<#assign unitListPrice=eachItem.get("unitListPrice")>
						<#assign amount=(quantity)*(unitListPrice)>
						<#assign returnQuantity=eachItem.get("returnQuantity")>
						<#assign returnAmount=eachItem.get("returnAmount")>
						<#assign prodTotal=(quantity-returnQuantity)>
						<#assign prodTotalAmount=(amount-returnAmount)>
						<#assign finalToatlQty=finalToatlQty+prodTotal>
						<#assign finalTotalAmnt=finalTotalAmnt+prodTotalAmount>
						<fo:table-row>
							<fo:table-cell>
								<fo:block  font-size = "12pt" text-align="left">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(eachItem.get("estimatedShipDate"), "dd-MM-yy")}</fo:block>
							</fo:table-cell>
					        <fo:table-cell>
								<fo:block  font-size = "12pt" text-align="center">${shipmentTypeId}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  font-size = "12pt" text-align="center">${eachItem.get('orderId')?if_exists}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  font-size = "12pt" text-align="center">${eachItem.get("externalId")?if_exists}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="left" font-size="12pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(eachItem.get("productName")?if_exists)),26)}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block text-align="right" font-size="12pt">${eachItem.get("quantity")?string("#0.0")?if_exists}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right" font-size="12pt">${amount?string("#0.00")?if_exists}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block text-align="right" font-size="12pt">${eachItem.get("returnQuantity")?string("#0.0")?if_exists}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right" font-size="12pt">${eachItem.get("returnAmount")?string("#0.00")?if_exists}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right" font-size="12pt">${prodTotal?string("#0.0")?if_exists}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right" font-size="12pt">${prodTotalAmount?string("#0.00")?if_exists}</fo:block>
							</fo:table-cell>
							<#assign totalQuantity=totalQuantity+quantity>
							<#assign totalAmount=totalAmount+amount>
							<#assign totalReturnQuantity=totalReturnQuantity+returnQuantity>
							<#assign totalReturnAmount=totalReturnAmount+returnAmount>
							<#assign netQuantity=totalQuantity-totalReturnQuantity>
							<#assign netAmount=totalAmount-totalReturnAmount>
						</fo:table-row>
						</#list>
						<fo:table-row>
						  <fo:table-cell>
						        <fo:block >-----------------------------------------------------------------------------------------------------------------------------------------------</fo:block>	
						   </fo:table-cell>
						</fo:table-row>
				
						<fo:table-row>
							<fo:table-cell number-columns-spanned="5">
								<fo:block  text-align="left" font-weight="bold" text-indent="15pt">TOTAL </fo:block>
							</fo:table-cell>
							
							<fo:table-cell>
								<fo:block  text-align="right" font-size="12pt">${totalQuantity?string("#0.0")}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right" font-size="12pt">${totalAmount?string("#0.00")}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right" font-size="12pt">${totalReturnQuantity?string("#0.0")}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right" font-size="12pt">${totalReturnAmount?string("#0.00")}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right" font-size="12pt">${finalToatlQty?string("#0.0")}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right" font-size="12pt">${finalTotalAmnt?string("#0.00")}</fo:block>
							</fo:table-cell>
							
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell>
								<fo:block  text-align="left" font-size="12pt" font-weight="bold" >&#160;&#160;Net Pay</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right">&#160;</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right">&#160;</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right">&#160;</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right">&#160;</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right">&#160;</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right">&#160;</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right">&#160;</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right">&#160;</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right">&#160;</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right" font-size="12pt">&#160; ${netAmount?string("#0.00")}</fo:block>
							</fo:table-cell>
						</fo:table-row>
						
						<fo:table-row>
							<fo:table-cell>
								<fo:block >-----------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
						    </fo:table-cell>
					</fo:table-row>					
					</fo:table-body>
				</fo:table>
			</fo:block>
			<fo:block>
			           <fo:table width="100%" table-layout="fixed" space-after="0.0in">
			           <fo:table-column column-width="100pt"/>
			              <fo:table-body>
			              <fo:table-row>
						  <fo:table-cell>
								<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold" font-size="13pt">Amount in Words:${Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble(netAmount?string("#0.00")), "%rupees-and-paise", locale).toUpperCase()} ONLY  </fo:block>
							</fo:table-cell>
							</fo:table-row>	
							
							 <fo:table-row>
							 <fo:table-cell>
								<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold" font-size="12pt">&#160;  </fo:block>
							</fo:table-cell>
							</fo:table-row>	
							
				          <fo:table-row>
							<fo:table-cell>
								<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold" font-size="13pt">Note: Kindly verify the Details and confirm the invoice within three days of receiving the invoice failing which </fo:block>
								<fo:block text-align="left" font-size="13pt" keep-together="always" white-space-collapse="false" font-weight="bold" >&#160;     will be deemed that the invoice is in order. Expedite payment within 15 days from the receipt of this bill.</fo:block>
								<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							</fo:table-cell>
						</fo:table-row>	
						 <#if (reportTypeFlag=="enclosureOfTaxInvoice")>
							<fo:table-row>
							<fo:table-cell>
								<fo:block  keep-together="always" white-space-collapse="false" font-size="13pt" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;                    This is a system generated report, signature is not required.</fo:block>
							</fo:table-cell>
							</fo:table-row>		
							</#if>
						 </fo:table-body>	
						 </fo:table>
			        </fo:block> 
					<#if (reportTypeFlag=="instBillOfSale")>
				     <fo:block>
				           <fo:table width="100%" table-layout="fixed" space-after="0.0in">
				           <fo:table-column column-width="33%"/>
				            <fo:table-column column-width="33%"/>
				             <fo:table-column column-width="33%"/>
				              <fo:table-body>
				                <fo:table-row>
									<fo:table-cell number-columns-spanned="3">
										<fo:block >&#160;</fo:block> 
									</fo:table-cell>
							     </fo:table-row>	
				              <fo:table-row>
				               <fo:table-cell number-columns-spanned="2">   						
						 	     <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 </fo:table-cell>
					 	      <fo:table-cell border-style="solid">   						
						 	      <fo:block text-align="center" font-size="13pt" white-space-collapse="false" font-family="Courier,monospace"  font-weight="bold"
						 	       keep-together="always">For MOTHER DAIRY</fo:block>
						 	      <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
						 	      <fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
						 	      <fo:block text-align="center" font-size="13pt" white-space-collapse="false" font-family="Courier,monospace"  font-weight="bold"
						 	       keep-together="always">AUTHORISED SIGNATORY</fo:block>
						      </fo:table-cell>
				              </fo:table-row>		
							 </fo:table-body>	
							 </fo:table>
					</fo:block> 
		         	</#if>  
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