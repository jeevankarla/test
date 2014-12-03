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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-left=".3in" margin-right=".3in" margin-top=".5in">
                <fo:region-body margin-top="1.8in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "IceCreamSalesReport.pdf")}
        <#if errorMessage?has_content>
	<fo:page-sequence master-reference="main">
	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	   <fo:block font-size="14pt">
	           ${errorMessage}.
        </fo:block>
	</fo:flow>
	</fo:page-sequence>	
	<#else>
       <#if finalInvoiceDateMap?has_content>
       		<#assign grandTotalBasicRev = 0>
	     	<#assign grandTotalBedRev = 0>
	     	<#assign grandTotalVatRev = 0>
	     	<#assign grandTotalCstRev = 0>
	     	<#assign grandTotalRev = 0>
	     	<#assign grandTotalMrpValue =0>
	     	<#assign grandTotalPPD =0>
	        <fo:page-sequence master-reference="main" font-size="12pt">	
	        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
                	<fo:block text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold">INVOICE NUMBER SALES (ABSTRACT) REGISTER REPORT</fo:block>
          			<fo:block text-align="center" font-weight="bold"  keep-together="always"  white-space-collapse="false"> PPD NANDINI SALES BOOK FOR THE PERIOD - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")} </fo:block>
          			<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
          			<fo:block>-------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            	    <fo:block text-align="left" font-weight="bold" font-size="12pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">Customer      								Invoice  Central Excise 			MRP 							Ex-factory    					ED             VAT(Rs)   		C.S.T(Rs)    PPD(RS)  Total(Rs)   				</fo:block>
        			<fo:block text-align="left" font-weight="bold" font-size="12pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;  								 Number			Inv.Number								Value(Rs)			Value(Rs)    				Value(Rs)    	</fo:block>
	        		<fo:block>-------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            	</fo:static-content>	        	
	        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
        			<fo:block>
        				<fo:table>
		                    <fo:table-column column-width="15pt"/>
		                    <fo:table-column column-width="100pt"/>
		                    <fo:table-column column-width="155pt"/> 
		               	    <fo:table-column column-width="145pt"/>
		            		<fo:table-column column-width="95pt"/> 		
		            		<fo:table-column column-width="100pt"/>
		            		<fo:table-column column-width="100pt"/>
		            		<fo:table-column column-width="100pt"/>
		            		<fo:table-column column-width="100pt"/>
		            		<fo:table-column column-width="100pt"/>
		                    <fo:table-body>
		                    
		                    <#assign invoiceDetailsList = finalInvoiceDateMap.entrySet()>
							 <#list invoiceDetailsList as invoiceDetails>
								<#assign dayTotalBasicRev = 0>
				             	<#assign dayTotalBedRev = 0>
				             	<#assign dayTotalVatRev = 0>
				             	<#assign dayTotalCstRev = 0>
				             	<#assign dayTotalRev = 0>
				             	<#assign dayTotalMrpValue = 0>
				             	<#assign dayTotalPPD = 0>
				             <fo:table-row>
				             	<fo:table-cell>
							            <fo:block  keep-together="always" text-align="left" font-weight = "bold" font-size="12pt" white-space-collapse="false" >Invoice Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invoiceDetails.getKey(), "dd/MM/yyyy")}</fo:block>  
							    </fo:table-cell>
				             </fo:table-row>
							 <fo:table-row>
					                    <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							             	<#assign invoiceTotList = invoiceDetails.getValue().entrySet()>
							 			    <#list invoiceTotList as invoiceTot>
							             	<fo:block>
						        				<fo:table>
								                    <fo:table-column column-width="125pt"/>
								                    <fo:table-column column-width="190pt"/>
								                    <fo:table-column column-width="100pt"/> 
								               	    <fo:table-column column-width="100pt"/>
								            		<fo:table-column column-width="130pt"/> 		
								            		<fo:table-column column-width="110pt"/>
								            		<fo:table-column column-width="100pt"/>
								            		<fo:table-column column-width="125pt"/>
								            		<fo:table-column column-width="95pt"/>
								            		<fo:table-column column-width="95pt"/>
								                    <fo:table-body>
							             				<fo:table-row>
						            						<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, invoiceTot.getKey()?if_exists, false)>
							             					<fo:table-cell>
						            							<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >${partyName}[${invoiceTot.getKey()?if_exists}]</fo:block>  
						            						</fo:table-cell>
						            						<fo:table-cell>
												             	<fo:block>
											        				<fo:table>
													                    <fo:table-column column-width="90pt"/>
													                    <fo:table-column column-width="90pt"/>
													                    <fo:table-column column-width="90pt"/> 
													               	    <fo:table-column column-width="90pt"/>
													            		<fo:table-column column-width="110pt"/> 		
													            		<fo:table-column column-width="100pt"/>
													            		<fo:table-column column-width="90pt"/>
													            		<fo:table-column column-width="105pt"/>
													            		<fo:table-column column-width="105pt"/>
													            		<fo:table-column column-width="900pt"/>
													                    <fo:table-body>
													                    <#assign totalBasicRev=0>
													                    <#assign totalBedRev=0>
													                    <#assign totalVatRev=0>
													                    <#assign totalCstRev=0>
													                    <#assign totalRevenue=0>
													                    <#assign totalMrpValue = 0>
													                    <#assign totalPPD=0>
													                    <#assign invoicePartyTotals = invoiceTot.getValue()>
								 										<#list invoicePartyTotals as invoicePartyTot>
									 										<#assign totalBasicRev=totalBasicRev+invoicePartyTot.get("basicRevenue")?if_exists>
											       							<#assign totalBedRev=totalBedRev+invoicePartyTot.get("bedRevenue")?if_exists>
											       							<#assign totalVatRev=totalVatRev+invoicePartyTot.get("vatRevenue")?if_exists>
											       							<#assign totalCstRev=totalCstRev+invoicePartyTot.get("cstRevenue")?if_exists>
											       							<#assign totalRevenue=totalRevenue+invoicePartyTot.get("totalRevenue")?if_exists>
											       							<#assign totalMrpValue=totalMrpValue+invoicePartyTot.get("totalMrpValue")?if_exists>
											       							<#if (invoicePartyTot.get("totalMrpValue"))??>
											       							<#assign ppd=invoicePartyTot.get("ppd")>
											       							<#assign totalPPD=totalPPD+invoicePartyTot.get("ppd")>
											       							<#else>
											       							<#assign ppd=0>
											       							</#if>
												             				<fo:table-row>
												             					<fo:table-cell>
											            							<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${invoicePartyTot.get("invoiceId")?if_exists}</fo:block>  
											            						</fo:table-cell>
											            						<fo:table-cell>
											            							<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${invoicePartyTot.get("invoiceSequenceId")?if_exists}</fo:block>  
											            						</fo:table-cell>
											            						<fo:table-cell>
											            							<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false">${invoicePartyTot.get("totalMrpValue")?if_exists?string("#0.00")}</fo:block>  
											            						</fo:table-cell>
											            						<fo:table-cell>
																	            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${invoicePartyTot.get("basicRevenue")?if_exists?string("#0.00")}</fo:block>  
																	            </fo:table-cell>
																	            <fo:table-cell>
																	            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${invoicePartyTot.get("bedRevenue")?if_exists?string("#0.00")}</fo:block>  
																	            </fo:table-cell>
																	            <fo:table-cell>
																	            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${invoicePartyTot.get("vatRevenue")?if_exists?string("#0.00")}</fo:block>  
																	            </fo:table-cell>
																	            <fo:table-cell>
																	            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${invoicePartyTot.get("cstRevenue")?if_exists?string("#0.00")}</fo:block>  
																	            </fo:table-cell>
																	            <fo:table-cell>
																	            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${ppd?string("#0.00")}</fo:block>  
																	            </fo:table-cell>
																	            <fo:table-cell>
																	            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${invoicePartyTot.get("totalRevenue")?if_exists?string("#0.00")}</fo:block>  
																	            </fo:table-cell>
												            				</fo:table-row>
												            				<fo:table-row>
												            					<fo:table-cell number-columns-spanned="2">
																	            	<fo:block  keep-together="always" text-align="left" font-size="12pt" font-weight="bold" white-space-collapse="false" >TIN : ${invoicePartyTot.get("idValue")?if_exists}</fo:block>  
																	            </fo:table-cell>
																	        </fo:table-row>
												            				</#list> 
												            				<fo:table-row> 
																			      <fo:table-cell>   						
																					 <fo:block>-----------------------------------------------------------------------------------------------------------------------</fo:block>
												          						  </fo:table-cell>
										          						  </fo:table-row> 
																			<fo:table-row>
															                    <fo:table-cell>
																	            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">Total</fo:block>  
																	            </fo:table-cell>
																	            <fo:table-cell>
																	            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>  
																	            </fo:table-cell>
																	            <#assign dayTotalMrpValue = dayTotalMrpValue + totalMrpValue>
																	             <fo:table-cell>
																	            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${totalMrpValue?if_exists?string("#0.00")}</fo:block>  
																	            </fo:table-cell>
																	            <#assign dayTotalBasicRev = dayTotalBasicRev + totalBasicRev>
																	             <fo:table-cell>
																	            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${totalBasicRev?if_exists?string("#0.00")}</fo:block>  
																	            </fo:table-cell>
																	            <#assign dayTotalBedRev = dayTotalBedRev + totalBedRev>
																	            <fo:table-cell>
																	            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${totalBedRev?if_exists?string("#0.00")}</fo:block>  
																	            </fo:table-cell>
																	            <#assign dayTotalVatRev = dayTotalVatRev + totalVatRev>
																	            <fo:table-cell>
																	            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${totalVatRev?if_exists?string("#0.00")}</fo:block>  
																	            </fo:table-cell>
																	            <#assign dayTotalCstRev = dayTotalCstRev + totalCstRev>
																	            <fo:table-cell>
																	            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${totalCstRev?if_exists?string("#0.00")}</fo:block>  
																	            </fo:table-cell>
																	            <#assign dayTotalPPD = dayTotalPPD + totalPPD>
																	            <fo:table-cell>
																	            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${totalPPD?string("#0.00")}</fo:block>  
																	            </fo:table-cell>
																	            <#assign dayTotalRev = dayTotalRev + totalRevenue>
																	            <fo:table-cell>
																	            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${totalRevenue?if_exists?string("#0.00")}</fo:block>  
																	            </fo:table-cell>
																	     	</fo:table-row>
																	     	<fo:table-row> 
																			      <fo:table-cell>   						
																					 <fo:block>-----------------------------------------------------------------------------------------------------------------------</fo:block>
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
        									</#list>
							             </fo:table-cell>
							 </fo:table-row>
							 <fo:table-row>
	      						  <fo:table-cell>
									<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>  
					             </fo:table-cell>
					             <fo:table-cell>
									<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">Day Total</fo:block>  
					             </fo:table-cell>
					             <fo:table-cell>
									<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>  
					             </fo:table-cell>
					             <#assign grandTotalMrpValue = grandTotalMrpValue + dayTotalMrpValue>
					             <fo:table-cell>
									<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${dayTotalMrpValue?if_exists?string("#0.00")}</fo:block>  
					             </fo:table-cell>
					             <#assign grandTotalBasicRev = grandTotalBasicRev + dayTotalBasicRev>
					             <fo:table-cell>
									<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${dayTotalBasicRev?if_exists?string("#0.00")}</fo:block>  
					             </fo:table-cell>
					             <#assign grandTotalBedRev = grandTotalBedRev + dayTotalBedRev>
					             <fo:table-cell>
									<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${dayTotalBedRev?if_exists?string("#0.00")}</fo:block>  
					             </fo:table-cell>
					             <#assign grandTotalVatRev = grandTotalVatRev + dayTotalVatRev>
					             <fo:table-cell>
									<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${dayTotalVatRev?if_exists?string("#0.00")}</fo:block>  
					             </fo:table-cell>
					             <#assign grandTotalCstRev = grandTotalCstRev + dayTotalCstRev>
					             <fo:table-cell>
									<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${dayTotalCstRev?if_exists?string("#0.00")}</fo:block>  
					             </fo:table-cell>
					             <#assign grandTotalPPD = grandTotalPPD + dayTotalPPD>
					             <fo:table-cell>
									<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${dayTotalPPD?string("#0.00")}</fo:block>  
					             </fo:table-cell>
					             <#assign grandTotalRev = grandTotalRev + dayTotalRev>
					             <fo:table-cell>
									<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${dayTotalRev?if_exists?string("#0.00")}</fo:block>  
					             </fo:table-cell>
	  						  </fo:table-row>
	  						  <fo:table-row> 
							      <fo:table-cell>   						
									 <fo:block>-------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
							</fo:table-row> 
							 </#list>
							 <fo:table-row>
	      						  <fo:table-cell>
									<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>  
					             </fo:table-cell>
					             <fo:table-cell>
									<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">Grand Total</fo:block>  
					             </fo:table-cell>
					             <fo:table-cell>
									<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>  
					             </fo:table-cell>
					             <fo:table-cell>
									<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${grandTotalMrpValue?if_exists?string("#0.00")}</fo:block>  
					             </fo:table-cell>
					             <fo:table-cell>
									<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${grandTotalBasicRev?if_exists?string("#0.00")}</fo:block>  
					             </fo:table-cell>
					             <fo:table-cell>
									<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${grandTotalBedRev?if_exists?string("#0.00")}</fo:block>  
					             </fo:table-cell>
					             <fo:table-cell>
									<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${grandTotalVatRev?if_exists?string("#0.00")}</fo:block>  
					             </fo:table-cell>
					             <fo:table-cell>
									<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${grandTotalCstRev?if_exists?string("#0.00")}</fo:block>  
					             </fo:table-cell>
					             <fo:table-cell>
									<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${grandTotalPPD?string("#0.00")}</fo:block>  
					             </fo:table-cell>
					             <fo:table-cell>
									<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${grandTotalRev?if_exists?string("#0.00")}</fo:block>  
					             </fo:table-cell>
	  						  </fo:table-row>
	  						  <fo:table-row> 
							      <fo:table-cell>   						
									 <fo:block>-------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
							</fo:table-row>
							<fo:table-row> 
							      <fo:table-cell number-columns-spanned="2" >   						
							 	      <fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
							 	   </fo:table-cell>
						 	         <fo:table-cell number-columns-spanned="2">   						
							 	         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
							 	   <fo:table-cell number-columns-spanned="2">   						
							 	        <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
						 	         <fo:table-cell >   						
							 	         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
							 </fo:table-row>	
							<fo:table-row> 
							      <fo:table-cell number-columns-spanned="2" >   						
							 	      <fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
							 	   </fo:table-cell>
						 	         <fo:table-cell number-columns-spanned="2">   						
							 	         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
							 	   <fo:table-cell number-columns-spanned="2">   						
							 	        <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
						 	         <fo:table-cell >   						
							 	         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
							 </fo:table-row>	
							<fo:table-row> 
							      <fo:table-cell number-columns-spanned="2" >   						
							 	      <fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
							 	   </fo:table-cell>
						 	         <fo:table-cell number-columns-spanned="2">   						
							 	         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
							 	   <fo:table-cell number-columns-spanned="2">   						
							 	        <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
						 	         <fo:table-cell >   						
							 	         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
							 </fo:table-row>	
								<fo:table-row> 
							      <fo:table-cell number-columns-spanned="2" >   						
							 	         <fo:block text-align="left" white-space-collapse="false" font-size="12pt" keep-together="always" font-weight="bold">Verifed By</fo:block>
							 	   </fo:table-cell>
						 	         <fo:table-cell number-columns-spanned="2">   						
							 	         <fo:block text-align="right" white-space-collapse="false" font-size="12pt"  keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;AM(F)/DM(F)</fo:block>
							 	   </fo:table-cell>
							 	   <fo:table-cell number-columns-spanned="2">   						
							 	         <fo:block text-align="right" white-space-collapse="false" font-size="12pt"  keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;M(F)/GM(F)</fo:block>
							 	   </fo:table-cell>
						 	         <fo:table-cell number-columns-spanned="2">   						
							 	         <fo:block text-align="right" white-space-collapse="false" font-size="12pt" keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;Pre-Audit</fo:block>
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
 </#if>
 </fo:root>
</#escape>