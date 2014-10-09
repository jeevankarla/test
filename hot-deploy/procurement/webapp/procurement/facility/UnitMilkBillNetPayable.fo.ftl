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
<#if errorMessage?has_content>
<fo:page-sequence master-reference="main">
   <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
      <fo:block font-size="14pt">
              ${errorMessage}.
   	  </fo:block>
   </fo:flow>
</fo:page-sequence>        
<#else>          
	    <#if UnitWiseDetailsMap?exists>    
	    	<#assign unitTotalEntries = UnitWiseDetailsMap.entrySet()>
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
			<#list unitTotalEntries as unitTotalEntry>				
				<#assign facility = delegator.findOne("Facility", {"facilityId" : unitTotalEntry.getKey()}, true)>				
					<#assign netAmt = (unitTotalEntry.getValue().get("netAmtPayable"))>
					<#if netAmt!=0>
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
										<fo:block>${facility.facilityCode?if_exists}</fo:block>
									</fo:table-cell>
									<fo:table-cell><fo:block keep-together="always" text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("facilityName")?if_exists)),18)}</fo:block></fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">${unitTotalEntry.getValue().get("milkValue")?if_exists}</fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right"><#if ((unitTotalEntry.getValue().get("shortKgFat"))<0)>${(unitTotalEntry.getValue().get("shortKgFat"))?if_exists?string("##0.000")}<#else>0.000</#if></fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">${(unitTotalEntry.getValue().get("shortKgFatAmt"))?if_exists}</fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right"><#if ((unitTotalEntry.getValue().get("shortKgSnf"))<0)>${(unitTotalEntry.getValue().get("shortKgSnf"))?if_exists?string("##0.000")}<#else>0.000</#if></fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">${(unitTotalEntry.getValue().get("shortKgSnfAmt"))?if_exists}</fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">${(unitTotalEntry.getValue().get("sourAmt"))?if_exists}</fo:block>										
									</fo:table-cell>									
									<fo:table-cell>										
										<fo:block text-align="right">${(unitTotalEntry.getValue().get("totalAmtRecovery"))?if_exists}</fo:block>										
									</fo:table-cell>
									<fo:table-cell>								
										<fo:block text-align="right">${(unitTotalEntry.getValue().get("netAmtPayable"))?if_exists}</fo:block>		
									</fo:table-cell>
								</fo:table-row>							
							</fo:table-body>
						</fo:table>
					</fo:block>
					</#if>
				</#list>	
				
				<#assign ddAmount =0>
				<#assign vaccine =0>
				<#assign seeded =0>
				<#assign store =0>
				<#assign feed =0>
				<#assign cessOnSale =0>
				<#assign others =0>
				<#assign tip =0>
				<#assign shedMaint =0>
				<#if ddAmountMap?has_content>
					<#assign ddAmount =ddAmountMap.get("amount")>
					<#assign vaccine =ddAmountMap.get("vaccine")>
					<#assign seeded =ddAmountMap.get("seedDed")>
					<#assign store =ddAmountMap.get("stores")>
					<#assign feed =ddAmountMap.get("feedDed")>
					<#assign cessOnSale =ddAmountMap.get("cessOnSale")>
					<#assign vijayRD =ddAmountMap.get("vijayRD")>
					<#assign vijayaLN =ddAmountMap.get("vijayaLN")>
					<#assign MSpares =ddAmountMap.get("MSpares")>
					<#assign MTester =ddAmountMap.get("MTester")>
					<#assign storeA =ddAmountMap.get("storeA")>
					<#assign stationary =ddAmountMap.get("stationary")>
					<#assign others =ddAmountMap.get("others")>
					<#assign tip =ddAmountMap.get("tipAmount")>
					<#assign shedMaint =ddAmountMap.get("shedMaintAmt")>
					<#if allRecoveries?has_content && (allRecoveries =="Y")>
						<#assign tip =tip+MSpares>
					</#if>
					
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
										<fo:block text-align="right">${(ddAmount)}</fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">0.00</fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">${(ddAmountMap.get("shortKgFatAmt"))?if_exists}</fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">0.00</fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">${(ddAmountMap.get("shortKgSnfAmt"))?if_exists}</fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">${(ddAmountMap.get("sourAmt"))?if_exists}</fo:block>										
									</fo:table-cell>									
									<fo:table-cell>										
										<fo:block text-align="right">${(ddAmountMap.get("totalAmtRecovery"))?if_exists}</fo:block>										
									</fo:table-cell>
									<fo:table-cell>								
										<fo:block text-align="right">${(ddAmountMap.get("netAmtPayable"))?if_exists}</fo:block>		
									</fo:table-cell>
								</fo:table-row>							
							</fo:table-body>
						</fo:table>
					</fo:block>
				</#if>	
					<fo:block >---------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		 		  <#if GrandTotalsMap?has_content> 	
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
										<fo:block text-align="right">${(GrandTotalsMap.get("milkValue")+ddAmountMap.get("amount"))}</fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">${GrandTotalsMap.get("shortKgFat")?if_exists?string("##0.000")}</fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">${(GrandTotalsMap.get("shortKgFatAmt"))?if_exists}</fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">${GrandTotalsMap.get("shortKgSnf")?if_exists?string("##0.000")}</fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">${(GrandTotalsMap.get("shortKgSnfAmt"))?if_exists}</fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">${(GrandTotalsMap.get("sourAmt"))?if_exists}</fo:block>										
									</fo:table-cell>									
									<fo:table-cell>										
										<fo:block text-align="right">${(GrandTotalsMap.get("totalAmtRecovery"))?if_exists}</fo:block>										
									</fo:table-cell>
									<fo:table-cell>										
										<fo:block text-align="right">${(GrandTotalsMap.get("netAmtPayable")+ddAmountMap.get("amount"))?if_exists}</fo:block>										
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:block>
				</#if>	
					<fo:block >---------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	 		   		<fo:block linefeed-treatment="preserve" font-size="8pt">&#xA;</fo:block> 
	 		   		<fo:block linefeed-treatment="preserve" font-size="8pt">&#xA;</fo:block> 
	 		   		<fo:block keep-together="always" white-space-collapse="false">${ddAmountMap.get("unitCode")?if_exists} DD OFFICE ACCOUNT DETAILS</fo:block>
	 		   		<fo:block>------------------------------</fo:block>
	 		   		<#assign orderAdjustmentDesc=Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].getShedOrderAdjustmentDescription( dctx,Static["org.ofbiz.base.util.UtilMisc"].toMap("shedId",parameters.shedId)).get("shedAdjustmentDescriptionMap")>
	 		   		<fo:block>
	 		   			<fo:table>
	 		   				<fo:table-column column-width="40pt"/>
	 		   				<fo:table-column column-width="170pt"/>
	 		   				<fo:table-column column-width="170pt"/>
	 		   				<fo:table-column column-width="90pt"/>
	 		   				<fo:table-body>
	 		   					<fo:table-row>
	 		   						<fo:table-cell>
	 		   							<fo:block keep-together="always" white-space-collapse="false">VACCINE   AMT  :</fo:block>
	 		   							<fo:block keep-together="always" white-space-collapse="false">${orderAdjustmentDesc["MILKPROC_SEEDDED"]?if_exists}  </fo:block>
	 		   							<fo:block keep-together="always" white-space-collapse="false">${orderAdjustmentDesc["MILKPROC_STORET"]?if_exists}  </fo:block>
	 		   							<fo:block keep-together="always" white-space-collapse="false">${orderAdjustmentDesc["MILKPROC_STATONRY"]?if_exists}  </fo:block>
	 		   							<fo:block keep-together="always" white-space-collapse="false">${orderAdjustmentDesc["MILKPROC_MSPARES"]?if_exists}  </fo:block>
	 		   							<#if allRecoveries?has_content && (allRecoveries =="Y")>
	 		   								<fo:block keep-together="always" white-space-collapse="false">FEED  AMT     :</fo:block>
	 		   								<!-- <fo:block keep-together="always" white-space-collapse="false">CESS ON SALE  :</fo:block> -->
	 		   								<fo:block keep-together="always" white-space-collapse="false">${orderAdjustmentDesc["MILKPROC_STOREA"]?if_exists}  </fo:block>
	 		   								<fo:block keep-together="always" white-space-collapse="false">${orderAdjustmentDesc["MILKPROC_VIJAYALN"]?if_exists}  </fo:block>
	 		   								<fo:block keep-together="always" white-space-collapse="false">${orderAdjustmentDesc["MILKPROC_VIJAYARD"]?if_exists}  </fo:block>
	 		   								<fo:block keep-together="always" white-space-collapse="false">${orderAdjustmentDesc["MILKPROC_MTESTER"]?if_exists}  </fo:block>
	 		   								
	 		   							</#if>
	 		   							<#if shedMaint !=0>
	 		   								<fo:block keep-together="always" white-space-collapse="false">SHED MAINT AMT :</fo:block>
	 		   							</#if>
	 		   							<fo:block keep-together="always" white-space-collapse="false">${orderAdjustmentDesc["MILKPROC_OTHERDED"]?if_exists}  </fo:block>
	 		   							<fo:block keep-together="always" white-space-collapse="false">TIP       AMT  :</fo:block>
	 		   							<#if ddAmountMap?has_content && ddAmountMap.get("totalAmtRecovery")!=0>
	 		   								<fo:block keep-together="always" white-space-collapse="false">TOT REC AMT :</fo:block>	
	 		   							</#if>
	 		   						</fo:table-cell>
	 		   						<fo:table-cell>
	 		   							<fo:block text-align="right">${Static["java.lang.Math"].round(vaccine)?string("#0.00")}</fo:block>
	 		   							<fo:block text-align="right">${Static["java.lang.Math"].round(seeded)?string("#0.00")}</fo:block>
	 		   							<fo:block text-align="right">${Static["java.lang.Math"].round(store)?string("#0.00")}</fo:block>
	 		   							<fo:block text-align="right">${Static["java.lang.Math"].round(stationary)?string("#0.00")}</fo:block>
	 		   							<fo:block text-align="right">${Static["java.lang.Math"].round(MSpares)?string("#0.00")}</fo:block>
	 		   							<#if allRecoveries?has_content && (allRecoveries =="Y")>
	 		   								<fo:block text-align="right">${Static["java.lang.Math"].round(feed)?string("#0.00")}</fo:block>
	 		   							<!--	<fo:block text-align="right">${Static["java.lang.Math"].round(cessOnSale)?string("#0.00")}</fo:block> -->
	 		   								<fo:block text-align="right">${Static["java.lang.Math"].round(storeA)?string("#0.00")}</fo:block>
	 		   								<fo:block text-align="right">${Static["java.lang.Math"].round(vijayaLN)?string("#0.00")}</fo:block>
	 		   								<fo:block text-align="right">${Static["java.lang.Math"].round(vijayRD)?string("#0.00")}</fo:block>
	 		   								<fo:block text-align="right">${Static["java.lang.Math"].round(MTester)?string("#0.00")}</fo:block>
	 		   								
	 		   							</#if>
	 		   							<#if shedMaint !=0>
	 		   								<fo:block text-align="right">${Static["java.lang.Math"].round(shedMaint)?string("#0.00")}</fo:block>
	 		   							</#if>
	 		   							<fo:block text-align="right">${Static["java.lang.Math"].round(others)?string("#0.00")}</fo:block>
	 		   							<fo:block text-align="right">${Static["java.lang.Math"].round(tip-MSpares)?string("#0.00")}</fo:block>
	 		   							<#if ddAmountMap?has_content && ddAmountMap.get("totalAmtRecovery")!=0>
	 		   									<fo:block text-align="right">${Static["java.lang.Math"].round(ddAmountMap.get("totalAmtRecovery")*(-1))?string("#0.00")}</fo:block>
	 		   							</#if>
	 		   							
	 		   						</fo:table-cell>
	 		   						<#if userCharges!=0 >
	 		   						<fo:table-cell>
	 		   							<fo:block keep-together="always" white-space-collapse="false" text-indent="80pt">TIP DETAILS</fo:block>
	 		   							<fo:block text-indent="40pt">------------------------------</fo:block>
	 		   							<fo:block keep-together="always" white-space-collapse="false" text-indent="60pt">Total Tip    : </fo:block>
	 		   							<fo:block keep-together="always" white-space-collapse="false" text-indent="60pt">User Charges :</fo:block>
	 		   							<fo:block linefeed-treatment="preserve" >.&#xA;</fo:block>
	 		   							<fo:block keep-together="always" white-space-collapse="false" text-indent="60pt">Net TIP</fo:block>
	 		   						</fo:table-cell>
	 		   						<fo:table-cell> 	
	 		   							<fo:block linefeed-treatment="preserve" >.&#xA;</fo:block>
	 		   							<fo:block linefeed-treatment="preserve" >.&#xA;</fo:block>	   							
	 		   							<fo:block text-align="right">${Static["java.lang.Math"].round(tip+userCharges-MSpares)?string("#0.00")}</fo:block>
	 		   							<fo:block text-align="right">${Static["java.lang.Math"].round(userCharges)?string("#0.00")}</fo:block> 
	 		   							<fo:block text-indent="5pt">--------------</fo:block>
	 		   							<fo:block text-align="right">${Static["java.lang.Math"].round(tip-MSpares)?string("#0.00")}</fo:block>
	 		   							<fo:block text-indent="5pt">--------------</fo:block>		   							
	 		   						</fo:table-cell>	 
	 		   						</#if>		   						
	 		   						
	 		   					</fo:table-row>
	 		   					<fo:table-row>
	 		   						<fo:table-cell>
	 		   							<fo:block>------------------------------</fo:block>
	 		   						</fo:table-cell>
	 		   					</fo:table-row>
	 		   					<fo:table-row>
	 		   						<fo:table-cell>
	 		   							<fo:block keep-together="always" white-space-collapse="false">DD OFFICE AMT :</fo:block>
	 		   						</fo:table-cell>
	 		   						<fo:table-cell>
	 		   							<fo:block text-align="right">${Static["java.lang.Math"].round(ddAmountMap.get("netAmtPayable"))?string("##0.00")}</fo:block>
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
		</#if>		
	</fo:root>
</#escape>