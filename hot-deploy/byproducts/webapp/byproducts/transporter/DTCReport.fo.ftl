<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

<#-- do not display columns associated with values specified in the request, ie constraint values -->

<fo:layout-master-set>
    <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
            margin-top="0.5in" margin-bottom="1in" margin-left="1in" margin-right="1in">
        <fo:region-body margin-top="1.2in"/>
        <fo:region-before extent=".5in"/>
        <fo:region-after extent="1in"/>
    </fo:simple-page-master>
</fo:layout-master-set>

		<#if masterList?has_content>	
		<#list masterList as trnsptMarginReportEntry>
			<#assign trnsptMarginReportEntries = (trnsptMarginReportEntry).entrySet()>	
			<#list trnsptMarginReportEntries as trnsptMarginValues> 
			<#assign facilityId= trnsptMarginValues.getKey()>
			<#assign facility = delegator.findOne("Facility",{"facilityId" : facilityId}, false)>
			<#assign trnsptMarginEntries = (trnsptMarginValues.getValue())>
			<#assign workOrderNo="">
			<#assign workOrderNo=facilityWorkOrdrNumMap(facilityId)?if_exists>
				<fo:page-sequence master-reference="main" >
					<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
						<fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-weight="bold" font-size="10pt" keep-together="always"> KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD.</fo:block>
						<fo:block text-align="center" font-weight="bold" font-size="10pt" white-space-collapse="false" keep-together="always">UNIT : MOTHER DAIRY : G.K.V.K POST : YELAHANKA : BANGALORE - 560065</fo:block>
						 <fo:block >-----------------------------------------------------------------------------------</fo:block>
						<fo:block text-align="center" keep-together="always">DISTRIBUTION TRANSPORTATION COST</fo:block>
						<fo:block text-align="center" keep-together="always">PAYMENT SHEET</fo:block>
				    </fo:static-content>
				<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
				<fo:block>
		            <fo:table width="100%" table-layout="fixed" space-after="0.0in">
		             <fo:table-column column-width="350pt"/>
		              <fo:table-column column-width="200pt"/>
		               <fo:table-body>
		               <#assign totGrTotQty = (Static["java.math.BigDecimal"].ZERO)>
            			<#assign totGrTotRtAmt = (Static["java.math.BigDecimal"].ZERO)>  
            			<#assign totGrTotPendingDue = (Static["java.math.BigDecimal"].ZERO)>  
            			<#assign totGrTotNetPayable = (Static["java.math.BigDecimal"].ZERO)>     
		               <#list trnsptMarginEntries as trnsptMarginEntry>
		               <#assign daywiseTrnsptMarginEntries = trnsptMarginEntry.entrySet()>
						<#assign grTotPaidAmt = (Static["java.math.BigDecimal"].ZERO)>
						<#list daywiseTrnsptMarginEntries as daywiseTrnsptEntry>
                    	<#if daywiseTrnsptEntry.getKey() =="Tot">                    							
						<#assign grTotRtAmt = daywiseTrnsptEntry.getValue().get("grTotRtAmount")>
						<#assign grTotpendingDue = daywiseTrnsptEntry.getValue().get("grTotpendingDue")>
						<#assign vehicleDue = daywiseTrnsptEntry.getValue().get("grTotpendingDue")>
						<#assign netPayable = grTotRtAmt.subtract(vehicleDue)>
						<#assign totGrTotQty = totGrTotQty.add(daywiseTrnsptEntry.getValue().get("grTotQty"))>
						<#assign totGrTotRtAmt = totGrTotRtAmt.add(grTotRtAmt)>
						<#assign totGrTotPendingDue = totGrTotPendingDue.add(grTotpendingDue)>
						<#assign uomId = daywiseTrnsptEntry.getValue().get("uomId")?if_exists>
						<#assign margin = daywiseTrnsptEntry.getValue().get("margin")?if_exists>
						<#assign partyCode = daywiseTrnsptEntry.getValue().get("partyCode")?if_exists>
						<#assign distance = daywiseTrnsptEntry.getValue().get("distance")?if_exists>
						<#assign partyName = daywiseTrnsptEntry.getValue().get("partyName")?if_exists>
						<#assign accNo = daywiseTrnsptEntry.getValue().get("accNo")?if_exists>
						<#assign panId = daywiseTrnsptEntry.getValue().get("panId")?if_exists>
						<#assign closedDate = daywiseTrnsptEntry.getValue().get("closedDate")?if_exists>
						<#assign facilityCode = daywiseTrnsptEntry.getValue().get("facilityCode")?if_exists>
						
						
		                   <fo:table-row>
			                   <fo:table-cell>
			                         <fo:block  text-indent="15pt">CONTRACTOR NAME:${partyName}</fo:block>
			                          <fo:block  text-indent="15pt">CONTRACTOR CODE:${partyCode}</fo:block>
			                         <fo:block  text-indent="15pt">PERIOD:&#160;&#160;&#160;${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "dd-MMM-yyyy")}&#160;&#160;TO&#160;&#160;${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime,"dd-MMM-yyyy")}</fo:block>
			                         <fo:block  text-indent="15pt">DISTRIBUTION CHARGES GROSS(Rs): ${grTotRtAmt.toEngineeringString()?if_exists}</fo:block>
			                         <fo:block  text-indent="15pt">ADDITIONAL PAYMENT:</fo:block>
			                         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			                     </fo:table-cell>
			                   <fo:table-cell>
			                         <fo:block  text-indent="15pt">ROUTE: ${trnsptMarginValues.getKey()}</fo:block>
			                         <fo:block  text-indent="15pt">TYPE: ${uomId?if_exists}</fo:block>
			                         <fo:block  text-indent="15pt">RATE:${margin?if_exists} </fo:block>
			                         <fo:block  text-indent="15pt">DISTANCE:${distance?if_exists}</fo:block>
			                         <fo:block  text-indent="15pt">WORK ORDER NO: ${workOrderNo?if_exists}</fo:block>
			                         <fo:block  text-indent="15pt">VALID UP TO:${closedDate?if_exists} </fo:block>
			                         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			                  </fo:table-cell>
		                     </fo:table-row>
		                     </#if>	
                    		</#list>	
							</#list>
							<fo:table-row>
                    		<fo:table-cell><fo:block >------------------------------------------------------------------------------------</fo:block></fo:table-cell>
                    	    </fo:table-row>
							<fo:table-row>
			                   <fo:table-cell>
			                         <fo:block  text-indent="15pt">DEDUCTIONS</fo:block>
			                  </fo:table-cell>
			                   <fo:table-cell>
			                         <fo:block  text-align="right" text-indent="15pt">&#160;&#160;&#160;&#160;&#160;AMOUNT(Rs)</fo:block>
			                  </fo:table-cell>
		                     </fo:table-row>
		                     <fo:table-row>
                    		 <fo:table-cell><fo:block >------------------------------------------------------------------------------------</fo:block></fo:table-cell>
                    	    </fo:table-row>
					        <fo:table-row>
			                   <fo:table-cell>
			                         <fo:block  text-indent="15pt">CRATES Recovery</fo:block>
			                          <fo:block  text-indent="15pt">CANS Recovery</fo:block>
			                          <fo:block  text-indent="15pt">FINE &amp; OTHERS</fo:block>
			                  </fo:table-cell>
			                  	<#assign facRecvoryMap=facilityRecoveryInfoMap.get(trnsptMarginValues.getKey())?if_exists>
			                   <fo:table-cell>
			                         <fo:block text-align="right"  text-indent="15pt"><#if facRecvoryMap?has_content>${facRecvoryMap.get("cratesFine")?if_exists?string("#0.0")}</#if></fo:block>
			                          <fo:block text-align="right" text-indent="15pt"><#if facRecvoryMap?has_content>${facRecvoryMap.get("cansFine")?if_exists?string("#0.0")}</#if></fo:block>
			                         <fo:block  text-align="right" text-indent="15pt"><#if facRecvoryMap?has_content>${facRecvoryMap.get("othersFine")?if_exists?string("#0.0")}</#if></fo:block>
			                  </fo:table-cell>
		                     </fo:table-row>
		                     <fo:table-row>
                    		 <fo:table-cell><fo:block >------------------------------------------------------------------------------------</fo:block></fo:table-cell>
                    	    </fo:table-row>
		                      <fo:table-row>
			                   <fo:table-cell>
			                         <fo:block  text-indent="15pt">(As Per Enclosure)</fo:block>
			                         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			                  </fo:table-cell>
			                   <fo:table-cell>
			                   <#assign totalDeduction=0>
			                   <#if facRecvoryMap?has_content>
			                   <#assign totalDeduction=facRecvoryMap.get("totalFine")?if_exists>
			                   </#if>
			                   <#assign netAmount=(grTotRtAmt-totalDeduction)>
			                         <fo:block text-align="right" text-indent="15pt">TOTAL DEDUCTION:<#if facRecvoryMap?has_content>${facRecvoryMap.get("totalFine")?if_exists?string("#0.0")}</#if></fo:block>
			                         <fo:block text-align="right"  text-indent="15pt">NET PAYABLE:${netAmount?string("#0.0")}</fo:block>
			                  </fo:table-cell>
		                     </fo:table-row>
		                     </fo:table-body>
		            </fo:table>
		        </fo:block> 
		        <fo:block>
			           <fo:table width="100%" table-layout="fixed" space-after="0.0in">
			           <fo:table-column column-width="100pt"/>
			              <fo:table-body>
			              <fo:table-row>
						  <fo:table-cell>
						   <#assign amountWords = Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble(netAmount?string("#0.0")), "%rupees-and-paise", locale).toUpperCase()>
                   					<fo:block white-space-collapse="false" keep-together="always">(In Words: ${StringUtil.wrapString(amountWords?default(""))}  ONLY)</fo:block>
							</fo:table-cell>
							</fo:table-row>		
				          <fo:table-row>
							<fo:table-cell>
								<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							</fo:table-cell>
						</fo:table-row>	
						 </fo:table-body>	
						 </fo:table>
			</fo:block> 
			 <fo:block >-----------------------------------------------------------------------------------</fo:block>
				<fo:block>
				           <fo:table width="100%" table-layout="fixed" space-after="0.0in">
					           <fo:table-column column-width="100pt"/>
					              <fo:table-body>
						              <fo:table-row>
										  <fo:table-cell>
												<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold">PAYMENT ARRANGED THROUGH BANK: </fo:block>
											</fo:table-cell>
										</fo:table-row>		
							          <fo:table-row>
											<fo:table-cell>
												<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
											</fo:table-cell>
									</fo:table-row>	
									<fo:table-row>
										  <fo:table-cell>
												<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold">ACCOUNT NO:${accNo} </fo:block>
											</fo:table-cell>
									</fo:table-row>		
									<fo:table-row>
									  <fo:table-cell>
											<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold">PAN NO:${panId} </fo:block>
										</fo:table-cell>
										</fo:table-row>		
								 </fo:table-body>	
								 </fo:table>
			        </fo:block> 
			  <fo:block>
		           <fo:table width="100%" table-layout="fixed" space-after="0.0in">
		           <fo:table-column column-width="180pt"/>
		           <fo:table-column column-width="170pt"/>
		           <fo:table-column column-width="120pt"/>
		              <fo:table-body>
			              <fo:table-row>
							  <fo:table-cell>
									<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold">PREPARED BY </fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold">PRE AUDIT</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold">MANAGER(MKTG) </fo:block>
								</fo:table-cell>
							</fo:table-row>		
					 </fo:table-body>	
					 </fo:table>
				 </fo:block> 
			</fo:flow>	
		 		</fo:page-sequence>
		 		</#list>
		 	    </#list>
			<#else>
				<fo:page-sequence master-reference="main">
			    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			       		 <fo:block font-size="14pt">
			            	${uiLabelMap.OrderNoOrderFound}
			       		 </fo:block>
			    	</fo:flow>
				</fo:page-sequence>
			</#if>						
</fo:root>
</#escape>