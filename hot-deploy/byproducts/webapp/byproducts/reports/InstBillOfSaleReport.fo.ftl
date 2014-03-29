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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-bottom="1in" margin-left=".3in" margin-right="1in">
        <fo:region-body margin-top=".7in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "GenerateTrnsptMarginReport.txt")}
<#if itemsListMap?has_content> 
<#assign itemsList=itemsListMap.entrySet()>
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			 <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
			  <#list itemsList as itemlst>
	         <#assign  facilityId=itemlst.getKey()>   
	         <#assign invoiceList=invoiceListMap.get(facilityId)>	
			 	    <fo:block>
		            <fo:table width="100%" table-layout="fixed" space-after="0.0in">
		             <fo:table-column column-width="100pt"/>
		              <fo:table-column column-width="420pt"/>
		               <fo:table-column column-width="200pt"/>
		               <fo:table-body>
		                 <fo:table-row>
			                   <fo:table-cell>
			                         <fo:block  text-indent="15pt">&#160;</fo:block>
			                         <fo:block  text-indent="15pt">TIN :</fo:block>
			                         <fo:block  text-indent="15pt">CST :</fo:block>
			                     </fo:table-cell>
			                    <fo:table-cell>
			                        <fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160; KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LIMITED</fo:block>
									<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;      UNIT : MOTHER DAIRY : BANGALORE - 560 065</fo:block>
									<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                 BILL OF SALE</fo:block>
									<fo:block text-align="left" keep-together="always" white-space-collapse="false">-----------------------------------------------------------</fo:block>
			                   </fo:table-cell>
			                   <fo:table-cell>
			                         <fo:block  text-indent="15pt">&#160;</fo:block>
			                         <fo:block  text-indent="15pt">INVOICE DATE :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invoiceList.getTimestamp("invoiceDate"), "dd-MMM-yyyy")}</fo:block>
			                         <fo:block  text-indent="15pt">&#160;INVOICE NO :${invoiceList.getString("invoiceId")}</fo:block>
			                     </fo:table-cell>
		                     </fo:table-row>
		                     </fo:table-body>
		                    </fo:table>
		     </fo:block> 
			     <fo:block>
			           <fo:table width="100%" table-layout="fixed" space-after="0.0in">
			           <fo:table-column column-width="100pt"/>
		               <fo:table-column column-width="420pt"/>
		               <fo:table-column column-width="200pt"/>
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
								<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160; (ISSUED UNDER KARNATAKA VALUE ADDED TAX ACT 2003 WEF 01-04-2005)</fo:block>
								<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;      FOR SALE OF VAT EXEMPTED GOODS</fo:block>
							</fo:table-cell>
						</fo:table-row>	
						 </fo:table-body>	
						 </fo:table>
						</fo:block> 
			    <fo:block>
		            <fo:table width="100%" table-layout="fixed" space-after="0.0in">
		              <fo:table-column column-width="200pt"/>
		               <fo:table-column column-width="430pt"/>
		               <fo:table-column column-width="300pt"/>
		               <fo:table-body>
		                 <fo:table-row>
			                   <fo:table-cell>
			                     <fo:block  text-indent="15pt">CUST NO :${facilityId}</fo:block>
			                     <#assign partyTelephoneResult = dispatcher.runSync("getPartyTelephone", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", facilityId, "userLogin", userLogin))/>
			                     <#assign partyAddressResult = dispatcher.runSync("getPartyPostalAddress", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", facilityId, "userLogin", userLogin))/>
			                      <fo:block  text-indent="15pt">${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, facilityId, false)}</fo:block>
								<#if (partyAddressResult.address1?has_content)>
								<fo:block font-size="5pt" text-align="center" keep-together="always">&#160;(${partyAddressResult.address1?if_exists})</fo:block>
								</#if>
								<#if (partyTelephoneResult.contactNumber?has_content)>
								<fo:block font-size="5pt" text-align="center" keep-together="always">&#160;(${partyTelephoneResult.contactNumber?if_exists})</fo:block>
								</#if>
			                        </fo:table-cell>
			                    <fo:table-cell>&#160;&#160;&#160;                  </fo:table-cell>
			                    <fo:table-cell>
			                         <fo:block>THE DIRECTOR:</fo:block>
			                         <fo:block>MOTHER DAIRY</fo:block>
			                         <fo:block>G.K.V.K. POST</fo:block>
			                         <fo:block>YELAHANKA</fo:block>
			                         <fo:block>BANGALORE</fo:block>
			                         <fo:block>PHONE : 8460162</fo:block>
			                         
			                         <fo:block>PLEASE MAIL CHEQUES TO</fo:block>
			                   </fo:table-cell>
		                     </fo:table-row>
		                     </fo:table-body>
		                    </fo:table>
		     </fo:block>         
			<fo:block text-align="left" keep-together="always" white-space-collapse="false">------------------------------------------------------------------------------------------------------------------------------</fo:block>	<fo:block font-size="6pt">
				<fo:table>
					<fo:table-column column-width="18pt"/>
					<fo:table-column column-width="55pt"/>
					<fo:table-column column-width="42pt"/>
					<fo:table-column column-width="50pt"/>
					<fo:table-column column-width="50pt"/>
					<fo:table-column column-width="50pt"/>
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="right">DATE</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="right">SHIFT</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="right">DC No</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="right">PRODUCT</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="right">QUANTITY</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="right">AMOUNT</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
						<fo:table-cell>
							<fo:block text-align="left" keep-together="always" white-space-collapse="false">------------------------------------------------------------------------------------------------------------------------------</fo:block>
						</fo:table-cell>
					</fo:table-row>					
					</fo:table-body>
				</fo:table>
			</fo:block>
			<fo:block font-size="6pt">
				<fo:table>
					<fo:table-column column-width="40pt"/>
					<fo:table-column column-width="25pt"/>
					<fo:table-column column-width="50pt"/>
					<fo:table-column column-width="50pt"/>
					<fo:table-column column-width="50pt"/>
					<fo:table-column column-width="50pt"/>
					<fo:table-body>
					 <#assign totalQuantity=0>
					 <#assign totalAmount=0>
					 <#assign  itList=itemlst.getValue()>
					 <#list itList as orderLst>
					 <#assign quantity=orderLst.getBigDecimal("quantity")>
					 <#assign unitListPrice=orderLst.getBigDecimal("unitListPrice")>
					 <#assign shipmentTypeId=orderLst.getString("shipmentTypeId")>
					 <#if shipmentTypeId=="AM_SHIPMENT_SUPPL" || shipmentTypeId=="AM_SHIPMENT">  
					 <#assign shipmentTypeId="M">        		
	              	<#elseif shipmentTypeId=="PM_SHIPMENT_SUPPL" || shipmentTypeId=="PM_SHIPMENT">
	              	 <#assign shipmentTypeId="E">   
              	    </#if> 
              	    <#assign amount=(quantity)*(unitListPrice)>
					
						<fo:table-row>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="right">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(orderLst.getTimestamp("orderDate"), "dd-MMM-yyyy")}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="right">${shipmentTypeId}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="right">${orderLst.getString("orderId")}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="right">${orderLst.getString("itemDescription")}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="right">${quantity?string("#0.00")}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="right">${amount?string("#0.00")}</fo:block>
							</fo:table-cell>
							<#assign totalQuantity=totalQuantity+quantity>
							<#assign totalAmount=totalAmount+amount>
						</fo:table-row>
						</#list>
					</fo:table-body>
				</fo:table>
			</fo:block>
			<fo:block text-align="left" keep-together="always" white-space-collapse="false">------------------------------------------------------------------------------------------------------------------------------</fo:block>	<fo:block font-size="6pt">
				<fo:table>
					<fo:table-column column-width="18pt"/>
					<fo:table-column column-width="55pt"/>
					<fo:table-column column-width="42pt"/>
					<fo:table-column column-width="50pt"/>
					<fo:table-column column-width="50pt"/>
					<fo:table-column column-width="50pt"/>
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="right">TOTAL</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="right">&#160;</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="right">&#160;</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="right">&#160;</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="right">${totalQuantity?string("#0.00")}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="6pt" text-align="right">${totalAmount?string("#0.00")}</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
						<fo:table-cell>
							<fo:block text-align="left" keep-together="always" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------------------</fo:block>
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
								<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;Note: Kindly verify the Details and confirm the invoice within three days of receiving the invoice failing which will be deemed</fo:block>
								<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;         that the invoice is in order  expedits payment within 15 days of bill received.</fo:block>
								<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160; </fo:block>
							</fo:table-cell>
						</fo:table-row>	
						<fo:table-row>
						<fo:table-cell>
								<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;                                                   FOR MOTHER DAIRY </fo:block>
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