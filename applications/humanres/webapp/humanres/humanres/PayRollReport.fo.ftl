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
    <#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" : parameters.partyId}, true)>
    <#assign postalAddress=delegator.findByAnd("PartyAndPostalAddress", {"partyId" : parameters.partyId})/>
    <#if payRollMap?has_content>
    	<#assign payRollHeaderList = payRollMap.entrySet()>
      <#list payRollHeaderList as payRollHeader>
      	<#assign totalEarnings =0 />
        <#assign totalDeductions =0 />
      	 <#assign payHeader = delegator.findOne("PayrollHeader", {"payrollHeaderId" : payRollHeader.getKey()}, true)>
      	 <#assign partyId = payHeader.partyIdFrom>
      	 <#assign emplDetails = delegator.findOne("PartyPersonAndEmployeeDetail", {"partyId" : partyId}, true)/>      	 
      	 <#assign emplLeavesDetails = delegator.findOne("PayrollAttendance", {"partyId" : partyId, "customTimePeriodId": timePeriod}, true)/>
      	 <#assign doj=delegator.findByAnd("Employment", {"partyIdTo" : partyId})/>
      	 <#assign emplPosition=delegator.findByAnd("EmplPosition", {"partyId" : partyId})/>
      	 <#assign emplPositionAndFulfilment=delegator.findByAnd("EmplPositionAndFulfillment", {"employeePartyId" : partyId})/>
         <#assign location=delegator.findByAnd("EmployeeContactDetails", {"partyId" : partyId})/>
         <#assign emplLeaves = delegator.findByAnd("EmplLeaveBalanceStatus", {"partyId" : partyId, "customTimePeriodId": parameters.customTimePeriodId})/>       
     <fo:page-sequence master-reference="main"> 	 <#-- the footer -->
        <fo:static-content flow-name="xsl-region-after">
             <fo:block font-size="8pt" text-align="center">             
             	<#if footerImageUrl?has_content><fo:external-graphic src="<@ofbizContentUrl>${footerImageUrl?if_exists}</@ofbizContentUrl>" overflow="hidden" height="20px" content-height="scale-to-fit"/></#if>             
         	 </fo:block>  
         	 <fo:block font-size="8pt" text-align="center" space-before="10pt">
                ${uiLabelMap.CommonPage} <fo:page-number-citation ref-id="theEnd"/> ${uiLabelMap.CommonOf} <fo:page-number-citation ref-id="theEnd"/>
            </fo:block> 
        </fo:static-content>
          <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
           	<fo:block id="theEnd"/>  
            <fo:block text-align="center" border-style="solid" font-weight="bold">
            	<fo:table>
            		<fo:table-column column-width="7in"/>
            		<fo:table-column column-width="7in"/>
            		<fo:table-column column-width="7in"/>
            		<fo:table-column column-width="7in"/>
            		<fo:table-body>
            			<fo:table-row>
            				<fo:table-cell><fo:block keep-together="always">${partyGroup.groupName?if_exists}</fo:block></fo:table-cell>
            			</fo:table-row>
            			<fo:table-row>
            				<fo:table-cell><fo:block keep-together="always">${postalAddress[0].address1?if_exists}</fo:block></fo:table-cell>
            			</fo:table-row>
            		</fo:table-body>
            	</fo:table>
            	
            </fo:block>
           	<fo:block font-size="8pt">
            	<fo:table width="100%" table-layout="fixed"  border-style="solid">
            		<fo:table-column column-width="7in"/>
            		<fo:table-body>
                  		<fo:table-row>
                    		<fo:table-cell>                      			
                     		 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                     		 	<fo:block text-align="center" font-weight="bold"><fo:inline text-decoration="underline">PAYSLIP FOR ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodEnd, "MMMMM-yyyy")).toUpperCase()}</fo:inline></fo:block>
                     		 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                     		 	<fo:block>
                     		 		<fo:table width="100%" table-layout="fixed">
                     		 			<fo:table-column />
                     		 			<fo:table-column />
                     		 			<fo:table-column />
                     		 			<fo:table-body>
                     		 				<fo:table-row>
                     		 					<fo:table-cell>
                     		 						<fo:block text-align="left" keep-together="always" white-space-collapse="false">Employee Name       : ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)}</fo:block>
                     		 					</fo:table-cell>
                     		 					<fo:table-cell/>
                     		 					<fo:table-cell>
                     		 						<fo:block text-align="left" keep-together="always" white-space-collapse="false">PF Account Number   : ${emplDetails.presentEpf?if_exists}</fo:block>
                     		 					</fo:table-cell>
                     		 				</fo:table-row>
                     		 				<fo:table-row>
                     		 					<fo:table-cell>
                     		 						<fo:block text-align="left" keep-together="always" white-space-collapse="false">Employee No.            : <#if emplDetails.employeeId?has_content>${emplDetails.employeeId?if_exists}<#else>${partyId?if_exists}</#if></fo:block>
                     		 					</fo:table-cell>
                     		 					<fo:table-cell/>
                     		 					<fo:table-cell>
                     		 						<fo:block text-align="left" keep-together="always" white-space-collapse="false">ESI Number               : ${emplDetails.presentEsic?if_exists}</fo:block>
                     		 					</fo:table-cell>
                     		 				</fo:table-row>
                     		 				<fo:table-row>
                     		 					<fo:table-cell>
                     		 						<fo:block text-align="left" keep-together="always" white-space-collapse="false">Department               : ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, (doj[0].partyIdFrom)?if_exists, false)}</fo:block>
                     		 					</fo:table-cell>
                     		 					<fo:table-cell/>
                     		 					<fo:table-cell>
                     		 						<fo:block text-align="left" keep-together="always" white-space-collapse="false">Employee.PAN          : ${emplDetails.panId?if_exists}</fo:block>
                     		 					</fo:table-cell>
                     		 				</fo:table-row>
                     		 				<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : emplPositionAndFulfilment[0].emplPositionTypeId}, true)>
                     		 				<fo:table-row>
                     		 					<fo:table-cell>
                     		 						<fo:block text-align="left" keep-together="always" white-space-collapse="false">Designation               : ${designation.description?if_exists}</fo:block>
                     		 					</fo:table-cell>
                     		 					<fo:table-cell/>
                     		 					<fo:table-cell>
                     		 						<fo:block text-align="left" keep-together="always" white-space-collapse="false">D.O.J                         : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString((doj[0].fromDate), "dd/MM/yyyy")}</fo:block>
                     		 					</fo:table-cell>
                     		 				</fo:table-row>                     		 				
                     		 				<fo:table-row>
                     		 					<fo:table-cell>
                     		 						<fo:block text-align="left" keep-together="always" white-space-collapse="false">Location                    : ${(doj[0].locationGeoId)?if_exists}</fo:block>
                     		 					</fo:table-cell>
                     		 					<fo:table-cell/>
                     		 					<fo:table-cell>
                     		 						<fo:block text-align="left" keep-together="always" white-space-collapse="false">Bank A/c No.              : ${emplDetails.employeeBankAccNo?if_exists}</fo:block>
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
                        		<fo:block font-weight="bold" text-align="center">Days</fo:block>
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
                    		<#assign totalDays=0>
                    		<#assign lossOfPay=0>
                    		<#assign netPaidDays=0>
                    		<#if emplLeavesDetails?has_content>
                    			<#assign totalDays=emplLeavesDetails.get("noOfCalenderDays")>
                    			<#assign lossOfPay=emplLeavesDetails.get("lossOfPayDays")>
                    		</#if>      
                    		<#assign netPaidDays=(totalDays-lossOfPay)>              		
                     		<fo:table-cell border-style="solid">
                     			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                        		<fo:block text-align="center">${totalDays?if_exists}</fo:block>
                        		<fo:block text-align="center">${lossOfPay?if_exists}days</fo:block>
                        		<fo:block text-align="center">${(netPaidDays?if_exists)}days</fo:block>
                     		</fo:table-cell> 
                    		<fo:table-cell border-style="solid">
                    			<fo:block>
                    				<fo:table width="100%">
                    					<fo:table-column column-width="1in"/>
                    					<fo:table-column column-width="1in"/>
                    					<fo:table-column column-width="0.5in"/>
                    					<fo:table-column column-width="0.7in"/>
                    					<fo:table-column column-width="0.5in"/>
                    					<fo:table-body>
                    						<fo:table-row>
                    							<fo:table-cell/>
                    							<fo:table-cell/>
                    							<fo:table-cell>
                    								<fo:block font-weight="bold">EL</fo:block>
                    							</fo:table-cell>
                    							<fo:table-cell>
                    								<fo:block font-weight="bold">CH</fo:block>
                    							</fo:table-cell>
                    							<fo:table-cell>
                    								<fo:block font-weight="bold">CL</fo:block>
                    							</fo:table-cell>
                    						</fo:table-row>
                    						<fo:table-row>
                    							<fo:table-cell><fo:block text-align="left" keep-together="always" white-space-collapse="false">Opening Leaves                  :</fo:block></fo:table-cell>
                    							<fo:table-cell/>
                    							<fo:table-cell><fo:block><#if emplLeaves?has_content><#list emplLeaves as empl><#if empl.leaveTypeId=="EL">${empl.openingBalance?if_exists}</#if></#list></#if></fo:block></fo:table-cell>
                    							<fo:table-cell><fo:block><#if emplLeaves?has_content><#list emplLeaves as empl><#if empl.leaveTypeId=="CH">${empl.openingBalance?if_exists}</#if></#list></#if></fo:block></fo:table-cell>
                    							<fo:table-cell><fo:block><#if emplLeaves?has_content><#list emplLeaves as empl><#if empl.leaveTypeId=="CL">${empl.openingBalance?if_exists}</#if></#list></#if></fo:block></fo:table-cell>
                    						</fo:table-row>
                    						<fo:table-row>
                    							<fo:table-cell><fo:block text-align="left" keep-together="always" white-space-collapse="false">Additions During the Month :</fo:block></fo:table-cell>
                    							<fo:table-cell/>
                    							<fo:table-cell><fo:block><#if emplLeaves?has_content><#list emplLeaves as empl><#if empl.leaveTypeId=="EL">${empl.adjustedDays?if_exists}</#if></#list></#if></fo:block></fo:table-cell>
                    							<fo:table-cell><fo:block><#if emplLeaves?has_content><#list emplLeaves as empl><#if empl.leaveTypeId=="CH">${empl.adjustedDays?if_exists}</#if></#list></#if></fo:block></fo:table-cell>
                    							<fo:table-cell><fo:block><#if emplLeaves?has_content><#list emplLeaves as empl><#if empl.leaveTypeId=="CL">${empl.adjustedDays?if_exists}</#if></#list></#if></fo:block></fo:table-cell>
                    						</fo:table-row>
                    						<fo:table-row>
                    							<fo:table-cell><fo:block text-align="left" keep-together="always" white-space-collapse="false">Availed During the Month    :</fo:block></fo:table-cell>
                    							<fo:table-cell/>
                    							<fo:table-cell><fo:block><#if emplLeaves?has_content><#list emplLeaves as empl><#if empl.leaveTypeId=="EL">${empl.availedDays?if_exists}</#if></#list></#if></fo:block></fo:table-cell>
                    							<fo:table-cell><fo:block><#if emplLeaves?has_content><#list emplLeaves as empl><#if empl.leaveTypeId=="CH">${empl.availedDays?if_exists}</#if></#list></#if></fo:block></fo:table-cell>
                    							<fo:table-cell><fo:block><#if emplLeaves?has_content><#list emplLeaves as empl><#if empl.leaveTypeId=="CL">${empl.availedDays?if_exists}</#if></#list></#if></fo:block></fo:table-cell>
                    						</fo:table-row>
                    						<fo:table-row>
                    							<fo:table-cell><fo:block text-align="left" keep-together="always" white-space-collapse="false">Closing Leaves                    :</fo:block></fo:table-cell>
                    							<fo:table-cell/>
                    							<fo:table-cell><fo:block><#if emplLeaves?has_content><#list emplLeaves as empl><#if empl.leaveTypeId=="EL">${((empl.openingBalance+empl.adjustedDays)-(empl.availedDays))?if_exists}</#if></#list></#if></fo:block></fo:table-cell>
                    							<fo:table-cell><fo:block><#if emplLeaves?has_content><#list emplLeaves as empl><#if empl.leaveTypeId=="CH">${((empl.openingBalance+empl.adjustedDays)-(empl.availedDays))?if_exists}</#if></#list></#if></fo:block></fo:table-cell>
                    							<fo:table-cell><fo:block><#if emplLeaves?has_content><#list emplLeaves as empl><#if empl.leaveTypeId=="CL">${((empl.openingBalance+empl.adjustedDays)-(empl.availedDays))?if_exists}</#if></#list></#if></fo:block></fo:table-cell>
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
                   <#assign payRollHeaderItemList =payRollHeader.getValue().entrySet()> 	
                   	<#list benefitTypeIds as benefitType>
                    		<#assign value=0>
                    		<#if (payRollHeader.getValue()).get(benefitType)?has_content>
                    			<#assign value=(payRollHeader.getValue()).get(benefitType)>	
                    		</#if>
                    		<#if value !=0>
	                    		<fo:table-row>                      
		                    		<fo:table-cell border-style="solid">                    		
		                      			<#assign totalEarnings=(totalEarnings+(value))>                  			
		                    			<fo:block>${benefitDescMap[benefitType]?if_exists}</fo:block>                			
		                    		</fo:table-cell>                 	              		
		                    		<fo:table-cell border-style="solid"><fo:block text-align="right">${value?if_exists?string("#0")}&#160;&#160;</fo:block></fo:table-cell>
		                    	</fo:table-row>
	                    	</#if>
                    	</#list>
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
                    	<#list dedTypeIds as deductionType>
                    		<#assign dedValue=0>
                    		<#if (payRollHeader.getValue()).get(deductionType)?has_content>
                    			<#assign dedValue=(payRollHeader.getValue()).get(deductionType)>	
                    		</#if>
                    		<#if dedValue !=0>
	                    		<fo:table-row>                      
		                    		<fo:table-cell border-style="solid">                    		
		                      			<#assign totalDeductions=(totalDeductions+(dedValue))> 
		                      			<#assign instmtNo=0>
		                      			<#if InstallmentFinalMap[payRollHeader.getKey()].get(deductionType)?has_content>
		                      				<#assign instmtNo=InstallmentFinalMap[payRollHeader.getKey()].get(deductionType)?if_exists>  
		                      			</#if>                 			
		                    			<fo:block>${dedDescMap[deductionType]?if_exists}<#if instmtNo !=0>[${instmtNo?if_exists}]</#if></fo:block>                			
		                    		</fo:table-cell>                 	              		
		                    		<fo:table-cell border-style="solid"><fo:block text-align="right">${((-1)*(dedValue))?if_exists?string("#0")}&#160;&#160;</fo:block></fo:table-cell>
		                    	</fo:table-row>
	                    	</#if>
                    	</#list>
            		</fo:table-body>
            	</fo:table>
            	</fo:block>         
            	</fo:table-cell>                   		
                   </fo:table-row>
                   <fo:table-row border-style="solid">
                   		<fo:table-cell border-style="solid">
                   			
                   			<fo:block font-weight="bold" white-space-collapse="false" keep-together="always">Total Earnings :                                         <#if totalEarnings?has_content>
                   			<#assign total = totalEarnings?if_exists />
                   			<@ofbizCurrency amount=total?string("#0") /></#if></fo:block>
                   		</fo:table-cell>
                   		<fo:table-cell>
                   			<fo:block font-weight="bold" white-space-collapse="false" keep-together="always">Total Deductions :                                       <#if totalDeductions?has_content>
                   			<#assign totalamount = ((-1)*totalDeductions)?if_exists />
                   			<@ofbizCurrency amount=totalamount?string("#0")/></#if></fo:block>
                   		</fo:table-cell>
                   </fo:table-row>
                   <#assign netAmt= total-totalamount>
                	<fo:table-row>
                   		<fo:table-cell>                   			
                   			<fo:block font-weight="bold">Net Pay   :
                            	<@ofbizCurrency amount=netAmt?string("#0")/>
                          	</fo:block>                       
                   			<fo:block white-space-collapse="false" keep-together="always">(In Words:${Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble(netAmt?string("#0")), "%rupees-and-paise", locale).toUpperCase()} ONLY)</fo:block>
                   		</fo:table-cell>
                   </fo:table-row>
            		</fo:table-body>
            	</fo:table>            		          
            </fo:block>
            <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
            <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
            <fo:block text-align="center" keep-together="always" font-size="8pt">This is a computer-generated salary slip. Does not require a Signature
            </fo:block>
          </fo:flow>          
        </fo:page-sequence>       
      </#list>
    <#else>    	
		<fo:page-sequence master-reference="main">
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
   		 		<fo:block font-size="14pt">
        			No Pay Slips Found.......!
   		 		</fo:block>
			</fo:flow>
		</fo:page-sequence>		
    </#if>        
  </fo:root>
</#escape>