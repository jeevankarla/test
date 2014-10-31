<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-top=".5in">
                <fo:region-body margin-top=".5in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
         ${setRequestAttribute("OUTPUT_FILENAME", "MilkReceiptWeeklyAnalysis.txt")}
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
		<#assign milkDetail = finalMap.entrySet()>
		
		<fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before">
              <fo:block font-size="4pt" >VST_ASCII-015 VST_ASCII-027VST_ASCII-103</fo:block>
                <fo:block text-align="left" white-space-collapse="false" keep-together="always">.              STATEMENT SHOWING THE SHED-WISE MILK RECIEPTS PERIOD FROM: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")} (MPF HYDERABAD)   </fo:block>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always">.                                     LITERS,(IN  LAKHS) KGFAT AND KGSNF(IN METRIC TONNES)   </fo:block>
				<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false">------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
							<#assign QtyLtrs=0>
	             			<#assign KgFat=0>
	             			<#assign KgSnf=0>
							<#assign totalQtyLtrs=0>
	             			<#assign totalKgFat=0>
	             			<#assign totalKgSnf=0>
	             			<#assign WeekQtyLtrs=0>
	             			<#assign WeekKgFat=0>
	             			<#assign WeekKgSnf=0>
	             			<#assign NoofDays=1>
	             			<#assign weektotQtyLtrs=0>
	             			<#assign weektotKgFat=0>
	             			<#assign weektotKgSnf=0>
	             			<#assign grandTotQtyLtrs=0>
	             			<#assign grandTotKgFat=0>
	             			<#assign grandTotKgSnf=0>
	             			<#assign grandTotalQtyLtrs=0>
	             			<#assign grandTotalKgFat=0>
	             			<#assign grandTotalKgSnf=0>       				   
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="4pt">
					<fo:table>
						<fo:table-column column-width="25pt"/>
						<fo:table-column column-width="5pt"/>
						<fo:table-column column-width="55pt"/>
						<fo:table-body>
							<fo:table-row>
								
							   	<fo:table-cell>
							   		<fo:block keep-together="always" text-align="left">DATE</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="right" font-size="5pt">&#160;</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block font-size="4pt" font-family="Courier,monospace">
										<fo:table>
											<#assign mccShedWiseDetailList = mccTypeShedMap.entrySet()>
											<#list mccShedWiseDetailList as mccShedWiseList>
												<#assign mccShedsList = mccShedWiseList.getValue()>
								            	<#list mccShedsList as shed>
													<fo:table-column column-width="52pt"/>
													<fo:table-column column-width="3pt"/>
					           					</#list>
					                		</#list>
					                		<fo:table-column column-width="52pt"/>
					           				<fo:table-body>
					        					<fo:table-row>
					               	 				<#list mccShedWiseDetailList as mccShedWiseList>
														<#assign mccShedsList = mccShedWiseList.getValue()>
								            			<#list mccShedsList as shed>
								            				<#assign facility = delegator.findOne("Facility", {"facilityId" : shed}, true)>
							                				<#assign shedName = (facility.facilityName).toUpperCase()>
							                				<#assign shedName = shedName.substring(0,shedName.indexOf("MILK SHED"))>
							                				<fo:table-cell>
							                					<fo:block white-space-collapse="false" font-size="4pt" text-align="center" >${shedName?if_exists}&#160;</fo:block>
							                					<fo:block white-space-collapse="false" font-size="4pt" text-align="center" >--------------------</fo:block>
							                					<fo:block font-size="4pt" font-family="Courier,monospace">
													           			<fo:table>
															           			<fo:table-column column-width="17pt"/>
															           			<fo:table-column column-width="17pt"/>
															           			<fo:table-column column-width="17pt"/>
																			<fo:table-body>
																				<fo:table-row>
																					<fo:table-cell><fo:block white-space-collapse="false" text-align="center" font-size="4pt">LITERS</fo:block></fo:table-cell>
																					<fo:table-cell><fo:block white-space-collapse="false" text-align="center" font-size="4pt"> KGFAT</fo:block></fo:table-cell>
																					<fo:table-cell><fo:block white-space-collapse="false" text-align="center" font-size="4pt"> KGSNF</fo:block></fo:table-cell>
																				</fo:table-row>
																			</fo:table-body>
										           						</fo:table>
										           					</fo:block>
							                				</fo:table-cell>
							                				<fo:table-cell><fo:block white-space-collapse="false" text-align="center" font-size="4pt">&#160;</fo:block></fo:table-cell>
					                					</#list>
					                				</#list>
					                				
					                				<fo:table-cell>
							                					<fo:block font-size="4pt" text-align="center" >TOTAL</fo:block>
							                					<fo:block white-space-collapse="false" font-size="4pt" text-align="center" >--------------------</fo:block>
							                					<fo:block font-size="4pt" font-family="Courier,monospace">
										           				<fo:table>
												           			<fo:table-column column-width="17pt"/>
												           			<fo:table-column column-width="17pt"/>
												           			<fo:table-column column-width="17pt"/>
												           			<fo:table-column column-width="1pt"/>
																<fo:table-body>
																	<fo:table-row>
																		<fo:table-cell><fo:block white-space-collapse="false" text-align="center" font-size="4pt">LITERS</fo:block></fo:table-cell>
																					<fo:table-cell><fo:block white-space-collapse="false" text-align="center" font-size="4pt"> KGFAT</fo:block></fo:table-cell>
																					<fo:table-cell><fo:block white-space-collapse="false" text-align="center" font-size="4pt"> KGSNF</fo:block></fo:table-cell>
																					<fo:table-cell><fo:block white-space-collapse="false" text-align="center" font-size="4pt">&#160;</fo:block></fo:table-cell>
																	</fo:table-row>
																</fo:table-body>
							           						</fo:table>
							           					</fo:block>
							                				</fo:table-cell>
					           					</fo:table-row>
											</fo:table-body>
										</fo:table>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
									<fo:block text-align="left" font-size="5pt" keep-together="always" white-space-collapse="false">------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				</fo:block>
				<fo:block font-size="4pt" font-family="Courier,monospace">
					<fo:table>
						<fo:table-column column-width="25pt"/>
							<#list mccShedWiseDetailList as mccShedWiseList>
								<#assign mccShedsList = mccShedWiseList.getValue()>
								<#list mccShedsList as shed>
									<fo:table-column column-width="17pt"/>
									<fo:table-column column-width="17pt"/>
									<fo:table-column column-width="17pt"/>
									<fo:table-column column-width="4pt"/>
								</#list>
							</#list>
                			<fo:table-column column-width="17pt"/>
							<fo:table-column column-width="17pt"/>
							<fo:table-column column-width="17pt"/>
							<fo:table-column column-width="4pt"/>
							<fo:table-body>
								<#assign noOfDays = 0>
								<#assign noDays = 0>
								<#assign weekCount=1>
								<#list dateKeysList as date>
									<#if weekCount==4>
										<#assign noDays = noDays+1>
									</#if>
									<fo:table-row>
										<fo:table-cell>
											<fo:block font-size="4pt">${date}</fo:block>
										</fo:table-cell>
										<#list mccShedWiseDetailList as mccShedWiseList>
											<#assign mccShedsList = mccShedWiseList.getValue()>
									        <#list mccShedsList as shed>
									          	<#assign shedWiseData ={}>  			
												<#assign shedWiseData = finalMap.get(shed)>
												<#if shedWiseData?has_content>
													<#assign dayValue={}>
													<#assign dayValue = shedWiseData.get(date)>
													<#if dayValue?has_content>
														<fo:table-cell>
															<fo:block text-align="right" font-size="4pt">${dayValue.get("qtyLtrs")?if_exists?string("##0.00")}</fo:block>
															<#assign totalQtyLtrs=totalQtyLtrs+(dayValue.get("qtyLtrs"))>
														</fo:table-cell>
														<fo:table-cell>
															<fo:block text-align="right" font-size="4pt">${dayValue.get("kgFat")?if_exists?string("##0.00")}</fo:block>
															<#assign totalKgFat=totalKgFat+(dayValue.get("kgFat"))>
														</fo:table-cell>
														<fo:table-cell>
															<fo:block text-align="right" font-size="4pt">${dayValue.get("kgSnf")?if_exists?string("##0.00")}</fo:block>
															<#assign totalKgSnf=totalKgSnf+(dayValue.get("kgSnf"))>
														</fo:table-cell>
														<fo:table-cell>
															<fo:block text-align="right" font-size="4pt">&#160;</fo:block>
														</fo:table-cell>
													<#else>
														<fo:table-cell>
															<fo:block text-align="right" font-size="4pt">0.00</fo:block>
														</fo:table-cell>
														<fo:table-cell>
															<fo:block text-align="right" font-size="4pt">0.00</fo:block>
														</fo:table-cell>
														<fo:table-cell>
															<fo:block text-align="right" font-size="4pt">0.00</fo:block>
														</fo:table-cell>
														<fo:table-cell>
															<fo:block text-align="right" font-size="4pt">&#160;</fo:block>
														</fo:table-cell>
													</#if>			
												<#else>
													<fo:table-cell>
														<fo:block text-align="right" font-size="4pt">0.00</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="right" font-size="4pt">0.00</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="right" font-size="4pt">0.00</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="right" font-size="4pt">&#160;</fo:block>
													</fo:table-cell>
												</#if>
											</#list>
										</#list>
										<fo:table-cell>
											<fo:block text-align="right" font-size="4pt">${totalQtyLtrs?if_exists?string("##0.00")}</fo:block>
											<#assign grandTotalQtyLtrs=grandTotalQtyLtrs+totalQtyLtrs>
											<#assign weektotQtyLtrs=weektotQtyLtrs+totalQtyLtrs>
											<#assign totalQtyLtrs=0>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block text-align="right" font-size="4pt">${totalKgFat?if_exists?string("##0.00")}</fo:block>
											<#assign grandTotalKgFat=grandTotalKgFat+totalKgFat>
											<#assign weektotKgFat=weektotKgFat+totalKgFat>
											<#assign totalKgFat=0>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block text-align="right" font-size="4pt">${totalKgSnf?if_exists?string("##0.00")}</fo:block>
											<#assign grandTotalKgSnf=grandTotalKgSnf+totalKgSnf>
											<#assign weektotKgSnf=weektotKgSnf+totalKgSnf>
											<#assign totalKgSnf=0>
										</fo:table-cell>
										<fo:table-cell>
															<fo:block text-align="right" font-size="4pt">&#160;</fo:block>
														</fo:table-cell>
									</fo:table-row>
										<#if NoofDays==7>
											<#assign weekKey = "week"+weekCount>
											<fo:table-row>
												<fo:table-cell>
												<fo:block text-align="left" font-size="5pt" keep-together="always" white-space-collapse="false">------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<fo:table-row>
												<fo:table-cell>
													<fo:block keep-together="always">WEEK TOT   :</fo:block>
												</fo:table-cell>
												<#assign week = WeekWiseMap.get(weekKey)>
												<#list mccShedWiseDetailList as mccShedWiseList>
													<#assign mccShedsList = mccShedWiseList.getValue()>
										            <#list mccShedsList as shed>
										            	<#if week.get(shed)?has_content>
															<fo:table-cell>
																<fo:block text-align="right" font-size="4pt">${week.get(shed).get("qtyLtrs")?if_exists?string("##0.00")}</fo:block>
															</fo:table-cell>
															<fo:table-cell>
																<fo:block text-align="right" font-size="4pt">${week.get(shed).get("kgFat")?if_exists?string("##0.00")}</fo:block>
															</fo:table-cell>
															<fo:table-cell>
																<fo:block text-align="right" font-size="4pt">${week.get(shed).get("kgSnf")?if_exists?string("##0.00")}</fo:block>
															</fo:table-cell>
															<fo:table-cell>
															<fo:block text-align="right" font-size="4pt">&#160;</fo:block>
														</fo:table-cell>
														<#else>
															<fo:table-cell>
																<fo:block text-align="right" font-size="4pt">0.00</fo:block>
															</fo:table-cell>
															<fo:table-cell>
																<fo:block text-align="right" font-size="4pt">0.00</fo:block>
															</fo:table-cell>
															<fo:table-cell>
																<fo:block text-align="right" font-size="4pt">0.00</fo:block>
															</fo:table-cell>
															<fo:table-cell>
															<fo:block text-align="right" font-size="4pt">&#160;</fo:block>
														</fo:table-cell>
														</#if>
													</#list>
													<fo:table-cell>
														<fo:block text-align="right" font-size="4pt">${weektotQtyLtrs?if_exists?string("##0.00")}</fo:block>
														<#assign weekavgQtyLtrs=(weektotQtyLtrs/7)>
														<#assign weektotQtyLtrs=0>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="right" font-size="4pt">${weektotKgFat?if_exists?string("##0.00")}</fo:block>
														<#assign weekavgKgFat=(weektotKgFat/7)>
														<#assign weektotKgFat=0>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block text-align="right" font-size="4pt">${weektotKgSnf?if_exists?string("##0.00")}</fo:block>
														<#assign weekavgKgSnf=(weektotKgFat/7)>
														<#assign weektotKgSnf=0>
													</fo:table-cell>
													<fo:table-cell>
															<fo:block text-align="right" font-size="4pt">&#160;</fo:block>
														</fo:table-cell>
												</#list>
											</fo:table-row>
											<fo:table-row>
												<fo:table-cell>
													<fo:block keep-together="always">AVG/PERDAY</fo:block>
												</fo:table-cell>
												<#assign week = WeekWiseMap.get(weekKey)>
												<#list mccShedWiseDetailList as mccShedWiseList>
													<#assign mccShedsList = mccShedWiseList.getValue()>
										            <#list mccShedsList as shed>
										            	<#if week.get(shed)?has_content>
										            		<#if weekCount==1>
																<fo:table-cell>
																	<fo:block text-align="right" font-size="4pt">${(week.get(shed).get("qtyLtrs")/7)?if_exists?string("##0.00")}</fo:block>
																</fo:table-cell>
																<fo:table-cell>
																	<fo:block text-align="right" font-size="4pt">${(week.get(shed).get("kgFat")/7)?if_exists?string("##0.00")}</fo:block>
																</fo:table-cell>
																<fo:table-cell>
																	<fo:block text-align="right" font-size="4pt">${(week.get(shed).get("kgSnf")/7)?if_exists?string("##0.00")}</fo:block>
																</fo:table-cell>
																<fo:table-cell>
															<fo:block text-align="right" font-size="4pt">&#160;</fo:block>
														</fo:table-cell>
															<#elseif weekCount==4 && noDays!=0>
																<fo:table-cell>
																	<fo:block text-align="right" font-size="4pt">${(week.get(shed).get("qtyLtrs")/noDays)?if_exists?string("##0.00")}</fo:block>
																</fo:table-cell>
																<fo:table-cell>
																	<fo:block text-align="right" font-size="4pt">${(week.get(shed).get("kgFat")/noDays)?if_exists?string("##0.00")}</fo:block>
																</fo:table-cell>
																<fo:table-cell>
																	<fo:block text-align="right" font-size="4pt">${(week.get(shed).get("kgSnf")/noDays)?if_exists?string("##0.00")}</fo:block>
																</fo:table-cell>
																<fo:table-cell>
															<fo:block text-align="right" font-size="4pt">&#160;</fo:block>
														</fo:table-cell>
															<#else>
																<fo:table-cell>
																	<fo:block text-align="right" font-size="4pt">${(week.get(shed).get("qtyLtrs")/8)?if_exists?string("##0.00")}</fo:block>
																</fo:table-cell>
																<fo:table-cell>
																	<fo:block text-align="right" font-size="4pt">${(week.get(shed).get("kgFat")/8)?if_exists?string("##0.00")}</fo:block>
																</fo:table-cell>
																<fo:table-cell>
																	<fo:block text-align="right" font-size="4pt">${(week.get(shed).get("kgSnf")/8)?if_exists?string("##0.00")}</fo:block>
																</fo:table-cell>
																<fo:table-cell>
															<fo:block text-align="right" font-size="4pt">&#160;</fo:block>
														</fo:table-cell>
															</#if>
														<#else>
															<fo:table-cell>
																<fo:block text-align="right" font-size="4pt">0.00</fo:block>
															</fo:table-cell>
															<fo:table-cell>
																<fo:block text-align="right" font-size="4pt">0.00</fo:block>
															</fo:table-cell>
															<fo:table-cell>
																<fo:block text-align="right" font-size="4pt">0.00</fo:block>
															</fo:table-cell>
															<fo:table-cell>
															<fo:block text-align="right" font-size="4pt">&#160;</fo:block>
														</fo:table-cell>
														</#if>
													</#list>
												<fo:table-cell>
													<fo:block text-align="right" font-size="4pt">${weekavgQtyLtrs?if_exists?string("##0.00")}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block text-align="right" font-size="4pt">${weekavgKgFat?if_exists?string("##0.00")}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block text-align="right" font-size="4pt">${weekavgKgSnf?if_exists?string("##0.00")}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
															<fo:block text-align="right" font-size="4pt">&#160;</fo:block>
														</fo:table-cell>
											</#list>
										</fo:table-row>
										<fo:table-row>
											<fo:table-cell>
												<fo:block text-align="left" font-size="5pt" keep-together="always" white-space-collapse="false">------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
											</fo:table-cell>
										</fo:table-row>
									<#assign NoofDays=-1>
									<#assign weekCount = weekCount+1>
								</#if>
								<#assign NoofDays=NoofDays+1>
							</#list>
							<#if weekCount==4>
								<#assign weekKey = "week"+weekCount>
								<fo:table-row>
									<fo:table-cell>
										<fo:block text-align="left" font-size="5pt" keep-together="always" white-space-collapse="false">------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<#if noDays!=0>
								<fo:table-row>
									<fo:table-cell>
										<fo:block>WEEK TOT   :</fo:block>
									</fo:table-cell>
									<#assign week = WeekWiseMap.get(weekKey)>
									<#list mccShedWiseDetailList as mccShedWiseList>
										<#assign mccShedsList = mccShedWiseList.getValue()>
								        <#list mccShedsList as shed>
								            <#if week.get(shed)?has_content>
												<fo:table-cell>
													<fo:block text-align="right" font-size="4pt">${week.get(shed).get("qtyLtrs")?if_exists?string("##0.00")}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block text-align="right" font-size="4pt">${week.get(shed).get("kgFat")?if_exists?string("##0.00")}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block text-align="right" font-size="4pt">${week.get(shed).get("kgSnf")?if_exists?string("##0.00")}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
															<fo:block text-align="right" font-size="4pt">&#160;</fo:block>
														</fo:table-cell>
											<#else>
												<fo:table-cell>
													<fo:block text-align="right" font-size="4pt">0.00</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block text-align="right" font-size="4pt">0.00</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block text-align="right" font-size="4pt">0.00</fo:block>
												</fo:table-cell>
												<fo:table-cell>
															<fo:block text-align="right" font-size="4pt">&#160;</fo:block>
														</fo:table-cell>
											</#if>
										</#list>
										<fo:table-cell>
											<fo:block text-align="right" font-size="4pt">${weektotQtyLtrs?if_exists?string("##0.00")}</fo:block>
											<#assign weekavgQtyLtrs=(weektotQtyLtrs/7)>
											<#assign weektotQtyLtrs=0>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block text-align="right" font-size="4pt">${weektotKgFat?if_exists?string("##0.00")}</fo:block>
											<#assign weekavgKgFat=(weektotKgFat/7)>
											<#assign weektotKgFat=0>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block text-align="right" font-size="4pt">${weektotKgSnf?if_exists?string("##0.00")}</fo:block>
											<#assign weekavgKgSnf=(weektotKgFat/7)>
											<#assign weektotKgSnf=0>
										</fo:table-cell>
										<fo:table-cell>
															<fo:block text-align="right" font-size="4pt">&#160;</fo:block>
														</fo:table-cell>
									</#list>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell>
										<fo:block>AVG/PERDAY</fo:block>
									</fo:table-cell>
									<#assign week = WeekWiseMap.get(weekKey)>
									<#list mccShedWiseDetailList as mccShedWiseList>
										<#assign mccShedsList = mccShedWiseList.getValue()>
								        <#list mccShedsList as shed>
								            <#if week.get(shed)?has_content && noDays!=0>
												<fo:table-cell>
													<fo:block text-align="right" font-size="4pt">${(week.get(shed).get("qtyLtrs")/noDays)?if_exists?string("##0.00")}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block text-align="right" font-size="4pt">${(week.get(shed).get("kgFat")/noDays)?if_exists?string("##0.00")}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block text-align="right" font-size="4pt">${(week.get(shed).get("kgSnf")/noDays)?if_exists?string("##0.00")}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
															<fo:block text-align="right" font-size="4pt">&#160;</fo:block>
														</fo:table-cell>
											<#else>
												<fo:table-cell>
													<fo:block text-align="right" font-size="4pt">0.00</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block text-align="right" font-size="4pt">0.00</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block text-align="right" font-size="4pt">0.00</fo:block>
												</fo:table-cell>
												<fo:table-cell>
															<fo:block text-align="right" font-size="4pt">&#160;</fo:block>
														</fo:table-cell>
											</#if>
										</#list>
										<fo:table-cell>
											<fo:block text-align="right" font-size="4pt">${weekavgQtyLtrs?if_exists?string("##0.00")}</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block text-align="right" font-size="4pt">${weekavgKgFat?if_exists?string("##0.00")}</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block text-align="right" font-size="4pt">${weekavgKgSnf?if_exists?string("##0.00")}</fo:block>
										</fo:table-cell>
										<fo:table-cell>
															<fo:block text-align="right" font-size="4pt">&#160;</fo:block>
														</fo:table-cell>
									</#list>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell>
									<fo:block text-align="left" font-size="5pt" keep-together="always" white-space-collapse="false">------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
									</fo:table-cell>
								</fo:table-row>
								</#if>
							</#if>
							<fo:table-row>
								<fo:table-cell>
									<fo:block>GRAND TOT:</fo:block>
								</fo:table-cell>
								<#assign grandTot = grandTotMap.entrySet()>
								<#list grandTot as grandtotValues>
									<fo:table-cell>
										<fo:block text-align="right" font-size="4pt">${grandtotValues.getValue().get("grandtotQty")?if_exists?string("##0.00")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="right" font-size="4pt">${grandtotValues.getValue().get("grandtotkgFat")?if_exists?string("##0.00")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="right" font-size="4pt">${grandtotValues.getValue().get("grandkgSnf")?if_exists?string("##0.00")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
															<fo:block text-align="right" font-size="4pt">&#160;</fo:block>
														</fo:table-cell>
								</#list>
								<fo:table-cell>
									<fo:block text-align="right" font-size="4pt">${grandTotalQtyLtrs?if_exists?string("##0.00")}</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="right" font-size="4pt">${grandTotalKgFat?if_exists?string("##0.00")}</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="right" font-size="4pt">${grandTotalKgSnf?if_exists?string("##0.00")}</fo:block>
								</fo:table-cell>
								<fo:table-cell>
															<fo:block text-align="right" font-size="4pt">&#160;</fo:block>
														</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
									<fo:block>AVG/PERDAY</fo:block>
								</fo:table-cell>
								<#assign totalDays=0>
								<#list dateKeysList as date>
									<#assign totalDays=totalDays+1>
								</#list>
								<#assign grandTot = grandTotMap.entrySet()>
								<#list grandTot as grandtotValues>
									<#assign avgGrandTotQty=grandtotValues.getValue().get("grandtotQty")/totalDays>
									<#assign avgGrandTotKgfat=grandtotValues.getValue().get("grandtotkgFat")/totalDays>
									<#assign avgGrandTotKgsnf=grandtotValues.getValue().get("grandkgSnf")/totalDays>
									<fo:table-cell>
										<fo:block text-align="right" font-size="4pt">${avgGrandTotQty?if_exists?string("##0.00")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="right" font-size="4pt">${avgGrandTotKgfat?if_exists?string("##0.00")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="right" font-size="4pt">${avgGrandTotKgsnf?if_exists?string("##0.00")}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
															<fo:block text-align="right" font-size="4pt">&#160;</fo:block>
														</fo:table-cell>
								</#list>
								<fo:table-cell>
									<fo:block text-align="right" font-size="4pt">${(grandTotalQtyLtrs/totalDays)?if_exists?string("##0.00")}</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="right" font-size="4pt">${(grandTotalKgFat/totalDays)?if_exists?string("##0.00")}</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="right" font-size="4pt">${(grandTotalKgSnf/totalDays)?if_exists?string("##0.00")}</fo:block>
								</fo:table-cell>
								<fo:table-cell>
															<fo:block text-align="right" font-size="4pt">&#160;</fo:block>
														</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
									<fo:block text-align="left" font-size="5pt" keep-together="always" white-space-collapse="false">------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
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