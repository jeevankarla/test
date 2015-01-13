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
        ${setRequestAttribute("OUTPUT_FILENAME", "ItEarningsReport.pdf")}
        <#if errorMessage?has_content>
	<fo:page-sequence master-reference="main">
	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	   <fo:block font-size="14pt">
	           ${errorMessage}.
        </fo:block>
	</fo:flow>
	</fo:page-sequence>	
	<#else>
       <#if partyBenefitFinalMap?has_content>
       <#assign partyBenefitFinalList = partyBenefitFinalMap.entrySet()>
       <#list partyBenefitFinalList as partyBenefitsMap>
       <#assign employeeId = partyBenefitsMap.getKey()>
	        <fo:page-sequence master-reference="main" font-size="12pt">	
	        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" font-size="12pt" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${uiLabelMap.CommonPage}- <fo:page-number/> </fo:block>
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
                	<fo:block text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold">DETAILS OF SALARY EARNED FROM ${fromDate?if_exists} TO ${thruDate?if_exists}</fo:block>
          			<#assign emplPositionAndFulfilment = delegator.findByAnd("EmplPositionAndFulfillment", {"employeePartyId" : employeeId})/>
          			<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : emplPositionAndFulfilment[0].emplPositionTypeId?if_exists}, true)>
          			<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
          			<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">EMPLOYEE NAME : ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, employeeId, false)}                     EMPLOYEE ID:${employeeId}              												DESIGNATION: <#if designation?has_content>${designation.description?if_exists}</#if></fo:block>
          			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
          			<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">(A)SALARY EARNINGS</fo:block>
          			<fo:block>------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            	    <fo:block text-align="left" font-weight="bold" font-size="12pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">MONTH																		BASIC  						DA  							HRA 								CNV 							CCA 						IR			    OTHERS      DA/IR ARREARS 		     BONUS    			TOTAL</fo:block>
	        		<fo:block>------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>	
            	</fo:static-content>	        	
	        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
        			<fo:block>
        				<fo:table>
		                    <fo:table-column column-width="50pt"/>
		                    <fo:table-column column-width="160pt"/>
		                    <fo:table-column column-width="80pt"/> 
		               	    <fo:table-column column-width="100pt"/>
		            		<fo:table-column column-width="75pt"/> 		
		            		<fo:table-column column-width="80pt"/>
		            		<fo:table-column column-width="60pt"/>
		            		<fo:table-column column-width="85pt"/>
		            		<fo:table-column column-width="110pt"/>
		            		<fo:table-column column-width="110pt"/>
		            		<fo:table-column column-width="100pt"/>
		                    <fo:table-body>
		                    
	                    	<#assign totalBasic = 0>
	                    	<#assign totalDA = 0>
	                    	<#assign totalHRA = 0>
	                    	<#assign totalConvey = 0>
	                    	<#assign totalCityComp = 0>
	                    	<#assign totalBonus = 0>
	                    	<#assign totalDADAAmount = 0>
	                    	<#assign totalOthers = 0>
	                    	<#assign totalBenefits = 0>
	                    	
	                    	<#assign totalLESalary = 0>
			                <#assign totalLEDAAmount = 0>
			                <#assign totalLEHRAAmount = 0>
			                <#assign totalLECCAmount = 0>
			                <#assign totalLESpecPay = 0>
			                
			                <#assign totalTESalary = 0>
			                <#assign totalTEDAAmount = 0>
			                <#assign totalTEHRAAmount = 0>
			                <#assign totalTECCAmount = 0>
			                <#assign totalTESpecPay = 0>
			                
			                <#assign totalSBESalary = 0>
			                <#assign totalSBEDAAmount = 0>
			                <#assign totalSBEHRAAmount = 0>
			                <#assign totalSBECCAmount = 0>
			                <#assign totalSBESpecPay = 0>
	                    	
			                    <#assign partyBenefitList = partyBenefitsMap.getValue().entrySet()>
			                    <#list partyBenefitList as partyBenefits>
			                    	<#assign basic = 0>
			                    	<#assign attendanceBonus = 0>
			                    	<#assign cityComp =0>
			                    	<#assign convey =0>
			                    	<#assign coldAllowance =0>
			                    	<#assign dearnessAllowance =0>
			                    	<#assign holidayAllowance =0>
			                    	<#assign houseRentAllowance =0>
			                    	<#assign personalPay =0>
			                        <#assign secndSatDay =0>
			                        <#assign shift =0>
			                        <#assign washing =0>
			                        <#assign bonus =0>
			                        <#assign DADAAmount=0>
			                        <#assign others = 0>
			                        <#assign benefits = 0>
			                        
		                         	<#assign LESalary =0>
		                         	<#assign LEDAAmount =0>
		                            <#assign LEHRAAmount =0>
		                            <#assign LECCAmount =0>
		                            <#assign LESpecPay = 0>
		                            <#assign leaveEncash = 0>
		                            
		                            <#assign TESalary =0>
			                        <#assign TEDAAmount =0>
			                        <#assign TEHRAAmount =0>
			                        <#assign TECCAmount =0>
			                        <#assign TESpecPay =0>
			                        <#assign TE = 0>
		                            
		                            <#assign SBESalary =0>
			                        <#assign SBEDAAmount =0>
			                        <#assign SBEHRAAmount =0>
			                        <#assign SBECCAmount =0>
			                        <#assign SBESpecPay =0>
			                        <#assign SBE = 0>
			                    
				                    <#assign monthKey = partyBenefits.getKey()>
				                    
				                    <#assign customTimePeriod = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : monthKey}, true)>
				                    <#if customTimePeriod?has_content>
				                    	<#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "MMM(dd")/>
                						<#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd),yyyy")/>
				                    </#if>
				                    
				                    <#assign basic = partyBenefits.getValue().get("basic")?if_exists>
				                    <#assign attendanceBonus = partyBenefits.getValue().get("attendanceBonus")?if_exists>
				                    <#assign cityComp = partyBenefits.getValue().get("cityComp")?if_exists>
				                    <#assign convey = partyBenefits.getValue().get("convey")?if_exists>
				                    <#assign coldAllowance = partyBenefits.getValue().get("coldAllowance")?if_exists>
				                    <#assign dearnessAllowance = partyBenefits.getValue().get("dearnessAllowance")?if_exists>
				                    <#assign holidayAllowance = partyBenefits.getValue().get("holidayAllowance")?if_exists>
				                    <#assign houseRentAllowance = partyBenefits.getValue().get("houseRentAllowance")?if_exists>
				                    <#assign personalPay = partyBenefits.getValue().get("personalPay")?if_exists>
				                    <#assign secndSatDay = partyBenefits.getValue().get("secndSatDay")?if_exists>
				                    <#assign shift = partyBenefits.getValue().get("shift")?if_exists>
				                    <#assign washing = partyBenefits.getValue().get("washing")?if_exists>
				                    <#assign bonus = partyBenefits.getValue().get("bonus")?if_exists>
				                    <#assign DADAAmount = partyBenefits.getValue().get("DADAAmount")?if_exists>
				                    <#assign others = partyBenefits.getValue().get("others")?if_exists>
			                    	<#assign benefits = partyBenefits.getValue().get("totalBenefits")?if_exists>
			                    	
			                    	<#assign LESalary = partyBenefits.getValue().get("LESalary")?if_exists>
			                    	<#assign LEDAAmount = partyBenefits.getValue().get("LEDAAmount")?if_exists>
			                    	<#assign LEHRAAmount = partyBenefits.getValue().get("LEHRAAmount")?if_exists>
			                    	<#assign LECCAmount = partyBenefits.getValue().get("LECCAmount")?if_exists>
			                    	<#assign LESpecPay = partyBenefits.getValue().get("LESpecPay")?if_exists>
			                    	
			                    	<#assign TESalary = partyBenefits.getValue().get("TESalary")?if_exists>
			                    	<#assign TEDAAmount = partyBenefits.getValue().get("TEDAAmount")?if_exists>
			                    	<#assign TEHRAAmount = partyBenefits.getValue().get("TEHRAAmount")?if_exists>
			                    	<#assign TECCAmount = partyBenefits.getValue().get("TECCAmount")?if_exists>
			                    	<#assign TESpecPay = partyBenefits.getValue().get("TESpecPay")?if_exists>
			                    	
			                    	
			                    	<#assign SBESalary = partyBenefits.getValue().get("SBESalary")?if_exists>
			                    	<#assign SBEDAAmount = partyBenefits.getValue().get("SBEDAAmount")?if_exists>
			                    	<#assign SBEHRAAmount = partyBenefits.getValue().get("SBEHRAAmount")?if_exists>
			                    	<#assign SBECCAmount = partyBenefits.getValue().get("SBECCAmount")?if_exists>
			                    	<#assign SBESpecPay = partyBenefits.getValue().get("SBESpecPay")?if_exists>
			                    	
			                    	
			                    	<#assign totalLESalary = totalLESalary + LESalary>
			                    	<#assign totalLEDAAmount = totalLEDAAmount + LEDAAmount>
			                    	<#assign totalLEHRAAmount = totalLEHRAAmount + LEHRAAmount>
			                    	<#assign totalLECCAmount = totalLECCAmount + LECCAmount>
			                    	<#assign totalLESpecPay = totalLESpecPay + LESpecPay>
			                    	
			                    	<#assign totalTESalary = totalTESalary + TESalary>
			                    	<#assign totalTEDAAmount = totalTEDAAmount + TEDAAmount>
			                    	<#assign totalTEHRAAmount = totalTEHRAAmount + TEHRAAmount>
			                    	<#assign totalTECCAmount = totalTECCAmount + TECCAmount>
			                    	<#assign totalTESpecPay = totalTESpecPay + TESpecPay>
			                    	
			                    	<#assign totalSBESalary = totalSBESalary + SBESalary>
			                    	<#assign totalSBEDAAmount = totalSBEDAAmount + SBEDAAmount>
			                    	<#assign totalSBEHRAAmount = totalSBEHRAAmount + SBEHRAAmount>
			                    	<#assign totalSBECCAmount = totalSBECCAmount + SBECCAmount>
			                    	<#assign totalSBESpecPay = totalSBESpecPay + SBESpecPay>
			                    	
			                    	
			                    	<#assign leaveEncash = LESalary+LEDAAmount+LEHRAAmount+LECCAmount+LESpecPay>
			                    	<#assign TE = TESalary+TEDAAmount+TEHRAAmount+TECCAmount+TESpecPay>
			                    	<#assign SBE = SBESalary+SBEDAAmount+SBEHRAAmount+SBECCAmount+SBESpecPay>
			                    	
			                    	<#assign totalBasic = totalBasic+LESalary+TESalary+SBESalary+basic>
			                    	<#assign totalDA = totalDA + LEDAAmount+TEDAAmount+SBEDAAmount+ dearnessAllowance>
			                    	<#assign totalHRA = totalHRA + LEHRAAmount+TEHRAAmount+SBEHRAAmount+ houseRentAllowance>
			                    	<#assign totalConvey = totalConvey + convey>
			                    	<#assign totalCityComp = totalCityComp + LECCAmount+TECCAmount+SBECCAmount+cityComp>
			                    	<#assign totalBonus = totalBonus + bonus>
			                    	<#assign totalDADAAmount = totalDADAAmount + DADAAmount>
			                    	<#assign totalOthers = totalOthers + others+LESpecPay+TESpecPay+SBESpecPay>
			                    	<#assign totalBenefits = totalBenefits + leaveEncash+ benefits+TE+SBE>
			                    	
			                    	
       							<fo:table-row>
       								<fo:table-cell>
							            <fo:block  keep-together="always" font-weight = "bold" text-align="left" font-size="12pt" white-space-collapse="false" >${fromDate?if_exists}-${thruDate?if_exists}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${basic?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${dearnessAllowance?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${houseRentAllowance?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${convey?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${cityComp?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${others?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${DADAAmount?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${bonus?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${benefits?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							    </fo:table-row>
							    <#if (LESalary?has_content && LESalary!=0) || (LEDAAmount?has_content && LEDAAmount!=0) || (LEHRAAmount?has_content && LEHRAAmount!=0) || (LECCAmount?has_content && LECCAmount!=0) || (LESpecPay?has_content && LESpecPay!=0)>
							    	<fo:table-row>
       								<fo:table-cell>
							            <fo:block  keep-together="always" font-weight = "bold" text-align="left" font-size="12pt" white-space-collapse="false" >Leave Encashment</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${LESalary?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${LEDAAmount?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${LEHRAAmount?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${LECCAmount?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${LESpecPay?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${leaveEncash?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							    </fo:table-row>
							    </#if>
							    <#if (TESalary?has_content && TESalary!=0) || (TEDAAmount?has_content && TEDAAmount!=0) || (TEHRAAmount?has_content && TEHRAAmount!=0) || (TECCAmount?has_content && TECCAmount!=0) || (TESpecPay?has_content && TESpecPay!=0)>
							    	<fo:table-row>
       								<fo:table-cell>
							            <fo:block  keep-together="always" font-weight = "bold" text-align="left" font-size="12pt" white-space-collapse="false" >Transfer:</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${TESalary?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${TEDAAmount?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${TEHRAAmount?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${TECCAmount?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${TESpecPay?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${TE?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							    </fo:table-row>
							    </#if>
							    <#if (SBESalary?has_content && SBESalary!=0) || (SBEDAAmount?has_content && SBEDAAmount!=0) || (SBEHRAAmount?has_content && SBEHRAAmount!=0) || (SBECCAmount?has_content && SBECCAmount!=0) || (SBESpecPay?has_content && SBESpecPay!=0)>
							    	<fo:table-row>
       								<fo:table-cell>
							            <fo:block  keep-together="always" font-weight = "bold" text-align="left" font-size="12pt" white-space-collapse="false" >Supplementary Bill:</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${SBESalary?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${SBEDAAmount?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${SBEHRAAmount?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${SBECCAmount?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${SBESpecPay?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${SBE?if_exists?string("#0.00")}</fo:block>  
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
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${totalBasic?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${totalDA?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${totalHRA?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${totalConvey?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${totalCityComp?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >0.00</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${totalOthers?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${totalDADAAmount?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${totalBonus?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${totalBenefits?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							    </fo:table-row>
							    <fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
          						</fo:table-row>
          						<#assign totalBasicDA = 0>
          						<#assign othersTotal = 0>
          						<#assign totalBasicDA = totalBasic+totalDA>
          						<#assign othersTotal = totalHRA+totalConvey+totalCityComp+totalOthers>
          						<fo:table-row font-weight= "bold">
       								<fo:table-cell>
							            <fo:block  keep-together="always" font-weight = "bold" text-align="left" font-size="12pt" white-space-collapse="false" >TOTAL(BASIC+DA)</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" > = ${totalBasicDA?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" ></fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >OTHERS</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >= ${othersTotal?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" ></fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" ></fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" ></fo:block>  
							         </fo:table-cell>
							    </fo:table-row>
							    <fo:table-row> 
						 	         <fo:table-cell >   						
							 	         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
								 </fo:table-row>
							    <fo:table-row font-weight= "bold">
       								<fo:table-cell>
							            <fo:block  keep-together="always" font-weight = "bold" text-align="left" font-size="12pt" white-space-collapse="false" ></fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" ></fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" ></fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >GROSS SALARY  = </fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" >${totalBenefits?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" ></fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" ></fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" ></fo:block>  
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
							 	   <fo:table-cell number-columns-spanned="5">   						
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
	            	${uiLabelMap.NoOrdersFound}.
	       		 </fo:block>
		</fo:flow>
	</fo:page-sequence>
		</#if>   
 </#if>
 </fo:root>
</#escape>