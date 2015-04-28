<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="11in" page-width="10in"
                     margin-left="0.6in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.2in" >
                <fo:region-body margin-top="0.1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "LICPolicyDetailsEmployeeWise.pdf")}
	<#if finalEmployeeMap?has_content>
		<fo:page-sequence master-reference="main">
    		<fo:flow flow-name="xsl-region-body" font-family="Arial">
    			<#assign employeeDetailsMap = finalEmployeeMap.entrySet()>
    			<#list employeeDetailsMap as employeeValues>
    				<#assign totalExtentAlw = 0>
    				<#assign balance = 0>
    				<#assign totalEmployeeEarnings = 0>
    				<#if employeeValues.getValue().get("totalEarnings") ?has_content>
    				<#if employeeValues.getValue().get("totalEarnings") != 0>
    				<fo:block font-family="Arial">
		            	<fo:table>
		                    <fo:table-column column-width="640pt"/>
							<fo:table-body> 
	                 			<fo:table-row >
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" font-weight = "bold">FORM NO.16</fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
	                           	<fo:table-row >
                           			<fo:table-cell border-style = "solid">
                           				<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" font-weight = "bold">[See rule 31(1)(a)]</fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
	                           	<fo:table-row >
	                           		<fo:table-cell border-style = "solid">
                           				<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" font-weight = "bold">PART A</fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
	                           	<fo:table-row >
	                           		<fo:table-cell border-style = "solid">
                           				<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" font-weight = "bold">Certificate under Section 203 of the Income-tax Act, 1961 for Tax deducted at source on Salary</fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
			              	</fo:table-body>
               			</fo:table>
               		</fo:block>
               		<fo:block font-family="Arial">
		            	<fo:table border-style = "solid">
		                    <fo:table-column column-width="320pt"/>
		                    <fo:table-column column-width="320pt"/>
							<fo:table-body> 
	                 			<fo:table-row >
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" font-weight = "bold">Certificate No.</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">Last updated on</fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
	                           	<fo:table-row >
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" font-weight = "bold">Name and address of the Employer</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" font-weight = "bold">Name and Address of the Employee</fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
	                           	<fo:table-row >
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block  keep-together="always" text-align="center" font-family="Arial" font-size="10pt" font-weight = "bold" line-height = "18pt">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">${uiLabelMap.KMFDairyHeader}</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">${uiLabelMap.KMFDairySubHeader}</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block  keep-together="always" text-align="center" font-family="Arial" font-size="10pt" font-weight = "bold" line-height = "18pt">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">${(employeeValues.getValue().get("employeeName")).toUpperCase()}</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt"><#if employeeValues.getValue().get("address1")?has_content>${employeeValues.getValue().get("address1")}<#else>&#160;</#if></fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt"><#if employeeValues.getValue().get("address2")?has_content>${employeeValues.getValue().get("address2")}<#else>&#160;</#if></fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
	                           	<fo:table-row >
	                           		<fo:table-cell border-style = "solid">
	                           			<fo:block>
		                           			<fo:table>
		                           				<fo:table-column column-width="160pt"/>
		                           				<fo:table-column column-width="160pt"/>
		                           				<fo:table-body> 
						                     		<fo:table-row >
							                           	<fo:table-cell border-right-style="solid">
						                           			<fo:block text-align="center" keep-together="always" font-size="11pt" font-weight = "bold" line-height = "18pt">PAN No. of the Deductor</fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeValues.getValue().get("panNumberOfCompany")?has_content>${employeeValues.getValue().get("panNumberOfCompany")}<#else>&#160;</#if></fo:block>
						                           		</fo:table-cell>
						                           		<fo:table-cell>
						                           			<fo:block text-align="center" keep-together="always" font-size="11pt" font-weight = "bold" line-height = "18pt">TAN No. of the Deductor</fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeValues.getValue().get("tanNumberOfCompany")?has_content>${employeeValues.getValue().get("tanNumberOfCompany")}<#else>&#160;</#if></fo:block>
						                           		</fo:table-cell>
								                   	</fo:table-row>
								              	</fo:table-body>
		                           			</fo:table>
		                           		</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid">
	                           			<fo:block>
		                           			<fo:table>
		                           				<fo:table-column column-width="160pt"/>
		                           				<fo:table-column column-width="160pt"/>
		                           				<fo:table-body> 
						                     		<fo:table-row >
							                           	<fo:table-cell border-right-style="solid">
						                           			<fo:block text-align="center" keep-together="always" font-size="11pt" font-weight = "bold" line-height = "18pt">PAN No. of the Employee</fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" ><#if employeeValues.getValue().get("panNumberOfEmployee")?has_content>${employeeValues.getValue().get("panNumberOfEmployee")}<#else>&#160;</#if></fo:block>
						                           		</fo:table-cell>
						                           		<fo:table-cell border-right-style="solid">
						                           			<fo:block text-align="center" font-size="11pt" font-weight = "bold" line-height = "18pt">Employee Reference No. provided by the Employer (If Available.)</fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">${employeeValues.getKey()}</fo:block>
						                           		</fo:table-cell>
								                   	</fo:table-row>
								              	</fo:table-body>
		                           			</fo:table>
		                           		</fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
                           		<fo:table-row >
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" font-weight = "bold">CIT(TDS)</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">The Commissioner Income Tax</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">Room No : 59, HMT Bhavan, Bollaram rd</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">Ganganagar,Bangalore.</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell> 
	                           			<fo:block>
		                           			<fo:table>
		                           				<fo:table-column column-width="160pt"/>
		                           				<fo:table-column column-width="160pt"/>
		                           				<fo:table-body> 
						                     		<fo:table-row >
							                           	<fo:table-cell>
						                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" font-weight = "bold">Assessment Year</fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(employeeValues.getValue().get("subSequentFromDate"), "yyyy")}  - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(employeeValues.getValue().get("subSequentThruDate"), "yyyy")}</fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
						                           		</fo:table-cell>
						                           		<fo:table-cell border-right-style="solid" border-left-style="solid">
						                           			<fo:block text-align="center" font-size="11pt" line-height = "18pt"  border-bottom-style="solid" font-weight = "bold">Period</fo:block>
						                           			<fo:block>
							                           			<fo:table>
							                           				<fo:table-column column-width="80pt"/>
							                           				<fo:table-column column-width="80pt"/>
							                           				<fo:table-body> 
											                     		<fo:table-row >
											                     			<fo:table-cell border-right-style="solid">
											                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">From</fo:block>
											                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "yyyy")} </fo:block>
										                           				<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
										                           			</fo:table-cell>
										                           			<fo:table-cell >
											                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">To</fo:block>
											                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "yyyy")} </fo:block>
										                           				<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
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
	                           		</fo:table-cell>
	                           	</fo:table-row>
			              	</fo:table-body>
               			</fo:table>
               		</fo:block>
               		<fo:block font-family="Arial">
		            	<fo:table>
		                    <fo:table-column column-width="640pt"/>
							<fo:table-body> 
	                 			<fo:table-row >
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" font-weight = "bold">Summary of amount paid/ credited and tax deducted at source thereon in respect of the employee</fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
			              	</fo:table-body>
               			</fo:table>
               		</fo:block>
               		<fo:block font-family="Arial">
		            	<fo:table>
		                    <fo:table-column column-width="108pt"/>
		                    <fo:table-column column-width="158pt"/>
		                    <fo:table-column column-width="118pt"/>
		                    <fo:table-column column-width="128pt"/>
		                    <fo:table-column column-width="128pt"/>
							<fo:table-body> 
	                 			<fo:table-row >
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "15pt">Quarter</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" font-size="11pt" line-height = "15pt">Receipt Numbers of original quarterly statements of TDS under sub-section (3) of Section 200.</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" font-size="11pt" line-height = "15pt">Amount paid/ credited</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" font-size="11pt" line-height = "15pt">Amount of tax deducted (Rs.)</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" font-size="11pt" line-height = "15pt">Amount of tax deposited/remitted (Rs.)</fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
	                           	<fo:table-row >
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">Quarter 1</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">Quarter 2</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">Quarter 3</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">Quarter 4</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">Total</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid">
	                           			<#if acknowlemntMap?has_content>
		                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">${acknowlemntMap.get("quarter1")?if_exists}&#160;&#160;</fo:block>
		                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">${acknowlemntMap.get("quarter2")?if_exists}&#160;&#160;</fo:block>
		                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">${acknowlemntMap.get("quarter3")?if_exists}&#160;&#160;</fo:block>
		                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">${acknowlemntMap.get("quarter4")?if_exists}&#160;&#160;</fo:block>
		                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
		                           		<#else>
		                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">&#160;</fo:block>
		                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">&#160;</fo:block>
		                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">&#160;</fo:block>
		                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">&#160;</fo:block>
		                           		</#if>
		                           	</fo:table-cell>
	                           		<#assign totalTax = 0>
	                           		<fo:table-cell border-style = "solid"> 
		                           		<#if employeewiseQuarterlyTaxMap?has_content>
		                           			<#assign employeeWiseTaxMap = employeewiseQuarterlyTaxMap.entrySet()>
		                           			<#list employeeWiseTaxMap as employeeWiseTaxDetails>
		                           				<#if employeeWiseTaxDetails.getKey() == employeeValues.getKey()>
				                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">${employeeWiseTaxDetails.getValue().get("quarter1")?if_exists?string("#0.00")}&#160;&#160;</fo:block>
				                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">${employeeWiseTaxDetails.getValue().get("quarter2")?if_exists?string("#0.00")}&#160;&#160;</fo:block>
				                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">${employeeWiseTaxDetails.getValue().get("quarter3")?if_exists?string("#0.00")}&#160;&#160;</fo:block>
				                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">${employeeWiseTaxDetails.getValue().get("quarter4")?if_exists?string("#0.00")}&#160;&#160;</fo:block>
				                           			<#assign totalTax = totalTax + employeeWiseTaxDetails.getValue().get("quarter1")>
				                           			<#assign totalTax = totalTax + employeeWiseTaxDetails.getValue().get("quarter2")>
				                           			<#assign totalTax = totalTax + employeeWiseTaxDetails.getValue().get("quarter3")>
				                           			<#assign totalTax = totalTax + employeeWiseTaxDetails.getValue().get("quarter4")>
				                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${totalTax?if_exists?string("#0.00")}&#160;&#160;</fo:block>
					                           	</#if>
				                           	</#list>
				                        <#else>
				                        	<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">&#160;</fo:block>
		                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">&#160;</fo:block>
		                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">&#160;</fo:block>
		                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">&#160;</fo:block>
			                           	</#if>
			                        </fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
		                           		<#if employeewiseQuarterlyTaxMap?has_content>
		                           			<#assign employeeWiseTaxMap = employeewiseQuarterlyTaxMap.entrySet()>
		                           			<#list employeeWiseTaxMap as employeeWiseTaxDetails>
		                           				<#if employeeWiseTaxDetails.getKey() == employeeValues.getKey()>
				                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">${employeeWiseTaxDetails.getValue().get("quarter1")?if_exists?string("#0.00")}&#160;&#160;</fo:block>
				                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">${employeeWiseTaxDetails.getValue().get("quarter2")?if_exists?string("#0.00")}&#160;&#160;</fo:block>
				                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">${employeeWiseTaxDetails.getValue().get("quarter3")?if_exists?string("#0.00")}&#160;&#160;</fo:block>
				                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">${employeeWiseTaxDetails.getValue().get("quarter4")?if_exists?string("#0.00")}&#160;&#160;</fo:block>
				                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${totalTax?if_exists?string("#0.00")}&#160;&#160;</fo:block>
					                           	</#if>
				                           	</#list>
				                        <#else>
				                        	<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">&#160;</fo:block>
		                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">&#160;</fo:block>
		                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">&#160;</fo:block>
		                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">&#160;</fo:block>
			                           	</#if>
			                        </fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
		                           		<#if employeewiseQuarterlyTaxMap?has_content>
		                           			<#assign employeeWiseTaxMap = employeewiseQuarterlyTaxMap.entrySet()>
		                           			<#list employeeWiseTaxMap as employeeWiseTaxDetails>
		                           				<#if employeeWiseTaxDetails.getKey() == employeeValues.getKey()>
				                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">${employeeWiseTaxDetails.getValue().get("quarter1")?if_exists?string("#0.00")}&#160;&#160;</fo:block>
				                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">${employeeWiseTaxDetails.getValue().get("quarter2")?if_exists?string("#0.00")}&#160;&#160;</fo:block>
				                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">${employeeWiseTaxDetails.getValue().get("quarter3")?if_exists?string("#0.00")}&#160;&#160;</fo:block>
				                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">${employeeWiseTaxDetails.getValue().get("quarter4")?if_exists?string("#0.00")}&#160;&#160;</fo:block>
				                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${totalTax?if_exists?string("#0.00")}&#160;&#160;</fo:block>
					                           	</#if>
				                           	</#list>
				                        <#else>
				                        	<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">&#160;</fo:block>
		                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">&#160;</fo:block>
		                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">&#160;</fo:block>
		                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">&#160;</fo:block>
			                           	</#if>
			                        </fo:table-cell>
	                           	</fo:table-row>
			              	</fo:table-body>
               			</fo:table>
               		</fo:block>
               		<fo:block  keep-together="always" text-align="center" font-family="Arial" font-size="10pt" font-weight = "bold" line-height = "18pt">&#160;</fo:block>
               		<fo:block  keep-together="always" text-align="center" font-family="Arial" font-size="10pt" font-weight = "bold" line-height = "18pt">I. DETAILS OF TAX DEDUCTED AND DEPOSITED IN THE CENTRAL GOVERNMENT ACCOUNT THROUGH BOOK ADJUSTMENT</fo:block>
               		<fo:block  keep-together="always" text-align="center" font-family="Arial" font-size="11pt" line-height = "18pt">(The deductor to provide payment wise details of tax deducted and deposited with respect to the deductee)</fo:block>
               		<fo:block font-family="Arial">
		            	<fo:table>
		                    <fo:table-column column-width="50pt"/>
		                    <fo:table-column column-width="162pt"/>
		                    <fo:table-column column-width="428pt"/>
							<fo:table-body> 
	                 			<fo:table-row >
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" font-size="11pt" line-height = "18pt">S.No</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" font-size="11pt" line-height = "18pt">Tax Deposited in respect of the deductee (Rs.)</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" font-size="11pt" line-height = "18pt" border-bottom-style = "solid">Book identification number (BIN)</fo:block>
	                           			<fo:block>
		                           			<fo:table>
		                           				<fo:table-column column-width="107pt"/>
		                           				<fo:table-column column-width="107pt"/>
		                           				<fo:table-column column-width="107pt"/>
		                           				<fo:table-column column-width="107pt"/>
		                           				<fo:table-body> 
						                     		<fo:table-row >
						                     			<fo:table-cell border-right-style="solid">
						                           			<fo:block text-align="center" font-size="11pt" line-height = "18pt">Receipt numbers of Form No.24G</fo:block>
					                           			</fo:table-cell>
					                           			<fo:table-cell border-right-style="solid">
						                           			<fo:block text-align="center" font-size="11pt" line-height = "18pt">DDO Sequence Number in Form No. 24G</fo:block>
					                           			</fo:table-cell>
					                           			<fo:table-cell border-right-style="solid">
						                           			<fo:block text-align="center" font-size="11pt" line-height = "18pt">Date on transfer voucher (dd/mm/yyyy)</fo:block>
					                           			</fo:table-cell>
					                           			<fo:table-cell >
						                           			<fo:block text-align="center" font-size="11pt" line-height = "18pt">Status of matching with Form No. 24G</fo:block>
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
               		<fo:block font-family="Arial">
		            	<fo:table>
		                    <fo:table-column column-width="50pt"/>
		                    <fo:table-column column-width="162pt"/>
		                    <fo:table-column column-width="107pt"/>
               				<fo:table-column column-width="107pt"/>
               				<fo:table-column column-width="107pt"/>
               				<fo:table-column column-width="107pt"/>
							<fo:table-body> 
	                 			<fo:table-row >
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" font-size="11pt" line-height = "18pt">S.No</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
	                           	<fo:table-row >
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" font-size="11pt" line-height = "18pt">Total</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
			              	</fo:table-body>
               			</fo:table>
               		</fo:block>
               		<fo:block  keep-together="always" text-align="center" font-family="Arial" font-size="10pt" font-weight = "bold" line-height = "18pt">&#160;</fo:block>
               		<fo:block page-break-after="always"></fo:block>
               		<fo:block  keep-together="always" text-align="center" font-family="Arial" font-size="10pt" font-weight = "bold" line-height = "18pt">&#160;</fo:block>
               		<fo:block  keep-together="always" text-align="center" font-family="Arial" font-size="10pt" font-weight = "bold" line-height = "18pt">&#160;</fo:block>
               		<fo:block  keep-together="always" text-align="center" font-family="Arial" font-size="10pt" font-weight = "bold" line-height = "18pt">II. DETAILS OF TAX DEDUCTED AND DEPOSITED IN THE CENTRAL GOVERNMENT ACCOUNT THROUGH CHALLAN</fo:block>
               		<fo:block  keep-together="always" text-align="center" font-family="Arial" font-size="11pt" line-height = "18pt">(The deductor to provide payment wise details of tax deducted and deposited with respect to the deductee)</fo:block>
               		<fo:block font-family="Arial">
		            	<fo:table>
		                    <fo:table-column column-width="50pt"/>
		                    <fo:table-column column-width="162pt"/>
		                    <fo:table-column column-width="428pt"/>
							<fo:table-body> 
	                 			<fo:table-row >
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" font-size="11pt" line-height = "18pt">S.No</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" font-size="11pt" line-height = "18pt">Tax Deposited in respect of the deductee (Rs.)</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" font-size="11pt" line-height = "18pt" border-bottom-style = "solid">Challan identification number (CIN)</fo:block>
	                           			<fo:block>
		                           			<fo:table>
		                           				<fo:table-column column-width="107pt"/>
		                           				<fo:table-column column-width="107pt"/>
		                           				<fo:table-column column-width="107pt"/>
		                           				<fo:table-column column-width="107pt"/>
		                           				<fo:table-body> 
						                     		<fo:table-row >
						                     			<fo:table-cell border-right-style="solid">
						                           			<fo:block text-align="center" font-size="11pt" line-height = "18pt">BSR Code of the Bank Branch</fo:block>
					                           			</fo:table-cell>
					                           			<fo:table-cell border-right-style="solid">
						                           			<fo:block text-align="center" font-size="11pt" line-height = "18pt">Date on which tax deposited (dd/mm/yyyy)</fo:block>
					                           			</fo:table-cell>
					                           			<fo:table-cell border-right-style="solid">
						                           			<fo:block text-align="center" font-size="11pt" line-height = "18pt">Challan Serial Number</fo:block>
					                           			</fo:table-cell>
					                           			<fo:table-cell >
						                           			<fo:block text-align="center" font-size="11pt" line-height = "18pt">Status of matching with OLTAS</fo:block>
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
               		<#assign totalMonthwiseTax = 0 >
               		<fo:block font-family="Arial">
		            	<fo:table>
		                    <fo:table-column column-width="50pt"/>
		                    <fo:table-column column-width="162pt"/>
		                    <fo:table-column column-width="107pt"/>
               				<fo:table-column column-width="107pt"/>
               				<fo:table-column column-width="107pt"/>
               				<fo:table-column column-width="107pt"/>
							<fo:table-body> 
								<#assign sNo = 1>
								<#if monthWiseTaxDepositedMap?has_content>
                           			<#assign employeeWiseTaxMap = monthWiseTaxDepositedMap.entrySet()>
                           			<#list employeeWiseTaxMap as employeeWiseTaxDepositedValues>
                           				<#if employeeWiseTaxDepositedValues.getKey() == employeeValues.getKey()>
                           					<#if currMonthKeyList?has_content>
                           						<#list currMonthKeyList as timePeriod>
		                           					<#assign monthWiseTaxMap = employeeWiseTaxDepositedValues.getValue().entrySet()>
		                           					<#list monthWiseTaxMap as monthWiseTaxDepositedList>
		                           						<#if monthWiseTaxDepositedList.getKey() == timePeriod>
								                 			<fo:table-row >
								                           		<fo:table-cell border-style = "solid"> 
								                           			<fo:block text-align="center" font-size="11pt" line-height = "18pt">${sNo}</fo:block>
								                           		</fo:table-cell>
								                           		<fo:table-cell border-style = "solid"> 
								                           			<fo:block text-align="right" font-size="11pt" line-height = "18pt"><#if monthWiseTaxDepositedList.getValue().get("taxAmount")?has_content>${((monthWiseTaxDepositedList.getValue().get("taxAmount"))*(-1))?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
								                           		</fo:table-cell>
								                           		<fo:table-cell border-style = "solid"> 
								                           			<fo:block text-align="right" font-size="11pt" line-height = "18pt">${monthWiseTaxDepositedList.getValue().get("BSRcode")?if_exists}&#160;&#160;</fo:block>
								                           		</fo:table-cell>
								                           		<fo:table-cell border-style = "solid"> 
								                           			<fo:block text-align="right" font-size="11pt" line-height = "18pt"><#if monthWiseTaxDepositedList.getValue().get("taxDepositedDate")?has_content>${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(monthWiseTaxDepositedList.getValue().get("taxDepositedDate"), "dd/MM/yyyy")).toUpperCase()}&#160;&#160;<#else>&#160;</#if></fo:block>
								                           		</fo:table-cell>
								                           		<fo:table-cell border-style = "solid"> 
								                           			<fo:block text-align="right" font-size="11pt" line-height = "18pt">${monthWiseTaxDepositedList.getValue().get("challanNumber")?if_exists}&#160;&#160;</fo:block>
								                           		</fo:table-cell>
								                           		<fo:table-cell border-style = "solid"> 
								                           			<fo:block text-align="right" font-size="11pt" line-height = "18pt">&#160;</fo:block>
								                           		</fo:table-cell>
								                           		<#assign sNo = sNo + 1>
								                           		<#if monthWiseTaxDepositedList.getValue().get("taxAmount")?exists>
								                           			<#if monthWiseTaxDepositedList.getValue().get("taxAmount")?has_content>
								                           				<#assign totalMonthwiseTax = totalMonthwiseTax - monthWiseTaxDepositedList.getValue().get("taxAmount")>
								                           			</#if>
								                           		</#if>
								                           	</fo:table-row>
								                       	</#if>
							                       	</#list>
						                      	</#list>
						                     </#if>
					                 	</#if>
					              	</#list>
					             </#if> 	
	                           	<fo:table-row >
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" font-size="11pt" line-height = "18pt">Total</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="right" font-size="11pt" line-height = "18pt">${totalMonthwiseTax?if_exists?string("#0.00")}&#160;&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="right" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="right" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="right" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
						                     	
			              	</fo:table-body>
               			</fo:table>
               		</fo:block>
               		<fo:block font-family="Arial">
		            	<fo:table>
		                    <fo:table-column column-width="640pt"/>
							<fo:table-body> 
	                 			<fo:table-row >
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" font-weight = "bold">Verification</fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
	                           	<fo:table-row >
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="left" font-size="11pt" line-height = "18pt">I <#if employeeValues.getValue().get("signatoryName")?has_content>${(employeeValues.getValue().get("signatoryName"))}<#else>&#160;</#if> son of <#if employeeValues.getValue().get("fatherName")?has_content>${(employeeValues.getValue().get("fatherName"))}<#else>&#160;</#if> working in the capacity of  <#if employeeValues.getValue().get("designation")?has_content>${(employeeValues.getValue().get("designation"))}<#else>&#160;</#if> do hereby certify that a sum of Rs.${totalMonthwiseTax?if_exists?string("#0.00")} [RUPEES ${((Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble(totalMonthwiseTax?string("#0")), "%rupees-and-paise", locale)).replace("rupees","")).toUpperCase()} ONLY] has been deducted and deposited to the credit of the Central Government. I further certify that the information given above is true, complete and correct and is based on the books of account, documents, TDS statements, TDS deposited and other available records.</fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
	                       	</fo:table-body>
               			</fo:table>
               		</fo:block>
               		<fo:block font-family="Arial">
		            	<fo:table>
		                    <fo:table-column column-width="113pt"/>
		                    <fo:table-column column-width="163pt"/>
		                    <fo:table-column column-width="363pt"/>
							<fo:table-body> 
	                 			<fo:table-row >
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt" font-weight = "bold">&#160;&#160;Place</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="left" font-size="11pt" line-height = "18pt">BANGALORE</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="left" font-size="11pt" line-height = "18pt">For KMF Unit : Mother Dairy, Bangalore.</fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
	                           	<fo:table-row >
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt" font-weight = "bold">&#160;&#160;Date</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="left" font-size="11pt" line-height = "18pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="left" font-size="11pt" line-height = "18pt" font-weight = "bold">Signature of person responsible for deduction of tax</fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
	                           	<fo:table-row >
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt" font-weight = "bold">&#160;&#160;Designation</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="left" font-size="11pt" line-height = "18pt"><#if employeeValues.getValue().get("designation")?has_content>${(employeeValues.getValue().get("designation"))}<#else>&#160;</#if></fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="left" font-size="11pt" line-height = "18pt">Full Name: <#if employeeValues.getValue().get("signatoryName")?has_content>${(employeeValues.getValue().get("signatoryName")).toUpperCase()}<#else>&#160;</#if></fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
	                           	<fo:table-row>
			                    	<fo:table-cell >	
			                    		<fo:block page-break-after="always"></fo:block>
			                    	</fo:table-cell>
			                    </fo:table-row>
	                       	</fo:table-body>
               			</fo:table>
               		</fo:block>
               		<fo:block font-family="Arial">
		            	<fo:table>
		                    <fo:table-column column-width="640pt"/>
							<fo:table-body> 
	                 			<fo:table-row >
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "15pt" font-weight = "bold">Form 16 - PART B (Annexure)</fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
	                       	</fo:table-body>
               			</fo:table>
               		</fo:block>
               		<fo:block font-family="Arial">
		            	<fo:table>
		                    <fo:table-column column-width="110pt"/>
		                    <fo:table-column column-width="220pt"/>
		                    <fo:table-column column-width="160pt"/>
		                    <fo:table-column column-width="150pt"/>
							<fo:table-body> 
	                 			<fo:table-row >
	                 				<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" font-size="11pt" line-height = "15pt" font-weight = "bold">Deductor Name</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" font-size="11pt" line-height = "15pt">DIRECTOR Mother Dairy</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" font-size="11pt" line-height = "15pt"><fo:inline font-weight="bold">TAN:</fo:inline> ${employeeValues.getValue().get("tanNumberOfCompany")}</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "15pt" font-weight = "bold">Period</fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
	                           	<fo:table-row >
	                 				<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "15pt" font-weight = "bold">Employee</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "15pt">${(employeeValues.getValue().get("employeeName")).toUpperCase()}</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "15pt"><fo:inline font-weight="bold">Emp. PAN:</fo:inline> ${employeeValues.getValue().get("panNumberOfEmployee")?if_exists} </fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block font-family="Arial">
							            	<fo:table>
							                    <fo:table-column column-width="75pt"/>
							                    <fo:table-column column-width="75pt"/>
							                    <fo:table-body> 
						                 			<fo:table-row >
						                 				<fo:table-cell border-right-style = "solid"> 
						                           			<fo:block text-align="center" font-size="11pt" line-height = "15pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "yyyy")} </fo:block>
						                           		</fo:table-cell>
						                           		<fo:table-cell> 
						                           			<fo:block text-align="center" font-size="11pt" line-height = "15pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "yyyy")} </fo:block>
						                           		</fo:table-cell>
							                    	</fo:table-row>
						                       	</fo:table-body>
					               			</fo:table>
					               		</fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
	                           	<fo:table-row >
	                 				<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "15pt" font-weight = "bold">Employee Ref. No.</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "15pt">${employeeValues.getKey()}</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "15pt" font-weight = "bold">Assessment Year</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "15pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(employeeValues.getValue().get("subSequentFromDate"), "yyyy")}  - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(employeeValues.getValue().get("subSequentThruDate"), "yyyy")}</fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
	                       	</fo:table-body>
               			</fo:table>
               		</fo:block>
               		<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt" font-weight = "bold">Details of Salary paid and any other income and tax deducted</fo:block>
               		<fo:block font-family="Arial">
		            	<fo:table border-style = "solid">
	                		<#assign totalAdditionAmount = 0>
	                		<#assign totalDeletionAmount = 0>
		                    <fo:table-column column-width="320pt"/>
		                    <fo:table-column column-width="108pt"/>
		                    <fo:table-column column-width="106pt"/>
		                    <fo:table-column column-width="106pt"/>
	                     	<fo:table-body> 
	                 			<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;1. Gross Salary</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt">&#160;</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid" >
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;(a)  &#160;&#160;Salary as per provisions contained in Section 17(1)</fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;(b)  &#160;&#160;Total</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt"><#if employeeValues.getValue().get("totalEarnings")?has_content>${employeeValues.getValue().get("totalEarnings")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           			<#if employeeValues.getValue().get("totalEarnings")?has_content>
	                           				<#assign totalEmployeeEarnings = totalEmployeeEarnings + employeeValues.getValue().get("totalEarnings")>
	                           			</#if>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt"><#if employeeValues.getValue().get("totalEarnings")?has_content>${employeeValues.getValue().get("totalEarnings")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt">&#160;</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;2. Less : Allowance to the extent exempt under Section 10</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
		                           		<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt">&#160;</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid" >
	                           			<#if employeeValues.getValue().get("leastValue")?has_content><#if employeeValues.getValue().get("leastValue")!= 0><fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;(1)  &#160;&#160;House Rent Allowance</fo:block></#if></#if>
	                           			<#if employeeValues.getValue().get("totalConveyanceTaxableAmount")?has_content><#if employeeValues.getValue().get("totalConveyanceTaxableAmount")!= 0><fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;(2)  &#160;&#160;Conveyance Allowance</fo:block></#if></#if>
	                           			<#if employeeValues.getValue().get("otherAlwDeductableAmount")?has_content><#if employeeValues.getValue().get("otherAlwDeductableAmount") != 0><fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;(3)  &#160;&#160;Other allowance</fo:block></#if></#if>
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;Total</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<#if employeeValues.getValue().get("leastValue")?has_content><#if employeeValues.getValue().get("leastValue")!= 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${(employeeValues.getValue().get("leastValue"))?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if></#if>
	                           			<#if employeeValues.getValue().get("totalConveyanceTaxableAmount")?has_content><#if employeeValues.getValue().get("totalConveyanceTaxableAmount")!= 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${(employeeValues.getValue().get("totalConveyanceTaxableAmount"))?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if></#if>
	                           			<#if employeeValues.getValue().get("otherAlwDeductableAmount")?has_content><#if employeeValues.getValue().get("otherAlwDeductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${(employeeValues.getValue().get("otherAlwDeductableAmount"))?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if></#if>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<#if employeeValues.getValue().get("leastValue")?has_content><#if employeeValues.getValue().get("leastValue")!= 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block></#if></#if>
	                           			<#if employeeValues.getValue().get("totalConveyanceTaxableAmount")?has_content><#if employeeValues.getValue().get("totalConveyanceTaxableAmount")!= 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block></#if></#if>
	                           			<#if employeeValues.getValue().get("otherAlwDeductableAmount")?has_content><#if employeeValues.getValue().get("otherAlwDeductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block></#if></#if>
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt"><#if employeeValues.getValue().get("totalExtentAlw")!= 0>${(employeeValues.getValue().get("totalExtentAlw"))?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt">&#160;</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;3. Balance (1-2)</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
		                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt"><#if employeeValues.getValue().get("balance")!= 0>${(employeeValues.getValue().get("balance"))?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;4. Deductions :</fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;(a) Entertainment allowance </fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;(b) Tax on Employment </fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
		                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
		                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt"><#if employeeValues.getValue().get("professionalTax")?has_content>${((employeeValues.getValue().get("professionalTax"))*(-1))?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
		                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<#if employeeValues.getValue().get("aggregate")!= 0>
				                   	<fo:table-row>
		                           		<fo:table-cell border-right-style = "solid">
		                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;5. Aggregate of 4(a) and (b)</fo:block>
		                           		</fo:table-cell>
		                           		<fo:table-cell border-right-style = "solid">
			                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
		                           		</fo:table-cell>
		                           		<fo:table-cell border-right-style = "solid">
		                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${((employeeValues.getValue().get("aggregate"))*(-1))?if_exists?string("#0.00")}&#160;&#160;</fo:block>
		                           		</fo:table-cell>
		                           		<fo:table-cell >
		                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
		                           		</fo:table-cell>
				                   	</fo:table-row>
				               	</#if>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;6. Income chargeable under the Head ' Salaries' (3-5)</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
		                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt"><#if employeeValues.getValue().get("income")!= 0>${((employeeValues.getValue().get("income")))?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<#if employeeValues.getValue().get("interestHBA")?has_content>
                       				<#assign interestHBA = employeeValues.getValue().get("interestHBA")>
                       			</#if>
                       			<#if interestHBA != 0>
				                   	<fo:table-row>
		                           		<fo:table-cell border-right-style = "solid">
		                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;7. Add : Any other income reported by employee</fo:block>
		                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;&#160;&#160;a. Loss from Home Property</fo:block>
		                           		</fo:table-cell>
		                           		<fo:table-cell border-right-style = "solid">
		                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
		                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
		                           		</fo:table-cell>
		                           		<fo:table-cell border-right-style = "solid">
			                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
		                           		</fo:table-cell>
		                           		<fo:table-cell >
		                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
		                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">(-)${interestHBA?if_exists?string("#0.00")}&#160;&#160;</fo:block>
		                           		</fo:table-cell>
				                   	</fo:table-row>
				               	</#if>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;8. Gross total income (6 +7)</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
		                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt"><#if employeeValues.getValue().get("income")!= 0>${((employeeValues.getValue().get("income")) - interestHBA)?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<#if employeeSectionMap?has_content>
		                           	<#assign employeeSections = employeeSectionMap.entrySet()>
		                           	<#list employeeSections as employeeWiseSectionDetails>
		                           		<#if employeeWiseSectionDetails.getKey() == employeeValues.getKey()>
							               	<fo:table-row>
				                           		<fo:table-cell border-right-style = "solid">
				                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;9. Deductions Under Chapter VI-A</fo:block>
				                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;(A) Sections 80C,80CCC and 80CCD</fo:block>
				                           		</fo:table-cell>
				                           		<fo:table-cell border-right-style = "solid">
				                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
				                           		</fo:table-cell>
				                           		<fo:table-cell border-right-style = "solid">
				                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
				                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "15pt">Gross Amount</fo:block>
				                           		</fo:table-cell>
				                           		<fo:table-cell border-right-style = "solid">
					                           		<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
				                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "15pt">Deductible Amount</fo:block>
				                           		</fo:table-cell>
						                   	</fo:table-row>
						                   	<fo:table-row>
				                           		<fo:table-cell border-right-style = "solid">
				                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(a) Section 80C</fo:block>
				                           			<#if employeeValues.getValue().get("totalLICAmt")?has_content>
					                           			<#if employeeValues.getValue().get("totalLICAmt")!= 0>
				                           					<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(i) LIC Policy</fo:block>
				                           				</#if>
				                           			</#if>
				                           			<#if employeeWiseSectionDetails.getValue().get("HBA_PRICIPAL")?has_content>
					                           			<#if employeeWiseSectionDetails.getValue().get("HBA_PRICIPAL").get("deductableAmount")?has_content>
					                           				<#if employeeWiseSectionDetails.getValue().get("HBA_PRICIPAL").get("deductableAmount") != 0>
					                           					<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(ii) HBA Principal</fo:block>
					                           				</#if>
					                           			</#if>
					                           		</#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("TUITION_FEE")?has_content>
					                           			<#if employeeWiseSectionDetails.getValue().get("TUITION_FEE").get("deductableAmount")?has_content>
					                           				<#if employeeWiseSectionDetails.getValue().get("TUITION_FEE").get("deductableAmount") != 0>
				                           						<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(iii) Tuition Fee</fo:block>
				                           					</#if>
					                           			</#if>
					                           		</#if>
				                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(iv) PF / VPF</fo:block>
				                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(iv) GSLIS</fo:block>
				                           			<#if employeeWiseSectionDetails.getValue().get("SECTION_80CCC").get("deductableAmount")?has_content>
					                           			<#if employeeWiseSectionDetails.getValue().get("SECTION_80CCC").get("deductableAmount")!= 0>
				                           					<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(b) Section 80CCC</fo:block>
				                           				</#if>
					                           		</#if>
				                           			<#if employeeWiseSectionDetails.getValue().get("SECTION_80CCD").get("deductableAmount")?has_content>
					                           			<#if employeeWiseSectionDetails.getValue().get("SECTION_80CCD").get("deductableAmount")!= 0>
				                           					<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(c) Section 80CCD</fo:block>
				                           				</#if>
					                           		</#if>
				                           		</fo:table-cell>
				                           		<fo:table-cell border-right-style = "solid">
				                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
				                           		</fo:table-cell>
				                           		<fo:table-cell border-right-style = "solid">
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80C").get("grossAmount")?has_content><#if employeeWiseSectionDetails.getValue().get("SECTION_80C").get("grossAmount") != 0>${employeeWiseSectionDetails.getValue().get("SECTION_80C").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if><#else>&#160;</#if></fo:block>
					                           		<#if employeeWiseSectionDetails.getValue().get("LIC_POLICY")?has_content>
					                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt"><#if employeeWiseSectionDetails.getValue().get("LIC_POLICY").get("grossAmount")?has_content>${employeeWiseSectionDetails.getValue().get("LIC_POLICY").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<#else>
					                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
					                           		</#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("HBA_PRICIPAL")?has_content>
					                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt"><#if employeeWiseSectionDetails.getValue().get("HBA_PRICIPAL").get("grossAmount")?has_content>${employeeWiseSectionDetails.getValue().get("HBA_PRICIPAL").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<#else>
					                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
					                           		</#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("HBA_PRICIPAL")?has_content>
					                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt"><#if employeeWiseSectionDetails.getValue().get("TUITION_FEE").get("grossAmount")?has_content>${employeeWiseSectionDetails.getValue().get("TUITION_FEE").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<#else>
					                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
					                           		</#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80CCC").get("deductableAmount")?has_content>
					                           			<#if employeeWiseSectionDetails.getValue().get("SECTION_80CCC").get("deductableAmount")!= 0>
					                           				<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80CCC").get("grossAmount")?has_content>${employeeWiseSectionDetails.getValue().get("SECTION_80CCC").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           			</#if>
					                           		</#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80CCD").get("deductableAmount")?has_content>
					                           			<#if employeeWiseSectionDetails.getValue().get("SECTION_80CCD").get("deductableAmount")!= 0>
					                           				<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80CCD").get("grossAmount")?has_content>${employeeWiseSectionDetails.getValue().get("SECTION_80CCD").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
				                           				</#if>
					                           		</#if>
				                           		</fo:table-cell>
				                           		<fo:table-cell >
				                           			<#if employeeWiseSectionDetails.getValue().get("SECTION_80C").get("deductableAmount")?has_content>
				                           				<#assign deductableAmount = employeeWiseSectionDetails.getValue().get("SECTION_80C").get("deductableAmount")>
				                           			</#if>
				                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt"><#if deductableAmount?has_content><#if deductableAmount != 0>${deductableAmount?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if><#else>&#160;</#if></fo:block>
				                           			<#if employeeValues.getValue().get("totalLICAmt")?has_content>
					                           			<#if employeeValues.getValue().get("totalLICAmt")!= 0>
					                           				<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeValues.getValue().get("totalLICAmt")?if_exists?string("#0.00")}&#160;&#160;</fo:block>
					                           			</#if>
					                           		</#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("HBA_PRICIPAL")?has_content>
					                           			<#if employeeWiseSectionDetails.getValue().get("HBA_PRICIPAL").get("deductableAmount")?has_content>
					                           				<#if employeeWiseSectionDetails.getValue().get("HBA_PRICIPAL").get("deductableAmount") != 0>
					                           					<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("HBA_PRICIPAL").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block>
					                           				</#if>
					                           			</#if>
					                           		</#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("TUITION_FEE")?has_content>
					                           			<#if employeeWiseSectionDetails.getValue().get("TUITION_FEE").get("deductableAmount")?has_content>
					                           				<#if employeeWiseSectionDetails.getValue().get("TUITION_FEE").get("deductableAmount") != 0>
					                           					<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("TUITION_FEE").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block>
					                           				</#if>
					                           			</#if>
					                           		</#if>
					                           		<#if employeeValues.getValue().get("sec80cPFAmt")?exists>
					                           			<#if employeeValues.getValue().get("sec80cPFAmt")!= 0>
					                           				<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${((employeeValues.getValue().get("sec80cPFAmt"))*(-1))?if_exists?string("#0.00")}&#160;&#160;</fo:block>
					                           			</#if>
					                           		</#if>
					                           		<#if employeeValues.getValue().get("sec80cGSLISAmt")?exists>
					                           			<#if employeeValues.getValue().get("sec80cGSLISAmt") != 0>
					                           				<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${((employeeValues.getValue().get("sec80cGSLISAmt"))*(-1))?if_exists?string("#0.00")}&#160;&#160;</fo:block>
					                           			</#if>
					                           		</#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80CCC").get("deductableAmount")?has_content>
					                           			<#if employeeWiseSectionDetails.getValue().get("SECTION_80CCC").get("deductableAmount")!= 0>
					                           				<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80CCC").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block>
					                           			</#if>
					                           		</#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80CCD").get("deductableAmount")?has_content>
					                           			<#if employeeWiseSectionDetails.getValue().get("SECTION_80CCD").get("deductableAmount")!= 0>
					                           				<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80CCD").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block>
				                           				</#if>
				                           			</#if>
				                           		</fo:table-cell>
						                   	</fo:table-row>
						                   	<fo:table-row>
			                           			<fo:table-cell border-right-style = "solid">
			                           				<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Total</fo:block>
			                           			</fo:table-cell>
			                           			<fo:table-cell border-right-style = "solid">
			                           				<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
			                           			</fo:table-cell>
			                           			<fo:table-cell border-right-style = "solid">
			                           				<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
			                           			</fo:table-cell>
			                           			<fo:table-cell border-right-style = "solid">
			                           				<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt"><#if employeeValues.getValue().get("total9Amount")!= 0> ${employeeValues.getValue().get("total9Amount")?if_exists?string("#0.00")}&#160;&#160;<#else>0.00&#160;&#160;</#if></fo:block>
			                           			</fo:table-cell>
			                           		</fo:table-row>
					                   		<fo:table-row>
			                           			<fo:table-cell border-right-style = "solid">
			                           				<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Restricted to</fo:block>
			                           			</fo:table-cell>
			                           			<fo:table-cell border-right-style = "solid">
			                           				<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
			                           			</fo:table-cell>
			                           			<fo:table-cell border-right-style = "solid">
			                           				<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
			                           			</fo:table-cell>
				                           		<fo:table-cell border-right-style = "solid">
				                           			<#if (employeeValues.getValue().get("total9Amount") > 150000 )>
				                           				<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt"> 150000.00&#160;&#160;</fo:block>
				                           			<#else>
				                           				<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt"><#if employeeValues.getValue().get("total9Amount")!= 0> ${employeeValues.getValue().get("total9Amount")?if_exists?string("#0.00")}&#160;&#160;<#else>0.00&#160;&#160;</#if></fo:block>
				                           			</#if>
				                           		</fo:table-cell>
				                           	</fo:table-row>
						                   <fo:table-row>
				                           		<fo:table-cell border-right-style = "solid">
				                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;(B) Other Sections (e.g. 80E, 80G etc.) under Chapter VI-A</fo:block>
				                           		</fo:table-cell>
				                           		<fo:table-cell border-right-style = "solid">
				                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">Gross Amount</fo:block>
				                           		</fo:table-cell>
				                           		<fo:table-cell border-right-style = "solid">
				                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">Qualifying amount</fo:block>
				                           		</fo:table-cell>
				                           		<fo:table-cell border-right-style = "solid">
				                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">Deductible Amount</fo:block>
				                           		</fo:table-cell>
						                   	</fo:table-row>
			                   				<fo:table-row>
				                           		<fo:table-cell border-right-style = "solid">
				                           			<#if employeeWiseSectionDetails.getValue().get("SECTION_80CCG").get("deductableAmount") != 0><fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(a) Section 80CCG</fo:block></#if>
				                           			<#if employeeWiseSectionDetails.getValue().get("SECTION_80D").get("deductableAmount") != 0><fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(b) Section 80D</fo:block></#if>
				                           			<#if employeeWiseSectionDetails.getValue().get("SECTION_80DD").get("deductableAmount") != 0><fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(c) Section 80DD</fo:block></#if>
				                           			<#if employeeWiseSectionDetails.getValue().get("SECTION_80DDB").get("deductableAmount") != 0><fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(d) Section 80DDB</fo:block></#if>
				                           			<#if employeeWiseSectionDetails.getValue().get("SECTION_80E").get("deductableAmount") != 0><fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(e) Section 80E</fo:block></#if>
				                           			<#if employeeWiseSectionDetails.getValue().get("SECTION_80EE").get("deductableAmount") != 0><fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(f) Section 80EE</fo:block></#if>
				                           			<#if employeeWiseSectionDetails.getValue().get("SECTION_80G").get("deductableAmount") != 0><fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(g) Section 80G</fo:block></#if>
				                           			<#if employeeWiseSectionDetails.getValue().get("SECTION_80GG").get("deductableAmount") != 0><fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(h) Section 80GG</fo:block></#if>
				                           			<#if employeeWiseSectionDetails.getValue().get("SECTION_80GGA").get("deductableAmount") != 0><fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(i) Section 80GGA</fo:block></#if>
				                           			<#if employeeWiseSectionDetails.getValue().get("SECTION_80GGC").get("deductableAmount") != 0><fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(j) Section 80GGC</fo:block></#if>
				                           			<#if employeeWiseSectionDetails.getValue().get("SECTION_80TTA").get("deductableAmount") != 0><fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(k) Section 80TTA</fo:block></#if>
				                           			<#if employeeWiseSectionDetails.getValue().get("SECTION_80U").get("deductableAmount") != 0><fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(l) Section 80U</fo:block></#if>
				                           			<#if employeeWiseSectionDetails.getValue().get("OTHER").get("deductableAmount") != 0><fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(m) Other</fo:block></#if>
				                           		</fo:table-cell>
				                           		<fo:table-cell border-right-style = "solid">
				                           			<#if employeeWiseSectionDetails.getValue().get("SECTION_80CCG").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80CCG").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80D").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80D").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80DD").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80DD").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80DDB").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80DDB").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80E").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80E").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80EE").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80EE").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80G").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80G").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80GG").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80GG").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80GGA").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80GGA").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80GGC").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80GGC").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80TTA").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80TTA").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80U").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80U").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("OTHER").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("OTHER").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
				                           		</fo:table-cell>
				                           		<fo:table-cell border-right-style = "solid">
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80CCG").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80CCG").get("qualifyingAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80D").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80D").get("qualifyingAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80DD").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80DD").get("qualifyingAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80DDB").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80DDB").get("qualifyingAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80E").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80E").get("qualifyingAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80EE").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80EE").get("qualifyingAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80G").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80G").get("qualifyingAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80GG").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80GG").get("qualifyingAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80GGA").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80GGA").get("qualifyingAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80GGC").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80GGC").get("qualifyingAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80TTA").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80TTA").get("qualifyingAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80U").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80U").get("qualifyingAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("OTHER").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("OTHER").get("qualifyingAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
				                           		</fo:table-cell>
				                           		<fo:table-cell >
				                           			<#if employeeWiseSectionDetails.getValue().get("SECTION_80CCG").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80CCG").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80D").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80D").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80DD").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80DD").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80DDB").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80DDB").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80E").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80E").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80EE").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80EE").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80G").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80G").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80GG").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80GG").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80GGA").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80GGA").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80GGC").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80GGC").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80TTA").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80TTA").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("SECTION_80U").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("SECTION_80U").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
					                           		<#if employeeWiseSectionDetails.getValue().get("OTHER").get("deductableAmount") != 0><fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeWiseSectionDetails.getValue().get("OTHER").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block></#if>
				                           		</fo:table-cell>
						                   	</fo:table-row>
		                   				</#if>
			                       	</#list>
			                 	</#if>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;(10) Aggregate of deductible amount under Chapter VI-A </fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt"><#if employeeValues.getValue().get("totalDeductableAmount")!= 0>${(employeeValues.getValue().get("totalDeductableAmount"))?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;(11) Total income (8-10) </fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt"><#if employeeValues.getValue().get("totalIncome")!= 0>${(employeeValues.getValue().get("totalIncome"))?if_exists?string("#0.00")}&#160;&#160;<#else>0.00&#160;&#160;</#if></fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;(12) Tax on total income</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt"><#if employeeValues.getValue().get("tax")!= 0>${(employeeValues.getValue().get("tax"))?if_exists?string("#0.00")}&#160;&#160;<#else>0.00&#160;&#160;</#if></fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<#if employeeValues.getValue().get("rebate")?has_content>
			                   		<#if employeeValues.getValue().get("rebate")!= 0>
					                   	<fo:table-row>
			                           		<fo:table-cell border-right-style = "solid">
			                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;(13) Less: Tax Rebate</fo:block>
			                           		</fo:table-cell>
			                           		<fo:table-cell border-right-style = "solid">
			                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
			                           		</fo:table-cell>
			                           		<fo:table-cell border-right-style = "solid">
			                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
			                           		</fo:table-cell>
			                           		<fo:table-cell >
			                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeValues.getValue().get("rebate")?if_exists?string("#0.00")}&#160;&#160;</fo:block>
			                           		</fo:table-cell>
					                   	</fo:table-row>
					              	</#if>
				               	</#if>
				               	<#if employeeValues.getValue().get("taxAfterRebate")?has_content>
				               		<#if employeeValues.getValue().get("taxAfterRebate") != 0>
					                   	<fo:table-row>
			                           		<fo:table-cell border-right-style = "solid">
			                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;(14) Tax on total after Rebate </fo:block>
			                           		</fo:table-cell>
			                           		<fo:table-cell border-right-style = "solid">
			                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
			                           		</fo:table-cell>
			                           		<fo:table-cell border-right-style = "solid">
			                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
			                           		</fo:table-cell>
			                           		<fo:table-cell >
			                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeValues.getValue().get("taxAfterRebate")?if_exists?string("#0.00")}&#160;&#160;</fo:block>
			                           		</fo:table-cell>
					                   	</fo:table-row>
					              	</#if>
					          	</#if>
					          	<#if employeeValues.getValue().get("educationalCessAmount")?has_content>
					          		<#if employeeValues.getValue().get("educationalCessAmount") != 0>
					                   	<fo:table-row>
			                           		<fo:table-cell border-right-style = "solid">
			                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;(15) Education Cess @ 3% on (tax at S.No.14)</fo:block>
			                           		</fo:table-cell>
			                           		<fo:table-cell border-right-style = "solid">
			                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
			                           		</fo:table-cell>
			                           		<fo:table-cell border-right-style = "solid">
			                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
			                           		</fo:table-cell>
			                           		<fo:table-cell >
			                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${employeeValues.getValue().get("educationalCessAmount")?if_exists?string("#0.00")}&#160;&#160;</fo:block>
			                           		</fo:table-cell>
					                   	</fo:table-row>
					              	</#if>
					           	</#if>
			                   	<#if employeeValues.getValue().get("educationalCessAmount")?has_content>
			                   		<#assign taxPay = employeeValues.getValue().get("taxAfterRebate") + employeeValues.getValue().get("educationalCessAmount")>
			                   	</#if>
			                   	<#if taxPay?has_content>
			                   		<#if taxPay != 0>
					                   	<fo:table-row>
			                           		<fo:table-cell border-right-style = "solid">
			                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;(16) Tax payable (12+13+14) </fo:block>
			                           		</fo:table-cell>
			                           		<fo:table-cell border-right-style = "solid">
			                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
			                           		</fo:table-cell>
			                           		<fo:table-cell border-right-style = "solid">
			                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
			                           		</fo:table-cell>
			                           		<#assign taxPayable = 0>
			                           		<#assign actualTaxPayable = 0>
			                           		<#if employeeValues.getValue().get("rebate")?has_content>
			                           			<#assign rebate = employeeValues.getValue().get("rebate")>
			                           		<#else>
			                           			<#assign rebate = 0>
			                           		</#if>
			                           		<#if employeeValues.getValue().get("taxPayable")?has_content>
			                           			<#assign taxPayable = employeeValues.getValue().get("taxPayable") - rebate>
			                           		</#if>
			                           		<#if employeeValues.getValue().get("educationalCessAmount")?has_content>
			                           			<#assign taxPayable = taxPayable + employeeValues.getValue().get("educationalCessAmount")>
			                           			<#assign actualTaxPayable = employeeValues.getValue().get("educationalCessAmount") - taxPayable>
			                           		</#if>
			                           		<fo:table-cell >
			                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${taxPay?if_exists?string("#0.00")}&#160;&#160;</fo:block>
			                           		</fo:table-cell>
					                   	</fo:table-row>
					               	</#if>
					          	</#if>
					          	<#if employeeValues.getValue().get("totalTaxDeductedatSource")?has_content>
					          		<#if employeeValues.getValue().get("totalTaxDeductedatSource") != 0>
					                   	<fo:table-row>
			                           		<fo:table-cell border-right-style = "solid">
			                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;(17) Less: Tax deducted at Source</fo:block>
			                           		</fo:table-cell>
			                           		<fo:table-cell border-right-style = "solid">
			                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
			                           		</fo:table-cell>
			                           		<fo:table-cell border-right-style = "solid">
			                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
			                           		</fo:table-cell>
			                           		<fo:table-cell >
			                           				<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${((employeeValues.getValue().get("totalTaxDeductedatSource"))*(-1))?if_exists?string("#0.00")}&#160;&#160;</fo:block>
			                           		</fo:table-cell>
					                   	</fo:table-row>
					              	</#if>
					          	</#if>
					          	<#assign taxDiff = 0>
                       			<#if employeeValues.getValue().get("totalTaxDeductedatSource")?has_content>
                       				<#assign totalTaxDeductedatSource = (employeeValues.getValue().get("totalTaxDeductedatSource"))*(-1)>
                       				<#if taxPay?has_content>
                           				<#assign taxDiff = (taxPay - totalTaxDeductedatSource)>
                           			</#if>
                       			</#if>
                       			<#if taxDiff != 0>
				                   	<fo:table-row>
		                           		<fo:table-cell border-right-style = "solid">
		                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;(18) Tax Payable / Refund</fo:block>
		                           		</fo:table-cell>
		                           		<fo:table-cell border-right-style = "solid">
		                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
		                           		</fo:table-cell>
		                           		<fo:table-cell border-right-style = "solid">
		                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
		                           		</fo:table-cell>
		                           		<fo:table-cell >
		                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${taxDiff?if_exists?string("#0.00")}&#160;&#160;</fo:block>
		                           		</fo:table-cell>
				                   	</fo:table-row>
				              	</#if>
			             	</fo:table-body>
	           			</fo:table>
	           		</fo:block>
	           		<fo:block font-family="Arial">
		            	<fo:table>
		                    <fo:table-column column-width="640pt"/>
							<fo:table-body> 
	                 			<fo:table-row >
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "15pt" font-weight = "bold">Verification</fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
	                           	<fo:table-row >
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="left" font-size="11pt" line-height = "15pt">I <#if employeeValues.getValue().get("signatoryName")?has_content>${(employeeValues.getValue().get("signatoryName"))}<#else>&#160;</#if> son of <#if employeeValues.getValue().get("fatherName")?has_content>${(employeeValues.getValue().get("fatherName"))}<#else>&#160;</#if> working in the capacity of  <#if employeeValues.getValue().get("designation")?has_content>${(employeeValues.getValue().get("designation"))}<#else>&#160;</#if> do hereby certify that a sum of Rs. ${totalMonthwiseTax?if_exists?string("#0.00")} [RUPEES ${((Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble(totalMonthwiseTax?string("#0")), "%rupees-and-paise", locale)).replace("rupees","")).toUpperCase()} ONLY  ] has been deducted and deposited to the credit of the Central Government. I further certify that the information given above is true, complete and correct and is based on the books of account, documents, TDS statements, TDS deposited and other available records.</fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
	                       	</fo:table-body>
               			</fo:table>
               		</fo:block>
               		<fo:block font-family="Arial">
		            	<fo:table>
		                    <fo:table-column column-width="113pt"/>
		                    <fo:table-column column-width="163pt"/>
		                    <fo:table-column column-width="363pt"/>
							<fo:table-body> 
	                 			<fo:table-row >
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt" font-weight = "bold">&#160;&#160;Place</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="left" font-size="11pt" line-height = "15pt">BANGALORE</fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
	                           	<fo:table-row >
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt" font-weight = "bold">&#160;&#160;Date</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="left" font-size="11pt" line-height = "15pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="left" font-size="11pt" line-height = "15pt">Signature of person responsible for deduction of tax</fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
	                           	<fo:table-row >
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt" font-weight = "bold">&#160;&#160;Designation</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="left" font-size="11pt" line-height = "15pt"><#if employeeValues.getValue().get("designation")?has_content>${(employeeValues.getValue().get("designation"))}<#else>&#160;</#if></fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="left" font-size="11pt" line-height = "15pt" >Full Name: <#if employeeValues.getValue().get("signatoryName")?has_content>${(employeeValues.getValue().get("signatoryName")).toUpperCase()}<#else>&#160;</#if></fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
	                           	<fo:table-row>
			                    	<fo:table-cell >	
			                    		<fo:block page-break-after="always"></fo:block>
			                    	</fo:table-cell>
			                    </fo:table-row>
	                       	</fo:table-body>
               			</fo:table>
               		</fo:block>
               		</#if>
               		<#else>
               			<#if (parameters.employeeId)?has_content>
	               			<fo:block font-family="Arial">
				            	<fo:table>
				                    <fo:table-column column-width="640pt"/>
									<fo:table-body> 
			                 			<fo:table-row >
			                           		<fo:table-cell border-style = "solid"> 
			                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" font-weight = "bold">FORM NO.16</fo:block>
			                           		</fo:table-cell>
			                           	</fo:table-row>
					              	</fo:table-body>
		               			</fo:table>
	               			</fo:block>
	               		</#if>
               		</#if>
               	</#list>
           	</fo:flow>
		</fo:page-sequence>
	<#else>
		<fo:page-sequence master-reference="main">
			<fo:flow flow-name="xsl-region-body" font-family="Arial">
   		 		<fo:block font-size="14pt">
        			No Employee Found.......!
   		 		</fo:block>
			</fo:flow>
		</fo:page-sequence>	
	</#if>
   </fo:root>
</#escape>
		                           		
		                           		