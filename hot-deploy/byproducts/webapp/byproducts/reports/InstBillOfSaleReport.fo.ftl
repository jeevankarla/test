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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-bottom=".5in" margin-left=".3in" margin-right="1in">
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
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
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
		              <fo:table-column column-width="150pt"/>
		              <fo:table-column column-width="480pt"/>
		              <fo:table-column column-width="350pt"/>
		               <fo:table-body>
		                 <fo:table-row>
			                   <fo:table-cell>
			                         <fo:block  text-indent="15pt">&#160;</fo:block>
			                         <fo:block  text-indent="15pt">TIN :${orgTinNumber}</fo:block>
			                         <fo:block  text-indent="15pt">CST :${orgCstNumber}</fo:block>
			                     </fo:table-cell>
			                    <fo:table-cell>
			                        <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold">&#160; KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LIMITED</fo:block>
									<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold">UNIT:MOTHER DAIRY :G.K.V.K. POST, YELAHANKA, BANGALORE - 560 065</fo:block>
									<#if (reportTypeFlag=="instBillOfSale")>
									<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold">&#160;                 BILL OF SALE</fo:block>
									<#else>
									<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold">&#160;                 ENCLOSURE FOR TAX INVOICE</fo:block>
									</#if>
									<fo:block >-----------------------------------------------------------</fo:block>
			                   </fo:table-cell>
			                   <fo:table-cell>
			                         <fo:block>&#160;</fo:block>
			                         <fo:block>INVOICE DATE :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invoiceList.getTimestamp("invoiceDate"), "dd-MMM-yyyy")}</fo:block>
			                         <fo:block>INVOICE NO :${invoiceList.getString("invoiceId")}</fo:block>
			                     </fo:table-cell>
		                     </fo:table-row>
		                     <#if (reportTypeFlag=="enclosureOfTaxInvoice")><#-- extraspace if TAX report-->
		                      <fo:table-row>
							<fo:table-cell>
								<fo:block text-align="right" keep-together="always" white-space-collapse="false" font-weight="bold">&#160;</fo:block>
								<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold">&#160;&#160;</fo:block>
							</fo:table-cell>
						     </fo:table-row>
						     </#if>
		                     </fo:table-body>
		                    </fo:table>
		     </fo:block>  
		     	<#if (reportTypeFlag=="instBillOfSale")>
			     <fo:block>
			           <fo:table width="100%" table-layout="fixed" space-after="0.0in">
			           <fo:table-column column-width="150pt"/>
		               <fo:table-column column-width="480pt"/>
		               <fo:table-column column-width="350pt"/>
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
									<fo:block text-align="right" keep-together="always" white-space-collapse="false" font-weight="bold">&#160;   (ISSUED UNDER KARNATAKA VALUE ADDED TAX ACT 2003 WEF 01-04-2005)</fo:block>
									<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold">&#160;&#160;           FOR SALE OF VAT EXEMPTED GOODS</fo:block>
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
		               <fo:table-column column-width="500pt"/>
		               <fo:table-column column-width="500pt"/>
		               <fo:table-body>
		                 <fo:table-row>
			                   <fo:table-cell>
			               <fo:table >
								<fo:table-column column-width="500pt"/>
								<fo:table-column column-width="500pt"/>
								<fo:table-column column-width="500pt"/>
							<fo:table-body>
									<fo:table-row>
										<fo:table-cell>
											<fo:block  text-align="left" font-weight="bold" text-indent="15pt">
								 			Bayer Name:${facilityId}/(${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, facilityId, false)})</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
                						<fo:table-cell>
				  						  <#assign partyTelephoneResult = dispatcher.runSync("getPartyTelephone", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", facilityId, "userLogin", userLogin))/>
			                		     <#assign partyAddressResult = dispatcher.runSync("getPartyPostalAddress", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", facilityId, "userLogin", userLogin))/>
									 <#if (partyAddressResult.address1?has_content)>
										<fo:block text-align="left" font-weight="bold" text-indent="15pt">
										 Address:${partyAddressResult.address1?if_exists}</fo:block>
										 </#if>
										 <#if (partyAddressResult.address2?has_content)>
										<fo:block font-weight="bold" text-align="left" keep-together="always" text-indent="50pt">
										&#160;${partyAddressResult.address2?if_exists}</fo:block>
										</#if>
										<#if (partyAddressResult.city?has_content)>
										<fo:block font-weight="bold" text-align="left" keep-together="always" text-indent="50pt">
										&#160;${partyAddressResult.city?if_exists} ${partyAddressResult.stateProvinceGeoId?if_exists}</fo:block>
										</#if>
										<#if (partyAddressResult.countryGeoId?has_content)>
										<fo:block font-weight="bold" text-align="left" keep-together="always" text-indent="50pt">
										&#160;${partyAddressResult.countryGeoId?if_exists}</fo:block>
										</#if>
										 <#if (partyAddressResult.contactNumber?has_content)>
									<fo:block font-weight="bold" text-align="left" keep-together="always" text-indent="50pt">
									&#160;${partyAddressResult.contactNumber?if_exists}</fo:block>
										</#if>
										<#if (partyAddressResult.postalCode?has_content)>
									<fo:block font-weight="bold" text-align="left" keep-together="always" text-indent="50pt">
									&#160;${partyAddressResult.postalCode?if_exists}</fo:block>
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
						       </fo:table-cell>
			                    <fo:table-cell>
									<fo:table >
										<fo:table-column column-width="500pt"/>
										<fo:table-column column-width="500pt"/>
										<fo:table-column column-width="500pt"/>
											<fo:table-body>
												<fo:table-row>
													<fo:table-cell>
													<fo:block  text-align="left" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160; Billing Period:${billingPeriodDate}</fo:block>
													 <fo:block text-align="left" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;PO No: <#if partyPONumMap?has_content && (partyPONumMap.get(facilityId))?exists>${partyPONumMap.get(facilityId)}</#if></fo:block>
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
									</fo:table-cell>
		            	         </fo:table-row>
		                	   </fo:table-body>
		              	</fo:table>
		     		</fo:block>  
			<fo:block >-----------------------------------------------------------------------------------------------------------------------------------------------</fo:block>	
			<fo:block >
				<fo:table >
					<fo:table-column column-width="70pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="170pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="90pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell>
								<fo:block  text-align="left" font-weight="bold" text-indent="15pt">DATE</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="center" font-weight="bold">SHIFT</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="center" font-weight="bold">INVOICE No</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="center" font-weight="bold">PRODUCT</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right" font-weight="bold"> QUANTITY</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right" font-weight="bold">AMOUNT&#160;&#160;</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="center" font-weight="bold">&#160;RETURNS QTY</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="center" font-weight="bold">RETURN AMOUNT</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="center" font-weight="bold">TOTAL QTY</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="center" font-weight="bold">TOTAL AMOUNT</fo:block>
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
				<fo:table>
					<fo:table-column column-width="70pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="170pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="90pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="100pt"/>
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
			<#assign noOfCrReturns=0>	
			<#assign returnsList =itemsReturnListMap.entrySet()>
				<#assign noOfCrReturns=noOfCrReturns+1>	
    		   <#if (noOfCrReturns==40) >
    			<fo:block  break-after="page"></fo:block>
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
								<fo:block  text-align="left">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(eachItem.get("estimatedShipDate"), "dd-MM-yyyy")}</fo:block>
							</fo:table-cell>
					        <fo:table-cell>
								<fo:block  text-align="center">${shipmentTypeId}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="center">${eachItem.get('orderId')?if_exists}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="left" font-size="11pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(eachItem.get("productName")?if_exists)),25)}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block text-align="right">${eachItem.get("quantity")?string("#0.00")?if_exists}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right">${amount?string("#0.00")?if_exists}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block text-align="right">${eachItem.get("returnQuantity")?string("#0.00")?if_exists}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right">${eachItem.get("returnAmount")?string("#0.00")?if_exists}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right">${prodTotal?string("#0.00")?if_exists}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right">${prodTotalAmount?string("#0.00")?if_exists}</fo:block>
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
							<fo:table-cell number-columns-spanned="4">
								<fo:block  text-align="left" font-weight="bold" text-indent="15pt">TOTAL </fo:block>
							</fo:table-cell>
							
							<fo:table-cell>
								<fo:block  text-align="right">${totalQuantity?string("#0.00")}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right">${totalAmount?string("#0.00")}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right">${totalReturnQuantity?string("#0.00")}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right">${totalReturnAmount?string("#0.00")}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right">${finalToatlQty?string("#0.00")}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block  text-align="right">${finalTotalAmnt?string("#0.00")}</fo:block>
							</fo:table-cell>
							
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell>
								<fo:block  text-align="left" font-weight="bold" >&#160;&#160;Net Pay</fo:block>
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
								<fo:block  text-align="right">&#160; ${netAmount?string("#0.00")}</fo:block>
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
								<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold" text-indent="15pt">Amount in Words:${Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble(totalAmount?string("#0.00")), "%rupees-and-paise", locale).toUpperCase()} ONLY  </fo:block>
							</fo:table-cell>
							</fo:table-row>	
							 <fo:table-row>
							 <fo:table-cell>
								<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold" text-indent="15pt">&#160;  </fo:block>
							</fo:table-cell>
							</fo:table-row>	
				          <fo:table-row>
							<fo:table-cell>
								<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold" text-indent="15pt">Note: Kindly verify the Details and confirm the invoice within three days of receiving the invoice failing which will be deemed</fo:block>
								<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold" >&#160;         that the invoice is in order. Expedite payment within 15 days from the receipt of this bill.</fo:block>
								<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							</fo:table-cell>
						</fo:table-row>	
						<fo:table-row>
						<fo:table-cell>
							<fo:block  keep-together="always" white-space-collapse="false" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;                          This is a system generated report, signature is not required.</fo:block>
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