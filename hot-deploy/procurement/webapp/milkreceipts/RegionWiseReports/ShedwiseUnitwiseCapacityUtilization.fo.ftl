<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="10in" page-width="12in" margin-top=".5in">
                <fo:region-body margin-top="1.2in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "ShedwiseUnitwiseCapacity.txt")}
		<#if errorMessage?has_content>
			<fo:page-sequence master-reference="main">
			   <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
			      <fo:block font-size="14pt">
			              ${errorMessage}.
			   	  </fo:block>
			   </fo:flow>
			</fo:page-sequence>        
		<#else>         
     		<#if finalShedMap?has_content>  
				<fo:page-sequence master-reference="main">
					<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="8pt">
						<fo:block font-size="5pt">VST_ASCII-015 VST_ASCII-027VST_ASCII-077</fo:block>
					 	<fo:block text-align="left" white-space-collapse="false" keep-together="always">.                  SHED-WISE,UNIT-WISE TOTAL AND AVERAGE PROCUREMENT, DIFFERENCE AND %GE VARIATION, CAPACITY UTILISATION</fo:block>
					 	<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;                  </fo:block>
					 	<fo:block text-align="left" white-space-collapse="false" keep-together="always">LAST YEAR PERIOD FROM   : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(prevDateStart, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(prevDateEnd, "dd/MM/yyyy")}              DAIRY'S,MCC'S,BCU'S ANALYSIS</fo:block>
					    <fo:block text-align="left" white-space-collapse="false" keep-together="always">CURRENT YEAR PERIOD FROM: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block>
						<fo:block font-size="7pt">----------------------|-----------------------|----------------------|-------------------------|--------------------------|-------------------------|</fo:block>
						<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">MILK SHED /           |         LAST YEAR    |        CURRENT YEAR   |   COMPARING OVER L-YEAR  |        LAST YEAR        |      CURRENT YEAR       |</fo:block>
						<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">.                     |-----------------------|----------------------|-------------------------|------------|-------------|------------|------------| </fo:block>
						<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">MCC/DAIRY/BCU         |    TOTAL       AVG   |    TOTAL       AVG    |   DIFRNCE  |     %GE    |   CAPACITY  | %GE.CAP    | CAPACITY   | %GE.CAP    |  </fo:block>
						<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">.                     |    LTS       PER DAY |    LTS        PER DAY  |   AVG LTS|   VARIATION |   LPD      | UTILISATION |     LPD    | UTILISATION| </fo:block>
						<fo:block font-size="7pt">----------------------|-----------------------|----------------------|-------------------------|--------------------------|-------------------------|</fo:block> 
					</fo:static-content>
					<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace"> 
						<fo:block font-family="Courier,monospace">
						 	<fo:table>
						 		<#assign noOfLines=0>
						 		<#assign grandTotPrevLtrs=0>
								<#assign grandTotPrevAvg=0>
								<#assign grandTotCurrLtrs=0>
								<#assign grandTotCurrAvg=0>
								<#assign grandTotAvgDiff=0>
								<#assign grandtotPrevCapacity=0>
								<#assign grandtotCurrCapacity=0>
								<#assign grandtotGEVariation=0>
								<#assign grandtotPrevUtilization=0>
								<#assign grandtotCurrUtilization=0>
						 		<fo:table-column column-width="55pt"/>
						 		<fo:table-column column-width="35pt"/>
						 		<fo:table-column column-width="35pt"/>
						 		<fo:table-column column-width="40pt"/>
						 		<fo:table-column column-width="35pt"/>
						 		<fo:table-column column-width="40pt"/>
						 		<fo:table-column column-width="35pt"/>
						 		<fo:table-column column-width="40pt"/>
						 		<fo:table-column column-width="40pt"/>
						 		<fo:table-column column-width="40pt"/>
						 		<fo:table-column column-width="40pt"/>
		   						<fo:table-body>
		   							<#assign shedValues=finalShedMap.entrySet()>
		   							<#list shedValues as shedDetails>
		   								<#assign totPrevLtrs=0>
		   								<#assign totCurrLtrs=0>
		   								<#assign totPrevAvg=0>
		   								<#assign totCurrAvg=0>
		   								<#assign totAvgDiff=0>
		   								<#assign totGEVariation=0>
		   								<#assign totCurrCapacity=0>
		   								<#assign totPrevCapacity=0>
		   								<#assign totPrevUtilization=0>
		   								<#assign totCurrUtilization=0>
		   								<#assign facility = delegator.findOne("Facility", {"facilityId" : shedDetails.getKey()}, true)>
		   								<#assign shedName = (facility.facilityName).toUpperCase()>
							           	<#assign shedName = shedName.substring(0,shedName.indexOf("MILK SHED"))>
							           	<fo:table-row>
											<fo:table-cell>
												<fo:block keep-together="always" text-align="left" font-size="5pt"> ${shedName}</fo:block>
											</fo:table-cell>
										</fo:table-row>
										<#assign noOfLines=noOfLines+1>
										<#if (noOfLines >50)>
		   									<#assign noOfLines =0>
		   									<fo:table-row>
		       									<fo:table-cell>
		       										<fo:block page-break-after="always"></fo:block>
		       									</fo:table-cell>
		   									</fo:table-row>
										</#if>
										<fo:table-row>
											<fo:table-cell>
												<fo:block keep-together="always" text-align="left" font-size="5pt"> &#160;</fo:block>
											</fo:table-cell>
										</fo:table-row>
										<#assign noOfLines=noOfLines+1>
										<#if (noOfLines >50)>
		   									<#assign noOfLines =0>
		   									<fo:table-row>
		       									<fo:table-cell>
		       										<fo:block page-break-after="always"></fo:block>
		       									</fo:table-cell>
		   									</fo:table-row>
										</#if>
										<#assign noOfLines=noOfLines+1>
			   							<#assign unitValues=shedDetails.getValue().entrySet()>
										<#list unitValues as unitDetails>
											<#assign currUnitValues=unitDetails.getValue().get("currYearMap")>
											<#assign prevUnitValues=unitDetails.getValue().get("prevYearMap")>
											<#if currUnitValues?has_content>
												<fo:table-row>
													<#assign currAvg=0>
													<#assign prevAvg=0>
													<#assign avgDiff=0>
													<#assign prevUtilization=0>
													<#assign currUtilization=0>
			   										<#assign unit = delegator.findOne("Facility", {"facilityId" : unitDetails.getKey()}, true)>
													<fo:table-cell>
														<fo:block keep-together="always" text-align="left" font-size="5pt"> ${unit.facilityName}</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block keep-together="always" text-align="right" font-size="5pt"><#if prevUnitValues?has_content><#if prevUnitValues.get(prevDateKey).get("qtyLtrs")!=0>${prevUnitValues.get(prevDateKey).get("qtyLtrs")?if_exists?string("##0")}</#if><#else>0</#if></fo:block>
														<#assign totPrevLtrs=totPrevLtrs+prevUnitValues.get(prevDateKey).get("qtyLtrs")>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block keep-together="always" text-align="right" font-size="5pt"><#if prevUnitValues?has_content><#if prevUnitValues.get("prevprocurementDays")!=0>${(prevUnitValues.get(prevDateKey).get("qtyLtrs"))/prevUnitValues.get("prevprocurementDays")?if_exists}</#if><#else>0</#if></fo:block>
													</fo:table-cell>
													<#if prevUnitValues?has_content>
														<#if prevUnitValues.get("prevprocurementDays")!=0>
															<#assign prevAvg=(prevUnitValues.get(prevDateKey).get("qtyLtrs"))/prevUnitValues.get("prevprocurementDays")>
															<#assign totPrevAvg=totPrevAvg+prevAvg>
														</#if>
													</#if>
													<fo:table-cell>
														<fo:block keep-together="always" text-align="right" font-size="5pt"><#if currUnitValues.get(currentDateKey).get("qtyLtrs")?has_content><#if currUnitValues.get(currentDateKey).get("qtyLtrs")!=0>${currUnitValues.get(currentDateKey).get("qtyLtrs")?if_exists?string("##0")}</#if><#else>0</#if></fo:block>
														<#assign totCurrLtrs=totCurrLtrs+currUnitValues.get(currentDateKey).get("qtyLtrs")>
													</fo:table-cell>
													<#if currUnitValues.get("procurementDays")!=0>
														<#assign currAvg=(currUnitValues.get(currentDateKey).get("qtyLtrs"))/currUnitValues.get("procurementDays")>
														<fo:table-cell>
															<fo:block keep-together="always" text-align="right" font-size="5pt">${currAvg?if_exists?string("##0")}</fo:block>
															<#assign totCurrAvg=totCurrAvg+currAvg>
														</fo:table-cell>
													<#else>
														<fo:table-cell>
															<fo:block keep-together="always" text-align="right" font-size="5pt">0</fo:block>
														</fo:table-cell>
													</#if>
													<#assign avgDiff=(currAvg-prevAvg)>
													<fo:table-cell>
														<fo:block keep-together="always" text-align="right" font-size="5pt">${avgDiff?if_exists?string("##0")}</fo:block>
														<#assign totAvgDiff=totAvgDiff+avgDiff>
													</fo:table-cell>
													<#if prevUnitValues?has_content>
														<#if currUnitValues.get(currentDateKey).get("qtyLtrs")==0>
															<fo:table-cell>
																<fo:block keep-together="always" text-align="right" font-size="5pt">-100.00</fo:block>
															</fo:table-cell>
														<#else>
															<#if prevAvg!=0>
																<fo:table-cell>
																	<fo:block keep-together="always" text-align="right" font-size="5pt">${((avgDiff/prevAvg)*100)?if_exists?string("##0.00")}</fo:block>
																</fo:table-cell>
															</#if>
														</#if>
													<#else>
														<#if currUnitValues.get(currentDateKey).get("qtyLtrs")==0>
															<fo:table-cell>
																<fo:block keep-together="always" text-align="right" font-size="5pt">0.00</fo:block>
															</fo:table-cell>
														<#else>
															<fo:table-cell>
																<fo:block keep-together="always" text-align="right" font-size="5pt">100.00</fo:block>
															</fo:table-cell>
														</#if>
													</#if>
													<#if prevUnitValues?has_content>
														<#if prevUnitValues.get(prevDateKey).get("qtyLtrs")==0>
															<#if prevAvg==0>
																<fo:table-cell>
																	<fo:block keep-together="always" text-align="right" font-size="5pt">0</fo:block>
																</fo:table-cell>
															<#else>
																<fo:table-cell>
																	<fo:block keep-together="always" text-align="right" font-size="5pt"><#if prevUnitValues.get(prevDateKey).get("capacity")?has_content>${prevUnitValues.get(prevDateKey).get("capacity")?if_exists}<#else>0</#if></fo:block>
																	<#assign totPrevCapacity=totPrevCapacity+prevUnitValues.get(prevDateKey).get("capacity")>
																</fo:table-cell>
															</#if>
														<#else>
															<fo:table-cell>
																<fo:block keep-together="always" text-align="right" font-size="5pt"><#if prevUnitValues.get(prevDateKey).get("capacity")?has_content>${prevUnitValues.get(prevDateKey).get("capacity")?if_exists}<#else>0</#if></fo:block>
																<#assign totPrevCapacity=totPrevCapacity+prevUnitValues.get(prevDateKey).get("capacity")>
															</fo:table-cell>
														</#if>
													<#else>
														<fo:table-cell>
															<fo:block keep-together="always" text-align="right" font-size="5pt">0</fo:block>
														</fo:table-cell>
													</#if>
													<#if prevUnitValues?has_content>
														<#if prevUnitValues.get(prevDateKey).get("capacity")!=0>
															<#assign prevUtilization=(prevAvg/prevUnitValues.get(prevDateKey).get("capacity"))*100>
														</#if>
													</#if>
													<fo:table-cell>
														<fo:block keep-together="always" text-align="right" font-size="5pt">${prevUtilization?if_exists?string("##0.00")}</fo:block>
													</fo:table-cell>
													<#if currUnitValues?has_content>
														<#if currUnitValues.get(currentDateKey).get("qtyLtrs")==0>
															<#if currAvg==0>
																<fo:table-cell>
																	<fo:block keep-together="always" text-align="right" font-size="5pt">0</fo:block>
																</fo:table-cell>
															<#else>
																<fo:table-cell>
																	<fo:block keep-together="always" text-align="right" font-size="5pt"><#if currUnitValues.get(currentDateKey).get("capacity")?has_content> ${currUnitValues.get(currentDateKey).get("capacity")?if_exists}<#else>0</#if></fo:block>
																	<#assign totCurrCapacity=totCurrCapacity+currUnitValues.get(currentDateKey).get("capacity")>
																</fo:table-cell>
															</#if>
														<#else>
															<fo:table-cell>
																<fo:block keep-together="always" text-align="right" font-size="5pt"><#if currUnitValues.get(currentDateKey).get("capacity")?has_content> ${currUnitValues.get(currentDateKey).get("capacity")?if_exists}<#else>0</#if></fo:block>
																<#assign totCurrCapacity=totCurrCapacity+currUnitValues.get(currentDateKey).get("capacity")>
															</fo:table-cell>
														</#if>
													<#else>
														<fo:table-cell>
															<fo:block keep-together="always" text-align="right" font-size="5pt">0</fo:block>
														</fo:table-cell>
													</#if>
													<#if currUnitValues.get(currentDateKey).get("capacity")!=0>
														<#assign currUtilization=(currAvg/currUnitValues.get(currentDateKey).get("capacity"))*100>
													</#if>
													<fo:table-cell>
														<fo:block keep-together="always" text-align="right" font-size="5pt"><#if currUtilization?has_content>${currUtilization?if_exists?string("##0.00")}<#else>0</#if></fo:block>
													</fo:table-cell>
												</fo:table-row>
												<#assign noOfLines=noOfLines+1>
												<#if (noOfLines >50)>
				   									<#assign noOfLines =0>
				   									<fo:table-row>
				       									<fo:table-cell>
				       										<fo:block page-break-after="always"></fo:block>
				       									</fo:table-cell>
				   									</fo:table-row>
												</#if>
											</#if>
										</#list>
										<fo:table-row>
											<fo:table-cell>
												<fo:block text-align="left" font-size="5pt" keep-together="always" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
											</fo:table-cell>
										</fo:table-row>
										<#assign noOfLines=noOfLines+1>
										<#if (noOfLines >50)>
		   									<#assign noOfLines =0>
		   									<fo:table-row>
		       									<fo:table-cell>
		       										<fo:block page-break-after="always"></fo:block>
		       									</fo:table-cell>
		   									</fo:table-row>
										</#if>
										<fo:table-row>
											<fo:table-cell>
												<fo:block keep-together="always" text-align="left" font-size="5pt"> ${shedName?if_exists}</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block keep-together="always" text-align="right" font-size="5pt">${totPrevLtrs?if_exists?string("##0")}</fo:block>
												<#assign grandTotPrevLtrs=grandTotPrevLtrs+totPrevLtrs>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block keep-together="always" text-align="right" font-size="5pt">${totPrevAvg?if_exists?string("##0")}</fo:block>
												<#assign grandTotPrevAvg=grandTotPrevAvg+totPrevAvg>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block keep-together="always" text-align="right" font-size="5pt">${totCurrLtrs?if_exists?string("##0")}</fo:block>
												<#assign grandTotCurrLtrs=grandTotCurrLtrs+totCurrLtrs>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block keep-together="always" text-align="right" font-size="5pt">${totCurrAvg?if_exists?string("##0")}</fo:block>
												<#assign grandTotCurrAvg=grandTotCurrAvg+totCurrAvg>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block keep-together="always" text-align="right" font-size="5pt">${totAvgDiff?if_exists?string("##0")}</fo:block>
											</fo:table-cell>
											<#if totPrevAvg!=0>
												<#assign totGEVariation=(totAvgDiff/totPrevAvg)*100>
											</#if>
											<#if totGEVariation!=0>
												<fo:table-cell>
													<fo:block keep-together="always" text-align="right" font-size="5pt">${totGEVariation?if_exists?string("##0.00")}</fo:block>
												</fo:table-cell>
											<#else>
												<#if totPrevLtrs==0 && totCurrLtrs==0>
													<fo:table-cell>
														<fo:block keep-together="always" text-align="right" font-size="5pt">0.00</fo:block>
													</fo:table-cell>
												<#else>
													<#if totPrevLtrs==0>
														<fo:table-cell>
															<fo:block keep-together="always" text-align="right" font-size="5pt">100.00</fo:block>
														</fo:table-cell>
													</#if>
													<#if totCurrLtrs==0>
														<fo:table-cell>
															<fo:block keep-together="always" text-align="right" font-size="5pt">-100.00</fo:block>
														</fo:table-cell>
													</#if>
												</#if>
											</#if>
											<#if totPrevLtrs==0>
												<#if totPrevAvg==0>
													<fo:table-cell>
														<fo:block keep-together="always" text-align="right" font-size="5pt">0</fo:block>
													</fo:table-cell>
												<#else>
													<fo:table-cell>
														<fo:block keep-together="always" text-align="right" font-size="5pt">${totPrevCapacity?if_exists}</fo:block>
														<#assign grandtotPrevCapacity=grandtotPrevCapacity+totPrevCapacity>
													</fo:table-cell>
												</#if>
											<#else>
												<fo:table-cell>
													<fo:block keep-together="always" text-align="right" font-size="5pt">${totPrevCapacity?if_exists}</fo:block>
													<#assign grandtotPrevCapacity=grandtotPrevCapacity+totPrevCapacity>
												</fo:table-cell>
											</#if>
											<#if totPrevCapacity!=0>
												<#assign totPrevUtilization=(totPrevAvg/totPrevCapacity)*100>
											</#if>
											<fo:table-cell>
												<fo:block keep-together="always" text-align="right" font-size="5pt">${totPrevUtilization?if_exists?string("##0.00")}</fo:block>
											</fo:table-cell>
											<#if totCurrLtrs==0>
												<#if totCurrAvg==0>
													<fo:table-cell>
														<fo:block keep-together="always" text-align="right" font-size="5pt">0</fo:block>
													</fo:table-cell>
												<#else>
													<fo:table-cell>
														<fo:block keep-together="always" text-align="right" font-size="5pt">${totCurrCapacity?if_exists}</fo:block>
														<#assign grandtotCurrCapacity=grandtotCurrCapacity+totCurrCapacity>
													</fo:table-cell>
												</#if>
											<#else>
												<fo:table-cell>
													<fo:block keep-together="always" text-align="right" font-size="5pt">${totCurrCapacity?if_exists}</fo:block>
													<#assign grandtotCurrCapacity=grandtotCurrCapacity+totCurrCapacity>
												</fo:table-cell>
											</#if>
											<#if totCurrCapacity!=0>
												<#assign totCurrUtilization=(totCurrAvg/totCurrCapacity)*100>
											</#if>
											<fo:table-cell>
												<fo:block keep-together="always" text-align="right" font-size="5pt">${totCurrUtilization?if_exists?string("##0.00")}</fo:block>
											</fo:table-cell>
										</fo:table-row>
										<#assign noOfLines=noOfLines+1>
										<#if (noOfLines >50)>
		   									<#assign noOfLines =0>
		   									<fo:table-row>
		       									<fo:table-cell>
		       										<fo:block page-break-after="always"></fo:block>
		       									</fo:table-cell>
		   									</fo:table-row>
										</#if>
										<fo:table-row>
											<fo:table-cell>
												<fo:block text-align="left" font-size="5pt" keep-together="always" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
											</fo:table-cell>
										</fo:table-row>
										<#assign noOfLines=noOfLines+1>
										<#if (noOfLines >50)>
		   									<#assign noOfLines =0>
		   									<fo:table-row>
		       									<fo:table-cell>
		       										<fo:block page-break-after="always"></fo:block>
		       									</fo:table-cell>
		   									</fo:table-row>
										</#if>
									</#list>
									<fo:table-row>
										<fo:table-cell>
											<fo:block keep-together="always" text-align="left" font-size="5pt"> FEDERATION TOTAL:</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block keep-together="always" text-align="right" font-size="5pt">${grandTotPrevLtrs?if_exists?string("##0")}</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block keep-together="always" text-align="right" font-size="5pt">${grandTotPrevAvg?if_exists?string("##0")}</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block keep-together="always" text-align="right" font-size="5pt">${grandTotCurrLtrs?if_exists?string("##0")}</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block keep-together="always" text-align="right" font-size="5pt">${grandTotCurrAvg?if_exists?string("##0")}</fo:block>
										</fo:table-cell>
										<#assign grandTotAvgDiff=grandTotCurrAvg-grandTotPrevAvg>
										<fo:table-cell>
											<fo:block keep-together="always" text-align="right" font-size="5pt">${grandTotAvgDiff?if_exists?string("##0")}</fo:block>
										</fo:table-cell>
										<#if grandTotPrevAvg!=0>
											<#assign grandtotGEVariation=(grandTotAvgDiff/grandTotPrevAvg)*100>
										</#if>
										<fo:table-cell>
											<fo:block keep-together="always" text-align="right" font-size="5pt">${grandtotGEVariation?if_exists?string("##0.00")}</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block keep-together="always" text-align="right" font-size="5pt">${grandtotPrevCapacity?if_exists}</fo:block>
										</fo:table-cell>
										<#if grandtotPrevCapacity!=0>
											<#assign grandtotPrevUtilization=(grandTotPrevAvg/grandtotPrevCapacity)*100>
										</#if>
										<fo:table-cell>
											<fo:block keep-together="always" text-align="right" font-size="5pt">${grandtotPrevUtilization?if_exists?string("##0.00")}</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block keep-together="always" text-align="right" font-size="5pt">${grandtotCurrCapacity?if_exists}</fo:block>
										</fo:table-cell>
										<#if grandtotCurrCapacity!=0>
											<#assign grandtotCurrUtilization=(grandTotCurrAvg/grandtotCurrCapacity)*100>
										</#if>
										<fo:table-cell>
											<fo:block keep-together="always" text-align="right" font-size="5pt">${grandtotCurrUtilization?if_exists?string("##0.00")}</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<#assign noOfLines=noOfLines+1>
									<#if (noOfLines >50)>
	   									<#assign noOfLines =0>
	   									<fo:table-row>
	       									<fo:table-cell>
	       										<fo:block page-break-after="always"></fo:block>
	       									</fo:table-cell>
	   									</fo:table-row>
									</#if>
									<fo:table-row>
										<fo:table-cell>
											<fo:block text-align="left" font-size="5pt" keep-together="always" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
						     			<fo:table-cell>	          				
						           			<fo:block font-family="Courier,monospace" font-size="10pt" break-before="page"/>     
						           		</fo:table-cell>
									</fo:table-row>
									<#assign fedTotPrevLtrs=0>
									<#assign fedTotPrevAvg=0>
									<#assign fedTotCurrLtrs=0>
									<#assign fedTotCurrAvg=0>
									<#assign fedTotAvgDiff=0>
									<#assign fedtotPrevCapacity=0>
									<#assign fedtotCurrCapacity=0>
									<#assign fedtotGEVariation=0>
									<#assign fedtotPrevUtilization=0>
									<#assign fedtotCurrUtilization=0>
									<#assign shedtotValues=shedWiseTotMap.entrySet()>
		   							<#list shedtotValues as shedtotDetails>
										<#assign facility = delegator.findOne("Facility", {"facilityId" : shedtotDetails.getKey()}, true)>
		   								<#assign shedName = (facility.facilityName).toUpperCase()>
							           	<#assign shedName = shedName.substring(0,shedName.indexOf("MILK SHED"))>
							           	<#assign shedtotPrevUtilization=0>
										<#assign shedtotCurrUtilization=0>
										<#assign shedtotGEVariation=0>
							           	<fo:table-row>
											<fo:table-cell>
												<fo:block keep-together="always" text-align="left" font-size="5pt"> ${shedName}</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block keep-together="always" text-align="right" font-size="5pt"> ${shedtotDetails.getValue().get("totPrevLtrs")?if_exists?string("##0")}</fo:block>
												<#assign fedTotPrevLtrs=fedTotPrevLtrs+shedtotDetails.getValue().get("totPrevLtrs")>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block keep-together="always" text-align="right" font-size="5pt"> ${shedtotDetails.getValue().get("totprevAvg")?if_exists?string("##0")}</fo:block>
												<#assign fedTotPrevAvg=fedTotPrevAvg+shedtotDetails.getValue().get("totprevAvg")>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block keep-together="always" text-align="right" font-size="5pt"> ${shedtotDetails.getValue().get("totCurrLtrs")?if_exists?string("##0")}</fo:block>
												<#assign fedTotCurrLtrs=fedTotCurrLtrs+shedtotDetails.getValue().get("totCurrLtrs")>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block keep-together="always" text-align="right" font-size="5pt"> ${shedtotDetails.getValue().get("totcurrAvg")?if_exists?string("##0")}</fo:block>
												<#assign fedTotCurrAvg=fedTotCurrAvg+shedtotDetails.getValue().get("totcurrAvg")>
											</fo:table-cell>
											<#assign shedAvgDiff=(shedtotDetails.getValue().get("totcurrAvg")-shedtotDetails.getValue().get("totprevAvg"))>
											<fo:table-cell>
												<fo:block keep-together="always" text-align="right" font-size="5pt"> ${shedAvgDiff?if_exists?string("##0")}</fo:block>
											</fo:table-cell>
											<#if shedtotDetails.getValue().get("totprevAvg")!=0>
												<#assign shedtotGEVariation=(shedAvgDiff/shedtotDetails.getValue().get("totprevAvg"))*100>
											</#if>
											<#if shedtotGEVariation!=0>
												<fo:table-cell>
													<fo:block keep-together="always" text-align="right" font-size="5pt">${shedtotGEVariation?if_exists?string("##0.00")}</fo:block>
												</fo:table-cell>
											<#else>
												<#if shedtotDetails.getValue().get("totPrevLtrs")==0 && shedtotDetails.getValue().get("totCurrLtrs")==0>
													<fo:table-cell>
														<fo:block keep-together="always" text-align="right" font-size="5pt">0.00</fo:block>
													</fo:table-cell>
												<#else>
													<#if shedtotDetails.getValue().get("totPrevLtrs")==0>
														<fo:table-cell>
															<fo:block keep-together="always" text-align="right" font-size="5pt">100.00</fo:block>
														</fo:table-cell>
													</#if>
													<#if shedtotDetails.getValue().get("totCurrLtrs")==0>
														<fo:table-cell>
															<fo:block keep-together="always" text-align="right" font-size="5pt">-100.00</fo:block>
														</fo:table-cell>
													</#if>
												</#if>
											</#if>
											<fo:table-cell>
												<fo:block keep-together="always" text-align="right" font-size="5pt"> ${shedtotDetails.getValue().get("totPrevCapacity")}</fo:block>
												<#assign fedtotPrevCapacity=fedtotPrevCapacity+shedtotDetails.getValue().get("totPrevCapacity")>
											</fo:table-cell>
											<#if shedtotDetails.getValue().get("totPrevCapacity")!=0>
												<#assign shedtotPrevUtilization=(shedtotDetails.getValue().get("totprevAvg")/shedtotDetails.getValue().get("totPrevCapacity"))*100>
											</#if>
											<fo:table-cell>
												<fo:block keep-together="always" text-align="right" font-size="5pt"> ${shedtotPrevUtilization?if_exists?string("##0.00")}</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block keep-together="always" text-align="right" font-size="5pt"> ${shedtotDetails.getValue().get("totCurrCapacity")}</fo:block>
												<#assign fedtotCurrCapacity=fedtotCurrCapacity+shedtotDetails.getValue().get("totCurrCapacity")>
											</fo:table-cell>
											<#if shedtotDetails.getValue().get("totCurrCapacity")!=0>
												<#assign shedtotCurrUtilization=(shedtotDetails.getValue().get("totcurrAvg")/shedtotDetails.getValue().get("totCurrCapacity"))*100>
											</#if>
											<fo:table-cell>
												<fo:block keep-together="always" text-align="right" font-size="5pt"> ${shedtotCurrUtilization?if_exists?string("##0.00")}</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</#list>
									<fo:table-row>
										<fo:table-cell>
											<fo:block text-align="left" font-size="5pt" keep-together="always" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell>
											<fo:block keep-together="always" text-align="left" font-size="5pt"> FEDERATION TOTAL:</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block keep-together="always" text-align="right" font-size="5pt">${fedTotPrevLtrs?if_exists?string("##0")}</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block keep-together="always" text-align="right" font-size="5pt">${fedTotPrevAvg?if_exists?string("##0")}</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block keep-together="always" text-align="right" font-size="5pt">${fedTotCurrLtrs?if_exists?string("##0")}</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block keep-together="always" text-align="right" font-size="5pt">${fedTotCurrAvg?if_exists?string("##0")}</fo:block>
										</fo:table-cell>
										<#assign fedTotAvgDiff=fedTotCurrAvg-fedTotPrevAvg>
										<fo:table-cell>
											<fo:block keep-together="always" text-align="right" font-size="5pt">${fedTotAvgDiff?if_exists?string("##0")}</fo:block>
										</fo:table-cell>
										<#if fedTotPrevAvg!=0>
											<#assign fedtotGEVariation=(fedTotAvgDiff/fedTotPrevAvg)*100>
										</#if>
										<fo:table-cell>
											<fo:block keep-together="always" text-align="right" font-size="5pt">${fedtotGEVariation?if_exists?string("##0.00")}</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block keep-together="always" text-align="right" font-size="5pt">${fedtotPrevCapacity?if_exists}</fo:block>
										</fo:table-cell>
										<#if fedtotPrevCapacity!=0>
											<#assign fedtotPrevUtilization=(fedTotPrevAvg/fedtotPrevCapacity)*100>
										</#if>
										<fo:table-cell>
											<fo:block keep-together="always" text-align="right" font-size="5pt">${fedtotPrevUtilization?if_exists?string("##0.00")}</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block keep-together="always" text-align="right" font-size="5pt">${fedtotCurrCapacity?if_exists}</fo:block>
										</fo:table-cell>
										<#if fedtotCurrCapacity!=0>
											<#assign fedtotCurrUtilization=(fedTotCurrAvg/fedtotCurrCapacity)*100>
										</#if>
										<fo:table-cell>
											<fo:block keep-together="always" text-align="right" font-size="5pt">${fedtotCurrUtilization?if_exists?string("##0.00")}</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell>
											<fo:block text-align="left" font-size="5pt" keep-together="always" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
		    				</fo:table>
		    			</fo:block>
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
						
						
						
						
						
						
						
						
						
						
						
						
						
						
						
						
						