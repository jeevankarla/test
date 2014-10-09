<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-left=".3in" margin-top=".3in">
                <fo:region-body margin-top="1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "centerWisePayment.txt")}
        <#if errorMessage?has_content>
		<fo:page-sequence master-reference="main">
		   <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
		      <fo:block font-size="14pt">
		              ${errorMessage}.
		   	  </fo:block>
		   </fo:flow>
		</fo:page-sequence>        
		<#else>
	<#if bankBranchCenterWiseMap?has_content>
		<#assign bankDetailsList = bankBranchCenterWiseMap.entrySet()>	
		<#assign bankGrandTotal=0>
		<#list bankDetailsList as bankWiseDetails>	
			<fo:page-sequence master-reference="main">
				<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" >
					<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
					<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;             ${reportHeader.description?if_exists}</fo:block>
					<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;             STATEMENT SHOWING THE MILK BILL PAYMENT OF CENTER-WISE BANK-WISE ABSTRACT</fo:block>
					<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : parameters.shedId}, true)>
					<fo:block text-align="left" white-space-collapse="false" keep-together="always">NAME FO THE SHED : ${facilityDetails.facilityName?if_exists}  PERIOD FROM   : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block>
					<fo:block text-align="left" white-space-collapse="false" keep-together="always"></fo:block>
					<fo:block >---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
					<fo:block>
						<fo:table>
							<fo:table-column column-width="50pt"/>
							<fo:table-column column-width="150pt"/>
							<fo:table-column column-width="170pt"/>
							<fo:table-column column-width="180pt"/>
							<fo:table-column column-width="130pt"/>
							<fo:table-column column-width="190pt"/>
							<fo:table-column column-width="140pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell>
										<fo:block>CODE</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block keep-together="always">CENTER NAME</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block keep-together="always">NAME OF THE BANK</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block keep-together="always">NAME OF THE BRANCH</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block keep-together="always">IFSCNO</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block keep-together="always">ACCOUNT HOLDER</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block keep-together="always">BANK A/C</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block keep-together="always">AMOUNT</fo:block>
									</fo:table-cell>
								</fo:table-row>									
							</fo:table-body>
						</fo:table>
					</fo:block>
					<fo:block >---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				</fo:static-content>
				<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
					<fo:block></fo:block>
					
						<#assign branchDetailsList=bankWiseDetails.getValue().entrySet()>
					<fo:block>
						<fo:table>
							<fo:table-column column-width="50pt"/>
							<fo:table-column column-width="160pt"/>
							<fo:table-column column-width="170pt"/>
							<fo:table-column column-width="160pt"/>
							<fo:table-column column-width="130pt"/>
							<fo:table-column column-width="130pt"/>
							<fo:table-column column-width="160pt"/>
							<fo:table-column column-width="95pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell></fo:table-cell>
								</fo:table-row>								
								<#list branchDetailsList as branchDetails>
									<#assign branchTotalAmt=0>
									<#assign branchWiseList = branchDetails.getValue()>
									<#list branchWiseList as branchCenterDetails>
										<#assign centerDetailsList=branchCenterDetails.entrySet()>
									<#list centerDetailsList as centerDetails>
										<#if centerDetails.getValue().get("amount") !=0>											
											<#if centerDetails.getValue().get("isDisplay")=="Y">
												<#assign branchTotalAmt=branchTotalAmt+centerDetails.getValue().get("amount")>
											<fo:table-row>
												<fo:table-cell>
													<fo:block>${centerDetails.getValue().get("centerCode")}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(centerDetails.getValue().get("centerName"))),19)}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block keep-together="always">${centerDetails.getValue().get("bankName")?if_exists}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block keep-together="always">${centerDetails.getValue().get("branchName")?if_exists}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block keep-together="always">${centerDetails.getValue().get("ifscNo")?if_exists}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block keep-together="always">${centerDetails.getValue().get("partyName")?if_exists}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block text-align="right">${centerDetails.getValue().get("accNo")?if_exists}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block text-align="right">${centerDetails.getValue().get("amount")?if_exists}</fo:block>
												</fo:table-cell>
											</fo:table-row>	
											</#if>
										</#if>	
									</#list>
									</#list>
									<#if ddAmountMap?has_content>
										<#if ddAmountMap.get("nameOfTheBank")==bankWiseDetails.getKey()>
										<#if ddAmountMap.get("nameOfTheBrch")==branchDetails.getKey()>
											<#assign branchTotalAmt=branchTotalAmt+ddAmountMap.get("netAmtPayable")>
											<fo:table-row>
												<fo:table-cell>
													<fo:block>${ddAmountMap.get("unitCode")?if_exists}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(ddAmountMap.get("nameOfUnit"))),19)}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block keep-together="always">${ddAmountMap.get("nameOfTheBank")?if_exists}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block keep-together="always">${ddAmountMap.get("nameOfTheBrch")?if_exists}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block keep-together="always">${ddAmountMap.get("ifscCode")?if_exists}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block keep-together="always">${ddAmountMap.get("accountHolder")?if_exists}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block text-align="right">${ddAmountMap.get("bankAccNo")?if_exists}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block text-align="right">${ddAmountMap.get("netAmtPayable")?if_exists}</fo:block>
												</fo:table-cell>
											</fo:table-row>
											</#if>
											</#if>	
									</#if>
									<#if branchTotalAmt !=0>
										<#assign bankGrandTotal=bankGrandTotal+branchTotalAmt>
										<fo:table-row>
											<fo:table-cell>
												<fo:block >---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
											</fo:table-cell>
										</fo:table-row>
										<fo:table-row>
											<fo:table-cell>
												<fo:block></fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block keep-together="always"></fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block keep-together="always">${bankWiseDetails.getKey()}</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block keep-together="always">${branchDetails.getKey()}</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block keep-together="always"></fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block keep-together="always">TOTAL</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block text-align="right"></fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block text-align="right">${branchTotalAmt?if_exists}</fo:block>
											</fo:table-cell>
										</fo:table-row>		
										<fo:table-row>
											<fo:table-cell>
												<fo:block >---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
											</fo:table-cell>
										</fo:table-row>	
									</#if>	
								</#list>	
								<#if bankWiseDetails.getKey()=="STATE BANK OF INDIA">
									<#if transferBankMap?has_content>
										<#assign trnsferBankDetails=transferBankMap.entrySet()>
										<#list trnsferBankDetails as transferDetails>
											<#assign trnsfBranchDetails=transferDetails.getValue().entrySet()>
											<#list trnsfBranchDetails as trnsfDetails>
											<fo:table-row>
												<fo:table-cell>
													<fo:block></fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block keep-together="always"></fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block keep-together="always">${trnsfDetails.getValue().get("TrnsferBankName")?if_exists}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block keep-together="always">${trnsfDetails.getValue().get("TrnsferBranch")?if_exists}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block keep-together="always" >${trnsfDetails.getValue().get("TrnsfIfcNo")?if_exists}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block keep-together="always">${trnsfDetails.getValue().get("partyName")?if_exists}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block text-align="right">${trnsfDetails.getValue().get("TrnsfAcNo")?if_exists}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block text-align="right">${trnsfDetails.getValue().get("amount")?if_exists}</fo:block>
												</fo:table-cell>
											</fo:table-row>	
											<fo:table-row>
												<fo:table-cell>
													<fo:block >---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<fo:table-row>
												<fo:table-cell>
													<fo:block></fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block keep-together="always"></fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block keep-together="always">${transferDetails.getKey()}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block keep-together="always">${trnsfDetails.getKey()}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block keep-together="always"></fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block keep-together="always">TOTAL</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block text-align="right"></fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block text-align="right">${trnsfDetails.getValue().get("amount")?if_exists}</fo:block>
												</fo:table-cell>
											</fo:table-row>	
											<#assign bankGrandTotal=bankGrandTotal+trnsfDetails.getValue().get("amount")>
											<fo:table-row>
												<fo:table-cell>
													<fo:block >---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
												</fo:table-cell>
											</fo:table-row>
											</#list>
										</#list>
									</#if>
								</#if>
								<#if bankGrandTotal !=0>
										<fo:table-row>
											<fo:table-cell>
												<fo:block >---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
											</fo:table-cell>
										</fo:table-row>
										<fo:table-row>
											<fo:table-cell>
												<fo:block></fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block keep-together="always">GRAND TOTAL</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block keep-together="always"></fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block keep-together="always"></fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block keep-together="always"></fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block keep-together="always"></fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block text-align="right"></fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block text-align="right">${bankGrandTotal?if_exists}</fo:block>
											</fo:table-cell>
										</fo:table-row>		
										<fo:table-row>
											<fo:table-cell>
												<fo:block >---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
											</fo:table-cell>
										</fo:table-row>	
										<#assign bankGrandTotal=0>
									</#if>								
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
	            	${uiLabelMap.OrderNoOrderFound}.
	       		 </fo:block>
	    	</fo:flow>
		</fo:page-sequence>	
	</#if>
	</#if>		
	</fo:root>
</#escape>