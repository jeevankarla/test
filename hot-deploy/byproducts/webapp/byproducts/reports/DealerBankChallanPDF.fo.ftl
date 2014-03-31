<#-- Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed with this work for additional information
regarding copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under
the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing
permissions and limitations under the License. --> 
<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

<fo:layout-master-set>
	<fo:simple-page-master master-name="main" page-height="12in" page-width="12in"
            margin-top="0.3in" margin-bottom=".3in" margin-left=".3in" margin-right=".3in">
        <fo:region-body margin-top=".5in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set> 
<#-- 
	<fo:layout-master-set>
		<fo:simple-page-master master-name="main" page-height="12in" page-width="12in" margin-left=".5in" margin-right=".5in">
			<fo:region-body margin-top=".35in" />
			<fo:region-before extent="1in" />
			<fo:region-after extent="1in" />
		</fo:simple-page-master>
	</fo:layout-master-set>  -->
         <#assign partyIdentification = delegator.findOne("PartyIdentification", {"partyId" :"Company","partyIdentificationTypeId":"PAN_NUMBER"}, true)>
         <#assign panIdDetails=partyIdentification?if_exists>
        <#if routeWiseMap?has_content> 
        <#assign routeWiseList= routeWiseMap.entrySet()>
        <#list routeWiseList as routeBoothsMap>
		 <fo:page-sequence master-reference="main">
		<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
		<fo:block text-align="left" keep-together="always" white-space-collapse="false">VST_ASCII-015 </fo:block>
			 <#assign routeId = routeBoothsMap.getKey()>
		        <#assign  boothWiseSaleList=routeBoothsMap.getValue().entrySet()>
		          <#assign noOfbooths=0>
        	     <#list   boothWiseSaleList as boothSaleMap>
		    		<#if (noOfbooths==2) >
		    			<fo:block  break-after="page"></fo:block>
		    			<#assign noOfbooths=0>
		    		</#if>
		    		 <#assign noOfbooths=noOfbooths+1>	
        	          <#assign facilityId = boothSaleMap.getKey()>
        	           <#assign accNumber =  facilityBankMap.get(facilityId)?if_exists>
        	         
        	          <#assign facility = delegator.findOne("Facility", {"facilityId" :facilityId}, true)>
        	           <#assign boothSaleDetails = boothSaleMap.getValue()>
        	                    
        	           <#assign amSaleDetails =boothSaleDetails.get("AM")>
        	           <#assign pmSaleDetails = boothSaleDetails.get("PM")>
        	            <#assign totalValue= amSaleDetails.get("saleVal")+pmSaleDetails.get("saleVal")>
					<fo:block>
					<#--
					<#if (noOfbooths==1) >
					  <fo:block> ${uiLabelMap.CommonPage} <fo:page-number/></fo:block>
					  </#if> -->
					<fo:table table-layout="fixed" width="100%" space-before="0.2in">
						<fo:table-column column-width="40%" />
						<fo:table-column column-width="5%" />
						<fo:table-column column-width="57%" />
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell >
									<fo:block>
										<fo:table table-layout="fixed" width="100%" space-before="0.2in">
											<fo:table-column column-width="100%" />
											<fo:table-body>
												<fo:table-row>
													<fo:table-cell>
														<fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">RETAILER COPY</fo:block>
														<fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-weight="bold" font-size="10pt" keep-together="always"> MOTHER DAIRY, KMF UNIT	</fo:block>
														<fo:block text-align="center" font-weight="bold" font-size="10pt" white-space-collapse="false" keep-together="always">BANGALORE - 560065.</fo:block>
														<fo:block font-size="10pt">
															<fo:table table-layout="fixed" width="100%" space-before="0.2in">
																<fo:table-column column-width="50%" />
																<fo:table-column column-width="50%" />
																<fo:table-body>
																	<fo:table-row>
																		<fo:table-cell>
																			<fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace" font-size="8pt" keep-together="always"> CASH PAYMENT	</fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace" font-size="8pt" keep-together="always">&#160;          PAN:${panIdDetails.idValue?if_exists}</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
																	<fo:table-row>
																		<fo:table-cell number-columns-spanned="2">
																			<fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="8pt" keep-together="always"> PayableDate: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayBegin, "dd-MMM-yyyy")}</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
																	<fo:table-row>
																		<fo:table-cell>
																			<fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace" font-size="8pt" keep-together="always">  A/C NO :${accNumber?if_exists}</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
																	<fo:table-row>
																		<fo:table-cell>
																			<fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace" font-size="8pt" keep-together="always">  RETAILER NAME:${facility.facilityName}</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
																	<fo:table-row>
																		<fo:table-cell>
																			<fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace" font-size="8pt" keep-together="always">  RETAILER CODE:${facilityId}</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
																</fo:table-body>
															</fo:table>
														</fo:block>
													</fo:table-cell>
												</fo:table-row>

												<fo:table-row width="100%">
													<fo:table-cell>
														<fo:block font-size="10pt">
															<fo:table table-layout="fixed" width="100%" space-before="0.2in">
																<fo:table-column column-width="170pt" />
																<fo:table-column column-width="80pt" />
																<fo:table-column column-width="60pt" />
																<fo:table-body>
																 <fo:table-row>
																		<fo:table-cell  number-columns-spanned="3">
																			<fo:block text-align="left" white-space-collapse="false" font-size="8pt" keep-together="always">__ __ __ __ __ __ __  __ __ __ __ __ __ __ __ __ __ __ __ __ __</fo:block>
																		</fo:table-cell>
															      </fo:table-row>
																  <fo:table-row >
																		<fo:table-cell>
																			<fo:block font-size="8pt" white-space-collapse="false">PARTICULARS</fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="left" font-size="8pt" white-space-collapse="false" keep-together="always">DATE</fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="right" font-size="8pt" white-space-collapse="false" keep-together="always">AMOUNT</fo:block>
																		</fo:table-cell>
																 </fo:table-row>
																  <fo:table-row>
																		<fo:table-cell  number-columns-spanned="3">
																			<fo:block text-align="left" white-space-collapse="false" font-size="8pt" keep-together="always">__ __ __ __ __ __ __  __ __ __ __ __ __ __ __ __ __ __ __ __ __</fo:block>
																		</fo:table-cell>
															      </fo:table-row>
															     
					        						 <#assign boothAmPmList = boothSaleDetails.entrySet()>
					        						
										        		 <#list boothAmPmList as boothAmPmMap>
										        		 <#assign innerMapDetails=boothAmPmMap.getValue()>
					        							<fo:table-row>
																		<fo:table-cell>
																			<fo:block text-align="left" font-size="8pt"><#if boothAmPmMap.getKey()=="AM">NET VALUE OF MILK &amp; MILK<#else>PRODUCTS SOLD ON.....</#if></fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																		   <fo:block text-align="left" font-size="8pt">${innerMapDetails.get("date")}</fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="right" font-size="8pt">${innerMapDetails.get("saleVal")?string("#0.00")}</fo:block>
																		</fo:table-cell>
														</fo:table-row>
					        							</#list>
					        							      <fo:table-row>
																		<fo:table-cell  number-columns-spanned="3">
																			<fo:block text-align="left" white-space-collapse="false" font-size="8pt" keep-together="always">__ __ __ __ __ __ __  __ __ __ __ __ __ __ __ __ __ __ __ __ __</fo:block>
																		</fo:table-cell>
															   </fo:table-row>
					        							      <fo:table-row >
																		<fo:table-cell>
																			<fo:block text-align="center" font-size="10pt">Total</fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="center" font-size="8pt">&#160;</fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="right" font-size="8pt">${totalValue?string("#0.00")}</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
																	 <fo:table-row>
																		<fo:table-cell  number-columns-spanned="3">
																			<fo:block text-align="left" white-space-collapse="false" font-size="8pt" keep-together="always">__ __ __ __ __ __ __  __ __ __ __ __ __ __ __ __ __ __ __ __ __</fo:block>
																		</fo:table-cell>
															      </fo:table-row>
																	<fo:table-row>
																		<fo:table-cell number-columns-spanned="3">
																			<fo:block text-align="left" wrap-option="wrap" font-size="10pt">TOTAL AMT TO BE PAID:  ${Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble(totalValue?string("#0")), "%rupees-and-paise", locale).toUpperCase()} ONLY</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
																	 <fo:table-row>
																		<fo:table-cell>
																			<fo:block text-align="center" font-size="10pt">&#160;</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
																	<fo:table-row>
																		<fo:table-cell>
																			<fo:block text-align="center" font-size="10pt"></fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="center" font-size="10pt">Cashier</fo:block>
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
								</fo:table-cell>
					        	<#-- end of  RetailerCopy-->
					        					
					        	<fo:table-cell >
									<fo:block text-align="center" font-size="10pt"></fo:block><#-- line ss-->
									<fo:block text-align="center" font-size="10pt">|</fo:block><#-- line ss-->
									<fo:block text-align="center" font-size="10pt">|</fo:block><#-- line ss-->
									<fo:block text-align="center" font-size="10pt">|</fo:block><#-- line ss-->
									<fo:block text-align="center" font-size="10pt">|</fo:block><#-- line ss-->
									<fo:block text-align="center" font-size="10pt">|</fo:block><#-- line ss-->
									<fo:block text-align="center" font-size="10pt">|</fo:block><#-- line ss-->
									<fo:block text-align="center" font-size="10pt">|</fo:block><#-- line ss-->
									<fo:block text-align="center" font-size="10pt">|</fo:block><#-- line ss-->
									<fo:block text-align="center" font-size="10pt">|</fo:block><#-- line ss-->
									<fo:block text-align="center" font-size="10pt">|</fo:block><#-- line ss-->
									<fo:block text-align="center" font-size="10pt">|</fo:block><#-- line ss-->
									<fo:block text-align="center" font-size="10pt">|</fo:block><#-- line ss-->
									<fo:block text-align="center" font-size="10pt">|</fo:block><#-- line ss-->
									<fo:block text-align="center" font-size="10pt">|</fo:block><#-- line ss-->
									<fo:block text-align="center" font-size="10pt">|</fo:block><#-- line ss-->
									<fo:block text-align="center" font-size="10pt">|</fo:block><#-- line ss-->
									<fo:block text-align="center" font-size="10pt">|</fo:block><#-- line ss-->
									<fo:block text-align="center" font-size="10pt">|</fo:block><#-- line ss-->
									<fo:block text-align="center" font-size="10pt">|</fo:block><#-- line ss-->
									<fo:block text-align="center" font-size="10pt">|</fo:block><#-- line ss-->
									
									
									
					        	</fo:table-cell>	   
					        	<fo:table-cell > <#--BankCopy started-->
									<fo:block>
									<fo:block text-align="left" >&#160;&#160;</fo:block>
										<fo:table>
											<fo:table-column column-width="320pt" />
											<fo:table-column column-width="110pt" />
											<fo:table-body>

												<fo:table-row>
													<fo:table-cell>
														<fo:block font-size="10pt">
									                     <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace" font-weight="bold" font-size="8pt" keep-together="always">&#160; &#160; &#160; &#160;MOTHER DAIRY,KMF UNIT,BANGALORE-560065.      BANK COPY</fo:block>
															<fo:table>
																<fo:table-column column-width="170pt" />
																<fo:table-column column-width="170pt" />
																<fo:table-body>
																	<fo:table-row>
																		<fo:table-cell number-columns-spanned="2">
																			<fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-weight="bold" font-size="11pt" keep-together="always"> CASH PAYMENT CHALLAN	</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
																	<fo:table-row>
																		<fo:table-cell>
																			<fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace" font-size="8pt" keep-together="always">&#160;</fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace" font-size="8pt" keep-together="always"> PayableDate: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayBegin, "dd-MMM-yyyy")}</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
																	<fo:table-row>
																		<fo:table-cell>
																			<fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace" font-size="8pt" keep-together="always"> A/C NO :${accNumber?if_exists}</fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace" font-size="8pt" keep-together="always">&#160;        PAN:${panIdDetails.idValue?if_exists}</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
																	<fo:table-row>
																		<fo:table-cell>
																			<fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace" font-size="8pt" keep-together="always">SERIAL NO:</fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace" font-size="8pt" keep-together="always">&#160;</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
																	<fo:table-row>
																		<fo:table-cell number-columns-spanned="2">
																			<fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace" font-size="8pt" font-weight="bold"  keep-together="always">--- --- --- --- --- --- --- --- --- --- --- --- --- --- --- </fo:block>
																		</fo:table-cell>
																	</fo:table-row>
																	<fo:table-row>
																		<fo:table-cell>
																			<fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always"> RETAILER NAME:${facility.facilityName}</fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																				<fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">&#160;</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
																	<fo:table-row>
																		<fo:table-cell>
																			<fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always"> RETAILER CODE:${facilityId}</fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">&#160;</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
																	<fo:table-row>
																		<fo:table-cell>
																			<fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always"> ROUTE NO:${routeId}</fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always">&#160;</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
																</fo:table-body>
															</fo:table>
														</fo:block>
													</fo:table-cell>
												</fo:table-row>

												<fo:table-row >
													<fo:table-cell>
														<fo:block font-size="10pt">
															<fo:table>
																<fo:table-column column-width="150pt" />
																<fo:table-column column-width="65pt" />
																<fo:table-column column-width="40pt" />
																<fo:table-column column-width="65pt" />
																<fo:table-body>
																 <fo:table-row>
																		<fo:table-cell  number-columns-spanned="3">
																			<fo:block text-align="left" font-size="8pt" white-space-collapse="false" keep-together="always">__ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ </fo:block>
																		</fo:table-cell>
															      </fo:table-row>
																	<fo:table-row >
																		<fo:table-cell>
																			<fo:block font-size="8pt" white-space-collapse="false">  PARTICULARS</fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="left" font-size="8pt" white-space-collapse="false" keep-together="always">DATE</fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="center" font-size="8pt" white-space-collapse="false" keep-together="always">SHIFT</fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="right" font-size="8pt" white-space-collapse="false" keep-together="always"> AMOUNT</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
																	 <fo:table-row>
																		<fo:table-cell  number-columns-spanned="3">
																			<fo:block text-align="left" font-size="8pt" white-space-collapse="false" keep-together="always">__ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ </fo:block>
																		</fo:table-cell>
															      </fo:table-row>
																
					        						 <#assign boothAmPmList = boothSaleDetails.entrySet()>
					        						
										        		 <#list boothAmPmList as boothAmPmMap>
										        		 <#assign innerMapDetails=boothAmPmMap.getValue()>
					        							<fo:table-row>
																		<fo:table-cell>
																			<fo:block text-align="left" font-size="8pt"><#if boothAmPmMap.getKey()=="AM">NET VALUE OF MILK &amp; MILK<#else>PRODUCTS SOLD ON.....</#if></fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="left" font-size="8pt">${innerMapDetails.get("date")}</fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="center" font-size="8pt">${innerMapDetails.get("shift")}</fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="right" font-size="8pt">${innerMapDetails.get("saleVal")?string("#0.00")}</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
					        							</#list>
					        							 <fo:table-row>
																		<fo:table-cell  number-columns-spanned="3">
																			<fo:block text-align="left" font-size="8pt" white-space-collapse="false" keep-together="always">__ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ </fo:block>
																		</fo:table-cell>
														</fo:table-row>
					        							<fo:table-row >
																		<fo:table-cell>
																			<fo:block text-align="center" font-size="8pt">Total</fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="center" font-size="8pt"></fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="center" font-size="8pt"></fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="right" font-size="8pt">${totalValue?string("#0.00")}</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
															 <fo:table-row>
																		<fo:table-cell  number-columns-spanned="3">
																			<fo:block text-align="left" font-size="8pt" white-space-collapse="false" keep-together="always">__ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ </fo:block>
																		</fo:table-cell>
															  </fo:table-row>
																	<fo:table-row>
																		<fo:table-cell number-columns-spanned="4">
																		<fo:block text-align="left" wrap-option="wrap" font-size="8pt"> TOTAL AMT TO BE PAID:  ${Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble(totalValue?string("#0")), "%rupees-and-paise", locale).toUpperCase()} ONLY</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
																	 <fo:table-row>
																		<fo:table-cell>
																			<fo:block text-align="left" white-space-collapse="false" keep-together="always"> &#160;</fo:block>
																		</fo:table-cell>
																   </fo:table-row>
																	<fo:table-row>
																		<fo:table-cell>
																			<fo:block text-align="center" font-size="10pt">Retailer's Signature</fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="center" font-size="10pt">Cashier</fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="center" font-size="10pt"></fo:block>
																		</fo:table-cell>
																		<fo:table-cell>
																			<fo:block text-align="center" font-size="10pt">Manager</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
																</fo:table-body>
															</fo:table>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell> <#-- denaminations cell started -->
        									          <fo:block font-size="10pt">
															<fo:table>
																<fo:table-column column-width="105pt" />
																<fo:table-body>
																	<fo:table-row>
																		<fo:table-cell>
																			<fo:block text-align="left" font-size="8pt">&#160;&#160;   DENOMINATIONS</fo:block>
																			<fo:block text-align="left" font-size="8pt">&#160;&#160;</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
																	<fo:table-row>
																		<fo:table-cell>
																			<fo:block text-align="left" font-size="8pt">&#160;&#160; 1000 X&#160;&#160; = </fo:block>
																		</fo:table-cell>
																	</fo:table-row>
																	<fo:table-row>
																		<fo:table-cell>
																			<fo:block text-align="left" font-size="8pt">&#160;&#160; 500 X&#160;&#160;&#160; =</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
																	<fo:table-row>
																		<fo:table-cell>
																			<fo:block text-align="left" font-size="8pt">&#160;&#160; 100 X&#160;&#160;&#160; =</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
																	<fo:table-row>
																		<fo:table-cell>
																			<fo:block text-align="left" font-size="8pt">&#160;&#160; 50 X&#160;&#160;&#160;&#160; =</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
																	<fo:table-row>
																		<fo:table-cell>
																			<fo:block text-align="left" font-size="8pt">&#160;&#160; 20 X&#160;&#160;&#160;&#160; =</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
																	<fo:table-row>
																		<fo:table-cell>
																			<fo:block text-align="left" font-size="8pt">&#160;&#160; 10 X&#160;&#160;&#160;&#160; =</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
																	<fo:table-row>
																		<fo:table-cell>
																			<fo:block text-align="left" font-size="8pt">&#160;&#160;  5 X&#160;&#160;&#160;&#160;&#160; =</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
																	<fo:table-row>
																		<fo:table-cell>
																			<fo:block text-align="left" font-size="8pt">&#160;&#160;  2   X&#160;&#160;&#160;&#160;&#160; =</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
																	<fo:table-row>
																		<fo:table-cell>
																			<fo:block text-align="left" font-size="8pt">&#160;&#160;  1   X&#160;&#160;&#160;&#160;&#160; =</fo:block>
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
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
							<fo:table-cell number-columns-spanned="3">
								<fo:block text-align="left" font-size="10pt" white-space-collapse="false" font-weight="normal" keep-together="always">__ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __</fo:block>
							</fo:table-cell>
						 </fo:table-row>
						</fo:table-body>
					</fo:table>
				</fo:block>
		        </#list>
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
