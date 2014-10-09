<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".5in" margin-top=".5in" margin-bottom=".5in">
                <fo:region-body margin-top=".9in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "shedWiseUserCharges.txt")}
		<#if ShedWiseUserChargesMap?has_content>
		<fo:page-sequence master-reference="main">
				<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" >
					<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
					<fo:block text-align="left" white-space-collapse="false" keep-together="always">VST_ASCII-018&#160;          ${reportHeader.description?if_exists}</fo:block>
					<fo:block text-align="left" white-space-collapse="false" keep-together="always">SHED WISE USER CHARGES    FROM: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "MMM d, yyyy")}  TO  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "MMM d, yyyy")}</fo:block>
					<fo:block font-size="8pt">------------------------------------------------------------------------------------</fo:block>
					<fo:block keep-together="always" white-space-collapse="false" font-size="8pt">NAME OF THE SHED          LTRS    USER CHARGES   SERVICE TAX   TOT.USER CHARGES</fo:block>
					<fo:block font-size="8pt">------------------------------------------------------------------------------------</fo:block>
				</fo:static-content>
				<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
					<fo:block font-size="8pt">
							<fo:table>
								<fo:table-column column-width="50pt"/>
								<fo:table-column column-width="100pt"/>
								<fo:table-column column-width="60pt"/>
								<fo:table-column column-width="65pt"/>
								<fo:table-column column-width="80pt"/>
								<fo:table-column column-width="65pt"/>
								<fo:table-column column-width="60pt"/>
								<fo:table-body>
									<#assign totLtrs= 0>
									<#assign totAmt= 0>
									<#assign totServiceTax= 0>
									<#assign totUserCharges= 0>
									<#assign shedWiseUserChargesList = ShedWiseUserChargesMap.entrySet()>
									<#list shedWiseUserChargesList as shedUserCharge>
										 <fo:table-row>
											<#assign facility = delegator.findOne("Facility", {"facilityId" : shedUserCharge.getKey()}, true)>
											<fo:table-cell>
												<fo:block text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("facilityName")?if_exists)),15)}</fo:block>
											</fo:table-cell>
											<#assign totLtrs= totLtrs+shedUserCharge.getValue().get("shedLtrs")>
											<#assign totAmt= totAmt+shedUserCharge.getValue().get("amount")>
											<#assign totServiceTax= totServiceTax+shedUserCharge.getValue().get("serviceTax")>
											<#assign totUserCharges= totUserCharges+shedUserCharge.getValue().get("userCharges")>
											<fo:table-cell>
												<fo:block text-align="right">${shedUserCharge.getValue().get("shedLtrs")?if_exists?string("##0.0")}</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block text-align="right">${shedUserCharge.getValue().get("amount")?if_exists?string("##0.00")}</fo:block>
											</fo:table-cell>											
											<fo:table-cell>
												<fo:block text-align="right">${shedUserCharge.getValue().get("serviceTax")?if_exists?string("##0.00")}</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block text-align="right">${shedUserCharge.getValue().get("userCharges")?if_exists?string("##0.00")}</fo:block>
											</fo:table-cell>
										</fo:table-row>	
										<fo:table-row>
											<fo:table-cell>
												<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</#list>	
									<fo:table-row>
										<fo:table-cell>
											<fo:block font-size="8pt">------------------------------------------------------------------------------------</fo:block>
										</fo:table-cell>
									</fo:table-row>	
									<fo:table-row>
										<fo:table-cell>
												<fo:block text-align="left" keep-together="always">TOTAL</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block text-align="right">${totLtrs?if_exists?string("##0.0")}</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block text-align="right">${totAmt?if_exists?string("##0.00")}</fo:block>
											</fo:table-cell>											
											<fo:table-cell>
												<fo:block text-align="right">${totServiceTax?if_exists?string("##0.00")}</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block text-align="right">${totUserCharges?if_exists?string("##0.00")}</fo:block>
											</fo:table-cell>
									</fo:table-row>	
									<fo:table-row>
										<fo:table-cell>
											<fo:block font-size="8pt">------------------------------------------------------------------------------------</fo:block>
										</fo:table-cell>
									</fo:table-row>			
								</fo:table-body>
							</fo:table>
						</fo:block>
							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
        <fo:block text-align="left" keep-together="always" font-size="10pt" white-space-collapse="false">&#160;                         C E R T I F I C A T E</fo:block>
        <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
        <fo:block text-align="left" keep-together="always" font-size="10pt" white-space-collapse="false">1.	Certified that the expenditure incurred for official purpose only.</fo:block>
        <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
        <fo:block text-align="left" keep-together="always" font-size="10pt" white-space-collapse="false">2.	Certified that the bill is not claimed earlier.</fo:block>
       	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
        <fo:block text-align="left" keep-together="always" font-size="10pt" white-space-collapse="false">3.	Certified that the system, database and application administration</fo:block>
        <fo:block text-align="left" keep-together="always" font-size="10pt" white-space-collapse="false">&#160; was attended during from ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "MMM d, yyyy")} to ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "MMM d, yyyy")} the satisfaction</fo:block> 
        <fo:block text-align="left" keep-together="always" font-size="10pt" white-space-collapse="false">&#160; by the M/s. Vasista Enterprise Solutions Pvt.Ltd, Hyderabad.</fo:block>
        <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
        <fo:block text-align="left" keep-together="always" font-size="10pt" white-space-collapse="false">4.	The claim by M/s. Vasista Enterprise Solutions Pvt.Ltd,</fo:block> 
        <fo:block text-align="left" keep-together="always" font-size="10pt" white-space-collapse="false">&#160; Hyderabad is as per Procurement performed during ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "MMM d, yyyy")} to ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "MMM d, yyyy")}.</fo:block>
     	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
     	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
     	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
     	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
     	<fo:block text-align="left" keep-together="always" font-size="10pt" white-space-collapse="false">Deputy Director(MIS)                        General Manager(P&amp;I)</fo:block>
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
	</fo:root>
</#escape>
				