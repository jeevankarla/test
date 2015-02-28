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
    		<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
    			<#assign employeeDetailsMap = finalEmployeeMap.entrySet()>
    			<#list employeeDetailsMap as employeeValues>
	        		<fo:block  keep-together="always" text-align="center" font-family="Helvetica" white-space-collapse="false" font-size="10pt" font-weight = "bold">FORM NO.16</fo:block>
	        		<fo:block  keep-together="always" text-align="center" font-family="Helvetica" white-space-collapse="false" font-size="10pt" font-style = "italic">[See rule 31(1)(a)]</fo:block>
		            <fo:block  keep-together="always" text-align="center" font-family="Helvetica" white-space-collapse="false" font-size="10pt" font-weight = "bold" font-style = "italic">Certificate under section 203 of the Income-tax Act, 1961 for tax deducted</fo:block>
		            <fo:block  keep-together="always" text-align="center" font-family="Helvetica" white-space-collapse="false" font-size="10pt" font-weight = "bold" font-style = "italic">at source from income chargeable under the head "Salaries".</fo:block>
		            <fo:block text-align="center" keep-together="always" font-size="11pt">&#160;</fo:block>
		            <fo:block font-family="Helvetica">
		            	<fo:table border-style = "solid">
	                		<#assign totalAdditionAmount = 0>
	                		<#assign totalDeletionAmount = 0>
		                    <fo:table-column column-width="320pt"/>
		                    <fo:table-column column-width="320pt"/>
	                     	<fo:table-body> 
	                 			<fo:table-row >
	                           		<fo:table-cell border-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">Name and Address of the Employer</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt">${(employeeValues.getValue().get("employeeName")).toUpperCase()}</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt">${uiLabelMap.KMFDairyHeader}</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt">${uiLabelMap.KMFDairySubHeader}</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">Name and Designation of the Employee</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt">${(employeeValues.getValue().get("employeeName")).toUpperCase()}</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt">${employeeValues.getValue().get("employeePosition")}</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt">&#160;</fo:block>
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
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt">PAN No. of the Deductor</fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt">&#160;</fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt"><#if employeeValues.getValue().get("panNumberOfCompany")?has_content>${employeeValues.getValue().get("panNumberOfCompany")}<#else>&#160;</#if></fo:block>
						                           		</fo:table-cell>
						                           		<fo:table-cell >
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt" >TAN No. of the Deductor</fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt">&#160;</fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt"><#if employeeValues.getValue().get("tanNumberOfCompany")?has_content>${employeeValues.getValue().get("tanNumberOfCompany")}<#else>&#160;</#if></fo:block>
						                           		</fo:table-cell>
								                   	</fo:table-row>
								              	</fo:table-body>
		                           			</fo:table>
		                           		</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">PAN No. of the Employee</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt"><#if employeeValues.getValue().get("panNumberOfEmployee")?has_content>${employeeValues.getValue().get("panNumberOfEmployee")}<#else>&#160;</#if></fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row >
	                           		<fo:table-cell border-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">Acknowledgement Nos. of all quarterly</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">statements of TDS under sub-section (3) of </fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">section 200 as provided by TIN Facilitation </fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">Center or NSDL web-site</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid">
	                           			<fo:block>
		                           			<fo:table>
		                           				<fo:table-column column-width="160pt"/>
		                           				<fo:table-column column-width="160pt"/>
		                           				<fo:table-body> 
						                     		<fo:table-row >
							                           	<fo:table-cell border-right-style="solid">
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt">PERIOD</fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt">&#160;</fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "yyyy")}  - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "yyyy")} </fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt">&#160;</fo:block>
						                           		</fo:table-cell>
						                           		<fo:table-cell >
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt" >Assessment Year</fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt">&#160;</fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(employeeValues.getValue().get("subSequentFromDate"), "yyyy")}  - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(employeeValues.getValue().get("subSequentThruDate"), "yyyy")}</fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt">&#160;</fo:block>
						                           		</fo:table-cell>
								                   	</fo:table-row>
								              	</fo:table-body>
		                           			</fo:table>
		                           		</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row >
	                           		<fo:table-cell border-style = "solid">
	                           			<fo:block>
		                           			<fo:table>
		                           				<fo:table-column column-width="100pt"/>
		                           				<fo:table-column column-width="220pt"/>
		                           				<fo:table-body> 
						                     		<fo:table-row >
							                           	<fo:table-cell >
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt">Quarter</fo:block>
						                           		</fo:table-cell>
						                           		<fo:table-cell >
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt" >Acknowledgement No.</fo:block>
						                           		</fo:table-cell>
								                   	</fo:table-row>
								              	</fo:table-body>
		                           			</fo:table>
		                           		</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid">
	                           			<fo:block>
		                           			<fo:table>
		                           				<fo:table-column column-width="80pt"/>
		                           				<fo:table-column column-width="80pt"/>
		                           				<fo:table-column column-width="160pt"/>
		                           				<fo:table-body> 
						                     		<fo:table-row >
							                           	<fo:table-cell border-right-style="solid">
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt">From</fo:block>
						                           		</fo:table-cell>
						                           		<fo:table-cell border-right-style="solid">
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt">To</fo:block>
						                           		</fo:table-cell>
						                           		<fo:table-cell >
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt" >&#160;</fo:block>
						                           		</fo:table-cell>
								                   	</fo:table-row>
								              	</fo:table-body>
		                           			</fo:table>
		                           		</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row >
	                           		<fo:table-cell border-style = "solid">
	                           			<fo:block>
		                           			<fo:table>
		                           				<fo:table-column column-width="100pt"/>
		                           				<fo:table-column column-width="220pt"/>
		                           				<fo:table-body> 
						                     		<fo:table-row >
							                           	<fo:table-cell >
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt">Quarter 1</fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt">Quarter 2</fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt">Quarter 3</fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt">Quarter 4</fo:block>
						                           		</fo:table-cell>
						                           		<fo:table-cell >
						                           			<#if acknowlemntMap?has_content>
						                           				<fo:block text-align="center" keep-together="always" font-size="10pt" >${acknowlemntMap.get("quarter1")}</fo:block>
						                           				<fo:block text-align="center" keep-together="always" font-size="10pt" >${acknowlemntMap.get("quarter2")}</fo:block>
						                           				<fo:block text-align="center" keep-together="always" font-size="10pt" >${acknowlemntMap.get("quarter3")}</fo:block>
						                           				<fo:block text-align="center" keep-together="always" font-size="10pt" >${acknowlemntMap.get("quarter4")}</fo:block>
						                           			<#else>
						                           				<fo:block text-align="center" keep-together="always" font-size="10pt" >&#160;</fo:block>
						                           			</#if>
						                           		</fo:table-cell>
								                   	</fo:table-row>
								              	</fo:table-body>
		                           			</fo:table>
		                           		</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid">
	                           			<fo:block>
		                           			<fo:table>
		                           				<fo:table-column column-width="80pt"/>
		                           				<fo:table-column column-width="80pt"/>
		                           				<fo:table-column column-width="160pt"/>
		                           				<fo:table-body> 
						                     		<fo:table-row >
							                           	<fo:table-cell border-right-style="solid">
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt" >&#160;</fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt" >&#160;</fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt" >&#160;</fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt" >&#160;</fo:block>
						                           		</fo:table-cell>
						                           		<fo:table-cell border-right-style="solid">
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt" >&#160;</fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt" >&#160;</fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt" >&#160;</fo:block>
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt" >&#160;</fo:block>
						                           		</fo:table-cell>
						                           		<fo:table-cell >
						                           			<fo:block text-align="center" keep-together="always" font-size="10pt" ></fo:block>
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
	           		<fo:block text-align="center" keep-together="always" font-size="10pt" >&#160;</fo:block>
	           		<fo:block text-align="center" keep-together="always" font-size="12pt" font-weight = "bold"> DETAILS OF SALARY PAID AND ANY OTHER INCOME AND TAX DEDUCTED</fo:block>
	           		<fo:block font-family="Helvetica">
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
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">1. Gross Salary</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">&#160;</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid" >
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "13pt">&#160;&#160;&#160;&#160;(a)  &#160;&#160;Salary as per provisions contained in section 17(1)</fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "13pt">&#160;&#160;&#160;&#160;(b)  &#160;&#160;Value of perquisites under section 17(2)</fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "13pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(as per Form No. 12BA, wherever applicable)</fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "13pt">&#160;&#160;&#160;&#160;(c)  &#160;&#160;Profits in lieu of salary under section 17(3)</fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "13pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(as per Form No. 12BA, wherever applicable) </fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "13pt">&#160;&#160;&#160;&#160;(d)  &#160;&#160;Total</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">Rs.____________</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">Rs.____________</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">Rs.____________</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "13pt">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "13pt">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "13pt">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "13pt">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "13pt">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "13pt">Rs. <#if employeeValues.getValue().get("totalEarnings")?has_content>${employeeValues.getValue().get("totalEarnings")}<#else>0.00</#if></fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">&#160;</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">2. Less : Allowance to the extent exempt under section 10</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "15pt">Rs.____________</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "15pt">Rs.____________</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "15pt">Rs.____________</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
		                           		<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "15pt">&#160;</fo:block>
		                           		<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "15pt">&#160;</fo:block>
		                           		<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "15pt">Rs.____________</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">&#160;</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">3. Balance (1-2)</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
		                           		<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "15pt">Rs.____________</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">&#160;</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">4. Deductions :</fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">&#160;&#160;&#160;&#160;(a) Entertainment allowance &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Rs.________________</fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">&#160;&#160;&#160;&#160;(b) Tax on Employment &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Rs. <#if employeeValues.getValue().get("professionalTax")?has_content>${(employeeValues.getValue().get("professionalTax"))*(-1)}<#else> ________________</#if></fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
		                           		<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">&#160;</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">5. Aggregate of 4(a) and (b)</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "15pt">Rs.____________</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
		                           		<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">&#160;</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">6. Income chargeable under the Head ' Salaries' (3-5)</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
		                           		<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">Rs.____________</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">7. Add : Any other income reported by employee</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "15pt">Rs.____________</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "15pt">Rs.____________</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "15pt">Rs.____________</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
		                           		<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "15pt">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "15pt">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">Rs.____________</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">8. Gross total income (6 +7)</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
		                           		<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">Rs.____________</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">9. Deductions Under Chapter VI-A</fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">&#160;&#160;&#160;&#160;(A) sections 80C,80CCC and 80CCD</fo:block>
	                           			<fo:block text-align="right" keep-together="always" font-size="9pt" line-height = "15pt">Gross Amount&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "15pt">Qualifying Amount</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
		                           		<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "15pt">Deductible Amount</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">Rs.____________</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(a) section 80C</fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(i)&#160;&#160;  __________________  &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; Rs.________________  </fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(ii)&#160; __________________  &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; Rs.________________</fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(iii) __________________  &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; Rs.________________</fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(iv) __________________  &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; Rs.________________</fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(v)&#160; __________________  &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; Rs.________________</fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(ivi) __________________  &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; Rs.________________</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">Rs.______________</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">Rs.______________</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">Rs.______________</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">Rs.______________</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">Rs.______________</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">Rs.______________</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
		                           		<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">Rs.______________</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">Rs.______________</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">Rs.______________</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">Rs.______________</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">Rs.______________</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">Rs.______________</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">Rs.____________</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(b) section 80CCC</fo:block>
			             				<fo:block text-align="right" keep-together="always" font-size="9pt" line-height = "18pt">Gross Amount&#160;&#160;&#160;&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">Rs.______________</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">Rs.______________</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">Rs.______________</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(c) section 80CCD</fo:block>
			             				<fo:block text-align="right" keep-together="always" font-size="9pt" line-height = "18pt">Gross Amount&#160;&#160;&#160;&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">Rs.______________</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">Rs.______________</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">Rs.______________</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">&#160;&#160;&#160;&#160;(B) Other sections (e.g. 80E,80G,ect.) Under Chapter VI-A </fo:block>
			             				<fo:block text-align="right" keep-together="always" font-size="9pt" line-height = "18pt">Gross Amount&#160;&#160;&#160;&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">Qualifying Amount</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">Deductible Amount</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(a) section 80C</fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(a) section  &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; Rs.________________  </fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(b) section  &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; Rs.________________</fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(c) section  &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; Rs.________________</fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(d) section  &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; Rs.________________</fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(e) section  &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; Rs.________________</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">Rs.______________</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">Rs.______________</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">Rs.______________</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">Rs.______________</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">Rs.______________</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
		                           		<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">Rs.______________</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">Rs.______________</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">Rs.______________</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">Rs.______________</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">Rs.______________</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">Rs.____________</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">(10) Aggregate of deductible amount under Chpater VI-A </fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">(11) Total income (8-10)&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Rs.____________ </fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">(12) Tax on total income&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Rs.____________ </fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">(13) Surcharge(on tax computed at S.No.12) &#160;&#160;&#160;Rs.____________</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">Rs.____________</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">(14) Education Cess @ 2% on (tax at S.No.12 &#160;&#160;&#160;Rs.____________</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">Rs.____________</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">(15) Tax payable (12+13+14) &#160;&#160;&#160;Rs.____________</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">Rs.____________</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">(16) Relief Under Section 89 (attach details) &#160;&#160;&#160;Rs.____________</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">Rs.______________</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">(17) Tax payable (15-16) &#160;&#160;&#160;Rs.____________</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">Rs.______________</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">(18) Less :</fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;(a) Tax deducted at source u/s 192(1)</fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;(b) Tax Paid by the employer on behalf of the employee </fo:block>
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; u/s 192(1A) on Perquisites u/s 17 (2)</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">Rs.______________</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">Rs.______________</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">&#160;</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">Rs.______________</fo:block>
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt" line-height = "18pt">Rs.______________</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">(19) Tax payable/refundable (17-18)</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell >
	                           			<fo:block text-align="center" keep-together="always" font-size="10pt">Rs.______________</fo:block>
	                           		</fo:table-cell>
			                   	</fo:table-row>
			             	</fo:table-body>
	           			</fo:table>
	           		</fo:block>
	           		<fo:block text-align="center" keep-together="always" font-size="10pt">&#160;</fo:block>
	           		<fo:block text-align="center" keep-together="always" font-size="12pt" font-weight = "bold">DETAILS OF TAX DEDUCTED AND DEPOSITED INTO GOVERNMENT ACCOUNT</fo:block>
	           		<fo:block>
	           			<fo:table border-style = "solid">
		                    <fo:table-column column-width="100pt"/>
		                    <fo:table-column column-width="170pt"/>
		                    <fo:table-column column-width="370pt"/>
	                     	<fo:table-body> 
	                 			<fo:table-row >
	                 				<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "15pt">AMOUNT</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">DATE OF PAYMENT</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-right-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "18pt">NAME OF BANK AND BRANCH WHERE TAX DEPOSITED</fo:block>
	                           		</fo:table-cell>
	           					</fo:table-row>
	           					<fo:table-row border-style = "solid">
	                 				<fo:table-cell border-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	           					</fo:table-row>
	           					<fo:table-row border-style = "solid">
	                 				<fo:table-cell border-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	           					</fo:table-row>
	           					<fo:table-row border-style = "solid">
	                 				<fo:table-cell border-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	           					</fo:table-row>
	           					<fo:table-row border-style = "solid">
	                 				<fo:table-cell border-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell border-style = "solid">
	                           			<fo:block text-align="center" keep-together="always" font-size="9pt" line-height = "15pt">&#160;</fo:block>
	                           		</fo:table-cell>
	           					</fo:table-row>
			             	</fo:table-body>
	           			</fo:table>
	           		</fo:block>
	           		<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">I ______________________________________________________________________ son of ______________________________________</fo:block>
	           		<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">working in the capacity of____________________(designation) do hereby certify that a sum of Rs.____________________________________</fo:block>
	           		<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">at source and paid to the credit of the Central Government. I further certify that the information given above is true and correct based on book of</fo:block>
	           		<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">accounts, documents and other available records.</fo:block>
	           		<fo:block text-align="right" keep-together="always" font-size="11pt" line-height = "5pt">______________________________________________</fo:block>
	           		<fo:block text-align="right" keep-together="always" font-size="10pt" line-height = "18pt" font-style = "italic">Signature of the person responsible for deduction of tax</fo:block>
	           		<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">Place __________________ &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; Full Name _____________________________________</fo:block>
	           		<fo:block text-align="left" keep-together="always" font-size="10pt" line-height = "18pt">Date __________________ &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; Designation  _____________________________________</fo:block>
           			<fo:block page-break-after="always"></fo:block>          
           		</#list>
           	</fo:flow>
		</fo:page-sequence>
	<#else>
		<fo:page-sequence master-reference="main">
			<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
   		 		<fo:block font-size="14pt">
        			No Employee Found.......!
   		 		</fo:block>
			</fo:flow>
		</fo:page-sequence>	
	</#if>
   </fo:root>
</#escape>