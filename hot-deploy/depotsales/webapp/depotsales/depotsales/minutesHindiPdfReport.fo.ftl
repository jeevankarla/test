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
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format" font-family="Courier,Mangal">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="8.69in" margin-left=".3in"  margin-right=".3in" margin-top=".5in" margin-bottom=".2in">
                <fo:region-body margin-top="1.2in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
       </fo:layout-master-set>
       ${setRequestAttribute("OUTPUT_FILENAME", "CenterWiseKgFatAcc.txt")}
       
       <#if OrderItemList?has_content>
       <fo:page-sequence master-reference="main" language="hi_IN">
       		<#assign locale= Static["org.ofbiz.base.util.UtilMisc"].parseLocale("hi_IN")>
            <fo:static-content flow-name="xsl-region-before" font-family="Courier,Mangal"> 
					<fo:block text-align="center" white-space-collapse="false" font-size="12pt" keep-together="always"></fo:block>
    				<fo:block text-align="center" white-space-collapse="false" font-size="12pt" keep-together="always">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "nhdcTitle", locale)}                                                 </fo:block>
    				<fo:block text-align="center" white-space-collapse="false"  font-size="12pt" keep-together="always">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "nhdcAddress", locale)}                                                 </fo:block>
    				
    				<#assign partyName = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", partyName, "toScript", "devanagari")).get("result")/>
    				<#assign supplierHindiPartyId = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", supplierHindiPartyId, "toScript", "devanagari")).get("result")/>
    				
    				<#assign fromDate = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yyyy"), "toScript", "devanagari")).get("result")/>
    				 
    				<fo:block>&#160;</fo:block>
    				<fo:block>&#160;</fo:block>
    				
    				<fo:block text-align="left"  white-space-collapse="false" font-size="12pt" >${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "minutes1", locale)}</fo:block>
    				<fo:block text-align="left" white-space-collapse="false" font-size="12pt" >${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "minutes2", locale)}</fo:block>
    				<fo:block text-align="left" white-space-collapse="false" font-size="12pt" >${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "minutes3", locale)}<fo:inline font-weight="bold">:${partyName?if_exists} </fo:inline></fo:block>
    				<fo:block text-align="left" white-space-collapse="false" font-size="12pt" >${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "minutes4", locale)}<fo:inline font-weight="bold">:${supplierHindiPartyId?if_exists} </fo:inline></fo:block>
    				<fo:block text-align="left" white-space-collapse="false" font-size="12pt" >${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "minutes5", locale)}:${fromDate?if_exists}</fo:block>
    				
    				
            		
    				
            </fo:static-content>
            
            
            
            <fo:flow flow-name="xsl-region-body" font-family="Courier,Mangal">
              <fo:block>
              <fo:block>&#160;</fo:block>
    				<fo:block>&#160;</fo:block>
    				<fo:block>&#160;</fo:block>
    				<fo:block>&#160;</fo:block>
             		<fo:table border-style="solid">
             		    <fo:table-column column-width="4%"/>
			            <fo:table-column column-width="35%"/>
			            <fo:table-column column-width="10%"/>
			            <fo:table-column column-width="10%"/>
			            <fo:table-column column-width="10%"/>
	                    <fo:table-column column-width="10%"/>
			            <fo:table-column column-width="10%"/>
			            <fo:table-column column-width="10%"/>
			            <fo:table-body>
			            
			            <#assign sno = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", "Number", "toScript", "devanagari")).get("result")/>
			            <#assign item = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", "item", "toScript", "devanagari")).get("result")/>
			            
			                <fo:table-row>
			                    <fo:table-cell border-style="solid">
					            	<fo:block  margin-top=".2in" text-align = "left" font-size="12pt">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "NHDCSNO", locale)}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in"  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "NHDCSNO", locale)}</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in"  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "NHDCUNIT", locale)}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in"   text-align="center" font-size="12pt" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "NHDCQUANTITY", locale)}</fo:block>
					            	<fo:block margin-top=".2in"  text-align="center" font-size="12pt" white-space-collapse="false">(KGS)</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in"  text-align="center" font-size="12pt" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "NHDCPURCHASERATE", locale)}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in"   text-align="center" font-size="12pt" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "NHDCANNUM", locale)}</fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in"  text-align="center" font-size="12pt" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "NHDCHANDLING", locale)}</fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in"  text-align="left" font-size="12pt" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "NHDCSALES", locale)}</fo:block>
					            </fo:table-cell>
					           
							</fo:table-row>
			            
			            
			                     
			                  <#list dayWiseEntriesLidast as orderList>
			            
			                <fo:table-row>
			                    <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in" keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${orderList.get("SrNo")}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in"   text-align="left" font-size="12pt" white-space-collapse="false">${orderList.get("productName")?if_exists} </fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in"  text-align="left" font-size="12pt" white-space-collapse="false">&#160;</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in" keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">${orderList.get("quantity")?if_exists} </fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in" keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">${orderList.get("unitPrice")?if_exists}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in"  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"> </fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in" keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in" keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${orderList.get("unitPrice")?if_exists}</fo:block>
					            </fo:table-cell>
					           
							</fo:table-row>
							</#list>
							
							
							
						</fo:table-body>
					</fo:table>
				</fo:block>
				
				
				<fo:block>&#160;&#160;&#160;&#160;&#160;</fo:block>
				<fo:block>&#160;&#160;&#160;&#160;&#160;</fo:block>
	          
				
				<fo:block>
              <fo:block>&#160;</fo:block>
             		<fo:table border-style="solid">
             		    <fo:table-column column-width="4%"/>
			            <fo:table-column column-width="35%"/>
			            <fo:table-column column-width="10%"/>
			            <fo:table-column column-width="10%"/>
			            <fo:table-column column-width="10%"/>
	                    <fo:table-column column-width="10%"/>
			            <fo:table-column column-width="10%"/>
			            <fo:table-column column-width="10%"/>
			            <fo:table-body>
			            
			            
			                <fo:table-row>
			                    <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in" text-align = "left" font-size="12pt">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "NHDCSNO", locale)}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in" keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "NHDCSNO", locale)}</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in" keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "NHDCUNIT", locale)}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  margin-top=".2in" text-align="center" font-size="12pt" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "NHDCQUANTITY", locale)}</fo:block>
					            	<fo:block margin-top=".2in"  text-align="center" font-size="12pt" white-space-collapse="false">(KGS)</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  margin-top=".2in" text-align="center" font-size="12pt" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "NHDCPURCHASERATE", locale)}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  margin-top=".2in" text-align="center" font-size="12pt" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "NHDCANNUM", locale)}</fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in" text-align="center" font-size="12pt" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "NHDCHANDLING", locale)}</fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in"  text-align="left" font-size="12pt" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "NHDCSALES", locale)}</fo:block>
					            </fo:table-cell>
					           
							</fo:table-row>
			            
			            
			                     
			                  <#list dayWiseEntriesLidast as orderList>
			            
			                <fo:table-row>
			                    <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in" keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${orderList.get("SrNo")}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in"  text-align="left" font-size="12pt" white-space-collapse="false">${orderList.get("productName")?if_exists} </fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in"  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">&#160;</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in" keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${orderList.get("quantity")?if_exists} </fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in" keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${orderList.get("unitPrice")?if_exists}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in"  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${orderList.get("annum")?if_exists}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in" keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${orderList.get("unitPrice")?if_exists}</fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in" keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${orderList.get("annum")?if_exists}</fo:block>
					            </fo:table-cell>
					           
							</fo:table-row>
							</#list>
							
							 <fo:table-row>
			                    <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in" keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"> </fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in"  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"><fo:block text-align = "left" font-size="12pt">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "NHDCTOTAL", locale)}</fo:block></fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in" keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">&#160;</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in"  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${totalsHindiList.get("totQuantity")?if_exists} </fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					           
					            	<fo:block margin-top=".2in" keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in" keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${totalsHindiList.get("totannum")?if_exists} </fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in" keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false"></fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block margin-top=".2in" keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">${totalsHindiList.get("totQuantity")?if_exists}  </fo:block>
					            </fo:table-cell>
					           
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				</fo:block>
				
				<fo:block>&#160;&#160;&#160;&#160;&#160;</fo:block>
				<#assign minutes8 = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", "3. Summary", "toScript", "devanagari")).get("result")/>
				
				<#assign minutes9 = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", "a) Actual Purchase Value", "toScript", "devanagari")).get("result")/>
				
				<#assign minutes10 = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", "b) Total Sale Value", "toScript", "devanagari")).get("result")/>
				
				<#assign minutes11 = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", "c) Difference of the Sale", "toScript", "devanagari")).get("result")/>
				
				<#assign minutes12 = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", "d) Value &amp;actual payment made to Mill:", "toScript", "devanagari")).get("result")/>
				
				<#assign minutes13 = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", "e) 0 days interest on the credit:", "toScript", "devanagari")).get("result")/>
				
				<#assign minutes14 = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", "f) Percentage of Trading Contribution: 0%", "toScript", "devanagari")).get("result")/>
				
				<#assign minutes15 = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", "4. Goods will be despatched on freight to-pay basis to:", "toScript", "devanagari")).get("result")/>
				
				<#assign minutes15 = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", "5. Payment will be made by user agency within BACK TO BACK/ ON CREDIT days / immediately failing which interest  11 per annum will be charged for the total number of days payment delayed.", "toScript", "devanagari")).get("result")/>
				
				<#assign minutes16 = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", "6. One total financial outflow in this transaction is Rs.", "toScript", "devanagari")).get("result")/>
				
				<#assign minutes17 = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", "7. Total supply including this transaction to the agency will Rs", "toScript", "devanagari")).get("result")/>
				
				<#assign minutes18 = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", "8. Payment dues with interest from the party:", "toScript", "devanagari")).get("result")/>
				
				<#assign minutes19 = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", "9. Payment Mill to be paid Cheque/Demand Draft for Rs.", "toScript", "devanagari")).get("result")/>
				
				<#assign minutes20 = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", "10. No. of Days credit extended by Mills to NHDC from date of despatch ........", "toScript", "devanagari")).get("result")/>
				
				<#assign minutes21 = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", "11. No. of Days credit extended by NHDC to Agency from date of despatch", "toScript", "devanagari")).get("result")/>
				
				<#assign minutes22 = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", "12. Any other specific information ...................", "toScript", "devanagari")).get("result")/>
				
				<#assign minutes23 = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", "13. Local Taxes as applicable.", "toScript", "devanagari")).get("result")/>
				
				<#assign minutes24 = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", "Advance Details: Cheque/DD No :Cr on Account amounting  received from user agency", "toScript", "devanagari")).get("result")/>
				
				
				<fo:block>${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "minutes8", locale)}</fo:block>
				<fo:block>&#160;                         ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "minutes9", locale)}</fo:block>
				<fo:block>&#160;                      ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "minutes10", locale)}</fo:block>
				<fo:block>&#160;                      ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "minutes11", locale)}</fo:block>
				<fo:block>&#160;                      ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "minutes12", locale)}</fo:block>
				<fo:block>&#160;                      ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "minutes13", locale)}</fo:block>
				<fo:block>&#160;                      ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "minutes14", locale)}</fo:block>
				<fo:block>${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "minutes15", locale)} : ${partyName}</fo:block>
				<fo:block>${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "minutes16", locale)}</fo:block>
                <fo:block>&#160;                      ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "minutes26", locale)}</fo:block>
				<fo:block>${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "minutes17", locale)}</fo:block>
				<fo:block>${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "minutes18", locale)}</fo:block>
                <fo:block>${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "minutes19", locale)}:${partyName} ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "asOn", locale)}:${fromDate}</fo:block>
				<fo:block>${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "minutes20", locale)}:${totalsHindiList.get("totannum")?if_exists}</fo:block>
				<fo:block>${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "minutes21", locale)}</fo:block>
				<fo:block>${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "minutes22", locale)}</fo:block>
				<fo:block>${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "minutes23", locale)}</fo:block>
				<fo:block>${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "minutes24", locale)}</fo:block>
				<fo:block>${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "minutes25", locale)}</fo:block>
			
			
			<#--	<fo:block>${minutes9}</fo:block>
				<fo:block>${minutes10}</fo:block>
				<fo:block>${minutes11}</fo:block>
				<fo:block>${minutes12}</fo:block>
				<fo:block>${minutes13}</fo:block>
				<fo:block>${minutes14}</fo:block>
				<fo:block>${minutes15}</fo:block>
				<fo:block>${minutes16}</fo:block>
				<fo:block>${minutes17}</fo:block>
				<fo:block>${minutes18}</fo:block>
				<fo:block>${minutes19}:${orderList.get("annum")?if_exists}</fo:block>
				<fo:block>${minutes20}</fo:block>
				<fo:block>${minutes21}</fo:block>
				<fo:block>${minutes22}</fo:block>
				<fo:block>${minutes23}</fo:block>
				<fo:block>${minutes24}</fo:block> -->
				
            </fo:flow>
       </fo:page-sequence>
    <#else>
                <fo:page-sequence master-reference="main">
                    <fo:flow flow-name="xsl-region-body">
                        <fo:block font-size="14pt" text-align="center">
                            ${uiLabelMap.NoOrdersFound}.
                        </fo:block>
                    </fo:flow>
                </fo:page-sequence>
     </#if>
   </fo:root> 
 </#escape>     