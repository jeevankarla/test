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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-left=".3in" margin-right=".3in" margin-top=".2in">
                <fo:region-body margin-top="2.2in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "ItDeductionReport.pdf")}
        <#if errorMessage?has_content>
	<fo:page-sequence master-reference="main">
	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	   <fo:block font-size="14pt">
	           ${errorMessage}.
        </fo:block>
	</fo:flow>
	</fo:page-sequence>	
	<#else>
       <#if partyDeductionFinalMap?has_content>
       		<#assign partyDeductionFinalList = partyDeductionFinalMap.entrySet()>
       		<#list partyDeductionFinalList as partyDeductionsMap>
       			<#assign employeeId = partyDeductionsMap.getKey()>
	        <fo:page-sequence master-reference="main" font-size="12pt">	
	        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" font-size="12pt" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${uiLabelMap.CommonPage}- <fo:page-number/> </fo:block>
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
                	<fo:block text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold">DETAILS OF RECOVERY(SAVINGS) AND ALSO OWN SAVINGS FROM ${fromDate?if_exists} TO ${thruDate?if_exists}</fo:block>
          			<#assign emplPositionAndFulfilment = delegator.findByAnd("EmplPositionAndFulfillment", {"employeePartyId" : employeeId})/>
          			<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : emplPositionAndFulfilment[0].emplPositionTypeId?if_exists}, true)>
          			<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               								&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
          			<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">EMPLOYEE NAME : ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, employeeId, false)}                     			 EMPLOYEE ID:${employeeId}              																DESIGNATION: <#if designation?has_content>${designation.description?if_exists}</#if></fo:block>
          			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
          			<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">(B)DEDUCTIONS FROM SALARY RECOVERY</fo:block>
          			<fo:block>------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            	    <fo:block text-align="left" font-weight="bold" font-size="12pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">MONTH																		EPF 							VPF  					GSLS 					LICP 				FRF/NSC 			PPF/GSAS    HBA/CANF/HDFC/HBAC/H			OTHERS 		TOTAL   		ITAX   			PTAX</fo:block>
	        		<fo:block>------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>	
            	</fo:static-content>	        	
	        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
        			<fo:block>
        				<fo:table>
		                    <fo:table-column column-width="50pt"/>
		                    <fo:table-column column-width="160pt"/>
		                    <fo:table-column column-width="60pt"/> 
		               	    <fo:table-column column-width="85pt"/>
		            		<fo:table-column column-width="70pt"/> 		
		            		<fo:table-column column-width="70pt"/>
		            		<fo:table-column column-width="80pt"/>
		            		<fo:table-column column-width="120pt"/>
		            		<fo:table-column column-width="125pt"/>
		            		<fo:table-column column-width="80pt"/>
		            		<fo:table-column column-width="70pt"/>
		            		<fo:table-column column-width="70pt"/>
		            		<fo:table-column column-width="80pt"/>
		                    <fo:table-body>
		                    
	                    	<#assign totalEpf = 0>
	                    	<#assign totalVpf = 0>
	                    	<#assign totalGsls = 0>
	                    	<#assign totalLicp = 0>
	                    	<#assign totalFRFNSC = 0>
	                    	<#assign totalPPFGSAS = 0>
	                    	<#assign totalExterLoan = 0>
	                    	<#assign totalDeductions = 0>
	                    	<#assign totalIncomeTax = 0>
	                    	<#assign totalPrfTax = 0>
	                    	
	                    	<#assign totalTEEmpProFund = 0>
	                    	<#assign totalTEIncTax = 0>
	                    	
	                    	<#assign totalSBEEmpProFund = 0>
	                    	<#assign totalSBEIncTax = 0>
	                    	<#assign totalSBEPrTax = 0>
	                    	<#assign totalSBEInsurance = 0>
	                    	<#assign totalOthersDed = 0>
	                    	
			                <#assign partyDeductionList = partyDeductionsMap.getValue().entrySet()>
			                    <#list partyDeductionList as partyDeductions>
			                    	<#assign epf = 0>
				                    <#assign vpf = 0>
				                    <#assign gsls = 0>
				                    <#assign licp = 0>
				                    <#assign incomeTax = 0>
				                    <#assign prfTax = 0>
				                    <#assign fRFNSC = 0>
				                    <#assign pPFGSAS = 0>
				                    <#assign exterLoan = 0>
			                    	<#assign deductions = 0>
			                    	
			                    	<#assign TEEmpProFund = 0>
			                    	<#assign TEIncTax = 0>
			                    	
			                    	<#assign SBEEmpProFund = 0>
			                    	<#assign SBEIncTax = 0>
			                    	<#assign SBEPrTax = 0>
			                    	<#assign SBEInsurance = 0>
			                    	<#assign SBEFest=0>
			                    	<#assign othersDed = 0>
			                    	<#assign SBEDeductions = 0>
			                    	
			                    	<#assign DAAREmpProFund = 0>
			                    
				                    <#assign monthKey = partyDeductions.getKey()>
				                    
				                    <#assign customTimePeriod = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : monthKey}, true)>
				                    <#if customTimePeriod?has_content>
				                    	<#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "MMM(dd")/>
                						<#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd),yyyy")/>
				                    </#if>
				                    
				                    <#assign epf = partyDeductions.getValue().get("epf")?if_exists>
				                    <#assign vpf = partyDeductions.getValue().get("vpf")?if_exists>
				                    <#assign gsls = partyDeductions.getValue().get("gsls")?if_exists>
				                    <#assign licp = partyDeductions.getValue().get("licp")?if_exists>
				                    <#assign incomeTax = partyDeductions.getValue().get("incomeTax")?if_exists>
				                    <#assign prfTax = partyDeductions.getValue().get("prfTax")?if_exists>
				                    <#assign fRFNSC = partyDeductions.getValue().get("totalFRFNSC")?if_exists>
				                    <#assign pPFGSAS = partyDeductions.getValue().get("totalPPFGSAS")?if_exists>
				                    <#assign exterLoan = partyDeductions.getValue().get("totalExterLoan")?if_exists>
			                    	<#assign deductions = partyDeductions.getValue().get("totalDeductions")?if_exists>
			                    	
			                    	
			                    	<#assign TEEmpProFund = partyDeductions.getValue().get("TEEmpProFund")?if_exists>
			                    	<#assign TEIncTax = partyDeductions.getValue().get("TEIncTax")?if_exists>
			                    	
			                    	<#assign SBEEmpProFund = partyDeductions.getValue().get("SBEEmpProFund")?if_exists>
			                    	<#assign SBEIncTax = partyDeductions.getValue().get("SBEIncTax")?if_exists>
			                    	<#assign SBEPrTax = partyDeductions.getValue().get("SBEPrTax")?if_exists>
			                    	<#assign SBEGrSav = partyDeductions.getValue().get("SBEGrSav")?if_exists>
			                    	<#assign SBEInsurance = partyDeductions.getValue().get("SBEInsurance")?if_exists>
			                    	<#assign SBEFest = partyDeductions.getValue().get("SBEFest")?if_exists>
			                    	<#assign othersDed = partyDeductions.getValue().get("othersDed")?if_exists>
			                    	
			                    	
			                    	<#assign DAAREmpProFund = partyDeductions.getValue().get("DAAREmpProFund")?if_exists>
			                    	
			                    	<#assign SBEDeductions = SBEEmpProFund+SBEInsurance+othersDed>
			                    	
			                    	<#assign totalEpf = totalEpf + epf + TEEmpProFund + SBEEmpProFund+DAAREmpProFund>
			                    	<#assign totalVpf = totalVpf + vpf>
			                    	<#assign totalGsls = totalGsls + gsls+SBEGrSav>
			                    	<#assign totalLicp = totalLicp + licp+SBEInsurance>
			                    	<#assign totalIncomeTax = totalIncomeTax + incomeTax+TEIncTax+SBEIncTax>
			                    	<#assign totalPrfTax = totalPrfTax + prfTax+SBEPrTax>
			                    	<#assign totalFRFNSC = totalFRFNSC + fRFNSC>
			                    	<#assign totalPPFGSAS = totalPPFGSAS + pPFGSAS>
			                    	<#assign totalExterLoan = totalExterLoan + exterLoan>
			                    	<#assign totalDeductions = totalDeductions + deductions+DAAREmpProFund+SBEDeductions+TEEmpProFund>
			                    	
			                    	<#assign totalOthersDed = totalOthersDed + othersDed>
			                    	
			                    	
			                    	
       							<fo:table-row>
       								<fo:table-cell>
							            <fo:block  keep-together="always" font-weight = "bold" text-align="left" font-size="12pt" white-space-collapse="false" >${fromDate?if_exists}-${thruDate?if_exists}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${epf?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${vpf?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${gsls?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${licp?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${fRFNSC?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${pPFGSAS?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${exterLoan?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${deductions?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${incomeTax?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${prfTax?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							    </fo:table-row>
							    <#if (TEEmpProFund?has_content && TEEmpProFund!=0) || (TEIncTax?has_content && TEIncTax!=0)>
							    <fo:table-row>
       								<fo:table-cell>
							            <fo:block  keep-together="always" font-weight = "bold" text-align="left" font-size="12pt" white-space-collapse="false" >Transfer:</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${TEEmpProFund?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${TEEmpProFund?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${TEIncTax?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							    </fo:table-row>
							    </#if>
							    <#if (SBEEmpProFund?has_content && SBEEmpProFund!=0) || (SBEIncTax?has_content && SBEIncTax!=0) || (SBEPrTax?has_content && SBEPrTax!=0) || (SBEInsurance?has_content && SBEInsurance!=0) || (SBEGrSav?has_content && SBEGrSav!=0) || (SBEFest?has_content && SBEFest!=0) || (othersDed?has_content && othersDed!=0)>
							    <fo:table-row>
       								<fo:table-cell>
							            <fo:block  keep-together="always" font-weight = "bold" text-align="left" font-size="12pt" white-space-collapse="false" >Supplementary Bill:</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${SBEEmpProFund?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${SBEGrSav?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${SBEInsurance?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${SBEFest?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${othersDed?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${SBEDeductions?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${SBEIncTax?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${SBEPrTax?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							    </fo:table-row>
							    </#if>
							    <#if (DAAREmpProFund?has_content && DAAREmpProFund!=0)>
							    <fo:table-row>
       								<fo:table-cell>
							            <fo:block  keep-together="always" font-weight = "bold" text-align="left" font-size="12pt" white-space-collapse="false" >DA Arrears:</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${DAAREmpProFund?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${DAAREmpProFund?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							    </fo:table-row>
							    </#if>
							    </#list>
							    <fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
          						</fo:table-row>
							    <fo:table-row font-weight= "bold">
       								<fo:table-cell>
							            <fo:block  keep-together="always" font-weight = "bold" text-align="left" font-size="12pt" white-space-collapse="false" >Grand Total</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${totalEpf?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${totalVpf?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${totalGsls?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${totalLicp?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${totalFRFNSC?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${totalPPFGSAS?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${totalExterLoan?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							          <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${totalOthersDed?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${totalDeductions?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${totalIncomeTax?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${totalPrfTax?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							    </fo:table-row>
							    <fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
          						</fo:table-row>
							    <fo:table-row> 
							      <fo:table-cell>   						
									<fo:block keep-together="always" font-weight = "bold" font-style="italic">*All figures are in Rupees.</fo:block>
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
							      <fo:table-cell number-columns-spanned="3" >   						
							 	         <fo:block text-align="left" white-space-collapse="false" font-size="12pt" keep-together="always" font-weight="bold">Signature(ACCNT ASST)</fo:block>
							 	   </fo:table-cell>
						 	         <fo:table-cell number-columns-spanned="3">   						
							 	         <fo:block text-align="right" white-space-collapse="false" font-size="12pt"  keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Signature(D.M.F)</fo:block>
							 	   </fo:table-cell>
							 	   <fo:table-cell number-columns-spanned="3">   						
							 	         <fo:block text-align="right" white-space-collapse="false" font-size="12pt"  keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;Signature(PRE AUDIT)</fo:block>
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
	            	No records found.
	       		 </fo:block>
		</fo:flow>
	</fo:page-sequence>
		</#if>   
 </#if>
 </fo:root>
</#escape>