<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".5in" margin-top=".5in">
                <fo:region-body margin-top="1.2in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "bankBranchWiseReport.txt")}
        <#if errorMessage?has_content>
		<fo:page-sequence master-reference="main">
		   <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
		      <fo:block font-size="14pt">
		              ${errorMessage}.
		   	  </fo:block>
		   </fo:flow>
		</fo:page-sequence>        
		<#else>
	<#if bankBranchWiseMap?has_content>
			<fo:page-sequence master-reference="main">
				<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" >
					<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
					<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;             ${reportHeader.description?if_exists}</fo:block>
					<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : parameters.shedId}, true)>
					<fo:block text-align="left" white-space-collapse="false" keep-together="always">BANK-BRANCH-WISE MILK PAYMENT   PERIOD FROM   : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block>
					<fo:block text-align="left" white-space-collapse="false" keep-together="always">MILK SHED NAME: ${(facilityDetails.facilityName).toUpperCase()?if_exists}          </fo:block>
					<fo:block text-align="left">----------------------------------------------------------------------</fo:block>
					<fo:block keep-together="always" white-space-collapse="false" font-size="9pt">SNO      BANK NAME          					   BRANCH NAME       		AMOUNT</fo:block>
					<fo:block text-align="left">----------------------------------------------------------------------</fo:block>
				</fo:static-content>
				<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
					<#assign bankWiseDetailsList = bankBranchWiseMap.entrySet()>	
					<#assign sno = 0>
					<#assign totalAmount = 0>
					<#assign banksTotalAmt = 0>
					<#list bankWiseDetailsList as bankWiseDetails>
						<#assign bankTot = 0>
						<#assign bankName = bankWiseDetails.getKey()>
						<#assign branchMap = bankWiseDetails.getValue()>
						
						<fo:block font-size="8pt">
							<fo:table>
								<fo:table-column column-width="30pt"/>
								<fo:table-column column-width="150pt"/>
								<fo:table-column column-width="80pt"/>
								<fo:table-column column-width="70pt"/>
								<fo:table-body>
										<#assign keys = branchMap.keySet()>
										<#list keys as key>
										<#if branchMap.get(key)!=0>
										<#assign sno = sno+1>
												<#assign bankTot = bankTot+branchMap.get(key)>
												<#assign totalAmount = totalAmount+branchMap.get(key)>
												<fo:table-row>
													<fo:table-cell>
														<fo:block text-align="left">${sno}</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="left" keep-together="always">${bankName}</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="left" text-indent="15pt" keep-together="always">${key}</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="right">${branchMap.get(key)?if_exists?string("#0.00")}</fo:block>
													</fo:table-cell>
												</fo:table-row>
												</#if>
											</#list>	
									<#if bankTot!=0>
									<#assign banksTotalAmt = banksTotalAmt+bankTot>
									<fo:table-row>
										<fo:table-cell>
											<fo:block text-align="left">----------------------------------------------------------------------</fo:block>
										</fo:table-cell>
									</fo:table-row>
										<fo:table-row>
													<fo:table-cell>
														<fo:block text-align="left"></fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="left" keep-together="always">${bankName}</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="left" text-indent="15pt" keep-together="always">&#160;</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="right">${bankTot?if_exists?string("#0.00")}</fo:block>
													</fo:table-cell>
												</fo:table-row>
									<fo:table-row>
										<fo:table-cell>
											<fo:block text-align="left">----------------------------------------------------------------------</fo:block>
										</fo:table-cell>
									</fo:table-row>
									</#if>		
								</fo:table-body>
							</fo:table>
						</fo:block>
					</#list>
					<#if ddAmountMap?has_content>
						<#if ddAmountMap.get("amount")!=0>
						<#assign ddAmount = netAmountPayable-banksTotalAmt>
						<#assign totalAmount = totalAmount+ddAmount>
						<fo:block font-size="8pt">
							<fo:table>
								<fo:table-column column-width="30pt"/>
								<fo:table-column column-width="150pt"/>
								<fo:table-column column-width="80pt"/>
								<fo:table-column column-width="70pt"/>
								<fo:table-body>
												<fo:table-row>
													<fo:table-cell>
														<fo:block text-align="left">DD</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="left" keep-together="always">${ddAmountMap.accountHolder}</fo:block>
														<fo:block text-align="left" keep-together="always">${ddAmountMap.nameOfTheBank}</fo:block>
														<fo:block text-align="left" keep-together="always">ACC NO :${ddAmountMap.bankAccNo}</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="left" text-indent="15pt" keep-together="always">${ddAmountMap.nameOfTheBrch}</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="right">${ddAmount?if_exists?string("#0.00")}</fo:block>
													</fo:table-cell>
												</fo:table-row>
												
								</fo:table-body>
							</fo:table>
						</fo:block>
						</#if>
					</#if>
					<fo:block text-align="left">----------------------------------------------------------------------</fo:block>
					<fo:block font-size="8pt">
							<fo:table>
								<fo:table-column column-width="30pt"/>
								<fo:table-column column-width="150pt"/>
								<fo:table-column column-width="80pt"/>
								<fo:table-column column-width="75pt"/>
								<fo:table-body>
												<fo:table-row>
													<fo:table-cell>
														<fo:block text-align="left">&#160;</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="left" keep-together="always">TOTAL AMOUNT</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="left" text-indent="15pt" keep-together="always">&#160;</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="right">${totalAmount?if_exists?string("#0.00")}</fo:block>
													</fo:table-cell>
												</fo:table-row>
												
								</fo:table-body>
							</fo:table>
						</fo:block>
						<fo:block text-align="left">----------------------------------------------------------------------</fo:block>
						<fo:block font-size="8pt">
							<fo:table>
								<fo:table-column column-width="30pt"/>
								<fo:table-column column-width="150pt"/>
								<fo:table-column column-width="80pt"/>
								<fo:table-column column-width="75pt"/>
								<fo:table-body>
												<fo:table-row>
													<fo:table-cell>
														<fo:block text-align="left">&#160;</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="left">&#160;</fo:block>
														<fo:block text-align="left">&#160;</fo:block>
														<fo:block text-align="left">&#160;</fo:block>
														<fo:block text-align="left" keep-together="always">MANAGER</fo:block>
														<fo:block text-align="left" keep-together="always">MILK CHILLING CENTER</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="left" text-indent="15pt" keep-together="always">&#160;</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="left">&#160;</fo:block>
														<fo:block text-align="left">&#160;</fo:block>
														<fo:block text-align="left">&#160;</fo:block>
														<fo:block text-align="CENTER">DEPUTY DIRECTOR</fo:block>
														<fo:block text-align="CENTER">A.P.D.D.C.F.LTD</fo:block>
													</fo:table-cell>
												</fo:table-row>
												
								</fo:table-body>
							</fo:table>
						</fo:block>
				</fo:flow>		
			</fo:page-sequence>
		
	<#else>
		<fo:page-sequence master-reference="main">
	    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	       		 <fo:block font-size="14pt">
	            	${uiLabelMap.OrderNoOrderFound}.
	       		 </fo:block>
	    	</fo:flow>
		</fo:page-sequence>	
	</#if>
	</#if>		
	</fo:root>
</#escape>