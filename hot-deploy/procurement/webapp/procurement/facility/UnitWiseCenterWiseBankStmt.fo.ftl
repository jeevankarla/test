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
    <#if centerMap?has_content>
		<fo:page-sequence master-reference="main">
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
				<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;           ${reportHeader.description?if_exists}</fo:block>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always">B  A  N  K  L  E  D  G  E  R     ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always">BANK CODE &amp; NAME :  <#if unitAccountMap?has_content>${unitAccountMap.get("finAccountName")?if_exists}</#if>              BRANCH : <#if unitAccountMap?has_content>${unitAccountMap.get("finAccountBranch")?if_exists}</#if></fo:block>
				<#assign unitDetails = delegator.findOne("Facility", {"facilityId" : parameters.unitId}, true)>	
							
				<fo:block text-align="left" white-space-collapse="false" keep-together="always">UNIT CODE &amp; NAME : ${unitDetails.facilityCode?if_exists} ${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((unitDetails.facilityName).toUpperCase())),18)}       IFCNO : ${unitAccountMap.get("ifscCode")?if_exists}</fo:block>
				<fo:block >----------------------------------------------------------------------------------</fo:block>
				<fo:block keep-together="always" white-space-collapse="false">CENTER   NAME OF CENTER     PRESIDENT NAME         A/C NO        RND-NET AMOUNT</fo:block>
				<fo:block >----------------------------------------------------------------------------------</fo:block>
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">       				   
		    	<fo:block >
					<fo:table>
				 		<fo:table-column column-width="80pt"/>
						<fo:table-column column-width="120pt"/>
						<fo:table-column column-width="90pt"/>
						<fo:table-column column-width="150pt"/>
						<fo:table-column column-width="110pt"/>						
						<fo:table-body>
							<#assign centerWiseValues =centerMap.entrySet()>
							<#assign totalAmt =0>
							<#list centerWiseValues as centerValues>								
								<#assign totDeductions = centerValues.getValue().get("adjustments").get("totDeductions")>
								<#assign totAdditions = centerValues.getValue().get("adjustments").get("totAdditions")>
								<#assign cartage = centerValues.getValue().get("adjustments").get("cartage")>
								<#assign  netAmount= (((centerValues.getValue().get("price")+centerValues.getValue().get("sPrice"))+totAdditions+cartage)-totDeductions)>
								<#if netAmount !=0>
								<#assign totalAmt =totalAmt+Static["java.lang.Math"].round(netAmount)>
								<fo:table-row>
									<fo:table-cell>
										<fo:block>${centerValues.getValue().get("facilityCode")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((centerValues.getValue().get("centerName")).toUpperCase())),18)}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, centerValues.getValue().get("ownerPartyId"), false)>
										<fo:block keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((partyName).toUpperCase())),18)}</fo:block>
									</fo:table-cell>
									<fo:table-cell>									
										<fo:block keep-together="always" text-align="right">${(centerValues.getValue().get("accountNum"))?if_exists}</fo:block>
									</fo:table-cell>																
									<fo:table-cell>									
										<fo:block  text-align="right" keep-together="always">${(Static["java.lang.Math"].round(netAmount))?string("##0.00#")}</fo:block>
										<fo:block  linefeed-treatment="preserve">&#xA;</fo:block>
									</fo:table-cell>
								</fo:table-row>
								</#if>
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
									<fo:block><#if unitAccountMap?has_content>${unitAccountMap.get("finAccountName")?if_exists}</#if></fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block></fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block></fo:block>
								</fo:table-cell>								
								<fo:table-cell>
									<fo:block text-align="left" text-indent="55pt">${totalAmt?if_exists?string("##0.00")}</fo:block>
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
	<#else>
		<fo:page-sequence master-reference="main">
			<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
		 		<fo:block font-size="14pt">
					${uiLabelMap.NoOrdersFound}.
		 		</fo:block>
			</fo:flow>
		</fo:page-sequence>
	</#if>	
</fo:root>
</#escape>