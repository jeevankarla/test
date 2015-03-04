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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
            margin-top="0.2in" margin-bottom=".5in" margin-left=".3in" margin-right=".5in">
        <fo:region-body margin-top="1.6in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "trabs.txt")}
<#assign lineNumber = 5>
<#assign numberOfLines = 60>
<#assign facilityNumberInPage = 0>
<#if DAArrearMap?has_content>
 <#assign partyBenefitFinalList=DAArrearMap.entrySet()>
	<#list partyBenefitFinalList as partyBenefitsMap>
	<#assign headCTP = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : customTimePeriodId}, true)>
    <#if headCTP?has_content>
    	<#assign fromDateHead = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(headCTP.fromDate, "dd/MM/yyyy")/>
		<#assign thruDateHead = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(headCTP.thruDate, "dd/MM/yyyy")/>
    </#if>
<fo:page-sequence master-reference="main" font-family="Courier,monospace"  force-page-count="no-force">					
		<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace"> 
			<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" font-size="12pt" white-space-collapse="false">${uiLabelMap.CommonPage}- <fo:page-number/> </fo:block>
    		<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace"  white-space-collapse="false"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
    		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairyHeader}</fo:block>
			<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
          	<#assign emplPositionAndFulfilment = delegator.findByAnd("EmplPositionAndFulfillment", {"employeePartyId" : partyBenefitsMap.getKey()})/>
            <#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : emplPositionAndFulfilment[0].emplPositionTypeId?if_exists}, true)>
          	<fo:block text-align="center" white-space-collapse="false" font-weight="bold"> DA ARREARS FOR THE PERIOD OF  From:${fromDateHead} To : ${thruDateHead}</fo:block>
          	<fo:block text-align="left" white-space-collapse="false" font-weight="bold">EMPLOYEE:${partyBenefitsMap.getKey()}                     </fo:block>
          	<fo:block text-align="left" white-space-collapse="false" font-weight="bold">EMPLOYEE NAME: ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyBenefitsMap.getKey(), false)}                     DESIGNATION: <#if designation?has_content>${designation.description?if_exists}</#if></fo:block>
          	<fo:block>---------------------------------------------------------------------------------------------</fo:block>
		</fo:static-content>
			<fo:flow flow-name="xsl-region-body"  font-size="10pt">
			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			<fo:block>	
    				 <fo:table  table-layout="fixed"   font-size="12pt" >                
		                <fo:table-column column-width="140pt"/>
				  		<fo:table-column column-width="100pt"/>
				  		<fo:table-column column-width="100pt"/>
				   	    <fo:table-column column-width="120pt"/>
				   	    <fo:table-column column-width="100pt"/>
				   	    <fo:table-column column-width="100pt"/>
		                <fo:table-body>
		                 <fo:table-row >  
					           <fo:table-cell >
		                            <fo:block text-align="left" white-space-collapse="false"  keep-together="always" font-weight="bold">MONTH</fo:block>
	                             </fo:table-cell>                  
                    		     <fo:table-cell >
                        			<fo:block  text-align="right" white-space-collapse="false" keep-together="always" font-weight="bold">BASIC</fo:block>
                    			 </fo:table-cell>
                    	         <fo:table-cell >
                        	        <fo:block  text-align="right" white-space-collapse="false" keep-together="always" font-weight="bold">DA DRAWN</fo:block>
                    	          </fo:table-cell>	
                                  <fo:table-cell >
                        	         <fo:block  white-space-collapse="false" text-align="right" keep-together="always" font-weight="bold">DA DUE</fo:block>
                    	         </fo:table-cell>
		                      	<fo:table-cell >
                            	         <fo:block  white-space-collapse="false" text-align="right" keep-together="always" font-weight="bold">DIFF.AMOUNT</fo:block>
                        	      </fo:table-cell>
                        	      	<fo:table-cell >
                            	         <fo:block  white-space-collapse="false" text-align="right" keep-together="always" font-weight="bold">PTAX</fo:block>
                        	      </fo:table-cell>
                 	         </fo:table-row> 
                 	         <fo:table-row>
	                         <fo:table-cell>
	                       <fo:block>---------------------------------------------------------------------------------------------</fo:block>
		                      </fo:table-cell>
	                      </fo:table-row>
	                      <#assign leaveMap = DAArrearLEMap.get(partyBenefitsMap.getKey())?if_exists>
 							<#assign  employeeamountMap=partyBenefitsMap.getValue()>
 							<#assign employeeamountList=employeeamountMap.entrySet()>
										<#assign oldDATotal=0>
										<#assign newDATotal=0>
										<#assign netDATotal=0>
										<#assign netDATotal1 = 0>
 										 <#list employeeamountList as employeeDetails>
 										 <#assign month = "">
 										 <#assign monthKey = employeeDetails.getKey()>
						                    <#assign customTimePeriod = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : monthKey}, true)>
						                    <#if customTimePeriod?has_content>
						                    	<#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "MMM")/>
		                						<#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, ",yyyy")/>
						                    </#if>
						                    <#assign month = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "MMMM")>
								             <#assign epf = employeeDetails.getValue().get("EpfAmount")?if_exists>
								             <fo:table-row >
						                        <fo:table-cell>
						                          <fo:block text-align="left" >${fromDate?if_exists}${thruDate?if_exists}</fo:block>
						                        </fo:table-cell>
						                        <fo:table-cell>
						                          <fo:block text-align="right"  >${employeeDetails.getValue().get("Basic")?if_exists?string("#0.00")}</fo:block>
						                        </fo:table-cell>
						                        <#assign oldDATotal = oldDATotal+ employeeDetails.getValue().get("oldDA")?if_exists>
						                         <fo:table-cell>
						                          <fo:block text-align="right"  >${employeeDetails.getValue().get("oldDA")?if_exists?string("#0.00")}</fo:block>
						                        </fo:table-cell>
						                        <#assign newDATotal = newDATotal+ employeeDetails.getValue().get("newDA")?if_exists>
						                        <fo:table-cell>
						                          <fo:block text-align="right"   keep-together="always">${employeeDetails.getValue().get("newDA")?if_exists?string("#0.00")}</fo:block>
						                        </fo:table-cell>
						                        <#assign netDATotal = netDATotal+ employeeDetails.getValue().get("netDA")?if_exists>
						                        <#assign netDATotal1 = netDATotal1+employeeDetails.getValue().get("netDA")?if_exists>
						                        <fo:table-cell>
						                          <fo:block text-align="right"  >${employeeDetails.getValue().get("netDA")?if_exists?string("#0.00")}</fo:block>
						                        </fo:table-cell>
						                         <fo:table-cell>
						                          <fo:block text-align="right"  >0.00</fo:block>
						                        </fo:table-cell>
						                      </fo:table-row>
						                      <#if leaveMap?has_content>
						                    		<#assign leaveEncashList = leaveMap.entrySet()?if_exists>
						                    		<#list leaveEncashList as leaveEnCash>
						                     			 <#assign leaveEncDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(leaveEnCash.getKey(), "MMMM")/>
						                      				<#if month == leaveEncDate>
									                      		<fo:table-row >
											                        <fo:table-cell>
											                          <fo:block text-align="left" keep-together="always">${leaveEncDate?if_exists}(Leave Encash)</fo:block>
											                        </fo:table-cell>
											                        <fo:table-cell>
											                          <fo:block text-align="right"  >${leaveEnCash.getValue().get("Basic1")?if_exists?string("#0.00")}</fo:block>
											                        </fo:table-cell>
											                        <#assign oldDATotal = oldDATotal+ leaveEnCash.getValue().get("oldDA1")?if_exists>
											                         <fo:table-cell>
											                          <fo:block text-align="right"  >${leaveEnCash.getValue().get("oldDA1")?if_exists?string("#0.00")}</fo:block>
											                        </fo:table-cell>
											                        <#assign newDATotal = newDATotal+ leaveEnCash.getValue().get("newDA1")?if_exists>
											                        <fo:table-cell>
											                          <fo:block text-align="right"   keep-together="always">${leaveEnCash.getValue().get("newDA1")?if_exists?string("#0.00")}</fo:block>
											                        </fo:table-cell>
											                        <#assign netDATotal1 = netDATotal1+ leaveEnCash.getValue().get("netDA1")?if_exists>
											                        <fo:table-cell>
											                          <fo:block text-align="right"  >${leaveEnCash.getValue().get("netDA1")?if_exists?string("#0.00")}</fo:block>
											                        </fo:table-cell>
											                         <fo:table-cell>
											                          <fo:block text-align="right"  >0.00</fo:block>
											                        </fo:table-cell>
									                      </fo:table-row>
						                      			</#if>
						                      		</#list>
						                      	</#if>
						                      </#list>
						                      <fo:table-row>
						                         <fo:table-cell>
						                       			<fo:block>---------------------------------------------------------------------------------------------</fo:block>
			 				                      </fo:table-cell>
						                      </fo:table-row>
											<fo:table-row font-weight= "bold">
						                        <fo:table-cell>
						                          <fo:block text-align="left" font-weight= "bold">DA DIFFERENCE</fo:block>
						                        </fo:table-cell>
						                        <fo:table-cell>
						                          <fo:block text-align="right"  ></fo:block>
						                        </fo:table-cell>
						                         <fo:table-cell>
						                          <fo:block text-align="right"  >${oldDATotal?if_exists?string("#0.00")}</fo:block>
						                        </fo:table-cell>
						                        <fo:table-cell>
						                          <fo:block text-align="right"   keep-together="always">${newDATotal?if_exists?string("#0.00")}</fo:block>
						                        </fo:table-cell>
						                        <fo:table-cell>
						                          <fo:block text-align="right"  >${netDATotal1?if_exists?string("#0.00")}</fo:block>
						                        </fo:table-cell>
						                         <fo:table-cell>
						                          <fo:block text-align="right"  >0.00</fo:block>
						                        </fo:table-cell>
						                      </fo:table-row>
						                      
			                       <fo:table-row>
			                         <fo:table-cell>
			                       			<fo:block>---------------------------------------------------------------------------------------------</fo:block>
 				                      </fo:table-cell>
			                      </fo:table-row>
			                       <fo:table-row>
			                       <#assign epf = employeeDetails.getValue().get("EpfAmount")?if_exists>
			                       <fo:table-cell>
						                          <fo:block text-align="right"  ></fo:block>
						                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="right"  font-weight="bold" >PTAX  = 0.00 </fo:block>
			                          <fo:block text-align="right"  font-weight="bold" >ESI   = 0.00</fo:block>
			                          <fo:block text-align="right"  font-weight="bold" >		EPP    = ${epf?if_exists?string("#0.00")}</fo:block>
			                          <fo:block text-align="right"  font-weight="bold" >NSC   = 0.00 </fo:block>
			                          <fo:block text-align="right"  ></fo:block>
			                          <fo:block text-align="right" ></fo:block>
			                        </fo:table-cell>
			                      </fo:table-row>
			                      <#assign net = netDATotal1-epf>
			                      <fo:table-row>
			                       <fo:table-cell>
						                   <fo:block text-align="right"  ></fo:block>
						          </fo:table-cell>
						          <fo:table-cell>
						                   <fo:block text-align="right"  ></fo:block>
						          </fo:table-cell>
						          <fo:table-cell>
						                   <fo:block text-align="right"  ></fo:block>
						          </fo:table-cell>
						          <fo:table-cell>
						                   <fo:block text-align="right"  ></fo:block>
						          </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="center"  keep-together= "always" font-weight="bold" >Net Amount = ${net?if_exists?string("#0.00")}</fo:block>
			                        </fo:table-cell>
			                      </fo:table-row>
			                      <fo:table-row>
			                         <fo:table-cell>
			                       <fo:block>---------------------------------------------------------------------------------------------</fo:block>
 				                      </fo:table-cell>
			                      </fo:table-row>
			                      <fo:table-row> 
						 	         <fo:table-cell >   						
							 	         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
								 </fo:table-row>
								 <fo:table-row> 
						 	         <fo:table-cell >   						
							 	         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
								 </fo:table-row>
								 <fo:table-row> 
						 	         <fo:table-cell >   						
							 	         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
								 </fo:table-row>
								 <fo:table-row> 
						 	         <fo:table-cell >   						
							 	         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
								 </fo:table-row>
			                      <fo:table-row> 
							      <fo:table-cell number-columns-spanned="2" >   						
							 	         <fo:block text-align="left" white-space-collapse="false" font-size="12pt" keep-together="always" font-weight="bold">Signature(ACCNT ASST)</fo:block>
							 	   </fo:table-cell>
						 	         <fo:table-cell number-columns-spanned="3">   							
							 	         <fo:block text-align="left" white-space-collapse="false" font-size="12pt"  keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Signature(D.M.F)</fo:block>
							 	   </fo:table-cell>
							 	   <fo:table-cell >   						
							 	         <fo:block text-align="right" white-space-collapse="false" font-size="12pt"  keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Signature(PRE AUDIT)</fo:block>
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
    	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
       		 <fo:block font-size="9pt">
            	${uiLabelMap.OrderNoOrderFound}.
       		 </fo:block>
    	</fo:flow>
	</fo:page-sequence>
</#if>						
</fo:root>
</#escape>