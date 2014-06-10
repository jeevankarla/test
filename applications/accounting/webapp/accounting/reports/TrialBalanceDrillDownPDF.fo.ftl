 <#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<fo:layout-master-set>
		<fo:simple-page-master master-name="main" page-height="8.27in"  page-width="14.29in" 
		 margin-top="0.3in" margin-bottom=".3in" 
		 margin-left=".3in" margin-right=".3in">
			<fo:region-body margin-top="2.1in" />
			<fo:region-before extent="1in" />
			<fo:region-after extent="1in" />
		</fo:simple-page-master>
	</fo:layout-master-set>
<#if trillDownTrialBalanceMap?has_content>
	<fo:page-sequence master-reference="main" force-page-count="no-force">
		<fo:static-content flow-name="xsl-region-before">
		<fo:block text-align="left" white-space-collapse="false"
									font-size="10pt" keep-together="always" >&#160;${uiLabelMap.CommonPage} <fo:page-number/></fo:block>
			<fo:block>
				<fo:table table-layout="fixed" width="100%" space-before="0.2in"
					table-border-style="solid" margin-left=".1in" margin-right=".1in">
					<fo:table-column column-width="27%" />
					<fo:table-column column-width="50%" />
					<fo:table-column column-width="23%" />
					<fo:table-body>
					<#--
						<fo:table-row>
							<fo:table-cell border-style="hidden">
								<fo:block-container position="absolute">
									<fo:block>
										<fo:external-graphic src="C:\Users\user\Downloads\msme.gif" />
									</fo:block>
								</fo:block-container>
							</fo:table-cell>

							<fo:table-cell border-style="hidden">
								<fo:block text-align="center" white-space-collapse="false"
									font-size="12pt" keep-together="always" color="blue">&#160;MSME- TOOL ROOM,HYDERABAD        </fo:block>
								<fo:block text-align="center" white-space-collapse="false"
									font-size="14pt" keep-together="always" color="red">&#160;CENTRAL INSTITUTE OF TOOL DESIGN</fo:block>
								<fo:block text-align="center" white-space-collapse="false"
									font-size="10pt" keep-together="always" color="brown">&#160;(Ministry of MSME - A Government of India Society)</fo:block>
								<fo:block text-align="center" white-space-collapse="false"
									font-size="10pt" keep-together="always" color="brown">&#160;Balanagar, Hyderabad- 500037</fo:block>
								<fo:block text-align="center" white-space-collapse="false"
									font-size="10pt" keep-together="always" color="brown">&#160;Ph.No. 040-23772749,23776178,23772747,Fax:040-23772658</fo:block>
								<fo:block text-align="center" white-space-collapse="false"
									font-size="10pt" keep-together="always" color="brown">&#160;E-mail: citdcamcad@citdindia.org, Website:<fo:inline
										text-decoration="underline" color="blue">www.citdindia.org</fo:inline>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="hidden">
								<fo:block-container position="absolute">
									<fo:block>&#160;&#160;&#160;&#160;&#160;&#160;
                                         <fo:external-graphic
											src="/images/citd_logo2.gif" />
									</fo:block>
								</fo:block-container>
							</fo:table-cell>
						</fo:table-row>-->
						<fo:table-row>
		                    <fo:table-cell border-style="hidden">
		                    	<fo:block-container position="absolute">
		                      <fo:block font-size="14pt" keep-together="always">
		                        ${screens.render("component://order/widget/ordermgr/OrderPrintScreens.xml#CompanyLogo")}
		                      </fo:block>
		                      </fo:block-container>
		                    </fo:table-cell>
		                    <fo:table-cell>
		                    <fo:block text-align="center" white-space-collapse="false"
					          font-size="14pt" font-weight="bold" keep-together="always">&#160;<fo:inline text-decoration="underline" >Trial Balance</fo:inline></fo:block>
		                    </fo:table-cell>
				       </fo:table-row>
				       <fo:table-row>
		                    <fo:table-cell>
		                      <fo:block font-size="14pt" keep-together="always" color="blue">&#160;
		                      </fo:block>
		                    </fo:table-cell>
		                    <fo:table-cell>
		                    <fo:block font-size="14pt" keep-together="always" color="blue">&#160;
		                      </fo:block>
		                    </fo:table-cell>
				       </fo:table-row>
				        <fo:table-row>
		                    <fo:table-cell>
		                      <fo:block font-size="14pt" keep-together="always" color="blue">&#160;
		                      </fo:block>
		                    </fo:table-cell>
		                    <fo:table-cell>
		                    <fo:block font-size="14pt" keep-together="always" color="blue">&#160;
		                      </fo:block>
		                    </fo:table-cell>
				       </fo:table-row>
				        <fo:table-row>
		                    <fo:table-cell>
		                      <fo:block font-size="14pt" keep-together="always" color="blue">&#160;
		                      </fo:block>
		                    </fo:table-cell>
		                    <fo:table-cell>
		                    <fo:block font-size="14pt" keep-together="always" color="blue">&#160;
		                      </fo:block>
		                    </fo:table-cell>
				       </fo:table-row>
				         <fo:table-row>
		                    <fo:table-cell>
		                      <fo:block font-size="14pt" keep-together="always" color="blue">&#160;
		                      </fo:block>
		                    </fo:table-cell>
		                    <fo:table-cell>
		                    <fo:block font-size="14pt" keep-together="always" color="blue">&#160;
		                      </fo:block>
		                    </fo:table-cell>
				       </fo:table-row>
				        <fo:table-row>
		                    <fo:table-cell number-columns-spanned="3">
		                    <fo:block text-align="left"  font-size="10pt" >&#160;    &#160;    &#160;${uiLabelMap.AccountingTimePeriod}   : ${(customTimePeriod.fromDate)!}  ${uiLabelMap.CommonTo}  ${(customTimePeriod.thruDate)!}</fo:block>
		                   </fo:table-cell>
				       </fo:table-row>
					</fo:table-body>
				</fo:table>
			</fo:block>
			<fo:block>
				<fo:table table-layout="fixed" width="120%" space-before="0.2in"
					margin-left=".3in" margin-right=".2in">
					<fo:table-column column-width="12%" />
						<fo:table-column column-width="27%" />
						<fo:table-column column-width="8%" />
						<fo:table-column column-width="8%" />
						<fo:table-column column-width="8%" />
						<fo:table-column column-width="8%" />
						<fo:table-column column-width="8%" />
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell border-style="solid">
								<fo:block text-align="center" white-space-collapse="false"
									font-size="10pt" font-weight="bold" keep-together="always">Account Code</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid">
								<fo:block text-align="center" font-family="Courier,monospace"
									font-weight="bold" font-size="12pt">Account Name</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid">
								<fo:block text-align="center" white-space-collapse="false"
									font-size="10pt" font-weight="bold" keep-together="always">Opening D</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid">
								<fo:block text-align="center" white-space-collapse="false"
									font-size="10pt" font-weight="bold" keep-together="always">Opening C</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid">
								<fo:block text-align="center" white-space-collapse="false"
									font-size="10pt" font-weight="bold" keep-together="always">Dr</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid">
								<fo:block text-align="center" white-space-collapse="false"
									font-weight="bold" font-size="10pt" keep-together="always">Cr</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid">
								<fo:block text-align="center" white-space-collapse="false"
									font-size="10pt" font-weight="bold" keep-together="always">Ending Balance</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</fo:table-body>
				</fo:table>
			</fo:block>
		</fo:static-content>
		<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			<fo:block>
				<fo:block>
					<fo:table table-layout="fixed" width="120%" space-before="0.2in"
						margin-left=".3in" margin-right=".2in">
						<fo:table-column column-width="12%" />
						<fo:table-column column-width="27%" />
						<fo:table-column column-width="8%" />
						<fo:table-column column-width="8%" />
						<fo:table-column column-width="8%" />
						<fo:table-column column-width="8%" />
						<fo:table-column column-width="8%" />
						<fo:table-body>
     					<#assign serialNo = 0>
     					<#assign trileBalanceListReport =trillDownTrialBalanceMap.entrySet()>
     					
     					<#list trileBalanceListReport as trailBalance >
     					  <fo:table-row>
								<fo:table-cell border-style="solid">
									<fo:block text-align="center" font-weight="bold"
										font-family="Courier,monospace" font-size="10pt">${trailBalance.getKey()?if_exists}</fo:block>
								</fo:table-cell>
								<fo:table-cell border-style="solid">
									<fo:block text-align="left" font-weight="bold"
										font-family="Courier,monospace" font-size="10pt"> ${parentGlNameMap.get(trailBalance.getKey())?if_exists}</fo:block>
								</fo:table-cell>
								<fo:table-cell border-style="solid">
									<fo:block text-align="right" font-weight="bold"
										font-family="Courier,monospace" font-size="10pt">${trailBalance.getValue().get("openingD")?string("#0.00")}</fo:block>
								</fo:table-cell>
								<fo:table-cell border-style="solid">
									<fo:block text-align="right" font-weight="bold"
										font-family="Courier,monospace" font-size="10pt">${trailBalance.getValue().get("openingC")?string("#0.00")}</fo:block>
								</fo:table-cell>
								<fo:table-cell border-style="solid">
									<fo:block text-align="right" font-weight="bold"
										font-family="Courier,monospace" font-size="10pt">${trailBalance.getValue().get("debit")?string("#0.00")}</fo:block>
								</fo:table-cell>
								<fo:table-cell border-style="solid">
									<fo:block text-align="right" font-weight="bold"
										font-family="Courier,monospace" font-size="10pt">${trailBalance.getValue().get("credit")?string("#0.00")}</fo:block>
								</fo:table-cell>
								<fo:table-cell border-style="solid">
									<fo:block text-align="right" font-weight="bold"
										font-family="Courier,monospace" font-size="10pt">${trailBalance.getValue().get("endingBal")?string("#0.00")}</fo:block>
								</fo:table-cell>
							</fo:table-row>
     					<#assign subTrailBalanceList =trailBalance.getValue().get("chaildGlList")>
     					
     					<#list subTrailBalanceList as subGlTrailBalance >
	     					<fo:table-row>
								<fo:table-cell border-style="solid">
									<fo:block text-align="center" font-family="Courier,monospace"
										font-size="10pt">${subGlTrailBalance.accountCode?if_exists}</fo:block>
								</fo:table-cell>
								<fo:table-cell border-style="solid">
									<fo:block text-align="left" font-family="Courier,monospace"
										font-size="10pt">${subGlTrailBalance.accountName?if_exists} </fo:block>
								</fo:table-cell>
								<fo:table-cell border-style="solid">
									<fo:block text-align="right" font-family="Courier,monospace"
										font-size="10pt">${subGlTrailBalance.openingD?string("#0.00")}</fo:block>
								</fo:table-cell>
								<fo:table-cell border-style="solid">
									<fo:block text-align="right" font-family="Courier,monospace"
										font-size="10pt">${subGlTrailBalance.openingC?string("#0.00")}</fo:block>
								</fo:table-cell>
								<fo:table-cell border-style="solid">
									<fo:block text-align="right" font-family="Courier,monospace"
										font-size="10pt">${subGlTrailBalance.totalPostedDebits?string("#0.00")}</fo:block>
								</fo:table-cell>
								<fo:table-cell border-style="solid">
									<fo:block text-align="right" font-family="Courier,monospace"
										font-size="10pt">${subGlTrailBalance.totalPostedCredits?string("#0.00")}</fo:block>
								</fo:table-cell>
								<fo:table-cell border-style="solid">
									<fo:block text-align="right" font-family="Courier,monospace"
										font-size="10pt">${((subGlTrailBalance.totalPostedDebits)-(subGlTrailBalance.totalPostedCredits))?string("#0.00")}</fo:block>
								</fo:table-cell>
							</fo:table-row>
     					</#list>
     					
     					</#list>
     					</fo:table-body>
					</fo:table>

				</fo:block>
			</fo:block>

		</fo:flow>
	</fo:page-sequence>	
<#else>	
	<fo:page-sequence master-reference="main">
		<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			<fo:block font-size="14pt">
            	No Records Found
       		 </fo:block>
		</fo:flow>
	</fo:page-sequence>
</#if>	
</fo:root>
</#escape>
