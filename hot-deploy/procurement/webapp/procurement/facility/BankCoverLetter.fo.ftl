<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".5in" margin-top=".5in">
                <fo:region-body margin-top=".8in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "bankCoverLetter.txt")}
        <#if errorMessage?has_content>
		<fo:page-sequence master-reference="main">
		   <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
		      <fo:block font-size="14pt">
		              ${errorMessage}.
		   	  </fo:block>
		   </fo:flow>
		</fo:page-sequence>        
		<#else>
	<#if bankBranchWiseMap?has_content>
			<fo:page-sequence master-reference="main">
				<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" >
									</fo:static-content>
				<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
						
						<fo:block font-size="8pt">
							<fo:table>
								<fo:table-column column-width="30pt"/>
								<fo:table-column column-width="150pt"/>
								<fo:table-column column-width="100pt"/>
								<fo:table-column column-width="70pt"/>
								 <fo:table-header height="14px">
									<fo:table-cell>
										<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
										<fo:block font-size="7pt" text-align="left" white-space-collapse="false" keep-together="always">&#160;             BANK COVER LETTER</fo:block>
										<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : parameters.shedId}, true)>
										<fo:block font-size="7pt" text-align="left" white-space-collapse="false" keep-together="always">BANK-BRANCH-WISE MILK PAYMENT   PERIOD FROM   : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block>
										<fo:block font-size="7pt" text-align="left" white-space-collapse="false" keep-together="always">MILK SHED NAME: ${(facilityDetails.facilityName).toUpperCase()?if_exists}          </fo:block>
										<fo:block font-size="7pt" text-align="left">-----------------------------------------------------------------------------------------------------</fo:block>
										<fo:block font-size="7pt" keep-together="always" white-space-collapse="false">SNO      NAME OF THE BANK           	  RTGS AND A/C NUMBER      	    	AMOUNT</fo:block>
									</fo:table-cell>
								 </fo:table-header>
								 
								<fo:table-body>
								<#assign bankWiseDetailsList = bankBranchWiseMap.entrySet()>	
					<#assign sno = 0>
					<#assign totalAmount = 0>
					<#list bankWiseDetailsList as bankWiseDetails>
						<#assign bankTot = 0>
						<#assign bankName = bankWiseDetails.getKey()>
						<#assign branchMap = bankWiseDetails.getValue()>
								<#assign keys = branchMap.keySet()>
										<#list keys as key>
										<#if branchMap.get(key)!=0>
											<#assign sno = sno+1>
											<#assign bankTot = bankTot+branchMap.get(key)>
											<#assign totalAmount = totalAmount+branchMap.get(key)>
										</#if>
										</#list>
									<#if bankTot!=0>
									<fo:table-row>
										<fo:table-cell>
											<fo:block font-size="7pt" text-align="left">-----------------------------------------------------------------------------------------------------</fo:block>
										</fo:table-cell>
									</fo:table-row>
										<fo:table-row>
											<fo:table-cell>
												<fo:block font-size="7pt" text-align="left"></fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block font-size="7pt" text-align="left" keep-together="always">${bankName}</fo:block>
											</fo:table-cell>
											<#if ddAmountMap.nameOfTheBank == bankName >
											<fo:table-cell>
												<fo:block font-size="7pt" text-align="left" text-indent="15pt" keep-together="always">${ddAmountMap.bankAccNo}</fo:block>
											</fo:table-cell>
											<#else>
											<fo:table-cell>
												<fo:block font-size="7pt" text-align="left" text-indent="15pt" keep-together="always"></fo:block>
											</fo:table-cell>
											</#if>
											<fo:table-cell>
												<fo:block font-size="7pt" text-align="right">${bankTot?if_exists?string("#0.00")}</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</#if>		
									</#list>
								</fo:table-body>
							</fo:table>
						</fo:block>
					
					<fo:block text-align="left">-----------------------------------------------------------------------------------------------------</fo:block>
					<fo:block font-size="8pt">
							<fo:table>
								<fo:table-column column-width="30pt"/>
								<fo:table-column column-width="150pt"/>
								<fo:table-column column-width="80pt"/>
								<fo:table-column column-width="75pt"/>
								<fo:table-body>
									<fo:table-row>
										<fo:table-cell>
											<fo:block font-size="7pt" text-align="left">&#160;</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block font-size="7pt" text-align="left" keep-together="always">TOTAL AMOUNT</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block font-size="7pt" text-align="left" text-indent="15pt" keep-together="always">&#160;</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block font-size="7pt" text-align="right">${totalAmount?if_exists?string("#0.00")}</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
						<fo:block text-align="left">-----------------------------------------------------------------------------------------------------</fo:block>
						<fo:block font-size="7pt" page-break-after="always"></fo:block>  
						
						<#list bankWiseDetailsList as bankWiseDetails>
						<#assign bankTot = 0>
						<#assign bankName = bankWiseDetails.getKey()>
						<#assign branchMap = bankWiseDetails.getValue()>
						<fo:block font-size="8pt">
							<fo:table>
								<fo:table-column column-width="30pt"/>
								<fo:table-column column-width="150pt"/>
								<fo:table-column column-width="80pt"/>
								<fo:table-column column-width="75pt"/>
								 <fo:table-header height="14px">
								 <fo:table-cell>
										<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;                       THE ANDHRA PRADESH</fo:block>
										<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;              DAIRY DEVELOPMENT CO-OP FEDERATION LTD.</fo:block>
										<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;                       LALAPET : HYDERABAD</fo:block>
										
										<fo:block text-align="left" white-space-collapse="false" keep-together="always">Letter No:160/AO(CASH)/APDDCF/2013-14                             DATED:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yyyy")}</fo:block>
										<fo:block text-align="left" white-space-collapse="false" keep-together="always">To,</fo:block>
										<fo:block text-align="left" white-space-collapse="false" keep-together="always">The Chief Manager</fo:block>
										<fo:block text-align="left" white-space-collapse="false" keep-together="always">State bank of Hyderabad</fo:block>
										<fo:block text-align="left" white-space-collapse="false" keep-together="always">Lallaguda</fo:block>
										<fo:block text-align="left" white-space-collapse="false" keep-together="always">HYDERABAD</fo:block>
										<fo:block text-align="left" white-space-collapse="false" keep-together="always">Sir,</fo:block>
										
										<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;     Sub: - APDDCF Ltd - Milk Payment through on-line</fo:block>
										<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;              transfer - ${(facilityDetails.facilityName).toUpperCase()?if_exists} - F.N. MILK</fo:block>
										<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;              PAYMENT   ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")}   -   ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block>
										<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;              Regarding.</fo:block>
								</fo:table-cell>		
								</fo:table-header>
								<fo:table-body>
									<fo:table-row>
										<fo:table-cell>
											<fo:block font-size="7pt" text-align="left">S.NO</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block font-size="7pt" text-align="left" keep-together="always">DESCRIPTION</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block font-size="7pt" text-align="left" text-indent="15pt" keep-together="always">A/C NO</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block font-size="7pt" text-align="right">AMOUNT</fo:block>
										</fo:table-cell>
									</fo:table-row>
										<#assign keys = branchMap.keySet()>
										<#list keys as key>
										<#if branchMap.get(key)!=0>
											<#assign sno = sno+1>
											<#assign bankTot = bankTot+branchMap.get(key)>
										</#if>
										</#list>
									<#if bankTot!=0>
									<fo:table-row>
										<fo:table-cell>
											<fo:block font-size="7pt" text-align="left">-----------------------------------------------------------------------------------------------------</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell>
											<fo:block font-size="7pt" text-align="left"></fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block font-size="7pt" text-align="left" keep-together="always">${bankName}</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block font-size="7pt" text-align="left" text-indent="15pt" keep-together="always">&#160;</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block font-size="7pt" text-align="right">${bankTot?if_exists?string("#0.00")}</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell>
											<fo:block font-size="7pt" text-align="left">-----------------------------------------------------------------------------------------------------</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell>
											<fo:block font-size="7pt" text-align="left">&#160;</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block font-size="7pt" text-align="left" keep-together="always">TOTAL AMOUNT</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block font-size="7pt" text-align="left" text-indent="15pt" keep-together="always">&#160;</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block font-size="7pt" text-align="right">${bankTot?if_exists?string("#0.00")}</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell>
											<fo:block font-size="7pt" text-align="left">-----------------------------------------------------------------------------------------------------</fo:block>
										</fo:table-cell>
									</fo:table-row>
								   <fo:table-row>
								 <fo:table-cell>
										<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;           We request you to transfer as above through on line and confirm the transfer to </fo:block>
										<fo:block text-align="left" white-space-collapse="false" keep-together="always">respective accounts</fo:block>
										<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
										<fo:block text-align="left" white-space-collapse="false" keep-together="always">1.cheque No:                                                                         Yours faithfully ,</fo:block>
										<fo:block text-align="left" white-space-collapse="false" keep-together="always">2.Cheque dt:</fo:block>
										<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
										<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;                                                                               DEPUTY DIRECTOR</fo:block>
										<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;                                                                              ${(facilityDetails.facilityName).toUpperCase()?if_exists}</fo:block>
										<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
										<fo:block text-align="left" white-space-collapse="false" keep-together="always">Copy submitted to the Managing Director, APDDCF Ltd, Head Office </fo:block>
								</fo:table-cell>		
								</fo:table-row>
									</#if>		
								</fo:table-body>
							</fo:table>
						</fo:block>
						<fo:block font-size="7pt" page-break-after="always"></fo:block>  
						</#list>
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
	</#if>		
	</fo:root>
</#escape>