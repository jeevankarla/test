<#escape x as x?xml>
	<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
		<fo:layout-master-set>
			<fo:simple-page-master master-name="main" page-height="12in" page-width="15in" >
			 <fo:region-body margin-top="1.1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
			</fo:simple-page-master>
		</fo:layout-master-set>
		${setRequestAttribute("OUTPUT_FILENAME","LeanFlushYearWise.txt")}
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
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
			 <fo:block font-size="10pt">VST_ASCII-015 VST_ASCII-027VST_ASCII-077</fo:block>
			 <#assign facility = delegator.findOne("Facility", {"facilityId" : parameters.shedId}, true)>
			<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">&#160;                                                                 STATEMENT SHOWING OF YEAR-WISE, MONTH-WISE, PROCUREMENT BILLING ANALYSYS</fo:block>
			<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">&#160; </fo:block>
			<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">&#160;                                                                            NAME OF THE COMPONENT		 :	PROCUREMENT (LTS)</fo:block>
			<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">&#160;                                                                            ANALYSYS PERIOD FROM			 :	01-04-${fromDate} TO 31-03-${thruDate}	</fo:block>
			<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">&#160;                                                                            NAME OF THE SHED			     :	${(facility.facilityName)?upper_case}                       (FIGURES IN LAKHS)</fo:block>
			<fo:block font-size="5pt">----------------|-------------------------------------------------------------------|----------|--------------------------------------------------------------|--------------|-------------|</fo:block>
			<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">&#160;               |                         LEAN SEASON PERIOD                        |  LEAN    |                        FLUSH SEASON PERIOD                   |    FLUSH     |     YEAR    |</fo:block>
			<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">&#160;               |-------------------------------------------------------------------|          |--------------------------------------------------------------|              |             |</fo:block>
			<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">&#160;  YEAR         |     APR       MAY       JUN       JUL       AUG       SEP         |  TOTAL   |   OCT       NOV       DEC       JAN       FEB        MAR     |    TOTAL     |     TOTAL   |</fo:block>
			<fo:block font-size="5pt">----------------|-------------------------------------------------------------------|----------|--------------------------------------------------------------|--------------|-------------|</fo:block>
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
			<#assign finalMapValues=finalMap.entrySet()>
				<fo:block font-size="5pt" font-family="Courier,monospace">
					<fo:table>
						<fo:table-column column-width="50pt"/>
						<#list leanMonthList as months>
						<fo:table-column column-width="30pt"/>
						</#list>
						<fo:table-column column-width="48pt"/>
						<#list flushMonthList as months>
						<fo:table-column column-width="31pt"/>
						</#list>
						<fo:table-column column-width="40pt"/>
						<fo:table-column column-width="50pt"/>
						<fo:table-body>
						<#list finalMapValues as mapValues>
							<fo:table-row>
							<#assign year=0>
							<#assign year=mapValues.getKey()>
								<fo:table-cell>
									<fo:block text-align="left"  font-size="5pt">${year}-${year+1}</fo:block>
								</fo:table-cell>
								<#assign leanTotal=0>
								<#assign mapTotals =mapValues.getValue()>
								<#list leanMonthList as months>
								<#assign monthValue={}>
								<#assign monthValue=mapTotals.get(months)>
								<#assign monthQty=0>
								<#if monthValue?has_content>
								<#assign monthQty=monthValue>
								<#assign leanTotal=leanTotal+monthQty>
								</#if>
								<fo:table-cell>
									<fo:block text-align="right"  font-size="5pt">${monthQty?if_exists?string("##0.00")}</fo:block>
								</fo:table-cell>
								</#list>
								<fo:table-cell>
									<fo:block text-align="right"  font-size="5pt">${leanTotal?if_exists?string("##0.00")}</fo:block>
								</fo:table-cell>
								<#assign flushTotal=0>
								<#list flushMonthList as months>
								<#assign monthValue={}>
								<#assign monthValue=mapTotals.get(months)>
								<#assign monthQty=0>
								<#if monthValue?has_content>
								<#assign monthQty=monthValue>
								<#assign flushTotal=flushTotal+monthQty>
								</#if>
								<fo:table-cell>
									<fo:block text-align="right"  font-size="5pt">${monthQty?if_exists?string("##0.00")}</fo:block>
								</fo:table-cell>
								</#list>
								<fo:table-cell>
									<fo:block text-align="right"  font-size="5pt">${flushTotal?if_exists?string("##0.00")}</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="right"  font-size="5pt">${(flushTotal+leanTotal)?if_exists?string("##0.00")}</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
	                    		<fo:table-cell><fo:block  font-size="5pt" linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
	                    	</fo:table-row>
							</#list>
						</fo:table-body>
					</fo:table>		
				</fo:block>
				<fo:block font-size="5pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				<fo:block font-size="5pt">VST_ASCII-012 VST_ASCII-027VST_ASCII-080</fo:block>
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