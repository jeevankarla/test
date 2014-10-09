<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="10in" page-width="12in" margin-top=".5in">
                <fo:region-body margin-top="1.2in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "LeanFlushSeason(UnitWise).txt")}
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
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="8pt">
			 <fo:block font-size="10pt">VST_ASCII-015 VST_ASCII-027VST_ASCII-077</fo:block> 
			    <fo:block text-align="left" white-space-collapse="false" keep-together="always">.                                      MILK SHEDS WISE PROCUREMENT</fo:block>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always">MILK SHED NAME:${parameters.shedId}                                                  </fo:block>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always">LEAN PERIOD  : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(leanStart, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(leanEnd, "dd/MM/yyyy")}                         DAYS:${totalDay}</fo:block>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always">FLUSH PERIOD  : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(flushStart, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(flushEnd, "dd/MM/yyyy")}                        DAYS:${totalD} (LAKHS IN LITERS)</fo:block>
				<fo:block font-size="7pt">-------------------------|-------------------------------------|--------------------------------------|--------------|-----------|</fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">.                         |              LEAN PERIOD            |              FLUSH PERIOD            |       YEAR    |    AVG   |       </fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">UNIT  NAME OF THE         |--------|---------------|------------|--------|---------------|-------------|       TOTAL.  |    PER   |       </fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">CODE  MCC/DAIRY UNIT      | DAYS   |    TOTAL      |  AVG/DAY   |   DAYS  |     TOTAL    |   AVG/DAY   |               |    DAY   |        </fo:block>
				<fo:block font-size="7pt">-------------------------|--------|---------------|------------|--------|---------------|-------------|--------------|-----------|</fo:block> 
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace"> 
			    <fo:block>
				 	<fo:table> 
					 	<fo:table-column column-width="20pt"/> 
					 	<fo:table-column column-width="40pt"/> 
					 	<fo:table-column column-width="40pt"/> 
					 	<fo:table-column column-width="40pt"/> 
					 	<fo:table-column column-width="40pt"/> 
					 	<fo:table-column column-width="40pt"/> 
					 	<fo:table-column column-width="40pt"/>
					 	<fo:table-column column-width="40pt"/> 
					 	<fo:table-column column-width="40pt"/> 
					 	<fo:table-column column-width="40pt"/> 
			          	<fo:table-body>
							<#assign monthTotals = finalMap.entrySet()>
							<#assign leangrTotLtrs = 0>
							<#assign flushgrTotLtrs = 0>
						    <#assign leanavgTtl = 0>
						    <#assign flushAvgTtl =0>
						    <#assign TotalLF=0>
						    <#assign Totavg=0>
							<#list monthTotals as monthTot> 
								<#assign facility = delegator.findOne("Facility", {"facilityId" : monthTot.getKey()}, true)>
								<#assign monthTotalEntries = monthTot.getValue()>
								<#assign days=totalDay+totalD>
				           		<fo:table-row> 
				           			<fo:table-cell>
				           				<fo:block keep-together="always" font-size="5pt" text-align="left">${facility.facilityCode?if_exists}</fo:block>
				           			</fo:table-cell>
       								<fo:table-cell>
       									<fo:block keep-together="always" font-size="5pt" text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("facilityName")?if_exists)),15)}</fo:block>
       								</fo:table-cell>
									<#assign LeanMap= monthTotalEntries.get("LEAN")?if_exists>
								    <#assign FlushMap= monthTotalEntries.get("FLUSH")?if_exists>
									<#if LeanMap?has_content>
										<#assign leanLtrs = (LeanMap.get("TOT").get("qtyLtrs"))> 
										<#assign leanTot = (leanLtrs/100000)>
										<#assign leangrTotLtrs = leangrTotLtrs+leanTot>
										<#assign LeanDay=monthTotalEntries.get("leanDays")>
										<#assign leanAvg = (leanTot/LeanDay)>
										<#assign leanavgTtl = leanavgTtl+leanAvg>
										<fo:table-cell>          
											<fo:block font-size="5pt" text-align="right"><#if LeanDay?has_content>${LeanDay}<#else>0</#if></fo:block>     																	
										</fo:table-cell>
										<fo:table-cell> 
											<fo:block font-size="5pt" text-align="right">${(leanLtrs/100000)?if_exists?string("##0.00")}</fo:block> 
										</fo:table-cell>
										<fo:table-cell> 
											<fo:block font-size="5pt" text-align="right">${(leanAvg)?if_exists?string("##0.000")}</fo:block> 
										</fo:table-cell>
									<#else>
										<fo:table-cell> 
											<fo:block font-size="5pt" text-align="right"></fo:block> 
										</fo:table-cell>
									</#if>
									<#if FlushMap?has_content>
										<#assign flushLtrs = (FlushMap.get("TOT").get("qtyLtrs"))>
										<#assign flushTot = (flushLtrs/100000)>
										<#assign flushgrTotLtrs = flushgrTotLtrs+flushTot>
										<#assign FlushDay=monthTotalEntries.get("flushDays")>
									    <#assign flushAvg = (flushTot/FlushDay)>
									    <#assign flushAvgTtl = flushAvgTtl+flushAvg>
										<fo:table-cell>          
											<fo:block font-size="5pt" text-align="right"><#if FlushDay?has_content>${FlushDay}<#else>0</#if></fo:block>     																	
										</fo:table-cell>  
										<fo:table-cell> 
											<fo:block font-size="5pt" text-align="right"><#if flushTot?has_content>${flushTot?if_exists?string("##0.00")}<#else>0.0</#if></fo:block> 
										</fo:table-cell>
										<fo:table-cell> 
											<fo:block font-size="5pt" text-align="right">${flushAvg?if_exists?string("##0.000")}</fo:block> 
										</fo:table-cell>
									<#else>
										<fo:table-cell> 
											<fo:block font-size="5pt" text-align="right">0</fo:block> 
										</fo:table-cell>
										<fo:table-cell> 
											<fo:block font-size="5pt" text-align="right">0.00</fo:block> 
										</fo:table-cell>
										<fo:table-cell> 
											<fo:block font-size="5pt" text-align="right">0.000</fo:block> 
										</fo:table-cell>
									</#if>
									<#if FlushMap?has_content>
										<#assign total=leanTot+flushTot>
										<#assign avg = (total/days)>
										<#assign TotalLF = TotalLF+total>
										<#assign Totavg = Totavg+avg>
										<fo:table-cell> 
											<fo:block font-size="5pt" text-align="right">${(total)?if_exists?string("##0.00")}</fo:block> 
										</fo:table-cell>
										<fo:table-cell> 
											<fo:block font-size="5pt" text-align="right">${(avg)?if_exists?string("##0.000")}</fo:block> 
										</fo:table-cell>
									<#else>
										<#assign flushTot=0>
										<#assign FlushDay=0>
										<#assign total=(leanLtrs/100000)+flushTot>
										<#assign avg = (total/(LeanDay+FlushDay))>
										<#assign TotalLF = TotalLF+total>
										<#assign Totavg = Totavg+(total/days)>
										<fo:table-cell> 
											<fo:block font-size="5pt" text-align="right">${total?if_exists?string("##0.00")}</fo:block> 
										</fo:table-cell>
										<fo:table-cell> 
											<fo:block font-size="5pt" text-align="right">${(avg)?if_exists?string("##0.000")}</fo:block> 
										</fo:table-cell>
									</#if>
								</fo:table-row>
							</#list>
							<fo:table-row>
								<fo:table-cell>
									<fo:block font-size="7pt">-----------------------------------------------------------------------------------------------------------------------------------</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
									<fo:block font-size="5pt" keep-together="always" text-align="left"></fo:block>
                   				</fo:table-cell>          						             						
                   				<fo:table-cell>
                   					<fo:block font-size="5pt" keep-together="always" text-align="left"> SHED ABSTRACT : </fo:block>
                   				</fo:table-cell>
                   				<fo:table-cell>
           							<fo:block font-size="5pt" keep-together="always" text-align="left"></fo:block>
           						</fo:table-cell>
           						<fo:table-cell>
           							<fo:block font-size="5pt" text-align="right">${(leangrTotLtrs)?if_exists?string("##0.00")}</fo:block>
           						</fo:table-cell>
           						<fo:table-cell>          
								    <fo:block font-size="5pt" text-align="right">${leanavgTtl?if_exists?string("##0.000")}</fo:block>     																	
								</fo:table-cell>
								<fo:table-cell>
           							<fo:block font-size="5pt" keep-together="always" text-align="left"></fo:block>
           						</fo:table-cell>
								<fo:table-cell>          
									 <fo:block font-size="5pt" text-align="right">${flushgrTotLtrs?if_exists?string("##0.00")}</fo:block>     																	
								</fo:table-cell>
								<fo:table-cell>          
									 <fo:block font-size="5pt" text-align="right">${flushAvgTtl?if_exists?string("##0.000")}</fo:block>     																	
								</fo:table-cell>
								<fo:table-cell>          
								    <fo:block font-size="5pt" text-align="right">${TotalLF?if_exists?string("##0.00")}</fo:block>     																	
								</fo:table-cell>
								<fo:table-cell>          
								    <fo:block font-size="5pt" text-align="right">${Totavg?if_exists?string("##0.000")}</fo:block>     																	
								</fo:table-cell>
                   			</fo:table-row>
                   			<fo:table-row>
								<fo:table-cell>
									<fo:block font-size="7pt">-----------------------------------------------------------------------------------------------------------------------------------</fo:block>
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
