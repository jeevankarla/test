<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-top=".2in">
                <fo:region-body margin-top=".8in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "MonthWiseDetailsList.txt")}
<#if errorMessage?has_content>
<fo:page-sequence master-reference="main">
   <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
      <fo:block font-size="14pt">
              ${errorMessage}.
   	  </fo:block>
   </fo:flow>
</fo:page-sequence>        
<#else>         
     <#if finalMap?has_content>  
		<fo:page-sequence master-reference="main">
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="8pt">
			 <fo:block font-size="10pt">VST_ASCII-015 VST_ASCII-027VST_ASCII-077</fo:block>
			 <#assign facility = delegator.findOne("Facility", {"facilityId" : parameters.shedId}, true)>
			 <fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">STATEMENT OF MONTH-WISE, UNIT-WISE, PROCUREMENT BILLING ANALYSIS</fo:block>
			 <fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">NAME OF THE COMPONENT	: PROCURMENT (LTS)</fo:block> 
			 <fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">ANALYSIS PERIOD FROM	:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block>
			 <fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">NAME OF THE SHED		:${facility.facilityName}  (FIGURES IN LAKHS)</fo:block>
				<fo:block font-size="6pt">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">CODE    NAME OF THE UNIT   <#list monthList as months> ${months} </#list>  CUR.YEAR  PRE.YEAR  DIFRNCE %GE.GROWTH   	</fo:block>
				<fo:block font-size="6pt">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" >
			<#assign unitTotals=finalMap.entrySet()>
			<#assign prevYearGrTotal=0>		
				<fo:block font-family="Courier,monospace" font-size="5pt">
				<fo:table>
					<fo:table-column column-width="20"/>
					<fo:table-column column-width="20"/>
					<fo:table-column column-width="35"/>
					<#list monthList as months>
					<fo:table-column column-width="30"/>
					</#list>
					<fo:table-column column-width="30"/>
					<fo:table-column column-width="30"/>
					<fo:table-column column-width="30"/>
					<fo:table-column column-width="30"/>
					<fo:table-column column-width="30"/>
						<fo:table-body>
						<#assign currentYearTotal=0>
							<#list unitTotals as unitTot>
							<#assign currentUnitTotal=0>
								<#assign facility=delegator.findOne("Facility",{"facilityId": unitTot.getKey()},true)>
								
								<#assign unitValues=unitTot.getValue()>
							<fo:table-row>
								<fo:table-cell>
									<fo:block keep-together="always" text-align="left" font-size="5pt">${facility.facilityCode?if_exists}</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block keep-together="always" text-align="left" font-size="5pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("facilityName")?if_exists)),20)}</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block keep-together="always" text-align="left" font-size="5pt">&#160;</fo:block>
								</fo:table-cell>
								<#assign monthWiseValues = unitTot.getValue()>	
								
								<#list monthList as months>
								<#assign monthValue={}>
								<#assign monthValue = monthWiseValues.get(months)>
								<#assign monthQty = 0>
								<#if monthValue?has_content>
									<#assign monthQty = monthValue>
									<#assign currentUnitTotal = currentUnitTotal+ monthQty>
									<#assign currentYearTotal=currentYearTotal+currentUnitTotal>
								</#if>	
								<fo:table-cell>
									<fo:block font-size="5pt" text-align="right">${(monthQty)?if_exists?string("##0.00")}</fo:block>
								</fo:table-cell> 
								</#list>
								<fo:table-cell>
										<fo:block font-size="5pt" text-align="right">${(currentUnitTotal)?if_exists?string("##0.00")}</fo:block>
								</fo:table-cell>
								<#if prevFinalMap?has_content>
									<#assign prevUnitTotals=prevFinalMap.entrySet()>
									<#assign preValue=0>
									<#assign preValue=prevFinalMap.get(unitTot.getKey())>
									<fo:table-cell>
										<fo:block font-size="5pt" text-align="right">${preValue?if_exists?string("##0.00")}</fo:block>
									</fo:table-cell>
								<#else>
								<#assign preValue=0>
								<fo:table-cell>
									<fo:block font-size="5pt" text-align="right">${preValue?if_exists?string("##0.00")}</fo:block>
								</fo:table-cell>
								</#if>									
									<#assign difrnce=0>
									<#assign difrnce=currentUnitTotal-preValue>
								<fo:table-cell>
									<fo:block font-size="5pt" text-align="right"><#if difrnce!=0>${difrnce?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
								</fo:table-cell>
								
								<fo:table-cell>
									<fo:block font-size="5pt" text-align="right"><#if (preValue!=0)>${((difrnce/preValue)*100)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
								</fo:table-cell>
							</fo:table-row>
									</#list>	
						</fo:table-body>
						
			</fo:table>
			</fo:block>
			<fo:block font-size="6pt">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			 <#assign facility = delegator.findOne("Facility", {"facilityId" : parameters.shedId}, true)>
			<fo:block font-family="Courier,monospace">
				<fo:table>
					<fo:table-column column-width="20"/>
					<fo:table-column column-width="20"/>
					<fo:table-column column-width="35"/>
					<#list monthList as months>
						<fo:table-column column-width="30"/>
					</#list>
					<fo:table-column column-width="30"/>
					<fo:table-column column-width="30"/>
					<fo:table-column column-width="30"/>
					<fo:table-column column-width="30"/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell>
									<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">${facility.facilityName}:</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block keep-together="always" text-align="left" font-size="5pt"></fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block keep-together="always" text-align="left" font-size="5pt"></fo:block>
								</fo:table-cell>
								<#assign currentYearGrTotal=0>
								<#assign grTotals=grandTotalMap>
								<#list monthList as months>
								<#assign monthValue={}>
								<#assign monthValue =grTotals.get(months)>
								<#assign grTotalValue = 0>
								<#if monthValue?has_content>
									<#assign grTotalValue = monthValue>
									<#assign currentYearGrTotal=currentYearGrTotal+grTotalValue>
								</#if>
								<fo:table-cell>
									<fo:block font-size="5pt" text-align="right">${(grTotalValue)?if_exists?string("##0.00")}</fo:block>
								</fo:table-cell>
								</#list>
								<fo:table-cell>
									<fo:block font-size="5pt" text-align="right">${(currentYearGrTotal)?if_exists?string("##0.00")}</fo:block>
								</fo:table-cell>
								<#assign prevYearGrTotal=0>
								<#assign prevYearGrTotal=prevGrFinalMap.get("preGrValue")>
								<fo:table-cell>
									<fo:block font-size="5pt" text-align="right">${prevYearGrTotal?if_exists?string("##0.00")}</fo:block>
								</fo:table-cell>
								<#assign grDifrnce=0>
								<#assign grDifrnce=(currentYearGrTotal-prevYearGrTotal)>
								<fo:table-cell>
									<fo:block font-size="5pt" text-align="right">${grDifrnce?if_exists?string("##0.00")}</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block font-size="5pt" text-align="right"><#if prevYearGrTotal!=0>${((grDifrnce/prevYearGrTotal)*100)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
				</fo:table>
			</fo:block>
			<fo:block font-size="6pt">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>			
		</fo:flow>
	</fo:page-sequence>
	<#else>
		<fo:page-sequence master-reference="main">
    		<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
       		 	<fo:block font-size="14pt">
            		${uiLabelMap.NoOrdersFound}.
       		 	</fo:block>
    		</fo:flow>
		</fo:page-sequence>
	</#if>	
</#if>
</fo:root>
</#escape>