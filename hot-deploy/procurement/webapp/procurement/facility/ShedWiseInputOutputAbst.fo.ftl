<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".5in" margin-top=".5in">
                <fo:region-body margin-top="1.5in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "ShedIOAbst.txt")}
<#if errorMessage?has_content>
<fo:page-sequence master-reference="main">
   <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
      <fo:block font-size="14pt">
              ${errorMessage}.
   	  </fo:block>
   </fo:flow>
</fo:page-sequence>        
<#else>        
	<#if unitTotalsMap?has_content>
		<#assign unitTotalEntries = unitTotalsMap.entrySet()>		
		<fo:page-sequence master-reference="main">
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" >
				<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;             ${reportHeader.description?if_exists}</fo:block>
				<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : parameters.shedId}, true)>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;                     MILK SHED NAME: ${facilityDetails.facilityName}</fo:block>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;                     PERIOD FROM   : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block>
				<fo:block font-size="8pt">---------------------------------------------------------------------------------------------</fo:block>
				<fo:block white-space-collapse="false" keep-together="always">&#160;         STATEMENT SHOWING THE INPUT/OUTPUT KG-FAT AND KG-SNF ACCOUNT</fo:block>
				<fo:block font-size="8pt">---------------------------------------------------------------------------------------------</fo:block>
				<fo:block keep-together="always" white-space-collapse="false" font-size="8pt">UNIT     NAME OF THE           QUANTITY     QUANTITY    TOTAL      TOTAL      AVG    AVG</fo:block>
				<fo:block keep-together="always" white-space-collapse="false" font-size="8pt">CODE     NCC/DAIRY              (LTRS)       (KGS)      KG-FAT     KG-SNF     FAT    SNF</fo:block>
				<fo:block font-size="8pt">---------------------------------------------------------------------------------------------</fo:block>
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
			
			<#assign totInputLtr = 0>
			<#assign totInputQty = 0>
			<#assign totInputKgFat = 0>
			<#assign totInputKgSnf = 0>
			<#if shedWiseTotMap?has_content>
				<#assign obQty = shedWiseTotMap.get("OpeningBalace").get("qty")>
				<#assign obQtyLtr = shedWiseTotMap.get("OpeningBalace").get("quantityLtrs")>
				<#assign obKgFat = shedWiseTotMap.get("OpeningBalace").get("kgFat")>
				<#assign obKgSnf = shedWiseTotMap.get("OpeningBalace").get("kgSnf")>
				
				
				<#assign totInputLtr = totInputLtr+obQtyLtr>
				<#assign totInputQty = totInputQty+obQty>
				<#assign totInputKgFat = totInputKgFat+obKgFat>
				<#assign totInputKgSnf = totInputKgSnf+obKgSnf>
				
				
				<fo:block font-family="Courier,monospace">
					<fo:table text-align="left" font-size="9pt">
						<fo:table-column column-width="45pt"/>
						<fo:table-column column-width="40pt"/>
						<fo:table-column column-width="120pt"/>
						<fo:table-column column-width="64pt"/>
						<fo:table-column column-width="63pt"/>
						<fo:table-column column-width="65pt"/>
						<fo:table-column column-width="45pt"/>
						<fo:table-column column-width="45pt"/>
						<fo:table-body>
							<fo:table-row>								
								<fo:table-cell><fo:block>&#160;</fo:block></fo:table-cell>
								<fo:table-cell><fo:block keep-together="always" text-align="left">Opening Balance</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${obQtyLtr?string("##0.0")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${obQty?string("##0.0")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${obKgFat?string("##0.000")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${obKgSnf?string("##0.000")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right"><#if obQty !=0>${((obKgFat*100)/obQty)?string("##0.0")}<#else>0.00</#if></fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right"><#if obQty !=0>${((obKgSnf*100)/obQty)?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>
							</fo:table-row>						
							<#if inputEntriesMap?has_content>
								<#assign inputEntries =inputEntriesMap.entrySet()>
									<#list inputEntries as inputEntry>
										<#assign key = inputEntry.getKey()>
											<#assign value= inputEntry.getValue()>
											<#if (value.get("qtyKgs"))!=0>
												<#assign totInputLtr = totInputLtr+value.get("qtyLtrs")>
												<#assign totInputQty = totInputQty+value.get("qtyKgs")>
												<#assign totInputKgFat = totInputKgFat+value.get("kgFat")>
												<#assign totInputKgSnf = totInputKgSnf+value.get("kgSnf")>
												<fo:table-row>								
													<fo:table-cell><fo:block>&#160;</fo:block></fo:table-cell>
													<fo:table-cell><fo:block keep-together="always" text-align="left">${key.replace("_"," ")}</fo:block></fo:table-cell>
													<fo:table-cell><fo:block text-align="right">${value.get("qtyLtrs")?string("##0.0")}</fo:block></fo:table-cell>
													<fo:table-cell><fo:block text-align="right">${value.get("qtyKgs")?string("##0.0")}</fo:block></fo:table-cell>
													<fo:table-cell><fo:block text-align="right">${value.get("kgFat")?string("##0.000")}</fo:block></fo:table-cell>
													<fo:table-cell><fo:block text-align="right">${value.get("kgSnf")?string("##0.000")}</fo:block></fo:table-cell>
													<fo:table-cell><fo:block text-align="right">${value.get("fat")?string("##0.0")}</fo:block></fo:table-cell>
													<fo:table-cell><fo:block text-align="right">${value.get("snf")?string("##0.00")}</fo:block></fo:table-cell>
												</fo:table-row>
											</#if>
									</#list>
									
							</#if>
							
						</fo:table-body>
					</fo:table>
				</fo:block>
			</#if>	
			<#assign iutQtyKgs =0>
			<#assign iutQtyLtrs =0>
			<#assign iutKgFat =0>
			<#assign iutKgSnf =0>	
			
			<#assign ikpTotKgs=0>		
			<#assign ikpTotLtrs=0>
			<#assign ikpTotKgFat=0>
			<#assign ikpTotKgSnf=0>	
			
			<#assign nonIkpTotKgs=0>		
			<#assign nonIkpTotLtrs=0>
			<#assign nonIkpTotKgFat=0>
			<#assign nonIkpTotKgSnf=0>	
			
			<#if  seperateIKPUnitsFlag="Y">
				<#list unitTotalEntries as unitTotalEntry> 
					<#assign unitTotalValues = unitTotalEntry.getValue().get("periodTransferTotalsMap")> 
					<#assign unitTotals= unitTotalValues.entrySet()>
					<#list unitTotals as unitTotalEntryValues>
						<#assign transferValues =unitTotalEntryValues.getValue().get("transfers")>			
						<#assign periodTotals = transferValues.get("procurementPeriodTotals").get("dayTotals")>					
						<#if periodTotals?has_content>							
							<fo:block font-family="Courier,monospace">
								<fo:table text-align="left" font-size="9pt">
									<fo:table-column column-width="45pt"/>
									<fo:table-column column-width="40pt"/>
									<fo:table-column column-width="120pt"/>
									<fo:table-column column-width="64pt"/>
									<fo:table-column column-width="63pt"/>
									<fo:table-column column-width="65pt"/>
									<fo:table-column column-width="45pt"/>
									<fo:table-column column-width="45pt"/>
									<fo:table-body>	
										<#assign facility = delegator.findOne("Facility", {"facilityId" : unitTotalEntryValues.getKey()}, true)>
														
										<#if facility.managedBy=="APDDCF">	
											<#assign nonIkpTotKgs=nonIkpTotKgs+(periodTotals.get("TOT").get("qtyKgs")+periodTotals.get("TOT").get("sQtyKgs"))>		
											<#assign nonIkpTotLtrs=nonIkpTotLtrs+(periodTotals.get("TOT").get("qtyLtrs")+periodTotals.get("TOT").get("sQtyLtrs"))>
											<#assign nonIkpTotKgFat=nonIkpTotKgFat+(periodTotals.get("TOT").get("kgFat"))>
											<#assign nonIkpTotKgSnf=nonIkpTotKgSnf+(periodTotals.get("TOT").get("kgSnf"))>				
											<fo:table-row>								
												<fo:table-cell><fo:block>${facility.facilityCode?if_exists}</fo:block></fo:table-cell>
												<fo:table-cell><fo:block keep-together="always" text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("facilityName")?if_exists)),19)}</fo:block></fo:table-cell>
												<fo:table-cell><fo:block text-align="right">${(periodTotals.get("TOT").get("qtyLtrs")+periodTotals.get("TOT").get("sQtyLtrs"))?string("##0.0")}</fo:block></fo:table-cell>
												<fo:table-cell><fo:block text-align="right">${(periodTotals.get("TOT").get("qtyKgs")+(periodTotals.get("TOT").get("sQtyLtrs") *1.03))?string("##0.0")}</fo:block></fo:table-cell>
												<fo:table-cell><fo:block text-align="right">${(periodTotals.get("TOT").get("kgFat"))?string("##0.000")}</fo:block></fo:table-cell>
												<fo:table-cell><fo:block text-align="right">${(periodTotals.get("TOT").get("kgSnf"))?string("##0.000")}</fo:block></fo:table-cell>
												<fo:table-cell><fo:block text-align="right">${(periodTotals.get("TOT").get("fat"))?string("##0.0")}</fo:block></fo:table-cell>
												<fo:table-cell><fo:block text-align="right">${(periodTotals.get("TOT").get("snf"))?string("##0.00")}</fo:block></fo:table-cell>
											</fo:table-row>						
										</#if>
									</fo:table-body>
								</fo:table>
							</fo:block> 
						</#if>
					</#list>	
				</#list> 
				<#if nonIkpTotKgs !=0>
					<fo:block font-family="Courier,monospace">
						<fo:table text-align="left" font-size="9pt">
							<fo:table-column column-width="45pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="120pt"/>
							<fo:table-column column-width="64pt"/>
							<fo:table-column column-width="63pt"/>
							<fo:table-column column-width="65pt"/>
							<fo:table-column column-width="45pt"/>
							<fo:table-column column-width="45pt"/>
							<fo:table-body>	
								<fo:table-row>
									<fo:table-cell><fo:block font-size="9pt">---------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
								</fo:table-row>
								<fo:table-row>								
									<fo:table-cell><fo:block></fo:block></fo:table-cell>
									<fo:table-cell><fo:block keep-together="always" text-align="left">NON IKP SUBTOTALS</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${(nonIkpTotLtrs)?string("##0.0")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${(nonIkpTotKgs)?string("##0.0")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${(nonIkpTotKgFat)?string("##0.000")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${(nonIkpTotKgSnf)?string("##0.000")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right"><#if nonIkpTotKgs!=0>${((nonIkpTotKgFat*100)/nonIkpTotKgs)?string("##0.0")}</#if></fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right"><#if nonIkpTotKgs!=0>${((nonIkpTotKgSnf*100)/nonIkpTotKgs)?string("##0.00")}</#if></fo:block></fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell><fo:block font-size="9pt">---------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:block> 
				</#if>
				<#list unitTotalEntries as unitTotalEntry> 
					<#assign unitTotalValues = unitTotalEntry.getValue().get("periodTransferTotalsMap")> 
					<#assign unitTotals= unitTotalValues.entrySet()>
				<#list unitTotals as unitTotalEntryValues>
					<#assign transferValues =unitTotalEntryValues.getValue().get("transfers")>			
					<#assign periodTotals = transferValues.get("procurementPeriodTotals").get("dayTotals")>					
					<#if periodTotals?has_content>							
						<fo:block font-family="Courier,monospace">
							<fo:table text-align="left" font-size="9pt">
								<fo:table-column column-width="45pt"/>
								<fo:table-column column-width="40pt"/>
								<fo:table-column column-width="120pt"/>
								<fo:table-column column-width="64pt"/>
								<fo:table-column column-width="63pt"/>
								<fo:table-column column-width="65pt"/>
								<fo:table-column column-width="45pt"/>
								<fo:table-column column-width="45pt"/>
								<fo:table-body>	
									<#assign facility = delegator.findOne("Facility", {"facilityId" : unitTotalEntryValues.getKey()}, true)>
									<#if facility.managedBy=="IKP">	
										<#assign ikpTotKgs=ikpTotKgs+(periodTotals.get("TOT").get("qtyKgs")+periodTotals.get("TOT").get("sQtyKgs"))>		
										<#assign ikpTotLtrs=ikpTotLtrs+(periodTotals.get("TOT").get("qtyLtrs")+periodTotals.get("TOT").get("sQtyLtrs"))>
										<#assign ikpTotKgFat=ikpTotKgFat+(periodTotals.get("TOT").get("kgFat"))>
										<#assign ikpTotKgSnf=ikpTotKgSnf+(periodTotals.get("TOT").get("kgSnf"))>				
										<fo:table-row>								
											<fo:table-cell><fo:block>${facility.facilityCode?if_exists}</fo:block></fo:table-cell>
											<fo:table-cell><fo:block keep-together="always" text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("facilityName")?if_exists)),19)}</fo:block></fo:table-cell>
											<fo:table-cell><fo:block text-align="right">${(periodTotals.get("TOT").get("qtyLtrs")+periodTotals.get("TOT").get("sQtyLtrs"))?string("##0.0")}</fo:block></fo:table-cell>
											<fo:table-cell><fo:block text-align="right">${(periodTotals.get("TOT").get("qtyKgs")+(periodTotals.get("TOT").get("sQtyLtrs") *1.03))?string("##0.0")}</fo:block></fo:table-cell>
											<fo:table-cell><fo:block text-align="right">${(periodTotals.get("TOT").get("kgFat"))?string("##0.000")}</fo:block></fo:table-cell>
											<fo:table-cell><fo:block text-align="right">${(periodTotals.get("TOT").get("kgSnf"))?string("##0.000")}</fo:block></fo:table-cell>
											<fo:table-cell><fo:block text-align="right">${(periodTotals.get("TOT").get("fat"))?string("##0.0")}</fo:block></fo:table-cell>
											<fo:table-cell><fo:block text-align="right">${(periodTotals.get("TOT").get("snf"))?string("##0.00")}</fo:block></fo:table-cell>
										</fo:table-row>						
									</#if>
								</fo:table-body>
							</fo:table>
						</fo:block> 
					</#if>
				</#list>	
			</#list> 
			<#if ikpTotKgs !=0>
				<fo:block font-family="Courier,monospace">
					<fo:table text-align="left" font-size="9pt">
						<fo:table-column column-width="45pt"/>
						<fo:table-column column-width="40pt"/>
						<fo:table-column column-width="120pt"/>
						<fo:table-column column-width="64pt"/>
						<fo:table-column column-width="63pt"/>
						<fo:table-column column-width="65pt"/>
						<fo:table-column column-width="45pt"/>
						<fo:table-column column-width="45pt"/>
						<fo:table-body>	
							<fo:table-row>
								<fo:table-cell><fo:block font-size="9pt">---------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
							</fo:table-row>
							<fo:table-row>								
								<fo:table-cell><fo:block></fo:block></fo:table-cell>
								<fo:table-cell><fo:block keep-together="always" text-align="left">IKP SUBTOTALS</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${(ikpTotLtrs)?string("##0.0")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${(ikpTotKgs)?string("##0.0")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${(ikpTotKgFat)?string("##0.000")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${(ikpTotKgSnf)?string("##0.000")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right"><#if ikpTotKgs!=0>${((ikpTotKgFat*100)/ikpTotKgs)?string("##0.0")}</#if></fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right"><#if ikpTotKgs!=0>${((ikpTotKgSnf*100)/ikpTotKgs)?string("##0.00")}</#if></fo:block></fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell><fo:block font-size="9pt">---------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				</fo:block>
			</#if>				
		<#else>
			<#list unitTotalEntries as unitTotalEntry> 
				<#assign unitTotalValues = unitTotalEntry.getValue().get("periodTransferTotalsMap")> 
				<#assign unitTotals= unitTotalValues.entrySet()>
				<#list unitTotals as unitTotalEntryValues>
					<#assign transferValues =unitTotalEntryValues.getValue().get("transfers")>			
					<#assign periodTotals = transferValues.get("procurementPeriodTotals").get("dayTotals")>					
					<#if periodTotals?has_content>							
						<fo:block font-family="Courier,monospace">
							<fo:table text-align="left" font-size="9pt">
								<fo:table-column column-width="45pt"/>
								<fo:table-column column-width="40pt"/>
								<fo:table-column column-width="120pt"/>
								<fo:table-column column-width="64pt"/>
								<fo:table-column column-width="63pt"/>
								<fo:table-column column-width="65pt"/>
								<fo:table-column column-width="45pt"/>
								<fo:table-column column-width="45pt"/>
								<fo:table-body>						
									<fo:table-row>
										<#assign facility = delegator.findOne("Facility", {"facilityId" : unitTotalEntryValues.getKey()}, true)>
										<fo:table-cell><fo:block>${facility.facilityCode?if_exists}</fo:block></fo:table-cell>
										<fo:table-cell><fo:block keep-together="always" text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("facilityName")?if_exists)),19)}</fo:block></fo:table-cell>
										<fo:table-cell><fo:block text-align="right">${(periodTotals.get("TOT").get("qtyLtrs")+periodTotals.get("TOT").get("sQtyLtrs"))?string("##0.0")}</fo:block></fo:table-cell>
										<fo:table-cell><fo:block text-align="right">${(periodTotals.get("TOT").get("qtyKgs")+(periodTotals.get("TOT").get("sQtyLtrs") *1.03))?string("##0.0")}</fo:block></fo:table-cell>
										<fo:table-cell><fo:block text-align="right">${(periodTotals.get("TOT").get("kgFat"))?string("##0.000")}</fo:block></fo:table-cell>
										<fo:table-cell><fo:block text-align="right">${(periodTotals.get("TOT").get("kgSnf"))?string("##0.000")}</fo:block></fo:table-cell>
										<fo:table-cell><fo:block text-align="right">${(periodTotals.get("TOT").get("fat"))?string("##0.0")}</fo:block></fo:table-cell>
										<fo:table-cell><fo:block text-align="right">${(periodTotals.get("TOT").get("snf"))?string("##0.00")}</fo:block></fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block> 
					</#if>
				</#list>	
			</#list> 
		</#if>	
			<fo:block font-size="9pt">---------------------------------------------------------------------------------------------</fo:block>
			<#if shedTotals?has_content>
				<#assign shedTranferTotals = shedTotals.get("periodTransferTotalsMap")>
				<#assign shedTransfers = shedTranferTotals.entrySet()>	
			<#list shedTransfers as shedTransferValues>		
			<#assign shedProcurementTotals = shedTransferValues.getValue().get("transfers").get("procurementPeriodTotals").get("dayTotals")>			
				<#assign TotQtyLtrs =0>
				<#assign TotQtyKgs = 0>
				<#assign TotKgFat = 0>
				<#assign TotKgSnf = 0>
				<#assign TotFat = 0>
				<#assign TotSnf = 0>	
			<#if shedProcurementTotals?has_content>			
				<#assign procumentTotals = shedProcurementTotals.get("TOT")>	
				<#if procumentTotals?has_content>
					<#assign TotQtyLtrs = (procumentTotals.get("qtyLtrs"))+(procumentTotals.get("sQtyLtrs"))>
					<#assign TotQtyKgs = (procumentTotals.get("qtyKgs")) +(procumentTotals.get("sQtyLtrs")*1.03)>
					<#assign TotKgFat = (procumentTotals.get("kgFat"))>
					<#assign TotKgSnf = (procumentTotals.get("kgSnf"))>	
					<#assign TotFat = (procumentTotals.get("fat"))>
					<#assign TotSnf = (procumentTotals.get("snf"))>								
				</#if>
			</#if>		
			<fo:block font-family="Courier,monospace">
					<fo:table text-align="left" font-size="9pt">
						<fo:table-column column-width="45pt"/>
						<fo:table-column column-width="40pt"/>
						<fo:table-column column-width="120pt"/>
						<fo:table-column column-width="64pt"/>
						<fo:table-column column-width="63pt"/>
						<fo:table-column column-width="65pt"/>
						<fo:table-column column-width="45pt"/>
						<fo:table-column column-width="45pt"/>
						<fo:table-body>						
							<fo:table-row>								
								<fo:table-cell><fo:block>&#160;</fo:block></fo:table-cell>
								<fo:table-cell><fo:block keep-together="always" text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((((facilityDetails.facilityName).toUpperCase()).replace("MILK",""))?if_exists)),19)}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${TotQtyLtrs?string("##0.0")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${TotQtyKgs?string("##0.0")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${TotKgFat?string("##0.000")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${TotKgSnf?string("##0.000")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${TotFat?string("##0.00")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${TotSnf?string("##0.00")}</fo:block></fo:table-cell>
							</fo:table-row>		
							<#if IutTotalsMap?has_content>
								<#assign iutQtyKgs = IutTotalsMap.get("iutQty")>
								<#assign iutQtyLtrs =  ((IutTotalsMap.get("iutQtyLtrs")))>
								<#assign iutKgFat = (IutTotalsMap.get("iutKgFat"))>
								<#assign iutKgSnf = (IutTotalsMap.get("iutKgSnf"))>
							</#if>		
							<#if iutQtyKgs !=0>						
								<fo:table-row>								
									<fo:table-cell><fo:block keep-together="always">&#160;</fo:block></fo:table-cell>
									<fo:table-cell><fo:block  keep-together="always" text-align="left">IUT RECIEVED(sheds)</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${iutQtyLtrs?string("##0.0")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${iutQtyKgs?string("##0.0")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${iutKgFat?string("##0.000")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${iutKgSnf?string("##0.000")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right"><#if iutQtyKgs !=0>${((iutKgFat*100)/iutQtyKgs)?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right"><#if iutQtyKgs !=0>${((iutKgSnf*100)/iutQtyKgs)?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>							
								</fo:table-row>	
							</#if>						
							<fo:table-row>
								<fo:table-cell><fo:block font-size="9pt">---------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
							</fo:table-row>
							<#assign totInputLtr = (totInputLtr+iutQtyLtrs+TotQtyLtrs)>
							<#assign totInputQty = (totInputQty+iutQtyKgs+TotQtyKgs)>
							<#assign totInputKgFat = (totInputKgFat+iutKgFat+TotKgFat)>
							<#assign totInputKgSnf = (totInputKgSnf+iutKgSnf+TotKgSnf)>
							<fo:table-row >								
								<fo:table-cell><fo:block keep-together="always">&#160;</fo:block></fo:table-cell>
								<fo:table-cell><fo:block  keep-together="always">TOTAL INPUT</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right" font-size="9pt">${totInputLtr?string("##0.0")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right" font-size="9pt">${totInputQty?string("##0.0")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right" font-size="9pt">${totInputKgFat?string("##0.000")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${totInputKgSnf?string("##0.000")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right"><#if totInputQty !=0>${((totInputKgFat*100)/totInputQty)?string("##0.0")}<#else>0.00</#if></fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right"><#if totInputQty !=0>${((totInputKgSnf*100)/totInputQty)?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell><fo:block font-size="9pt">---------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell><fo:block font-size="11pt" keep-together="always">MPF RECEIPTS :</fo:block></fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				</fo:block>
			</#list>
			<#assign outTotQty =0> 
			<#assign outTotLtr =0>
			<#assign outTotKgFat =0>
			<#assign outTotKgSnf =0>
			<#list unitTotalEntries as unitTotalEntry> 
				<#assign unitTotalValues = unitTotalEntry.getValue().get("periodTransferTotalsMap")> 
				<#assign unitTotals= unitTotalValues.entrySet()>
				<#list unitTotals as unitTotalEntryValues>
					<#assign transferValues =unitTotalEntryValues.getValue().get("transfers")>
					<#assign outputValue = transferValues.get("output")>
					<#assign outPutEntries = transferValues.get("outputEntries")>				
					<#if outputValue?has_content>					
						<#assign output = outputValue.get("mpfReciepts")>					
						<#assign outTotLtr = outTotLtr + (output.get("qtyLts"))> 
						<#assign outTotQty = outTotQty + (output.get("qtyKgs"))>
						<#assign outTotKgFat = outTotKgFat + (output.get("kgFat"))>
						<#assign outTotKgSnf = outTotKgSnf + (output.get("kgSnf"))>					
					<fo:block font-family="Courier,monospace">
						<fo:table text-align="left" font-size="9pt">
							<fo:table-column column-width="45pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="120pt"/>
							<fo:table-column column-width="64pt"/>
							<fo:table-column column-width="63pt"/>
							<fo:table-column column-width="65pt"/>
							<fo:table-column column-width="45pt"/>
							<fo:table-column column-width="45pt"/>
							<fo:table-body>
							  <#if output.get("qtyLts")!=0 && output.get("qtyKgs")!=0 && output.get("kgFat")!=0 && output.get("kgSnf")!=0>	
								<fo:table-row>								
									<#assign facility = delegator.findOne("Facility", {"facilityId" : unitTotalEntryValues.getKey()}, true)>
									<fo:table-cell><fo:block>${facility.facilityCode?if_exists}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("facilityName")?if_exists)),19)}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right" font-size="9pt">${(output.get("qtyLts"))?string("##0.0")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right" font-size="9pt">${(output.get("qtyKgs"))?string("##0.0")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right" font-size="9pt">${(output.get("kgFat"))?string("##0.000")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${(output.get("kgSnf"))?string("##0.000")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${(output.get("fat"))?string("##0.0")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${(output.get("snf"))?string("##0.00")}</fo:block></fo:table-cell>
								</fo:table-row>
							</#if>	
							</fo:table-body>
						</fo:table>
					</fo:block>			
					</#if>		
				</#list>
			</#list>
			</#if>			 				   
				<fo:block font-family="Courier,monospace">
					<fo:table text-align="left" font-size="9pt">
						<fo:table-column column-width="45pt"/>
						<fo:table-column column-width="40pt"/>
						<fo:table-column column-width="120pt"/>
						<fo:table-column column-width="64pt"/>
						<fo:table-column column-width="63pt"/>
						<fo:table-column column-width="65pt"/>
						<fo:table-column column-width="45pt"/>
						<fo:table-column column-width="45pt"/>
						<fo:table-body>	
							<fo:table-row>
								<fo:table-cell><fo:block font-size="9pt">---------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
							</fo:table-row>					
							<fo:table-row>								
								<fo:table-cell><fo:block>&#160;</fo:block></fo:table-cell>
								<fo:table-cell><fo:block keep-together="always" text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((((facilityDetails.facilityName).toUpperCase()).replace("MILK",""))?if_exists)),19)}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${outTotLtr?string("##0.0")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${outTotQty?string("##0.0")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${outTotKgFat?string("##0.000")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${outTotKgSnf?string("##0.000")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right"><#if outTotQty !=0>${((outTotKgFat*100)/outTotQty)?string("##0.0")}<#else>0.00</#if></fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right"><#if outTotQty !=0>${((outTotKgSnf*100)/outTotQty)?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell><fo:block font-size="9pt">---------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
							</fo:table-row>						
						</fo:table-body>
					</fo:table>
				</fo:block>
					
				
			<#if iutTransferMap?has_content>
				<#assign iutTranQtyKgs = 0>
				<#assign iutTranQtyLtrs = 0>
				<#assign iutTranKgFat = 0>
				<#assign iutTranKgSnf = 0>
				<fo:block font-family="Courier,monospace">
					<fo:table text-align="left" font-size="9pt">
						<fo:table-column column-width="45pt"/>
						<fo:table-column column-width="40pt"/>
						<fo:table-column column-width="120pt"/>
						<fo:table-column column-width="64pt"/>
						<fo:table-column column-width="63pt"/>
						<fo:table-column column-width="65pt"/>
						<fo:table-column column-width="45pt"/>
						<fo:table-column column-width="45pt"/>
						<fo:table-body>						
							<fo:table-row>
								<fo:table-cell><fo:block font-size="11pt" keep-together="always">IUT TRANSFERS :</fo:block></fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				</fo:block>
				<#assign outputEntries =iutTransferMap.entrySet()>
				<#list outputEntries as outputEntry>
					<#assign iutKey = outputEntry.getKey()>
					<#assign output= outputEntry.getValue()>
					<#assign output= output.getValue()>
						<#if (output.get("qtyKgs"))!=0>
							<#assign outTotLtr = outTotLtr + (output.get("qtyLtrs"))> 
							<#assign outTotQty = outTotQty + (output.get("qtyKgs"))>
							<#assign outTotKgFat = outTotKgFat + (output.get("kgFat"))>
							<#assign outTotKgSnf = outTotKgSnf + (output.get("kgSnf"))>	
							
							<#assign iutTranQtyLtrs = iutTranQtyLtrs + (output.get("qtyLtrs"))> 
							<#assign iutTranQtyKgs = iutTranQtyKgs + (output.get("qtyKgs"))>
							<#assign iutTranKgFat = iutTranKgFat + (output.get("kgFat"))>
							<#assign iutTranKgSnf = iutTranKgSnf + (output.get("kgSnf"))>	
						  <fo:block font-family="Courier,monospace">
							<fo:table text-align="left" font-size="9pt">
							<fo:table-column column-width="45pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="120pt"/>
							<fo:table-column column-width="64pt"/>
							<fo:table-column column-width="63pt"/>
							<fo:table-column column-width="65pt"/>
							<fo:table-column column-width="45pt"/>
							<fo:table-column column-width="45pt"/>
							<fo:table-body>
								<fo:table-row>								
									<#assign facility = delegator.findOne("Facility", {"facilityId" :iutKey}, true)>
									<fo:table-cell><fo:block>&#160;</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("facilityName")?if_exists)),19)}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right" font-size="9pt">${(output.get("qtyLtrs"))?string("##0.0")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right" font-size="9pt">${(output.get("qtyKgs"))?string("##0.0")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right" font-size="9pt">${(output.get("kgFat"))?string("##0.000")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${(output.get("kgSnf"))?string("##0.000")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${(output.get("fat"))?string("##0.0")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${(output.get("snf"))?string("##0.00")}</fo:block></fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:block>			
						</#if>
				</#list>
				<fo:block font-family="Courier,monospace">
					<fo:table text-align="left" font-size="9pt">
						<fo:table-column column-width="45pt"/>
						<fo:table-column column-width="40pt"/>
						<fo:table-column column-width="120pt"/>
						<fo:table-column column-width="64pt"/>
						<fo:table-column column-width="63pt"/>
						<fo:table-column column-width="65pt"/>
						<fo:table-column column-width="45pt"/>
						<fo:table-column column-width="45pt"/>
						<fo:table-body>	
							<fo:table-row>
								<fo:table-cell><fo:block font-size="9pt">---------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
							</fo:table-row>					
							<fo:table-row>								
								<fo:table-cell><fo:block>&#160;</fo:block></fo:table-cell>
								<fo:table-cell><fo:block keep-together="always" text-align="left">IUT TRANSFERS</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${iutTranQtyLtrs?string("##0.0")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${iutTranQtyKgs?string("##0.0")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${iutTranKgFat?string("##0.000")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${iutTranKgSnf?string("##0.000")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right"><#if iutTranQtyKgs !=0>${((iutTranKgFat*100)/iutTranQtyKgs)?string("##0.0")}<#else>0.0</#if></fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right"><#if iutTranQtyKgs !=0>${((iutTranKgSnf*100)/iutTranQtyKgs)?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell><fo:block font-size="9pt">---------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
							</fo:table-row>						
						</fo:table-body>
					</fo:table>
				</fo:block>
			</#if>		
					
				<#assign totOutputLtr = outTotLtr>
				<#assign totOutputQty = outTotQty>
				<#assign totOutputKgFat = outTotKgFat>
				<#assign totOutputKgSnf = outTotKgSnf>
					<fo:block font-family="Courier,monospace">
					<fo:table text-align="left" font-size="9pt">
						<fo:table-column column-width="45pt"/>
						<fo:table-column column-width="40pt"/>
						<fo:table-column column-width="120pt"/>
						<fo:table-column column-width="64pt"/>
						<fo:table-column column-width="63pt"/>
						<fo:table-column column-width="65pt"/>
						<fo:table-column column-width="45pt"/>
						<fo:table-column column-width="45pt"/>
							<fo:table-body>
								<#if outputEntriesMap?has_content>
								<#assign outputEntries =outputEntriesMap.entrySet()>
									<#list outputEntries as outputEntry>
										<#assign key = outputEntry.getKey()>
										<#if key!="CLOSING_BALANCE">
										<#assign value= outputEntry.getValue()>
											<#if (value.get("qtyKgs"))!=0>
												<#assign totOutputLtr = totOutputLtr+(value.get("qtyLtrs"))>
												<#assign totOutputQty = totOutputQty+(value.get("qtyKgs"))>
												<#assign totOutputKgFat = totOutputKgFat+(value.get("kgFat"))>
												<#assign totOutputKgSnf = totOutputKgSnf+(value.get("kgSnf"))>
												
												<fo:table-row>								
													<fo:table-cell><fo:block>&#160;</fo:block></fo:table-cell>
													<fo:table-cell><fo:block keep-together="always" text-align="left">${key.replace("_"," ")}</fo:block></fo:table-cell>
													<fo:table-cell><fo:block text-align="right">${value.get("qtyLtrs")?if_exists?string("##0.0")}</fo:block></fo:table-cell>
													<fo:table-cell><fo:block text-align="right">${value.get("qtyKgs")?if_exists?string("##0.0")}</fo:block></fo:table-cell>
													<fo:table-cell><fo:block text-align="right">${value.get("kgFat")?if_exists?string("##0.000")}</fo:block></fo:table-cell>
													<fo:table-cell><fo:block text-align="right">${value.get("kgSnf")?if_exists?string("##0.000")}</fo:block></fo:table-cell>
													<fo:table-cell><fo:block text-align="right">${value.get("fat")?if_exists?string("##0.0")}</fo:block></fo:table-cell>
													<fo:table-cell><fo:block text-align="right">${value.get("snf")?if_exists?string("##0.00")}</fo:block></fo:table-cell>
												</fo:table-row>
											</#if>
										</#if>
									</#list>
									<#if (closingBalanceMap.get("qtyKgs"))!=0>
										<#assign totOutputLtr = totOutputLtr+(closingBalanceMap.get("qtyLtrs"))>
										<#assign totOutputQty = totOutputQty+(closingBalanceMap.get("qtyKgs"))>
										<#assign totOutputKgFat = totOutputKgFat+(closingBalanceMap.get("kgFat"))>
										<#assign totOutputKgSnf = totOutputKgSnf+(closingBalanceMap.get("kgSnf"))>
										<fo:table-row>								
											<fo:table-cell><fo:block>&#160;</fo:block></fo:table-cell>
											<fo:table-cell><fo:block keep-together="always" text-align="left">CLOSING BALANCE</fo:block></fo:table-cell>
											<fo:table-cell><fo:block text-align="right">${closingBalanceMap.get("qtyLtrs")?if_exists?string("##0.0")}</fo:block></fo:table-cell>
											<fo:table-cell><fo:block text-align="right">${closingBalanceMap.get("qtyKgs")?if_exists?string("##0.0")}</fo:block></fo:table-cell>
											<fo:table-cell><fo:block text-align="right">${closingBalanceMap.get("kgFat")?if_exists?string("##0.000")}</fo:block></fo:table-cell>
											<fo:table-cell><fo:block text-align="right">${closingBalanceMap.get("kgSnf")?if_exists?string("##0.000")}</fo:block></fo:table-cell>
											<fo:table-cell><fo:block text-align="right">${closingBalanceMap.get("fat")?if_exists?string("##0.0")}</fo:block></fo:table-cell>
											<fo:table-cell><fo:block text-align="right">${closingBalanceMap.get("snf")?if_exists?string("##0.00")}</fo:block></fo:table-cell>
										</fo:table-row>
									</#if>
									
							</#if>
								
								<fo:table-row>
									<fo:table-cell><fo:block font-size="9pt">---------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
								</fo:table-row>
								<#assign totOutputLtr = (outTotLtr+clQtyLtrs+saleQtyLtrs+tmQtyLtrs+sourQtyLtrs+curdQtyLtrs)>
								<#assign totOutputQty = (outTotQty+clQty+saleQty+tmQty+sourQty+curdQty)>
								<#assign totOutputKgFat = (outTotKgFat+clKgFat+saleKgFat+tmKgFat+sourKgFat+curdKgFat)>
								<#assign totOutputKgSnf = (outTotKgSnf+clKgSnf+saleKgSnf+tmKgSnf+sourKgSnf+curdKgSnf)>
								<fo:table-row>
									<fo:table-cell><fo:block>&#160;</fo:block></fo:table-cell>
									<fo:table-cell><fo:block keep-together="always" text-align="left">TOTAL OUTPUT</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${totOutputLtr?if_exists?string("##0.0")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right" >${totOutputQty?if_exists?string("##0.0")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right" >${totOutputKgFat?if_exists?string("##0.000")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${totOutputKgSnf?if_exists?string("##0.000")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right"><#if totOutputQty !=0>${((totOutputKgFat*100)/totOutputQty)?if_exists?string("##0.0")}<#else>0.00</#if></fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right"><#if totOutputKgSnf !=0>${((totOutputKgSnf*100)/totOutputQty)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell><fo:block font-size="9pt">---------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
								</fo:table-row>								
								<fo:table-row>
									<fo:table-cell><fo:block>&#160;</fo:block></fo:table-cell>
									<fo:table-cell><fo:block keep-together="always" text-align="left">EXCESS/SHORTAGE</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${(totOutputLtr-totInputLtr)?if_exists?string("##0.0")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right" >${(totOutputQty-totInputQty)?if_exists?string("##0.0")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right" >${(totOutputKgFat-totInputKgFat)?if_exists?string("##0.000")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${(totOutputKgSnf-totInputKgSnf)?if_exists?string("##0.000")}</fo:block></fo:table-cell>
									<#assign avgOutPutFat =0>
									<#assign avgInPutFat =0>
									<#assign avgOutPutSnf =0>
									<#assign avgInPutSnf =0>
									<#if (totOutputQty) !=0>
										<#assign avgOutPutFat =((totOutputKgFat*100)/totOutputQty)>
										<#assign avgOutPutSnf =((totOutputKgSnf*100)/totOutputQty)>
									</#if>
									<#if (totInputQty) !=0>
										<#assign avgInPutFat =((totInputKgFat*100)/totInputQty)>
										<#assign avgInPutSnf =((totInputKgSnf*100)/totInputQty)>
									</#if>
									<fo:table-cell><fo:block text-align="right">${((avgOutPutFat)-(avgInPutFat))?if_exists?string("##0.0")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${((avgOutPutSnf)-(avgInPutSnf))?if_exists?string("##0.00")}</fo:block></fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell><fo:block font-size="9pt">---------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
								</fo:table-row>							
							</fo:table-body>
						</fo:table>
					</fo:block>							
			</fo:flow>		
		</fo:page-sequence>
	</#if>
	</#if>		
	</fo:root>
</#escape>