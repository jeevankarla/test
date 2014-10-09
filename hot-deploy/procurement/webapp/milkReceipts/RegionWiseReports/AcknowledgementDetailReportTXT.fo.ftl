<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-left=".3in" margin-top=".3in">
                <fo:region-body margin-top="0.8in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "UnionsOrPrivateMilkBills.txt")}
        <#if errorMessage?has_content>
		<fo:page-sequence master-reference="main">
		   <fo:flow flow-name="xsl-region-body" font-family="Courier,monospae">
		      <fo:block font-size="14pt">
		              ${errorMessage}.
		   	  </fo:block>
		   </fo:flow>
		</fo:page-sequence>        
		<#else>
		 <#if finalBillingList?has_content>
			<fo:page-sequence master-reference="main">
				<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospae">
					<#assign facility = delegator.findOne("Facility", {"facilityId" : parameters.unitId}, true)>
					<#assign product = delegator.findOne("Product", {"productId" : parameters.productId}, true)>
					<fo:block text-align="left" white-space-collapse="false" font-size="6pt" keep-together="always">&#160; UNIT NAME  :  ${facility.getString("facilityName")?if_exists}                MILK BILLS        ${product.brandName?if_exists}                       </fo:block>
					<fo:block text-align="left" white-space-collapse="false" font-size="6pt" keep-together="always">&#160; PERIOD FROM   :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "MMM d, yyyy")}   TO   ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "MMM d, yyyy")}</fo:block>
					<fo:block  white-space-collapse="false" font-size="6pt">---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
					<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt" >DATE   			TANKER 	 |           ${reportTypeStr}   DETAILS  	 	 			 							| RATE PER |      &#160;|     OPC   &#160;|          |        PREMIUM / DEDUCTION ON     &#160;&#160;&#160;&#160;|           </fo:block>
					<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">&#160;      			NO    	 |------------------------------------------------------| ${uomTypeStr}   | MILK   |   PER    |OPERATING | --------------------------------------|&#160;&#160;&#160;&#160;  NET  </fo:block>
					<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">&#160;		   				   					|QTY-LTS  	 QTY-KGS   FAT%   	SNF%  	KG-FAT    KG-SNF  &#160;| RS  	   |  AMOUNT  |  LTR    |  CHARGES  &#160;| 	 FAT%   	     SNF%       TOTAL      |    AMOUNT</fo:block>
					<fo:block  white-space-collapse="false" font-size="6pt">---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block> 
				</fo:static-content>
					<fo:flow flow-name="xsl-region-body" font-family="Courier,monospae"> 
						<fo:block font-family="Courier,monospae" font-size="4pt">
						<fo:table>
							<fo:table-column column-width="20pt"/>
							<fo:table-column column-width="21pt"/>
							<fo:table-column column-width="29pt"/>
							<fo:table-column column-width="30pt"/>
							<fo:table-column column-width="28pt"/>
							<fo:table-column column-width="24pt"/>	
							<fo:table-column column-width="24pt"/>
							<fo:table-column column-width="24pt"/>
							<fo:table-column column-width="18pt"/>
							<fo:table-column column-width="64pt"/>
							<fo:table-column column-width="20pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="38pt"/>
							<fo:table-column column-width="38pt"/>
							<fo:table-column column-width="46pt"/>
							<fo:table-body>
								<#assign totQtyLtrs = 0>
								<#assign totQtyKgs = 0>
								<#assign totFat = 0>
								<#assign totSnf = 0>
								<#assign totKgFat = 0>
								<#assign totKgSnf = 0>
								<#assign totMilkAmount = 0>
								<#assign totOpCharges = 0>
								<#assign totFatPremium = 0>
								<#assign totSnfPremium = 0>
								<#assign totPremium = 0>
								<#assign totNetAmount = 0>
           						<#assign dayCount = 0>
								<#list finalBillingList as milkDetail> 
									<#assign productRate = 0>
									<#assign milkAmount = 0>
									<#assign fatPremium =0>
									<#assign snfPremium = 0>
									<#assign netAmount = 0>
									<#assign recQtyLtrs = 0>
									<#assign recKgs = 0>
									<#assign fat = 0>
									<#assign snf = 0>
									<#assign recKgFat =0>
									<#assign recKgSnf =0>
									<#assign opCharges = 0>
									
								<#assign recQtyLtrs = milkDetail.get('receivedQuantityLtrs')>
								<#if reportTypeStr =="DISPATCH       " >
									<#assign recQtyLtrs = milkDetail.get('quantityLtrs')>
								</#if>
								<#if recQtyLtrs?has_content && recQtyLtrs!=0 >
								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="4pt" text-align="left" font-weight="bold"><#if milkDetail.get('receiveDate')?exists>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(milkDetail.get('receiveDate'), "dd/MM/yyyy")}</#if></fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="4pt" text-align="right" font-weight="bold">${milkDetail.get('vehicleId')}</fo:block>
									</fo:table-cell>
									<#if recQtyLtrs?exists>
										<#assign totQtyLtrs = totQtyLtrs+recQtyLtrs >
									</#if>
									<fo:table-cell>
										<fo:block font-size="4pt" text-align="right" font-weight="bold">${recQtyLtrs?if_exists?string("##0.00")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
									<#assign recKgs = milkDetail.get('receivedQuantity')>
									<#if reportTypeStr =="DISPATCH       " >
										<#assign recKgs = milkDetail.get('quantity')>
									</#if>
									<#if recKgs?exists>
										<#assign totQtyKgs = totQtyKgs+recKgs >
									</#if>
									<fo:block font-size="4pt" text-align="right" font-weight="bold">${recKgs?if_exists?string("##0.00")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
									<#assign fat = milkDetail.get('receivedFat')>
									<#if reportTypeStr =="DISPATCH       " >
										<#assign fat = milkDetail.get('fat')>
									</#if>
										<fo:block font-size="4pt" text-align="right" font-weight="bold">${fat?if_exists?string("##0.00")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
									<#assign snf = milkDetail.get('receivedSnf')>
									<#if reportTypeStr =="DISPATCH       " >
										<#assign snf = milkDetail.get('snf')>
									</#if>
										<fo:block font-size="4pt" text-align="right" font-weight="bold">${snf?if_exists?string("##0.00")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
									<#assign recKgFat = milkDetail.get('receivedKgFat')>
									<#if reportTypeStr =="DISPATCH       " >
										<#assign recKgFat = milkDetail.get('sendKgFat')>
									</#if>
									<#if recKgFat?exists>
										<#assign totKgFat = totKgFat+recKgFat >
									</#if>
									<fo:block font-size="4pt" text-align="right" font-weight="bold">${recKgFat?if_exists?string("##0.00")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
									<#assign recKgSnf = milkDetail.get('receivedKgSnf')>
									<#if reportTypeStr =="DISPATCH       " >
										<#assign recKgSnf = milkDetail.get('sendKgSnf')>
									</#if>
									
									<#if recKgSnf?exists>
										<#assign totKgSnf= totKgSnf+recKgSnf >
									</#if>
									<fo:block font-size="4pt" text-align="right" font-weight="bold">${recKgSnf?if_exists?string("##0.00")}</fo:block>
									</fo:table-cell>
									<#assign productRate = 0>									                								                     								
									<#assign productRate = milkDetail.get("defaultRate")>               								                     								
									<fo:table-cell>
										 <fo:block font-size="4pt" text-align="right">${(productRate)?if_exists?string("##0.00")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
									<#assign milkAmount = 0>
									<#assign milkAmount = milkDetail.get("milkAmount")>
									<#if milkAmount?exists>
										<#assign totMilkAmount = totMilkAmount+milkAmount >
									</#if>
										<fo:block font-size="4pt" text-align="right" font-weight="bold">${(milkAmount)?if_exists?string("##0.00")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="4pt" text-align="right" font-weight="bold">${(opCost)?if_exists?string("##0.00")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
									<#assign opCharges = (recQtyLtrs*opCost)>
									<#if opCharges?exists>
										<#assign totOpCharges = totOpCharges+opCharges >
									</#if>
										<fo:block font-size="4pt" text-align="right" font-weight="bold">${(opCharges)?if_exists?string("##0.00")}</fo:block>
									</fo:table-cell>
									<#assign fatPremium = (Static["java.math.BigDecimal"].ZERO)>
									<#assign fatPremium = milkDetail.get("fatPremium")>
									<fo:table-cell>
										<#if fatPremium?exists>
											<#assign totFatPremium = totFatPremium+fatPremium>
										</#if>
										
										 <fo:block font-size="4pt" text-align="right">${(fatPremium)?if_exists?string("##0.00")}</fo:block>
										
									</fo:table-cell>
									<#assign snfPremium = (Static["java.math.BigDecimal"].ZERO)>
									<#assign snfPremium = milkDetail.get("snfPremium")>
									<fo:table-cell>
										<#if snfPremium?exists>
											<#assign totSnfPremium = totSnfPremium+snfPremium>
										</#if>
										
										 <fo:block font-size="4pt" text-align="right">${(snfPremium)?if_exists?string("##0.00")} </fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="4pt" text-align="right" font-weight="bold">${(fatPremium+snfPremium)?if_exists?string("##0.00")}</fo:block>
									</fo:table-cell>
									
									<fo:table-cell>
									<#assign netAmount = (milkAmount+opCharges+fatPremium+snfPremium)>
									<#if netAmount?exists>
										<#assign totNetAmount = totNetAmount+netAmount >
									</#if>
										<fo:block font-size="4pt" text-align="right" font-weight="bold">${(netAmount)?if_exists?string("##0.00")}</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<#assign dayCount= dayCount+1>
								<#if (dayCount == 50)> 
			        				<#assign dayCount = 0>		          				
			           				<fo:table-row>
			        					<fo:table-cell>	          				
			           						<fo:block font-family="Courier,monospace" font-size="6pt" break-before="page"/>     
			           					</fo:table-cell>
									</fo:table-row>           				
			           			</#if>
								</#if>
									</#list>
									<fo:table-row>
										<fo:table-cell>
											<fo:block  white-space-collapse="false" font-size="6pt">---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
										</fo:table-cell>
									</fo:table-row>
								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="4pt" text-align="left" keep-together="always" font-weight="bold">GRAND TOTAL:</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="4pt" text-align="right" font-weight="bold">&#160;</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="4pt" text-align="right" font-weight="bold">${totQtyLtrs?if_exists?if_exists?string("##0.00")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="4pt" text-align="right" font-weight="bold">${totQtyKgs?if_exists?string("##0.00")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="4pt" text-align="right" font-weight="bold"><#if totQtyKgs !=0>${((totKgFat*100)/totQtyKgs)?if_exists?string("##0.00")}<#else>0.0</#if></fo:block> 
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="4pt" text-align="right" font-weight="bold"><#if totQtyKgs !=0>${((totKgSnf*100)/totQtyKgs)?if_exists?string("##0.00")}<#else>0.0</#if></fo:block> 
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="4pt" text-align="right" font-weight="bold">${totKgFat?if_exists?string("##0.00")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="4pt" text-align="right" font-weight="bold">${totKgSnf?if_exists?string("##0.00")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="4pt" text-align="right" font-weight="bold">&#160;</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="4pt" text-align="right" font-weight="bold">${totMilkAmount?if_exists?string("##0.00")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="4pt" text-align="right" font-weight="bold">&#160;</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="4pt" text-align="right" font-weight="bold">${(totOpCharges)?if_exists?string("##0.00")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="4pt" text-align="right" font-weight="bold">${(totFatPremium)?if_exists?string("##0.00")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="4pt" text-align="right" font-weight="bold">${(totSnfPremium)?if_exists?string("##0.00")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="4pt" text-align="right" font-weight="bold">${(totSnfPremium+totFatPremium)?if_exists?string("##0.00")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="4pt" text-align="right" font-weight="bold">${(totNetAmount)?if_exists?string("##0.00")}</fo:block>
									</fo:table-cell>
							</fo:table-row>	
									<fo:table-row>
										<fo:table-cell>
											<fo:block  white-space-collapse="false" font-size="6pt">---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
										</fo:table-cell>
									</fo:table-row>
							</fo:table-body>
						</fo:table>
			     	 </fo:block>
		</fo:flow>	
		</fo:page-sequence>
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