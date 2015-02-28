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
										                           			</fo:table-cell>
										                           			<fo:table-cell >
											                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">To</fo:block>
											                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "yyyy")} </fo:block>
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
	                           		<#if acknowlemntMap?has_content>
		                           		<fo:table-cell border-style = "solid"> 
		                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">${acknowlemntMap.get("quarter1")?if_exists}&#160;&#160;</fo:block>
		                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">${acknowlemntMap.get("quarter2")?if_exists}&#160;&#160;</fo:block>
		                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">${acknowlemntMap.get("quarter3")?if_exists}&#160;&#160;</fo:block>
		                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">${acknowlemntMap.get("quarter4")?if_exists}&#160;&#160;</fo:block>
		                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
		                           		</fo:table-cell>
	                           		</#if>
	                           		<#assign totalTax = 0>
	                           		<#if employeewiseQuarterlyTaxMap?has_content>
	                           			<#assign employeeWiseTaxMap = employeewiseQuarterlyTaxMap.entrySet()>
	                           			<#list employeeWiseTaxMap as employeeWiseTaxDetails>
	                           				<#if employeeWiseTaxDetails.getKey() == employeeValues.getKey()>
				                           		<fo:table-cell border-style = "solid"> 
				                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">${employeeWiseTaxDetails.getValue().get("quarter1")?if_exists?string("#0.00")}&#160;&#160;</fo:block>
				                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">${employeeWiseTaxDetails.getValue().get("quarter2")?if_exists?string("#0.00")}&#160;&#160;</fo:block>
				                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">${employeeWiseTaxDetails.getValue().get("quarter3")?if_exists?string("#0.00")}&#160;&#160;</fo:block>
				                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">${employeeWiseTaxDetails.getValue().get("quarter4")?if_exists?string("#0.00")}&#160;&#160;</fo:block>
				                           			<#assign totalTax = totalTax + employeeWiseTaxDetails.getValue().get("quarter1")>
				                           			<#assign totalTax = totalTax + employeeWiseTaxDetails.getValue().get("quarter2")>
				                           			<#assign totalTax = totalTax + employeeWiseTaxDetails.getValue().get("quarter3")>
				                           			<#assign totalTax = totalTax + employeeWiseTaxDetails.getValue().get("quarter4")>
				                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">${totalTax?if_exists?string("#0.00")}&#160;&#160;</fo:block>
				                           		</fo:table-cell>
				                           	</#if>
			                           	</#list>
		                           	</#if>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt" border-bottom-style="solid">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
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
								                           			<fo:block text-align="right" font-size="11pt" line-height = "18pt">${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(monthWiseTaxDepositedList.getValue().get("customTimePeriodDayEnd"), "dd-MM-yyyy")).toUpperCase()}&#160;&#160;</fo:block>
								                           		</fo:table-cell>
								                           		<fo:table-cell border-style = "solid"> 
								                           			<fo:block text-align="right" font-size="11pt" line-height = "18pt">${monthWiseTaxDepositedList.getValue().get("challanNumber")?if_exists}&#160;&#160;</fo:block>
								                           		</fo:table-cell>
								                           		<fo:table-cell border-style = "solid"> 
								                           			<fo:block text-align="right" font-size="11pt" line-height = "18pt">&#160;</fo:block>
								                           		</fo:table-cell>
								                           		<#assign sNo = sNo + 1>
								                           		<#assign totalMonthwiseTax = totalMonthwiseTax - monthWiseTaxDepositedList.getValue().get("taxAmount")>
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
	                           			<fo:block text-align="left" font-size="11pt" line-height = "18pt">I ${(employeeValues.getValue().get("signatoryName"))} son of ${(employeeValues.getValue().get("fatherName"))} working in the capacity of  ${(employeeValues.getValue().get("designation"))} do hereby certify that a sum of Rs.${totalMonthwiseTax?if_exists?string("#0.00")} [${Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble(totalMonthwiseTax?string("#0")), "%rupees-and-paise", locale)}] has been deducted and deposited to the credit of the Central Government. I further certify that the information given above is true, complete and correct and is based on the books of account, documents, TDS statements, TDS deposited and other available records.</fo:block>
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
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt" font-weight = "bold">Place</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="left" font-size="11pt" line-height = "18pt">Karnataka</fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
	                           	<fo:table-row >
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt" font-weight = "bold">Date</fo:block>
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
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt" font-weight = "bold">Designation</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="left" font-size="11pt" line-height = "18pt">${(employeeValues.getValue().get("designation"))}</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="left" font-size="11pt" line-height = "18pt">Full Name: ${(employeeValues.getValue().get("signatoryName")).toUpperCase()}</fo:block>
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
	                           			<fo:block text-align="center" font-size="11pt" line-height = "15pt">${(employeeValues.getValue().get("employeeName")).toUpperCase()}</fo:block>
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
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "15pt"><fo:inline font-weight="bold">Emp. PAN:</fo:inline> ${employeeValues.getValue().get("panNumberOfCompany")} </fo:block>
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
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">1. Gross Salary</fo:block>
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
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;(b)  &#160;&#160;Value of perquisites under Section 17(2)</fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(as per Form No. 12BA, wherever applicable)</fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;(c)  &#160;&#160;Profits in lieu of salary under Section 17(3)</fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(as per Form No. 12BA, wherever applicable) </fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;(d)  &#160;&#160;Total</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt"><#if employeeValues.getValue().get("totalEarnings")?has_content>${employeeValues.getValue().get("totalEarnings")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           			<#if employeeValues.getValue().get("totalEarnings")?has_content>
	                           				<#assign totalEmployeeEarnings = totalEmployeeEarnings + employeeValues.getValue().get("totalEarnings")>
	                           			</#if>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt"><#if employeeValues.getValue().get("totalEarnings")?has_content>${employeeValues.getValue().get("totalEarnings")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt">&#160;</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">2. Less : Allowance to the extent exempt under Section 10</fo:block>
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
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;(1)  &#160;&#160;House Rent Allowance</fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;(2)  &#160;&#160;Conveyance Allowance</fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;(3)  &#160;&#160;Medical Allowance</fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;(4)  &#160;&#160;mobile allowance</fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;(5)  &#160;&#160;other allowance</fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "15pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;Total</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt"><#if employeeValues.getValue().get("leastValue")?has_content>${(employeeValues.getValue().get("leastValue"))?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt"><#if employeeValues.getValue().get("totalConveyanceTaxableAmount")?has_content>${(employeeValues.getValue().get("totalConveyanceTaxableAmount"))?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt"><#if employeeValues.getValue().get("totalExtentAlw")!= 0>${(employeeValues.getValue().get("totalExtentAlw"))?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="11pt">&#160;</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">3. Balance (1-2)</fo:block>
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
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">4. Deductions :</fo:block>
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
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">5. Aggregate of 4(a) and (b)</fo:block>
	                           		</fo:table-cell>
	                           		
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt"><#if employeeValues.getValue().get("aggregate")!= 0>${((employeeValues.getValue().get("aggregate"))*(-1))?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
		                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt">&#160;</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">6. Income chargeable under the Head ' Salaries' (3-5)</fo:block>
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
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">7. Add : Any other income reported by employee</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
		                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt">&#160;</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">8. Gross total income (6 +7)</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
		                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt"><#if employeeValues.getValue().get("income")!= 0>${(employeeValues.getValue().get("income"))?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<#if employeeSectionMap?has_content>
		                           	<#assign employeeSections = employeeSectionMap.entrySet()>
		                           	<#list employeeSections as employeeWiseSectionDetails>
		                           		<#if employeeWiseSectionDetails.getKey() == employeeValues.getKey()>
							               	<fo:table-row>
				                           		<fo:table-cell border-right-style = "solid">
				                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">9. Deductions Under Chapter VI-A</fo:block>
				                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">&#160;&#160;&#160;&#160;(A) Sections 80C,80CCC and 80CCD</fo:block>
				                           		</fo:table-cell>
				                           		<fo:table-cell border-right-style = "solid">
				                           			<fo:block text-align="center" keep-together="always" font-size="11pt">&#160;</fo:block>
				                           		</fo:table-cell>
				                           		<fo:table-cell border-right-style = "solid">
				                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
				                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">Gross Amount</fo:block>
				                           		</fo:table-cell>
				                           		<fo:table-cell border-right-style = "solid">
					                           		<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
				                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">Deductible Amount</fo:block>
				                           		</fo:table-cell>
						                   	</fo:table-row>
						                   	<fo:table-row>
				                           		<fo:table-cell border-right-style = "solid">
				                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(a) Section 80C</fo:block>
				                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(a) Section 80CCC</fo:block>
				                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(a) Section 80CCD</fo:block>
				                           		</fo:table-cell>
				                           		<fo:table-cell border-right-style = "solid">
				                           			<fo:block text-align="center" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
				                           		</fo:table-cell>
				                           		<fo:table-cell border-right-style = "solid">
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80C").get("grossAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80C").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80CCC").get("grossAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80CCC").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80CCD").get("grossAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80CCD").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
				                           		</fo:table-cell>
				                           		<fo:table-cell >
				                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80C").get("deductableAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80C").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80CCC").get("deductableAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80CCC").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80CCD").get("deductableAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80CCD").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
				                           		</fo:table-cell>
						                   	</fo:table-row>
						                   <fo:table-row>
				                           		<fo:table-cell border-right-style = "solid">
				                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">&#160;&#160;&#160;&#160;(B) Other Sections (e.g. 80E, 80G etc.) under Chapter VI-A</fo:block>
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
				                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(a) Section 80CCG</fo:block>
				                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(b) Section 80D</fo:block>
				                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(c) Section 80DD</fo:block>
				                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(d) Section 80DDB</fo:block>
				                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(e) Section 80E</fo:block>
				                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(f) Section 80EE</fo:block>
				                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(g) Section 80G</fo:block>
				                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(h) Section 80GG</fo:block>
				                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(i) Section 80GGA</fo:block>
				                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(j) Section 80GGC</fo:block>
				                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(k) Section 80TTA</fo:block>
				                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(l) Section 80U</fo:block>
				                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(m) Other</fo:block>
				                           		</fo:table-cell>
				                           		<fo:table-cell border-right-style = "solid">
				                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80CCG").get("grossAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80CCG").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80D").get("grossAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80D").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80DD").get("grossAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80DD").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80DDB").get("grossAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80DDB").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80E").get("grossAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80E").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80EE").get("grossAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80EE").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80G").get("grossAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80G").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80GG").get("grossAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80GG").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80GGA").get("grossAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80GGA").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80GGC").get("grossAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80GGC").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80TTA").get("grossAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80TTA").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80U").get("grossAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80U").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("OTHER").get("grossAmount")!= 0>${employeeWiseSectionDetails.getValue().get("OTHER").get("grossAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
				                           		</fo:table-cell>
				                           		<fo:table-cell border-right-style = "solid">
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80CCG").get("qualifyingAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80CCG").get("qualifyingAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80D").get("qualifyingAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80D").get("qualifyingAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80DD").get("qualifyingAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80DD").get("qualifyingAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80DDB").get("qualifyingAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80DDB").get("qualifyingAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80E").get("qualifyingAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80E").get("qualifyingAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80EE").get("qualifyingAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80EE").get("qualifyingAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80G").get("qualifyingAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80G").get("qualifyingAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80GG").get("qualifyingAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80GG").get("qualifyingAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80GGA").get("qualifyingAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80GGA").get("qualifyingAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80GGC").get("qualifyingAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80GGC").get("qualifyingAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80TTA").get("qualifyingAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80TTA").get("qualifyingAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80U").get("qualifyingAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80U").get("qualifyingAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("OTHER").get("qualifyingAmount")!= 0>${employeeWiseSectionDetails.getValue().get("OTHER").get("qualifyingAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
				                           		</fo:table-cell>
				                           		<fo:table-cell >
				                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80CCG").get("deductableAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80CCG").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80D").get("deductableAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80D").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80DD").get("deductableAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80DD").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80DDB").get("deductableAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80DDB").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80E").get("deductableAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80E").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80EE").get("deductableAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80EE").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80G").get("deductableAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80G").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80GG").get("deductableAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80GG").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80GGA").get("deductableAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80GGA").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80GGC").get("deductableAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80GGC").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80TTA").get("deductableAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80TTA").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("SECTION_80U").get("deductableAmount")!= 0>${employeeWiseSectionDetails.getValue().get("SECTION_80U").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
					                           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeWiseSectionDetails.getValue().get("OTHER").get("deductableAmount")!= 0>${employeeWiseSectionDetails.getValue().get("OTHER").get("deductableAmount")?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
				                           		</fo:table-cell>
						                   	</fo:table-row>
		                   				</#if>
			                       	</#list>
			                 	</#if>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">(10) Aggregate of deductible amount under Chpater VI-A </fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeValues.getValue().get("totalDeductableAmount")!= 0>${(employeeValues.getValue().get("totalDeductableAmount"))?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">(11) Total income (8-10) </fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeValues.getValue().get("totalIncome")!= 0>${(employeeValues.getValue().get("totalIncome"))?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">(12) Tax on total income</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeValues.getValue().get("tax")!= 0>${(employeeValues.getValue().get("tax"))?if_exists?string("#0.00")}&#160;&#160;<#else>0.00&#160;&#160;</#if></fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">(13) Less: Tax Rebate</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<#if (employeeValues.getValue().get("totalIncome") < 500000) >
	                           			<#assign rebate = 2000>
	                           		<#else>
	                           			<#assign rebate = 0>
	                           		</#if>
	                           		<#if (rebate > employeeValues.getValue().get("tax"))>
	                           			<#assign rebate = employeeValues.getValue().get("tax")>
	                           		</#if>
	                           		<fo:table-cell >
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if rebate?has_content>${rebate?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">(14) Surcharge(on tax computed at S.No.12) </fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeValues.getValue().get("surchargeAmount")?has_content>${(employeeValues.getValue().get("surchargeAmount"))?if_exists?string("#0.00")}&#160;&#160;<#else> &#160;</#if></fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">(15) Education Cess @ 2% on (tax at S.No.12)</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if employeeValues.getValue().get("educationalCessAmount")?has_content>${(employeeValues.getValue().get("educationalCessAmount"))?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">(16) Tax payable (12+13+14) </fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<#assign taxPayable = 0>
	                           		<#if employeeValues.getValue().get("taxPayable")?has_content>
	                           			<#assign taxPayable = employeeValues.getValue().get("taxPayable") + rebate>
	                           		</#if>
	                           		<#assign actualTaxPayable = employeeValues.getValue().get("educationalCessAmount") - taxPayable>
	                           		<fo:table-cell >
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt"><#if taxPayable?has_content>${taxPayable?if_exists?string("#0.00")}&#160;&#160;<#else>&#160;</#if></fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">(17) Less: Tax deducted at Source</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt">${((actualTaxPayable)*(-1))?if_exists?string("#0.00")}&#160;&#160;</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt">(18) Tax payable / Refund</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "18pt">${(taxPayable - ((actualTaxPayable)*(-1))) ?if_exists?string("#0.00")}&#160;&#160;</fo:block>
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
	                           			<fo:block text-align="left" font-size="11pt" line-height = "18pt">I ${(employeeValues.getValue().get("signatoryName"))} son of ${(employeeValues.getValue().get("fatherName"))} working in the capacity of  ${(employeeValues.getValue().get("designation"))} do hereby certify that a sum of Rs. ${totalMonthwiseTax?if_exists?string("#0.00")} [${Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble(totalMonthwiseTax?string("#0")), "%rupees-and-paise", locale)}  ] has been deducted and deposited to the credit of the Central Government. I further certify that the information given above is true, complete and correct and is based on the books of account, documents, TDS statements, TDS deposited and other available records.</fo:block>
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
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt" font-weight = "bold">Place</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="left" font-size="11pt" line-height = "18pt">Karnataka</fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
	                           	<fo:table-row >
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt" font-weight = "bold">Date</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="left" font-size="11pt" line-height = "18pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="left" font-size="11pt" line-height = "18pt">Signature of person responsible for deduction of tax</fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
	                           	<fo:table-row >
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="left" keep-together="always" font-size="11pt" line-height = "18pt" font-weight = "bold">Designation</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="left" font-size="11pt" line-height = "18pt">${(employeeValues.getValue().get("designation"))}</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid"> 
	                           			<fo:block text-align="left" font-size="11pt" line-height = "18pt" >Full Name: ${(employeeValues.getValue().get("signatoryName")).toUpperCase()}</fo:block>
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
		                           		
		                           		