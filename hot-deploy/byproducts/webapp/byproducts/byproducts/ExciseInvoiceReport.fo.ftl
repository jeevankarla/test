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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="10in" margin-top="0.5in" margin-bottom="0.5in" margin-left=".3in" margin-right="1in">
        <fo:region-body margin-top="1.6in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "exciseInvoice.txt")}
<#assign lineNumber = 5>
<#assign numberOfLines = 60>
<#assign facilityNumberInPage = 0>
<fo:page-sequence master-reference="main" force-page-count="no-force">					
			<fo:static-content flow-name="xsl-region-before"> <#assign lineNumber = 5> 
				<#assign facilityNumberInPage = 0>
					<fo:block text-align="left" font-size="7pt" font-family="Courier,monospace" white-space-collapse="false">&#160;                ${uiLabelMap.aavinDairyMsg}</fo:block>				
              		<fo:block text-align="left" keep-together="always" font-size="7pt" font-family="Courier,monospace" white-space-collapse="false">&#160;                PRODUCT DAIRY-29and30, SIDCO INDL. ESTATE, AMBATTUR, CHENNAI-98</fo:block>
           			<fo:block text-align="left" keep-together="always" font-size="7pt"  font-family="Courier,monospace" white-space-collapse="false">TIN  :33761080302 DT. 01.01.2007                       PHONE :23464541,23464542</fo:block>
					<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
           			<fo:block text-align="left" keep-together="always" font-size="7pt" font-family="Courier,monospace" white-space-collapse="false">CST NO  : 50205 DT. 01.02.81   EXCISE INVOICE</fo:block>
           			<fo:block text-align="left" keep-together="always" font-size="7pt" font-family="Courier,monospace" white-space-collapse="false">AREA CODE : 055                       SL.NO : <fo:page-number/> </fo:block>
           			<fo:block font-size="7pt">-----------------------------------------------------------------------------------------</fo:block>
            		<fo:block text-align="left" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">RANGE       :IV                   DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(estimatedDeliveryDate, "dd/MM/yyyy")}</fo:block>
            		<fo:block text-align="left" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">DIVISION IV :T.N.S.C.BOARD,J.J.   Time of issue of invoice:</fo:block>
            		<fo:block text-align="left" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">COMPLEX, CHENNAI-40.             Time of removal of goods:</fo:block>
            		<fo:block text-align="left" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">PLA NO.  : 4/90                        Vehicle no.: </fo:block>
            		<fo:block text-align="left" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">ECC NO.  :AAAAT 0239 MXM 00II dt.19.02.03   </fo:block>
            		<fo:block text-align="left" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">TARIFF HEADING NO : 2105/2108/1901 S.no.and date of debit entry</fo:block>
            		<fo:block text-align="left" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">NO. AND DATE OF NOTIFICATION:   in PLA/RG 23A part II  :</fo:block>  
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">		
               		<fo:block font-size="7pt">	
               			<#assign routeWiseExciseDutyMapEntry = (routeMap).entrySet()>
               			
               			<#list routeWiseExciseDutyMapEntry as routeWiseExciseDuty>
               				<#assign routeId = routeWiseExciseDuty.getKey()>
               				<#assign categoryWiseMap = routeWiseExciseDuty.getValue()>
               				<!--<fo:block font-size="7pt">Route no.    :  ${routeId}</fo:block> -->
               				
               				<#assign categoryWiseMapEntry = (categoryWiseMap).entrySet()>
               					
               					<#list categoryWiseMapEntry as categoryWiseProduct>
               						
               						<#assign totalQtyInc = 0>
               						<#assign categoryTotalValue = 0>
               						<#assign exciseDutyTotal = 0>
               						<#assign serialNo = 1>
               						<#assign edCess = 0>
               						<#assign higherSecCess = 0>
               						
               				    	<fo:block font-size="7pt">-----------------------------------------------------------------------------------------</fo:block>
               						<fo:block font-size="7pt">NAME OF THE CONSIGNEE :  ${categoryWiseProduct.getKey()}                                         ROUTE NO: ${routeId}</fo:block> 
               						<fo:block font-size="8pt">-----------------------------------------------------------------------------------------</fo:block>
               						<fo:block font-size="8pt" white-space-collapse="false">S.NO  PCD   PRODUCT NAME    QUANTITY     QUANTITY      PRICE     TOTAL     EXD   EXCISE</fo:block>
               						<fo:block font-size="8pt" white-space-collapse="false">&#160;                              NOS          LTRS      PER UNIT   VALUE      %     DUTY</fo:block> 
               						<fo:block font-size="8pt">-----------------------------------------------------------------------------------------</fo:block>
               						<fo:table  table-layout="fixed" font-family="Courier,monospace" font-size="8pt" >                
				               				<fo:table-column column-width="22pt"/>
				                			<fo:table-column column-width="37pt"/>
				               				<fo:table-column column-width="75pt"/>
				              				<fo:table-column column-width="37pt"/>
				             				<fo:table-column column-width="60pt"/>
				                			<fo:table-column column-width="65pt"/>
				                			<fo:table-column column-width="50pt"/>
				               				<fo:table-column column-width="27pt"/>
				                			<fo:table-column column-width="41pt"/>
				                					<fo:table-body>
               											<#assign categoryWiseProductDetail = (categoryWiseProduct.getValue()).entrySet()>
               											<#list categoryWiseProductDetail as productWiseValues>
               												<#assign prodDetail = productWiseValues.getValue()>
	             						        				<fo:table-row >                    
						                            				<fo:table-cell>
						                                				<fo:block text-align="left">${serialNo}</fo:block>
						                            				</fo:table-cell>
						                            				<fo:table-cell>
						                                				<fo:block text-align="left">${prodDetail.get("productId")}</fo:block>
						                            				</fo:table-cell>
						                            				<fo:table-cell>
						                                				<fo:block text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(prodDetail.get("productName"))),17)}</fo:block>
						                            				</fo:table-cell>
						                            				<fo:table-cell>
						                                				<fo:block text-align="right">${prodDetail.get("quantity")}</fo:block>
						                            				</fo:table-cell>
						                            				<#assign incQty = prodDetail.get("quantity")*prodDetail.get("qtyInc")>
						                            				<fo:table-cell>
						                                				<fo:block text-align="right">${incQty?string("#0.000")}</fo:block>
						                            				</fo:table-cell>
						                            				<#assign totalQtyInc = totalQtyInc+incQty>
						                            				<fo:table-cell>
						                                				<fo:block text-align="right">${prodDetail.get("unitPrice")?string("#0.00")}</fo:block>
						                            				</fo:table-cell>
						                            				<#assign totalValue = prodDetail.get("quantity")*prodDetail.get("unitPrice")>
						                            				<#assign categoryTotalValue = categoryTotalValue + totalValue>
						                            				<fo:table-cell>
						                                				<fo:block text-align="right">${totalValue?string("#0.00")}</fo:block>
						                            				</fo:table-cell>
						                            				<#assign bedPercent = 2>
						                            				<#if prodDetail.get("BED_PERCENT")?has_content><#assign bedPercent = prodDetail.get("BED_PERCENT")?if_exists></#if>
						                            				
						                            				<#assign exciseDuty = totalValue*bedPercent/100>
						                            				<fo:table-cell>
						                                				<fo:block text-align="right">${prodDetail.get("BED_PERCENT")?if_exists}%</fo:block>
						                            				</fo:table-cell>
						                            				<fo:table-cell>
						                                				<fo:block text-align="right">${exciseDuty?string("#0.00")}</fo:block>
						                            				</fo:table-cell>
						                            				<#assign exciseDutyTotal = exciseDuty + exciseDutyTotal>
						                            				<#assign edCess = edCess + prodDetail.get("BEDCESS_SALE")>
						                            				<#assign higherSecCess = higherSecCess + prodDetail.get("BEDSECCESS_SALE")>
	             						        				</fo:table-row>
	             						        				<#assign serialNo = serialNo + 1>
               											</#list>
               											<fo:table-row >                    
						                            		<fo:table-cell>
						                                		<fo:block font-size="7pt">-----------------------------------------------------------------------------------------</fo:block>
						                                	</fo:table-cell>	
	             						           		</fo:table-row>	
	             						           		<fo:table-row >                    
						                            		<fo:table-cell>
						                                		<fo:block font-size="7pt"></fo:block>
						                                	</fo:table-cell>	
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt"></fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt"></fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt" text-align="left" keep-together="always"> TOTAL :</fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt" text-align="left" keep-together="always">&#160;   ${totalQtyInc?string("#0.000")}</fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt" text-align="right" keep-together="always">${categoryTotalValue?string("#0.00")}</fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt" text-align="right" keep-together="always">${exciseDutyTotal?string("#0.00")}</fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt" text-align="left" keep-together="always"></fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt" text-align="right" keep-together="always"></fo:block>
						                                	</fo:table-cell>
	             						           		</fo:table-row>	
	             						           		<fo:table-row >                    
						                            		<fo:table-cell>
						                                		<fo:block font-size="7pt"></fo:block>
						                                	</fo:table-cell>	
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt"></fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt"></fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt" text-align="left" keep-together="always" white-space-collapse="false"> EXCISE DUTY PAID</fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt" text-align="center" keep-together="always"></fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt" text-align="left" keep-together="always">&#160;   :</fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt" text-align="right" keep-together="always" white-space-collapse="false">${exciseDutyTotal?string("#0.00")}</fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt" text-align="left" keep-together="always"></fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt" text-align="right" keep-together="always"></fo:block>
						                                	</fo:table-cell>
	             						           		</fo:table-row>
	             						           		<fo:table-row >                    
						                            		<fo:table-cell>
						                                		<fo:block font-size="7pt"></fo:block>
						                                	</fo:table-cell>	
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt"></fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt"></fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt" text-align="left" keep-together="always"> EDUCATION CESS 2%</fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt" text-align="right" keep-together="always"></fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt" text-align="left" keep-together="always">&#160;   :</fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt" text-align="right" keep-together="always">${edCess?if_exists}</fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt" text-align="left" keep-together="always"></fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt" text-align="right" keep-together="always"></fo:block>
						                                	</fo:table-cell>
	             						           		</fo:table-row>
	             						           		<fo:table-row >                    
						                            		<fo:table-cell>
						                                		<fo:block font-size="7pt"></fo:block>
						                                	</fo:table-cell>	
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt"></fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt"></fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt" text-align="left" keep-together="always">HIGHER SECONDARY CESS 1%</fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt"  text-align="right" keep-together="always"></fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt" text-align="left" keep-together="always">&#160;   :</fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt" text-align="right" keep-together="always">${higherSecCess?string("#0.00")}</fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt" text-align="left" keep-together="always"></fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt" text-align="right" keep-together="always"></fo:block>
						                                	</fo:table-cell>
	             						           		</fo:table-row>
	             						           		<fo:table-row >                    
						                            		<fo:table-cell>
						                                		<fo:block font-size="7pt"></fo:block>
						                                	</fo:table-cell>	
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt"></fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt"></fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt" text-align="left" keep-together="always">GRAND TOTAL</fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt" text-align="right" keep-together="always" white-space-collapse="false">&#160;                :</fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell> 
						                                		<fo:block font-size="7pt" text-align="left" keep-together="always" white-space-collapse="false">&#160;       ${(categoryTotalValue + exciseDutyTotal + edCess + higherSecCess)?string("#0.00")}</fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt" text-align="left" keep-together="always"></fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt" text-align="right" keep-together="always"></fo:block>
						                                	</fo:table-cell>
	             						           		</fo:table-row>
	             						           		<fo:table-row >                    
						                            		<fo:table-cell>
						                                		<fo:block font-size="7pt">-----------------------------------------------------------------------------------------</fo:block>
						                                	</fo:table-cell>
	             						           		</fo:table-row>	
	             						           		<fo:table-row >                    
						                            		<fo:table-cell>
						                                		<fo:block font-size="7pt" keep-together="always" white-space-collapse="false">Certified that the particulars given are true and correct and the amount</fo:block>
						                                		<fo:block font-size="7pt" keep-together="always" white-space-collapse="false">Indicated represents the price actually charged and there is no flow of</fo:block>
																<fo:block font-size="7pt" keep-together="always" white-space-collapse="false">additional consideration directly or indirectly from the buyer.</fo:block>
																<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
																<fo:block font-size="7pt" keep-together="always" white-space-collapse="false">&#160;                         For TAMIL NADU CO-OP MILK PRODUCERS' FEDN. LTD,</fo:block>
																<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
																<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
																<fo:block font-size="7pt" keep-together="always" white-space-collapse="false">&#160;                                               for DY.GENERAL MANAGER (P &amp; M)</fo:block>
						                                	</fo:table-cell>	
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt"></fo:block>
						                                	</fo:table-cell>
						                                	<fo:table-cell>
						                                		<fo:block font-size="7pt"></fo:block>
						                                	</fo:table-cell>
	             						           		</fo:table-row>
	             						           </fo:table-body>
				                         	</fo:table> 
               								<fo:block font-size="7pt" break-after="page"></fo:block>
               					</#list>
               			
               			</#list> 
               	</fo:block>		
			 </fo:flow>
			 </fo:page-sequence>	
</fo:root>
</#escape>