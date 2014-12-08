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
	<#if costCodeSummaryMap?has_content>
	<#assign costCodeDetailList = costCodeSummaryMap.entrySet()>
		<fo:page-sequence master-reference="main">
			<fo:static-content flow-name="xsl-region-before" font-size="8pt">
				<fo:block font-size="5pt" white-space-collapse="false" font-weight="bold">&#160;.  </fo:block>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;                  A.P.Dairy Development Co-op. Federation Limited.                                </fo:block>                      
				<fo:block font-size="5pt" white-space-collapse="false" font-weight="bold">&#160;                                                  Lalapet : 	Hyderabad. </fo:block>
				<#assign shedCode = delegator.findOne("PartyGroup", {"partyId" : ShedId}, true)>
				<fo:block font-size="5pt" white-space-collapse="false" font-weight="bold">Unit Code:  <#if shedCode?has_content>${shedCode.comments?if_exists}</#if>         STATEMENT OF COST CODE WISE TOTALS FOR ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodStart, "MM/yyyy")}</fo:block>    
				<fo:block font-family="Courier,monospace" font-size="9pt">
					<fo:table>
		  				<fo:table-column column-width="15pt"/>
						<fo:table-column column-width="15pt"/>
						<fo:table-column column-width="50pt"/>
						<fo:table-column column-width="43pt"/>
						<fo:table-column column-width="43pt"/>
						<fo:table-column column-width="45pt"/>
						<fo:table-column column-width="45pt"/>
                       	<fo:table-body>
                       		<fo:table-row>
					   			<fo:table-cell>
					   				<fo:block font-size="8pt">------------------------------------------------------------------------------------------</fo:block>
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
					   				<fo:block font-size="8pt">------------------------------------------------------------------------------------------</fo:block>
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
							<fo:table-column column-width="20pt"/>
							<fo:table-column column-width="45pt"/>
							<fo:table-column column-width="45pt"/>
							<fo:table-column column-width="45pt"/>
							<fo:table-column column-width="42pt"/>
							<fo:table-column column-width="45pt"/>
							<#assign totalEarnings =0>
							<#assign totalDeductions =0>
							<#assign totalNetAmount =0>
							<#assign totalRndNetAmt =0>
							<fo:table-body>
								<#list costCodeDetailList as costCodeDetList>
								<#assign costCode = costCodeDetList.getKey()>
									<fo:table-row>	
										<fo:table-cell>
											<fo:block font-size="5pt"  keep-together="always" text-align="left">${costCodeMap.get(costCode).get("centerCode")}</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block font-size="5pt"  keep-together="always" text-align="left">${costCodeDetList.getKey()?if_exists}</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block font-size="5pt"  keep-together="always" text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(costCodeMap.get(costCode).get("centerName"))),12)}</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block font-size="5pt"  keep-together="always" text-align="right" >${costCodeDetList.getValue().get("totEarnings")?if_exists?string('0.00')}</fo:block>
										</fo:table-cell>
										<#assign totalEarnings =totalEarnings + costCodeDetList.getValue().get("totEarnings")>
										<fo:table-cell>
											<fo:block font-size="5pt"  keep-together="always" text-align="right" >${costCodeDetList.getValue().get("totDeductions")?if_exists?string('0.00')}</fo:block>
										</fo:table-cell>
										<#assign totalDeductions =totalDeductions + costCodeDetList.getValue().get("totDeductions")>
										<fo:table-cell>
											<fo:block font-size="5pt"  keep-together="always" text-align="right" >${costCodeDetList.getValue().get("netAmount")?if_exists?string('0.00')}</fo:block>
										</fo:table-cell>
										<#assign totalNetAmount =totalNetAmount + costCodeDetList.getValue().get("netAmount")>
										<fo:table-cell>
											<fo:block font-size="5pt"  keep-together="always" text-align="right" >${costCodeDetList.getValue().get("rndNetAmt")?if_exists?string('0.00')}</fo:block>
										</fo:table-cell>
										<#assign totalRndNetAmt =totalRndNetAmt + costCodeDetList.getValue().get("rndNetAmt")>
									</fo:table-row>
									<fo:table-row>
		                            	<fo:table-cell>
		                                	<fo:block font-size="5pt">&#160;</fo:block>
		                                </fo:table-cell>
		                           </fo:table-row>
								</#list>
								 <fo:table-row>
						   			<fo:table-cell>
						   				<fo:block font-size="8pt">------------------------------------------------------------------------------------------</fo:block>
						   			</fo:table-cell>
						   		</fo:table-row>
								<fo:table-row>	
									<fo:table-cell>
										<fo:block font-size="5pt" text-align="left"></fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="5pt" text-align="left"></fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="5pt" text-align="left">Grand Total:</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="5pt" text-align="right">${totalEarnings?if_exists?string('0.00')}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="5pt" text-align="right">${totalDeductions?if_exists?string('0.00')}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="5pt" text-align="right">${totalNetAmount?if_exists?string('0.00')}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="5pt" text-align="right">${totalRndNetAmt?if_exists?string('0.00')}</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
			     	 </fo:block>
					<fo:block font-size="8pt">------------------------------------------------------------------------------------------</fo:block>
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
