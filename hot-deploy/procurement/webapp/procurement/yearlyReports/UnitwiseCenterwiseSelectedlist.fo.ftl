<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-top=".2in">
                <fo:region-body margin-top=".6in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "UnitwiseCenterwiseSelectedlist.txt")}
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
    			<#assign centerDetail = finalMap.entrySet()>
    			<#assign Total = 0>
    			<fo:page-sequence master-reference="main">
					<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="5pt">
			 			<fo:block font-size="5pt">VST_ASCII-015 VST_ASCII-027VST_ASCII-077</fo:block>
			 			<#assign unitDetails = delegator.findOne("Facility", {"facilityId" : parameters.unitId}, true)>
			 			<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">STATEMENT OF CENTER-WISE, PROCUREMENT BILLING ANALYSIS  </fo:block>
			 			<fo:block font-size="5pt">-------------------------------------------------------------</fo:block>
			 			<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">UNIT NAME :${unitDetails.get("facilityName")?if_exists}  </fo:block>
                		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">PERIOD FROM : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate,"dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate,"dd/MM/yyyy")}  </fo:block>
						<fo:block font-size="5pt">-------------------------------------------------------------</fo:block>
						<fo:table>
							<fo:table-column column-width="12pt"/>
						 	<fo:table-column column-width="55pt"/>
						 	<fo:table-column column-width="155pt"/>
						 	<fo:table-body>
						 		
						 		<fo:table-row>
           							<fo:table-cell>
           								<fo:block keep-together="always" text-align="right" text-indent="5pt">CODE </fo:block>
           							</fo:table-cell>
           							<fo:table-cell>
           								<fo:block keep-together="always" text-align="left" text-indent="8pt">NAME OF THE CENTRE </fo:block>
           							</fo:table-cell>
           							<fo:table-cell>
           								<fo:block keep-together="always" text-align="center" text-indent="5pt">${(parameters.fieldName).toUpperCase()} </fo:block>
           							</fo:table-cell>
           						</fo:table-row>
           					</fo:table-body>
        				</fo:table>
						<fo:block font-size="5pt">-------------------------------------------------------------</fo:block>
					</fo:static-content>
					<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace"> 
						<fo:block font-family="Courier,monospace" font-size="5pt">
						 	<fo:table>
						 		<fo:table-column column-width="12pt"/>
						 		<fo:table-column column-width="57pt"/>
						 		<fo:table-column column-width="83pt"/>
						 		<fo:table-body>
						 			<#list tempCentersList as CentersList>
							 			<#list centerDetail as centerValues>
							 				<#if CentersList.get("facilityId")==centerValues.getKey()>
								 				<#if centerValues.getValue()!=0>
									 				<fo:table-row>
			           									<fo:table-cell>
			           										<fo:block keep-together="always" text-align="right" text-indent="5pt">${CentersList.get("facilityCode")?if_exists} </fo:block>
			           									</fo:table-cell>
			           									<fo:table-cell>
			           										<fo:block keep-together="always" text-align="left" text-indent="8pt">${CentersList.get("facilityName")?if_exists} </fo:block>
			           									</fo:table-cell>
			           									<fo:table-cell>
			           										<fo:block keep-together="always" text-align="right" text-indent="5pt">${centerValues.getValue()?if_exists?string("##0.00")} </fo:block>
			           										<#assign Total = Total+centerValues.getValue()>
			           									</fo:table-cell>
			           								</fo:table-row>
		           								</#if>
	           								</#if>
	           							</#list>
           							</#list>
           							<fo:table-row>
           								<fo:table-cell>
											<fo:block font-size="6pt">-------------------------------------------------------------</fo:block>           								
										</fo:table-cell>
           							</fo:table-row>
           						</fo:table-body>
        					</fo:table>
        					<fo:table>
						 		<fo:table-column column-width="12pt"/>
						 		<fo:table-column column-width="61pt"/>
						 		<fo:table-column column-width="81pt"/>
						 		<fo:table-body>
           							<fo:table-row>
           								<fo:table-cell>
											<fo:block keep-together="always" text-align="right" text-indent="5pt"> </fo:block>          								
										</fo:table-cell>
           								<fo:table-cell>
											<fo:block keep-together="always" text-align="left" text-indent="8pt">${unitDetails.get("facilityName")?if_exists} </fo:block>          								
										</fo:table-cell>
           								<fo:table-cell>
											<fo:block keep-together="always" text-align="right" text-indent="5pt">${Total?string("##0.00")} </fo:block>          								
										</fo:table-cell>
           							</fo:table-row>
           							<fo:table-row>
           								<fo:table-cell>
											<fo:block font-size="6pt">-------------------------------------------------------------</fo:block>           								
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