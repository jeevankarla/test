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
			<#assign obQty =0> 
			<#assign obQtyLtr =0>
			<#assign obKgFat =0>
			<#assign obKgSnf =0>
			<#assign tmObQty =0> 
			<#assign tmObQtyLtr =0>
			<#assign tmObKgFat =0>
			<#assign tmObKgSnf =0>
			<#if shedWiseTotMap?has_content>
				<#assign obQty = shedWiseTotMap.get("OpeningBalace").get("qty")>
				<#assign obQtyLtr = shedWiseTotMap.get("OpeningBalace").get("quantityLtrs")>
				<#assign obKgFat = shedWiseTotMap.get("OpeningBalace").get("kgFat")>
				<#assign obKgSnf = shedWiseTotMap.get("OpeningBalace").get("kgSnf")>
				
				<#assign tmObQty = shedWiseTotMap.get("TMPreparation").get("qty")>
				<#assign tmObQtyLtr = shedWiseTotMap.get("TMPreparation").get("quantityLtrs")>
				<#assign tmObKgFat = shedWiseTotMap.get("TMPreparation").get("kgFat")>
				<#assign tmObKgSnf = shedWiseTotMap.get("TMPreparation").get("kgSnf")>	
				<fo:block font-family="Courier,monospace">
					<fo:table text-align="left" font-size="9pt">
						<fo:table-column column-width="45pt"/>
						<fo:table-column column-width="40pt"/>
						<fo:table-column column-width="120pt"/>
						<fo:table-column column-width="60pt"/>
						<fo:table-column column-width="63pt"/>
						<fo:table-column column-width="65pt"/>
						<fo:table-column column-width="45pt"/>
						<fo:table-column column-width="45pt"/>
						<fo:table-column column-width="40pt"/>
						<fo:table-body>
							<fo:table-row>								
								<fo:table-cell><fo:block></fo:block></fo:table-cell>
								<fo:table-cell><fo:block keep-together="always" text-align="left">Opening Balance</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${obQtyLtr?string("##0.0")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${obQty?string("##0.0")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${obKgFat?string("##0.000")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${obKgSnf?string("##0.000")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right"><#if obQty !=0>${((obKgFat*100)/obQty)?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right"><#if obQty !=0>${((obKgSnf*100)/obQty)?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>
							</fo:table-row>						
							<fo:table-row>								
								<fo:table-cell><fo:block></fo:block></fo:table-cell>
								<fo:table-cell><fo:block keep-together="always" text-align="left">TM Balance</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${tmObQtyLtr?string("##0.0")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${tmObQty?string("##0.0")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${tmObKgFat?string("##0.000")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${tmObKgSnf?string("##0.000")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right"><#if tmObQty !=0>${((tmObKgFat*100)/tmObQty)?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right"><#if tmObQty !=0>${((tmObKgSnf*100)/tmObQty)?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>
							</fo:table-row>
							
						</fo:table-body>
					</fo:table>
				</fo:block>
			</#if>	
			<#assign iutQtyKgs =0>
			<#assign iutQtyLtrs =0>
			<#assign iutKgFat =0>
			<#assign iutKgSnf =0>				
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
						<fo:table-column column-width="60pt"/>
						<fo:table-column column-width="63pt"/>
						<fo:table-column column-width="65pt"/>
						<fo:table-column column-width="45pt"/>
						<fo:table-column column-width="45pt"/>
						<fo:table-column column-width="40pt"/>
						<fo:table-body>						
							<fo:table-row>
								<#assign facility = delegator.findOne("Facility", {"facilityId" : unitTotalEntryValues.getKey()}, true)>
								<fo:table-cell><fo:block>${facility.facilityCode?if_exists}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block keep-together="always" text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("facilityName")?if_exists)),15)}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${(periodTotals.get("TOT").get("qtyLtrs")+periodTotals.get("TOT").get("sQtyLtrs"))?string("##0.0")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${(periodTotals.get("TOT").get("qtyKgs")+(periodTotals.get("TOT").get("sQtyLtrs") *1.03))?string("##0.0")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${(periodTotals.get("TOT").get("kgFat"))?string("##0.000")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${(periodTotals.get("TOT").get("kgSnf"))?string("##0.000")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${(periodTotals.get("TOT").get("fat"))?string("##0.00")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${(periodTotals.get("TOT").get("snf"))?string("##0.00")}</fo:block></fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				</fo:block> 
				</#if>
			</#list>	
			</#list> 
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
					<fo:table font-size="9pt">
						<fo:table-column column-width="45pt"/>
						<fo:table-column column-width="40pt"/>
						<fo:table-column column-width="120pt"/>
						<fo:table-column column-width="63pt"/>
						<fo:table-column column-width="63pt"/>
						<fo:table-column column-width="65pt"/>
						<fo:table-column column-width="43pt"/>
						<fo:table-column column-width="43pt"/>
						<fo:table-column column-width="30pt"/>
						<fo:table-body>						
							<fo:table-row>								
								<fo:table-cell><fo:block></fo:block></fo:table-cell>
								<fo:table-cell><fo:block keep-together="always" text-align="left">${facilityDetails.facilityName}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${TotQtyLtrs?string("##0.0")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${TotQtyKgs?string("##0.0")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${TotKgFat?string("##0.000")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${TotKgSnf?string("##0.000")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${TotFat?string("##0.00")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${TotSnf?string("##0.00")}</fo:block></fo:table-cell>
							</fo:table-row>		
							<#if IutTotalsMap?has_content>
								<#assign iutQtyKgs = IutTotalsMap.get("iutQty")>
								<#assign iutQtyLtrs =  ((IutTotalsMap.get("iutQty"))/1.03)>
								<#assign iutKgFat = (IutTotalsMap.get("iutKgFat"))>
								<#assign iutKgSnf = (IutTotalsMap.get("iutKgSnf"))>
							</#if>								
							<fo:table-row>								
								<fo:table-cell><fo:block  keep-together="always" text-align="left">IUT RECIEVED(sheds)</fo:block></fo:table-cell>
								<fo:table-cell><fo:block keep-together="always"></fo:block></fo:table-cell>									
								<fo:table-cell><fo:block text-align="right">${iutQtyLtrs?string("##0.0")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${iutQtyKgs?string("##0.0")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${iutKgFat?string("##0.000")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${iutKgSnf?string("##0.000")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right"><#if iutQtyKgs !=0>${((iutKgFat*100)/iutQtyKgs)?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right"><#if iutQtyKgs !=0>${((iutKgSnf*100)/iutQtyKgs)?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>							
							</fo:table-row>							
							<fo:table-row>
								<fo:table-cell><fo:block font-size="9pt">---------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
							</fo:table-row>
							<#assign totInputLtr = (iutQtyLtrs+TotQtyLtrs+obQtyLtr+tmObQtyLtr)>
							<#assign totInputQty = (iutQtyKgs+TotQtyKgs+obQty+tmObQty)>
							<#assign totInputKgFat = (iutKgFat+TotKgFat+obKgFat+tmObKgFat)>
							<#assign totInputKgSnf = (iutKgSnf+TotKgSnf+obKgSnf+tmObKgSnf)>
							<fo:table-row >								
								<fo:table-cell><fo:block  keep-together="always">TOTAL INPUT</fo:block></fo:table-cell>
								<fo:table-cell><fo:block keep-together="always"></fo:block></fo:table-cell>								
								<fo:table-cell><fo:block text-align="right" font-size="9pt">${totInputLtr?string("##0.0")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right" font-size="9pt">${totInputQty?string("##0.0")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right" font-size="9pt">${totInputKgFat?string("##0.000")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${totInputKgSnf?string("##0.000")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right"><#if totInputQty !=0>${((totInputKgFat*100)/totInputQty)?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>
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
					<fo:table >
						<fo:table-column column-width="20pt"/>
						<fo:table-column column-width="60pt"/>
						<fo:table-column column-width="100pt"/>
						<fo:table-column column-width="65pt"/>
						<fo:table-column column-width="58pt"/>
						<fo:table-column column-width="75pt"/>
						<fo:table-column column-width="33pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="40pt"/>
						<fo:table-body>
						  <#if output.get("qtyLts")!=0 && output.get("qtyKgs")!=0 && output.get("kgFat")!=0 && output.get("kgSnf")!=0>	
							<fo:table-row>								
								<#assign facility = delegator.findOne("Facility", {"facilityId" : unitTotalEntryValues.getKey()}, true)>
								<fo:table-cell><fo:block>${facility.facilityCode?if_exists}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("facilityName")?if_exists)),15)}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right" font-size="9pt">${(output.get("qtyLts"))?string("##0.0")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right" font-size="9pt">${(output.get("qtyKgs"))?string("##0.0")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right" font-size="9pt">${(output.get("kgFat"))?string("##0.000")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${(output.get("kgSnf"))?string("##0.000")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${(output.get("fat"))?string("##0.00")}</fo:block></fo:table-cell>
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
					<fo:table font-size="9pt">
						<fo:table-column column-width="45pt"/>
						<fo:table-column column-width="40pt"/>
						<fo:table-column column-width="113pt"/>
						<fo:table-column column-width="73pt"/>
						<fo:table-column column-width="63pt"/>
						<fo:table-column column-width="65pt"/>
						<fo:table-column column-width="44pt"/>
						<fo:table-column column-width="45pt"/>
						<fo:table-column column-width="30pt"/>
						<fo:table-body>	
							<fo:table-row>
								<fo:table-cell><fo:block font-size="9pt">---------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
							</fo:table-row>					
							<fo:table-row>								
								<fo:table-cell><fo:block></fo:block></fo:table-cell>
								<fo:table-cell><fo:block keep-together="always" text-align="left">${facilityDetails.facilityName}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${outTotLtr?string("##0.0")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${outTotQty?string("##0.0")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${outTotKgFat?string("##0.000")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right">${outTotKgSnf?string("##0.000")}</fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right"><#if outTotQty !=0>${((outTotKgFat*100)/outTotQty)?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>
								<fo:table-cell><fo:block text-align="right"><#if outTotQty !=0>${((outTotKgSnf*100)/outTotQty)?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell><fo:block font-size="9pt">---------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
							</fo:table-row>						
						</fo:table-body>
					</fo:table>
				</fo:block>
				<#assign clQty =0>
				<#assign clQtyLtrs =0>
				<#assign clKgFat =0>
				<#assign clKgSnf =0>
				
				<#assign saleQty =0>
				<#assign saleQtyLtrs =0>
				<#assign saleKgFat =0>
				<#assign saleKgSnf =0>
				
				<#assign sourQty =0>
				<#assign sourQtyLtrs =0>
				<#assign sourKgFat =0>
				<#assign sourKgSnf =0>
				
				<#assign curdQty =0>
				<#assign curdQtyLtrs =0>
				<#assign curdKgFat =0>
				<#assign curdKgSnf =0>
				
				<#assign tmQty =0>
				<#assign tmQtyLtrs =0>
				<#assign tmKgFat =0>
				<#assign tmKgSnf =0>
				<#list unitTotalEntries as unitTotalEntry> 
				<#assign unitTotalValues = unitTotalEntry.getValue().get("periodTransferTotalsMap")> 
				<#assign unitTotals= unitTotalValues.entrySet()>				
				<#list unitTotals as unitTotalEntryValues>
					<#assign transferValues =unitTotalEntryValues.getValue().get("transfers")>			
					<#assign outPutEntries = transferValues.get("outputEntries")>					
					<#list outPutEntries as outputEntry>	
						<#if outputEntry.get("outputType")=="CLOSING_BALANCE">
							<#assign clQty = clQty+ outputEntry.get("qtyKgs")>
							<#assign clQtyLtrs = clQtyLtrs+ outputEntry.get("qtyLts")>
							<#assign clKgFat = clKgFat+outputEntry.get("kgFat")>
							<#assign clKgSnf = clKgSnf+outputEntry.get("kgSnf")>
						<#elseif outputEntry.get("outputType")=="LOCAL_SALES">
							<#assign saleQty = saleQty+ outputEntry.get("qtyKgs")>
							<#assign saleQtyLtrs = saleQtyLtrs+ outputEntry.get("qtyLts")>
							<#assign saleKgFat = saleKgFat+outputEntry.get("kgFat")>
							<#assign saleKgSnf = saleKgSnf+outputEntry.get("kgSnf")>
						<#elseif outputEntry.get("outputType")=="TM_PREPARATION">
							<#assign tmQty = tmQty+ outputEntry.get("qtyKgs")>
							<#assign tmQtyLtrs = tmQtyLtrs+ outputEntry.get("qtyLts")>
							<#assign tmKgFat = tmKgFat+outputEntry.get("kgFat")>
							<#assign tmKgSnf = tmKgSnf+outputEntry.get("kgSnf")>
						<#elseif outputEntry.get("outputType")=="SOUR">
							<#assign sourQty = sourQty+ outputEntry.get("qtyKgs")>
							<#assign sourQtyLtrs = sourQtyLtrs+ outputEntry.get("qtyLts")>
							<#assign sourKgFat = sourKgFat+outputEntry.get("kgFat")>
							<#assign sourKgSnf = sourKgSnf+outputEntry.get("kgSnf")>	
						<#elseif outputEntry.get("outputType")=="CURDLED">
							<#assign curdQty = curdQty+ outputEntry.get("qtyKgs")>
							<#assign curdQtyLtrs = curdQtyLtrs+ outputEntry.get("qtyLts")>
							<#assign curdKgFat = curdKgFat+outputEntry.get("kgFat")>
							<#assign curdKgSnf = curdKgSnf+outputEntry.get("kgSnf")>		
						</#if>	
					</#list>
					</#list>
				</#list>		
					<fo:block font-family="Courier,monospace" font-size="9pt">
						<fo:table >
							<fo:table-column column-width="45pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="120pt"/>
							<fo:table-column column-width="63pt"/>
							<fo:table-column column-width="65pt"/>
							<fo:table-column column-width="65pt"/>
							<fo:table-column column-width="48pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell><fo:block></fo:block></fo:table-cell>
									<fo:table-cell><fo:block keep-together="always" text-align="left">LOCAL SALES</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${saleQtyLtrs?if_exists?string("##0.0")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right" >${saleQty?if_exists?string("##0.0")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right" >${saleKgFat?if_exists?string("##0.000")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${saleKgSnf?if_exists?string("##0.000")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right"><#if saleQty !=0>${((saleKgFat*100)/saleQty)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right"><#if saleQty !=0>${((saleKgSnf*100)/saleQty)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell><fo:block></fo:block></fo:table-cell>
									<fo:table-cell><fo:block keep-together="always" text-align="left">TM PREPARATION</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${tmQtyLtrs?if_exists?string("##0.0")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right" >${tmQty?if_exists?string("##0.0")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right" >${tmKgFat?if_exists?string("##0.000")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${tmKgSnf?if_exists?string("##0.000")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right"><#if tmQty !=0>${((tmKgFat*100)/tmQty)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right"><#if tmQty !=0>${((tmKgSnf*100)/tmQty)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell><fo:block></fo:block></fo:table-cell>
									<fo:table-cell><fo:block keep-together="always" text-align="left">SOUR</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${sourQtyLtrs?if_exists?string("##0.0")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right" >${sourQty?if_exists?string("##0.0")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right" >${sourKgFat?if_exists?string("##0.000")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${sourKgSnf?if_exists?string("##0.000")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right"><#if sourQty !=0>${((sourKgFat*100)/sourQty)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right"><#if sourQty !=0>${((sourKgSnf*100)/sourQty)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell><fo:block></fo:block></fo:table-cell>
									<fo:table-cell><fo:block keep-together="always" text-align="left">CURDLED</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${curdQtyLtrs?if_exists?string("##0.0")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right" >${curdQty?if_exists?string("##0.0")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right" >${curdKgFat?if_exists?string("##0.000")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${curdKgSnf?if_exists?string("##0.000")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right"><#if curdQty !=0>${((curdKgFat*100)/curdQty)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right"><#if curdQty !=0>${((curdKgSnf*100)/curdQty)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell><fo:block></fo:block></fo:table-cell>
									<fo:table-cell><fo:block keep-together="always" text-align="left">CLOSING BALANCE</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${clQtyLtrs?if_exists?string("##0.0")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right" >${clQty?if_exists?string("##0.0")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right" >${clKgFat?if_exists?string("##0.000")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${clKgSnf?if_exists?string("##0.000")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right"><#if clQty !=0>${((clKgFat*100)/clQty)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right"><#if clQty !=0>${((clKgSnf*100)/clQty)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>
								</fo:table-row>	
								<fo:table-row>
									<fo:table-cell><fo:block font-size="9pt">---------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
								</fo:table-row>
								<#assign totOutputLtr = (outTotLtr+clQtyLtrs+saleQtyLtrs+tmQtyLtrs+sourQtyLtrs+curdQtyLtrs)>
								<#assign totOutputQty = (outTotQty+clQty+saleQty+tmQty+sourQty+curdQty)>
								<#assign totOutputKgFat = (outTotKgFat+clKgFat+saleKgFat+tmKgFat+sourKgFat+curdKgFat)>
								<#assign totOutputKgSnf = (outTotKgSnf+clKgSnf+saleKgSnf+tmKgSnf+sourKgSnf+curdKgSnf)>
								<fo:table-row>
									<fo:table-cell><fo:block></fo:block></fo:table-cell>
									<fo:table-cell><fo:block keep-together="always" text-align="left">TOTAL OUTPUT</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${totOutputLtr?if_exists?string("##0.0")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right" >${totOutputQty?if_exists?string("##0.0")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right" >${totOutputKgFat?if_exists?string("##0.000")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${totOutputKgSnf?if_exists?string("##0.000")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right"><#if totOutputQty !=0>${((totOutputKgFat*100)/totOutputQty)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right"><#if totOutputKgSnf !=0>${((totOutputKgSnf*100)/totOutputQty)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell><fo:block font-size="9pt">---------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
								</fo:table-row>								
								<fo:table-row>
									<fo:table-cell><fo:block></fo:block></fo:table-cell>
									<fo:table-cell><fo:block keep-together="always" text-align="left">EXCESS/SHORTAGE</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${(totOutputLtr-totInputLtr)?if_exists?string("##0.0")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right" >${(totOutputQty-totInputQty)?if_exists?string("##0.0")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right" >${(totOutputKgFat-totInputKgFat)?if_exists?string("##0.000")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right">${(totOutputKgSnf-totInputKgSnf)?if_exists?string("##0.000")}</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right"><#if (totOutputQty-totInputQty) !=0>${(((totOutputKgFat-totInputKgFat)*100)/(totOutputQty-totInputQty))?if_exists?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right"><#if (totOutputQty-totInputQty) !=0>${(((totOutputKgSnf-totInputKgSnf)*100)/(totOutputQty-totInputQty))?if_exists?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>
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
	</fo:root>
</#escape>