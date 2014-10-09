<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-top=".2in">
                <fo:region-body margin-top=".8in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "ConsolidatedReport.pdf")}
<#if errorMessage?has_content>
<fo:page-sequence master-reference="main">
   <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
      <fo:block font-size="14pt">
              ${errorMessage}.
   	  </fo:block>
   </fo:flow>
</fo:page-sequence>        
<#else>  
	<#if distDetailsMap?has_content>        
		<fo:page-sequence master-reference="main">
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="8pt">
				<fo:block text-align="left" white-space-collapse="false" keep-together="always">FEDERATION ABSTRACT              CONSOLIDATED REPORT OF THE MILK PROCUREMENT, KG-FAT, KG-SNF, TOTAL SOLIDS,KGFAT-RATE, AVG-LTR RATE PERIOD FROM : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "MMM d, yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "MMM d, yyyy")}</fo:block>
				<fo:block font-size="5pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">.                                                                    BUFFALO MILK                                                                                      COW MILK                                                                                                      TOTAL MIXED MILK                   </fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="6pt"> &#160;                   ---------------------------------------------------------------------------------    -------------------------------------------------------------------------------------------    --------------------------------------------------------------------------------------------------</fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="6pt">SHED   NAME OF THE      QTY      QTY       KG        KG        TOTAL      AVG   AVG    KG-FAT    LTR      QTY       QTY       KG       KG       KG          TOTAL      AVG    AVG    SOLIDS    LTR         QTY      QTY      KG        KG      TOTAL    TOTAL       AVG-QTY   AVG   AVG   LTR    (RATIO%)     </fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="6pt">CODE    MILK SHED      	KGS      LTS       FAT       SNF       AMOUNT     FAT   SNF     RATE     RATE     KGS       LTS       FAT      SNF     SOLIDS      AMOUNT      FAT    SNF     RATE     RATE        KGS      LTS      FAT       SNF     SOLIDS   (AMT+OPC)   LTRS      FAT   SNF   RATE   BM     CM    </fo:block>
				<fo:block font-size="5pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">  
			<#assign avgTotalLtrs = 0>
			<#assign distTotals = distDetailsMap.entrySet()>  
			<#assign sNo = 0>   				   
						<fo:block font-family="Courier,monospace" font-size="6pt">
						 	<fo:table>
           						<fo:table-column column-width="25pt"/>
           						<fo:table-column column-width="40pt"/>
           						<fo:table-column column-width="35pt"/>
           						<fo:table-column column-width="35pt"/>
           						<fo:table-column column-width="35pt"/>
           						<fo:table-column column-width="35pt"/>
           						<fo:table-column column-width="45pt"/>
           						<fo:table-column column-width="25pt"/>
           						<fo:table-column column-width="25pt"/>
           						<fo:table-column column-width="30pt"/>
           						<fo:table-column column-width="30pt"/>
           						<fo:table-column column-width="35pt"/>
           						<fo:table-column column-width="35pt"/>
           						<fo:table-column column-width="35pt"/>
           						<fo:table-column column-width="35pt"/>
           						<fo:table-column column-width="35pt"/>
           						<fo:table-column column-width="50pt"/>
           						<fo:table-column column-width="25pt"/>
           						<fo:table-column column-width="30pt"/>
           						<fo:table-column column-width="33pt"/>
           						<fo:table-column column-width="33pt"/>
           						<fo:table-column column-width="40pt"/>
           						<fo:table-column column-width="35pt"/>
           						<fo:table-column column-width="32pt"/>
           						<fo:table-column column-width="32pt"/>
           						<fo:table-column column-width="40pt"/>
           						<fo:table-column column-width="45pt"/>
           						<fo:table-column column-width="30pt"/>
           						<fo:table-column column-width="23pt"/>
           						<fo:table-column column-width="23pt"/>
           						<fo:table-column column-width="23pt"/>
           						<fo:table-column column-width="23pt"/>
           						<fo:table-column column-width="23pt"/>
           						<fo:table-column column-width="20pt"/>
           						<fo:table-column column-width="30pt"/>
           						<fo:table-body> 
           						 
           							<#list distTotals as eachDist>  
           							<#assign eachDistDetails = eachDist.getValue()> 
           								<#if eachDist.getKey() == "GrandTotals"> 
       									<fo:table-row>
           									<fo:table-cell>
       											<fo:block font-size="6pt">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
       										</fo:table-cell>
										</fo:table-row>
           								</#if>            						
	           							<fo:table-row>
	           								<#assign sNo = sNo + 1>
	           								<#if eachDist.getKey() == "GrandTotals">  
	           									<fo:table-cell>
	           										<fo:block keep-together="always" font-size="6pt" text-align="right"></fo:block>
	           									</fo:table-cell>
		           							<#else>
		           								<fo:table-cell>
	           										<fo:block keep-together="always" font-size="6pt" text-align="left">${sNo}</fo:block>
	           									</fo:table-cell>
		           							</#if> 
	           								<fo:table-cell>
	           									<fo:block keep-together="always" font-size="6pt" text-align="left">${eachDist.getKey()}</fo:block>
	           								</fo:table-cell>
	           								<#assign totKgFat = 0>
	           								<#assign totKgSnf = 0>
	           								<#assign opCost = 0>
	           								<#assign bmLtr = 0>
	           								<#assign cmLtr = 0>
		           							 <#list procurementProductList as procProducts>
		           							 <#assign Kgs = (eachDistDetails.get(procProducts.brandName+"QtyKgs"))>
		           							 <#assign Ltrs = (eachDistDetails.get(procProducts.brandName+"QtyLtrs"))>
											 <#assign KgFat = (eachDistDetails.get(procProducts.brandName+"kgFat"))>
											 <#assign KgSnf = (eachDistDetails.get(procProducts.brandName+"kgSnf"))>
											 <#assign netAmt = (eachDistDetails.get(procProducts.brandName+"netAmt"))>
											 <#assign tipAmt = (eachDistDetails.get(procProducts.brandName+"tipAmt"))>
											 <#assign solids = (eachDistDetails.get(procProducts.brandName+"solids"))>
											 <#assign totKgFat = totKgFat+KgFat>
	           								 <#assign totKgSnf = totKgSnf+KgSnf> 
		           								<fo:table-cell>
		           									<fo:block keep-together="always" font-size="6pt" text-align="right">${Kgs?if_exists?string("##0.0")}</fo:block>
		           								</fo:table-cell>
		           								<fo:table-cell>
		           									<fo:block keep-together="always" font-size="6pt" text-align="right">${Ltrs?if_exists?string("##0.0")}</fo:block>
		           								</fo:table-cell>
		           								<fo:table-cell>
		           									<fo:block keep-together="always" font-size="6pt" text-align="right">${KgFat?if_exists?string("##0.0")}</fo:block>
		           								</fo:table-cell>
		           								<fo:table-cell>
		           									<fo:block keep-together="always" font-size="6pt" text-align="right">${KgSnf?if_exists?string("##0.0")}</fo:block>
		           								</fo:table-cell>
		           								<#if (procProducts.brandName) == "CM">  
		           									<fo:table-cell>
	           											<fo:block keep-together="always" font-size="6pt" text-align="right">${solids?if_exists?string("##0.0")}</fo:block>
	           										</fo:table-cell>
           										</#if>
		           								<fo:table-cell>
		           									<fo:block keep-together="always" font-size="6pt" text-align="right">${(netAmt+tipAmt)?if_exists}</fo:block>
		           								</fo:table-cell>
		           								<fo:table-cell>
		           									<fo:block keep-together="always" font-size="6pt" text-align="right"><#if Kgs !=0>${((KgFat*100)/Kgs)?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
		           								</fo:table-cell>
		           								<fo:table-cell>
		           									<fo:block keep-together="always" font-size="6pt" text-align="right"><#if Kgs !=0>${((KgSnf*100)/Kgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
		           								</fo:table-cell>
		           								<#if (procProducts.brandName) == "CM">  
		           									<fo:table-cell>
	           											<fo:block keep-together="always" font-size="6pt" text-align="right"><#if (solids>0)>${(netAmt/solids)?if_exists?string("##0.00")}<#else>${netAmt?if_exists}</#if></fo:block>
	           										</fo:table-cell>
	           										<#assign cmLtr=Ltrs>
	           									<#else>	
	           										<fo:table-cell>
		           										<fo:block keep-together="always" font-size="6pt" text-align="right"><#if KgFat !=0>${(netAmt/KgFat)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
		           									</fo:table-cell>
		           									<#assign bmLtr=Ltrs>
           										</#if>
		           								<fo:table-cell>
		           									<fo:block keep-together="always" font-size="6pt" text-align="right"><#if Ltrs !=0>${(netAmt/Ltrs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
		           								</fo:table-cell>
		           								
	           								</#list>
	           									<#assign totQtyKgs = eachDistDetails.get("TOTQtyKgs")>
	           									<fo:table-cell>
		           									<fo:block keep-together="always" font-size="6pt" text-align="right">${totQtyKgs?if_exists?string("##0.0")}</fo:block>
		           								</fo:table-cell>
		           								<#assign totQtyLtrs = eachDistDetails.get("TOTQtyLtrs")>
		           								<fo:table-cell>
		           									<fo:block keep-together="always" font-size="6pt" text-align="right">${totQtyLtrs?if_exists?string("##0.0")}</fo:block>
		           								</fo:table-cell>
		           								<fo:table-cell>
		           									<fo:block keep-together="always" font-size="6pt" text-align="right">${eachDistDetails.get("TOTkgFat")?if_exists?string("##0.0")}</fo:block>
		           								</fo:table-cell>
		           								<fo:table-cell>
		           									<fo:block keep-together="always" font-size="6pt" text-align="right">${eachDistDetails.get("TOTkgSnf")?if_exists?string("##0.0")}</fo:block>
		           								</fo:table-cell>
		           								<fo:table-cell>
		           									<fo:block keep-together="always" font-size="6pt" text-align="right">${(totKgFat+totKgSnf)?if_exists?string("##0.0")}</fo:block>
		           								</fo:table-cell>
		           								<#assign opCost = (eachDistDetails.get("TOTopCost"))>
		           								<#assign tipAmt = (eachDistDetails.get("TOTtipAmt"))>
		           								<fo:table-cell>
		           									<fo:block keep-together="always" font-size="6pt" text-align="right">${(eachDistDetails.get("TOTnetAmt")+(opCost)+(tipAmt))?if_exists?string("##0.00")}</fo:block>
		           								</fo:table-cell>
		           								<#if eachDist.getKey() == "GrandTotals">  
		           									<fo:table-cell>
		           										<fo:block keep-together="always" font-size="6pt" text-align="right">${avgTotalLtrs?if_exists?string("##0")}</fo:block>
		           									</fo:table-cell>
		           								 <#else>
		           								 	<#assign totalQty=Static["java.lang.Math"].round(totQtyLtrs/maxIntervalDays)>
		           								 	<#assign avgTotalLtrs = avgTotalLtrs + (totalQty)>
		           								 	<fo:table-cell>
		           										<fo:block keep-together="always" font-size="6pt" text-align="right">${(totalQty)?if_exists?string("##0")}</fo:block>
		           									</fo:table-cell>
		           								</#if> 
		           								
		           								<#assign totAvgFat=0>
		           								<#if eachDistDetails.get("TOTkgFat")!=0>
		           								<#assign totAvgFat = (((eachDistDetails.get("TOTkgFat"))*100)/(totQtyKgs))>
		           								</#if>
		           								<fo:table-cell>
		           									<fo:block keep-together="always" font-size="6pt" text-align="right">${totAvgFat?if_exists?string("##0.0")}</fo:block>
		           								</fo:table-cell>
		           								<#assign totAvgSnf=0>
		           								<#if eachDistDetails.get("TOTkgSnf")!=0>
		           								<#assign totAvgSnf = (((eachDistDetails.get("TOTkgSnf"))*100)/(totQtyLtrs))>
		           								</#if>
		           								<fo:table-cell>
		           									<fo:block keep-together="always" font-size="6pt" text-align="right">${totAvgSnf?if_exists?string("##0.0")}</fo:block>
		           								</fo:table-cell>
		           								<fo:table-cell>
		           									<fo:block keep-together="always" font-size="6pt" text-align="right"><#if totQtyLtrs !=0>${(eachDistDetails.get("TOTnetAmt")/(totQtyLtrs))?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
		           								</fo:table-cell>
		           								<#assign cmRatio =0>
		           								<#assign bmRatio =0>
		           								<#if (procProducts.brandName) == "CM">  
		           								<#assign cmRatio = (((cmLtr)*100)/(totQtyLtrs))>
		           								</#if>
		           								<#if (procProducts.brandName) == "BM">  
		           								<#assign bmRatio = (((bmLtr)*100)/(totQtyLtrs))>
		           								</#if>
		           								<fo:table-cell>
		           									<fo:block keep-together="always" font-size="6pt" text-align="right"><#if totQtyLtrs != 0 >${((bmLtr*100)/totQtyLtrs)?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
		           								</fo:table-cell>
		           								<fo:table-cell>
		           									<fo:block keep-together="always" font-size="6pt" text-align="right"><#if totQtyLtrs != 0 >${((cmLtr*100)/totQtyLtrs)?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
		           								</fo:table-cell>
										</fo:table-row>  
									</#list>                 				
							</fo:table-body>
			 			</fo:table>
					</fo:block>
    			</fo:flow>		
   			</fo:page-sequence>	
			</#if>
		</#if>
</fo:root>
</#escape>