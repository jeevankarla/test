<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".5in" margin-top=".5in" margin-bottom=".5in">
                <fo:region-body margin-top="1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "bankCenterWiseReport.txt")}
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
		<#assign bankWiseDetailsList = bankWiseMap.entrySet()>	
		<#assign bankAmount=0>	
	<#if bankWiseDetailsList?has_content>	
		<#list bankWiseDetailsList as bankWiseDetails>
			<fo:page-sequence master-reference="main">
				<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" >
					<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
					<fo:block text-align="left" white-space-collapse="false" keep-together="always">VST_ASCII-018&#160;             ${reportHeader.description?if_exists}</fo:block>
					<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : parameters.shedId}, true)>
					<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;  BANK-WISE ABSTRACT PERIOD FROM   : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block>
					<fo:block text-align="left" white-space-collapse="false" keep-together="always">MILK SHED NAME: ${facilityDetails.facilityName?if_exists}      NAME OF THE BANK : ${bankWiseDetails.getKey()}</fo:block>
					<fo:block font-size="8pt">----------------------------------------------------------------------------------------</fo:block>
					<fo:block keep-together="always" white-space-collapse="false" font-size="8pt">SNO      CODE     CENTER NAME         PRESIDENT NAME        ACCOUNT NO       AMOUNT</fo:block>
					<fo:block font-size="8pt">----------------------------------------------------------------------------------------</fo:block>
				</fo:static-content>
				<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
					<#assign unitWiseDetailsList= bankWiseDetails.getValue().entrySet()>	
					<#assign unitAmount=0>
					<#assign sno=0>
					<#assign printLine=0>
					<#assign tempPage = 0>
					<#list unitWiseDetailsList as unitDetails>
						<#assign centerWiseDetailsList=unitDetails.getValue()>
						<#assign tempPage =  tempPage+(centerWiseDetailsList.size())+4>
						<#if (tempPage>50)>
							<fo:block  page-break-before="always"></fo:block>
							<#assign printLine=0>
							<#assign tempPage=0>
							<#assign tempPage =  tempPage+(centerWiseDetailsList.size())+4>
						</#if>
						<#if centerWiseDetailsList?has_content>
							<#assign unitFacility = delegator.findOne("Facility", {"facilityId" : unitDetails.getKey()}, true)>
							<fo:block font-size="8pt">${unitFacility.facilityName?if_exists}</fo:block>
						</#if>	
						<fo:block font-size="8pt">
							<fo:table>
								<fo:table-column column-width="25pt"/>
								<fo:table-column column-width="35pt"/>
								<fo:table-column column-width="95pt"/>
								<fo:table-column column-width="100pt"/>
								<fo:table-column column-width="90pt"/>
								<fo:table-column column-width="50pt"/>
								<fo:table-body>
									<#list centerWiseDetailsList as centerDetails>
										<#assign centersList= centerDetails.entrySet()>
										<#list centersList as centerValues>
											<#assign amount=centerValues.getValue().get("amount")>
											<#if amount !=0>
												<#assign sno=sno+1>
												<#assign printLine=printLine+1>
												<#assign unitAmount=unitAmount+amount>												
												<fo:table-row>
													<fo:table-cell>
														<fo:block>${sno}</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="right">${centerValues.getValue().get("centerCode")}</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="left" text-indent="15pt" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(centerValues.getValue().get("centerName").toUpperCase())),15)}</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="left" text-indent="15pt" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(centerValues.getValue().get("partyName").toUpperCase())),18)}</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="right">${centerValues.getValue().get("accNo")?if_exists}</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="right">${amount?if_exists}</fo:block>
													</fo:table-cell>
												</fo:table-row>
												<#if printLine==50>
													<fo:table-row>
														<fo:table-cell>
															<fo:block  page-break-after="always"></fo:block>
														</fo:table-cell>
													</fo:table-row>
													<#assign printLine=0>
													<#assign tempPage=0>
													<#assign tempPage =  tempPage+(centerWiseDetailsList.size())+4>
												</#if>
											</#if>
										</#list>
									</#list>
								</fo:table-body>
							</fo:table>
						</fo:block>	
						<#if unitAmount !=0>
						<#assign bankAmount=bankAmount+unitAmount>
						<fo:block font-size="8pt">----------------------------------------------------------------------------------------</fo:block>
						<fo:block font-size="8pt">
							<fo:table>
								<fo:table-column column-width="30pt"/>
								<fo:table-column column-width="40pt"/>
								<fo:table-column column-width="60pt"/>
								<fo:table-column column-width="80pt"/>
								<fo:table-column column-width="150pt"/>
								<fo:table-column column-width="50pt"/>
								<fo:table-body>
									<fo:table-row>
										<fo:table-cell>
											<fo:block keep-together="always">${unitFacility.facilityName?if_exists}</fo:block>
										</fo:table-cell>									
										<fo:table-cell>
											<fo:block></fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block></fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block>TOTAL:</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block text-align="right"></fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block text-align="right">${unitAmount?if_exists?string("#0.00")}</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
						<fo:block font-size="8pt">----------------------------------------------------------------------------------------</fo:block>
							<#assign unitAmount=0>
						</#if>
					</#list>
					<#if bankAmount !=0>
						<fo:block font-size="8pt">----------------------------------------------------------------------------------------</fo:block>
						<fo:block font-size="8pt">
							<fo:table>
								<fo:table-column column-width="30pt"/>
								<fo:table-column column-width="40pt"/>
								<fo:table-column column-width="60pt"/>
								<fo:table-column column-width="80pt"/>
								<fo:table-column column-width="150pt"/>
								<fo:table-column column-width="50pt"/>
								<fo:table-body>
									<fo:table-row>
										<fo:table-cell>
											<fo:block keep-together="always">${bankWiseDetails.getKey()}</fo:block>
										</fo:table-cell>									
										<fo:table-cell>
											<fo:block></fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block></fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block>TOTAL:</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block text-align="right"></fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block text-align="right">${bankAmount?if_exists?string("#0.00")}</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
						<fo:block font-size="8pt">----------------------------------------------------------------------------------------</fo:block>
							<#assign bankAmount=0>
						</#if>
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