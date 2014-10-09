<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".5in" margin-top=".5in" margin-bottom=".5in">
                <fo:region-body margin-top="1.1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "shedWiseTipAmountReport.txt")}
		<#if shedWiseAmountAbstractMap?has_content>
		<fo:page-sequence master-reference="main">
				<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" >
					<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
					<fo:block text-align="left" white-space-collapse="false" keep-together="always">VST_ASCII-018&#160;          ${reportHeader.description?if_exists}</fo:block>
					<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : parameters.shedId}, true)>
					<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;           MILK SHED NAME :${parameters.shedId}                </fo:block>
					<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;           PERIOD FROM: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "MMM d, yyyy")}  TO  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "MMM d, yyyy")}</fo:block>
					<fo:block font-size="8pt">------------------------------------------------------------------------------------</fo:block>
					<fo:block keep-together="always" white-space-collapse="false" font-size="8pt">  UNIT     NAME OF THE UNIT          TOTAL           TIP          TIP         TIP             </fo:block>
					<fo:block keep-together="always" white-space-collapse="false" font-size="8pt">  CODE                                TIP        GENERATION     PRODUCER    FIELD EXT         </fo:block>
					<fo:block font-size="8pt">------------------------------------------------------------------------------------</fo:block>
				</fo:static-content>
				<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
					<fo:block font-size="8pt">
							<fo:table>
								<fo:table-column column-width="25pt"/>
								<fo:table-column column-width="100pt"/>
								<fo:table-column column-width="80pt"/>
								<fo:table-column column-width="65pt"/>
								<fo:table-column column-width="60pt"/>
								<fo:table-column column-width="60pt"/>
								<fo:table-column column-width="60pt"/>
								<fo:table-body>
									<#assign centerWiseDetailsList = shedWiseAmountAbstractMap.entrySet()>
									<#list centerWiseDetailsList as centerWiseDetails>
									<#if centerWiseDetails.getValue().get("unitName") == "TOTAL">
										<fo:table-row>	
				        					<fo:table-cell >	
				                        		<fo:block font-size="8pt">------------------------------------------------------------------------------------</fo:block>
				                        	</fo:table-cell>
				                        </fo:table-row>	
					                </#if>
									 <fo:table-row>
										<#assign unitCode = centerWiseDetails.getKey()>
										<#assign unitDetails = delegator.findOne("Facility", {"facilityId" : unitCode}, true)>
										<#if centerWiseDetails.getValue().get("unitName") == "TOTAL">
										<fo:table-cell>
											<fo:block text-align="left" ></fo:block>
										</fo:table-cell>
										<#else>
										<fo:table-cell>
											<fo:block text-align="left" >${unitDetails.get("facilityCode")?if_exists}</fo:block>
										</fo:table-cell>
										</#if>
										<fo:table-cell>
											<fo:block text-align="left" text-indent="15pt" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim(centerWiseDetails.getValue().get("unitName"),20)}</fo:block>
										</fo:table-cell>
										<#assign tipAmount = centerWiseDetails.getValue().get("tipAmount")?if_exists>
										<fo:table-cell>
											<fo:block text-align="right">${tipAmount?if_exists?string("##0.00")}</fo:block>
										</fo:table-cell>
										<#assign tipGeneration = ((tipAmount*3.75)/6)>
										<fo:table-cell>
											<fo:block text-align="right">${tipGeneration?if_exists?string("##0.00")}</fo:block>
										</fo:table-cell>
										<#assign tipProducer = ((tipAmount*0.75)/6)>
										<fo:table-cell>
											<fo:block text-align="right">${tipProducer?if_exists?string("##0.00")}</fo:block>
										</fo:table-cell>
										<#assign tipFieldExt = ((tipAmount*1.50)/6)>
										<fo:table-cell>
											<fo:block text-align="right">${tipFieldExt?if_exists?string("##0.00")}</fo:block>
										</fo:table-cell>
									</fo:table-row>	
									</#list>				
								</fo:table-body>
							</fo:table>
						</fo:block>
						<fo:block font-size="8pt">------------------------------------------------------------------------------------</fo:block>
				</fo:flow>		
			</fo:page-sequence>
		<#else>
		<fo:page-sequence master-reference="main">
	    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	       		 <fo:block font-size="14pt">
	            	${uiLabelMap.OrderNoOrderFound}.
	       		 </fo:block>
	    	</fo:flow>
		</fo:page-sequence>	
		</#if>
	</fo:root>
</#escape>
				