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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-right=".3in" margin-top=".1in">
                <fo:region-body margin-top="0.3in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
         ${setRequestAttribute("OUTPUT_FILENAME", "stockPositionReport.pdf")}
		 
		 
       <#if finalStockPositionMap?has_content>        
		        <fo:page-sequence master-reference="main" font-size="10pt">	
		        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
		        		<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" font-size="10pt" white-space-collapse="false">&#160;${uiLabelMap.CommonPage}- <fo:page-number/> </fo:block>
		        	</fo:static-content>	        	
		        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">
						<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false">    UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
						<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false">&#160;      Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
		        		<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
                        <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
		        		<fo:block  keep-together="always" text-align="center" font-weight="bold"  font-size="14pt" font-family="Courier,monospace" white-space-collapse="false">&#160;      ${reportHeader.description?if_exists}</fo:block>
						<fo:block  keep-together="always" text-align="center" font-weight="bold"  font-size="14pt" font-family="Courier,monospace" white-space-collapse="false">&#160;      ${reportSubHeader.description?if_exists}</fo:block>
                    	<fo:block text-align="center" font-size="14pt" font-weight="bold"  keep-together="always"  white-space-collapse="false">&#160;     STOCK POSITION REPORT ON: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} </fo:block>
              			<fo:block font-size="10pt">================================================================================================================</fo:block>
            			<fo:block text-align="left" font-size="12pt" keep-together="always" font-weight="bold"  font-family="Courier,monospace" white-space-collapse="false">LedgerNo 	Product  		ProductDes 	&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;  Uom&#160;&#160;&#160;&#160;&#160;&#160; Quantity  Received&#160; In-QC</fo:block>	 	 	  
            			<fo:block text-align="left" font-size="12pt" keep-together="always" font-weight="bold"  font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;     		Code		&#160;  &#160;&#160;  &#160;&#160;  &#160;&#160;  &#160;&#160;  &#160;&#160;  &#160;&#160;  &#160;&#160;  &#160;&#160;  &#160;&#160;  &#160;&#160;&#160;&#160;&#160;&#160;    &#160;&#160;&#160;on Hand	&#160;&#160;&#160;&#160;Qty      Qty</fo:block>
            			<fo:block font-size="10pt">================================================================================================================</fo:block>
            	<fo:block>
                 	<fo:table>
                    <fo:table-column column-width="70pt"/>
                    <fo:table-column column-width="80pt"/> 
               	    <fo:table-column column-width="270pt"/>
            		<fo:table-column column-width="75pt"/> 	
            		<fo:table-column column-width="50pt"/>	
            		<fo:table-column column-width="60pt"/>
            		<fo:table-column column-width="60pt"/>
	                    <fo:table-body>
	                    			<#assign serialNo = 1>
	                    			<#assign ledgerGrandTotalPromise = 0>
				                     <#assign ledgerGrandQtyHnd = 0>
                                     <#assign ledgerGrandQtyRecvd =0>
		                    	<#assign stockDetails = finalStockPositionMap.keySet()>
		                    	<#list stockDetails as stockDet>
		                    		<#assign productDet = finalStockPositionMap.get(stockDet)>
		                    		<fo:table-row>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" font-weight="bold" text-align="left" font-size="12pt" white-space-collapse="false">${stockDet?if_exists}</fo:block>  
		                        			</fo:table-cell>
		                        	</fo:table-row>
		                        	<fo:table-row>
		                        			<fo:table-cell>
		                            			<fo:block font-size="10pt">----------------------------------------------------------------------------------------------------------------</fo:block>
		                        			</fo:table-cell>
					                 </fo:table-row>
					                 <#assign ledgerTotalPromise = 0>
				                     <#assign ledgerQtyHnd = 0>
                                     <#assign ledgerQtyRecvd = 0>
		                        	<#list productDet as productDetails>
		                        	<fo:table-row>
		                        			<fo:table-cell>
							            		<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>
							       			</fo:table-cell>
											<fo:table-cell>
							            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false">${productDetails.get("internalName")?if_exists}</fo:block>
							       			</fo:table-cell>
							       			<fo:table-cell>
							            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false">${productDetails.get("productName")?if_exists}</fo:block>
							       			</fo:table-cell>
							       			<fo:table-cell>
							            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false">${productDetails.get("uomDescription")?if_exists}</fo:block>
							       			</fo:table-cell>
							       			<#assign ledgerTotalPromise = ledgerTotalPromise + productDetails.get("quantityOnHandTotal")?if_exists>
							       			<fo:table-cell>
							            		<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${productDetails.get("quantityOnHandTotal")?if_exists?string("#0.000")}</fo:block>
							       			</fo:table-cell>
							       			<#assign ledgerQtyRecvd = ledgerQtyRecvd + productDetails.get("receivedQty")?if_exists>
											<fo:table-cell>
							            		<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${productDetails.get("receivedQty")?if_exists?string("#0.000")}</fo:block>
							       			</fo:table-cell>
							       			<#assign ledgerQtyHnd = ledgerQtyHnd + productDetails.get("qcQuantity")?if_exists>
							       			<fo:table-cell>
							            		<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${productDetails.get("qcQuantity")?if_exists?string("#0.000")}</fo:block>
							       			</fo:table-cell>
						  			</fo:table-row>
		                        	</#list>
		                        	<fo:table-row>
		                        			<fo:table-cell>
		                            			<fo:block font-size="10pt">----------------------------------------------------------------------------------------------------------------</fo:block>
		                        			</fo:table-cell>
					                 </fo:table-row>
		                        	<fo:table-row>
		                        			<fo:table-cell>
							            		<fo:block  keep-together="always" font-weight="bold" text-align="left" font-size="12pt" white-space-collapse="false">Ledger Folio Total</fo:block>
							       			</fo:table-cell>
											<fo:table-cell>
							            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>
							       			</fo:table-cell>
							       			<fo:table-cell>
							            		<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>
							       			</fo:table-cell>
							       			<fo:table-cell>
							            		<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>
							       			</fo:table-cell>
							       			<#assign ledgerGrandTotalPromise = ledgerGrandTotalPromise+ledgerTotalPromise>
							       			<fo:table-cell>
							            		<fo:block  keep-together="always" font-weight="bold" text-align="right" font-size="12pt" white-space-collapse="false">${ledgerTotalPromise?if_exists?string("#0.000")}</fo:block>
							       			</fo:table-cell>
                                            <#assign ledgerGrandQtyRecvd = ledgerGrandQtyRecvd + ledgerQtyRecvd>
                                             <fo:table-cell>
							            		<fo:block  keep-together="always" font-weight="bold" text-align="right" font-size="12pt" white-space-collapse="false">${ledgerQtyRecvd?if_exists?string("#0.000")}</fo:block>
							       			</fo:table-cell>
							       			<#assign ledgerGrandQtyHnd = ledgerGrandQtyHnd+ ledgerQtyHnd>
							       			<fo:table-cell>
							            		<fo:block  keep-together="always" font-weight="bold" text-align="right" font-size="12pt" white-space-collapse="false">${ledgerQtyHnd?if_exists?string("#0.000")}</fo:block>
							       			</fo:table-cell>
						  			</fo:table-row>
						  			<fo:table-row>
		                        			<fo:table-cell>
		                            			<fo:block font-size="10pt">----------------------------------------------------------------------------------------------------------------</fo:block>
		                        			</fo:table-cell>
					                 </fo:table-row>
	                    			</#list>
	                                <#if otherStockPositionMap?has_content>
									<#assign others=otherStockPositionMap.entrySet()>
                                    <#list others as otherValues> 
                                    <#assign ledgerTotalPromise = 0>
				                     <#assign ledgerQtyHnd = 0>
                                     <#assign ledgerQtyRecvd = 0>
                                     <fo:table-row>
		                        			<fo:table-cell>
		                            			<fo:block  keep-together="always" font-weight="bold" text-align="left" font-size="12pt" white-space-collapse="false">${otherValues.getKey()?if_exists}</fo:block>  
		                        			</fo:table-cell>
		                        	</fo:table-row>
		                        	<fo:table-row>
		                        			<fo:table-cell>
		                            			<fo:block font-size="10pt">----------------------------------------------------------------------------------------------------------------</fo:block>
		                        			</fo:table-cell>
					                 </fo:table-row>
					                 <#assign otherDetails=otherValues.getValue()>
                                     <#list otherDetails as details>
    								<fo:table-row>
		                        			<fo:table-cell>
							            		<fo:block  keep-together="always" font-weight="bold" text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>
							       			</fo:table-cell>
											<fo:table-cell>
							            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false">${details.get("internalName")?if_exists}</fo:block>
							       			</fo:table-cell>
							       			<fo:table-cell>
							            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false">${details.get("productName")?if_exists}</fo:block>
							       			</fo:table-cell>
							       			<fo:table-cell>
							            		<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${details.get("uomDescription")?if_exists}</fo:block>
							       			</fo:table-cell>
							       			<#assign ledgerTotalPromise = ledgerTotalPromise+details.get("quantityOnHandTotal")>
							       			<fo:table-cell>
							            		<fo:block  keep-together="always"  text-align="right" font-size="12pt" white-space-collapse="false">${details.get("quantityOnHandTotal")?if_exists?string("##0.000")}</fo:block>
							       			</fo:table-cell>
							       			<#assign ledgerQtyHnd = ledgerQtyHnd+details.get("qcQuantity")>
							       			<fo:table-cell>
							            		<fo:block  keep-together="always"   text-align="right" font-size="12pt" white-space-collapse="false">${details.get("receivedQty")?if_exists?string("##0.000")}</fo:block>
							       			</fo:table-cell>
                                             <#assign ledgerQtyRecvd = ledgerQtyRecvd+details.get("receivedQty")>
							       			<fo:table-cell>
							            		<fo:block  keep-together="always"   text-align="right" font-size="12pt" white-space-collapse="false">${details.get("qcQuantity")?if_exists?string("##0.000")}</fo:block>
							       			</fo:table-cell>
						  			</fo:table-row>
						  			</#list>
						  			<fo:table-row>
		                        			<fo:table-cell>
		                            			<fo:block font-size="10pt">----------------------------------------------------------------------------------------------------------------</fo:block>
		                        			</fo:table-cell>
					                 </fo:table-row>
		                        	<fo:table-row>
		                        			<fo:table-cell>
							            		<fo:block  keep-together="always" font-weight="bold" text-align="left" font-size="12pt" white-space-collapse="false">Ledger Folio Total</fo:block>
							       			</fo:table-cell>
											<fo:table-cell>
							            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>
							       			</fo:table-cell>
							       			<fo:table-cell>
							            		<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>
							       			</fo:table-cell>
							       			<fo:table-cell>
							            		<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>
							       			</fo:table-cell>
							       			<#assign ledgerGrandTotalPromise = ledgerGrandTotalPromise+ledgerTotalPromise>
							       			<fo:table-cell>
							            		<fo:block  keep-together="always" font-weight="bold" text-align="right" font-size="12pt" white-space-collapse="false">${ledgerTotalPromise?if_exists?string("#0.000")}</fo:block>
							       			</fo:table-cell>
                                            <#assign ledgerGrandQtyRecvd = ledgerGrandQtyRecvd + ledgerQtyRecvd>
                                             <fo:table-cell>
							            		<fo:block  keep-together="always" font-weight="bold" text-align="right" font-size="12pt" white-space-collapse="false">${ledgerQtyRecvd?if_exists?string("#0.000")}</fo:block>
							       			</fo:table-cell>
							       			<#assign ledgerGrandQtyHnd = ledgerGrandQtyHnd+ ledgerQtyHnd>
							       			<fo:table-cell>
							            		<fo:block  keep-together="always" font-weight="bold" text-align="right" font-size="12pt" white-space-collapse="false">${ledgerQtyHnd?if_exists?string("#0.000")}</fo:block>
							       			</fo:table-cell>
						  			</fo:table-row>
						  			<fo:table-row>
		                        			<fo:table-cell>
		                            			<fo:block font-size="10pt">----------------------------------------------------------------------------------------------------------------</fo:block>
		                        			</fo:table-cell>
					                 </fo:table-row>
                                    </#list>
						  			</#if>
	                    			<fo:table-row>
		                        			<fo:table-cell>
							            		<fo:block  keep-together="always" font-weight="bold" text-align="left" font-size="12pt" white-space-collapse="false">Grand Total</fo:block>
							       			</fo:table-cell>
											<fo:table-cell>
							            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>
							       			</fo:table-cell>
							       			<fo:table-cell>
							            		<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>
							       			</fo:table-cell>
							       			<fo:table-cell>
							            		<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>
							       			</fo:table-cell>
							       			<fo:table-cell>
							            		<fo:block  keep-together="always" font-weight="bold" text-align="right" font-size="12pt" white-space-collapse="false">${ledgerGrandTotalPromise?if_exists?string("#0.000")}</fo:block>
							       			</fo:table-cell>
							       			<fo:table-cell>
							            		<fo:block  keep-together="always"  font-weight="bold" text-align="right" font-size="12pt" white-space-collapse="false">${ledgerGrandQtyRecvd?if_exists?string("#0.000")}</fo:block>
							       			</fo:table-cell>
							       			<fo:table-cell>
							            		<fo:block  keep-together="always"  font-weight="bold" text-align="right" font-size="12pt" white-space-collapse="false">${ledgerGrandQtyHnd?if_exists?string("#0.000")}</fo:block>
							       			</fo:table-cell>
						  			</fo:table-row>
		                        		<fo:table-row>
											<fo:table-cell>
							            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
							       			</fo:table-cell>
						  				</fo:table-row>
						  				<fo:table-row>
											<fo:table-cell>
							            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
							       			</fo:table-cell>
						  				</fo:table-row>
						  				<fo:table-row>
											<fo:table-cell>
							            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
							       			</fo:table-cell>
						  				</fo:table-row>
						  				<fo:table-row>
											<fo:table-cell>
							            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
							       			</fo:table-cell>
						  				</fo:table-row>
		                        		<fo:table-row>
							       			<fo:table-cell>
							            		<fo:block  keep-together="always" font-size="11pt" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Authorised Signatory</fo:block>  
							       			</fo:table-cell>
										</fo:table-row>
	                    </fo:table-body>
                	</fo:table>
               </fo:block> 		
			</fo:flow>
		</fo:page-sequence>
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