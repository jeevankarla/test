<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-top=".5in">
                <fo:region-body margin-top="1.2in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "unitCenterwiseBankStmt.txt")}
    <#if centerMap?has_content>
		<fo:page-sequence master-reference="main">
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
				<#assign unitDetails = delegator.findOne("Facility", {"facilityId" : parameters.unitId}, true)>	
				<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-weight="bold">&#160;           ${reportHeader.description?if_exists}-${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((unitDetails.facilityName).toUpperCase())),18)}</fo:block>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-weight="bold">BONUS STATEMENT FOR THE PERIOD FROM  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block>
				<fo:block >---------------------------------------------------------------------------------------</fo:block>
				<fo:block keep-together="always" white-space-collapse="false" font-weight="bold">P.CODE    BMLT    BM.AMT      CMLT    CM.AMT    RETN.AMT  TTL.BONUS  NAME</fo:block>
				<fo:block >---------------------------------------------------------------------------------------</fo:block>
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">       				   
		    	<fo:block >
					<fo:table>
				 		<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="70pt"/>
						<fo:table-column column-width="70pt"/>
						<fo:table-column column-width="80pt"/>
						<fo:table-column column-width="60pt"/>
						<fo:table-column column-width="70pt"/>
						<fo:table-column column-width="90pt"/>
						<fo:table-column column-width="90pt"/>
						<fo:table-column column-width="90pt"/>
						<fo:table-column column-width="90pt"/>						
						<fo:table-body>
							<#assign centerWiseValues =centerMap.entrySet()>
							<#assign totBmLtrs =0>
							<#assign totBmAmt =0>
							<#assign totCmLtrs =0>
							<#assign totCmAmt =0>
							<#assign totRetnAmt =0>
							<#assign totBonus =0>
							
							<#list centerWiseValues as centerValues>	
									<#assign totBmLtrs =totBmLtrs+centerValues.getValue().get("BMLtrs")>
									<#assign totBmAmt =totBmAmt+centerValues.getValue().get("BMAmt")>
									<#assign totCmLtrs =totCmLtrs+centerValues.getValue().get("CMLtrs")>
									<#assign totCmAmt =totCmAmt+centerValues.getValue().get("CMAmt")>
									<#assign totRetnAmt =totRetnAmt+(centerValues.getValue().get("RetnAmt"))>
									<#assign totBonus =totBonus+((centerValues.getValue().get("RetnAmt"))+centerValues.getValue().get("BMAmt")+centerValues.getValue().get("CMAmt"))>
									<#assign bonus =((centerValues.getValue().get("RetnAmt"))+centerValues.getValue().get("BMAmt")+centerValues.getValue().get("CMAmt"))>
							<#if bonus !=0>	
								<fo:table-row>
									<fo:table-cell>
										<fo:block>${centerValues.getValue().get("facilityCode")}</fo:block>
									</fo:table-cell>									
									<fo:table-cell>
										<fo:block text-align="right">${centerValues.getValue().get("BMLtrs")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="right">${centerValues.getValue().get("BMAmt")?string("##0.000")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="right">${centerValues.getValue().get("CMLtrs")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="right">${centerValues.getValue().get("CMAmt")?string("##0.000")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="right">${centerValues.getValue().get("RetnAmt")?string("##0.00")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="right">${(centerValues.getValue().get("RetnAmt")+centerValues.getValue().get("BMAmt")+centerValues.getValue().get("CMAmt"))?string("##0.000")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, centerValues.getValue().get("ownerPartyId"), false)>
										<fo:block keep-together="always" text-indent="25pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((centerValues.getValue().get("centerName")).toUpperCase())),18)}-${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((partyName).toUpperCase())),18)}</fo:block>
										<fo:block  linefeed-treatment="preserve">&#xA;</fo:block>
									</fo:table-cell>		
								</fo:table-row>
							</#if>
							</#list>
							<fo:table-row>
								<fo:table-cell>
									<fo:block >---------------------------------------------------------------------------------------</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
									<fo:table-cell>
										<fo:block font-weight="bold">Total</fo:block>
									</fo:table-cell>									
									<fo:table-cell>
										<fo:block text-align="right">${totBmLtrs}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="right">${totBmAmt}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="right">${totCmLtrs}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="right">${totCmAmt}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="right">${totRetnAmt}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="right">${totBonus}</fo:block>
									</fo:table-cell>											
								</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
									<fo:block >---------------------------------------------------------------------------------------</fo:block>
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