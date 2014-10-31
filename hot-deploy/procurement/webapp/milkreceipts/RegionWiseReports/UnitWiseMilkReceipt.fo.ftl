<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-left=".3in" margin-top=".3in">
                <fo:region-body margin-top="1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
    ${setRequestAttribute("OUTPUT_FILENAME", "${shed}-UnitWiseMilkReceipts.txt")}
    <#if errorMessage?has_content>
		<fo:page-sequence master-reference="main">
		   <fo:flow flow-name="xsl-region-body" font-family="Courier,monospae">
		      <fo:block font-size="14pt">
		              ${errorMessage}.
		   	  </fo:block>
		   </fo:flow>
		</fo:page-sequence>        
	<#else>
	<#if unitWiseProductTotalMap?has_content>
		<#assign unitDetailList = unitMap.entrySet()>
   		<#list unitDetailList as unitDetails>
		<fo:page-sequence master-reference="main">
			<fo:static-content flow-name="xsl-region-before">
				<fo:block font-size="5pt" white-space-collapse="false" font-weight="bold">&#160;.  </fo:block>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;                    M.P.F., HYDERABAD MILK RECEIPTS                                 </fo:block>                      
				<fo:block font-size="5pt" white-space-collapse="false" font-weight="bold">&#160;.  </fo:block>
				<#assign facility = delegator.findOne("Facility", {"facilityId" : unitDetails.getKey()}, true)>
				<fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always">                       UNIT NAME  :  ${facility.facilityName?if_exists}  	   PERIOD FROM:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")}   TO   ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block>
				<fo:block font-size="10pt" keep-together="always" white-space-collapse="false" text-align="left">---------------------------------------------------------------------------------------------------</fo:block>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">&#160;   DATE      TANKER    TYPE     QTY-LTS    FAT%   SNF%    CLR      	KGS     	  &#160;&#160;&#160;  	KG-FAT       KG-SNF</fo:block>
				<fo:block font-size="9pt" white-space-collapse="false" text-align="left">---------------------------------------------------------------------------------------------------</fo:block> 
			</fo:static-content>
				<fo:flow flow-name="xsl-region-body" font-family="Courier,monospae"> 
					<fo:block font-family="Courier,monospae">
						<fo:table>
							<fo:table-column column-width="28pt"/>
							<fo:table-column column-width="15pt"/>
							<fo:table-column column-width="18pt"/>
							<fo:table-column column-width="28pt"/>
							<fo:table-column column-width="17pt"/>
							<fo:table-column column-width="19pt"/>
							<fo:table-column column-width="22pt"/>
							<fo:table-column column-width="28pt"/>
							<fo:table-column column-width="29pt"/>
							<fo:table-column column-width="30pt"/>
							<fo:table-body>
								<#assign totQtyLtr = 0>
								<#assign fat = 0>
								<#assign snf = 0>
								<#assign grandTotKgs = 0>
								<#assign grandTotKgFat = 0>
								<#assign grandTotKgSnf = 0>
								<#assign finalMilkDetails = unitDetails.getValue()>
								<#assign rowNumber = 1>
								<#list finalMilkDetails as milkDetail>
									<#if (milkDetail.get('receivedQuantityLtrs'))!=0>
										<fo:table-row>	
											<fo:table-cell>
											<#if (rowNumber>40)> 
													<fo:block font-family="Courier,monospace" font-size="10pt" break-before="page"/>
												<#assign rowNumber=1>		
											</#if>
												<fo:block font-size="4pt" text-align="left" font-weight="bold"><#if milkDetail.get('receiveDate')?exists>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(milkDetail.get('receiveDate'), "dd/MM/yyyy")}</#if></fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block font-size="4pt" text-align="right" font-weight="bold">${milkDetail.get('vehicleId')}</fo:block>
											</fo:table-cell>
												<#assign product = delegator.findOne("Product", {"productId" : milkDetail.get('receivedProductId')}, true)>
											<fo:table-cell>
												<fo:block font-size="3pt" keep-together="always" white-space-collapse="true" text-align="right" font-weight="bold">${product.brandName}</fo:block>
											</fo:table-cell>
												<#assign recQtyLtrs = milkDetail.get('receivedQuantityLtrs')>
												<#if recQtyLtrs?exists>
													<#assign totQtyLtr = totQtyLtr+recQtyLtrs >
												</#if>
											<fo:table-cell>
												<fo:block font-size="4pt" text-align="right" font-weight="bold">${recQtyLtrs?if_exists?string("##0.0")}</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<#assign fat = milkDetail.get('receivedFat')>
												<fo:block font-size="4pt" text-align="right" font-weight="bold">${fat?if_exists?string("##0.0")}0</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<#assign snf = milkDetail.get('receivedSnf')>
												<fo:block font-size="4pt" text-align="right" font-weight="bold">${snf?if_exists?string("##0.00")}</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block font-size="4pt" text-align="right" font-weight="bold">${milkDetail.get('receivedLR')?if_exists?string("##0.00")}</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<#assign recKgs = milkDetail.get('receivedQuantity')>
												<#if recKgs?exists>
													<#assign grandTotKgs = grandTotKgs+recKgs >
												</#if>
												<fo:block font-size="4pt" text-align="right" font-weight="bold">${recKgs?if_exists?string("##0.00")}</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<#assign recKgFat = milkDetail.get('receivedKgFat')>
												<#if recKgFat?exists>
													<#assign grandTotKgFat = grandTotKgFat+recKgFat >
												</#if>
												<fo:block font-size="4pt" text-align="right" font-weight="bold">${recKgFat?if_exists?string("##0.00")}</fo:block>
											</fo:table-cell>
											<fo:table-cell>
											<#assign recKgSnf = milkDetail.get('receivedKgSnf')>
											<#if recKgSnf?exists>
												<#assign grandTotKgSnf= grandTotKgSnf+recKgSnf >
											</#if>
												<fo:block font-size="4pt" text-align="right" font-weight="bold">${recKgSnf?if_exists?string("##0.00")}</fo:block>
											</fo:table-cell>
										</fo:table-row>
										<#assign rowNumber = rowNumber+1>
									</#if>
								</#list>
							</fo:table-body>
						</fo:table>
			     	 </fo:block>
					 <fo:block font-size="6pt" white-space-collapse="false" text-align="left">---------------------------------------------------------------------------------------------------</fo:block>
				   	 <fo:block font-size="8pt">
						 <fo:table>
							<fo:table-column column-width="28pt"/>
							<fo:table-column column-width="15pt"/>
							<fo:table-column column-width="18pt"/>
							<fo:table-column column-width="28pt"/>
							<fo:table-column column-width="17pt"/>
							<fo:table-column column-width="19pt"/>
							<fo:table-column column-width="22pt"/>
							<fo:table-column column-width="27pt"/>
							<fo:table-column column-width="29pt"/>
							<fo:table-column column-width="30pt"/>
								<fo:table-body> 
									<#assign unitProductTotalMap = unitWiseProductTotalMap[unitDetails.getKey()]>
									<#assign unitProductTotalList =  unitProductTotalMap.entrySet()>
									<#list unitProductTotalList as productTotal>
										<fo:table-row>
											<#assign product = delegator.findOne("Product", {"productId" : productTotal.getKey()}, true)>
											<fo:table-cell>
												<fo:block font-size="4pt" text-align="left" font-weight="bold">${product.brandName}</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block font-size="4pt" text-align="right" font-weight="bold"></fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block font-size="4pt" text-align="right" font-weight="bold"></fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block font-size="4pt" text-align="right" font-weight="bold">${productTotal.getValue().get("receivedQuantityLtrs")?if_exists?if_exists?string("##0.0")}</fo:block>
											</fo:table-cell>
											<#assign totKgFat=0>
											<#if (productTotal.getValue())?has_content  && (productTotal.getValue().get("receivedQuantity"))!=0>
												<#assign totKgFat=(((productTotal.getValue().get("receivedKgFat"))*100)/(productTotal.getValue().get("receivedQuantity")))>
											</#if>
											<fo:table-cell>
												<fo:block font-size="4pt" text-align="right" font-weight="bold"><#if (productTotal.getValue().get("receivedQuantity")) !=0>${(totKgFat)?if_exists?string("##0.0")}0<#else>0.00</#if></fo:block> 
											</fo:table-cell>
											<#assign totKgSnf=0>
											<#if (productTotal.getValue())?has_content  && (productTotal.getValue().get("receivedQuantity"))!=0>
												<#assign totKgSnf=(((productTotal.getValue().get("receivedKgSnf"))*100)/(productTotal.getValue().get("receivedQuantity")))>
											</#if>
											<fo:table-cell>
												<fo:block font-size="4pt" text-align="right" font-weight="bold"><#if (productTotal.getValue().get("receivedQuantity")) !=0>${(totKgSnf)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block> 
											</fo:table-cell>
											<fo:table-cell>
												<fo:block font-size="4pt" text-align="right" font-weight="bold"></fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block font-size="4pt" text-align="right" font-weight="bold">${productTotal.getValue().get("receivedQuantity")?if_exists?if_exists?string("##0.00")}</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block font-size="4pt" text-align="right" font-weight="bold">${productTotal.getValue().get("receivedKgFat")?if_exists?if_exists?string("##0.00")}</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block font-size="4pt" text-align="right" font-weight="bold">${productTotal.getValue().get("receivedKgSnf")?if_exists?if_exists?string("##0.00")}</fo:block>
											</fo:table-cell>
										</fo:table-row>	
									</#list>
							</fo:table-body>
						</fo:table>
					</fo:block>
					<fo:block font-size="6pt" white-space-collapse="false" text-align="left">---------------------------------------------------------------------------------------------------</fo:block>
					<fo:block font-size="8pt">
					<fo:table>
						<fo:table-column column-width="28pt"/>
						<fo:table-column column-width="15pt"/>
						<fo:table-column column-width="18pt"/>
						<fo:table-column column-width="28pt"/>
						<fo:table-column column-width="17pt"/>
						<fo:table-column column-width="19pt"/>
						<fo:table-column column-width="22pt"/>
						<fo:table-column column-width="27pt"/>
						<fo:table-column column-width="29pt"/>
						<fo:table-column column-width="30pt"/>
						<fo:table-body> 
							<fo:table-row>
								<fo:table-cell>
									<fo:block font-size="4pt" text-align="left" font-weight="bold">GRAND TOT:</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block font-size="4pt" text-align="right" font-weight="bold"></fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block font-size="4pt" text-align="right" font-weight="bold"></fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block font-size="4pt" text-align="right" font-weight="bold">${totQtyLtr?if_exists?if_exists?string("##0.0")}</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block font-size="4pt" text-align="right" font-weight="bold"><#if grandTotKgs !=0>${((grandTotKgFat*100)/grandTotKgs)?if_exists?string("##0.0")}0<#else>0.00</#if></fo:block> 
								</fo:table-cell>
								<fo:table-cell>
									<fo:block font-size="4pt" text-align="right" font-weight="bold"><#if grandTotKgs !=0>${((grandTotKgSnf*100)/grandTotKgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block> 
								</fo:table-cell>
								<fo:table-cell>
									<fo:block font-size="4pt" text-align="right" font-weight="bold"></fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block font-size="4pt" text-align="right" font-weight="bold">${grandTotKgs?if_exists?string("##0.00")}</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block font-size="4pt" text-align="right" font-weight="bold">${grandTotKgFat?if_exists?string("##0.00")}</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block font-size="4pt" text-align="right" font-weight="bold">${grandTotKgSnf?if_exists?string("##0.00")}</fo:block>
								</fo:table-cell>
							</fo:table-row>	
						</fo:table-body>
					</fo:table>
					</fo:block>
					<fo:block font-size="6pt" white-space-collapse="false" text-align="left">---------------------------------------------------------------------------------------------------</fo:block>
				</fo:flow>	
			</fo:page-sequence>
		</#list>
	<#else>
		<fo:page-sequence master-reference="main">
	    	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospae">
	       		 <fo:block font-size="14pt">
	            	${uiLabelMap.OrderNoOrderFound}.
	       		 </fo:block>
	    	</fo:flow>
		</fo:page-sequence>	
	</#if>
</#if>
	</fo:root>
</#escape>