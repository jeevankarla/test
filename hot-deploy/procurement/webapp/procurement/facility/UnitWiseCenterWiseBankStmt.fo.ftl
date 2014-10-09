<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-left=".3in" margin-top=".5in">
                <fo:region-body margin-top="1.2in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "unitCenterwiseBankStmt.txt")}
        <#assign pageStart= parameters.pageStart>
        <#assign pageEnd= parameters.pageEnd>       
        ${setRequestAttribute("VST_PAGE_START", "${pageStart}")}
        ${setRequestAttribute("VST_PAGE_END", "${pageEnd}")} 
<#if errorMessage?has_content>
<fo:page-sequence master-reference="main">
       <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
          <fo:block font-size="14pt">
                  ${errorMessage}.
       </fo:block>
       </fo:flow>
</fo:page-sequence>        
<#else>
    <#if bankBranchIfscWiseMap?has_content>
    	<#assign bankList = bankBranchIfscWiseMap.keySet()>
 		<#list bankList as bankName>   
    		<#assign branchMap = bankBranchIfscWiseMap.get(bankName)>
    		<#assign branchList = branchMap.keySet()>
    		<#list branchList as branchName>
    			<#assign ifscMap = branchMap.get(branchName)>
    			<#assign ifscList = ifscMap.keySet()>
    			<#list ifscList as ifscNo>
    		
				<fo:page-sequence master-reference="main">
					<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
						<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
						<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;           ${reportHeader.description?if_exists}       PAGE NO:<fo:page-number/></fo:block>
						<fo:block text-align="left" white-space-collapse="false" keep-together="always">B  A  N  K  L  E  D  G  E  R     ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block>
						<fo:block text-align="left" white-space-collapse="false" keep-together="always">BANK CODE &amp; NAME :  ${bankName} 																BRANCH : ${branchName}</fo:block>
						<#assign unitDetails = delegator.findOne("Facility", {"facilityId" : parameters.unitId}, true)>	
									
						<fo:block text-align="left" white-space-collapse="false" keep-together="always">UNIT CODE &amp; NAME : ${unitDetails.facilityCode?if_exists} ${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((unitDetails.facilityName).toUpperCase())),18)}       IFCNO : ${ifscNo}</fo:block>
						<fo:block >----------------------------------------------------------------------------------</fo:block>
						<fo:block keep-together="always" white-space-collapse="false">CENTER   NAME OF CENTER     PRESIDENT NAME            A/C NO        RND-NET AMOUNT</fo:block>
						<fo:block >----------------------------------------------------------------------------------</fo:block>
					</fo:static-content>
					<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">       				   
				    	<fo:block >
							<fo:table>
						 		<fo:table-column column-width="40pt"/>
								<fo:table-column column-width="160pt"/>
								<fo:table-column column-width="100pt"/>
								<fo:table-column column-width="150pt"/>
								<fo:table-column column-width="110pt"/>						
								<fo:table-body>
									<#assign total =0>									
									<#assign centerIfscList = ifscMap.get(ifscNo)>
									<#list centerIfscList as centerIfscMap >
										<#assign centerIfscMapentrySet = centerIfscMap.entrySet()>
										<#list centerIfscMapentrySet as center>
											<#if (center.getValue().get("amount"))!=0>
												<#assign total = total+(center.getValue().get("amount"))>
												<fo:table-row>
													<fo:table-cell>
														<fo:block text-align="left">${center.getValue().get("centerCode")}</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(((center.getValue().get("centerName"))).toUpperCase())),18)}</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(((center.getValue().get("partyName"))).toUpperCase())),18)}</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="right">${center.getValue().get("accNo")?if_exists}</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="right">${center.getValue().get("amount")?if_exists}</fo:block>
													</fo:table-cell>
												</fo:table-row>
											</#if>											
										</#list>	
									</#list>									
									<fo:table-row>
										<fo:table-cell>
											<fo:block >----------------------------------------------------------------------------------</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell>
											<fo:block>Total </fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block></fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block></fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block></fo:block>
										</fo:table-cell>								
										<fo:table-cell>
											<fo:block text-align="right" >${total}</fo:block>
										</fo:table-cell>							
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell>
											<fo:block >----------------------------------------------------------------------------------</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>	 	
						</fo:block>        				
		 		   </fo:flow>		
				</fo:page-sequence>
				</#list>
			</#list>	
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