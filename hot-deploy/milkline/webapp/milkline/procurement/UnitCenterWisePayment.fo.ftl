<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="12in"  margin-left=".3in" margin-top=".5in">
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
				<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-weight="bold">BANK/CASHIER WISE PAYMENT LITS FOR THE PERIOD  FROM  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block>
				<fo:block >---------------------------------------------------------------------------------------------</fo:block>
				<fo:block keep-together="always" white-space-collapse="false" font-weight="bold">SNO  RG/RT  CNT.CODE &amp; DESC       BMLT      CMLT      TTL      GROSS    RECOV.    NET.AMT</fo:block>
				<fo:block >---------------------------------------------------------------------------------------------</fo:block>
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">       				   
		    	<fo:block >
					<fo:table>
				 		<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="50pt"/>
						<fo:table-column column-width="50pt"/>
						<fo:table-column column-width="80pt"/>
						<fo:table-column column-width="60pt"/>
						<fo:table-column column-width="70pt"/>
						<fo:table-column column-width="70pt"/>
						<fo:table-column column-width="80pt"/>
						<fo:table-column column-width="60pt"/>
						<fo:table-column column-width="90pt"/>						
						<fo:table-body>
							<#assign centerWiseValues =centerMap.entrySet()>
							<#assign totalAmt =0>
							<#assign SNO=0>
							<#list centerWiseValues as centerValues>								
								<#assign totDeductions = centerValues.getValue().get("adjustments").get("totDeductions")>
								<#assign totAdditions = centerValues.getValue().get("adjustments").get("totAdditions")>
								<#assign cartage = centerValues.getValue().get("adjustments").get("cartage")>
								<#assign  netAmount= (((centerValues.getValue().get("price")+centerValues.getValue().get("sPrice"))+totAdditions+cartage)-totDeductions)>
								<#if netAmount !=0>
									<#assign totalAmt =totalAmt+Static["java.lang.Math"].round(netAmount)>
								<#assign SNO=SNO+1>
								<fo:table-row>
									<fo:table-cell>
										<fo:block keep-together="always">${SNO}.</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block keep-together="always">${centerValues.getValue().get("parentFacilityId")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block>${centerValues.getValue().get("facilityCode")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((centerValues.getValue().get("centerName")).toUpperCase())),18)}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="right">${centerValues.getValue().get("BMLtrs")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="right">${centerValues.getValue().get("CMLtrs")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="right">${centerValues.getValue().get("BMLtrs")+centerValues.getValue().get("CMLtrs")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block keep-together="always" text-align="right">${(Static["java.lang.Math"].round(((centerValues.getValue().get("price")+centerValues.getValue().get("sPrice"))+totAdditions+cartage)))?string("##0.00#")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>									
										<fo:block keep-together="always" text-align="right">${(totDeductions)?if_exists?string("##0.00#")}</fo:block>
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
									<fo:block >---------------------------------------------------------------------------------------------</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
									<fo:block font-weight="bold">Total</fo:block>
								</fo:table-cell>
								<fo:table-cell/>
								<fo:table-cell/>
								<fo:table-cell/>
								<fo:table-cell/>
								<fo:table-cell/>
								<fo:table-cell/>
								<fo:table-cell/>
								<fo:table-cell/>
								<fo:table-cell>
									<fo:block text-align="right">${totalAmt?if_exists?string("##0.00")}</fo:block>
								</fo:table-cell>							
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
									<fo:block >---------------------------------------------------------------------------------------------</fo:block>
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