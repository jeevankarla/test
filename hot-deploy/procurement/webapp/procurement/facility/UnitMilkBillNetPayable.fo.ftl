<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-left=".5in" margin-top=".2in">
                <fo:region-body margin-top="1.7in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "UnitMilkBillNetPayable.txt")} 
	    <#if unitTotalsMap?exists>    
	    	<#assign unitTotalEntries = unitTotalsMap.entrySet()>
	    	<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : parameters.shedId}, true)>
			<fo:page-sequence master-reference="main">
				<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
					<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
					<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="11pt">&#160;                                          ${reportHeader.description?if_exists}</fo:block>
					<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="11pt">&#160;                                              STATEMENT SHOWING THE MILK BILL PAYMENT OF UNIT-WISE ABSTRACT</fo:block>
					<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="11pt">NAME OF THE SHED   :   ${(facilityDetails.get("description").toUpperCase())?if_exists}           PERIOD FROM :  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")}     TO    ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}   PAGE NO : <fo:page-number/> </fo:block>
					<fo:block >------------------------|-----------|----------------------------------------------------------------------|------------|-------------|</fo:block>
					<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="11pt">CODE  NAME OF THE UNIT |           |                   DETAILS      OF     RECOVERIES                     |           |               |</fo:block>
					<fo:block white-space-collapse="false" keep-together="always" font-size="11pt">&#160;                      | TOTAL     |----------------------------|----------------------------|------------|  TOTAL    |  NET          |</fo:block>
					<fo:block white-space-collapse="false" keep-together="always" font-size="11pt">&#160;                      | MILK BILL |  SHORTAGE             SHORT|  SHORTAGE             SHORT| SOUR MILK/ |  AMOUNT   |  AMOUNT       |</fo:block>
					<fo:block white-space-collapse="false" keep-together="always" font-size="11pt">&#160;                      | AMOUNT    |   KG-FAT         KG-FAT AMT|   KG-SNF         KG-SNF AMT| OTH.AMOUNT |  RECOVERY |  PAYABLE      |</fo:block>
					<fo:block >------------------------|-----------|----------------------------|----------------------------|------------|------------|-------------|</fo:block>
				</fo:static-content>
				<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace"> 
			<#assign TotMilkValue =0>
			<#assign TotShortKgFat =0>
			<#assign TotShortKgFatAmt =0>
			<#assign TotShortKgSnf =0>
			<#assign TotShortKgSnfAmt =0>
			<#assign TotSourAmt =0>	  
			<#assign grTotRecoveryAmt =0>
			<#assign TotNetValue =0>
			<#list unitTotalEntries as unitTotalEntry> 
				<#assign unitTotalValues = unitTotalEntry.getValue().get("periodTransferTotalsMap")>
				<#assign unitTotals= unitTotalValues.entrySet()>
				<#assign unitsourAmt=0>
			<#list unitTotals as unitTotalEntryValues>
				<#assign transferValues =unitTotalEntryValues.getValue().get("transfers")>			
				<#assign periodTotals = transferValues.get("procurementPeriodTotals").get("dayTotals")>
				<#assign facility = delegator.findOne("Facility", {"facilityId" : unitTotalEntryValues.getKey()}, true)>				
				<#assign milkValue =0>
				<#assign adjustmentValues =unitWiseAdjustmentMap[unitTotalEntryValues.getKey()]>
				<#if sourDistributionMap[unitTotalEntryValues.getKey()]?has_content>
					<#assign unitsourAmt=sourDistributionMap[unitTotalEntryValues.getKey()]>
				</#if>	
				<#assign opCost =0>
				<#assign cartage=0>
				<#if shedWiseAmountAbstractMap?has_content>
					<#assign opCost = shedWiseAmountAbstractMap.get(unitTotalEntryValues.getKey()).get("opCost")>
					<#assign cartage= shedWiseAmountAbstractMap.get(unitTotalEntryValues.getKey()).get("cartage")>
				</#if>
				<#if periodTotals?has_content>	
					<#assign additions = adjustmentValues.get("totAdditions")>
					<#assign deductions = adjustmentValues.get("totDeductions")>				
					<#assign milkValue = Static["java.lang.Math"].round((((periodTotals.get("TOT").get("price")+periodTotals.get("TOT").get("sPrice"))+additions+opCost+cartage)-deductions))>
				</#if>				
				<#assign shortages =transferValues.get("shortages")>	
				
				<#assign totalRecoveryAmt = ((shortages.get("kgFatAmt"))+(unitsourAmt)+(shortages.get("kgSnfAmt")))>				
				<#assign TotMilkValue = TotMilkValue+milkValue>
				
				<#if (shortages.get("kgFat")<0)>
					<#assign TotShortKgFat =TotShortKgFat+shortages.get("kgFat")>
				</#if>	
				<#assign TotShortKgFatAmt =TotShortKgFatAmt+shortages.get("kgFatAmt")>	
			<#if (shortages.get("kgSnf")<0)>	
				<#assign TotShortKgSnf =TotShortKgSnf+shortages.get("kgSnf")>				
			</#if>	
				<#assign TotShortKgSnfAmt =TotShortKgSnfAmt+shortages.get("kgSnfAmt")>
				<#assign TotSourAmt =TotSourAmt+(unitsourAmt)>	  
				<#assign grTotRecoveryAmt =grTotRecoveryAmt+totalRecoveryAmt>				
				<#assign TotNetValue =TotNetValue+(milkValue+totalRecoveryAmt)>
					<fo:block>
						<fo:table>
							<fo:table-column column-width="27pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="190pt"/>
							<fo:table-column column-width="90pt"/>
							<fo:table-column column-width="110pt"/>
							<fo:table-column column-width="90pt"/>
							<fo:table-column column-width="100pt"/>
							<fo:table-column column-width="100pt"/>
							<fo:table-column column-width="110pt"/>
							<fo:table-column column-width="110pt"/>
							<fo:table-column column-width="60pt"/>
							<fo:table-body>
							<#if (milkValue+totalRecoveryAmt) !=0>
								<fo:table-row>
									<fo:table-cell>
										<fo:block>${facility.facilityCode?if_exists}</fo:block>
									</fo:table-cell>
									<fo:table-cell><fo:block keep-together="always" text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("facilityName")?if_exists)),18)}</fo:block></fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">${milkValue?if_exists?string("##0.00")}</fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right"><#if (shortages.get("kgFat")<0)>${shortages.get("kgFat")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">${shortages.get("kgFatAmt")?if_exists?string("##0.00")}</fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right"><#if (shortages.get("kgSnf")<0)>${shortages.get("kgSnf")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">${shortages.get("kgSnfAmt")?if_exists?string("##0.00")}</fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">${unitsourAmt?string("##0.00")}</fo:block>										
									</fo:table-cell>									
									<fo:table-cell>										
										<fo:block text-align="right">${totalRecoveryAmt?if_exists?string("##0.00")}</fo:block>										
									</fo:table-cell>
									<fo:table-cell>								
										<fo:block text-align="right">${(milkValue+totalRecoveryAmt)?if_exists?string("##0.00")}</fo:block>										
									</fo:table-cell>
								</fo:table-row>
							</#if>	
							</fo:table-body>
						</fo:table>
					</fo:block>
				</#list>	
				</#list>
				<#assign ddAmount =0>
				<#if ddAmountMap?has_content>
					<#assign ddAmount =ddAmountMap.get("amount")>
					<fo:block>
						<fo:table>
							<fo:table-column column-width="27pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="190pt"/>
							<fo:table-column column-width="90pt"/>
							<fo:table-column column-width="110pt"/>
							<fo:table-column column-width="90pt"/>
							<fo:table-column column-width="100pt"/>
							<fo:table-column column-width="100pt"/>
							<fo:table-column column-width="110pt"/>
							<fo:table-column column-width="110pt"/>
							<fo:table-column column-width="60pt"/>
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell>
										<fo:block>${ddAmountMap.get("unitCode")?if_exists}</fo:block>
									</fo:table-cell>
									<fo:table-cell><fo:block keep-together="always" text-align="left">${ddAmountMap.get("nameOfUnit")}</fo:block></fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">${Static["java.lang.Math"].round(ddAmount)?string("##0.00")}</fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">0.00</fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">0.00</fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">0.00</fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">0.00</fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">0.00</fo:block>										
									</fo:table-cell>									
									<fo:table-cell>										
										<fo:block text-align="right">0.00</fo:block>										
									</fo:table-cell>
									<fo:table-cell>								
										<fo:block text-align="right">${Static["java.lang.Math"].round(ddAmount)?string("##0.00")}</fo:block>										
									</fo:table-cell>
								</fo:table-row>
							
							</fo:table-body>
						</fo:table>
					</fo:block>
				</#if>	
					<fo:block >---------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		 		   	<fo:block>
						<fo:table>
							<fo:table-column column-width="27pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="190pt"/>
							<fo:table-column column-width="90pt"/>
							<fo:table-column column-width="110pt"/>
							<fo:table-column column-width="90pt"/>
							<fo:table-column column-width="100pt"/>
							<fo:table-column column-width="100pt"/>
							<fo:table-column column-width="110pt"/>
							<fo:table-column column-width="110pt"/>
							<fo:table-column column-width="60pt"/>
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell>										
									</fo:table-cell>
									<fo:table-cell><fo:block keep-together="always">${(facilityDetails.get("description").toUpperCase())?if_exists}</fo:block></fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">${Static["java.lang.Math"].round(TotMilkValue+ddAmountMap.get("amount"))?string("##0.00")}</fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">${TotShortKgFat?if_exists?string("##0.00")}</fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">${TotShortKgFatAmt?if_exists?string("##0.00")}</fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">${TotShortKgSnf?if_exists?string("##0.00")}</fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">${TotShortKgSnfAmt?if_exists?string("##0.00")}</fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">${TotSourAmt?if_exists?string("##0.00")}</fo:block>										
									</fo:table-cell>									
									<fo:table-cell>										
										<fo:block text-align="right">${grTotRecoveryAmt?if_exists?string("##0.00")}</fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">${Static["java.lang.Math"].round(TotNetValue+ddAmountMap.get("amount"))?string("##0.00")}</fo:block>										
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:block>
					<fo:block >---------------------------------------------------------------------------------------------------------------------------------------</fo:block>
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