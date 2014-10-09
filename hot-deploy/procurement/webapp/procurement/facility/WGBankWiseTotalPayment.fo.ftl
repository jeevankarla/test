<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-top=".3in">
                <fo:region-body margin-top="1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "WGBankWiseTotalPayment.txt")}
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
		
		<#assign Total=0>
		
			<fo:page-sequence master-reference="main">
				<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" >
					<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
					
					
					<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : parameters.shedId}, true)>
					<fo:block text-align="left" white-space-collapse="false" keep-together="always">NAME FO THE SHED : ${facilityDetails.facilityName?if_exists}</fo:block>
					<fo:block text-align="left" white-space-collapse="false" keep-together="always">  PERIOD FROM   : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block>
					<fo:block text-align="left" white-space-collapse="false" keep-together="always"></fo:block>
					<fo:block >---------------------------------------------</fo:block>
					<fo:block>
						<fo:table>
							<fo:table-column column-width="200pt"/>
							<fo:table-column column-width="150pt"/>
							<fo:table-column column-width="170pt"/>
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell>
										<fo:block keep-together="always">NAME OF THE BANK</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block keep-together="always">AMOUNT</fo:block>
									</fo:table-cell>
								</fo:table-row>									
							</fo:table-body>
						</fo:table>
					</fo:block>
					<fo:block >----------------------------------------------</fo:block>
				</fo:static-content>
				<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
					<fo:block></fo:block>
				<#list bankDetailsList as bankWiseDetails>	
					<#assign bankGrandTotal=0>
						<#assign branchDetailsList=bankWiseDetails.getValue().entrySet()>
					<fo:block>
						<fo:table>
							<fo:table-column column-width="50pt"/>
							<fo:table-column column-width="200pt"/>
							<fo:table-column column-width="170pt"/>
							<fo:table-column column-width="160pt"/>
							<fo:table-column column-width="130pt"/>
							<fo:table-column column-width="130pt"/>
							<fo:table-column column-width="160pt"/>
							<fo:table-column column-width="95pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-body>
															
								<#list branchDetailsList as branchDetails>
									<#assign branchTotalAmt=0>
									<#assign branchWiseList = branchDetails.getValue()>
									<#list branchWiseList as branchCenterDetails>
										<#assign centerDetailsList=branchCenterDetails.entrySet()>
									<#list centerDetailsList as centerDetails>
										<#if centerDetails.getValue().get("amount") !=0>											
											<#if centerDetails.getValue().get("isDisplay")=="Y">
												<#assign branchTotalAmt=branchTotalAmt+centerDetails.getValue().get("amount")>
											
											</#if>
										</#if>	
									</#list>
									</#list>
									<#if ddAmountMap?has_content>
										<#if ddAmountMap.get("nameOfTheBank")==bankWiseDetails.getKey()>
										<#if ddAmountMap.get("nameOfTheBrch")==branchDetails.getKey()>
											<#assign branchTotalAmt=branchTotalAmt+ddAmountMap.get("netAmtPayable")>
											
											</#if>
											</#if>	
									</#if>
									<#if branchTotalAmt !=0>
										<#assign bankGrandTotal=bankGrandTotal+branchTotalAmt>
										
									</#if>	
								</#list>	
								<#if bankWiseDetails.getKey()=="STATE BANK OF INDIA">
									<#if transferBankMap?has_content>
										<#assign trnsferBankDetails=transferBankMap.entrySet()>
										<#list trnsferBankDetails as transferDetails>
											<#assign trnsfBranchDetails=transferDetails.getValue().entrySet()>
											<#list trnsfBranchDetails as trnsfDetails>
											
											
											<#assign bankGrandTotal=bankGrandTotal+trnsfDetails.getValue().get("amount")>
											
											</#list>
										</#list>
									</#if>
								</#if>
								<#if bankGrandTotal !=0>
									<fo:table-row>
											<fo:table-cell>
												<fo:block keep-together="always">${bankWiseDetails.getKey()}</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block text-align="right">${bankGrandTotal?if_exists}</fo:block>
											</fo:table-cell>
											<#assign Total=Total+bankGrandTotal>
											<#assign bankGrandTotal=0>		
										
								</fo:table-row>								
								</#if>
								
						</fo:table-body>
						</fo:table>	
						
										
					</fo:block>		
					</#list>	
					<fo:block>
					
					<fo:table>
							<fo:table-column column-width="50pt"/>
							<fo:table-column column-width="200pt"/>
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell>
										<fo:block >----------------------------------------------</fo:block>
									</fo:table-cell>
								</fo:table-row>	
								<fo:table-row>
									<fo:table-cell>
										<fo:block keep-together="always">TOTAL</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="right">${Total}</fo:block>
									</fo:table-cell>
								</fo:table-row>	
								<fo:table-row>
									<fo:table-cell>
										<fo:block >----------------------------------------------</fo:block>
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