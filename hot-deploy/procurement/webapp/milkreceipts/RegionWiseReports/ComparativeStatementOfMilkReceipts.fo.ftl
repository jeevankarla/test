<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in" margin-top=".2in">
                <fo:region-body margin-top="1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "ComparativeStatement.txt")}
<#if errorMessage?has_content>
<fo:page-sequence master-reference="main">
   <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
      <fo:block font-size="14pt">
              ${errorMessage}.
   	  </fo:block>
   </fo:flow>
</fo:page-sequence>        
<#else>
<fo:page-sequence master-reference="main">
	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">&#160;																																		MPF,HYDERABAD MILK RECEIPTS</fo:block>
		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">.</fo:block>
		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">&#160;&#160; CURRENT  YEAR 	:	${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate,"dd-MM-yyyy")} To ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate,"dd-MM-yyyy")}		DAYS	:	${currTotalDays}</fo:block>
		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">&#160;&#160;PREVIOUS YEAR 	:	${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(prevDateStart,"dd-MM-yyyy")} To ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(prevDateEnd,"dd-MM-yyyy")}		DAYS	:	${prevTotalDays} (LAKHS IN LITRES)</fo:block>
		<fo:block font-size="5pt">---------------------------------------------------------------------------------------------------------</fo:block>
		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">&#160;                     COMPARATIVE STATEMENT OF M.P.F., HYDERABAD MILK RECEIPTS</fo:block>
		<fo:block font-size="5pt">--------------------------------|---------------------|--------------------|-------------|--------------|</fo:block>
		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">CODE				SHED/UNION NAME								 |			CURRENT YEAR					 |			PREVIOUS YEAR				| 		DIFFRENCE 	| 		%GE GROTH 	|</fo:block>
		<fo:block font-size="5pt" white-space-collapse="false">&#160;                               |---------------------|--------------------|</fo:block>
		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">&#160;                               | TOTAL   |  AVG/DAY  &#160; TOTAL  | AVG/DAY   |</fo:block>
		<fo:block font-size="5pt">--------------------------------|---------------------|--------------------|-------------|--------------|</fo:block>
	</fo:static-content>
	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
		<fo:block font-size="5pt">
			<fo:table>
				<fo:table-column column-width="20pt"/>
				<fo:table-column column-width="20pt"/>
				<fo:table-column column-width="83pt"/>
				<fo:table-column column-width="25pt"/>
				<fo:table-column column-width="40pt"/>
				<fo:table-column column-width="23pt"/>
				<fo:table-column column-width="45pt"/>
				<fo:table-column column-width="45pt"/>
				<#assign mccShedWiseDetailsList=mccTypeShedMap.entrySet()>
				<fo:table-body>
				<#list mccShedWiseDetailsList as mccShedWiseList>
					<#assign mccShedList = mccShedWiseList.getValue()>
					<#list mccShedList as shed>
					<fo:table-row>
						<#assign facility = delegator.findOne("Facility", {"facilityId" : shed}, true)>
						<fo:table-cell>
							<fo:block keep-together="always" text-align="left">${facility.mccCode?if_exists}</fo:block>
						</fo:table-cell>
						<fo:table-cell>
						<#assign shedName=Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((facility.get("facilityName")))),23)>
							<fo:block keep-together="always" text-align="left">${shedName?upper_case?if_exists}</fo:block>
						</fo:table-cell>
						<#assign shedValue=0>
						<#if (currentShedFinalMap.get(shed))?has_content>
						<#assign shedValue=currentShedFinalMap.get(shed)>
						<fo:table-cell>
							<fo:block keep-together="always" text-align="right">${shedValue?if_exists?string("##0.00")}</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block keep-together="always" text-align="right">${(shedValue/currTotalDays)?if_exists?string("##0.00")}</fo:block>
						</fo:table-cell>
						<#else>
						<fo:table-cell>
							<fo:block keep-together="always" text-align="right">0.00</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block keep-together="always" text-align="right">0.00</fo:block>
						</fo:table-cell>
						</#if>
						<#assign prevShedValue=0>
						<#if (prevShedFinalMap.get(shed))?has_content>
						<#assign prevShedValue=prevShedFinalMap.get(shed)>
						<fo:table-cell>
							<fo:block keep-together="always" text-align="right">${prevShedValue?if_exists?string("##0.00")}</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block keep-together="always" text-align="right">${(prevShedValue/prevTotalDays)?if_exists?string("##0.00")}</fo:block>
						</fo:table-cell>
						<#else>
						<fo:table-cell>
							<fo:block keep-together="always" text-align="right">0.00</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block keep-together="always" text-align="right">0.00</fo:block>
						</fo:table-cell>
						</#if>
						<#assign diffrence=0>
						<#assign diffrence=shedValue-prevShedValue>
						<fo:table-cell>
							<fo:block keep-together="always" text-align="right">${diffrence?if_exists?string("##0.00")}</fo:block>
						</fo:table-cell>
						<#if prevShedValue!=0>
						<#assign growth=(diffrence/prevShedValue)*(100)>
						<fo:table-cell>
							<fo:block keep-together="always" text-align="right">${growth?if_exists?string("##0.00")}</fo:block>
						</fo:table-cell>
						<#else>
						<fo:table-cell>
							<fo:block keep-together="always" text-align="right">0.00</fo:block>
						</fo:table-cell>
						</#if>
					</fo:table-row>
						</#list>
					<fo:table-row>
						<fo:table-cell>
							<fo:block font-size="5pt">---------------------------------------------------------------------------------------------------------</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<#assign mccTypeTot=currentShedTotalMap.get(mccShedWiseList.getKey())>
						<fo:table-cell>
							<fo:block text-align="left" font-size="5pt" keep-together="always">${mccShedWiseList.getKey()} TOTAL	:</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block keep-together="always" text-align="right"></fo:block>
						</fo:table-cell>
						<#assign currentValue=0>
						<#if currentShedTotalMap.get(mccShedWiseList.getKey())?has_content>
						<#assign currentValue=currentShedTotalMap.get(mccShedWiseList.getKey())>
						<fo:table-cell>
				            <fo:block text-align="right" font-size="5pt" keep-together="always">${currentValue?if_exists?string("##0.00")}</fo:block>
				        </fo:table-cell>
				        <fo:table-cell>
				            <fo:block text-align="right" font-size="5pt" keep-together="always">${(currentValue/currTotalDays)?if_exists?string("##0.00")}</fo:block>
				        </fo:table-cell>
						<#else>
						<fo:table-cell>
				            <fo:block text-align="right" font-size="5pt" keep-together="always">0.00</fo:block>
				        </fo:table-cell>
				        <fo:table-cell>
				            <fo:block text-align="right" font-size="5pt" keep-together="always">0.00</fo:block>
				        </fo:table-cell>
						</#if>
						<#assign prevValue=0>
						<#if prevShedTotalMap.get(mccShedWiseList.getKey())?has_content>
						<#assign prevValue=prevShedTotalMap.get(mccShedWiseList.getKey())>
						<fo:table-cell>
				            <fo:block text-align="right" font-size="5pt" keep-together="always">${prevValue?if_exists?string("##0.00")}</fo:block>
				        </fo:table-cell>
				        <fo:table-cell>
				            <fo:block text-align="right" font-size="5pt" keep-together="always">${(prevValue/prevTotalDays)?if_exists?string("##0.00")}</fo:block>
				        </fo:table-cell>
						<#else>
						<fo:table-cell>
				            <fo:block text-align="right" font-size="5pt" keep-together="always">0.00</fo:block>
				        </fo:table-cell>
				        <fo:table-cell>
				            <fo:block text-align="right" font-size="5pt" keep-together="always">0.00</fo:block>
				        </fo:table-cell>
						</#if>
						<#assign diffTotal=currentValue-prevValue>
						<fo:table-cell>
							<fo:block keep-together="always" text-align="right">${diffTotal?if_exists?string("##0.00")}</fo:block>
						</fo:table-cell>
						<#if prevValue!=0>
						<#assign growthTotal=(diffTotal/prevValue)*(100)>
						<fo:table-cell>
							<fo:block keep-together="always" text-align="right">${growthTotal?if_exists?string("##0.00")}</fo:block>
						</fo:table-cell>
						<#else>
						<fo:table-cell>
							<fo:block keep-together="always" text-align="right">0.00</fo:block>
						</fo:table-cell>
						</#if>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell>
							<fo:block font-size="5pt">---------------------------------------------------------------------------------------------------------</fo:block>
						</fo:table-cell>
					</fo:table-row>
					</#list>
				</fo:table-body>
			</fo:table>
		</fo:block>
		<fo:block  font-size="5pt">
			<fo:table>
				<fo:table-column column-width="20pt"/>
				<fo:table-column column-width="20pt"/>
				<fo:table-column column-width="83pt"/>
				<fo:table-column column-width="25pt"/>
				<fo:table-column column-width="40pt"/>
				<fo:table-column column-width="23pt"/>
				<fo:table-column column-width="45pt"/>
				<fo:table-column column-width="45pt"/>
				<fo:table-body>
	        	<fo:table-row>
                    <fo:table-cell>
	            		<fo:block text-align="left" font-size="5pt" keep-together="always">GRAND TOTALS	:</fo:block>
	            	</fo:table-cell>
	            	<fo:table-cell>
		            		<fo:block text-align="left" font-size="5pt" keep-together="always"></fo:block>
		            </fo:table-cell>
		            <#assign currGrValue=0>
					<#assign currGrValue=currentGrandTotalMap.get("currentGrandTotal")>
					<fo:table-cell>
			            <fo:block text-align="right" font-size="5pt" keep-together="always">${currGrValue?if_exists?string("##0.00")}</fo:block>
			        </fo:table-cell>
			        <#assign avgPerDay=0>
			        <#assign avgPerDay=currentGrandTotalMap.get("totalAvgPerDay")>
			        <fo:table-cell>
			             <fo:block text-align="right" font-size="5pt" keep-together="always"><#if avgPerDay!=0>${avgPerDay?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
			        </fo:table-cell>
					<#assign prevGrValue=0>
					<#assign prevGrValue=prevGrandTotalMap.get("prevGrandTotal")>
					<fo:table-cell>
			            <fo:block text-align="right" font-size="5pt" keep-together="always">${prevGrValue?if_exists?string("##0.00")}</fo:block>
			        </fo:table-cell>
			        <#assign prevAvgPerDay=0>
			         <#assign prevAvgPerDay=prevGrandTotalMap.get("prevTotalAvgPerDay")>
			        <fo:table-cell>
			            <fo:block text-align="right" font-size="5pt" keep-together="always"><#if prevAvgPerDay!=0>${prevAvgPerDay?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
			        </fo:table-cell>
			        <#assign grandDiffer=currGrValue-prevGrValue>
					<fo:table-cell>
						<fo:block keep-together="always" text-align="right">${grandDiffer?if_exists?string("##0.00")}</fo:block>
					</fo:table-cell>
					<#if prevGrValue!=0>
					<#assign grTotalGrowth=(grandDiffer/prevGrValue)*(100)>
					<fo:table-cell>
						<fo:block keep-together="always" text-align="right">${grTotalGrowth?if_exists?string("##0.00")}</fo:block>
					</fo:table-cell>
					<#else>
					<fo:table-cell>
						<fo:block keep-together="always" text-align="right">0.00</fo:block>
					</fo:table-cell>
					</#if>
				</fo:table-row>
		        <fo:table-row>
					<fo:table-cell>
						<fo:block font-size="5pt">---------------------------------------------------------------------------------------------------------</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
                   <fo:table-cell>
                        <fo:block linefeed-treatment="preserve" font-size="8pt">&#xA;</fo:block>
                   </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                   <fo:table-cell>
                        <fo:block font-size="5pt" keep-together="always">Copy submitted to the Managing Director,</fo:block>
                   </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                   <fo:table-cell>
                        <fo:block font-size="5pt" keep-together="always">Copy submitted to the Executive Director,            &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;SENIOR SYSTEMS ANALYST</fo:block>
                   </fo:table-cell>
               </fo:table-row>
               <fo:table-row>
                   <fo:table-cell>
                        <fo:block font-size="5pt" keep-together="always">Copy submitted to the General Manager(PI),</fo:block>
                   </fo:table-cell>
               </fo:table-row>
               <fo:table-row>
                   <fo:table-cell>
                        	<fo:block font-size="5pt" keep-together="always">Copy submitted to the General Manager(MPF),</fo:block>
                   </fo:table-cell>
               </fo:table-row>
               <fo:table-row>
                   <fo:table-cell>
                        <fo:block font-size="5pt" keep-together="always">Copy submitted to the Chief Quality Control,</fo:block>
                   </fo:table-cell>
               </fo:table-row>
		         </fo:table-body>
				 </fo:table>
		 </fo:block>           
	</fo:flow>
</fo:page-sequence> 
</#if>
</fo:root>
</#escape>