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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-right=".3in" margin-top=".5in">
                <fo:region-body margin-top="1.1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "IndentReport.pdf")}
        <#if orderDetailMap?has_content>
        <#assign ordersList = orderDetailMap.entrySet()> 
        <#list  ordersList as eachOrder>
        <#assign orderId = eachOrder.getKey()>
        <#assign orderDetail = eachOrder.getValue()>
        <#assign orderHeader = orderDetail.get('orderHeader')>
        <#assign shipment = orderDetail.get('shipment')>
        <#assign orderItems = orderDetail.get('orderItems')>
        <#assign billingAddress = orderDetail.get('partyAddress')>
        <#assign partyName = orderDetail.get('partyName')>
        <#assign invoice = orderDetail.get('invoice')>
        <#assign partyCode = orderDetail.get('partyCode')>
        <#assign screenFlag = orderDetail.get('screenFlag')>
        <#assign companyDetail = orderDetail.get('companyDetail')>
        <fo:page-sequence master-reference="main" font-size="12pt">	
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        		<fo:block text-align="left" font-size="13pt" keep-together="always"  white-space-collapse="false">
        			<fo:table>
			            <fo:table-column column-width="150pt"/>
			            <fo:table-column column-width="150pt"/>
			            <fo:table-column column-width="150pt"/>
			            <fo:table-column column-width="150pt"/> 
			            <fo:table-body>
			                <fo:table-row>
			                    <fo:table-cell number-columns-spanned="4">
					            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD.</fo:block>
					            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false"> UNIT : MOTHER DAIRY:G.K.V.K POST : YELAHANKA:BANGALORE : 560065</fo:block>
					            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
					            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">${screenFlag?if_exists}  INDENT FOR ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(orderHeader.get('createdStamp'), "dd-MMM-yyyy")?if_exists}</fo:block>
					            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">Date:  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy HH:mm:ss")?if_exists}                CST : ${companyDetail.get('CST_NUMBER')?if_exists}</fo:block>
					            </fo:table-cell>
							</fo:table-row>
			            </fo:table-body>
			        </fo:table>
        		</fo:block>
        	</fo:static-content>	        	
        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
        		<fo:block>
             		<fo:table border-style="solid">
			            <fo:table-column column-width="150pt"/>
			            <fo:table-column column-width="150pt"/>
			            <fo:table-column column-width="150pt"/>
			            <fo:table-column column-width="150pt"/>
			            <fo:table-body>
			                <fo:table-row>
			                    <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">INDENT NO : </fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">INDENT DATE : </fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(orderHeader.estimatedDeliveryDate, "dd-MMM-yyyy")}</fo:block>  
					            </fo:table-cell>
							</fo:table-row>
							<fo:table-row>
			                    <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">PARTY NAME : </fo:block>
					            </fo:table-cell>
					            <fo:table-cell>	
					            	<fo:block text-align="left" font-size="12pt" white-space-collapse="false" wrap-option="wrap">${partyName.get('groupName')?if_exists}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">PRODUCT TYPE :</fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"> ${screenFlag?if_exists}</fo:block>  
					            </fo:table-cell>
							</fo:table-row>
							<fo:table-row>
			                    <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">PO NO : </fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"> ${orderHeader.externalId?if_exists}</fo:block>
					            </fo:table-cell>
					           
							</fo:table-row>
							<fo:table-row>
			                    <fo:table-cell>
					            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					            </fo:table-cell>
							</fo:table-row>
							<fo:table-row border-style="solid" >
			                    <fo:table-cell number-columns-spanned="4">
					            	<fo:block text-align="center" font-size="12pt" white-space-collapse="false">
					            		<fo:table>
								            <fo:table-column column-width="40pt"/>
								            <fo:table-column column-width="200pt"/>
								            <fo:table-column column-width="100pt"/>
								            <fo:table-body>
								                <fo:table-row>
								                    <fo:table-cell >
										            	<fo:block text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">SL NO</fo:block>
										            </fo:table-cell>
													<fo:table-cell>
										            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">PRODUCT</fo:block>
										            </fo:table-cell>
										            <fo:table-cell>
										            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" font-weight="bold">QTY IN <#if screenFlag?exists && (screenFlag == "NANDINI" || screenFlag == "AMUL")>CRATES<#else>LTR/KGS</#if></fo:block>
										            </fo:table-cell>
												</fo:table-row>
												
												<#if orderItems?has_content>
													<#assign totalCrates = 0>
													<#assign slNo = 1>
													<#list orderItems as eachItem>
														<fo:table-row>
										                    <fo:table-cell>
												            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">${slNo?if_exists} </fo:block>
												            </fo:table-cell>
												            <fo:table-cell>	
												            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${eachItem.get('description')?if_exists}</fo:block>
												            </fo:table-cell>
												            <fo:table-cell>
												            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${eachItem.get('qtyInCrate')?if_exists?string("#0.00")}</fo:block>
												            </fo:table-cell>
														</fo:table-row>
														<#assign totalCrates = totalCrates+eachItem.get('qtyInCrate')>
														<#assign slNo = slNo+1>
													</#list>
													<fo:table-row >
									                    <fo:table-cell>	
											            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>
											            </fo:table-cell>
											            <fo:table-cell>
											            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">Total:</fo:block>
											            </fo:table-cell>
											            <fo:table-cell>
											            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${totalCrates?if_exists?string("#0.00")}</fo:block>  
											            </fo:table-cell>
											            
													</fo:table-row>
												</#if>
											</fo:table-body>
											</fo:table>
					            	</fo:block>
					            </fo:table-cell>
							</fo:table-row>
			            </fo:table-body>
			        </fo:table>
          		</fo:block>
          		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
          		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
          		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
          		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
          		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">
					<fo:table>
			            <fo:table-column column-width="300pt"/>
			            <fo:table-column column-width="300pt"/>
			            <fo:table-body>
			                <fo:table-row>
			                    <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">SIGNATURE &amp; OF</fo:block>
					            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">INDENTOR</fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">SIGNATURE &amp; OF</fo:block>
					            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">INCHARGE</fo:block>
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
	            	${uiLabelMap.NoOrdersFound}.
	       		 </fo:block>
	    	</fo:flow>
		</fo:page-sequence>	
    </#if> 
 </fo:root>
</#escape>