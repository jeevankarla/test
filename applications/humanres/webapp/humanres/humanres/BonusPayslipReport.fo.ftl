<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="10in" page-width="15in"
                     margin-left="0.5in" margin-right="0.3in"  margin-top="0.2in" margin-bottom="0.2in" >
                <fo:region-body margin-top="1.5in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
		<#if employeeWiseMap?has_content>
			<#assign slipNo = 1 >
			<#assign employeeMap=employeeWiseMap.entrySet()>
			<#list employeeMap as employeeValues>
				<#assign employeeMonthWiseMap=employeeValues.getValue().entrySet()>
                <#list employeeMonthWiseMap as employeeMonthWiseDetails>
                    <#assign accountNo =  employeeMonthWiseDetails.getValue().get("finAccountCode")>
                </#list>
				<#assign totalBasic = 0>
				<#assign totDearnessAlw = 0>
				<#assign totSpecialPay = 0>
				<#assign totFixedPay = 0>
				<#assign totBonus = 0>
				<#assign totPFAmount = 0>
				<fo:page-sequence master-reference="main">
				 	<#-- ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, employeeValues.getKey(), false)}   -->
	        		<fo:static-content font-size="10pt" font-family="Helvetica"  flow-name="xsl-region-before" font-weight="bold">        
		        		<fo:block  keep-together="always" text-align="center" font-family="Helvetica" white-space-collapse="false" font-weight="bold">&#160;      ${uiLabelMap.KMFDairyHeader}</fo:block>
						<fo:block  keep-together="always" text-align="right" font-family="Helvetica" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}                                                                                        SlipNo : ${slipNo}&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
						<#assign slipNo = slipNo + 1>
		        		<fo:block  keep-together="always" text-align="center" font-family="Helvetica" white-space-collapse="false" font-weight="bold">&#160;      </fo:block>
		        		<fo:block text-align="right" keep-together="always" white-space-collapse="false">BONUS/EXGRATIA FOR THE PERIOD FROM  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "MMM yyyy").toUpperCase()} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "MMM yyyy").toUpperCase()}                                                                                    Date : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yyyy")}&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>	 
		        		<fo:block  keep-together="always" text-align="center" font-family="Helvetica" white-space-collapse="false" font-weight="bold">&#160; </fo:block>
		        		<fo:block  keep-together="always" text-align="center" font-family="Helvetica" white-space-collapse="false" font-weight="bold">&#160; </fo:block>
		        		<fo:block font-family="Helvetica">
			        		<fo:table border-right-style = "solid" border-left-style = "solid" border-top-style = "solid">
			                	<fo:table-column column-width="513pt"/>
			                	<fo:table-column column-width="512pt"/>
			                	<#assign emplPositionAndFulfilment = delegator.findByAnd("EmplPositionAndFulfillment", {"employeePartyId" : employeeValues.getKey()})/>
            					<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : emplPositionAndFulfilment[0].emplPositionTypeId?if_exists}, true)>
				                <fo:table-body> 
		                     		<fo:table-row >
		                     			<fo:table-cell >	
			                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="10pt" line-height = "18pt">&#160;Name&#160;&#160;&#160;&#160;&#160;: ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, employeeValues.getKey(), false)}</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="10pt" line-height = "18pt">Designation&#160;: <#if designation?has_content>${designation.description?if_exists}</#if></fo:block>
			                            </fo:table-cell>
			                    	</fo:table-row>
			                    	<fo:table-row >
		                     			<fo:table-cell >	
			                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="10pt" line-height = "18pt">&#160;Emp No : ${employeeValues.getKey()}</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="10pt" line-height = "18pt">Account No : ${accountNo}</fo:block>
			                            </fo:table-cell>
			                    	</fo:table-row>
			                    </fo:table-body>
	                     	</fo:table>
	                     </fo:block>
		        	</fo:static-content>
		        	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			            <fo:block font-family="Helvetica">
			                <fo:table border-style = "solid">
			                	<fo:table-column column-width="105pt"/>
			                	<fo:table-column column-width="70pt"/>
			                	<fo:table-column column-width="70pt"/>
			                	<fo:table-column column-width="70pt"/>
			                	<fo:table-column column-width="70pt"/>
			                	<fo:table-column column-width="70pt"/>
			                	<fo:table-column column-width="70pt"/>
			                	<fo:table-column column-width="70pt"/>
			                	<fo:table-column column-width="70pt"/>
			                	<fo:table-column column-width="70pt"/>
			                	<fo:table-column column-width="70pt"/>
			                	<fo:table-column column-width="70pt"/>
			                	<fo:table-column column-width="70pt"/>
			                	<fo:table-column column-width="80pt"/>
				                <fo:table-body> 
		                     		<fo:table-row border-bottom-style = "solid">
		                     			<fo:table-cell border-right-style = "solid">	
			                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="10pt" line-height = "18pt">&#160;Month</fo:block>
			                            </fo:table-cell>
			                            <#list employeeMonthWiseMap as employeeMonthWiseDetails>
			                            	<#assign customTimePeriod = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : employeeMonthWiseDetails.getKey()}, true)>
						                    <#if customTimePeriod?has_content>
						                    	<#assign fromDateStart = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "MMM")/>
		                						<#assign thruDateEnd = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, ",yyyy")/>
						                    </#if>
			                            	<fo:table-cell border-right-style = "solid">	
				                            	<fo:block text-align="center" keep-together="always" font-weight="bold" font-size="10pt" line-height = "18pt">${fromDateStart.toUpperCase()?if_exists}</fo:block>
				                            </fo:table-cell>
			                            </#list>
			                            <fo:table-cell >	
			                            	<fo:block text-align="center" keep-together="always" font-weight="bold" font-size="10pt" line-height = "18pt">Total</fo:block>
			                            </fo:table-cell>
			                    	</fo:table-row>
			                    	<fo:table-row border-bottom-style = "solid">
		                     			<fo:table-cell border-right-style = "solid">	
			                            	<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "20pt">&#160;Basic</fo:block>
			                            </fo:table-cell>
			                            <#list employeeMonthWiseMap as employeeMonthWiseDetails>
			                            	<#assign customTimePeriod = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : employeeMonthWiseDetails.getKey()}, true)>
			                            	<#assign customTimePeriodId = employeeMonthWiseDetails.getKey()>
			                            	<fo:table-cell border-right-style = "solid">	
				                            	<fo:block text-align="right" keep-together="always" font-size="10pt" line-height = "20pt">${employeeMonthWiseDetails.getValue().get("basic")?if_exists?string("#0.00")}&#160;&#160;</fo:block>
				                           		<#assign totalBasic = totalBasic + employeeMonthWiseDetails.getValue().get("basic")>
				                            </fo:table-cell>
			                            </#list>
			                            <fo:table-cell border-right-style = "solid">	
			                            	<fo:block text-align="right" keep-together="always" font-size="10pt" line-height = "20pt">${totalBasic?if_exists?string("#0.00")}&#160;&#160;</fo:block>
			                            </fo:table-cell>
			                        </fo:table-row>
			                        <fo:table-row border-bottom-style = "solid">
		                     			<fo:table-cell border-right-style = "solid">	
			                            	<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "20pt">&#160;Additional Basic</fo:block>
			                            </fo:table-cell>
			                            <#list employeeMonthWiseMap as employeeMonthWiseDetails>
			                            	<fo:table-cell border-right-style = "solid">	
				                            	<fo:block text-align="right" keep-together="always" font-size="10pt" line-height = "20pt">0.00&#160;&#160;</fo:block>
				                            </fo:table-cell>
			                            </#list>
			                            <fo:table-cell border-right-style = "solid">	
			                            	<fo:block text-align="right" keep-together="always" font-size="10pt" line-height = "20pt">0.00&#160;&#160;</fo:block>
			                            </fo:table-cell>
			                        </fo:table-row>
			                        <fo:table-row border-bottom-style = "solid">
		                     			<fo:table-cell border-right-style = "solid">	
			                            	<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "20pt">&#160;Dearness Allowance</fo:block>
			                            </fo:table-cell>
			                            <#list employeeMonthWiseMap as employeeMonthWiseDetails>
			                            	<fo:table-cell border-right-style = "solid">	
				                            	<fo:block text-align="right" keep-together="always" font-size="10pt" line-height = "20pt">${employeeMonthWiseDetails.getValue().get("dearnessAllowance")?if_exists?string("#0.00")}&#160;&#160;</fo:block>
				                            	<#assign totDearnessAlw = totDearnessAlw + employeeMonthWiseDetails.getValue().get("dearnessAllowance")>
				                            </fo:table-cell>
			                            </#list>
			                            <fo:table-cell border-right-style = "solid">	
			                            	<fo:block text-align="right" keep-together="always" font-size="10pt" line-height = "20pt">${totDearnessAlw?if_exists?string("#0.00")}&#160;&#160;</fo:block>
			                            </fo:table-cell>
			                        </fo:table-row>
			                        <fo:table-row border-bottom-style = "solid">
		                     			<fo:table-cell border-right-style = "solid">	
			                            	<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "20pt">&#160;Interim Relief</fo:block>
			                            </fo:table-cell>
			                            <#list employeeMonthWiseMap as employeeMonthWiseDetails>
			                            	<fo:table-cell border-right-style = "solid">	
				                            	<fo:block text-align="right" keep-together="always" font-size="10pt" line-height = "20pt">0.00&#160;&#160;</fo:block>
				                            </fo:table-cell>
			                            </#list>
			                            <fo:table-cell border-right-style = "solid">	
			                            	<fo:block text-align="right" keep-together="always" font-size="10pt" line-height = "20pt">0.00&#160;&#160;</fo:block>
			                            </fo:table-cell>
			                        </fo:table-row>
			                        <fo:table-row border-bottom-style = "solid">
		                     			<fo:table-cell border-right-style = "solid">	
			                            	<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "20pt">&#160;Special Pay</fo:block>
			                            </fo:table-cell>
			                            <#list employeeMonthWiseMap as employeeMonthWiseDetails>
			                            	<fo:table-cell border-right-style = "solid">	
				                            	<fo:block text-align="right" keep-together="always" font-size="10pt" line-height = "20pt">${employeeMonthWiseDetails.getValue().get("specPay")?if_exists?string("#0.00")}&#160;&#160;</fo:block>
				                            </fo:table-cell>
				                            <#assign totSpecialPay = totSpecialPay + employeeMonthWiseDetails.getValue().get("specPay")>
			                            </#list>
			                            <fo:table-cell border-right-style = "solid">	
			                            	<fo:block text-align="right" keep-together="always" font-size="10pt" line-height = "20pt">${totSpecialPay?if_exists?string("#0.00")}&#160;&#160;</fo:block>
			                            </fo:table-cell>
			                        </fo:table-row>
			                        <fo:table-row border-bottom-style = "solid">
		                     			<fo:table-cell border-right-style = "solid">	
			                            	<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "20pt">&#160;Fixed Pay</fo:block>
			                            </fo:table-cell>
			                            <#list employeeMonthWiseMap as employeeMonthWiseDetails>
			                            	<fo:table-cell border-right-style = "solid">	
				                            	<fo:block text-align="right" keep-together="always" font-size="10pt" line-height = "20pt">${employeeMonthWiseDetails.getValue().get("fixedPay")?if_exists?string("#0.00")}&#160;&#160;</fo:block>
				                            </fo:table-cell>
				                            <#assign totFixedPay = totFixedPay + employeeMonthWiseDetails.getValue().get("fixedPay")>
			                            </#list>
			                            <fo:table-cell border-right-style = "solid">	
			                            	<fo:block text-align="right" keep-together="always" font-size="10pt" line-height = "20pt">${totFixedPay?if_exists?string("#0.00")}&#160;&#160;</fo:block>
			                            </fo:table-cell>
			                        </fo:table-row>
			                        <fo:table-row border-bottom-style = "solid">
		                     			<fo:table-cell border-right-style = "solid">	
			                            	<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "20pt">&#160;Payable Days</fo:block>
			                            </fo:table-cell>
			                            <#list employeeMonthWiseMap as employeeMonthWiseDetails>
			                            	<fo:table-cell border-right-style = "solid">	
				                            	<fo:block text-align="right" keep-together="always" font-size="10pt" line-height = "20pt">${employeeMonthWiseDetails.getValue().get("noOfPayableDays")?if_exists?string("#0.000")}&#160;&#160;</fo:block>
				                            </fo:table-cell>
			                            </#list>
			                        </fo:table-row>
			                        <fo:table-row border-bottom-style = "solid">
		                     			<fo:table-cell border-right-style = "solid">	
			                            	<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "20pt">&#160;Arrears Days</fo:block>
			                            </fo:table-cell>
			                            <#list employeeMonthWiseMap as employeeMonthWiseDetails>
			                            	<fo:table-cell border-right-style = "solid">	
				                            	<fo:block text-align="right" keep-together="always" font-size="10pt" line-height = "20pt">${employeeMonthWiseDetails.getValue().get("noOfArrearDays")?if_exists?string("#0.000")}&#160;&#160;</fo:block>
				                            </fo:table-cell>
			                            </#list>
			                        </fo:table-row>
			                        <fo:table-row border-bottom-style = "solid">
		                     			<fo:table-cell border-right-style = "solid">	
			                            	<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "20pt">&#160;Late Days</fo:block>
			                            </fo:table-cell>
			                            <#list employeeMonthWiseMap as employeeMonthWiseDetails>
			                            	<fo:table-cell border-right-style = "solid">	
				                            	<fo:block text-align="right" keep-together="always" font-size="10pt" line-height = "20pt">${employeeMonthWiseDetails.getValue().get("lossOfPayDays")?if_exists?string("#0.000")}&#160;&#160;</fo:block>
				                            </fo:table-cell>
			                            </#list>
			                        </fo:table-row>
			                        <fo:table-row border-bottom-style = "solid">
		                     			<fo:table-cell border-right-style = "solid">	
			                            	<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "20pt" font-weight="bold">&#160;TOTAL</fo:block>
			                            </fo:table-cell>
			                            <#list employeeMonthWiseMap as employeeMonthWiseDetails>
			                            	<fo:table-cell border-right-style = "solid">	
				                            	<fo:block text-align="right" keep-together="always" font-size="10pt" line-height = "20pt" font-weight="bold">${employeeMonthWiseDetails.getValue().get("totalValue")?if_exists?string("#0.00")}&#160;&#160;</fo:block>
				                            </fo:table-cell>
			                            </#list>
			                        </fo:table-row>
			                        <fo:table-row border-bottom-style = "solid">
		                     			<fo:table-cell border-right-style = "solid">	
			                            	<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "20pt" font-weight="bold">&#160;BONUS</fo:block>
			                            </fo:table-cell>
			                            <#list employeeMonthWiseMap as employeeMonthWiseDetails>
			                            	<fo:table-cell border-right-style = "solid">	
				                            	<fo:block text-align="right" keep-together="always" font-size="10pt" line-height = "20pt" font-weight="bold">${employeeMonthWiseDetails.getValue().get("bonus")?if_exists?string("#0.00")}&#160;&#160;</fo:block>
				                            </fo:table-cell>
				                            <#assign totBonus = totBonus + employeeMonthWiseDetails.getValue().get("bonus")>
			                            </#list>
			                            <#if emplBonusMap?has_content>
			                            	<#assign emplBonusDetails = emplBonusMap.entrySet()>
			                            	<#list emplBonusDetails as emplBonusVal>
			                            		<#if emplBonusVal.getKey()==employeeValues.getKey()>
						                            <fo:table-cell border-right-style = "solid">	
						                            	<fo:block text-align="right" keep-together="always" font-size="10pt" line-height = "20pt" font-weight="bold">${emplBonusVal.getValue()?if_exists?string("#0.00")}&#160;&#160;</fo:block>
						                            </fo:table-cell>
						                       	</#if>
						                  	</#list>
						              	</#if>
			                        </fo:table-row>
			                        <fo:table-row border-bottom-style = "solid">
		                     			<fo:table-cell border-right-style = "solid">	
			                            	<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "20pt" font-weight="bold">&#160;P.TAX DIFF</fo:block>
			                            </fo:table-cell>
			                            <#list employeeMonthWiseMap as employeeMonthWiseDetails>
			                            	<fo:table-cell border-right-style = "solid">	
				                            	<fo:block text-align="right" keep-together="always" font-size="10pt" line-height = "20pt" font-weight="bold"><#if employeeMonthWiseDetails.getValue().get("totalNetPfAmount")?has_content>${employeeMonthWiseDetails.getValue().get("totalNetPfAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>0.00&#160;&#160;</#if></fo:block>
				                            </fo:table-cell>
				                            <#if employeeMonthWiseDetails.getValue().get("totalNetPfAmount")?has_content>
				                            	<#assign totPFAmount = totPFAmount + employeeMonthWiseDetails.getValue().get("totalNetPfAmount")>
				                            </#if>
			                            </#list>
			                            <fo:table-cell >	
			                            	<fo:block text-align="right" keep-together="always" font-size="10pt" line-height = "20pt" font-weight="bold">${totPFAmount?if_exists?string("#0.00")}&#160;&#160;</fo:block>
			                            </fo:table-cell>
			                        </fo:table-row>
			                    </fo:table-body>
	                     	</fo:table>
	             		</fo:block>
	             		<fo:block font-family="Helvetica">
			                <fo:table border-style = "solid">
			                	<fo:table-column column-width="513pt"/>
			                	<fo:table-column column-width="512pt"/>
			                	<fo:table-body> 
	             					<fo:table-row>
		                     			<fo:table-cell>	
			                            	<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "25pt" font-weight="bold">&#160;<fo:inline text-decoration="underline">SUMMARY</fo:inline></fo:block>
			                            </fo:table-cell>
			                        </fo:table-row>
	                            	<#list emplBonusDetails as emplBonusVal>
	                            		<#if emplBonusVal.getKey()==employeeValues.getKey()>
					                        <fo:table-row>
				                     			<fo:table-cell>	
					                            	<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "25pt">&#160;GROSS BONUS PAYABLE :  ${emplBonusVal.getValue()?if_exists?string("#0.00")}</fo:block>
					                            </fo:table-cell>
					                        </fo:table-row>
					                        <fo:table-row >
				                     			<fo:table-cell>	
					                            	<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "25pt">&#160;LESS DEDUCTIONS&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; : ${totPFAmount?if_exists?string("#0.00")} </fo:block>
					                            </fo:table-cell>
					                            <fo:table-cell>	
					                            	<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "25pt">&#160;Signature : </fo:block>
					                            </fo:table-cell>
					                        </fo:table-row>
					                        <fo:table-row >
				                     			<fo:table-cell>	
					                            	<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "25pt">&#160;NET BONUS PAYABLE&#160;&#160;&#160;&#160;&#160;&#160; : ${((emplBonusVal.getValue())-totPFAmount)?if_exists?string("#0.00")}</fo:block>
					                            </fo:table-cell>
					                        </fo:table-row>
					                  	</#if>
				                  	</#list>
			                    </fo:table-body>
	                     	</fo:table>
	             		</fo:block>
	           		</fo:flow>
	  			</fo:page-sequence>
	  		</#list>
  		<#else>    	
		<fo:page-sequence master-reference="main">
			<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
   		 		<fo:block font-size="12pt">
        			No Employee Found.......!
   		 		</fo:block>
			</fo:flow>
		</fo:page-sequence>	
	</#if>
     </fo:root>
</#escape>