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
                <fo:region-body margin-top=".6in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
       
        <#assign routeMapEntries = routeMap.entrySet()>        
        <#list routeMapEntries as routeValues>
        	<#assign boothValuesList = routeValues.getValue()>
        	<#if boothValuesList?has_content>
        	<#list boothValuesList as boothValues>
        		<#assign boothsList = boothValues.get("boothList")>        		
        		<#list boothsList as boothEntries>
		        <fo:page-sequence master-reference="main">		        	
		        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
		        	<fo:block  border-style="solid" font-family="Courier,monospace">
		        			<fo:table  table-layout="fixed" width="100%" space-before="0.2in">
		        				<fo:table-column column-width="50pt"/>
        						<fo:table-column column-width="50pt"/>
        						<fo:table-column column-width="50pt"/>
        						<fo:table-column column-width="50pt"/>
        						<fo:table-column column-width="50pt"/>
        						<fo:table-column column-width="50pt"/>
        						<fo:table-header>
        							<fo:table-row>
        								<fo:table-cell>
        								<fo:block text-align="left">
                                           <#if logoImageUrl?has_content><fo:external-graphic src="<@ofbizContentUrl>${logoImageUrl}</@ofbizContentUrl>" overflow="hidden" height="40px" content-height="scale-to-fit"/></#if>
                                      </fo:block>
        								</fo:table-cell>
        								<fo:table-cell>
        								<fo:block>
        								</fo:block>
        								</fo:table-cell>
        								<fo:table-cell>
        								<fo:block text-align="left" white-space-collapse="false" keep-together="always"  font-family="Courier,monospace">&#160;    ${uiLabelMap.KMFDairyHeader}</fo:block>
        								<fo:block text-align="left" white-space-collapse="false" keep-together="always"  font-family="Courier,monospace">&#160;       ${uiLabelMap.KMFDairySubHeader}             </fo:block>
        								<!--<fo:block text-align="left"  white-space-collapse="false" keep-together="always"   font-family="Courier,monospace">&#160;				${companyName?if_exists},<#if postalAddress?exists><#if postalAddress?has_content>${postalAddress.address1?if_exists},${postalAddress.address2?if_exists},${postalAddress.city?if_exists},${stateProvinceAbbr?if_exists},${postalAddress.postalCode?if_exists},${countryName?if_exists}
										        <#else>${uiLabelMap.CommonNoPostalAddress},${uiLabelMap.CommonFor}: ${companyName}</#if></#if>
										        </fo:block>
										    <#if sendingPartyTaxId?exists || phone?exists || email?exists || website?exists || eftAccount?exists>
										    <fo:block  text-align="left"  white-space-collapse="false" keep-together="always"   font-family="Courier,monospace">&#160;<#if sendingPartyTaxId?exists>				${uiLabelMap.PartyTaxId}:${sendingPartyTaxId},</#if>
										    <#if phone?exists>${uiLabelMap.CommonTelephoneAbbr}:<#if phone.countryCode?exists>${phone.countryCode}-</#if><#if phone.areaCode?exists>${phone.areaCode}-</#if>${phone.contactNumber?if_exists} </#if>
										    <#if email?exists>${uiLabelMap.CommonEmail}:${email.infoString?if_exists},</#if>
										        <#if website?exists>${uiLabelMap.CommonWebsite}:${website.infoString?if_exists}</#if>	
										        </fo:block>		        right
										        <#if eftAccount?exists><fo:block text-align="left"  white-space-collapse="false" keep-together="always"   font-family="Courier,monospace">&#160;					${uiLabelMap.CommonFinBankName}:${eftAccount.bankName?if_exists},${uiLabelMap.CommonRouting}:${eftAccount.routingNumber?if_exists},${uiLabelMap.CommonBankAccntNrAbbr}:${eftAccount.accountNumber?if_exists}</fo:block></#if>
        			                      </#if>-->
        			                     	</fo:table-cell>
        							</fo:table-row>
        							<fo:table-row> 
        								<fo:table-cell>
        									 <fo:block text-align="left"  white-space-collapse="false" keep-together="always"   font-family="Courier,monospace">&#160;No. 3A,PASUMPON MUTHURAMALINGANAR SALAI,NANDANAM,CHENNAI - 600 035. PHONE: 23464563, 23464546,</fo:block>
        							  		<fo:block text-align="left"  white-space-collapse="false" keep-together="always"   font-family="Courier,monospace">&#160;29030439   PRODUCT DAIRY,AMBATTUR, PHONE: 32573880                (TOLL FREE: 18004253300)</fo:block>
        									 <fo:block text-align="left"  white-space-collapse="false" keep-together="always"   font-family="Courier,monospace">&#160;        REGD. OFFICE: AAVIN ILLAM, MADHAVARAM. MILK COLONY, CHENNAI-600 051.</fo:block>
        							  	</fo:table-cell>
        							</fo:table-row>
        							<fo:table-row> 
        							<fo:table-cell>
        								<fo:block>
        								</fo:block>
        								</fo:table-cell>
        								<fo:table-cell >
        								<fo:block>
        								</fo:block>
        								</fo:table-cell>
        								<fo:table-cell >	
        							  </fo:table-cell>
        							</fo:table-row>
        							<fo:table-row>
        							<fo:table-cell>
        							    <fo:block text-align="left"  white-space-collapse="false" keep-together="always">&#160;TIN No: 33761080302</fo:block>
        							</fo:table-cell>
        							</fo:table-row>
        							<fo:table-row>
        								<fo:table-cell >
        								    <fo:block text-align="left"  white-space-collapse="false" keep-together="always">&#160;CST No: 50205/dt. 1-2-81</fo:block>
        									<fo:block text-align="left" font-size="7pt" white-space-collapse="false" keep-together="always">&#160;Area Code: 0 5 5</fo:block>
        								</fo:table-cell>
        								<fo:table-cell >
        								<fo:block>
        								</fo:block>
        								</fo:table-cell>
        								<fo:table-cell>
        									<fo:block text-align="left" white-space-collapse="false" keep-together="always"></fo:block>
        								    <fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;                      INVOICE / DELIVERY NOTE</fo:block>
        								</fo:table-cell>
        								<fo:table-cell >
        								<fo:block>
        								</fo:block>
        								</fo:table-cell>
        								<fo:table-cell>
        									<fo:block text-align="left" font-size="9pt" white-space-collapse="false" keep-together="always">&#160;                                                         Invoice no: ${boothEntries.get("invoiceNumber")?if_exists}</fo:block>
        								    <fo:block text-align="left"  font-size="9pt" white-space-collapse="false" keep-together="always">&#160;                                                        Date: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(estimatedDeliveryDate, "dd/MM/yyyy")?if_exists}</fo:block>
        								</fo:table-cell>
        							</fo:table-row>
        							<fo:table-row> 
        								<fo:table-cell><fo:block  text-align="left" white-space-collapse="false" keep-together="always"></fo:block></fo:table-cell>
        							</fo:table-row>
        							<fo:table-row >
        								<fo:table-cell>
        									<fo:block>-------------------------------------------------------------------------------------------------</fo:block>
        								</fo:table-cell>
        							</fo:table-row>
        							<fo:table-row>
        								<fo:table-cell>
        									<fo:block text-align="left"  white-space-collapse="false" keep-together="always">Party Code   : ${boothEntries.get("booth")?if_exists}</fo:block>
        								    <fo:block text-align="left"  white-space-collapse="false" keep-together="always">Party Name   : ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, boothEntries.get("ownerPartyId"), false)}</fo:block>
        								    <fo:block text-align="left"  white-space-collapse="false" keep-together="always">Area         : ${boothEntries.get("area")?if_exists}</fo:block>
        								    <#if boothEntries.get("boothCategory") == 'REPLACEMENT_BYPROD'>
        								    	<#assign desFac = destinationFacMap.get(boothEntries.get("booth"))>
        								    	<fo:block text-align="left"  white-space-collapse="false" keep-together="always">Destination Party: ${desFac?if_exists}</fo:block>
        								    </#if>
        								</fo:table-cell>
        								<fo:table-cell/>
        								<fo:table-cell/>
        								<fo:table-cell>
        									<fo:block text-align="left"  white-space-collapse="false" keep-together="always">&#160;                                                  Sch. No.  : </fo:block>
        								    <fo:block text-align="left"  white-space-collapse="false" keep-together="always">&#160;                                                  Route No. : ${boothValues.get("route")}</fo:block>
        								    <fo:block text-align="left"  white-space-collapse="false" keep-together="always">&#160;                                                  Point No. : </fo:block>
        								</fo:table-cell>
        							</fo:table-row>
        							<fo:table-row>
        								<fo:table-cell>
        									<fo:block>-------------------------------------------------------------------------------------------------</fo:block>
        								</fo:table-cell>
        							</fo:table-row>         							
        						</fo:table-header>
        						<fo:table-body>
        							<fo:table-row width="100%">
        								<fo:table-cell> 
        									<fo:block >    
        										<fo:table  table-layout="fixed" width="100%" space-before="0.2in">
							        				<fo:table-column column-width="20%"/>
					        						<fo:table-column column-width="80%"/>
					        						<fo:table-column column-width="23%"/>
					        						<fo:table-column column-width="25%"/>
					        						<fo:table-column column-width="35%"/>
					        						<fo:table-column column-width="30%"/>
					        						<fo:table-column column-width="39%"/>
					        						<fo:table-column column-width="35%"/>
					        						<fo:table-column column-width="45%"/>
					        						<fo:table-column column-width="51%"/>
					        						<fo:table-header>
						        						<fo:table-row>
						        							<fo:table-cell border-style="solid">
						        								<fo:block  white-space-collapse="false" >&#160;   PCD</fo:block>
						        							</fo:table-cell>
						        							<fo:table-cell border-style="solid">
						        								<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;  PRODUCT NAME</fo:block>
						        							</fo:table-cell>
						        							<fo:table-cell border-style="solid">
						        								<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;  VAT</fo:block>
						        								<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;  %</fo:block>
						        							</fo:table-cell>
						        							<fo:table-cell border-style="solid">
						        								<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;     QTY</fo:block>
						        							</fo:table-cell>
						        							<fo:table-cell border-style="solid">
						        								<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;   BASIC</fo:block>
						        								<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;   RATE</fo:block>
						        							</fo:table-cell>
						        							<fo:table-cell border-style="solid">
						        								<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;    VAT</fo:block>
						        								<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;    RATE</fo:block>
						        							</fo:table-cell>
						        							<fo:table-cell border-style="solid">
						        								<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;    BASIC</fo:block>
						        								<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;    VALUE</fo:block>
						        							</fo:table-cell>
						        							<fo:table-cell border-style="solid">
						        								<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;   VAT</fo:block>
						        								<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;   VALUE</fo:block>
						        							</fo:table-cell>
						        							<fo:table-cell border-style="solid">
						        								<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;  TOTAL</fo:block>
						        								<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;  VALUE</fo:block>
						        							</fo:table-cell>
						        						</fo:table-row>	
						        						<fo:table-row >
					        								<fo:table-cell>
					        									<fo:block>-------------------------------------------------------------------------------------------------</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        						</fo:table-header>
					        						<fo:table-body>
					        							<#assign productList = boothEntries.get("productList")>
										        		<#assign totQty =0>
										        		<#assign totBasicValue =0>
										        		<#assign totVatValue =0>
										        		<#assign totalValue =0>
					        						    <#list productList as productEntries>	
										        		<#assign totQty =totQty+productEntries.get("quantity")>
										        		<#assign totBasicValue =totBasicValue+productEntries.get("basicValue")>
										        		<#assign totVatValue =totVatValue+productEntries.get("vatValue")>
										        		<#assign totalValue =totalValue+productEntries.get("totalValue")>
					        							<fo:table-row>
					        								<fo:table-cell border-style="solid">
					        									<fo:block text-align="center">${productEntries.get("productId")}</fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell border-style="solid">
					        									<fo:block text-indent="10pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(productEntries.get("productName"))),17)}</fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell border-style="solid">
					        									<fo:block text-align="right">${productEntries.get("VAT_Percentage")?string("#0.0")}</fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell border-style="solid">
					        									<fo:block text-align="right">${productEntries.get("quantity")}</fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell border-style="solid">
					        									<fo:block text-align="right">${productEntries.get("basicRate")?string("#0.00")}</fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell border-style="solid">
					        									<fo:block text-align="right">${productEntries.get("vatRate")?string("#0.00")}</fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell border-style="solid">
					        									<fo:block text-align="right">${(productEntries.get("basicValue"))?string("#0.00")}</fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell border-style="solid">
					        									<fo:block text-align="right">${(productEntries.get("vatValue"))?string("#0.00")}</fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell border-style="solid">
					        									<fo:block text-align="center">${((productEntries.get("basicRate")*productEntries.get("quantity"))+(productEntries.get("quantity")*productEntries.get("vatRate")))?string("#0.00")}</fo:block>
					        								</fo:table-cell>					        								
					        							</fo:table-row>
					        						</#list>	
					        						<fo:table-row >
				        								<fo:table-cell>
				        									<fo:block>-------------------------------------------------------------------------------------------------</fo:block>
				        								</fo:table-cell>
				        							</fo:table-row>
					        						<fo:table-row font-weight="bold">
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always"  text-align="center"></fo:block>
					        							</fo:table-cell>					        							
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always"  text-align="left">&#160;AMOUNT INCLUSIVE OF EXCISE DUTY</fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always"  text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always"  text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always"  text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always"  text-align="right"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always"  text-align="right">${totBasicValue?if_exists?string("#0.00")}</fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always"  text-align="right">${totVatValue?if_exists?string("#0.00")}</fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell border-style="solid">
					        								<fo:block keep-together="always"  text-align="center">${totalValue?if_exists?string("#0.00")}</fo:block>
					        							</fo:table-cell>
					        						</fo:table-row>
					        						<fo:table-row>
        								<fo:table-cell>
        									<fo:block>-------------------------------------------------------------------------------------------------</fo:block>
        								</fo:table-cell>
        							</fo:table-row>   
					        						<fo:table-row >
					        							<fo:table-cell>		
					        								<fo:block keep-together="always"  text-align="left">Rupees : </fo:block>
					        								<fo:block keep-together="always" text-align="left"  font-weight="bold">${Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble(totalValue?string("#0.00")), "%rupees-and-paise", locale).toUpperCase()} ONLY</fo:block>
					        								<fo:block keep-together="always"  text-align="left">
					        									<fo:table  table-layout="fixed" width="100%" space-before="0.2in">
											        				<fo:table-column column-width="101%"/>
									        						<fo:table-column column-width="85%"/>
									        						<fo:table-column column-width="25%"/>
									        						<fo:table-column column-width="30%"/>
									        					<fo:table-body>
									        						<fo:table-row border-style="solid">
									        							<fo:table-cell border-style="solid">
									        								<fo:block text-align="left"  keep-together="always" white-space-collapse="false">For TCMPF Ltd.</fo:block>
									        								<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
									        								<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
									        								<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
									        								<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
									        								<fo:block text-align="left" keep-together="always" white-space-collapse="false" >Authorised Signatory</fo:block>
									        							</fo:table-cell>
									        							<fo:table-cell >
									        								<fo:block text-align="left" keep-together="always" white-space-collapse="false" >&#160;Goods Recieved in Good Condition</fo:block>
									        								<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
									        								<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
									        								<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
									        								<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
									        								<fo:block text-align="left" keep-together="always" white-space-collapse="false" >&#160;           Signature of the Receiver  [E&amp;OE]</fo:block>
									        							</fo:table-cell>
									        						</fo:table-row>
									        					</fo:table-body>
									        					</fo:table>	
					        								</fo:block>
					        							</fo:table-cell>    								
					        							<fo:table-cell/>
					        							<fo:table-cell/>
					        							<fo:table-cell>					        								
					        								<fo:block text-align="center" ></fo:block>
					        							</fo:table-cell>					        							
					        							<fo:table-cell>					
					        								<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>        								
					        								<fo:block text-align="left" keep-together="always"  white-space-collapse="false" font-weight="bold">&#160;                               RECEIPT</fo:block>
					        								<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
					        								<fo:block keep-together="always" text-align="left" white-space-collapse="false">&#160;       Cheque No.-------------------Date----------</fo:block>
					        								<fo:block text-align="left" keep-together="always"  white-space-collapse="false">&#160;       Cheque drawn on ---------------------------</fo:block>
					        								<fo:block text-align="left" keep-together="always"  white-space-collapse="false">&#160;       Cheque Amount -----------------------------</fo:block>
					        								<fo:block text-align="left" keep-together="always"  white-space-collapse="false">&#160;       (Cheque subject to realisation)</fo:block>
					        								<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>					        								
					        								<fo:block text-align="left" keep-together="always" white-space-collapse="false" >&#160;                        Signature of Vehicle Crew</fo:block>
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
		          	</fo:flow>
		        </fo:page-sequence>
		      	</#list>
		    </#list>
		    <#else>
    	<fo:page-sequence master-reference="main">
	    	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	       		 <fo:block font-size="14pt">
	            	${uiLabelMap.NoOrdersFound}.
	       		 </fo:block>
	    	</fo:flow>
		</fo:page-sequence>	
    </#if>  
       </#list>
      
     </fo:root>
</#escape>