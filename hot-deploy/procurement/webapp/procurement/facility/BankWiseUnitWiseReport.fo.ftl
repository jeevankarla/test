<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".5in" margin-top=".5in">
                <fo:region-body margin-top="1.2in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "bankWiseUnitWiseReport.txt")}
        <#if errorMessage?has_content>
		<fo:page-sequence master-reference="main">
		   <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
		      <fo:block font-size="14pt">
		              ${errorMessage}.
		   	  </fo:block>
		   </fo:flow>
		</fo:page-sequence>        
		<#else>
	<#if bankWiseMap?has_content>
		
			<fo:page-sequence master-reference="main">
				<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" >
					<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
					<fo:block text-align="left" white-space-collapse="false" keep-together="always">VST_ASCII-018&#160;    ${reportHeader.description?if_exists}</fo:block>
					<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : parameters.shedId}, true)>
					<fo:block keep-together="always" white-space-collapse="false" >BANK-WISE ABSTRACT</fo:block>
					<fo:block text-align="left" white-space-collapse="false" keep-together="always">PERIOD FROM   : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block>
					<fo:block text-align="left" white-space-collapse="false" keep-together="always">MILK SHED NAME: ${facilityDetails.facilityName?if_exists}</fo:block>
					<fo:block font-size="8pt">---------------------------------------------------</fo:block>
					<fo:block keep-together="always" white-space-collapse="false" font-size="8pt">CODE      NAME OF UNIT           AMOUNT</fo:block>
					<fo:block font-size="8pt">---------------------------------------------------</fo:block>
				</fo:static-content>
				<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
				<#assign totalShedAmt=0>
				<#assign nonBankUnitsAmt=0>
				<#if facilityAttr != "Y">
				<#if shortageUnitsList?has_content>
					<fo:block font-size="8pt" white-space-collapse="false" keep-together="always">NON BANK/ SOUR UNITS</fo:block>
					<#list shortageUnitsList as unitId>
						<#assign nonBankUnits = delegator.findOne("Facility", {"facilityId" : unitId}, true)>
						<#assign unitAmount=0>
						<#if UnitWiseDetailsMap.get(unitId)?has_content>
							<#if UnitWiseDetailsMap.get(unitId).get("milkValue")?has_content>
								<#assign unitAmount=UnitWiseDetailsMap.get(unitId).get("milkValue")>
							</#if>
						</#if>
						<#assign nonBankUnitsAmt=nonBankUnitsAmt+unitAmount>
						<fo:block font-size="8pt">
							<fo:table>
								<fo:table-column column-width="30pt"/>
								<fo:table-column column-width="110pt"/>
								<fo:table-column column-width="60pt"/>
								<fo:table-column column-width="80pt"/>
								<fo:table-body>																
										<fo:table-row>
											<fo:table-cell>
												<fo:block>${nonBankUnits.facilityCode?if_exists}</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block text-align="left" keep-together="always">${nonBankUnits.facilityName?if_exists}</fo:block>
											</fo:table-cell>													
											<fo:table-cell>
												<fo:block text-align="right">${unitAmount?string("#0.00")}</fo:block>
											</fo:table-cell>
										</fo:table-row>														
								</fo:table-body>
							</fo:table>
						</fo:block>
					</#list>
				</#if>
				<#if nonBankUnitsAmt !=0>
					<fo:block font-size="8pt">---------------------------------------------------</fo:block>
					<fo:block font-size="8pt">
						<fo:table>
							<fo:table-column column-width="30pt"/>
							<fo:table-column column-width="110pt"/>
							<fo:table-column column-width="60pt"/>
							<fo:table-column column-width="80pt"/>
							<fo:table-body>																
								<fo:table-row>
									<fo:table-cell>
										<fo:block>0</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="left" keep-together="always">NON BANK/SOUR UNITS</fo:block>
									</fo:table-cell>													
									<fo:table-cell>
										<fo:block text-align="right">${nonBankUnitsAmt?string("#0.00")}</fo:block>
									</fo:table-cell>
								</fo:table-row>														
							</fo:table-body>
						</fo:table>
					</fo:block>
					<fo:block font-size="8pt">---------------------------------------------------</fo:block>
				</#if>
				</#if>
				<#assign bankWiseDetailsList = bankWiseMap.entrySet()>	
				<#assign bankAmount=0>	
				<#list bankWiseDetailsList as bankWiseDetails>
					<#assign unitWiseDetailsList= bankWiseDetails.getValue().entrySet()>	
					<#assign unitAmount=0>
					<#assign sno=0>
					<fo:block font-size="8pt"><#if bankWiseDetails.getKey() !="noBankName">${bankWiseDetails.getKey()}</#if></fo:block>
					<#list unitWiseDetailsList as unitDetails>
						<#assign unitFacility = delegator.findOne("Facility", {"facilityId" : unitDetails.getKey()}, true)>							
						<fo:block font-size="8pt">
							<fo:table>
								<fo:table-column column-width="30pt"/>
								<fo:table-column column-width="110pt"/>
								<fo:table-column column-width="60pt"/>
								<fo:table-column column-width="80pt"/>
								<fo:table-body>
								<#assign centerWiseDetailsList=unitDetails.getValue()>									
									<#list centerWiseDetailsList as centerDetails>
										<#assign centersList= centerDetails.entrySet()>										
										<#list centersList as centerValues>
											<#assign sno=sno+1>
											<#assign amount=centerValues.getValue().get("amount")>
											<#if amount !=0>
												<#assign unitAmount=unitAmount+amount>	
											</#if>
										</#list>
									</#list>
									<#if unitAmount !=0>	
									<#assign bankAmount=bankAmount+unitAmount>											
										<fo:table-row>
											<fo:table-cell>
												<fo:block>${unitFacility.facilityCode?if_exists}</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block text-align="left" keep-together="always">${unitFacility.facilityName?if_exists}</fo:block>
											</fo:table-cell>													
											<fo:table-cell>
												<fo:block text-align="right">${unitAmount?if_exists?string("#0.00")}</fo:block>
											</fo:table-cell>
										</fo:table-row>	
										<#assign unitAmount=0>		
									</#if>								
								</fo:table-body>
							</fo:table>
						</fo:block>					
					</#list>
						<#if bankAmount !=0>
							<#assign totalShedAmt=totalShedAmt+bankAmount>
							<fo:block font-size="8pt">---------------------------------------------------</fo:block>
							<fo:block font-size="8pt">
								<fo:table>
									<fo:table-column column-width="30pt"/>
									<fo:table-column column-width="110pt"/>
									<fo:table-column column-width="60pt"/>
									<fo:table-column column-width="80pt"/>
									<fo:table-body>
										<fo:table-row>
											<fo:table-cell>
												<fo:block text-align="right"></fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block keep-together="always">${bankWiseDetails.getKey()}</fo:block>
											</fo:table-cell>										
											<fo:table-cell>
												<fo:block text-align="right">${bankAmount?if_exists?string("#0.00")}</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
							</fo:block>
							<fo:block font-size="8pt">---------------------------------------------------</fo:block>
							<#assign bankAmount=0>
						</#if>
					</#list>
					<fo:block font-size="8pt">---------------------------------------------------</fo:block>
					<fo:block font-size="8pt">
						<fo:table>
							<fo:table-column column-width="30pt"/>
							<fo:table-column column-width="110pt"/>
							<fo:table-column column-width="60pt"/>
							<fo:table-column column-width="80pt"/>
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell>
										<fo:block text-align="right"></fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block keep-together="always">${facilityDetails.facilityName?if_exists}</fo:block>
									</fo:table-cell>										
									<fo:table-cell>
										<fo:block text-align="right">${(nonBankUnitsAmt+totalShedAmt)}</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:block>
					<fo:block font-size="8pt">---------------------------------------------------</fo:block>	
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