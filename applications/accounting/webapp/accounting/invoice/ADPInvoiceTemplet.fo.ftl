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
      <fo:simple-page-master master-name="main" page-height="11in" page-width="8.5in"
        margin-top="0.5in" margin-bottom="0.3in" margin-left=".5in" margin-right="1in">
          <fo:region-body margin-top="1in"/>
          <fo:region-before extent="1in"/>
          <fo:region-after extent="1in"/>
      </fo:simple-page-master>
    </fo:layout-master-set>
    <#assign emplDetails =0 />
    <#assign emplLeaves =0 />
    <#assign days=0 />
    <#assign doj=0 />
    <#assign emplPosition=0 />
    <#assign location=0 />
    <#if invoiceDetailList?has_content>
      <#list invoiceDetailList as invoiceDetail>
      	<#assign totalEarnings =0 />
        <#assign totalDeductions =0 />
        <#assign invoice = invoiceDetail.invoice />
        <#assign dispalyParty = invoiceDetail.dispalyParty />
        <#if invoiceDetail.billingParty?has_content>
          <#assign billingParty = invoiceDetail.billingParty />
        </#if>
        <#assign emplDetails = delegator.findByAnd("PartyPersonAndEmployeeDetail", {"partyId" : dispalyParty.partyId})/>
        <#assign emplLeaves = delegator.findByAnd("EmplLeaveStatus", {"partyId" : dispalyParty.partyId})/>
        <#assign days=(invoiceDetail.lossOfPayDays)/>
        <#assign doj=delegator.findByAnd("Employment", {"partyIdTo" : dispalyParty.partyId})/>
        <#assign emplPosition=delegator.findByAnd("EmplPosition", {"partyId" : dispalyParty.partyId})/>
         <#assign location=delegator.findByAnd("EmployeeContactDetails", {"partyId" : dispalyParty.partyId})/>
        <fo:page-sequence master-reference="main">
        	 <#-- the footer -->
        <fo:static-content flow-name="xsl-region-after">
         <#if invoice.invoiceTypeId =="PAYROL_INVOICE">
       <fo:block font-size="8pt" text-align="center">             
             <#if footerImageUrl?has_content><fo:external-graphic src="<@ofbizContentUrl>${footerImageUrl}</@ofbizContentUrl>" overflow="hidden" height="20px" content-height="scale-to-fit"/></#if>             
         </fo:block>  
            </#if>
            <fo:block font-size="8pt" text-align="center" space-before="10pt">
                ${uiLabelMap.CommonPage} <fo:page-number-citation ref-id="theEnd"/> ${uiLabelMap.CommonOf} <fo:page-number-citation ref-id="theEnd"/>
            </fo:block>   
            
        </fo:static-content>
          <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
            <fo:block id="theEnd"/>
            <fo:block text-align="center" border-style="solid" font-weight="bold">${billingParty.getRelatedOne("PartyGroup").get("groupName",locale)}</fo:block>
            <fo:block font-size="8pt">
            	<fo:table width="100%" table-layout="fixed"  border-style="solid">
            		<fo:table-column column-width="7in"/>
            		<fo:table-body>
                  		<fo:table-row>
                    		<fo:table-cell>
                      			<fo:block text-align="center">
                        		${screens.render("component://order/widget/ordermgr/OrderPrintScreens.xml#CompanyLogo")}
                     		 	</fo:block>
                     		 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                     		 	<fo:block text-align="center" font-weight="bold"><fo:inline text-decoration="underline">PAYSLIP FOR ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodEnd, "MMMMM-yyyy")}</fo:inline></fo:block>
                     		 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                     		 	<fo:block>
                     		 		<fo:table width="100%" table-layout="fixed">
                     		 			<fo:table-column />
                     		 			<fo:table-column />
                     		 			<fo:table-column />
                     		 			<fo:table-body>
                     		 				<fo:table-row>
                     		 					<fo:table-cell>
                     		 						<fo:block text-align="left" keep-together="always" white-space-collapse="false">Employee Name       :${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, dispalyParty.partyId, false)}</fo:block>
                     		 					</fo:table-cell>
                     		 					<fo:table-cell/>
                     		 					<fo:table-cell>
                     		 						<fo:block text-align="left" keep-together="always" white-space-collapse="false">Employee.PAN          :${emplDetails[0].panId?if_exists}</fo:block>
                     		 					</fo:table-cell>
                     		 				</fo:table-row>
                     		 				<fo:table-row>
                     		 					<fo:table-cell>
                     		 						<fo:block text-align="left" keep-together="always" white-space-collapse="false">Department               :${(emplPosition[0].emplPositionTypeId)?if_exists}</fo:block>
                     		 					</fo:table-cell>
                     		 					<fo:table-cell/>
                     		 					<fo:table-cell>
                     		 						<fo:block text-align="left" keep-together="always" white-space-collapse="false">D.O.J                         :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString((doj[0].fromDate), "dd/MM/yyyy")}</fo:block>
                     		 					</fo:table-cell>
                     		 				</fo:table-row>
                     		 				<fo:table-row>
                     		 					<fo:table-cell>
                     		 						<fo:block text-align="left" keep-together="always" white-space-collapse="false">Designation               :${(emplPosition[0].emplPositionId)?if_exists}</fo:block>
                     		 					</fo:table-cell>
                     		 					<fo:table-cell/>
                     		 					<fo:table-cell>
                     		 						<fo:block text-align="left" keep-together="always" white-space-collapse="false">Employee No.            : ${emplDetails[0].employeeId?if_exists}</fo:block>
                     		 					</fo:table-cell>
                     		 				</fo:table-row>
                     		 				<fo:table-row>
                     		 					<fo:table-cell>
                     		 						<fo:block text-align="left" keep-together="always" white-space-collapse="false">Location                    :${(location[0].city)?if_exists}</fo:block>
                     		 					</fo:table-cell>
                     		 					<fo:table-cell/>
                     		 					<fo:table-cell>
                     		 						<fo:block text-align="left" keep-together="always" white-space-collapse="false">Bank A/c No.              :${emplDetails[0].employeeBankAccNo?if_exists}</fo:block>
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
            <fo:block font-size="8pt">
            	<fo:table width="100%"   border-style="solid">
            		<fo:table-column column-width="2.5in"/>
            		<fo:table-column column-width="1in"/>
            		<fo:table-column column-width="3.5in"/>
            		<fo:table-header>
		       			<fo:table-row >
                			<fo:table-cell  border-style="solid">
                    			<fo:block text-align="center" font-weight="bold">Attendance Details</fo:block>
                    		</fo:table-cell>
                     		<fo:table-cell border-style="solid">
                        		<fo:block font-weight="bold" text-align="center">Value</fo:block>
                     		</fo:table-cell> 
                    		<fo:table-cell border-style="solid">
                        		<fo:block font-weight="bold" text-align="center">Leave Details</fo:block>
                    		</fo:table-cell>
                    	</fo:table-row>
                    </fo:table-header>
                    <fo:table-body>
                    	<fo:table-row>
                    		<fo:table-cell></fo:table-cell>
                    	</fo:table-row>
                    </fo:table-body>
                  </fo:table>	
            </fo:block>
            <fo:block font-size="8pt">
            	<fo:table width="100%"   border-width="solid">
            		<fo:table-column column-width="2.5in"/>
            		<fo:table-column column-width="1in"/>
            		<fo:table-column column-width="3.5in"/>
            		<fo:table-column column-width="2in"/>
            		<fo:table-body>
		       			<fo:table-row >
                			<fo:table-cell  border-style="solid">
                				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                    			<fo:block text-align="left" keep-together="always" white-space-collapse="false">Total Days                :</fo:block>
                    			<fo:block text-align="left" keep-together="always" white-space-collapse="false">Loss of Pay              :</fo:block>
                    			<fo:block text-align="left" keep-together="always" white-space-collapse="false">Net Paid Days          :</fo:block>
                    		</fo:table-cell>
                     		<fo:table-cell border-style="solid">
                     			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                        		<fo:block text-align="center">${Static["org.ofbiz.base.util.UtilDateTime"].getIntervalInDays(timePeriodStart,timePeriodEnd)}</fo:block>
                        		<fo:block text-align="center">${days}days</fo:block>
                        		<fo:block text-align="center">${(Static["org.ofbiz.base.util.UtilDateTime"].getIntervalInDays(timePeriodStart,timePeriodEnd))-days}days</fo:block>
                     		</fo:table-cell> 
                    		<fo:table-cell border-style="solid">
                    			<fo:block>
                    				<fo:table width="100%">
                    					<fo:table-column column-width="1in"/>
                    					<fo:table-column column-width="1in"/>
                    					<fo:table-column column-width="1in"/>
                    					<fo:table-column column-width="1in"/>
                    					<fo:table-body>
                    						<fo:table-row>
                    							<fo:table-cell/>
                    							<fo:table-cell/>
                    							<fo:table-cell>
                    								<fo:block font-weight="bold">SL</fo:block>
                    							</fo:table-cell>
                    							<fo:table-cell>
                    								<fo:block font-weight="bold">CL</fo:block>
                    							</fo:table-cell>
                    						</fo:table-row>
                    						<fo:table-row>
                    							<fo:table-cell><fo:block text-align="left" keep-together="always" white-space-collapse="false">Opening Leaves                  :</fo:block></fo:table-cell>
                    							<fo:table-cell/>
                    							<fo:table-cell><fo:block><#list emplLeaves as empl><#if empl.leaveTypeId=="SICK_LEAVE">${empl.availableLeaves?if_exists}</#if></#list></fo:block></fo:table-cell>
                    							<fo:table-cell><fo:block><#list emplLeaves as empl><#if empl.leaveTypeId=="CASUAL_LEAVE">${empl.availableLeaves?if_exists}</#if></#list></fo:block></fo:table-cell>
                    						</fo:table-row>
                    						<fo:table-row>
                    							<fo:table-cell><fo:block text-align="left" keep-together="always" white-space-collapse="false">Additions During the Month :</fo:block></fo:table-cell>
                    							<fo:table-cell/>
                    							<fo:table-cell><fo:block>1</fo:block></fo:table-cell>
                    							<fo:table-cell><fo:block>1</fo:block></fo:table-cell>
                    						</fo:table-row>
                    						<fo:table-row>
                    							<fo:table-cell><fo:block text-align="left" keep-together="always" white-space-collapse="false">Availed During the Month    :</fo:block></fo:table-cell>
                    							<fo:table-cell/>
                    							<fo:table-cell><fo:block><#list emplLeaves as empl><#if empl.leaveTypeId=="SICK_LEAVE">${empl.availedLeaves?if_exists}</#if></#list></fo:block></fo:table-cell>
                    							<fo:table-cell><fo:block><#list emplLeaves as empl><#if empl.leaveTypeId=="CASUAL_LEAVE">${empl.availedLeaves?if_exists}</#if></#list></fo:block></fo:table-cell>
                    						</fo:table-row>
                    						<fo:table-row>
                    							<fo:table-cell><fo:block text-align="left" keep-together="always" white-space-collapse="false">Closing Leaves                    :</fo:block></fo:table-cell>
                    							<fo:table-cell/>
                    							<fo:table-cell><fo:block><#list emplLeaves as empl><#if empl.leaveTypeId=="SICK_LEAVE">${(empl.availableLeaves+1)-empl.availedLeaves}</#if></#list></fo:block></fo:table-cell>
                    							<fo:table-cell><fo:block><#list emplLeaves as empl><#if empl.leaveTypeId=="CASUAL_LEAVE">${(empl.availableLeaves+1)-empl.availedLeaves}</#if></#list></fo:block></fo:table-cell>
                    						</fo:table-row>                   						
                    					</fo:table-body>	
                    				</fo:table>	
                    			</fo:block>
                        	</fo:table-cell>
                    	</fo:table-row>
                    	<fo:table-row >
                    		<fo:table-cell border-style="solid"/>
                    		<fo:table-cell/>
                    		<fo:table-cell border-style="solid">
                    			<fo:block>
                    				<fo:table width="100%" >
                    					<fo:table-column column-width="1in"/>
                    					<fo:table-column column-width="1in"/>
                    					<fo:table-column column-width="1in"/>
                    					<fo:table-body>
                    						<fo:table-row>
                    							<fo:table-cell><fo:block text-align="left" keep-together="always" white-space-collapse="false">Opening CompensationOffs:</fo:block></fo:table-cell>
                    							<fo:table-cell/>
                    							<fo:table-cell><fo:block><#list emplLeaves as empl><#if empl.leaveTypeId=="COMP_OFF">${empl.availableLeaves?if_exists}Hrs</#if></#list></fo:block></fo:table-cell>
                    						</fo:table-row>
                    						<fo:table-row>
                    							<fo:table-cell><fo:block text-align="left" keep-together="always" white-space-collapse="false">Worked During the Month    :</fo:block></fo:table-cell>
                    							<fo:table-cell/>
                    							<fo:table-cell><fo:block><#list emplLeaves as empl><#if empl.leaveTypeId=="COMP_OFF">${empl.accruedLeaves?if_exists}Hrs</#if></#list></fo:block></fo:table-cell>
                    						</fo:table-row>
                    						<fo:table-row>
                    							<fo:table-cell><fo:block text-align="left" keep-together="always" white-space-collapse="false">Availed/Encashed                :</fo:block></fo:table-cell>
                    							<fo:table-cell/>
                    							<fo:table-cell><fo:block><#list emplLeaves as empl><#if empl.leaveTypeId=="COMP_OFF">${empl.availedLeaves?if_exists}Hrs</#if></#list></fo:block></fo:table-cell>
                    						</fo:table-row>
                    						<fo:table-row>
                    							<fo:table-cell><fo:block text-align="left" keep-together="always" white-space-collapse="false">Closing Compensation Offs :</fo:block></fo:table-cell>
                    							<fo:table-cell/>
                    							<fo:table-cell><fo:block><#list emplLeaves as empl><#if empl.leaveTypeId=="COMP_OFF">${(empl.availableLeaves+empl.accruedLeaves)-empl.availedLeaves}</#if></#list></fo:block></fo:table-cell>
                    						</fo:table-row>                   						
                    					</fo:table-body>	
                    				</fo:table>	
                    			</fo:block>
                    		</fo:table-cell>
                    	</fo:table-row>
                   </fo:table-body>
                  </fo:table>	
            </fo:block>
            <fo:block font-size="8pt">
            <fo:table width="100%"   border-style="solid">
            		<fo:table-column column-width="3.5in"/>
            		<fo:table-column column-width="3.5in"/>            		
            		<fo:table-body>
            			<fo:table-row >
                    		<fo:table-cell border-style="solid">
                    			<fo:block keep-together="always" text-align="left">
            	<fo:table width="100%" >
            		<fo:table-column column-width="2.5in"/>
            		<fo:table-column column-width="1in"/>            		
            		<fo:table-body>
            			<fo:table-row >
                    		<fo:table-cell border-style="solid">
                    			<fo:block keep-together="always" text-align="center" font-weight="bold">Earnings</fo:block>
                    		</fo:table-cell>
                    		<fo:table-cell>
                    			<fo:block keep-together="always" text-align="center" font-weight="bold">${uiLabelMap.CommonAmount}</fo:block>
                    		</fo:table-cell>                    		
                    	</fo:table-row>
                    	<#if invoiceDetail.invoiceItems?has_content>
                    <#assign invoiceItems = invoiceDetail.invoiceItems?if_exists />            		
                      <#list invoiceItems as invoiceItem>
                      <#assign itemType = invoiceItem.getRelatedOne("InvoiceItemType")>
                       <#if (invoiceItem.invoiceItemTypeId).indexOf("PAYROL_BEN") != -1>
                    	<fo:table-row>                      
                    		<fo:table-cell border-style="solid">                    		
                    		<#if invoiceItem.description?has_content>
                        <#assign description=invoiceItem.description>
                      <#elseif taxRate?has_content & taxRate.get("description",locale)?has_content>
                        <#assign description=taxRate.get("description",locale)>
                      <#elseif itemType.get("description",locale)?has_content>
                        <#assign description=itemType.get("description",locale)>
                      </#if> 
                      <#assign totalEarnings=(totalEarnings+(invoiceItem.amount))>                  			
                    	<fo:block>${description?if_exists}</fo:block>                			
                    			
                    		</fo:table-cell>                    		
                    		<fo:table-cell border-style="solid" text-align="right"><fo:block>${invoiceItem.amount}.00</fo:block></fo:table-cell>
                    	</fo:table-row>
                    	</#if>                    	
                    	</#list>
                    	</#if>
            		</fo:table-body>
            	</fo:table>
            	</fo:block>
            	</fo:table-cell>
            	<fo:table-cell>
            		<fo:block keep-together="always" text-align="left">
            	<fo:table width="100%">
            		<fo:table-column column-width="2.5in"/>
            		<fo:table-column column-width="1in"/>            		
            		<fo:table-body>
            			<fo:table-row >
                    		<fo:table-cell border-style="solid">
                    			<fo:block keep-together="always" text-align="center" font-weight="bold">Deductions</fo:block>
                    		</fo:table-cell>
                    		<fo:table-cell border-style="solid">
                    			<fo:block keep-together="always" text-align="center" font-weight="bold">${uiLabelMap.CommonAmount}</fo:block>
                    		</fo:table-cell>                    		
                    	</fo:table-row>
                    	<#if invoiceDetail.invoiceItems?has_content>
                    <#assign invoiceItems = invoiceDetail.invoiceItems?if_exists />            		
                      <#list invoiceItems as invoiceItem>
                      <#assign itemType = invoiceItem.getRelatedOne("InvoiceItemType")>
                      <#if (invoiceItem.invoiceItemTypeId).indexOf("PAYROL_DD") != -1>
                    	<fo:table-row >                      
                    		<fo:table-cell border-style="solid">                    		
                    		<#if invoiceItem.description?has_content>
                        <#assign description=invoiceItem.description>
                      <#elseif taxRate?has_content & taxRate.get("description",locale)?has_content>
                        <#assign description=taxRate.get("description",locale)>
                      <#elseif itemType.get("description",locale)?has_content>
                        <#assign description=itemType.get("description",locale)>
                      </#if>
                      <#assign totalDeductions=(totalDeductions-(invoiceItem.amount))>                   			
                    	<fo:block>${description?if_exists}</fo:block>                			
                    			
                    		</fo:table-cell>                    		
                    		<fo:table-cell border-style="solid" text-align="right"><fo:block>${invoiceItem.amount}.00</fo:block></fo:table-cell>
                    	</fo:table-row>
                    	</#if>                    	
                    	</#list>
                    	</#if>
            		</fo:table-body>
            	</fo:table>
            	</fo:block>         
            	</fo:table-cell>                    		
                   </fo:table-row>
                   <fo:table-row border-style="solid">
                   		<fo:table-cell border-style="solid">
                   			
                   			<fo:block font-weight="bold" white-space-collapse="false" keep-together="always">Total Earnings :                                        <#if totalEarnings?has_content>
                   			<#assign total = totalEarnings?if_exists />
                   			<@ofbizCurrency amount=total isoCode=invoice.currencyUomId?if_exists/></#if></fo:block>
                   		</fo:table-cell>
                   		<fo:table-cell>
                   			<fo:block font-weight="bold" white-space-collapse="false" keep-together="always">Total Deductions :                                    <#if totalDeductions?has_content>
                   			<#assign totalamount = totalDeductions?if_exists />
                   			<@ofbizCurrency amount=totalamount isoCode=invoice.currencyUomId?if_exists/></#if></fo:block>
                   		</fo:table-cell>
                   </fo:table-row>
                   <fo:table-row>
                   		<fo:table-cell>
                   			
                   			<fo:block font-weight="bold">Net Pay Credited :<#if invoiceDetail.invoiceNoTaxTotal?has_content>
                            <#assign invoiceNoTaxTotal = invoiceDetail.invoiceNoTaxTotal?if_exists />
                            <@ofbizCurrency amount=invoiceNoTaxTotal isoCode=invoice.currencyUomId?if_exists/>
                          </#if></fo:block>
                          <#assign amount = Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(invoiceNoTaxTotal, "%dollars-and-hundredths", locale)>
                   			<fo:block white-space-collapse="false" keep-together="always">(In Words:${amount?if_exists.substring(0,(amount.length()-10))}  only)</fo:block>
                   		</fo:table-cell>
                   </fo:table-row>
            		</fo:table-body>
            	</fo:table>            		          
            </fo:block>
            <fo:block text-align="center" keep-together="always" font-size="8pt">This is a computer-generated salary slip. Does not require a Signature
            </fo:block>
          </fo:flow>          
        </fo:page-sequence>
       
      </#list>
    </#if>        
  </fo:root>
</#escape>