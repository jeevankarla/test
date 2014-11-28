<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-top=".3in">
                <fo:region-body margin-top=".7in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
    ${setRequestAttribute("OUTPUT_FILENAME", "CostCodeReport.txt")}
    <#if errorMessage?has_content>
		<fo:page-sequence master-reference="main">
		   <fo:flow flow-name="xsl-region-body" font-family="Courier,monospae">
		      <fo:block font-size="14pt">
		              ${errorMessage}.
		   	  </fo:block>
		   </fo:flow>
		</fo:page-sequence>        
	<#else>
	<#if finalMap?has_content>
	<#assign costCodeDetailList = finalMap.entrySet()>
		<fo:page-sequence master-reference="main">
			<fo:static-content flow-name="xsl-region-before" font-size="8pt">
				<fo:block font-size="5pt" white-space-collapse="false" font-weight="bold">&#160;.  </fo:block>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;                  A.P.Dairy Development Co-op. Federation Limited.                                </fo:block>                      
				<fo:block font-size="5pt" white-space-collapse="false" font-weight="bold">&#160;                                                  Lalapet : 	Hyderabad. </fo:block>
				<#assign shedCode = delegator.findOne("PartyGroup", {"partyId" : ShedId}, true)>
				<fo:block font-size="5pt" white-space-collapse="false" font-weight="bold">Unit Code:  <#if shedCode?has_content>${shedCode.comments?if_exists}</#if>         STATEMENT OF COST CODE WISE TOTALS FOR ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodStart, "MM-yyyy")}</fo:block>    
				<fo:block font-family="Courier,monospace" font-size="9pt">
					<fo:table>
		  				<fo:table-column column-width="15pt"/>
						<fo:table-column column-width="15pt"/>
						<fo:table-column column-width="50pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="35pt"/>
                       	<fo:table-body>
                       		<fo:table-row>
					   			<fo:table-cell>
					   				<fo:block font-size="8pt">--------------------------------------------------------------------------------------</fo:block>
					   			</fo:table-cell>
					   		</fo:table-row>
					       	<fo:table-row>
			                	<fo:table-cell>
			                    	<fo:block font-size="5pt" text-align="left">Unit</fo:block>
			                    </fo:table-cell>
			                    <fo:table-cell>
			                    	<fo:block font-size="5pt" text-align="left">Cost</fo:block>
			                    </fo:table-cell>
			                     <fo:table-cell>
			                        <fo:block font-size="5pt" text-align="left">Center Name</fo:block>
			                     </fo:table-cell> 
			                     <fo:table-cell>
			                        <fo:block font-size="5pt" text-align="right">Earnings</fo:block>
			                     </fo:table-cell>
			                    <fo:table-cell>
			                        <fo:block font-size="5pt" text-align="right">Deductions</fo:block>
			                    </fo:table-cell>
			                    <fo:table-cell>
			                        <fo:block font-size="5pt" text-align="right">Net</fo:block>
			                    </fo:table-cell>
			                    <fo:table-cell>
			                        <fo:block font-size="5pt" text-align="right">Rnd.Net</fo:block>
			                    </fo:table-cell>
			                </fo:table-row>
			                <fo:table-row>
					   			<fo:table-cell>
					   				<fo:block font-size="8pt">--------------------------------------------------------------------------------------</fo:block>
					   			</fo:table-cell>
					   		</fo:table-row>
					   	</fo:table-body>
					</fo:table> 
				</fo:block>
			</fo:static-content>
				<fo:flow flow-name="xsl-region-body" font-size="5pt" font-family="Courier,monospae"> 
					<fo:block font-family="Courier,monospae" font-size="9pt">
						<fo:table>
							<fo:table-column column-width="15pt"/>
							<fo:table-column column-width="15pt"/>
							<fo:table-column column-width="50pt"/>
							<fo:table-column column-width="35pt"/>
							<fo:table-column column-width="35pt"/>
							<fo:table-column column-width="35pt"/>
							<fo:table-column column-width="35pt"/>
							<fo:table-body>
								<#list costCodeDetailList as costCodeDetList>
									<fo:table-row>	
										<fo:table-cell>
											<fo:block font-size="5pt" text-align="left" ><#if shedCode?has_content>${shedCode.comments?if_exists}</#if></fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block font-size="5pt" text-align="left">${costCodeDetList.getKey()}</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block font-size="5pt" text-align="left"></fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block font-size="5pt" text-align="right">${costCodeDetList.getValue().get("totEarnings")?if_exists?string('0.00')}</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block font-size="5pt" text-align="right">${costCodeDetList.getValue().get("totDeductions")?if_exists?string('0.00')}</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block font-size="5pt" text-align="right">${costCodeDetList.getValue().get("netAmount")?if_exists?string('0.00')}</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block font-size="5pt" text-align="right">${costCodeDetList.getValue().get("rndNetAmt")?if_exists?string('0.00')}</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</#list>
							</fo:table-body>
						</fo:table>
			     	 </fo:block>
					 <fo:block font-size="8pt">--------------------------------------------------------------------------------------</fo:block>
					<fo:block font-size="9pt">
					 <#if grandTotalMap?has_content>
					<fo:table>
						<fo:table-column column-width="15pt"/>
						<fo:table-column column-width="15pt"/>
						<fo:table-column column-width="50pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-body> 
							<fo:table-row>
							<fo:table-cell>
									<fo:block font-size="5pt" text-align="left"></fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block font-size="5pt" text-align="left"></fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block font-size="5pt" text-align="left">GRAND TOT:</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block font-size="5pt" text-align="right">${grandTotalMap.get("grTotalEarnings")?if_exists?string('0.00')}</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block font-size="5pt" text-align="right">${grandTotalMap.get("grTotalDeductions")?if_exists?string('0.00')}</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block font-size="5pt" text-align="right">${grandTotalMap.get("grNetAmt")?if_exists?string('0.00')}</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block font-size="5pt" text-align="right">${grandTotalMap.get("grRndNetAmt")?if_exists?string('0.00')}</fo:block>
								</fo:table-cell>
							</fo:table-row>	
						</fo:table-body>
					</fo:table>
						</#if>
					</fo:block>
					<fo:block font-size="8pt">--------------------------------------------------------------------------------------</fo:block>
				</fo:flow>	
			</fo:page-sequence>
	<#else>
		<fo:page-sequence master-reference="main">
	    	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospae">
	       		 <fo:block font-size="5pt">
	            	${uiLabelMap.OrderNoOrderFound}.
	       		 </fo:block>
	    	</fo:flow>
		</fo:page-sequence>	
	</#if>
</#if>
	</fo:root>
</#escape>